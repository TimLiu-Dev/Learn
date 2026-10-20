import java.io.IOException;

public class UsePlain {
    public static void main(String[] args) {
        Greeter g = Greeter.create();                // no @JvmStatic
        System.out.println(g.greet("Ada"));          // no @JvmOverloads
        try {
            g.load("x.txt");
        } catch (IOException e) {                    // no @Throws
            System.out.println("caught");
        }
    }
}
