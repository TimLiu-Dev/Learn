import sys
sys.path.append('scripts')

p68 = {
    'num': 68,
    'lc': 380,
    'title': "Insert Delete GetRandom O(1)",
    'category': "Linked Lists & System Node Design",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "insert-delete-getrandom-o1",
    'verbatim_statement': """<p>Implement the <code>RandomizedSet</code> class:</p>
<ul>
	<li><code>RandomizedSet()</code> Initializes the <code>RandomizedSet</code> object.</li>
	<li><code>bool insert(int val)</code> Inserts an item <code>val</code> into the set if not present. Returns <code>true</code> if the item was not present, <code>false</code> otherwise.</li>
	<li><code>bool remove(int val)</code> Removes an item <code>val</code> from the set if present. Returns <code>true</code> if the item was present, <code>false</code> otherwise.</li>
	<li><code>int getRandom()</code> Returns a random element from the current set of elements (it's guaranteed that at least one element exists when this method is called). Each element must have the <b>same probability</b> of being returned.</li>
</ul>
<p>You must implement the functions of the class such that each function works in <b>average</b> <code>O(1)</code> time complexity.</p>""",
    'examples': [
        {'title': "Example 1", 'input': '["RandomizedSet", "insert", "remove", "insert", "getRandom", "remove", "insert", "getRandom"]\n[[], [1], [2], [2], [], [1], [2], []]', 'output': '[null, true, false, true, 2, true, false, 2]\nExplanation:\nRandomizedSet randomizedSet = new RandomizedSet();\nrandomizedSet.insert(1); // Inserts 1 to the set. Returns true as 1 was inserted successfully.\nrandomizedSet.remove(2); // Returns false as 2 does not exist in the set.\nrandomizedSet.insert(2); // Inserts 2 to the set, returns true. Set now contains [1,2].\nrandomizedSet.getRandom(); // getRandom() should return either 1 or 2 randomly.\nrandomizedSet.remove(1); // Removes 1 from the set, returns true. Set now contains [2].\nrandomizedSet.insert(2); // 2 was already in the set, so return false.\nrandomizedSet.getRandom(); // Since 2 is the only number in the set, getRandom() will always return 2.'}
    ],
    'constraints': [
        "-2^31 <= val <= 2^31 - 1",
        "At most 2 * 10^5 calls will be made to insert, remove, and getRandom.",
        "There will be at least one element in the data structure when getRandom is called."
    ],
    'plain_english': "A HashMap provides O(1) insert and delete, but cannot pick an element uniformly at random in O(1) time. An ArrayList allows O(1) random index sampling, but deletion from an arbitrary index takes O(N) due to array shifting. To get O(1) across all three operations, we combine an ArrayList with a HashMap<Value, Index>. For deletion, we swap the target element with the last element in the list, update the map, and pop the last element in O(1) time without shifting.",
    'boundary_checks': {
        'min_base': "Single element <code>insert(1) -> getRandom() -> remove(1)</code> &rarr; Returns 1, then empty.",
        'best_exit': "Remove element already at the end &rarr; No swap needed; simple O(1) pop.",
        'worst_scan': "Duplicate insert <code>insert(1) -> insert(1)</code> &rarr; Second call returns false immediately in O(1)."
    },
    'mental_model': {
        'analogy_title': "The Swapping Parking Lot",
        'analogy_text': "Imagine a parking lot with numbered parking stalls (an array) and a guard booth ledger listing where each license plate is parked (a HashMap). If car #3 wants to leave, you don't shift all cars from stall #4 to #100 forward! Instead, you take the car in the very last stall, drive it into stall #3, update the guard's ledger, and close the last stall. Now stall #3 is occupied, the last stall is free, and nobody else had to move.",
        'aha_moment': "Array deletion is only O(N) because shifting elements preserves order. But a Set has NO concept of order! Swapping the element to delete with the tail element turns an O(N) shift into an O(1) pop.",
        'trigger': "Insert, Delete, and GetRandom all in O(1) average time &rarr; ArrayList + HashMap with Swap-With-Last deletion.",
        'sisters': [
            {'name': "Insert Delete GetRandom O(1) - Duplicates allowed", 'lc': "LC #381"},
            {'name': "LRU Cache", 'lc': "LC #146"},
            {'name': "LFU Cache", 'lc': "LC #460"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(1) Insert/Delete, O(N) GetRandom",
        'naive_text': "Use a standard HashSet. GetRandom converts the set to an array or iterates N elements to pick randomly.",
        'wasted_work': "Converting set to array or advancing iterator on every getRandom() call.",
        'optimal_complexity': "O(1) Insert, O(1) Delete, O(1) GetRandom",
        'optimal_text': "ArrayList stores values for O(1) random index access; HashMap<Val, Index> stores positions for O(1) lookups and swaps.",
        'core_invariant': "For every entry in `map`, `list[map[val]] == val`. `list.size == map.size` at all times.",
        'table_rows': [
            {'name': "1. HashSet with Iteration", 'time': "Insert: O(1), Delete: O(1), Random: O(N)", 'space': "O(N)", 'note': "GetRandom violates O(1) requirement."},
            {'name': "2. ArrayList with Linear Search", 'time': "Insert: O(1), Delete: O(N), Random: O(1)", 'space': "O(N)", 'note': "Delete requires O(N) search and shifting."},
            {'name': "★ 3. ArrayList + HashMap (Swap-with-Last)", 'time': "Insert: O(1), Delete: O(1), Random: O(1)", 'space': "O(N)", 'note': "Gold standard; all operations strictly O(1) average."}
        ]
    },
    'trace_table': {
        'input_desc': 'insert(10), insert(20), insert(30), remove(20), getRandom()',
        'columns': ["Operation", "val", "ArrayList State", "HashMap (val -> idx)", "Return Value", "Action Details"],
        'rows': [
            ["insert", "10", "[10]", "{10: 0}", "true", "Appended 10 at index 0"],
            ["insert", "20", "[10, 20]", "{10: 0, 20: 1}", "true", "Appended 20 at index 1"],
            ["insert", "30", "[10, 20, 30]", "{10: 0, 20: 1, 30: 2}", "true", "Appended 30 at index 2"],
            ["remove", "20", "[10, 30]", "{10: 0, 30: 1}", "true", "Swap 20 (idx 1) with last element 30 (idx 2); pop tail; map updated"],
            ["getRandom", "-", "[10, 30]", "{10: 0, 30: 1}", "10 or 30 (50% each)", "Random index in [0, 1] selected in O(1)"]
        ],
        'takeaway': "Removing 20 took O(1) time because 30 was moved to index 1 and the tail was popped without shifting."
    },
    'stepper': {
        'title': "Swap-With-Last O(1) Deletion Stepper",
        'default_input': 'insert(10) -> insert(20) -> insert(30) -> remove(20)',
        'presets': [
            {'label': "Canonical Swap [10, 20, 30] remove 20", 'onclick': 'loadPreset("swap")'},
            {'label': "Remove Tail [10, 20] remove 20", 'onclick': 'loadPreset("tail")'},
            {'label': "Single Item [10] remove 10", 'onclick': 'loadPreset("single")'}
        ],
        'metrics': [
            {'id': "metric-op", 'label': "Operation", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-size", 'label': "Set Size", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-tail", 'label': "Tail Element", 'color': "text-purple-400", 'initial': "-"},
            {'id': "metric-res", 'label': "Return Value", 'color': "text-emerald-400", 'initial': "-"}
        ],
        'visual_html': '<div id="p380-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace swap-with-last O(1) operations.',
        'pseudocode_lines': [
            "fun insert(val): Boolean { if (val in map) return false; map[val] = list.size; list.add(val); return true }",
            "fun remove(val): Boolean {",
            "    val idx = map[val] ?: return false",
            "    val lastVal = list.last()",
            "    list[idx] = lastVal; map[lastVal] = idx",
            "    list.removeAt(list.lastIndex); map.remove(val)",
            "    return true",
            "}"
        ],
        'js_code': """
    let currentMode = "swap";

    function buildSteps(mode) {
      steps = [];
      if (mode === "swap") {
        steps.push({ op: "init", size: 0, tail: "-", res: "-", list: [], map: {}, line: 1, narration: "Initialized empty RandomizedSet." });
        steps.push({ op: "insert(10)", size: 1, tail: 10, res: "true", list: [10], map: { 10: 0 }, line: 1, narration: "Inserted 10 at index 0. Map: {10: 0}." });
        steps.push({ op: "insert(20)", size: 2, tail: 20, res: "true", list: [10, 20], map: { 10: 0, 20: 1 }, line: 1, narration: "Inserted 20 at index 1. Map: {10: 0, 20: 1}." });
        steps.push({ op: "insert(30)", size: 3, tail: 30, res: "true", list: [10, 20, 30], map: { 10: 0, 20: 1, 30: 2 }, line: 1, narration: "Inserted 30 at index 2. Map: {10: 0, 20: 1, 30: 2}." });
        steps.push({ op: "remove(20) [Target idx 1]", size: 3, tail: 30, res: "Swapping...", list: [10, 30, 30], map: { 10: 0, 30: 1 }, line: 5, narration: "Swap target 20 (idx 1) with tail element 30 (idx 2). Updated map: {30: 1}." });
        steps.push({ op: "remove(20) [Pop Tail]", size: 2, tail: 30, res: "true", list: [10, 30], map: { 10: 0, 30: 1 }, line: 6, narration: "Popped tail in O(1). 20 removed completely without array shifting!" });
        steps.push({ op: "getRandom()", size: 2, tail: 30, res: "10 or 30", list: [10, 30], map: { 10: 0, 30: 1 }, line: 7, narration: "Random index 0 or 1 picked with 50% probability." });
      }
    }

    function renderVisual(s) {
      const stage = document.getElementById('p380-stage');
      if (!stage) return;
      let html = '<div class="flex flex-col items-center gap-4 w-full max-w-xl">';

      // List
      html += '<div class="flex items-center gap-2"><span class="text-xs font-mono text-slate-400 w-16">ArrayList:</span>';
      if (s.list.length === 0) {
        html += '<span class="text-xs font-mono text-slate-500 italic">Empty</span>';
      } else {
        s.list.forEach((v, i) => {
          html += `<div class="w-12 h-12 rounded-lg border font-mono text-xs flex flex-col items-center justify-center bg-slate-900 border-slate-700 text-white"><span class="font-bold">${v}</span><span class="text-[9px] text-cyan-400">idx ${i}</span></div>`;
        });
      }
      html += '</div>';

      // Map
      html += '<div class="flex flex-wrap items-center justify-center gap-2"><span class="text-xs font-mono text-slate-400">Map (Val &rarr; Idx):</span>';
      Object.keys(s.map).forEach(k => {
        html += `<span class="px-2.5 py-1 rounded bg-slate-950 border border-slate-700 text-xs font-mono text-amber-300">${k} &rarr; <strong class="text-emerald-400">${s.map[k]}</strong></span>`;
      });
      html += '</div>';

      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-op').textContent = s.op;
      document.getElementById('metric-size').textContent = s.size;
      document.getElementById('metric-tail').textContent = s.tail;
      document.getElementById('metric-res').textContent = s.res;
    }

    function loadPreset(mode) {
      currentMode = mode;
      buildSteps(currentMode);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Production Optimal: ArrayList + HashMap with Swap-With-Last Removal",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "Insert: O(1), Delete: O(1), GetRandom: O(1)",
            'summary': "ArrayList stores values for uniform random indexing; HashMap maps each value to its index in the list. On deletion, overwrite target with tail element and pop the tail.",
            'kotlin_code': """import kotlin.random.Random

class RandomizedSet() {
    private val list = ArrayList<Int>()
    private val map = HashMap<Int, Int>() // val -> index in list

    fun insert(`val`: Int): Boolean {
        if (map.containsKey(`val`)) return false
        map[`val`] = list.size
        list.add(`val`)
        return true
    }

    fun remove(`val`: Int): Boolean {
        val idx = map[`val`] ?: return false
        val lastIdx = list.size - 1
        val lastVal = list[lastIdx]

        // Swap target with last element
        list[idx] = lastVal
        map[lastVal] = idx

        // Pop last element
        list.removeAt(lastIdx)
        map.remove(`val`)
        return true
    }

    fun getRandom(): Int {
        val randIdx = Random.nextInt(list.size)
        return list[randIdx]
    }
}""",
            'python_code': """import random

class RandomizedSet:
    def __init__(self):
        self.list = []
        self.map = {}  # val -> index in list

    def insert(self, val: int) -> bool:
        if val in self.map:
            return False
        self.map[val] = len(self.list)
        self.list.append(val)
        return True

    def remove(self, val: int) -> bool:
        if val not in self.map:
            return False
        idx = self.map[val]
        last_val = self.list[-1]

        # Swap target with last element
        self.list[idx] = last_val
        self.map[last_val] = idx

        # Pop last element
        self.list.pop()
        del self.map[val]
        return True

    def getRandom(self) -> int:
        return random.choice(self.list)""",
            'java_code': """import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class RandomizedSet {
    private final List<Integer> list;
    private final Map<Integer, Integer> map;
    private final Random rand;

    public RandomizedSet() {
        list = new ArrayList<>();
        map = new HashMap<>();
        rand = new Random();
    }

    public boolean insert(int val) {
        if (map.containsKey(val)) return false;
        map.put(val, list.size());
        list.add(val);
        return true;
    }

    public boolean remove(int val) {
        Integer idx = map.get(val);
        if (idx == null) return false;

        int lastIdx = list.size() - 1;
        int lastVal = list.get(lastIdx);

        // Swap with last element
        list.set(idx, lastVal);
        map.put(lastVal, idx);

        // Pop tail
        list.remove(lastIdx);
        map.remove(val);
        return true;
    }

    public int getRandom() {
        return list.get(rand.nextInt(list.size()));
    }
}""",
            'memory_breakdown': "O(N) memory for ArrayList and HashMap. Every method achieves strict O(1) average time complexity."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class RandomizedSet() {
    // Write your optimal O(1) ArrayList + HashMap implementation
    private val list = ArrayList<Int>()
    private val map = HashMap<Int, Int>()
    private val rand = java.util.Random()

    fun insert(`val`: Int): Boolean {
        if (map.containsKey(`val`)) return false
        map[`val`] = list.size
        list.add(`val`)
        return true
    }

    fun remove(`val`: Int): Boolean {
        val idx = map[`val`] ?: return false
        val lastIdx = list.size - 1
        val lastVal = list[lastIdx]
        list[idx] = lastVal
        map[lastVal] = idx
        list.removeAt(lastIdx)
        map.remove(`val`)
        return true
    }

    fun getRandom(): Int = list[rand.nextInt(list.size)]
}""",
        'python_boilerplate': """import random

class RandomizedSet:
    # Write your optimal O(1) ArrayList + HashMap implementation
    def __init__(self):
        self.list = []
        self.map = {}
    def insert(self, val: int) -> bool:
        if val in self.map: return False
        self.map[val] = len(self.list)
        self.list.append(val)
        return True
    def remove(self, val: int) -> bool:
        if val not in self.map: return False
        idx = self.map[val]
        last = self.list[-1]
        self.list[idx] = last
        self.map[last] = idx
        self.list.pop()
        del self.map[val]
        return True
    def getRandom(self) -> int:
        return random.choice(self.list)""",
        'java_boilerplate': """import java.util.*;

class RandomizedSet {
    // Write your optimal O(1) ArrayList + HashMap implementation
    List<Integer> list = new ArrayList<>();
    Map<Integer, Integer> map = new HashMap<>();
    Random rand = new Random();

    public boolean insert(int val) {
        if (map.containsKey(val)) return false;
        map.put(val, list.size());
        list.add(val);
        return true;
    }

    public boolean remove(int val) {
        Integer idx = map.get(val);
        if (idx == null) return false;
        int lastIdx = list.size() - 1;
        int lastVal = list.get(lastIdx);
        list.set(idx, lastVal);
        map.put(lastVal, idx);
        list.remove(lastIdx);
        map.remove(val);
        return true;
    }

    public int getRandom() {
        return list.get(rand.nextInt(list.size()));
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      class RandomizedSet {
        constructor() { this.list = []; this.map = new Map(); }
        insert(val) {
          if (this.map.has(val)) return false;
          this.map.set(val, this.list.length);
          this.list.push(val);
          return true;
        }
        remove(val) {
          if (!this.map.has(val)) return false;
          const idx = this.map.get(val);
          const lastVal = this.list[this.list.length - 1];
          this.list[idx] = lastVal;
          this.map.set(lastVal, idx);
          this.list.pop();
          this.map.delete(val);
          return true;
        }
        getRandom() {
          return this.list[Math.floor(Math.random() * this.list.length)];
        }
      }

      const rs = new RandomizedSet();
      const ins1 = rs.insert(1);
      const rem2 = rs.remove(2);
      const ins2 = rs.insert(2);
      const r1 = rs.getRandom();
      const rem1 = rs.remove(1);
      const ins2Again = rs.insert(2);
      const r2 = rs.getRandom();

      const pass = ins1 === true && rem2 === false && ins2 === true && (r1 === 1 || r1 === 2) && rem1 === true && ins2Again === false && r2 === 2;
      const row = document.createElement('div');
      row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
      row.textContent = `Test: ins(1)=${ins1}, rem(2)=${rem2}, ins(2)=${ins2}, rem(1)=${rem1}, ins(2 again)=${ins2Again}, r2=${r2} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: insert, remove, and getRandom must all execute in average O(1) time. getRandom must return an element chosen uniformly at random among currently stored elements. Elements fit in 32-bit signed integers, and up to 200,000 operations can be invoked.\"",
        'phase2': "\"If we only use a HashSet, insert and delete are O(1), but getRandom cannot access an element by random index in O(1) without iterating. If we only use an ArrayList, getRandom is O(1) by generating a random index from 0 to size-1, but deleting an element at an arbitrary index takes O(N) due to shifting. To get O(1) for all three, we combine both: an ArrayList to store values and a HashMap to store value-to-index mappings. When deleting an element, we don't shift elements. Instead, we swap the target element with the last element in the list, update the index in the map, and pop the tail in O(1) time.\"",
        'phase3': "\"Let me write the code. I declare `list = ArrayList<Int>()` and `map = HashMap<Int, Int>()`. In `insert(val)`: if `val` is already in map, return false. Otherwise, put `map[val] = list.size`, `list.add(val)`, return true. In `remove(val)`: if not in map, return false. Retrieve its index `idx`. Get `lastVal = list.last()`. Set `list[idx] = lastVal` and `map[lastVal] = idx`. Then `list.removeAt(lastIdx)` and `map.remove(val)`. Return true. In `getRandom()`: return `list[rand.nextInt(list.size)]`.\"",
        'phase4': "\"Tracing with `insert(1)`, `remove(2)` (returns false), `insert(2)`. List is `[1, 2]`. Call `getRandom()` &rarr; returns 1 or 2 with 50% probability each. Call `remove(1)`: index of 1 is 0, lastVal is 2. `list[0]` becomes 2, `map[2]` becomes 0. List pops tail &rarr; list is `[2]`. Then `insert(2)` returns false. `getRandom()` returns 2 with 100% probability. Handled cleanly.\"",
        'senior_followup': "\"If duplicates were allowed (LC #381), the map would store `HashMap<Integer, LinkedHashSet<Integer>>` mapping each value to a set of indices where it appears in the list. The swap-with-last removal logic remains the same, updating the index set in O(1) time.\""
    },
    'quiz': [
        {
            'question': "Why does a standard HashSet fail to achieve O(1) for getRandom()?",
            'options': [
                "HashSet does not support random index-based access; accessing a random element requires O(N) iteration",
                "HashSet hash buckets are encrypted in memory preventing random number generator access",
                "HashSet size is not known until the garbage collector runs a complete memory sweep",
                "Operating systems do not permit pseudo-random numbers to index into hash tables"
            ],
            'correct_index': 0,
            'explanation': "HashSets have non-contiguous hash buckets with collisions. To pick an element uniformly at random, you would have to iterate up to a random index, which takes O(N) time."
        },
        {
            'question': "How is O(1) deletion achieved in the ArrayList without incurring O(N) shifting?",
            'options': [
                "Overwrite target index with the last element, update map index, and pop the tail",
                "Mark the element as null and run a background defragmentation thread every minute",
                "Sort the array list after every removal using introspective QuickSort",
                "Allocate a completely new array of size N-1 and copy elements using native memcpy"
            ],
            'correct_index': 0,
            'explanation': "Since a set has no required ordering, swapping the target element with the tail element allows popping the tail in O(1) time without shifting any other elements."
        }
    ]
}

p69 = {
    'num': 69,
    'lc': 146,
    'title': "LRU Cache",
    'category': "Linked Lists & System Node Design",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "lru-cache",
    'verbatim_statement': """<p>Design a data structure that follows the constraints of a <strong><a href="https://en.wikipedia.org/wiki/Cache_replacement_policies#LRU" target="_blank">Least Recently Used (LRU) cache</a></strong>.</p>
<p>Implement the <code>LRUCache</code> class:</p>
<ul>
	<li><code>LRUCache(int capacity)</code> Initialize the LRU cache with <strong>positive</strong> size <code>capacity</code>.</li>
	<li><code>int get(int key)</code> Return the value of the <code>key</code> if the key exists, otherwise return <code>-1</code>.</li>
	<li><code>void put(int key, int value)</code> Update the value of the <code>key</code> if the <code>key</code> exists. Otherwise, add the <code>key-value</code> pair to the cache. If the number of keys exceeds the <code>capacity</code> from this operation, <strong>evict</strong> the least recently used key.</li>
</ul>
<p>The functions <code>get</code> and <code>put</code> must each run in <code>O(1)</code> average time complexity.</p>""",
    'examples': [
        {'title': "Example 1", 'input': '["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]\n[[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]', 'output': '[null, null, null, 1, null, -1, null, -1, 3, 4]\nExplanation:\nLRUCache lRUCache = new LRUCache(2);\nlRUCache.put(1, 1); // cache is {1=1}\nlRUCache.put(2, 2); // cache is {1=1, 2=2}\nlRUCache.get(1);    // return 1\nlRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}\nlRUCache.get(2);    // returns -1 (not found)\nlRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}\nlRUCache.get(1);    // return -1 (not found)\nlRUCache.get(3);    // return 3\nlRUCache.get(4);    // return 4'}
    ],
    'constraints': [
        "1 <= capacity <= 3000",
        "0 <= key <= 10^4",
        "0 <= value <= 10^5",
        "At most 2 * 10^5 calls will be made to get and put."
    ],
    'plain_english': "A Least Recently Used (LRU) cache must support O(1) lookup and O(1) eviction of the oldest item. We combine a HashMap<Key, Node> for instant O(1) node lookup with a Doubly Linked List with dummy head (Most Recently Used) and dummy tail (Least Recently Used) sentinels. When an item is accessed (get or put), we splice it out of its current position and insert it right behind head. When capacity is exceeded, we evict the node right in front of tail.",
    'boundary_checks': {
        'min_base': "Capacity = 1 <code>cap = 1</code> &rarr; Every new key immediately evicts previous key.",
        'best_exit': "Overwrite existing key <code>put(1, 10) -> put(1, 20)</code> &rarr; Updates value and moves to MRU without increasing cache size.",
        'worst_scan': "Continuous gets on non-existent keys &rarr; Returns -1 immediately in O(1)."
    },
    'mental_model': {
        'analogy_title': "The Magazine Rack with Bookends",
        'analogy_text': "Imagine a magazine rack that holds at most C issues. The front bookend is labeled 'Most Recently Read', and the back bookend is 'Dustiest / Oldest'. Whenever you pick up a magazine (get or put), you pull it out from wherever it was and place it right at the front behind the front bookend. When the rack is full and a new issue arrives, you toss out the magazine sitting right in front of the back bookend.",
        'aha_moment': "A Doubly Linked List with dummy head and tail sentinels eliminates all null checks and edge cases for empty lists, single-node lists, or boundary updates! Splice out in O(1), prepend behind head in O(1).",
        'trigger': "Cache with O(1) key access and least recently used eviction &rarr; HashMap<Key, Node> + Doubly Linked List with dummy sentinels.",
        'sisters': [
            {'name': "LFU Cache (Least Frequently Used)", 'lc': "LC #460"},
            {'name': "Design In-Memory File System", 'lc': "LC #588"},
            {'name': "Design Hit Counter", 'lc': "LC #362"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(1) Get, O(N) Put / Evict",
        'naive_text': "Store timestamps in a HashMap. On eviction, scan all N keys to find the minimum timestamp.",
        'wasted_work': "Scanning all entries linearly to find the oldest timestamp ($O(N)$ per write on N=3000).",
        'optimal_complexity': "O(1) Get, O(1) Put",
        'optimal_text': "Doubly Linked List node pointers (`prev` and `next`) provide instant O(1) detachment and re-attachment. HashMap maps key to Node pointer.",
        'core_invariant': "`head.next` always points to the Most Recently Used node; `tail.prev` always points to the Least Recently Used node. `map.size <= capacity` at all times.",
        'table_rows': [
            {'name': "1. Timestamp Map Linear Scan", 'time': "Get: O(1), Put: O(N)", 'space': "O(C)", 'note': "Scanning timestamps times out on large caches."},
            {'name': "2. Java LinkedHashMap", 'time': "Get: O(1), Put: O(1)", 'space': "O(C)", 'note': "Uses built-in access-order; forbidden in pure interviews."},
            {'name': "★ 3. Custom Doubly Linked List + HashMap", 'time': "Get: O(1), Put: O(1)", 'space': "O(C)", 'note': "Production standard; zero framework dependency."}
        ]
    },
    'trace_table': {
        'input_desc': 'Capacity = 2: put(1,1) -> put(2,2) -> get(1) -> put(3,3) -> get(2)',
        'columns': ["Operation", "Args", "List State (Head <-> Tail)", "HashMap Keys", "Evicted Key", "Return Value"],
        'rows': [
            ["put", "(1, 1)", "head <-> [1:1] <-> tail", "{1}", "none", "void"],
            ["put", "(2, 2)", "head <-> [2:2] <-> [1:1] <-> tail", "{1, 2}", "none", "void"],
            ["get", "1", "head <-> [1:1] <-> [2:2] <-> tail", "{1, 2}", "none", "1 (moved to head)"],
            ["put", "(3, 3)", "head <-> [3:3] <-> [1:1] <-> tail", "{1, 3}", "2 (was at tail.prev)", "void (evicted 2)"],
            ["get", "2", "head <-> [3:3] <-> [1:1] <-> tail", "{1, 3}", "none", "-1 (not found)"]
        ],
        'takeaway': "When key 1 was read, it became MRU, making key 2 the LRU. Thus, when key 3 was inserted, key 2 was correctly evicted instead of key 1."
    },
    'stepper': {
        'title': "LRU Cache Doubly Linked List Stepper",
        'default_input': 'cap=2: put(1,1), put(2,2), get(1), put(3,3)',
        'presets': [
            {'label': "Canonical Cap=2", 'onclick': 'loadPreset(2)'},
            {'label': "Cap=3 with Eviction", 'onclick': 'loadPreset(3)'}
        ],
        'metrics': [
            {'id': "metric-op", 'label': "Operation", 'color': "text-amber-400", 'initial': "init(2)"},
            {'id': "metric-mru", 'label': "MRU Node (Head)", 'color': "text-cyan-400", 'initial': "none"},
            {'id': "metric-lru", 'label': "LRU Node (Tail)", 'color': "text-purple-400", 'initial': "none"},
            {'id': "metric-evicted", 'label': "Last Evicted", 'color': "text-rose-400", 'initial': "none"}
        ],
        'visual_html': '<div id="p146-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to observe node splicing and LRU eviction.',
        'pseudocode_lines': [
            "fun get(key): Int { val node = map[key] ?: return -1; moveToHead(node); return node.value }",
            "fun put(key, value) {",
            "    if (key in map) { val node = map[key]!; node.value = value; moveToHead(node) }",
            "    else { val node = Node(key, value); map[key] = node; addToHead(node)",
            "        if (map.size > capacity) { val lru = removeTail(); map.remove(lru.key) } }",
            "}"
        ],
        'js_code': """
    let cap = 2;

    function buildSteps(capacity) {
      steps = [];
      let list = []; // items ordered from head to tail
      let evicted = "none";

      steps.push({ op: `init(cap=${capacity})`, mru: "-", lru: "-", evicted: "none", list: [], line: 1, narration: `Initialized LRUCache with capacity ${capacity}.` });

      // put(1, 1)
      list = [{ k: 1, v: 1 }];
      steps.push({ op: "put(1, 1)", mru: "[1:1]", lru: "[1:1]", evicted: "none", list: [...list], line: 4, narration: "Inserted key 1. Cache: [1:1]." });

      // put(2, 2)
      list = [{ k: 2, v: 2 }, { k: 1, v: 1 }];
      steps.push({ op: "put(2, 2)", mru: "[2:2]", lru: "[1:1]", evicted: "none", list: [...list], line: 4, narration: "Inserted key 2 at MRU head. Cache: [2:2] <-> [1:1]." });

      // get(1)
      list = [{ k: 1, v: 1 }, { k: 2, v: 2 }];
      steps.push({ op: "get(1) -> 1", mru: "[1:1]", lru: "[2:2]", evicted: "none", list: [...list], line: 1, narration: "Accessed key 1! Spliced from position and moved to MRU head. Key 2 is now LRU." });

      // put(3, 3) -> evict 2
      list = [{ k: 3, v: 3 }, { k: 1, v: 1 }];
      evicted = "key 2";
      steps.push({ op: "put(3, 3) [EVICT]", mru: "[3:3]", lru: "[1:1]", evicted: "key 2", list: [...list], line: 5, narration: "Capacity exceeded! Evicted LRU tail node (key 2). Inserted key 3 at head." });

      // get(2) -> -1
      steps.push({ op: "get(2) -> -1", mru: "[3:3]", lru: "[1:1]", evicted: "key 2", list: [...list], line: 1, narration: "get(2) returned -1 (properly evicted)." });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p146-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-2 flex-wrap">';
      html += '<div class="px-3 py-2 rounded bg-slate-900 border border-slate-700 text-xs font-mono text-cyan-400 font-bold">HEAD (MRU)</div>';
      html += '<span class="text-slate-500 font-mono">&larr;&rarr;</span>';

      if (s.list.length === 0) {
        html += '<div class="text-xs font-mono text-slate-500 italic">Empty</div>';
      } else {
        s.list.forEach((item, idx) => {
          const isMRU = idx === 0;
          const isLRU = idx === s.list.length - 1;
          let cls = 'bg-slate-900 border-slate-700 text-slate-300';
          if (isMRU) cls = 'bg-cyan-500/20 border-cyan-400 text-cyan-200 ring-2 ring-cyan-500';
          else if (isLRU) cls = 'bg-purple-500/20 border-purple-400 text-purple-200 ring-2 ring-purple-500';

          html += `<div class="p-2.5 rounded-lg border font-mono text-xs flex flex-col items-center ${cls}"><span class="font-bold">Key ${item.k}</span><span class="text-[10px] text-slate-400">Val ${item.v}</span></div>`;
          if (idx < s.list.length - 1) html += '<span class="text-slate-500 font-mono">&larr;&rarr;</span>';
        });
      }

      html += '<span class="text-slate-500 font-mono">&larr;&rarr;</span>';
      html += '<div class="px-3 py-2 rounded bg-slate-900 border border-slate-700 text-xs font-mono text-purple-400 font-bold">TAIL (LRU)</div>';
      html += '</div>';

      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-op').textContent = s.op;
      document.getElementById('metric-mru').textContent = s.mru;
      document.getElementById('metric-lru').textContent = s.lru;
      document.getElementById('metric-evicted').textContent = s.evicted;
    }

    function loadPreset(c) {
      cap = c;
      buildSteps(cap);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Production Optimal: Custom Doubly Linked List + HashMap",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "Get: O(1), Put: O(1)",
            'summary': "Doubly Linked List with dummy head and tail sentinels for O(1) splicing. HashMap maps key directly to Node reference for instant O(1) lookup.",
            'kotlin_code': """class LRUCache(private val capacity: Int) {
    private class Node(var key: Int = 0, var value: Int = 0) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val map = HashMap<Int, Node>()
    private val head = Node()
    private val tail = Node()

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: Int): Int {
        val node = map[key] ?: return -1
        moveToHead(node)
        return node.value
    }

    fun put(key: Int, value: Int) {
        val node = map[key]
        if (node != null) {
            node.value = value
            moveToHead(node)
        } else {
            val newNode = Node(key, value)
            map[key] = newNode
            addToHead(newNode)
            if (map.size > capacity) {
                val lru = removeTail()
                map.remove(lru.key)
            }
        }
    }

    private fun addToHead(node: Node) {
        node.prev = head
        node.next = head.next
        head.next?.prev = node
        head.next = node
    }

    private fun removeNode(node: Node) {
        node.prev?.next = node.next
        node.next?.prev = node.prev
    }

    private fun moveToHead(node: Node) {
        removeNode(node)
        addToHead(node)
    }

    private fun removeTail(): Node {
        val lru = tail.prev!!
        removeNode(lru)
        return lru
    }
}""",
            'python_code': """class Node:
    def __init__(self, key: int = 0, value: int = 0):
        self.key = key
        self.value = value
        self.prev = None
        self.next = None

class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.map = {}
        self.head = Node()
        self.tail = Node()
        self.head.next = self.tail
        self.tail.prev = self.head

    def get(self, key: int) -> int:
        if key not in self.map:
            return -1
        node = self.map[key]
        self._move_to_head(node)
        return node.value

    def put(self, key: int, value: int) -> None:
        if key in self.map:
            node = self.map[key]
            node.value = value
            self._move_to_head(node)
        else:
            new_node = Node(key, value)
            self.map[key] = new_node
            self._add_to_head(new_node)
            if len(self.map) > self.capacity:
                lru = self._remove_tail()
                del self.map[lru.key]

    def _add_to_head(self, node: Node) -> None:
        node.prev = self.head
        node.next = self.head.next
        self.head.next.prev = node
        self.head.next = node

    def _remove_node(self, node: Node) -> None:
        node.prev.next = node.next
        node.next.prev = node.prev

    def _move_to_head(self, node: Node) -> None:
        self._remove_node(node)
        self._add_to_head(node)

    def _remove_tail(self) -> Node:
        lru = self.tail.prev
        self._remove_node(lru)
        return lru""",
            'java_code': """import java.util.HashMap;
import java.util.Map;

class LRUCache {
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;
        Node() {}
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node();
        this.tail = new Node();
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
        } else {
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            addToHead(newNode);
            if (map.size() > capacity) {
                Node lru = removeTail();
                map.remove(lru.key);
            }
        }
    }

    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private Node removeTail() {
        Node lru = tail.prev;
        removeNode(lru);
        return lru;
    }
}""",
            'memory_breakdown': "O(Capacity) space for doubly linked list nodes and hash map entries. Strict O(1) average time per operation with zero GC churn on hits."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class LRUCache(private val capacity: Int) {
    // Write your optimal Doubly Linked List + HashMap implementation
    private class Node(var k: Int = 0, var v: Int = 0) {
        var prev: Node? = null; var next: Node? = null
    }
    private val map = HashMap<Int, Node>()
    private val head = Node(); private val tail = Node()
    init { head.next = tail; tail.prev = head }

    fun get(key: Int): Int {
        val n = map[key] ?: return -1
        remove(n); addHead(n)
        return n.v
    }
    fun put(key: Int, value: Int) {
        if (key in map) {
            val n = map[key]!; n.v = value; remove(n); addHead(n)
        } else {
            val n = Node(key, value); map[key] = n; addHead(n)
            if (map.size > capacity) {
                val lru = tail.prev!!; remove(lru); map.remove(lru.k)
            }
        }
    }
    private fun addHead(n: Node) { n.prev = head; n.next = head.next; head.next?.prev = n; head.next = n }
    private fun remove(n: Node) { n.prev?.next = n.next; n.next?.prev = n.prev }
}""",
        'python_boilerplate': """class Node:
    def __init__(self, k=0, v=0):
        self.k, self.v = k, v
        self.prev = self.next = None

class LRUCache:
    # Write your optimal Doubly Linked List + HashMap implementation
    def __init__(self, capacity: int):
        self.cap = capacity
        self.map = {}
        self.head, self.tail = Node(), Node()
        self.head.next, self.tail.prev = self.tail, self.head

    def get(self, key: int) -> int:
        if key not in self.map: return -1
        n = self.map[key]
        self._remove(n); self._add(n)
        return n.v

    def put(self, key: int, value: int) -> None:
        if key in self.map:
            n = self.map[key]; n.v = value
            self._remove(n); self._add(n)
        else:
            n = Node(key, value); self.map[key] = n; self._add(n)
            if len(self.map) > self.cap:
                lru = self.tail.prev; self._remove(lru); del self.map[lru.k]

    def _add(self, n):
        n.prev, n.next = self.head, self.head.next
        self.head.next.prev = self.head.next = n

    def _remove(self, n):
        n.prev.next, n.next.prev = n.next, n.prev""",
        'java_boilerplate': """import java.util.*;

class LRUCache {
    // Write your optimal Doubly Linked List + HashMap implementation
    static class Node { int k, v; Node prev, next; Node() {} Node(int k, int v) { this.k=k; this.v=v; } }
    int cap; Map<Integer, Node> map = new HashMap<>();
    Node head = new Node(), tail = new Node();

    public LRUCache(int capacity) {
        this.cap = capacity;
        head.next = tail; tail.prev = head;
    }
    public int get(int key) {
        Node n = map.get(key);
        if (n == null) return -1;
        remove(n); addHead(n);
        return n.v;
    }
    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node n = map.get(key); n.v = value;
            remove(n); addHead(n);
        } else {
            Node n = new Node(key, value); map.put(key, n); addHead(n);
            if (map.size() > cap) {
                Node lru = tail.prev; remove(lru); map.remove(lru.k);
            }
        }
    }
    void addHead(Node n) { n.prev = head; n.next = head.next; head.next.prev = n; head.next = n; }
    void remove(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      class Node { constructor(k=0, v=0) { this.k = k; this.v = v; this.prev = null; this.next = null; } }
      class LRUCache {
        constructor(cap) {
          this.cap = cap; this.map = new Map();
          this.head = new Node(); this.tail = new Node();
          this.head.next = this.tail; this.tail.prev = this.head;
        }
        get(key) {
          if (!this.map.has(key)) return -1;
          const n = this.map.get(key);
          this.remove(n); this.addHead(n);
          return n.v;
        }
        put(key, val) {
          if (this.map.has(key)) {
            const n = this.map.get(key); n.v = val;
            this.remove(n); this.addHead(n);
          } else {
            const n = new Node(key, val); this.map.set(key, n); this.addHead(n);
            if (this.map.size > this.cap) {
              const lru = this.tail.prev; this.remove(lru); this.map.delete(lru.k);
            }
          }
        }
        addHead(n) { n.prev = this.head; n.next = this.head.next; this.head.next.prev = n; this.head.next = n; }
        remove(n) { n.prev.next = n.next; n.next.prev = n.prev; }
      }

      const cache = new LRUCache(2);
      cache.put(1, 1);
      cache.put(2, 2);
      const r1 = cache.get(1);
      cache.put(3, 3);
      const r2 = cache.get(2);
      cache.put(4, 4);
      const r3 = cache.get(1);
      const r4 = cache.get(3);
      const r5 = cache.get(4);

      const pass = r1 === 1 && r2 === -1 && r3 === -1 && r4 === 3 && r5 === 4;
      const row = document.createElement('div');
      row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
      row.textContent = `Test: get(1)=${r1}, get(2)=${r2}, get(1)=${r3}, get(3)=${r4}, get(4)=${r5} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: `capacity` is positive, up to 3000. `get` and `put` must both execute in average O(1) time complexity. When capacity is exceeded during a `put`, we must evict the least recently used key. A `get` or an update `put` both count as using the key.\"",
        'phase2': "\"To achieve O(1) for both operations, we need two components: a HashMap to locate keys in O(1) time, and a Doubly Linked List to maintain the recency ordering of keys. Singly linked list won't work because removing an arbitrary node requires O(1) access to its `prev` pointer. To prevent edge cases when inserting or removing from the ends, I will use dummy sentinel nodes: `head` (pointing to Most Recently Used) and `tail` (pointing to Least Recently Used). When an item is read or written, we remove it from its current position and prepend it directly behind `head`. If size exceeds capacity, `tail.prev` gives the LRU node, which we splice out and delete from the map in O(1).\"",
        'phase3': "\"Let me write the code. I define `class Node(key, value, prev, next)`. In `LRUCache(capacity)`, initialize `head` and `tail`, link `head.next = tail` and `tail.prev = head`. Helper `addHead(node)` links `node` between `head` and `head.next`. Helper `remove(node)` links `node.prev.next = node.next` and `node.next.prev = node.prev`. In `get(key)`: if not in map, return -1. Otherwise, call `remove(node)` and `addHead(node)`, return `node.value`. In `put(key, value)`: if key exists, update value, move to head. If new, create node, add to map, add to head; if `map.size > capacity`, remove `tail.prev` and delete from map.\"",
        'phase4': "\"Tracing with `cap = 2`: `put(1, 1)`, `put(2, 2)`. List is `head <-> [2:2] <-> [1:1] <-> tail`. Call `get(1)`: node 1 moves to head, list becomes `head <-> [1:1] <-> [2:2] <-> tail`. Returns 1. Now `put(3, 3)`: adds 3 to head. Size is 3 > 2. Evicts `tail.prev`, which is key 2! Map removes 2. Calling `get(2)` returns -1. Calling `get(1)` returns 1. Everything is O(1) and verified.\"",
        'senior_followup': "\"In high-concurrency production systems (like Guava Cache or Caffeine), a single global lock around LRU links causes thread contention. Modern caches use striped ring buffers or ConcurrentLinkedQueue with read-write batching, or the W-TinyLFU eviction algorithm to achieve millions of ops/sec across concurrent cores.\""
    },
    'quiz': [
        {
            'question': "Why is a Doubly Linked List required instead of a Singly Linked List?",
            'options': [
                "Deleting an arbitrary node in O(1) requires access to its previous node pointer",
                "Singly linked list cannot store primitive 32-bit integer values in modern JVMs",
                "Doubly linked lists automatically run garbage collection without pause times",
                "Operating system memory allocators only allocate bidirectional node descriptors"
            ],
            'correct_index': 0,
            'explanation': "In a singly linked list, splicing out a node requires finding its previous node by traversing from the head in O(N) time. A doubly linked list gives direct O(1) access to `node.prev`."
        },
        {
            'question': "What is the primary benefit of dummy head and tail sentinel nodes?",
            'options': [
                "They eliminate null-pointer checks and special-casing for empty or boundary nodes",
                "They store cache statistical telemetry to optimize CPU branch prediction",
                "They allow the cache to exceed its capacity by two additional secret entries",
                "They enforce thread safety without needing synchronized method blocks"
            ],
            'correct_index': 0,
            'explanation': "With dummy head and tail sentinels, every real node always has both a valid non-null `prev` and `next`, eliminating boundary check if-statements across `addHead` and `remove`."
        }
    ]
}

p70 = {
    'num': 70,
    'lc': 33,
    'title': "Search in Rotated Sorted Array",
    'category': "Binary Search",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "search-in-rotated-sorted-array",
    'verbatim_statement': """<p>There is an integer array <code>nums</code> sorted in ascending order (with <strong>distinct</strong> values).</p>
<p>Prior to being passed to your function, <code>nums</code> is <strong>possibly rotated</strong> at an unknown pivot index <code>k</code> (<code>1 <= k < nums.length</code>) such that the resulting array is <code>[nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]</code> (<strong>0-indexed</strong>). For example, <code>[0,1,2,4,5,6,7]</code> might be rotated at pivot index <code>3</code> and become <code>[4,5,6,7,0,1,2]</code>.</p>
<p>Given the array <code>nums</code> after the possible rotation and an integer <code>target</code>, return <em>the index of </em><code>target</code><em> if it is in </em><code>nums</code><em>, or </em><code>-1</code><em> if it is not in </em><code>nums</code>.</p>
<p>You must write an algorithm with <code>O(log n)</code> runtime complexity.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'nums = [4,5,6,7,0,1,2], target = 0', 'output': '4'},
        {'title': "Example 2", 'input': 'nums = [4,5,6,7,0,1,2], target = 3', 'output': '-1'},
        {'title': "Example 3", 'input': 'nums = [1], target = 0', 'output': '-1'}
    ],
    'constraints': [
        "1 <= nums.length <= 5000",
        "-10^4 <= nums[i] <= 10^4",
        "All values of nums are unique.",
        "nums is an ascending array that is possibly rotated.",
        "-10^4 <= target <= 10^4"
    ],
    'plain_english': "In any rotated sorted array, splitting the array at midpoint `mid` guarantees that at least one of the two halves is strictly sorted! We check if the left half is sorted (`nums[low] <= nums[mid]`). If it is, and target lies within `[nums[low]..nums[mid]]`, we search left (`high = mid - 1`); otherwise we search right (`low = mid + 1`). If the left half is not sorted, the right half must be sorted, so we apply the symmetric logic. This maintains O(log N) halving on every step.",
    'boundary_checks': {
        'min_base': "Single element <code>nums = [1], target = 1</code> &rarr; Returns 0; if target = 0 &rarr; Returns -1.",
        'best_exit': "Target at midpoint <code>nums[mid] == target</code> &rarr; Returns mid immediately.",
        'worst_scan': "Pivot at edge / unrotated array <code>nums = [1, 2, 3, 4, 5], target = 5</code> &rarr; Behaves as standard binary search."
    },
    'mental_model': {
        'analogy_title': "The Two Ramps with a Cliff",
        'analogy_text': "Imagine a ramp that was sliced and shifted: you have an ascending ramp on the left, a cliff drop in the middle, and another ascending ramp on the right. If you stand in the middle (`mid`), you look to your left: is the left side a smooth, unbroken ascending ramp (`nums[low] <= nums[mid]`)? If yes, and your target altitude is between `nums[low]` and `nums[mid]`, you stay on that smooth ramp. If not, your target must be on the other side of the cliff.",
        'aha_moment': "No matter where the pivot split is, the midpoint divides the array such that ONE half is guaranteed to be completely sorted without a pivot. A sorted half is trivially bounded by its endpoints `[low..mid]` or `[mid..high]`, allowing an exact O(1) membership check.",
        'trigger': "Search for target in rotated sorted array in O(log N) time &rarr; Modified Binary Search checking which half is sorted first.",
        'sisters': [
            {'name': "Find Minimum in Rotated Sorted Array", 'lc': "LC #153"},
            {'name': "Search in Rotated Sorted Array II (Duplicates)", 'lc': "LC #81"},
            {'name': "Find Peak Element", 'lc': "LC #162"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N) Time, O(1) Space",
        'naive_text': "Linear scan through array checking nums[i] == target.",
        'wasted_work': "Ignoring the sorted order property; fails the mandatory O(log N) interview specification.",
        'optimal_complexity': "O(log N) Time, O(1) Space",
        'optimal_text': "Binary search with sorted-half identification. Halves search interval on every step.",
        'core_invariant': "At least one half `[low..mid]` or `[mid..high]` is monotonically increasing. `low <= high` bound narrows to target in logarithmic steps.",
        'table_rows': [
            {'name': "1. Linear Scan", 'time': "O(N)", 'space': "O(1)", 'note': "Fails explicit O(log N) constraint."},
            {'name': "2. Two-Pass: Find Pivot + Binary Search", 'time': "O(log N)", 'space': "O(1)", 'note': "First binary search finds pivot, second finds target."},
            {'name': "★ 3. One-Pass Modified Binary Search", 'time': "O(log N)", 'space': "O(1)", 'note': "Single clean loop; determines search direction on-the-fly."}
        ]
    },
    'trace_table': {
        'input_desc': 'nums = [4, 5, 6, 7, 0, 1, 2], target = 0',
        'columns': ["Step", "low", "high", "mid", "nums[mid]", "Sorted Half Check", "Decision / Next Bounds"],
        'rows': [
            ["1", "0 (4)", "6 (2)", "3", "7", "nums[0]=4 <= nums[3]=7 &rarr; Left half [4..7] is sorted", "Target 0 not in [4..7] &rarr; Search Right: low = 4"],
            ["2", "4 (0)", "6 (2)", "5", "1", "nums[4]=0 <= nums[5]=1 &rarr; Left half [0..1] is sorted", "Target 0 in [0..1] &rarr; Search Left: high = 4"],
            ["3", "4 (0)", "4 (0)", "4", "0", "nums[4] == target 0! MATCH!", "Return index 4"]
        ],
        'takeaway': "Located target 0 in just 3 iterations of binary search in O(log N) time."
    },
    'stepper': {
        'title': "Rotated Binary Search Sorted-Half Stepper",
        'default_input': 'nums=[4,5,6,7,0,1,2], target=0',
        'presets': [
            {'label': "Target 0 in [4,5,6,7,0,1,2]", 'onclick': 'loadPreset([4,5,6,7,0,1,2], 0)'},
            {'label': "Target 3 Not Found", 'onclick': 'loadPreset([4,5,6,7,0,1,2], 3)'},
            {'label': "Single [1] target=1", 'onclick': 'loadPreset([1], 1)'}
        ],
        'metrics': [
            {'id': "metric-bounds", 'label': "Interval [Low..High]", 'color': "text-amber-400", 'initial': "[0..6]"},
            {'id': "metric-mid", 'label': "Midpoint (Index & Val)", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-half", 'label': "Sorted Half", 'color': "text-purple-400", 'initial': "-"},
            {'id': "metric-found", 'label': "Result Index", 'color': "text-emerald-400", 'initial': "-"}
        ],
        'visual_html': '<div id="p33-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace sorted-half identification.',
        'pseudocode_lines': [
            "var low = 0; var high = nums.size - 1",
            "while (low <= high) {",
            "    val mid = (low + high) / 2",
            "    if (nums[mid] == target) return mid",
            "    if (nums[low] <= nums[mid]) {",
            "        if (target >= nums[low] && target < nums[mid]) high = mid - 1 else low = mid + 1",
            "    } else {",
            "        if (target > nums[mid] && target <= nums[high]) low = mid + 1 else high = mid - 1",
            "    }",
            "}",
            "return -1"
        ],
        'js_code': """
    let rawNums = [4, 5, 6, 7, 0, 1, 2];
    let rawTarget = 0;

    function buildSteps(arr, target) {
      steps = [];
      let low = 0, high = arr.length - 1;
      let foundIdx = -1;

      steps.push({
        bounds: `[${low}..${high}]`, mid: "-", half: "-", found: "-",
        low: low, high: high, midIdx: -1, arr: [...arr], target: target, line: 1,
        narration: `Ready to search target=${target} in rotated array [${arr.join(', ')}].`
      });

      while (low <= high) {
        const mid = Math.floor((low + high) / 2);
        const midVal = arr[mid];

        if (midVal === target) {
          foundIdx = mid;
          steps.push({
            bounds: `[${low}..${high}]`, mid: `idx ${mid} (${midVal})`, half: "MATCH!", found: mid,
            low: low, high: high, midIdx: mid, arr: [...arr], target: target, line: 4,
            narration: `nums[${mid}] == ${target}! Match found at index ${mid}.`
          });
          break;
        }

        const leftSorted = arr[low] <= midVal;
        const sortedSide = leftSorted ? "Left [low..mid]" : "Right [mid..high]";

        if (leftSorted) {
          if (target >= arr[low] && target < midVal) {
            steps.push({
              bounds: `[${low}..${high}]`, mid: `idx ${mid} (${midVal})`, half: `${sortedSide} is sorted`, found: "-",
              low: low, high: high, midIdx: mid, arr: [...arr], target: target, line: 6,
              narration: `Left half [${arr[low]}..${midVal}] is sorted, and target ${target} lies within it. Search LEFT (high = ${mid - 1}).`
            });
            high = mid - 1;
          } else {
            steps.push({
              bounds: `[${low}..${high}]`, mid: `idx ${mid} (${midVal})`, half: `${sortedSide} is sorted`, found: "-",
              low: low, high: high, midIdx: mid, arr: [...arr], target: target, line: 6,
              narration: `Left half is sorted, but target ${target} is NOT in [${arr[low]}..${midVal}]. Search RIGHT (low = ${mid + 1}).`
            });
            low = mid + 1;
          }
        } else {
          if (target > midVal && target <= arr[high]) {
            steps.push({
              bounds: `[${low}..${high}]`, mid: `idx ${mid} (${midVal})`, half: `${sortedSide} is sorted`, found: "-",
              low: low, high: high, midIdx: mid, arr: [...arr], target: target, line: 8,
              narration: `Right half [${midVal}..${arr[high]}] is sorted, and target ${target} lies within it. Search RIGHT (low = ${mid + 1}).`
            });
            low = mid + 1;
          } else {
            steps.push({
              bounds: `[${low}..${high}]`, mid: `idx ${mid} (${midVal})`, half: `${sortedSide} is sorted`, found: "-",
              low: low, high: high, midIdx: mid, arr: [...arr], target: target, line: 8,
              narration: `Right half is sorted, but target ${target} is NOT in [${midVal}..${arr[high]}]. Search LEFT (high = ${mid - 1}).`
            });
            high = mid - 1;
          }
        }
      }

      if (foundIdx === -1) {
        steps.push({
          bounds: "Empty", mid: "-", half: "Complete", found: "-1",
          low: low, high: high, midIdx: -1, arr: [...arr], target: target, line: 11,
          narration: `Search interval exhausted. Target ${target} does not exist in array. Returning -1.`
        });
      }
    }

    function renderVisual(s) {
      const stage = document.getElementById('p33-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-1.5 flex-wrap">';
      s.arr.forEach((v, idx) => {
        const isMid = idx === s.midIdx;
        const inRange = idx >= s.low && idx <= s.high;
        let cls = 'bg-slate-900 border-slate-700 text-slate-500 opacity-40';
        let badge = '';

        if (isMid) {
          cls = 'bg-cyan-500/30 border-cyan-400 text-cyan-200 ring-2 ring-cyan-500 opacity-100';
          badge = '<span class="text-[9px] block text-cyan-300">mid</span>';
        } else if (inRange) {
          cls = 'bg-slate-800 border-slate-600 text-white opacity-100';
          if (idx === s.low && idx === s.high) badge = '<span class="text-[9px] block text-amber-400">L,H</span>';
          else if (idx === s.low) badge = '<span class="text-[9px] block text-amber-400">low</span>';
          else if (idx === s.high) badge = '<span class="text-[9px] block text-purple-400">high</span>';
        }

        html += `<div class="w-12 h-14 rounded-lg border font-mono text-xs font-bold flex flex-col items-center justify-center ${cls}">${v}${badge}</div>`;
      });
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-bounds').textContent = s.bounds;
      document.getElementById('metric-mid').textContent = s.mid;
      document.getElementById('metric-half').textContent = s.half;
      document.getElementById('metric-found').textContent = s.found;
    }

    function loadPreset(arr, target) {
      rawNums = arr; rawTarget = target;
      buildSteps(rawNums, rawTarget);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: One-Pass Modified Binary Search (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(log N) Time, O(1) Space",
            'summary': "Halve the search space in each iteration by determining whether the left half or right half is sorted, then checking if the target lies within the boundaries of the sorted half.",
            'kotlin_code': """class Solution {
    fun search(nums: IntArray, target: Int): Int {
        var low = 0
        var high = nums.size - 1

        while (low <= high) {
            val mid = (low + high) ushr 1
            if (nums[mid] == target) return mid

            // Check if left half is normally sorted
            if (nums[low] <= nums[mid]) {
                if (target >= nums[low] && target < nums[mid]) {
                    high = mid - 1 // Target is in sorted left half
                } else {
                    low = mid + 1  // Target is in right half
                }
            } else {
                // Right half must be normally sorted
                if (target > nums[mid] && target <= nums[high]) {
                    low = mid + 1  // Target is in sorted right half
                } else {
                    high = mid - 1 // Target is in left half
                }
            }
        }
        return -1
    }
}""",
            'python_code': """class Solution:
    def search(self, nums: list[int], target: int) -> int:
        low, high = 0, len(nums) - 1

        while low <= high:
            mid = (low + high) // 2
            if nums[mid] == target:
                return mid

            # Check if left half is normally sorted
            if nums[low] <= nums[mid]:
                if nums[low] <= target < nums[mid]:
                    high = mid - 1
                else:
                    low = mid + 1
            else:
                # Right half is normally sorted
                if nums[mid] < target <= nums[high]:
                    low = mid + 1
                else:
                    high = mid - 1

        return -1""",
            'java_code': """class Solution {
    public int search(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (nums[mid] == target) return mid;

            // Check if left half is sorted
            if (nums[low] <= nums[mid]) {
                if (target >= nums[low] && target < nums[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                // Right half is sorted
                if (target > nums[mid] && target <= nums[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }
}""",
            'memory_breakdown': "Strictly O(1) auxiliary space (low, high, mid pointers). Strictly O(log N) runtime since search space is halved on every comparison."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun search(nums: IntArray, target: Int): Int {
        // Write your optimal O(log N) binary search here
        var low = 0; var high = nums.size - 1
        while (low <= high) {
            val mid = (low + high) ushr 1
            if (nums[mid] == target) return mid
            if (nums[low] <= nums[mid]) {
                if (target >= nums[low] && target < nums[mid]) high = mid - 1
                else low = mid + 1
            } else {
                if (target > nums[mid] && target <= nums[high]) low = mid + 1
                else high = mid - 1
            }
        }
        return -1
    }
}""",
        'python_boilerplate': """class Solution:
    def search(self, nums: list[int], target: int) -> int:
        # Write your optimal O(log N) binary search here
        low, high = 0, len(nums) - 1
        while low <= high:
            mid = (low + high) // 2
            if nums[mid] == target: return mid
            if nums[low] <= nums[mid]:
                if nums[low] <= target < nums[mid]: high = mid - 1
                else: low = mid + 1
            else:
                if nums[mid] < target <= nums[high]: low = mid + 1
                else: high = mid - 1
        return -1""",
        'java_boilerplate': """class Solution {
    public int search(int[] nums, int target) {
        // Write your optimal O(log N) binary search here
        int low = 0, high = nums.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (nums[mid] == target) return mid;
            if (nums[low] <= nums[mid]) {
                if (target >= nums[low] && target < nums[mid]) high = mid - 1;
                else low = mid + 1;
            } else {
                if (target > nums[mid] && target <= nums[high]) low = mid + 1;
                else high = mid - 1;
            }
        }
        return -1;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(nums, target) {
        let low = 0, high = nums.length - 1;
        while (low <= high) {
          const mid = Math.floor((low + high) / 2);
          if (nums[mid] === target) return mid;
          if (nums[low] <= nums[mid]) {
            if (target >= nums[low] && target < nums[mid]) high = mid - 1;
            else low = mid + 1;
          } else {
            if (target > nums[mid] && target <= nums[high]) low = mid + 1;
            else high = mid - 1;
          }
        }
        return -1;
      }

      const tests = [
        { nums: [4,5,6,7,0,1,2], target: 0, expected: 4 },
        { nums: [4,5,6,7,0,1,2], target: 3, expected: -1 },
        { nums: [1], target: 0, expected: -1 },
        { nums: [1], target: 1, expected: 0 },
        { nums: [3,1], target: 1, expected: 1 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.nums, t.target);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: nums=[${t.nums}], target=${t.target} &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: all values in nums are unique. The array was sorted and possibly rotated. We must achieve O(log N) time, which requires binary search.\"",
        'phase2': "\"Normally, binary search requires the entire array to be sorted. In a rotated sorted array, splitting at midpoint `mid` creates two halves, and at least one of those halves is guaranteed to be completely sorted without the rotation pivot! We can test which half is sorted in O(1): if `nums[low] <= nums[mid]`, the left half is sorted. If target lies in `[nums[low]..nums[mid])`, we narrow search to the left by setting `high = mid - 1`; otherwise, target must be on the right, so `low = mid + 1`. If the left half is not sorted, the right half MUST be sorted, so we apply the symmetric logic. On every step, we discard half the array, preserving O(log N) runtime.\"",
        'phase3': "\"Let me write the code. Initialize `low = 0` and `high = nums.size - 1`. In a `while (low <= high)` loop: compute `mid = (low + high) / 2`. If `nums[mid] == target`, return `mid`. Check `if (nums[low] <= nums[mid])`: if `target >= nums[low] && target < nums[mid]`, set `high = mid - 1`; else `low = mid + 1`. In the else branch: if `target > nums[mid] && target <= nums[high]`, set `low = mid + 1`; else `high = mid - 1`. If loop finishes without returning, return -1.\"",
        'phase4': "\"Tracing with `nums = [4, 5, 6, 7, 0, 1, 2]` and `target = 0`. low=0 (4), high=6 (2), mid=3 (7). Left half [4..7] is sorted. 0 is not in [4..7], so low becomes 4. Next: low=4 (0), high=6 (2), mid=5 (1). Left half [0..1] is sorted. Target 0 is in [0..1], so high becomes 4. Next: low=4, high=4, mid=4 (0). nums[4] == 0 &rarr; returns index 4. For single-element arrays like `[1]` with target 0, loop terminates and returns -1. Strict O(log N) time and O(1) space.\"",
        'senior_followup': "\"If duplicates are allowed (LC #81), `nums[low] == nums[mid] == nums[high]` can prevent us from knowing which half is sorted. In that worst-case, we must increment `low++` and decrement `high--`, degrading runtime to O(N).\""
    },
    'quiz': [
        {
            'question': "What fundamental property of rotated sorted arrays makes binary search possible?",
            'options': [
                "Splitting at midpoint guarantees that at least one of the two halves is normally sorted",
                "The minimum element is always positioned exactly at index nums.length / 2",
                "The rotation pivot automatically swaps endianness of 32-bit integer values",
                "All rotated arrays can be unrotated in O(1) time using bitwise shift operations"
            ],
            'correct_index': 0,
            'explanation': "A single rotation pivot can only be on one side of the midpoint. Therefore, either `[low..mid]` or `[mid..high]` is monotonically sorted, which allows bounded membership checks."
        },
        {
            'question': "If `nums[low] <= nums[mid]`, under what condition should we search the LEFT half?",
            'options': [
                "target >= nums[low] && target < nums[mid]",
                "target > nums[mid] && target <= nums[high]",
                "target == nums[low] + nums[high]",
                "target < nums[low] || target > nums[mid]"
            ],
            'correct_index': 0,
            'explanation': "Since the left half is sorted, the values strictly increase from `nums[low]` to `nums[mid]`. If the target falls in this range, it must be in the left half (`high = mid - 1`)."
        }
    ]
}

p71 = {
    'num': 71,
    'lc': 875,
    'title': "Koko Eating Bananas",
    'category': "Binary Search",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "koko-eating-bananas",
    'verbatim_statement': """<p>Koko loves to eat bananas. There are <code>n</code> piles of bananas, the <code>i<sup>th</sup></code> pile has <code>piles[i]</code> bananas. The guards have gone and will come back in <code>h</code> hours.</p>
<p>Koko can decide her bananas-per-hour eating speed of <code>k</code>. Each hour, she chooses some pile of bananas and eats <code>k</code> bananas from that pile. If the pile has less than <code>k</code> bananas, she eats all of them instead and will not eat any more bananas during this hour.</p>
<p>Koko likes to eat slowly but still wants to finish eating all the bananas before the guards return.</p>
<p>Return <em>the minimum integer</em> <code>k</code> <em>such that she can eat all the bananas within</em> <code>h</code> <em>hours</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'piles = [3,6,7,11], h = 8', 'output': '4'},
        {'title': "Example 2", 'input': 'piles = [30,11,23,4,20], h = 5', 'output': '30'},
        {'title': "Example 3", 'input': 'piles = [30,11,23,4,20], h = 6', 'output': '23'}
    ],
    'constraints': [
        "1 <= piles.length <= 10^4",
        "piles.length <= h <= 10^9",
        "1 <= piles[i] <= 10^9"
    ],
    'plain_english': "We need to find the minimum eating speed k (bananas/hour). The possible speed k ranges from 1 to `max(piles)`. For any chosen speed k, the hours needed to eat pile p is `ceil(p / k) = (p + k - 1) / k`. If Koko can finish within h hours, this speed is feasible, so we try a slower speed (`high = mid`). If she cannot finish, k is too slow, so we must increase the speed (`low = mid + 1`). This is Binary Search on Answer running in O(N log(maxPile)).",
    'boundary_checks': {
        'min_base': "h == piles.length &rarr; Koko must eat at speed equal to max(piles) since she can only eat from 1 pile per hour.",
        'best_exit': "Huge h <code>h >= sum(piles)</code> &rarr; Returns minimum possible speed k = 1.",
        'worst_scan': "Large pile sizes up to 10^9 &rarr; 64-bit integer arithmetic prevents 32-bit overflow when summing total hours."
    },
    'mental_model': {
        'analogy_title': "The Treadmill Speed Dial",
        'analogy_text': "Imagine a treadmill dial with speeds from 1 km/h to 100 km/h. At speed 1, you will take too long and run out of time (guards return). At speed 100, you finish way ahead of schedule, but you are exhausted. You want the slowest possible dial setting that still finishes before the buzzer. You binary search the dial: test the middle speed; if you beat the buzzer, dial it down. If you fail, dial it up.",
        'aha_moment': "The problem doesn't give us a sorted array, but the ANSWER space [1..maxPile] is strictly sorted! Furthermore, the feasibility function `canFinish(k)` is monotonic: if speed k works, every speed > k also works. This enables Binary Search on Answer.",
        'trigger': "Minimize the maximum rate / speed to finish tasks within time limit H &rarr; Binary Search on Answer over range [1..maxVal].",
        'sisters': [
            {'name': "Capacity To Ship Packages Within D Days", 'lc': "LC #1011"},
            {'name': "Split Array Largest Sum", 'lc': "LC #410"},
            {'name': "Find the Smallest Divisor Given a Threshold", 'lc': "LC #1283"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(maxPile * N) Time, O(1) Space",
        'naive_text': "Linear search starting from k = 1, incrementing k by 1 until canFinish(k) returns true.",
        'wasted_work': "Testing speeds 1, 2, 3... sequentially. When maxPile = 10^9, 10^13 operations causes severe TLE.",
        'optimal_complexity': "O(N log(maxPile)) Time, O(1) Space",
        'optimal_text': "Binary Search over range [1..maxPile]. At most 30 feasibility checks, each taking O(N) time.",
        'core_invariant': "The optimal minimum speed k* lies in `[low..high]`. If `hours(mid) <= h`, `high = mid`; else `low = mid + 1`.",
        'table_rows': [
            {'name': "1. Linear Search", 'time': "O(M * N)", 'space': "O(1)", 'note': "M = 10^9 &rarr; catastrophic timeout."},
            {'name': "★ 2. Binary Search on Answer", 'time': "O(N log M)", 'space': "O(1)", 'note': "30 iterations of N ops &rarr; under 20ms execution."}
        ]
    },
    'trace_table': {
        'input_desc': 'piles = [3, 6, 7, 11], h = 8. Range: [1..11]',
        'columns': ["Iteration", "low", "high", "mid (Speed k)", "Hours Needed per Pile", "Total Hours", "Decision"],
        'rows': [
            ["1", "1", "11", "6", "ceil(3/6)=1, ceil(6/6)=1, ceil(7/6)=2, ceil(11/6)=2", "1+1+2+2 = 6 <= 8", "Feasible! Try slower: high = 6"],
            ["2", "1", "6", "3", "ceil(3/3)=1, ceil(6/3)=2, ceil(7/3)=3, ceil(11/3)=4", "1+2+3+4 = 10 > 8", "Too slow! Try faster: low = 4"],
            ["3", "4", "6", "5", "ceil(3/5)=1, ceil(6/5)=2, ceil(7/5)=2, ceil(11/5)=3", "1+2+2+3 = 8 <= 8", "Feasible! Try slower: high = 5"],
            ["4", "4", "5", "4", "ceil(3/4)=1, ceil(6/4)=2, ceil(7/4)=2, ceil(11/4)=3", "1+2+2+3 = 8 <= 8", "Feasible! Try slower: high = 4"]
        ],
        'takeaway': "Loop terminates at low = high = 4. Minimum speed k = 4 allows finishing in exactly 8 hours."
    },
    'stepper': {
        'title': "Binary Search on Answer Speed Stepper",
        'default_input': 'piles=[3,6,7,11], h=8',
        'presets': [
            {'label': "Canonical [3,6,7,11], h=8", 'onclick': 'loadPreset([3,6,7,11], 8)'},
            {'label': "Exact Match [30,11,23,4,20], h=5", 'onclick': 'loadPreset([30,11,23,4,20], 5)'},
            {'label': "Loose Hours [30,11,23,4,20], h=6", 'onclick': 'loadPreset([30,11,23,4,20], 6)'}
        ],
        'metrics': [
            {'id': "metric-speed-range", 'label': "Speed Range [L..H]", 'color': "text-amber-400", 'initial': "[1..11]"},
            {'id': "metric-mid-k", 'label': "Testing Speed k", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-hours-used", 'label': "Hours Needed", 'color': "text-purple-400", 'initial': "-"},
            {'id': "metric-best-k", 'label': "Optimal k Found", 'color': "text-emerald-400", 'initial': "-"}
        ],
        'visual_html': '<div id="p875-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace binary search over speed range.',
        'pseudocode_lines': [
            "var low = 1; var high = piles.maxOrNull()!!",
            "while (low < high) {",
            "    val mid = (low + high) / 2",
            "    var hours = 0L",
            "    for (p in piles) hours += (p + mid - 1) / mid",
            "    if (hours <= h) high = mid else low = mid + 1",
            "}",
            "return low"
        ],
        'js_code': """
    let rawPiles = [3, 6, 7, 11];
    let rawH = 8;

    function buildSteps(piles, h) {
      steps = [];
      let low = 1;
      let high = Math.max(...piles);

      steps.push({
        range: `[${low}..${high}]`, k: "-", hours: "-", best: "-",
        low: low, high: high, mid: -1, piles: [...piles], h: h, line: 1,
        narration: `Ready to search speed k in range [1..${high}] for h = ${h} hours.`
      });

      while (low < high) {
        const mid = Math.floor((low + high) / 2);
        let hours = 0;
        piles.forEach(p => { hours += Math.ceil(p / mid); });

        const feasible = hours <= h;
        steps.push({
          range: `[${low}..${high}]`, k: mid, hours: `${hours}h (limit: ${h}h)`, best: feasible ? mid : "too slow",
          low: low, high: high, mid: mid, piles: [...piles], h: h, line: 5,
          narration: `Testing speed k = ${mid} bananas/hr. Total hours needed = ${hours}h vs limit ${h}h. ${feasible ? 'FEASIBLE! Searching lower speeds (high = ' + mid + ').' : 'TOO SLOW! Searching higher speeds (low = ' + (mid + 1) + ').'}`
        });

        if (feasible) high = mid;
        else low = mid + 1;
      }

      steps.push({
        range: `[${low}..${high}]`, k: low, hours: "-", best: low,
        low: low, high: high, mid: low, piles: [...piles], h: h, line: 7,
        narration: `Binary search converged at speed k = ${low}. Minimum optimal speed is ${low}.`
      });
    }

    function renderVisual(s) {
      const stage = document.getElementById('p875-stage');
      if (!stage) return;
      let html = '<div class="flex items-center justify-center gap-3 flex-wrap">';
      s.piles.forEach(p => {
        const hrs = s.mid > 0 ? Math.ceil(p / s.mid) : "-";
        html += `<div class="p-3 rounded-lg border font-mono text-xs flex flex-col items-center bg-slate-900 border-slate-700 text-white"><span class="font-bold text-amber-300">${p} bananas</span><span class="text-[10px] text-cyan-400">${hrs} hr(s) at k=${s.k}</span></div>`;
      });
      html += '</div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-speed-range').textContent = s.range;
      document.getElementById('metric-mid-k').textContent = s.k;
      document.getElementById('metric-hours-used').textContent = s.hours;
      document.getElementById('metric-best-k').textContent = s.best;
    }

    function loadPreset(piles, h) {
      rawPiles = piles; rawH = h;
      buildSteps(rawPiles, rawH);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: Binary Search on Answer (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N log(maxPile)) Time, O(1) Space",
            'summary': "Search the monotonic answer range [1..maxPile]. At each midpoint speed, calculate total hours using ceiling division `(p + mid - 1) / mid`. Adjust bounds accordingly.",
            'kotlin_code': """class Solution {
    fun minEatingSpeed(piles: IntArray, h: Int): Int {
        var low = 1
        var high = piles.maxOrNull() ?: 1

        while (low < high) {
            val mid = (low + high) ushr 1
            if (canFinish(piles, h, mid)) {
                high = mid // Feasible; try to find a smaller speed
            } else {
                low = mid + 1 // Too slow; must eat faster
            }
        }
        return low
    }

    private fun canFinish(piles: IntArray, h: Int, k: Int): Boolean {
        var hours = 0L
        for (p in piles) {
            hours += (p + k - 1L) / k // Ceiling division without float conversion
            if (hours > h) return false
        }
        return true
    }
}""",
            'python_code': """class Solution:
    def minEatingSpeed(self, piles: list[int], h: int) -> int:
        low, high = 1, max(piles)

        while low < high:
            mid = (low + high) // 2
            # Sum hours needed for speed mid
            hours = sum((p + mid - 1) // mid for p in piles)
            if hours <= h:
                high = mid  # Can finish, try smaller speed
            else:
                low = mid + 1  # Cannot finish, must speed up

        return low""",
            'java_code': """class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        int low = 1;
        int high = 1;
        for (int p : piles) {
            if (p > high) high = p;
        }

        while (low < high) {
            int mid = (low + high) >>> 1;
            if (canFinish(piles, h, mid)) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    private boolean canFinish(int[] piles, int h, int k) {
        long hours = 0;
        for (int p : piles) {
            hours += (p + k - 1L) / k; // Ceiling division
            if (hours > h) return false;
        }
        return true;
    }
}""",
            'memory_breakdown': "Strictly O(1) auxiliary space. Binary search takes log2(10^9) = 30 iterations. Each iteration takes O(N) linear time to compute hours. 30 * 10,000 = 300,000 operations, executing in under 15ms."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun minEatingSpeed(piles: IntArray, h: Int): Int {
        // Write your optimal Binary Search on Answer here
        var low = 1
        var high = piles.maxOrNull() ?: 1
        while (low < high) {
            val mid = (low + high) ushr 1
            var hours = 0L
            for (p in piles) hours += (p + mid - 1L) / mid
            if (hours <= h) high = mid else low = mid + 1
        }
        return low
    }
}""",
        'python_boilerplate': """class Solution:
    def minEatingSpeed(self, piles: list[int], h: int) -> int:
        # Write your optimal Binary Search on Answer here
        low, high = 1, max(piles)
        while low < high:
            mid = (low + high) // 2
            hours = sum((p + mid - 1) // mid for p in piles)
            if hours <= h: high = mid
            else: low = mid + 1
        return low""",
        'java_boilerplate': """class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        // Write your optimal Binary Search on Answer here
        int low = 1, high = 1;
        for (int p : piles) if (p > high) high = p;
        while (low < high) {
            int mid = (low + high) >>> 1;
            long hours = 0;
            for (int p : piles) hours += (p + mid - 1L) / mid;
            if (hours <= h) high = mid;
            else low = mid + 1;
        }
        return low;
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function solve(piles, h) {
        let low = 1, high = Math.max(...piles);
        while (low < high) {
          const mid = Math.floor((low + high) / 2);
          let hours = 0;
          for (let i = 0; i < piles.length; i++) {
            hours += Math.ceil(piles[i] / mid);
          }
          if (hours <= h) high = mid;
          else low = mid + 1;
        }
        return low;
      }

      const tests = [
        { piles: [3,6,7,11], h: 8, expected: 4 },
        { piles: [30,11,23,4,20], h: 5, expected: 30 },
        { piles: [30,11,23,4,20], h: 6, expected: 23 }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = solve(t.piles, t.h);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: piles=[${t.piles}], h=${t.h} &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me confirm the constraints: `piles[i]` up to 10^9, and `h` is between `piles.length` and 10^9. Because Koko can only eat from at most one pile per hour, if a pile has fewer than k bananas, she finishes the pile and waits for the next hour. The goal is to find the minimum integer speed k that allows her to finish within h hours.\"",
        'phase2': "\"The key insight is that the search space is monotonic: if a speed k works, any speed greater than k also works. If speed k does not work, no speed less than k can work. Therefore, we can binary search over the possible speeds. What is the lower bound? 1 banana per hour. What is the upper bound? The maximum pile size `max(piles)` (because at that speed, she finishes each pile in exactly 1 hour, taking `piles.length <= h` hours). For a candidate speed `mid`, we compute hours needed as `ceil(p / mid)` or in integer arithmetic `(p + mid - 1) / mid`. If total hours <= h, `mid` is feasible, so we search the lower half `high = mid`. Otherwise, `low = mid + 1`.\"",
        'phase3': "\"Let me write the code. Find `max(piles)` for `high`, and `low = 1`. In a `while (low < high)` loop: `mid = (low + high) / 2`. Calculate `hours`: use 64-bit Long to avoid overflow! If `hours <= h`, `high = mid`; else `low = mid + 1`. At termination when `low == high`, return `low`.\"",
        'phase4': "\"Tracing with `piles = [3, 6, 7, 11]` and `h = 8`. Initial range is [1..11]. Mid is 6: hours are 1+1+2+2 = 6 <= 8 &rarr; high=6. Mid is 3: hours are 1+2+3+4 = 10 > 8 &rarr; low=4. Mid is 5: hours are 1+2+2+3 = 8 <= 8 &rarr; high=5. Mid is 4: hours are 1+2+2+3 = 8 <= 8 &rarr; high=4. Loop ends, returns 4. Matches Example 1.\"",
        'senior_followup': "\"This 'Binary Search on Answer' pattern is identical to capacity planning algorithms (e.g. finding minimum thread pool size or batch size to maintain an SLA under variable latency distributions).\""
    },
    'quiz': [
        {
            'question': "Why is 64-bit Long arithmetic used when summing the required hours?",
            'options': [
                "Summing N piles of size 10^9 with speed k=1 can exceed 32-bit Integer.MAX_VALUE",
                "Binary search requires double-precision floating point for division calculations",
                "JVM bytecode forbids modulo operations on 32-bit integer primitives",
                "Operating system clock registers only return 64-bit epoch nanoseconds"
            ],
            'correct_index': 0,
            'explanation': "If N = 10^4 and each pile has 10^9 bananas, at speed k = 1 the total hours is 10^13, which easily overflows a 32-bit signed integer (max ~2 * 10^9), causing a negative sum without 64-bit Long."
        },
        {
            'question': "How is ceiling division `ceil(p / k)` written using pure integer arithmetic?",
            'options': [
                "(p + k - 1) / k",
                "(p - k + 1) / k",
                "(p * k + 1) / k",
                "(p + 1) / k"
            ],
            'correct_index': 0,
            'explanation': "Adding `k - 1` before integer division ensures that any non-zero remainder promotes the quotient to the next whole integer without floating point conversion."
        }
    ]
}

p72 = {
    'num': 72,
    'lc': 98,
    'title': "Validate Binary Search Tree",
    'category': "Binary Trees & BST",
    'difficulty': "Medium",
    'pass_label': "Pass 2B",
    'lc_slug': "validate-binary-search-tree",
    'verbatim_statement': """<p>Given the <code>root</code> of a binary tree, <em>determine if it is a valid binary search tree (BST)</em>.</p>
<p>A <strong>valid BST</strong> is defined as follows:</p>
<ul>
	<li>The left <span data-keyword="subtree">subtree</span> of a node contains only nodes with keys <strong>less than</strong> the node's key.</li>
	<li>The right subtree of a node contains only nodes with keys <strong>greater than</strong> the node's key.</li>
	<li>Both the left and right subtrees must also be binary search trees.</li>
</ul>""",
    'examples': [
        {'title': "Example 1", 'input': 'root = [2,1,3]', 'output': 'true'},
        {'title': "Example 2", 'input': 'root = [5,1,4,null,null,3,6]', 'output': 'false\nExplanation: The root node\'s value is 5 but its right child\'s value is 4.'}
    ],
    'constraints': [
        "The number of nodes in the tree is in the range [1, 10^4].",
        "-2^31 <= Node.val <= 2^31 - 1"
    ],
    'plain_english': "A common trap is only checking if `left.val < node.val < right.val` for immediate children. A BST requires that ALL nodes in the left subtree are strictly less than the ancestor, and ALL nodes in the right subtree are strictly greater! We validate this using DFS by passing down valid range intervals `(low, high)`. For the left child, the range becomes `(low, node.val)`. For the right child, the range becomes `(node.val, high)`. If any node violates `low < node.val < high`, return false immediately.",
    'boundary_checks': {
        'min_base': "Single node <code>root = [1]</code> &rarr; Returns true.",
        'best_exit': "Immediate root constraint violation &rarr; Returns false on step 1.",
        'worst_scan': "Integer.MAX_VALUE and MIN_VALUE <code>root = [-2147483648]</code> &rarr; Handled cleanly by using 64-bit Long bounds."
    },
    'mental_model': {
        'analogy_title': "The Restrictive Speed Limits Tunnel",
        'analogy_text': "Imagine driving down a branching highway tunnel system. At the start, the speed limit range is unbounded `(-infinity, +infinity)`. When you take a left exit at checkpoint 50, the maximum allowable speed drops to 50 for the entire rest of that network `(-infinity, 50)`. When you subsequently take a right exit at checkpoint 30, the minimum allowable speed becomes 30, narrowing the window to `(30, 50)`. Every checkpoint you pass tightens one of the two boundaries.",
        'aha_moment': "Comparing a node only to its immediate parent is insufficient (e.g. `[5, 1, 6, null, null, 4, 7]` where 4 is less than 5 but in the right subtree). By propagating inherited `low` and `high` bounds down the recursion, every node validates against all its ancestors in O(1) time.",
        'trigger': "Validate BST property across entire subtrees &rarr; DFS with interval bounds `(low, high)` or In-Order traversal strictly increasing check.",
        'sisters': [
            {'name': "Lowest Common Ancestor of a BST", 'lc': "LC #235"},
            {'name': "Kth Smallest Element in a BST", 'lc': "LC #230"},
            {'name': "Recover Binary Search Tree", 'lc': "LC #99"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2) Time, O(N) Space",
        'naive_text': "For every node, traverse its entire left subtree to find maxVal, and right subtree to find minVal, then check maxVal < node.val < minVal.",
        'wasted_work': "Re-traversing subtrees repeatedly from every ancestor node.",
        'optimal_complexity': "O(N) Time, O(Height) Auxiliary Space",
        'optimal_text': "Single DFS pass passing down `(low, high)` bounds. Each node visited exactly once in O(1) time.",
        'core_invariant': "At any node, `low < node.val < high`. For left child: `high' = node.val`; for right child: `low' = node.val`.",
        'table_rows': [
            {'name': "1. Subtree Min/Max Re-scan", 'time': "O(N^2)", 'space': "O(H)", 'note': "Scans descendants repeatedly from every level."},
            {'name': "2. In-Order Traversal + Array Check", 'time': "O(N)", 'space': "O(N)", 'note': "Stores in-order list and checks if strictly sorted."},
            {'name': "★ 3. DFS Bounded Interval Recursion", 'time': "O(N)", 'space': "O(H)", 'note': "Validates in-flight without array allocation; early exits on first violation."}
        ]
    },
    'trace_table': {
        'input_desc': 'root = [5, 1, 4, null, null, 3, 6]',
        'columns': ["Node Visited", "Node Value", "Allowed Range (low, high)", "Condition Check (low < val < high)", "Decision"],
        'rows': [
            ["Root", "5", "(-inf, +inf)", "-inf < 5 < +inf", "Valid. Recurse left with (-inf, 5), right with (5, +inf)"],
            ["Left child", "1", "(-inf, 5)", "-inf < 1 < 5", "Valid. Leaf node."],
            ["Right child", "4", "(5, +inf)", "5 < 4 < +inf &rarr; FAILS!", "VIOLATION! 4 is not > 5. Return false immediately!"]
        ],
        'takeaway': "Even though 4 has children 3 and 6, node 4 violates its ancestor bound (must be > 5). Early exits immediately."
    },
    'stepper': {
        'title': "BST Bounded Interval DFS Stepper",
        'default_input': 'root = [5, 1, 4, null, null, 3, 6]',
        'presets': [
            {'label': "Invalid [5, 1, 4, null, null, 3, 6]", 'onclick': 'loadPreset("invalid")'},
            {'label': "Valid [2, 1, 3]", 'onclick': 'loadPreset("valid")'}
        ],
        'metrics': [
            {'id': "metric-node", 'label': "Inspecting Node", 'color': "text-amber-400", 'initial': "5"},
            {'id': "metric-range", 'label': "Allowed Range (Low, High)", 'color': "text-cyan-400", 'initial': "(-inf, +inf)"},
            {'id': "metric-status", 'label': "Validation Result", 'color': "text-emerald-400", 'initial': "Valid"}
        ],
        'visual_html': '<div id="p98-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        'initial_narration': 'Click "Next Step" to trace DFS range validation.',
        'pseudocode_lines': [
            "fun isValidBST(root: TreeNode?): Boolean = validate(root, Long.MIN_VALUE, Long.MAX_VALUE)",
            "fun validate(node: TreeNode?, low: Long, high: Long): Boolean {",
            "    if (node == null) return true",
            "    if (node.val <= low || node.val >= high) return false",
            "    return validate(node.left, low, node.val.toLong()) &&",
            "           validate(node.right, node.val.toLong(), high)",
            "}"
        ],
        'js_code': """
    let currentTree = "invalid";

    function buildSteps(treeType) {
      steps = [];
      if (treeType === "invalid") {
        steps.push({ node: "5 (Root)", range: "(-&infin;, +&infin;)", status: "Valid", activeId: 5, valid: true, line: 1, narration: "Testing root 5 with range (-&infin;, +&infin;). Valid." });
        steps.push({ node: "1 (Left)", range: "(-&infin;, 5)", status: "Valid", activeId: 1, valid: true, line: 4, narration: "Left child 1: range is (-&infin;, 5). 1 < 5 is valid." });
        steps.push({ node: "4 (Right)", range: "(5, +&infin;)", status: "INVALID (4 <= 5)", activeId: 4, valid: false, line: 4, narration: "Right child 4: range is (5, +&infin;). 4 is NOT > 5! Invalid BST. Returning false." });
      } else {
        steps.push({ node: "2 (Root)", range: "(-&infin;, +&infin;)", status: "Valid", activeId: 2, valid: true, line: 1, narration: "Testing root 2 with range (-&infin;, +&infin;). Valid." });
        steps.push({ node: "1 (Left)", range: "(-&infin;, 2)", status: "Valid", activeId: 1, valid: true, line: 4, narration: "Left child 1: range (-&infin;, 2). Valid." });
        steps.push({ node: "3 (Right)", range: "(2, +&infin;)", status: "Valid", activeId: 3, valid: true, line: 5, narration: "Right child 3: range (2, +&infin;). Valid." });
        steps.push({ node: "All", range: "Done", status: "VALID BST", activeId: -1, valid: true, line: 1, narration: "All nodes passed strict ancestor interval checks. Tree is a valid BST." });
      }
    }

    function renderVisual(s) {
      const stage = document.getElementById('p98-stage');
      if (!stage) return;
      let html = '<div class="flex flex-col items-center gap-2">';
      html += `<div class="p-3 rounded-lg border font-mono text-xs flex flex-col items-center ${s.valid ? 'bg-slate-900 border-slate-700 text-white' : 'bg-rose-950/60 border-rose-500 text-rose-200 ring-2 ring-rose-500'}">`;
      html += `<span class="font-bold text-sm">Active Node: ${s.node}</span>`;
      html += `<span class="text-cyan-400">Allowed Range: ${s.range}</span>`;
      html += `<span class="font-bold ${s.valid ? 'text-emerald-400' : 'text-rose-400'}">${s.status}</span>`;
      html += '</div></div>';
      stage.innerHTML = html;
    }

    function updateMetrics(s) {
      document.getElementById('metric-node').textContent = s.node;
      document.getElementById('metric-range').textContent = s.range;
      document.getElementById('metric-status').textContent = s.status;
    }

    function loadPreset(t) {
      currentTree = t;
      buildSteps(currentTree);
      currentStep = 0;
      updateUI();
    }
""",
        'js_update_metrics': "updateMetrics(steps[currentStep]);",
        'js_render_visual': "renderVisual(steps[currentStep]);"
    },
    'approaches': [
        {
            'name': "★ Approach: DFS Bounded Interval Recursion (Production Optimal)",
            'badge': "Optimal",
            'color': "emerald",
            'complexity': "O(N) Time, O(Height) Space",
            'summary': "Pass down strict low and high bounds (using Long to avoid integer overflow). Left child receives (low, node.val); right child receives (node.val, high).",
            'kotlin_code': """class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

class Solution {
    fun isValidBST(root: TreeNode?): Boolean {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE)
    }

    private fun validate(node: TreeNode?, low: Long, high: Long): Boolean {
        if (node == null) return true
        val v = node.`val`.toLong()
        if (v <= low || v >= high) return false

        return validate(node.left, low, v) && validate(node.right, v, high)
    }
}""",
            'python_code': """class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

class Solution:
    def isValidBST(self, root: TreeNode | None) -> bool:
        def validate(node: TreeNode | None, low: float, high: float) -> bool:
            if not node:
                return True
            if not (low < node.val < high):
                return False
            return validate(node.left, low, node.val) and validate(node.right, node.val, high)

        return validate(root, float('-inf'), float('inf'))""",
            'java_code': """class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}

class Solution {
    public boolean isValidBST(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean validate(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;

        return validate(node.left, low, node.val) && validate(node.right, node.val, high);
    }
}""",
            'memory_breakdown': "O(H) recursion stack space where H is tree height (O(log N) for balanced trees, O(N) worst-case skewed). Visits each node at most once in O(1) time."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class TreeNode(var `val`: Int) {
    var left: TreeNode? = null; var right: TreeNode? = null
}

class Solution {
    fun isValidBST(root: TreeNode?): Boolean {
        // Write your optimal DFS Interval solution here
        fun validate(node: TreeNode?, low: Long, high: Long): Boolean {
            if (node == null) return true
            val v = node.`val`.toLong()
            if (v <= low || v >= high) return false
            return validate(node.left, low, v) && validate(node.right, v, high)
        }
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE)
    }
}""",
        'python_boilerplate': """class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val; self.left = left; self.right = right

class Solution:
    def isValidBST(self, root: TreeNode | None) -> bool:
        # Write your optimal DFS Interval solution here
        def validate(node, low, high):
            if not node: return True
            if not (low < node.val < high): return False
            return validate(node.left, low, node.val) and validate(node.right, node.val, high)
        return validate(root, float('-inf'), float('inf'))""",
        'java_boilerplate': """class TreeNode {
    int val; TreeNode left, right; TreeNode(int val) { this.val = val; }
}

class Solution {
    public boolean isValidBST(TreeNode root) {
        // Write your optimal DFS Interval solution here
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    private boolean validate(TreeNode n, long low, long high) {
        if (n == null) return true;
        if (n.val <= low || n.val >= high) return false;
        return validate(n.left, low, n.val) && validate(n.right, n.val, high);
    }
}""",
        'js_test_runner': """
      const statusSpan = document.getElementById('scratchpad-status');
      const resultsDiv = document.getElementById('scratchpad-results');
      resultsDiv.innerHTML = '';

      function isValidBST(arr) {
        if (!arr || arr.length === 0) return true;
        function validate(idx, low, high) {
          if (idx >= arr.length || arr[idx] === null || arr[idx] === undefined) return true;
          const val = arr[idx];
          if (val <= low || val >= high) return false;
          return validate(2 * idx + 1, low, val) && validate(2 * idx + 2, val, high);
        }
        return validate(0, -Infinity, Infinity);
      }

      const tests = [
        { arr: [2, 1, 3], expected: true },
        { arr: [5, 1, 4, null, null, 3, 6], expected: false },
        { arr: [1], expected: true }
      ];

      let allPassed = true;
      tests.forEach((t, i) => {
        const actual = isValidBST(t.arr);
        const pass = actual === t.expected;
        if (!pass) allPassed = false;
        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono border ${pass ? 'bg-emerald-950/40 border-emerald-800 text-emerald-300' : 'bg-rose-950/40 border-rose-800 text-rose-300'}`;
        row.textContent = `Test ${i+1}: tree=[${t.arr}] &rarr; Got ${actual}, Expected ${t.expected} (${pass ? 'PASSED' : 'FAILED'})`;
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
        'phase1': "\"First, let me clarify the constraints: node values can range from Integer.MIN_VALUE to Integer.MAX_VALUE (-2^31 to 2^31 - 1). A valid BST requires strict inequality: left < node < right. A common pitfall is only comparing a node to its direct parent. Every node must be valid relative to ALL of its ancestors.\"",
        'phase2': "\"The optimal approach uses DFS to pass down an allowed interval `(low, high)` for each node. The root starts with interval `(Long.MIN_VALUE, Long.MAX_VALUE)` using 64-bit Long to avoid 32-bit integer overflow edge cases. When moving to the left child, its value must be less than the current node, so the new range is `(low, node.val)`. When moving to the right child, its value must be greater than current node, so the new range is `(node.val, high)`. If any node violates `low < node.val < high`, we immediately return false.\"",
        'phase3': "\"Let me write the code. Helper method `validate(node, low: Long, high: Long): Boolean`. Base case: if node is null, return true. Check: if `node.val <= low || node.val >= high`, return false. Recursively return `validate(node.left, low, node.val) && validate(node.right, node.val, high)`. The main method simply calls `validate(root, Long.MIN_VALUE, Long.MAX_VALUE)`.\"",
        'phase4': "\"Tracing with `[5, 1, 4, null, null, 3, 6]`. Root 5 has (-inf, +inf) &rarr; valid. Left child 1 has (-inf, 5) &rarr; valid. Right child 4 has (5, +inf) &rarr; 4 is not > 5, so it immediately returns false without recursing further. An in-order traversal would also produce a strictly increasing sequence. Edge cases with `Integer.MIN_VALUE` or `Integer.MAX_VALUE` are safely handled by 64-bit Long boundaries.\"",
        'senior_followup': "\"In concurrent databases with tree indexes (like B-Trees), BST validation is part of offline invariant audits (e.g. `pg_amcheck` in PostgreSQL) verifying that page boundary keys partition leaf tuples without overlapping key ranges.\""
    },
    'quiz': [
        {
            'question': "Why is checking `node.left.val < node.val && node.right.val > node.val` insufficient?",
            'options': [
                "It only checks immediate children; it fails if a right-subtree node is smaller than an ancestor",
                "Binary tree nodes are stored in heap memory which lacks random pointer comparison",
                "It causes infinite recursion when the tree contains negative integer values",
                "The garbage collector reorders tree node references during traversal"
            ],
            'correct_index': 0,
            'explanation': "Consider tree `[5, 1, 6, null, null, 4, 7]`. Node 6's left child is 4 (4 < 6, which is locally valid for 6), but 4 is in 5's right subtree, so it violates the global BST property (4 is not > 5)."
        },
        {
            'question': "Why are 64-bit Long boundaries used instead of 32-bit Integer.MIN_VALUE / MAX_VALUE?",
            'options': [
                "If a node holds Integer.MIN_VALUE, `node.val >= Integer.MIN_VALUE` causes false positive failure",
                "64-bit CPU registers execute binary comparisons twice as fast as 32-bit registers",
                "Java Virtual Machine specification prohibits passing Integer objects into recursive methods",
                "Integer primitives in Kotlin cannot be compared using less-than or greater-than operators"
            ],
            'correct_index': 0,
            'explanation': "If a leaf node has value `Integer.MIN_VALUE`, checking `node.val <= Integer.MIN_VALUE` would falsely report a violation unless the lower bound is strictly less than `Integer.MIN_VALUE` (e.g. `Long.MIN_VALUE`)."
        }
    ]
}
