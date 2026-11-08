open class Item(val name: String) {
    open fun label(): String = "item: $name"        // a member, so subclasses can override it
}

class Book(name: String, val author: String) : Item(name) {
    override fun label(): String = "book: $name by $author"
}

fun labels(items: List<Item>): List<String> = items.map { it.label() }   // now dispatched at runtime

// Still a fine extension: List isn't ours, and nothing here needs dispatch.
val List<Item>.bookCount: Int
    get() = count { it is Book }
