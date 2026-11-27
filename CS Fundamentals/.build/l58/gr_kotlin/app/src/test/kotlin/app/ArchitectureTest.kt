package app

import catalogue.api.Catalogue
import lending.domain.LendingService
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArchitectureTest {
    // One compiled class of the module, mapped to every class file in that module (a folder or a jar).
    private fun classesOf(anyClass: Class<*>): Map<String, Set<String>> {
        val location = Path.of(anyClass.protectionDomain.codeSource.location.toURI())
        val root = if (Files.isDirectory(location)) location else FileSystems.newFileSystem(location).getPath("/")
        return Files.walk(root).use { paths ->
            paths.filter { it.extension == "class" }.toList()
                .associate { root.relativize(it).invariantSeparatorsPathString to it.inputStream().use(ClassRefs::of) }
        }
    }

    private val lending = classesOf(LendingService::class.java)
    private val catalogue = classesOf(Catalogue::class.java)

    // "lending/domain/Borrower.class uses java.time.LocalDate" for every reference that breaks a rule.
    private fun violations(classes: Map<String, Set<String>>, inPath: String, forbidden: List<String>) =
        classes.filterKeys { it.startsWith(inPath) }
            .flatMap { (file, refs) -> refs.filter { r -> forbidden.any { r.startsWith(it) } }.map { "$file uses $it" } }
            .sorted()

    @Test fun `the scan really sees both modules`() {
        // A rule over zero classes always passes, so first prove the scanner finds the classes and their uses.
        assertTrue("catalogue/internal/DefaultCatalogue.class" in catalogue, "catalogue classes: ${catalogue.keys}")
        val listener = lending.getValue("lending/adapters/CatalogueListener.class")
        assertTrue("catalogue.api.CatalogueEvent\$CopyAdded" in listener, "CatalogueListener uses: $listener")
    }

    @Test fun `lending never names catalogue internals`() {
        assertEquals(emptyList(), violations(lending, "lending/", listOf("catalogue.internal.")))
    }

    @Test fun `the lending domain knows nothing of the catalogue, the bus or JDBC`() {
        assertEquals(emptyList(), violations(lending, "lending/domain/", listOf("catalogue.", "platform.", "java.sql.")))
    }

    @Test fun `the catalogue never names lending`() {
        assertEquals(emptyList(), violations(catalogue, "catalogue/", listOf("lending.")))
    }
}
