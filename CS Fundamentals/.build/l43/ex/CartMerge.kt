// Exercise solution: keep both versions of a concurrent write, then merge them.
// A version remembers which version it replaced (its parent). Two versions with
// the same parent were written without seeing each other: a real conflict.

data class Cart(val items: Set<String>, val ts: Long, val parent: Long, val node: String)

var clock = 0L

class Replica(val name: String) {
    var cart = Cart(emptySet(), 0, 0, "-")
    val unsent = mutableListOf<Cart>()

    fun add(item: String) {
        cart = Cart(cart.items + item, ++clock, cart.ts, name)
        unsent += cart
    }

    fun receive(incoming: Cart) {
        when {
            incoming.parent == cart.ts -> cart = incoming                      // it built on mine: take it
            incoming.parent == cart.parent && incoming.ts != cart.ts -> {      // siblings: merge, lose nothing
                val merged = cart.items + incoming.items
                println("    $name: conflict ${cart.items} vs ${incoming.items} -> keep both: $merged")
                cart = Cart(merged, ++clock, maxOf(cart.ts, incoming.ts), name)
            }
            else -> {}                                                          // already have it or newer
        }
    }
}

fun main() {
    val a = Replica("A"); val b = Replica("B")
    a.add("book"); b.receive(a.unsent.removeFirst())
    println("synced: A=${a.cart.items} B=${b.cart.items}")
    println("-- partition")
    a.add("lamp"); b.add("pen")
    println("diverged: A=${a.cart.items} B=${b.cart.items}")
    println("-- heal")
    b.receive(a.unsent.removeFirst()); a.receive(b.unsent.removeFirst())
    println("A=${a.cart.items} B=${b.cart.items}")
}
