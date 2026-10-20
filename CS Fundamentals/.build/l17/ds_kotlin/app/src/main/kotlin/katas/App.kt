package katas

fun main() {
    for (s in listOf("{[()]}", "([)]")) println("$s balanced? ${isBalanced(s)}")
}
