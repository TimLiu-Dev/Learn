import java.lang.annotation.*;

public class Spread {
    @Retention(RetentionPolicy.RUNTIME)
    @interface Tag {}

    record User(@Tag String email) {}

    public static void main(String[] args) throws Exception {
        boolean onField = User.class.getDeclaredField("email").isAnnotationPresent(Tag.class);
        boolean onAccessor = User.class.getMethod("email").isAnnotationPresent(Tag.class);
        System.out.println(onField + " " + onAccessor);
    }
}
