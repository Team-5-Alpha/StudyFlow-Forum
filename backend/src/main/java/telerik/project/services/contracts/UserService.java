package telerik.project.services.contracts;

import telerik.project.models.Post;
import telerik.project.models.User;
import telerik.project.models.filters.UserFilterOptions;

import java.util.List;

public interface UserService {

    List<User> getAll(UserFilterOptions filterOptions);

    User getById(Long targetUserId);

    User getByUsername(String username);

    User getByEmail(String email);

    void create(User user);

    void update(Long targetUserId, User updatedUser, boolean isPasswordChanged);

    void delete(Long targetUserId);

    void blockUser(Long targetUserId);

    void unblockUser(Long targetUserId);

    void promoteToAdmin(Long targetUserId);

    List<Post> getPostsByUser(Long targetUserId);

    void followUser(Long targetUserId);

    void unfollowUser(Long targetUserId);

    List<User> getFollowers(Long targetUserId);

    List<User> getFollowing(Long targetUserId);

    long countFollowers(Long targetUserId);

    long countFollowing(Long targetUserId);

    long count();
}
