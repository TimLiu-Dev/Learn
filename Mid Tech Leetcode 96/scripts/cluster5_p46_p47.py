import sys
sys.path.append('scripts')

p46 = {
    'num': 46,
    'lc': 56,
    'title': "Merge Intervals",
    'category': "Intervals & Greedy",
    'difficulty': "Medium",
    'lc_slug': "merge-intervals",
    'verbatim_statement': """<p>Given an array of <code>intervals</code> where <code>intervals[i] = [start<sub>i</sub>, end<sub>i</sub>]</code>, merge all overlapping intervals, and return <em>an array of the non-overlapping intervals that cover all the intervals in the input</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'intervals = [[1,3],[2,6],[8,10],[15,18]]', 'output': '[[1,6],[8,10],[15,18]]\nExplanation: Since intervals [1,3] and [2,6] overlap, merge them into [1,6].'},
        {'title': "Example 2", 'input': 'intervals = [[1,4],[4,5]]', 'output': '[[1,5]]\nExplanation: Intervals [1,4] and [4,5] are considered overlapping.'}
    ],
    'constraints': [
        "1 <= intervals.length <= 10^4",
        "intervals[i].length == 2",
        "0 <= start_i <= end_i <= 10^4"
    ],
    'plain_english': "Sort intervals by their starting times. Maintain a list of merged intervals. For each interval, if it starts before or when the previous interval ends (`curr.start <= last.end`), merge them by extending `last.end = max(last.end, curr.end)`. Otherwise, append the interval as a new disjoint block.",
    'boundary_checks': {
        'min_base': "Single interval <code>[[1, 5]]</code> &rarr; Returns <code>[[1, 5]]</code> directly.",
        'best_exit': "Adjacent touching boundary <code>[[1,4],[4,5]]</code> &rarr; Overlaps at point 4, merges to <code>[[1,5]]</code>.",
        'worst_scan': "Enclosing interval <code>[[1,10],[2,3],[4,5],[6,7]]</code> &rarr; All engulfed into <code>[[1,10]]</code>."
    },
    'mental_model': {
        'analogy_title': "Calendar Meeting Compactor",
        'analogy_text': "Imagine scheduling meetings on a calendar. First, sort all meeting slips by their start time on your desk. Take the earliest meeting slip. Pick up the next slip: does it begin before your current meeting ends? If yes, you simply slide your departure time out to whichever meeting ends later (`max(end1, end2)`). If the next meeting starts after a break, you lock in the current block and start a fresh meeting block.",
        'aha_moment': "Sorting by start time guarantees that once an interval starts AFTER the current merged interval ends (`curr[0] > last[1]`), NO SUBSEQUENT interval can ever overlap with `last`! This reduces a quadratic pairwise comparison problem to a single linear sweep.",
        'trigger': "Overlapping ranges, calendar booking, scheduling conflicts, range consolidation &rarr; Sort by start time + Greedy merge loop.",
        'sisters': [
            {'name': "Insert Interval", 'lc': "LC #57"},
            {'name': "Non-overlapping Intervals", 'lc': "LC #435"},
            {'name': "Meeting Rooms II", 'lc': "LC #253"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(N) Space",
        'naive_text': "Model intervals as graph nodes where an edge exists if two intervals overlap. Find connected components and compute their bounding hulls.",
        'wasted_work': "Comparing every pair of intervals when a single sort establishes total ordering.",
        'optimal_complexity': "O(N log N) Time, O(N) Space",
        'optimal_text': "Sort by start time in O(N log N). Single pass merge in O(N) time with constant space per step.",
        'core_invariant': "All intervals prior to the current index in `result` are strictly disjoint, sorted, and immutable.",
        'table_rows': [
            {'name': "1. Connected Components Graph", 'time': "O(N^2)", 'space': "O(N^2)", 'note': "Builds overlap graph; extremely slow on large N."},
            {'name': "2. Sweep-Line Event Points", 'time': "O(N log N)", 'space': "O(N)", 'note': "Splits into +1/-1 start/end points; complex edge cases on touching endpoints."},
            {'name': "★ 3. Sort by Start Time + Greedy Sweep", 'time': "O(N log N)", 'space': "O(N)", 'note': "Standard optimal algorithm. Cleanest in interviews."}
        ]
    },
    'trace_table': {
        'input_desc': 'intervals = [[1,3], [2,6], [8,10], [15,18]]',
        'columns': ["Step", "Examining Interval", "Last Merged in Result", "Condition (start <= last.end)", "Action", "Result State"],
        'rows': [
            ["Init", "-", "-", "-", "Sort by start time", "[[1,3], [2,6], [8,10], [15,18]]"],
            ["1", "[1, 3]", "None", "-", "Add [1,3]", "[[1,3]]"],
            ["2", "[2, 6]", "[1, 3]", "2 <= 3 (True)", "Merge: max(3, 6) = 6", "[[1,6]]"],
            ["3", "[8, 10]", "[1, 6]", "8 <= 6 (False)", "Disjoint! Add [8,10]", "[[1,6], [8,10]]"],
            ["4", "[15, 18]", "[8, 10]", "15 <= 10 (False)", "Disjoint! Add [15,18]", "[[1,6], [8,10], [15,18]]"]
        ],
        'takeaway': "Sorting guarantees once 8 > 6, [1,6] is permanently closed and will never be touched again."
    },
    'stepper': {
        'title': "Interval Sorting & Greedy Merge Stepper",
        'default_input': '[[1,3],[2,6],[8,10],[15,18]]',
        'presets': [
            {'label': "Classic [[1,3],[2,6],[8,10],[15,18]]", 'onclick': 'loadPreset([[1,3],[2,6],[8,10],[15,18]])'},
            {'label': "Touching [[1,4],[4,5]]", 'onclick': 'loadPreset([[1,4],[4,5]])'},
            {'label': "Nested [[1,10],[2,4],[5,7]]", 'onclick': 'loadPreset([[1,10],[2,4],[5,7]])'}
        ],
        'metrics': [
            {'id': "metric-curr", 'label': "Current Interval", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-last", 'label': "Last Merged Block", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-overlap", 'label': "Overlap Detected?", 'color': "text-emerald-400", 'initial': "No"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="interval-stage" class="w-full flex flex-col items-center justify-center gap-3 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace intervals sorting and greedy merging.',
        'pseudocode_lines': [
            "intervals.sortBy { it[0] }",
            "val result = mutableListOf<IntArray>()",
            "for (interval in intervals) {",
            "    if (result.isEmpty() || interval[0] > result.last()[1]) {",
            "        result.add(interval)",
            "    } else {",
            "        result.last()[1] = maxOf(result.last()[1], interval[1])",
            "    }",
            "}",
            "return result.toTypedArray()"
        ],
        'js_code': """
    let rawIntervals = [[1,3],[2,6],[8,10],[15,18]];

    function buildSteps(arr) {
      steps = [];
      let sorted = [...arr].sort((a, b) => a[0] - b[0]);
      let res = [];

      steps.push({
        curr: "-", last: "-", overlap: "No", action: "Sorted by start time",
        res: [], sorted: sorted, line: 1,
        narration: `Sorted intervals: ${sorted.map(x => `[${x[0]},${x[1]}]`).join(', ')}.`
      });

      for (let i = 0; i < sorted.length; i++) {
        let it = sorted[i];
        if (res.length === 0 || it[0] > res[res.length - 1][1]) {
          res.push([...it]);
          steps.push({
            curr: `[${it[0]}, ${it[1]}]`,
            last: res.length > 1 ? `[${res[res.length - 2][0]}, ${res[res.length - 2][1]}]` : "-",
            overlap: "No",
            action: `Added [${it[0]}, ${it[1]}]`,
            res: JSON.parse(JSON.stringify(res)),
            sorted: sorted,
            line: 5,
            narration: `Interval [${it[0]}, ${it[1]}] starts after last merged ends. Added as new disjoint block.`
          });
        } else {
          let lastBlock = res[res.length - 1];
          let oldEnd = lastBlock[1];
          lastBlock[1] = Math.max(lastBlock[1], it[1]);
          steps.push({
            curr: `[${it[0]}, ${it[1]}]`,
            last: `[${lastBlock[0]}, ${oldEnd}]`,
            overlap: "YES (Merge)",
            action: `Extended to [${lastBlock[0]}, ${lastBlock[1]}]`,
            res: JSON.parse(JSON.stringify(res)),
            sorted: sorted,
            line: 7,
            narration: `Overlap! ${it[0]} <= ${oldEnd}. Extended end: max(${oldEnd}, ${it[1]}) = ${lastBlock[1]}.`
          });
        }
      }

      steps.push({
        curr: "Done", last: "-", overlap: "-", action: "Finished",
        res: JSON.parse(JSON.stringify(res)),
        sorted: sorted, line: 10,
        narration: `Merge complete! Non-overlapping result: ${res.map(x => `[${x[0]},${x[1]}]`).join(', ')}.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.map(x => `[${x[0]},${x[1]}]`).join(', ');
      rawIntervals = arr;
      stepperReset();
      buildSteps(rawIntervals);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const parsed = JSON.parse('[' + val.replace(/\\[/g, '[').replace(/\\]/g, ']') + ']');
        if (parsed.length > 0) {
          rawIntervals = parsed;
          stepperReset();
          buildSteps(rawIntervals);
          renderStepper();
        }
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-curr').textContent = step.curr;
      document.getElementById('metric-last').textContent = step.last;
      document.getElementById('metric-overlap').textContent = step.overlap;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('interval-stage');
      stage.innerHTML = '';
      if (!step.res) return;

      const wrap = document.createElement('div');
      wrap.className = 'w-full flex flex-col gap-2';

      const rowLabel = document.createElement('div');
      rowLabel.className = 'text-xs font-mono text-slate-400 font-bold';
      rowLabel.textContent = 'Merged Output Blocks:';
      wrap.appendChild(rowLabel);

      const blockRow = document.createElement('div');
      blockRow.className = 'flex flex-wrap items-center gap-2';
      step.res.forEach((iv, idx) => {
        const item = document.createElement('div');
        item.className = 'px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-mono text-xs font-bold';
        item.textContent = `[${iv[0]}, ${iv[1]}]`;
        blockRow.appendChild(item);
      });
      wrap.appendChild(blockRow);

      stage.appendChild(wrap);
"""
    },
    'approaches': [
        {
            'name': "Connected Components Overlap Graph (Naive)",
            'desc': "Construct an adjacency graph where an edge connects two overlapping intervals. Find components with BFS/DFS.",
            'time': "O(N^2)",
            'space': "O(N^2)",
            'code_kotlin': """class Solution {
    fun merge(intervals: Array<IntArray>): Array<IntArray> {
        val n = intervals.size
        val adj = Array(n) { mutableListOf<Int>() }
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                if (maxOf(intervals[i][0], intervals[j][0]) <= minOf(intervals[i][1], intervals[j][1])) {
                    adj[i].add(j)
                    adj[j].add(i)
                }
            }
        }
        val visited = BooleanArray(n)
        val result = mutableListOf<IntArray>()
        for (i in 0 until n) {
            if (!visited[i]) {
                var start = intervals[i][0]
                var end = intervals[i][1]
                val q = ArrayDeque<Int>().apply { add(i) }
                visited[i] = true
                while (q.isNotEmpty()) {
                    val curr = q.removeFirst()
                    start = minOf(start, intervals[curr][0])
                    end = maxOf(end, intervals[curr][1])
                    for (neighbor in adj[curr]) {
                        if (!visited[neighbor]) {
                            visited[neighbor] = true
                            q.add(neighbor)
                        }
                    }
                }
                result.add(intArrayOf(start, end))
            }
        }
        return result.toTypedArray()
    }
}""",
            'notes_kotlin': "Quadratic graph construction. Wastes memory and CPU comparing disjoint pairs.",
            'code_python': """class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        n = len(intervals)
        adj = collections.defaultdict(list)
        for i in range(n):
            for j in range(i + 1, n):
                if max(intervals[i][0], intervals[j][0]) <= min(intervals[i][1], intervals[j][1]):
                    adj[i].append(j)
                    adj[j].append(i)
        visited = [False] * n
        res = []
        for i in range(n):
            if not visited[i]:
                q = collections.deque([i])
                visited[i] = True
                s, e = intervals[i][0], intervals[i][1]
                while q:
                    cur = q.popleft()
                    s = min(s, intervals[cur][0])
                    e = max(e, intervals[cur][1])
                    for nbr in adj[cur]:
                        if not visited[nbr]:
                            visited[nbr] = True
                            q.append(nbr)
                res.append([s, e])
        return res""",
            'notes_python': "Graph connected components. Demonstrates interval overlap equivalence, but O(N^2) time.",
            'code_java': """class Solution {
    public int[][] merge(int[][] intervals) {
        int n = intervals.length;
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Math.max(intervals[i][0], intervals[j][0]) <= Math.min(intervals[i][1], intervals[j][1])) {
                    adj[i].add(j);
                    adj[j].add(i);
                }
            }
        }
        boolean[] visited = new boolean[n];
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                int start = intervals[i][0], end = intervals[i][1];
                Queue<Integer> q = new ArrayDeque<>();
                q.offer(i);
                visited[i] = true;
                while (!q.isEmpty()) {
                    int cur = q.poll();
                    start = Math.min(start, intervals[cur][0]);
                    end = Math.max(end, intervals[cur][1]);
                    for (int nbr : adj[cur]) {
                        if (!visited[nbr]) {
                            visited[nbr] = true;
                            q.offer(nbr);
                        }
                    }
                }
                result.add(new int[]{start, end});
            }
        }
        return result.toArray(new int[result.size()][]);
    }
}""",
            'notes_java': "O(N^2) space and time. Unacceptable for N = 10,000."
        },
        {
            'name': "Sweep Line / Endpoint Events",
            'desc': "Decompose intervals into start (+1) and end (-1) point events. Track active interval count.",
            'time': "O(N log N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun merge(intervals: Array<IntArray>): Array<IntArray> {
        val starts = IntArray(intervals.size) { intervals[it][0] }.sortedArray()
        val ends = IntArray(intervals.size) { intervals[it][1] }.sortedArray()
        val result = mutableListOf<IntArray>()
        var startIdx = 0
        for (endIdx in ends.indices) {
            if (endIdx == ends.size - 1 || starts[endIdx + 1] > ends[endIdx]) {
                result.add(intArrayOf(starts[startIdx], ends[endIdx]))
                startIdx = endIdx + 1
            }
        }
        return result.toTypedArray()
    }
}""",
            'notes_kotlin': "Two sorted parallel arrays for start and end times. Very efficient with minimal object creation.",
            'code_python': """class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        starts = sorted([i[0] for i in intervals])
        ends = sorted([i[1] for i in intervals])
        res = []
        start_idx = 0
        for end_idx in range(len(ends)):
            if end_idx == len(ends) - 1 or starts[end_idx + 1] > ends[end_idx]:
                res.append([starts[start_idx], ends[end_idx]])
                start_idx = end_idx + 1
        return res""",
            'notes_python': "Parallel sorted arrays sweep approach. Elegant but requires tracking two separate lists.",
            'code_java': """class Solution {
    public int[][] merge(int[][] intervals) {
        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];
        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }
        Arrays.sort(starts);
        Arrays.sort(ends);
        List<int[]> result = new ArrayList<>();
        int startIdx = 0;
        for (int endIdx = 0; endIdx < n; endIdx++) {
            if (endIdx == n - 1 || starts[endIdx + 1] > ends[endIdx]) {
                result.add(new int[]{starts[startIdx], ends[endIdx]});
                startIdx = endIdx + 1;
            }
        }
        return result.toArray(new int[result.size()][]);
    }
}""",
            'notes_java': "Sorts primitive integer arrays directly for maximum performance."
        },
        {
            'name': "Sort by Start Time + Greedy Merge (Optimal Primary)",
            'desc': "Sort intervals by start time. Maintain merged list, extending last interval's end on overlap.",
            'time': "O(N log N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Sort by Start Time + Single Linear Merge Pass
     * Time: O(N log N) - Dominated by sorting
     * Space: O(N) - Storage for merged result array
     */
    fun merge(intervals: Array<IntArray>): Array<IntArray> {
        if (intervals.size <= 1) return intervals

        // Sort ascending by start time
        intervals.sortBy { it[0] }

        val result = mutableListOf<IntArray>()

        for (interval in intervals) {
            // If result is empty or current does not overlap with previous
            if (result.isEmpty() || interval[0] > result.last()[1]) {
                result.add(interval)
            } else {
                // Overlap exists: extend previous interval's end time
                result.last()[1] = maxOf(result.last()[1], interval[1])
            }
        }

        return result.toTypedArray()
    }
}""",
            'notes_kotlin': "Canonical interview solution. Sorting by `it[0]` establishes deterministic linear order.",
            'code_python': """class Solution:
    # Optimal: Sort by Start Time + Greedy Merge
    # Time: O(N log N) - Sorting dominates
    # Space: O(N) - Output list
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        intervals.sort(key=lambda x: x[0])
        merged = []

        for interval in intervals:
            if not merged or interval[0] > merged[-1][1]:
                merged.append(interval)
            else:
                merged[-1][1] = max(merged[-1][1], interval[1])

        return merged""",
            'notes_python': "Concise, idiomatic Python. Modifies `merged[-1][1]` in place.",
            'code_java': """class Solution {
    /**
     * Optimal: Sort by Start Time + Greedy Merge
     * Time: O(N log N) - Array sorting
     * Space: O(N) - Output array
     */
    public int[][] merge(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        // Sort by start time ascending
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> result = new ArrayList<>();

        for (int[] interval : intervals) {
            if (result.isEmpty() || interval[0] > result.get(result.size() - 1)[1]) {
                result.add(interval);
            } else {
                int[] last = result.get(result.size() - 1);
                last[1] = Math.max(last[1], interval[1]);
            }
        }

        return result.toArray(new int[result.size()][]);
    }
}""",
            'notes_java': "Standard industry template. Integer.compare prevents subtraction underflow."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun merge(intervals: Array<IntArray>): Array<IntArray> {
        intervals.sortBy { it[0] }
        val res = mutableListOf<IntArray>()
        for (iv in intervals) {
            if (res.isEmpty() || iv[0] > res.last()[1]) {
                res.add(iv)
            } else {
                res.last()[1] = maxOf(res.last()[1], iv[1])
            }
        }
        return res.toTypedArray()
    }
}""",
        'python_boilerplate': """class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        intervals.sort(key=lambda x: x[0])
        res = []
        for iv in intervals:
            if not res or iv[0] > res[-1][1]: res.append(iv)
            else: res[-1][1] = max(res[-1][1], iv[1])
        return res""",
        'java_boilerplate': """class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> res = new ArrayList<>();
        for (int[] iv : intervals) {
            if (res.isEmpty() || iv[0] > res.get(res.size() - 1)[1]) {
                res.add(iv);
            } else {
                res.get(res.size() - 1)[1] = Math.max(res.get(res.size() - 1)[1], iv[1]);
            }
        }
        return res.toArray(new int[res.size()][]);
    }
}""",
        'js_test_runner': """
      const testCases = [
        { iv: [[1,3],[2,6],[8,10],[15,18]], expected: [[1,6],[8,10],[15,18]] },
        { iv: [[1,4],[4,5]], expected: [[1,5]] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runMerge(arr) {
          let sorted = [...arr].sort((a, b) => a[0] - b[0]);
          let res = [];
          for (let iv of sorted) {
            if (res.length === 0 || iv[0] > res[res.length - 1][1]) {
              res.push([...iv]);
            } else {
              res[res.length - 1][1] = Math.max(res[res.length - 1][1], iv[1]);
            }
          }
          return res;
        }

        const out = runMerge(tc.iv);
        const passed = JSON.stringify(out) === JSON.stringify(tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: iv=[${tc.iv.map(x=>'['+x.join(',')+']').join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (' + JSON.stringify(out) + ')' : '&#10008; FAILED'}</span>
        `;
        resultsDiv.appendChild(row);
      });

      if (allPassed) {
        statusSpan.textContent = "All test cases passed!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "I clarify: Are intervals guaranteed to be sorted in the input? No, intervals may appear in arbitrary order. What if two intervals touch at a single point, like [1,4] and [4,5]? The problem states touching endpoints count as overlapping, so they merge into [1,5].",
        'phase2': "For strategy: By sorting intervals ascending by their start time `interval[0]`, we guarantee that if `curr[0] > last[1]`, no future interval can ever overlap with `last`. We iterate through the sorted list: if `curr[0] <= last[1]`, we merge by updating `last[1] = max(last[1], curr[1])`. Otherwise, we append `curr` to the result list. Time complexity is O(N log N) due to sorting, and space is O(N) for the output.",
        'phase3': "I implement `merge`: check if `intervals.size <= 1`. Sort intervals by `a[0]`. Initialize `result = mutableListOf<IntArray>()`. For each interval: if `result.isEmpty()` or `interval[0] > result.last()[1]`, append it. Else update `result.last()[1] = maxOf(result.last()[1], interval[1])`. Return `result.toTypedArray()`.",
        'phase4': "Dry run with [[1,3],[2,6],[8,10],[15,18]]: Sorted is same. [1,3] added. [2,6] overlaps (2 <= 3), extended to [1, max(3,6)] = [1,6]. [8,10] disjoint (8 > 6), added. [15,18] disjoint (15 > 10), added. Output is [[1,6],[8,10],[15,18]]. Edge case [[1,4],[4,5]] overlaps (4 <= 4) and merges to [[1,5]].",
        'senior_followup': "Senior Scalability: In distributed cluster computing (e.g. Apache Spark), if intervals are partitioned across 1,000 workers, each partition sorts and merges locally, then partition boundaries are merged globally using an interval tree or range-partitioned k-way merge."
    },
    'quiz': [
        {
            'question': "Why is sorting by START time essential for the greedy single-pass merge?",
            'options': [
                "It guarantees that once an interval starts strictly after the current merged interval ends (`curr[0] > last[1]`), no subsequent interval in the array can ever overlap with `last`.",
                "It converts 2D coordinates into 1D integers.",
                "It reduces the space complexity from O(N) to O(1).",
                "It ensures intervals are stored in contiguous CPU cache lines."
            ],
            'correct_index': 0,
            'explanation': "Because subsequent intervals have `start >= curr[0] > last[1]`, none of them can overlap with `last`. This allows us to finalize `last` permanently."
        },
        {
            'question': "When merging overlapping intervals `[a, b]` and `[c, d]` (where `c <= b`), why is the new end time `max(b, d)` instead of simply `d`?",
            'options': [
                "Because `d` could be smaller than `b` (e.g., `[1, 10]` and `[2, 5]`), where the second interval is completely engulfed inside the first.",
                "Because Java comparator requires symmetric max operations.",
                "Because intervals can have negative lengths.",
                "To prevent integer underflow."
            ],
            'correct_index': 0,
            'explanation': "If interval 1 completely encloses interval 2 (e.g. [1, 10] and [2, 5]), simply taking `d = 5` would mistakenly shrink the interval to [1, 5] instead of preserving [1, 10]."
        }
    ]
}

p47 = {
    'num': 47,
    'lc': 57,
    'title': "Insert Interval",
    'category': "Intervals & Greedy",
    'difficulty': "Medium",
    'lc_slug': "insert-interval",
    'verbatim_statement': """<p>You are given an array of non-overlapping intervals <code>intervals</code> where <code>intervals[i] = [start<sub>i</sub>, end<sub>i</sub>]</code> represent the start and the end of the <code>i<sup>th</sup></code> interval and <code>intervals</code> is sorted in ascending order by <code>start<sub>i</sub></code>. You are also given an interval <code>newInterval = [start, end]</code> that represents the start and end of another interval.</p><p>Insert <code>newInterval</code> into <code>intervals</code> such that <code>intervals</code> is still sorted in ascending order by <code>start<sub>i</sub></code> and <code>intervals</code> still does not have any overlapping intervals (merge overlapping intervals if necessary).</p><p>Return <code>intervals</code> <em>after the insertion</em>.</p><p><strong>Note</strong> that you don't need to modify <code>intervals</code> in-place. You can make a new array and return it.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'intervals = [[1,3],[6,9]], newInterval = [2,5]', 'output': '[[1,5],[6,9]]'},
        {'title': "Example 2", 'input': 'intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]', 'output': '[[1,2],[3,10],[12,16]]\nExplanation: Because the new interval [4,8] overlaps with [3,5],[6,7],[8,10].'}
    ],
    'constraints': [
        "0 <= intervals.length <= 10^4",
        "intervals[i].length == 2",
        "0 <= start_i <= end_i <= 10^5",
        "intervals is sorted by start_i in ascending order.",
        "newInterval.length == 2",
        "0 <= start <= end <= 10^5"
    ],
    'plain_english': "The original intervals are already sorted and disjoint. Solve in 3 sequential phases: 1) add all intervals that end before `newInterval` starts (`interval[1] < newInterval[0]`); 2) merge all intervals that overlap with `newInterval` by updating `newInterval = [min(start), max(end)]`; 3) add all remaining intervals that start after `newInterval` ends.",
    'boundary_checks': {
        'min_base': "<code>intervals = [], newInterval = [5, 7]</code> &rarr; Empty list returns <code>[[5, 7]]</code>.",
        'best_exit': "Inserted before first interval or after last interval &rarr; Inserted cleanly with zero merges.",
        'worst_scan': "Inserted interval engulfs all existing intervals <code>newInterval = [0, 100]</code> &rarr; All merged into a single block."
    },
    'mental_model': {
        'analogy_title': "The 3-Act Highway Construction Zone",
        'analogy_text': "Think of a highway with established paving zones already sorted from West to East. A new construction crew arrives with zone `newInterval`. Act 1: All paving zones completely West of the new zone are untouched and immediately approved. Act 2: Any paving zones that touch or overlap the new crew are merged together into one continuous mega-zone (`min(starts), max(ends)`). Act 3: All paving zones strictly East of the new zone are appended without alteration.",
        'aha_moment': "Because `intervals` is ALREADY sorted and non-overlapping, we do NOT need to sort! A 3-phase linear scan solves insertion and merging in strict O(N) time and O(N) space.",
        'trigger': "Inserting into pre-sorted disjoint intervals &rarr; 3-phase linear scan: Left Disjoint &rarr; Overlapping Merge &rarr; Right Disjoint.",
        'sisters': [
            {'name': "Merge Intervals", 'lc': "LC #56"},
            {'name': "Range Module", 'lc': "LC #715"},
            {'name': "Meeting Rooms II", 'lc': "LC #253"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N log N) Time, O(N) Space",
        'naive_text': "Append `newInterval` to `intervals`, sort the entire array by start time, and run LC 56 Merge Intervals.",
        'wasted_work': "Re-sorting an array that was already 99% sorted.",
        'optimal_complexity': "O(N) Time, O(N) Space",
        'optimal_text': "3-phase single pass: add strictly earlier intervals, merge overlapping intervals with `newInterval`, add strictly later intervals.",
        'core_invariant': "An interval overlaps with `newInterval` if and only if `interval[0] <= newInterval[1]` and `interval[1] >= newInterval[0]`.",
        'table_rows': [
            {'name': "1. Append + Full Re-Sort", 'time': "O(N log N)", 'space': "O(N)", 'note': "Disregards existing sorted property."},
            {'name': "2. Binary Search Insertion Point", 'time': "O(log N + N)", 'space': "O(N)", 'note': "Finds position in O(log N), but merging still requires O(N) shifts."},
            {'name': "★ 3. Three-Phase Linear Sweep", 'time': "O(N)", 'space': "O(N)", 'note': "Strict single-pass linear time; zero re-sorting."}
        ]
    },
    'trace_table': {
        'input_desc': 'intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]',
        'columns': ["Phase", "Examining Interval", "Evaluation Condition", "Action", "Active newInterval"],
        'rows': [
            ["Phase 1 (Before)", "[1, 2]", "2 < 4 (ends before newInterval starts)", "Add [1, 2] to result", "[4, 8]"],
            ["Phase 2 (Overlap)", "[3, 5]", "3 <= 8 (overlaps!)", "Merge: [min(4,3), max(8,5)] = [3, 8]", "[3, 8]"],
            ["Phase 2 (Overlap)", "[6, 7]", "6 <= 8 (overlaps!)", "Merge: [min(3,6), max(8,7)] = [3, 8]", "[3, 8]"],
            ["Phase 2 (Overlap)", "[8, 10]", "8 <= 8 (overlaps!)", "Merge: [min(3,8), max(8,10)] = [3, 10]", "[3, 10]"],
            ["Phase 2 Commit", "-", "12 <= 10 (False, overlap ends)", "Commit [3, 10] to result", "[3, 10]"],
            ["Phase 3 (After)", "[12, 16]", "Remaining after overlap", "Add [12, 16] to result", "[3, 10]"]
        ],
        'takeaway': "Result is [[1,2], [3,10], [12,16]]. Single pass processed in O(N) without sorting."
    },
    'stepper': {
        'title': "3-Phase Interval Insertion Stepper",
        'default_input': '[[1,2],[3,5],[6,7],[8,10],[12,16]], [4,8]',
        'presets': [
            {'label': "Example [[1,2],[3,5],[6,7],[8,10],[12,16]], [4,8]", 'onclick': 'loadPreset([[1,2],[3,5],[6,7],[8,10],[12,16]], [4,8])'},
            {'label': "No Overlap (Insert at start)", 'onclick': 'loadPreset([[3,5],[6,9]], [1,2])'},
            {'label': "Engulf All [[2,3],[4,5]], [1,10]", 'onclick': 'loadPreset([[2,3],[4,5]], [1,10])'}
        ],
        'metrics': [
            {'id': "metric-phase", 'label': "Current Phase", 'color': "text-amber-400", 'initial': "Phase 1"},
            {'id': "metric-exam", 'label': "Examining Interval", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-new", 'label': "newInterval State", 'color': "text-emerald-400", 'initial': "-"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="insert-stage" class="w-full flex flex-col items-center justify-center gap-3 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace 3-phase insertion.',
        'pseudocode_lines': [
            "var i = 0; val n = intervals.size",
            "// Phase 1: Add all intervals ending before newInterval starts",
            "while (i < n && intervals[i][1] < newInterval[0]) result.add(intervals[i++])",
            "// Phase 2: Merge all overlapping intervals",
            "while (i < n && intervals[i][0] <= newInterval[1]) {",
            "    newInterval[0] = minOf(newInterval[0], intervals[i][0])",
            "    newInterval[1] = maxOf(newInterval[1], intervals[i][1])",
            "    i++",
            "}",
            "result.add(newInterval)",
            "// Phase 3: Add all remaining intervals",
            "while (i < n) result.add(intervals[i++])"
        ],
        'js_code': """
    let baseIntervals = [[1,2],[3,5],[6,7],[8,10],[12,16]];
    let insInterval = [4,8];

    function buildSteps(arr, ins) {
      steps = [];
      let res = [];
      let currNew = [...ins];
      let i = 0;
      let n = arr.length;

      // Phase 1
      while (i < n && arr[i][1] < currNew[0]) {
        res.push([...arr[i]]);
        steps.push({
          phase: "Phase 1: Before", exam: `[${arr[i][0]}, ${arr[i][1]}]`,
          newVal: `[${currNew[0]}, ${currNew[1]}]`,
          action: `Added strictly earlier [${arr[i][0]}, ${arr[i][1]}]`,
          res: JSON.parse(JSON.stringify(res)), line: 3,
          narration: `Interval [${arr[i][0]}, ${arr[i][1]}] ends before ${currNew[0]}. Added directly.`
        });
        i++;
      }

      // Phase 2
      while (i < n && arr[i][0] <= currNew[1]) {
        currNew[0] = Math.min(currNew[0], arr[i][0]);
        currNew[1] = Math.max(currNew[1], arr[i][1]);
        steps.push({
          phase: "Phase 2: Overlap", exam: `[${arr[i][0]}, ${arr[i][1]}]`,
          newVal: `[${currNew[0]}, ${currNew[1]}]`,
          action: `Merged &rarr; [${currNew[0]}, ${currNew[1]}]`,
          res: JSON.parse(JSON.stringify(res)), line: 6,
          narration: `Interval [${arr[i][0]}, ${arr[i][1]}] overlaps! Expanded newInterval to [${currNew[0]}, ${currNew[1]}].`
        });
        i++;
      }

      res.push([...currNew]);
      steps.push({
        phase: "Phase 2: Commit", exam: "-",
        newVal: `[${currNew[0]}, ${currNew[1]}]`,
        action: `Committed merged [${currNew[0]}, ${currNew[1]}]`,
        res: JSON.parse(JSON.stringify(res)), line: 10,
        narration: `Overlap phase complete. Committed merged block [${currNew[0]}, ${currNew[1]}].`
      });

      // Phase 3
      while (i < n) {
        res.push([...arr[i]]);
        steps.push({
          phase: "Phase 3: After", exam: `[${arr[i][0]}, ${arr[i][1]}]`,
          newVal: "-",
          action: `Added strictly later [${arr[i][0]}, ${arr[i][1]}]`,
          res: JSON.parse(JSON.stringify(res)), line: 12,
          narration: `Interval [${arr[i][0]}, ${arr[i][1]}] starts after merged block ends. Added directly.`
        });
        i++;
      }
    }

    function loadPreset(arr, ins) {
      document.getElementById('custom-input').value = arr.map(x => `[${x[0]},${x[1]}]`).join(', ') + ' | [' + ins.join(',') + ']';
      baseIntervals = arr;
      insInterval = ins;
      stepperReset();
      buildSteps(baseIntervals, insInterval);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const parts = val.split('|');
        const arr = JSON.parse('[' + parts[0].trim().replace(/\\[/g, '[').replace(/\\]/g, ']') + ']');
        const ins = JSON.parse(parts[1].trim());
        baseIntervals = arr;
        insInterval = ins;
        stepperReset();
        buildSteps(baseIntervals, insInterval);
        renderStepper();
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-phase').textContent = step.phase;
      document.getElementById('metric-exam').textContent = step.exam;
      document.getElementById('metric-new').textContent = step.newVal;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('insert-stage');
      stage.innerHTML = '';
      if (!step.res) return;

      const wrap = document.createElement('div');
      wrap.className = 'w-full flex flex-col gap-2';

      const rowLabel = document.createElement('div');
      rowLabel.className = 'text-xs font-mono text-slate-400 font-bold';
      rowLabel.textContent = 'Current Result List:';
      wrap.appendChild(rowLabel);

      const blockRow = document.createElement('div');
      blockRow.className = 'flex flex-wrap items-center gap-2';
      step.res.forEach((iv, idx) => {
        const item = document.createElement('div');
        item.className = 'px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-mono text-xs font-bold';
        item.textContent = `[${iv[0]}, ${iv[1]}]`;
        blockRow.appendChild(item);
      });
      wrap.appendChild(blockRow);

      stage.appendChild(wrap);
"""
    },
    'approaches': [
        {
            'name': "Append & Full Re-Sort (Naive)",
            'desc': "Append newInterval to intervals, sort the entire list by start time, and run standard merge intervals.",
            'time': "O(N log N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
        val list = intervals.toMutableList().apply { add(newInterval) }
        list.sortBy { it[0] }
        val result = mutableListOf<IntArray>()
        for (iv in list) {
            if (result.isEmpty() || iv[0] > result.last()[1]) {
                result.add(iv)
            } else {
                result.last()[1] = maxOf(result.last()[1], iv[1])
            }
        }
        return result.toTypedArray()
    }
}""",
            'notes_kotlin': "Appends and re-sorts. Discards the valuable pre-sorted property of the input.",
            'code_python': """class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        intervals.append(newInterval)
        intervals.sort(key=lambda x: x[0])
        res = []
        for iv in intervals:
            if not res or iv[0] > res[-1][1]:
                res.append(iv)
            else:
                res[-1][1] = max(res[-1][1], iv[1])
        return res""",
            'notes_python': "Easy to write, but interviewer will follow up: 'Can you do it in linear O(N) time?'.",
            'code_java': """class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        int[][] list = new int[intervals.length + 1][2];
        System.arraycopy(intervals, 0, list, 0, intervals.length);
        list[intervals.length] = newInterval;
        Arrays.sort(list, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> result = new ArrayList<>();
        for (int[] iv : list) {
            if (result.isEmpty() || iv[0] > result.get(result.size() - 1)[1]) {
                result.add(iv);
            } else {
                result.get(result.size() - 1)[1] = Math.max(result.get(result.size() - 1)[1], iv[1]);
            }
        }
        return result.toArray(new int[result.size()][]);
    }
}""",
            'notes_java': "Sorting incurs unnecessary O(N log N) overhead."
        },
        {
            'name': "Binary Search Insert Position + Merge",
            'desc': "Use binary search to locate the insertion index of newInterval in O(log N), then merge overlapping neighbors.",
            'time': "O(log N + N) = O(N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
        val n = intervals.size
        var left = 0
        var right = n - 1
        var insertIdx = n
        while (left <= right) {
            val mid = (left + right) / 2
            if (intervals[mid][0] >= newInterval[0]) {
                insertIdx = mid
                right = mid - 1
            } else {
                left = mid + 1
            }
        }
        val list = ArrayList<IntArray>(n + 1)
        for (i in 0 until insertIdx) list.add(intervals[i])
        list.add(newInterval)
        for (i in insertIdx until n) list.add(intervals[i])

        val result = mutableListOf<IntArray>()
        for (iv in list) {
            if (result.isEmpty() || iv[0] > result.last()[1]) {
                result.add(iv)
            } else {
                result.last()[1] = maxOf(result.last()[1], iv[1])
            }
        }
        return result.toTypedArray()
    }
}""",
            'notes_kotlin': "Binary search quickly finds index, but array creation and merging still take O(N).",
            'code_python': """class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        import bisect
        idx = bisect.bisect_left(intervals, newInterval[0], key=lambda x: x[0])
        intervals.insert(idx, newInterval)
        res = []
        for iv in intervals:
            if not res or iv[0] > res[-1][1]:
                res.append(iv)
            else:
                res[-1][1] = max(res[-1][1], iv[1])
        return res""",
            'notes_python': "Uses `bisect` for insertion position.",
            'code_java': """class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        int n = intervals.length;
        int low = 0, high = n - 1, idx = n;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (intervals[mid][0] >= newInterval[0]) {
                idx = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < idx; i++) list.add(intervals[i]);
        list.add(newInterval);
        for (int i = idx; i < n; i++) list.add(intervals[i]);

        List<int[]> result = new ArrayList<>();
        for (int[] iv : list) {
            if (result.isEmpty() || iv[0] > result.get(result.size() - 1)[1]) {
                result.add(iv);
            } else {
                result.get(result.size() - 1)[1] = Math.max(result.get(result.size() - 1)[1], iv[1]);
            }
        }
        return result.toArray(new int[result.size()][]);
    }
}""",
            'notes_java': "Binary search optimization does not reduce the asymptotic complexity beyond O(N)."
        },
        {
            'name': "Three-Phase Linear Scan (Optimal Primary)",
            'desc': "Add all intervals ending before newInterval starts. Merge overlapping intervals. Add remaining intervals.",
            'time': "O(N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Three-Phase Linear Scan
     * Time: O(N) - Strictly single pass through intervals
     * Space: O(N) - Space for result list
     */
    fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
        val result = mutableListOf<IntArray>()
        var i = 0
        val n = intervals.size
        var start = newInterval[0]
        var end = newInterval[1]

        // Phase 1: Add all intervals ending strictly before newInterval starts
        while (i < n && intervals[i][1] < start) {
            result.add(intervals[i])
            i++
        }

        // Phase 2: Merge all overlapping intervals
        while (i < n && intervals[i][0] <= end) {
            start = minOf(start, intervals[i][0])
            end = maxOf(end, intervals[i][1])
            i++
        }
        result.add(intArrayOf(start, end))

        // Phase 3: Add all remaining intervals starting after newInterval ends
        while (i < n) {
            result.add(intervals[i])
            i++
        }

        return result.toTypedArray()
    }
}""",
            'notes_kotlin': "Zero sorting. Direct single pass through the pre-sorted input array.",
            'code_python': """class Solution:
    # Optimal: Three-Phase Linear Scan
    # Time: O(N) - Single pass
    # Space: O(N) - Output list
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        result = []
        i = 0
        n = len(intervals)
        start, end = newInterval[0], newInterval[1]

        # Phase 1: Add intervals strictly before
        while i < n and intervals[i][1] < start:
            result.append(intervals[i])
            i += 1

        # Phase 2: Merge overlapping intervals
        while i < n and intervals[i][0] <= end:
            start = min(start, intervals[i][0])
            end = max(end, intervals[i][1])
            i += 1
        result.append([start, end])

        # Phase 3: Add intervals strictly after
        while i < n:
            result.append(intervals[i])
            i += 1

        return result""",
            'notes_python': "Masterclass interview solution. Clean division of 3 sequential while-loops.",
            'code_java': """class Solution {
    /**
     * Optimal: Three-Phase Linear Scan
     * Time: O(N) - Single pass
     * Space: O(N) - Output array
     */
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;
        int n = intervals.length;
        int start = newInterval[0];
        int end = newInterval[1];

        // Phase 1: Non-overlapping before
        while (i < n && intervals[i][1] < start) {
            result.add(intervals[i]);
            i++;
        }

        // Phase 2: Overlapping intervals merged
        while (i < n && intervals[i][0] <= end) {
            start = Math.min(start, intervals[i][0]);
            end = Math.max(end, intervals[i][1]);
            i++;
        }
        result.add(new int[]{start, end});

        // Phase 3: Non-overlapping after
        while (i < n) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }
}""",
            'notes_java': "Optimal O(N) time with no sorting. Guarantees non-overlapping sorted output."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
        val res = mutableListOf<IntArray>()
        var i = 0; val n = intervals.size
        var s = newInterval[0]; var e = newInterval[1]
        while (i < n && intervals[i][1] < s) res.add(intervals[i++])
        while (i < n && intervals[i][0] <= e) {
            s = minOf(s, intervals[i][0])
            e = maxOf(e, intervals[i][1])
            i++
        }
        res.add(intArrayOf(s, e))
        while (i < n) res.add(intervals[i++])
        return res.toTypedArray()
    }
}""",
        'python_boilerplate': """class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        res = []
        i, n = 0, len(intervals)
        s, e = newInterval[0], newInterval[1]
        while i < n and intervals[i][1] < s:
            res.append(intervals[i]); i += 1
        while i < n and intervals[i][0] <= e:
            s = min(s, intervals[i][0])
            e = max(e, intervals[i][1])
            i += 1
        res.append([s, e])
        while i < n:
            res.append(intervals[i]); i += 1
        return res""",
        'java_boilerplate': """class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> res = new ArrayList<>();
        int i = 0, n = intervals.length;
        int s = newInterval[0], e = newInterval[1];
        while (i < n && intervals[i][1] < s) res.add(intervals[i++]);
        while (i < n && intervals[i][0] <= e) {
            s = Math.min(s, intervals[i][0]);
            e = Math.max(e, intervals[i][1]);
            i++;
        }
        res.add(new int[]{s, e});
        while (i < n) res.add(intervals[i++]);
        return res.toArray(new int[res.size()][]);
    }
}""",
        'js_test_runner': """
      const testCases = [
        { iv: [[1,3],[6,9]], ins: [2,5], expected: [[1,5],[6,9]] },
        { iv: [[1,2],[3,5],[6,7],[8,10],[12,16]], ins: [4,8], expected: [[1,2],[3,10],[12,16]] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runInsert(arr, ins) {
          let res = [];
          let i = 0, n = arr.length;
          let s = ins[0], e = ins[1];
          while (i < n && arr[i][1] < s) res.push(arr[i++]);
          while (i < n && arr[i][0] <= e) {
            s = Math.min(s, arr[i][0]);
            e = Math.max(e, arr[i][1]);
            i++;
          }
          res.push([s, e]);
          while (i < n) res.push(arr[i++]);
          return res;
        }

        const out = runInsert(tc.iv, tc.ins);
        const passed = JSON.stringify(out) === JSON.stringify(tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: ins=[${tc.ins.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (' + JSON.stringify(out) + ')' : '&#10008; FAILED'}</span>
        `;
        resultsDiv.appendChild(row);
      });

      if (allPassed) {
        statusSpan.textContent = "All test cases passed!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "I clarify: Is the input `intervals` array already sorted and guaranteed non-overlapping? Yes, constraints state intervals is sorted by start time and non-overlapping. Can intervals be empty? Yes, if empty, return a list containing only newInterval.",
        'phase2': "For strategy: Since the original intervals are already sorted and disjoint, we do NOT need to sort! We can solve this with a 3-phase linear scan in O(N) time and O(N) space: Phase 1 adds all intervals ending strictly before newInterval starts. Phase 2 merges all overlapping intervals (`interval[0] <= newInterval[1]`) by taking min of starts and max of ends. Phase 3 appends all remaining intervals.",
        'phase3': "I implement `insert`: initialize `i = 0`, `start = newInterval[0]`, `end = newInterval[1]`. While `i < n && intervals[i][1] < start`, add `intervals[i++]`. Then while `i < n && intervals[i][0] <= end`, expand `start = min(start, intervals[i][0])` and `end = max(end, intervals[i][1])`, incrementing `i`. Commit `[start, end]`. Finally, while `i < n`, add `intervals[i++]`.",
        'phase4': "Dry run with intervals=[[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval=[4,8]: Phase 1 adds [1,2] (2 < 4). Phase 2 detects overlap with [3,5] (start becomes 3, end 8), [6,7] (end stays 8), [8,10] (end becomes 10). Stops at [12,16] (12 > 10). Commits [3,10]. Phase 3 adds [12,16]. Result is [[1,2],[3,10],[12,16]]. Clean linear execution.",
        'senior_followup': "Senior Scalability: If intervals are dynamically inserted and queried millions of times per second (e.g. database memory segment locking), we maintain an augmented Red-Black Interval Tree (like Segment Tree), supporting insertions and range overlap checks in O(log N) time instead of O(N)."
    },
    'quiz': [
        {
            'question': "Why is the overlap check in Phase 2 `intervals[i][0] <= end` rather than `< end`?",
            'options': [
                "Because intervals that touch at an endpoint (e.g. `[1, 4]` and `[4, 5]`) are considered overlapping per problem specification and must be merged.",
                "Because array indices are 0-based.",
                "Because intervals can have negative coordinates.",
                "It avoids null pointer exceptions."
            ],
            'correct_index': 0,
            'explanation': "The problem specifies that touching intervals (e.g. `[1, 4]` and `[4, 5]`) overlap at point 4 and must merge into `[1, 5]`. Hence `<=` is strictly required."
        },
        {
            'question': "Why does the 3-phase linear scan run in O(N) time rather than O(N log N)?",
            'options': [
                "Because the input intervals array is ALREADY sorted and non-overlapping, allowing us to find the overlap zone in a single forward pass without re-sorting.",
                "Because it uses binary search.",
                "Because integer division runs in O(1).",
                "Because intervals contains at most 2 elements."
            ],
            'correct_index': 0,
            'explanation': "Leveraging the pre-sorted invariant allows a single forward pointer `i` to traverse from 0 to N-1 exactly once, yielding strict $O(N)$ linear time."
        }
    ]
}
