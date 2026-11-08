import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class RepoTest {
    @Test fun `a new branch points at the current commit and makes no commit`() {
        val repo = Repo()
        repo.commit()
        repo.createBranch("fix")
        assertEquals("c2", repo.branches["fix"])
        assertEquals("HEAD stays on main", "main", repo.head)
        assertEquals("no new commit", 2, repo.parent.size)
    }

    @Test fun `committing moves only the branch HEAD points to`() {
        val repo = Repo()
        repo.createBranch("fix")
        repo.switch("fix")
        assertEquals("c2", repo.commit())
        assertEquals(mapOf("main" to "c1", "fix" to "c2"), repo.branches)
    }

    @Test fun `switching moves HEAD, not any branch`() {
        val repo = Repo()
        repo.createBranch("fix")
        repo.switch("fix")
        repo.commit()
        repo.switch("main")
        assertEquals("main", repo.head)
        assertEquals(mapOf("main" to "c1", "fix" to "c2"), repo.branches)
    }

    @Test fun `log follows parents back from HEAD`() {
        val repo = Repo()
        repo.commit()                 // c2 on main
        repo.createBranch("fix")
        repo.switch("fix")
        repo.commit()                 // c3 on fix
        repo.switch("main")
        repo.commit()                 // c4 on main: the history has diverged
        assertEquals(listOf("c4", "c2", "c1"), repo.log())
        repo.switch("fix")
        assertEquals(listOf("c3", "c2", "c1"), repo.log())
    }

    @Test fun `bad branch names are refused`() {
        val repo = Repo()
        try { repo.createBranch("main"); fail("createBranch(\"main\") should throw") } catch (e: IllegalArgumentException) { }
        try { repo.switch("nope"); fail("switch(\"nope\") should throw") } catch (e: IllegalArgumentException) { }
        assertEquals(mapOf("main" to "c1"), repo.branches)
    }
}
