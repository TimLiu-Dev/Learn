package lending.domain;

public sealed interface ReturnResult {
    record Returned(long feeCents) implements ReturnResult {}
    record NotBorrowed() implements ReturnResult {}
}
