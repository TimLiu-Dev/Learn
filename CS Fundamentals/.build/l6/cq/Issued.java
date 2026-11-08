public class Issued {
    static class Ticket {
        static int issued = 0;
        int number;

        Ticket() {
            issued++;
            number = issued;
        }
    }

    public static void main(String[] args) {
        Ticket a = new Ticket();
        Ticket b = new Ticket();
        Ticket c = new Ticket();
        System.out.println(a.number + " " + c.number + " " + a.issued);
    }
}
