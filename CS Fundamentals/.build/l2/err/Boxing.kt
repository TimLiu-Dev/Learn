fun main() {
    val nums = mutableListOf(7)   // List<Int> is fine
    val first: Int = nums[0]
    println(first)

    val maybe: Int? = null        // "?" marks it as nullable
    val n: Int = maybe            // refused at COMPILE time
}
