// Spring Framework 7.0, following "Error Responses" (RFC 9457). Not run here.
// application.properties:  spring.mvc.problemdetails.enabled=true
@RestControllerAdvice
class ShelfErrors {
    @ExceptionHandler(BookOnLoanException.class)
    ProblemDetail onLoan(BookOnLoanException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
}
