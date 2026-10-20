import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Instrumented {
    static class InstrumentedHashSet<E> extends HashSet<E> {   // inherits from a class it doesn't control
        private int addCount = 0;

        @Override
        public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c);
        }

        int getAddCount() { return addCount; }
    }

    public static void main(String[] args) {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        s.addAll(List.of("Snap", "Crackle", "Pop"));
        System.out.println("added 3, counted " + s.getAddCount());
    }
}
