import java.util.*;

public class Navigation {
    public static void main(String[] args) {
        // Departure times (minutes after midnight) -> train. Kept sorted by key.
        TreeMap<Integer, String> trains = new TreeMap<>(Map.of(
            375, "06:15 local", 420, "07:00 express", 465, "07:45 local",
            510, "08:30 express", 600, "10:00 local"));
        System.out.println(trains.getClass().getName());

        int now = 450;                                // it's 07:30
        System.out.println("last train before now:  " + trains.get(trains.floorKey(now)));
        System.out.println("next train from now:    " + trains.get(trains.ceilingKey(now)));
        System.out.println("first / last:           " + trains.firstKey() + " / " + trains.lastKey());
        System.out.println("before 08:00 (480):     " + trains.headMap(480).values());
        System.out.println("from 08:00 on:          " + trains.tailMap(480).values());
        System.out.println("after 10:00:            " + trains.ceilingKey(601));
    }
}
