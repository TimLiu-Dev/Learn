package katas;

import java.util.stream.Collectors;

// ./gradlew run --args="let x = 3; x * (x + 1)" shows every stage of the pipeline.
public class App {
    public static void main(String[] args) {
        String src = args.length == 0 ? "let x = 3; x * (x + 1)" : String.join(" ", args);
        System.out.println("source:      " + src);
        System.out.println("tokens:      " + new Lexer(src).tokens().stream()
                .map(t -> t.type() == T.EOF ? "EOF" : t.text()).collect(Collectors.joining(" ")));
        System.out.println("tree:        " + Parser.show(Parser.parse(src)).replace("\n", "; "));
        System.out.println("bytecode:");
        Vm.disassemble(new Compiler().compile(Parser.parse(src))).lines().forEach(l -> System.out.println("  " + l));
        System.out.println("interpreted: " + Calc.runInterpreted(src));
        System.out.println("compiled:    " + Calc.runCompiled(src));
    }
}
