package raft

// The final's scripted run: commit two writes, split the cluster, write on both sides, heal.
fun scenario(seed: Long, split: Boolean, sim: Sim = Sim(seed)): Sim {
    sim.start()
    sim.runUntil(400)
    val first = sim.leader()!!.id
    sim.propose(first, "x=1")
    sim.runUntil(450)
    sim.propose(first, "x=2")
    sim.runUntil(700)
    if (split) sim.partition(setOf(first, (0 until sim.n).first { it != first }))
    sim.runUntil(750)
    sim.propose(first, "x=3")                     // the client still believes 'first' is the leader
    sim.runUntil(1_300)
    sim.propose(sim.leader()!!.id, "x=4")
    sim.runUntil(1_800)
    if (split) sim.heal()
    sim.runUntil(2_300)
    return sim
}

fun printRun(sim: Sim) {
    for (e in sim.history) println("%6d ms  %s".format(e.at, e.text))
    println()
    println("%-4s %-4s %-9s %-24s %-6s %s".format("node", "term", "role", "log (term:command)", "commit", "applied"))
    for (node in sim.nodes) {
        println("%-4s %-4d %-9s %-24s %-6d %s".format(node.name, node.term, node.role,
            node.log.joinToString(" ") { "${it.term}:${it.command}" }, node.commitIndex, node.applied.joinToString(", ")))
    }
    println("election safety: ${sim.electionSafe()}, state machine safety: ${sim.stateMachineSafe()}")
}

// One frame per event, for the lesson's widget.
fun frames(seed: Long, split: Boolean): String {
    val out = StringBuilder()
    val sim = Sim(seed)
    sim.onEvent = { e ->
        if (out.isNotEmpty()) out.append(",\n")
        out.append("{\"t\":${e.at},\"note\":\"${e.text.replace("\"", "\\\"")}\"")
        if (e.from != null) out.append(",\"from\":${e.from},\"to\":${e.to}")
        sim.side?.let { s -> out.append(",\"side\":[${s.sorted().joinToString(",")}]") }
        out.append(",\"nodes\":[")
        out.append(sim.nodes.joinToString(",") { nd ->
            "{\"role\":\"${nd.role}\",\"term\":${nd.term},\"up\":${nd.up},\"commit\":${nd.commitIndex}," +
                "\"log\":[${nd.log.joinToString(",") { "[${it.term},\"${it.command}\"]" }}]}"
        })
        out.append("]}")
    }
    scenario(seed, split, sim)
    return "[\n$out\n]"
}

const val SEED = 7L

fun main(args: Array<String>) {
    if (args.firstOrNull() == "--json") {
        println("{\"healthy\":${frames(SEED, false)},\n\"split\":${frames(SEED, true)}}")
        return
    }
    println("== Seed $SEED: two writes, a partition at 700 ms, a write on each side, heal at 1,800 ms")
    printRun(scenario(SEED, split = true))
}
