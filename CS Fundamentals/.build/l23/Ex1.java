public class Ex1 {
    static int mul3(int a, int b, int c) {
        return a * b * c;
    }

    static int countDown(int n) {
        int steps = 0;
        while (n > 0) {
            n -= 2;
            steps++;
        }
        return steps;
    }

    public static void main(String[] args) {
        System.out.println(mul3(2, 3, 4) + " " + countDown(7));
    }
}
