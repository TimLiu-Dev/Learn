import sys, re
sys.path.append('scripts')
from template_engine import render_lesson

cluster3_problems = [
    # ---------------- 0034: REMOVE NTH NODE FROM END OF LIST ----------------
    {
        'num': 34,
        'lc': 19,
        'title': "Remove Nth Node From End of List",
        'category': "Linked Lists & System Node Design",
        'difficulty': "Medium",
        'lc_slug': "remove-nth-node-from-end-of-list",
        'verbatim_statement': """<p>Given the <code>head</code> of a linked list, remove the <code>n<sup>th</sup></code> node from the end of the list and return its head.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'head = [1,2,3,4,5], n = 2', 'output': '[1,2,3,5] (node 4 removed)'},
            {'title': "Example 2", 'input': 'head = [1], n = 1', 'output': '[]'},
            {'title': "Example 3", 'input': 'head = [1,2], n = 1', 'output': '[1]'}
        ],
        'constraints': [
            "The number of nodes in the list is sz.",
            "1 <= sz <= 30",
            "0 <= Node.val <= 100",
            "1 <= n <= sz"
        ],
        'plain_english': "Remove the nth node counting backward from the tail in a single pass. Use a dummy head node to handle deleting the first element. Advance a fast pointer n + 1 steps ahead of a slow pointer. When fast reaches null, slow is positioned exactly right before the node to be deleted.",
        'boundary_checks': {
            'min_base': "<code>head = [1], n = 1</code> &rarr; Deleting the only node &rarr; returns null.",
            'best_exit': "Deleting the head node <code>head = [1, 2], n = 2</code> &rarr; Dummy node sentinel handles smoothly.",
            'worst_scan': "Deleting the tail node <code>n = 1</code> &rarr; Slow stops at second to last node, sets next = null."
        },
        'mental_model': {
            'analogy_title': "The Fixed-Length Spacing Tape",
            'analogy_text': "Imagine two runners connected by a rigid stick of length n + 1. The lead runner (fast) runs down a narrow corridor. The trailing runner (slow) follows behind at the exact same speed, held at fixed distance. When the lead runner hits the dead end (null), the trailing runner is guaranteed to be standing directly in front of the trapdoor (the node before target). The trailing runner reaches over and uncouples the target car.",
            'aha_moment': "A dummy sentinel node (`dummy.next = head`) eliminates all edge cases for removing the head node. Spacing `fast` ahead of `slow` by `n + 1` steps guarantees that when `fast == null`, `slow.next` is the exact node to bypass.",
            'trigger': "Whenever finding or deleting the k-th element from the end of a singly linked list &rarr; Offset Two Pointers (Slow & Fast) + Dummy Node.",
            'sisters': [
                {'name': "Middle of the Linked List", 'lc': "LC #876"},
                {'name': "Delete Node in a Linked List", 'lc': "LC #237"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(2N) Time (Two Passes), O(1) Space",
            'naive_text': "First pass traverses the list to count total length L. Second pass traverses to index L - n and unlinks the node.",
            'wasted_work': "Traversing the list twice when a single pass with offset pointers can solve it in one go.",
            'optimal_complexity': "O(N) Time (Single Pass), O(1) Space",
            'optimal_text': "One-pass with fast pointer n + 1 steps ahead of slow. Constant memory, zero backtracking.",
            'core_invariant': "Distance between fast and slow is invariant (n + 1 nodes) throughout the traversal.",
            'table_rows': [
                {'name': "1. Two-Pass Length Count", 'time': "O(2N)", 'space': "O(1)", 'note': "Scans entire list twice; redundant pass."},
                {'name': "★ 2. One-Pass Offset Two Pointers", 'time': "O(N)", 'space': "O(1)", 'note': "Single pass with dummy sentinel node."}
            ]
        },
        'trace_table': {
            'input_desc': 'head = [1, 2, 3, 4, 5], n = 2',
            'columns': ["Step", "Fast Node", "Slow Node", "Distance (Fast - Slow)", "Action / State"],
            'rows': [
                ["Init", "Dummy", "Dummy", "0", "dummy.next = 1"],
                ["Fast offset", "Node(3)", "Dummy", "3 (n + 1 steps)", "Fast advanced n + 1 = 3 steps"],
                ["Step 1", "Node(4)", "Node(1)", "3", "Both advance 1 step"],
                ["Step 2", "Node(5)", "Node(2)", "3", "Both advance 1 step"],
                ["Step 3", "null", "Node(3)", "3", "Fast reached null! Stop."],
                ["Unlink", "null", "Node(3)", "-", "slow.next = slow.next.next (3 -> 5)"]
            ],
            'takeaway': "Node 4 cleanly bypassed. Returns dummy.next -> [1, 2, 3, 5]."
        },
        'stepper': {
            'title': "Offset Two-Pointer Scanning & Sentinel Unlinking",
            'default_input': '1, 2, 3, 4, 5',
            'presets': [
                {'label': "List [1,2,3,4,5], n=2", 'onclick': 'loadPreset([1,2,3,4,5], 2)'},
                {'label': "Delete Head (n=2 on [1,2])", 'onclick': 'loadPreset([1,2], 2)'},
                {'label': "Single Node (n=1 on [1])", 'onclick': 'loadPreset([1], 1)'}
            ],
            'metrics': [
                {'id': "metric-fast", 'label': "Fast Pointer", 'color': "text-cyan-400", 'initial': "Dummy"},
                {'id': "metric-slow", 'label': "Slow Pointer", 'color': "text-emerald-400", 'initial': "Dummy"},
                {'id': "metric-offset", 'label': "Pointer Gap", 'color': "text-amber-400", 'initial': "0"},
                {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Init"}
            ],
            'visual_html': '<div id="linked-list-stage" class="w-full flex items-center justify-center gap-3 py-6 overflow-x-auto min-h-[100px]"></div>',
            'initial_narration': 'Click "Next Step" to trace the one-pass offset pointer scan.',
            'pseudocode_lines': [
                "val dummy = ListNode(0).apply { next = head }",
                "var fast: ListNode? = dummy; var slow: ListNode? = dummy",
                "for (i in 0..n) fast = fast?.next // Advance fast n + 1 steps",
                "while (fast != null) { fast = fast.next; slow = slow?.next }",
                "slow?.next = slow?.next?.next // Bypass target node",
                "return dummy.next"
            ],
            'js_code': """
    let initialArr = [1, 2, 3, 4, 5];
    let nTarget = 2;

    function buildSteps(arr, nVal) {
      if (nVal !== undefined) nTarget = nVal;
      steps = [];
      const nodes = ["Dummy", ...arr];
      let fastIdx = 0;
      let slowIdx = 0;

      steps.push({
        fast: "Dummy",
        slow: "Dummy",
        gap: 0,
        action: "Init Dummy",
        nodes: [...nodes],
        fastIdx: 0,
        slowIdx: 0,
        deletedIdx: -1,
        line: 1,
        narration: "Created sentinel dummy node pointing to head."
      });

      // Advance fast n + 1 steps
      for (let i = 0; i <= nTarget; i++) {
        fastIdx++;
      }
      const fastVal = (fastIdx < nodes.length) ? nodes[fastIdx] : "null";

      steps.push({
        fast: fastVal,
        slow: "Dummy",
        gap: nTarget + 1,
        action: `Fast advanced ${nTarget + 1} steps`,
        nodes: [...nodes],
        fastIdx: fastIdx,
        slowIdx: 0,
        deletedIdx: -1,
        line: 3,
        narration: `Advanced fast pointer ${nTarget + 1} steps ahead of slow. Gap is now fixed at ${nTarget + 1}.`
      });

      while (fastIdx < nodes.length) {
        fastIdx++;
        slowIdx++;
        const fName = (fastIdx < nodes.length) ? nodes[fastIdx] : "null";
        steps.push({
          fast: fName,
          slow: nodes[slowIdx],
          gap: nTarget + 1,
          action: "Advancing both",
          nodes: [...nodes],
          fastIdx: fastIdx,
          slowIdx: slowIdx,
          deletedIdx: -1,
          line: 4,
          narration: `Fast at ${fName}, Slow at ${nodes[slowIdx]}. Advancing in tandem.`
        });
      }

      // Deletion step
      const toDeleteIdx = slowIdx + 1;
      const targetVal = nodes[toDeleteIdx];
      const remaining = [...nodes];
      remaining.splice(toDeleteIdx, 1);

      steps.push({
        fast: "null",
        slow: nodes[slowIdx],
        gap: nTarget + 1,
        action: `Bypass Node(${targetVal})`,
        nodes: remaining,
        fastIdx: fastIdx,
        slowIdx: slowIdx,
        deletedIdx: toDeleteIdx,
        line: 5,
        narration: `Fast reached null! Slow is at Node(${nodes[slowIdx]}). Bypassed Node(${targetVal}): slow.next = slow.next.next.`
      });
    }

    function loadPreset(arr, nVal) {
      document.getElementById('custom-input').value = arr.join(', ');
      initialArr = arr;
      nTarget = nVal;
      stepperReset();
      buildSteps(initialArr, nTarget);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        initialArr = arr;
        stepperReset();
        buildSteps(initialArr, nTarget);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-fast').textContent = step.fast;
      document.getElementById('metric-slow').textContent = step.slow;
      document.getElementById('metric-offset').textContent = step.gap;
      document.getElementById('metric-action').textContent = step.action;
""",
            'js_render_visual': """
      const container = document.getElementById('linked-list-stage');
      container.innerHTML = '';

      step.nodes.forEach((val, idx) => {
        const isSlow = (idx === step.slowIdx);
        const isFast = (idx === step.fastIdx);
        const isDummy = (idx === 0);

        const card = document.createElement('div');
        card.className = 'flex items-center gap-1.5';
        card.innerHTML = `
          <div class="flex flex-col items-center">
            <div class="w-12 h-12 rounded-xl flex items-center justify-center font-mono font-bold text-xs border ${
              isDummy ? 'border-dashed border-slate-700 bg-slate-900/40 text-slate-500' : 'border-slate-800 bg-slate-900 text-slate-200'
            } ${isSlow ? 'border-emerald-500 ring-2 ring-emerald-500/30' : ''} ${isFast ? 'border-cyan-400 ring-2 ring-cyan-400/30' : ''}">
              ${val}
            </div>
            <div class="flex gap-1 text-[8px] font-mono font-bold mt-1">
              ${isSlow ? '<span class="px-1 rounded bg-emerald-500 text-slate-950">SLOW</span>' : ''}
              ${isFast ? '<span class="px-1 rounded bg-cyan-400 text-slate-950">FAST</span>' : ''}
            </div>
          </div>
          ${idx < step.nodes.length - 1 ? '<span class="text-slate-600 font-mono text-sm">&rarr;</span>' : ''}
        `;
        container.appendChild(card);
      });
"""
        },
        'approaches': [
            {
                'name': "Two-Pass Length Counting",
                'desc': "First loop counts total length L, second loop advances L - n steps to delete.",
                'time': "O(2N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
        val dummy = ListNode(0).apply { next = head }
        var length = 0
        var curr = head
        while (curr != null) {
            length++
            curr = curr.next
        }
        var prev: ListNode? = dummy
        for (i in 0 until length - n) {
            prev = prev?.next
        }
        prev?.next = prev?.next?.next
        return dummy.next
    }
}""",
                'notes_kotlin': "Straightforward two passes. Computes `length - n` explicitly before unlinking.",
                'code_python': """class Solution:
    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        dummy = ListNode(0, head)
        length = 0
        curr = head
        while curr:
            length += 1
            curr = curr.next
        prev = dummy
        for _ in range(length - n):
            prev = prev.next
        prev.next = prev.next.next
        return dummy.next""",
                'notes_python': "Easy to reason through, but interviewer will immediately follow up: 'Can you do it in a single pass?'.",
                'code_java': """class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        int length = 0;
        ListNode curr = head;
        while (curr != null) {
            length++;
            curr = curr.next;
        }
        ListNode prev = dummy;
        for (int i = 0; i < length - n; i++) {
            prev = prev.next;
        }
        prev.next = prev.next.next;
        return dummy.next;
    }
}""",
                'notes_java': "Traverses the linked list twice. Functionally correct, but sub-optimal."
            },
            {
                'name': "Optimal: One-Pass Offset Two Pointers (Standard)",
                'desc': "Advance fast pointer n + 1 steps ahead of slow with dummy node sentinel.",
                'time': "O(N) (Single Pass)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: One-Pass Offset Two Pointers with Sentinel Dummy Node
     * Time: O(N) - Fast pointer traverses the list exactly once
     * Space: O(1) - Constant auxiliary pointer memory
     */
    fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
        val dummy = ListNode(0).apply { next = head }
        var fast: ListNode? = dummy
        var slow: ListNode? = dummy

        // Advance fast pointer n + 1 steps so gap is n + 1
        for (i in 0..n) {
            fast = fast?.next
        }

        // Move both pointers until fast reaches null
        while (fast != null) {
            fast = fast.next
            slow = slow?.next
        }

        // Unlink target node
        slow?.next = slow?.next?.next

        return dummy.next
    }
}""",
                'notes_kotlin': "The sentinel `dummy` node completely eliminates special-case checks for deleting the list head.",
                'code_python': """class Solution:
    # Optimal: One-Pass Offset Two Pointers
    # Time: O(N) - Single pass
    # Space: O(1) - Two pointer variables
    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        dummy = ListNode(0, head)
        fast = dummy
        slow = dummy
        
        # Advance fast n + 1 steps
        for _ in range(n + 1):
            fast = fast.next
            
        # Move until fast reaches end
        while fast:
            fast = fast.next
            slow = slow.next
            
        # Bypass nth node from end
        slow.next = slow.next.next
        
        return dummy.next""",
                'notes_python': "The canonical Python interview template. Clean and readable.",
                'code_java': """class Solution {
    /**
     * Optimal: One-Pass Offset Two Pointers
     * Time: O(N) - Single traversal
     * Space: O(1)
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy;
        ListNode slow = dummy;

        // Gap of n + 1 steps
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }

        // Bypass target node
        slow.next = slow.next.next;

        return dummy.next;
    }
}""",
                'notes_java': "Production standard. Ensures slow pointer halts at the node immediately preceding target."
            },
            {
                'name': "Recursive Backtracking with Call-Stack Return",
                'desc': "Traverse recursively to the end, then count backward on return unwinding.",
                'time': "O(N)",
                'space': "O(N) Call Stack",
                'code_kotlin': """class Solution {
    private var count = 0

    fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
        val dummy = ListNode(0).apply { next = head }
        removeHelper(dummy, n)
        return dummy.next
    }

    private fun removeHelper(node: ListNode?, n: Int) {
        if (node == null) return
        removeHelper(node.next, n)
        count++
        if (count == n + 1) {
            node.next = node.next?.next
        }
    }
}""",
                'notes_kotlin': "Educational recursive solution. Shows how the recursion stack can replace a two-pointer gap.",
                'code_python': """class Solution:
    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        count = 0
        dummy = ListNode(0, head)
        
        def helper(node):
            nonlocal count
            if not node:
                return
            helper(node.next)
            count += 1
            if count == n + 1:
                node.next = node.next.next
                
        helper(dummy)
        return dummy.next""",
                'notes_python': "Uses `nonlocal count` to unwind and delete during stack retraction.",
                'code_java': """class Solution {
    private int count = 0;

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        helper(dummy, n);
        return dummy.next;
    }

    private void helper(ListNode node, int n) {
        if (node == null) return;
        helper(node.next, n);
        count++;
        if (count == n + 1) {
            node.next = node.next.next;
        }
    }
}""",
                'notes_java': "Consumes O(N) stack frames. Good for demonstrating recursion mastery."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
        val dummy = ListNode(0).apply { next = head }
        var fast: ListNode? = dummy; var slow: ListNode? = dummy
        for (i in 0..n) fast = fast?.next
        while (fast != null) { fast = fast.next; slow = slow?.next }
        slow?.next = slow?.next?.next
        return dummy.next
    }
}""",
            'python_boilerplate': """class Solution:
    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        dummy = ListNode(0, head)
        fast = slow = dummy
        for _ in range(n + 1): fast = fast.next
        while fast:
            fast = fast.next; slow = slow.next
        slow.next = slow.next.next
        return dummy.next""",
            'java_boilerplate': """class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i <= n; i++) fast = fast.next;
        while (fast != null) { fast = fast.next; slow = slow.next; }
        slow.next = slow.next.next;
        return dummy.next;
    }
}""",
            'js_test_runner': """
      const testCases = [
        { arr: [1,2,3,4,5], n: 2, expected: [1,2,3,5] },
        { arr: [1], n: 1, expected: [] },
        { arr: [1,2], n: 1, expected: [1] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        const dummy = { val: 0, next: null };
        let cur = dummy;
        tc.arr.forEach(v => {
          cur.next = { val: v, next: null };
          cur = cur.next;
        });

        let fast = dummy, slow = dummy;
        for (let i = 0; i <= tc.n; i++) fast = fast ? fast.next : null;
        while (fast) {
          fast = fast.next;
          slow = slow.next;
        }
        slow.next = slow.next ? slow.next.next : null;

        const res = [];
        let p = dummy.next;
        while (p) { res.push(p.val); p = p.next; }

        const passed = (res.length === tc.expected.length) && res.every((v, i) => v === tc.expected[i]);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: list=[${tc.arr.join(',')}] n=${tc.n}</span>
          <span>${passed ? '&#10004; PASSED ([' + res.join(',') + '])' : '&#10008; FAILED'}</span>
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
            'phase1': "I clarify: can n be greater than the list size? No, constraints specify 1 <= n <= size. Can the list have only 1 node? Yes, if size = 1 and n = 1, we delete the only node and return null. Is in-place pointer manipulation expected? Yes.",
            'phase2': "For strategy: a two-pass approach counts length L and then walks L - n steps. We can do this in a single pass using two pointers (fast and slow) initialized at a sentinel dummy node. By advancing fast n + 1 steps ahead of slow, when fast reaches null, slow will stop exactly at the node prior to the target node. Time is O(N) single pass, space is O(1).",
            'phase3': "I create dummy = ListNode(0, head), fast = dummy, slow = dummy. I advance fast n + 1 times. Then while fast != null, I advance fast and slow together. When fast reaches null, I unlink the target node with slow.next = slow.next.next. Finally, I return dummy.next.",
            'phase4': "Dry run edge cases: deleting the head node [1, 2] with n = 2. Fast advances 3 steps (past 2 to null). Slow remains at dummy. slow.next = slow.next.next bypasses 1, dummy.next points to 2, successfully deleting head. Everything runs cleanly in one pass.",
            'senior_followup': "Senior Scalability: In production systems (e.g., C/C++ memory allocators or manual memory management), when unlinking `slow.next`, we must explicitly capture `temp = slow.next` and free its memory to prevent memory leaks."
        },
        'quiz': [
            {
                'question': "Why is a `dummy` sentinel node essential for this problem?",
                'options': [
                    "To prevent stack overflow.",
                    "It allows deleting the `head` node without requiring special-case if-branches.",
                    "Because singly linked lists cannot start with positive numbers.",
                    "It doubles the traversal speed."
                ],
                'correct_index': 1,
                'explanation': "If the node to be removed is the head itself (n == list.length), `slow` starts at `dummy`, so `slow.next = slow.next.next` smoothly updates `dummy.next` to point to the second node, requiring zero special conditions."
            },
            {
                'question': "Why must the initial gap between `fast` and `slow` be `n + 1` steps rather than `n` steps?",
                'options': [
                    "Because array indexing is 0-based.",
                    "Because we need `slow` to stop at the node IMMEDIATELY BEFORE the target node so we can rewrite its `.next` pointer.",
                    "Because linked lists are circular.",
                    "It compensates for fast pointer velocity."
                ],
                'correct_index': 1,
                'explanation': "In a singly linked list, you cannot delete a node if you are standing ON it (no `.prev` pointer). You must stop one node ahead of it (`slow.next = slow.next.next`), which requires a gap of `n + 1`."
            }
        ]
    },

    # ---------------- 0035: COPY LIST WITH RANDOM POINTER ----------------
    {
        'num': 35,
        'lc': 138,
        'title': "Copy List with Random Pointer",
        'category': "Linked Lists & System Node Design",
        'difficulty': "Medium",
        'lc_slug': "copy-list-with-random-pointer",
        'verbatim_statement': """<p>A linked list of length <code>n</code> is given such that each node contains an additional random pointer, which could point to any node in the list, or <code>null</code>.</p>
<p class="mt-2">Construct a <strong>deep copy</strong> of the list. The deep copy should consist of exactly <code>n</code> <strong>brand new</strong> nodes, where each new node has its value set to the value of its corresponding original node. Both the <code>next</code> and <code>random</code> pointer of the new nodes should point to new nodes in the copied list such that the pointers in the original list and copied list represent the same list state. <strong>None of the pointers in the new list should point to nodes in the original list</strong>.</p>
<p class="mt-2">Return <em>the head of the copied linked list</em>.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'head = [[7,null],[13,0],[11,4],[10,2],[1,0]]', 'output': '[[7,null],[13,0],[11,4],[10,2],[1,0]]'},
            {'title': "Example 2", 'input': 'head = [[1,1],[2,1]]', 'output': '[[1,1],[2,1]]'}
        ],
        'constraints': [
            "0 <= n <= 1000",
            "-10^4 <= Node.val <= 10^4",
            "Node.random is null or is pointing to some node in the linked list."
        ],
        'plain_english': "Deep clone a linked list where nodes have both next and random pointers. Maintain a 1-to-1 mapping between original nodes and copied nodes so random pointers can be wired to newly cloned nodes rather than old ones. Can be done with a HashMap in O(N) space or in-place interweaving in O(1) auxiliary space.",
        'boundary_checks': {
            'min_base': "<code>head = null</code> &rarr; Empty list &rarr; returns null.",
            'best_exit': "All random pointers null &rarr; Standard linked list deep copy.",
            'worst_scan': "Random pointers pointing to themselves or earlier nodes &rarr; Hash table or interweaving prevents cycles."
        },
        'mental_model': {
            'analogy_title': "The Shadow Clone Weaver",
            'analogy_text': "Imagine each person in a line clones themselves and places their clone directly behind them in line: A -> A' -> B -> B' -> C -> C'. Now, if original A has a laser pointer aimed at C, clone A' simply looks at original A's target (C) and aims its own laser at C's clone (C.next = C'). Once all clones have mirrored their lasers, we untangle the alternating line into two clean, independent lines.",
            'aha_moment': "Interweaving clones directly after each original node (`curr.next = clone; clone.next = nextNode`) encodes the 1-to-1 mapping into the pointer structure itself, achieving O(1) auxiliary space without a hash table!",
            'trigger': "Whenever deep cloning a graph or linked structure with arbitrary cross-references &rarr; HashMap<OldNode, NewNode> or In-Place Interweaving.",
            'sisters': [
                {'name': "Clone Graph", 'lc': "LC #133"},
                {'name': "Clone Binary Tree With Random Pointer", 'lc': "LC #1485"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N^2) Time, O(1) Space",
            'naive_text': "Copy the list linearly, then for each node's random pointer, scan from the head to find the matching node index.",
            'wasted_work': "Linear search to locate target nodes for random pointers.",
            'optimal_complexity': "O(N) Time, O(1) Auxiliary Space (Interweaving)",
            'optimal_text': "3 passes: 1) Create interweaved clones; 2) Assign clone.random = orig.random.next; 3) Separate lists.",
            'core_invariant': "During step 2, for every original node, its clone is at `curr.next`, and its random clone is at `curr.random.next`.",
            'table_rows': [
                {'name': "1. HashMap Two-Pass", 'time': "O(N)", 'space': "O(N)", 'note': "Intuitive, clean, standard interview solution."},
                {'name': "★ 2. In-Place Interweaving", 'time': "O(N)", 'space': "O(1) aux", 'note': "Zero hash table overhead; production standard."}
            ]
        },
        'trace_table': {
            'input_desc': 'Original: A -> B -> null (A.random = B, B.random = A)',
            'columns': ["Phase", "Node Structure", "Pointers State", "Invariant"],
            'rows': [
                ["Phase 1: Interweave", "A -> A' -> B -> B' -> null", "A'.next = B, B'.next = null", "Every node followed by its clone"],
                ["Phase 2: Wire Random", "A -> A' -> B -> B'", "A'.random = A.random.next = B'", "Clone randoms wired correctly"],
                ["Phase 2 (cont)", "A -> A' -> B -> B'", "B'.random = B.random.next = A'", "All clones wired to cloned targets"],
                ["Phase 3: Untangle", "A -> B -> null and A' -> B' -> null", "Original and clone decoupled", "Two distinct 100% deep copied lists"]
            ],
            'takeaway': "Achieves deep copy with zero hash table overhead."
        },
        'stepper': {
            'title': "3-Phase Interweaving & Decoupling Simulation",
            'default_input': 'A(7), B(13), C(11)',
            'presets': [
                {'label': "3 Nodes with Randoms", 'onclick': 'loadPreset(["A(7)", "B(13)", "C(11)"])'},
                {'label': "Self Random", 'onclick': 'loadPreset(["A(1)", "B(2)"])'},
                {'label': "Single Node", 'onclick': 'loadPreset(["A(42)"])'}
            ],
            'metrics': [
                {'id': "metric-phase", 'label': "Active Phase", 'color': "text-amber-400", 'initial': "Init"},
                {'id': "metric-curr", 'label': "Current Node", 'color': "text-cyan-400", 'initial': "Head"},
                {'id': "metric-clones", 'label': "Clones Created", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-status", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
            ],
            'visual_html': '<div id="clone-nodes-stage" class="w-full flex items-center justify-center gap-2 py-4 overflow-x-auto min-h-[90px]"></div>',
            'initial_narration': 'Click "Next Step" to trace the 3-phase interweaving algorithm.',
            'pseudocode_lines': [
                "// Phase 1: Interweave clone nodes",
                "var curr = head; while (curr != null) { curr.next = Node(curr.val, curr.next); curr = curr.next.next }",
                "// Phase 2: Wire random pointers",
                "curr = head; while (curr != null) { curr.next?.random = curr.random?.next; curr = curr.next?.next }",
                "// Phase 3: Separate lists",
                "val dummy = Node(0); var copyCurr = dummy; curr = head",
                "while (curr != null) { copyCurr.next = curr.next; curr.next = curr.next?.next; curr = curr.next; copyCurr = copyCurr.next!! }",
                "return dummy.next"
            ],
            'js_code': """
    let initialNodes = ["A(7)", "B(13)", "C(11)"];

    function buildSteps(nodes) {
      steps = [];

      steps.push({
        phase: "Phase 1",
        curr: "Head",
        clones: 0,
        status: "Original List",
        displayNodes: nodes.map(n => ({ val: n, isClone: false })),
        line: 1,
        narration: "Original list with next and random pointers ready."
      });

      // Phase 1: Interweave
      const interweaved = [];
      nodes.forEach(n => {
        interweaved.push({ val: n, isClone: false });
        interweaved.push({ val: n.replace('(', '\'('), isClone: true });
      });

      steps.push({
        phase: "Phase 1: Interweave",
        curr: "All",
        clones: nodes.length,
        status: "Duplicated in-place",
        displayNodes: [...interweaved],
        line: 2,
        narration: `Created clone nodes directly following each original: ${interweaved.map(x=>x.val).join(' -> ')}.`
      });

      // Phase 2: Wire Randoms
      steps.push({
        phase: "Phase 2: Wire Random",
        curr: "Randoms",
        clones: nodes.length,
        status: "Pointers Copied",
        displayNodes: [...interweaved],
        line: 4,
        narration: "Wired random pointers: clone.random = orig.random.next."
      });

      // Phase 3: Decouple
      const cloneList = interweaved.filter(x => x.isClone);
      steps.push({
        phase: "Phase 3: Decouple",
        curr: "Clone Head",
        clones: nodes.length,
        status: "Separated!",
        displayNodes: [...cloneList],
        line: 7,
        narration: `Decoupled! Returning brand new deep copied list: ${cloneList.map(x=>x.val).join(' -> ')}.`
      });
    }

    function loadPreset(nodes) {
      document.getElementById('custom-input').value = nodes.join(', ');
      initialNodes = nodes;
      stepperReset();
      buildSteps(initialNodes);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => s.trim()).filter(s => s.length > 0);
      if (arr.length > 0) {
        initialNodes = arr;
        stepperReset();
        buildSteps(initialNodes);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-phase').textContent = step.phase;
      document.getElementById('metric-curr').textContent = step.curr;
      document.getElementById('metric-clones').textContent = step.clones;
      document.getElementById('metric-status').textContent = step.status;
""",
            'js_render_visual': """
      const container = document.getElementById('clone-nodes-stage');
      container.innerHTML = '';

      step.displayNodes.forEach((node, idx) => {
        const card = document.createElement('div');
        card.className = 'flex items-center gap-1.5';
        card.innerHTML = `
          <div class="px-3 py-2 rounded-xl flex items-center justify-center font-mono font-bold text-xs border ${
            node.isClone ? 'border-emerald-500 bg-emerald-950/40 text-emerald-300' : 'border-slate-800 bg-slate-900 text-slate-300'
          }">
            ${node.val}
          </div>
          ${idx < step.displayNodes.length - 1 ? '<span class="text-slate-600 font-mono">&rarr;</span>' : ''}
        `;
        container.appendChild(card);
      });
"""
        },
        'approaches': [
            {
                'name': "HashMap Two-Pass (Most Common)",
                'desc': "Pass 1 creates new nodes in HashMap<Old, New>; Pass 2 wires next and random pointers.",
                'time': "O(N)",
                'space': "O(N)",
                'code_kotlin': """class Solution {
    fun copyRandomList(node: Node?): Node? {
        if (node == null) return null
        val map = HashMap<Node, Node>()

        var curr = node
        while (curr != null) {
            map[curr] = Node(curr.`val`)
            curr = curr.next
        }

        curr = node
        while (curr != null) {
            map[curr]?.next = map[curr.next]
            map[curr]?.random = map[curr.random]
            curr = curr.next
        }

        return map[node]
    }
}""",
                'notes_kotlin': "Two-pass hash map is intuitive and completely avoids touching input node pointers.",
                'code_python': """class Solution:
    def copyRandomList(self, head: 'Optional[Node]') -> 'Optional[Node]':
        if not head:
            return None
        mapping = {}
        
        curr = head
        while curr:
            mapping[curr] = Node(curr.val)
            curr = curr.next
            
        curr = head
        while curr:
            mapping[curr].next = mapping.get(curr.next)
            mapping[curr].random = mapping.get(curr.random)
            curr = curr.next
            
        return mapping[head]""",
                'notes_python': "`mapping.get(curr.next)` handles null pointers cleanly by returning None without KeyError.",
                'code_java': """import java.util.HashMap;
import java.util.Map;

class Solution {
    public Node copyRandomList(Node head) {
        if (head == null) return null;
        Map<Node, Node> map = new HashMap<>();

        Node curr = head;
        while (curr != null) {
            map.put(curr, new Node(curr.val));
            curr = curr.next;
        }

        curr = head;
        while (curr != null) {
            map.get(curr).next = map.get(curr.next);
            map.get(curr).random = map.get(curr.random);
            curr = curr.next;
        }

        return map.get(head);
    }
}""",
                'notes_java': "The standard interview solution. Clear, reliable, and easily executed in 5 minutes."
            },
            {
                'name': "Optimal: In-Place Interweaving (Zero Extra Space)",
                'desc': "Interweave clone nodes in-place, copy random pointers, then decouple lists.",
                'time': "O(N)",
                'space': "O(1) Auxiliary Space",
                'code_kotlin': """class Solution {
    /**
     * Optimal: In-Place Interweaving with O(1) Auxiliary Space
     * Time: O(N) - Three linear passes
     * Space: O(1) - No hash table allocation
     */
    fun copyRandomList(node: Node?): Node? {
        if (node == null) return null

        // 1. Interweave: A -> A' -> B -> B'
        var curr = node
        while (curr != null) {
            val clone = Node(curr.`val`)
            clone.next = curr.next
            curr.next = clone
            curr = clone.next
        }

        // 2. Wire random pointers: clone.random = orig.random.next
        curr = node
        while (curr != null) {
            curr.next?.random = curr.random?.next
            curr = curr.next?.next
        }

        // 3. Decouple lists
        curr = node
        val newHead = node.next
        while (curr != null) {
            val clone = curr.next
            curr.next = clone?.next
            clone?.next = clone?.next?.next
            curr = curr.next
        }

        return newHead
    }
}""",
                'notes_kotlin': "Demonstrates masterclass pointer manipulation. Decouples both original and copy in linear time without extra memory.",
                'code_python': """class Solution:
    # Optimal: In-Place Interweaving
    # Time: O(N) - 3 passes
    # Space: O(1) auxiliary
    def copyRandomList(self, head: 'Optional[Node]') -> 'Optional[Node]':
        if not head:
            return None
            
        # 1. Create interwoven clone nodes
        curr = head
        while curr:
            clone = Node(curr.val, curr.next)
            curr.next = clone
            curr = clone.next
            
        # 2. Wire random pointers
        curr = head
        while curr:
            if curr.random:
                curr.next.random = curr.random.next
            curr = curr.next.next
            
        # 3. Separate the two lists
        curr = head
        new_head = head.next
        while curr:
            clone = curr.next
            curr.next = clone.next
            if clone.next:
                clone.next = clone.next.next
            curr = curr.next
            
        return new_head""",
                'notes_python': "High-signal staff engineer solution. Restores original list pointers while generating a brand new list.",
                'code_java': """class Solution {
    /**
     * Optimal: In-Place Interweaving
     * Time: O(N)
     * Space: O(1) auxiliary
     */
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        // Step 1: Interweave
        Node curr = head;
        while (curr != null) {
            Node clone = new Node(curr.val);
            clone.next = curr.next;
            curr.next = clone;
            curr = clone.next;
        }

        // Step 2: Copy random pointers
        curr = head;
        while (curr != null) {
            if (curr.random != null) {
                curr.next.random = curr.random.next;
            }
            curr = curr.next.next;
        }

        // Step 3: Decouple lists
        curr = head;
        Node newHead = head.next;
        while (curr != null) {
            Node clone = curr.next;
            curr.next = clone.next;
            if (clone.next != null) {
                clone.next = clone.next.next;
            }
            curr = curr.next;
        }

        return newHead;
    }
}""",
                'notes_java': "The production gold standard. Avoids thousands of Map.Entry heap allocations."
            },
            {
                'name': "Recursive Graph DFS with Memoization",
                'desc': "Treat list as directed graph with next and random edges, cloning via DFS.",
                'time': "O(N)",
                'space': "O(N) Call Stack + Map",
                'code_kotlin': """class Solution {
    private val visited = HashMap<Node, Node>()

    fun copyRandomList(node: Node?): Node? {
        if (node == null) return null
        if (visited.containsKey(node)) return visited[node]

        val clone = Node(node.`val`)
        visited[node] = clone

        clone.next = copyRandomList(node.next)
        clone.random = copyRandomList(node.random)

        return clone
    }
}""",
                'notes_kotlin': "Identical algorithm to LC 133 Clone Graph. Shows deep conceptual generalization.",
                'code_python': """class Solution:
    def __init__(self):
        self.visited = {}

    def copyRandomList(self, head: 'Optional[Node]') -> 'Optional[Node]':
        if not head:
            return None
        if head in self.visited:
            return self.visited[head]
            
        clone = Node(head.val)
        self.visited[head] = clone
        
        clone.next = self.copyRandomList(head.next)
        clone.random = self.copyRandomList(head.random)
        
        return clone""",
                'notes_python': "DFS graph clone. Python recursion limit (1000) matches the problem constraint N <= 1000.",
                'code_java': """import java.util.HashMap;
