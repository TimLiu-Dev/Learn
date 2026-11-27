package library.app

import library.domain.LendingService
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArchitectureTest {
    // Every .class file of the :domain module, wherever Gradle put it: a folder or a jar.
    private val domainClasses: List<Path> = run {
        val location = Path.of(LendingService::class.java.protectionDomain.codeSource.location.toURI())
        val root = if (Files.isDirectory(location)) location else FileSystems.newFileSystem(location).getPath("/")
        Files.walk(root).use { paths -> paths.filter { it.extension == "class" }.toList() }
    }

    @Test fun `the scan really sees the domain`() {
        // A rule over zero classes always passes, so first prove the scanner finds something.
        val refs = domainClasses.first { it.fileName.toString() == "LendingService.class" }.inputStream().use(ClassRefs::of)
        assertTrue("library.domain.LoanStore" in refs, "LendingService should reference its port, got $refs")
    }

    @Test fun `the domain never names JDBC, SQLite or the app`() {
        val forbidden = listOf("java.sql.", "javax.sql.", "org.sqlite.", "library.app.")
        val violations = domainClasses.flatMap { file ->
            file.inputStream().use(ClassRefs::of)
                .filter { ref -> forbidden.any { ref.startsWith(it) } }
                .map { ref -> "${file.fileName} uses $ref" }
        }.sorted()
        assertEquals(emptyList(), violations, "the dependency rule is broken")
    }
}
