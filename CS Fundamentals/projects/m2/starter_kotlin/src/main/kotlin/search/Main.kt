package search

import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

/** Given: argument checks and the command loop. Your work starts in Index.load and tokenize. */
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

    val index = Index.load(dir) // TODO (milestone 1)
    println("loaded ${index.documentCount} documents, ${index.tokenCount} tokens, ${index.termCount} terms")
    println()

    val commands = Commands(index)
    for (line in System.`in`.bufferedReader(Charsets.UTF_8).lineSequence()) {
        val command = line.trim()
        if (command.isEmpty()) continue
        if (command == "quit") break
        println(commands.run(command))
        println() // every command's output ends with one empty line
    }
}