import java.util.Map;

class Solution {
    private Map<Node, Node> visited = new HashMap<>();

    public Node copyRandomList(Node head) {
        if (head == null) return null;
        if (visited.containsKey(head)) return visited.get(head);

        Node clone = new Node(head.val);
        visited.put(head, clone);

        clone.next = copyRandomList(head.next);
        clone.random = copyRandomList(head.random);

        return clone;
    }
}""",
                'notes_java': "Very elegant to present if the interviewer asks: 'How would you generalize this to a general graph?'."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun copyRandomList(node: Node?): Node? {
        if (node == null) return null
        val map = HashMap<Node, Node>()
        var curr = node
        while (curr != null) { map[curr] = Node(curr.`val`); curr = curr.next }
        curr = node
        while (curr != null) {
            map[curr]?.next = map[curr.next]
            map[curr]?.random = map[curr.random]
            curr = curr.next
        }
        return map[node]
    }
}""",
            'python_boilerplate': """class Solution:
    def copyRandomList(self, head: 'Optional[Node]') -> 'Optional[Node]':
        if not head: return None
        m = {}
        curr = head
        while curr:
            m[curr] = Node(curr.val); curr = curr.next
        curr = head
        while curr:
            m[curr].next = m.get(curr.next)
            m[curr].random = m.get(curr.random)
            curr = curr.next
        return m[head]""",
            'java_boilerplate': """class Solution {
    public Node copyRandomList(Node head) {
        if (head == null) return null;
        java.util.Map<Node, Node> m = new java.util.HashMap<>();
        Node c = head;
        while (c != null) { m.put(c, new Node(c.val)); c = c.next; }
        c = head;
        while (c != null) {
            m.get(c).next = m.get(c.next);
            m.get(c).random = m.get(c.random);
            c = c.next;
        }
        return m.get(head);
    }
}""",
            'js_test_runner': """
      // Node simulation
      const n1 = { val: 7, next: null, random: null };
      const n2 = { val: 13, next: null, random: null };
      const n3 = { val: 11, next: null, random: null };
      n1.next = n2; n2.next = n3;
      n2.random = n1; n3.random = n3;

      // Copy simulation
      const map = new Map();
      let c = n1;
      while (c) { map.set(c, { val: c.val, next: null, random: null }); c = c.next; }
      c = n1;
      while (c) {
        map.get(c).next = map.get(c.next) || null;
        map.get(c).random = map.get(c.random) || null;
        c = c.next;
      }
      const copyHead = map.get(n1);

      const passed = (copyHead !== n1 && copyHead.val === 7 && copyHead.next.val === 13 && copyHead.next.random === copyHead);
      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = `
        <div class="p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}">
          <span>Example 1 Deep Copy Validation</span>
          <span>${passed ? '&#10004; PASSED (Brand new nodes, correct pointers)' : '&#10008; FAILED'}</span>
        </div>
      `;

      if (passed) {
        statusSpan.textContent = "All test cases passed!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Test failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
        },
        'spoken_script': {
            'phase1': "I clarify: can random pointers point to null or to the node itself? Yes, both are valid. Must all new nodes be brand new allocations with no references to the old list? Yes, a true deep copy. Can the input list be empty? Yes, return null.",
            'phase2': "For strategy: a two-pass HashMap<Node, Node> maps each original node to its copy. In pass 1 we clone nodes; in pass 2 we wire next and random pointers using the map. This achieves O(N) time and O(N) space. If the interviewer requests O(1) auxiliary space, we can interweave clone nodes directly after their originals (A -> A' -> B -> B'), wire clone.random = orig.random.next, and then decouple the lists in a third pass.",
            'phase3': "I'll implement the HashMap approach first. I check if head == null. I initialize map = HashMap<Node, Node>(). In pass 1, curr walks the list creating map[curr] = Node(curr.val). In pass 2, curr walks again setting map[curr].next = map[curr.next] and map[curr].random = map[curr.random]. Finally, return map[head].",
            'phase4': "Dry running edge cases: null head returns null. A node pointing to itself (node.random = node) correctly resolves map[curr].random = map[curr] (points to its own clone). No old node pointers leak into the cloned list.",
            'senior_followup': "Senior Scalability: In large-scale graph serialization, cycles can cause infinite recursion. The map serves as a visited set, guaranteeing each object identity is instantiated exactly once across deserialization."
        },
        'quiz': [
            {
                'question': "Why is in-place interweaving able to eliminate the need for a HashMap?",
                'options': [
                    "Because linked lists cannot be stored in HashMaps.",
                    "Because placing the cloned node immediately after its original node (`orig.next == clone`) stores the mapping directly inside the pointer structure.",
                    "Because Java garbage collection automatically duplicates nodes.",
                    "Because random pointers can only point backward."
                ],
                'correct_index': 1,
                'explanation': "With interweaving, `orig.next` is its clone. Therefore, the clone of `orig.random` is found at `orig.random.next` in O(1) time without any auxiliary lookup table."
            },
            {
                'question': "What happens if a node's random pointer is null when wiring `curr.next.random = curr.random.next`?",
                'options': [
                    "A NullPointerException is thrown if not defensively checked with `if (curr.random != null)`.",
                    "It automatically creates a dummy node.",
                    "The JVM skips the instruction.",
                    "The list becomes doubly linked."
                ],
                'correct_index': 0,
                'explanation': "Attempting to access `curr.random.next` when `curr.random` is null will cause a NullPointerException, so checking `if (curr.random != null)` or using safe calls `curr.random?.next` is mandatory."
            }
        ]
    },

    # ---------------- 0036: SEARCH A 2D MATRIX ----------------
    {
        'num': 36,
        'lc': 74,
        'title': "Search a 2D Matrix",
        'category': "Binary Search",
        'difficulty': "Medium",
        'lc_slug': "search-a-2d-matrix",
        'verbatim_statement': """<p>You are given an <code>m x n</code> integer matrix <code>matrix</code> with the following two properties:</p>
