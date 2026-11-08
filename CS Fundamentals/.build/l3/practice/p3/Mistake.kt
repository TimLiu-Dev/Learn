fun parseAge(text: String?): Int? {
    val n = text?.toIntOrNull() ?: return null
    return if (n in 0..150) n else null
}

fun bossOfBoss(name: String, bosses: Map<String, String>): String? =
    bosses[bosses[name]!!]
