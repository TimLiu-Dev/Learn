"""
2027 Mid-Tech Curriculum Definitions for Lessons 0117 to 0121:
- #0117: LC #242 Valid Anagram (Pass 1 Easy - Fixed 26-Bucket Frequency Array)
- #0118: LC #252 Meeting Rooms (Pass 1 Easy - Interval Start-Sort & Overlap Check)
- #0119: LC #733 Flood Fill (Pass 1 Easy - 4-Directional Grid DFS/BFS + Same-Color Guard)
- #0120: LC #703 Kth Largest Element in a Stream (Pass 1 Easy - OOP Size-K Min-Heap)
- #0121: LC #40 Combination Sum II (Pass 2B Medium - Sorted Backtracking + Sibling Pruning)
"""

p117 = {
    "num": 117,
    "lc": 242,
    "title": "Valid Anagram",
    "category": "Arrays, Strings & Hashing",
    "difficulty": "Easy",
    "pass_label": "Pass 1",
    "lc_slug": "valid-anagram",
    "nc_url": "https://neetcode.io/problems/is-anagram/question?list=allNC",
    "prev_file": "0116-lc1091-shortest-path-in-binary-matrix.html",
    "next_file": "0118-lc252-meeting-rooms.html",
    "method_name": "isAnagram",
    "verbatim_statement": (
        "Given two strings <code>s</code> and <code>t</code>, return <code>true</code> if <code>t</code> is an "
        "<strong>anagram</strong> of <code>s</code>, and <code>false</code> otherwise.<br><br>"
        "An <strong>anagram</strong> is a word or phrase formed by rearranging the letters of a different word or phrase, "
        "using all the original letters exactly once."
    ),
    "examples": [
        {"title": "Example 1", "input": 's = "anagram", t = "nagaram"', "output": "true"},
        {"title": "Example 2", "input": 's = "rat", t = "car"', "output": "false"},
        {"title": "Example 3", "input": 's = "ab", t = "a"', "output": "false"}
    ],
    "constraints": [
        "1 <= s.length, t.length <= 5 * 10^4",
        "s and t consist of lowercase English letters."
    ],
    "plain_english": (
        "Check whether two strings contain the exact same multiset of characters—meaning identical lengths "
        "and identical counts for every letter from 'a' to 'z'."
    ),
    "boundary_checks": {
        "min_base": 's = "a", t = "a" -> Same length 1, bucket[0] increments then decrements back to 0 -> true.',
        "best_exit": 's = "abc", t = "ab" -> Unequal lengths (3 != 2) short-circuits in O(1) before touching any array.',
        "worst_scan": "s.length = t.length = 50,000 -> Single pass updates IntArray(26), followed by 26-slot zero check."
    },
    "mental_model": {
        "analogy_title": "The 26-Slot Scrabble Tile Ledger",
        "analogy_text": (
            "Imagine a wooden tray with 26 labeled compartments from 'a' to 'z'. As you read string s, every letter drops "
            "+1 marble into its compartment; at the exact same index in string t, its letter removes -1 marble. "
            "If s and t are true anagrams, every single deposit is canceled out by an equal withdrawal, leaving all 26 compartments at 0."
        ),
        "aha_moment": "Guard s.length != t.length upfront, then use a fixed 26-integer array (+1 for s[i], -1 for t[i]) for zero-boxing O(N) time and O(1) auxiliary space.",
        "trigger": "Comparing character frequencies across lowercase ASCII strings or building canonical keys for anagram grouping.",
        "sisters": [
            {"name": "Contains Duplicate", "lc": "#217"},
            {"name": "Group Anagrams", "lc": "#49"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N log N) Time, O(N) Space",
        "naive_text": "Convert both strings to character arrays, sort both alphabetically, and compare the sorted arrays.",
        "wasted_work": "Comparison sorting spends O(N log N) work ordering characters when only total per-letter counts matter, and allocates two O(N) char copies.",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Reject mismatched lengths immediately, then increment counts[s[i] - 'a'] and decrement counts[t[i] - 'a'] inside one primitive IntArray(26).",
        "core_invariant": "After scanning prefix 0..i, counts[c] equals freq_s(c, 0..i) - freq_t(c, 0..i) for all c in 'a'..'z'.",
        "table_rows": [
            {"name": "Naive: Sort Both Char Arrays", "time": "O(N log N)", "space": "O(N)", "note": "Allocates two char arrays; slower for 50K strings."},
            {"name": "Better: Generic HashMap<Char, Int>", "time": "O(N)", "space": "O(U)", "note": "Required for full Unicode follow-up, but boxes chars on JVM."},
            {"name": "Optimal: Fixed 26-Int Bucket Counter", "time": "O(N)", "space": "O(1) (26 ints)", "note": "★ Cache-resident 104-byte array; zero heap boxing."}
        ]
    },
    "trace_table": {
        "input_desc": 's = "rat", t = "car"',
        "columns": ["i", "s[i] (+1)", "t[i] (-1)", "Non-Zero Buckets", "Status"],
        "rows": [
            ["init", "-", "-", "all 0 (lengths 3 == 3)", "Proceed to scan"],
            ["0", "'r' (+1)", "'c' (-1)", "c:-1, r:+1", "Balanced total, nonzero slots"],
            ["1", "'a' (+1)", "'a' (-1)", "a:0, c:-1, r:+1", "'a' cancels immediately"],
            ["2", "'t' (+1)", "'r' (-1)", "c:-1, r:0, t:+1", "Scan finished -> check buckets"],
            ["check", "-", "-", "counts['c'] == -1 != 0", "Return false immediately"]
        ],
        "takeaway": "Because lengths are equal, any deficit (-1) in one letter guarantees a surplus (+1) elsewhere."
    },
    "stepper": {
        "title": "26-Bucket Net Balance Visualizer",
        "default_input": '"anagram", "nagaram"',
        "presets": [
            {"label": '"anagram" vs "nagaram"', "onclick": 'loadPreset("anagram", "nagaram")'},
            {"label": '"rat" vs "car"', "onclick": 'loadPreset("rat", "car")'},
            {"label": '"ab" vs "a"', "onclick": 'loadPreset("ab", "a")'}
        ],
        "metrics": [
            {"id": "m-idx", "label": "Index i", "color": "text-cyan-400", "initial": "-"},
            {"id": "m-pair", "label": "s[i] / t[i]", "color": "text-amber-400", "initial": "-"},
            {"id": "m-nonzero", "label": "Non-Zero Slots", "color": "text-purple-400", "initial": "0"},
            {"id": "m-res", "label": "Result", "color": "text-emerald-400", "initial": "Pending"}
        ],
        "visual_html": '<div id="p117-stage" class="flex flex-wrap items-center justify-center gap-2 w-full"></div>',
        "initial_narration": "Step through each index to watch +1 / -1 bucket cancellations.",
        "pseudocode_lines": [
            "if (s.length != t.length) return false",
            "val counts = IntArray(26)",
            "for (i in s.indices) {",
            "  counts[s[i] - 'a']++; counts[t[i] - 'a']--",
            "}",
            "return counts.all { it == 0 }"
        ],
        "js_code": """
    let curS = "anagram", curT = "nagaram";
    function buildSteps(sVal, tVal) {
      if (typeof sVal === 'string') curS = sVal.toLowerCase().replace(/[^a-z]/g, '');
      if (typeof tVal === 'string') curT = tVal.toLowerCase().replace(/[^a-z]/g, '');
      steps = [];
      if (curS.length !== curT.length) {
        steps.push({ i: '-', pair: `${curS.length} != ${curT.length}`, map: {}, res: 'false', line: 1, narration: `Length mismatch (${curS.length} vs ${curT.length}) -> return false immediately in O(1).` });
        return;
      }
      let map = {};
      steps.push({ i: 'init', pair: '-', map: {...map}, res: 'Pending', line: 2, narration: `Lengths match (${curS.length}). Initialized 26 zeroed buckets.` });
      for (let i = 0; i < curS.length; i++) {
        const a = curS[i], b = curT[i];
        map[a] = (map[a] || 0) + 1;
        map[b] = (map[b] || 0) - 1;
        steps.push({ i, pair: `+${a} / -${b}`, map: {...map}, res: 'Scanning', line: 4, narration: `Index ${i}: incremented '${a}' and decremented '${b}'.` });
      }
      const ok = Object.values(map).every(v => v === 0);
      steps.push({ i: 'done', pair: '-', map: {...map}, res: String(ok), line: 6, narration: ok ? 'All 26 buckets returned to 0 -> Valid Anagram (true)!' : 'Non-zero buckets remain -> Not an anagram (false).' });
    }
    function renderStage(step) {
      const el = document.getElementById('p117-stage');
      if (!el) return;
      const keys = Object.keys(step.map).sort();
      if (keys.length === 0) {
        el.innerHTML = '<div class="text-xs font-mono text-slate-400">26 buckets initialized to 0</div>';
        return;
      }
      el.innerHTML = keys.map(k => {
        const v = step.map[k];
        const cls = v === 0 ? 'border-emerald-500/40 bg-emerald-950/30 text-emerald-300' : (v > 0 ? 'border-amber-500/50 bg-amber-950/40 text-amber-300' : 'border-rose-500/50 bg-rose-950/40 text-rose-300');
        return `<div class="px-3 py-2 rounded-xl border font-mono text-xs flex flex-col items-center ${cls}"><span class="text-[10px] opacity-75">'${k}'</span><span class="font-bold text-sm">${v > 0 ? '+' + v : v}</span></div>`;
      }).join('');
    }
    function loadPreset(s, t) { buildSteps(s, t); stepperReset(); }
        """,
        "js_init_call": 'buildSteps("anagram", "nagaram");',
        "js_update_metrics": """
        document.getElementById('m-idx').textContent = step.i;
        document.getElementById('m-pair').textContent = step.pair;
        document.getElementById('m-nonzero').textContent = Object.values(step.map).filter(v => v !== 0).length;
        document.getElementById('m-res').textContent = step.res;
        """
    },
    "approaches": [
        {
            "name": "Sorting Both Strings",
            "badge": "Naive",
            "desc": "Sort both character arrays and check equality in O(N log N) time.",
            "time": "O(N log N)",
            "space": "O(N)",
            "code_kotlin": """class Solution {
    fun isAnagram(s: String, t: String): Boolean {
        if (s.length != t.length) return false
        val a = s.toCharArray()
        val b = t.toCharArray()
        a.sort()
        b.sort()
        return a.contentEquals(b)
    }
}""",
            "code_python": """class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        return sorted(s) == sorted(t)""",
            "code_java": """import java.util.Arrays;

class Solution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        char[] a = s.toCharArray();
        char[] b = t.toCharArray();
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }
}"""
        },
        {
            "name": "Optimal: Fixed 26-Slot Net Frequency Array",
            "badge": "Optimal",
            "desc": "Single-pass +1/-1 counting inside a fixed 26-int array with O(1) space.",
            "time": "O(N)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun isAnagram(s: String, t: String): Boolean {
        if (s.length != t.length) return false
        val counts = IntArray(26)
        for (i in 0 until s.length) {
            counts[s[i] - 'a']++
            counts[t[i] - 'a']--
        }
        for (c in counts) {
            if (c != 0) return false
        }
        return true
    }
}""",
            "code_python": """class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        counts = [0] * 26
        for i in range(len(s)):
            counts[ord(s[i]) - 97] += 1
            counts[ord(t[i]) - 97] -= 1
        for c in counts:
            if c != 0:
                return False
        return True""",
            "code_java": """class Solution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] counts = new int[26];
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }
        for (int c : counts) {
            if (c != 0) return false;
        }
        return true;
    }
}"""
        }
    ],
    "test_cases": [
        {"input": ["anagram", "nagaram"], "expected": True},
        {"input": ["rat", "car"], "expected": False},
        {"input": ["ab", "a"], "expected": False},
        {"input": ["listen", "silent"], "expected": True}
    ],
    "spoken_script": {
        "phase1": "First I'll confirm the alphabet: since inputs are lowercase English 'a'..'z', the alphabet size is fixed at 26. Also, unequal string lengths can return false immediately in O(1).",
        "phase2": "Sorting both strings takes O(N log N) time and O(N) char-array memory. Because we only care about character multiplicities over 26 letters, a primitive IntArray(26) gives O(N) time and O(1) space with zero heap boxing.",
        "phase3": "In one loop over i in 0 until s.length, I increment counts[s[i] - 'a'] and decrement counts[t[i] - 'a'], then verify all 26 slots equal zero.",
        "phase4": "Tracing s='rat', t='car': lengths are 3; 'a' cancels to 0, 'r' cancels to 0, while 't' stays +1 and 'c' stays -1, returning false on 'c'.",
        "mid_followup": "For the classic Unicode follow-up (emoji / multi-byte UTF-16 surrogate pairs), replace IntArray(26) with a HashMap<Int, Int> keyed by s.codePoints() (Kotlin/Java codePointAt) rather than raw 16-bit Char units so surrogate pairs aren't torn apart."
    },
    "quiz_questions": [
        {
            "category": "Boundary Guard Bug",
            "prompt": "What fails if you omit `if (s.length != t.length) return false` before looping `0 until s.length`?",
            "codeSnippet": "fun isAnagram(s: String, t: String): Boolean {\n    val counts = IntArray(26)\n    for (i in 0 until s.length) {\n        counts[s[i] - 'a']++; counts[t[i] - 'a']--\n    }\n    return counts.all { it == 0 }\n}",
            "options": [
                {"id": "A", "text": "When s is longer than t, t[i] throws StringIndexOutOfBoundsException; when shorter, trailing t characters are ignored.", "isCorrect": True, "whyRight": "For s='a', t='ab', looping only s.length checks index 0 and falsely returns true!"},
                {"id": "B", "text": "Kotlin IntArray(26) initializes elements to null instead of zero unless length is checked first.", "isCorrect": False, "whyWrong": "Primitive IntArray always initializes to 0."},
                {"id": "C", "text": "Subtracting 'a' from lowercase ASCII characters produces negative indices whenever strings differ in length.", "isCorrect": False, "whyWrong": "'a'..'z' always maps to 0..25 regardless of string length."},
                {"id": "D", "text": "The loop degrades from O(N) linear time to O(N^2) quadratic time on unequal strings.", "isCorrect": False, "whyWrong": "Runtime crashes or miscomputes rather than becoming quadratic."}
            ]
        },
        {
            "category": "Unicode Follow-Up",
            "prompt": "If inputs can contain arbitrary Unicode symbols including emojis, how should the data structure adapt?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Iterate Unicode code points and count frequencies inside a HashMap<Int, Int> taking O(U) space.", "isCorrect": True, "whyRight": "Code points handle supplementary characters > U+FFFF cleanly without allocating a 1.1M-entry array."},
                {"id": "B", "text": "Keep IntArray(26) and take modulo 26 of every character code to avoid collisions.", "isCorrect": False, "whyWrong": "Modulo 26 aliases distinct characters into identical buckets, causing false positives."},
                {"id": "C", "text": "Sort raw UTF-8 bytes independently so multi-byte characters scramble across character boundaries.", "isCorrect": False, "whyWrong": "Sorting raw bytes breaks multi-byte sequence integrity (e.g., two different UTF-8 pairs sharing bytes)."},
                {"id": "D", "text": "Use a BooleanArray(256) marking whether each character appeared at least once.", "isCorrect": False, "whyWrong": "Booleans lose multiplicity ('aab' vs 'abb') and 256 overflows on Unicode."}
            ]
        },
        {
            "category": "Complexity Analysis",
            "prompt": "Why is the auxiliary space of `IntArray(26)` classified as `O(1)` even when `N = 50,000`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "The array size is bounded by constant alphabet size 26 and never grows with N.", "isCorrect": True, "whyRight": "26 * 4 = 104 bytes is strictly independent of input string length N."},
                {"id": "B", "text": "JVM arrays are allocated inside CPU registers rather than main memory.", "isCorrect": False, "whyWrong": "IntArray is still heap-allocated (104 bytes + object header), just constant-sized."},
                {"id": "C", "text": "Big-O notation ignores any memory structure that stores integer counts.", "isCorrect": False, "whyWrong": "An N-sized integer array would still be O(N) space."},
                {"id": "D", "text": "The garbage collector deletes each bucket immediately after every loop iteration.", "isCorrect": False, "whyWrong": "The 26-element array lives until isAnagram returns."}
            ]
        },
        {
            "category": "Bridge to #49 Group Anagrams",
            "prompt": "How does Valid Anagram's 26-count array directly power optimal `O(N * K)` Group Anagrams (#49)?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Each word's delimited 26-count array acts as a canonical HashMap key without O(K log K) sorting.", "isCorrect": True, "whyRight": "Two words are anagrams iff their 26-count vectors are identical."},
                {"id": "B", "text": "Group Anagrams runs pairwise isAnagram comparisons across all word pairs in O(N) total time.", "isCorrect": False, "whyWrong": "Pairwise comparison across N words costs O(N^2 * K) time."},
                {"id": "C", "text": "Summing ASCII codes into one integer uniquely identifies every anagram group without collisions.", "isCorrect": False, "whyWrong": "ASCII sums collide easily (e.g., 'ac' = 97+99 = 196 == 'bb' = 98+98)."},
                {"id": "D", "text": "XORing all characters of a word produces a collision-free anagram signature.", "isCorrect": False, "whyWrong": "XOR cancels duplicate letters ('aa' ^ 'b' == 'b')."}
            ]
        }
    ]
}


p118 = {
    "num": 118,
    "lc": 252,
    "title": "Meeting Rooms",
    "category": "Intervals & Greedy",
    "difficulty": "Easy",
    "pass_label": "Pass 1",
    "lc_slug": "meeting-schedule",
    "nc_url": "https://neetcode.io/problems/meeting-schedule/question?list=allNC",
    "prev_file": "0117-lc242-valid-anagram.html",
    "next_file": "0119-lc733-flood-fill.html",
    "method_name": "canAttendMeetings",
    "verbatim_statement": (
        "Given an array of meeting time <code>intervals</code> where <code>intervals[i] = [start<sub>i</sub>, end<sub>i</sub>]</code>, "
        "determine if a person could attend all meetings.<br><br>"
        "Note: Back-to-back meetings where <code>end<sub>1</sub> == start<sub>2</sub></code> (such as <code>[0, 8]</code> and <code>[8, 10]</code>) do <strong>not</strong> conflict."
    ),
    "examples": [
        {"title": "Example 1", "input": "intervals = [[0,30],[5,10],[15,20]]", "output": "false"},
        {"title": "Example 2", "input": "intervals = [[7,10],[2,4]]", "output": "true"},
        {"title": "Example 3", "input": "intervals = [[0,8],[8,10]]", "output": "true"}
    ],
    "constraints": [
        "0 <= intervals.length <= 10^4",
        "intervals[i].length == 2",
        "0 <= start_i < end_i <= 10^6"
    ],
    "plain_english": "Sort all meetings chronologically by start time, then verify no meeting begins strictly before the previous meeting ends (`currStart < prevEnd`).",
    "boundary_checks": {
        "min_base": "intervals = [] or [[5, 10]] -> 0 or 1 meeting can never conflict -> returns true.",
        "best_exit": "After sorting, intervals[1].start < intervals[0].end -> returns false on index 1.",
        "worst_scan": "10^4 non-overlapping meetings -> O(N log N) sort + single O(N) adjacent check returns true."
    },
    "mental_model": {
        "analogy_title": "Single-Track Train Schedule",
        "analogy_text": (
            "Imagine lining up all reservations on a single conference room calendar in order of start time. "
            "You never need to compare every meeting against every other meeting: once sorted by start time, if Meeting i "
            "doesn't overlap Meeting i-1 (start_i >= end_{i-1}), it cannot overlap any earlier meeting either because every earlier meeting ended <= end_{i-1}!"
        ),
        "aha_moment": "Sorting by start time reduces N-choose-2 pairwise overlap checks to N - 1 adjacent comparisons: conflict iff intervals[i][0] < intervals[i - 1][1].",
        "trigger": "Detecting schedule conflicts, validating non-overlapping bookings, and warming up for Merge Intervals (#56) & Meeting Rooms II (#253).",
        "sisters": [
            {"name": "Merge Intervals", "lc": "#56"},
            {"name": "Meeting Rooms II", "lc": "#253"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N²) Time, O(1) Space",
        "naive_text": "Check every pair (i, j) for overlap using max(start_i, start_j) < min(end_i, end_j).",
        "wasted_work": "Comparing 10^4 intervals pairwise performs ~50 million checks on temporally distant meetings.",
        "optimal_complexity": "O(N log N) Time, O(1)–O(N) Sort Space",
        "optimal_text": "Sort intervals by start ascending, then check adjacent pairs i = 1..N-1 for intervals[i][0] < intervals[i - 1][1].",
        "core_invariant": "If intervals[0..i-1] are sorted by start and mutually disjoint, their maximum end time is strictly intervals[i - 1][1].",
        "table_rows": [
            {"name": "Naive: All-Pairs Overlap Check", "time": "O(N²)", "space": "O(1)", "note": "Slow when N = 10^4."},
            {"name": "Optimal: Sort by Start + Adjacent Scan", "time": "O(N log N)", "space": "O(log N)–O(N)", "note": "★ Canonical foundation for every interval problem."}
        ]
    },
    "trace_table": {
        "input_desc": "intervals = [[0,30],[5,10],[15,20]] (already sorted by start)",
        "columns": ["Step", "Prev [s, e]", "Curr [s, e]", "curr.start < prev.end?", "Action"],
        "rows": [
            ["Sort", "-", "-", "[[0,30], [5,10], [15,20]]", "Ordered by start ascending"],
            ["i = 1", "[0, 30]", "[5, 10]", "5 < 30 -> TRUE!", "Overlap detected -> return false"]
        ],
        "takeaway": "Exact boundary touch (e.g. [0,8] and [8,10]) has 8 < 8 == false, which is valid!"
    },
    "stepper": {
        "title": "Chronological Start-Sort & Adjacent Overlap Scanner",
        "default_input": "[[0,30],[5,10],[15,20]]",
        "presets": [
            {"label": "Conflict [[0,30],[5,10],[15,20]]", "onclick": "loadPreset([[0,30],[5,10],[15,20]])"},
            {"label": "Valid [[7,10],[2,4]]", "onclick": "loadPreset([[7,10],[2,4]])"},
            {"label": "Touching [[0,8],[8,10]]", "onclick": "loadPreset([[0,8],[8,10]])"}
        ],
        "metrics": [
            {"id": "m-idx", "label": "Pair Index", "color": "text-cyan-400", "initial": "-"},
            {"id": "m-prev", "label": "Prev End", "color": "text-amber-400", "initial": "-"},
            {"id": "m-curr", "label": "Curr Start", "color": "text-purple-400", "initial": "-"},
            {"id": "m-res", "label": "Can Attend?", "color": "text-emerald-400", "initial": "Pending"}
        ],
        "visual_html": '<div id="p118-stage" class="flex flex-wrap items-center justify-center gap-3 w-full"></div>',
        "initial_narration": "Sort intervals by start time, then inspect adjacent boundaries.",
        "pseudocode_lines": [
            "if (intervals.size <= 1) return true",
            "intervals.sortBy { it[0] }",
            "for (i in 1 until intervals.size) {",
            "  if (intervals[i][0] < intervals[i - 1][1]) return false",
            "}",
            "return true"
        ],
        "js_code": """
    let curIv = [[0,30],[5,10],[15,20]];
    function buildSteps(arr) {
      if (Array.isArray(arr)) curIv = arr.map(x => x.slice());
      steps = [];
      const sorted = curIv.slice().sort((a, b) => a[0] - b[0]);
      steps.push({ sorted, idx: -1, prevEnd: '-', currStart: '-', conflict: false, res: sorted.length <= 1 ? 'true' : 'Sorted', line: 2, narration: `Sorted by start time: ${JSON.stringify(sorted)}.` });
      for (let i = 1; i < sorted.length; i++) {
        const prevEnd = sorted[i - 1][1], currStart = sorted[i][0];
        const bad = currStart < prevEnd;
        steps.push({ sorted, idx: i, prevEnd, currStart, conflict: bad, res: bad ? 'false' : 'OK', line: 4, narration: `Compare #${i-1} end (${prevEnd}) vs #${i} start (${currStart}): ${currStart} < ${prevEnd} is ${bad}.` });
        if (bad) return;
      }
      if (sorted.length > 1) {
        steps.push({ sorted, idx: sorted.length, prevEnd: '-', currStart: '-', conflict: false, res: 'true', line: 6, narration: 'No adjacent conflicts found -> return true!' });
      }
    }
    function renderStage(step) {
      const el = document.getElementById('p118-stage');
      if (!el) return;
      if (!step.sorted.length) { el.innerHTML = '<span class="text-xs font-mono text-emerald-400">Empty schedule -> true</span>'; return; }
      el.innerHTML = step.sorted.map((iv, i) => {
        let cls = 'border-slate-700 bg-slate-900 text-slate-300';
        if (i === step.idx && step.conflict) cls = 'border-rose-500 bg-rose-950/50 text-rose-200 ring-2 ring-rose-500/40';
        else if (i === step.idx || i === step.idx - 1) cls = 'border-cyan-400 bg-cyan-950/40 text-cyan-200';
        return `<div class="px-3.5 py-2 rounded-xl border font-mono text-xs flex flex-col items-center ${cls}"><span class="text-[10px] text-slate-400">#${i}</span><span class="font-bold">[${iv[0]}, ${iv[1]}]</span></div>`;
      }).join('');
    }
    function loadPreset(arr) { buildSteps(arr); stepperReset(); }
        """,
        "js_init_call": "buildSteps([[0,30],[5,10],[15,20]]);",
        "js_update_metrics": """
        document.getElementById('m-idx').textContent = step.idx >= 0 ? step.idx : '-';
        document.getElementById('m-prev').textContent = step.prevEnd;
        document.getElementById('m-curr').textContent = step.currStart;
        document.getElementById('m-res').textContent = step.res;
        """
    },
    "approaches": [
        {
            "name": "Brute Force: All-Pairs Overlap Check",
            "badge": "Naive",
            "desc": "Checks every pair (i, j) without sorting in O(N²) time.",
            "time": "O(N²)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
        for (i in intervals.indices) {
            for (j in i + 1 until intervals.size) {
                if (maxOf(intervals[i][0], intervals[j][0]) < minOf(intervals[i][1], intervals[j][1])) {
                    return false
                }
            }
        }
        return true
    }
}""",
            "code_python": """class Solution:
    def canAttendMeetings(self, intervals: list[list[int]]) -> bool:
        for i in range(len(intervals)):
            for j in range(i + 1, len(intervals)):
                if max(intervals[i][0], intervals[j][0]) < min(intervals[i][1], intervals[j][1]):
                    return False
        return True""",
            "code_java": """class Solution {
    public boolean canAttendMeetings(int[][] intervals) {
        for (int i = 0; i < intervals.length; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (Math.max(intervals[i][0], intervals[j][0]) < Math.min(intervals[i][1], intervals[j][1])) {
                    return false;
                }
            }
        }
        return true;
    }
}"""
        },
        {
            "name": "Optimal: Sort by Start + Adjacent Boundary Check",
            "badge": "Optimal",
            "desc": "Clone/sort intervals by start ascending, then check currStart < prevEnd in one linear scan.",
            "time": "O(N log N)",
            "space": "O(N)",
            "code_kotlin": """class Solution {
    fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
        if (intervals.size <= 1) return true
        val sorted = intervals.sortedBy { it[0] }
        for (i in 1 until sorted.size) {
            if (sorted[i][0] < sorted[i - 1][1]) return false
        }
        return true
    }
}""",
            "code_python": """class Solution:
    def canAttendMeetings(self, intervals: list[list[int]]) -> bool:
        if len(intervals) <= 1:
            return True
        ordered = sorted(intervals, key=lambda iv: iv[0])
        for i in range(1, len(ordered)):
            if ordered[i][0] < ordered[i - 1][1]:
                return False
        return True""",
            "code_java": """import java.util.Arrays;

class Solution {
    public boolean canAttendMeetings(int[][] intervals) {
        if (intervals.length <= 1) return true;
        int[][] sorted = Arrays.copyOf(intervals, intervals.length);
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i][0] < sorted[i - 1][1]) return false;
        }
        return true;
    }
}"""
        }
    ],
    "test_cases": [
        {"input": [[[0, 30], [5, 10], [15, 20]]], "expected": False},
        {"input": [[[7, 10], [2, 4]]], "expected": True},
        {"input": [[[0, 8], [8, 10]]], "expected": True},
        {"input": [[]], "expected": True}
    ],
    "spoken_script": {
        "phase1": "First I clarify endpoint inclusivity: if one meeting ends at t=8 and the next starts at t=8, is that allowed? Yes, half-open intervals [start, end) do not conflict at 8 == 8, and empty/single-meeting schedules return true.",
        "phase2": "All-pairs checking costs O(N²). Sorting by start time in O(N log N) means any non-overlapping prefix has its latest end strictly at intervals[i-1][1], so checking adjacent pairs suffices.",
        "phase3": "I'll ask whether mutating the caller's array is okay or sort a shallow copy with Integer.compare(a[0], b[0])—never a[0] - b[0]—then scan i = 1..n-1 returning false if sorted[i][0] < sorted[i-1][1].",
        "phase4": "Dry-running [[0,8],[8,10]]: 8 < 8 is false, so it returns true. On [[0,30],[5,10]]: 5 < 30 is true, returning false on i=1.",
        "mid_followup": "If meetings arrive dynamically as an online booking calendar (LC #729 My Calendar I), sorting on every insert degrades to O(N) per call; instead store bookings in a TreeMap<Int, Int> (start -> end) and check floorEntry(start) and ceilingEntry(start) in O(log N) per booking."
    },
    "quiz_questions": [
        {
            "category": "Boundary Off-By-One Bug",
            "prompt": "What breaks if you write `if (sorted[i][0] <= sorted[i - 1][1]) return false`?",
            "codeSnippet": "if (sorted[i][0] <= sorted[i - 1][1]) return false",
            "options": [
                {"id": "A", "text": "Back-to-back meetings like [0, 8] and [8, 10] are falsely rejected as overlapping.", "isCorrect": True, "whyRight": "Equal start and end endpoints touch without sharing positive duration."},
                {"id": "B", "text": "Nested intervals like [0, 30] and [5, 10] are mistakenly accepted as valid.", "isCorrect": False, "whyWrong": "5 <= 30 still evaluates to true."},
                {"id": "C", "text": "Empty interval arrays throw IndexOutOfBoundsException inside the comparison.", "isCorrect": False, "whyWrong": "The loop 1 until sorted.size does not execute on empty arrays."},
                {"id": "D", "text": "Sorting order reverses from ascending start times to descending end times.", "isCorrect": False, "whyWrong": "The if condition runs after sorting."},
            ]
        },
        {
            "category": "Comparator Overflow Guardrail",
            "prompt": "In Java/Kotlin comparators, why is `Integer.compare(a[0], b[0])` preferred over `a[0] - b[0]`?",
            "codeSnippet": "Arrays.sort(intervals, (a, b) -> a[0] - b[0]); // vs Integer.compare(a[0], b[0])",
            "options": [
                {"id": "A", "text": "Subtraction overflows 32-bit signed int when coordinates have opposite extreme signs.", "isCorrect": True, "whyRight": "For example, -2_000_000_000 - 2_000_000_000 wraps around to a positive int, violating comparator transitivity!"},
                {"id": "B", "text": "Integer.compare reduces TimSort complexity from O(N log N) down to O(N).", "isCorrect": False, "whyWrong": "Comparison sorting still takes O(N log N)."},
                {"id": "C", "text": "Subtraction throws ArithmeticException whenever two meetings share equal start times.", "isCorrect": False, "whyWrong": "Equal numbers subtract cleanly to 0 without throwing."},
                {"id": "D", "text": "Arrays.sort rejects lambda expressions returning zero on two-element arrays.", "isCorrect": False, "whyWrong": "Returning 0 is the standard contract for equal sort keys."}
            ]
        },
        {
            "category": "Dynamic Online Calendar Pivot",
            "prompt": "If bookings arrive one-by-one online (`book(start, end)`), what structure checks and inserts in `O(log N)`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "A TreeMap keyed by start time checking floorEntry(start) and ceilingEntry(start).", "isCorrect": True, "whyRight": "Only the immediately preceding and succeeding start-sorted bookings can conflict with [start, end)."},
                {"id": "B", "text": "A standard HashSet storing start + end sums for every accepted interval.", "isCorrect": False, "whyWrong": "Endpoint sums cannot detect range overlaps."},
                {"id": "C", "text": "Re-sorting an ArrayList from scratch on every single book(start, end) call.", "isCorrect": False, "whyWrong": "Re-sorting on every call costs O(N log N) per booking."},
                {"id": "D", "text": "A Min-Heap tracking only the earliest end time across all meetings.", "isCorrect": False, "whyWrong": "A Min-Heap only inspects the minimum end, not arbitrary time slots."}
            ]
        },
        {
            "category": "Invariant Proof",
            "prompt": "After sorting by start time, why is comparing `sorted[i]` ONLY against `sorted[i - 1]` sufficient?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Once 0..i-1 are disjoint and start-sorted, sorted[i-1] necessarily has the maximum end time so far.", "isCorrect": True, "whyRight": "For any j < i-1, end_j <= start_{j+1} < end_{j+1} ... <= end_{i-1}."},
                {"id": "B", "text": "Sorting by start time also guarantees interval lengths are sorted in ascending order.", "isCorrect": False, "whyWrong": "[0, 100] comes before [5, 6] despite being much longer (which fails right at i=1!)."},
                {"id": "C", "text": "Intervals in LeetCode #252 are guaranteed to all have identical durations.", "isCorrect": False, "whyWrong": "Durations are arbitrary."},
                {"id": "D", "text": "TimSort automatically deletes overlapping intervals during the sorting pass.", "isCorrect": False, "whyWrong": "Sorting never removes elements."}
            ]
        }
    ]
}


