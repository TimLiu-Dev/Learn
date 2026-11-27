
const fs = require('fs');
const path = require('path');
const vm = require('vm');

const runtimeCode = fs.readFileSync('assets/lesson-runtime.js', 'utf8');
const sandbox = { window: { addEventListener: () => {}, location: { pathname: '/lessons/0001.html' } }, document: { getElementById: () => null, querySelectorAll: () => [], createElement: () => ({ style: {}, appendChild: () => {}, addEventListener: () => {} }) }, localStorage: { getItem: () => null, setItem: () => {}, removeItem: () => {} }, navigator: {}, console };
sandbox.global = sandbox;
sandbox.window.window = sandbox.window;
sandbox.window.document = sandbox.document;
sandbox.window.localStorage = sandbox.localStorage;
vm.createContext(sandbox);
vm.runInContext(runtimeCode, sandbox);
const LR = sandbox.LessonRuntime || sandbox.window.LessonRuntime;
const compileFn = Object.values(LR).find(v => typeof v === 'function' && String(v).includes('_transpilePython'));

function runOneSuite(lang, code, paCfg) {
  let compiledInfo;
  try {
    compiledInfo = compileFn(code, lang, paCfg);
  } catch (e) {
    return { allOk: false, detail: 'Compile error: ' + e.message };
  }
  for (let ti = 0; ti < paCfg.testCases.length; ti++) {
    const tc = paCfg.testCases[ti];
    try {
      const out = LR._runTestCase(compiledInfo, tc, paCfg);
      const ok = LR._compareResult(out.actual, tc, paCfg);
      if (!ok) {
        return { allOk: false, detail: `TC#${ti+1} expected=${JSON.stringify(tc.expected)} actual=${JSON.stringify(out.actual)}` };
      }
    } catch (e) {
      return { allOk: false, detail: `TC#${ti+1} runtime error: ${e.message}` };
    }
  }
  return { allOk: paCfg.testCases.length > 0, detail: 'OK' };
}

function extractBalanced(s, openIdx, openCh, closeCh) {
  let depth = 1, i = openIdx + 1, inStr = null, esc = false;
  while (i < s.length && depth > 0) {
    const ch = s[i];
    if (esc) esc = false;
    else if (ch === '\\') esc = true;
    else if (inStr) { if (ch === inStr) inStr = null; }
    else if (ch === '"' || ch === "'" || ch === '`') inStr = ch;
    else if (ch === openCh) depth++;
    else if (ch === closeCh) depth--;
    i++;
  }
  return i - 1;
}

const files = fs.readdirSync('lessons').filter(f => f.endsWith('.html')).sort();
if (files.length !== 126) {
  console.error('Expected 126 lessons, found', files.length);
  process.exit(1);
}

let modelPass = 0, modelFail = [];
let stubFalsePass = [];
let quizTotal = 0, quizLenFail = [], quizSameLetter = [];
let stepperFail = [];

