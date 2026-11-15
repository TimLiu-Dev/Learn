fun main() {
    var word = "cat"
    val viaRef = word::length
    val viaLambda = { word.length }
    word = "horse"
    println("${viaRef()} ${viaLambda()}")
}
