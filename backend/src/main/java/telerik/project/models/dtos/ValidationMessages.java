package telerik.project.models.dtos;

public final class ValidationMessages {

    private ValidationMessages() {}

    public final static String USERNAME_LENGTH_ERROR = "Username must be between {min} and {max} symbols.";
    public final static String FIRST_NAME_LENGTH_ERROR = "First name must be between {min} and {max} symbols.";
    public final static String LAST_NAME_LENGTH_ERROR = "Last name must be between {min} and {max} symbols.";
    public final static String EMAIL_LENGTH_ERROR = "Email must be between {min} and {max} symbols.";
    public final static String PASSWORD_LENGTH_ERROR = "Password must be between {min} and {max} symbols.";

    public final static String PROFILE_PHOTO_LENGTH_ERROR = "Profile photo URL can be max {max} symbols.";

    public final static String POST_TITLE_LENGTH_ERROR = "Post title must be between {min} and {max} symbols.";
    public final static String POST_CONTENT_LENGTH_ERROR = "Post content must be between {min} and {max} symbols.";

    public final static String COMMENT_CONTENT_LENGTH_ERROR =
            "Comment content must be between {min} and {max} symbols.";

    public final static String USERNAME_NOT_NULL_ERROR = "Username is required.";
    public final static String FIRST_NAME_NOT_NULL_ERROR = "First name is required.";
    public final static String LAST_NAME_NOT_NULL_ERROR = "Last name is required.";
    public final static String EMAIL_NOT_NULL_ERROR = "Email is required.";
    public final static String PASSWORD_NOT_NULL_ERROR = "Password is required.";

    public final static String EMAIL_INVALID_ERROR = "Email is invalid.";
    public final static String PROFILE_PHOTO_URL_ERROR = "Profile photo must be url.";

    public final static String POST_TITLE_NOT_NULL_ERROR = "Post title cannot be empty.";
    public final static String POST_CONTENT_NOT_NULL_ERROR = "Post content cannot be empty.";

    public final static String COMMENT_CONTENT_NOT_NULL_ERROR = "Comment content cannot be empty.";
}
