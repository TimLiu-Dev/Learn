/* CS Fundamentals: shared lesson behaviour.
 *
 * Every lesson includes this file. Set <body data-lesson="l01"> so progress is stored per lesson.
 *
 * Components (all optional):
 *
 *   Quiz: immediate feedback, one attempt.
 *     <div class="quiz" data-id="cli-1" data-group="Part 1" data-answer="2">
 *       <p class="q">Question?</p>
 *       <div class="opts"><button>A</button><button>B</button><button>C</button><button>D</button></div>
 *       <p class="why">Explanation, shown after answering.</p>
 *     </div>
 *     data-answer is the 0-based index of the correct button.
 *     Optional, after .why: <ol class="whyopts"><li>…</li>…</ol>, one <li> per button in the same order,
 *     saying why that option is right or wrong. Shown after answering, each labelled with its option.
 *     A code question uses <div class="q"> so it can hold a <pre> listing.
 *     Answering fires a "quiz:answered" event on the .quiz (playground.js uses it to unlock running its code).
 *
 *   Self-rating: pick one value per row.
 *     <div class="rating" data-id="T1-kotlin" data-label="T1 Temps (Kotlin)">
 *       <span class="who kt">KOTLIN</span><button>Solid</button><button>Shaky</button>…
 *     </div>
 *
 *   Part done: checkbox persisted; updates #resume and .toc entries [data-part].
 *     <label class="done"><input type="checkbox" class="part-done" data-part="p1"> Part 1 done</label>
 *
 *   Results summary: collects quiz scores + ratings into a textarea and copies it.
 *     <button class="btn" data-summary="#summary">Build my results</button>
 *     <textarea id="summary" readonly></textarea>
 *
 *   Reset: <button class="btn ghost" data-reset>Reset this lesson</button>
 */
