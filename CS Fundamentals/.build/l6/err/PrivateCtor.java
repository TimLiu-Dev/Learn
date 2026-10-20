public class PrivateCtor {
    public static void main(String[] args) {
        Ticket t = new Ticket(9);
    }
}

final class Ticket {
    private Ticket(int id) { }
}
