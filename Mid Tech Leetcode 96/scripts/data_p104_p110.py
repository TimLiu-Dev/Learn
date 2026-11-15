"""
Curriculum definitions for Problems 104 to 110 (2027 Mid-Tech Comprehensive Completion Set):
#104: LC #271 Encode and Decode Strings (Pass 2A Core Medium - Length-Prefix Framing)
#105: LC #230 Kth Smallest Element in a BST (Pass 2A Core Medium - Iterative In-Order BST Traversal)
#106: LC #105 Construct Binary Tree from Preorder and Inorder Traversal (Pass 2B Stretch Medium - Tree Reconstruction)
#107: LC #621 Task Scheduler (Pass 2B Stretch Medium - Max-Heap + Cooldown Queue / Greedy Idle Math)
#108: LC #55 Jump Game (Pass 2A Core Medium - Greedy Farthest Reachable Boundary)
#109: LC #416 Partition Equal Subset Sum (Pass 2B Stretch Medium - 0/1 Knapsack 1D Bitset/Boolean DP)
#110: LC #91 Decode Ways (Pass 2B Stretch Medium - 1D String Partitioning DP with Zero Guardrails)
"""

# =========================================================================
# PROBLEM 104: LC #271 ENCODE AND DECODE STRINGS (Pass 2A Core Medium)
# =========================================================================
p104 = {
    "num": 104,
    "lc": 271,
    "title": "Encode and Decode Strings",
    "category": "Arrays, Strings & Hashing",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "encode-and-decode-strings",
    "verbatim_statement": "Design an algorithm to encode a list of strings to a single string. The encoded string is then sent over the network and is decoded back to the original list of strings.<br><br>Implement <code>encode</code> and <code>decode</code> methods.<br><br>You are not allowed to solve the problem using any serialize methods (such as <code>eval</code> or <code>JSON.stringify</code>). The input strings may contain any possible characters out of 256 valid ASCII characters.",
    "examples": [
        {
            "title": "Example 1",
            "input": "strs = [\"neet\",\"code\",\"love\",\"you\"]",
            "output": "[\"neet\",\"code\",\"love\",\"you\"]"
        },
        {
            "title": "Example 2",
            "input": "strs = [\"we\",\"say\",\":\",\"yes\"]",
            "output": "[\"we\",\"say\",\":\",\"yes\"]"
        },
        {
            "title": "Example 3",
            "input": "strs = [\"\",\"a#b\",\"\"]",
            "output": "[\"\",\"a#b\",\"\"]"
        }
    ],
    "constraints": [
        "1 <= strs.length <= 200",
        "0 <= strs[i].length <= 200",
        "strs[i] contains any possible characters out of 256 valid ASCII characters."
    ],
    "plain_english": "Pack a list of arbitrary strings into one flat string so that you can unpack it back into the exact original list—even if the strings themselves contain colons, hashes, numbers, or empty strings.",
    "boundary_checks": {
        "min_base": "strs = [\"\"] (Single empty string) -> Encodes to \"0#\", decodes back to [\"\"].",
        "best_exit": "strs = [\"a\", \"b\"] -> Simple length-prefixed framing \"1#a1#b\".",
        "worst_scan": "strs = [\"4#fake\", \"10#12345\"] -> Contains delimiter '#' and digits inside payload."
    },
    "mental_model": {
        "analogy_title": "HTTP Content-Length Packet Framing",
        "analogy_text": "When a web server sends raw bytes over TCP, it cannot rely on a special 'end-of-message' character because the payload itself (like an image or text) might contain that exact byte! Instead, HTTP headers declare 'Content-Length: 4\\r\\n' followed by raw payload bytes. Once the reader sees the header delimiter '#', it stops looking for delimiters altogether and blindly reads the exact number of characters announced by the length.",
        "aha_moment": "Prefix every string with its character count and a single delimiter '#' (e.g., '4#neet4#code'). During decoding, only scan for '#' to parse the length integer L, then jump exactly L characters forward!",
        "trigger": "Serializing variable-length strings or binary payloads that may contain any delimiter character.",
        "sisters": [
            {"name": "String Compression", "lc": "#443"},
            {"name": "Serialize and Deserialize Binary Tree", "lc": "#297"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N) Time, Fragile / Incorrect on Collisions",
        "naive_text": "Joining strings with a single delimiter like ',' or '#' (e.g., strs.joinToString(\"#\")) and calling split(\"#\").",
        "wasted_work": "Breaks immediately when an input string naturally contains '#' or when empty strings collide with delimiter boundaries.",
        "optimal_complexity": "O(C) Time, O(1) Aux Space (where C = total characters)",
        "optimal_text": "Write '<length>#<string>' for each item. Decode with a pointer i: find the next '#' from i, parse the integer length in s[i..hashIdx-1], slice s[hashIdx+1 .. hashIdx+1+len], and advance i directly to the end of the slice.",
        "core_invariant": "Pointer i always lands on the first digit of the next chunk's length header—never inside a string payload.",
        "table_rows": [
            {
                "name": "Naive: Simple Delimiter Split",
                "time": "O(C)",
                "space": "O(C)",
                "note": "Fails when payload contains delimiter characters."
            },
            {
                "name": "Better: Character Escaping (\\\\#)",
                "time": "O(C)",
                "space": "O(C)",
                "note": "Correct, but requires char-by-char state machine scanning and doubles escape characters."
            },
            {
                "name": "Optimal: Length-Prefix Framing (<len>#<str>)",
                "time": "O(C)",
                "space": "O(1) aux",
                "note": "★ Production protocol standard (HTTP/gRPC/Redis RESP). Zero escaping needed."
            }
        ]
    },
    "trace_table": {
        "input_desc": "Encoded string s = \"4#neet2#hi0#\" (from [\"neet\", \"hi\", \"\"])",
        "columns": ["Step", "Pointer i", "hashIdx ('#')", "Parsed len", "Extracted Slice", "Next i", "Decoded List"],
        "rows": [
            ["1", "0", "1", "4", "s.substring(2, 6) -> \"neet\"", "6", "[\"neet\"]"],
            ["2", "6", "7", "2", "s.substring(8, 10) -> \"hi\"", "10", "[\"neet\", \"hi\"]"],
            ["3", "10", "11", "0", "s.substring(12, 12) -> \"\"", "12 (== s.length, stop)", "[\"neet\", \"hi\", \"\"]"]
        ],
        "takeaway": "Even if a string contained '99#tricky', jumping by len skips right over the internal '#' without ever misinterpreting it as a header!"
    },
    "stepper": {
        "title": "Length-Prefix Framing Decoder Stepper",
        "default_input": "4#neet4#code1#:",
        "presets": [
            {"label": "Standard [\"neet\",\"code\"]", "onclick": "loadPreset('4#neet4#code')"},
            {"label": "Embedded '#' [\"a#b\",\"\"]", "onclick": "loadPreset('3#a#b0#')"},
            {"label": "Multi-digit length", "onclick": "loadPreset('10#01234567892#ok')"}
        ],
        "metrics": [
            {"label": "Header Start (i)", "id": "metric-i", "initial": "0", "color": "text-emerald-400"},
            {"label": "Delimiter (#)", "id": "metric-hash", "initial": "-", "color": "text-amber-400"},
            {"label": "Payload Length", "id": "metric-len", "initial": "-", "color": "text-cyan-400"},
            {"label": "Decoded Count", "id": "metric-cnt", "initial": "0", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='framing-viz' class='flex flex-wrap items-center justify-center gap-1'></div>",
        "initial_narration": "Ready to decode length-prefixed stream. Pointer i starts at index 0.",
        "pseudocode_lines": [
            "var i = 0; val res = mutableListOf<String>()",
            "while (i < s.length) {",
            "  val hashIdx = s.indexOf('#', i)",
            "  val len = s.substring(i, hashIdx).toInt()",
            "  res.add(s.substring(hashIdx + 1, hashIdx + 1 + len))",
            "  i = hashIdx + 1 + len",
            "}"
        ],
        "js_code": """
        let currentEncoded = "4#neet4#code";
        function buildSteps(cand) {
          if (typeof cand === 'string' && cand.includes('#')) currentEncoded = cand;
          steps = [];
          let s = currentEncoded;
          let i = 0;
          let decoded = [];
          steps.push({ i: 0, hashIdx: -1, len: '-', decoded: [], line: 1, narration: `Initialized decoder on stream "${s}" (length ${s.length}).` });
          while (i < s.length) {
            let hashIdx = s.indexOf('#', i);
            if (hashIdx === -1) break;
            let len = parseInt(s.substring(i, hashIdx), 10) || 0;
            let word = s.substring(hashIdx + 1, hashIdx + 1 + len);
            decoded.push(word);
            steps.push({
              i, hashIdx, len, wordStart: hashIdx + 1, wordEnd: hashIdx + 1 + len,
              decoded: [...decoded], line: 5,
              narration: `Header at ${i}..${hashIdx}: length=${len}. Sliced "${word}" [${hashIdx+1}..${hashIdx+1+len}). Advancing i to ${hashIdx+1+len}.`
            });
            i = hashIdx + 1 + len;
          }
          steps.push({ i, hashIdx: -1, len: 'Done', decoded: [...decoded], line: 7, narration: `Stream exhausted! Decoded ${decoded.length} strings: ${JSON.stringify(decoded)}.` });
        }
        function renderStage(step) {
          const c = document.getElementById('framing-viz');
          if (!c) return;
          c.innerHTML = currentEncoded.split('').map((ch, idx) => {
            let cls = 'border-slate-800 bg-slate-900 text-slate-300';
            if (idx === step.hashIdx) cls = 'border-amber-400 bg-amber-950/60 text-amber-300';
            else if (step.hashIdx !== -1 && idx >= step.i && idx < step.hashIdx) cls = 'border-emerald-400 bg-emerald-950/60 text-emerald-300';
            else if (step.wordStart !== undefined && idx >= step.wordStart && idx < step.wordEnd) cls = 'border-cyan-400 bg-cyan-950/60 text-cyan-300';
            return `<div class="w-9 h-12 rounded-lg border flex flex-col items-center justify-center font-mono text-xs ${cls}">
              <span class="text-[9px] text-slate-500">${idx}</span>
              <span class="font-bold">${ch}</span>
            </div>`;
          }).join('');
        }
        function loadPreset(s) { currentEncoded = s; buildSteps(s); stepperReset(); }
        """,
        "js_update_metrics": """
        document.getElementById('metric-i').textContent = step.i;
        document.getElementById('metric-hash').textContent = step.hashIdx >= 0 ? step.hashIdx : '-';
        document.getElementById('metric-len').textContent = step.len;
        document.getElementById('metric-cnt').textContent = step.decoded ? step.decoded.length : 0;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Non-ASCII Delimiter Split",
            "badge": "Naive",
            "desc": "Join strings using a rare non-ASCII delimiter (e.g., Char(257)) and split. Fails if full Unicode is permitted or if empty list is ambiguous.",
            "time": "O(C)",
            "space": "O(C)",
            "code_kotlin": """class Solution {
    fun encode(strs: List<String>): String {
        if (strs.isEmpty()) return ""
        return strs.joinToString(separator = "\\u0100")
    }

    fun decode(s: String): List<String> {
        if (s.isEmpty()) return emptyList()
        return s.split("\\u0100")
    }
}""",
            "code_python": """class Solution:
    def encode(self, strs: list[str]) -> str:
        return "\\u0100".join(strs)

    def decode(self, s: str) -> list[str]:
        return s.split("\\u0100")""",
            "code_java": """import java.util.*;

class Solution {
    public String encode(List<String> strs) {
        return String.join("\\u0100", strs);
    }

    public List<String> decode(String s) {
        return Arrays.asList(s.split("\\u0100", -1));
    }
}""",
            "notes_kotlin": "Distinguishing [] vs [\"\"] is broken when encoding to an empty string without framing.",
            "notes_python": "Relies on out-of-band character assumption, which interviewers immediately challenge in follow-ups.",
            "notes_java": "Requires split(delim, -1) to preserve trailing empty strings."
        },
        {
            "name": "Approach 2: Character Escaping State Machine",
            "badge": "Better",
            "desc": "Double every '/' as '//' and terminate each string with '/:'. Requires scanning every character during both encode and decode.",
            "time": "O(C)",
            "space": "O(C)",
            "code_kotlin": """class Solution {
    fun encode(strs: List<String>): String = buildString {
        for (str in strs) {
            for (ch in str) {
                if (ch == '/') append("//") else append(ch)
            }
            append("/:")
        }
    }

    fun decode(s: String): List<String> {
        val res = mutableListOf<String>()
        val sb = StringBuilder()
        var i = 0
        while (i < s.length) {
            if (s[i] == '/') {
                if (s[i + 1] == '/') {
                    sb.append('/')
                } else if (s[i + 1] == ':') {
                    res.add(sb.toString())
                    sb.setLength(0)
                }
                i += 2
            } else {
                sb.append(s[i++])
            }
        }
        return res
    }
}""",
            "code_python": """class Solution:
    def encode(self, strs: list[str]) -> str:
        return "".join(s.replace("/", "//") + "/:" for s in strs)

    def decode(self, s: str) -> list[str]:
        res, cur, i = [], [], 0
        while i < len(s):
            if s[i:i+2] == "//":
                cur.append("/")
                i += 2
            elif s[i:i+2] == "/:":
                res.append("".join(cur))
                cur = []
                i += 2
            else:
                cur.append(s[i])
                i += 1
        return res""",
            "code_java": """import java.util.*;

class Solution {
    public String encode(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str.replace("/", "//")).append("/:");
        }
        return sb.toString();
    }

    public List<String> decode(String s) {
        List<String> res = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            if (s.charAt(i) == '/') {
                if (s.charAt(i + 1) == '/') cur.append('/');
                else { res.add(cur.toString()); cur.setLength(0); }
                i += 2;
            } else {
                cur.append(s.charAt(i++));
            }
        }
        return res;
    }
}""",
            "notes_kotlin": "Works for any character set, but forces char-by-char inspection instead of fast block memory copies.",
            "notes_python": "String replacement allocates intermediate copies per string.",
            "notes_java": "StringBuilder.setLength(0) reuses the internal char[] buffer across words."
        },
        {
            "name": "Approach 3: Length-Prefix Framing (<len>#<str>)",
            "badge": "Optimal",
            "desc": "Prefix each string with its character length followed by '#'. Decode by parsing the length header and slicing the exact byte range in O(1) jumps.",
            "time": "O(C)",
            "space": "O(1) auxiliary",
            "code_kotlin": """class Solution {
    fun encode(strs: List<String>): String = buildString {
        for (str in strs) {
            append(str.length).append('#').append(str)
        }
    }

    fun decode(s: String): List<String> {
        val result = ArrayList<String>()
        var i = 0
        while (i < s.length) {
            val hashIdx = s.indexOf('#', startIndex = i)
            val length = s.substring(i, hashIdx).toInt()
            val start = hashIdx + 1
            result.add(s.substring(start, start + length))
            i = start + length
        }
        return result
    }
}""",
            "code_python": """class Solution:
    def encode(self, strs: list[str]) -> str:
        return "".join(f"{len(s)}#{s}" for s in strs)

    def decode(self, s: str) -> list[str]:
        res = []
        i = 0
        while i < len(s):
            j = s.find("#", i)
            length = int(s[i:j])
            start = j + 1
            res.append(s[start : start + length])
            i = start + length
        return res""",
            "code_java": """import java.util.*;

class Solution {
    public String encode(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str.length()).append('#').append(str);
        }
        return sb.toString();
    }

    public List<String> decode(String s) {
        List<String> res = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            int hashIdx = s.indexOf('#', i);
            int len = Integer.parseInt(s.substring(i, hashIdx));
            int start = hashIdx + 1;
            res.add(s.substring(start, start + len));
            i = start + len;
        }
        return res;
    }
}""",
            "notes_kotlin": "buildString allocates a single StringBuilder. s.indexOf('#', startIndex = i) scans only the tiny digit prefix (1-3 chars).",
            "notes_python": "s.find('#', i) avoids slicing the remaining string, keeping decoding strictly linear O(C).",
            "notes_java": "s.substring(start, start + len) copies the exact payload range in one native System.arraycopy call."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun encode(strs: List<String>): String {
        // Write your encode logic here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def encode(self, strs: list[str]) -> str:
        # Write your encode logic here:
        pass""",
        "java_boilerplate": """class Solution {
    public String encode(List<String> strs) {
        // Write your encode logic here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { strs: ["neet","code","love","you"] }, expected: "4#neet4#code4#love3#you" },
          { input: { strs: ["we","say",":","yes"] }, expected: "2#we3#say1#:3#yes" },
          { input: { strs: ["","a#b",""] }, expected: "0#3#a#b0#" }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Before designing the wire format, can strings in the input list be empty, and can they contain any ASCII character including digits and our delimiter symbol?",
        "phase2": "A naive delimiter split breaks if the delimiter appears inside a string. Escaping works but requires character-by-character state machine scanning. Instead, I'll use Length-Prefix Framing—like HTTP Content-Length or Redis RESP: each string is encoded as '<length>#<payload>'. Both encode and decode run in O(C) time with O(1) auxiliary space.",
        "phase3": "In encode, we iterate over strs and append str.length, '#', and str into a StringBuilder. In decode, we maintain index i = 0. While i < s.length, we locate the next '#' starting from i, parse the integer length in s[i..hashIdx], extract the substring of that exact length starting at hashIdx + 1, and jump i to hashIdx + 1 + length.",
        "phase4": "Let's trace an edge case: strs = [\"\", \"2#a\"]. Encode outputs \"0#3#2#a\". Decode starts at i=0, finds '#' at 1, reads len=0, extracts \"\", jumps i to 2. Next finds '#' at 3, reads len=3, extracts s[4..7] = \"2#a\", jumps i to 7, and exits cleanly.",
        "senior_followup": "In a binary network protocol (like gRPC or Protobuf), instead of ASCII decimal digits + '#', we encode the length as a fixed 4-byte big-endian int32 or a Varint header, eliminating delimiter scanning entirely."
    }
}

# =========================================================================
# PROBLEM 105: LC #230 KTH SMALLEST ELEMENT IN A BST (Pass 2A Core Medium)
# =========================================================================
p105 = {
    "num": 105,
    "lc": 230,
    "title": "Kth Smallest Element in a BST",
    "category": "Binary Trees & BST",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "kth-smallest-element-in-a-bst",
    "verbatim_statement": "Given the <code>root</code> of a binary search tree, and an integer <code>k</code>, return <em>the</em> <code>k<sup>th</sup></code> <em>smallest value (<strong>1-indexed</strong>) of all the values of the nodes in the tree</em>.",
    "examples": [
        {
            "title": "Example 1",
            "input": "root = [3,1,4,null,2], k = 1",
            "output": "1"
        },
        {
            "title": "Example 2",
            "input": "root = [5,3,6,2,4,null,null,1], k = 3",
            "output": "3"
        }
    ],
    "constraints": [
        "The number of nodes in the tree is n.",
        "1 <= k <= n <= 10^4",
        "0 <= Node.val <= 10^4"
    ],
    "plain_english": "Find the k-th smallest number in a Binary Search Tree (where k=1 means the very smallest). Because a BST's In-Order traversal (Left -> Node -> Right) visits nodes in strictly sorted ascending order, we just need to stop on the k-th node visited.",
    "boundary_checks": {
        "min_base": "n = 1, k = 1 -> Single root node returned immediately after 1 left check.",
        "best_exit": "k = 1 on balanced BST -> Dive straight down the leftmost spine in O(log N) steps and return.",
        "worst_scan": "Right-skewed tree with k = N -> Traverses all N nodes down the right spine."
    },
    "mental_model": {
        "analogy_title": "The Iterative Left-Spine Elevator",
        "analogy_text": "In a Binary Search Tree, smaller values always live to the left. Imagine taking an elevator all the way down the left wall of a building until you hit the basement (the 1st smallest value). From there, each time you pop a floor from your call stack, you count it as visited, and if that floor has a right hallway, you take the elevator all the way down the left wall of that right hallway. The moment your counter hits k, you step off and return that floor!",
        "aha_moment": "An In-Order traversal of a BST yields values in sorted ascending order. Using an explicit Stack lets us pause and return immediately at the k-th popped node without visiting the rest of the tree!",
        "trigger": "Finding the k-th smallest/largest or sorted predecessor/successor in a BST.",
        "sisters": [
            {"name": "Validate Binary Search Tree", "lc": "#98"},
            {"name": "Binary Tree Inorder Traversal", "lc": "#94"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N log N) or O(N) Time, O(N) Space",
        "naive_text": "Traverse the entire tree to collect all N node values into an ArrayList, then return list[k - 1].",
        "wasted_work": "Visits all N nodes and allocates an O(N) list even when k = 1 on a 10,000-node tree!",
        "optimal_complexity": "O(H + k) Time, O(H) Space (where H = tree height)",
        "optimal_text": "Use an iterative stack for In-Order traversal: push all left children down to null, pop a node, decrement k, return node.val if k == 0, and move curr to node.right.",
        "core_invariant": "The k-th node popped from the In-Order stack is guaranteed to be the k-th smallest element in the BST.",
        "table_rows": [
            {
                "name": "Naive: Full Tree DFS + Sort",
                "time": "O(N log N)",
                "space": "O(N)",
                "note": "Ignores the BST ordering property completely."
            },
            {
                "name": "Better: Full In-Order Array Capture",
                "time": "O(N)",
                "space": "O(N)",
                "note": "Uses BST order, but still traverses the entire tree and allocates O(N) memory."
            },
            {
                "name": "Optimal: Iterative Stack In-Order Early Exit",
                "time": "O(H + k)",
                "space": "O(H)",
                "note": "★ Halts immediately at the k-th element using only O(H) stack depth."
            }
        ]
    },
    "trace_table": {
        "input_desc": "BST root = [5, 3, 6, 2, 4, null, null, 1], k = 3",
        "columns": ["Step", "Action", "Stack (Bottom -> Top)", "Popped Node", "Remaining k", "Next curr"],
        "rows": [
            ["1", "Dive left from 5", "[5, 3, 2, 1]", "-", "3", "null"],
            ["2", "Pop 1, decrement k", "[5, 3, 2]", "1 (1st smallest)", "2", "1.right (null)"],
            ["3", "Pop 2, decrement k", "[5, 3]", "2 (2nd smallest)", "1", "2.right (null)"],
            ["4", "Pop 3, decrement k -> k == 0!", "[5]", "3 (3rd smallest)", "0 -> RETURN 3", "Halt immediately!"]
        ],
        "takeaway": "Nodes 4, 5, and 6 were never even popped or explored because k reached 0 at Node 3!"
    },
    "stepper": {
        "title": "Iterative In-Order BST Stack Stepper",
        "default_input": "root=[5,3,6,2,4,null,null,1], k=3",
        "presets": [
            {"label": "Ex 2: k=3 on [5,3,6,2,4,1]", "onclick": "loadPreset(3)"},
            {"label": "k=1 (Fastest Early Exit)", "onclick": "loadPreset(1)"},
            {"label": "k=5 (Cross Root)", "onclick": "loadPreset(5)"}
        ],
        "metrics": [
            {"label": "Current Node", "id": "metric-curr", "initial": "5", "color": "text-cyan-400"},
            {"label": "Stack state", "id": "metric-stack", "initial": "[]", "color": "text-amber-400"},
            {"label": "Remaining k", "id": "metric-k", "initial": "3", "color": "text-emerald-400"},
            {"label": "Popped Value", "id": "metric-ans", "initial": "-", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='bst-viz' class='flex flex-wrap items-center justify-center gap-3'></div>",
        "initial_narration": "Starting at root (5) with k = 3. We will push left spine onto the stack.",
        "pseudocode_lines": [
            "val stack = ArrayDeque<TreeNode>(); var curr = root; var rem = k",
            "while (curr != null || stack.isNotEmpty()) {",
            "  while (curr != null) { stack.addLast(curr); curr = curr.left }",
            "  curr = stack.removeLast()",
            "  if (--rem == 0) return curr.`val`",
            "  curr = curr.right",
            "}"
        ],
        "js_code": """
        let targetK = 3;
        const bstNodes = [1, 2, 3, 4, 5, 6];
        function buildSteps(cand) {
          if (typeof cand === 'number') targetK = cand;
          steps = [];
          let rem = targetK;
          let visited = [];
          const seq = [
            { curr: '5->3->2->1', stack: '[5, 3, 2, 1]', popped: '-', rem, line: 3, narration: 'Pushed entire left spine [5, 3, 2, 1] onto stack.' },
            { curr: '1', stack: '[5, 3, 2]', popped: 1, rem: rem - 1, line: 5, narration: 'Popped 1 (1st smallest). Decremented k.' },
            { curr: '2', stack: '[5, 3]', popped: 2, rem: rem - 2, line: 5, narration: 'Popped 2 (2nd smallest). Decremented k.' },
            { curr: '3', stack: '[5]', popped: 3, rem: rem - 3, line: 5, narration: 'Popped 3 (3rd smallest). Inspecting right child 4.' },
            { curr: '4', stack: '[5]', popped: 4, rem: rem - 4, line: 5, narration: 'Pushed & popped 4 (4th smallest).' },
            { curr: '5', stack: '[]', popped: 5, rem: rem - 5, line: 5, narration: 'Popped root 5 (5th smallest).' }
          ];
          steps.push(seq[0]);
          for (let i = 1; i <= Math.min(targetK, 5); i++) {
            let s = { ...seq[i], visited: bstNodes.slice(0, i) };
            if (i === targetK) {
              s.narration += ` k reached 0! Immediately returning ${s.popped}!`;
            }
            steps.push(s);
          }
        }
        function renderStage(step) {
          const c = document.getElementById('bst-viz');
          if (!c) return;
          const vis = step.visited || [];
          c.innerHTML = bstNodes.map(v => {
            let isTarget = step.popped === v && step.rem === 0;
            let isVis = vis.includes(v);
            let cls = isTarget ? 'border-emerald-400 bg-emerald-950/60 text-emerald-300 scale-110' :
                      (isVis ? 'border-cyan-500/50 bg-cyan-950/30 text-cyan-300' : 'border-slate-800 bg-slate-900 text-slate-400');
            return `<div class="w-12 h-12 rounded-full border-2 flex items-center justify-center font-mono font-bold text-sm transition-all ${cls}">${v}</div>`;
          }).join('');
        }
        function loadPreset(kVal) { targetK = kVal; buildSteps(kVal); stepperReset(); }
        """,
        "js_update_metrics": """
        document.getElementById('metric-curr').textContent = step.curr;
        document.getElementById('metric-stack').textContent = step.stack;
        document.getElementById('metric-k').textContent = step.rem;
        document.getElementById('metric-ans').textContent = step.popped;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Full In-Order List Collection",
            "badge": "Naive",
            "desc": "Run recursive In-Order DFS across the entire tree into a list, then index at k - 1.",
            "time": "O(N)",
            "space": "O(N)",
            "code_kotlin": """class Solution {
    fun kthSmallest(root: TreeNode?, k: Int): Int {
        val sorted = mutableListOf<Int>()
        fun inorder(node: TreeNode?) {
            if (node == null) return
            inorder(node.left)
            sorted.add(node.`val`)
            inorder(node.right)
        }
        inorder(root)
        return sorted[k - 1]
    }
}""",
            "code_python": """class Solution:
    def kthSmallest(self, root: Optional[TreeNode], k: int) -> int:
        vals = []
        def inorder(node):
            if not node:
                return
            inorder(node.left)
            vals.append(node.val)
            inorder(node.right)
        inorder(root)
        return vals[k - 1]""",
            "code_java": """import java.util.*;

class Solution {
    public int kthSmallest(TreeNode root, int k) {
        List<Integer> vals = new ArrayList<>();
        inorder(root, vals);
        return vals.get(k - 1);
    }

    private void inorder(TreeNode node, List<Integer> vals) {
        if (node == null) return;
        inorder(node.left, vals);
        vals.add(node.val);
        inorder(node.right, vals);
    }
}""",
            "notes_kotlin": "Simple to write, but wastes O(N) heap space and traverses the right subtree even when k = 1.",
            "notes_python": "Allocates an O(N) Python list of boxed integers.",
            "notes_java": "Boxes every int into java.lang.Integer inside the ArrayList."
        },
        {
            "name": "Approach 2: Recursive In-Order with Early Pruning",
            "badge": "Better",
            "desc": "Maintain a mutable counter and answer field during recursion, returning early once count == 0.",
            "time": "O(H + k)",
            "space": "O(H) call stack",
            "code_kotlin": """class Solution {
    fun kthSmallest(root: TreeNode?, k: Int): Int {
        var remaining = k
        var answer = -1
        fun dfs(node: TreeNode?) {
            if (node == null || remaining == 0) return
            dfs(node.left)
            if (--remaining == 0) {
                answer = node.`val`
                return
            }
            dfs(node.right)
        }
        dfs(root)
        return answer
    }
}""",
            "code_python": """class Solution:
    def kthSmallest(self, root: Optional[TreeNode], k: int) -> int:
        self.rem = k
        self.ans = -1
        def dfs(node):
            if not node or self.rem <= 0:
                return
            dfs(node.left)
            self.rem -= 1
            if self.rem == 0:
                self.ans = node.val
                return
            dfs(node.right)
        dfs(root)
        return self.ans""",
            "code_java": """class Solution {
    private int remaining;
    private int answer;

    public int kthSmallest(TreeNode root, int k) {
        this.remaining = k;
        dfs(root);
        return this.answer;
    }

    private void dfs(TreeNode node) {
        if (node == null || remaining <= 0) return;
        dfs(node.left);
        if (--remaining == 0) {
            answer = node.val;
            return;
        }
        dfs(node.right);
    }
}""",
            "notes_kotlin": "Local function captures 'remaining' and 'answer' in a Ref$IntRef closure.",
            "notes_python": "Uses instance attributes to avoid nonlocal binding boilerplate.",
            "notes_java": "Clean recursion, though unwinding the call stack still touches parent frames on the way out."
        },
        {
            "name": "Approach 3: Iterative Stack In-Order (Zero Unwinding)",
            "badge": "Optimal",
            "desc": "Simulate the In-Order call stack explicitly with ArrayDeque and return immediately when --k == 0.",
            "time": "O(H + k)",
            "space": "O(H) heap stack",
            "code_kotlin": """class Solution {
    fun kthSmallest(root: TreeNode?, k: Int): Int {
        val stack = ArrayDeque<TreeNode>()
        var curr = root
        var rem = k

        while (curr != null || stack.isNotEmpty()) {
            while (curr != null) {
                stack.addLast(curr)
                curr = curr.left
            }
            curr = stack.removeLast()
            if (--rem == 0) {
                return curr.`val`
            }
            curr = curr.right
        }
        return -1
    }
}""",
            "code_python": """class Solution:
    def kthSmallest(self, root: Optional[TreeNode], k: int) -> int:
        stack = []
        curr = root
        while curr or stack:
            while curr:
                stack.append(curr)
                curr = curr.left
            curr = stack.pop()
            k -= 1
            if k == 0:
                return curr.val
            curr = curr.right
        return -1""",
            "code_java": """import java.util.*;

class Solution {
    public int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();
            if (--k == 0) {
                return curr.val;
            }
            curr = curr.right;
        }
        return -1;
    }
}""",
            "notes_kotlin": "ArrayDeque is faster than java.util.Stack (no synchronized lock overhead) and avoids recursion depth limits.",
            "notes_python": "Python list .append() and .pop() run in amortized O(1) time.",
            "notes_java": "Deque<TreeNode> via ArrayDeque is the standard JVM iterative tree traversal idiom."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun kthSmallest(nums: IntArray, k: Int): Int {
        // Note: In-browser runner passes sorted/level array values for quick verification:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def kthSmallest(self, nums: list[int], k: int) -> int:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public int kthSmallest(int[] nums, int k) {
        // Write your solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { root: [3,1,4,2], k: 1 }, expected: 1 },
          { input: { root: [5,3,6,2,4,1], k: 3 }, expected: 3 }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Before starting, is k guaranteed to be between 1 and the total number of nodes N, and can the tree be unbalanced or skewed?",
        "phase2": "Since a BST's In-Order traversal visits nodes in ascending sorted order, collecting all nodes into a list takes O(N) time and O(N) space. Instead, an iterative In-Order traversal using an explicit stack lets us stop at the exact k-th popped node in O(H + k) time and O(H) space.",
        "phase3": "I'll initialize an ArrayDeque stack and set curr = root. In our outer loop, an inner while loop pushes curr and advances curr = curr.left until null. Then we pop a node, decrement k, and if k == 0 we immediately return curr.val. Otherwise we advance curr = curr.right.",
        "phase4": "Tracing root = [3, 1, 4, null, 2] with k = 2: we push 3 then 1. Pop 1 (k becomes 1), move to 1.right which is 2. Push 2, pop 2 (k becomes 0), and return 2 immediately without ever visiting 3's right child 4.",
        "senior_followup": "If the BST is mutated frequently (insert/delete) and kthSmallest is queried millions of times, we augment each TreeNode with a 'leftSubtreeCount' integer—turning kthSmallest into an O(H) binary search (Order-Statistic Tree)!"
    }
}

