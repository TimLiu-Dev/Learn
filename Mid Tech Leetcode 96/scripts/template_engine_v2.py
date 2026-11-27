"""
Template Engine v2 (2027 126-Problem Edition) for Mid-Tech LeetCode Masterclass.
Renders self-contained Golden Master HTML lessons:
- Shared LessonRuntime.Whiteboard, PracticeArena (Cold Timer + Debug PR Mode), Quiz, SpacedRecall, & StudyNav
- Clean Section 2.5 Non-Code Blueprint & Indented Whiteboard Pseudocode
- Safe HTML generic escaping (<Int>, <Pair<K, V>> preserved cleanly)
- Clickable Sister Problem links & Category-Specific Primary Sources
- Mid-Level (SDE II / L4) Section 6 Spoken Script & Requirement-Pivot Follow-Ups
"""

import html
import re
import json
import sys
sys.path.append('scripts')
from lesson_builder import (
    CATEGORY_PRIMARY_SOURCES,
    sanitize_html_fragment,
    extract_method_name,
    build_stubs,
    build_solutions,
    build_hints,
    build_quiz,
    extract_test_cases,
    enrich_approaches,
    build_non_code_solution,
)

# Map LC# -> lesson filename across all 126 lessons for clickable Sister Problem links
LC_TO_LESSON_FILE = {
    217: "0001-lc217-contains-duplicate.html", 1: "0002-lc1-two-sum.html",
    1768: "0003-lc1768-merge-strings-alternately.html", 283: "0004-lc283-move-zeroes.html",
    125: "0005-lc125-valid-palindrome.html", 121: "0006-lc121-best-time-to-buy-and-sell-stock.html",
    643: "0007-lc643-maximum-average-subarray-i.html", 724: "0008-lc724-find-pivot-index.html",
    20: "0009-lc20-valid-parentheses.html", 933: "0010-lc933-number-of-recent-calls.html",
    206: "0011-lc206-reverse-linked-list.html", 21: "0012-lc21-merge-two-sorted-lists.html",
    141: "0013-lc141-linked-list-cycle.html", 160: "0014-lc160-intersection-of-two-linked-lists.html",
    704: "0015-lc704-binary-search.html", 226: "0016-lc226-invert-binary-tree.html",
    104: "0017-lc104-maximum-depth-of-binary-tree.html", 543: "0018-lc543-diameter-of-binary-tree.html",
    70: "0019-lc70-climbing-stairs.html", 136: "0020-lc136-single-number.html",
    191: "0021-lc191-number-of-1-bits.html", 338: "0022-lc338-counting-bits.html",
    49: "0023-lc49-group-anagrams.html", 443: "0024-lc443-string-compression.html",
    11: "0025-lc11-container-with-most-water.html", 3: "0026-lc3-longest-substring-without-repeating-characters.html",
    1004: "0027-lc1004-max-consecutive-ones-iii.html", 155: "0028-lc155-min-stack.html",
    735: "0029-lc735-asteroid-collision.html", 739: "0030-lc739-daily-temperatures.html",
    2043: "0031-lc2043-simple-bank-system.html", 362: "0032-lc362-design-hit-counter.html",
    1396: "0033-lc1396-design-underground-system.html", 19: "0034-lc19-remove-nth-node-from-end-of-list.html",
    138: "0035-lc138-copy-list-with-random-pointer.html", 74: "0036-lc74-search-a-2d-matrix.html",
    153: "0037-lc153-find-minimum-in-rotated-sorted-array.html", 162: "0038-lc162-find-peak-element.html",
    102: "0039-lc102-binary-tree-level-order-traversal.html", 199: "0040-lc199-binary-tree-right-side-view.html",
    1448: "0041-lc1448-count-good-nodes-in-binary-tree.html", 208: "0042-lc208-implement-trie-prefix-tree.html",
    215: "0043-lc215-kth-largest-element-in-an-array.html", 973: "0044-lc973-k-closest-points-to-origin.html",
    347: "0045-lc347-top-k-frequent-elements.html", 56: "0046-lc56-merge-intervals.html",
    57: "0047-lc57-insert-interval.html", 78: "0048-lc78-subsets.html",
    46: "0049-lc46-permutations.html", 54: "0050-lc54-spiral-matrix.html",
    200: "0051-lc200-number-of-islands.html", 994: "0052-lc994-rotting-oranges.html",
    841: "0053-lc841-keys-and-rooms.html", 547: "0054-lc547-number-of-provinces.html",
    198: "0055-lc198-house-robber.html", 62: "0056-lc62-unique-paths.html",
    322: "0057-lc322-coin-change.html", 238: "0058-lc238-product-of-array-except-self.html",
    128: "0059-lc128-longest-consecutive-sequence.html", 15: "0060-lc15-3sum.html",
    424: "0061-lc424-longest-repeating-character-replacement.html", 560: "0062-lc560-subarray-sum-equals-k.html",
    394: "0063-lc394-decode-string.html", 227: "0064-lc227-basic-calculator-ii.html",
    853: "0065-lc853-car-fleet.html", 981: "0066-lc981-time-based-key-value-store.html",
    380: "0068-lc380-insert-delete-getrandom-o1.html", 146: "0069-lc146-lru-cache.html",
    33: "0070-lc33-search-in-rotated-sorted-array.html", 875: "0071-lc875-koko-eating-bananas.html",
    98: "0072-lc98-validate-binary-search-tree.html", 236: "0073-lc236-lowest-common-ancestor-of-a-binary-tree.html",
    211: "0074-lc211-design-add-and-search-words-data-structure.html", 253: "0075-lc253-meeting-rooms-ii.html",
    435: "0076-lc435-non-overlapping-intervals.html", 452: "0077-lc452-minimum-number-of-arrows-to-burst-balloons.html",
    39: "0078-lc39-combination-sum.html", 22: "0079-lc22-generate-parentheses.html",
    417: "0080-lc417-pacific-atlantic-water-flow.html", 130: "0081-lc130-surrounded-regions.html",
    133: "0082-lc133-clone-graph.html", 207: "0083-lc207-course-schedule.html",
    210: "0084-lc210-course-schedule-ii.html", 684: "0085-lc684-redundant-connection.html",
    743: "0086-lc743-network-delay-time.html", 139: "0087-lc139-word-break.html",
    300: "0088-lc300-longest-increasing-subsequence.html", 1143: "0089-lc1143-longest-common-subsequence.html",
    42: "0090-lc42-trapping-rain-water.html", 76: "0091-lc76-minimum-window-substring.html",
    239: "0092-lc239-sliding-window-maximum.html", 588: "0093-lc588-design-in-memory-file-system.html",
    23: "0094-lc23-merge-k-sorted-lists.html", 124: "0095-lc124-binary-tree-maximum-path-sum.html",
    295: "0096-lc295-find-median-from-data-stream.html", 88: "0097-lc88-merge-sorted-array.html",
    359: "0098-lc359-logger-rate-limiter.html", 53: "0099-lc53-maximum-subarray.html",
    48: "0100-lc48-rotate-image.html", 79: "0101-lc79-word-search.html",
    399: "0102-lc399-evaluate-division.html", 5: "0103-lc5-longest-palindromic-substring.html",
    271: "0104-lc271-encode-and-decode-strings.html", 230: "0105-lc230-kth-smallest-element-in-a-bst.html",
    105: "0106-lc105-construct-binary-tree-from-preorder-and-inorder-traversal.html",
    621: "0107-lc621-task-scheduler.html", 55: "0108-lc55-jump-game.html",
    416: "0109-lc416-partition-equal-subset-sum.html", 91: "0110-lc91-decode-ways.html",
    528: "0111-lc528-random-pick-with-weight.html",
    34: "0112-lc34-find-first-and-last-position-of-element-in-sorted-array.html",
    2: "0113-lc2-add-two-numbers.html", 143: "0114-lc143-reorder-list.html",
    152: "0115-lc152-maximum-product-subarray.html", 1091: "0116-lc1091-shortest-path-in-binary-matrix.html",
    242: "0117-lc242-valid-anagram.html", 252: "0118-lc252-meeting-rooms.html",
    733: "0119-lc733-flood-fill.html", 703: "0120-lc703-kth-largest-element-in-a-stream.html",
    40: "0121-lc40-combination-sum-ii.html", 17: "0122-lc17-letter-combinations-of-a-phone-number.html",
    721: "0123-lc721-accounts-merge.html", 36: "0124-lc36-valid-sudoku.html",
    692: "0125-lc692-top-k-frequent-words.html",
}


