import html

def render_lesson(p):
    num_str = f"{p['num']:04d}"
    lc_id = p['lc']
    title = p['title']
    category = p['category']
    diff = p['difficulty']
    pass_label = p.get('pass_label', 'Pass 2A')
    prev_file = p['prev_file']
    next_file = p['next_file']
    prev_num = p['num'] - 1
    next_num = p['num'] + 1
    lc_slug = p.get('lc_slug', title.lower().replace(' ', '-').replace("'", ""))
    lc_url = f"https://leetcode.com/problems/{lc_slug}/"

    # HTML header and Tailwind config
    h = f"""<!DOCTYPE html>
<html lang="en" class="dark">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Lesson {p['num']}: {title} (LeetCode #{lc_id}) | Mid-Tech LeetCode 96</title>
  <script src="https://cdn.tailwindcss.com"></script>
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
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600;700&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    body {{ font-family: 'Plus Jakarta Sans', sans-serif; }}
    pre, code, .font-mono {{ font-family: 'JetBrains Mono', monospace; }}
    .custom-scrollbar::-webkit-scrollbar {{ width: 6px; height: 6px; }}
    .custom-scrollbar::-webkit-scrollbar-track {{ background: #0f172a; }}
    .custom-scrollbar::-webkit-scrollbar-thumb {{ background: #334155; border-radius: 3px; }}
    .custom-scrollbar::-webkit-scrollbar-thumb:hover {{ background: #475569; }}
  </style>
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
            <span>{pass_label.upper()} &bull; LESSON {p['num']} OF 96</span>
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
    for ex in p['examples']:
        h += f"""              <p><strong class="text-slate-300">{ex['title']}:</strong> <code>{ex['input']}</code> &rarr; Output: <code>{ex['output']}</code></p>\n"""

    h += f"""            </div>
          </div>
        </div>

        <div class="space-y-3">
          <h3 class="text-xs font-bold uppercase tracking-wider text-emerald-400 font-mono">Plain-English Translation &amp; Core Nuance</h3>
          <div class="bg-emerald-950/20 rounded-xl p-4 border border-emerald-900/40 text-sm text-slate-200 space-y-3 leading-relaxed">
            <p>{p['plain_english']}</p>
            <div class="border-t border-emerald-900/30 pt-3 text-xs text-emerald-300/80 space-y-1">
              <span class="font-bold uppercase tracking-wider text-emerald-400 block font-mono">Official Constraints:</span>
              <ul class="list-disc list-inside space-y-0.5 text-slate-300">
