import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NextVersionTest {
    @Test fun `a fix bumps the patch`() {
        assertEquals("1.4.3", nextVersion("1.4.2", listOf("fix: trim spaces around the name", "docs: fix a typo")))
    }

    @Test fun `a feature bumps the minor and resets the patch`() {
        assertEquals("1.5.0", nextVersion("1.4.2", listOf("fix: trim spaces", "feat(greet): add farewell")))
    }

    @Test fun `a bang or a BREAKING CHANGE footer bumps the major`() {
        assertEquals("with feat(api)!:", "2.0.0", nextVersion("1.4.2", listOf("feat(api)!: drop the /v1 routes")))
        val footer = "refactor: rename Book.title\n\nBREAKING CHANGE: clients must read `name` instead of `title`"
        assertEquals("with a footer", "2.0.0", nextVersion("1.4.2", listOf("fix: x", footer)))
    }

    @Test fun `no feat or fix means nothing to release`() {
        assertEquals(null, nextVersion("1.4.2", listOf("docs: explain how to run", "chore: bump Gradle")))
    }

    @Test fun `while major is 0 a breaking change bumps the minor`() {
        assertEquals("0.5.0", nextVersion("0.4.2", listOf("feat!: new config format")))
    }

    @Test fun `a tag name is not a version`() {
        assertThrows(IllegalArgumentException::class.java) { nextVersion("v1.2.3", listOf("fix: x")) }
    }
}
