import java.util.List;
import java.util.Objects;

public class Practice {
    // P(outage | alert), from P(outage), P(alert | outage) and P(alert | normal).
    static double pOutageGivenAlert(double prior, double hitRate, double falseAlarmRate) {
        throw new UnsupportedOperationException("TODO: Bayes' rule, with P(alert) from both ways an alert can happen");
    }

    // Standard deviation: the square root of the mean squared distance from the mean (divide by n).
    static double stdDev(List<Double> timings) {
        throw new UnsupportedOperationException("TODO: find the mean first, then the mean squared distance");
    }

    // CHECKS
    public static void main(String[] args) {
        check("rare outages make most alerts false", "0.0098", () -> String.format("%.4f", pOutageGivenAlert(0.0001, 0.99, 0.01)));
        check("common outages make most alerts real", "0.9900", () -> String.format("%.4f", pOutageGivenAlert(0.5, 0.99, 0.01)));
        check("an alert that ignores the system tells you nothing", "0.0200", () -> String.format("%.4f", pOutageGivenAlert(0.02, 0.3, 0.3)));
        check("identical timings have no spread", "0.0000", () -> String.format("%.4f", stdDev(List.of(41.5, 41.5, 41.5))));
        check("standard deviation of a known sample", "2.0000", () -> String.format("%.4f", stdDev(List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0))));
        check("shifting every timing does not change the spread", "2.0000", () -> String.format("%.4f", stdDev(List.of(102.0, 104.0, 104.0, 104.0, 105.0, 105.0, 107.0, 109.0))));
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
