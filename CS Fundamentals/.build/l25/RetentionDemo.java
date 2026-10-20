import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

// Three annotations, three lifetimes.
@Retention(RetentionPolicy.SOURCE) @interface Draft {}    // compiler only
@interface Audited {}                                     // no @Retention: CLASS
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Timed {
    String unit() default "ms";                           // elements look like methods
    int warnAbove() default 100;
}

class Repo {
    @Draft @Audited @Timed(warnAbove = 50)
    void save() {}

    @Timed
    void load() {}
}

public class RetentionDemo {
    public static void main(String[] args) throws Exception {
        for (String name : List.of("save", "load")) {
            Method m = Repo.class.getDeclaredMethod(name);
            System.out.println(name + " -> " + Arrays.toString(m.getAnnotations()));
        }
        Timed t = Repo.class.getDeclaredMethod("save").getAnnotation(Timed.class);
        System.out.println("save: unit=" + t.unit() + " warnAbove=" + t.warnAbove());
    }
}
