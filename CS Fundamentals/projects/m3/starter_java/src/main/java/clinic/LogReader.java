package clinic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Reads a log file into memory, one string per line, without the line endings. */
public class LogReader {
    private final File file;

    public LogReader(File file) {
        this.file = file;
    }

    public List<String> readLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            String line = "";
            int c;
            while ((c = reader.read()) != -1) {
                char ch = (char) c;
                if (ch == '\n') {
                    lines.add(line);
                    line = "";
                } else if (ch != '\r') { // files copied from Windows servers end lines with \r\n
                    line += ch;
                }
            }
            if (!line.isEmpty()) lines.add(line); // last line without a final newline
        }
        return lines;
    }
}
