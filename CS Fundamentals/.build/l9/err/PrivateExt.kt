class Account(private val pin: String) {
    fun pinLength() = pin.length
}

fun Account.isStrong(): Boolean = pin.length >= 6       // not allowed

fun Account.isStrongOk(): Boolean = pinLength() >= 6    // public members only
