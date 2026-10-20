import java.util.List;

public class BroadCatch {
    // A CSV row looks like: name,age   e.g. "Ada,36"
    static int readAge(List<String> fields) {
        try {
            return Integer.parseInt(fields.get(2).trim());   // BUG: age is at index 1, not 2
        } catch (Exception e) {                              // meant for bad numbers... catches EVERYTHING
            return -1;
        }
    }

    static int readAgeNarrow(List<String> fields) {
        try {
            return Integer.parseInt(fields.get(2).trim());   // same bug
        } catch (NumberFormatException e) {                  // only the failure we actually expect
            return -1;
        }
    }

    public static void main(String[] args) {
        List<String> row = List.of("Ada,36".split(","));
        System.out.println("broad catch says age = " + readAge(row));
        System.out.println("narrow catch says age = " + readAgeNarrow(row));
    }
}
