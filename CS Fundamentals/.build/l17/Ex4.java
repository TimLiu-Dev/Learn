import java.util.*;

public class Ex4 {
    record Task(String name, int remaining) {}

    static List<String> roundRobin(List<Task> tasks, int slice) {
        Queue<Task> ready = new ArrayDeque<>(tasks);    // FIFO: head = next to run
        List<String> finished = new ArrayList<>();
        int clock = 0;
        while (!ready.isEmpty()) {
            Task t = ready.poll();
            int ran = Math.min(slice, t.remaining());
            clock += ran;
            int left = t.remaining() - ran;
            if (left > 0) {
                System.out.printf("t=%2d  %s ran %d, %d left -> back of the queue%n", clock, t.name(), ran, left);
                ready.offer(new Task(t.name(), left));
            } else {
                System.out.printf("t=%2d  %s ran %d, done%n", clock, t.name(), ran);
                finished.add(t.name());
            }
        }
        return finished;
    }

    public static void main(String[] args) {
        List<String> order = roundRobin(List.of(new Task("build", 5), new Task("email", 2), new Task("backup", 7)), 3);
        System.out.println("finished in order: " + order);
    }
}
