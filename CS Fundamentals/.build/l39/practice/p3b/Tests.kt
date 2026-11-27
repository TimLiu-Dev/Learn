import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewWorkflowTest {
    @Test fun `a draft can be edited and then submitted`() {
        val edited = step(Doc.Draft("v1"), Cmd.Edit("v2"))
        assertEquals(Step.Moved(Doc.Draft("v2")), edited)
        assertEquals(Step.Moved(Doc.InReview("v2", "Grace")), step(Doc.Draft("v2"), Cmd.Submit("Grace")))
    }

    @Test fun `approving a document in review publishes it`() {
        assertEquals(Step.Moved(Doc.Published("v2")), step(Doc.InReview("v2", "Grace"), Cmd.Approve))
    }

    @Test fun `requested changes go back to draft by editing`() {
        val asked = step(Doc.InReview("v2", "Grace"), Cmd.RequestChanges("cite it"))
        assertEquals(Step.Moved(Doc.ChangesRequested("v2", "cite it")), asked)
        assertEquals(Step.Moved(Doc.Draft("v3")), step(Doc.ChangesRequested("v2", "cite it"), Cmd.Edit("v3")))
    }

    @Test fun `illegal moves are rejected and leave the document unchanged`() {
        val draft = Doc.Draft("v1")
        val review = Doc.InReview("v1", "Grace")
        val asked = Doc.ChangesRequested("v1", "cite it")
        val published = Doc.Published("v1")
        assertEquals("approve a draft", Step.Rejected(draft), step(draft, Cmd.Approve))
        assertEquals("edit under review", Step.Rejected(review), step(review, Cmd.Edit("sneaky")))
        assertEquals("resubmit without editing", Step.Rejected(asked), step(asked, Cmd.Submit("Grace")))
        for (cmd in listOf(Cmd.Edit("v2"), Cmd.Submit("Grace"), Cmd.Approve, Cmd.RequestChanges("no")))
            assertEquals("$cmd on a published doc", Step.Rejected(published), step(published, cmd))
    }

    @Test fun `replay applies commands in order and counts rejections`() {
        val cmds = listOf(Cmd.Approve, Cmd.Submit("Grace"), Cmd.RequestChanges("cite it"),
                          Cmd.Approve, Cmd.Edit("v2"), Cmd.Submit("Linus"), Cmd.Approve, Cmd.Edit("v3"))
        assertEquals(Replayed(Doc.Published("v2"), 3), replay(Doc.Draft("v1"), cmds))
        assertEquals(Replayed(Doc.Draft("v1"), 0), replay(Doc.Draft("v1"), emptyList()))
    }
}
