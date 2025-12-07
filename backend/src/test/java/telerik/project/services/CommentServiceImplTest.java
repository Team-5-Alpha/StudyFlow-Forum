package telerik.project.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import telerik.project.exceptions.EntityNotFoundException;
import telerik.project.helpers.AuthorizationHelper;
import telerik.project.helpers.validators.ActionValidationHelper;
import telerik.project.helpers.validators.CommentValidationHelper;
import telerik.project.helpers.validators.PostValidationHelper;
import telerik.project.models.Comment;
import telerik.project.models.Post;
import telerik.project.models.Role;
import telerik.project.models.User;
import telerik.project.repositories.CommentRepository;
import telerik.project.repositories.UserRepository;
import telerik.project.security.auth.SecurityContextUtil;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.PostService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private MockedStatic<SecurityContextUtil> securityContextUtilMock;
    private MockedStatic<AuthorizationHelper> authorizationHelperMock;
    private MockedStatic<CommentValidationHelper> commentValidationHelperMock;
    private MockedStatic<PostValidationHelper> postValidationHelperMock;
    private MockedStatic<ActionValidationHelper> actionValidationHelperMock;

    @BeforeEach
    void setUp() {
        securityContextUtilMock = mockStatic(SecurityContextUtil.class);
        authorizationHelperMock = mockStatic(AuthorizationHelper.class);
        commentValidationHelperMock = mockStatic(CommentValidationHelper.class);
        postValidationHelperMock = mockStatic(PostValidationHelper.class);
        actionValidationHelperMock = mockStatic(ActionValidationHelper.class);
    }

    @AfterEach
    void tearDown() {
        securityContextUtilMock.close();
        authorizationHelperMock.close();
        commentValidationHelperMock.close();
        postValidationHelperMock.close();
        actionValidationHelperMock.close();
    }

    private User buildUser(Long id, Role role) {
        User u = new User();
        u.setId(id);
        u.setRole(role);
        u.setLikedComments(new HashSet<>());
        return u;
    }

    private Comment buildComment(Long id, boolean deleted, User author, Post post) {
        Comment c = new Comment();
        c.setId(id);
        c.setIsDeleted(deleted);
        c.setAuthor(author);
        c.setPost(post);
        c.setLikedByUsers(new HashSet<>());
        return c;
    }

    @Test
    void getById_WhenExistsAndNotDeleted_ReturnsComment() {
        User author = buildUser(1L, Role.USER);
        Post post = new Post();
        Comment c = buildComment(5L, false, author, post);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(c));

        commentValidationHelperMock.when(() -> CommentValidationHelper.validateNotDeleted(c))
                .then(inv -> null);

        Comment result = commentService.getById(5L);

        assertEquals(5L, result.getId());
        verify(commentRepository).findById(5L);
    }

    @Test
    void getById_WhenMissing_Throws() {
        when(commentRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.getById(5L));
    }

    @Test
    void create_CommentOnPost_SendsNotificationToPostAuthor() {
        User acting = buildUser(1L, Role.USER);
        Post post = new Post();
        post.setId(10L);
        User postAuthor = buildUser(2L, Role.USER);
        post.setAuthor(postAuthor);

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(acting));
        when(postService.getById(10L)).thenReturn(post);

        authorizationHelperMock.when(() -> AuthorizationHelper.validateNotBlocked(acting))
                .then(inv -> null);
        postValidationHelperMock.when(() -> PostValidationHelper.validateNotDeleted(post))
                .then(inv -> null);

        Comment newComment = new Comment();
        newComment.setContent("Test");

        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        commentService.create(newComment, 10L);

        assertEquals(post, newComment.getPost());
        assertEquals(acting, newComment.getAuthor());

        verify(notificationService).send(acting, postAuthor, 10L, "COMMENT", "CREATE");
    }

    @Test
    void create_ReplyToComment_SendsReplyNotification() {
        User acting = buildUser(1L, Role.USER);
        Post post = new Post();
        post.setId(10L);
        User postAuthor = buildUser(2L, Role.USER);
        post.setAuthor(postAuthor);

        Comment parent = new Comment();
        parent.setId(50L);
        parent.setPost(post);
        parent.setAuthor(postAuthor);
        parent.setIsDeleted(false);

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(acting));
        when(postService.getById(10L)).thenReturn(post);

        when(commentRepository.findById(50L)).thenReturn(Optional.of(parent));

        authorizationHelperMock.when(() -> AuthorizationHelper.validateNotBlocked(acting))
                .then(inv -> null);
        postValidationHelperMock.when(() -> PostValidationHelper.validateNotDeleted(post))
                .then(inv -> null);
        commentValidationHelperMock.when(() -> CommentValidationHelper.validateParentNotDeleted(parent))
                .then(inv -> null);
        commentValidationHelperMock.when(() -> CommentValidationHelper
                .validateReplySamePost(parent, 10L)).then(inv -> null);

        Comment reply = new Comment();
        Comment parentRef = new Comment();
        parentRef.setId(50L);
        reply.setParentComment(parentRef);

        commentService.create(reply, 10L);

        assertEquals(parent, reply.getParentComment());
        verify(notificationService).send(acting, postAuthor, 50L, "COMMENT", "REPLY");
    }

    @Test
    void update_ChangesContent_WhenOwnerAndNotDeleted() {
        User acting = buildUser(1L, Role.USER);
        Post post = new Post();
        post.setIsDeleted(false);
        Comment existing = buildComment(5L, false, acting, post);
        existing.setContent("old");

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId)
                .thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(acting));
        when(commentRepository.findById(5L)).thenReturn(Optional.of(existing));

        authorizationHelperMock.when(() -> AuthorizationHelper.validateNotBlocked(acting))
                .then(inv -> null);
        commentValidationHelperMock.when(() -> CommentValidationHelper.validateNotDeleted(existing))
                .then(inv -> null);
        postValidationHelperMock.when(() -> PostValidationHelper.validateNotDeleted(post))
                .then(inv -> null);
        authorizationHelperMock.when(() -> AuthorizationHelper.validateOwner(acting, existing.getAuthor()))
                .then(inv -> null);

        Comment updated = new Comment();
        updated.setContent("new content");

        commentService.update(5L, updated);

        assertEquals("new content", existing.getContent());
        verify(commentRepository).save(existing);
    }

    @Test
    void delete_SoftDeletesAndNotifiesIfAdmin() {
        User admin = buildUser(1L, Role.ADMIN);
        User author = buildUser(2L, Role.USER);
        Comment comment = buildComment(5L, false, author, new Post());

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId)
                .thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        authorizationHelperMock.when(() -> AuthorizationHelper.validateNotBlocked(admin))
                .then(inv -> null);
        commentValidationHelperMock.when(() -> CommentValidationHelper.validateNotDeleted(comment))
                .then(inv -> null);
        authorizationHelperMock.when(() -> AuthorizationHelper
                .validateOwnerOrAdmin(admin, author)).then(inv -> null);

        commentService.delete(5L);

        assertTrue(comment.getIsDeleted());
        verify(commentRepository).save(comment);
        verify(notificationService).send(admin, author, 5L, "COMMENT", "DELETED");
    }

    @Test
    void likeComment_AddsLikeAndSendsNotification() {
        User acting = buildUser(1L, Role.USER);
        User author = buildUser(2L, Role.USER);
        Comment comment = buildComment(5L, false, author, new Post());

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId)
                .thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(acting));
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        authorizationHelperMock.when(() -> AuthorizationHelper.validateNotBlocked(acting))
                .then(inv -> null);
        commentValidationHelperMock.when(() -> CommentValidationHelper.validateNotDeleted(comment))
                .then(inv -> null);
        actionValidationHelperMock.when(() -> ActionValidationHelper.validateCanLike(acting, comment))
                .then(inv -> null);

        commentService.likeComment(5L);

        assertTrue(acting.getLikedComments().contains(comment));
        assertTrue(comment.getLikedByUsers().contains(acting));

        verify(userRepository).save(acting);
        verify(notificationService).send(acting, author, 5L, "COMMENT", "LIKE");
    }

    @Test
    void unlikeComment_RemovesLike() {
        User acting = buildUser(1L, Role.USER);
        User author = buildUser(2L, Role.USER);
        Comment comment = buildComment(5L, false, author, new Post());

        acting.getLikedComments().add(comment);
        comment.getLikedByUsers().add(acting);

        securityContextUtilMock.when(SecurityContextUtil::getCurrentUserId)
                .thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(acting));
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        authorizationHelperMock.when(() -> AuthorizationHelper.validateNotBlocked(acting))
                .then(inv -> null);
        commentValidationHelperMock.when(() -> CommentValidationHelper.validateNotDeleted(comment))
                .then(inv -> null);
        actionValidationHelperMock.when(() -> ActionValidationHelper.validateCanUnlike(acting, comment))
                .then(inv -> null);

        commentService.unlikeComment(5L);

        assertFalse(acting.getLikedComments().contains(comment));
        assertFalse(comment.getLikedByUsers().contains(acting));

        verify(userRepository).save(acting);
    }

    @Test
    void getReplies_FiltersDeleted() {
        Comment c1 = new Comment();
        c1.setIsDeleted(false);
        Comment c2 = new Comment();
        c2.setIsDeleted(true);

        when(commentRepository.findByParentCommentId(10L))
                .thenReturn(List.of(c1, c2));

        List<Comment> result = commentService.getReplies(10L);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isDeleted());
    }

    @Test
    void countByPostId_DelegatesToRepository() {
        when(commentRepository.countByPostId(99L)).thenReturn(7L);

        long result = commentService.countByPostId(99L);

        assertEquals(7L, result);
    }
}