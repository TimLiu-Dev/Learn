public class Bump {
    public static void main(String[] args) {
        Integer a = 127, b = 127;
        System.out.print((a == b) + " ");
        a++;
        b++;
        System.out.println((a == b) + " " + (a == 128));
    }
}
