package search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Given: argument checks and the command loop. Your work starts in Index.load and Tokenizer.tokenize. */
public final class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: search <corpus-dir>");
            System.exit(2);
        }
        Path dir = Path.of(args[0]);
        if (!Files.isDirectory(dir)) {
            System.err.println("error: not a directory: " + args[0]);
            System.exit(2);
        }

        Index index = Index.load(dir); // TODO (milestone 1)
        System.out.printf("loaded %d documents, %d tokens, %d terms%n%n",
                index.documentCount(), index.tokenCount(), index.termCount());

        Commands commands = new Commands(index);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            String command = line.strip();
            if (command.isEmpty()) continue;
            if (command.equals("quit")) break;
            System.out.println(commands.run(command));
            System.out.println(); // every command's output ends with one empty line
        }
    }
}
