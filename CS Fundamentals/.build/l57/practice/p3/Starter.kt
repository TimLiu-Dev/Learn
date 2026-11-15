// What a voter remembers. Raft keeps all of it on disk.
data class Voter(
    val currentTerm: Long,
    val votedFor: Int?,      // whom it voted for in currentTerm, or null
    val lastLogIndex: Int,   // 0 for an empty log
    val lastLogTerm: Long,   // 0 for an empty log
)

data class RequestVote(val term: Long, val candidateId: Int, val lastLogIndex: Int, val lastLogTerm: Long)

// The voter after handling a request, and whether it granted its vote.
data class Reply(val voter: Voter, val granted: Boolean)

// Is the candidate's log at least as up-to-date as the voter's?
fun candidateIsUpToDate(v: Voter, req: RequestVote): Boolean =
    TODO("compare last log terms first; only if they're equal, compare last indexes")

fun onRequestVote(v: Voter, req: RequestVote): Reply =
    TODO("stale term? newer term? already voted this term? log up to date?")
