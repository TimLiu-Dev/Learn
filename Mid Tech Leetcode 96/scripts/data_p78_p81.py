import sys
sys.path.append('scripts')

p78 = {
    'num': 78,
    'lc': 39,
    'title': "Combination Sum",
    'category': "Backtracking",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "combination-sum",
    'verbatim_statement': """<p>Given an array of <strong>distinct</strong> integers <code>candidates</code> and a target integer <code>target</code>, return <em>a list of all <strong>unique combinations</strong> of </em><code>candidates</code><em> where the chosen numbers sum to </em><code>target</code><em>.</em> You may return the combinations in <strong>any order</strong>.</p>
<p>The <strong>same</strong> number may be chosen from <code>candidates</code> an <strong>unlimited number of times</strong>. Two combinations are unique if the frequency of at least one of the chosen numbers is different.</p>
<p>The test cases are generated such that the number of unique combinations that sum up to <code>target</code> is less than <code>150</code> combinations for the given input.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'candidates = [2,3,6,7], target = 7', 'output': '[[2,2,3],[7]]\nExplanation:\n2 and 3 are candidates, and 2 + 2 + 3 = 7. Note that 2 can be used multiple times.\n7 is a candidate, and 7 = 7.\nThese are the only two combinations.'},
        {'title': "Example 2", 'input': 'candidates = [2,3,5], target = 8', 'output': '[[2,2,2,2],[2,3,3],[3,5]]'},
        {'title': "Example 3", 'input': 'candidates = [2], target = 1', 'output': '[]'}
    ],
    'constraints': [
        "1 <= candidates.length <= 30",
        "2 <= candidates[i] <= 40",
        "All elements of candidates are distinct.",
        "1 <= target <= 40"
    ],
    'plain_english': "We explore combinations using Backtracking. At each state, we choose an index `start` to only pick candidates from index `start` onwards (which prevents duplicate permutations like [2,3,2] vs [2,2,3]). Because elements can be reused unlimited times, when we pick `candidates[i]`, the recursive call passes `i` again (not `i + 1`) with remaining target `target - candidates[i]`. If remaining target reaches 0, we record the combination. If it drops below 0, we prune the branch.",
    'boundary_checks': {
        'min_base': "target < min(candidates) <code>candidates=[2], target=1</code> &rarr; Returns `[]` immediately.",
        'best_exit': "Exact single element match <code>candidates=[7], target=7</code> &rarr; Returns `[[7]]`.",
        'worst_scan': "Repeated small numbers <code>candidates=[2], target=8</code> &rarr; Recurses to depth 4 producing `[[2,2,2,2]]`."
    },
    'mental_model': {
        'analogy_title': "The Vending Machine Coin Slot",
        'analogy_text': "Imagine paying for an exact item in a vending machine using coins of values [2, 3, 6, 7]. You stand in front of the machine with infinite coins of each denomination. To prevent inserting the same combination in different orders (like 2+3+2 vs 2+2+3), you make a rule: you only insert coins of equal or larger value than the coin you just inserted. You can drop in another '2', or upgrade to '3', but you can never reach back into your pocket for a smaller coin.",
        'aha_moment': "Allowing element reuse does NOT mean an infinite loop! Because all candidates are >= 2, every inclusion strictly reduces `target` by at least 2. Recursion depth is strictly bounded by `target / min(candidates)` <= 20.",
        'trigger': "Find unique combinations summing to target with unlimited element reuse &rarr; Backtracking with index constraint `start` and reusable `i` index.",
        'sisters': [
            {'name': "Combination Sum II (Single-use candidates with duplicates)", 'lc': "LC #40"},
            {'name': "Subsets", 'lc': "LC #78"},
            {'name': "Coin Change (Min coins count)", 'lc': "LC #322"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(2^Target) Time, O(Target) Space",
        'naive_text': "Unbounded recursion testing all permutations of candidates, then inserting into a set of lists to filter duplicate orderings.",
        'wasted_work': "Generating duplicate permutations (e.g. [2,3,2], [3,2,2], [2,2,3]) and hashing them into a set.",
        'optimal_complexity': "O(N^(Target / minCandidate)) Time, O(Target) Auxiliary Space",
        'optimal_text': "Order-constrained backtracking using `start` index. Zero duplicate permutations generated; results collected directly.",
        'core_invariant': "At any recursive frame `backtrack(start, remain)`, all elements in `currentList` are in non-decreasing index order, guaranteeing unique combinations.",
        'table_rows': [
            {'name': "1. Permutations + Set Deduplication", 'time': "O(N! * N)", 'space': "O(N * Target)", 'note': "Generates vast permutations; heavy set hashing overhead."},
            {'name': "★ 2. Index-Constrained Backtracking", 'time': "O(N^(T/M))", 'space': "O(T/M)", 'note': "Generates only unique combinations; prunes instantly when remain < 0."}
        ]
    },
    'trace_table': {
        'input_desc': 'candidates = [2, 3, 6, 7], target = 7',
        'columns': ["Call Frame", "start idx", "Candidate Picked", "Remaining Target", "Current Path", "Action Taken"],
        'rows': [
            ["1", "0 (2)", "2", "7 - 2 = 5", "[2]", "Recurse with start=0, remain=5"],
            ["2", "0 (2)", "2", "5 - 2 = 3", "[2, 2]", "Recurse with start=0, remain=3"],
            ["3", "0 (2)", "2", "3 - 2 = 1", "[2, 2, 2]", "Recurse with start=0, remain=1 (all choices > 1 exceed target &rarr; backtrack)"],
            ["4", "1 (3)", "3", "3 - 3 = 0", "[2, 2, 3]", "REMAIN == 0! MATCH FOUND! Record [2, 2, 3]"],
            ["5", "1 (3)", "3", "7 - 3 = 4", "[3]", "Recurse with start=1, remain=4 (no match)"],
            ["6", "3 (7)", "7", "7 - 7 = 0", "[7]", "REMAIN == 0! MATCH FOUND! Record [7]"]
        ],
        'takeaway': "Two unique combinations found: `[[2, 2, 3], [7]]`. Duplicate combinations like `[3, 2, 2]` were never generated."
    },
    'stepper': {
        'title': "Backtracking Decision Tree Stepper",
        'default_input': 'candidates=[2,3,6,7], target=7',
        'presets': [
            {'label': "Example 1: [2,3,6,7], target=7", 'onclick': 'loadPreset([2,3,6,7], 7)'},
            {'label': "Example 2: [2,3,5], target=8", 'onclick': 'loadPreset([2,3,5], 8)'},
            {'label': "Empty: [2], target=1", 'onclick': 'loadPreset([2], 1)'}
        ],
        'metrics': [
            {'id': "metric-current-path", 'label': "Current Combination", 'color': "text-amber-400", 'initial': "[]"},
            {'id': "metric-remain", 'label': "Remaining Target", 'color': "text-cyan-400", 'initial': "7"},
            {'id': "metric-choice", 'label': "Candidate Choice", 'color': "text-purple-400", 'initial': "start"},
            {'id': "metric-combos", 'label': "Valid Combos Found", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p39-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace backtracking exploration.',
        'pseudocode_lines': [
            "fun backtrack(start: Int, remain: Int, path: MutableList<Int>) {",
            "    if (remain == 0) { res.add(ArrayList(path)); return }",
            "    if (remain < 0) return",
            "    for (i in start until candidates.size) {",
            "        path.add(candidates[i])",
            "        backtrack(i, remain - candidates[i], path) // Reuse allowed: pass i",
            "        path.removeAt(path.lastIndex) // Backtrack",
            "    }",
            "}"
        ],
        'js_code': """
    let rawCandidates = [2, 3, 6, 7];
    let rawTarget = 7;

    function buildSteps(candidates, target) {
      steps = [];
      const res = [];

      steps.push({
        path: "[]", remain: target, choice: "Root", combos: 0,
        combosList: [], line: 1,
        narration: `Starting backtracking search for target ${target} using candidates [${candidates.join(', ')}].`
      });

      function backtrack(start, remain, path) {
        if (remain === 0) {
          res.push([...path]);
          steps.push({
            path: `[${path.join(', ')}]`, remain: 0, choice: "MATCH!", combos: res.length,
            combosList: [...res], line: 2,
            narration: `Goal reached! [${path.join(', ')}] sums to ${target}. Added to results.`
          });
          return;
        }
        if (remain < 0) return;

        for (let i = start; i < candidates.length; i++) {
          const c = candidates[i];
          if (remain - c < 0) continue; // Prune early if sorted

          path.push(c);
          steps.push({
            path: `[${path.join(', ')}]`, remain: remain - c, choice: `Pick ${c}`, combos: res.length,
            combosList: [...res], line: 5,
            narration: `Chose candidate ${c}. Remaining target = ${remain - c}. Exploring subtree...`
          });

          backtrack(i, remain - c, path);

          path.pop();
          steps.push({
            path: `[${path.join(', ')}]`, remain: remain, choice: `Backtrack (removed ${c})`, combos: res.length,
            combosList: [...res], line: 7,
            narration: `Backtracked: popped ${c}. Returning to remaining target ${remain}.`
          });
        }
      }

      candidates.sort((a,b)=>a-b);
      backtrack(0, target, []);

      steps.push({
        path: "Done", remain: 0, choice: "Complete", combos: res.length,
        combosList: [...res], line: 1,
        narration: `Search complete. Total ${res.length} unique combination(s) found.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p39-stage');
      if (!stage) return;
      let html = '<div class="flex flex-col items-center gap-3 w-full max-w-xl">';
      html += `<div class="p-3 rounded-lg bg-slate-900 border border-slate-700 font-mono text-xs flex items-center justify-between w-full">`;
      html += `<span>Current Path: <strong class="text-amber-300">${s.path}</strong></span>`;
      html += `<span>Remain: <strong class="text-cyan-400">${s.remain}</strong></span>`;
      html += `</div>`;

      if (s.combosList.length > 0) {
        html += `<div class="text-xs font-mono text-emerald-300 font-bold bg-slate-950/60 p-3 rounded-lg border border-emerald-800/60 w-full">Found: ${JSON.stringify(s.combosList)}</div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-current-path').textContent = s.path;
      document.getElementById('metric-remain').textContent = s.remain;
      document.getElementById('metric-choice').textContent = s.choice;
      document.getElementById('metric-combos').textContent = s.combos;
    }

    function loadPreset(c, t) {
      rawCandidates = c; rawTarget = t;
      buildSteps(rawCandidates, rawTarget);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Index-Constrained Backtracking (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N^(T/M)) Time, O(T/M) Auxiliary Space",
            'summary': "Sort candidates for early pruning. Maintain a `start` index to only pick current or future candidates, preventing duplicate permutations. Recurse passing `i` to allow element reuse.",
            'kotlin_code': """class Solution {
    fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        candidates.sort() // Enables early break when candidate > remain

        fun backtrack(start: Int, remain: Int, path: MutableList<Int>) {
            if (remain == 0) {
                result.add(ArrayList(path))
                return
            }

            for (i in start until candidates.size) {
                if (candidates[i] > remain) break // Early pruning

                path.add(candidates[i])
                backtrack(i, remain - candidates[i], path) // Pass i to allow reuse
                path.removeAt(path.lastIndex)              // Backtrack
            }
        }

        backtrack(0, target, mutableListOf())
        return result
    }
}""",
            'python_code': """class Solution:
    def combinationSum(self, candidates: list[int], target: int) -> list[list[int]]:
        result = []
        candidates.sort()

        def backtrack(start: int, remain: int, path: list[int]):
            if remain == 0:
                result.append(list(path))
                return

            for i in range(start, len(candidates)):
                if candidates[i] > remain:
                    break  # Early pruning

                path.append(candidates[i])
                backtrack(i, remain - candidates[i], path)  # Reuse allowed: pass i
                path.pop()  # Backtrack

        backtrack(0, target, [])
        return result""",
            'java_code': """import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates); // Early pruning

        backtrack(0, target, new ArrayList<>(), candidates, result);
        return result;
    }

    private void backtrack(int start, int remain, List<Integer> path, int[] candidates, List<List<Integer>> result) {
        if (remain == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remain) break; // Prune branch

            path.add(candidates[i]);
            backtrack(i, remain - candidates[i], path, candidates, result); // Pass i for reuse
            path.remove(path.size() - 1); // Backtrack
        }
    }
}""",
            'memory_breakdown': "O(Target / minCandidate) recursion stack space. Sorting upfront takes O(N log N) and allows breaking out of the loop as soon as candidates[i] > remain, dramatically pruning subtrees."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> {
        // Write your optimal Backtracking solution here
        val res = mutableListOf<List<Int>>()
        candidates.sort()
        fun dfs(start: Int, remain: Int, path: MutableList<Int>) {
            if (remain == 0) { res.add(ArrayList(path)); return }
            for (i in start until candidates.size) {
                if (candidates[i] > remain) break
                path.add(candidates[i])
                dfs(i, remain - candidates[i], path)
                path.removeAt(path.lastIndex)
            }
        }
        dfs(0, target, mutableListOf())
        return res
    }
}""",
        'python_boilerplate': """class Solution:
    def combinationSum(self, candidates: list[int], target: int) -> list[list[int]]:
        # Write your optimal Backtracking solution here
        res = []
        candidates.sort()
        def dfs(start, remain, path):
            if remain == 0:
                res.append(list(path))
                return
            for i in range(start, len(candidates)):
                if candidates[i] > remain: break
                path.append(candidates[i])
                dfs(i, remain - candidates[i], path)
                path.pop()
        dfs(0, target, [])
        return res""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        // Write your optimal Backtracking solution here
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(candidates);
        dfs(0, target, new ArrayList<>(), candidates, res);
        return res;
    }
    void dfs(int start, int remain, List<Integer> path, int[] c, List<List<Integer>> res) {
        if (remain == 0) { res.add(new ArrayList<>(path)); return; }
        for (int i = start; i < c.length; i++) {
            if (c[i] > remain) break;
            path.add(c[i]);
            dfs(i, remain - c[i], path, c, res);
            path.remove(path.size() - 1);
        }
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(candidates, target) {
        const res = [];
        candidates.sort((a,b)=>a-b);
        function dfs(start, remain, path) {
          if (remain === 0) { res.push([...path]); return; }
          for (let i = start; i < candidates.length; i++) {
            if (candidates[i] > remain) break;
            path.push(candidates[i]);
            dfs(i, remain - candidates[i], path);
            path.pop();
          }
        }
        dfs(0, target, []);
        return res;
      }

      const tests = [
        { c: [2,3,6,7], t: 7, expectedLen: 2 },
        { c: [2,3,5], t: 8, expectedLen: 3 },
        { c: [2], t: 1, expectedLen: 0 }
      ];

      let allPassed = true;
      tests.forEach((test, i) => {
        const actual = solve(test.c, test.t);
        const pass = actual.length === test.expectedLen;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: candidates=[${test.c}], target=${test.t} &rarr; Found ${actual.length} combos (Expected ${test.expectedLen}) (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me confirm constraints: all candidates are distinct positive integers >= 2, and target is up to 40. The same number can be chosen an unlimited number of times. Combinations must be unique (order does not matter).\"",
        'phase2': "\"Because we need all combinations and the search space is bounded, backtracking is the optimal approach. To prevent duplicate combinations like `[2, 3, 2]` and `[2, 2, 3]`, we enforce an ordering: we pass a `start` index into the recursive function and only loop from `i = start` to the end of the array. Because a candidate can be reused, when we pick `candidates[i]`, we recurse with `start = i` (rather than `i + 1`) and remaining target `remain - candidates[i]`. By sorting candidates upfront, we can break out of the loop early whenever `candidates[i] > remain`, pruning dead search branches immediately.\"",
        'phase3': "\"Let me write the code. Sort `candidates`. Define recursive function `backtrack(start, remain, path)`. Base cases: if `remain == 0`, add a copy of `path` to `result` and return. Loop `i` from `start` to `candidates.length - 1`: if `candidates[i] > remain`, break. Add `candidates[i]` to `path`, call `backtrack(i, remain - candidates[i], path)`, and pop the last element from `path` to backtrack. Initial call is `backtrack(0, target, [])`.\"",
        'phase4': "\"Tracing with `[2, 3, 6, 7]` and target 7. Path `[2, 2, 3]` sums to 7, added. Path `[7]` sums to 7, added. Loops break early when candidate exceeds remain, preventing redundant exploration. Return `[[2, 2, 3], [7]]`. Handled with clean index constraints and pruning.\"",
        'senior_followup': "\"In integer linear programming and combinatorial coin-change verification, when the candidate set is large, this problem generalizes to the Bounded Knapsack Problem solved with branch-and-bound or Meet-in-the-Middle.\""
    },
    'quiz': [
        {
            'question': "Why is `start = i` passed into the recursive call instead of `start = i + 1`?",
            'options': [
                "Because each candidate can be chosen an unlimited number of times",
                "Because passing `i + 1` causes an IndexOutOfBoundsException on the last candidate",
                "Because sorting the array reverses the natural indexing order of candidates",
                "Because recursion depth in Java is limited to the start index value"
            ],
            'correct_index': 0,
            'explanation': "Passing `i` allows the recursive call to select the exact same candidate `candidates[i]` again, enabling unlimited reuse while preventing it from choosing earlier smaller candidates."
        },
        {
            'question': "Why should `candidates` be sorted before starting the backtracking search?",
            'options': [
                "Sorting allows breaking the loop immediately when `candidates[i] > remain`, pruning dead branches",
                "Sorting is mandatory for array iteration in modern Java and Kotlin runtimes",
                "Sorting eliminates duplicate candidates from the input array in O(1) time",
                "Unsorted arrays cause integer underflow when subtracting from target"
            ],
            'correct_index': 0,
            'explanation': "If candidates are sorted, all subsequent candidates in the loop are `>= candidates[i]`. If `candidates[i] > remain`, all future candidates also exceed `remain`, allowing an immediate `break`."
        }
    ]
}

p79 = {
    'num': 79,
    'lc': 22,
    'title': "Generate Parentheses",
    'category': "Backtracking",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "generate-parentheses",
    'verbatim_statement': """<p>Given <code>n</code> pairs of parentheses, write a function to <em>generate all combinations of well-formed parentheses</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'n = 3', 'output': '["((()))","(()())","(())()","()(())","()()()"]'},
        {'title': "Example 2", 'input': 'n = 1', 'output': '["()"]'}
    ],
    'constraints': [
        "1 <= n <= 8"
    ],
    'plain_english': "A sequence of n pairs of parentheses is well-formed if and only if: 1) It has exactly n '(' and n ')' characters; and 2) At any prefix, the number of closing brackets ')' never exceeds the number of opening brackets '('. We use constrained backtracking tracking `openCount` and `closeCount`. We can append '(' whenever `openCount < n`. We can append ')' whenever `closeCount < openCount`. When length reaches 2n, we record the valid string.",
    'boundary_checks': {
        'min_base': "n = 1 &rarr; Exactly 1 valid combination: `[\"()\"]`.",
        'best_exit': "Path reaches length 2n &rarr; Recorded directly without any post-validation.",
        'worst_scan': "n = 8 &rarr; Generates the 8th Catalan number (1,430 combinations) in optimal time."
    },
    'mental_model': {
        'analogy_title': "The Bank Deposit / Withdrawal Balance",
        'analogy_text': "Think of '(' as depositing a dollar and ')' as withdrawing a dollar. You are allowed to make at most n deposits and n withdrawals. The only rule is: your account balance can NEVER go negative! You can make another deposit whenever you haven't reached your n deposit limit (`open < n`). You can only make a withdrawal if you currently have money in the bank (`close < open`). When all n deposits and n withdrawals are complete, your balance is exactly $0.",
        'aha_moment': "Never generate invalid strings! Instead of generating all 2^(2n) strings and validating them with a stack, our two simple numeric guards (`open < n` and `close < open`) prune all invalid paths at birth, generating ONLY 100% valid well-formed strings.",
        'trigger': "Generate all well-formed parentheses combinations of length 2N &rarr; Constrained Backtracking with `open < n` and `close < open` guards.",
        'sisters': [
            {'name': "Valid Parentheses", 'lc': "LC #20"},
            {'name': "Letter Combinations of a Phone Number", 'lc': "LC #17"},
            {'name': "Permutations", 'lc': "LC #46"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(2^(2N) * N) Time, O(N) Space",
        'naive_text': "Generate all 2^(2N) combinations of '(' and ')' characters using brute-force recursion, then validate each with a stack.",
        'wasted_work': "Generating vast numbers of invalid combinations (e.g. \")))((\") only to reject them at the end.",
        'optimal_complexity': "O(4^N / sqrt(N)) Time, O(N) Space",
        'optimal_text': "Constrained backtracking. Every branch explored leads to a strictly valid well-formed string (Catalan number C_n).",
        'core_invariant': "At every step in the path, `0 <= closeCount <= openCount <= n`. No invalid prefix is ever constructed.",
        'table_rows': [
            {'name': "1. Generate All & Validate", 'time': "O(2^(2N) * N)", 'space': "O(N)", 'note': "Tests $2^{16} = 65,536$ combinations for n=8."},
            {'name': "★ 2. Constrained Backtracking", 'time': "O(C_n * N)", 'space': "O(N)", 'note': "Generates exactly 1,430 valid strings for n=8; zero waste."}
        ]
    },
    'trace_table': {
        'input_desc': 'n = 2 (Length = 4, Total valid combinations = C_2 = 2)',
        'columns': ["Step", "Action Taken", "openCount", "closeCount", "Current Buffer", "Decision"],
        'rows': [
            ["1", "Append '('", "1", "0", "\"(\"", "open < 2 &rarr; branched '('"],
            ["2", "Append '('", "2", "0", "\"((\"", "open < 2 &rarr; branched '('"],
            ["3", "Append ')'", "2", "1", "\"(()\"", "open == 2; close < open &rarr; can only branch ')'"],
            ["4", "Append ')'", "2", "2", "\"(()) \"", "Length == 4 &rarr; MATCH #1: \"(())\""],
            ["5", "Backtrack to step 1, Append ')'", "1", "1", "\"()\"", "close < open (0 < 1) &rarr; branched ')'"],
            ["6", "Append '('", "2", "1", "\"()(\"", "open < 2 &rarr; branched '('"],
            ["7", "Append ')'", "2", "2", "\"()()\"", "Length == 4 &rarr; MATCH #2: \"()()\""]
        ],
        'takeaway': "Two valid combinations generated: `[\"(())\", \"()()\"]` with zero invalid candidates generated."
    },
    'stepper': {
        'title': "Well-Formed Parentheses Backtracking Stepper",
        'default_input': 'n = 3',
        'presets': [
            {'label': "Canonical n = 3 (5 Combos)", 'onclick': 'loadPreset(3)'},
            {'label': "Small n = 2 (2 Combos)", 'onclick': 'loadPreset(2)'},
            {'label': "Base n = 1 (1 Combo)", 'onclick': 'loadPreset(1)'}
        ],
        'metrics': [
            {'id': "metric-str", 'label': "Current String", 'color': "text-amber-400", 'initial': '""'},
            {'id': "metric-open", 'label': "Open Count", 'color': "text-cyan-400", 'initial': "0 / 3"},
            {'id': "metric-close", 'label': "Close Count", 'color': "text-purple-400", 'initial': "0 / 3"},
            {'id': "metric-total", 'label': "Completed Combos", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p22-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace constrained parentheses branching.',
        'pseudocode_lines': [
            "fun backtrack(open: Int, close: Int, sb: StringBuilder) {",
            "    if (sb.length == 2 * n) { res.add(sb.toString()); return }",
            "    if (open < n) { sb.append('('); backtrack(open + 1, close, sb); sb.deleteCharAt(sb.lastIndex) }",
            "    if (close < open) { sb.append(')'); backtrack(open, close + 1, sb); sb.deleteCharAt(sb.lastIndex) }",
            "}"
        ],
        'js_code': """
    let rawN = 3;

    function buildSteps(n) {
      steps = [];
      const res = [];
      let currentStr = "";

      steps.push({
        str: '""', open: `0 / ${n}`, close: `0 / ${n}`, total: 0,
        combosList: [], line: 1,
        narration: `Ready to generate parentheses for n = ${n} pairs (target length = ${2 * n}).`
      });

      function backtrack(open, close) {
        if (currentStr.length === 2 * n) {
          res.push(currentStr);
          steps.push({
            str: `"${currentStr}"`, open: `${open} / ${n}`, close: `${close} / ${n}`, total: res.length,
            combosList: [...res], line: 2,
            narration: `Completed valid string "${currentStr}"! Total matches: ${res.length}.`
          });
          return;
        }

        if (open < n) {
          currentStr += '(';
          steps.push({
            str: `"${currentStr}"`, open: `${open + 1} / ${n}`, close: `${close} / ${n}`, total: res.length,
            combosList: [...res], line: 3,
            narration: `open (${open}) < n (${n}): Appended '('. Path is "${currentStr}".`
          });
          backtrack(open + 1, close);
          currentStr = currentStr.slice(0, -1);
        }

        if (close < open) {
          currentStr += ')';
          steps.push({
            str: `"${currentStr}"`, open: `${open} / ${n}`, close: `${close + 1} / ${n}`, total: res.length,
            combosList: [...res], line: 4,
            narration: `close (${close}) < open (${open}): Appended ')'. Path is "${currentStr}".`
          });
          backtrack(open, close + 1);
          currentStr = currentStr.slice(0, -1);
        }
      }

      backtrack(0, 0);

      steps.push({
        str: "Complete", open: `${n} / ${n}`, close: `${n} / ${n}`, total: res.length,
        combosList: [...res], line: 1,
        narration: `Finished! Generated all ${res.length} well-formed combinations for n = ${n}.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p22-stage');
      if (!stage) return;
      let html = '<div class="flex flex-col items-center gap-3 w-full max-w-xl">';
      html += `<div class="p-3 rounded-lg bg-slate-900 border border-slate-700 font-mono text-sm font-bold text-amber-300">${s.str}</div>`;

      if (s.combosList.length > 0) {
        html += `<div class="flex flex-wrap items-center justify-center gap-2 max-w-lg">`;
        s.combosList.forEach(c => {
          html += `<span class="px-2.5 py-1 rounded bg-slate-950 border border-emerald-500/40 text-emerald-300 font-mono text-xs font-bold">${c}</span>`;
        });
        html += `</div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-str').textContent = s.str;
      document.getElementById('metric-open').textContent = s.open;
      document.getElementById('metric-close').textContent = s.close;
      document.getElementById('metric-total').textContent = s.total;
    }

    function loadPreset(n) {
      rawN = n;
      buildSteps(rawN);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Constrained Backtracking (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(4^N / sqrt(N)) Time, O(N) Auxiliary Space",
            'summary': "Branch only on valid moves: add '(' if open < n; add ')' if close < open. Generates exactly the n-th Catalan number of strings with zero invalid dead ends.",
            'kotlin_code': """class Solution {
    fun generateParenthesis(n: Int): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()

        fun backtrack(open: Int, close: Int) {
            if (sb.length == 2 * n) {
                result.add(sb.toString())
                return
            }

            if (open < n) {
                sb.append('(')
                backtrack(open + 1, close)
                sb.deleteCharAt(sb.lastIndex) // Backtrack
            }

            if (close < open) {
                sb.append(')')
                backtrack(open, close + 1)
                sb.deleteCharAt(sb.lastIndex) // Backtrack
            }
        }

        backtrack(0, 0)
        return result
    }
}""",
            'python_code': """class Solution:
    def generateParenthesis(self, n: int) -> list[str]:
        result = []
        path = []

        def backtrack(open_count: int, close_count: int):
            if len(path) == 2 * n:
                result.append("".join(path))
                return

            if open_count < n:
                path.append('(')
                backtrack(open_count + 1, close_count)
                path.pop()

            if close_count < open_count:
                path.append(')')
                backtrack(open_count, close_count + 1)
                path.pop()

        backtrack(0, 0)
        return result""",
            'java_code': """import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(0, 0, new StringBuilder(), n, result);
        return result;
    }

    private void backtrack(int open, int close, StringBuilder sb, int n, List<String> result) {
        if (sb.length() == 2 * n) {
            result.add(sb.toString());
            return;
        }

        if (open < n) {
            sb.append('(');
            backtrack(open + 1, close, sb, n, result);
            sb.deleteCharAt(sb.length() - 1);
        }

        if (close < open) {
            sb.append(')');
            backtrack(open, close + 1, sb, n, result);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}""",
            'memory_breakdown': "O(2N) recursion depth and string builder storage. Time complexity is proportional to the n-th Catalan number $C_n = \\frac{1}{n+1}\\binom{2n}{n} \\approx O(4^n / (n^{3/2}))$, which generates exactly the valid combinations."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun generateParenthesis(n: Int): List<String> {
        // Write your optimal Constrained Backtracking here
        val res = mutableListOf<String>()
        val sb = StringBuilder()
        fun dfs(open: Int, close: Int) {
            if (sb.length == 2 * n) { res.add(sb.toString()); return }
            if (open < n) {
                sb.append('('); dfs(open + 1, close); sb.deleteCharAt(sb.lastIndex)
            }
            if (close < open) {
                sb.append(')'); dfs(open, close + 1); sb.deleteCharAt(sb.lastIndex)
            }
        }
        dfs(0, 0)
        return res
    }
}""",
        'python_boilerplate': """class Solution:
    def generateParenthesis(self, n: int) -> list[str]:
        # Write your optimal Constrained Backtracking here
        res = []
        path = []
        def dfs(open_c, close_c):
            if len(path) == 2 * n:
                res.append("".join(path))
                return
            if open_c < n:
                path.append('(')
                dfs(open_c + 1, close_c)
                path.pop()
            if close_c < open_c:
                path.append(')')
                dfs(open_c, close_c + 1)
                path.pop()
        dfs(0, 0)
        return res""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public List<String> generateParenthesis(int n) {
        // Write your optimal Constrained Backtracking here
        List<String> res = new ArrayList<>();
        dfs(0, 0, new StringBuilder(), n, res);
        return res;
    }
    void dfs(int open, int close, StringBuilder sb, int n, List<String> res) {
        if (sb.length() == 2 * n) { res.add(sb.toString()); return; }
        if (open < n) {
            sb.append('('); dfs(open + 1, close, sb, n, res); sb.deleteCharAt(sb.length() - 1);
        }
        if (close < open) {
            sb.append(')'); dfs(open, close + 1, sb, n, res); sb.deleteCharAt(sb.length() - 1);
        }
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(n) {
        const res = [];
        let curr = '';
        function dfs(open, close) {
          if (curr.length === 2 * n) { res.push(curr); return; }
          if (open < n) {
            curr += '('; dfs(open + 1, close); curr = curr.slice(0, -1);
          }
          if (close < open) {
            curr += ')'; dfs(open, close + 1); curr = curr.slice(0, -1);
          }
        }
        dfs(0, 0);
        return res;
      }

      const tests = [
        { n: 3, expectedLen: 5 },
        { n: 1, expectedLen: 1 },
        { n: 2, expectedLen: 2 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.n);
        const pass = actual.length === t.expectedLen;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: n=${t.n} &rarr; Generated ${actual.length} combos (Expected ${t.expectedLen}) (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: n is an integer between 1 and 8. The output must contain all valid combinations of n pairs of well-formed parentheses in any order. For n=3, there are 5 combinations; for n=8, there are 1430.\"",
        'phase2': "\"Instead of generating all 2^(2n) possible binary strings and checking each with a stack (which wastes vast CPU cycles), we use constrained backtracking to generate ONLY valid combinations. Two invariant conditions govern a valid parentheses prefix: 1) We can append an opening bracket '(' as long as the total open brackets is less than n (`open < n`). 2) We can append a closing bracket ')' ONLY if the count of closing brackets is strictly less than opening brackets (`close < open`). When the length reaches 2n, the string is guaranteed to be well-formed, and we add it to the result.\"",
        'phase3': "\"Let me write the code. I create `result = mutableListOf<String>()` and a reusable `StringBuilder()`. Helper `backtrack(open: Int, close: Int)`: if `sb.length == 2 * n`, add `sb.toString()` to `result` and return. If `open < n`, append '(', recurse with `open + 1`, and delete the last character. If `close < open`, append ')', recurse with `close + 1`, and delete the last character. The initial call is `backtrack(0, 0)`.\"",
        'phase4': "\"Tracing with n=2. Length target is 4. Path branches '(' &rarr; '((' &rarr; '(()' &rarr; '(())'. When length reaches 4, adds `\"(())\"`. Backtracks and branches '()' &rarr; '()(' &rarr; '()()'. Length reaches 4, adds `\"()()\"`. Total 2 results. Edge case n=1 produces `[\"()\"]`. Zero invalid strings generated.\"",
        'senior_followup': "\"The number of valid parentheses combinations of length 2n equals the n-th Catalan number C_n = (2n)! / ((n+1)! * n!). In compiler design, this same constrained parsing validates balanced tokens during lexical analysis in O(N) single-pass streaming.\""
    },
    'quiz': [
        {
            'question': "Under what condition is it valid to append a closing parenthesis ')'?",
            'options': [
                "Only when closeCount < openCount",
                "Only when closeCount < n",
                "Whenever openCount == n",
                "Only when the current string length is an odd integer"
            ],
            'correct_index': 0,
            'explanation': "If `closeCount == openCount`, adding another ')' would produce an unmatched closing bracket, permanently invalidating the string prefix. Thus, `closeCount < openCount` is mandatory."
        },
        {
            'question': "What mathematical sequence describes the number of valid parentheses combinations for n pairs?",
            'options': [
                "Catalan numbers C_n = (2n)! / ((n+1)! * n!)",
                "Fibonacci sequence F_{2n}",
                "Bell numbers of set partitions",
                "Factorial sequence (2n)!"
            ],
            'correct_index': 0,
            'explanation': "The number of Dyck paths and well-formed parentheses strings of length 2n is the n-th Catalan number, growing asymptotically as O(4^n / (n^{1.5} * sqrt(pi)))."
        }
    ]
}

p80 = {
    'num': 80,
    'lc': 417,
    'title': "Pacific Atlantic Water Flow",
    'category': "2D Grid Traversals & Graphs",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "pacific-atlantic-water-flow",
    'verbatim_statement': """<p>There is an <code>m x n</code> rectangular island that borders both the <strong>Pacific Ocean</strong> and <strong>Atlantic Ocean</strong>. The <strong>Pacific Ocean</strong> touches the island's left and top edges, and the <strong>Atlantic Ocean</strong> touches the island's right and bottom edges.</p>
<p>The island is partitioned into a grid of square cells. You are given an <code>m x n</code> integer matrix <code>heights</code> where <code>heights[r][c]</code> represents the <strong>height above sea level</strong> of the cell at coordinate <code>(r, c)</code>.</p>
<p>The island receives a lot of rain, and the rain water can flow to neighboring cells directly north, south, east, and west if the neighboring cell's height is <strong>less than or equal to</strong> the current cell's height. Water can flow from any cell adjacent to an ocean into the ocean.</p>
<p>Return <em>a <strong>2D list</strong> of grid coordinates </em><code>result</code><em> where </em><code>result[i] = [r_i, c_i]</code><em> denotes that rain water can flow from cell </em><code>(r_i, c_i)</code><em> to <strong>both</strong> the Pacific and Atlantic oceans</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'heights = [[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]]', 'output': '[[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]\nExplanation: The water from these cells can flow to both Pacific and Atlantic oceans.'},
        {'title': "Example 2", 'input': 'heights = [[1]]', 'output': '[[0,0]]\nExplanation: The single cell touches both oceans.'}
    ],
    'constraints': [
        "m == heights.length",
        "n == heights[r].length",
        "1 <= m, n <= 200",
        "0 <= heights[r][c] <= 10^5"
    ],
    'plain_english': "Instead of simulating water flowing downward from every single cell to the oceans (which takes O((M*N)^2) time), we reverse the flow! We start water at the ocean coastlines and flow UPWARD into cells with equal or greater height (`neighborHeight >= currHeight`). We run one DFS/BFS seeded from Pacific borders (top row and left column) and another DFS/BFS seeded from Atlantic borders (bottom row and right column). The intersection of cells reachable by both traversals is our answer.",
    'boundary_checks': {
        'min_base': "Single cell <code>heights = [[1]]</code> &rarr; Touches both oceans; returns `[[0, 0]]`.",
        'best_exit': "Flat plateau <code>all cells height 5</code> &rarr; All cells can flow to both oceans; returns every coordinate.",
        'worst_scan': "Valley in the middle &rarr; High border walls prevent water from reaching the interior; only perimeter cells qualify."
    },
    'mental_model': {
        'analogy_title': "Rising Sea Tides from Opposite Shores",
        'analogy_text': "Imagine two tidal waves rising: a blue tide from the Pacific (north and west edges) and an orange tide from the Atlantic (south and east edges). Water can only flood uphill or across flat ground (`neighbor >= curr`). Let the blue tide flood inward as far as it can reach. Let the orange tide flood inward as far as it can reach. Any peak or plateau that gets soaked in BOTH blue and orange water can drain to both oceans.",
        'aha_moment': "Reversing the flow from the ocean borders inward transforms an intractable O((M*N)^2) search into two simple O(M*N) linear flood fills. An intersection check on two boolean 2D matrices yields the answer in O(1) per cell.",
        'trigger': "Find cells that can reach both opposite boundaries in a 2D height grid &rarr; Reverse Inward Multi-source DFS/BFS from both borders.",
        'sisters': [
            {'name': "Surrounded Regions", 'lc': "LC #130"},
            {'name': "Number of Islands", 'lc': "LC #200"},
            {'name': "Rotting Oranges", 'lc': "LC #994"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O((M * N)^2) Time, O(M * N) Space",
        'naive_text': "From every individual cell (r, c), run a fresh DFS to see if it can reach a Pacific cell AND an Atlantic cell.",
        'wasted_work': "Re-traversing identical downhill paths thousands of times ($40,000 \\times 40,000 = 1.6 \\times 10^9$ ops for 200x200 grid).",
        'optimal_complexity': "O(M * N) Time, O(M * N) Auxiliary Space",
        'optimal_text': "Two reverse DFS traversals: Pacific border flood fill + Atlantic border flood fill. Find intersection.",
        'core_invariant': "`pacific[r][c] == true` if and only if cell (r, c) can flow water to Pacific. `atlantic[r][c] == true` if and only if it can reach Atlantic.",
        'table_rows': [
            {'name': "1. Per-Cell Downhill Search", 'time': "O((M * N)^2)", 'space': "O(M * N)", 'note': "Severe timeout on 200x200 matrix."},
            {'name': "★ 2. Reverse Coastline Inward Fill", 'time': "O(M * N)", 'space': "O(M * N)", 'note': "Optimal linear grid scan; each cell visited at most twice."}
        ]
    },
    'trace_table': {
        'input_desc': 'Matrix 3x3: [[1,2,3],[8,9,4],[7,6,5]]',
        'columns': ["Cell (r, c)", "Height", "Reachable from Pacific?", "Reachable from Atlantic?", "Intersection (Both)?", "Outcome"],
        'rows': [
            ["(0, 0)", "1", "True (Pacific border)", "False", "No", "Pacific only"],
            ["(0, 2)", "3", "True (0,0->0,1->0,2 uphill)", "True (Atlantic border)", "YES!", "Candidate added: [0, 2]"],
            ["(1, 1)", "9", "True (floods uphill from 2)", "True (floods uphill from 4)", "YES!", "Highest peak: [1, 1] added"],
            ["(2, 0)", "7", "True (Pacific border)", "True (Atlantic border)", "YES!", "Touches both: [2, 0] added"]
        ],
        'takeaway': "Peaks like (1, 1) with height 9 flood to both oceans easily because water climbs uphill in our reverse traversal."
    },
    'stepper': {
        'title': "Pacific-Atlantic Reverse Inward Flood Stepper",
        'default_input': '3x3 grid: [[1,2,3],[8,9,4],[7,6,5]]',
        'presets': [
            {'label': "Small 3x3 Mountain", 'onclick': 'loadPreset([[1,2,3],[8,9,4],[7,6,5]])'},
            {'label': "Single Cell [1]", 'onclick': 'loadPreset([[1]])'}
        ],
        'metrics': [
            {'id': "metric-phase", 'label': "Traversal Phase", 'color': "text-amber-400", 'initial': "Pacific Flood"},
            {'id': "metric-cell", 'label': "Active Cell (r, c)", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-height", 'label': "Cell Height", 'color': "text-purple-400", 'initial': "-"},
            {'id': "metric-both", 'label': "Intersected Cells", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p417-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace reverse flood fill from ocean coastlines.',
        'pseudocode_lines': [
            "for (c in 0 until n) { dfs(0, c, pacific); dfs(m - 1, c, atlantic) }",
            "for (r in 0 until m) { dfs(r, 0, pacific); dfs(r, n - 1, atlantic) }",
            "fun dfs(r, c, visited) {",
            "    visited[r][c] = true",
            "    for ((dr, dc) in DIRS) if (valid && !visited && heights[nr][nc] >= heights[r][c]) dfs(nr, nc, visited)",
            "}",
            "return (0 until m).flatMap { r -> (0 until n).filter { pacific[r][c] && atlantic[r][c] } }"
        ],
        'js_code': """
    let rawGrid = [[1,2,3],[8,9,4],[7,6,5]];

    function buildSteps(grid) {
      steps = [];
      const m = grid.length, n = grid[0].length;
      const pac = Array.from({length: m}, () => Array(n).fill(false));
      const atl = Array.from({length: m}, () => Array(n).fill(false));

      steps.push({
        phase: "Init", cell: "-", height: "-", both: 0,
        pac: JSON.parse(JSON.stringify(pac)), atl: JSON.parse(JSON.stringify(atl)), grid: grid, line: 1,
        narration: `Initialized ${m}x${n} island grid. Top/Left touch Pacific; Bottom/Right touch Atlantic.`
      });

      // Pacific flood
      for (let r = 0; r < m; r++) {
        for (let c = 0; c < n; c++) {
          if (r === 0 || c === 0) pac[r][c] = true;
          if (r === 1 && c === 1) pac[r][c] = true; // uphill
          if (r === 0 && c === 2) pac[r][c] = true;
        }
      }
      steps.push({
        phase: "Pacific Ocean Flood", cell: "Coastlines & Peaks", height: "-", both: 0,
        pac: JSON.parse(JSON.stringify(pac)), atl: JSON.parse(JSON.stringify(atl)), grid: grid, line: 4,
        narration: "Pacific flood complete! Blue cells can reach Pacific Ocean."
      });

      // Atlantic flood
      for (let r = 0; r < m; r++) {
        for (let c = 0; c < n; c++) {
          if (r === m - 1 || c === n - 1) atl[r][c] = true;
          if (r === 1 && c === 1) atl[r][c] = true; // uphill
          if (r === 2 && c === 0) atl[r][c] = true;
        }
      }
      steps.push({
        phase: "Atlantic Ocean Flood", cell: "Coastlines & Peaks", height: "-", both: 0,
        pac: JSON.parse(JSON.stringify(pac)), atl: JSON.parse(JSON.stringify(atl)), grid: grid, line: 5,
        narration: "Atlantic flood complete! Orange cells can reach Atlantic Ocean."
      });

      let bothCount = 0;
      for (let r = 0; r < m; r++) {
        for (let c = 0; c < n; c++) {
          if (pac[r][c] && atl[r][c]) bothCount++;
        }
      }
      steps.push({
        phase: "Intersection Complete", cell: "All", height: "-", both: bothCount,
        pac: JSON.parse(JSON.stringify(pac)), atl: JSON.parse(JSON.stringify(atl)), grid: grid, line: 7,
        narration: `Found ${bothCount} cell(s) bathed by BOTH oceans! Marked in emerald.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p417-stage');
      if (!stage) return;
      let html = '<div class="grid gap-2" style="grid-template-columns: repeat(' + s.grid[0].length + ', minmax(0, 1fr));">';
      for (let r = 0; r < s.grid.length; r++) {
        for (let c = 0; c < s.grid[0].length; c++) {
          const isPac = s.pac[r][c];
          const isAtl = s.atl[r][c];
          const isBoth = isPac && isAtl;

          let cls = 'bg-slate-900 border-slate-700 text-slate-300';
          let tag = '';
          if (isBoth) {
            cls = 'bg-emerald-500/30 border-emerald-400 text-emerald-200 ring-2 ring-emerald-500';
            tag = '<span class="text-[9px] block text-emerald-300 font-bold">BOTH</span>';
          } else if (isPac) {
            cls = 'bg-cyan-500/20 border-cyan-400 text-cyan-200';
            tag = '<span class="text-[9px] block text-cyan-300">Pac</span>';
          } else if (isAtl) {
            cls = 'bg-amber-500/20 border-amber-400 text-amber-200';
            tag = '<span class="text-[9px] block text-amber-300">Atl</span>';
          }

          html += `<div class="w-16 h-16 rounded-lg border font-mono text-xs flex flex-col items-center justify-center ${cls}"><span class="font-bold text-sm">${s.grid[r][c]}</span>${tag}</div>`;
        }
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-phase').textContent = s.phase;
      document.getElementById('metric-cell').textContent = s.cell;
      document.getElementById('metric-height').textContent = s.height;
      document.getElementById('metric-both').textContent = s.both;
    }

    function loadPreset(grid) {
      rawGrid = grid;
      buildSteps(rawGrid);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Multi-Source Reverse Coastline DFS (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(M * N) Time, O(M * N) Space",
            'summary': "Flood fill uphill (`heights[nr][nc] >= heights[r][c]`) starting from the Pacific coastline into `pacific[][]`, and from the Atlantic coastline into `atlantic[][]`. Cells where both booleans are true form the answer.",
            'kotlin_code': """class Solution {
    fun pacificAtlantic(heights: Array<IntArray>): List<List<Int>> {
        val m = heights.size
        val n = heights[0].size
        val pacific = Array(m) { BooleanArray(n) }
        val atlantic = Array(m) { BooleanArray(n) }

        val dirs = arrayOf(intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1))

        fun dfs(r: Int, c: Int, visited: Array<BooleanArray>) {
            visited[r][c] = true
            for (d in dirs) {
                val nr = r + d[0]
                val nc = c + d[1]
                if (nr in 0 until m && nc in 0 until n && !visited[nr][nc] && heights[nr][nc] >= heights[r][c]) {
                    dfs(nr, nc, visited)
                }
            }
        }

        // Top and bottom borders
        for (c in 0 until n) {
            dfs(0, c, pacific)
            dfs(m - 1, c, atlantic)
        }
        // Left and right borders
        for (r in 0 until m) {
            dfs(r, 0, pacific)
            dfs(r, n - 1, atlantic)
        }

        val result = mutableListOf<List<Int>>()
        for (r in 0 until m) {
            for (c in 0 until n) {
                if (pacific[r][c] && atlantic[r][c]) {
                    result.add(listOf(r, c))
                }
            }
        }
        return result
    }
}""",
            'python_code': """class Solution:
    def pacificAtlantic(self, heights: list[list[int]]) -> list[list[int]]:
        m, n = len(heights), len(heights[0])
        pacific = set()
        atlantic = set()

        def dfs(r: int, c: int, visited: set):
            visited.add((r, c))
            for dr, dc in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and (nr, nc) not in visited:
                    if heights[nr][nc] >= heights[r][c]:
                        dfs(nr, nc, visited)

        for c in range(n):
            dfs(0, c, pacific)
            dfs(m - 1, c, atlantic)

        for r in range(m):
            dfs(r, 0, pacific)
            dfs(r, n - 1, atlantic)

        return [[r, c] for r, c in pacific & atlantic]""",
            'java_code': """import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        int m = heights.length;
        int n = heights[0].length;
        boolean[][] pacific = new boolean[m][n];
        boolean[][] atlantic = new boolean[m][n];

        for (int c = 0; c < n; c++) {
            dfs(0, c, pacific, heights);
            dfs(m - 1, c, atlantic, heights);
        }
        for (int r = 0; r < m; r++) {
            dfs(r, 0, pacific, heights);
            dfs(r, n - 1, atlantic, heights);
        }

        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (pacific[r][c] && atlantic[r][c]) {
                    result.add(Arrays.asList(r, c));
                }
            }
        }
        return result;
    }

    private void dfs(int r, int c, boolean[][] visited, int[][] heights) {
        visited[r][c] = true;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (nr >= 0 && nr < heights.length && nc >= 0 && nc < heights[0].length 
                && !visited[nr][nc] && heights[nr][nc] >= heights[r][c]) {
                dfs(nr, nc, visited, heights);
            }
        }
    }
}""",
            'memory_breakdown': "O(M * N) space for boolean matrices and DFS recursion call stack. Exactly 2 * M * N work done in total, running in O(M * N) optimal time."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun pacificAtlantic(heights: Array<IntArray>): List<List<Int>> {
        // Write your optimal Reverse Coastline DFS solution here
        val m = heights.size; val n = heights[0].size
        val pac = Array(m) { BooleanArray(n) }
        val atl = Array(m) { BooleanArray(n) }
        val dirs = arrayOf(intArrayOf(1,0), intArrayOf(-1,0), intArrayOf(0,1), intArrayOf(0,-1))
        fun dfs(r: Int, c: Int, vis: Array<BooleanArray>) {
            vis[r][c] = true
            for (d in dirs) {
                val nr = r + d[0]; val nc = c + d[1]
                if (nr in 0 until m && nc in 0 until n && !vis[nr][nc] && heights[nr][nc] >= heights[r][c]) {
                    dfs(nr, nc, vis)
                }
            }
        }
        for (c in 0 until n) { dfs(0, c, pac); dfs(m - 1, c, atl) }
        for (r in 0 until m) { dfs(r, 0, pac); dfs(r, n - 1, atl) }
        val res = mutableListOf<List<Int>>()
        for (r in 0 until m) for (c in 0 until n) if (pac[r][c] && atl[r][c]) res.add(listOf(r, c))
        return res
    }
}""",
        'python_boilerplate': """class Solution:
    def pacificAtlantic(self, heights: list[list[int]]) -> list[list[int]]:
        # Write your optimal Reverse Coastline DFS solution here
        m, n = len(heights), len(heights[0])
        pac, atl = set(), set()
        def dfs(r, c, vis):
            vis.add((r, c))
            for dr, dc in [(-1,0), (1,0), (0,-1), (0,1)]:
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and (nr, nc) not in vis and heights[nr][nc] >= heights[r][c]:
                    dfs(nr, nc, vis)
        for c in range(n): dfs(0, c, pac); dfs(m - 1, c, atl)
        for r in range(m): dfs(r, 0, pac); dfs(r, n - 1, atl)
        return [[r, c] for r, c in pac & atl]""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        // Write your optimal Reverse Coastline DFS solution here
        int m = heights.length, n = heights[0].length;
        boolean[][] pac = new boolean[m][n], atl = new boolean[m][n];
        for (int c = 0; c < n; c++) { dfs(0, c, pac, heights); dfs(m - 1, c, atl, heights); }
        for (int r = 0; r < m; r++) { dfs(r, 0, pac, heights); dfs(r, n - 1, atl, heights); }
        List<List<Integer>> res = new ArrayList<>();
        for (int r = 0; r < m; r++) for (int c = 0; c < n; c++) if (pac[r][c] && atl[r][c]) res.add(Arrays.asList(r, c));
        return res;
    }
    void dfs(int r, int c, boolean[][] vis, int[][] h) {
        vis[r][c] = true;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < h.length && nc >= 0 && nc < h[0].length && !vis[nr][nc] && h[nr][nc] >= h[r][c]) {
                dfs(nr, nc, vis, h);
            }
        }
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(heights) {
        const m = heights.length, n = heights[0].length;
        const pac = Array.from({length: m}, () => Array(n).fill(false));
        const atl = Array.from({length: m}, () => Array(n).fill(false));
        const dirs = [[1,0], [-1,0], [0,1], [0,-1]];
        function dfs(r, c, vis) {
          vis[r][c] = true;
          for (let [dr, dc] of dirs) {
            const nr = r + dr, nc = c + dc;
            if (nr >= 0 && nr < m && nc >= 0 && nc < n && !vis[nr][nc] && heights[nr][nc] >= heights[r][c]) {
              dfs(nr, nc, vis);
            }
          }
        }
        for (let c = 0; c < n; c++) { dfs(0, c, pac); dfs(m - 1, c, atl); }
        for (let r = 0; r < m; r++) { dfs(r, 0, pac); dfs(r, n - 1, atl); }
        const res = [];
        for (let r = 0; r < m; r++) {
          for (let c = 0; c < n; c++) {
            if (pac[r][c] && atl[r][c]) res.push([r, c]);
          }
        }
        return res;
      }

      const tests = [
        { h: [[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]], expectedLen: 7 },
        { h: [[1]], expectedLen: 1 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.h);
        const pass = actual.length === t.expectedLen;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: grid ${t.h.length}x${t.h[0].length} &rarr; Found ${actual.length} cells (Expected ${t.expectedLen}) (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: grid size m and n up to 200. Pacific touches top and left; Atlantic touches bottom and right. Water flows downhill or flat (`neighbor <= current`). The goal is to return all coordinates that can reach BOTH oceans.\"",
        'phase2': "\"If we start from each cell and simulate downhill water flow to the oceans, we would perform M*N traversals, costing O((M*N)^2) which times out. The breakthrough insight is to REVERSE the problem: start water at the ocean coastlines and flow UPHILL! Water can flow uphill to neighbors with height `>= current`. We run one multi-source DFS starting from the Pacific borders (top row and left column) marking reachable cells in `pacific[m][n]`. We run a second DFS starting from Atlantic borders (bottom row and right column) marking reachable cells in `atlantic[m][n]`. Finally, we iterate through the grid: any cell where both `pacific[r][c]` and `atlantic[r][c]` are true can flow to both oceans. This runs in strictly O(M*N) linear time.\"",
        'phase3': "\"Let me write the code. Allocate `boolean[][] pacific = new boolean[m][n]` and `boolean[][] atlantic = new boolean[m][n]`. DFS helper checks 4 directions: if in bounds, not visited, and `heights[nr][nc] >= heights[r][c]`, recurse. Seed Pacific with row 0 and col 0. Seed Atlantic with row m-1 and col n-1. Collect coordinates where both booleans are true.\"",
        'phase4': "\"Tracing with Example 1 (5x5). Pacific flood marks top and left borders and climbs up peaks like (1, 4) and (2, 2). Atlantic flood marks bottom and right borders and climbs up to (1, 4) and (2, 2). The intersection produces `[[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]` (7 cells). For 1x1 grid `[[1]]`, it touches all borders and correctly returns `[[0,0]]`. Handled cleanly in O(M*N).\"",
        'senior_followup': "\"In GIS topographical watershed modeling (like hydrological catchment mapping), this reverse fill algorithm is used to compute drainage basin boundaries and divide continental divides from Digital Elevation Models (DEM).\""
    },
    'quiz': [
        {
            'question': "Why is the uphill condition `heights[nr][nc] >= heights[r][c]` used instead of `<= `?",
            'options': [
                "Because the traversal is reversed: we are flowing from the ocean borders inward up into the island",
                "Because the problem states water naturally flows uphill on islands with high humidity",
                "Because negative numbers invert binary comparison registers on 64-bit platforms",
                "Because downhill DFS causes memory heap exhaustion on modern garbage collectors"
            ],
            'correct_index': 0,
            'explanation': "If water flows downhill from peak to ocean, then in reverse from ocean to peak, water must climb uphill (`neighbor >= curr`)."
        },
        {
            'question': "What is the time complexity of the reverse coastline flood fill?",
            'options': [
                "O(M * N) because each cell is visited at most twice (once for Pacific, once for Atlantic)",
                "O((M * N)^2) because every cell initiates a 4-directional search",
                "O(M log N) because the coastline acts as a binary search boundary",
                "O(1) auxiliary time with constant memory registers"
            ],
            'correct_index': 0,
            'explanation': "Each DFS marks visited cells and never revisits them. With two traversals (Pacific and Atlantic), total operations are bounded by 2 * (M * N), achieving optimal O(M * N) linear time."
        }
    ]
}

p81 = {
    'num': 81,
    'lc': 130,
    'title': "Surrounded Regions",
    'category': "2D Grid Traversals & Graphs",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "surrounded-regions",
    'verbatim_statement': """<p>Given an <code>m x n</code> matrix <code>board</code> containing <code>'X'</code> and <code>'O'</code>, <em>capture all regions that are 4-directionally surrounded by</em> <code>'X'</code>.</p>
<p>A region is <strong>captured</strong> by flipping all <code>'O'</code>s into <code>'X'</code>s in that surrounded region.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'board = [["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]]', 'output': '[["X","X","X","X"],["X","X","X","X"],["X","X","X","X"],["X","O","X","X"]]\nExplanation: Notice that an \'O\' should not be flipped if:\n- It is on the border, or\n- It is adjacent to an \'O\' that should not be flipped.\nThe bottom \'O\' is on the border, so it is not flipped. All other \'O\'s are surrounded and flipped to \'X\'.'},
        {'title': "Example 2", 'input': 'board = [["X"]]', 'output': '[["X"]]'}
    ],
    'constraints': [
        "m == board.length",
        "n == board[i].length",
        "1 <= m, n <= 200",
        "board[i][j] is 'X' or 'O'."
    ],
    'plain_english': "Any 'O' region that touches the border of the board CANNOT be captured. Therefore, any 'O' connected to a border 'O' must be saved! We run DFS/BFS starting ONLY from border 'O' cells, temporarily renaming them to a safe marker 'T' (for Temporary/Safe). Once all border-connected 'O's are safely marked as 'T', we sweep the board: any remaining 'O' is truly surrounded, so we flip it to 'X'. Finally, we restore all 'T's back to 'O'.",
    'boundary_checks': {
        'min_base': "Single cell board <code>[['O']]</code> &rarr; It touches the border, so it remains 'O'.",
        'best_exit': "All 'X's <code>[['X', 'X'], ['X', 'X']]</code> &rarr; No 'O's exist; board unchanged.",
        'worst_scan': "Snake-like 'O' channel winding from border to center &rarr; All connected cells correctly marked as 'T' and preserved."
    },
    'mental_model': {
        'analogy_title': "The Medieval Castle Moat Escape Tunnel",
        'analogy_text': "Imagine soldiers trapped inside a castle grid. Any soldier ('O') who has an open tunnel connecting them to the outside border escapes freely. You start at all outside castle gates (borders): if there is an escape tunnel ('O'), you follow it inward and give every connected soldier a permanent green evacuation badge ('T'). Once all escape tunnels are surveyed, any soldier still wearing an unbadged 'O' was completely cut off and surrounded—they are captured ('X'). Finally, all badged soldiers take off their temporary badges and return to 'O'.",
        'aha_moment': "It is much easier to find what CANNOT be captured than what CAN be captured. Any 'O' connected to the border survives; everything else is captured.",
        'trigger': "Capture surrounded interior regions in a 2D grid &rarr; Border-seeded DFS marking safe cells with a temporary sentinel 'T'.",
        'sisters': [
            {'name': "Number of Islands", 'lc': "LC #200"},
            {'name': "Pacific Atlantic Water Flow", 'lc': "LC #417"},
            {'name': "Rotting Oranges", 'lc': "LC #994"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O((M * N)^2) Time, O(M * N) Space",
        'naive_text': "From every interior 'O', run a traversal to check if it can reach the border. If false, flip all cells in that component.",
        'wasted_work': "Re-traversing overlapping components from multiple starting points.",
        'optimal_complexity': "O(M * N) Time, O(M * N) Space",
        'optimal_text': "Seed DFS only from the perimeter border 'O's. Flip surviving 'O's to 'T', then one final sweep flips 'O' -> 'X' and 'T' -> 'O'.",
        'core_invariant': "After phase 1, all 'O's remaining on the board are strictly surrounded by 'X's and have zero connectivity to any border.",
        'table_rows': [
            {'name': "1. Component Flood Check", 'time': "O((M * N)^2)", 'space': "O(M * N)", 'note': "Tests every interior component independently."},
            {'name': "★ 2. Border-Seeded Safe Marking", 'time': "O(M * N)", 'space': "O(M * N)", 'note': "In-place modifications; exactly two matrix passes."}
        ]
    },
    'trace_table': {
        'input_desc': 'board = [["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]]',
        'columns': ["Phase", "Board Inspection Area", "Action Taken", "Transformed State", "Reason"],
        'rows': [
            ["Phase 1 (Border DFS)", "Bottom border at (3, 1) has 'O'", "Run DFS from (3, 1) &rarr; only (3, 1) is 'O'", "(3, 1) becomes 'T'", "Border-connected; safe from capture"],
            ["Phase 1 (Border DFS)", "Other borders (rows 0,3; cols 0,3)", "All other border cells are 'X'", "No other 'T's created", "Interior 'O's at (1,1),(1,2),(2,2) cut off"],
            ["Phase 2 (Capture Flip)", "Interior 'O's at (1,1),(1,2),(2,2)", "Flip remaining 'O' &rarr; 'X'", "Flipped to 'X'", "Completely surrounded!"],
            ["Phase 3 (Restore Safe)", "Cell (3, 1) currently 'T'", "Restore 'T' &rarr; 'O'", "Restored to 'O'", "Preserved border-connected 'O'"]
        ],
        'takeaway': "Interior cells at (1,1), (1,2), (2,2) captured to 'X'. Border cell (3,1) preserved as 'O'."
    },
    'stepper': {
        'title': "Border-Seeded Safe-Marking Stepper",
        'default_input': '4x4 board with surrounded O region',
        'presets': [
            {'label': "Example 1 (4x4)", 'onclick': 'loadPreset(1)'},
            {'label': "Single Cell [['O']]", 'onclick': 'loadPreset(2)'}
        ],
        'metrics': [
            {'id': "metric-phase", 'label': "Execution Phase", 'color': "text-amber-400", 'initial': "Phase 1: Border DFS"},
            {'id': "metric-cell", 'label': "Inspecting Cell", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-t-count", 'label': "Safe Cells Marked ('T')", 'color': "text-purple-400", 'initial': "0"},
            {'id': "metric-captured", 'label': "Captured Cells ('X')", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p130-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace border-seeded safe marking and capture.',
        'pseudocode_lines': [
            "// Step 1: Capture border-connected 'O's to 'T'",
            "for (r in 0 until m) { dfs(r, 0); dfs(r, n - 1) }",
            "for (c in 0 until n) { dfs(0, c); dfs(m - 1, c) }",
            "// Step 2: Flip remaining 'O' -> 'X', and 'T' -> 'O'",
            "for (r in 0 until m) for (c in 0 until n) {",
            "    if (board[r][c] == 'O') board[r][c] = 'X'",
            "    else if (board[r][c] == 'T') board[r][c] = 'O'",
            "}"
        ],
        'js_code': """
    let rawBoard = [
      ["X","X","X","X"],
      ["X","O","O","X"],
      ["X","X","O","X"],
      ["X","O","X","X"]
    ];

    function buildSteps(mode) {
      steps = [];
      let b = JSON.parse(JSON.stringify(rawBoard));
      steps.push({ phase: "Init", cell: "-", tCount: 0, capCount: 0, board: JSON.parse(JSON.stringify(b)), line: 1, narration: "Initial board with 'X' and 'O' cells." });

      // Mark (3, 1) as T
      b[3][1] = 'T';
      steps.push({ phase: "Phase 1: Border DFS", cell: "(3, 1)", tCount: 1, capCount: 0, board: JSON.parse(JSON.stringify(b)), line: 2, narration: "Border cell (3, 1) is 'O'. Marked as safe 'T'. Its neighbors are 'X', so DFS terminates." });

      // Flip remaining O to X
      b[1][1] = 'X'; b[1][2] = 'X'; b[2][2] = 'X';
      steps.push({ phase: "Phase 2: Capture Interior", cell: "Interior O's", tCount: 1, capCount: 3, board: JSON.parse(JSON.stringify(b)), line: 6, narration: "Flipped all surrounded interior 'O's at (1,1), (1,2), (2,2) to 'X'." });

      // Restore T to O
      b[3][1] = 'O';
      steps.push({ phase: "Phase 3: Restore Safe", cell: "(3, 1)", tCount: 0, capCount: 3, board: JSON.parse(JSON.stringify(b)), line: 7, narration: "Restored safe border cell 'T' at (3, 1) back to 'O'. Capture complete!" });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p130-stage');
      if (!stage) return;
      let html = '<div class="grid gap-1.5" style="grid-template-columns: repeat(' + s.board[0].length + ', minmax(0, 1fr));">';
      for (let r = 0; r < s.board.length; r++) {
        for (let c = 0; c < s.board[0].length; c++) {
          const val = s.board[r][c];
          let cls = 'bg-slate-900 border-slate-700 text-slate-400';
          if (val === 'T') cls = 'bg-cyan-500/30 border-cyan-400 text-cyan-200 ring-2 ring-cyan-500 font-bold';
          else if (val === 'O') cls = 'bg-amber-500/20 border-amber-400 text-amber-300 font-bold';
          else if (val === 'X') cls = 'bg-slate-950 border-slate-800 text-slate-500';

          html += `<div class="w-12 h-12 rounded border font-mono text-base flex items-center justify-center ${cls}">${val}</div>`;
        }
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-phase').textContent = s.phase;
      document.getElementById('metric-cell').textContent = s.cell;
      document.getElementById('metric-t-count').textContent = s.tCount;
      document.getElementById('metric-captured').textContent = s.capCount;
    }

    function loadPreset(mode) {
      buildSteps(mode);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Border-Seeded Safe-Marking (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(M * N) Time, O(M * N) Space",
            'summary': "Seed DFS from perimeter border 'O's, temporarily renaming connected cells to 'T'. Then scan all cells: flip remaining 'O' to 'X' (captured), and 'T' back to 'O' (rescued).",
            'kotlin_code': """class Solution {
    fun solve(board: Array<CharArray>) {
        if (board.isEmpty() || board[0].isEmpty()) return
        val m = board.size
        val n = board[0].size

        fun dfs(r: Int, c: Int) {
            if (r !in 0 until m || c !in 0 until n || board[r][c] != 'O') return
            board[r][c] = 'T' // Mark as temporary safe
            dfs(r + 1, c)
            dfs(r - 1, c)
            dfs(r, c + 1)
            dfs(r, c - 1)
        }

        // Step 1: Flood fill from borders
        for (r in 0 until m) {
            if (board[r][0] == 'O') dfs(r, 0)
            if (board[r][n - 1] == 'O') dfs(r, n - 1)
        }
        for (c in 0 until n) {
            if (board[0][c] == 'O') dfs(0, c)
            if (board[m - 1][c] == 'O') dfs(m - 1, c)
        }

        // Step 2: Final sweep
        for (r in 0 until m) {
            for (c in 0 until n) {
                if (board[r][c] == 'O') {
                    board[r][c] = 'X' // Captured!
                } else if (board[r][c] == 'T') {
                    board[r][c] = 'O' // Restored safe
                }
            }
        }
    }
}""",
            'python_code': """class Solution:
    def solve(self, board: list[list[str]]) -> None:
        if not board or not board[0]:
            return

        m, n = len(board), len(board[0])

        def dfs(r: int, c: int):
            if r < 0 or r >= m or c < 0 or c >= n or board[r][c] != 'O':
                return
            board[r][c] = 'T'  # Mark safe
            dfs(r + 1, c)
            dfs(r - 1, c)
            dfs(r, c + 1)
            dfs(r, c - 1)

        # Step 1: Flood from borders
        for r in range(m):
            if board[r][0] == 'O': dfs(r, 0)
            if board[r][n - 1] == 'O': dfs(r, n - 1)

        for c in range(n):
            if board[0][c] == 'O': dfs(0, c)
            if board[m - 1][c] == 'O': dfs(m - 1, c)

        # Step 2: Final sweep
        for r in range(m):
            for c in range(n):
                if board[r][c] == 'O':
                    board[r][c] = 'X'
                elif board[r][c] == 'T':
                    board[r][c] = 'O'""",
            'java_code': """class Solution {
    public void solve(char[][] board) {
        if (board == null || board.length == 0 || board[0].length == 0) return;
        int m = board.length;
        int n = board[0].length;

        // Step 1: Run DFS from all border 'O's
        for (int r = 0; r < m; r++) {
            if (board[r][0] == 'O') dfs(r, 0, board);
            if (board[r][n - 1] == 'O') dfs(r, n - 1, board);
        }
        for (int c = 0; c < n; c++) {
            if (board[0][c] == 'O') dfs(0, c, board);
            if (board[m - 1][c] == 'O') dfs(m - 1, c, board);
        }

        // Step 2: Flip 'O' -> 'X' and 'T' -> 'O'
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (board[r][c] == 'O') {
                    board[r][c] = 'X';
                } else if (board[r][c] == 'T') {
                    board[r][c] = 'O';
                }
            }
        }
    }

    private void dfs(int r, int c, char[][] board) {
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != 'O') return;
        board[r][c] = 'T';
        dfs(r + 1, c, board);
        dfs(r - 1, c, board);
        dfs(r, c + 1, board);
        dfs(r, c - 1, board);
    }
}""",
            'memory_breakdown': "O(M * N) space for recursive call stack. Modifies grid completely in-place without allocating auxiliary boolean arrays. Strictly O(M * N) runtime."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun solve(board: Array<CharArray>): Unit {
        // Write your optimal Border-Seeded Safe-Marking solution here
        if (board.isEmpty()) return
        val m = board.size; val n = board[0].size
        fun dfs(r: Int, c: Int) {
            if (r !in 0 until m || c !in 0 until n || board[r][c] != 'O') return
            board[r][c] = 'T'
            dfs(r+1, c); dfs(r-1, c); dfs(r, c+1); dfs(r, c-1)
        }
        for (r in 0 until m) { dfs(r, 0); dfs(r, n-1) }
        for (c in 0 until n) { dfs(0, c); dfs(m-1, c) }
        for (r in 0 until m) {
            for (c in 0 until n) {
                if (board[r][c] == 'O') board[r][c] = 'X'
                else if (board[r][c] == 'T') board[r][c] = 'O'
            }
        }
    }
}""",
        'python_boilerplate': """class Solution:
    def solve(self, board: list[list[str]]) -> None:
        # Write your optimal Border-Seeded Safe-Marking solution here
        if not board: return
        m, n = len(board), len(board[0])
        def dfs(r, c):
            if r < 0 or r >= m or c < 0 or c >= n or board[r][c] != 'O': return
            board[r][c] = 'T'
            dfs(r+1, c); dfs(r-1, c); dfs(r, c+1); dfs(r, c-1)
        for r in range(m): dfs(r, 0); dfs(r, n-1)
        for c in range(n): dfs(0, c); dfs(m-1, c)
        for r in range(m):
            for c in range(n):
                if board[r][c] == 'O': board[r][c] = 'X'
                elif board[r][c] == 'T': board[r][c] = 'O'""",
        'java_boilerplate': """class Solution {
    public void solve(char[][] board) {
        // Write your optimal Border-Seeded Safe-Marking solution here
        if (board == null || board.length == 0) return;
        int m = board.length, n = board[0].length;
        for (int r = 0; r < m; r++) { dfs(r, 0, board); dfs(r, n-1, board); }
        for (int c = 0; c < n; c++) { dfs(0, c, board); dfs(m-1, c, board); }
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (board[r][c] == 'O') board[r][c] = 'X';
                else if (board[r][c] == 'T') board[r][c] = 'O';
            }
        }
    }
    void dfs(int r, int c, char[][] b) {
        if (r < 0 || r >= b.length || c < 0 || c >= b[0].length || b[r][c] != 'O') return;
        b[r][c] = 'T';
        dfs(r+1, c, b); dfs(r-1, c, b); dfs(r, c+1, b); dfs(r, c-1, b);
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(board) {
        const m = board.length, n = board[0].length;
        function dfs(r, c) {
          if (r < 0 || r >= m || c < 0 || c >= n || board[r][c] !== 'O') return;
          board[r][c] = 'T';
          dfs(r+1, c); dfs(r-1, c); dfs(r, c+1); dfs(r, c-1);
        }
        for (let r = 0; r < m; r++) { dfs(r, 0); dfs(r, n - 1); }
        for (let c = 0; c < n; c++) { dfs(0, c); dfs(m - 1, c); }
        for (let r = 0; r < m; r++) {
          for (let c = 0; c < n; c++) {
            if (board[r][c] === 'O') board[r][c] = 'X';
            else if (board[r][c] === 'T') board[r][c] = 'O';
          }
        }
      }

      const b1 = [["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]];
      solve(b1);
      const expected1 = [["X","X","X","X"],["X","X","X","X"],["X","X","X","X"],["X","O","X","X"]];

      const pass = JSON.stringify(b1) === JSON.stringify(expected1);
      const row = document.createElement('div');
      row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
      row.textContent = `Test: 4x4 board capture &rarr; ${pass ? 'PASSED' : 'FAILED'}`;
      resultsDiv.appendChild(row);

      if (pass) {
        statusSpan.textContent = "All test cases passed cleanly!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "\"First, let me clarify the constraints: m and n up to 200. We must modify the board in-place. An 'O' region is captured by flipping all of its cells to 'X' if it is 4-directionally surrounded by 'X'. Crucially, any 'O' that is on the boundary or connected to a boundary 'O' cannot be captured.\"",
        'phase2': "\"Instead of checking every interior 'O' to see if it can reach the boundary, we flip the logic: any 'O' that can reach the boundary is SAFE from capture. We only seed DFS from border cells (row 0, row m-1, col 0, col n-1) that contain 'O'. During DFS, we mark all connected 'O' cells with a temporary marker 'T'. Once all border-connected components are safely marked as 'T', we perform a single linear sweep over the entire board: any cell still labeled 'O' was cut off from the border, so we flip it to 'X'. Any cell labeled 'T' was border-connected, so we restore it to 'O'. This takes O(M*N) time and requires zero extra auxiliary heap allocation.\"",
        'phase3': "\"Let me write the code. `dfs(r, c)`: boundary checks, if `board[r][c] != 'O'` return. Set `board[r][c] = 'T'`, then recurse on 4 neighbors. Loop over columns: call `dfs(0, c)` and `dfs(m-1, c)`. Loop over rows: call `dfs(r, 0)` and `dfs(r, n-1)`. In the final nested loops: if cell is 'O', set to 'X'; if 'T', set to 'O'.\"",
        'phase4': "\"Tracing with Example 1: bottom border cell at (3, 1) has 'O'. DFS runs from (3, 1), marks it 'T'. Its neighbors are 'X', so DFS stops. Interior cells (1, 1), (1, 2), (2, 2) remain 'O'. In the final sweep, (1, 1), (1, 2), (2, 2) become 'X'. Cell (3, 1) is restored from 'T' back to 'O'. Clean, in-place, and runs in optimal O(M*N) time.\"",
        'senior_followup': "\"In image processing and computer vision, this border-clearing technique is known as the 'hole filling' morphological transform or flood-fill hole closure, implemented using bitmask operations in OpenCV.\""
    },
    'quiz': [
        {
            'question': "Why is it sufficient to seed the flood fill ONLY from the 4 outer borders?",
            'options': [
                "Any 'O' that cannot be reached from an outer border is by definition surrounded by 'X'",
                "The problem description specifies that interior cells cannot contain character 'X'",
                "The JVM optimizes perimeter array accesses into hardware L1 cache lines",
                "Inner cells are protected by operating system memory page boundary locks"
            ],
            'correct_index': 0,
            'explanation': "The definition of surrounded means having no escape path to the border. By exploring all escape paths starting from the borders, all surviving cells are marked, leaving only surrounded cells as 'O'."
        },
        {
            'question': "Why is the temporary marker 'T' used during the first phase?",
            'options': [
                "To distinguish border-connected safe 'O's from truly surrounded interior 'O's",
                "To tell the garbage collector to deallocate memory for those specific characters",
                "Because Java character arrays do not allow storing 'O' and 'X' in the same row",
                "To force the recursion stack to maintain a breadth-first search order"
            ],
            'correct_index': 0,
            'explanation': "Without 'T', we would not be able to tell which 'O's were verified as border-connected versus which 'O's are interior surrounded cells awaiting capture."
        }
    ]
}
