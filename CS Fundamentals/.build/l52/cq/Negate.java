public class Negate {
    public static void main(String[] args) {
        int x = 5;
        System.out.println((~x) + " " + (~x + 1) + " " + ((~x + 1) & 0xFF));
    }
}
