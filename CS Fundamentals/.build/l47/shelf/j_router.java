// Excerpt of L45 Router.java, lines 70–94 (tested there; not a standalone file)
    // HttpServer calls this on a virtual thread for every request.
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            Response response;
            try {
                response = switch (match(exchange.getRequestMethod(), path)) {
                    case Match.Found(Handler handler, Map<String, String> params) ->
                        handler.handle(new Request(exchange, params));
                    case Match.WrongMethod(List<String> allowed) -> Json.problem(   // RFC 9110: a 405 MUST send Allow
                        new ApiError(405, "Method Not Allowed", exchange.getRequestMethod() + " is not supported on " + path),
                        Map.of("Allow", String.join(", ", allowed)));
                    case Match.NoRoute() -> Json.problem(ApiError.notFound(path), Map.of());
                };
            } catch (ApiError e) {
                response = Json.problem(e, Map.of());
            } catch (JacksonException e) {                       // malformed JSON or wrong types
                response = Json.problem(new ApiError(400, "Bad Request", e.getOriginalMessage()), Map.of());
            } catch (RuntimeException e) {                       // log the details, never send them (L33)
                System.err.println("ERROR " + exchange.getRequestMethod() + " " + path + ": " + e);
                response = Json.problem(new ApiError(500, "Internal Server Error", "Something went wrong"), Map.of());
            }
            send(exchange, response);
        }
    }
