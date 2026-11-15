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
// A later last term wins outright; only on a tie does the longer log win.
fun candidateIsUpToDate(v: Voter, req: RequestVote): Boolean =
    req.lastLogTerm > v.lastLogTerm ||
        (req.lastLogTerm == v.lastLogTerm && req.lastLogIndex >= v.lastLogIndex)

fun onRequestVote(v: Voter, req: RequestVote): Reply {
    if (req.term < v.currentTerm) return Reply(v, false)              // a stale candidate
    // A newer term always wins: adopt it, with a fresh vote, before deciding.
    val voter = if (req.term > v.currentTerm) v.copy(currentTerm = req.term, votedFor = null) else v
    val voteFree = voter.votedFor == null || voter.votedFor == req.candidateId
    return if (voteFree && candidateIsUpToDate(voter, req)) Reply(voter.copy(votedFor = req.candidateId), true)
    else Reply(voter, false)
}
