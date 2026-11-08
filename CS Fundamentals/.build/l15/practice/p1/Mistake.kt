fun firstFound(ids: List<Int>, n: Int, lookup: (Int) -> String?): List<String> =
    ids.mapNotNull(lookup).asSequence().take(n).toList()

fun collatz(start: Int): Sequence<Int> =
    generateSequence(start) { n ->
        when {
            n == 1 -> null
            n % 2 == 0 -> n / 2
            else -> 3 * n + 1
        }
    }
