// The "framework": it reads annotations through Java reflection. Don't change it.
fun columns(type: Class<*>): Map<String, String> =
    type.declaredFields
        .mapNotNull { f -> f.getAnnotation(Column::class.java)?.let { f.name to it.name } }
        .sortedBy { it.first }
        .toMap()

fun auditedMethods(type: Class<*>): List<String> =
    type.declaredMethods.filter { it.isAnnotationPresent(Audited::class.java) }.map { it.name }.sorted()

// Your part: fix these three declarations.
annotation class Column(val name: String)

@Retention(AnnotationRetention.BINARY)
annotation class Audited

@Retention(AnnotationRetention.RUNTIME)
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
