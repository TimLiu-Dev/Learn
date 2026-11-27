"""
2027 Mid-Tech Curriculum Definitions for Lessons 0122 to 0126 + Batch Renderer:
- #0122: LC #17 Letter Combinations of a Phone Number (Pass 2A Medium - Multi-Way Digit Backtracking)
- #0123: LC #721 Accounts Merge (Pass 2B Medium - Union-Find Equivalence Clustering)
- #0124: LC #36 Valid Sudoku (Pass 2A Medium - GCA Q3 Row/Col/Sub-Box Bitmask Validation)
- #0125: LC #692 Top K Frequent Words (Pass 2B Medium - Custom Comparator Heap Tie-Breaking)
- #0126: Cloud Storage System (Pass 2B Medium - 4-Stage CodeSignal Progressive ICA)
"""

import sys
sys.path.append("scripts")
from data_p117_p121 import p117, p118, p119, p120, p121
from template_engine_v2 import render_lesson_v2


p122 = {
    "num": 122,
    "lc": 17,
    "title": "Letter Combinations of a Phone Number",
    "category": "Backtracking",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "combinations-of-a-phone-number",
    "nc_url": "https://neetcode.io/problems/combinations-of-a-phone-number/question?list=allNC",
    "prev_file": "0121-lc40-combination-sum-ii.html",
    "next_file": "0123-lc721-accounts-merge.html",
    "method_name": "letterCombinations",
    "verbatim_statement": (
        "Given a string containing digits from <code>2-9</code> inclusive, return all possible letter combinations "
        "that the number could represent. Return the answer in <strong>any order</strong>.<br><br>"
        "Keypad mapping: <code>2:abc, 3:def, 4:ghi, 5:jkl, 6:mno, 7:pqrs, 8:tuv, 9:wxyz</code>."
    ),
    "examples": [
        {"title": "Example 1", "input": 'digits = "23"', "output": '["ad","ae","af","bd","be","bf","cd","ce","cf"]'},
        {"title": "Example 2", "input": 'digits = ""', "output": "[]"},
        {"title": "Example 3", "input": 'digits = "2"', "output": '["a","b","c"]'}
    ],
    "constraints": [
        "0 <= digits.length <= 4",
        "digits[i] is a digit in the range ['2', '9']."
    ],
    "plain_english": (
        "First guard `digits.isEmpty() -> emptyList()` (NOT `[\"\"]`!). Then at position `idx` from `0` to `digits.length - 1`, "
        "branch across the 3 or 4 letters mapped to `digits[idx]`, append to a single mutable `StringBuilder` / buffer, recurse, and pop."
    ),
    "boundary_checks": {
        "min_base": 'digits = "" -> Must return [] (empty list), NOT [""] (list containing one empty string)!',
        "best_exit": 'digits = "2" -> Depth 1 yields ["a", "b", "c"].',
        "worst_scan": 'digits = "7979" (all 4-letter keys pqrs/wxyz) -> 4^4 = 256 combinations, each length 4.'
    },
    "mental_model": {
        "analogy_title": "Combination Lock Wheels Spinning in Sequence",
        "analogy_text": (
            "Think of a luggage lock where Wheel 0 has letters ['a','b','c'] (for digit '2') and Wheel 1 has ['d','e','f'] (for digit '3'). "
            "Instead of subset include/exclude choices, every wheel MUST pick exactly one letter before advancing to `idx + 1`. "
            "Using one shared `CharArray(digits.length)` overwritten at `buf[idx] = ch` avoids string concatenation garbage AND doesn't even need an explicit delete step!"
        ),
        "aha_moment": "Guard `if (digits.isEmpty()) return emptyList()` first! Then overwrite `buf[idx] = ch` at each depth `idx` for zero-garbage O(N · 4^N) generation.",
        "trigger": "Cartesian product generation across variable-choice slots, keypad/T9 expansion, and multi-slot configuration combinations.",
        "sisters": [
            {"name": "Subsets", "lc": "#78"},
            {"name": "Generate Parentheses", "lc": "#22"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N · 4^N) with Intermediate String Garbage",
        "naive_text": "Repeatedly build new immutable strings (`prefix + ch`) or allocate full intermediate lists at every digit level.",
        "wasted_work": "Immutable `prefix + ch` allocates a fresh String at every internal tree node instead of only at the 4^N leaves.",
        "optimal_complexity": "O(N · 4^N) Time, O(N) Auxiliary Stack/Buffer Space",
        "optimal_text": "Pre-allocate one fixed `CharArray(digits.length)` (or `StringBuilder`), write `buf[idx] = ch`, recurse `dfs(idx + 1)`, and materialize `String(buf)` only at `idx == digits.length`.",
        "core_invariant": "At recursion depth idx, buf[0..idx-1] holds the chosen prefix for digits[0..idx-1].",
        "table_rows": [
            {"name": "Iterative BFS Queue Expansion", "time": "O(N · 4^N)", "space": "O(4^N) queue", "note": "Clean iterative alternative; holds partial strings in queue."},
            {"name": "Optimal: Fixed-Buffer DFS Backtracking", "time": "O(N · 4^N)", "space": "O(N) auxiliary", "note": "★ Allocates strings ONLY at leaf combinations."}
        ]
    },
    "trace_table": {
        "input_desc": 'digits = "23" (2->"abc", 3->"def")',
        "columns": ["Call", "Digit -> Letters", "buf State", "Output Appended"],
        "rows": [
            ["dfs(0)", "'2' -> \"abc\"", "['a', _]", "recurse dfs(1)"],
            ["  dfs(1)", "'3' -> \"def\"", "['a','d'], ['a','e'], ['a','f']", '"ad", "ae", "af"'],
            ["dfs(0)", "'2' -> 'b'", "['b', _] -> d,e,f", '"bd", "be", "bf"'],
            ["dfs(0)", "'2' -> 'c'", "['c', _] -> d,e,f", '"cd", "ce", "cf"']
        ],
        "takeaway": "Because `buf[idx]` is overwritten by the next loop iteration at the same depth, fixed-size `CharArray` needs no explicit pop!"
    },
    "stepper": {
        "title": "Multi-Way Keypad Decision Tree Stepper",
        "default_input": '"23"',
        "presets": [
            {"label": 'digits = "23"', "onclick": 'loadPreset("23")'},
            {"label": 'digits = "79"', "onclick": 'loadPreset("79")'},
            {"label": 'digits = "" (Empty Guard)', "onclick": 'loadPreset("")'}
        ],
        "metrics": [
            {"id": "m-digits", "label": "Digits", "color": "text-amber-400", "initial": '"23"'},
            {"id": "m-depth", "label": "Depth idx", "color": "text-cyan-400", "initial": "0"},
            {"id": "m-buf", "label": "Active Buffer", "color": "text-purple-400", "initial": '""'},
            {"id": "m-cnt", "label": "Outputs", "color": "text-emerald-400", "initial": "0"}
        ],
        "visual_html": '<div id="p122-stage" class="flex flex-col items-center gap-3 w-full"></div>',
        "initial_narration": "Step through each digit wheel to build Cartesian keypad strings.",
        "pseudocode_lines": [
            "if (digits.isEmpty()) return emptyList()",
            "val pad = arrayOf(\"\", \"\", \"abc\", \"def\", \"ghi\", \"jkl\", \"mno\", \"pqrs\", \"tuv\", \"wxyz\")",
            "fun dfs(idx: Int) {",
            "  if (idx == digits.length) { res.add(sb.toString()); return }",
            "  for (ch in pad[digits[idx] - '0']) { sb.append(ch); dfs(idx + 1); sb.deleteCharAt(sb.length - 1) }",
            "}"
        ],
        "js_code": """
    const PAD = ["", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"];
    function buildSteps(d) {
      const digits = typeof d === 'string' ? d.replace(/[^2-9]/g, '') : "23";
      steps = [];
      if (!digits.length) {
        steps.push({ digits: '""', idx: 0, buf: '', out: [], line: 1, narration: 'digits is empty -> guard returns [] immediately (NOT [""])!' });
        return;
      }
      let out = [];
      steps.push({ digits: `"${digits}"`, idx: 0, buf: '', out: [], line: 2, narration: `Starting DFS on digits="${digits}".` });
      function dfs(idx, buf) {
        if (steps.length > 32) return;
        if (idx === digits.length) {
          out.push(buf);
          steps.push({ digits: `"${digits}"`, idx, buf, out: out.slice(), line: 4, narration: `Reached leaf idx=${idx} -> emitted "${buf}".` });
          return;
        }
        const letters = PAD[Number(digits[idx])];
        for (const ch of letters) {
          steps.push({ digits: `"${digits}"`, idx, buf: buf + ch, out: out.slice(), line: 5, narration: `idx=${idx} ('${digits[idx]}'): chose '${ch}' -> prefix="${buf + ch}".` });
          dfs(idx + 1, buf + ch);
        }
      }
      dfs(0, '');
    }
    function renderStage(step) {
      const el = document.getElementById('p122-stage');
      if (!el) return;
      const badges = step.out.length ? step.out.map(s => `<span class="px-2 py-1 rounded bg-emerald-950/60 border border-emerald-500/40 font-mono text-xs text-emerald-300">"${s}"</span>`).join('') : '<span class="text-xs font-mono text-slate-500">Building first leaf...</span>';
      el.innerHTML = `<div class="px-4 py-2 rounded-xl bg-slate-900 border border-cyan-500/40 font-mono text-sm text-cyan-300">Buffer: "${step.buf}"</div><div class="flex flex-wrap gap-1.5 justify-center max-w-xl">${badges}</div>`;
    }
    function loadPreset(d) { buildSteps(d); stepperReset(); }
        """,
        "js_init_call": 'buildSteps("23");',
        "js_update_metrics": """
        document.getElementById('m-digits').textContent = step.digits;
        document.getElementById('m-depth').textContent = step.idx;
        document.getElementById('m-buf').textContent = `"${step.buf}"`;
        document.getElementById('m-cnt').textContent = step.out.length;
        """
    },
    "approaches": [
        {
            "name": "Iterative Queue / Cartesian Expansion",
            "badge": "Iterative",
            "desc": "Expands partial prefixes level-by-level for each digit.",
            "time": "O(N · 4^N)",
            "space": "O(4^N)",
            "code_kotlin": """class Solution {
    fun letterCombinations(digits: String): List<String> {
        if (digits.isEmpty()) return emptyList()
        val pad = arrayOf("", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz")
        var cur = listOf("")
        for (i in 0 until digits.length) {
            val letters = pad[digits[i] - '0']
            val next = mutableListOf<String>()
            for (prefix in cur) {
                for (j in 0 until letters.length) {
                    next.add(prefix + letters[j])
                }
            }
            cur = next
        }
        return cur
    }
}""",
            "code_python": """class Solution:
    def letterCombinations(self, digits: str) -> list[str]:
        if not digits:
            return []
        pad = ["", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"]
        cur = [""]
        for d in digits:
            letters = pad[ord(d) - 48]
            nxt = []
            for prefix in cur:
                for ch in letters:
                    nxt.append(prefix + ch)
            cur = nxt
        return cur""",
            "code_java": """import java.util.*;

class Solution {
    public List<String> letterCombinations(String digits) {
        if (digits.isEmpty()) return new ArrayList<>();
        String[] pad = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        List<String> cur = new ArrayList<>();
        cur.add("");
        for (int i = 0; i < digits.length(); i++) {
            String letters = pad[digits.charAt(i) - '0'];
            List<String> next = new ArrayList<>();
            for (String prefix : cur) {
                for (int j = 0; j < letters.length(); j++) {
                    next.add(prefix + letters.charAt(j));
                }
            }
            cur = next;
        }
        return cur;
    }
}"""
        },
        {
            "name": "Optimal: Mutable Buffer DFS Backtracking",
            "badge": "Optimal",
            "desc": "Uses O(N) auxiliary stack + StringBuilder/list buffer and materializes strings only at depth N.",
            "time": "O(N · 4^N)",
            "space": "O(N) aux",
            "code_kotlin": """class Solution {
    fun letterCombinations(digits: String): List<String> {
        if (digits.isEmpty()) return emptyList()
        val pad = arrayOf("", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz")
        val res = mutableListOf<String>()
        val sb = StringBuilder()

        fun dfs(idx: Int) {
            if (idx == digits.length) {
                res.add(sb.toString())
                return
            }
            val letters = pad[digits[idx] - '0']
            for (i in 0 until letters.length) {
                sb.append(letters[i])
                dfs(idx + 1)
                sb.deleteCharAt(sb.length - 1)
            }
        }

        dfs(0)
        return res
    }
}""",
            "code_python": """class Solution:
    def letterCombinations(self, digits: str) -> list[str]:
        if not digits:
            return []
        pad = ["", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"]
        res = []
        path = []

        def dfs(idx: int) -> None:
            if idx == len(digits):
                res.append("".join(path))
                return
            for ch in pad[ord(digits[idx]) - 48]:
                path.append(ch)
                dfs(idx + 1)
                path.pop()

        dfs(0)
        return res""",
            "code_java": """import java.util.*;

class Solution {
    private static final String[] PAD = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    public List<String> letterCombinations(String digits) {
        List<String> res = new ArrayList<>();
        if (digits.isEmpty()) return res;
        dfs(0, digits, new StringBuilder(), res);
        return res;
    }

    private void dfs(int idx, String digits, StringBuilder sb, List<String> res) {
        if (idx == digits.length()) {
            res.add(sb.toString());
            return;
        }
        String letters = PAD[digits.charAt(idx) - '0'];
        for (int i = 0; i < letters.length(); i++) {
            sb.append(letters.charAt(i));
            dfs(idx + 1, digits, sb, res);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}"""
        }
    ],
    "test_cases": [
        {"input": ["23"], "expected": ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"], "unordered": True},
        {"input": [""], "expected": [], "unordered": True},
        {"input": ["2"], "expected": ["a", "b", "c"], "unordered": True},
        {"input": ["7"], "expected": ["p", "q", "r", "s"], "unordered": True}
    ],
    "spoken_script": {
        "phase1": "First I check the empty-string edge case: for `digits = \"\"`, we must return an empty list `[]`, not `[\"\"]`. Also note digits `'7'` (`pqrs`) and `'9'` (`wxyz`) map to 4 letters rather than 3.",
        "phase2": "Since each of the N digits branches up to 4 ways and building each output string of length N takes O(N), optimal time is tightly `O(N * 4^N)`, with `O(N)` auxiliary recursion + buffer space.",
        "phase3": "I index a static 10-element keypad array via `digits[idx] - '0'`, append each letter to a single `StringBuilder`, recurse `idx + 1`, and `deleteCharAt(sb.length - 1)` on return.",
        "phase4": "Tracing `digits = \"\"` returns `[]` on line 1; tracing `\"2\"` visits `'a'`, `'b'`, `'c'` and returns `[\"a\",\"b\",\"c\"]`.",
        "mid_followup": "In real T9 predictive text where only valid dictionary words should be returned (pruning dead prefixes like `'qz...'` immediately!), pass a `TrieNode` (`#208`) alongside `idx`: only recurse on `ch` when `trieNode.children[ch] != null`!"
    },
    "quiz_questions": [
        {
            "category": "Empty Input Bug (`[\"\"]` vs `[]`)",
            "prompt": "What does `letterCombinations(\"\")` return if you forget `if (digits.isEmpty()) return emptyList()` before calling `dfs(0)`?",
            "codeSnippet": "fun letterCombinations(digits: String): List<String> {\n    val res = mutableListOf<String>()\n    val sb = StringBuilder()\n    fun dfs(idx: Int) {\n        if (idx == digits.length) { res.add(sb.toString()); return }\n        ...\n    }\n    dfs(0)\n    return res\n}",
            "options": [
                {"id": "A", "text": "It returns [\"\"] (a 1-element list containing an empty string) instead of [].", "isCorrect": True, "whyRight": "idx == 0 == digits.length triggers immediately on dfs(0), appending sb.toString() == \"\"!"},
                {"id": "B", "text": "It throws StringIndexOutOfBoundsException at index 0.", "isCorrect": False, "whyWrong": "The base case returns before indexing digits[0]."},
                {"id": "C", "text": "It returns null instead of a List<String>.", "isCorrect": False, "whyWrong": "res is a non-null MutableList."},
                {"id": "D", "text": "It enters infinite recursion at idx = 0.", "isCorrect": False, "whyWrong": "dfs(0) hits the base case and returns immediately."}
            ]
        },
        {
            "category": "Tight Time Complexity",
            "prompt": "Why is the worst-case time complexity `O(N * 4^N)` rather than `O(3^N)`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Digits '7' and '9' have 4 letters (4^N leaves) and copying each length-N string costs O(N).", "isCorrect": True, "whyRight": "For digits = '7979', there are 4^N leaves and each sb.toString() copies N characters."},
                {"id": "B", "text": "Every digit from 2 through 9 maps to exactly four letters on standard keypads.", "isCorrect": False, "whyWrong": "Digits 2,3,4,5,6,8 map to 3 letters; only 7 and 9 map to 4."},
                {"id": "C", "text": "StringBuilder.deleteCharAt(sb.length - 1) shifts all N characters on every pop.", "isCorrect": False, "whyWrong": "Removing the last character of StringBuilder is O(1)."},
                {"id": "D", "text": "Sorting the output list at the end dominates the exponential recursion.", "isCorrect": False, "whyWrong": "No sorting is needed (andkeypad order is already lexicographical!)."}
            ]
        },
        {
            "category": "T9 Dictionary Pruning Follow-Up",
            "prompt": "If `N = 12` and you only want combinations present in a 50,000-word dictionary, what prunes dead prefixes best?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Walk a Trie in lockstep with idx and only recurse when node.children[ch] is non-null.", "isCorrect": True, "whyRight": "Trie prefix gating kills dead branches at depth 2–3 instead of exploring all 4^12 = 16.7M leaves!"},
                {"id": "B", "text": "Generate all 4^12 strings first, then filter them against a HashSet.", "isCorrect": False, "whyWrong": "Generating 16.7M strings wastes massive time and memory when only ~5 words match."},
                {"id": "C", "text": "Sort the dictionary by word length and run Koko-style binary search on answer.", "isCorrect": False, "whyWrong": "Word validity is not a numeric monotonic predicate."},
                {"id": "D", "text": "Use Union-Find to merge digits '2' through '9' into connected components.", "isCorrect": False, "whyWrong": "Union-Find solves equivalence classes, not prefix filtering."}
            ]
        },
        {
            "category": "Keypad Mapping Bug",
            "prompt": "Which keypad digits map to **4** letters instead of **3**, causing classic off-by-one mapping bugs if computed via `'a' + (d - 2) * 3`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Digit '7' (pqrs) and digit '9' (wxyz) map to four letters, shifting '8' (tuv) by +1.", "isCorrect": True, "whyRight": "Naive `(d - 2) * 3` maps '8' to 'stu' instead of 'tuv'! A hardcoded 10-string lookup table prevents arithmetic bugs."},
                {"id": "B", "text": "Digit '2' (abcd) and digit '3' (efgh) map to four letters on phone keypads.", "isCorrect": False, "whyWrong": "2->abc and 3->def have 3 letters."},
                {"id": "C", "text": "Digit '0' and digit '1' map to four punctuation letters in LeetCode #17.", "isCorrect": False, "whyWrong": "Constraints restrict digits strictly to '2'..'9'."},
                {"id": "D", "text": "Only digit '9' has four letters while '7' has three letters.", "isCorrect": False, "whyWrong": "26 letters across 8 keys = six 3-letter keys (18) + two 4-letter keys (8): '7' and '9'."}
            ]
        }
    ]
}


