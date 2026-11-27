package chat

import java.io.IOException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val server = try {
        val options = parseOptions(args)
        ChatServer(options, UserStore.load(options.usersFile))
    } catch (e: UsageError) {
        System.err.println("error: ${e.message}")
        System.err.println(USAGE)
        exitProcess(2)
    } catch (e: IOException) {
        System.err.println("error: cannot listen: ${e.message}")
        exitProcess(2)
    }
    // SIGTERM (and Ctrl+C) run shutdown hooks; the JVM then exits with 128 + 15 = 143.
    Runtime.getRuntime().addShutdownHook(Thread(server::shutdown))
    server.start()
    println("ready tcp=${server.tcpPort} http=${server.httpPort}")
    System.out.flush()
}
