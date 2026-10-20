import java.util.ArrayList;
import java.util.List;

public class Ex2 {
    static <T> void transferFirst(List<? extends T> src, List<? super T> dst) {
        dst.add(src.remove(0));
    }

    interface Producer<T> { T produce(); }

    static class Greeter implements Producer<String> {
        public String produce() { return "hi"; }
    }

    public static void main(String[] args) {
        List<Integer> src = new ArrayList<>(List.of(1, 2, 3));
        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        transferFirst(src, numbers);
        transferFirst(src, objects);
        System.out.println("src=" + src + " numbers=" + numbers + " objects=" + objects);

        Producer<? extends Object> p = new Greeter();   // the wildcard goes at the use site
        System.out.println(p.produce());
    }
}
