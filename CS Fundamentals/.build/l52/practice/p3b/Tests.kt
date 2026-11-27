import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class StackTest {
    // Runs until HLT, failing instead of hanging if there's no HLT within 1,000 steps.
    private fun run(vararg program: Int): Cpu {
        val cpu = Cpu(program)
        repeat(1000) { if (!cpu.halted) cpu.step() }
        check(cpu.halted) { "no HLT after 1000 steps (pc=${hex(cpu.pc)})" }
        return cpu
    }

    @Test fun `CALL pushes the return address and jumps`() {
        val cpu = Cpu(intArrayOf(0x80, 0x04))                        // 00  CALL 04
        cpu.step()
        assertEquals("pc", 0x04, cpu.pc)
        assertEquals("sp", 0xFF, cpu.sp)
        assertEquals("the return address on the stack", 0x02, cpu.mem[0xFF])
    }

    @Test fun `RET resumes after the CALL`() {
        val cpu = run(0x80, 0x06, 0x10, 0x07, 0x00, 0x00, 0x90, 0x00)   // CALL 06; LDI R0, 07; HLT; RET
        assertEquals("R0", 0x07, cpu.reg[0])
        assertEquals("sp is back to empty", 0x00, cpu.sp)
    }

    @Test fun `nested calls return in reverse order`() {
        val quad = intArrayOf(0x20, 0x80, 0x80, 0x08, 0x30, 0x81, 0x00, 0x00,
                              0x80, 0x0E, 0x80, 0x0E, 0x90, 0x00, 0x40, 0x00, 0x90, 0x00).copyOf(0x81)
        quad[0x80] = 3
        val cpu = run(*quad)
        assertEquals("quad(3)", 12, cpu.mem[0x81])
        assertEquals("sp is back to empty", 0x00, cpu.sp)
    }

    @Test fun `runaway recursion overflows after 16 calls`() {
        val e = assertThrows(IllegalStateException::class.java) { run(0x80, 0x00) }   // 00  CALL 00
        assertEquals("stack overflow at 00: SP=F0", e.message)
    }

    @Test fun `RET on an empty stack is an underflow`() {
        val e = assertThrows(IllegalStateException::class.java) { run(0x90, 0x00) }
        assertEquals("stack underflow at 00", e.message)
    }
}
