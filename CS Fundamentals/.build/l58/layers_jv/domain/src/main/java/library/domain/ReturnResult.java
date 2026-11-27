package library.domain;

public sealed interface ReturnResult {
    record Returned(long feeCents) implements ReturnResult {}
    record AlreadyReturned() implements ReturnResult {}
    record UnknownLoan() implements ReturnResult {}
}
