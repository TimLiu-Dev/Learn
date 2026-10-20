import java.util.Map;

public class Observe {
    static class Thermostat {
        private int target = 20;
        private int limit = 28;

        int getTarget() { return target; }
        void setTarget(int value) {
            int old = target;
            target = value;
            System.out.println("  target " + old + " -> " + value);   // "observable": after
        }

        int getLimit() { return limit; }
        void setLimit(int value) {
            if (value >= 15 && value <= 30) limit = value;           // "vetoable": check first
        }
    }

    static class Settings {
        private final Map<String, Object> map;
        Settings(Map<String, Object> map) { this.map = map; }
        String getTheme() { return (String) map.get("theme"); }      // a cast per property
        int getFontSize() { return (Integer) map.get("fontSize"); }
    }

    public static void main(String[] args) {
        Thermostat t = new Thermostat();
        t.setTarget(22);
        t.setTarget(19);
        t.setLimit(45);
        System.out.println("limit after 45: " + t.getLimit());
        t.setLimit(25);
        System.out.println("limit after 25: " + t.getLimit());

        Settings s = new Settings(Map.of("theme", "dark", "fontSize", 14));
        System.out.println("theme=" + s.getTheme() + " fontSize=" + s.getFontSize());
    }
}
