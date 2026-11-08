"""
Upgrades Lessons 0002 to 0022 to Golden Master v2:
- Fixes broken gstatic Tailwind CDN links
- Links assets/lesson-theme.css and assets/lesson-runtime.js
- Injects Golden Master Practice Arena in Section 5
- Injects Golden Master 4-MCQ Diagnostic Quiz engine and 48-Hour Recall in Section 7
- Fixes stepper initialization
- Cleans raw unrendered LaTeX
"""

import glob, re, os, json, html, sys
sys.path.append('scripts')
from lesson_builder import build_quiz, extract_method_name

pass1_meta = {
    2: {
        "method": "twoSum",
        "tests": [
            { "input": { "nums": [2, 7, 11, 15], "target": 9 }, "expected": [0, 1] },
            { "input": { "nums": [3, 2, 4], "target": 6 }, "expected": [1, 2] },
            { "input": { "nums": [3, 3], "target": 6 }, "expected": [0, 1] }
        ]
    },
    3: {
        "method": "mergeAlternately",
        "tests": [
            { "input": { "word1": "abc", "word2": "pqr" }, "expected": "apbqcr" },
            { "input": { "word1": "ab", "word2": "pqrs" }, "expected": "apbqrs" },
            { "input": { "word1": "abcd", "word2": "pq" }, "expected": "apbqcd" }
        ]
    },
    4: {
        "method": "moveZeroes",
        "tests": [
            { "input": { "nums": [0, 1, 0, 3, 12] }, "expected": [1, 3, 12, 0, 0] },
            { "input": { "nums": [0] }, "expected": [0] }
        ]
    },
    5: {
        "method": "isPalindrome",
        "tests": [
            { "input": { "s": "A man, a plan, a canal: Panama" }, "expected": True },
            { "input": { "s": "race a car" }, "expected": False },
            { "input": { "s": " " }, "expected": True }
        ]
    },
    6: {
        "method": "maxProfit",
        "tests": [
            { "input": { "prices": [7, 1, 5, 3, 6, 4] }, "expected": 5 },
            { "input": { "prices": [7, 6, 4, 3, 1] }, "expected": 0 }
        ]
    },
    7: {
        "method": "findMaxAverage",
        "tests": [
            { "input": { "nums": [1, 12, -5, -6, 50, 3], "k": 4 }, "expected": 12.75 },
            { "input": { "nums": [5], "k": 1 }, "expected": 5.0 }
        ]
    },
    8: {
        "method": "pivotIndex",
        "tests": [
            { "input": { "nums": [1, 7, 3, 6, 5, 6] }, "expected": 3 },
            { "input": { "nums": [1, 2, 3] }, "expected": -1 },
            { "input": { "nums": [2, 1, -1] }, "expected": 0 }
        ]
    },
    9: {
        "method": "isValid",
        "tests": [
            { "input": { "s": "()" }, "expected": True },
            { "input": { "s": "()[]{}" }, "expected": True },
            { "input": { "s": "(]" }, "expected": False }
        ]
    },
    10: {
        "method": "ping",
        "tests": [
            { "input": { "t": 1 }, "expected": 1 },
            { "input": { "t": 100 }, "expected": 2 },
            { "input": { "t": 3001 }, "expected": 3 },
            { "input": { "t": 3002 }, "expected": 3 }
        ]
    },
    11: {
        "method": "reverseList",
        "tests": [
            { "input": { "head": [1, 2, 3, 4, 5] }, "expected": [5, 4, 3, 2, 1] },
            { "input": { "head": [1, 2] }, "expected": [2, 1] },
            { "input": { "head": [] }, "expected": [] }
        ]
    },
    12: {
        "method": "mergeTwoLists",
        "tests": [
            { "input": { "list1": [1, 2, 4], "list2": [1, 3, 4] }, "expected": [1, 1, 2, 3, 4, 4] },
            { "input": { "list1": [], "list2": [] }, "expected": [] },
            { "input": { "list1": [], "list2": [0] }, "expected": [0] }
        ]
    },
    13: {
        "method": "hasCycle",
        "tests": [
            { "input": { "head": [3, 2, 0, -4], "pos": 1 }, "expected": True },
            { "input": { "head": [1, 2], "pos": 0 }, "expected": True },
            { "input": { "head": [1], "pos": -1 }, "expected": False }
        ]
    },
    14: {
        "method": "getIntersectionNode",
        "tests": [
            { "input": { "intersectVal": 8, "listA": [4,1,8,4,5], "listB": [5,6,1,8,4,5], "skipA": 2, "skipB": 3 }, "expected": 8 }
        ]
    },
    15: {
        "method": "search",
        "tests": [
            { "input": { "nums": [-1, 0, 3, 5, 9, 12], "target": 9 }, "expected": 4 },
            { "input": { "nums": [-1, 0, 3, 5, 9, 12], "target": 2 }, "expected": -1 }
        ]
    },
    16: {
        "method": "invertTree",
        "tests": [
            { "input": { "root": [4, 2, 7, 1, 3, 6, 9] }, "expected": [4, 7, 2, 9, 6, 3, 1] },
            { "input": { "root": [2, 1, 3] }, "expected": [2, 3, 1] }
        ]
    },
    17: {
        "method": "maxDepth",
        "tests": [
            { "input": { "root": [3, 9, 20, None, None, 15, 7] }, "expected": 3 },
            { "input": { "root": [1, None, 2] }, "expected": 2 }
        ]
    },
    18: {
        "method": "diameterOfBinaryTree",
        "tests": [
            { "input": { "root": [1, 2, 3, 4, 5] }, "expected": 3 },
            { "input": { "root": [1, 2] }, "expected": 1 }
        ]
    },
    19: {
        "method": "climbStairs",
        "tests": [
            { "input": { "n": 2 }, "expected": 2 },
            { "input": { "n": 3 }, "expected": 3 },
            { "input": { "n": 5 }, "expected": 8 }
        ]
    },
    20: {
        "method": "singleNumber",
        "tests": [
            { "input": { "nums": [2, 2, 1] }, "expected": 1 },
            { "input": { "nums": [4, 1, 2, 1, 2] }, "expected": 4 },
            { "input": { "nums": [1] }, "expected": 1 }
        ]
    },
    21: {
        "method": "hammingWeight",
        "tests": [
            { "input": { "n": 11 }, "expected": 3 },
            { "input": { "n": 128 }, "expected": 1 },
            { "input": { "n": 2147483645 }, "expected": 30 }
        ]
    },
    22: {
        "method": "countBits",
        "tests": [
            { "input": { "n": 2 }, "expected": [0, 1, 1] },
            { "input": { "n": 5 }, "expected": [0, 1, 1, 2, 1, 2] }
        ]
    }
}

