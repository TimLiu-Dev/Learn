import java.util.List;
import java.util.Set;

public class FloatEq {
    public static void main(String[] args) {
        double nan = Double.NaN;
        System.out.println((nan == nan) + " " + Double.isNaN(nan));  // IEEE 754: NaN equals nothing
        System.out.println(0.0 == -0.0);                              // IEEE 754: the two zeros are equal
        Double boxed = nan;
        System.out.println(boxed.equals(nan) + " " + Double.valueOf(0.0).equals(-0.0));  // equals(): the opposite
        System.out.println(List.of(nan).contains(nan));               // collections use equals()
        System.out.println(Set.of(0.0, -0.0).size());
    }
}
