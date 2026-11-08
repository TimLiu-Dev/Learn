fun initials(vararg names: String, suffix: String = "."): String =
    names.joinToString(suffix) { it[0].uppercase() }

fun clamp(value: Int, min: Int = 0, max: Int = 100): Int =
    if (value < min) min else if (value > max) max else value
