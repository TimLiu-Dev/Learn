import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MutableKey {
    static final class Member {                   // a mutable field in a key: asking for trouble
        String name;
        Member(String name) { this.name = name; }
        @Override public boolean equals(Object o) { return o instanceof Member m && m.name.equals(name); }
        @Override public int hashCode() { return Objects.hash(name); }
        @Override public String toString() { return "Member(name=" + name + ")"; }
    }

    public static void main(String[] args) {
        Map<Member, Integer> points = new HashMap<>();
        Member ada = new Member("ada");
        points.put(ada, 100);
        System.out.println("before: " + points.get(ada));

        ada.name = "ada lovelace";                // hashCode changes, the entry doesn't move
        System.out.println("same object: " + points.get(ada));
        System.out.println("old name: " + points.get(new Member("ada")));
        System.out.println("size " + points.size() + ", contents " + points);
    }
}
