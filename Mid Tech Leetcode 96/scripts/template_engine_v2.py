"""
Template Engine v2 for Mid-Tech LeetCode Masterclass.
Renders Golden Master v2 HTML files:
- Real tri-language runnable Practice Arena via assets/lesson-runtime.js
- 3 Progressive hints & gated model solutions
- 4 length-balanced diagnostic MCQs with randomized answers and distractor feedback
- 48-hour spaced repetition tracker
- Fixed stepper initialization (zero initialData ReferenceErrors)
- Clean CDN links (no broken gstatic links)
"""

import html
import re
import json
import sys
sys.path.append('scripts')
from lesson_builder import extract_method_name, build_stubs, build_solutions, build_hints, build_quiz, extract_test_cases

def render_lesson_v2(p):
    num_str = f"{p['num']:04d}"
    lc_id = p['lc']
    title = p['title']
    category = p['category']
    diff = p['difficulty']
    pass_label = p.get('pass_label', 'Pass 2A')
    prev_file = p.get('prev_file', 'master-plan.html')
    next_file = p.get('next_file', 'master-plan.html')
    prev_num = p['num'] - 1
    next_num = p['num'] + 1
    lc_slug = p.get('lc_slug', title.lower().replace(' ', '-').replace("'", ""))
    lc_url = f"https://leetcode.com/problems/{lc_slug}/"

    method_name = extract_method_name(p)
    stubs = build_stubs(p, method_name)
    solutions = build_solutions(p)
    hints = build_hints(p)
    quiz_questions = build_quiz(p)
    test_cases = extract_test_cases(p)

    # Clean raw LaTeX strings from plain_english and notes
    def clean_latex(text):
        if not text: return ""
        # Replace $...$ with <code>...</code>
        return re.sub(r'\$([^$\n]+)\$', r'<code>\1</code>', text)

    h = f"""<!DOCTYPE html>
<html lang="en" class="dark">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Lesson {p['num']}: {title} (LeetCode #{lc_id}) | Mid-Tech LeetCode</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="../assets/lesson-theme.css">
  <script>
    tailwind.config = {{
      darkMode: 'class',
      theme: {{
        extend: {{
          colors: {{
            slate: {{
              950: '#020617',
              900: '#0f172a',
              850: '#111c38',
              800: '#1e293b',
              700: '#334155',
            }},
            emerald: {{
              400: '#34d399',
              500: '#10b981',
              950: '#022c22',
            }},
            amber: {{
              400: '#fbbf24',
              500: '#f59e0b',
              950: '#451a03',
            }},
            cyan: {{
              400: '#22d3ee',
              500: '#06b6d4',
              950: '#083344',
            }}
          }},
          fontFamily: {{
            sans: ['Plus Jakarta Sans', 'Inter', 'sans-serif'],
            mono: ['JetBrains Mono', 'monospace'],
          }}
        }}
      }}
    }}
  </script>
  <script src="../assets/lesson-runtime.js"></script>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen selection:bg-emerald-500/30 selection:text-emerald-300 antialiased">

  <!-- Top Sticky Navigation Bar -->
  <header class="sticky top-0 z-50 bg-slate-950/90 backdrop-blur-md border-b border-slate-800/80 px-4 py-3">
    <div class="max-w-7xl mx-auto flex items-center justify-between">
      <div class="flex items-center gap-3">
        <a href="../master-plan.html" class="p-2 rounded-lg bg-slate-900 border border-slate-800 hover:border-slate-700 text-slate-400 hover:text-white transition-colors" title="Back to Master Plan">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/></svg>
        </a>
        <div class="flex items-center gap-2">
          <span class="px-2.5 py-1 rounded-md text-xs font-mono font-bold bg-amber-500/10 text-amber-400 border border-amber-500/20">Lesson {num_str}</span>
          <span class="px-2 py-0.5 rounded text-[11px] font-mono font-semibold bg-amber-950/60 text-amber-300 border border-amber-800/60">{pass_label}</span>
          <span class="px-2 py-0.5 rounded text-[11px] font-bold bg-amber-500/10 text-amber-400 border border-amber-500/20">{diff}</span>
        </div>
        <h1 class="text-sm font-bold text-white hidden md:inline truncate max-w-md">{title} (LeetCode #{lc_id})</h1>
      </div>
      <div class="flex items-center gap-2">
        <a href="{prev_file}" class="px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 hover:border-slate-700 text-xs font-mono text-slate-300 hover:text-white transition-colors flex items-center gap-1.5" title="Previous Problem">
          <span>&larr;</span> <span class="hidden sm:inline">Prev #{prev_num}</span>
        </a>
        <a href="../master-plan.html" class="px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 hover:border-slate-700 text-xs font-mono text-slate-300 hover:text-white transition-colors hidden lg:flex items-center gap-1">
          <span>Master Plan</span>
        </a>
        <a href="{next_file}" class="px-3 py-1.5 rounded-lg bg-emerald-500 text-slate-950 font-bold text-xs font-mono hover:bg-emerald-400 transition-colors flex items-center gap-1.5" title="Next Problem">
          <span class="hidden sm:inline">Next #{next_num}</span> <span>&rarr;</span>
        </a>
      </div>
    </div>
  </header>

  <main class="max-w-7xl mx-auto px-4 py-8 space-y-12">

    <!-- Section 0: Problem Identity & Verification Matrix -->
    <section id="section-0" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="flex flex-wrap items-center justify-between gap-4 border-b border-slate-800 pb-5">
        <div>
          <div class="flex items-center gap-2 text-xs font-mono text-amber-400 font-semibold mb-1">
            <span>{pass_label.upper()} &bull; LESSON {p['num']} OF 103</span>
            <span>&bull;</span>
            <span class="text-slate-400">CATEGORY: {category}</span>
          </div>
          <h2 class="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">LeetCode #{lc_id}: {title}</h2>
        </div>
        <div class="flex items-center gap-3">
          <a href="{lc_url}" target="_blank" rel="noopener noreferrer" class="px-3.5 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 border border-slate-700 text-emerald-400 text-xs font-mono font-semibold transition-colors flex items-center gap-1.5">
            <span>Official LC #{lc_id}</span>
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"/></svg>
          </a>
        </div>
      </div>

      <!-- Verbatim Prompt & Plain English Translation -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="space-y-3">
          <h3 class="text-xs font-bold uppercase tracking-wider text-slate-400 font-mono">Official Problem Statement</h3>
          <div class="bg-slate-950/80 rounded-xl p-4 border border-slate-800/80 text-sm text-slate-300 space-y-3 leading-relaxed font-mono">
            {p['verbatim_statement']}
            <div class="border-t border-slate-800/80 pt-3 text-xs text-slate-400 space-y-1">
"""
    for ex in p.get('examples', []):
        h += f"""              <p><strong class="text-slate-300">{ex.get('title', 'Example')}:</strong> <code>{ex.get('input', '')}</code> &rarr; Output: <code>{ex.get('output', '')}</code></p>\n"""

    h += f"""            </div>
          </div>
        </div>

        <div class="space-y-3">
          <h3 class="text-xs font-bold uppercase tracking-wider text-emerald-400 font-mono">Plain-English Translation &amp; Core Nuance</h3>
          <div class="bg-emerald-950/20 rounded-xl p-4 border border-emerald-900/40 text-sm text-slate-200 space-y-3 leading-relaxed">
            <p>{clean_latex(p.get('plain_english', ''))}</p>
            <div class="border-t border-emerald-900/30 pt-3 text-xs text-emerald-300/80 space-y-1">
              <span class="font-bold uppercase tracking-wider text-emerald-400 block font-mono">Official Constraints:</span>
              <ul class="list-disc list-inside space-y-0.5 text-slate-300">
"""
    for c in p.get('constraints', []):
        h += f"""                <li><code>{c}</code></li>\n"""

    bc = p.get('boundary_checks', {})
    h += f"""              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Canonical Input Shapes (Boundary Checks) -->
      <div class="space-y-3 pt-2">
        <h3 class="text-xs font-bold uppercase tracking-wider text-slate-400 font-mono">Canonical Input Shapes (Mental Boundary Checks)</h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs font-mono">
          <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
            <div class="text-emerald-400 font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-emerald-400"></span> Minimum Base Case
            </div>
            <div class="text-slate-300">{bc.get('min_base', 'Empty or single item input')}</div>
          </div>
          <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
            <div class="text-cyan-400 font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-cyan-400"></span> Best-Case Early Exit
            </div>
            <div class="text-slate-300">{bc.get('best_exit', 'Target matched on index 0')}</div>
          </div>
          <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
            <div class="text-amber-400 font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-amber-400"></span> Worst-Case Full Scan
            </div>
            <div class="text-slate-300">{bc.get('worst_scan', 'Maximum constraint traversal')}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Section 0.5: Vector Whiteboard -->
    <section id="sketchpad-section" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 space-y-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
          <h3 class="text-sm font-bold uppercase tracking-wider text-white font-mono">Interactive Vector Whiteboard &amp; Interview Sketchpad</h3>
        </div>
        <div class="flex items-center gap-2">
          <button onclick="toggleCanvas()" id="btn-toggle-canvas" class="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-xs font-mono text-slate-300">Minimize</button>
          <button onclick="clearCanvas()" class="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-xs font-mono text-rose-400">Clear</button>
          <button onclick="undoCanvas()" class="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-xs font-mono text-slate-300">Undo</button>
        </div>
      </div>
      <div id="canvas-container" class="space-y-3">
        <div class="flex items-center gap-3 text-xs font-mono">
          <span class="text-slate-400">Palette:</span>
          <div class="flex items-center gap-1.5">
            <button onclick="setCanvasColor('#10b981')" class="w-5 h-5 rounded-full bg-emerald-500 border border-white/20"></button>
            <button onclick="setCanvasColor('#22d3ee')" class="w-5 h-5 rounded-full bg-cyan-400 border border-white/20"></button>
            <button onclick="setCanvasColor('#fbbf24')" class="w-5 h-5 rounded-full bg-amber-400 border border-white/20"></button>
            <button onclick="setCanvasColor('#c084fc')" class="w-5 h-5 rounded-full bg-purple-400 border border-white/20"></button>
            <button onclick="setCanvasColor('#f43f5e')" class="w-5 h-5 rounded-full bg-rose-500 border border-white/20"></button>
            <button onclick="setCanvasColor('#f8fafc')" class="w-5 h-5 rounded-full bg-slate-100 border border-white/20"></button>
          </div>
          <span class="text-slate-600">|</span>
          <button onclick="setEraser()" id="btn-eraser" class="px-2 py-0.5 rounded bg-slate-800 text-slate-300 text-[11px]">Eraser</button>
          <span class="text-slate-600">|</span>
          <span class="text-slate-400">Width:</span>
          <button onclick="setCanvasWidth(2)" class="px-1.5 py-0.5 rounded bg-slate-800 text-[11px] text-slate-300">2px</button>
          <button onclick="setCanvasWidth(4)" class="px-1.5 py-0.5 rounded bg-slate-800 text-[11px] text-emerald-400 font-bold">4px</button>
          <button onclick="setCanvasWidth(8)" class="px-1.5 py-0.5 rounded bg-slate-800 text-[11px] text-slate-300">8px</button>
        </div>
        <canvas id="whiteboard" width="1150" height="360" class="w-full bg-slate-950 rounded-xl border border-slate-800 cursor-crosshair touch-none"></canvas>
      </div>
    </section>

    <!-- Section 1: Physical Mental Model & Pattern Triggers -->
    <section id="section-1" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-emerald-400 uppercase tracking-wider font-semibold">Cognitive Anchor</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">The Mental Model &amp; "Explain Like I'm 12"</h3>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center gap-2">
            <span class="text-base font-bold text-amber-400">💡 {p.get('mental_model', {}).get('analogy_title', 'Mental Concept')}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {p.get('mental_model', {}).get('analogy_text', '')}
          </p>
          <div class="p-3.5 rounded-lg bg-emerald-950/20 border border-emerald-900/40 text-xs text-emerald-300 space-y-1">
            <strong class="font-bold text-emerald-400 block font-mono">The Mental "A-Ha!" Switch:</strong>
            <p>{p.get('mental_model', {}).get('aha_moment', '')}</p>
          </div>
        </div>

        <div class="p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-4 text-xs font-mono">
          <div>
            <span class="text-slate-400 block uppercase font-bold text-[10px] tracking-wider">Pattern Trigger:</span>
            <p class="text-amber-300 mt-1">{p.get('mental_model', {}).get('trigger', '')}</p>
          </div>
          <div class="border-t border-slate-800 pt-3">
            <span class="text-slate-400 block uppercase font-bold text-[10px] tracking-wider mb-1.5">Sister Problems:</span>
            <div class="space-y-1 text-slate-300">
"""
    for sis in p.get('mental_model', {}).get('sisters', []):
        h += f"""              <div class="flex items-center justify-between"><span>{sis.get('name', '')}</span><span class="text-emerald-400">{sis.get('lc', '')}</span></div>\n"""

    nv = p.get('naive_vs_optimal', {})
    h += f"""            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Section 2: Naive Trap vs Optimal Breakthrough -->
    <section id="section-2" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-rose-400 uppercase tracking-wider font-semibold">Algorithmic Trade-off Analysis</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">The Naive Trap (Red) vs. Optimal Breakthrough (Green)</h3>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Red Naive Trap -->
        <div class="p-5 rounded-xl bg-rose-950/20 border border-rose-900/40 space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs font-mono font-bold text-rose-400 uppercase tracking-wider flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-rose-500"></span> The Naive Trap
            </span>
            <span class="px-2 py-0.5 rounded text-[10px] font-mono bg-rose-900/40 text-rose-300">{clean_latex(nv.get('naive_complexity', 'O(N^2)'))}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {clean_latex(nv.get('naive_text', ''))}
          </p>
          <div class="p-3 rounded-lg bg-rose-950/40 border border-rose-900/50 text-xs text-rose-300 font-mono">
            <strong>Wasted Work:</strong> {clean_latex(nv.get('wasted_work', ''))}
          </div>
        </div>

        <!-- Green Optimal Breakthrough -->
        <div class="p-5 rounded-xl bg-emerald-950/20 border border-emerald-900/40 space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-emerald-500"></span> The Optimal Breakthrough
            </span>
            <span class="px-2 py-0.5 rounded text-[10px] font-mono bg-emerald-900/40 text-emerald-300">{clean_latex(nv.get('optimal_complexity', 'O(N)'))}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {clean_latex(nv.get('optimal_text', ''))}
          </p>
          <div class="p-3 rounded-lg bg-emerald-950/40 border border-emerald-900/50 text-xs text-emerald-300 font-mono">
            <strong>Core Invariant:</strong> {clean_latex(nv.get('core_invariant', ''))}
          </div>
        </div>
      </div>

      <!-- Space-Time Bargain Table -->
      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs font-mono border border-slate-800 rounded-xl overflow-hidden">
          <thead class="bg-slate-950 text-slate-400 uppercase text-[10px] border-b border-slate-800">
            <tr>
              <th class="p-3">Approach</th>
              <th class="p-3">Time Complexity</th>
              <th class="p-3">Space Complexity</th>
              <th class="p-3">Practical Trade-off / Bottleneck</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800/60 bg-slate-900/30">
"""
    for row in nv.get('table_rows', []):
        badge_cls = "text-rose-400 font-bold" if "Brute" in row.get('name', '') or "Naive" in row.get('name', '') else ("text-amber-400 font-bold" if "Better" in row.get('name', '') else "text-emerald-400 font-bold")
        h += f"""            <tr>
              <td class="p-3 {badge_cls}">{row.get('name', '')}</td>
              <td class="p-3 text-slate-200">{clean_latex(row.get('time', ''))}</td>
              <td class="p-3 text-slate-200">{clean_latex(row.get('space', ''))}</td>
              <td class="p-3 text-slate-400">{clean_latex(row.get('note', ''))}</td>
            </tr>\n"""

    tt = p.get('trace_table', {})
    h += f"""          </tbody>
        </table>
      </div>
    </section>

    <!-- Section 3: Concrete Step-by-Step Trace Table -->
    <section id="section-3" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-cyan-400 uppercase tracking-wider font-semibold">Concrete Execution Trace</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">The Concrete Variable Trace Table</h3>
        <p class="text-xs text-slate-400 font-mono mt-1">Trace input: {clean_latex(tt.get('input_desc', ''))}</p>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs font-mono border border-slate-800 rounded-xl overflow-hidden">
          <thead class="bg-slate-950 text-slate-400 uppercase text-[10px] border-b border-slate-800">
            <tr>
"""
    for col in tt.get('columns', []):
        h += f"""              <th class="p-3">{col}</th>\n"""
    h += f"""            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800/60 bg-slate-900/30 text-slate-300">
"""
    for row in tt.get('rows', []):
        h += "            <tr>\n"
        for i, val in enumerate(row):
            cls = "p-3 font-semibold text-white" if i == 0 else "p-3"
            h += f"""              <td class="{cls}">{val}</td>\n"""
        h += "            </tr>\n"

    st = p.get('stepper', {})
    h += f"""          </tbody>
        </table>
      </div>
      <p class="text-xs text-slate-400 leading-relaxed">{clean_latex(tt.get('takeaway', ''))}</p>
    </section>

    <!-- Section 4: Matt Pocock Interactive Visual Stepper -->
    <section id="section-4" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div>
          <span class="text-xs font-mono text-cyan-400 uppercase tracking-wider font-semibold">Matt Pocock Interactive Visualizer</span>
          <h3 class="text-xl sm:text-2xl font-bold text-white">{st.get('title', 'State Machine & Memory Trace')}</h3>
        </div>
        <!-- Presets -->
        <div class="flex items-center gap-2">
"""
    for pr in st.get('presets', []):
        h += f"""          <button onclick="{pr.get('onclick', '')}" class="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-xs font-mono text-slate-300">{pr.get('label', '')}</button>\n"""

    h += f"""        </div>
      </div>

      <!-- Custom Input Bar -->
      <div class="flex items-center gap-3 p-3 rounded-xl bg-slate-950 border border-slate-800">
        <label for="custom-input" class="text-xs font-mono text-slate-400 whitespace-nowrap">Input:</label>
        <input id="custom-input" type="text" value="{st.get('default_input', '')}" class="flex-1 bg-slate-900 border border-slate-700 rounded-lg px-3 py-1 text-xs font-mono text-white focus:outline-none focus:border-emerald-500">
        <button onclick="handleCustomInput()" class="px-3 py-1 rounded bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs font-mono transition-colors">Apply</button>
      </div>

      <!-- Visual Stage Container -->
      <div class="p-6 rounded-xl bg-slate-950 border border-slate-800 space-y-6">
        <!-- Live State Metrics Bar -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs font-mono">
"""
    for m in st.get('metrics', []):
        h += f"""          <div class="p-3 rounded-xl bg-slate-900 border border-slate-800">
            <span class="text-[10px] text-slate-500 uppercase block">{m.get('label', '')}</span>
            <span id="{m.get('id', '')}" class="text-sm font-bold {m.get('color', '')}">{m.get('initial', '')}</span>
          </div>\n"""

    h += f"""        </div>

        <!-- Animation Canvas / Container -->
        <div id="stepper-visual-container" class="min-h-[160px] flex flex-col justify-center items-center py-6 border border-slate-800/80 rounded-xl bg-slate-900/40 p-4 overflow-x-auto">
          {st.get('visual_html', '')}
        </div>

        <!-- Narration & Controls -->
        <div class="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">
          <div class="flex items-center gap-2 text-xs font-mono text-slate-300">
            <span class="w-2 h-2 rounded-full bg-cyan-400"></span>
            <span id="stepper-narration" class="leading-relaxed">{st.get('initial_narration', '')}</span>
          </div>
          <span id="stepper-counter" class="text-xs font-mono text-slate-500 whitespace-nowrap">Step 0</span>
        </div>
      </div>

      <!-- Synced Pseudocode Highlighting -->
      <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
        <span class="text-xs font-mono text-slate-400 uppercase font-bold tracking-wider">Synced Pseudocode Line Tracking</span>
        <pre class="text-xs font-mono p-3 bg-slate-900/60 rounded-lg overflow-x-auto space-y-0.5 leading-relaxed">
"""
    for i, line in enumerate(st.get('pseudocode_lines', [])):
        line_num = i + 1
        h += f"""<div id="code-line-{line_num}" class="px-2 py-0.5 rounded text-slate-400"><span class="text-slate-600 select-none mr-3">{line_num:02d}</span>{html.escape(line)}</div>\n"""

    h += f"""</pre>
      </div>

      <!-- Playback Controls Bar -->
      <div class="flex items-center justify-between gap-3">
        <div class="flex items-center gap-2">
          <button onclick="stepperReset()" class="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-mono transition-colors">Reset</button>
          <button onclick="togglePlay()" id="btn-play" class="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-cyan-400 text-xs font-mono transition-colors">Play &#9654;</button>
        </div>
        <div class="flex items-center gap-2">
          <button onclick="stepperPrev()" id="btn-prev" class="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-mono transition-colors disabled:opacity-40">&larr; Prev Step</button>
          <button onclick="stepperNext()" id="btn-next" class="px-3 py-1.5 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs font-mono transition-colors disabled:opacity-40">Next Step &rarr;</button>
        </div>
      </div>
    </section>

    <!-- Section 5: Multi-Approach Solutions & Runnable Practice Arena -->
    <section id="section-5" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-8">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between border-b border-slate-800 pb-4 gap-4">
        <div>
          <span class="text-xs font-mono text-emerald-400 uppercase tracking-wider font-semibold">Production Implementations</span>
          <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">From Naive to Optimal &amp; Runnable Practice Arena</h3>
          <p class="text-xs text-slate-400 mt-1">Strict ordering: Kotlin (Default) &rarr; Python &rarr; Java across all approaches.</p>
        </div>

        <!-- Approach Tabs Selector -->
        <div class="flex items-center bg-slate-950 p-1 rounded-xl border border-slate-800 text-xs font-mono">
"""
    app_count = len(p.get('approaches', []))
    for idx, app in enumerate(p.get('approaches', [])):
        app_num = idx + 1
        is_last = (app_num == app_count)
        name = app.get('name', f'Approach {app_num}')
        if is_last:
            h += f"""          <button onclick="switchApproach({app_num})" id="tab-app-{app_num}" class="px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold transition-all">&#9733; {app_num}. {name} (Optimal)</button>\n"""
        else:
            h += f"""          <button onclick="switchApproach({app_num})" id="tab-app-{app_num}" class="px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all">{app_num}. {name}</button>\n"""

    h += f"""        </div>
      </div>
"""
    for idx, app in enumerate(p.get('approaches', [])):
        app_num = idx + 1
        hidden_cls = "" if app_num == app_count else "hidden"
        name = app.get('name', f'Approach {app_num}')
        desc = app.get('desc') or app.get('summary') or ''
        
        comp = app.get('complexity', 'O(N) Time, O(1) Space')
        time_val = app.get('time') or comp.split(',')[0].strip()
        space_val = app.get('space') or (comp.split(',')[1].strip() if ',' in comp else 'O(1)')

        code_k = (app.get('code_kotlin') or app.get('kotlin_code') or app.get('kotlin') or '').strip()
        code_p = (app.get('code_python') or app.get('python_code') or app.get('python') or '').strip()
        code_j = (app.get('code_java') or app.get('java_code') or app.get('java') or '').strip()

        notes_k = app.get('notes_kotlin') or "Idiomatic Kotlin implementation optimizing collections and null-safety."
        notes_p = app.get('notes_python') or "Pythonic implementation using optimal data structures and built-ins."
        notes_j = app.get('notes_java') or "Robust Java implementation avoiding unnecessary heap allocations."

        h += f"""      <!-- APPROACH {app_num}: {name} -->
      <div id="approach-{app_num}" class="{hidden_cls} space-y-6">
        <div class="flex flex-wrap items-center justify-between gap-3 p-4 rounded-xl bg-slate-950 border border-slate-800">
          <div>
            <h4 class="text-sm font-bold text-white font-mono">{name}</h4>
            <p class="text-xs text-slate-400 mt-0.5">{desc}</p>
          </div>
          <div class="flex items-center gap-3 text-xs font-mono">
            <span class="px-2.5 py-1 rounded bg-slate-900 border border-slate-700 text-slate-300">Time: <strong class="text-emerald-400">{time_val}</strong></span>
            <span class="px-2.5 py-1 rounded bg-slate-900 border border-slate-700 text-slate-300">Space: <strong class="text-cyan-400">{space_val}</strong></span>
          </div>
        </div>

        <div class="flex items-center gap-2 border-b border-slate-800 pb-2 text-xs font-mono">
          <button onclick="switchLangApp{app_num}('kotlin')" id="lang-btn-app{app_num}-kotlin" class="px-3 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold">Kotlin (Default)</button>
          <button onclick="switchLangApp{app_num}('python')" id="lang-btn-app{app_num}-python" class="px-3 py-1 rounded text-slate-400 hover:text-white">Python</button>
          <button onclick="switchLangApp{app_num}('java')" id="lang-btn-app{app_num}-java" class="px-3 py-1 rounded text-slate-400 hover:text-white">Java</button>
        </div>

        <div id="code-app{app_num}-kotlin" class="space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-emerald-300 overflow-x-auto leading-relaxed">{html.escape(code_k)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-emerald-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Kotlin Runtime &amp; Memory Nuance:</span>
            <p>{notes_k}</p>
          </div>
        </div>

        <div id="code-app{app_num}-python" class="hidden space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-amber-300 overflow-x-auto leading-relaxed">{html.escape(code_p)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-amber-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Python Runtime &amp; Memory Nuance:</span>
            <p>{notes_p}</p>
          </div>
        </div>

        <div id="code-app{app_num}-java" class="hidden space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-cyan-300 overflow-x-auto leading-relaxed">{html.escape(code_j)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-cyan-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Java Runtime &amp; Memory Nuance:</span>
            <p>{notes_j}</p>
          </div>
        </div>
      </div>\n"""

    # Golden Master Practice Arena HTML Container
    h += f"""
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
    </section>

    <!-- Section 6: Spoken Interview Script & Senior Scalability -->
    <section id="section-6" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-amber-400 uppercase tracking-wider font-semibold">Live Interview Execution Transcript</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">Live Interview Spoken Script &amp; Senior Follow-Ups</h3>
        <p class="text-xs text-slate-400 mt-1">Word-for-word 4-phase interview dialogue across constraints inquiry, pitch, coding, and dry run.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-cyan-400 uppercase tracking-wider block">Phase 1: Clarifying Questions &amp; Edge Cases</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">"{p.get('spoken_script', {}).get('phase1', '')}"</p>
        </div>
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider block">Phase 2: Space-Time Strategy Pitch</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">"{p.get('spoken_script', {}).get('phase2', '')}"</p>
        </div>
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-amber-400 uppercase tracking-wider block">Phase 3: Live Coding Narrative</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">"{p.get('spoken_script', {}).get('phase3', '')}"</p>
        </div>
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-purple-400 uppercase tracking-wider block">Phase 4: Cold Edge-Case Verification</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">"{p.get('spoken_script', {}).get('phase4', '')}"</p>
        </div>
      </div>

      <div class="p-4 rounded-xl bg-slate-950 border border-amber-900/40 text-xs text-slate-300 space-y-2">
        <span class="text-amber-400 font-bold font-mono uppercase tracking-wider block text-[11px]">&#9889; Senior Architectural &amp; Scalability Follow-Up:</span>
        <p class="leading-relaxed">{p.get('spoken_script', {}).get('senior_followup', '')}</p>
      </div>
    </section>

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
          <li><strong>Step 2 (24 Hours):</strong> Re-read Section 1 ({p.get('mental_model', {}).get('analogy_title', 'Mental Concept')}) and articulate the mental switch out loud.</li>
          <li><strong>Step 3 (48 Hours):</strong> Open LeetCode #{lc_id} cold. Code the solution in under 10 minutes without any hints.</li>
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

  </main>

  <!-- Interactive JavaScript Engine -->
  <script>
    // ---------------- WHITEBOARD CANVAS LOGIC ----------------
    const canvas = document.getElementById('whiteboard');
    const ctx = canvas.getContext('2d');
    let isDrawing = false;
    let currentColor = '#10b981';
    let currentWidth = 4;
    let canvasHistory = [];

    function saveState() {{
      canvasHistory.push(ctx.getImageData(0, 0, canvas.width, canvas.height));
      if (canvasHistory.length > 20) canvasHistory.shift();
    }}

    function initCanvas() {{
      ctx.fillStyle = '#020617';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      ctx.fillStyle = '#1e293b';
      for (let x = 20; x < canvas.width; x += 30) {{
        for (let y = 20; y < canvas.height; y += 30) {{
          ctx.fillRect(x, y, 1.5, 1.5);
        }}
      }}
      saveState();
    }}

    function getCoords(e) {{
      const rect = canvas.getBoundingClientRect();
      const scaleX = canvas.width / rect.width;
      const scaleY = canvas.height / rect.height;
      if (e.touches && e.touches.length > 0) {{
        return {{
          x: (e.touches[0].clientX - rect.left) * scaleX,
          y: (e.touches[0].clientY - rect.top) * scaleY
        }};
      }}
      return {{
        x: (e.clientX - rect.left) * scaleX,
        y: (e.clientY - rect.top) * scaleY
      }};
    }}

    function startDraw(e) {{
      isDrawing = true;
      const pos = getCoords(e);
      ctx.beginPath();
      ctx.moveTo(pos.x, pos.y);
      e.preventDefault();
    }}

    function draw(e) {{
      if (!isDrawing) return;
      const pos = getCoords(e);
      ctx.lineTo(pos.x, pos.y);
      ctx.strokeStyle = currentColor;
      ctx.lineWidth = currentWidth;
      ctx.lineCap = 'round';
      ctx.lineJoin = 'round';
      ctx.stroke();
      e.preventDefault();
    }}

    function stopDraw() {{
      if (isDrawing) {{
        isDrawing = false;
        ctx.closePath();
        saveState();
      }}
    }}

    canvas.addEventListener('mousedown', startDraw);
    canvas.addEventListener('mousemove', draw);
    window.addEventListener('mouseup', stopDraw);
    canvas.addEventListener('touchstart', startDraw, {{ passive: false }});
    canvas.addEventListener('touchmove', draw, {{ passive: false }});
    window.addEventListener('touchend', stopDraw);

    function setCanvasColor(color) {{
      currentColor = color;
      document.getElementById('btn-eraser').className = "px-2 py-0.5 rounded bg-slate-800 text-slate-300 text-[11px]";
    }}

    function setCanvasWidth(w) {{ currentWidth = w; }}

    function setEraser() {{
      currentColor = '#020617';
      document.getElementById('btn-eraser').className = "px-2 py-0.5 rounded bg-rose-500/20 text-rose-300 border border-rose-500/40 text-[11px] font-bold";
    }}

    function clearCanvas() {{
      ctx.fillStyle = '#020617';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      ctx.fillStyle = '#1e293b';
      for (let x = 20; x < canvas.width; x += 30) {{
        for (let y = 20; y < canvas.height; y += 30) {{
          ctx.fillRect(x, y, 1.5, 1.5);
        }}
      }}
      saveState();
    }}

    function undoCanvas() {{
      if (canvasHistory.length > 1) {{
        canvasHistory.pop();
        ctx.putImageData(canvasHistory[canvasHistory.length - 1], 0, 0);
      }}
    }}

    function toggleCanvas() {{
      const c = document.getElementById('canvas-container');
      const btn = document.getElementById('btn-toggle-canvas');
      if (c.classList.contains('hidden')) {{
        c.classList.remove('hidden');
        btn.textContent = 'Minimize';
      }} else {{
        c.classList.add('hidden');
        btn.textContent = 'Expand';
      }}
    }}

    // ---------------- VISUAL STEPPER LOGIC ----------------
    let steps = [];
    let currentStepIdx = 0;
    let playTimer = null;
    let isPlaying = false;

    {st.get('js_code', '')}

    function renderStepper() {{
      if (steps.length === 0) return;
      const step = steps[currentStepIdx];
      
      const narr = document.getElementById('stepper-narration');
      if (narr) narr.textContent = step.narration;
      const countEl = document.getElementById('stepper-counter');
      if (countEl) countEl.textContent = `Step ${{currentStepIdx + 1}} of ${{steps.length}}`;

      // Update metrics
      {st.get('js_update_metrics', '')}

      // Render custom visual
      {st.get('js_render_visual', 'if (typeof renderStage === "function") renderStage(step);')}

      // Synced pseudocode line highlighting
      const totalLines = {len(st.get('pseudocode_lines', []))};
      for (let i = 1; i <= totalLines; i++) {{
        const lineEl = document.getElementById(`code-line-${{i}}`);
        if (lineEl) {{
          if (step.line === i) {{
            lineEl.className = "px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-300 border-l-2 border-emerald-400 font-bold";
          }} else {{
            lineEl.className = "px-2 py-0.5 rounded text-slate-400";
          }}
        }}
      }}

      const btnPrev = document.getElementById('btn-prev');
      if (btnPrev) btnPrev.disabled = (currentStepIdx === 0);
      const btnNext = document.getElementById('btn-next');
      if (btnNext) btnNext.disabled = (currentStepIdx === steps.length - 1);
    }}

    function stepperNext() {{
      if (currentStepIdx < steps.length - 1) {{
        currentStepIdx++;
        renderStepper();
      }} else if (isPlaying) {{
        togglePlay();
      }}
    }}

    function stepperPrev() {{
      if (currentStepIdx > 0) {{
        currentStepIdx--;
        renderStepper();
      }}
    }}

    function stepperReset() {{
      currentStepIdx = 0;
      if (isPlaying) togglePlay();
      renderStepper();
    }}

    function togglePlay() {{
      const btn = document.getElementById('btn-play');
      if (!btn) return;
      if (isPlaying) {{
        clearInterval(playTimer);
        isPlaying = false;
        btn.innerHTML = 'Play &#9654;';
        btn.className = "px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-cyan-400 text-xs font-mono transition-colors";
      }} else {{
        isPlaying = true;
        btn.innerHTML = 'Pause &#10074;&#10074;';
        btn.className = "px-3 py-1.5 rounded-lg bg-amber-500/20 text-amber-300 border border-amber-500/40 text-xs font-mono font-bold transition-colors";
        playTimer = setInterval(() => {{
          if (currentStepIdx < steps.length - 1) {{
            stepperNext();
          }} else {{
            togglePlay();
          }}
        }}, 1400);
      }}
    }}

    // ---------------- SOLUTION TABS LOGIC ----------------
    function switchApproach(num) {{
      const total = {len(p.get('approaches', []))};
      for (let n = 1; n <= total; n++) {{
        const block = document.getElementById(`approach-${{n}}`);
        const tab = document.getElementById(`tab-app-${{n}}`);
        if (!block || !tab) continue;
        if (n === num) {{
          block.classList.remove('hidden');
          tab.className = "px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold transition-all";
        }} else {{
          block.classList.add('hidden');
          tab.className = "px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all";
        }}
      }}
    }}

    function makeLangSwitcher(appNum) {{
      return function(lang) {{
        ['kotlin', 'python', 'java'].forEach(l => {{
          const codeEl = document.getElementById(`code-app${{appNum}}-${{l}}`);
          const btn = document.getElementById(`lang-btn-app${{appNum}}-${{l}}`);
          if (codeEl && btn) {{
            if (l === lang) {{
              codeEl.classList.remove('hidden');
              btn.className = "px-3 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold";
            }} else {{
              codeEl.classList.add('hidden');
              btn.className = "px-3 py-1 rounded text-slate-400 hover:text-white";
            }}
          }}
        }});
      }};
    }}

    for (let a = 1; a <= {len(p.get('approaches', []))}; a++) {{
      window[`switchLangApp${{a}}`] = makeLangSwitcher(a);
    }}

    // Safe Stepper Candidate Finder
    function getStepperCandidate() {{
      try {{
        if (typeof defaultInput !== 'undefined') return defaultInput;
        if (typeof initialData !== 'undefined') return initialData;
        if (typeof initialOps !== 'undefined') return initialOps;
        if (typeof rawLists !== 'undefined') return rawLists;
        if (typeof rawStream !== 'undefined') return rawStream;
        if (typeof rawGrid !== 'undefined') return rawGrid;
        if (typeof rawArr !== 'undefined') return rawArr;
        if (typeof rawNums !== 'undefined') return rawNums;
        if (typeof rawIntervals !== 'undefined') return rawIntervals;
        if (typeof rawPoints !== 'undefined') return rawPoints;
        if (typeof rawBoard !== 'undefined') return rawBoard;
        if (typeof rawRooms !== 'undefined') return rawRooms;
        if (typeof rawProv !== 'undefined') return rawProv;
        if (typeof rawDict !== 'undefined') return rawDict;
        if (typeof rawTimes !== 'undefined') return rawTimes;
        if (typeof rawEdges !== 'undefined') return rawEdges;
        if (typeof rawPrereqs !== 'undefined') return rawPrereqs;
        if (typeof rawOranges !== 'undefined') return rawOranges;
        if (typeof rawCandidates !== 'undefined') return rawCandidates;
        if (typeof rawH !== 'undefined') return rawH;
        if (typeof rawPiles !== 'undefined') return rawPiles;
        if (typeof rawPts !== 'undefined') return rawPts;
        if (typeof dataNums !== 'undefined') return dataNums;
        if (typeof dataList !== 'undefined') return dataList;
        if (typeof gridData !== 'undefined') return gridData;
        if (typeof matrixData !== 'undefined') return matrixData;
        if (typeof baseIntervals !== 'undefined') return baseIntervals;
        if (typeof initialBalances !== 'undefined') return initialBalances;
        if (typeof initialNodes !== 'undefined') return initialNodes;
        if (typeof initialArr !== 'undefined') return initialArr;
        if (typeof trieCommands !== 'undefined') return trieCommands;
        if (typeof currentArray !== 'undefined') return currentArray;
        if (typeof arrData !== 'undefined') return arrData;
        if (typeof stepperData !== 'undefined') return stepperData;
      }} catch(e) {{}}
      return undefined;
    }}

    // ==========================================
    // INITIALIZATION VIA SHARED RUNTIME ENGINE
    // ==========================================
    window.addEventListener('DOMContentLoaded', () => {{
      // Initialize Whiteboard
      try {{ initCanvas(); }} catch(e) {{}}

      // Safe Stepper Execution
      try {{
        if (typeof buildSteps === 'function') {{
          const cand = getStepperCandidate();
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
      }} catch(err) {{
        console.warn("Stepper renderStepper note:", err);
      }}

      // Initialize Practice Arena
      LessonRuntime.PracticeArena.init({{
        problemId: '{num_str}',
        methodName: '{method_name}',
        starterStubs: {json.dumps(stubs)},
        modelSolutions: {json.dumps(solutions)},
        hints: {json.dumps(hints)},
        testCases: {json.dumps(test_cases)}
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
  </script>
</body>
</html>"""
    return h
