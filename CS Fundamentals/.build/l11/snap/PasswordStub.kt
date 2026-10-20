package katas

enum class Rule { TOO_SHORT, NO_DIGIT, NO_UPPERCASE, HAS_SPACE }

sealed interface PasswordResult {
    data object Ok : PasswordResult
    data class Rejected(val failures: List<Rule>) : PasswordResult
}

fun checkPassword(password: String): PasswordResult {
    return PasswordResult.Ok        // stub: compiles, so the first test can go red
}