def render_sister_html(sis):
    name = sanitize_html_fragment(sis.get('name', ''))
    lc_str = str(sis.get('lc', ''))
    m_num = re.search(r'(\d+)', lc_str)
    if sis.get('file'):
        return f'<a href="{sis["file"]}" class="flex items-center justify-between hover:text-emerald-400 underline decoration-slate-700 transition-colors"><span>{name}</span><span class="text-emerald-400">{html.escape(lc_str)} ↗</span></a>'
    if m_num and int(m_num.group(1)) in LC_TO_LESSON_FILE:
        f = LC_TO_LESSON_FILE[int(m_num.group(1))]
        return f'<a href="{f}" class="flex items-center justify-between hover:text-emerald-400 underline decoration-slate-700 transition-colors"><span>{name}</span><span class="text-emerald-400">#{m_num.group(1)} ↗</span></a>'
    return f'<a href="../reference/pattern-reference.html" class="flex items-center justify-between hover:text-emerald-400 underline decoration-slate-700 transition-colors"><span>{name}</span><span class="text-slate-400">{html.escape(lc_str)}</span></a>'


def render_lesson_v2(p):
    num_str = f"{p['num']:04d}"
    lc_id = p['lc']
    title = p['title']
    category = p['category']
    diff = p['difficulty']
    pass_label = p.get('pass_label', 'Pass 2A')
    prev_file = p.get('prev_file', '../master-plan.html')
    next_file = p.get('next_file', '../master-plan.html')
    if prev_file == 'master-plan.html':
        prev_file = '../master-plan.html'
    if next_file == 'master-plan.html':
        next_file = '../master-plan.html'
    prev_num = p['num'] - 1
    next_num = p['num'] + 1
    lc_slug = p.get('lc_slug', title.lower().replace(' ', '-').replace("'", ""))
    leetcode_only = {"maximum-average-subarray-i", "number-of-recent-calls", "simple-bank-system", "keys-and-rooms", "flood-fill", "top-k-frequent-words"}
    if p.get('nc_url'):
        lc_url = p['nc_url']
    elif lc_slug in leetcode_only:
        lc_url = f"https://leetcode.com/problems/{lc_slug}/"
    else:
        nc_slug = "design-in-memory-file-system" if str(lc_id).lower() in ("custom", "ica") else lc_slug
        lc_url = f"https://neetcode.io/problems/{nc_slug}/question?list=allNC"
    prev_label = "Curriculum Hub" if prev_num < 1 else f"Prev #{prev_num}"
    next_label = "Curriculum Hub" if next_file == '../master-plan.html' else f"Next #{next_num}"

    method_name = p.get('method_name') or extract_method_name(p)
    stubs = p.get('starter_stubs') or build_stubs(p, method_name)
    solutions = build_solutions(p)
    hints = build_hints(p)
    quiz_questions = build_quiz(p)
    test_cases = extract_test_cases(p)
    approaches = enrich_approaches(p)
    non_code = build_non_code_solution(p)

    src_url, src_title, src_book = CATEGORY_PRIMARY_SOURCES.get(
        category,
        ("https://kotlinlang.org/docs/collections-overview.html", "Kotlin Collections Reference", "CLRS 4th Edition")
    )

    h = f"""<!DOCTYPE html>
<html lang="en" class="dark">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Lesson {p['num']}: {html.escape(title)} (LeetCode #{lc_id}) | Mid-Tech 126 Masterclass</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="../assets/lesson-theme.css">
  <script>
    tailwind.config = {{
      darkMode: 'class',
      theme: {{
        extend: {{
          colors: {{
            slate: {{ 950: '#020617', 900: '#0f172a', 850: '#111c38', 800: '#1e293b', 700: '#334155' }},
            emerald: {{ 400: '#34d399', 500: '#10b981', 950: '#022c22' }},
            amber: {{ 400: '#fbbf24', 500: '#f59e0b', 950: '#451a03' }},
            cyan: {{ 400: '#22d3ee', 500: '#06b6d4', 950: '#083344' }}
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
        <a href="../master-plan.html" class="w-8 h-8 rounded-lg bg-gradient-to-tr from-emerald-500 to-cyan-500 text-slate-950 font-black text-xs flex items-center justify-center shadow-sm hover:scale-105 transition-transform" title="Back to Master Plan">
          126
        </a>
        <div class="flex items-center gap-2">
          <span class="px-2.5 py-1 rounded-md text-xs font-mono font-bold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">Lesson {num_str}</span>
          <span class="px-2 py-0.5 rounded text-[11px] font-mono font-semibold bg-amber-950/60 text-amber-300 border border-amber-800/60">{html.escape(pass_label)}</span>
          <span class="px-2 py-0.5 rounded text-[11px] font-bold bg-slate-800 text-slate-200 border border-slate-700">{html.escape(diff)}</span>
        </div>
        <h1 class="text-sm font-bold text-white hidden md:inline truncate max-w-md">{html.escape(title)} (#{lc_id})</h1>
      </div>
      <div class="flex items-center gap-2">
        <a href="{prev_file}" class="px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 hover:border-slate-700 text-xs font-mono text-slate-300 hover:text-white transition-colors flex items-center gap-1.5" title="Previous Sequential File">
          <span>&larr;</span> <span class="hidden sm:inline">{prev_label}</span>
        </a>
        <a href="../master-plan.html" class="px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 hover:border-slate-700 text-xs font-mono text-slate-300 hover:text-white transition-colors hidden lg:flex items-center gap-1">
          <span>Master Plan</span>
        </a>
        <a href="{next_file}" class="px-3 py-1.5 rounded-lg bg-emerald-500 text-slate-950 font-bold text-xs font-mono hover:bg-emerald-400 transition-colors flex items-center gap-1.5" title="Next Sequential File">
          <span class="hidden sm:inline">{next_label}</span> <span>&rarr;</span>
        </a>
      </div>
    </div>
    <!-- Quick-Jump Anchor Bar -->
    <div class="border-t border-slate-800/80 mt-2.5 pt-2 overflow-x-auto custom-scrollbar">
      <div class="max-w-7xl mx-auto flex items-center gap-4 text-xs font-mono text-slate-400 whitespace-nowrap">
        <span class="text-slate-500 font-bold uppercase tracking-wider text-[10px]">Jump to:</span>
        <a href="#section-0" class="hover:text-emerald-400 transition-colors font-bold text-slate-300">0. Problem</a>
        <a href="#sketchpad-section" class="hover:text-teal-400 transition-colors font-bold text-teal-300">✏️ Whiteboard</a>
        <a href="#section-1" class="hover:text-emerald-400 transition-colors">1. Mental Model</a>
        <a href="#section-2" class="hover:text-emerald-400 transition-colors">2. Naive vs Optimal</a>
        <a href="#section-2-5" class="hover:text-amber-300 transition-colors font-bold text-amber-400">2.5 Solution &amp; Pseudocode</a>
        <a href="#section-3" class="hover:text-emerald-400 transition-colors">3. Trace Table</a>
        <a href="#section-4" class="hover:text-emerald-400 transition-colors">4. Stepper</a>
        <a href="#section-5" class="hover:text-emerald-400 transition-colors font-bold text-indigo-400">5. Code &amp; Practice</a>
        <a href="#section-6" class="hover:text-emerald-400 transition-colors">6. L4 Script</a>
        <a href="#section-7" class="hover:text-emerald-400 transition-colors">7. Quiz &amp; Recall</a>
      </div>
    </div>
  </header>

  <main class="max-w-7xl mx-auto px-4 py-8 space-y-12">

    <!-- Section 0: Problem Identity & Verification Matrix -->
    <section id="section-0" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="flex flex-wrap items-center justify-between gap-4 border-b border-slate-800 pb-5">
        <div>
          <div class="flex items-center gap-2 text-xs font-mono text-amber-400 font-semibold mb-1">
            <span>{html.escape(pass_label.upper())} &bull; LESSON {p['num']} OF 126</span>
            <span>&bull;</span>
            <span class="text-slate-400">CATEGORY: {html.escape(category)}</span>
          </div>
          <h2 class="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">Problem #{lc_id}: {html.escape(title)}</h2>
        </div>
        <div class="flex items-center gap-3">
          <a href="{lc_url}" target="_blank" rel="noopener noreferrer" class="px-3.5 py-1.5 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-slate-950 text-xs font-mono font-bold transition-colors flex items-center gap-1.5 shadow-sm">
            <span>External Judge ↗</span>
          </a>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="space-y-3">
          <h3 class="text-xs font-bold uppercase tracking-wider text-slate-400 font-mono">Official Problem Statement</h3>
          <div class="bg-slate-950/80 rounded-xl p-4 border border-slate-800/80 text-sm text-slate-300 space-y-3 leading-relaxed font-mono">
            {sanitize_html_fragment(p['verbatim_statement'])}
            <div class="border-t border-slate-800/80 pt-3 text-xs text-slate-400 space-y-1">
"""
    for ex in p.get('examples', []):
        h += f"""              <p><strong class="text-slate-300">{html.escape(ex.get('title', 'Example'))}:</strong> <code>{html.escape(ex.get('input', ''))}</code> &rarr; Output: <code>{html.escape(ex.get('output', ''))}</code></p>\n"""

    h += f"""            </div>
          </div>
        </div>

        <div class="space-y-3">
          <h3 class="text-xs font-bold uppercase tracking-wider text-emerald-400 font-mono">Plain-English Translation &amp; Core Nuance</h3>
          <div class="bg-emerald-950/20 rounded-xl p-4 border border-emerald-900/40 text-sm text-slate-200 space-y-3 leading-relaxed">
            <p>{sanitize_html_fragment(p.get('plain_english', ''))}</p>
            <div class="border-t border-emerald-900/30 pt-3 text-xs text-emerald-300/80 space-y-1">
              <span class="font-bold uppercase tracking-wider text-emerald-400 block font-mono">Official Constraints:</span>
              <ul class="list-disc list-inside space-y-0.5 text-slate-300">
"""
    for c in p.get('constraints', []):
        h += f"""                <li><code>{html.escape(c)}</code></li>\n"""

    bc = p.get('boundary_checks', {})
    h += f"""              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="space-y-3 pt-2">
        <h3 class="text-xs font-bold uppercase tracking-wider text-slate-400 font-mono">Canonical Input Shapes (Mental Boundary Checks)</h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs font-mono">
          <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
            <div class="text-emerald-400 font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-emerald-400"></span> Minimum Base Case
            </div>
            <div class="text-slate-300">{sanitize_html_fragment(bc.get('min_base', 'Empty or single item input'))}</div>
          </div>
          <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
            <div class="text-cyan-400 font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-cyan-400"></span> Best-Case Early Exit
            </div>
            <div class="text-slate-300">{sanitize_html_fragment(bc.get('best_exit', 'Target matched on index 0'))}</div>
          </div>
          <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800 space-y-1.5">
            <div class="text-amber-400 font-bold flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-amber-400"></span> Worst-Case Full Scan
            </div>
            <div class="text-slate-300">{sanitize_html_fragment(bc.get('worst_scan', 'Maximum constraint traversal'))}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Shared Vector Whiteboard (via LessonRuntime.Whiteboard) -->
    <section id="sketchpad-section" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 sm:p-6 space-y-4 shadow-xl">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
        <div class="flex items-center gap-3">
          <span class="w-8 h-8 rounded-lg bg-teal-500 text-slate-950 flex items-center justify-center font-bold font-mono text-sm">✏️</span>
          <div>
            <span class="text-xs font-mono font-semibold text-teal-400 uppercase tracking-wider block">Visual Brainstorming Arena</span>
            <h2 class="text-lg font-bold text-white">Interactive Whiteboard &amp; Interview Sketchpad</h2>
          </div>
        </div>
        <button onclick="wbToggle()" id="wb-toggle-btn" class="self-start sm:self-auto px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-mono font-semibold transition-colors flex items-center gap-1.5">
          <span id="wb-toggle-icon">▲</span>
          <span id="wb-toggle-text">Minimize Board</span>
        </button>
      </div>
      <div id="wb-body" class="space-y-3">
        <div class="flex flex-wrap items-center justify-between gap-3 p-2.5 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono">
          <div class="flex items-center gap-1.5">
            <span class="text-slate-500 text-[11px] mr-1">Color:</span>
            <button onclick="wbSetColor('#10b981')" class="wb-color-btn w-6 h-6 rounded-full bg-emerald-500 ring-2 ring-white ring-offset-2 ring-offset-slate-950 transition-all" data-color="#10b981"></button>
            <button onclick="wbSetColor('#06b6d4')" class="wb-color-btn w-6 h-6 rounded-full bg-cyan-500 hover:scale-110 transition-all" data-color="#06b6d4"></button>
            <button onclick="wbSetColor('#f59e0b')" class="wb-color-btn w-6 h-6 rounded-full bg-amber-500 hover:scale-110 transition-all" data-color="#f59e0b"></button>
            <button onclick="wbSetColor('#a855f7')" class="wb-color-btn w-6 h-6 rounded-full bg-purple-500 hover:scale-110 transition-all" data-color="#a855f7"></button>
            <button onclick="wbSetColor('#ffffff')" class="wb-color-btn w-6 h-6 rounded-full bg-white hover:scale-110 transition-all" data-color="#ffffff"></button>
            <button onclick="wbSetColor('#ef4444')" class="wb-color-btn w-6 h-6 rounded-full bg-red-500 hover:scale-110 transition-all" data-color="#ef4444"></button>
          </div>
          <div class="flex items-center gap-2">
            <div class="flex items-center bg-slate-900 rounded-lg p-0.5 border border-slate-800">
              <button onclick="wbSetMode('draw')" id="wb-tool-pen" class="px-2.5 py-1 rounded bg-teal-500/20 text-teal-300 font-bold transition-colors">Pen</button>
              <button onclick="wbSetMode('erase')" id="wb-tool-eraser" class="px-2.5 py-1 rounded text-slate-400 hover:text-white transition-colors">Eraser</button>
            </div>
            <button onclick="wbSetSize(2)" id="wb-size-2" class="wb-size-btn px-2 py-0.5 rounded bg-slate-900 border border-slate-800 text-[11px] text-slate-400">2px</button>
            <button onclick="wbSetSize(4)" id="wb-size-4" class="wb-size-btn px-2 py-0.5 rounded bg-teal-500/20 border border-teal-500/40 text-[11px] text-teal-300 font-bold">4px</button>
            <button onclick="wbSetSize(8)" id="wb-size-8" class="wb-size-btn px-2 py-0.5 rounded bg-slate-900 border border-slate-800 text-[11px] text-slate-400">8px</button>
          </div>
          <div class="flex items-center gap-2">
            <button onclick="wbUndo()" class="px-2.5 py-1 rounded bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-300">↩ Undo</button>
            <button onclick="wbClear()" class="px-2.5 py-1 rounded bg-red-950/40 hover:bg-red-900/40 border border-red-800/60 text-red-300">🗑 Clear</button>
          </div>
        </div>
        <div class="relative rounded-xl border border-slate-800 bg-slate-950 overflow-hidden shadow-inner cursor-crosshair">
          <canvas id="whiteboard-canvas" class="w-full h-72 block whiteboard-active-canvas"></canvas>
          <div id="wb-hint" class="absolute bottom-2.5 right-3 text-[10px] font-mono text-slate-600 pointer-events-none select-none">
            💡 Sketch state diagrams, index pointers, and boundary cases here
          </div>
        </div>
      </div>
    </section>

    <!-- Section 1: Physical Mental Model & Pattern Triggers -->
    <section id="section-1" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-emerald-400 uppercase tracking-wider font-semibold">Section 01 &bull; Cognitive Anchor</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">The Mental Model &amp; "Explain Like I'm 12"</h3>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center gap-2">
            <span class="text-base font-bold text-amber-400">💡 {sanitize_html_fragment(p.get('mental_model', {}).get('analogy_title', 'Mental Concept'))}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {sanitize_html_fragment(p.get('mental_model', {}).get('analogy_text', ''))}
          </p>
          <div class="p-3.5 rounded-lg bg-emerald-950/20 border border-emerald-900/40 text-xs text-emerald-300 space-y-1">
            <strong class="font-bold text-emerald-400 block font-mono">The Mental "A-Ha!" Switch:</strong>
            <p>{sanitize_html_fragment(p.get('mental_model', {}).get('aha_moment', ''))}</p>
          </div>
        </div>

        <div class="p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-4 text-xs font-mono">
          <div>
            <span class="text-slate-400 block uppercase font-bold text-[10px] tracking-wider">Pattern Trigger:</span>
            <p class="text-amber-300 mt-1">{sanitize_html_fragment(p.get('mental_model', {}).get('trigger', ''))}</p>
          </div>
          <div class="border-t border-slate-800 pt-3">
            <span class="text-slate-400 block uppercase font-bold text-[10px] tracking-wider mb-1.5">Connected Sister Lessons:</span>
            <div class="space-y-1.5 text-slate-300">
"""
    for sis in p.get('mental_model', {}).get('sisters', []):
        h += f"              {render_sister_html(sis)}\n"

    nv = p.get('naive_vs_optimal', {})
    h += f"""            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Section 2: Naive Trap vs Optimal Breakthrough -->
    <section id="section-2" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-rose-400 uppercase tracking-wider font-semibold">Section 02 &bull; Algorithmic Trade-off Analysis</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">The Naive Trap (Red) vs. Optimal Breakthrough (Green)</h3>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="p-5 rounded-xl bg-rose-950/20 border border-rose-900/40 space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs font-mono font-bold text-rose-400 uppercase tracking-wider flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-rose-500"></span> The Naive Trap
            </span>
            <span class="px-2 py-0.5 rounded text-[10px] font-mono bg-rose-900/40 text-rose-300">{sanitize_html_fragment(nv.get('naive_complexity', 'O(N²)'))}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {sanitize_html_fragment(nv.get('naive_text', ''))}
          </p>
          <div class="p-3 rounded-lg bg-rose-950/40 border border-rose-900/50 text-xs text-rose-300 font-mono">
            <strong>Wasted Work:</strong> {sanitize_html_fragment(nv.get('wasted_work', ''))}
          </div>
        </div>

        <div class="p-5 rounded-xl bg-emerald-950/20 border border-emerald-900/40 space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full bg-emerald-500"></span> The Optimal Breakthrough
            </span>
            <span class="px-2 py-0.5 rounded text-[10px] font-mono bg-emerald-900/40 text-emerald-300">{sanitize_html_fragment(nv.get('optimal_complexity', 'O(N)'))}</span>
          </div>
          <p class="text-sm text-slate-300 leading-relaxed">
            {sanitize_html_fragment(nv.get('optimal_text', ''))}
          </p>
          <div class="p-3 rounded-lg bg-emerald-950/40 border border-emerald-900/50 text-xs text-emerald-300 font-mono">
            <strong>Core Invariant:</strong> {sanitize_html_fragment(nv.get('core_invariant', ''))}
          </div>
        </div>
      </div>

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
        rname = row.get('name', '')
        badge_cls = "text-rose-400 font-bold" if ("Brute" in rname or "Naive" in rname or "Trap" in rname) else ("text-amber-400 font-bold" if "Better" in rname else "text-emerald-400 font-bold")
        h += f"""            <tr>
              <td class="p-3 {badge_cls}">{sanitize_html_fragment(rname)}</td>
              <td class="p-3 text-slate-200">{sanitize_html_fragment(row.get('time', ''))}</td>
              <td class="p-3 text-slate-200">{sanitize_html_fragment(row.get('space', ''))}</td>
              <td class="p-3 text-slate-400">{sanitize_html_fragment(row.get('note', ''))}</td>
            </tr>\n"""

    tt = p.get('trace_table', {})
    h += f"""          </tbody>
        </table>
      </div>
    </section>

    <!-- Section 2.5: Non-Code Solution Blueprint & Language-Agnostic Pseudocode -->
    <section id="section-2-5" class="rounded-2xl border border-amber-500/30 bg-slate-900/70 p-6 sm:p-8 space-y-6 shadow-xl">
      <div class="border-b border-slate-800 pb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <span class="text-xs font-mono text-amber-400 uppercase tracking-wider font-semibold">Section 2.5 &bull; Pre-Trace Algorithm Blueprint</span>
          <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">Non-Code Solution Explanation &amp; Whiteboard Pseudocode</h3>
          <p class="text-xs text-slate-400 mt-1">Internalize the state transitions and indented logic before reading syntax or writing code.</p>
        </div>
        <span class="px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs font-mono font-bold">
          Language-Agnostic Blueprint
        </span>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center gap-2 text-xs font-mono font-bold text-cyan-400 uppercase tracking-wider">
            <span class="w-2 h-2 rounded-full bg-cyan-400"></span>
            1. State &amp; Data Structure Roles
          </div>
          <ul class="space-y-2.5 text-xs text-slate-300">
