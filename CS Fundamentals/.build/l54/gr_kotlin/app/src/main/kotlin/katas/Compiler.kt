package katas

// Calc's back end, part 1: compile the tree to stack bytecode.
object Op {
    const val PUSH = 0; const val LOAD = 1; const val STORE = 2; const val NEG = 3
    const val ADD = 4; const val SUB = 5; const val MUL = 6; const val DIV = 7
    const val MOD = 8; const val POW = 9; const val RETURN = 10
    val names = listOf("push", "load", "store", "neg", "add", "sub", "mul", "div", "mod", "pow", "return")
    fun hasOperand(op: Int) = op <= STORE
}

class Chunk(val code: IntArray, val where: Array<String>, val maxStack: Int, val locals: List<String>)

class Compiler {
    private val code = mutableListOf<Int>()
    private val where = mutableListOf<String>()        // source position of each instruction, for errors
    private val slots = LinkedHashMap<String, Int>()    // variable name -> local slot, like javac's locals
    private var depth = 0
    private var maxStack = 0

    fun compile(p: Program): Chunk {
        for (let in p.lets) {
            expr(let.value)
            emit(Op.STORE, "", slots.getOrPut(let.name) { slots.size }, delta = -1)
        }
        expr(p.result)
        emit(Op.RETURN, "", delta = -1)
        return Chunk(code.toIntArray(), where.toTypedArray(), maxStack, slots.keys.toList())
    }

    private fun expr(e: Expr) {
        when (e) {
            is Num -> emit(Op.PUSH, "", e.value, delta = +1)
            is Var -> {
                val slot = slots[e.name] ?: throw CalcError("undefined variable '${e.name}' at ${e.at.at}")
                emit(Op.LOAD, "", slot, delta = +1)
            }
            is Neg -> { expr(e.operand); emit(Op.NEG, e.at.at, delta = 0) }
            is Bin -> {
                expr(e.left)                            // post-order, just like eval:
                expr(e.right)                           // both operands end up on the stack,
                val op = when (e.op) {                  // then one instruction combines them
                    T.PLUS -> Op.ADD; T.MINUS -> Op.SUB; T.STAR -> Op.MUL
                    T.SLASH -> Op.DIV; T.PERCENT -> Op.MOD; T.CARET -> Op.POW
                    else -> error("not an operator: ${e.op}")
                }
                emit(op, e.at.at, delta = -1)
            }
        }
    }

    private fun emit(op: Int, at: String, operand: Int? = null, delta: Int) {
        code += op; where += at
        if (operand != null) { code += operand; where += at }
        depth += delta
        maxStack = maxOf(maxStack, depth)
    }
}
