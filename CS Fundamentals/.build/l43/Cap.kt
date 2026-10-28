// Two replicas of one key-value store, joined by a network link we can cut.
// CP mode refuses writes it can't replicate; AP mode accepts them and syncs later.

data class Versioned(val value: String, val ts: Long, val node: String)

class Link { var up = true }

var clock = 0L                                   // one logical clock, so timestamps are comparable

class Node(val name: String, private val link: Link, private val mode: String) {
    lateinit var peer: Node
    val data = mutableMapOf<String, Versioned>()
    private val unsent = mutableListOf<Pair<String, Versioned>>()   // AP: writes the peer hasn't seen

    fun write(key: String, value: String): String {
        val v = Versioned(value, ++clock, name)
        if (mode == "CP") {
            if (!link.up) return "REJECTED: can't reach ${peer.name}, so the write can't be replicated"
            data[key] = v; peer.data[key] = v
            return "OK (replicated to ${peer.name})"
        }
        data[key] = v
        if (link.up) peer.receive(key, v) else unsent += key to v
        return if (link.up) "OK (sent to ${peer.name})" else "OK locally (${peer.name} will hear later)"
    }

    fun read(key: String) = data[key]?.value ?: "-"

    fun receive(key: String, incoming: Versioned) {
        val mine = data[key]
        if (mine == null || incoming.ts > mine.ts) {        // last write wins
            data[key] = incoming
        } else if (incoming.ts < mine.ts) {
            println("    $name drops ${incoming.value} (t=${incoming.ts} from ${incoming.node}): older than ${mine.value} (t=${mine.ts})")
        }
    }

    fun flush() { unsent.forEach { (k, v) -> peer.receive(k, v) }; unsent.clear() }
}

fun cluster(mode: String): Triple<Node, Node, Link> {
    val link = Link()
    val a = Node("A", link, mode); val b = Node("B", link, mode)
    a.peer = b; b.peer = a
    return Triple(a, b, link)
}

fun main() {
    println("== CP mode: a bank balance")
    val (a, b, link) = cluster("CP")
    println("A write balance=100 -> ${a.write("balance", "100")}")
    println("-- partition: the link between A and B is cut")
    link.up = false
    println("A write balance=70  -> ${a.write("balance", "70")}")
    println("B write balance=40  -> ${b.write("balance", "40")}")
    println("reads: A=${a.read("balance")} B=${b.read("balance")}   (consistent, but no one can write)")

    println("\n== AP mode: a shopping cart")
    val (c, d, link2) = cluster("AP")
    println("A write cart=[book] -> ${c.write("cart", "[book]")}")
    println("-- partition")
    link2.up = false
    println("A write cart=[book,lamp] -> ${c.write("cart", "[book,lamp]")}")
    println("B write cart=[book,pen]  -> ${d.write("cart", "[book,pen]")}")
    println("reads: A=${c.read("cart")} B=${d.read("cart")}   (available, but they disagree)")
    println("-- heal: exchange the writes each side missed (last write wins)")
    link2.up = true
    c.flush(); d.flush()
    println("reads: A=${c.read("cart")} B=${d.read("cart")}   (agree again, and the lamp is gone)")
}
