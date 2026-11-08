public class Definite {
    static int pick(int n) {
        int x;
        if (n == n) x = 1;     // always true for an int, but the compiler doesn't reason about values
        return x;
    }

    public static void main(String[] args) { System.out.println(pick(7)); }
}
