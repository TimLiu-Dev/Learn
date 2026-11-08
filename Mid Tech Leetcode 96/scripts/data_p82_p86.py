import sys
sys.path.append('scripts')

p82 = {
    'num': 82,
    'lc': 133,
    'title': "Clone Graph",
    'category': "Graphs",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "clone-graph",
    'verbatim_statement': """<p>Given a reference of a node in a <strong>connected</strong> undirected graph.</p>
<p>Return a <a href="https://en.wikipedia.org/wiki/Object_copying#Deep_copy" target="_blank"><strong>deep copy</strong></a> (clone) of the graph.</p>
<p>Each node in the graph contains a value (<code>int</code>) and a list (<code>List[Node]</code>) of its neighbors.</p>
<pre>
class Node {
    public int val;
    public List&lt;Node&gt; neighbors;
}
</pre>
<p><strong>Test case format:</strong> For simplicity, each node's value is the same as the node's index (1-indexed). For example, the first node with <code>val == 1</code>, the second node with <code>val == 2</code>, and so on. The graph is represented in the test case using an adjacency list.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'adjList = [[2,4],[1,3],[2,4],[1,3]]', 'output': '[[2,4],[1,3],[2,4],[1,3]]\nExplanation: There are 4 nodes in the graph.\n1st node (val = 1)\'s neighbors are 2nd node (val = 2) and 4th node (val = 4).\n2nd node (val = 2)\'s neighbors are 1st node (val = 1) and 3rd node (val = 3).\n3rd node (val = 3)\'s neighbors are 2nd node (val = 2) and 4th node (val = 4).\n4th node (val = 4)\'s neighbors are 1st node (val = 1) and 3rd node (val = 3).'},
        {'title': "Example 2", 'input': 'adjList = [[]]', 'output': '[[]]\nExplanation: Note that the input contains one empty list. The graph consists of only one node with val = 1 and it does not have any neighbors.'},
        {'title': "Example 3", 'input': 'adjList = []', 'output': '[]\nExplanation: This an empty graph, it does not have any nodes.'}
    ],
    'constraints': [
        "The number of nodes in the graph is in the range [0, 100].",
        "1 <= Node.val <= 100",
        "Node.val is unique for each node.",
        "There are no repeated edges and no self-loops in the graph.",
        "The Graph is connected and all nodes can be visited starting from the given node."
    ],
    'plain_english': "A deep copy means every node and neighbor list must be brand-new memory objects, with the exact same values and connectivity as the original graph. Because graphs have cycles, navigating without tracking visited nodes causes infinite recursion. We maintain a hash map `cloned = HashMap<Node, Node>()` mapping original nodes to their cloned copies. For any node, if it's already in the map, return the existing copy. Otherwise, create `Node(node.val)`, store it in the map, and recursively clone each neighbor, adding each clone to `copy.neighbors`.",
    'boundary_checks': {
        'min_base': "Null input <code>node == null</code> &rarr; Return null immediately.",
        'best_exit': "Single node with no neighbors <code>adjList = [[]]</code> &rarr; Returns a clone with <code>val=1</code> and empty neighbor list.",
        'worst_scan': "Fully connected cycle (e.g. 4-node ring) &rarr; Visited hash map prevents cycles; each node and edge traversed exactly once."
    },
    'mental_model': {
        'analogy_title': "The Identity Registry & 3D Printer",
        'analogy_text': "Imagine a network of friends holding hands in circles. To clone this network, you walk up to person 1. If you haven't 3D-printed a twin for them yet, you print `Twin(1)` and write their name in your registry: `Original 1 -> Twin 1`. Then you look at person 1's friends. For each friend, you consult the registry: if their twin already exists, you simply have `Twin(1)` hold hands with that existing twin. If not, you print that friend's twin and recurse. Because of the registry, you never print the same person twice and never get stuck in an endless loop.",
        'aha_moment': "Put the cloned node into the map BEFORE recursing on its neighbors! If you wait until after recursing, a cycle will try to clone the original node again, causing a stack overflow.",
        'trigger': "Deep copy of cyclic data structure / graph &rarr; Hash Map (`original -> clone`) combined with DFS or BFS.",
        'sisters': [
            {'name': "Copy List with Random Pointer", 'lc': "LC #138"},
            {'name': "Clone Binary Tree With Random Node", 'lc': "LC #1485"},
            {'name': "Clone N-ary Tree", 'lc': "LC #1490"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(V + E) Time, O(V) Space",
        'naive_text': "DFS/BFS with HashMap. Since every node and edge must be duplicated, O(V + E) is both theoretically and practically optimal.",
        'wasted_work': "Re-instantiating nodes or getting stuck in infinite loops if a visited set/map is omitted.",
        'optimal_complexity': "O(V + E) Time, O(V) Space",
        'optimal_text': "HashMap + DFS recursion or BFS queue. Traverses every vertex once and every edge twice (undirected).",
        'core_invariant': "Every unique node `u` in the original graph corresponds to exactly one node `cloned[u]` in the cloned graph, created before its outgoing edges are explored.",
        'table_rows': [
            {'name': "DFS with Map (Pre-order)", 'time': "O(V + E)", 'space': "O(V)", 'note': "Cleanest code; recursion stack depth bounded by V."},
            {'name': "BFS with Map + Queue", 'time': "O(V + E)", 'space': "O(V)", 'note': "Iterative; avoids call stack overhead for very deep graphs."}
        ]
    },
    'trace_table': {
        'input_desc': 'Graph: 1-2, 2-3, 3-4, 4-1 (4-node cycle)',
        'columns': ["Step", "Call Node", "Action", "Map State (`orig -> copy`)", "Neighbors Linked"],
        'rows': [
            ["1", "Node 1", "Clone Node 1", "{1: 1'}", "None yet"],
            ["2", "Node 2", "Clone Node 2", "{1: 1', 2: 2'}", "1' links to 2'"],
            ["3", "Node 3", "Clone Node 3", "{1: 1', 2: 2', 3: 3'}", "2' links to 3'"],
            ["4", "Node 4", "Clone Node 4", "{1: 1', 2: 2', 3: 3', 4: 4'}", "3' links to 4'"],
            ["5", "Node 1", "Found in map! Return 1'", "{1: 1', 2: 2', 3: 3', 4: 4'}", "4' links to 1' (cycle resolved)"],
            ["6", "Done", "Unwind DFS", "All 4 nodes cloned", "Complete mirrored graph returned"]
        ],
        'takeaway': "Cycle 4 -> 1 immediately resolved because Node 1 already exists in the map."
    },
    'stepper': {
        'title': "Graph Deep Copy Stepper",
        'default_input': 'adjList=[[2,4],[1,3],[2,4],[1,3]]',
        'presets': [
            {'label': "4-Node Cycle", 'onclick': 'loadPreset([[2,4],[1,3],[2,4],[1,3]])'},
            {'label': "Single Node", 'onclick': 'loadPreset([[]])'},
            {'label': "Linear Path (1-2-3)", 'onclick': 'loadPreset([[2],[1,3],[2]])'}
        ],
        'metrics': [
            {'id': "metric-current-node", 'label': "Current Node", 'color': "text-amber-400", 'initial': "1"},
            {'id': "metric-map-size", 'label': "Cloned Count", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"},
            {'id': "metric-queue", 'label': "Stack Depth", 'color': "text-emerald-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p82-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[180px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to watch DFS clone nodes and link duplicate-safe edges.',
        'pseudocode_lines': [
            "fun cloneGraph(node: Node?): Node? {",
            "    if (node == null) return null",
            "    if (cloned.containsKey(node)) return cloned[node]!",
            "    val copy = Node(node.`val`)",
            "    cloned[node] = copy",
            "    for (neighbor in node.neighbors) {",
            "        copy.neighbors.add(cloneGraph(neighbor))",
            "    }",
            "    return copy",
            "}"
        ],
        'js_code': """
    let rawAdj = [[2,4],[1,3],[2,4],[1,3]];

    function buildSteps(adj) {
      steps = [];
      if (!adj || adj.length === 0) {
        steps.push({ node: "null", count: 0, action: "Empty Graph", depth: 0, line: 2, narration: "Input is empty, return null immediately." });
        return;
      }

      const map = {};
      steps.push({
        node: "1", count: 0, action: "Start DFS", depth: 1, line: 1,
        narration: `Starting DFS from root Node 1 with ${adj.length} total nodes.`
      });

      function dfs(val, depth) {
        if (map[val]) {
          steps.push({
            node: val, count: Object.keys(map).length, action: `Lookup Map: Node ${val} exists`, depth: depth, line: 3,
            narration: `Node ${val} already cloned! Reusing cloned instance without re-traversal.`
          });
          return;
        }

        map[val] = true;
        steps.push({
          node: val, count: Object.keys(map).length, action: `Created Clone(${val})`, depth: depth, line: 5,
          narration: `Created clone for Node ${val}. Added to cloned map.`
        });

        const neighbors = adj[val - 1] || [];
        for (let n of neighbors) {
          steps.push({
            node: val, count: Object.keys(map).length, action: `Visit neighbor ${n}`, depth: depth + 1, line: 6,
            narration: `Node ${val} visiting neighbor ${n}.`
          });
          dfs(n, depth + 1);
        }
      }

      dfs(1, 1);

      steps.push({
        node: "Done", count: Object.keys(map).length, action: "Finished Clone", depth: 0, line: 9,
        narration: `All ${Object.keys(map).length} nodes and their bidirectional edges cloned successfully!`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p82-stage');
      if (!stage) return;
      document.getElementById('metric-current-node').textContent = step.node;
      document.getElementById('metric-map-size').textContent = step.count;
      document.getElementById('metric-action').textContent = step.action;
      document.getElementById('metric-queue').textContent = step.depth;

      let html = '<div class="flex items-center gap-6">';
      for (let i = 1; i <= rawAdj.length; i++) {
        const isCloned = (step.count >= i);
        const isCur = (step.node === i);
        const bg = isCur ? 'bg-amber-500 text-black border-amber-300 ring-2 ring-amber-400' : (isCloned ? 'bg-emerald-600/40 text-emerald-200 border-emerald-500' : 'bg-slate-800 text-slate-400 border-slate-700');
        html += `<div class="flex flex-col items-center">
                   <div class="w-12 h-12 rounded-full flex items-center justify-center font-bold border ${bg} transition-all duration-300">
                     Node ${i}
                   </div>
                   <span class="text-xs mt-1 text-slate-400">${isCloned ? "Cloned" : "Pending"}</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(adj) {
      rawAdj = adj;
      buildSteps(adj);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawAdj);
"""
    },
    'approaches': [
        {
            'name': "DFS with Cloned Node Hash Map",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(V + E) Time, O(V) Space",
            'summary': "Depth-first traversal with a hash map `cloned = HashMap<Node, Node>()`. Instantiate and store each node before exploring its neighbors to avoid cycle recursion.",
            'kotlin_code': """class Solution {
    private val cloned = HashMap<Node, Node>()

    fun cloneGraph(node: Node?): Node? {
        if (node == null) return null
        // If already cloned, return existing copy (breaks cycles)
        if (cloned.containsKey(node)) return cloned[node]

        // Create copy and record immediately before recursing on neighbors
        val copy = Node(node.`val`)
        cloned[node] = copy

        for (neighbor in node.neighbors) {
            neighbor?.let {
                copy.neighbors.add(cloneGraph(it))
            }
        }
        return copy
    }
}""",
            'python_code': """class Solution:
    def cloneGraph(self, node: 'Optional[Node]') -> 'Optional[Node]':
        if not node:
            return None
        
        cloned = {}
        
        def dfs(curr: 'Node') -> 'Node':
            if curr in cloned:
                return cloned[curr]
            
            copy = Node(curr.val)
            cloned[curr] = copy
            
            for neighbor in curr.neighbors:
                copy.neighbors.append(dfs(neighbor))
            return copy
        
        return dfs(node)""",
            'java_code': """class Solution {
    private final Map<Node, Node> cloned = new HashMap<>();

    public Node cloneGraph(Node node) {
        if (node == null) return null;
        if (cloned.containsKey(node)) return cloned.get(node);

        Node copy = new Node(node.val);
        cloned.put(node, copy);

        for (Node neighbor : node.neighbors) {
            copy.neighbors.add(cloneGraph(neighbor));
        }
        return copy;
    }
}""",
            'memory_breakdown': [
                {'component': "HashMap `cloned`", 'cost': "O(V)", 'description': "Stores references for all V unique nodes."},
                {'component': "Call Stack", 'cost': "O(V)", 'description': "Recursion depth equal to graph diameter/longest simple path, at most V."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun cloneGraph(node: Node?): Node? {
    // Write your solution here
    return null
}""",
        'python_boilerplate': """def cloneGraph(node: 'Optional[Node]') -> 'Optional[Node]':
    # Write your solution here
    return None""",
        'java_boilerplate': """public Node cloneGraph(Node node) {
    // Write your solution here
    return null;
}""",
        'js_test_runner': """
function Node(val, neighbors) {
    this.val = (val===undefined ? 0 : val);
    this.neighbors = (neighbors===undefined ? [] : neighbors);
}

function buildGraph(adj) {
    if (!adj || adj.length === 0) return null;
    const nodes = adj.map((_, i) => new Node(i + 1));
    for (let i = 0; i < adj.length; i++) {
        for (let neighborVal of adj[i]) {
            nodes[i].neighbors.push(nodes[neighborVal - 1]);
        }
    }
    return nodes[0];
}

function graphToAdj(node) {
    if (!node) return [];
    const map = new Map();
    function dfs(n) {
        if (map.has(n.val)) return;
        map.set(n.val, n);
        for (let nbr of n.neighbors) dfs(nbr);
    }
    dfs(node);
    const sortedKeys = Array.from(map.keys()).sort((a,b) => a - b);
    return sortedKeys.map(k => map.get(k).neighbors.map(nbr => nbr.val));
}

function cloneGraph(node) {
    if (!node) return null;
    const cloned = new Map();
    function dfs(curr) {
        if (cloned.has(curr)) return cloned.get(curr);
        const copy = new Node(curr.val);
        cloned.set(curr, copy);
        for (let neighbor of curr.neighbors) {
            copy.neighbors.push(dfs(neighbor));
        }
        return copy;
    }
    return dfs(node);
}

const tests = [
    { input: [[2,4],[1,3],[2,4],[1,3]], expected: [[2,4],[1,3],[2,4],[1,3]] },
    { input: [[]], expected: [[]] },
    { input: [], expected: [] }
];

let passed = 0;
tests.forEach((t, i) => {
    const original = buildGraph(t.input);
    const cloned = cloneGraph(original);
    // Verify different object identity
    const isDistinct = (!original && !cloned) || (original !== cloned);
    const output = graphToAdj(cloned);
    const ok = isDistinct && JSON.stringify(output) === JSON.stringify(t.expected);
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Output: ${JSON.stringify(output)})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's clarify the graph structure: this is a connected undirected graph where each node contains an integer value and a list of neighbor nodes. We need to produce a deep copy: new node objects, new lists, but mirroring the exact values and connectivity.",
        'phase2': "Because graphs can have cycles, a naive traversal will get trapped in an infinite loop. We need a way to remember nodes we have already duplicated. A hash map mapping the original `Node` reference to its `cloned Node` instance is ideal. Whenever we visit a node, we check if it's in our map.",
        'phase3': "Crucially, we must store the new cloned node in the map *before* we iterate through its neighbors. If node A has neighbor B, and B has neighbor A, when B looks up A, A is already in the map, returning immediately and terminating the cycle.",
        'phase4': "Both DFS and BFS yield $O(V + E)$ time and $O(V)$ auxiliary space for the map. Let's write the recursive DFS solution, which is exceptionally concise and handles the base case of null gracefully.",
        'senior_followup': "In a massive distributed graph with millions of vertices where recursion causes stack overflows, we would use an iterative BFS approach with a queue or partition the graph across nodes using a distributed key-value store to cache clone references."
    },
    'quiz': [
        {
            'question': "Why must you insert the cloned node into the map BEFORE recursively cloning its neighbors?",
            'options': [
                "To prevent infinite recursion when cyclic references (e.g., A -> B -> A) look up A",
                "To ensure the cloned node values are sorted in ascending numerical order",
                "Because Java HashMaps throw an exception if items are inserted during post-order traversal",
                "To optimize garbage collection of unused original nodes"
            ],
            'correct_index': 0,
            'explanation': "If you recurse on neighbors before caching the current clone in the map, a cycle pointing back to this node will see it as unvisited and attempt to clone it again, leading to infinite recursion and a StackOverflowError."
        },
        {
            'question': "What is the auxiliary space complexity of Clone Graph on a graph with V vertices and E edges?",
            'options': [
                "O(V) to store the mapping from original nodes to cloned nodes",
                "O(V * E) due to quadratic edge references in the adjacency lists",
                "O(1) auxiliary space because nodes are modified in place",
                "O(E) to track edge traversal states"
            ],
            'correct_index': 0,
            'explanation': "The hash map stores exactly one entry for each of the V vertices, and the recursion stack (or BFS queue) holds at most V vertices, yielding O(V) auxiliary space."
        }
    ]
}

p83 = {
    'num': 83,
    'lc': 207,
    'title': "Course Schedule",
    'category': "Graphs",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "course-schedule",
    'verbatim_statement': """<p>There are a total of <code>numCourses</code> courses you have to take, labeled from <code>0</code> to <code>numCourses - 1</code>. You are given an array <code>prerequisites</code> where <code>prerequisites[i] = [a<sub>i</sub>, b<sub>i</sub>]</code> indicates that you <strong>must</strong> take course <code>b<sub>i</sub></code> first if you want to take course <code>a<sub>i</sub></code>.</p>
<p>For example, the pair <code>[0, 1]</code>, indicates that to take course <code>0</code> you have to first take course <code>1</code>.</p>
<p>Return <code>true</code> if you can finish all courses. Otherwise, return <code>false</code>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'numCourses = 2, prerequisites = [[1,0]]', 'output': 'true\nExplanation: There are a total of 2 courses to take. To take course 1 you should have finished course 0. So it is possible.'},
        {'title': "Example 2", 'input': 'numCourses = 2, prerequisites = [[1,0],[0,1]]', 'output': 'false\nExplanation: There are a total of 2 courses to take. To take course 1 you should have finished course 0, and to take course 0 you should also have finished course 1. So it is impossible.'}
    ],
    'constraints': [
        "1 <= numCourses <= 2000",
        "0 <= prerequisites.length <= 5000",
        "prerequisites[i].length == 2",
        "0 <= ai, bi < numCourses",
        "All the pairs prerequisites[i] are unique."
    ],
    'plain_english': "This is cycle detection in a directed graph. Courses are vertices, and each prerequisite `[a, b]` is a directed edge `b -> a` (course `b` must precede course `a`). You can finish all courses if and only if there are no directed cycles (the graph is a Directed Acyclic Graph, or DAG). We can solve this with Kahn's Algorithm (BFS topological sort): count the in-degree (number of prerequisites) for every course. Add courses with in-degree 0 to a queue. Dequeue each course, increment our completed count, and decrement the in-degree of its outgoing neighbors. Any neighbor whose in-degree drops to 0 is enqueued. If completed courses equals `numCourses`, return true; otherwise a cycle blocked progress, return false.",
    'boundary_checks': {
        'min_base': "No prerequisites <code>prerequisites = []</code> &rarr; All in-degrees are 0; return true immediately.",
        'best_exit': "Linear dependency <code>[[1,0],[2,1]]</code> &rarr; Order 0 -> 1 -> 2; all processed.",
        'worst_scan': "Mutual dependency cycle <code>[[1,0],[0,1]]</code> &rarr; In-degrees remain > 0; queue empties with 0 processed, returns false."
    },
    'mental_model': {
        'analogy_title': "College Degree Prerequisite Tree",
        'analogy_text': "Think of your university syllabus. Freshman year, you can only enroll in courses that have ZERO prerequisites (in-degree 0), like Math 101 and CS 101. Once you pass Math 101, all classes that depended on it have one fewer requirement. As soon as all requirements for Calculus I are checked off (in-degree hits 0), it becomes available in your queue. If every course is unlocked eventually, you graduate. But if Course A requires Course B, and Course B requires Course A, neither can ever be unlocked, and you can never graduate.",
        'aha_moment': "In-degree represents the remaining blocking dependencies. When in-degree reaches 0, the course has NO remaining blockers and can be safely taken.",
        'trigger': "Directed dependencies with feasibility check &rarr; Topological sort via Kahn's algorithm (BFS with in-degree array).",
        'sisters': [
            {'name': "Course Schedule II (Return ordering)", 'lc': "LC #210"},
            {'name': "Alien Dictionary", 'lc': "LC #269"},
            {'name': "Minimum Height Trees", 'lc': "LC #310"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(V * (V + E)) Time, O(V) Space",
        'naive_text': "Run DFS from every single vertex independently to search for a back-edge to an ancestor.",
        'wasted_work': "Re-traversing overlapping directed paths multiple times without memoizing completed components.",
        'optimal_complexity': "O(V + E) Time, O(V + E) Space",
        'optimal_text': "Kahn's Algorithm (BFS) or 3-color DFS. Processes every vertex and edge exactly once.",
        'core_invariant': "Only courses whose prerequisites have all been completely resolved (in-degree == 0) enter the queue.",
        'table_rows': [
            {'name': "DFS with 3 States (White/Gray/Black)", 'time': "O(V + E)", 'space': "O(V + E)", 'note': "Recursion detects back-edges (Gray &rarr; Gray)."},
            {'name': "★ Kahn's Algorithm (BFS + In-Degree)", 'time': "O(V + E)", 'space': "O(V + E)", 'note': "Industry standard; iterative, eliminates recursion stack overflow."}
        ]
    },
    'trace_table': {
        'input_desc': 'numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]',
        'columns': ["Step", "In-Degree Array `[0,1,2,3]`", "Queue State", "Course Popped", "Completed Count", "Neighbors Updated"],
        'rows': [
            ["Init", "[0, 1, 1, 2]", "[0]", "-", "0", "Course 0 has in-degree 0"],
            ["1", "[0, 0, 0, 2]", "[1, 2]", "0", "1", "Decr in-degree of 1 and 2 (both become 0)"],
            ["2", "[0, 0, 0, 1]", "[2, -]", "1", "2", "Decr in-degree of 3 (becomes 1)"],
            ["3", "[0, 0, 0, 0]", "[3]", "2", "3", "Decr in-degree of 3 (becomes 0, push 3)"],
            ["4", "[0, 0, 0, 0]", "[]", "3", "4", "Queue empty. Completed = 4 == numCourses"]
        ],
        'takeaway': "Completed count (4) == numCourses (4). Graph is a DAG; returns true."
    },
    'stepper': {
        'title': "Kahn's Topological Sort Stepper",
        'default_input': 'numCourses=4, prereqs=[[1,0],[2,0],[3,1],[3,2]]',
        'presets': [
            {'label': "Valid DAG: 4 Courses", 'onclick': 'loadPreset(4, [[1,0],[2,0],[3,1],[3,2]])'},
            {'label': "Cycle: 2 Courses", 'onclick': 'loadPreset(2, [[1,0],[0,1]])'},
            {'label': "Independent Courses", 'onclick': 'loadPreset(3, [])'}
        ],
        'metrics': [
            {'id': "metric-queue-items", 'label': "Queue (In-degree 0)", 'color': "text-amber-400", 'initial': "[]"},
            {'id': "metric-completed", 'label': "Courses Taken", 'color': "text-emerald-400", 'initial': "0 / 4"},
            {'id': "metric-current", 'label': "Processing Course", 'color': "text-cyan-400", 'initial': "None"},
            {'id': "metric-can-finish", 'label': "Feasible?", 'color': "text-purple-400", 'initial': "Evaluating"}
        ],
        'visual_html': '<div id="p83-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace in-degree decrements and course completion.',
        'pseudocode_lines': [
            "val inDegree = IntArray(numCourses)",
            "for (p in prerequisites) inDegree[p[0]]++",
            "val q = ArrayDeque<Int>()",
            "for (i in 0 until numCourses) if (inDegree[i] == 0) q.add(i)",
            "var taken = 0",
            "while (q.isNotEmpty()) {",
            "    val u = q.removeFirst(); taken++",
            "    for (v in adj[u]) {",
            "        if (--inDegree[v] == 0) q.add(v)",
            "    }",
            "}",
            "return taken == numCourses"
        ],
        'js_code': """
    let rawN = 4;
    let rawPrereqs = [[1,0],[2,0],[3,1],[3,2]];

    function buildSteps(n, prereqs) {
      steps = [];
      const adj = Array.from({length: n}, () => []);
      const inDegree = new Array(n).fill(0);

      for (let [course, pre] of prereqs) {
        adj[pre].push(course);
        inDegree[course]++;
      }

      const q = [];
      for (let i = 0; i < n; i++) {
        if (inDegree[i] === 0) q.push(i);
      }

      steps.push({
        inDegree: [...inDegree], q: [...q], current: null, taken: 0, n: n,
        line: 4, narration: `Initialized in-degrees. Found ${q.length} courses with 0 prerequisites: [${q.join(', ')}].`
      });

      let taken = 0;
      while (q.length > 0) {
        const u = q.shift();
        taken++;
        steps.push({
          inDegree: [...inDegree], q: [...q], current: u, taken: taken, n: n,
          line: 7, narration: `Taking Course ${u} (in-degree was 0). Total completed: ${taken}.`
        });

        for (let v of adj[u]) {
          inDegree[v]--;
          const unlocked = inDegree[v] === 0;
          if (unlocked) q.push(v);
          steps.push({
            inDegree: [...inDegree], q: [...q], current: u, taken: taken, n: n,
            line: 9, narration: `Prerequisite Course ${u} satisfied for Course ${v}. In-degree is now ${inDegree[v]}.${unlocked ? ' (Unlocked & enqueued!)' : ''}`
          });
        }
      }

      const ok = (taken === n);
      steps.push({
        inDegree: [...inDegree], q: [], current: "Done", taken: taken, n: n,
        line: 12, narration: ok ? `All ${n} courses completed! Result: true (No cycle).` : `Only ${taken} of ${n} courses completed! Cycle detected, result: false.`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p83-stage');
      if (!stage) return;
      document.getElementById('metric-queue-items').textContent = `[${step.q.join(', ')}]`;
      document.getElementById('metric-completed').textContent = `${step.taken} / ${step.n}`;
      document.getElementById('metric-current').textContent = step.current !== null ? `Course ${step.current}` : "None";
      document.getElementById('metric-can-finish').textContent = step.current === 'Done' ? (step.taken === step.n ? 'TRUE' : 'FALSE') : 'Evaluating';

      let html = '<div class="flex items-center gap-4 flex-wrap justify-center">';
      for (let i = 0; i < step.n; i++) {
        const deg = step.inDegree[i];
        const isCur = (step.current === i);
        const isZero = (deg === 0);
        const bg = isCur ? 'bg-amber-500 text-black border-amber-300 ring-2 ring-amber-400' : (isZero ? 'bg-emerald-800/40 text-emerald-200 border-emerald-500' : 'bg-slate-800 text-slate-300 border-slate-700');
        html += `<div class="flex flex-col items-center p-2 rounded-lg border ${bg} min-w-[70px]">
                   <span class="text-xs font-mono text-slate-400">Course ${i}</span>
                   <span class="text-lg font-bold">${deg}</span>
                   <span class="text-[10px] uppercase font-semibold text-slate-400">In-degree</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(n, prereqs) {
      rawN = n;
      rawPrereqs = prereqs;
      buildSteps(n, prereqs);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawN, rawPrereqs);
"""
    },
    'approaches': [
        {
            'name': "Kahn's Algorithm (BFS with In-Degree)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(V + E) Time, O(V + E) Space",
            'summary': "Count prerequisite in-degrees. Seed queue with in-degree 0 courses. Pop and decrement neighbor in-degrees, pushing to queue when 0. If processed count equals `numCourses`, no cycles exist.",
            'kotlin_code': """class Solution {
    fun canFinish(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
        val inDegree = IntArray(numCourses)
        val adj = Array(numCourses) { mutableListOf<Int>() }

        // prereq: [course, prereq] -> edge: prereq -> course
        for (edge in prerequisites) {
            val course = edge[0]
            val pre = edge[1]
            adj[pre].add(course)
            inDegree[course]++
        }

        val queue = ArrayDeque<Int>()
        for (i in 0 until numCourses) {
            if (inDegree[i] == 0) {
                queue.add(i)
            }
        }

        var completed = 0
        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            completed++

            for (neighbor in adj[curr]) {
                inDegree[neighbor]--
                if (inDegree[neighbor] == 0) {
                    queue.add(neighbor)
                }
            }
        }

        return completed == numCourses
    }
}""",
            'python_code': """from collections import deque

class Solution:
    def canFinish(self, numCourses: int, prerequisites: List[List[int]]) -> bool:
        in_degree = [0] * numCourses
        adj = [[] for _ in range(numCourses)]
        
        for course, pre in prerequisites:
            adj[pre].append(course)
            in_degree[course] += 1
            
        queue = deque([i for i in range(numCourses) if in_degree[i] == 0])
        completed = 0
        
        while queue:
            curr = queue.popleft()
            completed += 1
            
            for neighbor in adj[curr]:
                in_degree[neighbor] -= 1
                if in_degree[neighbor] == 0:
                    queue.append(neighbor)
                    
        return completed == numCourses""",
            'java_code': """class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        int[] inDegree = new int[numCourses];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] p : prerequisites) {
            int course = p[0];
            int pre = p[1];
            adj.get(pre).add(course);
            inDegree[course]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        int completed = 0;
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            completed++;

            for (int neighbor : adj.get(curr)) {
                if (--inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return completed == numCourses;
    }
}""",
            'memory_breakdown': [
                {'component': "Adjacency List `adj`", 'cost': "O(V + E)", 'description': "Stores V vertices and E directed prerequisite dependencies."},
                {'component': "In-degree array", 'cost': "O(V)", 'description': "Single integer count for each course."},
                {'component': "BFS Queue", 'cost': "O(V)", 'description': "Holds up to V courses simultaneously."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun canFinish(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
    // Write your solution here
    return true
}""",
        'python_boilerplate': """def canFinish(numCourses: int, prerequisites: List[List[int]]) -> bool:
    # Write your solution here
    return True""",
        'java_boilerplate': """public boolean canFinish(int numCourses, int[][] prerequisites) {
    // Write your solution here
    return true;
}""",
        'js_test_runner': """
function canFinish(numCourses, prerequisites) {
    const inDegree = new Array(numCourses).fill(0);
    const adj = Array.from({length: numCourses}, () => []);
    for (let [c, p] of prerequisites) {
        adj[p].push(c);
        inDegree[c]++;
    }
    const q = [];
    for (let i = 0; i < numCourses; i++) {
        if (inDegree[i] === 0) q.push(i);
    }
    let completed = 0;
    while (q.length > 0) {
        const u = q.shift();
        completed++;
        for (let v of adj[u]) {
            if (--inDegree[v] === 0) q.push(v);
        }
    }
    return completed === numCourses;
}

const tests = [
    { numCourses: 2, prerequisites: [[1,0]], expected: true },
    { numCourses: 2, prerequisites: [[1,0],[0,1]], expected: false },
    { numCourses: 4, prerequisites: [[1,0],[2,0],[3,1],[3,2]], expected: true },
    { numCourses: 3, prerequisites: [[0,1],[1,2],[2,0]], expected: false }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = canFinish(t.numCourses, t.prerequisites);
    const ok = output === t.expected;
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${output}, Expected: ${t.expected})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's understand the input: we are given `numCourses` and a list of directed prerequisites where `[a, b]` means course `b` is a prerequisite for `a`. We need to determine if all courses can be completed.",
        'phase2': "A student can finish all courses if and only if there are no circular dependencies. In graph theory, this is cycle detection in a directed graph: can we construct a topological ordering?",
        'phase3': "We can use Kahn's Algorithm. First, we compute the in-degree of every course—which represents the number of prerequisites remaining. Courses with in-degree 0 can be taken immediately, so we seed our queue with them.",
        'phase4': "As we dequeue a course, we increment our completed counter and decrement the in-degree of all courses that depended on it. When a course's in-degree reaches 0, all its prerequisites have been fulfilled, so we enqueue it. Finally, if `completed == numCourses`, return true.",
        'senior_followup': "Notice that if prerequisites contains duplicate edges, our in-degree count could be artificially inflated unless we deduplicate edges during adjacency list construction or assume the problem constraint of unique pairs."
    },
    'quiz': [
        {
            'question': "In Kahn's Algorithm, what does an in-degree of 0 signify for a vertex?",
            'options': [
                "The course has 0 unsatisfied prerequisites and is ready to be taken",
                "The course has no subsequent courses depending on it",
                "The vertex is isolated and has no connections",
                "The vertex is part of a strongly connected component"
            ],
            'correct_index': 0,
            'explanation': "An in-degree of 0 means no directed edges point into this vertex, indicating all prerequisites have been satisfied."
        },
        {
            'question': "If the while loop terminates but `completed < numCourses`, what does this prove?",
            'options': [
                "At least one directed cycle exists preventing courses in the cycle from reaching in-degree 0",
                "The input contains self-loops only",
                "The graph has multiple connected components",
                "Memory limits were exceeded"
            ],
            'correct_index': 0,
            'explanation': "Nodes inside a directed cycle have at least one incoming edge from another node in the cycle; their in-degrees can never reach 0, so they are never enqueued."
        }
    ]
}

p84 = {
    'num': 84,
    'lc': 210,
    'title': "Course Schedule II",
    'category': "Graphs",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "course-schedule-ii",
    'verbatim_statement': """<p>There are a total of <code>numCourses</code> courses you have to take, labeled from <code>0</code> to <code>numCourses - 1</code>. You are given an array <code>prerequisites</code> where <code>prerequisites[i] = [a<sub>i</sub>, b<sub>i</sub>]</code> indicates that you <strong>must</strong> take course <code>b<sub>i</sub></code> first if you want to take course <code>a<sub>i</sub></code>.</p>
<p>Return <em>the ordering of courses you should take to finish all courses</em>. If there are many valid answers, return <strong>any</strong> of them. If it is impossible to finish all courses, return <strong>an empty array</strong>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'numCourses = 2, prerequisites = [[1,0]]', 'output': '[0,1]\nExplanation: There are a total of 2 courses to take. To take course 1 you should have finished course 0. So the correct course order is [0,1].'},
        {'title': "Example 2", 'input': 'numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]', 'output': '[0,2,1,3]\nExplanation: There are a total of 4 courses to take. To take course 3 you should have finished both courses 1 and 2. Both courses 1 and 2 should be taken after you finished course 0.\nSo one correct course order is [0,1,2,3]. Another correct ordering is [0,2,1,3].'},
        {'title': "Example 3", 'input': 'numCourses = 1, prerequisites = []', 'output': '[0]'}
    ],
    'constraints': [
        "1 <= numCourses <= 2000",
        "0 <= prerequisites.length <= numCourses * (numCourses - 1)",
        "prerequisites[i].length == 2",
        "0 <= ai, bi < numCourses",
        "ai != bi",
        "All the pairs [ai, bi] are distinct."
    ],
    'plain_english': "This is the direct continuation of Course Schedule (LC 207). Instead of returning a boolean, we must return the actual valid topological order array. We allocate an integer array `order` of size `numCourses`. Whenever a node with in-degree 0 is dequeued in Kahn's algorithm, we write it into `order[index++]`. When the queue empties: if `index == numCourses`, return `order`; if `index < numCourses` (a cycle existed), return `intArrayOf()` (empty array).",
    'boundary_checks': {
        'min_base': "Single course without prerequisites <code>numCourses = 1, prerequisites = []</code> &rarr; Returns <code>[0]</code>.",
        'best_exit': "Cycle detected <code>[[1,0],[0,1]]</code> &rarr; Returns empty array <code>[]</code>.",
        'worst_scan': "Multiple valid topological orders &rarr; Any valid topological sort is accepted by the grader."
    },
    'mental_model': {
        'analogy_title': "The Graduation Transcript",
        'analogy_text': "Just like in Course Schedule I, you pick courses with 0 remaining requirements. But now, the registrar is stamping your official diploma transcript in real time: each time you finish a class, its course code is inked into the next empty slot on your transcript. If you fill all rows on your transcript, you hand it in. If you get stuck before finishing because of an unresolvable prerequisite deadlock, the registrar shreds the transcript and hands you an empty folder.",
        'aha_moment': "The exact sequence in which nodes are popped from the in-degree-0 queue IS a valid topological sort!",
        'trigger': "Return a valid ordering of items subject to dependency constraints &rarr; Kahn's Algorithm recording output array.",
        'sisters': [
            {'name': "Course Schedule (Feasibility boolean)", 'lc': "LC #207"},
            {'name': "Sequence Reconstruction", 'lc': "LC #444"},
            {'name': "Build a Matrix With Conditions", 'lc': "LC #2392"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(V + E) Time, O(V + E) Space",
        'naive_text': "Kahn's algorithm is optimal. A naive approach might repeatedly re-scan the entire adjacency matrix searching for in-degree 0 vertices without an active queue, yielding O(V^2).",
        'wasted_work': "Re-scanning all vertices on every reduction step instead of maintaining an active worklist queue.",
        'optimal_complexity': "O(V + E) Time, O(V + E) Space",
        'optimal_text': "Queue-based Kahn's algorithm writes directly into a pre-allocated fixed-size array.",
        'core_invariant': "Every element `u` placed in `order[i]` precedes all elements `v` that have `u` as a prerequisite.",
        'table_rows': [
            {'name': "★ Kahn's Algorithm (Pre-allocated Array)", 'time': "O(V + E)", 'space': "O(V + E)", 'note': "Zero overhead; writes directly into output array index."},
            {'name': "DFS Post-Order + Reverse", 'time': "O(V + E)", 'space': "O(V + E)", 'note': "Requires reversing post-order traversal and 3-state cycle detection."}
        ]
    },
    'trace_table': {
        'input_desc': 'numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]',
        'columns': ["Step", "Queue", "Popped (u)", "Order Array State", "Updated In-Degrees"],
        'rows': [
            ["Init", "[0]", "-", "[-,-,-,-]", "inDegree: [0, 1, 1, 2]"],
            ["1", "[1, 2]", "0", "[0,-,-,-]", "inDegree[1]=0, inDegree[2]=0"],
            ["2", "[2]", "1", "[0,1,-,-]", "inDegree[3]=1"],
            ["3", "[3]", "2", "[0,1,2,-]", "inDegree[3]=0 &rarr; push 3"],
            ["4", "[]", "3", "[0,1,2,3]", "Finished. Index = 4 == numCourses"]
        ],
        'takeaway': "Returns `[0, 1, 2, 3]` (or `[0, 2, 1, 3]`), satisfying all prerequisites."
    },
    'stepper': {
        'title': "Topological Order Generation Stepper",
        'default_input': 'numCourses=4, prereqs=[[1,0],[2,0],[3,1],[3,2]]',
        'presets': [
            {'label': "Valid 4 Courses", 'onclick': 'loadPreset(4, [[1,0],[2,0],[3,1],[3,2]])'},
            {'label': "Cycle: 2 Courses", 'onclick': 'loadPreset(2, [[1,0],[0,1]])'},
            {'label': "3 Linear Courses", 'onclick': 'loadPreset(3, [[1,0],[2,1]])'}
        ],
        'metrics': [
            {'id': "metric-order-result", 'label': "Ordering Array", 'color': "text-amber-400", 'initial': "[]"},
            {'id': "metric-queue-state", 'label': "Queue (In-Degree 0)", 'color': "text-cyan-400", 'initial': "[]"},
            {'id': "metric-idx", 'label': "Output Index", 'color': "text-emerald-400", 'initial': "0 / 4"},
            {'id': "metric-valid", 'label': "Status", 'color': "text-purple-400", 'initial': "In Progress"}
        ],
        'visual_html': '<div id="p84-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace topological ordering extraction.',
        'pseudocode_lines': [
            "val order = IntArray(numCourses)",
            "var idx = 0",
            "while (queue.isNotEmpty()) {",
            "    val u = queue.removeFirst()",
            "    order[idx++] = u",
            "    for (v in adj[u]) {",
            "        if (--inDegree[v] == 0) queue.add(v)",
            "    }",
            "}",
            "return if (idx == numCourses) order else intArrayOf()"
        ],
        'js_code': """
    let rawN = 4;
    let rawPrereqs = [[1,0],[2,0],[3,1],[3,2]];

    function buildSteps(n, prereqs) {
      steps = [];
      const adj = Array.from({length: n}, () => []);
      const inDegree = new Array(n).fill(0);

      for (let [course, pre] of prereqs) {
        adj[pre].push(course);
        inDegree[course]++;
      }

      const q = [];
      for (let i = 0; i < n; i++) {
        if (inDegree[i] === 0) q.push(i);
      }

      const order = [];
      steps.push({
        order: [], q: [...q], inDegree: [...inDegree], n: n, line: 3,
        narration: `Starting Kahn's algorithm. Queue seeded with courses having 0 prerequisites: [${q.join(', ')}].`
      });

      while (q.length > 0) {
        const u = q.shift();
        order.push(u);
        steps.push({
          order: [...order], q: [...q], inDegree: [...inDegree], n: n, line: 5,
          narration: `Dequeued Course ${u} and appended to ordering array. order = [${order.join(', ')}].`
        });

        for (let v of adj[u]) {
          inDegree[v]--;
          const unlocked = inDegree[v] === 0;
          if (unlocked) q.push(v);
          steps.push({
            order: [...order], q: [...q], inDegree: [...inDegree], n: n, line: 7,
            narration: `Course ${u} satisfied for Course ${v} (inDegree=${inDegree[v]}).${unlocked ? ' Added to queue!' : ''}`
          });
        }
      }

      const ok = (order.length === n);
      steps.push({
        order: ok ? [...order] : [], q: [], inDegree: [...inDegree], n: n, line: 10,
        narration: ok ? `Successfully ordered all ${n} courses: [${order.join(', ')}].` : `Cycle detected! Only ${order.length} courses reachable. Returning empty array [].`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p84-stage');
      if (!stage) return;
      document.getElementById('metric-order-result').textContent = `[${step.order.join(', ')}]`;
      document.getElementById('metric-queue-state').textContent = `[${step.q.join(', ')}]`;
      document.getElementById('metric-idx').textContent = `${step.order.length} / ${step.n}`;
      document.getElementById('metric-valid').textContent = step.order.length === step.n ? 'VALID' : (step.q.length === 0 ? 'CYCLE / EMPTY' : 'IN PROGRESS');

      let html = '<div class="flex items-center gap-3">';
      for (let i = 0; i < step.n; i++) {
        const val = i < step.order.length ? step.order[i] : '?';
        const filled = (i < step.order.length);
        const bg = filled ? 'bg-amber-500 text-black border-amber-300 font-bold' : 'bg-slate-800 text-slate-500 border-slate-700';
        html += `<div class="flex flex-col items-center">
                   <div class="w-12 h-12 rounded-lg flex items-center justify-center border ${bg}">
                     ${val}
                   </div>
                   <span class="text-[10px] text-slate-400 mt-1">idx ${i}</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(n, prereqs) {
      rawN = n;
      rawPrereqs = prereqs;
      buildSteps(n, prereqs);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawN, rawPrereqs);
"""
    },
    'approaches': [
        {
            'name': "Kahn's Topological Sort with Output Array",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(V + E) Time, O(V + E) Space",
            'summary': "Pre-allocate an integer array of size `numCourses`. Append each popped zero-in-degree vertex. If `idx == numCourses`, return `order`; else return empty array.",
            'kotlin_code': """class Solution {
    fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
        val inDegree = IntArray(numCourses)
        val adj = Array(numCourses) { mutableListOf<Int>() }

        for (edge in prerequisites) {
            val course = edge[0]
            val pre = edge[1]
            adj[pre].add(course)
            inDegree[course]++
        }

        val queue = ArrayDeque<Int>()
        for (i in 0 until numCourses) {
            if (inDegree[i] == 0) {
                queue.add(i)
            }
        }

        val order = IntArray(numCourses)
        var idx = 0

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            order[idx++] = curr

            for (neighbor in adj[curr]) {
                inDegree[neighbor]--
                if (inDegree[neighbor] == 0) {
                    queue.add(neighbor)
                }
            }
        }

        return if (idx == numCourses) order else IntArray(0)
    }
}""",
            'python_code': """from collections import deque

class Solution:
    def findOrder(self, numCourses: int, prerequisites: List[List[int]]) -> List[int]:
        in_degree = [0] * numCourses
        adj = [[] for _ in range(numCourses)]
        
        for course, pre in prerequisites:
            adj[pre].append(course)
            in_degree[course] += 1
            
        queue = deque([i for i in range(numCourses) if in_degree[i] == 0])
        order = []
        
        while queue:
            curr = queue.popleft()
            order.append(curr)
            
            for neighbor in adj[curr]:
                in_degree[neighbor] -= 1
                if in_degree[neighbor] == 0:
                    queue.append(neighbor)
                    
        return order if len(order) == numCourses else []""",
            'java_code': """class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        int[] inDegree = new int[numCourses];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] p : prerequisites) {
            int course = p[0];
            int pre = p[1];
            adj.get(pre).add(course);
            inDegree[course]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        int[] order = new int[numCourses];
        int idx = 0;

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            order[idx++] = curr;

            for (int neighbor : adj.get(curr)) {
                if (--inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return idx == numCourses ? order : new int[0];
    }
}""",
            'memory_breakdown': [
                {'component': "Result Array `order`", 'cost': "O(V)", 'description': "Pre-allocated array of size numCourses."},
                {'component': "Adjacency List `adj`", 'cost': "O(V + E)", 'description': "Stores outgoing edges for each node."},
                {'component': "BFS Queue & In-Degree", 'cost': "O(V)", 'description': "Stores nodes ready for execution."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
    // Write your solution here
    return intArrayOf()
}""",
        'python_boilerplate': """def findOrder(numCourses: int, prerequisites: List[List[int]]) -> List[int]:
    # Write your solution here
    return []""",
        'java_boilerplate': """public int[] findOrder(int numCourses, int[][] prerequisites) {
    // Write your solution here
    return new int[0];
}""",
        'js_test_runner': """
function findOrder(numCourses, prerequisites) {
    const inDegree = new Array(numCourses).fill(0);
    const adj = Array.from({length: numCourses}, () => []);
    for (let [c, p] of prerequisites) {
        adj[p].push(c);
        inDegree[c]++;
    }
    const q = [];
    for (let i = 0; i < numCourses; i++) {
        if (inDegree[i] === 0) q.push(i);
    }
    const order = [];
    while (q.length > 0) {
        const u = q.shift();
        order.push(u);
        for (let v of adj[u]) {
            if (--inDegree[v] === 0) q.push(v);
        }
    }
    return order.length === numCourses ? order : [];
}

function isValidTopological(numCourses, prereqs, order) {
    if (order.length !== numCourses) return false;
    const pos = new Map();
    order.forEach((c, idx) => pos.set(c, idx));
    for (let [c, p] of prereqs) {
        if (pos.get(p) >= pos.get(c)) return false;
    }
    return true;
}

const tests = [
    { numCourses: 2, prerequisites: [[1,0]], expectCycle: false },
    { numCourses: 4, prerequisites: [[1,0],[2,0],[3,1],[3,2]], expectCycle: false },
    { numCourses: 2, prerequisites: [[1,0],[0,1]], expectCycle: true }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = findOrder(t.numCourses, t.prerequisites);
    let ok = false;
    if (t.expectCycle) {
        ok = output.length === 0;
    } else {
        ok = isValidTopological(t.numCourses, t.prerequisites, output);
    }
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${JSON.stringify(output)})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "Let's review the problem statement: we have `numCourses` courses and dependency pairs `[a, b]`, meaning `b` must precede `a`. We need to return a valid complete sequence of course completions, or an empty array if impossible.",
        'phase2': "This is the classic Topological Sort problem on a Directed Acyclic Graph. If a valid ordering exists, we return it; if there is a cycle, we must return an empty array.",
        'phase3': "We can use Kahn's Algorithm. We pre-allocate an array `order` of size `numCourses` with a pointer `idx = 0`. We count in-degrees, populate our queue with courses having 0 prerequisites, and then repeatedly dequeue.",
        'phase4': "Each time we dequeue a course `u`, we place it into `order[idx++]`. Then we decrement the in-degree of all courses dependent on `u`. When all is done, if `idx == numCourses`, `order` is a valid sequence. Otherwise, a cycle was encountered and we return `[]`.",
        'senior_followup': "Notice how pre-allocating the primitive array `int[] order` in Kotlin/Java avoids dynamic resizing overhead and boxing compared to building a dynamic list."
    },
    'quiz': [
        {
            'question': "If there are multiple courses with in-degree 0 at the same time in the queue, what does this imply about the graph?",
            'options': [
                "There are multiple valid topological orders for this graph",
                "The graph must contain a cycle",
                "The graph has disconnected vertices that cannot be taken",
                "The topological sort algorithm is non-deterministic and invalid"
            ],
            'correct_index': 0,
            'explanation': "When multiple vertices have in-degree 0 simultaneously, any of them can be processed next without violating dependencies, which means multiple valid topological orderings exist."
        },
        {
            'question': "Why is an empty array returned when `idx < numCourses` at the end of Kahn's algorithm?",
            'options': [
                "Because remaining vertices belong to or depend on a directed cycle and could never be resolved",
                "Because of an off-by-one error in array indexing",
                "Because disconnected nodes cannot be included in an array",
                "Because Java initializes unused array elements to zero"
            ],
            'correct_index': 0,
            'explanation': "If the queue empties before all courses are processed, the remaining courses have in-degrees >= 1 caused by mutually dependent cycles, making completion impossible."
        }
    ]
}

p85 = {
    'num': 85,
    'lc': 684,
    'title': "Redundant Connection",
    'category': "Graphs",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "redundant-connection",
    'verbatim_statement': """<p>In this problem, a tree is an <strong>undirected graph</strong> that is connected and has no cycles.</p>
<p>You are given a graph that started as a tree with <code>n</code> nodes labeled from <code>1</code> to <code>n</code>, with one additional edge added. The added edge has two <strong>different</strong> vertices chosen from <code>1</code> to <code>n</code>, and was not an edge that already existed. The graph is represented as an array <code>edges</code> of length <code>n</code> where <code>edges[i] = [u<sub>i</sub>, v<sub>i</sub>]</code> indicates that there is an edge between nodes <code>u<sub>i</sub></code> and <code>v<sub>i</sub></code> in the graph.</p>
<p>Return <em>an edge that can be removed so that the resulting graph is a tree of </em><code>n</code><em> nodes</em>. If there are multiple answers, return the answer that occurs <strong>last</strong> in the input.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'edges = [[1,2],[1,3],[2,3]]', 'output': '[2,3]'},
        {'title': "Example 2", 'input': 'edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]', 'output': '[1,4]'}
    ],
    'constraints': [
        "n == edges.length",
        "3 <= n <= 1000",
        "edges[i].length == 2",
        "1 <= ui < vi <= edges.length",
        "ui != vi",
        "There are no repeated edges.",
        "The given graph is connected."
    ],
    'plain_english': "A tree with `n` vertices has exactly `n - 1` edges and no cycles. We are given `n` edges, meaning exactly ONE extra edge was added that creates a cycle. We process edges in their given order using a Disjoint Set Union (DSU / Union-Find) structure. For each edge `[u, v]`, we find the representative roots of `u` and `v`. If `find(u) == find(v)`, nodes `u` and `v` are already connected through another path—so adding this edge closes a cycle! Since we iterate in order and want the last edge that creates a cycle, this exact edge is our answer.",
    'boundary_checks': {
        'min_base': "Minimum tree with 3 nodes <code>[[1,2],[1,3],[2,3]]</code> &rarr; Returns <code>[2,3]</code>.",
        'best_exit': "Cycle closed early or late &rarr; Because we process edges in input order, the first edge that connects two already-connected nodes is the redundant edge.",
        'worst_scan': "Linear path with back edge connecting ends <code>[[1,2],[2,3],[3,4],[1,4]]</code> &rarr; Returns <code>[1,4]</code>."
    },
    'mental_model': {
        'analogy_title': "Bridging Islands in an Archipelago",
        'analogy_text': "Imagine building bridges between islands. Initially, every island is an isolated province. When you build a bridge between island A and island B, their two provinces merge. But if someone suggests building a bridge between island 1 and island 4, and you realize island 1 and island 4 are ALREADY part of the exact same unified province via existing bridges, building this bridge is redundant and creates a circular loop. You reject that bridge!",
        'aha_moment': "In Union-Find, two vertices having the SAME root before union means a path already exists between them. Adding this edge forms a cycle!",
        'trigger': "Find an edge that creates a cycle in an undirected graph / dynamically merge components &rarr; Disjoint Set Union (DSU) with Path Compression and Union by Rank.",
        'sisters': [
            {'name': "Number of Connected Components in an Undirected Graph", 'lc': "LC #323"},
            {'name': "Graph Valid Tree", 'lc': "LC #261"},
            {'name': "Accounts Merge", 'lc': "LC #721"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(N) Space",
        'naive_text': "For every edge, run a full DFS from `u` to test if `v` is reachable before adding the edge.",
        'wasted_work': "Re-traversing the entire connected component with DFS on every edge insertion.",
        'optimal_complexity': "O(N * α(N)) ≈ O(N) Time, O(N) Space",
        'optimal_text': "Disjoint Set Union (DSU) with Path Compression and Union by Rank gives near-constant amortized time per operation.",
        'core_invariant': "Before processing edge `[u, v]`, all previous edges form an acyclic forest of tree components.",
        'table_rows': [
            {'name': "DFS Reachability per Edge", 'time': "O(N^2)", 'space': "O(N)", 'note': "Runs DFS up to N times; redundant traversals."},
            {'name': "★ Union-Find (Path Compression + Rank)", 'time': "O(N * α(N))", 'space': "O(N)", 'note': "Inverse Ackermann α(N) < 5 for all practical N; essentially O(N)."}
        ]
    },
    'trace_table': {
        'input_desc': 'edges = [[1,2], [1,3], [2,3]]',
        'columns': ["Edge", "find(u)", "find(v)", "Equal?", "Action Taken", "DSU Parents State"],
        'rows': [
            ["[1,2]", "1", "2", "No", "Union 1 and 2", "parent[2] = 1"],
            ["[1,3]", "1", "3", "No", "Union 1 and 3", "parent[3] = 1"],
            ["[2,3]", "1", "1", "YES!", "CYCLE DETECTED! Redundant edge found.", "Return [2,3]"]
        ],
        'takeaway': "When edge `[2,3]` is inspected, `find(2) == 1` and `find(3) == 1`. They already share root 1, making `[2,3]` the redundant cycle-closing edge."
    },
    'stepper': {
        'title': "Union-Find Cycle Detection Stepper",
        'default_input': 'edges=[[1,2],[2,3],[3,4],[1,4],[1,5]]',
        'presets': [
            {'label': "Example 2: 5 Nodes", 'onclick': 'loadPreset([[1,2],[2,3],[3,4],[1,4],[1,5]])'},
            {'label': "Example 1: 3 Nodes", 'onclick': 'loadPreset([[1,2],[1,3],[2,3]])'}
        ],
        'metrics': [
            {'id': "metric-edge-inspect", 'label': "Inspecting Edge", 'color': "text-amber-400", 'initial': "None"},
            {'id': "metric-roots", 'label': "Roots (u, v)", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-dsu-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"},
            {'id': "metric-redundant", 'label': "Cycle Edge", 'color': "text-emerald-400", 'initial': "None"}
        ],
        'visual_html': '<div id="p85-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace DSU union operations and cycle detection.',
        'pseudocode_lines': [
            "class DSU(n: Int) {",
            "    val parent = IntArray(n + 1) { it }",
            "    fun find(i: Int): Int {",
            "        if (parent[i] != i) parent[i] = find(parent[i])",
            "        return parent[i]",
            "    }",
            "    fun union(i: Int, j: Int): Boolean {",
            "        val rootI = find(i); val rootJ = find(j)",
            "        if (rootI == rootJ) return false // Cycle!",
            "        parent[rootI] = rootJ; return true",
            "    }",
            "}"
        ],
        'js_code': """
    let rawEdges = [[1,2],[2,3],[3,4],[1,4],[1,5]];

    function buildSteps(edges) {
      steps = [];
      const n = edges.length;
      const parent = Array.from({length: n + 1}, (_, i) => i);

      function find(i) {
        if (parent[i] !== i) parent[i] = find(parent[i]);
        return parent[i];
      }

      steps.push({
        edge: null, roots: "-", action: "Initialize DSU", parents: [...parent], redundant: null, line: 2,
        narration: `Initialized DSU for ${n} vertices. Each node is its own root.`
      });

      for (let [u, v] of edges) {
        const rootU = find(u);
        const rootV = find(v);

        if (rootU === rootV) {
          steps.push({
            edge: `[${u}, ${v}]`, roots: `root(${u})=${rootU}, root(${v})=${rootV}`,
            action: "CYCLE DETECTED!", parents: [...parent], redundant: `[${u}, ${v}]`, line: 9,
            narration: `Edge [${u}, ${v}] connects nodes already in the same component (root ${rootU})! This is the redundant connection!`
          });
          return;
        }

        parent[rootU] = rootV;
        steps.push({
          edge: `[${u}, ${v}]`, roots: `root(${u})=${rootU}, root(${v})=${rootV}`,
          action: `Union: parent[${rootU}] = ${rootV}`, parents: [...parent], redundant: null, line: 10,
          narration: `Merged components of ${u} (root ${rootU}) and ${v} (root ${rootV}).`
        });
      }
    }

    function renderStage(step) {
      const stage = document.getElementById('p85-stage');
      if (!stage) return;
      document.getElementById('metric-edge-inspect').textContent = step.edge || "None";
      document.getElementById('metric-roots').textContent = step.roots;
      document.getElementById('metric-dsu-action').textContent = step.action;
      document.getElementById('metric-redundant').textContent = step.redundant || "None";

      let html = '<div class="flex items-center gap-3 flex-wrap justify-center">';
      for (let i = 1; i < step.parents.length; i++) {
        const p = step.parents[i];
        const isSelf = (p === i);
        const bg = isSelf ? 'bg-indigo-900/50 text-indigo-200 border-indigo-500' : 'bg-slate-800 text-slate-300 border-slate-700';
        html += `<div class="flex flex-col items-center p-2 rounded-lg border ${bg} min-w-[64px]">
                   <span class="text-xs font-mono text-slate-400">Node ${i}</span>
                   <span class="text-base font-bold text-amber-400">&rarr; ${p}</span>
                   <span class="text-[9px] uppercase text-slate-500">${isSelf ? 'ROOT' : 'PARENT'}</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(edges) {
      rawEdges = edges;
      buildSteps(edges);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawEdges);
"""
    },
    'approaches': [
        {
            'name': "Disjoint Set Union (DSU with Path Compression)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N * α(N)) Time, O(N) Space",
            'summary': "Maintain a parent array of size `N + 1`. For each edge `[u, v]`, find their root representatives. If `find(u) == find(v)`, this edge forms a cycle and is the redundant connection. Otherwise union them.",
            'kotlin_code': """class Solution {
    fun findRedundantConnection(edges: Array<IntArray>): IntArray {
        val parent = IntArray(edges.size + 1) { it }

        fun find(i: Int): Int {
            if (parent[i] != i) {
                parent[i] = find(parent[i]) // Path compression
            }
            return parent[i]
        }

        fun union(i: Int, j: Int): Boolean {
            val rootI = find(i)
            val rootJ = find(j)
            if (rootI == rootJ) return false // Already in same component -> cycle!
            parent[rootI] = rootJ
            return true
        }

        for (edge in edges) {
            if (!union(edge[0], edge[1])) {
                return edge
            }
        }
        return IntArray(0)
    }
}""",
            'python_code': """class Solution:
    def findRedundantConnection(self, edges: List[List[int]]) -> List[int]:
        parent = list(range(len(edges) + 1))
        
        def find(i: int) -> int:
            if parent[i] != i:
                parent[i] = find(parent[i]) # Path compression
            return parent[i]
        
        def union(i: int, j: int) -> bool:
            root_i = find(i)
            root_j = find(j)
            if root_i == root_j:
                return False
            parent[root_i] = root_j
            return True
            
        for u, v in edges:
            if not union(u, v):
                return [u, v]
                
        return []""",
            'java_code': """class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        int[] parent = new int[edges.length + 1];
        for (int i = 0; i <= edges.length; i++) parent[i] = i;

        for (int[] edge : edges) {
            int rootU = find(parent, edge[0]);
            int rootV = find(parent, edge[1]);

            if (rootU == rootV) {
                return edge; // Cycle detected
            }
            parent[rootU] = rootV;
        }
        return new int[0];
    }

    private int find(int[] parent, int i) {
        if (parent[i] != i) {
            parent[i] = find(parent, parent[i]); // Path compression
        }
        return parent[i];
    }
}""",
            'memory_breakdown': [
                {'component': "Parent Array `parent`", 'cost': "O(N)", 'description': "Single integer array of size N + 1 tracking component representatives."},
                {'component': "Recursion Stack", 'cost': "O(α(N))", 'description': "Call stack depth bounded by inverse Ackermann function."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun findRedundantConnection(edges: Array<IntArray>): IntArray {
    // Write your solution here
    return intArrayOf()
}""",
        'python_boilerplate': """def findRedundantConnection(edges: List[List[int]]) -> List[int]:
    # Write your solution here
    return []""",
        'java_boilerplate': """public int[] findRedundantConnection(int[][] edges) {
    // Write your solution here
    return new int[0];
}""",
        'js_test_runner': """
function findRedundantConnection(edges) {
    const parent = Array.from({length: edges.length + 1}, (_, i) => i);
    function find(i) {
        if (parent[i] !== i) parent[i] = find(parent[i]);
        return parent[i];
    }
    for (let [u, v] of edges) {
        const rootU = find(u);
        const rootV = find(v);
        if (rootU === rootV) return [u, v];
        parent[rootU] = rootV;
    }
    return [];
}

const tests = [
    { edges: [[1,2],[1,3],[2,3]], expected: [2,3] },
    { edges: [[1,2],[2,3],[3,4],[1,4],[1,5]], expected: [1,4] }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = findRedundantConnection(t.edges);
    const ok = JSON.stringify(output) === JSON.stringify(t.expected);
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${JSON.stringify(output)}, Expected: ${JSON.stringify(t.expected)})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's clarify the scenario: we start with a valid tree of `n` vertices labeled 1 to `n`, meaning it originally had `n - 1` edges. Exactly one additional edge was added, which introduces a cycle. We are asked to return the edge that creates this cycle, favoring the one that appears latest in the input.",
        'phase2': "Because the edges are supplied sequentially, we can build the tree incrementally. The very edge that connects two vertices that are already part of the same connected component is the cycle-closing edge.",
        'phase3': "The optimal data structure for dynamic connectivity and component merging is Disjoint Set Union (DSU / Union-Find). We initialize each node to be its own parent. For each edge `[u, v]`, we call `find(u)` and `find(v)`.",
        'phase4': "If `find(u) == find(v)`, a path between them already existed, so this edge creates a cycle and is redundant. Otherwise, we union the sets by pointing one root to the other. With path compression, the runtime is practically linear, $O(N \\cdot \\alpha(N))$.",
        'senior_followup': "If we also maintained a `rank` array to attach the shorter tree under the taller tree, we guarantee an absolute worst-case tree height of $O(\\log N)$ even without path compression, but path compression alone is already asymptotically optimal."
    },
    'quiz': [
        {
            'question': "What condition in Disjoint Set Union signifies that an edge `[u, v]` creates a cycle?",
            'options': [
                "`find(u) == find(v)` before merging",
                "`parent[u] == v`",
                "`rank[u] > rank[v]`",
                "`u > v`"
            ],
            'correct_index': 0,
            'explanation': "If both vertices already share the same canonical representative root, there is already an active path connecting them in the forest. Adding another edge between them creates a closed cycle."
        },
        {
            'question': "What is the time complexity of processing N edges with Union-Find using Path Compression?",
            'options': [
                "O(N * α(N)) where α is the inverse Ackermann function (effectively O(N))",
                "O(N log N) strictly",
                "O(N^2) worst case",
                "O(N * E)"
            ],
            'correct_index': 0,
            'explanation': "Path compression with or without rank union runs in O(N * α(N)) time, where α(N) is less than 5 for any conceivable input size."
        }
    ]
}

p86 = {
    'num': 86,
    'lc': 743,
    'title': "Network Delay Time",
    'category': "Graphs",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "network-delay-time",
    'verbatim_statement': """<p>You are given a network of <code>n</code> nodes, labeled from <code>1</code> to <code>n</code>. You are also given <code>times</code>, a list of travel times as directed edges <code>times[i] = (u<sub>i</sub>, v<sub>i</sub>, w<sub>i</sub>)</code>, where <code>u<sub>i</sub></code> is the source node, <code>v<sub>i</sub></code> is the target node, and <code>w<sub>i</sub></code> is the time it takes for a signal to travel from source to target.</p>
<p>We will send a signal from a given node <code>k</code>. Return <em>the <strong>minimum</strong> time it takes for all the </em><code>n</code><em> nodes to receive the signal</em>. If it is impossible for all the <code>n</code> nodes to receive the signal, return <code>-1</code>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2', 'output': '2\nExplanation: The signal is sent from node 2. At time 1, nodes 1 and 3 receive the signal. At time 2, node 4 receives the signal. All nodes have received the signal, so the answer is 2.'},
        {'title': "Example 2", 'input': 'times = [[1,2,1]], n = 2, k = 1', 'output': '1'},
        {'title': "Example 3", 'input': 'times = [[1,2,1]], n = 2, k = 2', 'output': '-1\nExplanation: Node 1 cannot be reached from node 2.'}
    ],
    'constraints': [
        "1 <= k <= n <= 100",
        "1 <= times.length <= 6000",
        "times[i].length == 3",
        "1 <= ui, vi <= n",
        "ui != vi",
        "0 <= wi <= 100",
        "All the pairs (ui, vi) are unique. (i.e., no multiple edges.)"
    ],
    'plain_english': "A signal radiates outward from node `k`. Because travel times `w` are non-negative, the earliest time node `v` receives the signal is the shortest path distance from `k` to `v`. The time for ALL reachable nodes to receive the signal is the maximum of these shortest paths: `max(dist[1..n])`. If any node remains unreachable (`dist[i] == ∞`), return -1. Since all edge weights are non-negative, this is a textbook Dijkstra's Single-Source Shortest Path algorithm using a Min-Heap (PriorityQueue).",
    'boundary_checks': {
        'min_base': "Disconnected target <code>times = [[1,2,1]], n = 2, k = 2</code> &rarr; Node 1 is unreachable, returns -1.",
        'best_exit': "Single node network <code>n = 1, k = 1, times = []</code> &rarr; Signal already at destination; returns 0.",
        'worst_scan': "Linear daisy chain of weights <code>1-2 (1), 2-3 (2), 3-4 (3)</code> &rarr; Returns sum of weights 6."
    },
    'mental_model': {
        'analogy_title': "Ripples in a Pond",
        'analogy_text': "Drop a pebble at node `k`. A circular wave ripples outward in water. Nodes closer in distance are touched by the wave front first. A priority queue tracks the expanding perimeter of the wavefront, always expanding the node that is touched soonest in chronological time. Once the wave has touched every island, the timestamp of the last island touched is your answer.",
        'aha_moment': "The problem asks for the minimum time for *all* nodes to receive the signal. That equals the *maximum* shortest path distance among all nodes from `k`! If any shortest path is $\\infty$, it's unreachable.",
        'trigger': "Shortest path with non-negative edge weights from a single source &rarr; Dijkstra's Algorithm with Min-Heap `(dist, node)`.",
        'sisters': [
            {'name': "Cheapest Flights Within K Stops", 'lc': "LC #787"},
            {'name': "Path with Minimum Effort", 'lc': "LC #1631"},
            {'name': "Swim in Rising Water", 'lc': "LC #778"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(V * E) Time, O(V) Space",
        'naive_text': "Bellman-Ford algorithm relaxing all edges V - 1 times.",
        'wasted_work': "Repeatedly scanning every edge regardless of whether its source distance has stabilized.",
        'optimal_complexity': "O(E log V) Time, O(V + E) Space",
        'optimal_text': "Dijkstra's Algorithm using a binary Min-Heap priority queue.",
        'core_invariant': "When a node `u` is extracted from the min-heap, its recorded distance `dist[u]` is strictly optimal and will never be improved.",
        'table_rows': [
            {'name': "Bellman-Ford", 'time': "O(V * E)", 'space': "O(V)", 'note': "General for negative weights, but slower when weights are non-negative."},
            {'name': "★ Dijkstra (Min-Heap)", 'time': "O(E log V)", 'space': "O(V + E)", 'note': "Greedy wavefront expansion; visits nodes in monotonically increasing distance."}
        ]
    },
    'trace_table': {
        'input_desc': 'times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2',
        'columns': ["Step", "PQ State `(dist, node)`", "Popped `(d, u)`", "Relaxed Neighbors", "Dist Array `[1,2,3,4]`"],
        'rows': [
            ["Init", "[(0, 2)]", "-", "-", "[∞, 0, ∞, ∞]"],
            ["1", "[(1, 1), (1, 3)]", "(0, 2)", "u=2 &rarr; v=1 (w=1), v=3 (w=1)", "[1, 0, 1, ∞]"],
            ["2", "[(1, 3)]", "(1, 1)", "u=1 has no outgoing edges", "[1, 0, 1, ∞]"],
            ["3", "[(2, 4)]", "(1, 3)", "u=3 &rarr; v=4 (w=1, dist=2)", "[1, 0, 1, 2]"],
            ["4", "[]", "(2, 4)", "u=4 has no outgoing edges", "[1, 0, 1, 2]"]
        ],
        'takeaway': "Final dist array = `[1, 0, 1, 2]`. Max distance is `max(1, 0, 1, 2) = 2`. All nodes reached, returns 2."
    },
    'stepper': {
        'title': "Dijkstra's Shortest Path Stepper",
        'default_input': 'times=[[2,1,1],[2,3,1],[3,4,1]], n=4, k=2',
        'presets': [
            {'label': "Example 1: 4 Nodes from k=2", 'onclick': 'loadPreset([[2,1,1],[2,3,1],[3,4,1]], 4, 2)'},
            {'label': "Disconnected: k=2", 'onclick': 'loadPreset([[1,2,1]], 2, 2)'}
        ],
        'metrics': [
            {'id': "metric-pq-head", 'label': "Current Node", 'color': "text-amber-400", 'initial': "None"},
            {'id': "metric-min-time", 'label': "Current Time", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-reached", 'label': "Nodes Reached", 'color': "text-emerald-400", 'initial': "1 / 4"},
            {'id': "metric-max-delay", 'label': "Max Delay So Far", 'color': "text-purple-400", 'initial': "0"}
        ],
        'visual_html': '<div id="p86-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace Dijkstra\'s wavefront propagation.',
        'pseudocode_lines': [
            "val dist = IntArray(n + 1) { Int.MAX_VALUE }",
            "dist[k] = 0",
            "val pq = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })",
            "pq.add(Pair(0, k))",
            "while (pq.isNotEmpty()) {",
            "    val (d, u) = pq.poll()",
            "    if (d > dist[u]) continue",
            "    for ((v, w) in adj[u]) {",
            "        if (dist[u] + w < dist[v]) {",
            "            dist[v] = dist[u] + w; pq.add(Pair(dist[v], v))",
            "        }",
            "    }",
            "}"
        ],
        'js_code': """
    let rawTimes = [[2,1,1],[2,3,1],[3,4,1]];
    let rawN = 4;
    let rawK = 2;

    function buildSteps(times, n, k) {
      steps = [];
      const adj = Array.from({length: n + 1}, () => []);
      for (let [u, v, w] of times) {
        adj[u].push({to: v, weight: w});
      }

      const dist = new Array(n + 1).fill(Infinity);
      dist[k] = 0;
      const pq = [{dist: 0, node: k}];

      steps.push({
        node: k, time: 0, dist: [...dist], pqSize: 1, n: n, line: 4,
        narration: `Starting Dijkstra from source node ${k} at time 0.`
      });

      while (pq.length > 0) {
        pq.sort((a, b) => a.dist - b.dist);
        const {dist: d, node: u} = pq.shift();

        if (d > dist[u]) continue;

        steps.push({
          node: u, time: d, dist: [...dist], pqSize: pq.length, n: n, line: 6,
          narration: `Finalized shortest path to Node ${u} with delay ${d}.`
        });

        for (let edge of adj[u]) {
          const v = edge.to;
          const w = edge.weight;
          if (dist[u] + w < dist[v]) {
            dist[v] = dist[u] + w;
            pq.push({dist: dist[v], node: v});
            steps.push({
              node: u, time: dist[u], dist: [...dist], pqSize: pq.length, n: n, line: 10,
              narration: `Relaxed edge ${u} &rarr; ${v} (w=${w}). New delay for Node ${v} is ${dist[v]}.`
            });
          }
        }
      }

      let maxDist = 0;
      for (let i = 1; i <= n; i++) {
        if (dist[i] === Infinity) { maxDist = -1; break; }
        maxDist = Math.max(maxDist, dist[i]);
      }

      steps.push({
        node: "Done", time: maxDist, dist: [...dist], pqSize: 0, n: n, line: 12,
        narration: maxDist === -1 ? `Not all nodes reached from ${k}! Returning -1.` : `All nodes reached! Total network delay = ${maxDist}.`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p86-stage');
      if (!stage) return;
      document.getElementById('metric-pq-head').textContent = step.node !== 'Done' ? `Node ${step.node}` : "Complete";
      document.getElementById('metric-min-time').textContent = step.time;

      let reached = 0;
      let maxD = 0;
      for (let i = 1; i <= step.n; i++) {
        if (step.dist[i] !== Infinity) {
          reached++;
          maxD = Math.max(maxD, step.dist[i]);
        }
      }
      document.getElementById('metric-reached').textContent = `${reached} / ${step.n}`;
      document.getElementById('metric-max-delay').textContent = step.node === 'Done' ? step.time : maxD;

      let html = '<div class="flex items-center gap-3 flex-wrap justify-center">';
      for (let i = 1; i <= step.n; i++) {
        const d = step.dist[i];
        const isReached = (d !== Infinity);
        const isCur = (step.node === i);
        const bg = isCur ? 'bg-amber-500 text-black border-amber-300 ring-2 ring-amber-400 font-bold' : (isReached ? 'bg-emerald-900/40 text-emerald-300 border-emerald-600' : 'bg-slate-800 text-slate-500 border-slate-700');
        html += `<div class="flex flex-col items-center p-2 rounded-lg border ${bg} min-w-[70px]">
                   <span class="text-xs font-mono text-slate-400">Node ${i}</span>
                   <span class="text-lg font-bold">${isReached ? d : '&infin;'}</span>
                   <span class="text-[9px] uppercase text-slate-500">Delay</span>
                 </div>`;
      }
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(times, n, k) {
      rawTimes = times;
      rawN = n;
      rawK = k;
      buildSteps(times, n, k);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawTimes, rawN, rawK);
"""
    },
    'approaches': [
        {
            'name': "Dijkstra's Algorithm with Min-Heap",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(E log V) Time, O(V + E) Space",
            'summary': "Adjacency list with Min-Heap priority queue holding pairs `(dist, node)`. Relax edges when `dist[u] + w < dist[v]`. The network delay is `max(dist[1..n])` or `-1` if any node is unreachable.",
            'kotlin_code': """import java.util.PriorityQueue

class Solution {
    fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {
        val adj = Array(n + 1) { mutableListOf<Pair<Int, Int>>() }
        for (edge in times) {
            val u = edge[0]
            val v = edge[1]
            val w = edge[2]
            adj[u].add(Pair(v, w))
        }

        val dist = IntArray(n + 1) { Int.MAX_VALUE }
        dist[k] = 0

        // Min-Heap ordered by travel time from source
        val pq = PriorityQueue<Pair<Int, Int>>(compareBy { it.first })
        pq.add(Pair(0, k)) // (current_dist, node)

        while (pq.isNotEmpty()) {
            val (d, u) = pq.poll()

            // Skip outdated pairs in heap
            if (d > dist[u]) continue

            for ((v, weight) in adj[u]) {
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight
                    pq.add(Pair(dist[v], v))
                }
            }
        }

        var maxTime = 0
        for (i in 1..n) {
            if (dist[i] == Int.MAX_VALUE) return -1
            maxTime = maxOf(maxTime, dist[i])
        }
        return maxTime
    }
}""",
            'python_code': """import heapq

class Solution:
    def networkDelayTime(self, times: List[List[int]], n: int, k: int) -> int:
        adj = [[] for _ in range(n + 1)]
        for u, v, w in times:
            adj[u].append((v, w))
            
        dist = [float('inf')] * (n + 1)
        dist[k] = 0
        
        # Priority queue stores (time, node)
        pq = [(0, k)]
        
        while pq:
            d, u = heapq.heappop(pq)
            
            if d > dist[u]:
                continue
                
            for v, weight in adj[u]:
                if dist[u] + weight < dist[v]:
                    dist[v] = dist[u] + weight
                    heapq.heappush(pq, (dist[v], v))
                    
        max_time = max(dist[1:])
        return max_time if max_time != float('inf') else -1""",
            'java_code': """class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] edge : times) {
            adj.get(edge[0]).add(new int[]{edge[1], edge[2]});
        }

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        // Min-heap ordered by distance
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, k});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0];
            int u = curr[1];

            if (d > dist[u]) continue;

            for (int[] edge : adj.get(u)) {
                int v = edge[0];
                int weight = edge[1];
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }

        int maxTime = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxTime = Math.max(maxTime, dist[i]);
        }
        return maxTime;
    }
}""",
            'memory_breakdown': [
                {'component': "Adjacency List `adj`", 'cost': "O(V + E)", 'description': "Stores nodes and weighted directed edges."},
                {'component': "Distance Array `dist`", 'cost': "O(V)", 'description': "Shortest distance from k to each node."},
                {'component': "PriorityQueue `pq`", 'cost': "O(E)", 'description': "Can hold up to E pending edge relaxation pairs."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {
    // Write your solution here
    return -1
}""",
        'python_boilerplate': """def networkDelayTime(times: List[List[int]], n: int, k: int) -> int:
    # Write your solution here
    return -1""",
        'java_boilerplate': """public int networkDelayTime(int[][] times, int n, int k) {
    // Write your solution here
    return -1;
}""",
        'js_test_runner': """
function networkDelayTime(times, n, k) {
    const adj = Array.from({length: n + 1}, () => []);
    for (let [u, v, w] of times) adj[u].push([v, w]);
    const dist = new Array(n + 1).fill(Infinity);
    dist[k] = 0;
    const pq = [[0, k]];
    while (pq.length > 0) {
        pq.sort((a,b) => a[0] - b[0]);
        const [d, u] = pq.shift();
        if (d > dist[u]) continue;
        for (let [v, w] of adj[u]) {
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                pq.push([dist[v], v]);
            }
        }
    }
    let max = 0;
    for (let i = 1; i <= n; i++) {
        if (dist[i] === Infinity) return -1;
        max = Math.max(max, dist[i]);
    }
    return max;
}

const tests = [
    { times: [[2,1,1],[2,3,1],[3,4,1]], n: 4, k: 2, expected: 2 },
    { times: [[1,2,1]], n: 2, k: 1, expected: 1 },
    { times: [[1,2,1]], n: 2, k: 2, expected: -1 }
];

let passed = 0;
tests.forEach((t, i) => {
    const output = networkDelayTime(t.times, t.n, t.k);
    const ok = output === t.expected;
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${output}, Expected: ${t.expected})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "Let's state the problem: we have a directed graph of `n` nodes with non-negative transmission delays, and we transmit a signal from node `k`. We want to know the total time until every node has received the signal, or -1 if any node cannot be reached.",
        'phase2': "Because travel times are non-negative, the earliest moment node `v` gets the signal is the shortest path distance from `k` to `v`. The time when *all* nodes have received the signal is the maximum shortest path distance across all nodes.",
        'phase3': "Single-source shortest path with non-negative weights is solved optimally using Dijkstra's Algorithm. We use a Min-Heap PriorityQueue holding `(distance, node)` pairs, starting with `(0, k)`.",
        'phase4': "We pop the minimum distance node, relax each outgoing edge, and push new distances to the heap. If `d > dist[u]`, we skip it as stale. After the heap is exhausted, we take `max(dist[1..n])`. If any node remains at $\\infty$, return -1.",
        'senior_followup': "If negative edge weights were possible, Dijkstra would fail and we would need Bellman-Ford or SPFA. But because network signal propagation delays are inherently $\\ge 0$, Dijkstra is both optimal and safe."
    },
    'quiz': [
        {
            'question': "Why is Dijkstra's algorithm applicable to Network Delay Time instead of Bellman-Ford?",
            'options': [
                "Because all transmission weights w_i are guaranteed to be non-negative (0 <= w_i <= 100)",
                "Because the graph is an undirected tree",
                "Because Dijkstra's algorithm uses less code",
                "Because topological sorting is required"
            ],
            'correct_index': 0,
            'explanation': "Dijkstra's greedy shortest path algorithm is mathematically guaranteed to be correct when all edge weights are non-negative."
        },
        {
            'question': "What is the condition for returning -1 in Network Delay Time?",
            'options': [
                "If any node i from 1 to n still has dist[i] == Infinity after the priority queue is empty",
                "If the sum of all edge weights exceeds Integer.MAX_VALUE",
                "If there is any cycle in the graph",
                "If k equals n"
            ],
            'correct_index': 0,
            'explanation': "If any node's distance remains infinite, there is no directed path from source k to that node, making it impossible for the signal to reach all nodes."
        }
    ]
}
