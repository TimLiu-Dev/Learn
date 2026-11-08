// A bug hides where behaviour changes, so test both sides of every edge.
val gradeCases: List<Pair<Int, String>> = listOf(
    -1 to "invalid", 0 to "F",          // bottom of the valid range
    89 to "B", 90 to "A",               // the A/B edge, from both sides
    80 to "B", 70 to "C",               // the lowest B and the lowest C
    100 to "A", 101 to "invalid",       // top of the valid range
)
