package pricing

fun price(items: List<Pair<Int, Int>>, coupon: String?, member: Boolean): Int {
    var totalCents = 0
    for ((quantity, unitCents) in items) {
        if (quantity <= 0) continue
        totalCents += quantity * unitCents
    }
    if (member) {
        if (totalCents > 5000) {
            totalCents = totalCents - totalCents * 10 / 100
        } else {
            totalCents = totalCents - totalCents * 5 / 100
        }
    }
    if (coupon != null) {
        if (coupon == "SAVE10") {
            var discount = totalCents * 10 / 100
            if (discount > 2000) discount = 2000
            totalCents = totalCents - discount
        } else if (coupon == "SAVE20") {
            var discount = totalCents * 20 / 100
            if (discount > 2000) discount = 2000
            totalCents = totalCents - discount
        } else if (coupon.startsWith("FLAT")) {
            val dollars = coupon.substring(4).toIntOrNull() ?: 0
            totalCents = totalCents - dollars * 100
            if (totalCents < 0) totalCents = 0
        }
    }
    if (totalCents < 3500) {
        if (!member) totalCents = totalCents + 499
    }
    return totalCents
}
