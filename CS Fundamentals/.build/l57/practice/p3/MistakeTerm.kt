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

fun candidateIsUpToDate(v: Voter, req: RequestVote): Boolean =
    req.lastLogTerm > v.lastLogTerm ||
        (req.lastLogTerm == v.lastLogTerm && req.lastLogIndex >= v.lastLogIndex)

// Adopts the newer term only when it grants the vote.
fun onRequestVote(v: Voter, req: RequestVote): Reply {
    if (req.term < v.currentTerm) return Reply(v, false)
    val voteFree = req.term > v.currentTerm || v.votedFor == null || v.votedFor == req.candidateId
    return if (voteFree && candidateIsUpToDate(v, req))
        Reply(v.copy(currentTerm = req.term, votedFor = req.candidateId), true)
    else Reply(v, false)
}
