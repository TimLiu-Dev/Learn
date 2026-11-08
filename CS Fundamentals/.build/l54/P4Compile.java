public class P4Compile {
    public static void main(String[] args) {
        String src = "let x = 3; x * (x + 1)";
        Chunk chunk = new Compiler().compile(Parser.parse(src));
        System.out.print(Vm.disassemble(chunk));
        System.out.println("result = " + Vm.execute(chunk) + ", interpreter says " + Interp.interpret(Parser.parse(src)));

        for (String s : new String[] {"1 + (2 * (3 - 4))", "((1 + 2) * 3) - 4"}) {
            System.out.println(String.format("%-20s", s) + "maxStack=" + new Compiler().compile(Parser.parse(s)).maxStack());
        }
        for (String bad : new String[] {"let a = 1; a + b", "10 / (5 - 5)"}) {
            try {
                Vm.execute(new Compiler().compile(Parser.parse(bad)));
            } catch (CalcError e) {
                System.out.println(String.format("%-20s", bad) + "CalcError: " + e.getMessage());
            }
        }
    }
}
