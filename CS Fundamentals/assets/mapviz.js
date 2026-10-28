/* CS Fundamentals: map what you built by hand to what a framework automates (L47).
 *
 *   <div class="mapviz" data-src="mv-1"></div>
 *   <script type="application/json" id="mv-1">{ ...spec... }</script>
 *   <script src="../assets/mapviz.js"></script>
 *
 * Spec:
 *   { "columns": ["You built (Shelf)", "Spring Boot", "Android"],
 *     "rows": [
 *       { "piece": "Router",                       // short name of the thing you built
 *         "lesson": "L45",                         // where you built it
 *         "spring": "@RestController",             // one-line Spring equivalent (row label)
 *         "cells": [ { "code": "...", "note": "..." },      // one per column; code is optional
 *                    { "code": "...", "note": "..." },
 *                    { "note": "...", "none": true } ],    // none: no honest equivalent
 *         "hides": "what the framework hides that you now understand",
 *         "misses": "what it still does not do for you" } ] }
 * Click a row (or use ↑/↓): the first two columns sit side by side, the third spans below; all stack on a phone.
 */
(function () {
  const css = `
  .mapviz, .mapviz * { font-variant-ligatures: none; }
  .mapviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .mapviz:focus-visible { border-color: var(--accent); }
  .mv-list { display: grid; gap: .25rem; margin-bottom: .7rem; }
  .mv-row { display: grid; grid-template-columns: 2.6rem minmax(0, 1fr) 1.2rem minmax(0, 1fr); align-items: center; gap: .4rem;
            font: inherit; font-size: .84rem; text-align: left; color: var(--ink); background: var(--bg);
            border: 1px solid var(--rule); border-radius: 6px; padding: .3rem .55rem; cursor: pointer; }
  .mv-row:hover { background: var(--surface); }
  .mv-row[aria-selected="true"] { border-color: var(--accent); box-shadow: inset 4px 0 0 var(--accent);
            background: color-mix(in srgb, var(--accent) 9%, var(--bg)); font-weight: 600; }
  .mv-lesson { font-family: var(--mono); font-size: .7rem; color: var(--ink-soft); }
  .mv-arrow { color: var(--ink-soft); text-align: center; }
  .mv-spring { font-family: var(--mono); font-size: .76rem; color: var(--ok); overflow-wrap: anywhere; }
  .mv-detail { display: grid; grid-template-columns: minmax(0, 1fr); gap: .6rem; }
  @media (min-width: 52rem) { .mv-detail { grid-template-columns: repeat(2, minmax(0, 1fr)); } .mv-cell.c2 { grid-column: 1 / -1; } }
  .mv-cell { border: 1px solid var(--rule); border-radius: 6px; padding: .5rem .6rem; background: var(--bg); min-width: 0; }
  .mv-cell h4 { margin: 0 0 .35rem; font-family: var(--mono); font-size: .68rem; letter-spacing: .06em; text-transform: uppercase; }
  .mv-cell.c0 h4 { color: var(--kotlin); } .mv-cell.c1 h4 { color: var(--ok); } .mv-cell.c2 h4 { color: var(--java); }
  .mv-cell pre { margin: 0 0 .4rem; padding: .45rem .55rem; font-family: var(--mono); font-size: .72rem; line-height: 1.45;
                 background: var(--code-bg); border-radius: 4px; overflow-x: auto; white-space: pre; }
  .mv-cell p { margin: 0; font-size: .86rem; }
  .mv-cell.mv-none { border-style: dashed; color: var(--ink-soft); }
  .mv-foot { margin-top: .6rem; display: grid; gap: .35rem; font-size: .88rem; }
  .mv-foot div { padding: .35rem .6rem; border-radius: 5px; background: var(--surface); }
  .mv-foot b { font-family: var(--mono); font-size: .7rem; letter-spacing: .05em; text-transform: uppercase; margin-right: .35rem; }
  .mv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); margin-top: .4rem; }
  @media print { .mv-list { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));

  document.querySelectorAll(".mapviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let cur = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="mv-list" role="listbox" aria-label="What you built, mapped to Spring Boot">${spec.rows.map((r, i) => `
        <button class="mv-row" role="option" data-i="${i}">
          <span class="mv-lesson">${esc(r.lesson)}</span><span>${esc(r.piece)}</span>
          <span class="mv-arrow">→</span><span class="mv-spring">${esc(r.spring)}</span></button>`).join("")}
      </div>
      <div class="mv-detail" aria-live="polite"></div>
      <div class="mv-foot"></div>
      <div class="mv-count"></div>`;

    function draw() {
      const r = spec.rows[cur];
      root.querySelectorAll(".mv-row").forEach((b, i) => b.setAttribute("aria-selected", String(i === cur)));
      root.querySelector(".mv-detail").innerHTML = r.cells.map((c, k) => `
        <div class="mv-cell c${k}${c.none ? " mv-none" : ""}">
          <h4>${esc(spec.columns[k])}</h4>
          ${c.code ? `<pre><code>${esc(c.code)}</code></pre>` : ""}
          <p>${esc(c.note)}</p>
        </div>`).join("");
      root.querySelector(".mv-foot").innerHTML =
        `<div><b>Hides</b>${esc(r.hides)}</div><div><b>Still yours</b>${esc(r.misses)}</div>`;
      root.querySelector(".mv-count").textContent = `row ${cur + 1} of ${spec.rows.length} · ↑/↓ to move`;
    }

    root.querySelectorAll(".mv-row").forEach((b) => b.addEventListener("click", () => { cur = Number(b.dataset.i); draw(); }));
    root.addEventListener("keydown", (e) => {
      if (e.key === "ArrowDown" || e.key === "ArrowRight") { cur = Math.min(spec.rows.length - 1, cur + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowUp" || e.key === "ArrowLeft") { cur = Math.max(0, cur - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
