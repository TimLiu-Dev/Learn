@Target(AnnotationTarget.FIELD)
annotation class Range(val min: Int, val max: Int)

@Target(AnnotationTarget.FIELD)
annotation class NotBlank

class Signup(
    @Range(13, 120) val age: Int,
    @NotBlank val name: String,
    @Range(0, 10) val referrals: Int,
)

// Reads the annotations at runtime: no if-statements about Signup anywhere.
fun validate(obj: Any): List<String> {
    val problems = mutableListOf<String>()
    for (f in obj.javaClass.declaredFields.sortedBy { it.name }) {
        f.isAccessible = true
        f.getAnnotation(Range::class.java)?.let { r ->
            val v = f.getInt(obj)
            if (v !in r.min..r.max) problems += "${f.name}=$v is not in ${r.min}..${r.max}"
        }
        if (f.isAnnotationPresent(NotBlank::class.java) && (f.get(obj) as String).isBlank()) {
            problems += "${f.name} must not be blank"
        }
    }
    return problems
}

fun main() {
    println(validate(Signup(30, "Ada", 2)))
    println(validate(Signup(9, " ", 11)))
}