p123 = {
    "num": 123,
    "lc": 721,
    "title": "Accounts Merge",
    "category": "2D Grid Traversals & Graphs",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "accounts-merge",
    "nc_url": "https://neetcode.io/problems/accounts-merge/question?list=allNC",
    "prev_file": "0122-lc17-letter-combinations-of-a-phone-number.html",
    "next_file": "0124-lc36-valid-sudoku.html",
    "method_name": "accountsMerge",
    "verbatim_statement": (
        "Given a list of <code>accounts</code> where <code>accounts[i][0]</code> is a person's <strong>name</strong> and the rest of the elements "
        "are <strong>emails</strong> of that account, merge accounts belonging to the same person.<br><br>"
        "Two accounts definitely belong to the same person if there is some common email to both accounts. Note that two people may share the same name! "
        "Return each merged account as <code>[name, ...sortedUniqueEmails]</code> in any account order."
    ),
    "examples": [
        {
            "title": "Example 1",
            "input": '[["John","johnsmith@mail.com","john_newyork@mail.com"],["John","johnsmith@mail.com","john00@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]]',
            "output": '[["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]]'
        }
    ],
    "constraints": [
        "1 <= accounts.length <= 1000",
        "2 <= accounts[i].length <= 10",
        "accounts[i][0] consists of English letters; emails are valid lowercase ASCII."
    ],
    "plain_english": (
        "Names are NOT unique IDs—two different Johns can exist! Treat account indices `0..N-1` as Union-Find nodes: "
        "map each `email -> firstOwnerAccountIdx`, and whenever an email was already seen under `prevIdx`, `union(i, prevIdx)`. "
        "Finally group all unique emails by `find(ownerIdx)`, sort each group's emails, and prepend `accounts[root][0]`."
    ),
    "boundary_checks": {
        "min_base": 'Single account [["Ada", "b@x.com", "a@x.com", "a@x.com"]] -> Deduplicates and sorts emails to [["Ada", "a@x.com", "b@x.com"]].',
        "best_exit": "Two accounts sharing the same name 'John' with ZERO shared emails -> Remain two separate 'John' records!",
        "worst_scan": "Transitive chain A-B, B-C, C-D -> Union-Find merges all into one root component in O(E α(N)) + O(E log E) sort."
    },
    "mental_model": {
        "analogy_title": "Customer Identity Resolution via Shared Email Bridges",
        "analogy_text": (
            "In real Fintech/Retail identity systems, display names ('John') collide constantly, whereas a verified email is a unique bridge. "
            "Number the account rows `0..N-1`. As you read every email in row `i`, look it up in `emailToAcc`: if nobody claimed it yet, "
            "record `emailToAcc[email] = i`; if row `prev` already claimed it, `union(i, prev)` fuses their entire identity clusters! "
            "Once all bridges are unioned, every email in `emailToAcc` jumps straight to its cluster root `find(accIdx)`."
        ),
        "aha_moment": "Union-Find over account indices `0..N-1` (bridged via `emailToFirstAccountIdx` map) avoids string-parent maps AND preserves each cluster's display name at `accounts[root][0]`!",
        "trigger": "Entity deduplication, transitive equivalence classes (A~B and B~C => A~C), user profile merging.",
        "sisters": [
            {"name": "Redundant Connection", "lc": "#684"},
            {"name": "Number of Provinces", "lc": "#547"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N² · E) Repeated Pairwise Merging",
        "naive_text": "Repeatedly scan all pairs of accounts for intersecting email sets and merge until no pair overlaps.",
        "wasted_work": "A chain of accounts merging at the very last link forces multiple quadratic passes over all sets.",
        "optimal_complexity": "O(E · log E) Time, O(E) Space (where E = total emails)",
        "optimal_text": "Union-Find over account indices `0..N-1` in `O(E · α(N))` + group unique emails by `find(idx)` + sort each component's emails in `O(E log E)`.",
        "core_invariant": "Two emails e1, e2 belong to the same person iff find(emailToAcc[e1]) == find(emailToAcc[e2]).",
        "table_rows": [
            {"name": "Alt: Bipartite Email Graph + DFS", "time": "O(E log E)", "space": "O(E)", "note": "Valid and clean; builds adjacency sets between emails."},
            {"name": "Optimal: Account-Index Union-Find", "time": "O(E log E)", "space": "O(N + E)", "note": "★ Integer parent[N] array + single emailToAcc map; minimal memory."}
        ]
    },
    "trace_table": {
        "input_desc": '0:["John","a","b"], 1:["John","a","c"], 2:["John","d"]',
        "columns": ["Step", "Email Seen", "emailToAcc", "parent[]", "Note"],
        "rows": [
            ["Row 0", "'a', 'b'", "{a:0, b:0}", "[0, 1, 2]", "Row 0 claims a and b"],
            ["Row 1", "'a' (hit 0!), 'c'", "{a:0, b:0, c:1}", "[0, 0, 2]", "'a' already at 0 -> union(1, 0)!"],
            ["Row 2", "'d'", "{a:0, b:0, c:1, d:2}", "[0, 0, 2]", "Same name 'John', disjoint email 'd'"],
            ["Group", "find(owner)", "root 0: [a,b,c], root 2: [d]", "[0, 0, 2]", 'Output 2 John accounts: ["John","a","b","c"], ["John","d"]']
        ],
        "takeaway": "Iterating `emailToAcc.keys` during grouping automatically deduplicates repeated emails!"
    },
    "stepper": {
        "title": "Account-Index Union-Find Identity Merger",
        "default_input": "official",
        "presets": [
            {"label": "Official 4-Account Example", "onclick": 'loadPreset("ex1")'},
            {"label": "Transitive Chain (0-1-2)", "onclick": 'loadPreset("chain")'}
        ],
        "metrics": [
            {"id": "m-acc", "label": "Account Row", "color": "text-amber-400", "initial": "-"},
            {"id": "m-emails", "label": "Unique Emails", "color": "text-cyan-400", "initial": "0"},
            {"id": "m-comps", "label": "Merged People", "color": "text-purple-400", "initial": "4"},
            {"id": "m-state", "label": "Phase", "color": "text-emerald-400", "initial": "Init"}
        ],
        "visual_html": '<div id="p123-stage" class="flex flex-wrap justify-center gap-3 w-full"></div>',
        "initial_narration": "Union account indices whenever an email collides in emailToAcc.",
        "pseudocode_lines": [
            "val parent = IntArray(n) { it }; val emailToAcc = HashMap<String, Int>()",
            "for (i in accounts.indices) for (j in 1 until accounts[i].size) {",
            "  val em = accounts[i][j]; val prev = emailToAcc[em]",
            "  if (prev == null) emailToAcc[em] = i else union(i, prev)",
            "}",
            "group emailToAcc.keys by find(owner), sort emails, prepend accounts[root][0]"
        ],
        "js_code": """
    function buildSteps(mode) {
      const accs = mode === 'chain'
        ? [["Ada","e1","e2"], ["Ada","e3","e4"], ["Ada","e2","e3"]]
        : [["John","js@m","jny@m"], ["John","js@m","j00@m"], ["Mary","mary@m"], ["John","bravo@m"]];
      steps = [];
      const n = accs.length, parent = accs.map((_, i) => i), emMap = {};
      const find = x => { while (parent[x] !== x) x = parent[x]; return x; };
      let comps = n;
      steps.push({ parent: parent.slice(), accs, acc: '-', emails: 0, comps, phase: 'Init', groups: null, line: 1, narration: `${n} accounts initialized as disjoint roots.` });
      for (let i = 0; i < n; i++) {
        for (let j = 1; j < accs[i].length; j++) {
          const em = accs[i][j];
          if (emMap[em] === undefined) emMap[em] = i;
          else {
            const ra = find(i), rb = find(emMap[em]);
            if (ra !== rb) { parent[ra] = rb; comps--; }
          }
        }
        steps.push({ parent: parent.slice(), accs, acc: `#${i} (${accs[i][0]})`, emails: Object.keys(emMap).length, comps, phase: 'Unioning', groups: null, line: 4, narration: `Processed account #${i}; active components=${comps}.` });
      }
      const grp = {};
      Object.keys(emMap).sort().forEach(em => {
        const r = find(emMap[em]);
        (grp[r] = grp[r] || [accs[r][0]]).push(em);
      });
      steps.push({ parent: parent.slice(), accs, acc: 'Done', emails: Object.keys(emMap).length, comps, phase: 'Merged & Sorted', groups: Object.values(grp), line: 6, narration: `Grouped & sorted emails into ${comps} final merged accounts.` });
    }
    function renderStage(step) {
      const el = document.getElementById('p123-stage');
      if (!el) return;
      if (step.groups) {
        el.innerHTML = step.groups.map(g => `<div class="p-2.5 rounded-xl bg-emerald-950/40 border border-emerald-500/40 font-mono text-xs"><div class="text-emerald-300 font-bold">${g[0]}</div><div class="text-slate-300 text-[11px]">${g.slice(1).join(', ')}</div></div>`).join('');
        return;
      }
      el.innerHTML = step.accs.map((a, i) => `<div class="p-2.5 rounded-xl bg-slate-900 border border-slate-700 font-mono text-xs"><div class="text-amber-300 font-bold">#${i} ${a[0]} (parent=${step.parent[i]})</div><div class="text-slate-400 text-[11px]">${a.slice(1).join(', ')}</div></div>`).join('');
    }
    function loadPreset(m) { buildSteps(m); stepperReset(); }
        """,
        "js_init_call": 'buildSteps("ex1");',
        "js_update_metrics": """
        document.getElementById('m-acc').textContent = step.acc;
        document.getElementById('m-emails').textContent = step.emails;
        document.getElementById('m-comps').textContent = step.comps;
        document.getElementById('m-state').textContent = step.phase;
        """
    },
    "approaches": [
        {
            "name": "Email Graph Adjacency + Connected-Component DFS",
            "badge": "Graph DFS",
            "desc": "Builds star edges from each account's first email to its other emails, then runs DFS over unvisited emails.",
            "time": "O(E log E)",
            "space": "O(E)",
            "code_kotlin": """import java.util.ArrayDeque

class Solution {
    fun accountsMerge(accounts: List<List<String>>): List<List<String>> {
        val adj = HashMap<String, MutableList<String>>()
        val emailToName = HashMap<String, String>()
        for (acc in accounts) {
            val name = acc[0]
            val first = acc[1]
            for (j in 1 until acc.size) {
                val em = acc[j]
                emailToName[em] = name
                adj.getOrPut(first) { mutableListOf() }.add(em)
                adj.getOrPut(em) { mutableListOf() }.add(first)
            }
        }
        val visited = HashSet<String>()
        val res = mutableListOf<List<String>>()
        for (em in emailToName.keys) {
            if (!visited.add(em)) continue
            val comp = mutableListOf<String>()
            val q = ArrayDeque<String>()
            q.addLast(em)
            while (q.isNotEmpty()) {
                val cur = q.removeFirst()
                comp.add(cur)
                for (nb in adj[cur] ?: emptyList()) {
                    if (visited.add(nb)) q.addLast(nb)
                }
            }
            comp.sort()
            val row = mutableListOf(emailToName[em]!!)
            row.addAll(comp)
            res.add(row)
        }
        return res
    }
}""",
            "code_python": """from collections import defaultdict, deque

class Solution:
    def accountsMerge(self, accounts: list[list[str]]) -> list[list[str]]:
        adj = defaultdict(list)
        email_to_name = {}
        for acc in accounts:
            name = acc[0]
            first = acc[1]
            for em in acc[1:]:
                email_to_name[em] = name
                adj[first].append(em)
                adj[em].append(first)
        visited = set()
        res = []
        for em in email_to_name.keys():
            if em in visited:
                continue
            visited.add(em)
            comp = []
            q = deque([em])
            while q:
                cur = q.popleft()
                comp.append(cur)
                for nb in adj[cur]:
                    if nb not in visited:
                        visited.add(nb)
                        q.append(nb)
            comp.sort()
            row = [email_to_name[em]]
            row.extend(comp)
            res.append(row)
        return res""",
            "code_java": """import java.util.*;

class Solution {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String, List<String>> adj = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        for (List<String> acc : accounts) {
            String name = acc.get(0);
            String first = acc.get(1);
            for (int j = 1; j < acc.size(); j++) {
                String em = acc.get(j);
                emailToName.put(em, name);
                adj.computeIfAbsent(first, k -> new ArrayList<>()).add(em);
                adj.computeIfAbsent(em, k -> new ArrayList<>()).add(first);
            }
        }
        Set<String> visited = new HashSet<>();
        List<List<String>> res = new ArrayList<>();
        for (String em : emailToName.keySet()) {
            if (!visited.add(em)) continue;
            List<String> comp = new ArrayList<>();
            Deque<String> q = new ArrayDeque<>();
            q.addLast(em);
            while (!q.isEmpty()) {
                String cur = q.removeFirst();
                comp.add(cur);
                for (String nb : adj.getOrDefault(cur, Collections.emptyList())) {
                    if (visited.add(nb)) q.addLast(nb);
                }
            }
            Collections.sort(comp);
            List<String> row = new ArrayList<>();
            row.add(emailToName.get(em));
            row.addAll(comp);
            res.add(row);
        }
        return res;
    }
}"""
        },
        {
            "name": "Optimal: Account-Index Union-Find (Path Compression + Rank)",
            "badge": "Optimal",
            "desc": "Unions integer account indices 0..N-1 via emailToAcc, then groups & sorts unique emails per root.",
            "time": "O(E log E)",
            "space": "O(N + E)",
            "code_kotlin": """class Solution {
    fun accountsMerge(accounts: List<List<String>>): List<List<String>> {
        val n = accounts.size
        val parent = IntArray(n) { it }
        val rank = IntArray(n)

        fun find(x: Int): Int {
            var cur = x
            while (parent[cur] != cur) {
                parent[cur] = parent[parent[cur]]
                cur = parent[cur]
            }
            return cur
        }

        fun union(a: Int, b: Int) {
            val ra = find(a)
            val rb = find(b)
            if (ra == rb) return
            if (rank[ra] < rank[rb]) {
                parent[ra] = rb
            } else if (rank[ra] > rank[rb]) {
                parent[rb] = ra
            } else {
                parent[rb] = ra
                rank[ra]++
            }
        }

        val emailToAcc = HashMap<String, Int>()
        for (i in 0 until n) {
            val acc = accounts[i]
            for (j in 1 until acc.size) {
                val em = acc[j]
                val prev = emailToAcc[em]
                if (prev == null) {
                    emailToAcc[em] = i
                } else {
                    union(i, prev)
                }
            }
        }

        val groups = HashMap<Int, MutableList<String>>()
        for (em in emailToAcc.keys) {
            val root = find(emailToAcc[em]!!)
            groups.getOrPut(root) { mutableListOf() }.add(em)
        }

        val res = mutableListOf<List<String>>()
        for (root in groups.keys) {
            val emails = groups[root]!!
            emails.sort()
            val merged = mutableListOf(accounts[root][0])
            merged.addAll(emails)
            res.add(merged)
        }
        return res
    }
}""",
            "code_python": """from collections import defaultdict

class Solution:
    def accountsMerge(self, accounts: list[list[str]]) -> list[list[str]]:
        n = len(accounts)
        parent = list(range(n))
        rank = [0] * n

        def find(x: int) -> int:
            while parent[x] != x:
                parent[x] = parent[parent[x]]
                x = parent[x]
            return x

        def union(a: int, b: int) -> None:
            ra, rb = find(a), find(b)
            if ra == rb:
                return
            if rank[ra] < rank[rb]:
                parent[ra] = rb
            elif rank[ra] > rank[rb]:
                parent[rb] = ra
            else:
                parent[rb] = ra
                rank[ra] += 1

        email_to_acc = {}
        for i in range(n):
            for em in accounts[i][1:]:
                if em not in email_to_acc:
                    email_to_acc[em] = i
                else:
                    union(i, email_to_acc[em])

        groups = defaultdict(list)
        for em, idx in email_to_acc.items():
            groups[find(idx)].append(em)

        res = []
        for root, emails in groups.items():
            emails.sort()
            merged = [accounts[root][0]]
            merged.extend(emails)
            res.append(merged)
        return res""",
            "code_java": """import java.util.*;

class Solution {
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        int n = accounts.size();
        int[] parent = new int[n];
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        Map<String, Integer> emailToAcc = new HashMap<>();
        for (int i = 0; i < n; i++) {
            List<String> acc = accounts.get(i);
            for (int j = 1; j < acc.size(); j++) {
                String em = acc.get(j);
                Integer prev = emailToAcc.get(em);
                if (prev == null) {
                    emailToAcc.put(em, i);
                } else {
                    union(i, prev, parent, rank);
                }
            }
        }

        Map<Integer, List<String>> groups = new HashMap<>();
        for (Map.Entry<String, Integer> e : emailToAcc.entrySet()) {
            int root = find(e.getValue(), parent);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(e.getKey());
        }

        List<List<String>> res = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> g : groups.entrySet()) {
            List<String> emails = g.getValue();
            Collections.sort(emails);
            List<String> row = new ArrayList<>();
            row.add(accounts.get(g.getKey()).get(0));
            row.addAll(emails);
            res.add(row);
        }
        return res;
    }

    private int find(int x, int[] parent) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    private void union(int a, int b, int[] parent, int[] rank) {
        int ra = find(a, parent), rb = find(b, parent);
        if (ra == rb) return;
        if (rank[ra] < rank[rb]) parent[ra] = rb;
        else if (rank[ra] > rank[rb]) parent[rb] = ra;
        else { parent[rb] = ra; rank[ra]++; }
    }
}"""
        }
    ],
    "test_cases": [
        {
            "input": [[
                ["John", "johnsmith@mail.com", "john_newyork@mail.com"],
                ["John", "johnsmith@mail.com", "john00@mail.com"],
                ["Mary", "mary@mail.com"],
                ["John", "johnnybravo@mail.com"]
            ]],
            "expected": [
                ["John", "john00@mail.com", "john_newyork@mail.com", "johnsmith@mail.com"],
                ["Mary", "mary@mail.com"],
                ["John", "johnnybravo@mail.com"]
            ],
            "accountsMerge": True
        },
        {
            "input": [[
                ["Alex", "a@m.com", "b@m.com"],
                ["Alex", "c@m.com", "d@m.com"],
                ["Alex", "b@m.com", "c@m.com"]
            ]],
            "expected": [["Alex", "a@m.com", "b@m.com", "c@m.com", "d@m.com"]],
            "accountsMerge": True
        },
        {
            "input": [[["Ada", "z@x.com", "a@x.com", "a@x.com"]]],
            "expected": [["Ada", "a@x.com", "z@x.com"]],
            "accountsMerge": True
        }
    ],
    "spoken_script": {
        "phase1": "First I note that display names (`accounts[i][0]`) are NOT unique identifiers—two distinct Johns stay separate unless connected directly or transitively by shared emails, and each merged record's emails must be deduplicated and sorted.",
        "phase2": "This is dynamic connected components over equivalence bridges. Running Union-Find over integer account indices `0..N-1` (rather than string parents) uses a tiny `IntArray(N)` and keeps `accounts[root][0]` immediately accessible.",
        "phase3": "Pass 1 scans every email `em` in account `i`: if `em` isn't in `emailToAcc`, store `i`; otherwise `union(i, emailToAcc[em])`. Pass 2 groups `emailToAcc.keys` by `find(ownerIdx)` (which automatically deduplicates emails!), sorts each list, and prepends `accounts[root][0]`.",
        "phase4": "Tracing the transitive case `[a,b]`, `[c,d]`, `[b,c]`: account 2 unions `2->0` on `b` and `2->1` on `c`, collapsing all 3 accounts under one root.",
        "mid_followup": "In real Fintech/SaaS identity graphs where new login events stream in continuously, Union-Find excels because merging two clusters on a newly verified email/phone link is `O(α(N))` incremental without rebuilding an adjacency graph!"
    },
    "quiz_questions": [
        {
            "category": "Identity Collision Bug (Keying by Name)",
            "prompt": "Why does grouping emails into `HashMap<String, TreeSet<String>>` keyed by `account[0]` (name) fail?",
            "codeSnippet": "val byName = HashMap<String, TreeSet<String>>()\nfor (acc in accounts) {\n    byName.getOrPut(acc[0]) { TreeSet() }.addAll(acc.drop(1)) // <-- BUG!\n}",
            "options": [
                {"id": "A", "text": "Two completely different users named 'John' with zero shared emails get wrongly merged together.", "isCorrect": True, "whyRight": "Names are display labels, not primary keys; only shared emails establish identity equivalence."},
                {"id": "B", "text": "TreeSet fails to sort email addresses containing underscores or digits.", "isCorrect": False, "whyWrong": "String natural order sorts ASCII characters including '_' and '0'-'9'."},
                {"id": "C", "text": "acc.drop(1) deletes the last email instead of the account name.", "isCorrect": False, "whyWrong": "drop(1) skips index 0 (the name)."},
                {"id": "D", "text": "HashMap cannot store TreeSet values without throwing ClassCastException.", "isCorrect": False, "whyWrong": "HashMap values can be any object."}
            ]
        },
        {
            "category": "Union-Find Grouping Bug (`parent[idx]` vs `find(idx)`)",
            "prompt": "During Pass 2 grouping, what breaks if you group emails by `parent[idx]` instead of `find(idx)`?",
            "codeSnippet": "for ((em, idx) in emailToAcc) {\n    val root = parent[idx] // <-- BUG: missing find(idx)!\n    groups.getOrPut(root) { mutableListOf() }.add(em)\n}",
            "options": [
                {"id": "A", "text": "Uncompressed intermediate parent pointers split one transitive person across multiple output rows.", "isCorrect": True, "whyRight": "Path compression is lazy: `parent[idx]` may point to a mid-level node whose parent changed in a later union!"},
                {"id": "B", "text": "parent[idx] throws IndexOutOfBoundsException whenever two accounts merge.", "isCorrect": False, "whyWrong": "parent[idx] is always in 0..N-1."},
                {"id": "C", "text": "Calling find(idx) reverses alphabetical order of the emails.", "isCorrect": False, "whyWrong": "find(idx) only resolves the integer component root."},
                {"id": "D", "text": "Duplicate emails within a single account are duplicated in emailToAcc.keys.", "isCorrect": False, "whyWrong": "Map keys are always unique."}
            ]
        },
        {
            "category": "Graph vs Union-Find Edge Count",
            "prompt": "In the Graph DFS approach, why do we connect `acc[1]` to `acc[2..k]` (a star) instead of all-pairs cliques within an account?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "A star of k - 1 edges preserves exact connectivity while avoiding O(k^2) clique edges.", "isCorrect": True, "whyRight": "Connected components only care about reachability, so linking every email in the account to `acc[1]` suffices in O(k) edges!"},
                {"id": "B", "text": "All-pairs edges would disconnect accounts that contain only two emails.", "isCorrect": False, "whyWrong": "For 2 emails, star and clique both have 1 edge."},
                {"id": "C", "text": "Star graphs automatically sort neighbor lists in lexicographical order.", "isCorrect": False, "whyWrong": "Component emails still require an explicit sort."},
                {"id": "D", "text": "BFS/DFS cannot traverse graphs that contain triangles or cycles.", "isCorrect": False, "whyWrong": "A visited set handles cycles cleanly."}
            ]
        },
        {
            "category": "Asymptotic Bottleneck",
            "prompt": "With `E` total emails across `N` accounts, which step dominates optimal Union-Find runtime?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Sorting each merged component's email list dominates at O(E log E) time.", "isCorrect": True, "whyRight": "Union-Find operations take near-linear O(E * alpha(N)), whereas sorting up to E emails takes O(E log E)."},
                {"id": "B", "text": "Initializing IntArray(N) dominates at O(N^2) quadratic time.", "isCorrect": False, "whyWrong": "Initializing parent[N] is O(N)."},
                {"id": "C", "text": "Path-compressed find(x) degrades to O(N^2) per lookup.", "isCorrect": False, "whyWrong": "Path compression + union by rank runs in O(alpha(N)) amortized."},
                {"id": "D", "text": "Prepending the account owner's name costs O(E^2) string copies.", "isCorrect": False, "whyWrong": "Building `[name] + sortedEmails` is O(E) across all components."}
            ]
        }
    ]
}


