// The same pipeline started from Kotlin, with no shell in between
fun main() {
    val stages = listOf(
        ProcessBuilder("cut", "-d", " ", "-f3", "requests.log"),
        ProcessBuilder("sort"),
        ProcessBuilder("uniq", "-c"),
        ProcessBuilder("sort", "-rn"),
        ProcessBuilder("head", "-5"),
    )
    val procs = ProcessBuilder.startPipeline(stages)  // stdout of each one feeds stdin of the next
    print(procs.last().inputReader().readText())
    for ((stage, p) in stages.zip(procs)) {
        val shown = stage.command().joinToString(" ") { if (' ' in it) "'$it'" else it }
        println("exit ${p.waitFor()}  $shown")
    }
}
