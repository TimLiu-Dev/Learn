// Targets like a typical Java annotation (think Jakarta's @Email): no PROPERTY.
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.VALUE_PARAMETER)
annotation class Tag(val v: String)

// One Kotlin property = a constructor parameter + a field + a getter.
class User(
    @field:Tag("field") val a: String,
    @get:Tag("get") val b: String,
    @param:Tag("param") val c: String,
    @Tag("none") val d: String,          // no use-site target: the compiler picks
    @all:Tag("all") val e: String,       // every applicable target
)

fun tags(anns: Array<Annotation>) = anns.map { (it as Tag).v }

fun main() {
    val k = User::class.java
    val params = k.constructors[0].parameterAnnotations
    for ((i, n) in listOf("a", "b", "c", "d", "e").withIndex()) {
        val field = tags(k.getDeclaredField(n).annotations)
        val getter = tags(k.getDeclaredMethod("get" + n.uppercase()).annotations)
        println("$n: param=${tags(params[i])} field=$field getter=$getter")
    }
}
