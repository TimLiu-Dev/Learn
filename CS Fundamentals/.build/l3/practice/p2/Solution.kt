fun initials(vararg names: String, suffix: String = "."): String {
    var result = ""
    for (name in names) result += name[0].uppercase() + suffix   // names is an Array<String>
    return result
}

fun clamp(value: Int, min: Int = 0, max: Int = 100): Int =
    if (value < min) min else if (value > max) max else value
