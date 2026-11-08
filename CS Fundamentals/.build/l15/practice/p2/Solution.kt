class Request(val method: String, val url: String, val headers: Map<String, String>)

class RequestBuilder {
    var method = "GET"
    var url = ""
    private val headers = linkedMapOf<String, String>()

    fun header(name: String, value: String) {
        headers[name] = value               // a LinkedHashMap keeps a replaced key in its first position
    }

    fun build(): Request {
        require(url.isNotBlank()) { "url must not be blank" }
        return Request(method, url, headers.toMap())    // a copy: later header() calls can't reach it
    }
}

// apply runs block with the builder as `this` and returns the builder, so build() can follow.
fun request(block: RequestBuilder.() -> Unit): Request = RequestBuilder().apply(block).build()
