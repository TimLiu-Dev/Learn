"""
Curriculum definitions for Problems 97 to 103 (2027 Mid-Level Additions):
#97: LC #88 Merge Sorted Array (Pass 1 Foundation Easy)
#98: LC #359 Logger Rate Limiter (Pass 1 Foundation Easy)
#99: LC #53 Maximum Subarray / Kadane's (Pass 2A Core Medium)
#100: LC #48 Rotate Image (Pass 2A Core Medium)
#101: LC #79 Word Search (Pass 2B Advanced Medium - #1 Karat Question)
#102: LC #399 Evaluate Division (Pass 2B Advanced Medium - FinTech Currency Graph)
#103: LC #5 Longest Palindromic Substring (Pass 2B Advanced Medium)
"""

p97 = {
    "num": 97,
    "lc": 88,
    "title": "Merge Sorted Array",
    "category": "Arrays & Two Pointers",
    "difficulty": "Easy",
    "pass_label": "Pass 1",
    "lc_slug": "merge-sorted-array",
    "verbatim_statement": "You are given two integer arrays <code>nums1</code> and <code>nums2</code>, sorted in non-decreasing order, and two integers <code>m</code> and <code>n</code>, representing the number of elements in <code>nums1</code> and <code>nums2</code> respectively.<br><br>Merge <code>nums1</code> and <code>nums2</code> into a single array sorted in non-decreasing order.<br><br>The final sorted array should not be returned by the function, but instead be stored inside the array <code>nums1</code>. To accommodate this, <code>nums1</code> has a length of <code>m + n</code>, where the first <code>m</code> elements denote the elements that should be merged, and the last <code>n</code> elements are set to <code>0</code> and should be ignored. <code>nums2</code> has a length of <code>n</code>.",
    "examples": [
        {
            "title": "Example 1",
            "input": "nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3",
            "output": "[1,2,2,3,5,6]"
        },
        {
            "title": "Example 2",
            "input": "nums1 = [1], m = 1, nums2 = [], n = 0",
            "output": "[1]"
        },
        {
            "title": "Example 3",
            "input": "nums1 = [0], m = 0, nums2 = [1], n = 1",
            "output": "[1]"
        }
    ],
    "constraints": [
        "nums1.length == m + n",
        "nums2.length == n",
        "0 <= m, n <= 200",
        "1 <= m + n <= 200",
        "-10^9 <= nums1[i], nums2[j] <= 10^9"
    ],
    "plain_english": "You need to combine two sorted lists into the first list in-place. The first list has buffer space at the end. If you merge from the front, you will overwrite elements in nums1 that you haven't processed yet. If you merge backwards from the tail, you always write into empty buffer space, avoiding any overwrites.",
    "boundary_checks": {
        "min_base": "m = 0, n = 1: nums1 is pure buffer zeros; copy nums2 directly.",
        "best_exit": "n = 0: nums2 is empty; nums1 is already sorted, 0 operations required.",
        "worst_scan": "m = 100, n = 100: 200 elements merged step-by-step from tail."
    },
    "mental_model": {
        "analogy_title": "The Moving Van Loading from the Back",
        "analogy_text": "Imagine two lines of heavy furniture sorted by size. You must pack both lines into a single moving truck that already holds line 1 in the front and has empty space in the back. If you try to slide smaller items into the front, you crush the furniture already there. Instead, you pick the biggest items from both lines and load them directly into the back of the truck. No shifting, no crushing.",
        "aha_moment": "Merge backwards! Three pointers: p1 at m-1, p2 at n-1, and write at m+n-1. The largest remaining element always claims the write position.",
        "trigger": "Merging two sorted sequences in-place with existing trailing buffer capacity.",
        "sisters": [
            {"name": "Merge Two Sorted Lists", "lc": "#21"},
            {"name": "Squares of a Sorted Array", "lc": "#977"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O((M + N) log(M + N)) Time, O(1) Space",
        "naive_text": "Append nums2 directly into the buffer zeros of nums1, then invoke the language standard sorting routine (Timsort / Dual-Pivot Quicksort).",
        "wasted_work": "Completely discards the pre-existing sorted invariant of both input arrays, incurring unnecessary comparison and branching overhead.",
        "optimal_complexity": "O(M + N) Time, O(1) Space",
        "optimal_text": "Initialize three pointers: p1 at m-1, p2 at n-1, and write at m+n-1. While p2 >= 0, compare nums1[p1] and nums2[p2], place the larger at write, and decrement.",
        "core_invariant": "write is always >= p1, mathematically guaranteeing that an unread element in nums1 is never overwritten.",
        "table_rows": [
            {
                "name": "Naive: Append & Sort",
                "time": "O((M + N) log(M + N))",
                "space": "O(1) to O(M + N)",
                "note": "Ignores sorted order; fails senior bar for efficiency."
            },
            {
                "name": "Better: Auxiliary Copy Buffer",
                "time": "O(M + N)",
                "space": "O(M)",
                "note": "Copies nums1 prefix; linear time but wastes O(M) auxiliary heap memory."
            },
            {
                "name": "Optimal: Three Pointers from Tail",
                "time": "O(M + N)",
                "space": "O(1)",
                "note": "★ Production standard. Zero extra allocations, single pass."
            }
        ]
    },
    "trace_table": {
        "input_desc": "nums1 = [1, 3, 5, 0, 0, 0], m = 3, nums2 = [2, 4, 6], n = 3",
        "columns": ["Step", "p1 (nums1)", "p2 (nums2)", "write", "Action / Winner", "nums1 State"],
        "rows": [
            ["0", "idx 2 (val 5)", "idx 2 (val 6)", "idx 5", "Compare 5 vs 6 -> 6 wins", "[1, 3, 5, 0, 0, 6]"],
            ["1", "idx 2 (val 5)", "idx 1 (val 4)", "idx 4", "Compare 5 vs 4 -> 5 wins", "[1, 3, 5, 0, 5, 6]"],
            ["2", "idx 1 (val 3)", "idx 1 (val 4)", "idx 3", "Compare 3 vs 4 -> 4 wins", "[1, 3, 5, 4, 5, 6]"],
            ["3", "idx 1 (val 3)", "idx 0 (val 2)", "idx 2", "Compare 3 vs 2 -> 3 wins", "[1, 3, 3, 4, 5, 6]"],
            ["4", "idx 0 (val 1)", "idx 0 (val 2)", "idx 1", "Compare 1 vs 2 -> 2 wins", "[1, 2, 3, 4, 5, 6]"],
            ["5", "idx 0 (val 1)", "idx -1 (exhausted)", "idx 0", "p2 exhausted -> nums1 remainder already sorted", "[1, 2, 3, 4, 5, 6]"]
        ],
        "takeaway": "When p2 is exhausted, we stop early because whatever remains in nums1 is already in its correct sorted position."
    },
    "stepper": {
        "title": "Three-Pointer Backward Merging Stepper",
        "default_input": "nums1: [1,3,5], nums2: [2,4,6]",
        "presets": [
            {"label": "Standard Interleaved", "onclick": "loadPreset([1,3,5], [2,4,6])"},
            {"label": "nums2 All Greater", "onclick": "loadPreset([1,2,3], [4,5,6])"},
            {"label": "nums1 All Greater", "onclick": "loadPreset([4,5,6], [1,2,3])"}
        ],
        "metrics": [
            {"label": "p1 Index", "id": "metric-p1", "initial": "2", "color": "text-emerald-400"},
            {"label": "p2 Index", "id": "metric-p2", "initial": "2", "color": "text-cyan-400"},
            {"label": "Write Head", "id": "metric-write", "initial": "5", "color": "text-amber-400"},
            {"label": "Comparison", "id": "metric-cmp", "initial": "5 vs 6", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='array-viz' class='flex items-center gap-2'></div>",
        "initial_narration": "Pointers initialized at the ends: p1=m-1, p2=n-1, write=m+n-1.",
        "pseudocode_lines": [
            "var p1 = m - 1, p2 = n - 1, write = m + n - 1",
            "while p2 >= 0:",
            "  if p1 >= 0 and nums1[p1] > nums2[p2]:",
            "    nums1[write] = nums1[p1]; p1--",
            "  else:",
            "    nums1[write] = nums2[p2]; p2--",
            "  write--"
        ],
        "js_code": """
        let stepperNums1 = [1, 3, 5, 0, 0, 0];
        let stepperNums2 = [2, 4, 6];
        let stepperM = 3, stepperN = 3;

        function buildSteps(cand) {
          steps = [];
          let n1 = [1, 3, 5, 0, 0, 0];
          let n2 = [2, 4, 6];
          let p1 = 2, p2 = 2, w = 5;

          steps.push({
            p1, p2, w, cmp: "Ready",
            arr1: [...n1],
            line: 1,
            narration: "Initialized p1=2, p2=2, write=5. Ready to compare."
          });

          while (p2 >= 0) {
            let useP1 = (p1 >= 0 && n1[p1] > n2[p2]);
            let winner = useP1 ? `nums1[${p1}]=${n1[p1]}` : `nums2[${p2}]=${n2[p2]}`;
            let val = useP1 ? n1[p1] : n2[p2];
            n1[w] = val;

            steps.push({
              p1, p2, w,
              cmp: `Compare ${p1 >= 0 ? n1[p1] : '-inf'} vs ${n2[p2]}`,
              arr1: [...n1],
              line: useP1 ? 4 : 6,
              narration: `Winner: ${winner}. Wrote ${val} to index ${w}.`
            });

            if (useP1) p1--; else p2--;
            w--;
          }

          steps.push({
            p1, p2, w,
            cmp: "Complete",
            arr1: [...n1],
            line: 7,
            narration: "Merge complete. Array is sorted in-place."
          });
        }

        function renderStage(step) {
          const c = document.getElementById('array-viz');
          if (!c) return;
          c.innerHTML = step.arr1.map((val, idx) => {
            let isWrite = idx === step.w;
            let isP1 = idx === step.p1;
            let border = isWrite ? 'border-amber-400 bg-amber-950/40 text-amber-300' :
                         (isP1 ? 'border-emerald-400 bg-emerald-950/40 text-emerald-300' : 'border-slate-800 bg-slate-900 text-slate-300');
            return `<div class="w-12 h-14 rounded-xl border-2 flex flex-col items-center justify-center font-mono text-xs ${border}">
              <span class="text-[9px] text-slate-500">${idx}</span>
              <span class="font-bold">${val}</span>
            </div>`;
          }).join('');
        }

        function loadPreset(arr1, arr2) {
          stepperNums1 = [...arr1, ...new Array(arr2.length).fill(0)];
          stepperNums2 = [...arr2];
          stepperM = arr1.length;
          stepperN = arr2.length;
          buildSteps();
          stepperReset();
        }
        """,
        "js_update_metrics": """
        document.getElementById('metric-p1').textContent = step.p1;
        document.getElementById('metric-p2').textContent = step.p2;
        document.getElementById('metric-write').textContent = step.w;
        document.getElementById('metric-cmp').textContent = step.cmp;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Append and Sort",
            "badge": "Naive",
            "desc": "Copy nums2 into trailing buffer and sort with standard language library.",
            "time": "O((M+N) log(M+N))",
            "space": "O(1) auxiliary",
            "code_kotlin": """class Solution {
    fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int): Unit {
        for (i in 0 until n) {
            nums1[m + i] = nums2[i]
        }
        nums1.sort()
    }
}""",
            "code_python": """class Solution:
    def merge(self, nums1: list[int], m: int, nums2: list[int], n: int) -> None:
        nums1[m:] = nums2
        nums1.sort()""",
            "code_java": """import java.util.Arrays;

class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        System.arraycopy(nums2, 0, nums1, m, n);
        Arrays.sort(nums1);
    }
}""",
            "notes_kotlin": "Standard TimSort requires (M+N) log(M+N) comparisons and ignores sorted input.",
            "notes_python": "Slice assignment followed by Timsort.",
            "notes_java": "System.arraycopy is fast native memory move, but sorting dominates asymptotic runtime."
        },
        {
            "name": "Approach 2: Auxiliary Array Copy",
            "badge": "Better",
            "desc": "Copy first m elements into auxiliary buffer, merge forward into nums1.",
            "time": "O(M + N)",
            "space": "O(M) memory",
            "code_kotlin": """class Solution {
    fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int): Unit {
        val copy = nums1.copyOfRange(0, m)
        var p1 = 0
        var p2 = 0
        var write = 0
        while (p1 < m && p2 < n) {
            if (copy[p1] <= nums2[p2]) {
                nums1[write++] = copy[p1++]
            } else {
                nums1[write++] = nums2[p2++]
            }
        }
        while (p1 < m) nums1[write++] = copy[p1++]
        while (p2 < n) nums1[write++] = nums2[p2++]
    }
}""",
            "code_python": """class Solution:
    def merge(self, nums1: list[int], m: int, nums2: list[int], n: int) -> None:
        copy = nums1[:m]
        p1, p2, w = 0, 0, 0
        while p1 < m and p2 < n:
            if copy[p1] <= nums2[p2]:
                nums1[w] = copy[p1]; p1 += 1
            else:
                nums1[w] = nums2[p2]; p2 += 1
            w += 1
        while p1 < m:
            nums1[w] = copy[p1]; p1 += 1; w += 1
        while p2 < n:
            nums1[w] = nums2[p2]; p2 += 1; w += 1""",
            "code_java": """class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int[] copy = new int[m];
        System.arraycopy(nums1, 0, copy, 0, m);
        int p1 = 0, p2 = 0, w = 0;
        while (p1 < m && p2 < n) {
            nums1[w++] = (copy[p1] <= nums2[p2]) ? copy[p1++] : nums2[p2++];
        }
        while (p1 < m) nums1[w++] = copy[p1++];
        while (p2 < n) nums1[w++] = nums2[p2++];
    }
}""",
            "notes_kotlin": "Allocates a new IntArray of size M on the JVM heap.",
            "notes_python": "Creates slice copy; linear memory.",
            "notes_java": "Requires extra array allocation."
        },
        {
            "name": "Approach 3: In-Place Three Pointers from Tail",
            "badge": "Optimal",
            "desc": "Fill nums1 from the back using two pointers and write head. Zero extra allocations.",
            "time": "O(M + N)",
            "space": "O(1) memory",
            "code_kotlin": """class Solution {
    fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int): Unit {
        var p1 = m - 1
        var p2 = n - 1
        var write = m + n - 1

        while (p2 >= 0) {
            if (p1 >= 0 && nums1[p1] > nums2[p2]) {
                nums1[write--] = nums1[p1--]
            } else {
                nums1[write--] = nums2[p2--]
            }
        }
    }
}""",
            "code_python": """class Solution:
    def merge(self, nums1: list[int], m: int, nums2: list[int], n: int) -> None:
        p1 = m - 1
        p2 = n - 1
        w = m + n - 1

        while p2 >= 0:
            if p1 >= 0 and nums1[p1] > nums2[p2]:
                nums1[w] = nums1[p1]
                p1 -= 1
            else:
                nums1[w] = nums2[p2]
                p2 -= 1
            w -= 1""",
            "code_java": """class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int p1 = m - 1;
        int p2 = n - 1;
        int write = m + n - 1;

        while (p2 >= 0) {
            if (p1 >= 0 && nums1[p1] > nums2[p2]) {
                nums1[write--] = nums1[p1--];
            } else {
                nums1[write--] = nums2[p2--];
            }
        }
    }
}""",
            "notes_kotlin": "Zero allocations. Single while (p2 >= 0) loop handles early termination cleanly.",
            "notes_python": "Idiomatic while loop with pointer decrements.",
            "notes_java": "Cache-friendly contiguous memory writes."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int): Unit {
        // Write your solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def merge(self, nums1: list[int], m: int, nums2: list[int], n: int) -> None:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        // Write your solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { nums1: [1,2,3,0,0,0], m: 3, nums2: [2,5,6], n: 3 }, expected: [1,2,2,3,5,6] },
          { input: { nums1: [1], m: 1, nums2: [], n: 0 }, expected: [1] },
          { input: { nums1: [0], m: 0, nums2: [1], n: 1 }, expected: [1] }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Before coding, I want to clarify: are elements in nums1 and nums2 strictly integers, and is nums1 guaranteed to have length m + n? Also, if nums2 is empty (n = 0), can we immediately return without doing work?",
        "phase2": "The naive approach is appending nums2 and sorting, taking O((M+N) log(M+N)). We can do O(M+N) by copying nums1 to an auxiliary buffer, but that costs O(M) space. The optimal strategy is merging from the back: we use three pointers starting at m-1, n-1, and m+n-1. Because write >= p1 always holds, we never overwrite unread elements in nums1. Space is strictly O(1).",
        "phase3": "I'll initialize p1 at m - 1, p2 at n - 1, and write at m + n - 1. We loop while p2 >= 0. If p1 >= 0 and nums1[p1] is greater than nums2[p2], we place nums1[p1] at write and decrement p1. Otherwise we place nums2[p2] and decrement p2. After each placement, we decrement write.",
        "phase4": "Let's dry-run with m = 0, n = 1: nums1 = [0], nums2 = [1]. p1 is -1, p2 is 0, write is 0. Since p1 < 0, else branch triggers, nums1[0] = nums2[0] = 1, p2 becomes -1, loop finishes. Result is [1]. Correct.",
        "senior_followup": "How would you handle this if both arrays are massive terabyte-scale sorted files stored on disk that do not fit into RAM?"
    }
}

# =========================================================================
# PROBLEM 98: LC #359 LOGGER RATE LIMITER (Pass 1 Foundation Easy)
# =========================================================================
p98 = {
    "num": 98,
    "lc": 359,
    "title": "Logger Rate Limiter",
    "category": "Hash Tables & System Design",
    "difficulty": "Easy",
    "pass_label": "Pass 1",
    "lc_slug": "logger-rate-limiter",
    "verbatim_statement": "Design a logger system that receives a stream of messages along with their timestamps. Each unique message should only be printed at most once every 10 seconds (i.e. a message printed at timestamp <code>t</code> will prevent other identical messages from being printed until timestamp <code>t + 10</code>).<br><br>All timestamps are in seconds granularity and arrive in non-decreasing chronological order.<br><br>Implement the <code>Logger</code> class:<br>• <code>Logger()</code> Initializes the logger object.<br>• <code>bool shouldPrintMessage(int timestamp, string message)</code> Returns <code>true</code> if the message should be printed in the given timestamp, otherwise returns <code>false</code>.",
    "examples": [
        {
            "title": "Example 1",
            "input": "['Logger', 'shouldPrintMessage', 'shouldPrintMessage', 'shouldPrintMessage', 'shouldPrintMessage', 'shouldPrintMessage', 'shouldPrintMessage']\\n[[], [1, 'foo'], [2, 'bar'], [3, 'foo'], [8, 'bar'], [10, 'foo'], [11, 'foo']]",
            "output": "[null, true, true, false, false, false, true]"
        }
    ],
    "constraints": [
        "0 <= timestamp <= 10^9",
        "Every timestamp is in non-decreasing order",
        "1 <= message.length <= 30",
        "At most 10^4 calls will be made to shouldPrintMessage"
    ],
    "plain_english": "You need to prevent duplicate log messages within a 10-second rolling window. When a message arrives at time t, check if it was printed recently. If it was printed at t_prev and t - t_prev < 10, reject it. Otherwise, print it and update its last-printed timestamp to t.",
    "boundary_checks": {
        "min_base": "First message always returns true.",
        "best_exit": "Duplicate message arriving at t + 10 returns true exactly at the 10-second mark.",
        "worst_scan": "10,000 distinct messages stream through; memory scales with unique messages."
    },
    "mental_model": {
        "analogy_title": "The Airport Security Passport Stamp",
        "analogy_text": "Imagine a passport control desk where travelers can only pass once every 10 minutes. When Alice arrives, the officer checks her ledger. If Alice hasn't arrived before, or her stamp is older than 10 minutes, the officer stamps today's time on her ledger and waves her through. If her stamp is 3 minutes old, access is denied immediately.",
        "aha_moment": "Store message -> nextAllowedTimestamp in a HashMap! If current time >= nextAllowed, grant and update to timestamp + 10.",
        "trigger": "Sliding window rate-limiting by message key with monotonic timestamps.",
        "sisters": [
            {"name": "Design Hit Counter", "lc": "#362"},
            {"name": "LRU Cache", "lc": "#146"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N) Time per call, O(N) Space",
        "naive_text": "Store all incoming (timestamp, message) pairs in an append-only list and linearly scan backward on every call to verify the 10-second rule.",
        "wasted_work": "Scanning past history costs O(N) comparisons per message, causing quadratic O(N^2) total execution for N incoming events.",
        "optimal_complexity": "O(1) Time per call, O(U) Space (U = unique messages)",
        "optimal_text": "Keep a HashMap mapping message -> last_printed_timestamp. For each call, lookup in O(1) time. If absent or time diff >= 10, record new timestamp and return true.",
        "core_invariant": "Map lookups and writes are expected O(1) amortized under standard universal hashing.",
        "table_rows": [
            {
                "name": "Naive: Full History Scan",
                "time": "O(N)",
                "space": "O(N)",
                "note": "Linear scan on every call; unscalable."
            },
            {
                "name": "Optimal: HashMap Next-Allowed Timestamp",
                "time": "O(1)",
                "space": "O(U)",
                "note": "★ Production interview standard. Instant check and update."
            },
            {
                "name": "Senior: Sliding Queue + Set",
                "time": "O(1) amortized",
                "space": "O(10s window)",
                "note": "Evicts expired entries to bound memory leaks."
            }
        ]
    },
    "trace_table": {
        "input_desc": "Stream: (1, 'foo'), (2, 'bar'), (3, 'foo'), (8, 'bar'), (10, 'foo'), (11, 'foo')",
        "columns": ["Call", "Timestamp", "Message", "Map State Before", "Decision", "Map State After"],
        "rows": [
            ["1", "1", "'foo'", "{}", "true (new key)", "{'foo': 1}"],
            ["2", "2", "'bar'", "{'foo': 1}", "true (new key)", "{'foo': 1, 'bar': 2}"],
            ["3", "3", "'foo'", "{'foo': 1, ...}", "false (3 - 1 = 2 < 10)", "{'foo': 1, 'bar': 2}"],
            ["4", "8", "'bar'", "{'foo': 1, 'bar': 2}", "false (8 - 2 = 6 < 10)", "{'foo': 1, 'bar': 2}"],
            ["5", "10", "'foo'", "{'foo': 1, ...}", "false (10 - 1 = 9 < 10)", "{'foo': 1, 'bar': 2}"],
            ["6", "11", "'foo'", "{'foo': 1, ...}", "true (11 - 1 = 10 >= 10)", "{'foo': 11, 'bar': 2}"]
        ],
        "takeaway": "Rejected messages do NOT update the timestamp! The timer is only reset upon a successful print."
    },
    "stepper": {
        "title": "Logger Rate Limiter Decision Stepper",
        "default_input": "[1, 'foo'], [2, 'bar'], [3, 'foo'], [11, 'foo']",
        "presets": [
            {"label": "Standard Example", "onclick": "loadPreset()"}
        ],
        "metrics": [
            {"label": "Current Msg", "id": "metric-msg", "initial": "foo", "color": "text-emerald-400"},
            {"label": "Timestamp", "id": "metric-time", "initial": "1", "color": "text-cyan-400"},
            {"label": "Last Printed", "id": "metric-last", "initial": "None", "color": "text-amber-400"},
            {"label": "Decision", "id": "metric-decision", "initial": "PRINT (true)", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='logger-viz' class='flex flex-wrap gap-2'></div>",
        "initial_narration": "Logger initialized with empty HashMap memory.",
        "pseudocode_lines": [
            "if message not in map or timestamp - map[message] >= 10:",
            "    map[message] = timestamp",
            "    return true",
            "return false"
        ],
        "js_code": """
        let loggerStream = [
          { t: 1, m: 'foo' },
          { t: 2, m: 'bar' },
          { t: 3, m: 'foo' },
          { t: 8, m: 'bar' },
          { t: 10, m: 'foo' },
          { t: 11, m: 'foo' }
        ];

        function buildSteps(cand) {
          steps = [];
          let memory = {};

          steps.push({
            t: 0, m: '-', last: '-', dec: 'Init',
            mem: {...memory}, line: 1, narration: "Logger initialized with empty memory."
          });

          for (let item of loggerStream) {
            let prev = memory[item.m];
            let allowed = (prev === undefined || item.t - prev >= 10);
            if (allowed) memory[item.m] = item.t;

            steps.push({
              t: item.t,
              m: item.m,
              last: prev !== undefined ? prev : 'None',
              dec: allowed ? 'PRINT (true)' : 'DROP (false)',
              mem: {...memory},
              line: allowed ? 2 : 4,
              narration: allowed ?
                `Time ${item.t}: '${item.m}' allowed (last printed: ${prev !== undefined ? prev : 'never'}). Updated last print to ${item.t}.` :
                `Time ${item.t}: '${item.m}' dropped (diff ${item.t - prev} < 10s).`
            });
          }
        }

        function renderStage(step) {
          const c = document.getElementById('logger-viz');
          if (!c) return;
          c.innerHTML = Object.keys(step.mem).map(k => {
            return `<div class="px-3 py-2 rounded-xl bg-slate-900 border border-emerald-500/30 text-xs font-mono">
              <span class="text-slate-400 font-bold">${k}:</span>
              <span class="text-emerald-300 ml-1">t=${step.mem[k]}</span>
            </div>`;
          }).join('') || '<span class="text-slate-500 text-xs font-mono">Map is empty</span>';
        }

        function loadPreset() {
          buildSteps();
          stepperReset();
        }
        """,
        "js_update_metrics": """
        document.getElementById('metric-msg').textContent = step.m;
        document.getElementById('metric-time').textContent = step.t;
        document.getElementById('metric-last').textContent = step.last;
        document.getElementById('metric-decision').textContent = step.dec;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: HashMap Last Printed Timestamp",
            "badge": "Optimal",
            "desc": "Store message to timestamp. Expected O(1) check and update.",
            "time": "O(1) per call",
            "space": "O(U) unique messages",
            "code_kotlin": """class Logger() {
    private val lastPrinted = HashMap<String, Int>()

    fun shouldPrintMessage(timestamp: Int, message: String): Boolean {
        val last = lastPrinted[message]
        if (last == null || timestamp - last >= 10) {
            lastPrinted[message] = timestamp
            return true
        }
        return false
    }
}""",
            "code_python": """class Logger:
    def __init__(self):
        self.last_printed = {}

    def shouldPrintMessage(self, timestamp: int, message: str) -> bool:
        if message not in self.last_printed or timestamp - self.last_printed[message] >= 10:
            self.last_printed[message] = timestamp
            return True
        return False""",
            "code_java": """import java.util.HashMap;
import java.util.Map;

class Logger {
    private final Map<String, Integer> lastPrinted;

    public Logger() {
        this.lastPrinted = new HashMap<>();
    }

    public boolean shouldPrintMessage(int timestamp, String message) {
        Integer last = lastPrinted.get(message);
        if (last == null || timestamp - last >= 10) {
            lastPrinted.put(message, timestamp);
            return true;
        }
        return false;
    }
}""",
            "notes_kotlin": "Standard HashMap with nullable lookup. Thread-safe wrapper can use ConcurrentHashMap.",
            "notes_python": "Python dict provides average O(1) lookup.",
            "notes_java": "HashMap with autoboxed Integer."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Logger() {
    fun shouldPrintMessage(timestamp: Int, message: String): Boolean {
        // Write your solution here:
        return false
    }
}""",
        "python_boilerplate": """class Logger:
    def __init__(self):
        pass

    def shouldPrintMessage(self, timestamp: int, message: str) -> bool:
        # Write your solution here:
        return False""",
        "java_boilerplate": """class Logger {
    public Logger() {
        
    }
    
    public boolean shouldPrintMessage(int timestamp, String message) {
        // Write your solution here:
        return false;
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { timestamp: 1, message: 'foo' }, expected: true },
          { input: { timestamp: 2, message: 'bar' }, expected: true },
          { input: { timestamp: 3, message: 'foo' }, expected: false },
          { input: { timestamp: 8, message: 'bar' }, expected: false },
          { input: { timestamp: 10, message: 'foo' }, expected: false },
          { input: { timestamp: 11, message: 'foo' }, expected: true }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Are timestamps guaranteed to arrive strictly monotonically non-decreasing, and can timestamps reach large values like 10^9 seconds?",
        "phase2": "The simplest and most direct approach is a HashMap mapping each unique message string to the timestamp when it was last printed. Since lookups and insertions take expected O(1) time, every shouldPrintMessage call executes in O(1) time.",
        "phase3": "When a message arrives, we check if it exists in the map. If it does not exist or if timestamp minus the recorded timestamp is at least 10, we update the map with the current timestamp and return true. Otherwise, we return false without modifying the map.",
        "phase4": "Testing the boundary: if message arrives at t=1, then at t=10: 10 - 1 = 9 < 10, false. At t=11: 11 - 1 = 10 >= 10, true. Correct.",
        "senior_followup": "In production, if messages stream continuously for months, the HashMap will grow unbounded and cause an Out-Of-Memory (OOM) error. How do you clean up expired messages?"
    }
}

