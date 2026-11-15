import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

// The lesson's disassembler, used to check that assemble undoes it.
fun disassemble(b0: Int, b1: Int): String {
    val rd = (b0 ushr 2) and 3
    val rs = b0 and 3
    val n = "%02X".format(b1)
    return when (b0 ushr 4) {
        HLT -> "HLT"
        LDI -> "LDI R$rd, $n"
        LD -> "LD  R$rd, [$n]"
        ST -> "ST  R$rd, [$n]"
        ADD -> "ADD R$rd, R$rs"
        SUB -> "SUB R$rd, R$rs"
        JMP -> "JMP $n"
        JZ -> "JZ  R$rd, $n"
        else -> "???"
    }
}

class AssemblerTest {
    private fun bytes(line: String) = assemble(line).joinToString(" ") { "%02X".format(it) }

    @Test fun `SUB R2, R3 is 5B, not 5E`() {
        assertEquals("rd goes in bits 2-3, rs in bits 0-1", "5B 00", bytes("SUB R2, R3"))
    }

    @Test fun `numbers and addresses are hex`() {
        assertEquals("18 01", bytes("LDI R2, 01"))
        assertEquals("24 80", bytes("LD R1, [80]"))
        assertEquals("74 0E", bytes("JZ R1, 0E"))
    }

    @Test fun `JMP and HLT have no register`() {
        assertEquals("60 06", bytes("JMP 06"))
        assertEquals("00 00", bytes("HLT"))
    }

    @Test fun `the sum program assembles to the lesson's bytes`() {
        val source = listOf("LDI R0, 00", "LD R1, [80]", "LDI R2, 01", "JZ R1, 0E",
            "ADD R0, R1", "SUB R1, R2", "JMP 06", "ST R0, [81]", "HLT")
        val expected = intArrayOf(0x10, 0x00, 0x24, 0x80, 0x18, 0x01, 0x74, 0x0E, 0x41, 0x00,
            0x56, 0x00, 0x60, 0x06, 0x30, 0x81, 0x00, 0x00)
        assertArrayEquals(expected, source.flatMap { assemble(it).toList() }.toIntArray())
    }

    @Test fun `assemble undoes the disassembler for every instruction`() {
        for (op in 0..7) for (rd in 0..3) for (rs in 0..3) {
            val b0 = when (op) {
                HLT, JMP -> op shl 4                        // no register fields
                ADD, SUB -> (op shl 4) or (rd shl 2) or rs
                else -> (op shl 4) or (rd shl 2)            // rd only
            }
            val b1 = if (op == ADD || op == SUB || op == HLT) 0 else 0xA7
            val text = disassemble(b0, b1)
            assertEquals(text, "%02X %02X".format(b0, b1), bytes(text))
        }
    }

    @Test fun `an unknown mnemonic is rejected`() {
        try {
            assemble("MUL R0, R1")
            fail("MUL should throw IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }
}
