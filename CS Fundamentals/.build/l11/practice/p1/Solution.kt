fun expectEqual(expected: Any?, actual: Any?) {
    if (expected != actual) throw AssertionError("expected: <$expected> but was: <$actual>")
}

fun runAll(tests: Map<String, () -> Unit>): List<String> {
    val report = mutableListOf<String>()
    for ((name, body) in tests) {
        try {
            body()
            report += "PASS $name"
        } catch (e: AssertionError) {     // an Error, not an Exception (L10)
            report += "FAIL $name: ${e.message}"
        }
    }
    return report
}
