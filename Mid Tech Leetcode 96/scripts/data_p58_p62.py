import sys
sys.path.append('scripts')

p58 = {
    'num': 58,
    'lc': 238,
    'title': "Product of Array Except Self",
    'category': "Arrays, Strings & Hashing",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "product-of-array-except-self",
    'verbatim_statement': """<p>Given an integer array <code>nums</code>, return <em>an array</em> <code>answer</code> <em>such that</em> <code>answer[i]</code> <em>is equal to the product of all the elements of</em> <code>nums</code> <em>except</em> <code>nums[i]</code>.</p>
<p>The product of any prefix or suffix of <code>nums</code> is <strong>guaranteed</strong> to fit in a <strong>32-bit</strong> integer.</p>
<p>You must write an algorithm that runs in <code>O(n)</code> time and without using the division operation.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [1,2,3,4]', 'output': '[24,12,8,6]'},
        {'title': "Example 2", 'input': 'nums = [-1,1,0,-3,3]', 'output': '[0,0,9,0,0]'}
    ],
    'constraints': [
        "2 <= nums.length <= 10^5",
        "-30 <= nums[i] <= 30",
        "The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer."
    ],
    'plain_english': "For every position i, the result is the product of everything strictly to its left multiplied by the product of everything strictly to its right. We compute prefix products directly into the output array in pass 1, then sweep backwards maintaining a running suffix product in pass 2, achieving O(N) time with O(1) extra space.",
    'boundary_checks': {
        'min_base': "Two elements <code>nums = [3, 4]</code> &rarr; Returns `[4, 3]` immediately.",
        'best_exit': "Single zero <code>nums = [-1, 1, 0, -3, 3]</code> &rarr; Only index of 0 gets non-zero product; all others become 0.",
        'worst_scan': "Multiple zeros <code>nums = [0, 4, 0]</code> &rarr; Every element evaluates to 0."
    },
    'mental_model': {
        'analogy_title': "The Two-Sided Grandstand View",
        'analogy_text': "Imagine standing in a stadium row. You want to know the combined weight of everyone except yourself. You ask the person to your left for the product of everyone from the left edge up to them, and the person to your right for the product from the right edge up to them. You multiply those two numbers together. By sweeping left-to-right first and right-to-left second, everyone gets their answer without any division.",
        'aha_moment': "Any element `nums[i]` splits the array into two disjoint halves: prefix `nums[0..i-1]` and suffix `nums[i+1..n-1]`. Instead of allocating two O(N) arrays for prefix and suffix, we store prefix products directly into the return array `res`, and fold the suffix product into `res[i]` on-the-fly using a single scalar variable `postfix`.",
        'trigger': "Calculate total aggregate excluding current index without division &rarr; Prefix product sweep followed by running suffix accumulator.",
        'sisters': [
            {'name': "Trapping Rain Water (Prefix/Suffix Max)", 'lc': "LC #42"},
            {'name': "Subarray Sum Equals K (Prefix Sum)", 'lc': "LC #560"},
            {'name': "Candy (Two-way Slope Sweep)", 'lc': "LC #135"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(1) Space",
        'naive_text': "Nested loops: for each index i, iterate through all other indices j != i and multiply nums[j].",
        'wasted_work': "Re-multiplying overlapping chains of numbers over and over ($O(N^2)$ scalar multiplications).",
        'optimal_complexity': "O(N) Time, O(1) Auxiliary Space",
        'optimal_text': "Two linear passes: Left-to-right pass writes prefix products into output array; right-to-left pass accumulates running suffix scalar into output.",
        'core_invariant': "After prefix pass, `res[i] = prod(nums[0..i-1])`. During suffix pass at index i, `res[i] = res[i] * postfix`, where `postfix = prod(nums[i+1..n-1])`.",
        'table_rows': [
            {'name': "1. Brute Force (Nested Loops)", 'time': "O(N^2)", 'space': "O(1)", 'note': "Times out on N = 10^5 ($10^{10}$ ops)."},
            {'name': "2. Prefix & Suffix Arrays", 'time': "O(N)", 'space': "O(N)", 'note': "Allocates two auxiliary arrays (2N integers)."},
            {'name': "★ 3. In-Place Output Accumulator", 'time': "O(N)", 'space': "O(1) Auxiliary", 'note': "Optimal cache locality; output array does not count as extra space per problem spec."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [1, 2, 3, 4]',
        'columns': ["Pass", "Index i", "nums[i]", "Running Multiplier", "res Array State", "Action Summary"],
        'rows': [
            ["Pass 1 (Prefix)", "i = 0", "1", "prefix = 1", "[1, -, -, -]", "res[0] = 1; prefix *= 1 &rarr; 1"],
            ["Pass 1 (Prefix)", "i = 1", "2", "prefix = 1", "[1, 1, -, -]", "res[1] = 1; prefix *= 2 &rarr; 2"],
            ["Pass 1 (Prefix)", "i = 2", "3", "prefix = 2", "[1, 1, 2, -]", "res[2] = 2; prefix *= 3 &rarr; 6"],
            ["Pass 1 (Prefix)", "i = 3", "4", "prefix = 6", "[1, 1, 2, 6]", "res[3] = 6; prefix *= 4 &rarr; 24"],
            ["Pass 2 (Suffix)", "i = 3", "4", "postfix = 1", "[1, 1, 2, 6 * 1 = 6]", "res[3] *= 1; postfix *= 4 &rarr; 4"],
            ["Pass 2 (Suffix)", "i = 2", "3", "postfix = 4", "[1, 1, 2 * 4 = 8, 6]", "res[2] *= 4; postfix *= 3 &rarr; 12"],
            ["Pass 2 (Suffix)", "i = 1", "2", "postfix = 12", "[1, 1 * 12 = 12, 8, 6]", "res[1] *= 12; postfix *= 2 &rarr; 24"],
            ["Pass 2 (Suffix)", "i = 0", "1", "postfix = 24", "[1 * 24 = 24, 12, 8, 6]", "res[0] *= 24; complete"]
        ],
        'takeaway': "Final result `[24, 12, 8, 6]` computed in 2N total operations with strictly O(1) auxiliary memory."
    },
    'stepper': {
        'title': "Two-Pass Prefix / Suffix Product Stepper",
        'default_input': '1, 2, 3, 4',
        'presets': [
            {'label': "Canonical [1, 2, 3, 4]", 'onclick': 'loadPreset([1, 2, 3, 4])'},
            {'label': "With Zero [-1, 1, 0, -3, 3]", 'onclick': 'loadPreset([-1, 1, 0, -3, 3])'},
            {'label': "Two Items [5, 2]", 'onclick': 'loadPreset([5, 2])'}
        ],
        'metrics': [
            {'id': "metric-phase", 'label': "Current Phase", 'color': "text-amber-400", 'initial': "Pass 1: Prefix"},
            {'id': "metric-idx", 'label': "Active Index i", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-mult", 'label': "Running Multiplier", 'color': "text-purple-400", 'initial': "1"},
            {'id': "metric-curr-res", 'label': "Output Slot res[i]", 'color': "text-emerald-400", 'initial': "-"}
        ],
        'visual_html': '<div id="p238-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to observe prefix accumulation followed by reverse suffix folding.',
        'pseudocode_lines': [
            "val res = IntArray(nums.size) { 1 }",
            "var prefix = 1",
            "for (i in nums.indices) { res[i] = prefix; prefix *= nums[i] }",
            "var postfix = 1",
            "for (i in nums.indices.reversed()) { res[i] *= postfix; postfix *= nums[i] }",
            "return res"
        ],
        'js_code': """
    let rawArr = [1, 2, 3, 4];

    function buildSteps(arr) {
      steps = [];
      const n = arr.length;
      const res = new Array(n).fill(1);

      steps.push({
        phase: "Init", idx: "-", mult: 1, currRes: "-",
        activeIdx: -1, resArr: [...res], origArr: [...arr], line: 1,
        narration: `Initialized output array of size ${n} with 1s.`
      });

      let prefix = 1;
      for (let i = 0; i < n; i++) {
        res[i] = prefix;
        steps.push({
          phase: "Pass 1 (Prefix &rarr;)", idx: i, mult: prefix, currRes: res[i],
          activeIdx: i, resArr: [...res], origArr: [...arr], line: 3,
          narration: `Prefix pass at i=${i}: stored prefix=${prefix} into res[${i}]. Updated prefix = ${prefix} * ${arr[i]} = ${prefix * arr[i]}.`
        });
        prefix *= arr[i];
      }

      let postfix = 1;
      for (let i = n - 1; i >= 0; i--) {
        const oldVal = res[i];
        res[i] *= postfix;
        steps.push({
          phase: "Pass 2 (&larr; Suffix)", idx: i, mult: postfix, currRes: res[i],
          activeIdx: i, resArr: [...res], origArr: [...arr], line: 5,
          narration: `Suffix pass at i=${i}: multiplied res[${i}] (${oldVal}) by postfix (${postfix}) = ${res[i]}. Updated postfix = ${postfix} * ${arr[i]} = ${postfix * arr[i]}.`
        });
        postfix *= arr[i];
      }

      steps.push({
        phase: "Complete", idx: "All", mult: "-", currRes: "Done",
        activeIdx: -1, resArr: [...res], origArr: [...arr], line: 6,
        narration: `Final result produced: [${res.join(', ')}] in O(N) time with zero division.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p238-stage');
      if (!stage) return;
      let html = '<div class="flex items-center gap-2"><span class="text-xs font-mono text-slate-400 w-16">nums:</span>';
      s.origArr.forEach((v, idx) => {
        const isAct = idx === s.activeIdx;
        html += `<div class="w-12 h-10 rounded border flex items-center justify-center font-mono text-xs font-bold ${isAct ? 'bg-cyan-500/20 border-cyan-400 text-cyan-300' : 'bg-slate-900 border-slate-700 text-slate-300'}">${v}</div>`;
      });
      html += '</div>';

      html += '<div class="flex items-center gap-2"><span class="text-xs font-mono text-slate-400 w-16">res:</span>';
      s.resArr.forEach((v, idx) => {
        const isAct = idx === s.activeIdx;
        html += `<div class="w-12 h-10 rounded border flex items-center justify-center font-mono text-xs font-bold ${isAct ? 'bg-emerald-500/20 border-emerald-400 text-emerald-300 ring-2 ring-emerald-500/50' : 'bg-slate-900 border-slate-700 text-slate-200'}">${v}</div>`;
      });
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-phase').textContent = s.phase;
      document.getElementById('metric-idx').textContent = s.idx;
      document.getElementById('metric-mult').textContent = s.mult;
      document.getElementById('metric-curr-res').textContent = s.currRes;
    }

    function loadPreset(arr) {
      rawArr = [...arr];
      buildSteps(rawArr);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Brute Force (O(N^2))",
            'badge': "Naive",
            'color': "rose",
            'complexity': "O(N^2) Time, O(1) Space",
            'summary': "Iterate through every index i, and for every i, run an inner loop over all j != i multiplying nums[j].",
            'kotlin_code': """class Solution {
    fun productExceptSelf(nums: IntArray): IntArray {
        val n = nums.size
        val res = IntArray(n)
        for (i in 0 until n) {
            var prod = 1
            for (j in 0 until n) {
                if (i != j) {
                    prod *= nums[j]
                }
            }
            res[i] = prod
        }
        return res
    }
}""",
            'python_code': """class Solution:
    def productExceptSelf(self, nums: list[int]) -> list[int]:
        n = len(nums)
        res = [1] * n
        for i in range(n):
            prod = 1
            for j in range(n):
                if i != j:
                    prod *= nums[j]
            res[i] = prod
        return res""",
            'java_code': """class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int prod = 1;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    prod *= nums[j];
                }
            }
            res[i] = prod;
        }
        return res;
    }
}""",
            'memory_breakdown': "O(1) auxiliary memory beyond the output array. However, runtime is quadratic O(N^2). For N = 100,000, 10^10 operations trigger a Time Limit Exceeded error on modern judges."
        },
        {
            'name': "Approach 2: Separate Prefix & Suffix Arrays (O(N) Space)",
            'badge': "Better",
            'color': "cyan",
            'complexity': "O(N) Time, O(N) Space",
            'summary': "Allocate prefix[N] and suffix[N] arrays. Precompute all prefix and suffix products in linear time, then set res[i] = prefix[i-1] * suffix[i+1].",
            'kotlin_code': """class Solution {
    fun productExceptSelf(nums: IntArray): IntArray {
        val n = nums.size
        val prefix = IntArray(n)
        val suffix = IntArray(n)
        val res = IntArray(n)

        prefix[0] = nums[0]
        for (i in 1 until n) {
            prefix[i] = prefix[i - 1] * nums[i]
        }

        suffix[n - 1] = nums[n - 1]
        for (i in n - 2 downTo 0) {
            suffix[i] = suffix[i + 1] * nums[i]
        }

        for (i in 0 until n) {
            val left = if (i > 0) prefix[i - 1] else 1
            val right = if (i < n - 1) suffix[i + 1] else 1
            res[i] = left * right
        }
        return res
    }
}""",
            'python_code': """class Solution:
    def productExceptSelf(self, nums: list[int]) -> list[int]:
        n = len(nums)
        prefix = [0] * n
        suffix = [0] * n
        res = [0] * n

        prefix[0] = nums[0]
        for i in range(1, n):
            prefix[i] = prefix[i - 1] * nums[i]

        suffix[-1] = nums[-1]
        for i in range(n - 2, -1, -1):
            suffix[i] = suffix[i + 1] * nums[i]

        for i in range(n):
            left = prefix[i - 1] if i > 0 else 1
            right = suffix[i + 1] if i < n - 1 else 1
            res[i] = left * right

        return res""",
            'java_code': """class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] prefix = new int[n];
        int[] suffix = new int[n];
        int[] res = new int[n];

        prefix[0] = nums[0];
        for (int i = 1; i < n; i++) {
            prefix[i] = prefix[i - 1] * nums[i];
        }

        suffix[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            suffix[i] = suffix[i + 1] * nums[i];
        }

        for (int i = 0; i < n; i++) {
            int left = (i > 0) ? prefix[i - 1] : 1;
            int right = (i < n - 1) ? suffix[i + 1] : 1;
            res[i] = left * right;
        }
        return res;
    }
}""",
            'memory_breakdown': "O(N) extra space: creates two auxiliary int arrays of size N. While O(N) time is optimal, it incurs unnecessary GC overhead and cache pollution compared to in-place accumulation."
        },
        {
            'name': "★ Approach 3: Two-Pass Output Array Accumulation (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(1) Auxiliary Space",
            'summary': "Store prefix products directly in res. Sweep backwards folding in a rolling suffix product scalar. Achieves strict O(1) auxiliary space.",
            'kotlin_code': """class Solution {
    fun productExceptSelf(nums: IntArray): IntArray {
        val n = nums.size
        val res = IntArray(n) { 1 }

        // Pass 1: Build prefix products directly into res
        var prefix = 1
        for (i in 0 until n) {
            res[i] = prefix
            prefix *= nums[i]
        }

        // Pass 2: Multiply by running suffix product backwards
        var postfix = 1
        for (i in n - 1 downTo 0) {
            res[i] *= postfix
            postfix *= nums[i]
        }

        return res
    }
}""",
            'python_code': """class Solution:
    def productExceptSelf(self, nums: list[int]) -> list[int]:
        n = len(nums)
        res = [1] * n

        # Pass 1: Prefix accumulation
        prefix = 1
        for i in range(n):
            res[i] = prefix
            prefix *= nums[i]

        # Pass 2: Suffix accumulation
        postfix = 1
        for i in range(n - 1, -1, -1):
            res[i] *= postfix
            postfix *= nums[i]

        return res""",
            'java_code': """class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];

        // Pass 1: Prefix accumulation
        int prefix = 1;
        for (int i = 0; i < n; i++) {
            res[i] = prefix;
            prefix *= nums[i];
        }

        // Pass 2: Suffix accumulation
        int postfix = 1;
        for (int i = n - 1; i >= 0; i--) {
            res[i] *= postfix;
            postfix *= nums[i];
        }

        return res;
    }
}""",
            'memory_breakdown': "Strictly O(1) auxiliary space (output array is excluded per problem description). Exactly two scalar variables (prefix, postfix). Linear scan with optimal CPU L1 cache prefetching."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun productExceptSelf(nums: IntArray): IntArray {
        // Write your optimal O(N) solution here
        val n = nums.size
        val res = IntArray(n) { 1 }
        var prefix = 1
        for (i in 0 until n) {
            res[i] = prefix
            prefix *= nums[i]
        }
        var postfix = 1
        for (i in n - 1 downTo 0) {
            res[i] *= postfix
            postfix *= nums[i]
        }
        return res
    }
}""",
        'python_boilerplate': """class Solution:
    def productExceptSelf(self, nums: list[int]) -> list[int]:
        # Write your optimal O(N) solution here
        n = len(nums)
        res = [1] * n
        prefix = 1
        for i in range(n):
            res[i] = prefix
            prefix *= nums[i]
        postfix = 1
        for i in range(n - 1, -1, -1):
            res[i] *= postfix
            postfix *= nums[i]
        return res""",
        'java_boilerplate': """class Solution {
    public int[] productExceptSelf(int[] nums) {
        // Write your optimal O(N) solution here
        int n = nums.length;
        int[] res = new int[n];
        int prefix = 1;
        for (int i = 0; i < n; i++) {
            res[i] = prefix;
            prefix *= nums[i];
        }
        int postfix = 1;
        for (int i = n - 1; i >= 0; i--) {
            res[i] *= postfix;
            postfix *= nums[i];
        }
        return res;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(nums) {
        const n = nums.length;
        const res = new Array(n).fill(1);
        let prefix = 1;
        for (let i = 0; i < n; i++) {
          res[i] = prefix;
          prefix *= nums[i];
        }
        let postfix = 1;
        for (let i = n - 1; i >= 0; i--) {
          res[i] *= postfix;
          postfix *= nums[i];
        }
        return res;
      }

      const tests = [
        { input: [1,2,3,4], expected: [24,12,8,6] },
        { input: [-1,1,0,-3,3], expected: [0,0,9,0,0] },
        { input: [4,5], expected: [5,4] }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.input);
        const pass = JSON.stringify(actual) === JSON.stringify(t.expected);
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: nums=[${t.input}] &rarr; Got [${actual}], Expected [${t.expected}] (${pass ? 'PASSED' : 'FAILED'})`;
        resultsDiv.appendChild(row);
      });

      if (allPassed) {
        statusSpan.textContent = "All test cases passed cleanly!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "\"First, let me clarify the constraints: Can nums contain negative numbers or zeros? Yes, values range from -30 to 30. Division is explicitly forbidden, and we are aiming for an O(N) time solution with O(1) auxiliary space.\"",
        'phase2': "\"For any element nums[i], the answer is the product of all elements to its left times the product of all elements to its right. Instead of calculating prefix and suffix in separate O(N) arrays, I will do this in two passes: first pass populates prefix products directly into the output array. Second pass sweeps backwards using a single scalar variable `postfix` that folds into the output array on the fly. This gives O(N) time and O(1) extra space.\"",
        'phase3': "\"Let me write the code. I initialize `res` of size N with 1s and set `prefix = 1`. In the forward loop, `res[i] = prefix` and `prefix *= nums[i]`. Next, I set `postfix = 1` and loop from N-1 down to 0, setting `res[i] *= postfix` and `postfix *= nums[i]`. Finally, return `res`.\"",
        'phase4': "\"Now let me trace with `[1, 2, 3, 4]`. After pass 1, res is `[1, 1, 2, 6]`. In pass 2, at i=3: res[3] becomes 6*1=6, postfix becomes 4. At i=2: res[2] becomes 2*4=8, postfix becomes 12. At i=1: res[1] becomes 1*12=12, postfix becomes 24. At i=0: res[0] becomes 1*24=24. Result is `[24, 12, 8, 6]`. For edge cases: when an array contains zeros, prefix and postfix handle it naturally without ZeroDivisionError.\"",
        'senior_followup': "\"In a high-throughput stream or distributed partition setting where nums does not fit in memory, we can compute prefix and suffix products using parallel associative scans (MapReduce prefix scan pattern) with O(log P) communication latency.\""
    },
    'quiz': [
        {
            'question': "Why is using total product divided by nums[i] not an acceptable general solution?",
            'options': [
                "Division operation is explicitly forbidden and fails whenever nums has zeros",
                "Division operation requires floating point hardware that slows CPU execution",
                "Division operation changes the Big-O time complexity from linear to quadratic",
                "Division operation cannot handle negative integers without parity bit inversion"
            ],
            'correct_index': 0,
            'explanation': "The problem explicitly forbids division. Furthermore, division by zero crashes if nums contains 0, and if nums contains multiple 0s, all answers must be 0."
        },
        {
            'question': "What is the auxiliary space complexity of the two-pass accumulator approach?",
            'options': [
                "O(1) auxiliary space because the output array is excluded by problem spec",
                "O(N) auxiliary space because the returned array takes memory on heap",
                "O(log N) auxiliary space because of recursive execution call stacks",
                "O(N^2) auxiliary space because of intermediate multiplication buffers"
            ],
            'correct_index': 0,
            'explanation': "Per standard interview conventions and problem description, the returned result array does not count as auxiliary space. Only two scalar variables (prefix and postfix) are maintained."
        }
    ]
}

p59 = {
    'num': 59,
    'lc': 128,
    'title': "Longest Consecutive Sequence",
    'category': "Arrays, Strings & Hashing",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "longest-consecutive-sequence",
    'verbatim_statement': """<p>Given an unsorted array of integers <code>nums</code>, return <em>the length of the longest consecutive elements sequence.</em></p>
<p>You must write an algorithm that runs in <code>O(n)</code> time.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [100,4,200,1,3,2]', 'output': '4\nExplanation: The longest consecutive elements sequence is [1, 2, 3, 4]. Therefore its length is 4.'},
        {'title': "Example 2", 'input': 'nums = [0,3,7,2,5,8,4,6,0,1]', 'output': '9\nExplanation: The sequence is [0, 1, 2, 3, 4, 5, 6, 7, 8]. Length is 9.'}
    ],
    'constraints': [
        "0 <= nums.length <= 10^5",
        "-10^9 <= nums[i] <= 10^9"
    ],
    'plain_english': "Insert all numbers into a HashSet for O(1) membership lookup. A number is the beginning of a consecutive streak if and only if `num - 1` is NOT present in the set. For every streak leader, count forward `num + 1, num + 2, ...` to determine streak length. Because each element is visited at most twice, total runtime is strictly O(N).",
    'boundary_checks': {
        'min_base': "Empty array <code>nums = []</code> &rarr; Returns 0.",
        'best_exit': "Single element <code>nums = [10]</code> &rarr; Returns 1.",
        'worst_scan': "All duplicates <code>nums = [5, 5, 5, 5]</code> &rarr; Set deduplicates to `{5}`, streak length = 1."
    },
    'mental_model': {
        'analogy_title': "Finding the Head of the Conga Line",
        'analogy_text': "Imagine a crowded party with people wearing numbers. If you want to find the longest chain of consecutive numbers, don't start counting from someone if their predecessor is already standing behind them. Only start counting if you are looking at the head of a line (`num - 1` does not exist). From that head, march forward counting consecutive people until the chain breaks.",
        'aha_moment': "Checking `num - 1 not in set` filters out 90%+ of numbers from initiating a count. Each number is visited once during set insertion and at most once while expanding a sequence. This guarantees O(N) linear time despite the nested while loop.",
        'trigger': "Unsorted array, find longest consecutive sequence in O(N) time &rarr; HashSet with sequence-start filtering (`!set.contains(n - 1)`).",
        'sisters': [
            {'name': "Missing Number", 'lc': "LC #268"},
            {'name': "Find First Missing Positive", 'lc': "LC #41"},
            {'name': "Binary Tree Longest Consecutive Sequence", 'lc': "LC #298"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N log N) Time, O(1) or O(N) Space",
        'naive_text': "Sort the array and scan linearly, incrementing a streak counter whenever nums[i] == nums[i-1] + 1.",
        'wasted_work': "Sorting takes O(N log N) which violates the strict O(N) requirement in the problem description.",
        'optimal_complexity': "O(N) Time, O(N) Auxiliary Space",
        'optimal_text': "HashSet lookup: populate set in O(N). Check streak start in O(1). Each streak member visited exactly once.",
        'core_invariant': "A sequence expansion only starts at `num` if `!numSet.contains(num - 1)`. Total while-loop iterations across all numbers sum to at most N.",
        'table_rows': [
            {'name': "1. Brute Force Search", 'time': "O(N^3)", 'space': "O(1)", 'note': "For each element, sequentially search array for n+1, n+2."},
            {'name': "2. Sorting Baseline", 'time': "O(N log N)", 'space': "O(1) to O(N)", 'note': "Sorts array; violates the explicit O(N) requirement."},
            {'name': "★ 3. HashSet Sequence-Start Scan", 'time': "O(N)", 'space': "O(N)", 'note': "Optimal linear time amortized; avoids sorting."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [100, 4, 200, 1, 3, 2]',
        'columns': ["Element n", "Is Start? (!contains(n-1))", "Counting Sequence", "Streak Length", "Global Max Streak"],
        'rows': [
            ["100", "True (99 not in set)", "100 &rarr; done", "1", "max(0, 1) = 1"],
            ["4", "False (3 in set)", "Skipped (not a start)", "-", "1"],
            ["200", "True (199 not in set)", "200 &rarr; done", "1", "max(1, 1) = 1"],
            ["1", "True (0 not in set)", "1 &rarr; 2 &rarr; 3 &rarr; 4", "4", "max(1, 4) = 4"],
            ["3", "False (2 in set)", "Skipped (not a start)", "-", "4"],
            ["2", "False (1 in set)", "Skipped (not a start)", "-", "4"]
        ],
        'takeaway': "Only numbers 100, 200, and 1 initiated counts. Sequence starting at 1 reached length 4."
    },
    'stepper': {
        'title': "HashSet Sequence-Start Stepper",
        'default_input': '100, 4, 200, 1, 3, 2',
        'presets': [
            {'label': "Example [100, 4, 200, 1, 3, 2]", 'onclick': 'loadPreset([100, 4, 200, 1, 3, 2])'},
            {'label': "Overlapping [0,3,7,2,5,8,4,6,0,1]", 'onclick': 'loadPreset([0,3,7,2,5,8,4,6,0,1])'},
            {'label': "Single [10]", 'onclick': 'loadPreset([10])'}
        ],
        'metrics': [
            {'id': "metric-curr-num", 'label': "Inspecting Num", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-is-start", 'label': "Is Sequence Head?", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-streak", 'label': "Current Streak", 'color': "text-purple-400", 'initial': "0"},
            {'id': "metric-max-len", 'label': "Max Streak Length", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p128-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to observe how only sequence heads trigger expansion.',
        'pseudocode_lines': [
            "val set = nums.toHashSet()",
            "var maxLen = 0",
            "for (n in set) {",
            "    if (!set.contains(n - 1)) {",
            "        var curr = n; var length = 1",
            "        while (set.contains(curr + 1)) { curr++; length++ }",
            "        maxLen = maxOf(maxLen, length)",
            "    }",
            "}",
            "return maxLen"
        ],
        'js_code': """
    let rawArr = [100, 4, 200, 1, 3, 2];

    function buildSteps(arr) {
      steps = [];
      const set = new Set(arr);
      let maxLen = 0;
      const uniqueArr = Array.from(set);

      steps.push({
        num: "-", isStart: "-", streak: 0, maxLen: 0,
        activeNum: null, streakNums: [], line: 1,
        narration: `Populated HashSet with ${uniqueArr.length} unique elements: {${uniqueArr.join(', ')}}.`
      });

      for (let n of uniqueArr) {
        const isStart = !set.has(n - 1);
        if (!isStart) {
          steps.push({
            num: n, isStart: "No (n-1 in set)", streak: "-", maxLen: maxLen,
            activeNum: n, streakNums: [], line: 4,
            narration: `Examining ${n}: predecessor ${n - 1} exists in set. Skipping (not a streak head).`
          });
        } else {
          let curr = n;
          let length = 1;
          const streakNums = [curr];

          steps.push({
            num: n, isStart: "YES (Streak Head)", streak: 1, maxLen: maxLen,
            activeNum: n, streakNums: [...streakNums], line: 5,
            narration: `Found streak head ${n} (${n - 1} not in set). Starting count at length 1.`
          });

          while (set.has(curr + 1)) {
            curr++;
            length++;
            streakNums.push(curr);
            steps.push({
              num: n, isStart: "Expanding...", streak: length, maxLen: maxLen,
              activeNum: n, streakNums: [...streakNums], line: 6,
              narration: `Found consecutive successor ${curr}. Streak expanded to length ${length}: [${streakNums.join(' &rarr; ')}].`
            });
          }

          if (length > maxLen) maxLen = length;
          steps.push({
            num: n, isStart: "Finished Streak", streak: length, maxLen: maxLen,
            activeNum: n, streakNums: [...streakNums], line: 7,
            narration: `Streak starting at ${n} ended. Max length updated to ${maxLen}.`
          });
        }
      }

      steps.push({
        num: "All", isStart: "Complete", streak: maxLen, maxLen: maxLen,
        activeNum: null, streakNums: [], line: 10,
        narration: `Evaluation complete. Longest consecutive sequence has length ${maxLen}.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p128-stage');
      if (!stage) return;
      const set = Array.from(new Set(rawArr)).sort((a,b)=>a-b);
      let html = '<div class="flex flex-wrap items-center justify-center gap-2">';
      set.forEach(val => {
        const isStreak = s.streakNums.includes(val);
        const isHead = val === s.activeNum;
        let cls = 'bg-slate-900 border-slate-700 text-slate-300';
        if (isHead) cls = 'bg-cyan-500/30 border-cyan-400 text-cyan-200 ring-2 ring-cyan-500';
        else if (isStreak) cls = 'bg-emerald-500/20 border-emerald-400 text-emerald-300';
        html += `<div class="px-3 py-2 rounded-lg border font-mono text-xs font-bold ${cls}">${val}</div>`;
      });
      html += '</div>';

      if (s.streakNums.length > 0) {
        html += `<div class="text-xs font-mono text-emerald-400 font-bold bg-slate-950/60 px-4 py-2 rounded-lg border border-emerald-800/60">Active Chain: ${s.streakNums.join(' &rarr; ')} (Length: ${s.streakNums.length})</div>`;
      }
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-curr-num').textContent = s.num;
      document.getElementById('metric-is-start').textContent = s.isStart;
      document.getElementById('metric-streak').textContent = s.streak;
      document.getElementById('metric-max-len').textContent = s.maxLen;
    }

    function loadPreset(arr) {
      rawArr = [...arr];
      buildSteps(rawArr);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Brute Force Search (O(N^3))",
            'badge': "Naive",
            'color': "rose",
            'complexity': "O(N^3) Time, O(1) Space",
            'summary': "For every number n, sequentially search the array to see if n+1 exists. If so, search for n+2, and so forth.",
            'kotlin_code': """class Solution {
    fun longestConsecutive(nums: IntArray): Int {
        var maxLen = 0
        for (num in nums) {
            var curr = num
            var count = 1
            while (nums.contains(curr + 1)) {
                curr++
                count++
            }
            maxLen = maxOf(maxLen, count)
        }
        return maxLen
    }
}""",
            'python_code': """class Solution:
    def longestConsecutive(self, nums: list[int]) -> list[int]:
        max_len = 0
        for num in nums:
            curr = num
            count = 1
            while (curr + 1) in nums:
                curr += 1
                count += 1
            max_len = max(max_len, count)
        return max_len""",
            'java_code': """class Solution {
    public int longestConsecutive(int[] nums) {
        int maxLen = 0;
        for (int num : nums) {
            int curr = num;
            int count = 1;
            while (contains(nums, curr + 1)) {
                curr++;
                count++;
            }
            maxLen = Math.max(maxLen, count);
        }
        return maxLen;
    }
    private boolean contains(int[] nums, int target) {
        for (int n : nums) if (n == target) return true;
        return false;
    }
}""",
            'memory_breakdown': "O(1) auxiliary space, but terrible cubic O(N^3) runtime due to repeated linear scans for each consecutive step."
        },
        {
            'name': "Approach 2: Sorting Baseline (O(N log N))",
            'badge': "Better",
            'color': "cyan",
            'complexity': "O(N log N) Time, O(1) Space",
            'summary': "Sort the array in-place. Traverse linearly, skipping duplicate adjacent elements and incrementing the count when nums[i] == nums[i-1] + 1.",
            'kotlin_code': """class Solution {
    fun longestConsecutive(nums: IntArray): Int {
        if (nums.isEmpty()) return 0
        nums.sort()
        var maxLen = 1
        var currLen = 1

        for (i in 1 until nums.size) {
            if (nums[i] == nums[i - 1]) continue
            if (nums[i] == nums[i - 1] + 1) {
                currLen++
                maxLen = maxOf(maxLen, currLen)
            } else {
                currLen = 1
            }
        }
        return maxLen
    }
}""",
            'python_code': """class Solution:
    def longestConsecutive(self, nums: list[int]) -> int:
        if not nums:
            return 0
        nums.sort()
        max_len = 1
        curr_len = 1
        for i in range(1, len(nums)):
            if nums[i] == nums[i - 1]:
                continue
            if nums[i] == nums[i - 1] + 1:
                curr_len += 1
                max_len = max(max_len, curr_len)
            else:
                curr_len = 1
        return max_len""",
            'java_code': """import java.util.Arrays;

class Solution {
    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) return 0;
        Arrays.sort(nums);
        int maxLen = 1;
        int currLen = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1]) continue;
            if (nums[i] == nums[i - 1] + 1) {
                currLen++;
                maxLen = Math.max(maxLen, currLen);
            } else {
                currLen = 1;
            }
        }
        return maxLen;
    }
}""",
            'memory_breakdown': "O(1) extra space if primitive sort is done in-place, but fails the interview's strict O(N) linear time constraint."
        },
        {
            'name': "★ Approach 3: HashSet Sequence Head Expansion (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(N) Space",
            'summary': "Convert array to a HashSet. For every element, check if num - 1 is present. If not, expand sequence forward. Each number is touched at most twice.",
            'kotlin_code': """class Solution {
    fun longestConsecutive(nums: IntArray): Int {
        val numSet = nums.toHashSet()
        var maxLen = 0

        for (num in numSet) {
            // Only initiate a streak if num is the start of a sequence
            if (!numSet.contains(num - 1)) {
                var currentNum = num
                var currentStreak = 1

                while (numSet.contains(currentNum + 1)) {
                    currentNum++
                    currentStreak++
                }

                maxLen = maxOf(maxLen, currentStreak)
            }
        }
        return maxLen
    }
}""",
            'python_code': """class Solution:
    def longestConsecutive(self, nums: list[int]) -> int:
        num_set = set(nums)
        max_len = 0

        for num in num_set:
            # Check if num is the start of a sequence
            if (num - 1) not in num_set:
                current_num = num
                current_streak = 1

                while (current_num + 1) in num_set:
                    current_num += 1
                    current_streak += 1

                max_len = max(max_len, current_streak)

        return max_len""",
            'java_code': """import java.util.HashSet;
import java.util.Set;

class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }

        int maxLen = 0;
        for (int num : numSet) {
            // Check if num is the start of a sequence
            if (!numSet.contains(num - 1)) {
                int currentNum = num;
                int currentStreak = 1;

                while (numSet.contains(currentNum + 1)) {
                    currentNum++;
                    currentStreak++;
                }

                maxLen = Math.max(maxLen, currentStreak);
            }
        }
        return maxLen;
    }
}""",
            'memory_breakdown': "O(N) space to store unique elements in a HashSet. Average O(1) lookup time per element. Total while loop iterations across the entire set is at most N, ensuring strict O(N) overall time."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun longestConsecutive(nums: IntArray): Int {
        // Write your optimal O(N) solution here
        val numSet = nums.toHashSet()
        var maxLen = 0
        for (num in numSet) {
            if (!numSet.contains(num - 1)) {
                var curr = num
                var streak = 1
                while (numSet.contains(curr + 1)) {
                    curr++
                    streak++
                }
                maxLen = maxOf(maxLen, streak)
            }
        }
        return maxLen
    }
}""",
        'python_boilerplate': """class Solution:
    def longestConsecutive(self, nums: list[int]) -> int:
        # Write your optimal O(N) solution here
        num_set = set(nums)
        max_len = 0
        for num in num_set:
            if (num - 1) not in num_set:
                curr = num
                streak = 1
                while (curr + 1) in num_set:
                    curr += 1
                    streak += 1
                max_len = max(max_len, streak)
        return max_len""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public int longestConsecutive(int[] nums) {
        // Write your optimal O(N) solution here
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);
        int maxLen = 0;
        for (int n : set) {
            if (!set.contains(n - 1)) {
                int curr = n;
                int streak = 1;
                while (set.contains(curr + 1)) {
                    curr++;
                    streak++;
                }
                maxLen = Math.max(maxLen, streak);
            }
        }
        return maxLen;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(nums) {
        const set = new Set(nums);
        let maxLen = 0;
        for (const num of set) {
          if (!set.has(num - 1)) {
            let curr = num;
            let streak = 1;
            while (set.has(curr + 1)) {
              curr++;
              streak++;
            }
            if (streak > maxLen) maxLen = streak;
          }
        }
        return maxLen;
      }

      const tests = [
        { input: [100,4,200,1,3,2], expected: 4 },
        { input: [0,3,7,2,5,8,4,6,0,1], expected: 9 },
        { input: [], expected: 0 },
        { input: [7], expected: 1 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.input);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: nums=[${t.input}] &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
        resultsDiv.appendChild(row);
      });

      if (allPassed) {
        statusSpan.textContent = "All test cases passed cleanly!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "\"First, let me clarify the constraints: The array is unsorted and may contain duplicates, negative values, or be empty. We must achieve O(N) time, which rules out conventional O(N log N) sorting.\"",
        'phase2': "\"To achieve O(N) time without sorting, we can store all elements in a HashSet. The key observation is that any consecutive sequence has a unique beginning: a number whose predecessor `num - 1` is NOT in the set. For every element, we check if `num - 1` exists; if it does, we skip it because it is part of a streak counted elsewhere. If it does not exist, we count forward with a while loop as long as `curr + 1` exists in the set. Even though there is a nested loop, each number is only checked a constant number of times, giving O(N) overall time.\"",
        'phase3': "\"Let me write the code. I build the set from nums. I loop over the set: if `num - 1` is not in set, I set `curr = num` and `streak = 1`. Then `while (set.contains(curr + 1)) { curr++; streak++; }`. I update `maxLen = max(maxLen, streak)` and return `maxLen` at the end.\"",
        'phase4': "\"Tracing with `[100, 4, 200, 1, 3, 2]`. 100 has no 99, streak=1. 4 has 3, skipped. 200 has no 199, streak=1. 1 has no 0: counts 1, 2, 3, 4, streak=4. 3 and 2 are skipped. Max is 4. Empty array returns 0 cleanly. All edge cases handled.\"",
        'senior_followup': "\"If the dataset cannot fit into a single machine's memory, we can distribute numbers across a cluster using consistent hashing (range partitioning or modulo shard) and find consecutive components using a distributed Disjoint Set / Union-Find graph across partition boundaries.\""
    },
    'quiz': [
        {
            'question': "Why does the nested while loop NOT cause O(N^2) quadratic runtime?",
            'options': [
                "Only numbers without a predecessor in the set trigger the while loop",
                "The HashSet automatically sorts elements upon insertion behind the scenes",
                "The inner while loop terminates immediately after checking two items",
                "The garbage collector optimizes repeated contains queries in CPU cache"
            ],
            'correct_index': 0,
            'explanation': "The guard condition `!set.contains(num - 1)` ensures that sequence expansion only runs once per consecutive chain. Every element in the array is traversed at most twice (once in outer loop, once in inner while loop), yielding strict O(N) time."
        },
        {
            'question': "What is the primary trade-off of the optimal HashSet solution over sorting?",
            'options': [
                "Trading O(N) auxiliary space to achieve O(N) linear time complexity",
                "Trading O(N log N) time to achieve strict O(1) auxiliary space complexity",
                "Trading recursion call stack depth to eliminate integer underflow errors",
                "Trading algorithmic stability for multi-threaded thread pool concurrency"
            ],
            'correct_index': 0,
            'explanation': "Sorting takes O(1) extra space but requires O(N log N) time. The HashSet approach uses O(N) auxiliary memory to buy O(N) linear time."
        }
    ]
}

p60 = {
    'num': 60,
    'lc': 15,
    'title': "3Sum",
    'category': "Two Pointers",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "3sum",
    'verbatim_statement': """<p>Given an integer array <code>nums</code>, return all the triplets <code>[nums[i], nums[j], nums[k]]</code> such that <code>i != j</code>, <code>i != k</code>, and <code>j != k</code>, and <code>nums[i] + nums[j] + nums[k] == 0</code>.</p>
<p>Notice that the solution set must not contain duplicate triplets.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [-1,0,1,2,-1,-4]', 'output': '[[-1,-1,2],[-1,0,1]]\nExplanation: nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0.\nnums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0.\nnums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0.\nThe distinct triplets are [-1,0,1] and [-1,-1,2].'},
        {'title': "Example 2", 'input': 'nums = [0,1,1]', 'output': '[]\nExplanation: The only possible triplet does not sum up to 0.'},
        {'title': "Example 3", 'input': 'nums = [0,0,0]', 'output': '[[0,0,0]]\nExplanation: The only possible triplet sums up to 0.'}
    ],
    'constraints': [
        "3 <= nums.length <= 3000",
        "-10^5 <= nums[i] <= 10^5"
    ],
    'plain_english': "Sort the array first. Fix each element nums[i] with an outer loop, then use inward Two Pointers (left = i + 1, right = n - 1) to find pairs that sum to -nums[i]. To prevent duplicate triplets in the output, advance past identical adjacent numbers at both the outer loop and inner pointer levels.",
    'boundary_checks': {
        'min_base': "Exactly 3 elements <code>nums = [0, 0, 0]</code> &rarr; Returns `[[0, 0, 0]]`.",
        'best_exit': "All positive or first element > 0 after sort <code>nums = [1, 2, 3]</code> &rarr; Returns `[]` immediately since sum can never be 0.",
        'worst_scan': "Many duplicates <code>nums = [-2, 0, 0, 2, 2]</code> &rarr; Skipped correctly without allocating duplicates."
    },
    'mental_model': {
        'analogy_title': "Anchor One Weight, Balance the Other Two",
        'analogy_text': "Imagine balancing a 3-pan scale to 0. First, sort all weights in a line. Choose one weight `nums[i]` as your anchor. Now you need two remaining weights to balance `-nums[i]`. Place one pointer at the lightest available weight (`left`) and one at the heaviest (`right`). If the sum is too light, step `left` inward to heavier weights. If too heavy, step `right` inward to lighter weights. Skip identical duplicate weights along the way.",
        'aha_moment': "Sorting the array upfront costs O(N log N), which is completely dominated by the O(N^2) two-pointer search. In exchange, sorting makes duplicate skipping trivial and enables directional O(N) two-pointer convergence.",
        'trigger': "Find distinct combinations of 3 elements summing to target &rarr; Sort + Outer Anchor Loop + Inward Two Pointers.",
        'sisters': [
            {'name': "Two Sum II - Input Array Sorted", 'lc': "LC #167"},
            {'name': "3Sum Closest", 'lc': "LC #16"},
            {'name': "4Sum", 'lc': "LC #18"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^3) Time, O(N) Space",
        'naive_text': "Three nested loops trying all triplets (i, j, k), sorting each triplet and inserting into a set to eliminate duplicates.",
        'wasted_work': "Testing cubic combinations and heavy hashing overhead to deduplicate triplets ($N=3000 \\rightarrow 2.7 \\times 10^{10}$ ops).",
        'optimal_complexity': "O(N^2) Time, O(1) or O(log N) Space",
        'optimal_text': "Sort in O(N log N). Outer loop runs N times; inner two pointers scan in O(N). Duplicate skipping handles uniqueness in-place.",
        'core_invariant': "For a fixed sorted anchor `nums[i]`, `nums[left] + nums[right] == -nums[i]`. `left < right` always holds, and duplicates are skipped before next search.",
        'table_rows': [
            {'name': "1. Brute Force (3 Nested Loops)", 'time': "O(N^3)", 'space': "O(N)", 'note': "Severe TLE on N = 3,000."},
            {'name': "2. Hash Set Two-Sum Inner Loop", 'time': "O(N^2)", 'space': "O(N)", 'note': "Requires hash set of lists to prevent duplicate triplets."},
            {'name': "★ 3. Sort + Two Pointers", 'time': "O(N^2)", 'space': "O(1) to O(log N)", 'note': "Gold standard; zero auxiliary memory beyond sorting."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [-1, 0, 1, 2, -1, -4] &rarr; Sorted: [-4, -1, -1, 0, 1, 2]',
        'columns': ["i", "nums[i]", "Left", "Right", "Sum", "Action Taken"],
        'rows': [
            ["0", "-4", "1 (-1)", "5 (2)", "-4 + -1 + 2 = -3 < 0", "left++ (too small)"],
            ["0", "-4", "2 (-1)", "5 (2)", "-3 < 0", "left++ (too small)"],
            ["0", "-4", "4 (1)", "5 (2)", "-1 < 0", "left++ &rarr; left == right, loop ends"],
            ["1", "-1", "2 (-1)", "5 (2)", "-1 + -1 + 2 = 0", "MATCH! Add [-1,-1,2]. left++, right--"],
            ["1", "-1", "3 (0)", "4 (1)", "-1 + 0 + 1 = 0", "MATCH! Add [-1,0,1]. left++, right--"],
            ["2", "-1", "-", "-", "-", "nums[2] == nums[1] &rarr; Skip duplicate anchor"]
        ],
        'takeaway': "Two distinct triplets `[[-1,-1,2], [-1,0,1]]` discovered with zero duplicate entries."
    },
    'stepper': {
        'title': "Sort + Two Pointers 3Sum Stepper",
        'default_input': '-1, 0, 1, 2, -1, -4',
        'presets': [
            {'label': "Canonical [-1, 0, 1, 2, -1, -4]", 'onclick': 'loadPreset([-1, 0, 1, 2, -1, -4])'},
            {'label': "Zeros [0, 0, 0, 0]", 'onclick': 'loadPreset([0, 0, 0, 0])'},
            {'label': "No Solution [1, 2, -2, -1]", 'onclick': 'loadPreset([1, 2, -2, -1])'}
        ],
        'metrics': [
            {'id': "metric-anchor", 'label': "Anchor nums[i]", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-ptrs", 'label': "Pointers (L, R)", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-sum", 'label': "Current Sum", 'color': "text-purple-400", 'initial': "-"},
            {'id': "metric-triplets", 'label': "Found Triplets", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p15-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to observe sorting and two-pointer convergence.',
        'pseudocode_lines': [
            "nums.sort()",
            "for (i in 0 until nums.size - 2) {",
            "    if (i > 0 && nums[i] == nums[i - 1]) continue",
            "    var l = i + 1; var r = nums.size - 1",
            "    while (l < r) {",
            "        val sum = nums[i] + nums[l] + nums[r]",
            "        if (sum == 0) { res.add(listOf(nums[i], nums[l], nums[r])); l++; r-- }",
            "        else if (sum < 0) l++ else r--",
            "    }",
            "}"
        ],
        'js_code': """
    let rawArr = [-1, 0, 1, 2, -1, -4];

    function buildSteps(arr) {
      steps = [];
      const sorted = [...arr].sort((a,b)=>a-b);
      const triplets = [];
      const n = sorted.length;

      steps.push({
        anchor: "-", ptrs: "-", sum: "-", tripletsCount: 0,
        i: -1, l: -1, r: -1, sortedArr: [...sorted], triplets: [], line: 1,
        narration: `Sorted input array: [${sorted.join(', ')}].`
      });

      for (let i = 0; i < n - 2; i++) {
        if (sorted[i] > 0) {
          steps.push({
            anchor: `${sorted[i]} (i=${i})`, ptrs: "-", sum: "> 0", tripletsCount: triplets.length,
            i: i, l: -1, r: -1, sortedArr: [...sorted], triplets: [...triplets], line: 3,
            narration: `Anchor sorted[${i}] = ${sorted[i]} > 0. Since array is sorted, remaining elements cannot sum to 0. Breaking early.`
          });
          break;
        }

        if (i > 0 && sorted[i] === sorted[i - 1]) {
          steps.push({
            anchor: `${sorted[i]} (i=${i})`, ptrs: "-", sum: "-", tripletsCount: triplets.length,
            i: i, l: -1, r: -1, sortedArr: [...sorted], triplets: [...triplets], line: 3,
            narration: `Duplicate anchor ${sorted[i]} at i=${i} matches i=${i-1}. Skipping to avoid duplicate triplets.`
          });
          continue;
        }

        let l = i + 1;
        let r = n - 1;

        while (l < r) {
          const sum = sorted[i] + sorted[l] + sorted[r];
          if (sum === 0) {
            const trip = [sorted[i], sorted[l], sorted[r]];
            triplets.push(trip);
            steps.push({
              anchor: `${sorted[i]} (i=${i})`, ptrs: `L=${l}, R=${r}`, sum: `0 (MATCH!)`, tripletsCount: triplets.length,
              i: i, l: l, r: r, sortedArr: [...sorted], triplets: [...triplets], line: 7,
              narration: `Found valid triplet: [${trip.join(', ')}]. Advancing both pointers and skipping identical values.`
            });
            while (l < r && sorted[l] === sorted[l + 1]) l++;
            while (l < r && sorted[r] === sorted[r - 1]) r--;
            l++;
            r--;
          } else if (sum < 0) {
            steps.push({
              anchor: `${sorted[i]} (i=${i})`, ptrs: `L=${l}, R=${r}`, sum: `${sum} (< 0)`, tripletsCount: triplets.length,
              i: i, l: l, r: r, sortedArr: [...sorted], triplets: [...triplets], line: 8,
              narration: `Sum ${sum} is too small. Incrementing left pointer L from ${l} to ${l+1}.`
            });
            l++;
          } else {
            steps.push({
              anchor: `${sorted[i]} (i=${i})`, ptrs: `L=${l}, R=${r}`, sum: `${sum} (> 0)`, tripletsCount: triplets.length,
              i: i, l: l, r: r, sortedArr: [...sorted], triplets: [...triplets], line: 8,
              narration: `Sum ${sum} is too large. Decrementing right pointer R from ${r} to ${r-1}.`
            });
            r--;
          }
        }
      }

      steps.push({
        anchor: "Done", ptrs: "-", sum: "-", tripletsCount: triplets.length,
        i: -1, l: -1, r: -1, sortedArr: [...sorted], triplets: [...triplets], line: 10,
        narration: `Finished scan. Total ${triplets.length} unique triplet(s) discovered.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p15-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-1.5 flex-wrap">';
      s.sortedArr.forEach((v, idx) => {
        let cls = 'bg-slate-900 border-slate-700 text-slate-300';
        let badge = '';
        if (idx === s.i) {
          cls = 'bg-amber-500/20 border-amber-400 text-amber-300 ring-2 ring-amber-500/50';
          badge = '<span class="text-[9px] block text-amber-400">Anchor</span>';
        } else if (idx === s.l) {
          cls = 'bg-cyan-500/20 border-cyan-400 text-cyan-300 ring-2 ring-cyan-500/50';
          badge = '<span class="text-[9px] block text-cyan-400">Left</span>';
        } else if (idx === s.r) {
          cls = 'bg-purple-500/20 border-purple-400 text-purple-300 ring-2 ring-purple-500/50';
          badge = '<span class="text-[9px] block text-purple-400">Right</span>';
        }
        html += `<div class="px-2.5 py-1.5 rounded-lg border font-mono text-xs font-bold text-center ${cls}">${v}${badge}</div>`;
      });
      html += '</div>';

      if (s.triplets.length > 0) {
        html += `<div class="text-xs font-mono text-emerald-300 font-bold bg-slate-950/60 px-4 py-2 rounded-lg border border-emerald-800/60">Discovered Triplets: ${JSON.stringify(s.triplets)}</div>`;
      }
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-anchor').textContent = s.anchor;
      document.getElementById('metric-ptrs').textContent = s.ptrs;
      document.getElementById('metric-sum').textContent = s.sum;
      document.getElementById('metric-triplets').textContent = s.tripletsCount;
    }

    function loadPreset(arr) {
      rawArr = [...arr];
      buildSteps(rawArr);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Cubic Triple Loops + HashSet (O(N^3))",
            'badge': "Naive",
            'color': "rose",
            'complexity': "O(N^3) Time, O(N) Space",
            'summary': "Loop over all triplets (i, j, k). For each matching triplet, sort it and insert into a HashSet to filter duplicate combinations.",
            'kotlin_code': """class Solution {
    fun threeSum(nums: IntArray): List<List<Int>> {
        val n = nums.size
        val set = mutableSetOf<List<Int>>()
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                for (k in j + 1 until n) {
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        val triplet = listOf(nums[i], nums[j], nums[k]).sorted()
                        set.add(triplet)
                    }
                }
            }
        }
        return set.toList()
    }
}""",
            'python_code': """class Solution:
    def threeSum(self, nums: list[int]) -> list[list[int]]:
        n = len(nums)
        res_set = set()
        for i in range(n):
            for j in range(i + 1, n):
                for k in range(j + 1, n):
                    if nums[i] + nums[j] + nums[k] == 0:
                        trip = tuple(sorted([nums[i], nums[j], nums[k]]))
                        res_set.add(trip)
        return [list(t) for t in res_set]""",
            'java_code': """import java.util.*;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        int n = nums.length;
        Set<List<Integer>> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        List<Integer> trip = Arrays.asList(nums[i], nums[j], nums[k]);
                        Collections.sort(trip);
                        set.add(trip);
                    }
                }
            }
        }
        return new ArrayList<>(set);
    }
}""",
            'memory_breakdown': "O(N) space for HashSet. Quadratic combinations multiplied by inner loop yield O(N^3) time. For N = 3000, 2.7 * 10^10 operations triggers severe TLE."
        },
        {
            'name': "Approach 2: Hash Map Complement Lookup (O(N^2) Time, O(N) Space)",
            'badge': "Better",
            'color': "cyan",
            'complexity': "O(N^2) Time, O(N) Space",
            'summary': "Sort the array. For each anchor i, run a Two-Sum HashMap search on the remaining subarray, keeping track of seen values in a set to avoid duplicates.",
            'kotlin_code': """class Solution {
    fun threeSum(nums: IntArray): List<List<Int>> {
        nums.sort()
        val res = mutableListOf<List<Int>>()
        for (i in nums.indices) {
            if (i > 0 && nums[i] == nums[i - 1]) continue
            val seen = mutableSetOf<Int>()
            var j = i + 1
            while (j < nums.size) {
                val complement = -nums[i] - nums[j]
                if (seen.contains(complement)) {
                    res.add(listOf(nums[i], complement, nums[j]))
                    while (j + 1 < nums.size && nums[j] == nums[j + 1]) j++
                }
                seen.add(nums[j])
                j++
            }
        }
        return res
    }
}""",
            'python_code': """class Solution:
    def threeSum(self, nums: list[int]) -> list[list[int]]:
        nums.sort()
        res = []
        for i in range(len(nums)):
            if i > 0 and nums[i] == nums[i - 1]:
                continue
            seen = set()
            j = i + 1
            while j < len(nums):
                comp = -nums[i] - nums[j]
                if comp in seen:
                    res.append([nums[i], comp, nums[j]])
                    while j + 1 < len(nums) and nums[j] == nums[j + 1]:
                        j += 1
                seen.add(nums[j])
                j += 1
        return res""",
            'java_code': """import java.util.*;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            Set<Integer> seen = new HashSet<>();
            for (int j = i + 1; j < nums.length; j++) {
                int comp = -nums[i] - nums[j];
                if (seen.contains(comp)) {
                    res.add(Arrays.asList(nums[i], comp, nums[j]));
                    while (j + 1 < nums.length && nums[j] == nums[j + 1]) j++;
                }
                seen.add(nums[j]);
            }
        }
        return res;
    }
}""",
            'memory_breakdown': "O(N) auxiliary memory allocated per outer loop iteration. While O(N^2) time is achieved, memory allocation overhead makes it slower than two pointers."
        },
        {
            'name': "★ Approach 3: Sort + Inward Two Pointers (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N^2) Time, O(1) Auxiliary Space",
            'summary': "Sort once. Anchor nums[i], then converge left and right pointers. Skip duplicate anchors and duplicate inner values in-place.",
            'kotlin_code': """class Solution {
    fun threeSum(nums: IntArray): List<List<Int>> {
        nums.sort()
        val res = mutableListOf<List<Int>>()
        val n = nums.size

        for (i in 0 until n - 2) {
            // Early exit: if the smallest remaining number is > 0, sum can never be 0
            if (nums[i] > 0) break

            // Skip duplicate anchors
            if (i > 0 && nums[i] == nums[i - 1]) continue

            var l = i + 1
            var r = n - 1

            while (l < r) {
                val sum = nums[i] + nums[l] + nums[r]
                when {
                    sum == 0 -> {
                        res.add(listOf(nums[i], nums[l], nums[r]))
                        // Skip duplicates for left and right
                        while (l < r && nums[l] == nums[l + 1]) l++
                        while (l < r && nums[r] == nums[r - 1]) r--
                        l++
                        r--
                    }
                    sum < 0 -> l++
                    else -> r--
                }
            }
        }
        return res
    }
}""",
            'python_code': """class Solution:
    def threeSum(self, nums: list[int]) -> list[list[int]]:
        nums.sort()
        res = []
        n = len(nums)

        for i in range(n - 2):
            if nums[i] > 0:
                break
            if i > 0 and nums[i] == nums[i - 1]:
                continue

            l, r = i + 1, n - 1
            while l < r:
                s = nums[i] + nums[l] + nums[r]
                if s == 0:
                    res.append([nums[i], nums[l], nums[r]])
                    while l < r and nums[l] == nums[l + 1]:
                        l += 1
                    while l < r and nums[r] == nums[r - 1]:
                        r -= 1
                    l += 1
                    r -= 1
                elif s < 0:
                    l += 1
                else:
                    r -= 1

        return res""",
            'java_code': """import java.util.*;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        int n = nums.length;

        for (int i = 0; i < n - 2; i++) {
            if (nums[i] > 0) break;
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int l = i + 1, r = n - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    res.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    while (l < r && nums[l] == nums[l + 1]) l++;
                    while (l < r && nums[r] == nums[r - 1]) r--;
                    l++;
                    r--;
                } else if (sum < 0) {
                    l++;
                } else {
                    r--;
                }
            }
        }
        return res;
    }
}""",
            'memory_breakdown': "Strictly O(1) auxiliary space beyond the sorting call stack. Zero heap allocations for sets. Eliminates duplicates entirely in-place."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun threeSum(nums: IntArray): List<List<Int>> {
        // Write your optimal O(N^2) solution here
        nums.sort()
        val res = mutableListOf<List<Int>>()
        val n = nums.size
        for (i in 0 until n - 2) {
            if (nums[i] > 0) break
            if (i > 0 && nums[i] == nums[i - 1]) continue
            var l = i + 1
            var r = n - 1
            while (l < r) {
                val sum = nums[i] + nums[l] + nums[r]
                if (sum == 0) {
                    res.add(listOf(nums[i], nums[l], nums[r]))
                    while (l < r && nums[l] == nums[l + 1]) l++
                    while (l < r && nums[r] == nums[r - 1]) r--
                    l++; r--
                } else if (sum < 0) l++ else r--
            }
        }
        return res
    }
}""",
        'python_boilerplate': """class Solution:
    def threeSum(self, nums: list[int]) -> list[list[int]]:
        # Write your optimal O(N^2) solution here
        nums.sort()
        res = []
        n = len(nums)
        for i in range(n - 2):
            if nums[i] > 0: break
            if i > 0 and nums[i] == nums[i - 1]: continue
            l, r = i + 1, n - 1
            while l < r:
                s = nums[i] + nums[l] + nums[r]
                if s == 0:
                    res.append([nums[i], nums[l], nums[r]])
                    while l < r and nums[l] == nums[l + 1]: l += 1
                    while l < r and nums[r] == nums[r - 1]: r -= 1
                    l += 1; r -= 1
                elif s < 0: l += 1
                else: r -= 1
        return res""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        // Write your optimal O(N^2) solution here
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            if (nums[i] > 0) break;
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int l = i + 1, r = n - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    res.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    while (l < r && nums[l] == nums[l + 1]) l++;
                    while (l < r && nums[r] == nums[r - 1]) r--;
                    l++; r--;
                } else if (sum < 0) l++; else r--;
            }
        }
        return res;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(nums) {
        nums.sort((a,b)=>a-b);
        const res = [];
        const n = nums.length;
        for (let i = 0; i < n - 2; i++) {
          if (nums[i] > 0) break;
          if (i > 0 && nums[i] === nums[i - 1]) continue;
          let l = i + 1, r = n - 1;
          while (l < r) {
            const sum = nums[i] + nums[l] + nums[r];
            if (sum === 0) {
              res.push([nums[i], nums[l], nums[r]]);
              while (l < r && nums[l] === nums[l + 1]) l++;
              while (l < r && nums[r] === nums[r - 1]) r--;
              l++; r--;
            } else if (sum < 0) l++; else r--;
          }
        }
        return res;
      }

      const tests = [
        { input: [-1,0,1,2,-1,-4], expectedLen: 2 },
        { input: [0,1,1], expectedLen: 0 },
        { input: [0,0,0], expectedLen: 1 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.input);
        const pass = actual.length === t.expectedLen;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: nums=[${t.input}] &rarr; Found ${actual.length} triplets (Expected ${t.expectedLen}) (${pass ? 'PASSED' : 'FAILED'})`;
        resultsDiv.appendChild(row);
      });

      if (allPassed) {
        statusSpan.textContent = "All test cases passed cleanly!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "\"First, let me confirm: the triplets must be distinct, indices must be distinct, and the sum must equal zero. An array of length less than 3 cannot produce any triplets. What are the size bounds? N up to 3000.\"",
        'phase2': "\"Since N is up to 3000, an O(N^3) brute force will time out. An O(N^2) solution is optimal. I will sort the array first in O(N log N). Then, I fix an anchor element nums[i] using an outer loop. If nums[i] > 0, we can break immediately because all remaining elements are positive. For each anchor, I set up two pointers: left at i+1 and right at n-1. If their sum is 0, we record the triplet, advance both pointers, and skip over duplicate values for both left and right to prevent duplicate triplets. If the sum is less than 0, we increment left; if greater, we decrement right.\"",
        'phase3': "\"Let me write the code. I sort nums. The outer loop runs from 0 to n-3. If i > 0 and nums[i] == nums[i-1], continue. Inside, left = i+1, right = n-1. While left < right, calculate sum. If sum == 0, add to result, then `while (l < r && nums[l] == nums[l+1]) l++` and `while (l < r && nums[r] == nums[r-1]) r--`, followed by `l++; r--`. When sum < 0, l++; when sum > 0, r--. Return result.\"",
        'phase4': "\"Testing with `[-1, 0, 1, 2, -1, -4]`. Sorted is `[-4, -1, -1, 0, 1, 2]`. Anchor -4 finds no match. Anchor -1 at i=1 finds `[-1, -1, 2]` and `[-1, 0, 1]`. Anchor -1 at i=2 is skipped because it equals nums[1]. Anchor 0 finds no match. Anchor 1 > 0 terminates the loop. Output is `[[-1,-1,2], [-1,0,1]]`. Edge case `[0,0,0]` outputs `[[0,0,0]]` cleanly.\"",
        'senior_followup': "\"If extending this to K-Sum (like 4Sum), we can generalize with recursion: base case K=2 uses two pointers; K > 2 uses an outer loop to reduce to K-1 sum, maintaining O(N^{K-1}) time complexity without extra data structure overhead.\""
    },
    'quiz': [
        {
            'question': "Why is sorting the array permissible even though it modifies original element order?",
            'options': [
                "The problem asks for values of the triplets, not their original indices",
                "Sorting changes the Big-O time complexity from quadratic to linear time",
                "Sorting allows the garbage collector to compact primitive memory buffers",
                "Sorting is mandatory for any binary search lookup in standard libraries"
            ],
            'correct_index': 0,
            'explanation': "The problem statement asks for triplet values `[nums[i], nums[j], nums[k]]`, not original array indices. Sorting enables O(N^2) two-pointer scanning and trivial duplicate skipping."
        },
        {
            'question': "What is the primary condition used to avoid duplicate triplets in the outer loop?",
            'options': [
                "Check if (i > 0 && nums[i] == nums[i - 1]) and continue if true",
                "Check if (nums[i] == 0) and reset the inner left pointer to 0",
                "Insert all triplets into a thread-safe synchronized ConcurrentHashMap",
                "Break out of the outer loop whenever nums[i] is an odd integer"
            ],
            'correct_index': 0,
            'explanation': "If `nums[i] == nums[i-1]`, all triplets starting with this value were already explored during the previous iteration. Skipping avoids duplicate triplets at the anchor level."
        }
    ]
}

p61 = {
    'num': 61,
    'lc': 424,
    'title': "Longest Repeating Character Replacement",
    'category': "Sliding Window",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "longest-repeating-character-replacement",
    'verbatim_statement': """<p>You are given a string <code>s</code> and an integer <code>k</code>. You can choose any character of the string and change it to any other uppercase English character. You can perform this operation at most <code>k</code> times.</p>
<p>Return <em>the length of the longest substring containing the same letter you can get after performing the above operations</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 's = "ABAB", k = 2', 'output': '4\nExplanation: Replace the two \'A\'s with \'B\'s or vice versa.'},
        {'title': "Example 2", 'input': 's = "AABABBA", k = 1', 'output': '4\nExplanation: Replace the one \'A\' in the middle with \'B\' and form "AABBBBA". The substring "BBBB" has the longest repeating letters, which is 4.'}
    ],
    'constraints': [
        "1 <= s.length <= 10^5",
        "s consists of only uppercase English letters.",
        "0 <= k <= s.length"
    ],
    'plain_english': "Maintain a dynamic sliding window [left..right]. In any window, the number of characters that must be replaced to make the whole window uniform is `windowLength - maxFreqChar`. If this count exceeds k, the window is invalid, so we increment left. Otherwise, we expand right. The maximum valid window size seen is our answer.",
    'boundary_checks': {
        'min_base': "k >= string length <code>s = \"ABC\", k = 5</code> &rarr; Returns 3 (entire string can be replaced).",
        'best_exit': "All identical letters <code>s = \"AAAA\", k = 0</code> &rarr; Returns 4 without any replacements.",
        'worst_scan': "k = 0 with alternating letters <code>s = \"ABABA\", k = 0</code> &rarr; Returns 1."
    },
    'mental_model': {
        'analogy_title': "The Dominant Color Banner",
        'analogy_text': "Imagine stretching a rectangular frame over a banner of colored flags. Inside the frame, you find the most frequent flag color (`maxCount`). Every other flag in the frame must be repainted using your limited supply of paint cans (`k`). If the number of other flags `(windowSize - maxCount)` exceeds your paint cans `k`, the frame is too wide. Shift the left edge of the frame to the right.",
        'aha_moment': "You do not need to decrease `maxCount` when shrinking the window! A window can only beat our current global maximum if it achieves a strictly higher `maxCount`. Stale `maxCount` values cannot produce false positives because window validity is bounded by `right - left + 1`.",
        'trigger': "Maximize substring length with at most k character replacements &rarr; Sliding window with frequency map and `windowSize - maxCount <= k` invariant.",
        'sisters': [
            {'name': "Max Consecutive Ones III", 'lc': "LC #1004"},
            {'name': "Longest Substring Without Repeating Characters", 'lc': "LC #3"},
            {'name': "Minimum Window Substring", 'lc': "LC #76"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(26 * N^2) Time, O(1) Space",
        'naive_text': "Check all O(N^2) substrings. For each substring, count frequencies of 26 uppercase letters, find max frequency, and check if length - max <= k.",
        'wasted_work': "Re-scanning substrings from scratch repeatedly instead of sliding existing frequency counts.",
        'optimal_complexity': "O(N) Time, O(1) Auxiliary Space",
        'optimal_text': "Two-pointer sliding window with a 26-element integer frequency array. Both pointers advance monotonically from 0 to N.",
        'core_invariant': "At any instant, `(right - left + 1) - maxCount <= k` guarantees the window can be turned into identical characters within k edits.",
        'table_rows': [
            {'name': "1. Brute Force Substrings", 'time': "O(26 * N^2)", 'space': "O(1)", 'note': "Times out on N = 10^5 ($2.6 \\times 10^{11}$ ops)."},
            {'name': "2. Sliding Window with Recalculation", 'time': "O(26 * N)", 'space': "O(1)", 'note': "Scans 26 alphabet buckets on every window contraction."},
            {'name': "★ 3. Monotonic Expanding Sliding Window", 'time': "O(N)", 'space': "O(1)", 'note': "Optimal linear execution; maxCount never decremented."}
        ]
    },
    'trace_table': {
        'input_desc': 's = "AABABBA", k = 1',
        'columns': ["Right", "Char", "Freq State", "maxCount", "Window Size", "Replacements Needed", "Action / Left Pointer"],
        'rows': [
            ["0", "'A'", "{A:1}", "1", "1", "1 - 1 = 0 <= 1", "Valid. maxLen = 1"],
            ["1", "'A'", "{A:2}", "2", "2", "2 - 2 = 0 <= 1", "Valid. maxLen = 2"],
            ["2", "'B'", "{A:2, B:1}", "2", "3", "3 - 2 = 1 <= 1", "Valid. maxLen = 3"],
            ["3", "'A'", "{A:3, B:1}", "3", "4", "4 - 3 = 1 <= 1", "Valid. maxLen = 4"],
            ["4", "'B'", "{A:3, B:2}", "3", "5", "5 - 3 = 2 > 1", "INVALID! Shrink: decrement s[left='A'], left=1"],
            ["5", "'B'", "{A:2, B:3}", "3", "5", "5 - 3 = 2 > 1", "INVALID! Shrink: decrement s[left='A'], left=2"],
            ["6", "'A'", "{A:2, B:3}", "3", "5", "5 - 3 = 2 > 1", "INVALID! Shrink: decrement s[left='B'], left=3"]
        ],
        'takeaway': "Maximum valid window size achieved is 4 (e.g. \"AABA\" or \"ABBA\" with 1 edit)."
    },
    'stepper': {
        'title': "Sliding Window Character Replacement Stepper",
        'default_input': 'AABABBA',
        'presets': [
            {'label': "Example [AABABBA, k=1]", 'onclick': 'loadPreset("AABABBA", 1)'},
            {'label': "Example [ABAB, k=2]", 'onclick': 'loadPreset("ABAB", 2)'},
            {'label': "Repeated [AAAA, k=0]", 'onclick': 'loadPreset("AAAA", 0)'}
        ],
        'metrics': [
            {'id': "metric-window", 'label': "Window [L..R]", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-max-count", 'label': "Max Char Count", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-needed", 'label': "Replacements Needed", 'color': "text-purple-400", 'initial': "0"},
            {'id': "metric-best-len", 'label': "Best Window Length", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p424-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace sliding window expansion and contraction.',
        'pseudocode_lines': [
            "val count = IntArray(26)",
            "var l = 0; var maxCount = 0; var maxLen = 0",
            "for (r in s.indices) {",
            "    count[s[r] - 'A']++",
            "    maxCount = maxOf(maxCount, count[s[r] - 'A'])",
            "    while ((r - l + 1) - maxCount > k) { count[s[l] - 'A']--; l++ }",
            "    maxLen = maxOf(maxLen, r - l + 1)",
            "}"
        ],
        'js_code': """
    let rawStr = "AABABBA";
    let rawK = 1;

    function buildSteps(str, k) {
      steps = [];
      const count = {};
      let l = 0;
      let maxCount = 0;
      let maxLen = 0;

      steps.push({
        window: "-", maxCount: 0, needed: 0, bestLen: 0,
        l: -1, r: -1, str: str, k: k, line: 2,
        narration: `Initialized sliding window with k = ${k}. Ready to scan.`
      });

      for (let r = 0; r < str.length; r++) {
        const char = str[r];
        count[char] = (count[char] || 0) + 1;
        maxCount = Math.max(maxCount, count[char]);
        const windowLen = r - l + 1;
        const needed = windowLen - maxCount;

        steps.push({
          window: `[${l}..${r}] ("${str.substring(l, r + 1)}")`, maxCount: maxCount, needed: needed, bestLen: maxLen,
          l: l, r: r, str: str, k: k, line: 4,
          narration: `Expanded right to index ${r} ('${char}'). Window is "${str.substring(l, r + 1)}". Max char freq = ${maxCount}. Needed replacements = ${needed}.`
        });

        if (needed > k) {
          count[str[l]]--;
          l++;
          steps.push({
            window: `[${l}..${r}] ("${str.substring(l, r + 1)}")`, maxCount: maxCount, needed: (r - l + 1) - maxCount, bestLen: maxLen,
            l: l, r: r, str: str, k: k, line: 6,
            narration: `Needed replacements ${needed} > k (${k}). Contracted left pointer to index ${l}.`
          });
        }

        maxLen = Math.max(maxLen, r - l + 1);
        steps.push({
          window: `[${l}..${r}] ("${str.substring(l, r + 1)}")`, maxCount: maxCount, needed: (r - l + 1) - maxCount, bestLen: maxLen,
          l: l, r: r, str: str, k: k, line: 7,
          narration: `Valid window maintained. Current max valid length is ${maxLen}.`
        });
      }

      steps.push({
        window: "Complete", maxCount: maxCount, needed: "-", bestLen: maxLen,
        l: -1, r: -1, str: str, k: k, line: 8,
        narration: `Scan finished. Longest repeating substring obtainable with ${k} replacements has length ${maxLen}.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p424-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-1.5 flex-wrap">';
      for (let i = 0; i < s.str.length; i++) {
        const inWindow = i >= s.l && i <= s.r;
        let cls = 'bg-slate-900 border-slate-700 text-slate-400';
        let tag = '';
        if (inWindow) {
          cls = 'bg-emerald-500/20 border-emerald-400 text-emerald-300 ring-2 ring-emerald-500/40';
          if (i === s.l && i === s.r) tag = '<span class="text-[9px] block text-cyan-400">L,R</span>';
          else if (i === s.l) tag = '<span class="text-[9px] block text-cyan-400">L</span>';
          else if (i === s.r) tag = '<span class="text-[9px] block text-purple-400">R</span>';
        }
        html += `<div class="w-10 h-12 rounded-lg border font-mono text-sm font-bold flex flex-col items-center justify-center ${cls}">${s.str[i]}${tag}</div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-window').textContent = s.window;
      document.getElementById('metric-max-count').textContent = s.maxCount;
      document.getElementById('metric-needed').textContent = s.needed;
      document.getElementById('metric-best-len').textContent = s.bestLen;
    }

    function loadPreset(str, k) {
      rawStr = str;
      rawK = k;
      buildSteps(rawStr, rawK);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Brute Force All Substrings (O(26 * N^2))",
            'badge': "Naive",
            'color': "rose",
            'complexity': "O(26 * N^2) Time, O(1) Space",
            'summary': "Generate all pairs (i, j). In each substring, count character occurrences across 26 uppercase letters and test if length - maxCount <= k.",
            'kotlin_code': """class Solution {
    fun characterReplacement(s: String, k: Int): Int {
        var maxLen = 0
        val n = s.length
        for (i in 0 until n) {
            val count = IntArray(26)
            var maxFreq = 0
            for (j in i until n) {
                count[s[j] - 'A']++
                maxFreq = maxOf(maxFreq, count[s[j] - 'A'])
                val len = j - i + 1
                if (len - maxFreq <= k) {
                    maxLen = maxOf(maxLen, len)
                }
            }
        }
        return maxLen
    }
}""",
            'python_code': """class Solution:
    def characterReplacement(self, s: str, k: int) -> int:
        max_len = 0
        n = len(s)
        for i in range(n):
            count = [0] * 26
            max_freq = 0
            for j in range(i, n):
                idx = ord(s[j]) - ord('A')
                count[idx] += 1
                max_freq = max(max_freq, count[idx])
                length = j - i + 1
                if length - max_freq <= k:
                    max_len = max(max_len, length)
        return max_len""",
            'java_code': """class Solution {
    public int characterReplacement(String s, int k) {
        int maxLen = 0;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            int[] count = new int[26];
            int maxFreq = 0;
            for (int j = i; j < n; j++) {
                int idx = s.charAt(j) - 'A';
                count[idx]++;
                maxFreq = Math.max(maxFreq, count[idx]);
                int len = j - i + 1;
                if (len - maxFreq <= k) {
                    maxLen = Math.max(maxLen, len);
                }
            }
        }
        return maxLen;
    }
}""",
            'memory_breakdown': "O(1) space since alphabet size is bounded to 26 uppercase letters. Quadratic runtime of 10^10 operations triggers TLE on inputs of length 10^5."
        },
        {
            'name': "Approach 2: Sliding Window with Rescan (O(26 * N))",
            'badge': "Better",
            'color': "cyan",
            'complexity': "O(26 * N) Time, O(1) Space",
            'summary': "Two-pointer sliding window. When window becomes invalid, scan the 26-element array to recompute maxCount precisely before advancing left.",
            'kotlin_code': """class Solution {
    fun characterReplacement(s: String, k: Int): Int {
        val count = IntArray(26)
        var l = 0
        var maxLen = 0

        for (r in s.indices) {
            count[s[r] - 'A']++
            var maxCount = 0
            for (c in count) maxCount = maxOf(maxCount, c)

            while ((r - l + 1) - maxCount > k) {
                count[s[l] - 'A']--
                l++
                maxCount = 0
                for (c in count) maxCount = maxOf(maxCount, c)
            }
            maxLen = maxOf(maxLen, r - l + 1)
        }
        return maxLen
    }
}""",
            'python_code': """class Solution:
    def characterReplacement(self, s: str, k: int) -> int:
        count = [0] * 26
        l = 0
        max_len = 0
        for r in range(len(s)):
            count[ord(s[r]) - ord('A')] += 1
            max_count = max(count)
            while (r - l + 1) - max_count > k:
                count[ord(s[l]) - ord('A')] -= 1
                l += 1
                max_count = max(count)
            max_len = max(max_len, r - l + 1)
        return max_len""",
            'java_code': """class Solution {
    public int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int l = 0;
        int maxLen = 0;
        for (int r = 0; r < s.length(); r++) {
            count[s.charAt(r) - 'A']++;
            int maxCount = 0;
            for (int c : count) maxCount = Math.max(maxCount, c);
            while ((r - l + 1) - maxCount > k) {
                count[s.charAt(l) - 'A']--;
                l++;
                maxCount = 0;
                for (int c : count) maxCount = Math.max(maxCount, c);
            }
            maxLen = Math.max(maxLen, r - l + 1);
        }
        return maxLen;
    }
}""",
            'memory_breakdown': "O(1) auxiliary space (26 ints). Linear time scaled by 26 loop passes per contraction step."
        },
        {
            'name': "★ Approach 3: Monotonic Expanding Window (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(1) Auxiliary Space",
            'summary': "Never decrement maxCount when shrinking! A smaller maxCount cannot increase the maximum valid window size seen so far.",
            'kotlin_code': """class Solution {
    fun characterReplacement(s: String, k: Int): Int {
        val count = IntArray(26)
        var l = 0
        var maxCount = 0
        var maxLen = 0

        for (r in s.indices) {
            val idx = s[r] - 'A'
            count[idx]++
            maxCount = maxOf(maxCount, count[idx])

            // If invalid, shift left pointer by 1
            if ((r - l + 1) - maxCount > k) {
                count[s[l] - 'A']--
                l++
            }

            maxLen = maxOf(maxLen, r - l + 1)
        }
        return maxLen
    }
}""",
            'python_code': """class Solution:
    def characterReplacement(self, s: str, k: int) -> int:
        count = [0] * 26
        l = 0
        max_count = 0
        max_len = 0

        for r in range(len(s)):
            idx = ord(s[r]) - ord('A')
            count[idx] += 1
            max_count = max(max_count, count[idx])

            if (r - l + 1) - max_count > k:
                count[ord(s[l]) - ord('A')] -= 1
                l += 1

            max_len = max(max_len, r - l + 1)

        return max_len""",
            'java_code': """class Solution {
    public int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int l = 0;
        int maxCount = 0;
        int maxLen = 0;

        for (int r = 0; r < s.length(); r++) {
            int idx = s.charAt(r) - 'A';
            count[idx]++;
            maxCount = Math.max(maxCount, count[idx]);

            if ((r - l + 1) - maxCount > k) {
                count[s.charAt(l) - 'A']--;
                l++;
            }

            maxLen = Math.max(maxLen, r - l + 1);
        }
        return maxLen;
    }
}""",
            'memory_breakdown': "Strictly O(1) extra space (26-element array). Strictly O(N) runtime with no 26-element re-scans. Left and right pointers increment at most N times."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun characterReplacement(s: String, k: Int): Int {
        // Write your optimal O(N) solution here
        val count = IntArray(26)
        var l = 0
        var maxCount = 0
        var maxLen = 0
        for (r in s.indices) {
            val idx = s[r] - 'A'
            count[idx]++
            maxCount = maxOf(maxCount, count[idx])
            if ((r - l + 1) - maxCount > k) {
                count[s[l] - 'A']--
                l++
            }
            maxLen = maxOf(maxLen, r - l + 1)
        }
        return maxLen
    }
}""",
        'python_boilerplate': """class Solution:
    def characterReplacement(self, s: str, k: int) -> int:
        # Write your optimal O(N) solution here
        count = [0] * 26
        l = 0
        max_count = 0
        max_len = 0
        for r in range(len(s)):
            idx = ord(s[r]) - ord('A')
            count[idx] += 1
            max_count = max(max_count, count[idx])
            if (r - l + 1) - max_count > k:
                count[ord(s[l]) - ord('A')] -= 1
                l += 1
            max_len = max(max_len, r - l + 1)
        return max_len""",
        'java_boilerplate': """class Solution {
    public int characterReplacement(String s, int k) {
        // Write your optimal O(N) solution here
        int[] count = new int[26];
        int l = 0, maxCount = 0, maxLen = 0;
        for (int r = 0; r < s.length(); r++) {
            int idx = s.charAt(r) - 'A';
            count[idx]++;
            maxCount = Math.max(maxCount, count[idx]);
            if ((r - l + 1) - maxCount > k) {
                count[s.charAt(l) - 'A']--;
                l++;
            }
            maxLen = Math.max(maxLen, r - l + 1);
        }
        return maxLen;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(s, k) {
        const count = new Array(26).fill(0);
        let l = 0, maxCount = 0, maxLen = 0;
        for (let r = 0; r < s.length; r++) {
          const idx = s.charCodeAt(r) - 65;
          count[idx]++;
          maxCount = Math.max(maxCount, count[idx]);
          if ((r - l + 1) - maxCount > k) {
            count[s.charCodeAt(l) - 65]--;
            l++;
          }
          maxLen = Math.max(maxLen, r - l + 1);
        }
        return maxLen;
      }

      const tests = [
        { s: "ABAB", k: 2, expected: 4 },
        { s: "AABABBA", k: 1, expected: 4 },
        { s: "AAAA", k: 0, expected: 4 },
        { s: "ABAA", k: 0, expected: 2 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.s, t.k);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: s="${t.s}", k=${t.k} &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
        resultsDiv.appendChild(row);
      });

      if (allPassed) {
        statusSpan.textContent = "All test cases passed cleanly!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "\"First, let me confirm the constraints: s consists of uppercase English letters A through Z. k is between 0 and s.length. The return value is the maximum length of a substring with all identical characters after at most k replacements.\"",
        'phase2': "\"We can solve this in linear time using a sliding window. In any window [left..right], the length is (right - left + 1). The most frequent character occurs `maxCount` times. That means we need to replace `(right - left + 1) - maxCount` characters to make the entire window uniform. If this needed replacements exceeds k, the window is invalid and we shift left forward. Crucially, we do NOT need to decrement maxCount when left shifts forward, because our answer can only improve if we discover a new window with a higher maxCount. This gives strict O(N) runtime.\"",
        'phase3': "\"Let me write the code. I allocate an integer array of size 26 for character frequencies. Initialize `l = 0`, `maxCount = 0`, and `maxLen = 0`. Iterate `r` from 0 to s.length - 1: update frequency, update `maxCount = max(maxCount, count[idx])`. If `(r - l + 1) - maxCount > k`, decrement `count[s[l] - 'A']` and increment `l`. Then update `maxLen = max(maxLen, r - l + 1)`. Return `maxLen`.\"",
        'phase4': "\"Tracing with `\"AABABBA\"` and `k=1`. At r=0..3 ('AABA'), maxCount=3, windowSize=4, needed=1 <= 1, maxLen=4. At r=4 ('B'), needed becomes 2 > 1, so l increments to 1. The window size never drops below our previous best. The final maxLen is 4. For edge case k=0, only identical contiguous characters qualify. Everything is verified.\"",
        'senior_followup': "\"If the alphabet was unicode or arbitrary 32-bit characters instead of 26 English letters, we would use an in-memory HashMap for frequency tracking with average O(1) ops and O(U) space where U is unique characters in the window.\""
    },
    'quiz': [
        {
            'question': "Why is it mathematically safe NOT to decrement maxCount when the window shrinks?",
            'options': [
                "A smaller maxCount cannot yield a longer valid window than already seen",
                "Decreasing maxCount causes an integer underflow error on 32-bit registers",
                "The garbage collector automatically restores maxCount on next loop pass",
                "The English alphabet is fixed to 26 letters which guarantees monotonicity"
            ],
            'correct_index': 0,
            'explanation': "Our goal is to find the maximum window length. A new window can only beat our previous best length if its `maxCount` is strictly larger than the historical maximum. Thus, a stale (larger) `maxCount` does not inflate our answer."
        },
        {
            'question': "What is the condition that determines if the current window [l..r] is valid?",
            'options': [
                "(right - left + 1) - maxCount <= k",
                "(right - left + 1) + maxCount <= k",
                "maxCount - (right - left + 1) >= k",
                "(right - left + 1) * maxCount <= k"
            ],
            'correct_index': 0,
            'explanation': "The number of non-dominant characters in the window is `windowLength - maxCount`. These must be changed to match the dominant character, so this difference must be <= k."
        }
    ]
}

p62 = {
    'num': 62,
    'lc': 560,
    'title': "Subarray Sum Equals K",
    'category': "Prefix Sum",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "subarray-sum-equals-k",
    'verbatim_statement': """<p>Given an array of integers <code>nums</code> and an integer <code>k</code>, return <em>the total number of subarrays whose sum equals to</em> <code>k</code>.</p>
<p>A subarray is a contiguous <strong>non-empty</strong> sequence of elements within an array.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [1,1,1], k = 2', 'output': '2'},
        {'title': "Example 2", 'input': 'nums = [1,2,3], k = 3', 'output': '2'}
    ],
    'constraints': [
        "1 <= nums.length <= 2 * 10^4",
        "-1000 <= nums[i] <= 1000",
        "-10^7 <= k <= 10^7"
    ],
    'plain_english': "Maintain a running prefix sum and a frequency map storing how many times each prefix sum has occurred. At any index, the sum of a subarray between some earlier index j and current index i is `currSum - prefixSum[j]`. If `currSum - prefixSum[j] == k`, then `prefixSum[j] == currSum - k`. We add `map.getOrDefault(currSum - k, 0)` to our answer on every step in O(1) time.",
    'boundary_checks': {
        'min_base': "Single element matching k <code>nums = [5], k = 5</code> &rarr; Returns 1.",
        'best_exit': "Base map initialization <code>map[0] = 1</code> &rarr; Ensures subarrays starting at index 0 are counted correctly.",
        'worst_scan': "Negative numbers cancelling out <code>nums = [1, -1, 1, -1], k = 0</code> &rarr; Correctly counts 4 subarrays summing to 0."
    },
    'mental_model': {
        'analogy_title': "The Elevation Difference Altimeter",
        'analogy_text': "Imagine hiking along a mountain trail with an altimeter. You want to know how many trail sections had an elevation gain of exactly k meters. You log every elevation you have ever crossed into a notebook. At your current altitude `currSum`, you look back in your notebook: have you ever stood at altitude `currSum - k`? Every time you did, the hike from that point to where you are now gained exactly k meters.",
        'aha_moment': "Sliding window FAILS when numbers can be negative (because shrinking the window does not monotonically decrease the sum). Prefix Sum + HashMap is the only O(N) strategy that handles negative and zero values unconditionally.",
        'trigger': "Contiguous subarray sum equals target with negative numbers allowed &rarr; Running Prefix Sum with HashMap count lookup.",
        'sisters': [
            {'name': "Two Sum", 'lc': "LC #1"},
            {'name': "Continuous Subarray Sum (Modulo K)", 'lc': "LC #523"},
            {'name': "Subarray Sums Divisible by K", 'lc': "LC #974"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(1) Space",
        'naive_text': "Test all O(N^2) subarrays with nested loops: outer loop fixes start i, inner loop sums from i to j.",
        'wasted_work': "Recomputing sum combinations independently instead of looking up mathematical complements in O(1).",
        'optimal_complexity': "O(N) Time, O(N) Auxiliary Space",
        'optimal_text': "Single linear pass. Keep running sum and store frequencies of past prefix sums in a HashMap.",
        'core_invariant': "At index i, `count` increases by `prefixCounts[currSum - k]`, representing the exact number of contiguous subarrays ending at i that sum to k.",
        'table_rows': [
            {'name': "1. Brute Force (All Subarrays)", 'time': "O(N^3)", 'space': "O(1)", 'note': "Computes sum from scratch for each subarray."},
            {'name': "2. Running Sum Inner Loop", 'time': "O(N^2)", 'space': "O(1)", 'note': "Times out on N = 20,000 ($4 \\times 10^8$ ops)."},
            {'name': "★ 3. Prefix Sum + Frequency HashMap", 'time': "O(N)", 'space': "O(N)", 'note': "Single linear pass; immune to negative numbers."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [1, 2, 3], k = 3',
        'columns': ["Index i", "nums[i]", "currSum", "Target Needed (currSum - k)", "Occurrences in Map", "Total Count", "Map State After Step"],
        'rows': [
            ["Init", "-", "0", "-", "-", "0", "{0: 1}"],
            ["0", "1", "1", "1 - 3 = -2", "0", "0", "{0: 1, 1: 1}"],
            ["1", "2", "3", "3 - 3 = 0", "1 (from init)", "0 + 1 = 1", "{0: 1, 1: 1, 3: 1}"],
            ["2", "3", "6", "6 - 3 = 3", "1 (from i=1)", "1 + 1 = 2", "{0: 1, 1: 1, 3: 1, 6: 1}"]
        ],
        'takeaway': "Two subarrays found: `[1, 2]` (sum=3) and `[3]` (sum=3). Final count = 2."
    },
    'stepper': {
        'title': "Prefix Sum + Frequency Map Stepper",
        'default_input': '1, 2, 3',
        'presets': [
            {'label': "Example [1, 2, 3], k=3", 'onclick': 'loadPreset([1, 2, 3], 3)'},
            {'label': "Example [1, 1, 1], k=2", 'onclick': 'loadPreset([1, 1, 1], 2)'},
            {'label': "With Negatives [1, -1, 1, -1], k=0", 'onclick': 'loadPreset([1, -1, 1, -1], 0)'}
        ],
        'metrics': [
            {'id': "metric-curr-sum", 'label': "Running currSum", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-needed", 'label': "Complement (sum - k)", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-matches", 'label': "Matches Found", 'color': "text-purple-400", 'initial': "0"},
            {'id': "metric-total-subarrays", 'label': "Total Subarrays", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p560-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to observe prefix sum accumulation and hash map lookup.',
        'pseudocode_lines': [
            "val map = mutableMapOf(0 to 1)",
            "var currSum = 0; var count = 0",
            "for (num in nums) {",
            "    currSum += num",
            "    count += map.getOrDefault(currSum - k, 0)",
            "    map[currSum] = map.getOrDefault(currSum, 0) + 1",
            "}",
            "return count"
        ],
        'js_code': """
    let rawArr = [1, 2, 3];
    let rawK = 3;

    function buildSteps(arr, k) {
      steps = [];
      const map = { 0: 1 };
      let currSum = 0;
      let count = 0;

      steps.push({
        currSum: 0, needed: "-", matches: 0, total: 0,
        activeIdx: -1, mapState: { ...map }, arr: [...arr], k: k, line: 1,
        narration: `Initialized map with {0: 1} to handle subarrays starting at index 0. Target k = ${k}.`
      });

      for (let i = 0; i < arr.length; i++) {
        currSum += arr[i];
        const needed = currSum - k;
        const matches = map[needed] || 0;
        count += matches;

        steps.push({
          currSum: currSum, needed: `${currSum} - ${k} = ${needed}`, matches: matches, total: count,
          activeIdx: i, mapState: { ...map }, arr: [...arr], k: k, line: 5,
          narration: `At index ${i} (${arr[i]}): currSum = ${currSum}. Complement needed = ${needed}. Map has ${matches} match(es). Total count = ${count}.`
        });

        map[currSum] = (map[currSum] || 0) + 1;

        steps.push({
          currSum: currSum, needed: `${needed}`, matches: matches, total: count,
          activeIdx: i, mapState: { ...map }, arr: [...arr], k: k, line: 6,
          narration: `Recorded currSum ${currSum} into map. Frequency is now ${map[currSum]}.`
        });
      }

      steps.push({
        currSum: currSum, needed: "Done", matches: "-", total: count,
        activeIdx: -1, mapState: { ...map }, arr: [...arr], k: k, line: 8,
        narration: `Scan complete. Found ${count} total subarrays summing to ${k}.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p560-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-2">';
      s.arr.forEach((v, idx) => {
        const isAct = idx === s.activeIdx;
        html += `<div class="w-12 h-10 rounded-lg border font-mono text-xs font-bold flex items-center justify-center ${isAct ? 'bg-cyan-500/20 border-cyan-400 text-cyan-300 ring-2 ring-cyan-500/50' : 'bg-slate-900 border-slate-700 text-slate-300'}">${v}</div>`;
      });
      html += '</div>';

      html += '<div class="flex flex-wrap items-center justify-center gap-2 max-w-xl">';
      html += '<span class="text-xs font-mono text-slate-400">Prefix Map:</span>';
      Object.keys(s.mapState).forEach(k => {
        html += `<span class="px-2.5 py-1 rounded bg-slate-900 border border-slate-700 font-mono text-xs text-amber-300">sum ${k}: <strong class="text-white">${s.mapState[k]}x</strong></span>`;
      });
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-curr-sum').textContent = s.currSum;
      document.getElementById('metric-needed').textContent = s.needed;
      document.getElementById('metric-matches').textContent = s.matches;
      document.getElementById('metric-total-subarrays').textContent = s.total;
    }

    function loadPreset(arr, k) {
      rawArr = [...arr];
      rawK = k;
      buildSteps(rawArr, rawK);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Nested Loops Running Sum (O(N^2))",
            'badge': "Naive",
            'color': "rose",
            'complexity': "O(N^2) Time, O(1) Space",
            'summary': "For each start index i, run inner loop j from i to n-1 accumulating sum. Increment count whenever sum == k.",
            'kotlin_code': """class Solution {
    fun subarraySum(nums: IntArray, k: Int): Int {
        var count = 0
        val n = nums.size
        for (i in 0 until n) {
            var sum = 0
            for (j in i until n) {
                sum += nums[j]
                if (sum == k) count++
            }
        }
        return count
    }
}""",
            'python_code': """class Solution:
    def subarraySum(self, nums: list[int], k: int) -> int:
        count = 0
        n = len(nums)
        for i in range(n):
            s = 0
            for j in range(i, n):
                s += nums[j]
                if s == k:
                    count += 1
        return count""",
            'java_code': """class Solution {
    public int subarraySum(int[] nums, int k) {
        int count = 0;
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = i; j < n; j++) {
                sum += nums[j];
                if (sum == k) count++;
            }
        }
        return count;
    }
}""",
            'memory_breakdown': "O(1) memory, but O(N^2) operations. With N = 20,000, 4 * 10^8 operations exceeds the 1-second execution timeout."
        },
        {
            'name': "Approach 2: Full Prefix Array + Nested Search (O(N^2) Time, O(N) Space)",
            'badge': "Better",
            'color': "cyan",
            'complexity': "O(N^2) Time, O(N) Space",
            'summary': "Precompute prefix sum array where prefix[i] = sum(nums[0..i-1]). Check all pairs (i, j) where prefix[j] - prefix[i] == k.",
            'kotlin_code': """class Solution {
    fun subarraySum(nums: IntArray, k: Int): Int {
        val n = nums.size
        val prefix = IntArray(n + 1)
        for (i in 0 until n) {
            prefix[i + 1] = prefix[i] + nums[i]
        }
        var count = 0
        for (i in 0 until n) {
            for (j in i + 1..n) {
                if (prefix[j] - prefix[i] == k) count++
            }
        }
        return count
    }
}""",
            'python_code': """class Solution:
    def subarraySum(self, nums: list[int], k: int) -> int:
        n = len(nums)
        prefix = [0] * (n + 1)
        for i in range(n):
            prefix[i + 1] = prefix[i] + nums[i]
        count = 0
        for i in range(n):
            for j in range(i + 1, n + 1):
                if prefix[j] - prefix[i] == k:
                    count += 1
        return count""",
            'java_code': """class Solution {
    public int subarraySum(int[] nums, int k) {
        int n = nums.length;
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (prefix[j] - prefix[i] == k) count++;
            }
        }
        return count;
    }
}""",
            'memory_breakdown': "O(N) space for prefix array without improving the quadratic time complexity."
        },
        {
            'name': "★ Approach 3: Prefix Sum + Frequency HashMap (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(N) Space",
            'summary': "Keep running prefix sum. Query map for currSum - k occurrences in O(1) time. Increment current sum frequency in map.",
            'kotlin_code': """class Solution {
    fun subarraySum(nums: IntArray, k: Int): Int {
        val map = HashMap<Int, Int>()
        map[0] = 1 // Base case: prefix sum of 0 has occurred once
        var currSum = 0
        var count = 0

        for (num in nums) {
            currSum += num
            count += map.getOrDefault(currSum - k, 0)
            map[currSum] = map.getOrDefault(currSum, 0) + 1
        }
        return count
    }
}""",
            'python_code': """from collections import defaultdict

class Solution:
    def subarraySum(self, nums: list[int], k: int) -> int:
        prefix_counts = defaultdict(int)
        prefix_counts[0] = 1  # Base case
        curr_sum = 0
        count = 0

        for num in nums:
            curr_sum += num
            count += prefix_counts[curr_sum - k]
            prefix_counts[curr_sum] += 1

        return count""",
            'java_code': """import java.util.HashMap;
import java.util.Map;

class Solution {
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1); // Base case: prefix sum of 0 occurred once
        int currSum = 0;
        int count = 0;

        for (int num : nums) {
            currSum += num;
            count += map.getOrDefault(currSum - k, 0);
            map.put(currSum, map.getOrDefault(currSum, 0) + 1);
        }
        return count;
    }
}""",
            'memory_breakdown': "O(N) space to store at most N unique prefix sums in the HashMap. Strictly O(N) runtime with O(1) average lookup per element. Completely immune to negative values."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun subarraySum(nums: IntArray, k: Int): Int {
        // Write your optimal O(N) solution here
        val map = HashMap<Int, Int>()
        map[0] = 1
        var currSum = 0
        var count = 0
        for (num in nums) {
            currSum += num
            count += map.getOrDefault(currSum - k, 0)
            map[currSum] = map.getOrDefault(currSum, 0) + 1
        }
        return count
    }
}""",
        'python_boilerplate': """from collections import defaultdict

class Solution:
    def subarraySum(self, nums: list[int], k: int) -> int:
        # Write your optimal O(N) solution here
        counts = defaultdict(int)
        counts[0] = 1
        curr_sum = 0
        res = 0
        for n in nums:
            curr_sum += n
            res += counts[curr_sum - k]
            counts[curr_sum] += 1
        return res""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public int subarraySum(int[] nums, int k) {
        // Write your optimal O(N) solution here
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        int curr = 0, count = 0;
        for (int n : nums) {
            curr += n;
            count += map.getOrDefault(curr - k, 0);
            map.put(curr, map.getOrDefault(curr, 0) + 1);
        }
        return count;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(nums, k) {
        const map = { 0: 1 };
        let curr = 0, count = 0;
        for (let i = 0; i < nums.length; i++) {
          curr += nums[i];
          count += (map[curr - k] || 0);
          map[curr] = (map[curr] || 0) + 1;
        }
        return count;
      }

      const tests = [
        { nums: [1,1,1], k: 2, expected: 2 },
        { nums: [1,2,3], k: 3, expected: 2 },
        { nums: [1,-1,1,-1], k: 0, expected: 4 },
        { nums: [3], k: 3, expected: 1 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.nums, t.k);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: nums=[${t.nums}], k=${t.k} &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
        resultsDiv.appendChild(row);
      });

      if (allPassed) {
        statusSpan.textContent = "All test cases passed cleanly!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "\"First, let me confirm the constraints: can nums contain negative numbers or zeros? Yes, values range from -1000 to 1000, and k can be positive, negative, or zero. Because negative numbers are allowed, a standard sliding window will fail because expanding or shrinking does not have monotonic sum behavior.\"",
        'phase2': "\"The optimal approach uses a Prefix Sum combined with a Hash Map. For any contiguous subarray from index j to index i, the sum is `prefixSum[i] - prefixSum[j-1]`. If this sum equals k, then `prefixSum[j-1] = prefixSum[i] - k`. As we traverse the array, we maintain a running sum `currSum` and ask the map: how many times have we seen a prefix sum equal to `currSum - k`? We add that count to our total, and then record `currSum` into the map. We initialize `map[0] = 1` to handle the case where a prefix from the very start of the array equals k directly.\"",
        'phase3': "\"Let me write the code. I create a HashMap `map` and put (0, 1). Variables `currSum = 0` and `count = 0`. Loop through each `num` in `nums`: `currSum += num`, `count += map.getOrDefault(currSum - k, 0)`, and `map.put(currSum, map.getOrDefault(currSum, 0) + 1)`. Finally, return `count`.\"",
        'phase4': "\"Tracing with `[1, 2, 3]` and `k=3`. Init map has `{0: 1}`. At 1: sum=1, need -2 (0 in map). Map gets `{1: 1}`. At 2: sum=3, need 3-3=0 (1 in map). Count becomes 1. Map gets `{3: 1}`. At 3: sum=6, need 6-3=3 (1 in map). Count becomes 2. Map gets `{6: 1}`. Result is 2 (subarrays `[1, 2]` and `[3]`). Negative numbers work seamlessly.\"",
        'senior_followup': "\"In streaming architectures with high cardinality sums, we can use a Count-Min Sketch or sliding reservoir to estimate frequency counts with bounded memory.\""
    },
    'quiz': [
        {
            'question': "Why is `map[0] = 1` initialized before processing any elements?",
            'options': [
                "To account for valid subarrays that start at index 0 whose sum equals k",
                "To prevent null pointer exceptions when looking up zero in the map",
                "To tell the hash map that all numbers in the array are non-negative",
                "To reserve the first hash bucket in memory for CPU cache optimization"
            ],
            'correct_index': 0,
            'explanation': "If a running sum from the beginning of the array equals k (`currSum == k`), then `currSum - k == 0`. Initializing `map[0] = 1` ensures this entire prefix subarray is counted."
        },
        {
            'question': "Why does a two-pointer sliding window fail on this problem?",
            'options': [
                "Negative numbers mean expanding or shrinking the window is non-monotonic",
                "Two pointers require random access arrays which are prohibited in LeetCode",
                "Sliding window cannot run in O(N) time when k is an odd integer",
                "The two pointer approach triggers index out of bounds on negative numbers"
            ],
            'correct_index': 0,
            'explanation': "Sliding window relies on the property that expanding right increases the sum and moving left decreases it. With negative numbers, adding an element can decrease the sum and removing an element can increase it, breaking the sliding window invariant."
        }
    ]
}
