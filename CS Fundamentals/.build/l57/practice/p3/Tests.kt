import org.junit.Assert.assertEquals
import org.junit.Test

class VoteTest {
    @Test fun `a fresh voter grants and remembers its vote`() {
        val v = Voter(currentTerm = 3, votedFor = null, lastLogIndex = 5, lastLogTerm = 2)
        assertEquals(Reply(v.copy(votedFor = 1), true), onRequestVote(v, RequestVote(3, 1, 5, 2)))
    }

    @Test fun `a request from an older term is refused and changes nothing`() {
        val v = Voter(currentTerm = 5, votedFor = null, lastLogIndex = 0, lastLogTerm = 0)
        assertEquals(Reply(v, false), onRequestVote(v, RequestVote(4, 1, 9, 4)))
    }

    @Test fun `one vote per term, but the same candidate may ask again`() {
        val v = Voter(currentTerm = 3, votedFor = 2, lastLogIndex = 0, lastLogTerm = 0)
        assertEquals("n2 already has this term's vote", Reply(v, false), onRequestVote(v, RequestVote(3, 1, 0, 0)))
        assertEquals("a retried request from n2", Reply(v, true), onRequestVote(v, RequestVote(3, 2, 0, 0)))
    }

    @Test fun `a newer term frees the vote`() {
        val v = Voter(currentTerm = 3, votedFor = 2, lastLogIndex = 4, lastLogTerm = 3)
        assertEquals(Reply(Voter(4, 1, 4, 3), true), onRequestVote(v, RequestVote(4, 1, 4, 3)))
    }

    @Test fun `the last term decides before the length`() {
        val v = Voter(currentTerm = 5, votedFor = null, lastLogIndex = 10, lastLogTerm = 2)
        fun asks(lastIndex: Int, lastTerm: Long) = onRequestVote(v, RequestVote(5, 1, lastIndex, lastTerm)).granted
        assertEquals("shorter log, later last term", true, asks(3, 3))
        assertEquals("longer log, earlier last term", false, asks(20, 1))
        assertEquals("same last term, shorter log", false, asks(9, 2))
        assertEquals("same last term, same length", true, asks(10, 2))
    }

    @Test fun `a newer term is adopted even when the vote is refused`() {
        val v = Voter(currentTerm = 3, votedFor = 2, lastLogIndex = 10, lastLogTerm = 3)
        assertEquals("the candidate's log is behind, but its term is news",
            Reply(Voter(5, null, 10, 3), false), onRequestVote(v, RequestVote(5, 1, 4, 2)))
    }
}
