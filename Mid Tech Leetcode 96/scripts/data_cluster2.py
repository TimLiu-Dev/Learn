import sys, re
sys.path.append('scripts')
from template_engine import render_lesson

cluster2_problems = [
    # ---------------- 0028: MIN STACK ----------------
    {
        'num': 28,
        'lc': 155,
        'title': "Min Stack",
        'category': "Stacks, Queues & Monotonic Stacks",
        'difficulty': "Medium",
        'lc_slug': "min-stack",
        'verbatim_statement': """<p>Design a stack that supports push, pop, top, and retrieving the minimum element in constant time.</p>
<p class="mt-2">Implement the <code>MinStack</code> class:</p>
<ul class="list-disc pl-5 mt-1 space-y-1 text-xs">
  <li><code>MinStack()</code> initializes the stack object.</li>
  <li><code>void push(int val)</code> pushes the element <code>val</code> onto the stack.</li>
  <li><code>void pop()</code> removes the element on the top of the stack.</li>
  <li><code>int top()</code> gets the top element of the stack.</li>
  <li><code>int getMin()</code> retrieves the minimum element in the stack.</li>
</ul>
<p class="mt-2">You must implement a solution with <code>O(1)</code> time complexity for each function.</p>""",
        'examples': [
            {'title': "Example 1", 'input': '["MinStack","push","push","push","getMin","pop","top","getMin"]\n[[],[-2],[0],[-3],[],[],[],[]]', 'output': '[null,null,null,null,-3,null,0,-2]'}
        ],
        'constraints': [
            "-2^31 <= val <= 2^31 - 1",
            "Methods pop, top and getMin operations will always be called on non-empty stacks.",
            "At most 3 * 10^4 calls will be made to push, pop, top, and getMin."
        ],
        'plain_english': "Build a stack where finding the minimum value takes O(1) time without looping. Each element records what the minimum was at the exact moment it was pushed. When an element is popped, the minimum naturally rolls back to what it was before.",
        'boundary_checks': {
            'min_base': "Single element pushed &rarr; top() == getMin() == val.",
            'best_exit': "Strictly decreasing pushes <code>[-1, -2, -3]</code> &rarr; Each push becomes the new minimum.",
            'worst_scan': "Popping duplicate minimums &rarr; Correctly decrements tracking without losing minimum."
        },
        'mental_model': {
            'analogy_title': "The Stack of Labeled Plates",
            'analogy_text': "Imagine a cafeteria stack of plates. Each time you wash a plate, you write two numbers on its rim: the plate's own weight, and the minimum weight of any plate below it (including itself). To find the minimum plate in the stack, you simply glance at the rim of the top plate. Popping a plate automatically exposes the previous minimum beneath it.",
            'aha_moment': "Stacks are LIFO. The minimum cannot change between pushes and pops. Storing `currentMin` alongside each node (or in a parallel min-stack) guarantees O(1) lookup.",
            'trigger': "Whenever asked for O(1) queries on dynamic stack state &rarr; Parallel Stack or (Value, State) Node Tuple.",
            'sisters': [
                {'name': "Max Stack", 'lc': "LC #716"},
                {'name': "Daily Temperatures", 'lc': "LC #739"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "push/pop/top: O(1), getMin: O(N)",
            'naive_text': "Standard stack implementation; getMin iterates through the entire stack to find the minimum value. Fails constant time constraint.",
            'wasted_work': "Re-scanning historical elements on every getMin call when LIFO guarantees historical minimums do not change.",
            'optimal_complexity': "All operations O(1) Time, O(N) Space",
            'optimal_text': "Store (val, currentMin) pairs or maintain a synchronized `minStack` where top is always the running minimum.",
            'core_invariant': "At any stack depth D, the min-tracker stores the minimum of elements from depth 0 to D.",
            'table_rows': [
                {'name': "1. Linear Scan getMin", 'time': "getMin: O(N)", 'space': "O(N)", 'note': "Scans stack on query; fails O(1) requirement."},
                {'name': "2. Parallel Min Stack", 'time': "All O(1)", 'space': "O(2N)", 'note': "Dedicated secondary stack tracking min values."},
                {'name': "★ 3. Value-Min Node Pair", 'time': "All O(1)", 'space': "O(N)", 'note': "Single stack of Pair(val, min). Cleanest OOP."}
            ]
        },
        'trace_table': {
            'input_desc': 'push(-2), push(0), push(-3), getMin(), pop(), top(), getMin()',
            'columns': ["Operation", "Val", "Main Stack Top", "Current Min", "Returned Value"],
            'rows': [
                ["push(-2)", "-2", "-2", "-2", "null"],
                ["push(0)", "0", "0", "-2", "null"],
                ["push(-3)", "-3", "-3", "-3", "null"],
                ["getMin()", "-", "-3", "-3", "-3"],
                ["pop()", "-", "0", "-2", "null"],
                ["top()", "-", "0", "-2", "0"],
                ["getMin()", "-", "0", "-2", "-2"]
            ],
            'takeaway': "When -3 is popped, the minimum instantly rolls back to -2 in O(1) time."
        },
        'stepper': {
            'title': "LIFO Push/Pop with Running Minimum Shadow Tracking",
            'default_input': 'push(-2), push(0), push(-3), getMin, pop, top, getMin',
            'presets': [
                {'label': "Example Flow", 'onclick': 'loadPreset(["push(-2)","push(0)","push(-3)","getMin","pop","top","getMin"])'},
                {'label': "Ascending Push", 'onclick': 'loadPreset(["push(1)","push(2)","push(3)","getMin","pop"])'},
                {'label': "Duplicate Mins", 'onclick': 'loadPreset(["push(2)","push(2)","getMin","pop","getMin"])'}
            ],
            'metrics': [
                {'id': "metric-op", 'label': "Operation", 'color': "text-amber-400", 'initial': "Init"},
                {'id': "metric-top", 'label': "Top Value", 'color': "text-cyan-400", 'initial': "null"},
                {'id': "metric-min", 'label': "Current Min", 'color': "text-emerald-400", 'initial': "null"},
                {'id': "metric-size", 'label': "Stack Depth", 'color': "text-purple-400", 'initial': "0"}
            ],
            'visual_html': '<div id="minstack-visual" class="w-full flex flex-col-reverse items-center justify-end gap-1.5 h-48 border border-slate-800 rounded-xl p-3 bg-slate-900/40"></div>',
            'initial_narration': 'Click "Next Step" to trace MinStack state transitions.',
            'pseudocode_lines': [
                "class Node(val value: Int, val min: Int)",
                "val stack = ArrayDeque<Node>()",
                "fun push(x: Int) {",
                "    val currentMin = if (stack.isEmpty()) x else minOf(x, stack.first().min)",
                "    stack.addFirst(Node(x, currentMin))",
                "}",
                "fun pop() = stack.removeFirst()",
                "fun getMin() = stack.first().min"
            ],
            'js_code': """
    let initialOps = ["push(-2)","push(0)","push(-3)","getMin","pop","top","getMin"];

    function buildSteps(ops) {
      steps = [];
      const stack = [];

      steps.push({
        op: "Ready",
        top: "null",
        min: "null",
        size: 0,
        stackState: [],
        line: 2,
        narration: "Initialized empty MinStack."
      });

      for (let i = 0; i < ops.length; i++) {
        const item = ops[i].trim();
        if (item.startsWith("push")) {
          const val = parseInt(item.match(/\\(([^)]+)\\)/)[1]);
          const currentMin = (stack.length === 0) ? val : Math.min(val, stack[stack.length - 1].min);
          stack.push({ val: val, min: currentMin });
          steps.push({
            op: `push(${val})`,
            top: val,
            min: currentMin,
            size: stack.length,
            stackState: JSON.parse(JSON.stringify(stack)),
            line: 5,
            narration: `Pushed ${val}. Running minimum is now ${currentMin}. Stack size = ${stack.length}.`
          });
        } else if (item === "pop") {
          const popped = stack.pop();
          const topVal = stack.length > 0 ? stack[stack.length - 1].val : "null";
          const minVal = stack.length > 0 ? stack[stack.length - 1].min : "null";
          steps.push({
            op: "pop()",
            top: topVal,
            min: minVal,
            size: stack.length,
            stackState: JSON.parse(JSON.stringify(stack)),
            line: 7,
            narration: `Popped top element (${popped.val}). Minimum automatically restored to ${minVal}.`
          });
        } else if (item === "getMin") {
          const minVal = stack.length > 0 ? stack[stack.length - 1].min : "null";
          steps.push({
            op: "getMin()",
            top: stack.length > 0 ? stack[stack.length - 1].val : "null",
            min: minVal,
            size: stack.length,
            stackState: JSON.parse(JSON.stringify(stack)),
            line: 8,
            narration: `Query getMin(): O(1) read from top of stack returns ${minVal}.`
          });
        } else if (item === "top") {
          const topVal = stack.length > 0 ? stack[stack.length - 1].val : "null";
          steps.push({
            op: "top()",
            top: topVal,
            min: stack.length > 0 ? stack[stack.length - 1].min : "null",
            size: stack.length,
            stackState: JSON.parse(JSON.stringify(stack)),
            line: 2,
            narration: `Query top(): Returns ${topVal}.`
          });
        }
      }
    }

    function loadPreset(ops) {
      document.getElementById('custom-input').value = ops.join(', ');
      initialOps = ops;
      stepperReset();
      buildSteps(initialOps);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => s.trim()).filter(s => s.length > 0);
      if (arr.length > 0) {
        initialOps = arr;
        stepperReset();
        buildSteps(initialOps);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-op').textContent = step.op;
      document.getElementById('metric-top').textContent = step.top;
      document.getElementById('metric-min').textContent = step.min;
      document.getElementById('metric-size').textContent = step.size;
""",
            'js_render_visual': """
      const container = document.getElementById('minstack-visual');
      container.innerHTML = '';
      if (step.stackState.length === 0) {
        container.innerHTML = '<span class="text-xs font-mono text-slate-500 self-center">Stack Empty</span>';
      } else {
        step.stackState.forEach((node, idx) => {
          const isTop = (idx === step.stackState.length - 1);
          const el = document.createElement('div');
          el.className = `w-56 p-2 rounded-lg border flex items-center justify-between text-xs font-mono ${
            isTop ? 'border-emerald-500 bg-emerald-950/50 text-white font-bold' : 'border-slate-800 bg-slate-950 text-slate-400'
          }`;
          el.innerHTML = `
            <span>Val: <strong class="text-cyan-300">${node.val}</strong></span>
            <span>Min: <strong class="text-emerald-400">${node.min}</strong></span>
            ${isTop ? '<span class="px-1.5 py-0.5 rounded bg-emerald-500 text-slate-950 text-[9px]">TOP</span>' : ''}
          `;
          container.appendChild(el);
        });
      }
"""
        },
        'approaches': [
            {
                'name': "Two Parallel Stacks",
                'desc': "Use mainStack for values and minStack where minStack.top() is always the running min.",
                'time': "All operations O(1)",
                'space': "O(2N)",
                'code_kotlin': """class MinStack() {
    private val stack = ArrayDeque<Int>()
    private val minStack = ArrayDeque<Int>()

    fun push(`val`: Int) {
        stack.addLast(`val`)
        val currentMin = if (minStack.isEmpty()) `val` else minOf(`val`, minStack.last())
        minStack.addLast(currentMin)
    }

    fun pop() {
        stack.removeLast()
        minStack.removeLast()
    }

    fun top(): Int = stack.last()

    fun getMin(): Int = minStack.last()
}""",
                'notes_kotlin': "`ArrayDeque<Int>` in Kotlin is high-performance, non-synchronized, and cache-friendly.",
                'code_python': """class MinStack:
    def __init__(self):
        self.stack = []
        self.min_stack = []

    def push(self, val: int) -> None:
        self.stack.append(val)
        current_min = val if not self.min_stack else min(val, self.min_stack[-1])
        self.min_stack.append(current_min)

    def pop(self) -> None:
        self.stack.pop()
        self.min_stack.pop()

    def top(self) -> int:
        return self.stack[-1]

    def getMin(self) -> int:
        return self.min_stack[-1]""",
                'notes_python': "Two parallel Python lists. Extremely simple and clean for a 5-minute phone screen solution.",
                'code_java': """import java.util.ArrayDeque;
import java.util.Deque;

class MinStack {
    private Deque<Integer> stack = new ArrayDeque<>();
    private Deque<Integer> minStack = new ArrayDeque<>();

    public MinStack() {}

    public void push(int val) {
        stack.push(val);
        int currentMin = minStack.isEmpty() ? val : Math.min(val, minStack.peek());
        minStack.push(currentMin);
    }

    public void pop() {
        stack.pop();
        minStack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}""",
                'notes_java': "Use `ArrayDeque` rather than `java.util.Stack`. Stack inherits from Vector and has synchronization overhead."
            },
            {
                'name': "Single Stack with Pair Object",
                'desc': "Store an internal Node/Pair holding (value, currentMin) at each level.",
                'time': "All operations O(1)",
                'space': "O(N)",
                'code_kotlin': """class MinStack() {
    private data class Element(val value: Int, val min: Int)
    private val stack = ArrayDeque<Element>()

    fun push(`val`: Int) {
        val currentMin = if (stack.isEmpty()) `val` else minOf(`val`, stack.last().min)
        stack.addLast(Element(`val`, currentMin))
    }

    fun pop() {
        stack.removeLast()
    }

    fun top(): Int = stack.last().value

    fun getMin(): Int = stack.last().min
}""",
                'notes_kotlin': "Kotlin `data class` keeps value and min tightly coupled, eliminating synchronization bugs between two separate stacks.",
                'code_python': """class MinStack:
    def __init__(self):
        self.stack = []  # stores (val, current_min)

    def push(self, val: int) -> None:
        current_min = val if not self.stack else min(val, self.stack[-1][1])
        self.stack.append((val, current_min))

    def pop(self) -> None:
        self.stack.pop()

    def top(self) -> int:
        return self.stack[-1][0]

    def getMin(self) -> int:
        return self.stack[-1][1]""",
                'notes_python': "Tuples `(val, current_min)` avoid creating a custom class in Python while maintaining 100% cohesion.",
                'code_java': """import java.util.ArrayDeque;
import java.util.Deque;

class MinStack {
    private static class Node {
        final int val;
        final int min;
        Node(int val, int min) {
            this.val = val;
            this.min = min;
        }
    }

    private Deque<Node> stack = new ArrayDeque<>();

    public void push(int val) {
        int currentMin = stack.isEmpty() ? val : Math.min(val, stack.peek().min);
        stack.push(new Node(val, currentMin));
    }

    public void pop() {
        stack.pop();
    }

    public int top() {
        return stack.peek().val;
    }

    public int getMin() {
        return stack.peek().min;
    }
}""",
                'notes_java': "Static nested Node class is the canonical enterprise Java pattern for encapsulated linked state."
            },
            {
                'name': "Optimal: Singly-Linked Head Pointer (Zero Collection Overhead)",
                'desc': "Custom singly linked list where head is the stack top and stores min.",
                'time': "All operations O(1)",
                'space': "O(N) Minimum Memory Footprint",
                'code_kotlin': """class MinStack() {
    private class Node(val value: Int, val min: Int, val next: Node?)
    private var head: Node? = null

    fun push(`val`: Int) {
        val currentMin = if (head == null) `val` else minOf(`val`, head!!.min)
        head = Node(`val`, currentMin, head)
    }

    fun pop() {
        head = head?.next
    }

    fun top(): Int = head!!.value

    fun getMin(): Int = head!!.min
}""",
                'notes_kotlin': "Bypasses standard collection frameworks entirely. Allocating a single Node per push with pointer linking gives the fastest runtime.",
                'code_python': """class MinStack:
    class Node:
        def __init__(self, val: int, min_val: int, next_node=None):
            self.val = val
            self.min = min_val
            self.next = next_node

    def __init__(self):
        self.head = None

    def push(self, val: int) -> None:
        current_min = val if self.head is None else min(val, self.head.min)
        self.head = self.Node(val, current_min, self.head)

    def pop(self) -> None:
        self.head = self.head.next

    def top(self) -> int:
        return self.head.val

    def getMin(self) -> int:
        return self.head.min""",
                'notes_python': "Custom linked node implementation demonstrates deep data structure mastery without relying on built-in collections.",
                'code_java': """class MinStack {
    private static class Node {
        int val;
        int min;
        Node next;
        Node(int val, int min, Node next) {
            this.val = val;
            this.min = min;
            this.next = next;
        }
    }

    private Node head = null;

    public void push(int val) {
        int currentMin = (head == null) ? val : Math.min(val, head.min);
        head = new Node(val, currentMin, head);
    }

    public void pop() {
        head = head.next;
    }

    public int top() {
        return head.val;
    }

    public int getMin() {
        return head.min;
    }
}""",
                'notes_java': "The production gold standard for low-overhead stack implementations. Zero array resizing or queue wrappers."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class MinStack() {
    private val stack = ArrayDeque<Pair<Int, Int>>()
    fun push(`val`: Int) {
        val m = if (stack.isEmpty()) `val` else minOf(`val`, stack.last().second)
        stack.addLast(Pair(`val`, m))
    }
    fun pop() { stack.removeLast() }
    fun top(): Int = stack.last().first
    fun getMin(): Int = stack.last().second
}""",
            'python_boilerplate': """class MinStack:
    def __init__(self):
        self.stack = []
    def push(self, val: int) -> None:
        m = val if not self.stack else min(val, self.stack[-1][1])
        self.stack.append((val, m))
    def pop(self) -> None:
        self.stack.pop()
    def top(self) -> int:
        return self.stack[-1][0]
    def getMin(self) -> int:
        return self.stack[-1][1]""",
            'java_boilerplate': """class MinStack {
    private java.util.Deque<int[]> s = new java.util.ArrayDeque<>();
    public void push(int val) {
        int m = s.isEmpty() ? val : Math.min(val, s.peek()[1]);
        s.push(new int[]{val, m});
    }
    public void pop() { s.pop(); }
    public int top() { return s.peek()[0]; }
    public int getMin() { return s.peek()[1]; }
}""",
            'js_test_runner': """
      // Test suite
      const ms = [];
      let allPassed = true;
      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';

      // Test 1: push(-2), push(0), push(-3), getMin -> -3, pop, top -> 0, getMin -> -2
      const push = (v) => {
        const m = ms.length === 0 ? v : Math.min(v, ms[ms.length - 1].min);
        ms.push({ val: v, min: m });
      };
      const pop = () => ms.pop();
      const top = () => ms[ms.length - 1].val;
      const getMin = () => ms[ms.length - 1].min;

      push(-2); push(0); push(-3);
      const t1 = (getMin() === -3);
      pop();
      const t2 = (top() === 0);
      const t3 = (getMin() === -2);

      const passed = t1 && t2 && t3;
      const row = document.createElement('div');
      row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
      row.innerHTML = `
        <span>Official Example 1 (push -2, 0, -3 &rarr; pop &rarr; getMin)</span>
        <span>${passed ? '&#10004; PASSED (min=-2)' : '&#10008; FAILED'}</span>
      `;
      resultsDiv.appendChild(row);

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
            'phase1': "I clarify: will pop, top, and getMin always be called on non-empty stacks? Yes, per constraints. What range of integers should we expect? Standard 32-bit signed integers. Must all operations strictly run in O(1) time? Yes, including getMin.",
            'phase2': "For space-time trade-off: a standard stack requires O(N) linear scan to find the minimum. To achieve O(1) time for getMin, we trade O(N) additional memory by maintaining a snapshot of the minimum element at each stack depth. We can do this with either a parallel minStack, a Node object containing (value, min), or a custom linked list.",
            'phase3': "I'll implement the custom Node approach. Each Node holds its value and the minimum at or below its position. For push(x), the new node's min is min(x, head.min). For pop(), we advance head to head.next. For top() and getMin(), we read head.value and head.min directly.",
            'phase4': "Dry run edge cases: pushing duplicates of the minimum: if we push -2, then -2 again, both nodes correctly record -2 as the minimum; popping one still leaves -2 as the minimum. Everything runs in strict O(1) time with zero array reallocation.",
            'senior_followup': "Senior Scalability: What if memory is extremely constrained and we cannot afford the 2x space overhead? We can use mathematical difference encoding: store (val - currentMin) as a 64-bit long in a single stack, tracking currentMin in a single variable. When popping a negative value, currentMin can be reconstructed in O(1) time without extra memory."
        },
        'quiz': [
            {
                'question': "Why is it impossible to track the minimum with a single integer variable `minVal` without auxiliary historical data?",
                'options': [
                    "Because integer variables in Java can overflow.",
                    "Because when the minimum element is popped, there is no way to know what the PREVIOUS minimum was without an O(N) scan.",
                    "Because stacks only store positive integers.",
                    "Because getMin() requires a return type of List."
                ],
                'correct_index': 1,
                'explanation': "If you push 2, then push 1 (minVal becomes 1), and then pop 1, you have lost the information that 2 was the previous minimum unless you stored the historical state."
            },
            {
                'question': "Why is `java.util.ArrayDeque` strongly preferred over `java.util.Stack` in Java interviews?",
                'options': [
                    "`java.util.Stack` inherits from `Vector`, which synchronizes every method call and adds unnecessary lock contention overhead.",
                    "`ArrayDeque` supports negative numbers while `Stack` does not.",
                    "`Stack` is deprecated in Java 8.",
                    "`ArrayDeque` is written in C++."
                ],
                'correct_index': 0,
                'explanation': "`Stack` is a legacy JDK 1.0 class that extends `Vector`. All its methods are synchronized, incurring thread-synchronization penalties even in single-threaded code. `ArrayDeque` is the modern, non-synchronized standard."
            }
        ]
    }
]

def main():
    print(f"Loaded {len(cluster2_problems)} problems in Cluster 2 so far...")

if __name__ == '__main__':
    main()

# Append 29, 30, 31, 32, 33
cluster2_problems.extend([
    # ---------------- 0029: ASTEROID COLLISION ----------------
    {
        'num': 29,
        'lc': 735,
        'title': "Asteroid Collision",
        'category': "Stacks, Queues & Monotonic Stacks",
        'difficulty': "Medium",
        'lc_slug': "asteroid-collision",
        'verbatim_statement': """<p>We are given an array <code>asteroids</code> of integers representing asteroids in a row.</p>
<p class="mt-2">For each asteroid, the absolute value represents its size, and the sign represents its direction (positive meaning right, negative meaning left). Each asteroid moves at the same speed.</p>
<p class="mt-2">Find out the state of the asteroids after all collisions. If two asteroids meet, the smaller one will explode. If both are the same size, both will explode. Two asteroids moving in the same direction will never meet.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'asteroids = [5,10,-5]', 'output': '[5,10] (10 and -5 collide resulting in 10; 5 and 10 never collide)'},
            {'title': "Example 2", 'input': 'asteroids = [8,-8]', 'output': '[] (Both explode)'},
            {'title': "Example 3", 'input': 'asteroids = [10,2,-5]', 'output': '[10] (2 and -5 collide -> -5; 10 and -5 collide -> 10)'}
        ],
        'constraints': [
            "2 <= asteroids.length <= 10^4",
            "-1000 <= asteroids[i] <= 1000",
            "asteroids[i] != 0"
        ],
        'plain_english': "Simulate moving asteroids. A collision only happens when a right-moving asteroid (positive) is followed by a left-moving asteroid (negative). Use a stack to hold surviving asteroids. When a negative asteroid arrives, compare it against positive asteroids on top of the stack: smaller explodes, equal both explode, larger destroys the stack top and keeps colliding.",
        'boundary_checks': {
            'min_base': "<code>[8, -8]</code> &rarr; Equal size opposite direction &rarr; Both explode &rarr; <code>[]</code>.",
            'best_exit': "All same sign <code>[1, 2, 3]</code> or moving apart <code>[-5, 5]</code> &rarr; Zero collisions.",
            'worst_scan': "Large negative asteroid <code>[1, 2, 3, -10]</code> &rarr; Negative asteroid destroys all previous stack elements."
        },
        'mental_model': {
            'analogy_title': "The Sumo Wrestling Tunnel",
            'analogy_text': "Imagine sumo wrestlers entering a narrow 1-lane tunnel. Wrestlers moving East (+) wait in line. When a Westbound (-) wrestler enters, they clash with the last Eastbound wrestler who entered (the top of the stack). The heavier wrestler wins and stays; equal weights eliminate each other. The Westbound wrestler continues fighting preceding Eastbound wrestlers until stopped, eliminated, or clearing the tunnel.",
            'aha_moment': "Collisions ONLY happen if `top > 0` and `incoming < 0`. If `top < 0` and `incoming > 0`, they are moving AWAY from each other and will never meet! Stack perfectly models this sequential LIFO clash.",
            'trigger': "Whenever sequential entities collide, cancel, or eliminate based on direction or matching pairs &rarr; Stack.",
            'sisters': [
                {'name': "Valid Parentheses", 'lc': "LC #20"},
                {'name': "Daily Temperatures", 'lc': "LC #739"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N^2) Time, O(N) Space",
            'naive_text': "Repeatedly scan the array for adjacent collision pairs (+, -), splice them out, and restart scanning from the beginning.",
            'wasted_work': "Re-scanning the entire prefix of the array after every single collision.",
            'optimal_complexity': "O(N) Time, O(N) Space",
            'optimal_text': "Process each asteroid once using a stack. Each asteroid is pushed at most once and popped at most once.",
            'core_invariant': "The stack contains only asteroids that can never collide with each other.",
            'table_rows': [
                {'name': "1. Repeated Array Rescan", 'time': "O(N^2)", 'space': "O(N)", 'note': "Splices array elements repeatedly."},
                {'name': "★ 2. Monotonic Collision Stack", 'time': "O(N)", 'space': "O(N)", 'note': "Each asteroid enters/exits stack once."}
            ]
        },
        'trace_table': {
            'input_desc': 'asteroids = [10, 2, -5]',
            'columns': ["Incoming", "Stack Before", "Comparison / Condition", "Action Taken", "Stack After"],
            'rows': [
                ["10", "[]", "Stack empty", "Push 10", "[10]"],
                ["2", "[10]", "top=10, incoming=2 (both > 0)", "Push 2", "[10, 2]"],
                ["-5", "[10, 2]", "top=2 > 0, incoming=-5: |-5| > 2", "Pop 2, incoming persists", "[10]"],
                ["-5", "[10]", "top=10 > 0, incoming=-5: 10 > |-5|", "Incoming -5 explodes", "[10]"]
            ],
            'takeaway': "Returns [10]. Intermediate asteroid 2 was popped and eliminated."
        },
        'stepper': {
            'title': "Sequential Directional Clash on LIFO Stack",
            'default_input': '10, 2, -5',
            'presets': [
                {'label': "[10, 2, -5]", 'onclick': 'loadPreset([10, 2, -5])'},
                {'label': "[5, 10, -5]", 'onclick': 'loadPreset([5, 10, -5])'},
                {'label': "[8, -8]", 'onclick': 'loadPreset([8, -8])'}
            ],
            'metrics': [
                {'id': "metric-incoming", 'label': "Incoming Asteroid", 'color': "text-amber-400", 'initial': "None"},
                {'id': "metric-top", 'label': "Stack Top", 'color': "text-cyan-400", 'initial': "Empty"},
                {'id': "metric-action", 'label': "Collision Result", 'color': "text-purple-400", 'initial': "Init"},
                {'id': "metric-survivors", 'label': "Survivors Count", 'color': "text-emerald-400", 'initial': "0"}
            ],
            'visual_html': '<div id="asteroid-track" class="w-full flex items-center justify-center gap-3 py-6 overflow-x-auto min-h-[100px]"></div>',
            'initial_narration': 'Click "Next Step" to trace sequential asteroid collisions.',
            'pseudocode_lines': [
                "val stack = ArrayDeque<Int>()",
                "for (ast in asteroids) {",
                "    var alive = true",
                "    while (alive && ast < 0 && stack.isNotEmpty() && stack.last() > 0) {",
                "        if (stack.last() <= -ast) { if (stack.last() == -ast) alive = false; stack.removeLast() }",
                "        else alive = false",
                "    }",
                "    if (alive) stack.addLast(ast)",
                "}",
                "return stack.toIntArray()"
            ],
            'js_code': """
    let initialData = [10, 2, -5];

    function buildSteps(arr) {
      steps = [];
      const stack = [];

      steps.push({
        incoming: "Init",
        top: "Empty",
        action: "Ready",
        stack: [],
        line: 1,
        narration: "Initialized empty survival stack."
      });

      for (let i = 0; i < arr.length; i++) {
        let ast = arr[i];
        let alive = true;

        while (alive && ast < 0 && stack.length > 0 && stack[stack.length - 1] > 0) {
          const topVal = stack[stack.length - 1];
          if (topVal < -ast) {
            stack.pop();
            steps.push({
              incoming: ast,
              top: topVal,
              action: `Top (${topVal}) Exploded`,
              stack: [...stack],
              line: 5,
              narration: `Collision: Top ${topVal} clashed with incoming ${ast}. Top was smaller and exploded!`
            });
          } else if (topVal === -ast) {
            stack.pop();
            alive = false;
            steps.push({
              incoming: ast,
              top: topVal,
              action: `Both Exploded!`,
              stack: [...stack],
              line: 5,
              narration: `Mutual annihilation: Top ${topVal} and incoming ${ast} are equal in size. Both exploded!`
            });
          } else {
            alive = false;
            steps.push({
              incoming: ast,
              top: topVal,
              action: `Incoming Exploded`,
              stack: [...stack],
              line: 6,
              narration: `Collision: Incoming ${ast} clashed with larger top ${topVal}. Incoming was destroyed.`
            });
          }
        }

        if (alive) {
          stack.push(ast);
          steps.push({
            incoming: ast,
            top: ast,
            action: `Pushed ${ast}`,
            stack: [...stack],
            line: 8,
            narration: `Asteroid ${ast} survived and settled on the stack.`
          });
        }
      }

      steps.push({
        incoming: "Done",
        top: stack.length > 0 ? stack[stack.length - 1] : "Empty",
        action: "Finished",
        stack: [...stack],
        line: 10,
        narration: `Simulation complete! Remaining surviving asteroids: [${stack.join(', ')}].`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      initialData = arr;
      stepperReset();
      buildSteps(initialData);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n) && n !== 0);
      if (arr.length > 0) {
        initialData = arr;
        stepperReset();
        buildSteps(initialData);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-incoming').textContent = step.incoming;
      document.getElementById('metric-top').textContent = step.top;
      document.getElementById('metric-action').textContent = step.action;
      document.getElementById('metric-survivors').textContent = step.stack.length;
""",
            'js_render_visual': """
      const container = document.getElementById('asteroid-track');
      container.innerHTML = '';
      if (step.stack.length === 0) {
        container.innerHTML = '<span class="text-xs font-mono text-slate-500">Track empty (all asteroids exploded)</span>';
      } else {
        step.stack.forEach((ast, idx) => {
          const isRight = ast > 0;
          const card = document.createElement('div');
          card.className = `px-3 py-2 rounded-xl border flex items-center gap-1.5 font-mono text-xs font-bold ${
            isRight ? 'border-cyan-500 bg-cyan-950/40 text-cyan-300' : 'border-amber-500 bg-amber-950/40 text-amber-300'
          }`;
          card.innerHTML = `
            <span>${isRight ? '&#10132;' : '&#11013;'}</span>
            <span>${ast}</span>
          `;
          container.appendChild(card);
        });
      }
"""
        },
        'approaches': [
            {
                'name': "Standard Stack Simulation",
                'desc': "Use Stack/ArrayDeque to resolve collisions sequentially.",
                'time': "O(N)",
                'space': "O(N)",
                'code_kotlin': """class Solution {
    fun asteroidCollision(asteroids: IntArray): IntArray {
        val stack = ArrayDeque<Int>()
        for (ast in asteroids) {
            var alive = true
            while (alive && ast < 0 && stack.isNotEmpty() && stack.last() > 0) {
                val top = stack.last()
                if (top < -ast) {
                    stack.removeLast()
                } else if (top == -ast) {
                    stack.removeLast()
                    alive = false
                } else {
                    alive = false
                }
            }
            if (alive) {
                stack.addLast(ast)
            }
        }
        return stack.toIntArray()
    }
}""",
                'notes_kotlin': "`stack.toIntArray()` produces primitive `int[]` in Java runtime with zero wrapper overhead.",
                'code_python': """class Solution:
    def asteroidCollision(self, asteroids: list[int]) -> list[int]:
        stack = []
        for ast in asteroids:
            alive = True
            while alive and ast < 0 and stack and stack[-1] > 0:
                if stack[-1] < -ast:
                    stack.pop()
                elif stack[-1] == -ast:
                    stack.pop()
                    alive = False
                else:
                    alive = False
            if alive:
                stack.append(ast)
        return stack""",
                'notes_python': "Using standard list as stack with `append()` and `pop()` runs at pure C speed in CPython.",
                'code_java': """import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int ast : asteroids) {
            boolean alive = true;
            while (alive && ast < 0 && !stack.isEmpty() && stack.peek() > 0) {
                if (stack.peek() < -ast) {
                    stack.pop();
                } else if (stack.peek() == -ast) {
                    stack.pop();
                    alive = false;
                } else {
                    alive = false;
                }
            }
            if (alive) {
                stack.push(ast);
            }
        }
        int[] result = new int[stack.size()];
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = stack.pop();
        }
        return result;
    }
}""",
                'notes_java': "Since ArrayDeque pop() pops from the front, reversing into `result[i]` correctly maintains chronological order."
            },
            {
                'name': "Better: Two-Pointer In-Place Array Stack",
                'desc': "Use the input array itself as the stack storage via top pointer.",
                'time': "O(N)",
                'space': "O(1) Auxiliary Space",
                'code_kotlin': """class Solution {
    fun asteroidCollision(asteroids: IntArray): IntArray {
        var top = -1
        for (ast in asteroids) {
            var alive = true
            while (alive && ast < 0 && top >= 0 && asteroids[top] > 0) {
                if (asteroids[top] < -ast) {
                    top--
                } else if (asteroids[top] == -ast) {
                    top--
                    alive = false
                } else {
                    alive = false
                }
            }
            if (alive) {
                asteroids[++top] = ast
            }
        }
        return asteroids.copyOfRange(0, top + 1)
    }
}""",
                'notes_kotlin': "Uses the input array itself as the stack buffer with `top` index tracker, yielding O(1) auxiliary space.",
                'code_python': """class Solution:
    def asteroidCollision(self, asteroids: list[int]) -> list[int]:
        top = -1
        for ast in asteroids:
            alive = True
            while alive and ast < 0 and top >= 0 and asteroids[top] > 0:
                if asteroids[top] < -ast:
                    top -= 1
                elif asteroids[top] == -ast:
                    top -= 1
                    alive = False
                else:
                    alive = False
            if alive:
                top += 1
                asteroids[top] = ast
        return asteroids[:top + 1]""",
                'notes_python': "Slices `asteroids[:top + 1]` at the end. Zero additional data structures required.",
                'code_java': """import java.util.Arrays;

class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        int top = -1;
        for (int ast : asteroids) {
            boolean alive = true;
            while (alive && ast < 0 && top >= 0 && asteroids[top] > 0) {
                if (asteroids[top] < -ast) {
                    top--;
                } else if (asteroids[top] == -ast) {
                    top--;
                    alive = false;
                } else {
                    alive = false;
                }
            }
            if (alive) {
                asteroids[++top] = ast;
            }
        }
        return Arrays.copyOfRange(asteroids, 0, top + 1);
    }
}""",
                'notes_java': "`Arrays.copyOfRange` outputs the compacted survivor array without extra wrapper structures."
            },
            {
                'name': "Optimal: Clean Production ArrayDeque",
                'desc': "Production gold standard with robust readability and defensive edge checks.",
                'time': "O(N)",
                'space': "O(N)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: Monotonic Collision Stack
     * Time: O(N) - Each asteroid is pushed and popped at most once
     * Space: O(N) - Survivor storage
     */
    fun asteroidCollision(asteroids: IntArray): IntArray {
        val stack = ArrayDeque<Int>()

        for (ast in asteroids) {
            var destroyed = false
            while (stack.isNotEmpty() && stack.last() > 0 && ast < 0) {
                val top = stack.last()
                val diff = top + ast // ast is negative, so diff compares magnitudes
                when {
                    diff < 0 -> stack.removeLast() // top destroyed, incoming keeps going
                    diff == 0 -> { stack.removeLast(); destroyed = true; break } // both destroyed
                    else -> { destroyed = true; break } // incoming destroyed
                }
            }
            if (!destroyed) {
                stack.addLast(ast)
            }
        }

        return stack.toIntArray()
    }
}""",
                'notes_kotlin': "Using `when` with `diff = top + ast` is idiomatic Kotlin: if `diff < 0`, incoming magnitude is larger; if `diff == 0`, equal.",
                'code_python': """class Solution:
    # Optimal: Collision Stack with Magnitude Delta
    # Time: O(N) - Amortized linear pass
    # Space: O(N) - Survivors
    def asteroidCollision(self, asteroids: list[int]) -> list[int]:
        stack = []
        for ast in asteroids:
            while stack and stack[-1] > 0 and ast < 0:
                diff = stack[-1] + ast
                if diff < 0:
                    stack.pop()
                elif diff == 0:
                    stack.pop()
                    break
                else:
                    break
            else:
                stack.append(ast)
        return stack""",
                'notes_python': "Utilizes Python's `while...else` construct: the `else` block executes only if the while loop was NOT exited via `break`!",
                'code_java': """import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    /**
     * Optimal: Collision Stack with Magnitude Delta
     * Time: O(N) - Linear pass
     * Space: O(N) - Survivor storage
     */
    public int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (int ast : asteroids) {
            boolean exploded = false;
            while (!stack.isEmpty() && stack.peekLast() > 0 && ast < 0) {
                int diff = stack.peekLast() + ast;
                if (diff < 0) {
                    stack.pollLast(); // top destroyed
                } else if (diff == 0) {
                    stack.pollLast(); // both destroyed
                    exploded = true;
                    break;
                } else {
                    exploded = true; // incoming destroyed
                    break;
                }
            }
            if (!exploded) {
                stack.addLast(ast);
            }
        }

        int[] result = new int[stack.size()];
        int idx = 0;
        for (int val : stack) {
            result[idx++] = val;
        }
        return result;
    }
}""",
                'notes_java': "Iterating directly through ArrayDeque with `for (int val : stack)` preserves first-to-last insertion order."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun asteroidCollision(asteroids: IntArray): IntArray {
        val stack = ArrayDeque<Int>()
        for (ast in asteroids) {
            var alive = true
            while (alive && ast < 0 && stack.isNotEmpty() && stack.last() > 0) {
                if (stack.last() < -ast) stack.removeLast()
                else if (stack.last() == -ast) { stack.removeLast(); alive = false }
                else alive = false
            }
            if (alive) stack.addLast(ast)
        }
        return stack.toIntArray()
    }
}""",
            'python_boilerplate': """class Solution:
    def asteroidCollision(self, asteroids: list[int]) -> list[int]:
        stack = []
        for ast in asteroids:
            alive = True
            while alive and ast < 0 and stack and stack[-1] > 0:
                if stack[-1] < -ast: stack.pop()
                elif stack[-1] == -ast: stack.pop(); alive = False
                else: alive = False
            if alive: stack.append(ast)
        return stack""",
            'java_boilerplate': """class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        java.util.ArrayDeque<Integer> s = new java.util.ArrayDeque<>();
        for (int a : asteroids) {
            boolean alive = true;
            while (alive && a < 0 && !s.isEmpty() && s.peekLast() > 0) {
                if (s.peekLast() < -a) s.pollLast();
                else if (s.peekLast() == -a) { s.pollLast(); alive = false; }
                else alive = false;
            }
            if (alive) s.addLast(a);
        }
        int[] res = new int[s.size()];
        int idx = 0;
        for (int v : s) res[idx++] = v;
        return res;
    }
}""",
            'js_test_runner': """
      const testCases = [
        { input: [5,10,-5], expected: [5,10] },
        { input: [8,-8], expected: [] },
        { input: [10,2,-5], expected: [10] },
        { input: [-2,-1,1,2], expected: [-2,-1,1,2] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        const s = [];
        for (let a of tc.input) {
          let alive = true;
          while (alive && a < 0 && s.length > 0 && s[s.length - 1] > 0) {
            if (s[s.length - 1] < -a) s.pop();
            else if (s[s.length - 1] === -a) { s.pop(); alive = false; }
            else alive = false;
          }
          if (alive) s.push(a);
        }

        const passed = (s.length === tc.expected.length) && s.every((v, i) => v === tc.expected[i]);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: input=[${tc.input.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED ([' + s.join(',') + '])' : '&#10008; FAILED (got [' + s.join(',') + '], expected [' + tc.expected.join(',') + '])'}</span>
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
            'phase1': "I clarify: can asteroids have speed differences? No, all move at identical speeds. When do two asteroids collide? Strictly when a positive asteroid is on the left of a negative asteroid. Two asteroids moving apart (e.g. [-2, 2]) will never collide.",
            'phase2': "For complexity, repeatedly scanning the array to erase adjacent pairs is O(N^2). We can achieve O(N) time and O(N) space using a stack. We iterate through asteroids. For each asteroid, as long as it's negative and the stack top is positive, we resolve collisions against the stack top until the incoming asteroid is destroyed, destroys the top, or they mutually annihilate.",
            'phase3': "I maintain an ArrayDeque<Int>. For each asteroid, I set alive = true. In a while loop, while alive, ast < 0, and stack top > 0: if stack.top < |ast|, we pop the top and continue. If stack.top == |ast|, we pop top and set alive = false. Otherwise, alive = false. If alive remains true, we push ast onto the stack.",
            'phase4': "Dry run edge cases: with [-2, -1, 1, 2], all negative asteroids are pushed first without collision, then positive ones are pushed, returns [-2, -1, 1, 2]. With [8, -8], both pop, returns empty array. Each asteroid is pushed and popped at most once.",
            'senior_followup': "Senior Scalability: What if asteroids have variable velocities $v_i$? Then collisions become event-driven: we compute intersection timestamps $t = (x_2 - x_1) / (v_1 - v_2)$ for adjacent pairs and use a Min-Heap / PriorityQueue simulation to resolve collisions in chronological order."
        },
        'quiz': [
            {
                'question': "Why do the asteroids `[-5, 5]` NEVER collide?",
                'options': [
                    "Because negative numbers cannot be compared to positive numbers.",
                    "`-5` is moving left (west) and `5` is moving right (east); they are moving away from each other.",
                    "Because equal sizes cancel each other out.",
                    "Because stack operations require positive numbers."
                ],
                'correct_index': 1,
                'explanation': "The first asteroid at index 0 is moving left, and the second at index 1 is moving right. Their distance increases over time, so they can never intersect."
            },
            {
                'question': "What is the amortized time complexity of the stack-based solution for an array of size N?",
                'options': [
                    "O(N^2) because of the inner while loop.",
                    "O(N log N) because of size comparisons.",
                    "O(N) because each of the N asteroids is pushed at most once and popped at most once.",
                    "O(1) constant time."
                ],
                'correct_index': 2,
                'explanation': "Even though an inner while loop exists, each element is pushed to the stack once and can only be popped from the stack once. Total operations across the entire array cannot exceed 2N."
            }
        ]
    },

    # ---------------- 0030: DAILY TEMPERATURES ----------------
    {
        'num': 30,
        'lc': 739,
        'title': "Daily Temperatures",
        'category': "Stacks, Queues & Monotonic Stacks",
        'difficulty': "Medium",
        'lc_slug': "daily-temperatures",
        'verbatim_statement': """<p>Given an array of integers <code>temperatures</code> represents the daily temperatures, return <em>an array</em> <code>answer</code> <em>such that</em> <code>answer[i]</code> <em>is the number of days you have to wait after the</em> <code>i<sup>th</sup></code> <em>day to get a warmer temperature</em>. If there is no future day for which this is possible, keep <code>answer[i] == 0</code> instead.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'temperatures = [73,74,75,71,69,72,76,73]', 'output': '[1,1,4,2,1,1,0,0]'},
            {'title': "Example 2", 'input': 'temperatures = [30,40,50,60]', 'output': '[1,1,1,0]'},
            {'title': "Example 3", 'input': 'temperatures = [30,60,90]', 'output': '[1,1,0]'}
        ],
        'constraints': [
            "1 <= temperatures.length <= 10^5",
            "30 <= temperatures[i] <= 100"
        ],
        'plain_english': "For each day, find how many days until the next strictly warmer temperature. Use a Monotonic Decreasing Stack storing indices. When a warmer day arrives, it resolves and pops all colder days sitting on the stack, computing their wait duration as `currentDay - poppedDay`.",
        'boundary_checks': {
            'min_base': "<code>temperatures = [50]</code> &rarr; Single day, no future warmer day &rarr; <code>[0]</code>.",
            'best_exit': "Strictly increasing <code>[30, 40, 50, 60]</code> &rarr; Each day resolved by the very next day &rarr; <code>[1, 1, 1, 0]</code>.",
            'worst_scan': "Strictly decreasing <code>[100, 90, 80, 70]</code> &rarr; Nothing is resolved &rarr; <code>[0, 0, 0, 0]</code>."
        },
        'mental_model': {
            'analogy_title': "The Unresolved Cold Tickets Line",
            'analogy_text': "Imagine a ticket desk where people wait for warmer weather. Each person holds a ticket with their arrival day and temperature. If today is colder than the person before you, you join the waiting queue behind them. But if today is warmer, you tap the person ahead of you: 'Hey, I'm warmer than you!' They calculate their wait time (`today - ticketDay`), cash out, and leave. You repeat this for anyone else behind them who is colder than today.",
            'aha_moment': "A Monotonic Decreasing Stack maintains unresolved days in decreasing temperature order. The moment a day with temperature > stack.top arrives, it resolves that waiting day instantly.",
            'trigger': "Whenever asked to find the 'next greater element' or 'days until greater' &rarr; Monotonic Decreasing Stack.",
            'sisters': [
                {'name': "Next Greater Element I", 'lc': "LC #496"},
                {'name': "Online Stock Span", 'lc': "LC #901"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N^2) Time, O(1) Auxiliary Space",
            'naive_text': "For each index i, run an inner loop forward looking for the first j such that temp[j] > temp[i]. For N = 100,000, requires 5 billion comparisons, triggering Time Limit Exceeded.",
            'wasted_work': "Re-scanning cold days repeatedly that have already been examined by previous iterations.",
            'optimal_complexity': "O(N) Time, O(N) Auxiliary Space",
            'optimal_text': "Single pass with a stack storing indices. Every index is pushed once and popped once when its warmer day arrives.",
            'core_invariant': "Temperatures corresponding to indices in the stack are in strictly non-increasing order from bottom to top.",
            'table_rows': [
                {'name': "1. Brute Force Forward Scan", 'time': "O(N^2)", 'space': "O(1)", 'note': "Nested loop scanning. TLE on N=10^5."},
                {'name': "★ 2. Monotonic Decreasing Stack", 'time': "O(N)", 'space': "O(N)", 'note': "Linear amortized pass. Industry standard."},
                {'name': "3. Array Memoized Backward Hop", 'time': "O(N)", 'space': "O(1)", 'note': "Reverse scan hopping via answer array."}
            ]
        },
        'trace_table': {
            'input_desc': 'temperatures = [73, 74, 75, 71, 69, 72, 76, 73]',
            'columns': ["Day (Temp)", "Stack Before (Indices)", "Resolution / Pops", "Stack After", "Answer Array State"],
            'rows': [
                ["0 (73)", "[]", "None", "[0]", "[0, 0, 0, 0, 0, 0, 0, 0]"],
                ["1 (74)", "[0]", "74 > 73 &rarr; ans[0] = 1 - 0 = 1", "[1]", "[1, 0, 0, 0, 0, 0, 0, 0]"],
                ["2 (75)", "[1]", "75 > 74 &rarr; ans[1] = 2 - 1 = 1", "[2]", "[1, 1, 0, 0, 0, 0, 0, 0]"],
                ["3 (71)", "[2]", "71 < 75 &rarr; Push", "[2, 3]", "[1, 1, 0, 0, 0, 0, 0, 0]"],
                ["4 (69)", "[2, 3]", "69 < 71 &rarr; Push", "[2, 3, 4]", "[1, 1, 0, 0, 0, 0, 0, 0]"],
                ["5 (72)", "[2, 3, 4]", "72 > 69 &rarr; ans[4]=1; 72 > 71 &rarr; ans[3]=2", "[2, 5]", "[1, 1, 0, 2, 1, 0, 0, 0]"],
                ["6 (76)", "[2, 5]", "76 > 72 &rarr; ans[5]=1; 76 > 75 &rarr; ans[2]=4", "[6]", "[1, 1, 4, 2, 1, 1, 0, 0]"],
                ["7 (73)", "[6]", "73 < 76 &rarr; Push", "[6, 7]", "[1, 1, 4, 2, 1, 1, 0, 0]"]
            ],
            'takeaway': "Indices 6 and 7 remain on stack at end &rarr; their answer defaults to 0."
        },
        'stepper': {
            'title': "Monotonic Decreasing Stack Resolution Stepper",
            'default_input': '73, 74, 75, 71, 69, 72, 76, 73',
            'presets': [
                {'label': "LeetCode 8 Days", 'onclick': 'loadPreset([73, 74, 75, 71, 69, 72, 76, 73])'},
                {'label': "Strictly Increasing", 'onclick': 'loadPreset([30, 40, 50, 60])'},
                {'label': "Strictly Decreasing", 'onclick': 'loadPreset([80, 70, 60, 50])'}
            ],
            'metrics': [
                {'id': "metric-day", 'label': "Current Day", 'color': "text-amber-400", 'initial': "0 (73°)"},
                {'id': "metric-stack", 'label': "Stack (Indices)", 'color': "text-cyan-400", 'initial': "[]"},
                {'id': "metric-resolved", 'label': "Days Resolved", 'color': "text-emerald-400", 'initial': "None"},
                {'id': "metric-ans", 'label': "Current Answer", 'color': "text-purple-400", 'initial': "[]"}
            ],
            'visual_html': '<div id="temperatures-stage" class="w-full flex items-end justify-center gap-2 h-44 border-b border-slate-800 pb-2"></div>',
            'initial_narration': 'Click "Next Step" to trace monotonic stack resolution.',
            'pseudocode_lines': [
                "val ans = IntArray(temperatures.size)",
                "val stack = ArrayDeque<Int>() // stores indices",
                "for (i in temperatures.indices) {",
                "    while (stack.isNotEmpty() && temperatures[i] > temperatures[stack.last()]) {",
                "        val prevIndex = stack.removeLast()",
                "        ans[prevIndex] = i - prevIndex",
                "    }",
                "    stack.addLast(i)",
                "}",
                "return ans"
            ],
            'js_code': """
    let initialData = [73, 74, 75, 71, 69, 72, 76, 73];

    function buildSteps(temps) {
      steps = [];
      const stack = [];
      const ans = new Array(temps.length).fill(0);

      steps.push({
        day: "Init",
        stack: [],
        resolved: "None",
        ans: [...ans],
        activeIdx: -1,
        line: 1,
        narration: "Initialized answer array with 0s and empty index stack."
      });

      for (let i = 0; i < temps.length; i++) {
        const curr = temps[i];
        let resolvedInfo = [];

        while (stack.length > 0 && curr > temps[stack[stack.length - 1]]) {
          const prevIdx = stack.pop();
          ans[prevIdx] = i - prevIdx;
          resolvedInfo.push(`Day ${prevIdx} waited ${i - prevIdx}d`);

          steps.push({
            day: `Day ${i} (${curr}°)`,
            stack: [...stack],
            resolved: `Day ${prevIdx} (${temps[prevIdx]}°) &rarr; waited ${i - prevIdx}d`,
            ans: [...ans],
            activeIdx: i,
            poppedIdx: prevIdx,
            line: 5,
            narration: `Day ${i} (${curr}°) is warmer than Day ${prevIdx} (${temps[prevIdx]}°). Resolved Day ${prevIdx} wait = ${i - prevIdx} days.`
          });
        }

        stack.push(i);
        steps.push({
          day: `Day ${i} (${curr}°)`,
          stack: [...stack],
          resolved: resolvedInfo.length > 0 ? resolvedInfo.join('; ') : "Pushed to stack",
          ans: [...ans],
          activeIdx: i,
          poppedIdx: -1,
          line: 8,
          narration: `Pushed Day ${i} to stack. Stack now stores unresolved indices [${stack.join(', ')}].`
        });
      }

      steps.push({
        day: "Done",
        stack: [...stack],
        resolved: "Finished",
        ans: [...ans],
        activeIdx: -1,
        poppedIdx: -1,
        line: 10,
        narration: `Complete! Final answer: [${ans.join(', ')}]. Remaining stack days have no warmer future day (0).`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = arr.join(', ');
      initialData = arr;
      stepperReset();
      buildSteps(initialData);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        initialData = arr;
        stepperReset();
        buildSteps(initialData);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-day').textContent = step.day;
      document.getElementById('metric-stack').textContent = `[${step.stack.join(', ')}]`;
      document.getElementById('metric-resolved').textContent = step.resolved;
      document.getElementById('metric-ans').textContent = `[${step.ans.join(', ')}]`;
""",
            'js_render_visual': """
      const container = document.getElementById('temperatures-stage');
      container.innerHTML = '';
      const minTemp = Math.min(...initialData);
      const maxTemp = Math.max(...initialData);
      const range = Math.max(maxTemp - minTemp, 1);

      initialData.forEach((temp, idx) => {
        const isCurrent = (idx === step.activeIdx);
        const isPopped = (idx === step.poppedIdx);
        const isOnStack = step.stack.includes(idx);
        const heightPct = Math.round(((temp - minTemp + 5) / (range + 10)) * 100);

        let barCls = "bg-slate-800 text-slate-400";
        if (isCurrent) barCls = "bg-amber-400 text-slate-950 font-bold";
        else if (isPopped) barCls = "bg-emerald-500 text-slate-950 font-bold animate-pulse";
        else if (isOnStack) barCls = "bg-cyan-500/80 text-cyan-200 border border-cyan-400";

        const col = document.createElement('div');
        col.className = 'flex flex-col items-center justify-end h-full w-9';
        col.innerHTML = `
          <span class="text-[9px] font-mono font-bold mb-1 ${isCurrent ? 'text-amber-400' : 'text-slate-400'}">${temp}°</span>
          <div class="w-full ${barCls} rounded-t transition-all duration-300" style="height: ${heightPct}%;"></div>
          <span class="text-[9px] font-mono text-slate-500 mt-1">${idx}</span>
          <span class="text-[8px] font-mono text-emerald-400 font-bold">${step.ans[idx] > 0 ? step.ans[idx] + 'd' : ''}</span>
        `;
        container.appendChild(col);
      });
"""
        },
        'approaches': [
            {
                'name': "Brute Force Nested Loops",
                'desc': "For each day, linearly scan all future days until finding a warmer temperature.",
                'time': "O(N^2)",
                'space': "O(1) Auxiliary Space",
                'code_kotlin': """class Solution {
    fun dailyTemperatures(temperatures: IntArray): IntArray {
        val n = temperatures.size
        val result = IntArray(n)
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                if (temperatures[j] > temperatures[i]) {
                    result[i] = j - i
                    break
                }
            }
        }
        return result
    }
}""",
                'notes_kotlin': "Quadratic complexity O(N^2). Times out on N = 100,000.",
                'code_python': """class Solution:
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        n = len(temperatures)
        ans = [0] * n
        for i in range(n):
            for j in range(i + 1, n):
                if temperatures[j] > temperatures[i]:
                    ans[i] = j - i
                    break
        return ans""",
                'notes_python': "Nested loop is too slow for production constraints; triggers Time Limit Exceeded.",
                'code_java': """class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (temperatures[j] > temperatures[i]) {
                    ans[i] = j - i;
                    break;
                }
            }
        }
        return ans;
    }
}""",
                'notes_java': "Simple brute force baseline to present during the initial 45 seconds of the interview."
            },
            {
                'name': "Optimal: Monotonic Decreasing Stack (Indices)",
                'desc': "Store indices in a decreasing stack; pop and resolve days when warmer day arrives.",
                'time': "O(N)",
                'space': "O(N)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: Monotonic Decreasing Stack
     * Time: O(N) - Each index pushed and popped at most once
     * Space: O(N) - Stack stores unresolved indices
     */
    fun dailyTemperatures(temperatures: IntArray): IntArray {
        val n = temperatures.size
        val result = IntArray(n)
        val stack = ArrayDeque<Int>() // stores indices

        for (i in 0 until n) {
            val currentTemp = temperatures[i]
            while (stack.isNotEmpty() && currentTemp > temperatures[stack.last()]) {
                val prevDay = stack.removeLast()
                result[prevDay] = i - prevDay
            }
            stack.addLast(i)
        }

        return result
    }
}""",
                'notes_kotlin': "Stores primitive integer indices in `ArrayDeque<Int>`, running in linear amortized time.",
                'code_python': """class Solution:
    # Optimal: Monotonic Decreasing Stack
    # Time: O(N) - Linear pass
    # Space: O(N) - Stack memory
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        n = len(temperatures)
        ans = [0] * n
        stack = []  # stores indices
        
        for i, temp in enumerate(temperatures):
            while stack and temp > temperatures[stack[-1]]:
                prev_day = stack.pop()
                ans[prev_day] = i - prev_day
            stack.append(i)
            
        return ans""",
                'notes_python': "The canonical Python solution. `enumerate` provides index and value cleanly.",
                'code_java': """import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    /**
     * Optimal: Monotonic Decreasing Stack
     * Time: O(N) - Exactly N iterations
     * Space: O(N) - Stack for indices
     */
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prevDay = stack.pop();
                result[prevDay] = i - prevDay;
            }
            stack.push(i);
        }

        return result;
    }
}""",
                'notes_java': "The production gold standard. `result` automatically initializes to 0 in Java."
            },
            {
                'name': "Better: Backward Traversal with Answer Array Hopping",
                'desc': "Iterate backward; jump ahead using already-calculated answers.",
                'time': "O(N)",
                'space': "O(1) Auxiliary Space",
                'code_kotlin': """class Solution {
    fun dailyTemperatures(temperatures: IntArray): IntArray {
        val n = temperatures.size
        val result = IntArray(n)

        for (i in n - 2 downTo 0) {
            var j = i + 1
            while (j < n) {
                if (temperatures[j] > temperatures[i]) {
                    result[i] = j - i
                    break
                } else if (result[j] == 0) {
                    break // No warmer day after j exists
                } else {
                    j += result[j] // Hop directly to next warmer day
                }
            }
        }
        return result
    }
}""",
                'notes_kotlin': "Hopping backward using `j += result[j]` avoids stack allocation entirely, achieving O(1) auxiliary space.",
                'code_python': """class Solution:
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        n = len(temperatures)
        ans = [0] * n
        
        for i in range(n - 2, -1, -1):
            j = i + 1
            while j < n:
                if temperatures[j] > temperatures[i]:
                    ans[i] = j - i
                    break
                elif ans[j] == 0:
                    break
                else:
                    j += ans[j]
                    
        return ans""",
                'notes_python': "Backward memoized hopping. Uses the output array as dynamic programming state.",
                'code_java': """class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];

        for (int i = n - 2; i >= 0; i--) {
            int j = i + 1;
            while (j < n) {
                if (temperatures[j] > temperatures[i]) {
                    result[i] = j - i;
                    break;
                } else if (result[j] == 0) {
                    break;
                } else {
                    j += result[j];
                }
            }
        }
        return result;
    }
}""",
                'notes_java': "Zero auxiliary data structure. Optimal memory footprint for embedded or memory-critical systems."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun dailyTemperatures(temperatures: IntArray): IntArray {
        val n = temperatures.size
        val res = IntArray(n)
        val stack = ArrayDeque<Int>()
        for (i in 0 until n) {
            while (stack.isNotEmpty() && temperatures[i] > temperatures[stack.last()]) {
                val prev = stack.removeLast()
                res[prev] = i - prev
            }
            stack.addLast(i)
        }
        return res
    }
}""",
            'python_boilerplate': """class Solution:
    def dailyTemperatures(self, temperatures: list[int]) -> list[int]:
        ans = [0] * len(temperatures)
        stack = []
        for i, t in enumerate(temperatures):
            while stack and t > temperatures[stack[-1]]:
                prev = stack.pop()
                ans[prev] = i - prev
            stack.append(i)
        return ans""",
            'java_boilerplate': """class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int[] res = new int[temperatures.length];
        java.util.Deque<Integer> s = new java.util.ArrayDeque<>();
        for (int i = 0; i < temperatures.length; i++) {
            while (!s.isEmpty() && temperatures[i] > temperatures[s.peek()]) {
                int p = s.pop();
                res[p] = i - p;
            }
            s.push(i);
        }
        return res;
    }
}""",
            'js_test_runner': """
      const testCases = [
        { input: [73,74,75,71,69,72,76,73], expected: [1,1,4,2,1,1,0,0] },
        { input: [30,40,50,60], expected: [1,1,1,0] },
        { input: [30,60,90], expected: [1,1,0] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        const res = new Array(tc.input.length).fill(0);
        const stack = [];
        for (let i = 0; i < tc.input.length; i++) {
          while (stack.length > 0 && tc.input[i] > tc.input[stack[stack.length - 1]]) {
            const prev = stack.pop();
            res[prev] = i - prev;
          }
          stack.push(i);
        }

        const passed = (res.length === tc.expected.length) && res.every((v, i) => v === tc.expected[i]);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: temps=[${tc.input.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED ([' + res.join(',') + '])' : '&#10008; FAILED (got [' + res.join(',') + '], expected [' + tc.expected.join(',') + '])'}</span>
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
            'phase1': "I clarify: if there is no warmer day in the future, should the answer be 0? Yes, per specification. What is the length of temperatures? Up to 10^5, so an O(N^2) brute force will definitely time out.",
            'phase2': "For space-time strategy, the naive approach takes O(N^2). This is a textbook Next Greater Element problem: we can use a Monotonic Decreasing Stack storing day indices. Because the stack maintains temperatures in decreasing order, when a warmer day arrives, it can resolve multiple colder days from the top of the stack in O(1) amortized time, achieving O(N) time and O(N) space.",
            'phase3': "I allocate result = IntArray(n) initialized to 0. I maintain stack = ArrayDeque<Int>(). As I iterate through index i from 0 to n - 1: while the stack is non-empty and temperatures[i] > temperatures[stack.top], I pop prevDay and record result[prevDay] = i - prevDay. Then I push index i onto the stack. At the end, I return result.",
            'phase4': "Dry running edge cases: with strictly decreasing temps [90, 80, 70], nothing pops, stack holds [0, 1, 2], result remains [0, 0, 0]. With strictly increasing temps [30, 40, 50], every day is popped on the next iteration, producing [1, 1, 0]. Total operations strictly bounded by 2N.",
            'senior_followup': "Senior Scalability: What if memory is critical and O(N) extra stack space is forbidden? We can iterate backward from N - 2 down to 0 and use the result array itself to hop forward: if temp[j] <= temp[i], we jump j += result[j]. If result[j] == 0, we immediately know no warmer day exists, achieving O(1) auxiliary space."
        },
        'quiz': [
            {
                'question': "Why must the stack store array INDICES rather than temperature values?",
                'options': [
                    "Because Java Collections cannot store temperatures.",
                    "Because we need to compute the elapsed days `i - prevIndex`, which requires the original index of the colder day.",
                    "Because indices take less memory than temperatures.",
                    "Because temperatures can be negative."
                ],
                'correct_index': 1,
                'explanation': "The question asks for the number of days you have to wait (`currentDayIndex - pastDayIndex`). Storing only the temperature value would lose the timestamp."
            },
            {
                'question': "What is the total number of push and pop operations across the entire array of size N?",
                'options': [
                    "Exactly N^2 operations.",
                    "At most 2N operations (each index is pushed once and popped at most once).",
                    "N log N operations.",
                    "Exactly N operations."
                ],
                'correct_index': 1,
                'explanation': "Every index from 0 to N-1 is pushed exactly once. Each index can be popped at most once. Hence, total operations cannot exceed 2N, proving O(N) amortized time."
            }
        ]
    },

    # ---------------- 0031: SIMPLE BANK SYSTEM ----------------
    {
        'num': 31,
        'lc': 2043,
        'title': "Simple Bank System",
        'category': "Multi-Tier OOP & Timed State Systems",
        'difficulty': "Medium",
        'lc_slug': "simple-bank-system",
        'verbatim_statement': """<p>You have been tasked with writing a program for a popular bank that will automate all its operations. The bank has <code>n</code> accounts numbered from <code>1</code> to <code>n</code>. The initial balance of each account is stored in a <strong>0-indexed</strong> integer array <code>balance</code> of size <code>n</code>, with the <code>(i + 1)<sup>th</sup></code> account having an initial balance of <code>balance[i]</code>.</p>
<p class="mt-2">Execute all the <strong>valid</strong> transactions. A transaction is <strong>valid</strong> if:</p>
<ul class="list-disc pl-5 mt-1 space-y-1 text-xs">
  <li>The given account number(s) are between <code>1</code> and <code>n</code>, and</li>
  <li>The amount of money withdrawn or transferred is <strong>less than or equal to</strong> the balance of the account.</li>
</ul>
<p class="mt-2">Implement the <code>Bank</code> class:</p>
<ul class="list-disc pl-5 mt-1 space-y-1 text-xs">
  <li><code>Bank(long[] balance)</code> Initializes the object with the 0-indexed integer array <code>balance</code>.</li>
  <li><code>boolean transfer(int account1, int account2, long money)</code> Transfers <code>money</code> dollars from the account numbered <code>account1</code> to <code>account2</code>. Return <code>true</code> if the transaction was successful, or <code>false</code> otherwise.</li>
  <li><code>boolean deposit(int account, long money)</code> Deposit <code>money</code> dollars into the account numbered <code>account</code>. Return <code>true</code> if the transaction was successful, or <code>false</code> otherwise.</li>
  <li><code>boolean withdraw(int account, long money)</code> Withdraw <code>money</code> dollars from the account numbered <code>account</code>. Return <code>true</code> if the transaction was successful, or <code>false</code> otherwise.</li>
</ul>""",
        'examples': [
            {'title': "Example 1", 'input': '["Bank","withdraw","transfer","deposit","transfer","withdraw"]\n[[[10,100,20,50,30]],[3,10],[5,1,20],[5,20],[3,4,15],[10,50]]', 'output': '[null,true,true,true,false,false]'}
        ],
        'constraints': [
            "n == balance.length",
            "1 <= n <= 10^5",
            "0 <= balance[i] <= 10^12",
            "1 <= account, account1, account2 <= n",
            "0 <= money <= 10^12",
            "At most 10^4 calls will be made to each function transfer, deposit, withdraw."
        ],
        'plain_english': "Design an OOP banking ledger class. Accounts are 1-indexed (account k maps to balance[k-1]). Validate account existence (1 <= account <= n) and balance sufficiency before updating. Return true on valid execution, false otherwise. Must handle 64-bit Long balances to avoid overflow.",
        'boundary_checks': {
            'min_base': "Invalid account <code>account > n</code> &rarr; Immediately return false without mutating state.",
            'best_exit': "Valid transfer with exact balance <code>balance[acc] == money</code> &rarr; Account balance becomes 0, return true.",
            'worst_scan': "10^12 money transactions &rarr; Requires 64-bit integers (Long in Kotlin/Java) to prevent arithmetic overflow."
        },
        'mental_model': {
            'analogy_title': "The Bank Teller Gatekeeper",
            'analogy_text': "Think of a bank teller with a ledger binder. For every request, the teller executes three mandatory security checks before touching cash: 1) Does the account number exist in the ledger? 2) Does the destination account exist? 3) Does the sender have enough funds? If any check fails, the teller rejects the slip immediately (returns false) with zero state alteration.",
            'aha_moment': "1-indexed accounts mapping to a 0-indexed LongArray. Encapsulating an `isValid(account)` helper method ensures clean code and eliminates off-by-one errors across deposit, withdraw, and transfer.",
            'trigger': "Whenever designing entity state management with transactional validations &rarr; Modular OOP with atomic validation guards.",
            'sisters': [
                {'name': "Design Parking System", 'lc': "LC #1603"},
                {'name': "Design Underground System", 'lc': "LC #1396"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(1) operations, but prone to integer overflow and off-by-one bugs",
            'naive_text': "Using standard 32-bit integers (overflowing at 2*10^9), or forgetting that account numbers are 1-indexed.",
            'wasted_work': "Re-writing account bounds checks redundantly inside every method instead of a unified validator.",
            'optimal_complexity': "All operations O(1) Time, O(N) Space",
            'optimal_text': "Direct 0-indexed LongArray storage with a private `isValidAccount(acc)` helper method.",
            'core_invariant': "Total money in the system is invariant during transfers; balances are non-negative at all times.",
            'table_rows': [
                {'name': "1. HashMap Storage", 'time': "O(1) average", 'space': "O(N) with boxing", 'note': "High memory overhead; boxing Long objects."},
                {'name': "★ 2. Direct LongArray + Helper Guards", 'time': "O(1) strict", 'space': "O(N) primitive", 'note': "Zero allocation, production standard."}
            ]
        },
        'trace_table': {
            'input_desc': 'Initial: [10, 100, 20, 50, 30]. Ops: withdraw(3, 10), transfer(5, 1, 20), deposit(5, 20), transfer(3, 4, 15), withdraw(10, 50)',
            'columns': ["Operation", "Validation Checks", "Mutation", "Balances After", "Result"],
            'rows': [
                ["withdraw(3, 10)", "Acc 3 valid, bal[2]=20 >= 10", "bal[2] -= 10", "[10, 100, 10, 50, 30]", "true"],
                ["transfer(5, 1, 20)", "Acc 5 & 1 valid, bal[4]=30 >= 20", "bal[4]-=20, bal[0]+=20", "[30, 100, 10, 50, 10]", "true"],
                ["deposit(5, 20)", "Acc 5 valid", "bal[4] += 20", "[30, 100, 10, 50, 30]", "true"],
                ["transfer(3, 4, 15)", "Acc 3 bal[2]=10 < 15", "None (insufficient funds)", "[30, 100, 10, 50, 30]", "false"],
                ["withdraw(10, 50)", "Acc 10 > 5 (invalid account)", "None (invalid account)", "[30, 100, 10, 50, 30]", "false"]
            ],
            'takeaway': "Failed transactions leave ledger balances 100% untouched."
        },
        'stepper': {
            'title': "Transactional Validation & Ledger State Stepper",
            'default_input': 'withdraw(3, 10), transfer(5, 1, 20), deposit(5, 20), transfer(3, 4, 15), withdraw(10, 50)',
            'presets': [
                {'label': "Example 1 Sequence", 'onclick': 'loadPreset(["withdraw(3, 10)", "transfer(5, 1, 20)", "deposit(5, 20)", "transfer(3, 4, 15)", "withdraw(10, 50)"])'},
                {'label': "Overdraft Rejection", 'onclick': 'loadPreset(["withdraw(1, 100)", "withdraw(1, 10)"])'},
                {'label': "Invalid Account", 'onclick': 'loadPreset(["deposit(99, 50)", "transfer(1, 99, 10)"])'}
            ],
            'metrics': [
                {'id': "metric-op", 'label': "Active Operation", 'color': "text-amber-400", 'initial': "Init"},
                {'id': "metric-valid", 'label': "Validation Status", 'color': "text-emerald-400", 'initial': "Pass"},
                {'id': "metric-res", 'label': "Return Value", 'color': "text-cyan-400", 'initial': "null"},
                {'id': "metric-total", 'label': "Total Bank Deposits", 'color': "text-purple-400", 'initial': "$210"}
            ],
            'visual_html': '<div id="ledger-accounts-container" class="w-full flex flex-wrap gap-2 justify-center items-center py-4"></div>',
            'initial_narration': 'Click "Next Step" to trace banking operations.',
            'pseudocode_lines': [
                "private fun isValid(acc: Int) = acc in 1..balance.size",
                "fun transfer(acc1: Int, acc2: Int, money: Long): Boolean {",
                "    if (!isValid(acc1) || !isValid(acc2) || balance[acc1 - 1] < money) return false",
                "    balance[acc1 - 1] -= money; balance[acc2 - 1] += money; return true",
                "}",
                "fun deposit(acc: Int, money: Long): Boolean {",
                "    if (!isValid(acc)) return false; balance[acc - 1] += money; return true",
                "}",
                "fun withdraw(acc: Int, money: Long): Boolean {",
                "    if (!isValid(acc) || balance[acc - 1] < money) return false",
                "    balance[acc - 1] -= money; return true",
                "}"
            ],
            'js_code': """
    let initialBalances = [10, 100, 20, 50, 30];
    let initialOps = ["withdraw(3, 10)", "transfer(5, 1, 20)", "deposit(5, 20)", "transfer(3, 4, 15)", "withdraw(10, 50)"];

    function buildSteps(ops) {
      steps = [];
      const bal = [...initialBalances];

      steps.push({
        op: "Initialize Ledger",
        valid: "Ready",
        res: "null",
        balances: [...bal],
        total: bal.reduce((a, b) => a + b, 0),
        line: 1,
        narration: `Initialized bank with 5 accounts. Total capital = $${bal.reduce((a,b)=>a+b, 0)}.`
      });

      for (let i = 0; i < ops.length; i++) {
        const opStr = ops[i].trim();
        let res = false;
        let validStatus = "Passed";
        let narration = "";
        let lineNum = 1;

        if (opStr.startsWith("withdraw")) {
          const parts = opStr.match(/\\(([^)]+)\\)/)[1].split(',').map(s => parseInt(s.trim()));
          const acc = parts[0];
          const money = parts[1];
          lineNum = 9;
          if (acc < 1 || acc > bal.length) {
            validStatus = "Invalid Account";
            narration = `withdraw(${acc}, ${money}): Account ${acc} does not exist (valid 1..${bal.length}). Rejected.`;
          } else if (bal[acc - 1] < money) {
            validStatus = "Insufficient Funds";
            narration = `withdraw(${acc}, ${money}): Balance is $${bal[acc - 1]} < $${money}. Overdraft rejected.`;
          } else {
            bal[acc - 1] -= money;
            res = true;
            narration = `withdraw(${acc}, ${money}): Success. New balance for Account ${acc} is $${bal[acc - 1]}.`;
          }
        } else if (opStr.startsWith("deposit")) {
          const parts = opStr.match(/\\(([^)]+)\\)/)[1].split(',').map(s => parseInt(s.trim()));
          const acc = parts[0];
          const money = parts[1];
          lineNum = 6;
          if (acc < 1 || acc > bal.length) {
            validStatus = "Invalid Account";
            narration = `deposit(${acc}, ${money}): Account ${acc} does not exist. Rejected.`;
          } else {
            bal[acc - 1] += money;
            res = true;
            narration = `deposit(${acc}, ${money}): Deposited $${money}. New balance is $${bal[acc - 1]}.`;
          }
        } else if (opStr.startsWith("transfer")) {
          const parts = opStr.match(/\\(([^)]+)\\)/)[1].split(',').map(s => parseInt(s.trim()));
          const acc1 = parts[0];
          const acc2 = parts[1];
          const money = parts[2];
          lineNum = 3;
          if (acc1 < 1 || acc1 > bal.length || acc2 < 1 || acc2 > bal.length) {
            validStatus = "Invalid Account(s)";
            narration = `transfer(${acc1}, ${acc2}, ${money}): One or both accounts invalid. Rejected.`;
          } else if (bal[acc1 - 1] < money) {
            validStatus = "Insufficient Funds";
            narration = `transfer(${acc1}, ${acc2}, ${money}): Sender Account ${acc1} has $${bal[acc1 - 1]} < $${money}. Rejected.`;
          } else {
            bal[acc1 - 1] -= money;
            bal[acc2 - 1] += money;
            res = true;
            narration = `transfer(${acc1}, ${acc2}, ${money}): Transferred $${money} from Acc ${acc1} to Acc ${acc2}.`;
          }
        }

        steps.push({
          op: opStr,
          valid: validStatus,
          res: res ? "true" : "false",
          balances: [...bal],
          total: bal.reduce((a, b) => a + b, 0),
          line: lineNum,
          narration: narration
        });
      }
    }

    function loadPreset(ops) {
      document.getElementById('custom-input').value = ops.join(', ');
      initialOps = ops;
      stepperReset();
      buildSteps(initialOps);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(';').map(s => s.trim()).filter(s => s.length > 0);
      if (arr.length > 0) {
        initialOps = arr;
        stepperReset();
        buildSteps(initialOps);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-op').textContent = step.op;
      document.getElementById('metric-valid').textContent = step.valid;
      document.getElementById('metric-res').textContent = step.res;
      document.getElementById('metric-total').textContent = `$${step.total}`;
""",
            'js_render_visual': """
      const container = document.getElementById('ledger-accounts-container');
      container.innerHTML = '';
      step.balances.forEach((amount, idx) => {
        const accNum = idx + 1;
        const card = document.createElement('div');
        card.className = 'p-3 rounded-xl border border-slate-800 bg-slate-900/60 min-w-[110px] text-xs font-mono text-center';
        card.innerHTML = `
          <div class="text-[10px] text-slate-500 uppercase font-bold">Account ${accNum}</div>
          <div class="text-base font-bold text-emerald-400 mt-1">$${amount}</div>
        `;
        container.appendChild(card);
      });
"""
        },
        'approaches': [
            {
                'name': "Direct LongArray with Validation Guards",
                'desc': "Store balance array directly and check 1-indexed bounds with helper method.",
                'time': "All operations O(1)",
                'space': "O(N) Primitive Storage",
                'code_kotlin': """class Bank(private val balance: LongArray) {

    private fun isValid(account: Int): Boolean {
        return account in 1..balance.size
    }

    fun transfer(account1: Int, account2: Int, money: Long): Boolean {
        if (!isValid(account1) || !isValid(account2) || balance[account1 - 1] < money) {
            return false
        }
        balance[account1 - 1] -= money
        balance[account2 - 1] += money
        return true
    }

    fun deposit(account: Int, money: Long): Boolean {
        if (!isValid(account)) return false
        balance[account - 1] += money
        return true
    }

    fun withdraw(account: Int, money: Long): Boolean {
        if (!isValid(account) || balance[account - 1] < money) {
            return false
        }
        balance[account - 1] -= money
        return true
    }
}""",
                'notes_kotlin': "`account in 1..balance.size` is idiomatic range check in Kotlin. Uses primitive `LongArray` directly without wrapping.",
                'code_python': """class Bank:
    def __init__(self, balance: list[int]):
        self.balance = balance
        self.n = len(balance)

    def _is_valid(self, account: int) -> bool:
        return 1 <= account <= self.n

    def transfer(self, account1: int, account2: int, money: int) -> bool:
        if not self._is_valid(account1) or not self._is_valid(account2):
            return False
        if self.balance[account1 - 1] < money:
            return False
        self.balance[account1 - 1] -= money
        self.balance[account2 - 1] += money
        return True

    def deposit(self, account: int, money: int) -> bool:
        if not self._is_valid(account):
            return False
        self.balance[account - 1] += money
        return True

    def withdraw(self, account: int, money: int) -> bool:
        if not self._is_valid(account) or self.balance[account - 1] < money:
            return False
        self.balance[account - 1] -= money
        return True""",
                'notes_python': "Python handles arbitrarily large integers automatically, preventing 64-bit integer overflow bugs.",
                'code_java': """class Bank {
    private final long[] balance;
    private final int n;

    public Bank(long[] balance) {
        this.balance = balance;
        this.n = balance.length;
    }

    private boolean isValid(int account) {
        return account >= 1 && account <= n;
    }

    public boolean transfer(int account1, int account2, long money) {
        if (!isValid(account1) || !isValid(account2) || balance[account1 - 1] < money) {
            return false;
        }
        balance[account1 - 1] -= money;
        balance[account2 - 1] += money;
        return true;
    }

    public boolean deposit(int account, long money) {
        if (!isValid(account)) return false;
        balance[account - 1] += money;
        return true;
    }

    public boolean withdraw(int account, long money) {
        if (!isValid(account) || balance[account - 1] < money) {
            return false;
        }
        balance[account - 1] -= money;
        return true;
    }
}""",
                'notes_java': "Must use `long` primitives throughout. In a 32-bit `int`, values up to 10^12 cause silent numeric overflow and negative balances."
            },
            {
                'name': "Defensive Copying & Encapsulated Account Entities",
                'desc': "Model individual Account domain objects with private locks for thread-safe balance mutations.",
                'time': "All operations O(1)",
                'space': "O(N)",
                'code_kotlin': """class Bank(initialBalances: LongArray) {
    private class Account(val id: Int, var balance: Long)

    private val accounts = Array(initialBalances.size) { i ->
        Account(i + 1, initialBalances[i])
    }

    fun transfer(account1: Int, account2: Int, money: Long): Boolean {
        if (account1 !in 1..accounts.size || account2 !in 1..accounts.size) return false
        val acc1 = accounts[account1 - 1]
        val acc2 = accounts[account2 - 1]

        synchronized(acc1) {
            if (acc1.balance < money) return false
            synchronized(acc2) {
                acc1.balance -= money
                acc2.balance += money
                return true
            }
        }
    }

    fun deposit(account: Int, money: Long): Boolean {
        if (account !in 1..accounts.size) return false
        synchronized(accounts[account - 1]) {
            accounts[account - 1].balance += money
            return true
        }
    }

    fun withdraw(account: Int, money: Long): Boolean {
        if (account !in 1..accounts.size) return false
        val acc = accounts[account - 1]
        synchronized(acc) {
            if (acc.balance < money) return false
            acc.balance -= money
            return true
        }
    }
}""",
                'notes_kotlin': "Demonstrates senior domain-driven design with explicit synchronized locking per account to prevent race conditions.",
                'code_python': """class Bank:
    class Account:
        def __init__(self, acc_id: int, balance: int):
            self.id = acc_id
            self.balance = balance

    def __init__(self, balance: list[int]):
        self.accounts = [self.Account(i + 1, b) for i, b in enumerate(balance)]
        self.n = len(balance)

    def transfer(self, account1: int, account2: int, money: int) -> bool:
        if not (1 <= account1 <= self.n and 1 <= account2 <= self.n):
            return False
        src = self.accounts[account1 - 1]
        dst = self.accounts[account2 - 1]
        if src.balance < money:
            return False
        src.balance -= money
        dst.balance += money
        return True

    def deposit(self, account: int, money: int) -> bool:
        if not (1 <= account <= self.n):
            return False
        self.accounts[account - 1].balance += money
        return True

    def withdraw(self, account: int, money: int) -> bool:
        if not (1 <= account <= self.n):
            return False
        acc = self.accounts[account - 1]
        if acc.balance < money:
            return False
        acc.balance -= money
        return True""",
                'notes_python': "Encapsulating within an internal `Account` class mirrors production OOP microservices.",
                'code_java': """class Bank {
    private static class Account {
        long balance;
        Account(long balance) { this.balance = balance; }
    }

    private final Account[] accounts;

    public Bank(long[] balance) {
        this.accounts = new Account[balance.length];
        for (int i = 0; i < balance.length; i++) {
            accounts[i] = new Account(balance[i]);
        }
    }

    public synchronized boolean transfer(int account1, int account2, long money) {
        if (account1 < 1 || account1 > accounts.length || account2 < 1 || account2 > accounts.length) {
            return false;
        }
        Account a1 = accounts[account1 - 1];
        Account a2 = accounts[account2 - 1];
        if (a1.balance < money) return false;
        a1.balance -= money;
        a2.balance += money;
        return true;
    }

    public synchronized boolean deposit(int account, long money) {
        if (account < 1 || account > accounts.length) return false;
        accounts[account - 1].balance += money;
        return true;
    }

    public synchronized boolean withdraw(int account, long money) {
        if (account < 1 || account > accounts.length) return false;
        Account a = accounts[account - 1];
        if (a.balance < money) return false;
        a.balance -= money;
        return true;
    }
}""",
                'notes_java': "Senior consideration: adding `synchronized` ensures thread safety in concurrent banking environments."
            },
            {
                'name': "Optimal: Zero-Allocation Guarded Array (Production Standard)",
                'desc': "Maximum performance with strict guards and zero garbage collection overhead.",
                'time': "All operations O(1)",
                'space': "O(1) Auxiliary Memory",
                'code_kotlin': """class Bank(private val balance: LongArray) {
    /**
     * Optimal: Zero-Allocation Guarded Primitive LongArray
     * Time: O(1) for all methods
     * Space: O(1) auxiliary memory (in-place)
     */
    private val accountCount = balance.size

    @Suppress("NOTHING_TO_INLINE")
    private inline fun isValid(account: Int): Boolean = account in 1..accountCount

    fun transfer(account1: Int, account2: Int, money: Long): Boolean {
        if (!isValid(account1) || !isValid(account2)) return false
        val idx1 = account1 - 1
        val idx2 = account2 - 1
        if (balance[idx1] < money) return false
        
        balance[idx1] -= money
        balance[idx2] += money
        return true
    }

    fun deposit(account: Int, money: Long): Boolean {
        if (!isValid(account)) return false
        balance[account - 1] += money
        return true
    }

    fun withdraw(account: Int, money: Long): Boolean {
        if (!isValid(account)) return false
        val idx = account - 1
        if (balance[idx] < money) return false
        balance[idx] -= money
        return true
    }
}""",
                'notes_kotlin': "Inlining the validation function `inline fun isValid` eliminates function call overhead at runtime.",
                'code_python': """class Bank:
    # Optimal: Direct Fast Array Indexing
    def __init__(self, balance: list[int]):
        self.b = balance
        self.n = len(balance)

    def transfer(self, a1: int, a2: int, money: int) -> bool:
        if 1 <= a1 <= self.n and 1 <= a2 <= self.n and self.b[a1 - 1] >= money:
            self.b[a1 - 1] -= money
            self.b[a2 - 1] += money
            return True
        return False

    def deposit(self, a: int, money: int) -> bool:
        if 1 <= a <= self.n:
            self.b[a - 1] += money
            return True
        return False

    def withdraw(self, a: int, money: int) -> bool:
        if 1 <= a <= self.n and self.b[a - 1] >= money:
            self.b[a - 1] -= money
            return True
        return False""",
                'notes_python': "Concise, Pythonic, single-expression guards for fast evaluation.",
                'code_java': """class Bank {
    /**
     * Optimal: Direct Primitive Array with Short-Circuit Guards
     * Time: O(1)
     * Space: O(1) auxiliary
     */
    private final long[] b;
    private final int n;

    public Bank(long[] balance) {
        this.b = balance;
        this.n = balance.length;
    }

    public boolean transfer(int account1, int account2, long money) {
        if (account1 < 1 || account1 > n || account2 < 1 || account2 > n) return false;
        int i1 = account1 - 1;
        int i2 = account2 - 1;
        if (b[i1] < money) return false;
        b[i1] -= money;
        b[i2] += money;
        return true;
    }

    public boolean deposit(int account, long money) {
        if (account < 1 || account > n) return false;
        b[account - 1] += money;
        return true;
    }

    public boolean withdraw(int account, long money) {
        if (account < 1 || account > n) return false;
        int i = account - 1;
        if (b[i] < money) return false;
        b[i] -= money;
        return true;
    }
}""",
                'notes_java': "Production standard in Java. Bypasses collection wrappers entirely and uses cached `n`."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Bank(private val balance: LongArray) {
    private fun isValid(a: Int) = a in 1..balance.size
    fun transfer(a1: Int, a2: Int, m: Long): Boolean {
        if (!isValid(a1) || !isValid(a2) || balance[a1 - 1] < m) return false
        balance[a1 - 1] -= m; balance[a2 - 1] += m; return true
    }
    fun deposit(a: Int, m: Long): Boolean {
        if (!isValid(a)) return false
        balance[a - 1] += m; return true
    }
    fun withdraw(a: Int, m: Long): Boolean {
        if (!isValid(a) || balance[a - 1] < m) return false
        balance[a - 1] -= m; return true
    }
}""",
            'python_boilerplate': """class Bank:
    def __init__(self, balance: list[int]):
        self.b = balance
        self.n = len(balance)
    def transfer(self, a1: int, a2: int, m: int) -> bool:
        if 1 <= a1 <= self.n and 1 <= a2 <= self.n and self.b[a1 - 1] >= m:
            self.b[a1 - 1] -= m; self.b[a2 - 1] += m; return True
        return False
    def deposit(self, a: int, m: int) -> bool:
        if 1 <= a <= self.n:
            self.b[a - 1] += m; return True
        return False
    def withdraw(self, a: int, m: int) -> bool:
        if 1 <= a <= self.n and self.b[a - 1] >= m:
            self.b[a - 1] -= m; return True
        return False""",
            'java_boilerplate': """class Bank {
    private final long[] b;
    public Bank(long[] balance) { this.b = balance; }
    public boolean transfer(int a1, int a2, long m) {
        if (a1 < 1 || a1 > b.length || a2 < 1 || a2 > b.length || b[a1 - 1] < m) return false;
        b[a1 - 1] -= m; b[a2 - 1] += m; return true;
    }
    public boolean deposit(int a, long m) {
        if (a < 1 || a > b.length) return false;
        b[a - 1] += m; return true;
    }
    public boolean withdraw(int a, long m) {
        if (a < 1 || a > b.length || b[a - 1] < m) return false;
        b[a - 1] -= m; return true;
    }
}""",
            'js_test_runner': """
      const b = [10, 100, 20, 50, 30];
      const isValid = (a) => a >= 1 && a <= b.length;
      const withdraw = (a, m) => {
        if (!isValid(a) || b[a - 1] < m) return false;
        b[a - 1] -= m; return true;
      };
      const transfer = (a1, a2, m) => {
        if (!isValid(a1) || !isValid(a2) || b[a1 - 1] < m) return false;
        b[a1 - 1] -= m; b[a2 - 1] += m; return true;
      };
      const deposit = (a, m) => {
        if (!isValid(a)) return false;
        b[a - 1] += m; return true;
      };

      const t1 = withdraw(3, 10); // true
      const t2 = transfer(5, 1, 20); // true
      const t3 = deposit(5, 20); // true
      const t4 = transfer(3, 4, 15); // false (insufficient)
      const t5 = withdraw(10, 50); // false (invalid account)

      const passed = (t1 === true && t2 === true && t3 === true && t4 === false && t5 === false);
      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = `
        <div class="p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}">
          <span>Example 1 Test Suite</span>
          <span>${passed ? '&#10004; ALL 5 TRANSACTIONS PASSED' : '&#10008; FAILED'}</span>
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
            'phase1': "I clarify: are account numbers 1-indexed? Yes, from 1 to n. Can money values exceed 32-bit integers? Yes, up to 10^12, which requires 64-bit Long integers. If a transfer fails, must both accounts remain unchanged? Yes, transactions must be atomic.",
            'phase2': "For strategy, since accounts are contiguous integers from 1 to n, a direct 0-indexed long[] array provides O(1) reads and writes with zero object overhead. I will define a private helper function isValid(account) to encapsulate the 1 <= account <= n boundary check.",
            'phase3': "I implement the class Bank storing the array. In transfer, I validate both account1 and account2, check if balance[account1 - 1] >= money, and only then perform the subtraction and addition before returning true. For deposit and withdraw, I apply the same guards. Everything runs in O(1) time.",
            'phase4': "Dry run edge cases: transfer with insufficient funds: balance[a1 - 1] < money returns false before touching balances. Out-of-bounds account (account > n or account <= 0): returns false immediately. Large balances (10^12): 64-bit longs prevent overflow. Invariants hold.",
            'senior_followup': "Senior Scalability: In a multi-threaded banking environment, concurrent transfers between account A and B can cause deadlocks if two threads lock them in reverse order (A->B vs B->A). To prevent deadlocks, we must always acquire locks in global consistent order, e.g. `synchronized(min(acc1, acc2)) { synchronized(max(acc1, acc2)) { ... } }`."
        },
        'quiz': [
            {
                'question': "Why is it critical to use `Long` (or `long[]`) instead of `Int` (or `int[]`) in this banking problem?",
                'options': [
                    "Java arrays only support long indices.",
                    "The constraints state balances and money can reach 10^12, which exceeds 32-bit Integer.MAX_VALUE (~2 * 10^9) and causes arithmetic overflow.",
                    "Kotlin does not support primitive int arrays.",
                    "Banking regulations require floating point numbers."
                ],
                'correct_index': 1,
                'explanation': "The maximum value for a 32-bit signed integer is 2,147,483,647 (~2 * 10^9). Transactions up to 10^12 will wrap around into negative numbers unless 64-bit longs are used."
            },
            {
                'question': "How should account numbers (1 to n) be mapped to a 0-indexed array?",
                'options': [
                    "`balance[account]`",
                    "`balance[account - 1]`",
                    "`balance[account + 1]`",
                    "`balance[account * 2]`"
                ],
                'correct_index': 1,
                'explanation': "Since accounts are 1-indexed (account 1 to n), account k is stored at array index `k - 1`."
            }
        ]
    },

    # ---------------- 0032: DESIGN HIT COUNTER ----------------
    {
        'num': 32,
        'lc': 362,
        'title': "Design Hit Counter",
        'category': "Multi-Tier OOP & Timed State Systems",
        'difficulty': "Medium",
        'lc_slug': "design-hit-counter",
        'verbatim_statement': """<p>Design a hit counter which counts the number of hits received in the past <code>5</code> minutes (i.e., the past <code>300</code> seconds).</p>
<p class="mt-2">Your system should accept a <code>timestamp</code> parameter (in seconds granularity), and you may assume that calls are being made to the system in chronological order (i.e., <code>timestamp</code> is monotonically increasing). Several hits may arrive roughly at the same time.</p>
<p class="mt-2">Implement the <code>HitCounter</code> class:</p>
<ul class="list-disc pl-5 mt-1 space-y-1 text-xs">
  <li><code>HitCounter()</code> Initializes the object of the hit counter system.</li>
  <li><code>void hit(int timestamp)</code> Records a hit that happened at <code>timestamp</code> (in seconds). Several hits may happen at the same <code>timestamp</code>.</li>
  <li><code>int getHits(int timestamp)</code> Returns the number of hits in the past 5 minutes from <code>timestamp</code> (i.e., the past 300 seconds, range <code>[timestamp - 299, timestamp]</code>).</li>
</ul>""",
        'examples': [
            {'title': "Example 1", 'input': '["HitCounter","hit","hit","hit","getHits","hit","getHits","getHits"]\n[[],[1],[2],[3],[4],[300],[300],[301]]', 'output': '[null,null,null,null,3,null,4,3]'}
        ],
        'constraints': [
            "1 <= timestamp <= 2 * 10^9",
            "All the calls are being made to the system in chronological order (i.e., timestamp is monotonically increasing).",
            "At most 300 calls will be made to hit and getHits."
        ],
        'plain_english': "Track request rates over a sliding 300-second window. `hit(timestamp)` records an event; `getHits(timestamp)` returns the count of all hits that occurred in `(timestamp - 300, timestamp]`. Memory must not grow indefinitely with time.",
        'boundary_checks': {
            'min_base': "No hits recorded &rarr; getHits returns 0.",
            'best_exit': "All hits within 300 seconds &rarr; Return total count without eviction.",
            'worst_scan': "10,000 hits at the exact same second &rarr; Handled via hit count aggregation in circular buffer."
        },
        'mental_model': {
            'analogy_title': "The 300-Second Circular Conveyor Belt",
            'analogy_text': "Imagine a circular sushi carousel with exactly 300 plates numbered 0 to 299. Each second maps to plate `timestamp % 300`. If a plate's label matches the current timestamp, you add a hit token to it. If the plate holds an old timestamp from 5 minutes ago, you throw away the old tokens, update the plate's timestamp to now, and reset count to 1. To get the 5-minute hit count, you simply sum the tokens on all plates whose timestamp is >= timestamp - 300.",
            'aha_moment': "A 300-element circular buffer `times[300]` and `hits[300]` provides strict O(1) memory! Memory never exceeds 300 integers, regardless of whether 1,000 or 1,000,000,000 hits occur.",
            'trigger': "Whenever designing rate limiters, hit counters, or sliding metrics over fixed time W &rarr; Circular Ring Buffer of size W.",
            'sisters': [
                {'name': "Number of Recent Calls", 'lc': "LC #933"},
                {'name': "Design Bounded Blocking Queue", 'lc': "LC #1188"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "Queue: O(N) space growth, linear scan getHits",
            'naive_text': "Store every timestamp in a dynamic list. Memory grows without bound over days of operation.",
            'wasted_work': "Storing individual timestamps instead of bucketing by second, and letting expired historical hits leak memory.",
            'optimal_complexity': "hit: O(1), getHits: O(300) = O(1) Time, O(300) = O(1) Space",
            'optimal_text': "Fixed circular array of 300 buckets storing `(timestamp, hitCount)`. Strictly constant memory.",
            'core_invariant': "Only events within `(timestamp - 300, timestamp]` contribute to the sum; circular indexing recycles expired buckets.",
            'table_rows': [
                {'name': "1. Unbounded List", 'time': "getHits: O(N)", 'space': "O(N) memory leak", 'note': "Memory exhausts over long runtimes."},
                {'name': "2. Deque Eviction", 'time': "Amortized O(1)", 'space': "O(MaxHitsInWindow)", 'note': "Safe for single hits; balloons if bursty."},
                {'name': "★ 3. Circular Array (Size 300)", 'time': "All O(1)", 'space': "O(300) = O(1)", 'note': "Production standard. Zero memory growth."}
            ]
        },
        'trace_table': {
            'input_desc': 'hit(1), hit(2), hit(3), getHits(4), hit(300), getHits(300), getHits(301)',
            'columns': ["Operation", "Timestamp", "Window Range (t - 300, t]", "Active Timestamps in Window", "Result"],
            'rows': [
                ["hit(1)", "1", "(-299, 1]", "[1]", "null"],
                ["hit(2)", "2", "(-298, 2]", "[1, 2]", "null"],
                ["hit(3)", "3", "(-297, 3]", "[1, 2, 3]", "null"],
                ["getHits(4)", "4", "(-296, 4]", "[1, 2, 3]", "3"],
                ["hit(300)", "300", "(0, 300]", "[1, 2, 3, 300]", "null"],
                ["getHits(300)", "300", "(0, 300]", "[1, 2, 3, 300]", "4"],
                ["getHits(301)", "301", "(1, 301]", "[2, 3, 300] (hit at 1 expired)", "3"]
            ],
            'takeaway': "At t=301, the window is (1, 301]; hit at t=1 is excluded, returning 3."
        },
        'stepper': {
            'title': "300-Second Circular Buffer Slot Recycled Tracker",
            'default_input': 'hit(1), hit(2), hit(3), getHits(4), hit(300), getHits(300), getHits(301)',
            'presets': [
                {'label': "Example 1 Sequence", 'onclick': 'loadPreset(["hit(1)","hit(2)","hit(3)","getHits(4)","hit(300)","getHits(300)","getHits(301)"])'},
                {'label': "Burst Hits Same Sec", 'onclick': 'loadPreset(["hit(10)","hit(10)","hit(10)","getHits(10)","getHits(310)","getHits(311)"])'},
                {'label': "Complete Expiration", 'onclick': 'loadPreset(["hit(1)","getHits(2)","getHits(400)"])'}
            ],
            'metrics': [
                {'id': "metric-op", 'label': "Operation", 'color': "text-amber-400", 'initial': "Init"},
                {'id': "metric-t", 'label': "Current Timestamp", 'color': "text-cyan-400", 'initial': "0"},
                {'id': "metric-window", 'label': "Active Window", 'color': "text-emerald-400", 'initial': "(-300, 0]"},
                {'id': "metric-hits", 'label': "Hits in Window", 'color': "text-purple-400", 'initial': "0"}
            ],
            'visual_html': '<div id="hitcounter-timeline" class="w-full flex items-center justify-center gap-2 py-4 overflow-x-auto"></div>',
            'initial_narration': 'Click "Next Step" to trace HitCounter window eviction.',
            'pseudocode_lines': [
                "val times = IntArray(300)",
                "val hits = IntArray(300)",
                "fun hit(timestamp: Int) {",
                "    val idx = timestamp % 300",
                "    if (times[idx] != timestamp) { times[idx] = timestamp; hits[idx] = 1 } else hits[idx]++",
                "}",
                "fun getHits(timestamp: Int): Int {",
                "    return (0 until 300).filter { timestamp - times[it] < 300 }.sumOf { hits[it] }",
                "}"
            ],
            'js_code': """
    let initialOps = ["hit(1)","hit(2)","hit(3)","getHits(4)","hit(300)","getHits(300)","getHits(301)"];

    function buildSteps(ops) {
      steps = [];
      const times = new Array(300).fill(0);
      const hits = new Array(300).fill(0);

      steps.push({
        op: "Initialize",
        t: 0,
        win: "(-300, 0]",
        count: 0,
        activeBuckets: [],
        line: 1,
        narration: "Initialized 300-slot circular buffer. Memory is strictly O(1)."
      });

      for (let i = 0; i < ops.length; i++) {
        const item = ops[i].trim();
        if (item.startsWith("hit")) {
          const t = parseInt(item.match(/\\(([^)]+)\\)/)[1]);
          const idx = t % 300;
          if (times[idx] !== t) {
            times[idx] = t;
            hits[idx] = 1;
          } else {
            hits[idx]++;
          }

          const active = [];
          for (let b = 0; b < 300; b++) {
            if (times[b] > 0 && t - times[b] < 300) {
              active.push({ t: times[b], count: hits[b] });
            }
          }
          active.sort((a,b) => a.t - b.t);

          steps.push({
            op: `hit(${t})`,
            t: t,
            win: `(${t - 300}, ${t}]`,
            count: active.reduce((acc, c) => acc + c.count, 0),
            activeBuckets: active,
            line: 5,
            narration: `Recorded hit at t=${t} (slot ${idx}). Total hits in current 300s window = ${active.reduce((acc, c) => acc + c.count, 0)}.`
          });
        } else if (item.startsWith("getHits")) {
          const t = parseInt(item.match(/\\(([^)]+)\\)/)[1]);
          let total = 0;
          const active = [];
          for (let b = 0; b < 300; b++) {
            if (times[b] > 0 && t - times[b] < 300) {
              total += hits[b];
              active.push({ t: times[b], count: hits[b] });
            }
          }
          active.sort((a,b) => a.t - b.t);

          steps.push({
            op: `getHits(${t})`,
            t: t,
            win: `(${t - 300}, ${t}]`,
            count: total,
            activeBuckets: active,
            line: 7,
            narration: `Queried getHits(${t}): Summing all slots within (${t-300}, ${t}] &rarr; Returns ${total}.`
          });
        }
      }
    }

    function loadPreset(ops) {
      document.getElementById('custom-input').value = ops.join(', ');
      initialOps = ops;
      stepperReset();
      buildSteps(initialOps);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => s.trim()).filter(s => s.length > 0);
      if (arr.length > 0) {
        initialOps = arr;
        stepperReset();
        buildSteps(initialOps);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-op').textContent = step.op;
      document.getElementById('metric-t').textContent = step.t;
      document.getElementById('metric-window').textContent = step.win;
      document.getElementById('metric-hits').textContent = step.count;
""",
            'js_render_visual': """
      const container = document.getElementById('hitcounter-timeline');
      container.innerHTML = '';
      if (step.activeBuckets.length === 0) {
        container.innerHTML = '<span class="text-xs font-mono text-slate-500">No active hits in 300s window</span>';
      } else {
        step.activeBuckets.forEach(b => {
          const el = document.createElement('div');
          el.className = 'p-2.5 rounded-xl border border-emerald-500 bg-emerald-950/40 text-xs font-mono flex flex-col items-center min-w-[70px]';
          el.innerHTML = `
            <span class="text-[9px] text-slate-400">t = ${b.t}s</span>
            <span class="text-emerald-300 font-bold text-sm mt-0.5">${b.count} hit${b.count > 1 ? 's' : ''}</span>
          `;
          container.appendChild(el);
        });
      }
"""
        },
        'approaches': [
            {
                'name': "Queue Sliding Window Eviction",
                'desc': "Use ArrayDeque to store hit timestamps and poll expired timestamps on each query.",
                'time': "hit: O(1), getHits: O(Expired) amortized O(1)",
                'space': "O(MaxHitsInWindow)",
                'code_kotlin': """class HitCounter() {
    private val queue = ArrayDeque<Int>()

    fun hit(timestamp: Int) {
        queue.addLast(timestamp)
    }

    fun getHits(timestamp: Int): Int {
        while (queue.isNotEmpty() && timestamp - queue.first() >= 300) {
            queue.removeFirst()
        }
        return queue.size
    }
}""",
                'notes_kotlin': "Simple and concise. However, if 10 million hits arrive at the same second, queue size grows to 10 million.",
                'code_python': """class HitCounter:
    def __init__(self):
        from collections import deque
        self.queue = deque()

    def hit(self, timestamp: int) -> None:
        self.queue.append(timestamp)

    def getHits(self, timestamp: int) -> int:
        while self.queue and timestamp - self.queue[0] >= 300:
            self.queue.popleft()
        return len(self.queue)""",
                'notes_python': "Standard Python queue eviction. Amortized O(1) time per operation.",
                'code_java': """import java.util.ArrayDeque;
import java.util.Deque;

class HitCounter {
    private Deque<Integer> queue = new ArrayDeque<>();

    public HitCounter() {}

    public void hit(int timestamp) {
        queue.offer(timestamp);
    }

    public int getHits(int timestamp) {
        while (!queue.isEmpty() && timestamp - queue.peek() >= 300) {
            queue.poll();
        }
        return queue.size();
    }
}""",
                'notes_java': "Clean for low traffic. Under high concurrency, memory is not strictly bounded."
            },
            {
                'name': "Optimal: Fixed 300-Second Circular Array (Production Standard)",
                'desc': "Store hits in 300 circular buckets of (timestamp, hitCount). Guaranteed O(1) space.",
                'time': "hit: O(1), getHits: O(300) = O(1)",
                'space': "O(300) = O(1) Absolute Memory Cap",
                'code_kotlin': """class HitCounter() {
    /**
     * Optimal: 300-Slot Circular Ring Buffer
     * Time: hit: O(1), getHits: O(300) = O(1)
     * Space: O(300) = O(1) Strict Constant Memory
     */
    private val times = IntArray(300)
    private val hits = IntArray(300)

    fun hit(timestamp: Int) {
        val idx = timestamp % 300
        if (times[idx] != timestamp) {
            times[idx] = timestamp
            hits[idx] = 1 // Reset slot for new timestamp cycle
        } else {
            hits[idx]++
        }
    }

    fun getHits(timestamp: Int): Int {
        var total = 0
        for (i in 0 until 300) {
            if (timestamp - times[i] < 300) {
                total += hits[i]
            }
        }
        return total
    }
}""",
                'notes_kotlin': "Staff-level design: handles unlimited hits per second without memory expansion. Memory is fixed at exactly 600 integers.",
                'code_python': """class HitCounter:
    # Optimal: 300-Bucket Circular Array
    # Time: hit O(1), getHits O(300) = O(1)
    # Space: O(300) = O(1)
    def __init__(self):
        self.times = [0] * 300
        self.hits = [0] * 300

    def hit(self, timestamp: int) -> None:
        idx = timestamp % 300
        if self.times[idx] != timestamp:
            self.times[idx] = timestamp
            self.hits[idx] = 1
        else:
            self.hits[idx] += 1

    def getHits(self, timestamp: int) -> int:
        total = 0
        for i in range(300):
            if timestamp - self.times[i] < 300:
                total += self.hits[i]
        return total""",
                'notes_python': "Constant memory footprint. Ideal for high-throughput rate-limiting microservices.",
                'code_java': """class HitCounter {
    /**
     * Optimal: 300-Bucket Circular Array
     * Time: O(1) hit, O(300) getHits
     * Space: O(1) (600 ints total)
     */
    private final int[] times = new int[300];
    private final int[] hits = new int[300];

    public HitCounter() {}

    public void hit(int timestamp) {
        int idx = timestamp % 300;
        if (times[idx] != timestamp) {
            times[idx] = timestamp;
            hits[idx] = 1;
        } else {
            hits[idx]++;
        }
    }

    public int getHits(int timestamp) {
        int total = 0;
        for (int i = 0; i < 300; i++) {
            if (timestamp - times[i] < 300) {
                total += hits[i];
            }
        }
        return total;
    }
}""",
                'notes_java': "The production gold standard for rate limiters. Zero GC pressure; constant runtime."
            },
            {
                'name': "Thread-Safe Concurrent Circular Buffer",
                'desc': "Add lock-free AtomicInteger arrays or synchronized locks for concurrent multi-threaded hits.",
                'time': "All O(1)",
                'space': "O(1)",
                'code_kotlin': """import java.util.concurrent.atomic.AtomicIntegerArray

class HitCounter() {
    private val times = AtomicIntegerArray(300)
    private val hits = AtomicIntegerArray(300)

    fun hit(timestamp: Int) {
        val idx = timestamp % 300
        synchronized(this) {
            if (times.get(idx) != timestamp) {
                times.set(idx, timestamp)
                hits.set(idx, 1)
            } else {
                hits.incrementAndGet(idx)
            }
        }
    }

    fun getHits(timestamp: Int): Int {
        var total = 0
        for (i in 0 until 300) {
            if (timestamp - times.get(i) < 300) {
                total += hits.get(i)
            }
        }
        return total
    }
}""",
                'notes_kotlin': "`AtomicIntegerArray` demonstrates senior production awareness for multi-threaded web servers.",
                'code_python': """import threading

class HitCounter:
    def __init__(self):
        self.times = [0] * 300
        self.hits = [0] * 300
        self.lock = threading.Lock()

    def hit(self, timestamp: int) -> None:
        idx = timestamp % 300
        with self.lock:
            if self.times[idx] != timestamp:
                self.times[idx] = timestamp
                self.hits[idx] = 1
            else:
                self.hits[idx] += 1

    def getHits(self, timestamp: int) -> int:
        total = 0
        with self.lock:
            for i in range(300):
                if timestamp - self.times[i] < 300:
                    total += self.hits[i]
        return total""",
                'notes_python': "Thread-safe with Python `threading.Lock()` to prevent race conditions during concurrent hits.",
                'code_java': """import java.util.concurrent.atomic.AtomicIntegerArray;

class HitCounter {
    private final AtomicIntegerArray times = new AtomicIntegerArray(300);
    private final AtomicIntegerArray hits = new AtomicIntegerArray(300);

    public synchronized void hit(int timestamp) {
        int idx = timestamp % 300;
        if (times.get(idx) != timestamp) {
            times.set(idx, timestamp);
            hits.set(idx, 1);
        } else {
            hits.incrementAndGet(idx);
        }
    }

    public synchronized int getHits(int timestamp) {
        int total = 0;
        for (int i = 0; i < 300; i++) {
            if (timestamp - times.get(i) < 300) {
                total += hits.get(i);
            }
        }
        return total;
    }
}""",
                'notes_java': "Enterprise standard for synchronized rate-limiting counters."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class HitCounter() {
    private val times = IntArray(300)
    private val hits = IntArray(300)
    fun hit(t: Int) {
        val i = t % 300
        if (times[i] != t) { times[i] = t; hits[i] = 1 } else hits[i]++
    }
    fun getHits(t: Int): Int {
        var total = 0
        for (i in 0 until 300) if (t - times[i] < 300) total += hits[i]
        return total
    }
}""",
            'python_boilerplate': """class HitCounter:
    def __init__(self):
        self.times = [0] * 300
        self.hits = [0] * 300
    def hit(self, t: int) -> None:
        i = t % 300
        if self.times[i] != t: self.times[i] = t; self.hits[i] = 1
        else: self.hits[i] += 1
    def getHits(self, t: int) -> int:
        return sum(self.hits[i] for i in range(300) if t - self.times[i] < 300)""",
            'java_boilerplate': """class HitCounter {
    private final int[] times = new int[300];
    private final int[] hits = new int[300];
    public void hit(int t) {
        int i = t % 300;
        if (times[i] != t) { times[i] = t; hits[i] = 1; } else hits[i]++;
    }
    public int getHits(int t) {
        int total = 0;
        for (int i = 0; i < 300; i++) if (t - times[i] < 300) total += hits[i];
        return total;
    }
}""",
            'js_test_runner': """
      const times = new Array(300).fill(0);
      const hits = new Array(300).fill(0);
      const hit = (t) => {
        const i = t % 300;
        if (times[i] !== t) { times[i] = t; hits[i] = 1; }
        else hits[i]++;
      };
      const getHits = (t) => {
        let total = 0;
        for (let i = 0; i < 300; i++) {
          if (times[i] > 0 && t - times[i] < 300) total += hits[i];
        }
        return total;
      };

      hit(1); hit(2); hit(3);
      const t1 = getHits(4); // 3
      hit(300);
      const t2 = getHits(300); // 4
      const t3 = getHits(301); // 3 (hit at 1 expired)

      const passed = (t1 === 3 && t2 === 4 && t3 === 3);
      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = `
        <div class="p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}">
          <span>Example 1 Test Suite</span>
          <span>${passed ? '&#10004; PASSED (getHits(4)=3, getHits(300)=4, getHits(301)=3)' : '&#10008; FAILED'}</span>
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
            'phase1': "I clarify: are timestamps strictly in seconds granularity? Yes. Are timestamps monotonically increasing? Yes. Can multiple hits occur at the same timestamp? Yes, and getHits must include all of them.",
            'phase2': "For strategy: a Queue works by polling timestamps < t - 300, but memory scales with the number of hits. If millions of hits occur in a burst, memory balloons. A superior production approach is a fixed 300-element circular buffer: times[300] and hits[300]. Any timestamp maps to timestamp % 300. This caps memory strictly to 600 integers (O(1) space) and makes hit() O(1) and getHits() O(300) = O(1).",
            'phase3': "I allocate int[] times = new int[300] and int[] hits = new int[300]. In hit(t), idx = t % 300. If times[idx] != t, it means this slot held a timestamp from >= 300 seconds ago; we overwrite times[idx] = t and reset hits[idx] = 1. Otherwise, we increment hits[idx]. In getHits(t), we loop through all 300 slots, summing hits[i] wherever t - times[i] < 300.",
            'phase4': "Dry running edge cases: hit at t=1, query at t=301: 301 - 1 = 300, which is not < 300, so it is correctly excluded. Hit at t=300 and query at t=300: 300 - 300 = 0 < 300, included. Memory is strictly constant.",
            'senior_followup': "Senior Scalability: What if the system handles 50,000 hits/sec across multiple server nodes? We would deploy local in-memory circular buffers on each edge server node, aggregating metrics asynchronously into a distributed time-series database or Redis sliding hyperloglog."
        },
        'quiz': [
            {
                'question': "Why is a fixed 300-element circular buffer superior to a Queue for high-throughput production systems?",
                'options': [
                    "Queues cannot store integers in Java.",
                    "If millions of hits arrive in 5 minutes, a Queue consumes gigabytes of memory, whereas a circular array memory footprint is capped at exactly 300 slots.",
                    "Circular arrays do not require modulo arithmetic.",
                    "Queues do not support FIFO eviction."
                ],
                'correct_index': 1,
                'explanation': "Under high volume (e.g., 50k hits/sec), a Queue must store 15,000,000 timestamps. The circular buffer aggregates counts per second into 300 slots, capping memory at under 3 KB total."
            },
            {
                'question': "What happens in `hit(timestamp)` if `times[timestamp % 300] != timestamp`?",
                'options': [
                    "A collision error is thrown.",
                    "The slot contains an expired timestamp from a previous 5-minute cycle; we overwrite it with the current timestamp and reset its hit count to 1.",
                    "The new timestamp is discarded.",
                    "The array expands by doubling its size."
                ],
                'correct_index': 1,
                'explanation': "Because timestamps are monotonically increasing, finding a different timestamp at `t % 300` means that slot's data is at least 300 seconds old and can be safely recycled."
            }
        ]
    },

    # ---------------- 0033: DESIGN UNDERGROUND SYSTEM ----------------
    {
        'num': 33,
        'lc': 1396,
        'title': "Design Underground System",
        'category': "Multi-Tier OOP & Timed State Systems",
        'difficulty': "Medium",
        'lc_slug': "design-underground-system",
        'verbatim_statement': """<p>An underground railway system is keeping track of customer travel times between different stations. They are using this data to calculate the average time it takes to travel from one station to another.</p>
<p class="mt-2">Implement the <code>UndergroundSystem</code> class:</p>
<ul class="list-disc pl-5 mt-1 space-y-1 text-xs">
  <li><code>void checkIn(int id, string stationName, int t)</code>
    <ul class="list-disc pl-5 space-y-0.5 mt-1">
      <li>A customer with a card ID equal to <code>id</code>, checks in at the station <code>stationName</code> at time <code>t</code>.</li>
      <li>A customer can only be checked into one place at a time.</li>
    </ul>
  </li>
  <li><code>void checkOut(int id, string stationName, int t)</code>
    <ul class="list-disc pl-5 space-y-0.5 mt-1">
      <li>A customer with a card ID equal to <code>id</code>, checks out from the station <code>stationName</code> at time <code>t</code>.</li>
    </ul>
  </li>
  <li><code>double getAverageTime(string startStation, string endStation)</code>
    <ul class="list-disc pl-5 space-y-0.5 mt-1">
      <li>Returns the average time it takes to travel from <code>startStation</code> to <code>endStation</code>.</li>
      <li>The average time is computed from all the previous traveling times between <code>startStation</code> and <code>endStation</code> that have happened <strong>directly</strong>, meaning a check in at <code>startStation</code> followed by a check out from <code>endStation</code>.</li>
    </ul>
  </li>
</ul>""",
        'examples': [
            {'title': "Example 1", 'input': 'checkIn(45,"Leyton",3), checkIn(32,"Paradise",8), checkIn(27,"Leyton",10), checkOut(45,"Waterloo",15), checkOut(27,"Waterloo",20), checkOut(32,"Cambridge",22), getAverageTime("Paradise","Cambridge")', 'output': '14.0 (22 - 8 = 14)'}
        ],
        'constraints': [
            "1 <= id, t <= 10^6",
            "1 <= stationName.length, startStation.length, endStation.length <= 10",
            "All strings consist of uppercase and lowercase English letters and digits.",
            "There will be at most 2 * 10^4 calls in total to checkIn, checkOut, and getAverageTime.",
            "Answers within 10^-5 of the actual value will be accepted."
        ],
        'plain_english': "Track transit travel durations between stations. When a passenger checks in, store their active trip (startStation, startTime). When they check out, retrieve and remove their check-in, compute the trip duration, and update the aggregate stats (totalDuration, totalTrips) for that route (startStation -> endStation). getAverageTime returns totalDuration / totalTrips in O(1) time.",
        'boundary_checks': {
            'min_base': "Single trip between A and B with duration D &rarr; getAverageTime(A, B) == D.",
            'best_exit': "Multiple trips on same route &rarr; Running sum and count updated in O(1) time.",
            'worst_scan': "10,000 distinct stations &rarr; HashMap lookup by composite route string 'Start->End'."
        },
        'mental_model': {
            'analogy_title': "The Metro Turnstile Ledger",
            'analogy_text': "Think of a subway station turnstile. When you tap in, the system writes a temporary post-it note: [Card 45: Station 'Leyton' at 3:00 PM]. When you tap out at 'Waterloo' at 3:15 PM, the exit turnstile takes down your post-it, calculates 15 minutes, throws away the post-it, and updates a permanent tally board labeled 'Leyton &rarr; Waterloo': +15 minutes, +1 trip. To find the average time, the station manager just divides the board's total minutes by the trip count.",
            'aha_moment': "Two distinct HashMaps: 1) `checkInMap<PassengerId, CheckInInfo>` tracking in-flight passenger trips; 2) `routeStatsMap<RouteKey, RouteStats>` tracking running totals. When checkOut occurs, delete from checkInMap to prevent memory leaks.",
            'trigger': "Whenever computing real-time aggregations (averages, counts) between start and end events &rarr; Active-Event Map + Aggregate-Stats Map.",
            'sisters': [
                {'name': "Design Hit Counter", 'lc': "LC #362"},
                {'name': "Time Based Key-Value Store", 'lc': "LC #981"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "checkIn: O(1), checkOut: O(1), getAverageTime: O(TotalTrips)",
            'naive_text': "Store every completed trip in a massive list. In getAverageTime, linearly iterate through all historical trips filtering by startStation and endStation.",
            'wasted_work': "Re-calculating the sum and count of matching trips on every query instead of maintaining pre-aggregated running stats.",
            'optimal_complexity': "All operations strictly O(1) Time, O(P + R) Space",
            'optimal_text': "checkOut updates running sum and count on the route. getAverageTime divides sum / count in O(1) time.",
            'core_invariant': "checkInMap holds strictly in-flight journeys; routeStatsMap holds precomputed (sum, count) for each route.",
            'table_rows': [
                {'name': "1. Historical Trips List", 'time': "getAverageTime: O(N)", 'space': "O(N)", 'note': "Scans all completed trips on every query."},
                {'name': "★ 2. Two HashMaps (Pre-Aggregated)", 'time': "All O(1)", 'space': "O(P + R)", 'note': "O(1) lookups; memory evicted on checkout."}
            ]
        },
        'trace_table': {
            'input_desc': 'checkIn(45, "Leyton", 3), checkOut(45, "Waterloo", 15), checkIn(27, "Leyton", 10), checkOut(27, "Waterloo", 20), getAverageTime("Leyton", "Waterloo")',
            'columns': ["Operation", "Passenger ID", "checkInMap State", "Route Key", "Route Total Time / Trips", "Result"],
            'rows': [
                ["checkIn(45, 'Leyton', 3)", "45", "{45: ('Leyton', 3)}", "-", "-", "null"],
                ["checkOut(45, 'Waterloo', 15)", "45", "{} (evicted)", "'Leyton->Waterloo'", "12 / 1", "null"],
                ["checkIn(27, 'Leyton', 10)", "27", "{27: ('Leyton', 10)}", "-", "-", "null"],
                ["checkOut(27, 'Waterloo', 20)", "27", "{} (evicted)", "'Leyton->Waterloo'", "22 / 2", "null"],
                ["getAverageTime('Leyton', 'Waterloo')", "-", "{}", "'Leyton->Waterloo'", "22 / 2 = 11.0", "11.0"]
            ],
            'takeaway': "getAverageTime computes 22 / 2 = 11.0 in instantaneous O(1) time."
        },
        'stepper': {
            'title': "Two-HashMap Lifecycle: CheckIn Eviction & Route Stat Accumulation",
            'default_input': 'checkIn(45, "Leyton", 3), checkOut(45, "Waterloo", 15), checkIn(27, "Leyton", 10), checkOut(27, "Waterloo", 20), getAverageTime("Leyton", "Waterloo")',
            'presets': [
                {'label': "Two Passengers", 'onclick': 'loadPreset(["checkIn(45, Leyton, 3)", "checkOut(45, Waterloo, 15)", "checkIn(27, Leyton, 10)", "checkOut(27, Waterloo, 20)", "getAverageTime(Leyton, Waterloo)"])'},
                {'label': "Single Trip", 'onclick': 'loadPreset(["checkIn(10, StationA, 5)", "checkOut(10, StationB, 25)", "getAverageTime(StationA, StationB)"])'},
                {'label': "Different Routes", 'onclick': 'loadPreset(["checkIn(1, A, 0)", "checkOut(1, B, 10)", "checkIn(2, A, 0)", "checkOut(2, C, 20)", "getAverageTime(A, B)"])'}
            ],
            'metrics': [
                {'id': "metric-op", 'label': "Operation", 'color': "text-amber-400", 'initial': "Init"},
                {'id': "metric-inflight", 'label': "In-Flight Passengers", 'color': "text-cyan-400", 'initial': "0"},
                {'id': "metric-routes", 'label': "Total Routes Tracked", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-avg", 'label': "Queried Average", 'color': "text-purple-400", 'initial': "-"}
            ],
            'visual_html': '<div id="underground-visual" class="w-full flex flex-col md:flex-row gap-4 justify-around items-start py-4"></div>',
            'initial_narration': 'Click "Next Step" to trace transit check-ins and check-outs.',
            'pseudocode_lines': [
                "data class CheckIn(val station: String, val time: Int)",
                "data class RouteStats(var totalTime: Long, var count: Int)",
                "val checkIns = mutableMapOf<Int, CheckIn>()",
                "val routes = mutableMapOf<String, RouteStats>()",
                "fun checkIn(id: Int, station: String, t: Int) = checkIns.put(id, CheckIn(station, t))",
                "fun checkOut(id: Int, station: String, t: Int) {",
                "    val start = checkIns.remove(id)!!; val key = \"${start.station}->${station}\"",
                "    val s = routes.getOrPut(key) { RouteStats(0, 0) }; s.totalTime += (t - start.time); s.count++",
                "}",
                "fun getAverageTime(start: String, end: String) = routes[\"${start}->${end}\"]!!.let { it.totalTime.toDouble() / it.count }"
            ],
            'js_code': """
    let initialOps = [
      "checkIn(45, Leyton, 3)",
      "checkOut(45, Waterloo, 15)",
      "checkIn(27, Leyton, 10)",
      "checkOut(27, Waterloo, 20)",
      "getAverageTime(Leyton, Waterloo)"
    ];

    function buildSteps(ops) {
      steps = [];
      const checkIns = {};
      const routes = {};

      steps.push({
        op: "Initialize",
        inflight: 0,
        routesCount: 0,
        avg: "-",
        checkInsState: {},
        routesState: {},
        line: 3,
        narration: "Initialized checkInMap and routeStatsMap."
      });

      for (let i = 0; i < ops.length; i++) {
        const item = ops[i].trim();
        if (item.startsWith("checkIn")) {
          const parts = item.match(/\\(([^)]+)\\)/)[1].split(',').map(s => s.trim().replace(/['"]/g, ''));
          const id = parts[0];
          const station = parts[1];
          const t = parseInt(parts[2]);
          checkIns[id] = { station: station, time: t };

          steps.push({
            op: `checkIn(${id}, ${station}, ${t})`,
            inflight: Object.keys(checkIns).length,
            routesCount: Object.keys(routes).length,
            avg: "-",
            checkInsState: JSON.parse(JSON.stringify(checkIns)),
            routesState: JSON.parse(JSON.stringify(routes)),
            line: 5,
            narration: `Passenger ${id} checked in at "${station}" at time ${t}. Stored in checkInMap.`
          });
        } else if (item.startsWith("checkOut")) {
          const parts = item.match(/\\(([^)]+)\\)/)[1].split(',').map(s => s.trim().replace(/['"]/g, ''));
          const id = parts[0];
          const station = parts[1];
          const t = parseInt(parts[2]);
          const startInfo = checkIns[id];
          delete checkIns[id]; // evict

          const routeKey = `${startInfo.station}->${station}`;
          const duration = t - startInfo.time;
          if (!routes[routeKey]) routes[routeKey] = { totalTime: 0, count: 0 };
          routes[routeKey].totalTime += duration;
          routes[routeKey].count += 1;

          steps.push({
            op: `checkOut(${id}, ${station}, ${t})`,
            inflight: Object.keys(checkIns).length,
            routesCount: Object.keys(routes).length,
            avg: "-",
            checkInsState: JSON.parse(JSON.stringify(checkIns)),
            routesState: JSON.parse(JSON.stringify(routes)),
            line: 8,
            narration: `Passenger ${id} checked out at "${station}". Trip duration: ${duration}s. Route "${routeKey}" updated: ${routes[routeKey].totalTime}s across ${routes[routeKey].count} trips.`
          });
        } else if (item.startsWith("getAverageTime")) {
          const parts = item.match(/\\(([^)]+)\\)/)[1].split(',').map(s => s.trim().replace(/['"]/g, ''));
          const start = parts[0];
          const end = parts[1];
          const routeKey = `${start}->${end}`;
          const r = routes[routeKey];
          const avgVal = r ? (r.totalTime / r.count).toFixed(2) : "0.00";

          steps.push({
            op: `getAverageTime(${start}, ${end})`,
            inflight: Object.keys(checkIns).length,
            routesCount: Object.keys(routes).length,
            avg: `${avgVal}s`,
            checkInsState: JSON.parse(JSON.stringify(checkIns)),
            routesState: JSON.parse(JSON.stringify(routes)),
            line: 10,
            narration: `Queried average time for "${routeKey}": ${r.totalTime} / ${r.count} = ${avgVal} seconds.`
          });
        }
      }
    }

    function loadPreset(ops) {
      document.getElementById('custom-input').value = ops.join(', ');
      initialOps = ops;
      stepperReset();
      buildSteps(initialOps);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(';').map(s => s.trim()).filter(s => s.length > 0);
      if (arr.length > 0) {
        initialOps = arr;
        stepperReset();
        buildSteps(initialOps);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-op').textContent = step.op;
      document.getElementById('metric-inflight').textContent = step.inflight;
      document.getElementById('metric-routes').textContent = step.routesCount;
      document.getElementById('metric-avg').textContent = step.avg;
""",
            'js_render_visual': """
      const container = document.getElementById('underground-visual');
      container.innerHTML = `
        <div class="flex-1 p-3 rounded-xl border border-slate-800 bg-slate-950 min-w-[200px] text-xs font-mono space-y-2">
          <div class="text-[10px] uppercase font-bold text-cyan-400">In-Flight CheckIns (${Object.keys(step.checkInsState).length})</div>
          ${Object.keys(step.checkInsState).length === 0 ? '<span class="text-slate-600">None in transit</span>' : ''}
          ${Object.keys(step.checkInsState).map(id => `
            <div class="p-1.5 rounded bg-slate-900 border border-slate-800 flex justify-between">
              <span>Card #${id}</span>
              <span class="text-emerald-400">${step.checkInsState[id].station} @ t=${step.checkInsState[id].time}</span>
            </div>
          `).join('')}
        </div>
        <div class="flex-1 p-3 rounded-xl border border-slate-800 bg-slate-950 min-w-[200px] text-xs font-mono space-y-2">
          <div class="text-[10px] uppercase font-bold text-emerald-400">Route Stats Board (${Object.keys(step.routesState).length})</div>
          ${Object.keys(step.routesState).length === 0 ? '<span class="text-slate-600">No completed routes</span>' : ''}
          ${Object.keys(step.routesState).map(k => `
            <div class="p-1.5 rounded bg-slate-900 border border-slate-800 flex justify-between">
              <span class="text-white">${k}</span>
              <span class="text-cyan-300 font-bold">${(step.routesState[k].totalTime / step.routesState[k].count).toFixed(1)}s (${step.routesState[k].count} trips)</span>
            </div>
          `).join('')}
        </div>
      `;
"""
        },
        'approaches': [
            {
                'name': "Two HashMaps with Composite String Key",
                'desc': "checkInMap for passenger state, routeMap with 'startStation->endStation' key.",
                'time': "All operations O(1)",
                'space': "O(P + R) where P is active passengers, R is routes",
                'code_kotlin': """class UndergroundSystem() {
    private data class CheckIn(val station: String, val time: Int)
    private data class RouteStats(var totalTime: Long, var count: Int)

    private val checkIns = HashMap<Int, CheckIn>()
    private val routes = HashMap<String, RouteStats>()

    fun checkIn(id: Int, stationName: String, t: Int) {
        checkIns[id] = CheckIn(stationName, t)
    }

    fun checkOut(id: Int, stationName: String, t: Int) {
        val start = checkIns.remove(id) ?: return
        val routeKey = "${start.station}->$stationName"
        val stats = routes.getOrPut(routeKey) { RouteStats(0L, 0) }
        stats.totalTime += (t - start.time)
        stats.count++
    }

    fun getAverageTime(startStation: String, endStation: String): Double {
        val stats = routes["$startStation->$endStation"] ?: return 0.0
        return stats.totalTime.toDouble() / stats.count
    }
}""",
                'notes_kotlin': "Removing passenger on checkout via `checkIns.remove(id)` prevents memory leaks over continuous runtime.",
                'code_python': """class UndergroundSystem:
    def __init__(self):
        self.check_ins = {}  # id -> (station, time)
        self.routes = {}     # (start, end) -> [total_time, count]

    def checkIn(self, id: int, stationName: str, t: int) -> None:
        self.check_ins[id] = (stationName, t)

    def checkOut(self, id: int, stationName: str, t: int) -> None:
        start_station, start_time = self.check_ins.pop(id)
        route_key = (start_station, stationName)
        duration = t - start_time
        
        if route_key not in self.routes:
            self.routes[route_key] = [0, 0]
        self.routes[route_key][0] += duration
        self.routes[route_key][1] += 1

    def getAverageTime(self, startStation: str, endStation: str) -> float:
        total_time, count = self.routes[(startStation, endStation)]
        return total_time / count""",
                'notes_python': "Tuples `(start_station, stationName)` serve natively as hashable dictionary keys in Python without string concatenation.",
                'code_java': """import java.util.HashMap;
import java.util.Map;

class UndergroundSystem {
    private static class CheckIn {
        final String station;
        final int time;
        CheckIn(String station, int time) {
            this.station = station;
            this.time = time;
        }
    }

    private static class RouteStats {
        long totalTime = 0;
        int count = 0;
    }

    private final Map<Integer, CheckIn> checkIns = new HashMap<>();
    private final Map<String, RouteStats> routes = new HashMap<>();

    public UndergroundSystem() {}

    public void checkIn(int id, String stationName, int t) {
        checkIns.put(id, new CheckIn(stationName, t));
    }

    public void checkOut(int id, String stationName, int t) {
        CheckIn start = checkIns.remove(id);
        if (start == null) return;
        String routeKey = start.station + "->" + stationName;
        RouteStats stats = routes.computeIfAbsent(routeKey, k -> new RouteStats());
        stats.totalTime += (t - start.time);
        stats.count++;
    }

    public double getAverageTime(String startStation, String endStation) {
        RouteStats stats = routes.get(startStation + "->" + endStation);
        if (stats == null || stats.count == 0) return 0.0;
        return (double) stats.totalTime / stats.count;
    }
}""",
                'notes_java': "`Map.computeIfAbsent` ensures thread-safe atomic insertion of new RouteStats."
            },
            {
                'name': "Tuple Record Route Keys with Primitive Double Array",
                'desc': "Use primitive array `double[]{totalTime, count}` for RouteStats to reduce object overhead.",
                'time': "All O(1)",
                'space': "O(P + R)",
                'code_kotlin': """class UndergroundSystem() {
    private val checkIns = HashMap<Int, Pair<String, Int>>()
    private val routes = HashMap<String, DoubleArray>() // [totalTime, count]

    fun checkIn(id: Int, stationName: String, t: Int) {
        checkIns[id] = Pair(stationName, t)
    }

    fun checkOut(id: Int, stationName: String, t: Int) {
        val (startStation, startTime) = checkIns.remove(id) ?: return
        val key = "$startStation->$stationName"
        val arr = routes.getOrPut(key) { DoubleArray(2) }
        arr[0] += (t - startTime).toDouble()
        arr[1] += 1.0
    }

    fun getAverageTime(startStation: String, endStation: String): Double {
        val arr = routes["$startStation->$endStation"] ?: return 0.0
        return arr[0] / arr[1]
    }
}""",
                'notes_kotlin': "Destructuring declaration `val (startStation, startTime) = checkIns.remove(id)!!` is clean Kotlin syntax.",
                'code_python': """class UndergroundSystem:
    def __init__(self):
        from collections import defaultdict
        self.check_ins = {}
        self.routes = defaultdict(lambda: [0, 0])

    def checkIn(self, id: int, stationName: str, t: int) -> None:
        self.check_ins[id] = (stationName, t)

    def checkOut(self, id: int, stationName: str, t: int) -> None:
        start_station, start_time = self.check_ins.pop(id)
        stats = self.routes[(start_station, stationName)]
        stats[0] += t - start_time
        stats[1] += 1

    def getAverageTime(self, startStation: str, endStation: str) -> float:
        total_time, count = self.routes[(startStation, endStation)]
        return total_time / count""",
                'notes_python': "`defaultdict(lambda: [0, 0])` eliminates membership checks on checkOut.",
                'code_java': """import java.util.HashMap;
import java.util.Map;

class UndergroundSystem {
    private final Map<Integer, Object[]> checkIns = new HashMap<>();
    private final Map<String, double[]> routes = new HashMap<>();

    public void checkIn(int id, String stationName, int t) {
        checkIns.put(id, new Object[]{stationName, t});
    }

    public void checkOut(int id, String stationName, int t) {
        Object[] start = checkIns.remove(id);
        String key = (String) start[0] + "->" + stationName;
        double duration = t - (int) start[1];
        double[] stats = routes.computeIfAbsent(key, k -> new double[2]);
        stats[0] += duration;
        stats[1] += 1.0;
    }

    public double getAverageTime(String startStation, String endStation) {
        double[] stats = routes.get(startStation + "->" + endStation);
        return stats[0] / stats[1];
    }
}""",
                'notes_java': "Using primitive `double[2]` simplifies arithmetic and avoids custom inner class definitions."
            },
            {
                'name': "Optimal: Domain-Driven Object Entities with Immutability",
                'desc': "Production gold standard with strict domain models (PassengerTrip & RouteAggregator).",
                'time': "All operations O(1)",
                'space': "O(P + R)",
                'code_kotlin': """class UndergroundSystem() {
    /**
     * Optimal: Domain-Driven Two-Map Architecture
     * Time: All operations O(1)
     * Space: O(Passengers + Routes)
     */
    private class ActiveTrip(val origin: String, val departureTime: Int)

    private class RouteMetric {
        var cumulativeTime: Long = 0L
        var tripCount: Int = 0

        fun record(duration: Int) {
            cumulativeTime += duration
            tripCount++
        }

        fun average(): Double = cumulativeTime.toDouble() / tripCount
    }

    private val inFlightTrips = HashMap<Int, ActiveTrip>()
    private val routeMetrics = HashMap<String, RouteMetric>()

    fun checkIn(id: Int, stationName: String, t: Int) {
        inFlightTrips[id] = ActiveTrip(stationName, t)
    }

    fun checkOut(id: Int, stationName: String, t: Int) {
        val trip = inFlightTrips.remove(id) ?: return
        val key = trip.origin + "->" + stationName
        routeMetrics.getOrPut(key) { RouteMetric() }.record(t - trip.departureTime)
    }

    fun getAverageTime(startStation: String, endStation: String): Double {
        return routeMetrics[startStation + "->" + endStation]?.average() ?: 0.0
    }
}""",
                'notes_kotlin': "Clean separation of concerns. `RouteMetric` encapsulates its own aggregation and average calculation logic.",
                'code_python': """class UndergroundSystem:
    # Optimal: Domain Entities
    class RouteMetric:
        def __init__(self):
            self.total = 0
            self.count = 0
        def record(self, duration: int):
            self.total += duration
            self.count += 1
        def average(self) -> float:
            return self.total / self.count

    def __init__(self):
        self.active_trips = {}
        self.metrics = {}

    def checkIn(self, id: int, stationName: str, t: int) -> None:
        self.active_trips[id] = (stationName, t)

    def checkOut(self, id: int, stationName: str, t: int) -> None:
        start_station, start_time = self.active_trips.pop(id)
        key = (start_station, stationName)
        if key not in self.metrics:
            self.metrics[key] = self.RouteMetric()
        self.metrics[key].record(t - start_time)

    def getAverageTime(self, startStation: str, endStation: str) -> float:
        return self.metrics[(startStation, endStation)].average()""",
                'notes_python': "Encapsulated metric object makes code self-documenting for staff-level interview rubrics.",
                'code_java': """import java.util.HashMap;
import java.util.Map;

class UndergroundSystem {
    /**
     * Optimal: Domain Entity Encapsulation
     * Time: All operations O(1)
     * Space: O(P + R)
     */
    private static class ActiveTrip {
        final String station;
        final int time;
        ActiveTrip(String station, int time) {
            this.station = station;
            this.time = time;
        }
    }

    private static class RouteMetric {
        long totalDuration = 0L;
        int tripCount = 0;

        void record(int duration) {
            totalDuration += duration;
            tripCount++;
        }

        double getAverage() {
            return (double) totalDuration / tripCount;
        }
    }

    private final Map<Integer, ActiveTrip> activeTrips = new HashMap<>();
    private final Map<String, RouteMetric> routeMetrics = new HashMap<>();

    public void checkIn(int id, String stationName, int t) {
        activeTrips.put(id, new ActiveTrip(stationName, t));
    }

    public void checkOut(int id, String stationName, int t) {
        ActiveTrip trip = activeTrips.remove(id);
        if (trip == null) return;
        String key = trip.station + "->" + stationName;
        routeMetrics.computeIfAbsent(key, k -> new RouteMetric()).record(t - trip.time);
    }

    public double getAverageTime(String startStation, String endStation) {
        RouteMetric metric = routeMetrics.get(startStation + "->" + endStation);
        return (metric != null) ? metric.getAverage() : 0.0;
    }
}""",
                'notes_java': "The production gold standard for transit tracking microservices."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class UndergroundSystem() {
    private val checkIns = HashMap<Int, Pair<String, Int>>()
    private val routes = HashMap<String, Pair<Long, Int>>()
    fun checkIn(id: Int, s: String, t: Int) { checkIns[id] = Pair(s, t) }
    fun checkOut(id: Int, s: String, t: Int) {
        val (start, time) = checkIns.remove(id)!!
        val key = "$start->$s"
        val (tot, cnt) = routes.getOrDefault(key, Pair(0L, 0))
        routes[key] = Pair(tot + (t - time), cnt + 1)
    }
    fun getAverageTime(start: String, end: String): Double {
        val (tot, cnt) = routes["$start->$end"]!!
        return tot.toDouble() / cnt
    }
}""",
            'python_boilerplate': """class UndergroundSystem:
    def __init__(self):
        self.check_ins = {}
        self.routes = {}
    def checkIn(self, id: int, s: str, t: int) -> None:
        self.check_ins[id] = (s, t)
    def checkOut(self, id: int, s: str, t: int) -> None:
        start, time = self.check_ins.pop(id)
        key = (start, s)
        tot, cnt = self.routes.get(key, (0, 0))
        self.routes[key] = (tot + t - time, cnt + 1)
    def getAverageTime(self, start: str, end: str) -> float:
        tot, cnt = self.routes[(start, end)]
        return tot / cnt""",
            'java_boilerplate': """class UndergroundSystem {
    private final java.util.Map<Integer, Object[]> checkIns = new java.util.HashMap<>();
    private final java.util.Map<String, double[]> routes = new java.util.HashMap<>();
    public void checkIn(int id, String s, int t) { checkIns.put(id, new Object[]{s, t}); }
    public void checkOut(int id, String s, int t) {
        Object[] st = checkIns.remove(id);
        String k = (String) st[0] + "->" + s;
        double[] r = routes.computeIfAbsent(k, x -> new double[2]);
        r[0] += (t - (int) st[1]); r[1] += 1;
    }
    public double getAverageTime(String start, String end) {
        double[] r = routes.get(start + "->" + end);
        return r[0] / r[1];
    }
}""",
            'js_test_runner': """
      const checkIns = {};
      const routes = {};
      const checkIn = (id, s, t) => { checkIns[id] = { s: s, t: t }; };
      const checkOut = (id, s, t) => {
        const start = checkIns[id];
        delete checkIns[id];
        const k = `${start.s}->${s}`;
        if (!routes[k]) routes[k] = { total: 0, count: 0 };
        routes[k].total += (t - start.t);
        routes[k].count++;
      };
      const getAvg = (s1, s2) => {
        const r = routes[`${s1}->${s2}`];
        return r ? (r.total / r.count) : 0;
      };

      checkIn(45, "Leyton", 3);
      checkIn(32, "Paradise", 8);
      checkIn(27, "Leyton", 10);
      checkOut(45, "Waterloo", 15);
      checkOut(27, "Waterloo", 20);
      checkOut(32, "Cambridge", 22);

      const avg1 = getAvg("Paradise", "Cambridge"); // 14.0
      const avg2 = getAvg("Leyton", "Waterloo"); // (12 + 10) / 2 = 11.0

      const passed = (Math.abs(avg1 - 14.0) < 1e-4 && Math.abs(avg2 - 11.0) < 1e-4);
      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = `
        <div class="p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}">
          <span>Example 1 Transit Suite</span>
          <span>${passed ? '&#10004; PASSED (Paradise->Cambridge=14.0, Leyton->Waterloo=11.0)' : '&#10008; FAILED'}</span>
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
            'phase1': "I clarify: can a customer check in again before checking out? No, constraints guarantee customer is checked into at most one place. Are station names case-sensitive? Yes. Does the direction matter? Yes, average time from A to B is separate from B to A.",
            'phase2': "For strategy: saving all completed trips to a list would require O(N) linear filtering for every getAverageTime call. Instead, we can achieve O(1) time for ALL methods by using two HashMaps: checkInMap storing active in-flight passenger trips by customer ID, and routeStatsMap precomputing cumulative time and trip count by composite route key (startStation + '->' + endStation).",
            'phase3': "In checkIn, I put (station, time) in checkInMap. In checkOut, I remove the entry by id to avoid memory leaks, calculate duration = t - checkIn.time, and update totalTime += duration and count++ in routeStatsMap. In getAverageTime, I read the route object in O(1) time and return totalTime / count.",
            'phase4': "Dry running edge cases: a passenger checking out must remove their ID from checkInMap so if they travel again tomorrow with the same card ID, old state won't interfere. Decimal accuracy: casting totalTime to double avoids integer truncation. Complexities are strictly O(1) time.",
            'senior_followup': "Senior Scalability: In a distributed system with turnstiles across 500 metro stations, checkIn events and checkOut events arrive at different microservices. We would publish events to an Apache Kafka topic partitioned by passenger ID, processing check-outs via a stateful stream processor (Apache Flink) that maintains keyed state and writes aggregated route metrics to Redis."
        },
        'quiz': [
            {
                'question': "Why is it important to CALL `checkInMap.remove(id)` rather than `checkInMap.get(id)` during `checkOut`?",
                'options': [
                    "Because Java HashMaps throw an exception if you do not remove keys.",
                    "To prevent memory leaks: once a passenger finishes their trip, keeping their check-in record in memory indefinitely would exhaust RAM over time.",
                    "Because removing keys automatically computes the average.",
                    "Because passenger IDs can only be used once in their lifetime."
                ],
                'correct_index': 1,
                'explanation': "In a 24/7 production system with millions of riders, failing to remove completed check-ins causes the `checkInMap` to grow unboundedly, eventually causing an OutOfMemoryError."
            },
            {
                'question': "What is the time complexity of `getAverageTime` in the pre-aggregated two-map architecture?",
                'options': [
                    "O(N) where N is the total number of trips completed.",
                    "O(log N) due to binary search.",
                    "O(1) constant time because cumulative duration and count are stored directly.",
                    "O(S) where S is the number of stations in the city."
                ],
                'correct_index': 2,
                'explanation': "Because the sum of durations and the count of trips are maintained incrementally on each checkout, `getAverageTime` only performs a single hash map lookup and a division in O(1) time."
            }
        ]
    }
])

def main():
    import re
    from template_engine import render_lesson

    c = open('master-plan.html').read()
    files = {}
    for m in re.finditer(r'{\s*num:\s*([0-9]+),\s*title:\s*\"([^\"]+)\",\s*lc:\s*([^,]+),\s*pass:\s*\"([^\"]+)\".*?file:\s*\"([^\"]+)\"', c):
        num = int(m.group(1))
        fname = m.group(5)
        files[num] = fname

    print(f"Generating Cluster 2: {len(cluster2_problems)} problems (28 to 33)...")
    for p in cluster2_problems:
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
