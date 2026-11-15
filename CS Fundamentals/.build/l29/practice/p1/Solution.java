import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

// Nearest Java equivalent: Java has no coroutines and no scope that cancels siblings
// for you (StructuredTaskScope is still a preview), so you track the futures yourself.
public class Practice {
    record Page(String name, int friends, List<String> posts) { }

    interface Api {
        String name(int id) throws Exception;
        int friendCount(int id) throws Exception;
        List<String> posts(int id) throws Exception;
    }

    static Page loadPage(Api api, int id) throws Exception {
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {   // close() waits for every task
            Future<String> name = pool.submit(() -> api.name(id));
            Future<Integer> friends = pool.submit(() -> api.friendCount(id));
            Future<List<String>> posts = pool.submit(() -> api.posts(id));
            try {
                return new Page(name.get(), friends.get(), posts.get());
            } catch (ExecutionException e) {
                for (Future<?> f : List.of(name, friends, posts)) f.cancel(true);   // by hand: interrupts the rest
                throw e;
            }
        }
    }

    static List<Page> loadPages(Api api, List<Integer> ids) throws Exception {
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Page>> pages = new ArrayList<>();
            for (int id : ids) pages.add(pool.submit(() -> loadPage(api, id)));
            List<Page> result = new ArrayList<>();
            for (Future<Page> page : pages) {
                try {
                    result.add(page.get());
                } catch (ExecutionException e) {
                    result.add(null);                     // this page failed; the others carry on
                }
            }
            return result;
        }
    }

    // CHECKS
    // name takes 100 ms, friendCount 200 ms, posts 300 ms. For id 13, friendCount fails.
    static class FakeApi implements Api {
        final List<String> log = Collections.synchronizedList(new ArrayList<>());

        <T> T call(String what, int id, long ms, Callable<T> result) throws Exception {
            log.add("start " + what + " " + id);
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                log.add("cancel " + what + " " + id);
                throw e;
            }
            log.add("end " + what + " " + id);
            return result.call();
        }

        public String name(int id) throws Exception { return call("name", id, 100, () -> "user" + id); }
        public int friendCount(int id) throws Exception {
            return call("friends", id, 200, () -> { if (id == 13) throw new IOException("friends down"); return id * 10; });
        }
        public List<String> posts(int id) throws Exception { return call("posts", id, 300, () -> List.of("hello from " + id)); }
    }

    static long startedBeforeFirstEnd(List<String> log) {
        List<String> copy = new ArrayList<>(log);
        int firstEnd = 0;
        while (firstEnd < copy.size() && !copy.get(firstEnd).startsWith("end")) firstEnd++;
        return copy.subList(0, firstEnd).stream().filter(s -> s.startsWith("start")).count();
    }

    static String failure(Callable<?> c) {
        try {
            c.call();
            return "no failure";
        } catch (Exception e) {
            Throwable t = e;
            while (t.getCause() != null) t = t.getCause();
            return t.getMessage();
        }
    }

    public static void main(String[] args) throws Exception {
        check("a page has all three parts", new Page("user1", 10, List.of("hello from 1")), () -> loadPage(new FakeApi(), 1));
        FakeApi a = new FakeApi();
        check("the three calls run at the same time", 3L, () -> { loadPage(a, 1); return startedBeforeFirstEnd(a.log); });
        FakeApi b = new FakeApi();
        check("a failing call fails the page", "friends down", () -> failure(() -> loadPage(b, 13)));
        check("the slower call is cancelled, not finished", "cancel posts 13", () ->
            b.log.contains("end posts 13") ? "posts finished" : b.log.contains("cancel posts 13") ? "cancel posts 13" : "posts never started");
        Page p1 = new Page("user1", 10, List.of("hello from 1")), p2 = new Page("user2", 20, List.of("hello from 2"));
        check("one broken page does not stop the others", Arrays.asList(p1, null, p2), () -> loadPages(new FakeApi(), List.of(1, 13, 2)));
        FakeApi c = new FakeApi();
        check("pages load at the same time", 6L, () -> { loadPages(c, List.of(1, 2)); return startedBeforeFirstEnd(c.log); });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
