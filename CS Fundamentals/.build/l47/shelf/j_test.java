// Excerpt of L45 ShelfApiTest.java, lines 19–43 (tested there; not a standalone file)
// Integration tests: the whole app on a real port, a real SQLite file, real HTTP.
class ShelfApiTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC);
    private final HttpClient client = HttpClient.newHttpClient();
    private Path dbFile;
    private Shelf shelf;

    @BeforeEach
    void start() throws Exception {
        dbFile = Files.createTempFile("shelf-api", ".db");
        shelf = new Shelf(dbFile, 0, clock).start();               // port 0: the OS picks a free port
    }

    @AfterEach
    void stop() throws Exception {
        shelf.close();
        Files.deleteIfExists(dbFile);
    }

    private HttpResponse<String> call(String method, String path, String body) throws Exception {
        var publisher = body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body);
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + shelf.port() + path))
            .method(method, publisher).header("Content-Type", "application/json").build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
