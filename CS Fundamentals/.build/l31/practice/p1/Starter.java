import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Practice {
    record Response(int status, Map<String, String> headers, String body) {
        Response(int status) { this(status, Map.of(), ""); }
        Response(int status, String body) { this(status, Map.of(), body); }
    }

    static class NotesApi {
        private final List<String> titles = new ArrayList<>(List.of("groceries"));   // note 1

        Response handle(String method, String path, String body) {
            throw new UnsupportedOperationException("TODO: route by path first, then by method");
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("a new note is 201 Created", 201, () -> new NotesApi().handle("POST", "/notes", "milk").status());
        check("with its Location", "/notes/2", () -> new NotesApi().handle("POST", "/notes", "milk").headers().get("Location"));
        check("a blank title is 422", 422, () -> new NotesApi().handle("POST", "/notes", "   ").status());
        check("a duplicate title is 409", 409, () -> new NotesApi().handle("POST", "/notes", "groceries").status());
        check("DELETE /notes is 405", 405, () -> new NotesApi().handle("DELETE", "/notes", "").status());
        check("Allow on /notes", "GET, POST", () -> new NotesApi().handle("DELETE", "/notes", "").headers().get("Allow"));
        check("Allow on /notes/1", "GET", () -> new NotesApi().handle("PUT", "/notes/1", "eggs").headers().get("Allow"));
        for (String path : List.of("/notes/7", "/notes/0", "/notes/abc", "/users")) {
            check("GET " + path + " is 404", 404, () -> new NotesApi().handle("GET", path, "").status());
        }
        NotesApi api = new NotesApi();
        api.handle("POST", "/notes", "milk");
        check("GET lists the notes", "groceries\nmilk", () -> api.handle("GET", "/notes", "").body());
        check("GET fetches one", "milk", () -> api.handle("GET", "/notes/2", "").body());
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