"""
    for st_name, st_role in non_code.get('state_setup', []):
        h += f"""            <li class="p-2.5 rounded-lg bg-slate-900/80 border border-slate-800/80">
              <strong class="text-emerald-400 font-mono block mb-0.5">{sanitize_html_fragment(st_name)}</strong>
              <span class="text-slate-300 leading-relaxed">{sanitize_html_fragment(st_role)}</span>
            </li>\n"""

    h += f"""          </ul>
        </div>

        <div class="lg:col-span-2 p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center gap-2 text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider">
            <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
            2. Step-by-Step Non-Code Algorithm Walkthrough
          </div>
          <div class="space-y-3">
"""
    for step_title, step_body in non_code.get('walkthrough_steps', []):
        h += f"""            <div class="p-3.5 rounded-xl bg-slate-900/70 border border-slate-800/80 space-y-1">
              <div class="text-xs font-mono font-bold text-amber-300">{sanitize_html_fragment(step_title)}</div>
              <p class="text-xs sm:text-sm text-slate-300 leading-relaxed">{sanitize_html_fragment(step_body)}</p>
            </div>\n"""

    h += f"""          </div>
        </div>
      </div>

      <div class="p-5 rounded-xl bg-slate-950 border border-amber-500/30 space-y-3">
        <div class="flex items-center justify-between">
          <span class="text-xs font-mono font-bold text-amber-400 uppercase tracking-wider">3. Clean Whiteboard Pseudocode (Syntax-Free)</span>
          <span class="text-[11px] font-mono text-slate-500">Direct blueprint for Trace Table &amp; Code</span>
        </div>
        <pre class="p-4 rounded-xl bg-slate-900/90 border border-slate-800 text-xs font-mono text-emerald-300 overflow-x-auto leading-relaxed">"""
    for idx_p, pline in enumerate(non_code.get('pseudocode_lines', []), 1):
        h += f"""<span class="text-slate-600 select-none mr-3">{idx_p:02d}</span>{html.escape(pline)}\n"""
    h += f"""</pre>
        <p class="text-xs text-slate-400 leading-relaxed">{sanitize_html_fragment(non_code.get('bridge_note', ''))}</p>
      </div>
    </section>

    <!-- Section 3: Concrete Step-by-Step Trace Table -->
    <section id="section-3" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-cyan-400 uppercase tracking-wider font-semibold">Section 03 &bull; Concrete Execution Trace</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">The Concrete Variable Trace Table</h3>
        <p class="text-xs text-slate-400 font-mono mt-1">Trace input: {sanitize_html_fragment(tt.get('input_desc', ''))}</p>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs font-mono border border-slate-800 rounded-xl overflow-hidden">
          <thead class="bg-slate-950 text-slate-400 uppercase text-[10px] border-b border-slate-800">
            <tr>
