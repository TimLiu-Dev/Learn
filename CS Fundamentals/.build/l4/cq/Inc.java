public class Inc {
    static void inc(Integer i) {
        i++;
    }

    public static void main(String[] args) {
        Integer x = 5;
        inc(x);
        System.out.println(x);
    }
}
