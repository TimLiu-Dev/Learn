package search;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The inverted index: term → postings (document, term frequency), plus whatever the commands need.
 * Design it yourself; the milestones on the project page say what each command needs.
 */
public final class Index {

    /** Loads every regular *.txt file directly inside dir. */
    public static Index load(Path dir) throws IOException {
        throw new UnsupportedOperationException("TODO: Index.load (milestone 1): read the *.txt files, tokenise, count");
    }

    public int documentCount() { throw new UnsupportedOperationException("TODO: documentCount (milestone 1)"); }

    public long tokenCount() { throw new UnsupportedOperationException("TODO: tokenCount (milestone 1)"); }

    public int termCount() { throw new UnsupportedOperationException("TODO: termCount (milestone 1)"); }
}
