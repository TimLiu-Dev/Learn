/* CS Fundamentals: step-through refactoring diff viewer.
 *
 *   <div class="refactorviz" data-src="rv-1"></div>
 *   <script type="application/json" id="rv-1">{ ...spec... }</script>
 *   <script src="../assets/refactorviz.js"></script>
 *
 * Spec:
 *   { "langs": ["Kotlin", "Java"],                       // tab labels, one diff per language per step
 *     "steps": [
 *       { "name": "Rename Variable",                      // the refactoring's catalog name
 *         "url": "https://refactoring.com/catalog/renameVariable.html",
 *         "why": "HTML: what this step improves",
 *         "diffs": ["unified diff text (Kotlin)", "unified diff text (Java)"],
 *         "tests": ["11 tests completed, 11 passed", "…"],   // one per language, from real runs
 *         "ok": [true, true] } ] }
 * Diff lines starting with + / - / @@ are coloured. Left/right arrow keys step when the widget has focus.
 */
(function () {
  const css = `
  .refactorviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .refactorviz:focus-visible { border-color: var(--accent); }
  .rv-head { display: flex; flex-wrap: wrap; align-items: baseline; gap: .3rem .8rem; margin-bottom: .4rem; }
  .rv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  .rv-name { font-weight: 600; font-size: 1rem; }
  .rv-why { font-size: .9rem; margin: 0 0 .6rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .rv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .5rem; }
  .rv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .rv-tabs button[aria-selected="true"] { font-weight: 600; }
  .rv-tabs button[data-lang="0"][aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); }
  .rv-tabs button[data-lang="1"][aria-selected="true"] { border-color: var(--java); color: var(--java); }
  .rv-diff { font-family: var(--mono); font-size: .74rem; line-height: 1.45; background: var(--code-bg); border-radius: 6px; padding: .5rem 0; margin: 0; overflow-x: auto; max-height: 26rem; overflow-y: auto; }
  .rv-diff span { display: block; padding: 0 .7rem; white-space: pre; min-height: 1.45em; }
  .rv-add { color: var(--ok); background: color-mix(in srgb, var(--ok) 12%, transparent); }
  .rv-del { color: var(--accent); background: color-mix(in srgb, var(--accent) 10%, transparent); }
  .rv-hunk { color: var(--ink-soft); }
  .rv-badge { display: inline-block; font-family: var(--mono); font-size: .74rem; margin: .5rem 0 .2rem; padding: .15rem .6rem; border-radius: 999px; }
  .rv-badge.rv-pass { color: var(--ok); background: color-mix(in srgb, var(--ok) 14%, var(--bg)); }
  .rv-badge.rv-fail { color: var(--accent); background: color-mix(in srgb, var(--accent) 12%, var(--bg)); }
  .rv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; margin-top: .4rem; }
  .rv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .rv-ctl button:disabled { opacity: .4; cursor: default; }
  @media print { .rv-ctl, .rv-tabs { display: none; } .rv-diff { max-height: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  function renderDiff(text) {
    return text.split("\n").map((line) => {
      let cls = "";
      if (line.startsWith("@@")) cls = "rv-hunk";
      else if (line.startsWith("+")) cls = "rv-add";
      else if (line.startsWith("-")) cls = "rv-del";
      return `<span class="${cls}">${esc(line)}</span>`;
    }).join("");
  }

  document.querySelectorAll(".refactorviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let step = 0;
    let lang = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="rv-head"><span class="rv-count"></span><a class="rv-name" target="_blank" rel="noopener"></a></div>
      <p class="rv-why" aria-live="polite"></p>
      <div class="rv-tabs" role="tablist">${spec.langs.map((l, k) =>
        `<button role="tab" data-lang="${k}">${esc(l)}</button>`).join("")}</div>
      <pre class="rv-diff"></pre>
      <span class="rv-badge"></span>
      <div class="rv-ctl"><button data-act="back">← Back</button><button data-act="next">Next →</button></div>`;
    const $ = (sel) => root.querySelector(sel);

    function draw() {
      const s = spec.steps[step];
      $(".rv-count").textContent = `Step ${step + 1} of ${spec.steps.length}`;
      $(".rv-name").textContent = s.name;
      $(".rv-name").href = s.url || "#";
      $(".rv-why").innerHTML = s.why;
      root.querySelectorAll(".rv-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.lang) === lang)));
      $(".rv-diff").innerHTML = renderDiff(s.diffs[lang]);
      $(".rv-diff").scrollTop = 0;
      const ok = s.ok ? s.ok[lang] : true;
      const badge = $(".rv-badge");
      badge.className = "rv-badge " + (ok ? "rv-pass" : "rv-fail");
      badge.textContent = (ok ? "✓ " : "✗ ") + s.tests[lang];
      $('[data-act="back"]').disabled = step === 0;
      $('[data-act="next"]').disabled = step === spec.steps.length - 1;
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.lang !== undefined) lang = Number(b.dataset.lang);
      if (b.dataset.act === "back" && step > 0) step--;
      if (b.dataset.act === "next" && step < spec.steps.length - 1) step++;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight" && step < spec.steps.length - 1) { step++; draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft" && step > 0) { step--; draw(); e.preventDefault(); }
    });
    draw();
  });
})();
