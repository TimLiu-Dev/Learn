/* CS Fundamentals: two-thread interleaving stepper for count++ (lost updates, and how a lock fixes them).
 *
 *   <div class="threadviz" data-src="tv-1"></div>
 *   <script type="application/json" id="tv-1">{ ...spec... }</script>
 *   <script src="../assets/threadviz.js"></script>
 *
 * Spec:
 *   { "start": 41,                                   // initial value of the shared field
 *     "ops": ["getfield count", "iconst_1; iadd", "putfield count"],   // count++ as bytecode (from javap)
 *     "modes": [
 *       { "name": "No lock", "lock": false, "script": ["A","B","A","A","B","B"] },
 *       { "name": "synchronized", "lock": true, "script": ["A","B","A","A","A","A","B","B","B","B","B"] } ] }
 * With "lock": true each thread runs monitorenter first and monitorexit last.
 * The learner either clicks a lane's button to run that thread's next instruction,
 * or steps through the mode's scripted interleaving. A blocked monitorenter consumes no instruction.
 */
(function () {
  const css = `
  .threadviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; }
  .tv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .tv-tabs button, .tv-ctl button, .tv-lane button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .tv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .tv-ctl button:disabled, .tv-lane button:disabled { opacity: .4; cursor: default; }
  .tv-mem { display: flex; justify-content: center; gap: 1rem; flex-wrap: wrap; align-items: center; margin: .2rem 0 .7rem; font-family: var(--mono); font-size: .85rem; }
  .tv-cell { border: 2px solid var(--ink-soft); border-radius: 6px; padding: .3rem .8rem; background: var(--code-bg); }
  .tv-cell b { font-size: 1.1rem; }
  .tv-cell.tv-flash { border-color: var(--accent); }
  .tv-lock { color: var(--ink-soft); }
  .tv-lock.tv-held { color: var(--java); font-weight: 600; }
  .tv-lanes { display: grid; grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); gap: .6rem; }
  .tv-lane { border: 1px solid var(--rule); border-radius: 6px; padding: .5rem; min-width: 0; }
  .tv-lane h4 { margin: 0 0 .3rem; font-size: .85rem; display: flex; justify-content: space-between; gap: .3rem; flex-wrap: wrap; }
  .tv-status { font-family: var(--mono); font-size: .68rem; font-weight: 400; color: var(--ink-soft); }
  .tv-status.tv-blocked { color: var(--accent); font-weight: 600; }
  .tv-status.tv-done { color: var(--ok); }
  .tv-lane ol { list-style: none; margin: 0 0 .4rem; padding: 0; font-family: var(--mono); font-size: .74rem; }
  .tv-lane li { padding: .12rem .3rem; border-radius: 4px; color: var(--ink-soft); overflow-wrap: anywhere; }
  .tv-lane li.tv-past { color: var(--ink); text-decoration: line-through; text-decoration-color: var(--rule); }
  .tv-lane li.tv-next { color: var(--ink); background: color-mix(in srgb, var(--kotlin) 14%, var(--bg)); font-weight: 600; }
  .tv-stack { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); margin-bottom: .4rem; }
  .tv-stack b { color: var(--ink); }
  .tv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .tv-note.tv-bad { border-left: 3px solid var(--accent); }
  .tv-note.tv-good { border-left: 3px solid var(--ok); }
  .tv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .tv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .tv-ctl, .tv-tabs, .tv-lane button { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".threadviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let mode = 0, st;

    function program() {
      return spec.modes[mode].lock ? ["monitorenter", ...spec.ops, "monitorexit"] : spec.ops.slice();
    }
    function reset() {
      st = { count: spec.start, owner: null, k: 0, flash: false,
             t: { A: { pc: 0, val: null, blocked: false }, B: { pc: 0, val: null, blocked: false } },
             note: "Each thread will run count++ once, so the right answer is " + (spec.start + 2) +
                   ". Click a thread's button to run its next instruction, or step through the scripted interleaving.",
             tone: "" };
    }

    function exec(name) {
      const prog = program(), th = st.t[name], other = name === "A" ? "B" : "A";
      if (th.pc >= prog.length) return;
      const op = prog[th.pc];
      st.flash = false;
      if (op === "monitorenter") {
        if (st.owner && st.owner !== name) {
          th.blocked = true;
          st.note = `${name} wants the lock, but ${st.owner} holds it, so ${name} is BLOCKED and runs nothing.`;
          st.tone = "";
          return;
        }
        st.owner = name; th.blocked = false;
        st.note = `${name} acquires the lock. Nobody else can enter until ${name} runs monitorexit.`;
      } else if (op === "monitorexit") {
        st.owner = null;
        st.note = `${name} releases the lock.` + (st.t[other].blocked ? ` ${other} can now acquire it.` : "");
      } else if (op.startsWith("getfield")) {
        th.val = st.count;
        st.note = `${name} reads count from memory onto its own operand stack: ${th.val}.`;
      } else if (op.startsWith("putfield")) {
        const lost = st.count !== th.val - 1;
        st.count = th.val; st.flash = true;
        st.note = `${name} writes ${th.val} back to memory.` +
          (lost ? ` It never saw the other thread's write, so that increment is overwritten.` : "");
      } else {
        th.val = th.val + 1;
        st.note = `${name} adds 1 on its own stack: ${th.val}. Memory still says ${st.count}.`;
      }
      st.tone = "";
      th.pc++;
      if (st.t.A.pc >= prog.length && st.t.B.pc >= prog.length) {
        const ok = st.count === spec.start + 2;
        st.note += ok ? ` Both finished: count = ${st.count}, correct.`
                      : ` Both finished: count = ${st.count}, but it should be ${spec.start + 2}. Lost update!`;
        st.tone = ok ? "tv-good" : "tv-bad";
      }
    }

    root.innerHTML = `
      <div class="tv-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <div class="tv-mem"><span class="tv-cell">count = <b></b></span><span class="tv-lock"></span></div>
      <div class="tv-lanes">${["A", "B"].map((n) => `
        <div class="tv-lane" data-t="${n}">
          <h4>Thread ${n} <span class="tv-status"></span></h4>
          <ol></ol>
          <div class="tv-stack"></div>
          <button data-run="${n}">Run ${n}'s next step</button>
        </div>`).join("")}</div>
      <div class="tv-note" aria-live="polite"></div>
      <div class="tv-ctl">
        <button data-act="script">Scripted step ▶</button>
        <button data-act="reset">Reset</button>
        <span class="tv-count"></span>
      </div>`;

    function draw() {
      const prog = program(), script = spec.modes[mode].script;
      root.querySelectorAll(".tv-tabs button").forEach((b, k) => b.setAttribute("aria-selected", String(k === mode)));
      root.querySelector(".tv-cell b").textContent = st.count;
      root.querySelector(".tv-cell").classList.toggle("tv-flash", st.flash);
      const lock = root.querySelector(".tv-lock");
      lock.style.display = spec.modes[mode].lock ? "" : "none";
      lock.textContent = st.owner ? `lock held by ${st.owner}` : "lock free";
      lock.classList.toggle("tv-held", !!st.owner);
      ["A", "B"].forEach((n) => {
        const th = st.t[n], lane = root.querySelector(`.tv-lane[data-t="${n}"]`);
        lane.querySelector("ol").innerHTML = prog.map((op, i) =>
          `<li class="${i < th.pc ? "tv-past" : i === th.pc ? "tv-next" : ""}">${esc(op)}</li>`).join("");
        lane.querySelector(".tv-stack").innerHTML = `stack: <b>${th.val === null ? "—" : th.val}</b>`;
        const s = lane.querySelector(".tv-status");
        const done = th.pc >= prog.length;
        s.textContent = done ? "TERMINATED" : th.blocked ? "BLOCKED" : th.pc === 0 ? "ready" : "RUNNABLE";
        s.className = "tv-status" + (done ? " tv-done" : th.blocked ? " tv-blocked" : "");
        lane.querySelector("button").disabled = done;
      });
      const note = root.querySelector(".tv-note");
      note.textContent = st.note;
      note.className = "tv-note " + st.tone;
      root.querySelector('[data-act="script"]').disabled = st.k >= script.length;
      root.querySelector(".tv-count").textContent = `script ${st.k}/${script.length}: ${script.join(" ")}`;
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.mode) { mode = +b.dataset.mode; reset(); }
      else if (b.dataset.run) { st.k = Infinity; exec(b.dataset.run); }   // manual play leaves the script
      else if (b.dataset.act === "reset") reset();
      else if (b.dataset.act === "script") { const s = spec.modes[mode].script; if (st.k < s.length) exec(s[st.k++]); }
      if (st.k === Infinity) st.k = spec.modes[mode].script.length;
      draw();
    });
    reset();
    draw();
  });
})();
