// Spring Framework 7.0, following "Error Responses" (RFC 9457). Not run here.
// application.properties:  spring.mvc.problemdetails.enabled=true
@RestControllerAdvice
class ShelfErrors {
    @ExceptionHandler(BookOnLoanException::class)
    fun onLoan(e: BookOnLoanException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message ?: "Book is on loan")
}
