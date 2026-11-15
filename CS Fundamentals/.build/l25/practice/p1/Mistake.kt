fun columns(type: Class<*>): Map<String, String> =
    type.declaredFields
        .mapNotNull { f -> f.getAnnotation(Column::class.java)?.let { f.name to it.name } }
        .sortedBy { it.first }
        .toMap()

fun auditedMethods(type: Class<*>): List<String> =
    type.declaredMethods.filter { it.isAnnotationPresent(Audited::class.java) }.map { it.name }.sorted()

// Mistake: fix each use site with @field: instead of restricting the annotation.
annotation class Column(val name: String)

annotation class Audited

@Retention(AnnotationRetention.SOURCE)
annotation class Secret

class Account(
    @field:Column("user_email") val email: String,
    @field:Column("display_name") val name: String,
    @Secret val password: String,
) {
    @Audited fun save() {}
    @Audited fun delete() {}
    fun refresh() {}
}
