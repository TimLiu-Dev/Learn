fun main() {
    val lock = Any()
    val sleeper = Thread { Thread.sleep(500) }
    println("before start: ${sleeper.state}")
    sleeper.start()
    Thread.sleep(50)                     // demo only: give the thread time to get there
    println("sleeping:     ${sleeper.state}")

    val joiner = Thread { sleeper.join() }
    joiner.start(); Thread.sleep(50)
    println("joining:      ${joiner.state}")

    val blocked = Thread { synchronized(lock) { } }
    synchronized(lock) {                 // main holds the lock...
        blocked.start(); Thread.sleep(50)
        println("needs lock:   ${blocked.state}")
    }                                    // ...and releases it here

    val spinner = Thread { var x = 0L; while (!Thread.currentThread().isInterrupted) x++ }
    spinner.start(); Thread.sleep(50)
    println("computing:    ${spinner.state}")
    spinner.interrupt()

    for (t in listOf(sleeper, joiner, blocked, spinner)) t.join()
    println("all finished: ${sleeper.state}")
}
