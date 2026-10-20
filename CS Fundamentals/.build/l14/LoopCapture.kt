fun main() {
    val fromFor = mutableListOf<() -> Int>()
    for (i in 1..3) {
        fromFor.add { i }          // each iteration has its own `i`
    }
    println(fromFor.map { it() })

    val fromVar = mutableListOf<() -> Int>()
    var j = 1
    while (j <= 3) {
        fromVar.add { j }          // all three capture the SAME var
        j++
    }
    println(fromVar.map { it() })
}
