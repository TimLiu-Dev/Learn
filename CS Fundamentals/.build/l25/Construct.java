import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

class Greeter { String hi() { return "hi"; } }
record Server(String host, int port) {}

public class Construct {
    public static void main(String[] args) throws Exception {
        // 1. No-arg constructor.
        Greeter g = Greeter.class.getDeclaredConstructor().newInstance();
        System.out.println(g.hi());

        // 2. Pick a constructor by its parameter types, then pass every argument.
        Constructor<Server> ctor = Server.class.getDeclaredConstructor(String.class, int.class);
        System.out.println(ctor.newInstance("localhost", 9000));

        // 3. Records keep their component names at runtime.
        System.out.println(Arrays.stream(Server.class.getRecordComponents())
            .map(RecordComponent::getName).toList());

        // 4. Asking for a constructor that doesn't exist fails at runtime.
        try {
            Server.class.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            System.out.println("NoSuchMethodException: " + e.getMessage());
        }
    }
}
