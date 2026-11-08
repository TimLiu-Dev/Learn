/* CS Fundamentals: a small sequence-diagram stepper (L55).
 *
 *   <div class="authviz" data-src="av-1"></div>
 *   <script type="application/json" id="av-1">{"scenarios":[{"name":"…","actors":["A","B"],
 *     "steps":[{"from":0,"to":1,"label":"GET /","note":"…"}]}]}</script>
 *   <script src="../assets/authviz.js"></script>
 *
 * Each step is one message between two lanes. Steps appear one at a time.
 */
(function () {
  const css = `
  .authviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; outline: none; }
  .authviz:focus-visible { border-color: var(--accent); }
  .av-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .av-tabs button, .av-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .av-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .av-ctl button:disabled { opacity: .4; cursor: default; }
  .av-grid { display: grid; grid-template-columns: repeat(var(--n), minmax(0, 1fr)); }
  .av-head { font-family: var(--mono); font-size: .7rem; font-weight: 600; text-align: center; padding: .25rem .1rem; border-bottom: 2px solid var(--rule); overflow-wrap: anywhere; }
  .av-row { position: relative; padding: .3rem 0 .15rem; }
  .av-row::before { content: ""; position: absolute; inset: 0; background-image: repeating-linear-gradient(to right, transparent 0, transparent calc(100% / var(--n) / 2 - .5px), var(--rule) calc(100% / var(--n) / 2 - .5px), var(--rule) calc(100% / var(--n) / 2 + .5px), transparent calc(100% / var(--n) / 2 + .5px), transparent calc(100% / var(--n))); }
  .av-msg { position: relative; min-height: 1.6rem; display: flex; flex-direction: column; justify-content: flex-end; }
  .av-label { font-family: var(--mono); font-size: .66rem; line-height: 1.15; text-align: center; color: var(--ink); overflow-wrap: anywhere; padding: 0 .2rem .15rem; }
  .av-line { height: 2px; background: var(--ink-soft); position: relative; }
  .av-line::after { content: ""; position: absolute; top: -4px; border: 5px solid transparent; }
  .av-msg.av-r .av-line::after { right: -2px; border-left-color: var(--ink-soft); border-right-width: 0; }
  .av-msg.av-l .av-line::after { left: -2px; border-right-color: var(--ink-soft); border-left-width: 0; }
  .av-msg.av-now .av-line { background: var(--accent); }
  .av-msg.av-now.av-r .av-line::after { border-left-color: var(--accent); }
  .av-msg.av-now.av-l .av-line::after { border-right-color: var(--accent); }
  .av-msg.av-now .av-label { font-weight: 600; }
  .av-msg.av-self { justify-content: center; }
  .av-msg.av-self .av-label { border: 1px solid var(--rule); border-radius: 4px; background: var(--surface); padding: .15rem .3rem; }
  .av-msg.av-self.av-now .av-label { border-color: var(--accent); }
  .av-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .av-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .av-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .av-ctl, .av-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".authviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let sc = 0, shown = 1;
    root.tabIndex = 0;
    const tabs = spec.scenarios.length < 2 ? "" : `<div class="av-tabs" role="tablist">${spec.scenarios.map((s, k) =>
        `<button role="tab" data-sc="${k}">${esc(s.name)}</button>`).join("")}</div>`;
    root.innerHTML = `${tabs}
      <div class="av-body"></div><div class="av-note" aria-live="polite"></div>
      <div class="av-ctl"><button data-a="prev">← Back</button><button data-a="next">Next →</button>
      <button data-a="reset">Reset</button><span class="av-count"></span></div>`;

    function render() {
      const s = spec.scenarios[sc], n = s.actors.length;
      root.style.setProperty("--n", n);
      const rows = s.steps.slice(0, shown).map((st, i) => {
        const lo = Math.min(st.from, st.to), hi = Math.max(st.from, st.to);
        const now = i === shown - 1 ? " av-now" : "";
        if (lo === hi) {                                   // a self-step: a box wider than its lane, kept inside the grid
          const w = Math.min(100, 160 / n), left = Math.max(0, Math.min(100 - w, ((lo + 0.5) / n) * 100 - w / 2));
          return `<div class="av-row"><div class="av-msg av-self${now}" style="margin-left:${left}%;width:${w}%"><span class="av-label">${esc(st.label)}</span></div></div>`;
        }
        const left = ((lo + 0.5) / n) * 100, width = ((hi - lo) / n) * 100;
        const dir = st.to > st.from ? " av-r" : " av-l";
        return `<div class="av-row"><div class="av-msg${dir}${now}" style="margin-left:${left}%;width:${width}%"><span class="av-label">${esc(st.label)}</span><span class="av-line"></span></div></div>`;
      }).join("");
      root.querySelector(".av-body").innerHTML =
        `<div class="av-grid">${s.actors.map((a) => `<div class="av-head">${esc(a)}</div>`).join("")}</div>${rows}`;
      root.querySelector(".av-note").textContent = s.steps[shown - 1].note || "";
      root.querySelector(".av-count").textContent = `step ${shown} of ${s.steps.length}`;
      root.querySelector('[data-a="prev"]').disabled = shown <= 1;
      root.querySelector('[data-a="next"]').disabled = shown >= s.steps.length;
      root.querySelectorAll(".av-tabs button").forEach((b, k) => b.setAttribute("aria-selected", String(k === sc)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.sc !== undefined) { sc = +b.dataset.sc; shown = 1; }
      else if (b.dataset.a === "next") shown = Math.min(shown + 1, spec.scenarios[sc].steps.length);
      else if (b.dataset.a === "prev") shown = Math.max(shown - 1, 1);
      else if (b.dataset.a === "reset") shown = 1;
      render();
    });
    root.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight") { shown = Math.min(shown + 1, spec.scenarios[sc].steps.length); render(); }
      if (e.key === "ArrowLeft") { shown = Math.max(shown - 1, 1); render(); }
    });
    render();
  });
})();
