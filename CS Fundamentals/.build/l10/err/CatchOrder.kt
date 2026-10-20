fun main() {
    try {
        "80x".toInt()
    } catch (e: Exception) {                     // general first...
        println("something failed")
    } catch (e: NumberFormatException) {         // ...so this can never run
        println("not a number")
    }
}
