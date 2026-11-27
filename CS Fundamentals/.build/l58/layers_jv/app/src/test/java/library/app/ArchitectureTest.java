package library.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import library.domain.LendingService;
import org.junit.jupiter.api.Test;

class ArchitectureTest {
    private static final List<String> FORBIDDEN = List.of("java.sql.", "javax.sql.", "org.sqlite.", "library.app.");

    // Every .class file of the :domain module, wherever Gradle put it: a folder or a jar.
    private static List<Path> domainClasses() throws Exception {
        Path location = Path.of(LendingService.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path root = Files.isDirectory(location) ? location : FileSystems.newFileSystem(location).getPath("/");
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(p -> p.toString().endsWith(".class")).toList();
        }
    }

    private static Set<String> refs(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            return ClassRefs.of(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void theScanReallySeesTheDomain() throws Exception {
        // A rule over zero classes always passes, so first prove the scanner finds something.
        Path service = domainClasses().stream()
                .filter(p -> p.getFileName().toString().equals("LendingService.class")).findFirst().orElseThrow();
        Set<String> refs = refs(service);
        assertTrue(refs.contains("library.domain.LoanStore"), "LendingService should reference its port, got " + refs);
    }

    @Test
    void theDomainNeverNamesJdbcSqliteOrTheApp() throws Exception {
        List<String> violations = new ArrayList<>();
        for (Path file : domainClasses()) {
            for (String ref : refs(file)) {
                if (FORBIDDEN.stream().anyMatch(ref::startsWith)) violations.add(file.getFileName() + " uses " + ref);
            }
        }
        violations.sort(null);
        assertEquals(List.of(), violations, "the dependency rule is broken");
    }
}
