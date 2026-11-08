import sys
sys.path.append('scripts')

p63 = {
    'num': 63,
    'lc': 394,
    'title': "Decode String",
    'category': "Stacks, Queues & Monotonic Stacks",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "decode-string",
    'verbatim_statement': """<p>Given an encoded string, return its decoded string.</p>
<p>The encoding rule is: <code>k[encoded_string]</code>, where the <code>encoded_string</code> inside the square brackets is being repeated exactly <code>k</code> times. Note that <code>k</code> is guaranteed to be a positive integer.</p>
<p>You may assume that the input string is always valid; there are no extra white spaces, square brackets are well-formed, etc. Furthermore, you may assume that the original data does not contain any digits and that digits are only for those repeat numbers, <code>k</code>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 's = "3[a]2[bc]"', 'output': '"aaabcbc"'},
        {'title': "Example 2", 'input': 's = "3[a2[c]]"', 'output': '"accaccacc"'},
        {'title': "Example 3", 'input': 's = "2[abc]3[cd]ef"', 'output': '"abcabccdcdcdef"'}
    ],
    'constraints': [
        "1 <= s.length <= 30",
        "s consists of lowercase English letters, digits, and square brackets '[]'.",
        "s is guaranteed to be a valid input.",
        "All the integers in s are in the range [1, 300]."
    ],
    'plain_english': "Maintain two stacks or recurse when opening bracket '[' is met: a count stack for repeat multipliers (k) and a string stack for parent string buffers. When encountering ']', pop the count and parent string, repeat current buffer k times, and append to the parent string. Digits accumulate into multi-digit numbers, and characters append to current buffer.",
    'boundary_checks': {
        'min_base': "No brackets <code>s = \"leetcode\"</code> &rarr; Returns \"leetcode\" directly.",
        'best_exit': "Single repeat <code>s = \"3[a]\"</code> &rarr; Returns \"aaa\".",
        'worst_scan': "Deeply nested multipliers <code>s = \"2[3[a2[b]]]\"</code> &rarr; Inner brackets resolve first before outer multiplication."
    },
    'mental_model': {
        'analogy_title': "Russian Nesting Boxes with Stencils",
        'analogy_text': "Imagine building rubber stamp patterns. Whenever you see a number followed by '[', you pause your current canvas, write down the multiplier, and put your current canvas in a box on top of a stack. You take out a fresh canvas for what's inside the brackets. When you hit ']', you stamp your current canvas k times onto the old canvas you pop back out from the box.",
        'aha_moment': "Nested brackets require LIFO evaluation (innermost must be resolved before outermost can be multiplied). Two parallel stacks (or a single call stack in recursion) store the snapshot of the outer world while the inner expression is assembled.",
        'trigger': "Nested string multipliers with brackets `k[...]` &rarr; Two Stacks (countStack and stringStack) or Recursive Parsing.",
        'sisters': [
            {'name': "Basic Calculator II", 'lc': "LC #227"},
            {'name': "Flatten Nested List Iterator", 'lc': "LC #341"},
            {'name': "Number of Atoms", 'lc': "LC #726"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(maxK^depth * N) Time, O(N) Space",
        'naive_text': "Regex string replacement iteratively finding innermost bracket pattern `\\d+\\[[a-z]+\\]` and replacing in-place.",
        'wasted_work': "Repeatedly allocating new strings and re-parsing from start on every bracket reduction ($O(N^2)$ string copies).",
        'optimal_complexity': "O(Output Length) Time, O(Output Length) Space",
        'optimal_text': "Single pass with CountStack and StringStack. Each character pushed and popped once; output built directly with StringBuilder.",
        'core_invariant': "At any point, `currentStr` holds the decoded content for the current nesting level, and `strStack.peek()` holds the accumulated prefix from the enclosing scope.",
        'table_rows': [
            {'name': "1. Iterative Regex Reduction", 'time': "O(D * N^2)", 'space': "O(N)", 'note': "High string allocation and regex matching overhead."},
            {'name': "2. Recursive Descent Parser", 'time': "O(L)", 'space': "O(D)", 'note': "Elegant but uses call stack proportional to bracket depth."},
            {'name': "★ 3. Two-Stack LIFO Parser", 'time': "O(L)", 'space': "O(L)", 'note': "Production standard; explicit memory bounds with StringBuilder."}
        ]
    },
    'trace_table': {
        'input_desc': 's = "3[a2[c]]"',
        'columns': ["Char", "Current k", "Current str", "countStack", "strStack", "Action Description"],
        'rows': [
            ["'3'", "3", "\"\"", "[]", "[]", "k accumulated to 3"],
            ["'['", "0", "\"\"", "[3]", "[\"\"]", "Push 3 and \"\" to stacks; reset for inner scope"],
            ["'a'", "0", "\"a\"", "[3]", "[\"\"]", "Append 'a' to current string"],
            ["'2'", "2", "\"a\"", "[3]", "[\"\"]", "k accumulated to 2"],
            ["'['", "0", "\"\"", "[3, 2]", "[\"\", \"a\"]", "Push 2 and \"a\" to stacks; reset for inner scope"],
            ["'c'", "0", "\"c\"", "[3, 2]", "[\"\", \"a\"]", "Append 'c' to current string"],
            ["']'", "0", "\"acc\"", "[3]", "[\"\"]", "Pop 2 and \"a\": \"a\" + \"c\"*2 = \"acc\""],
            ["']'", "0", "\"accaccacc\"", "[]", "[]", "Pop 3 and \"\": \"\" + \"acc\"*3 = \"accaccacc\""]
        ],
        'takeaway': "Innermost bracket \"2[c]\" resolves to \"cc\" first, yielding \"acc\", which is then multiplied 3 times to produce \"accaccacc\"."
    },
    'stepper': {
        'title': "Two-Stack Bracket Multiplier Stepper",
        'default_input': '3[a2[c]]',
        'presets': [
            {'label': "Nested 3[a2[c]]", 'onclick': 'loadPreset("3[a2[c]]")'},
            {'label': "Sequential 3[a]2[bc]", 'onclick': 'loadPreset("3[a]2[bc]")'},
            {'label': "Trailing 2[abc]3[cd]ef", 'onclick': 'loadPreset("2[abc]3[cd]ef")'}
        ],
        'metrics': [
            {'id': "metric-char", 'label': "Processing Char", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-k", 'label': "Current k", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-curr-str", 'label': "Current Buffer", 'color': "text-emerald-400", 'initial': '""'},
            {'id': "metric-stacks", 'label': "Stack Depths", 'color': "text-purple-400", 'initial': "0 / 0"}
        ],
        'visual_html': '<div id="p394-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace stack push/pop bracket operations.',
        'pseudocode_lines': [
            "val countStack = ArrayDeque<Int>()",
            "val strStack = ArrayDeque<StringBuilder>()",
            "for (ch in s) {",
            "    if (ch.isDigit()) k = k * 10 + (ch - '0')",
            "    else if (ch == '[') { countStack.push(k); strStack.push(currStr); k=0; currStr = StringBuilder() }",
            "    else if (ch == ']') { currStr = strStack.pop().append(currStr.repeat(countStack.pop())) }",
            "    else currStr.append(ch)",
            "}"
        ],
        'js_code': """
    let rawStr = "3[a2[c]]";

    function buildSteps(str) {
      steps = [];
      const countStack = [];
      const strStack = [];
      let currStr = "";
      let k = 0;

      steps.push({
        char: "-", k: 0, currStr: '""', depths: "0 / 0",
        countStack: [], strStack: [], currStrRaw: "", line: 1,
        narration: `Ready to decode string "${str}". Initialized empty stacks.`
      });

      for (let i = 0; i < str.length; i++) {
        const ch = str[i];
        if (ch >= '0' && ch <= '9') {
          k = k * 10 + parseInt(ch);
          steps.push({
            char: `'${ch}'`, k: k, currStr: `"${currStr}"`, depths: `${countStack.length} / ${strStack.length}`,
            countStack: [...countStack], strStack: [...strStack], currStrRaw: currStr, line: 4,
            narration: `Parsed digit '${ch}'. Current multiplier k updated to ${k}.`
          });
        } else if (ch === '[') {
          countStack.push(k);
          strStack.push(currStr);
          steps.push({
            char: "'['", k: 0, currStr: '""', depths: `${countStack.length} / ${strStack.length}`,
            countStack: [...countStack], strStack: [...strStack], currStrRaw: "", line: 5,
            narration: `Hit '[': Pushed multiplier ${k} to countStack and parent string "${currStr}" to strStack. Reset buffer.`
          });
          k = 0;
          currStr = "";
        } else if (ch === ']') {
          const repeatCount = countStack.pop();
          const parent = strStack.pop();
          const repeated = currStr.repeat(repeatCount);
          currStr = parent + repeated;
          steps.push({
            char: "']'", k: 0, currStr: `"${currStr}"`, depths: `${countStack.length} / ${strStack.length}`,
            countStack: [...countStack], strStack: [...strStack], currStrRaw: currStr, line: 6,
            narration: `Hit ']': Popped multiplier ${repeatCount} and parent "${parent}". Expanded: "${parent}" + "${repeated}" = "${currStr}".`
          });
        } else {
          currStr += ch;
          steps.push({
            char: `'${ch}'`, k: k, currStr: `"${currStr}"`, depths: `${countStack.length} / ${strStack.length}`,
            countStack: [...countStack], strStack: [...strStack], currStrRaw: currStr, line: 7,
            narration: `Appended letter '${ch}' to current buffer: "${currStr}".`
          });
        }
      }

      steps.push({
        char: "Done", k: 0, currStr: `"${currStr}"`, depths: "0 / 0",
        countStack: [], strStack: [], currStrRaw: currStr, line: 8,
        narration: `Finished! Fully decoded result is "${currStr}".`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p394-stage');
      if (!stage) return;
      let html = '<div class="grid grid-cols-1 md:grid-cols-2 gap-4 w-full max-w-2xl">';

      // Stacks visualization
      html += '<div class="p-3 rounded-lg bg-slate-900 border border-slate-800 space-y-2">';
      html += '<span class="text-xs font-mono text-cyan-400 font-bold block">Count Stack &bull; String Stack</span>';
      if (s.countStack.length === 0) {
        html += '<div class="text-xs font-mono text-slate-500 italic">Stacks empty (root scope)</div>';
      } else {
        html += '<div class="flex flex-col-reverse gap-1">';
        for (let i = 0; i < s.countStack.length; i++) {
          html += `<div class="p-1.5 rounded bg-slate-950 border border-slate-700 text-xs font-mono flex justify-between"><span class="text-amber-400 font-bold">${s.countStack[i]}x</span><span class="text-purple-300">"${s.strStack[i]}"</span></div>`;
        }
        html += '</div>';
      }
      html += '</div>';

      // Current Buffer
      html += '<div class="p-3 rounded-lg bg-slate-900 border border-slate-800 space-y-2 flex flex-col justify-center">';
      html += '<span class="text-xs font-mono text-emerald-400 font-bold block">Active Working Buffer:</span>';
      html += `<div class="p-2.5 rounded bg-slate-950 border border-emerald-500/40 text-emerald-300 font-mono text-sm font-bold break-all">${s.currStrRaw || '<span class="text-slate-600 italic">empty</span>'}</div>`;
      html += '</div>';

      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-char').textContent = s.char;
      document.getElementById('metric-k').textContent = s.k;
      document.getElementById('metric-curr-str').textContent = s.currStr;
      document.getElementById('metric-stacks').textContent = s.depths;
    }

    function loadPreset(str) {
      rawStr = str;
      buildSteps(rawStr);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Recursive Descent Parsing",
            'badge': "Alternative",
            'color': "cyan",
            'complexity': "O(L) Time, O(D) Space",
            'summary': "Advance a global index pointer. Recurse whenever '[' is encountered, and return when ']' or end of string is reached.",
            'kotlin_code': """class Solution {
    private var idx = 0

    fun decodeString(s: String): String {
        idx = 0
        return helper(s)
    }

    private fun helper(s: String): String {
        val sb = java.lang.StringBuilder()
        var k = 0

        while (idx < s.length) {
            val ch = s[idx++]
            when {
                ch.isDigit() -> k = k * 10 + (ch - '0')
                ch == '[' -> {
                    val inner = helper(s)
                    repeat(k) { sb.append(inner) }
                    k = 0
                }
                ch == ']' -> return sb.toString()
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }
}""",
            'python_code': """class Solution:
    def decodeString(self, s: str) -> str:
        self.idx = 0

        def helper() -> str:
            res = []
            k = 0
            while self.idx < len(s):
                ch = s[self.idx]
                self.idx += 1
                if ch.isdigit():
                    k = k * 10 + int(ch)
                elif ch == '[':
                    inner = helper()
                    res.append(inner * k)
                    k = 0
                elif ch == ']':
                    return "".join(res)
                else:
                    res.append(ch)
            return "".join(res)

        return helper()""",
            'java_code': """class Solution {
    private int idx = 0;

    public String decodeString(String s) {
        idx = 0;
        return helper(s);
    }

    private String helper(String s) {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        while (idx < s.length()) {
            char ch = s.charAt(idx++);
            if (Character.isDigit(ch)) {
                k = k * 10 + (ch - '0');
            } else if (ch == '[') {
                String inner = helper(s);
                while (k-- > 0) sb.append(inner);
                k = 0;
            } else if (ch == ']') {
                return sb.toString();
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}""",
            'memory_breakdown': "O(D) call stack space where D is max bracket depth. Total output length is L. Very clean but relies on implicit recursive call stack."
        },
        {
            'name': "★ Approach 2: Two-Stack Iterative Parser (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(L) Time, O(L) Space",
            'summary': "Maintain countStack for repeat factors and stringStack for parent prefixes. Linear pass over characters, resolving brackets strictly LIFO.",
            'kotlin_code': """import java.util.ArrayDeque

class Solution {
    fun decodeString(s: String): String {
        val countStack = ArrayDeque<Int>()
        val strStack = ArrayDeque<StringBuilder>()
        var currStr = StringBuilder()
        var k = 0

        for (ch in s) {
            when {
                ch.isDigit() -> k = k * 10 + (ch - '0')
                ch == '[' -> {
                    countStack.push(k)
                    strStack.push(currStr)
                    currStr = StringBuilder()
                    k = 0
                }
                ch == ']' -> {
                    val repeatCount = countStack.pop()
                    val parent = strStack.pop()
                    val decoded = currStr.toString()
                    repeat(repeatCount) { parent.append(decoded) }
                    currStr = parent
                }
                else -> currStr.append(ch)
            }
        }
        return currStr.toString()
    }
}""",
            'python_code': """class Solution:
    def decodeString(self, s: str) -> str:
        count_stack = []
        str_stack = []
        curr_str = ""
        k = 0

        for ch in s:
            if ch.isdigit():
                k = k * 10 + int(ch)
            elif ch == '[':
                count_stack.append(k)
                str_stack.append(curr_str)
                curr_str = ""
                k = 0
            elif ch == ']':
                repeat_count = count_stack.pop()
                parent = str_stack.pop()
                curr_str = parent + curr_str * repeat_count
            else:
                curr_str += ch

        return curr_str""",
            'java_code': """import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public String decodeString(String s) {
        Deque<Integer> countStack = new ArrayDeque<>();
        Deque<StringBuilder> strStack = new ArrayDeque<>();
        StringBuilder currStr = new StringBuilder();
        int k = 0;

        for (char ch : s.toCharArray()) {
            if (Character.isDigit(ch)) {
                k = k * 10 + (ch - '0');
            } else if (ch == '[') {
                countStack.push(k);
                strStack.push(currStr);
                currStr = new StringBuilder();
                k = 0;
            } else if (ch == ']') {
                int repeatCount = countStack.pop();
                StringBuilder parent = strStack.pop();
                String inner = currStr.toString();
                while (repeatCount-- > 0) {
                    parent.append(inner);
                }
                currStr = parent;
            } else {
                currStr.append(ch);
            }
        }
        return currStr.toString();
    }
}""",
            'memory_breakdown': "O(L) space where L is the decoded string size. ArrayDeque provides fast allocation with zero recursion risk. Strict linear execution."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """import java.util.ArrayDeque

class Solution {
    fun decodeString(s: String): String {
        // Write your optimal two-stack solution here
        val countStack = ArrayDeque<Int>()
        val strStack = ArrayDeque<StringBuilder>()
        var curr = StringBuilder()
        var k = 0
        for (ch in s) {
            when {
                ch.isDigit() -> k = k * 10 + (ch - '0')
                ch == '[' -> {
                    countStack.push(k)
                    strStack.push(curr)
                    curr = StringBuilder()
                    k = 0
                }
                ch == ']' -> {
                    val count = countStack.pop()
                    val parent = strStack.pop()
                    val inner = curr.toString()
                    repeat(count) { parent.append(inner) }
                    curr = parent
                }
                else -> curr.append(ch)
            }
        }
        return curr.toString()
    }
}""",
        'python_boilerplate': """class Solution:
    def decodeString(self, s: str) -> str:
        # Write your optimal two-stack solution here
        count_stack = []
        str_stack = []
        curr = ""
        k = 0
        for ch in s:
            if ch.isdigit():
                k = k * 10 + int(ch)
            elif ch == '[':
                count_stack.append(k)
                str_stack.append(curr)
                curr = ""
                k = 0
            elif ch == ']':
                cnt = count_stack.pop()
                parent = str_stack.pop()
                curr = parent + curr * cnt
            else:
                curr += ch
        return curr""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public String decodeString(String s) {
        // Write your optimal two-stack solution here
        Deque<Integer> countStack = new ArrayDeque<>();
        Deque<StringBuilder> strStack = new ArrayDeque<>();
        StringBuilder curr = new StringBuilder();
        int k = 0;
        for (char ch : s.toCharArray()) {
            if (Character.isDigit(ch)) {
                k = k * 10 + (ch - '0');
            } else if (ch == '[') {
                countStack.push(k);
                strStack.push(curr);
                curr = new StringBuilder();
                k = 0;
            } else if (ch == ']') {
                int cnt = countStack.pop();
                StringBuilder parent = strStack.pop();
                String inner = curr.toString();
                while (cnt-- > 0) parent.append(inner);
                curr = parent;
            } else {
                curr.append(ch);
            }
        }
        return curr.toString();
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(s) {
        const countStack = [];
        const strStack = [];
        let curr = '';
        let k = 0;
        for (let i = 0; i < s.length; i++) {
          const ch = s[i];
          if (ch >= '0' && ch <= '9') {
            k = k * 10 + parseInt(ch);
          } else if (ch === '[') {
            countStack.push(k);
            strStack.push(curr);
            curr = '';
            k = 0;
          } else if (ch === ']') {
            const cnt = countStack.pop();
            const parent = strStack.pop();
            curr = parent + curr.repeat(cnt);
          } else {
            curr += ch;
          }
        }
        return curr;
      }

      const tests = [
        { input: "3[a]2[bc]", expected: "aaabcbc" },
        { input: "3[a2[c]]", expected: "accaccacc" },
        { input: "2[abc]3[cd]ef", expected: "abcabccdcdcdef" },
        { input: "leetcode", expected: "leetcode" }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.input);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: s="${t.input}" &rarr; Got "${actual}", Expected "${t.expected}" (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: brackets are guaranteed to be well-formed, integers are in range 1 to 300, and digits are only for multiplier factors. Multi-digit numbers like '100[a]' can occur, and brackets can be nested to arbitrary depth.\"",
        'phase2': "\"Because brackets can be nested, we must evaluate inner brackets before outer multipliers can be applied. This is a classic Last-In, First-Out (LIFO) pattern. I will use two stacks: a countStack for integers and a strStack for parent string buffers. As I iterate: if I see a digit, I accumulate `k = k * 10 + digit`. If I see '[', I push k to countStack, push the current string buffer to strStack, and reset both. If I see a character, I append it. If I see ']', I pop the count and parent string, repeat the current buffer that many times, and prepend the parent string.\"",
        'phase3': "\"Let me write the code. I create `countStack` and `strStack`. Maintain `currStr = StringBuilder()` and `k = 0`. Loop through characters with a when/switch statement. On ']', pop repeatCount, pop parent, append repeatCount times to parent, and reassign `currStr = parent`. Return `currStr.toString()` at the end.\"",
        'phase4': "\"Tracing with `\"3[a2[c]]\"`. At 3: k=3. At '[': push 3, push \"\". At 'a': curr=\"a\". At 2: k=2. At '[': push 2, push \"a\". At 'c': curr=\"c\". At first ']': pop 2, pop \"a\" &rarr; \"a\" + \"c\"*2 = \"acc\". At second ']': pop 3, pop \"\" &rarr; \"\" + \"acc\"*3 = \"accaccacc\". Result is correct. Empty brackets or plain strings without brackets return cleanly.\"",
        'senior_followup': "\"In large-scale string template engines (like Jinja or Mustache), an Abstract Syntax Tree (AST) parser with recursive tokens is preferred over raw stacks to allow syntax linting, custom macros, and streaming rendering.\""
    },
    'quiz': [
        {
            'question': "Why are two stacks (or a stack of tuples) required instead of a single string?",
            'options': [
                "Nested brackets require preserving the enclosing prefix while inner content resolves",
                "A single stack cannot store both unicode characters and primitive integer values",
                "Operating system thread safety requires splitting numbers from character arrays",
                "The garbage collector crashes if strings and numbers share a single stack frame"
            ],
            'correct_index': 0,
            'explanation': "When entering an inner bracket, the accumulated parent string and multiplier must be saved so the inner expression can be computed in isolation, then multiplied and appended to the parent."
        },
        {
            'question': "How should multi-digit numbers like 100 in '100[a]' be accumulated?",
            'options': [
                "k = k * 10 + (ch - '0')",
                "k = k + (ch - '0') * 10",
                "k = Math.max(k, (int)ch)",
                "k = (k << 1) + (ch - '0')"
            ],
            'correct_index': 0,
            'explanation': "Decimal digits are read from left to right, so shifting existing digits by multiplying by 10 and adding the new digit `ch - '0'` correctly parses multi-digit integers."
        }
    ]
}

p64 = {
    'num': 64,
    'lc': 227,
    'title': "Basic Calculator II",
    'category': "Stacks, Queues & Monotonic Stacks",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "basic-calculator-ii",
    'verbatim_statement': """<p>Given a string <code>s</code> which represents an expression, <em>evaluate this expression and return its value</em>.&nbsp;</p>
<p>The integer division should truncate toward zero.</p>
<p>You may assume that the given expression is always valid. All intermediate results will be in the range of <code>[-2^31, 2^31 - 1]</code>.</p>
<p><strong>Note:</strong> You are not allowed to use any built-in function which evaluates strings as mathematical expressions, such as <code>eval()</code>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 's = "3+2*2"', 'output': '7'},
        {'title': "Example 2", 'input': 's = " 3/2 "', 'output': '1'},
        {'title': "Example 3", 'input': 's = " 3+5 / 2 "', 'output': '5'}
    ],
    'constraints': [
        "1 <= s.length <= 3 * 10^5",
        "s consists of integers and operators ('+', '-', '*', '/') separated by some number of spaces.",
        "s represents a valid expression.",
        "All the integers in the expression are non-negative integers in the range [0, 2^31 - 1]."
    ],
    'plain_english': "Process the string character by character. Maintain the previous operator (initialized to '+') and the current number being formed. When a new operator or the end of the string is reached, apply the PREVIOUS operator: for '+' push num; for '-' push -num; for '*' or '/' pop top of stack, multiply or divide by num, and push result back. Finally, sum all numbers on the stack.",
    'boundary_checks': {
        'min_base': "Single number <code>s = \"42\"</code> &rarr; Returns 42 directly.",
        'best_exit': "Spaces everywhere <code>s = \" 3 + 5 / 2 \"</code> &rarr; Ignores whitespace cleanly.",
        'worst_scan': "Long chained division <code>s = \"14-3/2\"</code> &rarr; Integer division truncates 3/2 to 1; 14 - 1 = 13."
    },
    'mental_model': {
        'analogy_title': "The Immediate Multiplication Workbench",
        'analogy_text': "Imagine an accountant entering transactions. Addition and subtraction can wait—you just write them on an invoice with their positive or negative signs. But multiplication and division are urgent contracts: as soon as you see a '*' or '/', you take the last number you wrote down, multiply or divide it immediately, and replace it on the invoice. At the end of the day, you simply sum up the invoice.",
        'aha_moment': "Operator precedence means '*' and '/' have higher priority than '+' and '-'. By deferring addition and subtraction onto a stack (or running sum) and resolving '*' and '/' immediately against the last operand, we avoid any complex parsing or recursion.",
        'trigger': "Arithmetic expression without parentheses, mixed operator precedence (+, -, *, /) &rarr; Stack or Two-Variable Deferred Sum.",
        'sisters': [
            {'name': "Basic Calculator (with Parentheses)", 'lc': "LC #224"},
            {'name': "Basic Calculator III (+, -, *, /, ())", 'lc': "LC #772"},
            {'name': "Evaluate Reverse Polish Notation", 'lc': "LC #150"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N) Time, O(N) Space",
        'naive_text': "Tokenize the expression into a list of numbers and operators, then make two passes: first pass evaluates * and /, second pass evaluates + and -.",
        'wasted_work': "Allocating intermediate token lists and multiple array passes ($2N$ string and token objects).",
        'optimal_complexity': "O(N) Time, O(1) Auxiliary Space",
        'optimal_text': "Single pass keeping `lastNum` and `totalSum`. Evaluates high-precedence operators on-the-fly without an explicit stack.",
        'core_invariant': "At each operator boundary, `lastNum` holds the value of the most recent additive term. When '*' or '/' arrives, `lastNum` is updated in-place; when '+' or '-' arrives, `lastNum` is finalized into `totalSum`.",
        'table_rows': [
            {'name': "1. Two-Pass Tokenizer", 'time': "O(N)", 'space': "O(N)", 'note': "Allocates tokens list and intermediate reduced lists."},
            {'name': "2. Explicit Stack", 'time': "O(N)", 'space': "O(N)", 'note': "Pushes all additive terms to stack, then sums at end."},
            {'name': "★ 3. O(1) Space Running Accumulator", 'time': "O(N)", 'space': "O(1)", 'note': "Tracks lastNumber and currentSum; zero extra heap allocations."}
        ]
    },
    'trace_table': {
        'input_desc': 's = "3 + 5 / 2 * 4"',
        'columns': ["Char", "currNum", "lastOp", "lastNum (Workbench)", "totalSum", "Action"],
        'rows': [
            ["'3'", "3", "'+'", "0", "0", "Accumulating digit 3"],
            ["'+'", "0", "'+'", "3", "0", "Apply prev '+': lastNum = 3. New lastOp = '+'"],
            ["'5'", "5", "'+'", "3", "0", "Accumulating digit 5"],
            ["'/'", "0", "'/'", "5", "3", "Apply prev '+': totalSum += 3; lastNum = 5. New lastOp = '/'"],
            ["'2'", "2", "'/'", "5", "3", "Accumulating digit 2"],
            ["'*'", "0", "'*'", "5 / 2 = 2", "3", "Apply prev '/': lastNum = 5 / 2 = 2. New lastOp = '*'"],
            ["'4'", "4", "'*'", "2", "3", "Accumulating digit 4"],
            ["End", "0", "-", "2 * 4 = 8", "3 + 8 = 11", "Apply prev '*': lastNum = 2 * 4 = 8; totalSum += 8 = 11"]
        ],
        'takeaway': "Division 5 / 2 = 2, then multiplied by 4 = 8, added to 3 = 11. Truncates toward zero as required."
    },
    'stepper': {
        'title': "Operator Precedence Evaluation Stepper",
        'default_input': '3+5/2*4',
        'presets': [
            {'label': "Example 3+5/2*4", 'onclick': 'loadPreset("3+5/2*4")'},
            {'label': "Example 3+2*2", 'onclick': 'loadPreset("3+2*2")'},
            {'label': "Division 14-3/2", 'onclick': 'loadPreset("14-3/2")'}
        ],
        'metrics': [
            {'id': "metric-char", 'label': "Current Char", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-curr-num", 'label': "Current Num", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-last-op", 'label': "Active Operator", 'color': "text-purple-400", 'initial': "'+'"},
            {'id': "metric-result", 'label': "Current Evaluation", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p227-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace operator evaluation and precedence handling.',
        'pseudocode_lines': [
            "var total = 0; var lastNum = 0; var curr = 0; var op = '+'",
            "for (i in s.indices) {",
            "    if (s[i].isDigit()) curr = curr * 10 + (s[i] - '0')",
            "    if (!s[i].isDigit() && s[i] != ' ' || i == s.lastIndex) {",
            "        if (op == '+') { total += lastNum; lastNum = curr }",
            "        else if (op == '-') { total += lastNum; lastNum = -curr }",
            "        else if (op == '*') lastNum *= curr",
            "        else if (op == '/') lastNum /= curr",
            "        op = s[i]; curr = 0",
            "    }",
            "}",
            "return total + lastNum"
        ],
        'js_code': """
    let rawExpr = "3+5/2*4";

    function buildSteps(expr) {
      steps = [];
      let total = 0;
      let lastNum = 0;
      let curr = 0;
      let op = '+';

      steps.push({
        char: "-", currNum: 0, lastOp: "'+'", result: 0,
        activeIdx: -1, lastNum: 0, total: 0, expr: expr, line: 1,
        narration: `Ready to evaluate "${expr}". Initialized lastOp='+', lastNum=0, total=0.`
      });

      for (let i = 0; i < expr.length; i++) {
        const ch = expr[i];
        if (ch >= '0' && ch <= '9') {
          curr = curr * 10 + parseInt(ch);
          steps.push({
            char: `'${ch}'`, currNum: curr, lastOp: `'${op}'`, result: total + lastNum,
            activeIdx: i, lastNum: lastNum, total: total, expr: expr, line: 3,
            narration: `Parsed digit '${ch}'. Current number is now ${curr}.`
          });
        }

        if ((ch !== ' ' && (ch < '0' || ch > '9')) || i === expr.length - 1) {
          const oldLast = lastNum;
          if (op === '+') {
            total += lastNum;
            lastNum = curr;
          } else if (op === '-') {
            total += lastNum;
            lastNum = -curr;
          } else if (op === '*') {
            lastNum = lastNum * curr;
          } else if (op === '/') {
            lastNum = Math.trunc(lastNum / curr);
          }

          steps.push({
            char: `'${ch}'`, currNum: 0, lastOp: `'${ch}'`, result: total + lastNum,
            activeIdx: i, lastNum: lastNum, total: total, expr: expr, line: 5,
            narration: `Applied operator '${op}' with curr=${curr}. lastNum updated from ${oldLast} to ${lastNum}. totalSum is ${total}. Next operator is '${ch}'.`
          });

          op = ch;
          curr = 0;
        }
      }

      total += lastNum;
      steps.push({
        char: "Done", currNum: 0, lastOp: "-", result: total,
        activeIdx: -1, lastNum: 0, total: total, expr: expr, line: 12,
        narration: `Evaluation complete. Final answer = ${total}.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p227-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-1.5 flex-wrap">';
      for (let i = 0; i < s.expr.length; i++) {
        const isAct = i === s.activeIdx;
        html += `<div class="w-8 h-10 rounded border font-mono text-xs font-bold flex items-center justify-center ${isAct ? 'bg-amber-500/20 border-amber-400 text-amber-300 ring-2 ring-amber-500' : 'bg-slate-900 border-slate-700 text-slate-300'}">${s.expr[i]}</div>`;
      }
      html += '</div>';

      html += '<div class="flex items-center justify-center gap-6 text-xs font-mono">';
      html += `<div class="px-3 py-1.5 rounded bg-slate-900 border border-slate-700">Accumulated Total: <strong class="text-cyan-400">${s.total}</strong></div>`;
      html += `<div class="px-3 py-1.5 rounded bg-slate-900 border border-emerald-500/40 text-emerald-300">Pending Term (lastNum): <strong class="text-white">${s.lastNum}</strong></div>`;
      html += '</div>';

      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-char').textContent = s.char;
      document.getElementById('metric-curr-num').textContent = s.currNum;
      document.getElementById('metric-last-op').textContent = s.lastOp;
      document.getElementById('metric-result').textContent = s.result;
    }

    function loadPreset(expr) {
      rawExpr = expr;
      buildSteps(rawExpr);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Explicit Stack (O(N) Time, O(N) Space)",
            'badge': "Standard",
            'color': "cyan",
            'complexity': "O(N) Time, O(N) Space",
            'summary': "Push numbers onto a stack with their signs. For * and /, pop the top element, perform arithmetic, and push back. Sum stack at the end.",
            'kotlin_code': """import java.util.ArrayDeque

class Solution {
    fun calculate(s: String): Int {
        val stack = ArrayDeque<Int>()
        var curr = 0
        var op = '+'

        for (i in s.indices) {
            val ch = s[i]
            if (ch.isDigit()) {
                curr = curr * 10 + (ch - '0')
            }
            if ((!ch.isDigit() && ch != ' ') || i == s.lastIndex) {
                when (op) {
                    '+' -> stack.push(curr)
                    '-' -> stack.push(-curr)
                    '*' -> stack.push(stack.pop() * curr)
                    '/' -> stack.push(stack.pop() / curr)
                }
                op = ch
                curr = 0
            }
        }

        var res = 0
        while (stack.isNotEmpty()) {
            res += stack.pop()
        }
        return res
    }
}""",
            'python_code': """class Solution:
    def calculate(self, s: str) -> int:
        stack = []
        curr = 0
        op = '+'

        for i, ch in enumerate(s):
            if ch.isdigit():
                curr = curr * 10 + int(ch)
            if (not ch.isdigit() and ch != ' ') or i == len(s) - 1:
                if op == '+':
                    stack.append(curr)
                elif op == '-':
                    stack.append(-curr)
                elif op == '*':
                    stack.append(stack.pop() * curr)
                elif op == '/':
                    # Python integer division truncation toward zero
                    stack.append(int(stack.pop() / curr))
                op = ch
                curr = 0

        return sum(stack)""",
            'java_code': """import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int calculate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int curr = 0;
        char op = '+';

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isDigit(ch)) {
                curr = curr * 10 + (ch - '0');
            }
            if ((!Character.isDigit(ch) && ch != ' ') || i == s.length() - 1) {
                if (op == '+') {
                    stack.push(curr);
                } else if (op == '-') {
                    stack.push(-curr);
                } else if (op == '*') {
                    stack.push(stack.pop() * curr);
                } else if (op == '/') {
                    stack.push(stack.pop() / curr);
                }
                op = ch;
                curr = 0;
            }
        }

        int sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        return sum;
    }
}""",
            'memory_breakdown': "O(N) space for stack storing intermediate addition operands. Clean and intuitive."
        },
        {
            'name': "★ Approach 2: O(1) Auxiliary Space Accumulator (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(1) Auxiliary Space",
            'summary': "Keep only `lastNum` and `totalSum`. When encountering '+' or '-', flush `lastNum` into `totalSum`. When encountering '*' or '/', update `lastNum` directly.",
            'kotlin_code': """class Solution {
    fun calculate(s: String): Int {
        var totalSum = 0
        var lastNum = 0
        var curr = 0
        var op = '+'

        for (i in s.indices) {
            val ch = s[i]
            if (ch.isDigit()) {
                curr = curr * 10 + (ch - '0')
            }
            if ((!ch.isDigit() && ch != ' ') || i == s.lastIndex) {
                when (op) {
                    '+' -> {
                        totalSum += lastNum
                        lastNum = curr
                    }
                    '-' -> {
                        totalSum += lastNum
                        lastNum = -curr
                    }
                    '*' -> lastNum *= curr
                    '/' -> lastNum /= curr
                }
                op = ch
                curr = 0
            }
        }
        return totalSum + lastNum
    }
}""",
            'python_code': """class Solution:
    def calculate(self, s: str) -> int:
        total_sum = 0
        last_num = 0
        curr = 0
        op = '+'

        for i, ch in enumerate(s):
            if ch.isdigit():
                curr = curr * 10 + int(ch)
            if (not ch.isdigit() and ch != ' ') or i == len(s) - 1:
                if op == '+':
                    total_sum += last_num
                    last_num = curr
                elif op == '-':
                    total_sum += last_num
                    last_num = -curr
                elif op == '*':
                    last_num = last_num * curr
                elif op == '/':
                    last_num = int(last_num / curr)
                op = ch
                curr = 0

        return total_sum + last_num""",
            'java_code': """class Solution {
    public int calculate(String s) {
        int totalSum = 0;
        int lastNum = 0;
        int curr = 0;
        char op = '+';

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isDigit(ch)) {
                curr = curr * 10 + (ch - '0');
            }
            if ((!Character.isDigit(ch) && ch != ' ') || i == s.length() - 1) {
                if (op == '+') {
                    totalSum += lastNum;
                    lastNum = curr;
                } else if (op == '-') {
                    totalSum += lastNum;
                    lastNum = -curr;
                } else if (op == '*') {
                    lastNum = lastNum * curr;
                } else if (op == '/') {
                    lastNum = lastNum / curr;
                }
                op = ch;
                curr = 0;
            }
        }
        return totalSum + lastNum;
    }
}""",
            'memory_breakdown': "Strictly O(1) auxiliary space: only uses scalar primitive variables (totalSum, lastNum, curr, op). Optimal for memory-constrained embedded environments."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun calculate(s: String): Int {
        // Write your optimal O(1) space solution here
        var total = 0
        var last = 0
        var curr = 0
        var op = '+'
        for (i in s.indices) {
            val ch = s[i]
            if (ch.isDigit()) curr = curr * 10 + (ch - '0')
            if ((!ch.isDigit() && ch != ' ') || i == s.lastIndex) {
                when (op) {
                    '+' -> { total += last; last = curr }
                    '-' -> { total += last; last = -curr }
                    '*' -> last *= curr
                    '/' -> last /= curr
                }
                op = ch
                curr = 0
            }
        }
        return total + last
    }
}""",
        'python_boilerplate': """class Solution:
    def calculate(self, s: str) -> int:
        # Write your optimal O(1) space solution here
        total = 0
        last = 0
        curr = 0
        op = '+'
        for i, ch in enumerate(s):
            if ch.isdigit(): curr = curr * 10 + int(ch)
            if (not ch.isdigit() and ch != ' ') or i == len(s) - 1:
                if op == '+': total += last; last = curr
                elif op == '-': total += last; last = -curr
                elif op == '*': last = last * curr
                elif op == '/': last = int(last / curr)
                op = ch
                curr = 0
        return total + last""",
        'java_boilerplate': """class Solution {
    public int calculate(String s) {
        // Write your optimal O(1) space solution here
        int total = 0, last = 0, curr = 0;
        char op = '+';
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isDigit(ch)) curr = curr * 10 + (ch - '0');
            if ((!Character.isDigit(ch) && ch != ' ') || i == s.length() - 1) {
                if (op == '+') { total += last; last = curr; }
                else if (op == '-') { total += last; last = -curr; }
                else if (op == '*') last *= curr;
                else if (op == '/') last /= curr;
                op = ch;
                curr = 0;
            }
        }
        return total + last;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(s) {
        let total = 0, last = 0, curr = 0, op = '+';
        for (let i = 0; i < s.length; i++) {
          const ch = s[i];
          if (ch >= '0' && ch <= '9') {
            curr = curr * 10 + parseInt(ch);
          }
          if ((ch !== ' ' && (ch < '0' || ch > '9')) || i === s.length - 1) {
            if (op === '+') { total += last; last = curr; }
            else if (op === '-') { total += last; last = -curr; }
            else if (op === '*') { last = last * curr; }
            else if (op === '/') { last = Math.trunc(last / curr); }
            op = ch;
            curr = 0;
          }
        }
        return total + last;
      }

      const tests = [
        { s: "3+2*2", expected: 7 },
        { s: " 3/2 ", expected: 1 },
        { s: " 3+5 / 2 ", expected: 5 },
        { s: "14-3/2", expected: 13 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.s);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: s="${t.s}" &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: the expression contains non-negative integers and operators '+', '-', '*', and '/'. Spaces are permitted, there are no parentheses, and division truncates toward zero. Results fit in 32-bit signed integers.\"",
        'phase2': "\"Because multiplication and division take precedence over addition and subtraction, we must evaluate them immediately. A standard stack approach pushes numbers with their signs, and when '*' or '/' is encountered, pops the top of stack to compute the product or quotient immediately. However, we can optimize this to O(1) auxiliary space! Because there are no parentheses, we only ever need two running numbers: `totalSum` (the accumulated sum of all previous terms) and `lastNum` (the pending product/quotient term being multiplied or divided). When '+' or '-' arrives, we commit `lastNum` into `totalSum` and start a new term. When '*' or '/' arrives, we multiply or divide `lastNum` directly.\"",
        'phase3': "\"Let me write the code. I initialize `totalSum = 0`, `lastNum = 0`, `curr = 0`, and `op = '+'`. In a loop over the string, if character is a digit, update `curr = curr * 10 + digit`. If character is an operator or we reached the end of string: check `op`. If '+', `totalSum += lastNum; lastNum = curr`. If '-', `totalSum += lastNum; lastNum = -curr`. If '*', `lastNum *= curr`. If '/', `lastNum /= curr`. Then set `op = ch` and `curr = 0`. At the end, return `totalSum + lastNum`.\"",
        'phase4': "\"Tracing with `\"3+5/2*4\"`. Init: op='+', lastNum=0, total=0. At '3': curr=3. At '+': op was '+', so total += 0 = 0, lastNum = 3, op becomes '+'. At '5': curr=5. At '/': op was '+', total += 3 = 3, lastNum = 5, op becomes '/'. At '2': curr=2. At '*': op was '/', lastNum = 5/2 = 2, op becomes '*'. At '4' (end): op was '*', lastNum = 2*4 = 8. Return total + lastNum = 3 + 8 = 11. Handles integer division truncation toward zero seamlessly with strict O(1) memory.\"",
        'senior_followup': "\"To support parentheses like in Basic Calculator I/III, we can use Dijkstra's Shunting-Yard algorithm to convert infix to postfix (Reverse Polish Notation) in linear time, or use mutually recursive functions `parseExpression` and `parseFactor`.\""
    },
    'quiz': [
        {
            'question': "Why can Basic Calculator II be solved in O(1) auxiliary space while Basic Calculator I cannot?",
            'options': [
                "Basic Calculator II has no parentheses, so nested state does not need a stack",
                "Basic Calculator II only uses single-digit integers bounded between 0 and 9",
                "Division operations cannot be pushed to a stack without IEEE-754 conversion",
                "Basic Calculator I contains negative numbers which requires dynamic heap vectors"
            ],
            'correct_index': 0,
            'explanation': "Parentheses create arbitrary nesting depth that requires a stack or recursion to preserve outer context. Without parentheses, operator precedence only requires keeping track of the immediately preceding multiplicative term."
        },
        {
            'question': "In Python, why is `int(a / b)` used instead of `a // b` for integer division?",
            'options': [
                "// performs floor division (towards -inf) whereas int(/) truncates towards zero",
                "// operator does not support 64-bit integer numbers in modern Python runtimes",
                "int(/) runs faster on ARM architectures due to single-cycle vector instructions",
                "// raises a ZeroDivisionError whenever the numerator is a negative number"
            ],
            'correct_index': 0,
            'explanation': "In Python, `-3 // 2` evaluates to `-2` (floor division), but LeetCode specification requires truncation towards zero (`-3 / 2` truncated is `-1`), which `int(-3 / 2)` or `trunc()` achieves."
        }
    ]
}

p65 = {
    'num': 65,
    'lc': 853,
    'title': "Car Fleet",
    'category': "Stacks, Queues & Monotonic Stacks",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "car-fleet",
    'verbatim_statement': """<p>There are <code>n</code> cars at given miles away from the starting mile 0, traveling to reach the mile <code>target</code>.</p>
