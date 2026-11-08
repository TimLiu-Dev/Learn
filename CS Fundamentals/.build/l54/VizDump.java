import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Runs the real pipeline on a few programs and prints the parseviz spec as JSON (used by fill.py).
public class VizDump {
    static String q(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }

    record Node(int id, String label, String kind, List<Integer> kids) {}

    static final List<Node> nodes = new ArrayList<>();
    static final List<String> order = new ArrayList<>();      // post-order steps as JSON objects
    static int pc;

    static int add(String label, String kind, List<Integer> kids) {
        nodes.add(new Node(nodes.size(), label, kind, kids));
        return nodes.size() - 1;
    }

    // Walks the tree in the order eval and the compiler both use, recording each node's value and its bytecode.
    static int walk(Expr e, Map<String, Integer> env) {
        return switch (e) {
            case Expr.Num n -> step(add(String.valueOf(n.value()), "num", List.of()), n.value(), "push " + n.value(), 2);
            case Expr.Var v -> step(add(v.name(), "var", List.of()), env.get(v.name()), "load " + v.name(), 2);
            case Expr.Neg n -> {
                int kid = walk(n.operand(), env);
                yield step(add("neg", "op", List.of(kid)), Interp.eval(e, env), "neg", 1);
            }
            case Expr.Bin b -> {
                int l = walk(b.left(), env), r = walk(b.right(), env);
                String op = Op.NAMES.get(switch (b.op()) {
                    case PLUS -> Op.ADD; case MINUS -> Op.SUB; case STAR -> Op.MUL;
                    case SLASH -> Op.DIV; case PERCENT -> Op.MOD; default -> Op.POW; });
                yield step(add(b.at().text(), "op", List.of(l, r)), Interp.eval(e, env), op, 1);
            }
        };
    }

    static int step(int id, int value, String instr, int size) {
        order.add("{\"id\":" + id + ",\"value\":" + value + ",\"pc\":" + pc + ",\"instr\":" + q(instr) + "}");
        pc += size;
        return id;
    }

    static String dump(String src) {
        nodes.clear(); order.clear(); pc = 0;
        // 1. tokens, with character offsets so the widget can highlight the lexeme in the source
        var tokens = new ArrayList<String>();
        String[] lines = src.split("\n", -1);
        for (Token t : new Lexer(src).tokens()) {
            int offset = t.col() - 1;
            for (int i = 0; i < t.line() - 1; i++) offset += lines[i].length() + 1;
            tokens.add("{\"type\":" + q(t.type().name()) + ",\"text\":" + q(t.text()) + ",\"at\":" + q(t.at())
                    + ",\"start\":" + offset + ",\"end\":" + (offset + t.text().length()) + "}");
        }
        // 2. tree and evaluation order
        Program p = Parser.parse(src);
        var env = new HashMap<String, Integer>();
        var top = new ArrayList<Integer>();
        for (var let : p.lets()) {
            int kid = walk(let.value(), env);
            env.put(let.name(), Interp.eval(let.value(), env));
            top.add(step(add("let " + let.name(), "let", List.of(kid)), env.get(let.name()), "store " + let.name(), 2));
        }
        top.add(walk(p.result(), env));
        int root = add("program", "root", top);
        // 3. bytecode, and a traced run of the same loop as Vm.execute
        Chunk c = new Compiler().compile(p);
        var code = new ArrayList<String>();
        for (String line : Vm.disassemble(c).lines().skip(1).toList()) {
            String[] parts = line.trim().split(":\\s+", 2);
            code.add("{\"pc\":" + parts[0] + ",\"text\":" + q(parts[1].replaceAll("\\s+//", "  //")) + "}");
        }
        var steps = new ArrayList<String>();
        int[] stack = new int[c.maxStack()], locals = new int[c.locals().size()];
        boolean[] set = new boolean[locals.length];
        int sp = 0, ip = 0, result = 0;
        while (true) {
            int at = ip, op = c.code()[ip++];
            String note;
            if (op == Op.PUSH) { stack[sp++] = c.code()[ip++]; note = "Push the constant " + stack[sp - 1] + "."; }
            else if (op == Op.LOAD) { int s = c.code()[ip++]; stack[sp++] = locals[s]; note = "Copy local " + s + " (" + c.locals().get(s) + " = " + locals[s] + ") onto the stack."; }
            else if (op == Op.STORE) { int s = c.code()[ip++]; locals[s] = stack[--sp]; set[s] = true; note = "Pop " + locals[s] + " into local " + s + " (" + c.locals().get(s) + ")."; }
            else if (op == Op.NEG) { stack[sp - 1] = -stack[sp - 1]; note = "Negate the top of the stack: " + stack[sp - 1] + "."; }
            else if (op == Op.RETURN) { result = stack[--sp]; note = "Pop " + result + " and return it. Done."; }
            else {
                int b = stack[--sp], a = stack[sp - 1];
                T t = switch (op) { case Op.ADD -> T.PLUS; case Op.SUB -> T.MINUS; case Op.MUL -> T.STAR;
                                    case Op.DIV -> T.SLASH; case Op.MOD -> T.PERCENT; default -> T.CARET; };
                stack[sp - 1] = Interp.arith(t, a, b, c.where()[at]);
                note = "Pop " + b + " and " + a + ", push " + a + " " + t.name().toLowerCase().replace("plus", "+").replace("minus", "-")
                        .replace("star", "*").replace("slash", "/").replace("percent", "%").replace("caret", "^") + " " + b + " = " + stack[sp - 1] + ".";
            }
            var st = new ArrayList<String>();
            for (int i = 0; i < sp; i++) st.add(String.valueOf(stack[i]));
            var lc = new ArrayList<String>();
            for (int i = 0; i < locals.length; i++) lc.add(set[i] ? String.valueOf(locals[i]) : "null");
            steps.add("{\"pc\":" + at + ",\"stack\":" + st + ",\"locals\":" + lc + ",\"note\":" + q(note) + "}");
            if (op == Op.RETURN) break;
        }
        if (result != Vm.execute(c) || result != Interp.interpret(p)) throw new AssertionError("trace disagrees: " + src);
        // the post-order walk must predict exactly the instructions the compiler emitted
        var predicted = order.stream().map(s -> s.replaceAll(".*\"instr\":\"([a-z]+).*", "$1")).toList();
        var emitted = code.stream().map(s -> s.replaceAll(".*\"text\":\"([a-z]+).*", "$1")).filter(s -> !s.equals("return")).toList();
        if (!predicted.equals(emitted)) throw new AssertionError(predicted + " vs " + emitted);

        var ns = new ArrayList<String>();
        for (Node n : nodes) ns.add("{\"label\":" + q(n.label()) + ",\"kind\":" + q(n.kind()) + ",\"kids\":" + n.kids() + "}");
        var locs = c.locals().stream().map(VizDump::q).toList();
        return "{\"src\":" + q(src) + ",\"tokens\":" + tokens + ",\"nodes\":" + ns + ",\"root\":" + root
                + ",\"order\":" + order + ",\"code\":" + code + ",\"locals\":" + locs + ",\"maxStack\":" + c.maxStack()
                + ",\"steps\":" + steps + ",\"result\":" + result + "}";
    }

    public static void main(String[] args) {
        var out = new ArrayList<String>();
        for (String src : args) out.add(dump(src));
        System.out.println("{\"programs\":" + out + "}");
    }
}