PRACTICE_ARENA_HTML = """
      <!-- GOLDEN MASTER RUNNABLE PRACTICE ARENA -->
      <div class="rounded-2xl border border-indigo-500/30 bg-slate-900/80 p-6 sm:p-8 space-y-5 shadow-2xl">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-4">
          <div>
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
              <h4 class="text-sm font-bold text-white font-mono uppercase tracking-wider">Interactive Runnable Practice Arena</h4>
            </div>
            <p class="text-xs text-slate-400 mt-1">Code solution cold in your target language. Real in-browser sandboxed execution against test cases.</p>
          </div>

          <div class="flex items-center gap-1.5 bg-slate-950 p-1 rounded-xl border border-slate-800 text-xs font-mono">
            <button onclick="LessonRuntime.PracticeArena.setLang('kotlin')" id="practice-lang-kotlin" class="px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold transition-all">Kotlin</button>
            <button onclick="LessonRuntime.PracticeArena.setLang('python')" id="practice-lang-python" class="px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all">Python</button>
            <button onclick="LessonRuntime.PracticeArena.setLang('java')" id="practice-lang-java" class="px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all">Java</button>
          </div>
        </div>

        <div class="relative">
          <div class="flex items-center justify-between px-4 py-2 bg-slate-950 border border-b-0 border-slate-800 rounded-t-xl text-xs font-mono text-slate-400">
            <span id="practice-filename" class="text-emerald-400 font-bold">Solution.kt</span>
            <span class="text-[11px] text-slate-500">Supports Tab key • 5,000,000 operations TLE limit</span>
          </div>
          <textarea id="practice-editor" rows="12" class="w-full bg-slate-950 border border-slate-800 rounded-b-xl p-4 text-xs font-mono text-emerald-300 focus:outline-none focus:border-emerald-500 leading-relaxed custom-scrollbar spellcheck-false" placeholder="Write your solution here..."></textarea>
        </div>

        <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800/80 space-y-2">
          <div class="flex items-center justify-between text-xs font-mono">
            <label for="practice-custom-input" class="text-slate-400 font-bold">Custom Test Input (JSON args array):</label>
            <span class="text-slate-500 text-[10px]">e.g. [[1, 2, 3, 1]]</span>
          </div>
          <div class="flex gap-2">
            <input id="practice-custom-input" type="text" class="flex-1 bg-slate-900 border border-slate-700 rounded-lg px-3 py-1.5 text-xs font-mono text-slate-200 focus:outline-none focus:border-indigo-500" placeholder="e.g. [[1, 2, 3, 1]]">
            <button onclick="LessonRuntime.PracticeArena.runCustomInput()" class="px-3.5 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs font-mono transition-colors">
              Run Custom Input
            </button>
          </div>
        </div>

        <div class="flex flex-wrap items-center justify-between gap-3 pt-1">
          <div class="flex items-center gap-2">
            <button onclick="LessonRuntime.PracticeArena.runTests()" class="px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs font-mono transition-all shadow-lg shadow-emerald-500/20 flex items-center gap-2">
              <span>▶</span> Run Code &amp; Test Cases
            </button>
            <button onclick="LessonRuntime.PracticeArena.resetStub()" class="px-3.5 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 font-mono text-xs transition-colors">
              Reset Stub
            </button>
          </div>

          <div class="flex items-center gap-2">
            <button onclick="LessonRuntime.PracticeArena.revealHint()" id="practice-hint-btn" class="px-3.5 py-2 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-300 hover:bg-amber-500/20 text-xs font-mono font-bold transition-colors">
              💡 Need a Hint? (Step 1/3)
            </button>
            <button onclick="LessonRuntime.PracticeArena.revealSolution()" class="px-3.5 py-2 rounded-xl bg-slate-800 border border-slate-700 text-slate-300 hover:text-white text-xs font-mono transition-colors">
              Gated Model Answer
            </button>
          </div>
        </div>

        <div id="practice-hints-container" class="space-y-2 hidden"></div>

        <div id="practice-model-box" class="hidden p-4 rounded-xl bg-slate-950 border border-emerald-500/30 space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider">Gated Model Reference Solution</span>
            <span class="text-[11px] font-mono text-slate-500">Study after writing your solution</span>
          </div>
          <pre id="practice-model-code" class="p-3.5 rounded-lg bg-slate-900 text-xs font-mono text-emerald-300 overflow-x-auto"></pre>
        </div>

        <div id="practice-console" class="hidden p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center justify-between border-b border-slate-800/80 pb-2">
            <div class="flex items-center gap-2">
              <span id="practice-status-dot" class="w-2 h-2 rounded-full bg-slate-500"></span>
              <span id="practice-status-text" class="text-xs font-mono font-bold text-slate-300">Test Execution Idle</span>
            </div>
          </div>
          <div id="practice-output" class="space-y-2 text-xs font-mono"></div>
        </div>
      </div>
"""

