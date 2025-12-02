package telerik.project.utils;

public final class StringUtils {

    private StringUtils() {}

    public static boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    public static String safeLower(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    public static boolean equalsIgnoreCaseSafe(String a, String b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.trim().equalsIgnoreCase(b.trim());
    }
}
