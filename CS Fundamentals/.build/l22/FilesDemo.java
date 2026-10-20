import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class FilesDemo {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l22");
        Path notes = dir.resolve("notes.txt");

        Files.writeString(notes, "milk\neggs\n");                // whole file at once
        Files.writeString(notes, "bread\n", StandardOpenOption.APPEND);
        System.out.println("readString: " + Files.readString(notes).replace("\n", "|"));

        String longest;
        try (Stream<String> lines = Files.lines(notes)) {       // lazy: you must close it
            longest = lines.max(Comparator.comparingInt(String::length)).orElse("");
        }
        System.out.println("lines: longest is " + longest);

        try (BufferedReader r = Files.newBufferedReader(notes)) {
            System.out.println("first line: " + r.readLine());
        }
        for (String line : Files.readAllLines(notes)) System.out.println("  readAllLines: " + line);

        Files.createDirectory(dir.resolve("sub"));
        Files.writeString(dir.resolve("sub").resolve("todo.txt"), "call mum\n");
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path p : paths.filter(Files::isRegularFile).toList())
                System.out.println("walk: " + dir.relativize(p) + " (" + Files.size(p) + " bytes)");
        }

        Path rel = Path.of("data/../report.csv");
        System.out.println("relative: " + rel + "  absolute? " + rel.isAbsolute() + "  normalized: " + rel.normalize());
        Path cwd = Path.of("").toAbsolutePath();                  // the working directory
        System.out.println("working dir: .../" + cwd.getFileName() + "  toAbsolutePath(): .../"
                + cwd.getFileName() + "/" + rel.normalize().toAbsolutePath().getFileName());

        try (Stream<Path> paths = Files.walk(dir)) {             // delete children before parents
            for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(p);
        }
    }
}
