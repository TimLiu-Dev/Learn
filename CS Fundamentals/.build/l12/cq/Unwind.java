public class Unwind {
    static void inner() {
        try {
            Integer.parseInt("64l");
        } finally {
            System.out.println("inner finally");
        }
    }

    static void outer() {
        try {
            inner();
        } finally {
            System.out.println("outer finally");
        }
    }

    public static void main(String[] args) {
        try {
            outer();
        } catch (NumberFormatException e) {
            System.out.println("caught");
        }
    }
}
