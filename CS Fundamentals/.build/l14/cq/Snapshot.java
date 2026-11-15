import java.util.function.Supplier;

public class Snapshot {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("a");
        Supplier<String> s = () -> sb.toString();
        sb.append("b");
        System.out.println(s.get());
    }
}
