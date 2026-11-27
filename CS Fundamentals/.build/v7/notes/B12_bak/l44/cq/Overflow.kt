fun totalLinks(perMonth: Int, years: Int) = perMonth * 12 * years

fun main() {
    println(totalLinks(100_000_000, 5))
}
