/* CS Fundamentals: step a DFA or NFA over an input string (L51).
 *
 *   <div class="automataviz" data-src="av-1"></div>
 *   <script type="application/json" id="av-1">{ ...spec... }</script>
 *   <script src="../assets/automataviz.js"></script>
 *
 * Spec:
 *   { "kind": "dfa" | "nfa",
 *     "h": 220,                                   // viewBox height (width is always 480)
 *     "states": { "R0": [x, y], ... },             // centres
 *     "start": "R0", "accept": ["R0"],
 *     "edges": [ ["R0", "R1", "1"], ["q0", "q1", "a,b"], ["s0", "s1", "ε", -0.3], ... ],
 *                                                 // optional 4th item: this edge's curvature
 *     "bend": { "R0>R1": 0.25 },                   // optional curvature by pair (default: 0.22 if a reverse edge exists)
 *     "loop": { "R2": "below" },                   // optional: draw a state's self-loop below it
 *     "alphabet": "01", "inputs": ["110", "1001"] }
 *
 * The widget simulates the machine itself: a DFA is in one state; an NFA is in a SET of states,
 * closed under ε-moves after every step (the same algorithm as Part 2's NfaSim). A missing
 * transition means the machine drops that state; an empty set means reject.
 */
(function () {
  const css = `
  .automataviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 40rem; outline: none; }
  .automataviz:focus-visible { border-color: var(--accent); }
  .av-svg { width: 100%; height: auto; display: block; }
  .av-svg .av-edge { fill: none; stroke: var(--ink-soft); stroke-width: 1.6; }
  .av-svg .av-edge.av-hot { stroke: var(--kotlin); stroke-width: 3.2; }
  .av-svg .av-lbl { font-family: var(--mono); font-size: 14px; fill: var(--ink); text-anchor: middle; dominant-baseline: central; paint-order: stroke; stroke: var(--bg); stroke-width: 5px; }
  .av-svg .av-lbl.av-hot { fill: var(--kotlin); font-weight: 600; }
  .av-svg circle { fill: var(--bg); stroke: var(--ink-soft); stroke-width: 2; }
  .av-svg circle.av-inner { fill: none; }
  .av-svg g.av-cur circle.av-outer { fill: color-mix(in srgb, var(--accent) 24%, var(--bg)); stroke: var(--accent); stroke-width: 3.5; }
  .av-svg g.av-cur circle.av-inner { stroke: var(--accent); }
  .av-svg .av-name { font-family: var(--mono); font-size: 15px; font-weight: 600; fill: var(--ink); text-anchor: middle; dominant-baseline: central; }
  .av-svg .av-start { stroke: var(--ink); stroke-width: 1.6; }
  .av-svg marker path { fill: var(--ink-soft); }
  .av-svg marker.av-hotm path { fill: var(--kotlin); }
  .av-inputs { display: flex; gap: .4rem; flex-wrap: wrap; align-items: center; margin: .5rem 0; font-size: .82rem; }
  .av-inputs input { font-family: var(--mono); font-size: .9rem; width: 9rem; max-width: 100%; padding: .15rem .4rem; border: 1px solid var(--rule); border-radius: 4px; background: var(--bg); color: var(--ink); }
  .av-inputs button, .av-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .av-inputs button.av-preset { font-family: var(--mono); }
  .av-ctl button:disabled { opacity: .4; cursor: default; }
  .av-tape { display: flex; flex-wrap: wrap; gap: 3px; margin: .5rem 0 .3rem; font-family: var(--mono); min-height: 2rem; align-items: center; }
  .av-tape span { display: inline-block; min-width: 1.6rem; text-align: center; padding: .2rem .25rem; border: 1px solid var(--rule); border-radius: 4px; }
  .av-tape span.av-done { color: var(--ink-soft); background: var(--surface); }
  .av-tape span.av-next { border-color: var(--accent); border-width: 2px; font-weight: 600; }
  .av-tape em { font-style: normal; color: var(--ink-soft); font-size: .78rem; }
  .av-row { font-family: var(--mono); font-size: .8rem; margin: .2rem 0; }
  .av-row b { font-size: .66rem; letter-spacing: .08em; color: var(--ink-soft); margin-right: .4rem; }
  .av-verdict { font-weight: 600; }
  .av-verdict.av-yes { color: var(--ok); }
  .av-verdict.av-no { color: var(--accent); }
  .av-note { font-size: .9rem; min-height: 2.4rem; margin: .5rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .av-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .av-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .av-ctl, .av-inputs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const NS = "http://www.w3.org/2000/svg";
  const R = 22;
  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));
  let uid = 0;

  function geometry(spec) {
    const out = [];
    const keyOf = (a, b) => `${a}>${b}`;
    const has = new Set(spec.edges.map(([a, b]) => keyOf(a, b)));
    spec.edges.forEach(([a, b, label, ownBend], i) => {
      const [x1, y1] = spec.states[a], [x2, y2] = spec.states[b];
      if (a === b) {
        const below = (spec.loop || {})[a] === "below";
        const s = below ? 1 : -1;
        const d = `M ${x1 - 12} ${y1 + s * (R - 4)} C ${x1 - 34} ${y1 + s * 70}, ${x1 + 34} ${y1 + s * 70}, ${x1 + 12} ${y1 + s * (R - 4)}`;
        out.push({ i, a, b, label, d, lx: x1, ly: y1 + s * 62 });
        return;
      }
      let bend = ownBend !== undefined ? ownBend : (spec.bend || {})[keyOf(a, b)];
      if (bend === undefined) bend = has.has(keyOf(b, a)) ? 0.22 : 0;
      const dx = x2 - x1, dy = y2 - y1, len = Math.hypot(dx, dy);
      const nx = -dy / len, ny = dx / len;
      const cx = (x1 + x2) / 2 + nx * bend * len, cy = (y1 + y2) / 2 + ny * bend * len;
      // start and end on the circles, aimed at the control point
      const a1 = Math.atan2(cy - y1, cx - x1), a2 = Math.atan2(cy - y2, cx - x2);
      const sx = x1 + R * Math.cos(a1), sy = y1 + R * Math.sin(a1);
      const ex = x2 + (R + 2) * Math.cos(a2), ey = y2 + (R + 2) * Math.sin(a2);
      const d = `M ${sx} ${sy} Q ${cx} ${cy} ${ex} ${ey}`;
      // label at the curve's midpoint, nudged outwards
      const mx = 0.25 * sx + 0.5 * cx + 0.25 * ex, my = 0.25 * sy + 0.5 * cy + 0.25 * ey;
      const off = bend === 0 ? 11 : 10 * Math.sign(bend);
      out.push({ i, a, b, label, d, lx: mx + nx * off, ly: my + ny * off });
    });
    return out;
  }

  function setup(root) {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const id = `av${uid++}`;
    const h = spec.h || 220;
    const nfa = spec.kind === "nfa";
    const edges = geometry(spec);
    const names = Object.keys(spec.states);
    const accept = new Set(spec.accept);
    const [sx, sy] = spec.states[spec.start];

    const svg = `<svg class="av-svg" viewBox="0 0 480 ${h}" xmlns="${NS}" role="img" aria-label="${nfa ? "NFA" : "DFA"} diagram">
      <defs>
        <marker id="${id}-m" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z"/></marker>
        <marker id="${id}-h" class="av-hotm" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z"/></marker>
      </defs>
      <line class="av-start" x1="${sx - R - 30}" y1="${sy}" x2="${sx - R - 2}" y2="${sy}" marker-end="url(#${id}-m)"/>
      ${edges.map((e) => `<path class="av-edge" data-e="${e.i}" d="${e.d}" marker-end="url(#${id}-m)"/>`).join("")}
      ${edges.map((e) => `<text class="av-lbl" data-l="${e.i}" x="${e.lx}" y="${e.ly}">${esc(e.label)}</text>`).join("")}
      ${names.map((n) => {
        const [x, y] = spec.states[n];
        return `<g data-s="${esc(n)}"><circle class="av-outer" cx="${x}" cy="${y}" r="${R}"/>` +
          (accept.has(n) ? `<circle class="av-inner" cx="${x}" cy="${y}" r="${R - 5}"/>` : "") +
          `<text class="av-name" x="${x}" y="${y}">${esc(n)}</text></g>`;
      }).join("")}
    </svg>`;

    root.tabIndex = 0;
    root.innerHTML = svg +
      `<div class="av-inputs"><span>Input:</span><input type="text" spellcheck="false" autocomplete="off" aria-label="input string">` +
      (spec.inputs || []).map((s) => `<button class="av-preset" data-in="${esc(s)}">${s === "" ? "ε (empty)" : esc(s)}</button>`).join("") +
      `</div><div class="av-tape"></div>
       <div class="av-row"><b>${nfa ? "STATE SET" : "STATE"}</b><span class="av-set"></span></div>
       <div class="av-row"><b>VERDICT</b><span class="av-verdict"></span></div>
       <div class="av-note" aria-live="polite"></div>
       <div class="av-ctl"><button data-act="prev">◀ Back</button><button data-act="next">Step ▶</button>
       <button data-act="all">Run all</button><span class="av-count"></span></div>`;

    const field = root.querySelector("input");
    const alphabet = spec.alphabet || "";
    let input = (spec.inputs || [""])[0];
    let history = [];                                   // history[k] = { set, hot } after k characters

    function closure(set, hot) {
      const out = new Set(set), todo = [...set];
      while (todo.length) {
        const s = todo.pop();
        edges.forEach((e) => {
          if (e.a === s && e.label === "ε") { hot.add(e.i); if (!out.has(e.b)) { out.add(e.b); todo.push(e.b); } }
        });
      }
      return out;
    }

    function reset() {
      const hot = new Set();
      history = [{ set: nfa ? closure(new Set([spec.start]), hot) : new Set([spec.start]), hot, closureOnly: true }];
      field.value = input;
      draw();
    }

    function stepOnce() {
      const k = history.length - 1;
      if (k >= input.length) return;
      const c = input[k];
      const hot = new Set(), next = new Set();
      history[k].set.forEach((s) => edges.forEach((e) => {
        if (e.a === s && e.label !== "ε" && e.label.split(",").map((t) => t.trim()).includes(c)) { hot.add(e.i); next.add(e.b); }
      }));
      history.push({ set: nfa ? closure(next, hot) : next, hot });
    }

    const showSet = (set) => (set.size === 0 ? "∅ (no states left)" : nfa ? `{${[...set].join(", ")}}` : [...set][0]);

    function draw() {
      const k = history.length - 1, st = history[k];
      root.querySelectorAll("g[data-s]").forEach((g) => g.setAttribute("class", st.set.has(g.dataset.s) ? "av-cur" : ""));
      root.querySelectorAll(".av-edge").forEach((p) => {
        const hot = k > 0 && st.hot.has(Number(p.dataset.e));
        p.classList.toggle("av-hot", hot);
        p.setAttribute("marker-end", `url(#${id}-${hot ? "h" : "m"})`);
      });
      root.querySelectorAll(".av-lbl").forEach((t) => t.classList.toggle("av-hot", k > 0 && st.hot.has(Number(t.dataset.l))));
      root.querySelector(".av-tape").innerHTML = input.length === 0 ? "<em>empty input: the start state alone decides</em>" :
        [...input].map((c, i) => `<span class="${i < k ? "av-done" : i === k ? "av-next" : ""}">${esc(c)}</span>`).join("");
      root.querySelector(".av-set").textContent = showSet(st.set);
      const v = root.querySelector(".av-verdict");
      const finished = k === input.length || st.set.size === 0;
      const yes = [...st.set].some((s) => accept.has(s));
      v.className = "av-verdict" + (finished ? (yes && k === input.length ? " av-yes" : " av-no") : "");
      v.textContent = !finished ? "reading…" : (yes && k === input.length ? "ACCEPT" : "REJECT");
      const note = root.querySelector(".av-note");
      if (k === 0) {
        note.innerHTML = nfa
          ? `Start in <code>${esc(spec.start)}</code>, then follow every ε-arrow for free: the machine is in all of ${showSet(st.set)} at once. Press <b>Step</b> (or →).`
          : `Start in <code>${esc(spec.start)}</code>. Press <b>Step</b> (or →) to read one character.`;
      } else {
        const c = input[k - 1];
        note.innerHTML = st.set.size === 0
          ? `Read <code>${esc(c)}</code>: no state has an arrow for it, so the machine is stuck. Reject.`
          : `Read <code>${esc(c)}</code>: every highlighted arrow fires${nfa ? ", then ε-arrows are followed" : ""}. Now in ${showSet(st.set)}.` +
            (k === input.length ? (yes ? " Input finished in an accepting state (double circle): accept." : " Input finished, no accepting state: reject.") : "");
      }
      root.querySelector(".av-count").textContent = `char ${k}/${input.length}`;
      root.querySelector('[data-act="prev"]').disabled = k === 0;
      root.querySelector('[data-act="next"]').disabled = finished;
      root.querySelector('[data-act="all"]').disabled = finished;
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.in !== undefined) { input = b.dataset.in; reset(); return; }
      const act = b.dataset.act;
      if (act === "next") { if (history[history.length - 1].set.size) stepOnce(); }
      else if (act === "prev") { if (history.length > 1) history.pop(); }
      else if (act === "all") { while (history.length - 1 < input.length && history[history.length - 1].set.size) stepOnce(); }
      draw();
    });
    field.addEventListener("input", () => {
      const clean = [...field.value].filter((c) => !alphabet || alphabet.includes(c)).join("").slice(0, 24);
      if (clean !== field.value) field.value = clean;
      input = clean;
      const hot = new Set();
      history = [{ set: nfa ? closure(new Set([spec.start]), hot) : new Set([spec.start]), hot }];
      draw();
    });
    root.addEventListener("keydown", (e) => {
      if (e.target === field) return;
      const last = history[history.length - 1];
      if (e.key === "ArrowRight" && history.length - 1 < input.length && last.set.size) { stepOnce(); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft" && history.length > 1) { history.pop(); draw(); e.preventDefault(); }
    });
    reset();
  }

  document.querySelectorAll(".automataviz").forEach(setup);
})();
