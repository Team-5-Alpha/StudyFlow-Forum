package telerik.project.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import telerik.project.security.auth.AuthenticationService;
import telerik.project.security.auth.CustomUserDetailsService;
import telerik.project.security.jwt.JwtService;
import telerik.project.services.contracts.*;
import telerik.project.helpers.mappers.*;

@TestConfiguration
public class TestBeans {

    @Bean @Primary
    AuthenticationService authenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }

    @Bean @Primary
    JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean @Primary
    CustomUserDetailsService customUserDetailsService() {
        return Mockito.mock(CustomUserDetailsService.class);
    }

    @Bean @Primary
    UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean @Primary
    PostService postService() {
        return Mockito.mock(PostService.class);
    }

    @Bean @Primary
    CommentService commentService() {
        return Mockito.mock(CommentService.class);
    }

    @Bean @Primary
    TagService tagService() {
        return Mockito.mock(TagService.class);
    }

    @Bean @Primary
    NotificationService notificationService() {
        return Mockito.mock(NotificationService.class);
    }

    @Bean @Primary
    UserMapper userMapper() {
        return Mockito.mock(UserMapper.class);
    }

    @Bean @Primary
    PostMapper postMapper() {
        return Mockito.mock(PostMapper.class);
    }

    @Bean @Primary
    CommentMapper commentMapper() {
        return Mockito.mock(CommentMapper.class);
    }

    @Bean @Primary
    TagMapper tagMapper() {
        return Mockito.mock(TagMapper.class);
    }

    @Bean @Primary
    NotificationMapper notificationMapper() {
        return Mockito.mock(NotificationMapper.class);
    }
}
