import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@interface Tag { String value(); }

// A record component also becomes a parameter + a field + an accessor.
record User(@Tag("component") String a) {}

public class UseSite {
    static List<String> tags(Annotation[] anns) {
        return Arrays.stream(anns).map(x -> ((Tag) x).value()).toList();
    }

    public static void main(String[] args) throws Exception {
        Class<User> k = User.class;
        System.out.println("a: param=" + tags(k.getDeclaredConstructors()[0].getParameterAnnotations()[0])
            + " field=" + tags(k.getDeclaredField("a").getAnnotations())
            + " getter=" + tags(k.getDeclaredMethod("a").getAnnotations()));
    }
}
