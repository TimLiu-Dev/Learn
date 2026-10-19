// Step-through visualizer for arrays, strings, stacks, and lists.
// Markup:
//   <figure class="trace" data-trace='{
//     "title": "Two Sum, target = 9",
//     "cells": [2, 7, 11, 15],
//     "steps": [
//       { "ptr": {"i": 0}, "hl": [0], "win": [0, 2], "dim": [3],
//         "aside": "seen = {2: 0}", "note": "What happens on this step." },
//       { "cells": ["a", "b"], ... }     // a step may replace the cells (e.g. a stack)
//     ]
//   }'></figure>
// ptr: pointer name -> index. hl: highlighted indices. win: inclusive [lo, hi] window.
// dim: faded indices. aside: monospace side state (hash map, stack...). note: caption.
// "showIdx": false on the root hides index labels.
document.querySelectorAll(".trace").forEach((fig) => {
  const spec = JSON.parse(fig.dataset.trace);
  let at = 0;

  fig.innerHTML = `
    ${spec.title ? `<figcaption>${spec.title}</figcaption>` : ""}
    <div class="cells"></div>
    <div class="aside"></div>
    <p class="note"></p>
    <div class="controls">
      <button type="button" data-d="-1">◀ Prev</button>
      <button type="button" data-d="1">Next ▶</button>
      <span class="count"></span>
    </div>`;
  const cellsEl = fig.querySelector(".cells");
  const asideEl = fig.querySelector(".aside");
  const noteEl = fig.querySelector(".note");
  const countEl = fig.querySelector(".count");

  function render() {
    const step = spec.steps[at];
    // Cells carry forward: a step without "cells" reuses the latest earlier ones.
    let cells = spec.cells;
    for (let i = 0; i <= at; i++) if (spec.steps[i].cells) cells = spec.steps[i].cells;
    const ptrs = {};
    Object.entries(step.ptr || {}).forEach(([name, i]) => (ptrs[i] = ptrs[i] ? ptrs[i] + "," + name : name));
    const [lo, hi] = step.win || [-1, -2];
    cellsEl.innerHTML = cells.map((v, i) => {
      const cls = ["cell"];
      if (i >= lo && i <= hi) cls.push("in-window");
      if ((step.hl || []).includes(i)) cls.push("hl");
      if ((step.dim || []).includes(i)) cls.push("dim");
      return `<div class="${cls.join(" ")}">
        ${ptrs[i] ? `<div class="ptr">${ptrs[i]}↓</div>` : ""}
        <div class="box">${v}</div>
        ${spec.showIdx === false ? "" : `<div class="idx">${i}</div>`}
      </div>`;
    }).join("");
    asideEl.textContent = step.aside || "";
    asideEl.style.display = step.aside ? "" : "none";
    noteEl.innerHTML = step.note || "";
    countEl.textContent = `Step ${at + 1} of ${spec.steps.length}`;
    fig.querySelector('[data-d="-1"]').disabled = at === 0;
    fig.querySelector('[data-d="1"]').disabled = at === spec.steps.length - 1;
  }

  fig.querySelectorAll("button").forEach((b) =>
    b.addEventListener("click", () => {
      at = Math.min(Math.max(at + Number(b.dataset.d), 0), spec.steps.length - 1);
      render();
    }));
  render();
});