p124 = {
    "num": 124,
    "lc": 36,
    "title": "Valid Sudoku",
    "category": "Arrays, Strings & Hashing",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "valid-sudoku",
    "nc_url": "https://neetcode.io/problems/valid-sudoku/question?list=allNC",
    "prev_file": "0123-lc721-accounts-merge.html",
    "next_file": "0125-lc692-top-k-frequent-words.html",
    "method_name": "isValidSudoku",
    "verbatim_statement": (
        "Determine if a <code>9 x 9</code> Sudoku board is valid. Only the filled cells need to be validated according to the following rules:<br>"
        "1. Each row must contain the digits <code>1-9</code> without repetition.<br>"
        "2. Each column must contain the digits <code>1-9</code> without repetition.<br>"
        "3. Each of the nine <code>3 x 3</code> sub-boxes of the grid must contain the digits <code>1-9</code> without repetition.<br><br>"
        "<strong>Note:</strong> Empty cells are <code>'.'</code>. A partially filled Sudoku board can be valid without necessarily being solvable!"
    ),
    "examples": [
        {"title": "Example 1", "input": "Standard partial 9x9 board with unique digits in every row, col, and 3x3 box", "output": "true"},
        {"title": "Example 2", "input": "Top-left cell changed from '5' to '8', colliding with '8' in row 0 / box 0", "output": "false"}
    ],
    "constraints": [
        "board.length == 9, board[i].length == 9",
        "board[i][j] is a digit '1'-'9' or '.'."
    ],
    "plain_english": (
        "Do NOT run a backtracking Sudoku solver! Simply scan all 81 cells once, skip `'.'`, map `(r, c)` to its `3 x 3` box index "
        "`box = (r / 3) * 3 + (c / 3)`, and verify digit `d` hasn't already appeared in `rows[r]`, `cols[c]`, or `boxes[box]`."
    ),
    "boundary_checks": {
        "min_base": "Board of all '.' empty cells -> Every cell is skipped, returns true.",
        "best_exit": "Duplicate digit in row 0 (e.g., two '8's) -> Detected immediately in the first few cells, returns false.",
        "worst_scan": "All 81 cells checked once -> Fixed 81 iterations using three 9-int bitmask arrays."
    },
    "mental_model": {
        "analogy_title": "Three Simultaneous Attendance Sheets + The `(r/3)*3 + (c/3)` Coordinate Formula",
        "analogy_text": (
            "Every filled cell `(r, c)` belongs to three clubs at once: Row `r`, Column `c`, and one of the nine `3 x 3` neighborhoods. "
            "Dividing `r / 3` gives the macro-row `0..2` and `c / 3` gives the macro-col `0..2`; flattening a `3 x 3` macro-grid into `0..8` "
            "uses standard 2D-to-1D row-major math `macroRow * 3 + macroCol = (r / 3) * 3 + (c / 3)`! "
            "Even better: each attendance sheet only tracks digits `0..8`, which fits in a single 9-bit integer bitmask (`1 shl d`)!"
        ),
        "aha_moment": "`boxIdx = (r / 3) * 3 + (c / 3)` + three 9-element integer bitmasks (`rows[9]`, `cols[9]`, `boxes[9]`) validates the entire board in 1 pass and 108 bytes!",
        "trigger": "CodeSignal GCA Q3 sub-grid indexing, Latin-square / board validation, and multi-axis uniqueness checks.",
        "sisters": [
            {"name": "Rotate Image", "lc": "#48"},
            {"name": "Spiral Matrix", "lc": "#54"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "3 Separate Passes + String Allocation",
        "naive_text": "Write 3 separate nested loop blocks (one for rows, one for cols, one for 4-nested 3x3 boxes) or build string keys per cell.",
        "wasted_work": "Four-deep nested box loops are bug-prone under GCA time pressure, and string concatenation allocates up to 243 strings.",
        "optimal_complexity": "O(1) Fixed 81-Cell Pass (O(N²) for N×N), O(1) Space",
        "optimal_text": "One `r in 0..8, c in 0..8` loop checking bit `bit = 1 shl (ch - '1')` against `rows[r]`, `cols[c]`, and `boxes[(r / 3) * 3 + (c / 3)]`.",
        "core_invariant": "Bit d of rows[r], cols[c], and boxes[b] is 1 iff digit (d + 1) has already been seen in that row, column, or 3x3 box.",
        "table_rows": [
            {"name": "3 Separate Loops (Rows, Cols, Boxes)", "time": "3 × 81 ops", "space": "O(1)", "note": "Verbose 4-loop sub-box indexing invites off-by-one bugs."},
            {"name": "Single Pass + Boolean[9][9] Tables", "time": "81 ops", "space": "243 bools", "note": "Super readable alternative in interviews."},
            {"name": "Optimal: Single Pass + 9-Int Bitmasks", "time": "81 ops", "space": "27 ints", "note": "★ Zero object allocation, instant bitwise OR/AND check."}
        ]
    },
    "trace_table": {
        "input_desc": "Checking cells (0,0)='8', (0,4)='7', (2,2)='8' (same box 0!)",
        "columns": ["Cell (r, c)", "Val", "box = (r/3)*3 + (c/3)", "Bit Check", "Action"],
        "rows": [
            ["(0, 0)", "'8'", "(0)*3 + 0 = 0", "rows[0], cols[0], boxes[0] bit 7 == 0", "Set bit 7 on r0, c0, b0"],
            ["(0, 4)", "'7'", "(0)*3 + 1 = 1", "rows[0], cols[4], boxes[1] bit 6 == 0", "Set bit 6 on r0, c4, b1"],
            ["(2, 2)", "'8'", "(0)*3 + 0 = 0", "boxes[0] already has bit 7 ('8')!", "Collision in Box 0 -> return false!"]
        ],
        "takeaway": "Cells `(0,0)` and `(2,2)` are in different rows and columns, yet both map to `box = 0` and collide immediately!"
    },
    "stepper": {
        "title": "Sub-Box `(r/3)*3 + (c/3)` & Bitmask Inspector",
        "default_input": "box_collision",
        "presets": [
            {"label": "Box 0 Collision ((0,0) & (2,2))", "onclick": 'loadPreset("box")'},
            {"label": "Valid Partial Board", "onclick": 'loadPreset("valid")'}
        ],
        "metrics": [
            {"id": "m-cell", "label": "Cell (r, c)", "color": "text-cyan-400", "initial": "(0, 0)"},
            {"id": "m-box", "label": "Box Index", "color": "text-amber-400", "initial": "0"},
            {"id": "m-val", "label": "Digit", "color": "text-purple-400", "initial": "'8'"},
            {"id": "m-res", "label": "Board Status", "color": "text-emerald-400", "initial": "Valid so far"}
        ],
        "visual_html": '<div id="p124-stage" class="flex flex-col items-center gap-2"></div>',
        "initial_narration": "Inspect how (r, c) maps into 3x3 sub-box index (r/3)*3 + (c/3).",
        "pseudocode_lines": [
            "val rows = IntArray(9); val cols = IntArray(9); val boxes = IntArray(9)",
            "for (r in 0 until 9) for (c in 0 until 9) {",
            "  if (board[r][c] == '.') continue",
            "  val bit = 1 shl (board[r][c] - '1'); val b = (r / 3) * 3 + (c / 3)",
            "  if ((rows[r] and bit) != 0 || (cols[c] and bit) != 0 || (boxes[b] and bit) != 0) return false",
            "  rows[r] = rows[r] or bit; cols[c] = cols[c] or bit; boxes[b] = boxes[b] or bit",
            "}",
            "return true"
        ],
        "js_code": """
    function buildSteps(mode) {
      const cells = mode === 'valid'
        ? [[0,0,'5'], [0,1,'3'], [0,4,'7'], [1,0,'6'], [4,4,'5'], [8,8,'9']]
        : [[0,0,'8'], [0,4,'7'], [1,3,'1'], [2,2,'8']];
      steps = [];
      let rows = Array(9).fill(0), cols = Array(9).fill(0), boxes = Array(9).fill(0);
      for (const [r, c, ch] of cells) {
        const bit = 1 << (Number(ch) - 1);
        const b = Math.floor(r / 3) * 3 + Math.floor(c / 3);
        const bad = (rows[r] & bit) || (cols[c] & bit) || (boxes[b] & bit);
        steps.push({ r, c, ch, b, bad: Boolean(bad), status: bad ? 'INVALID (Collision!)' : 'Valid', line: bad ? 5 : 6, narration: `Cell (${r},${c})='${ch}' -> Row ${r}, Col ${c}, Box ${b}: ${bad ? 'Bit already set -> return false!' : 'Recorded cleanly.'}` });
        if (bad) return;
        rows[r] |= bit; cols[c] |= bit; boxes[b] |= bit;
      }
      steps.push({ r: 8, c: 8, ch: '-', b: 8, bad: false, status: 'Valid Board (true)', line: 8, narration: 'All filled cells passed row, column, and 3x3 box checks -> return true!' });
    }
    function renderStage(step) {
      const el = document.getElementById('p124-stage');
      if (!el) return;
      let html = '<div class="grid grid-cols-3 gap-2">';
      for (let b = 0; b < 9; b++) {
        const active = b === step.b;
        const cls = active ? (step.bad ? 'border-rose-500 bg-rose-950/60 text-rose-200 ring-2 ring-rose-500/40' : 'border-emerald-400 bg-emerald-950/50 text-emerald-200 ring-2 ring-emerald-400/30') : 'border-slate-800 bg-slate-900 text-slate-500';
        html += `<div class="w-20 h-14 rounded-xl border flex flex-col items-center justify-center font-mono text-xs ${cls}"><span class="text-[10px]">Box ${b}</span><b>${active ? `(${step.r},${step.c})='${step.ch}'` : '3x3'}</b></div>`;
      }
      html += '</div>';
      el.innerHTML = html;
    }
    function loadPreset(m) { buildSteps(m); stepperReset(); }
        """,
        "js_init_call": 'buildSteps("box");',
        "js_update_metrics": """
        document.getElementById('m-cell').textContent = `(${step.r}, ${step.c})`;
        document.getElementById('m-box').textContent = step.b;
        document.getElementById('m-val').textContent = `'${step.ch}'`;
        document.getElementById('m-res').textContent = step.status;
        """
    },
    "approaches": [
        {
            "name": "Single Pass with 2D Boolean Tables",
            "badge": "Clean",
            "desc": "Tracks rows[9][9], cols[9][9], and boxes[9][9] boolean flags in one 81-cell pass.",
            "time": "O(1) (81 cells)",
            "space": "O(1) (243 bools)",
            "code_kotlin": """class Solution {
    fun isValidSudoku(board: Array<CharArray>): Boolean {
        val rows = Array(9) { BooleanArray(9) }
        val cols = Array(9) { BooleanArray(9) }
        val boxes = Array(9) { BooleanArray(9) }
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                val ch = board[r][c]
                if (ch == '.') continue
                val d = ch - '1'
                val b = (r / 3) * 3 + (c / 3)
                if (rows[r][d] || cols[c][d] || boxes[b][d]) return false
                rows[r][d] = true
                cols[c][d] = true
                boxes[b][d] = true
            }
        }
        return true
    }
}""",
            "code_python": """class Solution:
    def isValidSudoku(self, board: list[list[str]]) -> bool:
        rows = [[False] * 9 for _ in range(9)]
        cols = [[False] * 9 for _ in range(9)]
        boxes = [[False] * 9 for _ in range(9)]
        for r in range(9):
            for c in range(9):
                ch = board[r][c]
                if ch == ".":
                    continue
                d = ord(ch) - 49
                b = (r // 3) * 3 + (c // 3)
                if rows[r][d] or cols[c][d] or boxes[b][d]:
                    return False
                rows[r][d] = True
                cols[c][d] = True
                boxes[b][d] = True
        return True""",
            "code_java": """class Solution {
    public boolean isValidSudoku(char[][] board) {
        boolean[][] rows = new boolean[9][9];
        boolean[][] cols = new boolean[9][9];
        boolean[][] boxes = new boolean[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = board[r][c];
                if (ch == '.') continue;
                int d = ch - '1';
                int b = (r / 3) * 3 + (c / 3);
                if (rows[r][d] || cols[c][d] || boxes[b][d]) return false;
                rows[r][d] = true;
                cols[c][d] = true;
                boxes[b][d] = true;
            }
        }
        return true;
    }
}"""
        },
        {
            "name": "Optimal: Single Pass with 9-Int Bitmasks",
            "badge": "Optimal",
            "desc": "Compresses each row, column, and 3x3 sub-box into a 9-bit integer mask for zero-allocation bitwise validation.",
            "time": "O(1) (81 cells)",
            "space": "O(1) (27 ints)",
            "code_kotlin": """class Solution {
    fun isValidSudoku(board: Array<CharArray>): Boolean {
        val rows = IntArray(9)
        val cols = IntArray(9)
        val boxes = IntArray(9)
        for (r in 0 until 9) {
            for (c in 0 until 9) {
                val ch = board[r][c]
                if (ch == '.') continue
                val bit = 1 shl (ch - '1')
                val b = (r / 3) * 3 + (c / 3)
                if ((rows[r] and bit) != 0 || (cols[c] and bit) != 0 || (boxes[b] and bit) != 0) {
                    return false
                }
                rows[r] = rows[r] or bit
                cols[c] = cols[c] or bit
                boxes[b] = boxes[b] or bit
            }
        }
        return true
    }
}""",
            "code_python": """class Solution:
    def isValidSudoku(self, board: list[list[str]]) -> bool:
        rows = [0] * 9
        cols = [0] * 9
        boxes = [0] * 9
        for r in range(9):
            for c in range(9):
                ch = board[r][c]
                if ch == ".":
                    continue
                bit = 1 << (ord(ch) - 49)
                b = (r // 3) * 3 + (c // 3)
                if (rows[r] & bit) or (cols[c] & bit) or (boxes[b] & bit):
                    return False
                rows[r] |= bit
                cols[c] |= bit
                boxes[b] |= bit
        return True""",
            "code_java": """class Solution {
    public boolean isValidSudoku(char[][] board) {
        int[] rows = new int[9];
        int[] cols = new int[9];
        int[] boxes = new int[9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = board[r][c];
                if (ch == '.') continue;
                int bit = 1 << (ch - '1');
                int b = (r / 3) * 3 + (c / 3);
                if ((rows[r] & bit) != 0 || (cols[c] & bit) != 0 || (boxes[b] & bit) != 0) {
                    return false;
                }
                rows[r] |= bit;
                cols[c] |= bit;
                boxes[b] |= bit;
            }
        }
        return true;
    }
}"""
        }
    ],
    "test_cases": [
        {
            "input": [[
                ["5","3",".",".","7",".",".",".","."],
                ["6",".",".","1","9","5",".",".","."],
                [".","9","8",".",".",".",".","6","."],
                ["8",".",".",".","6",".",".",".","3"],
                ["4",".",".","8",".","3",".",".","1"],
                ["7",".",".",".","2",".",".",".","6"],
                [".","6",".",".",".",".","2","8","."],
                [".",".",".","4","1","9",".",".","5"],
                [".",".",".",".","8",".",".","7","9"]
            ]],
            "expected": True
        },
        {
            "input": [[
                ["8","3",".",".","7",".",".",".","."],
                ["6",".",".","1","9","5",".",".","."],
                [".","9","8",".",".",".",".","6","."],
                ["8",".",".",".","6",".",".",".","3"],
                ["4",".",".","8",".","3",".",".","1"],
                ["7",".",".",".","2",".",".",".","6"],
                [".","6",".",".",".",".","2","8","."],
                [".",".",".","4","1","9",".",".","5"],
                [".",".",".",".","8",".",".","7","9"]
            ]],
            "expected": False
        },
        {
            "input": [[
                ["5","3",".",".","7",".",".",".","."],
                ["6",".",".","1","9","5",".",".","."],
                [".","9","5",".",".",".",".","6","."],
                ["8",".",".",".","6",".",".",".","3"],
                ["4",".",".","8",".","3",".",".","1"],
                ["7",".",".",".","2",".",".",".","6"],
                [".","6",".",".",".",".","2","8","."],
                [".",".",".","4","1","9",".",".","5"],
                [".",".",".",".","8",".",".","7","9"]
            ]],
            "expected": False
        }
    ],
    "spoken_script": {
        "phase1": "First, an important scope check: we are ONLY validating that currently filled digits `'1'..'9'` don't repeat in any row, column, or `3 x 3` sub-box—we do NOT need to check whether the board is solvable!",
        "phase2": "Writing three separate multi-loop passes is verbose and error-prone. Instead, one `9 x 9` scan maps any cell `(r, c)` to its `3 x 3` box `0..8` via `(r / 3) * 3 + (c / 3)` and uses three 9-int bitmasks (`rows`, `cols`, `boxes`).",
        "phase3": "For each non-dot cell, `bit = 1 shl (ch - '1')`; if `(rows[r] and bit) != 0` or cols or boxes has that bit set, return `false`; otherwise OR `bit` into all three masks.",
        "phase4": "Checking `(2, 2)`: `r/3 = 0`, `c/3 = 0` -> `box = 0` (same box as `(0, 0)`!). Checking `(4, 7)`: `r/3 = 1`, `c/3 = 2` -> `box = 1*3 + 2 = 5`.",
        "mid_followup": "If generalized to an `N x N` Sudoku (`N = K²`, e.g., `64 x 64` where `N > 32` exceeds 32-bit `Int` bitmasks), replace `IntArray(N)` with `Array(N) { BooleanArray(N) }` or `BitSet(N)` and compute `box = (r / K) * K + (c / K)` in `O(N²)` time."
    },
    "quiz_questions": [
        {
            "category": "Sub-Box Indexing Bug",
            "prompt": "What bug occurs if you compute `val b = (r / 3) + (c / 3)` instead of `(r / 3) * 3 + (c / 3)`?",
            "codeSnippet": "val b = (r / 3) + (c / 3) // <-- BUG!",
            "options": [
                {"id": "A", "text": "Distinct sub-boxes like top-middle (0,3) and middle-left (3,0) both collide at b = 1!", "isCorrect": True, "whyRight": "(0/3)+(3/3) = 1 and (3/3)+(0/3) = 1, falsely rejecting valid boards whenever those two unrelated boxes share a digit!"},
                {"id": "B", "text": "b exceeds index 8 and throws ArrayIndexOutOfBoundsException at cell (8, 8).", "isCorrect": False, "whyWrong": "(8/3)+(8/3) = 2 + 2 = 4 <= 8; it under-indexes into only 0..4 instead of 0..8."},
                {"id": "C", "text": "Empty '.' cells are no longer skipped by the loop.", "isCorrect": False, "whyWrong": "The '.' check runs before computing b."},
                {"id": "D", "text": "Row and column checks also break whenever r equals c.", "isCorrect": False, "whyWrong": "rows[r] and cols[c] do not use b."}
            ]
        },
        {
            "category": "Problem Scope Trap (Valid vs Solvable)",
            "prompt": "Suppose a board has NO duplicate digits in any row, column, or 3x3 box, yet leaves cell `(0, 2)` with zero legal moves `1..9`. What must `isValidSudoku` return?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "It must return true because validity only checks filled cells for current rule violations.", "isCorrect": True, "whyRight": "The problem statement explicitly notes a partially filled board can be valid without being solvable (#37 Sudoku Solver is separate!)."},
                {"id": "B", "text": "It must return false by running full backtracking to verify at least one solution exists.", "isCorrect": False, "whyWrong": "Running backtracking would wrongly reject unsolvable-but-valid boards."},
                {"id": "C", "text": "It throws IllegalArgumentException on dead-end Sudoku states.", "isCorrect": False, "whyWrong": "Return type is boolean."},
                {"id": "D", "text": "It mutates cell (0, 2) to '0' before returning false.", "isCorrect": False, "whyWrong": "isValidSudoku never mutates the board."}
            ]
        },
        {
            "category": "Python Integer Division Trap",
            "prompt": "In Python 3, what happens if you write `b = (r / 3) * 3 + (c / 3)` using `/` instead of `//`?",
            "codeSnippet": "b = (r / 3) * 3 + (c / 3)  # Python 3",
            "options": [
                {"id": "A", "text": "`/` produces floats (like `1.333...`), simplifying to `r + c/3` and crashing list indexing with TypeError!", "isCorrect": True, "whyRight": "Python 3 true division `/` returns float; integer floor division `//` is mandatory for `(r // 3) * 3 + (c // 3)`."},
                {"id": "B", "text": "Python 3 automatically truncates `/` to `int` whenever both operands are `int`.", "isCorrect": False, "whyWrong": "That was Python 2 behavior; Python 3 always returns `float` from `/`."},
                {"id": "C", "text": "It silently maps every cell on the board to box 0.", "isCorrect": False, "whyWrong": "Indexing `boxes[float]` raises `TypeError: list indices must be integers`."},
                {"id": "D", "text": "It only fails when r or c is negative.", "isCorrect": False, "whyWrong": "Even `0 / 3` returns `0.0` (`float`) in Python 3!"}
            ]
        },
        {
            "category": "Generalized N x N Scaling",
            "prompt": "If `isValidSudoku` is generalized to `N x N` boards with `N = 64` (`8 x 8` sub-boxes), what must change in the 32-bit `IntArray` bitmask code?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "32-bit Int shifts wrap past bit 31, so use LongArray(64) or Boolean[64][64] with K = 8.", "isCorrect": True, "whyRight": "In JVM languages, `1 << d` masks the shift count to `d & 31`, colliding digit 33 with digit 1 unless 64-bit `1L << d` or `BooleanArray` is used!"},
                {"id": "B", "text": "Keep `1 shl d` on 32-bit Int because JVM ints automatically expand to arbitrary precision.", "isCorrect": False, "whyWrong": "JVM `Int` is strictly 32-bit; only Python `int` has arbitrary precision."},
                {"id": "C", "text": "Keep `(r / 3) * 3 + (c / 3)` unchanged regardless of board dimension N.", "isCorrect": False, "whyWrong": "For N = K^2 = 64, sub-boxes are 8x8, requiring `(r / 8) * 8 + (c / 8)`."},
                {"id": "D", "text": "Time complexity stays independent of N at 81 operations.", "isCorrect": False, "whyWrong": "Scanning an N x N board takes O(N^2) operations."}
            ]
        }
    ]
}


p125 = {
    "num": 125,
    "lc": 692,
    "title": "Top K Frequent Words",
    "category": "Heaps & Priority Queues",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "top-k-frequent-words",
    "nc_url": "https://leetcode.com/problems/top-k-frequent-words/",
    "prev_file": "0124-lc36-valid-sudoku.html",
    "next_file": "0126-custom-cloud-storage-ica.html",
    "method_name": "topKFrequent",
    "verbatim_statement": (
        "Given an array of strings <code>words</code> and an integer <code>k</code>, return the <code>k</code> most frequent strings.<br><br>"
        "Return the answer <strong>sorted</strong> by the <strong>frequency</strong> from highest to lowest. "
        "Sort the words with the same frequency by their <strong>lexicographical order</strong> (<code>\"i\"</code> before <code>\"love\"</code>)."
    ),
    "examples": [
        {"title": "Example 1", "input": 'words = ["i","love","leetcode","i","love","coding"], k = 2', "output": '["i","love"]'},
        {"title": "Example 2", "input": 'words = ["the","day","is","sunny","the","the","the","sunny","is","is"], k = 4', "output": '["the","is","sunny","day"]'}
    ],
    "constraints": [
        "1 <= words.length <= 500",
        "1 <= words[i].length <= 10",
        "k is in [1, number of unique words]"
    ],
    "plain_english": (
        "Count word frequencies in a HashMap. To keep only the best `k` words inside a bounded Min-Heap (which evicts the *worst* survivor first!), "
        "define 'worse' as **lower frequency** OR **same frequency and lexicographically LARGER** (`b.compareTo(a)`), then pop and reverse!"
    ),
    "boundary_checks": {
        "min_base": 'words = ["a"], k = 1 -> Returns ["a"].',
        "best_exit": 'Tie-break test: ["love", "i", "love", "i"], k = 1 -> Both have freq 2; heap evicts "love" ("love" > "i") and returns ["i"]!',
        "worst_scan": "U unique words -> O(N) counting + O(U log k) bounded heap (or O(U + k log U) heapify)."
    },
    "mental_model": {
        "analogy_title": "The Inverted Tie-Breaker Trap in Worst-First Eviction",
        "analogy_text": (
            "In Top K Frequent Elements (#347), order didn't matter and ties didn't exist. Here, `'i'` (freq 2) beats `'love'` (freq 2) alphabetically! "
            "Inside a size-`k` Min-Heap, the root is the **first candidate to get kicked out** when `size > k`. "
            "Between `'i'` and `'love'` at freq 2, who should get kicked out? `'love'`! Therefore `'love'` must sit at the root BEFORE `'i'`—meaning "
            "the Min-Heap comparator sorts frequency **ascending** (`fa - fb`) and word **descending** (`b.compareTo(a)`), then reverses at the end!"
        ),
        "aha_moment": "In a size-K Min-Heap, root = WORST kept item: lower frequency (`fa - fb`) AND lexicographically LARGER word (`b.compareTo(a)`), followed by `res.reverse()`!",
        "trigger": "Multi-criteria Top-K leaderboards, search autocomplete ranking, and custom comparator design.",
        "sisters": [
            {"name": "Top K Frequent Elements", "lc": "#347"},
            {"name": "Kth Largest Element in a Stream", "lc": "#703"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(U log U) Full Sort",
        "naive_text": "Count frequencies in O(N), then sort all U unique words by (-freq, word) and slice the first k.",
        "wasted_work": "When N = 10^6 search queries and k = 10 autocomplete suggestions, sorting all U unique queries wastes O(U log U) time and O(U) sort buffer.",
        "optimal_complexity": "O(N + U log K) Time, O(U) Map + O(K) Heap Space",
        "optimal_text": "Count frequencies in O(N), push into a size-K Min-Heap with inverted comparator `(fa != fb ? fa - fb : b.compareTo(a))`, then poll K items and reverse.",
        "core_invariant": "At every step, pq holds the best min(seen, k) words, with the single worst among them (lowest freq, or largest word on tie) at pq.peek().",
        "table_rows": [
            {"name": "Full Sort on (-freq, word)", "time": "O(N + U log U)", "space": "O(U)", "note": "Great baseline; slower when K << U."},
            {"name": "Max-Heap Heapify ((-freq, word))", "time": "O(N + U + K log U)", "space": "O(U)", "note": "Clean in Python (`heapq.heapify` in O(U) + K pops)."},
            {"name": "Optimal: Bounded Size-K Min-Heap", "time": "O(N + U log K)", "space": "O(U) map, O(K) heap", "note": "★ Best streaming/autocomplete scaling when K = 10."}
        ]
    },
    "trace_table": {
        "input_desc": 'words = ["i","love","leetcode","i","love","coding"], k = 2 -> freq: {i:2, love:2, leetcode:1, coding:1}',
        "columns": ["Offer Word", "Heap Contents (Root First = Worst)", "Evicted (size > 2)", "Why"],
        "rows": [
            ['"i" (2)', '["i"(2)]', "-", "Size 1 <= 2"],
            ['"love" (2)', '["love"(2), "i"(2)]', "-", 'Tie freq=2: "love" > "i" so "love" is WORSE (at root!)'],
            ['"leetcode" (1)', '["love"(2), "i"(2)]', '"leetcode"(1)', "Freq 1 < 2 -> root 'leetcode' evicted"],
            ['"coding" (1)', '["love"(2), "i"(2)]', '"coding"(1)', "Freq 1 < 2 -> root 'coding' evicted"],
            ["Poll & Reverse", 'poll -> ["love", "i"] -> reverse -> ["i", "love"]', "-", "Exact sorted Top-2 output!"]
        ],
        "takeaway": "If you forgot `b.compareTo(a)` and wrote `a.compareTo(b)`, `'i'` would sit at the root and get evicted if a freq-3 word arrived!"
    },
    "stepper": {
        "title": "Size-K Min-Heap with Inverted Tie-Breaker Stepper",
        "default_input": '["i","love","leetcode","i","love","coding"], k=2',
        "presets": [
            {"label": 'Example 1 (k=2, tie "i" vs "love")', "onclick": 'loadPreset(["i","love","leetcode","i","love","coding"], 2)'},
            {"label": 'Example 2 (k=4)', "onclick": 'loadPreset(["the","day","is","sunny","the","the","the","sunny","is","is"], 4)'}
        ],
        "metrics": [
            {"id": "m-word", "label": "Word (Freq)", "color": "text-amber-400", "initial": "-"},
            {"id": "m-root", "label": "Heap Root (Worst)", "color": "text-rose-400", "initial": "-"},
            {"id": "m-k", "label": "Heap Size / K", "color": "text-cyan-400", "initial": "0 / 2"},
            {"id": "m-out", "label": "Final Top-K", "color": "text-emerald-400", "initial": "Pending"}
        ],
        "visual_html": '<div id="p125-stage" class="flex flex-wrap justify-center gap-2.5 w-full"></div>',
        "initial_narration": "Observe how worse tie-breakers sit at the Min-Heap root for eviction.",
        "pseudocode_lines": [
            "val freq = HashMap<String, Int>(); for (w in words) freq[w] = (freq[w] ?: 0) + 1",
            "val pq = PriorityQueue<String> { a, b ->",
            "  val fa = freq[a]!!; val fb = freq[b]!!; if (fa != fb) fa - fb else b.compareTo(a)",
            "}",
            "for (w in freq.keys) { pq.offer(w); if (pq.size > k) pq.poll() }",
            "val res = mutableListOf<String>(); while (pq.isNotEmpty()) res.add(pq.poll()); res.reverse()"
        ],
        "js_code": """
    function buildSteps(words, k) {
      words = words || ["i","love","leetcode","i","love","coding"]; k = k || 2;
      steps = [];
      const freq = {};
      for (const w of words) freq[w] = (freq[w] || 0) + 1;
      const cmpWorstFirst = (a, b) => freq[a] !== freq[b] ? freq[a] - freq[b] : (b < a ? -1 : (b > a ? 1 : 0));
      let pq = [];
      steps.push({ w: 'counted', pq: [], k, freq, out: '-', line: 1, narration: `Frequencies: ${JSON.stringify(freq)}.` });
      for (const w of Object.keys(freq)) {
        pq.push(w);
        pq.sort(cmpWorstFirst);
        let ev = null;
        if (pq.length > k) ev = pq.shift();
        steps.push({ w: `${w} (${freq[w]})`, pq: pq.slice(), k, freq, out: ev ? `evicted "${ev}"` : 'kept', line: 5, narration: `Offered "${w}" (freq=${freq[w]}) -> ${ev ? `evicted worst "${ev}"` : 'kept'}. Worst survivor at root: "${pq[0]}".` });
      }
      const finalOut = pq.slice().reverse();
      steps.push({ w: 'done', pq: finalOut, k, freq, out: JSON.stringify(finalOut), line: 6, narration: `Polled worst-to-best and reversed -> ${JSON.stringify(finalOut)}!` });
    }
    function renderStage(step) {
      const el = document.getElementById('p125-stage');
      if (!el) return;
      if (!step.pq.length) { el.innerHTML = '<span class="text-xs font-mono text-slate-400">Frequency map ready</span>'; return; }
      el.innerHTML = step.pq.map((w, i) => `<div class="px-3.5 py-2 rounded-xl border font-mono text-xs flex flex-col items-center ${i === 0 && step.w !== 'done' ? 'border-rose-400 bg-rose-950/50 text-rose-200' : 'border-emerald-500/40 bg-slate-900 text-emerald-300'}"><span class="text-[10px] opacity-75">${step.w === 'done' ? '#' + (i + 1) : (i === 0 ? 'ROOT (Worst)' : 'Kept')}</span><b>"${w}" (${step.freq[w]})</b></div>`).join('');
    }
    function loadPreset(w, k) { buildSteps(w, k); stepperReset(); }
        """,
        "js_init_call": 'buildSteps(["i","love","leetcode","i","love","coding"], 2);',
        "js_update_metrics": """
        document.getElementById('m-word').textContent = step.w;
        document.getElementById('m-root').textContent = step.pq.length ? `"${step.pq[0]}"` : '-';
        document.getElementById('m-k').textContent = `${step.pq.length} / ${step.k}`;
        document.getElementById('m-out').textContent = step.out;
        """
    },
    "approaches": [
        {
            "name": "HashMap + Full Custom Sort",
            "badge": "Baseline",
            "desc": "Counts frequencies and sorts all unique words by (-freq, word) in O(U log U) time.",
            "time": "O(N + U log U)",
            "space": "O(U)",
            "code_kotlin": """class Solution {
    fun topKFrequent(words: Array<String>, k: Int): List<String> {
        val freq = HashMap<String, Int>()
        for (w in words) freq[w] = (freq[w] ?: 0) + 1
        val unique = freq.keys.toMutableList()
        unique.sortWith { a, b ->
            val fa = freq[a]!!
            val fb = freq[b]!!
            if (fa != fb) fb - fa else a.compareTo(b)
        }
        return unique.subList(0, k)
    }
}""",
            "code_python": """from collections import Counter

class Solution:
    def topKFrequent(self, words: list[str], k: int) -> list[str]:
        freq = Counter(words)
        ordered = sorted(freq.keys(), key=lambda w: [-freq[w], w])
        return ordered[:k]""",
            "code_java": """import java.util.*;

class Solution {
    public List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> freq = new HashMap<>();
        for (String w : words) freq.put(w, freq.getOrDefault(w, 0) + 1);
        List<String> unique = new ArrayList<>(freq.keySet());
        Collections.sort(unique, (a, b) -> {
            int fa = freq.get(a), fb = freq.get(b);
            return fa != fb ? fb - fa : a.compareTo(b);
        });
        return unique.subList(0, k);
    }
}"""
        },
        {
            "name": "Optimal: PriorityQueue Heap (O(N + U log K) / O(N + K log U))",
            "badge": "Optimal",
            "desc": "Uses a bounded size-K Min-Heap with inverted lexicographical tie-breaker in Kotlin/Java, and O(U) heapify + K pops in Python.",
            "time": "O(N + U log K)",
            "space": "O(U)",
            "code_kotlin": """import java.util.PriorityQueue

class Solution {
    fun topKFrequent(words: Array<String>, k: Int): List<String> {
        val freq = HashMap<String, Int>()
        for (w in words) {
            freq[w] = (freq[w] ?: 0) + 1
        }
        val pq = PriorityQueue<String> { a, b ->
            val fa = freq[a]!!
            val fb = freq[b]!!
            if (fa != fb) fa - fb else b.compareTo(a)
        }
        for (w in freq.keys) {
            pq.offer(w)
            if (pq.size > k) pq.poll()
        }
        val res = mutableListOf<String>()
        while (pq.isNotEmpty()) {
            res.add(pq.poll())
        }
        res.reverse()
        return res
    }
}""",
            "code_python": """import heapq
from collections import Counter

class Solution:
    def topKFrequent(self, words: list[str], k: int) -> list[str]:
        freq = Counter(words)
        heap = [[-cnt, word] for word, cnt in freq.items()]
        heapq.heapify(heap)
        res = []
        for _ in range(k):
            res.append(heapq.heappop(heap)[1])
        return res""",
            "code_java": """import java.util.*;

class Solution {
    public List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> freq = new HashMap<>();
        for (String w : words) {
            freq.put(w, freq.getOrDefault(w, 0) + 1);
        }
        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> {
            int fa = freq.get(a);
            int fb = freq.get(b);
            return fa != fb ? fa - fb : b.compareTo(a);
        });
        for (String w : freq.keySet()) {
            pq.offer(w);
            if (pq.size() > k) pq.poll();
        }
        List<String> res = new ArrayList<>();
        while (!pq.isEmpty()) {
            res.add(pq.poll());
        }
        Collections.reverse(res);
        return res;
    }
}"""
        }
    ],
    "test_cases": [
        {"input": [["i", "love", "leetcode", "i", "love", "coding"], 2], "expected": ["i", "love"]},
        {"input": [["the", "day", "is", "sunny", "the", "the", "the", "sunny", "is", "is"], 4], "expected": ["the", "is", "sunny", "day"]},
        {"input": [["love", "i", "love", "i", "apple"], 1], "expected": ["i"]}
    ],
    "spoken_script": {
        "phase1": "Unlike Top K Frequent Elements (#347), output order here is strictly deterministic: highest frequency first, and ties broken lexicographically ascending (`\"i\"` before `\"love\"`).",
        "phase2": "Full sorting takes `O(U log U)`. Using a Min-Heap capped at size `k` drops that to `O(U log k)`—the crucial nuance is inverting BOTH axes in the Min-Heap comparator because `poll()` evicts the *worst* survivor.",
        "phase3": "In the size-`k` Min-Heap, `a` comes before `b` (is worse) when `freq[a] < freq[b]`, OR when `freq[a] == freq[b]` and `b.compareTo(a) < 0` (`a > b` alphabetically). After processing all unique keys, I poll all `k` elements (worst-to-best) and reverse the list.",
        "phase4": "Dry-running `[\"love\",\"i\",\"love\",\"i\",\"apple\"]`, `k=1`: `'love'`(2) enters; `'i'`(2) enters -> `'love' > 'i'` puts `'love'` at root and evicts `'love'`; `'apple'`(1) enters -> freq 1 < 2 evicts `'apple'`; returns `[\"i\"]`!",
        "mid_followup": "For real-time prefix autocomplete ('top 5 words starting with prefix P'), precompute and cache the top-5 list directly inside every `TrieNode` (`#208`) during index build or update so runtime keystroke queries run in `O(length(P))` with zero heap allocation on the read path!"
    },
    "quiz_questions": [
        {
            "category": "Comparator Inversion Bug",
            "prompt": "In the size-`k` Min-Heap solution, what happens on `words = [\"i\",\"love\",\"i\",\"love\"], k = 1` if you write `a.compareTo(b)` instead of `b.compareTo(a)`?",
            "codeSnippet": "val pq = PriorityQueue<String> { a, b ->\n    val fa = freq[a]!!; val fb = freq[b]!!\n    if (fa != fb) fa - fb else a.compareTo(b) // <-- BUG!\n}",
            "options": [
                {"id": "A", "text": "\"i\" sits at the Min-Heap root, gets evicted by pq.poll(), and [\"love\"] is wrongly returned!", "isCorrect": True, "whyRight": "a.compareTo(b) treats smaller \"i\" as the minimum (eviction target) instead of keeping \"i\" and evicting \"love\"!"},
                {"id": "B", "text": "The PriorityQueue throws IllegalArgumentException on equal frequencies.", "isCorrect": False, "whyWrong": "a.compareTo(b) is a valid comparator; it simply evicts the winner instead of the loser."},
                {"id": "C", "text": "Words with frequency 1 beat words with frequency 2.", "isCorrect": False, "whyWrong": "Primary key fa - fb still evicts lower frequencies properly."},
                {"id": "D", "text": "res.reverse() automatically fixes the wrong eviction after polling.", "isCorrect": False, "whyWrong": "Once \"i\" was evicted when k=1, reversing [\"love\"] cannot recover \"i\"!"}
            ]
        },
        {
            "category": "Java Integer Caching Pitfall",
            "prompt": "In Java, why is `freq.get(a) != freq.get(b)` a subtle production bug inside a `Map<String, Integer>` comparator when counts exceed `127`?",
            "codeSnippet": "return freq.get(a) != freq.get(b) ? freq.get(a) - freq.get(b) : b.compareTo(a);",
            "options": [
                {"id": "A", "text": "`!=` compares boxed `Integer` object references, which fails for equal counts outside `-128..127`!", "isCorrect": True, "whyRight": "JVM only caches Integer instances in -128..127; unboxing to `int fa = freq.get(a), fb = freq.get(b)` or `.equals()` avoids reference inequality bugs!"},
                {"id": "B", "text": "`HashMap.get` returns `Double` whenever counts exceed 127.", "isCorrect": False, "whyWrong": "Return type remains `Integer`."},
                {"id": "C", "text": "`b.compareTo(a)` only compares the first 127 characters of a string.", "isCorrect": False, "whyWrong": "`String.compareTo` compares the full string."},
                {"id": "D", "text": "Java `PriorityQueue` caps maximum heap capacity at 127 elements.", "isCorrect": False, "whyWrong": "`PriorityQueue` grows dynamically on the heap."}
            ]
        },
        {
            "category": "Python `heapq` Tuple Trick",
            "prompt": "Why does `heap = [(-cnt, word) for word, cnt in freq.items()]; heapq.heapify(heap)` handle ties without inverting `word`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Negating `cnt` simulates a Max-Heap of ALL `U` words where root is the BEST item (`-cnt` min, `word` min)!", "isCorrect": True, "whyRight": "Because we pop the K *best* items directly (instead of evicting U - K *worst* items), smaller `word` ('i' < 'love') should sit at the root!"},
                {"id": "B", "text": "Python `heapq` automatically reverses string order whenever the first tuple element is negative.", "isCorrect": False, "whyWrong": "Tuple comparison always compares strings in standard ascending ASCII order."},
                {"id": "C", "text": "`heapify` sorts the entire array in O(U log U) time before returning.", "isCorrect": False, "whyWrong": "Floyd's `heapify` runs in linear O(U) time."},
                {"id": "D", "text": "Python strings compare by length before alphabetical order.", "isCorrect": False, "whyWrong": "Python strings compare lexicographically code-point by code-point."}
            ]
        },
        {
            "category": "Autocomplete System Design Pivot",
            "prompt": "If an autocomplete service serves `50,000 QPS` asking for top-5 completions per typed prefix, where should Top-K live?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Precompute and store the sorted top-5 list on each TrieNode so prefix reads run in O(L).", "isCorrect": True, "whyRight": "Scanning all subtree words per keystroke is too slow at 50K QPS; caching top-5 per TrieNode makes reads O(prefixLength)."},
                {"id": "B", "text": "Re-scan the entire 10M-word frequency table with a Min-Heap on every keystroke.", "isCorrect": False, "whyWrong": "10M scan * 50K QPS is impossible on the hot read path."},
                {"id": "C", "text": "Store words in an unsorted linked list and run binary search.", "isCorrect": False, "whyWrong": "Linked lists do not support random-access binary search."},
                {"id": "D", "text": "Hash only full words so prefixes cannot be matched until Enter is pressed.", "isCorrect": False, "whyWrong": "Autocomplete requires live prefix matching."}
            ]
        }
    ]
}


