public class InitOrder {
    static class Registry {
        static { System.out.println("  [Registry initialised]"); }
        static final java.util.List<String> items = java.util.List.of("a", "b");
    }

    static class Settings {
        static { System.out.println("  [Settings initialised]"); }
        static String theme() { return "dark"; }
    }

    public static void main(String[] args) {
        System.out.println("main starts");
        System.out.println("Registry: " + Registry.items);
        System.out.println("Settings: " + Settings.theme());
        System.out.println("Registry again: " + Registry.items);
    }
}
