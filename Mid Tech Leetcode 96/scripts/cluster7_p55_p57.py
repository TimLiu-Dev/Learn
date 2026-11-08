import sys
sys.path.append('scripts')

p55 = {
    'num': 55,
    'lc': 198,
    'title': "House Robber",
    'category': "Dynamic Programming",
    'difficulty': "Medium",
    'lc_slug': "house-robber",
    'verbatim_statement': """<p>You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security systems connected and <strong>it will automatically contact the police if two adjacent houses were broken into on the same night</strong>.</p>
<p>Given an integer array <code>nums</code> representing the amount of money of each house, return <em>the maximum amount of money you can rob tonight <strong>without alerting the police</strong></em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [1,2,3,1]', 'output': '4\nExplanation: Rob house 1 (money = 1) and then rob house 3 (money = 3). Total amount = 1 + 3 = 4.'},
        {'title': "Example 2", 'input': 'nums = [2,7,9,3,1]', 'output': '12\nExplanation: Rob house 1 (money = 2), rob house 3 (money = 9) and rob house 5 (money = 1). Total amount = 2 + 9 + 1 = 12.'}
    ],
    'constraints': [
        "1 <= nums.length <= 100",
        "0 <= nums[i] <= 400"
    ],
    'plain_english': "At each house, you have two mutually exclusive choices: 1) Skip this house and keep the maximum loot from the previous house (`rob1`); or 2) Rob this house and add its cash to the maximum loot from two houses ago (`rob2 + num`). Maintain two running variables to achieve optimal O(N) time with O(1) space.",
    'boundary_checks': {
        'min_base': "Single house <code>nums = [5]</code> &rarr; Returns 5.",
        'best_exit': "Two houses <code>nums = [2, 7]</code> &rarr; Returns `max(2, 7) = 7`.",
        'worst_scan': "All zeroes <code>nums = [0, 0, 0]</code> &rarr; Returns 0 smoothly."
    },
    'mental_model': {
        'analogy_title': "The Two-Step Money Leapfrog",
        'analogy_text': "Imagine running down a street with a backpack. You look at the house in front of you. You can either skip it, which means your maximum money is whatever you could carry up to the previous house (`rob1`). Or you can rob it, which means you cannot have robbed the previous house, so your money is whatever you had two houses ago (`rob2`) plus this house's loot. You take the better of the two options (`max(rob1, rob2 + num)`) and leapfrog forward.",
        'aha_moment': "Computing `dp[i]` only ever looks back at `dp[i-1]` and `dp[i-2]`. You never need an entire `dp` array! Maintaining just two scalar variables (`rob1`, `rob2`) compresses space from O(N) to O(1).",
        'trigger': "Cannot pick adjacent elements, maximize sum of non-consecutive elements &rarr; 1D Dynamic Programming with 2-variable state reduction.",
        'sisters': [
            {'name': "House Robber II (Circular Street)", 'lc': "LC #213"},
            {'name': "House Robber III (Binary Tree)", 'lc': "LC #337"},
            {'name': "Delete and Earn", 'lc': "LC #740"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(2^N) Time, O(N) Space",
        'naive_text': "Brute-force recursion: for each house, branch into either robbing it (recurse i+2) or skipping it (recurse i+1).",
        'wasted_work': "Re-evaluating identical subproblems exponentially ($2^N$ calls).",
        'optimal_complexity': "O(N) Time, O(1) Auxiliary Space",
        'optimal_text': "Bottom-up DP using two rolling variables. Each house is processed in O(1) time.",
        'core_invariant': "At the end of processing house `i`, `rob1` stores the optimal profit for houses up to `i`, and `rob2` stores the optimal profit up to `i - 1`.",
        'table_rows': [
            {'name': "1. Brute-Force Recursion", 'time': "O(2^N)", 'space': "O(N)", 'note': "Exponential call tree with massive duplicate calculations."},
            {'name': "2. Top-Down Memoization / 1D DP Array", 'time': "O(N)", 'space': "O(N)", 'note': "Allocates size N array or hash table."},
            {'name': "★ 3. Two-Variable Space-Optimized DP", 'time': "O(N)", 'space': "O(1) Auxiliary", 'note': "Strict constant memory; fastest cache execution."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [2, 7, 9, 3, 1]',
        'columns': ["House Index", "House Loot", "Option 1 (Skip: rob1)", "Option 2 (Rob: rob2 + num)", "New Max Loot", "New (rob2, rob1)"],
        'rows': [
            ["Init", "-", "-", "-", "-", "rob2 = 0, rob1 = 0"],
            ["i = 0", "2", "0", "0 + 2 = 2", "max(0, 2) = 2", "rob2 = 0, rob1 = 2"],
            ["i = 1", "7", "2", "0 + 7 = 7", "max(2, 7) = 7", "rob2 = 2, rob1 = 7"],
            ["i = 2", "9", "7", "2 + 9 = 11", "max(7, 11) = 11", "rob2 = 7, rob1 = 11"],
            ["i = 3", "3", "11", "7 + 3 = 10", "max(11, 10) = 11", "rob2 = 11, rob1 = 11"],
            ["i = 4", "1", "11", "11 + 1 = 12", "max(11, 12) = 12", "rob2 = 11, rob1 = 12"]
        ],
        'takeaway': "Maximum loot = 12 (from houses 0, 2, 4: 2 + 9 + 1 = 12). Achieved with O(1) memory."
    },
    'stepper': {
        'title': "House Robber State Optimization Stepper",
        'default_input': '2, 7, 9, 3, 1',
        'presets': [
            {'label': "Example [2, 7, 9, 3, 1]", 'onclick': 'loadPreset([2, 7, 9, 3, 1])'},
            {'label': "Example [1, 2, 3, 1]", 'onclick': 'loadPreset([1, 2, 3, 1])'},
            {'label': "Two Houses [2, 7]", 'onclick': 'loadPreset([2, 7])'}
        ],
        'metrics': [
            {'id': "metric-house", 'label': "Current House", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-skip", 'label': "Skip Option (rob1)", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-rob", 'label': "Rob Option (rob2 + num)", 'color': "text-purple-400", 'initial': "0"},
            {'id': "metric-max", 'label': "Current Max Loot", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="robber-stage" class="w-full flex items-center justify-center p-4 min-h-[140px] overflow-x-auto"></div>',
        'initial_narration': 'Click "Next Step" to trace dynamic programming state evolution.',
        'pseudocode_lines': [
            "var rob1 = 0; var rob2 = 0",
            "for (num in nums) {",
            "    val newRob = maxOf(rob1, rob2 + num)",
            "    rob2 = rob1",
            "    rob1 = newRob",
            "}",
            "return rob1"
        ],
        'js_code': """
    let rawHouses = [2, 7, 9, 3, 1];

    function buildSteps(arr) {
      steps = [];
      let rob1 = 0, rob2 = 0;

      steps.push({
        house: "Init", skip: 0, rob: 0, maxVal: 0,
        activeIdx: -1, rob1: 0, rob2: 0, line: 1,
        narration: "Initialized running variables rob1 = 0, rob2 = 0."
      });

      for (let i = 0; i < arr.length; i++) {
        let n = arr[i];
        let skipVal = rob1;
        let robVal = rob2 + n;
        let newRob = Math.max(skipVal, robVal);

        steps.push({
          house: `House ${i} ($${n})`, skip: skipVal, rob: `${rob2} + ${n} = ${robVal}`,
          maxVal: newRob, activeIdx: i, rob1: newRob, rob2: rob1, line: 3,
          narration: `House ${i} ($${n}): Skip = $${skipVal}, Rob = $${robVal}. Max is $${newRob}.`
        });

        rob2 = rob1;
        rob1 = newRob;
      }

      steps.push({
        house: "Complete", skip: "-", rob: "-", maxVal: rob1,
        activeIdx: -1, rob1: rob1, rob2: rob2, line: 7,
        narration: `Finished! Maximum loot possible without alerting police is $${rob1}.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      rawHouses = arr;
      stepperReset();
      buildSteps(rawHouses);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        rawHouses = arr;
        stepperReset();
        buildSteps(rawHouses);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-house').textContent = step.house;
      document.getElementById('metric-skip').textContent = `$${step.skip}`;
      document.getElementById('metric-rob').textContent = `$${step.rob}`;
      document.getElementById('metric-max').textContent = `$${step.maxVal}`;
""",
        'js_render_visual': """
      const stage = document.getElementById('robber-stage');
      stage.innerHTML = '';

      const row = document.createElement('div');
      row.className = 'flex flex-wrap items-center justify-center gap-3';

      rawHouses.forEach((money, idx) => {
        const isActive = (idx === step.activeIdx);
        const card = document.createElement('div');
        card.className = `flex flex-col items-center justify-center p-3 rounded-xl border min-w-[70px] font-mono ${
          isActive ? 'bg-amber-500/30 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' :
          'bg-slate-900 text-slate-300 border-slate-800'
        }`;
        card.innerHTML = `
          <span class="text-[9px] text-slate-500 uppercase">H${idx}</span>
          <span class="text-sm font-bold text-emerald-400">$${money}</span>
        `;
        row.appendChild(card);
      });

      stage.appendChild(row);
"""
    },
    'approaches': [
        {
            'name': "Top-Down Recursion with Memoization",
            'desc': "Recursive DFS choosing to rob or skip, memoizing results in a DP array.",
            'time': "O(N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun rob(nums: IntArray): Int {
        val memo = IntArray(nums.size) { -1 }
        return dfs(0, nums, memo)
    }

    private fun dfs(i: Int, nums: IntArray, memo: IntArray): Int {
        if (i >= nums.size) return 0
        if (memo[i] != -1) return memo[i]
        val rob = nums[i] + dfs(i + 2, nums, memo)
        val skip = dfs(i + 1, nums, memo)
        memo[i] = maxOf(rob, skip)
        return memo[i]
    }
}""",
            'notes_kotlin': "Top-down memoization. Recursion stack and memo table take O(N) space.",
            'code_python': """class Solution:
    def rob(self, nums: List[int]) -> int:
        memo = {}
        def dfs(i):
            if i >= len(nums): return 0
            if i in memo: return memo[i]
            res = max(nums[i] + dfs(i + 2), dfs(i + 1))
            memo[i] = res
            return res
        return dfs(0)""",
            'notes_python': "Intuitive top-down recursion with memoization dictionary.",
            'code_java': """class Solution {
    public int rob(int[] nums) {
        int[] memo = new int[nums.length];
        Arrays.fill(memo, -1);
        return dfs(0, nums, memo);
    }

    private int dfs(int i, int[] nums, int[] memo) {
        if (i >= nums.length) return 0;
        if (memo[i] != -1) return memo[i];
        int rob = nums[i] + dfs(i + 2, nums, memo);
        int skip = dfs(i + 1, nums, memo);
        return memo[i] = Math.max(rob, skip);
    }
}""",
            'notes_java': "O(N) memory for memo array and recursion stack."
        },
        {
            'name': "Bottom-Up 1D DP Array",
            'desc': "Allocate a DP array of size N where `dp[i] = max(dp[i-1], dp[i-2] + nums[i])`.",
            'time': "O(N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun rob(nums: IntArray): Int {
        if (nums.isEmpty()) return 0
        if (nums.size == 1) return nums[0]

        val dp = IntArray(nums.size)
        dp[0] = nums[0]
        dp[1] = maxOf(nums[0], nums[1])

        for (i in 2 until nums.size) {
            dp[i] = maxOf(dp[i - 1], dp[i - 2] + nums[i])
        }

        return dp[nums.size - 1]
    }
}""",
            'notes_kotlin': "Classic 1D DP table. Clear base case handling for sizes 1 and 2.",
            'code_python': """class Solution:
    def rob(self, nums: List[int]) -> int:
        if not nums: return 0
        if len(nums) == 1: return nums[0]

        dp = [0] * len(nums)
        dp[0] = nums[0]
        dp[1] = max(nums[0], nums[1])

        for i in range(2, len(nums)):
            dp[i] = max(dp[i - 1], dp[i - 2] + nums[i])

        return dp[-1]""",
            'notes_python': "Standard 1D tabular dynamic programming.",
            'code_java': """class Solution {
    public int rob(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }

        return dp[nums.length - 1];
    }
}""",
            'notes_java': "Straightforward bottom-up array formulation."
        },
        {
            'name': "Two-Variable State Optimization (Optimal Primary)",
            'desc': "Maintain only two variables: `rob1` (loot up to i) and `rob2` (loot up to i-1). Reduces space to O(1).",
            'time': "O(N)",
            'space': "O(1) Auxiliary Space",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Two-Variable State Space Optimization
     * Time: O(N) - Single pass through nums
     * Space: O(1) - Only two integer variables
     */
    fun rob(nums: IntArray): Int {
        var rob1 = 0 // Represents max loot up to i - 1
        var rob2 = 0 // Represents max loot up to i - 2

        for (num in nums) {
            val newRob = maxOf(rob1, rob2 + num)
            rob2 = rob1
            rob1 = newRob
        }

        return rob1
    }
}""",
            'notes_kotlin': "Gold standard production implementation. Completely eliminates array allocations.",
            'code_python': """class Solution:
    # Optimal: Two-Variable State Reduction
    # Time: O(N) - Linear scan
    # Space: O(1) Auxiliary
    def rob(self, nums: List[int]) -> int:
        rob1, rob2 = 0, 0

        for num in nums:
            new_rob = max(rob1, rob2 + num)
            rob2 = rob1
            rob1 = new_rob

        return rob1""",
            'notes_python': "Extremely clean and pythonic. Zero edge case if-branches needed.",
            'code_java': """class Solution {
    /**
     * Optimal: Two-Variable State Reduction
     * Time: O(N) - Linear pass
     * Space: O(1) - Constant auxiliary memory
     */
    public int rob(int[] nums) {
        int rob1 = 0; // dp[i-1]
        int rob2 = 0; // dp[i-2]

        for (int num : nums) {
            int newRob = Math.max(rob1, rob2 + num);
            rob2 = rob1;
            rob1 = newRob;
        }

        return rob1;
    }
}""",
            'notes_java': "Optimal O(1) space with minimal bytecode instructions."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun rob(nums: IntArray): Int {
        var r1 = 0; var r2 = 0
        for (n in nums) {
            val cur = maxOf(r1, r2 + n)
            r2 = r1; r1 = cur
        }
        return r1
    }
}""",
        'python_boilerplate': """class Solution:
    def rob(self, nums: List[int]) -> int:
        r1 = r2 = 0
        for n in nums:
            r1, r2 = max(r1, r2 + n), r1
        return r1""",
        'java_boilerplate': """class Solution {
    public int rob(int[] nums) {
        int r1 = 0, r2 = 0;
        for (int n : nums) {
            int cur = Math.max(r1, r2 + n);
            r2 = r1; r1 = cur;
        }
        return r1;
    }
}""",
        'js_test_runner': """
      const testCases = [
        { nums: [1,2,3,1], expected: 4 },
        { nums: [2,7,9,3,1], expected: 12 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runRob(arr) {
          let r1 = 0, r2 = 0;
          for (let n of arr) {
            let cur = Math.max(r1, r2 + n);
            r2 = r1; r1 = cur;
          }
          return r1;
        }

        const out = runRob(tc.nums);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: nums=[${tc.nums.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED ($' + out + ')' : '&#10008; FAILED (got $' + out + ')'}</span>
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
        'phase1': "I clarify: What if the array has only 1 house? Return that house's value. What if all values are 0? Return 0. Can house loot be negative? The problem states money is >= 0.",
        'phase2': "For strategy: At house i, we have two choices: rob it or skip it. If we skip, the max profit is whatever we had up to house i-1 (`rob1`). If we rob it, we cannot rob house i-1, so our profit is whatever we had up to house i-2 plus this house's value (`rob2 + num`). Since we only ever need `dp[i-1]` and `dp[i-2]`, we don't need a full array! We can maintain two scalar variables (`rob1`, `rob2`) in O(N) time and O(1) space.",
        'phase3': "I implement `rob`: initialize `rob1 = 0` and `rob2 = 0`. Iterate through `nums`: compute `newRob = maxOf(rob1, rob2 + num)`, shift `rob2 = rob1`, and update `rob1 = newRob`. Return `rob1`.",
        'phase4': "Dry run with [2, 7, 9, 3, 1]: num=2 &rarr; max(0, 0+2)=2, rob2=0, rob1=2. num=7 &rarr; max(2, 0+7)=7, rob2=2, rob1=7. num=9 &rarr; max(7, 2+9)=11, rob2=7, rob1=11. num=3 &rarr; max(11, 7+3)=11, rob2=11, rob1=11. num=1 &rarr; max(11, 11+1)=12, rob2=11, rob1=12. Returns 12. Correct.",
        'senior_followup': "Senior Scalability: In House Robber II (where houses are arranged in a circle, so house 0 and house N-1 are adjacent), we run this exact O(1) space algorithm twice: once for `nums[0..N-2]` and once for `nums[1..N-1]`, taking `max(ans1, ans2)` in O(N) time and O(1) space."
    },
    'quiz': [
        {
            'question': "Why is it possible to reduce the space complexity of House Robber from O(N) to O(1)?",
            'options': [
                "Because computing the maximum profit for house i only ever depends on the two immediately preceding subproblem values: dp[i-1] and dp[i-2].",
                "Because house values are positive integers.",
                "Because adjacent houses are connected in a circular ring.",
                "To prevent integer underflow."
            ],
            'correct_index': 0,
            'explanation': "A recurrence relation $dp[i] = \\max(dp[i-1], dp[i-2] + nums[i])$ has a dependency horizon of size 2, making two scalar variables sufficient to carry the state."
        },
        {
            'question': "If nums = [10, 1, 1, 10], what is the optimal robbery amount?",
            'options': [
                "11",
                "12",
                "20 (robbing house 0 and house 3: 10 + 10)",
                "22"
            ],
            'correct_index': 2,
            'explanation': "Houses 0 and 3 are non-adjacent (separated by houses 1 and 2). Robbing house 0 (10) and house 3 (10) yields $10 + 10 = 20$."
        }
    ]
}

p56 = {
    'num': 56,
    'lc': 62,
    'title': "Unique Paths",
    'category': "Dynamic Programming",
    'difficulty': "Medium",
    'lc_slug': "unique-paths",
    'verbatim_statement': """<p>There is a robot on an <code>m x n</code> grid. The robot is initially located at the <strong>top-left corner</strong> (i.e., <code>grid[0][0]</code>). The robot tries to move to the <strong>bottom-right corner</strong> (i.e., <code>grid[m - 1][n - 1]</code>). The robot can only move either <strong>down</strong> or <strong>right</strong> at any point in time.</p>
<p>Given the two integers <code>m</code> and <code>n</code>, return <em>the number of possible unique paths that the robot can take to reach the bottom-right corner</em>.</p>
<p>The test cases are generated so that the answer will be less than or equal to <code>2 * 10<sup>9</sup></code>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'm = 3, n = 7', 'output': '28'},
        {'title': "Example 2", 'input': 'm = 3, n = 2', 'output': '3\nExplanation: From the top-left corner, there are a total of 3 ways to reach the bottom-right corner:\n1. Right -> Down -> Down\n2. Down -> Down -> Right\n3. Down -> Right -> Down'}
    ],
    'constraints': [
        "1 <= m, n <= 100"
    ],
    'plain_english': "The robot can only reach cell `(r, c)` from the cell above `(r - 1, c)` or the cell to the left `(r, c - 1)`. Therefore, `paths(r, c) = paths(r - 1, c) + paths(r, c - 1)`. By using a rolling 1D row array of size N initialized to 1s, each cell updates with `row[c] += row[c - 1]`, solving the problem in O(M*N) time and O(N) space.",
    'boundary_checks': {
        'min_base': "1x1 grid <code>m = 1, n = 1</code> &rarr; Robot already at destination, returns 1.",
        'best_exit': "Single row <code>m = 1, n = 5</code> or single column <code>m = 5, n = 1</code> &rarr; Strictly 1 path.",
        'worst_scan': "100x100 grid &rarr; Problem guarantees answer fits in standard 32-bit signed integer (<= 2*10^9)."
    },
    'mental_model': {
        'analogy_title': "Pascal's Triangle in a Manhattan Grid",
        'analogy_text': "Think of walking along street intersections in Manhattan. To reach the intersection of 5th Ave and 3rd St, you can only arrive from 5th Ave & 2nd St (from the West) or 4th Ave & 3rd St (from the North). The total routes to any corner is the sum of routes from those two feeding intersections. This is Pascal's Triangle tilted onto a grid.",
        'aha_moment': "Notice that to compute a new row, we only need the value above it (`row[c]`) and the value to the left of it in the current row (`row[c-1]`). The update `row[c] = row[c] + row[c-1]` can be done in place inside a single 1D array of size N!",
        'trigger': "Paths on 2D grid moving only Down and Right &rarr; Grid DP: `dp[r][c] = dp[r-1][c] + dp[r][c-1]` or 1D rolling array.",
        'sisters': [
            {'name': "Unique Paths II (with Obstacles)", 'lc': "LC #63"},
            {'name': "Minimum Path Sum", 'lc': "LC #64"},
            {'name': "Dungeon Game", 'lc': "LC #174"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(2^(M + N)) Time, O(M + N) Space",
        'naive_text': "Brute-force recursion branching right or down at every cell.",
        'wasted_work': "Re-exploring overlapping coordinate paths exponentially.",
        'optimal_complexity': "O(M * N) Time, O(N) Space",
        'optimal_text': "1D Rolling Row DP. Updates `row[c] += row[c-1]` across M rows in O(N) space.",
        'core_invariant': "Before processing column `c` in row `r`, `row[c]` holds the path count from `(r-1, c)` and `row[c-1]` holds the count from `(r, c-1)`.",
        'table_rows': [
            {'name': "1. Brute-Force Recursion", 'time': "O(2^(M + N))", 'space': "O(M + N)", 'note': "Exponential branching without memoization."},
            {'name': "2. Full 2D DP Table", 'time': "O(M * N)", 'space': "O(M * N)", 'note': "Allocates full M x N table."},
            {'name': "★ 3. 1D Rolling Row DP", 'time': "O(M * N)", 'space': "O(N)", 'note': "Optimal space; updates single row in place."}
        ]
    },
    'trace_table': {
        'input_desc': 'm = 3, n = 3',
        'columns': ["Row Processed", "Col 0", "Col 1", "Col 2", "Calculation Note"],
        'rows': [
            ["Row 0 (Init)", "1", "1", "1", "Only 1 way to travel purely right along top edge"],
            ["Row 1", "1", "1 + 1 = 2", "2 + 1 = 3", "row[1] = 1+1=2, row[2] = 2+1=3"],
            ["Row 2", "1", "1 + 2 = 3", "3 + 3 = 6", "row[1] = 1+2=3, row[2] = 3+3=6"]
        ],
        'takeaway': "Total unique paths = 6. Rolling 1D array collapses grid rows perfectly."
    },
    'stepper': {
        'title': "1D Rolling Row DP Stepper",
        'default_input': 'm = 3, n = 7',
        'presets': [
            {'label': "3x7 Grid (28 paths)", 'onclick': 'loadPreset(3, 7)'},
            {'label': "3x2 Grid (3 paths)", 'onclick': 'loadPreset(3, 2)'},
            {'label': "3x3 Grid (6 paths)", 'onclick': 'loadPreset(3, 3)'}
        ],
        'metrics': [
            {'id': "metric-row", 'label': "Current Row", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-col", 'label': "Current Col", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-cellval", 'label': "Paths to Cell", 'color': "text-emerald-400", 'initial': "1"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="paths-stage" class="w-full flex items-center justify-center p-4 min-h-[140px] overflow-x-auto"></div>',
        'initial_narration': 'Click "Next Step" to trace rolling row updates.',
        'pseudocode_lines': [
            "val dp = IntArray(n) { 1 } // Top row has 1 path to each cell",
            "for (r in 1 until m) {",
            "    for (c in 1 until n) {",
            "        dp[c] += dp[c - 1] // dp[c] (from above) + dp[c-1] (from left)",
            "    }",
            "}",
            "return dp[n - 1]"
        ],
        'js_code': """
    let gridM = 3, gridN = 7;

    function buildSteps(m, n) {
      steps = [];
      let dp = new Array(n).fill(1);

      steps.push({
        r: 0, c: 0, val: 1, action: "Init Row 0",
        dpArr: [...dp], line: 1,
        narration: `Initialized 1D row array of size ${n} with all 1s (top row paths).`
      });

      for (let r = 1; r < m; r++) {
        for (let c = 1; c < n; c++) {
          let topVal = dp[c];
          let leftVal = dp[c - 1];
          dp[c] = topVal + leftVal;

          steps.push({
            r: r, c: c, val: dp[c], action: `Cell (${r},${c}) Updated`,
            dpArr: [...dp], line: 4,
            narration: `Row ${r}, Col ${c}: dp[${c}] = above(${topVal}) + left(${leftVal}) = ${dp[c]}.`
          });
        }
      }

      steps.push({
        r: m - 1, c: n - 1, val: dp[n - 1], action: "Complete",
        dpArr: [...dp], line: 7,
        narration: `Reached bottom-right corner! Total unique paths = ${dp[n - 1]}.`
      });
    }

    function loadPreset(m, n) {
      document.getElementById('custom-input').value = `m = ${m}, n = ${n}`;
      gridM = m; gridN = n;
      stepperReset();
      buildSteps(gridM, gridN);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const match = val.match(/(\\d+).*?(\\d+)/);
      if (match) {
        gridM = parseInt(match[1]);
        gridN = parseInt(match[2]);
        stepperReset();
        buildSteps(gridM, gridN);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-row').textContent = step.r;
      document.getElementById('metric-col').textContent = step.c;
      document.getElementById('metric-cellval').textContent = step.val;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('paths-stage');
      stage.innerHTML = '';
      if (!step.dpArr) return;

      const row = document.createElement('div');
      row.className = 'flex flex-wrap items-center justify-center gap-2';

      step.dpArr.forEach((p, idx) => {
        const isActive = (idx === step.c);
        const card = document.createElement('div');
        card.className = `flex flex-col items-center justify-center p-3 rounded-xl border min-w-[60px] font-mono ${
          isActive ? 'bg-amber-500/30 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' :
          'bg-slate-900 text-slate-300 border-slate-800'
        }`;
        card.innerHTML = `
          <span class="text-[9px] text-slate-500 uppercase">Col ${idx}</span>
          <span class="text-sm font-bold text-cyan-300">${p}</span>
        `;
        row.appendChild(card);
      });

      stage.appendChild(row);
"""
    },
    'approaches': [
        {
            'name': "Full 2D DP Table (Standard)",
            'desc': "Allocate an M x N table. Initialize first row and column with 1. Fill table using dp[r][c] = dp[r-1][c] + dp[r][c-1].",
            'time': "O(M * N)",
            'space': "O(M * N)",
            'code_kotlin': """class Solution {
    fun uniquePaths(m: Int, n: Int): Int {
        val dp = Array(m) { IntArray(n) }
        for (r in 0 until m) dp[r][0] = 1
        for (c in 0 until n) dp[0][c] = 1

        for (r in 1 until m) {
            for (c in 1 until n) {
                dp[r][c] = dp[r - 1][c] + dp[r][c - 1]
            }
        }

        return dp[m - 1][n - 1]
    }
}""",
            'notes_kotlin': "Classic 2D dynamic programming grid. Allocates full M x N table.",
            'code_python': """class Solution:
    def uniquePaths(self, m: int, n: int) -> int:
        dp = [[1] * n for _ in range(m)]

        for r in range(1, m):
            for c in range(1, n):
                dp[r][c] = dp[r - 1][c] + dp[r][c - 1]

        return dp[m - 1][n - 1]""",
            'notes_python': "Intuitive 2D matrix initialization.",
            'code_java': """class Solution {
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for (int r = 0; r < m; r++) dp[r][0] = 1;
        for (int c = 0; c < n; c++) dp[0][c] = 1;

        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[r][c] = dp[r - 1][c] + dp[r][c - 1];
            }
        }

        return dp[m - 1][n - 1];
    }
}""",
            'notes_java': "Standard 2D DP table. Clear and straightforward."
        },
        {
            'name': "Combinatorics Formula (Math)",
            'desc': "The robot must take (m - 1) down moves and (n - 1) right moves. Total unique paths is C(m+n-2, m-1).",
            'time': "O(min(M, N))",
            'space': "O(1)",
            'code_kotlin': """class Solution {
    fun uniquePaths(m: Int, n: Int): Int {
        val totalMoves = m + n - 2
        val k = minOf(m - 1, n - 1)
        var result: Long = 1

        for (i in 1..k) {
            result = result * (totalMoves - k + i) / i
        }

        return result.toInt()
    }
}""",
            'notes_kotlin': "Computes combination nCr in O(min(m, n)) time. Must use Long to prevent intermediate overflow.",
            'code_python': """class Solution:
    def uniquePaths(self, m: int, n: int) -> int:
        import math
        return math.comb(m + n - 2, m - 1)""",
            'notes_python': "Python 3.8+ `math.comb` computes exact combinations in O(min(M, N)).",
            'code_java': """class Solution {
    public int uniquePaths(int m, int n) {
        int total = m + n - 2;
        int k = Math.min(m - 1, n - 1);
        long result = 1;

        for (int i = 1; i <= k; i++) {
            result = result * (total - k + i) / i;
        }

        return (int) result;
    }
}""",
            'notes_java': "Fastest mathematical formulation. Requires careful ordering to avoid integer truncation."
        },
        {
            'name': "1D Rolling Row Array (Optimal Primary)",
            'desc': "Maintain a single 1D array of size n initialized to 1. In place update `dp[c] += dp[c-1]` for each row.",
            'time': "O(M * N)",
            'space': "O(N) Auxiliary Space",
            'code_kotlin': """class Solution {
    /**
     * Optimal: 1D Rolling Row Dynamic Programming
     * Time: O(M * N) - Every grid cell processed once
     * Space: O(N) - Single 1D array of size N
     */
    fun uniquePaths(m: Int, n: Int): Int {
        val dp = IntArray(n) { 1 }

        for (r in 1 until m) {
            for (c in 1 until n) {
                dp[c] += dp[c - 1]
            }
        }

        return dp[n - 1]
    }
}""",
            'notes_kotlin': "Extremely clean state compression. Space reduced from O(M*N) to O(N).",
            'code_python': """class Solution:
    # Optimal: 1D Rolling Row DP
    # Time: O(M * N)
    # Space: O(N)
    def uniquePaths(self, m: int, n: int) -> int:
        dp = [1] * n

        for r in range(1, m):
            for c in range(1, n):
                dp[c] += dp[c - 1]

        return dp[-1]""",
            'notes_python': "The canonical interview solution. Beautifully concise.",
            'code_java': """class Solution {
    /**
     * Optimal: 1D Rolling Row DP
     * Time: O(M * N) - Computes all cells
     * Space: O(N) - 1D array bounded by column count
     */
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);

        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[c] += dp[c - 1];
            }
        }

        return dp[n - 1];
    }
}""",
            'notes_java': "Optimal production standard. Maximizes CPU cache locality."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun uniquePaths(m: Int, n: Int): Int {
        val dp = IntArray(n) { 1 }
        for (r in 1 until m) {
            for (c in 1 until n) dp[c] += dp[c - 1]
        }
        return dp[n - 1]
    }
}""",
        'python_boilerplate': """class Solution:
    def uniquePaths(self, m: int, n: int) -> int:
        dp = [1] * n
        for r in range(1, m):
            for c in range(1, n):
                dp[c] += dp[c - 1]
        return dp[-1]""",
        'java_boilerplate': """class Solution {
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[c] += dp[c - 1];
            }
        }
        return dp[n - 1];
    }
}""",
        'js_test_runner': """
      const testCases = [
        { m: 3, n: 7, expected: 28 },
        { m: 3, n: 2, expected: 3 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runPaths(m, n) {
          let dp = new Array(n).fill(1);
          for (let r = 1; r < m; r++) {
            for (let c = 1; c < n; c++) {
              dp[c] += dp[c - 1];
            }
          }
          return dp[n - 1];
        }

        const out = runPaths(tc.m, tc.n);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: m=${tc.m}, n=${tc.n}</span>
          <span>${passed ? '&#10004; PASSED (' + out + ' paths)' : '&#10008; FAILED (got ' + out + ')'}</span>
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
        'phase1': "I clarify: Can the robot move diagonally or backward? No, only strictly right and down. Can m or n be 1? Yes, if m=1 or n=1, there is only a single straight-line path, so return 1. Is integer overflow possible? The problem guarantees the answer is <= 2 * 10^9, fitting in a signed 32-bit int.",
        'phase2': "For strategy: To reach cell (r, c), the robot must come from either (r-1, c) above or (r, c-1) to the left. The recurrence is `dp[r][c] = dp[r-1][c] + dp[r][c-1]`. We can compress this to a 1D array of size N initialized to 1. As we iterate through rows from 1 to m-1 and columns from 1 to n-1, we update `dp[c] += dp[c - 1]`. Time is O(M * N) and space is O(N). Alternatively, combinatorics gives C(m+n-2, m-1) in O(min(m, n)) time.",
        'phase3': "I implement `uniquePaths`: allocate `dp = IntArray(n) { 1 }`. Loop `r` from 1 to m-1, and inner loop `c` from 1 to n-1, executing `dp[c] += dp[c - 1]`. Finally return `dp[n - 1]`.",
        'phase4': "Dry run on m=3, n=2: dp initialized to [1, 1]. Row 1: c=1 &rarr; dp[1] = 1 + 1 = 2. Row 2: c=1 &rarr; dp[1] = 2 + 1 = 3. Returns 3 paths: Right-Down-Down, Down-Right-Down, Down-Down-Right. Matches Example 2 perfectly.",
        'senior_followup': "Senior Scalability: In Unique Paths II (where obstacles are present), any obstacle cell resets `dp[c] = 0`. In grid path problems with weighted costs (Minimum Path Sum), the addition is replaced with `min(dp[c], dp[c-1]) + cost`."
    },
    'quiz': [
        {
            'question': "Why can the 2D DP table in Unique Paths be compressed into a single 1D array of size N?",
            'options': [
                "Because calculating a cell in the current row only requires the value directly above it in the previous row (`dp[c]`) and the value to the left in the current row (`dp[c-1]`); in-place addition `dp[c] += dp[c-1]` updates both simultaneously.",
                "Because all rows in the grid are identical.",
                "Because the robot only moves right.",
                "To prevent JVM garbage collection."
            ],
            'correct_index': 0,
            'explanation': "Before the update, `dp[c]` holds the path count from the row above. Adding `dp[c-1]` (which was already updated for the current row) computes the sum of above + left in place."
        },
        {
            'question': "What is the mathematical combinatorial formula to compute unique paths for an m x n grid?",
            'options': [
                "C(m + n - 2, m - 1)",
                "(m - 1) * (n - 1)",
                "2^(m + n)",
                "P(m + n, 2)"
            ],
            'correct_index': 0,
            'explanation': "The robot must take $(m - 1)$ Down steps and $(n - 1)$ Right steps in total $(m + n - 2)$ moves. The number of unique sequences is choosing $(m - 1)$ Downs out of $(m + n - 2)$ total steps: $\\binom{m+n-2}{m-1}$."
        }
    ]
}

p57 = {
    'num': 57,
    'lc': 322,
    'title': "Coin Change",
    'category': "Dynamic Programming",
    'difficulty': "Medium",
    'lc_slug': "coin-change",
    'verbatim_statement': """<p>You are given an integer array <code>coins</code> representing coins of different denominations and an integer <code>amount</code> representing a total amount of money.</p>
<p>Return <em>the fewest number of coins that you need to make up that amount</em>. If that amount of money cannot be made up by any combination of the coins, return <code>-1</code>.</p>
<p>You may assume that you have an infinite number of each kind of coin.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'coins = [1,2,5], amount = 11', 'output': '3\nExplanation: 11 = 5 + 5 + 1'},
        {'title': "Example 2", 'input': 'coins = [2], amount = 3', 'output': '-1'},
        {'title': "Example 3", 'input': 'coins = [1], amount = 0', 'output': '0'}
    ],
    'constraints': [
        "1 <= coins.length <= 12",
        "1 <= coins[i] <= 2^31 - 1",
        "0 <= amount <= 10^4"
    ],
    'plain_english': "Find the minimum number of coins to make `amount`. Maintain a 1D DP array of size `amount + 1` initialized to an impossible sentinel `amount + 1` (with `dp[0] = 0`). For each sub-amount `a` from 1 to `amount`, try every coin: `if (a >= coin) dp[a] = min(dp[a], dp[a - coin] + 1)`. If `dp[amount] > amount`, return -1; else return `dp[amount]`.",
    'boundary_checks': {
        'min_base': "<code>amount = 0</code> &rarr; 0 coins needed; returns 0.",
        'best_exit': "Single coin equals amount (e.g. coin 5 for amount 5) &rarr; Returns 1.",
        'worst_scan': "Impossible amount (e.g. coins = [2], amount = 3) &rarr; `dp[amount]` stays sentinel `amount + 1`; returns -1."
    },
    'mental_model': {
        'analogy_title': "Shortest Path in an Amount Stepping-Stone Graph",
        'analogy_text': "Imagine stepping stones across a river labeled 0 to `amount`. Stone 0 is your starting shore. Each coin denomination is a fixed jumping distance. A coin of 5 lets you leap 5 stones forward in 1 step. You want to reach stone `amount` in the minimum number of leaps. At stone `a`, you look back at all stepping stones you could have jumped from (`a - coin`): your minimum leap count is 1 plus the best leap count among those landing pads.",
        'aha_moment': "Greedy (always picking the largest coin) FAILS! For coins = [1, 3, 4] and amount = 6: greedy picks 4 + 1 + 1 (3 coins), whereas the optimal DP finds 3 + 3 (2 coins). Dynamic programming guarantees the global minimum.",
        'trigger': "Fewest items to make exact target, infinite supply, greedy fails &rarr; Unbounded Knapsack / 1D Bottom-Up Dynamic Programming.",
        'sisters': [
            {'name': "Coin Change II (Total Combinations)", 'lc': "LC #518"},
            {'name': "Perfect Squares", 'lc': "LC #279"},
            {'name': "Word Break", 'lc': "LC #139"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(C^A) Time, O(A) Space",
        'naive_text': "Brute-force recursion: at each step, try subtracting every coin. Branch factor C raised to depth A.",
        'wasted_work': "Exponentially recalculating identical sub-amounts millions of times.",
        'optimal_complexity': "O(amount * C) Time, O(amount) Space",
        'optimal_text': "Bottom-up 1D DP array of size `amount + 1`. Each sub-amount is computed once in O(C) operations.",
        'core_invariant': "For every `a <= amount`, `dp[a]` stores the globally optimal minimum coin count to produce exact amount `a`.",
        'table_rows': [
            {'name': "1. Brute-Force Recursion", 'time': "O(C^A)", 'space': "O(A)", 'note': "Exponential tree; time limit exceeded."},
            {'name': "2. Queue BFS on Amount Graph", 'time': "O(amount * C)", 'space': "O(amount)", 'note': "Level-by-level BFS finds shortest path in unweighted DAG."},
            {'name': "★ 3. Bottom-Up 1D DP Array", 'time': "O(amount * C)", 'space': "O(amount)", 'note': "Standard optimal algorithm. Cleanest cache performance."}
        ]
    },
    'trace_table': {
        'input_desc': 'coins = [1, 2, 5], amount = 11',
        'columns': ["Amount a", "Evaluation (min over valid a - coin + 1)", "Optimal dp[a]", "Coin Used to Achieve Min"],
        'rows': [
            ["0", "Base case", "0", "None"],
            ["1", "dp[1 - 1] + 1 = 1", "1", "Coin 1"],
            ["2", "min(dp[1]+1, dp[0]+1) = min(2, 1) = 1", "1", "Coin 2"],
            ["3", "min(dp[2]+1, dp[1]+1) = min(2, 2) = 2", "2", "Coin 1 or 2"],
            ["4", "min(dp[3]+1, dp[2]+1) = min(3, 2) = 2", "2", "Coin 2"],
            ["5", "min(dp[4]+1, dp[3]+1, dp[0]+1) = min(3, 3, 1) = 1", "1", "Coin 5"],
            ["...", "...", "...", "..."],
            ["11", "min(dp[10]+1, dp[9]+1, dp[6]+1) = min(3, 4, 3) = 3", "3", "Coin 5 (5 + 5 + 1 = 11)"]
        ],
        'takeaway': "Optimal answer = 3 coins (5 + 5 + 1). DP table built in O(amount * |coins|)."
    },
    'stepper': {
        'title': "Coin Change Bottom-Up DP Stepper",
        'default_input': 'coins = [1,2,5], amount = 11',
        'presets': [
            {'label': "Coins [1,2,5], Amount 11", 'onclick': 'loadPreset([1,2,5], 11)'},
            {'label': "Greedy Trap [1,3,4], Amount 6", 'onclick': 'loadPreset([1,3,4], 6)'},
            {'label': "Impossible [2], Amount 3", 'onclick': 'loadPreset([2], 3)'}
        ],
        'metrics': [
            {'id': "metric-amt", 'label': "Current Amount", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-coin", 'label': "Coin Evaluated", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-opt", 'label': "Optimal dp[amt]", 'color': "text-emerald-400", 'initial': "0"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="coin-stage" class="w-full flex items-center justify-center p-4 min-h-[140px] overflow-x-auto"></div>',
        'initial_narration': 'Click "Next Step" to trace bottom-up DP table construction.',
        'pseudocode_lines': [
            "val dp = IntArray(amount + 1) { amount + 1 }",
            "dp[0] = 0",
            "for (a in 1..amount) {",
            "    for (coin in coins) {",
            "        if (a >= coin) dp[a] = minOf(dp[a], dp[a - coin] + 1)",
            "    }",
            "}",
            "return if (dp[amount] > amount) -1 else dp[amount]"
        ],
        'js_code': """
    let coinSet = [1, 2, 5], targetAmt = 11;

    function buildSteps(coins, amount) {
      steps = [];
      let sentinel = amount + 1;
      let dp = new Array(amount + 1).fill(sentinel);
      dp[0] = 0;

      steps.push({
        amt: 0, coin: "-", opt: 0, action: "Init Base Case dp[0]=0",
        dpArr: [...dp], line: 2,
        narration: `Initialized dp array of size ${amount + 1} with sentinel ${sentinel}. dp[0] = 0.`
      });

      for (let a = 1; a <= amount; a++) {
        for (let coin of coins) {
          if (a >= coin) {
            let prevOpt = dp[a - coin];
            if (prevOpt !== sentinel) {
              let candidate = prevOpt + 1;
              if (candidate < dp[a]) {
                dp[a] = candidate;
                steps.push({
                  amt: a, coin: coin, opt: dp[a],
                  action: `Updated dp[${a}] = ${dp[a]}`,
                  dpArr: [...dp], line: 5,
                  narration: `Amount ${a} using coin ${coin}: 1 + dp[${a - coin}](${prevOpt}) = ${candidate}. New min!`
                });
              }
            }
          }
        }
      }

      let res = (dp[amount] > amount) ? -1 : dp[amount];
      steps.push({
        amt: amount, coin: "Done", opt: res,
        action: (res !== -1) ? `Fewest Coins = ${res}` : "Impossible (-1)",
        dpArr: [...dp], line: 8,
        narration: (res !== -1)
          ? `Complete! Fewest coins to form amount ${amount} is ${res}.`
          : `Amount ${amount} cannot be formed with coins [${coins.join(',')}]. Returned -1.`
      });
    }

    function loadPreset(coins, amount) {
      document.getElementById('custom-input').value = `coins = [${coins.join(',')}], amount = ${amount}`;
      coinSet = coins; targetAmt = amount;
      stepperReset();
      buildSteps(coinSet, targetAmt);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const cMatch = val.match(/\\[([^\\]]+)\\]/);
        const aMatch = val.match(/amount\\s*=\\s*(\\d+)/);
        if (cMatch && aMatch) {
          coinSet = cMatch[1].split(',').map(s => parseInt(s.trim()));
          targetAmt = parseInt(aMatch[1]);
          stepperReset();
          buildSteps(coinSet, targetAmt);
          renderStepper();
        }
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-amt').textContent = step.amt;
      document.getElementById('metric-coin').textContent = step.coin;
      document.getElementById('metric-opt').textContent = step.opt;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('coin-stage');
      stage.innerHTML = '';
      if (!step.dpArr) return;

      const row = document.createElement('div');
      row.className = 'flex flex-wrap items-center justify-center gap-2 max-h-40 overflow-y-auto p-2';

      step.dpArr.forEach((cVal, idx) => {
        const isActive = (idx === step.amt);
        const card = document.createElement('div');
        const isSentinel = (cVal > targetAmt);

        card.className = `flex flex-col items-center justify-center p-2 rounded-lg border min-w-[50px] font-mono ${
          isActive ? 'bg-amber-500/30 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' :
          isSentinel ? 'bg-slate-900 text-slate-600 border-slate-800' :
          'bg-slate-900 text-emerald-300 border-slate-700'
        }`;
        card.innerHTML = `
          <span class="text-[9px] text-slate-500">dp[${idx}]</span>
          <span class="text-xs font-bold">${isSentinel ? '∞' : cVal}</span>
        `;
        row.appendChild(card);
      });

      stage.appendChild(row);
"""
    },
    'approaches': [
        {
            'name': "Top-Down Recursion with Memoization",
            'desc': "Recursively compute `coinChange(amount)` trying all coins, memoizing answers in a map or array.",
            'time': "O(amount * C)",
            'space': "O(amount)",
            'code_kotlin': """class Solution {
    fun coinChange(coins: IntArray, amount: Int): Int {
        val memo = IntArray(amount + 1) { -2 }
        return dfs(amount, coins, memo)
    }

    private fun dfs(amt: Int, coins: IntArray, memo: IntArray): Int {
        if (amt == 0) return 0
        if (amt < 0) return -1
        if (memo[amt] != -2) return memo[amt]

        var minCoins = Int.MAX_VALUE
        for (c in coins) {
            val res = dfs(amt - c, coins, memo)
            if (res >= 0 && res < minCoins) {
                minCoins = res + 1
            }
        }

        memo[amt] = if (minCoins == Int.MAX_VALUE) -1 else minCoins
        return memo[amt]
    }
}""",
            'notes_kotlin': "Top-down memoized recursion. Recursion depth up to `amount`.",
            'code_python': """class Solution:
    def coinChange(self, coins: List[int], amount: int) -> int:
        memo = {}
        def dfs(rem):
            if rem == 0: return 0
            if rem < 0: return -1
            if rem in memo: return memo[rem]
            min_coins = float('inf')
            for c in coins:
                res = dfs(rem - c)
                if res >= 0 and res < min_coins:
                    min_coins = res + 1
            memo[rem] = min_coins if min_coins != float('inf') else -1
            return memo[rem]
        return dfs(amount)""",
            'notes_python': "Top-down recursion with memo dict.",
            'code_java': """class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -2);
        return dfs(amount, coins, memo);
    }

    private int dfs(int rem, int[] coins, int[] memo) {
        if (rem == 0) return 0;
        if (rem < 0) return -1;
        if (memo[rem] != -2) return memo[rem];

        int minCoins = Integer.MAX_VALUE;
        for (int c : coins) {
            int res = dfs(rem - c, coins, memo);
            if (res >= 0 && res < minCoins) {
                minCoins = res + 1;
            }
        }

        memo[rem] = (minCoins == Integer.MAX_VALUE) ? -1 : minCoins;
        return memo[rem];
    }
}""",
            'notes_java': "O(amount) recursion stack."
        },
        {
            'name': "Breadth-First Search (Shortest Path in DAG)",
            'desc': "Model amounts as graph nodes. Multi-level queue BFS finds the shortest path from `amount` down to `0`.",
            'time': "O(amount * C)",
            'space': "O(amount)",
            'code_kotlin': """class Solution {
    fun coinChange(coins: IntArray, amount: Int): Int {
        if (amount == 0) return 0
        val queue = ArrayDeque<Int>()
        val visited = BooleanArray(amount + 1)
        queue.add(amount)
        visited[amount] = true
        var steps = 0

        while (queue.isNotEmpty()) {
            steps++
            val size = queue.size
            for (i in 0 until size) {
                val curr = queue.removeFirst()
                for (c in coins) {
                    val next = curr - c
                    if (next == 0) return steps
                    if (next > 0 && !visited[next]) {
                        visited[next] = true
                        queue.add(next)
                    }
                }
            }
        }
        return -1
    }
}""",
            'notes_kotlin': "Queue BFS shortest path. First path to reach 0 is guaranteed to be minimal.",
            'code_python': """class Solution:
    def coinChange(self, coins: List[int], amount: int) -> int:
        if amount == 0: return 0
        queue = collections.deque([amount])
        visited = {amount}
        steps = 0

        while queue:
            steps += 1
            for _ in range(len(queue)):
                curr = queue.popleft()
                for c in coins:
                    nxt = curr - c
                    if nxt == 0:
                        return steps
                    if nxt > 0 and nxt not in visited:
                        visited.add(nxt)
                        queue.append(nxt)
        return -1""",
            'notes_python': "BFS shortest path on amount state transition graph.",
            'code_java': """class Solution {
    public int coinChange(int[] coins, int amount) {
        if (amount == 0) return 0;
        Queue<Integer> queue = new ArrayDeque<>();
        boolean[] visited = new boolean[amount + 1];
        queue.offer(amount);
        visited[amount] = true;
        int steps = 0;

        while (!queue.isEmpty()) {
            steps++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int curr = queue.poll();
                for (int c : coins) {
                    int next = curr - c;
                    if (next == 0) return steps;
                    if (next > 0 && !visited[next]) {
                        visited[next] = true;
                        queue.offer(next);
                    }
                }
            }
        }
        return -1;
    }
}""",
            'notes_java': "Iterative BFS guaranteed to discover optimal coin count on first hit."
        },
        {
            'name': "Bottom-Up 1D DP Array (Optimal Primary)",
            'desc': "Initialize DP array of size amount + 1 with amount + 1. Set dp[0] = 0. Iterate sub-amounts and coins.",
            'time': "O(amount * C)",
            'space': "O(amount)",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Bottom-Up 1D Array Dynamic Programming
     * Time: O(amount * |coins|) - Inner loop processes each coin per sub-amount
     * Space: O(amount) - Primitive integer array of size amount + 1
     */
    fun coinChange(coins: IntArray, amount: Int): Int {
        val sentinel = amount + 1
        val dp = IntArray(amount + 1) { sentinel }
        dp[0] = 0

        for (a in 1..amount) {
            for (coin in coins) {
                if (a >= coin) {
                    dp[a] = minOf(dp[a], dp[a - coin] + 1)
                }
            }
        }

        return if (dp[amount] > amount) -1 else dp[amount]
    }
}""",
            'notes_kotlin': "Using `amount + 1` as sentinel avoids integer overflow bugs when adding `+ 1`.",
            'code_python': """class Solution:
    # Optimal: Bottom-Up 1D DP Array
    # Time: O(amount * |coins|)
    # Space: O(amount)
    def coinChange(self, coins: List[int], amount: int) -> int:
        dp = [float('inf')] * (amount + 1)
        dp[0] = 0

        for a in range(1, amount + 1):
            for coin in coins:
                if a >= coin:
                    dp[a] = min(dp[a], dp[a - coin] + 1)

        return dp[amount] if dp[amount] != float('inf') else -1""",
            'notes_python': "Canonical Python solution. Fast and clean.",
            'code_java': """class Solution {
    /**
     * Optimal: Bottom-Up 1D DP Array
     * Time: O(amount * |coins|)
     * Space: O(amount)
     */
    public int coinChange(int[] coins, int amount) {
        int sentinel = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, sentinel);
        dp[0] = 0;

        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                if (a >= coin) {
                    dp[a] = Math.min(dp[a], dp[a - coin] + 1);
                }
            }
        }

        return (dp[amount] > amount) ? -1 : dp[amount];
    }
}""",
            'notes_java': "Gold standard production template. Uses sentinel `amount + 1` to safely prevent overflow."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun coinChange(coins: IntArray, amount: Int): Int {
        val max = amount + 1
        val dp = IntArray(amount + 1) { max }
        dp[0] = 0
        for (a in 1..amount) {
            for (c in coins) if (a >= c) dp[a] = minOf(dp[a], dp[a - c] + 1)
        }
        return if (dp[amount] > amount) -1 else dp[amount]
    }
}""",
        'python_boilerplate': """class Solution:
    def coinChange(self, coins: List[int], amount: int) -> int:
        dp = [float('inf')] * (amount + 1)
        dp[0] = 0
        for a in range(1, amount + 1):
            for c in coins:
                if a >= c: dp[a] = min(dp[a], dp[a - c] + 1)
        return dp[amount] if dp[amount] != float('inf') else -1""",
        'java_boilerplate': """class Solution {
    public int coinChange(int[] coins, int amount) {
        int max = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, max);
        dp[0] = 0;
        for (int a = 1; a <= amount; a++) {
            for (int c : coins) {
                if (a >= c) dp[a] = Math.min(dp[a], dp[a - c] + 1);
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }
}""",
        'js_test_runner': """
      const testCases = [
        { coins: [1,2,5], amount: 11, expected: 3 },
        { coins: [2], amount: 3, expected: -1 },
        { coins: [1], amount: 0, expected: 0 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runCoin(coins, amount) {
          let max = amount + 1;
          let dp = new Array(amount + 1).fill(max);
          dp[0] = 0;
          for (let a = 1; a <= amount; a++) {
            for (let c of coins) {
              if (a >= c) dp[a] = Math.min(dp[a], dp[a - c] + 1);
            }
          }
          return dp[amount] > amount ? -1 : dp[amount];
        }

        const out = runCoin(tc.coins, tc.amount);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: coins=[${tc.coins.join(',')}] amt=${tc.amount}</span>
          <span>${passed ? '&#10004; PASSED (' + out + ' coins)' : '&#10008; FAILED (got ' + out + ')'}</span>
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
        'phase1': "I clarify: Can amount be 0? Yes, if amount == 0, return 0. Do we have an infinite supply of each coin denomination? Yes, this is an unbounded knapsack. Can a greedy approach work? No, because coin denominations may not be canonical (e.g. coins=[1,3,4], amount=6: greedy takes 4+1+1=3 coins, while optimal is 3+3=2 coins).",
        'phase2': "For strategy: We use bottom-up dynamic programming. Let `dp[a]` be the minimum coins needed to make amount `a`. We initialize an array `dp` of size `amount + 1` filled with `amount + 1` (a sentinel larger than any possible answer, which avoids integer overflow when adding 1). We set `dp[0] = 0`. For each sub-amount `a` from 1 to `amount`, and for each `coin`: if `a >= coin`, `dp[a] = min(dp[a], dp[a - coin] + 1)`. If `dp[amount] > amount`, return -1; else return `dp[amount]`. Time complexity is O(amount * |coins|) and space is O(amount).",
        'phase3': "I implement `coinChange`: create `dp = IntArray(amount + 1) { amount + 1 }` and `dp[0] = 0`. Nested loop `for (a in 1..amount)` and `for (coin in coins)`: if `a >= coin`, `dp[a] = minOf(dp[a], dp[a - coin] + 1)`. Return `if (dp[amount] > amount) -1 else dp[amount]`.",
        'phase4': "Dry run: coins=[1, 2, 5], amount=11. dp[0]=0, dp[1]=1 (coin 1), dp[2]=1 (coin 2), dp[5]=1 (coin 5). At a=11: tries coin 5 &rarr; dp[6]+1 = 2+1=3 (via 5+1). Output is 3 coins (5+5+1). Edge case amount=0 returns 0. Impossible amount like coins=[2], amount=3 leaves dp[3]=4 > 3, returning -1. Perfect.",
        'senior_followup': "Senior Scalability: If amount is in the billions (e.g. financial clearing systems), DP is too slow. However, by Frobenius coin problem and periodicity of coin change, for large amounts we use a mathematical shortcut: greedily take the largest denomination coin until `amount` falls into a bounded DP window."
    },
    'quiz': [
        {
            'question': "Why does the greedy strategy (always taking the largest coin denomination first) fail for Coin Change?",
            'options': [
                "Because for non-canonical coin systems (e.g. coins = [1, 3, 4] and amount = 6), greedy takes 4 + 1 + 1 (3 coins), whereas the optimal solution is 3 + 3 (2 coins).",
                "Because greedy algorithms only work on sorted arrays.",
                "Because coins can be negative.",
                "Because greedy algorithms cannot be implemented in Java."
            ],
            'correct_index': 0,
            'explanation': "Greedy only works for canonical coin systems (like US currency). For arbitrary denominations, choosing the largest coin can force suboptimal smaller coins later."
        },
        {
            'question': "Why is `amount + 1` preferred over `Integer.MAX_VALUE` as the sentinel value in the DP array?",
            'options': [
                "Because adding 1 to `Integer.MAX_VALUE` (`dp[a - coin] + 1`) causes integer overflow wrap-around to `Integer.MIN_VALUE` (negative numbers), completely breaking the `min` comparison.",
                "Because `amount + 1` takes less memory.",
                "Because Java does not have `Integer.MAX_VALUE`.",
                "To speed up array allocation."
            ],
            'correct_index': 0,
            'explanation': "In 32-bit signed integers, `Integer.MAX_VALUE + 1` overflows to `-2,147,483,648`, which causes `Math.min()` to incorrectly choose the negative overflowed number."
        }
    ]
}
