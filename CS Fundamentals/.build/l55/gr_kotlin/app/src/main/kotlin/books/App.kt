package books

fun main(args: Array<String>) {
    val port = args.getOrNull(0)?.toInt() ?: 18055
    val seconds = args.getOrNull(1)?.toLong() ?: 300                 // never leave a server running
    // Demo users. In real life passwords come from a sign-up form, never from source code.
    val auth = Auth(mapOf("ana" to "correct horse", "ben" to "battery staple", "root" to "admin admin"), admins = setOf("root"))
    val api = BookApi(auth, port)
    api.start()
    println("Books API on http://127.0.0.1:${api.port}/books (stops after $seconds s)")
    Thread.sleep(seconds * 1000)
    api.stop()
    println("stopped")
}
