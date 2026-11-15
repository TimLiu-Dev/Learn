class Step(val name: String, val always: Boolean = false, val run: () -> Int)

data class JobResult(val passed: Boolean, val log: List<String>)

/** Runs the steps in order, like one CI job. */
fun runJob(steps: List<Step>): JobResult {
    TODO("run each step, remember whether anything has failed, and log every step")
}