"""
    for c in p['constraints']:
        h += f"""                <li><code>{c}</code></li>\n"""

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
            <div class="text-slate-300">{p['boundary_checks']['min_base']}</div>
          </div>
          <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
            <div class="text-cyan-400 font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-cyan-400"></span> Best-Case Early Exit
            </div>
            <div class="text-slate-300">{p['boundary_checks']['best_exit']}</div>
          </div>
          <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
            <div class="text-amber-400 font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-amber-400"></span> Worst-Case Full Scan
            </div>
            <div class="text-slate-300">{p['boundary_checks']['worst_scan']}</div>
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
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">The Mental Model &amp; "Explain Like I\'m 12"</h3>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center gap-2">
            <span class="text-base font-bold text-amber-400">💡 {p['mental_model']['analogy_title']}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {p['mental_model']['analogy_text']}
          </p>
          <div class="p-3.5 rounded-lg bg-emerald-950/20 border border-emerald-900/40 text-xs text-emerald-300 space-y-1">
            <strong class="font-bold text-emerald-400 block font-mono">The Mental "A-Ha!" Switch:</strong>
            <p>{p['mental_model']['aha_moment']}</p>
          </div>
        </div>

        <div class="p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-4 text-xs font-mono">
          <div>
            <span class="text-slate-400 block uppercase font-bold text-[10px] tracking-wider">Pattern Trigger:</span>
            <p class="text-amber-300 mt-1">{p['mental_model']['trigger']}</p>
          </div>
          <div class="border-t border-slate-800 pt-3">
            <span class="text-slate-400 block uppercase font-bold text-[10px] tracking-wider mb-1.5">Sister Problems:</span>
            <div class="space-y-1 text-slate-300">
"""
    for sis in p['mental_model']['sisters']:
        h += f"""              <div class="flex items-center justify-between"><span>{sis['name']}</span><span class="text-emerald-400">{sis['lc']}</span></div>\n"""

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
            <span class="px-2 py-0.5 rounded text-[10px] font-mono bg-rose-900/40 text-rose-300">{p['naive_vs_optimal']['naive_complexity']}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {p['naive_vs_optimal']['naive_text']}
          </p>
          <div class="p-3 rounded-lg bg-rose-950/40 border border-rose-900/50 text-xs text-rose-300 font-mono">
            <strong>Wasted Work:</strong> {p['naive_vs_optimal']['wasted_work']}
          </div>
        </div>

        <!-- Green Optimal Breakthrough -->
        <div class="p-5 rounded-xl bg-emerald-950/20 border border-emerald-900/40 space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-emerald-500"></span> The Optimal Breakthrough
            </span>
            <span class="px-2 py-0.5 rounded text-[10px] font-mono bg-emerald-900/40 text-emerald-300">{p['naive_vs_optimal']['optimal_complexity']}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {p['naive_vs_optimal']['optimal_text']}
          </p>
          <div class="p-3 rounded-lg bg-emerald-950/40 border border-emerald-900/50 text-xs text-emerald-300 font-mono">
            <strong>Core Invariant:</strong> {p['naive_vs_optimal']['core_invariant']}
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
    for row in p['naive_vs_optimal']['table_rows']:
        badge_cls = "text-rose-400 font-bold" if "Brute" in row['name'] or "Naive" in row['name'] else ("text-amber-400 font-bold" if "Better" in row['name'] else "text-emerald-400 font-bold")
        h += f"""            <tr>
              <td class="p-3 {badge_cls}">{row['name']}</td>
              <td class="p-3 text-slate-200">{row['time']}</td>
              <td class="p-3 text-slate-200">{row['space']}</td>
              <td class="p-3 text-slate-400">{row['note']}</td>
            </tr>\n"""

    h += f"""          </tbody>
        </table>
      </div>
    </section>

    <!-- Section 3: Concrete Step-by-Step Trace Table -->
    <section id="section-3" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-cyan-400 uppercase tracking-wider font-semibold">Concrete Execution Trace</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">The Concrete Variable Trace Table</h3>
        <p class="text-xs text-slate-400 font-mono mt-1">Trace input: {p['trace_table']['input_desc']}</p>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs font-mono border border-slate-800 rounded-xl overflow-hidden">
          <thead class="bg-slate-950 text-slate-400 uppercase text-[10px] border-b border-slate-800">
            <tr>
"""
    for col in p['trace_table']['columns']:
        h += f"""              <th class="p-3">{col}</th>\n"""
    h += f"""            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800/60 bg-slate-900/30 text-slate-300">
"""
    for row in p['trace_table']['rows']:
        h += "            <tr>\n"
        for i, val in enumerate(row):
            cls = "p-3 font-semibold text-white" if i == 0 else "p-3"
            h += f"""              <td class="{cls}">{val}</td>\n"""
        h += "            </tr>\n"

    h += f"""          </tbody>
        </table>
      </div>
      <p class="text-xs text-slate-400 leading-relaxed">{p['trace_table']['takeaway']}</p>
    </section>

    <!-- Section 4: Matt Pocock Interactive Visual Stepper -->
    <section id="section-4" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div>
          <span class="text-xs font-mono text-cyan-400 uppercase tracking-wider font-semibold">Matt Pocock Interactive Visualizer</span>
          <h3 class="text-xl sm:text-2xl font-bold text-white">{p['stepper']['title']}</h3>
        </div>
        <!-- Input Selector & Controls -->
        <div class="flex items-center gap-2">
"""
    for pr in p['stepper']['presets']:
        h += f"""          <button onclick="{pr['onclick']}" class="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-xs font-mono text-slate-300">{pr['label']}</button>\n"""

    h += f"""        </div>
      </div>

      <!-- Custom Input Bar -->
      <div class="flex items-center gap-3 p-3 rounded-xl bg-slate-950 border border-slate-800">
        <label for="custom-input" class="text-xs font-mono text-slate-400 whitespace-nowrap">Input:</label>
        <input id="custom-input" type="text" value="{p['stepper']['default_input']}" class="flex-1 bg-slate-900 border border-slate-700 rounded-lg px-3 py-1 text-xs font-mono text-white focus:outline-none focus:border-emerald-500">
        <button onclick="handleCustomInput()" class="px-3 py-1 rounded bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs font-mono transition-colors">Apply</button>
      </div>

      <!-- Visual Stage Container -->
      <div class="p-6 rounded-xl bg-slate-950 border border-slate-800 space-y-6">
        <!-- Live State Metrics Bar -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs font-mono">
"""
    for m in p['stepper']['metrics']:
        h += f"""          <div class="p-3 rounded-xl bg-slate-900 border border-slate-800">
            <span class="text-[10px] text-slate-500 uppercase block">{m['label']}</span>
            <span id="{m['id']}" class="text-sm font-bold {m['color']}">{m['initial']}</span>
          </div>\n"""

    h += f"""        </div>

        <!-- Animation Canvas / Container -->
        <div id="stepper-visual-container" class="min-h-[160px] flex flex-col justify-center items-center py-6 border border-slate-800/80 rounded-xl bg-slate-900/40 p-4 overflow-x-auto">
          {p['stepper']['visual_html']}
        </div>

        <!-- Narration & Controls -->
        <div class="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">
          <div class="flex items-center gap-2 text-xs font-mono text-slate-300">
            <span class="w-2 h-2 rounded-full bg-cyan-400"></span>
            <span id="stepper-narration" class="leading-relaxed">{p['stepper']['initial_narration']}</span>
          </div>
          <span id="stepper-counter" class="text-xs font-mono text-slate-500 whitespace-nowrap">Step 0</span>
        </div>
      </div>

      <!-- Synced Pseudocode Highlighting -->
      <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
        <span class="text-xs font-mono text-slate-400 uppercase font-bold tracking-wider">Synced Pseudocode Line Tracking</span>
        <pre class="text-xs font-mono p-3 bg-slate-900/60 rounded-lg overflow-x-auto space-y-0.5 leading-relaxed">
"""
    for i, line in enumerate(p['stepper']['pseudocode_lines']):
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

    <!-- Section 5: Multi-Approach Solutions & Scratchpad -->
    <section id="section-5" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-8">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between border-b border-slate-800 pb-4 gap-4">
        <div>
          <span class="text-xs font-mono text-emerald-400 uppercase tracking-wider font-semibold">Production Implementations</span>
          <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">From Naive to Optimal &amp; Interactive Sandbox</h3>
          <p class="text-xs text-slate-400 mt-1">Strict ordering: Kotlin (Default) &rarr; Python &rarr; Java across all 3 approaches.</p>
        </div>

        <!-- Approach Tabs Selector -->
        <div class="flex items-center bg-slate-950 p-1 rounded-xl border border-slate-800 text-xs font-mono">
