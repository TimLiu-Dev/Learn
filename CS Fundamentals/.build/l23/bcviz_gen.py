#!/usr/bin/env python3
"""Generate the bytecodeviz specs for L23 from REAL javap output, then inject them into the template.

usage: python3 bcviz_gen.py <dir with Calc.class (javac -g)> <dir with CalcKt.class> <template>

A tiny interpreter runs the disassembled instructions with concrete arguments, so every snapshot
(pc, locals, operand stack) follows from the actual bytecode javap printed. It supports only the
int instructions these methods use, and fails loudly on anything else.
"""
import json, re, subprocess, sys

def javap(cls_dir, cls):
    return subprocess.run(["javap", "-c", "-l", "-cp", cls_dir, cls],
                          capture_output=True, text=True, check=True).stdout

def method_block(text, sig):
    start = text.index(sig)
    rest = text[start:]
    end = rest.find("\n\n")
    return rest if end < 0 else rest[:end]

def parse(block):
    code, names = [], {}
    in_code = in_lvt = False
    for line in block.splitlines():
        s = line.strip()
        if s == "Code:": in_code = True; continue
        if s.startswith("LineNumberTable"): in_code = False; continue
        if s.startswith("LocalVariableTable"): in_lvt = True; continue
        if in_code:
            m = re.match(r"(\d+): (\S+)\s*(.*)", s)
            if m: code.append({"pc": int(m.group(1)), "op": m.group(2), "arg": m.group(3).split("//")[0].strip()})
        elif in_lvt:
            m = re.match(r"\d+\s+\d+\s+(\d+)\s+(\w+)\s+\S+", s)
            if m: names[int(m.group(1))] = m.group(2)
    return code, names

def run(code, names, args):
    nloc = max(names) + 1
    loc = [None] * nloc
    for i, a in enumerate(args): loc[i] = a
    st, steps = [], []
    idx = {c["pc"]: k for k, c in enumerate(code)}
    k = 0
    nm = lambda i: names.get(i, f"local {i}")
    while True:
        c = code[k]; op, arg = c["op"], c["arg"]; pc = c["pc"]; nxt = k + 1
        if m := re.fullmatch(r"iconst_(m1|\d)", op):
            v = -1 if m.group(1) == "m1" else int(m.group(1)); st.append(v); note = f"Push the constant {v}."
        elif op == "bipush":
            v = int(arg); st.append(v); note = f"Push the constant {v}."
        elif m := re.fullmatch(r"iload_(\d)", op) or (op == "iload" and re.fullmatch(r"(\d+)", arg)):
            i = int(m.group(1)); st.append(loc[i]); note = f"Copy local {i} ({nm(i)} = {loc[i]}) onto the stack."
        elif m := re.fullmatch(r"istore_(\d)", op) or (op == "istore" and re.fullmatch(r"(\d+)", arg)):
            i = int(m.group(1)); loc[i] = st.pop(); note = f"Pop {loc[i]} into local {i} ({nm(i)})."
        elif op in ("iadd", "imul"):
            b, a = st.pop(), st.pop(); r = a + b if op == "iadd" else a * b; st.append(r)
            note = f"Pop {b} and {a}, push {a} {'+' if op == 'iadd' else '×'} {b} = {r}."
        elif op == "iinc":
            i, d = [int(x) for x in arg.split(",")]; loc[i] += d; note = f"Add {d} to local {i} ({nm(i)}) in place, now {loc[i]}. The stack isn't touched."
        elif op in ("if_icmpgt", "if_icmpeq"):
            b, a = st.pop(), st.pop(); t = int(arg)
            jump = a > b if op == "if_icmpgt" else a == b
            sym = ">" if op == "if_icmpgt" else "=="
            note = f"Pop {b} and {a}. Is {a} {sym} {b}? {'Yes: jump to ' + str(t) if jump else 'No: fall through'}."
            if jump: nxt = idx[t]
        elif op == "goto":
            t = int(arg); nxt = idx[t]; note = f"Jump back to {t}."
        elif op == "ireturn":
            note = f"Pop {st[-1]} and return it to the caller. The frame is discarded (L4)."
            steps.append({"pc": pc, "locals": list(loc), "stack": list(st), "note": note}); break
        else:
            sys.exit(f"unsupported instruction {op}")
        steps.append({"pc": pc, "locals": list(loc), "stack": list(st), "note": note})
        k = nxt
    return steps

def spec(cls_dir, cls, sig, title, args):
    code, names = parse(method_block(javap(cls_dir, cls), sig))
    steps = run(code, names, args)
    return {"name": title,
            "code": [f'{c["pc"]:>2}: {c["op"]}' + (f' {c["arg"]}' if c["arg"] else "") for c in code],
            "pcs": [c["pc"] for c in code],
            "locals": [names[i] for i in range(max(names) + 1)],
            "start": [a for a in args] + [None] * (max(names) + 1 - len(args)),
            "steps": steps}

if __name__ == "__main__":
    jdir, kdir, tpl = sys.argv[1:4]
    data = {"methods": [
        spec(jdir, "Calc", "static int add(int, int);", "Java · add(2, 3)", [2, 3]),
        spec(jdir, "Calc", "static int sumTo(int);", "Java · sumTo(3)", [3]),
        spec(kdir, "CalcKt", "public static final int sumTo(int);", "Kotlin · sumTo(3)", [3]),
    ]}
    # sanity: results match the real program (add(2,3)=5, sumTo(3)=6)
    assert data["methods"][0]["steps"][-1]["stack"][-1] == 5
    assert data["methods"][1]["steps"][-1]["stack"][-1] == 6
    assert data["methods"][2]["steps"][-1]["stack"][-1] == 6
    blob = json.dumps(data, ensure_ascii=False)
    t = open(tpl).read()
    new = re.sub(r'(<script type="application/json" id="bc-calc">).*?(</script>)',
                 lambda m: m.group(1) + blob + m.group(2), t, flags=re.S)
    assert new != t or blob in t, "marker not found"
    open(tpl, "w").write(new)
    for m in data["methods"]: print(m["name"], len(m["steps"]), "steps")