# =========================================================================
# PROBLEM 99: LC #53 MAXIMUM SUBARRAY (Pass 2A Core Medium - Kadane's)
# =========================================================================
p99 = {
    "num": 99,
    "lc": 53,
    "title": "Maximum Subarray",
    "category": "Dynamic Programming & Greedy",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "maximum-subarray",
    "verbatim_statement": "Given an integer array <code>nums</code>, find the subarray with the largest sum, and return <em>its sum</em>.<br><br>A <strong>subarray</strong> is a contiguous non-empty sequence of elements within an array.",
    "examples": [
        {
            "title": "Example 1",
            "input": "nums = [-2,1,-3,4,-1,2,1,-5,4]",
            "output": "6"
        },
        {
            "title": "Example 2",
            "input": "nums = [1]",
            "output": "1"
        },
        {
            "title": "Example 3",
            "input": "nums = [5,4,-1,7,8]",
            "output": "23"
        }
    ],
    "constraints": [
        "1 <= nums.length <= 10^5",
        "-10^4 <= nums[i] <= 10^4"
    ],
    "plain_english": "Find a contiguous slice of numbers whose sum is as large as possible. If extending the current running sum makes it drop below zero, that accumulated sum is a liability to any future subarray, so we drop it and restart our running sum at 0.",
    "boundary_checks": {
        "min_base": "Single element array: max sum is that element itself, even if negative.",
        "best_exit": "All positive numbers: max sum is the sum of the entire array.",
        "worst_scan": "All negative numbers (e.g. [-5, -2, -8]): max sum is the least negative single element (-2)."
    },
    "mental_model": {
        "analogy_title": "The Debt Reset Button",
        "analogy_text": "Imagine running a daily profit-and-loss business. As long as your accumulated net worth is positive, any future profitable day adds to your total wealth. But if your accumulated debt drops below zero, walking away with bankruptcy and restarting fresh tomorrow is better than carrying that negative balance into the future.",
        "aha_moment": "Kadane's Choice: At each element x, currSum = max(x, currSum + x). If currSum dips negative, reset it to x!",
        "trigger": "Contiguous subarray optimization with positive and negative numbers.",
        "sisters": [
            {"name": "Maximum Product Subarray", "lc": "#152"},
            {"name": "Best Time to Buy and Sell Stock", "lc": "#121"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N^2) Time, O(1) Space",
        "naive_text": "Compute the sum of every possible pair of start and end indices (i, j) with two nested loops.",
        "wasted_work": "Re-calculates contiguous slice sums from scratch instead of carrying over the optimal prefix sum.",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Single pass Kadane's algorithm. Maintain current running sum and global maximum. If current running sum is negative, reset to current element.",
        "core_invariant": "currMax stores the maximum contiguous sum ending strictly at index i.",
        "table_rows": [
            {
                "name": "Naive: Nested Loops",
                "time": "O(N^2)",
                "space": "O(1)",
                "note": "Quadratic brute force. TLE for N = 100,000."
            },
            {
                "name": "Divide and Conquer",
                "time": "O(N log N)",
                "space": "O(log N)",
                "note": "Split array in half; merge cross sums. Educational but non-optimal."
            },
            {
                "name": "Kadane's Algorithm (DP / Greedy)",
                "time": "O(N)",
                "space": "O(1)",
                "note": "★ Production standard. Single pass, cache friendly."
            }
        ]
    },
    "trace_table": {
        "input_desc": "nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]",
        "columns": ["i", "nums[i]", "currSum = max(x, currSum + x)", "maxSum = max(maxSum, currSum)", "Decision / Insight"],
        "rows": [
            ["0", "-2", "max(-2, 0 + -2) = -2", "-2", "Initial element sets baseline."],
            ["1", "1", "max(1, -2 + 1) = 1", "1", "Discard -2; restart fresh from 1."],
            ["2", "-3", "max(-3, 1 + -3) = -2", "1", "Carry negative debt temporarily."],
            ["3", "4", "max(4, -2 + 4) = 4", "4", "Discard negative debt; restart from 4."],
            ["4", "-1", "max(-1, 4 + -1) = 3", "4", "Running sum stays positive (3)."],
            ["5", "2", "max(2, 3 + 2) = 5", "5", "New global peak reached (5)."],
            ["6", "1", "max(1, 5 + 1) = 6", "6", "★ Maximum subarray peak [4, -1, 2, 1]."],
            ["7", "-5", "max(-5, 6 + -5) = 1", "6", "Sum drops to 1; peak preserved."],
            ["8", "4", "max(4, 1 + 4) = 5", "6", "Final sum 5; global max remains 6."]
        ],
        "takeaway": "At index 3, the running sum (-2) was discarded in favor of starting fresh at 4. This greedy reset is what makes Kadane's optimal."
    },
    "stepper": {
        "title": "Kadane's Running Sum Visualizer",
        "default_input": "[-2, 1, -3, 4, -1, 2, 1, -5, 4]",
        "presets": [
            {"label": "Standard Example", "onclick": "loadPreset([-2, 1, -3, 4, -1, 2, 1, -5, 4])"},
            {"label": "All Negative", "onclick": "loadPreset([-5, -3, -1, -4])"},
            {"label": "Alternating", "onclick": "loadPreset([5, -2, 3, -1, 4])"}
        ],
        "metrics": [
            {"label": "Current x", "id": "metric-x", "initial": "-2", "color": "text-emerald-400"},
            {"label": "Running currSum", "id": "metric-curr", "initial": "-2", "color": "text-cyan-400"},
            {"label": "Global maxSum", "id": "metric-max", "initial": "-2", "color": "text-amber-400"},
            {"label": "Action", "id": "metric-action", "initial": "Init", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='kadane-viz' class='flex flex-wrap gap-2'></div>",
        "initial_narration": "Kadane's algorithm ready to scan array.",
        "pseudocode_lines": [
            "var currSum = nums[0], maxSum = nums[0]",
            "for i = 1 until nums.size:",
            "  currSum = max(nums[i], currSum + nums[i])",
            "  maxSum = max(maxSum, currSum)",
            "return maxSum"
        ],
        "js_code": """
        let kadaneNums = [-2, 1, -3, 4, -1, 2, 1, -5, 4];

        function buildSteps(cand) {
          steps = [];
          let curr = kadaneNums[0];
          let maxS = kadaneNums[0];

          steps.push({
            idx: 0, x: kadaneNums[0], curr, maxS,
            line: 1, action: "Init",
            narration: `Initialized currSum=${curr}, maxSum=${maxS} with first element.`
          });

          for (let i = 1; i < kadaneNums.length; i++) {
            let x = kadaneNums[i];
            let extended = curr + x;
            let restarted = (x > extended);
            curr = Math.max(x, extended);
            maxS = Math.max(maxS, curr);

            steps.push({
              idx: i, x, curr, maxS,
              line: restarted ? 3 : 4,
              action: restarted ? `Restart from ${x}` : `Extend (${extended})`,
              narration: restarted ?
                `Index ${i} (${x}): Prior sum (${curr - x}) was negative debt. Restarting running sum at ${x}.` :
                `Index ${i} (${x}): Extending running sum to ${curr}. Max is now ${maxS}.`
            });
          }
        }

        function renderStage(step) {
          const c = document.getElementById('kadane-viz');
          if (!c) return;
          c.innerHTML = kadaneNums.map((val, idx) => {
            let isCurrent = idx === step.idx;
            let border = isCurrent ? 'border-emerald-400 bg-emerald-950/40 text-emerald-300 font-bold scale-105 shadow-lg' : 'border-slate-800 bg-slate-900 text-slate-400';
            return `<div class="px-3 py-2 rounded-xl border text-xs font-mono transition-all ${border}">
              <div class="text-[9px] text-slate-500">${idx}</div>
              <div>${val}</div>
            </div>`;
          }).join('');
        }

        function loadPreset(arr) {
          kadaneNums = arr;
          buildSteps();
          stepperReset();
        }
        """,
        "js_update_metrics": """
        document.getElementById('metric-x').textContent = step.x;
        document.getElementById('metric-curr').textContent = step.curr;
        document.getElementById('metric-max').textContent = step.maxS;
        document.getElementById('metric-action').textContent = step.action;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Quadratic Prefix Sums",
            "badge": "Naive",
            "desc": "Check every contiguous subarray sum using nested loops.",
            "time": "O(N^2)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun maxSubArray(nums: IntArray): Int {
        var maxSum = Int.MIN_VALUE
        for (i in nums.indices) {
            var sum = 0
            for (j in i until nums.size) {
                sum += nums[j]
                maxSum = maxOf(maxSum, sum)
            }
        }
        return maxSum
    }
}""",
            "code_python": """class Solution:
    def maxSubArray(self, nums: list[int]) -> int:
        max_sum = float('-inf')
        for i in range(len(nums)):
            curr = 0
            for j in range(i, len(nums)):
                curr += nums[j]
                max_sum = max(max_sum, curr)
        return max_sum""",
            "code_java": """class Solution {
    public int maxSubArray(int[] nums) {
        int maxSum = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                maxSum = Math.max(maxSum, sum);
            }
        }
        return maxSum;
    }
}""",
            "notes_kotlin": "Quadratic time complexity fails with TLE for N = 100,000.",
            "notes_python": "Nested loops in Python will timeout rapidly.",
            "notes_java": "O(N^2) baseline."
        },
        {
            "name": "Approach 2: Kadane's Algorithm",
            "badge": "Optimal",
            "desc": "Single pass greedy/dynamic programming running sum tracking.",
            "time": "O(N)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun maxSubArray(nums: IntArray): Int {
        var currSum = nums[0]
        var maxSum = nums[0]
        for (i in 1 until nums.size) {
            currSum = maxOf(nums[i], currSum + nums[i])
            maxSum = maxOf(maxSum, currSum)
        }
        return maxSum
    }
}""",
            "code_python": """class Solution:
    def maxSubArray(self, nums: list[int]) -> int:
        curr_sum = nums[0]
        max_sum = nums[0]
        for x in nums[1:]:
            curr_sum = max(x, curr_sum + x)
            max_sum = max(max_sum, curr_sum)
        return max_sum""",
            "code_java": """class Solution {
    public int maxSubArray(int[] nums) {
        int currSum = nums[0];
        int maxSum = nums[0];
        for (int i = 1; i < nums.length; i++) {
            currSum = Math.max(nums[i], currSum + nums[i]);
            maxSum = Math.max(maxSum, currSum);
        }
        return maxSum;
    }
}""",
            "notes_kotlin": "Zero extra allocations. Handles all negative numbers seamlessly.",
            "notes_python": "Clean single pass.",
            "notes_java": "Cache-local register arithmetic."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun maxSubArray(nums: IntArray): Int {
        // Write your solution here:
        return 0
    }
}""",
        "python_boilerplate": """class Solution:
    def maxSubArray(self, nums: list[int]) -> int:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public int maxSubArray(int[] nums) {
        // Write your solution here:
        return 0;
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: [-2,1,-3,4,-1,2,1,-5,4], expected: 6 },
          { input: [1], expected: 1 },
          { input: [5,4,-1,7,8], expected: 23 },
          { input: [-1], expected: -1 }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Does the array always contain at least one element, and can the elements be all negative?",
        "phase2": "The brute force checks every subarray in O(N^2). We can solve this in linear O(N) time and O(1) space using Kadane's algorithm. The key insight is that if our running sum becomes negative, it can never contribute positively to any subsequent subarray, so we should discard it and start fresh.",
        "phase3": "We initialize currSum and maxSum to nums[0]. As we iterate from index 1 to N-1, currSum becomes max(nums[i], currSum + nums[i]), and we update maxSum = max(maxSum, currSum).",
        "phase4": "Testing all negative: [-3, -1, -5]. currSum starts at -3, max -3. Next is -1: max(-1, -3 + -1) = -1. max becomes -1. Next is -5: max(-5, -1 + -5) = -5. max remains -1. Output is -1. Correct.",
        "senior_followup": "What if you must also return the actual start and end indices of the maximum subarray rather than just its sum?"
    }
}

# =========================================================================
# PROBLEM 100: LC #48 ROTATE IMAGE (Pass 2A Core Medium)
# =========================================================================
p100 = {
    "num": 100,
    "lc": 48,
    "title": "Rotate Image",
    "category": "Matrix & In-Place Geometry",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "rotate-image",
    "verbatim_statement": "You are given an <code>n x n</code> 2D matrix representing an image, rotate the image by <strong>90 degrees (clockwise)</strong>.<br><br>You have to rotate the image <strong>in-place</strong>, which means you have to modify the input 2D matrix directly. <strong>DO NOT</strong> allocate another 2D matrix and do the rotation.",
    "examples": [
        {
            "title": "Example 1",
            "input": "matrix = [[1,2,3],[4,5,6],[7,8,9]]",
            "output": "[[7,4,1],[8,5,2],[9,6,3]]"
        },
        {
            "title": "Example 2",
            "input": "matrix = [[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]",
            "output": "[[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]"
        }
    ],
    "constraints": [
        "n == matrix.length == matrix[i].length",
        "1 <= n <= 20",
        "-1000 <= matrix[i][j] <= 1000"
    ],
    "plain_english": "Rotate an N x N square grid 90 degrees clockwise without creating a second grid. Mathematically, rotating 90 degrees clockwise is identical to: 1. Transposing the matrix (swapping matrix[i][j] with matrix[j][i]), then 2. Reversing each row horizontally.",
    "boundary_checks": {
        "min_base": "1x1 matrix: rotation is a no-op.",
        "best_exit": "2x2 matrix: 1 diagonal swap and 2 horizontal row reversals.",
        "worst_scan": "20x20 matrix: in-place cache-local transposition."
    },
    "mental_model": {
        "analogy_title": "The Fold and Flip (Origami Geometry)",
        "analogy_text": "If you draw a picture on square tracing paper and want to rotate it 90 degrees clockwise, you can achieve the exact same transformation in two simple physical folds: first, fold the paper diagonally from top-left to bottom-right (transposing rows into columns), then flip the paper horizontally like a page in a book (reversing each row).",
        "aha_moment": "Rotate 90° Clockwise = Transpose across main diagonal + Reverse each row horizontally!",
        "trigger": "In-place 90 degree matrix rotation without allocating a new 2D grid.",
        "sisters": [
            {"name": "Spiral Matrix", "lc": "#54"},
            {"name": "Set Matrix Zeroes", "lc": "#73"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N^2) Time, O(N^2) Space",
        "naive_text": "Allocate a new N x N result matrix and assign res[j][n - 1 - i] = matrix[i][j]. Violates the in-place constraint.",
        "wasted_work": "Allocates an entirely redundant 2D matrix on the heap, violating strict in-place interview requirements.",
        "optimal_complexity": "O(N^2) Time, O(1) Space",
        "optimal_text": "Two-phase in-place transformation: 1. Transpose the matrix across main diagonal (i < j: swap matrix[i][j] and matrix[j][i]). 2. Reverse each row.",
        "core_invariant": "Every element is swapped in-place using O(1) temporary variables without auxiliary arrays.",
        "table_rows": [
            {
                "name": "Naive: New 2D Buffer",
                "time": "O(N^2)",
                "space": "O(N^2)",
                "note": "Forbidden by problem statement; fails in-place constraint."
            },
            {
                "name": "Four-Way Cycle Rotation",
                "time": "O(N^2)",
                "space": "O(1)",
                "note": "Rotates 4 corners in concentric rings; mathematically equivalent but error-prone indexing."
            },
            {
                "name": "Transpose + Horizontal Reverse",
                "time": "O(N^2)",
                "space": "O(1)",
                "note": "★ Production standard. Clean, bug-free, and easy to explain in live interviews."
            }
        ]
    },
    "trace_table": {
        "input_desc": "matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]",
        "columns": ["Phase", "Operation", "Target Indices", "Values Swapped", "Grid State Result"],
        "rows": [
            ["Init", "Start", "-", "-", "[[1, 2, 3], [4, 5, 6], [7, 8, 9]]"],
            ["1. Transpose", "Swap (0, 1) & (1, 0)", "i=0, j=1", "2 <-> 4", "[[1, 4, 3], [2, 5, 6], [7, 8, 9]]"],
            ["1. Transpose", "Swap (0, 2) & (2, 0)", "i=0, j=2", "3 <-> 7", "[[1, 4, 7], [2, 5, 6], [3, 8, 9]]"],
            ["1. Transpose", "Swap (1, 2) & (2, 1)", "i=1, j=2", "6 <-> 8", "[[1, 4, 7], [2, 5, 8], [3, 6, 9]] (Transposed)"],
            ["2. Reverse Rows", "Reverse Row 0", "left=0, right=2", "1 <-> 7", "[[7, 4, 1], [2, 5, 8], [3, 6, 9]]"],
            ["2. Reverse Rows", "Reverse Row 1", "left=0, right=2", "2 <-> 8", "[[7, 4, 1], [8, 5, 2], [3, 6, 9]]"],
            ["2. Reverse Rows", "Reverse Row 2", "left=0, right=2", "3 <-> 9", "[[7, 4, 1], [8, 5, 2], [9, 6, 3]] (Rotated 90°)"]
        ],
        "takeaway": "Notice that transposition swaps matrix[i][j] with matrix[j][i] only when j > i. Then each row is mirrored horizontally."
    },
    "stepper": {
        "title": "Transpose & Reverse 2-Phase Stepper",
        "default_input": "[[1,2,3],[4,5,6],[7,8,9]]",
        "presets": [
            {"label": "3x3 Matrix", "onclick": "loadPreset([[1,2,3],[4,5,6],[7,8,9]])"},
            {"label": "4x4 Matrix", "onclick": "loadPreset([[1,2,3,4],[5,6,7,8],[9,10,11,12],[13,14,15,16]])"}
        ],
        "metrics": [
            {"label": "Phase", "id": "metric-phase", "initial": "Init", "color": "text-emerald-400"},
            {"label": "Current Cell", "id": "metric-cell", "initial": "(0, 0)", "color": "text-cyan-400"},
            {"label": "Swapped With", "id": "metric-swap", "initial": "-", "color": "text-amber-400"},
            {"label": "Progress", "id": "metric-prog", "initial": "0%", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='matrix-viz' class='grid gap-1.5'></div>",
        "initial_narration": "Matrix ready for in-place 90-degree rotation.",
        "pseudocode_lines": [
            "for i = 0 until n:",
            "  for j = i + 1 until n:",
            "    swap(matrix[i][j], matrix[j][i]) // Transpose",
            "for i = 0 until n:",
            "  reverse(matrix[i]) // Horizontal Flip"
        ],
        "js_code": """
        let rotateMat = [[1,2,3],[4,5,6],[7,8,9]];

        function buildSteps(cand) {
          steps = [];
          let mat = JSON.parse(JSON.stringify(rotateMat));
          let n = mat.length;

          steps.push({
            mat: JSON.parse(JSON.stringify(mat)),
            phase: "Init", cell: "-", swap: "-", prog: "0%", line: 1,
            narration: "Initial state of NxN matrix."
          });

          // Phase 1: Transpose
          for (let i = 0; i < n; i++) {
            for (let j = i + 1; j < n; j++) {
              let tmp = mat[i][j];
              mat[i][j] = mat[j][i];
              mat[j][i] = tmp;

              steps.push({
                mat: JSON.parse(JSON.stringify(mat)),
                phase: "1. Transpose",
                cell: `(${i}, ${j})`,
                swap: `(${j}, ${i})`,
                prog: "50%",
                line: 3,
                narration: `Transpose: Swapped (${i},${j}) with (${j},${i}).`
              });
            }
          }

          // Phase 2: Reverse Rows
          for (let i = 0; i < n; i++) {
            mat[i].reverse();
            steps.push({
              mat: JSON.parse(JSON.stringify(mat)),
              phase: "2. Reverse Row",
              cell: `Row ${i}`,
              swap: `Horiz Flip`,
              prog: `${Math.round(50 + ((i + 1) / n) * 50)}%`,
              line: 5,
              narration: `Reversed Row ${i} horizontally.`
            });
          }
        }

        function renderStage(step) {
          const c = document.getElementById('matrix-viz');
          if (!c) return;
          let n = step.mat.length;
          c.style.gridTemplateColumns = `repeat(${n}, minmax(0, 1fr))`;
          c.innerHTML = step.mat.flatMap((row, r) => {
            return row.map((val, col) => {
              return `<div class="w-10 h-10 rounded-lg bg-slate-900 border border-slate-800 flex items-center justify-center font-mono text-xs text-emerald-300 font-bold">
                ${val}
              </div>`;
            });
          }).join('');
        }

        function loadPreset(m) {
          rotateMat = m;
          buildSteps();
          stepperReset();
        }
        """,
        "js_update_metrics": """
        document.getElementById('metric-phase').textContent = step.phase;
        document.getElementById('metric-cell').textContent = step.cell;
        document.getElementById('metric-swap').textContent = step.swap;
        document.getElementById('metric-prog').textContent = step.prog;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Transpose and Reverse Rows",
            "badge": "Optimal",
            "desc": "In-place linear algebra transformation: transpose diagonally, then reverse horizontally.",
            "time": "O(N^2)",
            "space": "O(1) memory",
            "code_kotlin": """class Solution {
    fun rotate(matrix: Array<IntArray>): Unit {
        val n = matrix.size
        // Phase 1: Transpose
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                val temp = matrix[i][j]
                matrix[i][j] = matrix[j][i]
                matrix[j][i] = temp
            }
        }
        // Phase 2: Reverse each row
        for (i in 0 until n) {
            matrix[i].reverse()
        }
    }
}""",
            "code_python": """class Solution:
    def rotate(self, matrix: list[list[int]]) -> None:
        n = len(matrix)
        # Phase 1: Transpose
        for i in range(n):
            for j in range(i + 1, n):
                matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
        # Phase 2: Reverse each row
        for row in matrix:
            row.reverse()""",
            "code_java": """class Solution {
    public void rotate(int[][] matrix) {
        int n = matrix.length;
        // Phase 1: Transpose
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
        // Phase 2: Reverse each row
        for (int i = 0; i < n; i++) {
            int left = 0, right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
    }
}""",
            "notes_kotlin": "Extremely clean. Uses matrix[i].reverse() standard extension function.",
            "notes_python": "Python tuple unpacking makes in-place swapping trivial.",
            "notes_java": "Explicit two-pointer reversal for each row avoids allocations."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun rotate(matrix: Array<IntArray>): Unit {
        // Write your solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def rotate(self, matrix: list[list[int]]) -> None:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public void rotate(int[][] matrix) {
        // Write your solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: [[1,2,3],[4,5,6],[7,8,9]], expected: [[7,4,1],[8,5,2],[9,6,3]] },
          { input: [[1]], expected: [[1]] }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Is the matrix strictly square (N x N), and are we strictly forbidden from allocating a new 2D matrix?",
        "phase2": "Allocating a new matrix takes O(N^2) space. The optimal in-place strategy uses linear algebra: rotating 90 degrees clockwise is identical to transposing the matrix across its main diagonal, followed by reversing each row horizontally. This requires only O(1) extra space.",
        "phase3": "We first run nested loops where j starts from i + 1 to n - 1, swapping matrix[i][j] with matrix[j][i]. Then, for each row, we reverse the elements from left to right.",
        "phase4": "Testing a 2x2: [[1, 2], [3, 4]]. Transpose gives [[1, 3], [2, 4]]. Reversing rows gives [[3, 1], [4, 2]]. That is a 90-degree clockwise rotation. Correct.",
        "senior_followup": "What if you were asked to rotate the matrix 90 degrees counter-clockwise or 180 degrees in-place?"
    }
}

# =========================================================================
# PROBLEM 101: LC #79 WORD SEARCH (Pass 2B Advanced Medium - #1 Karat)
# =========================================================================
p101 = {
    "num": 101,
    "lc": 79,
    "title": "Word Search",
    "category": "Backtracking & 2D Grid DFS",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "word-search",
    "verbatim_statement": "Given an <code>m x n</code> grid of characters <code>board</code> and a string <code>word</code>, return <code>true</code> if <code>word</code> exists in the grid.<br><br>The word can be constructed from letters of sequentially adjacent cells, where adjacent cells are horizontally or vertically neighboring. The same letter cell may not be used more than once in a word.",
    "examples": [
        {
            "title": "Example 1",
            "input": "board = [['A','B','C','E'],['S','F','C','S'],['A','D','E','E']], word = 'ABCCED'",
            "output": "true"
        },
        {
            "title": "Example 2",
            "input": "board = [['A','B','C','E'],['S','F','C','S'],['A','D','E','E']], word = 'SEE'",
            "output": "true"
        },
        {
            "title": "Example 3",
            "input": "board = [['A','B','C','E'],['S','F','C','S'],['A','D','E','E']], word = 'ABCB'",
            "output": "false"
        }
    ],
    "constraints": [
        "m == board.length",
        "n = board[i].length",
        "1 <= m, n <= 6",
        "1 <= word.length <= 15",
        "board and word consist of only lowercase and uppercase English letters"
    ],
    "plain_english": "Find if a target word can be spelled by moving up, down, left, and right on a 2D letter board. You can start at any cell, but you cannot revisit the same cell within the same path. If a path fails, backtrack and restore the cell so other paths can use it.",
    "boundary_checks": {
        "min_base": "Single cell board matching word of length 1 returns true immediately.",
        "best_exit": "Word starts at (0, 0) and matches along top row; returns true on first branch.",
        "worst_scan": "Word does not exist on a grid of matching letters (e.g. board of all 'A's with word 'AAAAAB'); explores 3^L paths."
    },
    "mental_model": {
        "analogy_title": "The Maze Explorer Leaving Breadcrumbs",
        "analogy_text": "Imagine exploring a dungeon maze where you spell a secret password step by step. When you step into a room, you drop a temporary stone (masking the cell with '#') so you don't walk in circles. You explore the 4 adjacent corridors. If all 4 fail, you pick your stone back up (restoring the letter) and backtrack.",
        "aha_moment": "In-Place Visited Masking: Temporarily overwrite board[r][c] = '#' during DFS recursion, then restore board[r][c] = origChar upon backtracking. Zero extra visited set memory!",
        "trigger": "2D grid word path finding with no cell reuse in single path.",
        "sisters": [
            {"name": "Number of Islands", "lc": "#200"},
            {"name": "Word Search II", "lc": "#212"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(M * N * 4^L) Time, O(L) Space with HashSet",
        "naive_text": "DFS using a HashSet of visited coordinates (r, c) on each path.",
        "wasted_work": "HashSet hashing, boxing, and unboxing for coordinate tuples on every recursion step adds heavy JVM overhead.",
        "optimal_complexity": "O(M * N * 3^L) Time, O(L) Recursion Stack Space",
        "optimal_text": "Backtracking DFS with in-place character masking: board[r][c] = '#'. Since we don't turn back to the cell we came from, branching factor is at most 3.",
        "core_invariant": "Every visited cell is restored to its exact original character when the call frame unwinds.",
        "table_rows": [
            {
                "name": "Visited HashSet DFS",
                "time": "O(M * N * 4^L)",
                "space": "O(L) set overhead",
                "note": "High allocation overhead for coordinate objects."
            },
            {
                "name": "In-Place Backtracking DFS",
                "time": "O(M * N * 3^L)",
                "space": "O(L) stack frames",
                "note": "★ Production standard. Zero extra heap memory; #1 Karat interview problem."
            }
        ]
    },
    "trace_table": {
        "input_desc": "board = [['A','B'],['C','D']], word = 'ABD'",
        "columns": ["Call Depth", "Cell (r, c)", "Char Matched", "Branch Explored", "Action / Backtrack"],
        "rows": [
            ["0", "(0, 0)", "'A'", "Matches word[0]", "Mask (0,0)='#', explore neighbors"],
            ["1", "(0, 1)", "'B'", "Matches word[1]", "Mask (0,1)='#', explore neighbors"],
            ["2", "(1, 1)", "'D'", "Matches word[2]", "k == word.length - 1 -> FOUND (true)"],
            ["End", "-", "-", "-", "Return true bubbling up recursion stack"]
        ],
        "takeaway": "When index k reaches word.length - 1 with a character match, we return true immediately without exploring further neighbors."
    },
    "stepper": {
        "title": "Word Search Backtracking DFS Stepper",
        "default_input": "word: 'ABCCED'",
        "presets": [
            {"label": "Word 'ABCCED' (Exists)", "onclick": "loadPreset('ABCCED')"},
            {"label": "Word 'ABCB' (No Reuse)", "onclick": "loadPreset('ABCB')"}
        ],
        "metrics": [
            {"label": "Current Cell", "id": "metric-cell", "initial": "(0, 0)", "color": "text-emerald-400"},
            {"label": "Char Seeking", "id": "metric-char", "initial": "A", "color": "text-cyan-400"},
            {"label": "Path Length", "id": "metric-k", "initial": "0", "color": "text-amber-400"},
            {"label": "Status", "id": "metric-status", "initial": "Searching", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='grid-viz' class='grid grid-cols-4 gap-2'></div>",
        "initial_narration": "Word search visualizer ready to trace DFS path.",
        "pseudocode_lines": [
            "if k == word.length: return true",
            "if out of bounds or board[r][c] != word[k]: return false",
            "board[r][c] = '#' // mark visited",
            "found = dfs(r+1,c) or dfs(r-1,c) or dfs(r,c+1) or dfs(r,c-1)",
            "board[r][c] = origChar // backtrack restore",
            "return found"
        ],
        "js_code": """
        let wsBoard = [
          ['A','B','C','E'],
          ['S','F','C','S'],
          ['A','D','E','E']
        ];
        let wsWord = 'ABCCED';

        function buildSteps(cand) {
          steps = [];
          let path = [[0,0], [0,1], [0,2], [1,2], [2,2], [2,1]];

          steps.push({
            r: 0, c: 0, k: 0, status: "Start at (0, 0)", line: 1,
            narration: "Found starting letter 'A' at (0, 0)."
          });

          for (let i = 1; i < path.length; i++) {
            let [r, c] = path[i];
            steps.push({
              r, c, k: i, status: `Matched '${wsWord[i]}'`, line: 3,
              narration: `Advanced to (${r}, ${c}), matched letter '${wsWord[i]}'.`
            });
          }

          steps.push({
            r: 2, c: 1, k: 5, status: "FOUND!", line: 1,
            narration: "Entire word 'ABCCED' matched successfully."
          });
        }

        function renderStage(step) {
          const c = document.getElementById('grid-viz');
          if (!c) return;
          c.innerHTML = wsBoard.flatMap((row, r) => {
            return row.map((char, col) => {
              let isCurrent = (r === step.r && col === step.c);
              let border = isCurrent ? 'border-amber-400 bg-amber-950/40 text-amber-300 font-bold scale-110' : 'border-slate-800 bg-slate-900 text-slate-300';
              return `<div class="w-12 h-12 rounded-xl border flex items-center justify-center font-mono text-sm transition-all ${border}">
                ${char}
              </div>`;
            });
          }).join('');
        }

        function loadPreset(w) {
          wsWord = w;
          buildSteps();
          stepperReset();
        }
        """,
        "js_update_metrics": """
        document.getElementById('metric-cell').textContent = `(${step.r}, ${step.c})`;
        document.getElementById('metric-char').textContent = wsWord[step.k] || '-';
        document.getElementById('metric-k').textContent = step.k;
        document.getElementById('metric-status').textContent = step.status;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: In-Place Backtracking DFS",
            "badge": "Optimal",
            "desc": "Depth-first search with in-place cell masking and visited restoration.",
            "time": "O(M * N * 3^L)",
            "space": "O(L) recursion stack",
            "code_kotlin": """class Solution {
    fun exist(board: Array<CharArray>, word: String): Boolean {
        val m = board.size
        val n = board[0].size

        fun dfs(r: Int, c: Int, k: Int): Boolean {
            if (k == word.length) return true
            if (r !in 0 until m || c !in 0 until n || board[r][c] != word[k]) return false

            val temp = board[r][c]
            board[r][c] = '#' // Mask visited

            val found = dfs(r + 1, c, k + 1) ||
                        dfs(r - 1, c, k + 1) ||
                        dfs(r, c + 1, k + 1) ||
                        dfs(r, c - 1, k + 1)

            board[r][c] = temp // Restore on backtrack
            return found
        }

        for (r in 0 until m) {
            for (c in 0 until n) {
                if (board[r][c] == word[0] && dfs(r, c, 0)) {
                    return true
                }
            }
        }
        return false
    }
}""",
            "code_python": """class Solution:
    def exist(self, board: list[list[str]], word: str) -> bool:
        m, n = len(board), len(board[0])

        def dfs(r: int, c: int, k: int) -> bool:
            if k == len(word):
                return True
            if not (0 <= r < m and 0 <= c < n) or board[r][c] != word[k]:
                return False

            temp = board[r][c]
            board[r][c] = '#'

            found = (dfs(r + 1, c, k + 1) or
                     dfs(r - 1, c, k + 1) or
                     dfs(r, c + 1, k + 1) or
                     dfs(r, c - 1, k + 1))

            board[r][c] = temp
            return found

        for r in range(m):
            for c in range(n):
                if board[r][c] == word[0] and dfs(r, c, 0):
                    return True
        return False""",
            "code_java": """class Solution {
    public boolean exist(char[][] board, String word) {
        int m = board.length;
        int n = board[0].length;

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (board[r][c] == word.charAt(0) && dfs(board, word, r, c, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, int r, int c, int k) {
        if (k == word.length()) return true;
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != word.charAt(k)) {
            return false;
        }

        char temp = board[r][c];
        board[r][c] = '#';

        boolean found = dfs(board, word, r + 1, c, k + 1) ||
                        dfs(board, word, r - 1, c, k + 1) ||
                        dfs(board, word, r, c + 1, k + 1) ||
                        dfs(board, word, r, c - 1, k + 1);

        board[r][c] = temp;
        return found;
    }
}""",
            "notes_kotlin": "Idiomatic local function closure capturing board dimensions.",
            "notes_python": "Clean nested DFS with character masking.",
            "notes_java": "Fast primitive char comparison with zero heap allocations."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun exist(board: Array<CharArray>, word: String): Boolean {
        // Write your solution here:
        return false
    }
}""",
        "python_boilerplate": """class Solution:
    def exist(self, board: list[list[str]], word: str) -> bool:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public boolean exist(char[][] board, String word) {
        // Write your solution here:
        return false;
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { board: [['A','B','C','E'],['S','F','C','S'],['A','D','E','E']], word: 'ABCCED' }, expected: true },
          { input: { board: [['A','B','C','E'],['S','F','C','S'],['A','D','E','E']], word: 'SEE' }, expected: true },
          { input: { board: [['A','B','C','E'],['S','F','C','S'],['A','D','E','E']], word: 'ABCB' }, expected: false }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Can a cell be reused within the same word path, and does diagonal movement count as adjacent?",
        "phase2": "Diagonal movement is disallowed, and cells cannot be reused in a single path. We can search from each grid cell using Backtracking DFS. To avoid allocating a separate visited set, we mask the current cell with a sentinel character '#' during recursion and restore it before returning.",
        "phase3": "We iterate over all cells. When board[r][c] matches word[0], we trigger dfs(r, c, 0). The base cases are: if k == word.length, return true; if out of bounds or character mismatch, return false. Otherwise, we mask, explore 4 neighbors with short-circuit OR, restore the character, and return.",
        "phase4": "Testing a loop: board = [['A','B'],['C','D']], word = 'ABDC'. A -> B -> D -> C: matches. If word is 'ABA', A -> B -> try A: blocked because first A is marked '#'. Returns false. Correct.",
        "senior_followup": "What optimization can you add if the word ends with rare characters like 'Z' while the grid starts with abundant 'A's?"
    }
}

# =========================================================================
# PROBLEM 102: LC #399 EVALUATE DIVISION (Pass 2B Advanced Medium - FinTech)
# =========================================================================
p102 = {
    "num": 102,
    "lc": 399,
    "title": "Evaluate Division",
    "category": "Graphs & Weighted DFS/BFS",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "evaluate-division",
    "verbatim_statement": "You are given an array of variable pairs <code>equations</code> and an array of real numbers <code>values</code>, where <code>equations[i] = [Ai, Bi]</code> and <code>values[i]</code> represent the equation <code>Ai / Bi = values[i]</code>. Each <code>Ai</code> or <code>Bi</code> is a string representing a single variable.<br><br>You are also given some <code>queries</code>, where <code>queries[j] = [Cj, Dj]</code> represents the <code>j-th</code> query where you must find the answer for <code>Cj / Dj = ?</code>.<br><br>Return <em>the answers to all queries</em>. If a single answer cannot be determined, return <code>-1.0</code>.",
    "examples": [
        {
            "title": "Example 1",
            "input": "equations = [['a','b'],['b','c']], values = [2.0,3.0], queries = [['a','c'],['b','a'],['a','e'],['a','a'],['x','x']]",
            "output": "[6.0,0.5,-1.0,1.0,-1.0]"
        }
    ],
    "constraints": [
        "1 <= equations.length <= 20",
        "values.length == equations.length",
        "0.0 < values[i] <= 20.0",
        "1 <= queries.length <= 20",
        "Ai, Bi, Cj, Dj consist of lowercase English letters and have length between 1 and 5"
    ],
    "plain_english": "Given ratios between variables like a/b = 2 and b/c = 3, calculate queries like a/c. Think of this as a currency exchange graph: moving from 'a' to 'b' multiplies by 2, and moving from 'b' to 'a' multiplies by 1/2. To find a/c, find a path from a to c and multiply the edge weights along the path.",
    "boundary_checks": {
        "min_base": "Variable not present in any equation returns -1.0 immediately.",
        "best_exit": "Query for known equation or identity a/a for existing variable returns 1.0.",
        "worst_scan": "Disjoint graph components: query connecting variables in different components returns -1.0."
    },
    "mental_model": {
        "analogy_title": "The Foreign Currency Exchange Ledger",
        "analogy_text": "Suppose you know 1 USD = 0.9 EUR, and 1 EUR = 160 JPY. If someone asks for USD / JPY, you follow the exchange chain: USD -> EUR -> JPY, multiplying 0.9 * 160 = 144. If you want JPY / USD, you follow the reverse edges with reciprocal weights (1 / 144). If someone asks about Bitcoin and you have no exchange rate, the answer is undefined (-1.0).",
        "aha_moment": "Model equations as a directed, weighted graph! For a/b = v, create edge a -> b with weight v, and reverse edge b -> a with weight 1/v. Querying c/d is just path search (DFS/BFS) multiplying weights!",
        "trigger": "Transitive ratio calculations or multi-step currency conversion rates.",
        "sisters": [
            {"name": "Course Schedule", "lc": "#207"},
            {"name": "Network Delay Time", "lc": "#743"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(Q * (V + E)) Time, O(V + E) Space",
        "naive_text": "Run DFS or BFS from start to end for each query independently.",
        "wasted_work": "Re-explores the same graph paths for identical or redundant queries.",
        "optimal_complexity": "O(Q * α(V) + (V + E)) with Union-Find or O(Q * (V + E)) with clean Graph DFS",
        "optimal_text": "Construct adjacency list with weights. For each query, run DFS tracking cumulative product with a visited set. For small constraints (E, Q <= 20), Graph DFS is standard and easiest to write cleanly in an interview.",
        "core_invariant": "Path product from source to target represents the exact ratio source / target.",
        "table_rows": [
            {
                "name": "Graph DFS / BFS",
                "time": "O(Q * (V + E))",
                "space": "O(V + E)",
                "note": "★ FinTech interview standard. Clean, highly readable, and handles reciprocals naturally."
            },
            {
                "name": "Disjoint Set with Weights (Union-Find)",
                "time": "O((E + Q) * α(V))",
                "space": "O(V)",
                "note": "Optimal for millions of online queries; requires complex parent-weight tracking."
            }
        ]
    },
    "trace_table": {
        "input_desc": "a/b = 2.0, b/c = 3.0. Query: a/c",
        "columns": ["Current Node", "Target", "Accumulated Product", "Edges Available", "Next Step"],
        "rows": [
            ["'a'", "'c'", "1.0", "('b', 2.0)", "Visit 'b', prod = 1.0 * 2.0 = 2.0"],
            ["'b'", "'c'", "2.0", "('c', 3.0), ('a', 0.5)", "Visit 'c', prod = 2.0 * 3.0 = 6.0"],
            ["'c'", "'c'", "6.0", "Target reached!", "Return 6.0"]
        ],
        "takeaway": "Product accumulates multiplicatively along the path: 2.0 * 3.0 = 6.0."
    },
    "stepper": {
        "title": "Currency Conversion Graph Path Stepper",
        "default_input": "a/b=2.0, b/c=3.0. Query: a/c",
        "presets": [
            {"label": "Standard Chain", "onclick": "loadPreset()"}
        ],
        "metrics": [
            {"label": "Current Node", "id": "metric-node", "initial": "a", "color": "text-emerald-400"},
            {"label": "Target Node", "id": "metric-target", "initial": "c", "color": "text-cyan-400"},
            {"label": "Running Ratio", "id": "metric-ratio", "initial": "1.0", "color": "text-amber-400"},
            {"label": "Decision", "id": "metric-decision", "initial": "Start", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='graph-viz' class='flex items-center gap-4 text-xs font-mono'></div>",
        "initial_narration": "Graph constructed with edges a <-> b (2.0) and b <-> c (3.0).",
        "pseudocode_lines": [
            "build graph: add_edge(u, v, w) and add_edge(v, u, 1/w)",
            "for each query (start, end):",
            "  if start or end not in graph: return -1.0",
            "  return dfs(start, end, visited, 1.0)"
        ],
        "js_code": """
        function buildSteps(cand) {
          steps = [
            { node: "a", target: "c", ratio: "1.0", line: 1, narration: "Query 'a / c'. Starting at node 'a' with base multiplier 1.0." },
            { node: "b", target: "c", ratio: "2.0", line: 4, narration: "Traversed edge a -> b (weight 2.0). Multiplier is now 2.0." },
            { node: "c", target: "c", ratio: "6.0", line: 4, narration: "Traversed edge b -> c (weight 3.0). Reached target 'c'! Answer is 6.0." }
          ];
        }

        function renderStage(step) {
          const c = document.getElementById('graph-viz');
          if (!c) return;
          c.innerHTML = `
            <div class="p-3 rounded-xl bg-slate-900 border border-slate-700">Node: <strong class="text-emerald-400">${step.node}</strong></div>
            <div class="text-slate-500">&rarr; (${step.ratio}) &rarr;</div>
            <div class="p-3 rounded-xl bg-slate-900 border border-slate-700">Target: <strong class="text-cyan-400">${step.target}</strong></div>
          `;
        }

        function loadPreset() {
          buildSteps();
          stepperReset();
        }
        """,
        "js_update_metrics": """
        document.getElementById('metric-node').textContent = step.node;
        document.getElementById('metric-target').textContent = step.target;
        document.getElementById('metric-ratio').textContent = step.ratio;
        document.getElementById('metric-decision').textContent = step.node === step.target ? 'FOUND' : 'Traversing';
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Graph Construction & Multiplicative DFS",
            "badge": "Optimal",
            "desc": "Adjacency list graph with forward and reciprocal edges, evaluated via DFS.",
            "time": "O(Q * (V + E))",
            "space": "O(V + E)",
            "code_kotlin": """class Solution {
    fun calcEquation(
        equations: List<List<String>>,
        values: DoubleArray,
        queries: List<List<String>>
    ): DoubleArray {
        val graph = HashMap<String, MutableMap<String, Double>>()

        for (i in equations.indices) {
            val u = equations[i][0]
            val v = equations[i][1]
            val w = values[i]
            graph.getOrPut(u) { HashMap() }[v] = w
            graph.getOrPut(v) { HashMap() }[u] = 1.0 / w
        }

        fun dfs(curr: String, target: String, visited: HashSet<String>, acc: Double): Double {
            if (curr == target) return acc
            visited.add(curr)

            val neighbors = graph[curr] ?: return -1.0
            for ((next, weight) in neighbors) {
                if (next !in visited) {
                    val res = dfs(next, target, visited, acc * weight)
                    if (res != -1.0) return res
                }
            }
            return -1.0
        }

        val result = DoubleArray(queries.size)
        for (i in queries.indices) {
            val u = queries[i][0]
            val v = queries[i][1]
            if (u !in graph || v !in graph) {
                result[i] = -1.0
            } else if (u == v) {
                result[i] = 1.0
            } else {
                result[i] = dfs(u, v, HashSet(), 1.0)
            }
        }
        return result
    }
}""",
            "code_python": """class Solution:
    def calcEquation(
        self,
        equations: list[list[str]],
        values: list[float],
        queries: list[list[str]]
    ) -> list[float]:
        from collections import defaultdict
        graph = defaultdict(dict)

        for (u, v), val in zip(equations, values):
            graph[u][v] = val
            graph[v][u] = 1.0 / val

        def dfs(curr: str, target: str, visited: set, acc: float) -> float:
            if curr == target:
                return acc
            visited.add(curr)
            for nxt, weight in graph[curr].items():
                if nxt not in visited:
                    res = dfs(nxt, target, visited, acc * weight)
                    if res != -1.0:
                        return res
            return -1.0

        ans = []
        for u, v in queries:
            if u not in graph or v not in graph:
                ans.append(-1.0)
            elif u == v:
                ans.append(1.0)
            else:
                ans.append(dfs(u, v, set(), 1.0))
        return ans""",
            "code_java": """import java.util.*;

class Solution {
    public double[] calcEquation(
        List<List<String>> equations,
        double[] values,
        List<List<String>> queries
    ) {
        Map<String, Map<String, Double>> graph = new HashMap<>();

        for (int i = 0; i < equations.size(); i++) {
            String u = equations.get(i).get(0);
            String v = equations.get(i).get(1);
            double w = values[i];
            graph.computeIfAbsent(u, k -> new HashMap<>()).put(v, w);
            graph.computeIfAbsent(v, k -> new HashMap<>()).put(u, 1.0 / w);
        }

        double[] ans = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String u = queries.get(i).get(0);
            String v = queries.get(i).get(1);
            if (!graph.containsKey(u) || !graph.containsKey(v)) {
                ans[i] = -1.0;
            } else if (u.equals(v)) {
                ans[i] = 1.0;
            } else {
                ans[i] = dfs(graph, u, v, new HashSet<>(), 1.0);
            }
        }
        return ans;
    }

    private double dfs(
        Map<String, Map<String, Double>> graph,
        String curr,
        String target,
        Set<String> visited,
        double acc
    ) {
        if (curr.equals(target)) return acc;
        visited.add(curr);

        Map<String, Double> neighbors = graph.get(curr);
        if (neighbors != null) {
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String next = entry.getKey();
                if (!visited.contains(next)) {
                    double res = dfs(graph, next, target, visited, acc * entry.getValue());
                    if (res != -1.0) return res;
                }
            }
        }
        return -1.0;
    }
}""",
            "notes_kotlin": "Nested map provides concise representation of weighted graph.",
            "notes_python": "defaultdict(dict) creates fast adjacency lookup.",
            "notes_java": "computeIfAbsent idiomatically builds weighted adjacency graph."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun calcEquation(
        equations: List<List<String>>,
        values: DoubleArray,
        queries: List<List<String>>
    ): DoubleArray {
        // Write your solution here:
        return doubleArrayOf()
    }
}""",
        "python_boilerplate": """class Solution:
    def calcEquation(
        self,
        equations: list[list[str]],
        values: list[float],
        queries: list[list[str]]
    ) -> list[float]:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public double[] calcEquation(
        List<List<String>> equations,
        double[] values,
        List<List<String>> queries
    ) {
        // Write your solution here:
        return new double[0];
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { equations: [['a','b'],['b','c']], values: [2.0,3.0], queries: [['a','c'],['b','a'],['a','e'],['a','a'],['x','x']] }, expected: [6.0,0.5,-1.0,1.0,-1.0] }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Can equations contain division by zero, and are variable names guaranteed non-empty strings?",
        "phase2": "This problem is naturally a directed weighted graph. An equation a / b = v defines an edge from a to b with weight v, and a reverse edge from b to a with weight 1/v. Querying c / d is equivalent to finding a path from c to d and multiplying the edge weights.",
        "phase3": "We build an adjacency list map: graph[u][v] = w and graph[v][u] = 1.0/w. For each query (start, end), if either node does not exist, return -1.0. If start == end, return 1.0. Otherwise, run DFS with a visited set to avoid cycles, accumulating the product along the path.",
        "phase4": "Testing a / c with a/b=2, b/c=3: a -> b multiplies 1.0 * 2.0 = 2.0; b -> c multiplies 2.0 * 3.0 = 6.0. Returns 6.0. Correct.",
        "senior_followup": "If this was an active financial FX exchange service handling millions of queries per second with streaming currency updates, how would you optimize query latency?"
    }
}

# =========================================================================
# PROBLEM 103: LC #5 LONGEST PALINDROMIC SUBSTRING (Pass 2B Adv Medium)
# =========================================================================
p103 = {
    "num": 103,
    "lc": 5,
    "title": "Longest Palindromic Substring",
    "category": "Two Pointers & String Manipulation",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "longest-palindromic-substring",
    "verbatim_statement": "Given a string <code>s</code>, return <em>the longest palindromic substring</em> in <code>s</code>.<br><br>A substring is a contiguous sequence of characters within a string.",
    "examples": [
        {
            "title": "Example 1",
            "input": "s = 'babad'",
            "output": "'bab' (or 'aba')"
        },
        {
            "title": "Example 2",
            "input": "s = 'cbbd'",
            "output": "'bb'"
        }
    ],
    "constraints": [
        "1 <= s.length <= 1000",
        "s consist of only digits and English letters"
    ],
    "plain_english": "Find the longest sequence of characters within a string that reads the same forwards and backwards. Instead of testing all O(N^2) substrings by reversing them in O(N^3), notice that every palindrome expands outwards from a center. There are only 2N - 1 possible centers (odd-length single letters and even-length gaps between letters). Expanding outwards from each center takes O(N^2) total time and O(1) space.",
    "boundary_checks": {
        "min_base": "Single character string: returns s itself immediately.",
        "best_exit": "Two identical characters 'aa': returns 'aa'.",
        "worst_scan": "String of all identical characters (e.g. 1000 'a's): every expansion goes to boundaries."
    },
    "mental_model": {
        "analogy_title": "The Ripples in a Pond (Expanding from Centers)",
        "analogy_text": "Imagine throwing a stone into a still pond: ripples expand outward symmetrically from the impact point. A palindrome works identically: it has an epicenter (either a single character like 'r-a-c-e-c-a-r' or a mirrored pair like 'n-o-o-n') and expands symmetrically to the left and right as long as both characters match.",
        "aha_moment": "Expand Around Center: There are only 2N - 1 centers! Expand odd centers (i, i) and even centers (i, i+1). Zero DP table allocation, strictly O(1) space!",
        "trigger": "Longest contiguous palindromic sequence in a string.",
        "sisters": [
            {"name": "Valid Palindrome", "lc": "#125"},
            {"name": "Palindromic Substrings", "lc": "#647"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N^3) Time, O(1) Space",
        "naive_text": "Check every possible substring (i, j) and verify if it is a palindrome by reading it backwards.",
        "wasted_work": "Examines O(N^2) substrings and spends O(N) checking each, re-verifying identical inner palindromes repeatedly.",
        "optimal_complexity": "O(N^2) Time, O(1) Space",
        "optimal_text": "Expand around 2N - 1 centers. For each center, expand pointers left and right while s[left] == s[right]. Track the maximum start index and length.",
        "core_invariant": "A palindrome of length L can only exist if its inner core of length L - 2 is also a palindrome.",
        "table_rows": [
            {
                "name": "Naive Substring Verification",
                "time": "O(N^3)",
                "space": "O(1)",
                "note": "Cubic time; completely unscalable."
            },
            {
                "name": "2D Dynamic Programming Table",
                "time": "O(N^2)",
                "space": "O(N^2)",
                "note": "dp[i][j] table; quadratic heap memory is wasteful."
            },
            {
                "name": "Expand Around Center",
                "time": "O(N^2)",
                "space": "O(1)",
                "note": "★ Production standard. Cache friendly, zero heap allocations."
            }
        ]
    },
    "trace_table": {
        "input_desc": "s = 'babad'",
        "columns": ["Center i", "Odd Expansion (i, i)", "Even Expansion (i, i+1)", "Max Length Found", "Best Substring"],
        "rows": [
            ["0", "'b' (len 1)", "none", "1", "'b'"],
            ["1", "'aba' (len 3)", "none", "3", "'aba'"],
            ["2", "'bab' (len 3)", "none", "3", "'aba' (preserved)"],
            ["3", "'a' (len 1)", "none", "3", "'aba'"],
            ["4", "'d' (len 1)", "none", "3", "'aba'"]
        ],
        "takeaway": "At center index 1, expanding from 'a' matches 'b' on left and 'b' on right, achieving length 3 ('bab')."
    },
    "stepper": {
        "title": "Expand Around Center Palindrome Stepper",
        "default_input": "s = 'babad'",
        "presets": [
            {"label": "Odd Palindrome 'babad'", "onclick": "loadPreset('babad')"},
            {"label": "Even Palindrome 'cbbd'", "onclick": "loadPreset('cbbd')"}
        ],
        "metrics": [
            {"label": "Center Index", "id": "metric-center", "initial": "0", "color": "text-emerald-400"},
            {"label": "Left Pointer", "id": "metric-left", "initial": "0", "color": "text-cyan-400"},
            {"label": "Right Pointer", "id": "metric-right", "initial": "0", "color": "text-amber-400"},
            {"label": "Longest So Far", "id": "metric-longest", "initial": "b", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='palindrome-viz' class='flex items-center gap-2'></div>",
        "initial_narration": "Palindrome expander initialized.",
        "pseudocode_lines": [
            "for i = 0 until s.length:",
            "  expand(i, i) // Odd length center",
            "  expand(i, i + 1) // Even length center",
            "return s.substring(start, start + maxLen)"
        ],
        "js_code": """
        let palStr = 'babad';

        function buildSteps(cand) {
          steps = [
            { center: 0, left: 0, right: 0, sub: "b", line: 1, narration: "Center 0 ('b'): length 1." },
            { center: 1, left: 0, right: 2, sub: "bab", line: 2, narration: "Center 1 ('a'): expanded left to 0 ('b') and right to 2 ('b'). Palindrome: 'bab' (len 3)." },
            { center: 2, left: 1, right: 3, sub: "aba", line: 2, narration: "Center 2 ('b'): expanded to 'aba' (len 3)." },
            { center: 3, left: 3, right: 3, sub: "aba", line: 1, narration: "Center 3 ('a'): single char 'a'." },
            { center: 4, left: 4, right: 4, sub: "aba", line: 4, narration: "Scan complete. Longest palindrome found: 'bab' / 'aba'." }
          ];
        }

        function renderStage(step) {
          const c = document.getElementById('palindrome-viz');
          if (!c) return;
          c.innerHTML = palStr.split('').map((ch, idx) => {
            let inPal = (idx >= step.left && idx <= step.right);
            let isCenter = (idx === step.center);
            let border = inPal ? 'border-emerald-400 bg-emerald-950/40 text-emerald-300 font-bold scale-105' : 'border-slate-800 bg-slate-900 text-slate-400';
            return `<div class="w-10 h-12 rounded-xl border flex flex-col items-center justify-center font-mono text-xs ${border}">
              <span class="text-[9px] text-slate-500">${idx}</span>
              <span>${ch}</span>
            </div>`;
          }).join('');
        }

        function loadPreset(str) {
          palStr = str;
          buildSteps();
          stepperReset();
        }
        """,
        "js_update_metrics": """
        document.getElementById('metric-center').textContent = step.center;
        document.getElementById('metric-left').textContent = step.left;
        document.getElementById('metric-right').textContent = step.right;
        document.getElementById('metric-longest').textContent = step.sub;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Expand Around Center",
            "badge": "Optimal",
            "desc": "Check 2N - 1 centers expanding outwards symmetrically.",
            "time": "O(N^2)",
            "space": "O(1) memory",
            "code_kotlin": """class Solution {
    fun longestPalindrome(s: String): String {
        if (s.length <= 1) return s
        var start = 0
        var maxLen = 0

        fun expand(l: Int, r: Int) {
            var left = l
            var right = r
            while (left >= 0 && right < s.length && s[left] == s[right]) {
                left--
                right++
            }
            val len = right - left - 1
            if (len > maxLen) {
                maxLen = len
                start = left + 1
            }
        }

        for (i in s.indices) {
            expand(i, i)     // Odd length
            expand(i, i + 1) // Even length
        }

        return s.substring(start, start + maxLen)
    }
}""",
            "code_python": """class Solution:
    def longestPalindrome(self, s: str) -> str:
        if len(s) <= 1:
            return s

        start = 0
        max_len = 0

        def expand(left: int, right: int):
            nonlocal start, max_len
            while left >= 0 and right < len(s) and s[left] == s[right]:
                left -= 1
                right += 1
            length = right - left - 1
            if length > max_len:
                max_len = length
                start = left + 1

        for i in range(len(s)):
            expand(i, i)       # Odd length
            expand(i, i + 1)   # Even length

        return s[start:start + max_len]""",
            "code_java": """class Solution {
    public String longestPalindrome(String s) {
        if (s == null || s.length() <= 1) return s;

        int start = 0, maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            int len1 = expand(s, i, i);     // Odd length
            int len2 = expand(s, i, i + 1); // Even length
            int len = Math.max(len1, len2);

            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2;
            }
        }

        return s.substring(start, start + maxLen);
    }

    private int expand(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }
}""",
            "notes_kotlin": "Zero memory allocation. Pure index pointers with single substring slice at end.",
            "notes_python": "Clean helper closure with nonlocal indices.",
            "notes_java": "String.charAt avoids creating intermediate strings."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun longestPalindrome(s: String): String {
        // Write your solution here:
        return ""
    }
}""",
        "python_boilerplate": """class Solution:
    def longestPalindrome(self, s: str) -> str:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public String longestPalindrome(String s) {
        // Write your solution here:
        return "";
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: 'babad', expected: 'bab' },
          { input: 'cbbd', expected: 'bb' },
          { input: 'a', expected: 'a' }
        ];
        """
    },
    "spoken_script": {
        "phase1": "If there are multiple palindromic substrings with the same maximum length, is returning any valid?",
        "phase2": "Checking all substrings costs O(N^3). A 2D DP table costs O(N^2) time but wastes O(N^2) space. The optimal approach is expanding around centers: every palindrome expands symmetrically from its center. There are 2N - 1 centers (N odd centers of length 1, and N - 1 even centers of length 2). Expanding from each center takes O(N), yielding O(N^2) time and strictly O(1) space.",
        "phase3": "We maintain start and maxLen. For each index i from 0 to N-1, we call expand(i, i) and expand(i, i+1). Inside expand, while left >= 0 and right < s.length and characters match, we decrement left and increment right. If right - left - 1 is greater than maxLen, we update our best start and maxLen.",
        "phase4": "Testing 'cbbd': at i=1, even expand(1, 2) matches 'b' and 'b'. left becomes 0 ('c'), right becomes 3 ('d'). Mismatch. Length is 3 - 0 - 1 = 2. Substring is 'bb'. Correct.",
        "senior_followup": "Can this be solved in strictly linear O(N) time? (Manacher's Algorithm)"
    }
}

all_new_problems = [p97, p98, p99, p100, p101, p102, p103]
