package telerik.project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import telerik.project.models.Role;
import telerik.project.models.User;
import telerik.project.services.contracts.UserService;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if (userService.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("Admin");
            admin.setLastName("Adminov");
            admin.setEmail("admin@example.com");
            admin.setPassword("admin12");
            admin.setRole(Role.ADMIN);

            userService.create(admin);

            User user = new User();
            user.setUsername("user");
            user.setFirstName("User");
            user.setLastName("Userski");
            user.setEmail("user@example.com");
            user.setPassword("12345678");
            user.setRole(Role.USER);

            userService.create(user);
        }
    }
}
