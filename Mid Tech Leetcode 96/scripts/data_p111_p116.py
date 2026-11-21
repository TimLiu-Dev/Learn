"""
Curriculum definitions for Problems 111 to 116 (2027 Mid-Tech High-ROI Gap Additions):
#111: LC #528 Random Pick with Weight (Pass 2B Stretch Medium - Prefix Sum + Binary Search)
#112: LC #34 Find First and Last Position of Element in Sorted Array (Pass 2A Core Medium - Lower/Upper Bound Binary Search)
#113: LC #2 Add Two Numbers (Pass 2A Core Medium - Linked List Digit Addition & Carry Propagation)
#114: LC #143 Reorder List (Pass 2B Stretch Medium - Mid-Find + Reverse + Interleave Merge)
#115: LC #152 Maximum Product Subarray (Pass 2B Stretch Medium - Sign-Flip Dual-Extremum DP)
#116: LC #1091 Shortest Path in Binary Matrix (Pass 2B Stretch Medium - 8-Directional Grid Shortest Path BFS)
"""

# =========================================================================
# PROBLEM 111: LC #528 RANDOM PICK WITH WEIGHT (Pass 2B Stretch Medium)
# =========================================================================
p111 = {
    "num": 111,
    "lc": 528,
    "title": "Random Pick with Weight",
    "category": "Prefix Sum",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "random-pick-with-weight",
    "prev_file": "0110-lc91-decode-ways.html",
    "next_file": "0112-lc34-find-first-and-last-position-of-element-in-sorted-array.html",
    "verbatim_statement": "You are given a <strong>0-indexed</strong> array of positive integers <code>w</code> where <code>w[i]</code> describes the <strong>weight</strong> of the <code>i<sup>th</sup></code> index.<br><br>You need to implement the function <code>pickIndex()</code>, which <strong>randomly</strong> picks an index in the range <code>[0, w.length - 1]</code> (<strong>inclusive</strong>) and returns it. The <strong>probability</strong> of picking an index <code>i</code> is <code>w[i] / sum(w)</code>.<br><br>For example, if <code>w = [1, 3]</code>, the probability of picking index <code>0</code> is <code>1 / (1 + 3) = 0.25</code> (25%), and the probability of picking index <code>1</code> is <code>3 / (1 + 3) = 0.75</code> (75%).",
    "examples": [
        {"title": "Example 1", "input": "Solution([1]), pickIndex()", "output": "0 (100% probability)"},
        {"title": "Example 2", "input": "Solution([1, 3]), pickIndex() x4", "output": "[1, 1, 1, 0] (~75% 1s, ~25% 0s)"},
        {"title": "Example 3", "input": "Solution([1, 9]), pickIndex() x100", "output": "~10% index 0, ~90% index 1"}
    ],
    "constraints": [
        "1 <= w.length <= 10^4",
        "1 <= w[i] <= 10^5",
        "pickIndex will be called at most 10^4 times."
    ],
    "plain_english": "Given positive weights, build a weighted random sampler so each index is chosen proportionally to its share of the total weight—without duplicating elements in a giant array.",
    "boundary_checks": {
        "min_base": "w = [5] (Single element) -> Prefix sum is [5]; every random target in 1..5 maps to index 0.",
        "best_exit": "w = [1, 999] -> Target lands in [2..1000] 99.9% of the time; binary search resolves index 1 in O(log N).",
        "worst_scan": "w.length = 10^4, 10^4 calls -> Prefix build O(N) once; each pickIndex runs in O(log N) binary search."
    },
    "mental_model": {
        "analogy_title": "The Weighted Number-Line Dartboard",
        "analogy_text": "Imagine laying wooden planks end-to-end along a ruler, where plank i has length w[i]. For w = [1, 3, 2], plank 0 spans (0, 1], plank 1 spans (1, 4], and plank 2 spans (4, 6]. When you drop a dart at a random integer tick from 1 to 6, each plank captures an interval proportional to its weight. Because the plank endpoints [1, 4, 6] are strictly sorted, finding which plank caught your dart is a single binary search for the first endpoint >= dart!",
        "aha_moment": "Convert weights into a monotonically increasing Prefix Sum array, pick a uniform random integer target in 1..totalSum, and binary search for the leftmost index where prefix[mid] >= target.",
        "trigger": "Weighted random sampling, load balancing by server weight, or probabilistic ad selection.",
        "sisters": [
            {"name": "Subarray Sum Equals K", "lc": "#560"},
            {"name": "Binary Search", "lc": "#704"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(sum(w)) Space, O(1) Pick",
        "naive_text": "Expand an explicit bucket array repeating index i exactly w[i] times, then pick a random slot.",
        "wasted_work": "If w has 10^4 items of weight 10^5, the expanded array requires 10^9 integers (~4 GB RAM) and immediately hits Memory Limit Exceeded.",
        "optimal_complexity": "O(N) Init, O(log N) Pick, O(N) Space",
        "optimal_text": "Precompute prefix[i] = prefix[i - 1] + w[i]. On pickIndex(), draw target in [1..total] and run lower-bound binary search (first prefix[mid] >= target).",
        "core_invariant": "Because all w[i] >= 1, prefix[] is strictly increasing, making lower-bound binary search exact over [0..N-1].",
        "table_rows": [
            {"name": "Naive: Expanded Ticket Array", "time": "O(W) init / O(1) pick", "space": "O(sum(w))", "note": "OOM when weights reach 10^5."},
            {"name": "Better: Prefix Sums + Linear Scan", "time": "O(N) init / O(N) pick", "space": "O(N)", "note": "10^4 x 10^4 = 10^8 operations -> slow."},
            {"name": "Optimal: Prefix Sums + Lower-Bound Binary Search", "time": "O(N) init / O(log N) pick", "space": "O(N)", "note": "★ Production load-balancer standard."}
        ]
    },
    "trace_table": {
        "input_desc": "w = [1, 3, 2] -> prefix = [1, 4, 6], total = 6, simulated target = 4",
        "columns": ["Step", "left", "right", "mid", "prefix[mid]", "Check (prefix[mid] >= target)", "Action"],
        "rows": [
            ["1", "0", "2", "1", "4", "4 >= 4 (True)", "Candidate found! Shrink right = mid = 1"],
            ["2", "0", "1", "0", "1", "1 >= 4 (False)", "Too small! Advance left = mid + 1 = 1"],
            ["3", "1", "1", "-", "-", "left == right -> stop", "Return left = 1 (covers targets 2, 3, 4)"]
        ],
        "takeaway": "Target 1 maps to idx 0 (1/6); targets 2, 3, 4 map to idx 1 (3/6); targets 5, 6 map to idx 2 (2/6)."
    },
    "stepper": {
        "title": "Prefix Sum Interval & Lower-Bound Binary Search Stepper",
        "default_input": "w=[1,3,2], target=4",
        "presets": [
            {"label": "w=[1,3,2], target=4", "onclick": "loadPreset([1,3,2], 4)"},
            {"label": "w=[1,3,2], target=1", "onclick": "loadPreset([1,3,2], 1)"},
            {"label": "w=[2,5,3,4], target=11", "onclick": "loadPreset([2,5,3,4], 11)"}
        ],
        "metrics": [
            {"label": "Target Dart", "id": "metric-target", "initial": "4", "color": "text-amber-400"},
            {"label": "Search [L..R]", "id": "metric-range", "initial": "[0..2]", "color": "text-cyan-400"},
            {"label": "Mid / Prefix", "id": "metric-mid", "initial": "1 (4)", "color": "text-purple-400"},
            {"label": "Picked Index", "id": "metric-res", "initial": "-", "color": "text-emerald-400"}
        ],
        "visual_html": "<div id='prefix-viz' class='flex flex-wrap items-center justify-center gap-2'></div>",
        "initial_narration": "Prefix array built from weights. Binary search locates first prefix[mid] >= target.",
        "pseudocode_lines": [
            "prefix[i] = prefix[i - 1] + w[i]",
            "target = randomInt(1..total)",
            "left = 0; right = prefix.size - 1",
            "while (left < right) {",
            "  mid = left + (right - left) / 2",
            "  if (prefix[mid] >= target) right = mid else left = mid + 1",
            "}",
            "return left"
        ],
        "js_code": """
        let curW = [1, 3, 2], curTarget = 4, curPrefix = [];
        function buildSteps(wArr, tgt) {
          if (Array.isArray(wArr) && wArr.length) curW = wArr;
          if (typeof tgt === 'number') curTarget = tgt;
          curPrefix = [];
          let sum = 0;
          for (let x of curW) { sum += x; curPrefix.push(sum); }
          if (curTarget < 1 || curTarget > sum) curTarget = Math.max(1, Math.min(sum, curTarget));
          steps = [];
          let l = 0, r = curPrefix.length - 1;
          steps.push({ l, r, mid: -1, res: '-', line: 3, narration: `Built prefix=${JSON.stringify(curPrefix)}, total=${sum}. Target dart=${curTarget}.` });
          while (l < r) {
            let mid = l + Math.floor((r - l) / 2);
            let ok = curPrefix[mid] >= curTarget;
            steps.push({ l, r, mid, res: '-', line: 6, narration: `mid=${mid}, prefix[${mid}]=${curPrefix[mid]} ${ok ? '>=' : '<'} ${curTarget} -> ${ok ? 'right = mid' : 'left = mid + 1'}.` });
            if (ok) r = mid; else l = mid + 1;
          }
          steps.push({ l, r, mid: l, res: l, line: 8, narration: `Converged at left=${l} (weight=${curW[l]}, prefix=${curPrefix[l]}). Return ${l}.` });
        }
        function renderStage(step) {
          const c = document.getElementById('prefix-viz');
          if (!c) return;
          c.innerHTML = curPrefix.map((pv, idx) => {
            let prev = idx === 0 ? 0 : curPrefix[idx - 1];
            let cls = 'border-slate-800 bg-slate-900 text-slate-400';
            if (idx === step.res) cls = 'border-emerald-400 bg-emerald-950/60 text-emerald-300 ring-2 ring-emerald-500/40';
            else if (idx === step.mid) cls = 'border-amber-400 bg-amber-950/50 text-amber-300';
            else if (idx >= step.l && idx <= step.r) cls = 'border-cyan-500/60 bg-cyan-950/30 text-cyan-200';
            return `<div class="px-3 py-2 rounded-xl border-2 flex flex-col items-center font-mono text-xs ${cls}">
              <span class="text-[10px] text-slate-500">idx ${idx} (w=${curW[idx]})</span>
              <span class="font-bold text-sm">prefix=${pv}</span>
              <span class="text-[10px] text-slate-400">(${prev + 1}..${pv})</span>
            </div>`;
          }).join('');
        }
        function loadPreset(wArr, tgt) { buildSteps(wArr, tgt); stepperReset(); }
        """,
        "js_custom_input": """function handleCustomInput() {
          const el = document.getElementById('custom-input');
          if (!el) return;
          const raw = el.value.trim();
          const mW = raw.match(/\\[[0-9,\\s]+\\]/);
          const mT = raw.match(/target\\s*=\\s*(\\d+)/i);
          try {
            const wArr = mW ? JSON.parse(mW[0]) : JSON.parse('[' + raw + ']');
            const tgt = mT ? parseInt(mT[1], 10) : Math.ceil(wArr.reduce((a,b)=>a+b,0) / 2);
            if (Array.isArray(wArr) && wArr.length) loadPreset(wArr, tgt);
          } catch (_) {}
        }""",
        "js_update_metrics": """
        document.getElementById('metric-target').textContent = curTarget;
        document.getElementById('metric-range').textContent = `[${step.l}..${step.r}]`;
        document.getElementById('metric-mid').textContent = step.mid >= 0 ? `${step.mid} (${curPrefix[step.mid]})` : '-';
        document.getElementById('metric-res').textContent = step.res;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Prefix Sum + Linear Scan",
            "badge": "Better",
            "desc": "Build prefix sums in O(N), then scan linearly for the first bucket exceeding random target.",
            "time": "O(N) pick",
            "space": "O(N)",
            "code_kotlin": """import kotlin.random.Random

class Solution(w: IntArray) {
    private val prefix = IntArray(w.size)
    private var total = 0

    init {
        for (i in w.indices) {
            total += w[i]
            prefix[i] = total
        }
    }

    fun pickIndex(): Int {
        val target = Random.nextInt(total) + 1
        for (i in prefix.indices) {
            if (prefix[i] >= target) return i
        }
        return prefix.size - 1
    }
}""",
            "code_python": """import random

class Solution:
    def __init__(self, w: list[int]):
        self.prefix = []
        total = 0
        for weight in w:
            total += weight
            self.prefix.append(total)
        self.total = total

    def pickIndex(self) -> int:
        target = random.randint(1, self.total)
        for i, val in enumerate(self.prefix):
            if val >= target:
                return i
        return len(self.prefix) - 1""",
            "code_java": """import java.util.Random;

class Solution {
    private final int[] prefix;
    private int total = 0;
    private final Random rand = new Random();

    public Solution(int[] w) {
        prefix = new int[w.length];
        for (int i = 0; i < w.length; i++) {
            total += w[i];
            prefix[i] = total;
        }
    }

    public int pickIndex() {
        int target = rand.nextInt(total) + 1;
        for (int i = 0; i < prefix.length; i++) {
            if (prefix[i] >= target) return i;
        }
        return prefix.length - 1;
    }
}""",
            "notes_kotlin": "Linear scan takes O(N) per pickIndex() call, timing out when N=10^4 and pickIndex runs 10^4 times.",
            "notes_python": "Avoids memory explosion of naive bucket expansion, but O(N) scan per pick fails latency SLAs.",
            "notes_java": "Precomputes primitive int[] prefix cleanly, though linear lookup wastes sorted monotonicity."
        },
        {
            "name": "Approach 2: Prefix Sum + Lower-Bound Binary Search",
            "badge": "Optimal",
            "desc": "Precompute prefix sums in O(N) and resolve random targets in O(log N) with lower-bound binary search.",
            "time": "O(log N) pick",
            "space": "O(N)",
            "code_kotlin": """import kotlin.random.Random

class Solution(w: IntArray) {
    private val prefix = IntArray(w.size)
    private var total = 0

    init {
        for (i in w.indices) {
            total += w[i]
            prefix[i] = total
        }
    }

    fun pickIndex(): Int {
        val target = Random.nextInt(total) + 1
        var left = 0
        var right = prefix.size - 1
        while (left < right) {
            val mid = left + (right - left) / 2
            if (prefix[mid] >= target) {
                right = mid
            } else {
                left = mid + 1
            }
        }
        return left
    }
}""",
            "code_python": """import bisect
import random

class Solution:
    def __init__(self, w: list[int]):
        self.prefix = []
        total = 0
        for weight in w:
            total += weight
            self.prefix.append(total)
        self.total = total

    def pickIndex(self) -> int:
        target = random.randint(1, self.total)
        return bisect.bisect_left(self.prefix, target)""",
            "code_java": """import java.util.Random;

class Solution {
    private final int[] prefix;
    private int total = 0;
    private final Random rand = new Random();

    public Solution(int[] w) {
        prefix = new int[w.length];
        for (int i = 0; i < w.length; i++) {
            total += w[i];
            prefix[i] = total;
        }
    }

    public int pickIndex() {
        int target = rand.nextInt(total) + 1;
        int left = 0;
        int right = prefix.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefix[mid] >= target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }
}""",
            "notes_kotlin": "Uses unboxed IntArray for prefix sums and Random.nextInt(total) + 1 to draw uniform integer ticks in [1..total].",
            "notes_python": "bisect.bisect_left runs in C and returns the exact leftmost index where prefix[i] >= target in O(log N).",
            "notes_java": "Halves the search range with overflow-safe mid = left + (right - left) / 2 and zero per-query allocations."
        }
    ],
    "test_cases": [
        {"mode": "oop", "ops": ["Solution", "pickIndex", "pickIndex", "pickIndex"], "args": [[[5]], [], [], []], "expected": [None, 0, 0, 0]},
        {"mode": "oop", "ops": ["Solution"] + ["pickIndex"] * 40, "args": [[[1, 9]]] + [[]] * 40, "weightedPick": {"weights": [1, 9], "dominantIdx": 1, "minDomCount": 22}},
        {"mode": "oop", "ops": ["Solution"] + ["pickIndex"] * 40, "args": [[[10, 1, 1]]] + [[]] * 40, "weightedPick": {"weights": [10, 1, 1], "dominantIdx": 0, "minDomCount": 20}}
    ],
    "scratchpad": {
        "kotlin_boilerplate": """import kotlin.random.Random

class Solution(w: IntArray) {
    fun pickIndex(): Int {
        // Write your O(log N) weighted sampler here:
        
    }
}""",
        "python_boilerplate": """import bisect
import random

class Solution:
    def __init__(self, w: list[int]):
        # Build prefix sum array here:
        pass

    def pickIndex(self) -> int:
        # Binary search random target in O(log N):
        pass""",
        "java_boilerplate": """import java.util.Random;

class Solution {
    public Solution(int[] w) {
        // Build prefix sum array here:
        
    }

    public int pickIndex() {
        // Binary search random target in O(log N):
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { mode: "oop", ops: ["Solution", "pickIndex", "pickIndex", "pickIndex"], args: [[[5]], [], [], []], expected: [null, 0, 0, 0] },
          { mode: "oop", ops: ["Solution", ...Array(40).fill("pickIndex")], args: [[[1, 9]], ...Array(40).fill([])], weightedPick: { weights: [1, 9], dominantIdx: 1, minDomCount: 22 } },
          { mode: "oop", ops: ["Solution", ...Array(40).fill("pickIndex")], args: [[[10, 1, 1]], ...Array(40).fill([])], weightedPick: { weights: [10, 1, 1], dominantIdx: 0, minDomCount: 20 } }
        ];
        """
    },
    "quiz_questions": [
        {
            "category": "Complexity & Scalability",
            "prompt": "Why does expanding w into an array repeating index i w[i] times fail production constraints?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "It allocates O(sum(w)) memory which overflows RAM when weights reach 10^5.", "isCorrect": True, "whyRight": "With 10^4 elements of weight 10^5, sum(w) reaches 10^9 slots (~4 GB), causing OOM; prefix sums take only O(N) space."},
                {"id": "B", "text": "It breaks uniform probability because duplicate indices distort hash bucket lookups.", "isCorrect": False, "whyWrong": "Probability is mathematically valid, but memory usage is prohibitive."},
                {"id": "C", "text": "It degrades pickIndex() runtime from O(1) to quadratic O(N^2) per query.", "isCorrect": False, "whyWrong": "Array indexing is O(1); the failure mode is O(sum(w)) space and initialization time."},
                {"id": "D", "text": "It prevents random number generators from sampling index zero on small inputs.", "isCorrect": False, "whyWrong": "Random indexing over the expanded array can still sample index zero cleanly."}
            ],
            "allExplanations": "• A is correct because sum(w) can reach 10^9.<br>• B is false because expanded tickets preserve exact probabilities.<br>• C is false because lookup is O(1), not O(N^2).<br>• D is false because index 0 still gets w[0] slots."
        },
        {
            "category": "Random Range & Off-by-One",
            "prompt": "Why must target be drawn from [1..total] (not [0..total - 1]) when testing prefix[mid] >= target?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Drawing 0 makes prefix[0] >= 0 always true even when w[0] = 0.", "isCorrect": False, "whyWrong": "Constraints guarantee w[i] >= 1, and off-by-one skews the bucket widths."},
                {"id": "B", "text": "Target 0 maps to index 0, giving index 0 one extra tick and starving the last bucket.", "isCorrect": True, "whyRight": "With [1..total], bucket 0 covers 1..w[0] (size w[0]). With [0..total-1] and >=, bucket 0 covers 0..w[0] (size w[0]+1) while the last bucket loses 1 tick!"},
                {"id": "C", "text": "Random.nextInt throws IllegalArgumentException whenever zero is included in range.", "isCorrect": False, "whyWrong": "Random.nextInt(total) natively generates integers in [0..total - 1]."},
                {"id": "D", "text": "Binary search diverges into infinite recursion if target equals zero.", "isCorrect": False, "whyWrong": "Binary search terminates at index 0, but weights are distorted by 1 tick."}
            ],
            "allExplanations": "• B is correct: >= with 0..total-1 widens index 0 by 1 and shrinks the last index by 1.<br>• Using [1..total] with >= (or [0..total-1] with >) keeps every bucket exact."
        },
        {
            "category": "Code Snippet Bug Hunt",
            "prompt": "What bug occurs in this Kotlin binary search when prefix[mid] >= target?",
            "codeSnippet": "var left = 0; var right = prefix.size - 1\nwhile (left < right) {\n    val mid = left + (right - left) / 2\n    if (prefix[mid] >= target) right = mid - 1\n    else left = mid + 1\n}\nreturn left",
            "options": [
                {"id": "A", "text": "Setting right = mid - 1 discards mid even when prefix[mid] is the valid answer.", "isCorrect": True, "whyRight": "Because prefix[mid] >= target could be the first qualifying bucket, setting right = mid - 1 skips it and returns the wrong index!"},
                {"id": "B", "text": "Setting left = mid + 1 causes integer overflow on arrays under 10^4 elements.", "isCorrect": False, "whyWrong": "left + (right - left) / 2 prevents overflow, and left = mid + 1 is correct when prefix[mid] < target."},
                {"id": "C", "text": "Using while (left < right) skips evaluating index 0 when w.length equals 1.", "isCorrect": False, "whyWrong": "When w.length == 1, left == right == 0, so returning left correctly returns 0."},
                {"id": "D", "text": "Computing mid rounds upward instead of downward, creating an infinite loop.", "isCorrect": False, "whyWrong": "Integer division rounds downward; the defect is discarding a valid mid candidate."}
            ],
            "allExplanations": "• A is correct: lower-bound binary search must keep mid via right = mid.<br>• B, C, and D describe non-existent issues."
        },
        {
            "category": "Mid-Level Production Follow-Up",
            "prompt": "If w.length = 10^4 and weights are updated frequently via updateWeight(i, newW), what upgrades O(N) rebuilds to O(log N)?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Sorting w before each pickIndex call to stabilize random bucket lookups.", "isCorrect": False, "whyWrong": "Sorting breaks original index identities and still takes O(N log N) per update."},
                {"id": "B", "text": "Replacing the static prefix array with a Fenwick Tree or Segment Tree.", "isCorrect": True, "whyRight": "A Fenwick (Binary Indexed) Tree or Segment Tree supports O(log N) point weight updates and O(log N) prefix-sum binary search."},
                {"id": "C", "text": "Storing all weight deltas inside a FIFO queue without updating prefix sums.", "isCorrect": False, "whyWrong": "Querying unapplied deltas would require O(N) scanning on every pickIndex call."},
                {"id": "D", "text": "Converting weights to strings inside a Trie for O(L) prefix lookups.", "isCorrect": False, "whyWrong": "Tries partition string characters, not cumulative numeric weight sums."}
            ],
            "allExplanations": "• B is correct: Segment Tree / Fenwick Tree gives O(log N) update and query.<br>• A destroys indices, C degrades reads, and D is the wrong data structure."
        }
    ],
    "spoken_script": {
        "phase1": "Before coding, I want to verify: all weights w[i] are positive integers >= 1, and w is non-empty, correct? Also, we want O(log N) pickIndex rather than O(sum(w)) memory.",
        "phase2": "Expanding each index w[i] times fails memory limits when weights are up to 10^5. Instead, we build a strictly increasing prefix sum array in O(N) time and space. On pickIndex(), we draw a uniform random target in [1..totalSum] and binary search for the leftmost index where prefix[mid] >= target in O(log N) time.",
        "phase3": "In the constructor, I accumulate running total into prefix[i]. In pickIndex(), I pick target = Random.nextInt(total) + 1. Then standard lower-bound binary search with left = 0, right = prefix.size - 1: if prefix[mid] >= target, keep mid with right = mid; else advance left = mid + 1. Return left.",
        "phase4": "Dry-running w = [1, 3]: prefix = [1, 4], total = 4. Target 1 -> mid=0 (1 >= 1) -> right=0 -> returns 0 (1/4 chance). Targets 2, 3, 4 -> mid=0 (< target) -> left=1 -> returns 1 (3/4 chance).",
        "senior_followup": "If weights can be mutated dynamically via update(i, val), a Fenwick Tree or Segment Tree maintains prefix sums in O(log N) per update and O(log N) per weighted sample."
    }
}

# =========================================================================
# PROBLEM 112: LC #34 FIND FIRST AND LAST POSITION (Pass 2A Core Medium)
# =========================================================================
p112 = {
    "num": 112,
    "lc": 34,
    "title": "Find First and Last Position of Element in Sorted Array",
    "category": "Binary Search",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "find-first-and-last-position-of-element-in-sorted-array",
    "prev_file": "0111-lc528-random-pick-with-weight.html",
    "next_file": "0113-lc2-add-two-numbers.html",
    "verbatim_statement": "Given an array of integers <code>nums</code> sorted in non-decreasing order, find the starting and ending position of a given <code>target</code> value.<br><br>If <code>target</code> is not found in the array, return <code>[-1, -1]</code>.<br><br>You must write an algorithm with <code>O(log n)</code> runtime complexity.",
    "examples": [
        {"title": "Example 1", "input": "nums = [5,7,7,8,8,10], target = 8", "output": "[3,4]"},
        {"title": "Example 2", "input": "nums = [5,7,7,8,8,10], target = 6", "output": "[-1,-1]"},
        {"title": "Example 3", "input": "nums = [], target = 0", "output": "[-1,-1]"}
    ],
    "constraints": [
        "0 <= nums.length <= 10^5",
        "-10^9 <= nums[i] <= 10^9",
        "nums is a non-decreasing array.",
        "-10^9 <= target <= 10^9"
    ],
    "plain_english": "In a sorted array with duplicates, find both the leftmost and rightmost indices of target in strict O(log N) time—never scanning outward linearly from a hit.",
    "boundary_checks": {
        "min_base": "nums = [], target = 0 -> Empty array guard immediately returns [-1, -1].",
        "best_exit": "nums = [8,8,8,8], target = 8 -> All elements match; returns [0, 3] in O(log N) without linear scan.",
        "worst_scan": "nums = [5,7,7,8,8,10], target = 6 -> Target missing; first bound check returns -1 so second search can short-circuit."
    },
    "mental_model": {
        "analogy_title": "Two Bookmarks in a Multi-Copy Dictionary",
        "analogy_text": "Suppose a word appears on 500 consecutive pages of a dictionary. If you open to the middle and land on one copy, flipping page-by-page left and right takes O(N) time! Instead, do two binary searches: the first asks 'Is this copy still too far right?' (keep pushing right = mid - 1 after recording ans = mid to find the leftmost page). The second asks 'Is this copy still too far left?' (keep pushing left = mid + 1 after recording ans = mid to find the rightmost page).",
        "aha_moment": "Never stop when nums[mid] == target! Record mid as a candidate bound and keep narrowing left (for first position) or right (for last position) in two clean O(log N) passes.",
        "trigger": "Finding exact range boundaries [first, last], frequency counts in sorted arrays, or lower/upper bounds.",
        "sisters": [
            {"name": "Binary Search", "lc": "#704"},
            {"name": "Time Based Key-Value Store", "lc": "#981"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N) Worst-Case Time, O(1) Space",
        "naive_text": "Binary search to find one occurrence of target, then expand two while-loops outward (left--, right++) to find boundaries.",
        "wasted_work": "When all 10^5 elements equal target (e.g. [8, 8, ..., 8]), expanding outward degrades straight to O(N) linear time, violating the O(log N) requirement.",
        "optimal_complexity": "O(log N) Time, O(1) Space",
        "optimal_text": "Run one parameterized helper findBound(nums, target, isFirst): when nums[mid] == target, record ans = mid and continue halving left (right = mid - 1) if isFirst else right (left = mid + 1).",
        "core_invariant": "ans always holds the best verified occurrence of target seen so far while the active [left..right] window probes strictly closer to the target edge.",
        "table_rows": [
            {"name": "Naive: Linear Scan", "time": "O(N)", "space": "O(1)", "note": "Violates O(log N) prompt requirement."},
            {"name": "Trap: Binary Search + Linear Expand", "time": "O(N) worst", "space": "O(1)", "note": "Degrades to O(N) on arrays of identical elements."},
            {"name": "Optimal: Dual Biased Binary Search", "time": "O(log N)", "space": "O(1)", "note": "★ Guaranteed O(log N) even when 100% of elements equal target."}
        ]
    },
    "trace_table": {
        "input_desc": "nums = [5, 7, 7, 8, 8, 10], target = 8",
        "columns": ["Pass", "left", "right", "mid", "nums[mid]", "ans", "Next Window"],
        "rows": [
            ["First #1", "0", "5", "2", "7 (< 8)", "-1", "left = 3..5"],
            ["First #2", "3", "5", "4", "8 (== 8)", "4", "Push left: right = 3..3"],
            ["First #3", "3", "3", "3", "8 (== 8)", "3", "Push left: right = 2 -> stop (first=3)"],
            ["Last #1", "0", "5", "2", "7 (< 8)", "-1", "left = 3..5"],
            ["Last #2", "3", "5", "4", "8 (== 8)", "4", "Push right: left = 5..5"],
            ["Last #3", "5", "5", "5", "10 (> 8)", "4", "right = 4 -> stop (last=4)"]
        ],
        "takeaway": "If the first pass returns -1, we can immediately return [-1, -1] without running the second search!"
    },
    "stepper": {
        "title": "Biased Lower-Bound & Upper-Bound Binary Search Stepper",
        "default_input": "nums=[5,7,7,8,8,10], target=8",
        "presets": [
            {"label": "Standard [5,7,7,8,8,10], 8", "onclick": "loadPreset([5,7,7,8,8,10], 8)"},
            {"label": "All Equal [8,8,8,8], 8", "onclick": "loadPreset([8,8,8,8], 8)"},
            {"label": "Missing [5,7,7,8,8,10], 6", "onclick": "loadPreset([5,7,7,8,8,10], 6)"}
        ],
        "metrics": [
            {"label": "Search Mode", "id": "metric-mode", "initial": "Find First", "color": "text-purple-400"},
            {"label": "Window [L..R]", "id": "metric-lr", "initial": "[0..5]", "color": "text-cyan-400"},
            {"label": "Mid / Val", "id": "metric-mid", "initial": "-", "color": "text-amber-400"},
            {"label": "Result [First,Last]", "id": "metric-res", "initial": "[-1,-1]", "color": "text-emerald-400"}
        ],
        "visual_html": "<div id='bs-range-viz' class='flex flex-wrap items-center justify-center gap-2'></div>",
        "initial_narration": "Running Pass 1 (leftmost index) then Pass 2 (rightmost index).",
        "pseudocode_lines": [
            "fun findBound(nums, target, isFirst):",
            "  left = 0; right = nums.size - 1; ans = -1",
            "  while (left <= right) {",
            "    mid = left + (right - left) / 2",
            "    if (nums[mid] == target) { ans = mid; if (isFirst) right = mid - 1 else left = mid + 1 }",
            "    else if (nums[mid] < target) left = mid + 1 else right = mid - 1",
            "  }",
            "  return ans"
        ],
        "js_code": """
        let bsArr = [5, 7, 7, 8, 8, 10], bsTarget = 8;
        function buildSteps(arr, tgt) {
          if (Array.isArray(arr)) bsArr = arr;
          if (typeof tgt === 'number') bsTarget = tgt;
          steps = [];
          function runPass(isFirst, firstRes) {
            let l = 0, r = bsArr.length - 1, ans = -1;
            let mode = isFirst ? 'Find First' : 'Find Last';
            while (l <= r) {
              let mid = l + Math.floor((r - l) / 2);
              let v = bsArr[mid];
              if (v === bsTarget) {
                ans = mid;
                steps.push({ mode, l, r, mid, ans, res: isFirst ? `[${ans},?]` : `[${firstRes},${ans}]`, line: 5, narration: `${mode}: nums[${mid}]==${bsTarget}! Record ans=${mid}, push ${isFirst ? 'right=mid-1' : 'left=mid+1'}.` });
                if (isFirst) r = mid - 1; else l = mid + 1;
              } else if (v < bsTarget) {
                steps.push({ mode, l, r, mid, ans, res: isFirst ? `[${ans},?]` : `[${firstRes},${ans}]`, line: 6, narration: `${mode}: nums[${mid}]=${v} < ${bsTarget}, move left=${mid+1}.` });
                l = mid + 1;
              } else {
                steps.push({ mode, l, r, mid, ans, res: isFirst ? `[${ans},?]` : `[${firstRes},${ans}]`, line: 6, narration: `${mode}: nums[${mid}]=${v} > ${bsTarget}, move right=${mid-1}.` });
                r = mid - 1;
              }
            }
            return ans;
          }
          let first = runPass(true, -1);
          if (first === -1) {
            steps.push({ mode: 'Done', l: 0, r: -1, mid: -1, ans: -1, res: '[-1,-1]', line: 8, narration: `Target ${bsTarget} not found in first pass. Early return [-1, -1].` });
            return;
          }
          let last = runPass(false, first);
          steps.push({ mode: 'Done', l: first, r: last, mid: -1, ans: last, res: `[${first},${last}]`, line: 8, narration: `Completed! First=${first}, Last=${last} -> [${first}, ${last}].` });
        }
        function renderStage(step) {
          const c = document.getElementById('bs-range-viz');
          if (!c) return;
          c.innerHTML = bsArr.map((v, idx) => {
            let cls = 'border-slate-800 bg-slate-900 text-slate-400';
            if (idx === step.mid) cls = 'border-amber-400 bg-amber-950/60 text-amber-300 scale-105';
            else if (idx === step.ans) cls = 'border-emerald-400 bg-emerald-950/50 text-emerald-300';
            else if (idx >= step.l && idx <= step.r) cls = 'border-cyan-500/60 bg-cyan-950/30 text-cyan-200';
            return `<div class="w-12 h-14 rounded-xl border-2 flex flex-col items-center justify-center font-mono text-xs ${cls}">
              <span class="text-[9px] text-slate-500">[${idx}]</span>
              <span class="font-bold">${v}</span>
            </div>`;
          }).join('');
        }
        function loadPreset(arr, tgt) { buildSteps(arr, tgt); stepperReset(); }
        """,
        "js_custom_input": """function handleCustomInput() {
          const el = document.getElementById('custom-input');
          if (!el) return;
          const raw = el.value.trim();
          const mA = raw.match(/\\[[0-9,-\\s]*\\]/);
          const mT = raw.match(/target\\s*=\\s*(-?\\d+)/i);
          try {
            const arr = mA ? JSON.parse(mA[0]) : JSON.parse('[' + raw + ']');
            const tgt = mT ? parseInt(mT[1], 10) : (arr[0] ?? 0);
            loadPreset(arr, tgt);
          } catch (_) {}
        }""",
        "js_update_metrics": """
        document.getElementById('metric-mode').textContent = step.mode;
        document.getElementById('metric-lr').textContent = `[${step.l}..${step.r}]`;
        document.getElementById('metric-mid').textContent = step.mid >= 0 ? `${step.mid} (${bsArr[step.mid]})` : '-';
        document.getElementById('metric-res').textContent = step.res;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Binary Search + Linear Expansion",
            "badge": "Naive",
            "desc": "Locate any matching index with binary search, then walk left and right linearly. Degrades to O(N) on duplicates.",
            "time": "O(N) worst",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun searchRange(nums: IntArray, target: Int): IntArray {
        var left = 0
        var right = nums.size - 1
        var hit = -1
        while (left <= right) {
            val mid = left + (right - left) / 2
            if (nums[mid] == target) { hit = mid; break }
            else if (nums[mid] < target) left = mid + 1
            else right = mid - 1
        }
        if (hit == -1) return intArrayOf(-1, -1)
        var start = hit
        var end = hit
        while (start > 0 && nums[start - 1] == target) start--
        while (end < nums.size - 1 && nums[end + 1] == target) end++
        return intArrayOf(start, end)
    }
}""",
            "code_python": """class Solution:
    def searchRange(self, nums: list[int], target: int) -> list[int]:
        left, right = 0, len(nums) - 1
        hit = -1
        while left <= right:
            mid = left + (right - left) // 2
            if nums[mid] == target:
                hit = mid
                break
            elif nums[mid] < target:
                left = mid + 1
            else:
                right = mid - 1
        if hit == -1:
            return [-1, -1]
        start = end = hit
        while start > 0 and nums[start - 1] == target:
            start -= 1
        while end < len(nums) - 1 and nums[end + 1] == target:
            end += 1
        return [start, end]""",
            "code_java": """class Solution {
    public int[] searchRange(int[] nums, int target) {
        int left = 0, right = nums.length - 1, hit = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) { hit = mid; break; }
            else if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        if (hit == -1) return new int[]{-1, -1};
        int start = hit, end = hit;
        while (start > 0 && nums[start - 1] == target) start--;
        while (end < nums.length - 1 && nums[end + 1] == target) end++;
        return new int[]{start, end};
    }
}""",
            "notes_kotlin": "Linear expansion fails the O(log N) interview requirement if all elements equal target.",
            "notes_python": "Easy to write first, but interviewers specifically test all-duplicate inputs like [2,2,2,...,2].",
            "notes_java": "Hits O(N) worst case when target spans the majority of the sorted array."
        },
        {
            "name": "Approach 2: Dual Biased Binary Search",
            "badge": "Optimal",
            "desc": "Parameterized findBound(isFirst) helper guarantees strict O(log N) time even on uniform arrays.",
            "time": "O(log N)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun searchRange(nums: IntArray, target: Int): IntArray {
        val first = findBound(nums, target, true)
        if (first == -1) return intArrayOf(-1, -1)
        val last = findBound(nums, target, false)
        return intArrayOf(first, last)
    }

    private fun findBound(nums: IntArray, target: Int, isFirst: Boolean): Int {
        var left = 0
        var right = nums.size - 1
        var ans = -1
        while (left <= right) {
            val mid = left + (right - left) / 2
            if (nums[mid] == target) {
                ans = mid
                if (isFirst) {
                    right = mid - 1
                } else {
                    left = mid + 1
                }
            } else if (nums[mid] < target) {
                left = mid + 1
            } else {
                right = mid - 1
            }
        }
        return ans
    }
}""",
            "code_python": """class Solution:
    def searchRange(self, nums: list[int], target: int) -> list[int]:
        def find_bound(is_first: bool) -> int:
            left, right, ans = 0, len(nums) - 1, -1
            while left <= right:
                mid = left + (right - left) // 2
                if nums[mid] == target:
                    ans = mid
                    if is_first:
                        right = mid - 1
                    else:
                        left = mid + 1
                elif nums[mid] < target:
                    left = mid + 1
                else:
                    right = mid - 1
            return ans

        first = find_bound(True)
        if first == -1:
            return [-1, -1]
        return [first, find_bound(False)]""",
            "code_java": """class Solution {
    public int[] searchRange(int[] nums, int target) {
        int first = findBound(nums, target, true);
        if (first == -1) return new int[]{-1, -1};
        int last = findBound(nums, target, false);
        return new int[]{first, last};
    }

    private int findBound(int[] nums, int target, boolean isFirst) {
        int left = 0, right = nums.length - 1, ans = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                ans = mid;
                if (isFirst) right = mid - 1;
                else left = mid + 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return ans;
    }
}""",
            "notes_kotlin": "Early short-circuit when first == -1 saves the second binary search whenever target is absent.",
            "notes_python": "Shared find_bound closure eliminates duplicate binary-search logic cleanly without extra allocation.",
            "notes_java": "Returns unboxed int[] in strict O(log N) worst-case time and O(1) space."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun searchRange(nums: IntArray, target: Int): IntArray {
        // Write your O(log N) dual binary search here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def searchRange(self, nums: list[int], target: int) -> list[int]:
        # Write your O(log N) dual binary search here:
        pass""",
        "java_boilerplate": """class Solution {
    public int[] searchRange(int[] nums, int target) {
        // Write your O(log N) dual binary search here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: [[5, 7, 7, 8, 8, 10], 8], expected: [3, 4] },
          { input: [[5, 7, 7, 8, 8, 10], 6], expected: [-1, -1] },
          { input: [[], 0], expected: [-1, -1] },
          { input: [[8, 8, 8, 8], 8], expected: [0, 3] }
        ];
        """
    },
    "quiz_questions": [
        {
            "category": "Complexity & Trap Analysis",
            "prompt": "Why does expanding outward with while loops after one binary search hit fail the O(log N) requirement?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "It degrades to O(N) time when target repeats across most of the array.", "isCorrect": True, "whyRight": "On [8, 8, ..., 8] of length 10^5, the linear expansion loop visits all 10^5 elements, making worst-case runtime O(N)."},
                {"id": "B", "text": "It causes IndexOutOfBoundsException whenever target appears at index zero.", "isCorrect": False, "whyWrong": "Properly guarded while (start > 0) checks prevent out-of-bounds reads."},
                {"id": "C", "text": "It doubles auxiliary heap memory allocation inside sorted primitive integer arrays.", "isCorrect": False, "whyWrong": "Linear expansion uses O(1) extra space; the flaw is O(N) time complexity."},
                {"id": "D", "text": "It skips non-adjacent occurrences of target when duplicates are separated.", "isCorrect": False, "whyWrong": "Because the array is sorted, all occurrences of target are strictly contiguous."}
            ],
            "allExplanations": "• A is correct: uniform arrays turn linear expansion into O(N).<br>• B, C, and D do not happen in sorted arrays with bounds checks."
        },
        {
            "category": "Invariant & Search Direction",
            "prompt": "When nums[mid] == target while searching for the LAST occurrence, how must the pointers update?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Immediately return mid because any matching index guarantees the rightmost boundary.", "isCorrect": False, "whyWrong": "Returning early misses additional copies of target located at indices > mid."},
                {"id": "B", "text": "Record ans = mid and set left = mid + 1 to probe further right.", "isCorrect": True, "whyRight": "Saving ans = mid preserves the best known rightmost copy while shifting left = mid + 1 searches strictly to the right."},
                {"id": "C", "text": "Record ans = mid and set right = mid - 1 to probe further left.", "isCorrect": False, "whyWrong": "Shifting right = mid - 1 finds the FIRST (leftmost) occurrence, not the last."},
                {"id": "D", "text": "Increment both left and right simultaneously until nums[left] != target.", "isCorrect": False, "whyWrong": "Incrementing both pointers loses the binary search invariant and becomes linear."}
            ],
            "allExplanations": "• B is correct: save candidate mid, then push left = mid + 1.<br>• C finds the leftmost index instead."
        },
        {
            "category": "Code Snippet Bug Hunt",
            "prompt": "What bug occurs if the loop condition is changed to while (left < right) in findBound?",
            "codeSnippet": "var left = 0; var right = nums.size - 1; var ans = -1\nwhile (left < right) {\n    val mid = left + (right - left) / 2\n    if (nums[mid] == target) { ans = mid; right = mid - 1 }\n    else if (nums[mid] < target) left = mid + 1\n    else right = mid - 1\n}\nreturn ans",
            "options": [
                {"id": "A", "text": "When left == right converges on a single-element match, that index is skipped.", "isCorrect": True, "whyRight": "With left < right and right = mid - 1, converging to [idx, idx] exits before inspecting nums[idx], returning -1 on [8] or edge hits!"},
                {"id": "B", "text": "The loop enters an infinite cycle whenever nums is empty.", "isCorrect": False, "whyWrong": "When nums is empty, left=0 and right=-1, so left < right is immediately false."},
                {"id": "C", "text": "Mid calculation overflows 32-bit signed integers on large negative values.", "isCorrect": False, "whyWrong": "left + (right - left) / 2 operates on indices, not element values, and never overflows."},
                {"id": "D", "text": "It always returns the last occurrence instead of the first occurrence.", "isCorrect": False, "whyWrong": "The defect is exiting early when left == right, missing valid boundary matches."}
            ],
            "allExplanations": "• A is correct: modifying mid to mid - 1 requires while (left <= right) so the final candidate [left == right] is inspected."
        },
        {
            "category": "Optimization & Follow-Up",
            "prompt": "Why should searchRange check if (first == -1) return [-1, -1] before running the second binary search?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Otherwise the second binary search throws NullPointerException on missing targets.", "isCorrect": False, "whyWrong": "The second search still safely returns -1 without throwing exceptions."},
                {"id": "B", "text": "If target is absent from the array, skipping the second search halves work cleanly.", "isCorrect": True, "whyRight": "Short-circuiting cuts constant-factor latency in half on cache misses without affecting correctness."},
                {"id": "C", "text": "The second search would otherwise return the nearest greater element index.", "isCorrect": False, "whyWrong": "ans stays -1 unless nums[mid] == target is observed."},
                {"id": "D", "text": "Kotlin IntArray requires explicit non-negative indices before array allocation.", "isCorrect": False, "whyWrong": "intArrayOf(-1, -1) holds signed ints including -1 cleanly."}
            ],
            "allExplanations": "• B is correct: if target has no first occurrence, it cannot have a last occurrence, avoiding a redundant O(log N) pass."
        }
    ],
    "spoken_script": {
        "phase1": "Let me confirm: nums is sorted in non-decreasing order, may contain duplicates or be empty, and we must return [-1, -1] if target is absent in O(log N) time, correct?",
        "phase2": "Expanding linearly from one binary-search hit degrades to O(N) when all elements match target. Instead, we run two O(log N) binary searches via one helper findBound(isFirst): when nums[mid] == target, we record ans = mid and keep narrowing left (right = mid - 1) for the start or right (left = mid + 1) for the end.",
        "phase3": "I call first = findBound(nums, target, true). If first == -1, I return [-1, -1] immediately. Otherwise I call last = findBound(nums, target, false) and return [first, last]. Both run in O(log N) time and O(1) space.",
        "phase4": "Tracing [5,7,7,8,8,10], target=8: first pass hits mid=4 (ans=4, right=3), then mid=3 (ans=3, right=2) -> returns 3. Second pass hits mid=4 (ans=4, left=5), then mid=5 (10 > 8, right=4) -> returns 4. Output [3, 4].",
        "senior_followup": "Once you have [first, last], you can compute the exact frequency count of target in O(log N) as (last - first + 1), which is a common follow-up for sorted log/index queries."
    }
}

# =========================================================================
# PROBLEM 113: LC #2 ADD TWO NUMBERS (Pass 2A Core Medium)
# =========================================================================
p113 = {
    "num": 113,
    "lc": 2,
    "title": "Add Two Numbers",
    "category": "Linked Lists & System Node Design",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "add-two-numbers",
    "io_type": "list",
    "prev_file": "0112-lc34-find-first-and-last-position-of-element-in-sorted-array.html",
    "next_file": "0114-lc143-reorder-list.html",
    "verbatim_statement": "You are given two <strong>non-empty</strong> linked lists representing two non-negative integers. The digits are stored in <strong>reverse order</strong>, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.<br><br>You may assume the two numbers do not contain any leading zero, except the number 0 itself.",
    "examples": [
        {"title": "Example 1", "input": "l1 = [2,4,3], l2 = [5,6,4]", "output": "[7,0,8] (342 + 465 = 807)"},
        {"title": "Example 2", "input": "l1 = [0], l2 = [0]", "output": "[0]"},
        {"title": "Example 3", "input": "l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]", "output": "[8,9,9,9,0,0,0,1]"}
    ],
    "constraints": [
        "The number of nodes in each linked list is in the range [1, 100].",
        "0 <= Node.val <= 9",
        "It is guaranteed that the list represents a number that does not have leading zeros."
    ],
    "plain_english": "Add two numbers whose digits are already reversed (ones place at the head), propagating carry forward node-by-node—including any trailing carry after both lists end.",
    "boundary_checks": {
        "min_base": "l1 = [0], l2 = [0] -> Single digit sum = 0, carry = 0, returns [0].",
        "best_exit": "l1 = [9,9], l2 = [1] -> Unequal lengths plus trailing carry; loop runs while (p != null || q != null || carry > 0) -> [0,0,1].",
        "worst_scan": "100-digit lists -> Converting to 64-bit Long overflows! Must add digit-by-digit."
    },
    "mental_model": {
        "analogy_title": "Elementary School Column Addition (Right-to-Left)",
        "analogy_text": "Because the linked lists store the ones digit at the head and tens/hundreds further down, they are ALREADY lined up in the exact order you add numbers by hand! You add digit1 + digit2 + carry, write down (sum % 10) on the result chain behind a dummy sentinel, and carry (sum / 10) to the next column. Even if both lists run out of digits, a leftover carry == 1 creates one final node.",
        "aha_moment": "Use a dummy sentinel head and keep looping while (p != null || q != null || carry > 0)—eliminating separate cleanup loops for unequal list lengths and trailing carry!",
        "trigger": "Big-integer arithmetic, unequal-length linked list merging, or carry propagation.",
        "sisters": [
            {"name": "Merge Two Sorted Lists", "lc": "#21"},
            {"name": "Reverse Linked List", "lc": "#206"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "Overflow / Invalid for > 19 Digits",
        "naive_text": "Convert both linked lists into 64-bit integers (Long), add them, and convert the sum back into linked-list nodes.",
        "wasted_work": "Constraints allow up to 100 digits! Even unsigned 64-bit integers overflow at 20 digits, failing immediately.",
        "optimal_complexity": "O(max(M, N)) Time, O(max(M, N)) Output Space",
        "optimal_text": "Walk p and q simultaneously with a dummy head and integer carry. Treat null pointers as digit 0 and continue as long as p != null || q != null || carry > 0.",
        "core_invariant": "At step k, carry holds the exact overflow from digit position k - 1, and curr points to the last finalized result node.",
        "table_rows": [
            {"name": "Trap: Convert Lists to 64-bit Long", "time": "O(M + N)", "space": "O(M + N)", "note": "Overflows above 19 digits (constraints allow 100!)."},
            {"name": "Optimal: Dummy Head + Carry Loop", "time": "O(max(M, N))", "space": "O(max(M, N))", "note": "★ Handles arbitrary 100+ digit precision and trailing carry."}
        ]
    },
    "trace_table": {
        "input_desc": "l1 = [9, 9], l2 = [1] (99 + 1 = 100)",
        "columns": ["Step", "p.val", "q.val", "carry_in", "sum", "digit (sum%10)", "carry_out", "Result List"],
        "rows": [
            ["1", "9", "1", "0", "10", "0", "1", "[0]"],
            ["2", "9", "0 (null)", "1", "10", "0", "1", "[0, 0]"],
            ["3", "0 (null)", "0 (null)", "1", "1", "1", "0", "[0, 0, 1]"]
        ],
        "takeaway": "Putting `carry > 0` right inside the `while` condition handles `[9,9] + [1] = [0,0,1]` with zero extra post-loop `if` checks!"
    },
    "stepper": {
        "title": "Column-by-Column Linked List Carry Stepper",
        "default_input": "l1=[2,4,3], l2=[5,6,4]",
        "presets": [
            {"label": "[2,4,3] + [5,6,4]", "onclick": "loadPreset([2,4,3], [5,6,4])"},
            {"label": "Carry Cascade [9,9] + [1]", "onclick": "loadPreset([9,9], [1])"},
            {"label": "Zero [0] + [0]", "onclick": "loadPreset([0], [0])"}
        ],
        "metrics": [
            {"label": "Digit p / q", "id": "metric-pq", "initial": "2 + 5", "color": "text-cyan-400"},
            {"label": "Carry In -> Out", "id": "metric-carry", "initial": "0 -> 0", "color": "text-amber-400"},
            {"label": "Sum / New Node", "id": "metric-sum", "initial": "7 -> 7", "color": "text-purple-400"},
            {"label": "Result Nodes", "id": "metric-res", "initial": "[]", "color": "text-emerald-400"}
        ],
        "visual_html": "<div id='ll-add-viz' class='space-y-3 w-full max-w-2xl'></div>",
        "initial_narration": "Dummy sentinel created. Adding digits while p, q, or carry remain.",
        "pseudocode_lines": [
            "dummy = ListNode(0); curr = dummy; carry = 0",
            "while (p != null || q != null || carry > 0) {",
            "  sum = carry + (p?.val ?: 0) + (q?.val ?: 0)",
            "  carry = sum / 10",
            "  curr.next = ListNode(sum % 10); curr = curr.next",
            "  p = p?.next; q = q?.next",
            "}",
            "return dummy.next"
        ],
        "js_code": """
        let arr1 = [2, 4, 3], arr2 = [5, 6, 4];
        function buildSteps(a, b) {
          if (Array.isArray(a)) arr1 = a;
          if (Array.isArray(b)) arr2 = b;
          steps = [];
          let i = 0, j = 0, carry = 0, res = [];
          steps.push({ i: 0, j: 0, pq: '-', carryStr: '0 -> 0', sumStr: '-', res: [], line: 1, narration: `Initialized dummy sentinel and carry = 0.` });
          while (i < arr1.length || j < arr2.length || carry > 0) {
            let v1 = i < arr1.length ? arr1[i] : 0;
            let v2 = j < arr2.length ? arr2[j] : 0;
            let sum = carry + v1 + v2;
            let nextCarry = Math.floor(sum / 10);
            let digit = sum % 10;
            res.push(digit);
            steps.push({
              i, j, pq: `${v1} + ${v2}`, carryStr: `${carry} -> ${nextCarry}`, sumStr: `${sum} -> ${digit}`,
              res: [...res], line: 5,
              narration: `Column ${res.length-1}: ${v1} + ${v2} + carry(${carry}) = ${sum}. Append node(${digit}), next carry=${nextCarry}.`
            });
            carry = nextCarry;
            i++; j++;
          }
          steps.push({ i, j, pq: 'Done', carryStr: '0', sumStr: 'Done', res: [...res], line: 8, narration: `Finished! Return dummy.next -> [${res.join(', ')}].` });
        }
        function renderStage(step) {
          const c = document.getElementById('ll-add-viz');
          if (!c) return;
          function row(label, list, activeIdx, color) {
            return `<div class="flex items-center gap-2 font-mono text-xs"><span class="w-12 text-slate-400">${label}:</span>` +
              list.map((v, idx) => `<div class="w-9 h-9 rounded-lg border flex items-center justify-center font-bold ${idx === activeIdx ? color : 'border-slate-800 bg-slate-900 text-slate-400'}">${v}</div>`).join('') +
              `</div>`;
          }
          c.innerHTML = row('l1', arr1, step.i, 'border-cyan-400 bg-cyan-950/50 text-cyan-300') +
                        row('l2', arr2, step.j, 'border-amber-400 bg-amber-950/50 text-amber-300') +
                        row('out', step.res, step.res.length - 1, 'border-emerald-400 bg-emerald-950/60 text-emerald-300');
        }
        function loadPreset(a, b) { buildSteps(a, b); stepperReset(); }
        """,
        "js_custom_input": """function handleCustomInput() {
          const el = document.getElementById('custom-input');
          if (!el) return;
          const matches = el.value.match(/\\[[0-9,\\s]+\\]/g);
          if (matches && matches.length >= 2) {
            try { loadPreset(JSON.parse(matches[0]), JSON.parse(matches[1])); } catch (_) {}
          }
        }""",
        "js_update_metrics": """
        document.getElementById('metric-pq').textContent = step.pq;
        document.getElementById('metric-carry').textContent = step.carryStr;
        document.getElementById('metric-sum').textContent = step.sumStr;
        document.getElementById('metric-res').textContent = '[' + step.res.join(',') + ']';
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Two-Pass Separate Remainder Loops",
            "badge": "Better",
            "desc": "Loop while both p and q are non-null, then handle remaining tail nodes and final carry in separate loops.",
            "time": "O(max(M, N))",
            "space": "O(max(M, N))",
            "code_kotlin": """class Solution {
    fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
        val dummy = ListNode(0)
        var curr = dummy
        var p = l1
        var q = l2
        var carry = 0
        while (p != null && q != null) {
            val sum = p.`val` + q.`val` + carry
            carry = sum / 10
            curr.next = ListNode(sum % 10)
            curr = curr.next!!
            p = p.next
            q = q.next
        }
        var rem = p ?: q
        while (rem != null) {
            val sum = rem.`val` + carry
            carry = sum / 10
            curr.next = ListNode(sum % 10)
            curr = curr.next!!
            rem = rem.next
        }
        if (carry > 0) curr.next = ListNode(carry)
        return dummy.next
    }
}""",
            "code_python": """class Solution:
    def addTwoNumbers(self, l1: ListNode | None, l2: ListNode | None) -> ListNode | None:
        dummy = ListNode(0)
        curr = dummy
        p, q, carry = l1, l2, 0
        while p and q:
            s = p.val + q.val + carry
            carry = s // 10
            curr.next = ListNode(s % 10)
            curr = curr.next
            p, q = p.next, q.next
        rem = p or q
        while rem:
            s = rem.val + carry
            carry = s // 10
            curr.next = ListNode(s % 10)
            curr = curr.next
            rem = rem.next
        if carry:
            curr.next = ListNode(carry)
        return dummy.next""",
            "code_java": """class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), curr = dummy;
        ListNode p = l1, q = l2;
        int carry = 0;
        while (p != null && q != null) {
            int sum = p.val + q.val + carry;
            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
            p = p.next; q = q.next;
        }
        ListNode rem = (p != null) ? p : q;
        while (rem != null) {
            int sum = rem.val + carry;
            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
            rem = rem.next;
        }
        if (carry > 0) curr.next = new ListNode(carry);
        return dummy.next;
    }
}""",
            "notes_kotlin": "Correct, but duplicates the carry-update and node-creation block unnecessarily.",
            "notes_python": "Requires two separate while loops plus a trailing carry check.",
            "notes_java": "More verbose than unifying all three conditions into one single loop."
        },
        {
            "name": "Approach 2: Unified Dummy-Head Carry Loop",
            "badge": "Optimal",
            "desc": "Unify p, q, and trailing carry inside a single while (p != null || q != null || carry > 0) loop.",
            "time": "O(max(M, N))",
            "space": "O(max(M, N))",
            "code_kotlin": """class Solution {
    fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
        val dummy = ListNode(0)
        var curr = dummy
        var p = l1
        var q = l2
        var carry = 0
        while (p != null || q != null || carry > 0) {
            val x = p?.`val` ?: 0
            val y = q?.`val` ?: 0
            val sum = carry + x + y
            carry = sum / 10
            curr.next = ListNode(sum % 10)
            curr = curr.next!!
            p = p?.next
            q = q?.next
        }
        return dummy.next
    }
}""",
            "code_python": """class Solution:
    def addTwoNumbers(self, l1: ListNode | None, l2: ListNode | None) -> ListNode | None:
        dummy = ListNode(0)
        curr = dummy
        p, q, carry = l1, l2, 0
        while p or q or carry:
            x = p.val if p else 0
            y = q.val if q else 0
            total = carry + x + y
            carry = total // 10
            curr.next = ListNode(total % 10)
            curr = curr.next
            p = p.next if p else None
            q = q.next if q else None
        return dummy.next""",
            "code_java": """class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        ListNode p = l1, q = l2;
        int carry = 0;
        while (p != null || q != null || carry > 0) {
            int x = (p != null) ? p.val : 0;
            int y = (q != null) ? q.val : 0;
            int sum = carry + x + y;
            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
            if (p != null) p = p.next;
            if (q != null) q = q.next;
        }
        return dummy.next;
    }
}""",
            "notes_kotlin": "Kotlin null-safe ?. and Elvis ?: 0 operators handle unequal lengths and trailing carry in one clean loop.",
            "notes_python": "Integer floor division // 10 and modulo % 10 preserve arbitrary 100+ digit precision cleanly.",
            "notes_java": "Dummy sentinel eliminates head null checks and avoids mutating caller-owned input nodes."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
        // Write your dummy-head carry propagation solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def addTwoNumbers(self, l1: ListNode | None, l2: ListNode | None) -> ListNode | None:
        # Write your dummy-head carry propagation solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // Write your dummy-head carry propagation solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: [[2, 4, 3], [5, 6, 4]], expected: [7, 0, 8] },
          { input: [[0], [0]], expected: [0] },
          { input: [[9, 9, 9, 9, 9, 9, 9], [9, 9, 9, 9]], expected: [8, 9, 9, 9, 0, 0, 0, 1] },
          { input: [[9, 9], [1]], expected: [0, 0, 1] }
        ];
        """
    },
    "quiz_questions": [
        {
            "category": "Why Not Integer Conversion",
            "prompt": "Why does converting both linked lists into 64-bit Long integers and summing them fail?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Lists can hold 100 digits, overflowing 64-bit Long integers past 19 digits.", "isCorrect": True, "whyRight": "Signed 64-bit Long maxes out around 9.22e18 (19 digits), whereas constraints allow 100-node numbers."},
                {"id": "B", "text": "Linked lists cannot be traversed sequentially without first reversing all pointers.", "isCorrect": False, "whyWrong": "The digits are already stored in least-significant-first order."},
                {"id": "C", "text": "Converting nodes to integers requires quadratic O(N^2) recursive stack frames.", "isCorrect": False, "whyWrong": "Accumulating a Long takes O(N) time and O(1) space, but overflows numerically."},
                {"id": "D", "text": "64-bit integer addition cannot compute modulo remainders on zero digits.", "isCorrect": False, "whyWrong": "Modulo works on zero; the failure is integer overflow above 19 digits."}
            ],
            "allExplanations": "• A is correct: 100-digit numbers exceed 64-bit integers."
        },
        {
            "category": "Loop Condition Design",
            "prompt": "What does including `|| carry > 0` in `while (p != null || q != null || carry > 0)` solve?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "It skips leading zeros when both input lists represent the number zero.", "isCorrect": False, "whyWrong": "Zero + zero runs once for p and q, producing [0] before carry is checked."},
                {"id": "B", "text": "It creates the final overflow digit node (e.g., 99 + 1 = 100) without extra code.", "isCorrect": True, "whyRight": "When both p and q reach null with carry == 1, the loop runs one final iteration, appending ListNode(1)."},
                {"id": "C", "text": "It keeps unequal-length input lists balanced by mutating null pointers in place.", "isCorrect": False, "whyWrong": "Inputs are not mutated; missing nodes simply contribute 0."},
                {"id": "D", "text": "It prevents NullPointerException when p.next advances past the tail node.", "isCorrect": False, "whyWrong": "Null guards inside the loop body handle p.next safety."}
            ],
            "allExplanations": "• B is correct: trailing carry like 99 + 1 -> [0,0,1] is handled automatically."
        },
        {
            "category": "Code Snippet Bug Hunt",
            "prompt": "What defect occurs in this Kotlin snippet when updating carry and creating the node?",
            "codeSnippet": "val sum = carry + x + y\ncurr.next = ListNode(sum / 10)\ncarry = sum % 10\ncurr = curr.next!!",
            "options": [
                {"id": "A", "text": "Dividing by 10 and taking modulo 10 are swapped, storing carry in the node.", "isCorrect": True, "whyRight": "The digit belongs in sum % 10 (ones place) and the carry belongs in sum / 10 (tens place); swapping them corrupts every multi-digit sum!"},
                {"id": "B", "text": "Advancing curr before moving p and q drops the dummy sentinel head.", "isCorrect": False, "whyWrong": "dummy still points to the start of the result list while curr advances."},
                {"id": "C", "text": "Adding x + y before carry produces negative remainders on nine plus nine.", "isCorrect": False, "whyWrong": "Addition is commutative and all digits are non-negative."},
                {"id": "D", "text": "Using !! throws NullPointerException right after assigning curr.next = ListNode(...).", "isCorrect": False, "whyWrong": "curr.next was just assigned a non-null ListNode instance."}
            ],
            "allExplanations": "• A is correct: node value is `sum % 10`, carry is `sum / 10`."
        },
        {
            "category": "Mid-Level Variant Follow-Up",
            "prompt": "If digits were stored in FORWARD order (most significant digit first, LC #445) without reversing inputs, what structure helps?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Two LIFO stacks pushing digits from head to tail so ones digits pop first.", "isCorrect": True, "whyRight": "Pushing both lists onto two stacks reverses digit order into O(M + N) LIFO pops without mutating the input lists."},
                {"id": "B", "text": "A Min-Heap ordering digits from smallest to largest before addition.", "isCorrect": False, "whyWrong": "Heaps sort by digit value, destroying positional place value."},
                {"id": "C", "text": "A HashSet deduplicating matching digits across corresponding list positions.", "isCorrect": False, "whyWrong": "Deduplication loses digit counts and positional ordering."},
                {"id": "D", "text": "Binary search over list lengths to align decimal points in O(log N) time.", "isCorrect": False, "whyWrong": "Linked lists do not support O(1) random access for binary search."}
            ],
            "allExplanations": "• A is correct: two stacks yield least-significant digits first without mutating caller lists."
        }
    ],
    "spoken_script": {
        "phase1": "To confirm: digits are stored in reverse order (least-significant digit at the head), both lists are non-empty up to 100 nodes, and we shouldn't mutate the input lists, correct?",
        "phase2": "Converting to 64-bit integers fails past 19 digits. Instead, since digits are already reversed, we simulate column addition in one O(max(M, N)) pass using a dummy head and a carry integer.",
        "phase3": "I initialize dummy = ListNode(0), curr = dummy, carry = 0, and loop while p != null || q != null || carry > 0. Each step sums carry + (p?.val ?: 0) + (q?.val ?: 0), writes ListNode(sum % 10) to curr.next, updates carry = sum / 10, and advances p, q, and curr.",
        "phase4": "Testing [9, 9] + [1]: step 1 sum=10 -> node(0), carry=1; step 2 sum=9+0+1=10 -> node(0), carry=1; step 3 sum=0+0+1=1 -> node(1), carry=0. Returns [0, 0, 1].",
        "senior_followup": "If digits were stored in forward order (LC #445) and input mutation was disallowed, I would push both lists onto two stacks and build the output list from tail to head."
    }
}

# =========================================================================
# PROBLEM 114: LC #143 REORDER LIST (Pass 2B Stretch Medium)
# =========================================================================
p114 = {
    "num": 114,
    "lc": 143,
    "title": "Reorder List",
    "category": "Linked Lists & System Node Design",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "reorder-list",
    "io_type": "list",
    "prev_file": "0113-lc2-add-two-numbers.html",
    "next_file": "0115-lc152-maximum-product-subarray.html",
    "verbatim_statement": "You are given the head of a singly linked-list. The list can be represented as:<br><code>L0 &rarr; L1 &rarr; ... &rarr; Ln - 1 &rarr; Ln</code><br>Reorder the list to be on the following form:<br><code>L0 &rarr; Ln &rarr; L1 &rarr; Ln - 1 &rarr; L2 &rarr; Ln - 2 &rarr; ...</code><br><br>You may not modify the values in the list's nodes. Only nodes themselves may be changed.",
    "examples": [
        {"title": "Example 1", "input": "head = [1,2,3,4]", "output": "[1,4,2,3]"},
        {"title": "Example 2", "input": "head = [1,2,3,4,5]", "output": "[1,5,2,4,3]"},
        {"title": "Example 3", "input": "head = [1]", "output": "[1]"}
    ],
    "constraints": [
        "The number of nodes in the list is in the range [1, 5 * 10^4].",
        "1 <= Node.val <= 1000"
    ],
    "plain_english": "Fold a singly linked list in half from the outside in (1st -> last -> 2nd -> 2nd-to-last...) strictly in O(1) extra space by rewiring node pointers.",
    "boundary_checks": {
        "min_base": "head = [1] or [1, 2] -> Zero or one link; loop terminates immediately with zero cycles.",
        "best_exit": "head = [1, 2, 3, 4] (Even length) -> Split [1,2] and [4,3], zip into [1,4,2,3].",
        "worst_scan": "head = [1, 2, 3, 4, 5] (Odd length) -> Split [1,2,3] and [5,4]; MUST sever slow.next = null to prevent cycle!"
    },
    "mental_model": {
        "analogy_title": "Cutting a Deck of Cards & Perfect Riffle Shuffle",
        "analogy_text": "You can't walk backwards in a singly linked list. So to pair the front card with the back card, cut the deck cleanly in half using slow/fast pointers, reverse the second half on the table so the bottom card is now on top, and zip the two halves together one card at a time!",
        "aha_moment": "Reorder List is a 3-act synthesis of Pass 1 primitives: (1) Find Middle (Slow/Fast) + Cut (`slow.next = null`), (2) Reverse Second Half (`#206`), (3) Interleave Merge (`#21`-style).",
        "trigger": "Pairing front-and-back elements in a singly linked list in O(1) space.",
        "sisters": [
            {"name": "Reverse Linked List", "lc": "#206"},
            {"name": "Linked List Cycle", "lc": "#141"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N) Time, O(N) Space",
        "naive_text": "Copy all ListNode references into an ArrayList, then rewire pointers using inward two pointers left++ and right--.",
        "wasted_work": "Allocates an O(N) pointer array on the heap instead of doing pure O(1) in-place pointer rewiring.",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "1) Find middle with slow/fast; 2) Split list via second = slow.next; slow.next = null; 3) Reverse second half; 4) Interleave first and second halves alternating next pointers.",
        "core_invariant": "Cutting slow.next = null guarantees two strictly disjoint acyclic halves before interleaving.",
        "table_rows": [
            {"name": "Better: ArrayList + Two Pointers", "time": "O(N)", "space": "O(N)", "note": "Valid warmup, but wastes O(N) heap array."},
            {"name": "Optimal: Mid-Split + Reverse + Zip", "time": "O(N)", "space": "O(1)", "note": "★ Canonical O(1) pointer synthesis."}
        ]
    },
    "trace_table": {
        "input_desc": "head = [1, 2, 3, 4, 5]",
        "columns": ["Phase", "First Half", "Second Half", "Pointer Action"],
        "rows": [
            ["1. Find Mid", "slow at 3, fast at 5", "-", "Middle node is 3"],
            ["2. Split & Reverse", "1 -> 2 -> 3 -> null", "5 -> 4 -> null", "Sever 3.next = null; reverse [4,5] -> [5,4]"],
            ["3. Zip Step 1", "2 -> 3", "4", "1 -> 5 -> 2"],
            ["4. Zip Step 2", "3", "null", "1 -> 5 -> 2 -> 4 -> 3 -> null"]
        ],
        "takeaway": "Forgetting `slow.next = null` leaves node 3 pointing to 4, creating an infinite cycle when zipping!"
    },
    "stepper": {
        "title": "3-Phase Split, Reverse & Zip Stepper",
        "default_input": "1, 2, 3, 4, 5",
        "presets": [
            {"label": "Odd [1,2,3,4,5]", "onclick": "loadPreset([1,2,3,4,5])"},
            {"label": "Even [1,2,3,4]", "onclick": "loadPreset([1,2,3,4])"},
            {"label": "Pair [1,2]", "onclick": "loadPreset([1,2])"}
        ],
        "metrics": [
            {"label": "Phase", "id": "metric-phase", "initial": "Find Mid", "color": "text-purple-400"},
            {"label": "Half 1", "id": "metric-h1", "initial": "-", "color": "text-cyan-400"},
            {"label": "Half 2", "id": "metric-h2", "initial": "-", "color": "text-amber-400"},
            {"label": "Reordered", "id": "metric-out", "initial": "-", "color": "text-emerald-400"}
        ],
        "visual_html": "<div id='reorder-viz' class='flex flex-wrap items-center justify-center gap-2'></div>",
        "initial_narration": "Phase 1 finds the midpoint with slow/fast pointers.",
        "pseudocode_lines": [
            "slow = head; fast = head",
            "while (fast.next != null && fast.next.next != null) { slow = slow.next; fast = fast.next.next }",
            "second = reverse(slow.next); slow.next = null",
            "first = head",
            "while (second != null) {",
            "  t1 = first.next; t2 = second.next",
            "  first.next = second; second.next = t1",
            "  first = t1; second = t2",
            "}"
        ],
        "js_code": """
        let roArr = [1, 2, 3, 4, 5];
        function buildSteps(arr) {
          if (Array.isArray(arr) && arr.length) roArr = arr;
          steps = [];
          let n = roArr.length;
          let mid = Math.floor((n - 1) / 2);
          let h1 = roArr.slice(0, mid + 1);
          let h2Raw = roArr.slice(mid + 1);
          let h2 = [...h2Raw].reverse();
          steps.push({ phase: '1. Find Mid', h1: roArr, h2: [], out: roArr, hi: mid, line: 2, narration: `Slow/Fast stops at slow index ${mid} (val=${roArr[mid]}).` });
          steps.push({ phase: '2. Split & Reverse', h1, h2, out: [...h1, ...h2], hi: -1, line: 3, narration: `Cut at slow.next=null -> First=${JSON.stringify(h1)}, Reversed Second=${JSON.stringify(h2)}.` });
          let zipped = [];
          for (let i = 0; i < h1.length; i++) {
            zipped.push(h1[i]);
            if (i < h2.length) zipped.push(h2[i]);
            steps.push({ phase: '3. Zip Merge', h1: h1.slice(i+1), h2: h2.slice(i+1), out: [...zipped], hi: zipped.length - 1, line: 7, narration: `Interleaved step ${i+1}: zipped prefix = ${JSON.stringify(zipped)}.` });
          }
          steps.push({ phase: 'Complete', h1: [], h2: [], out: zipped, hi: -1, line: 9, narration: `Reordered in-place in O(1) space: ${JSON.stringify(zipped)}.` });
        }
        function renderStage(step) {
          const c = document.getElementById('reorder-viz');
          if (!c) return;
          c.innerHTML = step.out.map((v, idx) => {
            let cls = idx === step.hi ? 'border-emerald-400 bg-emerald-950/60 text-emerald-300 scale-105' : 'border-cyan-500/40 bg-slate-900 text-slate-200';
            return `<div class="w-11 h-11 rounded-xl border-2 flex items-center justify-center font-mono text-sm font-bold ${cls}">${v}</div>`;
          }).join('<span class="text-slate-600 font-mono">&rarr;</span>');
        }
        function loadPreset(arr) { buildSteps(arr); stepperReset(); }
        """,
        "js_custom_input": """function handleCustomInput() {
          const el = document.getElementById('custom-input');
          if (!el) return;
          const parsed = el.value.replace(/[\\[\\]]/g, '').split(',').map(s => parseInt(s.trim(), 10)).filter(n => !isNaN(n));
          if (parsed.length) loadPreset(parsed);
        }""",
        "js_update_metrics": """
        document.getElementById('metric-phase').textContent = step.phase;
        document.getElementById('metric-h1').textContent = JSON.stringify(step.h1);
        document.getElementById('metric-h2').textContent = JSON.stringify(step.h2);
        document.getElementById('metric-out').textContent = JSON.stringify(step.out);
        """
    },
    "approaches": [
        {
            "name": "Approach 1: ArrayList Copy + Two Pointers",
            "badge": "Better",
            "desc": "Store node pointers in a flat ArrayList and rewire using inward left/right indices.",
            "time": "O(N)",
            "space": "O(N)",
            "code_kotlin": """class Solution {
    fun reorderList(head: ListNode?): Unit {
        if (head?.next == null) return
        val nodes = ArrayList<ListNode>()
        var curr = head
        while (curr != null) {
            nodes.add(curr)
            curr = curr.next
        }
        var l = 0
        var r = nodes.size - 1
        while (l < r) {
            nodes[l].next = nodes[r]
            l++
            if (l == r) break
            nodes[r].next = nodes[l]
            r--
        }
        nodes[l].next = null
    }
}""",
            "code_python": """class Solution:
    def reorderList(self, head: ListNode | None) -> None:
        if not head or not head.next:
            return
        nodes = []
        curr = head
        while curr:
            nodes.append(curr)
            curr = curr.next
        l, r = 0, len(nodes) - 1
        while l < r:
            nodes[l].next = nodes[r]
            l += 1
            if l == r:
                break
            nodes[r].next = nodes[l]
            r -= 1
        nodes[l].next = None""",
            "code_java": """import java.util.*;

class Solution {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) return;
        List<ListNode> nodes = new ArrayList<>();
        ListNode curr = head;
        while (curr != null) {
            nodes.add(curr);
            curr = curr.next;
        }
        int l = 0, r = nodes.size() - 1;
        while (l < r) {
            nodes.get(l).next = nodes.get(r);
            l++;
            if (l == r) break;
            nodes.get(r).next = nodes.get(l);
            r--;
        }
        nodes.get(l).next = null;
    }
}""",
            "notes_kotlin": "Easy to reason about, but uses O(N) auxiliary list memory.",
            "notes_python": "Clean random indexing, though interviewers follow up asking for O(1) extra space.",
            "notes_java": "Must remember nodes.get(l).next = null at the end to terminate the reordered chain."
        },
        {
            "name": "Approach 2: Mid-Split + Reverse + In-Place Zip",
            "badge": "Optimal",
            "desc": "Find middle via slow/fast, sever & reverse second half, and interleave nodes in O(1) space.",
            "time": "O(N)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun reorderList(head: ListNode?): Unit {
        if (head?.next == null) return
        var slow = head
        var fast = head
        while (fast?.next != null && fast.next?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }
        var second = reverseList(slow?.next)
        slow?.next = null

        var first = head
        while (second != null) {
            val tmp1 = first?.next
            val tmp2 = second.next
            first?.next = second
            second.next = tmp1
            first = tmp1
            second = tmp2
        }
    }

    private fun reverseList(node: ListNode?): ListNode? {
        var prev: ListNode? = null
        var curr = node
        while (curr != null) {
            val nxt = curr.next
            curr.next = prev
            prev = curr
            curr = nxt
        }
        return prev
    }
}""",
            "code_python": """class Solution:
    def reorderList(self, head: ListNode | None) -> None:
        if not head or not head.next:
            return
        slow, fast = head, head
        while fast.next and fast.next.next:
            slow = slow.next
            fast = fast.next.next

        prev, curr = None, slow.next
        slow.next = None
        while curr:
            nxt = curr.next
            curr.next = prev
            prev = curr
            curr = nxt

        first, second = head, prev
        while second:
            tmp1, tmp2 = first.next, second.next
            first.next = second
            second.next = tmp1
            first, second = tmp1, tmp2""",
            "code_java": """class Solution {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) return;
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode second = reverseList(slow.next);
        slow.next = null;

        ListNode first = head;
        while (second != null) {
            ListNode tmp1 = first.next;
            ListNode tmp2 = second.next;
            first.next = second;
            second.next = tmp1;
            first = tmp1;
            second = tmp2;
        }
    }

    private ListNode reverseList(ListNode node) {
        ListNode prev = null, curr = node;
        while (curr != null) {
            ListNode nxt = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nxt;
        }
        return prev;
    }
}""",
            "notes_kotlin": "Strict O(1) space using only scalar node references across all 3 phases.",
            "notes_python": "Saving tmp1, tmp2 before rewiring prevents losing tail references during interleaving.",
            "notes_java": "Severing slow.next = null before merging guarantees zero linked-list cycles."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun reorderList(head: ListNode?): Unit {
        // Write your O(1) space Mid-Split + Reverse + Zip solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def reorderList(self, head: ListNode | None) -> None:
        # Write your O(1) space Mid-Split + Reverse + Zip solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public void reorderList(ListNode head) {
        // Write your O(1) space Mid-Split + Reverse + Zip solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: [[1, 2, 3, 4]], expected: [1, 4, 2, 3] },
          { input: [[1, 2, 3, 4, 5]], expected: [1, 5, 2, 4, 3] },
          { input: [[1]], expected: [1] },
          { input: [[1, 2]], expected: [1, 2] }
        ];
        """
    },
    "quiz_questions": [
        {
            "category": "Space Complexity Trade-Off",
            "prompt": "Why is the 3-phase Mid-Split + Reverse + Zip approach preferred over copying nodes into an ArrayList?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "It eliminates O(N) auxiliary pointer storage to run in O(1) extra space.", "isCorrect": True, "whyRight": "Rewiring next pointers directly achieves O(N) time and strictly O(1) auxiliary space without allocating an N-element array."},
                {"id": "B", "text": "ArrayList random access runs in O(N) per lookup when storing linked nodes.", "isCorrect": False, "whyWrong": "ArrayList indexing is O(1); the penalty is O(N) extra heap memory."},
                {"id": "C", "text": "Copying ListNodes into an ArrayList mutates node integer values illegally.", "isCorrect": False, "whyWrong": "Storing references does not alter node.val values."},
                {"id": "D", "text": "Reversing a linked list reduces total runtime from O(N) down to O(log N).", "isCorrect": False, "whyWrong": "Both approaches run in O(N) linear time."}
            ],
            "allExplanations": "• A is correct: O(1) extra space vs O(N) ArrayList."
        },
        {
            "category": "Pointer Safety",
            "prompt": "Why MUST we execute `slow.next = null` after capturing `second = slow.next` and before interleaving?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Otherwise the tail of the first half still points into the second half, creating a cycle.", "isCorrect": True, "whyRight": "After reversing the second half, slow.next still points to the old middle+1 node (now the tail of second!), forming a cycle if unsevered."},
                {"id": "B", "text": "Otherwise reverseList reverses the entire first half along with the second half.", "isCorrect": False, "whyWrong": "reverseList only walks forward from slow.next, not backward."},
                {"id": "C", "text": "Otherwise the fast pointer skips the last node on odd-length lists.", "isCorrect": False, "whyWrong": "fast already finished Phase 1 before the split."},
                {"id": "D", "text": "Otherwise garbage collection immediately frees head before zipping begins.", "isCorrect": False, "whyWrong": "head remains reachable through the local variable."}
            ],
            "allExplanations": "• A is correct: leaving slow.next connected creates a cycle during zipping."
        },
        {
            "category": "Code Snippet Bug Hunt",
            "prompt": "What happens in the zip loop if `first.next = second` runs BEFORE saving `tmp1 = first.next`?",
            "codeSnippet": "while (second != null) {\n    first.next = second\n    val tmp1 = first.next\n    val tmp2 = second.next\n    second.next = tmp1\n    first = tmp1; second = tmp2\n}",
            "options": [
                {"id": "A", "text": "The remaining nodes of the first half are lost and second points to itself.", "isCorrect": True, "whyRight": "Overwriting first.next = second makes tmp1 equal second, so second.next = second creates a self-loop and drops the rest of first!"},
                {"id": "B", "text": "The loop skips every even-indexed node from the reversed second half.", "isCorrect": False, "whyWrong": "It immediately loses first.next and creates a self-cycle on second."},
                {"id": "C", "text": "It reverses the interleaved list into descending order cleanly.", "isCorrect": False, "whyWrong": "It corrupts the chain into a cycle."},
                {"id": "D", "text": "It triggers StackOverflowError because second.next becomes recursive.", "isCorrect": False, "whyWrong": "The while loop is iterative, though it loops infinitely on the self-cycle."}
            ],
            "allExplanations": "• A is correct: always save `tmp1 = first.next` and `tmp2 = second.next` BEFORE mutating `.next`."
        },
        {
            "category": "Loop Termination",
            "prompt": "Why does `while (second != null)` cleanly terminate for BOTH odd and even length lists?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "First half always has equal or one more node than second, so second exhausts first.", "isCorrect": True, "whyRight": "Splitting at slow leaves ceil(N/2) nodes in first and floor(N/2) in second; once all second nodes are spliced in, the remaining node in first is already trailing at null."},
                {"id": "B", "text": "Second half always contains one extra dummy sentinel node at the tail.", "isCorrect": False, "whyWrong": "Neither half uses a dummy sentinel in the optimal split."},
                {"id": "C", "text": "Odd-length lists always have an extra node appended after the zip loop ends.", "isCorrect": False, "whyWrong": "On odd lengths, the middle node stays at the end of the first half naturally."},
                {"id": "D", "text": "Fast pointer sets second to null whenever an even number of nodes exist.", "isCorrect": False, "whyWrong": "second is only null when the list has <= 1 node."}
            ],
            "allExplanations": "• A is correct: length(first) >= length(second) by 0 or 1, so exhausting second finishes the zip."
        }
    ],
    "spoken_script": {
        "phase1": "Let me confirm: we must reorder the linked list in-place by rewiring `.next` pointers without modifying node values, and target O(1) extra space, correct?",
        "phase2": "Storing nodes in an ArrayList works in O(N) time and O(N) space. To get O(1) space, we combine three standard linked-list primitives: find the midpoint with slow/fast pointers, sever and reverse the second half, and interleave the two halves.",
        "phase3": "First, advance slow by 1 and fast by 2 while fast.next != null && fast.next.next != null. Second, reverse slow.next and set slow.next = null to prevent a cycle. Third, zip first and second by saving tmp1 = first.next, tmp2 = second.next, linking first.next = second, second.next = tmp1, and advancing both.",
        "phase4": "Tracing [1, 2, 3, 4, 5]: slow stops at 3. First half is 1->2->3->null, reversed second is 5->4->null. Zip 1->5->2, then 2->4->3->null. Output [1, 5, 2, 4, 3].",
        "senior_followup": "Note that if we needed the list unmodified for concurrent readers, we would deep-copy nodes or use the ArrayList approach rather than mutating `.next` pointers in place."
    }
}

# =========================================================================
# PROBLEM 115: LC #152 MAXIMUM PRODUCT SUBARRAY (Pass 2B Stretch Medium)
# =========================================================================
p115 = {
    "num": 115,
    "lc": 152,
    "title": "Maximum Product Subarray",
    "category": "Dynamic Programming",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "maximum-product-subarray",
    "prev_file": "0114-lc143-reorder-list.html",
    "next_file": "0116-lc1091-shortest-path-in-binary-matrix.html",
    "verbatim_statement": "Given an integer array <code>nums</code>, find a subarray that has the largest product, and return <em>the product</em>.<br><br>The test cases are generated so that the answer will fit in a <strong>32-bit</strong> integer.",
    "examples": [
        {"title": "Example 1", "input": "nums = [2,3,-2,4]", "output": "6 (subarray [2,3])"},
        {"title": "Example 2", "input": "nums = [-2,0,-1]", "output": "0 (cannot be [-2,-1] because 0 separates them)"},
        {"title": "Example 3", "input": "nums = [-2,3,-4]", "output": "24 (product of entire array)"}
    ],
    "constraints": [
        "1 <= nums.length <= 2 * 10^4",
        "-10 <= nums[i] <= 10",
        "The product of any subarray of nums is guaranteed to fit in a 32-bit integer."
    ],
    "plain_english": "Like Maximum Subarray (#53), find the highest-product contiguous slice—but track BOTH the running max AND running min because two negatives multiply into a giant positive!",
    "boundary_checks": {
        "min_base": "nums = [-3] -> Single negative element; global max initialized to nums[0] returns -3.",
        "best_exit": "nums = [-2, 0, -1] -> Zero resets both curMax and curMin cleanly; answer is 0.",
        "worst_scan": "nums = [-2, 3, -4] -> curMin (-6) flips sign on -4 to become curMax = +24!"
    },
    "mental_model": {
        "analogy_title": "The Jekyll-and-Hyde Sign-Flip Multiplier",
        "analogy_text": "In Maximum Sum (#53), a huge negative streak is garbage—you throw it away. In Maximum Product, a huge negative product like -100 is a sleeping superhero: the moment it hits another negative number like -2, (-100) * (-2) rockets to +200! So at every index, you carry two champions forward: the largest positive product (curMax) AND the most negative product (curMin).",
        "aha_moment": "Track BOTH curMax and curMin ending at i! When nums[i] < 0, swap curMax and curMin (or take max/min across {n, n * prevMax, n * prevMin}), because negative times negative flips min into max!",
        "trigger": "Contiguous subarray product optimization with negative numbers and zeros.",
        "sisters": [
            {"name": "Maximum Subarray", "lc": "#53"},
            {"name": "Product of Array Except Self", "lc": "#238"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N^2) Time, O(1) Space",
        "naive_text": "Try all pairs (i, j) with nested loops accumulating running product.",
        "wasted_work": "Recomputes overlapping subarray products across 2 * 10^4 elements -> 4 * 10^8 operations (TLE).",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Maintain curMax and curMin initialized to nums[0] (and ans = nums[0]). For each nums[i], save prevMax = curMax; curMax = max(n, n * prevMax, n * curMin); curMin = min(n, n * prevMax, n * curMin); ans = max(ans, curMax).",
        "core_invariant": "At step i, curMax and curMin equal the exact maximum and minimum subarray products ending at index i.",
        "table_rows": [
            {"name": "Naive: All Subarray Pairs", "time": "O(N^2)", "space": "O(1)", "note": "TLE at N = 20,000."},
            {"name": "Trap: Single curMax Kadane's", "time": "O(N)", "space": "O(1)", "note": "Fails on [-2, 3, -4] because it discards -6."},
            {"name": "Optimal: Dual curMax/curMin DP", "time": "O(N)", "space": "O(1)", "note": "★ Handles negatives, zeros, and sign flips in O(1) space."}
        ]
    },
    "trace_table": {
        "input_desc": "nums = [-2, 3, -4]",
        "columns": ["i", "nums[i]", "prevMax / prevMin", "curMax", "curMin", "globalMax"],
        "rows": [
            ["0", "-2", "init", "-2", "-2", "-2"],
            ["1", "3", "-2 / -2", "max(3, -6, -6) = 3", "min(3, -6, -6) = -6", "3"],
            ["2", "-4", "3 / -6", "max(-4, -12, 24) = 24", "min(-4, -12, 24) = -12", "24"]
        ],
        "takeaway": "Because we kept `curMin = -6` at index 1, multiplying by `-4` at index 2 unlocks `(-6) * (-4) = 24`!"
    },
    "stepper": {
        "title": "Dual-Extremum (curMax / curMin) Kadane Stepper",
        "default_input": "-2, 3, -4",
        "presets": [
            {"label": "Sign Flip [-2,3,-4]", "onclick": "loadPreset([-2,3,-4])"},
            {"label": "Zero Reset [-2,0,-1]", "onclick": "loadPreset([-2,0,-1])"},
            {"label": "Standard [2,3,-2,4]", "onclick": "loadPreset([2,3,-2,4])"}
        ],
        "metrics": [
            {"label": "Index / Val", "id": "metric-iv", "initial": "0 (-2)", "color": "text-cyan-400"},
            {"label": "curMax", "id": "metric-cmax", "initial": "-2", "color": "text-amber-400"},
            {"label": "curMin", "id": "metric-cmin", "initial": "-2", "color": "text-purple-400"},
            {"label": "Global Max", "id": "metric-gmax", "initial": "-2", "color": "text-emerald-400"}
        ],
        "visual_html": "<div id='prod-viz' class='flex flex-wrap items-center justify-center gap-2'></div>",
        "initial_narration": "Tracking curMax and curMin simultaneously to catch double-negative sign flips.",
        "pseudocode_lines": [
            "curMax = nums[0]; curMin = nums[0]; ans = nums[0]",
            "for (i in 1 until nums.size) {",
            "  val n = nums[i]; val prevMax = curMax",
            "  curMax = maxOf(n, n * prevMax, n * curMin)",
            "  curMin = minOf(n, n * prevMax, n * curMin)",
            "  ans = maxOf(ans, curMax)",
            "}",
            "return ans"
        ],
        "js_code": """
        let mpArr = [-2, 3, -4];
        function buildSteps(arr) {
          if (Array.isArray(arr) && arr.length) mpArr = arr;
          steps = [];
          let cMax = mpArr[0], cMin = mpArr[0], ans = mpArr[0];
          steps.push({ idx: 0, val: mpArr[0], cMax, cMin, ans, line: 1, narration: `Initialized curMax=${cMax}, curMin=${cMin}, ans=${ans} at index 0.` });
          for (let i = 1; i < mpArr.length; i++) {
            let n = mpArr[i], pMax = cMax, pMin = cMin;
            cMax = Math.max(n, n * pMax, n * pMin);
            cMin = Math.min(n, n * pMax, n * pMin);
            ans = Math.max(ans, cMax);
            steps.push({ idx: i, val: n, cMax, cMin, ans, line: 6, narration: `i=${i} (val=${n}): candidates=[${n}, ${n*pMax}, ${n*pMin}] -> curMax=${cMax}, curMin=${cMin}, ans=${ans}.` });
          }
          steps.push({ idx: -1, val: '-', cMax, cMin, ans, line: 8, narration: `Scan complete! Maximum product subarray = ${ans}.` });
        }
        function renderStage(step) {
          const c = document.getElementById('prod-viz');
          if (!c) return;
          c.innerHTML = mpArr.map((v, idx) => {
            let cls = idx === step.idx ? 'border-emerald-400 bg-emerald-950/60 text-emerald-300 scale-105' : 'border-slate-800 bg-slate-900 text-slate-300';
            return `<div class="w-12 h-14 rounded-xl border-2 flex flex-col items-center justify-center font-mono text-xs ${cls}">
              <span class="text-[9px] text-slate-500">[${idx}]</span>
              <span class="font-bold">${v}</span>
            </div>`;
          }).join('');
        }
        function loadPreset(arr) { buildSteps(arr); stepperReset(); }
        """,
        "js_custom_input": """function handleCustomInput() {
          const el = document.getElementById('custom-input');
          if (!el) return;
          const parsed = el.value.replace(/[\\[\\]]/g, '').split(',').map(s => parseInt(s.trim(), 10)).filter(n => !isNaN(n));
          if (parsed.length) loadPreset(parsed);
        }""",
        "js_update_metrics": """
        document.getElementById('metric-iv').textContent = step.idx >= 0 ? `${step.idx} (${step.val})` : 'Done';
        document.getElementById('metric-cmax').textContent = step.cMax;
        document.getElementById('metric-cmin').textContent = step.cMin;
        document.getElementById('metric-gmax').textContent = step.ans;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Brute-Force All Subarrays",
            "badge": "Naive",
            "desc": "Evaluate every contiguous [i..j] product in nested loops.",
            "time": "O(N^2)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun maxProduct(nums: IntArray): Int {
        var ans = nums[0]
        for (i in nums.indices) {
            var prod = 1
            for (j in i until nums.size) {
                prod *= nums[j]
                if (prod > ans) ans = prod
            }
        }
        return ans
    }
}""",
            "code_python": """class Solution:
    def maxProduct(self, nums: list[int]) -> int:
        ans = nums[0]
        for i in range(len(nums)):
            prod = 1
            for j in range(i, len(nums)):
                prod *= nums[j]
                if prod > ans:
                    ans = prod
        return ans""",
            "code_java": """class Solution {
    public int maxProduct(int[] nums) {
        int ans = nums[0];
        for (int i = 0; i < nums.length; i++) {
            int prod = 1;
            for (int j = i; j < nums.length; j++) {
                prod *= nums[j];
                if (prod > ans) ans = prod;
            }
        }
        return ans;
    }
}""",
            "notes_kotlin": "Quadratic O(N^2) exceeds time limits when N approaches 20,000.",
            "notes_python": "Pure interpreter nested loops time out quickly on N = 2 * 10^4.",
            "notes_java": "Useful only as a spoken baseline before introducing curMax/curMin DP."
        },
        {
            "name": "Approach 2: Dual Extremum Rolling DP",
            "badge": "Optimal",
            "desc": "Maintain both curMax and curMin at each step so negative sign flips promote curMin to curMax.",
            "time": "O(N)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun maxProduct(nums: IntArray): Int {
        var curMax = nums[0]
        var curMin = nums[0]
        var ans = nums[0]
        for (i in 1 until nums.size) {
            val n = nums[i]
            val prevMax = curMax
            curMax = maxOf(n, maxOf(n * prevMax, n * curMin))
            curMin = minOf(n, minOf(n * prevMax, n * curMin))
            if (curMax > ans) ans = curMax
        }
        return ans
    }
}""",
            "code_python": """class Solution:
    def maxProduct(self, nums: list[int]) -> int:
        cur_max = cur_min = ans = nums[0]
        for n in nums[1:]:
            prev_max = cur_max
            cur_max = max(n, n * prev_max, n * cur_min)
            cur_min = min(n, n * prev_max, n * cur_min)
            ans = max(ans, cur_max)
        return ans""",
            "code_java": """class Solution {
    public int maxProduct(int[] nums) {
        int curMax = nums[0], curMin = nums[0], ans = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int n = nums[i];
            int prevMax = curMax;
            curMax = Math.max(n, Math.max(n * prevMax, n * curMin));
            curMin = Math.min(n, Math.min(n * prevMax, n * curMin));
            if (curMax > ans) ans = curMax;
        }
        return ans;
    }
}""",
            "notes_kotlin": "Snapshots prevMax before overwriting curMax so curMin computes against the prior step's max.",
            "notes_python": "Uses O(1) scalar variables; can also iterate by index range(1, len(nums)) to avoid slicing nums[1:].",
            "notes_java": "Zero heap allocations; handles zeros naturally because max(n, 0, 0) resets on the next non-zero."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun maxProduct(nums: IntArray): Int {
        // Write your O(N) curMax / curMin DP solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def maxProduct(self, nums: list[int]) -> int:
        # Write your O(N) curMax / curMin DP solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public int maxProduct(int[] nums) {
        // Write your O(N) curMax / curMin DP solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: [[2, 3, -2, 4]], expected: 6 },
          { input: [[-2, 0, -1]], expected: 0 },
          { input: [[-2, 3, -4]], expected: 24 },
          { input: [[-3]], expected: -3 }
        ];
        """
    },
    "quiz_questions": [
        {
            "category": "Core DP State Design",
            "prompt": "Why does standard Kadane's algorithm (tracking only curMax) fail on Maximum Product Subarray?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "A negative running product can flip into the maximum after another negative number.", "isCorrect": True, "whyRight": "On [-2, 3, -4], tracking only curMax discards -6 at index 1, missing (-6) * (-4) = +24 at index 2."},
                {"id": "B", "text": "Multiplication distributes differently over zeros than addition inside contiguous subarrays.", "isCorrect": False, "whyWrong": "Zero resets both sum/product chains similarly; the critical difference is negative sign flips."},
                {"id": "C", "text": "Kadane's algorithm requires sorting the input array before multiplying values.", "isCorrect": False, "whyWrong": "Sorting destroys contiguous subarray order."},
                {"id": "D", "text": "Tracking only curMax allocates O(N) heap memory instead of O(1) registers.", "isCorrect": False, "whyWrong": "Standard Kadane's also uses O(1) space; it fails on correctness, not space."}
            ],
            "allExplanations": "• A is correct: two negatives multiply into a large positive."
        },
        {
            "category": "Zero Handling",
            "prompt": "What happens automatically in curMax = max(n, n * prevMax, n * curMin) when nums[i] == 0?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Both curMax and curMin become 0, resetting the subarray cleanly on the next element.", "isCorrect": True, "whyRight": "At 0, all three terms evaluate to 0; at the next element m, n * 0 is 0 so max/min can start fresh at m."},
                {"id": "B", "text": "It multiplies by zero and crashes on division by zero on the next iteration.", "isCorrect": False, "whyWrong": "No division is ever performed in this algorithm."},
                {"id": "C", "text": "It skips zero and preserves the prior curMax across the zero boundary.", "isCorrect": False, "whyWrong": "Preserving product across 0 would illegally span a non-zero break; ans retains any earlier peak."},
                {"id": "D", "text": "It returns zero immediately before checking any positive numbers later in the array.", "isCorrect": False, "whyWrong": "The loop continues so later subarrays like [0, 5, 6] still produce 30."}
            ],
            "allExplanations": "• A is correct: 0 resets curMax and curMin to 0 while ans remembers earlier peaks."
        },
        {
            "category": "Code Snippet Bug Hunt",
            "prompt": "What subtle bug occurs when curMin uses the ALREADY-UPDATED curMax instead of prevMax?",
            "codeSnippet": "curMax = maxOf(n, maxOf(n * curMax, n * curMin))\ncurMin = minOf(n, minOf(n * curMax, n * curMin))",
            "options": [
                {"id": "A", "text": "curMin uses the new step's curMax instead of the previous step's curMax.", "isCorrect": True, "whyRight": "Mutating curMax first corrupts curMin's candidate n * curMax (multiplying n twice!). You must snapshot prevMax first."},
                {"id": "B", "text": "ans fails to initialize when nums has only negative integer values.", "isCorrect": False, "whyWrong": "ans is initialized to nums[0], which handles negative-only arrays."},
                {"id": "C", "text": "maxOf throws ClassCastException when comparing negative and positive integers.", "isCorrect": False, "whyWrong": "maxOf compares Int primitives cleanly."},
                {"id": "D", "text": "The loop executes backwards from nums.size - 1 down to zero.", "isCorrect": False, "whyWrong": "The loop direction is unaffected."}
            ],
            "allExplanations": "• A is correct: always save `val prevMax = curMax` (or swap curMax/curMin when n < 0)."
        },
        {
            "category": "Edge Case Analysis",
            "prompt": "Why must ans, curMax, and curMin initialize to nums[0] (not 0 or 1) at the start?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Initializing to 0 wrongly returns 0 for single-element negative arrays like [-3].", "isCorrect": True, "whyRight": "The subarray must be non-empty! For [-3], the only valid answer is -3, not 0."},
                {"id": "B", "text": "Initializing to 1 fails whenever the array contains only even positive numbers.", "isCorrect": False, "whyWrong": "Positive numbers > 1 exceed 1, though 0 or [-3] breaks wrong initializers."},
                {"id": "C", "text": "Initializing to nums[0] forces O(N) auxiliary array allocation for prefix products.", "isCorrect": False, "whyWrong": "Scalar variables still use strictly O(1) space."},
                {"id": "D", "text": "Starting the loop at index 0 with ans = 0 overflows 32-bit integers.", "isCorrect": False, "whyWrong": "The prompt guarantees all subarray products fit in 32-bit signed ints."}
            ],
            "allExplanations": "• A is correct: on [-3], ans = 0 would wrongly beat -3."
        }
    ],
    "spoken_script": {
        "phase1": "To confirm: the array is non-empty (length >= 1), products fit in 32-bit signed ints, and a subarray must be contiguous and non-empty, correct?",
        "phase2": "Unlike Maximum Subarray (#53), a negative product can become our largest positive product as soon as it meets another negative number. So we track BOTH curMax and curMin ending at each index in O(N) time and O(1) space.",
        "phase3": "Initialize curMax = curMin = ans = nums[0]. For each i from 1 to n-1, snapshot prevMax = curMax, update curMax = max(n, n * prevMax, n * curMin) and curMin = min(n, n * prevMax, n * curMin), and update ans = max(ans, curMax).",
        "phase4": "Tracing [-2, 3, -4]: start (-2, -2, ans=-2). At 3: curMax=3, curMin=-6, ans=3. At -4: (-6)*(-4) = 24 -> curMax=24, curMin=-12, ans=24. Returns 24.",
        "senior_followup": "An elegant alternative is a two-pass prefix/suffix product scan resetting to 1 whenever 0 is hit—which works because any max product without 0 must extend to either the left or right boundary of a zero-free segment."
    }
}

# =========================================================================
# PROBLEM 116: LC #1091 SHORTEST PATH IN BINARY MATRIX (Pass 2B Stretch)
# =========================================================================
p116 = {
    "num": 116,
    "lc": 1091,
    "title": "Shortest Path in Binary Matrix",
    "category": "2D Grid Traversals & Graphs",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "shortest-path-in-binary-matrix",
    "prev_file": "0115-lc152-maximum-product-subarray.html",
    "next_file": "../master-plan.html",
    "verbatim_statement": "Given an <code>n x n</code> binary matrix <code>grid</code>, return <em>the length of the shortest <strong>clear path</strong> in the matrix</em>. If there is no clear path, return <code>-1</code>.<br><br>A <strong>clear path</strong> in a binary matrix is a path from the <strong>top-left</strong> cell <code>(0, 0)</code> to the <strong>bottom-right</strong> cell <code>(n - 1, n - 1)</code> such that:<br>&bull; All the visited cells of the path are <code>0</code>.<br>&bull; All the adjacent cells of the path are <strong>8-directionally</strong> connected (i.e., they are different and they share an edge or a corner).",
    "examples": [
        {"title": "Example 1", "input": "grid = [[0,1],[1,0]]", "output": "2"},
        {"title": "Example 2", "input": "grid = [[0,0,0],[1,1,0],[1,1,0]]", "output": "4"},
        {"title": "Example 3", "input": "grid = [[1,0,0],[1,1,0],[1,1,0]]", "output": "-1 (start blocked)"}
    ],
    "constraints": [
        "n == grid.length == grid[i].length",
        "1 <= n <= 100",
        "grid[i][j] is 0 or 1."
    ],
    "plain_english": "Find the minimum number of cells in an 8-directional walk (including diagonals!) from top-left (0, 0) to bottom-right (n-1, n-1) stepping only on 0s.",
    "boundary_checks": {
        "min_base": "grid = [[0]] -> Start IS destination; shortest path length is 1 (1 cell visited).",
        "best_exit": "grid[0][0] == 1 or grid[n-1][n-1] == 1 -> Start or goal blocked; immediately return -1 in O(1).",
        "worst_scan": "100 x 100 grid -> Unweighted BFS guarantees first visit to (n-1, n-1) is shortest in O(N^2) time."
    },
    "mental_model": {
        "analogy_title": "Queen's Ripple Wave on a Chessboard",
        "analogy_text": "Unlike Rotting Oranges (4-directional), here you can step horizontally, vertically, OR diagonally—8 neighbors like a chess King! Dropping a pebble at (0, 0) sends a concentric BFS ripple outward: distance 1 at (0, 0), distance 2 for all 8 reachable 0s around it, distance 3 for the next ring. The very first ripple to touch (n-1, n-1) is mathematically guaranteed to be the shortest path.",
        "aha_moment": "DFS finds *any* path (exponential backtracking for shortest!); unweighted BFS finds the *shortest* path in O(N^2) as soon as it dequeues (n-1, n-1). Mark cells visited UPON ENQUEUEING—not upon dequeueing—to prevent exponential queue blowup!",
        "trigger": "Shortest path in an unweighted 2D grid with obstacles and diagonal/orthogonal moves.",
        "sisters": [
            {"name": "Rotting Oranges", "lc": "#994"},
            {"name": "Number of Islands", "lc": "#200"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(8^(N^2)) Exponential DFS",
        "naive_text": "Run recursive backtracking DFS exploring all clear paths from (0, 0) to (n-1, n-1) and take the minimum length.",
        "wasted_work": "Re-explores the same cells along exponentially many winding detours because DFS cannot guarantee first arrival is shortest.",
        "optimal_complexity": "O(N^2) Time, O(N^2) Space",
        "optimal_text": "Guard grid[0][0] == 1 || grid[n-1][n-1] == 1 -> -1. Enqueue (0, 0, dist=1), mark visited immediately, and expand 8 directions via FIFO queue until (n-1, n-1) is reached.",
        "core_invariant": "BFS explores cells in strictly non-decreasing distance order, and marking visited on enqueue ensures each cell enters the queue at most once.",
        "table_rows": [
            {"name": "Naive: Backtracking DFS", "time": "O(8^(N^2))", "space": "O(N^2)", "note": "Exponential TLE on open 100x100 grids."},
            {"name": "Trap: BFS Marking on Pop", "time": "TLE / Duplicate Enqueues", "space": "Huge Queue", "note": "Multiple neighbors re-queue the same cell before it pops!"},
            {"name": "Optimal: 8-Dir BFS (Mark on Enqueue)", "time": "O(N^2)", "space": "O(N^2)", "note": "★ Standard shortest-path grid traversal."}
        ]
    },
    "trace_table": {
        "input_desc": "grid = [[0, 0, 0], [1, 1, 0], [1, 1, 0]] (3x3)",
        "columns": ["Step", "Popped (r, c, dist)", "Valid 8-Dir 0-Neighbors Enqueued", "Goal Reached?"],
        "rows": [
            ["1", "(0, 0, dist=1)", "(0, 1, dist=2)", "No"],
            ["2", "(0, 1, dist=2)", "(0, 2, dist=3), (1, 2, dist=3) [diagonal!]", "No"],
            ["3", "(0, 2, dist=3)", "none unvisited", "No"],
            ["4", "(1, 2, dist=3)", "(2, 2, dist=4)", "Wait for pop or early return -> 4!"]
        ],
        "takeaway": "Diagonal move `(0, 1) -> (1, 2)` bypasses the wall `(1, 0), (1, 1)`, yielding shortest path length 4."
    },
    "stepper": {
        "title": "8-Directional Concentric BFS Wave Stepper",
        "default_input": "[[0,0,0],[1,1,0],[1,1,0]]",
        "presets": [
            {"label": "3x3 Diagonal [[0,0,0],[1,1,0],[1,1,0]]", "onclick": "loadPreset([[0,0,0],[1,1,0],[1,1,0]])"},
            {"label": "2x2 [[0,1],[1,0]]", "onclick": "loadPreset([[0,1],[1,0]])"},
            {"label": "Blocked [[1,0],[0,0]]", "onclick": "loadPreset([[1,0],[0,0]])"}
        ],
        "metrics": [
            {"label": "Popped Cell", "id": "metric-cell", "initial": "(0,0)", "color": "text-cyan-400"},
            {"label": "Distance Wave", "id": "metric-dist", "initial": "1", "color": "text-amber-400"},
            {"label": "Queue Size", "id": "metric-q", "initial": "1", "color": "text-purple-400"},
            {"label": "Shortest Path", "id": "metric-ans", "initial": "-", "color": "text-emerald-400"}
        ],
        "visual_html": "<div id='grid-bfs-viz' class='inline-grid gap-1.5'></div>",
        "initial_narration": "BFS starts at (0, 0) with distance 1 and expands 8-directionally.",
        "pseudocode_lines": [
            "if (grid[0][0] == 1 || grid[n-1][n-1] == 1) return -1",
            "queue.add((0, 0, 1)); visited[0][0] = true",
            "while (queue.isNotEmpty()) {",
            "  val (r, c, d) = queue.removeFirst()",
            "  if (r == n - 1 && c == n - 1) return d",
            "  for (8 directions (dr, dc)) {",
            "    if (inBounds && grid[nr][nc] == 0 && !visited[nr][nc]) { visited[nr][nc] = true; queue.add((nr, nc, d + 1)) }",
            "  }",
            "}",
            "return -1"
        ],
        "js_code": """
        let bfsGrid = [[0,0,0],[1,1,0],[1,1,0]];
        function buildSteps(g) {
          if (Array.isArray(g) && g.length) bfsGrid = g;
          steps = [];
          let n = bfsGrid.length;
          if (bfsGrid[0][0] === 1 || bfsGrid[n-1][n-1] === 1) {
            steps.push({ active: [-1,-1], distGrid: bfsGrid.map(r=>r.map(()=>0)), d: 0, qLen: 0, ans: -1, line: 1, narration: `Start or destination cell is blocked (1). Return -1 immediately.` });
            return;
          }
          let distGrid = bfsGrid.map(r => r.map(() => 0));
          distGrid[0][0] = 1;
          let q = [[0, 0, 1]];
          const dirs = [[-1,-1],[-1,0],[-1,1],[0,-1],[0,1],[1,-1],[1,0],[1,1]];
          steps.push({ active: [0,0], distGrid: distGrid.map(r=>[...r]), d: 1, qLen: 1, ans: '-', line: 2, narration: `Enqueued (0, 0) with dist=1.` });
          while (q.length > 0) {
            let [r, c, d] = q.shift();
            if (r === n - 1 && c === n - 1) {
              steps.push({ active: [r,c], distGrid: distGrid.map(row=>[...row]), d, qLen: q.length, ans: d, line: 5, narration: `Reached destination (${r}, ${c}) at shortest distance ${d}!` });
              return;
            }
            for (let [dr, dc] of dirs) {
              let nr = r + dr, nc = c + dc;
              if (nr >= 0 && nr < n && nc >= 0 && nc < n && bfsGrid[nr][nc] === 0 && distGrid[nr][nc] === 0) {
                distGrid[nr][nc] = d + 1;
                q.push([nr, nc, d + 1]);
              }
            }
            steps.push({ active: [r,c], distGrid: distGrid.map(row=>[...row]), d, qLen: q.length, ans: '-', line: 7, narration: `Expanded (${r}, ${c}) at dist=${d}; queue size=${q.length}.` });
          }
          steps.push({ active: [-1,-1], distGrid: distGrid.map(row=>[...row]), d: '-', qLen: 0, ans: -1, line: 10, narration: `Queue exhausted without reaching (${n-1}, ${n-1}). Return -1.` });
        }
        function renderStage(step) {
          const c = document.getElementById('grid-bfs-viz');
          if (!c) return;
          let n = bfsGrid.length;
          c.style.gridTemplateColumns = `repeat(${n}, minmax(0, 1fr))`;
          let html = '';
          for (let r = 0; r < n; r++) {
            for (let col = 0; col < n; col++) {
              let blocked = bfsGrid[r][col] === 1;
              let dist = step.distGrid[r][col];
              let isAct = step.active[0] === r && step.active[1] === col;
              let cls = blocked ? 'border-rose-900 bg-rose-950/60 text-rose-400' : (isAct ? 'border-emerald-400 bg-emerald-950/70 text-emerald-300 ring-2 ring-emerald-400/50' : (dist > 0 ? 'border-cyan-500/60 bg-cyan-950/40 text-cyan-200' : 'border-slate-800 bg-slate-900 text-slate-500'));
              html += `<div class="w-12 h-12 rounded-lg border-2 flex flex-col items-center justify-center font-mono text-xs ${cls}">
                <span class="text-[9px] opacity-60">${r},${col}</span>
                <span class="font-bold">${blocked ? 'X' : (dist > 0 ? 'd=' + dist : '0')}</span>
              </div>`;
            }
          }
          c.innerHTML = html;
        }
        function loadPreset(g) { buildSteps(g); stepperReset(); }
        """,
        "js_custom_input": """function handleCustomInput() {
          const el = document.getElementById('custom-input');
          if (!el) return;
          try {
            const g = JSON.parse(el.value.trim());
            if (Array.isArray(g) && g.length && Array.isArray(g[0])) loadPreset(g);
          } catch (_) {}
        }""",
        "js_update_metrics": """
        document.getElementById('metric-cell').textContent = step.active[0] >= 0 ? `(${step.active[0]},${step.active[1]})` : '-';
        document.getElementById('metric-dist').textContent = step.d;
        document.getElementById('metric-q').textContent = step.qLen;
        document.getElementById('metric-ans').textContent = step.ans;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Backtracking DFS (Exponential Baseline)",
            "badge": "Naive",
            "desc": "Explore every simple path recursively and take the minimum length. Causes TLE on open grids.",
            "time": "O(8^(N^2))",
            "space": "O(N^2)",
            "code_kotlin": """class Solution {
    fun shortestPathBinaryMatrix(grid: Array<IntArray>): Int {
        val n = grid.size
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) return -1
        var best = Int.MAX_VALUE
        val visited = Array(n) { BooleanArray(n) }
        val dirs = arrayOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
        fun dfs(r: Int, c: Int, len: Int) {
            if (len >= best) return
            if (r == n - 1 && c == n - 1) { best = len; return }
            visited[r][c] = true
            for ((dr, dc) in dirs) {
                val nr = r + dr; val nc = c + dc
                if (nr in 0 until n && nc in 0 until n && grid[nr][nc] == 0 && !visited[nr][nc]) {
                    dfs(nr, nc, len + 1)
                }
            }
            visited[r][c] = false
        }
        dfs(0, 0, 1)
        return if (best == Int.MAX_VALUE) -1 else best
    }
}""",
            "code_python": """class Solution:
    def shortestPathBinaryMatrix(self, grid: list[list[int]]) -> int:
        n = len(grid)
        if grid[0][0] == 1 or grid[n - 1][n - 1] == 1:
            return -1
        best = float('inf')
        visited = [[False] * n for _ in range(n)]
        dirs = [(-1,-1),(-1,0),(-1,1),(0,-1),(0,1),(1,-1),(1,0),(1,1)]
        def dfs(r: int, c: int, dist: int) -> None:
            nonlocal best
            if dist >= best:
                return
            if r == n - 1 and c == n - 1:
                best = dist
                return
            visited[r][c] = True
            for dr, dc in dirs:
                nr, nc = r + dr, c + dc
                if 0 <= nr < n and 0 <= nc < n and grid[nr][nc] == 0 and not visited[nr][nc]:
                    dfs(nr, nc, dist + 1)
            visited[r][c] = False
        dfs(0, 0, 1)
        return -1 if best == float('inf') else best""",
            "code_java": """class Solution {
    private int best = Integer.MAX_VALUE;
    private final int[][] dirs = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
    public int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) return -1;
        boolean[][] visited = new boolean[n][n];
        dfs(grid, visited, 0, 0, 1);
        return best == Integer.MAX_VALUE ? -1 : best;
    }
    private void dfs(int[][] grid, boolean[][] visited, int r, int c, int len) {
        int n = grid.length;
        if (len >= best) return;
        if (r == n - 1 && c == n - 1) { best = len; return; }
        visited[r][c] = true;
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 0 && !visited[nr][nc]) {
                dfs(grid, visited, nr, nc, len + 1);
            }
        }
        visited[r][c] = false;
    }
}""",
            "notes_kotlin": "DFS cannot stop on first arrival at (n-1, n-1), causing exponential time on open grids.",
            "notes_python": "Backtracking unmarks visited[r][c] = False and revisits cells exponentially.",
            "notes_java": "Use only as a verbal contrast explaining why unweighted shortest-path problems require BFS."
        },
        {
            "name": "Approach 2: 8-Directional Level-Order BFS",
            "badge": "Optimal",
            "desc": "Mark cells visited on enqueue inside a FIFO ArrayDeque/deque for guaranteed O(N^2) shortest path.",
            "time": "O(N^2)",
            "space": "O(N^2)",
            "code_kotlin": """import java.util.ArrayDeque

class Solution {
    fun shortestPathBinaryMatrix(grid: Array<IntArray>): Int {
        val n = grid.size
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) return -1
        val visited = Array(n) { BooleanArray(n) }
        val q = ArrayDeque<IntArray>()
        q.addLast(intArrayOf(0, 0, 1))
        visited[0][0] = true

        val dr = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
        val dc = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)
        while (q.isNotEmpty()) {
            val cur = q.removeFirst()
            val r = cur[0]
            val c = cur[1]
            val dist = cur[2]
            if (r == n - 1 && c == n - 1) return dist
            for (k in 0 until 8) {
                val nr = r + dr[k]
                val nc = c + dc[k]
                if (nr in 0 until n && nc in 0 until n && grid[nr][nc] == 0 && !visited[nr][nc]) {
                    visited[nr][nc] = true
                    q.addLast(intArrayOf(nr, nc, dist + 1))
                }
            }
        }
        return -1
    }
}""",
            "code_python": """from collections import deque

class Solution:
    def shortestPathBinaryMatrix(self, grid: list[list[int]]) -> int:
        n = len(grid)
        if grid[0][0] == 1 or grid[n - 1][n - 1] == 1:
            return -1
        visited = [[False] * n for _ in range(n)]
        visited[0][0] = True
        q = deque([(0, 0, 1)])
        dirs = [(-1,-1), (-1,0), (-1,1), (0,-1), (0,1), (1,-1), (1,0), (1,1)]

        while q:
            r, c, dist = q.popleft()
            if r == n - 1 and c == n - 1:
                return dist
            for dr, dc in dirs:
                nr, nc = r + dr, c + dc
                if 0 <= nr < n and 0 <= nc < n and grid[nr][nc] == 0 and not visited[nr][nc]:
                    visited[nr][nc] = True
                    q.append((nr, nc, dist + 1))
        return -1""",
            "code_java": """import java.util.*;

class Solution {
    public int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) return -1;
        boolean[][] visited = new boolean[n][n];
        Queue<int[]> q = new ArrayDeque<>();
        q.offer(new int[]{0, 0, 1});
        visited[0][0] = true;

        int[][] dirs = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1], dist = cur[2];
            if (r == n - 1 && c == n - 1) return dist;
            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 0 && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    q.offer(new int[]{nr, nc, dist + 1});
                }
            }
        }
        return -1;
    }
}""",
            "notes_kotlin": "Uses a separate BooleanArray visited matrix so caller-owned grid[][] stays immutable.",
            "notes_python": "collections.deque provides O(1) popleft(), and visited[nr][nc] = True happens immediately on append.",
            "notes_java": "ArrayDeque<int[]> outperforms LinkedList with contiguous ring-buffer storage and zero node wrappers."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """import java.util.ArrayDeque

class Solution {
    fun shortestPathBinaryMatrix(grid: Array<IntArray>): Int {
        // Write your 8-directional BFS shortest path solution here:
        
    }
}""",
        "python_boilerplate": """from collections import deque

class Solution:
    def shortestPathBinaryMatrix(self, grid: list[list[int]]) -> int:
        # Write your 8-directional BFS shortest path solution here:
        pass""",
        "java_boilerplate": """import java.util.*;

class Solution {
    public int shortestPathBinaryMatrix(int[][] grid) {
        // Write your 8-directional BFS shortest path solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: [[[0, 1], [1, 0]]], expected: 2 },
          { input: [[[0, 0, 0], [1, 1, 0], [1, 1, 0]]], expected: 4 },
          { input: [[[1, 0, 0], [1, 1, 0], [1, 1, 0]]], expected: -1 },
          { input: [[[0]]], expected: 1 }
        ];
        """
    },
    "quiz_questions": [
        {
            "category": "BFS vs DFS Selection",
            "prompt": "Why does unweighted 8-directional BFS solve this problem in O(N^2) while DFS becomes exponential?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "BFS expands by wave distance, guaranteeing the first hit on target is shortest.", "isCorrect": True, "whyRight": "Every cell is visited once in non-decreasing distance order, so the very first visit to (n-1, n-1) is guaranteed optimal."},
                {"id": "B", "text": "DFS cannot traverse diagonal neighbor cells inside two-dimensional binary matrices.", "isCorrect": False, "whyWrong": "DFS can loop over 8 directions too, but cannot stop on first arrival."},
                {"id": "C", "text": "BFS inspects only boundary cells without ever entering the interior matrix cells.", "isCorrect": False, "whyWrong": "BFS explores all reachable clear cells across the interior."},
                {"id": "D", "text": "DFS throws integer overflow errors whenever path lengths exceed grid dimensions.", "isCorrect": False, "whyWrong": "DFS fails due to O(8^(N^2)) time complexity, not integer overflow."}
            ],
            "allExplanations": "• A is correct: BFS visits unweighted graphs in exact shortest-path wave order."
        },
        {
            "category": "Queue Blowup Trap",
            "prompt": "Why is marking `visited[nr][nc] = true` WHEN ENQUEUEING critical rather than marking after dequeueing?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Marking on dequeue lets up to 8 neighbors enqueue the same cell redundantly.", "isCorrect": True, "whyRight": "If not marked on enqueue, multiple wave-k neighbors push the same wave-(k+1) cell simultaneously, exploding queue size and causing TLE/MLE!"},
                {"id": "B", "text": "Marking on enqueue converts BFS into depth-first search with shorter paths.", "isCorrect": False, "whyWrong": "Queue order stays strictly FIFO (breadth-first)."},
                {"id": "C", "text": "Marking on dequeue skips checking diagonal neighbor coordinates completely.", "isCorrect": False, "whyWrong": "It checks diagonals, just enqueues duplicates exponentially."},
                {"id": "D", "text": "Marking on enqueue prevents the start cell from counting toward total path length.", "isCorrect": False, "whyWrong": "Path length initializes at 1 when (0, 0) is enqueued."}
            ],
            "allExplanations": "• A is correct: mark visited immediately upon pushing to the queue."
        },
        {
            "category": "Code Snippet Bug Hunt",
            "prompt": "What edge case fails in this implementation when `grid = [[0]]`?",
            "codeSnippet": "if (grid[0][0] == 1 || grid[n-1][n-1] == 1) return -1\nq.addLast(intArrayOf(0, 0, 1)); visited[0][0] = true\nwhile (q.isNotEmpty()) {\n    val (r, c, d) = q.removeFirst()\n    for (k in 0 until 8) {\n        val nr = r + dr[k]; val nc = c + dc[k]\n        if (nr == n - 1 && nc == n - 1 && grid[nr][nc] == 0) return d + 1\n    }\n}\nreturn -1",
            "options": [
                {"id": "A", "text": "Checking goal only on neighbor expansion misses 1x1 grids and returns -1.", "isCorrect": True, "whyRight": "For [[0]], (0, 0) IS already (n-1, n-1); checking goal only inside the neighbor loop skips it and returns -1 instead of 1!"},
                {"id": "B", "text": "Starting distance at 1 inflates all 2x2 path lengths by one extra cell.", "isCorrect": False, "whyWrong": "Path length counts visited cells (not edges), so [[0,1],[1,0]] correctly returns 2."},
                {"id": "C", "text": "Using intArrayOf inside ArrayDeque causes compilation failure on 1x1 matrices.", "isCorrect": False, "whyWrong": "intArrayOf(0, 0, 1) compiles cleanly regardless of n."},
                {"id": "D", "text": "Grid bounds check fails whenever n equals one due to zero indexing.", "isCorrect": False, "whyWrong": "0 in 0 until 1 is valid; the bug is checking goal only on neighbor expansion."}
            ],
            "allExplanations": "• A is correct: check `if (r == n - 1 && c == n - 1) return dist` on pop (or handle `n == 1` upfront)."
        },
        {
            "category": "Production & Immutability",
            "prompt": "Why use a separate `visited` boolean array instead of mutating `grid[nr][nc] = 1` in place?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Mutating grid corrupts caller-owned state in concurrent or repeated queries.", "isCorrect": True, "whyRight": "Both approaches are O(N^2) space/time, or mutating saves N^2 bits—but in production services, mutating caller input breaks reuse and thread safety."},
                {"id": "B", "text": "Mutating grid[nr][nc] = 1 reduces worst-case time complexity from O(N^2) to O(N).", "isCorrect": False, "whyWrong": "Time complexity remains O(N^2) either way."},
                {"id": "C", "text": "Mutating grid cells prevents BFS from visiting diagonal neighbors safely.", "isCorrect": False, "whyWrong": "In-place marking works functionally if mutation is explicitly allowed."},
                {"id": "D", "text": "BooleanArray takes more memory than allocating a HashSet of string coordinates.", "isCorrect": False, "whyWrong": "BooleanArray uses ~1 byte per cell, far smaller than boxed String/Pair objects in a HashSet."}
            ],
            "allExplanations": "• A is correct: keeping input immutable avoids subtle caller/concurrency bugs (and you should explicitly ask the interviewer before mutating `grid`)."
        }
    ],
    "spoken_script": {
        "phase1": "Before coding, I'll clarify: we move 8-directionally (including diagonals), path length counts total visited cells starting at 1 for (0,0), and should I avoid mutating the input `grid`?",
        "phase2": "DFS backtracking takes exponential time because it can't stop on first arrival at (n-1, n-1). Because every step has uniform cost 1, BFS visits cells in exact shortest-path order in O(N^2) time and O(N^2) space.",
        "phase3": "First guard if grid[0][0] == 1 or grid[n-1][n-1] == 1 -> return -1. Push (0, 0, 1) into an ArrayDeque and mark visited[0][0] = true. Pop (r, c, dist); if (r == n-1 && c == n-1) return dist. Otherwise iterate over all 8 directions, and for each clear unvisited neighbor, mark visited immediately on enqueue and push (nr, nc, dist + 1).",
        "phase4": "Tracing [[0]] gives immediate pop (0, 0, 1) -> returns 1. Tracing [[0,1],[1,0]] pops (0, 0, 1), steps diagonally to (1, 1, 2), and returns 2.",
        "senior_followup": "If cells had varying traversal costs instead of binary 0/1, we would upgrade from BFS to Dijkstra's algorithm with a Min-Heap, or bidirectional BFS from both (0,0) and (n-1,n-1) to cut the search frontier on huge open maps."
    }
}

all_problems_111_116 = [p111, p112, p113, p114, p115, p116]
