public class Season {
    static String season(int month) {
        return switch (month) {
            case 12, 1, 2 -> "winter";
            case 3, 4, 5 -> "spring";
            case 6, 7, 8 -> "summer";
            case 9, 10, 11 -> "autumn";
            default -> "invalid";
        };
    }

    public static void main(String[] args) {
        for (int m = 1; m <= 12; m++) System.out.print(season(m) + " ");
        System.out.println();
        System.out.println(season(13));
        for (int i = 20; i >= 0; i -= 5) System.out.print(i + " ");
        System.out.println("liftoff");
    }
}
