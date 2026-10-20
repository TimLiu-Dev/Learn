public class Tickets {
    public static void main(String[] args) {
        Ticket t1 = Ticket.standard("Concert");
        Ticket t2 = Ticket.vip("Concert");
        System.out.println("#" + t1.id + " " + t1.event + " " + t1.priceCents);
        System.out.println("#" + t2.id + " " + t2.event + " " + t2.priceCents);
        System.out.println("issued: " + Ticket.issuedCount());
        System.out.println("max: " + Ticket.MAX_PRICE_CENTS);
        System.out.println("next id would be " + IdGenerator.INSTANCE.nextId());
    }
}

final class IdGenerator {                  // singleton: private constructor + one static instance
    static final IdGenerator INSTANCE = new IdGenerator();
    private int next = 1;

    private IdGenerator() { }

    int nextId() { return next++; }
}

final class Ticket {
    static final long MAX_PRICE_CENTS = 50_000L;    // static: one copy, shared by the class
    private static int issued = 0;

    final int id;
    final String event;
    final long priceCents;

    private Ticket(int id, String event, long priceCents) {   // outsiders must use the factories
        this.id = id;
        this.event = event;
        this.priceCents = priceCents;
    }

    static Ticket standard(String event) { return create(event, 2_500); }
    static Ticket vip(String event) { return create(event, 12_000); }
    static int issuedCount() { return issued; }

    private static Ticket create(String event, long priceCents) {
        if (priceCents > MAX_PRICE_CENTS) throw new IllegalArgumentException("too expensive");
        issued++;
        return new Ticket(IdGenerator.INSTANCE.nextId(), event, priceCents);
    }
}
