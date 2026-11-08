class Request(val method: String, val url: String, val headers: Map<String, String>)

class RequestBuilder {
    var method = "GET"
    var url = ""
    private val headers = linkedMapOf<String, String>()

    fun header(name: String, value: String) {
        TODO("store it; a repeated name replaces the old value")
    }

    fun build(): Request = TODO("reject a blank url, then build a Request that can't change later")
}

fun request(block: RequestBuilder.() -> Unit): Request = TODO("a fresh builder as the receiver of block, then build()")