# =========================================================================
# PROBLEM 106: LC #105 CONSTRUCT BINARY TREE FROM PREORDER AND INORDER
# =========================================================================
p106 = {
    "num": 106,
    "lc": 105,
    "title": "Construct Binary Tree from Preorder and Inorder Traversal",
    "category": "Binary Trees & BST",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "construct-binary-tree-from-preorder-and-inorder-traversal",
    "verbatim_statement": "Given two integer arrays <code>preorder</code> and <code>inorder</code> where <code>preorder</code> is the preorder traversal of a binary tree and <code>inorder</code> is the inorder traversal of the same tree, construct and return <em>the binary tree</em>.",
    "examples": [
        {
            "title": "Example 1",
            "input": "preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]",
            "output": "[3,9,20,null,null,15,7]"
        },
        {
            "title": "Example 2",
            "input": "preorder = [-1], inorder = [-1]",
            "output": "[-1]"
        }
    ],
    "constraints": [
        "1 <= preorder.length <= 3000",
        "inorder.length == preorder.length",
        "-3000 <= preorder[i], inorder[i] <= 3000",
        "preorder and inorder consist of unique values."
    ],
    "plain_english": "Rebuild the exact Binary Tree given two flat lists: Preorder (which always lists Root first, then Left subtree, then Right subtree) and Inorder (which lists Left subtree, then Root in the middle, then Right subtree).",
    "boundary_checks": {
        "min_base": "preorder = [-1], inorder = [-1] -> Single leaf node with left = null, right = null.",
        "best_exit": "Balanced 3-node tree [3, 9, 20] -> Root 3 splits inorder [9, 3, 20] cleanly at index 1.",
        "worst_scan": "Left-skewed tree of 3,000 nodes -> O(N^2) if linear indexOf is used; O(N) with HashMap lookup."
    },
    "mental_model": {
        "analogy_title": "The CEO Org-Chart Splitter",
        "analogy_text": "Preorder is a roll-call sheet where the boss always speaks first, followed by the entire West Coast division, then the East Coast division. Inorder is a seating chart where the West Coast sits on the left, the boss sits in the middle seat, and the East Coast sits on the right. You read the next boss from the Preorder roll-call, find their seat in the Inorder chart in O(1) using a directory map, and everything to their left becomes the left sub-tree while everything to their right becomes the right sub-tree!",
        "aha_moment": "Preorder tells you WHO the current root is (via a monotonically advancing preIdx++); Inorder tells you HOW MANY nodes belong to the left vs. right subtree (via a precomputed HashMap<Value, Index>)!",
        "trigger": "Reconstructing a tree from two traversal arrays (Preorder + Inorder, or Postorder + Inorder).",
        "sisters": [
            {"name": "Construct Binary Tree from Inorder and Postorder", "lc": "#106"},
            {"name": "Serialize and Deserialize Binary Tree", "lc": "#297"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N^2) Time, O(N^2) Space",
        "naive_text": "At every recursive step, linearly scan inorder[] to find the root's index and slice new preorder/inorder subarrays.",
        "wasted_work": "On a skewed tree, linear scanning + array slicing does N + (N-1) + ... + 1 = O(N^2) work and allocates O(N^2) subarray copies.",
        "optimal_complexity": "O(N) Time, O(N) Space",
        "optimal_text": "Precompute a HashMap mapping each value in inorder to its index in O(N). Maintain a single global preIdx pointer and pass (inLeft, inRight) index bounds into a recursive helper.",
        "core_invariant": "Each call consumes preorder[preIdx++] as the root and recurses on [inLeft .. mid - 1] (left) then [mid + 1 .. inRight] (right).",
        "table_rows": [
            {
                "name": "Naive: Array Slicing + Linear Scan",
                "time": "O(N^2)",
                "space": "O(N^2)",
                "note": "Allocates new subarrays and scans for root on every node."
            },
            {
                "name": "Better: Index Bounds + Linear Scan",
                "time": "O(N^2)",
                "space": "O(H)",
                "note": "Eliminates array copies, but still takes O(N^2) time on skewed trees."
            },
            {
                "name": "Optimal: HashMap Lookup + Index Bounds",
                "time": "O(N)",
                "space": "O(N)",
                "note": "★ O(1) root split lookup + zero array slicing. Visits each node once."
            }
        ]
    },
    "trace_table": {
        "input_desc": "preorder = [3, 9, 20, 15, 7], inorder = [9, 3, 15, 20, 7]",
        "columns": ["Call", "inLeft..inRight", "preIdx -> rootVal", "inMap[rootVal] (mid)", "Left Call Range", "Right Call Range"],
        "rows": [
            ["1", "0..4", "0 -> 3", "1", "0..0 (val 9)", "2..4 (vals 15,20,7)"],
            ["2 (Left of 3)", "0..0", "1 -> 9", "0", "0..-1 (Empty -> null)", "1..0 (Empty -> null)"],
            ["3 (Right of 3)", "2..4", "2 -> 20", "3", "2..2 (val 15)", "4..4 (val 7)"],
            ["4 (Left of 20)", "2..2", "3 -> 15", "2", "2..1 (null)", "3..2 (null)"],
            ["5 (Right of 20)", "4..4", "4 -> 7", "4", "4..3 (null)", "5..4 (null)"]
        ],
        "takeaway": "Because Preorder visits Root -> Left -> Right, advancing preIdx++ naturally visits every left-subtree root before the first right-subtree root!"
    },
    "stepper": {
        "title": "Preorder Root + Inorder Split Stepper",
        "default_input": "pre=[3,9,20,15,7], in=[9,3,15,20,7]",
        "presets": [
            {"label": "Canonical [3,9,20,15,7]", "onclick": "loadPreset('std')"},
            {"label": "Left-Skewed [1,2,3]", "onclick": "loadPreset('skew')"}
        ],
        "metrics": [
            {"label": "preIdx", "id": "metric-pre", "initial": "0", "color": "text-emerald-400"},
            {"label": "Current Root", "id": "metric-root", "initial": "3", "color": "text-amber-400"},
            {"label": "Inorder Bounds", "id": "metric-bounds", "initial": "[0..4]", "color": "text-cyan-400"},
            {"label": "Split mid", "id": "metric-mid", "initial": "1", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='tree-build-viz' class='flex flex-wrap items-center justify-center gap-2'></div>",
        "initial_narration": "HashMap built: {9:0, 3:1, 15:2, 20:3, 7:4}. Ready to split inorder array.",
        "pseudocode_lines": [
            "val inMap = inorder.withIndex().associate { (i, v) -> v to i }",
            "var preIdx = 0",
            "fun build(inLeft: Int, inRight: Int): TreeNode? {",
            "  if (inLeft > inRight) return null",
            "  val rootVal = preorder[preIdx++]; val mid = inMap[rootVal]!!",
            "  val root = TreeNode(rootVal)",
            "  root.left = build(inLeft, mid - 1); root.right = build(mid + 1, inRight)",
            "  return root",
            "}"
        ],
        "js_code": """
        const inArr = [9, 3, 15, 20, 7];
        function buildSteps() {
          steps = [
            { pre: 0, root: 3, bounds: '[0..4]', mid: 1, lRange: [0,0], rRange: [2,4], line: 5, narration: 'preIdx=0 -> Root is 3 (at inorder idx 1). Left gets [0..0], Right gets [2..4].' },
            { pre: 1, root: 9, bounds: '[0..0]', mid: 0, lRange: [-1,-1], rRange: [-1,-1], line: 6, narration: 'preIdx=1 -> Left child of 3 is 9 (at idx 0). Sub-ranges empty -> leaf node.' },
            { pre: 2, root: 20, bounds: '[2..4]', mid: 3, lRange: [2,2], rRange: [4,4], line: 6, narration: 'preIdx=2 -> Right child of 3 is 20 (at idx 3). Left gets [2..2], Right gets [4..4].' },
            { pre: 3, root: 15, bounds: '[2..2]', mid: 2, lRange: [-1,-1], rRange: [-1,-1], line: 6, narration: 'preIdx=3 -> Left child of 20 is 15 (at idx 2). Leaf node.' },
            { pre: 4, root: 7, bounds: '[4..4]', mid: 4, lRange: [-1,-1], rRange: [-1,-1], line: 7, narration: 'preIdx=4 -> Right child of 20 is 7 (at idx 4). Tree construction complete!' }
          ];
        }
        function renderStage(step) {
          const c = document.getElementById('tree-build-viz');
          if (!c) return;
          c.innerHTML = inArr.map((v, idx) => {
            let cls = 'border-slate-800 bg-slate-900 text-slate-400';
            if (idx === step.mid) cls = 'border-amber-400 bg-amber-950/60 text-amber-300 scale-105';
            else if (idx >= step.lRange[0] && idx <= step.lRange[1]) cls = 'border-emerald-400 bg-emerald-950/40 text-emerald-300';
            else if (idx >= step.rRange[0] && idx <= step.rRange[1]) cls = 'border-cyan-400 bg-cyan-950/40 text-cyan-300';
            return `<div class="w-12 h-14 rounded-xl border-2 flex flex-col items-center justify-center font-mono text-xs ${cls}">
              <span class="text-[9px] text-slate-500">in[${idx}]</span>
              <span class="font-bold">${v}</span>
            </div>`;
          }).join('');
        }
        function loadPreset() { buildSteps(); stepperReset(); }
        """,
        "js_update_metrics": """
        document.getElementById('metric-pre').textContent = step.pre;
        document.getElementById('metric-root').textContent = step.root;
        document.getElementById('metric-bounds').textContent = step.bounds;
        document.getElementById('metric-mid').textContent = step.mid;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Recursive Subarray Slicing",
            "badge": "Naive",
            "desc": "Find the root in inorder via indexOf and slice new subarrays for left and right recursive calls.",
            "time": "O(N^2)",
            "space": "O(N^2)",
            "code_kotlin": """class Solution {
    fun buildTree(preorder: IntArray, inorder: IntArray): TreeNode? {
        if (preorder.isEmpty()) return null
        val rootVal = preorder[0]
        val mid = inorder.indexOf(rootVal)
        val root = TreeNode(rootVal)
        root.left = buildTree(
            preorder.sliceArray(1..mid),
            inorder.sliceArray(0 until mid)
        )
        root.right = buildTree(
            preorder.sliceArray((mid + 1) until preorder.size),
            inorder.sliceArray((mid + 1) until inorder.size)
        )
        return root
    }
}""",
            "code_python": """class Solution:
    def buildTree(self, preorder: list[int], inorder: list[int]) -> Optional[TreeNode]:
        if not preorder or not inorder:
            return None
        root = TreeNode(preorder[0])
        mid = inorder.index(preorder[0])
        root.left = self.buildTree(preorder[1 : mid + 1], inorder[:mid])
        root.right = self.buildTree(preorder[mid + 1 :], inorder[mid + 1 :])
        return root""",
            "code_java": """import java.util.*;

class Solution {
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0) return null;
        int rootVal = preorder[0];
        int mid = 0;
        while (inorder[mid] != rootVal) mid++;

        TreeNode root = new TreeNode(rootVal);
        root.left = buildTree(
            Arrays.copyOfRange(preorder, 1, mid + 1),
            Arrays.copyOfRange(inorder, 0, mid)
        );
        root.right = buildTree(
            Arrays.copyOfRange(preorder, mid + 1, preorder.length),
            Arrays.copyOfRange(inorder, mid + 1, inorder.length)
        );
        return root;
    }
}""",
            "notes_kotlin": "Easy to read, but sliceArray copies subarrays at every level and indexOf scans linearly.",
            "notes_python": "Classic Python interview starter, but interviewers will immediately ask you to eliminate the O(N) list slices.",
            "notes_java": "Arrays.copyOfRange allocates 4 new int[] arrays per tree node."
        },
        {
            "name": "Approach 2: HashMap Index Lookup + Pointer Bounds",
            "badge": "Optimal",
            "desc": "Precompute value -> inorder index in a HashMap and recurse using (inLeft, inRight) boundaries with a shared preIdx.",
            "time": "O(N)",
            "space": "O(N)",
            "code_kotlin": """class Solution {
    fun buildTree(preorder: IntArray, inorder: IntArray): TreeNode? {
        val inMap = HashMap<Int, Int>(inorder.size * 2)
        for (i in inorder.indices) {
            inMap[inorder[i]] = i
        }
        var preIdx = 0

        fun helper(inLeft: Int, inRight: Int): TreeNode? {
            if (inLeft > inRight) return null
            val rootVal = preorder[preIdx++]
            val root = TreeNode(rootVal)
            val mid = inMap[rootVal]!!

            root.left = helper(inLeft, mid - 1)
            root.right = helper(mid + 1, inRight)
            return root
        }

        return helper(0, inorder.lastIndex)
    }
}""",
            "code_python": """class Solution:
    def buildTree(self, preorder: list[int], inorder: list[int]) -> Optional[TreeNode]:
        in_map = {val: idx for idx, val in enumerate(inorder)}
        self.pre_idx = 0

        def helper(in_left: int, in_right: int) -> Optional[TreeNode]:
            if in_left > in_right:
                return None
            root_val = preorder[self.pre_idx]
            self.pre_idx += 1
            root = TreeNode(root_val)
            mid = in_map[root_val]

            root.left = helper(in_left, mid - 1)
            root.right = helper(mid + 1, in_right)
            return root

        return helper(0, len(inorder) - 1)""",
            "code_java": """import java.util.*;

class Solution {
    private int preIdx = 0;
    private Map<Integer, Integer> inMap;

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        inMap = new HashMap<>(inorder.length * 2);
        for (int i = 0; i < inorder.length; i++) {
            inMap.put(inorder[i], i);
        }
        preIdx = 0;
        return helper(preorder, 0, inorder.length - 1);
    }

    private TreeNode helper(int[] preorder, int inLeft, int inRight) {
        if (inLeft > inRight) return null;
        int rootVal = preorder[preIdx++];
        TreeNode root = new TreeNode(rootVal);
        int mid = inMap.get(rootVal);

        root.left = helper(preorder, inLeft, mid - 1);
        root.right = helper(preorder, mid + 1, inRight);
        return root;
    }
}""",
            "notes_kotlin": "Pre-sizing HashMap(inorder.size * 2) avoids internal rehashing while indexing all N elements.",
            "notes_python": "Dictionary comprehension builds the lookup table in C-speed O(N); zero list slicing inside helper.",
            "notes_java": "Each node is instantiated once with O(1) HashMap lookup; total time is strictly O(N)."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun buildTree(preorder: IntArray, inorder: IntArray): IntArray {
        // Write your solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def buildTree(self, preorder: list[int], inorder: list[int]) -> list[int]:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public int[] buildTree(int[] preorder, int[] inorder) {
        // Write your solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { preorder: [3,9,20,15,7], inorder: [9,3,15,20,7] }, expected: [3,9,20,15,7] },
          { input: { preorder: [-1], inorder: [-1] }, expected: [-1] }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Before coding, I want to confirm: are all node values in the tree strictly unique? Because if duplicates existed, an inorder value wouldn't uniquely identify the split index.",
        "phase2": "Slicing arrays and searching for the root linearly at each step costs O(N^2) time and space. By precomputing a HashMap from each value to its inorder index, we can find the split point in O(1) time and pass index bounds (inLeft, inRight) down our recursion in O(N) total time.",
        "phase3": "I'll build inMap mapping inorder[i] -> i, and keep a pointer preIdx = 0. In helper(inLeft, inRight), if inLeft > inRight we return null. Otherwise we consume rootVal = preorder[preIdx++], look up mid = inMap[rootVal], build root.left on (inLeft, mid - 1), and root.right on (mid + 1, inRight).",
        "phase4": "Let's verify the recursion order: root.left MUST be built before root.right because Preorder visits the entire left subtree before any node in the right subtree.",
        "senior_followup": "If we were given Postorder and Inorder instead (LC #106), we would start postIdx at n - 1, decrement it, and recurse on root.right BEFORE root.left because Postorder is Left -> Right -> Root."
    }
}

# =========================================================================
# PROBLEM 107: LC #621 TASK SCHEDULER (Pass 2B Stretch Medium)
# =========================================================================
p107 = {
    "num": 107,
    "lc": 621,
    "title": "Task Scheduler",
    "category": "Heaps & Priority Queues",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "task-scheduler",
    "verbatim_statement": "You are given an array of CPU <code>tasks</code>, each represented by letters A to Z, and a cooling time, <code>n</code>. Each cycle or interval allows the completion of one task. Tasks can be completed in any order, but there's a constraint: <strong>identical tasks must be separated by at least <code>n</code> intervals</strong> due to cooling time.<br><br>Return the <em>minimum number of intervals</em> the CPU will take to complete all the given tasks.",
    "examples": [
        {
            "title": "Example 1",
            "input": "tasks = [\"A\",\"A\",\"A\",\"B\",\"B\",\"B\"], n = 2",
            "output": "8"
        },
        {
            "title": "Example 2",
            "input": "tasks = [\"A\",\"C\",\"A\",\"B\",\"D\",\"B\"], n = 1",
            "output": "6"
        },
        {
            "title": "Example 3",
            "input": "tasks = [\"A\",\"A\",\"A\", \"B\",\"B\",\"B\"], n = 3",
            "output": "10"
        }
    ],
    "constraints": [
        "1 <= tasks.length <= 10^4",
        "tasks[i] is an uppercase English letter.",
        "0 <= n <= 100"
    ],
    "plain_english": "Schedule CPU tasks (like 'A', 'B') so that two identical tasks have at least n clock ticks between them. If no valid task is ready, the CPU sits idle. Find the minimum total clock ticks needed to finish everything.",
    "boundary_checks": {
        "min_base": "n = 0 -> Zero cooldown required; CPU never idles, answer is simply tasks.size.",
        "best_exit": "High task variety (e.g. 6 distinct tasks, n = 2) -> Zero idle slots needed, answer is tasks.size.",
        "worst_scan": "tasks = ['A','A','A','A'], n = 100 -> Bottlenecked by single frequent task: (4 - 1) * 101 + 1 = 304."
    },
    "mental_model": {
        "analogy_title": "The Bottleneck Frame & Cooldown Penalty Box",
        "analogy_text": "Always run the task with the highest remaining count first so it starts cooling down as early as possible! You can picture this two ways: (1) Simulation: a Max-Heap of ready task counts and a FIFO Cooldown Queue holding (remainingCount, readyAtTime) pairs; or (2) Geometry: the most frequent task (say 'A' appearing maxFreq times) creates (maxFreq - 1) frames of width (n + 1), plus a final tail holding all tasks tied for maxFreq!",
        "aha_moment": "The minimum schedule length is max(tasks.size, (maxFreq - 1) * (n + 1) + countOfMaxFreqTasks)!",
        "trigger": "Scheduling items with cooldown/spacing constraints or rearranging characters so identical ones are k apart.",
        "sisters": [
            {"name": "Reorganize String", "lc": "#767"},
            {"name": "Top K Frequent Elements", "lc": "#347"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(T * 26 log 26) Time, O(26) Space",
        "naive_text": "Simulate every single CPU clock tick one by one using a Max-Heap of available counts and a Cooldown Queue of (remaining, unlockTime).",
        "wasted_work": "When n = 100 and tasks = 10,000 'A's, tick-by-tick simulation steps through ~1,000,000 idle ticks unnecessarily.",
        "optimal_complexity": "O(N) Time, O(1) Space (26-element array)",
        "optimal_text": "Count frequencies in IntArray(26), find maxFreq and how many tasks tie for maxFreq (maxCount), and return maxOf(tasks.size, (maxFreq - 1) * (n + 1) + maxCount).",
        "core_invariant": "Idle slots only exist if (maxFreq - 1) * (n + 1) + maxCount exceeds tasks.size; otherwise all idle slots are filled and the CPU runs at 100% utilization (tasks.size).",
        "table_rows": [
            {
                "name": "Approach 1: Max-Heap + Cooldown Queue Simulation",
                "time": "O(TotalTicks)",
                "space": "O(1) (<= 26 items)",
                "note": "Great for explaining actual schedule order in interviews."
            },
            {
                "name": "Optimal: Greedy Frame Math Formula",
                "time": "O(N)",
                "space": "O(1)",
                "note": "★ Computes exact minimum intervals in a single pass over 26 frequencies."
            }
        ]
    },
    "trace_table": {
        "input_desc": "tasks = ['A','A','A','B','B','B'], n = 2",
        "columns": ["Metric / Step", "Value / State", "Formula / Explanation"],
        "rows": [
            ["1. Tally Frequencies", "A: 3, B: 3", "IntArray(26) frequency count"],
            ["2. Find Peak (maxFreq)", "maxFreq = 3", "Highest frequency among all 26 letters"],
            ["3. Count Peak Ties (maxCount)", "maxCount = 2 ('A' and 'B')", "Number of tasks with freq == maxFreq (3)"],
            ["4. Frame Capacity", "(3 - 1) * (2 + 1) + 2 = 2 * 3 + 2 = 8", "Frames: [A B idle] [A B idle] [A B]"],
            ["5. Final Clamp", "maxOf(tasks.size = 6, frameTicks = 8) -> 8", "Return 8 intervals"]
        ],
        "takeaway": "Both the Max-Heap + Queue simulation and the O(N) frame math are valuable to mention: pitch the Heap + Queue first to prove intuition, then derive the O(N) closed-form math!"
    },
    "stepper": {
        "title": "CPU Cooldown Frame & Heap Schedule Visualizer",
        "default_input": "tasks=[A,A,A,B,B,B], n=2",
        "presets": [
            {"label": "Ex 1: [A:3, B:3], n=2 -> 8", "onclick": "loadPreset('ex1')"},
            {"label": "No Idles: [A:2, B:2, C:1, D:1], n=1 -> 6", "onclick": "loadPreset('ex2')"}
        ],
        "metrics": [
            {"label": "Clock Time", "id": "metric-time", "initial": "0", "color": "text-emerald-400"},
            {"label": "Max-Heap", "id": "metric-heap", "initial": "[3, 3]", "color": "text-amber-400"},
            {"label": "Cooldown Queue", "id": "metric-q", "initial": "[]", "color": "text-cyan-400"},
            {"label": "CPU Timeline", "id": "metric-cpu", "initial": "Ready", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='cpu-viz' class='flex flex-wrap items-center justify-center gap-2'></div>",
        "initial_narration": "Frequencies tallied: A=3, B=3. Cooldown n=2 means identical tasks must be 3 ticks apart.",
        "pseudocode_lines": [
            "val freq = IntArray(26); for (t in tasks) freq[t - 'A']++",
            "val maxFreq = freq.maxOrNull() ?: 0",
            "val maxCount = freq.count { it == maxFreq }",
            "val frameSlots = (maxFreq - 1) * (n + 1) + maxCount",
            "return maxOf(tasks.size, frameSlots)"
        ],
        "js_code": """
        let timeline = ['A', 'B', 'idle', 'A', 'B', 'idle', 'A', 'B'];
        function buildSteps() {
          steps = [
            { t: 1, heap: '[3]', q: '[(2, t=3)]', cpu: 'A', upto: 1, line: 1, narration: 't=1: Pop A(3) from Max-Heap. A(2) enters cooldown until t=3.' },
            { t: 2, heap: '[]', q: '[(2, t=3), (2, t=4)]', cpu: 'B', upto: 2, line: 2, narration: 't=2: Pop B(3) from Max-Heap. B(2) enters cooldown until t=4.' },
            { t: 3, heap: '[2]', q: '[(2, t=4)]', cpu: 'idle', upto: 3, line: 3, narration: 't=3: Heap empty -> CPU idles! At end of tick 3, A(2) unlocks back to Heap.' },
            { t: 6, heap: '[1]', q: '[(1, t=7)]', cpu: 'A -> B -> idle', upto: 6, line: 4, narration: 't=4..6: Frame 2 executes [A, B, idle].' },
            { t: 8, heap: '[]', q: '[]', cpu: 'A -> B (Done!)', upto: 8, line: 5, narration: 't=7..8: Final tail executes [A, B]. Total = (3-1)*(2+1) + 2 = 8 ticks!' }
          ];
        }
        function renderStage(step) {
          const c = document.getElementById('cpu-viz');
          if (!c) return;
          c.innerHTML = timeline.slice(0, step.upto).map((slot, idx) => {
            let cls = slot === 'idle' ? 'border-rose-500/50 bg-rose-950/40 text-rose-300' : 'border-emerald-400 bg-emerald-950/50 text-emerald-300';
            return `<div class="px-3 py-2 rounded-xl border-2 font-mono text-xs flex flex-col items-center ${cls}">
              <span class="text-[9px] text-slate-500">t=${idx+1}</span>
              <span class="font-bold">${slot}</span>
            </div>`;
          }).join('');
        }
        function loadPreset() { buildSteps(); stepperReset(); }
        """,
        "js_update_metrics": """
        document.getElementById('metric-time').textContent = step.t;
        document.getElementById('metric-heap').textContent = step.heap;
        document.getElementById('metric-q').textContent = step.q;
        document.getElementById('metric-cpu').textContent = step.cpu;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Max-Heap + Cooldown Queue Simulation",
            "badge": "Simulation",
            "desc": "Always execute the highest-frequency available task via a Max-Heap, placing remaining copies into a FIFO cooldown queue until time + n.",
            "time": "O(TotalTicks)",
            "space": "O(1) (at most 26 tasks)",
            "code_kotlin": """import java.util.PriorityQueue

class Solution {
    fun leastInterval(tasks: CharArray, n: Int): Int {
        val freq = IntArray(26)
        for (t in tasks) freq[t - 'A']++

        val maxHeap = PriorityQueue<Int>(compareByDescending { it })
        for (f in freq) if (f > 0) maxHeap.offer(f)

        // Pair of (remainingCount, readyAtTime)
        val cooldown = ArrayDeque<IntArray>()
        var time = 0

        while (maxHeap.isNotEmpty() || cooldown.isNotEmpty()) {
            time++
            if (maxHeap.isNotEmpty()) {
                val rem = maxHeap.poll() - 1
                if (rem > 0) {
                    cooldown.addLast(intArrayOf(rem, time + n))
                }
            }
            if (cooldown.isNotEmpty() && cooldown.first()[1] == time) {
                maxHeap.offer(cooldown.removeFirst()[0])
            }
        }
        return time
    }
}""",
            "code_python": """import heapq
from collections import Counter, deque

class Solution:
    def leastInterval(self, tasks: list[str], n: int) -> int:
        counts = Counter(tasks)
        max_heap = [-cnt for cnt in counts.values()]
        heapq.heapify(max_heap)

        time = 0
        q = deque()  # pairs of (-cnt, unlock_time)

        while max_heap or q:
            time += 1
            if max_heap:
                cnt = 1 + heapq.heappop(max_heap)
                if cnt != 0:
                    q.append((cnt, time + n))
            if q and q[0][1] == time:
                heapq.heappush(max_heap, q.popleft()[0])
        return time""",
            "code_java": """import java.util.*;

class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char t : tasks) freq[t - 'A']++;

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int f : freq) if (f > 0) maxHeap.offer(f);

        Deque<int[]> cooldown = new ArrayDeque<>();
        int time = 0;

        while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
            time++;
            if (!maxHeap.isEmpty()) {
                int rem = maxHeap.poll() - 1;
                if (rem > 0) cooldown.offerLast(new int[]{rem, time + n});
            }
            if (!cooldown.isEmpty() && cooldown.peekFirst()[1] == time) {
                maxHeap.offer(cooldown.pollFirst()[0]);
            }
        }
        return time;
    }
}""",
            "notes_kotlin": "Since at most 26 letters exist, PriorityQueue operations take O(log 26) = O(1) time per tick.",
            "notes_python": "Negating counts turns Python's min-heap (heapq) into a max-heap.",
            "notes_java": "Great approach when an interviewer asks you to print the actual task execution trace."
        },
        {
            "name": "Approach 2: Greedy Frame Math (O(N) Closed Form)",
            "badge": "Optimal",
            "desc": "Calculate the frame size dictated by the highest-frequency task: maxOf(tasks.size, (maxFreq - 1) * (n + 1) + maxCount).",
            "time": "O(N)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun leastInterval(tasks: CharArray, n: Int): Int {
        val freq = IntArray(26)
        var maxFreq = 0
        for (t in tasks) {
            val f = ++freq[t - 'A']
            if (f > maxFreq) maxFreq = f
        }

        var maxCount = 0
        for (f in freq) {
            if (f == maxFreq) maxCount++
        }

        val frameIntervals = (maxFreq - 1) * (n + 1) + maxCount
        return maxOf(tasks.size, frameIntervals)
    }
}""",
            "code_python": """class Solution:
    def leastInterval(self, tasks: list[str], n: int) -> int:
        freq = [0] * 26
        for t in tasks:
            freq[ord(t) - ord('A')] += 1

        max_freq = max(freq)
        max_count = freq.count(max_freq)

        frame_intervals = (max_freq - 1) * (n + 1) + max_count
        return max(len(tasks), frame_intervals)""",
            "code_java": """class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        int maxFreq = 0;
        for (char t : tasks) {
            maxFreq = Math.max(maxFreq, ++freq[t - 'A']);
        }

        int maxCount = 0;
        for (int f : freq) {
            if (f == maxFreq) maxCount++;
        }

        int frameIntervals = (maxFreq - 1) * (n + 1) + maxCount;
        return Math.max(tasks.length, frameIntervals);
    }
}""",
            "notes_kotlin": "Single 26-int primitive array on stack/heap; zero dynamic allocations.",
            "notes_python": "O(N) frequency count + O(26) max and count scan.",
            "notes_java": "Runs in 1ms on LeetCode/NeetCode with O(1) space."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun leastInterval(tasks: List<String>, n: Int): Int {
        // Write your solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def leastInterval(self, tasks: list[str], n: int) -> int:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public int leastInterval(String[] tasks, int n) {
        // Write your solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { tasks: ["A","A","A","B","B","B"], n: 2 }, expected: 8 },
          { input: { tasks: ["A","C","A","B","D","B"], n: 1 }, expected: 6 },
          { input: { tasks: ["A","A","A","B","B","B"], n: 3 }, expected: 10 }
        ];
        """
    },
    "spoken_script": {
        "phase1": "I'll clarify two constraints: are tasks strictly uppercase English letters A-Z, and do we only need to return the minimum interval count rather than the schedule array itself?",
        "phase2": "We can simulate the schedule using a Max-Heap of frequencies and a FIFO cooldown queue of (remainingCount, unlockTime), or compute the exact answer in O(N) time and O(1) space using greedy bucket math: the most frequent task (maxFreq) requires (maxFreq - 1) chunks of length (n + 1), plus a final tail of size maxCount (the number of tasks tied at maxFreq).",
        "phase3": "I'll count frequencies in an IntArray(26), track maxFreq, then count how many letters equal maxFreq as maxCount. Finally, I return maxOf(tasks.size, (maxFreq - 1) * (n + 1) + maxCount).",
        "phase4": "Testing tasks = [A,A,A,B,B,B], n = 2: maxFreq = 3, maxCount = 2 ('A' and 'B'). Formula gives (3 - 1) * 3 + 2 = 8. Max(6, 8) = 8. Matches [A, B, idle, A, B, idle, A, B].",
        "senior_followup": "If tasks arrive dynamically in an online stream rather than upfront in a batch, the Max-Heap + Cooldown Queue simulation is the exact production scheduler to use because it adapts on every clock tick."
    }
}

