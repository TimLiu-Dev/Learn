open class Item(val name: String)

class Book(name: String, val author: String) : Item(name)

fun Item.label(): String = "item: $name"
fun Book.label(): String = "book: $name by $author"

fun labels(items: List<Item>): List<String> = items.map { it.label() }

val List<Item>.bookCount: Int
    get() = TODO("how many of the items are books?")
