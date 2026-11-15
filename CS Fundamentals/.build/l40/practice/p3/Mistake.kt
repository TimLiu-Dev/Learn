class Step(val name: String, val always: Boolean = false, val run: () -> Int)

data class JobResult(val passed: Boolean, val log: List<String>)

fun runJob(steps: List<Step>): JobResult {
    var passed = true
    val log = mutableListOf<String>()
    for (step in steps) {
        if (!passed && !step.always) {
            log.add("skipped ${step.name}")
            continue
        }
        val problem = try {
            val code = step.run()
            if (code == 0) null else "exit $code"
        } catch (e: Exception) {
            "threw ${e::class.simpleName}"
        }
        // The job's result is whatever the latest step that ran said, so a green always-step hides the failure.
        passed = problem == null
        log.add(if (problem == null) "ok ${step.name}" else "FAILED ${step.name} ($problem)")
    }
    return JobResult(passed, log)
}
