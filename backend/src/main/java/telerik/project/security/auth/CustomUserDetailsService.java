package telerik.project.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import telerik.project.models.User;
import telerik.project.repositories.UserRepository;
import telerik.project.utils.NormalizationUtils;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) {

        final String normalized = identifier.contains("@")
                ? NormalizationUtils.normalizeEmail(identifier)
                : identifier;

        User user = userRepository.findByUsername(normalized)
                .orElseGet(() -> userRepository.findByEmail(normalized)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found: " + identifier)
                        )
                );

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}