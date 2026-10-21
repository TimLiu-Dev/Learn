import java.util.ArrayList;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class Tree {
    static final String RESPONSE = """
        {"city": "Oslo", "current": {"tempC": 7.5, "wind": {"kmh": 18}},
         "alerts": [{"level": "yellow", "text": "Ice"}, {"level": "orange", "text": "Storm"}]}
        """;

    public static void main(String[] args) {
        JsonNode root = JsonMapper.builder().build().readTree(RESPONSE);   // no DTO classes at all
        System.out.println("city: " + root.path("city").asString());
        System.out.println("wind: " + root.path("current").path("wind").path("kmh").asInt());
        var levels = new ArrayList<String>();
        for (JsonNode alert : root.path("alerts")) levels.add(alert.path("level").asString());
        System.out.println("alerts: " + levels);
        System.out.println("missing: " + root.path("humidity").isMissingNode());  // absent -> MissingNode
    }
}
