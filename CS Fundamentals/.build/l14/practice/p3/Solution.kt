data class Sale(val day: String, val item: String, val qty: Int)

// groupBy keeps first-seen key order
fun unitsPerItem(sales: List<Sale>): Map<String, Int> =
    sales.groupBy { it.item }.mapValues { (_, group) -> group.sumOf { it.qty } }

fun topItem(sales: List<Sale>): String? =
    unitsPerItem(sales).maxByOrNull { it.value }?.key

fun splitBySize(sales: List<Sale>, min: Int): Pair<List<Sale>, List<Sale>> =
    sales.partition { it.qty >= min }

fun changes(totals: List<Int>): List<Int> = totals.zipWithNext { a, b -> b - a }
