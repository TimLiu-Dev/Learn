/* Shared spoken-answer drill (mock interview flashcards).
 *
 * Usage, inside a lesson HTML file:
 *
 *   <div id="drill"></div>
 *   <script src="../assets/drill.js"></script>
 *   <script>
 *     renderDrill('drill', [
 *       {
 *         q: "Question the interviewer asks?",
 *         a: "Model answer (HTML allowed).",
 *         from: "0001-what-is-mvi.html",   // lesson to revisit if missed
 *         label: "Lesson 1"
 *       },
 *       // ...more cards
 *     ], { seconds: 60 });
 *   </script>
 *
 * Design rules:
 * - Cards are shuffled every run (interleaving across lessons).
 * - The learner answers out loud first; the model answer stays hidden until
 *   they reveal it (retrieval before feedback).
 * - Self-grading feeds a summary that links back to the lessons behind any
 *   shaky or missed cards, and offers a re-run of just those.
 */
function renderDrill(containerId, cards, opts) {
  const container = document.getElementById(containerId);
  if (!container) return;
  const seconds = (opts && opts.seconds) || 60;

  const style = document.createElement('style');
  style.textContent = `
    .drill-card { border: 1px solid var(--rule, #ddd); border-radius: 8px; padding: 1.2em 1.4em; margin: 1.5em 0; }
    .drill-meta { display: flex; justify-content: space-between; font-size: 0.75em; color: var(--muted, #666);
                  text-transform: uppercase; letter-spacing: 0.06em; }
    .drill-q { font-size: 1.15em; font-weight: 600; margin: 0.8em 0 1em; }
    .drill-timer { font-variant-numeric: tabular-nums; }
    .drill-timer.done { color: var(--bad, #a33b2f); }
    .drill-a { border-top: 1px solid var(--rule, #ddd); margin-top: 1em; padding-top: 0.8em; font-size: 0.95em; }
    .drill-btns { display: flex; flex-wrap: wrap; gap: 0.5em; margin-top: 1em; }
    .drill-btns button { font-family: inherit; font-size: 0.9em; padding: 0.5em 1em; cursor: pointer;
                         border: 1px solid var(--rule, #ddd); border-radius: 5px;
                         background: var(--paper, #fff); color: inherit; }
    .drill-btns button:hover { border-color: var(--accent, #8a3b2f); }
    .drill-summary li { margin-bottom: 0.3em; }
  `;
  container.appendChild(style);

  const host = document.createElement('div');
  container.appendChild(host);

  function shuffle(list) {
    const a = list.slice();
    for (let i = a.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
  }

  function button(text, onClick) {
    const b = document.createElement('button');
    b.textContent = text;
    b.addEventListener('click', onClick);
    return b;
  }

  let timerId = null;

  function run(deck) {
    const order = shuffle(deck);
    const grades = [];
    let i = 0;

    function showCard() {
      clearInterval(timerId);
      host.innerHTML = '';
      if (i >= order.length) return showSummary();

      const card = order[i];
      const box = document.createElement('div');
      box.className = 'drill-card';

      const meta = document.createElement('div');
      meta.className = 'drill-meta';
      const count = document.createElement('span');
      count.textContent = `Question ${i + 1} of ${order.length}`;
      const timer = document.createElement('span');
      timer.className = 'drill-timer';
      meta.append(count, timer);

      const q = document.createElement('div');
      q.className = 'drill-q';
      q.textContent = card.q;

      let left = seconds;
      timer.textContent = `${left}s`;
      timerId = setInterval(() => {
        left--;
        timer.textContent = left > 0 ? `${left}s` : 'time';
        if (left <= 0) { timer.classList.add('done'); clearInterval(timerId); }
      }, 1000);

      const btns = document.createElement('div');
      btns.className = 'drill-btns';
      btns.append(button('Reveal model answer', () => {
        clearInterval(timerId);
        const a = document.createElement('div');
        a.className = 'drill-a';
        a.innerHTML = card.a;
        btns.replaceWith(a, gradeRow());
      }));

      function gradeRow() {
        const row = document.createElement('div');
        row.className = 'drill-btns';
        [['Nailed it', 'good'], ['Shaky', 'shaky'], ['Missed', 'missed']].forEach(([text, grade]) => {
          row.append(button(text, () => { grades.push({ card, grade }); i++; showCard(); }));
        });
        return row;
      }

      box.append(meta, q, btns);
      host.append(box);
    }

    function showSummary() {
      const weak = grades.filter(g => g.grade !== 'good');
      const box = document.createElement('div');
      box.className = 'drill-card drill-summary';
      const good = grades.length - weak.length;
      box.innerHTML = `<div class="drill-q">${good} / ${grades.length} nailed</div>`;

      if (weak.length) {
        const p = document.createElement('p');
        p.textContent = 'Revisit these (and tell your teacher which ones):';
        const ul = document.createElement('ul');
        weak.forEach(({ card, grade }) => {
          const li = document.createElement('li');
          li.innerHTML = `<strong>${grade}</strong>: ${card.q.replace(/</g, '&lt;')} `
            + (card.from ? `<a href="${card.from}">(${card.label || 'lesson'})</a>` : '');
          ul.append(li);
        });
        const btns = document.createElement('div');
        btns.className = 'drill-btns';
        btns.append(button('Re-run only these', () => run(weak.map(g => g.card))));
        btns.append(button('Re-run everything', () => run(cards)));
        box.append(p, ul, btns);
      } else {
        const btns = document.createElement('div');
        btns.className = 'drill-btns';
        btns.append(button('Run again', () => run(cards)));
        box.append(btns);
      }
      host.append(box);
    }

    showCard();
  }

  run(cards);
}
