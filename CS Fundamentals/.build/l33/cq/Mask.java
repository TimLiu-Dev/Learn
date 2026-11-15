public class Mask {
    // Written for card numbers: hide every digit that has four more digits after it.
    static String mask(String s) {
        return s.replaceAll("\\d(?=\\d{4})", "*");
    }

    public static void main(String[] args) {
        System.out.println(mask("card 4111111111111111"));
        System.out.println(mask("order 12345678 on 2026-11-14"));
    }
}
