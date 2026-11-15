// Does the If-None-Match header say the client already has this version?
fun clientHasIt(ifNoneMatch: String?, etag: String): Boolean =
    TODO("missing -> false; * -> true; otherwise any tag in the list, ignoring a W/ prefix")

// The whole HTTP/1.1 response, as it goes on the wire.
fun respond(ifNoneMatch: String?, etag: String, cacheControl: String, body: String): String =
    TODO("a 304 with no body, or a 200 with Content-Length and the body")
