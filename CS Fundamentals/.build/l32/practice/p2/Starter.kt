data class Response(val status: Int, val body: String)

class Orders {
    val orders = linkedMapOf<Long, String>()         // id -> item: the server's state
    private var nextId = 1L

    // POST /orders, with an optional Idempotency-Key header
    fun post(item: String, key: String?): Response {
        val id = nextId++                            // TODO: look the key up first
        orders[id] = item
        return Response(201, "order $id: $item")
    }

    // PUT /orders/{id}: "make order id look like this". Idempotent already.
    fun put(id: Long, item: String): Response {
        val existed = orders.put(id, item) != null
        return Response(if (existed) 200 else 201, "order $id: $item")
    }
}
