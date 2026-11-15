import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Snapshot {
    public static void main(String[] args) {
        List<String> list = new CopyOnWriteArrayList<>(List.of("a", "b"));
        for (String s : list) {
            list.add(s + "!");
        }
        System.out.println(list);
    }
}
