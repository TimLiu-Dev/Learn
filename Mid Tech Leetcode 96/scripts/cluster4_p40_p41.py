import sys
sys.path.append('scripts')

p40 = {
    'num': 40,
    'lc': 199,
    'title': "Binary Tree Right Side View",
    'category': "Binary Trees & BST",
    'difficulty': "Medium",
    'lc_slug': "binary-tree-right-side-view",
    'verbatim_statement': """<p>Given the <code>root</code> of a binary tree, imagine yourself standing on the <strong>right side</strong> of it, return <em>the values of the nodes you can see ordered from top to bottom</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'root = [1,2,3,null,5,null,4]', 'output': '[1,3,4]'},
        {'title': "Example 2", 'input': 'root = [1,null,3]', 'output': '[1,3]'},
        {'title': "Example 3", 'input': 'root = []', 'output': '[]'}
    ],
    'constraints': [
        "The number of nodes in the tree is in the range [0, 100].",
        "-100 <= Node.val <= 100"
    ],
    'plain_english': "Return the rightmost node at every vertical depth level. In BFS, record the last element popped in each level snapshot. In DFS, visit right children before left children: the very first node visited at each depth level is the visible rightmost node.",
    'boundary_checks': {
        'min_base': "<code>root == null</code> &rarr; Empty tree returns <code>[]</code>.",
        'best_exit': "Single node tree <code>root = [1]</code> &rarr; Immediately returns <code>[1]</code>.",
        'worst_scan': "Left subtree deeper than right subtree &rarr; Left-side nodes become visible as soon as right branch runs out."
    },
    'mental_model': {
        'analogy_title': "Horizons from an Observation Deck",
        'analogy_text': "You are standing on a viewing platform looking across a forest canopy from the right side. At each horizontal altitude (depth), you can only see the outermost tree branch protruding toward the right. If the right wing terminates early, deeper left branches underneath are exposed to the horizon.",
        'aha_moment': "A node in the left subtree CAN be visible from the right if no node exists at that same depth in the right subtree! You cannot merely traverse `curr = curr.right`.",
        'trigger': "View from an angle (Right view, Left view, Top view) &rarr; BFS taking terminal row element OR Reverse DFS (Right before Left) with depth check `depth == result.size`.",
        'sisters': [
            {'name': "Binary Tree Level Order Traversal", 'lc': "LC #102"},
            {'name': "Find Bottom Left Tree Value", 'lc': "LC #513"},
            {'name': "Binary Tree Vertical Order Traversal", 'lc': "LC #314"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N) Time, O(N) Space",
        'naive_text': "Run full Level-Order BFS, construct entire sublists for every level, then extract `sublist.last()` for each level.",
        'wasted_work': "Allocating full row arrays for all N nodes when we only ever preserve one element per level.",
        'optimal_complexity': "O(N) Time, O(H) Space",
        'optimal_text': "Reverse Pre-order DFS (visit Root, Right, Left). When `depth == result.size`, append the node value immediately. Uses minimal O(H) recursion stack.",
        'core_invariant': "Because right subtrees are fully traversed before left subtrees, the first node reached at depth d is strictly the rightmost node at that level.",
        'table_rows': [
            {'name': "1. Full BFS Sublist Collection", 'time': "O(N)", 'space': "O(N)", 'note': "Allocates lists for all tree nodes across all tiers."},
            {'name': "2. BFS Queue Snapshot (Last Element)", 'time': "O(N)", 'space': "O(W)", 'note': "Captures `if (i == size - 1)` without extra row allocations."},
            {'name': "★ 3. Reverse DFS (Root -> Right -> Left)", 'time': "O(N)", 'space': "O(H)", 'note': "Appends directly on `depth == result.size`; optimal call stack."}
        ]
    },
    'trace_table': {
        'input_desc': 'root = [1, 2, 3, null, 5, null, 4]',
        'columns': ["Step", "Node Visited", "Current Depth", "Result List Before", "Condition (depth == res.size)", "Action"],
        'rows': [
            ["1", "Node(1) (Root)", "0", "[]", "0 == 0 (True)", "Add 1 &rarr; [1]"],
            ["2", "Node(3) (Right child)", "1", "[1]", "1 == 1 (True)", "Add 3 &rarr; [1, 3]"],
            ["3", "Node(4) (Right child)", "2", "[1, 3]", "2 == 2 (True)", "Add 4 &rarr; [1, 3, 4]"],
            ["4", "Node(2) (Left child)", "1", "[1, 3, 4]", "1 == 3 (False)", "Depth 1 already recorded &rarr; Skip"],
            ["5", "Node(5) (Right child of 2)", "2", "[1, 3, 4]", "2 == 3 (False)", "Depth 2 already recorded &rarr; Skip"]
        ],
        'takeaway': "Result is [1, 3, 4]. Visiting Right before Left ensures the rightmost node at each depth claims the slot first."
    },
    'stepper': {
        'title': "Right Side View Reverse DFS Stepper",
        'default_input': '1, 2, 3, null, 5, null, 4',
        'presets': [
            {'label': "Example [1,2,3,null,5,null,4]", 'onclick': 'loadPreset([1, 2, 3, null, 5, null, 4])'},
            {'label': "Left Taller [1, 2, null, 3, null, 4]", 'onclick': 'loadPreset([1, 2, null, 3, null, 4])'},
            {'label': "Right Skewed [1, null, 2, null, 3]", 'onclick': 'loadPreset([1, null, 2, null, 3])'}
        ],
        'metrics': [
            {'id': "metric-node", 'label': "Current Node", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-depth", 'label': "Current Depth", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-view", 'label': "Visible Right View", 'color': "text-emerald-400", 'initial': "[]"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="right-view-stage" class="w-full flex items-center justify-center gap-4 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace reverse pre-order traversal (Root -> Right -> Left).',
        'pseudocode_lines': [
            "val result = mutableListOf<Int>()",
            "fun dfs(node: TreeNode?, depth: Int) {",
            "    if (node == null) return",
            "    if (depth == result.size) result.add(node.`val`)",
            "    dfs(node.right, depth + 1) // Prioritize right branch",
            "    dfs(node.left, depth + 1)",
            "}"
        ],
        'js_code': """
    let viewTree = [1, 2, 3, null, 5, null, 4];

    function buildSteps(arr) {
      steps = [];
      if (!arr || arr.length === 0 || arr[0] === null) {
        steps.push({
          node: "null", depth: 0, view: "[]", action: "Empty Tree",
          res: [], line: 3, narration: "Empty root returns empty view []."
        });
        return;
      }

      let res = [];

      function dfs(idx, depth) {
        if (idx >= arr.length || arr[idx] === null) return;
        let val = arr[idx];

        if (depth === res.length) {
          res.push(val);
          steps.push({
            node: `${val}`, depth: depth, view: `[${res.join(', ')}]`,
            action: `Added ${val} (First at depth ${depth})`,
            res: [...res], line: 4,
            narration: `Depth ${depth}: ${val} is the first node encountered. It is visible on the right!`
          });
        } else {
          steps.push({
            node: `${val}`, depth: depth, view: `[${res.join(', ')}]`,
            action: `Shadowed ${val}`,
            res: [...res], line: 4,
            narration: `Depth ${depth} already claimed by ${res[depth]}. Node ${val} is shadowed behind it.`
          });
        }

        let rightChild = 2 * idx + 2;
        dfs(rightChild, depth + 1);

        let leftChild = 2 * idx + 1;
        dfs(leftChild, depth + 1);
      }

      dfs(0, 0);

      steps.push({
        node: "Done", depth: "-", view: `[${res.join(', ')}]`,
        action: "Traversal Complete",
        res: [...res], line: 1,
        narration: `Completed right side view: [${res.join(', ')}].`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      viewTree = arr;
      stepperReset();
      buildSteps(viewTree);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => {
        let t = s.trim();
        return (t === 'null' || t === '') ? null : parseInt(t);
      });
      if (arr.length > 0) {
        viewTree = arr;
        stepperReset();
        buildSteps(viewTree);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-node').textContent = step.node;
      document.getElementById('metric-depth').textContent = step.depth;
      document.getElementById('metric-view').textContent = step.view;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('right-view-stage');
      stage.innerHTML = '';
      if (!step.res) return;

      const box = document.createElement('div');
      box.className = 'flex items-center gap-2 p-3 bg-slate-950 border border-slate-800 rounded-xl';
      box.innerHTML = '<span class="text-xs text-slate-400 font-bold uppercase mr-2 font-mono">Visible Right View:</span>' +
        step.res.map((v, i) => `<span class="px-3 py-1 bg-cyan-500/20 text-cyan-300 font-mono text-sm font-bold border border-cyan-500/40 rounded">${v}</span>`).join(' ');
      stage.appendChild(box);
"""
    },
    'approaches': [
        {
            'name': "Level-by-Level BFS Queue Snapshot",
            'desc': "Perform level-order traversal with a FIFO queue. Record the last element in each level snapshot.",
            'time': "O(N)",
            'space': "O(W) <= O(N)",
            'code_kotlin': """class Solution {
    fun rightSideView(root: TreeNode?): List<Int> {
        if (root == null) return emptyList()
        val result = mutableListOf<Int>()
        val queue = ArrayDeque<TreeNode>()
        queue.add(root)

        while (queue.isNotEmpty()) {
            val levelSize = queue.size
            for (i in 0 until levelSize) {
                val node = queue.removeFirst()
                if (i == levelSize - 1) {
                    result.add(node.`val`)
                }
                node.left?.let { queue.add(it) }
                node.right?.let { queue.add(it) }
            }
        }
        return result
    }
}""",
            'notes_kotlin': "Standard queue BFS. Capturing `i == levelSize - 1` takes the rightmost node of each tier.",
            'code_python': """class Solution:
    def rightSideView(self, root: Optional[TreeNode]) -> List[int]:
        if not root:
            return []
        result = []
        queue = collections.deque([root])

        while queue:
            level_size = len(queue)
            for i in range(level_size):
                node = queue.popleft()
                if i == level_size - 1:
                    result.append(node.val)
                if node.left:
                    queue.append(node.left)
                if node.right:
                    queue.append(node.right)
                    
        return result""",
            'notes_python': "Intuitive BFS. Memory bounded by maximum tree width W.",
            'code_java': """class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (i == levelSize - 1) {
                    result.add(node.val);
                }
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return result;
    }
}""",
            'notes_java': "Iterative and robust against recursion depth stack overflow exceptions."
        },
        {
            'name': "Reverse BFS (Right to Left Enqueue)",
            'desc': "Enqueue right children before left children in BFS; the first element popped per level is the rightmost.",
            'time': "O(N)",
            'space': "O(W) <= O(N)",
            'code_kotlin': """class Solution {
    fun rightSideView(root: TreeNode?): List<Int> {
        if (root == null) return emptyList()
        val result = mutableListOf<Int>()
        val queue = ArrayDeque<TreeNode>()
        queue.add(root)

        while (queue.isNotEmpty()) {
            val levelSize = queue.size
            for (i in 0 until levelSize) {
                val node = queue.removeFirst()
                if (i == 0) {
                    result.add(node.`val`)
                }
                node.right?.let { queue.add(it) }
                node.left?.let { queue.add(it) }
            }
        }
        return result
    }
}""",
            'notes_kotlin': "Enqueueing `node.right` first allows taking `i == 0` immediately as the visible node.",
            'code_python': """class Solution:
    def rightSideView(self, root: Optional[TreeNode]) -> List[int]:
        if not root: return []
        res = []
        q = collections.deque([root])
        while q:
            size = len(q)
            for i in range(size):
                n = q.popleft()
                if i == 0:
                    res.append(n.val)
                if n.right: q.append(n.right)
                if n.left: q.append(n.left)
        return res""",
            'notes_python': "Slight variation that captures the first node dequeued per level.",
            'code_java': """class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (i == 0) result.add(node.val);
                if (node.right != null) queue.offer(node.right);
                if (node.left != null) queue.offer(node.left);
            }
        }
        return result;
    }
}""",
            'notes_java': "Mirrored BFS order gives instant access to rightmost node at index 0."
        },
        {
            'name': "Reverse Pre-order DFS (Optimal Primary)",
            'desc': "Traverse recursively visiting right child before left. When depth == result.size, record node.",
            'time': "O(N)",
            'space': "O(H) Call Stack",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Reverse Pre-Order DFS (Root -> Right -> Left)
     * Time: O(N) - Visits each node once
     * Space: O(H) - Recursion stack bounded by tree height (log N balanced)
     */
    fun rightSideView(root: TreeNode?): List<Int> {
        val result = mutableListOf<Int>()
        dfs(root, 0, result)
        return result
    }

    private fun dfs(node: TreeNode?, depth: Int, result: MutableList<Int>) {
        if (node == null) return

        // First node reached at this depth is guaranteed to be the rightmost
        if (depth == result.size) {
            result.add(node.`val`)
        }

        dfs(node.right, depth + 1)
        dfs(node.left, depth + 1)
    }
}""",
            'notes_kotlin': "Minimal O(H) memory. Bypasses queue heap allocation entirely.",
            'code_python': """class Solution:
    # Optimal: Reverse Pre-Order DFS
    # Time: O(N) - Single pass
    # Space: O(H) - Recursion stack
    def rightSideView(self, root: Optional[TreeNode]) -> List[int]:
        result = []
        
        def dfs(node: Optional[TreeNode], depth: int):
            if not node:
                return
            if depth == len(result):
                result.append(node.val)
            dfs(node.right, depth + 1)
            dfs(node.left, depth + 1)
            
        dfs(root, 0)
        return result""",
            'notes_python': "Concise, elegant Python implementation. Demonstrates mastery of pre-order tree traversal.",
            'code_java': """class Solution {
    /**
     * Optimal: Reverse Pre-Order DFS
     * Time: O(N) - Visits all nodes
     * Space: O(H) - Call stack proportional to tree height
     */
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        dfs(root, 0, result);
        return result;
    }

    private void dfs(TreeNode node, int depth, List<Integer> result) {
        if (node == null) return;

        if (depth == result.size()) {
            result.add(node.val);
        }

        dfs(node.right, depth + 1);
        dfs(node.left, depth + 1);
    }
}""",
            'notes_java': "Clean idiomatic Java. `depth == result.size()` acts as an automatic guard against duplicate level entries."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun rightSideView(root: TreeNode?): List<Int> {
        val result = mutableListOf<Int>()
        fun dfs(node: TreeNode?, depth: Int) {
            if (node == null) return
            if (depth == result.size) result.add(node.`val`)
            dfs(node.right, depth + 1)
            dfs(node.left, depth + 1)
        }
        dfs(root, 0)
        return result
    }
}""",
        'python_boilerplate': """class Solution:
    def rightSideView(self, root: Optional[TreeNode]) -> List[int]:
        res = []
        def dfs(node, depth):
            if not node: return
            if depth == len(res): res.append(node.val)
            dfs(node.right, depth + 1)
            dfs(node.left, depth + 1)
        dfs(root, 0)
        return res""",
        'java_boilerplate': """class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        dfs(root, 0, res);
        return res;
    }
    private void dfs(TreeNode node, int depth, List<Integer> res) {
        if (node == null) return;
        if (depth == res.size()) res.add(node.val);
        dfs(node.right, depth + 1);
        dfs(node.left, depth + 1);
    }
}""",
        'js_test_runner': """
      const testCases = [
        { tree: [1,2,3,null,5,null,4], expected: [1,3,4] },
        { tree: [1,null,3], expected: [1,3] },
        { tree: [], expected: [] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runRightView(arr) {
          if (!arr || arr.length === 0 || arr[0] === null) return [];
          let res = [];
          function dfs(i, d) {
            if (i >= arr.length || arr[i] === null) return;
            if (d === res.length) res.push(arr[i]);
            dfs(2 * i + 2, d + 1);
            dfs(2 * i + 1, d + 1);
          }
          dfs(0, 0);
          return res;
        }

        const out = runRightView(tc.tree);
        const passed = JSON.stringify(out) === JSON.stringify(tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: tree=[${tc.tree.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (' + JSON.stringify(out) + ')' : '&#10008; FAILED'}</span>
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
        'phase1': "I clarify: What if the left branch extends deeper than the right branch? Nodes in the left branch must still be visible if nothing is blocking them on the right. Can the tree be empty? Yes, return an empty list.",
        'phase2': "For strategy: In BFS, we can collect the last node of each level snapshot in O(N) time and O(W) space. But an even cleaner approach is reverse pre-order DFS (visiting root, then right child, then left child). By visiting right first, the first node reached at depth d is strictly the rightmost node at that level. We add it whenever `depth == result.size`. Time is O(N) and space is O(H).",
        'phase3': "I implement reverse DFS. In the helper function `dfs(node, depth)`: base check returns if node is null. If `depth == result.size`, append `node.val`. Then call `dfs(node.right, depth + 1)`, followed by `dfs(node.left, depth + 1)`. Finally return the result list.",
        'phase4': "Dry run edge case: root = [1, 2, 3, null, 5, null, 4]. Root 1 is added at depth 0. Right child 3 added at depth 1. Node 4 added at depth 2. Left subtree visits 2 (depth 1, already recorded, skipped) and 5 (depth 2, already recorded, skipped). Result is [1, 3, 4].",
        'senior_followup': "Senior Scalability: In deep binary trees with recursion limits, DFS risks stack overflow. In production language runtimes without tail call optimization, queue BFS using an `ArrayDeque` is safer for arbitrary depths."
    },
    'quiz': [
        {
            'question': "Why does `depth == result.size` correctly identify visible nodes in reverse pre-order DFS?",
            'options': [
                "Because right children are visited before left children, meaning the first node to reach any depth is guaranteed to be the rightmost.",
                "Because all binary trees are complete and balanced.",
                "Because depth counts up while result.size counts down.",
                "It only works on binary search trees."
            ],
            'correct_index': 0,
            'explanation': "Prioritizing the right subtree ensures that the rightmost element at level d is discovered first. Once added, `result.size` increases to `d + 1`, blocking subsequent left elements at level d from entering."
        },
        {
            'question': "If the right subtree has depth 2 and the left subtree has depth 5, does reverse DFS include nodes from depths 3, 4, and 5?",
            'options': [
                "No, because traversal halts when the right branch ends.",
                "Yes, because when DFS moves to the left branch, result.size is still only 2, so depths 3, 4, and 5 will satisfy depth == result.size.",
                "No, it throws a ConcurrentModificationException.",
                "Only if the nodes have positive values."
            ],
            'correct_index': 1,
            'explanation': "The algorithm continues down the deeper left branch. For depths 3, 4, and 5, no node from the right branch was recorded, so `depth == result.size` holds true."
        }
    ]
}

p41 = {
    'num': 41,
    'lc': 1448,
    'title': "Count Good Nodes in Binary Tree",
    'category': "Binary Trees & BST",
    'difficulty': "Medium",
    'lc_slug': "count-good-nodes-in-binary-tree",
    'verbatim_statement': """<p>Given a binary tree <code>root</code>, a node <em>X</em> in the tree is named <strong>good</strong> if in the path from the root to <em>X</em> there are no nodes with a value <em>greater than</em> X.</p><p>Return the number of <strong>good</strong> nodes in the binary tree.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'root = [3,1,4,3,null,1,5]', 'output': '4 (Nodes: 3, 4, 5, 3)'},
        {'title': "Example 2", 'input': 'root = [3,3,null,4,2]', 'output': '3 (Nodes: 3, 3, 4)'},
        {'title': "Example 3", 'input': 'root = [1]', 'output': '1'}
    ],
    'constraints': [
        "The number of nodes in the binary tree is in the range [1, 10^5].",
        "Each node's value is between [-10^4, 10^4]."
    ],
    'plain_english': "A node is 'good' if its value is greater than or equal to the maximum value seen so far on the path from the root. Traverse top-down with DFS, passing `maxSoFar`. If `node.val >= maxSoFar`, increment good count and propagate `max(maxSoFar, node.val)` downward.",
    'boundary_checks': {
        'min_base': "Single node tree <code>[1]</code> &rarr; Root has no ancestors, so it is always good (returns 1).",
        'best_exit': "All strictly increasing path (e.g. 1 &rarr; 2 &rarr; 3) &rarr; Every node is good (returns N).",
        'worst_scan': "All strictly decreasing path (e.g. 5 &rarr; 4 &rarr; 3 &rarr; 1) &rarr; Only root is good (returns 1)."
    },
    'mental_model': {
        'analogy_title': "Mountain Ridge Line-of-Sight",
        'analogy_text': "Imagine descending down a mountain ridge. You only have a clear line of sight back up to the base camp if no higher peak blocks your view. As you hike down, you maintain a record of the highest summit traversed so far (`maxSoFar`). If your current spot is at least as high as `maxSoFar`, you plant a flag (Good Node!).",
        'aha_moment': "You do NOT need to store or search the entire ancestor path! Storing just a single scalar `maxSoFar` passed down the call stack compresses path validation from O(H) to O(1) per node.",
        'trigger': "Condition depends on all ancestors on the path from root &rarr; Top-down DFS passing running aggregate (max, min, bitmask, sum).",
        'sisters': [
            {'name': "Path Sum", 'lc': "LC #112"},
            {'name': "Longest Univalue Path", 'lc': "LC #687"},
            {'name': "Lowest Common Ancestor of a Binary Tree", 'lc': "LC #236"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N * H) Time, O(H) Space",
        'naive_text': "Collect ancestor values in a list during DFS. At each node, iterate through all ancestors in the list to check if any is greater than node.val.",
        'wasted_work': "Re-scanning the entire ancestor array of length H at every single node.",
        'optimal_complexity': "O(N) Time, O(H) Space",
        'optimal_text': "Pass a single integer `maxSoFar` down the recursive call stack. Update with `max(maxSoFar, node.val)` in O(1) per step.",
        'core_invariant': "`maxSoFar` always represents the maximum node value strictly on the simple path from root to the current node.",
        'table_rows': [
            {'name': "1. Full Ancestor Array Check", 'time': "O(N * H)", 'space': "O(H)", 'note': "Scans ancestor list of size up to H at each node."},
            {'name': "2. Iterative BFS with (Node, MaxSoFar) Pairs", 'time': "O(N)", 'space': "O(W)", 'note': "Queue of pairs; safe against call-stack overflow."},
            {'name': "★ 3. Recursive Top-Down DFS with Scalar Max", 'time': "O(N)", 'space': "O(H)", 'note': "O(1) work per node by passing running max scalar."}
        ]
    },
    'trace_table': {
        'input_desc': 'root = [3, 1, 4, 3, null, 1, 5]',
        'columns': ["Node Visited", "Node Value", "Path maxSoFar", "Condition (val >= maxSoFar)", "Is Good?", "New maxSoFar"],
        'rows': [
            ["Root(3)", "3", "3 (initial)", "3 >= 3 (True)", "YES (+1)", "3"],
            ["Node(1) (left of 3)", "1", "3", "1 >= 3 (False)", "NO", "3"],
            ["Node(3) (left of 1)", "3", "3", "3 >= 3 (True)", "YES (+1)", "3"],
            ["Node(4) (right of 3)", "4", "3", "4 >= 3 (True)", "YES (+1)", "4"],
            ["Node(1) (left of 4)", "1", "4", "1 >= 4 (False)", "NO", "4"],
            ["Node(5) (right of 4)", "5", "4", "5 >= 4 (True)", "YES (+1)", "5"]
        ],
        'takeaway': "Total Good Nodes = 4 (values: 3, 3, 4, 5). Single integer state is completely sufficient."
    },
    'stepper': {
        'title': "Running Max Path Traversal Stepper",
        'default_input': '3, 1, 4, 3, null, 1, 5',
        'presets': [
            {'label': "Example [3, 1, 4, 3, null, 1, 5]", 'onclick': 'loadPreset([3, 1, 4, 3, null, 1, 5])'},
            {'label': "Strictly Decreasing [5, 4, null, 3, null, 2]", 'onclick': 'loadPreset([5, 4, null, 3, null, 2])'},
            {'label': "Strictly Increasing [1, null, 2, null, 3]", 'onclick': 'loadPreset([1, null, 2, null, 3])'}
        ],
        'metrics': [
            {'id': "metric-curr", 'label': "Current Node", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-max", 'label': "Path Max So Far", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-count", 'label': "Good Nodes Count", 'color': "text-emerald-400", 'initial': "0"},
            {'id': "metric-status", 'label': "Evaluation", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="good-nodes-stage" class="w-full flex items-center justify-center gap-4 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace running max propagation down the tree branches.',
        'pseudocode_lines': [
            "fun goodNodes(root: TreeNode): Int {",
            "    return dfs(root, root.`val`)",
            "}",
            "fun dfs(node: TreeNode?, maxSoFar: Int): Int {",
            "    if (node == null) return 0",
            "    val isGood = if (node.`val` >= maxSoFar) 1 else 0",
            "    val newMax = maxOf(maxSoFar, node.`val`)",
            "    return isGood + dfs(node.left, newMax) + dfs(node.right, newMax)",
            "}"
        ],
        'js_code': """
    let goodTree = [3, 1, 4, 3, null, 1, 5];

    function buildSteps(arr) {
      steps = [];
      if (!arr || arr.length === 0 || arr[0] === null) return;

      let goodCount = 0;

      function dfs(idx, maxSoFar) {
        if (idx >= arr.length || arr[idx] === null) return;
        let val = arr[idx];
        let isGood = val >= maxSoFar;
        if (isGood) goodCount++;
        let newMax = Math.max(maxSoFar, val);

        steps.push({
          curr: val, maxSoFar: maxSoFar, goodCount: goodCount,
          status: isGood ? "GOOD NODE (+1)" : "SHADOWED",
          isGood: isGood, newMax: newMax,
          line: isGood ? 6 : 7,
          narration: isGood
            ? `Node ${val} >= path max ${maxSoFar} &rarr; GOOD! Count = ${goodCount}. New path max = ${newMax}.`
            : `Node ${val} < path max ${maxSoFar} &rarr; Shadowed by earlier ancestor. Count remains ${goodCount}.`
        });

        let left = 2 * idx + 1;
        let right = 2 * idx + 2;
        dfs(left, newMax);
        dfs(right, newMax);
      }

      dfs(0, arr[0]);

      steps.push({
        curr: "Done", maxSoFar: "-", goodCount: goodCount,
        status: "FINISHED", isGood: false, newMax: "-",
        line: 8,
        narration: `Finished traversal. Total good nodes found: ${goodCount}.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      goodTree = arr;
      stepperReset();
      buildSteps(goodTree);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => {
        let t = s.trim();
        return (t === 'null' || t === '') ? null : parseInt(t);
      });
      if (arr.length > 0) {
        goodTree = arr;
        stepperReset();
        buildSteps(goodTree);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-curr').textContent = step.curr;
      document.getElementById('metric-max').textContent = step.maxSoFar;
      document.getElementById('metric-count').textContent = step.goodCount;
      document.getElementById('metric-status').textContent = step.status;
""",
        'js_render_visual': """
      const stage = document.getElementById('good-nodes-stage');
      stage.innerHTML = '';

      const card = document.createElement('div');
      card.className = `p-4 rounded-xl border flex items-center gap-4 ${
        step.status.indexOf('GOOD') !== -1 ? 'bg-emerald-500/10 border-emerald-500 text-emerald-300' :
        step.status === 'SHADOWED' ? 'bg-rose-500/10 border-rose-500 text-rose-300' :
        'bg-slate-900 border-slate-700 text-slate-300'
      }`;
      card.innerHTML = `
        <div class="text-3xl font-black font-mono">${step.curr}</div>
        <div class="text-xs space-y-1">
          <div>Path Max: <span class="font-bold font-mono">${step.maxSoFar}</span></div>
          <div>Evaluation: <span class="font-bold">${step.status}</span></div>
        </div>
      `;
      stage.appendChild(card);
"""
    },
    'approaches': [
        {
            'name': "Full Ancestor List Passing (Naive)",
            'desc': "Pass a list of all ancestor values down the tree. Check every ancestor for values > node.val.",
            'time': "O(N * H)",
            'space': "O(H)",
            'code_kotlin': """class Solution {
    fun goodNodes(root: TreeNode?): Int {
        if (root == null) return 0
        return dfs(root, mutableListOf())
    }

    private fun dfs(node: TreeNode?, ancestors: MutableList<Int>): Int {
        if (node == null) return 0
        val isGood = if (ancestors.all { it <= node.`val` }) 1 else 0
        ancestors.add(node.`val`)
        val left = dfs(node.left, ancestors)
        val right = dfs(node.right, ancestors)
        ancestors.removeAt(ancestors.size - 1)
        return isGood + left + right
    }
}""",
            'notes_kotlin': "Stores all ancestors on path. Validating all ancestors incurs redundant O(H) overhead per node.",
            'code_python': """class Solution:
    def goodNodes(self, root: TreeNode) -> int:
        def dfs(node, ancestors):
            if not node: return 0
            is_good = 1 if all(a <= node.val for a in ancestors) else 0
            ancestors.append(node.val)
            cnt = is_good + dfs(node.left, ancestors) + dfs(node.right, ancestors)
            ancestors.pop()
            return cnt
        return dfs(root, [])""",
            'notes_python': "Passing path lists leads to quadratic behavior on skewed trees.",
            'code_java': """class Solution {
    public int goodNodes(TreeNode root) {
        if (root == null) return 0;
        return dfs(root, new ArrayList<>());
    }

    private int dfs(TreeNode node, List<Integer> ancestors) {
        if (node == null) return 0;
        boolean good = true;
        for (int a : ancestors) {
            if (a > node.val) { good = false; break; }
        }
        int count = good ? 1 : 0;
        ancestors.add(node.val);
        count += dfs(node.left, ancestors);
        count += dfs(node.right, ancestors);
        ancestors.remove(ancestors.size() - 1);
        return count;
    }
}""",
            'notes_java': "Wasteful list allocations and linear lookups at each step."
        },
        {
            'name': "Iterative BFS with (Node, MaxSoFar) Pairs",
            'desc': "Queue BFS storing pairs of (node, maxSoFar). Safe against stack overflow on skewed trees.",
            'time': "O(N)",
            'space': "O(W) <= O(N)",
            'code_kotlin': """class Solution {
    fun goodNodes(root: TreeNode?): Int {
        if (root == null) return 0
        var count = 0
        val queue = ArrayDeque<Pair<TreeNode, Int>>()
        queue.add(Pair(root, root.`val`))

        while (queue.isNotEmpty()) {
            val (node, maxSoFar) = queue.removeFirst()
            if (node.`val` >= maxSoFar) count++
            val newMax = maxOf(maxSoFar, node.`val`)
            node.left?.let { queue.add(Pair(it, newMax)) }
            node.right?.let { queue.add(Pair(it, newMax)) }
        }
        return count
    }
}""",
            'notes_kotlin': "Iterative approach storing state in the heap queue. Immune to call stack recursion limits.",
            'code_python': """class Solution:
    def goodNodes(self, root: TreeNode) -> int:
        if not root:
            return 0
        count = 0
        queue = collections.deque([(root, root.val)])

        while queue:
            node, max_so_far = queue.popleft()
            if node.val >= max_so_far:
                count += 1
            new_max = max(max_so_far, node.val)
            if node.left:
                queue.append((node.left, new_max))
            if node.right:
                queue.append((node.right, new_max))
                
        return count""",
            'notes_python': "Uses `collections.deque` with lightweight 2-tuples.",
            'code_java': """class Solution {
    record Pair(TreeNode node, int maxSoFar) {}

    public int goodNodes(TreeNode root) {
        if (root == null) return 0;
        int count = 0;
        Queue<Pair> queue = new ArrayDeque<>();
        queue.offer(new Pair(root, root.val));

        while (!queue.isEmpty()) {
            Pair curr = queue.poll();
            TreeNode node = curr.node;
            int maxSoFar = curr.maxSoFar;
            if (node.val >= maxSoFar) count++;
            int newMax = Math.max(maxSoFar, node.val);
            if (node.left != null) queue.offer(new Pair(node.left, newMax));
            if (node.right != null) queue.offer(new Pair(node.right, newMax));
        }
        return count;
    }
}""",
            'notes_java': "Java 17+ record `Pair` provides compact tuple encapsulation."
        },
        {
            'name': "Recursive Top-Down DFS with Scalar Max (Optimal Primary)",
            'desc': "Pass running maximum value down recursive call stack. Compute result bottom-up.",
            'time': "O(N)",
            'space': "O(H) Call Stack",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Top-Down DFS Passing Running Maximum
     * Time: O(N) - Every node visited once
     * Space: O(H) - Recursion stack bounded by tree height
     */
    fun goodNodes(root: TreeNode?): Int {
        if (root == null) return 0
        return dfs(root, root.`val`)
    }

    private fun dfs(node: TreeNode?, maxSoFar: Int): Int {
        if (node == null) return 0

        val isGood = if (node.`val` >= maxSoFar) 1 else 0
        val newMax = maxOf(maxSoFar, node.`val`)

        return isGood + dfs(node.left, newMax) + dfs(node.right, newMax)
    }
}""",
            'notes_kotlin': "Passing primitive `maxSoFar` by value means sibling calls do not pollute each other's state.",
            'code_python': """class Solution:
    # Optimal: Top-Down DFS Passing Running Maximum
    # Time: O(N) - Visits each node once
    # Space: O(H) - Call stack proportional to tree height
    def goodNodes(self, root: TreeNode) -> int:
        def dfs(node: TreeNode, max_so_far: int) -> int:
            if not node:
                return 0
            is_good = 1 if node.val >= max_so_far else 0
            new_max = max(max_so_far, node.val)
            return is_good + dfs(node.left, new_max) + dfs(node.right, new_max)
            
        return dfs(root, root.val)""",
            'notes_python': "Textbook optimal solution. Extremely clean recursion with zero mutable global variables.",
            'code_java': """class Solution {
    /**
     * Optimal: Top-Down DFS with Scalar Max
     * Time: O(N) - Linear single-pass traversal
     * Space: O(H) - Recursion stack
     */
    public int goodNodes(TreeNode root) {
        if (root == null) return 0;
        return dfs(root, root.val);
    }

    private int dfs(TreeNode node, int maxSoFar) {
        if (node == null) return 0;

        int isGood = (node.val >= maxSoFar) ? 1 : 0;
        int newMax = Math.max(maxSoFar, node.val);

        return isGood + dfs(node.left, newMax) + dfs(node.right, newMax);
    }
}""",
            'notes_java': "Stack frames automatically backtrack `maxSoFar` upon returning from subtrees."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun goodNodes(root: TreeNode?): Int {
        fun dfs(node: TreeNode?, maxSoFar: Int): Int {
            if (node == null) return 0
            val good = if (node.`val` >= maxSoFar) 1 else 0
            val m = maxOf(maxSoFar, node.`val`)
            return good + dfs(node.left, m) + dfs(node.right, m)
        }
        return if (root == null) 0 else dfs(root, root.`val`)
    }
}""",
        'python_boilerplate': """class Solution:
    def goodNodes(self, root: TreeNode) -> int:
        def dfs(node, max_val):
            if not node: return 0
            good = 1 if node.val >= max_val else 0
            m = max(max_val, node.val)
            return good + dfs(node.left, m) + dfs(node.right, m)
        return dfs(root, root.val) if root else 0""",
        'java_boilerplate': """class Solution {
    public int goodNodes(TreeNode root) {
        if (root == null) return 0;
        return dfs(root, root.val);
    }
    private int dfs(TreeNode node, int maxVal) {
        if (node == null) return 0;
        int good = (node.val >= maxVal) ? 1 : 0;
        int m = Math.max(maxVal, node.val);
        return good + dfs(node.left, m) + dfs(node.right, m);
    }
}""",
        'js_test_runner': """
      const testCases = [
        { tree: [3,1,4,3,null,1,5], expected: 4 },
        { tree: [3,3,null,4,2], expected: 3 },
        { tree: [1], expected: 1 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function countGood(arr) {
          if (!arr || arr.length === 0 || arr[0] === null) return 0;
          function dfs(i, maxVal) {
            if (i >= arr.length || arr[i] === null) return 0;
            let val = arr[i];
            let good = (val >= maxVal) ? 1 : 0;
            let m = Math.max(maxVal, val);
            return good + dfs(2 * i + 1, m) + dfs(2 * i + 2, m);
          }
          return dfs(0, arr[0]);
        }

        const out = countGood(tc.tree);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: tree=[${tc.tree.join(',')}]</span>
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
        'phase1': "I clarify: Can node values be negative? Yes, constraints say -10^4 <= val <= 10^4, so we must initialize maxSoFar to root.val, NOT 0. Can the root be considered good? Yes, root has no ancestors, so it is always good.",
        'phase2': "For strategy: Storing all ancestors in a list costs O(H) work per node. We only need the maximum ancestor value. Passing `maxSoFar` down the recursion tree evaluates `node.val >= maxSoFar` in O(1) time and updates `newMax = max(maxSoFar, node.val)` for children. Time is O(N) and space is O(H).",
        'phase3': "I implement `dfs(node, maxSoFar)`: if node is null return 0. Calculate `isGood = 1 if node.val >= maxSoFar else 0`. Calculate `newMax = max(maxSoFar, node.val)`. Return `isGood + dfs(node.left, newMax) + dfs(node.right, newMax)`. Kick off recursion with `dfs(root, root.val)`.",
        'phase4': "Dry run: root=3 (maxSoFar=3, good=1). Left child=1 (1 >= 3 is False, good=0, newMax=3). Descendant=3 (3 >= 3 is True, good=1). Right child=4 (4 >= 3 is True, good=1, newMax=4). Total good count = 4. Negative values and single root cases execute correctly.",
        'senior_followup': "Senior Scalability: If the binary tree is serialized as an adjacency list over an object store (e.g. S3), we can compute good nodes via MapReduce by keying path prefixes and mapping prefix max reductions."
    },
    'quiz': [
        {
            'question': "Why should `maxSoFar` be initialized with `root.val` rather than `0`?",
            'options': [
                "Because 0 is not a valid node value.",
                "Because node values can be negative (e.g., -10,000); initializing with 0 would incorrectly shadow valid negative root values.",
                "Because root.val is always the largest node in a binary tree.",
                "It avoids integer overflow."
            ],
            'correct_index': 1,
            'explanation': "If all nodes are negative (e.g. -5, -10), initializing with 0 would cause the root to compare `-5 >= 0` (False), mistakenly treating the root as not good."
        },
        {
            'question': "Why does passing `maxSoFar` as an argument by value avoid manual backtracking undo operations?",
            'options': [
                "Because primitive numbers are copied onto each stack frame; when a recursive call returns, the caller's stack frame still retains its original maxSoFar value untouched.",
                "Because binary trees prevent variable mutation by language specification.",
                "Because garbage collection restores old values automatically.",
                "Because tree nodes are visited in reverse topological order."
            ],
            'correct_index': 0,
            'explanation': "Stack activation records isolate primitive arguments per frame. Returning from the left subtree does not affect the `maxSoFar` value held in the parent frame for the right subtree call."
        }
    ]
}
