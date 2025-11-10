package telerik.project.repositories.contracts;

import telerik.project.models.User;

import java.util.List;

public interface UserRepository {
    User getById(Long id);

    List<User> getAll();

//  List<User> getByFilter(UserFilter filter);

    User getByUsername(String username);

    User getByEmail(String email);

    void create(User user);

    void update(User user);

    void delete(Long id);
}
