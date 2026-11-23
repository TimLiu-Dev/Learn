var logged = 0
fun log(msg: String) { logged++; println("ERROR $msg") }

fun dao(): Int = throw IllegalStateException("disk full")
fun repo(): Int = try { dao() } catch (e: Exception) { log("repo: ${e.message}"); throw e }
fun service(): Int = try { repo() } catch (e: Exception) { log("service: ${e.message}"); throw e }

fun main() {
    try { service() } catch (e: Exception) { log("request failed: ${e.message}") }
    println("lines logged: $logged")
}
