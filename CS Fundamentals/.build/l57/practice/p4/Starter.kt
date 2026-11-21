enum class Decision { WAITING, COMMIT, ABORT }

// Phase 1 of two-phase commit, as a pure state machine.
class Coordinator(private val participants: Set<String>) {
    var decision = Decision.WAITING
        private set

    // A participant's vote arrives. Returns the decision afterwards.
    fun onVote(from: String, yes: Boolean): Decision =
        TODO("no -> ABORT; the last missing yes -> COMMIT; once decided, never change")

    // The votes took too long. Returns the decision afterwards.
    fun onTimeout(): Decision = TODO("a coordinator still waiting gives up")
}
