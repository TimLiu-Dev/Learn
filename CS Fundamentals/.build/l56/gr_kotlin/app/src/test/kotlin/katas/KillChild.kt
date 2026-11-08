package katas

import java.nio.file.Path

// Run as its own process by the kill -9 test: write forever, acknowledging each put on stdout.
fun main(args: Array<String>) {
    LogStore.open(Path.of(args[0]), Sync.NEVER).use { db ->
        var i = 0
        while (true) {
            db.put("k$i", "v$i")
            println("acked $i")                 // printed only after put() returned
            System.out.flush()
            i++
        }
    }
}
