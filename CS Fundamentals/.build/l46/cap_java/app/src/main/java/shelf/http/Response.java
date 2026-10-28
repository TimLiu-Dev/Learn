package shelf.http;

import java.util.Map;

public record Response(int status, String body, String type, Map<String, String> headers) {
    public Response(int status) {
        this(status, null, "application/json", Map.of());
    }
}
