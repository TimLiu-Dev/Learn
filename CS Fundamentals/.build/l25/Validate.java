import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
@interface Range { int min(); int max(); }

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
@interface NotBlank {}

record Signup(@Range(min = 13, max = 120) int age,
              @NotBlank String name,
              @Range(min = 0, max = 10) int referrals) {}

public class Validate {
    // Reads the annotations at runtime: no if-statements about Signup anywhere.
    static List<String> validate(Object obj) throws IllegalAccessException {
        List<String> problems = new ArrayList<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        Arrays.sort(fields, Comparator.comparing(Field::getName));
        for (Field f : fields) {
            f.setAccessible(true);
            Range r = f.getAnnotation(Range.class);
            if (r != null) {
                int v = f.getInt(obj);
                if (v < r.min() || v > r.max()) problems.add(f.getName() + "=" + v + " is not in " + r.min() + ".." + r.max());
            }
            if (f.isAnnotationPresent(NotBlank.class) && ((String) f.get(obj)).isBlank()) {
                problems.add(f.getName() + " must not be blank");
            }
        }
        return problems;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(validate(new Signup(30, "Ada", 2)));
        System.out.println(validate(new Signup(9, " ", 11)));
    }
}
