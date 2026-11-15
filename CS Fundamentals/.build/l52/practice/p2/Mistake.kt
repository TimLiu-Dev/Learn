const val HLT = 0x0
const val LDI = 0x1
const val LD = 0x2
const val ST = 0x3
const val ADD = 0x4
const val SUB = 0x5
const val JMP = 0x6
const val JZ = 0x7

// "LD  R1, [80]" -> [LD, R1, 80]
fun tokens(line: String): List<String> = line.trim().split(Regex("[\\s,\\[\\]]+")).filter { it.isNotEmpty() }

fun reg(t: String): Int = t.removePrefix("R").toInt()   // "R2" -> 2
fun num(t: String): Int = t.toInt(16)                  // "0E" -> 14

fun encode(op: Int, rd: Int = 0, rs: Int = 0, b1: Int = 0) =
    intArrayOf((op shl 4) or (rs shl 2) or rd, b1)     // the mistake: rs and rd swap places

// One line of toy assembly -> its two bytes.
fun assemble(line: String): IntArray {
    val t = tokens(line)
    return when (t[0]) {
        "HLT" -> encode(HLT)
        "LDI" -> encode(LDI, rd = reg(t[1]), b1 = num(t[2]))
        "LD" -> encode(LD, rd = reg(t[1]), b1 = num(t[2]))
        "ST" -> encode(ST, rd = reg(t[1]), b1 = num(t[2]))
        "ADD" -> encode(ADD, rd = reg(t[1]), rs = reg(t[2]))
        "SUB" -> encode(SUB, rd = reg(t[1]), rs = reg(t[2]))
        "JMP" -> encode(JMP, b1 = num(t[1]))
        "JZ" -> encode(JZ, rd = reg(t[1]), b1 = num(t[2]))
        else -> throw IllegalArgumentException("unknown mnemonic ${t[0]}")
    }
}
