// The "framework": it reads annotations through Java reflection. Don't change it.
fun columns(type: Class<*>): Map<String, String> =
    type.declaredFields
        .mapNotNull { f -> f.getAnnotation(Column::class.java)?.let { f.name to it.name } }
        .sortedBy { it.first }
        .toMap()

fun auditedMethods(type: Class<*>): List<String> =
    type.declaredMethods.filter { it.isAnnotationPresent(Audited::class.java) }.map { it.name }.sorted()

// FIELD is the only target, so the default use-site rule has one place to put it.
@Target(AnnotationTarget.FIELD)
annotation class Column(val name: String)

// No @Retention: Kotlin's default is RUNTIME, visible to reflection.
annotation class Audited

@Retention(AnnotationRetention.SOURCE)
annotation class Secret

class Account(
    @Column("user_email") val email: String,
    @Column("display_name") val name: String,
    @Secret val password: String,
) {
    @Audited fun save() {}
    @Audited fun delete() {}
    fun refresh() {}
}
