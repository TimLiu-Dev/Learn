// Eager: mapNotNull calls lookup for every id before take ever runs.
fun firstFound(ids: List<Int>, n: Int, lookup: (Int) -> String?): List<String> =
    ids.mapNotNull(lookup).take(n)

fun collatz(start: Int): Sequence<Int> = TODO("generateSequence ends when its function returns null")
