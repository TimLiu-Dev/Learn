// 1. An assertion: quiet when equal, loud when not.
fun expectEqual(expected: Any?, actual: Any?) {
    TODO("compare with ==, and throw an AssertionError on a mismatch")
}

// 2. A runner: body() runs one test. Report every test, even after a failure.
fun runAll(tests: Map<String, () -> Unit>): List<String> {
    TODO("run each body inside try/catch and build the report lines")
}
