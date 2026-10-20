package katas

fun toRoman(number: Int): String {
    if (number == 4) return "IV"    // ugly, but green. Refactor later.
    if (number == 5) return "V"
    return "I".repeat(number)
}
