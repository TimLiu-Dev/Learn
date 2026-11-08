import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MergeTest {
    // main: c1 - c3, fix: c1 - c2 (diverged)
    fun diverged() = Repo().apply {
        createBranch("fix"); switch("fix"); commit()   // c2 on fix
        switch("main"); commit()                       // c3 on main
    }

    @Test fun `merging a branch that is ahead fast-forwards`() {
        val repo = Repo()
        repo.createBranch("fix"); repo.switch("fix"); repo.commit()
        repo.switch("main")
        assertEquals("fast-forward", repo.merge("fix"))
        assertEquals("c2", repo.branches["main"])
        assertEquals("a fast-forward makes no commit", 2, repo.parents.size)
    }

    @Test fun `diverged branches get a merge commit with two parents`() {
        val repo = diverged()
        assertEquals("merge commit", repo.merge("fix"))
        assertEquals("c4", repo.branches["main"])
        assertEquals("parents: your tip, then theirs", listOf("c3", "c2"), repo.parents["c4"])
        assertEquals("the merged branch doesn't move", "c2", repo.branches["fix"])
    }

    @Test fun `merging what you already have changes nothing`() {
        val repo = diverged()
        repo.merge("fix")
        assertEquals("up to date", repo.merge("fix"))
        assertEquals("c4", repo.branches["main"])
        assertEquals(4, repo.parents.size)
    }

    @Test fun `after a merge, merging back the other way is a fast-forward`() {
        val repo = diverged()
        repo.merge("fix")             // c4 = merge of c3 and c2, on main
        repo.switch("fix")
        assertEquals("fast-forward", repo.merge("main"))
        assertEquals("c4", repo.branches["fix"])
    }

    @Test fun `isAncestor follows every parent`() {
        val repo = diverged()
        repo.merge("fix")
        assertTrue("c2 is c4's second parent", repo.isAncestor("c2", "c4"))
        assertTrue("a commit is its own ancestor", repo.isAncestor("c1", "c1"))
        assertFalse("ancestry only goes backwards", repo.isAncestor("c4", "c2"))
        assertFalse("side branches aren't ancestors", repo.isAncestor("c3", "c2"))
    }
}
