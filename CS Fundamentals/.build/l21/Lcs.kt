// dp[i][j] = LCS length of the first i chars of a and the first j chars of b.
fun lcs(a: String, b: String): String {
    val dp = Array(a.length + 1) { IntArray(b.length + 1) }    // row 0 / column 0 = empty prefix = 0
    for (i in 1..a.length) {
        for (j in 1..b.length) {
            dp[i][j] = if (a[i - 1] == b[j - 1]) dp[i - 1][j - 1] + 1
                       else maxOf(dp[i - 1][j], dp[i][j - 1])
        }
    }
    // Walk back from the bottom-right corner to recover the letters.
    val sb = StringBuilder()
    var i = a.length
    var j = b.length
    while (i > 0 && j > 0) {
        when {
            a[i - 1] == b[j - 1] -> { sb.append(a[i - 1]); i--; j-- }
            dp[i - 1][j] >= dp[i][j - 1] -> i--
            else -> j--
        }
    }
    return sb.reverse().toString()
}

fun main() {
    for ((a, b) in listOf("STACK" to "TRACK", "HASHMAP" to "HEAP", "AGGTAB" to "GXTXAYB", "ABC" to "XYZ")) {
        val s = lcs(a, b)
        println("lcs($a, $b) = \"$s\" (${s.length})")
    }
}
