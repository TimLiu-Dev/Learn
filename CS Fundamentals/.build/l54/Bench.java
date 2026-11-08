// A long straight-line program: 300 lets, each built from earlier variables.
public class Bench {
    static String bigProgram(int n) {
        var sb = new StringBuilder("let v0 = 7;\n");
        for (int i = 1; i < n; i++) {
            sb.append("let v").append(i).append(" = v").append(i - 1).append(" * 3 + v").append(i / 2)
              .append(" % 7 - ").append(i % 10).append(";\n");
        }
        return sb.append("v").append(n - 1).toString();
    }

    public static void main(String[] args) {
        Program program = Parser.parse(bigProgram(300));
        Chunk chunk = new Compiler().compile(program);
        System.out.println(chunk.code().length + " ints of bytecode, maxStack=" + chunk.maxStack()
                + ", locals=" + chunk.locals().size());
        long sink = 0;                                    // use every result, so the JIT can't skip the work (L16)
        for (int round = 1; round <= 3; round++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < 20_000; i++) sink += Interp.interpret(program);
            long t1 = System.nanoTime();
            for (int i = 0; i < 20_000; i++) sink += Vm.execute(chunk);
            long t2 = System.nanoTime();
            System.out.printf("round %d: tree-walk %d ms, VM %d ms, ratio %.1f%n",
                    round, (t1 - t0) / 1_000_000, (t2 - t1) / 1_000_000, (double) (t1 - t0) / (t2 - t1));
        }
        System.out.println("results agree: " + (Interp.interpret(program) == Vm.execute(chunk)) + " (sink " + sink + ")");
    }
}
