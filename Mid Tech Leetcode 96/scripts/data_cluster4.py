import sys, re
sys.path.append('scripts')
from template_engine import render_lesson

cluster4_problems = [
    # ---------------- 0039: BINARY TREE LEVEL ORDER TRAVERSAL ----------------
    {
        'num': 39,
        'lc': 102,
        'title': "Binary Tree Level Order Traversal",
        'category': "Binary Trees & BST",
        'difficulty': "Medium",
        'lc_slug': "binary-tree-level-order-traversal",
        'verbatim_statement': """<p>Given the <code>root</code> of a binary tree, return <em>the level-order traversal of its nodes' values</em>. (i.e., from left to right, level by level).</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'root = [3,9,20,null,null,15,7]', 'output': '[[3],[9,20],[15,7]]'},
            {'title': "Example 2", 'input': 'root = [1]', 'output': '[[1]]'},
            {'title': "Example 3", 'input': 'root = []', 'output': '[]'}
        ],
        'constraints': [
            "The number of nodes in the tree is in the range [0, 2000].",
            "-1000 <= Node.val <= 1000"
        ],
        'plain_english': "Visit nodes tier by tier, top to bottom, left to right. Maintain a FIFO queue. At each level, snapshot `queue.size` to process exactly that batch of nodes into a sublist before dequeuing children for the next level.",
        'boundary_checks': {
            'min_base': "<code>root == null</code> &rarr; Immediately return empty list <code>[]</code>.",
            'best_exit': "Single-node tree <code>root = [1]</code> &rarr; Returns <code>[[1]]</code> directly.",
            'worst_scan': "Skewed linked-list tree (depth N) &rarr; N levels each with 1 element; queue never exceeds 1 node."
        },
        'mental_model': {
            'analogy_title': "Airport Boarding Groups (Queue Snapshot)",
            'analogy_text': "Passengers for Group 1 line up at the gate. Before boarding begins, the gate agent counts exactly how many passengers are currently in line: `val size = queue.size`. Exactly that many passengers board the plane together as Group 1. As each passenger boards, they register their travel companions into the back of the queue. Those companions don't board now—they form Group 2 for the next announcement.",
            'aha_moment': "Freezing `val size = queue.size` at the start of each level loop partitions continuous stream processing into discrete horizontal tiers without needing auxiliary node-depth wrapper objects.",
            'trigger': "Level-by-level processing, shortest path in unweighted tree/graph, or viewing nodes by row &rarr; Queue BFS with `size` snapshot loop.",
            'sisters': [
                {'name': "Binary Tree Zigzag Level Order Traversal", 'lc': "LC #103"},
                {'name': "Binary Tree Right Side View", 'lc': "LC #199"},
                {'name': "Minimum Depth of Binary Tree", 'lc': "LC #111"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N^2) Time, O(N) Space",
            'naive_text': "For depth d from 0 to maxDepth, run a full DFS search printing nodes at depth d. Scans upper nodes repeatedly.",
            'wasted_work': "Re-traversing ancestor nodes up to N times when a single FIFO queue visits every node exactly once.",
            'optimal_complexity': "O(N) Time, O(W) = O(N) Space",
            'optimal_text': "Breadth-First Search using a Queue. Snapshot size at the beginning of each level. Process each node and its children in O(1) per node.",
            'core_invariant': "At the start of the outer while loop, the queue holds precisely all nodes belonging to level d.",
            'table_rows': [
                {'name': "1. Repeated DFS by Depth", 'time': "O(N^2)", 'space': "O(N)", 'note': "Scans depth d repeatedly from root; severe duplicate visits."},
                {'name': "★ 2. Queue BFS Snapshot", 'time': "O(N)", 'space': "O(W) <= O(N)", 'note': "Visits each node once; W is maximum tree width."}
            ]
        },
        'trace_table': {
            'input_desc': 'root = [3, 9, 20, null, null, 15, 7]',
            'columns': ["Outer Loop", "Snapshot Queue Size", "Level Nodes Extracted", "Next Children Queued", "Result Array"],
            'rows': [
                ["Level 0", "1 (Queue: [3])", "[3]", "[9, 20]", "[[3]]"],
                ["Level 1", "2 (Queue: [9, 20])", "[9, 20]", "[15, 7]", "[[3], [9, 20]]"],
                ["Level 2", "2 (Queue: [15, 7])", "[15, 7]", "[] (all leaf nodes)", "[[3], [9, 20], [15, 7]]"],
                ["End", "0 (Queue is empty)", "-", "-", "Finished: 3 levels"]
            ],
            'takeaway': "Snapshotting `size` isolates each level cleanly. Total iterations equal total nodes N."
        },
        'stepper': {
            'title': "Queue Snapshot BFS Level Stepper",
            'default_input': '3, 9, 20, null, null, 15, 7',
            'presets': [
                {'label': "Classic Tree [3, 9, 20, 15, 7]", 'onclick': 'loadPreset([3, 9, 20, null, null, 15, 7])'},
                {'label': "Single Node [1]", 'onclick': 'loadPreset([1])'},
                {'label': "Skewed Tree [1, 2, 3]", 'onclick': 'loadPreset([1, 2, null, 3])'}
            ],
            'metrics': [
                {'id': "metric-level", 'label': "Current Level", 'color': "text-amber-400", 'initial': "0"},
                {'id': "metric-queue", 'label': "Queue Contents", 'color': "text-cyan-400", 'initial': "[]"},
                {'id': "metric-sublist", 'label': "Current Level Sublist", 'color': "text-emerald-400", 'initial': "[]"},
                {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
            ],
            'visual_html': '<div id="tree-bfs-stage" class="w-full flex flex-col items-center justify-center gap-4 py-4 min-h-[140px]"></div>',
            'initial_narration': 'Click "Next Step" to observe queue snapshotting and level grouping.',
            'pseudocode_lines': [
                "if (root == null) return emptyList()",
                "val queue = ArrayDeque<TreeNode>().apply { add(root) }",
                "while (queue.isNotEmpty()) {",
                "    val size = queue.size; val level = mutableListOf<Int>()",
                "    repeat(size) {",
                "        val node = queue.removeFirst(); level.add(node.`val`)",
                "        node.left?.let { queue.add(it) }",
                "        node.right?.let { queue.add(it) }",
                "    }",
                "    result.add(level)",
                "}"
            ],
            'js_code': """
    let treeData = [3, 9, 20, null, null, 15, 7];

    function buildSteps(arr) {
      steps = [];
      if (!arr || arr.length === 0 || arr[0] === null) {
        steps.push({
          level: 0, queue: "[]", sublist: "[]", action: "Empty Tree",
          levels: [], line: 1, narration: "Root is null, immediately return []."
        });
        return;
      }

      let q = [ { val: arr[0], idx: 0 } ];
      let levels = [];
      let currentLevelNum = 0;

      steps.push({
        level: 0, queue: `[${arr[0]}]`, sublist: "[]", action: "Enqueue Root",
        levels: [], currentQ: [...q], line: 2,
        narration: `Initialized queue with root value ${arr[0]}.`
      });

      while (q.length > 0) {
        let size = q.length;
        let sublist = [];
        steps.push({
          level: currentLevelNum,
          queue: `[${q.map(n => n.val).join(', ')}]`,
          sublist: `[]`,
          action: `Snapshot size = ${size}`,
          levels: [...levels],
          currentQ: [...q],
          line: 4,
          narration: `Level ${currentLevelNum}: snapshot queue.size = ${size}. Will pop exactly ${size} nodes.`
        });

        for (let i = 0; i < size; i++) {
          let curr = q.shift();
          sublist.push(curr.val);

          let leftIdx = 2 * curr.idx + 1;
          let rightIdx = 2 * curr.idx + 2;
          if (leftIdx < arr.length && arr[leftIdx] !== null) {
            q.push({ val: arr[leftIdx], idx: leftIdx });
          }
          if (rightIdx < arr.length && arr[rightIdx] !== null) {
            q.push({ val: arr[rightIdx], idx: rightIdx });
          }

          steps.push({
            level: currentLevelNum,
            queue: `[${q.map(n => n.val).join(', ')}]`,
            sublist: `[${sublist.join(', ')}]`,
            action: `Popped ${curr.val}`,
            levels: [...levels],
            currentQ: [...q],
            line: 6,
            narration: `Popped node ${curr.val}, pushed available children into queue.`
          });
        }

        levels.push([...sublist]);
        steps.push({
          level: currentLevelNum,
          queue: `[${q.map(n => n.val).join(', ')}]`,
          sublist: `[${sublist.join(', ')}]`,
          action: `Level ${currentLevelNum} Complete`,
          levels: [...levels],
          currentQ: [...q],
          line: 10,
          narration: `Completed Level ${currentLevelNum}: [${sublist.join(', ')}] committed to final result.`
        });
        currentLevelNum++;
      }
    }

    function renderStep(step) {
      document.getElementById('metric-level').textContent = step.level;
      document.getElementById('metric-queue').textContent = step.queue;
      document.getElementById('metric-sublist').textContent = step.sublist;
      document.getElementById('metric-action').textContent = step.action;

      const stage = document.getElementById('tree-bfs-stage');
      stage.innerHTML = '';

      const wrap = document.createElement('div');
      wrap.className = 'w-full flex flex-col gap-2 items-center';

      // Render levels committed so far
      step.levels.forEach((lvl, idx) => {
        const row = document.createElement('div');
        row.className = 'flex items-center gap-2 text-xs font-mono';
        row.innerHTML = `<span class="text-zinc-500 font-bold w-16 text-right">Lvl ${idx}:</span>` +
          lvl.map(v => `<span class="px-2 py-1 bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 rounded">${v}</span>`).join(' ');
        wrap.appendChild(row);
      });

      // Active row
      if (step.sublist !== '[]' && step.action.indexOf('Complete') === -1) {
        const activeRow = document.createElement('div');
        activeRow.className = 'flex items-center gap-2 text-xs font-mono mt-1';
        activeRow.innerHTML = `<span class="text-amber-400 font-bold w-16 text-right">Current:</span> <span class="text-amber-300 bg-amber-500/20 px-2 py-1 rounded border border-amber-500/40">${step.sublist}</span>`;
        wrap.appendChild(activeRow);
      }

      stage.appendChild(wrap);
    }

    function loadPreset(arr) {
      treeData = arr;
      buildSteps(treeData);
      currentStepIndex = 0;
      updateUI();
    }

    window.addEventListener('load', () => {
      buildSteps(treeData);
      updateUI();
    });
"""
        },
        'approaches': [
            {
                'name': "Approach 1: Queue Snapshot Breadth-First Search (Optimal)",
                'desc': "Standard queue BFS. Snapshot `queue.size` at the top of each level loop to process each tier atomically.",
                'time': "O(N)",
                'space': "O(W) <= O(N)",
                'kotlin': """class Solution {
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
                'python': """class Solution:
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
                'java': """class Solution {
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
                'explanation': "By capturing `levelSize = queue.size()` before the inner loop, all child nodes added inside the loop wait until the next level iteration. Memory corresponds to maximum width W (up to N/2 nodes on bottom tier)."
            },
            {
                'name': "Approach 2: Depth-First Search with Level Depth Index",
                'desc': "Traverse tree recursively using pre-order (root, left, right), passing current depth. Append to result[depth].",
                'time': "O(N)",
                'space': "O(H) Recursion Stack",
                'kotlin': """class Solution {
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
                'python': """class Solution:
    def levelOrder(self, root: Optional[TreeNode]) -> List[List[int]]:
        result = []
        def dfs(node: Optional[TreeNode], depth: int):
            if not node:
                return
            if depth == len(result):
                result.append([])
            result[depth].append(node.val)
            dfs(node.left, depth + 1)
            dfs(node.right, depth + 1)
            
        dfs(root, 0)
        return result""",
                'java': """class Solution {
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
                'explanation': "DFS naturally visits nodes from left to right at each depth because left subtree is explored before right subtree. Stack space is O(H), saving memory on wide trees."
            }
        ],
        'scratchpad': {
            'initial_code': """class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def levelOrder(root):
    if not root:
        return []
    import collections
    res = []
    q = collections.deque([root])
    while q:
        size = len(q)
        level = []
        for _ in range(size):
            node = q.popleft()
            level.append(node.val)
            if node.left: q.append(node.left)
            if node.right: q.append(node.right)
        res.append(level)
    return res

# Test tree: 3 -> (9, 20 -> (15, 7))
t = TreeNode(3, TreeNode(9), TreeNode(20, TreeNode(15), TreeNode(7)))
print("Output:", levelOrder(t))
""",
            'test_cases_json': """[
  {"input": "root = [3,9,20,null,null,15,7]", "expected": "[[3],[9,20],[15,7]]"},
  {"input": "root = [1]", "expected": "[[1]]"},
  {"input": "root = []", "expected": "[]"}
]"""
        },
        'interview_dialogue': {
            'opening': """<p><strong>Candidate:</strong> "For level-order traversal, we need to visit nodes row by row from top to bottom. Standard BFS using a FIFO queue is the textbook mechanism for tier-based discovery."</p>
<p><strong>Interviewer:</strong> "Good. How will you separate each row into its own independent sublist rather than returning one flat list?"</p>
<p><strong>Candidate:</strong> "Instead of tagging nodes with depth metadata objects, we snapshot <code>val size = queue.size</code> at the beginning of each while-loop iteration. That loop dequeues exactly <code>size</code> nodes for the current level, while any enqueued children are held for the next round."</p>""",
            'follow_ups': [
                {
                    'question': "What is the memory trade-off between BFS and DFS for this problem?",
                    'answer': "BFS holds the maximum width (up to N/2 nodes in a complete binary tree). DFS only holds the call stack of maximum depth H (log N balanced, N worst). On extremely wide trees with shallow depth, DFS uses less memory."
                },
                {
                    'question': "How would you implement Zigzag Level Order (alternating left-right and right-left)?",
                    'answer': "Use a boolean flag `isReverse`. If true, either reverse the sublist before adding to `result`, or insert into a `LinkedList` using `addFirst()` instead of `addLast()`."
                }
            ]
        },
        'mcqs': [
            {
                'question': "Why is snapshotting `queue.size` before the inner loop essential in BFS level traversal?",
                'options': [
                    "(Recommended) It freezes the count of nodes belonging strictly to the current level so newly enqueued children aren't consumed prematurely.",
                    "It prevents the queue from running out of memory in garbage collection.",
                    "It sorts the nodes in ascending order by value.",
                    "It ensures binary search properties are maintained."
                ],
                'correct': 0,
                'explanation': "Without the snapshot, newly pushed children would increase queue size dynamically, causing the loop to blend multiple levels into a single flat batch."
            },
            {
                'question': "What is the maximum number of nodes in the queue at any time for a balanced complete binary tree with N nodes?",
                'options': [
                    "O(1)",
                    "O(log N)",
                    "(Recommended) O(N) (specifically ceil(N/2) nodes at the leaves level)",
                    "O(N^2)"
                ],
                'correct': 2,
                'explanation': "The bottom level of a complete binary tree contains approximately half of all nodes in the tree, requiring O(N) queue storage."
            }
        ]
    },

    # ---------------- 0040: BINARY TREE RIGHT SIDE VIEW ----------------
    {
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
        'plain_english': "Return the rightmost node at each depth. In BFS, collect the last element popped during each level's snapshot loop. In DFS, visit right children before left children: the first node encountered at any depth `d == result.size` is the visible node.",
        'boundary_checks': {
            'min_base': "<code>root == null</code> &rarr; Empty tree returns <code>[]</code>.",
            'best_exit': "Single node <code>[1]</code> &rarr; Returns <code>[1]</code>.",
            'worst_scan': "Left branch deeper than right branch (e.g. `[1, 2, 3, 4]`) &rarr; Left-side nodes become visible when right subtree runs out."
        },
        'mental_model': {
            'analogy_title': "Observation Deck at Sunset",
            'analogy_text': "You are standing on the far right cliff looking horizontally into the tree canopy. At each horizontal altitude (depth), light rays only let you see the single outermost node on the right. If a right branch ends early, the deeper left branch underneath emerges into view.",
            'aha_moment': "A left node CAN be visible if there is no right node at that depth! Thus, you can't just follow `node.right`. You must check EVERY level and take the rightmost node present.",
            'trigger': "View from an edge (Right/Left side view), profile projection &rarr; Level-order BFS taking last node per level OR Reverse DFS (Root &rarr; Right &rarr; Left).",
            'sisters': [
                {'name': "Binary Tree Level Order Traversal", 'lc': "LC #102"},
                {'name': "Find Bottom Left Tree Value", 'lc': "LC #513"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N) Time, O(N) Space",
            'naive_text': "Run full Level Order BFS, collect every node into full sublists, then extract `level.last()` for each level.",
            'wasted_work': "Allocating memory for full sublists at every tier when we only care about the single terminal element.",
            'optimal_complexity': "O(N) Time, O(H) Space",
            'optimal_text': "Reverse Pre-order DFS (visit Root, Right, Left). Check `if (depth == result.size) result.add(val)`. Uses O(H) call stack.",
            'core_invariant': "Because right subtrees are traversed before left subtrees, the first node reached at depth d is strictly the rightmost node at that tier.",
            'table_rows': [
                {'name': "1. Full BFS Sublists Extraction", 'time': "O(N)", 'space': "O(W) + O(N)", 'note': "Allocates lists for all nodes at each level."},
                {'name': "★ 2. Reverse DFS (Root->Right->Left)", 'time': "O(N)", 'space': "O(H)", 'note': "Appends only when depth equals list size; minimal space."}
            ]
        },
        'trace_table': {
            'input_desc': 'root = [1, 2, 3, null, 5, null, 4]',
            'columns': ["Depth", "Visited Node", "Result Size Before", "Action", "Result State"],
            'rows': [
                ["0", "Node(1)", "0", "0 == 0 &rarr; Append 1", "[1]"],
                ["1", "Node(3) (Right child)", "1", "1 == 1 &rarr; Append 3", "[1, 3]"],
                ["2", "Node(4) (Right child of 3)", "2", "2 == 2 &rarr; Append 4", "[1, 3, 4]"],
                ["1", "Node(2) (Left child of 1)", "3", "1 < 3 &rarr; Ignore (depth already seen)", "[1, 3, 4]"],
                ["2", "Node(5) (Right child of 2)", "3", "2 < 3 &rarr; Ignore (depth already seen)", "[1, 3, 4]"]
            ],
            'takeaway': "DFS visiting Right before Left guarantees the rightmost node claims each depth slot first."
        },
        'stepper': {
            'title': "Right Side View DFS / BFS Tracer",
            'default_input': '1, 2, 3, null, 5, null, 4',
            'presets': [
                {'label': "Classic [1, 2, 3, null, 5, null, 4]", 'onclick': 'loadPreset([1, 2, 3, null, 5, null, 4])'},
                {'label': "Left Taller [1, 2, null, 3, null, 4]", 'onclick': 'loadPreset([1, 2, null, 3, null, 4])'},
                {'label': "Right Skewed [1, null, 2, null, 3]", 'onclick': 'loadPreset([1, null, 2, null, 3])'}
            ],
            'metrics': [
                {'id': "metric-depth", 'label': "Current Depth", 'color': "text-amber-400", 'initial': "0"},
                {'id': "metric-node", 'label': "Examining Node", 'color': "text-cyan-400", 'initial': "-"},
                {'id': "metric-visible", 'label': "Visible List", 'color': "text-emerald-400", 'initial': "[]"},
                {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
            ],
            'visual_html': '<div id="right-view-stage" class="w-full flex items-center justify-center gap-4 py-4 min-h-[140px]"></div>',
            'initial_narration': 'Click "Next Step" to trace reverse pre-order DFS.',
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
    let treeArr = [1, 2, 3, null, 5, null, 4];

    function buildSteps(arr) {
      steps = [];
      if (!arr || arr.length === 0 || arr[0] === null) {
        steps.push({ depth: 0, node: "null", visible: "[]", action: "Empty Tree", line: 3, narration: "Empty root returns empty view." });
        return;
      }

      let res = [];

      function dfs(idx, depth) {
        if (idx >= arr.length || arr[idx] === null) return;
        let val = arr[idx];

        let added = false;
        if (depth === res.length) {
          res.push(val);
          added = true;
          steps.push({
            depth: depth,
            node: `${val}`,
            visible: `[${res.join(', ')}]`,
            action: `Added ${val} (First at depth ${depth})`,
            res: [...res],
            highlightNode: val,
            line: 4,
            narration: `Depth ${depth}: ${val} is the first node encountered. It is visible on the right!`
          });
        } else {
          steps.push({
            depth: depth,
            node: `${val}`,
            visible: `[${res.join(', ')}]`,
            action: `Ignored ${val} (Shadowed)`,
            res: [...res],
            highlightNode: val,
            line: 4,
            narration: `Depth ${depth} already claimed by ${res[depth]}. Node ${val} is hidden behind it.`
          });
        }

        // Right child first
        let rightChild = 2 * idx + 2;
        dfs(rightChild, depth + 1);

        // Left child
        let leftChild = 2 * idx + 1;
        dfs(leftChild, depth + 1);
      }

      dfs(0, 0);

      steps.push({
        depth: "-",
        node: "Done",
        visible: `[${res.join(', ')}]`,
        action: "Traversal Complete",
        res: [...res],
        line: 1,
        narration: `Completed right side view: [${res.join(', ')}].`
      });
    }

    function renderStep(step) {
      document.getElementById('metric-depth').textContent = step.depth;
      document.getElementById('metric-node').textContent = step.node;
      document.getElementById('metric-visible').textContent = step.visible;
      document.getElementById('metric-action').textContent = step.action;

      const stage = document.getElementById('right-view-stage');
      stage.innerHTML = '';
      if (!step.res) return;

      const box = document.createElement('div');
      box.className = 'flex items-center gap-2 p-3 bg-zinc-900 border border-zinc-700 rounded-lg';
      box.innerHTML = '<span class="text-xs text-zinc-400 font-bold uppercase mr-2">Right View Output:</span>' +
        step.res.map((v, i) => `<span class="px-3 py-1 bg-cyan-500/20 text-cyan-300 font-mono text-sm font-bold border border-cyan-500/40 rounded">${v}</span>`).join(' ');
      stage.appendChild(box);
    }

    function loadPreset(arr) {
      treeArr = arr;
      buildSteps(treeArr);
      currentStepIndex = 0;
      updateUI();
    }

    window.addEventListener('load', () => {
      buildSteps(treeArr);
      updateUI();
    });
"""
        },
        'approaches': [
            {
                'name': "Approach 1: Reverse Pre-Order DFS (Root -> Right -> Left)",
                'desc': "Traverse recursively visiting right child before left. When `depth == result.size`, this is the rightmost node at that depth.",
                'time': "O(N)",
                'space': "O(H) Call Stack",
                'kotlin': """class Solution {
    fun rightSideView(root: TreeNode?): List<Int> {
        val result = mutableListOf<Int>()
        dfs(root, 0, result)
        return result
    }

    private fun dfs(node: TreeNode?, depth: Int, result: MutableList<Int>) {
        if (node == null) return
        if (depth == result.size) {
            result.add(node.`val`)
        }
        dfs(node.right, depth + 1, result)
        dfs(node.left, depth + 1, result)
    }
}""",
                'python': """class Solution:
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
                'java': """class Solution {
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
        dfs(node.right, depth + 1, result);
        dfs(node.left, depth + 1, result);
    }
}""",
                'explanation': "The right branch is explored first. If a depth level hasn't recorded an element yet (`depth == result.size`), this node is guaranteed to be the rightmost visible node."
            },
            {
                'name': "Approach 2: Breadth-First Search (Queue Snapshot Level by Level)",
                'desc': "Perform standard BFS. At each level, take the last element in the level loop.",
                'time': "O(N)",
                'space': "O(W) <= O(N)",
                'kotlin': """class Solution {
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
                'python': """class Solution:
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
                'java': """class Solution {
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
                'explanation': "The last popped element in each level (`i == levelSize - 1`) is the rightmost node at that horizontal layer."
            }
        ],
        'scratchpad': {
            'initial_code': """class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def rightSideView(root):
    res = []
    def dfs(node, depth):
        if not node: return
        if depth == len(res):
            res.append(node.val)
        dfs(node.right, depth + 1)
        dfs(node.left, depth + 1)
    dfs(root, 0)
    return res

# Tree: 1 -> (2 -> (none, 5), 3 -> (none, 4))
t = TreeNode(1, TreeNode(2, None, TreeNode(5)), TreeNode(3, None, TreeNode(4)))
print("Right side view:", rightSideView(t))
""",
            'test_cases_json': """[
  {"input": "root = [1,2,3,null,5,null,4]", "expected": "[1,3,4]"},
  {"input": "root = [1,null,3]", "expected": "[1,3]"},
  {"input": "root = []", "expected": "[]"}
]"""
        },
        'interview_dialogue': {
            'opening': """<p><strong>Interviewer:</strong> "How do we get the right side view of a binary tree?"</p>
<p><strong>Candidate:</strong> "We need the rightmost node at each horizontal level. An intuitive way is BFS taking the last element at each level. Alternatively, reverse pre-order DFS visiting <code>right</code> before <code>left</code> allows us to simply add the node whenever <code>depth == result.size</code>."</p>
<p><strong>Interviewer:</strong> "Why does DFS work if a left branch is deeper than the right branch?"</p>
<p><strong>Candidate:</strong> "Because once the right branch terminates, the DFS continues down the deeper left branch. When it reaches depth <code>d</code>, <code>depth == result.size</code> will still be true for that new depth level, so the left node is cleanly recorded!"</p>""",
            'follow_ups': [
                {
                    'question': "How would you do Left Side View?",
                    'answer': "In DFS, visit Left child before Right child (`dfs(left)`, then `dfs(right)`). In BFS, capture `i == 0` instead of `i == levelSize - 1`."
                },
                {
                    'question': "What is the stack overflow risk with DFS in production?",
                    'answer': "On completely skewed trees with 100,000 nodes, DFS recursion depth exceeds default stack size (causing StackOverflowError). In languages like Python/Java, BFS using a heap-allocated `ArrayDeque` is safer for arbitrary depths."
                }
            ]
        },
        'mcqs': [
            {
                'question': "Why is `depth == result.size` the correct check in reverse pre-order DFS?",
                'options': [
                    "(Recommended) Because right children are visited first, the first node visited at each depth level is strictly the rightmost node at that level.",
                    "Because binary trees have at most two children per node.",
                    "Because result.size stores the total number of leaves in the tree.",
                    "Because it halts the search once the maximum depth is reached."
                ],
                'correct': 0,
                'explanation': "Visiting right before left ensures the rightmost node claims index `depth` in the result list. Subsequent left nodes at that depth see `depth < result.size` and are ignored."
            },
            {
                'question': "If the right subtree only has depth 2, but the left subtree has depth 5, does the DFS correctly capture nodes at depths 3, 4, and 5?",
                'options': [
                    "No, because the algorithm stops after exploring the right subtree.",
                    "(Recommended) Yes, the left subtree continues descending, and because result.size is only 2, depths 3, 4, and 5 are added.",
                    "No, it throws an IndexOutOfBoundsException.",
                    "Only if the tree is a Binary Search Tree."
                ],
                'correct': 1,
                'explanation': "The right branch does not stop the left branch from running; once depth exceeds the right branch's max depth, the left branch nodes qualify because `depth == result.size`."
            }
        ]
    },

    # ---------------- 0041: COUNT GOOD NODES IN BINARY TREE ----------------
    {
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
        'plain_english': "A node is 'good' if its value is greater than or equal to the maximum value seen on the path from root to that node. Traverse the tree with DFS, passing `maxSoFar` down to children. If `node.val >= maxSoFar`, increment good count and update `maxSoFar = max(maxSoFar, node.val)`.",
        'boundary_checks': {
            'min_base': "Root node alone <code>[1]</code> &rarr; Root has no ancestors, so it is always good (returns 1).",
            'best_exit': "All strictly increasing path (e.g. 1 &rarr; 2 &rarr; 3) &rarr; Every node is good (returns N).",
            'worst_scan': "All strictly decreasing path (e.g. 5 &rarr; 4 &rarr; 3 &rarr; 1) &rarr; Only root is good (returns 1)."
        },
        'mental_model': {
            'analogy_title': "The Mountain Ridge Line-of-Sight",
            'analogy_text': "Imagine climbing down a mountain ridge starting from the summit. As you descend, you only get a clear line of sight back up to the base camp if no taller mountain ridge stands between you and the starting peak. You carry a beacon showing the highest elevation peak traversed so far (`maxSoFar`). If your current spot is at least as high as `maxSoFar`, you plant a flag (Good Node!).",
            'aha_moment': "You do not need to store the entire ancestor path! You only need to pass down a single scalar: `maxSoFar`. This compresses path validation from O(Depth) to O(1) per node.",
            'trigger': "Path condition evaluating all ancestors &rarr; Top-down DFS carrying running metric (max, min, sum, bitmask).",
            'sisters': [
                {'name': "Path Sum", 'lc': "LC #112"},
                {'name': "Lowest Common Ancestor", 'lc': "LC #236"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N * H) Time, O(H) Space",
            'naive_text': "Collect ancestor values in a list during DFS. At each node, iterate through all ancestors in the list to verify none is greater than node.val.",
            'wasted_work': "Re-scanning the entire ancestor array of length H at every single node.",
            'optimal_complexity': "O(N) Time, O(H) Space",
            'optimal_text': "Pass a single integer `maxSoFar` as an argument down the recursive call stack. Update with `max(maxSoFar, node.val)`.",
            'core_invariant': "`maxSoFar` always represents the maximum node value strictly on the simple path from root to the current node.",
            'table_rows': [
                {'name': "1. Full Ancestor Array Check", 'time': "O(N * H)", 'space': "O(H)", 'note': "Scans ancestor list of size up to H at each node."},
                {'name': "★ 2. Running Max Scalar DFS", 'time': "O(N)", 'space': "O(H)", 'note': "O(1) work per node by maintaining running max."}
            ]
        },
        'trace_table': {
            'input_desc': 'root = [3, 1, 4, 3, null, 1, 5]',
            'columns': ["Node", "Current Val", "Path maxSoFar", "node.val >= maxSoFar?", "Is Good?", "New maxSoFar"],
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
                {'label': "Strictly Decreasing [5, 4, 3, 2, 1]", 'onclick': 'loadPreset([5, 4, null, 3, null, 2])'},
                {'label': "Strictly Increasing [1, null, 2, null, 3]", 'onclick': 'loadPreset([1, null, 2, null, 3])'}
            ],
            'metrics': [
                {'id': "metric-curr", 'label': "Current Node", 'color': "text-amber-400", 'initial': "-"},
                {'id': "metric-max", 'label': "Path Max So Far", 'color': "text-cyan-400", 'initial': "-"},
                {'id': "metric-count", 'label': "Good Nodes Count", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-status", 'label': "Status", 'color': "text-purple-400", 'initial': "Ready"}
            ],
            'visual_html': '<div id="good-nodes-stage" class="w-full flex items-center justify-center gap-4 py-4 min-h-[140px]"></div>',
            'initial_narration': 'Click "Next Step" to trace running max propagation.',
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
    let treeVals = [3, 1, 4, 3, null, 1, 5];

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
          curr: val,
          maxSoFar: maxSoFar,
          goodCount: goodCount,
          status: isGood ? "GOOD NODE" : "SHADOWED",
          isGood: isGood,
          newMax: newMax,
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
        curr: "Done",
        maxSoFar: "-",
        goodCount: goodCount,
        status: "FINISHED",
        isGood: false,
        newMax: "-",
        line: 8,
        narration: `Finished traversal. Total good nodes found: ${goodCount}.`
      });
    }

    function renderStep(step) {
      document.getElementById('metric-curr').textContent = step.curr;
      document.getElementById('metric-max').textContent = step.maxSoFar;
      document.getElementById('metric-count').textContent = step.goodCount;
      document.getElementById('metric-status').textContent = step.status;

      const stage = document.getElementById('good-nodes-stage');
      stage.innerHTML = '';

      const card = document.createElement('div');
      card.className = `p-4 rounded-xl border flex items-center gap-4 ${
        step.status === 'GOOD NODE' ? 'bg-emerald-500/10 border-emerald-500 text-emerald-300' :
        step.status === 'SHADOWED' ? 'bg-rose-500/10 border-rose-500 text-rose-300' :
        'bg-zinc-800 border-zinc-700 text-zinc-300'
      }`;
      card.innerHTML = `
        <div class="text-2xl font-black font-mono">${step.curr}</div>
        <div class="text-xs">
          <div>Path Max: <span class="font-bold font-mono">${step.maxSoFar}</span></div>
          <div>Evaluation: <span class="font-bold">${step.status}</span></div>
        </div>
      `;
      stage.appendChild(card);
    }

    function loadPreset(arr) {
      treeVals = arr;
      buildSteps(treeVals);
      currentStepIndex = 0;
      updateUI();
    }

    window.addEventListener('load', () => {
      buildSteps(treeVals);
      updateUI();
    });
"""
        },
        'approaches': [
            {
                'name': "Approach 1: Top-Down DFS with Path Max (Optimal)",
                'desc': "Traverse tree recursively, passing the running maximum along the branch. Increment good count whenever node.val >= maxSoFar.",
                'time': "O(N)",
                'space': "O(H) Call Stack",
                'kotlin': """class Solution {
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
                'python': """class Solution:
    def goodNodes(self, root: TreeNode) -> int:
        def dfs(node: TreeNode, max_so_far: int) -> int:
            if not node:
                return 0
            is_good = 1 if node.val >= max_so_far else 0
            new_max = max(max_so_far, node.val)
            return is_good + dfs(node.left, new_max) + dfs(node.right, new_max)
            
        return dfs(root, root.val)""",
                'java': """class Solution {
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
                'explanation': "Every node is visited exactly once. We pass `newMax` as an argument by value, so backtracking automatically restores the correct parent max value for sibling branches without explicit mutation."
            },
            {
                'name': "Approach 2: Iterative BFS with Queue of Pairs",
                'desc': "Use a Queue storing pairs `(node, maxSoFar)`. Avoids recursion stack overflow on deep trees.",
                'time': "O(N)",
                'space': "O(W) <= O(N)",
                'kotlin': """class Solution {
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
                'python': """class Solution:
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
                'java': """class Solution {
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
                'explanation': "The queue stores state alongside the node reference, eliminating call stack limits at the cost of heap allocation for the pairs."
            }
        ],
        'scratchpad': {
            'initial_code': """class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def goodNodes(root):
    def dfs(node, max_so_far):
        if not node: return 0
        good = 1 if node.val >= max_so_far else 0
        new_max = max(max_so_far, node.val)
        return good + dfs(node.left, new_max) + dfs(node.right, new_max)
    return dfs(root, root.val)

# Tree: 3 -> (1 -> (3, none), 4 -> (1, 5))
t = TreeNode(3, TreeNode(1, TreeNode(3)), TreeNode(4, TreeNode(1), TreeNode(5)))
print("Good nodes:", goodNodes(t))
""",
            'test_cases_json': """[
  {"input": "root = [3,1,4,3,null,1,5]", "expected": "4"},
  {"input": "root = [3,3,null,4,2]", "expected": "3"},
  {"input": "root = [1]", "expected": "1"}
]"""
        },
        'interview_dialogue': {
            'opening': """<p><strong>Candidate:</strong> "To decide if node X is good, we need to know the maximum value among all nodes along the path from the root to X. If X's value is greater than or equal to that maximum, it is good."</p>
<p><strong>Interviewer:</strong> "Do we need to store every node on the path?"</p>
<p><strong>Candidate:</strong> "No! Because we only care about the maximum value, we can simply maintain a single scalar <code>maxSoFar</code> and pass it down the recursion tree. This keeps space at O(H) and avoids any auxiliary list copies."</p>""",
            'follow_ups': [
                {
                    'question': "What if node values can be negative, such as -10,000?",
                    'answer': "Initialize `maxSoFar` with `root.val` (or `Integer.MIN_VALUE`). Never initialize with 0, otherwise negative node values would incorrectly be compared against 0."
                },
                {
                    'question': "Can two sibling nodes affect each other's maxSoFar?",
                    'answer': "No. Because `maxSoFar` is passed by value down the stack frames, changes made in the left child's subtree do not bleed into the right child's invocation."
                }
            ]
        },
        'mcqs': [
            {
                'question': "Why is passing `maxSoFar` by value down the call stack sufficient without using backtracking undo steps?",
                'options': [
                    "(Recommended) Function arguments are stored on the activation record; when returning from the left subtree, the parent frame's original maxSoFar remains completely intact for the right subtree.",
                    "Because primitive integers are stored in global CPU registers.",
                    "Because binary trees are immutable by definition.",
                    "Because Garbage Collection resets modified variables automatically."
                ],
                'correct': 0,
                'explanation': "Call-by-value on stack frames automatically isolates branch computations without requiring manual mutation and reversal."
            },
            {
                'question': "If root.val = -50 and all other nodes are -100, how many good nodes are in the tree?",
                'options': [
                    "0",
                    "(Recommended) 1 (only the root)",
                    "All nodes",
                    "Depends on tree height"
                ],
                'correct': 1,
                'explanation': "The root (-50) is good because it has no ancestors. For all descendants, node.val (-100) < maxSoFar (-50), so none are good."
            }
        ]
    },

    # ---------------- 0042: IMPLEMENT TRIE (PREFIX TREE) ----------------
    {
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
        'plain_english': "A tree where each node has up to 26 children (letters 'a' through 'z') and a boolean flag `isEnd`. To insert, walk character by character, creating nodes if absent, then mark the terminal node `isEnd = true`. To search, verify the path exists and terminal `isEnd == true`. For startsWith, simply verify the path exists without checking `isEnd`.",
        'boundary_checks': {
            'min_base': "Single character insert <code>insert('a')</code> &rarr; Creates root child at index 0, marks `isEnd = true`.",
            'best_exit': "Search prefix that is exact full word &rarr; Both `search` and `startsWith` return true.",
            'worst_scan': "Searching prefix that doesn't exist at first letter &rarr; Immediately returns false at step 1 in O(1)."
        },
        'mental_model': {
            'analogy_title': "The Rotary Dial Phone Directory",
            'analogy_text': "Think of an old phone tree directory where each digit turns a dial. Dialing 'A-P-P-L-E' walks through hallways labeled A, then P, then P, then L, then E. At the end of hallway E, you find a sticky note that says 'END OF OFFICIAL WORD'. If someone dials 'A-P-P', they reach room P. Room P exists (so `startsWith('app')` is TRUE), but has no sticky note yet (so `search('app')` is FALSE) until you explicitly insert 'app'.",
            'aha_moment': "`search` and `startsWith` share identical path traversal logic! The ONLY difference: `search` requires `node.isEnd == true`, whereas `startsWith` just requires that the final node is non-null.",
            'trigger': "Prefix matching, autocomplete suggestions, dictionary word lookup, boggle solver &rarr; Trie (Prefix Tree).",
            'sisters': [
                {'name': "Design Add and Search Words Data Structure", 'lc': "LC #211"},
                {'name': "Word Search II", 'lc': "LC #212"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(W * L) Search Time, O(N * L) Space",
            'naive_text': "Store words in a HashSet for search (O(L)), but `startsWith` requires iterating through all W words checking `w.startsWith(prefix)` in O(W * L).",
            'wasted_work': "Re-scanning every word in the dictionary for prefix queries.",
            'optimal_complexity': "O(L) Time per Op, O(Total Chars * 26) Space",
            'optimal_text': "Trie structure. Both exact search and prefix queries run in strict O(L) time proportional to word length, independent of dictionary size W.",
            'core_invariant': "Every path from the root node represents a unique prefix formed by the concatenated edge characters.",
            'table_rows': [
                {'name': "1. HashSet + Linear Prefix Scan", 'time': "Search: O(L), Prefix: O(W * L)", 'space': "O(W * L)", 'note': "Prefix search degrades with large dictionaries."},
                {'name': "★ 2. 26-Way Array TrieNode", 'time': "Insert/Search/Prefix: O(L)", 'space': "O(Total Chars * 26)", 'note': "Strict O(L) independent of dictionary size W."}
            ]
        },
        'trace_table': {
            'input_desc': 'insert("apple"), search("app"), startsWith("app"), insert("app"), search("app")',
            'columns': ["Operation", "Word/Prefix", "Path Navigated", "Terminal isEnd?", "Return Value"],
            'rows': [
                ["insert", "apple", "root -> a -> p -> p -> l -> e", "Marked true at 'e'", "void"],
                ["search", "app", "root -> a -> p -> p", "false (node exists, but not end)", "false"],
                ["startsWith", "app", "root -> a -> p -> p", "Don't care (node != null)", "true"],
                ["insert", "app", "root -> a -> p -> p", "Marked true at 'p'", "void"],
                ["search", "app", "root -> a -> p -> p", "true (now marked!)", "true"]
            ],
            'takeaway': "Shared prefix 'app' reuses the exact same 3 node instances for both 'apple' and 'app'."
        },
        'stepper': {
            'title': "Trie Node Insertion & Lookup Stepper",
            'default_input': 'apple',
            'presets': [
                {'label': "Insert 'apple' then 'app'", 'onclick': 'loadPreset(["insert:apple", "search:app", "insert:app", "search:app"])'},
                {'label': "Prefix search 'ban'", 'onclick': 'loadPreset(["insert:banana", "startsWith:ban", "search:ban"])'}
            ],
            'metrics': [
                {'id': "metric-op", 'label': "Operation", 'color': "text-amber-400", 'initial': "Init"},
                {'id': "metric-char", 'label': "Current Char", 'color': "text-cyan-400", 'initial': "-"},
                {'id': "metric-isend", 'label': "Node isEnd", 'color': "text-emerald-400", 'initial': "false"},
                {'id': "metric-res", 'label': "Result", 'color': "text-purple-400", 'initial': "-"}
            ],
            'visual_html': '<div id="trie-stage" class="w-full flex items-center justify-center gap-2 py-4 min-h-[140px] overflow-x-auto"></div>',
            'initial_narration': 'Click "Next Step" to trace letter-by-letter trie traversal.',
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
    let opQueue = ["insert:apple", "search:app", "insert:app", "search:app"];

    function buildSteps(ops) {
      steps = [];
      let trie = { children: {}, isEnd: false };

      ops.forEach(cmd => {
        let [op, word] = cmd.split(':');
        let curr = trie;
        let path = [];

        steps.push({
          op: op, char: "-", isEnd: false, res: "-",
          word: word, path: [], line: 5,
          narration: `Starting operation: ${op}("${word}")`
        });

        if (op === 'insert') {
          for (let ch of word) {
            if (!curr.children[ch]) {
              curr.children[ch] = { children: {}, isEnd: false };
            }
            curr = curr.children[ch];
            path.push(ch);
            steps.push({
              op: op, char: ch, isEnd: curr.isEnd, res: "-",
              word: word, path: [...path], line: 9,
              narration: `Navigated character '${ch}' &rarr; Node active.`
            });
          }
          curr.isEnd = true;
          steps.push({
            op: op, char: word[word.length - 1], isEnd: true, res: "OK",
            word: word, path: [...path], line: 12,
            narration: `Marked terminal node '${word[word.length - 1]}' as isEnd = true.`
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
            op: op, char: path[path.length - 1] || "-", isEnd: curr.isEnd, res: res ? "TRUE" : "FALSE",
            word: word, path: [...path], line: 3,
            narration: `Search("${word}"): Found path=${found}, isEnd=${curr.isEnd} &rarr; Result: ${res}.`
          });
        }
      });
    }

    function renderStep(step) {
      document.getElementById('metric-op').textContent = `${step.op}("${step.word}")`;
      document.getElementById('metric-char').textContent = step.char;
      document.getElementById('metric-isend').textContent = step.isEnd ? "true" : "false";
      document.getElementById('metric-res').textContent = step.res;

      const stage = document.getElementById('trie-stage');
      stage.innerHTML = '';

      if (!step.path) return;
      step.path.forEach((c, idx) => {
        const node = document.createElement('div');
        const isLast = idx === step.path.length - 1;
        node.className = `flex flex-col items-center justify-center w-12 h-12 rounded-xl font-mono font-black text-lg border ${
          isLast ? 'bg-amber-500/20 text-amber-300 border-amber-500' : 'bg-zinc-800 text-zinc-300 border-zinc-700'
        }`;
        node.innerHTML = `<span>${c}</span>`;
        stage.appendChild(node);
        if (idx < step.path.length - 1) {
          const arrow = document.createElement('span');
          arrow.className = 'text-zinc-600 font-bold';
          arrow.textContent = '→';
          stage.appendChild(arrow);
        }
      });
    }

    function loadPreset(arr) {
      opQueue = arr;
      buildSteps(opQueue);
      currentStepIndex = 0;
      updateUI();
    }

    window.addEventListener('load', () => {
      buildSteps(opQueue);
      updateUI();
    });
"""
        },
        'approaches': [
            {
                'name': "Approach 1: Array-Based Fixed Alphabet (26-way TrieNode)",
                'desc': "Store child pointers in an array of size 26 (`arrayOfNulls<TrieNode>(26)`). Optimal speed with zero hash collisions.",
                'time': "O(L) per operation",
                'space': "O(N * 26) = O(N)",
                'kotlin': """class Trie() {
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
                'python': """class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False

class Trie:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word: str) -> None:
        curr = self.root
        for ch in word:
            if ch not in curr.children:
                curr.children[ch] = TrieNode()
            curr = curr.children[ch]
        curr.is_end = True

    def search(self, word: str) -> bool:
        node = self._find_node(word)
        return node is not None and node.is_end

    def startsWith(self, prefix: str) -> bool:
        return self._find_node(prefix) is not None

    def _find_node(self, prefix: str) -> Optional[TrieNode]:
        curr = self.root
        for ch in prefix:
            if ch not in curr.children:
                return None
            curr = curr.children[ch]
        return curr""",
                'java': """class Trie {
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
                'explanation': "Using helper method `findNode(prefix)` deduplicates traversal code between `search` and `startsWith`. Direct indexing with `ch - 'a'` provides instant O(1) branch selection without hash overhead."
            },
            {
                'name': "Approach 2: Map-Based Dynamic TrieNode (Unicode & Dynamic Charsets)",
                'desc': "Store child pointers in a HashMap. Saves memory when alphabet is sparse or contains Unicode characters.",
                'time': "O(L) per operation",
                'space': "O(Total Chars)",
                'kotlin': """class Trie() {
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
                'python': """class Trie:
    def __init__(self):
        self.trie = {}

    def insert(self, word: str) -> None:
        curr = self.trie
        for ch in word:
            curr = curr.setdefault(ch, {})
        curr['#'] = True

    def search(self, word: str) -> bool:
        curr = self.trie
        for ch in word:
            if ch not in curr:
                return False
            curr = curr[ch]
        return '#' in curr

    def startsWith(self, prefix: str) -> bool:
        curr = self.trie
        for ch in prefix:
            if ch not in curr:
                return False
            curr = curr[ch]
        return True""",
                'java': """class Trie {
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
                'explanation': "The Map approach is ideal when character set size is large (e.g. UTF-8 or Chinese characters) where 65,536-length arrays would waste prohibitive amounts of memory."
            }
        ],
        'scratchpad': {
            'initial_code': """class Trie:
    def __init__(self):
        self.root = {}

    def insert(self, word: str) -> None:
        node = self.root
        for char in word:
            node = node.setdefault(char, {})
        node['$'] = True

    def search(self, word: str) -> bool:
        node = self.root
        for char in word:
            if char not in node: return False
            node = node[char]
        return '$' in node

    def startsWith(self, prefix: str) -> bool:
        node = self.root
        for char in prefix:
            if char not in node: return False
            node = node[char]
        return True

t = Trie()
t.insert("apple")
print("search apple:", t.search("apple"))
print("search app:", t.search("app"))
print("startsWith app:", t.startsWith("app"))
t.insert("app")
print("search app after insert:", t.search("app"))
""",
            'test_cases_json': """[
  {"input": "insert('apple'), search('apple'), search('app'), startsWith('app'), insert('app'), search('app')", "expected": "[true, false, true, true]"}
]"""
        },
        'interview_dialogue': {
            'opening': """<p><strong>Candidate:</strong> "A Trie (Prefix Tree) organizes strings hierarchically by character edges. Each node has child links for alphabet characters and an <code>isEnd</code> flag."</p>
<p><strong>Interviewer:</strong> "Would you use an array of size 26 or a HashMap for the children?"</p>
<p><strong>Candidate:</strong> "For lowercase English letters ('a' through 'z'), a fixed array <code>TrieNode[26]</code> has zero hashing overhead and superior cache locality. If the problem required full Unicode or arbitrary character sets, a <code>HashMap&lt;Character, TrieNode&gt;</code> avoids allocating 65,536 null pointers per node."</p>""",
            'follow_ups': [
                {
                    'question': "How much memory does a Trie consume compared to a HashSet?",
                    'answer': "A Trie shares common prefixes, saving storage when many words share prefixes. However, each node pointer incurs object overhead. If words share few prefixes, a Trie can consume more memory than a raw HashSet of strings."
                },
                {
                    'question': "How would you implement delete(word) in a Trie?",
                    'answer': "Perform post-order recursion: unmark `isEnd = false`. If the node has no other children and is not the end of another word, prune the node reference to free memory."
                }
            ]
        },
        'mcqs': [
            {
                'question': "What is the key difference between `search(str)` and `startsWith(str)` in a Trie?",
                'options': [
                    "(Recommended) `search` requires that the terminal node has `isEnd == true`, whereas `startsWith` only requires that the path of nodes exists.",
                    "`search` runs in O(N) while `startsWith` runs in O(log N).",
                    "`startsWith` searches from right to left.",
                    "`search` builds the tree while `startsWith` reads it."
                ],
                'correct': 0,
                'explanation': "A prefix exists as long as all characters form a valid path. An exact word match additionally requires that an inserted word actually ended at that terminal node (`isEnd == true`)."
            },
            {
                'question': "If word length is L and dictionary contains N words, what is the time complexity of `search(word)` in a 26-way Trie?",
                'options': [
                    "O(N * L)",
                    "O(log N)",
                    "(Recommended) O(L) (completely independent of N)",
                    "O(26^L)"
                ],
                'correct': 2,
                'explanation': "Lookup follows exactly one edge per character in `word`, requiring exactly L pointer dereferences regardless of how many millions of words N are stored."
            }
        ]
    },

    # ---------------- 0043: KTH LARGEST ELEMENT IN AN ARRAY ----------------
    {
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
        'plain_english': "Find the element that would be at index `n - k` if the array were sorted. Maintain a Min-Heap of size `k`. When size exceeds `k`, eject the minimum. After scanning all elements, the top of the Min-Heap is precisely the kth largest element.",
        'boundary_checks': {
            'min_base': "<code>k = 1</code> &rarr; Returns the absolute maximum element in the array.",
            'best_exit': "<code>k = nums.length</code> &rarr; Returns the absolute minimum element.",
            'worst_scan': "Array with all duplicate elements <code>nums = [2, 2, 2, 2], k = 2</code> &rarr; Returns 2 smoothly."
        },
        'mental_model': {
            'analogy_title': "The VIP Lounge with K Seats",
            'analogy_text': "A VIP lounge holds strictly k people. The bouncer only allows high-status guests in. When a newcomer arrives, if the lounge has space, they enter. Once full (k people), any new challenger compares status against the lowest-ranked guest currently inside (`heap.peek()`). If the newcomer is richer, the poorest VIP is kicked out (`heap.poll()`), and the newcomer takes a seat. At closing time, who is the poorest person remaining in the VIP lounge? Precisely the kth richest person overall!",
            'aha_moment': "To find the k-th LARGEST element, use a MIN-HEAP of size k! The smallest element inside the top-k club is the entrance barrier—the k-th largest.",
            'trigger': "K-th largest/smallest, Top K stream processing &rarr; Min-Heap of size K (or QuickSelect).",
            'sisters': [
                {'name': "Top K Frequent Elements", 'lc': "LC #347"},
                {'name': "Kth Smallest Element in a Sorted Matrix", 'lc': "LC #378"},
                {'name': "Find Median from Data Stream", 'lc': "LC #295"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N log N) Time, O(1) Space",
            'naive_text': "Sort the entire array in descending order and return `nums[k - 1]`.",
            'wasted_work': "Sorting all N elements when we only need the boundary between the top k and remaining elements.",
            'optimal_complexity': "O(N log K) Time, O(K) Space",
            'optimal_text': "Min-Heap of size k. Constant-time peek, log(K) push/pop. Works seamlessly on infinite streaming data.",
            'core_invariant': "At all times, the min-heap contains the k largest elements seen so far; its root is the smallest among those k.",
            'table_rows': [
                {'name': "1. Full Array Sort", 'time': "O(N log N)", 'space': "O(1) to O(N)", 'note': "Wasteful sort on large N; fails on streams."},
                {'name': "★ 2. Min-Heap of Size K", 'time': "O(N log K)", 'space': "O(K)", 'note': "Optimal for streaming; memory bounded by K."},
                {'name': "3. QuickSelect (Hoare)", 'time': "O(N) Avg, O(N^2) Worst", 'space': "O(1)", 'note': "Average linear time in-place, but degrades on bad pivot."}
            ]
        },
        'trace_table': {
            'input_desc': 'nums = [3, 2, 1, 5, 6, 4], k = 2',
            'columns': ["Element", "Heap Before", "Action", "Heap After (Size <= 2)", "Current Peek (2nd Largest)"],
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
    let kVal = 2;

    function buildSteps(arr, k) {
      steps = [];
      let heap = [];

      function pushHeap(val) {
        heap.push(val);
        heap.sort((a, b) => a - b);
      }

      function popHeap() {
        return heap.shift();
      }

      for (let i = 0; i < arr.length; i++) {
        let n = arr[i];
        pushHeap(n);
        let ejected = null;
        if (heap.length > k) {
          ejected = popHeap();
        }

        steps.push({
          num: n,
          heap: `[${heap.join(', ')}]`,
          kth: heap[0],
          action: ejected !== null ? `Added ${n}, ejected min ${ejected}` : `Added ${n}`,
          heapArr: [...heap],
          line: ejected !== null ? 4 : 3,
          narration: ejected !== null
            ? `Pushed ${n}. Heap size exceeded ${k}, so ejected smallest element ${ejected}. Current Kth largest is ${heap[0]}.`
            : `Pushed ${n}. Heap size is ${heap.length} <= ${k}.`
        });
      }

      steps.push({
        num: "Done",
        heap: `[${heap.join(', ')}]`,
        kth: heap[0],
        action: "Finished Scanning",
        heapArr: [...heap],
        line: 6,
        narration: `Finished scan. Min-heap root ${heap[0]} is the ${k}-th largest element!`
      });
    }

    function renderStep(step) {
      document.getElementById('metric-num').textContent = step.num;
      document.getElementById('metric-heap').textContent = step.heap;
      document.getElementById('metric-kth').textContent = step.kth;
      document.getElementById('metric-action').textContent = step.action;

      const stage = document.getElementById('heap-stage');
      stage.innerHTML = '';

      if (!step.heapArr) return;
      step.heapArr.forEach((v, idx) => {
        const item = document.createElement('div');
        const isMin = idx === 0;
        item.className = `flex flex-col items-center justify-center w-14 h-14 rounded-xl border font-mono font-bold ${
          isMin ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500 ring-2 ring-emerald-500/40' : 'bg-zinc-800 text-zinc-300 border-zinc-700'
        }`;
        item.innerHTML = `
          <span class="text-xs text-zinc-400 font-normal">${isMin ? 'ROOT' : 'NODE'}</span>
          <span class="text-base">${v}</span>
        `;
        stage.appendChild(item);
      });
    }

    function loadPreset(arr, k) {
      dataNums = arr;
      kVal = k;
      buildSteps(dataNums, kVal);
      currentStepIndex = 0;
      updateUI();
    }

    window.addEventListener('load', () => {
      buildSteps(dataNums, kVal);
      updateUI();
    });
"""
        },
        'approaches': [
            {
                'name': "Approach 1: Min-Heap of Size K (Stream-Optimal & Production-Safe)",
                'desc': "Maintain a Min-Heap of size k. Eject root when size > k. At the end, the root is the kth largest.",
                'time': "O(N log K)",
                'space': "O(K)",
                'kotlin': """class Solution {
    fun findKthLargest(nums: IntArray, k: Int): Int {
        val minHeap = java.util.PriorityQueue<Int>()
        for (num in nums) {
            minHeap.offer(num)
            if (minHeap.size > k) {
                minHeap.poll()
            }
        }
        return minHeap.peek()
    }
}""",
                'python': """class Solution:
    def findKthLargest(self, nums: List[int], k: int) -> int:
        min_heap = []
        for num in nums:
            heapq.heappush(min_heap, num)
            if len(min_heap) > k:
                heapq.heappop(min_heap)
        return min_heap[0]""",
                'java': """class Solution {
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
                'explanation': "The Min-Heap keeps exactly the k largest elements. The smallest of those k elements is at the root. Heap operations take O(log K), resulting in O(N log K) overall time with minimal O(K) extra space."
            },
            {
                'name': "Approach 2: QuickSelect (Hoare's Partitioning)",
                'desc': "Partition array around a random pivot like QuickSort. Discard half the array each step to achieve average linear time.",
                'time': "O(N) Average, O(N^2) Worst",
                'space': "O(1) In-Place",
                'kotlin': """class Solution {
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
                nums[p] = nums[i].also { nums[i] = nums[p] }
                p++
            }
        }
        nums[p] = nums[right].also { nums[right] = nums[p] }
        return p
    }
}""",
                'python': """class Solution:
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
            pivot_idx = partition(left, right)
            if pivot_idx == target_idx:
                return nums[pivot_idx]
            elif pivot_idx < target_idx:
                left = pivot_idx + 1
            else:
                right = pivot_idx - 1
        return -1""",
                'java': """class Solution {
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
                'explanation': "QuickSelect reduces problem size by half on average: N + N/2 + N/4 + ... = 2N = O(N). However, if adversary input triggers worst-case pivots repeatedly, it degrades to O(N^2)."
            }
        ],
        'scratchpad': {
            'initial_code': """import heapq

def findKthLargest(nums, k):
    heap = []
    for n in nums:
        heapq.heappush(heap, n)
        if len(heap) > k:
            heapq.heappop(heap)
    return heap[0]

arr = [3, 2, 1, 5, 6, 4]
k = 2
print(f"{k}th largest in {arr} is:", findKthLargest(arr, k))
""",
            'test_cases_json': """[
  {"input": "nums = [3,2,1,5,6,4], k = 2", "expected": "5"},
  {"input": "nums = [3,2,3,1,2,4,5,5,6], k = 4", "expected": "4"}
]"""
        },
        'interview_dialogue': {
            'opening': """<p><strong>Candidate:</strong> "We can sort the array in O(N log N), but we can do better. We can use a Min-Heap of size k in O(N log K) time and O(K) space, or QuickSelect in O(N) average time."</p>
<p><strong>Interviewer:</strong> "Why would an engineering team prefer the Min-Heap over QuickSelect in production?"</p>
<p><strong>Candidate:</strong> "Two huge reasons: 1) Min-Heap processes unbounded data streams where we don't have the entire array in memory upfront. 2) Min-Heap has guaranteed O(N log K) worst-case performance without vulnerability to O(N^2) worst-case pivot attacks or stack overflows."</p>""",
            'follow_ups': [
                {
                    'question': "Why a Min-Heap instead of a Max-Heap?",
                    'answer': "A Max-Heap would need to store all N elements to extract the k-th largest, requiring O(N) space and O(N + K log N) time. A Min-Heap of size K retains only the top K elements, using only O(K) memory."
                },
                {
                    'question': "What if k is very close to N, like k = N - 1?",
                    'answer': "Finding the (N - 1)th largest is equivalent to finding the 2nd smallest! We can flip logic and use a Max-Heap of size (N - k + 1) = 2, achieving O(N log(N - K)) time."
                }
            ]
        },
        'mcqs': [
            {
                'question': "Why does a Min-Heap of capacity K give the K-th LARGEST element at its root?",
                'options': [
                    "(Recommended) Because retaining the K largest elements means the smallest among those K is precisely the K-th largest element overall.",
                    "Because Min-Heaps automatically invert numbers into negatives.",
                    "Because Java's PriorityQueue sorts in descending order by default.",
                    "Because polling a Min-Heap removes the largest element first."
                ],
                'correct': 0,
                'explanation': "The K largest elements form a set. The minimum element inside this set is the barrier value—the K-th largest of the entire array."
            },
            {
                'question': "What is the worst-case time complexity of QuickSelect without randomized pivot selection?",
                'options': [
                    "O(N log N)",
                    "O(N)",
                    "(Recommended) O(N^2)",
                    "O(K log N)"
                ],
                'correct': 2,
                'explanation': "If pivots partition the array into 1 and N-1 elements repeatedly (such as already sorted arrays with end-pivot selection), QuickSelect degrades to O(N^2)."
            }
        ]
    },

    # ---------------- 0044: K CLOSEST POINTS TO ORIGIN ----------------
    {
        'num': 44,
        'lc': 973,
        'title': "K Closest Points to Origin",
        'category': "Heaps & Priority Queues",
        'difficulty': "Medium",
        'lc_slug': "k-closest-points-to-origin",
        'verbatim_statement': """<p>Given an array of <code>points</code> where <code>points[i] = [x<sub>i</sub>, y<sub>i</sub>]</code> represents a point on the <strong>X-Y</strong> plane and an integer <code>k</code>, return the <code>k</code> closest points to the origin <code>(0, 0)</code>.</p><p>The distance between two points on the <strong>X-Y</strong> plane is the Euclidean distance (i.e., <code>&radic;(x<sub>1</sub> - x<sub>2</sub>)<sup>2</sup> + (y<sub>1</sub> - y<sub>2</sub>)<sup>2</sup></code>).</p><p>You may return the answer in <strong>any order</strong>. The answer is <strong>guaranteed</strong> to be <strong>unique</strong> (except for the order that it is in).</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'points = [[1,3],[-2,2]], k = 1', 'output': '[[-2,2]]\nExplanation: (1)^2 + (3)^2 = 10, (-2)^2 + (2)^2 = 8. Since 8 < 10, [-2,2] is closer.'},
            {'title': "Example 2", 'input': 'points = [[3,3],[5,-1],[-2,4]], k = 2', 'output': '[[3,3],[-2,4]] (or in reverse order)'}
        ],
        'constraints': [
            "1 <= k <= points.length <= 10^4",
            "-10^4 <= x_i, y_i <= 10^4"
        ],
        'plain_english': "Compare points by their squared Euclidean distance `x^2 + y^2` (no square root needed). Maintain a Max-Heap of size `k`. When size exceeds `k`, eject the point with the largest distance. At the end, the heap contains the `k` closest points.",
        'boundary_checks': {
            'min_base': "<code>k == points.length</code> &rarr; All points are requested; return input array immediately in O(1).",
            'best_exit': "Point at origin <code>[0, 0]</code> &rarr; Distance is 0, guaranteed to enter and stay in heap.",
            'worst_scan': "Ties in distance &rarr; Problem statement guarantees return order does not matter."
        },
        'mental_model': {
            'analogy_title': "The Bubble Bouncer (Max-Heap of Size K)",
            'analogy_text': "You have a security perimeter that can protect the k closest citizens. To keep only the closest k people, you constantly keep an eye on the FURTHEST citizen currently in your safe zone (Max-Heap root). When a new citizen arrives, if they are closer to home than your furthest protected citizen, you kick out the furthest citizen and let the closer one into your circle.",
            'aha_moment': "We do NOT need `Math.sqrt()`! Because $f(d) = \\sqrt{d}$ is strictly monotonically increasing for $d \\ge 0$, comparing $x^2 + y^2$ gives the exact same ordering while avoiding expensive floating-point arithmetic and precision issues.",
            'trigger': "Find K smallest/closest items &rarr; Max-Heap of size K (eject largest when size > K).",
            'sisters': [
                {'name': "Kth Largest Element in an Array", 'lc': "LC #215"},
                {'name': "Top K Frequent Elements", 'lc': "LC #347"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N log N) Time, O(N) Space",
            'naive_text': "Sort all points by $x^2 + y^2$ using full quicksort or mergesort, then slice the first k elements.",
            'wasted_work': "Sorting all N elements when we only need the smallest k in arbitrary order.",
            'optimal_complexity': "O(N log K) Time, O(K) Space",
            'optimal_text': "Max-Heap of size k keyed on squared distance $x^2 + y^2$. Elements beyond the top k are discarded.",
            'core_invariant': "The Max-Heap root holds the point with the greatest distance among the current k closest candidates.",
            'table_rows': [
                {'name': "1. Full Array Distance Sort", 'time': "O(N log N)", 'space': "O(N)", 'note': "Sorts entire dataset; inefficient if N is large and K is small."},
                {'name': "★ 2. Max-Heap of Size K", 'time': "O(N log K)", 'space': "O(K)", 'note': "Bounded memory O(K); ideal for streaming geo-points."}
            ]
        },
        'trace_table': {
            'input_desc': 'points = [[3,3], [5,-1], [-2,4]], k = 2',
            'columns': ["Point [x, y]", "Distance Sq (x^2 + y^2)", "Heap Before", "Action", "Heap After (Size <= 2)"],
            'rows': [
                ["[3, 3]", "9 + 9 = 18", "[]", "Add [3,3] (dist 18)", "[18: [3,3]]"],
                ["[5, -1]", "25 + 1 = 26", "[18]", "Add [5,-1] (dist 26)", "[26: [5,-1], 18: [3,3]]"],
                ["[-2, 4]", "4 + 16 = 20", "[26, 18]", "Add [-2,4] (dist 20) &rarr; Size 3 &rarr; Eject max (26: [5,-1])", "[20: [-2,4], 18: [3,3]]"]
            ],
            'takeaway': "Points [3,3] and [-2,4] remain. Furthest point [5,-1] (dist 26) was successfully pruned."
        },
        'stepper': {
            'title': "Max-Heap K Closest Points Stepper",
            'default_input': '[[3,3],[5,-1],[-2,4]], k=2',
            'presets': [
                {'label': "Example [[3,3],[5,-1],[-2,4]], k=2", 'onclick': 'loadPreset([[3,3],[5,-1],[-2,4]], 2)'},
                {'label': "Example [[1,3],[-2,2]], k=1", 'onclick': 'loadPreset([[1,3],[-2,2]], 1)'}
            ],
            'metrics': [
                {'id': "metric-pt", 'label': "Current Point", 'color': "text-amber-400", 'initial': "-"},
                {'id': "metric-dsq", 'label': "Squared Distance", 'color': "text-cyan-400", 'initial': "-"},
                {'id': "metric-maxd", 'label': "Furthest in Heap", 'color': "text-emerald-400", 'initial': "-"},
                {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
            ],
            'visual_html': '<div id="points-stage" class="w-full flex items-center justify-center gap-3 py-4 min-h-[140px]"></div>',
            'initial_narration': 'Click "Next Step" to trace Max-Heap retention.',
            'pseudocode_lines': [
                "val maxHeap = PriorityQueue<IntArray> { a, b ->",
                "    (b[0]*b[0] + b[1]*b[1]) - (a[0]*a[0] + a[1]*a[1])",
                "}",
                "for (pt in points) {",
                "    maxHeap.offer(pt)",
                "    if (maxHeap.size > k) maxHeap.poll()",
                "}",
                "return maxHeap.toTypedArray()"
            ],
            'js_code': """
    let rawPts = [[3,3],[5,-1],[-2,4]];
    let kCount = 2;

    function buildSteps(pts, k) {
      steps = [];
      let heap = [];

      function distSq(p) { return p[0]*p[0] + p[1]*p[1]; }

      for (let i = 0; i < pts.length; i++) {
        let p = pts[i];
        let d = distSq(p);
        heap.push({ p: p, d: d });
        // Max-heap: sort descending by distance
        heap.sort((a, b) => b.d - a.d);

        let ejected = null;
        if (heap.length > k) {
          ejected = heap.shift(); // remove largest
        }

        steps.push({
          pt: `[${p[0]}, ${p[1]}]`,
          dsq: `${d}`,
          maxd: `${heap[0].d}`,
          action: ejected ? `Ejected [${ejected.p[0]}, ${ejected.p[1]}] (dist ${ejected.d})` : `Retained [${p[0]}, ${p[1]}]`,
          heap: [...heap],
          line: ejected ? 6 : 5,
          narration: ejected
            ? `Point [${p[0]}, ${p[1]}] (dist ${d}) entered. Heap exceeded ${k}, so discarded furthest point [${ejected.p[0]}, ${ejected.p[1]}] (dist ${ejected.d}).`
            : `Point [${p[0]}, ${p[1]}] (dist ${d}) added to heap.`
        });
      }

      steps.push({
        pt: "Done",
        dsq: "-",
        maxd: `${heap[0].d}`,
        action: "Finished",
        heap: [...heap],
        line: 8,
        narration: `Finished! The ${k} closest points are: ${heap.map(x => `[${x.p[0]},${x.p[1]}]`).join(', ')}.`
      });
    }

    function renderStep(step) {
      document.getElementById('metric-pt').textContent = step.pt;
      document.getElementById('metric-dsq').textContent = step.dsq;
      document.getElementById('metric-maxd').textContent = step.maxd;
      document.getElementById('metric-action').textContent = step.action;

      const stage = document.getElementById('points-stage');
      stage.innerHTML = '';
      if (!step.heap) return;

      step.heap.forEach((item, idx) => {
        const card = document.createElement('div');
        const isFurthest = idx === 0;
        card.className = `flex flex-col items-center justify-center p-3 rounded-xl border font-mono ${
          isFurthest ? 'bg-amber-500/20 text-amber-300 border-amber-500' : 'bg-emerald-500/20 text-emerald-300 border-emerald-500/50'
        }`;
        card.innerHTML = `
          <span class="text-xs text-zinc-400 font-bold">${isFurthest ? 'FURTHEST (ROOT)' : 'SAFE'}</span>
          <span class="text-sm font-bold">[${item.p[0]}, ${item.p[1]}]</span>
          <span class="text-xs text-zinc-400">d² = ${item.d}</span>
        `;
        stage.appendChild(card);
      });
    }

    function loadPreset(pts, k) {
      rawPts = pts;
      kCount = k;
      buildSteps(rawPts, kCount);
      currentStepIndex = 0;
      updateUI();
    }

    window.addEventListener('load', () => {
      buildSteps(rawPts, kCount);
      updateUI();
    });
"""
        },
        'approaches': [
            {
                'name': "Approach 1: Max-Heap of Size K (Optimal Space-Time)",
                'desc': "Use a Max-Heap to keep the k closest points. When the heap size exceeds k, poll the furthest point.",
                'time': "O(N log K)",
                'space': "O(K)",
                'kotlin': """class Solution {
    fun kClosest(points: Array<IntArray>, k: Int): Array<IntArray> {
        // Max-heap: furthest point at the top
        val maxHeap = java.util.PriorityQueue<IntArray> { a, b ->
            val distA = a[0] * a[0] + a[1] * a[1]
            val distB = b[0] * b[0] + b[1] * b[1]
            distB.compareTo(distA)
        }

        for (pt in points) {
            maxHeap.offer(pt)
            if (maxHeap.size > k) {
                maxHeap.poll()
            }
        }

        val result = Array(k) { IntArray(2) }
        var i = 0
        while (maxHeap.isNotEmpty()) {
            result[i++] = maxHeap.poll()
        }
        return result
    }
}""",
                'python': """class Solution:
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        # Python heapq is a min-heap, so negate distance for max-heap behavior
        max_heap = []
        for x, y in points:
            dist = -(x * x + y * y)
            heapq.heappush(max_heap, (dist, [x, y]))
            if len(max_heap) > k:
                heapq.heappop(max_heap)
                
        return [pt for (_, pt) in max_heap]""",
                'java': """class Solution {
    public int[][] kClosest(int[][] points, int k) {
        // Max-heap based on squared distance
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> {
            int distA = a[0] * a[0] + a[1] * a[1];
            int distB = b[0] * b[0] + b[1] * b[1];
            return Integer.compare(distB, distA);
        });

        for (int[] pt : points) {
            maxHeap.offer(pt);
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }
        return result;
    }
}""",
                'explanation': "Using squared distance eliminates floating-point operations. The heap never grows past `k + 1` elements, guaranteeing strict O(K) memory and O(N log K) execution time."
            },
            {
                'name': "Approach 2: QuickSelect (In-Place Average Linear)",
                'desc': "Use QuickSelect to partition the array so that the k closest points are in `points[0..k-1]`.",
                'time': "O(N) Average, O(N^2) Worst",
                'space': "O(1) Auxiliary",
                'kotlin': """class Solution {
    fun kClosest(points: Array<IntArray>, k: Int): Array<IntArray> {
        var left = 0
        var right = points.size - 1

        while (left <= right) {
            val pivotIdx = partition(points, left, right)
            when {
                pivotIdx == k -> break
                pivotIdx < k -> left = pivotIdx + 1
                else -> right = pivotIdx - 1
            }
        }
        return points.copyOfRange(0, k)
    }

    private fun partition(points: Array<IntArray>, left: Int, right: Int): Int {
        val pivotDist = dist(points[right])
        var p = left
        for (i in left until right) {
            if (dist(points[i]) <= pivotDist) {
                val temp = points[p]
                points[p] = points[i]
                points[i] = temp
                p++
            }
        }
        val temp = points[p]
        points[p] = points[right]
        points[right] = temp
        return p
    }

    private fun dist(p: IntArray) = p[0] * p[0] + p[1] * p[1]
}""",
                'python': """class Solution:
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        def dist(p):
            return p[0]**2 + p[1]**2

        left, right = 0, len(points) - 1
        while left <= right:
            pivot_dist = dist(points[right])
            p = left
            for i in range(left, right):
                if dist(points[i]) <= pivot_dist:
                    points[p], points[i] = points[i], points[p]
                    p += 1
            points[p], points[right] = points[right], points[p]

            if p == k:
                break
            elif p < k:
                left = p + 1
            else:
                right = p - 1

        return points[:k]""",
                'java': """class Solution {
    public int[][] kClosest(int[][] points, int k) {
        int left = 0, right = points.length - 1;
        while (left <= right) {
            int p = partition(points, left, right);
            if (p == k) break;
            else if (p < k) left = p + 1;
            else right = p - 1;
        }
        return Arrays.copyOfRange(points, 0, k);
    }

    private int partition(int[][] points, int left, int right) {
        int pivotDist = dist(points[right]);
        int p = left;
        for (int i = left; i < right; i++) {
            if (dist(points[i]) <= pivotDist) {
                int[] temp = points[p];
                points[p] = points[i];
                points[i] = temp;
                p++;
            }
        }
        int[] temp = points[p];
        points[p] = points[right];
        points[right] = temp;
        return p;
    }

    private int dist(int[] p) {
        return p[0] * p[0] + p[1] * p[1];
    }
}""",
                'explanation': "QuickSelect reorders points in place so that the k smallest distances end up in indices `0` to `k-1`. Operates in average O(N) time with zero extra heap memory."
            }
        ],
        'scratchpad': {
            'initial_code': """import heapq

def kClosest(points, k):
    max_heap = []
    for x, y in points:
        d = -(x*x + y*y)
        heapq.heappush(max_heap, (d, [x, y]))
        if len(max_heap) > k:
            heapq.heappop(max_heap)
    return [p for (_, p) in max_heap]

pts = [[3, 3], [5, -1], [-2, 4]]
k = 2
print("K closest points:", kClosest(pts, k))
""",
            'test_cases_json': """[
  {"input": "points = [[1,3],[-2,2]], k = 1", "expected": "[[-2,2]]"},
  {"input": "points = [[3,3],[5,-1],[-2,4]], k = 2", "expected": "[[3,3],[-2,4]]"}
]"""
        },
        'interview_dialogue': {
            'opening': """<p><strong>Candidate:</strong> "We need the k points with the smallest Euclidean distance to (0,0). Euclidean distance is <code>sqrt(x^2 + y^2)</code>, but since square root is monotonic, comparing <code>x^2 + y^2</code> directly gives identical ordering without floating-point inaccuracies."</p>
<p><strong>Interviewer:</strong> "Which data structure will you use to keep only the k closest?"</p>
<p><strong>Candidate:</strong> "A Max-Heap of capacity k. The heap root always holds the point with the largest distance among our current k candidates. When a new point is closer, we push it and pop the root. At the end, the heap contains the k closest points in O(N log K) time and O(K) space."</p>""",
            'follow_ups': [
                {
                    'question': "Could integer overflow occur when computing x^2 + y^2?",
                    'answer': "Given constraints `x, y <= 10^4`, `x^2 + y^2 <= 2 * 10^8`, which fits comfortably inside a standard signed 32-bit integer (max ~2.14 * 10^9). If coordinates exceeded 46,340, we would cast to 64-bit Long."
                },
                {
                    'question': "Why avoid subtracting distances directly in Java/Kotlin comparators like `(a - b)`?",
                    'answer': "Direct subtraction can cause integer underflow/overflow if coordinates are large. Using `Integer.compare(bDist, aDist)` is always safe against numeric overflow."
                }
            ]
        },
        'mcqs': [
            {
                'question': "Why is comparing `x^2 + y^2` mathematically equivalent to comparing `sqrt(x^2 + y^2)` for Euclidean distance?",
                'options': [
                    "(Recommended) Because f(x) = sqrt(x) is strictly monotonically increasing for all non-negative numbers, preserving order.",
                    "Because integer values can never be negative.",
                    "Because floating-point numbers are automatically converted to integers.",
                    "Because origin coordinates are always zero."
                ],
                'correct': 0,
                'explanation': "Monotonicity guarantees that $a < b \\iff \\sqrt{a} < \\sqrt{b}$ for all non-negative reals, allowing us to avoid expensive and precision-lossy square root calls."
            },
            {
                'question': "To find the K SMALLEST elements in a dataset using a heap of size K, which heap type must you use?",
                'options': [
                    "Min-Heap",
                    "(Recommended) Max-Heap",
                    "Fibonacci Heap",
                    "Binomial Heap"
                ],
                'correct': 1,
                'explanation': "A Max-Heap exposes the LARGEST element currently in the top-K set at the root, allowing you to kick it out whenever a smaller candidate arrives."
            }
        ]
    },

    # ---------------- 0045: TOP K FREQUENT ELEMENTS ----------------
    {
        'num': 45,
        'lc': 347,
        'title': "Top K Frequent Elements",
        'category': "Heaps & Priority Queues",
        'difficulty': "Medium",
        'lc_slug': "top-k-frequent-elements",
        'verbatim_statement': """<p>Given an integer array <code>nums</code> and an integer <code>k</code>, return <em>the</em> <code>k</code> <em>most frequent elements</em>. You may return the answer in <strong>any order</strong>.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'nums = [1,1,1,2,2,3], k = 2', 'output': '[1,2]'},
            {'title': "Example 2", 'input': 'nums = [1], k = 1', 'output': '[1]'}
        ],
        'constraints': [
            "1 <= nums.length <= 10^5",
            "-10^4 <= nums[i] <= 10^4",
            "k is in the range [1, the number of unique elements in the array].",
            "It is guaranteed that the answer is unique."
        ],
        'plain_english': "Count occurrences of each number in a frequency map. Then either use a Min-Heap of size `k` to track the top frequencies in `O(N log K)` time, or use Bucket Sort where array index represents frequency to achieve strictly linear `O(N)` time.",
        'boundary_checks': {
            'min_base': "<code>nums = [1], k = 1</code> &rarr; Single element returns <code>[1]</code> directly.",
            'best_exit': "<code>k == unique elements count</code> &rarr; Returns all unique elements.",
            'worst_scan': "All elements have identical frequency (e.g., [1, 2, 3, 4], k = 2) &rarr; Problem guarantees unique top K answer."
        },
        'mental_model': {
            'analogy_title': "The Frequency Pigeonhole Shelves (Bucket Sort)",
            'analogy_text': "Suppose you have N ballots. You count the votes for each candidate. You set up N + 1 pigeonhole shelves numbered 0 to N, representing the number of votes received. A candidate with 3 votes goes onto Shelf #3; a candidate with 10 votes goes onto Shelf #10. To find the top K candidates, you simply walk from Shelf #N down to Shelf #0, collecting candidates until you have K winners. No sorting required!",
            'aha_moment': "An element's frequency can NEVER exceed N (the array length). Because the frequency range is bounded between 0 and N, Bucket Sort solves Top K in strict O(N) linear time without any heap or comparison sorting!",
            'trigger': "Top K frequent / count-based rankings with bounded frequencies &rarr; Bucket Sort (index = frequency) or Min-Heap of size K.",
            'sisters': [
                {'name': "Sort Characters By Frequency", 'lc': "LC #451"},
                {'name': "Kth Largest Element in an Array", 'lc': "LC #215"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(U log U) Time, O(U) Space",
            'naive_text': "Build frequency map of unique elements U, convert to list of pairs, and sort by frequency descending.",
            'wasted_work': "Sorting all U unique elements when we only need the top k.",
            'optimal_complexity': "O(N) Time, O(N) Space",
            'optimal_text': "Bucket Sort: create array of lists of size N + 1 where `buckets[freq]` contains numbers with that count. Traverse backward from N to 0.",
            'core_invariant': "Bucket index `f` strictly represents the exact count of occurrences in the input array.",
            'table_rows': [
                {'name': "1. Frequency Map + Full Sort", 'time': "O(U log U)", 'space': "O(U)", 'note': "Sorts all unique keys; U up to N."},
                {'name': "2. Frequency Map + Min-Heap of Size K", 'time': "O(N log K)", 'space': "O(N + K)", 'note': "Standard heap approach; ideal for streaming data."},
                {'name': "★ 3. Frequency Map + Bucket Sort", 'time': "O(N)", 'space': "O(N)", 'note': "Strict linear O(N) time using bounded count pigeonholes."}
            ]
        },
        'trace_table': {
            'input_desc': 'nums = [1, 1, 1, 2, 2, 3], k = 2 (N = 6)',
            'columns': ["Stage", "Frequencies Map", "Bucket Index (Count)", "Numbers in Bucket", "Collected Result (k=2)"],
            'rows': [
                ["Count Map", "{1: 3, 2: 2, 3: 1}", "-", "-", "[]"],
                ["Fill Buckets", "-", "Bucket 1", "[3]", "[]"],
                ["Fill Buckets", "-", "Bucket 2", "[2]", "[]"],
                ["Fill Buckets", "-", "Bucket 3", "[1]", "[]"],
                ["Scan Shelves", "-", "Scan Bucket 6..4", "Empty", "[]"],
                ["Scan Shelves", "-", "Scan Bucket 3", "[1] &rarr; Collect 1", "[1]"],
                ["Scan Shelves", "-", "Scan Bucket 2", "[2] &rarr; Collect 2", "[1, 2] &rarr; Target reached!"]
            ],
            'takeaway': "Walking backward from bucket N to 0 extracts highest frequencies first in guaranteed O(N) time."
        },
        'stepper': {
            'title': "Bucket Sort Top K Frequent Stepper",
            'default_input': '1, 1, 1, 2, 2, 3',
            'presets': [
                {'label': "Example [1,1,1,2,2,3], k=2", 'onclick': 'loadPreset([1, 1, 1, 2, 2, 3], 2)'},
                {'label': "Distinct [4, 1, -1, 2, -1, 2, 3], k=2", 'onclick': 'loadPreset([4, 1, -1, 2, -1, 2, 3], 2)'}
            ],
            'metrics': [
                {'id': "metric-freq", 'label': "Examining Bucket", 'color': "text-amber-400", 'initial': "-"},
                {'id': "metric-items", 'label': "Bucket Contents", 'color': "text-cyan-400", 'initial': "[]"},
                {'id': "metric-res", 'label': "Collected Top K", 'color': "text-emerald-400", 'initial': "[]"},
                {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
            ],
            'visual_html': '<div id="bucket-stage" class="w-full flex items-center justify-center gap-2 py-4 min-h-[140px] overflow-x-auto"></div>',
            'initial_narration': 'Click "Next Step" to trace bucket scanning from highest frequency downward.',
            'pseudocode_lines': [
                "val countMap = nums.toList().groupingBy { it }.eachCount()",
                "val buckets = Array(nums.size + 1) { mutableListOf<Int>() }",
                "for ((num, count) in countMap) buckets[count].add(num)",
                "val result = IntArray(k)",
                "var idx = 0",
                "for (freq in buckets.indices.reversed()) {",
                "    for (num in buckets[freq]) {",
                "        result[idx++] = num",
                "        if (idx == k) return result",
                "    }",
                "}"
            ],
            'js_code': """
    let dataList = [1, 1, 1, 2, 2, 3];
    let topK = 2;

    function buildSteps(arr, k) {
      steps = [];
      let counts = {};
      arr.forEach(x => counts[x] = (counts[x] || 0) + 1);

      let n = arr.length;
      let buckets = Array.from({ length: n + 1 }, () => []);
      Object.keys(counts).forEach(num => {
        let cnt = counts[num];
        buckets[cnt].push(Number(num));
      });

      steps.push({
        freq: "-",
        items: "-",
        res: "[]",
        action: "Frequencies Counted",
        buckets: buckets,
        activeFreq: -1,
        line: 3,
        narration: `Calculated frequencies: ${JSON.stringify(counts)}. Populated buckets 0 to ${n}.`
      });

      let res = [];
      for (let f = n; f >= 1; f--) {
        if (buckets[f].length > 0) {
          steps.push({
            freq: `Bucket ${f}`,
            items: `[${buckets[f].join(', ')}]`,
            res: `[${res.join(', ')}]`,
            action: `Inspecting Shelf ${f}`,
            buckets: buckets,
            activeFreq: f,
            line: 6,
            narration: `Found numbers with frequency ${f}: [${buckets[f].join(', ')}].`
          });

          for (let num of buckets[f]) {
            res.push(num);
            steps.push({
              freq: `Bucket ${f}`,
              items: `[${buckets[f].join(', ')}]`,
              res: `[${res.join(', ')}]`,
              action: `Added ${num}`,
              buckets: buckets,
              activeFreq: f,
              line: 8,
              narration: `Collected ${num} into top K result (size: ${res.length}/${k}).`
            });
            if (res.length === k) {
              steps.push({
                freq: "Complete",
                items: "-",
                res: `[${res.join(', ')}]`,
                action: "Reached Target K",
                buckets: buckets,
                activeFreq: f,
                line: 9,
                narration: `Collected all ${k} top frequent elements! Done.`
              });
              return;
            }
          }
        }
      }
    }

    function renderStep(step) {
      document.getElementById('metric-freq').textContent = step.freq;
      document.getElementById('metric-items').textContent = step.items;
      document.getElementById('metric-res').textContent = step.res;
      document.getElementById('metric-action').textContent = step.action;

      const stage = document.getElementById('bucket-stage');
      stage.innerHTML = '';
      if (!step.buckets) return;

      // Render only non-empty buckets plus active
      step.buckets.forEach((b, idx) => {
        if (b.length === 0 && idx !== step.activeFreq) return;
        const box = document.createElement('div');
        const isActive = idx === step.activeFreq;
        box.className = `flex flex-col items-center justify-center p-2 rounded-lg border min-w-[60px] font-mono ${
          isActive ? 'bg-amber-500/20 text-amber-300 border-amber-500 ring-2 ring-amber-500/30' : 'bg-zinc-800 text-zinc-300 border-zinc-700'
        }`;
        box.innerHTML = `
          <span class="text-xs text-zinc-400">Freq ${idx}</span>
          <span class="text-sm font-bold">${b.length > 0 ? b.join(', ') : 'Ø'}</span>
        `;
        stage.appendChild(box);
      });
    }

    function loadPreset(arr, k) {
      dataList = arr;
      topK = k;
      buildSteps(dataList, topK);
      currentStepIndex = 0;
      updateUI();
    }

    window.addEventListener('load', () => {
      buildSteps(dataList, topK);
      updateUI();
    });
"""
        },
        'approaches': [
            {
                'name': "Approach 1: Bucket Sort by Frequency (Optimal Linear Time)",
                'desc': "Count occurrences in a HashMap. Place numbers into buckets where index = frequency. Collect top k from highest bucket.",
                'time': "O(N)",
                'space': "O(N)",
                'kotlin': """class Solution {
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        val countMap = HashMap<Int, Int>()
        for (num in nums) {
            countMap[num] = countMap.getOrDefault(num, 0) + 1
        }

        // Buckets where index represents frequency (0 to nums.size)
        val buckets = Array(nums.size + 1) { mutableListOf<Int>() }
        for ((num, count) in countMap) {
            buckets[count].add(num)
        }

        val result = IntArray(k)
        var idx = 0
        for (freq in buckets.indices.reversed()) {
            for (num in buckets[freq]) {
                result[idx++] = num
                if (idx == k) return result
            }
        }
        return result
    }
}""",
                'python': """class Solution:
    def topKFrequent(self, nums: List[int], k: int) -> List[int]:
        count_map = collections.Counter(nums)
        n = len(nums)
        buckets = [[] for _ in range(n + 1)]
        
        for num, count in count_map.items():
            buckets[count].append(num)
            
        result = []
        for freq in range(n, 0, -1):
            for num in buckets[freq]:
                result.append(num)
                if len(result) == k:
                    return result
                    
        return result""",
                'java': """class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int num : nums) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        }

        List<Integer>[] buckets = new List[nums.length + 1];
        for (int i = 0; i <= nums.length; i++) {
            buckets[i] = new ArrayList<>();
        }

        for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
            int num = entry.getKey();
            int count = entry.getValue();
            buckets[count].add(num);
        }

        int[] result = new int[k];
        int idx = 0;
        for (int freq = nums.length; freq >= 0 && idx < k; freq--) {
            for (int num : buckets[freq]) {
                result[idx++] = num;
                if (idx == k) return result;
            }
        }
        return result;
    }
}""",
                'explanation': "Because frequency is bounded by N, Bucket Sort completely bypasses the O(N log N) comparison sort barrier, solving the problem in guaranteed linear O(N) time and space."
            },
            {
                'name': "Approach 2: Min-Heap of Size K (Streaming-Optimal)",
                'desc': "Use a Min-Heap of size k comparing map counts. Eject lowest frequency element when heap size exceeds k.",
                'time': "O(N log K)",
                'space': "O(N + K)",
                'kotlin': """class Solution {
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        val countMap = HashMap<Int, Int>()
        for (num in nums) countMap[num] = countMap.getOrDefault(num, 0) + 1

        val minHeap = java.util.PriorityQueue<Int> { a, b ->
            countMap[a]!!.compareTo(countMap[b]!!)
        }

        for (num in countMap.keys) {
            minHeap.offer(num)
            if (minHeap.size > k) minHeap.poll()
        }

        val result = IntArray(k)
        for (i in 0 until k) result[i] = minHeap.poll()
        return result
    }
}""",
                'python': """class Solution:
    def topKFrequent(self, nums: List[int], k: int) -> List[int]:
        count_map = collections.Counter(nums)
        min_heap = []
        
        for num, freq in count_map.items():
            heapq.heappush(min_heap, (freq, num))
            if len(min_heap) > k:
                heapq.heappop(min_heap)
                
        return [num for (freq, num) in min_heap]""",
                'java': """class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int num : nums) countMap.put(num, countMap.getOrDefault(num, 0) + 1);

        PriorityQueue<Integer> minHeap = new PriorityQueue<>(
            Comparator.comparingInt(countMap::get)
        );

        for (int num : countMap.keySet()) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = minHeap.poll();
        }
        return result;
    }
}""",
                'explanation': "Min-Heap retains only the top k most frequent numbers. Useful when memory is constrained or numbers arrive over an ongoing socket connection."
            }
        ],
        'scratchpad': {
            'initial_code': """import collections

