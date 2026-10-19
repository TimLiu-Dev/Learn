import java.util.HashMap;
import java.util.Map;

public class Config {
    static int intSetting(Map<String, String> settings, String key, int defaultValue) {
        String raw = settings.get(key);
        if (raw == null) return defaultValue;
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void main(String[] args) {
        Map<String, String> settings = new HashMap<>();
        settings.put("port", "8080");
        settings.put("timeout", "abc");
        settings.put("host", null);
        System.out.println("port = " + intSetting(settings, "port", 80));
        System.out.println("timeout = " + intSetting(settings, "timeout", 30));
        System.out.println("retries = " + intSetting(settings, "retries", 3));
        String host = settings.get("host");
        System.out.println("host = " + (host != null ? host : "localhost"));
    }
}
