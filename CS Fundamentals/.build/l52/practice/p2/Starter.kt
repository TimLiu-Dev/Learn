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

// One line of toy assembly -> its two bytes.
fun assemble(line: String): IntArray = TODO("look up the opcode, read the registers and the hex number, then pack the fields")
