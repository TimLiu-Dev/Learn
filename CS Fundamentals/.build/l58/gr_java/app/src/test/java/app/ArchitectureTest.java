package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import catalogue.api.Catalogue;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import lending.domain.LendingService;
import org.junit.jupiter.api.Test;

class ArchitectureTest {
    // One compiled class of the module, mapped to every class file in that module (a folder or a jar).
    private static Map<String, Set<String>> classesOf(Class<?> anyClass) throws Exception {
        Path location = Path.of(anyClass.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path root = Files.isDirectory(location) ? location : FileSystems.newFileSystem(location).getPath("/");
        Map<String, Set<String>> classes = new TreeMap<>();
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path p : paths.filter(p -> p.toString().endsWith(".class")).toList()) {
                classes.put(root.relativize(p).toString().replace('\\', '/'), refs(p));
            }
        }
        return classes;
    }

    private static Set<String> refs(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            return ClassRefs.of(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // "lending/domain/Borrower.class uses java.time.LocalDate" for every reference that breaks a rule.
    private static List<String> violations(Map<String, Set<String>> classes, String inPath, List<String> forbidden) {
        List<String> found = new ArrayList<>();
        classes.forEach((file, refs) -> {
            if (!file.startsWith(inPath)) return;
            for (String ref : refs) {
                if (forbidden.stream().anyMatch(ref::startsWith)) found.add(file + " uses " + ref);
            }
        });
        found.sort(null);
        return found;
    }

    private final Map<String, Set<String>> lending = classesOf(LendingService.class);
    private final Map<String, Set<String>> catalogue = classesOf(Catalogue.class);

    ArchitectureTest() throws Exception {}

    @Test
    void theScanReallySeesBothModules() {
        // A rule over zero classes always passes, so first prove the scanner finds the classes and their uses.
        assertTrue(catalogue.containsKey("catalogue/internal/DefaultCatalogue.class"), "catalogue classes: " + catalogue.keySet());
        Set<String> listener = lending.get("lending/adapters/CatalogueListener.class");
        assertTrue(listener.contains("catalogue.api.CatalogueEvent$CopyAdded"), "CatalogueListener uses: " + listener);
    }

    @Test
    void lendingNeverNamesCatalogueInternals() {
        assertEquals(List.of(), violations(lending, "lending/", List.of("catalogue.internal.")));
    }

    @Test
    void theLendingDomainKnowsNothingOfTheCatalogueTheBusOrJdbc() {
        assertEquals(List.of(), violations(lending, "lending/domain/", List.of("catalogue.", "platform.", "java.sql.")));
    }

    @Test
    void theCatalogueNeverNamesLending() {
        assertEquals(List.of(), violations(catalogue, "catalogue/", List.of("lending.")));
    }
}
