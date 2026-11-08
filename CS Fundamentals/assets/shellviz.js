/* CS Fundamentals: shell widgets (L53).
 *
 * 1. Pipeline stepper: each stage's real output, one stage at a time.
 *    <div class="shv-pipe" data-src="sp-1"></div>
 *    <script type="application/json" id="sp-1">
 *      { "stages": [ { "cmd": "head -12 requests.log", "out": "...", "note": "HTML ok" }, ... ] }
 *    </script>
 *
 * 2. Permission toggler: nine rwx switches <-> symbolic, binary and octal mode, plus the chmod command.
 *    <div class="shv-perm" data-mode="644" data-file="notes.txt"></div>
 *
 *    <script src="../assets/shellviz.js"></script>
 */
(function () {
  const css = `
  .shv-pipe, .shv-perm { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .shv-pipe:focus-visible, .shv-perm:focus-visible { border-color: var(--accent); }
  .shv-cmd { font-family: var(--mono); font-size: .78rem; background: #1e1d1a; color: #8f8b7e; border-left: 3px solid var(--accent); border-radius: 5px; padding: .45rem .8rem; margin-bottom: .5rem; overflow-x: auto; white-space: pre-wrap; word-break: break-word; }
  .shv-cmd span { white-space: nowrap; }
  .shv-cmd .shv-done { color: #ecead9; }
  .shv-cmd .shv-cur { color: #fff; background: color-mix(in srgb, var(--accent) 45%, transparent); border-radius: 3px; padding: 0 .15rem; }
  .shv-cmd .shv-bar { color: #e0745c; }
  .shv-note { font-size: .9rem; min-height: 2.4rem; margin-bottom: .5rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .shv-out { font-family: var(--mono); font-size: .75rem; line-height: 1.45; background: var(--code-bg); color: var(--ink); border-radius: 5px; padding: .5rem .7rem; margin: 0; max-height: 15rem; overflow: auto; white-space: pre; }
  .shv-meta { font-family: var(--mono); font-size: .7rem; color: var(--ink-soft); margin: .3rem 0 0; }
  .shv-ctl { display: flex; align-items: center; flex-wrap: wrap; gap: .5rem; margin-top: .5rem; }
  .shv-ctl button, .shv-presets button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .shv-ctl button:disabled { opacity: .4; cursor: default; }
  .shv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  .shv-grid { display: grid; grid-template-columns: max-content repeat(3, 3.8rem); justify-content: start; gap: .3rem .4rem; align-items: center; margin-bottom: .7rem; }
  .shv-grid .shv-h { font-family: var(--mono); font-size: .7rem; color: var(--ink-soft); text-align: center; }
  .shv-grid .shv-who { font-size: .88rem; padding-right: .4rem; }
  .shv-bit { font-family: var(--mono); font-size: .9rem; font-weight: 600; border: 1px solid var(--rule); border-radius: 6px; padding: .3rem 0; cursor: pointer; background: var(--bg); color: var(--ink-soft); }
  .shv-bit[aria-pressed="true"] { background: color-mix(in srgb, var(--ok) 18%, var(--bg)); border-color: var(--ok); color: var(--ink); }
  .shv-read { display: grid; grid-template-columns: max-content 1fr; gap: .2rem .9rem; font-size: .88rem; margin-bottom: .6rem; }
  .shv-read dt { color: var(--ink-soft); }
  .shv-read dd { margin: 0; font-family: var(--mono); font-size: .85rem; }
  .shv-read dd b { color: var(--accent); }
  .shv-presets { display: flex; flex-wrap: wrap; gap: .4rem; align-items: center; }
  .shv-presets span { font-size: .8rem; color: var(--ink-soft); margin-right: .2rem; }
  .shv-presets input { font-family: var(--mono); font-size: .85rem; width: 5.6rem; padding: .2rem .4rem; border: 1px solid var(--rule); border-radius: 6px; background: var(--bg); color: var(--ink); }
  .shv-explain { font-size: .85rem; color: var(--ink-soft); margin: .6rem 0 0; }
  @media print { .shv-ctl, .shv-presets { display: none; } .shv-out { max-height: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");

  /* ---------- 1. pipeline stepper ---------- */
  document.querySelectorAll(".shv-pipe").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const stages = spec.stages;
    let step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="shv-cmd"></div>
      <div class="shv-note" aria-live="polite"></div>
      <pre class="shv-out"></pre>
      <p class="shv-meta"></p>
      <div class="shv-ctl">
        <button type="button" data-a="prev">◂ Back</button>
        <button type="button" data-a="next">Add the next stage ▸</button>
        <span class="shv-count"></span>
      </div>`;
    const cmd = root.querySelector(".shv-cmd"), note = root.querySelector(".shv-note");
    const out = root.querySelector(".shv-out"), meta = root.querySelector(".shv-meta");
    const prev = root.querySelector('[data-a="prev"]'), next = root.querySelector('[data-a="next"]');
    const count = root.querySelector(".shv-count");
    function render() {
      cmd.innerHTML = stages.map((s, i) => {
        const cls = i < step ? "shv-done" : i === step ? "shv-cur" : "";
        return (i ? '<span class="shv-bar"> | </span>' : "") + `<span class="${cls}">${i ? "" : "$ "}${esc(s.cmd)}</span>`;
      }).join("");
      note.innerHTML = stages[step].note;
      out.textContent = stages[step].out;
      const n = stages[step].out === "" ? 0 : stages[step].out.split("\n").length;
      meta.textContent = `stdout of stage ${step + 1}: ${n} line${n === 1 ? "" : "s"}` + (step < stages.length - 1 ? " → stdin of the next stage" : " → your terminal");
      prev.disabled = step === 0;
      next.disabled = step === stages.length - 1;
      count.textContent = `stage ${step + 1} / ${stages.length}`;
    }
    prev.onclick = () => { if (step > 0) { step--; render(); } };
    next.onclick = () => { if (step < stages.length - 1) { step++; render(); } };
    root.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight") { next.onclick(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { prev.onclick(); e.preventDefault(); }
    });
    render();
  });

  /* ---------- 2. permission toggler ---------- */
  const WHO = ["owner", "group", "others"], RWX = ["r", "w", "x"];
  const MEANING = { r: "read", w: "write", x: "execute" };
  document.querySelectorAll(".shv-perm").forEach((root) => {
    let mode = parseInt(root.dataset.mode || "644", 8);
    const file = root.dataset.file || "file";
    root.tabIndex = 0;
    let grid = '<div class="shv-grid"><span></span>' + RWX.map((c) => `<span class="shv-h">${MEANING[c]}</span>`).join("");
    WHO.forEach((w, wi) => {
      grid += `<span class="shv-who">${w}</span>`;
      RWX.forEach((c, ci) => {
        const bit = 1 << (8 - (wi * 3 + ci));
        grid += `<button type="button" class="shv-bit" data-bit="${bit}" aria-label="${w} ${MEANING[c]}">${c}</button>`;
      });
    });
    grid += "</div>";
    root.innerHTML = grid + `
      <dl class="shv-read">
        <dt>ls -l shows</dt><dd class="shv-sym"></dd>
        <dt>bits</dt><dd class="shv-bin"></dd>
        <dt>octal</dt><dd class="shv-oct"></dd>
        <dt>command</dt><dd class="shv-chmod"></dd>
      </dl>
      <div class="shv-presets"><span>Try:</span>
        <button type="button" data-m="644">644</button><button type="button" data-m="600">600</button>
        <button type="button" data-m="755">755</button><button type="button" data-m="700">700</button>
        <input type="text" inputmode="numeric" maxlength="3" aria-label="octal mode" placeholder="e.g. 640">
      </div>
      <p class="shv-explain"></p>`;
    const sym = root.querySelector(".shv-sym"), bin = root.querySelector(".shv-bin");
    const oct = root.querySelector(".shv-oct"), chm = root.querySelector(".shv-chmod");
    const explain = root.querySelector(".shv-explain"), input = root.querySelector("input");
    function render() {
      root.querySelectorAll(".shv-bit").forEach((b) => b.setAttribute("aria-pressed", String((mode & +b.dataset.bit) !== 0)));
      let s = "-", groups = [];
      for (let wi = 0; wi < 3; wi++) {
        let g = "";
        for (let ci = 0; ci < 3; ci++) {
          const on = (mode >> (8 - (wi * 3 + ci))) & 1;
          s += on ? RWX[ci] : "-";
          g += on;
        }
        groups.push(g);
      }
      const o = mode.toString(8).padStart(3, "0");
      sym.textContent = s;
      bin.innerHTML = groups.map((g) => `<span style="white-space:nowrap">${g} <span style="color:var(--ink-soft)">(${parseInt(g, 2)})</span></span>`).join(" · ");
      oct.innerHTML = `<b>${o}</b>`;
      chm.textContent = `chmod ${o} ${file}`;
      const who = WHO.map((w, wi) => {
        const d = (mode >> (6 - wi * 3)) & 7;
        const can = RWX.filter((c, ci) => d & (4 >> ci)).map((c) => MEANING[c]);
        return `${w}: ${can.length ? can.join(", ") : "nothing"}`;
      });
      explain.textContent = `Each octal digit is one group of three bits: r = 4, w = 2, x = 1. ${who.join("; ")}.`;
    }
    root.querySelectorAll(".shv-bit").forEach((b) => b.onclick = () => { mode ^= +b.dataset.bit; render(); });
    root.querySelectorAll("[data-m]").forEach((b) => b.onclick = () => { mode = parseInt(b.dataset.m, 8); input.value = ""; render(); });
    input.addEventListener("input", () => {
      if (/^[0-7]{3}$/.test(input.value)) { mode = parseInt(input.value, 8); render(); }
    });
    render();
  });
})();
