public class Inserted {
    static final int MAX = 3;

    static Inserted create() { return new Inserted(); }

    static String greet(String name) { return "Hi, " + name; }

    static String dayName(int d) {
        switch (d) {
            case 1: return "Mon";
            case 2: return "Tue";
            case 3: return "Wed";
            case 4: return "Thu";
            default: return "?";
        }
    }

    public static void main(String[] args) {
        System.out.println(greet("Ada") + " " + dayName(3) + " " + create().getClass().getSimpleName());
    }
}
