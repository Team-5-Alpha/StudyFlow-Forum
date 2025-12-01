package telerik.project.utils;

public final class NormalizationUtils {

    private NormalizationUtils() {}

    public static String normalizeKeyword(String input) {
        return StringUtils.safeLower(input);
    }

    public static String normalizeTagName(String tag) {
        return StringUtils.safeLower(tag);
    }

    public static String normalizeEmail(String email) {
        return StringUtils.safeLower(email);
    }

    public static String normalizeUsername(String username) {
        return StringUtils.safeLower(username);
    }
}