"""
    for col in tt.get('columns', []):
        h += f"""              <th class="p-3">{sanitize_html_fragment(col)}</th>\n"""
    h += f"""            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800/60 bg-slate-900/30 text-slate-300">
"""
    for row in tt.get('rows', []):
        h += "            <tr>\n"
        for i, val in enumerate(row):
            cls = "p-3 font-semibold text-white" if i == 0 else "p-3"
            h += f"""              <td class="{cls}">{sanitize_html_fragment(val)}</td>\n"""
        h += "            </tr>\n"

    st = p.get('stepper', {})
    h += f"""          </tbody>
        </table>
      </div>
      <p class="text-xs text-slate-400 leading-relaxed">{sanitize_html_fragment(tt.get('takeaway', ''))}</p>
    </section>

    <!-- Section 4: Interactive Visual Stepper -->
    <section id="section-4" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div>
          <span class="text-xs font-mono text-cyan-400 uppercase tracking-wider font-semibold">Section 04 &bull; Interactive Visualizer</span>
          <h3 class="text-xl sm:text-2xl font-bold text-white">{html.escape(st.get('title', 'State Machine & Memory Trace'))}</h3>
        </div>
        <div class="flex flex-wrap items-center gap-2">
"""
    for pr in st.get('presets', []):
        h += f"""          <button onclick="{html.escape(pr.get('onclick', ''), quote=True)}" class="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-xs font-mono text-slate-300">{html.escape(pr.get('label', ''))}</button>\n"""

    h += f"""        </div>
      </div>

      <div class="flex items-center gap-3 p-3 rounded-xl bg-slate-950 border border-slate-800">
        <label for="custom-input" class="text-xs font-mono text-slate-400 whitespace-nowrap">Input:</label>
        <input id="custom-input" type="text" value="{html.escape(st.get('default_input', ''), quote=True)}" class="flex-1 bg-slate-900 border border-slate-700 rounded-lg px-3 py-1 text-xs font-mono text-white focus:outline-none focus:border-emerald-500">
        <button onclick="handleCustomInput()" class="px-3 py-1 rounded bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs font-mono transition-colors">Apply</button>
      </div>

      <div class="p-6 rounded-xl bg-slate-950 border border-slate-800 space-y-6">
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs font-mono">
"""
    for m in st.get('metrics', []):
        h += f"""          <div class="p-3 rounded-xl bg-slate-900 border border-slate-800">
            <span class="text-[10px] text-slate-500 uppercase block">{html.escape(m.get('label', ''))}</span>
            <span id="{m.get('id', '')}" class="text-sm font-bold {m.get('color', '')}">{html.escape(str(m.get('initial', '')))}</span>
          </div>\n"""

    h += f"""        </div>

        <div id="stepper-visual-container" class="min-h-[160px] flex flex-col justify-center items-center py-6 border border-slate-800/80 rounded-xl bg-slate-900/40 p-4 overflow-x-auto">
          {st.get('visual_html', '')}
        </div>

        <div class="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">
          <div class="flex items-center gap-2 text-xs font-mono text-slate-300">
            <span class="w-2 h-2 rounded-full bg-cyan-400"></span>
            <span id="stepper-narration" class="leading-relaxed">{html.escape(st.get('initial_narration', ''))}</span>
          </div>
          <span id="stepper-counter" class="text-xs font-mono text-slate-500 whitespace-nowrap">Step 1</span>
        </div>
      </div>

      <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
        <span class="text-xs font-mono text-slate-400 uppercase font-bold tracking-wider">Synced Pseudocode Line Tracking</span>
        <pre class="text-xs font-mono p-3 bg-slate-900/60 rounded-lg overflow-x-auto space-y-0.5 leading-relaxed">
