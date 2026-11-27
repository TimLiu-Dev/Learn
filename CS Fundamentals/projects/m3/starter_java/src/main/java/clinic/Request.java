package clinic;

/** A parsed request plus where it was in the file (1-based, counting every line). */
public record Request(int lineNo, LogLine line, String endpoint) {
    public Request(int lineNo, LogLine line) {
        this(lineNo, line, LogParser.endpointOf(line.method(), line.path()));
    }
}
