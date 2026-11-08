package raft

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RaftTest {
    private fun stable(seed: Long) = Sim(seed).apply { start(); runUntil(1_000) }

    // Every 100 ms for 3 s: propose, crash, restart, partition or heal at random. Then heal, restart all, settle.
    private fun chaos(seed: Long): Sim {
        val sim = Sim(seed)
        sim.start()
        var k = 0
        for (t in 100L..3_000L step 100) {
            sim.runUntil(t)
            val victim = sim.rnd.nextInt(sim.n)
            when (sim.rnd.nextInt(5)) {
                0, 1 -> sim.leader()?.let { sim.propose(it.id, "c${k++}") }
                2 -> if (sim.nodes.count { !it.up } < 2 && sim.nodes[victim].up) sim.crash(victim) else if (!sim.nodes[victim].up) sim.restart(victim)
                3 -> sim.partition((0 until sim.n).filter { sim.rnd.nextBoolean() }.toSet())
                else -> sim.heal()
            }
        }
        sim.heal()
        for (node in sim.nodes) if (!node.up) sim.restart(node.id)
        sim.runUntil(3_500)
        sim.leader()?.let { sim.propose(it.id, "final") }        // a current-term entry commits everything before it
        sim.runUntil(5_000)
        return sim
    }

    @Test fun `a healthy cluster elects one leader and everyone agrees on its term`() {
        val sim = stable(1)
        val leader = assertNotNull(sim.leader())
        assertEquals(1, sim.nodes.count { it.role == Role.LEADER })
        assertTrue(sim.nodes.all { it.term == leader.term })
    }

    @Test fun `at most one leader per term, across 300 chaotic seeds`() {
        for (seed in 1L..300L) assertTrue(chaos(seed).electionSafe(), "seed $seed")
    }

    @Test fun `no two nodes ever apply different commands at one index, across 300 chaotic seeds`() {
        for (seed in 1L..300L) assertTrue(chaos(seed).stateMachineSafe(), "seed $seed")
    }

    @Test fun `after the chaos stops, every node ends with the same applied commands`() {
        for (seed in 1L..300L) {
            val sim = chaos(seed)
            val first = sim.nodes[0].applied
            assertTrue(sim.nodes.all { it.applied == first }, "seed $seed: ${sim.nodes.map { it.applied }}")
        }
    }

    @Test fun `a leader cut off in the minority cannot commit`() {
        val sim = stable(7)
        val old = sim.leader()!!
        sim.partition(setOf(old.id, (0 until sim.n).first { it != old.id }))
        val before = old.commitIndex
        assertTrue(sim.propose(old.id, "lost?"))                 // accepted...
        sim.runUntil(2_000)
        assertEquals(before, old.commitIndex)                    // ...but never committed
    }

    @Test fun `the majority side elects a new leader with a higher term`() {
        val sim = stable(7)
        val old = sim.leader()!!
        sim.partition(setOf(old.id, (0 until sim.n).first { it != old.id }))
        sim.runUntil(2_000)
        val fresh = assertNotNull(sim.leader())
        assertTrue(fresh.id != old.id && fresh.term > old.term)
    }

    @Test fun `after healing, the old leader steps down and every log converges`() {
        val sim = scenario(SEED, split = true)
        assertEquals(1, sim.nodes.count { it.role == Role.LEADER })
        for (node in sim.nodes) {
            assertEquals(listOf("x=1", "x=2", "x=4"), node.log.map { it.command }, node.name)
            assertEquals(listOf("x=1", "x=2", "x=4"), node.applied, node.name)
        }
    }

    @Test fun `a voter refuses a candidate whose log is behind its own`() {
        val voter = Sim(1).nodes[0]
        voter.log += Entry(1, "a"); voter.log += Entry(2, "b")
        assertFalse(voter.isUpToDate(lastIndex = 5, lastTerm = 1))   // longer, but an older last term
        assertFalse(voter.isUpToDate(lastIndex = 1, lastTerm = 2))   // same last term, shorter
        assertTrue(voter.isUpToDate(lastIndex = 2, lastTerm = 2))
        assertTrue(voter.isUpToDate(lastIndex = 1, lastTerm = 3))
    }

    @Test fun `one seed always replays the same history`() {
        assertEquals(scenario(3, split = true).history, scenario(3, split = true).history)
    }
}
