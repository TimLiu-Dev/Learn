public class DeclSite {
    interface Source<T> { T next(); }            // Java can't mark T as "out" here...
    interface Sink<T> { void put(T item); }      // ...or "in" here

    static class Counter implements Source<Integer> {
        private int n;
        Counter(int n) { this.n = n; }
        public Integer next() { return n++; }
    }

    static class Printer implements Sink<Object> {
        public void put(Object item) { System.out.println("printing " + item); }
    }

    public static void main(String[] args) {
        Source<Integer> ints = new Counter(10);
        Source<? extends Number> nums = ints;    // ...so every USE needs a wildcard
        System.out.println(nums.next());

        Sink<Object> anything = new Printer();
        Sink<? super String> strings = anything;
        strings.put("hello");

        Comparable<Number> byNumber = new Comparable<Number>() {
            public int compareTo(Number other) { return 0; }
        };
        Comparable<? super Double> byDouble = byNumber;  // must spell out "? super"
        System.out.println(byDouble.compareTo(1.5));
    }
}
