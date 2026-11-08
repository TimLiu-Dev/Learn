open class Item(val name: String) {
    open fun label(): String = "item: $name"
}

class Book(name: String, val author: String) : Item(name)

fun Book.label(): String = "book: $name by $author"   // shadowed by the member: never called

fun labels(items: List<Item>): List<String> = items.map { it.label() }

val List<Item>.bookCount: Int
    get() = count { it is Book }
