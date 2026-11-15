public class Encode {
    public static void main(String[] args) {
        int op = 5, rd = 2, rs = 3;   // SUB R2, R3
        int withOr = op << 4 | rd << 2 | rs;
        int withPlus = op << 4 + rd << 2 + rs;
        System.out.println(withOr + " " + withPlus);
    }
}
