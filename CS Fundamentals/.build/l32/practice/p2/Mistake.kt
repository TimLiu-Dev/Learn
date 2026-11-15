data class Response(val status: Int, val body: String)

class Orders {
    val orders = linkedMapOf<Long, String>()
    private var nextId = 1L

    // "Deduplicate" by looking for the same item, ignoring the key
    fun post(item: String, key: String?): Response {
        if (key != null) {
            val existing = orders.entries.firstOrNull { it.value == item }
            if (existing != null) return Response(201, "order ${existing.key}: $item")
        }
        val id = nextId++
        orders[id] = item
        return Response(201, "order $id: $item")
    }

    fun put(id: Long, item: String): Response {
        val existed = orders.put(id, item) != null
        return Response(if (existed) 200 else 201, "order $id: $item")
    }
}
