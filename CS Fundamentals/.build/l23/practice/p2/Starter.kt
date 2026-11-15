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
        val def = classes.getValue(name)
        events += "init ${def.name}"
        TODO("once only, superclass first, then the initialiser's own uses")
    }
}
