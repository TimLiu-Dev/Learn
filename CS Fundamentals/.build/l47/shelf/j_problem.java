// Excerpt of L45 Json.java, lines 17–18, 38–41 (tested there; not a standalone file)
    // RFC 9457 Problem Details (L32).
    public record Problem(String type, String title, int status, String detail, List<String> errors) {}

    // ...

    static Response problem(ApiError e, Map<String, String> headers) {
        Problem body = new Problem("about:blank", e.title, e.status, e.getMessage(), e.errors);
        return new Response(e.status, MAPPER.writeValueAsString(body), "application/problem+json", headers);
    }
