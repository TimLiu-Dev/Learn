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

// Switch on the state first: each state lists the commands it accepts.
fun step(doc: Doc, cmd: Cmd): Step {
    val next: Doc? = when (doc) {
        is Doc.Draft -> when (cmd) {
            is Cmd.Edit -> Doc.Draft(cmd.text)
            is Cmd.Submit -> Doc.InReview(doc.text, cmd.reviewer)
            else -> null
        }
        is Doc.InReview -> when (cmd) {
            Cmd.Approve -> Doc.Published(doc.text)
            is Cmd.RequestChanges -> Doc.ChangesRequested(doc.text, cmd.note)
            else -> null
        }
        is Doc.ChangesRequested -> if (cmd is Cmd.Edit) Doc.Draft(cmd.text) else null
        is Doc.Published -> null
    }
    return if (next == null) Step.Rejected(doc) else Step.Moved(next)
}

fun replay(start: Doc, cmds: List<Cmd>): Replayed =
    cmds.fold(Replayed(start, 0)) { acc, cmd ->
        when (val s = step(acc.doc, cmd)) {
            is Step.Moved -> acc.copy(doc = s.doc)
            is Step.Rejected -> acc.copy(rejected = acc.rejected + 1)
        }
    }
