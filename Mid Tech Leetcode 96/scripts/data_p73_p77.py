import sys
sys.path.append('scripts')

p73 = {
    'num': 73,
    'lc': 236,
    'title': "Lowest Common Ancestor of a Binary Tree",
    'category': "Binary Trees & BST",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "lowest-common-ancestor-of-a-binary-tree",
    'verbatim_statement': """<p>Given a binary tree, find the lowest common ancestor (LCA) of two given nodes, <code>p</code> and <code>q</code>.</p>
<p>According to the <a href="https://en.wikipedia.org/wiki/Lowest_common_ancestor" target="_blank">definition of LCA on Wikipedia</a>: “The lowest common ancestor is defined between two nodes <code>p</code> and <code>q</code> as the lowest node in <code>T</code> that has both <code>p</code> and <code>q</code> as descendants (where we allow <b>a node to be a descendant of itself</b>).”</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1', 'output': '3\nExplanation: The LCA of nodes 5 and 1 is 3.'},
        {'title': "Example 2", 'input': 'root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4', 'output': '5\nExplanation: The LCA of nodes 5 and 4 is 5, since a node can be a descendant of itself according to the LCA definition.'}
    ],
    'constraints': [
        "The number of nodes in the tree is in the range [2, 10^5].",
        "-10^9 <= Node.val <= 10^9",
        "All Node.val are unique.",
        "p != q",
        "p and q will exist in the tree."
    ],
    'plain_english': "Perform a bottom-up post-order DFS. If the current node is null, or matches p or q, return the current node immediately. Recurse down left and right subtrees. If both the left and right recursive calls return non-null references, it means p and q are in separate subtrees, so the current node is their Lowest Common Ancestor! If only one side returns non-null, bubble up that non-null node.",
    'boundary_checks': {
        'min_base': "p is direct parent of q <code>p=5, q=4</code> &rarr; Returns 5 as soon as node 5 is reached.",
        'best_exit': "Root is p or q &rarr; Returns root immediately without scanning subtrees.",
        'worst_scan': "p and q at deepest leaf nodes in opposite subtrees &rarr; Full post-order DFS visits all N nodes."
    },
    'mental_model': {
        'analogy_title': "The River Confluence Search",
        'analogy_text': "Imagine two lost hikers p and q in a river delta system. Search boats travel up every tributary. When a boat finds p, it signals upstream 'Found p'. When a boat finds q, it signals 'Found q'. The first river fork where signals arrive simultaneously from two different tributaries is the confluence point—the Lowest Common Ancestor.",
        'aha_moment': "A node is the LCA if and only if: 1) One target is in its left subtree and the other is in its right subtree; OR 2) The node itself is p (or q) and the other target is in one of its subtrees. Both cases are unified into a single 4-line recursive post-order check.",
        'trigger': "Find lowest common ancestor in a general binary tree (without BST ordering) &rarr; Bottom-up Post-Order DFS returning matched target nodes.",
        'sisters': [
            {'name': "Lowest Common Ancestor of a BST", 'lc': "LC #235"},
            {'name': "Lowest Common Ancestor of Deepest Leaves", 'lc': "LC #1123"},
            {'name': "Step-By-Step Directions From a Binary Tree Node to Another", 'lc': "LC #2096"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N) Time, O(N) Space",
        'naive_text': "Find path from root to p, find path from root to q, store both paths in lists, then iterate to find the last common node.",
        'wasted_work': "Allocating extra path lists; requiring two separate top-down path traversals.",
        'optimal_complexity': "O(N) Time, O(Height) Auxiliary Space",
        'optimal_text': "Single-pass post-order traversal directly returning the LCA node reference without auxiliary path storage.",
        'core_invariant': "`dfs(node)` returns non-null if either p, q, or their LCA exists within `node`'s subtree.",
        'table_rows': [
            {'name': "1. Dual Path Lists", 'time': "O(N)", 'space': "O(N)", 'note': "Traces two paths from root, then finds divergence."},
            {'name': "★ 2. Bottom-Up Post-Order DFS", 'time': "O(N)", 'space': "O(H)", 'note': "Single-pass, zero heap allocations beyond recursion call stack."}
        ]
    },
    'trace_table': {
        'input_desc': 'root = [3, 5, 1, 6, 2, 0, 8], p = 5, q = 1',
        'columns': ["Call Frame", "Node", "Left Subtree Result", "Right Subtree Result", "Return Value", "Reasoning"],
        'rows': [
            ["dfs(6)", "6", "null", "null", "null", "Neither p nor q found"],
            ["dfs(2)", "2", "null", "null", "null", "Neither p nor q found"],
            ["dfs(5)", "5", "-", "-", "Node 5", "Matches target p=5; early return"],
            ["dfs(0)", "0", "null", "null", "null", "Neither p nor q found"],
            ["dfs(8)", "8", "null", "null", "null", "Neither p nor q found"],
            ["dfs(1)", "1", "-", "-", "Node 1", "Matches target q=1; early return"],
            ["dfs(3)", "3", "Node 5", "Node 1", "Node 3 (LCA)", "Both left and right are non-null &rarr; Node 3 is LCA!"]
        ],
        'takeaway': "At root 3, left returned Node 5 and right returned Node 1. Because both branches found a target, 3 is the Lowest Common Ancestor."
    },
    'stepper': {
        'title': "LCA Post-Order Confluence Stepper",
        'default_input': 'root=[3,5,1,6,2,0,8], p=5, q=1',
        'presets': [
            {'label': "Example 1: p=5, q=1 &rarr; LCA 3", 'onclick': 'loadPreset(3, 5, 1)'},
            {'label': "Example 2: p=5, q=4 &rarr; LCA 5", 'onclick': 'loadPreset(5, 5, 4)'}
        ],
        'metrics': [
            {'id': "metric-node", 'label': "Current Frame", 'color': "text-amber-400", 'initial': "dfs(3)"},
            {'id': "metric-left", 'label': "Left Result", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-right", 'label': "Right Result", 'color': "text-purple-400", 'initial': "-"},
            {'id': "metric-lca", 'label': "Returned Value", 'color': "text-emerald-400", 'initial': "-"}
        ],
        'visual_html': '<div id="p236-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace post-order bubble up.',
        'pseudocode_lines': [
            "fun lowestCommonAncestor(root, p, q): TreeNode? {",
            "    if (root == null || root == p || root == q) return root",
            "    val left = lowestCommonAncestor(root.left, p, q)",
            "    val right = lowestCommonAncestor(root.right, p, q)",
            "    if (left != null && right != null) return root",
            "    return left ?: right",
            "}"
        ],
        'js_code': """
    function buildSteps(ans, p, q) {
      steps = [];
      steps.push({ node: "dfs(3) [Root]", left: "pending", right: "pending", ret: "-", line: 1, narration: "Started DFS at root 3." });
      steps.push({ node: "dfs(5) [Left]", left: "-", right: "-", ret: "Node 5", line: 2, narration: "Node 5 matches target p=5! Returned Node 5 immediately." });
      steps.push({ node: "dfs(1) [Right]", left: "-", right: "-", ret: "Node 1", line: 2, narration: "Node 1 matches target q=1! Returned Node 1 immediately." });
      steps.push({ node: "dfs(3) [Confluence]", left: "Node 5", right: "Node 1", ret: "Node 3 (LCA)", line: 5, narration: "At root 3: left=Node 5, right=Node 1. Both non-null! Root 3 is the Lowest Common Ancestor." });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p236-stage');
      if (!stage) return;
      let html = '<div class="p-3 rounded-lg bg-slate-900 border border-slate-700 font-mono text-xs flex flex-col items-center gap-2">';
      html += `<div class="font-bold text-white text-sm">Inspecting: ${s.node}</div>`;
      html += `<div class="flex gap-4"><span class="text-cyan-300">Left: ${s.left}</span><span class="text-purple-300">Right: ${s.right}</span></div>`;
      html += `<div class="text-emerald-400 font-bold">Bubble-Up: ${s.ret}</div>`;
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-node').textContent = s.node;
      document.getElementById('metric-left').textContent = s.left;
      document.getElementById('metric-right').textContent = s.right;
      document.getElementById('metric-lca').textContent = s.ret;
    }

    function loadPreset(ans, p, q) {
      buildSteps(ans, p, q);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Bottom-Up Post-Order DFS (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(Height) Auxiliary Space",
            'summary': "Bottom-up recursion. If root matches p or q, bubble up root. If both left and right branches return non-null, root is LCA. Otherwise propagate whichever branch returned non-null.",
            'kotlin_code': """class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

class Solution {
    fun lowestCommonAncestor(root: TreeNode?, p: TreeNode?, q: TreeNode?): TreeNode? {
        if (root == null || root == p || root == q) return root

        val left = lowestCommonAncestor(root.left, p, q)
        val right = lowestCommonAncestor(root.right, p, q)

        return when {
            left != null && right != null -> root // Split point: this is the LCA
            left != null -> left                  // Both targets on left side
            else -> right                         // Both targets on right side
        }
    }
}""",
            'python_code': """class TreeNode:
    def __init__(self, x):
        self.val = x
        self.left = None
        self.right = None

class Solution:
    def lowestCommonAncestor(self, root: TreeNode | None, p: TreeNode, q: TreeNode) -> TreeNode | None:
        if not root or root == p or root == q:
            return root

        left = self.lowestCommonAncestor(root.left, p, q)
        right = self.lowestCommonAncestor(root.right, p, q)

        if left and right:
            return root
        return left if left else right""",
            'java_code': """class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left != null && right != null) return root;
        return (left != null) ? left : right;
    }
}""",
            'memory_breakdown': "O(H) recursion call stack where H is the height of the tree. Maximum O(N) worst-case skewed, O(log N) for balanced binary trees. Visits each node once."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class TreeNode(var `val`: Int) {
    var left: TreeNode? = null; var right: TreeNode? = null
}

class Solution {
    fun lowestCommonAncestor(root: TreeNode?, p: TreeNode?, q: TreeNode?): TreeNode? {
        // Write your optimal Post-Order LCA solution here
        if (root == null || root == p || root == q) return root
        val left = lowestCommonAncestor(root.left, p, q)
        val right = lowestCommonAncestor(root.right, p, q)
        if (left != null && right != null) return root
        return left ?: right
    }
}""",
        'python_boilerplate': """class TreeNode:
    def __init__(self, x):
        self.val = x; self.left = self.right = None

class Solution:
    def lowestCommonAncestor(self, root: TreeNode, p: TreeNode, q: TreeNode) -> TreeNode:
        # Write your optimal Post-Order LCA solution here
        if not root or root == p or root == q: return root
        left = self.lowestCommonAncestor(root.left, p, q)
        right = self.lowestCommonAncestor(root.right, p, q)
        if left and right: return root
        return left or right""",
        'java_boilerplate': """class TreeNode {
    int val; TreeNode left, right; TreeNode(int x) { val = x; }
}

class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Write your optimal Post-Order LCA solution here
        if (root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) return root;
        return left != null ? left : right;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function lca(nodes, p, q) {
        function dfs(idx) {
          if (idx >= nodes.length || nodes[idx] === null || nodes[idx] === undefined) return null;
          const val = nodes[idx];
          if (val === p || val === q) return val;
          const left = dfs(2 * idx + 1);
          const right = dfs(2 * idx + 2);
          if (left !== null && right !== null) return val;
          return left !== null ? left : right;
        }
        return dfs(0);
      }

      const tests = [
        { tree: [3,5,1,6,2,0,8], p: 5, q: 1, expected: 3 },
        { tree: [3,5,1,6,2,0,8], p: 5, q: 6, expected: 5 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = lca(t.tree, t.p, t.q);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: p=${t.p}, q=${t.q} &rarr; Got LCA=${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me confirm constraints: p and q are guaranteed to exist in the tree and are distinct. All node values are unique. The tree is a general binary tree, NOT a binary search tree, so we cannot rely on BST ordering.\"",
        'phase2': "\"We can solve this using bottom-up post-order DFS. At each node: if the node is null, or if it equals p, or if it equals q, we return the node immediately. Otherwise, we recursively search both left and right subtrees. If both the left and right calls return a non-null node, it means one target is on the left and the other is on the right, which makes the current node their Lowest Common Ancestor! If only one side returns non-null, we propagate that non-null result upward.\"",
        'phase3': "\"Let me write the code. `if (root == null || root == p || root == q) return root`. `val left = lowestCommonAncestor(root.left, p, q)`. `val right = lowestCommonAncestor(root.right, p, q)`. `if (left != null && right != null) return root`. `return left ?: right`. In just 6 lines, all recursive cases are handled elegantly.\"",
        'phase4': "\"Tracing with Example 1: `p=5, q=1`. `dfs(5)` returns 5. `dfs(1)` returns 1. At root 3, `left=5` and `right=1`. Both are non-null, so root 3 is returned. In Example 2: `p=5, q=4` (where 4 is inside 5's right child subtree). When `dfs(5)` is reached, it returns 5 immediately! The right subtree of root 3 returns null. Root 3 propagates `left` which is 5. Result is 5, correctly identifying that a node can be an ancestor of itself.\"",
        'senior_followup': "\"If many repeated LCA queries are made on the same static tree (e.g. in phylogenetic gene trees or Git commit graphs), we precompute node depths and 2^k binary lifting ancestor tables in O(N log N) preprocessing, answering each subsequent LCA query in O(log N) time.\""
    },
    'quiz': [
        {
            'question': "Why is an early return safe when `root == p || root == q`?",
            'options': [
                "If the other target is in root's subtree, root is its ancestor; otherwise, root is bubbled up to meet the other branch",
                "Binary tree search terminates immediately once any leaf node is visited",
                "The garbage collector optimizes recursive base cases by inlining registers",
                "All nodes below p or q are guaranteed to have identical hash codes"
            ],
            'correct_index': 0,
            'explanation': "If root matches p, and q is somewhere below p, then p is the LCA by definition. If q is NOT below p, bubbling p up allows the true ancestor higher in the tree to receive non-null from both branches."
        },
        {
            'question': "What is the time complexity of the bottom-up post-order traversal?",
            'options': [
                "O(N) because each node in the tree is visited at most once",
                "O(N log N) because of sorting overhead across tree branches",
                "O(Height^2) because each recursive frame allocates an array",
                "O(1) because pointer comparisons run in constant time"
            ],
            'correct_index': 0,
            'explanation': "The post-order DFS visits each node at most once, performing O(1) pointer checks per node, giving strict O(N) linear time."
        }
    ]
}

p74 = {
    'num': 74,
    'lc': 211,
    'title': "Design Add and Search Words Data Structure",
    'category': "Tries & String Parsing",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "design-add-and-search-words-data-structure",
    'verbatim_statement': """<p>Design a data structure that supports adding new words and finding if a string matches any previously added string.</p>
<p>Implement the <code>WordDictionary</code> class:</p>
<ul>
	<li><code>WordDictionary()</code> Initializes the object.</li>
	<li><code>void addWord(word)</code> Adds <code>word</code> to the data structure, it can be matched later.</li>
	<li><code>bool search(word)</code> Returns <code>true</code> if there is any string in the data structure that matches <code>word</code> or <code>false</code> otherwise. <code>word</code> may contain dots <code>'.'</code> where dots can be matched with any letter.</li>
</ul>""",
    'examples': [
        {'title': "Example 1", 'input': '["WordDictionary","addWord","addWord","addWord","search","search","search","search"]\n[[],["bad"],["dad"],["mad"],["pad"],["bad"],[".ad"],["b.."]]', 'output': '[null,null,null,null,false,true,true,true]\nExplanation:\nWordDictionary wordDictionary = new WordDictionary();\nwordDictionary.addWord("bad");\nwordDictionary.addWord("dad");\nwordDictionary.addWord("mad");\nwordDictionary.search("pad"); // return False\nwordDictionary.search("bad"); // return True\nwordDictionary.search(".ad"); // return True\nwordDictionary.search("b.."); // return True'}
    ],
    'constraints': [
        "1 <= word.length <= 25",
        "word in addWord consists of lowercase English letters.",
        "word in search consist of '.' or lowercase English letters.",
        "There will be at most 2 dots in word for search queries.",
        "At most 10^4 calls will be made to addWord and search."
    ],
    'plain_english': "Build a standard Trie (Prefix Tree) where each node contains an array of 26 children and an `isEnd` boolean flag. `addWord` is standard Trie insertion in O(L). For `search`, when matching an alphabetic character, traverse directly to `children[ch - 'a']`. When matching the wildcard character '.', branch recursively into all 26 non-null children; if any child subtree matches the remaining suffix, return true.",
    'boundary_checks': {
        'min_base': "Single character wildcard <code>search(\".\")</code> &rarr; Returns true if any 1-letter word exists.",
        'best_exit': "All wildcards <code>search(\"...\")</code> &rarr; Returns true if any 3-letter word exists.",
        'worst_scan': "Word with prefix match but isEnd=false &rarr; Returns false if word was only an intermediate prefix."
    },
    'mental_model': {
        'analogy_title': "The Library Card Catalog with Blank Tiles",
        'analogy_text': "Imagine a wooden card catalog organized like a tree of letters. Each drawer represents a letter. If a query has a specific letter like 'b', you only open drawer 'b'. But if a query has a blank wildcard tile '.', you don't know which drawer holds the answer—so you inspect all 26 drawers at that level. If any drawer's sub-catalog contains the rest of the word, your search succeeds.",
        'aha_moment': "Without wildcards, Trie search is purely deterministic O(L) down a single path. The wildcard '.' simply turns the search into a Depth-First Search branching across existing non-null children. Because there are at most 2 dots per query in constraints, the maximum branching is bounded.",
        'trigger': "Dictionary with wildcard character search ('.') &rarr; Trie with Recursive DFS Wildcard Branching.",
        'sisters': [
            {'name': "Implement Trie (Prefix Tree)", 'lc': "LC #208"},
            {'name': "Word Search II", 'lc': "LC #212"},
            {'name': "Wildcard Matching", 'lc': "LC #44"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N * L) Search",
        'naive_text': "Store words in an ArrayList. For each search, iterate over all N words, doing character-by-character regex matching.",
        'wasted_work': "Re-scanning every word in the dictionary on every search query ($10^4 \\times 10^4$ operations).",
        'optimal_complexity': "O(L) Add, O(26^Dots * L) Search",
        'optimal_text': "Trie prefix tree. Non-wildcard characters take O(1) per step; wildcards branch only to instantiated non-null children.",
        'core_invariant': "A path in the Trie matches query `word[i..]` if characters match and the terminal node has `isEnd == true`.",
        'table_rows': [
            {'name': "1. List Scan + Regex", 'time': "Add: O(1), Search: O(N * L)", 'space': "O(N * L)", 'note': "Severe timeout on 10^4 queries."},
            {'name': "★ 2. Trie + DFS Wildcard Branching", 'time': "Add: O(L), Search: O(26^K * L)", 'space': "O(Total Characters)", 'note': "Optimal; instant prefix termination."}
        ]
    },
    'trace_table': {
        'input_desc': 'Trie contains ["bad", "dad", "mad"]. Query search(".ad")',
        'columns': ["DFS Depth", "Char Pattern", "Trie Node", "Action Taken", "Outcome"],
        'rows': [
            ["0", "'.'", "root", "Wildcard '.' &rarr; Branch to all non-null children: 'b', 'd', 'm'", "Exploring branch 'b' first"],
            ["1", "'a'", "node 'b'", "Explicit 'a' &rarr; Traverse to children['a']", "Found node 'a' under 'b'"],
            ["2", "'d'", "node 'a'", "Explicit 'd' &rarr; Traverse to children['d']", "Found node 'd' with isEnd=true"],
            ["3", "end", "node 'd'", "Reached end of pattern, isEnd=true", "MATCH FOUND! Return true"]
        ],
        'takeaway': "The wildcard '.' successfully matched 'b' (from 'bad'), returning true on the first branch without needing to search 'd' or 'm'."
    },
    'stepper': {
        'title': "Trie Wildcard DFS Branching Stepper",
        'default_input': 'add("bad","dad","mad"), search(".ad")',
        'presets': [
            {'label': "Search .ad in [bad, dad, mad]", 'onclick': 'loadPreset(".ad")'},
            {'label': "Search b.. in [bad, dad, mad]", 'onclick': 'loadPreset("b..")'},
            {'label': "Search pad (Not Found)", 'onclick': 'loadPreset("pad")'}
        ],
        'metrics': [
            {'id': "metric-pattern", 'label': "Search Pattern", 'color': "text-amber-400", 'initial': ".ad"},
            {'id': "metric-char-idx", 'label': "Pattern Index", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-branch", 'label': "Active Branch", 'color': "text-purple-400", 'initial': "root"},
            {'id': "metric-res", 'label': "Match Result", 'color': "text-emerald-400", 'initial': "Searching..."}
        ],
        'visual_html': '<div id="p211-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace Trie wildcard matching.',
        'pseudocode_lines': [
            "fun search(word: String): Boolean = dfs(root, word, 0)",
            "fun dfs(node: TrieNode?, word: String, idx: Int): Boolean {",
            "    if (node == null) return false",
            "    if (idx == word.length) return node.isEnd",
            "    if (word[idx] == '.') {",
            "        for (child in node.children) if (child != null && dfs(child, word, idx + 1)) return true",
            "        return false",
            "    }",
            "    return dfs(node.children[word[idx] - 'a'], word, idx + 1)",
            "}"
        ],
        'js_code': """
    let rawQuery = ".ad";

    function buildSteps(query) {
      steps = [];
      steps.push({ pattern: query, idx: 0, branch: "root", res: "Searching...", line: 1, narration: `Initiating search for "${query}" against Trie containing ["bad", "dad", "mad"].` });

      if (query === ".ad") {
        steps.push({ pattern: query, idx: 0, branch: "root &rarr; ['.']", res: "Branching", line: 5, narration: "Index 0 is '.': Branching to all available children: 'b', 'd', 'm'." });
        steps.push({ pattern: query, idx: 1, branch: "root &rarr; 'b'", res: "Searching 'b'", line: 6, narration: "Testing branch 'b'. Character at index 1 is 'a'. Found child 'a'." });
        steps.push({ pattern: query, idx: 2, branch: "'b' &rarr; 'a' &rarr; 'd'", res: "MATCH!", line: 4, narration: "Character at index 2 is 'd'. Node 'd' has isEnd=true! Match found via word 'bad'." });
        steps.push({ pattern: query, idx: 3, branch: "Done", res: "TRUE", line: 4, narration: 'Search succeeded. Returning true.' });
      } else if (query === "pad") {
        steps.push({ pattern: query, idx: 0, branch: "root &rarr; 'p'", res: "Miss", line: 9, narration: "Index 0 is 'p'. Root has no child 'p'. Subtree is null." });
        steps.push({ pattern: query, idx: 0, branch: "Done", res: "FALSE", line: 3, narration: 'Search failed. Returning false.' });
      } else if (query === "b..") {
        steps.push({ pattern: query, idx: 0, branch: "root &rarr; 'b'", res: "Matched 'b'", line: 9, narration: "Index 0 is 'b'. Traversed to child 'b'." });
        steps.push({ pattern: query, idx: 1, branch: "'b' &rarr; ['.']", res: "Branching", line: 5, narration: "Index 1 is '.'. Branching to child 'a'." });
        steps.push({ pattern: query, idx: 2, branch: "'a' &rarr; ['.']", res: "Branching", line: 5, narration: "Index 2 is '.'. Branching to child 'd'. Terminal isEnd=true!" });
        steps.push({ pattern: query, idx: 3, branch: "Done", res: "TRUE", line: 4, narration: 'Matched "bad". Returning true.' });
      }
    }

    function renderVisual(s) {
      const stage = document.getElementById('p211-stage');
      if (!stage) return;
      let html = '<div class="p-3 rounded-lg bg-slate-900 border border-slate-700 font-mono text-xs flex flex-col items-center gap-2">';
      html += `<div class="font-bold text-amber-300">Active Query: "${s.pattern}"</div>`;
      html += `<div class="text-cyan-300">Traversal Path: ${s.branch}</div>`;
      html += `<div class="font-bold ${s.res === 'TRUE' ? 'text-emerald-400' : (s.res === 'FALSE' ? 'text-rose-400' : 'text-slate-200')}">${s.res}</div>`;
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-pattern').textContent = s.pattern;
      document.getElementById('metric-char-idx').textContent = s.idx;
      document.getElementById('metric-branch').textContent = s.branch;
      document.getElementById('metric-res').textContent = s.res;
    }

    function loadPreset(q) {
      rawQuery = q;
      buildSteps(rawQuery);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Trie with DFS Wildcard Branching (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "Add: O(L), Search: O(26^Dots * L)",
            'summary': "TrieNode stores array of 26 children. Explicit characters advance in O(1). Wildcard '.' iterates over non-null children, short-circuiting on the first successful match.",
            'kotlin_code': """class WordDictionary() {
    private class TrieNode {
        val children = arrayOfNulls<TrieNode>(26)
        var isEnd = false
    }

    private val root = TrieNode()

    fun addWord(word: String) {
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
        return dfs(root, word, 0)
    }

    private fun dfs(node: TrieNode?, word: String, idx: Int): Boolean {
        if (node == null) return false
        if (idx == word.length) return node.isEnd

        val ch = word[idx]
        return if (ch == '.') {
            for (child in node.children) {
                if (child != null && dfs(child, word, idx + 1)) {
                    return true
                }
            }
            false
        } else {
            dfs(node.children[ch - 'a'], word, idx + 1)
        }
    }
}""",
            'python_code': """class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False

class WordDictionary:
    def __init__(self):
        self.root = TrieNode()

    def addWord(self, word: str) -> None:
        curr = self.root
        for ch in word:
            if ch not in curr.children:
                curr.children[ch] = TrieNode()
            curr = curr.children[ch]
        curr.is_end = True

    def search(self, word: str) -> bool:
        def dfs(node: TrieNode, idx: int) -> bool:
            if idx == len(word):
                return node.is_end

            ch = word[idx]
            if ch == '.':
                for child in node.children.values():
                    if dfs(child, idx + 1):
                        return True
                return False
            else:
                if ch not in node.children:
                    return False
                return dfs(node.children[ch], idx + 1)

        return dfs(self.root, 0)""",
            'java_code': """class WordDictionary {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public WordDictionary() {
        root = new TrieNode();
    }

    public void addWord(String word) {
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
        return dfs(root, word, 0);
    }

    private boolean dfs(TrieNode node, String word, int idx) {
        if (node == null) return false;
        if (idx == word.length()) return node.isEnd;

        char ch = word.charAt(idx);
        if (ch == '.') {
            for (TrieNode child : node.children) {
                if (child != null && dfs(child, word, idx + 1)) {
                    return true;
                }
            }
            return false;
        } else {
            return dfs(node.children[ch - 'a'], word, idx + 1);
        }
    }
}""",
            'memory_breakdown': "O(Total characters inserted) space across Trie nodes. Search is O(L) for exact words and bounded by O(26^2 * L) for at most 2 wildcards."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class WordDictionary() {
    // Write your optimal Trie + Wildcard DFS here
    private class TrieNode {
        val children = arrayOfNulls<TrieNode>(26)
        var isEnd = false
    }
    private val root = TrieNode()

    fun addWord(word: String) {
        var curr = root
        for (ch in word) {
            val i = ch - 'a'
            if (curr.children[i] == null) curr.children[i] = TrieNode()
            curr = curr.children[i]!!
        }
        curr.isEnd = true
    }

    fun search(word: String): Boolean = dfs(root, word, 0)

    private fun dfs(node: TrieNode?, word: String, i: Int): Boolean {
        if (node == null) return false
        if (i == word.length) return node.isEnd
        val ch = word[i]
        if (ch == '.') {
            for (c in node.children) if (c != null && dfs(c, word, i + 1)) return true
            return false
        }
        return dfs(node.children[ch - 'a'], word, i + 1)
    }
}""",
        'python_boilerplate': """class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False

class WordDictionary:
    # Write your optimal Trie + Wildcard DFS here
    def __init__(self):
        self.root = TrieNode()

    def addWord(self, word: str) -> None:
        curr = self.root
        for ch in word:
            if ch not in curr.children: curr.children[ch] = TrieNode()
            curr = curr.children[ch]
        curr.is_end = True

    def search(self, word: str) -> bool:
        def dfs(node, i):
            if i == len(word): return node.is_end
            ch = word[i]
            if ch == '.':
                return any(dfs(c, i + 1) for c in node.children.values())
            if ch not in node.children: return False
            return dfs(node.children[ch], i + 1)
        return dfs(self.root, 0)""",
        'java_boilerplate': """class WordDictionary {
    // Write your optimal Trie + Wildcard DFS here
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }
    TrieNode root = new TrieNode();

    public void addWord(String word) {
        TrieNode curr = root;
        for (int i = 0; i < word.length(); i++) {
            int idx = word.charAt(i) - 'a';
            if (curr.children[idx] == null) curr.children[idx] = new TrieNode();
            curr = curr.children[idx];
        }
        curr.isEnd = true;
    }

    public boolean search(String word) {
        return dfs(root, word, 0);
    }

    private boolean dfs(TrieNode node, String word, int idx) {
        if (node == null) return false;
        if (idx == word.length()) return node.isEnd;
        char ch = word.charAt(idx);
        if (ch == '.') {
            for (TrieNode child : node.children) {
                if (child != null && dfs(child, word, idx + 1)) return true;
            }
            return false;
        }
        return dfs(node.children[ch - 'a'], word, idx + 1);
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      class TrieNode { constructor() { this.children = {}; this.isEnd = false; } }
      class WordDictionary {
        constructor() { this.root = new TrieNode(); }
        addWord(word) {
          let curr = this.root;
          for (let ch of word) {
            if (!curr.children[ch]) curr.children[ch] = new TrieNode();
            curr = curr.children[ch];
          }
          curr.isEnd = true;
        }
        search(word) {
          function dfs(node, idx) {
            if (idx === word.length) return node.isEnd;
            const ch = word[idx];
            if (ch === '.') {
              for (const key of Object.keys(node.children)) {
                if (dfs(node.children[key], idx + 1)) return true;
              }
              return false;
            } else {
              if (!node.children[ch]) return false;
              return dfs(node.children[ch], idx + 1);
            }
          }
          return dfs(this.root, 0);
        }
      }

      const wd = new WordDictionary();
      wd.addWord("bad");
      wd.addWord("dad");
      wd.addWord("mad");
      const r1 = wd.search("pad");
      const r2 = wd.search("bad");
      const r3 = wd.search(".ad");
      const r4 = wd.search("b..");

      const pass = r1 === false && r2 === true && r3 === true && r4 === true;
      const row = document.createElement('div');
      row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
      row.textContent = `Test: search("pad")=${r1}, search("bad")=${r2}, search(".ad")=${r3}, search("b..")=${r4} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: words consist of lowercase English letters 'a' through 'z', and search queries can contain up to 2 dot '.' wildcard characters. Word length is up to 25. Up to 10,000 operations.\"",
        'phase2': "\"For prefix-based dictionary queries, a Trie is the standard data structure. Each TrieNode has an array of 26 children references and an `isEnd` boolean. Adding a word is O(L) by walking down the Trie, creating nodes as needed. For `search(word)`, if the character is an explicit letter, we simply step to `children[ch - 'a']`. If the character is '.', it can match any letter, so we branch recursively into all 26 non-null children. If any child DFS returns true, the entire search returns true. Because queries contain at most 2 dots, the branching factor is strictly bounded.\"",
        'phase3': "\"Let me write the code. I define `TrieNode` with `children = arrayOfNulls<TrieNode>(26)` and `isEnd = false`. In `addWord(word)`, traverse down, allocate if null, set `isEnd = true` at the last node. Helper `dfs(node, word, idx)`: if node is null, return false; if `idx == word.length`, return `node.isEnd`. If `word[idx] == '.'`, loop over `node.children`: if any child is non-null and `dfs(child, word, idx + 1)` is true, return true; else return false. For normal letters, return `dfs(node.children[ch - 'a'], word, idx + 1)`.\"",
        'phase4': "\"Tracing with `[\"bad\", \"dad\", \"mad\"]`. `search(\"pad\")` checks root's child for 'p', which is null &rarr; returns false. `search(\"bad\")` walks root &rarr; 'b' &rarr; 'a' &rarr; 'd' (isEnd=true) &rarr; returns true. `search(\".ad\")` at index 0 sees '.', branches into 'b', 'd', 'm'. Branch 'b' matches 'a' &rarr; 'd' with isEnd=true, immediately short-circuiting to return true. `search(\"b..\")` matches 'bad', returns true. Handled with optimal DFS short-circuiting.\"",
        'senior_followup': "\"In full-text search systems (like Elasticsearch or Lucene), regex and wildcard queries on inverted indexes use Finite State Transducers (FSTs) that intersect a wildcard automaton with the dictionary graph in sub-millisecond latency.\""
    },
    'quiz': [
        {
            'question': "What is the worst-case time complexity of searching a word with K wildcard dots?",
            'options': [
                "O(26^K * L) where L is the length of the word",
                "O(N * 26) where N is the total number of words in the dictionary",
                "O(L^2) because each dot increments the quadratic recursion stack",
                "O(1) because Trie node lookups are cached in CPU registers"
            ],
            'correct_index': 0,
            'explanation': "Each wildcard dot can branch into up to 26 children. For K dots, there are at most 26^K paths explored of length L, giving O(26^K * L)."
        },
        {
            'question': "Why is an array `children[26]` preferred over a HashMap for lowercase English letters?",
            'options': [
                "Array indexing `ch - 'a'` is faster than hashing with zero collision overhead",
                "HashMaps cannot store recursive TrieNode child references in Java",
                "Arrays automatically compress strings using Huffman encoding",
                "HashMaps require 64-bit integer keys which are forbidden for characters"
            ],
            'correct_index': 0,
            'explanation': "For a fixed 26-character alphabet, a direct 26-element array avoids hash calculation, bucket chaining, and boxing overhead, providing superior cache locality and speed."
        }
    ]
}

p75 = {
    'num': 75,
    'lc': 253,
    'title': "Meeting Rooms II",
    'category': "Heaps & Priority Queues",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "meeting-rooms-ii",
    'verbatim_statement': """<p>Given an array of meeting time intervals <code>intervals</code> where <code>intervals[i] = [start_i, end_i]</code>, return <em>the minimum number of conference rooms required</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'intervals = [[0,30],[5,10],[15,20]]', 'output': '2\nExplanation:\nRoom 1: [0,30]\nRoom 2: [5,10], [15,20]'},
        {'title': "Example 2", 'input': 'intervals = [[7,10],[2,4]]', 'output': '1'}
    ],
    'constraints': [
        "1 <= intervals.length <= 10^4",
        "0 <= start_i < end_i <= 10^6"
    ],
    'plain_english': "Sort all meetings by their start time. Maintain a Min-Heap of meeting end times representing the rooms currently in use. For each meeting, check the earliest ending room (`heap.peek()`). If the new meeting starts after or at the moment that meeting ends (`start >= heap.peek()`), that room is now free, so we reuse it by popping the old end time. Then we push the new meeting's end time. The maximum size of the heap (or the final heap size) is the minimum conference rooms required.",
    'boundary_checks': {
        'min_base': "Single meeting <code>intervals = [[1, 5]]</code> &rarr; Returns 1.",
        'best_exit': "All meetings strictly disjoint <code>[[1, 2], [3, 4], [5, 6]]</code> &rarr; Reuses same room; returns 1.",
        'worst_scan': "All meetings completely overlapping <code>[[1, 10], [2, 10], [3, 10]]</code> &rarr; Requires N rooms."
    },
    'mental_model': {
        'analogy_title': "The Airport Gate Assignment Desk",
        'analogy_text': "Imagine assigning airport gates to incoming flights. You sort all flights by their arrival time. You keep a scoreboard of the departure times for all occupied gates. When a new flight lands, you look at the earliest departing plane on your scoreboard. If that plane has already left, you clear that gate and assign the incoming flight to it. If not, all gates are occupied, so airport operations must open a brand new gate.",
        'aha_moment': "We only need to track the EARLIEST finishing meeting room! If the meeting that finishes earliest isn't free yet, NO room is free, forcing us to allocate a new room. A Min-Heap gives instant O(1) access to the earliest finishing room.",
        'trigger': "Count maximum concurrent overlapping intervals / minimum resources required &rarr; Sort by start time + Min-Heap of end times (or Chronological Two-Pointer Event Scan).",
        'sisters': [
            {'name': "Meeting Rooms (Can attend all?)", 'lc': "LC #252"},
            {'name': "Merge Intervals", 'lc': "LC #56"},
            {'name': "Car Pooling", 'lc': "LC #1094"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(N) Space",
        'naive_text': "For each meeting, compare with all other meetings to count how many overlap with its start time.",
        'wasted_work': "Re-scanning every interval pair pairwise ($10^8$ checks for N=10,000).",
        'optimal_complexity': "O(N log N) Time, O(N) Space",
        'optimal_text': "Sort intervals in O(N log N). Each meeting pushed and popped from Min-Heap at most once in O(log N).",
        'core_invariant': "At any instant after processing a meeting, `minHeap.size()` equals the exact number of conference rooms currently active.",
        'table_rows': [
            {'name': "1. Pairwise Overlap Matrix", 'time': "O(N^2)", 'space': "O(N)", 'note': "Quadratic pairwise comparison."},
            {'name': "2. Chronological Sweep-Line", 'time': "O(N log N)", 'space': "O(N)", 'note': "Sort start and end arrays separately; two pointers."},
            {'name': "★ 3. Min-Heap of End Times", 'time': "O(N log N)", 'space': "O(N)", 'note': "Production standard; models real-world resource pools."}
        ]
    },
    'trace_table': {
        'input_desc': 'intervals = [[0, 30], [5, 10], [15, 20]] (Already sorted by start)',
        'columns': ["Meeting [start, end]", "Earliest Free Room (heap.peek())", "Is Room Reused? (start >= peek)", "Min-Heap State After", "Total Rooms in Use"],
        'rows': [
            ["[0, 30]", "Empty", "No (first meeting)", "[30]", "1 room active"],
            ["[5, 10]", "30", "No (5 < 30)", "[10, 30]", "2 rooms active (allocated Room 2)"],
            ["[15, 20]", "10", "YES! (15 >= 10, Room 2 free)", "Pop 10, Push 20 &rarr; [20, 30]", "2 rooms active (reused Room 2)"]
        ],
        'takeaway': "Meeting [15, 20] reused the room vacated by [5, 10] at time 10. Maximum simultaneous rooms = 2."
    },
    'stepper': {
        'title': "Meeting Rooms Min-Heap Allocation Stepper",
        'default_input': '[[0,30],[5,10],[15,20]]',
        'presets': [
            {'label': "Example 1: [[0,30],[5,10],[15,20]]", 'onclick': 'loadPreset([[0,30],[5,10],[15,20]])'},
            {'label': "No Overlap: [[2,4],[7,10]]", 'onclick': 'loadPreset([[2,4],[7,10]])'},
            {'label': "All Overlap: [[1,10],[2,10],[3,10]]", 'onclick': 'loadPreset([[1,10],[2,10],[3,10]])'}
        ],
        'metrics': [
            {'id': "metric-meeting", 'label': "Current Meeting", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-earliest-end", 'label': "Earliest Room Free", 'color': "text-cyan-400", 'initial': "none"},
            {'id': "metric-action", 'label': "Room Action", 'color': "text-purple-400", 'initial': "Idle"},
            {'id': "metric-rooms", 'label': "Rooms Required", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p253-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace meeting room allocation.',
        'pseudocode_lines': [
            "intervals.sortBy { it[0] }",
            "val minHeap = PriorityQueue<Int>()",
            "for (m in intervals) {",
            "    if (minHeap.isNotEmpty() && m[0] >= minHeap.peek()) minHeap.poll()",
            "    minHeap.offer(m[1])",
            "}",
            "return minHeap.size"
        ],
        'js_code': """
    let rawIntervals = [[0, 30], [5, 10], [15, 20]];

    function buildSteps(intervals) {
      steps = [];
      const sorted = [...intervals].sort((a,b)=>a[0]-b[0]);
      const heap = []; // min-heap simulation

      steps.push({
        meeting: "-", earliest: "none", action: "Sorted input", rooms: 0,
        heap: [], sorted: [...sorted], line: 1,
        narration: `Sorted ${sorted.length} meetings by start time: ${sorted.map(m=>'['+m[0]+','+m[1]+']').join(', ')}.`
      });

      for (let i = 0; i < sorted.length; i++) {
        const m = sorted[i];
        let action = "";
        const earliest = heap.length > 0 ? heap[0] : null;

        if (earliest !== null && m[0] >= earliest) {
          heap.shift(); // poll min
          action = `Reused room (freed at ${earliest})`;
        } else {
          action = `Allocated NEW room (earliest free is ${earliest !== null ? earliest : 'none'})`;
        }
        heap.push(m[1]);
        heap.sort((a,b)=>a-b);

        steps.push({
          meeting: `[${m[0]}, ${m[1]}]`, earliest: earliest !== null ? `${earliest}` : "none", action: action, rooms: heap.length,
          heap: [...heap], sorted: [...sorted], line: 4,
          narration: `Meeting [${m[0]}, ${m[1]}]: ${action}. Active rooms in use = ${heap.length}.`
        });
      }

      steps.push({
        meeting: "All Done", earliest: "-", action: "Complete", rooms: heap.length,
        heap: [...heap], sorted: [...sorted], line: 7,
        narration: `Evaluation complete. Minimum conference rooms needed = ${heap.length}.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p253-stage');
      if (!stage) return;
      let html = '<div class="flex flex-col items-center gap-3 w-full max-w-xl">';

      // Heap of rooms
      html += '<div class="flex items-center gap-2"><span class="text-xs font-mono text-slate-400">Active Rooms:</span>';
      if (s.heap.length === 0) {
        html += '<span class="text-xs font-mono text-slate-500 italic">No rooms allocated</span>';
      } else {
        s.heap.forEach((end, idx) => {
          const isEarliest = idx === 0;
          html += `<div class="p-2.5 rounded-lg border font-mono text-xs flex flex-col items-center ${isEarliest ? 'bg-cyan-500/20 border-cyan-400 text-cyan-200 ring-2 ring-cyan-500' : 'bg-slate-900 border-slate-700 text-slate-300'}"><span class="font-bold">Room ${idx + 1}</span><span class="text-[10px] text-slate-400">Free at ${end}</span></div>`;
        });
      }
      html += '</div>';

      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-meeting').textContent = s.meeting;
      document.getElementById('metric-earliest-end').textContent = s.earliest;
      document.getElementById('metric-action').textContent = s.action;
      document.getElementById('metric-rooms').textContent = s.rooms;
    }

    function loadPreset(arr) {
      rawIntervals = arr;
      buildSteps(rawIntervals);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "Approach 1: Two Pointers / Chronological Sweep-Line",
            'badge': "Alternative",
            'color': "cyan",
            'complexity': "O(N log N) Time, O(N) Space",
            'summary': "Extract all start times and end times into separate arrays. Sort both. Use two pointers to track active meetings without a heap.",
            'kotlin_code': """class Solution {
    fun minMeetingRooms(intervals: Array<IntArray>): Int {
        val n = intervals.size
        val starts = IntArray(n) { intervals[it][0] }.sorted()
        val ends = IntArray(n) { intervals[it][1] }.sorted()

        var rooms = 0
        var endPtr = 0

        for (i in 0 until n) {
            if (starts[i] < ends[endPtr]) {
                rooms++ // Need a new room
            } else {
                endPtr++ // Room freed up, reuse it
            }
        }
        return rooms
    }
}""",
            'python_code': """class Solution:
    def minMeetingRooms(self, intervals: list[list[int]]) -> int:
        starts = sorted([i[0] for i in intervals])
        ends = sorted([i[1] for i in intervals])

        rooms = 0
        end_ptr = 0

        for start in starts:
            if start < ends[end_ptr]:
                rooms += 1
            else:
                end_ptr += 1

        return rooms""",
            'java_code': """import java.util.Arrays;

class Solution {
    public int minMeetingRooms(int[][] intervals) {
        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0;
        int endPtr = 0;

        for (int i = 0; i < n; i++) {
            if (starts[i] < ends[endPtr]) {
                rooms++;
            } else {
                endPtr++;
            }
        }
        return rooms;
    }
}""",
            'memory_breakdown': "O(N) memory for start and end time arrays. O(N log N) time for sorting. Avoids heap operations."
        },
        {
            'name': "★ Approach 2: Sort + Min-Heap of End Times (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N log N) Time, O(N) Space",
            'summary': "Sort meetings by start time. Maintain a PriorityQueue of active room end times. If meeting starts >= heap.peek(), poll old end time. Offer new end time.",
            'kotlin_code': """import java.util.PriorityQueue

class Solution {
    fun minMeetingRooms(intervals: Array<IntArray>): Int {
        if (intervals.isEmpty()) return 0
        intervals.sortBy { it[0] }

        val minHeap = PriorityQueue<Int>() // stores room end times

        for (m in intervals) {
            if (minHeap.isNotEmpty() && m[0] >= minHeap.peek()) {
                minHeap.poll() // reuse room
            }
            minHeap.offer(m[1])
        }
        return minHeap.size
    }
}""",
            'python_code': """import heapq

class Solution:
    def minMeetingRooms(self, intervals: list[list[int]]) -> int:
        if not intervals:
            return 0

        intervals.sort(key=lambda x: x[0])
        min_heap = []  # stores end times

        for start, end in intervals:
            if min_heap and start >= min_heap[0]:
                heapq.heappop(min_heap)
            heapq.heappush(min_heap, end)

        return len(min_heap)""",
            'java_code': """import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) return 0;

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int[] m : intervals) {
            if (!minHeap.isEmpty() && m[0] >= minHeap.peek()) {
                minHeap.poll(); // reuse room
            }
            minHeap.offer(m[1]);
        }
        return minHeap.size();
    }
}""",
            'memory_breakdown': "O(N) space for priority queue holding at most N end times. O(N log N) runtime dominated by initial sort and N heap insertions."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """import java.util.PriorityQueue

class Solution {
    fun minMeetingRooms(intervals: Array<IntArray>): Int {
        // Write your optimal Min-Heap solution here
        if (intervals.isEmpty()) return 0
        intervals.sortBy { it[0] }
        val heap = PriorityQueue<Int>()
        for (m in intervals) {
            if (heap.isNotEmpty() && m[0] >= heap.peek()) heap.poll()
            heap.offer(m[1])
        }
        return heap.size
    }
}""",
        'python_boilerplate': """import heapq

class Solution:
    def minMeetingRooms(self, intervals: list[list[int]]) -> int:
        # Write your optimal Min-Heap solution here
        if not intervals: return 0
        intervals.sort(key=lambda x: x[0])
        heap = []
        for start, end in intervals:
            if heap and start >= heap[0]: heapq.heappop(heap)
            heapq.heappush(heap, end)
        return len(heap)""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public int minMeetingRooms(int[][] intervals) {
        // Write your optimal Min-Heap solution here
        if (intervals.length == 0) return 0;
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int[] m : intervals) {
            if (!heap.isEmpty() && m[0] >= heap.peek()) heap.poll();
            heap.offer(m[1]);
        }
        return heap.size();
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(intervals) {
        if (!intervals.length) return 0;
        intervals.sort((a, b) => a[0] - b[0]);
        const heap = [];
        for (let i = 0; i < intervals.length; i++) {
          if (heap.length > 0 && intervals[i][0] >= heap[0]) {
            heap.shift();
          }
          heap.push(intervals[i][1]);
          heap.sort((a, b) => a - b);
        }
        return heap.length;
      }

      const tests = [
        { intervals: [[0,30],[5,10],[15,20]], expected: 2 },
        { intervals: [[7,10],[2,4]], expected: 1 },
        { intervals: [[1,5]], expected: 1 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.intervals);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: intervals=${JSON.stringify(t.intervals)} &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me confirm constraints: intervals are given as `[start, end]`. Start is strictly less than end. Meetings that end at time T release their room at time T, so a meeting starting at time T can immediately reuse that room without conflict.\"",
        'phase2': "\"To find the minimum number of rooms, we must find the maximum number of concurrent overlapping meetings. We first sort the meetings by start time so we process them in chronological order. To manage active rooms, we use a Min-Heap storing the end times of ongoing meetings. When considering a meeting `[start, end]`, we check `minHeap.peek()`—the earliest time an ongoing meeting finishes. If `start >= minHeap.peek()`, that room has been vacated! We poll the old end time and reuse the room by offering the new `end`. If `start < minHeap.peek()`, all currently active rooms are occupied, so we must allocate a new room by offering `end` without polling. The size of the heap after processing all meetings is the minimum rooms required.\"",
        'phase3': "\"Let me write the code. If intervals is empty, return 0. Sort intervals by start time. Create `val minHeap = PriorityQueue<Int>()`. For each meeting `m`: `if (minHeap.isNotEmpty() && m[0] >= minHeap.peek()) minHeap.poll()`. Then `minHeap.offer(m[1])`. Finally, return `minHeap.size`.\"",
        'phase4': "\"Tracing with `[[0, 30], [5, 10], [15, 20]]`. Sorted: `[0, 30]`, `[5, 10]`, `[15, 20]`. At `[0, 30]`: heap gets 30. At `[5, 10]`: 5 < 30 &rarr; allocate room 2, heap has `[10, 30]`. At `[15, 20]`: 15 >= 10 &rarr; room 2 is free! Poll 10, offer 20. Heap has `[20, 30]`. Total heap size is 2. Handled cleanly.\"",
        'senior_followup': "\"In large-scale cloud job schedulers (like Kubernetes kube-scheduler), this interval partitioning pattern is applied across multi-dimensional resource vectors (CPU cores, RAM, GPU) using multidimensional bin-packing heuristics.\""
    },
    'quiz': [
        {
            'question': "Why is a Min-Heap used to track room end times instead of a Max-Heap?",
            'options': [
                "We need to know the EARLIEST room that becomes free to see if any room can be reused",
                "Max-Heap does not support integer deletion in standard Java Collections Framework",
                "A Min-Heap guarantees that meeting start times are sorted in descending order",
                "CPU cache controllers optimize min-heap comparisons using single-instruction SIMD"
            ],
            'correct_index': 0,
            'explanation': "If the meeting with the earliest ending time is still in progress, then ALL rooms are currently occupied, and a new room must be allocated. Thus, checking `minHeap.peek()` is the only check needed."
        },
        {
            'question': "If meeting A ends at 10 and meeting B starts at 10, can they share the same room?",
            'options': [
                "Yes, because start >= end allows immediate reuse of the room at the transition boundary",
                "No, there must be at least a 1-minute buffer between meetings on all schedules",
                "Only if the meeting durations are strictly equal to 30 minutes",
                "Only if both meetings are scheduled on the same calendar weekday"
            ],
            'correct_index': 0,
            'explanation': "The problem specification allows a meeting to start at the exact same moment another ends (`start >= peek()`)."
        }
    ]
}

p76 = {
    'num': 76,
    'lc': 435,
    'title': "Non-overlapping Intervals",
    'category': "Intervals & Greedy",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "non-overlapping-intervals",
    'verbatim_statement': """<p>Given an array of intervals <code>intervals</code> where <code>intervals[i] = [start_i, end_i]</code>, return <em>the minimum number of intervals you need to remove to make the rest of the intervals non-overlapping</em>.</p>
<p><strong>Note:</strong> Intervals that touch at a point like <code>[1, 2]</code> and <code>[2, 3]</code> are non-overlapping.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'intervals = [[1,2],[2,3],[3,4],[1,3]]', 'output': '1\nExplanation: [1,3] can be removed and the rest of the intervals are non-overlapping.'},
        {'title': "Example 2", 'input': 'intervals = [[1,2],[1,2],[1,2]]', 'output': '2\nExplanation: You need to remove two [1,2] to make the rest of the intervals non-overlapping.'},
        {'title': "Example 3", 'input': 'intervals = [[1,2],[2,3]]', 'output': '0\nExplanation: You don\'t need to remove any of the intervals since they\'re already non-overlapping.'}
    ],
    'constraints': [
        "1 <= intervals.length <= 10^5",
        "intervals[i].length == 2",
        "-5 * 10^4 <= start_i < end_i <= 5 * 10^4"
    ],
    'plain_english': "Minimizing the number of intervals to remove is equivalent to maximizing the number of compatible non-overlapping intervals you keep! The classic greedy strategy (Interval Scheduling) is to sort intervals by their END time ascending. Always keep the interval that finishes earliest, because it leaves the maximum possible remaining time for subsequent intervals. If the next interval starts before the last kept interval ends, an overlap occurs and it must be removed.",
    'boundary_checks': {
        'min_base': "Single interval <code>intervals = [[1, 2]]</code> &rarr; Returns 0 removals.",
        'best_exit': "All already non-overlapping <code>[[1, 2], [2, 3], [3, 4]]</code> &rarr; Returns 0.",
        'worst_scan': "All identical intervals <code>[[1, 2], [1, 2], [1, 2]]</code> &rarr; Must remove N - 1 = 2."
    },
    'mental_model': {
        'analogy_title': "The Movie Marathon Theater Hopper",
        'analogy_text': "Imagine wanting to watch as many movies at a film festival as possible. When choosing your next movie, you should always pick the one that ENDS the earliest. Finishing a movie early frees you up to catch other movies starting later. A movie that drags on until midnight prevents you from seeing 3 other films. Greedy end-time selection maximizes films watched and minimizes skipped films.",
        'aha_moment': "Sorting by start time does NOT work (e.g. `[1, 100]` starts early but blocks everything). Sorting by END time guarantees that every greedy choice preserves the maximal remaining runway.",
        'trigger': "Remove minimum intervals to make non-overlapping &rarr; Greedy Interval Scheduling: Sort by END time ascending.",
        'sisters': [
            {'name': "Minimum Number of Arrows to Burst Balloons", 'lc': "LC #452"},
            {'name': "Meeting Rooms II", 'lc': "LC #253"},
            {'name': "Merge Intervals", 'lc': "LC #56"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(2^N) Time, O(N) Space",
        'naive_text': "Brute-force recursion trying all subsets of intervals and checking which valid subset has the maximum size.",
        'wasted_work': "Exploring exponential subsets when the greedy choice property guarantees optimal substructure.",
        'optimal_complexity': "O(N log N) Time, O(1) Space",
        'optimal_text': "Sort intervals by end coordinate in O(N log N). Single linear pass counting removals.",
        'core_invariant': "At each step, `lastEnd` is the minimum possible finishing time of all valid subsets of compatible intervals chosen so far.",
        'table_rows': [
            {'name': "1. Brute Force Subsets", 'time': "O(2^N)", 'space': "O(N)", 'note': "Exponential; times out on N > 20."},
            {'name': "2. Dynamic Programming (LIS-style)", 'time': "O(N^2)", 'space': "O(N)", 'note': "Quadratic DP on sorted intervals."},
            {'name': "★ 3. Greedy End-Time Sort", 'time': "O(N log N)", 'space': "O(1) Auxiliary", 'note': "Optimal; mathematically proven greedy choice property."}
        ]
    },
    'trace_table': {
        'input_desc': 'intervals = [[1, 2], [2, 3], [3, 4], [1, 3]] &rarr; Sorted by end: [[1, 2], [2, 3], [1, 3], [3, 4]]',
        'columns': ["Interval [start, end]", "lastEnd", "Check (start >= lastEnd)", "Action Taken", "Removals Count", "New lastEnd"],
        'rows': [
            ["[1, 2]", "-inf", "1 >= -inf (Yes)", "Keep [1, 2]", "0", "2"],
            ["[2, 3]", "2", "2 >= 2 (Yes, touch allowed)", "Keep [2, 3]", "0", "3"],
            ["[1, 3]", "3", "1 >= 3 (NO, overlaps!)", "Remove [1, 3]", "1", "3 (unchanged)"],
            ["[3, 4]", "3", "3 >= 3 (Yes, touch allowed)", "Keep [3, 4]", "1", "4"]
        ],
        'takeaway': "Total removals = 1 (interval [1, 3] removed). 3 intervals kept: [1,2], [2,3], [3,4]."
    },
    'stepper': {
        'title': "Greedy Earliest-End Interval Stepper",
        'default_input': '[[1,2],[2,3],[3,4],[1,3]]',
        'presets': [
            {'label': "Example 1: [[1,2],[2,3],[3,4],[1,3]]", 'onclick': 'loadPreset([[1,2],[2,3],[3,4],[1,3]])'},
            {'label': "Identical: [[1,2],[1,2],[1,2]]", 'onclick': 'loadPreset([[1,2],[1,2],[1,2]])'},
            {'label': "Disjoint: [[1,2],[2,3]]", 'onclick': 'loadPreset([[1,2],[2,3]])'}
        ],
        'metrics': [
            {'id': "metric-curr-interval", 'label': "Inspecting Interval", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-last-end", 'label': "Last Kept End", 'color': "text-cyan-400", 'initial': "-inf"},
            {'id': "metric-action", 'label': "Greedy Decision", 'color': "text-purple-400", 'initial': "Sorted by end"},
            {'id': "metric-removals", 'label': "Removals Needed", 'color': "text-rose-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p435-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace greedy interval scheduling.',
        'pseudocode_lines': [
            "intervals.sortBy { it[1] }",
            "var count = 0; var lastEnd = Int.MIN_VALUE",
            "for (inter in intervals) {",
            "    if (inter[0] >= lastEnd) lastEnd = inter[1]",
            "    else count++",
            "}",
            "return count"
        ],
        'js_code': """
    let rawIntervals = [[1, 2], [2, 3], [3, 4], [1, 3]];

    function buildSteps(intervals) {
      steps = [];
      const sorted = [...intervals].sort((a,b)=>a[1]-b[1]);
      let lastEnd = -Infinity;
      let removals = 0;

      steps.push({
        interval: "-", lastEnd: "-inf", action: "Sorted by end time", removals: 0,
        activeIdx: -1, sorted: [...sorted], kept: [], removed: [], line: 1,
        narration: `Sorted intervals by end time ascending: ${sorted.map(i=>'['+i[0]+','+i[1]+']').join(', ')}.`
      });

      const kept = [];
      const removed = [];

      for (let i = 0; i < sorted.length; i++) {
        const cur = sorted[i];
        if (cur[0] >= lastEnd) {
          lastEnd = cur[1];
          kept.push(cur);
          steps.push({
            interval: `[${cur[0]}, ${cur[1]}]`, lastEnd: lastEnd, action: "KEEP", removals: removals,
            activeIdx: i, sorted: [...sorted], kept: [...kept], removed: [...removed], line: 4,
            narration: `Interval [${cur[0]}, ${cur[1]}] starts at ${cur[0]} >= lastEnd (${lastEnd === cur[1] ? 'prev' : lastEnd}). No overlap! Kept. New lastEnd = ${lastEnd}.`
          });
        } else {
          removals++;
          removed.push(cur);
          steps.push({
            interval: `[${cur[0]}, ${cur[1]}]`, lastEnd: lastEnd, action: "REMOVE", removals: removals,
            activeIdx: i, sorted: [...sorted], kept: [...kept], removed: [...removed], line: 5,
            narration: `Interval [${cur[0]}, ${cur[1]}] starts at ${cur[0]} < lastEnd (${lastEnd}). OVERLAPS! Must be removed. Total removals = ${removals}.`
          });
        }
      }

      steps.push({
        interval: "Done", lastEnd: lastEnd, action: "Finished", removals: removals,
        activeIdx: -1, sorted: [...sorted], kept: [...kept], removed: [...removed], line: 7,
        narration: `Greedy sweep complete. Removed ${removals} interval(s) to make remaining ${kept.length} non-overlapping.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p435-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-2 flex-wrap">';
      s.sorted.forEach((cur, idx) => {
        const isAct = idx === s.activeIdx;
        const isKept = s.kept.some(k => k[0] === cur[0] && k[1] === cur[1]);
        const isRem = s.removed.some(r => r[0] === cur[0] && r[1] === cur[1]);
        let cls = 'bg-slate-900 border-slate-700 text-slate-300';
        let badge = '';

        if (isRem) {
          cls = 'bg-rose-950/40 border-rose-600 text-rose-300 line-through';
          badge = '<span class="text-[9px] block text-rose-400 font-bold">REMOVED</span>';
        } else if (isKept) {
          cls = 'bg-emerald-500/20 border-emerald-500 text-emerald-200';
          badge = '<span class="text-[9px] block text-emerald-400 font-bold">KEPT</span>';
        }
        if (isAct) cls += ' ring-2 ring-amber-400';

        html += `<div class="p-2 rounded-lg border font-mono text-xs flex flex-col items-center ${cls}"><span>[${cur[0]}, ${cur[1]}]</span>${badge}</div>`;
      });
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-curr-interval').textContent = s.interval;
      document.getElementById('metric-last-end').textContent = s.lastEnd;
      document.getElementById('metric-action').textContent = s.action;
      document.getElementById('metric-removals').textContent = s.removals;
    }

    function loadPreset(arr) {
      rawIntervals = arr;
      buildSteps(rawIntervals);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Greedy Sort by End Time (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N log N) Time, O(1) Auxiliary Space",
            'summary': "Sort intervals ascending by end coordinate. Iterate linearly: if interval starts >= lastEnd, advance lastEnd. Otherwise, increment removal counter.",
            'kotlin_code': """class Solution {
    fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
        if (intervals.isEmpty()) return 0
        intervals.sortBy { it[1] } // Sort by end time ascending

        var removals = 0
        var lastEnd = intervals[0][1]

        for (i in 1 until intervals.size) {
            if (intervals[i][0] >= lastEnd) {
                lastEnd = intervals[i][1] // No overlap, keep this interval
            } else {
                removals++ // Overlap detected, remove this interval
            }
        }
        return removals
    }
}""",
            'python_code': """class Solution:
    def eraseOverlapIntervals(self, intervals: list[list[int]]) -> int:
        if not intervals:
            return 0

        # Sort by end time ascending
        intervals.sort(key=lambda x: x[1])

        removals = 0
        last_end = intervals[0][1]

        for i in range(1, len(intervals)):
            if intervals[i][0] >= last_end:
                last_end = intervals[i][1]
            else:
                removals += 1

        return removals""",
            'java_code': """import java.util.Arrays;

class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) return 0;

        // Sort by end time ascending
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int removals = 0;
        int lastEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= lastEnd) {
                lastEnd = intervals[i][1];
            } else {
                removals++;
            }
        }
        return removals;
    }
}""",
            'memory_breakdown': "Strictly O(1) auxiliary space beyond the in-place sorting stack. Strictly O(N log N) runtime dominated by the initial sort."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
        // Write your optimal Greedy End-Time solution here
        if (intervals.isEmpty()) return 0
        intervals.sortBy { it[1] }
        var count = 0
        var lastEnd = intervals[0][1]
        for (i in 1 until intervals.size) {
            if (intervals[i][0] >= lastEnd) lastEnd = intervals[i][1]
            else count++
        }
        return count
    }
}""",
        'python_boilerplate': """class Solution:
    def eraseOverlapIntervals(self, intervals: list[list[int]]) -> int:
        # Write your optimal Greedy End-Time solution here
        if not intervals: return 0
        intervals.sort(key=lambda x: x[1])
        removals = 0
        last_end = intervals[0][1]
        for i in range(1, len(intervals)):
            if intervals[i][0] >= last_end: last_end = intervals[i][1]
            else: removals += 1
        return removals""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        // Write your optimal Greedy End-Time solution here
        if (intervals.length == 0) return 0;
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
        int count = 0, lastEnd = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= lastEnd) lastEnd = intervals[i][1];
            else count++;
        }
        return count;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(intervals) {
        if (!intervals.length) return 0;
        intervals.sort((a, b) => a[1] - b[1]);
        let count = 0;
        let lastEnd = intervals[0][1];
        for (let i = 1; i < intervals.length; i++) {
          if (intervals[i][0] >= lastEnd) lastEnd = intervals[i][1];
          else count++;
        }
        return count;
      }

      const tests = [
        { intervals: [[1,2],[2,3],[3,4],[1,3]], expected: 1 },
        { intervals: [[1,2],[1,2],[1,2]], expected: 2 },
        { intervals: [[1,2],[2,3]], expected: 0 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.intervals);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: intervals=${JSON.stringify(t.intervals)} &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: we are given intervals `[start, end]`. We need to return the minimum number of intervals to remove so that no intervals overlap. Note that touching intervals like `[1, 2]` and `[2, 3]` are considered non-overlapping.\"",
        'phase2': "\"Minimizing the number of removed intervals is mathematically equivalent to maximizing the number of compatible non-overlapping intervals we keep. This is the canonical Interval Scheduling problem, solvable with a greedy choice: sort intervals by their END time in ascending order. By always selecting the interval that ends earliest, we leave the maximum possible room for all remaining intervals. We iterate through the sorted intervals: if an interval starts >= `lastEnd`, there is no overlap, so we keep it and update `lastEnd = interval[1]`. If it starts < `lastEnd`, it conflicts with our previously kept interval, so we must remove it and increment our removal counter.\"",
        'phase3': "\"Let me write the code. If intervals is empty, return 0. Sort intervals by end coordinate: `Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]))`. Initialize `removals = 0` and `lastEnd = intervals[0][1]`. Loop `i` from 1 to `intervals.length - 1`: if `intervals[i][0] >= lastEnd`, update `lastEnd = intervals[i][1]`; else increment `removals`. Return `removals`.\"",
        'phase4': "\"Tracing with `[[1,2], [2,3], [3,4], [1,3]]`. Sorted by end: `[1,2]`, `[2,3]`, `[1,3]`, `[3,4]`. Start with `[1,2]`, lastEnd=2. Next is `[2,3]`: 2 >= 2 &rarr; keep, lastEnd=3. Next is `[1,3]`: 1 < 3 &rarr; overlap! Removals=1. Next is `[3,4]`: 3 >= 3 &rarr; keep, lastEnd=4. Total removals = 1. Matches Example 1.\"",
        'senior_followup': "\"In distributed operating systems and real-time CPU task scheduling (Earliest Deadline First / EDF), this greedy policy is proven mathematically optimal for single-resource scheduling under preemptive and non-preemptive workloads.\""
    },
    'quiz': [
        {
            'question': "Why must intervals be sorted by END time rather than START time?",
            'options': [
                "Sorting by end time greedily leaves the maximum remaining runway for future intervals",
                "Sorting by start time causes integer division by zero in the comparison predicate",
                "End time sorting is required by POSIX memory management specifications",
                "Start time sorting cannot handle negative coordinates on 64-bit architectures"
            ],
            'correct_index': 0,
            'explanation': "An interval that starts very early might span the entire timeline (e.g. `[1, 100]`), blocking many shorter intervals. Choosing the interval that finishes earliest guarantees the smallest commitment of timeline resources."
        },
        {
            'question': "Why should `Integer.compare(a[1], b[1])` be used instead of `a[1] - b[1]` when sorting?",
            'options': [
                "Subtraction `a[1] - b[1]` can cause 32-bit signed integer overflow when coordinates are large negative/positive",
                "Integer.compare converts numbers into floating-point representation for GPU sorting",
                "Subtraction is deprecated in Java 17 and removed in modern Kotlin runtimes",
                "Integer.compare allocates nodes on thread-local heap memory"
            ],
            'correct_index': 0,
            'explanation': "If `a[1] = 50,000` and `b[1] = -50,000`, subtraction `50000 - (-50000) = 100000` is fine, but if numbers are near `Integer.MIN_VALUE` and `Integer.MAX_VALUE`, subtraction overflows into negative numbers, corrupting the comparator."
        }
    ]
}

p77 = {
    'num': 77,
    'lc': 452,
    'title': "Minimum Number of Arrows to Burst Balloons",
    'category': "Intervals & Greedy",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "minimum-number-of-arrows-to-burst-balloons",
    'verbatim_statement': """<p>There are some spherical balloons taped onto a flat wall that represents the XY-plane. The balloons are represented as a 2D integer array <code>points</code> where <code>points[i] = [x_start, x_end]</code> denotes a balloon whose horizontal diameter stretches between <code>x_start</code> and <code>x_end</code>.</p>
<p>An arrow can be shot up <strong>exactly vertically</strong> (in the positive Y-direction) from different points along the X-axis. A balloon with <code>x_start</code> and <code>x_end</code> is burst by an arrow shot at <code>x</code> if <code>x_start <= x <= x_end</code>. There is no limit to the number of arrows that can be shot. A single arrow can burst multiple balloons as long as its shot coordinate overlaps each balloon.</p>
<p>Given the array <code>points</code>, return <em>the minimum number of arrows that must be shot to burst all balloons</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'points = [[10,16],[2,8],[1,6],[7,12]]', 'output': '2\nExplanation: The balloons can be burst by 2 arrows:\n- Shoot an arrow at x = 6, bursting the balloons [2,8] and [1,6].\n- Shoot an arrow at x = 11, bursting the balloons [10,16] and [7,12].'},
        {'title': "Example 2", 'input': 'points = [[1,2],[3,4],[5,6],[7,8]]', 'output': '4\nExplanation: One arrow needs to be shot for each balloon for a total of 4 arrows.'},
        {'title': "Example 3", 'input': 'points = [[1,2],[2,3],[3,4],[4,5]]', 'output': '2\nExplanation: The balloons can be burst by 2 arrows:\n- Shoot an arrow at x = 2, bursting the balloons [1,2] and [2,3].\n- Shoot an arrow at x = 4, bursting the balloons [3,4] and [4,5].'}
    ],
    'constraints': [
        "1 <= points.length <= 10^5",
        "points[i].length == 2",
        "-2^31 <= x_start < x_end <= 2^31 - 1"
    ],
    'plain_english': "Each arrow shot at coordinate x bursts all balloons covering x. To burst the maximum number of overlapping balloons with each arrow, sort balloons by their end coordinate `x_end` ascending. Shoot the first arrow at the end of the first balloon (`arrowPos = points[0][1]`). For every subsequent balloon, if its start is <= `arrowPos`, it is already pierced by the same arrow! Only when a balloon starts strictly after `arrowPos` (`x_start > arrowPos`) do we need a new arrow, which we shoot at that balloon's end coordinate.",
    'boundary_checks': {
        'min_base': "Single balloon <code>points = [[1, 5]]</code> &rarr; Returns 1.",
        'best_exit': "All balloons sharing common point <code>[[1, 10], [2, 8], [3, 6]]</code> &rarr; Shot at x=6 bursts all; returns 1.",
        'worst_scan': "Integer.MIN_VALUE / MAX_VALUE <code>[[-2147483646,-2147483645],[2147483646,2147483647]]</code> &rarr; Must use `Integer.compare` to prevent subtraction overflow."
    },
    'mental_model': {
        'analogy_title': "The Skewer Through the Fruit Line",
        'analogy_text': "Imagine rows of fruit slices on a table. You want to skewer them with the fewest vertical toothpicks. Look at the fruit slice that ends earliest on the table. You MUST put a toothpick through this fruit before it ends, and the optimal place to skewer it is at its very rightmost edge. Placing the toothpick at its right edge gives it the greatest possible reach to simultaneously pierce any other fruits starting before that point.",
        'aha_moment': "This problem is mathematically isomorphic to Non-overlapping Intervals (LC #435)! The minimum number of arrows equals the maximum number of mutually disjoint intervals. Every disjoint interval requires its own dedicated arrow.",
        'trigger': "Burst overlapping 1D intervals with minimum vertical points &rarr; Greedy Sort by End Coordinate.",
        'sisters': [
            {'name': "Non-overlapping Intervals", 'lc': "LC #435"},
            {'name': "Merge Intervals", 'lc': "LC #56"},
            {'name': "Meeting Rooms II", 'lc': "LC #253"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(2^N) Time, O(N) Space",
        'naive_text': "Brute-force set cover: generate candidate shooting points and search minimum hitting set.",
        'wasted_work': "Solving general NP-hard Set Cover when 1D intervals permit an O(N log N) greedy optimal solution.",
        'optimal_complexity': "O(N log N) Time, O(1) Auxiliary Space",
        'optimal_text': "Sort by end coordinate. Linear sweep updating `arrowPos` whenever `point[0] > arrowPos`.",
        'core_invariant': "`arrowPos` is the coordinate of the last arrow fired. Any balloon with `start <= arrowPos` is guaranteed to be burst by that arrow.",
        'table_rows': [
            {'name': "1. Set Cover Backtracking", 'time': "O(2^N)", 'space': "O(N)", 'note': "Exponential search; unfeasible for N > 25."},
            {'name': "★ 2. Greedy End-Coordinate Sort", 'time': "O(N log N)", 'space': "O(1) Auxiliary", 'note': "Optimal; exactly 1 pass after sorting."}
        ]
    },
    'trace_table': {
        'input_desc': 'points = [[10, 16], [2, 8], [1, 6], [7, 12]] &rarr; Sorted by end: [[1, 6], [2, 8], [7, 12], [10, 16]]',
        'columns': ["Balloon [start, end]", "arrowPos", "Condition Check", "Action Taken", "Total Arrows"],
        'rows': [
            ["[1, 6]", "Unset", "First balloon", "Shoot Arrow #1 at x = 6", "1"],
            ["[2, 8]", "6", "2 <= 6 (start <= arrowPos)", "Burst by Arrow #1 (shares x = 6)", "1"],
            ["[7, 12]", "6", "7 > 6 (start > arrowPos)", "Arrow #1 missed! Shoot Arrow #2 at x = 12", "2"],
            ["[10, 16]", "12", "10 <= 12 (start <= arrowPos)", "Burst by Arrow #2 (shares x = 12)", "2"]
        ],
        'takeaway': "Two arrows (at x = 6 and x = 12) burst all 4 balloons."
    },
    'stepper': {
        'title': "Balloon Arrow Skewering Stepper",
        'default_input': '[[10,16],[2,8],[1,6],[7,12]]',
        'presets': [
            {'label': "Example 1: [[10,16],[2,8],[1,6],[7,12]]", 'onclick': 'loadPreset([[10,16],[2,8],[1,6],[7,12]])'},
            {'label': "Sequential [[1,2],[3,4],[5,6]]", 'onclick': 'loadPreset([[1,2],[3,4],[5,6]])'},
            {'label': "Touching [[1,2],[2,3],[3,4]]", 'onclick': 'loadPreset([[1,2],[2,3],[3,4]])'}
        ],
        'metrics': [
            {'id': "metric-balloon", 'label': "Inspecting Balloon", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-arrow-pos", 'label': "Current Arrow X", 'color': "text-cyan-400", 'initial': "none"},
            {'id': "metric-action", 'label': "Arrow Action", 'color': "text-purple-400", 'initial': "Sorted"},
            {'id': "metric-arrows-count", 'label': "Arrows Shot", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p452-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace greedy arrow placement.',
        'pseudocode_lines': [
            "points.sortWith { a, b -> a[1].compareTo(b[1]) }",
            "var arrows = 1; var arrowPos = points[0][1]",
            "for (i in 1 until points.size) {",
            "    if (points[i][0] > arrowPos) { arrows++; arrowPos = points[i][1] }",
            "}",
            "return arrows"
        ],
        'js_code': """
    let rawPoints = [[10, 16], [2, 8], [1, 6], [7, 12]];

    function buildSteps(points) {
      steps = [];
      const sorted = [...points].sort((a,b)=>a[1]-b[1]);
      let arrows = 1;
      let arrowPos = sorted[0][1];

      steps.push({
        balloon: "-", arrowPos: "none", action: "Sorted by end point", arrows: 0,
        activeIdx: -1, sorted: [...sorted], arrowLocations: [], line: 1,
        narration: `Sorted balloons by end position: ${sorted.map(p=>'['+p[0]+','+p[1]+']').join(', ')}.`
      });

      const arrowLocs = [arrowPos];
      steps.push({
        balloon: `[${sorted[0][0]}, ${sorted[0][1]}]`, arrowPos: `${arrowPos}`, action: "Fired Arrow #1", arrows: 1,
        activeIdx: 0, sorted: [...sorted], arrowLocations: [...arrowLocs], line: 2,
        narration: `Shot Arrow #1 at x = ${arrowPos} (end of first balloon).`
      });

      for (let i = 1; i < sorted.length; i++) {
        const b = sorted[i];
        if (b[0] > arrowPos) {
          arrows++;
          arrowPos = b[1];
          arrowLocs.push(arrowPos);
          steps.push({
            balloon: `[${b[0]}, ${b[1]}]`, arrowPos: `${arrowPos}`, action: `Fired Arrow #${arrows}`, arrows: arrows,
            activeIdx: i, sorted: [...sorted], arrowLocations: [...arrowLocs], line: 4,
            narration: `Balloon [${b[0]}, ${b[1]}] starts at ${b[0]} > last arrow (${arrowLocs[arrowLocs.length - 2]}). Fired Arrow #${arrows} at x = ${arrowPos}.`
          });
        } else {
          steps.push({
            balloon: `[${b[0]}, ${b[1]}]`, arrowPos: `${arrowPos}`, action: "Burst by current arrow", arrows: arrows,
            activeIdx: i, sorted: [...sorted], arrowLocations: [...arrowLocs], line: 3,
            narration: `Balloon [${b[0]}, ${b[1]}] overlaps x = ${arrowPos} (${b[0]} <= ${arrowPos} <= ${b[1]}). Pierced by current arrow!`
          });
        }
      }

      steps.push({
        balloon: "All Done", arrowPos: "-", action: "Complete", arrows: arrows,
        activeIdx: -1, sorted: [...sorted], arrowLocations: [...arrowLocs], line: 6,
        narration: `All balloons popped! Minimum arrows needed = ${arrows}. Arrow positions: x = [${arrowLocs.join(', ')}].`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p452-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-2 flex-wrap">';
      s.sorted.forEach((p, idx) => {
        const isAct = idx === s.activeIdx;
        html += `<div class="p-2.5 rounded-lg border font-mono text-xs flex flex-col items-center ${isAct ? 'bg-amber-500/20 border-amber-400 text-amber-300 ring-2 ring-amber-500' : 'bg-slate-900 border-slate-700 text-slate-300'}"><span class="font-bold">[${p[0]}, ${p[1]}]</span></div>`;
      });
      html += '</div>';

      if (s.arrowLocations.length > 0) {
        html += `<div class="text-xs font-mono text-cyan-300 font-bold bg-slate-950/60 px-4 py-2 rounded-lg border border-cyan-800/60">Arrows Fired at X: [${s.arrowLocations.join(', ')}]</div>`;
      }
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-balloon').textContent = s.balloon;
      document.getElementById('metric-arrow-pos').textContent = s.arrowPos;
      document.getElementById('metric-action').textContent = s.action;
      document.getElementById('metric-arrows-count').textContent = s.arrows;
    }

    function loadPreset(arr) {
      rawPoints = arr;
      buildSteps(rawPoints);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Greedy Sort by End Coordinate (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N log N) Time, O(1) Auxiliary Space",
            'summary': "Sort balloons by end coordinate ascending. Shoot first arrow at points[0][1]. For each subsequent balloon, if its start > arrowPos, shoot a new arrow at its end coordinate.",
            'kotlin_code': """class Solution {
    fun findMinArrowShots(points: Array<IntArray>): Int {
        if (points.isEmpty()) return 0

        // Use compareTo to avoid integer subtraction overflow with large negative numbers
        points.sortWith { a, b -> a[1].compareTo(b[1]) }

        var arrows = 1
        var arrowPos = points[0][1]

        for (i in 1 until points.size) {
            if (points[i][0] > arrowPos) {
                arrows++
                arrowPos = points[i][1]
            }
        }
        return arrows
    }
}""",
            'python_code': """class Solution:
    def findMinArrowShots(self, points: list[list[int]]) -> int:
        if not points:
            return 0

        points.sort(key=lambda x: x[1])

        arrows = 1
        arrow_pos = points[0][1]

        for i in range(1, len(points)):
            if points[i][0] > arrow_pos:
                arrows += 1
                arrow_pos = points[i][1]

        return arrows""",
            'java_code': """import java.util.Arrays;

class Solution {
    public int findMinArrowShots(int[][] points) {
        if (points == null || points.length == 0) return 0;

        // Use Integer.compare to prevent integer overflow with Integer.MIN_VALUE
        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));

        int arrows = 1;
        int arrowPos = points[0][1];

        for (int i = 1; i < points.length; i++) {
            if (points[i][0] > arrowPos) {
                arrows++;
                arrowPos = points[i][1];
            }
        }
        return arrows;
    }
}""",
            'memory_breakdown': "Strictly O(1) auxiliary space beyond the in-place sorting call stack. Strictly O(N log N) runtime dominated by sorting."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun findMinArrowShots(points: Array<IntArray>): Int {
        // Write your optimal Greedy Sort by End Coordinate solution here
        if (points.isEmpty()) return 0
        points.sortWith { a, b -> a[1].compareTo(b[1]) }
        var arrows = 1
        var arrowPos = points[0][1]
        for (i in 1 until points.size) {
            if (points[i][0] > arrowPos) {
                arrows++
                arrowPos = points[i][1]
            }
        }
        return arrows
    }
}""",
        'python_boilerplate': """class Solution:
    def findMinArrowShots(self, points: list[list[int]]) -> int:
        # Write your optimal Greedy Sort by End Coordinate solution here
        if not points: return 0
        points.sort(key=lambda x: x[1])
        arrows = 1
        arrow_pos = points[0][1]
        for i in range(1, len(points)):
            if points[i][0] > arrow_pos:
                arrows += 1
                arrow_pos = points[i][1]
        return arrows""",
        'java_boilerplate': """import java.util.*;

class Solution {
    public int findMinArrowShots(int[][] points) {
        // Write your optimal Greedy Sort by End Coordinate solution here
        if (points == null || points.length == 0) return 0;
        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
        int arrows = 1, arrowPos = points[0][1];
        for (int i = 1; i < points.length; i++) {
            if (points[i][0] > arrowPos) {
                arrows++;
                arrowPos = points[i][1];
            }
        }
        return arrows;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(points) {
        if (!points.length) return 0;
        points.sort((a, b) => a[1] - b[1]);
        let arrows = 1;
        let arrowPos = points[0][1];
        for (let i = 1; i < points.length; i++) {
          if (points[i][0] > arrowPos) {
            arrows++;
            arrowPos = points[i][1];
          }
        }
        return arrows;
      }

      const tests = [
        { points: [[10,16],[2,8],[1,6],[7,12]], expected: 2 },
        { points: [[1,2],[3,4],[5,6],[7,8]], expected: 4 },
        { points: [[1,2],[2,3],[3,4],[4,5]], expected: 2 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.points);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: points=${JSON.stringify(t.points)} &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me confirm constraints: `points[i]` represents `[x_start, x_end]`. Coordinates range from -2^31 to 2^31 - 1, which means differences can overflow a 32-bit integer. An arrow shot at x bursts all balloons where `x_start <= x <= x_end`.\"",
        'phase2': "\"This is a greedy interval scheduling problem. To maximize the number of balloons each arrow can pierce, we sort all balloons by their end coordinate in ascending order. We shoot the first arrow at the end coordinate of the first balloon: `arrowPos = points[0][1]`. Because balloons are sorted by end coordinate, shooting at `arrowPos` guarantees we pierce the first balloon, while reaching as far to the right as possible to hit any overlapping balloons. We iterate through the remaining balloons: if a balloon starts after `arrowPos` (`points[i][0] > arrowPos`), our current arrow cannot hit it, so we must shoot a new arrow at that balloon's end coordinate (`arrowPos = points[i][1]`) and increment our arrow count.\"",
        'phase3': "\"Let me write the code. If points is empty, return 0. Sort points using `Integer.compare(a[1], b[1])` to prevent integer overflow. Initialize `arrows = 1` and `arrowPos = points[0][1]`. For `i` from 1 to `points.length - 1`: if `points[i][0] > arrowPos`, `arrows++`, `arrowPos = points[i][1]`. Return `arrows`.\"",
        'phase4': "\"Tracing with `[[10,16], [2,8], [1,6], [7,12]]`. Sorted by end: `[1,6]`, `[2,8]`, `[7,12]`, `[10,16]`. Arrow 1 shot at x=6. Balloon `[2,8]` has start 2 <= 6 &rarr; pierced! Balloon `[7,12]` has start 7 > 6 &rarr; requires Arrow 2, shot at x=12. Balloon `[10,16]` has start 10 <= 12 &rarr; pierced! Total arrows = 2. Handled with O(N log N) time and O(1) auxiliary space.\"",
        'senior_followup': "\"In radar signal processing and sensor fusion, this 1D stabbing problem corresponds to finding minimum sensor pulses to detect a set of transient signal bursts across time windows.\""
    },
    'quiz': [
        {
            'question': "Why should the arrow be shot at `points[i][1]` (the end of the balloon) rather than the start?",
            'options': [
                "Shooting at the end reaches as far right as possible to pierce any subsequent balloons starting nearby",
                "Shooting at the start causes integer overflow on negative coordinates",
                "The problem description prohibits shooting arrows at the left boundary of a balloon",
                "The balloon's rubber skin is thinnest at its rightmost coordinate"
            ],
            'correct_index': 0,
            'explanation': "The balloon must be pierced somewhere within `[start, end]`. Placing the arrow at the furthest possible right point (`end`) maximizes the probability that balloons starting later also overlap with this arrow."
        },
        {
            'question': "Why does `(a, b) -> a[1] - b[1]` fail on LeetCode test cases?",
            'options': [
                "Subtracting large negative from large positive numbers causes 32-bit signed integer overflow",
                "Java comparator prohibits lambda expressions with two parameters",
                "Subtraction creates garbage collection heap allocations in modern JVMs",
                "The arrow shooting engine requires unsigned integer arithmetic"
            ],
            'correct_index': 0,
            'explanation': "If `a[1] = 2147483647` and `b[1] = -2147483648`, `a[1] - b[1]` overflows into a negative value, causing the sort to place `a` before `b` incorrectly. Using `Integer.compare(a[1], b[1])` correctly handles extreme 32-bit bounds."
        }
    ]
}