p119 = {
    "num": 119,
    "lc": 733,
    "title": "Flood Fill",
    "category": "2D Grid Traversals & Graphs",
    "difficulty": "Easy",
    "pass_label": "Pass 1",
    "lc_slug": "flood-fill",
    "nc_url": "https://leetcode.com/problems/flood-fill/",
    "prev_file": "0118-lc252-meeting-rooms.html",
    "next_file": "0120-lc703-kth-largest-element-in-a-stream.html",
    "method_name": "floodFill",
    "verbatim_statement": (
        "You are given an image represented by an <code>m x n</code> grid of integers <code>image</code>, where <code>image[i][j]</code> "
        "represents the pixel value of the image. You are also given three integers <code>sr</code>, <code>sc</code>, and <code>color</code>.<br><br>"
        "Perform a <strong>flood fill</strong> starting from pixel <code>image[sr][sc]</code>: recolor the starting pixel and all "
        "<strong>4-directionally connected</strong> pixels sharing the same original color as <code>image[sr][sc]</code> to <code>color</code>, and return the modified image."
    ),
    "examples": [
        {"title": "Example 1", "input": "image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2", "output": "[[2,2,2],[2,2,0],[2,0,1]]"},
        {"title": "Example 2", "input": "image = [[0,0,0],[0,0,0]], sr = 0, sc = 0, color = 0", "output": "[[0,0,0],[0,0,0]]"}
    ],
    "constraints": [
        "m == image.length, n == image[i].length",
        "1 <= m, n <= 50",
        "0 <= image[i][j], color < 2^16"
    ],
    "plain_english": (
        "Like the paint-bucket tool in a drawing app: record `origColor = image[sr][sc]`, guard `origColor == color` immediately "
        "(to prevent infinite ping-pong recursion!), and recolor every 4-directionally reachable `origColor` cell to `color`."
    ),
    "boundary_checks": {
        "min_base": "1x1 grid [[3]], sr=0, sc=0, color=7 -> recolors single cell to [[7]].",
        "best_exit": "origColor == color (Example 2: 0 -> 0) -> returns immediately in O(1) without recursion or visited array!",
        "worst_scan": "50x50 uniform grid -> visits each of the 2,500 cells once in O(R * C) time."
    },
    "mental_model": {
        "analogy_title": "The Paint-Bucket Spill with Self-Sealing Paint",
        "analogy_text": (
            "Pouring new blue paint onto connected white tiles spreads up, down, left, and right only onto white tiles. "
            "Because each painted tile immediately turns blue, the new color itself acts as your 'visited' marker—EXCEPT "
            "when the bucket already holds white paint (`origColor == color`)! Checking `if (origColor == color) return image` "
            "at the very top prevents infinite neighbor bouncing without needing an extra `visited[][]` matrix."
        ),
        "aha_moment": "Guard `if (origColor == color) return image` upfront; once `origColor != color`, mutating `image[r][c] = color` serves as an O(1)-overhead visited check!",
        "trigger": "Connected-component recoloring, region labeling, and Pass 1 foundation before Number of Islands (#200) & Rotting Oranges (#994).",
        "sisters": [
            {"name": "Number of Islands", "lc": "#200"},
            {"name": "Rotting Oranges", "lc": "#994"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "StackOverflowError if origColor == color",
        "naive_text": "Run recursive DFS recoloring neighbors without checking origColor == color or allocating an explicit visited[R][C] matrix.",
        "wasted_work": "Without the same-color guard, two adjacent 0-pixels infinitely recurse between each other until the call stack crashes.",
        "optimal_complexity": "O(R · C) Time, O(R · C) Worst-Case Queue/Stack",
        "optimal_text": "Short-circuit when image[sr][sc] == color, then run iterative Queue BFS (or bounded DFS) painting cells on enqueue.",
        "core_invariant": "Every enqueued cell had value origColor and is immediately overwritten to color != origColor before its neighbors are explored.",
        "table_rows": [
            {"name": "Trap: Unguarded In-Place DFS", "time": "Infinite Loop", "space": "StackOverflow", "note": "Crashes immediately on Example 2 (orig == color)."},
            {"name": "Recursive DFS + Same-Color Guard", "time": "O(R · C)", "space": "O(R · C) call stack", "note": "Concise for 50x50; risks stack overflow on 2000x2000 images."},
            {"name": "Optimal: Iterative BFS + Same-Color Guard", "time": "O(R · C)", "space": "O(R · C) heap queue", "note": "★ Stack-safe in production even on multi-megapixel grids."}
        ]
    },
    "trace_table": {
        "input_desc": "image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2 (orig = 1)",
        "columns": ["Step", "Popped (r,c)", "Valid orig=1 Neighbors Painted -> 2", "Queue State"],
        "rows": [
            ["Init", "-", "Seed (1,1) -> 2", "[(1,1)]"],
            ["1", "(1,1)", "(0,1)->2, (1,0)->2 [(2,1)=0 & (1,2)=0 skipped]", "[(0,1), (1,0)]"],
            ["2", "(0,1)", "(0,0)->2, (0,2)->2", "[(1,0), (0,0), (0,2)]"],
            ["3", "(1,0)", "(2,0)->2 [(0,0) already 2]", "[(0,0), (0,2), (2,0)]"],
            ["4–6", "(0,0),(0,2),(2,0)", "None (bottom-right (2,2)=1 is diagonal-only!)", "[] -> Done"]
        ],
        "takeaway": "Pixel (2,2) stays 1 because flood fill is strictly 4-directional—diagonals do not connect!"
    },
    "stepper": {
        "title": "4-Directional Flood Fill Wavefront Stepper",
        "default_input": "example1",
        "presets": [
            {"label": "Example 1 (1 -> 2)", "onclick": 'loadPreset("ex1")'},
            {"label": "Same Color Guard (0 -> 0)", "onclick": 'loadPreset("same")'}
        ],
        "metrics": [
            {"id": "m-orig", "label": "Orig -> New", "color": "text-amber-400", "initial": "1 -> 2"},
            {"id": "m-curr", "label": "Active Cell", "color": "text-cyan-400", "initial": "(1, 1)"},
            {"id": "m-q", "label": "Queue Size", "color": "text-purple-400", "initial": "1"},
            {"id": "m-painted", "label": "Status", "color": "text-emerald-400", "initial": "Ready"}
        ],
        "visual_html": '<div id="p119-stage" class="flex flex-col items-center justify-center gap-1.5"></div>',
        "initial_narration": "Watch 4-directional BFS paint connected pixels while isolating diagonal (2,2).",
        "pseudocode_lines": [
            "val orig = image[sr][sc]; if (orig == color) return image",
            "val q = ArrayDeque<IntArray>(); image[sr][sc] = color; q.addLast(intArrayOf(sr, sc))",
            "while (q.isNotEmpty()) {",
            "  val (r, c) = q.removeFirst()",
            "  for ((dr, dc) in DIRS4) if (inBounds && image[nr][nc] == orig) {",
            "    image[nr][nc] = color; q.addLast(intArrayOf(nr, nc))",
            "  }",
            "}"
        ],
        "js_code": """
    function buildSteps(mode) {
      steps = [];
      if (mode === 'same') {
        const g = [[0,0,0],[0,0,0]];
        steps.push({ grid: g, curr: [0,0], orig: '0 -> 0', qLen: 0, status: 'Early Exit!', line: 1, narration: 'orig (0) == color (0) -> returns immediately in O(1) without infinite loop!' });
        return;
      }
      let g = [[1,1,1],[1,1,0],[1,0,1]];
      const clone = () => g.map(r => r.slice());
      const q = [[1,1]];
      g[1][1] = 2;
      steps.push({ grid: clone(), curr: [1,1], orig: '1 -> 2', qLen: 1, status: 'Seeded (1,1)', line: 2, narration: 'orig=1 != 2. Painted seed (1,1)=2 and pushed to queue.' });
      const dirs = [[-1,0],[1,0],[0,-1],[0,1]];
      while (q.length) {
        const [r, c] = q.shift();
        for (const [dr, dc] of dirs) {
          const nr = r + dr, nc = c + dc;
          if (nr >= 0 && nr < 3 && nc >= 0 && nc < 3 && g[nr][nc] === 1) {
            g[nr][nc] = 2;
            q.push([nr, nc]);
          }
        }
        steps.push({ grid: clone(), curr: [r,c], orig: '1 -> 2', qLen: q.length, status: q.length ? 'Expanding' : 'Complete', line: 6, narration: `Expanded (${r},${c}); painted 4-dir neighbors with value 1. Queue size=${q.length}.` });
      }
    }
    function renderStage(step) {
      const el = document.getElementById('p119-stage');
      if (!el) return;
      el.innerHTML = step.grid.map((row, r) => `<div class="flex gap-1.5">` + row.map((v, c) => {
        const active = step.curr && step.curr[0] === r && step.curr[1] === c;
        let cls = v === 2 ? 'bg-emerald-950/70 border-emerald-400 text-emerald-300' : (v === 1 ? 'bg-amber-950/40 border-amber-700/60 text-amber-200' : 'bg-slate-900 border-slate-800 text-slate-500');
        if (active) cls += ' ring-2 ring-cyan-400';
        return `<div class="w-11 h-11 rounded-lg border flex items-center justify-center font-mono font-bold text-sm ${cls}">${v}</div>`;
      }).join('') + `</div>`).join('');
    }
    function loadPreset(m) { buildSteps(m); stepperReset(); }
        """,
        "js_init_call": 'buildSteps("ex1");',
        "js_update_metrics": """
        document.getElementById('m-orig').textContent = step.orig;
        document.getElementById('m-curr').textContent = `(${step.curr[0]}, ${step.curr[1]})`;
        document.getElementById('m-q').textContent = step.qLen;
        document.getElementById('m-painted').textContent = step.status;
        """
    },
    "approaches": [
        {
            "name": "Recursive 4-Directional DFS",
            "badge": "Clean",
            "desc": "Guards orig == color, then recursively paints 4-directional matching neighbors.",
            "time": "O(R · C)",
            "space": "O(R · C) stack",
            "code_kotlin": """class Solution {
    fun floodFill(image: Array<IntArray>, sr: Int, sc: Int, color: Int): Array<IntArray> {
        val orig = image[sr][sc]
        if (orig == color) return image
        fun dfs(r: Int, c: Int) {
            if (r < 0 || r >= image.size || c < 0 || c >= image[0].size || image[r][c] != orig) return
            image[r][c] = color
            dfs(r - 1, c)
            dfs(r + 1, c)
            dfs(r, c - 1)
            dfs(r, c + 1)
        }
        dfs(sr, sc)
        return image
    }
}""",
            "code_python": """class Solution:
    def floodFill(self, image: list[list[int]], sr: int, sc: int, color: int) -> list[list[int]]:
        orig = image[sr][sc]
        if orig == color:
            return image
        rows, cols = len(image), len(image[0])

        def dfs(r: int, c: int) -> None:
            if r < 0 or r >= rows or c < 0 or c >= cols or image[r][c] != orig:
                return
            image[r][c] = color
            dfs(r - 1, c)
            dfs(r + 1, c)
            dfs(r, c - 1)
            dfs(r, c + 1)

        dfs(sr, sc)
        return image""",
            "code_java": """class Solution {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int orig = image[sr][sc];
        if (orig == color) return image;
        dfs(image, sr, sc, orig, color);
        return image;
    }

    private void dfs(int[][] image, int r, int c, int orig, int color) {
        if (r < 0 || r >= image.length || c < 0 || c >= image[0].length || image[r][c] != orig) return;
        image[r][c] = color;
        dfs(image, r - 1, c, orig, color);
        dfs(image, r + 1, c, orig, color);
        dfs(image, r, c - 1, orig, color);
        dfs(image, r, c + 1, orig, color);
    }
}"""
        },
        {
            "name": "Optimal: Iterative Queue BFS (Stack-Safe)",
            "badge": "Optimal",
            "desc": "Paints cells on enqueue using an explicit ArrayDeque/deque so deep 2000x2000 grids never overflow the call stack.",
            "time": "O(R · C)",
            "space": "O(R · C)",
            "code_kotlin": """import java.util.ArrayDeque

class Solution {
    fun floodFill(image: Array<IntArray>, sr: Int, sc: Int, color: Int): Array<IntArray> {
        val orig = image[sr][sc]
        if (orig == color) return image
        val rows = image.size
        val cols = image[0].size
        val q = ArrayDeque<IntArray>()
        image[sr][sc] = color
        q.addLast(intArrayOf(sr, sc))
        val dirs = arrayOf(intArrayOf(-1, 0), intArrayOf(1, 0), intArrayOf(0, -1), intArrayOf(0, 1))
        while (q.isNotEmpty()) {
            val curr = q.removeFirst()
            val r = curr[0]
            val c = curr[1]
            for (d in dirs) {
                val nr = r + d[0]
                val nc = c + d[1]
                if (nr in 0 until rows && nc in 0 until cols && image[nr][nc] == orig) {
                    image[nr][nc] = color
                    q.addLast(intArrayOf(nr, nc))
                }
            }
        }
        return image
    }
}""",
            "code_python": """from collections import deque

class Solution:
    def floodFill(self, image: list[list[int]], sr: int, sc: int, color: int) -> list[list[int]]:
        orig = image[sr][sc]
        if orig == color:
            return image
        rows, cols = len(image), len(image[0])
        q = deque([[sr, sc]])
        image[sr][sc] = color
        dirs = [[-1, 0], [1, 0], [0, -1], [0, 1]]
        while q:
            curr = q.popleft()
            r, c = curr[0], curr[1]
            for d in dirs:
                nr, nc = r + d[0], c + d[1]
                if 0 <= nr < rows and 0 <= nc < cols and image[nr][nc] == orig:
                    image[nr][nc] = color
                    q.append([nr, nc])
        return image""",
            "code_java": """import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int orig = image[sr][sc];
        if (orig == color) return image;
        int rows = image.length, cols = image[0].length;
        Deque<int[]> q = new ArrayDeque<>();
        image[sr][sc] = color;
        q.addLast(new int[]{sr, sc});
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!q.isEmpty()) {
            int[] curr = q.removeFirst();
            for (int[] d : dirs) {
                int nr = curr[0] + d[0], nc = curr[1] + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && image[nr][nc] == orig) {
                    image[nr][nc] = color;
                    q.addLast(new int[]{nr, nc});
                }
            }
        }
        return image;
    }
}"""
        }
    ],
    "test_cases": [
        {"input": [[[1, 1, 1], [1, 1, 0], [1, 0, 1]], 1, 1, 2], "expected": [[2, 2, 2], [2, 2, 0], [2, 0, 1]]},
        {"input": [[[0, 0, 0], [0, 0, 0]], 0, 0, 0], "expected": [[0, 0, 0], [0, 0, 0]]},
        {"input": [[[3]], 0, 0, 9], "expected": [[9]]}
    ],
    "spoken_script": {
        "phase1": "I'll confirm connectivity is 4-directional (not 8-directional) and check the key edge case: what if image[sr][sc] already equals color? In that case the image is unchanged and we must return immediately.",
        "phase2": "Once orig != color is guarded, recoloring a pixel from orig to color automatically marks it visited in-place. While recursive DFS is short for 50x50, iterative Queue BFS avoids JVM/Python call-stack overflow on large production images.",
        "phase3": "I recolor (sr, sc) before enqueuing—and recolor every valid neighbor right when enqueuing rather than on dequeue—so no pixel is ever pushed into the queue twice.",
        "phase4": "Testing Example 2 ([[0,0,0],[0,0,0]], color=0): orig == color triggers line 1 and returns in O(1). Testing Example 1: (2,2) is only diagonally adjacent to 1s, so it remains 1.",
        "mid_followup": "In a real photo editor supporting Undo/Redo on 4K canvases, instead of mutating the shared image buffer directly, write into copy-on-write 64x64 tiles or record the bounding box + bitmask of flipped pixels."
    },
    "quiz_questions": [
        {
            "category": "Infinite Recursion Trap",
            "prompt": "What happens in in-place Flood Fill if `image[sr][sc] == color` and you omit the `orig == color` early return?",
            "codeSnippet": "fun dfs(r: Int, c: Int) {\n    if (!inBounds(r, c) || image[r][c] != orig) return\n    image[r][c] = color\n    dfs(r + 1, c); dfs(r - 1, c) ...\n}",
            "options": [
                {"id": "A", "text": "Adjacent matching pixels infinitely re-visit each other until StackOverflowError or memory exhaustion.", "isCorrect": True, "whyRight": "Setting image[r][c] = color leaves image[r][c] == orig still true, so visited marking never takes effect!"},
                {"id": "B", "text": "Only the starting pixel is painted and none of its neighbors are visited.", "isCorrect": False, "whyWrong": "Neighbors also equal orig, so recursion bounces back and forth endlessly."},
                {"id": "C", "text": "Diagonal pixels get painted alongside 4-directional neighbors.", "isCorrect": False, "whyWrong": "Direction vectors remain 4-directional."},
                {"id": "D", "text": "The code still finishes in O(R * C) time with twice as many comparisons.", "isCorrect": False, "whyWrong": "Without a visited change, any 2-pixel component cycles forever."}
            ]
        },
        {
            "category": "BFS Enqueue vs Dequeue Discipline",
            "prompt": "In iterative BFS Flood Fill, why should `image[nr][nc] = color` happen BEFORE `q.addLast(...)` rather than after `q.removeFirst()`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Painting on enqueue prevents multiple neighbors from pushing duplicate copies of the same cell.", "isCorrect": True, "whyRight": "In a dense grid, delaying visited marking until dequeue causes exponential/quadratic duplicate queue entries!"},
                {"id": "B", "text": "ArrayDeque throws NullPointerException if unmodified coordinates are added.", "isCorrect": False, "whyWrong": "ArrayDeque accepts any non-null coordinate array."},
                {"id": "C", "text": "Painting on dequeue changes 4-directional traversal into 8-directional traversal.", "isCorrect": False, "whyWrong": "Reachability stays identical; only duplicate queue work explodes."},
                {"id": "D", "text": "BFS only visits top and left neighbors unless cells are painted before enqueue.", "isCorrect": False, "whyWrong": "All 4 directions are still checked."}
            ]
        },
        {
            "category": "Production Call-Stack Trade-Off",
            "prompt": "Why does an L4 engineer prefer iterative BFS over recursive DFS if image dimensions scale to `2000 x 2000`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Recursive DFS can hit 4,000,000 frames on a snake-like region, overflowing thread stack limits.", "isCorrect": True, "whyRight": "Default JVM (~1MB) and Python (1,000 recursion limit) stacks crash long before 4M frames, whereas heap queues scale cleanly."},
                {"id": "B", "text": "Recursive DFS has O(R^2 * C^2) asymptotic time complexity on large grids.", "isCorrect": False, "whyWrong": "Both DFS and BFS run in O(R * C) time."},
                {"id": "C", "text": "Iterative BFS uses zero heap memory regardless of grid width or height.", "isCorrect": False, "whyWrong": "The BFS frontier can hold O(min(R, C)) or O(R * C) coordinates on the heap."},
                {"id": "D", "text": "Recursive DFS cannot check boundary conditions on rectangular matrices.", "isCorrect": False, "whyWrong": "Bounds checks are identical in DFS and BFS."}
            ]
        },
        {
            "category": "Pattern Bridge (#733 -> #200)",
            "prompt": "How does Number of Islands (#200) build directly on Flood Fill (#733)?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "It scans every cell and triggers one Flood Fill ('1' -> '0') per unvisited land component.", "isCorrect": True, "whyRight": "Each outer discovery increments islands++ and sinks the entire connected component via Flood Fill."},
                {"id": "B", "text": "It replaces 4-directional traversal with Dijkstra's weighted priority queue.", "isCorrect": False, "whyWrong": "Number of Islands is unweighted 4-directional component counting."},
                {"id": "C", "text": "It sorts each row of the grid before checking adjacent overlaps.", "isCorrect": False, "whyWrong": "Sorting rows destroys 2D column adjacency."},
                {"id": "D", "text": "It uses binary search to locate the single largest island pixel.", "isCorrect": False, "whyWrong": "Grid land distribution is not monotonic."}
            ]
        }
    ]
}


