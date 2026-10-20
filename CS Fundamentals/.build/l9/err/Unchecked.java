public class Unchecked {
    public static void main(String[] args) {
        Tasks.readConfig("app.conf");   // no try/catch, no throws clause
    }
}