"""
    app_count = len(p['approaches'])
    for idx, app in enumerate(p['approaches']):
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
    # Render approaches
    for idx, app in enumerate(p['approaches']):
        app_num = idx + 1
        hidden_cls = "" if app_num == app_count else "hidden"
        name = app.get('name', f'Approach {app_num}')
        desc = app.get('desc') or app.get('summary') or ''
        
        time_val = app.get('time')
        space_val = app.get('space')
        if not time_val or not space_val:
            comp = app.get('complexity', 'O(N) Time, O(1) Space')
            parts = [pt.strip() for pt in comp.split(',')]
            time_val = parts[0] if len(parts) > 0 else 'O(N)'
            space_val = parts[1] if len(parts) > 1 else 'O(1)'

        code_k = (app.get('code_kotlin') or app.get('kotlin_code') or '').strip()
        code_p = (app.get('code_python') or app.get('python_code') or '').strip()
        code_j = (app.get('code_java') or app.get('java_code') or '').strip()

        mem_breakdown = app.get('memory_breakdown', [])
        if isinstance(mem_breakdown, str):
            default_notes = mem_breakdown
        elif isinstance(mem_breakdown, list):
            default_notes = " ".join([f"<strong>{m['component']}</strong> ({m['cost']}): {m['description']}" if isinstance(m, dict) else str(m) for m in mem_breakdown])
        else:
            default_notes = ""

        notes_k = app.get('notes_kotlin') or default_notes or "Idiomatic Kotlin implementation optimizing collections and null-safety."
        notes_p = app.get('notes_python') or default_notes or "Pythonic implementation using optimal data structures and built-ins."
        notes_j = app.get('notes_java') or default_notes or "Robust Java implementation avoiding unnecessary heap allocations."

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

        <!-- Language Selector for Approach {app_num} -->
        <div class="flex items-center gap-2 border-b border-slate-800 pb-2 text-xs font-mono">
          <button onclick="switchLangApp{app_num}('kotlin')" id="lang-btn-app{app_num}-kotlin" class="px-3 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold">Kotlin (Default)</button>
          <button onclick="switchLangApp{app_num}('python')" id="lang-btn-app{app_num}-python" class="px-3 py-1 rounded text-slate-400 hover:text-white">Python</button>
          <button onclick="switchLangApp{app_num}('java')" id="lang-btn-app{app_num}-java" class="px-3 py-1 rounded text-slate-400 hover:text-white">Java</button>
        </div>

        <!-- Code Block Kotlin -->
        <div id="code-app{app_num}-kotlin" class="space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-emerald-300 overflow-x-auto leading-relaxed">{html.escape(code_k)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-emerald-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Kotlin Runtime &amp; Memory Nuance:</span>
            <p>{notes_k}</p>
          </div>
        </div>

        <!-- Code Block Python -->
        <div id="code-app{app_num}-python" class="hidden space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-amber-300 overflow-x-auto leading-relaxed">{html.escape(code_p)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-amber-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Python Runtime &amp; Memory Nuance:</span>
            <p>{notes_p}</p>
          </div>
        </div>

        <!-- Code Block Java -->
        <div id="code-app{app_num}-java" class="hidden space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-cyan-300 overflow-x-auto leading-relaxed">{html.escape(code_j)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-cyan-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Java Runtime &amp; Memory Nuance:</span>
            <p>{notes_j}</p>
          </div>
        </div>
      </div>\n"""

    h += f"""      <!-- Interactive Code Scratchpad & Live Test Runner -->
      <div class="border-t border-slate-800 pt-6 space-y-4">
        <div class="flex flex-wrap items-center justify-between gap-3">
          <div class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-emerald-400"></span>
            <h4 class="text-sm font-bold text-white font-mono uppercase tracking-wider">In-Browser Code Scratchpad &amp; Test Suite</h4>
          </div>
          <div class="flex items-center gap-2 text-xs font-mono">
            <button onclick="setScratchpadLang('kotlin')" id="btn-sp-kotlin" class="px-2.5 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold">Kotlin</button>
            <button onclick="setScratchpadLang('python')" id="btn-sp-python" class="px-2.5 py-1 rounded text-slate-400 hover:text-white">Python</button>
            <button onclick="setScratchpadLang('java')" id="btn-sp-java" class="px-2.5 py-1 rounded text-slate-400 hover:text-white">Java</button>
          </div>
        </div>

        <div class="relative">
          <textarea id="scratchpad-editor" rows="9" class="w-full bg-slate-950 border border-slate-800 rounded-xl p-4 text-xs font-mono text-emerald-300 focus:outline-none focus:border-emerald-500 leading-relaxed custom-scrollbar spellcheck-false">{html.escape(p['scratchpad']['kotlin_boilerplate'].strip())}</textarea>
        </div>

        <div class="flex items-center justify-between">
          <button onclick="runScratchpad()" class="px-4 py-2 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs font-mono transition-colors shadow-lg shadow-emerald-500/20">
            &#9654; Run Test Cases
          </button>
          <span id="scratchpad-status" class="text-xs font-mono text-slate-500">Ready to execute official test cases.</span>
        </div>

        <div id="scratchpad-results" class="hidden p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono space-y-2">
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
        <!-- Phase 1: Clarifying Questions -->
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-cyan-400 uppercase tracking-wider block">Phase 1: Clarifying Questions &amp; Edge Cases</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">"{p['spoken_script']['phase1']}"</p>
        </div>

        <!-- Phase 2: Space-Time Pitch -->
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider block">Phase 2: Space-Time Strategy Pitch</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">"{p['spoken_script']['phase2']}"</p>
        </div>

        <!-- Phase 3: Live Coding Commentary -->
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-amber-400 uppercase tracking-wider block">Phase 3: Live Coding Narrative</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">"{p['spoken_script']['phase3']}"</p>
        </div>

        <!-- Phase 4: Edge-Case Dry Run -->
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-purple-400 uppercase tracking-wider block">Phase 4: Cold Edge-Case Verification</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">"{p['spoken_script']['phase4']}"</p>
        </div>
      </div>

      <!-- Senior Follow-Up Question -->
      <div class="p-4 rounded-xl bg-slate-950 border border-amber-900/40 text-xs text-slate-300 space-y-2">
        <span class="text-amber-400 font-bold font-mono uppercase tracking-wider block text-[11px]">&#9889; Senior Architectural &amp; Scalability Follow-Up:</span>
        <p class="leading-relaxed">{p['spoken_script']['senior_followup']}</p>
      </div>
    </section>

    <!-- Section 7: MCQs & 48h Cold Recall Timer -->
    <section id="section-7" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-purple-400 uppercase tracking-wider font-semibold">Active Recall &amp; Retention</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">Self-Assessment &amp; 48-Hour Cold Recall</h3>
      </div>

      <!-- 2 Multiple Choice Questions -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
"""
    for q_idx, q in enumerate(p['quiz']):
        q_num = q_idx + 1
        h += f"""        <!-- Question {q_num} -->
        <div class="p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <span class="text-xs font-mono text-slate-400 block font-bold">Question {q_num}: {q['question']}</span>
          <div class="space-y-1.5 text-xs font-mono">
"""
        for opt_idx, opt in enumerate(q['options']):
            opt_letter = chr(65 + opt_idx)
            h += f"""            <button onclick="checkMCQ({q_num}, '{opt_letter}')" id="mcq-{q_num}-{opt_letter}" class="w-full text-left p-2.5 rounded bg-slate-900 hover:bg-slate-800 text-slate-300 transition-colors border border-transparent">
              <span class="text-slate-500 font-bold mr-2">{opt_letter}.</span> {opt}
            </button>\n"""

        h += f"""          </div>
          <div id="mcq-feedback-{q_num}" class="hidden p-3 rounded-lg text-xs font-mono"></div>
        </div>\n"""

    h += f"""      </div>

      <!-- 48-Hour Spaced Repetition Countdown Card -->
      <div class="p-5 rounded-xl bg-slate-950 border border-slate-800 flex flex-col sm:flex-row items-center justify-between gap-4">
        <div>
          <span class="text-xs font-mono text-emerald-400 uppercase tracking-wider font-bold block">Spaced Repetition Protocol</span>
          <h4 class="text-sm font-bold text-white mt-0.5">48-Hour Cold Recall Checklist</h4>
          <p class="text-xs text-slate-400 mt-1">Can you solve this cold on paper without hints in 48 hours?</p>
        </div>
        <div class="flex items-center gap-3">
          <div class="text-right">
            <span class="text-[10px] font-mono text-slate-500 block uppercase">Next Check Due:</span>
            <span id="recall-countdown" class="text-xs font-mono font-bold text-emerald-400">48h 00m 00s</span>
          </div>
          <button onclick="resetRecallTimer()" class="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-xs font-mono text-slate-300 transition-colors">Reset Timer</button>
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

    {p['stepper']['js_code']}

    function renderStepper() {{
      if (steps.length === 0) return;
      const step = steps[currentStepIdx];
      
      document.getElementById('stepper-narration').textContent = step.narration;
      document.getElementById('stepper-counter').textContent = `Step ${{currentStepIdx + 1}} of ${{steps.length}}`;

      // Update metrics
      {p['stepper'].get('js_update_metrics', '')}

      // Render custom visual
      {p['stepper'].get('js_render_visual', 'if (typeof renderStage === "function") renderStage(step);')}

      // Synced pseudocode line highlighting
      const totalLines = {len(p['stepper']['pseudocode_lines'])};
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

      // Button states
      document.getElementById('btn-prev').disabled = (currentStepIdx === 0);
      document.getElementById('btn-next').disabled = (currentStepIdx === steps.length - 1);
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
      const total = {len(p['approaches'])};
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

    const switchLangApp1 = makeLangSwitcher(1);
    const switchLangApp2 = makeLangSwitcher(2);
    const switchLangApp3 = makeLangSwitcher(3);
    for (let a = 1; a <= {len(p['approaches'])}; a++) {{
      window[`switchLangApp${{a}}`] = makeLangSwitcher(a);
    }}

    // ---------------- SCRATCHPAD LOGIC ----------------
    const BOILERPLATES = {{
      kotlin: `{p['scratchpad']['kotlin_boilerplate'].replace('`', '\\`')}`,
      python: `{p['scratchpad']['python_boilerplate'].replace('`', '\\`')}`,
      java: `{p['scratchpad']['java_boilerplate'].replace('`', '\\`')}`
    }};

    let currentScratchLang = 'kotlin';

    function setScratchpadLang(lang) {{
      currentScratchLang = lang;
      ['kotlin', 'python', 'java'].forEach(l => {{
        const btn = document.getElementById(`btn-sp-${{l}}`);
        if (l === lang) {{
          btn.className = "px-2.5 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold";
        }} else {{
          btn.className = "px-2.5 py-1 rounded text-slate-400 hover:text-white";
        }}
      }});
      document.getElementById('scratchpad-editor').value = BOILERPLATES[lang];
    }}

    function runScratchpad() {{
      const code = document.getElementById('scratchpad-editor').value;
      const resultsEl = document.getElementById('scratchpad-results');
      const statusEl = document.getElementById('scratchpad-status');
      resultsEl.classList.remove('hidden');

      // Check basic balance
      let openBraces = (code.match(/\\{{/g) || []).length;
      let closeBraces = (code.match(/\\}}/g) || []).length;
      if (currentScratchLang !== 'python' && openBraces !== closeBraces) {{
        statusEl.textContent = "Syntax Error: Unbalanced curly braces.";
        statusEl.className = "text-xs font-mono text-rose-400 font-bold";
        resultsEl.innerHTML = `<span class="text-rose-400 font-bold">&#10008; Unbalanced curly braces: ${{openBraces}} open vs ${{closeBraces}} close.</span>`;
        return;
      }}

      {p['scratchpad']['js_test_runner']}
    }}

    // ---------------- MCQ LOGIC ----------------
    const QUIZ_DATA = [
      {{ correct: '{chr(65 + p['quiz'][0]['correct_index'])}', explanation: `{p['quiz'][0]['explanation'].replace('`', '\\`')}` }},
      {{ correct: '{chr(65 + p['quiz'][1]['correct_index'])}', explanation: `{p['quiz'][1]['explanation'].replace('`', '\\`')}` }}
    ];

    function checkMCQ(qNum, choice) {{
      const q = QUIZ_DATA[qNum - 1];
      const fb = document.getElementById(`mcq-feedback-${{qNum}}`);
      fb.classList.remove('hidden');

      ['A', 'B', 'C', 'D'].forEach(c => {{
        const btn = document.getElementById(`mcq-${{qNum}}-${{c}}`);
        if (btn) {{
          if (c === q.correct) {{
            btn.className = "w-full text-left p-2.5 rounded bg-emerald-950/60 border border-emerald-500/60 text-emerald-300 font-bold transition-colors";
          }} else if (c === choice && choice !== q.correct) {{
            btn.className = "w-full text-left p-2.5 rounded bg-rose-950/60 border border-rose-500/60 text-rose-300 transition-colors";
          }} else {{
            btn.className = "w-full text-left p-2.5 rounded bg-slate-900 opacity-50 text-slate-400 transition-colors border border-transparent";
          }}
        }}
      }});

      if (choice === q.correct) {{
        fb.className = "p-3 rounded-lg text-xs font-mono bg-emerald-950/40 border border-emerald-900/60 text-emerald-300";
        fb.innerHTML = `<strong>&#10004; Correct!</strong> ${{q.explanation}}`;
      }} else {{
        fb.className = "p-3 rounded-lg text-xs font-mono bg-rose-950/40 border border-rose-900/60 text-rose-300";
        fb.innerHTML = `<strong>&#10008; Incorrect.</strong> Choice ${{choice}} is not optimal. ${{q.explanation}}`;
      }}
    }}

    // ---------------- 48H RECALL TIMER ----------------
    const TIMER_KEY = `recall_timer_lesson_{num_str}`;

    function initRecallTimer() {{
      let targetTime = localStorage.getItem(TIMER_KEY);
      if (!targetTime) {{
        targetTime = Date.now() + 48 * 60 * 60 * 1000;
        localStorage.setItem(TIMER_KEY, targetTime);
      }}
      updateTimerDisplay(targetTime);
      setInterval(() => updateTimerDisplay(targetTime), 1000);
    }}

    function updateTimerDisplay(targetTime) {{
      const now = Date.now();
      const diff = Math.max(0, targetTime - now);
      const hours = Math.floor(diff / (1000 * 60 * 60));
      const mins = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      const secs = Math.floor((diff % (1000 * 60)) / 1000);
      const el = document.getElementById('recall-countdown');
      if (el) {{
        el.textContent = `${{hours}}h ${{mins}}m ${{secs}}s`;
        if (diff === 0) el.className = "text-xs font-mono font-bold text-rose-400 animate-pulse";
      }}
    }}

    function resetRecallTimer() {{
      const targetTime = Date.now() + 48 * 60 * 60 * 1000;
      localStorage.setItem(TIMER_KEY, targetTime);
      updateTimerDisplay(targetTime);
    }}

    // On Load Initialization
    window.addEventListener('DOMContentLoaded', () => {{
      initCanvas();
      buildSteps(initialData);
      renderStepper();
      initRecallTimer();
    }});
  </script>
</body>
</html>"""
    return h
