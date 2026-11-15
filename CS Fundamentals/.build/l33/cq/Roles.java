public class Roles {
    static boolean canDelete(String role) {
        return switch (role) {
            case "guest" -> false;
            case "member" -> false;
            default -> true;            // "everyone else must be staff"
        };
    }

    public static void main(String[] args) {
        System.out.println(canDelete("admin") + " " + canDelete("Guest") + " " + canDelete(""));
    }
}
