package org.example

fun main() {
    val text = """
        The cat sat on the mat. The mat was flat,
        and the cat was fat! A cat, a mat, a hat.
    """.trimIndent()
    println(wordReport(text))

    val cache = LruCache<String, String>(2)
    cache["home"] = "<html>home</html>"
    cache["about"] = "<html>about</html>"
    cache["home"]                                   // home is now the most recent
    cache["blog"] = "<html>blog</html>"             // evicts about
    println("cache keys: ${cache.keys()}, about cached? ${cache["about"] != null}")
}
