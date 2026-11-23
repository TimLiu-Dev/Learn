/* ==========================================================================
   OfferLedger Web Dashboard UI Controller
   ========================================================================== */
(function () {
  'use strict';

  const E = window.OfferLedgerEngine;
  const STORAGE_KEY = 'offerledger_app_offers_v1';

  const DEFAULT_OFFERS = [
    {
      name: 'Job 1 · Johnson Health Tech (Baseline)',
      level: 'L3',
      tier: 'tier1',
      base: 85000,
      bonusPct: 0.0588,
      rsuGrant: 0,
      vesting: [0.25, 0.25, 0.25, 0.25],
      signOn: [0, 0, 0, 0],
      effectiveTaxRate: 0.22,
      colIndex: 100,
      match401k: 3400,
      esppBenefit: 0
    },
    {
      name: 'Job 2 · Remote Mid-Tech Android (SWE II)',
      level: 'L4',
      tier: 'tier2',
      base: 162000,
      bonusPct: 0.10,
      rsuGrant: 80000,
      vesting: [0.25, 0.25, 0.25, 0.25],
      signOn: [15000, 0, 0, 0],
      effectiveTaxRate: 0.25,
      colIndex: 105,
      match401k: 8000,
      esppBenefit: 3500
    },
    {
      name: 'Job 2 · Big Tech Hub (L4 Android)',
      level: 'L4',
      tier: 'tier3',
      base: 175000,
      bonusPct: 0.15,
      rsuGrant: 260000,
      vesting: [0.25, 0.25, 0.25, 0.25],
      signOn: [30000, 0, 0, 0],
      effectiveTaxRate: 0.34,
      colIndex: 148,
      match401k: 11000,
      esppBenefit: 5000
    }
  ];

  function loadOffers() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        const parsed = JSON.parse(raw);
        if (Array.isArray(parsed) && parsed.length > 0) return parsed;
      }
    } catch (_) { /* ignore */ }
    return JSON.parse(JSON.stringify(DEFAULT_OFFERS));
  }

  let offers = loadOffers();

  function saveOffers() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(offers));
    } catch (_) { /* ignore */ }
  }

  function fmt(n) {
    return '$' + Math.round(n).toLocaleString('en-US');
  }

  function renderOfferInputs() {
    const container = document.getElementById('offer-editors');
    container.innerHTML = offers.map(function (o, idx) {
      return `
        <div class="card" data-offer-idx="${idx}">
          <div style="display:flex;justify-content:space-between;align-items:center;gap:0.5rem;margin-bottom:0.65rem;">
            <strong style="font-family:var(--font-serif);font-size:1.05rem;">Offer ${idx + 1}</strong>
            ${offers.length > 1 ? `<button type="button" class="btn" data-remove="${idx}">Remove</button>` : ''}
          </div>
          <div class="grid-2" style="gap:0.6rem;margin:0;">
            <label>Label<br><input class="ol-input" data-field="name" value="${o.name.replace(/"/g, '&quot;')}" /></label>
            <label>Level / Tier<br>
              <div style="display:flex;gap:0.4rem;">
                <select class="ol-input" data-field="level">
                  ${['L3', 'L4', 'L5', 'L6', 'L7'].map(lvl => `<option value="${lvl}" ${o.level === lvl ? 'selected' : ''}>${lvl}</option>`).join('')}
                </select>
                <select class="ol-input" data-field="tier">
                  <option value="tier1" ${o.tier === 'tier1' ? 'selected' : ''}>Tier 1</option>
                  <option value="tier2" ${o.tier === 'tier2' ? 'selected' : ''}>Tier 2 Mid</option>
                  <option value="tier3" ${o.tier === 'tier3' ? 'selected' : ''}>Tier 3 Big</option>
                </select>
              </div>
            </label>
            <label>Base Salary ($)<br><input type="number" class="ol-input" data-field="base" value="${o.base}" /></label>
            <label>Target Bonus (%)<br><input type="number" step="0.5" class="ol-input" data-field="bonusPctDisplay" value="${(o.bonusPct * 100).toFixed(1)}" /></label>
            <label>4-Year RSU Grant ($)<br><input type="number" class="ol-input" data-field="rsuGrant" value="${o.rsuGrant}" /></label>
            <label>Year-1 Sign-On ($)<br><input type="number" class="ol-input" data-field="signOnY1" value="${o.signOn[0] || 0}" /></label>
            <label>Year-2 Sign-On ($)<br><input type="number" class="ol-input" data-field="signOnY2" value="${o.signOn[1] || 0}" /></label>
            <label>Effective Tax (%)<br><input type="number" step="1" class="ol-input" data-field="taxPctDisplay" value="${Math.round((o.effectiveTaxRate || 0.25) * 100)}" /></label>
            <label>COL Index (100 = Base)<br><input type="number" class="ol-input" data-field="colIndex" value="${o.colIndex || 100}" /></label>
            <label>401k Match ($/yr)<br><input type="number" class="ol-input" data-field="match401k" value="${o.match401k || 0}" /></label>
            <label>ESPP Benefit ($/yr)<br><input type="number" class="ol-input" data-field="esppBenefit" value="${o.esppBenefit || 0}" /></label>
            <label>Vesting Schedule<br>
              <select class="ol-input" data-field="vestingPreset">
                <option value="even" ${o.vesting[0] === 0.25 ? 'selected' : ''}>Even 25 / 25 / 25 / 25</option>
                <option value="back" ${o.vesting[0] === 0.05 ? 'selected' : ''}>Back-Weighted 5 / 15 / 40 / 40</option>
                <option value="front" ${o.vesting[0] === 0.35 ? 'selected' : ''}>Front-Loaded 35 / 30 / 20 / 15</option>
              </select>
            </label>
          </div>
        </div>
      `;
    }).join('');

    container.querySelectorAll('[data-offer-idx]').forEach(function (card) {
      const idx = Number(card.getAttribute('data-offer-idx'));
      card.querySelectorAll('.ol-input').forEach(function (input) {
        input.addEventListener('change', function () {
          const f = input.getAttribute('data-field');
          const o = offers[idx];
          if (f === 'name' || f === 'level' || f === 'tier') o[f] = input.value;
          else if (f === 'base' || f === 'rsuGrant' || f === 'colIndex' || f === 'match401k' || f === 'esppBenefit') o[f] = Number(input.value) || 0;
          else if (f === 'bonusPctDisplay') o.bonusPct = (Number(input.value) || 0) / 100;
          else if (f === 'taxPctDisplay') o.effectiveTaxRate = (Number(input.value) || 0) / 100;
          else if (f === 'signOnY1') o.signOn = [Number(input.value) || 0, o.signOn[1] || 0, 0, 0];
          else if (f === 'signOnY2') o.signOn = [o.signOn[0] || 0, Number(input.value) || 0, 0, 0];
          else if (f === 'vestingPreset') {
            if (input.value === 'back') o.vesting = [0.05, 0.15, 0.40, 0.40];
            else if (input.value === 'front') o.vesting = [0.35, 0.30, 0.20, 0.15];
            else o.vesting = [0.25, 0.25, 0.25, 0.25];
          }
          saveOffers();
          renderAnalysis();
        });
      });
    });

    container.querySelectorAll('[data-remove]').forEach(function (btn) {
      btn.addEventListener('click', function () {
        offers.splice(Number(btn.getAttribute('data-remove')), 1);
        saveOffers();
        renderOfferInputs();
        renderAnalysis();
      });
    });
  }

  function renderAnalysis() {
    const ranked = E.compareAndRankOffers(offers);
    const tbody = document.getElementById('comparison-tbody');
    tbody.innerHTML = ranked.map(function (r, idx) {
      let cliffBadge = `<span class="pill pill-planned">$0 Y2 (Flat)</span>`;
      if (r.year1ToYear2Delta < 0) {
        cliffBadge = `<span class="pill pill-planned">${fmt(r.year1ToYear2Delta)} Y2 drop</span>`;
      } else if (r.year1ToYear2Delta > 0) {
        cliffBadge = `<span class="pill pill-live">+${fmt(r.year1ToYear2Delta)} Y2</span>`;
      }
      return `
        <tr>
          <td><strong>#${idx + 1} · ${r.name}</strong><br><small>${r.level} · ${r.tier.toUpperCase()}</small></td>
          <td class="num">${fmt(r.year1Tc)}</td>
          <td class="num">${fmt(r.fourYearAvgTc)}</td>
          <td class="num"><strong>${fmt(r.colAdjustedRealValue)}</strong></td>
          <td class="num">${r.bandPercentile}%<br><small>${r.bandZone}</small></td>
          <td>${cliffBadge}</td>
        </tr>
      `;
    }).join('');

    // Build Counter Strategy from top external offer (or highest nominal offer)
    const sortedByNominal = ranked.slice().sort(function (a, b) { return b.fourYearAvgTc - a.fourYearAvgTc; });
    const topTarget = sortedByNominal[0];
    const secondOffer = sortedByNominal[1];
    const competingTc = secondOffer ? secondOffer.fourYearAvgTc : 0;
    const counter = E.generateCounterTarget(topTarget.fourYearAvgTc, topTarget.bandMax, competingTc);
    const desiredBaseIfCash = topTarget.base + Math.round(counter.askDelta / (1 + (topTarget.bonusPct || 0)));
    const swap = E.swapCompLevers(desiredBaseIfCash, topTarget.baseCap, 4);

    document.getElementById('counter-summary').innerHTML = `
      <p>
        <strong>Target Offer:</strong> ${topTarget.name} (Base: <strong>${fmt(topTarget.base)}</strong> · 4-Yr Avg TC: <strong>${fmt(topTarget.fourYearAvgTc)}</strong>, ${topTarget.bandPercentile}% of ${topTarget.level} ${topTarget.tier.toUpperCase()} band).<br>
        <strong>Recommended Counter Anchor:</strong> <strong>${fmt(counter.counterTc)}</strong> (+${fmt(counter.askDelta)}/yr, +${counter.askPct}%).<br>
        <strong>If Base Caps at ${fmt(swap.cappedBase)} (${topTarget.level} Cap):</strong> Pivot remaining <strong>${fmt(swap.annualBaseGap)}/yr</strong> gap into <strong>+${fmt(swap.extraSignOn)} Year-1 Sign-On</strong> + <strong>+${fmt(swap.extraRsuGrant)} 4-Year RSUs</strong>.
      </p>
    `;

    const emailText = E.buildForwardableEmail({
      companyName: topTarget.name.split('·').pop().trim(),
      roleTitle: topTarget.level + ' Android Engineer',
      recruiterName: 'Alex',
      highlightImpact: 'production Kotlin, Jetpack Compose, and mobile architecture ownership',
      competingCompany: secondOffer && !secondOffer.name.includes('Job 1') ? secondOffer.name.split('·').pop().trim() : '',
      targetTc: counter.counterTc,
      signOnAsk: Math.max(15000, swap.extraSignOn || Math.round(counter.askDelta * 0.5)),
      extraRsuAsk: Math.max(40000, swap.extraRsuGrant || counter.askDelta * 2),
      candidateName: 'Your Name'
    });
    document.getElementById('email-preview').textContent = emailText;
  }

  document.getElementById('add-offer-btn').addEventListener('click', function () {
    offers.push({
      name: 'New Offer ' + (offers.length + 1),
      level: 'L4',
      tier: 'tier2',
      base: 155000,
      bonusPct: 0.10,
      rsuGrant: 70000,
      vesting: [0.25, 0.25, 0.25, 0.25],
      signOn: [10000, 0, 0, 0],
      effectiveTaxRate: 0.26,
      colIndex: 110,
      match401k: 7000,
      esppBenefit: 2500
    });
    saveOffers();
    renderOfferInputs();
    renderAnalysis();
  });

  document.getElementById('reset-offers-btn').addEventListener('click', function () {
    offers = JSON.parse(JSON.stringify(DEFAULT_OFFERS));
    saveOffers();
    renderOfferInputs();
    renderAnalysis();
  });

  document.getElementById('copy-email-btn').addEventListener('click', function () {
    const text = document.getElementById('email-preview').textContent;
    if (navigator.clipboard) {
      navigator.clipboard.writeText(text);
    }
    this.textContent = 'Copied!';
    setTimeout(() => { this.textContent = 'Copy Email Template'; }, 1500);
  });

  document.getElementById('export-json-btn').addEventListener('click', function () {
    const blob = new Blob([JSON.stringify(offers, null, 2)], { type: 'application/json' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'offerledger-scenarios.json';
    a.click();
  });

  renderOfferInputs();
  renderAnalysis();
})();
