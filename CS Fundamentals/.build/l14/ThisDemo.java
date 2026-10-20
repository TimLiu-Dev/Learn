public class ThisDemo {
    String name = "outer ThisDemo";

    void run() {
        Runnable lambda = () -> System.out.println("lambda this: " + this.name);
        Runnable anon = new Runnable() {
            String name = "anonymous class";
            public void run() { System.out.println("anon this:   " + this.name); }
        };
        lambda.run();
        anon.run();
    }

    public static void main(String[] args) { new ThisDemo().run(); }
}
