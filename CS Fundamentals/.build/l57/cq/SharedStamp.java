import java.util.Arrays;

public class SharedStamp {
    static int[] clock = new int[3];

    static int[] tick(int me) {      // returns the event's vector timestamp
        clock[me]++;
        return clock;
    }

    public static void main(String[] args) {
        int[] a = tick(0);
        int[] b = tick(0);
        System.out.println(Arrays.toString(a) + " " + Arrays.toString(b) + " " + (a == b));
    }
}
