public class RefusedWrite {
    static class Replica {
        String balance = "100";
        Replica peer;
        boolean linkUp = true;

        void write(String value) {
            balance = value;
            if (!linkUp) throw new IllegalStateException("can't reach peer");
            peer.balance = value;
        }
    }

    public static void main(String[] args) {
        Replica a = new Replica(), b = new Replica();
        a.peer = b;
        b.peer = a;
        a.linkUp = false;                       // partition
        String result;
        try {
            a.write("70");
            result = "written";
        } catch (IllegalStateException e) {
            result = "refused";
        }
        System.out.println(result + ": A=" + a.balance + " B=" + b.balance);
    }
}
