public class Late {
    static class Report {
        String title;                 // starts as null; nothing stops you reading it

        void print() { System.out.println("Report: " + title); }
    }

    public static void main(String[] args) {
        Report r = new Report();
        r.print();                    // no crash: silently prints null
        r.title = "Q3 sales";
        r.print();
    }
}
