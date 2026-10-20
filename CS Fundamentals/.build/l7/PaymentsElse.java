public class PaymentsElse {
    sealed interface PaymentResult permits Success, Declined, Failed, Pending {}

    record Success(String transactionId) implements PaymentResult {}
    record Declined(String reason) implements PaymentResult {}
    record Failed(boolean retryable) implements PaymentResult {}
    record Pending(int checkAgainInSeconds) implements PaymentResult {}   // added later

    static String describe(PaymentResult result) {
        return switch (result) {
            case Success s -> "paid, id " + s.transactionId();
            case Declined d -> "declined: " + d.reason();
            default -> "failed";              // swallows Failed AND the new Pending
        };
    }

    public static void main(String[] args) {
        System.out.println(describe(new Pending(30)));   // compiles, and silently lies
    }
}
