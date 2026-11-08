package raft;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RaftTest {
    private Sim stable(long seed) { Sim sim = new Sim(seed); sim.start(); sim.runUntil(1_000); return sim; }

    // Every 100 ms for 3 s: propose, crash, restart, partition or heal at random. Then heal, restart all, settle.
    private Sim chaos(long seed) {
        Sim sim = new Sim(seed);
        sim.start();
        int k = 0;
        for (long t = 100; t <= 3_000; t += 100) {
            sim.runUntil(t);
            int victim = sim.rnd.nextInt(sim.n);
            switch (sim.rnd.nextInt(5)) {
                case 0, 1 -> { Sim.Node l = sim.leader(); if (l != null) sim.propose(l.id, "c" + k++); }
                case 2 -> {
                    long down = sim.nodes.stream().filter(x -> !x.up).count();
                    if (down < 2 && sim.nodes.get(victim).up) sim.crash(victim);
                    else if (!sim.nodes.get(victim).up) sim.restart(victim);
                }
                case 3 -> {
                    Set<Integer> side = new HashSet<>();
                    for (int i = 0; i < sim.n; i++) if (sim.rnd.nextBoolean()) side.add(i);
                    sim.partition(side);
                }
                default -> sim.heal();
            }
        }
        sim.heal();
        for (Sim.Node node : sim.nodes) if (!node.up) sim.restart(node.id);
        sim.runUntil(3_500);
        Sim.Node l = sim.leader();
        if (l != null) sim.propose(l.id, "final");          // a current-term entry commits everything before it
        sim.runUntil(5_000);
        return sim;
    }

    @Test void aHealthyClusterElectsOneLeaderAndEveryoneAgreesOnItsTerm() {
        Sim sim = stable(1);
        Sim.Node leader = sim.leader();
        assertNotNull(leader);
        assertEquals(1, sim.nodes.stream().filter(x -> x.role == Sim.Role.LEADER).count());
        assertTrue(sim.nodes.stream().allMatch(x -> x.term == leader.term));
    }

    @Test void atMostOneLeaderPerTermAcross300ChaoticSeeds() {
        for (long seed = 1; seed <= 300; seed++) assertTrue(chaos(seed).electionSafe(), "seed " + seed);
    }

    @Test void noTwoNodesEverApplyDifferentCommandsAtOneIndex() {
        for (long seed = 1; seed <= 300; seed++) assertTrue(chaos(seed).stateMachineSafe(), "seed " + seed);
    }

    @Test void afterTheChaosStopsEveryNodeEndsWithTheSameAppliedCommands() {
        for (long seed = 1; seed <= 300; seed++) {
            Sim sim = chaos(seed);
            List<String> first = sim.nodes.get(0).applied;
            assertTrue(sim.nodes.stream().allMatch(x -> x.applied.equals(first)), "seed " + seed);
        }
    }

    @Test void aLeaderCutOffInTheMinorityCannotCommit() {
        Sim sim = stable(7);
        Sim.Node old = sim.leader();
        sim.partition(Set.of(old.id, old.id == 0 ? 1 : 0));
        int before = old.commitIndex;
        assertTrue(sim.propose(old.id, "lost?"));             // accepted...
        sim.runUntil(2_000);
        assertEquals(before, old.commitIndex);                // ...but never committed
    }

    @Test void theMajoritySideElectsANewLeaderWithAHigherTerm() {
        Sim sim = stable(7);
        Sim.Node old = sim.leader();
        sim.partition(Set.of(old.id, old.id == 0 ? 1 : 0));
        sim.runUntil(2_000);
        Sim.Node fresh = sim.leader();
        assertNotNull(fresh);
        assertTrue(fresh.id != old.id && fresh.term > old.term);
    }

    @Test void afterHealingTheOldLeaderStepsDownAndEveryLogConverges() {
        Sim sim = App.scenario(App.SEED, true);
        assertEquals(1, sim.nodes.stream().filter(x -> x.role == Sim.Role.LEADER).count());
        for (Sim.Node node : sim.nodes) {
            assertEquals(List.of("x=1", "x=2", "x=4"), node.log.stream().map(Sim.Entry::command).toList(), node.name);
            assertEquals(List.of("x=1", "x=2", "x=4"), node.applied, node.name);
        }
    }

    @Test void aVoterRefusesACandidateWhoseLogIsBehindItsOwn() {
        Sim.Node voter = new Sim(1).nodes.get(0);
        voter.log.add(new Sim.Entry(1, "a")); voter.log.add(new Sim.Entry(2, "b"));
        assertFalse(voter.isUpToDate(5, 1));                  // longer, but an older last term
        assertFalse(voter.isUpToDate(1, 2));                  // same last term, shorter
        assertTrue(voter.isUpToDate(2, 2));
        assertTrue(voter.isUpToDate(1, 3));
    }

    @Test void oneSeedAlwaysReplaysTheSameHistory() {
        assertEquals(App.scenario(3, true).history, App.scenario(3, true).history);
    }
}
