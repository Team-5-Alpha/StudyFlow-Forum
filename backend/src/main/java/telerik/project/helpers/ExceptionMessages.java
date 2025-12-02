package telerik.project.helpers;

public final class ExceptionMessages {

    private ExceptionMessages() {}

    public static final String ONLY_ADMIN = "Only admins are allowed to perform this action.";
    public static final String USER_BLOCKED = "Blocked users cannot perform this action.";
    public static final String SELF_ACTION = "You cannot perform this action on yourself.";
    public static final String NOT_OWNER = "You cannot modify %s belonging to %s.";

    public static final String USER_ALREADY_BLOCKED = "User %s is already blocked.";
    public static final String USER_NOT_BLOCKED = "User %s is not blocked.";
    public static final String USER_ALREADY_ADMIN = "User %s is already admin.";

    public static final String ALREADY_FOLLOWING = "You already follow user %s.";
    public static final String NOT_FOLLOWING = "You do not follow user %s.";

    public static final String ALREADY_LIKED = "You have already liked this %s.";
    public static final String NOT_LIKED = "You have not liked this %s.";

    public static final String ENTITY_DELETED = "This %s has been deleted.";

    public static final String WRONG_POST_REPLY = "Reply must belong to post %s.";
    public static final String REPLY_TO_DELETED = "You cannot reply to a deleted comment.";

    public static final String NOTIFICATION_ALREADY_READ = "Notification %s is already marked as read.";
}
