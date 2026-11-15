fun main() {
    val undo = ArrayDeque<String>()
    val redo = ArrayDeque<String>()
    for (edit in listOf("a", "b", "c")) undo.addLast(edit)

    redo.addLast(undo.removeLast())     // undo
    redo.addLast(undo.removeLast())     // undo
    undo.addLast(redo.removeLast())     // redo

    println("$undo $redo")
}
