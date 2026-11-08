import java.util.*;

public class Count {
    static class CountingSet extends HashSet<String> {
        int count = 0;

        @Override public boolean add(String s) {
            count++;
            return super.add(s);
        }

        @Override public boolean addAll(Collection<? extends String> c) {
            count += c.size();
            return super.addAll(c);
        }
    }

    public static void main(String[] args) {
        CountingSet s = new CountingSet();
        s.addAll(List.of("a", "b", "a"));
        System.out.println(s.count + " " + s.size());
    }
}
