public class Finally {
    static int keep() {
        int x = 1;
        try {
            return x;
        } finally {
            x = 99;
        }
    }

    static int replace() {
        try {
            return 1;
        } finally {
            return 2;
        }
    }

    public static void main(String[] args) {
        System.out.println(keep() + " " + replace());
    }
}
