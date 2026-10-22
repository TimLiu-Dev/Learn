// The sealed side of the trade-off: add a new CASE and every switch must be edited.
public class OcpNewCase {
    sealed interface Channel permits Email, Sms, Slack {}
    record Email() implements Channel {}
    record Sms(int maxChars) implements Channel {}
    record Slack(String room) implements Channel {}   // the new case

    static int costCents(Channel c) {
        return switch (c) {
            case Email e -> 0;
            case Sms s -> 5;
        };
    }

    public static void main(String[] args) { System.out.println(costCents(new Slack("library"))); }
}
