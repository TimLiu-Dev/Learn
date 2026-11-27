package budget;

import java.util.ArrayList;
import java.util.List;

final class Csv {
    private Csv() {}

    /** One CSV line split into fields, or the reason it couldn't be split. */
    static Parsed<List<String>> splitLine(String line) {
        List<String> fields = new ArrayList<>();
        int i = 0;
        while (true) {
            if (i < line.length() && line.charAt(i) == '"') {
                // Quoted field: commas are literal and "" is one quote character.
                StringBuilder field = new StringBuilder();
                i++;
                while (true) {
                    if (i >= line.length()) return Parsed.bad("unterminated quoted field");
                    char c = line.charAt(i);
                    if (c == '"') {
                        if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                            field.append('"');
                            i += 2;
                        } else {
                            i++;
                            break;
                        }
                    } else {
                        field.append(c);
                        i++;
                    }
                }
                if (i < line.length() && line.charAt(i) != ',') return Parsed.bad("text after closing quote");
                fields.add(field.toString());
            } else {
                int comma = line.indexOf(',', i);
                int end = comma < 0 ? line.length() : comma;
                fields.add(line.substring(i, end));
                i = end;
            }
            if (i >= line.length()) return Parsed.ok(fields);
            i++; // skip the comma; a trailing comma leaves one empty field
        }
    }
}
