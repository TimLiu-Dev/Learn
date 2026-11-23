/* ==========================================================================
   Interactive Total Compensation & 4-Year Vesting Calculator
   Compares Big Tech RSU schedules (Even 25% vs Amazon-style 5/15/40/40)
   against Startup Option EV / 409A math. Zero external dependencies.
   ========================================================================== */

(function () {
  const PRESETS = {
    midtech: {
      label: "2027 Mid-Tech L4",
      base: 165000,
      bonusPct: 10,
      rsuTotal: 160000,
      signOnY1: 20000,
      signOnY2: 0,
      schedule: "even"
    },
    bigtechEven: {
      label: "Big Tech L4/L5 (Even 25%)",
      base: 185000,
      bonusPct: 15,
      rsuTotal: 320000,
      signOnY1: 35000,
      signOnY2: 0,
      schedule: "even"
    },
    amazonBackloaded: {
      label: "Amazon L5/L6 (5/15/40/40)",
      base: 180000,
      bonusPct: 0,
      rsuTotal: 360000,
      signOnY1: 85000,
      signOnY2: 65000,
      schedule: "backloaded"
    }
  };

  const SCHEDULES = {
    even: [0.25, 0.25, 0.25, 0.25],
    backloaded: [0.05, 0.15, 0.40, 0.40]
  };

  function fmtMoney(n) {
    return "$" + Math.round(n).toLocaleString("en-US");
  }

  function initCalculator() {
    document.querySelectorAll(".offer-calc[data-offer-calc]").forEach((root) => {
      root.innerHTML = `
        <div class="sandbox-panel">
          <div class="sandbox-controls">
            <button type="button" class="sandbox-btn is-active" data-preset="midtech">2027 Mid-Tech L4</button>
            <button type="button" class="sandbox-btn" data-preset="bigtechEven">Big Tech (25/25/25/25)</button>
            <button type="button" class="sandbox-btn" data-preset="amazonBackloaded">Amazon (5/15/40/40)</button>
          </div>
          <div class="sandbox-grid" style="margin-bottom:0.75rem;">
            <div class="sandbox-box">
              <h4>Cash &amp; Bonus</h4>
              <label style="display:block;margin-bottom:0.35rem;">Base Salary ($):
                <input id="oc-base" class="sandbox-input" type="number" step="5000" value="165000" style="margin-bottom:0.35rem;" />
              </label>
              <label style="display:block;margin-bottom:0.35rem;">Target Bonus (%):
                <input id="oc-bonus" class="sandbox-input" type="number" step="1" value="10" style="margin-bottom:0.35rem;" />
              </label>
              <label style="display:block;margin-bottom:0;">Sign-On Year 1 / Year 2 ($):
                <div style="display:flex;gap:0.4rem;">
                  <input id="oc-s1" class="sandbox-input" type="number" step="5000" value="20000" style="margin-bottom:0;" />
                  <input id="oc-s2" class="sandbox-input" type="number" step="5000" value="0" style="margin-bottom:0;" />
                </div>
              </label>
            </div>
            <div class="sandbox-box">
              <h4>4-Year RSU Equity</h4>
              <label style="display:block;margin-bottom:0.35rem;">Total 4-Yr RSU Grant ($):
                <input id="oc-rsu" class="sandbox-input" type="number" step="10000" value="160000" style="margin-bottom:0.35rem;" />
              </label>
              <label style="display:block;margin-bottom:0;">Vesting Schedule:
                <select id="oc-sched" class="sandbox-input" style="margin-bottom:0;">
                  <option value="even">Standard Even (25% / 25% / 25% / 25%)</option>
                  <option value="backloaded">Back-Loaded (5% / 15% / 40% / 40%)</option>
                </select>
              </label>
              <p style="margin:0.5rem 0 0;font-size:0.78rem;color:var(--ink-muted);">
                Watch how Year 3 drops if Year 1–2 sign-on cash ends without an equity refresher or back-loaded vest.
              </p>
            </div>
          </div>
          <div id="oc-summary" style="font-weight:700;margin-bottom:0.5rem;"></div>
          <div id="oc-table"></div>
        </div>
      `;

      const baseEl = root.querySelector("#oc-base");
      const bonusEl = root.querySelector("#oc-bonus");
      const s1El = root.querySelector("#oc-s1");
      const s2El = root.querySelector("#oc-s2");
      const rsuEl = root.querySelector("#oc-rsu");
      const schedEl = root.querySelector("#oc-sched");
      const summaryEl = root.querySelector("#oc-summary");
      const tableEl = root.querySelector("#oc-table");
      const presetBtns = root.querySelectorAll(".sandbox-btn[data-preset]");

      function render() {
        const base = Number(baseEl.value) || 0;
        const bonus = base * ((Number(bonusEl.value) || 0) / 100);
        const s1 = Number(s1El.value) || 0;
        const s2 = Number(s2El.value) || 0;
        const rsu = Number(rsuEl.value) || 0;
        const weights = SCHEDULES[schedEl.value] || SCHEDULES.even;
        const signOns = [s1, s2, 0, 0];

        const rows = weights.map((w, idx) => {
          const equity = rsu * w;
          const signOn = signOns[idx];
          const total = base + bonus + equity + signOn;
          return { year: idx + 1, equity, signOn, total };
        });

        const fourYrTotal = rows.reduce((acc, r) => acc + r.total, 0);
        const avgTc = fourYrTotal / 4;
        const minTc = Math.min(...rows.map((r) => r.total));
        const maxTc = Math.max(...rows.map((r) => r.total));
        const cliffGap = maxTc - minTc;

        summaryEl.innerHTML =
          `4-Year Avg TC: <span style="color:var(--ok)">${fmtMoney(avgTc)}/yr</span> ` +
          `(${fmtMoney(fourYrTotal)} total) · Max Year-to-Year Swing: ` +
          `<span style="color:${cliffGap > 25000 ? "var(--accent)" : "var(--ink)"}">${fmtMoney(cliffGap)}</span>`;

        tableEl.innerHTML = `
          <table style="margin:0;font-size:0.84rem;">
            <thead>
              <tr>
                <th>Year</th>
                <th>Base + Bonus</th>
                <th>RSU Vest</th>
                <th>Sign-On</th>
                <th>Annual TC</th>
              </tr>
            </thead>
            <tbody>
              ${rows
                .map(
                  (r) => `
                <tr>
                  <td><strong>Year ${r.year}</strong></td>
                  <td>${fmtMoney(base + bonus)}</td>
                  <td>${fmtMoney(r.equity)}</td>
                  <td>${fmtMoney(r.signOn)}</td>
                  <td><strong>${fmtMoney(r.total)}</strong></td>
                </tr>`
                )
                .join("")}
            </tbody>
          </table>
        `;
      }

      presetBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
          const p = PRESETS[btn.getAttribute("data-preset")];
          if (!p) return;
          presetBtns.forEach((b) => b.classList.toggle("is-active", b === btn));
          baseEl.value = p.base;
          bonusEl.value = p.bonusPct;
          rsuEl.value = p.rsuTotal;
          s1El.value = p.signOnY1;
          s2El.value = p.signOnY2;
          schedEl.value = p.schedule;
          render();
        });
      });

      [baseEl, bonusEl, s1El, s2El, rsuEl, schedEl].forEach((el) => {
        el.addEventListener("input", render);
        el.addEventListener("change", render);
      });

      render();
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initCalculator);
  } else {
    initCalculator();
  }
})();
