// Calc's back end, part 2: a fetch-decode-execute loop over the bytecode, plus a disassembler.
fun disassemble(c: Chunk): String = buildString {
    appendLine("stack=${c.maxStack}, locals=${c.locals.size} ${c.locals}")
    var pc = 0
    while (pc < c.code.size) {
        val op = c.code[pc]
        append("%3d: %s".format(pc, Op.names[op]))
        if (Op.hasOperand(op)) {
            val arg = c.code[pc + 1]
            append(" $arg")
            if (op != Op.PUSH) append("   // ${c.locals[arg]}")
            pc += 2
        } else pc += 1
        appendLine()
    }
}

fun execute(c: Chunk): Int {
    val code = c.code
    val stack = IntArray(c.maxStack)                    // the compiler worked out how deep it gets
    val locals = IntArray(c.locals.size)
    var sp = 0
    var pc = 0
    while (true) {
        val at = pc
        when (code[pc++]) {                             // fetch, decode ...
            Op.PUSH -> stack[sp++] = code[pc++]         // ... execute
            Op.LOAD -> stack[sp++] = locals[code[pc++]]
            Op.STORE -> locals[code[pc++]] = stack[--sp]
            Op.NEG -> stack[sp - 1] = -stack[sp - 1]
            Op.ADD -> { sp--; stack[sp - 1] += stack[sp] }
            Op.SUB -> { sp--; stack[sp - 1] -= stack[sp] }
            Op.MUL -> { sp--; stack[sp - 1] *= stack[sp] }
            Op.DIV -> { sp--; stack[sp - 1] = arith(T.SLASH, stack[sp - 1], stack[sp], c.where[at]) }
            Op.MOD -> { sp--; stack[sp - 1] = arith(T.PERCENT, stack[sp - 1], stack[sp], c.where[at]) }
            Op.POW -> { sp--; stack[sp - 1] = arith(T.CARET, stack[sp - 1], stack[sp], c.where[at]) }
            Op.RETURN -> return stack[--sp]
            else -> error("bad opcode ${code[at]} at $at")
        }
    }
}
