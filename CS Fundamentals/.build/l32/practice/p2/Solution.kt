data class Response(val status: Int, val body: String)

class Orders {
    val orders = linkedMapOf<Long, String>()         // id -> item: the server's state
    private var nextId = 1L
    private val seen = mutableMapOf<String, Pair<String, Response>>()   // key -> (item, first response)

    // POST /orders, with an optional Idempotency-Key header
    fun post(item: String, key: String?): Response {
        if (key != null) {
            val earlier = seen[key]
            if (earlier != null) {
                val (earlierItem, earlierResponse) = earlier
                return if (earlierItem == item) earlierResponse      // a retry: replay, don't redo
                else Response(422, "key $key was used for a different request")
            }
        }
        val id = nextId++
        orders[id] = item
        val response = Response(201, "order $id: $item")
        if (key != null) seen[key] = item to response
        return response
    }

    // PUT /orders/{id}: "make order id look like this". Idempotent already.
    fun put(id: Long, item: String): Response {
        val existed = orders.put(id, item) != null
        return Response(if (existed) 200 else 201, "order $id: $item")
    }
}
