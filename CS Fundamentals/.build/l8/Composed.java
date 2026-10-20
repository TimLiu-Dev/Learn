import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Composed {
    static class InstrumentedSet<E> {                // HAS a set
        private final Set<E> inner;
        private int addCount = 0;

        InstrumentedSet(Set<E> inner) { this.inner = inner; }

        boolean add(E e) {
            addCount++;
            return inner.add(e);                     // forward the real work
        }

        boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return inner.addAll(c);                  // inner.addAll can't call OUR add
        }

        int size() { return inner.size(); }

        int getAddCount() { return addCount; }
    }

    public static void main(String[] args) {
        InstrumentedSet<String> s = new InstrumentedSet<>(new HashSet<>());
        s.addAll(List.of("Snap", "Crackle", "Pop"));
        System.out.println("added 3, counted " + s.getAddCount() + ", size " + s.size());
    }
}
