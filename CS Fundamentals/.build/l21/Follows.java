import java.util.*;

public class Follows {
    record Follow(String from, String to) {}

    public static void main(String[] args) {
        List<Follow> follows = List.of(
            new Follow("ana", "ben"), new Follow("ana", "cat"), new Follow("ben", "cat"),
            new Follow("cat", "ana"), new Follow("dan", "cat"), new Follow("eve", "dan"));
        // Directed: the edge goes into the "from" list only.
        Map<String, List<String>> following = new LinkedHashMap<>();
        Map<String, Integer> followers = new LinkedHashMap<>();
        for (Follow f : follows) {
            following.computeIfAbsent(f.from(), k -> new ArrayList<>()).add(f.to());
            followers.merge(f.to(), 1, Integer::sum);
        }
        System.out.println("following: " + following);
        System.out.println("followers: " + followers);
        String top = Collections.max(followers.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("most followed: " + top);
        List<String> mutual = new ArrayList<>();
        for (Follow f : follows) {
            String a = f.from(), b = f.to();
            if (a.compareTo(b) < 0 && following.getOrDefault(b, List.of()).contains(a)) mutual.add(a + "<->" + b);
        }
        System.out.println("mutual: " + mutual);
    }
}
