import java.util.*;

public class PushOffer {
    public static void main(String[] args) {
        Deque<Integer> d = new ArrayDeque<>();
        d.push(1);
        d.push(2);
        d.offer(3);
        System.out.println(d + " " + d.pop() + " " + d.poll());
    }
}
