sealed interface Doc {
    data class Draft(val text: String) : Doc
    data class InReview(val text: String, val reviewer: String) : Doc
    data class ChangesRequested(val text: String, val note: String) : Doc
    data class Published(val text: String) : Doc
}

// Command: each request is an object
sealed interface Cmd {
    data class Edit(val text: String) : Cmd
    data class Submit(val reviewer: String) : Cmd
    data object Approve : Cmd
    data class RequestChanges(val note: String) : Cmd
}

sealed interface Step {
    data class Moved(val doc: Doc) : Step
    data class Rejected(val doc: Doc) : Step
}

data class Replayed(val doc: Doc, val rejected: Int)

fun step(doc: Doc, cmd: Cmd): Step = TODO("decide every (state, command) pair")

fun replay(start: Doc, cmds: List<Cmd>): Replayed = TODO("apply the commands in order; count the rejected ones")
