package telerik.project.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import telerik.project.models.Post;
import telerik.project.models.Role;
import telerik.project.models.User;
import telerik.project.repositories.UserRepository;
import telerik.project.security.auth.CustomUserDetails;
import telerik.project.services.contracts.NotificationService;
import telerik.project.services.contracts.PostService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostService postService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User actingUser;

    @BeforeEach
    void setUp() {
        actingUser = new User();
        actingUser.setId(1L);
        actingUser.setRole(Role.USER);

        setAuthenticatedPrincipal(actingUser);
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(actingUser));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void followUser_AddsRelationAndSendsNotification() {
        User target = new User();
        target.setId(2L);
        target.setUsername("target");

        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(userRepository.save(actingUser)).thenReturn(actingUser);

        userService.followUser(2L);

        assertThat(actingUser.getFollowing()).contains(target);
        verify(notificationService).send(actingUser, target, 2L, "USER", "FOLLOW");
        verify(userRepository).save(actingUser);
    }

    @Test
    void blockUser_AdminBlocksAndNotifies() {
        actingUser.setRole(Role.ADMIN);
        setAuthenticatedPrincipal(actingUser);

        User target = new User();
        target.setId(5L);
        target.setUsername("victim");

        when(userRepository.findById(5L)).thenReturn(Optional.of(target));

        userService.blockUser(5L);

        assertThat(target.isBlocked()).isTrue();
        verify(userRepository).save(target);
        verify(notificationService).send(actingUser, target, 5L, "USER", "BLOCK");
    }

    @Test
    void update_ChangesEmailAndPasswordForOwner() {
        User target = new User();
        target.setId(1L);
        target.setEmail("old@mail.com");

        User update = new User();
        update.setEmail("New@Mail.com");
        update.setPassword("plain");

        when(userRepository.findById(1L)).thenReturn(Optional.of(target));
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("hashed");

        userService.update(1L, update, true);

        assertThat(target.getEmail()).isEqualTo("new@mail.com");
        assertThat(target.getPassword()).isEqualTo("hashed");
        verify(userRepository).save(target);
    }

    @Test
    void getPostsByUser_DelegatesToPostService() {
        List<Post> posts = List.of(new Post());
        when(postService.getByAuthorId(2L)).thenReturn(posts);

        List<Post> result = userService.getPostsByUser(2L);

        assertThat(result).isEqualTo(posts);
    }

    private void setAuthenticatedPrincipal(User user) {
        CustomUserDetails principal = new CustomUserDetails(
                user.getId(),
                "user",
                "mail",
                "pw",
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
