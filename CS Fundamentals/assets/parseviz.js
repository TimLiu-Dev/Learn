/* CS Fundamentals: step one program through a whole language pipeline (L54).
 *
 *   <div class="parseviz" data-src="pv-1" data-stage="0"></div>   // data-stage: 0 scan, 1 tree, 2 run (optional)
 *   <script type="application/json" id="pv-1">{ "programs": [ ...spec... ] }</script>
 *   <script src="../assets/parseviz.js"></script>
 *
 * Each program spec is produced by running the lesson's real Java pipeline (.build/l54/VizDump.java):
 *   { "src": "let x = 3; x * (x + 1)",
 *     "tokens": [ { "type": "LET", "text": "let", "at": "1:1", "start": 0, "end": 3 }, ... ],
 *     "nodes": [ { "label": "*", "kind": "op", "kids": [2, 5] }, ... ], "root": 7,
 *     "order": [ { "id": 0, "value": 3, "pc": 0, "instr": "push 3" }, ... ],   // post-order = evaluation order
 *     "code": [ { "pc": 0, "text": "push 3" }, ... ], "locals": ["x"], "maxStack": 3,
 *     "steps": [ { "pc": 0, "stack": [3], "locals": [null], "note": "..." }, ... ], "result": 12 }
 *
 * Three stages, each stepped with Back/Next (or the arrow keys): scan the text into tokens,
 * walk the tree in post-order (which is both the interpreter's evaluation order and the compiler's
 * emission order), then run the bytecode on the stack machine.
 */
