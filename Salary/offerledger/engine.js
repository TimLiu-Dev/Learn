/* ==========================================================================
   OfferLedger Domain Engine (Universal Browser + Node.js Module)
   Pure deterministic financial & negotiation modeling engine.
   ========================================================================== */
(function (root, factory) {
  if (typeof module === 'object' && module.exports) {
    module.exports = factory();
  } else {
    root.OfferLedgerEngine = factory();
  }
})(typeof self !== 'undefined' ? self : this, function () {
  'use strict';

  const BAND_BENCHMARKS = {
    L3: {
      tier1: { min: 85000, max: 120000 },
      tier2: { min: 130000, max: 175000 },
      tier3: { min: 180000, max: 240000 }
    },
    L4: {
      tier1: { min: 115000, max: 155000 },
      tier2: { min: 175000, max: 245000 },
      tier3: { min: 250000, max: 330000 }
    },
    L5: {
      tier1: { min: 140000, max: 185000 },
      tier2: { min: 230000, max: 330000 },
      tier3: { min: 360000, max: 500000 }
    },
    L6: {
      tier1: { min: 180000, max: 245000 },
      tier2: { min: 320000, max: 460000 },
      tier3: { min: 500000, max: 780000 }
    },
    L7: {
      tier1: { min: 240000, max: 350000 },
      tier2: { min: 450000, max: 680000 },
      tier3: { min: 750000, max: 1400000 }
    }
  };

  const BASE_CAPS = {
    L3: { tier1: 115000, tier2: 155000, tier3: 170000 },
    L4: { tier1: 145000, tier2: 185000, tier3: 205000 },
    L5: { tier1: 175000, tier2: 215000, tier3: 245000 },
    L6: { tier1: 220000, tier2: 245000, tier3: 270000 },
    L7: { tier1: 280000, tier2: 285000, tier3: 320000 }
  };

  function calculateOfferTimeline(offer) {
    const annualBonus = offer.base * offer.bonusPct;
    const yearlyTc = offer.vesting.map(function (pct, i) {
      const equity = offer.rsuGrant * pct;
      const signOn = (offer.signOn && offer.signOn[i]) || 0;
      return Math.round(offer.base + annualBonus + equity + signOn);
    });
    const fourYearTotal = yearlyTc.reduce(function (sum, v) { return sum + v; }, 0);
    const fourYearAvg = Math.round(fourYearTotal / yearlyTc.length);
    const year1ToYear2Delta = yearlyTc.length > 1 ? yearlyTc[1] - yearlyTc[0] : 0;
    return { yearlyTc: yearlyTc, fourYearTotal: fourYearTotal, fourYearAvg: fourYearAvg, year1ToYear2Delta: year1ToYear2Delta };
  }

  function locateBandPercentile(tc, band) {
    const span = band.max - band.min;
    const rawPct = span > 0 ? ((tc - band.min) / span) * 100 : 0;
    const percentile = Math.round(rawPct * 10) / 10;
    const headroomToMax = Math.max(0, Math.round(band.max - tc));

    let zone = 'Above Band';
    if (percentile < 0) zone = 'Below Band';
    else if (percentile < 50) zone = 'Bottom Half';
    else if (percentile < 80) zone = 'Upper Half';
    else if (percentile <= 100) zone = 'Top-of-Band';

    return { percentile: percentile, headroomToMax: headroomToMax, zone: zone };
  }

  function modelEquityVesting(grantAmount, schedule, growthMultipliers) {
    const yearlyValue = schedule.map(function (pct, i) {
      return Math.round(grantAmount * pct * growthMultipliers[i]);
    });
    const totalRealized = yearlyValue.reduce(function (sum, v) { return sum + v; }, 0);
    const year1SharePct = totalRealized > 0 ? Math.round((yearlyValue[0] / totalRealized) * 1000) / 10 : 0;
    return { yearlyValue: yearlyValue, totalRealized: totalRealized, year1SharePct: year1SharePct };
  }

  function startupOptionEV(optionsCount, strikePrice, current409a, exitSharePrices, probabilities) {
    let evSum = 0;
    let underwaterCount = 0;
    for (let i = 0; i < exitSharePrices.length; i++) {
      const price = exitSharePrices[i];
      if (price <= strikePrice) underwaterCount += 1;
      const spread = Math.max(0, price - strikePrice) * optionsCount;
      evSum += spread * probabilities[i];
    }
    return {
      expectedValue: Math.round(evSum),
      paperGainAt409a: Math.round(Math.max(0, current409a - strikePrice) * optionsCount),
      underwaterCount: underwaterCount
    };
  }

  function calculateNetRealValue(pkg) {
    const afterTaxCash = Math.round(pkg.grossTc * (1 - pkg.effectiveTaxRate));
    const nominalNetTotal = afterTaxCash + (pkg.match401k || 0) + (pkg.esppBenefit || 0);
    const colAdjustedRealValue = Math.round((nominalNetTotal / (pkg.colIndex || 100)) * 100);
    return { afterTaxCash: afterTaxCash, nominalNetTotal: nominalNetTotal, colAdjustedRealValue: colAdjustedRealValue };
  }

  function clawbackExposure(signOnAmount, monthsStayed, requiredMonths, proRated) {
    if (monthsStayed >= requiredMonths) {
      return { owedAmount: 0, netKept: signOnAmount };
    }
    const owedAmount = proRated
      ? Math.round(signOnAmount * ((requiredMonths - monthsStayed) / requiredMonths))
      : signOnAmount;
    return { owedAmount: owedAmount, netKept: signOnAmount - owedAmount };
  }

  function generateCounterTarget(initialTc, bandMax, competingTc) {
    const rawTarget = Math.max(
      Math.round(initialTc * 1.15),
      Math.round(bandMax * 0.92),
      Math.round((competingTc || 0) * 1.05)
    );
    const counterTc = Math.min(Math.round(bandMax * 1.05), rawTarget);
    const askDelta = counterTc - initialTc;
    const askPct = initialTc > 0 ? Math.round((askDelta / initialTc) * 1000) / 10 : 0;
    return { counterTc: counterTc, askDelta: askDelta, askPct: askPct };
  }

  function swapCompLevers(desiredBase, baseCap, rsuYears) {
    if (desiredBase <= baseCap) {
      return { cappedBase: desiredBase, annualBaseGap: 0, extraSignOn: 0, extraRsuGrant: 0 };
    }
    const annualBaseGap = desiredBase - baseCap;
    return {
      cappedBase: baseCap,
      annualBaseGap: annualBaseGap,
      extraSignOn: annualBaseGap,
      extraRsuGrant: annualBaseGap * (rsuYears || 4)
    };
  }

  function projectFiveYearWithRefreshers(base, bonusPct, initial4YrRsu, annual4YrRefresher) {
    const cash = base * (1 + bonusPct);
    const initAnnual = initial4YrRsu * 0.25;
    const refSlice = annual4YrRefresher * 0.25;

    const yearlyTc = [0, 1, 2, 3, 4].map(function (i) {
      const initialPart = i < 4 ? initAnnual : 0;
      const refresherPart = i * refSlice;
      return Math.round(cash + initialPart + refresherPart);
    });
    const year4ToYear5Delta = yearlyTc[4] - yearlyTc[3];
    return { yearlyTc: yearlyTc, year4ToYear5Delta: year4ToYear5Delta, hasYear5Cliff: year4ToYear5Delta < 0 };
  }

  function calculateMakeWholeGrant(unvestedSharesY1, unvestedSharesY2, currentSharePrice, switchPremiumPct) {
    const forfeitedY1 = Math.round(unvestedSharesY1 * currentSharePrice);
    const forfeitedY2 = Math.round(unvestedSharesY2 * currentSharePrice);
    return {
      forfeitedY1: forfeitedY1,
      forfeitedY2: forfeitedY2,
      totalForfeited: forfeitedY1 + forfeitedY2,
      signOnBridge: Math.round(forfeitedY1 * (1 + switchPremiumPct)),
      extraRsuGrant: forfeitedY2 * 2
    };
  }

  function compareAndRankOffers(offers) {
    return offers
      .map(function (o) {
        const timeline = calculateOfferTimeline(o);
        const net = calculateNetRealValue({
          grossTc: timeline.fourYearAvg,
          effectiveTaxRate: o.effectiveTaxRate || 0.25,
          colIndex: o.colIndex || 100,
          match401k: o.match401k || 0,
          esppBenefit: o.esppBenefit || 0
        });
        const band = BAND_BENCHMARKS[o.level] && BAND_BENCHMARKS[o.level][o.tier]
          ? BAND_BENCHMARKS[o.level][o.tier]
          : { min: 130000, max: 180000 };
        const baseCap = BASE_CAPS[o.level] && BASE_CAPS[o.level][o.tier]
          ? BASE_CAPS[o.level][o.tier]
          : 185000;
        const bandPos = locateBandPercentile(timeline.fourYearAvg, band);
        return {
          name: o.name,
          level: o.level,
          tier: o.tier,
          base: o.base,
          bonusPct: o.bonusPct,
          baseCap: baseCap,
          year1Tc: timeline.yearlyTc[0],
          fourYearAvgTc: timeline.fourYearAvg,
          fourYearTotalTc: timeline.fourYearTotal,
          year1ToYear2Delta: timeline.year1ToYear2Delta,
          colAdjustedRealValue: net.colAdjustedRealValue,
          bandPercentile: bandPos.percentile,
          bandZone: bandPos.zone,
          headroomToMax: bandPos.headroomToMax,
          bandMax: band.max
        };
      })
      .sort(function (a, b) {
        return b.colAdjustedRealValue - a.colAdjustedRealValue;
      });
  }

  function buildForwardableEmail(params) {
    const fmt = function (n) { return '$' + Number(n).toLocaleString('en-US'); };
    return [
      'Subject: Excited about the ' + params.roleTitle + ' offer at ' + params.companyName,
      '',
      'Hi ' + params.recruiterName + ',',
      '',
      'Thank you so much for sending over the written offer—I genuinely enjoyed meeting the Android engineering team and am very excited about the technical roadmap at ' + params.companyName + '.',
      '',
      'I have been reviewing the full package carefully. Based on my production Android background (' + params.highlightImpact + ') and current market benchmarks' + (params.competingCompany ? '—alongside another active opportunity I am evaluating at ' + params.competingCompany : '') + ', I am looking to reach ' + fmt(params.targetTc) + ' in annual total compensation.',
      '',
      'If ' + params.companyName + ' has flexibility to close that gap via base salary (or a combination of a ' + fmt(params.signOnAsk) + ' Year-1 sign-on bonus and ' + fmt(params.extraRsuAsk) + ' in additional 4-year RSUs), I would be thrilled to move forward and get started.',
      '',
      'Thanks again for championing this internally—let me know what works on your side!',
      '',
      'Best,',
      params.candidateName || '[Your Name]'
    ].join('\n');
  }

  return {
    BAND_BENCHMARKS: BAND_BENCHMARKS,
    BASE_CAPS: BASE_CAPS,
    calculateOfferTimeline: calculateOfferTimeline,
    locateBandPercentile: locateBandPercentile,
    modelEquityVesting: modelEquityVesting,
    startupOptionEV: startupOptionEV,
    calculateNetRealValue: calculateNetRealValue,
    clawbackExposure: clawbackExposure,
    generateCounterTarget: generateCounterTarget,
    swapCompLevers: swapCompLevers,
    projectFiveYearWithRefreshers: projectFiveYearWithRefreshers,
    calculateMakeWholeGrant: calculateMakeWholeGrant,
    compareAndRankOffers: compareAndRankOffers,
    buildForwardableEmail: buildForwardableEmail
  };
});
