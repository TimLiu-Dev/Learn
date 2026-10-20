import java.util.*;

public class LegacyStack {
    public static void main(String[] args) {
        Stack<String> old = new Stack<>();
        old.push("a"); old.push("b"); old.push("c");
        System.out.println(old instanceof Vector);      // Stack IS-A Vector...
        old.add(0, "sneaky");                           // ...so any Vector method works
        System.out.println(old + " get(1)=" + old.get(1));
        System.out.println("pop: " + old.pop());

        Deque<String> stack = new ArrayDeque<>();
        stack.push("a"); stack.push("b"); stack.push("c");
        System.out.println(stack + " pop: " + stack.pop());   // iterates top first
    }
}
