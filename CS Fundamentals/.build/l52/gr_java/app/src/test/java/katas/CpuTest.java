package katas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CpuTest {
    private static Cpu runOne(int... program) {
        Cpu cpu = new Cpu(program);
        cpu.run();
        return cpu;
    }

    @Test void ldiLoadsTheByteAfterTheOpcode() {
        Cpu cpu = runOne(0x14, 0x2A, 0x00, 0x00);              // LDI R1, 2A; HLT
        assertEquals(0x2A, cpu.reg[1]);
    }

    @Test void addAndSubWrapAroundAt256() {
        Cpu cpu = runOne(0x10, 200, 0x14, 100, 0x41, 0x00,     // R0 = 200 + 100
                         0x18, 3, 0x1C, 5, 0x5B, 0x00,         // R2 = 3 - 5
                         0x00, 0x00);
        assertEquals(44, cpu.reg[0]);
        assertEquals(254, cpu.reg[2]);
    }

    @Test void jzJumpsOnlyWhenTheRegisterIsZero() {
        // R1 = 0, so JZ skips the LDI R0, 63 and lands on HLT
        Cpu jumped = runOne(0x14, 0x00, 0x74, 0x08, 0x10, 0x63, 0x00, 0x00, 0x00, 0x00);
        assertEquals(0, jumped.reg[0]);
        Cpu fellThrough = runOne(0x14, 0x01, 0x74, 0x08, 0x10, 0x63, 0x00, 0x00, 0x00, 0x00);
        assertEquals(0x63, fellThrough.reg[0]);
    }

    @Test void stThenLdRoundTripsThroughMemory() {
        Cpu cpu = runOne(0x10, 0x5A, 0x30, 0x90, 0x28, 0x90, 0x00, 0x00);   // R0 = 5A; mem[90] = R0; R2 = mem[90]
        assertEquals(0x5A, cpu.mem[0x90]);
        assertEquals(0x5A, cpu.reg[2]);
    }

    @Test void hltStopsTheCpu() {
        Cpu cpu = runOne(0x00, 0x00);
        assertTrue(cpu.halted());
        assertEquals(1, cpu.steps());
        assertThrows(IllegalStateException.class, cpu::step);
    }

    @Test void unknownOpcodeIsRejectedWithItsAddress() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> runOne(0x10, 0x00, 0xF0, 0x00));
        assertEquals("unknown opcode 15 at address 2", e.getMessage());
    }

    @Test void aProgramWithNoHltHitsTheStepBudget() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> new Cpu(new int[]{0x60, 0x00}).run(1_000));   // JMP 00
        assertEquals("no HLT after 1000 steps (pc=0)", e.getMessage());
    }

    @Test void multiplyMatchesATimesBForAll65536Pairs() {
        for (int a = 0; a <= 255; a++)
            for (int b = 0; b <= 255; b++)
                assertEquals((a * b) & 0xFF, Programs.multiply(a, b).mem[0x82], a + " * " + b);
    }

    @Test void multiplyTakes4bPlus7Instructions() {
        for (int b = 0; b <= 255; b++) assertEquals(4 * b + 7, Programs.multiply(3, b).steps(), "b = " + b);
    }
}
