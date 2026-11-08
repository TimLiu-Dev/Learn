package katas;

public class App {
    public static void main(String[] args) {
        int[][] pairs = {{6, 7}, {13, 20}, {255, 255}};
        for (int[] p : pairs) {
            Cpu cpu = Programs.multiply(p[0], p[1]);
            System.out.println(p[0] + " * " + p[1] + " = " + cpu.mem[0x82] + " (mod 256) in " + cpu.steps() + " instructions");
        }
    }
}
