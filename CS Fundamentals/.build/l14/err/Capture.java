import java.util.List;

public class Capture {
    public static void main(String[] args) {
        int total = 0;
        List.of(3, 4, 5).forEach(n -> total += n);   // modify a captured local
        System.out.println(total);
    }
}
