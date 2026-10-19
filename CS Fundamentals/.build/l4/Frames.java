public class Frames {
    static int square(int x) {
        int result = x * x;
        return result;
    }

    static int sumOfSquares(int a, int b) {
        int sa = square(a);
        int sb = square(b);
        return sa + sb;
    }

    public static void main(String[] args) {
        int total = sumOfSquares(3, 4);
        System.out.println(total);
    }
}
