enum class Decision { WAITING, COMMIT, ABORT }

// Phase 1 of two-phase commit, as a pure state machine.
class Coordinator(private val participants: Set<String>) {
    var decision = Decision.WAITING
        private set
    private val yesVotes = mutableSetOf<String>()   // a set, so a duplicated vote counts once

    // A participant's vote arrives. Returns the decision afterwards.
    fun onVote(from: String, yes: Boolean): Decision {
        if (decision != Decision.WAITING) return decision        // logged, so final
        if (!yes) decision = Decision.ABORT
        else {
            yesVotes += from
            if (yesVotes.containsAll(participants)) decision = Decision.COMMIT
        }
        return decision
    }

    // The votes took too long. Returns the decision afterwards.
    fun onTimeout(): Decision {
        if (decision == Decision.WAITING) decision = Decision.ABORT  // not every yes arrived: abort is safe
        return decision
    }
}
