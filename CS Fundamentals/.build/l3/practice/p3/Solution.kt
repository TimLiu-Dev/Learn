fun parseAge(text: String?): Int? {
    val n = text?.toIntOrNull() ?: return null    // null text or not a number: stop here
    return if (n in 0..150) n else null           // n is a plain Int: ?: already handled null
}

fun bossOfBoss(name: String, bosses: Map<String, String>): String? =
    bosses[name]?.let { boss -> bosses[boss] }