p120 = {
    "num": 120,
    "lc": 703,
    "title": "Kth Largest Element in a Stream",
    "category": "Heaps & Priority Queues",
    "difficulty": "Easy",
    "pass_label": "Pass 1",
    "lc_slug": "kth-largest-integer-in-a-stream",
    "nc_url": "https://neetcode.io/problems/kth-largest-integer-in-a-stream/question?list=allNC",
    "prev_file": "0119-lc733-flood-fill.html",
    "next_file": "0121-lc40-combination-sum-ii.html",
    "method_name": "add",
    "verbatim_statement": (
        "Design a class to find the <code>k<sup>th</sup></code> largest element in a stream. Note that it is the "
        "<code>k<sup>th</sup></code> largest element in the sorted order, not the <code>k<sup>th</sup></code> distinct element.<br><br>"
        "Implement <code>KthLargest</code>:<br>"
        "&bull; <code>KthLargest(int k, int[] nums)</code> Initializes the object with the integer <code>k</code> and initial stream <code>nums</code>.<br>"
        "&bull; <code>int add(int val)</code> Appends <code>val</code> to the stream and returns the current <code>k<sup>th</sup></code> largest element."
    ),
    "examples": [
        {"title": "Example 1", "input": "KthLargest(3, [4, 5, 8, 2]); add(3); add(5); add(10); add(9); add(4)", "output": "4, 5, 5, 8, 8"}
    ],
    "constraints": [
        "1 <= k <= 10^4",
        "0 <= nums.length <= 10^4 (initial array may have k - 1 elements!)",
        "It is guaranteed at least k elements exist when add() is called."
    ],
    "plain_english": (
        "Maintain a VIP club with capacity `k` storing the `k` largest numbers seen so far. Keep a **Min-Heap** of size `k` "
        "so the *weakest* VIP (`k`-th largest overall!) always stands right at the door (`peek()`): any newcomer smaller than "
        "the weakest VIP bounces off; any larger newcomer evicts the weakest VIP in `O(log k)`."
    ),
    "boundary_checks": {
        "min_base": "k = 1, nums = [] -> Constructor starts with empty heap; first add(5) pushes 5 and returns 5.",
        "best_exit": "Heap already has k items and val <= minHeap.peek() -> can skip or push+pop, root stays unchanged.",
        "worst_scan": "10^4 calls with k = 10^4 -> Each add() runs in O(log k) time and O(k) total memory."
    },
    "mental_model": {
        "analogy_title": "The K-Seat VIP Lounge Bouncer (Min-Heap, Not Max-Heap!)",
        "analogy_text": (
            "Instinct screams 'Largest -> Max-Heap!'—that's the classic trap. If you keep only the top K champions in a lounge, "
            "who gets kicked out when a stronger challenger arrives? The *weakest* of those K champions! And which champion is "
            "the K-th largest overall? Also the *weakest* of those K champions! A Min-Heap capped at size K keeps that exact "
            "threshold element at O(1) peek() and evicts it in O(log K)."
        ),
        "aha_moment": "To track the K largest elements, use a MIN-Heap capped at size K: its minimum root IS the K-th largest element!",
        "trigger": "Streaming Top-K leaderboard thresholds, rolling percentile cutoffs, and Pass 1 warmup for #215 & #295.",
        "sisters": [
            {"name": "Kth Largest Element in an Array", "lc": "#215"},
            {"name": "Find Median from Data Stream", "lc": "#295"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(M · N log N) or O(M · N) Time",
        "naive_text": "Append every incoming val to an ArrayList and re-sort (or binary-insert shift) on every add().",
        "wasted_work": "Elements smaller than the K-th largest can NEVER become the K-th largest later because the stream only grows—storing and shifting them is pure waste.",
        "optimal_complexity": "O(N log K) Init, O(log K) per add(), O(K) Space",
        "optimal_text": "Maintain a PriorityQueue (Min-Heap) of size <= K: offer(val), if (pq.size > k) pq.poll(), return pq.peek().",
        "core_invariant": "After every operation with >= k stream items, minHeap contains the exact k largest values seen so far, and minHeap.peek() is the k-th largest.",
        "table_rows": [
            {"name": "Naive: Append + Sort Every add()", "time": "O(N log N) per add", "space": "O(N)", "note": "TLE across 10^4 stream events."},
            {"name": "Better: Max-Heap of All Elements", "time": "O(K log N) per add", "space": "O(N)", "note": "Must pop K-1 items and push them back on every read!"},
            {"name": "Optimal: Size-K Min-Heap", "time": "O(log K) per add", "space": "O(K)", "note": "★ Bounded O(K) memory even on infinite streams."}
        ]
    },
    "trace_table": {
        "input_desc": "k = 3, nums = [4, 5, 8, 2], stream = [3, 5, 10, 9, 4]",
        "columns": ["Call", "Min-Heap (Top K=3)", "Evicted", "peek() (3rd Largest)"],
        "rows": [
            ["init([4,5,8,2])", "[4, 5, 8]", "2", "4"],
            ["add(3)", "[4, 5, 8]", "3", "4"],
            ["add(5)", "[5, 5, 8]", "4", "5 (duplicates count!)"],
            ["add(10)", "[5, 8, 10]", "5", "5"],
            ["add(9)", "[8, 9, 10]", "5", "8"],
            ["add(4)", "[8, 9, 10]", "4", "8"]
        ],
        "takeaway": "Duplicate 5s occupy distinct seats in the K-seat lounge because the problem asks for sorted order, not distinct values."
    },
    "stepper": {
        "title": "Size-K Min-Heap Bouncer Visualizer",
        "default_input": "k=3, [4,5,8,2] + [3,5,10,9,4]",
        "presets": [
            {"label": "Official Stream (k=3)", "onclick": "loadPreset(3, [4,5,8,2], [3,5,10,9,4])"},
            {"label": "Empty Init (k=1)", "onclick": "loadPreset(1, [], [2, 7, 4])"}
        ],
        "metrics": [
            {"id": "m-call", "label": "Operation", "color": "text-amber-400", "initial": "init"},
            {"id": "m-size", "label": "Heap Size / K", "color": "text-cyan-400", "initial": "3 / 3"},
            {"id": "m-evict", "label": "Evicted", "color": "text-rose-400", "initial": "-"},
            {"id": "m-peek", "label": "Kth Largest", "color": "text-emerald-400", "initial": "4"}
        ],
        "visual_html": '<div id="p120-stage" class="flex flex-wrap items-center justify-center gap-2.5"></div>',
        "initial_narration": "Min-Heap keeps only the K largest elements; root (leftmost) is always the Kth largest.",
        "pseudocode_lines": [
            "class KthLargest(private val k: Int, nums: IntArray) {",
            "  private val minHeap = PriorityQueue<Int>()",
            "  init { for (x in nums) add(x) }",
            "  fun add(v: Int): Int {",
            "    if (minHeap.size < k) minHeap.offer(v) else if (v > minHeap.peek()) { minHeap.poll(); minHeap.offer(v) }",
            "    return minHeap.peek()",
            "  }",
            "}"
        ],
        "js_code": """
    function buildSteps(k, initNums, adds) {
      k = k || 3; initNums = initNums || [4,5,8,2]; adds = adds || [3,5,10,9,4];
      steps = [];
      let h = [];
      for (const x of initNums) { h.push(x); h.sort((a,b)=>a-b); if (h.length > k) h.shift(); }
      steps.push({ call: `init(k=${k})`, heap: h.slice(), k, ev: '-', peek: h.length ? h[0] : '-', line: 3, narration: `Initialized size-${k} Min-Heap: [${h.join(', ')}].` });
      for (const v of adds) {
        let ev = '-';
        if (h.length < k) { h.push(v); h.sort((a,b)=>a-b); }
        else if (v > h[0]) { ev = h.shift(); h.push(v); h.sort((a,b)=>a-b); }
        else { ev = v + ' (ignored)'; }
        steps.push({ call: `add(${v})`, heap: h.slice(), k, ev, peek: h[0], line: 5, narration: `add(${v}) -> heap=[${h.join(', ')}], root=${h[0]}.` });
      }
    }
    function renderStage(step) {
      const el = document.getElementById('p120-stage');
      if (!el) return;
      if (!step.heap.length) { el.innerHTML = '<span class="text-xs font-mono text-slate-500">Heap empty (waiting for first add)</span>'; return; }
      el.innerHTML = step.heap.map((v, idx) => `<div class="px-4 py-2.5 rounded-xl border font-mono text-xs flex flex-col items-center ${idx === 0 ? 'border-emerald-400 bg-emerald-950/60 text-emerald-300 ring-2 ring-emerald-500/30' : 'border-slate-700 bg-slate-900 text-slate-200'}"><span class="text-[10px] opacity-75">${idx === 0 ? 'ROOT (Kth)' : 'Top-' + step.k}</span><span class="font-bold text-base">${v}</span></div>`).join('');
    }
    function loadPreset(k, initNums, adds) { buildSteps(k, initNums, adds); stepperReset(); }
        """,
        "js_init_call": "buildSteps(3, [4,5,8,2], [3,5,10,9,4]);",
        "js_update_metrics": """
        document.getElementById('m-call').textContent = step.call;
        document.getElementById('m-size').textContent = `${step.heap.length} / ${step.k}`;
        document.getElementById('m-evict').textContent = step.ev;
        document.getElementById('m-peek').textContent = step.peek;
        """
    },
    "approaches": [
        {
            "name": "Naive: Append & Re-Sort List on Every Add",
            "badge": "Naive",
            "desc": "Stores all stream values and sorts on every add() in O(N log N) per query.",
            "time": "O(N log N) per add",
            "space": "O(N)",
            "code_kotlin": """class KthLargest(private val k: Int, nums: IntArray) {
    private val list = nums.toMutableList()

    fun add(v: Int): Int {
        list.add(v)
        list.sort()
        return list[list.size - k]
    }
}""",
            "code_python": """class KthLargest:
    def __init__(self, k: int, nums: list[int]):
        self.k = k
        self.arr = list(nums)

    def add(self, val: int) -> int:
        self.arr.append(val)
        self.arr.sort()
        return self.arr[len(self.arr) - self.k]""",
            "code_java": """import java.util.*;

class KthLargest {
    private final int k;
    private final List<Integer> list = new ArrayList<>();

    public KthLargest(int k, int[] nums) {
        this.k = k;
        for (int x : nums) list.add(x);
    }

    public int add(int val) {
        list.add(val);
        Collections.sort(list);
        return list.get(list.size() - k);
    }
}"""
        },
        {
            "name": "Optimal: Bounded Size-K Min-Heap",
            "badge": "Optimal",
            "desc": "Maintains only the k largest elements in a Min-Heap; only mutates the heap when size < k or val > peek().",
            "time": "O(log K) per add",
            "space": "O(K)",
            "code_kotlin": """import java.util.PriorityQueue

class KthLargest(private val k: Int, nums: IntArray) {
    private val minHeap = PriorityQueue<Int>()

    init {
        for (x in nums) {
            add(x)
        }
    }

    fun add(v: Int): Int {
        if (minHeap.size < k) {
            minHeap.offer(v)
        } else if (v > minHeap.peek()) {
            minHeap.poll()
            minHeap.offer(v)
        }
        return minHeap.peek()
    }
}""",
            "code_python": """import heapq

class KthLargest:
    def __init__(self, k: int, nums: list[int]):
        self.k = k
        self.heap = []
        for x in nums:
            self.add(x)

    def add(self, val: int) -> int:
        if len(self.heap) < self.k:
            heapq.heappush(self.heap, val)
        elif val > self.heap[0]:
            heapq.heappop(self.heap)
            heapq.heappush(self.heap, val)
        return self.heap[0]""",
            "code_java": """import java.util.PriorityQueue;

class KthLargest {
    private final int k;
    private final PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    public KthLargest(int k, int[] nums) {
        this.k = k;
        for (int x : nums) {
            add(x);
        }
    }

    public int add(int val) {
        if (minHeap.size() < k) {
            minHeap.offer(val);
        } else if (val > minHeap.peek()) {
            minHeap.poll();
            minHeap.offer(val);
        }
        return minHeap.peek();
    }
}"""
        }
    ],
    "test_cases": [
        {
            "mode": "oop",
            "ops": ["KthLargest", "add", "add", "add", "add", "add"],
            "args": [[3, [4, 5, 8, 2]], [3], [5], [10], [9], [4]],
            "expected": [None, 4, 5, 5, 8, 8]
        },
        {
            "mode": "oop",
            "ops": ["KthLargest", "add", "add", "add"],
            "args": [[1, []], [-3], [-2], [-4]],
            "expected": [None, -3, -2, -2]
        },
        {
            "mode": "oop",
            "ops": ["KthLargest", "add", "add"],
            "args": [[2, [0]], [-1], [1]],
            "expected": [None, -1, 0]
        }
    ],
    "spoken_script": {
        "phase1": "Notice two key details in the spec: first, we want the k-th largest in sorted order including duplicates; second, initial nums can have k - 1 elements (even empty when k = 1), only guaranteeing >= k elements once add() is called.",
        "phase2": "Since the stream is append-only, any number smaller than the current k-th largest can never enter the top k later. Keeping a Min-Heap capped at size k bounds space at O(k) and exposes the k-th largest at the root in O(1).",
        "phase3": "In init, I route every element through add(x)—or guarded offer—and inside add(val), if size < k I offer(val); else if val > peek(), I poll and offer(val), avoiding unnecessary heap sifts when val <= peek().",
        "phase4": "Tracing k=1, nums=[]: constructor leaves minHeap empty without calling peek() prematurely; add(-3) pushes -3 and returns -3.",
        "mid_followup": "If elements can ALSO expire or be deleted (sliding window K-th largest), a single Min-Heap isn't enough because shrunk top-K needs promotion from the rank-(K+1) runner-up; pair two heaps (or two TreeMaps / Order-Statistic Tree) like #295/#480 with lazy deletion."
    },
    "quiz_questions": [
        {
            "category": "Constructor Edge-Case Bug",
            "prompt": "Why does calling `minHeap.peek()` inside `init` crash on valid LeetCode #703 inputs?",
            "codeSnippet": "init {\n    for (x in nums) minHeap.offer(x)\n    while (minHeap.size > k) minHeap.poll()\n    cachedKth = minHeap.peek() // <-- BUG HERE\n}",
            "options": [
                {"id": "A", "text": "nums can initially have k - 1 elements (e.g., k=1, nums=[]), making minHeap.peek() null.", "isCorrect": True, "whyRight": "The problem guarantees >= k elements only at the first add() call, not during construction!"},
                {"id": "B", "text": "PriorityQueue forbids calling peek() inside Kotlin init blocks.", "isCorrect": False, "whyWrong": "peek() is legal whenever the queue is non-empty."},
                {"id": "C", "text": "Polling inside a while loop corrupts binary heap order.", "isCorrect": False, "whyWrong": "poll() preserves the heap invariant."},
                {"id": "D", "text": "Java/Kotlin PriorityQueue defaults to a Max-Heap unless reversed.", "isCorrect": False, "whyWrong": "PriorityQueue defaults to a Min-Heap (natural ascending order)."}
            ]
        },
        {
            "category": "Heap Polarity Intuition",
            "prompt": "Why do we use a **Min-Heap** instead of a **Max-Heap** to track the `k` largest elements?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "A size-k Min-Heap keeps the smallest of the top k (the k-th largest!) at O(1) root for instant eviction.", "isCorrect": True, "whyRight": "Both queries (what is the k-th largest?) and evictions (drop the k+1-th largest) happen at the minimum end of the top-k set."},
                {"id": "B", "text": "Max-Heaps cannot store duplicate integers in Java, Kotlin, or Python.", "isCorrect": False, "whyWrong": "Both Min-Heaps and Max-Heaps allow duplicate elements."},
                {"id": "C", "text": "Min-Heap insertions run in O(1) worst-case time whereas Max-Heap insertions take O(N).", "isCorrect": False, "whyWrong": "Both heaps take O(log H) worst-case time per insertion."},
                {"id": "D", "text": "A size-k Max-Heap always stores the k-th largest element at index 0.", "isCorrect": False, "whyWrong": "A Max-Heap stores the 1st largest (global maximum) at index 0, hiding the k-th largest at a leaf."}
            ]
        },
        {
            "category": "Fast-Reject Optimization",
            "prompt": "When `minHeap.size == k` and incoming `val <= minHeap.peek()`, what is the fastest correct action?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Skip modifying the heap completely and return minHeap.peek() in O(1) time.", "isCorrect": True, "whyRight": "Offering and immediately polling `val` wastes O(log k) sifting work to arrive at the exact same heap."},
                {"id": "B", "text": "Poll the root and discard both the root and val.", "isCorrect": False, "whyWrong": "That shrinks the heap below k and loses the true k-th largest."},
                {"id": "C", "text": "Clear and rebuild the heap from scratch.", "isCorrect": False, "whyWrong": "Clearing destroys all state."},
                {"id": "D", "text": "Return val because it is the new minimum.", "isCorrect": False, "whyWrong": "We must return the k-th largest, not the global minimum."}
            ]
        },
        {
            "category": "Requirement Pivot (Deletions)",
            "prompt": "Why does a single size-`k` Min-Heap fail if the stream also supports `remove(val)`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Discarded elements below the top k are gone, so removing a top-k item has no (k+1)-th replacement.", "isCorrect": True, "whyRight": "Once elements can leave the top k, you must retain smaller elements in a companion Max-Heap or TreeMap."},
                {"id": "B", "text": "PriorityQueue throws UnsupportedOperationException if size ever decreases.", "isCorrect": False, "whyWrong": "poll() decreases size normally; the issue is missing discarded runner-up elements."},
                {"id": "C", "text": "Removing an element turns a Min-Heap into an unsorted linked list.", "isCorrect": False, "whyWrong": "Heap structure remains intact, just missing the next candidate."},
                {"id": "D", "text": "Duplicates become impossible to represent once remove(val) exists.", "isCorrect": False, "whyWrong": "Frequency maps or multisets track duplicates cleanly."}
            ]
        }
    ]
}


