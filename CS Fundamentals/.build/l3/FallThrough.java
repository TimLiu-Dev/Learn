public class FallThrough {
    public static void main(String[] args) {
        String day = "SAT";
        switch (day) {
            case "SAT":
            case "SUN":
                System.out.println("weekend");
                // forgot break!
            case "MON":
                System.out.println("weekday");
                break;
            default:
                System.out.println("unknown");
        }
    }
}
