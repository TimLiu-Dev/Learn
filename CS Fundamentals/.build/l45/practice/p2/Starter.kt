import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val json: Json = Json   // configure me: responses leave null fields out

@Serializable
data class Problem(val type: String, val title: String, val status: Int, val detail: String, val errors: List<String>?)

@Serializable
data class LoanIn(val bookId: Long? = null, val memberId: Long? = null)

@Serializable
data class LoanOut(val id: Long, val bookId: Long, val memberId: Long, val loaned: String, val returned: String?)

data class Response(val status: Int, val body: String, val type: String = "application/json", val headers: Map<String, String> = emptyMap())

sealed interface LendResult {
    data class Lent(val loan: LoanOut) : LendResult
    data object UnknownBook : LendResult
    data object UnknownMember : LendResult
    data object BookOnLoan : LendResult
    data object LimitReached : LendResult
}

fun problem(status: Int, detail: String, errors: List<String>? = null): Response =
    TODO("encode a Problem with json.encodeToString(Problem.serializer(), …)")

fun postLoan(body: LoanIn, lend: (bookId: Long, memberId: Long) -> LendResult): Response =
    TODO("validate, then map every LendResult with an exhaustive when")