p126 = {
    "num": 126,
    "lc": "ICA",
    "title": "Cloud Storage System (4-Stage ICA)",
    "category": "Multi-Tier OOP & Timed State Systems",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "cloud-storage-ica",
    "nc_url": "https://neetcode.io/problems/design-in-memory-file-system/question?list=allNC",
    "prev_file": "0125-lc692-top-k-frequent-words.html",
    "next_file": "../master-plan.html",
    "method_name": "addFile",
    "verbatim_statement": (
        "Design a deterministic <strong>4-Stage CodeSignal ICA Cloud Storage System</strong>:<br><br>"
        "&bull; <strong>Stage 1 (Core File Ops):</strong> <code>addFile(name, size)</code> (returns <code>false</code> if <code>name</code> already exists, else creates owned by <code>\"admin\"</code> and returns <code>true</code>), "
        "<code>copyFile(source, dest)</code> (returns <code>false</code> if <code>source</code> missing, <code>dest</code> exists, or owner's quota exceeded), "
        "<code>getFileSize(name)</code> (returns <code>size</code> or <code>-1</code> if missing).<br>"
        "&bull; <strong>Stage 2 (Filtered Ranked Search):</strong> <code>findFile(prefix, suffix)</code> returns matching files formatted as <code>\"name(size)\"</code> sorted by <strong>size descending</strong>, tie-broken by <strong>name lexicographically ascending</strong>.<br>"
        "&bull; <strong>Stage 3 (Multi-Tenant User Quotas):</strong> <code>addUser(userId, capacity)</code> (returns <code>false</code> if user exists), "
        "<code>addFileBy(userId, name, size)</code> (returns remaining user capacity or <code>-1</code> if user missing / file exists / over capacity), "
        "<code>updateCapacity(userId, capacity)</code> (updates limit; if usage exceeds new capacity, evicts that user's <strong>largest files first</strong>—tie-breaking by <strong>lexicographically largest name</strong>—and returns count of evicted files, or <code>-1</code> if user missing).<br>"
        "&bull; <strong>Stage 4 (Scoped Backup &amp; Restore):</strong> <code>backupUser(userId)</code> snapshots user's current files &amp; returns file count (or <code>-1</code>); "
        "<code>restoreUser(userId)</code> deletes user's current files, restores backed-up files whose path isn't currently owned by another user (if no backup exists, simply clears user's files), and returns restored count (or <code>-1</code>)."
    ),
    "examples": [
        {"title": "Stage 1–2", "input": 'addFile("/a.txt", 10); copyFile("/a.txt", "/b.txt"); findFile("/", ".txt")', "output": '["/a.txt(10)", "/b.txt(10)"]'},
        {"title": "Stage 3 (Quota Eviction)", "input": 'addUser("u1", 100); addFileBy("u1", "/x", 60); addFileBy("u1", "/y", 30); updateCapacity("u1", 50)', "output": 'Evicts "/x" (60) -> returns 1'},
        {"title": "Stage 4 (Backup/Restore)", "input": 'backupUser("u1"); updateCapacity("u1", 0); updateCapacity("u1", 100); restoreUser("u1")', "output": "Restores u1's backed-up files -> returns count"}
    ],
    "constraints": [
        "1 <= operations <= 5000 across Stages 1–4",
        '"admin" has unlimited capacity; regular users enforce used + size <= capacity.',
        "copyFile preserves the source file's owner and checks that owner's quota!"
    ],
    "plain_english": (
        "Architect Stage 1 with Stage 3 & 4 in mind: store `FileMeta(size, owner)` inside `files` from line 1, with `\"admin\"` having infinite capacity! "
        "Then `addFile(name, size)` is just `addFileBy(\"admin\", name, size) >= 0`, `copyFile` preserves `src.owner` while checking quota, "
        "`updateCapacity` sorts a user's owned files by `(-size, -name)` to evict until `used <= capacity`, and `backupUser` snapshots a clean `Map<Path, Size>`."
    ),
    "boundary_checks": {
        "min_base": 'getFileSize("/missing") -> returns -1; addFile duplicate path -> returns false without overwriting.',
        "best_exit": 'copyFile owned by user "u1" when u1 lacks capacity -> returns false cleanly without partial state mutation.',
        "worst_scan": 'restoreUser("u1") when "/shared.txt" was claimed by "u2" after backup -> skips "/shared.txt" and restores remaining valid files.'
    },
    "mental_model": {
        "analogy_title": "Single Source-of-Truth `FileMeta(size, owner)` + User Ledger",
        "analogy_text": (
            "The #1 reason candidates run out of time on CodeSignal's Cloud Storage ICA is storing `Map<String, Int>` (`path -> size`) in Stage 1, "
            "then having to rip apart `addFile` and `copyFile` in Stage 3 when users arrive! Instead, give `\"admin\"` unlimited quota at startup and "
            "store `FileMeta(size, owner)` + `User(capacity, used, filesSet, backupMap)` from Stage 1. Every mutation goes through two tiny helpers—"
            "`attachFile(userId, name, size)` and `removeFile(name)`—so `used` bytes and `files` NEVER drift out of sync across Stage 3 evictions or Stage 4 restores!"
        ),
        "aha_moment": "Route ALL inserts and deletes through `attachFile` / `removeFile` updating `files[path]` and `user.used` atomically, and treat Stage 1 `addFile` as `\"admin\"` with unlimited quota!",
        "trigger": "90-minute CodeSignal 4-Stage Progressive ICA (Dropbox / Stripe / Capital One / Robinhood Cloud Storage & File Server variants).",
        "sisters": [
            {"name": "In-Memory KV Store with TTL (4-Stage ICA)", "lc": "ICA", "file": "0067-custom-in-memory-kv-store-ttl.html"},
            {"name": "Design In-Memory File System", "lc": "#588"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "Stage 3–4 Schema Rewrite & Desync Bugs",
        "naive_text": "Write Stage 1 with a flat `path -> size` map, bolt on separate `userFiles` lists in Stage 3 without updating `copyFile`, and forget global path collisions in Stage 4 `restoreUser`.",
        "wasted_work": "Duplicating quota/file mutations across 5 methods causes `user.used` drift and fails hidden regression tests on `copyFile` + `restoreUser`.",
        "optimal_complexity": "O(1) CRUD/Copy, O(F log F) Find/Evict, O(U_f) Backup/Restore",
        "optimal_text": "Unified `files: Map<Path, FileMeta>` + `users: Map<UserId, UserState>` with atomic `attach`/`detach` helpers.",
        "core_invariant": "For every user u, u.used equals the exact sum of files[p].size over p in u.ownedPaths, and p in u.ownedPaths iff files[p].owner == u.",
        "table_rows": [
            {"name": "Ad-Hoc Per-Stage Patchwork", "time": "High rewrite risk", "space": "Desynced maps", "note": "Breaks copyFile owner quota & restore collision rules."},
            {"name": "Optimal: Unified FileMeta + Atomic Helpers", "time": "O(1) CRUD, O(F log F) search", "space": "O(F + B)", "note": "★ Zero-rewrite Level 1 -> Level 4 ICA architecture."}
        ]
    },
    "trace_table": {
        "input_desc": 'addUser("u1", 100) -> addFileBy("u1","/a",40) -> addFileBy("u1","/b",40) -> backupUser("u1") -> updateCapacity("u1", 50) -> addFile("/b", 10) -> restoreUser("u1")',
        "columns": ["Step", "Call", "Global files", "u1 (used / cap)", "Return & Invariant Note"],
        "rows": [
            ["1", 'addUser("u1", 100)', "{}", "0 / 100", "true"],
            ["2", 'addFileBy("u1","/a",40), ("/b",40)', "{/a:40(u1), /b:40(u1)}", "80 / 100", "returns 60, then 20"],
            ["3", 'backupUser("u1")', "unchanged", "80 / 100 (snap={/a:40,/b:40})", "returns 2"],
            ["4", 'updateCapacity("u1", 50)', "{/a:40(u1)}", "40 / 50", 'Tie at 40: evicts larger name "/b" -> returns 1!'],
            ["5", 'addFile("/b", 10) (admin claims /b!)', "{/a:40(u1), /b:10(admin)}", "40 / 50", "true"],
            ["6", 'restoreUser("u1")', "{/a:40(u1), /b:10(admin)}", "40 / 50", 'Purges u1, restores /a (skips /b owned by admin!) -> returns 1']
        ],
        "takeaway": "Look at Steps 4 & 6: tie-breaking eviction (`\"/b\" > \"/a\"`) and skipping paths claimed by another user during `restoreUser` are the exact hidden ICA Stage 3–4 tests!"
    },
    "stepper": {
        "title": "4-Stage Cloud Storage ICA State & Quota Simulator",
        "default_input": "stage3_4",
        "presets": [
            {"label": "Stage 3–4: Quotas, Eviction & Restore", "onclick": 'loadPreset("full")'},
            {"label": "Stage 2: Ranked Prefix/Suffix Search", "onclick": 'loadPreset("search")'}
        ],
        "metrics": [
            {"id": "m-op", "label": "ICA Call", "color": "text-amber-400", "initial": "init"},
            {"id": "m-files", "label": "Total Files", "color": "text-cyan-400", "initial": "0"},
            {"id": "m-quota", "label": "u1 Used / Cap", "color": "text-purple-400", "initial": "0 / 100"},
            {"id": "m-ret", "label": "Return Value", "color": "text-emerald-400", "initial": "Ready"}
        ],
        "visual_html": '<div id="p126-stage" class="w-full max-w-3xl flex flex-col gap-3"></div>',
        "initial_narration": "Step through Stages 1–4 to inspect file ownership, quota eviction, and scoped restore.",
        "pseudocode_lines": [
            "data class FileMeta(val size: Int, val owner: String)",
            "fun addFile(name, size) = addFileBy(\"admin\", name, size) != -1",
            "fun findFile(pre, suf) = files matching pre/suf sorted by (-size, +name)",
            "fun updateCapacity(u, cap): while (u.used > cap) evict u's file with max (size, name)",
            "fun backupUser(u): u.backup = HashMap(u.ownedSizes); return u.backup.size",
            "fun restoreUser(u): deleteAll(u); restore (p, sz) if p !in files && u.used + sz <= u.cap"
        ],
        "js_code": """
    function buildSteps(mode) {
      steps = [];
      if (mode === 'search') {
        steps.push({ op: 'addFile("/d/b.log", 20), ("/d/a.log", 20), ("/d/c.log", 50)', files: {'/d/b.log':'20 (admin)', '/d/a.log':'20 (admin)', '/d/c.log':'50 (admin)'}, quota: 'unlimited', snap: 'none', ret: 'true', line: 2, narration: 'Inserted 3 log files with sizes 20, 20, and 50.' });
        steps.push({ op: 'findFile("/d/", ".log")', files: {'/d/c.log':'50 (admin)', '/d/a.log':'20 (admin)', '/d/b.log':'20 (admin)'}, quota: 'unlimited', snap: 'none', ret: '["/d/c.log(50)", "/d/a.log(20)", "/d/b.log(20)"]', line: 3, narration: 'Sorted by size DESC (50 first), then name ASC ("/d/a.log" before "/d/b.log")!' });
        return;
      }
      steps.push({ op: 'addUser("u1", 100)', files: {}, quota: '0 / 100', snap: 'none', ret: 'true', line: 1, narration: 'Created tenant u1 with capacity 100.' });
      steps.push({ op: 'addFileBy("u1","/a",40) & ("/b",40)', files: {'/a':'40 (u1)', '/b':'40 (u1)'}, quota: '80 / 100', snap: 'none', ret: '20 rem', line: 2, narration: 'u1 owns /a (40) and /b (40); remaining capacity = 20.' });
      steps.push({ op: 'backupUser("u1")', files: {'/a':'40 (u1)', '/b':'40 (u1)'}, quota: '80 / 100', snap: '{/a:40, /b:40}', ret: '2', line: 5, narration: 'Snapshotted 2 files for u1.' });
      steps.push({ op: 'updateCapacity("u1", 50)', files: {'/a':'40 (u1)'}, quota: '40 / 50', snap: '{/a:40, /b:40}', ret: '1 evicted', line: 4, narration: '80 > 50: tied at size 40, evicted lexicographically larger "/b"!' });
      steps.push({ op: 'addFile("/b", 10) [admin]', files: {'/a':'40 (u1)', '/b':'10 (admin)'}, quota: '40 / 50', snap: '{/a:40, /b:40}', ret: 'true', line: 2, narration: 'admin created a new file at "/b".' });
      steps.push({ op: 'restoreUser("u1")', files: {'/a':'40 (u1)', '/b':'10 (admin)'}, quota: '40 / 50', snap: '{/a:40, /b:40}', ret: '1 restored', line: 6, narration: 'Purged u1 current files, restored /a (40), skipped /b because admin owns it!' });
    }
    function renderStage(step) {
      const el = document.getElementById('p126-stage');
      if (!el) return;
      const fks = Object.keys(step.files);
      const fHtml = fks.length ? fks.map(k => `<div class="p-1.5 rounded bg-slate-950 border border-slate-700 font-mono text-xs flex justify-between"><span class="text-cyan-300">${k}</span><span class="text-slate-300">${step.files[k]}</span></div>`).join('') : '<span class="text-xs font-mono text-slate-500 italic">No files</span>';
      el.innerHTML = `<div class="grid sm:grid-cols-2 gap-3"><div class="p-3 rounded-xl bg-slate-900 border border-slate-800 space-y-1.5"><span class="text-xs font-mono font-bold text-emerald-400 block">Global File Table:</span>${fHtml}</div><div class="p-3 rounded-xl bg-slate-900 border border-slate-800 font-mono text-xs space-y-2"><div class="text-purple-300 font-bold">u1 Quota: ${step.quota}</div><div class="text-amber-300">u1 Backup: ${step.snap}</div><div class="text-emerald-300">Output: ${step.ret}</div></div></div>`;
    }
    function loadPreset(m) { buildSteps(m); stepperReset(); }
        """,
        "js_init_call": 'buildSteps("full");',
        "js_update_metrics": """
        document.getElementById('m-op').textContent = step.op;
        document.getElementById('m-files').textContent = Object.keys(step.files).length;
        document.getElementById('m-quota').textContent = step.quota;
        document.getElementById('m-ret').textContent = step.ret;
        """
    },
    "approaches": [
        {
            "name": "Stage 1–2 Baseline: Single-Tenant File Map + Search",
            "badge": "Stages 1–2",
            "desc": "Shows the clean Stage 1–2 core before multi-tenant quotas and snapshots are layered on.",
            "time": "O(1) CRUD, O(F log F) find",
            "space": "O(F)",
            "code_kotlin": """class CloudStorageBasic {
    private val files = HashMap<String, Int>()

    fun addFile(name: String, size: Int): Boolean {
        if (files.containsKey(name)) return false
        files[name] = size
        return true
    }

    fun copyFile(source: String, dest: String): Boolean {
        val sz = files[source] ?: return false
        if (files.containsKey(dest)) return false
        files[dest] = sz
        return true
    }

    fun getFileSize(name: String): Int = files[name] ?: -1

    fun findFile(prefix: String, suffix: String): List<String> {
        val matched = mutableListOf<String>()
        for (name in files.keys) {
            if (name.startsWith(prefix) && name.endsWith(suffix)) matched.add(name)
        }
        matched.sortWith { a, b ->
            val sa = files[a]!!
            val sb = files[b]!!
            if (sa != sb) sb - sa else a.compareTo(b)
        }
        return matched.map { "$it(${files[it]})" }
    }
}""",
            "code_python": """class CloudStorageBasic:
    def __init__(self):
        self.files = {}

    def addFile(self, name: str, size: int) -> bool:
        if name in self.files:
            return False
        self.files[name] = size
        return True

    def copyFile(self, source: str, dest: str) -> bool:
        if source not in self.files or dest in self.files:
            return False
        self.files[dest] = self.files[source]
        return True

    def getFileSize(self, name: str) -> int:
        return self.files.get(name, -1)

    def findFile(self, prefix: str, suffix: str) -> list[str]:
        matched = [k for k in self.files.keys() if k.startswith(prefix) and k.endswith(suffix)]
        matched.sort(key=lambda k: [-self.files[k], k])
        return [f"{k}({self.files[k]})" for k in matched]""",
            "code_java": """import java.util.*;

class CloudStorageBasic {
    private final Map<String, Integer> files = new HashMap<>();

    public boolean addFile(String name, int size) {
        if (files.containsKey(name)) return false;
        files.put(name, size);
        return true;
    }

    public boolean copyFile(String source, String dest) {
        Integer sz = files.get(source);
        if (sz == null || files.containsKey(dest)) return false;
        files.put(dest, sz);
        return true;
    }

    public int getFileSize(String name) {
        return files.getOrDefault(name, -1);
    }

    public List<String> findFile(String prefix, String suffix) {
        List<String> matched = new ArrayList<>();
        for (String k : files.keySet()) {
            if (k.startsWith(prefix) && k.endsWith(suffix)) matched.add(k);
        }
        Collections.sort(matched, (a, b) -> {
            int sa = files.get(a), sb = files.get(b);
            return sa != sb ? sb - sa : a.compareTo(b);
        });
        List<String> out = new ArrayList<>();
        for (String k : matched) out.add(k + "(" + files.get(k) + ")");
        return out;
    }
}"""
        },
        {
            "name": "Optimal: Full 4-Stage ICA Architecture (Quotas, Eviction & Snapshots)",
            "badge": "Optimal",
            "desc": "Complete Levels 1–4 CloudStorage engine with unified FileMeta, owner-preserving copyFile, quota eviction, and collision-safe restoreUser.",
            "time": "O(1) CRUD / O(F log F) search & evict",
            "space": "O(F + B)",
            "code_kotlin": """class CloudStorage {
    private data class FileMeta(val size: Int, val owner: String)
    private class UserState(var capacity: Int) {
        var used: Int = 0
        val owned = HashSet<String>()
        var backup: HashMap<String, Int>? = null
    }

    private val files = HashMap<String, FileMeta>()
    private val users = HashMap<String, UserState>()

    init {
        users["admin"] = UserState(Int.MAX_VALUE)
    }

    fun addFile(name: String, size: Int): Boolean {
        return addFileBy("admin", name, size) != -1
    }

    fun copyFile(source: String, dest: String): Boolean {
        val meta = files[source] ?: return false
        if (files.containsKey(dest)) return false
        return addFileBy(meta.owner, dest, meta.size) != -1
    }

    fun getFileSize(name: String): Int {
        val meta = files[name] ?: return -1
        return meta.size
    }

    fun findFile(prefix: String, suffix: String): List<String> {
        val matched = mutableListOf<String>()
        for (name in files.keys) {
            if (name.startsWith(prefix) && name.endsWith(suffix)) {
                matched.add(name)
            }
        }
        matched.sortWith { a, b ->
            val sa = files[a]!!.size
            val sb = files[b]!!.size
            if (sa != sb) sb - sa else a.compareTo(b)
        }
        val out = mutableListOf<String>()
        for (name in matched) {
            out.add("$name(${files[name]!!.size})")
        }
        return out
    }

    fun addUser(userId: String, capacity: Int): Boolean {
        if (users.containsKey(userId)) return false
        users[userId] = UserState(capacity)
        return true
    }

    fun addFileBy(userId: String, name: String, size: Int): Int {
        val u = users[userId] ?: return -1
        if (files.containsKey(name)) return -1
        if (userId != "admin" && u.used + size > u.capacity) return -1
        files[name] = FileMeta(size, userId)
        u.used += size
        u.owned.add(name)
        return if (userId == "admin") Int.MAX_VALUE else u.capacity - u.used
    }

    fun updateCapacity(userId: String, capacity: Int): Int {
        if (userId == "admin") return -1
        val u = users[userId] ?: return -1
        u.capacity = capacity
        if (u.used <= capacity) return 0
        val candidates = u.owned.toList().toMutableList()
        candidates.sortWith { a, b ->
            val sa = files[a]!!.size
            val sb = files[b]!!.size
            if (sa != sb) sb - sa else b.compareTo(a)
        }
        var evicted = 0
        for (path in candidates) {
            if (u.used <= capacity) break
            removeInternal(path)
            evicted++
        }
        return evicted
    }

    fun backupUser(userId: String): Int {
        if (userId == "admin") return -1
        val u = users[userId] ?: return -1
        val snap = HashMap<String, Int>()
        for (path in u.owned.toList()) {
            snap[path] = files[path]!!.size
        }
        u.backup = snap
        return snap.size
    }

    fun restoreUser(userId: String): Int {
        if (userId == "admin") return -1
        val u = users[userId] ?: return -1
        for (path in u.owned.toList()) {
            removeInternal(path)
        }
        val snap = u.backup ?: return 0
        val paths = snap.keys.toList().sorted()
        var restored = 0
        for (path in paths) {
            val sz = snap[path]!!
            if (!files.containsKey(path) && u.used + sz <= u.capacity) {
                files[path] = FileMeta(sz, userId)
                u.used += sz
                u.owned.add(path)
                restored++
            }
        }
        return restored
    }

    private fun removeInternal(path: String) {
        val meta = files.remove(path) ?: return
        val u = users[meta.owner]
        if (u != null) {
            u.used -= meta.size
            u.owned.remove(path)
        }
    }
}""",
            "code_python": """class CloudStorage:
    def __init__(self):
        self.files = {}
        self.users = {
            "admin": {"cap": float("inf"), "used": 0, "owned": set(), "backup": None}
        }

    def _remove_internal(self, path: str) -> None:
        meta = self.files.pop(path, None)
        if meta is not None:
            u = self.users.get(meta[1])
            if u is not None:
                u["used"] -= meta[0]
                u["owned"].remove(path)

    def addFile(self, name: str, size: int) -> bool:
        return self.addFileBy("admin", name, size) != -1

    def copyFile(self, source: str, dest: str) -> bool:
        if source not in self.files or dest in self.files:
            return False
        meta = self.files[source]
        return self.addFileBy(meta[1], dest, meta[0]) != -1

    def getFileSize(self, name: str) -> int:
        if name not in self.files:
            return -1
        return self.files[name][0]

    def findFile(self, prefix: str, suffix: str) -> list[str]:
        matched = [k for k in self.files.keys() if k.startswith(prefix) and k.endswith(suffix)]
        matched.sort(key=lambda k: [-self.files[k][0], k])
        return [f"{k}({self.files[k][0]})" for k in matched]

    def addUser(self, userId: str, capacity: int) -> bool:
        if userId in self.users:
            return False
        self.users[userId] = {"cap": capacity, "used": 0, "owned": set(), "backup": None}
        return True

    def addFileBy(self, userId: str, name: str, size: int) -> int:
        if userId not in self.users or name in self.files:
            return -1
        u = self.users[userId]
        if userId != "admin" and u["used"] + size > u["cap"]:
            return -1
        self.files[name] = [size, userId]
        u["used"] += size
        u["owned"].add(name)
        return 0 if userId == "admin" else u["cap"] - u["used"]

    def updateCapacity(self, userId: str, capacity: int) -> int:
        if userId == "admin" or userId not in self.users:
            return -1
        u = self.users[userId]
        u["cap"] = capacity
        if u["used"] <= capacity:
            return 0
        cands = list(u["owned"])
        cands.sort(key=lambda k: [self.files[k][0], k], reverse=True)
        evicted = 0
        for path in cands:
            if u["used"] <= capacity:
                break
            self._remove_internal(path)
            evicted += 1
        return evicted

    def backupUser(self, userId: str) -> int:
        if userId == "admin" or userId not in self.users:
            return -1
        u = self.users[userId]
        snap = {}
        for path in list(u["owned"]):
            snap[path] = self.files[path][0]
        u["backup"] = snap
        return len(snap)

    def restoreUser(self, userId: str) -> int:
        if userId == "admin" or userId not in self.users:
            return -1
        u = self.users[userId]
        for path in list(u["owned"]):
            self._remove_internal(path)
        snap = u["backup"]
        if snap is None:
            return 0
        restored = 0
        for path in sorted(snap.keys()):
            sz = snap[path]
            if path not in self.files and u["used"] + sz <= u["cap"]:
                self.files[path] = [sz, userId]
                u["used"] += sz
                u["owned"].add(path)
                restored += 1
        return restored""",
            "code_java": """import java.util.*;

class CloudStorage {
    private static class FileMeta {
        final int size;
        final String owner;
        FileMeta(int size, String owner) { this.size = size; this.owner = owner; }
    }

    private static class UserState {
        int capacity;
        int used = 0;
        final Set<String> owned = new HashSet<>();
        Map<String, Integer> backup = null;
        UserState(int capacity) { this.capacity = capacity; }
    }

    private final Map<String, FileMeta> files = new HashMap<>();
    private final Map<String, UserState> users = new HashMap<>();

    public CloudStorage() {
        users.put("admin", new UserState(Integer.MAX_VALUE));
    }

    public boolean addFile(String name, int size) {
        return addFileBy("admin", name, size) != -1;
    }

    public boolean copyFile(String source, String dest) {
        FileMeta meta = files.get(source);
        if (meta == null || files.containsKey(dest)) return false;
        return addFileBy(meta.owner, dest, meta.size) != -1;
    }

    public int getFileSize(String name) {
        FileMeta meta = files.get(name);
        return meta == null ? -1 : meta.size;
    }

    public List<String> findFile(String prefix, String suffix) {
        List<String> matched = new ArrayList<>();
        for (String name : files.keySet()) {
            if (name.startsWith(prefix) && name.endsWith(suffix)) matched.add(name);
        }
        Collections.sort(matched, (a, b) -> {
            int sa = files.get(a).size, sb = files.get(b).size;
            return sa != sb ? sb - sa : a.compareTo(b);
        });
        List<String> out = new ArrayList<>();
        for (String name : matched) out.add(name + "(" + files.get(name).size + ")");
        return out;
    }

    public boolean addUser(String userId, int capacity) {
        if (users.containsKey(userId)) return false;
        users.put(userId, new UserState(capacity));
        return true;
    }

    public int addFileBy(String userId, String name, int size) {
        UserState u = users.get(userId);
        if (u == null || files.containsKey(name)) return -1;
        if (!"admin".equals(userId) && u.used + size > u.capacity) return -1;
        files.put(name, new FileMeta(size, userId));
        u.used += size;
        u.owned.add(name);
        return "admin".equals(userId) ? Integer.MAX_VALUE : u.capacity - u.used;
    }

    public int updateCapacity(String userId, int capacity) {
        if ("admin".equals(userId)) return -1;
        UserState u = users.get(userId);
        if (u == null) return -1;
        u.capacity = capacity;
        if (u.used <= capacity) return 0;
        List<String> cands = new ArrayList<>(u.owned);
        Collections.sort(cands, (a, b) -> {
            int sa = files.get(a).size, sb = files.get(b).size;
            return sa != sb ? sb - sa : b.compareTo(a);
        });
        int evicted = 0;
        for (String path : cands) {
            if (u.used <= capacity) break;
            removeInternal(path);
            evicted++;
        }
        return evicted;
    }

    public int backupUser(String userId) {
        if ("admin".equals(userId)) return -1;
        UserState u = users.get(userId);
        if (u == null) return -1;
        Map<String, Integer> snap = new HashMap<>();
        for (String path : u.owned) snap.put(path, files.get(path).size);
        u.backup = snap;
        return snap.size();
    }

    public int restoreUser(String userId) {
        if ("admin".equals(userId)) return -1;
        UserState u = users.get(userId);
        if (u == null) return -1;
        for (String path : new ArrayList<>(u.owned)) removeInternal(path);
        if (u.backup == null) return 0;
        List<String> paths = new ArrayList<>(u.backup.keySet());
        Collections.sort(paths);
        int restored = 0;
        for (String path : paths) {
            int sz = u.backup.get(path);
            if (!files.containsKey(path) && u.used + sz <= u.capacity) {
                files.put(path, new FileMeta(sz, userId));
                u.used += sz;
                u.owned.add(path);
                restored++;
            }
        }
        return restored;
    }

    private void removeInternal(String path) {
        FileMeta meta = files.remove(path);
        if (meta == null) return;
        UserState u = users.get(meta.owner);
        if (u != null) {
            u.used -= meta.size;
            u.owned.remove(path);
        }
    }
}"""
        }
    ],
    "test_cases": [
        {
            "mode": "oop",
            "ops": ["CloudStorage", "addFile", "addFile", "copyFile", "getFileSize", "getFileSize"],
            "args": [[], ["/dir/a.txt", 10], ["/dir/a.txt", 20], ["/dir/a.txt", "/dir/b.txt"], ["/dir/b.txt"], ["/missing"]],
            "expected": [None, True, False, True, 10, -1]
        },
        {
            "mode": "oop",
            "ops": ["CloudStorage", "addFile", "addFile", "addFile", "findFile"],
            "args": [[], ["/d/b.log", 20], ["/d/a.log", 20], ["/d/c.log", 50], ["/d/", ".log"]],
            "expected": [None, True, True, True, ["/d/c.log(50)", "/d/a.log(20)", "/d/b.log(20)"]]
        },
        {
            "mode": "oop",
            "ops": ["CloudStorage", "addUser", "addFileBy", "addFileBy", "copyFile", "updateCapacity", "getFileSize", "getFileSize"],
            "args": [[], ["u1", 100], ["u1", "/a", 40], ["u1", "/b", 40], ["/a", "/c"], ["u1", 50], ["/b"], ["/a"]],
            "expected": [None, True, 60, 20, False, 1, -1, 40]
        },
        {
            "mode": "oop",
            "ops": ["CloudStorage", "addUser", "addFileBy", "addFileBy", "backupUser", "updateCapacity", "addFile", "updateCapacity", "restoreUser", "getFileSize", "getFileSize"],
            "args": [[], ["u1", 100], ["u1", "/a", 40], ["u1", "/b", 40], ["u1"], ["u1", 0], ["/b", 10], ["u1", 100], ["u1"], ["/a"], ["/b"]],
            "expected": [None, True, 60, 20, 2, 2, True, 0, 1, 40, 10]
        }
    ],
    "spoken_script": {
        "phase1": "In a 4-stage CodeSignal Cloud Storage ICA, I read Stage 1 carefully and design my model so Stage 3 quotas and Stage 4 backups plug straight in: `FileMeta(size, owner)` with a built-in `'admin'` user holding unlimited capacity.",
        "phase2": "By routing `addFile` and `copyFile` directly through `addFileBy(owner, path, size)`, Stage 3's hidden requirement—that `copyFile` preserves file ownership AND enforces that owner's quota—works automatically with zero duplicate logic!",
        "phase3": "Notice the two tie-breakers: Stage 2 `findFile` sorts by `(-size, +name)`, whereas Stage 3 `updateCapacity` evicts largest files tie-broken by `(-size, -name)`! And in Stage 4 `restoreUser`, I snapshot `u.owned` before iterating `removeInternal` to avoid `ConcurrentModificationException`, then skip any path claimed by another user.",
        "phase4": "Tracing `restoreUser` after `admin` created `'/b'`: `u1`'s current files are cleared, `'/a'` restores cleanly, and `'/b'` is skipped because `files.containsKey('/b')` is true!",
        "mid_followup": "If Stage 2 `findFile(prefix, suffix)` becomes the bottleneck over 10^5 paths, index paths inside a prefix `Trie` (for `prefix`) intersected or filtered by `endsWith(suffix)`."
    },
    "quiz_questions": [
        {
            "category": "Hidden Stage 3 Regression (`copyFile` Ownership)",
            "prompt": "When Stage 3 adds multi-tenant quotas, what hidden regression fails if `copyFile` still delegates to `addFile(dest, size)` (`\"admin\"`)?",
            "codeSnippet": "fun copyFile(source: String, dest: String): Boolean {\n    val meta = files[source] ?: return false\n    return addFile(dest, meta.size) // <-- BUG IN STAGE 3!\n}",
            "options": [
                {"id": "A", "text": "Copied user files bypass the owner's quota limit and wrongly transfer ownership to admin!", "isCorrect": True, "whyRight": "In CodeSignal's Cloud Storage spec, copying a user-owned file keeps the same owner and must fail if that owner lacks remaining capacity!"},
                {"id": "B", "text": "copyFile overwrites existing destination files instead of returning false.", "isCorrect": False, "whyWrong": "addFile still checks containsKey(dest)."},
                {"id": "C", "text": "getFileSize returns negative values on copied files.", "isCorrect": False, "whyWrong": "File size remains meta.size."},
                {"id": "D", "text": "findFile stops matching copied files by suffix.", "isCorrect": False, "whyWrong": "Suffix matching only inspects path strings."}
            ]
        },
        {
            "category": "ConcurrentModificationException Bug (`restoreUser`)",
            "prompt": "Why does iterating `for (path in u.owned) removeInternal(path)` directly crash in Java/Kotlin/Python?",
            "codeSnippet": "for (path in u.owned) {\n    removeInternal(path) // calls u.owned.remove(path)!\n}",
            "options": [
                {"id": "A", "text": "Mutating HashSet `u.owned` while iterating it throws ConcurrentModificationException / RuntimeError!", "isCorrect": True, "whyRight": "Always iterate a snapshot copy (`u.owned.toList()` / `new ArrayList<>(u.owned)` / `list(u['owned'])`) when removing elements!"},
                {"id": "B", "text": "HashSet iteration visits paths in reverse alphabetical order.", "isCorrect": False, "whyWrong": "Purging all owned files is order-independent; the crash is structural modification during iteration."},
                {"id": "C", "text": "removeInternal increases u.used instead of decreasing it.", "isCorrect": False, "whyWrong": "removeInternal subtracts meta.size properly."},
                {"id": "D", "text": "Admin files also get deleted when purging a tenant user.", "isCorrect": False, "whyWrong": "u.owned only contains that specific user's files."}
            ]
        },
        {
            "category": "Tie-Breaker Contrast (`findFile` vs `updateCapacity`)",
            "prompt": "Compare the tie-breakers when two files have equal size: `findFile` vs `updateCapacity` eviction.",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "findFile orders equal-size names ascending (`a` before `b`); eviction drops lexicographically largest (`b` before `a`)!", "isCorrect": True, "whyRight": "Misreading tie-breaker polarity between Stage 2 and Stage 3 is one of the most common ICA pitfalls."},
                {"id": "B", "text": "Both Stage 2 and Stage 3 sort file sizes in ascending order.", "isCorrect": False, "whyWrong": "Both prioritize largest file sizes first."},
                {"id": "C", "text": "updateCapacity evicts smallest files first to maximize evicted file count.", "isCorrect": False, "whyWrong": "Eviction removes largest files first to minimize evicted file count."},
                {"id": "D", "text": "Tie-breaking is undefined in CodeSignal ICA tests.", "isCorrect": False, "whyWrong": "CodeSignal ICA tests strictly assert deterministic tie-breaking."}
            ]
        },
        {
            "category": "Stage 4 Collision Semantics (`restoreUser`)",
            "prompt": "During `restoreUser(\"u1\")`, what must happen if backed-up path `\"/report.csv\"` is currently owned by `\"u2\"`?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Skip `\"/report.csv\"` leaving `\"u2\"`'s file intact, and continue restoring `\"u1\"`'s other non-colliding files.", "isCorrect": True, "whyRight": "User-scoped restore cannot overwrite or delete another tenant's live file, nor does one collision abort the rest of the restore."},
                {"id": "B", "text": "Delete `\"u2\"`'s file and overwrite it with `\"u1\"`'s backed-up copy.", "isCorrect": False, "whyWrong": "Tenants cannot clobber files owned by other users."},
                {"id": "C", "text": "Abort the entire restoreUser call and return -1.", "isCorrect": False, "whyWrong": "-1 is only returned when userId does not exist."},
                {"id": "D", "text": "Rename `\"/report.csv\"` to `\"/report_copy.csv\"` automatically.", "isCorrect": False, "whyWrong": "Paths are never auto-renamed during restore."}
            ]
        }
    ]
}


ALL_NEW_117_126 = [
    (p117, "lessons/0117-lc242-valid-anagram.html"),
    (p118, "lessons/0118-lc252-meeting-rooms.html"),
    (p119, "lessons/0119-lc733-flood-fill.html"),
    (p120, "lessons/0120-lc703-kth-largest-element-in-a-stream.html"),
    (p121, "lessons/0121-lc40-combination-sum-ii.html"),
    (p122, "lessons/0122-lc17-letter-combinations-of-a-phone-number.html"),
    (p123, "lessons/0123-lc721-accounts-merge.html"),
    (p124, "lessons/0124-lc36-valid-sudoku.html"),
    (p125, "lessons/0125-lc692-top-k-frequent-words.html"),
    (p126, "lessons/0126-custom-cloud-storage-ica.html"),
]


def main():
    for p_def, out_path in ALL_NEW_117_126:
        html_doc = render_lesson_v2(p_def)
        with open(out_path, "w", encoding="utf-8") as f:
            f.write(html_doc)
        print(f"✓ Rendered {out_path}")
    print(f"🎉 Rendered all {len(ALL_NEW_117_126)} new 2027 lessons (0117..0126)!")


if __name__ == "__main__":
    main()
