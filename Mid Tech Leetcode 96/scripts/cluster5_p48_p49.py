import sys
sys.path.append('scripts')

p48 = {
    'num': 48,
    'lc': 78,
    'title': "Subsets",
    'category': "Backtracking",
    'difficulty': "Medium",
    'lc_slug': "subsets",
    'verbatim_statement': """<p>Given an integer array <code>nums</code> of <strong>unique</strong> elements, return <em>all possible</em> <span data-keyword="subset"><em>subsets</em></span> <em>(the power set)</em>.</p><p>The solution set <strong>must not</strong> contain duplicate subsets. Return the solution in <strong>any order</strong>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [1,2,3]', 'output': '[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]'},
        {'title': "Example 2", 'input': 'nums = [0]', 'output': '[[],[0]]'}
    ],
    'constraints': [
        "1 <= nums.length <= 10",
        "-10 <= nums[i] <= 10",
        "All the numbers of nums are unique."
    ],
    'plain_english': "Generate the power set of 2^N subsets. At each step in recursive backtracking, commit a copy of the current combination to the result list. Then loop through remaining elements from index `start` to `nums.length - 1`, append `nums[i]`, recurse on `i + 1`, and backtrack by popping the last element.",
    'boundary_checks': {
        'min_base': "Single element <code>nums = [0]</code> &rarr; Returns <code>[[], [0]]</code> (2^1 = 2 subsets).",
        'best_exit': "Empty subset <code>[]</code> is always included as the base element of the power set.",
        'worst_scan': "Maximum constraint N = 10 &rarr; 2^10 = 1,024 subsets; executes instantaneously."
    },
    'mental_model': {
        'analogy_title': "The Decision Switchboard (Include / Exclude)",
        'analogy_text': "Imagine a row of N toggle switches, one for each element. For each switch, you have two choices: leave it OFF (exclude the element) or flip it ON (include the element). Because every switch independently has 2 states, N switches produce 2 * 2 * ... * 2 = 2^N distinct configurations. Every single state of the switches is a valid subset.",
        'aha_moment': "Unlike permutation problems where you only save states at leaf nodes (`path.size == N`), in Subsets EVERY node in the recursion tree represents a valid distinct subset! We append a snapshot of `path` at the start of every single recursive call.",
        'trigger': "Generate all combinations, power set, all subsets &rarr; Backtracking with `start` index or Binary Bitmask (0 to 2^N - 1).",
        'sisters': [
            {'name': "Subsets II (with Duplicates)", 'lc': "LC #90"},
            {'name': "Combination Sum", 'lc': "LC #39"},
            {'name': "Permutations", 'lc': "LC #46"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N * 2^N) Time, O(N * 2^N) Space",
        'naive_text': "Cascading iterative approach: start with `[[]]`. For each number, duplicate all existing subsets and append the current number.",
        'wasted_work': "Allocating many intermediate array copies in iterative loops.",
        'optimal_complexity': "O(N * 2^N) Time, O(N) Call Stack",
        'optimal_text': "Recursive backtracking with `start` pointer and a single reusable mutable list. Pops in O(1) on backtrack.",
        'core_invariant': "At recursion depth d, `path` contains a subset chosen strictly from elements before index `start`.",
        'table_rows': [
            {'name': "1. Cascading Iterative Generation", 'time': "O(N * 2^N)", 'space': "O(N * 2^N)", 'note': "Duplicates previous subsets for each new element."},
            {'name': "2. Binary Bitmasking (0 to 2^N - 1)", 'time': "O(N * 2^N)", 'space': "O(1) extra", 'note': "Uses i-th bit to decide inclusion of nums[i]."},
            {'name': "★ 3. Backtracking with Start Index", 'time': "O(N * 2^N)", 'space': "O(N) Call Stack", 'note': "Canonical interview pattern; reusable path buffer."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [1, 2, 3]',
        'columns': ["Call Depth", "start Index", "Current path Snapshot", "Action / Node Explored", "Result Count"],
        'rows': [
            ["0", "0", "[]", "Commit [] to result", "1 ([[]])"],
            ["1", "1", "[1]", "Loop i=0: pick 1 &rarr; Commit [1]", "2 ([[], [1]])"],
            ["2", "2", "[1, 2]", "Loop i=1: pick 2 &rarr; Commit [1, 2]", "3 ([[], [1], [1,2]])"],
            ["3", "3", "[1, 2, 3]", "Loop i=2: pick 3 &rarr; Commit [1, 2, 3]", "4"],
            ["Backtrack", "2", "[1, 2]", "Pop 3 &rarr; loop i=3 ends &rarr; return", "4"],
            ["Backtrack", "1", "[1]", "Pop 2 &rarr; loop i=2: pick 3 &rarr; [1, 3]", "5 ([..., [1,3]])"],
            ["Backtrack", "0", "[]", "Pop 1 &rarr; loop i=1: pick 2 &rarr; [2]", "6 ([..., [2]])"]
        ],
        'takeaway': "Total recursive nodes visited = 2^3 = 8. Reusable `path` adds elements and pops them back in O(1)."
    },
    'stepper': {
        'title': "Backtracking Decision Tree Stepper",
        'default_input': '1, 2, 3',
        'presets': [
            {'label': "Example [1, 2, 3]", 'onclick': 'loadPreset([1, 2, 3])'},
            {'label': "Two Items [1, 2]", 'onclick': 'loadPreset([1, 2])'},
            {'label': "Single Item [0]", 'onclick': 'loadPreset([0])'}
        ],
        'metrics': [
            {'id': "metric-start", 'label': "Start Index", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-path", 'label': "Current Path", 'color': "text-cyan-400", 'initial': "[]"},
            {'id': "metric-total", 'label': "Subsets Found", 'color': "text-emerald-400", 'initial': "0"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="subsets-stage" class="w-full flex flex-col items-center justify-center gap-3 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace recursive subset formation and backtracking.',
        'pseudocode_lines': [
            "fun backtrack(start: Int, path: MutableList<Int>) {",
            "    result.add(ArrayList(path)) // Every node is a valid subset",
            "    for (i in start until nums.size) {",
            "        path.add(nums[i])       // Choose",
            "        backtrack(i + 1, path)  // Explore",
            "        path.removeAt(path.size - 1) // Backtrack (Un-choose)",
            "    }",
            "}"
        ],
        'js_code': """
    let rawNums = [1, 2, 3];

    function buildSteps(arr) {
      steps = [];
      let res = [];
      let path = [];

      function backtrack(start) {
        res.push([...path]);
        steps.push({
          start: start, path: `[${path.join(', ')}]`,
          total: res.length, action: `Commit [${path.join(', ')}]`,
          res: JSON.parse(JSON.stringify(res)), line: 2,
          narration: `Committed current path [${path.join(', ')}] as subset #${res.length}.`
        });

        for (let i = start; i < arr.length; i++) {
          path.push(arr[i]);
          steps.push({
            start: i + 1, path: `[${path.join(', ')}]`,
            total: res.length, action: `Pushed ${arr[i]} (Choose)`,
            res: JSON.parse(JSON.stringify(res)), line: 4,
            narration: `Chose nums[${i}] = ${arr[i]}. Recursing to start = ${i + 1}.`
          });

          backtrack(i + 1);

          let popped = path.pop();
          steps.push({
            start: start, path: `[${path.join(', ')}]`,
            total: res.length, action: `Popped ${popped} (Backtrack)`,
            res: JSON.parse(JSON.stringify(res)), line: 6,
            narration: `Backtracked: removed ${popped} from path. Restored path to [${path.join(', ')}].`
          });
        }
      }

      backtrack(0);

      steps.push({
        start: "-", path: "Done", total: res.length,
        action: "All 2^N Generated", res: JSON.parse(JSON.stringify(res)), line: 1,
        narration: `Completed power set! Generated all 2^${arr.length} = ${res.length} subsets.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      rawNums = arr;
      stepperReset();
      buildSteps(rawNums);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        rawNums = arr;
        stepperReset();
        buildSteps(rawNums);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-start').textContent = step.start;
      document.getElementById('metric-path').textContent = step.path;
      document.getElementById('metric-total').textContent = step.total;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('subsets-stage');
      stage.innerHTML = '';
      if (!step.res) return;

      const wrap = document.createElement('div');
      wrap.className = 'w-full flex flex-col gap-2';

      const label = document.createElement('div');
      label.className = 'text-xs font-mono text-slate-400 font-bold';
      label.textContent = `Generated Subsets (${step.res.length} / 2^N):`;
      wrap.appendChild(label);

      const grid = document.createElement('div');
      grid.className = 'flex flex-wrap gap-2 max-h-40 overflow-y-auto p-2 bg-slate-950 border border-slate-800 rounded-xl';
      step.res.forEach((s, idx) => {
        const item = document.createElement('span');
        item.className = 'px-2.5 py-1 rounded bg-slate-900 border border-slate-700 text-slate-300 font-mono text-xs';
        item.textContent = `[${s.join(', ')}]`;
        grid.appendChild(item);
      });
      wrap.appendChild(grid);

      stage.appendChild(wrap);
"""
    },
    'approaches': [
        {
            'name': "Cascading Iterative Generation (Naive)",
            'desc': "Start with `[[]]`. For each number in nums, duplicate all existing subsets and append the current number.",
            'time': "O(N * 2^N)",
            'space': "O(N * 2^N)",
            'code_kotlin': """class Solution {
    fun subsets(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>(emptyList())
        for (num in nums) {
            val newSubsets = mutableListOf<List<Int>>()
            for (curr in result) {
                newSubsets.add(ArrayList(curr).apply { add(num) })
            }
            result.addAll(newSubsets)
        }
        return result
    }
}""",
            'notes_kotlin': "Iterative cascading generation. Allocates many temporary lists in every outer loop iteration.",
            'code_python': """class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        res = [[]]
        for num in nums:
            res += [curr + [num] for curr in res]
        return res""",
            'notes_python': "Concise Python list comprehension. Doubles the result list size on each element.",
            'code_java': """class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        for (int num : nums) {
            int n = result.size();
            for (int i = 0; i < n; i++) {
                List<Integer> set = new ArrayList<>(result.get(i));
                set.add(num);
                result.add(set);
            }
        }
        return result;
    }
}""",
            'notes_java': "Iterative duplication. Easy to write, but creates high object allocation pressure."
        },
        {
            'name': "Bit Manipulation / Binary Masks",
            'desc': "Iterate from 0 to 2^N - 1. Treat each integer as a bitmask where the i-th bit indicates inclusion of nums[i].",
            'time': "O(N * 2^N)",
            'space': "O(1) Auxiliary",
            'code_kotlin': """class Solution {
    fun subsets(nums: IntArray): List<List<Int>> {
        val n = nums.size
        val total = 1 shl n
        val result = ArrayList<List<Int>>(total)

        for (mask in 0 until total) {
            val subset = mutableListOf<Int>()
            for (i in 0 until n) {
                if ((mask and (1 shl i)) != 0) {
                    subset.add(nums[i])
                }
            }
            result.add(subset)
        }
        return result
    }
}""",
            'notes_kotlin': "Bitmask integer representation. Ideal for low-level systems and bitwise operations.",
            'code_python': """class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        n = len(nums)
        res = []
        for mask in range(1 << n):
            subset = [nums[i] for i in range(n) if (mask & (1 << i))]
            res.append(subset)
        return res""",
            'notes_python': "Clean bitwise formulation. Strictly evaluates $2^N$ binary states.",
            'code_java': """class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        int n = nums.length;
        int total = 1 << n;
        List<List<Integer>> result = new ArrayList<>(total);

        for (int mask = 0; mask < total; mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(nums[i]);
                }
            }
            result.add(subset);
        }
        return result;
    }
}""",
            'notes_java': "Uses primitive bit shifts `1 << n`. No recursion stack."
        },
        {
            'name': "Backtracking with Start Index (Optimal Primary)",
            'desc': "Depth-First Search passing `start` index with Choose-Explore-Unchoose pattern. Uses a single reusable buffer.",
            'time': "O(N * 2^N)",
            'space': "O(N) Call Stack",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Backtracking with Start Index
     * Time: O(N * 2^N) - 2^N subsets, each taking O(N) to copy into result
     * Space: O(N) - Recursion stack and path buffer of depth N
     */
    fun subsets(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()

        fun backtrack(start: Int) {
            // Snapshot current path into result
            result.add(ArrayList(path))

            for (i in start until nums.size) {
                path.add(nums[i])        // Choose
                backtrack(i + 1)         // Explore
                path.removeAt(path.size - 1) // Un-choose (Backtrack)
            }
        }

        backtrack(0)
        return result
    }
}""",
            'notes_kotlin': "Standard backtracking template. Extends smoothly to Subsets II (handling duplicates) and Combination Sum.",
            'code_python': """class Solution:
    # Optimal: Backtracking with Start Index
    # Time: O(N * 2^N)
    # Space: O(N) - Recursion stack
    def subsets(self, nums: List[int]) -> List[List[int]]:
        result = []
        path = []

        def backtrack(start: int):
            result.append(list(path))
            for i in range(start, len(nums)):
                path.append(nums[i])     # Choose
                backtrack(i + 1)          # Explore
                path.pop()                # Backtrack

        backtrack(0)
        return result""",
            'notes_python': "The canonical Python backtracking template. Highly readable and standard.",
            'code_java': """class Solution {
    /**
     * Optimal: Backtracking with Start Index
     * Time: O(N * 2^N) - Exactly 2^N states explored
     * Space: O(N) - Reusable path list + recursion stack
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        backtrack(0, nums, path, result);
        return result;
    }

    private void backtrack(int start, int[] nums, List<Integer> path, List<List<Integer>> result) {
        result.add(new ArrayList<>(path));

        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);            // Choose
            backtrack(i + 1, nums, path, result); // Explore
            path.remove(path.size() - 1); // Un-choose
        }
    }
}""",
            'notes_java': "Reusable `path` buffer avoids intermediate allocations. Backtracks with O(1) removals."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun subsets(nums: IntArray): List<List<Int>> {
        val res = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()
        fun dfs(start: Int) {
            res.add(ArrayList(path))
            for (i in start until nums.size) {
                path.add(nums[i])
                dfs(i + 1)
                path.removeAt(path.size - 1)
            }
        }
        dfs(0)
        return res
    }
}""",
        'python_boilerplate': """class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        res, path = [], []
        def dfs(start):
            res.append(list(path))
            for i in range(start, len(nums)):
                path.append(nums[i])
                dfs(i + 1)
                path.pop()
        dfs(0)
        return res""",
        'java_boilerplate': """class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        dfs(0, nums, path, res);
        return res;
    }
    private void dfs(int s, int[] nums, List<Integer> path, List<List<Integer>> res) {
        res.add(new ArrayList<>(path));
        for (int i = s; i < nums.length; i++) {
            path.add(nums[i]);
            dfs(i + 1, nums, path, res);
            path.remove(path.size() - 1);
        }
    }
}""",
        'js_test_runner': """
      const testCases = [
        { nums: [1,2,3], count: 8 },
        { nums: [0], count: 2 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runSubsets(arr) {
          let res = [], path = [];
          function dfs(s) {
            res.push([...path]);
            for (let i = s; i < arr.length; i++) {
              path.push(arr[i]);
              dfs(i + 1);
              path.pop();
            }
          }
          dfs(0);
          return res;
        }

        const out = runSubsets(tc.nums);
        const passed = (out.length === tc.count);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: nums=[${tc.nums.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (' + out.length + ' subsets)' : '&#10008; FAILED'}</span>
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
        'phase1': "I clarify: Does the input array contain duplicates? The problem guarantees all elements are unique. Does subset ordering matter? Return in any order. What is the maximum size of nums? Up to 10 elements, meaning 2^10 = 1,024 subsets.",
        'phase2': "For strategy: Every element has 2 choices: include or exclude. A backtracking DFS passing a `start` index explores the decision space systematically. At every step, we snapshot `ArrayList(path)` into the result list because every prefix in the tree is a valid subset. We then loop `i` from `start` to `nums.size - 1`, append `nums[i]`, recurse on `i + 1`, and pop the element to backtrack. Time is O(N * 2^N) and stack space is O(N).",
        'phase3': "I implement `subsets`: create `result = mutableListOf<List<Int>>()` and reusable buffer `path = mutableListOf<Int>()`. Inside helper `backtrack(start)`: add `ArrayList(path)` to result. Loop `for (i in start until nums.size)`: `path.add(nums[i])`, call `backtrack(i + 1)`, then `path.removeAt(path.size - 1)`. Kick off with `backtrack(0)`.",
        'phase4': "Dry run with [1, 2]: backtrack(0) adds []. i=0 picks 1 &rarr; backtrack(1) adds [1]. i=1 picks 2 &rarr; backtrack(2) adds [1,2]. Backtracks out of 2 and 1. i=1 picks 2 &rarr; backtrack(2) adds [2]. Total 4 subsets: [], [1], [1,2], [2]. Exactly matches 2^2.",
        'senior_followup': "Senior Scalability: When N grows beyond 30, generating all 2^N subsets is physically impossible (2^30 > 1 billion). In production optimization problems (like Knapsack or subset sum), engineers apply Branch and Bound pruning, dynamic programming bitmasks, or genetic algorithms to evaluate only viable candidate subsets."
    },
    'quiz': [
        {
            'question': "Why is `result.add(ArrayList(path))` executed at the very beginning of every recursive call in Subsets, rather than only at a leaf condition?",
            'options': [
                "Because every state and prefix in the decision tree represents a distinct, valid subset of the power set.",
                "To prevent integer stack overflow.",
                "Because binary trees always have 2^N leaves.",
                "It enables parallel GPU processing."
            ],
            'correct_index': 0,
            'explanation': "Unlike permutations where only full-length paths of size N are valid solutions, any subset of any size from 0 to N is a valid member of the power set."
        },
        {
            'question': "Why do we pass `i + 1` (rather than `start + 1`) to the recursive call inside the loop?",
            'options': [
                "Passing `i + 1` ensures that the next element chosen is strictly to the right of the current element, avoiding duplicates like `[2, 1]` when `[1, 2]` was already generated.",
                "Passing `start + 1` causes infinite recursion.",
                "Because Java does not support variable mutation.",
                "To skip negative numbers."
            ],
            'correct_index': 0,
            'explanation': "Using `i + 1` guarantees elements are picked in strictly increasing index order, naturally preventing duplicate subset combinations like `[1, 2]` and `[2, 1]`."
        }
    ]
}

p49 = {
    'num': 49,
    'lc': 46,
    'title': "Permutations",
    'category': "Backtracking",
    'difficulty': "Medium",
    'lc_slug': "permutations",
    'verbatim_statement': """<p>Given an array <code>nums</code> of distinct integers, return <em>all the possible</em> <span data-keyword="permutation-array"><em>permutations</em></span>. You can return the answer in <strong>any order</strong>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [1,2,3]', 'output': '[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]'},
        {'title': "Example 2", 'input': 'nums = [0,1]', 'output': '[[0,1],[1,0]]'},
        {'title': "Example 3", 'input': 'nums = [1]', 'output': '[[1]]'}
    ],
    'constraints': [
        "1 <= nums.length <= 6",
        "-10 <= nums[i] <= 10",
        "All the integers of nums are unique."
    ],
    'plain_english': "Generate all N! arrangements. In backtracking, maintain a `visited` boolean array and a `path` buffer. At each step, if `path.size == nums.length`, commit a copy to `result`. Otherwise, iterate through all elements from 0 to N-1: if not visited, mark visited, append to path, recurse, then unmark visited and pop from path.",
    'boundary_checks': {
        'min_base': "Single element <code>[1]</code> &rarr; Returns <code>[[1]]</code> directly (1! = 1).",
        'best_exit': "Two elements <code>[0, 1]</code> &rarr; Returns <code>[[0, 1], [1, 0]]</code> (2! = 2).",
        'worst_scan': "Maximum constraint N = 6 &rarr; 6! = 720 permutations; executes in single-digit milliseconds."
    },
    'mental_model': {
        'analogy_title': "The Seating Arrangement Lottery",
        'analogy_text': "Imagine arranging N distinct people into a row of N numbered chairs. For Chair #1, you have N choices. Once you seat someone in Chair #1 (marking them as 'seated'), you move to Chair #2, where you have N - 1 remaining choices. You continue until all N chairs are filled. You record the photograph (one permutation), ask the last person to stand up, and try the next candidate.",
        'aha_moment': "In Subsets, order does not matter, so we use a `start` pointer moving forward. In Permutations, order DOES matter, so every call loops over ALL indices `0..N-1`, using a boolean `visited` array (or in-place swap) to prevent reusing the same number.",
        'trigger': "All arrangements, all orders, permutations of distinct items &rarr; Backtracking with boolean `visited` lookup or in-place swapping.",
        'sisters': [
            {'name': "Permutations II (with Duplicates)", 'lc': "LC #47"},
            {'name': "Next Permutation", 'lc': "LC #31"},
            {'name': "Subsets", 'lc': "LC #78"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N * N!) Time, O(N * N!) Space",
        'naive_text': "Insert elements iteratively in every possible slot: start with `[[nums[0]]]`, for next element insert into all possible index positions.",
        'wasted_work': "Allocating massive intermediate lists and copying elements across nested loops.",
        'optimal_complexity': "O(N * N!) Time, O(N) Recursion Stack",
        'optimal_text': "Backtracking with boolean `visited` array and reusable `path` buffer. Only full permutations of length N are committed.",
        'core_invariant': "At recursion depth d, `path` contains exactly d distinct elements, and `visited[i] == true` for each included element.",
        'table_rows': [
            {'name': "1. Iterative Position Insertion", 'time': "O(N * N!)", 'space': "O(N * N!)", 'note': "High allocation overhead inserting at every index."},
            {'name': "2. In-Place Swap Backtracking", 'time': "O(N * N!)", 'space': "O(N) Call Stack", 'note': "Swaps `nums[start]` with `nums[i]`; zero extra visited array."},
            {'name': "★ 3. Backtracking with Boolean Visited", 'time': "O(N * N!)", 'space': "O(N)", 'note': "Most intuitive interview structure; preserves lexicographical order."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [1, 2, 3]',
        'columns': ["Depth", "Path State", "Visited Array [1, 2, 3]", "Loop Index", "Action / State"],
        'rows': [
            ["0", "[]", "[F, F, F]", "i = 0", "Pick 1 &rarr; visited[0] = true"],
            ["1", "[1]", "[T, F, F]", "i = 1", "Pick 2 &rarr; visited[1] = true"],
            ["2", "[1, 2]", "[T, T, F]", "i = 2", "Pick 3 &rarr; visited[2] = true"],
            ["3 (Leaf)", "[1, 2, 3]", "[T, T, T]", "-", "path.size == 3 &rarr; Commit [1, 2, 3]!"],
            ["Backtrack", "[1, 2]", "[T, T, F]", "i = 2 done", "Pop 3 &rarr; visited[2] = false"],
            ["Backtrack", "[1]", "[T, F, F]", "i = 1 done", "Pop 2 &rarr; visited[1] = false"],
            ["1", "[1]", "[T, F, F]", "i = 2", "Pick 3 &rarr; visited[2] = true &rarr; leads to [1, 3, 2]"]
        ],
        'takeaway': "Total leaf nodes = 3! = 6. Visited array guarantees each number appears exactly once per permutation."
    },
    'stepper': {
        'title': "Permutations Backtracking Stepper",
        'default_input': '1, 2, 3',
        'presets': [
            {'label': "Example [1, 2, 3]", 'onclick': 'loadPreset([1, 2, 3])'},
            {'label': "Two Items [1, 2]", 'onclick': 'loadPreset([1, 2])'}
        ],
        'metrics': [
            {'id': "metric-depth", 'label': "Current Depth", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-path", 'label': "Current Path", 'color': "text-cyan-400", 'initial': "[]"},
            {'id': "metric-visited", 'label': "Visited State", 'color': "text-emerald-400", 'initial': "[F, F, F]"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="perm-stage" class="w-full flex flex-col items-center justify-center gap-3 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace permutation tree exploration.',
        'pseudocode_lines': [
            "fun backtrack(path: MutableList<Int>) {",
            "    if (path.size == nums.size) {",
            "        result.add(ArrayList(path)) // Leaf node reached",
            "        return",
            "    }",
            "    for (i in nums.indices) {",
            "        if (visited[i]) continue",
            "        visited[i] = true; path.add(nums[i]) // Choose",
            "        backtrack(path)                     // Explore",
            "        visited[i] = false; path.removeAt(path.size - 1) // Un-choose",
            "    }",
            "}"
        ],
        'js_code': """
    let permNums = [1, 2, 3];

    function buildSteps(arr) {
      steps = [];
      let res = [];
      let path = [];
      let visited = new Array(arr.length).fill(false);

      function backtrack() {
        if (path.length === arr.length) {
          res.push([...path]);
          steps.push({
            depth: path.length, path: `[${path.join(', ')}]`,
            visited: `[${visited.map(v => v ? 'T' : 'F').join(', ')}]`,
            action: `Leaf: Commit [${path.join(', ')}]`,
            res: JSON.parse(JSON.stringify(res)), line: 3,
            narration: `Full permutation [${path.join(', ')}] formed! Committed to result (#${res.length}).`
          });
          return;
        }

        for (let i = 0; i < arr.length; i++) {
          if (visited[i]) continue;
          visited[i] = true;
          path.push(arr[i]);

          steps.push({
            depth: path.length, path: `[${path.join(', ')}]`,
            visited: `[${visited.map(v => v ? 'T' : 'F').join(', ')}]`,
            action: `Chose ${arr[i]} at idx ${i}`,
            res: JSON.parse(JSON.stringify(res)), line: 8,
            narration: `Chose ${arr[i]}. Marked visited[${i}] = true. Recursing to depth ${path.length}.`
          });

          backtrack();

          let popped = path.pop();
          visited[i] = false;

          steps.push({
            depth: path.length, path: `[${path.join(', ')}]`,
            visited: `[${visited.map(v => v ? 'T' : 'F').join(', ')}]`,
            action: `Backtracked: Unpicked ${popped}`,
            res: JSON.parse(JSON.stringify(res)), line: 10,
            narration: `Backtracked: unpicked ${popped}, set visited[${i}] = false.`
          });
        }
      }

      backtrack();

      steps.push({
        depth: "-", path: "Done",
        visited: `[${visited.map(v => v ? 'T' : 'F').join(', ')}]`,
        action: "All Permutations Generated",
        res: JSON.parse(JSON.stringify(res)), line: 1,
        narration: `Finished! Generated all ${arr.length}! = ${res.length} permutations.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      permNums = arr;
      stepperReset();
      buildSteps(permNums);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        permNums = arr;
        stepperReset();
        buildSteps(permNums);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-depth').textContent = step.depth;
      document.getElementById('metric-path').textContent = step.path;
      document.getElementById('metric-visited').textContent = step.visited;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('perm-stage');
      stage.innerHTML = '';
      if (!step.res) return;

      const wrap = document.createElement('div');
      wrap.className = 'w-full flex flex-col gap-2';

      const label = document.createElement('div');
      label.className = 'text-xs font-mono text-slate-400 font-bold';
      label.textContent = `Permutations Committed (${step.res.length} total):`;
      wrap.appendChild(label);

      const grid = document.createElement('div');
      grid.className = 'flex flex-wrap gap-2 max-h-40 overflow-y-auto p-2 bg-slate-950 border border-slate-800 rounded-xl';
      step.res.forEach((p, idx) => {
        const item = document.createElement('span');
        item.className = 'px-2.5 py-1 rounded bg-slate-900 border border-slate-700 text-slate-300 font-mono text-xs';
        item.textContent = `[${p.join(', ')}]`;
        grid.appendChild(item);
      });
      wrap.appendChild(grid);

      stage.appendChild(wrap);
"""
    },
    'approaches': [
        {
            'name': "Iterative Insertion in All Positions (Naive)",
            'desc': "Start with an initial single element permutation. For each new number, insert it into every possible index of every existing permutation.",
            'time': "O(N * N!)",
            'space': "O(N * N!)",
            'code_kotlin': """class Solution {
    fun permute(nums: IntArray): List<List<Int>> {
        var perms = mutableListOf<List<Int>>(listOf(nums[0]))
        for (i in 1 until nums.size) {
            val num = nums[i]
            val nextPerms = mutableListOf<List<Int>>()
            for (p in perms) {
                for (j in 0..p.size) {
                    val newP = ArrayList(p)
                    newP.add(j, num)
                    nextPerms.add(newP)
                }
            }
            perms = nextPerms
        }
        return perms
    }
}""",
            'notes_kotlin': "Iterative placement at every position. High memory allocation overhead.",
            'code_python': """class Solution:
    def permute(self, nums: List[int]) -> List[List[int]]:
        perms = [[nums[0]]]
        for n in nums[1:]:
            next_perms = []
            for p in perms:
                for j in range(len(p) + 1):
                    next_perms.append(p[:j] + [n] + p[j:])
            perms = next_perms
        return perms""",
            'notes_python': "Inserts element into all j slices. Functionally correct, but generates high GC churn.",
            'code_java': """class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> perms = new ArrayList<>();
        perms.add(List.of(nums[0]));
        for (int i = 1; i < nums.length; i++) {
            int num = nums[i];
            List<List<Integer>> nextPerms = new ArrayList<>();
            for (List<Integer> p : perms) {
                for (int j = 0; j <= p.size(); j++) {
                    List<Integer> newP = new ArrayList<>(p);
                    newP.add(j, num);
                    nextPerms.add(newP);
                }
            }
            perms = nextPerms;
        }
        return perms;
    }
}""",
            'notes_java': "Iterative insertion creates thousands of list instances."
        },
        {
            'name': "In-Place Swap Backtracking",
            'desc': "Partition array into visited prefix and unvisited suffix. Swap nums[start] with nums[i] to avoid extra visited array.",
            'time': "O(N * N!)",
            'space': "O(N) Call Stack",
            'code_kotlin': """class Solution {
    fun permute(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()

        fun backtrack(start: Int) {
            if (start == nums.size) {
                result.add(nums.toList())
                return
            }
            for (i in start until nums.size) {
                nums[start] = nums[i].also { nums[i] = nums[start] } // Swap
                backtrack(start + 1)
                nums[start] = nums[i].also { nums[i] = nums[start] } // Un-swap
            }
        }

        backtrack(0)
        return result
    }
}""",
            'notes_kotlin': "Memory efficient. Modifies nums directly via swap and un-swap.",
            'code_python': """class Solution:
    def permute(self, nums: List[int]) -> List[List[int]]:
        res = []
        def backtrack(start):
            if start == len(nums):
                res.append(list(nums))
                return
            for i in range(start, len(nums)):
                nums[start], nums[i] = nums[i], nums[start]
                backtrack(start + 1)
                nums[start], nums[i] = nums[i], nums[start]
        backtrack(0)
        return res""",
            'notes_python': "Pythonic swap idiom `a, b = b, a`. Eliminates auxiliary boolean arrays.",
            'code_java': """class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(0, nums, result);
        return result;
    }

    private void backtrack(int start, int[] nums, List<List<Integer>> result) {
        if (start == nums.length) {
            List<Integer> list = new ArrayList<>();
            for (int n : nums) list.add(n);
            result.add(list);
            return;
        }
        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);
            backtrack(start + 1, nums, result);
            swap(nums, start, i);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}""",
            'notes_java': "Swap-based permutation. Very fast because it avoids object list modifications."
        },
        {
            'name': "Backtracking with Boolean Visited (Optimal Primary)",
            'desc': "Standard DFS tracking picked elements with a boolean array. Commits permutation when path.size == nums.length.",
            'time': "O(N * N!)",
            'space': "O(N) Recursion Stack & Path Buffer",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Backtracking with Boolean Visited Array
     * Time: O(N * N!) - N! permutations, each taking O(N) to copy
     * Space: O(N) - Recursion stack, visited array, and path buffer
     */
    fun permute(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()
        val visited = BooleanArray(nums.size)

        fun backtrack() {
            if (path.size == nums.size) {
                result.add(ArrayList(path))
                return
            }

            for (i in nums.indices) {
                if (visited[i]) continue

                visited[i] = true
                path.add(nums[i])      // Choose

                backtrack()            // Explore

                visited[i] = false     // Un-choose (Backtrack)
                path.removeAt(path.size - 1)
            }
        }

        backtrack()
        return result
    }
}""",
            'notes_kotlin': "Standard interview template. Preserves natural sorted order of elements.",
            'code_python': """class Solution:
    # Optimal: Backtracking with Boolean Visited
    # Time: O(N * N!)
    # Space: O(N)
    def permute(self, nums: List[int]) -> List[List[int]]:
        result = []
        path = []
        visited = [False] * len(nums)

        def backtrack():
            if len(path) == len(nums):
                result.append(list(path))
                return

            for i in range(len(nums)):
                if visited[i]:
                    continue
                visited[i] = True
                path.append(nums[i])   # Choose
                backtrack()             # Explore
                visited[i] = False      # Un-choose
                path.pop()

        backtrack()
        return result""",
            'notes_python': "The classic textbook backtracking pattern. Clean separation of choose, explore, and un-choose.",
            'code_java': """class Solution {
    /**
     * Optimal: Backtracking with Boolean Visited Array
     * Time: O(N * N!) - Explores N! leaf nodes
     * Space: O(N) - Visited array and recursion stack
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[nums.length];
        backtrack(nums, visited, path, result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] visited, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (visited[i]) continue;

            visited[i] = true;
            path.add(nums[i]);                  // Choose

            backtrack(nums, visited, path, result); // Explore

            visited[i] = false;                 // Backtrack
            path.remove(path.size() - 1);
        }
    }
}""",
            'notes_java': "Widely preferred by interviewers because it generalizes directly to Permutations II with duplicate handling."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun permute(nums: IntArray): List<List<Int>> {
        val res = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()
        val vis = BooleanArray(nums.size)
        fun dfs() {
            if (path.size == nums.size) {
                res.add(ArrayList(path))
                return
            }
            for (i in nums.indices) {
                if (vis[i]) continue
                vis[i] = true; path.add(nums[i])
                dfs()
                vis[i] = false; path.removeAt(path.size - 1)
            }
        }
        dfs()
        return res
    }
}""",
        'python_boilerplate': """class Solution:
    def permute(self, nums: List[int]) -> List[List[int]]:
        res, path = [], []
        vis = [False] * len(nums)
        def dfs():
            if len(path) == len(nums):
                res.append(list(path))
                return
            for i in range(len(nums)):
                if vis[i]: continue
                vis[i] = True; path.append(nums[i])
                dfs()
                vis[i] = False; path.pop()
        dfs()
        return res""",
        'java_boilerplate': """class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        boolean[] vis = new boolean[nums.length];
        dfs(nums, vis, path, res);
        return res;
    }
    private void dfs(int[] nums, boolean[] vis, List<Integer> path, List<List<Integer>> res) {
        if (path.size() == nums.length) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (vis[i]) continue;
            vis[i] = true; path.add(nums[i]);
            dfs(nums, vis, path, res);
            vis[i] = false; path.remove(path.size() - 1);
        }
    }
}""",
        'js_test_runner': """
      const testCases = [
        { nums: [1,2,3], count: 6 },
        { nums: [0,1], count: 2 },
        { nums: [1], count: 1 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runPermute(arr) {
          let res = [], path = [], vis = new Array(arr.length).fill(false);
          function dfs() {
            if (path.length === arr.length) {
              res.push([...path]);
              return;
            }
            for (let i = 0; i < arr.length; i++) {
              if (vis[i]) continue;
              vis[i] = true; path.push(arr[i]);
              dfs();
              vis[i] = false; path.pop();
            }
          }
          dfs();
          return res;
        }

        const out = runPermute(tc.nums);
        const passed = (out.length === tc.count);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: nums=[${tc.nums.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (' + out.length + ' permutations)' : '&#10008; FAILED'}</span>
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
        'phase1': "I clarify: Are all numbers in nums distinct? Yes, constraints state all elements are unique. What is the maximum length of nums? Up to 6, which yields 6! = 720 permutations.",
        'phase2': "For strategy: Unlike Subsets where elements are chosen in forward order using a start index, in Permutations every element can appear in any position. We use a boolean `visited` array of size N and a reusable `path` list. In each recursive call, if `path.size == nums.length`, we commit `ArrayList(path)`. Otherwise, we loop over all indices `0..N-1`: if not visited, mark visited, append to path, recurse, then unmark visited and pop to backtrack. Time is O(N * N!) and space is O(N).",
        'phase3': "I implement `permute`: allocate `result = mutableListOf<List<Int>>()`, `path = mutableListOf<Int>()`, and `visited = BooleanArray(nums.size)`. Inside `backtrack()`: base condition checks `path.size == nums.size`, adding `ArrayList(path)` and returning. Loop `for (i in nums.indices)`: skip if `visited[i]`. Set `visited[i] = true`, add `nums[i]`, call `backtrack()`, then reset `visited[i] = false` and remove last element.",
        'phase4': "Dry run with [1, 2]: Depth 0 picks 1 (vis[0]=T), depth 1 picks 2 (vis[1]=T). Depth 2 reaches size 2, commits [1, 2]. Backtracks 2, backtracks 1. Loop picks 2 (vis[1]=T), depth 1 picks 1 (vis[0]=T). Commits [2, 1]. Total 2 permutations: [1, 2], [2, 1]. Matches 2! = 2.",
        'senior_followup': "Senior Scalability: In combinatorial optimization (e.g. Traveling Salesperson Problem), finding permutations over 20+ cities is NP-hard. Real-world systems use heuristic branch-and-bound, 2-opt local search, or simulated annealing rather than exhaustive permutation generation."
    },
    'quiz': [
        {
            'question': "Why does the loop in Permutations iterate `for (i in 0 until nums.size)` on every recursive frame instead of starting from a `start` index like Subsets?",
            'options': [
                "Because permutations care about ordering; an earlier element (e.g. index 0) can appear AFTER a later element (e.g. index 2), as in `[3, 1, 2]`.",
                "To prevent array index out of bounds exceptions.",
                "Because permutations can contain negative numbers.",
                "To optimize CPU branch prediction."
            ],
            'correct_index': 0,
            'explanation': "In Subsets, combinations are unordered, so advancing `start` avoids duplicate sets like `[1,2]` and `[2,1]`. In Permutations, `[1, 2]` and `[2, 1]` are different valid solutions, so every position can select any unused element from anywhere in the array."
        },
        {
            'question': "What is the total number of permutations generated for an array of N distinct elements?",
            'options': [
                "2^N",
                "N * (N + 1) / 2",
                "N! (N factorial)",
                "N^2"
            ],
            'correct_index': 2,
            'explanation': "There are N choices for the first position, N-1 for the second, ..., and 1 choice for the last position, yielding $N \\times (N-1) \\times \\cdots \\times 1 = N!$ total permutations."
        }
    ]
}
