package books

fun main(args: Array<String>) {
    val port = args.getOrNull(0)?.toInt() ?: 18032
    val seconds = args.getOrNull(1)?.toLong() ?: 300                 // never leave a server running
    val api = BookApi(port)
    api.start()
    println("Books API on http://127.0.0.1:${api.port}/books (stops after $seconds s)")
    Thread.sleep(seconds * 1000)
    api.stop()
    println("stopped")
}
