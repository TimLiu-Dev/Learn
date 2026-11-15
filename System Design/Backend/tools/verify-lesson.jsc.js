// Usage: /System/Library/Frameworks/JavaScriptCore.framework/Versions/Current/Helpers/jsc verify_lesson.js -- <lesson.html>
// Checks every CodeRunner (solution passes all tests, starter fails >= 1) and every QuizEngine (equal word counts).
var console = { log: function () { print(Array.prototype.slice.call(arguments).join(' ')); } };
var path = arguments[0];
var html = readFile(path);
var scripts = html.split('<script>').slice(1).map(function (s) { return s.split('</script>')[0]; });
var runners = [], quizzes = [];
globalThis.CodeRunner = function (id, opts) { runners.push({ id: id, opts: opts }); };
globalThis.QuizEngine = function (id, q) { quizzes.push({ id: id, q: q }); };
globalThis.document = { getElementById: function () { return null; }, querySelectorAll: function () { return []; } };
globalThis.window = globalThis;
scripts.forEach(function (s) { eval(s); });
var ok = true;
runners.forEach(function (r) {
  var syms = r.opts.tests.map(function (t) { return t.exportSymbol; }).filter(Boolean)
    .filter(function (v, i, a) { return a.indexOf(v) === i; });
  ['solutionCode', 'starterCode'].forEach(function (k) {
    var pass = 0, fails = [];
    r.opts.tests.forEach(function (t) {
      try { var ex = new Function(r.opts[k] + '\n; return {' + syms.join(',') + '};')(); t.testFn(ex); pass++; }
      catch (e) { fails.push(t.name + ' -> ' + e.message); }
    });
    console.log('[' + r.id + '] ' + k + ': ' + pass + '/' + r.opts.tests.length);
    if (k === 'solutionCode' && pass !== r.opts.tests.length) { ok = false; fails.forEach(function (f) { console.log('   FAIL ' + f); }); }
    if (k === 'starterCode' && pass === r.opts.tests.length) { ok = false; console.log('   starter passes everything (should fail at least one)'); }
  });
  if (!r.opts.successMessage) console.log('   WARN no successMessage');
});
quizzes.forEach(function (z) {
  var wc = z.q.options.map(function (o) { return o.text.trim().split(/\s+/).length; });
  var same = wc.every(function (w) { return w === wc[0]; });
  if (!same) ok = false;
  console.log('[quiz ' + z.id + '] words ' + wc.join(',') + (same ? ' OK' : ' MISMATCH') + ' | chars ' + z.q.options.map(function (o) { return o.text.length; }).join(','));
});
console.log('runners=' + runners.length + ' quizzes=' + quizzes.length + ' RESULT=' + (ok && runners.length && quizzes.length >= 3 ? 'PASS' : 'FAIL'));
