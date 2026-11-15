data class Sale(val day: String, val item: String, val qty: Int)

fun unitsPerItem(sales: List<Sale>): Map<String, Int> =
    sales.groupBy { it.item }.mapValues { (_, group) -> group.sumOf { it.qty } }

// The biggest single sale, not the biggest total
fun topItem(sales: List<Sale>): String? = sales.maxByOrNull { it.qty }?.item

fun splitBySize(sales: List<Sale>, min: Int): Pair<List<Sale>, List<Sale>> =
    sales.partition { it.qty >= min }

fun changes(totals: List<Int>): List<Int> = totals.zipWithNext { a, b -> b - a }
