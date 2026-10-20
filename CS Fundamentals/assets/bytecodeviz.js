/* CS Fundamentals: step-through JVM operand-stack diagram.
 *
 *   <div class="bytecodeviz" data-src="bc-1"></div>
 *   <script type="application/json" id="bc-1">{ ...spec... }</script>
 *   <script src="../assets/bytecodeviz.js"></script>
 *
 * Spec (generated from real javap output, see .build/l23/bcviz_gen.py):
 *   { "methods": [
 *       { "name": "Java · add(2, 3)",
 *         "code": [" 0: iload_0", ...],      // one line per instruction, as javap prints it
 *         "pcs": [0, 1, 2, 3],               // bytecode offset of each line
 *         "locals": ["a", "b"],              // local-variable slot names
 *         "start": [2, 3],                   // slot values before the first instruction
 *         "steps": [ { "pc": 0, "locals": [2, 3], "stack": [2], "note": "…" }, ... ] } ] }
 * Each step is the frame's state AFTER executing the instruction at pc.
 */
(function () {
  const css = `
  .bytecodeviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .bytecodeviz:focus-visible { border-color: var(--accent); }
  .bc-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .bc-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .bc-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .bc-body { display: grid; grid-template-columns: minmax(0, 1.2fr) minmax(0, 1fr); gap: .8rem; }
  @media (max-width: 34rem) { .bc-body { grid-template-columns: minmax(0, 1fr); } }
  .bc-h { font-family: var(--mono); font-size: .62rem; letter-spacing: .08em; text-transform: uppercase; color: var(--ink-soft); margin: 0 0 .3rem; }
  .bc-code { font-family: var(--mono); font-size: .76rem; line-height: 1.5; margin: 0; padding: .4rem 0; background: var(--surface); border-radius: 5px; }
  .bc-code div { padding: 0 .6rem; white-space: pre; overflow: hidden; text-overflow: ellipsis; border-left: 3px solid transparent; }
  .bc-code div.bc-now { background: color-mix(in srgb, var(--accent) 16%, var(--surface)); border-left-color: var(--accent); font-weight: 600; }
  .bc-code div.bc-next { border-left-color: var(--rule); }
  .bc-locals { border-collapse: collapse; font-family: var(--mono); font-size: .76rem; width: 100%; margin-bottom: .7rem; }
  .bc-locals td { border: 1px solid var(--rule); padding: .2rem .45rem; text-align: center; }
  .bc-locals td.bc-slot { color: var(--ink-soft); font-size: .68rem; }
  .bc-locals td.bc-changed { background: color-mix(in srgb, var(--kotlin) 14%, var(--bg)); color: var(--kotlin); font-weight: 600; }
  .bc-stack { display: flex; flex-direction: column-reverse; gap: .25rem; min-height: 5.6rem; justify-content: flex-start; padding: .4rem; border: 2px solid var(--rule); border-top: none; border-radius: 0 0 6px 6px; max-width: 9rem; }
  .bc-stack span { font-family: var(--mono); font-size: .8rem; text-align: center; padding: .15rem .4rem; border-radius: 4px; background: color-mix(in srgb, var(--java) 14%, var(--bg)); color: var(--java); border: 1px solid color-mix(in srgb, var(--java) 40%, var(--bg)); }
  .bc-stack span.bc-top { font-weight: 700; }
  .bc-empty { font-family: var(--mono); font-size: .7rem; color: var(--ink-soft); text-align: center; }
  .bc-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .bc-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .bc-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .bc-ctl button:disabled { opacity: .4; cursor: default; }
  .bc-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .bc-ctl, .bc-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
  const show = (v) => (v === null || v === undefined ? "–" : String(v));

  document.querySelectorAll(".bytecodeviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let m = 0;      // method index
    let step = 0;   // number of instructions executed
    root.tabIndex = 0;

    root.innerHTML = `
      <div class="bc-tabs" role="tablist">${spec.methods.map((x, k) =>
        `<button role="tab" data-m="${k}">${esc(x.name)}</button>`).join("")}</div>
      <div class="bc-body">
        <div><p class="bc-h">bytecode (javap -c)</p><div class="bc-code"></div></div>
        <div>
          <p class="bc-h">local variables</p><table class="bc-locals"></table>
          <p class="bc-h">operand stack (top ↑)</p><div class="bc-stack"></div>
        </div>
      </div>
      <div class="bc-note" aria-live="polite"></div>
      <div class="bc-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="end">Run to return</button>
        <span class="bc-count"></span>
      </div>`;

    function draw() {
      const x = spec.methods[m];
      const cur = step > 0 ? x.steps[step - 1] : null;
      const prevLocals = step > 1 ? x.steps[step - 2].locals : x.start;
      const locals = cur ? cur.locals : x.start;
      const stack = cur ? cur.stack : [];
      const nextPc = step < x.steps.length ? x.steps[step].pc : null;

      root.querySelector(".bc-code").innerHTML = x.code.map((line, i) => {
        const pc = x.pcs[i];
        const cls = cur && pc === cur.pc ? "bc-now" : (pc === nextPc ? "bc-next" : "");
        return `<div class="${cls}">${esc(line)}</div>`;
      }).join("");
      root.querySelector(".bc-locals").innerHTML =
        "<tr>" + x.locals.map((_, i) => `<td class="bc-slot">${i}</td>`).join("") + "</tr>" +
        "<tr>" + x.locals.map((n) => `<td class="bc-slot">${esc(n)}</td>`).join("") + "</tr>" +
        "<tr>" + locals.map((v, i) =>
          `<td class="${cur && v !== prevLocals[i] ? "bc-changed" : ""}">${esc(show(v))}</td>`).join("") + "</tr>";
      root.querySelector(".bc-stack").innerHTML = stack.length
        ? stack.map((v, i) => `<span class="${i === stack.length - 1 ? "bc-top" : ""}">${esc(v)}</span>`).join("")
        : '<div class="bc-empty">empty</div>';
      root.querySelector(".bc-note").innerHTML = cur
        ? `<b>${esc(x.code[x.pcs.indexOf(cur.pc)].trim())}</b>: ${esc(cur.note)}`
        : "A new frame: the arguments are already in the local-variable slots, and the operand stack is empty. Press <b>Step</b>.";
      root.querySelector(".bc-count").textContent = `instruction ${step}/${x.steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === x.steps.length;
      root.querySelector('[data-act="end"]').disabled = step === x.steps.length;
      root.querySelectorAll(".bc-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.m) === m)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = spec.methods[m].steps.length;
      if (b.dataset.m !== undefined) { m = Number(b.dataset.m); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(n, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "end") step = n;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = spec.methods[m].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
