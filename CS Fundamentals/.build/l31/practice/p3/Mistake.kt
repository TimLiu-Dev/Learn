// Compares the whole header with the tag: misses lists, W/ tags and *.
fun clientHasIt(ifNoneMatch: String?, etag: String): Boolean = ifNoneMatch == etag

fun respond(ifNoneMatch: String?, etag: String, cacheControl: String, body: String): String {
    if (clientHasIt(ifNoneMatch, etag)) {
        return "HTTP/1.1 304 Not Modified\r\nETag: $etag\r\nCache-Control: $cacheControl\r\n\r\n"
    }
    val length = body.encodeToByteArray().size
    return "HTTP/1.1 200 OK\r\nETag: $etag\r\nCache-Control: $cacheControl\r\n" +
        "Content-Length: $length\r\n\r\n$body"
}