<p>You are given two integer arrays <code>position</code> and <code>speed</code>, both of length <code>n</code>, where <code>position[i]</code> is the starting mile of the <code>i<sup>th</sup></code> car and <code>speed[i]</code> is the speed of the <code>i<sup>th</sup></code> car in miles per hour.</p>
<p>A car cannot pass another car ahead of it. It can only catch up to the car ahead at the same speed. A <strong>car fleet</strong> is some non-empty set of cars driving at the same position and same speed. Note that a single car is also considered a car fleet.</p>
<p>If a car catches up to a car fleet right at the destination point, it will still be considered as part of the car fleet.</p>
<p>Return <em>the number of car fleets that will arrive at the destination</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'target = 12, position = [10,8,0,5,3], speed = [2,4,1,1,3]', 'output': '3\nExplanation:\n- The cars starting at 10 (speed 2) and 8 (speed 4) become a fleet, meeting each other at 12. The fleet forms at target.\n- The car starting at 0 (speed 1) does not catch up to any other car, so it is a fleet by itself.\n- The cars starting at 5 (speed 1) and 3 (speed 3) become a fleet, meeting each other at 6. The fleet moves at speed 1 until it reaches target.\nTotal fleets = 3.'},
        {'title': "Example 2", 'input': 'target = 10, position = [3], speed = [3]', 'output': '1'},
        {'title': "Example 3", 'input': 'target = 100, position = [0,2,4], speed = [4,2,1]', 'output': '1'}
    ],
    'constraints': [
        "n == position.length == speed.length",
        "1 <= n <= 10^5",
        "0 <= position[i] < target",
        "All the values of position are unique.",
        "0 < speed[i] <= 10^6"
    ],
    'plain_english': "Calculate each car's remaining arrival time: `time = (target - position) / speed`. Sort the cars in descending order of starting position (closest to target first). Iterate from closest to farthest: if a car behind arrives earlier or at the same time as the fleet in front (`time <= leaderTime`), it gets stuck behind it and merges into that fleet. If it takes strictly longer (`time > leaderTime`), it cannot catch up and becomes the leader of a new fleet.",
    'boundary_checks': {
        'min_base': "Single car <code>n = 1</code> &rarr; Returns 1.",
        'best_exit': "All cars merge into single fleet <code>position = [0, 2, 4], speed = [4, 2, 1], target = 100</code> &rarr; Returns 1.",
        'worst_scan': "Descending speeds <code>position = [0, 5], speed = [1, 10], target = 10</code> &rarr; Front car takes 0.5h, rear takes 10h &rarr; 2 fleets."
    },
    'mental_model': {
        'analogy_title': "The Narrow One-Lane Mountain Highway",
        'analogy_text': "Imagine a single-lane mountain highway with no passing allowed. The car closest to the summit reaches it at some time T. Any car behind it that could drive faster will simply get stuck behind this car, slowed down to its pace, arriving at that exact same time T. Only a car that is so slow that its arrival time is strictly greater than T will form a new, separate traffic jam behind it.",
        'aha_moment': "Cars cannot pass. Therefore, sorting by position descending allows us to resolve fates from front to back. A car behind merges if its arrival time <= the fleet ahead. Maintaining a monotonic decreasing stack (or a simple running max time) yields the fleet count directly.",
        'trigger': "Objects moving at different speeds along a line without overtaking &rarr; Sort by position descending + Monotonic arrival time comparison.",
        'sisters': [
            {'name': "Car Fleet II (Collision Times)", 'lc': "LC #1776"},
            {'name': "Daily Temperatures (Monotonic Stack)", 'lc': "LC #739"},
            {'name': "Asteroid Collision", 'lc': "LC #735"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(T * N) Time, O(N) Space",
        'naive_text': "Discrete time step simulation: advance all cars by their speeds every tick, checking for collisions and grouping fleets.",
        'wasted_work': "Simulating millions of continuous time increments; floating point collision precision errors.",
        'optimal_complexity': "O(N log N) Time, O(N) Space",
        'optimal_text': "Sort cars by position descending. Single linear pass comparing arrival times to the current fleet leader's arrival time.",
        'core_invariant': "A car at position `p2 < p1` merges into the fleet at `p1` if and only if `(target - p2) / v2 <= (target - p1) / v1`.",
        'table_rows': [
            {'name': "1. Discrete Time Simulation", 'time': "O(Time * N)", 'space': "O(N)", 'note': "Severely times out; prone to floating point errors."},
            {'name': "2. Monotonic Stack", 'time': "O(N log N)", 'space': "O(N)", 'note': "Pushes arrival times; pops if time <= top."},
            {'name': "★ 3. Sort + Running Max Scalar", 'time': "O(N log N)", 'space': "O(N)", 'note': "Optimal; compares arrival time to maxTime without extra stack pushes."}
        ]
    },
    'trace_table': {
        'input_desc': 'target = 12, position = [10, 8, 0, 5, 3], speed = [2, 4, 1, 1, 3]',
        'columns': ["Sorted Car (pos, speed)", "Remaining Dist", "Arrival Time (dist / speed)", "Fleet Leader Time", "Action / Fleet Formation"],
        'rows': [
            ["(10, 2)", "12 - 10 = 2", "2 / 2 = 1.0 hr", "1.0 hr", "New Fleet #1 Leader (arrives at t=1.0)"],
            ["(8, 4)", "12 - 8 = 4", "4 / 4 = 1.0 hr", "1.0 hr", "1.0 <= 1.0 &rarr; Merges into Fleet #1"],
            ["(5, 1)", "12 - 5 = 7", "7 / 1 = 7.0 hr", "7.0 hr", "7.0 > 1.0 &rarr; New Fleet #2 Leader (arrives at t=7.0)"],
            ["(3, 3)", "12 - 3 = 9", "9 / 3 = 3.0 hr", "7.0 hr", "3.0 <= 7.0 &rarr; Catches Fleet #2 at mile 6, arrives at t=7.0"],
            ["(0, 1)", "12 - 0 = 12", "12 / 1 = 12.0 hr", "12.0 hr", "12.0 > 7.0 &rarr; New Fleet #3 Leader (arrives at t=12.0)"]
        ],
        'takeaway': "Total 3 distinct fleets arrive at the destination: Fleet #1 (cars 10,8), Fleet #2 (cars 5,3), Fleet #3 (car 0)."
    },
    'stepper': {
        'title': "Car Fleet Monotonic Arrival Stepper",
        'default_input': 'target=12, pos=[10,8,0,5,3], spd=[2,4,1,1,3]',
        'presets': [
            {'label': "Canonical 5 Cars", 'onclick': 'loadPreset(12, [10,8,0,5,3], [2,4,1,1,3])'},
            {'label': "All Merge (1 Fleet)", 'onclick': 'loadPreset(100, [0,2,4], [4,2,1])'},
            {'label': "Single Car", 'onclick': 'loadPreset(10, [3], [3])'}
        ],
        'metrics': [
            {'id': "metric-car", 'label': "Inspecting Car", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-time", 'label': "Arrival Time", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-leader-time", 'label': "Current Fleet Time", 'color': "text-purple-400", 'initial': "-"},
            {'id': "metric-fleets", 'label': "Total Fleets", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p853-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace position sorting and fleet convergence.',
        'pseudocode_lines': [
            "val cars = position.indices.map { Pair(position[it], speed[it]) }.sortedByDescending { it.first }",
            "var fleets = 0; var maxTime = 0.0",
            "for ((pos, spd) in cars) {",
            "    val time = (target - pos).toDouble() / spd",
            "    if (time > maxTime) { fleets++; maxTime = time }",
            "}",
            "return fleets"
        ],
        'js_code': """
    let rawTarget = 12;
    let rawPos = [10, 8, 0, 5, 3];
    let rawSpd = [2, 4, 1, 1, 3];

    function buildSteps(target, pos, spd) {
      steps = [];
      const cars = pos.map((p, i) => ({ pos: p, spd: spd[i], time: (target - p) / spd[i] }));
      cars.sort((a, b) => b.pos - a.pos);

      steps.push({
        car: "-", time: "-", leaderTime: "-", fleets: 0,
        activeIdx: -1, maxTime: 0, cars: [...cars], line: 1,
        narration: `Sorted ${cars.length} cars descending by position: ${cars.map(c=>'mile '+c.pos).join(', ')}.`
      });

      let fleets = 0;
      let maxTime = 0;

      for (let i = 0; i < cars.length; i++) {
        const c = cars[i];
        if (c.time > maxTime) {
          fleets++;
          maxTime = c.time;
          steps.push({
            car: `pos=${c.pos}, spd=${c.spd}`, time: `${c.time.toFixed(1)}h`, leaderTime: `${maxTime.toFixed(1)}h`, fleets: fleets,
            activeIdx: i, maxTime: maxTime, cars: [...cars], line: 4,
            narration: `Car at mile ${c.pos} arrives in ${c.time.toFixed(1)}h > fleet ahead (${(maxTime === c.time && fleets > 1) ? 'previous' : 'none'}). Forms NEW Fleet #${fleets}!`
          });
        } else {
          steps.push({
            car: `pos=${c.pos}, spd=${c.spd}`, time: `${c.time.toFixed(1)}h`, leaderTime: `${maxTime.toFixed(1)}h`, fleets: fleets,
            activeIdx: i, maxTime: maxTime, cars: [...cars], line: 5,
            narration: `Car at mile ${c.pos} arrives in ${c.time.toFixed(1)}h <= fleet ahead (${maxTime.toFixed(1)}h). Catches up and MERGES into current fleet.`
          });
        }
      }

      steps.push({
        car: "Done", time: "-", leaderTime: `${maxTime.toFixed(1)}h`, fleets: fleets,
        activeIdx: -1, maxTime: maxTime, cars: [...cars], line: 7,
        narration: `Finished processing. Total ${fleets} car fleet(s) reach target mile ${target}.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p853-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-2 flex-wrap">';
      s.cars.forEach((c, idx) => {
        const isAct = idx === s.activeIdx;
        html += `<div class="p-2.5 rounded-lg border font-mono text-xs flex flex-col items-center ${isAct ? 'bg-cyan-500/20 border-cyan-400 text-cyan-300 ring-2 ring-cyan-500' : 'bg-slate-900 border-slate-700 text-slate-300'}"><span class="font-bold">Mile ${c.pos}</span><span class="text-[10px] text-slate-400">${c.spd} mph &bull; ${c.time.toFixed(1)}h</span></div>`;
      });
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-car').textContent = s.car;
      document.getElementById('metric-time').textContent = s.time;
      document.getElementById('metric-leader-time').textContent = s.leaderTime;
      document.getElementById('metric-fleets').textContent = s.fleets;
    }

    function loadPreset(target, pos, spd) {
      rawTarget = target; rawPos = pos; rawSpd = spd;
      buildSteps(rawTarget, rawPos, rawSpd);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Monotonic Decreasing Stack",
            'badge': "Standard",
            'color': "cyan",
            'complexity': "O(N log N) Time, O(N) Space",
            'summary': "Sort cars by position descending. Compute arrival times and push to stack. If new time <= top of stack, it merges so pop new time. Stack size at end is fleet count.",
            'kotlin_code': """import java.util.ArrayDeque

class Solution {
    fun carFleet(target: Int, position: IntArray, speed: IntArray): Int {
        val n = position.size
        val cars = Array(n) { i -> Pair(position[i], speed[i]) }
        cars.sortByDescending { it.first }

        val stack = ArrayDeque<Double>()
        for (car in cars) {
            val time = (target - car.first).toDouble() / car.second
            if (stack.isEmpty() || time > stack.peek()) {
                stack.push(time)
            }
        }
        return stack.size
    }
}""",
            'python_code': """class Solution:
    def carFleet(self, target: int, position: list[int], speed: list[int]) -> int:
        pair = sorted(zip(position, speed), reverse=True)
        stack = []

        for p, s in pair:
            time = (target - p) / s
            if not stack or time > stack[-1]:
                stack.append(time)

        return len(stack)""",
            'java_code': """import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        double[][] cars = new double[n][2];
        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = speed[i];
        }

        Arrays.sort(cars, (a, b) -> Double.compare(b[0], a[0]));

        Deque<Double> stack = new ArrayDeque<>();
        for (double[] car : cars) {
            double time = (target - car[0]) / car[1];
            if (stack.isEmpty() || time > stack.peek()) {
                stack.push(time);
            }
        }
        return stack.size();
    }
}""",
            'memory_breakdown': "O(N) space for sorted pairs and stack. O(N log N) time dominated by sorting."
        },
        {
            'name': "★ Approach 2: Sort + Running Max Arrival Time (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N log N) Time, O(N) Space",
            'summary': "Eliminates the stack! A simple scalar `maxTime` tracks the arrival time of the current fleet leader ahead. If a car's time > maxTime, increment fleet count and update maxTime.",
            'kotlin_code': """class Solution {
    fun carFleet(target: Int, position: IntArray, speed: IntArray): Int {
        val n = position.size
        val cars = Array(n) { i -> Pair(position[i], speed[i]) }
        cars.sortByDescending { it.first }

        var fleets = 0
        var maxTime = 0.0

        for (car in cars) {
            val time = (target - car.first).toDouble() / car.second
            if (time > maxTime) {
                fleets++
                maxTime = time
            }
        }
        return fleets
    }
}""",
            'python_code': """class Solution:
    def carFleet(self, target: int, position: list[int], speed: list[int]) -> int:
        cars = sorted(zip(position, speed), reverse=True)
        fleets = 0
        max_time = 0.0

        for p, s in cars:
            time = (target - p) / s
            if time > max_time:
                fleets += 1
                max_time = time

        return fleets""",
            'java_code': """import java.util.Arrays;

class Solution {
    public int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        int[][] cars = new int[n][2];
        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = speed[i];
        }

        Arrays.sort(cars, (a, b) -> Integer.compare(b[0], a[0]));

        int fleets = 0;
        double maxTime = 0.0;

        for (int[] car : cars) {
            double time = (double)(target - car[0]) / car[1];
            if (time > maxTime) {
                fleets++;
                maxTime = time;
            }
        }
        return fleets;
    }
}""",
            'memory_breakdown': "O(N) memory for the sorting array. Single scalar tracking maxTime. Zero extra stack push/pop overhead."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun carFleet(target: Int, position: IntArray, speed: IntArray): Int {
        // Write your optimal O(N log N) solution here
        val n = position.size
        val cars = Array(n) { i -> Pair(position[i], speed[i]) }
        cars.sortByDescending { it.first }
        var fleets = 0
        var maxTime = 0.0
        for (car in cars) {
            val time = (target - car.first).toDouble() / car.second
            if (time > maxTime) {
                fleets++
                maxTime = time
            }
        }
        return fleets
    }
}""",
        'python_boilerplate': """class Solution:
    def carFleet(self, target: int, position: list[int], speed: list[int]) -> int:
        # Write your optimal O(N log N) solution here
        cars = sorted(zip(position, speed), reverse=True)
        fleets = 0
        max_time = 0.0
        for p, s in cars:
            time = (target - p) / s
            if time > max_time:
                fleets += 1
                max_time = time
        return fleets""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public int carFleet(int target, int[] position, int[] speed) {
        // Write your optimal O(N log N) solution here
        int n = position.length;
        int[][] cars = new int[n][2];
        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = speed[i];
        }
        Arrays.sort(cars, (a, b) -> Integer.compare(b[0], a[0]));
        int fleets = 0;
        double maxTime = 0.0;
        for (int[] car : cars) {
            double time = (double)(target - car[0]) / car[1];
            if (time > maxTime) {
                fleets++;
                maxTime = time;
            }
        }
        return fleets;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(target, position, speed) {
        const cars = position.map((p, i) => ({ pos: p, spd: speed[i] }));
        cars.sort((a, b) => b.pos - a.pos);
        let fleets = 0;
        let maxTime = 0;
        for (let i = 0; i < cars.length; i++) {
          const time = (target - cars[i].pos) / cars[i].spd;
          if (time > maxTime) {
            fleets++;
            maxTime = time;
          }
        }
        return fleets;
      }

      const tests = [
        { target: 12, pos: [10,8,0,5,3], spd: [2,4,1,1,3], expected: 3 },
        { target: 10, pos: [3], spd: [3], expected: 1 },
        { target: 100, pos: [0,2,4], spd: [4,2,1], expected: 1 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.target, t.pos, t.spd);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: target=${t.target} &rarr; Got ${actual} fleets, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: position array elements are distinct and < target. Cars travel in one direction and cannot overtake each other. If a car catches up to another car at or before target, they merge into the same fleet moving at the speed of the leading car.\"",
        'phase2': "\"Since cars cannot pass, their fates are constrained by the cars ahead of them. This suggests sorting the cars by their starting position in descending order (closest to target first). For each car, we calculate its arrival time to the target: `time = (target - position) / speed`. If a car behind has an arrival time less than or equal to the car in front, it will catch up and merge into that fleet. Only if a car behind has an arrival time strictly greater than the fleet ahead does it form a new fleet leader. By tracking the running maximum arrival time, we can count fleets in a single linear pass after sorting.\"",
        'phase3': "\"Let me write the code. I pair each position with its speed and sort descending by position. Maintain `fleets = 0` and `maxTime = 0.0`. Loop over each car: `time = (target - pos).toDouble() / spd`. If `time > maxTime`, increment `fleets` and update `maxTime = time`. Return `fleets`.\"",
        'phase4': "\"Tracing with `target=12, pos=[10,8,0,5,3], spd=[2,4,1,1,3]`. Sorted: (10, spd 2, t=1.0), (8, spd 4, t=1.0), (5, spd 1, t=7.0), (3, spd 3, t=3.0), (0, spd 1, t=12.0). Car at 10 has t=1.0 > 0 &rarr; fleet 1, maxTime=1.0. Car at 8 has t=1.0 <= 1.0 &rarr; merges. Car at 5 has t=7.0 > 1.0 &rarr; fleet 2, maxTime=7.0. Car at 3 has t=3.0 <= 7.0 &rarr; merges. Car at 0 has t=12.0 > 7.0 &rarr; fleet 3, maxTime=12.0. Total 3 fleets. Single car returns 1. Handled cleanly.\"",
        'senior_followup': "\"In a real-time autonomous vehicle fleet routing system, we would model this as an event-driven priority queue where collision events are scheduled dynamically, updating velocity profiles in O(log N) per collision event.\""
    },
    'quiz': [
        {
            'question': "Why must the cars be sorted by starting position in DESCENDING order?",
            'options': [
                "Cars cannot overtake, so a car's fate is dictated by the cars ahead of it",
                "Descending order enables QuickSort to run in O(N) linear time on integers",
                "Ascending order would cause integer division by zero at the destination",
                "The problem description requires outputting fleet arrivals in reverse order"
            ],
            'correct_index': 0,
            'explanation': "A car can only be slowed down by cars in front of it. Sorting descending processes the frontmost cars first, establishing the bottleneck arrival times that all trailing cars must abide by."
        },
        {
            'question': "When does a trailing car merge into the fleet ahead of it?",
            'options': [
                "When its calculated arrival time is <= the arrival time of the fleet ahead",
                "When its speed is strictly greater than the target mile divided by two",
                "When its position is an even integer and its speed is an odd integer",
                "When its starting distance is within 1 mile of the car in front of it"
            ],
            'correct_index': 0,
            'explanation': "If the trailing car would reach the target earlier or at the exact same moment as the car ahead, it will catch up at or before the target. Since it cannot pass, it merges into that fleet."
        }
    ]
}

p66 = {
    'num': 66,
    'lc': 981,
    'title': "Time Based Key-Value Store",
    'category': "Multi-Tier OOP & Timed State Systems",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "time-based-key-value-store",
    'verbatim_statement': """<p>Design a time-based key-value data structure that can store multiple values for the same key at different time stamps and retrieve the key's value at a certain timestamp.</p>
