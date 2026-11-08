// Lazy: each id goes through lookup only when take(n) still wants another name.
fun firstFound(ids: List<Int>, n: Int, lookup: (Int) -> String?): List<String> =
    ids.asSequence().mapNotNull(lookup).take(n).toList()

fun collatz(start: Int): Sequence<Int> =
    generateSequence(start) { n ->
        when {
            n == 1 -> null              // null ends the sequence, after 1 was emitted
            n % 2 == 0 -> n / 2
            else -> 3 * n + 1
        }
    }