# =========================================================================
# PROBLEM 108: LC #55 JUMP GAME (Pass 2A Core Medium)
# =========================================================================
p108 = {
    "num": 108,
    "lc": 55,
    "title": "Jump Game",
    "category": "Intervals & Greedy",
    "difficulty": "Medium",
    "pass_label": "Pass 2A",
    "lc_slug": "jump-game",
    "verbatim_statement": "You are given an integer array <code>nums</code>. You are initially positioned at the array's <strong>first index</strong>, and each element in the array represents your maximum jump length at that position.<br><br>Return <code>true</code> <em>if you can reach the last index, or</em> <code>false</code> <em>otherwise</em>.",
    "examples": [
        {
            "title": "Example 1",
            "input": "nums = [2,3,1,1,4]",
            "output": "true"
        },
        {
            "title": "Example 2",
            "input": "nums = [3,2,1,0,4]",
            "output": "false"
        }
    ],
    "constraints": [
        "1 <= nums.length <= 10^4",
        "0 <= nums[i] <= 10^5"
    ],
    "plain_english": "Starting at index 0, where nums[i] tells you the furthest number of steps you can jump forward from i, determine if it is possible to reach the final index n - 1 without getting trapped on a 0.",
    "boundary_checks": {
        "min_base": "nums = [0] -> Already at the last index (0 == n - 1), returns true immediately.",
        "best_exit": "nums = [10, 0, 0, 0] -> First jump reaches beyond n - 1 immediately.",
        "worst_scan": "nums = [3, 2, 1, 0, 4] -> All jumps funnel into index 3 (val 0), leaving index 4 unreachable."
    },
    "mental_model": {
        "analogy_title": "The Expanding Gas Tank Horizon",
        "analogy_text": "Instead of guessing which specific jump length to pick at each stone, imagine an expanding 'reachable horizon' (maxReach). As you walk index by index from left to right, first check: 'Is my current index i beyond maxReach?' If i > maxReach, you hit a chasm you could never cross! Otherwise, standing on stone i lets you push the horizon out to max(maxReach, i + nums[i]).",
        "aha_moment": "Because jumping UP TO nums[i] steps means every intermediate index between i and i + nums[i] is reachable, the reachable set is always a single contiguous prefix [0 .. maxReach]!",
        "trigger": "Determining reachability across sequential steps where each position grants a maximum forward range.",
        "sisters": [
            {"name": "Jump Game II", "lc": "#45"},
            {"name": "Gas Station", "lc": "#134"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(2^N) Backtracking / O(N^2) DP Time, O(N) Space",
        "naive_text": "Try every possible jump from 1..nums[i] recursively (or with a boolean dp[] table marking reachable indices).",
        "wasted_work": "Repeatedly marks the same overlapping index ranges as reachable in O(N^2) nested loops.",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Track a single integer maxReach = 0. Iterate i from 0 to n - 1: if i > maxReach return false; update maxReach = maxOf(maxReach, i + nums[i]); if maxReach >= n - 1 return true.",
        "core_invariant": "At step i, every index in [0 .. maxReach] is guaranteed reachable from index 0.",
        "table_rows": [
            {
                "name": "Naive: Recursive DFS Backtracking",
                "time": "O(2^N)",
                "space": "O(N)",
                "note": "Exponential branching across jump choices; TLE immediately."
            },
            {
                "name": "Better: 1D Dynamic Programming",
                "time": "O(N^2)",
                "space": "O(N)",
                "note": "Marks dp[i + j] = true; still quadratic when jump lengths are large."
            },
            {
                "name": "Optimal: Greedy Farthest Horizon (maxReach)",
                "time": "O(N)",
                "space": "O(1)",
                "note": "★ Single pass, 1 integer variable, early exit as soon as maxReach >= n - 1."
            }
        ]
    },
    "trace_table": {
        "input_desc": "nums = [3, 2, 1, 0, 4] (Trap at index 3)",
        "columns": ["Index i", "nums[i]", "Check i > maxReach?", "i + nums[i]", "Updated maxReach", "Status"],
        "rows": [
            ["0", "3", "0 > 0 (False)", "0 + 3 = 3", "max(0, 3) = 3", "Horizon reaches idx 3"],
            ["1", "2", "1 > 3 (False)", "1 + 2 = 3", "max(3, 3) = 3", "Horizon stays at idx 3"],
            ["2", "1", "2 > 3 (False)", "2 + 1 = 3", "max(3, 3) = 3", "Horizon stays at idx 3"],
            ["3", "0", "3 > 3 (False)", "3 + 0 = 3", "max(3, 3) = 3", "Zero jump! Horizon stuck at 3"],
            ["4", "4", "4 > 3 (TRUE!)", "-", "3", "Index 4 > maxReach (3) -> RETURN FALSE"]
        ],
        "takeaway": "No matter which path you take from 0, 1, or 2, the maximum reachable index never exceeds 3."
    },
    "stepper": {
        "title": "Greedy Reachability Horizon Stepper",
        "default_input": "2, 3, 1, 1, 4",
        "presets": [
            {"label": "Reachable [2,3,1,1,4]", "onclick": "loadPreset([2,3,1,1,4])"},
            {"label": "Stuck at Zero [3,2,1,0,4]", "onclick": "loadPreset([3,2,1,0,4])"},
            {"label": "Single [0]", "onclick": "loadPreset([0])"}
        ],
        "metrics": [
            {"label": "Current Index (i)", "id": "metric-i", "initial": "0", "color": "text-cyan-400"},
            {"label": "Jump Power nums[i]", "id": "metric-jump", "initial": "2", "color": "text-amber-400"},
            {"label": "Horizon (maxReach)", "id": "metric-reach", "initial": "0", "color": "text-emerald-400"},
            {"label": "Verdict", "id": "metric-res", "initial": "Scanning", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='jump-viz' class='flex flex-wrap items-center justify-center gap-2'></div>",
        "initial_narration": "Starting at index 0 with maxReach = 0.",
        "pseudocode_lines": [
            "var maxReach = 0",
            "for (i in nums.indices) {",
            "  if (i > maxReach) return false",
            "  maxReach = maxOf(maxReach, i + nums[i])",
            "  if (maxReach >= nums.lastIndex) return true",
            "}",
            "return true"
        ],
        "js_code": """
        let jumpArr = [2, 3, 1, 1, 4];
        function buildSteps(cand) {
          if (Array.isArray(cand)) jumpArr = cand;
          steps = [];
          let maxReach = 0;
          for (let i = 0; i < jumpArr.length; i++) {
            if (i > maxReach) {
              steps.push({ i, jump: jumpArr[i], reach: maxReach, res: 'FALSE (Unreachable)', line: 3, narration: `Index ${i} > maxReach (${maxReach}). Cannot cross gap -> return false!` });
              return;
            }
            maxReach = Math.max(maxReach, i + jumpArr[i]);
            let done = maxReach >= jumpArr.length - 1;
            steps.push({
              i, jump: jumpArr[i], reach: maxReach,
              res: done ? 'TRUE (Goal Reached!)' : 'Reachable',
              line: done ? 5 : 4,
              narration: `At i=${i} (jump=${jumpArr[i]}): maxReach updated to ${maxReach}.${done ? ' Reached last index -> return true!' : ''}`
            });
            if (done) return;
          }
        }
        function renderStage(step) {
          const c = document.getElementById('jump-viz');
          if (!c) return;
          c.innerHTML = jumpArr.map((v, idx) => {
            let cls = 'border-slate-800 bg-slate-900 text-slate-500';
            if (idx === step.i) cls = 'border-amber-400 bg-amber-950/60 text-amber-300 scale-105';
            else if (idx <= step.reach) cls = 'border-emerald-500/60 bg-emerald-950/30 text-emerald-300';
            return `<div class="w-12 h-14 rounded-xl border-2 flex flex-col items-center justify-center font-mono text-xs ${cls}">
              <span class="text-[9px] text-slate-500">idx ${idx}</span>
              <span class="font-bold">+${v}</span>
            </div>`;
          }).join('');
        }
        function loadPreset(arr) { jumpArr = arr; buildSteps(arr); stepperReset(); }
        """,
        "js_update_metrics": """
        document.getElementById('metric-i').textContent = step.i;
        document.getElementById('metric-jump').textContent = step.jump;
        document.getElementById('metric-reach').textContent = step.reach;
        document.getElementById('metric-res').textContent = step.res;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Dynamic Programming (Bottom-Up Reachability)",
            "badge": "Naive DP",
            "desc": "Allocate a BooleanArray dp where dp[i] is true if index i can reach the end, checking all jump steps 1..nums[i].",
            "time": "O(N^2)",
            "space": "O(N)",
            "code_kotlin": """class Solution {
    fun canJump(nums: IntArray): Boolean {
        val n = nums.size
        val dp = BooleanArray(n)
        dp[n - 1] = true

        for (i in n - 2 downTo 0) {
            val furthest = minOf(n - 1, i + nums[i])
            for (next in i + 1..furthest) {
                if (dp[next]) {
                    dp[i] = true
                    break
                }
            }
        }
        return dp[0]
    }
}""",
            "code_python": """class Solution:
    def canJump(self, nums: list[int]) -> bool:
        n = len(nums)
        dp = [False] * n
        dp[n - 1] = True
        for i in range(n - 2, -1, -1):
            furthest = min(n - 1, i + nums[i])
            for j in range(i + 1, furthest + 1):
                if dp[j]:
                    dp[i] = True
                    break
        return dp[0]""",
            "code_java": """class Solution {
    public boolean canJump(int[] nums) {
        int n = nums.length;
        boolean[] dp = new boolean[n];
        dp[n - 1] = true;
        for (int i = n - 2; i >= 0; i--) {
            int furthest = Math.min(n - 1, i + nums[i]);
            for (int j = i + 1; j <= furthest; j++) {
                if (dp[j]) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[0];
    }
}""",
            "notes_kotlin": "Inner loop scans up to nums[i] steps even though we only care about the leftmost 'true' goal post.",
            "notes_python": "O(N^2) worst case when elements near the end cannot reach n - 1.",
            "notes_java": "Allocates an unnecessary O(N) boolean array."
        },
        {
            "name": "Approach 2: Greedy Farthest Reachable Horizon",
            "badge": "Optimal",
            "desc": "Maintain maxReach in a single forward pass. If i > maxReach, return false; otherwise extend maxReach = max(maxReach, i + nums[i]).",
            "time": "O(N)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun canJump(nums: IntArray): Boolean {
        var maxReach = 0
        for (i in nums.indices) {
            if (i > maxReach) return false
            maxReach = maxOf(maxReach, i + nums[i])
            if (maxReach >= nums.lastIndex) return true
        }
        return true
    }
}""",
            "code_python": """class Solution:
    def canJump(self, nums: list[int]) -> bool:
        max_reach = 0
        last_idx = len(nums) - 1
        for i, jump in enumerate(nums):
            if i > max_reach:
                return False
            if i + jump > max_reach:
                max_reach = i + jump
            if max_reach >= last_idx:
                return True
        return True""",
            "code_java": """class Solution {
    public boolean canJump(int[] nums) {
        int maxReach = 0;
        int lastIdx = nums.length - 1;
        for (int i = 0; i <= lastIdx; i++) {
            if (i > maxReach) return false;
            maxReach = Math.max(maxReach, i + nums[i]);
            if (maxReach >= lastIdx) return true;
        }
        return true;
    }
}""",
            "notes_kotlin": "Early exit when maxReach >= nums.lastIndex skips scanning the remaining tail.",
            "notes_python": "Using an explicit if (i + jump > max_reach) avoids function-call overhead of max() in tight Python loops.",
            "notes_java": "Zero heap allocations; strictly O(1) registers."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun canJump(nums: IntArray): Boolean {
        // Write your solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def canJump(self, nums: list[int]) -> bool:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public boolean canJump(int[] nums) {
        // Write your solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { nums: [2,3,1,1,4] }, expected: true },
          { input: { nums: [3,2,1,0,4] }, expected: false },
          { input: { nums: [0] }, expected: true }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Before coding, I'll confirm: can nums[i] be 0, and if nums has length 1 (e.g. [0]), is the answer true since we start at the last index?",
        "phase2": "Dynamic programming checks all reachable jumps in O(N^2) time. However, since jumping up to nums[i] steps reaches every index in [i .. i + nums[i]], the reachable region is always a single contiguous prefix [0 .. maxReach]. Tracking maxReach greedily solves this in O(N) time and O(1) space.",
        "phase3": "I'll initialize maxReach = 0 and loop i from 0 to nums.lastIndex. First, if i > maxReach, index i is unreachable so we return false. Otherwise we update maxReach = maxOf(maxReach, i + nums[i]), and if maxReach >= nums.lastIndex we exit early with true.",
        "phase4": "Dry-running [3, 2, 1, 0, 4]: i=0 sets maxReach=3. i=1, 2, 3 all yield i + nums[i] = 3. At i=4, 4 > maxReach (3) triggers and returns false.",
        "senior_followup": "If the follow-up asks for the MINIMUM number of jumps to reach the end (Jump Game II, LC #45), we track two boundaries: currentJumpEnd and farthestReach, incrementing jumps++ each time i reaches currentJumpEnd."
    }
}

# =========================================================================
# PROBLEM 109: LC #416 PARTITION EQUAL SUBSET SUM (Pass 2B Stretch Medium)
# =========================================================================
p109 = {
    "num": 109,
    "lc": 416,
    "title": "Partition Equal Subset Sum",
    "category": "Dynamic Programming",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "partition-equal-subset-sum",
    "verbatim_statement": "Given an integer array <code>nums</code>, return <code>true</code> <em>if you can partition the array into two subsets such that the sum of the elements in both subsets is equal or</em> <code>false</code> <em>otherwise</em>.",
    "examples": [
        {
            "title": "Example 1",
            "input": "nums = [1,5,11,5]",
            "output": "true"
        },
        {
            "title": "Example 2",
            "input": "nums = [1,2,3,5]",
            "output": "false"
        }
    ],
    "constraints": [
        "1 <= nums.length <= 200",
        "1 <= nums[i] <= 100"
    ],
    "plain_english": "Split the array into two buckets with the exact same total sum. This is mathematically identical to asking: if totalSum is even, can we pick a subset of numbers that adds up to exactly target = totalSum / 2?",
    "boundary_checks": {
        "min_base": "totalSum is odd (e.g. [1, 2, 5], sum = 8? No, [1, 2, 4] sum = 7) -> Immediately return false in O(1) after summing!",
        "best_exit": "Single element equals target (e.g. [1, 5, 11, 5], target = 11) -> Hits dp[target] = true as soon as 11 is processed.",
        "worst_scan": "200 elements summing to 20,000 -> 1D boolean DP array of size 10,001 updated backwards."
    },
    "mental_model": {
        "analogy_title": "The 0/1 Knapsack Reverse-Ripple Ledger",
        "analogy_text": "First, if the total weight of all gold bars is odd, you can never split integer bars into two equal halves! If the sum is even, you only need to fill one bag to exactly target = totalSum / 2. Maintain a boolean checklist dp[0..target] of achievable sums. For each number 'num', walk BACKWARDS from s = target down to num: if dp[s - num] was already achievable, check off dp[s] = true! Walking backwards guarantees you don't use the same gold bar twice in the same pass.",
        "aha_moment": "In 0/1 Knapsack (each item used at most once), iterating the target sum s BACKWARDS from target down to num prevents an item from building on itself in the same row!",
        "trigger": "Splitting items into two equal halves, minimizing subset difference, or finding a subset that sums to K without reusing elements.",
        "sisters": [
            {"name": "Coin Change (Unbounded Forward Loop)", "lc": "#322"},
            {"name": "Target Sum", "lc": "#494"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(2^N) Time, O(N) Space",
        "naive_text": "Branch recursively on every element: include nums[i] in subset A or exclude it.",
        "wasted_work": "Recomputes identical (index, remainingSum) subproblems exponentially.",
        "optimal_complexity": "O(N * Target) Time, O(Target) Space",
        "optimal_text": "Check totalSum % 2 == 0. Allocate BooleanArray(target + 1) with dp[0] = true. For each num in nums, loop s from target downTo num: dp[s] = dp[s] || dp[s - num].",
        "core_invariant": "After processing the first i elements, dp[s] is true iff some subset of nums[0..i] sums to s.",
        "table_rows": [
            {
                "name": "Naive: Recursive Include/Exclude Tree",
                "time": "O(2^N)",
                "space": "O(N)",
                "note": "2^200 branches; TLE on small inputs."
            },
            {
                "name": "Better: 2D Memoization / DP Table",
                "time": "O(N * Target)",
                "space": "O(N * Target)",
                "note": "Allocates 200 x 10,001 table (~2M cells) when only the previous row is needed."
            },
            {
                "name": "Optimal: Space-Optimized 1D Reverse DP",
                "time": "O(N * Target)",
                "space": "O(Target)",
                "note": "★ Single BooleanArray(target + 1) iterated backwards with early exit on dp[target]."
            }
        ]
    },
    "trace_table": {
        "input_desc": "nums = [1, 5, 11, 5] -> totalSum = 22, target = 11",
        "columns": ["Step / Item", "Reverse Loop Range", "Achievable Sums (where dp[s] == true)", "dp[11] Status"],
        "rows": [
            ["Init", "-", "{ 0 }", "false"],
            ["num = 1", "s = 11 downTo 1", "{ 0, 1 }", "false"],
            ["num = 5", "s = 11 downTo 5", "{ 0, 1, 5, 6 }", "false"],
            ["num = 11", "s = 11 downTo 11", "{ 0, 1, 5, 6, 11 } (since dp[11 - 11] = dp[0] is true!)", "TRUE -> Early Exit!"]
        ],
        "takeaway": "If we had iterated forward (1 to 11) on num = 5, dp[5] would become true and then immediately make dp[10] true using the SAME 5 twice!"
    },
    "stepper": {
        "title": "0/1 Knapsack Reverse-Iteration DP Stepper",
        "default_input": "1, 5, 11, 5",
        "presets": [
            {"label": "Ex 1: [1, 5, 11, 5] (Target 11)", "onclick": "loadPreset([1,5,11,5])"},
            {"label": "Ex 2: [1, 2, 3, 5] (False)", "onclick": "loadPreset([1,2,3,5])"}
        ],
        "metrics": [
            {"label": "Total Sum / Target", "id": "metric-target", "initial": "22 / 11", "color": "text-amber-400"},
            {"label": "Current Item", "id": "metric-item", "initial": "-", "color": "text-cyan-400"},
            {"label": "Reachable Sums", "id": "metric-sums", "initial": "{0}", "color": "text-emerald-400"},
            {"label": "dp[target]", "id": "metric-done", "initial": "false", "color": "text-purple-400"}
        ],
        "visual_html": "<div id='knapsack-viz' class='flex flex-wrap items-center justify-center gap-1.5'></div>",
        "initial_narration": "Base state: only sum 0 is achievable (dp[0] = true).",
        "pseudocode_lines": [
            "val sum = nums.sum(); if (sum % 2 != 0) return false",
            "val target = sum / 2; val dp = BooleanArray(target + 1)",
            "dp[0] = true",
            "for (num in nums) {",
            "  for (s in target downTo num) {",
            "    if (dp[s - num]) dp[s] = true",
            "  }",
            "  if (dp[target]) return true",
            "}",
            "return dp[target]"
        ],
        "js_code": """
        let ksNums = [1, 5, 11, 5];
        function buildSteps(cand) {
          if (Array.isArray(cand)) ksNums = cand;
          steps = [];
          let sum = ksNums.reduce((a,b)=>a+b, 0);
          let target = Math.floor(sum / 2);
          let dp = new Array(target + 1).fill(false);
          dp[0] = true;
          steps.push({ target: `${sum} / ${target}`, item: 'Init', dp: [...dp], done: false, line: 3, narration: `Initialized dp[0..${target}]. dp[0] = true.` });
          if (sum % 2 !== 0) {
            steps.push({ target: `${sum} (Odd!)`, item: '-', dp: [...dp], done: false, line: 1, narration: 'Total sum is odd! Cannot split into two equal integers -> return false.' });
            return;
          }
          for (let num of ksNums) {
            for (let s = target; s >= num; s--) {
              if (dp[s - num]) dp[s] = true;
            }
            steps.push({
              target: `${sum} / ${target}`, item: num, dp: [...dp], done: dp[target],
              line: dp[target] ? 8 : 6,
              narration: `Processed num=${num} backwards (${target} downTo ${num}).${dp[target] ? ' dp[target] is now TRUE -> return true!' : ''}`
            });
            if (dp[target]) return;
          }
        }
        function renderStage(step) {
          const c = document.getElementById('knapsack-viz');
          if (!c) return;
          c.innerHTML = step.dp.map((ok, s) => {
            let cls = ok ? 'border-emerald-400 bg-emerald-950/60 text-emerald-300' : 'border-slate-800 bg-slate-900 text-slate-500';
            return `<div class="w-10 h-12 rounded-lg border flex flex-col items-center justify-center font-mono text-xs ${cls}">
              <span class="text-[9px] text-slate-500">s=${s}</span>
              <span class="font-bold">${ok ? 'T' : 'F'}</span>
            </div>`;
          }).join('');
        }
        function loadPreset(arr) { ksNums = arr; buildSteps(arr); stepperReset(); }
        """,
        "js_update_metrics": """
        document.getElementById('metric-target').textContent = step.target;
        document.getElementById('metric-item').textContent = step.item;
        document.getElementById('metric-sums').textContent = '{' + step.dp.map((v,i)=>v?i:-1).filter(x=>x>=0).join(',') + '}';
        document.getElementById('metric-done').textContent = String(step.done);
        """
    },
    "approaches": [
        {
            "name": "Approach 1: Top-Down DFS + 2D Memoization",
            "badge": "Memoization",
            "desc": "Recurse on (index, remainingTarget) caching results in a 2D Array<Boolean?> to avoid exponential recomputation.",
            "time": "O(N * Target)",
            "space": "O(N * Target)",
            "code_kotlin": """class Solution {
    fun canPartition(nums: IntArray): Boolean {
        val total = nums.sum()
        if (total % 2 != 0) return false
        val target = total / 2
        val memo = Array(nums.size) { arrayOfNulls<Boolean>(target + 1) }

        fun dfs(i: Int, rem: Int): Boolean {
            if (rem == 0) return true
            if (i == nums.size || rem < 0) return false
            memo[i][rem]?.let { return it }

            val res = dfs(i + 1, rem - nums[i]) || dfs(i + 1, rem)
            memo[i][rem] = res
            return res
        }
        return dfs(0, target)
    }
}""",
            "code_python": """from functools import lru_cache

class Solution:
    def canPartition(self, nums: list[int]) -> bool:
        total = sum(nums)
        if total % 2 != 0:
            return False
        target = total // 2

        @lru_cache(None)
        def dfs(i: int, rem: int) -> bool:
            if rem == 0:
                return True
            if i == len(nums) or rem < 0:
                return False
            return dfs(i + 1, rem - nums[i]) or dfs(i + 1, rem)

        return dfs(0, target)""",
            "code_java": """class Solution {
    public boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % 2 != 0) return false;
        int target = total / 2;
        Boolean[][] memo = new Boolean[nums.length][target + 1];
        return dfs(nums, 0, target, memo);
    }

    private boolean dfs(int[] nums, int i, int rem, Boolean[][] memo) {
        if (rem == 0) return true;
        if (i == nums.length || rem < 0) return false;
        if (memo[i][rem] != null) return memo[i][rem];
        return memo[i][rem] = dfs(nums, i + 1, rem - nums[i], memo) || dfs(nums, i + 1, rem, memo);
    }
}""",
            "notes_kotlin": "Boxes Boolean objects in an N x Target 2D reference array.",
            "notes_python": "@lru_cache caches tuple keys (i, rem) in a hash dictionary, incurring high memory overhead.",
            "notes_java": "Boolean[][] uses 3-state (null, true, false) object references."
        },
        {
            "name": "Approach 2: Space-Optimized 1D Reverse Boolean DP",
            "badge": "Optimal",
            "desc": "Compress state to a single BooleanArray(target + 1) and iterate s backwards from target downTo num.",
            "time": "O(N * Target)",
            "space": "O(Target)",
            "code_kotlin": """class Solution {
    fun canPartition(nums: IntArray): Boolean {
        val total = nums.sum()
        if (total % 2 != 0) return false
        val target = total / 2

        val dp = BooleanArray(target + 1)
        dp[0] = true

        for (num in nums) {
            for (s in target downTo num) {
                if (dp[s - num]) {
                    dp[s] = true
                }
            }
            if (dp[target]) return true
        }
        return dp[target]
    }
}""",
            "code_python": """class Solution:
    def canPartition(self, nums: list[int]) -> bool:
        total = sum(nums)
        if total % 2 != 0:
            return False
        target = total // 2

        dp = [False] * (target + 1)
        dp[0] = True

        for num in nums:
            for s in range(target, num - 1, -1):
                if dp[s - num]:
                    dp[s] = True
            if dp[target]:
                return True
        return dp[target]""",
            "code_java": """class Solution {
    public boolean canPartition(int[] nums) {
        int total = 0;
        for (int num : nums) total += num;
        if (total % 2 != 0) return false;
        int target = total / 2;

        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {
            for (int s = target; s >= num; s--) {
                if (dp[s - num]) {
                    dp[s] = true;
                }
            }
            if (dp[target]) return true;
        }
        return dp[target];
    }
}""",
            "notes_kotlin": "Primitive BooleanArray(target + 1) fits entirely in CPU L1 data cache (~10KB max).",
            "notes_python": "Checking if dp[target]: return True inside the outer loop exits early as soon as target is formed.",
            "notes_java": "Contiguous boolean[] reverse scan is vectorizable and allocation-free."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun canPartition(nums: IntArray): Boolean {
        // Write your solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def canPartition(self, nums: list[int]) -> bool:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public boolean canPartition(int[] nums) {
        // Write your solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { nums: [1,5,11,5] }, expected: true },
          { input: { nums: [1,2,3,5] }, expected: false },
          { input: { nums: [2,2,1,1] }, expected: true }
        ];
        """
    },
    "spoken_script": {
        "phase1": "First, are all elements in nums strictly positive integers? And if the total sum of nums is odd, can we immediately return false?",
        "phase2": "Yes—if totalSum is odd, two equal integer subsets cannot exist. Otherwise, the problem reduces to 0/1 Subset Sum with target = totalSum / 2. Instead of an O(N * Target) 2D table, we can use a 1D BooleanArray(target + 1) iterated backwards from target down to num in O(N * Target) time and O(Target) space.",
        "phase3": "I'll compute total = nums.sum(), return false if total % 2 != 0, and set target = total / 2. I initialize dp = BooleanArray(target + 1) with dp[0] = true. For each num in nums, I loop s from target downTo num and set dp[s] = dp[s] || dp[s - num]. If dp[target] becomes true, I return true early.",
        "phase4": "Why iterate s backwards? Because if num = 5 and we iterated forward, setting dp[5] = true would immediately cause s = 10 to see dp[10 - 5] == true in the same iteration, illegally using the same 5 twice!",
        "senior_followup": "In Python or C++, we can represent the entire boolean DP array as a single big integer bitmask `bits = 1`, updating `bits |= (bits << num)` in a single CPU bit-shift instruction per element!"
    }
}

# =========================================================================
# PROBLEM 110: LC #91 DECODE WAYS (Pass 2B Stretch Medium)
# =========================================================================
p110 = {
    "num": 110,
    "lc": 91,
    "title": "Decode Ways",
    "category": "Dynamic Programming",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "decode-ways",
    "verbatim_statement": "A message containing letters from <code>A-Z</code> can be <strong>encoded</strong> into numbers using the following mapping:<br><code>'A' -> \"1\"</code>, <code>'B' -> \"2\"</code>, ..., <code>'Z' -> \"26\"</code>.<br><br>To <strong>decode</strong> an encoded message, all the digits must be grouped then mapped back into letters using the reverse of the mapping above (there may be multiple ways). For example, <code>\"11106\"</code> can be mapped into:<br>• <code>\"AAJF\"</code> with the grouping <code>(1 1 10 6)</code><br>• <code>\"KJF\"</code> with the grouping <code>(11 10 6)</code><br>Note that the grouping <code>(1 11 06)</code> is invalid because <code>\"06\"</code> cannot be mapped into <code>'F'</code> since <code>\"6\"</code> is different from <code>\"06\"</code>.<br><br>Given a string <code>s</code> containing only digits, return <em>the <strong>number</strong> of ways to <strong>decode</strong> it</em>.",
    "examples": [
        {
            "title": "Example 1",
            "input": "s = \"12\"",
            "output": "2"
        },
        {
            "title": "Example 2",
            "input": "s = \"226\"",
            "output": "3"
        },
        {
            "title": "Example 3",
            "input": "s = \"06\"",
            "output": "0"
        }
    ],
    "constraints": [
        "1 <= s.length <= 100",
        "s contains only digits and may contain leading zero(s)."
    ],
    "plain_english": "Count how many valid ways a string of digits can be sliced into 1-digit ('1'..'9') and 2-digit ('10'..'26') letter codes. Watch out for '0': '0' can NEVER stand alone, and can only survive as the second digit of '10' or '20'.",
    "boundary_checks": {
        "min_base": "s starts with '0' (e.g. \"0\" or \"06\") -> 0 valid ways! Immediately return 0.",
        "best_exit": "s = \"10\" -> Only 1 way ('J'), because '0' cannot be decoded alone.",
        "worst_scan": "s = \"111111...\" -> Fibonacci growth where every step branches into both 1-digit and 2-digit decodings."
    },
    "mental_model": {
        "analogy_title": "Climbing Stairs with Locked '0' Steps",
        "analogy_text": "Decode Ways is identical to Climbing Stairs (where you can take a 1-digit step or a 2-digit step), EXCEPT some steps are booby-trapped! A 1-digit step from index i is only legal if s[i] != '0' (since '1'..'9' are valid). A 2-digit step from index i-1 to i+1 is only legal if the two digits form a number between 10 and 26 (i.e., s[i-1] is '1', or s[i-1] is '2' and s[i] <= '6').",
        "aha_moment": "At each position i, ways[i] = (ways[i-1] if s[i] != '0' else 0) + (ways[i-2] if s[i-1..i] in 10..26 else 0). And since we only look back 2 steps, we only need two integer variables!",
        "trigger": "Counting valid ways to partition a string into 1-char or 2-char tokens under validity rules.",
        "sisters": [
            {"name": "Climbing Stairs", "lc": "#70"},
            {"name": "Word Break", "lc": "#139"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(2^N) Time, O(N) Space",
        "naive_text": "Recursively branch at index i into decode(i + 1) and decode(i + 2) without memoization.",
        "wasted_work": "On a string of 100 '1's, the recursion tree branches like Fibonacci F(100) ~ 3.5 x 10^20 calls.",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Track prev2 = 1 (empty prefix) and prev1 = 1 (after verifying s[0] != '0'). For i from 1 to n - 1, compute curr by adding prev1 (if s[i] != '0') and prev2 (if twoDigit in 10..26), then shift prev2 = prev1, prev1 = curr.",
        "core_invariant": "At the end of iteration i, prev1 holds the exact number of valid decodings for prefix s[0..i].",
        "table_rows": [
            {
                "name": "Naive: Unmemoized Recursive Branching",
                "time": "O(2^N)",
                "space": "O(N)",
                "note": "Exponential Fibonacci branching; TLE on length 45+."
            },
            {
                "name": "Better: 1D DP Array (IntArray(N + 1))",
                "time": "O(N)",
                "space": "O(N)",
                "note": "Linear time, allocates O(N) array."
            },
            {
                "name": "Optimal: Two-Variable Rolling DP",
                "time": "O(N)",
                "space": "O(1)",
                "note": "★ Constant space using only prev2 and prev1 registers."
            }
        ]
    },
    "trace_table": {
        "input_desc": "s = \"226\" (Valid decodings: \"2,2,6\" [BBF], \"22,6\" [VF], \"2,26\" [BZ])",
        "columns": ["Index i", "Char s[i]", "1-Digit Check (s[i] != '0')", "2-Digit Check (s[i-1..i] in 10..26)", "curr Ways", "New (prev2, prev1)"],
        "rows": [
            ["Init (i=0)", "'2'", "'2' != '0' -> valid (1)", "-", "1", "(prev2 = 1, prev1 = 1)"],
            ["i = 1", "'2'", "'2' != '0' -> +prev1 (1)", "\"22\" in 10..26 -> +prev2 (1)", "1 + 1 = 2", "(prev2 = 1, prev1 = 2)"],
            ["i = 2", "'6'", "'6' != '0' -> +prev1 (2)", "\"26\" in 10..26 -> +prev2 (1)", "2 + 1 = 3", "(prev2 = 2, prev1 = 3) -> Return 3"]
        ],
        "takeaway": "If s = \"206\", at i=1 ('0') the 1-digit check is 0 and the 2-digit check (\"20\") adds prev2 (1) -> curr = 1. Then at i=2 ('6'), 1-digit adds 1 and 2-digit (\"06\") adds 0 -> total 1!"
    },
    "stepper": {
        "title": "Rolling 2-State Decode Ways Stepper",
        "default_input": "226",
        "presets": [
            {"label": "Ex 2: \"226\" -> 3", "onclick": "loadPreset('226')"},
            {"label": "Zero Trap: \"11106\" -> 2", "onclick": "loadPreset('11106')"},
            {"label": "Invalid: \"06\" -> 0", "onclick": "loadPreset('06')"}
        ],
        "metrics": [
            {"label": "Index i / Char", "id": "metric-i", "initial": "0 ('2')", "color": "text-cyan-400"},
            {"label": "prev2 (i-2)", "id": "metric-p2", "initial": "1", "color": "text-slate-300"},
            {"label": "prev1 (i-1)", "id": "metric-p1", "initial": "1", "color": "text-amber-400"},
            {"label": "curr Ways", "id": "metric-curr", "initial": "1", "color": "text-emerald-400"}
        ],
        "visual_html": "<div id='decode-viz' class='flex flex-wrap items-center justify-center gap-2'></div>",
        "initial_narration": "Checked s[0] != '0'. Initialized prev2 = 1, prev1 = 1.",
        "pseudocode_lines": [
            "if (s.isEmpty() || s[0] == '0') return 0",
            "var prev2 = 1; var prev1 = 1",
            "for (i in 1 until s.length) {",
            "  var curr = if (s[i] != '0') prev1 else 0",
            "  val twoDigit = (s[i - 1] - '0') * 10 + (s[i] - '0')",
            "  if (twoDigit in 10..26) curr += prev2",
            "  prev2 = prev1; prev1 = curr",
            "}",
            "return prev1"
        ],
        "js_code": """
        let decStr = "226";
        function buildSteps(cand) {
          if (typeof cand === 'string' && cand.length > 0) decStr = cand;
          steps = [];
          if (decStr[0] === '0') {
            steps.push({ idx: 0, p2: 0, p1: 0, curr: 0, line: 1, narration: `Leading '0' detected at s[0]! Cannot decode -> return 0 immediately.` });
            return;
          }
          let p2 = 1, p1 = 1;
          steps.push({ idx: 0, p2, p1, curr: 1, line: 2, narration: `s[0]='${decStr[0]}' is valid. Initialized prev2=1, prev1=1.` });
          for (let i = 1; i < decStr.length; i++) {
            let oneOk = decStr[i] !== '0';
            let twoVal = parseInt(decStr.substring(i - 1, i + 1), 10);
            let twoOk = twoVal >= 10 && twoVal <= 26;
            let curr = (oneOk ? p1 : 0) + (twoOk ? p2 : 0);
            steps.push({
              idx: i, p2, p1, curr, line: 6,
              narration: `i=${i} ('${decStr[i]}'): 1-digit=${oneOk ? '+'+p1 : '0'}, 2-digit("${decStr.substring(i-1,i+1)}")=${twoOk ? '+'+p2 : '0'} -> curr=${curr}.`
            });
            p2 = p1;
            p1 = curr;
          }
        }
        function renderStage(step) {
          const c = document.getElementById('decode-viz');
          if (!c) return;
          c.innerHTML = decStr.split('').map((ch, idx) => {
            let cls = 'border-slate-800 bg-slate-900 text-slate-400';
            if (idx === step.idx) cls = 'border-emerald-400 bg-emerald-950/60 text-emerald-300 scale-105';
            else if (idx === step.idx - 1) cls = 'border-cyan-400 bg-cyan-950/40 text-cyan-300';
            return `<div class="w-12 h-14 rounded-xl border-2 flex flex-col items-center justify-center font-mono text-xs ${cls}">
              <span class="text-[9px] text-slate-500">s[${idx}]</span>
              <span class="font-bold">'${ch}'</span>
            </div>`;
          }).join('');
        }
        function loadPreset(s) { decStr = s; buildSteps(s); stepperReset(); }
        """,
        "js_update_metrics": """
        document.getElementById('metric-i').textContent = `${step.idx} ('${decStr[step.idx] || ''}')`;
        document.getElementById('metric-p2').textContent = step.p2;
        document.getElementById('metric-p1').textContent = step.p1;
        document.getElementById('metric-curr').textContent = step.curr;
        """
    },
    "approaches": [
        {
            "name": "Approach 1: 1D DP Array (Bottom-Up Prefix Table)",
            "badge": "1D DP",
            "desc": "Maintain dp[i] = number of ways to decode prefix of length i, checking 1-digit and 2-digit transitions.",
            "time": "O(N)",
            "space": "O(N)",
            "code_kotlin": """class Solution {
    fun numDecodings(s: String): Int {
        if (s.isEmpty() || s[0] == '0') return 0
        val n = s.length
        val dp = IntArray(n + 1)
        dp[0] = 1
        dp[1] = 1

        for (i in 2..n) {
            val oneDigit = s[i - 1] - '0'
            val twoDigits = (s[i - 2] - '0') * 10 + oneDigit
            if (oneDigit in 1..9) {
                dp[i] += dp[i - 1]
            }
            if (twoDigits in 10..26) {
                dp[i] += dp[i - 2]
            }
        }
        return dp[n]
    }
}""",
            "code_python": """class Solution:
    def numDecodings(self, s: str) -> int:
        if not s or s[0] == '0':
            return 0
        n = len(s)
        dp = [0] * (n + 1)
        dp[0] = 1
        dp[1] = 1

        for i in range(2, n + 1):
            if s[i - 1] != '0':
                dp[i] += dp[i - 1]
            two_digit = int(s[i - 2 : i])
            if 10 <= two_digit <= 26:
                dp[i] += dp[i - 2]
        return dp[n]""",
            "code_java": """class Solution {
    public int numDecodings(String s) {
        if (s == null || s.isEmpty() || s.charAt(0) == '0') return 0;
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            int oneDigit = s.charAt(i - 1) - '0';
            int twoDigits = (s.charAt(i - 2) - '0') * 10 + oneDigit;
            if (oneDigit >= 1) dp[i] += dp[i - 1];
            if (twoDigits >= 10 && twoDigits <= 26) dp[i] += dp[i - 2];
        }
        return dp[n];
    }
}""",
            "notes_kotlin": "Clear 1-indexed prefix state where dp[0] = 1 represents the empty prefix base case.",
            "notes_python": "Allocates an O(N) list when only the last 2 states are ever read.",
            "notes_java": "Char arithmetic (s.charAt(i) - '0') avoids Integer.parseInt substring allocations."
        },
        {
            "name": "Approach 2: Two-Variable Rolling DP (O(1) Space)",
            "badge": "Optimal",
            "desc": "Compress dp[i-2] and dp[i-1] into two scalar integers prev2 and prev1.",
            "time": "O(N)",
            "space": "O(1)",
            "code_kotlin": """class Solution {
    fun numDecodings(s: String): Int {
        if (s.isEmpty() || s[0] == '0') return 0
        var prev2 = 1
        var prev1 = 1

        for (i in 1 until s.length) {
            var curr = if (s[i] != '0') prev1 else 0
            val twoDigits = (s[i - 1] - '0') * 10 + (s[i] - '0')
            if (twoDigits in 10..26) {
                curr += prev2
            }
            prev2 = prev1
            prev1 = curr
        }
        return prev1
    }
}""",
            "code_python": """class Solution:
    def numDecodings(self, s: str) -> int:
        if not s or s[0] == "0":
            return 0
        prev2, prev1 = 1, 1

        for i in range(1, len(s)):
            curr = prev1 if s[i] != "0" else 0
            two_digits = (ord(s[i - 1]) - 48) * 10 + (ord(s[i]) - 48)
            if 10 <= two_digits <= 26:
                curr += prev2
            prev2, prev1 = prev1, curr
        return prev1""",
            "code_java": """class Solution {
    public int numDecodings(String s) {
        if (s == null || s.isEmpty() || s.charAt(0) == '0') return 0;
        int prev2 = 1;
        int prev1 = 1;

        for (int i = 1; i < s.length(); i++) {
            int curr = (s.charAt(i) != '0') ? prev1 : 0;
            int twoDigits = (s.charAt(i - 1) - '0') * 10 + (s.charAt(i) - '0');
            if (twoDigits >= 10 && twoDigits <= 26) {
                curr += prev2;
            }
            prev2 = prev1;
            prev1 = curr;
        }
        return prev1;
    }
}""",
            "notes_kotlin": "If an un-decodable '00' or '30' occurs, curr becomes 0 and naturally propagates 0 to the end.",
            "notes_python": "Tuple swap prev2, prev1 = prev1, curr updates state in O(1) registers.",
            "notes_java": "Zero heap allocations; runs in O(N) time and O(1) auxiliary memory."
        }
    ],
    "scratchpad": {
        "kotlin_boilerplate": """class Solution {
    fun numDecodings(s: String): Int {
        // Write your solution here:
        
    }
}""",
        "python_boilerplate": """class Solution:
    def numDecodings(self, s: str) -> int:
        # Write your solution here:
        pass""",
        "java_boilerplate": """class Solution {
    public int numDecodings(String s) {
        // Write your solution here:
        
    }
}""",
        "js_test_runner": """
        const tests = [
          { input: { s: "12" }, expected: 2 },
          { input: { s: "226" }, expected: 3 },
          { input: { s: "06" }, expected: 0 }
        ];
        """
    },
    "spoken_script": {
        "phase1": "Before coding, I'll confirm: the string contains only digits '0'-'9', and any leading zero like \"06\" is invalid and should return 0, correct?",
        "phase2": "Recursive branching without memoization takes O(2^N) time. Since the number of decodings at index i only depends on i - 1 (single-digit decode '1'..'9') and i - 2 (two-digit decode '10'..'26'), we can use two rolling variables prev2 and prev1 in O(N) time and O(1) space.",
        "phase3": "First, if s[0] == '0' we return 0 immediately. Otherwise we set prev2 = 1 and prev1 = 1. Looping i from 1 to s.lastIndex, curr starts at prev1 if s[i] != '0' else 0. Then we compute twoDigits = (s[i-1] - '0') * 10 + (s[i] - '0'); if twoDigits is in 10..26, we add prev2 to curr. Finally we shift prev2 = prev1 and prev1 = curr.",
        "phase4": "Let's test the tricky zero case s = \"100\": i=0 ('1') -> p2=1, p1=1. i=1 ('0') -> 1-digit is 0, 2-digit \"10\" is valid -> curr=1, p2=1, p1=1. i=2 ('0') -> 1-digit is 0, 2-digit \"00\" is invalid -> curr=0, p1=0. Returns 0!",
        "senior_followup": "If the string can contain wildcard '*' characters (Decode Ways II, LC #639), the same 2-variable rolling DP applies, multiplying transitions by the number of ways '*' can match 1..9 (9 ways) or 10..26 (15 ways) modulo 10^9 + 7."
    }
}

all_problems_104_110 = [p104, p105, p106, p107, p108, p109, p110]
