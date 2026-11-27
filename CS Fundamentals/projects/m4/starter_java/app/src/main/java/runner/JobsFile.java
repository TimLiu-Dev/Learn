package runner;

import java.util.List;

/** Parses and validates a jobs file. Every problem is an {@link InputError} naming the line. */
public final class JobsFile {
    private JobsFile() {}

    public static List<Job> parse(String text) throws InputError {
        throw new UnsupportedOperationException(
            "TODO: Milestone 1: parse 'id=a duration=200 fail=2 deps=b,c' lines (see the spec on the project page)");
    }
}
