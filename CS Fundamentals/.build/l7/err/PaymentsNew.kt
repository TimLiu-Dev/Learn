sealed interface PaymentResult

data class Success(val transactionId: String) : PaymentResult
data class Declined(val reason: String) : PaymentResult
data class Failed(val retryable: Boolean) : PaymentResult
data class Pending(val checkAgainInSeconds: Int) : PaymentResult

fun describe(result: PaymentResult): String = when (result) {   // no else: the compiler knows every case
    is Success -> "paid, id ${result.transactionId}"             // smart cast to Success
    is Declined -> "declined: ${result.reason}"
    is Failed -> if (result.retryable) "failed, try again" else "failed permanently"
}

fun main() {
    val results = listOf(Success("tx-42"), Declined("insufficient funds"), Failed(retryable = true))
    for (r in results) println(describe(r))
}
