// 1. An order lifecycle where illegal transitions don't compile.
sealed interface Order {
    data class Placed(val items: List<String>) : Order
    data class Paid(val items: List<String>, val paymentId: String) : Order
    data class Shipped(val items: List<String>, val tracking: String) : Order
    data class Cancelled(val refund: Boolean) : Order
}
fun Order.Placed.pay(paymentId: String) = Order.Paid(items, paymentId)
fun Order.Placed.cancel() = Order.Cancelled(refund = false)
fun Order.Paid.ship(tracking: String) = Order.Shipped(items, tracking)
fun Order.Paid.cancel() = Order.Cancelled(refund = true)
// No Order.Shipped.cancel(): "cancel a shipped order" can't be written.

// 2. Commands that know how to undo themselves.
class Editor { val text = StringBuilder() }
sealed interface EditCommand {
    fun run(e: Editor)
    fun undo(e: Editor)
    data class Insert(val s: String) : EditCommand {
        override fun run(e: Editor) { e.text.append(s) }
        override fun undo(e: Editor) { e.text.setLength(e.text.length - s.length) }
    }
    class DeleteLast(private val n: Int) : EditCommand {
        private var removed = ""                                  // remembered for undo
        override fun run(e: Editor) {
            removed = e.text.takeLast(n).toString()
            e.text.setLength(e.text.length - removed.length)
        }
        override fun undo(e: Editor) { e.text.append(removed) }
    }
}
class History(private val editor: Editor) {
    private val done = ArrayDeque<EditCommand>()
    fun run(c: EditCommand) { c.run(editor); done.addLast(c) }
    fun undo() { done.removeLastOrNull()?.undo(editor) }
}

fun main() {
    val order = Order.Placed(listOf("Dune")).pay("pay-1")
    println(order.ship("TRK-9"))
    println(order.cancel())

    val editor = Editor()
    val history = History(editor)
    history.run(EditCommand.Insert("Hello"))
    history.run(EditCommand.Insert(", world"))
    history.run(EditCommand.DeleteLast(5))
    println(editor.text)
    history.undo(); println(editor.text)
    history.undo(); history.undo(); history.undo(); println("[${editor.text}]")
}
