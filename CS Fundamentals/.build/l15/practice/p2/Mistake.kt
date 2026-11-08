class Request(val method: String, val url: String, val headers: Map<String, String>)

class RequestBuilder {
    var method = "GET"
    var url = ""
    private val headers = linkedMapOf<String, String>()

    fun header(name: String, value: String) {
        headers[name] = value
    }

    fun build(): Request {
        require(url.isNotBlank()) { "url must not be blank" }
        return Request(method, url, headers)
    }
}

fun request(block: RequestBuilder.() -> Unit): Request = RequestBuilder().apply(block).build()