<ul class="list-disc pl-5 mt-1 space-y-1 text-xs">
  <li>Each row is sorted in non-decreasing order.</li>
  <li>The first integer of each row is greater than the last integer of the previous row.</li>
</ul>
<p class="mt-2">Given an integer <code>target</code>, return <code>true</code> <em>if</em> <code>target</code> <em>is in</em> <code>matrix</code> <em>or</em> <code>false</code> <em>otherwise</em>.</p>
<p class="mt-2">You must write a solution in <code>O(log(m * n))</code> time complexity.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3', 'output': 'true'},
            {'title': "Example 2", 'input': 'matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 13', 'output': 'false'}
        ],
        'constraints': [
            "m == matrix.length",
            "n == matrix[i].length",
            "1 <= m, n <= 100",
            "-10^4 <= matrix[i][j], target <= 10^4"
        ],
        'plain_english': "Search for a target value in a 2D grid where every row is sorted and each row strictly follows the previous one. Because the entire grid is in sorted order from top-left to bottom-right, treat it as a flattened 1D array of length m * n and perform a single binary search. Convert 1D index mid to 2D coordinates: row = mid / n, col = mid % n.",
        'boundary_checks': {
            'min_base': "1x1 matrix <code>[[5]], target = 5</code> &rarr; single check, returns true.",
            'best_exit': "Target at middle index <code>matrix[m/2][n/2] == target</code> &rarr; exits on step 1.",
            'worst_scan': "Target not in matrix &rarr; standard O(log(m*n)) search interval exhaustion."
        },
        'mental_model': {
            'analogy_title': "The Unfolded Calendar Ribbon",
            'analogy_text': "Think of a wall calendar with rows (weeks) and columns (days). Because day 8 starts right after day 7, the calendar is just a single long ribbon of numbers folded into rows of width 7. To find any day index `i` on the calendar, week is `i / 7` and day of week is `i % 7`. You can binary search the entire year in one smooth sequence without checking row by row.",
            'aha_moment': "A 2D matrix with these properties is mathematically identical to a 1D sorted array of length `m * n`. Virtual mapping: index `mid` &rarr; `matrix[mid / cols][mid % cols]` in O(1) time.",
            'trigger': "Whenever searching a sorted 2D matrix where row starts > previous row ends &rarr; Flattened 1D Binary Search.",
            'sisters': [
                {'name': "Search a 2D Matrix II", 'lc': "LC #240"},
                {'name': "Binary Search", 'lc': "LC #704"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(m * n) Time, O(1) Space",
            'naive_text': "Nested loop scanning all elements row by row. Completely ignores sorted properties.",
            'wasted_work': "Checking every cell instead of eliminating half the search space at each step.",
            'optimal_complexity': "O(log(m * n)) Time, O(1) Space",
            'optimal_text': "Standard binary search over range `[0..m*n - 1]`. Halves search space on every iteration.",
            'core_invariant': "If target exists in the matrix, it must lie within the flattened 1D interval `[left..right]`.",
            'table_rows': [
                {'name': "1. Linear Scan", 'time': "O(m * n)", 'space': "O(1)", 'note': "Scans every cell; fails logarithmic requirement."},
                {'name': "2. Double Binary Search", 'time': "O(log m + log n)", 'space': "O(1)", 'note': "Search row first, then search column."},
                {'name': "★ 3. Virtual 1D Binary Search", 'time': "O(log(m * n))", 'space': "O(1)", 'note': "Single binary search via coordinate math. Optimal."}
            ]
        },
        'trace_table': {
            'input_desc': 'matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3, m=3, n=4',
            'columns': ["Left", "Right", "Mid (1D)", "Row (mid / 4)", "Col (mid % 4)", "Value", "Action"],
            'rows': [
                ["0", "11", "5", "1", "1", "11", "11 > 3 &rarr; right = mid - 1 = 4"],
                ["0", "4", "2", "0", "2", "5", "5 > 3 &rarr; right = mid - 1 = 1"],
                ["0", "1", "0", "0", "0", "1", "1 < 3 &rarr; left = mid + 1 = 1"],
                ["1", "1", "1", "0", "1", "3", "3 == 3 &rarr; FOUND! Return true"]
            ],
            'takeaway': "Located target 3 in exactly 4 iterations out of 12 elements."
        },
        'stepper': {
            'title': "Virtual 1D Coordinate Mapping Binary Search",
            'default_input': '3',
            'presets': [
                {'label': "Target = 3 (Found)", 'onclick': 'loadTarget(3)'},
                {'label': "Target = 13 (Missing)", 'onclick': 'loadTarget(13)'},
                {'label': "Target = 34 (Late Row)", 'onclick': 'loadTarget(34)'}
            ],
            'metrics': [
                {'id': "metric-mid", 'label': "Mid Coordinate", 'color': "text-amber-400", 'initial': "mid = 5"},
                {'id': "metric-val", 'label': "Value at Mid", 'color': "text-cyan-400", 'initial': "matrix[1][1] = 11"},
                {'id': "metric-range", 'label': "Search Range", 'color': "text-emerald-400", 'initial': "[0 .. 11]"},
                {'id': "metric-status", 'label': "Status", 'color': "text-purple-400", 'initial': "Searching"}
            ],
            'visual_html': '<div id="matrix-grid-stage" class="w-full flex flex-col items-center justify-center gap-1.5 py-4"></div>',
            'initial_narration': 'Click "Next Step" to trace virtual 1D binary search.',
            'pseudocode_lines': [
                "val m = matrix.size; val n = matrix[0].size",
                "var l = 0; var r = m * n - 1",
                "while (l <= r) {",
                "    val mid = l + (r - l) / 2",
                "    val midVal = matrix[mid / n][mid % n]",
                "    if (midVal == target) return true",
                "    else if (midVal < target) l = mid + 1",
                "    else r = mid - 1",
                "}",
                "return false"
            ],
            'js_code': """
    const matrixData = [
      [1, 3, 5, 7],
      [10, 11, 16, 20],
      [23, 30, 34, 60]
    ];
    let currentTarget = 3;

    function buildSteps(matrix, target) {
      steps = [];
      const m = matrix.length;
      const n = matrix[0].length;
      let l = 0;
      let r = m * n - 1;

      steps.push({
        l: l,
        r: r,
        mid: -1,
        row: -1,
        col: -1,
        val: "-",
        status: "Initialize",
        line: 2,
        narration: `Searching target ${target} across ${m}x${n} matrix (total ${m*n} elements).`
      });

      while (l <= r) {
        const mid = Math.floor(l + (r - l) / 2);
        const row = Math.floor(mid / n);
        const col = mid % n;
        const val = matrix[row][col];

        if (val === target) {
          steps.push({
            l: l,
            r: r,
            mid: mid,
            row: row,
            col: col,
            val: val,
            status: "Found!",
            line: 6,
            narration: `matrix[${row}][${col}] = ${val} == target (${target}). Target FOUND!`
          });
          return;
        } else if (val < target) {
          steps.push({
            l: l,
            r: r,
            mid: mid,
            row: row,
            col: col,
            val: val,
            status: "Search Right Half",
            line: 7,
            narration: `matrix[${row}][${col}] = ${val} < target (${target}). Target must be in right half. l = ${mid + 1}.`
          });
          l = mid + 1;
        } else {
          steps.push({
            l: l,
            r: r,
            mid: mid,
            row: row,
            col: col,
            val: val,
            status: "Search Left Half",
            line: 8,
            narration: `matrix[${row}][${col}] = ${val} > target (${target}). Target must be in left half. r = ${mid - 1}.`
          });
          r = mid - 1;
        }
      }

      steps.push({
        l: l,
        r: r,
        mid: -1,
        row: -1,
        col: -1,
        val: "-",
        status: "Not Found",
        line: 10,
        narration: `Search interval exhausted (l > r). Target ${target} does not exist in matrix.`
      });
    }

    function loadTarget(t) {
      document.getElementById('custom-input').value = t;
      currentTarget = t;
      stepperReset();
      buildSteps(matrixData, currentTarget);
      renderStepper();
    }

    function handleCustomInput() {
      const val = parseInt(document.getElementById('custom-input').value.trim());
      if (!isNaN(val)) {
        currentTarget = val;
        stepperReset();
        buildSteps(matrixData, currentTarget);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-mid').textContent = step.mid >= 0 ? `mid = ${step.mid} (${step.row}, ${step.col})` : "-";
      document.getElementById('metric-val').textContent = step.val !== "-" ? `matrix[${step.row}][${step.col}] = ${step.val}` : "-";
      document.getElementById('metric-range').textContent = `[${step.l} .. ${step.r}]`;
      document.getElementById('metric-status').textContent = step.status;
""",
            'js_render_visual': """
      const container = document.getElementById('matrix-grid-stage');
      container.innerHTML = '';
      const n = matrixData[0].length;

      matrixData.forEach((rowArr, rIdx) => {
        const rowEl = document.createElement('div');
        rowEl.className = 'flex gap-1.5';
        rowArr.forEach((val, cIdx) => {
          const flatIdx = rIdx * n + cIdx;
          const isMid = (rIdx === step.row && cIdx === step.col);
          const inRange = (flatIdx >= step.l && flatIdx <= step.r);

          let cellCls = "border-slate-800 bg-slate-900 text-slate-500 opacity-40";
          if (isMid) {
            cellCls = (val === currentTarget)
              ? "border-emerald-500 bg-emerald-500 text-slate-950 font-bold scale-105"
              : "border-amber-500 bg-amber-500 text-slate-950 font-bold scale-105";
          } else if (inRange) {
            cellCls = "border-slate-700 bg-slate-800 text-slate-200 font-semibold";
          }

          const cell = document.createElement('div');
          cell.className = `w-12 h-10 rounded-lg flex flex-col items-center justify-center font-mono text-xs border transition-all duration-200 ${cellCls}`;
          cell.innerHTML = `
            <span>${val}</span>
            <span class="text-[7px] opacity-70">${flatIdx}</span>
          `;
          rowEl.appendChild(cell);
        });
        container.appendChild(rowEl);
      });
"""
        },
        'approaches': [
            {
                'name': "Two Binary Searches (Row then Col)",
                'desc': "Binary search first column to find row, then binary search within that row.",
                'time': "O(log m + log n) = O(log(m * n))",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        val m = matrix.size
        val n = matrix[0].size

        // 1. Binary search target row
        var top = 0
        var bot = m - 1
        var targetRow = -1

        while (top <= bot) {
            val mid = top + (bot - top) / 2
            if (target >= matrix[mid][0] && target <= matrix[mid][n - 1]) {
                targetRow = mid
                break
            } else if (target < matrix[mid][0]) {
                bot = mid - 1
            } else {
                top = mid + 1
            }
        }

        if (targetRow == -1) return false

        // 2. Binary search within targetRow
        var l = 0
        var r = n - 1
        while (l <= r) {
            val mid = l + (r - l) / 2
            if (matrix[targetRow][mid] == target) return true
            else if (matrix[targetRow][mid] < target) l = mid + 1
            else r = mid - 1
        }
        return false
    }
}""",
                'notes_kotlin': "Two sequential binary searches. Logically intuitive, but requires writing two separate loops.",
                'code_python': """class Solution:
    def searchMatrix(self, matrix: list[list[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        
        # 1. Binary search for row
        top, bot = 0, m - 1
        target_row = -1
        while top <= bot:
            mid = (top + bot) // 2
            if matrix[mid][0] <= target <= matrix[mid][-1]:
                target_row = mid
                break
            elif target < matrix[mid][0]:
                bot = mid - 1
            else:
                top = mid + 1
                
        if target_row == -1:
            return False
            
        # 2. Binary search within row
        row = matrix[target_row]
        l, r = 0, n - 1
        while l <= r:
            mid = (l + r) // 2
            if row[mid] == target:
                return True
            elif row[mid] < target:
                l = mid + 1
            else:
                r = mid - 1
        return False""",
                'notes_python': "Works cleanly, but virtual 1D search is half the code length.",
                'code_java': """class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length;
        int n = matrix[0].length;

        int top = 0, bot = m - 1;
        int targetRow = -1;

        while (top <= bot) {
            int mid = top + (bot - top) / 2;
            if (target >= matrix[mid][0] && target <= matrix[mid][n - 1]) {
                targetRow = mid;
                break;
            } else if (target < matrix[mid][0]) {
                bot = mid - 1;
            } else {
                top = mid + 1;
            }
        }

        if (targetRow == -1) return false;

        int l = 0, r = n - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (matrix[targetRow][mid] == target) return true;
            else if (matrix[targetRow][mid] < target) l = mid + 1;
            else r = mid - 1;
        }
        return false;
    }
}""",
                'notes_java': "Correct O(log m + log n), but virtual 1D search is considered more elegant."
            },
            {
                'name': "Optimal: Virtual 1D Flattened Binary Search (Production Standard)",
                'desc': "Single binary search treating m x n matrix as 1D array via mid / n and mid % n.",
                'time': "O(log(m * n))",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: Virtual 1D Binary Search
     * Time: O(log(m * n)) - Single binary search loop
     * Space: O(1) - Constant auxiliary memory
     */
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        val m = matrix.size
        val n = matrix[0].size
        var left = 0
        var right = m * n - 1

        while (left <= right) {
            val mid = left + (right - left) / 2
            val midVal = matrix[mid / n][mid % n]

            when {
                midVal == target -> return true
                midVal < target -> left = mid + 1
                else -> right = mid - 1
            }
        }

        return false
    }
}""",
                'notes_kotlin': "Cleanest possible code. `mid / n` gives row index, `mid % n` gives column index.",
                'code_python': """class Solution:
    # Optimal: Virtual 1D Binary Search
    # Time: O(log(m * n))
    # Space: O(1)
    def searchMatrix(self, matrix: list[list[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        left, right = 0, m * n - 1
        
        while left <= right:
            mid = (left + right) // 2
            mid_val = matrix[mid // n][mid % n]
            
            if mid_val == target:
                return True
            elif mid_val < target:
                left = mid + 1
            else:
                right = mid - 1
                
        return False""",
                'notes_python': "Concise, idiomatic Python. Directly maps 1D coordinate `mid` to `(mid // n, mid % n)`.",
                'code_java': """class Solution {
    /**
     * Optimal: Virtual 1D Binary Search
     * Time: O(log(m * n))
     * Space: O(1)
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length;
        int n = matrix[0].length;
        int left = 0;
        int right = m * n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midVal = matrix[mid / n][mid % n];

            if (midVal == target) {
                return true;
            } else if (midVal < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }
}""",
                'notes_java': "The production gold standard. `left + (right - left) / 2` prevents integer overflow."
            },
            {
                'name': "Top-Right Corner Elimination (Search 2D Matrix II Style)",
                'desc': "Start at top-right corner; step left if value > target, step down if value < target.",
                'time': "O(m + n)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        var r = 0
        var c = matrix[0].size - 1

        while (r < matrix.size && c >= 0) {
            val curr = matrix[r][c]
            if (curr == target) return true
            else if (curr > target) c--
            else r++
        }
        return false
    }
}""",
                'notes_kotlin': "Runs in O(m + n) time. Slower than O(log(m*n)) for this problem, but universal for LC 240.",
                'code_python': """class Solution:
    def searchMatrix(self, matrix: list[list[int]], target: int) -> bool:
        r, c = 0, len(matrix[0]) - 1
        while r < len(matrix) and c >= 0:
            if matrix[r][c] == target:
                return True
            elif matrix[r][c] > target:
                c -= 1
            else:
                r += 1
        return False""",
                'notes_python': "Good approach to mention to interviewer as a bridge to LC 240 (Search a 2D Matrix II).",
                'code_java': """class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int r = 0;
        int c = matrix[0].length - 1;
        while (r < matrix.length && c >= 0) {
            if (matrix[r][c] == target) return true;
            else if (matrix[r][c] > target) c--;
            else r++;
        }
        return false;
    }
}""",
                'notes_java': "O(m + n) staircase walk. Elegant, but binary search is strictly faster when rows are fully connected."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        val m = matrix.size; val n = matrix[0].size
        var l = 0; var r = m * n - 1
        while (l <= r) {
            val mid = l + (r - l) / 2
            val v = matrix[mid / n][mid % n]
            if (v == target) return true
            else if (v < target) l = mid + 1
            else r = mid - 1
        }
        return false
    }
}""",
            'python_boilerplate': """class Solution:
    def searchMatrix(self, matrix: list[list[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        l, r = 0, m * n - 1
        while l <= r:
            mid = (l + r) // 2
            v = matrix[mid // n][mid % n]
            if v == target: return True
            elif v < target: l = mid + 1
            else: r = mid - 1
        return False""",
            'java_boilerplate': """class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int l = 0, r = m * n - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            int v = matrix[mid / n][mid % n];
            if (v == target) return true;
            else if (v < target) l = mid + 1;
            else r = mid - 1;
        }
        return false;
    }
}""",
            'js_test_runner': """
      const mat = [[1,3,5,7],[10,11,16,20],[23,30,34,60]];
      const m = mat.length, n = mat[0].length;
      const search = (target) => {
        let l = 0, r = m * n - 1;
        while (l <= r) {
          const mid = Math.floor(l + (r - l) / 2);
          const v = mat[Math.floor(mid / n)][mid % n];
          if (v === target) return true;
          else if (v < target) l = mid + 1;
          else r = mid - 1;
        }
        return false;
      };

      const t1 = (search(3) === true);
      const t2 = (search(13) === false);
      const t3 = (search(1) === true);
      const t4 = (search(60) === true);

      const passed = t1 && t2 && t3 && t4;
      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = `
        <div class="p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}">
          <span>Binary Search 2D Matrix Suite</span>
          <span>${passed ? '&#10004; ALL 4 TESTS PASSED (Target 3=true, 13=false, 1=true, 60=true)' : '&#10008; FAILED'}</span>
        </div>
      `;

      if (passed) {
        statusSpan.textContent = "All test cases passed!";
        statusSpan.className = "text-xs font-mono text-emerald-400 font-bold";
      } else {
        statusSpan.textContent = "Test failed.";
        statusSpan.className = "text-xs font-mono text-rose-400 font-bold";
      }
"""
        },
        'spoken_script': {
            'phase1': "I clarify: is the matrix guaranteed to be non-empty? Yes, m >= 1 and n >= 1. Are all rows sorted, and does each row strictly follow the last element of the previous row? Yes, meaning the entire matrix forms a single strictly increasing sequence if read in row-major order.",
            'phase2': "For strategy: a linear scan takes O(m * n). However, because the entire matrix is strictly sorted in row-major order, we can treat the m x n matrix as a flattened 1D array of length m * n. Any 1D index `mid` maps directly to 2D coordinates `matrix[mid / n][mid % n]`. This allows a single binary search running in optimal O(log(m * n)) time and O(1) space.",
            'phase3': "I set m = matrix.length, n = matrix[0].length, left = 0, right = m * n - 1. While left <= right, I compute mid = left + (right - left) / 2. I retrieve val = matrix[mid / n][mid % n]. If val == target, return true. If val < target, left = mid + 1. Else, right = mid - 1. If loop terminates, return false.",
            'phase4': "Dry running edge cases: 1x1 matrix [[5]] target 5: left=0, right=0, mid=0/0 -> returns true. Target smaller than min or larger than max: range quickly collapses and returns false. Calculation `mid = left + (right - left) / 2` prevents overflow.",
            'senior_followup': "Senior Follow-up: What if the matrix only guarantees that individual rows and columns are sorted, but row i does NOT start greater than row i-1 (e.g. Search a 2D Matrix II)? Then 1D virtual binary search does not hold; we must start at the top-right corner and perform a staircase elimination in O(m + n) time."
        },
        'quiz': [
            {
                'question': "Given a matrix with `n` columns, what is the formula to map 1D index `mid` to its (row, col) coordinates?",
                'options': [
                    "`row = mid % n`, `col = mid / n`",
                    "`row = mid / n`, `col = mid % n`",
                    "`row = mid / m`, `col = mid % m`",
                    "`row = mid * n`, `col = mid + n`"
                ],
                'correct_index': 1,
                'explanation': "Dividing by the column count (`mid / n`) calculates how many full rows fit into `mid`, and the remainder (`mid % n`) gives the horizontal column offset."
            },
            {
                'question': "What is the time complexity of virtual 1D binary search on an `m x n` matrix?",
                'options': [
                    "O(m + n)",
                    "O(m * n)",
                    "O(log(m * n))",
                    "O(log m * log n)"
                ],
                'correct_index': 2,
                'explanation': "The search space has size `m * n`. Binary search divides the remaining space in half on each step, requiring `log2(m * n)` iterations."
            }
        ]
    },

    # ---------------- 0037: FIND MINIMUM IN ROTATED SORTED ARRAY ----------------
    {
        'num': 37,
        'lc': 153,
        'title': "Find Minimum in Rotated Sorted Array",
        'category': "Binary Search",
        'difficulty': "Medium",
        'lc_slug': "find-minimum-in-rotated-sorted-array",
        'verbatim_statement': """<p>Suppose an array of length <code>n</code> sorted in ascending order is <strong>rotated</strong> between <code>1</code> and <code>n</code> times. For example, the array <code>nums = [0,1,2,4,5,6,7]</code> might become:</p>
<ul class="list-disc pl-5 mt-1 space-y-1 text-xs">
  <li><code>[4,5,6,7,0,1,2]</code> if it was rotated <code>4</code> times.</li>
  <li><code>[0,1,2,4,5,6,7]</code> if it was rotated <code>7</code> times.</li>
</ul>
<p class="mt-2">Notice that rotating an array <code>[a[0], a[1], a[2], ..., a[n-1]]</code> 1 time results in the array <code>[a[n-1], a[0], a[1], a[2], ..., a[n-2]]</code>.</p>
<p class="mt-2">Given the sorted rotated array <code>nums</code> of <strong>unique</strong> elements, return <em>the minimum element of this array</em>.</p>
<p class="mt-2">You must write an algorithm that runs in <code>O(log n)</code> time.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'nums = [3,4,5,1,2]', 'output': '1 (Original array was [1,2,3,4,5] rotated 3 times)'},
            {'title': "Example 2", 'input': 'nums = [4,5,6,7,0,1,2]', 'output': '0'},
            {'title': "Example 3", 'input': 'nums = [11,13,15,17]', 'output': '11'}
        ],
        'constraints': [
            "n == nums.length",
            "1 <= n <= 5000",
            "-5000 <= nums[i] <= 5000",
            "All the integers of nums are unique.",
            "nums is sorted and rotated between 1 and n times."
        ],
        'plain_english': "Find the smallest element in a sorted array that has been rotated around an unknown pivot. Use binary search. Compare `nums[mid]` against `nums[right]`. If `nums[mid] > nums[right]`, the inflection point (minimum) must be in the right half (`left = mid + 1`). If `nums[mid] <= nums[right]`, the minimum is in the left half or at `mid` (`right = mid`). Stop when `left == right`.",
        'boundary_checks': {
            'min_base': "<code>nums = [1]</code> &rarr; Single element &rarr; returns 1.",
            'best_exit': "Unrotated array <code>[11, 13, 15, 17]</code> &rarr; <code>nums[mid] < nums[right]</code> at every step, returns nums[0].",
            'worst_scan': "Rotated right at the end <code>[2, 1]</code> &rarr; Takes O(log N) steps to converge on index 1."
        },
        'mental_model': {
            'analogy_title': "The Broken Escalator",
            'analogy_text': "Imagine a sorted array as an escalator that constantly goes uphill. When it is rotated, it breaks into two separate ascending ramps: a higher ramp on the left, and a lower ramp on the right. The minimum element is the bottom step of the lower ramp. You compare where you stand (`mid`) against the far right step. If you are standing higher than the right step (`mid > right`), you must be on the higher ramp, so the drop-off is to your right. If you are lower, the drop-off is at or to your left.",
            'aha_moment': "Compare `nums[mid]` to `nums[right]`, NOT `nums[left]`. If `nums[mid] > nums[right]`, the pivot break is guaranteed to be in the right half (`left = mid + 1`). Otherwise `right = mid`. When `left == right`, it points directly to the minimum.",
            'trigger': "Whenever searching a rotated sorted array &rarr; Pivot Comparison Binary Search.",
            'sisters': [
                {'name': "Search in Rotated Sorted Array", 'lc': "LC #33"},
                {'name': "Find Minimum in Rotated Sorted Array II", 'lc': "LC #154"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N) Time, O(1) Space",
            'naive_text': "Linear scan to find the minimum value or first drop where `nums[i] < nums[i-1]`. Fails logarithmic requirement.",
            'wasted_work': "Inspecting every element sequentially instead of eliminating half the array using monotonicity.",
            'optimal_complexity': "O(log N) Time, O(1) Space",
            'optimal_text': "Binary search with invariant `nums[mid] vs nums[right]`. Halves search space on every iteration.",
            'core_invariant': "The minimum element is always contained within the closed interval `[left..right]`.",
            'table_rows': [
                {'name': "1. Linear Scan", 'time': "O(N)", 'space': "O(1)", 'note': "Checks all elements; fails O(log N) requirement."},
                {'name': "★ 2. Pivot Binary Search", 'time': "O(log N)", 'space': "O(1)", 'note': "Optimal, clean, standard interview solution."}
            ]
        },
        'trace_table': {
            'input_desc': 'nums = [4, 5, 6, 7, 0, 1, 2]',
            'columns': ["Left", "Right", "Mid", "nums[mid]", "nums[right]", "Comparison", "New Interval"],
            'rows': [
                ["0", "6", "3", "7", "2", "7 > 2 (Pivot is right)", "left = mid + 1 = 4"],
                ["4", "6", "5", "1", "2", "1 < 2 (Pivot is at/left)", "right = mid = 5"],
                ["4", "5", "4", "0", "1", "0 < 1 (Pivot is at/left)", "right = mid = 4"],
                ["4", "4", "-", "-", "-", "left == right", "Converged at index 4 (val = 0)"]
            ],
            'takeaway': "Converges in 3 iterations to the exact minimum 0."
        },
        'stepper': {
            'title': "Binary Search Invariant Halving on Rotated Array",
            'default_input': '4, 5, 6, 7, 0, 1, 2',
            'presets': [
                {'label': "Rotated [4,5,6,7,0,1,2]", 'onclick': 'loadPreset([4,5,6,7,0,1,2])'},
                {'label': "Rotated [3,4,5,1,2]", 'onclick': 'loadPreset([3,4,5,1,2])'},
                {'label': "Unrotated [11,13,15,17]", 'onclick': 'loadPreset([11,13,15,17])'}
            ],
            'metrics': [
                {'id': "metric-left", 'label': "Left Index", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-right", 'label': "Right Index", 'color': "text-cyan-400", 'initial': "6"},
                {'id': "metric-mid", 'label': "Mid Comparison", 'color': "text-amber-400", 'initial': "nums[3]=7 vs 2"},
                {'id': "metric-status", 'label': "Search Action", 'color': "text-purple-400", 'initial': "Start"}
            ],
            'visual_html': '<div id="rotated-array-stage" class="w-full flex items-end justify-center gap-2 h-44 border-b border-slate-800 pb-2"></div>',
            'initial_narration': 'Click "Next Step" to trace rotated binary search.',
            'pseudocode_lines': [
                "var l = 0; var r = nums.size - 1",
                "while (l < r) {",
                "    val mid = l + (r - l) / 2",
                "    if (nums[mid] > nums[r]) l = mid + 1",
                "    else r = mid",
                "}",
                "return nums[l]"
            ],
            'js_code': """
    let initialNums = [4, 5, 6, 7, 0, 1, 2];

    function buildSteps(arr) {
      steps = [];
      let l = 0;
      let r = arr.length - 1;

      steps.push({
        l: l,
        r: r,
        mid: -1,
        comp: "-",
        action: "Init",
        line: 1,
        narration: `Searching minimum across rotated array [${arr.join(', ')}].`
      });

      while (l < r) {
        const mid = Math.floor(l + (r - l) / 2);
        const valMid = arr[mid];
        const valR = arr[r];

        if (valMid > valR) {
          steps.push({
            l: l,
            r: r,
            mid: mid,
            comp: `${valMid} > ${valR}`,
            action: `left = mid + 1 (${mid + 1})`,
            line: 4,
            narration: `nums[${mid}] (${valMid}) > nums[${r}] (${valR}). Drop-off is strictly to the right. left = ${mid + 1}.`
          });
          l = mid + 1;
        } else {
          steps.push({
            l: l,
            r: r,
            mid: mid,
            comp: `${valMid} <= ${valR}`,
            action: `right = mid (${mid})`,
            line: 5,
            narration: `nums[${mid}] (${valMid}) <= nums[${r}] (${valR}). Minimum is at or left of mid. right = ${mid}.`
          });
          r = mid;
        }
      }

      steps.push({
        l: l,
        r: r,
        mid: l,
        comp: "Converged",
        action: `Minimum = ${arr[l]}`,
        line: 7,
        narration: `Converged! left == right at index ${l}. Minimum element is ${arr[l]}.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      initialNums = arr;
      stepperReset();
      buildSteps(initialNums);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        initialNums = arr;
        stepperReset();
        buildSteps(initialNums);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-left').textContent = `${step.l} (${initialNums[step.l]})`;
      document.getElementById('metric-right').textContent = `${step.r} (${initialNums[step.r]})`;
      document.getElementById('metric-mid').textContent = step.comp;
      document.getElementById('metric-status').textContent = step.action;
""",
            'js_render_visual': """
      const container = document.getElementById('rotated-array-stage');
      container.innerHTML = '';
      const minVal = Math.min(...initialNums);
      const maxVal = Math.max(...initialNums);
      const range = Math.max(maxVal - minVal, 1);

      initialNums.forEach((val, idx) => {
        const isLeft = (idx === step.l);
        const isRight = (idx === step.r);
        const isMid = (idx === step.mid);
        const inRange = (idx >= step.l && idx <= step.r);
        const heightPct = Math.round(((val - minVal + 2) / (range + 4)) * 100);

        let barCls = "bg-slate-800 text-slate-500 opacity-40";
        if (isMid && step.l === step.r) {
          barCls = "bg-emerald-500 text-slate-950 font-bold animate-pulse";
        } else if (isMid) {
          barCls = "bg-amber-400 text-slate-950 font-bold";
        } else if (inRange) {
          barCls = "bg-slate-700 text-slate-300";
        }

        const col = document.createElement('div');
        col.className = 'flex flex-col items-center justify-end h-full w-9';
        col.innerHTML = `
          <span class="text-[9px] font-mono text-slate-400 mb-1">${val}</span>
          <div class="w-full ${barCls} rounded-t transition-all duration-300" style="height: ${heightPct}%;"></div>
          <span class="text-[9px] font-mono text-slate-500 mt-1">${idx}</span>
          <div class="text-[8px] font-mono font-bold mt-0.5">
            ${isLeft ? '<span class="text-emerald-400">L</span>' : ''}
            ${isRight ? '<span class="text-cyan-400">R</span>' : ''}
            ${isMid ? '<span class="text-amber-400">M</span>' : ''}
          </div>
        `;
        container.appendChild(col);
      });
"""
        },
        'approaches': [
            {
                'name': "Linear Scan Baseline",
                'desc': "Iterate through array to find the smallest number.",
                'time': "O(N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun findMin(nums: IntArray): Int {
        var minVal = nums[0]
        for (num in nums) {
            if (num < minVal) minVal = num
        }
        return minVal
    }
}""",
                'notes_kotlin': "Linear scan is O(N). Fails the required O(log N) constraint.",
                'code_python': """class Solution:
    def findMin(self, nums: list[int]) -> int:
        return min(nums)""",
                'notes_python': "Takes O(N) time. Will be rejected in a technical interview requiring logarithmic time.",
                'code_java': """class Solution {
    public int findMin(int[] nums) {
        int min = nums[0];
        for (int num : nums) {
            if (num < min) min = num;
        }
        return min;
    }
}""",
                'notes_java': "Acceptable only as the immediate 10-second naive verbal baseline."
            },
            {
                'name': "Optimal: Pivot Comparison Binary Search (Standard)",
                'desc': "Compare nums[mid] with nums[right] to halve search space.",
                'time': "O(log N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: Binary Search comparing against right pointer
     * Time: O(log N) - Halves interval at each step
     * Space: O(1) - Constant auxiliary memory
     */
    fun findMin(nums: IntArray): Int {
        var left = 0
        var right = nums.size - 1

        while (left < right) {
            val mid = left + (right - left) / 2

            if (nums[mid] > nums[right]) {
                // Inflection point is in the right half
                left = mid + 1
            } else {
                // Minimum is at mid or in the left half
                right = mid
            }
        }

        return nums[left]
    }
}""",
                'notes_kotlin': "Notice the condition `while (left < right)` and `right = mid`. When pointers converge, `left == right` points to the minimum.",
                'code_python': """class Solution:
    # Optimal: Binary Search
    # Time: O(log N)
    # Space: O(1)
    def findMin(self, nums: list[int]) -> int:
        left = 0
        right = len(nums) - 1
        
        while left < right:
            mid = (left + right) // 2
            
            if nums[mid] > nums[right]:
                left = mid + 1
            else:
                right = mid
                
        return nums[left]""",
                'notes_python': "The canonical Python solution. Never compares against `nums[left]` because unrotated segments break symmetry.",
                'code_java': """class Solution {
    /**
     * Optimal: Binary Search
     * Time: O(log N)
     * Space: O(1)
     */
    public int findMin(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return nums[left];
    }
}""",
                'notes_java': "The production gold standard. `right = mid` retains mid as a potential minimum candidate."
            },
            {
                'name': "Early-Exit Pre-Sorted Optimization",
                'desc': "Add check `if (nums[left] < nums[right]) return nums[left]` to exit on unrotated subarrays.",
                'time': "O(log N) worst, O(1) best",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun findMin(nums: IntArray): Int {
        var left = 0
        var right = nums.size - 1

        while (left < right) {
            // If subarray is already sorted, left is the minimum
            if (nums[left] < nums[right]) return nums[left]

            val mid = left + (right - left) / 2
            if (nums[mid] > nums[right]) {
                left = mid + 1
            } else {
                right = mid
            }
        }
        return nums[left]
    }
}""",
                'notes_kotlin': "`nums[left] < nums[right]` short-circuits in O(1) time if the current search segment is already sorted.",
                'code_python': """class Solution:
    def findMin(self, nums: list[int]) -> int:
        l, r = 0, len(nums) - 1
        while l < r:
            if nums[l] < nums[r]:
                return nums[l]
            mid = (l + r) // 2
            if nums[mid] > nums[r]:
                l = mid + 1
            else:
                r = mid
        return nums[l]""",
                'notes_python': "Optimizes unrotated or partially rotated test cases to O(1) early exit.",
                'code_java': """class Solution {
    public int findMin(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            if (nums[l] < nums[r]) return nums[l];
            int mid = l + (r - l) / 2;
            if (nums[mid] > nums[r]) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return nums[l];
    }
}""",
                'notes_java': "Saves loop cycles on arrays rotated n times (which are completely sorted)."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun findMin(nums: IntArray): Int {
        var l = 0; var r = nums.size - 1
        while (l < r) {
            val mid = l + (r - l) / 2
            if (nums[mid] > nums[r]) l = mid + 1
            else r = mid
        }
        return nums[l]
    }
}""",
            'python_boilerplate': """class Solution:
    def findMin(self, nums: list[int]) -> int:
        l, r = 0, len(nums) - 1
        while l < r:
            mid = (l + r) // 2
            if nums[mid] > nums[r]: l = mid + 1
            else: r = mid
        return nums[l]""",
            'java_boilerplate': """class Solution {
    public int findMin(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] > nums[r]) l = mid + 1;
            else r = mid;
        }
        return nums[l];
    }
}""",
            'js_test_runner': """
      const testCases = [
        { nums: [3,4,5,1,2], expected: 1 },
        { nums: [4,5,6,7,0,1,2], expected: 0 },
        { nums: [11,13,15,17], expected: 11 },
        { nums: [2,1], expected: 1 },
        { nums: [1], expected: 1 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        let l = 0, r = tc.nums.length - 1;
        while (l < r) {
          const mid = Math.floor(l + (r - l) / 2);
          if (tc.nums[mid] > tc.nums[r]) l = mid + 1;
          else r = mid;
        }
        const res = tc.nums[l];
        const passed = (res === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: nums=[${tc.nums.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (min=' + res + ')' : '&#10008; FAILED (expected ' + tc.expected + ', got ' + res + ')'}</span>
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
            'phase1': "I clarify: are all elements guaranteed to be unique? Yes, elements are unique per constraints. What if the array is rotated n times (meaning it is back in original sorted order)? It should still return the first element. Must the time complexity be O(log N)? Yes.",
            'phase2': "For strategy: a linear scan is O(N). Because the array consists of two sorted subarrays, we can binary search by comparing nums[mid] against nums[right]. If nums[mid] > nums[right], the pivot break must be to the right, so left = mid + 1. If nums[mid] <= nums[right], the minimum is in the left half or at mid itself, so right = mid. We loop while left < right and return nums[left], achieving O(log N) time and O(1) space.",
            'phase3': "I initialize left = 0, right = nums.length - 1. While left < right: compute mid = left + (right - left) / 2. If nums[mid] > nums[right], left = mid + 1. Otherwise, right = mid. When left == right, the pointers converge on the minimum element, and I return nums[left].",
            'phase4': "Dry running edge cases: unrotated array [1, 2, 3]: mid=1 (val 2), right=2 (val 3). 2 <= 3 -> right = 1. mid=0 (val 1), right=1 (val 2). 1 <= 2 -> right = 0. left == right == 0, returns nums[0] = 1. Two elements [2, 1]: mid=0 (val 2), right=1 (val 1). 2 > 1 -> left = 1. left == right == 1, returns nums[1] = 1. All edge cases hold.",
            'senior_followup': "Senior Follow-up: What if duplicate elements are allowed (e.g. LC 154)? If nums[mid] == nums[right], we cannot determine which half contains the pivot (e.g. [3,3,1,3] vs [1,3,3,3]). We must defensively decrement right-- in the worst case, degrading time complexity to O(N)."
        },
        'quiz': [
            {
                'question': "Why do we compare `nums[mid]` against `nums[right]` rather than `nums[left]`?",
                'options': [
                    "Because arrays in Java are right-aligned.",
                    "If the array is already sorted (e.g. [1, 2, 3]), `nums[mid] > nums[left]` is true, which would falsely suggest moving right when the minimum is at index 0.",
                    "Because left pointers cannot be compared.",
                    "Because right pointers are always positive."
                ],
                'correct_index': 1,
                'explanation': "Comparing against `nums[left]` fails on unrotated sorted arrays like `[1, 2, 3, 4, 5]`: `nums[mid]=3 > nums[left]=1`, which would cause `left = mid + 1`, completely skipping the minimum element at index 0."
            },
            {
                'question': "Why is the assignment `right = mid` instead of `right = mid - 1` when `nums[mid] <= nums[right]`?",
                'options': [
                    "To prevent infinite loop.",
                    "`nums[mid]` could itself be the minimum element (e.g. [4, 5, 1, 2]), so setting `right = mid - 1` would exclude the answer.",
                    "Because right pointers cannot decrement.",
                    "Because mid is always even."
                ],
                'correct_index': 1,
                'explanation': "When `nums[mid] <= nums[right]`, `mid` itself could be the inflection point (the minimum). Setting `right = mid - 1` would eliminate the correct answer from the search range."
            }
        ]
    },

    # ---------------- 0038: FIND PEAK ELEMENT ----------------
    {
        'num': 38,
        'lc': 162,
        'title': "Find Peak Element",
        'category': "Binary Search",
        'difficulty': "Medium",
        'lc_slug': "find-peak-element",
        'verbatim_statement': """<p>A peak element is an element that is strictly greater than its neighbors.</p>
<p class="mt-2">Given a <strong>0-indexed</strong> integer array <code>nums</code>, find a peak element, and return its index. If the array contains multiple peaks, return the index to <strong>any of the peaks</strong>.</p>
<p class="mt-2">You may imagine that <code>nums[-1] = nums[n] = -&infin;</code>. In other words, an element is always considered to be strictly greater than a neighbor that is outside the array.</p>
<p class="mt-2">You must write an algorithm that runs in <code>O(log n)</code> time.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'nums = [1,2,3,1]', 'output': '2 (3 is a peak element and your function should return index 2)'},
            {'title': "Example 2", 'input': 'nums = [1,2,1,3,5,6,4]', 'output': '5 (or index 1, where value is 2)'}
        ],
        'constraints': [
            "1 <= nums.length <= 1000",
            "-2^31 <= nums[i] <= 2^31 - 1",
            "nums[i] != nums[i + 1] for all valid i."
        ],
        'plain_english': "Find the index of any local peak (where nums[i] > nums[i-1] and nums[i] > nums[i+1]). You don't need to find the global maximum, just any peak. If nums[mid] < nums[mid + 1], you are on an upward slope, so a peak is guaranteed to exist to the right. If nums[mid] > nums[mid + 1], you are on a downward slope (or at a peak), so a peak exists to the left or at mid.",
        'boundary_checks': {
            'min_base': "<code>nums = [1]</code> &rarr; Boundary walls are -&infin; &rarr; index 0 is always a peak.",
            'best_exit': "Strictly increasing <code>[1, 2, 3]</code> &rarr; Last element is peak (3 > -&infin;).",
            'worst_scan': "Zigzag array <code>[1, 3, 2, 4, 3]</code> &rarr; Any of multiple valid peaks acceptable."
        },
        'mental_model': {
            'analogy_title': "The Foggy Mountain Climber",
            'analogy_text': "Imagine climbing in dense fog where you can only see one foot ahead. If the ground slopes UP to your right (`nums[mid] < nums[mid + 1]`), walking right is guaranteed to lead to a peak (even if the terrain keeps rising all the way to the boundary, the boundary drops off to -&infin;, creating a peak). If the ground slopes DOWN to your right, you either are at a peak or passed one, so walk left.",
            'aha_moment': "You do not need a sorted array to use binary search! Because boundaries are -&infin;, following the rising slope is mathematically guaranteed to terminate at a peak.",
            'trigger': "Whenever asked to find a local extrema in O(log N) time on an unsorted array &rarr; Slope-Following Binary Search.",
            'sisters': [
                {'name': "Peak Index in a Mountain Array", 'lc': "LC #852"},
                {'name': "Find in Mountain Array", 'lc': "LC #1095"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N) Time, O(1) Space",
            'naive_text': "Linear scan checking where `nums[i] > nums[i+1]`. Fails the strict O(log N) requirement.",
            'wasted_work': "Checking every step instead of skipping half the mountain range based on slope direction.",
            'optimal_complexity': "O(log N) Time, O(1) Space",
            'optimal_text': "Binary search. If `nums[mid] < nums[mid+1]`, search right (`l = mid + 1`); else search left (`r = mid`).",
            'core_invariant': "The current search interval `[left..right]` contains at least one local peak at all times.",
            'table_rows': [
                {'name': "1. Linear Scan", 'time': "O(N)", 'space': "O(1)", 'note': "Scans every element; fails logarithmic constraint."},
                {'name': "★ 2. Slope Binary Search", 'time': "O(log N)", 'space': "O(1)", 'note': "Optimal. Follows upward slope to peak."}
            ]
        },
        'trace_table': {
            'input_desc': 'nums = [1, 2, 1, 3, 5, 6, 4]',
            'columns': ["Left", "Right", "Mid", "nums[mid]", "nums[mid+1]", "Slope Direction", "New Interval"],
            'rows': [
                ["0", "6", "3", "3", "5", "3 < 5 (Upward right)", "left = mid + 1 = 4"],
                ["4", "6", "5", "6", "4", "6 > 4 (Downward right)", "right = mid = 5"],
                ["4", "5", "4", "5", "6", "5 < 6 (Upward right)", "left = mid + 1 = 5"],
                ["5", "5", "-", "-", "-", "left == right", "Converged at index 5 (val = 6)"]
            ],
            'takeaway': "Returns index 5 (val 6), which is strictly greater than 5 and 4."
        },
        'stepper': {
            'title': "Slope Climbing Binary Search Animation",
            'default_input': '1, 2, 1, 3, 5, 6, 4',
            'presets': [
                {'label': "Two Peaks [1,2,1,3,5,6,4]", 'onclick': 'loadPreset([1,2,1,3,5,6,4])'},
                {'label': "Single Peak [1,2,3,1]", 'onclick': 'loadPreset([1,2,3,1])'},
                {'label': "Strictly Rising [1,2,3,4,5]", 'onclick': 'loadPreset([1,2,3,4,5])'}
            ],
            'metrics': [
                {'id': "metric-left", 'label': "Left Index", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-right", 'label': "Right Index", 'color': "text-cyan-400", 'initial': "6"},
                {'id': "metric-mid", 'label': "Slope at Mid", 'color': "text-amber-400", 'initial': "3 < 5 (Up)"},
                {'id': "metric-status", 'label': "Action", 'color': "text-purple-400", 'initial': "Start"}
            ],
            'visual_html': '<div id="peak-array-stage" class="w-full flex items-end justify-center gap-2 h-44 border-b border-slate-800 pb-2"></div>',
            'initial_narration': 'Click "Next Step" to trace slope climbing binary search.',
            'pseudocode_lines': [
                "var l = 0; var r = nums.size - 1",
                "while (l < r) {",
                "    val mid = l + (r - l) / 2",
                "    if (nums[mid] < nums[mid + 1]) l = mid + 1",
                "    else r = mid",
                "}",
                "return l"
            ],
            'js_code': """
    let initialNums = [1, 2, 1, 3, 5, 6, 4];

    function buildSteps(arr) {
      steps = [];
      let l = 0;
      let r = arr.length - 1;

      steps.push({
        l: l,
        r: r,
        mid: -1,
        slope: "-",
        action: "Init",
        line: 1,
        narration: `Searching peak across array [${arr.join(', ')}].`
      });

      while (l < r) {
        const mid = Math.floor(l + (r - l) / 2);
        const valMid = arr[mid];
        const valNext = arr[mid + 1];

        if (valMid < valNext) {
          steps.push({
            l: l,
            r: r,
            mid: mid,
            slope: `${valMid} < ${valNext} (Ascending)`,
            action: `left = mid + 1 (${mid + 1})`,
            line: 4,
            narration: `nums[${mid}] (${valMid}) < nums[${mid+1}] (${valNext}). Uphill to the right &rarr; peak must be on right. left = ${mid + 1}.`
          });
          l = mid + 1;
        } else {
          steps.push({
            l: l,
            r: r,
            mid: mid,
            slope: `${valMid} > ${valNext} (Descending)`,
            action: `right = mid (${mid})`,
            line: 5,
            narration: `nums[${mid}] (${valMid}) > nums[${mid+1}] (${valNext}). Downhill to right &rarr; mid could be peak or peak is to left. right = ${mid}.`
          });
          r = mid;
        }
      }

      steps.push({
        l: l,
        r: r,
        mid: l,
        slope: "Converged",
        action: `Peak index = ${l} (val = ${arr[l]})`,
        line: 7,
        narration: `Converged! left == right at index ${l}. Element ${arr[l]} is a confirmed local peak.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      initialNums = arr;
      stepperReset();
      buildSteps(initialNums);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        initialNums = arr;
        stepperReset();
        buildSteps(initialNums);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-left').textContent = `${step.l} (${initialNums[step.l]})`;
      document.getElementById('metric-right').textContent = `${step.r} (${initialNums[step.r]})`;
      document.getElementById('metric-mid').textContent = step.slope;
      document.getElementById('metric-status').textContent = step.action;
""",
            'js_render_visual': """
      const container = document.getElementById('peak-array-stage');
      container.innerHTML = '';
      const minVal = Math.min(...initialNums);
      const maxVal = Math.max(...initialNums);
      const range = Math.max(maxVal - minVal, 1);

      initialNums.forEach((val, idx) => {
        const isLeft = (idx === step.l);
        const isRight = (idx === step.r);
        const isMid = (idx === step.mid);
        const inRange = (idx >= step.l && idx <= step.r);
        const heightPct = Math.round(((val - minVal + 2) / (range + 4)) * 100);

        let barCls = "bg-slate-800 text-slate-500 opacity-40";
        if (isMid && step.l === step.r) {
          barCls = "bg-emerald-500 text-slate-950 font-bold animate-pulse";
        } else if (isMid) {
          barCls = "bg-amber-400 text-slate-950 font-bold";
        } else if (inRange) {
          barCls = "bg-slate-700 text-slate-300";
        }

        const col = document.createElement('div');
        col.className = 'flex flex-col items-center justify-end h-full w-9';
        col.innerHTML = `
          <span class="text-[9px] font-mono text-slate-400 mb-1">${val}</span>
          <div class="w-full ${barCls} rounded-t transition-all duration-300" style="height: ${heightPct}%;"></div>
          <span class="text-[9px] font-mono text-slate-500 mt-1">${idx}</span>
          <div class="text-[8px] font-mono font-bold mt-0.5">
            ${isLeft ? '<span class="text-emerald-400">L</span>' : ''}
            ${isRight ? '<span class="text-cyan-400">R</span>' : ''}
            ${isMid ? '<span class="text-amber-400">M</span>' : ''}
          </div>
        `;
        container.appendChild(col);
      });
"""
        },
        'approaches': [
            {
                'name': "Linear Scan Baseline",
                'desc': "First index where nums[i] > nums[i+1] is guaranteed to be a peak.",
                'time': "O(N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun findPeakElement(nums: IntArray): Int {
        for (i in 0 until nums.size - 1) {
            if (nums[i] > nums[i + 1]) return i
        }
        return nums.size - 1
    }
}""",
                'notes_kotlin': "Linear scan is O(N). Fails the required O(log N) constraint.",
                'code_python': """class Solution:
    def findPeakElement(self, nums: list[int]) -> int:
        for i in range(len(nums) - 1):
            if nums[i] > nums[i + 1]:
                return i
        return len(nums) - 1""",
                'notes_python': "Clean O(N) baseline, but fails logarithmic interview requirement.",
                'code_java': """class Solution {
    public int findPeakElement(int[] nums) {
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                return i;
            }
        }
        return nums.length - 1;
    }
}""",
                'notes_java': "Shows understanding of the -infinity boundary conditions in O(N) time."
            },
            {
                'name': "Optimal: Slope-Following Binary Search (Production Standard)",
                'desc': "If nums[mid] < nums[mid+1], climb right; else climb left.",
                'time': "O(log N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: Slope-Climbing Binary Search
     * Time: O(log N) - Halves mountain search space on each step
     * Space: O(1) - Constant auxiliary memory
     */
    fun findPeakElement(nums: IntArray): Int {
        var left = 0
        var right = nums.size - 1

        while (left < right) {
            val mid = left + (right - left) / 2

            if (nums[mid] < nums[mid + 1]) {
                // Ascending slope: peak is guaranteed to the right
                left = mid + 1
            } else {
                // Descending slope: mid could be a peak, or peak is to the left
                right = mid
            }
        }

        return left
    }
}""",
                'notes_kotlin': "`while (left < right)` ensures `mid + 1` is always a valid array index, eliminating IndexOutOfBoundsException.",
                'code_python': """class Solution:
    # Optimal: Slope-Following Binary Search
    # Time: O(log N)
    # Space: O(1)
    def findPeakElement(self, nums: list[int]) -> int:
        left = 0
        right = len(nums) - 1
        
        while left < right:
            mid = (left + right) // 2
            
            if nums[mid] < nums[mid + 1]:
                left = mid + 1
            else:
                right = mid
                
        return left""",
                'notes_python': "The canonical Python solution. Converges when left == right.",
                'code_java': """class Solution {
    /**
     * Optimal: Slope-Following Binary Search
     * Time: O(log N)
     * Space: O(1)
     */
    public int findPeakElement(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] < nums[mid + 1]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}""",
                'notes_java': "Production standard. Guarantees finding a local maximum in O(log N) time on an unsorted array."
            },
            {
                'name': "Recursive Divide and Conquer",
                'desc': "Recursive formulation of slope climbing.",
                'time': "O(log N)",
                'space': "O(log N) Call Stack",
                'code_kotlin': """class Solution {
    fun findPeakElement(nums: IntArray): Int {
        return search(nums, 0, nums.size - 1)
    }

    private fun search(nums: IntArray, l: Int, r: Int): Int {
        if (l == r) return l
        val mid = l + (r - l) / 2
        return if (nums[mid] < nums[mid + 1]) {
            search(nums, mid + 1, r)
        } else {
            search(nums, l, mid)
        }
    }
}""",
                'notes_kotlin': "Recursive divide and conquer. Elegant proof of convergence.",
                'code_python': """class Solution:
    def findPeakElement(self, nums: list[int]) -> int:
        def search(l, r):
            if l == r:
                return l
            mid = (l + r) // 2
            if nums[mid] < nums[mid + 1]:
                return search(mid + 1, r)
            return search(l, mid)
            
        return search(0, len(nums) - 1)""",
                'notes_python': "Clean recursive formulation with helper closure.",
                'code_java': """class Solution {
    public int findPeakElement(int[] nums) {
        return search(nums, 0, nums.length - 1);
    }

    private int search(int[] nums, int l, int r) {
        if (l == r) return l;
        int mid = l + (r - l) / 2;
        if (nums[mid] < nums[mid + 1]) {
            return search(nums, mid + 1, r);
        } else {
            return search(nums, l, mid);
        }
    }
}""",
                'notes_java': "O(log N) recursion depth. Useful for demonstrating divide and conquer fundamentals."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun findPeakElement(nums: IntArray): Int {
        var l = 0; var r = nums.size - 1
        while (l < r) {
            val mid = l + (r - l) / 2
            if (nums[mid] < nums[mid + 1]) l = mid + 1
            else r = mid
        }
        return l
    }
}""",
            'python_boilerplate': """class Solution:
    def findPeakElement(self, nums: list[int]) -> int:
        l, r = 0, len(nums) - 1
        while l < r:
            mid = (l + r) // 2
            if nums[mid] < nums[mid + 1]: l = mid + 1
            else: r = mid
        return l""",
            'java_boilerplate': """class Solution {
    public int findPeakElement(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] < nums[mid + 1]) l = mid + 1;
            else r = mid;
        }
        return l;
    }
}""",
            'js_test_runner': """
      const testCases = [
        { nums: [1,2,3,1], validPeaks: [2] },
        { nums: [1,2,1,3,5,6,4], validPeaks: [1, 5] },
        { nums: [1], validPeaks: [0] },
        { nums: [1,2], validPeaks: [1] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        let l = 0, r = tc.nums.length - 1;
        while (l < r) {
          const mid = Math.floor(l + (r - l) / 2);
          if (tc.nums[mid] < tc.nums[mid + 1]) l = mid + 1;
          else r = mid;
        }

        const passed = tc.validPeaks.includes(l);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: nums=[${tc.nums.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (found peak index ' + l + ' val=' + tc.nums[l] + ')' : '&#10008; FAILED (got ' + l + ')'}</span>
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
            'phase1': "I clarify: can there be multiple peaks? Yes, returning ANY peak index is acceptable. Are adjacent elements guaranteed to be non-equal? Yes, nums[i] != nums[i + 1]. What are the boundary conditions? nums[-1] = nums[n] = -&infin;, which guarantees that every array has at least one peak.",
            'phase2': "For strategy: a linear scan takes O(N). Because adjacent elements are never equal and boundaries are -&infin;, if nums[mid] < nums[mid + 1], there is guaranteed to be a peak in the right half (even if the values keep rising all the way to the right boundary). Otherwise, a peak exists at mid or to the left. Thus, we can apply binary search to find a peak in O(log N) time and O(1) space.",
            'phase3': "I set left = 0, right = nums.length - 1. While left < right: compute mid = left + (right - left) / 2. If nums[mid] < nums[mid + 1], left = mid + 1. Else, right = mid. When left == right, we have converged on a peak, and I return left.",
            'phase4': "Dry running edge cases: single element [1]: loop does not run, returns 0 (which is a peak since 1 > -&infin; on both sides). Strictly ascending [1, 2, 3]: always climbs right, converges on index 2 (val 3). Time complexity is strictly O(log N).",
            'senior_followup': "Senior Scalability: What if adjacent elements CAN be equal (nums[i] == nums[i+1])? Then slope direction becomes ambiguous on a plateau (e.g. [1, 2, 2, 2, 3] vs [1, 2, 2, 2, 1]); binary search can no longer guarantee O(log N) and degrades to O(N) linear search."
        },
        'quiz': [
            {
                'question': "Why is binary search applicable to an UNSORTED array in Find Peak Element?",
                'options': [
                    "Because all arrays in Java are internally sorted by the compiler.",
                    "Because following the rising slope (where nums[mid] < nums[mid+1]) is mathematically guaranteed to lead to a local peak due to the -infinity boundaries.",
                    "Because peaks only exist at even indices.",
                    "Binary search is not applicable; the problem is impossible."
                ],
                'correct_index': 1,
                'explanation': "Binary search does not strictly require a sorted array; it requires an invariant that allows eliminating one half of the search space. Following the rising slope guarantees that at least one peak exists in that direction."
            },
            {
                'question': "Why is it guaranteed that `mid + 1` never causes an IndexOutOfBoundsException?",
                'options': [
                    "Because `while (left < right)` ensures `mid < right`. Since `right <= nums.length - 1`, `mid` is at most `nums.length - 2`, making `mid + 1` strictly valid.",
                    "Because arrays in Python wrap around automatically.",
                    "Because mid is always 0.",
                    "Because length is at least 1000."
                ],
                'correct_index': 0,
                'explanation': "With `left < right`, integer division `(left + right) / 2` strictly floors `mid < right`. Thus `mid` can never equal `right`, guaranteeing `mid + 1 <= right` is always within array bounds."
            }
        ]
    }
]

def main():
    import re
    from template_engine import render_lesson

    c = open('master-plan.html').read()
    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),\s*title:\s*\"([^\"]+)\",\s*lc:\s*([^,]+),\s*pass:\s*\"([^\"]+)\".*?file:\s*\"([^\"]+)\"', c):
        num = int(m.group(1))
        fname = m.group(5)
        files[num] = fname

    print(f"Generating Cluster 3: {len(cluster3_problems)} problems (34 to 38)...")
    for p in cluster3_problems:
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
