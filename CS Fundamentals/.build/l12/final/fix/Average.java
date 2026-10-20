import java.util.List;

public class Average {
    static double average(List<Integer> readings) {
        int sum = 0;
        for (int r : readings) sum += r;
        return (double) sum / readings.size();
    }

    public static void main(String[] args) {
        List<Integer> readings = List.of(72, 75, 79, 80);
        System.out.println("Average heart rate: " + average(readings) + " bpm");
    }
}
