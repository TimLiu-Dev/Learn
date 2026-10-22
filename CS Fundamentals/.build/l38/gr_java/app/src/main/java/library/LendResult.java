package library;

public sealed interface LendResult {
    record Lent(Loan loan) implements LendResult {}
    record Blocked(String reason) implements LendResult {}
    record BookOnLoan() implements LendResult {}
    record UnknownMember() implements LendResult {}
}
