import tools.jackson.databind.json.JsonMapper;

public class BigNumbers {
    record AsLong(long id) {}
    record AsDouble(double id) {}     // what a JavaScript client effectively does
    record AsString(String id) {}

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        String json = "{\"id\":9007199254740993}";   // 2^53 + 1

        System.out.println("Long:   " + mapper.readValue(json, AsLong.class).id());
        AsDouble d = mapper.readValue(json, AsDouble.class);
        System.out.println("Double: " + (long) d.id() + "  re-encoded: " + mapper.writeValueAsString(d));
        System.out.println("String: " + mapper.readValue("{\"id\":\"9007199254740993\"}", AsString.class).id());

        System.out.println("0.1 + 0.2 = " + (0.1 + 0.2));  // why money is never a JSON float
        System.out.println("10 + 20 cents = " + (10L + 20L));
    }
}
