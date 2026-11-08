package katas;

import java.util.function.IntSupplier;

// One entry point per back end.
public final class Calc {
    private static Outcome attempt(IntSupplier run) {
        try {
            return new Outcome.Value(run.getAsInt());
        } catch (CalcError e) {
            return new Outcome.Error(e.getMessage());
        }
    }

    public static Outcome runInterpreted(String src) {
        return attempt(() -> {
            Program p = Parser.parse(src);
            Resolve.resolve(p);
            return Interp.interpret(p);
        });
    }

    public static Outcome runCompiled(String src) {
        return attempt(() -> Vm.execute(new Compiler().compile(Parser.parse(src))));
    }

    private Calc() {}
}