SECTION_7_HTML = """
    <!-- Section 7: Diagnostic Code Quizzes & Spaced Recall Protocol -->
    <section id="section-7" class="space-y-6 pt-4">
      <div class="flex items-center gap-3">
        <span class="w-8 h-8 rounded-lg bg-emerald-500 text-slate-950 flex items-center justify-center font-bold font-mono text-sm">07</span>
        <div>
          <span class="text-xs font-mono font-semibold text-emerald-400 uppercase tracking-wider">Storage Strength &amp; Retention</span>
          <h2 class="text-2xl font-extrabold text-white">Diagnostic Code Quizzes &amp; Spaced Recall Protocol</h2>
        </div>
      </div>

      <!-- 4 Balanced Diagnostic Code Quizzes Container -->
      <div id="quiz-container" class="space-y-6"></div>

      <!-- 48-Hour Cold Practice Protocol & Countdown Card -->
      <div class="rounded-2xl border border-emerald-500/30 bg-emerald-950/20 p-6 sm:p-8 space-y-4">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-emerald-500/20 pb-4">
          <div>
            <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider">Storage Strength Protocol</span>
            <h3 class="text-lg font-bold text-white mt-0.5">The 48-Hour Cold Practice Regimen</h3>
          </div>
          <div id="recall-status-badge" class="px-3 py-1 rounded-full bg-slate-900 border border-slate-800 text-xs font-mono text-slate-400">
            Status: Not Practiced Yet
          </div>
        </div>

        <ol class="text-xs sm:text-sm text-slate-300 space-y-2 list-decimal list-inside leading-relaxed">
          <li><strong>Step 1 (Immediate):</strong> Close this browser tab and write the solution on paper from memory.</li>
          <li><strong>Step 2 (24 Hours):</strong> Re-read the mental model analogy and articulate the core invariant out loud.</li>
          <li><strong>Step 3 (48 Hours):</strong> Open LeetCode cold. Code the solution in under 5 minutes without any hints.</li>
        </ol>

        <div class="pt-2 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
          <div class="flex items-center gap-3">
            <button onclick="markPracticedNow()" class="px-4 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs font-mono transition-colors shadow-lg shadow-emerald-500/20">
              ✓ Mark Practiced Now (Start 48h Timer)
            </button>
            <button onclick="resetRecallTimer()" class="px-3 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-400 text-xs font-mono transition-colors">
              Reset Timer
            </button>
          </div>
          <div class="text-right">
            <span class="text-[10px] font-mono text-slate-500 block uppercase">Next Check Due In:</span>
            <span id="recall-countdown" class="text-xs font-mono font-bold text-emerald-400">48h 00m 00s</span>
          </div>
        </div>
      </div>

      <!-- Primary Trusted Reference & Teacher Agent Card -->
      <div class="grid md:grid-cols-2 gap-6 pt-2">
        <div class="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-2 text-xs font-mono">
          <span class="text-indigo-400 font-bold uppercase tracking-wider block text-[11px]">📖 Primary Trusted Reference:</span>
          <p class="text-slate-300 leading-relaxed">
            <a href="https://kotlinlang.org/docs/home.html" target="_blank" rel="noopener noreferrer" class="text-emerald-400 underline font-semibold">Kotlin Official Reference Documentation</a> and CLRS 4th Edition. Study optimal runtime invariants and algorithmic bounds.
          </p>
        </div>

        <div class="p-5 rounded-2xl bg-indigo-950/30 border border-indigo-500/30 space-y-2 text-xs font-mono">
          <span class="text-indigo-300 font-bold uppercase tracking-wider block text-[11px]">💬 Ask Your Teacher Agent:</span>
          <p class="text-slate-300 leading-relaxed">
            Unclear about this problem's memory bounds or edge case guarantees? <strong>Ask follow-up questions in chat anytime</strong>—Antigravity is your personal pair-programming teacher.
          </p>
        </div>
      </div>
    </section>
"""

