import java.lang.annotation.*;
import java.util.Arrays;
import java.util.List;

@Retention(RetentionPolicy.CLASS) @interface Internal {}
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) @interface Range { int min(); int max(); }
@Retention(RetentionPolicy.RUNTIME) @interface Note { String value(); }   // no @Target

record Signup(@Range(min = 1, max = 120) int age,
              @Internal @Note("hashed") String password,
              String nickname) {}

public class Predict {
    static List<String> names(Annotation[] anns) {
        return Arrays.stream(anns).map(a -> a.annotationType().getSimpleName()).toList();
    }

    public static void main(String[] args) throws Exception {
        Class<Signup> k = Signup.class;
        for (var f : Arrays.stream(k.getDeclaredFields()).sorted((a, b) -> a.getName().compareTo(b.getName())).toList())
            System.out.println("field " + f.getName() + ": " + names(f.getAnnotations()));
        System.out.println("password(): " + names(k.getMethod("password").getAnnotations()));
        System.out.println("ctor params: " + Arrays.stream(k.getDeclaredConstructors()[0].getParameterAnnotations()).map(Predict::names).toList());
    }
}
