import sys, re
sys.path.append('scripts')
from template_engine import render_lesson

p39 = {
    'num': 39,
    'lc': 102,
    'title': "Binary Tree Level Order Traversal",
    'category': "Binary Trees & BST",
    'difficulty': "Medium",
    'lc_slug': "binary-tree-level-order-traversal",
    'verbatim_statement': """<p>Given the <code>root</code> of a binary tree, return <em>the level order traversal of its nodes' values</em>. (i.e., from left to right, level by level).</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'root = [3,9,20,null,null,15,7]', 'output': '[[3],[9,20],[15,7]]'},
        {'title': "Example 2", 'input': 'root = [1]', 'output': '[[1]]'},
        {'title': "Example 3", 'input': 'root = []', 'output': '[]'}
    ],
    'constraints': [
        "The number of nodes in the tree is in the range [0, 2000].",
        "-1000 <= Node.val <= 1000"
    ],
    'plain_english': "Traverse the tree layer by layer from top to bottom and left to right. Maintain a queue. For each level, snapshot the current queue size, pop that exact number of nodes, append their values to the level list, and push their children into the queue for the next level.",
    'boundary_checks': {
        'min_base': "<code>root == null</code> &rarr; Empty tree returns empty list <code>[]</code>.",
        'best_exit': "Single node tree <code>root = [1]</code> &rarr; Immediately returns <code>[[1]]</code>.",
        'worst_scan': "Completely unbalanced (skewed) tree of depth N &rarr; N tiers with 1 node per tier; queue size stays 1."
    },
    'mental_model': {
        'analogy_title': "Airport Boarding Waves",
        'analogy_text': "Think of passengers lining up for flight boarding. When Group 1 is called, the gate agent counts exactly how many passengers are in line right then (`val size = queue.size`). Exactly that batch walks down the jet bridge together. As each passenger boards, their family members line up behind the barrier for Group 2. Group 1 finishes before Group 2 begins.",
        'aha_moment': "Freezing `val size = queue.size` at the top of the while-loop naturally chunks continuous FIFO queue streaming into discrete horizontal tiers without needing custom depth wrappers.",
        'trigger': "Level-by-level tree processing, shortest paths in unweighted graphs, row-wise aggregation &rarr; Queue BFS with `size` snapshot loop.",
        'sisters': [
            {'name': "Binary Tree Zigzag Level Order Traversal", 'lc': "LC #103"},
            {'name': "Binary Tree Right Side View", 'lc': "LC #199"},
            {'name': "Average of Levels in Binary Tree", 'lc': "LC #637"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(N) Space",
        'naive_text': "Compute height H, then for each d from 1 to H run a separate DFS printing nodes at depth d from scratch.",
        'wasted_work': "Re-traversing ancestor nodes up to N times.",
        'optimal_complexity': "O(N) Time, O(W) <= O(N) Space",
        'optimal_text': "Queue BFS snapshot loop. Every node is enqueued and dequeued exactly once.",
        'core_invariant': "At the start of the outer while-loop, the queue contains exclusively the nodes belonging to level d.",
        'table_rows': [
            {'name': "1. Repeated Depth-Scoped DFS", 'time': "O(N^2)", 'space': "O(N)", 'note': "Scans depth d repeatedly from root."},
            {'name': "2. Recursive Pre-order DFS with Depth Index", 'time': "O(N)", 'space': "O(H)", 'note': "Passes depth index; appends to result[depth]."},
            {'name': "★ 3. Queue BFS with Snapshot Size", 'time': "O(N)", 'space': "O(W) <= O(N)", 'note': "Textbook BFS pattern; optimal row-by-row streaming."}
        ]
    },
    'trace_table': {
        'input_desc': 'root = [3, 9, 20, null, null, 15, 7]',
        'columns': ["Outer Loop", "Queue Snapshot Size", "Nodes Popped", "Children Queued", "Result Array"],
        'rows': [
            ["Init", "1 (Queue: [3])", "-", "-", "[]"],
            ["Level 0", "1", "[3]", "[9, 20]", "[[3]]"],
            ["Level 1", "2", "[9, 20]", "[15, 7]", "[[3], [9, 20]]"],
            ["Level 2", "2", "[15, 7]", "[] (leaves)", "[[3], [9, 20], [15, 7]]"],
            ["Exit", "0 (Queue empty)", "-", "-", "Finished: 3 levels"]
        ],
        'takeaway': "Total nodes popped equals N = 5. Snapshot `levelSize` prevents newly enqueued children from leaking into the current row."
    },
    'stepper': {
        'title': "Queue Snapshot BFS Stepper",
        'default_input': '3, 9, 20, null, null, 15, 7',
        'presets': [
            {'label': "Classic [3,9,20,15,7]", 'onclick': 'loadPreset([3,9,20,null,null,15,7])'},
            {'label': "Single Node [1]", 'onclick': 'loadPreset([1])'},
            {'label': "Skewed [1,2,null,3]", 'onclick': 'loadPreset([1,2,null,3])'}
        ],
        'metrics': [
            {'id': "metric-level", 'label': "Current Level", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-qsize", 'label': "Snapshot Size", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-current", 'label': "Current Row", 'color': "text-emerald-400", 'initial': "[]"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="bfs-tree-stage" class="w-full flex flex-col items-center justify-center gap-3 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace BFS queue snapshotting tier by tier.',
        'pseudocode_lines': [
            "if (root == null) return emptyList()",
            "val queue = ArrayDeque<TreeNode>().apply { add(root) }",
            "while (queue.isNotEmpty()) {",
            "    val size = queue.size; val level = ArrayList<Int>(size)",
            "    for (i in 0 until size) {",
            "        val node = queue.removeFirst(); level.add(node.`val`)",
            "        node.left?.let { queue.add(it) }",
            "        node.right?.let { queue.add(it) }",
            "    }",
            "    result.add(level)",
            "}"
        ],
        'js_code': """
    let rawTree = [3, 9, 20, null, null, 15, 7];

    function buildSteps(arr) {
      steps = [];
      if (!arr || arr.length === 0 || arr[0] === null) {
        steps.push({
          level: 0, qsize: 0, current: "[]", action: "Empty Tree",
          levels: [], q: [], line: 1, narration: "Tree is empty, returning []."
        });
        return;
      }

      let q = [{ val: arr[0], idx: 0 }];
      let levels = [];
      let lvlNum = 0;

      steps.push({
        level: 0, qsize: 1, current: "[]", action: "Enqueue Root",
        levels: [], q: [...q], line: 2,
        narration: `Enqueued root node ${arr[0]}.`
      });

      while (q.length > 0) {
        let size = q.length;
        let currRow = [];

        steps.push({
          level: lvlNum, qsize: size, current: "[]",
          action: `Snapshot size = ${size}`,
          levels: [...levels], q: [...q], line: 4,
          narration: `Level ${lvlNum}: Queue size snapshot is ${size}. Exactly ${size} nodes will be popped.`
        });

        for (let i = 0; i < size; i++) {
          let node = q.shift();
          currRow.push(node.val);

          let leftIdx = 2 * node.idx + 1;
          let rightIdx = 2 * node.idx + 2;
          if (leftIdx < arr.length && arr[leftIdx] !== null) q.push({ val: arr[leftIdx], idx: leftIdx });
          if (rightIdx < arr.length && arr[rightIdx] !== null) q.push({ val: arr[rightIdx], idx: rightIdx });

          steps.push({
            level: lvlNum, qsize: size, current: `[${currRow.join(', ')}]`,
            action: `Popped ${node.val}`,
            levels: [...levels], q: [...q], line: 6,
            narration: `Popped node ${node.val}, added its children to back of queue.`
          });
        }

        levels.push([...currRow]);
        steps.push({
          level: lvlNum, qsize: size, current: `[${currRow.join(', ')}]`,
          action: `Level ${lvlNum} Complete`,
          levels: [...levels], q: [...q], line: 10,
          narration: `Committed level ${lvlNum}: [${currRow.join(', ')}] to result.`
        });
        lvlNum++;
      }
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      rawTree = arr;
      stepperReset();
      buildSteps(rawTree);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => {
        let t = s.trim();
        return (t === 'null' || t === '') ? null : parseInt(t);
      });
      if (arr.length > 0) {
        rawTree = arr;
        stepperReset();
        buildSteps(rawTree);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-level').textContent = step.level;
      document.getElementById('metric-qsize').textContent = step.qsize;
      document.getElementById('metric-current').textContent = step.current;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('bfs-tree-stage');
      stage.innerHTML = '';

      const wrap = document.createElement('div');
      wrap.className = 'w-full flex flex-col gap-2 items-center';

      // Show committed tiers
      step.levels.forEach((row, idx) => {
        const rEl = document.createElement('div');
        rEl.className = 'flex items-center gap-2 text-xs font-mono';
        rEl.innerHTML = `<span class="text-slate-500 font-bold w-16 text-right">Lvl ${idx}:</span>` +
          row.map(v => `<span class="px-2.5 py-1 bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 rounded font-bold">${v}</span>`).join(' ');
        wrap.appendChild(rEl);
      });

      // Show Queue
      const qEl = document.createElement('div');
      qEl.className = 'flex items-center gap-2 text-xs font-mono mt-3 p-2 bg-slate-950 border border-slate-800 rounded-lg';
      qEl.innerHTML = `<span class="text-cyan-400 font-bold">Queue:</span> [${step.q.map(x => x.val).join(', ')}]`;
      wrap.appendChild(qEl);

      stage.appendChild(wrap);
"""
    },
    'approaches': [
        {
            'name': "Repeated Depth-Scoped DFS (Naive)",
            'desc': "Calculate maximum depth, then run a separate recursive traversal for depth 0 to maxDepth.",
            'time': "O(N^2)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun levelOrder(root: TreeNode?): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val height = getHeight(root)
        for (d in 0 until height) {
            val level = mutableListOf<Int>()
            collectLevel(root, d, level)
            result.add(level)
        }
        return result
    }

    private fun getHeight(node: TreeNode?): Int {
        if (node == null) return 0
        return 1 + maxOf(getHeight(node.left), getHeight(node.right))
    }

    private fun collectLevel(node: TreeNode?, depthRemaining: Int, level: MutableList<Int>) {
        if (node == null) return
        if (depthRemaining == 0) {
            level.add(node.`val`)
            return
        }
        collectLevel(node.left, depthRemaining - 1, level)
        collectLevel(node.right, depthRemaining - 1, level)
    }
}""",
            'notes_kotlin': "Naive multi-pass. Scans ancestor nodes repeatedly for each depth level.",
            'code_python': """class Solution:
    def levelOrder(self, root: Optional[TreeNode]) -> List[List[int]]:
        def get_height(node):
            if not node: return 0
            return 1 + max(get_height(node.left), get_height(node.right))
            
        def collect(node, d, row):
            if not node: return
            if d == 0:
                row.append(node.val)
                return
            collect(node.left, d - 1, row)
            collect(node.right, d - 1, row)
            
        h = get_height(root)
        res = []
        for d in range(h):
            row = []
            collect(root, d, row)
            res.append(row)
        return res""",
            'notes_python': "Repeats traversal H times. Degrades to O(N^2) on skewed trees.",
            'code_java': """class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        int h = getHeight(root);
        for (int d = 0; d < h; d++) {
            List<Integer> row = new ArrayList<>();
            collect(root, d, row);
            result.add(row);
        }
        return result;
    }

    private int getHeight(TreeNode node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    private void collect(TreeNode node, int d, List<Integer> row) {
        if (node == null) return;
        if (d == 0) {
            row.add(node.val);
            return;
        }
        collect(node.left, d - 1, row);
        collect(node.right, d - 1, row);
    }
}""",
            'notes_java': "High constant factor due to redundant subtree visits."
        },
        {
            'name': "Recursive Pre-order DFS with Depth Index",
            'desc': "Traverse tree recursively, passing current depth. Append each node to result[depth].",
            'time': "O(N)",
            'space': "O(H) Call Stack",
            'code_kotlin': """class Solution {
    fun levelOrder(root: TreeNode?): List<List<Int>> {
        val result = mutableListOf<MutableList<Int>>()
        dfs(root, 0, result)
        return result
    }

    private fun dfs(node: TreeNode?, depth: Int, result: MutableList<MutableList<Int>>) {
        if (node == null) return
        if (depth == result.size) {
            result.add(mutableListOf())
        }
        result[depth].add(node.`val`)
        dfs(node.left, depth + 1, result)
        dfs(node.right, depth + 1, result)
    }
}""",
            'notes_kotlin': "Visits left child before right child, preserving left-to-right order at each depth level.",
            'code_python': """class Solution:
    def levelOrder(self, root: Optional[TreeNode]) -> List[List[int]]:
        res = []
        def dfs(node, depth):
            if not node: return
            if depth == len(res):
                res.append([])
            res[depth].append(node.val)
            dfs(node.left, depth + 1)
            dfs(node.right, depth + 1)
        dfs(root, 0)
        return res""",
            'notes_python': "Compact DFS alternative. Stack space is O(H), saving memory on wide trees.",
            'code_java': """class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        dfs(root, 0, result);
        return result;
    }

    private void dfs(TreeNode node, int depth, List<List<Integer>> result) {
        if (node == null) return;
        if (depth == result.size()) {
            result.add(new ArrayList<>());
        }
        result.get(depth).add(node.val);
        dfs(node.left, depth + 1, result);
        dfs(node.right, depth + 1, result);
    }
}""",
            'notes_java': "Clean recursive formulation that automatically groups tiers by depth."
        },
        {
            'name': "Queue BFS with Snapshot Size (Optimal Primary)",
            'desc': "Standard Breadth-First Search using a Queue. Snapshot size at the beginning of each level.",
            'time': "O(N)",
            'space': "O(W) <= O(N)",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Queue BFS with Snapshot Level Size
     * Time: O(N) - Every node is queued and dequeued once
     * Space: O(W) - Max width of tree in queue (up to N/2)
     */
    fun levelOrder(root: TreeNode?): List<List<Int>> {
        if (root == null) return emptyList()
        val result = mutableListOf<List<Int>>()
        val queue = ArrayDeque<TreeNode>()
        queue.add(root)

        while (queue.isNotEmpty()) {
            val levelSize = queue.size
            val currentLevel = ArrayList<Int>(levelSize)

            for (i in 0 until levelSize) {
                val node = queue.removeFirst()
                currentLevel.add(node.`val`)
                node.left?.let { queue.add(it) }
                node.right?.let { queue.add(it) }
            }

            result.add(currentLevel)
        }

        return result
    }
}""",
            'notes_kotlin': "`ArrayDeque` provides zero-allocation FIFO operations in Kotlin/Java. `levelSize` isolates the layer.",
            'code_python': """class Solution:
    # Optimal: Queue BFS with Snapshot Level Size
    # Time: O(N) - Visits each node once
    # Space: O(W) - Max tree width in deque
    def levelOrder(self, root: Optional[TreeNode]) -> List[List[int]]:
        if not root:
            return []
        result = []
        queue = collections.deque([root])

        while queue:
            level_size = len(queue)
            current_level = []
            for _ in range(level_size):
                node = queue.popleft()
                current_level.append(node.val)
                if node.left:
                    queue.append(node.left)
                if node.right:
                    queue.append(node.right)
            result.append(current_level)
            
        return result""",
            'notes_python': "Using `collections.deque` ensures O(1) `popleft()`. Avoid list `pop(0)` which is O(N).",
            'code_java': """class Solution {
    /**
     * Optimal: Queue BFS with Snapshot Level Size
     * Time: O(N) - Linear scan
     * Space: O(W) - Maximum width of binary tree
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>(levelSize);

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            result.add(currentLevel);
        }

        return result;
    }
}""",
            'notes_java': "`ArrayDeque` is significantly faster than `LinkedList` as a queue in Java due to cache locality."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun levelOrder(root: TreeNode?): List<List<Int>> {
        if (root == null) return emptyList()
        val result = mutableListOf<List<Int>>()
        val queue = ArrayDeque<TreeNode>()
        queue.add(root)
        while (queue.isNotEmpty()) {
            val size = queue.size
            val level = ArrayList<Int>(size)
            for (i in 0 until size) {
                val node = queue.removeFirst()
                level.add(node.`val`)
                node.left?.let { queue.add(it) }
                node.right?.let { queue.add(it) }
            }
            result.add(level)
        }
        return result
    }
}""",
        'python_boilerplate': """class Solution:
    def levelOrder(self, root: Optional[TreeNode]) -> List[List[int]]:
        if not root: return []
        res, q = [], collections.deque([root])
        while q:
            size = len(q)
            row = []
            for _ in range(size):
                n = q.popleft()
                row.append(n.val)
                if n.left: q.append(n.left)
                if n.right: q.append(n.right)
            res.append(row)
        return res""",
        'java_boilerplate': """class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;
        Queue<TreeNode> q = new ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty()) {
            int size = q.size();
            List<Integer> row = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                TreeNode n = q.poll();
                row.add(n.val);
                if (n.left != null) q.offer(n.left);
                if (n.right != null) q.offer(n.right);
            }
            res.add(row);
        }
        return res;
    }
}""",
        'js_test_runner': """
      const testCases = [
        { tree: [3,9,20,null,null,15,7], expected: [[3],[9,20],[15,7]] },
        { tree: [1], expected: [[1]] },
        { tree: [], expected: [] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runBFS(arr) {
          if (!arr || arr.length === 0 || arr[0] === null) return [];
          let q = [{ val: arr[0], idx: 0 }];
          let res = [];
          while (q.length > 0) {
            let size = q.length;
            let row = [];
            for (let i = 0; i < size; i++) {
              let node = q.shift();
              row.push(node.val);
              let l = 2 * node.idx + 1, r = 2 * node.idx + 2;
              if (l < arr.length && arr[l] !== null) q.push({ val: arr[l], idx: l });
              if (r < arr.length && arr[r] !== null) q.push({ val: arr[r], idx: r });
            }
            res.push(row);
          }
          return res;
        }

        const out = runBFS(tc.tree);
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
        'phase1': "I clarify: can root be null? Yes, return an empty list. What is the maximum node count? Up to 2,000 nodes. Are we returning values grouped strictly by horizontal level from left to right? Yes, each tier forms a sublist.",
        'phase2': "For strategy: Breadth-First Search using a Queue is the ideal approach. To partition nodes into separate rows without tracking depth metadata on each node, we snapshot `val size = queue.size` at the top of each while loop. That loop processes exactly `size` nodes for the current level while queuing their children for the next level. Time is O(N) and space is O(W).",
        'phase3': "I implement BFS with `ArrayDeque`. First check if root is null. Then while `queue.isNotEmpty()`, snapshot `val levelSize = queue.size`. Allocate an `ArrayList(levelSize)`, loop `levelSize` times polling from the head, add values, and enqueue non-null left and right children. Finally add the level list to result and return.",
        'phase4': "Dry running with `[3, 9, 20, null, null, 15, 7]`: Queue starts with [3], size=1, pops 3, enqueues 9 and 20. Next iteration size=2, pops 9 (no children), pops 20 (enqueues 15, 7). Next iteration size=2, pops 15 and 7. Result is `[[3], [9, 20], [15, 7]]`. Edge cases like single root [1] and null root return cleanly.",
        'senior_followup': "Senior Scalability: In distributed graph processing (e.g. Apache Spark GraphX or Pregel), level-order traversal is expressed as bulk-synchronous parallel (BSP) supersteps, where all nodes at level d compute in parallel before exchanging frontier messages for level d+1."
    },
    'quiz': [
        {
            'question': "Why is freezing `levelSize = queue.size()` necessary at the start of each BFS level loop?",
            'options': [
                "It prevents memory garbage collection overhead.",
                "It freezes the count of nodes belonging to the current horizontal tier so newly enqueued child nodes are not processed in the same row.",
                "It ensures nodes are sorted by their integer values.",
                "It enables parallel thread execution."
            ],
            'correct_index': 1,
            'explanation': "Without snapshotting `queue.size`, enqueuing children would continuously enlarge the queue, collapsing the entire tree into a single flat tier."
        },
        {
            'question': "What is the maximum space complexity of BFS on a balanced binary tree with N nodes?",
            'options': [
                "O(1)",
                "O(log N)",
                "O(N) (specifically ceil(N/2) leaf nodes at the widest tier)",
                "O(N^2)"
            ],
            'correct_index': 2,
            'explanation': "The bottom level of a full binary tree contains approximately N/2 nodes, meaning the queue must hold O(N) nodes simultaneously."
        }
    ]
}
