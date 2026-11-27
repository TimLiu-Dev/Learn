package lending.domain;

public sealed interface LendResult {
    record Lent(Loan loan) implements LendResult {}
    record Blocked(String reason) implements LendResult {}
    record CopyOnLoan() implements LendResult {}
    record NotLendable() implements LendResult {}
    record UnknownCopy() implements LendResult {}
}
