package telerik.project.repositories.contracts;

import telerik.project.models.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User getById(Long id);

    User getByUsername(String username);

    User getByEmail(String email);

    void create(User user);

    void update(User user);

    void delete(Long id);
}
