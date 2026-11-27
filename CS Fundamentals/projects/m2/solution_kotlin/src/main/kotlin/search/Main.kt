package search

import java.io.IOException
import java.io.UncheckedIOException
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("usage: search <corpus-dir>")
        exitProcess(2)
    }
    val dir = Path(args[0])
    if (!dir.isDirectory()) {
        System.err.println("error: not a directory: ${args[0]}")
        exitProcess(2)
    }
    val index = try {
        Index.load(dir)
    } catch (e: IOException) {
        System.err.println("error: cannot load ${args[0]}: ${e.message}")
        exitProcess(1)
    } catch (e: UncheckedIOException) {
        System.err.println("error: cannot load ${args[0]}: ${e.message}")
        exitProcess(1)
    }

    println("loaded ${index.documentCount} documents, ${index.tokenCount} tokens, ${index.termCount} terms")
    println()
    val commands = Commands(index)
    val input = System.`in`.bufferedReader(Charsets.UTF_8)
    for (line in input.lineSequence()) {
        val command = line.trim()
        if (command.isEmpty()) continue
        if (command == "quit") break
        println(commands.run(command))
        println()
    }
}
