class ClassDef(
    val name: String,
    val superName: String? = null,
    val initUses: List<String> = emptyList(),   // classes its static initialiser touches, in order
)

class MiniJvm(defs: List<ClassDef>) {
    private val classes = defs.associateBy { it.name }
    private val started = mutableSetOf<String>()   // initialisation begun (or finished)
    val events = mutableListOf<String>()

    // An active use: new, getstatic, putstatic or invokestatic (JVMS §5.5).
    fun use(name: String) {
        if (!started.add(name)) return            // already started: done, or in progress (a cycle)
        val def = classes.getValue(name)
        def.superName?.let { use(it) }            // superclass first
        events += "init ${def.name}"              // now <clinit> runs…
        def.initUses.forEach { use(it) }          // …and touches other classes as it goes
    }
}
