fun describe(x: Any): String =
    if (x is List<String>) "strings" else "other"   // can't check a type argument

fun <T> make(): T = T()                              // can't construct a T