(function () {
  const css = `
  .parseviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 40rem; outline: none; }
  .parseviz:focus-visible { border-color: var(--accent); }
  .pz-row { display: flex; gap: .4rem; flex-wrap: wrap; align-items: center; margin: 0 0 .55rem; font-size: .82rem; }
  .pz-row b { font-size: .66rem; letter-spacing: .08em; color: var(--ink-soft); margin-right: .2rem; }
  .pz-row button { font: inherit; font-size: .8rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .15rem .7rem; cursor: pointer; }
  .pz-row button.pz-preset { font-family: var(--mono); }
  .pz-row button.pz-on { border-color: var(--accent); color: var(--accent); font-weight: 600; }
  .pz-row button:disabled { opacity: .4; cursor: default; }
  .pz-src { font-family: var(--mono); font-size: .92rem; padding: .45rem .6rem; background: var(--code-bg); border-radius: 5px; white-space: pre-wrap; word-break: break-word; margin: 0 0 .55rem; }
  .pz-src .pz-done { color: var(--ink-soft); }
  .pz-src .pz-cur { background: color-mix(in srgb, var(--accent) 26%, transparent); outline: 1.5px solid var(--accent); border-radius: 2px; }
  .pz-toks { display: flex; flex-wrap: wrap; gap: 4px; min-height: 2.2rem; margin: 0 0 .4rem; }
  .pz-tok { font-family: var(--mono); font-size: .72rem; border: 1px solid var(--rule); border-radius: 4px; padding: .1rem .35rem; line-height: 1.35; }
  .pz-tok i { font-style: normal; color: var(--ink-soft); font-size: .62rem; display: block; }
  .pz-tok.pz-cur { border-color: var(--accent); border-width: 2px; }
  .pz-svg { width: 100%; height: auto; display: block; margin: 0 0 .3rem; }
  .pz-svg line { stroke: var(--rule); stroke-width: 1.6; }
  .pz-svg rect { fill: var(--bg); stroke: var(--ink-soft); stroke-width: 1.4; }
  .pz-svg g.pz-root rect { stroke-dasharray: 4 3; }
  .pz-svg g.pz-seen rect { fill: var(--surface); stroke: var(--ok); }
  .pz-svg g.pz-cur rect { fill: color-mix(in srgb, var(--accent) 22%, var(--bg)); stroke: var(--accent); stroke-width: 2.6; }
  .pz-svg text { font-family: var(--mono); font-size: 13px; fill: var(--ink); text-anchor: middle; dominant-baseline: central; }
  .pz-svg text.pz-val { font-size: 11px; fill: var(--ok); font-weight: 600; }
  .pz-svg g.pz-cur text.pz-val { fill: var(--accent); }
  .pz-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(11rem, 1fr)); gap: .8rem; margin: 0 0 .3rem; }
  .pz-code { font-family: var(--mono); font-size: .8rem; margin: 0; padding: 0; list-style: none; }
  .pz-code li { padding: .05rem .4rem; border-left: 3px solid transparent; white-space: pre; }
  .pz-code li.pz-cur { border-left-color: var(--accent); background: color-mix(in srgb, var(--accent) 16%, transparent); font-weight: 600; }
  .pz-label { font-size: .64rem; letter-spacing: .08em; color: var(--ink-soft); margin: 0 0 .2rem; font-weight: 600; }
  .pz-stack { display: flex; flex-direction: column-reverse; gap: 3px; min-height: 2rem; margin: 0 0 .6rem; }
  .pz-stack span, .pz-locals span { font-family: var(--mono); font-size: .82rem; border: 1px solid var(--rule); border-radius: 4px; padding: .12rem .45rem; background: var(--surface); }
  .pz-stack span:last-child { border-color: var(--kotlin); }
  .pz-stack em, .pz-locals em { font-style: normal; color: var(--ink-soft); font-size: .78rem; }
  .pz-locals { display: flex; flex-wrap: wrap; gap: 4px; }
  .pz-note { font-size: .9rem; min-height: 2.4rem; margin: .45rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .pz-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .pz-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .pz-ctl button:disabled { opacity: .4; cursor: default; }
  .pz-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .pz-ctl, .pz-row { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const NS = "http://www.w3.org/2000/svg";
  const STAGES = ["1 · Scan", "2 · Parse & evaluate", "3 · Compile & run"];

  function el(tag, cls, text) {
    const e = document.createElement(tag);
    if (cls) e.className = cls;
    if (text != null) e.textContent = text;
    return e;
  }
  function svg(tag, attrs) {
    const e = document.createElementNS(NS, tag);
    for (const k in attrs) e.setAttribute(k, attrs[k]);
    return e;
  }

  // Leaves get consecutive columns left to right; a parent sits over the middle of its children.
  function layout(p) {
    const pos = {};
    let col = 0, maxDepth = 0;
    (function place(id, depth) {
      const n = p.nodes[id];
      maxDepth = Math.max(maxDepth, depth);
      if (n.kids.length === 0) pos[id] = { x: col++, y: depth };
      else {
        n.kids.forEach(k => place(k, depth + 1));
        const xs = n.kids.map(k => pos[k].x);
        pos[id] = { x: (Math.min(...xs) + Math.max(...xs)) / 2, y: depth };
      }
    })(p.root, 0);
    return { pos, cols: col, depth: maxDepth };
  }

  function mount(host) {
    const spec = JSON.parse(document.getElementById(host.dataset.src).textContent);
    let prog = 0, stage = Number(host.dataset.stage || 0), step = 0;
    host.tabIndex = 0;

    const presets = el("div", "pz-row");
    presets.appendChild(el("b", null, "PROGRAM"));
    const presetButtons = spec.programs.map((p, i) => {
      const b = el("button", "pz-preset", p.src);
      b.type = "button";
      b.addEventListener("click", () => { prog = i; step = 0; render(); });
      presets.appendChild(b);
      return b;
    });
    const tabs = el("div", "pz-row");
    tabs.appendChild(el("b", null, "STAGE"));
    const tabButtons = STAGES.map((name, i) => {
      const b = el("button", null, name);
      b.type = "button";
      b.addEventListener("click", () => { stage = i; step = 0; render(); });
      tabs.appendChild(b);
      return b;
    });
    const body = el("div");
    const note = el("div", "pz-note");
    const ctl = el("div", "pz-ctl");
    const back = el("button", null, "◀ Back"), next = el("button", null, "Next ▶"), reset = el("button", null, "Reset");
    [back, next, reset].forEach(b => { b.type = "button"; ctl.appendChild(b); });
    const count = el("span", "pz-count");
    ctl.appendChild(count);
    host.append(presets, tabs, body, note, ctl);

    const steps = () => {
      const p = spec.programs[prog];
      return stage === 0 ? p.tokens.length : stage === 1 ? p.order.length : p.steps.length;
    };
    back.addEventListener("click", () => { if (step > 0) { step--; render(); } });
    next.addEventListener("click", () => { if (step < steps()) { step++; render(); } });
    reset.addEventListener("click", () => { step = 0; render(); });
    host.addEventListener("keydown", e => {
      if (e.key === "ArrowRight") { next.click(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { back.click(); e.preventDefault(); }
    });

    function sourceView(p, from, to, doneTo) {
      const box = el("div", "pz-src");
      const s = p.src;
      if (doneTo > 0) box.appendChild(el("span", "pz-done", s.slice(0, doneTo)));
      if (to > from) {
        box.appendChild(document.createTextNode(s.slice(doneTo, from)));
        box.appendChild(el("span", "pz-cur", s.slice(from, to)));
        box.appendChild(document.createTextNode(s.slice(to)));
      } else box.appendChild(document.createTextNode(s.slice(doneTo)));
      return box;
    }

    function renderScan(p) {
      const cur = step > 0 ? p.tokens[step - 1] : null;
      const prevEnd = step > 1 ? p.tokens[step - 2].end : 0;
      body.appendChild(cur ? sourceView(p, cur.start, cur.end, prevEnd) : sourceView(p, 0, 0, 0));
      const toks = el("div", "pz-toks");
      p.tokens.slice(0, step).forEach((t, i) => {
        const chip = el("span", "pz-tok" + (i === step - 1 ? " pz-cur" : ""));
        chip.appendChild(el("i", null, t.type));
        chip.appendChild(document.createTextNode(t.type === "EOF" ? "∎" : t.text));
        toks.appendChild(chip);
      });
      body.appendChild(toks);
      if (!cur) note.textContent = "The scanner reads characters left to right. Press Next to emit the first token.";
      else if (cur.type === "EOF") note.textContent = `End of input at ${cur.at}: the scanner adds an EOF token so the parser always has something to look at. ${p.tokens.length} tokens in all.`;
      else {
        const skipped = p.src.slice(prevEnd, cur.start).trim();
        note.textContent = `${cur.type} '${cur.text}' at ${cur.at}.` + (skipped ? ` Skipped a comment first.` : "")
          + (cur.type === "IDENT" || cur.type === "LET" || cur.type === "NUMBER" ? " Maximal munch: it kept reading while the next character could still belong to this token." : "");
      }
    }

    function renderTree(p) {
      const { pos, cols, depth } = layout(p);
      const dx = 64, dy = 74, w = Math.max(cols * dx, 200), h = (depth + 1) * dy + 14;
      const s = svg("svg", { viewBox: `0 0 ${w} ${h}`, class: "pz-svg", role: "img", "aria-label": "syntax tree" });
      const X = id => pos[id].x * dx + dx / 2 + (w - cols * dx) / 2, Y = id => pos[id].y * dy + 22;
      p.nodes.forEach((n, id) => n.kids.forEach(k => s.appendChild(svg("line", { x1: X(id), y1: Y(id) + (n.kind === "root" ? 12 : 31), x2: X(k), y2: Y(k) - 12 }))));
      const seen = new Map(p.order.slice(0, step).map(o => [o.id, o]));
      const curId = step > 0 ? p.order[step - 1].id : -1;
      p.nodes.forEach((n, id) => {
        const g = svg("g", { class: (n.kind === "root" ? "pz-root " : "") + (id === curId ? "pz-cur" : seen.has(id) ? "pz-seen" : "") });
        const bw = Math.max(30, n.label.length * 8.5 + 14);
        g.appendChild(svg("rect", { x: X(id) - bw / 2, y: Y(id) - 12, width: bw, height: 24, rx: 5 }));
        const t = svg("text", { x: X(id), y: Y(id) });
        t.textContent = n.label;
        g.appendChild(t);
        if (seen.has(id)) {
          const v = svg("text", { x: X(id), y: Y(id) + 22, class: "pz-val" });
          v.textContent = "= " + seen.get(id).value;
          g.appendChild(v);
        }
        s.appendChild(g);
      });
      body.appendChild(sourceView(p, 0, 0, 0));
      body.appendChild(s);
      if (step === 0) note.textContent = "The parser built this tree from the tokens. Press Next to evaluate it bottom-up: children before parents (post-order).";
      else {
        const o = p.order[step - 1], n = p.nodes[o.id];
        const what = n.kind === "num" ? `A number evaluates to itself: ${o.value}.`
          : n.kind === "var" ? `Look up ${n.label} in the environment: ${o.value}.`
          : n.kind === "let" ? `Bind ${n.label.slice(4)} = ${o.value} in the environment.`
          : `Both operands are done, so ${n.label} can run: ${o.value}.`;
        note.textContent = `${what} The compiler, walking in the same order, emits "${o.instr}" here (pc ${o.pc}).`
          + (step === p.order.length ? ` Result: ${p.result}.` : "");
      }
    }

    function renderRun(p) {
      const st = step > 0 ? p.steps[step - 1] : null;
      const grid = el("div", "pz-grid");
      const left = el("div");
      left.appendChild(el("div", "pz-label", `BYTECODE · stack=${p.maxStack}, locals=${p.locals.length}`));
      const ol = el("ol", "pz-code");
      p.code.forEach(c => ol.appendChild(el("li", st && st.pc === c.pc ? "pz-cur" : "", `${String(c.pc).padStart(2)}: ${c.text}`)));
      left.appendChild(ol);
      const right = el("div");
      right.appendChild(el("div", "pz-label", "OPERAND STACK · top outlined"));
      const stack = el("div", "pz-stack");
      const values = st ? st.stack : [];
      if (values.length === 0) stack.appendChild(el("em", null, "empty"));
      values.forEach(v => stack.appendChild(el("span", null, v)));
      right.appendChild(stack);
      right.appendChild(el("div", "pz-label", "LOCALS"));
      const locals = el("div", "pz-locals");
      if (p.locals.length === 0) locals.appendChild(el("em", null, "none"));
      p.locals.forEach((name, i) => {
        const v = st ? st.locals[i] : null;
        locals.appendChild(el("span", null, `${i} ${name} = ${v == null ? "·" : v}`));
      });
      right.appendChild(locals);
      grid.append(left, right);
      body.appendChild(grid);
      note.textContent = st ? st.note : "The VM starts at pc 0 with an empty stack. Each Next is one trip round the fetch–decode–execute loop.";
    }

    function render() {
      const p = spec.programs[prog];
      presetButtons.forEach((b, i) => b.classList.toggle("pz-on", i === prog));
      tabButtons.forEach((b, i) => b.classList.toggle("pz-on", i === stage));
      body.textContent = "";
      [renderScan, renderTree, renderRun][stage](p);
      back.disabled = step === 0;
      next.disabled = step === steps();
      count.textContent = `step ${step} / ${steps()}`;
    }
    render();
  }

  document.querySelectorAll(".parseviz").forEach(mount);
})();
