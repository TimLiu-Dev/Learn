// Does the If-None-Match header say the client already has this version?
fun clientHasIt(ifNoneMatch: String?, etag: String): Boolean {
    if (ifNoneMatch == null) return false
    if (ifNoneMatch.trim() == "*") return true                    // "any version you have"
    return ifNoneMatch.split(",").any { it.trim().removePrefix("W/") == etag.removePrefix("W/") }
}

// The whole HTTP/1.1 response, as it goes on the wire.
fun respond(ifNoneMatch: String?, etag: String, cacheControl: String, body: String): String {
    if (clientHasIt(ifNoneMatch, etag)) {
        return "HTTP/1.1 304 Not Modified\r\nETag: $etag\r\nCache-Control: $cacheControl\r\n\r\n"
    }
    val length = body.encodeToByteArray().size                    // bytes, not characters
    return "HTTP/1.1 200 OK\r\nETag: $etag\r\nCache-Control: $cacheControl\r\n" +
        "Content-Length: $length\r\n\r\n$body"
}
