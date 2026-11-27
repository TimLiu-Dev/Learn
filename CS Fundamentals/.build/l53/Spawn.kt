import java.io.File

fun main() {
    val pb = ProcessBuilder("sh", "-c", "echo \"child pid \$\$, parent \$PPID, cwd \$(pwd), GREETING=\$GREETING\"")
    pb.directory(File("/tmp"))                          // the child's working directory
    pb.environment()["GREETING"] = "from the parent"    // the child's copy of the environment
    pb.inheritIO()                                      // the child's fds 0, 1 and 2 are ours
    println("parent pid ${ProcessHandle.current().pid()}")
    println("child exit ${pb.start().waitFor()}")
}
