import sys
sys.path.append('scripts')

p90 = {
    'num': 90,
    'lc': 42,
    'title': "Trapping Rain Water",
    'category': "Two Pointers",
    'difficulty': "Hard",
    'pass_label': "Pass 3",
    'lc_slug': "trapping-rain-water",
    'verbatim_statement': """<p>Given <code>n</code> non-negative integers representing an elevation map where the width of each bar is <code>1</code>, compute how much water it can trap after raining.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'height = [0,1,0,2,1,0,1,3,2,1,2,1]', 'output': '6\nExplanation: The above elevation map (black section) is represented by array [0,1,0,2,1,0,1,3,2,1,2,1]. In this case, 6 units of rain water (blue section) are being trapped.'},
        {'title': "Example 2", 'input': 'height = [4,2,0,3,2,5]', 'output': '9'}
    ],
    'constraints': [
        "n == height.length",
        "1 <= n <= 2 * 10^4",
        "0 <= height[i] <= 10^5"
    ],
    'plain_english': "Water trapped above any individual bar `i` is determined entirely by the bottleneck between the highest wall to its left and the highest wall to its right: `water[i] = max(0, min(maxLeft, maxRight) - height[i])`. Instead of precomputing prefix and suffix max arrays in O(N) space, we can converge inward with two pointers `l` and `r`. If `maxLeft < maxRight`, we know for certain that the left side is the restricting bottleneck (because there is a wall at `r` at least as tall as `maxLeft`), so we can calculate water at `l`, update `maxLeft = max(maxLeft, height[l])`, and increment `l`. Otherwise, the right side is the bottleneck, so we calculate water at `r`, update `maxRight`, and decrement `r`.",
    'boundary_checks': {
        'min_base': "Fewer than 3 bars <code>height = [1, 2]</code> or <code>height = []</code> &rarr; Cannot form a valley; traps 0 water.",
        'best_exit': "Monotonic slope <code>[1, 2, 3, 4]</code> or <code>[4, 3, 2, 1]</code> &rarr; No depression exists; traps 0 water.",
        'worst_scan': "Deep canyon <code>[5, 0, 0, 0, 5]</code> &rarr; Traps 5 units across 3 slots = 15 total."
    },
    'mental_model': {
        'analogy_title': "The Two Pointers Valley Flood",
        'analogy_text': "Imagine two hikers walking toward each other from opposite sides of a mountain range. The hiker on the lower elevation peak determines the maximum possible water level for the side they are standing on, because no matter what terrain lies between the hikers, the opposite hiker is standing on a peak that is at least as tall. As the lower hiker steps inward, any dip below their peak traps water. Whichever hiker has the lower peak advances.",
        'aha_moment': "You do NOT need to know the exact rightMax to compute water at `l`! As long as `maxLeft < maxRight`, the left wall is guaranteed to be the limiting bottleneck. The water level is locked at `maxLeft`.",
        'trigger': "Trapping water / volume calculation between boundaries &rarr; Two Pointers inward convergence with running boundary maxes.",
        'sisters': [
            {'name': "Container With Most Water", 'lc': "LC #11"},
            {'name': "Trapping Rain Water II (3D Grid)", 'lc': "LC #407"},
            {'name': "Largest Rectangle in Histogram", 'lc': "LC #84"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(1) Space",
        'naive_text': "For every index i, scan all elements to the left to find maxLeft, and scan all elements to the right to find maxRight.",
        'wasted_work': "Repeatedly scanning the entire array for each bar to find boundary maximums.",
        'optimal_complexity': "O(N) Time, O(1) Auxiliary Space",
        'optimal_text': "Two pointers `l` and `r` tracking `maxLeft` and `maxRight` converging inward in a single pass.",
        'core_invariant': "At all times, water added at pointer `p` is strictly bounded by `min(maxLeft, maxRight)`, with zero risk of overcounting.",
        'table_rows': [
            {'name': "1. Brute Force Scan", 'time': "O(N^2)", 'space': "O(1)", 'note': "Scans entire array per bar; times out on N = 20,000."},
            {'name': "2. Prefix & Suffix Max Arrays", 'time': "O(N)", 'space': "O(N)", 'note': "Precomputes leftMax[i] and rightMax[i]; 3 passes."},
            {'name': "★ 3. Two Pointers Inward Convergence", 'time': "O(N)", 'space': "O(1)", 'note': "Single pass; zero heap allocation; optimal cache locality."}
        ]
    },
    'trace_table': {
        'input_desc': 'height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]',
        'columns': ["Step", "l, r", "height[l], height[r]", "maxLeft, maxRight", "Bottleneck Side", "Water Added", "Total Water"],
        'rows': [
            ["1", "0, 11", "0, 1", "0, 1", "Left (0 < 1)", "max(0, 0-0) = 0", "0"],
            ["2", "1, 11", "1, 1", "1, 1", "Right (1 <= 1)", "max(0, 1-1) = 0", "0"],
            ["3", "1, 10", "1, 2", "1, 2", "Left (1 < 2)", "max(0, 1-1) = 0", "0"],
            ["4", "2, 10", "0, 2", "1, 2", "Left (1 < 2)", "max(0, 1-0) = 1", "1"],
            ["5", "3, 10", "2, 2", "2, 2", "Right (2 <= 2)", "max(0, 2-2) = 0", "1"],
            ["6", "3, 9", "2, 1", "2, 2", "Right (2 <= 2)", "max(0, 2-1) = 1", "2"],
            ["...", "...", "...", "...", "...", "...", "6"]
        ],
        'takeaway': "Total trapped water = 6. Each bar is processed exactly once in O(1) time."
    },
    'stepper': {
        'title': "Two-Pointer Trapped Water Stepper",
        'default_input': 'height=[0,1,0,2,1,0,1,3,2,1,2,1]',
        'presets': [
            {'label': "Example 1: Mixed Terrain", 'onclick': 'loadPreset([0,1,0,2,1,0,1,3,2,1,2,1])'},
            {'label': "Example 2: Canyon [4,2,0,3,2,5]", 'onclick': 'loadPreset([4,2,0,3,2,5])'},
            {'label': "V-Shape [3,0,3]", 'onclick': 'loadPreset([3,0,3])'}
        ],
        'metrics': [
            {'id': "metric-l-r", 'label': "Pointers (L, R)", 'color': "text-amber-400", 'initial': "(0, 11)"},
            {'id': "metric-max-bounds", 'label': "maxLeft | maxRight", 'color': "text-cyan-400", 'initial': "0 | 0"},
            {'id': "metric-current-add", 'label': "Water Added", 'color': "text-purple-400", 'initial': "0"},
            {'id': "metric-total-water", 'label': "Total Trapped", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p90-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[180px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace two-pointer inward convergence and water collection.',
        'pseudocode_lines': [
            "var l = 0; var r = height.size - 1",
            "var maxL = 0; var maxR = 0; var total = 0",
            "while (l < r) {",
            "    if (height[l] < height[r]) {",
            "        if (height[l] >= maxL) maxL = height[l]",
            "        else total += maxL - height[l]",
            "        l++",
            "    } else {",
            "        if (height[r] >= maxR) maxR = height[r]",
            "        else total += maxR - height[r]",
            "        r--",
            "    }",
            "}",
            "return total"
        ],
        'js_code': """
    let rawH = [0,1,0,2,1,0,1,3,2,1,2,1];

    function buildSteps(h) {
      steps = [];
      const n = h.length;
      if (n < 3) {
        steps.push({ l: 0, r: n - 1, maxL: 0, maxR: 0, added: 0, total: 0, line: 1, narration: "Less than 3 bars: cannot trap water. Return 0." });
        return;
      }

      let l = 0, r = n - 1;
      let maxL = 0, maxR = 0, total = 0;
      const water = new Array(n).fill(0);

      steps.push({
        l: l, r: r, maxL: 0, maxR: 0, added: 0, total: 0, water: [...water], line: 1,
        narration: `Starting Two-Pointer scan with L=0, R=${r}.`
      });

      while (l < r) {
        if (h[l] < h[r]) {
          if (h[l] >= maxL) {
            maxL = h[l];
            steps.push({
              l: l, r: r, maxL: maxL, maxR: maxR, added: 0, total: total, water: [...water], line: 5,
              narration: `Left height ${h[l]} >= maxL (${maxL}). Updated maxL = ${maxL}. Advance L.`
            });
          } else {
            const add = maxL - h[l];
            total += add;
            water[l] = add;
            steps.push({
              l: l, r: r, maxL: maxL, maxR: maxR, added: add, total: total, water: [...water], line: 6,
              narration: `Trapped ${add} water at index ${l} (maxL ${maxL} - height ${h[l]}). Total = ${total}.`
            });
          }
          l++;
        } else {
          if (h[r] >= maxR) {
            maxR = h[r];
            steps.push({
              l: l, r: r, maxL: maxL, maxR: maxR, added: 0, total: total, water: [...water], line: 9,
              narration: `Right height ${h[r]} >= maxR (${maxR}). Updated maxR = ${maxR}. Advance R.`
            });
          } else {
            const add = maxR - h[r];
            total += add;
            water[r] = add;
            steps.push({
              l: l, r: r, maxL: maxL, maxR: maxR, added: add, total: total, water: [...water], line: 10,
              narration: `Trapped ${add} water at index ${r} (maxR ${maxR} - height ${h[r]}). Total = ${total}.`
            });
          }
          r--;
        }
      }

      steps.push({
        l: l, r: r, maxL: maxL, maxR: maxR, added: 0, total: total, water: [...water], line: 14,
        narration: `Pointers met at index ${l}. Total trapped rain water = ${total} units.`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p90-stage');
      if (!stage) return;
      document.getElementById('metric-l-r').textContent = `(${step.l}, ${step.r})`;
      document.getElementById('metric-max-bounds').textContent = `${step.maxL} | ${step.maxR}`;
      document.getElementById('metric-current-add').textContent = `+${step.added}`;
      document.getElementById('metric-total-water').textContent = step.total;

      const maxVal = Math.max(...rawH, 5);
      let html = '<div class="flex items-end gap-1.5 h-36 px-4 py-2 border-b border-slate-700 w-full max-w-xl justify-center">';
      for (let i = 0; i < rawH.length; i++) {
        const barH = (rawH[i] / maxVal) * 110;
        const watH = step.water ? (step.water[i] / maxVal) * 110 : 0;
        const isL = (step.l === i);
        const isR = (step.r === i);
        const borderCls = isL ? 'ring-2 ring-amber-400' : (isR ? 'ring-2 ring-cyan-400' : '');

        html += `<div class="flex flex-col items-center justify-end h-full w-7">
                   <div class="w-full flex flex-col justify-end items-center ${borderCls}">
                     ${watH > 0 ? `<div style="height: ${watH}px" class="w-full bg-cyan-500/60 border-t border-cyan-400"></div>` : ''}
                     <div style="height: ${barH}px" class="w-full bg-slate-700 border-t border-slate-500"></div>
                   </div>
                   <span class="text-[9px] font-mono text-slate-400 mt-1">${rawH[i]}</span>
                   <span class="text-[8px] font-mono ${isL ? 'text-amber-400 font-bold' : (isR ? 'text-cyan-400 font-bold' : 'text-slate-600')}">${isL ? 'L' : (isR ? 'R' : i)}</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(h) {
      rawH = h;
      buildSteps(h);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawH);
"""
    },
    'approaches': [
        {
            'name': "Two Pointers Inward Convergence",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(1) Space",
            'summary': "Two pointers `l` and `r` converging inward. Whichever boundary max is smaller determines the bottleneck for that pointer, allowing immediate water accumulation and pointer advance in single pass.",
            'kotlin_code': """class Solution {
    fun trap(height: IntArray): Int {
        if (height.size < 3) return 0

        var l = 0
        var r = height.size - 1
        var maxL = 0
        var maxR = 0
        var totalWater = 0

        while (l < r) {
            if (height[l] < height[r]) {
                if (height[l] >= maxL) {
                    maxL = height[l]
                } else {
                    totalWater += maxL - height[l]
                }
                l++
            } else {
                if (height[r] >= maxR) {
                    maxR = height[r]
                } else {
                    totalWater += maxR - height[r]
                }
                r--
            }
        }

        return totalWater
    }
}""",
            'python_code': """class Solution:
    def trap(self, height: List[int]) -> int:
        if len(height) < 3:
            return 0
            
        l, r = 0, len(height) - 1
        max_l, max_r = 0, 0
        total_water = 0
        
        while l < r:
            if height[l] < height[r]:
                if height[l] >= max_l:
                    max_l = height[l]
                else:
                    total_water += max_l - height[l]
                l += 1
            else:
                if height[r] >= max_r:
                    max_r = height[r]
                else:
                    total_water += max_r - height[r]
                r -= 1
                
        return total_water""",
            'java_code': """class Solution {
    public int trap(int[] height) {
        if (height.length < 3) return 0;

        int l = 0;
        int r = height.length - 1;
        int maxL = 0;
        int maxR = 0;
        int totalWater = 0;

        while (l < r) {
            if (height[l] < height[r]) {
                if (height[l] >= maxL) {
                    maxL = height[l];
                } else {
                    totalWater += maxL - height[l];
                }
                l++;
            } else {
                if (height[r] >= maxR) {
                    maxR = height[r];
                } else {
                    totalWater += maxR - height[r];
                }
                r--;
            }
        }

        return totalWater;
    }
}""",
            'memory_breakdown': [
                {'component': "Pointers & Scalars", 'cost': "O(1)", 'description': "Only scalar registers l, r, maxL, maxR, and totalWater. Zero heap allocations."}
            ]
        },
        {
            'name': "Prefix & Suffix Max DP Arrays",
            'badge': "Standard",
            'color': "blue",
            'complexity': "O(N) Time, O(N) Space",
            'summary': "Precompute `leftMax[i]` from left to right and `rightMax[i]` from right to left. Then sum `min(leftMax[i], rightMax[i]) - height[i]`.",
            'kotlin_code': """class SolutionDP {
    fun trap(height: IntArray): Int {
        val n = height.size
        if (n < 3) return 0

        val leftMax = IntArray(n)
        val rightMax = IntArray(n)

        leftMax[0] = height[0]
        for (i in 1 until n) {
            leftMax[i] = maxOf(leftMax[i - 1], height[i])
        }

        rightMax[n - 1] = height[n - 1]
        for (i in n - 2 downTo 0) {
            rightMax[i] = maxOf(rightMax[i + 1], height[i])
        }

        var total = 0
        for (i in 0 until n) {
            total += minOf(leftMax[i], rightMax[i]) - height[i]
        }
        return total
    }
}""",
            'python_code': """class SolutionDP:
    def trap(self, height: List[int]) -> int:
        n = len(height)
        if n < 3:
            return 0
            
        left_max = [0] * n
        right_max = [0] * n
        
        left_max[0] = height[0]
        for i in range(1, n):
            left_max[i] = max(left_max[i - 1], height[i])
            
        right_max[n - 1] = height[n - 1]
        for i in range(n - 2, -1, -1):
            right_max[i] = max(right_max[i + 1], height[i])
            
        return sum(min(left_max[i], right_max[i]) - height[i] for i in range(n))""",
            'java_code': """class SolutionDP {
    public int trap(int[] height) {
        int n = height.length;
        if (n < 3) return 0;

        int[] leftMax = new int[n];
        int[] rightMax = new int[n];

        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        }

        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }

        int total = 0;
        for (int i = 0; i < n; i++) {
            total += Math.min(leftMax[i], rightMax[i]) - height[i];
        }
        return total;
    }
}""",
            'memory_breakdown': [
                {'component': "DP Arrays", 'cost': "O(N)", 'description': "Two arrays of size N for left and right maximums."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun trap(height: IntArray): Int {
    // Write your solution here
    return 0
}""",
        'python_boilerplate': """def trap(height: List[int]) -> int:
    # Write your solution here
    return 0""",
        'java_boilerplate': """public int trap(int[] height) {
    // Write your solution here
    return 0;
}""",
        'js_test_runner': """
function trap(height) {
    if (height.length < 3) return 0;
    let l = 0, r = height.length - 1;
    let maxL = 0, maxR = 0, total = 0;
    while (l < r) {
        if (height[l] < height[r]) {
            if (height[l] >= maxL) maxL = height[l];
            else total += maxL - height[l];
            l++;
        } else {
            if (height[r] >= maxR) maxR = height[r];
            else total += maxR - height[r];
            r--;
        }
    }
    return total;
}

const tests = [
    { input: [0,1,0,2,1,0,1,3,2,1,2,1], expected: 6 },
    { input: [4,2,0,3,2,5], expected: 9 },
    { input: [1,2], expected: 0 }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = trap(t.input);
    const ok = output === t.expected;
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${output}, Expected: ${t.expected})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's establish constraints: we are given an elevation map where each bar has width 1. Water can only be trapped between taller bars. If the array length is less than 3, no valley can exist, so the answer is immediately 0.",
        'phase2': "The fundamental insight is that the water trapped above bar `i` is determined entirely by the bottleneck `min(leftMax, rightMax) - height[i]`. While we can compute this using two $O(N)$ prefix/suffix arrays, we can do it in $O(1)$ auxiliary space using two pointers.",
        'phase3': "We initialize pointers `l` at 0 and `r` at `n - 1`, tracking `maxLeft` and `maxRight`. At each step, if `height[l] < height[r]`, we know with 100% mathematical certainty that `maxLeft` is the limiting bottleneck, regardless of what unvisited terrain lies between them. Thus we process `l` and advance it. Otherwise, we process `r` and decrement it.",
        'phase4': "Let's trace: on `[4, 2, 0, 3, 2, 5]`, `l` starts at 4 and `r` at 5. Since 4 < 5, the left side advances, adding 2 units above the 2 bar, and 4 units above the 0 bar. The loop executes in $O(N)$ time with strictly $O(1)$ space.",
        'senior_followup': "In 3D terrain (Trapping Rain Water II), two pointers cannot be used because water spills in 4 cardinal directions. We must generalize this to Dijkstra's algorithm using a Min-Heap initialized with all boundary cells, eroding inward."
    },
    'quiz': [
        {
            'question': "Why is it mathematically sound to advance pointer `l` when `height[l] < height[r]` without knowing the absolute maximum to the right of `l`?",
            'options': [
                "Because `height[r]` is already >= `height[l]`, guaranteeing that `maxRight` will never be the limiting bottleneck for `l`",
                "Because the array is guaranteed to be sorted",
                "Because rightMax is always equal to height[r]",
                "Because water flows exclusively from left to right"
            ],
            'correct_index': 0,
            'explanation': "Since height[r] is already taller than height[l], any water trapped at l is capped by maxLeft (or height[l] itself). The exact global maximum to the right cannot restrict water below maxLeft."
        },
        {
            'question': "What is the auxiliary memory consumption of the optimal Two-Pointer approach?",
            'options': [
                "O(1) auxiliary space using four primitive integer variables",
                "O(N) auxiliary space to cache intermediate water levels",
                "O(log N) due to recursive divide-and-conquer",
                "O(N) to store monotonic stack frames"
            ],
            'correct_index': 0,
            'explanation': "The two-pointer technique uses only scalar pointers (l, r, maxLeft, maxRight, totalWater), achieving optimal O(1) space."
        }
    ]
}

p91 = {
    'num': 91,
    'lc': 76,
    'title': "Minimum Window Substring",
    'category': "Sliding Window",
    'difficulty': "Hard",
    'pass_label': "Pass 3",
    'lc_slug': "minimum-window-substring",
    'verbatim_statement': """<p>Given two strings <code>s</code> and <code>t</code> of lengths <code>m</code> and <code>n</code> respectively, return <em>the <strong>minimum window substring</strong> of </em><code>s</code><em> such that every character in </em><code>t</code><em> (<strong>including duplicates</strong>) is included in the window</em>. If there is no such substring, return <em>the empty string </em><code>""</code>.</p>
<p>The testcases will be generated such that the answer is <strong>unique</strong>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 's = "ADOBECODEBANC", t = "ABC"', 'output': '"BANC"\nExplanation: The minimum window substring "BANC" includes \'A\', \'B\', and \'C\' from string t.'},
        {'title': "Example 2", 'input': 's = "a", t = "a"', 'output': '"a"'},
        {'title': "Example 3", 'input': 's = "a", t = "aa"', 'output': '""\nExplanation: Both \'a\'s from t must be included in the window. Since the largest window of s only has one \'a\', return empty string.'}
    ],
    'constraints': [
        "m == s.length",
        "n == t.length",
        "1 <= m, n <= 10^5",
        "s and t consist of uppercase and lowercase English letters."
    ],
    'plain_english': "We want to find the shortest contiguous slice of `s` that contains all characters of `t` with at least their required frequencies. We count the target character requirements into a frequency map `needMap` and track how many unique required characters have their quota met (`have == need`). We expand `r` to include characters into `windowMap`. When a character's count in `windowMap` matches its requirement in `needMap`, we increment `have`. Once `have == need`, the window is valid: we record the substring if it's the shortest seen so far, and greedily shrink `l` until the window is no longer valid. Then we continue expanding `r`.",
    'boundary_checks': {
        'min_base': "s shorter than t <code>s = 'a', t = 'aa'</code> &rarr; Impossible to satisfy; returns `\"\"`.",
        'best_exit': "Exact match <code>s = 'a', t = 'a'</code> &rarr; Returns `\"a\"` immediately.",
        'worst_scan': "Target scattered across entire string with duplicates &rarr; Shrinks from left efficiently in O(M + N) total steps."
    },
    'mental_model': {
        'analogy_title': "The Grocery Shopping Cart",
        'analogy_text': "Think of string `t` as a shopping grocery list: 2 apples, 1 banana, 1 milk (`need = 3 unique items`). You walk down the supermarket aisle `s` pushing your cart. As you walk forward (expanding `r`), you toss items into your cart. The moment your cart has at least 2 apples, 1 banana, and 1 milk (`have == need`), your list is satisfied! Now you stop and look at your rear cart wheel (shrinking `l`): if the first item you picked up was a useless box of cereal or an extra apple you didn't need, you drop it back on the shelf to make your trip as short as possible.",
        'aha_moment': "Comparing two frequency maps on every character addition would cost O(128) per step. By maintaining a single integer `have` that only increments when `windowMap[c] == needMap[c]`, validation checks become strictly O(1)!",
        'trigger': "Find shortest substring containing all required target characters &rarr; Variable Sliding Window with `have` / `need` match counters.",
        'sisters': [
            {'name': "Permutation in String", 'lc': "LC #567"},
            {'name': "Find All Anagrams in a String", 'lc': "LC #438"},
            {'name': "Substring with Concatenation of All Words", 'lc': "LC #30"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(M^2 * |Σ|) Time, O(|Σ|) Space",
        'naive_text': "Generate all O(M^2) substrings of s and check if each contains all characters of t.",
        'wasted_work': "Re-evaluating overlapping substrings from scratch rather than sliding window boundaries.",
        'optimal_complexity': "O(M + N) Time, O(|Σ|) Space",
        'optimal_text': "Two-pointer sliding window where each pointer advances at most M times, with O(1) condition checks via `have` counter.",
        'core_invariant': "Whenever `have == need`, the current window `s[l..r]` is guaranteed to contain every required character from `t` with sufficient multiplicity.",
        'table_rows': [
            {'name': "All Substrings Generation", 'time': "O(M^2 * |Σ|)", 'space': "O(|Σ|)", 'note': "Vastly too slow for M = 100,000 (10^10 operations)."},
            {'name': "★ Two-Pointer Sliding Window", 'time': "O(M + N)", 'space': "O(|Σ|)", 'note': "Single pass; each character processed at most twice (entered & exited)."}
        ]
    },
    'trace_table': {
        'input_desc': 's = "ADOBECODEBANC", t = "ABC" (need: A=1, B=1, C=1; needCount = 3)',
        'columns': ["r char", "l", "Window String", "have / need", "Valid?", "Action & Best Window"],
        'rows': [
            ["A (0)", "0", "\"A\"", "1 / 3", "No", "Expand r"],
            ["B (3)", "0", "\"ADOB\"", "2 / 3", "No", "Expand r"],
            ["C (5)", "0", "\"ADOBEC\"", "3 / 3", "YES", "Valid! Min len = 6 (\"ADOBEC\"). Shrink l to 1."],
            ["O (6)", "1", "\"DOBECO\"", "2 / 3", "No", "L lost 'A', have drops to 2. Expand r."],
            ["A (10)", "1", "\"DOBECODEBA\"", "3 / 3", "YES", "Valid! Shrink l from 1 to 9."],
            ["C (12)", "9", "\"BANC\"", "3 / 3", "YES", "Valid! Min len = 4 (\"BANC\"). Optimal!"]
        ],
        'takeaway': "Returns `\"BANC\"` with length 4, containing 'A', 'B', and 'C'."
    },
    'stepper': {
        'title': "Sliding Window Substring Stepper",
        'default_input': 's="ADOBECODEBANC", t="ABC"',
        'presets': [
            {'label': "Example 1: ADOBECODEBANC", 'onclick': 'loadPreset("ADOBECODEBANC", "ABC")'},
            {'label': "Single Char: 'a', 'a'", 'onclick': 'loadPreset("a", "a")'},
            {'label': "Impossible: 'a', 'aa'", 'onclick': 'loadPreset("a", "aa")'}
        ],
        'metrics': [
            {'id': "metric-window-range", 'label': "Window [L .. R]", 'color': "text-amber-400", 'initial': "[0 .. 0]"},
            {'id': "metric-have-need", 'label': "have / need", 'color': "text-cyan-400", 'initial': "0 / 3"},
            {'id': "metric-best-window", 'label': "Best Window", 'color': "text-emerald-400", 'initial': "None"},
            {'id': "metric-action-sw", 'label': "Current Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="p91-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace sliding window expansion and contraction.',
        'pseudocode_lines': [
            "val need = IntArray(128); for (c in t) need[c.code]++",
            "var required = t.toSet().size; var have = 0",
            "var l = 0; var minLen = Int.MAX_VALUE; var start = 0",
            "for (r in s.indices) {",
            "    val c = s[r]; window[c.code]++",
            "    if (window[c.code] == need[c.code]) have++",
            "    while (have == required) {",
            "        if (r - l + 1 < minLen) { minLen = r - l + 1; start = l }",
            "        val leftChar = s[l]; window[leftChar.code]--",
            "        if (window[leftChar.code] < need[leftChar.code]) have--",
            "        l++",
            "    }",
            "}",
            "return if (minLen == Int.MAX_VALUE) \"\" else s.substring(start, start + minLen)"
        ],
        'js_code': """
    let rawS = "ADOBECODEBANC";
    let rawT = "ABC";

    function buildSteps(s, t) {
      steps = [];
      const needMap = {};
      for (let ch of t) needMap[ch] = (needMap[ch] || 0) + 1;
      const needCount = Object.keys(needMap).length;

      const windowMap = {};
      let have = 0;
      let l = 0;
      let minLen = Infinity;
      let bestStart = 0;

      steps.push({
        l: 0, r: 0, have: 0, need: needCount, best: "", action: "Start", line: 2,
        narration: `Need target characters: ${JSON.stringify(needMap)}. Total required unique = ${needCount}.`
      });

      for (let r = 0; r < s.length; r++) {
        const c = s[r];
        windowMap[c] = (windowMap[c] || 0) + 1;

        if (needMap[c] && windowMap[c] === needMap[c]) {
          have++;
        }

        steps.push({
          l: l, r: r, have: have, need: needCount, best: minLen === Infinity ? "" : s.substring(bestStart, bestStart + minLen),
          action: `Expand R to ${r} ('${c}')`, line: 5,
          narration: `Added '${c}' at index ${r}. have = ${have}/${needCount}.`
        });

        while (have === needCount) {
          const len = r - l + 1;
          if (len < minLen) {
            minLen = len;
            bestStart = l;
          }

          const currentBest = s.substring(bestStart, bestStart + minLen);
          steps.push({
            l: l, r: r, have: have, need: needCount, best: currentBest,
            action: `Valid Window! Shrinking L from ${l}`, line: 8,
            narration: `Window '${s.substring(l, r + 1)}' is valid! Current best = '${currentBest}'.`
          });

          const leftChar = s[l];
          windowMap[leftChar]--;
          if (needMap[leftChar] && windowMap[leftChar] < needMap[leftChar]) {
            have--;
          }
          l++;
        }
      }

      const res = minLen === Infinity ? "" : s.substring(bestStart, bestStart + minLen);
      steps.push({
        l: l, r: s.length - 1, have: have, need: needCount, best: res,
        action: "Finished", line: 14,
        narration: res ? `Scan complete! Minimum window substring is '${res}'.` : "No valid window found. Return empty string ''."
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p91-stage');
      if (!stage) return;
      document.getElementById('metric-window-range').textContent = `[${step.l} .. ${step.r}]`;
      document.getElementById('metric-have-need').textContent = `${step.have} / ${step.need}`;
      document.getElementById('metric-best-window').textContent = step.best ? `'${step.best}'` : "None";
      document.getElementById('metric-action-sw').textContent = step.action;

      let html = '<div class="flex items-center gap-1 overflow-x-auto max-w-full px-2 py-4">';
      for (let i = 0; i < rawS.length; i++) {
        const inWindow = (i >= step.l && i <= step.r);
        const isL = (i === step.l);
        const isR = (i === step.r);
        let bg = 'bg-slate-800 text-slate-400 border-slate-700';
        if (inWindow) {
          bg = 'bg-emerald-950/60 text-emerald-300 border-emerald-500 font-bold';
        }
        if (isL) bg += ' ring-2 ring-amber-400';
        if (isR) bg += ' ring-2 ring-cyan-400';

        html += `<div class="flex flex-col items-center min-w-[28px]">
                   <div class="w-7 h-9 rounded flex items-center justify-center font-mono text-xs border ${bg}">
                     ${rawS[i]}
                   </div>
                   <span class="text-[8px] font-mono text-slate-500 mt-1">${i}</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(s, t) {
      rawS = s;
      rawT = t;
      buildSteps(s, t);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawS, rawT);
"""
    },
    'approaches': [
        {
            'name': "Sliding Window with IntArray Frequency Vector",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(M + N) Time, O(1) Space",
            'summary': "Two pointers `l` and `r` with fixed size 128 integer array. Increment `have` when `window[c] == need[c]`. Once `have == required`, record minimum length and greedily shrink `l`.",
            'kotlin_code': """class Solution {
    fun minWindow(s: String, t: String): String {
        if (s.length < t.length) return ""

        val need = IntArray(128)
        var required = 0
        for (c in t) {
            if (need[c.code] == 0) required++
            need[c.code]++
        }

        val window = IntArray(128)
        var have = 0
        var l = 0
        var minLen = Int.MAX_VALUE
        var startIdx = 0

        for (r in s.indices) {
            val c = s[r]
            window[c.code]++

            if (need[c.code] > 0 && window[c.code] == need[c.code]) {
                have++
            }

            while (have == required) {
                val currentLen = r - l + 1
                if (currentLen < minLen) {
                    minLen = currentLen
                    startIdx = l
                }

                val leftChar = s[l]
                window[leftChar.code]--
                if (need[leftChar.code] > 0 && window[leftChar.code] < need[leftChar.code]) {
                    have--
                }
                l++
            }
        }

        return if (minLen == Int.MAX_VALUE) "" else s.substring(startIdx, startIdx + minLen)
    }
}""",
            'python_code': """from collections import Counter

class Solution:
    def minWindow(self, s: str, t: str) -> str:
        if len(s) < len(t):
            return ""
            
        need = Counter(t)
        required = len(need)
        window = {}
        have = 0
        
        min_len = float('inf')
        best_range = (0, 0)
        l = 0
        
        for r, c in enumerate(s):
            window[c] = window.get(c, 0) + 1
            if c in need and window[c] == need[c]:
                have += 1
                
            while have == required:
                if (r - l + 1) < min_len:
                    min_len = r - l + 1
                    best_range = (l, r)
                    
                left_c = s[l]
                window[left_c] -= 1
                if left_c in need and window[left_c] < need[left_c]:
                    have -= 1
                l += 1
                
        return "" if min_len == float('inf') else s[best_range[0]:best_range[1] + 1]""",
            'java_code': """class Solution {
    public String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";

        int[] need = new int[128];
        int required = 0;
        for (char c : t.toCharArray()) {
            if (need[c] == 0) required++;
            need[c]++;
        }

        int[] window = new int[128];
        int have = 0;
        int l = 0;
        int minLen = Integer.MAX_VALUE;
        int startIdx = 0;

        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            window[c]++;

            if (need[c] > 0 && window[c] == need[c]) {
                have++;
            }

            while (have == required) {
                int len = r - l + 1;
                if (len < minLen) {
                    minLen = len;
                    startIdx = l;
                }

                char leftChar = s.charAt(l);
                window[leftChar]--;
                if (need[leftChar] > 0 && window[leftChar] < need[leftChar]) {
                    have--;
                }
                l++;
            }
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(startIdx, startIdx + minLen);
    }
}""",
            'memory_breakdown': [
                {'component': "Alphabet Frequency Array", 'cost': "O(1)", 'description': "Fixed-size 128 integer array bounded by ASCII alphabet size."},
                {'component': "Pointers and Range Coordinates", 'cost': "O(1)", 'description': "Scalar registers l, r, startIdx, minLen, have, required."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun minWindow(s: String, t: String): String {
    // Write your solution here
    return ""
}""",
        'python_boilerplate': """def minWindow(s: str, t: str) -> str:
    # Write your solution here
    return """,
        'java_boilerplate': """public String minWindow(String s, String t) {
    // Write your solution here
    return "";
}""",
        'js_test_runner': """
function minWindow(s, t) {
    if (s.length < t.length) return "";
    const need = new Array(128).fill(0);
    let required = 0;
    for (let c of t) {
        const code = c.charCodeAt(0);
        if (need[code] === 0) required++;
        need[code]++;
    }
    const window = new Array(128).fill(0);
    let have = 0, l = 0, minLen = Infinity, start = 0;
    for (let r = 0; r < s.length; r++) {
        const code = s.charCodeAt(r);
        window[code]++;
        if (need[code] > 0 && window[code] === need[code]) have++;
        while (have === required) {
            if (r - l + 1 < minLen) {
                minLen = r - l + 1;
                start = l;
            }
            const leftCode = s.charCodeAt(l);
            window[leftCode]--;
            if (need[leftCode] > 0 && window[leftCode] < need[leftCode]) have--;
            l++;
        }
    }
    return minLen === Infinity ? "" : s.substring(start, start + minLen);
}

const tests = [
    { s: "ADOBECODEBANC", t: "ABC", expected: "BANC" },
    { s: "a", t: "a", expected: "a" },
    { s: "a", t: "aa", expected: "" }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = minWindow(t.s, t.t);
    const ok = output === t.expected;
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: "${output}", Expected: "${t.expected}")`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "Let's clarify the problem: given strings `s` and `t`, find the shortest substring in `s` that contains all characters of `t` with at least their required multiplicities. If no such substring exists, return empty string.",
        'phase2': "A naive solution would check all $O(M^2)$ substrings, costing $O(M^2 \\cdot |\\Sigma|)$. Instead, we use a classic two-pointer sliding window where `r` expands and `l` contracts.",
        'phase3': "To achieve $O(1)$ validity checks rather than re-comparing the whole alphabet, we track `have` and `need`. `need` is the number of distinct characters in `t`, and `have` is incremented only when `window[c] == need[c]`.",
        'phase4': "Once `have == need`, the current window is valid. We record the substring if it's the shortest seen, then shrink `l` until `have < need`. Because each pointer moves strictly forward from 0 to $M$, the total runtime is $O(M + N)$ with $O(|\\Sigma|)$ space.",
        'senior_followup': "If `s` contains millions of characters but only a sparse handful appear in `t`, we can build a filtered list `filteredS = [(index, char)]` containing only the relevant characters, allowing the sliding window to jump directly between candidate positions."
    },
    'quiz': [
        {
            'question': "Why is `have` incremented only when `window[c] == need[c]` rather than `window[c] >= need[c]`?",
            'options': [
                "To ensure `have` is incremented exactly once per satisfied character type, avoiding duplicate increments when extra occurrences arrive",
                "Because exceeding the target count invalidates the window",
                "Because Java arrays cannot handle counts greater than 1",
                "To maintain alphabetical sorting order"
            ],
            'correct_index': 0,
            'explanation': "If window[c] exceeds need[c], the requirement for character c was already satisfied previously. Incrementing only at exact equality ensures have reaches required precisely when all distinct quotas are met."
        },
        {
            'question': "What is the time complexity of the optimal Two-Pointer Sliding Window approach on strings of length M and N?",
            'options': [
                "O(M + N) because both left and right pointers advance at most M times, and t is scanned once in N steps",
                "O(M * N) because left pointer can re-scan the entire string for every step of right pointer",
                "O(M log N) using binary search",
                "O(2^M) exponential recursion"
            ],
            'correct_index': 0,
            'explanation': "Both the left pointer and right pointer only move forward and never backtrack, so each character in s is visited at most twice, yielding linear O(M + N) runtime."
        }
    ]
}

p92 = {
    'num': 92,
    'lc': 239,
    'title': "Sliding Window Maximum",
    'category': "Stacks, Queues & Monotonic Stacks",
    'difficulty': "Hard",
    'pass_label': "Pass 3",
    'lc_slug': "sliding-window-maximum",
    'verbatim_statement': """<p>You are given an array of integers <code>nums</code>, there is a sliding window of size <code>k</code> which is moving from the very left of the array to the very right. You can only see the <code>k</code> numbers in the window. Each time the sliding window moves right by one position.</p>
<p>Return <em>the max sliding window</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [1,3,-1,-3,5,3,6,7], k = 3', 'output': '[3,3,5,5,6,7]\nExplanation: \nWindow position                Max\n---------------               -----\n[1  3  -1] -3  5  3  6  7       3\n 1 [3  -1  -3] 5  3  6  7       3\n 1  3 [-1  -3  5] 3  6  7       5\n 1  3  -1 [-3  5  3] 6  7       5\n 1  3  -1  -3 [5  3  6] 7       6\n 1  3  -1  -3  5 [3  6  7]      7'},
        {'title': "Example 2", 'input': 'nums = [1], k = 1', 'output': '[1]'}
    ],
    'constraints': [
        "1 <= nums.length <= 10^5",
        "-10^4 <= nums[i] <= 10^4",
        "1 <= k <= nums.length"
    ],
    'plain_english': "As a window of length `k` slides across `nums`, we must return the maximum element in each window. A naive scan takes O(N * k) time. A max-heap takes O(N log k). We can achieve optimal O(N) linear time using a Monotonic Decreasing Deque of *indices*. When element `nums[i]` arrives: (1) Any elements at the back of the deque smaller than `nums[i]` can NEVER be the window maximum again (since `nums[i]` is both larger and will survive longer in the window), so we pop them. (2) Pop the front if its index has slid outside the window (`deque.first <= i - k`). (3) Push index `i` to the back. Once `i >= k - 1`, the element at `nums[deque.first]` is the guaranteed window maximum.",
    'boundary_checks': {
        'min_base': "Window size k = 1 <code>nums = [1, -1], k = 1</code> &rarr; Returns exact original array <code>[1, -1]</code>.",
        'best_exit': "Entire array is window <code>k == nums.length</code> &rarr; Returns single element <code>[max(nums)]</code>.",
        'worst_scan': "Strictly increasing array <code>[1, 2, 3, 4, 5], k = 3</code> &rarr; Deque keeps popping smaller back elements; front always holds latest."
    },
    'mental_model': {
        'analogy_title': "The Work Hierarchy Seniority Rule",
        'analogy_text': "Imagine a company hiring candidates over time. When a candidate arrives with skill level $X$: anyone already at the company who is LESS skilled than the new recruit and ALSO hired earlier will NEVER be the most valuable employee again—the new recruit outperforms them and will retire later! So all less skilled predecessors are removed from contention. The most valuable employee currently active is always the person standing at the very front of the line.",
        'aha_moment': "Storing INDICES in the deque instead of values allows us to verify two things simultaneously: whether an element is smaller than the current value (`nums[deque.last] < nums[i]`), and whether it has expired past the window boundary (`deque.first <= i - k`).",
        'trigger': "Sliding window query asking for min/max over contiguous subsegments of fixed size &rarr; Monotonic Deque storing indices.",
        'sisters': [
            {'name': "Shortest Subarray with Sum at Least K", 'lc': "LC #862"},
            {'name': "Jump Game VI", 'lc': "LC #1696"},
            {'name': "Constrained Subsequence Sum", 'lc': "LC #1425"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N * K) Time, O(1) Space",
        'naive_text': "Iterate through all N - K + 1 windows and take the maximum of each K elements.",
        'wasted_work': "Re-scanning all K elements from scratch on every step when only 1 element enters and 1 element leaves.",
        'optimal_complexity': "O(N) Time, O(K) Auxiliary Space",
        'optimal_text': "Monotonic Decreasing Deque of indices. Each index is pushed and popped at most once.",
        'core_invariant': "The deque indices are strictly increasing, and their corresponding values `nums[idx]` are strictly monotonically decreasing from front to back.",
        'table_rows': [
            {'name': "Brute Force Sub-array Scan", 'time': "O(N * K)", 'space': "O(1)", 'note': "N = 100,000, K = 50,000 &rarr; 5 * 10^9 operations (TLE)."},
            {'name': "Max-Heap PriorityQueue", 'time': "O(N log K)", 'space': "O(K)", 'note': "Logarithmic push/poll per step."},
            {'name': "★ Monotonic Decreasing Deque", 'time': "O(N)", 'space': "O(K)", 'note': "Amortized O(1) per element; exactly 2N total operations."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [1, 3, -1, -3, 5, 3, 6, 7], k = 3',
        'columns': ["Step i", "Element nums[i]", "Deque Indices (Values)", "Window Active?", "Window Max Appended"],
        'rows': [
            ["0", "1", "[0 (1)]", "No (i < 2)", "-"],
            ["1", "3", "[1 (3)]", "No (i < 2)", "Popped index 0 since 1 < 3"],
            ["2", "-1", "[1 (3), 2 (-1)]", "YES (window 0..2)", "nums[1] = 3"],
            ["3", "-3", "[1 (3), 2 (-1), 3 (-3)]", "YES (window 1..3)", "nums[1] = 3"],
            ["4", "5", "[4 (5)]", "YES (window 2..4)", "5 pops 3, -1, -3! nums[4] = 5"],
            ["5", "3", "[4 (5), 5 (3)]", "YES (window 3..5)", "nums[4] = 5"],
            ["6", "6", "[6 (6)]", "YES (window 4..6)", "6 pops 5, 3! nums[6] = 6"],
            ["7", "7", "[7 (7)]", "YES (window 5..7)", "7 pops 6! nums[7] = 7"]
        ],
        'takeaway': "Result array = `[3, 3, 5, 5, 6, 7]`. Linear time processing."
    },
    'stepper': {
        'title': "Monotonic Deque Window Stepper",
        'default_input': 'nums=[1,3,-1,-3,5,3,6,7], k=3',
        'presets': [
            {'label': "Example 1: [1,3,-1,-3,5,3,6,7], k=3", 'onclick': 'loadPreset([1,3,-1,-3,5,3,6,7], 3)'},
            {'label': "Decreasing: [9,8,7,6,5], k=3", 'onclick': 'loadPreset([9,8,7,6,5], 3)'},
            {'label': "Increasing: [1,2,3,4,5], k=3", 'onclick': 'loadPreset([1,2,3,4,5], 3)'}
        ],
        'metrics': [
            {'id': "metric-idx-i", 'label': "Current Index (i)", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-deque-items", 'label': "Deque (Values)", 'color': "text-cyan-400", 'initial': "[]"},
            {'id': "metric-window-max", 'label': "Current Max", 'color': "text-emerald-400", 'initial': "None"},
            {'id': "metric-action-md", 'label': "Deque Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="p92-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace monotonic deque index maintenance.',
        'pseudocode_lines': [
            "val deque = ArrayDeque<Int>()",
            "val res = IntArray(nums.size - k + 1)",
            "for (i in nums.indices) {",
            "    while (deque.isNotEmpty() && nums[deque.last()] < nums[i]) {",
            "        deque.removeLast() // Discard smaller elements",
            "    }",
            "    deque.addLast(i)",
            "    if (deque.first() <= i - k) deque.removeFirst() // Evict stale",
            "    if (i >= k - 1) res[i - k + 1] = nums[deque.first()]",
            "}",
            "return res"
        ],
        'js_code': """
    let rawNums = [1, 3, -1, -3, 5, 3, 6, 7];
    let rawK = 3;

    function buildSteps(nums, k) {
      steps = [];
      const deque = [];
      const res = [];

      steps.push({
        i: -1, deque: [], currentVal: "-", action: "Initialize Deque", res: [], line: 1,
        narration: `Starting Monotonic Deque algorithm with window size k = ${k}.`
      });

      for (let i = 0; i < nums.length; i++) {
        const val = nums[i];

        // 1. Pop smaller elements from back
        while (deque.length > 0 && nums[deque[deque.length - 1]] < val) {
          const poppedIdx = deque.pop();
          steps.push({
            i: i, deque: [...deque], currentVal: val,
            action: `Popped index ${poppedIdx} (${nums[poppedIdx]} < ${val})`, res: [...res], line: 4,
            narration: `nums[${poppedIdx}] = ${nums[poppedIdx]} is smaller than incoming ${val}. Popped from back of deque.`
          });
        }

        // 2. Add current index
        deque.push(i);

        // 3. Remove out-of-window elements from front
        if (deque[0] <= i - k) {
          const expired = deque.shift();
          steps.push({
            i: i, deque: [...deque], currentVal: val,
            action: `Evicted expired index ${expired}`, res: [...res], line: 8,
            narration: `Index ${expired} has slid outside the window [${i - k + 1} .. ${i}]. Evicted from front.`
          });
        }

        // 4. Record result
        if (i >= k - 1) {
          const maxVal = nums[deque[0]];
          res.push(maxVal);
          steps.push({
            i: i, deque: [...deque], currentVal: val,
            action: `Recorded window max = ${maxVal}`, res: [...res], line: 9,
            narration: `Window [${i - k + 1} .. ${i}] max is ${maxVal} (at front of deque). Appended to result.`
          });
        } else {
          steps.push({
            i: i, deque: [...deque], currentVal: val,
            action: `Building initial window (${i + 1}/${k})`, res: [...res], line: 7,
            narration: `Pushed index ${i}. Initial window still warming up.`
          });
        }
      }

      steps.push({
        i: nums.length - 1, deque: [...deque], currentVal: "Done",
        action: "Finished", res: [...res], line: 11,
        narration: `All windows evaluated. Final maximums: [${res.join(', ')}].`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p92-stage');
      if (!stage) return;
      document.getElementById('metric-idx-i').textContent = step.i >= 0 ? step.i : "-";
      document.getElementById('metric-deque-items').textContent = `[${step.deque.map(idx => rawNums[idx]).join(', ')}]`;
      document.getElementById('metric-window-max').textContent = step.res.length > 0 ? step.res[step.res.length - 1] : "Warming up";
      document.getElementById('metric-action-md').textContent = step.action;

      let html = '<div class="flex items-center gap-2 overflow-x-auto max-w-full px-2 py-4">';
      const winStart = Math.max(0, step.i - rawK + 1);
      const winEnd = step.i;

      for (let idx = 0; idx < rawNums.length; idx++) {
        const inWindow = (step.i >= rawK - 1 && idx >= winStart && idx <= winEnd);
        const inDeque = step.deque.includes(idx);
        const isHead = (step.deque.length > 0 && step.deque[0] === idx);

        let borderCls = 'border-slate-700 bg-slate-900 text-slate-400';
        if (inWindow) borderCls = 'border-amber-500 bg-amber-950/30 text-amber-200';
        if (inDeque) borderCls = 'border-cyan-500 bg-cyan-950/40 text-cyan-200 font-bold';
        if (isHead && inWindow) borderCls = 'border-emerald-500 bg-emerald-950/60 text-emerald-300 font-bold ring-2 ring-emerald-400';

        html += `<div class="flex flex-col items-center min-w-[36px]">
                   <div class="w-9 h-11 rounded-lg border flex flex-col items-center justify-center font-mono text-xs ${borderCls}">
                     <span>${rawNums[idx]}</span>
                   </div>
                   <span class="text-[9px] font-mono text-slate-500 mt-1">idx ${idx}</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(nums, k) {
      rawNums = nums;
      rawK = k;
      buildSteps(nums, k);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawNums, rawK);
"""
    },
    'approaches': [
        {
            'name': "Monotonic Decreasing Deque of Indices",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(K) Space",
            'summary': "Double-ended queue (ArrayDeque) storing indices in decreasing order of their corresponding values. Prunes smaller back elements and slides out-of-bounds front elements in amortized O(1).",
            'kotlin_code': """class Solution {
    fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
        if (nums.isEmpty() || k <= 0) return IntArray(0)

        val n = nums.size
        val res = IntArray(n - k + 1)
        val deque = ArrayDeque<Int>() // Stores indices

        for (i in 0 until n) {
            // Remove indices that are out of current window
            if (deque.isNotEmpty() && deque.first() <= i - k) {
                deque.removeFirst()
            }

            // Remove indices whose values are smaller than nums[i]
            while (deque.isNotEmpty() && nums[deque.last()] < nums[i]) {
                deque.removeLast()
            }

            deque.addLast(i)

            // Once window reaches size k, front is the window maximum
            if (i >= k - 1) {
                res[i - k + 1] = nums[deque.first()]
            }
        }

        return res
    }
}""",
            'python_code': """from collections import deque

class Solution:
    def maxSlidingWindow(self, nums: List[int], k: int) -> List[int]:
        if not nums or k <= 0:
            return []
            
        dq = deque() # Stores indices
        res = []
        
        for i, val in enumerate(nums):
            # Remove elements outside current window
            if dq and dq[0] <= i - k:
                dq.popleft()
                
            # Discard smaller elements from back
            while dq and nums[dq[-1]] < val:
                dq.pop()
                
            dq.append(i)
            
            # Record window maximum
            if i >= k - 1:
                res.append(nums[dq[0]])
                
        return res""",
            'java_code': """class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || k <= 0) return new int[0];

        int n = nums.length;
        int[] res = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            // Evict elements outside the window
            if (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }

            // Discard elements smaller than incoming nums[i]
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.offerLast(i);

            // Record maximum for complete windows
            if (i >= k - 1) {
                res[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return res;
    }
}""",
            'memory_breakdown': [
                {'component': "Monotonic Deque", 'cost': "O(K)", 'description': "Stores at most K window indices."},
                {'component': "Result Array `res`", 'cost': "O(N - K + 1)", 'description': "Stores one maximum integer per window position."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
    // Write your solution here
    return intArrayOf()
}""",
        'python_boilerplate': """def maxSlidingWindow(nums: List[int], k: int) -> List[int]:
    # Write your solution here
    return []""",
        'java_boilerplate': """public int[] maxSlidingWindow(int[] nums, int k) {
    // Write your solution here
    return new int[0];
}""",
        'js_test_runner': """
function maxSlidingWindow(nums, k) {
    if (!nums || k <= 0) return [];
    const deque = [];
    const res = [];
    for (let i = 0; i < nums.length; i++) {
        if (deque.length > 0 && deque[0] <= i - k) deque.shift();
        while (deque.length > 0 && nums[deque[deque.length - 1]] < nums[i]) deque.pop();
        deque.push(i);
        if (i >= k - 1) res.push(nums[deque[0]]);
    }
    return res;
}

const tests = [
    { nums: [1,3,-1,-3,5,3,6,7], k: 3, expected: [3,3,5,5,6,7] },
    { nums: [1], k: 1, expected: [1] },
    { nums: [9,11], k: 2, expected: [11] }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = maxSlidingWindow(t.nums, t.k);
    const ok = JSON.stringify(output) === JSON.stringify(t.expected);
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${JSON.stringify(output)}, Expected: ${JSON.stringify(t.expected)})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "Let's clarify requirements: we are given an integer array `nums` and a sliding window of size `k`. We need to output the maximum element of every contiguous window of size `k` as it shifts right by 1.",
        'phase2': "A naive solution checks all $k$ elements in every window, which is $O(N \\cdot K)$ and times out. A max-heap gives $O(N \\log K)$ time. But we can achieve strictly optimal $O(N)$ linear time using a Monotonic Decreasing Deque.",
        'phase3': "The key property is storing *indices* in the deque. When processing index `i`, we pop elements from the back of the deque that are smaller than `nums[i]`, because `nums[i]` is both greater and will remain in the window longer. Then we pop from the front if `deque.first <= i - k`.",
        'phase4': "The element at the front of the deque is always the window maximum. Because every index is pushed onto the deque once and popped at most once, the total number of operations across the entire algorithm is bounded by $2N$, running in $O(N)$ time with $O(K)$ space.",
        'senior_followup': "In high-throughput trading or signal processing systems, replacing Java's `ArrayDeque` with an unboxed primitive circular ring buffer array `int[] q` avoids object boxing and GC allocation overhead completely."
    },
    'quiz': [
        {
            'question': "Why is each element added and removed from the deque at most once, guaranteeing O(N) total time?",
            'options': [
                "Because an element is only pushed once when its index is visited, and once popped from front or back, it is never re-inserted",
                "Because the array is partitioned into N / K independent blocks",
                "Because binary search determines the insertion point",
                "Because garbage collection automatically limits the size of ArrayDeque"
            ],
            'correct_index': 0,
            'explanation': "Each index enters the deque exactly once (during its loop iteration) and leaves at most once (either pushed out by a larger element from the back or evicted due to age from the front), yielding exactly 2N operations."
        },
        {
            'question': "What happens to elements in the deque when an incoming element is larger than all elements currently in the deque?",
            'options': [
                "All smaller elements are popped from the back until the deque is empty, and the new element becomes the sole item at the front",
                "The deque throws an IllegalStateException",
                "The deque is cleared and reconstructed using a heap",
                "The new element is placed at the front, pushing others to the back"
            ],
            'correct_index': 0,
            'explanation': "The while loop repeatedly removes smaller elements from the back because they can never serve as the maximum for any current or future window that contains the larger incoming element."
        }
    ]
}

p93 = {
    'num': 93,
    'lc': 588,
    'title': "Design In-Memory File System",
    'category': "Multi-Tier OOP & Timed State Systems",
    'difficulty': "Hard",
    'pass_label': "Pass 3",
    'lc_slug': "design-in-memory-file-system",
    'verbatim_statement': """<p>Design a data structure that represents an in-memory file system.</p>
<p>Implement the <code>FileSystem</code> class:</p>
<ul>
    <li><code>FileSystem()</code> Initializes the object of the system.</li>
    <li><code>List&lt;String&gt; ls(String path)</code>
        <ul>
            <li>If <code>path</code> is a file path, returns a list that only contains this file's name.</li>
            <li>If <code>path</code> is a directory path, returns the list of file and directory names <strong>in this directory</strong> in <strong>lexicographical order</strong>.</li>
        </ul>
    </li>
    <li><code>void mkdir(String path)</code> Makes a new directory according to the given <code>path</code>. The given directory path does not exist. If the middle directories in the path do not exist, you should create them as well.</li>
    <li><code>void addContentToFile(String filePath, String content)</code>
        <ul>
            <li>If <code>filePath</code> does not exist, creates that file containing given <code>content</code>.</li>
            <li>If <code>filePath</code> already exists, appends the given <code>content</code> to original content.</li>
        </ul>
    </li>
    <li><code>String readContentFromFile(String filePath)</code> Returns the content in the file by <code>filePath</code>.</li>
</ul>""",
    'examples': [
        {'title': "Example 1", 'input': '["FileSystem", "ls", "mkdir", "addContentToFile", "ls", "readContentFromFile"]\n[[], ["/"], ["/a/b/c"], ["/a/b/c/d", "hello"], ["/"], ["/a/b/c/d"]]', 'output': '[null, [], null, null, ["a"], "hello"]\nExplanation:\nFileSystem fs = new FileSystem();\nfs.ls("/"); // return []\nfs.mkdir("/a/b/c");\nfs.addContentToFile("/a/b/c/d", "hello");\nfs.ls("/"); // return ["a"]\nfs.readContentFromFile("/a/b/c/d"); // return "hello"'}
    ],
    'constraints': [
        "1 <= path.length, filePath.length <= 100",
        "path and filePath are absolute paths, beginning with '/' and do not end with '/' except for the root '/' itself.",
        "1 <= content.length <= 50",
        "All the names in directory and file will only have lowercase letters.",
        "At most 300 calls will be made to ls, mkdir, addContentToFile, and readContentFromFile."
    ],
    'plain_english': "A file system is naturally modeled as a Trie / N-ary Tree. We represent both directories and files using a `FileNode` class containing: `isFile: Boolean`, `content: StringBuilder`, and `children: TreeMap<String, FileNode>`. Using a `TreeMap` automatically maintains children in lexicographical order, making `ls()` effortless. We maintain a root node `/`. For any path, we split by `/` into parts. `mkdir` navigates down from root, creating intermediate `FileNode`s as needed. `addContentToFile` navigates to the file node (creating it if absent, marking `isFile = true`) and appends content. `readContentFromFile` retrieves the content string. `ls` returns `[node.name]` if the target is a file, or `children.keys` if it is a directory.",
    'boundary_checks': {
        'min_base': "Root directory queries <code>ls(\"/\")</code> &rarr; Empty system returns `[]`; after mkdir returns top-level folder names.",
        'best_exit': "Deep intermediate paths <code>mkdir(\"/a/b/c/d\")</code> &rarr; Creates all missing ancestor nodes in a single traversal.",
        'worst_scan': "Sequential string appending to large file &rarr; `StringBuilder` avoids quadratic string allocation overhead."
    },
    'mental_model': {
        'analogy_title': "The Library Filing Cabinet Tree",
        'analogy_text': "Think of a filing cabinet at a municipal library. The root drawer is `/`. Inside are hanging folders (directories) and paper documents (files). Each folder contains dividers for subfolders and individual documents, alphabetically arranged (`TreeMap`). To add notes to `/finance/q4/report.txt`, you pull drawer `/`, open folder `finance`, open subfolder `q4`, take out document `report.txt`, and append your text.",
        'aha_moment': "A File System is simply a Trie where directory and file names are the 'edges', and `TreeMap` provides automatic sorting for free without needing explicit `sort()` calls on every `ls()` query!",
        'trigger': "Design hierarchical path-based system (folders, files, nested paths) &rarr; Trie-based tree structure (`FileNode`) with `TreeMap` children.",
        'sisters': [
            {'name': "Design In-Memory File System", 'lc': "LC #588"},
            {'name': "Design Log Storage System", 'lc': "LC #635"},
            {'name': "Implement Trie (Prefix Tree)", 'lc': "LC #208"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(PathLength + K log K) Time per operation, O(TotalChars) Space",
        'naive_text': "Single flat `HashMap<String, String>` mapping full path strings to file contents, scanning all map keys with prefix matches for `ls`.",
        'wasted_work': "Scanning every path in the entire system on every `ls` call instead of traversing directly to the target node's children.",
        'optimal_complexity': "O(PathLength + ChildrenCount) Time, O(TotalNodes + ContentLength) Space",
        'optimal_text': "Trie-based `FileNode` hierarchy with `TreeMap` children for instantaneous O(Depth) traversal and auto-sorted child keys.",
        'core_invariant': "Every path `/a/b/c` maps to a unique tree path from the root node `/` down to node `c`.",
        'table_rows': [
            {'name': "Flat HashMap + Key Prefix Scan", 'time': "O(TotalKeys * PathLen)", 'space': "O(TotalKeys)", 'note': "Inefficient ls(): must check every key in the entire filesystem."},
            {'name': "★ Trie-Based Node Hierarchy", 'time': "O(PathDepth + ChildrenCount)", 'space': "O(TotalNodes)", 'note': "Navigates directly down the tree; TreeMap provides pre-sorted children."}
        ]
    },
    'trace_table': {
        'input_desc': 'mkdir("/a/b/c"), addContentToFile("/a/b/c/d", "hello"), ls("/")',
        'columns': ["Operation", "Path Parsed", "Nodes Created / Updated", "Target State", "Return Value"],
        'rows': [
            ["FileSystem()", "-", "Root node '/' created", "children = {}", "-"],
            ["ls(\"/\")", "[]", "Traverse to root", "children is empty", "[]"],
            ["mkdir(\"/a/b/c\")", "[\"a\", \"b\", \"c\"]", "Created 'a' &rarr; 'b' &rarr; 'c'", "'c' is directory", "null"],
            ["addContentToFile(\"/a/b/c/d\", \"hello\")", "[\"a\", \"b\", \"c\", \"d\"]", "Created file 'd' under 'c'", "'d'.content = \"hello\"", "null"],
            ["ls(\"/\")", "[]", "Look up root children", "children = {\"a\"}", "[\"a\"]"],
            ["readContentFromFile(\"/a/b/c/d\")", "[\"a\", \"b\", \"c\", \"d\"]", "Traverse to 'd'", "'d'.content = \"hello\"", "\"hello\""]
        ],
        'takeaway': "Tree hierarchy handles intermediate folders and file operations cleanly with direct path lookups."
    },
    'stepper': {
        'title': "File System Trie Hierarchy Stepper",
        'default_input': 'mkdir("/a/b/c") -> addContentToFile("/a/b/c/d", "hello") -> ls("/")',
        'presets': [
            {'label': "Example 1: Deep Nested File", 'onclick': 'loadPreset(1)'},
            {'label': "Example 2: Multiple Files in Root", 'onclick': 'loadPreset(2)'}
        ],
        'metrics': [
            {'id': "metric-fs-op", 'label': "Current Operation", 'color': "text-amber-400", 'initial': "init()"},
            {'id': "metric-fs-path", 'label': "Path Evaluated", 'color': "text-cyan-400", 'initial': "/"},
            {'id': "metric-fs-type", 'label': "Node Type", 'color': "text-purple-400", 'initial': "Directory"},
            {'id': "metric-fs-res", 'label': "Output Result", 'color': "text-emerald-400", 'initial': "None"}
        ],
        'visual_html': '<div id="p93-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[180px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace directory creation and file retrieval.',
        'pseudocode_lines': [
            "class FileNode(val isFile: Boolean = false) {",
            "    val children = TreeMap<String, FileNode>()",
            "    val content = StringBuilder()",
            "}",
            "fun mkdir(path: String): FileNode {",
            "    var curr = root",
            "    for (part in path.split('/').filter { it.isNotEmpty() }) {",
            "        curr = curr.children.computeIfAbsent(part) { FileNode() }",
            "    }",
            "    return curr",
            "}"
        ],
        'js_code': """
    let presetMode = 1;

    function buildSteps(mode) {
      steps = [];

      steps.push({
        op: "FileSystem()", path: "/", type: "Root Directory", res: "null",
        tree: { name: "/", isFile: false, children: {} }, line: 1,
        narration: "Initialized empty FileSystem with root directory '/'."
      });

      steps.push({
        op: "mkdir('/a/b/c')", path: "/a/b/c", type: "Directory", res: "null",
        tree: { name: "/", isFile: false, children: { "a": { name: "a", isFile: false, children: { "b": { name: "b", isFile: false, children: { "c": { name: "c", isFile: false, children: {} } } } } } } }, line: 5,
        narration: "Traversed and created intermediate folders: root &rarr; a &rarr; b &rarr; c."
      });

      steps.push({
        op: "addContentToFile('/a/b/c/d', 'hello')", path: "/a/b/c/d", type: "File", res: "null",
        tree: { name: "/", isFile: false, children: { "a": { name: "a", isFile: false, children: { "b": { name: "b", isFile: false, children: { "c": { name: "c", isFile: false, children: { "d": { name: "d", isFile: true, content: "hello" } } } } } } } } }, line: 8,
        narration: "Created file node 'd' inside directory '/a/b/c' and appended 'hello'."
      });

      steps.push({
        op: "ls('/')", path: "/", type: "Directory", res: "['a']",
        tree: { name: "/", isFile: false, children: { "a": { name: "a", isFile: false, children: { "b": { name: "b", isFile: false, children: { "c": { name: "c", isFile: false, children: { "d": { name: "d", isFile: true, content: "hello" } } } } } } } } }, line: 10,
        narration: "ls('/') inspected root children: returns ['a'] in alphabetical order."
      });

      steps.push({
        op: "readContentFromFile('/a/b/c/d')", path: "/a/b/c/d", type: "File", res: "'hello'",
        tree: { name: "/", isFile: false, children: { "a": { name: "a", isFile: false, children: { "b": { name: "b", isFile: false, children: { "c": { name: "c", isFile: false, children: { "d": { name: "d", isFile: true, content: "hello" } } } } } } } } }, line: 11,
        narration: "readContentFromFile navigated directly to '/a/b/c/d' &rarr; returns 'hello'."
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p93-stage');
      if (!stage) return;
      document.getElementById('metric-fs-op').textContent = step.op;
      document.getElementById('metric-fs-path').textContent = step.path;
      document.getElementById('metric-fs-type').textContent = step.type;
      document.getElementById('metric-fs-res').textContent = step.res;

      function renderNode(node) {
        const isF = node.isFile;
        const color = isF ? 'bg-amber-900/40 border-amber-500 text-amber-200' : 'bg-slate-800 border-slate-700 text-slate-200';
        const icon = isF ? '&#128196;' : '&#128193;';
        let sub = '';
        if (node.children) {
          sub = '<div class="pl-4 border-l border-slate-700 flex flex-col gap-1 mt-1">';
          for (let k of Object.keys(node.children)) {
            sub += renderNode(node.children[k]);
          }
          sub += '</div>';
        }
        return `<div>
                  <div class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded border text-xs font-mono ${color}">
                    <span>${icon}</span> <strong>${node.name}</strong> ${isF ? `<span class="text-[10px] text-slate-400">("${node.content}")</span>` : ''}
                  </div>
                  ${sub}
                </div>`;
      }

      stage.innerHTML = `<div class="w-full flex justify-center py-2">${renderNode(step.tree)}</div>`;
    }

    window.loadPreset = function(m) {
      presetMode = m;
      buildSteps(m);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(1);
"""
    },
    'approaches': [
        {
            'name': "Trie-Based FileNode Tree with TreeMap",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(PathLength + K) Time per operation, O(TotalNodes) Space",
            'summary': "Hierarchical tree where each node represents a directory or file. `TreeMap` ensures all child names are kept sorted lexicographically for instant O(Children) `ls` listing.",
            'kotlin_code': """import java.util.TreeMap

class FileSystem() {
    private class FileNode(var isFile: Boolean = false) {
        val children = TreeMap<String, FileNode>()
        val content = StringBuilder()
    }

    private val root = FileNode(false)

    fun ls(path: String): List<String> {
        val node = traverse(path)
        if (node.isFile) {
            // Return only file name
            val parts = path.split("/").filter { it.isNotEmpty() }
            return listOf(parts.last())
        }
        return node.children.keys.toList()
    }

    fun mkdir(path: String) {
        traverse(path)
    }

    fun addContentToFile(filePath: String, content: String) {
        val node = traverse(filePath)
        node.isFile = true
        node.content.append(content)
    }

    fun readContentFromFile(filePath: String): String {
        val node = traverse(filePath)
        return node.content.toString()
    }

    private fun traverse(path: String): FileNode {
        var curr = root
        val parts = path.split("/").filter { it.isNotEmpty() }
        for (part in parts) {
            curr = curr.children.computeIfAbsent(part) { FileNode(false) }
        }
        return curr
    }
}""",
            'python_code': """class FileNode:
    def __init__(self, is_file=False):
        self.is_file = is_file
        self.children = {}
        self.content = []

class FileSystem:
    def __init__(self):
        self.root = FileNode(False)

    def _traverse(self, path: str) -> FileNode:
        curr = self.root
        parts = [p for p in path.split('/') if p]
        for part in parts:
            if part not in curr.children:
                curr.children[part] = FileNode(False)
            curr = curr.children[part]
        return curr

    def ls(self, path: str) -> List[str]:
        curr = self._traverse(path)
        if curr.is_file:
            return [path.split('/')[-1]]
        return sorted(curr.children.keys())

    def mkdir(self, path: str) -> None:
        self._traverse(path)

    def addContentToFile(self, filePath: str, content: str) -> None:
        node = self._traverse(filePath)
        node.is_file = True
        node.content.append(content)

    def readContentFromFile(self, filePath: str) -> str:
        node = self._traverse(filePath)
        return "".join(node.content)""",
            'java_code': """import java.util.*;

class FileSystem {
    private static class FileNode {
        boolean isFile = false;
        Map<String, FileNode> children = new TreeMap<>();
        StringBuilder content = new StringBuilder();
    }

    private final FileNode root = new FileNode();

    public FileSystem() {}

    public List<String> ls(String path) {
        FileNode node = traverse(path);
        if (node.isFile) {
            String[] parts = path.split("/");
            return Collections.singletonList(parts[parts.length - 1]);
        }
        return new ArrayList<>(node.children.keySet());
    }

    public void mkdir(String path) {
        traverse(path);
    }

    public void addContentToFile(String filePath, String content) {
        FileNode node = traverse(filePath);
        node.isFile = true;
        node.content.append(content);
    }

    public String readContentFromFile(String filePath) {
        FileNode node = traverse(filePath);
        return node.content.toString();
    }

    private FileNode traverse(String path) {
        FileNode curr = root;
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.isEmpty()) continue;
            curr.children.putIfAbsent(part, new FileNode());
            curr = curr.children.get(part);
        }
        return curr;
    }
}""",
            'memory_breakdown': [
                {'component': "Tree Nodes `FileNode`", 'cost': "O(Directories + Files)", 'description': "Each unique file and directory is stored as exactly one node."},
                {'component': "File Content Buffers", 'cost': "O(TotalContentLength)", 'description': "StringBuilder objects holding raw file text."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class FileSystem() {
    fun ls(path: String): List<String> = emptyList()
    fun mkdir(path: String) {}
    fun addContentToFile(filePath: String, content: String) {}
    fun readContentFromFile(filePath: String): String = ""
}""",
        'python_boilerplate': """class FileSystem:
    def __init__(self):
        pass
    def ls(self, path: str) -> List[str]:
        return []
    def mkdir(self, path: str) -> None:
        pass
    def addContentToFile(self, filePath: str, content: str) -> None:
        pass
    def readContentFromFile(self, filePath: str) -> str:
        return \"\"""",
        'java_boilerplate': """class FileSystem {
    public FileSystem() {}
    public List<String> ls(String path) { return new ArrayList<>(); }
    public void mkdir(String path) {}
    public void addContentToFile(String filePath, String content) {}
    public String readContentFromFile(String filePath) { return ""; }
}""",
        'js_test_runner': """
function FileSystem() {
    function Node(isFile) {
        this.isFile = isFile || false;
        this.children = {};
        this.content = "";
    }
    const root = new Node(false);

    function traverse(path) {
        let curr = root;
        const parts = path.split("/").filter(p => p.length > 0);
        for (let p of parts) {
            if (!curr.children[p]) curr.children[p] = new Node(false);
            curr = curr.children[p];
        }
        return curr;
    }

    this.ls = function(path) {
        const node = traverse(path);
        if (node.isFile) {
            const parts = path.split("/").filter(p => p.length > 0);
            return [parts[parts.length - 1]];
        }
        return Object.keys(node.children).sort();
    };

    this.mkdir = function(path) {
        traverse(path);
    };

    this.addContentToFile = function(filePath, content) {
        const node = traverse(filePath);
        node.isFile = true;
        node.content += content;
    };

    this.readContentFromFile = function(filePath) {
        const node = traverse(filePath);
        return node.content;
    };
}

const fs = new FileSystem();
const tests = [];
tests.push({ op: "ls('/')", got: fs.ls("/"), expected: [] });
fs.mkdir("/a/b/c");
tests.push({ op: "mkdir(/a/b/c)", got: "ok", expected: "ok" });
fs.addContentToFile("/a/b/c/d", "hello");
tests.push({ op: "ls('/')", got: fs.ls("/"), expected: ["a"] });
tests.push({ op: "readContentFromFile", got: fs.readContentFromFile("/a/b/c/d"), expected: "hello" });

let passed = 0;
tests.forEach((t, i) => {
    const ok = JSON.stringify(t.got) === JSON.stringify(t.expected);
    if (ok) passed++;
    log(`Test ${i+1}: ${t.op} -> ${ok ? 'PASSED' : 'FAILED'} (Got: ${JSON.stringify(t.got)})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "Let's clarify the system requirements: we need an in-memory file system supporting directory creation with nested paths (`mkdir`), appending text to files (`addContentToFile`), reading files (`readContentFromFile`), and listing files or directory contents in lexicographical order (`ls`).",
        'phase2': "A flat hash map of full paths would make `ls` very inefficient because listing a directory would require scanning every single path in the system. A hierarchical Trie / N-ary Tree is the natural and optimal design.",
        'phase3': "We define a `FileNode` with `isFile: Boolean`, `content: StringBuilder`, and `children: TreeMap<String, FileNode>`. Using `TreeMap` automatically maintains sorted order of children, making `ls` as simple as returning `children.keySet()`.",
        'phase4': "Notice how all operations share a single helper method `traverse(path)`: it splits the path by `/`, walks down from root, and creates intermediate `FileNode`s on the fly. This keeps the implementation exceptionally clean and bug-free.",
        'senior_followup': "In a real operating system or distributed filesystem (like HDFS or ext4), we would need inode metadata (permissions, timestamps, file sizes), locking mechanisms (`ReadWriteLock` on directory nodes) to handle concurrent multi-threaded writes, and block chunking for massive files."
    },
    'quiz': [
        {
            'question': "Why is using a Trie structure superior to a flat `HashMap<String, String>` mapping full paths to file contents?",
            'options': [
                "Because listing a directory in a Trie takes O(Depth + Children) rather than scanning every path in the entire system",
                "Because flat hash maps cannot store string values",
                "Because a Trie uses zero memory",
                "Because flat hash maps do not allow directory renaming"
            ],
            'correct_index': 0,
            'explanation': "With a flat map, an ls() operation must inspect all entries in the entire system to find matching prefix children. A Trie navigates directly to the target directory node in O(Depth) time."
        },
        {
            'question': "Why is `TreeMap` chosen for the children container in `FileNode`?",
            'options': [
                "Because `TreeMap` keeps its keys sorted in lexicographical order, fulfilling the `ls()` requirement without manual sorting",
                "Because `TreeMap` uses less memory than `HashMap`",
                "Because `TreeMap` supports O(1) lookups",
                "Because Java forces all tree nodes to use `TreeMap`"
            ],
            'correct_index': 0,
            'explanation': "The problem specification explicitly requires `ls` to return entries in lexicographical order. `TreeMap` automatically maintains sorted keys via red-black tree ordering."
        }
    ]
}