(function () {
  // Runnable Kotlin and practice widgets live in playground.js, next to this file.
  if (document.currentScript) {
    const pg = document.createElement("script");
    pg.src = new URL("playground.js", document.currentScript.src).href;
    document.head.appendChild(pg);
  }
  const lesson = document.body.dataset.lesson || "lesson";
  const KEY = "csf:" + lesson;

  // Storage can be unavailable (private mode, blocked site data); the page must work without it.
  function load() {
    try { return JSON.parse(localStorage.getItem(KEY)) || {}; } catch (e) { return {}; }
  }
  function save() {
    try { localStorage.setItem(KEY, JSON.stringify(state)); } catch (e) { /* ignore */ }
  }
  const state = Object.assign({ quiz: {}, rating: {}, parts: {} }, load());

  // ── Quizzes ──────────────────────────────────────────────
  document.querySelectorAll(".quiz").forEach((quiz) => {
    const id = quiz.dataset.id;
    const answer = Number(quiz.dataset.answer);
    const buttons = [...quiz.querySelectorAll(".opts button")];
    const why = quiz.querySelector(".why");

    function reveal(chosen) {
      quiz.classList.add("answered");
      buttons.forEach((b, i) => {
        b.disabled = true;
        if (i === answer) b.classList.add("right");
        if (i === chosen && chosen !== answer) b.classList.add("wrong");
      });
      if (why && !why.querySelector(".verdict")) {
        const v = document.createElement("span");
        const ok = chosen === answer;
        v.className = "verdict " + (ok ? "ok" : "no");
        v.textContent = ok ? "CORRECT ·" : "NOT QUITE ·";
        why.prepend(v);
      }
      const whyopts = quiz.querySelector(".whyopts");
      if (whyopts && !whyopts.dataset.ready) {
        whyopts.dataset.ready = "1";
        [...whyopts.children].forEach((li, i) => {
          if (!buttons[i]) return;
          const tag = document.createElement("b");
          tag.className = "opt";
          tag.innerHTML = (i === answer ? "✓ " : "✗ ") + buttons[i].innerHTML.trim();   // keeps <code> styling
          li.prepend(tag, " ");
          if (i === answer) li.classList.add("right");
          if (i === chosen) li.classList.add("chosen");
        });
      }
      quiz.dispatchEvent(new CustomEvent("quiz:answered", { bubbles: true }));
    }

    buttons.forEach((b, i) =>
      b.addEventListener("click", () => {
        state.quiz[id] = { chosen: i, correct: i === answer, group: quiz.dataset.group || "" };
        save();
        reveal(i);
      })
    );
    if (state.quiz[id]) reveal(state.quiz[id].chosen);
  });

  // ── Self-ratings ─────────────────────────────────────────
  document.querySelectorAll(".rating").forEach((row) => {
    const id = row.dataset.id;
    const buttons = [...row.querySelectorAll("button")];
    function show(value) {
      buttons.forEach((b) => b.classList.toggle("on", b.textContent.trim() === value));
    }
    buttons.forEach((b) =>
      b.addEventListener("click", () => {
        state.rating[id] = { value: b.textContent.trim(), label: row.dataset.label || id };
        save();
        show(state.rating[id].value);
      })
    );
    if (state.rating[id]) show(state.rating[id].value);
  });

  // ── Part progress ────────────────────────────────────────
  const partBoxes = [...document.querySelectorAll("input.part-done")];
  function updateProgress() {
    const done = partBoxes.filter((b) => b.checked);
    partBoxes.forEach((b) => {
      const tocItem = document.querySelector(`.toc [data-part="${b.dataset.part}"]`);
      if (tocItem) tocItem.classList.toggle("ok", b.checked);
    });
    const resume = document.getElementById("resume");
    if (!resume || !partBoxes.length) return;
    const next = partBoxes.find((b) => !b.checked);
    if (!done.length) resume.textContent = "";
    else if (!next) resume.textContent = "all parts done ✓";
    else {
      const target = document.getElementById(next.dataset.part);
      const name = target ? target.dataset.title || next.dataset.part : next.dataset.part;
      resume.innerHTML = `resume at <a href="#${next.dataset.part}">${name}</a>`;
    }
  }
  partBoxes.forEach((b) => {
    b.checked = !!state.parts[b.dataset.part];
    b.addEventListener("change", () => {
      state.parts[b.dataset.part] = b.checked;
      save();
      updateProgress();
    });
  });
  updateProgress();

  // ── Results summary ──────────────────────────────────────
  document.querySelectorAll("[data-summary]").forEach((btn) =>
    btn.addEventListener("click", () => {
      const out = document.querySelector(btn.dataset.summary);
      const title = document.querySelector("h1")?.textContent.trim() || lesson;
      const lines = [`${lesson.toUpperCase()} results: ${title}`, ""];

      const groups = {};
      Object.values(state.quiz).forEach((q) => {
        const g = (groups[q.group || "Quiz"] ||= { right: 0, total: 0 });
        g.total++;
        if (q.correct) g.right++;
      });
      const totalQuizzes = document.querySelectorAll(".quiz").length;
      lines.push(`Quizzes (${Object.keys(state.quiz).length}/${totalQuizzes} answered):`);
      Object.entries(groups).forEach(([g, s]) => lines.push(`  ${g}: ${s.right}/${s.total}`));
      const missed = Object.entries(state.quiz).filter(([, q]) => !q.correct).map(([id]) => id);
      if (missed.length) lines.push(`  Missed: ${missed.join(", ")}`);

      const rows = document.querySelectorAll(".rating");
      if (rows.length) {
        lines.push("", "Self-ratings:");
        rows.forEach((row) => {
          const r = state.rating[row.dataset.id];
          lines.push(`  ${row.dataset.label || row.dataset.id}: ${r ? r.value : "not rated"}`);
        });
      }

      const practices = [...document.querySelectorAll(".practice")];
      if (practices.length) {
        let solved = {};
        try { solved = JSON.parse(localStorage.getItem(KEY + ":practice")) || {}; } catch (e) { /* ignore */ }
        const ids = practices.map((p) => p.dataset.id);
        lines.push("", `Practice solved in page: ${ids.filter((id) => solved[id] && solved[id].solved).length} of ${ids.length}`);
        const open = ids.filter((id) => !(solved[id] && solved[id].solved));
        if (open.length) lines.push(`  Not yet: ${open.join(", ")}`);
      }

      const partsDone = partBoxes.filter((b) => b.checked).map((b) => b.dataset.part);
      lines.push("", `Parts done: ${partsDone.length ? partsDone.join(", ") : "none"} of ${partBoxes.length}`);
      lines.push("", "Notes for my teacher: ");

      out.value = lines.join("\n");
      out.focus();
      out.select();
      // Clipboard access is blocked on some file:// pages; the selected text can still be copied by hand.
      if (navigator.clipboard) {
        navigator.clipboard.writeText(out.value).then(
          () => { btn.textContent = "Copied to clipboard ✓"; },
          () => { btn.textContent = "Selected: press ⌘C to copy"; }
        );
      } else {
        btn.textContent = "Selected: press ⌘C to copy";
      }
    })
  );

  // ── Reset ────────────────────────────────────────────────
  document.querySelectorAll("[data-reset]").forEach((btn) =>
    btn.addEventListener("click", () => {
      if (!confirm("Clear all saved answers and progress for this lesson?")) return;
      try { localStorage.removeItem(KEY); localStorage.removeItem(KEY + ":practice"); } catch (e) { /* ignore */ }
      location.reload();
    })
  );
})();
