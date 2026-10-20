public class Points {
    public static void main(String[] args) {
        int total = 0;
        for (int customer = 1; customer <= 1000; customer++) {
            int earned = customer * 8000;
            total += earned;
        }
        System.out.println("total points: " + total);
    }
}
