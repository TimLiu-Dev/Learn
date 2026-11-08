import sys
sys.path.append('scripts')

p42 = {
    'num': 42,
    'lc': 208,
    'title': "Implement Trie (Prefix Tree)",
    'category': "Tries & String Parsing",
    'difficulty': "Medium",
    'lc_slug': "implement-trie-prefix-tree",
    'verbatim_statement': """<p>A <strong>trie</strong> (pronounced as "try") or <strong>prefix tree</strong> is a tree data structure used to efficiently store and retrieve keys in a dataset of strings. There are various applications of this data structure, such as autocomplete and spellchecker.</p>
<p>Implement the <code>Trie</code> class:</p>
<ul>
  <li><code>Trie()</code> Initializes the trie object.</li>
  <li><code>void insert(String word)</code> Inserts the string <code>word</code> into the trie.</li>
  <li><code>boolean search(String word)</code> Returns <code>true</code> if the string <code>word</code> is in the trie (i.e., was inserted before), and <code>false</code> otherwise.</li>
  <li><code>boolean startsWith(String prefix)</code> Returns <code>true</code> if there is a previously inserted string <code>word</code> that has the prefix <code>prefix</code>, and <code>false</code> otherwise.</li>
</ul>""",
    'examples': [
        {'title': "Example 1", 'input': '["Trie", "insert", "search", "search", "startsWith", "insert", "search"]\n[[], ["apple"], ["apple"], ["app"], ["app"], ["app"], ["app"]]', 'output': '[null, null, true, false, true, null, true]'}
    ],
    'constraints': [
        "1 <= word.length, prefix.length <= 2000",
        "word and prefix consist only of lowercase English letters.",
        "At most 3 * 10^4 calls in total will be made to insert, search, and startsWith."
    ],
    'plain_english': "A tree where each node has up to 26 children ('a' through 'z') and a boolean `isEnd` flag. To insert, walk characters, creating nodes as needed, marking the terminal node `isEnd = true`. For search, ensure path exists and `isEnd == true`. For startsWith, ensure path exists regardless of `isEnd`.",
    'boundary_checks': {
        'min_base': "Single character word <code>insert('a')</code> &rarr; Creates root child at index 0, marks `isEnd = true`.",
        'best_exit': "Search prefix that equals full word &rarr; Both `search` and `startsWith` return true.",
        'worst_scan': "Querying word that fails at first character &rarr; Immediately returns false in O(1)."
    },
    'mental_model': {
        'analogy_title': "Airport Luggage Routing Hub",
        'analogy_text': "Think of an automated airport conveyor system. Bags are routed conveyor by conveyor based on the code letters 'A-P-P-L-E'. Conveyor A branches to P, then P, then L, then E. At room E, a green stamp 'FINAL DESTINATION' is marked. If a baggage handler asks: 'Are there any bags routed through A-P-P?' (startsWith), we answer YES because room P exists. If they ask: 'Is A-P-P a final flight?' (search), we check if room P has the green stamp.",
        'aha_moment': "`search` and `startsWith` share identical node-walking logic! Extracting a private helper `findPrefixNode(str)` unifies both operations: `startsWith` returns `node != null`, while `search` returns `node != null && node.isEnd`.",
        'trigger': "Prefix matching, autocomplete suggestions, dictionary word lookup, boggle solver &rarr; Trie (Prefix Tree).",
        'sisters': [
            {'name': "Design Add and Search Words Data Structure", 'lc': "LC #211"},
            {'name': "Word Search II", 'lc': "LC #212"},
            {'name': "Replace Words", 'lc': "LC #648"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "Search: O(L), Prefix: O(W * L) Time",
        'naive_text': "Store words in a HashSet. Exact search is O(L), but `startsWith(prefix)` requires scanning all W words in the dictionary.",
        'wasted_work': "Re-scanning every word in the dictionary for prefix queries.",
        'optimal_complexity': "O(L) Time per Op, O(Total Chars * 26) Space",
        'optimal_text': "26-Way Array TrieNode. Insert, search, and startsWith all execute in strict O(L) time proportional to query length L, completely independent of total words W.",
        'core_invariant': "Every path from the root node represents a unique prefix formed by the concatenated edge characters.",
        'table_rows': [
            {'name': "1. HashSet + Linear Prefix Scan", 'time': "Search: O(L), Prefix: O(W * L)", 'space': "O(W * L)", 'note': "Prefix search degrades with large dictionaries."},
            {'name': "2. HashMap-Based TrieNode", 'time': "Insert/Search/Prefix: O(L)", 'space': "O(Total Chars)", 'note': "Memory adaptive for Unicode charsets."},
            {'name': "★ 3. 26-Way Array TrieNode", 'time': "Insert/Search/Prefix: O(L)", 'space': "O(Total Chars * 26)", 'note': "Optimal cache locality and zero hash collisions."}
        ]
    },
    'trace_table': {
        'input_desc': 'insert("apple"), search("app"), startsWith("app"), insert("app"), search("app")',
        'columns': ["Operation", "Word/Prefix", "Path Navigated", "Terminal isEnd?", "Return Value"],
        'rows': [
            ["insert", "apple", "root -> a -> p -> p -> l -> e", "Marked true at 'e'", "void"],
            ["search", "app", "root -> a -> p -> p", "false (node exists, but isEnd=false)", "false"],
            ["startsWith", "app", "root -> a -> p -> p", "Doesn't matter (node != null)", "true"],
            ["insert", "app", "root -> a -> p -> p", "Marked true at 'p'", "void"],
            ["search", "app", "root -> a -> p -> p", "true (now marked isEnd=true!)", "true"]
        ],
        'takeaway': "Shared prefix 'app' reuses the exact same 3 node instances for both 'apple' and 'app'."
    },
    'stepper': {
        'title': "Trie Insertion & Prefix Traversal Stepper",
        'default_input': 'insert:apple, search:app, insert:app, search:app',
        'presets': [
            {'label': "Insert 'apple' & 'app'", 'onclick': 'loadPreset(["insert:apple", "search:app", "startsWith:app", "insert:app", "search:app"])'},
            {'label': "Prefix 'ban'", 'onclick': 'loadPreset(["insert:banana", "startsWith:ban", "search:ban"])'}
        ],
        'metrics': [
            {'id': "metric-cmd", 'label': "Current Command", 'color': "text-amber-400", 'initial': "Init"},
            {'id': "metric-char", 'label': "Letter Walked", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-isend", 'label': "Terminal isEnd", 'color': "text-emerald-400", 'initial': "false"},
            {'id': "metric-output", 'label': "Output", 'color': "text-purple-400", 'initial': "-"}
        ],
        'visual_html': '<div id="trie-stage" class="w-full flex items-center justify-center gap-2 py-4 min-h-[140px] overflow-x-auto"></div>',
        'initial_narration': 'Click "Next Step" to trace letter-by-letter trie path navigation.',
        'pseudocode_lines': [
            "class TrieNode {",
            "    val children = arrayOfNulls<TrieNode>(26)",
            "    var isEnd = false",
            "}",
            "fun insert(word: String) {",
            "    var curr = root",
            "    for (ch in word) {",
            "        val idx = ch - 'a'",
            "        if (curr.children[idx] == null) curr.children[idx] = TrieNode()",
            "        curr = curr.children[idx]!!",
            "    }",
            "    curr.isEnd = true",
            "}"
        ],
        'js_code': """
    let trieCommands = ["insert:apple", "search:app", "startsWith:app", "insert:app", "search:app"];

    function buildSteps(cmds) {
      steps = [];
      let root = { children: {}, isEnd: false };

      cmds.forEach(cmd => {
        let [op, word] = cmd.split(':');
        let curr = root;
        let path = [];

        steps.push({
          cmd: `${op}("${word}")`, char: "-", isEnd: false, out: "-",
          word: word, path: [], line: 5,
          narration: `Starting operation ${op}("${word}").`
        });

        if (op === 'insert') {
          for (let ch of word) {
            if (!curr.children[ch]) {
              curr.children[ch] = { children: {}, isEnd: false };
            }
            curr = curr.children[ch];
            path.push(ch);
            steps.push({
              cmd: `${op}("${word}")`, char: ch, isEnd: curr.isEnd, out: "-",
              word: word, path: [...path], line: 9,
              narration: `Navigated edge '${ch}'.`
            });
          }
          curr.isEnd = true;
          steps.push({
            cmd: `${op}("${word}")`, char: word[word.length - 1], isEnd: true, out: "void",
            word: word, path: [...path], line: 12,
            narration: `Finished inserting "${word}". Terminal node marked isEnd = true.`
          });
        } else if (op === 'search') {
          let found = true;
          for (let ch of word) {
            if (!curr.children[ch]) { found = false; break; }
            curr = curr.children[ch];
            path.push(ch);
          }
          let res = found && curr.isEnd;
          steps.push({
            cmd: `${op}("${word}")`, char: path[path.length - 1] || "-", isEnd: curr ? curr.isEnd : false, out: res ? "TRUE" : "FALSE",
            word: word, path: [...path], line: 3,
            narration: `Search("${word}"): Found path=${found}, terminal isEnd=${curr ? curr.isEnd : false} &rarr; Result: ${res}.`
          });
        } else if (op === 'startsWith') {
          let found = true;
          for (let ch of word) {
            if (!curr.children[ch]) { found = false; break; }
            curr = curr.children[ch];
            path.push(ch);
          }
          steps.push({
            cmd: `${op}("${word}")`, char: path[path.length - 1] || "-", isEnd: curr ? curr.isEnd : false, out: found ? "TRUE" : "FALSE",
            word: word, path: [...path], line: 3,
            narration: `startsWith("${word}"): Path exists=${found} &rarr; Result: ${found}.`
          });
        }
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      trieCommands = arr;
      stepperReset();
      buildSteps(trieCommands);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => s.trim()).filter(s => s.length > 0);
      if (arr.length > 0) {
        trieCommands = arr;
        stepperReset();
        buildSteps(trieCommands);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-cmd').textContent = step.cmd;
      document.getElementById('metric-char').textContent = step.char;
      document.getElementById('metric-isend').textContent = step.isEnd ? "true" : "false";
      document.getElementById('metric-output').textContent = step.out;
""",
        'js_render_visual': """
      const stage = document.getElementById('trie-stage');
      stage.innerHTML = '';
      if (!step.path) return;

      step.path.forEach((c, idx) => {
        const node = document.createElement('div');
        const isLast = (idx === step.path.length - 1);
        node.className = `flex flex-col items-center justify-center w-12 h-12 rounded-xl font-mono font-black text-sm border ${
          isLast ? 'bg-amber-500/20 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' : 'bg-slate-900 text-slate-200 border-slate-800'
        }`;
        node.innerHTML = `<span>${c}</span>`;
        stage.appendChild(node);
        if (idx < step.path.length - 1) {
          const arrow = document.createElement('span');
          arrow.className = 'text-slate-600 font-bold';
          arrow.textContent = '→';
          stage.appendChild(arrow);
        }
      });
"""
    },
    'approaches': [
        {
            'name': "HashSet + Linear Prefix Scan (Naive)",
            'desc': "Store words in a HashSet. Search checks set membership in O(L). startsWith scans all words in O(W * L).",
            'time': "Search: O(L), Prefix: O(W * L)",
            'space': "O(W * L)",
            'code_kotlin': """class Trie() {
    private val words = HashSet<String>()

    fun insert(word: String) {
        words.add(word)
    }

    fun search(word: String): Boolean {
        return words.contains(word)
    }

    fun startsWith(prefix: String): Boolean {
        return words.any { it.startsWith(prefix) }
    }
}""",
            'notes_kotlin': "Linear search through entire dictionary makes `startsWith` crawl on large wordsets.",
            'code_python': """class Trie:
    def __init__(self):
        self.words = set()

    def insert(self, word: str) -> None:
        self.words.add(word)

    def search(self, word: str) -> bool:
        return word in self.words

    def startsWith(self, prefix: str) -> bool:
        return any(w.startswith(prefix) for w in self.words)""",
            'notes_python': "Inefficient for prefixes. Fails time limit on datasets with 30,000 words.",
            'code_java': """class Trie {
    private final Set<String> words = new HashSet<>();

    public void insert(String word) {
        words.add(word);
    }

    public boolean search(String word) {
        return words.contains(word);
    }

    public boolean startsWith(String prefix) {
        for (String w : words) {
            if (w.startsWith(prefix)) return true;
        }
        return false;
    }
}""",
            'notes_java': "O(W * L) prefix verification wastes CPU cycles."
        },
        {
            'name': "HashMap-Based Dynamic TrieNode",
            'desc': "Use a HashMap for child links. Saves memory when the character set is large or sparse (e.g. Unicode).",
            'time': "O(L) per operation",
            'space': "O(Total Chars)",
            'code_kotlin': """class Trie() {
    private class TrieNode {
        val children = HashMap<Char, TrieNode>()
        var isEnd = false
    }

    private val root = TrieNode()

    fun insert(word: String) {
        var curr = root
        for (ch in word) {
            curr = curr.children.computeIfAbsent(ch) { TrieNode() }
        }
        curr.isEnd = true
    }

    fun search(word: String): Boolean {
        var curr = root
        for (ch in word) {
            curr = curr.children[ch] ?: return false
        }
        return curr.isEnd
    }

    fun startsWith(prefix: String): Boolean {
        var curr = root
        for (ch in prefix) {
            curr = curr.children[ch] ?: return false
        }
        return true
    }
}""",
            'notes_kotlin': "Map-based approach. Ideal for arbitrary character alphabets.",
            'code_python': """class Trie:
    def __init__(self):
        self.root = {}

    def insert(self, word: str) -> None:
        curr = self.root
        for ch in word:
            curr = curr.setdefault(ch, {})
        curr['#'] = True

    def search(self, word: str) -> bool:
        curr = self.root
        for ch in word:
            if ch not in curr: return False
            curr = curr[ch]
        return '#' in curr

    def startsWith(self, prefix: str) -> bool:
        curr = self.root
        for ch in prefix:
            if ch not in curr: return False
            curr = curr[ch]
        return True""",
            'notes_python': "Nested dictionary idiom. Extremely fast and pythonic.",
            'code_java': """class Trie {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
    }

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode curr = root;
        for (char ch : word.toCharArray()) {
            curr = curr.children.computeIfAbsent(ch, k -> new TrieNode());
        }
        curr.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode curr = root;
        for (char ch : word.toCharArray()) {
            curr = curr.children.get(ch);
            if (curr == null) return false;
        }
        return curr.isEnd;
    }

    public boolean startsWith(String prefix) {
        TrieNode curr = root;
        for (char ch : prefix.toCharArray()) {
            curr = curr.children.get(ch);
            if (curr == null) return false;
        }
        return true;
    }
}""",
            'notes_java': "Flexible HashMap-based implementation."
        },
        {
            'name': "26-Way Array TrieNode (Optimal Primary)",
            'desc': "Array of size 26 per node (`TrieNode[26]`). Zero hashing overhead and maximum memory locality.",
            'time': "O(L) per operation",
            'space': "O(Total Chars * 26)",
            'code_kotlin': """class Trie() {
    /**
     * Optimal: 26-Way Fixed Array Trie
     * Time: O(L) - Operations proportional to query length L
     * Space: O(N * 26) - Fixed alphabet storage
     */
    private class TrieNode {
        val children = arrayOfNulls<TrieNode>(26)
        var isEnd = false
    }

    private val root = TrieNode()

    fun insert(word: String) {
        var curr = root
        for (ch in word) {
            val idx = ch - 'a'
            if (curr.children[idx] == null) {
                curr.children[idx] = TrieNode()
            }
            curr = curr.children[idx]!!
        }
        curr.isEnd = true
    }

    fun search(word: String): Boolean {
        val node = findPrefixNode(word)
        return node != null && node.isEnd
    }

    fun startsWith(prefix: String): Boolean {
        return findPrefixNode(prefix) != null
    }

    private fun findPrefixNode(str: String): TrieNode? {
        var curr = root
        for (ch in str) {
            val idx = ch - 'a'
            curr = curr.children[idx] ?: return null
        }
        return curr
    }
}""",
            'notes_kotlin': "`findPrefixNode` deduplicates navigation logic between `search` and `startsWith`.",
            'code_python': """class TrieNode:
    def __init__(self):
        self.children = [None] * 26
        self.is_end = False

class Trie:
    # Optimal: 26-Way Array Trie
    # Time: O(L) per operation
    # Space: O(Total Chars * 26)
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word: str) -> None:
        curr = self.root
        for ch in word:
            idx = ord(ch) - ord('a')
            if not curr.children[idx]:
                curr.children[idx] = TrieNode()
            curr = curr.children[idx]
        curr.is_end = True

    def search(self, word: str) -> bool:
        node = self._find_node(word)
        return node is not None and node.is_end

    def startsWith(self, prefix: str) -> bool:
        return self._find_node(prefix) is not None

    def _find_node(self, prefix: str) -> Optional[TrieNode]:
        curr = self.root
        for ch in prefix:
            idx = ord(ch) - ord('a')
            if not curr.children[idx]:
                return None
            curr = curr.children[idx]
        return curr""",
            'notes_python': "Index mapping `ord(ch) - ord('a')` delivers instant O(1) branch indexing.",
            'code_java': """class Trie {
    /**
     * Optimal: 26-Way Array TrieNode
     * Time: O(L) per operation
     * Space: O(Total Chars * 26)
     */
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode curr = root;
        for (int i = 0; i < word.length(); i++) {
            int idx = word.charAt(i) - 'a';
            if (curr.children[idx] == null) {
                curr.children[idx] = new TrieNode();
            }
            curr = curr.children[idx];
        }
        curr.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode findNode(String prefix) {
        TrieNode curr = root;
        for (int i = 0; i < prefix.length(); i++) {
            int idx = prefix.charAt(i) - 'a';
            if (curr.children[idx] == null) return null;
            curr = curr.children[idx];
        }
        return curr;
    }
}""",
            'notes_java': "Gold standard production implementation for lowercase English alphabets."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Trie() {
    private class TrieNode {
        val children = arrayOfNulls<TrieNode>(26)
        var isEnd = false
    }
    private val root = TrieNode()
    fun insert(word: String) {
        var curr = root
        for (c in word) {
            val i = c - 'a'
            if (curr.children[i] == null) curr.children[i] = TrieNode()
            curr = curr.children[i]!!
        }
        curr.isEnd = true
    }
    fun search(word: String): Boolean {
        var curr = root
        for (c in word) {
            curr = curr.children[c - 'a'] ?: return false
        }
        return curr.isEnd
    }
    fun startsWith(prefix: String): Boolean {
        var curr = root
        for (c in prefix) {
            curr = curr.children[c - 'a'] ?: return false
        }
        return true
    }
}""",
        'python_boilerplate': """class Trie:
    def __init__(self):
        self.root = {}
    def insert(self, word: str) -> None:
        c = self.root
        for ch in word:
            c = c.setdefault(ch, {})
        c['#'] = True
    def search(self, word: str) -> bool:
        c = self.root
        for ch in word:
            if ch not in c: return False
            c = c[ch]
        return '#' in c
    def startsWith(self, prefix: str) -> bool:
        c = self.root
        for ch in prefix:
            if ch not in c: return False
            c = c[ch]
        return True""",
        'java_boilerplate': """class Trie {
    private static class Node {
        Node[] next = new Node[26];
        boolean end = false;
    }
    private final Node root = new Node();
    public void insert(String word) {
        Node c = root;
        for (char ch : word.toCharArray()) {
            int i = ch - 'a';
            if (c.next[i] == null) c.next[i] = new Node();
            c = c.next[i];
        }
        c.end = true;
    }
    public boolean search(String word) {
        Node c = root;
        for (char ch : word.toCharArray()) {
            if ((c = c.next[ch - 'a']) == null) return false;
        }
        return c.end;
    }
    public boolean startsWith(String prefix) {
        Node c = root;
        for (char ch : prefix.toCharArray()) {
            if ((c = c.next[ch - 'a']) == null) return false;
        }
        return true;
    }
}""",
        'js_test_runner': """
      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';

      function testTrie() {
        let root = { children: {}, isEnd: false };
        function insert(w) {
          let c = root;
          for (let ch of w) {
            if (!c.children[ch]) c.children[ch] = { children: {}, isEnd: false };
            c = c.children[ch];
          }
          c.isEnd = true;
        }
        function search(w) {
          let c = root;
          for (let ch of w) {
            if (!c.children[ch]) return false;
            c = c.children[ch];
          }
          return c.isEnd;
        }
        function startsWith(p) {
          let c = root;
          for (let ch of p) {
            if (!c.children[ch]) return false;
            c = c.children[ch];
          }
          return true;
        }

        insert("apple");
        const t1 = search("apple") === true;
        const t2 = search("app") === false;
        const t3 = startsWith("app") === true;
        insert("app");
        const t4 = search("app") === true;

        const all = t1 && t2 && t3 && t4;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${all ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test: ["apple", "app"] insert/search/startsWith</span>
          <span>${all ? '&#10004; ALL OPERATIONS PASSED' : '&#10008; FAILED'}</span>
        `;
        resultsDiv.appendChild(row);
        return all;
      }

      if (testTrie()) {
        statusSpan.textContent = "All test cases passed!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Some test cases failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
    },
    'spoken_script': {
        'phase1': "I clarify: What characters are expected in word and prefix? Only lowercase English letters 'a' through 'z'. What is the maximum word length? Up to 2,000 characters. How many calls? Up to 30,000 calls across all operations.",
        'phase2': "For strategy: A 26-way Trie (Prefix Tree) is the ideal data structure. Each node has an array `children = TrieNode[26]` and a boolean `isEnd`. Inserting walks characters, creating nodes as needed and marking terminal `isEnd = true`. Search and startsWith share the exact same character walking logic; search checks `node != null && node.isEnd`, while startsWith only checks `node != null`. Time is O(L) per operation, space is O(Total Chars * 26).",
        'phase3': "I implement `TrieNode` with `children = arrayOfNulls<TrieNode>(26)` and `isEnd = false`. In `insert(word)`, I walk character by character computing index `ch - 'a'`, allocating nodes if absent, and setting `curr.isEnd = true`. I create helper `findPrefixNode(str)` to reuse traversal for both `search` and `startsWith`.",
        'phase4': "Dry run: insert('apple') builds path a->p->p->l->e with isEnd=true at 'e'. search('app') reaches 'p' where isEnd=false, returns false. startsWith('app') finds node 'p' != null, returns true. insert('app') marks 'p' isEnd=true. Subsequent search('app') returns true. All boundary cases match expected behavior.",
        'senior_followup': "Senior Scalability: In production autocomplete systems (e.g. search engine suggestions), each TrieNode often caches the top 5 pre-ranked completion ids directly on the node, enabling instant O(L) autocomplete lookups without tree traversal."
    },
    'quiz': [
        {
            'question': "What is the primary operational difference between `search(word)` and `startsWith(prefix)` in a Trie?",
            'options': [
                "search checks that the terminal node has `isEnd == true`, whereas startsWith only checks that the path exists (`node != null`).",
                "search runs in O(N) while startsWith runs in O(log N).",
                "search checks the length of the string while startsWith checks ASCII values.",
                "startsWith modifies the tree structure."
            ],
            'correct_index': 0,
            'explanation': "A prefix exists as long as the character sequence leads to a valid non-null node. Full word search additionally requires that an inserted word actually terminated at that exact node (`isEnd == true`)."
        },
        {
            'question': "What is the time complexity to search for a word of length L in a Trie containing 1,000,000 words?",
            'options': [
                "O(1,000,000 * L)",
                "O(log(1,000,000))",
                "O(L) (strictly proportional to word length, independent of total words in the Trie)",
                "O(26^L)"
            ],
            'correct_index': 2,
            'explanation': "The Trie navigates exactly one child pointer per letter in `word`. Total time is exactly L pointer dereferences, completely independent of how many millions of words are stored."
        }
    ]
}

p43 = {
    'num': 43,
    'lc': 215,
    'title': "Kth Largest Element in an Array",
    'category': "Heaps & Priority Queues",
    'difficulty': "Medium",
    'lc_slug': "kth-largest-element-in-an-array",
    'verbatim_statement': """<p>Given an integer array <code>nums</code> and an integer <code>k</code>, return <em>the</em> <code>k<sup>th</sup></code> <em>largest element in the array</em>.</p><p>Note that it is the <code>k<sup>th</sup></code> largest element in the sorted order, not the <code>k<sup>th</sup></code> distinct element.</p><p>Can you solve it without sorting?</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [3,2,1,5,6,4], k = 2', 'output': '5'},
        {'title': "Example 2", 'input': 'nums = [3,2,3,1,2,4,5,5,6], k = 4', 'output': '4'}
    ],
    'constraints': [
        "1 <= k <= nums.length <= 10^5",
        "-10^4 <= nums[i] <= 10^4"
    ],
    'plain_english': "Find the element that sits at index `n - k` in sorted order. Maintain a Min-Heap of size `k`. When size exceeds `k`, pop the minimum element. After processing the whole array, the root of the Min-Heap is precisely the kth largest element.",
    'boundary_checks': {
        'min_base': "<code>k = 1</code> &rarr; Returns the maximum value in the array.",
        'best_exit': "<code>k = nums.length</code> &rarr; Returns the minimum value in the array.",
        'worst_scan': "Array with all duplicate values <code>[2, 2, 2, 2], k = 2</code> &rarr; Returns 2 smoothly."
    },
    'mental_model': {
        'analogy_title': "The K-Seat VIP Lounge",
        'analogy_text': "A VIP club has exactly k seats. Only the wealthiest patrons are allowed. When a newcomer arrives, if the club isn't full, they take a seat. Once all k seats are filled, any new applicant compares net worth against the poorest person currently in the club (`heap.peek()`). If the newcomer is richer, the poorest member gets kicked out (`heap.poll()`) and the newcomer takes their seat. At the end of the night, who is the poorest person in the VIP club? The K-th richest person overall!",
        'aha_moment': "To find the k-th LARGEST element, use a MIN-HEAP of size k! The smallest element in the top-k group is the barrier value—the k-th largest element.",
        'trigger': "K-th largest/smallest, Top K stream processing &rarr; Min-Heap of size K (or QuickSelect).",
        'sisters': [
            {'name': "Top K Frequent Elements", 'lc': "LC #347"},
            {'name': "Kth Smallest Element in a Sorted Matrix", 'lc': "LC #378"},
            {'name': "Find Median from Data Stream", 'lc': "LC #295"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N log N) Time, O(1) Space",
        'naive_text': "Sort the array in descending order and return `nums[k - 1]`.",
        'wasted_work': "Sorting all N elements when we only care about the boundary between top k and the rest.",
        'optimal_complexity': "O(N log K) Time, O(K) Space",
        'optimal_text': "Min-Heap of size k. Push each element, and poll whenever size exceeds k. Works on unbounded streaming data with bounded memory.",
        'core_invariant': "At all times, the Min-Heap contains precisely the k largest elements seen so far; its root is the smallest among those k.",
        'table_rows': [
            {'name': "1. Full Array Sort", 'time': "O(N log N)", 'space': "O(1) to O(N)", 'note': "Sorts entire array; fails on continuous streams."},
            {'name': "★ 2. Min-Heap of Size K", 'time': "O(N log K)", 'space': "O(K)", 'note': "Bounded O(K) memory; ideal for streaming data."},
            {'name': "3. QuickSelect (Hoare)", 'time': "O(N) Avg, O(N^2) Worst", 'space': "O(1)", 'note': "Average linear time in-place, but degrades on bad pivots."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [3, 2, 1, 5, 6, 4], k = 2',
        'columns': ["Element", "Heap Before", "Action", "Heap After (Size <= 2)", "Heap Peek (2nd Largest)"],
        'rows': [
            ["3", "[]", "Add 3", "[3]", "3"],
            ["2", "[3]", "Add 2", "[2, 3]", "2"],
            ["1", "[2, 3]", "Add 1 &rarr; Size 3 &rarr; Poll min (1)", "[2, 3]", "2"],
            ["5", "[2, 3]", "Add 5 &rarr; Size 3 &rarr; Poll min (2)", "[3, 5]", "3"],
            ["6", "[3, 5]", "Add 6 &rarr; Size 3 &rarr; Poll min (3)", "[5, 6]", "5"],
            ["4", "[5, 6]", "Add 4 &rarr; Size 3 &rarr; Poll min (4)", "[5, 6]", "5"]
        ],
        'takeaway': "Heap root is 5. Top 2 elements overall are [5, 6]. The 2nd largest is 5."
    },
    'stepper': {
        'title': "Min-Heap Kth Largest Stepper",
        'default_input': '3, 2, 1, 5, 6, 4',
        'presets': [
            {'label': "Array [3,2,1,5,6,4], k=2", 'onclick': 'loadPreset([3, 2, 1, 5, 6, 4], 2)'},
            {'label': "Array [3,2,3,1,2,4,5,5,6], k=4", 'onclick': 'loadPreset([3, 2, 3, 1, 2, 4, 5, 5, 6], 4)'}
        ],
        'metrics': [
            {'id': "metric-num", 'label': "Examining Element", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-heap", 'label': "Min-Heap (Size <= K)", 'color': "text-cyan-400", 'initial': "[]"},
            {'id': "metric-kth", 'label': "Current Kth Largest", 'color': "text-emerald-400", 'initial': "-"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="heap-stage" class="w-full flex items-center justify-center gap-3 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace elements entering the Min-Heap.',
        'pseudocode_lines': [
            "val minHeap = PriorityQueue<Int>()",
            "for (num in nums) {",
            "    minHeap.add(num)",
            "    if (minHeap.size > k) minHeap.poll()",
            "}",
            "return minHeap.peek()"
        ],
        'js_code': """
    let dataNums = [3, 2, 1, 5, 6, 4];
    let kTarget = 2;

    function buildSteps(arr, k) {
      steps = [];
      let heap = [];

      for (let i = 0; i < arr.length; i++) {
        let n = arr[i];
        heap.push(n);
        heap.sort((a, b) => a - b);

        let ejected = null;
        if (heap.length > k) {
          ejected = heap.shift();
        }

        steps.push({
          num: n, heap: `[${heap.join(', ')}]`, kth: heap[0],
          action: ejected !== null ? `Added ${n}, ejected min ${ejected}` : `Added ${n}`,
          heapArr: [...heap], line: ejected !== null ? 4 : 3,
          narration: ejected !== null
            ? `Pushed ${n}. Heap size exceeded ${k}, so ejected smallest element ${ejected}. Current Kth largest is ${heap[0]}.`
            : `Pushed ${n}. Heap size is ${heap.length} <= ${k}.`
        });
      }

      steps.push({
        num: "Done", heap: `[${heap.join(', ')}]`, kth: heap[0],
        action: "Finished Scanning",
        heapArr: [...heap], line: 6,
        narration: `Finished scan. Min-heap root ${heap[0]} is the ${k}-th largest element!`
      });
    }

    function loadPreset(arr, k) {
      document.getElementById('custom-input').value = arr.join(', ');
      dataNums = arr;
      kTarget = k;
      stepperReset();
      buildSteps(dataNums, kTarget);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        dataNums = arr;
        stepperReset();
        buildSteps(dataNums, kTarget);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-num').textContent = step.num;
      document.getElementById('metric-heap').textContent = step.heap;
      document.getElementById('metric-kth').textContent = step.kth;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('heap-stage');
      stage.innerHTML = '';
      if (!step.heapArr) return;

      step.heapArr.forEach((v, idx) => {
        const item = document.createElement('div');
        const isMin = (idx === 0);
        item.className = `flex flex-col items-center justify-center w-14 h-14 rounded-xl border font-mono font-bold ${
          isMin ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500 ring-2 ring-emerald-500/40' : 'bg-slate-900 text-slate-300 border-slate-800'
        }`;
        item.innerHTML = `
          <span class="text-[9px] text-slate-400 font-normal uppercase">${isMin ? 'ROOT' : 'NODE'}</span>
          <span class="text-base">${v}</span>
        `;
        stage.appendChild(item);
      });
"""
    },
    'approaches': [
        {
            'name': "Full Array Sort (Naive)",
            'desc': "Sort the entire array in descending order and index `nums[k - 1]` directly.",
            'time': "O(N log N)",
            'space': "O(1) to O(N)",
            'code_kotlin': """class Solution {
    fun findKthLargest(nums: IntArray, k: Int): Int {
        nums.sort()
        return nums[nums.size - k]
    }
}""",
            'notes_kotlin': "In-place primitive dual-pivot Quicksort in Kotlin/Java. Simple but wastes work on large arrays.",
            'code_python': """class Solution:
    def findKthLargest(self, nums: List[int], k: int) -> int:
        nums.sort(reverse=True)
        return nums[k - 1]""",
            'notes_python': "Timsort runs in O(N log N). Does not satisfy the interviewer's request: 'Can you solve it without sorting?'.",
            'code_java': """class Solution {
    public int findKthLargest(int[] nums, int k) {
        Arrays.sort(nums);
        return nums[nums.length - k];
    }
}""",
            'notes_java': "O(N log N) sorting is inefficient when N is 10^7 and k is small."
        },
        {
            'name': "QuickSelect Hoare Partitioning (Average Linear)",
            'desc': "Divide-and-conquer partition around a pivot. Discard half the array at each step.",
            'time': "O(N) Average, O(N^2) Worst",
            'space': "O(1) In-Place",
            'code_kotlin': """class Solution {
    fun findKthLargest(nums: IntArray, k: Int): Int {
        val targetIdx = nums.size - k
        var left = 0
        var right = nums.size - 1

        while (left <= right) {
            val pivotIdx = partition(nums, left, right)
            when {
                pivotIdx == targetIdx -> return nums[pivotIdx]
                pivotIdx < targetIdx -> left = pivotIdx + 1
                else -> right = pivotIdx - 1
            }
        }
        return -1
    }

    private fun partition(nums: IntArray, left: Int, right: Int): Int {
        val pivot = nums[right]
        var p = left
        for (i in left until right) {
            if (nums[i] <= pivot) {
                val temp = nums[p]
                nums[p] = nums[i]
                nums[i] = temp
                p++
            }
        }
        val temp = nums[p]
        nums[p] = nums[right]
        nums[right] = temp
        return p
    }
}""",
            'notes_kotlin': "In-place partitioning gives average O(N) runtime. Vulnerable to O(N^2) worst case on bad pivots.",
            'code_python': """class Solution:
    def findKthLargest(self, nums: List[int], k: int) -> int:
        target_idx = len(nums) - k
        left, right = 0, len(nums) - 1

        def partition(l, r):
            pivot = nums[r]
            p = l
            for i in range(l, r):
                if nums[i] <= pivot:
                    nums[p], nums[i] = nums[i], nums[p]
                    p += 1
            nums[p], nums[r] = nums[r], nums[p]
            return p

        while left <= right:
            p_idx = partition(left, right)
            if p_idx == target_idx:
                return nums[p_idx]
            elif p_idx < target_idx:
                left = p_idx + 1
            else:
                right = p_idx - 1
        return -1""",
            'notes_python': "Classic QuickSelect. Average O(N) time with O(1) extra space.",
            'code_java': """class Solution {
    public int findKthLargest(int[] nums, int k) {
        int targetIdx = nums.length - k;
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int pivotIdx = partition(nums, left, right);
            if (pivotIdx == targetIdx) {
                return nums[pivotIdx];
            } else if (pivotIdx < targetIdx) {
                left = pivotIdx + 1;
            } else {
                right = pivotIdx - 1;
            }
        }
        return -1;
    }

    private int partition(int[] nums, int left, int right) {
        int pivot = nums[right];
        int p = left;
        for (int i = left; i < right; i++) {
            if (nums[i] <= pivot) {
                int temp = nums[p];
                nums[p] = nums[i];
                nums[i] = temp;
                p++;
            }
        }
        int temp = nums[p];
        nums[p] = nums[right];
        nums[right] = temp;
        return p;
    }
}""",
            'notes_java': "Mutates input array in place. Degrades to O(N^2) on sorted arrays with fixed right pivot."
        },
        {
            'name': "Min-Heap of Size K (Optimal Primary)",
            'desc': "Maintain a Min-Heap of capacity k. Eject root when size > k. The root is the kth largest.",
            'time': "O(N log K)",
            'space': "O(K)",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Min-Heap of Size K
     * Time: O(N log K) - Each push/pop costs log K
     * Space: O(K) - Heap capacity strictly bounded by K
     */
    fun findKthLargest(nums: IntArray, k: Int): Int {
        val minHeap = java.util.PriorityQueue<Int>(k)

        for (num in nums) {
            minHeap.offer(num)
            if (minHeap.size > k) {
                minHeap.poll()
            }
        }

        return minHeap.peek()
    }
}""",
            'notes_kotlin': "Guaranteed O(N log K) worst-case time. Safe against pathological pivot patterns.",
            'code_python': """class Solution:
    # Optimal: Min-Heap of Size K
    # Time: O(N log K)
    # Space: O(K)
    def findKthLargest(self, nums: List[int], k: int) -> int:
        min_heap = []
        for num in nums:
            heapq.heappush(min_heap, num)
            if len(min_heap) > k:
                heapq.heappop(min_heap)
                
        return min_heap[0]""",
            'notes_python': "Using standard `heapq`. Zero custom comparators required.",
            'code_java': """class Solution {
    /**
     * Optimal: Min-Heap of Size K
     * Time: O(N log K) - Predictable log K per item
     * Space: O(K) - Constant heap memory bounded by K
     */
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }
}""",
            'notes_java': "Production standard. Seamlessly handles streaming input data without loading entire array."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun findKthLargest(nums: IntArray, k: Int): Int {
        val minHeap = java.util.PriorityQueue<Int>()
        for (n in nums) {
            minHeap.offer(n)
            if (minHeap.size > k) minHeap.poll()
        }
        return minHeap.peek()
    }
}""",
        'python_boilerplate': """import heapq

class Solution:
    def findKthLargest(self, nums: List[int], k: int) -> int:
        h = []
        for n in nums:
            heapq.heappush(h, n)
            if len(h) > k: heapq.heappop(h)
        return h[0]""",
        'java_boilerplate': """class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int n : nums) {
            pq.offer(n);
            if (pq.size() > k) pq.poll();
        }
        return pq.peek();
    }
}""",
        'js_test_runner': """
      const testCases = [
        { nums: [3,2,1,5,6,4], k: 2, expected: 5 },
        { nums: [3,2,3,1,2,4,5,5,6], k: 4, expected: 4 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runKth(arr, k) {
          let h = [];
          for (let n of arr) {
            h.push(n);
            h.sort((a, b) => a - b);
            if (h.length > k) h.shift();
          }
          return h[0];
        }

        const out = runKth(tc.nums, tc.k);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: nums=[${tc.nums.join(',')}] k=${tc.k}</span>
          <span>${passed ? '&#10004; PASSED (' + out + ')' : '&#10008; FAILED (got ' + out + ')'}</span>
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
        'phase1': "I clarify: Are duplicate numbers counted individually toward kth largest or distinct? The problem specifies kth largest in sorted order, not kth distinct. Can k be larger than array length? Constraints state 1 <= k <= nums.length.",
        'phase2': "For strategy: Sorting takes O(N log N). QuickSelect gives O(N) average time, but degrades to O(N^2) on bad pivots. A Min-Heap of capacity k provides guaranteed O(N log K) time and O(K) space. When size exceeds k, we poll the minimum. At the end, the heap contains the k largest elements, with the root being precisely the kth largest.",
        'phase3': "I implement `findKthLargest`: instantiate a `PriorityQueue<Int>` initialized with capacity k. Iterate over each number in `nums`: offer it into the heap, and if `heap.size > k`, poll the smallest element. Finally, return `heap.peek()`.",
        'phase4': "Dry run with [3,2,1,5,6,4] and k=2: 3 enters [3]. 2 enters [2,3]. 1 enters [1,2,3], size=3 > 2 so 1 is polled [2,3]. 5 enters [2,3,5], 2 is polled [3,5]. 6 enters [3,5,6], 3 is polled [5,6]. 4 enters [4,5,6], 4 is polled [5,6]. Heap peek returns 5. Correct.",
        'senior_followup': "Senior Scalability: If nums is a distributed dataset of 100 billion rows across 1,000 workers, each worker computes its local top-k using a local Min-Heap of size k, sends its k elements to a central aggregator (1,000 * k total elements), which runs a final Min-Heap of size k to extract the global kth largest."
    },
    'quiz': [
        {
            'question': "Why does a MIN-heap (rather than a max-heap) give the K-th LARGEST element when bounded to size K?",
            'options': [
                "Because retaining the K largest elements means the smallest of that winning set is precisely the K-th largest element overall.",
                "Because PriorityQueue in Java only supports min-heaps.",
                "Because max-heaps cannot process negative numbers.",
                "Because min-heaps consume less RAM."
            ],
            'correct_index': 0,
            'explanation': "The Min-Heap keeps the least of the top K winners at the root (`peek()`). Whenever a new element is larger than this root, we evict the old smallest winner to make room."
        },
        {
            'question': "What is the worst-case time complexity of QuickSelect without randomized pivot selection?",
            'options': [
                "O(N)",
                "O(N log N)",
                "O(N^2)",
                "O(K log N)"
            ],
            'correct_index': 2,
            'explanation': "If pivots partition the array into 1 and N-1 elements repeatedly (such as already-sorted inputs with fixed edge pivots), QuickSelect degrades to O(N^2)."
        }
    ]
}
