fun main() {
    val mem = IntArray(256)
    var sp = 0x00
    fun push(ret: Int) { sp = (sp - 1) and 0xFF; mem[sp] = ret }   // what CALL does
    fun pop(): Int { val r = mem[sp]; sp = (sp + 1) and 0xFF; return r }   // what RET does
    push(0x04); push(0x0A)
    val a = pop()
    push(0x0C)
    println("%02X %02X %02X".format(a, pop(), pop()))
}
