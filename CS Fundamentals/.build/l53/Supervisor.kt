// A parent process that stops its child politely, then forcibly
fun main() {
    for (force in listOf(false, true)) {
        val child = ProcessBuilder("java", "-jar", "hook.jar", "30")
            .inheritIO()                      // the child writes straight to our terminal
            .start()
        Thread.sleep(1000)                    // give it time to start and register its hook
        if (force) child.destroyForcibly() else child.destroy()
        println("${if (force) "destroyForcibly()" else "destroy()"}: child exited with ${child.waitFor()}")
    }
}
