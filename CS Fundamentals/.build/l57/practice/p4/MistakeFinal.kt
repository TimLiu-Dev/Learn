enum class Decision { WAITING, COMMIT, ABORT }

class Coordinator(private val participants: Set<String>) {
    var decision = Decision.WAITING
        private set
    private val yesVotes = mutableSetOf<String>()

    fun onVote(from: String, yes: Boolean): Decision {
        if (!yes) decision = Decision.ABORT
        else {
            yesVotes += from
            if (yesVotes.containsAll(participants)) decision = Decision.COMMIT
        }
        return decision
    }

    // Forgets that a COMMIT may already be logged.
    fun onTimeout(): Decision {
        decision = Decision.ABORT
        return decision
    }
}
