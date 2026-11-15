fun main() {
    val stock = hashMapOf<String, Int?>("tea" to null)
    val a = stock["tea"]
    val b = stock.getOrDefault("tea", 0)
    val c = stock.getOrElse("tea") { 0 }
    println("$a $b $c")
}
