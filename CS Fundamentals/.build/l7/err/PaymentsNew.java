import java.util.List;

public class PaymentsNew {
    sealed interface PaymentResult permits Success, Declined, Failed, Pending {}

    record Success(String transactionId) implements PaymentResult {}
    record Declined(String reason) implements PaymentResult {}
    record Failed(boolean retryable) implements PaymentResult {}
    record Pending(int checkAgainInSeconds) implements PaymentResult {}

    static String describe(PaymentResult result) {
        return switch (result) {                  // no default: the compiler knows every case
            case Success s -> "paid, id " + s.transactionId();      // type pattern
            case Declined(String reason) -> "declined: " + reason;  // record pattern
            case Failed f -> f.retryable() ? "failed, try again" : "failed permanently";
        };
    }

    public static void main(String[] args) {
        List<PaymentResult> results = List.of(new Success("tx-42"), new Declined("insufficient funds"), new Failed(true));
        for (PaymentResult r : results) System.out.println(describe(r));
    }
}