for (const f of files) {
  const full = path.join('lessons', f);
  const doc = fs.readFileSync(full, 'utf8');

  const mPa = doc.match(/LessonRuntime\.PracticeArena\.init\(\s*\{/);
  if (!mPa) { modelFail.push(`${f}: missing PracticeArena.init`); continue; }
  const paOpen = mPa.index + mPa[0].length - 1;
  const paClose = extractBalanced(doc, paOpen, '{', '}');
  const paCfg = vm.runInNewContext('(' + doc.slice(paOpen, paClose + 1) + ')');

  for (const lang of ['kotlin', 'python', 'java']) {
    const mRes = runOneSuite(lang, paCfg.modelSolutions[lang], paCfg);
    if (mRes.allOk) modelPass++;
    else modelFail.push(`${f} [${lang}]: ${mRes.detail}`);

    const sRes = runOneSuite(lang, paCfg.starterStubs[lang], paCfg);
    if (sRes.allOk) stubFalsePass.push(`${f} [${lang}]`);
  }

  if (f.startsWith('0035-')) {
    const shallowRes = runOneSuite('kotlin', 'class Solution { fun copyRandomList(node: Node?): Node? { return node } }', paCfg);
    if (shallowRes.allOk) modelFail.push('0035 shallow copy was NOT rejected!');
  }
  if (f.startsWith('0082-')) {
    const shallowRes = runOneSuite('kotlin', 'class Solution { fun cloneGraph(node: Node?): Node? { return node } }', paCfg);
    if (shallowRes.allOk) modelFail.push('0082 shallow copy was NOT rejected!');
  }

  const mQz = doc.match(/LessonRuntime\.Quiz\.init\(\s*\{/);
  if (!mQz) { quizLenFail.push(`${f}: missing Quiz.init`); continue; }
  const qzOpen = mQz.index + mQz[0].length - 1;
  const qzClose = extractBalanced(doc, qzOpen, '{', '}');
  const qzCfg = vm.runInNewContext('(' + doc.slice(qzOpen, qzClose + 1) + ')');
  if (!qzCfg.problemId) quizLenFail.push(`${f}: missing problemId in Quiz.init`);
  const qs = qzCfg.questions || [];
  if (qs.length !== 4) quizLenFail.push(`${f}: expected 4 questions, got ${qs.length}`);
  const rightIds = [];
  qs.forEach((q, qi) => {
    quizTotal++;
    const counts = q.options.map(o => o.text.trim().split(/\s+/).filter(Boolean).length);
    if (new Set(counts).size !== 1) {
      quizLenFail.push(`${f} Q${qi+1}: unequal word counts ${JSON.stringify(counts)}`);
    }
    const right = q.options.find(o => o.isCorrect);
    if (right) rightIds.push(right.id);
  });
  if (new Set(rightIds).size === 1) quizSameLetter.push(`${f}: all ${rightIds[0]}`);

  // Inline scripts (excluding tailwind.config)
  const inlineScripts = [...doc.matchAll(/<script>([\s\S]*?)<\/script>/gi)]
    .map(m => m[1])
    .filter(s => !s.includes('tailwind.config'))
    .join('\n;\n');
  const domCbs = [];
  const dummyEl = () => ({
    style: {}, classList: { add:()=>{}, remove:()=>{}, toggle:()=>{}, contains:()=>false },
    innerHTML: '', textContent: '', innerText: '', value: '[1,2,3,1]', disabled: false, width: 600, height: 300,
    getContext: () => ({ clearRect:()=>{}, fillRect:()=>{}, strokeRect:()=>{}, scale:()=>{}, beginPath:()=>{}, closePath:()=>{}, moveTo:()=>{}, lineTo:()=>{}, stroke:()=>{}, arc:()=>{}, fill:()=>{}, fillText:()=>{}, getImageData:()=>({data:[]}), putImageData:()=>{} }),
    getBoundingClientRect: () => ({ width: 600, height: 300, left: 0, top: 0 }),
    addEventListener: () => {}, appendChild: () => {}, setAttribute: () => {}, getAttribute: () => '', querySelectorAll: () => []
  });
  const stCtx = {
    console: { log:()=>{}, warn:()=>{}, error:()=>{} },
    Math, JSON, Array, Object, String, Number, Boolean, Set, Map, RegExp, Date, parseInt, parseFloat, isNaN, Infinity,
    localStorage: { getItem: () => null, setItem: () => {}, removeItem: () => {} },
    navigator: { clipboard: { writeText: () => Promise.resolve() } },
    setTimeout: () => 1, clearTimeout: () => {}, setInterval: () => 1, clearInterval: () => {},
    updateRecallBadge: ()=>{},
    document: { getElementById: () => dummyEl(), querySelector: () => dummyEl(), querySelectorAll: () => [], createElement: () => dummyEl() },
    window: { devicePixelRatio: 1, addEventListener: (ev, cb) => { if (ev === 'DOMContentLoaded' || ev === 'load') domCbs.push(cb); } }
  };
  stCtx.window.document = stCtx.document;
  stCtx.window.localStorage = stCtx.localStorage;
  try {
    stCtx.global = stCtx; stCtx.window.window = stCtx.window; vm.createContext(stCtx);
    vm.runInContext(runtimeCode, stCtx); stCtx.LessonRuntime = stCtx.window.LessonRuntime;
    vm.runInContext(inlineScripts, stCtx);
    domCbs.forEach(cb => cb());
    const stepsLen = vm.runInContext(`
      (typeof steps !== "undefined" && Array.isArray(steps) ? steps.length :
      (typeof STEPS !== "undefined" && Array.isArray(STEPS) ? STEPS.length :
      (typeof traceSteps !== "undefined" && Array.isArray(traceSteps) ? traceSteps.length :
      (typeof stepperSteps !== "undefined" && Array.isArray(stepperSteps) ? stepperSteps.length : 2))))
    `, stCtx);
    if (stepsLen < 2) {
      stepperFail.push(`${f}: stepsLen=${stepsLen}`);
    }
  } catch (e) {
    stepperFail.push(`${f}: threw ${e.message}`);
  }
}

console.log(JSON.stringify({
  lessons: files.length,
  modelPass,
  modelFail,
  stubFalsePass,
  quizTotal,
  quizLenFail,
  quizSameLetter,
  stepperFail,
  curriculumLen: (LR.CURRICULUM || []).length,
  studyOrderLen: (LR.STUDY_ORDER || []).length
}, null, 2));

if (modelFail.length || stubFalsePass.length || quizLenFail.length || quizSameLetter.length || stepperFail.length) {
  process.exit(1);
}
