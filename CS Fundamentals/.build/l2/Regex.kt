fun main() {
    val text = "Order 66 shipped 3 items for 120 dollars"
    val digits = Regex("""\d+""")              // raw string: no double escaping

    println(digits.find(text)?.value)          // first match
    for (match in digits.findAll(text)) print("${match.value} ")
    println()

    val id = Regex("""ID-\d{4}""")
    println(id.matches("ID-1234"))             // whole string must match
    println(id.matches("ID-12"))
    println("a1b22c333".replace(digits, "#"))

    val date = Regex("""(\d{4})-(\d{2})-(\d{2})""")
    val m = date.matchEntire("2026-10-18")
    println(m?.groupValues)                    // [whole, group 1, group 2, group 3]
}