def topKFrequent(nums, k):
    counts = collections.Counter(nums)
    buckets = [[] for _ in range(len(nums) + 1)]
    for n, c in counts.items():
        buckets[c].append(n)
    res = []
    for f in range(len(nums), 0, -1):
        for n in buckets[f]:
            res.append(n)
            if len(res) == k:
                return res
    return res

arr = [1, 1, 1, 2, 2, 3]
k = 2
print("Top 2 frequent:", topKFrequent(arr, k))
""",
            'test_cases_json': """[
  {"input": "nums = [1,1,1,2,2,3], k = 2", "expected": "[1,2]"},
  {"input": "nums = [1], k = 1", "expected": "[1]"}
]"""
        },
        'interview_dialogue': {
            'opening': """<p><strong>Candidate:</strong> "First we tally frequencies using a hash map in O(N). To get the top k, sorting takes O(U log U), and a Min-Heap of size k takes O(N log K). But we can achieve strict O(N) linear time using Bucket Sort."</p>
<p><strong>Interviewer:</strong> "How does Bucket Sort achieve O(N) here?"</p>
<p><strong>Candidate:</strong> "Because no number can appear more than N times, the maximum possible frequency is N. We allocate an array of lists of size N + 1 where index represents frequency. We insert each number into <code>buckets[count]</code>, then traverse backward from index N to 1. We stop as soon as we collect k elements."</p>""",
            'follow_ups': [
                {
                    'question': "What is the worst-case space complexity of the Bucket Sort approach?",
                    'answer': "O(N). The frequency map holds at most N keys, and the bucket list holds exactly U elements across all sublists, with array overhead of N + 1."
                },
                {
                    'question': "When would you prefer Min-Heap over Bucket Sort?",
                    'answer': "When data is streamed continuously (N is unbounded, so you cannot pre-allocate an array of size N + 1), or when K is very small (e.g. k = 1 or 2) and N is billions."
                }
            ]
        },
        'mcqs': [
            {
                'question': "Why is Bucket Sort able to achieve O(N) time for Top K Frequent Elements, beating the standard O(N log N) sorting limit?",
                'options': [
                    "(Recommended) Because the range of possible frequencies is strictly bounded between 1 and N, permitting pigeonhole bucket distribution in linear time.",
                    "Because integers in the array are positive.",
                    "Because it utilizes hardware-level parallel GPU sorting.",
                    "Because the answer is guaranteed to be unique."
                ],
                'correct': 0,
                'explanation': "Comparison sorts are bounded by $\\Omega(N \\log N)$, but counting/bucket sorts on bounded keys $[0..N]$ execute in $O(N)$ linear time."
            },
            {
                'question': "In the Min-Heap approach, why do we use a MIN-heap instead of a MAX-heap to find the top K frequent elements?",
                'options': [
                    "Min-Heaps have smaller memory footprint than Max-Heaps.",
                    "(Recommended) A Min-Heap of size K lets us evict the smallest frequency among the current top K candidates in O(log K) whenever a larger candidate appears.",
                    "Max-Heaps cannot store negative numbers.",
                    "Java's Collections framework only supports Min-Heaps."
                ],
                'correct': 1,
                'explanation': "The Min-Heap keeps the least frequent of the top-K winners at the root so it can be evicted immediately when a higher-frequency element arrives."
            }
        ]
    }
]

def main():
    c = open('master-plan.html').read()
    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),\s*title:\s*\"([^\"]+)\",\s*lc:\s*([^,]+),\s*pass:\s*\"([^\"]+)\".*?file:\s*\"([^\"]+)\"', c):
        num = int(m.group(1))
        fname = m.group(5)
        files[num] = fname

    print(f"Generating Cluster 4: {len(cluster4_problems)} problems (39 to 45)...")
    for p in cluster4_problems:
        num = p['num']
        p['prev_file'] = files[num - 1]
        p['next_file'] = files[num + 1]
        fname = f"lessons/{files[num]}"
        html = render_lesson(p)
        with open(fname, 'w', encoding='utf-8') as f:
            f.write(html)
        print(f"  Generated {fname} ({len(html)} bytes)")

if __name__ == '__main__':
    main()
