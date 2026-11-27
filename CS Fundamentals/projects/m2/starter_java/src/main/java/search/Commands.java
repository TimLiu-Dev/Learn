package search;

/** Runs one command line against an index and returns its output, without the trailing blank line. */
public final class Commands {
    private final Index index;

    public Commands(Index index) { this.index = index; }

    public String run(String line) {
        String[] parts = line.strip().split("\\s+", 2);
        String word = parts[0];
        String rest = parts.length > 1 ? parts[1] : "";
        return switch (word) {
            case "stats", "doc" -> todo(word, 1);
            case "tokens" -> todo(word, 2);
            case "q", "or" -> todo(word, 3);
            case "prefix", "top" -> todo(word, 4);
            case "bench" -> todo(word, 5);
            default -> "error: unknown command '" + word + "'";
        };
    }

    private static String todo(String command, int milestone) {
        throw new UnsupportedOperationException("TODO: the '" + command + "' command (milestone " + milestone + ")");
    }
}
