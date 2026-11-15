import java.util.Arrays;

public class Split {
    public static void main(String[] args) {
        String[] a = "/books/42".split("/");
        String[] b = "/books/42/".split("/");
        System.out.println(a.length + " " + b.length + " " + Arrays.equals(a, b));
    }
}
