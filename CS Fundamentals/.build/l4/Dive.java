public class Dive {
    static int depth = 0;

    static void dive() {
        depth++;
        dive();         // no base case: every call pushes another frame
    }

    public static void main(String[] args) {
        try {
            dive();
        } catch (StackOverflowError e) {
            System.out.println("stack overflowed at depth " + depth);
        }
    }
}
