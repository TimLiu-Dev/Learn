var published = setOf("k1")          // the kids in the AS's JWKS right now
var cached = emptySet<String>()      // the API's copy
var fetches = 0

fun knows(kid: String): Boolean {
    if (kid !in cached) { cached = published; fetches++ }   // unknown kid: fetch the JWKS again
    return kid in cached
}

fun main() {
    val a = knows("k1")
    published = setOf("k1", "k2")    // the AS rotates in a new key
    val b = knows("k1")
    val c = knows("k2")
    val d = knows("zz")
    println("$a $b $c $d fetches=$fetches")
}
