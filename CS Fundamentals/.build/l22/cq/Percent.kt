fun heapA(limitMb: Int, percent: Int) = limitMb * (percent / 100)
fun heapB(limitMb: Int, percent: Int) = limitMb * percent / 100

fun main() {
    println("${heapA(2048, 75)} ${heapB(2048, 75)}")
}
