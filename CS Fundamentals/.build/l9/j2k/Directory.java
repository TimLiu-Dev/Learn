import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class Directory {
    // Annotated: Kotlin sees String? and String
    public static @Nullable String findEmail(@NonNull String name) {
        return name.equals("ada") ? "ada@math.org" : null;
    }

    // Not annotated: Kotlin sees the platform type String!
    public static String findPhone(String name) {
        return null;
    }
}
