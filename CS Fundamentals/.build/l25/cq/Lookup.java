public class Lookup {
    static class Calc {
        public int twice(int x) { return 2 * x; }
    }

    public static void main(String[] args) throws Exception {
        String first;
        try {
            first = String.valueOf(Calc.class.getMethod("twice", Integer.class).invoke(new Calc(), 21));
        } catch (NoSuchMethodException e) {
            first = "missing";
        }
        Object second = Calc.class.getMethod("twice", int.class).invoke(new Calc(), 21);
        System.out.println(first + " " + second);
    }
}
