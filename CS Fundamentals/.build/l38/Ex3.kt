// Exercise 3 solution: retry once on a 5xx, proved with a scripted fake. No network, no mocking library.
fun interface HttpPoster { fun post(url: String, body: String): Int }

class FeeNotifier(private val http: HttpPoster, private val baseUrl: String) {
    fun lateFee(member: String, cents: Int): Boolean {
        repeat(2) {                                              // first try + one retry
            val status = http.post("$baseUrl/payments", "member=$member&cents=$cents")
            if (status in 200..299) return true
            if (status !in 500..599) return false                // 4xx: retrying won't help
        }
        return false
    }
}

// Stub + spy in one: answers from a script, records every call.
class ScriptedPoster(vararg statuses: Int) : HttpPoster {
    private val script = ArrayDeque(statuses.toList())
    var calls = 0
        private set
    override fun post(url: String, body: String): Int { calls++; return script.removeFirst() }
}

fun check(name: String, ok: Boolean) = println("${if (ok) "PASS" else "FAIL"}  $name")

fun main() {
    ScriptedPoster(201).let { p -> check("success first time: 1 call", FeeNotifier(p, "x").lateFee("ada", 75) && p.calls == 1) }
    ScriptedPoster(503, 201).let { p -> check("503 then 201: true after 2 calls", FeeNotifier(p, "x").lateFee("ada", 75) && p.calls == 2) }
    ScriptedPoster(503, 503).let { p -> check("503 twice: false after 2 calls", !FeeNotifier(p, "x").lateFee("ada", 75) && p.calls == 2) }
    ScriptedPoster(400).let { p -> check("400: false, no retry", !FeeNotifier(p, "x").lateFee("ada", 75) && p.calls == 1) }
}
