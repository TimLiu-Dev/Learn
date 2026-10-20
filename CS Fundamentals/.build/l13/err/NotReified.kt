fun <T> List<Any>.only(): List<T> {               // no inline, no reified
    val out = mutableListOf<T>()
    for (x in this) if (x is T) out.add(x)
    return out
}
