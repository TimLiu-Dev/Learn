public class Digits {
    public static void main(String[] args) {
        int viaLn = (int) (Math.log(1000) / Math.log(10));
        int viaLog10 = (int) Math.log10(1000);
        System.out.println(viaLn + " " + viaLog10);
    }
}
