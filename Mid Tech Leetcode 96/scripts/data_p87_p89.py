import sys
sys.path.append('scripts')

p87 = {
    'num': 87,
    'lc': 139,
    'title': "Word Break",
    'category': "Dynamic Programming",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "word-break",
    'verbatim_statement': """<p>Given a string <code>s</code> and a dictionary of strings <code>wordDict</code>, return <code>true</code> if <code>s</code> can be segmented into a space-separated sequence of one or more dictionary words.</p>
<p><strong>Note</strong> that the same word in the dictionary may be reused multiple times in the segmentation.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 's = "leetcode", wordDict = ["leet","code"]', 'output': 'true\nExplanation: Return true because "leetcode" can be segmented as "leet code".'},
        {'title': "Example 2", 'input': 's = "applepenapple", wordDict = ["apple","pen"]', 'output': 'true\nExplanation: Return true because "applepenapple" can be segmented as "apple pen apple". Note that you are allowed to reuse a dictionary word.'},
        {'title': "Example 3", 'input': 's = "catsandog", wordDict = ["cats","dog","sand","and","cat"]', 'output': 'false'}
    ],
    'constraints': [
        "1 <= s.length <= 300",
        "1 <= wordDict.length <= 1000",
        "1 <= wordDict[i].length <= 20",
        "s and wordDict[i] consist of only lowercase English letters.",
        "All the strings of wordDict are unique."
    ],
    'plain_english': "We want to know if the entire string `s` can be formed by concatenating words from `wordDict`. We define a 1D boolean array `dp` of size `n + 1`, where `dp[i]` is true if the prefix of length `i` (`s[0 until i]`) can be successfully segmented. The base case is `dp[0] = true` (an empty string is trivially segmented). For each prefix length `i` from 1 to `n`, we test every partition index `j < i`. If `dp[j]` is true AND the substring `s.substring(j, i)` exists in `wordDictSet`, then `dp[i] = true` and we can break early to the next `i`. The final answer is `dp[n]`.",
    'boundary_checks': {
        'min_base': "Single character matching word <code>s = 'a', wordDict = ['a']</code> &rarr; Returns true.",
        'best_exit': "Repeated word reuse <code>s = 'applepenapple', wordDict = ['apple','pen']</code> &rarr; Returns true.",
        'worst_scan': "Near-miss dead ends <code>s = 'catsandog', wordDict = ['cats','dog','sand','and','cat']</code> &rarr; Explores both 'cat' and 'cats' prefixes; neither reaches end with 'dog', returns false."
    },
    'mental_model': {
        'analogy_title': "Stepping Stones Across a River",
        'analogy_text': "Imagine crossing a river of length `n`. You start on stone 0 (`dp[0] = true`). Each dictionary word is a wooden plank of specific length and label. If you are standing safely on stone `j`, and there is a plank in your bag whose letters match the river bed between stone `j` and stone `i`, you can place that plank and safely step forward to stone `i` (`dp[i] = true`). If you reach stone `n`, you made it across the river!",
        'aha_moment': "We only need to know IF stone `j` is reachable, not HOW many different ways it was reached. This reduces an exponential tree of combinations down to a simple 1D boolean array.",
        'trigger': "String segmentation / prefix decomposition with dictionary lookup &rarr; 1D Boolean Dynamic Programming with HashSet.",
        'sisters': [
            {'name': "Word Break II (Return all sentences)", 'lc': "LC #140"},
            {'name': "Palindrome Partitioning", 'lc': "LC #131"},
            {'name': "Extra Characters in a String", 'lc': "LC #2707"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(2^N) Time, O(N) Space",
        'naive_text': "Pure recursion without memoization testing every prefix split, causing overlapping branches to be computed repeatedly.",
        'wasted_work': "Re-evaluating identical suffixes (e.g. testing `andog` multiple times after choosing either 'cat' or 'cats').",
        'optimal_complexity': "O(N^2 * L) Time, O(N + M * L) Space",
        'optimal_text': "1D Dynamic Programming with HashSet. Bounding prefix partition checks by `maxWordLength` further reduces time to O(N * maxWordLen * L).",
        'core_invariant': "`dp[i] == true` if and only if `s[0 until i]` can be completely partitioned into valid dictionary words.",
        'table_rows': [
            {'name': "Recursion without Memoization", 'time': "O(2^N)", 'space': "O(N)", 'note': "Exponential blowup on inputs like 'aaaaaaab' with words ['a', 'aa', ...]."},
            {'name': "★ 1D DP Array + HashSet", 'time': "O(N^2 * L)", 'space': "O(N + D)", 'note': "Calculates each prefix state once; O(1) hash lookups."}
        ]
    },
    'trace_table': {
        'input_desc': 's = "leetcode", wordDict = ["leet", "code"]',
        'columns': ["Prefix Length `i`", "Substring tested `s[j until i]`", "dp[j] condition", "Word in Dict?", "dp State `dp[0..8]`"],
        'rows': [
            ["0", "-", "-", "-", "[T, F, F, F, F, F, F, F, F]"],
            ["1 to 3", "'l', 'le', 'lee'", "dp[0]=T", "No", "All false"],
            ["4", "'leet' (j=0)", "dp[0] == T", "YES ('leet')", "[T, F, F, F, T, F, F, F, F]"],
            ["5 to 7", "Testing j=0 and j=4", "dp[0] or dp[4]", "No", "All false"],
            ["8", "'code' (j=4)", "dp[4] == T", "YES ('code')", "[T, F, F, F, T, F, F, F, T]"]
        ],
        'takeaway': "At `i = 8`, `dp[4] == true` and `s[4..8] == 'code'`, setting `dp[8] = true`. Return true."
    },
    'stepper': {
        'title': "1D DP Word Break Stepper",
        'default_input': 's="leetcode", wordDict=["leet","code"]',
        'presets': [
            {'label': "Example 1: leetcode", 'onclick': 'loadPreset("leetcode", ["leet","code"])'},
            {'label': "Example 2: applepenapple", 'onclick': 'loadPreset("applepenapple", ["apple","pen"])'},
            {'label': "Example 3: catsandog (False)", 'onclick': 'loadPreset("catsandog", ["cats","dog","sand","and","cat"])'}
        ],
        'metrics': [
            {'id': "metric-i", 'label': "Prefix Length (i)", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-sub", 'label': "Current Substring", 'color': "text-cyan-400", 'initial': "None"},
            {'id': "metric-matched", 'label': "Matched Words", 'color': "text-emerald-400", 'initial': "None"},
            {'id': "metric-dp-n", 'label': "dp[n] Result", 'color': "text-purple-400", 'initial': "Pending"}
        ],
        'visual_html': '<div id="p87-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace 1D DP table progression.',
        'pseudocode_lines': [
            "val dp = BooleanArray(s.length + 1)",
            "dp[0] = true // Base case",
            "val dict = wordDict.toHashSet()",
            "for (i in 1..s.length) {",
            "    for (j in 0 until i) {",
            "        if (dp[j] && s.substring(j, i) in dict) {",
            "            dp[i] = true; break",
            "        }",
            "    }",
            "}",
            "return dp[s.length]"
        ],
        'js_code': """
    let rawS = "leetcode";
    let rawDict = ["leet", "code"];

    function buildSteps(s, dictArr) {
      steps = [];
      const n = s.length;
      const dict = new Set(dictArr);
      const dp = new Array(n + 1).fill(false);
      dp[0] = true;

      steps.push({
        i: 0, sub: "''", dp: [...dp], matched: "-", line: 2,
        narration: `Base case: dp[0] = true (empty prefix is always segmented). String length = ${n}.`
      });

      for (let i = 1; i <= n; i++) {
        let found = false;
        for (let j = 0; j < i; j++) {
          const sub = s.substring(j, i);
          if (dp[j] && dict.has(sub)) {
            dp[i] = true;
            found = true;
            steps.push({
              i: i, sub: `s[${j}..${i}] = '${sub}'`, dp: [...dp], matched: sub, line: 6,
              narration: `Match found! dp[${j}] is true and '${sub}' is in dictionary &rarr; dp[${i}] = true.`
            });
            break;
          }
        }
        if (!found) {
          steps.push({
            i: i, sub: `s[..${i}] = '${s.substring(0, i)}'`, dp: [...dp], matched: "none", line: 4,
            narration: `Prefix of length ${i} ('${s.substring(0, i)}') cannot be formed from valid words. dp[${i}] remains false.`
          });
        }
      }

      steps.push({
        i: n, sub: "Complete", dp: [...dp], matched: dp[n] ? "Success" : "Failed", line: 11,
        narration: dp[n] ? `dp[${n}] == true! String '${s}' can be completely segmented into dictionary words.` : `dp[${n}] == false. String '${s}' cannot be segmented.`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p87-stage');
      if (!stage) return;
      document.getElementById('metric-i').textContent = step.i;
      document.getElementById('metric-sub').textContent = step.sub;
      document.getElementById('metric-matched').textContent = step.matched;
      document.getElementById('metric-dp-n').textContent = step.dp[step.dp.length - 1] ? "TRUE" : (step.i === rawS.length ? "FALSE" : "Evaluating");

      let html = '<div class="flex items-center gap-2 flex-wrap justify-center">';
      for (let i = 0; i < step.dp.length; i++) {
        const val = step.dp[i];
        const isCur = (step.i === i);
        const bg = isCur ? 'bg-amber-500 text-black border-amber-300 ring-2 ring-amber-400 font-bold' : (val ? 'bg-emerald-800/40 text-emerald-300 border-emerald-500' : 'bg-slate-800 text-slate-500 border-slate-700');
        const char = i === 0 ? '&empty;' : rawS[i - 1];
        html += `<div class="flex flex-col items-center p-2 rounded-lg border ${bg} min-w-[50px]">
                   <span class="text-xs font-mono text-slate-400">dp[${i}]</span>
                   <span class="text-sm font-bold">${val ? 'T' : 'F'}</span>
                   <span class="text-[10px] text-slate-400 mt-1">${char}</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(s, dict) {
      rawS = s;
      rawDict = dict;
      buildSteps(s, dict);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawS, rawDict);
"""
    },
    'approaches': [
        {
            'name': "1D Dynamic Programming with HashSet",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N^2 * L) Time, O(N + D) Space",
            'summary': "Boolean array `dp[i]` tracking segmentability of prefix length `i`. Convert dictionary to HashSet for O(L) substring lookups. Break early upon finding the first valid partition point `j`.",
            'kotlin_code': """class Solution {
    fun wordBreak(s: String, wordDict: List<String>): Boolean {
        val wordSet = wordDict.toHashSet()
        val n = s.length
        val dp = BooleanArray(n + 1)
        dp[0] = true // Empty string base case

        val maxWordLen = wordDict.maxOfOrNull { it.length } ?: 0

        for (i in 1..n) {
            // Pruning: only check j down to maxWordLen
            val startJ = maxOf(0, i - maxWordLen)
            for (j in startJ until i) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true
                    break
                }
            }
        }

        return dp[n]
    }
}""",
            'python_code': """class Solution:
    def wordBreak(self, s: str, wordDict: List[str]) -> bool:
        word_set = set(wordDict)
        n = len(s)
        dp = [False] * (n + 1)
        dp[0] = True
        
        max_len = max(len(w) for w in wordDict) if wordDict else 0
        
        for i in range(1, n + 1):
            start_j = max(0, i - max_len)
            for j in range(start_j, i):
                if dp[j] and s[j:i] in word_set:
                    dp[i] = True
                    break
                    
        return dp[n]""",
            'java_code': """class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        int maxLen = 0;
        for (String w : wordDict) {
            maxLen = Math.max(maxLen, w.length());
        }

        for (int i = 1; i <= n; i++) {
            int startJ = Math.max(0, i - maxLen);
            for (int j = startJ; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }
}""",
            'memory_breakdown': [
                {'component': "DP Array `dp`", 'cost': "O(N)", 'description': "Boolean array of size N + 1."},
                {'component': "HashSet `wordSet`", 'cost': "O(D * L)", 'description': "Stores D dictionary words of average length L."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun wordBreak(s: String, wordDict: List<String>): Boolean {
    // Write your solution here
    return false
}""",
        'python_boilerplate': """def wordBreak(s: str, wordDict: List[str]) -> bool:
    # Write your solution here
    return False""",
        'java_boilerplate': """public boolean wordBreak(String s, List<String> wordDict) {
    // Write your solution here
    return false;
}""",
        'js_test_runner': """
function wordBreak(s, wordDict) {
    const set = new Set(wordDict);
    const n = s.length;
    const dp = new Array(n + 1).fill(false);
    dp[0] = true;
    let maxLen = 0;
    for (let w of wordDict) maxLen = Math.max(maxLen, w.length);
    for (let i = 1; i <= n; i++) {
        const startJ = Math.max(0, i - maxLen);
        for (let j = startJ; j < i; j++) {
            if (dp[j] && set.has(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    return dp[n];
}

const tests = [
    { s: "leetcode", dict: ["leet","code"], expected: true },
    { s: "applepenapple", dict: ["apple","pen"], expected: true },
    { s: "catsandog", dict: ["cats","dog","sand","and","cat"], expected: false }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = wordBreak(t.s, t.dict);
    const ok = output === t.expected;
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${output}, Expected: ${t.expected})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's understand the input: we are given a string `s` and a list of dictionary words `wordDict`. We can reuse words any number of times. We need to determine if `s` can be completely partitioned into valid dictionary words.",
        'phase2': "A naive recursive backtracking approach would try every prefix match and recurse on the suffix. This exhibits overlapping subproblems and would blow up to exponential $O(2^N)$ time on inputs with repeated letters.",
        'phase3': "Instead, we use 1D Dynamic Programming. Let `dp[i]` be true if prefix `s[0 until i]` can be segmented. Base case: `dp[0] = true` for the empty string. For each length `i`, we look back at prior split points `j`. If `dp[j]` is true AND `s[j until i]` is in the dictionary, then `dp[i] = true`.",
        'phase4': "As an optimization, we only need to test `j` such that `i - j <= maxWordLen`, which bounds the inner loop to $O(\\text{maxWordLen})$ rather than $O(i)$. The final answer is `dp[n]`.",
        'senior_followup': "For extremely large dictionaries or strings with huge alphabets, replacing the HashSet with a Trie enables us to iterate forward through characters of `s` and break immediately when a trie node has no matching child, avoiding redundant string allocations."
    },
    'quiz': [
        {
            'question': "Why is the DP array allocated with size `n + 1` instead of `n`?",
            'options': [
                "To represent the base case of an empty string prefix at index 0",
                "Because strings in Kotlin and Java are null-terminated",
                "To prevent integer overflow in the loop index",
                "To store the boolean result in the final spare index"
            ],
            'correct_index': 0,
            'explanation': "dp[0] represents the empty prefix string, which is trivially segmentable and serves as the foundation for the first word in the sentence."
        },
        {
            'question': "How does tracking the maximum word length in `wordDict` optimize the runtime?",
            'options': [
                "It restricts the inner loop search window to at most maxWordLen rather than all i previous characters",
                "It reduces space complexity from O(N) to O(1)",
                "It eliminates the need for a HashSet",
                "It automatically sorts the dictionary strings by length"
            ],
            'correct_index': 0,
            'explanation': "Since no word in the dictionary can be longer than maxWordLen, checking j values where (i - j) > maxWordLen is guaranteed to fail, pruning unnecessary substring allocations."
        }
    ]
}

p88 = {
    'num': 88,
    'lc': 300,
    'title': "Longest Increasing Subsequence",
    'category': "Dynamic Programming",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "longest-increasing-subsequence",
    'verbatim_statement': """<p>Given an integer array <code>nums</code>, return <em>the length of the longest <strong>strictly increasing</strong> subsequence</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [10,9,2,5,3,7,101,18]', 'output': '4\nExplanation: The longest increasing subsequence is [2,3,7,101], therefore the length is 4.'},
        {'title': "Example 2", 'input': 'nums = [0,1,0,3,2,3]', 'output': '4'},
        {'title': "Example 3", 'input': 'nums = [7,7,7,7,7,7,7]', 'output': '1'}
    ],
    'constraints': [
        "1 <= nums.length <= 2500",
        "-10^4 <= nums[i] <= 10^4"
    ],
    'plain_english': "A subsequence is derived by deleting zero or more elements without changing the order of remaining elements. We want the maximum length of a strictly increasing subsequence. There are two standard approaches: (1) O(N^2) Dynamic Programming: `dp[i]` is the length of the longest increasing subsequence ending at index `i`. For each `i`, we check all `j < i` and if `nums[j] < nums[i]`, `dp[i] = max(dp[i], dp[j] + 1)`. (2) O(N log N) Patience Sorting: We maintain an array `tails` where `tails[k]` stores the smallest ending tail of all valid increasing subsequences of length `k + 1`. For each number `x`, we use binary search to find the first tail >= `x`. If `x` is larger than all tails, we append it; otherwise we replace that tail with `x`. The length of `tails` at the end is the optimal LIS length.",
    'boundary_checks': {
        'min_base': "Single element array <code>[5]</code> &rarr; Returns 1.",
        'best_exit': "All identical elements <code>[7,7,7,7]</code> &rarr; Strict inequality means LIS length is 1.",
        'worst_scan': "Strictly decreasing array <code>[5,4,3,2,1]</code> &rarr; Each element replaces index 0; returns 1."
    },
    'mental_model': {
        'analogy_title': "Patience Card Game Solitaire",
        'analogy_text': "Think of the card game Patience. When dealt cards one by one: you can place a card on top of the leftmost pile whose top card is >= current card. If no such pile exists (the card is strictly larger than all pile tops), you start a BRAND-NEW pile to the right. Because pile top cards are always in sorted order from left to right, finding which pile to place a card onto takes just $O(\\log N)$ binary search! At the end, the total number of piles created equals the length of the Longest Increasing Subsequence.",
        'aha_moment': "Replacing a pile top with a smaller number doesn't change the number of piles, but it 'greedily' opens up more opportunities for subsequent smaller numbers to extend the sequence!",
        'trigger': "Longest increasing / non-decreasing subsequence &rarr; Patience Sorting with Binary Search ($O(N \\log N)$).",
        'sisters': [
            {'name': "Russian Doll Envelopes", 'lc': "LC #354"},
            {'name': "Number of Longest Increasing Subsequence", 'lc': "LC #673"},
            {'name': "Maximum Length of Pair Chain", 'lc': "LC #646"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(N) Space",
        'naive_text': "Standard 1D DP nested loops: for each i, scan all j < i to find max dp[j].",
        'wasted_work': "Linearly re-scanning all previous elements instead of maintaining a sorted monotonic tails array.",
        'optimal_complexity': "O(N log N) Time, O(N) Space",
        'optimal_text': "Patience Sorting binary search on `tails` array.",
        'core_invariant': "Array `tails` is always strictly sorted in ascending order; `tails[k]` stores the minimum possible last element of an increasing subsequence of length `k + 1`.",
        'table_rows': [
            {'name': "1. Classical 1D DP", 'time': "O(N^2)", 'space': "O(N)", 'note': "Intuitive; easy to reconstruct actual sequence; too slow for N > 10,000."},
            {'name': "★ 2. Patience Sorting + Binary Search", 'time': "O(N log N)", 'space': "O(N)", 'note': "Optimal; uses binary search (bisect_left) on tails array."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [10, 9, 2, 5, 3, 7, 101, 18]',
        'columns': ["Element x", "Binary Search Index", "Action Taken", "Tails Array State", "LIS Length So Far"],
        'rows': [
            ["10", "0", "Initialize tails[0] = 10", "[10]", "1"],
            ["9", "0", "Replace tails[0] with 9", "[9]", "1"],
            ["2", "0", "Replace tails[0] with 2", "[2]", "1"],
            ["5", "1", "Append 5 to tails", "[2, 5]", "2"],
            ["3", "1", "Replace tails[1] with 3", "[2, 3]", "2"],
            ["7", "2", "Append 7 to tails", "[2, 3, 7]", "3"],
            ["101", "3", "Append 101 to tails", "[2, 3, 7, 101]", "4"],
            ["18", "3", "Replace tails[3] with 18", "[2, 3, 7, 18]", "4"]
        ],
        'takeaway': "Final length of `tails` is 4. LIS length = 4 (e.g. `[2, 3, 7, 18]` or `[2, 3, 7, 101]`)."
    },
    'stepper': {
        'title': "Patience Sorting LIS Stepper",
        'default_input': 'nums=[10,9,2,5,3,7,101,18]',
        'presets': [
            {'label': "Example 1: Mixed", 'onclick': 'loadPreset([10,9,2,5,3,7,101,18])'},
            {'label': "Example 2: [0,1,0,3,2,3]", 'onclick': 'loadPreset([0,1,0,3,2,3])'},
            {'label': "Identical: [7,7,7,7]", 'onclick': 'loadPreset([7,7,7,7])'}
        ],
        'metrics': [
            {'id': "metric-elem", 'label': "Inspecting Num", 'color': "text-amber-400", 'initial': "None"},
            {'id': "metric-tails", 'label': "Tails Array", 'color': "text-cyan-400", 'initial': "[]"},
            {'id': "metric-action-lis", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"},
            {'id': "metric-len", 'label': "LIS Length", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p88-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace patience sorting piles and binary search insertions.',
        'pseudocode_lines': [
            "val tails = mutableListOf<Int>()",
            "for (x in nums) {",
            "    var l = 0; var r = tails.size",
            "    while (l < r) {",
            "        val mid = (l + r) / 2",
            "        if (tails[mid] < x) l = mid + 1 else r = mid",
            "    }",
            "    if (l == tails.size) tails.add(x) else tails[l] = x",
            "}",
            "return tails.size"
        ],
        'js_code': """
    let rawNums = [10, 9, 2, 5, 3, 7, 101, 18];

    function buildSteps(nums) {
      steps = [];
      const tails = [];

      steps.push({
        num: "-", tails: [], action: "Start", len: 0, line: 1,
        narration: `Starting LIS algorithm on array [${nums.join(', ')}].`
      });

      for (let x of nums) {
        let l = 0, r = tails.length;
        while (l < r) {
          const mid = Math.floor((l + r) / 2);
          if (tails[mid] < x) l = mid + 1;
          else r = mid;
        }

        const isAppend = (l === tails.length);
        if (isAppend) {
          tails.push(x);
          steps.push({
            num: x, tails: [...tails], action: `Appended ${x} (new pile ${l})`, len: tails.length, line: 8,
            narration: `Num ${x} is strictly greater than all tails. Appended as new pile ${l + 1}. Current LIS = ${tails.length}.`
          });
        } else {
          const old = tails[l];
          tails[l] = x;
          steps.push({
            num: x, tails: [...tails], action: `Replaced pile ${l} (${old} &rarr; ${x})`, len: tails.length, line: 8,
            narration: `Num ${x} replaces pile top ${old} at index ${l}. Pile becomes greedily smaller.`
          });
        }
      }

      steps.push({
        num: "Done", tails: [...tails], action: `Completed LIS = ${tails.length}`, len: tails.length, line: 10,
        narration: `Finished! The length of longest increasing subsequence is ${tails.length}.`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p88-stage');
      if (!stage) return;
      document.getElementById('metric-elem').textContent = step.num;
      document.getElementById('metric-tails').textContent = `[${step.tails.join(', ')}]`;
      document.getElementById('metric-action-lis').textContent = step.action;
      document.getElementById('metric-len').textContent = step.len;

      let html = '<div class="flex items-center gap-3">';
      for (let i = 0; i < step.tails.length; i++) {
        html += `<div class="flex flex-col items-center p-3 rounded-xl border border-indigo-500 bg-indigo-950/40 min-w-[70px]">
                   <span class="text-xs text-indigo-300 font-mono">Pile ${i + 1}</span>
                   <span class="text-xl font-bold text-amber-400">${step.tails[i]}</span>
                   <span class="text-[10px] text-slate-400 mt-1">tail val</span>
                 </div>`;
      }
      if (step.tails.length === 0) {
        html += '<span class="text-slate-500 text-sm italic">Tails array is empty</span>';
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(nums) {
      rawNums = nums;
      buildSteps(nums);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawNums);
"""
    },
    'approaches': [
        {
            'name': "Patience Sorting with Binary Search",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N log N) Time, O(N) Space",
            'summary': "Maintain `tails` array where `tails[k]` is the smallest ending value among all increasing subsequences of length `k + 1`. Binary search for insertion point of each number.",
            'kotlin_code': """class Solution {
    fun lengthOfLIS(nums: IntArray): Int {
        val tails = IntArray(nums.size)
        var size = 0

        for (x in nums) {
            var l = 0
            var r = size
            while (l < r) {
                val mid = (l + r) ushr 1
                if (tails[mid] < x) {
                    l = mid + 1
                } else {
                    r = mid
                }
            }
            tails[l] = x
            if (l == size) size++
        }

        return size
    }
}""",
            'python_code': """import bisect

class Solution:
    def lengthOfLIS(self, nums: List[int]) -> int:
        tails = []
        for x in nums:
            # Find the leftmost index where tails[idx] >= x
            idx = bisect.bisect_left(tails, x)
            if idx == len(tails):
                tails.append(x)
            else:
                tails[idx] = x
        return len(tails)""",
            'java_code': """class Solution {
    public int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;

        for (int x : nums) {
            int l = 0, r = size;
            while (l < r) {
                int mid = (l + r) >>> 1;
                if (tails[mid] < x) {
                    l = mid + 1;
                } else {
                    r = mid;
                }
            }
            tails[l] = x;
            if (l == size) size++;
        }

        return size;
    }
}""",
            'memory_breakdown': [
                {'component': "Tails Array `tails`", 'cost': "O(N)", 'description': "Primitive array of size N holding monotonic pile tails."},
                {'component': "Auxiliary Variables", 'cost': "O(1)", 'description': "Binary search pointers l, r, mid."}
            ]
        },
        {
            'name': "Classical 1D Dynamic Programming",
            'badge': "Standard",
            'color': "blue",
            'complexity': "O(N^2) Time, O(N) Space",
            'summary': "Let `dp[i]` be the length of LIS ending at index `i`. For each `i`, scan all `j < i` and update `dp[i] = max(dp[i], dp[j] + 1)` when `nums[j] < nums[i]`.",
            'kotlin_code': """class SolutionDP {
    fun lengthOfLIS(nums: IntArray): Int {
        if (nums.isEmpty()) return 0
        val dp = IntArray(nums.size) { 1 }
        var maxLen = 1

        for (i in 1 until nums.size) {
            for (j in 0 until i) {
                if (nums[j] < nums[i]) {
                    dp[i] = maxOf(dp[i], dp[j] + 1)
                }
            }
            maxLen = maxOf(maxLen, dp[i])
        }

        return maxLen
    }
}""",
            'python_code': """class SolutionDP:
    def lengthOfLIS(self, nums: List[int]) -> int:
        if not nums:
            return 0
        dp = [1] * len(nums)
        for i in range(1, len(nums)):
            for j in range(i):
                if nums[j] < nums[i]:
                    dp[i] = max(dp[i], dp[j] + 1)
        return max(dp)""",
            'java_code': """class SolutionDP {
    public int lengthOfLIS(int[] nums) {
        if (nums.length == 0) return 0;
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int maxLen = 1;

        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLen = Math.max(maxLen, dp[i]);
        }

        return maxLen;
    }
}""",
            'memory_breakdown': [
                {'component': "DP Array `dp`", 'cost': "O(N)", 'description': "Stores LIS length ending at each index."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun lengthOfLIS(nums: IntArray): Int {
    // Write your solution here
    return 0
}""",
        'python_boilerplate': """def lengthOfLIS(nums: List[int]) -> int:
    # Write your solution here
    return 0""",
        'java_boilerplate': """public int lengthOfLIS(int[] nums) {
    // Write your solution here
    return 0;
}""",
        'js_test_runner': """
function lengthOfLIS(nums) {
    const tails = [];
    for (let x of nums) {
        let l = 0, r = tails.length;
        while (l < r) {
            const mid = Math.floor((l + r) / 2);
            if (tails[mid] < x) l = mid + 1;
            else r = mid;
        }
        if (l === tails.length) tails.push(x);
        else tails[l] = x;
    }
    return tails.length;
}

const tests = [
    { nums: [10,9,2,5,3,7,101,18], expected: 4 },
    { nums: [0,1,0,3,2,3], expected: 4 },
    { nums: [7,7,7,7,7,7,7], expected: 1 }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = lengthOfLIS(t.nums);
    const ok = output === t.expected;
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${output}, Expected: ${t.expected})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's clarify what a subsequence is: elements must appear in their relative original order, but do not need to be contiguous. We are searching for the maximum length of a strictly increasing subsequence.",
        'phase2': "The classical Dynamic Programming approach defines `dp[i]` as the length of the LIS ending at index `i`. By comparing index `i` against all preceding elements `j < i`, we get an $O(N^2)$ solution.",
        'phase3': "We can optimize this to $O(N \\log N)$ using Patience Sorting. We maintain an array `tails` where `tails[k]` stores the smallest possible tail of any increasing subsequence of length `k + 1`.",
        'phase4': "Because `tails` is strictly sorted, for every new element `x`, we can find its insertion position in $O(\\log N)$ time using binary search (`bisect_left`). If `x` is greater than all elements, we append it; otherwise, we replace the existing element at that index, greedily lowering the bar for future numbers.",
        'senior_followup': "Notice that `tails` does NOT necessarily represent an actual valid subsequence itself (e.g., indices may be shuffled), but its length is mathematically guaranteed to equal the exact LIS length."
    },
    'quiz': [
        {
            'question': "Why does replacing an element in `tails` maintain correctness even if the resulting `tails` array isn't a valid subsequence from the original array?",
            'options': [
                "Because the length of `tails` never decreases, and replacing an element only gives future elements a smaller lower bound to build upon",
                "Because `tails` is sorted backwards after replacement",
                "Because replacing elements deletes the previous subsequence",
                "Because patience sorting requires backtracking"
            ],
            'correct_index': 0,
            'explanation': "Replacing an entry `tails[i]` with a smaller value `x` keeps the length unchanged while making pile `i` more receptive to future elements, preserving the validity of the maximum length invariant."
        },
        {
            'question': "What is the time complexity difference between classical DP and Patience Sorting for N = 100,000?",
            'options': [
                "Classical DP takes ~10^10 operations (TLE), while Patience Sorting takes ~1.7 * 10^6 operations (fast, < 50ms)",
                "They have identical runtime because binary search overhead negates any gains",
                "Classical DP is faster because of CPU branch prediction",
                "Patience sorting requires O(N^2) auxiliary memory"
            ],
            'correct_index': 0,
            'explanation': "At N = 100,000, N^2 is 10 billion operations (times out in several seconds/minutes), whereas N log2(N) is only ~1.7 million operations, executing in milliseconds."
        }
    ]
}

p89 = {
    'num': 89,
    'lc': 1143,
    'title': "Longest Common Subsequence",
    'category': "Dynamic Programming",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "longest-common-subsequence",
    'verbatim_statement': """<p>Given two strings <code>text1</code> and <code>text2</code>, return <em>the length of their longest <strong>common subsequence</strong></em>. If there is no <strong>common subsequence</strong>, return <code>0</code>.</p>
<p>A <strong>subsequence</strong> of a string is a new string generated from the original string with some characters (can be none) deleted without changing the relative order of the remaining characters.</p>
<ul>
	<li>For example, <code>"ace"</code> is a subsequence of <code>"abcde"</code>.</li>
</ul>
<p>A <strong>common subsequence</strong> of two strings is a subsequence that is common to both strings.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'text1 = "abcde", text2 = "ace"', 'output': '3\nExplanation: The longest common subsequence is "ace" and its length is 3.'},
        {'title': "Example 2", 'input': 'text1 = "abc", text2 = "abc"', 'output': '3\nExplanation: The longest common subsequence is "abc" and its length is 3.'},
        {'title': "Example 3", 'input': 'text1 = "abc", text2 = "def"', 'output': '0\nExplanation: There is no such common subsequence, so the result is 0.'}
    ],
    'constraints': [
        "1 <= text1.length, text2.length <= 1000",
        "text1 and text2 consist of only lowercase English characters."
    ],
    'plain_english': "We want to find the length of the longest subsequence shared between two strings `text1` (length $M$) and `text2` (length $N$). We build a 2D table `dp` of size `(M + 1) x (N + 1)`, where `dp[i][j]` is the LCS of prefixes `text1[0 until i]` and `text2[0 until j]`. The base cases are `dp[0][j] = 0` and `dp[i][0] = 0`. For each cell `(i, j)`: if characters match (`text1[i - 1] == text2[j - 1]`), we extend the diagonal result: `dp[i][j] = dp[i - 1][j - 1] + 1`. If they don't match, we take the best choice of skipping one character: `dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])`. Space can be optimized to $O(\\min(M, N))$ using two rolling 1D rows.",
    'boundary_checks': {
        'min_base': "No common letters <code>text1 = 'abc', text2 = 'def'</code> &rarr; Returns 0.",
        'best_exit': "Identical strings <code>text1 = 'abc', text2 = 'abc'</code> &rarr; Diagonal match everywhere; returns 3.",
        'worst_scan': "Strings with interleaved matches <code>text1 = 'ezupkr', text2 = 'ubmrapg'</code> &rarr; Returns 2 ('up')."
    },
    'mental_model': {
        'analogy_title': "The DNA Alignment Grid",
        'analogy_text': "Imagine comparing two strands of DNA written along the top and left of a grid. Whenever two matching nucleotides line up at coordinate `(i, j)`, you get a bonus point and step diagonally down-right (`dp[i-1][j-1] + 1`). When they don't match, you can either delete a nucleotide from strand 1 (taking the value from above) or strand 2 (taking the value from the left), whichever achieved a higher score so far.",
        'aha_moment': "When `text1[i-1] == text2[j-1]`, it is mathematically proven that matching them greedily is never worse than skipping one. Hence `1 + dp[i-1][j-1]` is strictly optimal for matching characters.",
        'trigger': "Longest common subsequence between two sequences / edit distance foundation &rarr; 2D Grid DP with diagonal vs max(top, left) transitions.",
        'sisters': [
            {'name': "Edit Distance", 'lc': "LC #72"},
            {'name': "Distinct Subsequences", 'lc': "LC #115"},
            {'name': "Delete Operation for Two Strings", 'lc': "LC #583"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(2^(M+N)) Time, O(M+N) Space",
        'naive_text': "Recursive branching: if matching, advance both; if differing, branch into two recursive calls (advance i OR advance j).",
        'wasted_work': "Re-evaluating identical prefix coordinate pairs `(i, j)` exponentially many times.",
        'optimal_complexity': "O(M * N) Time, O(min(M, N)) Space",
        'optimal_text': "2D DP with two 1D rolling arrays `prev` and `curr`.",
        'core_invariant': "`dp[i][j]` is the exact length of the longest common subsequence between `text1[0 until i]` and `text2[0 until j]`.",
        'table_rows': [
            {'name': "Recursion without Memoization", 'time': "O(2^(M+N))", 'space': "O(M+N)", 'note': "Exponential branching."},
            {'name': "2D DP Grid", 'time': "O(M * N)", 'space': "O(M * N)", 'note': "Full table allows backtracking to reconstruct the actual string."},
            {'name': "★ 1D Rolling Row DP", 'time': "O(M * N)", 'space': "O(min(M, N))", 'note': "Memory optimized; only requires two rows (previous & current)."}
        ]
    },
    'trace_table': {
        'input_desc': 'text1 = "abcde", text2 = "ace"',
        'columns': ["Cell (i, j)", "text1[i-1]", "text2[j-1]", "Match?", "DP Transition", "dp[i][j] Value"],
        'rows': [
            ["(1, 1)", "'a'", "'a'", "YES", "dp[0][0] + 1 = 0 + 1", "1"],
            ["(2, 1)", "'b'", "'a'", "No", "max(dp[1][1], dp[2][0]) = max(1, 0)", "1"],
            ["(3, 2)", "'c'", "'c'", "YES", "dp[2][1] + 1 = 1 + 1", "2"],
            ["(4, 2)", "'d'", "'c'", "No", "max(dp[3][2], dp[4][1]) = max(2, 1)", "2"],
            ["(5, 3)", "'e'", "'e'", "YES", "dp[4][2] + 1 = 2 + 1", "3"]
        ],
        'takeaway': "Characters 'a', 'c', and 'e' match along the diagonal progression, yielding final answer 3."
    },
    'stepper': {
        'title': "2D LCS Grid Traversal Stepper",
        'default_input': 'text1="abcde", text2="ace"',
        'presets': [
            {'label': "Example 1: 'abcde' & 'ace'", 'onclick': 'loadPreset("abcde", "ace")'},
            {'label': "Example 2: 'abc' & 'abc'", 'onclick': 'loadPreset("abc", "abc")'},
            {'label': "Disjoint: 'abc' & 'def'", 'onclick': 'loadPreset("abc", "def")'}
        ],
        'metrics': [
            {'id': "metric-cell", 'label': "Current Cell (i, j)", 'color': "text-amber-400", 'initial': "(0, 0)"},
            {'id': "metric-chars", 'label': "Chars (t1[i-1], t2[j-1])", 'color': "text-cyan-400", 'initial': "None"},
            {'id': "metric-lcs-val", 'label': "LCS Value", 'color': "text-emerald-400", 'initial': "0"},
            {'id': "metric-trans", 'label': "Rule Applied", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="p89-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace 2D dynamic programming grid cell computation.',
        'pseudocode_lines': [
            "val dp = Array(m + 1) { IntArray(n + 1) }",
            "for (i in 1..m) {",
            "    for (j in 1..n) {",
            "        if (text1[i - 1] == text2[j - 1]) {",
            "            dp[i][j] = dp[i - 1][j - 1] + 1",
            "        } else {",
            "            dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])",
            "        }",
            "    }",
            "}",
            "return dp[m][n]"
        ],
        'js_code': """
    let rawT1 = "abcde";
    let rawT2 = "ace";

    function buildSteps(t1, t2) {
      steps = [];
      const m = t1.length;
      const n = t2.length;
      const dp = Array.from({length: m + 1}, () => new Array(n + 1).fill(0));

      steps.push({
        i: 0, j: 0, c1: "-", c2: "-", val: 0, trans: "Base Cases (0s)",
        grid: dp.map(row => [...row]), line: 1,
        narration: `Initialized (${m+1} x ${n+1}) grid with 0s. Base rows/cols are 0.`
      });

      for (let i = 1; i <= m; i++) {
        for (let j = 1; j <= n; j++) {
          const match = (t1[i - 1] === t2[j - 1]);
          if (match) {
            dp[i][j] = dp[i - 1][j - 1] + 1;
            steps.push({
              i: i, j: j, c1: t1[i - 1], c2: t2[j - 1], val: dp[i][j],
              trans: `Match: 1 + diag(${dp[i-1][j-1]})`,
              grid: dp.map(row => [...row]), line: 5,
              narration: `'${t1[i-1]}' matches '${t2[j-1]}'! dp[${i}][${j}] = dp[${i-1}][${j-1}] + 1 = ${dp[i][j]}.`
            });
          } else {
            dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            steps.push({
              i: i, j: j, c1: t1[i - 1], c2: t2[j - 1], val: dp[i][j],
              trans: `Mismatch: max(top, left)`,
              grid: dp.map(row => [...row]), line: 7,
              narration: `'${t1[i-1]}' != '${t2[j-1]}'. dp[${i}][${j}] = max(${dp[i-1][j]}, ${dp[i][j-1]}) = ${dp[i][j]}.`
            });
          }
        }
      }

      steps.push({
        i: m, j: n, c1: "Done", c2: "Done", val: dp[m][n],
        trans: "Final LCS Found",
        grid: dp.map(row => [...row]), line: 11,
        narration: `Calculation complete! Longest Common Subsequence length = ${dp[m][n]}.`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p89-stage');
      if (!stage) return;
      document.getElementById('metric-cell').textContent = `(${step.i}, ${step.j})`;
      document.getElementById('metric-chars').textContent = step.c1 !== '-' ? `'${step.c1}', '${step.c2}'` : "None";
      document.getElementById('metric-lcs-val').textContent = step.val;
      document.getElementById('metric-trans').textContent = step.trans;

      let html = '<table class="border-collapse text-center text-xs font-mono">';
      html += '<tr><th class="p-1 text-slate-500"></th><th class="p-1 text-slate-500">&empty;</th>';
      for (let ch of rawT2) html += `<th class="p-1 text-cyan-400 font-bold">${ch}</th>`;
      html += '</tr>';

      for (let i = 0; i < step.grid.length; i++) {
        const rowLabel = i === 0 ? '&empty;' : rawT1[i - 1];
        html += `<tr><th class="p-1 text-amber-400 font-bold">${rowLabel}</th>`;
        for (let j = 0; j < step.grid[i].length; j++) {
          const isCur = (step.i === i && step.j === j);
          const cellVal = step.grid[i][j];
          const bg = isCur ? 'bg-amber-500 text-black font-bold ring-2 ring-amber-300' : (cellVal > 0 ? 'bg-emerald-950/60 text-emerald-300' : 'bg-slate-900 text-slate-600');
          html += `<td class="p-2 border border-slate-700 ${bg}">${cellVal}</td>`;
        }
        html += '</tr>';
      }
      html += '</table>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(t1, t2) {
      rawT1 = t1;
      rawT2 = t2;
      buildSteps(t1, t2);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawT1, rawT2);
"""
    },
    'approaches': [
        {
            'name': "Space-Optimized 1D Rolling Row DP",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(M * N) Time, O(min(M, N)) Space",
            'summary': "Because `dp[i][j]` only references the current row and previous row, maintain two 1D rows `prev` and `curr`. Swap rows after each character of `text1`.",
            'kotlin_code': """class Solution {
    fun longestCommonSubsequence(text1: String, text2: String): Int {
        // Ensure text2 is the shorter string to minimize space
        var s1 = text1
        var s2 = text2
        if (s1.length < s2.length) {
            val tmp = s1; s1 = s2; s2 = tmp
        }

        val m = s1.length
        val n = s2.length
        var prev = IntArray(n + 1)
        var curr = IntArray(n + 1)

        for (i in 1..m) {
            for (j in 1..n) {
                if (s1[i - 1] == s2[j - 1]) {
                    curr[j] = prev[j - 1] + 1
                } else {
                    curr[j] = maxOf(prev[j], curr[j - 1])
                }
            }
            // Swap rows
            val temp = prev
            prev = curr
            curr = temp
        }

        return prev[n]
    }
}""",
            'python_code': """class Solution:
    def longestCommonSubsequence(self, text1: str, text2: str) -> int:
        if len(text1) < len(text2):
            text1, text2 = text2, text1
            
        m, n = len(text1), len(text2)
        prev = [0] * (n + 1)
        curr = [0] * (n + 1)
        
        for i in range(1, m + 1):
            for j in range(1, n + 1):
                if text1[i - 1] == text2[j - 1]:
                    curr[j] = prev[j - 1] + 1
                else:
                    curr[j] = max(prev[j], curr[j - 1])
            prev, curr = curr, [0] * (n + 1)
            
        return prev[n]""",
            'java_code': """class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        if (text1.length() < text2.length()) {
            String temp = text1;
            text1 = text2;
            text2 = temp;
        }

        int m = text1.length();
        int n = text2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }
}""",
            'memory_breakdown': [
                {'component': "Rolling DP Rows (`prev`, `curr`)", 'cost': "O(min(M, N))", 'description': "Two integer arrays of length min(M, N) + 1."},
                {'component': "Loop Variables", 'cost': "O(1)", 'description': "Indices i, j."}
            ]
        },
        {
            'name': "Full 2D DP Table (Reconstructable)",
            'badge': "Standard",
            'color': "blue",
            'complexity': "O(M * N) Time, O(M * N) Space",
            'summary': "Full `(M + 1) x (N + 1)` matrix. Straightforward implementation; facilitates backtracking from `dp[M][N]` to reconstruct the actual common subsequence characters.",
            'kotlin_code': """class Solution2D {
    fun longestCommonSubsequence(text1: String, text2: String): Int {
        val m = text1.length
        val n = text2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 1..m) {
            for (j in 1..n) {
                if (text1[i - 1] == text2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1
                } else {
                    dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }

        return dp[m][n]
    }
}""",
            'python_code': """class Solution2D:
    def longestCommonSubsequence(self, text1: str, text2: str) -> int:
        m, n = len(text1), len(text2)
        dp = [[0] * (n + 1) for _ in range(m + 1)]
        
        for i in range(1, m + 1):
            for j in range(1, n + 1):
                if text1[i - 1] == text2[j - 1]:
                    dp[i][j] = dp[i - 1][j - 1] + 1
                else:
                    dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])
                    
        return dp[m][n]""",
            'java_code': """class Solution2D {
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }
}""",
            'memory_breakdown': [
                {'component': "Full 2D Matrix `dp`", 'cost': "O(M * N)", 'description': "Entire grid preserved for reconstruction."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun longestCommonSubsequence(text1: String, text2: String): Int {
    // Write your solution here
    return 0
}""",
        'python_boilerplate': """def longestCommonSubsequence(text1: str, text2: str) -> int:
    # Write your solution here
    return 0""",
        'java_boilerplate': """public int longestCommonSubsequence(String text1, String text2) {
    // Write your solution here
    return 0;
}""",
        'js_test_runner': """
function longestCommonSubsequence(text1, text2) {
    const m = text1.length, n = text2.length;
    let prev = new Array(n + 1).fill(0);
    let curr = new Array(n + 1).fill(0);
    for (let i = 1; i <= m; i++) {
        for (let j = 1; j <= n; j++) {
            if (text1[i - 1] === text2[j - 1]) {
                curr[j] = prev[j - 1] + 1;
            } else {
                curr[j] = Math.max(prev[j], curr[j - 1]);
            }
        }
        prev = [...curr];
        curr.fill(0);
    }
    return prev[n];
}

const tests = [
    { t1: "abcde", t2: "ace", expected: 3 },
    { t1: "abc", t2: "abc", expected: 3 },
    { t1: "abc", t2: "def", expected: 0 }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = longestCommonSubsequence(t.t1, t.t2);
    const ok = output === t.expected;
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${output}, Expected: ${t.expected})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's establish the problem requirements: given two strings `text1` and `text2`, we need to find the length of the longest subsequence present in both. Subsequences maintain order but do not need to be contiguous.",
        'phase2': "A naive recursion would branch on each character pair: if they match, advance both; if they differ, branch into skipping a char in text1 or text2. This yields $O(2^{M+N})$ worst-case time.",
        'phase3': "We can formulate this as a 2D dynamic program: `dp[i][j]` is the LCS of `text1[0..i-1]` and `text2[0..j-1]`. If `text1[i-1] == text2[j-1]`, we extend the diagonal: `1 + dp[i-1][j-1]`. Otherwise, we take `max(dp[i-1][j], dp[i][j-1])`.",
        'phase4': "Notice each row only depends on the row directly above it. By assigning the shorter string to the columns and keeping just two rolling 1D arrays (`prev` and `curr`), we reduce the auxiliary space from $O(M \\times N)$ down to $O(\\min(M, N))$.",
        'senior_followup': "If the interviewer asks to return the actual string instead of just the length, we would retain the full 2D matrix and backtrack from cell `(m, n)` up to `(0, 0)`, appending matching characters to a string builder and reversing at the end."
    },
    'quiz': [
        {
            'question': "Why is `1 + dp[i-1][j-1]` strictly optimal when `text1[i-1] == text2[j-1]` without needing to check `max(dp[i-1][j], dp[i][j-1])`?",
            'options': [
                "Matching common characters as early as possible is mathematically guaranteed to be >= skipping either character",
                "Because diagonal moves cost zero memory in modern CPU caches",
                "Because strings are guaranteed to have identical characters only at identical index positions",
                "Because DP tables must be monotonic"
            ],
            'correct_index': 0,
            'explanation': "A standard greedy property of LCS: pairing matching characters `text1[i-1]` and `text2[j-1]` leaves the maximal possible prefix space for both strings, which can never be worse than discarding one of them."
        },
        {
            'question': "How can space complexity be reduced from O(M * N) to O(min(M, N))?",
            'options': [
                "By maintaining only two 1D rows (prev and curr) and aligning the shorter string along the columns",
                "By sorting both strings before running dynamic programming",
                "By computing LCS using a binary search tree",
                "By hashing both strings into 64-bit integers"
            ],
            'correct_index': 0,
            'explanation': "Since calculating row i only references cells from row i - 1 and the current row i, only two 1D rows are needed at any time, bounded by the length of the shorter string."
        }
    ]
}