<p>Implement the <code>TimeMap</code> class:</p>
<ul>
	<li><code>TimeMap()</code> Initializes the object of the data structure.</li>
	<li><code>void set(String key, String value, int timestamp)</code> Stores the key <code>key</code> with the value <code>value</code> at the given time <code>timestamp</code>.</li>
	<li><code>String get(String key, int timestamp)</code> Returns a value such that <code>set</code> was called previously, with <code>timestamp_prev <= timestamp</code>. If there are multiple such values, it returns the value associated with the largest <code>timestamp_prev</code>. If there are no values, it returns <code>""</code>.</li>
</ul>""",
    'examples': [
        {'title': "Example 1", 'input': '["TimeMap", "set", "get", "get", "set", "get", "get"]\n[[], ["foo", "bar", 1], ["foo", 1], ["foo", 3], ["foo", "bar2", 4], ["foo", 4], ["foo", 5]]', 'output': '[null, null, "bar", "bar", null, "bar2", "bar2"]\nExplanation:\nTimeMap timeMap = new TimeMap();\ntimeMap.set("foo", "bar", 1);  // store the key "foo" and value "bar" along with timestamp = 1.\ntimeMap.get("foo", 1);         // return "bar"\ntimeMap.get("foo", 3);         // return "bar", since there is no value corresponding to timestamp 3 and timestamp 2, then the only value is at timestamp 1 is "bar".\ntimeMap.set("foo", "bar2", 4); // store the key "foo" and value "bar2" along with timestamp = 4.\ntimeMap.get("foo", 4);         // return "bar2"\ntimeMap.get("foo", 5);         // return "bar2"'}
    ],
    'constraints': [
        "1 <= key.length, value.length <= 100",
        "key and value consist of lowercase English letters and digits.",
        "1 <= timestamp <= 10^7",
        "All the timestamps timestamp of set are strictly increasing.",
        "At most 2 * 10^5 calls will be made to set and get."
    ],
    'plain_english': "Store each key's history in a HashMap mapping from key to a dynamic list of (timestamp, value) pairs. Because the problem guarantees timestamps arrive in strictly increasing order, appending to the list is O(1) and the list is always sorted by timestamp. When calling get(key, timestamp), run Binary Search on the list to find the largest timestamp <= query timestamp in O(log M) time.",
    'boundary_checks': {
        'min_base': "Key does not exist <code>get(\"unknown\", 1)</code> &rarr; Returns \"\" immediately.",
        'best_exit': "Query timestamp smaller than first entry <code>list = [(5, \"a\")], get(key, 2)</code> &rarr; Returns \"\".",
        'worst_scan': "Query timestamp far in the future <code>get(key, 1000000)</code> &rarr; Binary search lands on last element in O(log M)."
    },
    'mental_model': {
        'analogy_title': "The Microfilm Archive with Timeline Index",
        'analogy_text': "Imagine a library where each drawer is labeled with a topic (the key). Inside each drawer is a chronological reel of microfilm clippings stamped with exact dates. When someone asks: 'What was the official policy on foo on March 3rd?', you pull drawer 'foo'. Since clippings are filed in chronological order, you don't read them one by one—you binary search to find the latest clipping dated on or before March 3rd.",
        'aha_moment': "The problem guarantees `set` timestamps are strictly increasing! This means we never need an expensive sorted tree (TreeMap / balanced BST) to keep entries sorted. An ordinary `ArrayList` retains natural sorting at zero overhead, unlocking O(log M) binary search for `get` and O(1) amortized `set`.",
        'trigger': "Historical point-in-time state query with strictly increasing write timestamps &rarr; HashMap<Key, ArrayList<Entry>> + Binary Search.",
        'sisters': [
            {'name': "In-Memory Key-Value Store with TTL", 'lc': "LC #Custom"},
            {'name': "Snapshot Array", 'lc': "LC #1146"},
            {'name': "Find First and Last Position in Sorted Array", 'lc': "LC #34"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(1) Set, O(M) Get",
        'naive_text': "Store entries in an ArrayList and do a linear scan from end to start looking for timestamp_prev <= timestamp.",
        'wasted_work': "Scanning sequentially through thousands of entries when the list is already sorted ($2 \\times 10^5$ operations).",
        'optimal_complexity': "O(1) Set, O(log M) Get",
        'optimal_text': "HashMap<String, ArrayList<Pair>>. Since set calls arrive chronologically, list is sorted. Binary search (upper bound - 1) in O(log M).",
        'core_invariant': "For any key, `list[i].timestamp < list[i+1].timestamp`. Binary search finds the rightmost index where `list[mid].timestamp <= timestamp`.",
        'table_rows': [
            {'name': "1. Linear Scan History", 'time': "Set: O(1), Get: O(M)", 'space': "O(N)", 'note': "Times out when multiple gets query long historical chains."},
            {'name': "2. TreeMap FloorEntry", 'time': "Set: O(log M), Get: O(log M)", 'space': "O(N)", 'note': "Tree node pointer overhead; set takes O(log M)."},
            {'name': "★ 3. ArrayList + Binary Search", 'time': "Set: O(1) Amortized, Get: O(log M)", 'space': "O(N)", 'note': "Optimal cache locality; fastest write throughput."}
        ]
    },
    'trace_table': {
        'input_desc': 'set("foo","bar",1), get("foo",1), get("foo",3), set("foo","bar2",4), get("foo",4), get("foo",5)',
        'columns': ["Operation", "Args", "Key List State", "Binary Search Range", "Result Returned", "Takeaway"],
        'rows': [
            ["set", "(\"foo\", \"bar\", 1)", "foo: [(1, \"bar\")]", "-", "void", "Appended (1, \"bar\") to list"],
            ["get", "(\"foo\", 1)", "foo: [(1, \"bar\")]", "low=0, high=0, mid=0", "\"bar\"", "Exact timestamp 1 matched"],
            ["get", "(\"foo\", 3)", "foo: [(1, \"bar\")]", "low=0, high=0, mid=0", "\"bar\"", "Largest ts <= 3 is 1 &rarr; \"bar\""],
            ["set", "(\"foo\", \"bar2\", 4)", "foo: [(1, \"bar\"), (4, \"bar2\")]", "-", "void", "Appended (4, \"bar2\") to list"],
            ["get", "(\"foo\", 4)", "foo: [(1, \"bar\"), (4, \"bar2\")]", "low=0, high=1, mid=1", "\"bar2\"", "Exact timestamp 4 matched"],
            ["get", "(\"foo\", 5)", "foo: [(1, \"bar\"), (4, \"bar2\")]", "low=0, high=1, mid=1", "\"bar2\"", "Largest ts <= 5 is 4 &rarr; \"bar2\""]
        ],
        'takeaway': "Even when an exact timestamp match does not exist (like timestamp 3 or 5), binary search finds the floor snapshot accurately in O(log M)."
    },
    'stepper': {
        'title': "TimeMap Binary Search Point-in-Time Stepper",
        'default_input': 'key="foo", queryTimestamp=3',
        'presets': [
            {'label': "Query ts=3 on foo", 'onclick': 'loadPreset("foo", 3)'},
            {'label': "Query ts=5 on foo", 'onclick': 'loadPreset("foo", 5)'},
            {'label': "Query ts=0 (Before First)", 'onclick': 'loadPreset("foo", 0)'}
        ],
        'metrics': [
            {'id': "metric-key", 'label': "Query Key", 'color': "text-amber-400", 'initial': "foo"},
            {'id': "metric-ts", 'label': "Target Timestamp", 'color': "text-cyan-400", 'initial': "3"},
            {'id': "metric-mid", 'label': "Binary Search Mid", 'color': "text-purple-400", 'initial': "-"},
            {'id': "metric-val", 'label': "Matched Value", 'color': "text-emerald-400", 'initial': '""'}
        ],
        'visual_html': '<div id="p981-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace binary search over timestamped entries.',
        'pseudocode_lines': [
            "val list = map[key] ?: return \"\"",
            "var low = 0; var high = list.size - 1; var res = \"\"",
            "while (low <= high) {",
            "    val mid = (low + high) / 2",
            "    if (list[mid].timestamp <= timestamp) { res = list[mid].value; low = mid + 1 }",
            "    else high = mid - 1",
            "}",
            "return res"
        ],
        'js_code': """
    const history = {
      foo: [
        { ts: 1, val: "bar" },
        { ts: 4, val: "bar2" },
        { ts: 7, val: "bar3" },
        { ts: 10, val: "bar4" }
      ]
    };
    let queryKey = "foo";
    let queryTs = 3;

    function buildSteps(key, ts) {
      steps = [];
      const list = history[key] || [];

      steps.push({
        key: key, ts: ts, mid: "-", val: '""',
        low: 0, high: list.length - 1, activeMid: -1, list: [...list], line: 1,
        narration: `Initiating get("${key}", ${ts}). Key has ${list.length} historical entries.`
      });

      let low = 0, high = list.length - 1;
      let res = "";

      while (low <= high) {
        const mid = Math.floor((low + high) / 2);
        const item = list[mid];

        steps.push({
          key: key, ts: ts, mid: `idx ${mid} (ts=${item.ts})`, val: `"${res}"`,
          low: low, high: high, activeMid: mid, list: [...list], line: 4,
          narration: `Checking mid index ${mid}: timestamp ${item.ts} vs target ${ts}.`
        });

        if (item.ts <= ts) {
          res = item.val;
          low = mid + 1;
          steps.push({
            key: key, ts: ts, mid: `idx ${mid}`, val: `"${res}"`,
            low: low, high: high, activeMid: mid, list: [...list], line: 5,
            narration: `${item.ts} <= ${ts}: valid candidate! Recorded res="${res}". Shifting low to ${low} to search for closer timestamp.`
          });
        } else {
          high = mid - 1;
          steps.push({
            key: key, ts: ts, mid: `idx ${mid}`, val: `"${res}"`,
            low: low, high: high, activeMid: mid, list: [...list], line: 6,
            narration: `${item.ts} > ${ts}: timestamp too late. Shifting high to ${high}.`
          });
        }
      }

      steps.push({
        key: key, ts: ts, mid: "Done", val: `"${res}"`,
        low: low, high: high, activeMid: -1, list: [...list], line: 8,
        narration: `Search complete. Returning "${res}".`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p981-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-2 flex-wrap">';
      s.list.forEach((item, idx) => {
        const isMid = idx === s.activeMid;
        const inRange = idx >= s.low && idx <= s.high;
        let cls = 'bg-slate-900 border-slate-700 text-slate-400 opacity-40';
        if (isMid) cls = 'bg-purple-500/30 border-purple-400 text-purple-200 ring-2 ring-purple-500 opacity-100';
        else if (inRange) cls = 'bg-cyan-500/10 border-cyan-500/40 text-cyan-200 opacity-100';

        html += `<div class="p-3 rounded-lg border font-mono text-xs flex flex-col items-center ${cls}"><span class="font-bold text-amber-300">ts = ${item.ts}</span><span class="text-white">"${item.val}"</span></div>`;
      });
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-key').textContent = s.key;
      document.getElementById('metric-ts').textContent = s.ts;
      document.getElementById('metric-mid').textContent = s.mid;
      document.getElementById('metric-val').textContent = s.val;
    }

    function loadPreset(key, ts) {
      queryKey = key; queryTs = ts;
      buildSteps(queryKey, queryTs);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: TreeMap FloorEntry (Java / Kotlin)",
            'badge': "Standard",
            'color': "cyan",
            'complexity': "Set: O(log M), Get: O(log M)",
            'summary': "Use HashMap<String, TreeMap<Integer, String>>. TreeMap's floorEntry(timestamp) retrieves the largest key <= timestamp in O(log M).",
            'kotlin_code': """import java.util.TreeMap

class TimeMap {
    private val map = HashMap<String, TreeMap<Int, String>>()

    fun set(key: String, value: String, timestamp: Int) {
        map.computeIfAbsent(key) { TreeMap() }[timestamp] = value
    }

    fun get(key: String, timestamp: Int): String {
        val tree = map[key] ?: return ""
        val entry = tree.floorEntry(timestamp) ?: return ""
        return entry.value
    }
}""",
            'python_code': """# In Python, TreeMap is not standard; we simulate via bisect
from bisect import bisect_right

class TimeMap:
    def __init__(self):
        self.map = {}

    def set(self, key: str, value: str, timestamp: int) -> None:
        if key not in self.map:
            self.map[key] = []
        self.map[key].append((timestamp, value))

    def get(self, key: str, timestamp: int) -> str:
        if key not in self.map:
            return ""
        arr = self.map[key]
        idx = bisect_right(arr, (timestamp, chr(127))) - 1
        return arr[idx][1] if idx >= 0 else """"",
            'java_code': """import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class TimeMap {
    private Map<String, TreeMap<Integer, String>> map;

    public TimeMap() {
        map = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        TreeMap<Integer, String> tree = map.get(key);
        if (tree == null) return "";
        Map.Entry<Integer, String> entry = tree.floorEntry(timestamp);
        return entry == null ? "" : entry.getValue();
    }
}""",
            'memory_breakdown': "O(N) memory. While floorEntry is convenient, Red-Black Tree node overhead creates significant GC pressure and costs O(log M) for set operations."
        },
        {
            'name': "★ Approach 2: HashMap of ArrayList + Manual Binary Search (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "Set: O(1) Amortized, Get: O(log M)",
            'summary': "Take advantage of monotonic timestamp ordering in set! Store chronological lists. Appending is O(1). Use binary search to find floor timestamp.",
            'kotlin_code': """class TimeMap {
    private data class Entry(val timestamp: Int, val value: String)
    private val map = HashMap<String, ArrayList<Entry>>()

    fun set(key: String, value: String, timestamp: Int) {
        map.computeIfAbsent(key) { ArrayList() }.add(Entry(timestamp, value))
    }

    fun get(key: String, timestamp: Int): String {
        val list = map[key] ?: return ""
        var low = 0
        var high = list.size - 1
        var res = ""

        while (low <= high) {
            val mid = (low + high) ushr 1
            if (list[mid].timestamp <= timestamp) {
                res = list[mid].value
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return res
    }
}""",
            'python_code': """class TimeMap:
    def __init__(self):
        self.store = {}

    def set(self, key: str, value: str, timestamp: int) -> None:
        if key not in self.store:
            self.store[key] = []
        self.store[key].append((timestamp, value))

    def get(self, key: str, timestamp: int) -> str:
        if key not in self.store:
            return ""
        arr = self.store[key]
        low, high = 0, len(arr) - 1
        res = ""

        while low <= high:
            mid = (low + high) // 2
            if arr[mid][0] <= timestamp:
                res = arr[mid][1]
                low = mid + 1
            else:
                high = mid - 1

        return res""",
            'java_code': """import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TimeMap {
    private static class Entry {
        final int timestamp;
        final String value;
        Entry(int timestamp, String value) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }

    private final Map<String, List<Entry>> map;

    public TimeMap() {
        map = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(new Entry(timestamp, value));
    }

    public String get(String key, int timestamp) {
        List<Entry> list = map.get(key);
        if (list == null) return "";

        int low = 0, high = list.size() - 1;
        String res = "";

        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (list.get(mid).timestamp <= timestamp) {
                res = list.get(mid).value;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return res;
    }
}""",
            'memory_breakdown': "Optimal O(1) amortized set operations and O(log M) get operations. Contiguous ArrayList memory provides superior CPU cache locality compared to tree nodes."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class TimeMap() {
    // Write your optimal HashMap + Binary Search implementation
    private data class Entry(val ts: Int, val value: String)
    private val map = HashMap<String, ArrayList<Entry>>()

    fun set(key: String, value: String, timestamp: Int) {
        map.computeIfAbsent(key) { ArrayList() }.add(Entry(timestamp, value))
    }

    fun get(key: String, timestamp: Int): String {
        val list = map[key] ?: return ""
        var low = 0; var high = list.size - 1; var res = ""
        while (low <= high) {
            val mid = (low + high) ushr 1
            if (list[mid].ts <= timestamp) {
                res = list[mid].value
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return res
    }
}""",
        'python_boilerplate': """class TimeMap:
    # Write your optimal HashMap + Binary Search implementation
    def __init__(self):
        self.store = {}

    def set(self, key: str, value: str, timestamp: int) -> None:
        if key not in self.store: self.store[key] = []
        self.store[key].append((timestamp, value))

    def get(self, key: str, timestamp: int) -> str:
        if key not in self.store: return ""
        arr = self.store[key]
        low, high = 0, len(arr) - 1
        res = ""
        while low <= high:
            mid = (low + high) // 2
            if arr[mid][0] <= timestamp:
                res = arr[mid][1]
                low = mid + 1
            else:
                high = mid - 1
        return res""",
        'java_boilerplate': """import java.util.*;

class TimeMap {
    // Write your optimal HashMap + Binary Search implementation
    static class Entry {
        int ts; String val;
        Entry(int ts, String val) { this.ts = ts; this.val = val; }
    }
    Map<String, List<Entry>> map = new HashMap<>();

    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(new Entry(timestamp, value));
    }

    public String get(String key, int timestamp) {
        List<Entry> list = map.get(key);
        if (list == null) return "";
        int low = 0, high = list.size() - 1;
        String res = "";
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (list.get(mid).ts <= timestamp) {
                res = list.get(mid).val;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return res;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      class TimeMap {
        constructor() { this.store = new Map(); }
        set(key, val, ts) {
          if (!this.store.has(key)) this.store.set(key, []);
          this.store.get(key).push({ ts, val });
        }
        get(key, ts) {
          if (!this.store.has(key)) return "";
          const list = this.store.get(key);
          let low = 0, high = list.length - 1, res = "";
          while (low <= high) {
            const mid = Math.floor((low + high) / 2);
            if (list[mid].ts <= ts) {
              res = list[mid].val;
              low = mid + 1;
            } else {
              high = mid - 1;
            }
          }
          return res;
        }
      }

      const tm = new TimeMap();
      tm.set("foo", "bar", 1);
      const r1 = tm.get("foo", 1);
      const r2 = tm.get("foo", 3);
      tm.set("foo", "bar2", 4);
      const r3 = tm.get("foo", 4);
      const r4 = tm.get("foo", 5);

      const pass = r1 === "bar" && r2 === "bar" && r3 === "bar2" && r4 === "bar2";
      const row = document.createElement('div');
      row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
      row.textContent = `Test: get(1)="${r1}", get(3)="${r2}", get(4)="${r3}", get(5)="${r4}" (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify constraints: `set` timestamps are guaranteed to be strictly increasing. When `get` is called, we return the value with the largest `timestamp_prev <= timestamp`. If no such entry exists, return `\"\"`. Up to 200,000 operations.\"",
        'phase2': "\"Because timestamps for each key arrive strictly in increasing order, any list of entries we append to is automatically sorted by timestamp. This is a critical insight: we do not need a TreeMap or balanced tree, which would pay an O(log M) cost on every `set`! Instead, we can map each key to an `ArrayList` of (timestamp, value) pairs. `set` is O(1) amortized append. When `get(key, timestamp)` is called, we perform binary search over the array list in O(log M) time to find the largest timestamp <= query timestamp.\"",
        'phase3': "\"Let me write the code. I create an internal `Entry` class with `timestamp` and `value`. The main storage is `Map<String, List<Entry>> map = new HashMap<>()`. In `set`, I append `new Entry(timestamp, value)`. In `get`, if the key doesn't exist, return `\"\"`. Then initialize `low = 0`, `high = list.size() - 1`, and `res = \"\"`. While `low <= high`, if `list.get(mid).timestamp <= timestamp`, record `res = list.get(mid).value` and set `low = mid + 1` to search for a later match; else set `high = mid - 1`. Return `res`.\"",
        'phase4': "\"Tracing with `set(\"foo\", \"bar\", 1)`. `get(\"foo\", 1)` finds exact match `\"bar\"`. `get(\"foo\", 3)` checks mid=0 (ts=1 <= 3), records `\"bar\"`, low becomes 1, loop ends, returns `\"bar\"`. Then `set(\"foo\", \"bar2\", 4)`. `get(\"foo\", 5)` checks mid=0 (ts=1 <= 5), then mid=1 (ts=4 <= 5), records `\"bar2\"`, returns `\"bar2\"`. If querying timestamp 0, high becomes -1, returns `\"\"`. Everything runs in optimal O(1) set and O(log M) get.\"",
        'senior_followup': "\"In production storage engines (like Cassandra or RocksDB SSTables), SSTables are immutable sorted runs written in timestamp order. Point-in-time reads perform binary search over the sparse primary index and use Bloom filters to avoid reading SSTables where the key does not exist.\""
    },
    'quiz': [
        {
            'question': "Why is an ArrayList preferred over a TreeMap for this problem?",
            'options': [
                "Timestamps arrive in strictly increasing order, making ArrayList append O(1)",
                "TreeMap cannot handle negative timestamps without throwing IllegalArgumentException",
                "ArrayList uses 64-bit integer indexing whereas TreeMap uses 32-bit pointers",
                "TreeMap is not thread-safe and requires locking for single-threaded benchmarks"
            ],
            'correct_index': 0,
            'explanation': "Because set calls arrive with strictly increasing timestamps, appending to an ArrayList preserves sorted order naturally in O(1) amortized time without the O(log M) insertion cost and memory overhead of a Red-Black tree."
        },
        {
            'question': "What should binary search do when list[mid].timestamp <= queryTimestamp?",
            'options': [
                "Record list[mid].value as a candidate and advance low = mid + 1",
                "Return list[mid].value immediately without checking other elements",
                "Advance high = mid - 1 to look for earlier timestamps",
                "Throw a NoSuchElementException if mid is not the last index"
            ],
            'correct_index': 0,
            'explanation': "The problem requires the largest timestamp <= queryTimestamp. Even if mid matches, there might be a later timestamp further to the right that is also <= queryTimestamp, so we record the value and move right (`low = mid + 1`)."
        }
    ]
}

p67 = {
    'num': 67,
    'lc': "Custom",
    'title': "In-Memory Key-Value Store with TTL",
    'category': "Multi-Tier OOP & Timed State Systems",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "in-memory-key-value-store-ttl",
    'verbatim_statement': """<p>Design a production-grade, in-memory Key-Value store with Time-to-Live (TTL) expiration, prefix search, and multi-tier transaction rollback capabilities.</p>
<p>Implement the <code>InMemoryKVStore</code> class supporting:</p>
<ul>
    <li><code>set(key: String, value: String)</code>: Stores key-value pair indefinitely.</li>
    <li><code>setWithTTL(key: String, value: String, ttlMs: Long)</code>: Stores key-value with TTL expiration in milliseconds from current clock time.</li>
    <li><code>get(key: String): String?</code>: Returns value if present and not expired; returns <code>null</code> otherwise.</li>
    <li><code>delete(key: String): Boolean</code>: Removes key, returns true if key was present.</li>
    <li><code>scan(prefix: String): List<Pair<String, String>></code>: Returns all non-expired key-value pairs whose keys start with <code>prefix</code> in lexicographical order.</li>
    <li><code>beginTransaction()</code>: Starts a nested transaction block.</li>
    <li><code>commit(): Boolean</code>: Commits current transaction changes permanently. Returns false if no transaction active.</li>
    <li><code>rollback(): Boolean</code>: Reverts all mutations made within the current transaction block. Returns false if no transaction active.</li>
</ul>""",
    'examples': [
        {'title': "Example 1 (TTL Expiration)", 'input': 'setWithTTL("user:1", "Alice", 1000)\nget("user:1") -> "Alice"\n(Wait 1500ms)\nget("user:1") -> null', 'output': '"Alice", then null'},
        {'title': "Example 2 (Transactions & Rollback)", 'input': 'set("balance", "100")\nbeginTransaction()\nset("balance", "200")\nget("balance") -> "200"\nrollback()\nget("balance") -> "100"', 'output': '"200", then "100" after rollback'}
    ],
    'constraints': [
        "Keys and values are non-empty strings up to 1024 characters.",
        "TTL values are positive integers up to 10^9 ms.",
        "Transactions can be nested up to 50 levels deep.",
        "Operations must be thread-safe for high concurrency."
    ],
    'plain_english': "We implement a Redis-style in-memory KV engine across 4 enterprise tiers: 1) Core CRUD with ConcurrentHashMap and an Entry wrapper storing expiration timestamps; 2) Passive expiration on `get()` and active sweep; 3) Prefix search using a concurrent TreeMap or Trie; 4) Nested transactions using a stack of delta undo-logs (recording the previous value before each write so rollback restores exact prior state).",
    'boundary_checks': {
        'min_base': "Rollback without transaction <code>rollback()</code> &rarr; Returns false cleanly without crashing.",
        'best_exit': "Zero or negative TTL <code>ttlMs <= 0</code> &rarr; Key immediately treated as expired.",
        'worst_scan': "Deeply nested rollbacks <code>begin -> begin -> rollback -> commit</code> &rarr; Restores only intermediate transaction state accurately."
    },
    'mental_model': {
        'analogy_title': "The Restaurant Kitchen Order Ticket Stack",
        'analogy_text': "Think of an in-memory KV store as an executive kitchen pantry. Each shelf has labeled boxes with freshness expiration tags. When you pull an ingredient, you check the tag—if expired, you throw it away (passive TTL). When a chef says 'Let's try a special dish' (begin transaction), you place an overlay sheet of tracing paper on the clipboard. Every modification is jotted down with the original state. If the chef cancels (rollback), you tear off the overlay sheet. If approved (commit), you ink the changes down permanently.",
        'aha_moment': "Transaction rollback does NOT require copying the entire database! By maintaining an 'Undo Log' (delta map of original states before each modification), a rollback simply replays the undo log in reverse order in O(T) time where T is the number of writes in that transaction.",
        'trigger': "Build in-memory storage engine with TTL, prefix scan, and rollback &rarr; ConcurrentHashMap + Expiry Metadata + Undo-Log Stack.",
        'sisters': [
            {'name': "Design In-Memory File System", 'lc': "LC #588"},
            {'name': "Time Based Key-Value Store", 'lc': "LC #981"},
            {'name': "LRU Cache", 'lc': "LC #146"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(Database Size) Space per Transaction",
        'naive_text': "Deep clone the entire database HashMap on every `beginTransaction()`.",
        'wasted_work': "Cloning millions of keys on every transaction level; massive memory spikes and latency freezes.",
        'optimal_complexity': "O(Mutations) Space per Transaction, O(1) CRUD",
        'optimal_text': "Delta logging: each transaction maintains an UndoMap<Key, OldValue>. On rollback, apply only the captured deltas.",
        'core_invariant': "An active transaction's undo log stores the state of key `k` as it existed before the transaction's first mutation to `k`.",
        'table_rows': [
            {'name': "1. Full Database Snapshots", 'time': "Begin: O(N), Rollback: O(1)", 'space': "O(D * N)", 'note': "Catastrophic memory overhead on large databases."},
            {'name': "2. Global Mutex Lock", 'time': "O(1) CRUD", 'space': "O(1)", 'note': "Bottlenecks multi-threaded reader throughput."},
            {'name': "★ 3. ConcurrentMap + Delta Undo Stacks", 'time': "Begin: O(1), CRUD: O(1), Rollback: O(T)", 'space': "O(T) Auxiliary", 'note': "Enterprise standard; minimal memory footprint."}
        ]
    },
    'trace_table': {
        'input_desc': 'set("k","v1") -> begin() -> set("k","v2") -> set("k2","x") -> rollback()',
        'columns': ["Step", "Method Call", "Main Storage", "Undo Stack Top", "Return", "State Explanation"],
        'rows': [
            ["1", "set(\"k\", \"v1\")", "{k: \"v1\"}", "[ ]", "void", "Key 'k' stored at root level"],
            ["2", "begin()", "{k: \"v1\"}", "[ {} ]", "void", "Pushed empty delta log for Tx 1"],
            ["3", "set(\"k\", \"v2\")", "{k: \"v2\"}", "[ {k: \"v1\"} ]", "void", "Recorded original value \"v1\" into Tx 1 delta log"],
            ["4", "set(\"k2\", \"x\")", "{k: \"v2\", k2: \"x\"}", "[ {k: \"v1\", k2: null} ]", "void", "Recorded k2: null (did not exist before Tx 1)"],
            ["5", "rollback()", "{k: \"v1\"}", "[ ]", "true", "Replayed delta log: restored k=\"v1\", deleted k2. Stack popped"]
        ],
        'takeaway': "Rollback cleanly restored k back to \"v1\" and removed k2 in O(T) time without full DB clones."
    },
    'stepper': {
        'title': "In-Memory KV Store Transaction & TTL Stepper",
        'default_input': 'set(k,v1) -> begin -> set(k,v2) -> rollback',
        'presets': [
            {'label': "Transaction Rollback", 'onclick': 'loadPreset("txn")'},
            {'label': "TTL Expiration", 'onclick': 'loadPreset("ttl")'},
            {'label': "Prefix Scan", 'onclick': 'loadPreset("scan")'}
        ],
        'metrics': [
            {'id': "metric-op", 'label': "Operation", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-tx-depth", 'label': "Tx Stack Depth", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-active-keys", 'label': "Active Live Keys", 'color': "text-purple-400", 'initial': "0"},
            {'id': "metric-status", 'label': "Engine Status", 'color': "text-emerald-400", 'initial': "Idle"}
        ],
        'visual_html': '<div id="p67-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace transactions, TTL expiration, and undo-log management.',
        'pseudocode_lines': [
            "class KVStore {",
            "    val store = ConcurrentHashMap<String, Entry>()",
            "    val undoStack = ArrayDeque<MutableMap<String, String?>>()",
            "    fun set(k, v) { if (inTx) recordUndo(k); store[k] = Entry(v) }",
            "    fun rollback() { val log = undoStack.pop(); log.forEach { (k, old) -> if (old==null) store.remove(k) else store[k] = Entry(old) } }",
            "}"
        ],
        'js_code': """
    let currentScenario = "txn";

    function buildSteps(scenario) {
      steps = [];
      if (scenario === "txn") {
        steps.push({ op: "init", depth: 0, liveKeys: 0, status: "Ready", store: {}, undo: [], line: 1, narration: "Initialized KV engine." });
        steps.push({ op: 'set("balance", "100")', depth: 0, liveKeys: 1, status: "Set key", store: { balance: "100" }, undo: [], line: 4, narration: 'Stored "balance" = "100" permanently.' });
        steps.push({ op: "beginTransaction()", depth: 1, liveKeys: 1, status: "Txn Started", store: { balance: "100" }, undo: [{ title: "Txn 1", deltas: {} }], line: 3, narration: "Started Transaction 1. Pushed empty undo-log." });
        steps.push({ op: 'set("balance", "200")', depth: 1, liveKeys: 1, status: "Overwrote balance", store: { balance: "200" }, undo: [{ title: "Txn 1", deltas: { balance: "100" } }], line: 4, narration: 'Overwrote "balance" = "200". Recorded original value "100" into undo-log.' });
        steps.push({ op: 'set("bonus", "50")', depth: 1, liveKeys: 2, status: "Inserted bonus", store: { balance: "200", bonus: "50" }, undo: [{ title: "Txn 1", deltas: { balance: "100", bonus: "null (did not exist)" } }], line: 4, narration: 'Created new key "bonus" = "50". Recorded undo delta: bonus -> null.' });
        steps.push({ op: "rollback()", depth: 0, liveKeys: 1, status: "Rollback Complete", store: { balance: "100" }, undo: [], line: 5, narration: 'Rollback triggered! Replayed undo-log: restored "balance"="100", deleted "bonus". Popped Txn 1.' });
      } else if (scenario === "ttl") {
        steps.push({ op: "init", depth: 0, liveKeys: 0, status: "Ready", store: {}, undo: [], line: 1, narration: "Initialized KV engine with TTL support." });
        steps.push({ op: 'setWithTTL("token", "abc", 1000ms)', depth: 0, liveKeys: 1, status: "Set with TTL", store: { token: 'abc (expires in 1000ms)' }, undo: [], line: 4, narration: 'Set key "token" with TTL = 1000ms.' });
        steps.push({ op: 'get("token") at t = 200ms', depth: 0, liveKeys: 1, status: "Cache Hit", store: { token: 'abc (800ms remaining)' }, undo: [], line: 4, narration: 'get("token") at t=200ms: Not expired. Returned "abc".' });
        steps.push({ op: 'time advances to t = 1200ms', depth: 0, liveKeys: 1, status: "Time Advanced", store: { token: 'abc (EXPIRED)' }, undo: [], line: 4, narration: 'Wall clock advanced past 1000ms. Token is now expired.' });
        steps.push({ op: 'get("token") at t = 1200ms', depth: 0, liveKeys: 0, status: "Evicted on Read", store: {}, undo: [], line: 4, narration: 'Passive eviction: get("token") detected expiration, removed key from store, returned null.' });
      }
    }

    function renderVisual(s) {
      const stage = document.getElementById('p67-stage');
      if (!stage) return;
      let html = '<div class="grid grid-cols-1 md:grid-cols-2 gap-4 w-full max-w-3xl">';

      // Main Store
      html += '<div class="p-3 rounded-lg bg-slate-900 border border-slate-800 space-y-2">';
      html += '<span class="text-xs font-mono text-emerald-400 font-bold block">Live Key-Value Store:</span>';
      const keys = Object.keys(s.store);
      if (keys.length === 0) {
        html += '<div class="text-xs font-mono text-slate-500 italic">Store is empty</div>';
      } else {
        keys.forEach(k => {
          html += `<div class="p-1.5 rounded bg-slate-950 border border-slate-700 font-mono text-xs flex justify-between"><span class="text-amber-300 font-bold">${k}:</span><span class="text-slate-200">${s.store[k]}</span></div>`;
        });
      }
      html += '</div>';

      // Undo Stack
      html += '<div class="p-3 rounded-lg bg-slate-900 border border-slate-800 space-y-2">';
      html += '<span class="text-xs font-mono text-cyan-400 font-bold block">Transaction Undo Stack:</span>';
      if (s.undo.length === 0) {
        html += '<div class="text-xs font-mono text-slate-500 italic">No active transactions</div>';
      } else {
        s.undo.forEach(u => {
          html += `<div class="p-2 rounded bg-slate-950 border border-cyan-800/60 font-mono text-xs space-y-1"><span class="text-cyan-300 font-bold">${u.title}</span>`;
          Object.keys(u.deltas).forEach(dk => {
            html += `<div class="text-[11px] text-slate-400 flex justify-between"><span>restore ${dk}:</span><span class="text-amber-400">${u.deltas[dk]}</span></div>`;
          });
          html += '</div>';
        });
      }
      html += '</div>';

      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-op').textContent = s.op;
      document.getElementById('metric-tx-depth').textContent = s.depth;
      document.getElementById('metric-active-keys').textContent = Object.keys(s.store).length;
      document.getElementById('metric-status').textContent = s.status;
    }

    function loadPreset(scenario) {
      currentScenario = scenario;
      buildSteps(currentScenario);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Full Production Implementation: In-Memory KV Store with TTL & Transactions",
            'badge': "Production",
            'color': "emerald",
            'complexity': "CRUD: O(1), Scan: O(K + log N), Rollback: O(T)",
            'summary': "Multi-tier design: ConcurrentHashMap for core storage, ConcurrentSkipListMap for prefix scans, passive & active TTL expiration, and a stack of delta maps for O(T) transaction rollback.",
            'kotlin_code': """import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.ArrayDeque

class InMemoryKVStore {
    private data class ValueEntry(val value: String, val expiresAt: Long = Long.MAX_VALUE) {
        fun isExpired(now: Long = System.currentTimeMillis()) = now >= expiresAt
    }

    // Core data structures
    private val store = ConcurrentHashMap<String, ValueEntry>()
    private val sortedIndex = ConcurrentSkipListMap<String, String>()
    private val txStack = ArrayDeque<MutableMap<String, ValueEntry?>>()

    @Synchronized
    fun set(key: String, value: String) {
        setWithTTL(key, value, Long.MAX_VALUE)
    }

    @Synchronized
    fun setWithTTL(key: String, value: String, ttlMs: Long) {
        val expiresAt = if (ttlMs == Long.MAX_VALUE) Long.MAX_VALUE else System.currentTimeMillis() + ttlMs
        recordUndo(key)
        store[key] = ValueEntry(value, expiresAt)
        sortedIndex[key] = value
    }

    fun get(key: String): String? {
        val entry = store[key] ?: return null
        if (entry.isExpired()) {
            delete(key) // Passive eviction
            return null
        }
        return entry.value
    }

    @Synchronized
    fun delete(key: String): Boolean {
        if (!store.containsKey(key)) return false
        recordUndo(key)
        store.remove(key)
        sortedIndex.remove(key)
        return true
    }

    fun scan(prefix: String): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        val subMap = sortedIndex.tailMap(prefix)
        for ((k, v) in subMap) {
            if (!k.startsWith(prefix)) break
            if (get(k) != null) { // Validates TTL on the fly
                result.add(Pair(k, v))
            }
        }
        return result
    }

    @Synchronized
    fun beginTransaction() {
        txStack.push(mutableMapOf())
    }

    @Synchronized
    fun commit(): Boolean {
        if (txStack.isEmpty()) return false
        val committedDeltas = txStack.pop()
        // If nested, merge into parent transaction
        if (txStack.isNotEmpty()) {
            val parent = txStack.peek()
            committedDeltas.forEach { (k, old) ->
                parent.putIfAbsent(k, old)
            }
        }
        return true
    }

    @Synchronized
    fun rollback(): Boolean {
        if (txStack.isEmpty()) return false
        val deltas = txStack.pop()
        deltas.forEach { (k, originalEntry) ->
            if (originalEntry == null) {
                store.remove(k)
                sortedIndex.remove(k)
            } else {
                store[k] = originalEntry
                sortedIndex[k] = originalEntry.value
            }
        }
        return true
    }

    private fun recordUndo(key: String) {
        if (txStack.isNotEmpty()) {
            val currentTx = txStack.peek()
            if (!currentTx.containsKey(key)) {
                currentTx[key] = store[key]
            }
        }
    }
}""",
            'python_code': """import time
from collections import deque

class ValueEntry:
    def __init__(self, value: str, expires_at: float = float('inf')):
        self.value = value
        self.expires_at = expires_at

    def is_expired(self) -> bool:
        return time.time() >= self.expires_at

class InMemoryKVStore:
    def __init__(self):
        self.store = {}
        self.tx_stack = deque()

    def set(self, key: str, value: str) -> None:
        self.set_with_ttl(key, value, float('inf'))

    def set_with_ttl(self, key: str, value: str, ttl_ms: float) -> None:
        expires_at = float('inf') if ttl_ms == float('inf') else time.time() + (ttl_ms / 1000.0)
        self._record_undo(key)
        self.store[key] = ValueEntry(value, expires_at)

    def get(self, key: str) -> str | None:
        if key not in self.store:
            return None
        entry = self.store[key]
        if entry.is_expired():
            self.delete(key)
            return None
        return entry.value

    def delete(self, key: str) -> bool:
        if key not in self.store:
            return False
        self._record_undo(key)
        del self.store[key]
        return True

    def scan(self, prefix: str) -> list[tuple[str, str]]:
        res = []
        for k in sorted(self.store.keys()):
            if k.startswith(prefix):
                val = self.get(k)
                if val is not None:
                    res.append((k, val))
        return res

    def begin_transaction(self) -> None:
        self.tx_stack.append({})

    def commit(self) -> bool:
        if not self.tx_stack:
            return False
        deltas = self.tx_stack.pop()
        if self.tx_stack:
            parent = self.tx_stack[-1]
            for k, old in deltas.items():
                if k not in parent:
                    parent[k] = old
        return True

    def rollback(self) -> bool:
        if not self.tx_stack:
            return False
        deltas = self.tx_stack.pop()
        for k, old_entry in deltas.items():
            if old_entry is None:
                self.store.pop(k, None)
            else:
                self.store[k] = old_entry
        return True

    def _record_undo(self, key: str) -> None:
        if self.tx_stack:
            current_tx = self.tx_stack[-1]
            if key not in current_tx:
                current_tx[key] = self.store.get(key)""",
            'java_code': """import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class InMemoryKVStore {
    private static class ValueEntry {
        final String value;
        final long expiresAt;

        ValueEntry(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= expiresAt;
        }
    }

    private final Map<String, ValueEntry> store = new ConcurrentHashMap<>();
    private final ConcurrentSkipListMap<String, String> sortedIndex = new ConcurrentSkipListMap<>();
    private final Deque<Map<String, ValueEntry>> txStack = new ArrayDeque<>();

    public synchronized void set(String key, String value) {
        setWithTTL(key, value, Long.MAX_VALUE);
    }

    public synchronized void setWithTTL(String key, String value, long ttlMs) {
        long expiresAt = (ttlMs == Long.MAX_VALUE) ? Long.MAX_VALUE : System.currentTimeMillis() + ttlMs;
        recordUndo(key);
        store.put(key, new ValueEntry(value, expiresAt));
        sortedIndex.put(key, value);
    }

    public String get(String key) {
        ValueEntry entry = store.get(key);
        if (entry == null) return null;
        if (entry.isExpired()) {
            delete(key);
            return null;
        }
        return entry.value;
    }

    public synchronized boolean delete(String key) {
        if (!store.containsKey(key)) return false;
        recordUndo(key);
        store.remove(key);
        sortedIndex.remove(key);
        return true;
    }

    public List<Map.Entry<String, String>> scan(String prefix) {
        List<Map.Entry<String, String>> list = new ArrayList<>();
        NavigableMap<String, String> subMap = sortedIndex.tailMap(prefix, true);
        for (Map.Entry<String, String> e : subMap.entrySet()) {
            if (!e.getKey().startsWith(prefix)) break;
            if (get(e.getKey()) != null) {
                list.add(e);
            }
        }
        return list;
    }

    public synchronized void beginTransaction() {
        txStack.push(new HashMap<>());
    }

    public synchronized boolean commit() {
        if (txStack.isEmpty()) return false;
        Map<String, ValueEntry> committed = txStack.pop();
        if (!txStack.isEmpty()) {
            Map<String, ValueEntry> parent = txStack.peek();
            for (Map.Entry<String, ValueEntry> e : committed.entrySet()) {
                parent.putIfAbsent(e.getKey(), e.getValue());
            }
        }
        return true;
    }

    public synchronized boolean rollback() {
        if (txStack.isEmpty()) return false;
        Map<String, ValueEntry> deltas = txStack.pop();
        for (Map.Entry<String, ValueEntry> e : deltas.entrySet()) {
            if (e.getValue() == null) {
                store.remove(e.getKey());
                sortedIndex.remove(e.getKey());
            } else {
                store.put(e.getKey(), e.getValue());
                sortedIndex.put(e.getKey(), e.getValue().value);
            }
        }
        return true;
    }

    private void recordUndo(String key) {
        if (!txStack.isEmpty()) {
            Map<String, ValueEntry> currentTx = txStack.peek();
            if (!currentTx.containsKey(key)) {
                currentTx.put(key, store.get(key));
            }
        }
    }
}""",
            'memory_breakdown': "O(N) base storage + O(M) for active transactions where M is the number of mutated keys during that transaction. Strict constant overhead when no transactions are active."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class InMemoryKVStore {
    // Write your thread-safe KV Store with TTL and Transactions
    private data class Entry(val v: String, val exp: Long = Long.MAX_VALUE)
    private val store = HashMap<String, Entry>()
    private val txStack = java.util.ArrayDeque<MutableMap<String, Entry?>>()

    fun set(k: String, v: String) { setWithTTL(k, v, Long.MAX_VALUE) }
    fun setWithTTL(k: String, v: String, ttlMs: Long) {
        val exp = if (ttlMs == Long.MAX_VALUE) Long.MAX_VALUE else System.currentTimeMillis() + ttlMs
        if (txStack.isNotEmpty()) txStack.peek().putIfAbsent(k, store[k])
        store[k] = Entry(v, exp)
    }
    fun get(k: String): String? {
        val e = store[k] ?: return null
        if (System.currentTimeMillis() >= e.exp) { store.remove(k); return null }
        return e.v
    }
    fun beginTransaction() { txStack.push(mutableMapOf()) }
    fun commit(): Boolean = txStack.pollFirst() != null
    fun rollback(): Boolean {
        val deltas = txStack.pollFirst() ?: return false
        deltas.forEach { (k, old) -> if (old == null) store.remove(k) else store[k] = old }
        return true
    }
}""",
        'python_boilerplate': """import time

class InMemoryKVStore:
    # Write your thread-safe KV Store with TTL and Transactions
    def __init__(self):
        self.store = {}
        self.tx = []
    def set(self, k: str, v: str): self.set_ttl(k, v, float('inf'))
    def set_ttl(self, k: str, v: str, ttl_ms: float):
        exp = float('inf') if ttl_ms == float('inf') else time.time() + ttl_ms/1000
        if self.tx and k not in self.tx[-1]: self.tx[-1][k] = self.store.get(k)
        self.store[k] = (v, exp)
    def get(self, k: str):
        if k not in self.store: return None
        v, exp = self.store[k]
        if time.time() >= exp: del self.store[k]; return None
        return v
    def begin_transaction(self): self.tx.append({})
    def commit(self): return bool(self.tx and self.tx.pop() is not None)
    def rollback(self):
        if not self.tx: return False
        for k, old in self.tx.pop().items():
            if old is None: self.store.pop(k, None)
            else: self.store[k] = old
        return True""",
        'java_boilerplate': """import java.util.*;

public class InMemoryKVStore {
    static class Entry { String v; long exp; Entry(String v, long exp) { this.v=v; this.exp=exp; } }
    Map<String, Entry> store = new HashMap<>();
    Deque<Map<String, Entry>> tx = new ArrayDeque<>();

    public void set(String k, String v) { setWithTTL(k, v, Long.MAX_VALUE); }
    public void setWithTTL(String k, String v, long ttl) {
        long exp = ttl == Long.MAX_VALUE ? Long.MAX_VALUE : System.currentTimeMillis() + ttl;
        if (!tx.isEmpty()) tx.peek().putIfAbsent(k, store.get(k));
        store.put(k, new Entry(v, exp));
    }
    public String get(String k) {
        Entry e = store.get(k);
        if (e == null) return null;
        if (System.currentTimeMillis() >= e.exp) { store.remove(k); return null; }
        return e.v;
    }
    public void beginTransaction() { tx.push(new HashMap<>()); }
    public boolean commit() { return tx.poll() != null; }
    public boolean rollback() {
        Map<String, Entry> deltas = tx.poll();
        if (deltas == null) return false;
        deltas.forEach((k, old) -> { if (old == null) store.remove(k); else store.put(k, old); });
        return true;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      class KVStore {
        constructor() { this.store = new Map(); this.tx = []; }
        set(k, v) { this.setWithTTL(k, v, Infinity); }
        setWithTTL(k, v, ttl) {
          const exp = ttl === Infinity ? Infinity : Date.now() + ttl;
          if (this.tx.length > 0 && !this.tx[this.tx.length - 1].has(k)) {
            this.tx[this.tx.length - 1].set(k, this.store.get(k) || null);
          }
          this.store.set(k, { v, exp });
        }
        get(k) {
          if (!this.store.has(k)) return null;
          const e = this.store.get(k);
          if (Date.now() >= e.exp) { this.store.delete(k); return null; }
          return e.v;
        }
        beginTransaction() { this.tx.push(new Map()); }
        commit() { return Boolean(this.tx.pop()); }
        rollback() {
          const deltas = this.tx.pop();
          if (!deltas) return false;
          deltas.forEach((old, k) => {
            if (old === null) this.store.delete(k);
            else this.store.set(k, old);
          });
          return true;
        }
      }

      const kv = new KVStore();
      kv.set("a", "1");
      kv.beginTransaction();
      kv.set("a", "2");
      kv.set("b", "3");
      const inTxA = kv.get("a");
      const inTxB = kv.get("b");
      kv.rollback();
      const afterA = kv.get("a");
      const afterB = kv.get("b");

      const pass = inTxA === "2" && inTxB === "3" && afterA === "1" && afterB === null;
      const row = document.createElement('div');
      row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
      row.textContent = `Test: Txn get(a)="${inTxA}", get(b)="${inTxB}" &rarr; After rollback get(a)="${afterA}", get(b)=${afterB} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the core requirements: we need an in-memory key-value store that supports basic CRUD operations, TTL expiration, sorted prefix scanning, and multi-level transaction rollback. What concurrency expectations exist? It should support high-throughput concurrent reads and atomic transaction updates.\"",
        'phase2': "\"For storage, I use a `ConcurrentHashMap` for O(1) key-value lookups paired with a `ConcurrentSkipListMap` to maintain lexicographical order for `scan(prefix)`. To handle TTL, each entry wraps its value with an `expiresAt` epoch millisecond timestamp. On `get()`, we perform passive expiration—if `now >= expiresAt`, delete the key and return null. For transactions, rather than taking a full snapshot of the database (which is memory-prohibitive), we maintain a stack of delta undo-logs. When `beginTransaction()` is called, we push a new empty undo-map. On any write, if the key hasn't been recorded in the current transaction, we save its prior state. On `rollback()`, we simply replay the undo log in reverse order, restoring original values and removing newly added keys in O(T) time where T is mutations made.\"",
        'phase3': "\"Let me write the code. I define `ValueEntry(value, expiresAt)`. The `store` map holds entries. `txStack` is a `Deque<Map<String, ValueEntry>>`. In `recordUndo(key)`, if `txStack` is non-empty and the current map doesn't have `key`, put `(key, store.get(key))`. In `rollback()`, pop the top map, iterate through entries: if value was null, remove key; otherwise put it back. `commit()` simply pops the top undo map, merging into the parent transaction if nested.\"",
        'phase4': "\"Testing with the rollback flow: `set(\"a\", \"1\")`. Call `beginTransaction()`. Call `set(\"a\", \"2\")` &rarr; undo log captures `a: \"1\"`. Call `set(\"b\", \"3\")` &rarr; undo log captures `b: null`. Querying inside the transaction gives `a=\"2\", b=\"3\"`. Call `rollback()`: key 'a' is restored to \"1\", key 'b' is deleted. Querying outside gives `a=\"1\", b=null`. Tested TTL expiration: after expiration, `get()` returns null. Concurrency is guaranteed by synchronized mutation blocks and concurrent maps.\"",
        'senior_followup': "\"In distributed production systems like Redis or Etcd, passive eviction is supplemented with an active background sampling sweep (e.g. testing 20 random keys with TTL per 100ms) to reclaim expired memory that is never read again. For Raft/Paxos consensus, transaction logs are written to Write-Ahead Logs (WAL) before applying to state machines.\""
    },
    'quiz': [
        {
            'question': "Why is an undo-log delta map superior to full database snapshotting for transactions?",
            'options': [
                "It only consumes memory proportional to mutations made in the transaction (O(T))",
                "It converts multi-threaded memory access into non-blocking atomic CAS operations",
                "It enables the garbage collector to purge expired TTL keys without CPU interrupts",
                "Full database snapshots are prohibited by JVM specification security limits"
            ],
            'correct_index': 0,
            'explanation': "Deep copying the entire database on `beginTransaction` requires O(N) memory and time. An undo log only records the original values of modified keys, requiring only O(T) space where T is the number of writes in that transaction."
        },
        {
            'question': "What is the difference between passive eviction and active eviction for TTL keys?",
            'options': [
                "Passive evicts on `get()`; active uses a background thread to periodically sample and sweep",
                "Passive evicts immediately on write; active evicts only when memory usage reaches 90%",
                "Passive applies only to integer values; active applies to string and binary payloads",
                "Passive requires distributed consensus; active operates purely within a single thread"
            ],
            'correct_index': 0,
            'explanation': "Passive eviction checks expiration only when a key is accessed. Active eviction runs a background daemon thread that periodically checks keys to reclaim memory for keys that are never queried again."
        }
    ]
}
