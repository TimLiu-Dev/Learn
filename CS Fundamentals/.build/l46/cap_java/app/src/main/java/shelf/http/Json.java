package shelf.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

public final class Json {
    // Null fields are left out of responses. Jackson 3 ignores unknown keys by default (L32),
    // so we switch that on to match the strict Kotlin side.
    static final JsonMapper MAPPER = JsonMapper.builder()
        .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
        .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

    // RFC 9457 Problem Details (L32).
    public record Problem(String type, String title, int status, String detail, List<String> errors) {}

    private Json() {}

    public static <T> T read(Request request, Class<T> type) {
        return MAPPER.readValue(request.body(), type);
    }

    public static Response ok(Object value) {
        return ok(value, 200, Map.of());
    }

    public static Response ok(Object value, int status, Map<String, String> headers) {
        return new Response(status, MAPPER.writeValueAsString(value), "application/json", headers);
    }

    public static Response noContent() {
        return new Response(204);
    }

    static Response problem(ApiError e, Map<String, String> headers) {
        Problem body = new Problem("about:blank", e.title, e.status, e.getMessage(), e.errors);
        return new Response(e.status, MAPPER.writeValueAsString(body), "application/problem+json", headers);
    }
}
