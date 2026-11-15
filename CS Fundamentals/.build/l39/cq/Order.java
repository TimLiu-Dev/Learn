public class Order {
    interface Text { String render(String s); }

    static Text plain() { return s -> s; }
    static Text stars(Text inner) { return s -> "*" + inner.render(s) + "*"; }
    static Text cut(Text inner, int n) {
        return s -> {
            String r = inner.render(s);                      // let the inner layer go first
            return r.substring(0, Math.min(n, r.length()));
        };
    }

    public static void main(String[] args) {
        System.out.println(cut(stars(plain()), 3).render("dune") + " " + stars(cut(plain(), 3)).render("dune"));
    }
}
