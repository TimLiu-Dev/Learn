class Step(val name: String, val always: Boolean = false, val run: () -> Int)

data class JobResult(val passed: Boolean, val log: List<String>)

/** Runs the steps in order, like one CI job. */
fun runJob(steps: List<Step>): JobResult {
    var passed = true
    val log = mutableListOf<String>()
    for (step in steps) {
        if (!passed && !step.always) {
            log.add("skipped ${step.name}")
            continue
        }
        // An exception is just another way for a step to fail (L10), not a reason to crash the runner.
        val problem = try {
            val code = step.run()
            if (code == 0) null else "exit $code"
        } catch (e: Exception) {
            "threw ${e::class.simpleName}"
        }
        if (problem == null) {
            log.add("ok ${step.name}")
        } else {
            log.add("FAILED ${step.name} ($problem)")
            passed = false
        }
    }
    return JobResult(passed, log)
}
