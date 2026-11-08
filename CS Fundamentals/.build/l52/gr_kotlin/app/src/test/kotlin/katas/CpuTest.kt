package katas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CpuTest {
    private fun runOne(vararg program: Int): Cpu {
        val cpu = Cpu(program)
        cpu.run()
        return cpu
    }

    @Test fun `LDI loads the byte after the opcode`() {
        val cpu = runOne(0x14, 0x2A, 0x00, 0x00)              // LDI R1, 2A; HLT
        assertEquals(0x2A, cpu.reg[1])
    }

    @Test fun `ADD and SUB wrap around at 256`() {
        val cpu = runOne(0x10, 200, 0x14, 100, 0x41, 0x00,   // R0 = 200 + 100
                         0x18, 3, 0x1C, 5, 0x5B, 0x00,       // R2 = 3 - 5
                         0x00, 0x00)
        assertEquals(44, cpu.reg[0])
        assertEquals(254, cpu.reg[2])
    }

    @Test fun `JZ jumps only when the register is zero`() {
        // R1 = 0, so JZ skips the LDI R0, 63 and lands on HLT
        val jumped = runOne(0x14, 0x00, 0x74, 0x08, 0x10, 0x63, 0x00, 0x00, 0x00, 0x00)
        assertEquals(0, jumped.reg[0])
        val fellThrough = runOne(0x14, 0x01, 0x74, 0x08, 0x10, 0x63, 0x00, 0x00, 0x00, 0x00)
        assertEquals(0x63, fellThrough.reg[0])
    }

    @Test fun `ST then LD round-trips through memory`() {
        val cpu = runOne(0x10, 0x5A, 0x30, 0x90, 0x28, 0x90, 0x00, 0x00)   // R0 = 5A; mem[90] = R0; R2 = mem[90]
        assertEquals(0x5A, cpu.mem[0x90])
        assertEquals(0x5A, cpu.reg[2])
    }

    @Test fun `HLT stops the CPU`() {
        val cpu = runOne(0x00, 0x00)
        assertTrue(cpu.halted)
        assertEquals(1, cpu.steps)
        assertFailsWith<IllegalStateException> { cpu.step() }
    }

    @Test fun `unknown opcode is rejected with its address`() {
        val e = assertFailsWith<IllegalStateException> { runOne(0x10, 0x00, 0xF0, 0x00) }
        assertEquals("unknown opcode 15 at address 2", e.message)
    }

    @Test fun `a program with no HLT hits the step budget`() {
        val e = assertFailsWith<IllegalStateException> { Cpu(intArrayOf(0x60, 0x00)).run(maxSteps = 1_000) }  // JMP 00
        assertEquals("no HLT after 1000 steps (pc=0)", e.message)
    }

    @Test fun `multiply matches a times b for all 65,536 pairs`() {
        for (a in 0..255) for (b in 0..255) {
            assertEquals((a * b) and 0xFF, multiply(a, b).mem[0x82], "$a * $b")
        }
    }

    @Test fun `multiply takes 4b + 7 instructions`() {
        for (b in 0..255) assertEquals(4 * b + 7, multiply(3, b).steps, "b = $b")
    }
}
