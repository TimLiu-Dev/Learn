package katas

enum class Rule { TOO_SHORT, NO_DIGIT, NO_UPPERCASE, HAS_SPACE }

sealed interface PasswordResult {
    data object Ok : PasswordResult
    data class Rejected(val failures: List<Rule>) : PasswordResult
}

fun checkPassword(password: String): PasswordResult {
    val failures = mutableListOf<Rule>()
    if (password.length < 8) failures += Rule.TOO_SHORT
    if (password.none { it.isDigit() }) failures += Rule.NO_DIGIT
    if (password.none { it.isUpperCase() }) failures += Rule.NO_UPPERCASE
    if (' ' in password) failures += Rule.HAS_SPACE
    return if (failures.isEmpty()) PasswordResult.Ok else PasswordResult.Rejected(failures)
}
