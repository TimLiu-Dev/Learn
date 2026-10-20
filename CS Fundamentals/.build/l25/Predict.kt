@Retention(AnnotationRetention.BINARY) annotation class Internal
@Target(AnnotationTarget.FIELD) annotation class Range(val min: Int, val max: Int)
annotation class Note(val text: String)          // default target and retention

class Signup(
    @Range(1, 120) val age: Int,
    @Internal @Note("hashed") val password: String,
    @get:Note("shown") val nickname: String,
)

fun names(anns: Array<Annotation>) = anns.map { it.annotationClass.simpleName }

fun main() {
    val k = Signup::class.java
    for (f in k.declaredFields.sortedBy { it.name }) println("field ${f.name}: ${names(f.annotations)}")
    println("getNickname(): ${names(k.getMethod("getNickname").annotations)}")
    println("ctor params: ${k.constructors[0].parameterAnnotations.map { names(it) }}")
}
