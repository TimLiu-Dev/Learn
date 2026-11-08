// Exercise: a group chat that never shows a reply before the message it answers.
// Each message carries a vector clock; a member holds a message back until everything
// it depends on has been delivered (causal delivery).

class Msg(val sender: Int, val text: String, val vc: IntArray)

val names = listOf("Ana", "Bob", "Cleo")

class Member(val id: Int, n: Int) {
    private val delivered = IntArray(n)             // delivered[k] = how many of k's messages I've shown
    private val waiting = mutableListOf<Msg>()
    val screen = mutableListOf<String>()

    fun send(text: String): Msg {
        val vc = delivered.copyOf().also { it[id]++ }
        val m = Msg(id, text, vc)
        show(m)
        return m
    }

    fun receive(m: Msg) {
        waiting += m
        while (true) {
            val next = waiting.firstOrNull { ready(it) } ?: break
            waiting -= next
            show(next)
        }
        for (w in waiting) println("  ${names[id]} holds back ${names[w.sender]}: \"${w.text}\" (missing an earlier message)")
    }

    private fun ready(m: Msg) = m.vc[m.sender] == delivered[m.sender] + 1 &&
        m.vc.indices.all { it == m.sender || m.vc[it] <= delivered[it] }

    private fun show(m: Msg) {
        delivered[m.sender]++
        screen += "${names[m.sender]}: ${m.text}"
    }
}

fun main() {
    val (ana, bob, cleo) = List(3) { Member(it, 3) }
    val m1 = ana.send("Dinner at 7?")
    bob.receive(m1)
    val m2 = bob.send("Yes, 7 works")
    val m3 = cleo.send("I'm running late today")      // Cleo hasn't seen anything yet: concurrent
    // The network delivers in a bad order:
    cleo.receive(m2)                                  // the reply overtakes the question
    ana.receive(m3); ana.receive(m2)
    cleo.receive(m1)
    bob.receive(m3)
    for (m in listOf(ana, bob, cleo)) {
        println("${names[m.id]}'s screen:")
        m.screen.forEach { println("    $it") }
    }
}