def get_solutions_from_html(content):
    pres = re.findall(r'<pre[^>]*>([\s\S]*?)</pre>', content)
    k_sols, p_sols, j_sols = [], [], []
    for p in pres:
        clean = html.unescape(re.sub(r'<[^>]+>', '', p)).strip()
        if 'class Solution' in clean or 'class Logger' in clean or 'class RecentCounter' in clean:
            if 'fun ' in clean or 'val ' in clean:
                k_sols.append(clean)
            elif 'def ' in clean or 'self' in clean or 'import re' in clean:
                p_sols.append(clean)
            elif 'public ' in clean or 'private ' in clean:
                j_sols.append(clean)
    return {
        "kotlin": k_sols[-1] if k_sols else "// Solution\n",
        "python": p_sols[-1] if p_sols else "# Solution\n",
        "java": j_sols[-1] if j_sols else "// Solution\n"
    }

def upgrade_file(fpath):
    with open(fpath, 'r', encoding='utf-8') as f:
        c = f.read()

    m_num = re.search(r'00(\d\d)', fpath)
    num = int(m_num.group(1))
    num_str = f"{num:04d}"
    meta = pass1_meta.get(num, {})
    method = meta.get("method", "solve")
    tests = meta.get("tests", [])

    title_m = re.search(r'<title>.*?:\s*([^|(]+)', c)
    title = title_m.group(1).strip() if title_m else f"Problem {num}"
    
    # 1. Head links
    c = c.replace('https://www.gstatic.com/antigravity/web/dev/tailwindcss.min.js', 'https://cdn.tailwindcss.com')
    if 'assets/lesson-theme.css' not in c:
        c = c.replace('</head>', '  <link rel="stylesheet" href="../assets/lesson-theme.css">\n  <script src="../assets/lesson-runtime.js"></script>\n</head>')

    # 2. Clean raw LaTeX
    # replace $O(...) $ in body
    c = re.sub(r'\$(O\([^$]+\))\$', r'<code>\1</code>', c)

    # 3. Extract solutions & stubs
    sols = get_solutions_from_html(c)
    stubs = {
        "kotlin": f"class Solution {{\n    fun {method}() {{\n        // Write your solution here:\n        \n    }}\n}}",
        "python": f"class Solution:\n    def {method}(self):\n        # Write your solution here:\n        pass",
        "java": f"class Solution {{\n    public void {method}() {{\n        // Write your solution here:\n        \n    }}\n}}"
    }

    # 4. Hints & Quiz
    prob_dict = {
        "num": num,
        "title": title,
        "category": "Pass 1 Foundation",
        "difficulty": "Easy"
    }
    quiz_questions = build_quiz(prob_dict)
    hints = [
        f"Recall the core invariant for {title}. Look for opportunities to avoid quadratic nested scans.",
        f"Can you use a frequency table, two-pointer window, or monotonic invariant to make decisions in O(1) time?",
        f"Iterate through the elements checking target conditions before committing mutations to your state."
    ]

    # 5. Section 5: Replace legacy scratchpad
    # Find scratchpad marker
    sp_patterns = [
        r'<!--\s*(?:Interactive Scratchpad|In-Browser Code Scratchpad)[\s\S]*?</section>',
        r'<div class="border-t border-slate-800 pt-6 space-y-4">[\s\S]*?</section>',
        r'<div class="relative rounded-xl border border-slate-800 bg-slate-950[\s\S]*?</section>'
    ]
    replaced_sec5 = False
    for pat in sp_patterns:
        if re.search(pat, c):
            c = re.sub(pat, PRACTICE_ARENA_HTML + "\n    </section>", c, count=1)
            replaced_sec5 = True
            break
    if not replaced_sec5:
        # Fallback: replace before </section> in section-5
        sec5_end = c.find('</section>', c.find('id="section-5"'))
        if sec5_end != -1:
            c = c[:sec5_end] + PRACTICE_ARENA_HTML + "\n    " + c[sec5_end:]

    # 6. Section 7: Replace Section 7 entirely
    sec7_pat = r'<section id="section-7"[\s\S]*?</section>'
    if re.search(sec7_pat, c):
        c = re.sub(sec7_pat, SECTION_7_HTML.strip(), c, count=1)

    # 7. Bottom Script: Wire LessonRuntime
    # Remove old scratchpad & quiz functions
    c = re.sub(r'function runSandboxCode\(\)[\s\S]*?\n    \}', '', c)
    c = re.sub(r'function checkSyntaxOnly\(\)[\s\S]*?\n    \}', '', c)
    c = re.sub(r'function checkQuiz\([\s\S]*?\n    \}', '', c)
    c = re.sub(r'function updateRecallBadge\(\)[\s\S]*?\n    \}', '', c)

    # Add LessonRuntime initialization before </script>
    runtime_init_code = f"""
    // ==========================================
    // INITIALIZATION VIA SHARED RUNTIME ENGINE
    // ==========================================
    window.addEventListener('DOMContentLoaded', () => {{
      // Safe Whiteboard Init
      try {{ if (typeof initWhiteboard === 'function') initWhiteboard(); }} catch(e) {{}}
      try {{ if (typeof initCanvas === 'function') initCanvas(); }} catch(e) {{}}

      // Safe Stepper Execution
      try {{
        if (typeof buildSteps === 'function') {{
          const cand = (typeof stepperData !== 'undefined') ? stepperData :
                       (typeof initialData !== 'undefined') ? initialData :
                       (typeof arrData !== 'undefined') ? arrData :
                       (typeof list1Data !== 'undefined') ? list1Data :
                       (typeof nodesData !== 'undefined') ? nodesData :
                       undefined;
          if (cand !== undefined) {{
            buildSteps(cand);
          }} else {{
            buildSteps();
          }}
        }}
      }} catch(err) {{
        console.warn("Stepper buildSteps note:", err);
      }}

      try {{
        if (typeof renderStepper === 'function') renderStepper();
        if (typeof renderStep === 'function') renderStep();
      }} catch(err) {{
        console.warn("Stepper render note:", err);
      }}

      // Initialize Practice Arena
      LessonRuntime.PracticeArena.init({{
        problemId: '{num_str}',
        methodName: '{method}',
        starterStubs: {json.dumps(stubs)},
        modelSolutions: {json.dumps(sols)},
        hints: {json.dumps(hints)},
        testCases: {json.dumps(tests)}
      }});

      // Initialize 4-Question Balanced Diagnostic Quizzes
      LessonRuntime.Quiz.init({{
        containerId: 'quiz-container',
        questions: {json.dumps(quiz_questions)}
      }});

      // Initialize 48-Hour Cold Recall Protocol
      LessonRuntime.SpacedRecall.init({{
        problemId: '{num_str}',
        badgeId: 'recall-status-badge',
        countdownId: 'recall-countdown'
      }});
    }});
    """

    script_end = c.rfind('</script>')
    if script_end != -1:
        c = c[:script_end] + runtime_init_code + "\n  " + c[script_end:]

    with open(fpath, 'w', encoding='utf-8') as f:
        f.write(c)
    print(f"✅ Upgraded {fpath}")

def main():
    files = sorted(glob.glob('lessons/00[0-2][0-9]*.html'))
    for f in files:
        m = re.search(r'00(\d\d)', f)
        if m:
            num = int(m.group(1))
            if num in [1, 23]: continue
            if 2 <= num <= 22:
                upgrade_file(f)

if __name__ == '__main__':
    main()
