public class Points {
    public static void main(String[] args) {
        long total = 0;
        for (int customer = 1; customer <= 1000; customer++) {
            long earned = customer * 8000L;
            total += earned;
        }
        System.out.println("total points: " + total);
    }
}
