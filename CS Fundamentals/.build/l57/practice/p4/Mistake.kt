enum class Decision { WAITING, COMMIT, ABORT }

class Coordinator(private val participants: Set<String>) {
    var decision = Decision.WAITING
        private set
    private var yesCount = 0          // counts messages, not participants

    fun onVote(from: String, yes: Boolean): Decision {
        if (decision != Decision.WAITING) return decision
        if (!yes) decision = Decision.ABORT
        else if (++yesCount == participants.size) decision = Decision.COMMIT
        return decision
    }

    fun onTimeout(): Decision {
        if (decision == Decision.WAITING) decision = Decision.ABORT
        return decision
    }
}
