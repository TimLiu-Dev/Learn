data class Sale(val day: String, val item: String, val qty: Int)

fun unitsPerItem(sales: List<Sale>): Map<String, Int> = TODO("group by item, then sum each group")

fun topItem(sales: List<Sale>): String? = TODO("reuse unitsPerItem")

fun splitBySize(sales: List<Sale>, min: Int): Pair<List<Sale>, List<Sale>> = TODO("one call splits a list in two")

fun changes(totals: List<Int>): List<Int> = TODO("pair each element with the next one")
