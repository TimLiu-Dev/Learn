// Three annotations, three lifetimes.
@Retention(AnnotationRetention.SOURCE) annotation class Draft      // compiler only
@Retention(AnnotationRetention.BINARY) annotation class Audited    // in the .class file
@Target(AnnotationTarget.FUNCTION)                                 // no @Retention:
annotation class Timed(val unit: String = "ms", val warnAbove: Int = 100) // RUNTIME

class Repo {
    @Draft @Audited @Timed(warnAbove = 50)
    fun save() {}

    @Timed
    fun load() {}
}

fun main() {
    for (name in listOf("save", "load")) {
        val m = Repo::class.java.getDeclaredMethod(name)
        println("$name -> ${m.annotations.toList()}")
    }
    val t = Repo::class.java.getDeclaredMethod("save").getAnnotation(Timed::class.java)
    println("save: unit=${t.unit} warnAbove=${t.warnAbove}")
}
