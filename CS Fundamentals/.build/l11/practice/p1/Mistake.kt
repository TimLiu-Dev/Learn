fun expectEqual(expected: Any?, actual: Any?) {
    if (expected != actual) throw AssertionError("expected: <$expected> but was: <$actual>")
}

fun runAll(tests: Map<String, () -> Unit>): List<String> {
    val report = mutableListOf<String>()
    for ((name, body) in tests) {
        try {
            body()
            report += "PASS $name"
        } catch (e: Exception) {          // misses AssertionError: it's an Error
            report += "FAIL $name: ${e.message}"
        }
    }
    return report
}
