// A named single-method type: `fun interface` allows lambdas (SAM conversion)
fun interface Rule {
    fun check(password: String): Boolean
}

val longEnough = Rule { it.length >= 8 }          // lambda becomes a Rule
val hasDigit = Rule { pw -> pw.any { it.isDigit() } }

fun passesAll(pw: String, rules: List<Rule>) = rules.all { it.check(pw) }

fun main() {
    val rules = listOf(longEnough, hasDigit)
    println(passesAll("hunter2", rules))
    println(passesAll("correct7horse", rules))
}
