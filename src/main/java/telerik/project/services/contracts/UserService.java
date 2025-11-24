package telerik.project.services.contracts;

import telerik.project.models.Post;
import telerik.project.models.User;
import telerik.project.models.filters.UserFilterOptions;

import java.util.List;

public interface UserService {

    List<User> getAll(UserFilterOptions filterOptions);

    User getById(Long id);

    User getByUsername(String username);

    User getByEmail(String email);

    void create(User user);

    void update(Long id, User updatedUser, User actingUser);

    void delete(Long id, User actingUser);

    void blockUser(Long id, User actingUser);

    void unblockUser(Long id, User actingUser);

    void promoteToAdmin(Long id, User actingUser);

    List<Post> getPostsByUser(Long userId);

    void followUser(Long targetUserId, User actingUser);

    void unfollowUser(Long targetUserId, User actingUser);

    List<User> getFollowers(Long userId);

    List<User> getFollowing(Long userId);

    long countFollowers(Long userId);

    long countFollowing(Long userId);
}
