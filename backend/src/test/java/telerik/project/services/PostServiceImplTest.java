package telerik.project.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import telerik.project.models.Post;
import telerik.project.models.Role;
import telerik.project.models.Tag;
import telerik.project.models.User;
import telerik.project.models.dtos.create.PostCreateDTO;
import telerik.project.models.dtos.update.PostUpdateDTO;
import telerik.project.models.filters.PostFilterOptions;
import telerik.project.repositories.PostRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.security.auth.CustomUserDetails;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.TagService;
import telerik.project.exceptions.InvalidOperationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User actingUser;
    private long tagIdSeq = 1L;

    @BeforeEach
    void setUp() {
        actingUser = new User();
        actingUser.setId(1L);
        actingUser.setUsername("author");
        actingUser.setEmail("author@mail.com");
        actingUser.setRole(Role.USER);
        tagIdSeq = 1L;

        setAuthenticatedPrincipal(actingUser);
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(actingUser));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAll_FiltersDeletedPosts() {
        Post visible = new Post();
        visible.setId(10L);

        Post deleted = new Post();
        deleted.setId(11L);
        deleted.setIsDeleted(true);

        Page<Post> page = new PageImpl<>(List.of(visible, deleted));
        when(postRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<Post> result = postService.getAll(new PostFilterOptions());

        assertThat(result).containsExactly(visible);
    }

    @Test
    void create_SavesPostAndNotifiesFollowers() {
        User follower = new User();
        follower.setId(2L);
        actingUser.getFollowers().add(follower);

        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("some valid title");
        dto.setContent("This is a sufficiently long post content body.");

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        Post created = postService.create(dto);

        assertThat(created.getAuthor()).isEqualTo(actingUser);
        assertThat(created.getTitle()).isEqualTo(dto.getTitle());
        verify(postRepository).save(created);
        verify(notificationService).send(actingUser, follower, 99L, "POST", "CREATE");
    }

    @Test
    void update_ChangesFieldsAndTags() {
        Post existing = new Post();
        existing.setId(5L);
        existing.setAuthor(actingUser);

        when(postRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(postRepository.save(existing)).thenReturn(existing);
        when(tagService.createIfNotExists(anyString())).thenAnswer(invocation -> {
            Tag tag = new Tag();
            tag.setId(tagIdSeq++);
            tag.setName(invocation.getArgument(0));
            return tag;
        });

        Set<String> tags = Set.of("Java", "Spring");
        PostUpdateDTO dto = new PostUpdateDTO();
        dto.setTitle("updated");
        dto.setContent("updated content");
        dto.setTags(tags);

        postService.update(5L, dto);

        assertThat(existing.getTitle()).isEqualTo("updated");
        assertThat(existing.getContent()).isEqualTo("updated content");
        assertThat(existing.getTags())
                .extracting(Tag::getName)
                .containsExactlyInAnyOrder("java", "spring");
        verify(postRepository).save(existing);
    }

    @Test
    void delete_SetsFlagWithoutAdminNotificationForOwner() {
        Post existing = new Post();
        existing.setId(3L);
        existing.setAuthor(actingUser);

        when(postRepository.findById(3L)).thenReturn(Optional.of(existing));

        postService.delete(3L);

        assertThat(existing.isDeleted()).isTrue();
        verify(postRepository).save(existing);
        verify(notificationService, never()).send(any(), any(), anyLong(), anyString(), anyString());
    }

    @Test
    void delete_AdminSendsNotificationToAuthor() {
        actingUser.setRole(Role.ADMIN);

        User targetAuthor = new User();
        targetAuthor.setId(5L);
        targetAuthor.setUsername("target");

        Post existing = new Post();
        existing.setId(7L);
        existing.setAuthor(targetAuthor);

        when(postRepository.findById(7L)).thenReturn(Optional.of(existing));

        postService.delete(7L);

        assertThat(existing.isDeleted()).isTrue();
        verify(notificationService).send(actingUser, targetAuthor, 7L, "POST", "DELETED");
    }

    @Test
    void likePost_AddsRelationAndSendsNotification() {
        User postAuthor = new User();
        postAuthor.setId(8L);
        postAuthor.setUsername("poster");

        Post post = new Post();
        post.setId(12L);
        post.setAuthor(postAuthor);

        when(postRepository.findById(12L)).thenReturn(Optional.of(post));
        when(userRepository.save(actingUser)).thenReturn(actingUser);

        postService.likePost(12L);

        assertThat(actingUser.getLikedPosts()).contains(post);
        assertThat(post.getLikedByUsers()).contains(actingUser);
        verify(userRepository).save(actingUser);
        verify(notificationService).send(actingUser, postAuthor, 12L, "POST", "LIKE");
    }

    @Test
    void unlikePost_RemovesRelation() {
        Post post = new Post();
        post.setId(15L);
        post.getLikedByUsers().add(actingUser);
        actingUser.getLikedPosts().add(post);

        when(postRepository.findById(15L)).thenReturn(Optional.of(post));
        when(userRepository.save(actingUser)).thenReturn(actingUser);

        postService.unlikePost(15L);

        assertThat(actingUser.getLikedPosts()).doesNotContain(post);
        assertThat(post.getLikedByUsers()).doesNotContain(actingUser);
        verify(userRepository).save(actingUser);
    }

    @Test
    void getById_ThrowsWhenDeleted() {
        Post post = new Post();
        post.setId(20L);
        post.setIsDeleted(true);

        when(postRepository.findById(20L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.getById(20L))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void getMostCommented_SkipsDeletedAndLimits() {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Post p = new Post();
            p.setId((long) i);
            p.setIsDeleted(i % 2 == 0);
            posts.add(p);
        }

        when(postRepository.findMostCommented()).thenReturn(posts);

        List<Post> result = postService.getMostCommented();

        assertThat(result).hasSize(6);
        assertThat(result).allMatch(p -> !p.isDeleted());
    }

    @Test
    void getByAuthorId_FiltersDeleted() {
        Post active = new Post();
        active.setId(1L);
        Post deleted = new Post();
        deleted.setId(2L);
        deleted.setIsDeleted(true);

        when(postRepository.findByAuthor_Id(actingUser.getId()))
                .thenReturn(List.of(active, deleted));

        List<Post> result = postService.getByAuthorId(actingUser.getId());

        assertThat(result).containsExactly(active);
    }

    @Test
    void getLikedPosts_FiltersDeleted() {
        Post liked = new Post();
        liked.setId(1L);
        Post deletedLiked = new Post();
        deletedLiked.setId(2L);
        deletedLiked.setIsDeleted(true);

        when(postRepository.findByLikedByUsers_Id(actingUser.getId()))
                .thenReturn(List.of(liked, deletedLiked));

        List<Post> result = postService.getLikedPosts(actingUser.getId());

        assertThat(result).containsExactly(liked);
    }

    @Test
    void countByAuthor_DelegatesToRepository() {
        when(postRepository.countByAuthor_Id(actingUser.getId())).thenReturn(4L);

        long result = postService.countByAuthor(actingUser.getId());

        assertThat(result).isEqualTo(4L);
    }

    @Test
    void count_DelegatesToRepository() {
        when(postRepository.count()).thenReturn(15L);

        long result = postService.count();

        assertThat(result).isEqualTo(15L);
    }

    private void setAuthenticatedPrincipal(User user) {
        CustomUserDetails principal = new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                "password",
                user.getRole()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                )
        );
    }
}
