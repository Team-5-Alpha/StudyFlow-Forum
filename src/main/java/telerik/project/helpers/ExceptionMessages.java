package telerik.project.helpers;

public final class ExceptionMessages {

    private ExceptionMessages() {}

    public static final String ADMIN_ONLY = "Only admins can perform this action.";
    public static final String USER_BLOCKED = "Blocked users cannot perform this action.";
    public static final String CANNOT_SELF_ACTION = "You cannot perform this action on yourself.";

    public static final String CANNOT_MODIFY_RESOURCE = "You are not allowed to modify this resource.";
    public static final String CANNOT_DELETE_RESOURCE = "You are not allowed to delete this resource.";

    public static final String CANNOT_BLOCK_UNBLOCK_OTHER_USER = "You are not allowed to block or unblock this user.";
    public static final String CANNOT_BLOCK_UNBLOCK_SELF = "You are not allowed to block or unblock yourself.";

    public static final String POST_DELETED = "This post has been deleted.";
    public static final String COMMENT_DELETED = "This comment has been deleted.";
    public static final String REPLY_WRONG_POST = "Reply must belong to the same post.";
    public static final String REPLY_TO_DELETED = "Cannot reply to a deleted comment.";
    public static final String ONLY_COMMENT_AUTHOR_CAN_UPDATE = "Only the comment author can update this comment.";

    public static final String NOTIFICATION_NOT_ALLOWED = "You cannot modify someone else's notifications.";
}
