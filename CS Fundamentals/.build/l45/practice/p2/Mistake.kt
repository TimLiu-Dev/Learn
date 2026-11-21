import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val json: Json = Json   // the default writes "returned":null and "errors":null

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

private val PHRASES = mapOf(400 to "Bad Request", 404 to "Not Found", 409 to "Conflict")

fun problem(status: Int, detail: String, errors: List<String>? = null): Response {
    val p = Problem("about:blank", PHRASES.getValue(status), status, detail, errors)
    return Response(status, json.encodeToString(Problem.serializer(), p), "application/problem+json")
}

fun postLoan(body: LoanIn, lend: (bookId: Long, memberId: Long) -> LendResult): Response {
    val errors = buildList {
        if (body.bookId == null) add("bookId is required")
        if (body.memberId == null) add("memberId is required")
    }
    if (errors.isNotEmpty()) return problem(400, "${errors.size} problem(s) with the request", errors)
    val (bookId, memberId) = body.bookId!! to body.memberId!!

    // The service decides what happened; the handler decides how HTTP says it.
    return when (val r = lend(bookId, memberId)) {
        is LendResult.Lent -> Response(201, json.encodeToString(LoanOut.serializer(), r.loan),
                                       headers = mapOf("Location" to "/loans/${r.loan.id}"))
        LendResult.UnknownBook -> problem(404, "No book with id $bookId")
        LendResult.UnknownMember -> problem(404, "No member with id $memberId")
        LendResult.BookOnLoan -> problem(409, "Book $bookId is already on loan")
        LendResult.LimitReached -> problem(409, "Member $memberId already has 3 open loans")
    }
}
