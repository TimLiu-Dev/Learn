/**
 * Mid-Tech LeetCode Masterclass — Shared Runtime Engine
 * 
 * Includes:
 * 1. High-DPI Vector Whiteboard & Sketchpad
 * 2. Multi-Approach & Tri-Language Code Viewer with Clipboard Copy
 * 3. Runnable Tri-Language Practice Arena (Kotlin, Python, Java) with 3 Hints & Model Reveal
 * 4. 4-Question Diagnostic Code Quiz Engine with Per-Option Right/Wrong Explanations
 * 5. 48-Hour Spaced-Repetition Recall Tracker
 */

(function(global) {
  'use strict';

  var LessonRuntime = {};

  // =========================================================================
  // 1. HIGH-DPI VECTOR WHITEBOARD ENGINE
  // =========================================================================
  LessonRuntime.Whiteboard = {
    init: function(config) {
      var canvas = document.getElementById(config.canvasId || 'whiteboard-canvas');
      if (!canvas) return;

      var ctx = canvas.getContext('2d');
      var strokes = [];
      var currentStroke = null;
      var currentColor = '#10b981';
      var currentSize = 4;
      var currentMode = 'draw'; // 'draw' or 'erase'

      function resize() {
        var rect = canvas.getBoundingClientRect();
        var dpr = window.devicePixelRatio || 1;
        canvas.width = rect.width * dpr;
        canvas.height = rect.height * dpr;
        ctx.scale(dpr, dpr);
        redraw();
      }

      function getCoords(e) {
        var rect = canvas.getBoundingClientRect();
        var clientX = e.touches ? e.touches[0].clientX : e.clientX;
        var clientY = e.touches ? e.touches[0].clientY : e.clientY;
        return {
          x: clientX - rect.left,
          y: clientY - rect.top
        };
      }

      function redraw() {
        var rect = canvas.getBoundingClientRect();
        ctx.clearRect(0, 0, rect.width, rect.height);
        for (var i = 0; i < strokes.length; i++) {
          var s = strokes[i];
          if (!s.points || s.points.length < 2) continue;
          ctx.strokeStyle = s.color;
          ctx.lineWidth = s.size;
          ctx.lineCap = 'round';
          ctx.lineJoin = 'round';
          ctx.beginPath();
          ctx.moveTo(s.points[0].x, s.points[0].y);
          for (var p = 1; p < s.points.length; p++) {
            ctx.lineTo(s.points[p].x, s.points[p].y);
          }
          ctx.stroke();
        }
      }

      function startDraw(e) {
        if (e.touches) e.preventDefault();
        var pos = getCoords(e);
        currentStroke = {
          color: currentMode === 'erase' ? '#020617' : currentColor,
          size: currentMode === 'erase' ? currentSize * 4 : currentSize,
          points: [pos]
        };
        strokes.push(currentStroke);
        var hint = document.getElementById(config.hintId || 'wb-hint');
        if (hint) hint.style.display = 'none';
      }

      function moveDraw(e) {
        if (!currentStroke) return;
        if (e.touches) e.preventDefault();
        var pos = getCoords(e);
        currentStroke.points.push(pos);
        var pts = currentStroke.points;
        if (pts.length >= 2) {
          ctx.strokeStyle = currentStroke.color;
          ctx.lineWidth = currentStroke.size;
          ctx.lineCap = 'round';
          ctx.lineJoin = 'round';
          ctx.beginPath();
          ctx.moveTo(pts[pts.length - 2].x, pts[pts.length - 2].y);
          ctx.lineTo(pts[pts.length - 1].x, pts[pts.length - 1].y);
          ctx.stroke();
        }
      }

      function endDraw() {
        currentStroke = null;
      }

      canvas.addEventListener('mousedown', startDraw);
      window.addEventListener('mousemove', moveDraw);
      window.addEventListener('mouseup', endDraw);
      canvas.addEventListener('touchstart', startDraw, { passive: false });
      window.addEventListener('touchmove', moveDraw, { passive: false });
      window.addEventListener('touchend', endDraw);
      window.addEventListener('resize', resize);

      // Public controls
      window.wbSetColor = function(color) {
        currentColor = color;
        currentMode = 'draw';
        document.querySelectorAll('.wb-color-btn').forEach(function(btn) {
          if (btn.getAttribute('data-color') === color) {
            btn.className = "wb-color-btn w-6 h-6 rounded-full ring-2 ring-white ring-offset-2 ring-offset-slate-950 transition-all";
          } else {
            btn.className = "wb-color-btn w-6 h-6 rounded-full hover:scale-110 transition-all";
          }
        });
        var penBtn = document.getElementById('wb-tool-pen');
        var eraserBtn = document.getElementById('wb-tool-eraser');
        if (penBtn && eraserBtn) {
          penBtn.className = "px-2.5 py-1 rounded bg-teal-500/20 text-teal-300 font-bold transition-colors";
          eraserBtn.className = "px-2.5 py-1 rounded text-slate-400 hover:text-white transition-colors";
        }
      };

      window.wbSetMode = function(mode) {
        currentMode = mode;
        var penBtn = document.getElementById('wb-tool-pen');
        var eraserBtn = document.getElementById('wb-tool-eraser');
        if (mode === 'draw') {
          if (penBtn) penBtn.className = "px-2.5 py-1 rounded bg-teal-500/20 text-teal-300 font-bold transition-colors";
          if (eraserBtn) eraserBtn.className = "px-2.5 py-1 rounded text-slate-400 hover:text-white transition-colors";
        } else {
          if (eraserBtn) eraserBtn.className = "px-2.5 py-1 rounded bg-teal-500/20 text-teal-300 font-bold transition-colors";
          if (penBtn) penBtn.className = "px-2.5 py-1 rounded text-slate-400 hover:text-white transition-colors";
        }
      };

      window.wbSetSize = function(size) {
        currentSize = size;
        [2, 4, 8].forEach(function(s) {
          var btn = document.getElementById('wb-size-' + s);
          if (btn) {
            if (s === size) {
              btn.className = "wb-size-btn px-2 py-0.5 rounded bg-teal-500/20 border border-teal-500/40 text-[11px] text-teal-300 font-bold";
            } else {
              btn.className = "wb-size-btn px-2 py-0.5 rounded bg-slate-900 border border-slate-800 text-[11px] text-slate-400";
            }
          }
        });
      };

      window.wbUndo = function() {
        strokes.pop();
        redraw();
      };

      window.wbClear = function() {
        strokes = [];
        redraw();
        var hint = document.getElementById(config.hintId || 'wb-hint');
        if (hint) hint.style.display = 'block';
      };

      window.wbToggle = function() {
        var body = document.getElementById(config.bodyId || 'wb-body');
        var icon = document.getElementById(config.toggleIconId || 'wb-toggle-icon');
        var text = document.getElementById(config.toggleTextId || 'wb-toggle-text');
        if (!body) return;
        if (body.classList.contains('hidden')) {
          body.classList.remove('hidden');
          if (icon) icon.innerText = '▲';
          if (text) text.innerText = 'Minimize Board';
          setTimeout(resize, 50);
        } else {
          body.classList.add('hidden');
          if (icon) icon.innerText = '▼';
          if (text) text.innerText = 'Open Sketchpad';
        }
      };

      setTimeout(resize, 50);
    }
  };

  // =========================================================================
  // 2. MULTI-APPROACH & TRI-LANGUAGE CODE TABS
  // =========================================================================
  LessonRuntime.MultiApproach = {
    init: function(config) {
      var currentApproach = config.defaultApproach || 'optimal';
      var currentLang = config.defaultLang || 'kotlin';

      function updateUI() {
        // Approach buttons
        ['brute', 'better', 'optimal'].forEach(function(a) {
          var btn = document.getElementById('tab-app-' + a);
          if (btn) {
            if (a === currentApproach) {
              btn.className = "approach-btn active px-3 py-1.5 rounded-lg text-xs font-mono font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 transition-all shadow-sm";
            } else {
              btn.className = "approach-btn px-3 py-1.5 rounded-lg text-xs font-mono font-semibold text-slate-400 hover:text-white transition-all";
            }
          }
        });

        // Complexity pill
        var pill = document.getElementById('approach-complexity-pill');
        if (pill && config.complexity && config.complexity[currentApproach]) {
          pill.innerHTML = config.complexity[currentApproach].html;
          pill.className = "text-xs font-mono px-3 py-1.5 rounded-lg " + config.complexity[currentApproach].className;
        }

        // Language buttons
        ['kotlin', 'python', 'java'].forEach(function(l) {
          var btn = document.getElementById('lang-btn-' + l);
          if (btn) {
            if (l === currentLang) {
              btn.className = "lang-tab active px-4 py-2 rounded-t-lg text-xs font-bold font-mono border-b-2 border-emerald-400 text-emerald-400 bg-slate-900";
            } else {
              btn.className = "lang-tab px-4 py-2 rounded-t-lg text-xs font-bold font-mono border-b-2 border-transparent text-slate-400 hover:text-slate-200";
            }
          }
        });

        // Code cards visibility
        document.querySelectorAll('.code-solution-card').forEach(function(c) {
          c.classList.add('hidden');
        });
        var target = document.getElementById('code-' + currentApproach + '-' + currentLang);
        if (target) target.classList.remove('hidden');
      }

      window.switchApproach = function(app) {
        currentApproach = app;
        updateUI();
      };

      window.switchLang = function(lang) {
        currentLang = lang;
        updateUI();
      };

      window.copyCurrentCode = function() {
        var target = document.querySelector('#code-' + currentApproach + '-' + currentLang + ' pre');
        if (target) {
          var text = target.innerText;
          navigator.clipboard.writeText(text).then(function() {
            var icon = document.getElementById('copy-icon');
            var label = document.getElementById('copy-text');
            if (icon) icon.innerText = '✓';
            if (label) label.innerText = 'Copied!';
            setTimeout(function() {
              if (icon) icon.innerText = '📋';
              if (label) label.innerText = 'Copy Code';
            }, 2000);
          });
        }
      };

      updateUI();
    }
  };

  // =========================================================================
  // 3. RUNNABLE CODE PRACTICE ARENA (JVM-SUBSET & PYTHON RUNNER)
  // =========================================================================
  LessonRuntime.PracticeArena = {
    init: function(config) {
      var currentLang = 'kotlin';
      var problemId = config.problemId || 'lc_practice';
      var textarea = document.getElementById('practice-editor');
      if (!textarea) return;

      var storageKey = 'lc103_practice_code_' + problemId + '_';

      function loadCodeForLang(lang) {
        var saved = localStorage.getItem(storageKey + lang);
        if (saved) return saved;
        return config.starterStubs[lang] || '';
      }

      function setLang(lang) {
        currentLang = lang;
        ['kotlin', 'python', 'java'].forEach(function(l) {
          var btn = document.getElementById('practice-lang-' + l);
          if (btn) {
            if (l === lang) {
              btn.className = "px-3 py-1 rounded bg-indigo-600 text-white font-bold transition-colors";
            } else {
              btn.className = "px-3 py-1 rounded text-slate-400 hover:text-white transition-colors";
            }
          }
        });

        var filenameMap = { kotlin: 'Solution.kt', python: 'solution.py', java: 'Solution.java' };
        var fnEl = document.getElementById('practice-filename');
        if (fnEl) fnEl.innerText = filenameMap[lang];

        textarea.value = loadCodeForLang(lang);
      }

      // Tab indentation support (4 spaces)
      textarea.addEventListener('keydown', function(e) {
        if (e.key === 'Tab') {
          e.preventDefault();
          var start = this.selectionStart;
          var end = this.selectionEnd;
          this.value = this.value.substring(0, start) + "    " + this.value.substring(end);
          this.selectionStart = this.selectionEnd = start + 4;
        } else if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
          e.preventDefault();
          window.runPracticeCode();
        }
      });

      // Auto-save on typing
      textarea.addEventListener('input', function() {
        localStorage.setItem(storageKey + currentLang, textarea.value);
      });

      window.setPracticeLang = setLang;

      window.resetPracticeStub = function() {
        if (confirm("Reset current code back to the cold empty starter stub?")) {
          var stub = config.starterStubs[currentLang] || '';
          textarea.value = stub;
          localStorage.setItem(storageKey + currentLang, stub);
          var consoleBox = document.getElementById('practice-console');
          if (consoleBox) consoleBox.classList.add('hidden');
        }
      };

      // 3 Progressive Hints logic
      var currentHintStep = 0;
      window.revealNextHint = function() {
        if (currentHintStep < config.hints.length) {
          currentHintStep++;
          var hintContainer = document.getElementById('practice-hints-container');
          if (hintContainer) {
            hintContainer.classList.remove('hidden');
            var card = document.createElement('div');
            card.className = "p-3.5 rounded-xl bg-slate-950 border border-indigo-900/50 text-xs font-mono space-y-1 animate-subtle";
            card.innerHTML = "<div class='text-indigo-400 font-bold uppercase text-[10px]'>💡 Progressive Hint " + currentHintStep + " of " + config.hints.length + ":</div><p class='text-slate-300 leading-relaxed'>" + config.hints[currentHintStep - 1] + "</p>";
            hintContainer.appendChild(card);
          }
          var hintBtn = document.getElementById('practice-hint-btn');
          if (hintBtn) {
            if (currentHintStep < config.hints.length) {
              hintBtn.innerText = "💡 Need another hint? (" + currentHintStep + "/" + config.hints.length + " used)";
            } else {
              hintBtn.innerText = "✓ All " + config.hints.length + " Hints Revealed";
              hintBtn.disabled = true;
              hintBtn.className = "text-xs font-mono text-slate-500 cursor-not-allowed";
            }
          }
        }
      };

      // Final Answer Reveal Logic
      window.toggleModelSolution = function() {
        var box = document.getElementById('practice-model-box');
        if (!box) return;
        if (box.classList.contains('hidden')) {
          box.classList.remove('hidden');
          var pre = document.getElementById('practice-model-code');
          if (pre) pre.innerText = config.modelSolutions[currentLang] || '';
        } else {
          box.classList.add('hidden');
        }
      };

      window.loadModelIntoEditor = function() {
        if (confirm("Replace editor code with the official model solution?")) {
          var sol = config.modelSolutions[currentLang] || '';
          textarea.value = sol;
          localStorage.setItem(storageKey + currentLang, sol);
          window.runPracticeCode();
        }
      };

      // Code Execution Engine
      window.runPracticeCode = function() {
        var code = textarea.value;
        var consoleBox = document.getElementById('practice-console');
        var dot = document.getElementById('practice-status-dot');
        var statusText = document.getElementById('practice-status-text');
        var output = document.getElementById('practice-output');
        if (!consoleBox) return;

        consoleBox.classList.remove('hidden');

        var runtime = LessonRuntime._createRuntimeEnv();
        var transpiled;
        try {
          if (currentLang === 'python') {
            transpiled = LessonRuntime._transpilePython(code, config.methodName);
          } else {
            transpiled = LessonRuntime._transpileJVM(code, config.methodName);
          }
        } catch (transpileErr) {
          if (dot) dot.className = "w-2.5 h-2.5 rounded-full bg-red-400";
          if (statusText) {
            statusText.className = "text-red-400 font-bold text-xs";
            statusText.innerText = "Compilation / Syntax Error";
          }
          output.innerHTML = "<div class='p-3 rounded-lg bg-red-950/60 border border-red-800 text-red-200 text-xs font-mono'><strong>Syntax Issue:</strong> " + transpileErr.message + "</div>";
          return;
        }

        var fn;
        try {
          var factory = new Function(
            '__sub', '__sort', '__HashSet', '__HashMap', '__ArrayList', '__StringBuilder', 'buildString', 'IntArray', 'BooleanArray', 'ord', 'tuple',
            'return ' + transpiled.code + ';'
          );
          fn = factory(
            runtime.__sub, runtime.__sort, runtime.__HashSet, runtime.__HashMap, runtime.__ArrayList, runtime.__StringBuilder, runtime.buildString, runtime.IntArray, runtime.BooleanArray, runtime.ord, runtime.tuple
          );
        } catch (compileErr) {
          if (dot) dot.className = "w-2.5 h-2.5 rounded-full bg-red-400";
          if (statusText) {
            statusText.className = "text-red-400 font-bold text-xs";
            statusText.innerText = "Compile Error";
          }
          output.innerHTML = "<div class='p-3 rounded-lg bg-red-950/60 border border-red-800 text-red-200 text-xs font-mono'><strong>Error:</strong> " + compileErr.message + "</div>";
          return;
        }

        // Run against official test cases + custom test case if present
        var testCases = config.testCases.slice();
        var customInputVal = document.getElementById('practice-custom-input');
        if (customInputVal && customInputVal.value.trim()) {
          try {
            var parsedCustom = JSON.parse('[' + customInputVal.value.trim() + ']');
            testCases.push({
              input: parsedCustom,
              isCustom: true
            });
          } catch (e) {
            // ignore malformed custom json
          }
        }

        var startTime = performance.now();
        var allPassed = true;
        var resultsHtml = '';

        for (var i = 0; i < testCases.length; i++) {
          var tc = testCases[i];
          var actual;
          var error = null;
          try {
            // Clone input so mutations in-place don't pollute subsequent assertions
            var inputClone = JSON.parse(JSON.stringify(tc.input));
            actual = fn.apply(null, inputClone);
          } catch (err) {
            error = err.message;
            allPassed = false;
          }

          var passed = false;
          if (!error) {
            if (tc.isCustom) {
              passed = true; // custom cases just show output
            } else if (config.comparator) {
              passed = config.comparator(actual, tc.expected);
            } else {
              passed = (JSON.stringify(actual) === JSON.stringify(tc.expected));
            }
            if (!passed) allPassed = false;
          }

          var statusBadge = '';
          if (error) {
            statusBadge = "<span class='px-2 py-0.5 rounded bg-red-950/80 border border-red-800 text-red-300 font-bold'>ERROR</span>";
          } else if (tc.isCustom) {
            statusBadge = "<span class='px-2 py-0.5 rounded bg-indigo-950/80 border border-indigo-800 text-indigo-300 font-bold'>CUSTOM</span>";
          } else if (passed) {
            statusBadge = "<span class='px-2 py-0.5 rounded bg-emerald-950/80 border border-emerald-800 text-emerald-300 font-bold'>PASS ✓</span>";
          } else {
            statusBadge = "<span class='px-2 py-0.5 rounded bg-red-950/80 border border-red-800 text-red-300 font-bold'>FAIL ✗</span>";
          }

          var inputStr = JSON.stringify(tc.input[0]);
          if (tc.input.length > 1) inputStr = tc.input.map(function(a) { return JSON.stringify(a); }).join(', ');
          var expectedStr = tc.isCustom ? 'User Input' : JSON.stringify(tc.expected);
          var actualStr = error ? ('Exception: ' + error) : JSON.stringify(actual);

          resultsHtml += "<div class='p-3 rounded-xl bg-slate-900 border " + (passed ? 'border-slate-800' : 'border-red-900/60') + " space-y-1.5 text-xs font-mono'>" +
            "<div class='flex items-center justify-between'>" +
              "<span class='" + (passed ? 'text-emerald-400' : 'text-red-400') + " font-bold'>" + (tc.isCustom ? 'Custom Test Case:' : ('Test Case ' + (i + 1) + ':')) + "</span>" +
              statusBadge +
            "</div>" +
            "<div class='text-slate-400 truncate'><strong>Input:</strong> <span class='text-slate-200'>" + inputStr + "</span></div>" +
            (tc.isCustom ? "" : "<div class='text-slate-400'><strong>Expected:</strong> <span class='text-emerald-300'>" + expectedStr + "</span></div>") +
            "<div class='text-slate-400'><strong>Output:</strong> <span class='" + (passed ? 'text-cyan-300' : 'text-red-300') + "'>" + actualStr + "</span></div>" +
          "</div>";
        }

        var elapsedMs = Math.round((performance.now() - startTime) * 10) / 10;

        if (allPassed) {
          if (dot) dot.className = "w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse";
          if (statusText) {
            statusText.className = "text-emerald-400 font-bold text-xs";
            statusText.innerText = "All " + config.testCases.length + " Test Cases Passed (" + elapsedMs + "ms)";
          }
        } else {
          if (dot) dot.className = "w-2.5 h-2.5 rounded-full bg-red-400";
          if (statusText) {
            statusText.className = "text-red-400 font-bold text-xs";
            statusText.innerText = "Test Suite Failed (" + elapsedMs + "ms)";
          }
        }

        output.innerHTML = "<div class='space-y-2'>" + resultsHtml + "</div>";
      };

      setLang('kotlin');
    }
  };

  // =========================================================================
  // 4. FOUR-QUESTION DIAGNOSTIC CODE QUIZ ENGINE
  // =========================================================================
  LessonRuntime.Quiz = {
    init: function(config) {
      var container = document.getElementById(config.containerId || 'quiz-container');
      if (!container) return;

      var questions = config.questions || [];
      var userAnswers = {};

      function render() {
        var html = '';
        for (var qIdx = 0; qIdx < questions.length; qIdx++) {
          var q = questions[qIdx];
          var qNum = qIdx + 1;
          var codeSnippetHtml = q.codeSnippet ? 
            "<pre class='p-3.5 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-cyan-300 overflow-x-auto leading-relaxed'>" + q.codeSnippet + "</pre>" : "";

          var optionsHtml = '';
          for (var oIdx = 0; oIdx < q.options.length; oIdx++) {
            var opt = q.options[oIdx];
            optionsHtml += "<button onclick='window.checkQuizChoice(" + qNum + ", \"" + opt.id + "\")' id='quiz-opt-" + qNum + "-" + opt.id + "' class='w-full text-left p-3.5 rounded-xl bg-slate-950 border border-slate-800 hover:border-slate-700 text-slate-300 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed'>" +
              "<span class='px-2 py-0.5 rounded bg-slate-900 border border-slate-800 text-slate-400 font-bold uppercase text-[11px]'>" + opt.id + "</span>" +
              "<span class='flex-1'>" + opt.text + "</span>" +
            "</button>";
          }

          html += "<div class='p-6 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-4 shadow-xl' id='quiz-card-" + qNum + "'>" +
            "<div class='flex items-center justify-between text-xs font-mono'>" +
              "<span class='text-emerald-400 font-bold uppercase tracking-wider'>Diagnostic Check " + qNum + " of " + questions.length + "</span>" +
              "<span class='text-slate-500'>" + (q.category || 'Concept Check') + "</span>" +
            "</div>" +
            "<h3 class='text-sm sm:text-base font-bold text-white leading-relaxed'>" + q.prompt + "</h3>" +
            codeSnippetHtml +
            "<div class='space-y-2.5 pt-1'>" + optionsHtml + "</div>" +
            "<div id='quiz-feedback-" + qNum + "' class='hidden p-4 rounded-xl text-xs font-mono leading-relaxed space-y-2'></div>" +
          "</div>";
        }
        container.innerHTML = html;
      }

      window.checkQuizChoice = function(qNum, selectedId) {
        var q = questions[qNum - 1];
        if (!q) return;

        var selectedOpt = null;
        var correctOpt = null;
        for (var i = 0; i < q.options.length; i++) {
          if (q.options[i].id === selectedId) selectedOpt = q.options[i];
          if (q.options[i].isCorrect) correctOpt = q.options[i];
        }

        var feedbackBox = document.getElementById('quiz-feedback-' + qNum);
        if (!feedbackBox) return;

        // Reset styling on all options for this question
        q.options.forEach(function(opt) {
          var btn = document.getElementById('quiz-opt-' + qNum + '-' + opt.id);
          if (btn) {
            btn.className = "w-full text-left p-3.5 rounded-xl bg-slate-950 border border-slate-800 text-slate-300 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed opacity-70";
          }
        });

        var chosenBtn = document.getElementById('quiz-opt-' + qNum + '-' + selectedId);

        if (selectedOpt && selectedOpt.isCorrect) {
          if (chosenBtn) {
            chosenBtn.className = "w-full text-left p-3.5 rounded-xl bg-emerald-950/60 border-2 border-emerald-500 text-emerald-200 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed shadow-lg shadow-emerald-500/20";
          }
          feedbackBox.className = "p-4 rounded-xl text-xs font-mono leading-relaxed bg-emerald-950/60 border border-emerald-800 text-emerald-200 space-y-2.5";
          feedbackBox.innerHTML = "<div class='flex items-center gap-2 font-bold text-emerald-300 uppercase text-[11px]'>" +
            "<span>✅</span> Correct! Choice " + selectedId + " is optimal" +
          "</div>" +
          "<p class='text-slate-200'>" + selectedOpt.whyRight + "</p>" +
          (q.allExplanations ? "<div class='pt-2 border-t border-emerald-900/60 text-slate-300 space-y-1'>" +
            "<span class='text-[10px] uppercase font-bold text-emerald-400 block'>Why the other 3 choices fail:</span>" +
            q.allExplanations +
          "</div>" : "");
          feedbackBox.classList.remove('hidden');
        } else {
          if (chosenBtn) {
            chosenBtn.className = "w-full text-left p-3.5 rounded-xl bg-red-950/60 border-2 border-red-500 text-red-200 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed";
          }
          feedbackBox.className = "p-4 rounded-xl text-xs font-mono leading-relaxed bg-red-950/60 border border-red-800 text-red-200 space-y-2";
          feedbackBox.innerHTML = "<div class='flex items-center gap-2 font-bold text-red-300 uppercase text-[11px]'>" +
            "<span>❌</span> Choice " + selectedId + " is incorrect" +
          "</div>" +
          "<p class='text-slate-200'>" + (selectedOpt ? selectedOpt.whyWrong : 'Incorrect selection.') + "</p>" +
          "<div class='pt-2 flex items-center justify-between text-[11px]'>" +
            "<span class='text-slate-400'>Click another option to try again, or:</span>" +
            "<button onclick='window.revealFullQuizAnswer(" + qNum + ")' class='text-indigo-400 hover:text-indigo-300 underline font-semibold'>Reveal Full Solution Breakdown ↗</button>" +
          "</div>";
          feedbackBox.classList.remove('hidden');
        }
      };

      window.revealFullQuizAnswer = function(qNum) {
        var q = questions[qNum - 1];
        if (!q) return;
        var feedbackBox = document.getElementById('quiz-feedback-' + qNum);
        if (!feedbackBox) return;

        var correctOpt = null;
        q.options.forEach(function(opt) {
          if (opt.isCorrect) correctOpt = opt;
          var btn = document.getElementById('quiz-opt-' + qNum + '-' + opt.id);
          if (btn && opt.isCorrect) {
            btn.className = "w-full text-left p-3.5 rounded-xl bg-emerald-950/60 border-2 border-emerald-500 text-emerald-200 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed shadow-lg shadow-emerald-500/20";
          }
        });

        feedbackBox.className = "p-4 rounded-xl text-xs font-mono leading-relaxed bg-slate-950 border border-slate-800 text-slate-200 space-y-2.5";
        feedbackBox.innerHTML = "<div class='flex items-center gap-2 font-bold text-emerald-400 uppercase text-[11px]'>" +
          "<span>💡</span> Solution Breakdown: Choice " + (correctOpt ? correctOpt.id : '') + " is correct" +
        "</div>" +
        "<p class='text-slate-300'>" + (correctOpt ? correctOpt.whyRight : '') + "</p>" +
        (q.allExplanations ? "<div class='pt-2 border-t border-slate-800 text-slate-300 space-y-1'>" +
          "<span class='text-[10px] uppercase font-bold text-slate-400 block'>Why the other 3 choices fail:</span>" +
          q.allExplanations +
        "</div>" : "");
      };

      render();
    }
  };

  // =========================================================================
  // 5. SPACED REPETITION 48-HOUR RECALL TIMER
  // =========================================================================
  LessonRuntime.SpacedRecall = {
    init: function(config) {
      var storageKey = 'lc103_problem_' + (config.problemId || '0001') + '_recall';
      var badge = document.getElementById(config.badgeId || 'recall-status-badge');
      var countdown = document.getElementById(config.countdownId || 'recall-countdown');

      function update() {
        var saved = localStorage.getItem(storageKey);
        if (!saved) {
          if (badge) badge.innerHTML = "Status: <span class='text-amber-400'>Not Practiced Yet</span>";
          if (countdown) countdown.innerText = "48h 00m 00s";
          return;
        }

        var elapsedMs = Date.now() - parseInt(saved, 10);
        var target48h = 48 * 60 * 60 * 1000;
        var diff = Math.max(0, target48h - elapsedMs);

        var hours = Math.floor(diff / (1000 * 60 * 60));
        var mins = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        var secs = Math.floor((diff % (1000 * 60)) / 1000);

        if (countdown) {
          countdown.innerText = (hours < 10 ? '0' : '') + hours + 'h ' + (mins < 10 ? '0' : '') + mins + 'm ' + (secs < 10 ? '0' : '') + secs + 's';
        }

        if (diff === 0) {
          if (badge) {
            badge.className = "px-3 py-1 rounded-full bg-emerald-950/80 border border-emerald-600 text-xs font-mono text-emerald-300 font-bold animate-pulse";
            badge.innerHTML = "Status: 🟢 Cold Recall Ready! (48h reached)";
          }
        } else {
          if (badge) {
            badge.className = "px-3 py-1 rounded-full bg-cyan-950/60 border border-cyan-800 text-xs font-mono text-cyan-300";
            badge.innerHTML = "Status: ⏳ Next Recall in ~" + Math.ceil(diff / (60 * 60 * 1000)) + " hrs";
          }
        }
      }

      window.markPracticedNow = function() {
        localStorage.setItem(storageKey, Date.now().toString());
        update();
      };

      window.resetRecallTimer = function() {
        localStorage.removeItem(storageKey);
        update();
      };

      update();
      setInterval(update, 1000);
    }
  };

  // =========================================================================
  // INTERNAL: TRANSPILERS & SHIMS FOR JVM-SUBSET & PYTHON
  // =========================================================================
  LessonRuntime._createRuntimeEnv = function() {
    function __sub(a, b) {
      var na = typeof a === 'string' && a.length === 1 ? a.charCodeAt(0) : Number(a);
      var nb = typeof b === 'string' && b.length === 1 ? b.charCodeAt(0) : Number(b);
      return na - nb;
    }

    function __sort(arr) {
      if (!Array.isArray(arr)) return arr;
      if (arr.length > 0 && typeof arr[0] === 'number') {
        return arr.sort(function(a, b) { return a - b; });
      }
      return arr.sort();
    }

    function __HashSet() {
      this._set = new Set();
    }
    __HashSet.prototype.add = function(val) {
      if (this._set.has(val)) return false;
      this._set.add(val);
      return true;
    };
    __HashSet.prototype.contains = function(val) {
      return this._set.has(val);
    };
    __HashSet.prototype.has = function(val) {
      return this._set.has(val);
    };
    __HashSet.prototype.remove = function(val) {
      return this._set.delete(val);
    };
    Object.defineProperty(__HashSet.prototype, 'size', {
      get: function() { return this._set.size; }
    });

    function __ArrayList(init) {
      var arr = Array.isArray(init) ? init.slice() : [];
      arr.add = function(x) { this.push(x); return true; };
      return arr;
    }

    function __HashMap() {
      var map = new Map();
      var proxy = new Proxy(this, {
        get: function(target, prop) {
          if (prop === '_map') return map;
          if (prop in target) return target[prop];
          if (map.has(prop)) return map.get(prop);
          return undefined;
        },
        set: function(target, prop, value) {
          if (prop in target) {
            target[prop] = value;
          } else {
            map.set(prop, value);
          }
          return true;
        }
      });
      this._map = map;
      return proxy;
    }
    __HashMap.prototype.put = function(k, v) { this._map.set(k, v); return v; };
    __HashMap.prototype.get = function(k) { return this._map.get(k); };
    __HashMap.prototype.getOrDefault = function(k, d) { return this._map.has(k) ? this._map.get(k) : d; };
    __HashMap.prototype.containsKey = function(k) { return this._map.has(k); };
    __HashMap.prototype.contains = function(k) { return this._map.has(k); };
    __HashMap.prototype.getOrPut = function(k, defaultValueFn) {
      if (!this._map.has(k)) {
        var val = typeof defaultValueFn === 'function' ? defaultValueFn() : defaultValueFn;
        this._map.set(k, val);
      }
      return this._map.get(k);
    };
    __HashMap.prototype.computeIfAbsent = function(k, mappingFn) {
      if (!this._map.has(k)) {
        var val = typeof mappingFn === 'function' ? mappingFn(k) : mappingFn;
        this._map.set(k, val);
      }
      return this._map.get(k);
    };
    __HashMap.prototype.values = function() {
      return Array.from(this._map.values());
    };

    function __StringBuilder() {
      this._parts = [];
    }
    __StringBuilder.prototype.append = function(val) {
      this._parts.push(val);
      return this;
    };
    __StringBuilder.prototype.toString = function() {
      return this._parts.join('');
    };

    function buildString(fn) {
      var sb = new __StringBuilder();
      fn.call(sb);
      return sb.toString();
    }

    function IntArray(size, initVal) {
      var arr = new Array(size);
      for (var i = 0; i < size; i++) arr[i] = (initVal || 0);
      arr.sort = function() { return __sort(this); };
      return arr;
    }

    function BooleanArray(size, initVal) {
      var arr = new Array(size);
      for (var i = 0; i < size; i++) arr[i] = (initVal || false);
      return arr;
    }

    function ord(c) {
      return typeof c === 'string' && c.length > 0 ? c.charCodeAt(0) : Number(c);
    }

    function tuple(arr) {
      return Array.isArray(arr) ? arr.join(',') : String(arr);
    }

    return {
      __sub: __sub,
      __sort: __sort,
      __HashSet: __HashSet,
      __HashMap: __HashMap,
      __ArrayList: __ArrayList,
      __StringBuilder: __StringBuilder,
      buildString: buildString,
      IntArray: IntArray,
      BooleanArray: BooleanArray,
      ord: ord,
      tuple: tuple
    };
  };

  LessonRuntime._extractMethod = function(code, methodName) {
    var clean = code.replace(/\/\*[\s\S]*?\*\//g, '').replace(/\/\/.*$/gm, '');
    var regex = new RegExp('(?:fun|def|public|private|static|\\w+(?:<[^>]*>)?|\\[\\])*\\s+' + methodName + '\\s*\\(([^)]*)\\)', 'm');
    var match = clean.match(regex);
    if (!match) {
      regex = new RegExp(methodName + '\\s*\\(([^)]*)\\)', 'm');
      match = clean.match(regex);
    }
    if (!match) return null;

    var paramStr = match[1];
    var afterHeaderIdx = match.index + match[0].length;
    var openBraceIdx = clean.indexOf('{', afterHeaderIdx);
    if (openBraceIdx === -1) return null;

    var depth = 1;
    var endIdx = openBraceIdx + 1;
    while (depth > 0 && endIdx < clean.length) {
      if (clean[endIdx] === '{') depth++;
      else if (clean[endIdx] === '}') depth--;
      endIdx++;
    }

    var body = clean.slice(openBraceIdx + 1, endIdx - 1);
    var params = [];
    var parts = paramStr.split(',');
    for (var i = 0; i < parts.length; i++) {
      var p = parts[i].trim();
      if (!p) continue;
      if (p.indexOf(':') !== -1) {
        params.push(p.split(':')[0].trim());
      } else {
        var words = p.split(/\s+/);
        params.push(words[words.length - 1].trim());
      }
    }

    return { params: params, body: body };
  };

  LessonRuntime._transpileJVM = function(code, methodName) {
    var parsed = LessonRuntime._extractMethod(code, methodName);
    var body = parsed ? parsed.body : code;
    var params = parsed ? parsed.params : ['nums'];

    var js = body;
    js = js.replace(/^\s*(?:package|import)\s+.*$/gm, '');

    js = js.replace(/(\w+)\.computeIfAbsent\(([^,]+),\s*(\w+)\s*->\s*new\s+ArrayList\s*(?:<[^>]*>)?\s*\(\)\)/g, '$1.computeIfAbsent($2, function($3) { return __ArrayList(); })');
    js = js.replace(/(\w+)\.computeIfAbsent\(([^,]+),\s*(\w+)\s*->\s*([^)]+)\)/g, '$1.computeIfAbsent($2, function($3) { return $4; })');
    js = js.replace(/(\w+)\.getOrPut\(([^)]+)\)\s*\{\s*([^}]+)\s*\}/g, '$1.getOrPut($2, function() { return $3; })');

    js = js.replace(/hashSetOf\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashSet()');
    js = js.replace(/mutableSetOf\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashSet()');
    js = js.replace(/new\s+HashSet\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashSet()');
    js = js.replace(/\bHashSet\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashSet()');

    js = js.replace(/mutableMapOf\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashMap()');
    js = js.replace(/new\s+HashMap\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashMap()');
    js = js.replace(/\bHashMap\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashMap()');

    js = js.replace(/\b(?:[A-Z]\w*(?:<[\s\S]*?>)?|boolean|int|char|double|float|long|short|byte)(?:\[\s*\])*\s+(\w+)\s*=/g, 'var $1 =');

    js = js.replace(/new\s+ArrayList\s*(?:<[\s\S]*?>)?\s*\(([^)]*)\)/g, '__ArrayList($1)');
    js = js.replace(/\bArrayList\s*(?:<[\s\S]*?>)?\s*\(([^)]*)\)/g, '__ArrayList($1)');
    js = js.replace(/\bmutableListOf\s*\(([^)]*)\)/g, '__ArrayList([$1])');
    js = js.replace(/\bmutableListOf\s*(?:<[\s\S]*?>)?\s*\(\)/g, '__ArrayList()');
    js = js.replace(/\bArrayList\s*(?:<[\s\S]*?>)?\s*\(\)/g, '__ArrayList()');

    js = js.replace(/new\s+int\[([^\]]+)\]/g, 'IntArray($1)');
    js = js.replace(/\bIntArray\(([^)]+)\)/g, 'IntArray($1)');
    js = js.replace(/new\s+boolean\[([^\]]+)\]/g, 'BooleanArray($1)');
    js = js.replace(/\bBooleanArray\(([^)]+)\)/g, 'BooleanArray($1)');

    js = js.replace(/new\s+StringBuilder\(\)/g, 'new __StringBuilder()');

    js = js.replace(/String\(([^)]+)\.toCharArray\(\)\.apply\s*\{\s*sort\(\)\s*\}\)/g, '$1.split("").sort().join("")');
    js = js.replace(/(\w+)\.toCharArray\(\)\.sorted\(\)\.joinToString\([^)]*\)/g, '$1.split("").sort().join("")');
    js = js.replace(/new\s+String\(([^)]+)\)/g, '($1.join ? $1.join("") : String($1))');
    js = js.replace(/(\w+)\.toCharArray\(\)/g, '$1.split("")');
    js = js.replace(/Arrays\.sort\(([^)]+)\)/g, '__sort($1)');
    js = js.replace(/(\w+)\.sort\(\)/g, '__sort($1)');

    js = js.replace(/(\w+(?:\.charAt\([^)]+\))?)\s*-\s*['"]([a-zA-Z0-9])['"]/g, '__sub($1, "$2")');

    js = js.replace(/for\s*\(\s*(?:val|var)?\s*(\w+)\s+in\s+([^)]+)\)/g, function(m, p1, p2) {
      if (p2.indexOf('until') !== -1) {
        var parts = p2.split('until');
        return 'for (var ' + p1 + ' = ' + parts[0].trim() + '; ' + p1 + ' < ' + parts[1].trim() + '; ' + p1 + '++)';
      }
      if (p2.indexOf('indices') !== -1) {
        var arrName = p2.replace('.indices', '').trim();
        return 'for (var ' + p1 + ' = 0; ' + p1 + ' < ' + arrName + '.length; ' + p1 + '++)';
      }
      return 'for (var ' + p1 + ' of ' + p2 + ')';
    });

    js = js.replace(/for\s*\(\s*(?:int|String|char|boolean|\w+)\s+(\w+)\s*:\s*([^)]+)\)/g, 'for (var $1 of $2)');
    js = js.replace(/for\s*\(\s*(?:int|var)\s+(\w+)\s*=/g, 'for (var $1 =');

    var bsIdx = js.indexOf('buildString');
    while (bsIdx !== -1) {
      var openB = js.indexOf('{', bsIdx);
      if (openB === -1) break;
      var d = 1;
      var closeB = openB + 1;
      while (d > 0 && closeB < js.length) {
        if (js[closeB] === '{') d++;
        else if (js[closeB] === '}') d--;
        closeB++;
      }
      var inner = js.slice(openB + 1, closeB - 1);
      var transformedInner = inner.replace(/\bappend\(([^)]+)\)/g, 'sb.append($1)');
      var replacement = '(function() { var sb = new __StringBuilder(); ' + transformedInner + '; return sb.toString(); })()';
      js = js.slice(0, bsIdx) + replacement + js.slice(closeB);
      bsIdx = js.indexOf('buildString');
    }

    // Nested Kotlin local functions
    js = js.replace(/\bfun\s+(\w+)\s*\(([^)]*)\)(?:\s*:\s*[^{]+)?\s*\{/g, function(m, fnName, rawParams) {
      var cleanParams = rawParams.split(',').map(function(p) {
        return p.trim().split(':')[0].trim();
      }).filter(Boolean).join(', ');
      return 'function ' + fnName + '(' + cleanParams + ') {';
    });

    js = js.replace(/\bmaxOf\(([^)]+)\)/g, 'Math.max($1)');
    js = js.replace(/\bminOf\(([^)]+)\)/g, 'Math.min($1)');
    js = js.replace(/\bval\s+/g, 'var ');
    js = js.replace(/\.values\b(?!\()/g, '.values()');
    js = js.replace(/\.size\b(?!\()/g, '.length');
    js = js.replace(/\.size\(\)/g, '.length');
    js = js.replace(/\.length\(\)/g, '.length');
    js = js.replace(/(\w+)\.equals\(([^)]+)\)/g, '$1 === $2');

    var guardedJs = 'var __ops = 0;\n';
    guardedJs += js.replace(/(for\s*\([^)]*\)\s*\{|while\s*\([^)]*\)\s*\{)/g, '$1 if (++__ops > 5000000) throw new Error("Time Limit Exceeded (>5M operations / infinite loop)"); ');

    var paramList = params.length > 0 ? params.join(', ') : 'arg1';
    var fullFnCode = '(function(' + paramList + ') {\n' + guardedJs + '\n})';
    return { params: params, code: fullFnCode };
  };

  LessonRuntime._transpilePython = function(code, methodName) {
    var clean = code.replace(/\/\*[\s\S]*?\*\//g, '').replace(/#.*$/gm, '');
    clean = clean.replace(/^\s*(?:from|import)\s+.*$/gm, '');

    var lines = clean.split('\n');
    var params = [];
    var rawBody = [];
    var insideMethod = false;
    var baseIndent = -1;

    for (var i = 0; i < lines.length; i++) {
      var line = lines[i];
      if (!line.trim()) continue;

      var defMatch = line.match(/^(\s*)def\s+(\w+)\s*\(([^)]*)\)/);
      if (defMatch) {
        if (defMatch[2] === methodName) {
          insideMethod = true;
          baseIndent = -1;
          var rawParams = defMatch[3].split(',');
          for (var p = 0; p < rawParams.length; p++) {
            var param = rawParams[p].trim().split(':')[0].trim();
            if (param && param !== 'self') {
              params.push(param);
            }
          }
          continue;
        } else if (insideMethod) {
          break;
        }
      }

      if (insideMethod) {
        var currentIndent = line.search(/\S/);
        if (baseIndent === -1) baseIndent = currentIndent;
        if (currentIndent < baseIndent) break;
        rawBody.push({
          indent: currentIndent - baseIndent,
          text: line.trim()
        });
      }
    }

    var jsLines = ['var __ops = 0;'];
    var indentStack = [0];

    for (var j = 0; j < rawBody.length; j++) {
      var item = rawBody[j];
      var ind = item.indent;
      var stmt = item.text;

      while (indentStack.length > 1 && ind < indentStack[indentStack.length - 1]) {
        indentStack.pop();
        jsLines.push('}');
      }

      stmt = stmt.replace(/\bTrue\b/g, 'true');
      stmt = stmt.replace(/\bFalse\b/g, 'false');
      stmt = stmt.replace(/\bNone\b/g, 'null');

      stmt = stmt.replace(/seen\s*=\s*set\(\)/g, 'var seen = new __HashSet()');
      stmt = stmt.replace(/groups\s*=\s*defaultdict\(list\)/g, 'var groups = new __HashMap()');

      // Python list replication e.g. [0] * 26 or [False] * len(strs)
      stmt = stmt.replace(/\[([^\]]+)\]\s*\*\s*(\d+|\w+(?:\.length)?)/g, 'new Array($2).fill($1)');

      if (stmt.match(/^if\s+(\w+)\s+in\s+(\w+)\s*:/)) {
        stmt = stmt.replace(/^if\s+(\w+)\s+in\s+(\w+)\s*:/, 'if ($2.contains ? $2.contains($1) : ($1 in $2)) {');
      } else if (stmt.match(/^if\s+not\s+(\w+)\s*:/)) {
        stmt = stmt.replace(/^if\s+not\s+(\w+)\s*:/, 'if (!$1) {');
      } else if (stmt.match(/^if\s+([^:]+):/)) {
        stmt = stmt.replace(/^if\s+([^:]+):/, 'if ($1) {');
      } else if (stmt.match(/^elif\s+([^:]+):/)) {
        stmt = stmt.replace(/^elif\s+([^:]+):/, '} else if ($1) {');
      } else if (stmt.match(/^else\s*:/)) {
        stmt = stmt.replace(/^else\s*:/, '} else {');
      } else if (stmt.match(/^for\s+(\w+)\s*,\s*(\w+)\s+in\s+enumerate\(([^)]+)\)\s*:/)) {
        stmt = stmt.replace(/^for\s+(\w+)\s*,\s*(\w+)\s+in\s+enumerate\(([^)]+)\)\s*:/, 'for (var $1 = 0; $1 < $3.length; $1++) { var $2 = $3[$1]; if (++__ops > 5000000) throw new Error("TLE");');
      } else if (stmt.match(/^for\s+(\w+)\s+in\s+range\((\w+)\)\s*:/)) {
        stmt = stmt.replace(/^for\s+(\w+)\s+in\s+range\((\w+)\)\s*:/, 'for (var $1 = 0; $1 < $2; $1++) { if (++__ops > 5000000) throw new Error("TLE");');
      } else if (stmt.match(/^for\s+(\w+)\s+in\s+range\(([^,]+),\s*([^)]+)\)\s*:/)) {
        stmt = stmt.replace(/^for\s+(\w+)\s+in\s+range\(([^,]+),\s*([^)]+)\)\s*:/, 'for (var $1 = $2; $1 < $3; $1++) { if (++__ops > 5000000) throw new Error("TLE");');
      } else if (stmt.match(/^for\s+(\w+)\s+in\s+([^:]+):/)) {
        stmt = stmt.replace(/^for\s+(\w+)\s+in\s+([^:]+):/, 'for (var $1 of $2) { if (++__ops > 5000000) throw new Error("TLE");');
      } else if (stmt.match(/^while\s+([^:]+):/)) {
        stmt = stmt.replace(/^while\s+([^:]+):/, 'while ($1) { if (++__ops > 5000000) throw new Error("TLE");');
      }

      stmt = stmt.replace(/len\((\w+)\)/g, '$1.length');
      stmt = stmt.replace(/["']["']\.join\(sorted\((\w+)\)\)/g, '$1.split("").sort().join("")');
      stmt = stmt.replace(/sorted\((\w+)\)/g, '$1.slice().sort()');

      stmt = stmt.replace(/groups\[([^\]]+)\]\.append\(([^)]+)\)/g, function(m, key, val) {
        return 'groups.getOrPut(' + key + ', function() { return []; }).push(' + val + ')';
      });
      stmt = stmt.replace(/(\w+)\.append\(([^)]+)\)/g, '$1.push($2)');

      stmt = stmt.replace(/list\((\w+)\.values\(\)\)/g, '$1.values()');

      if (stmt.match(/^([a-zA-Z_]\w*)\s*=/)) {
        if (!stmt.startsWith('var ') && !stmt.startsWith('let ') && !stmt.startsWith('this.')) {
          stmt = 'var ' + stmt;
        }
      }

      var opensBlock = item.text.endsWith(':');
      if (opensBlock) {
        indentStack.push(ind + 4);
      } else {
        stmt += ';';
      }

      jsLines.push(stmt);
    }

    while (indentStack.length > 1) {
      indentStack.pop();
      jsLines.push('}');
    }

    var paramList = params.length > 0 ? params.join(', ') : 'arg1';
    var fullFnCode = '(function(' + paramList + ') {\n' + jsLines.join('\n') + '\n})';
    return { params: params, code: fullFnCode };
  };

  global.LessonRuntime = LessonRuntime;

})(typeof window !== 'undefined' ? window : this);
