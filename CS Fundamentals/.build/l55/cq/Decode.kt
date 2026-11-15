import java.util.Base64

fun main() {
    // The third part of RFC 7515's example token: a genuine signature.
    val sig = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
    val results = listOf("basic" to Base64.getDecoder(), "url" to Base64.getUrlDecoder()).map { (name, decoder) ->
        name + ": " + runCatching { decoder.decode(sig).size }.fold({ "$it bytes" }, { "rejected" })
    }
    println(results.joinToString(", "))
}