p121 = {
    "num": 121,
    "lc": 40,
    "title": "Combination Sum II",
    "category": "Backtracking",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "combination-target-sum-ii",
    "nc_url": "https://neetcode.io/problems/combination-target-sum-ii/question?list=allNC",
    "prev_file": "0120-lc703-kth-largest-element-in-a-stream.html",
    "next_file": "0122-lc17-letter-combinations-of-a-phone-number.html",
    "method_name": "combinationSum2",
    "verbatim_statement": (
        "Given a collection of candidate numbers (<code>candidates</code>) and a target number (<code>target</code>), "
        "find all unique combinations in <code>candidates</code> where the candidate numbers sum to <code>target</code>.<br><br>"
        "Each number in <code>candidates</code> may only be used <strong>once</strong> in the combination.<br>"
        "<strong>Note:</strong> The solution set must not contain duplicate combinations."
    ),
    "examples": [
        {"title": "Example 1", "input": "candidates = [10,1,2,7,6,1,5], target = 8", "output": "[[1,1,6],[1,2,5],[1,7],[2,6]]"},
        {"title": "Example 2", "input": "candidates = [2,5,2,1,2], target = 5", "output": "[[1,2,2],[5]]"}
    ],
    "constraints": [
        "1 <= candidates.length <= 100",
        "1 <= candidates[i] <= 50",
        "1 <= target <= 30"
    ],
    "plain_english": (
        "Unlike Combination Sum I (#39), `candidates` contains duplicate values AND each array slot can be used at most once. "
        "Sort first, advance recursion to `i + 1`, and skip equal values at the **same** decision depth (`if (i > start && candidates[i] == candidates[i - 1]) continue`)."
    ),
    "boundary_checks": {
        "min_base": "candidates = [5], target = 5 -> Single exact hit returns [[5]].",
        "best_exit": "Sorted pruning: as soon as candidates[i] > rem, `break` (not just `continue`) because all later elements are >= candidates[i]!",
        "worst_scan": "All 1s [1,1,...,1] (30 copies), target = 15 -> Without sibling pruning, C(30,15) = 155,117,520 branches TLE; with sibling pruning, only 30 linear steps!"
    },
    "mental_model": {
        "analogy_title": "Twin Siblings at the Same Fork vs. Parent-and-Child Steps",
        "analogy_text": (
            "Sort `[1a, 1b, 2, 5, 6, 7, 10]`. At the root fork (`start = 0`), picking `1a` explores EVERY combination that begins with a `1` "
            "(including `[1a, 1b, 6]`, because at the child fork `start = 1`, `i = 1 == start` IS allowed!). "
            "When we backtrack to the root fork (`start = 0`) and reach `i = 1` (`1b`), picking `1b` as our first element would only "
            "re-generate subsets of combos already found under `1a`! Skipping `i > start && a[i] == a[i - 1]` blocks twin siblings at the same depth while allowing twins down a chain."
        ),
        "aha_moment": "`i > start && candidates[i] == candidates[i - 1]` is the surgical invariant: `i == start` allows using a second copy as a child; `i > start` blocks re-picking the same value as a sibling!",
        "trigger": "Subsets II (#90), Combination Sum II (#40), and 3Sum (#15) — any unique-tuple search over inputs with duplicate numbers.",
        "sisters": [
            {"name": "Combination Sum", "lc": "#39"},
            {"name": "3Sum", "lc": "#15"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(2^N) Time (TLE on Repeated Values)",
        "naive_text": "Explore all 2^N subsets without pruning duplicates, then deduplicate at the leaves with a HashSet<List<Int>>.",
        "wasted_work": "On thirty 1s with target=15, generating 155 million identical [1,1,...,1] leaves before HashSet deduplication times out.",
        "optimal_complexity": "O(2^N) Worst-Case, Exponentially Pruned in Practice",
        "optimal_text": "Sort candidates ascending; inside dfs(start, rem), `break` if candidates[i] > rem, `continue` if i > start && candidates[i] == candidates[i - 1], and recurse on i + 1.",
        "core_invariant": "At recursion frame start, each distinct value v in candidates[start..N-1] is branched at most once (at its earliest index >= start).",
        "table_rows": [
            {"name": "Naive: Brute 2^N + HashSet Dedupe", "time": "O(N · 2^N)", "space": "O(2^N)", "note": "TLE on [1,1,...,1] (100 ones) hidden LeetCode test!"},
            {"name": "Optimal: Sort + Sibling Skip + Early Break", "time": "O(2^N) tight", "space": "O(target) stack", "note": "★ Collapses [1]*100 from 2^100 down to O(target) calls."}
        ]
    },
    "trace_table": {
        "input_desc": "candidates = [1, 1, 2, 5] (sorted), target = 3",
        "columns": ["Call (start, rem)", "i & val", "Guard Check", "Path", "Action"],
        "rows": [
            ["dfs(0, 3)", "i=0 (val=1)", "i == start (OK)", "[1]", "Recurse dfs(1, 2)"],
            ["  dfs(1, 2)", "i=1 (val=1)", "i == start (OK!)", "[1, 1]", "Recurse dfs(2, 1) -> i=2 (2 > 1) break"],
            ["  dfs(1, 2)", "i=2 (val=2)", "rem == 2 -> 0!", "[1, 2]", "Record [1, 2]! Next i=3 (5 > 2) break"],
            ["dfs(0, 3)", "i=1 (val=1)", "i > 0 && a[1]==a[0]", "[]", "SKIP duplicate sibling '1'!"],
            ["dfs(0, 3)", "i=2 (val=2)", "2 <= 3 (OK)", "[2]", "Recurse dfs(3, 1) -> i=3 (5 > 1) break"]
        ],
        "takeaway": "Notice `i=1` WAS explored inside `dfs(1, 2)` (`i == start`), and SKIPPED inside `dfs(0, 3)` (`i > start`)."
    },
    "stepper": {
        "title": "Sorted Backtracking & Same-Depth Sibling Pruner",
        "default_input": "[10,1,2,7,6,1,5], 8",
        "presets": [
            {"label": "[1,1,2,5], target=4", "onclick": "loadPreset([1,1,2,5], 4)"},
            {"label": "[2,5,2,1,2], target=5", "onclick": "loadPreset([2,5,2,1,2], 5)"}
        ],
        "metrics": [
            {"id": "m-rem", "label": "Remaining", "color": "text-amber-400", "initial": "4"},
            {"id": "m-path", "label": "Current Path", "color": "text-cyan-400", "initial": "[]"},
            {"id": "m-pruned", "label": "Siblings Skipped", "color": "text-rose-400", "initial": "0"},
            {"id": "m-found", "label": "Combos Found", "color": "text-emerald-400", "initial": "0"}
        ],
        "visual_html": '<div id="p121-stage" class="flex flex-col items-center gap-3 w-full"></div>',
        "initial_narration": "Watch sorted backtracking record valid combinations while skipping duplicate siblings.",
        "pseudocode_lines": [
            "candidates.sort()",
            "fun dfs(start: Int, rem: Int) {",
            "  if (rem == 0) { res.add(path.toList()); return }",
            "  for (i in start until candidates.size) {",
            "    if (candidates[i] > rem) break",
            "    if (i > start && candidates[i] == candidates[i - 1]) continue",
            "    path.add(candidates[i]); dfs(i + 1, rem - candidates[i]); path.removeLast()",
            "  }",
            "}"
        ],
        "js_code": """
    function buildSteps(cands, target) {
      cands = (cands || [1,1,2,5]).slice().sort((a,b)=>a-b);
      target = target || 4;
      steps = [];
      let path = [], res = [], skipped = 0;
      steps.push({ cands, start: 0, idx: -1, rem: target, path: [], res: [], skipped: 0, line: 1, narration: `Sorted candidates: [${cands.join(', ')}], target=${target}.` });
      function dfs(start, rem) {
        if (steps.length > 35) return;
        if (rem === 0) {
          res.push(path.slice());
          steps.push({ cands, start, idx: -1, rem: 0, path: path.slice(), res: res.map(x=>x.slice()), skipped, line: 3, narration: `rem == 0! Recorded [${path.join(', ')}].` });
          return;
        }
        for (let i = start; i < cands.length; i++) {
          if (cands[i] > rem) {
            steps.push({ cands, start, idx: i, rem, path: path.slice(), res: res.map(x=>x.slice()), skipped, line: 5, narration: `candidates[${i}]=${cands[i]} > rem (${rem}) -> break early!` });
            break;
          }
          if (i > start && cands[i] === cands[i - 1]) {
            skipped++;
            steps.push({ cands, start, idx: i, rem, path: path.slice(), res: res.map(x=>x.slice()), skipped, line: 6, narration: `i=${i} > start=${start} and ${cands[i]} == ${cands[i-1]} -> SKIP duplicate sibling!` });
            continue;
          }
          path.push(cands[i]);
          steps.push({ cands, start, idx: i, rem: rem - cands[i], path: path.slice(), res: res.map(x=>x.slice()), skipped, line: 7, narration: `Picked cands[${i}]=${cands[i]} -> path=[${path.join(', ')}], rem=${rem - cands[i]}.` });
          dfs(i + 1, rem - cands[i]);
          path.pop();
        }
      }
      dfs(0, target);
    }
    function renderStage(step) {
      const el = document.getElementById('p121-stage');
      if (!el) return;
      const row = step.cands.map((v, i) => {
        let cls = 'border-slate-700 bg-slate-900 text-slate-300';
        if (i === step.idx) cls = 'border-amber-400 bg-amber-950/50 text-amber-200 ring-2 ring-amber-400/40';
        else if (i < step.start) cls = 'border-slate-800 bg-slate-950 text-slate-600';
        return `<div class="px-3 py-1.5 rounded-lg border font-mono text-xs ${cls}"><span class="text-[10px] block opacity-60">i=${i}</span><b>${v}</b></div>`;
      }).join('');
      const found = step.res.length ? step.res.map(r => `[${r.join(',')}]`).join('  ') : 'none yet';
      el.innerHTML = `<div class="flex flex-wrap gap-2 justify-center">${row}</div><div class="text-xs font-mono text-emerald-300">Found: ${found}</div>`;
    }
    function loadPreset(c, t) { buildSteps(c, t); stepperReset(); }
        """,
        "js_init_call": "buildSteps([1,1,2,5], 4);",
        "js_update_metrics": """
        document.getElementById('m-rem').textContent = step.rem;
        document.getElementById('m-path').textContent = '[' + step.path.join(', ') + ']';
        document.getElementById('m-pruned').textContent = step.skipped;
        document.getElementById('m-found').textContent = step.res.length;
        """
    },
    "approaches": [
        {
            "name": "Frequency-Counter Group Backtracking",
            "badge": "Alternative",
            "desc": "Groups identical numbers into (value, count) pairs and chooses how many copies 0..count to include.",
            "time": "O(2^N)",
            "space": "O(N)",
            "code_kotlin": """class Solution {
    fun combinationSum2(candidates: IntArray, target: Int): List<List<Int>> {
        val freq = HashMap<Int, Int>()
        for (x in candidates) freq[x] = (freq[x] ?: 0) + 1
        val unique = freq.keys.toList().sorted()
        val res = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()

        fun dfs(idx: Int, rem: Int) {
            if (rem == 0) {
                res.add(path.toList())
                return
            }
            if (idx == unique.size || unique[idx] > rem) return
            val v = unique[idx]
            val maxCopies = minOf(freq[v] ?: 0, rem / v)
            for (cnt in 0..maxCopies) {
                for (k in 0 until cnt) path.add(v)
                dfs(idx + 1, rem - cnt * v)
                for (k in 0 until cnt) path.removeAt(path.size - 1)
            }
        }
        dfs(0, target)
        return res
    }
}""",
            "code_python": """from collections import Counter

class Solution:
    def combinationSum2(self, candidates: list[int], target: int) -> list[list[int]]:
        freq = Counter(candidates)
        unique = sorted(freq.keys())
        res = []
        path = []

        def dfs(idx: int, rem: int) -> None:
            if rem == 0:
                res.append(list(path))
                return
            if idx == len(unique) or unique[idx] > rem:
                return
            v = unique[idx]
            max_copies = min(freq[v], rem // v)
            for cnt in range(max_copies + 1):
                for _ in range(cnt):
                    path.append(v)
                dfs(idx + 1, rem - cnt * v)
                for _ in range(cnt):
                    path.pop()

        dfs(0, target)
        return res""",
            "code_java": """import java.util.*;

class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : candidates) freq.put(x, freq.getOrDefault(x, 0) + 1);
        List<Integer> unique = new ArrayList<>(freq.keySet());
        Collections.sort(unique);
        List<List<Integer>> res = new ArrayList<>();
        dfs(0, target, unique, freq, new ArrayList<>(), res);
        return res;
    }

    private void dfs(int idx, int rem, List<Integer> unique, Map<Integer, Integer> freq, List<Integer> path, List<List<Integer>> res) {
        if (rem == 0) { res.add(new ArrayList<>(path)); return; }
        if (idx == unique.size() || unique.get(idx) > rem) return;
        int v = unique.get(idx);
        int maxCopies = Math.min(freq.get(v), rem / v);
        for (int cnt = 0; cnt <= maxCopies; cnt++) {
            for (int k = 0; k < cnt; k++) path.add(v);
            dfs(idx + 1, rem - cnt * v, unique, freq, path, res);
            for (int k = 0; k < cnt; k++) path.remove(path.size() - 1);
        }
    }
}"""
        },
        {
            "name": "Optimal: Sort + Same-Depth Sibling Skip + Early Break",
            "badge": "Optimal",
            "desc": "Sorts in-place, breaks when candidates[i] > rem, and skips i > start && candidates[i] == candidates[i - 1].",
            "time": "O(2^N)",
            "space": "O(target) stack",
            "code_kotlin": """class Solution {
    fun combinationSum2(candidates: IntArray, target: Int): List<List<Int>> {
        candidates.sort()
        val res = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()

        fun dfs(start: Int, rem: Int) {
            if (rem == 0) {
                res.add(path.toList())
                return
            }
            for (i in start until candidates.size) {
                if (candidates[i] > rem) break
                if (i > start && candidates[i] == candidates[i - 1]) continue
                path.add(candidates[i])
                dfs(i + 1, rem - candidates[i])
                path.removeAt(path.size - 1)
            }
        }

        dfs(0, target)
        return res
    }
}""",
            "code_python": """class Solution:
    def combinationSum2(self, candidates: list[int], target: int) -> list[list[int]]:
        candidates.sort()
        res = []
        path = []

        def dfs(start: int, rem: int) -> None:
            if rem == 0:
                res.append(list(path))
                return
            for i in range(start, len(candidates)):
                if candidates[i] > rem:
                    break
                if i > start and candidates[i] == candidates[i - 1]:
                    continue
                path.append(candidates[i])
                dfs(i + 1, rem - candidates[i])
                path.pop()

        dfs(0, target)
        return res""",
            "code_java": """import java.util.*;

class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> res = new ArrayList<>();
        dfs(0, target, candidates, new ArrayList<>(), res);
        return res;
    }

    private void dfs(int start, int rem, int[] candidates, List<Integer> path, List<List<Integer>> res) {
        if (rem == 0) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > rem) break;
            if (i > start && candidates[i] == candidates[i - 1]) continue;
            path.add(candidates[i]);
            dfs(i + 1, rem - candidates[i], candidates, path, res);
            path.remove(path.size() - 1);
        }
    }
}"""
        }
    ],
    "test_cases": [
        {"input": [[10, 1, 2, 7, 6, 1, 5], 8], "expected": [[1, 1, 6], [1, 2, 5], [1, 7], [2, 6]], "unorderedInner": True},
        {"input": [[2, 5, 2, 1, 2], 5], "expected": [[1, 2, 2], [5]], "unorderedInner": True},
        {"input": [[1, 1, 1, 1], 2], "expected": [[1, 1]], "unorderedInner": True}
    ],
    "spoken_script": {
        "phase1": "Two critical differences from Combination Sum I (#39): first, each array index can be picked at most once (`dfs(i + 1, ...)` instead of `dfs(i, ...)`), and second, `candidates` contains duplicate values while output combinations must be unique.",
        "phase2": "Generating all 2^N subsets and deduplicating with a Set times out when `candidates` has dozens of repeated 1s. Sorting first unlocks two optimizations: early `break` when `candidates[i] > rem`, and sibling deduplication `if (i > start && candidates[i] == candidates[i - 1]) continue`.",
        "phase3": "Notice why `i > start` (not `i > 0`!) is the exact condition: when `i == start`, we are picking the first element at this new depth (allowing `[1, 1, 6]`), whereas when `i > start`, we just backtracked from an identical sibling at the exact same depth.",
        "phase4": "Tracing `[1, 1, 1, 1]` with `target = 2`: `i=0` picks `1`, child `i=1 == start` picks `1` -> records `[1, 1]`, sibling `i=2 > 1` skips, root `i=1,2,3 > 0` all skip!",
        "mid_followup": "If we only needed the *count* of valid combinations (or boolean feasibility) rather than enumerating every list, replace backtracking with 1D 0/1 Knapsack DP (`#416`) or grouped polynomial DP in `O(N * target)` time."
    },
    "quiz_questions": [
        {
            "category": "Classic Bug Hunt (`i > 0` vs `i > start`)",
            "prompt": "What goes wrong if you write `if (i > 0 && candidates[i] == candidates[i - 1]) continue` instead of `i > start`?",
            "codeSnippet": "for (i in start until candidates.size) {\n    if (i > 0 && candidates[i] == candidates[i - 1]) continue // <-- BUG!\n    path.add(candidates[i])\n    dfs(i + 1, rem - candidates[i])\n    path.removeLast()\n}",
            "options": [
                {"id": "A", "text": "Valid combinations using multiple copies like [1, 1, 6] are wrongly discarded!", "isCorrect": True, "whyRight": "Inside child frame start=1, i=1 has i > 0 true, so the second '1' can NEVER be chosen even down a single branch!"},
                {"id": "B", "text": "Duplicate combinations like two copies of [1, 7] leak into the result list.", "isCorrect": False, "whyWrong": "i > 0 over-prunes rather than under-prunes."},
                {"id": "C", "text": "candidates[i - 1] throws ArrayIndexOutOfBoundsException when start equals zero.", "isCorrect": False, "whyWrong": "i > 0 guards index i - 1 >= 0."},
                {"id": "D", "text": "Each candidate index gets reused infinitely many times like Combination Sum I.", "isCorrect": False, "whyWrong": "Recursion still advances to i + 1."}
            ]
        },
        {
            "category": "Sorted Early-Exit (`break` vs `continue`)",
            "prompt": "Because `candidates` is sorted ascending, why can we `break` (instead of `continue`) when `candidates[i] > rem`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "All candidates are positive and sorted, so every j > i also satisfies candidates[j] > rem.", "isCorrect": True, "whyRight": "Monotonicity + positive values guarantees no subsequent index in the loop can reduce rem to 0."},
                {"id": "B", "text": "Negative numbers later in the array are automatically moved before index i by sort().", "isCorrect": False, "whyWrong": "Constraints guarantee 1 <= candidates[i] <= 50 (strictly positive); if negatives existed, `candidates[i] > rem` could NOT break!"},
                {"id": "C", "text": "Breaking automatically pops the last element from path without calling removeLast.", "isCorrect": False, "whyWrong": "We haven't added candidates[i] to path yet when checking > rem."},
                {"id": "D", "text": "Continuing instead of breaking causes duplicate combinations to appear.", "isCorrect": False, "whyWrong": "Continue is still correct, just slower than break."}
            ]
        },
        {
            "category": "Snapshot Reference Bug",
            "prompt": "Why must base case `rem == 0` record `res.add(path.toList())` instead of `res.add(path)`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "path is mutated in-place during backtracking and ends empty, leaving res full of empty lists.", "isCorrect": True, "whyRight": "Adding the mutable reference aliases every entry in res to the same cleared list."},
                {"id": "B", "text": "toList() sorts the combination in descending order before storing it.", "isCorrect": False, "whyWrong": "toList() preserves insertion order."},
                {"id": "C", "text": "Kotlin and Java forbid adding a MutableList inside a List<List<Int>>.", "isCorrect": False, "whyWrong": "MutableList is a subtype of List; it compiles cleanly and fails silently at runtime!"},
                {"id": "D", "text": "Copying path reduces recursion stack depth from O(target) down to O(1).", "isCorrect": False, "whyWrong": "Stack depth depends on recursion chain length, not leaf copying."}
            ]
        },
        {
            "category": "Comparison (#39 vs #40)",
            "prompt": "Which two lines change when moving from Combination Sum I (`#39`, unlimited reuse, distinct input) to Combination Sum II (`#40`)?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Recurse with `i + 1` instead of `i`, and skip `i > start && a[i] == a[i - 1]` after sorting.", "isCorrect": True, "whyRight": "i + 1 enforces single-use per index, and sibling skip eliminates duplicate combinations from repeated values."},
                {"id": "B", "text": "Replace DFS backtracking with BFS queue traversal and remove sorting.", "isCorrect": False, "whyWrong": "Sorting + DFS remains the canonical template."},
                {"id": "C", "text": "Sort in descending order and recurse with `start - 1`.", "isCorrect": False, "whyWrong": "Ascending sort enables early break on > rem."},
                {"id": "D", "text": "Track a global visited BooleanArray across unrelated branches without unmarking.", "isCorrect": False, "whyWrong": "Never unmarking visited blocks elements from appearing in other valid combinations."}
            ]
        }
    ]
}
