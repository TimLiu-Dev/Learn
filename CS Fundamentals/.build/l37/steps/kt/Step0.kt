package pricing

// calculates price
fun price(items: List<Pair<Int, Int>>, coupon: String?, member: Boolean): Int {
    var t = 0
    for (i in items) {
        if (i.first <= 0) continue
        t += i.first * i.second
    }
    if (member) {
        if (t > 5000) {
            t = t - t * 10 / 100
        } else {
            t = t - t * 5 / 100
        }
    }
    if (coupon != null) {
        if (coupon == "SAVE10") {
            var d = t * 10 / 100
            if (d > 2000) d = 2000
            t = t - d
        } else if (coupon == "SAVE20") {
            var d = t * 20 / 100
            if (d > 2000) d = 2000
            t = t - d
        } else if (coupon.startsWith("FLAT")) {
            val d = coupon.substring(4).toIntOrNull() ?: 0
            t = t - d * 100
            if (t < 0) t = 0
        }
    }
    if (t < 3500) {
        if (!member) t = t + 499
    }
    return t
}
