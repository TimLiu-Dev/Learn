public class Types {
    public static void main(String[] args) {
        int count = 42;
        long big = 3_000_000_000L;
        double price = 9.99;
        float ratio = 0.5f;
        char initial = 'K';
        boolean done = false;
        int mask = 0xFF;
        int flags = 0b1010;
        System.out.println(count + " " + big + " " + price + " " + ratio + " "
                + initial + " " + done + " " + mask + " " + flags);

        final int limit = 100;      // final: can't be reassigned
        var score = 10;             // var: type inferred (int), still reassignable
        score += 5;
        System.out.println(score + " " + limit);

        System.out.println(010);    // a leading 0 means OCTAL in Java
    }
}
