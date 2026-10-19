import java.util.ArrayList;
import java.util.List;

public class Grid {
    public static void main(String[] args) {
        List<Integer> row = new ArrayList<>();
        List<List<Integer>> grid = new ArrayList<>();
        for (int i = 0; i < 3; i++) grid.add(row);         // three references to ONE list
        grid.get(0).add(1);
        System.out.println(grid);

        List<List<Integer>> fixed = new ArrayList<>();
        for (int i = 0; i < 3; i++) fixed.add(new ArrayList<>());   // a NEW list each time
        fixed.get(0).add(1);
        System.out.println(fixed);
    }
}
