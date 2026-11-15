class ClassDef(
    val name: String,
    val superName: String? = null,
    val initUses: List<String> = emptyList(),
)

class MiniJvm(defs: List<ClassDef>) {
    private val classes = defs.associateBy { it.name }
    private val started = mutableSetOf<String>()
    val events = mutableListOf<String>()

    fun use(name: String) {
        if (name in started) return
        val def = classes.getValue(name)
        def.superName?.let { use(it) }
        events += "init ${def.name}"
        def.initUses.forEach { use(it) }
        started += name                           // marked only when finished: a cycle never ends
    }
}
