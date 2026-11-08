import sys
sys.path.append('scripts')

p94 = {
    'num': 94,
    'lc': 23,
    'title': "Merge k Sorted Lists",
    'category': "Linked Lists & System Node Design",
    'difficulty': "Hard",
    'pass_label': "Pass 3",
    'lc_slug': "merge-k-sorted-lists",
    'verbatim_statement': """<p>You are given an array of <code>k</code> linked-lists <code>lists</code>, each linked-list is sorted in ascending order.</p>
<p><em>Merge all the linked-lists into one sorted linked-list and return it.</em></p>""",
    'examples': [
        {'title': "Example 1", 'input': 'lists = [[1,4,5],[1,3,4],[2,6]]', 'output': '[1,1,2,3,4,4,5,6]\nExplanation: The linked-lists are:\n[\n  1->4->5,\n  1->3->4,\n  2->6\n]\nmerging them into one sorted list:\n1->1->2->3->4->4->5->6'},
        {'title': "Example 2", 'input': 'lists = []', 'output': '[]'},
        {'title': "Example 3", 'input': 'lists = [[]]', 'output': '[]'}
    ],
    'constraints': [
        "k == lists.length",
        "0 <= k <= 10^4",
        "0 <= lists[i].length <= 500",
        "-10^4 <= lists[i][j] <= 10^4",
        "lists[i] is sorted in ascending order.",
        "The sum of lists[i].length will not exceed 10^4."
    ],
    'plain_english': "We have `k` sorted linked lists and want to merge them into a single sorted list. There are two primary optimal strategies: (1) Min-Heap: We maintain a PriorityQueue of size at most `k` holding the current head node of each non-empty list. We repeatedly poll the smallest node, attach it to our merged result, and if that node has a `next`, offer `node.next` into the heap. Total time: O(N log k), space: O(k). (2) Divide and Conquer: Pairwise merge lists 2-by-2 in rounds (e.g. 0 with 1, 2 with 3) like Merge Sort. After log2(k) rounds, one combined list remains. Total time: O(N log k), space: O(1) auxiliary space.",
    'boundary_checks': {
        'min_base': "Empty list of lists <code>lists = []</code> &rarr; Returns null immediately.",
        'best_exit': "List of empty heads <code>lists = [[], []]</code> &rarr; Returns null.",
        'worst_scan': "Single non-empty list <code>lists = [[1, 2, 3]]</code> &rarr; Returns that list directly."
    },
    'mental_model': {
        'analogy_title': "The Highway Tollbooth Merge",
        'analogy_text': "Imagine $k$ parallel lanes of traffic merging into a single highway lane. In each lane, cars are already sorted from smallest to largest license plate number. Rather than comparing all $k$ cars across every lane on every move, a single traffic director watches only the very front car of each lane using a mini leaderboard (the Min-Heap). The car with the lowest plate number is waved through to the highway, and the next car in that lane rolls forward to take its place on the leaderboard.",
        'aha_moment': "At any given moment, the global minimum among all remaining unmerged nodes MUST be sitting at the head of one of the $k$ lists. We only ever need to compare $k$ candidates at a time!",
        'trigger': "Merge K sorted sequences / external sorting &rarr; Min-Heap of size K, or Pairwise Divide and Conquer.",
        'sisters': [
            {'name': "Merge Two Sorted Lists", 'lc': "LC #21"},
            {'name': "Sort List", 'lc': "LC #148"},
            {'name': "Find K Pairs with Smallest Sums", 'lc': "LC #373"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N * K) Time, O(1) Space",
        'naive_text': "Repeatedly merge lists one by one: merge list 0 with list 1, then the result with list 2, then list 3, etc.",
        'wasted_work': "Re-traversing already merged elements K times, leading to quadratic time on the number of lists.",
        'optimal_complexity': "O(N log K) Time, O(K) Space",
        'optimal_text': "Min-Heap priority queue holding at most K active node heads.",
        'core_invariant': "The priority queue always holds the active candidate head of every non-exhausted linked list, guaranteeing `pq.peek()` is the global minimum.",
        'table_rows': [
            {'name': "1. Sequential 1-by-1 Merge", 'time': "O(N * K)", 'space': "O(1)", 'note': "N = 10,000, K = 1,000 &rarr; 10^7 operations (too slow)."},
            {'name': "★ 2. Min-Heap PriorityQueue", 'time': "O(N log K)", 'space': "O(K)", 'note': "Optimal runtime; heap size strictly bounded by K."},
            {'name': "★ 3. Divide & Conquer Pairwise", 'time': "O(N log K)", 'space': "O(1)", 'note': "In-place pointer rewiring; zero heap memory overhead."}
        ]
    },
    'trace_table': {
        'input_desc': 'lists = [L1: 1->4->5, L2: 1->3->4, L3: 2->6]',
        'columns': ["Step", "PQ State `(val, list)`", "Node Polled", "Next Node Pushed", "Merged List State"],
        'rows': [
            ["Init", "[(1, L1), (1, L2), (2, L3)]", "-", "-", "dummy -> null"],
            ["1", "[(1, L2), (2, L3), (4, L1)]", "1 (from L1)", "4 (from L1)", "dummy -> 1"],
            ["2", "[(2, L3), (3, L2), (4, L1)]", "1 (from L2)", "3 (from L2)", "dummy -> 1 -> 1"],
            ["3", "[(3, L2), (4, L1), (6, L3)]", "2 (from L3)", "6 (from L3)", "dummy -> 1 -> 1 -> 2"],
            ["4", "[(4, L1), (4, L2), (6, L3)]", "3 (from L2)", "4 (from L2)", "dummy -> 1 -> 1 -> 2 -> 3"],
            ["...", "...", "...", "...", "dummy -> 1->1->2->3->4->4->5->6"]
        ],
        'takeaway': "Min-Heap processes each of the $N$ nodes in $O(\\log K)$ time, yielding total time $O(N \\log K)$."
    },
    'stepper': {
        'title': "Min-Heap Multi-List Merge Stepper",
        'default_input': 'lists=[[1,4,5],[1,3,4],[2,6]]',
        'presets': [
            {'label': "Example 1: 3 Lists", 'onclick': 'loadPreset([[1,4,5],[1,3,4],[2,6]])'},
            {'label': "Two Lists: [1,2], [3,4]", 'onclick': 'loadPreset([[1,2],[3,4]])'},
            {'label': "Empty List: []", 'onclick': 'loadPreset([])'}
        ],
        'metrics': [
            {'id': "metric-pq-top", 'label': "PQ Minimum", 'color': "text-amber-400", 'initial': "None"},
            {'id': "metric-pq-size", 'label': "PQ Size", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-merged-tail", 'label': "Merged Tail", 'color': "text-emerald-400", 'initial': "dummy"},
            {'id': "metric-action-kmerge", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="p94-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace Min-Heap extraction and pointer rewiring.',
        'pseudocode_lines': [
            "val pq = PriorityQueue<ListNode>(compareBy { it.`val` })",
            "for (head in lists) if (head != null) pq.add(head)",
            "val dummy = ListNode(0); var tail = dummy",
            "while (pq.isNotEmpty()) {",
            "    val minNode = pq.poll()",
            "    tail.next = minNode; tail = tail.next",
            "    if (minNode.next != null) pq.add(minNode.next)",
            "}",
            "return dummy.next"
        ],
        'js_code': """
    let rawLists = [[1,4,5],[1,3,4],[2,6]];

    function buildSteps(listsData) {
      steps = [];
      if (!listsData || listsData.length === 0) {
        steps.push({ pq: [], merged: [], top: "None", tail: "null", action: "Empty Input", line: 2, narration: "Input is empty. Return null." });
        return;
      }

      // Clone lists
      const heads = listsData.map((arr, idx) => ({
        listId: idx + 1,
        items: [...arr]
      })).filter(h => h.items.length > 0);

      const pq = [];
      for (let h of heads) {
        pq.push({ val: h.items[0], listId: h.listId, remaining: h.items.slice(1) });
      }

      steps.push({
        pq: pq.map(x => `${x.val} (L${x.listId})`),
        merged: [],
        top: pq.length > 0 ? `${pq[0].val}` : "None",
        tail: "dummy",
        action: "Initialize PQ",
        line: 2,
        narration: `Initialized Priority Queue with ${pq.length} active list heads.`
      });

      const merged = [];
      while (pq.length > 0) {
        pq.sort((a, b) => a.val - b.val);
        const minItem = pq.shift();
        merged.push(minItem.val);

        let pushedNext = null;
        if (minItem.remaining.length > 0) {
          const nextVal = minItem.remaining[0];
          pushedNext = `${nextVal} (L${minItem.listId})`;
          pq.push({
            val: nextVal,
            listId: minItem.listId,
            remaining: minItem.remaining.slice(1)
          });
        }

        steps.push({
          pq: pq.map(x => `${x.val} (L${x.listId})`),
          merged: [...merged],
          top: `${minItem.val}`,
          tail: `${minItem.val}`,
          action: `Polled ${minItem.val} from L${minItem.listId}${pushedNext ? '; Pushed ' + pushedNext : ''}`,
          line: 5,
          narration: `Polled minimum node ${minItem.val} from List ${minItem.listId}. Appended to merged list.${pushedNext ? ' Next node ' + pushedNext + ' added to heap.' : ' List ' + minItem.listId + ' exhausted.'}`
        });
      }

      steps.push({
        pq: [],
        merged: [...merged],
        top: "Done",
        tail: `${merged[merged.length - 1]}`,
        action: "Completed",
        line: 9,
        narration: `All lists merged successfully into: [${merged.join(' -> ')}].`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p94-stage');
      if (!stage) return;
      document.getElementById('metric-pq-top').textContent = step.top;
      document.getElementById('metric-pq-size').textContent = step.pq.length;
      document.getElementById('metric-merged-tail').textContent = step.tail;
      document.getElementById('metric-action-kmerge').textContent = step.action;

      let html = '<div class="flex flex-col items-center gap-4 w-full">';
      
      // PQ visual
      html += '<div class="flex items-center gap-2">';
      html += '<span class="text-xs font-mono text-cyan-400 font-bold mr-2">Min-Heap:</span>';
      if (step.pq.length === 0) {
        html += '<span class="text-xs text-slate-500 italic">Empty</span>';
      } else {
        for (let item of step.pq) {
          html += `<div class="px-2.5 py-1 rounded bg-cyan-950/60 border border-cyan-500 text-cyan-200 text-xs font-mono font-bold">${item}</div>`;
        }
      }
      html += '</div>';

      // Merged list visual
      html += '<div class="flex items-center gap-1.5 overflow-x-auto max-w-full px-2 py-2">';
      html += '<span class="text-xs font-mono text-emerald-400 font-bold mr-2">Merged:</span>';
      if (step.merged.length === 0) {
        html += '<span class="text-xs text-slate-500 italic">dummy</span>';
      } else {
        for (let i = 0; i < step.merged.length; i++) {
          html += `<div class="w-8 h-8 rounded-full bg-emerald-950/70 border border-emerald-500 text-emerald-300 font-mono text-xs font-bold flex items-center justify-center">${step.merged[i]}</div>`;
          if (i < step.merged.length - 1) {
            html += '<span class="text-slate-600 text-xs font-bold">&rarr;</span>';
          }
        }
      }
      html += '</div>';

      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(lists) {
      rawLists = lists;
      buildSteps(lists);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawLists);
"""
    },
    'approaches': [
        {
            'name': "Min-Heap PriorityQueue",
            'badge': "Optimal Primary",
            'color': "emerald",
            'complexity': "O(N log K) Time, O(K) Space",
            'summary': "PriorityQueue holding at most K list heads. Poll smallest node, wire into merged list, and offer its next pointer into the queue.",
            'kotlin_code': """import java.util.PriorityQueue

class Solution {
    fun mergeKLists(lists: Array<ListNode?>): ListNode? {
        if (lists.isEmpty()) return null

        val pq = PriorityQueue<ListNode>(compareBy { it.`val` })
        for (head in lists) {
            if (head != null) {
                pq.add(head)
            }
        }

        val dummy = ListNode(0)
        var tail = dummy

        while (pq.isNotEmpty()) {
            val minNode = pq.poll()
            tail.next = minNode
            tail = tail.next!!

            if (minNode.next != null) {
                pq.add(minNode.next)
            }
        }

        return dummy.next
    }
}""",
            'python_code': """import heapq

class Solution:
    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        # Store (val, unique_id, node) to break ties without comparing ListNode objects
        pq = []
        for i, head in enumerate(lists):
            if head:
                heapq.heappush(pq, (head.val, i, head))
                
        dummy = ListNode(0)
        tail = dummy
        count = len(lists)
        
        while pq:
            val, _, min_node = heapq.heappop(pq)
            tail.next = min_node
            tail = tail.next
            
            if min_node.next:
                heapq.heappush(pq, (min_node.next.val, count, min_node.next))
                count += 1
                
        return dummy.next""",
            'java_code': """class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));
        for (ListNode head : lists) {
            if (head != null) {
                pq.offer(head);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (!pq.isEmpty()) {
            ListNode minNode = pq.poll();
            tail.next = minNode;
            tail = tail.next;

            if (minNode.next != null) {
                pq.offer(minNode.next);
            }
        }

        return dummy.next;
    }
}""",
            'memory_breakdown': [
                {'component': "PriorityQueue", 'cost': "O(K)", 'description': "Holds references to at most K list heads at any given time."},
                {'component': "Dummy Sentinel", 'cost': "O(1)", 'description': "Single head pointer for result construction."}
            ]
        },
        {
            'name': "Divide and Conquer Pairwise Merging",
            'badge': "Space Optimal",
            'color': "blue",
            'complexity': "O(N log K) Time, O(1) Auxiliary Space",
            'summary': "Merge pairs of lists iteratively in rounds (0 with 1, 2 with 3, etc.). In each round, the number of lists is halved, terminating after log2(K) rounds with zero extra memory.",
            'kotlin_code': """class SolutionDivideConquer {
    fun mergeKLists(lists: Array<ListNode?>): ListNode? {
        if (lists.isEmpty()) return null
        var interval = 1
        val k = lists.size

        while (interval < k) {
            for (i in 0 until k - interval step interval * 2) {
                lists[i] = mergeTwo(lists[i], lists[i + interval])
            }
            interval *= 2
        }

        return lists[0]
    }

    private fun mergeTwo(l1: ListNode?, l2: ListNode?): ListNode? {
        val dummy = ListNode(0)
        var tail = dummy
        var p1 = l1
        var p2 = l2

        while (p1 != null && p2 != null) {
            if (p1.`val` <= p2.`val`) {
                tail.next = p1
                p1 = p1.next
            } else {
                tail.next = p2
                p2 = p2.next
            }
            tail = tail.next!!
        }

        tail.next = p1 ?: p2
        return dummy.next
    }
}""",
            'python_code': """class SolutionDivideConquer:
    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        if not lists:
            return None
        interval = 1
        k = len(lists)
        
        while interval < k:
            for i in range(0, k - interval, interval * 2):
                lists[i] = self.merge_two(lists[i], lists[i + interval])
            interval *= 2
            
        return lists[0]
        
    def merge_two(self, l1: Optional[ListNode], l2: Optional[ListNode]) -> Optional[ListNode]:
        dummy = ListNode(0)
        tail = dummy
        while l1 and l2:
            if l1.val <= l2.val:
                tail.next = l1
                l1 = l1.next
            else:
                tail.next = l2
                l2 = l2.next
            tail = tail.next
        tail.next = l1 or l2
        return dummy.next""",
            'java_code': """class SolutionDivideConquer {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        int interval = 1;
        int k = lists.length;

        while (interval < k) {
            for (int i = 0; i < k - interval; i += interval * 2) {
                lists[i] = mergeTwo(lists[i], lists[i + interval]);
            }
            interval *= 2;
        }

        return lists[0];
    }

    private ListNode mergeTwo(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                tail.next = l1;
                l1 = l1.next;
            } else {
                tail.next = l2;
                l2 = l2.next;
            }
            tail = tail.next;
        }
        tail.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }
}""",
            'memory_breakdown': [
                {'component': "Auxiliary Pointers", 'cost': "O(1)", 'description': "Modifies list references in-place without heap allocations."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun mergeKLists(lists: Array<ListNode?>): ListNode? {
    // Write your solution here
    return null
}""",
        'python_boilerplate': """def mergeKLists(lists: List[Optional[ListNode]]) -> Optional[ListNode]:
    # Write your solution here
    return None""",
        'java_boilerplate': """public ListNode mergeKLists(ListNode[] lists) {
    // Write your solution here
    return null;
}""",
        'js_test_runner': """
function ListNode(val, next) {
    this.val = (val===undefined ? 0 : val);
    this.next = (next===undefined ? null : next);
}

function buildList(arr) {
    const dummy = new ListNode(0);
    let tail = dummy;
    for (let x of arr) {
        tail.next = new ListNode(x);
        tail = tail.next;
    }
    return dummy.next;
}

function listToArr(node) {
    const res = [];
    while (node) {
        res.push(node.val);
        node = node.next;
    }
    return res;
}

function mergeKLists(lists) {
    const pq = [];
    for (let head of lists) {
        if (head) pq.push(head);
    }
    const dummy = new ListNode(0);
    let tail = dummy;
    while (pq.length > 0) {
        pq.sort((a,b) => a.val - b.val);
        const minNode = pq.shift();
        tail.next = minNode;
        tail = tail.next;
        if (minNode.next) pq.push(minNode.next);
    }
    return dummy.next;
}

const tests = [
    { input: [[1,4,5],[1,3,4],[2,6]], expected: [1,1,2,3,4,4,5,6] },
    { input: [], expected: [] },
    { input: [[]], expected: [] }
];

let passed = 0;
tests.forEach((t, i) => {
    const lists = t.input.map(buildList);
    const outputNode = mergeKLists(lists);
    const output = listToArr(outputNode);
    const ok = JSON.stringify(output) === JSON.stringify(t.expected);
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: [${output}], Expected: [${t.expected}])`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "Let's clarify the problem parameters: we are given an array of `k` singly linked lists, each sorted in non-decreasing order. We need to merge them into a single sorted linked list and return the head.",
        'phase2': "A naive sequential merge would merge list 0 with 1, then with 2, which does redundant work and takes $O(N \\cdot K)$ time. Instead, we can use a Min-Heap of size $K$.",
        'phase3': "We initialize a `PriorityQueue` with the head of each non-empty list. At each step, we extract the minimum node, append it to our `dummy` linked list, and if that node has a `next`, offer `node.next` into the queue.",
        'phase4': "Because the queue contains at most $K$ items, each insertion and deletion takes $O(\\log K)$ time. With $N$ total nodes across all lists, the total runtime is $O(N \\log K)$ with $O(K)$ space.",
        'senior_followup': "If memory is so constrained that an $O(K)$ heap is prohibited, we can use an iterative pairwise Divide and Conquer merge approach, which achieves the exact same $O(N \\log K)$ time complexity while requiring strictly $O(1)$ auxiliary space."
    },
    'quiz': [
        {
            'question': "What is the maximum size of the PriorityQueue during the execution of the Min-Heap approach?",
            'options': [
                "At most K (one active candidate node per linked list)",
                "O(N) (all nodes across all lists)",
                "O(N * K)",
                "O(1)"
            ],
            'correct_index': 0,
            'explanation': "The priority queue only holds the current head node of each non-exhausted list. Therefore, the heap size is strictly bounded by K."
        },
        {
            'question': "How does the Divide and Conquer approach achieve O(N log K) time while using O(1) auxiliary space?",
            'options': [
                "By pairing up lists and merging them in-place, halving the number of lists in each of the log2(K) rounds",
                "By converting all lists to arrays first",
                "By sorting with Quicksort",
                "By using thread-local memory caches"
            ],
            'correct_index': 0,
            'explanation': "In round 1, K lists become K/2; in round 2, K/4, taking log2(K) rounds. Each round processes all N nodes in-place by rewiring next pointers, consuming O(1) auxiliary space."
        }
    ]
}

p95 = {
    'num': 95,
    'lc': 124,
    'title': "Binary Tree Maximum Path Sum",
    'category': "Binary Trees & BST",
    'difficulty': "Hard",
    'pass_label': "Pass 3",
    'lc_slug': "binary-tree-maximum-path-sum",
    'verbatim_statement': """<p>A <strong>path</strong> in a binary tree is a sequence of nodes where each pair of adjacent nodes in the sequence has an edge connecting them. A node can only appear in the sequence <strong>at most once</strong>. Note that the path does not need to pass through the root.</p>
<p>The <strong>path sum</strong> of a path is the sum of the node's values in the path.</p>
<p>Given the <code>root</code> of a binary tree, return <em>the maximum <strong>path sum</strong> of any <strong>non-empty</strong> path</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'root = [1,2,3]', 'output': '6\nExplanation: The optimal path is 2 -> 1 -> 3 with a path sum of 2 + 1 + 3 = 6.'},
        {'title': "Example 2", 'input': 'root = [-10,9,20,null,null,15,7]', 'output': '42\nExplanation: The optimal path is 15 -> 20 -> 7 with a path sum of 15 + 20 + 7 = 42.'}
    ],
    'constraints': [
        "The number of nodes in the tree is in the range [1, 3 * 10^4].",
        "-1000 <= Node.val <= 1000"
    ],
    'plain_english': "A path in a binary tree can start and end at any node, traveling upward through ancestors and downward into other branches. At each node, the path can either 'arch' through this node (combining left branch + node + right branch), or continue upward to the node's parent (node + max(left, right)). Crucially, any child branch that yields a negative sum should be clamped to 0 (`max(0, gain)`), meaning we simply choose NOT to extend our path down that branch. We run a post-order DFS: for each node, compute `leftGain = max(0, dfs(left))` and `rightGain = max(0, dfs(right))`. The maximum arched path through this node is `node.val + leftGain + rightGain`, which updates our global maximum. We then return `node.val + max(leftGain, rightGain)` to the parent caller.",
    'boundary_checks': {
        'min_base': "Single negative node <code>[-3]</code> &rarr; Must pick a non-empty path, returns -3.",
        'best_exit': "All negative values <code>[-10, -20, -5]</code> &rarr; Returns max single node -5.",
        'worst_scan': "Asymmetric zigzag tree &rarr; Post-order recursion handles any tree topology in O(N) time."
    },
    'mental_model': {
        'analogy_title': "The Mountain Bridge and Ridgeline",
        'analogy_text': "Imagine standing on a mountain peak (`node.val`). You look down your left ridge (`leftGain`) and your right ridge (`rightGain`). If a ridge leads into a toxic swamp (negative sum), you close that trail (`max(0, gain)`). You can build an arching scenic bridge across your peak connecting the left ridge and right ridge (`node.val + leftGain + rightGain`), which might be the most scenic view in the entire mountain range (updating global max). However, if your parent mountain higher up wants you to connect to them, you can only offer them ONE ridge (the best one: `node.val + max(leftGain, rightGain)`), because a continuous trail cannot split into two directions at once.",
        'aha_moment': "A path can only arch (use both left and right children) at ONE apex node! For that apex's parent, it can only extend along a single branch. Clamping negative child gains to 0 cleanly handles negative node values.",
        'trigger': "Maximum path sum in binary tree (any start/end, non-empty) &rarr; Post-order DFS returning single-branch gain while updating global arched max.",
        'sisters': [
            {'name': "Diameter of Binary Tree", 'lc': "LC #543"},
            {'name': "Binary Tree Longest Consecutive Sequence", 'lc': "LC #298"},
            {'name': "Path Sum III", 'lc': "LC #437"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(H) Space",
        'naive_text': "For every node in the tree, run a separate DFS down its left and right subtrees to calculate max linear paths.",
        'wasted_work': "Re-traversing subtrees multiple times from different ancestor frames instead of bottom-up post-order aggregation.",
        'optimal_complexity': "O(N) Time, O(H) Space",
        'optimal_text': "Single-pass post-order DFS (bottom-up). Each node is visited once.",
        'core_invariant': "The recursive function `dfs(node)` returns the strictly maximum non-negative contribution this node can provide to a path extending to its parent.",
        'table_rows': [
            {'name': "Top-Down DFS per Node", 'time': "O(N^2)", 'space': "O(H)", 'note': "Calculates subtree depths repeatedly; quadratic on skewed trees."},
            {'name': "★ Post-Order Single Pass DFS", 'time': "O(N)", 'space': "O(H)", 'note': "Optimal; visits each node once in bottom-up post-order."}
        ]
    },
    'trace_table': {
        'input_desc': 'root = [-10, 9, 20, null, null, 15, 7]',
        'columns': ["Node Visited", "leftGain (clamped)", "rightGain (clamped)", "Arched Sum (Local)", "Global Max Updated", "Returned to Parent"],
        'rows': [
            ["Node 9", "0", "0", "9 + 0 + 0 = 9", "max(-∞, 9) = 9", "9 + 0 = 9"],
            ["Node 15", "0", "0", "15 + 0 + 0 = 15", "max(9, 15) = 15", "15 + 0 = 15"],
            ["Node 7", "0", "0", "7 + 0 + 0 = 7", "max(15, 7) = 15", "7 + 0 = 7"],
            ["Node 20", "15", "7", "20 + 15 + 7 = 42", "max(15, 42) = 42!", "20 + max(15, 7) = 35"],
            ["Node -10", "9", "35", "-10 + 9 + 35 = 34", "max(42, 34) = 42", "-10 + 35 = 25"]
        ],
        'takeaway': "The apex node is Node 20 with arched path `15 -> 20 -> 7 = 42`. Global maximum = 42."
    },
    'stepper': {
        'title': "Binary Tree Path Sum Stepper",
        'default_input': 'root=[-10,9,20,null,null,15,7]',
        'presets': [
            {'label': "Example 2: [-10,9,20,null,null,15,7]", 'onclick': 'loadPreset("ex2")'},
            {'label': "Simple: [1,2,3]", 'onclick': 'loadPreset("simple")'},
            {'label': "All Negative: [-3]", 'onclick': 'loadPreset("neg")'}
        ],
        'metrics': [
            {'id': "metric-dfs-node", 'label': "Current Node", 'color': "text-amber-400", 'initial': "None"},
            {'id': "metric-left-gain", 'label': "Left Gain", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-right-gain", 'label': "Right Gain", 'color': "text-purple-400", 'initial': "0"},
            {'id': "metric-global-max", 'label': "Global Max Sum", 'color': "text-emerald-400", 'initial': "-∞"}
        ],
        'visual_html': '<div id="p95-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[180px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace bottom-up post-order path sum evaluation.',
        'pseudocode_lines': [
            "var maxSum = Int.MIN_VALUE",
            "fun maxGain(node: TreeNode?): Int {",
            "    if (node == null) return 0",
            "    val leftGain = maxOf(0, maxGain(node.left))",
            "    val rightGain = maxOf(0, maxGain(node.right))",
            "    val currentPath = node.`val` + leftGain + rightGain",
            "    maxSum = maxOf(maxSum, currentPath)",
            "    return node.`val` + maxOf(leftGain, rightGain)",
            "}"
        ],
        'js_code': """
    let currentPreset = "ex2";

    function buildSteps(preset) {
      steps = [];

      if (preset === "neg") {
        steps.push({ node: "-3", left: 0, right: 0, local: -3, global: -3, line: 1, narration: "Single node -3: path sum is -3." });
        return;
      }

      if (preset === "simple") {
        steps.push({ node: "2", left: 0, right: 0, local: 2, global: 2, line: 4, narration: "Leaf 2 returns gain 2. Global max = 2." });
        steps.push({ node: "3", left: 0, right: 0, local: 3, global: 3, line: 5, narration: "Leaf 3 returns gain 3. Global max = 3." });
        steps.push({ node: "1 (Root)", left: 2, right: 3, local: 6, global: 6, line: 7, narration: "Apex 1 combines 2 + 1 + 3 = 6! Global max = 6." });
        return;
      }

      // ex2: [-10, 9, 20, null, null, 15, 7]
      steps.push({
        node: "Start", left: 0, right: 0, local: 0, global: -Infinity, line: 1,
        narration: "Starting post-order DFS on tree [-10, 9, 20, null, null, 15, 7]."
      });

      steps.push({
        node: "Node 9", left: 0, right: 0, local: 9, global: 9, line: 4,
        narration: "Leaf Node 9: left=0, right=0. Local arch = 9. Global max = 9. Returns 9."
      });

      steps.push({
        node: "Node 15", left: 0, right: 0, local: 15, global: 15, line: 4,
        narration: "Leaf Node 15: left=0, right=0. Local arch = 15. Global max = 15. Returns 15."
      });

      steps.push({
        node: "Node 7", left: 0, right: 0, local: 7, global: 15, line: 5,
        narration: "Leaf Node 7: left=0, right=0. Local arch = 7. Global max remains 15. Returns 7."
      });

      steps.push({
        node: "Node 20", left: 15, right: 7, local: 42, global: 42, line: 7,
        narration: "Apex Node 20: leftGain=15, rightGain=7. Local arch = 20 + 15 + 7 = 42! Global max = 42! Returns 20 + max(15,7) = 35."
      });

      steps.push({
        node: "Node -10 (Root)", left: 9, right: 35, local: 34, global: 42, line: 7,
        narration: "Root Node -10: leftGain=9, rightGain=35. Local arch = -10 + 9 + 35 = 34. Global max remains 42."
      });

      steps.push({
        node: "Done", left: 0, right: 0, local: 42, global: 42, line: 9,
        narration: "Traversal complete! Maximum path sum is 42 (path 15 -> 20 -> 7)."
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p95-stage');
      if (!stage) return;
      document.getElementById('metric-dfs-node').textContent = step.node;
      document.getElementById('metric-left-gain').textContent = step.left;
      document.getElementById('metric-right-gain').textContent = step.right;
      document.getElementById('metric-global-max').textContent = step.global === -Infinity ? "-∞" : step.global;

      let html = '<div class="flex flex-col items-center gap-3">';
      html += `<div class="p-3 rounded-xl border border-amber-500 bg-amber-950/40 text-amber-200 text-xs font-mono">
                 <strong>Evaluating:</strong> ${step.node} | Local Arch Sum = <span class="text-emerald-400 font-bold">${step.local}</span>
               </div>`;
      html += '<div class="text-xs text-slate-400 font-mono text-center max-w-md">Path can arch at an apex node combining left + right, but only offers its best single branch to ancestors.</div>';
      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(p) {
      currentPreset = p;
      buildSteps(p);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(currentPreset);
"""
    },
    'approaches': [
        {
            'name': "Post-Order Bottom-Up DFS",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(H) Space",
            'summary': "Bottom-up recursive DFS. At each node, clamp negative child contributions to 0, update global max with `node.val + leftGain + rightGain`, and return `node.val + max(leftGain, rightGain)` to parent.",
            'kotlin_code': """class Solution {
    private var maxSum = Int.MIN_VALUE

    fun maxPathSum(root: TreeNode?): Int {
        maxSum = Int.MIN_VALUE
        maxGain(root)
        return maxSum
    }

    private fun maxGain(node: TreeNode?): Int {
        if (node == null) return 0

        // Clamping to 0: ignore subtrees with negative net contribution
        val leftGain = maxOf(0, maxGain(node.left))
        val rightGain = maxOf(0, maxGain(node.right))

        // Price of the new path where `node` acts as apex
        val currentPath = node.`val` + leftGain + rightGain
        maxSum = maxOf(maxSum, currentPath)

        // For parent recursion: can only extend along ONE child branch
        return node.`val` + maxOf(leftGain, rightGain)
    }
}""",
            'python_code': """class Solution:
    def maxPathSum(self, root: Optional[TreeNode]) -> int:
        max_sum = float('-inf')
        
        def max_gain(node: Optional[TreeNode]) -> int:
            nonlocal max_sum
            if not node:
                return 0
                
            # Clamp negative branches to 0
            left_gain = max(0, max_gain(node.left))
            right_gain = max(0, max_gain(node.right))
            
            # Check path arching at this node
            current_path = node.val + left_gain + right_gain
            max_sum = max(max_sum, current_path)
            
            # Return single-branch extension to parent
            return node.val + max(left_gain, right_gain)
            
        max_gain(root)
        return max_sum""",
            'java_code': """class Solution {
    private int maxSum = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        maxSum = Integer.MIN_VALUE;
        maxGain(root);
        return maxSum;
    }

    private int maxGain(TreeNode node) {
        if (node == null) return 0;

        // Clamp negative paths to 0
        int leftGain = Math.max(0, maxGain(node.left));
        int rightGain = Math.max(0, maxGain(node.right));

        // Evaluate arched path through node
        int currentPath = node.val + leftGain + rightGain;
        maxSum = Math.max(maxSum, currentPath);

        // Return best single branch to parent
        return node.val + Math.max(leftGain, rightGain);
    }
}""",
            'memory_breakdown': [
                {'component': "Recursion Call Stack", 'cost': "O(H)", 'description': "Height of the tree (O(log N) for balanced trees, O(N) for degenerate linked lists)."},
                {'component': "Scalar Tracker `maxSum`", 'cost': "O(1)", 'description': "Single integer tracking the all-time maximum path sum."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """fun maxPathSum(root: TreeNode?): Int {
    // Write your solution here
    return 0
}""",
        'python_boilerplate': """def maxPathSum(root: Optional[TreeNode]) -> int:
    # Write your solution here
    return 0""",
        'java_boilerplate': """public int maxPathSum(TreeNode root) {
    // Write your solution here
    return 0;
}""",
        'js_test_runner': """
function TreeNode(val, left, right) {
    this.val = (val===undefined ? 0 : val);
    this.left = (left===undefined ? null : left);
    this.right = (right===undefined ? null : right);
}

function buildTree(arr) {
    if (!arr || arr.length === 0 || arr[0] === null) return null;
    const root = new TreeNode(arr[0]);
    const queue = [root];
    let i = 1;
    while (queue.length > 0 && i < arr.length) {
        const curr = queue.shift();
        if (i < arr.length && arr[i] !== null) {
            curr.left = new TreeNode(arr[i]);
            queue.push(curr.left);
        }
        i++;
        if (i < arr.length && arr[i] !== null) {
            curr.right = new TreeNode(arr[i]);
            queue.push(curr.right);
        }
        i++;
    }
    return root;
}

function maxPathSum(root) {
    let maxSum = -Infinity;
    function maxGain(node) {
        if (!node) return 0;
        const leftGain = Math.max(0, maxGain(node.left));
        const rightGain = Math.max(0, maxGain(node.right));
        const currentPath = node.val + leftGain + rightGain;
        maxSum = Math.max(maxSum, currentPath);
        return node.val + Math.max(leftGain, rightGain);
    }
    maxGain(root);
    return maxSum;
}

const tests = [
    { input: [1,2,3], expected: 6 },
    { input: [-10,9,20,null,null,15,7], expected: 42 },
    { input: [-3], expected: -3 }
];

let passed = 0;
tests.forEach((t, i) => {
    const root = buildTree(t.input);
    const output = maxPathSum(root);
    const ok = output === t.expected;
    if (ok) passed++;
    log(`Test ${i+1}: ${ok ? 'PASSED' : 'FAILED'} (Got: ${output}, Expected: ${t.expected})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's clarify what defines a valid path: a sequence of connected nodes where no node is visited more than once. The path does not need to pass through the root, and negative numbers can be present.",
        'phase2': "A path can only have ONE highest node (apex). At that apex, the path can branch into both left and right children. But for any ancestor above that apex, only ONE branch can be chosen. Furthermore, any branch with a negative total sum should be clamped to 0.",
        'phase3': "We implement a bottom-up post-order DFS. At each node, we recursively compute `leftGain = max(0, dfs(node.left))` and `rightGain = max(0, dfs(node.right))`.",
        'phase4': "The local arched path sum through this node is `node.val + leftGain + rightGain`, which updates our global `maxSum`. Then we return `node.val + max(leftGain, rightGain)` to the caller. Each node is visited once in $O(N)$ time with $O(H)$ space.",
        'senior_followup': "If the tree was so massive it caused a call stack overflow, we could convert this to an iterative post-order traversal using two stacks or Morris traversal modified to track subtree branch gains."
    },
    'quiz': [
        {
            'question': "Why do we compute `maxOf(0, maxGain(node.left))` with a clamp to zero?",
            'options': [
                "Because if a subtree's total gain is negative, we can choose not to include that subtree in the path at all",
                "Because node values are guaranteed to be positive",
                "Because negative numbers crash the math function",
                "To ensure the recursion terminates"
            ],
            'correct_index': 0,
            'explanation': "Since paths do not need to span to leaf nodes, if a subtree adds a negative value, we simply choose to terminate the path before entering that subtree, adding 0."
        },
        {
            'question': "Why does `maxGain` return `node.val + max(leftGain, rightGain)` rather than `node.val + leftGain + rightGain` to its parent caller?",
            'options': [
                "Because a continuous path extending to an ancestor cannot split into both left and right child branches simultaneously",
                "Because of integer overflow limitations",
                "Because right child branches always have lower priority",
                "Because Java pass-by-value prevents returning two values"
            ],
            'correct_index': 0,
            'explanation': "A path in a graph cannot branch into two paths at once. An arched path can only exist at the single apex node; ancestors can only extend along one branch."
        }
    ]
}

p96 = {
    'num': 96,
    'lc': 295,
    'title': "Find Median from Data Stream",
    'category': "Heaps & Priority Queues",
    'difficulty': "Hard",
    'pass_label': "Pass 3",
    'lc_slug': "find-median-from-data-stream",
    'verbatim_statement': """<p>The <strong>median</strong> is the middle value in an ordered integer list. If the size of the list is even, there is no middle value, and the median is the mean of the two middle values.</p>
<ul>
	<li>For example, for <code>arr = [2,3,4]</code>, the median is <code>3</code>.</li>
	<li>For example, for <code>arr = [2,3]</code>, the median is <code>(2 + 3) / 2 = 2.5</code>.</li>
</ul>
<p>Implement the MedianFinder class:</p>
<ul>
	<li><code>MedianFinder()</code> initializes the <code>MedianFinder</code> object.</li>
	<li><code>void addNum(int num)</code> adds the integer <code>num</code> from the data stream to the data structure.</li>
	<li><code>double findMedian()</code> returns the median of all elements so far. Answers within <code>10<sup>-5</sup></code> of the actual answer will be accepted.</li>
</ul>""",
    'examples': [
        {'title': "Example 1", 'input': '["MedianFinder", "addNum", "addNum", "findMedian", "addNum", "findMedian"]\n[[], [1], [2], [], [3], []]', 'output': '[null, null, null, 1.5, null, 2.0]\nExplanation:\nMedianFinder medianFinder = new MedianFinder();\nmedianFinder.addNum(1);    // arr = [1]\nmedianFinder.addNum(2);    // arr = [1, 2]\nmedianFinder.findMedian(); // return 1.5 (i.e., (1 + 2) / 2)\nmedianFinder.addNum(3);    // arr[1, 2, 3]\nmedianFinder.findMedian(); // return 2.0'}
    ],
    'constraints': [
        "-10^5 <= num <= 10^5",
        "There will be at least one element in the data structure before calling findMedian.",
        "At most 5 * 10^4 calls will be made to addNum and findMedian."
    ],
    'plain_english': "The median divides numbers into two halves: a smaller half and a larger half. If we can instantaneously access the largest element of the small half and the smallest element of the large half, finding the median takes O(1) time! We maintain two heaps: (1) `maxHeap`: stores the smaller half of numbers (its root is the maximum of the small half). (2) `minHeap`: stores the larger half of numbers (its root is the minimum of the large half). Invariant: `maxHeap` is kept equal in size to `minHeap`, or exactly 1 element larger (`maxHeap.size == minHeap.size + 1`). When `addNum(x)` is called: we push `x` to `maxHeap`, then move `maxHeap.poll()` to `minHeap`. If `minHeap.size > maxHeap.size`, we move `minHeap.poll()` back to `maxHeap`. `findMedian()` takes O(1): if sizes are equal, average their roots; else return `maxHeap.peek()`.",
    'boundary_checks': {
        'min_base': "Single element stream <code>addNum(1), findMedian()</code> &rarr; Returns 1.0 directly from maxHeap root.",
        'best_exit': "Two element stream <code>addNum(1), addNum(2), findMedian()</code> &rarr; Returns (1 + 2) / 2.0 = 1.5.",
        'worst_scan': "50,000 stream elements with duplicate values &rarr; Balanced dual heaps process each insertion in O(log N) time with zero drift."
    },
    'mental_model': {
        'analogy_title': "The Two-Pan Balancing Scale",
        'analogy_text': "Imagine a classic two-pan balance scale. The left pan is a Max-Heap holding the lighter half of rocks; the top rock is the heaviest of the light ones. The right pan is a Min-Heap holding the heavier half of rocks; the top rock is the lightest of the heavy ones. Whenever a new rock arrives, we place it on the left pan, sift the heaviest rock over to the right pan, and re-level the scale so the left pan holds at most 1 more rock than the right. The median is always right at the fulcrum between the two pans!",
        'aha_moment': "We don't need to keep the entire stream sorted! We only need the boundary between the lower 50% and upper 50%. Two balanced heaps achieve this in O(log N) insertion and O(1) median query.",
        'trigger': "Online streaming median / dynamic percentile queries &rarr; Balanced Dual Heaps (Max-Heap + Min-Heap).",
        'sisters': [
            {'name': "Sliding Window Median", 'lc': "LC #480"},
            {'name': "Find Median from Data Stream", 'lc': "LC #295"},
            {'name': "Kth Largest Element in a Stream", 'lc': "LC #703"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N) addNum, O(1) findMedian, or O(1) addNum, O(N log N) findMedian",
        'naive_text': "Insert into dynamic list and sort before every median query, or insert with binary search insertion in O(N).",
        'wasted_work': "Sorting all N elements repeatedly or shifting array elements during insertion.",
        'optimal_complexity': "O(log N) addNum, O(1) findMedian, O(N) Space",
        'optimal_text': "Two balanced heaps (Max-Heap and Min-Heap).",
        'core_invariant': "Every element in `maxHeap` <= every element in `minHeap`, and `0 <= maxHeap.size - minHeap.size <= 1`.",
        'table_rows': [
            {'name': "Dynamic Array + Full Sort", 'time': "O(N log N) per query", 'space': "O(N)", 'note': "Sorts array on every findMedian call; completely unscalable."},
            {'name': "Insertion Sort (bisect.insort)", 'time': "O(N) per addNum", 'space': "O(N)", 'note': "Array element shifting takes linear time per insertion."},
            {'name': "★ Balanced Dual Heaps", 'time': "O(log N) add, O(1) median", 'space': "O(N)", 'note': "Golden standard; logarithmic insertion and instant O(1) median retrieval."}
        ]
    },
    'trace_table': {
        'input_desc': 'addNum(1), addNum(2), findMedian(), addNum(3), findMedian()',
        'columns': ["Operation", "maxHeap (Lower Half)", "minHeap (Upper Half)", "Size Difference", "Returned Median"],
        'rows': [
            ["Init", "[]", "[]", "0", "-"],
            ["addNum(1)", "[1]", "[]", "1 - 0 = 1", "-"],
            ["addNum(2)", "[1]", "[2]", "1 - 1 = 0", "-"],
            ["findMedian()", "[1]", "[2]", "Equal (size 2)", "(1 + 2) / 2.0 = 1.5"],
            ["addNum(3)", "[2, 1]", "[3]", "2 - 1 = 1", "-"],
            ["findMedian()", "[2, 1]", "[3]", "Odd (size 3)", "maxHeap.peek() = 2.0"]
        ],
        'takeaway': "Max-heap holds `[2, 1]`, Min-heap holds `[3]`. Median is 2.0."
    },
    'stepper': {
        'title': "Two-Heap Median Balancing Stepper",
        'default_input': 'add(1) -> add(2) -> median() -> add(3) -> median()',
        'presets': [
            {'label': "Example 1: [1, 2, 3]", 'onclick': 'loadPreset([1, 2, 3])'},
            {'label': "Mixed Stream: [5, 2, 8, 1, 9]", 'onclick': 'loadPreset([5, 2, 8, 1, 9])'}
        ],
        'metrics': [
            {'id': "metric-added-num", 'label': "Incoming Num", 'color': "text-amber-400", 'initial': "None"},
            {'id': "metric-max-heap-root", 'label': "Max-Heap Top", 'color': "text-cyan-400", 'initial': "None"},
            {'id': "metric-min-heap-root", 'label': "Min-Heap Top", 'color': "text-purple-400", 'initial': "None"},
            {'id': "metric-current-median", 'label': "Median", 'color': "text-emerald-400", 'initial': "None"}
        ],
        'visual_html': '<div id="p96-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace two-heap balancing and O(1) median extraction.',
        'pseudocode_lines': [
            "val maxHeap = PriorityQueue<Int>(compareByDescending { it })",
            "val minHeap = PriorityQueue<Int>()",
            "fun addNum(num: Int) {",
            "    maxHeap.offer(num)",
            "    minHeap.offer(maxHeap.poll())",
            "    if (minHeap.size > maxHeap.size) maxHeap.offer(minHeap.poll())",
            "}",
            "fun findMedian(): Double {",
            "    return if (maxHeap.size > minHeap.size) maxHeap.peek().toDouble()",
            "    else (maxHeap.peek() + minHeap.peek()) / 2.0",
            "}"
        ],
        'js_code': """
    let rawStream = [1, 2, 3];

    function buildSteps(stream) {
      steps = [];
      const maxH = []; // Max-heap (lower half)
      const minH = []; // Min-heap (upper half)

      steps.push({
        num: "-", maxH: [], minH: [], median: "-", action: "Initialize Two Heaps", line: 1,
        narration: "Initialized Max-Heap (lower half) and Min-Heap (upper half)."
      });

      for (let x of stream) {
        // Step 1: Add to maxH
        maxH.push(x);
        maxH.sort((a,b) => b - a);

        // Step 2: Sift top of maxH to minH
        const sift = maxH.shift();
        minH.push(sift);
        minH.sort((a,b) => a - b);

        // Step 3: Rebalance
        if (minH.length > maxH.length) {
          const back = minH.shift();
          maxH.push(back);
          maxH.sort((a,b) => b - a);
        }

        const med = (maxH.length > minH.length) ? maxH[0] : (maxH[0] + minH[0]) / 2.0;

        steps.push({
          num: x, maxH: [...maxH], minH: [...minH], median: med,
          action: `Added ${x} & Rebalanced`, line: 3,
          narration: `Added ${x}. maxHeap = [${maxH.join(', ')}], minHeap = [${minH.join(', ')}]. Current median = ${med}.`
        });
      }

      const finalMed = (maxH.length > minH.length) ? maxH[0] : (maxH[0] + minH[0]) / 2.0;
      steps.push({
        num: "Done", maxH: [...maxH], minH: [...minH], median: finalMed,
        action: "Finished", line: 8,
        narration: `All stream items processed. Final median = ${finalMed}.`
      });
    }

    function renderStage(step) {
      const stage = document.getElementById('p96-stage');
      if (!stage) return;
      document.getElementById('metric-added-num').textContent = step.num;
      document.getElementById('metric-max-heap-root').textContent = step.maxH.length > 0 ? step.maxH[0] : "Empty";
      document.getElementById('metric-min-heap-root').textContent = step.minH.length > 0 ? step.minH[0] : "Empty";
      document.getElementById('metric-current-median').textContent = step.median;

      let html = '<div class="flex items-center justify-center gap-8 w-full max-w-lg">';
      
      // Max-Heap Box
      html += `<div class="flex-1 p-3 rounded-xl border border-cyan-500 bg-cyan-950/30 flex flex-col items-center">
                 <span class="text-xs font-mono text-cyan-300 font-bold uppercase">Max-Heap (Lower Half)</span>
                 <span class="text-lg font-extrabold text-white mt-1">${step.maxH.length > 0 ? step.maxH[0] : '-'}</span>
                 <span class="text-[10px] text-slate-400 mt-1">[${step.maxH.join(', ')}]</span>
               </div>`;

      // Scale fulcrum
      html += `<div class="flex flex-col items-center">
                 <span class="text-2xl font-bold text-emerald-400">&#9878;</span>
                 <span class="text-xs font-mono text-emerald-300 font-bold mt-1">${step.median}</span>
                 <span class="text-[9px] uppercase text-slate-500 font-mono">Median</span>
               </div>`;

      // Min-Heap Box
      html += `<div class="flex-1 p-3 rounded-xl border border-purple-500 bg-purple-950/30 flex flex-col items-center">
                 <span class="text-xs font-mono text-purple-300 font-bold uppercase">Min-Heap (Upper Half)</span>
                 <span class="text-lg font-extrabold text-white mt-1">${step.minH.length > 0 ? step.minH[0] : '-'}</span>
                 <span class="text-[10px] text-slate-400 mt-1">[${step.minH.join(', ')}]</span>
               </div>`;

      html += '</div>';
      stage.innerHTML = html;
    }

    window.loadPreset = function(stream) {
      rawStream = stream;
      buildSteps(stream);
      currentStepIndex = 0;
      updateUI();
    };

    buildSteps(rawStream);
"""
    },
    'approaches': [
        {
            'name': "Balanced Dual Heaps (Max-Heap + Min-Heap)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(log N) addNum, O(1) findMedian, O(N) Space",
            'summary': "Two PriorityQueues: `maxHeap` stores smaller 50% of numbers, `minHeap` stores larger 50%. Balances sizes such that `maxHeap.size == minHeap.size` or `maxHeap.size == minHeap.size + 1`.",
            'kotlin_code': """import java.util.PriorityQueue

class MedianFinder() {
    // Max-Heap stores lower half of numbers
    private val maxHeap = PriorityQueue<Int>(compareByDescending { it })
    // Min-Heap stores upper half of numbers
    private val minHeap = PriorityQueue<Int>()

    fun addNum(num: Int) {
        maxHeap.offer(num)
        minHeap.offer(maxHeap.poll())

        // Maintain balance: maxHeap must hold >= minHeap
        if (minHeap.size > maxHeap.size) {
            maxHeap.offer(minHeap.poll())
        }
    }

    fun findMedian(): Double {
        return if (maxHeap.size > minHeap.size) {
            maxHeap.peek().toDouble()
        } else {
            (maxHeap.peek().toDouble() + minHeap.peek().toDouble()) / 2.0
        }
    }
}""",
            'python_code': """import heapq

class MedianFinder:
    def __init__(self):
        # Python heapq is min-heap; negate values for max-heap
        self.small = [] # max-heap (negated values)
        self.large = [] # min-heap

    def addNum(self, num: int) -> None:
        heapq.heappush(self.small, -num)
        heapq.heappush(self.large, -heapq.heappop(self.small))
        
        # Maintain balance invariant
        if len(self.large) > len(self.small):
            heapq.heappush(self.small, -heapq.heappop(self.large))

    def findMedian(self) -> float:
        if len(self.small) > len(self.large):
            return float(-self.small[0])
        return (-self.small[0] + self.large[0]) / 2.0""",
            'java_code': """import java.util.Collections;
import java.util.PriorityQueue;

class MedianFinder {
    private final PriorityQueue<Integer> maxHeap;
    private final PriorityQueue<Integer> minHeap;

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        maxHeap.offer(num);
        minHeap.offer(maxHeap.poll());

        if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        } else {
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }
}""",
            'memory_breakdown': [
                {'component': "Dual Heaps", 'cost': "O(N)", 'description': "Total elements partitioned across maxHeap and minHeap."},
                {'component': "Scalar References", 'cost': "O(1)", 'description': "Heap root references for instant median calculation."}
            ]
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class MedianFinder() {
    fun addNum(num: Int) {}
    fun findMedian(): Double = 0.0
}""",
        'python_boilerplate': """class MedianFinder:
    def __init__(self):
        pass
    def addNum(self, num: int) -> None:
        pass
    def findMedian(self) -> float:
        return 0.0""",
        'java_boilerplate': """class MedianFinder {
    public MedianFinder() {}
    public void addNum(int num) {}
    public double findMedian() { return 0.0; }
}""",
        'js_test_runner': """
function MedianFinder() {
    const maxH = [];
    const minH = [];

    this.addNum = function(num) {
        maxH.push(num);
        maxH.sort((a,b) => b - a);
        minH.push(maxH.shift());
        minH.sort((a,b) => a - b);
        if (minH.length > maxH.length) {
            maxH.push(minH.shift());
            maxH.sort((a,b) => b - a);
        }
    };

    this.findMedian = function() {
        if (maxH.length > minH.length) return maxH[0];
        return (maxH[0] + minH[0]) / 2.0;
    };
}

const mf = new MedianFinder();
const tests = [];
mf.addNum(1);
mf.addNum(2);
tests.push({ op: "findMedian after [1,2]", got: mf.findMedian(), expected: 1.5 });
mf.addNum(3);
tests.push({ op: "findMedian after [1,2,3]", got: mf.findMedian(), expected: 2.0 });

let passed = 0;
tests.forEach((t, i) => {
    const ok = Math.abs(t.got - t.expected) < 1e-5;
    if (ok) passed++;
    log(`Test ${i+1}: ${t.op} -> ${ok ? 'PASSED' : 'FAILED'} (Got: ${t.got}, Expected: ${t.expected})`);
});
updateStatus(passed === tests.length, passed, tests.length);
"""
    },
    'spoken_script': {
        'phase1': "First, let's understand the data structure: we receive an ongoing stream of integers and must return the running median at any point in time. The median is the exact middle element for odd lengths, or the mean of the two middle elements for even lengths.",
        'phase2': "A naive solution of storing elements in an array and sorting takes $O(N \\log N)$ per query. Insertion sort takes $O(N)$ per insertion. We can achieve $O(\\log N)$ insertion and instantaneous $O(1)$ query using Two Balanced Heaps.",
        'phase3': "We divide the stream into two halves: `maxHeap` stores the smaller half (its root is the maximum of the small numbers), and `minHeap` stores the larger half (its root is the minimum of the large numbers).",
        'phase4': "When adding a number, we push to `maxHeap`, sift its max to `minHeap`, and rebalance if `minHeap.size > maxHeap.size`. `findMedian` simply inspects the heap roots in $O(1)$ time.",
        'senior_followup': "If all numbers are bounded within a known small integer range (e.g., between 0 and 100), we could replace the heaps with a frequency array `int[101]` and use a two-pointer cumulative count to find the median in $O(1)$ time and $O(1)$ space."
    },
    'quiz': [
        {
            'question': "What invariant must hold between the two heaps at all times?",
            'options': [
                "Every element in maxHeap <= every element in minHeap, and maxHeap has either equal size or 1 more element than minHeap",
                "Both heaps must always have an identical number of elements",
                "maxHeap contains all odd numbers, minHeap contains all even numbers",
                "minHeap must be twice as large as maxHeap"
            ],
            'correct_index': 0,
            'explanation': "The dual-heap technique requires maxHeap to store the lower half and minHeap the upper half, with maxHeap holding at most 1 extra element to make median extraction O(1)."
        },
        {
            'question': "What is the time complexity of `findMedian()` in the Dual-Heap solution?",
            'options': [
                "O(1) because it only inspects the roots of maxHeap and minHeap via peek()",
                "O(log N) due to heap reconstruction",
                "O(N) to iterate through the heap arrays",
                "O(N log N) because heaps must be sorted"
            ],
            'correct_index': 0,
            'explanation': "Finding the median only requires accessing the root element of maxHeap and/or minHeap via peek(), which is an O(1) constant time operation."
        }
    ]
}
