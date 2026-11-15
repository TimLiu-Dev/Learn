package app

import core.Version

fun main(args: Array<String>) {
    println("highest as strings:  ${args.max()}")
    println("highest as versions: ${args.map(Version::parse).max()}")
}
