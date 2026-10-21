fun run(vararg cmd: String): String =
    ProcessBuilder(*cmd).redirectErrorStream(true).start().run {
        val out = inputStream.bufferedReader().readText().trim()
        waitFor()
        out
    }

fun main() {
    val name = "Ada; echo INJECTED"  // pretend this came from a web form

    // VULNERABLE: a shell parses the whole string, so ';' starts a second command
    println("shell:   " + run("sh", "-c", "echo Hello, $name").replace("\n", " | "))

    // FIXED: no shell; the input is one argument, whatever it contains
    println("argList: " + run("echo", "Hello,", name))
}