"""
    for i, line in enumerate(st.get('pseudocode_lines', [])):
        line_num = i + 1
        h += f"""<div id="code-line-{line_num}" class="px-2 py-0.5 rounded text-slate-400"><span class="text-slate-600 select-none mr-3">{line_num:02d}</span>{html.escape(line)}</div>\n"""

    h += f"""</pre>
      </div>

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
          <span class="text-xs font-mono text-emerald-400 uppercase tracking-wider font-semibold">Section 05 &bull; Production Implementations</span>
          <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">From Naive to Optimal &amp; Runnable Practice Arena</h3>
          <p class="text-xs text-slate-400 mt-1">Strict ordering: Kotlin (Default) &rarr; Python &rarr; Java across all approaches.</p>
        </div>

        <div class="flex flex-wrap items-center bg-slate-950 p-1 rounded-xl border border-slate-800 text-xs font-mono gap-1">
"""
    app_count = len(approaches)
    for idx, app in enumerate(approaches):
        app_num = idx + 1
        is_last = (app_num == app_count)
        name = app.get('name', f'Approach {app_num}')
        if is_last:
            h += f"""          <button onclick="switchApproach({app_num})" id="tab-app-{app_num}" class="px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold transition-all">&#9733; {app_num}. {html.escape(name)}</button>\n"""
        else:
            h += f"""          <button onclick="switchApproach({app_num})" id="tab-app-{app_num}" class="px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all">{app_num}. {html.escape(name)}</button>\n"""

    h += """        </div>
      </div>
"""
    for idx, app in enumerate(approaches):
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

        h += f"""      <div id="approach-{app_num}" class="{hidden_cls} space-y-6">
        <div class="flex flex-wrap items-center justify-between gap-3 p-4 rounded-xl bg-slate-950 border border-slate-800">
          <div>
            <h4 class="text-sm font-bold text-white font-mono">{html.escape(name)}</h4>
            <p class="text-xs text-slate-400 mt-0.5">{sanitize_html_fragment(desc)}</p>
          </div>
          <div class="flex items-center gap-3 text-xs font-mono">
            <span class="px-2.5 py-1 rounded bg-slate-900 border border-slate-700 text-slate-300">Time: <strong class="text-emerald-400">{html.escape(time_val)}</strong></span>
            <span class="px-2.5 py-1 rounded bg-slate-900 border border-slate-700 text-slate-300">Space: <strong class="text-cyan-400">{html.escape(space_val)}</strong></span>
          </div>
        </div>

        <div class="flex items-center gap-2 border-b border-slate-800 pb-2 text-xs font-mono">
          <button onclick="switchLangApp({app_num}, 'kotlin')" id="lang-btn-app{app_num}-kotlin" class="px-3 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold">Kotlin (Default)</button>
          <button onclick="switchLangApp({app_num}, 'python')" id="lang-btn-app{app_num}-python" class="px-3 py-1 rounded text-slate-400 hover:text-white">Python</button>
          <button onclick="switchLangApp({app_num}, 'java')" id="lang-btn-app{app_num}-java" class="px-3 py-1 rounded text-slate-400 hover:text-white">Java</button>
        </div>

        <div id="code-app{app_num}-kotlin" class="space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-emerald-300 overflow-x-auto leading-relaxed">{html.escape(code_k)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-emerald-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Kotlin Runtime &amp; Memory Nuance:</span>
            <p>{app['notes_kotlin']}</p>
          </div>
        </div>

        <div id="code-app{app_num}-python" class="hidden space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-amber-300 overflow-x-auto leading-relaxed">{html.escape(code_p)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-amber-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Python Runtime &amp; Memory Nuance:</span>
            <p>{app['notes_python']}</p>
          </div>
        </div>

        <div id="code-app{app_num}-java" class="hidden space-y-3">
          <pre class="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-cyan-300 overflow-x-auto leading-relaxed">{html.escape(code_j)}</pre>
          <div class="p-3.5 rounded-lg bg-slate-950/80 border border-slate-800 text-xs text-slate-300 space-y-1">
            <span class="text-cyan-400 font-bold font-mono block text-[11px] uppercase tracking-wider">Java Runtime &amp; Memory Nuance:</span>
            <p>{app['notes_java']}</p>
          </div>
        </div>
      </div>\n"""

    sp_script = p.get('spoken_script', {})
    followup_text = sp_script.get('mid_followup') or sp_script.get('senior_followup') or ''

    h += f"""
      <!-- GOLDEN MASTER RUNNABLE PRACTICE ARENA -->
      <div class="rounded-2xl border border-indigo-500/30 bg-slate-900/80 p-6 sm:p-8 space-y-5 shadow-2xl">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-4">
          <div>
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
              <h4 class="text-sm font-bold text-white font-mono uppercase tracking-wider">Interactive Runnable Practice Arena</h4>
            </div>
            <p class="text-xs text-slate-400 mt-1">Solve cold from the empty stub (or click 🐛 Debug Broken PR) to build durable storage strength.</p>
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
            <span class="text-[11px] text-slate-500">Tab = 4 spaces • Cmd/Ctrl+Enter = Run • 5M ops TLE guard</span>
          </div>
          <textarea id="practice-editor" rows="13" class="w-full bg-slate-950 border border-slate-800 rounded-b-xl p-4 text-xs font-mono text-emerald-300 focus:outline-none focus:border-emerald-500 leading-relaxed custom-scrollbar code-editor-textarea" spellcheck="false" placeholder="Write your solution here..."></textarea>
        </div>

        <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800/80 space-y-2">
          <div class="flex items-center justify-between text-xs font-mono">
            <label for="practice-custom-input" class="text-slate-400 font-bold">Custom Test Input (JSON args):</label>
            <span class="text-slate-500 text-[10px]">Optional ad-hoc dry run</span>
          </div>
          <div class="flex gap-2">
            <input id="practice-custom-input" type="text" class="flex-1 bg-slate-900 border border-slate-700 rounded-lg px-3 py-1.5 text-xs font-mono text-slate-200 focus:outline-none focus:border-indigo-500" placeholder="Enter comma-separated JSON arguments...">
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
            <button onclick="LessonRuntime.PracticeArena.loadSolution()" class="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-xs font-mono text-emerald-300">Load into Editor</button>
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

    <!-- Section 6: Spoken Interview Script & Mid-Level (L4) Follow-Ups -->
    <section id="section-6" class="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 sm:p-8 space-y-6">
      <div class="border-b border-slate-800 pb-4">
        <span class="text-xs font-mono text-amber-400 uppercase tracking-wider font-semibold">Section 06 &bull; Live Interview Execution Transcript</span>
        <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">Live Interview Spoken Script &amp; Mid-Level (L4) Follow-Ups</h3>
        <p class="text-xs text-slate-400 mt-1">4-phase spoken communication script for Karat, pair-programming screens, and onsite loops.</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-cyan-400 uppercase tracking-wider block">Phase 1: Clarifying Constraints</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">{sanitize_html_fragment(sp_script.get('phase1', ''))}</p>
        </div>
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider block">Phase 2: Space-Time Trade-Off Pitch</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">{sanitize_html_fragment(sp_script.get('phase2', ''))}</p>
        </div>
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-amber-400 uppercase tracking-wider block">Phase 3: Clean Implementation Narration</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">{sanitize_html_fragment(sp_script.get('phase3', ''))}</p>
        </div>
        <div class="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
          <span class="text-xs font-mono font-bold text-purple-400 uppercase tracking-wider block">Phase 4: Boundary Dry-Run Verification</span>
          <p class="text-xs text-slate-300 italic leading-relaxed">{sanitize_html_fragment(sp_script.get('phase4', ''))}</p>
        </div>
      </div>

      <div class="p-4 rounded-xl bg-slate-950 border border-amber-900/40 text-xs text-slate-300 space-y-2">
        <span class="text-amber-400 font-bold font-mono uppercase tracking-wider block text-[11px]">&#9889; Mid-Level (L4) Production &amp; Requirement-Pivot Follow-Up:</span>
        <p class="leading-relaxed">{sanitize_html_fragment(followup_text)}</p>
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

      <div id="quiz-container" class="space-y-6"></div>

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
          <li><strong>Step 1 (Immediate):</strong> Reset to stub (or close this tab) and code the solution cold without peeking at Section 5.</li>
          <li><strong>Step 2 (24 Hours):</strong> Articulate the mental switch ({sanitize_html_fragment(p.get('mental_model', {}).get('analogy_title', 'Mental Concept'))}) and Big-O trade-off aloud in 60 seconds.</li>
          <li><strong>Step 3 (48 Hours):</strong> Return when the timer hits 0 and solve cold within the target time window (or use <strong>🐛 Debug Broken PR</strong>).</li>
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

      <div class="grid md:grid-cols-2 gap-6 pt-2">
        <div class="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-2 text-xs font-mono">
          <span class="text-indigo-400 font-bold uppercase tracking-wider block text-[11px]">📖 Primary Trusted References:</span>
          <p class="text-slate-300 leading-relaxed">
            <a href="{src_url}" target="_blank" rel="noopener noreferrer" class="text-emerald-400 underline font-semibold">{html.escape(src_title)}</a> &amp; {html.escape(src_book)}. Quick-review in <a href="../reference/pattern-reference.html" class="text-cyan-400 underline">Pattern Cheat Sheet</a> and <a href="../reference/glossary.html" class="text-cyan-400 underline">Glossary</a>.
          </p>
        </div>

        <div class="p-5 rounded-2xl bg-indigo-950/30 border border-indigo-500/30 space-y-2 text-xs font-mono">
          <span class="text-indigo-300 font-bold uppercase tracking-wider block text-[11px]">💬 Ask Your Teacher Agent:</span>
          <p class="text-slate-300 leading-relaxed">
            Want a mock Karat requirement pivot, edge-case critique on your code, or deeper intuition on this invariant? <strong>Ask follow-up questions in chat anytime</strong>—Antigravity is your pair-programming teacher.
          </p>
        </div>
      </div>
    </section>

    <nav class="border-t border-slate-800 pt-8 flex items-center justify-between text-xs font-mono">
      <a href="{prev_file}" class="text-slate-400 hover:text-white transition-colors">&larr; {prev_label}</a>
      <a href="../master-plan.html" class="text-slate-400 hover:text-emerald-400 transition-colors">Master Plan (126 Problems)</a>
      <a href="{next_file}" class="text-emerald-400 hover:text-emerald-300 font-bold transition-colors">{next_label} &rarr;</a>
    </nav>

  </main>

  <script>
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

      {st.get('js_update_metrics', '')}
      {st.get('js_render_visual', 'if (typeof renderStage === "function") renderStage(step);')}

      const totalLines = {len(st.get('pseudocode_lines', []))};
      for (let i = 1; i <= totalLines; i++) {{
        const lineEl = document.getElementById(`code-line-${{i}}`);
        if (lineEl) {{
          lineEl.className = (step.line === i)
            ? "px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-300 border-l-2 border-emerald-400 font-bold"
            : "px-2 py-0.5 rounded text-slate-400";
        }}
      }}

      const btnPrev = document.getElementById('btn-prev');
      if (btnPrev) btnPrev.disabled = (currentStepIdx === 0);
      const btnNext = document.getElementById('btn-next');
      if (btnNext) btnNext.disabled = (currentStepIdx === steps.length - 1);
    }}

    function stepperNext() {{
      if (currentStepIdx < steps.length - 1) {{ currentStepIdx++; renderStepper(); }}
      else if (isPlaying) togglePlay();
    }}
    function stepperPrev() {{
      if (currentStepIdx > 0) {{ currentStepIdx--; renderStepper(); }}
    }}
    function stepperReset() {{
      currentStepIdx = 0;
      if (isPlaying) togglePlay();
      renderStepper();
    }}

    {st.get('js_custom_input', '''function handleCustomInput() {{
      const el = document.getElementById('custom-input');
      if (!el) return;
      const raw = el.value.trim();
      if (!raw) return;
      try {{
        const parsed = JSON.parse('[' + raw + ']');
        if (typeof loadPreset === 'function') {{ loadPreset.apply(null, parsed); return; }}
        if (typeof buildSteps === 'function') {{ buildSteps.apply(null, parsed); stepperReset(); return; }}
      }} catch (_) {{}}
      if (typeof loadPreset === 'function') loadPreset(raw);
      else if (typeof buildSteps === 'function') {{ buildSteps(raw); stepperReset(); }}
    }}''')}

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
          if (currentStepIdx < steps.length - 1) stepperNext();
          else togglePlay();
        }}, 1200);
      }}
    }}

    // ---------------- SOLUTION TABS LOGIC ----------------
    function switchApproach(num) {{
      const total = {len(approaches)};
      for (let n = 1; n <= total; n++) {{
        const block = document.getElementById(`approach-${{n}}`);
        const tab = document.getElementById(`tab-app-${{n}}`);
        if (!block || !tab) continue;
        block.classList.toggle('hidden', n !== num);
        tab.className = (n === num)
          ? "px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold transition-all"
          : "px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all";
      }}
    }}

    function switchLangApp(appNum, lang) {{
      ['kotlin', 'python', 'java'].forEach(l => {{
        const codeEl = document.getElementById(`code-app${{appNum}}-${{l}}`);
        const btn = document.getElementById(`lang-btn-app${{appNum}}-${{l}}`);
        if (codeEl && btn) {{
          codeEl.classList.toggle('hidden', l !== lang);
          btn.className = (l === lang)
            ? "px-3 py-1 rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold"
            : "px-3 py-1 rounded text-slate-400 hover:text-white";
        }}
      }});
    }}

    window.addEventListener('DOMContentLoaded', () => {{
      LessonRuntime.Whiteboard.init({{
        canvasId: 'whiteboard-canvas',
        bodyId: 'wb-body',
        toggleBtnId: 'wb-toggle-btn',
        toggleIconId: 'wb-toggle-icon',
        toggleTextId: 'wb-toggle-text',
        hintId: 'wb-hint'
      }});

      try {{
        {st.get('js_init_call', 'if (typeof buildSteps === "function") buildSteps();')}
        renderStepper();
      }} catch (err) {{
        console.warn("Stepper init:", err);
      }}

      LessonRuntime.PracticeArena.init({{
        problemId: '{num_str}',
        methodName: '{method_name}',
        {f"ioType: '{p['io_type']}'," if p.get('io_type') else ""}
        {f"inPlace: true," if p.get('in_place') else ""}
        starterStubs: {json.dumps(stubs)},
        modelSolutions: {json.dumps(solutions)},
        hints: {json.dumps(hints)},
        testCases: {json.dumps(test_cases)}
      }});

      LessonRuntime.Quiz.init({{
        problemId: '{num_str}',
        containerId: 'quiz-container',
        questions: {json.dumps(quiz_questions)}
      }});

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
