sealed interface PaymentResult

data class Success(val transactionId: String) : PaymentResult
data class Declined(val reason: String) : PaymentResult
data class Failed(val retryable: Boolean) : PaymentResult
data class Pending(val checkAgainInSeconds: Int) : PaymentResult   // added later

fun describe(result: PaymentResult): String = when (result) {
    is Success -> "paid, id ${result.transactionId}"
    is Declined -> "declined: ${result.reason}"
    else -> "failed"                          // swallows Failed AND the new Pending
}

fun main() {
    println(describe(Pending(30)))            // compiles, and silently lies
}
