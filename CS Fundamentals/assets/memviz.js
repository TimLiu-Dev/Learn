/* CS Fundamentals: step-through stack & heap visualizer.
 *
 *   <div class="memviz" data-src="viz-1"></div>
 *   <script type="application/json" id="viz-1">{ ...spec... }</script>
 *   <script src="../assets/memviz.js"></script>
 *
 * Spec:
 *   {
 *     "kotlin": ["line 1", "line 2", ...],      // code listings (either or both)
 *     "java":   ["line 1", ...],
 *     "steps": [
 *       { "kt": 3, "jv": 4,                      // 1-based line to highlight in each listing
 *         "note": "What just happened (HTML ok)",
 *         "stack": [ { "name": "main", "vars": [["x", "5"], ["c", "@o1"], ["d", "null"]] } ],
 *         "heap":  [ { "id": "o1", "type": "Counter", "fields": [["count", "1"]] } ] }
 *     ]
 *   }
 * Stack frames are listed bottom-first (main first) and drawn with the newest frame on top.
 * A value starting with "@" is a reference to the heap object with that id and is drawn as an arrow.
 * Values that changed since the previous step are highlighted.
 */
(function () {
  const SVGNS = "http://www.w3.org/2000/svg";
  let preferred = "kotlin";
  try { preferred = localStorage.getItem("csf:memviz-lang") || "kotlin"; } catch (e) { /* ignore */ }

  document.querySelectorAll(".memviz").forEach((root, vizIndex) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const langs = ["kotlin", "java"].filter((l) => spec[l]);
    let lang = langs.includes(preferred) ? preferred : langs[0];
    let step = 0;
    const markerId = `mv-arrow-${vizIndex}`;

    root.innerHTML = `
      <div class="mv-code">
        <div class="mv-tabs">${langs.map((l) => `<button data-lang="${l}">${l === "kotlin" ? "KOTLIN" : "JAVA"}</button>`).join("")}</div>
        <pre class="mv-listing"></pre>
      </div>
      <div class="mv-note" aria-live="polite"></div>
      <div class="mv-mem">
        <div class="mv-col"><div class="mv-h">STACK <small>newest frame on top</small></div><div class="mv-stack"></div></div>
        <div class="mv-col"><div class="mv-h">HEAP <small>objects</small></div><div class="mv-heap"></div></div>
        <svg class="mv-svg" aria-hidden="true"><defs><marker id="${markerId}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z"></path></marker></defs><g class="mv-arrows"></g></svg>
      </div>
      <div class="mv-ctl">
        <button data-go="first" title="First step">⏮</button>
        <button data-go="prev">◀ Prev</button>
        <span class="mv-count"></span>
        <button data-go="next">Next ▶</button>
      </div>`;

    const $ = (sel) => root.querySelector(sel);

    root.querySelector(".mv-tabs").addEventListener("click", (e) => {
      const l = e.target.dataset.lang;
      if (!l) return;
      lang = l;
      try { localStorage.setItem("csf:memviz-lang", l); } catch (err) { /* ignore */ }
      render();
    });
    root.querySelector(".mv-ctl").addEventListener("click", (e) => {
      const go = e.target.dataset.go;
      if (go === "first") step = 0;
      if (go === "prev") step = Math.max(0, step - 1);
      if (go === "next") step = Math.min(spec.steps.length - 1, step + 1);
      render();
    });
    root.tabIndex = 0;
    root.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight") { step = Math.min(spec.steps.length - 1, step + 1); render(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); render(); e.preventDefault(); }
    });

    function esc(s) {
      return String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
    }

    // Map "frame/var" or "obj/field" -> value, for change highlighting
    function valuesOf(s) {
      const m = {};
      (s.stack || []).forEach((f, i) => f.vars.forEach(([k, v]) => { m[`f${i}:${f.name}/${k}`] = v; }));
      (s.heap || []).forEach((o) => o.fields.forEach(([k, v]) => { m[`o:${o.id}/${k}`] = v; }));
      return m;
    }

    function cell(key, value, prev) {
      const changed = prev !== undefined && prev[key] !== value;
      const isNew = prev !== undefined && !(key in prev);
      let shown;
      if (typeof value === "string" && value.startsWith("@")) shown = `<span class="mv-dot" data-ref="${esc(value.slice(1))}">●</span>`;
      else if (value === "null") shown = `<span class="mv-null">null</span>`;
      else shown = esc(value);
      return `<td class="mv-v${changed || isNew ? " mv-changed" : ""}">${shown}</td>`;
    }

    function render() {
      const s = spec.steps[step];
      const prev = step > 0 ? valuesOf(spec.steps[step - 1]) : undefined;

      root.querySelectorAll(".mv-tabs button").forEach((b) => b.classList.toggle("on", b.dataset.lang === lang));
      const lines = spec[lang];
      const hl = lang === "kotlin" ? s.kt : s.jv;
      $(".mv-listing").innerHTML = lines
        .map((line, i) => `<span class="mv-line${i + 1 === hl ? " mv-hl" : ""}"><span class="mv-ln">${i + 1}</span>${esc(line) || " "}</span>`)
        .join("");

      $(".mv-note").innerHTML = s.note || "";
      $(".mv-count").textContent = `step ${step + 1} / ${spec.steps.length}`;

      const frames = (s.stack || []).map((f, i) => ({ f, i })).reverse();
      $(".mv-stack").innerHTML = frames
        .map(({ f, i }, k) => `
          <div class="mv-frame${k === 0 ? " mv-top" : ""}">
            <div class="mv-fname">${esc(f.name)}${k === 0 ? ' <small>← running</small>' : ""}</div>
            <table>${f.vars.map(([n, v]) => `<tr><td class="mv-n">${esc(n)}</td>${cell(`f${i}:${f.name}/${n}`, v, prev)}</tr>`).join("") || '<tr><td class="mv-n mv-empty">(no locals yet)</td></tr>'}</table>
          </div>`)
        .join("");

      $(".mv-heap").innerHTML = (s.heap || [])
        .map((o) => `
          <div class="mv-obj" data-id="${esc(o.id)}">
            <div class="mv-oname">${esc(o.type)}</div>
            <table>${o.fields.map(([n, v]) => `<tr><td class="mv-n">${esc(n)}</td>${cell(`o:${o.id}/${n}`, v, prev)}</tr>`).join("")}</table>
          </div>`)
        .join("") || '<div class="mv-empty">(empty)</div>';

      root.querySelector(".mv-ctl [data-go=prev]").disabled = step === 0;
      root.querySelector(".mv-ctl [data-go=first]").disabled = step === 0;
      root.querySelector(".mv-ctl [data-go=next]").disabled = step === spec.steps.length - 1;

      requestAnimationFrame(drawArrows);
    }

    function drawArrows() {
      const mem = $(".mv-mem");
      const svg = $(".mv-svg");
      const g = $(".mv-arrows");
      const box = mem.getBoundingClientRect();
      svg.setAttribute("width", mem.scrollWidth);
      svg.setAttribute("height", mem.scrollHeight);
      g.innerHTML = "";
      root.querySelectorAll(".mv-dot").forEach((dot) => {
        const target = root.querySelector(`.mv-obj[data-id="${CSS.escape(dot.dataset.ref)}"]`);
        if (!target) return;
        const a = dot.getBoundingClientRect();
        const t = target.getBoundingClientRect();
        const x1 = a.left + a.width / 2 - box.left;
        const y1 = a.top + a.height / 2 - box.top;
        let x2, y2, path;
        if (t.left > a.right + 8) {
          // target is to the right: enter its left edge
          x2 = t.left - box.left;
          y2 = Math.min(Math.max(y1, t.top - box.top + 14), t.bottom - box.top - 10);
          const dx = Math.max(30, (x2 - x1) / 2);
          path = `M${x1},${y1} C${x1 + dx},${y1} ${x2 - dx},${y2} ${x2},${y2}`;
        } else if (t.top > a.bottom || t.bottom < a.top) {
          // stacked layout or heap-to-heap: loop out to the right and enter the target's right edge
          x2 = t.right - box.left;
          y2 = t.top - box.top + 14;
          const bulge = Math.max(x1, x2) + 36;
          path = `M${x1},${y1} C${bulge},${y1} ${bulge},${y2} ${x2},${y2}`;
        } else {
          x2 = t.left - box.left;
          y2 = t.top - box.top + 14;
          path = `M${x1},${y1} L${x2},${y2}`;
        }
        const p = document.createElementNS(SVGNS, "path");
        p.setAttribute("d", path);
        p.setAttribute("marker-end", `url(#${markerId})`);
        g.appendChild(p);
      });
    }

    window.addEventListener("resize", () => requestAnimationFrame(drawArrows));
    render();
  });
})();
