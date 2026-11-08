// Emits the cpuviz.js spec for the sum program by driving the real Toy.Cpu one instruction
// at a time. Each instruction becomes three frames: fetch, decode, execute.
// Run: javac -d /tmp/ct ../Toy.java CpuTrace.java && java -cp /tmp/ct CpuTrace 5 > sum.json
import java.util.*;

public class CpuTrace {
    static final String[] NAMES = {"HLT", "LDI", "LD", "ST", "ADD", "SUB", "JMP", "JZ"};
    static String hex(int x) { return Toy.hex(x); }
    static String bin(int x, int w) { String s = Integer.toBinaryString(x); return "0".repeat(w - s.length()) + s; }
    static String q(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }

    static String state(Toy.Cpu c, int[] data) {
        StringBuilder m = new StringBuilder();
        for (int a : data) m.append(m.length() == 0 ? "" : ",").append(c.mem[a]);
        return "\"reg\":" + Arrays.toString(c.reg).replace(" ", "") + ",\"data\":[" + m + "],\"pc\":" + c.pc;
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int[] prog = Toy.SUM_PROGRAM;
        int[] data = {0x80, 0x81};
        Toy.Cpu cpu = new Toy.Cpu(prog);
        cpu.mem[0x80] = n;
        StringBuilder out = new StringBuilder("{\"kind\":\"cpu\",\"code\":[");
        for (int a = 0; a < prog.length; a += 2) {
            out.append(a == 0 ? "" : ",").append("{\"a\":").append(a).append(",\"b\":[").append(prog[a]).append(",")
               .append(prog[a + 1]).append("],\"asm\":").append(q(Toy.disassemble(prog[a], prog[a + 1]))).append("}");
        }
        out.append("],\"dataAddr\":[128,129],\"dataLabel\":[\"n\",\"answer\"],\"init\":{").append(state(cpu, data)).append("},\"steps\":[");
        boolean first = true;
        while (!cpu.halted) {
            int at = cpu.pc, b0 = cpu.mem[at], b1 = cpu.mem[at + 1];
            int op = b0 >>> 4, rd = (b0 >>> 2) & 3, rs = b0 & 3;
            String asm = Toy.disassemble(b0, b1);
            int[] before = cpu.reg.clone();
            String pre = state(cpu, data);
            String fetch = "<b>Fetch.</b> Read the two bytes at address " + hex(at) + " (" + hex(b0) + " " + hex(b1)
                + ") into the instruction register. The program counter moves on to " + hex((at + 2) & 0xFF) + ".";
            String fields = bin(b0 >>> 4, 4) + " " + bin(rd, 2) + " " + bin(rs, 2);
            String decode = "<b>Decode.</b> Split " + hex(b0) + " = <code>" + fields + "</code>: opcode " + op + " is "
                + NAMES[op] + ", rd = R" + rd + ", rs = R" + rs + ". So this is <code>" + asm + "</code>.";
            cpu.step();
            String exec;
            switch (op) {
                case 0 -> exec = "HLT: the CPU stops. The answer " + cpu.mem[0x81] + " is in memory at 81.";
                case 1 -> exec = "R" + rd + " = " + hex(b1) + ", the value carried in the instruction itself.";
                case 2 -> exec = "R" + rd + " = mem[" + hex(b1) + "] = " + hex(cpu.reg[rd]) + ".";
                case 3 -> exec = "mem[" + hex(b1) + "] = R" + rd + " = " + hex(cpu.reg[rd]) + ".";
                case 4 -> exec = "R" + rd + " = R" + rd + " + R" + rs + " = " + hex(before[rd]) + " + " + hex(before[rs]) + " = " + hex(cpu.reg[rd]) + " (the ALU's adder).";
                case 5 -> exec = "R" + rd + " = R" + rd + " − R" + rs + " = " + hex(before[rd]) + " − " + hex(before[rs]) + " = " + hex(cpu.reg[rd]) + ".";
                case 6 -> exec = "Jump: pc = " + hex(b1) + ". This is how a loop goes back to its top.";
                default -> exec = before[rd] == 0
                    ? "R" + rd + " is zero, so jump: pc = " + hex(b1) + ". The loop is over."
                    : "R" + rd + " is " + hex(before[rd]) + ", not zero, so no jump: pc stays " + hex(cpu.pc) + ".";
            }
            // After the fetch, the program counter already points at the next instruction
            String afterFetch = pre.replaceAll("\"pc\":\\d+$", "\"pc\":" + ((at + 2) & 0xFF));
            String[][] frames = {{"fetch", fetch, pre}, {"decode", decode, afterFetch}, {"execute", "<b>Execute.</b> " + exec, state(cpu, data)}};
            for (String[] f : frames) {
                out.append(first ? "" : ",").append("{\"phase\":").append(q(f[0])).append(",\"at\":").append(at)
                   .append(",\"ir\":[").append(b0).append(",").append(b1).append("],\"fields\":").append(q(fields))
                   .append(",").append(f[2]).append(",\"note\":").append(q(f[1])).append("}");
                first = false;
            }
        }
        out.append("]}");
        System.out.println(out);
    }
}
