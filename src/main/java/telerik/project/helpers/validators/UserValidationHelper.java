package telerik.project.helpers.validators;

import telerik.project.exceptions.EntityDuplicateException;
import telerik.project.repositories.UserRepository;

public final class UserValidationHelper {

    private UserValidationHelper() {}

    public static void validateEmailAvailable(UserRepository repository, String newEmail, String oldEmail) {
        if (newEmail == null || newEmail.isBlank()) {
            return;
        }

        if (newEmail.equals(oldEmail)) {
            return;
        }

        if (repository.existsByEmail(newEmail)) {
            throw new EntityDuplicateException("User", "email", newEmail);
        }
    }

    public static void validateUsernameNotTaken(UserRepository repository, String username) {
        if (repository.existsByUsername(username)) {
            throw new EntityDuplicateException("User", "username", username);
        }
    }

    public static void validateEmailNotTaken(UserRepository repository, String email) {
        if (repository.existsByEmail(email)) {
            throw new EntityDuplicateException("User", "email", email);
        }
    }
}
