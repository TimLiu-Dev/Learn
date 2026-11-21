public class PerSecond {
    public static void main(String[] args) {
        long linksPerMonth = 100_000_000L;
        long secondsPerMonth = 30L * 24 * 3600;
        double writesPerSec = linksPerMonth / secondsPerMonth;
        double readsPerSec = writesPerSec * 100;
        System.out.println(readsPerSec);
    }
}
