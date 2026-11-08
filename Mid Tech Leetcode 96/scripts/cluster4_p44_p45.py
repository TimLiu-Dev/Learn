import sys
sys.path.append('scripts')

p44 = {
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
    'plain_english': "Compare points by their squared Euclidean distance `x^2 + y^2` (avoiding floating point square root calculations). Maintain a Max-Heap of size `k`. When size exceeds `k`, eject the point with the largest distance. At the end, the heap contains the `k` closest points.",
    'boundary_checks': {
        'min_base': "<code>k == points.length</code> &rarr; All points are requested; return the input array immediately.",
        'best_exit': "Point at origin <code>[0, 0]</code> &rarr; Distance is 0, guaranteed to enter and stay in the heap.",
        'worst_scan': "Ties in distance &rarr; Problem statement guarantees return order does not matter."
    },
    'mental_model': {
        'analogy_title': "The Perimeter Defense Dome (Max-Heap of Size K)",
        'analogy_text': "You have a defensive shield that can only shelter the k closest targets to the central fortress (0,0). To ensure only the closest k units stay inside, the perimeter scanner always tracks the MOST DISTANT unit currently inside the shelter (Max-Heap root). When a new unit approaches, if it is closer than the furthest sheltered unit, the outermost unit is kicked outside the dome, and the closer one takes its spot.",
        'aha_moment': "We do NOT need `Math.sqrt()`! Because $f(d) = \\sqrt{d}$ is strictly monotonically increasing for $d \\ge 0$, comparing $x^2 + y^2$ preserves the exact same relative ordering while avoiding floating-point inaccuracies.",
        'trigger': "Find K smallest / closest items &rarr; Max-Heap of size K (eject largest when size > K).",
        'sisters': [
            {'name': "Kth Largest Element in an Array", 'lc': "LC #215"},
            {'name': "Top K Frequent Elements", 'lc': "LC #347"},
            {'name': "Meeting Rooms II", 'lc': "LC #253"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N log N) Time, O(N) Space",
        'naive_text': "Sort all points by $x^2 + y^2$ using full quicksort or mergesort, then slice the first k elements.",
        'wasted_work': "Sorting all N elements when we only need the smallest k in arbitrary order.",
        'optimal_complexity': "O(N log K) Time, O(K) Space",
        'optimal_text': "Max-Heap of size k keyed on squared distance $x^2 + y^2$. Memory is strictly bounded by K.",
        'core_invariant': "The Max-Heap root holds the point with the greatest distance among the current k closest candidates.",
        'table_rows': [
            {'name': "1. Full Array Sort", 'time': "O(N log N)", 'space': "O(N)", 'note': "Sorts entire dataset; inefficient if N is large and K is small."},
            {'name': "★ 2. Max-Heap of Size K", 'time': "O(N log K)", 'space': "O(K)", 'note': "Bounded memory O(K); ideal for streaming geo-points."},
            {'name': "3. QuickSelect on Distance", 'time': "O(N) Avg, O(N^2) Worst", 'space': "O(1)", 'note': "In-place average linear partition."}
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
        'default_input': '[3,3], [5,-1], [-2,4]',
        'presets': [
            {'label': "Example [[3,3],[5,-1],[-2,4]], k=2", 'onclick': 'loadPreset([[3,3],[5,-1],[-2,4]], 2)'},
            {'label': "Example [[1,3],[-2,2]], k=1", 'onclick': 'loadPreset([[1,3],[-2,2]], 1)'}
        ],
        'metrics': [
            {'id': "metric-pt", 'label': "Current Point", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-dsq", 'label': "Squared Distance", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-furthest", 'label': "Furthest in Heap", 'color': "text-emerald-400", 'initial': "-"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="points-stage" class="w-full flex items-center justify-center gap-3 py-4 min-h-[140px]"></div>',
        'initial_narration': 'Click "Next Step" to trace Max-Heap retention of closest coordinates.',
        'pseudocode_lines': [
            "val maxHeap = PriorityQueue<IntArray> { a, b ->",
            "    val distA = a[0]*a[0] + a[1]*a[1]",
            "    val distB = b[0]*b[0] + b[1]*b[1]",
            "    distB.compareTo(distA) // Furthest at top",
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
        heap.sort((a, b) => b.d - a.d); // Max-heap: descending

        let ejected = null;
        if (heap.length > k) {
          ejected = heap.shift();
        }

        steps.push({
          pt: `[${p[0]}, ${p[1]}]`, dsq: `${d}`, furthest: `${heap[0].d}`,
          action: ejected ? `Ejected [${ejected.p[0]}, ${ejected.p[1]}] (dist ${ejected.d})` : `Retained [${p[0]}, ${p[1]}]`,
          heap: [...heap], line: ejected ? 8 : 7,
          narration: ejected
            ? `Point [${p[0]}, ${p[1]}] (dist ${d}) entered. Heap exceeded ${k}, so discarded furthest point [${ejected.p[0]}, ${ejected.p[1]}] (dist ${ejected.d}).`
            : `Point [${p[0]}, ${p[1]}] (dist ${d}) added to heap.`
        });
      }

      steps.push({
        pt: "Done", dsq: "-", furthest: `${heap[0].d}`,
        action: "Finished", heap: [...heap], line: 10,
        narration: `Finished! The ${k} closest points are: ${heap.map(x => `[${x.p[0]},${x.p[1]}]`).join(', ')}.`
      });
    }

    function loadPreset(pts, k) {
      document.getElementById('custom-input').value = pts.map(p => `[${p[0]},${p[1]}]`).join(', ');
      rawPts = pts;
      kCount = k;
      stepperReset();
      buildSteps(rawPts, kCount);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const parsed = JSON.parse('[' + val.replace(/\\[/g, '[').replace(/\\]/g, ']') + ']');
        if (parsed.length > 0) {
          rawPts = parsed;
          stepperReset();
          buildSteps(rawPts, kCount);
          renderStepper();
        }
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-pt').textContent = step.pt;
      document.getElementById('metric-dsq').textContent = step.dsq;
      document.getElementById('metric-furthest').textContent = step.furthest;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('points-stage');
      stage.innerHTML = '';
      if (!step.heap) return;

      step.heap.forEach((item, idx) => {
        const card = document.createElement('div');
        const isFurthest = (idx === 0);
        card.className = `flex flex-col items-center justify-center p-3 rounded-xl border font-mono ${
          isFurthest ? 'bg-amber-500/20 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' : 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40'
        }`;
        card.innerHTML = `
          <span class="text-[9px] text-slate-400 uppercase font-bold">${isFurthest ? 'FURTHEST (ROOT)' : 'SAFE'}</span>
          <span class="text-sm font-bold">[${item.p[0]}, ${item.p[1]}]</span>
          <span class="text-[10px] text-slate-400">d² = ${item.d}</span>
        `;
        stage.appendChild(card);
      });
"""
    },
    'approaches': [
        {
            'name': "Full Array Sort by Distance (Naive)",
            'desc': "Sort all points by squared distance in ascending order, then return the first k elements.",
            'time': "O(N log N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun kClosest(points: Array<IntArray>, k: Int): Array<IntArray> {
        points.sortWith { a, b ->
            val distA = a[0] * a[0] + a[1] * a[1]
            val distB = b[0] * b[0] + b[1] * b[1]
            distA.compareTo(distB)
        }
        return points.copyOfRange(0, k)
    }
}""",
            'notes_kotlin': "Straightforward array sorting. Sorts all N points when only K are required.",
            'code_python': """class Solution:
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        points.sort(key=lambda p: p[0]**2 + p[1]**2)
        return points[:k]""",
            'notes_python': "Clean Python one-liner. O(N log N) time.",
            'code_java': """class Solution {
    public int[][] kClosest(int[][] points, int k) {
        Arrays.sort(points, (a, b) -> {
            int distA = a[0] * a[0] + a[1] * a[1];
            int distB = b[0] * b[0] + b[1] * b[1];
            return Integer.compare(distA, distB);
        });
        return Arrays.copyOfRange(points, 0, k);
    }
}""",
            'notes_java': "Sorts all N elements; suboptimal for small K and large N."
        },
        {
            'name': "QuickSelect on Distance (In-Place Linear Average)",
            'desc': "Partition points around a pivot distance until the k smallest are in indices 0 to k-1.",
            'time': "O(N) Average, O(N^2) Worst",
            'space': "O(1) Auxiliary",
            'code_kotlin': """class Solution {
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
            'notes_kotlin': "In-place QuickSelect eliminates extra memory. Worst-case is O(N^2) on bad pivot choices.",
            'code_python': """class Solution:
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
            'notes_python': "Hoare partitioning on distance metric.",
            'code_java': """class Solution {
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
            'notes_java': "Average linear runtime, but alters the input array order."
        },
        {
            'name': "Max-Heap of Size K (Optimal Primary)",
            'desc': "Maintain a Max-Heap of capacity k. Eject the furthest point when size > k. Optimal for stream processing.",
            'time': "O(N log K)",
            'space': "O(K)",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Max-Heap of Size K
     * Time: O(N log K) - Each point insertion takes log K
     * Space: O(K) - Heap capacity strictly bounded by K
     */
    fun kClosest(points: Array<IntArray>, k: Int): Array<IntArray> {
        val maxHeap = java.util.PriorityQueue<IntArray> { a, b ->
            val distA = a[0] * a[0] + a[1] * a[1]
            val distB = b[0] * b[0] + b[1] * b[1]
            distB.compareTo(distA) // Furthest at top
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
            'notes_kotlin': "Using squared distance eliminates `Math.sqrt()` float precision and CPU cycles.",
            'code_python': """class Solution:
    # Optimal: Max-Heap of Size K
    # Time: O(N log K)
    # Space: O(K)
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        max_heap = []
        for x, y in points:
            dist = -(x * x + y * y)
            heapq.heappush(max_heap, (dist, [x, y]))
            if len(max_heap) > k:
                heapq.heappop(max_heap)
                
        return [pt for (_, pt) in max_heap]""",
            'notes_python': "Negating distance achieves max-heap behavior with Python's standard `heapq`.",
            'code_java': """class Solution {
    /**
     * Optimal: Max-Heap of Size K
     * Time: O(N log K) - Consistent heap maintenance
     * Space: O(K) - Heap bounded to K points
     */
    public int[][] kClosest(int[][] points, int k) {
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
            'notes_java': "Use `Integer.compare` to prevent subtraction integer underflow/overflow bugs."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun kClosest(points: Array<IntArray>, k: Int): Array<IntArray> {
        val maxHeap = java.util.PriorityQueue<IntArray> { a, b ->
            (b[0]*b[0] + b[1]*b[1]).compareTo(a[0]*a[0] + a[1]*a[1])
        }
        for (pt in points) {
            maxHeap.offer(pt)
            if (maxHeap.size > k) maxHeap.poll()
        }
        return Array(k) { maxHeap.poll() }
    }
}""",
        'python_boilerplate': """import heapq

class Solution:
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        h = []
        for x, y in points:
            heapq.heappush(h, (-(x*x + y*y), [x, y]))
            if len(h) > k: heapq.heappop(h)
        return [p for (_, p) in h]""",
        'java_boilerplate': """class Solution {
    public int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> 
            Integer.compare(b[0]*b[0] + b[1]*b[1], a[0]*a[0] + a[1]*a[1])
        );
        for (int[] p : points) {
            pq.offer(p);
            if (pq.size() > k) pq.poll();
        }
        int[][] res = new int[k][2];
        for (int i = 0; i < k; i++) res[i] = pq.poll();
        return res;
    }
}""",
        'js_test_runner': """
      const testCases = [
        { pts: [[1,3],[-2,2]], k: 1, expected: [[-2,2]] },
        { pts: [[3,3],[5,-1],[-2,4]], k: 2, expected: [[3,3],[-2,4]] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runKClosest(arr, k) {
          let h = [];
          for (let p of arr) {
            let d = p[0]*p[0] + p[1]*p[1];
            h.push({ p: p, d: d });
            h.sort((a, b) => b.d - a.d);
            if (h.length > k) h.shift();
          }
          return h.map(x => x.p);
        }

        const out = runKClosest(tc.pts, tc.k);
        function ptKey(p) { return `${p[0]},${p[1]}`; }
        const sOut = new Set(out.map(ptKey));
        const sExp = new Set(tc.expected.map(ptKey));
        const passed = (sOut.size === sExp.size) && [...sOut].every(x => sExp.has(x));
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: pts=[${tc.pts.map(p => '['+p.join(',')+']').join(',')}] k=${tc.k}</span>
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
        'phase1': "I clarify: Does the returned list need to be ordered by distance? The problem states points may be returned in any order. What if two points have identical distance? The problem guarantees the answer is unique. Can coordinates be negative? Yes, between -10^4 and 10^4.",
        'phase2': "For strategy: To find the k SMALLEST distances, we maintain a MAX-HEAP of size k. We compare points by squared Euclidean distance `x^2 + y^2`, which avoids floating-point square root operations. The root of the Max-Heap is the furthest point among our top-k candidates; when a new closer point arrives, the furthest point is evicted. Time is O(N log K) and space is O(K).",
        'phase3': "I implement `kClosest`: instantiate a `PriorityQueue<IntArray>` with a comparator sorting descending by `p[0]*p[0] + p[1]*p[1]`. For each point, offer it to the heap; if `heap.size > k`, poll the head. Finally, drain the k points into a 2D integer array and return.",
        'phase4': "Dry run with points=[[3,3], [5,-1], [-2,4]], k=2: [3,3] (dist 18) enters. [5,-1] (dist 26) enters. [-2,4] (dist 20) enters, size is 3 > 2, so the max distance point [5,-1] (dist 26) is evicted. Remaining points are [3,3] and [-2,4]. Correct.",
        'senior_followup': "Senior Scalability: In geospatial systems (e.g. Uber/Lyft driver dispatch), searching k nearest drivers is accelerated using spatial partitioning structures like R-Trees or Geohash buckets, querying only candidate clusters within a bounding box rather than scanning all N worldwide coordinates."
    },
    'quiz': [
        {
            'question': "Why do we use a MAX-heap to find the K CLOSEST (smallest distance) points?",
            'options': [
                "Because a Max-Heap exposes the furthest candidate among the current K closest at the root, allowing O(log K) eviction when a closer candidate arrives.",
                "Because min-heaps cannot compare 2D coordinates.",
                "Because distances are always positive numbers.",
                "It guarantees the returned array is sorted in ascending order."
            ],
            'correct_index': 0,
            'explanation': "To retain the K smallest values, we need quick access to the LARGEST of those K values so that any incoming smaller value can replace it."
        },
        {
            'question': "Why should we avoid calculating `Math.sqrt(x*x + y*y)` when comparing distances?",
            'options': [
                "Square root is illegal in Java PriorityQueues.",
                "Because sqrt is strictly monotonic for non-negative numbers; comparing squared distance yields identical ordering while avoiding CPU cycle overhead and floating-point rounding errors.",
                "Because coordinates can be negative, producing imaginary numbers in sqrt.",
                "Because sqrt truncates values to 0."
            ],
            'correct_index': 1,
            'explanation': "Since $a^2 + b^2 \\ge 0$ is always non-negative, $d_1 < d_2 \\iff d_1^2 < d_2^2$. Bypassing square root avoids precision loss and saves expensive CPU instructions."
        }
    ]
}

p45 = {
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
    'plain_english': "Count occurrences of each number in a hash map. Then use Bucket Sort where the array index directly represents frequency (0 to N). Traverse the buckets backward from highest frequency N down to 1, collecting numbers until k elements are gathered in strictly linear O(N) time.",
    'boundary_checks': {
        'min_base': "<code>nums = [1], k = 1</code> &rarr; Single element returns <code>[1]</code> directly.",
        'best_exit': "<code>k == unique elements count</code> &rarr; Returns all unique elements.",
        'worst_scan': "All elements have frequency 1 (e.g. `[1,2,3,4], k=2`) &rarr; Problem guarantees answer is unique."
    },
    'mental_model': {
        'analogy_title': "Pigeonhole Ballot Counting Shelves",
        'analogy_text': "Suppose you have N ballots. You count the votes for each candidate. You set up N + 1 pigeonhole shelves numbered 0 to N, representing the number of votes received. A candidate with 3 votes goes onto Shelf #3; a candidate with 10 votes goes onto Shelf #10. To find the top K candidates, you simply walk from Shelf #N down to Shelf #0, collecting candidates until you have K winners. No sorting required!",
        'aha_moment': "An element's frequency can NEVER exceed N (the array length). Because the frequency range is bounded between 0 and N, Bucket Sort solves Top K in strict O(N) linear time without any heap or comparison sorting!",
        'trigger': "Top K frequent / count-based rankings with bounded frequencies &rarr; Bucket Sort (index = frequency) or Min-Heap of size K.",
        'sisters': [
            {'name': "Sort Characters By Frequency", 'lc': "LC #451"},
            {'name': "Kth Largest Element in an Array", 'lc': "LC #215"},
            {'name': "Word Frequency", 'lc': "LC #192"}
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
            {'label': "Array [4,1,-1,2,-1,2,3], k=2", 'onclick': 'loadPreset([4, 1, -1, 2, -1, 2, 3], 2)'}
        ],
        'metrics': [
            {'id': "metric-freq", 'label': "Examining Shelf", 'color': "text-amber-400", 'initial': "-"},
            {'id': "metric-items", 'label': "Shelf Elements", 'color': "text-cyan-400", 'initial': "[]"},
            {'id': "metric-res", 'label': "Collected Top K", 'color': "text-emerald-400", 'initial': "[]"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="bucket-stage" class="w-full flex items-center justify-center gap-2 py-4 min-h-[140px] overflow-x-auto"></div>',
        'initial_narration': 'Click "Next Step" to trace bucket scanning from highest frequency downward.',
        'pseudocode_lines': [
            "val countMap = nums.toList().groupingBy { it }.eachCount()",
            "val buckets = Array(nums.size + 1) { mutableListOf<Int>() }",
            "for ((num, count) in countMap) buckets[count].add(num)",
            "val result = IntArray(k); var idx = 0",
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
        freq: "-", items: "-", res: "[]", action: "Frequencies Counted",
        buckets: buckets, activeFreq: -1, line: 3,
        narration: `Calculated frequencies: ${JSON.stringify(counts)}. Populated buckets 0 to ${n}.`
      });

      let res = [];
      for (let f = n; f >= 1; f--) {
        if (buckets[f].length > 0) {
          steps.push({
            freq: `Shelf ${f}`, items: `[${buckets[f].join(', ')}]`, res: `[${res.join(', ')}]`,
            action: `Inspecting Shelf ${f}`, buckets: buckets, activeFreq: f, line: 5,
            narration: `Found numbers with frequency ${f}: [${buckets[f].join(', ')}].`
          });

          for (let num of buckets[f]) {
            res.push(num);
            steps.push({
              freq: `Shelf ${f}`, items: `[${buckets[f].join(', ')}]`, res: `[${res.join(', ')}]`,
              action: `Collected ${num}`, buckets: buckets, activeFreq: f, line: 7,
              narration: `Collected ${num} into top K result (size: ${res.length}/${k}).`
            });
            if (res.length === k) {
              steps.push({
                freq: "Complete", items: "-", res: `[${res.join(', ')}]`,
                action: "Target K Reached", buckets: buckets, activeFreq: f, line: 8,
                narration: `Collected all ${k} top frequent elements! Done.`
              });
              return;
            }
          }
        }
      }
    }

    function loadPreset(arr, k) {
      document.getElementById('custom-input').value = arr.join(', ');
      dataList = arr;
      topK = k;
      stepperReset();
      buildSteps(dataList, topK);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
      if (arr.length > 0) {
        dataList = arr;
        stepperReset();
        buildSteps(dataList, topK);
        renderStepper();
      }
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-freq').textContent = step.freq;
      document.getElementById('metric-items').textContent = step.items;
      document.getElementById('metric-res').textContent = step.res;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('bucket-stage');
      stage.innerHTML = '';
      if (!step.buckets) return;

      step.buckets.forEach((b, idx) => {
        if (b.length === 0 && idx !== step.activeFreq) return;
        const box = document.createElement('div');
        const isActive = (idx === step.activeFreq);
        box.className = `flex flex-col items-center justify-center p-2 rounded-xl border min-w-[64px] font-mono ${
          isActive ? 'bg-amber-500/20 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' : 'bg-slate-900 text-slate-300 border-slate-800'
        }`;
        box.innerHTML = `
          <span class="text-[9px] text-slate-400 font-bold uppercase">Freq ${idx}</span>
          <span class="text-sm font-bold">${b.length > 0 ? b.join(', ') : 'Ø'}</span>
        `;
        stage.appendChild(box);
      });
"""
    },
    'approaches': [
        {
            'name': "Frequency Map + Full Sort (Naive)",
            'desc': "Count frequencies in a Map, convert to a list of entries, and sort descending by value.",
            'time': "O(U log U)",
            'space': "O(U)",
            'code_kotlin': """class Solution {
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        val countMap = HashMap<Int, Int>()
        for (num in nums) countMap[num] = countMap.getOrDefault(num, 0) + 1

        return countMap.entries
            .sortedByDescending { it.value }
            .take(k)
            .map { it.key }
            .toIntArray()
    }
}""",
            'notes_kotlin': "Idiomatic Kotlin collections sort. Sorts all U unique keys.",
            'code_python': """class Solution:
    def topKFrequent(self, nums: List[int], k: int) -> List[int]:
        counts = collections.Counter(nums)
        sorted_keys = sorted(counts.keys(), key=lambda x: counts[x], reverse=True)
        return sorted_keys[:k]""",
            'notes_python': "Sorts all keys. Runs in O(U log U) time.",
            'code_java': """class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int num : nums) countMap.put(num, countMap.getOrDefault(num, 0) + 1);

        List<Integer> keys = new ArrayList<>(countMap.keySet());
        keys.sort((a, b) -> countMap.get(b).compareTo(countMap.get(a)));

        int[] result = new int[k];
        for (int i = 0; i < k; i++) result[i] = keys.get(i);
        return result;
    }
}""",
            'notes_java': "O(U log U) comparison sort on all distinct elements."
        },
        {
            'name': "Frequency Map + Min-Heap of Size K (Streaming)",
            'desc': "Maintain a Min-Heap of size k comparing map frequencies. Eject lowest frequency when heap size > k.",
            'time': "O(N log K)",
            'space': "O(N + K)",
            'code_kotlin': """class Solution {
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
            'notes_kotlin': "Standard heap approach. Excellent for streaming data feeds.",
            'code_python': """class Solution:
    def topKFrequent(self, nums: List[int], k: int) -> List[int]:
        count_map = collections.Counter(nums)
        min_heap = []
        
        for num, freq in count_map.items():
            heapq.heappush(min_heap, (freq, num))
            if len(min_heap) > k:
                heapq.heappop(min_heap)
                
        return [num for (freq, num) in min_heap]""",
            'notes_python': "Bounded heap size k ensures memory is kept low.",
            'code_java': """class Solution {
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
            'notes_java': "O(N log K) time with O(N + K) auxiliary space."
        },
        {
            'name': "Frequency Map + Bucket Sort (Optimal Primary)",
            'desc': "Tally frequencies in a HashMap, place numbers into an array of buckets where index = frequency, scan downward.",
            'time': "O(N)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Bucket Sort by Frequency
     * Time: O(N) - Strictly linear time bypassing comparison sorting
     * Space: O(N) - Bounded array of size N + 1
     */
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
            'notes_kotlin': "Bypasses comparison sort entirely. Guarantees linear O(N) performance on all inputs.",
            'code_python': """class Solution:
    # Optimal: Bucket Sort by Frequency
    # Time: O(N) - Linear time
    # Space: O(N)
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
            'notes_python': "Index mapping frequency directly delivers guaranteed O(N) runtime.",
            'code_java': """class Solution {
    /**
     * Optimal: Bucket Sort by Frequency
     * Time: O(N) - Linear execution
     * Space: O(N) - Array of frequency buckets
     */
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
            'notes_java': "Unchecked generic array cast is safe here because array elements are strictly `List<Integer>`."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        val map = HashMap<Int, Int>()
        for (n in nums) map[n] = map.getOrDefault(n, 0) + 1
        val buckets = Array(nums.size + 1) { mutableListOf<Int>() }
        for ((n, c) in map) buckets[c].add(n)
        val res = IntArray(k)
        var i = 0
        for (f in buckets.indices.reversed()) {
            for (n in buckets[f]) {
                res[i++] = n
                if (i == k) return res
            }
        }
        return res
    }
}""",
        'python_boilerplate': """import collections

class Solution:
    def topKFrequent(self, nums: List[int], k: int) -> List[int]:
        counts = collections.Counter(nums)
        buckets = [[] for _ in range(len(nums) + 1)]
        for n, c in counts.items(): buckets[c].append(n)
        res = []
        for f in range(len(nums), 0, -1):
            for n in buckets[f]:
                res.append(n)
                if len(res) == k: return res
        return res""",
        'java_boilerplate': """class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int n : nums) map.put(n, map.getOrDefault(n, 0) + 1);
        List<Integer>[] buckets = new List[nums.length + 1];
        for (int i = 0; i <= nums.length; i++) buckets[i] = new ArrayList<>();
        for (var e : map.entrySet()) buckets[e.getValue()].add(e.getKey());
        int[] res = new int[k];
        int idx = 0;
        for (int f = nums.length; f >= 0 && idx < k; f--) {
            for (int n : buckets[f]) {
                res[idx++] = n;
                if (idx == k) return res;
            }
        }
        return res;
    }
}""",
        'js_test_runner': """
      const testCases = [
        { nums: [1,1,1,2,2,3], k: 2, expected: [1,2] },
        { nums: [1], k: 1, expected: [1] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runTopK(arr, k) {
          let counts = {};
          arr.forEach(n => counts[n] = (counts[n] || 0) + 1);
          let b = Array.from({ length: arr.length + 1 }, () => []);
          Object.keys(counts).forEach(n => b[counts[n]].push(Number(n)));
          let res = [];
          for (let f = arr.length; f >= 1; f--) {
            for (let num of b[f]) {
              res.push(num);
              if (res.length === k) return res;
            }
          }
          return res;
        }

        const out = runTopK(tc.nums, tc.k);
        const sOut = new Set(out);
        const sExp = new Set(tc.expected);
        const passed = (sOut.size === sExp.size) && [...sOut].every(x => sExp.has(x));
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: nums=[${tc.nums.join(',')}] k=${tc.k}</span>
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
        'phase1': "I clarify: Is the output order specified? The problem states points can be in any order. Is the answer guaranteed to be unique? Yes. Can numbers be negative? Yes, between -10^4 and 10^4.",
        'phase2': "For strategy: Tallying frequencies in a HashMap takes O(N). Sorting the frequencies takes O(U log U), and a Min-Heap of size k takes O(N log K). But we can achieve strict O(N) linear time using Bucket Sort! Because no element can appear more than N times, the maximum possible frequency is N. We allocate an array of lists of size N + 1 where index represents frequency, and scan backward from N to 1 until k elements are collected.",
        'phase3': "I implement `topKFrequent`: first count frequencies using `HashMap<Int, Int>`. Next allocate `Array(nums.size + 1) { mutableListOf<Int>() }`. Insert each number into `buckets[count]`. Iterate backward from `nums.size` down to 1, collecting numbers into `result` array until `idx == k`.",
        'phase4': "Dry run with [1,1,1,2,2,3], k=2: countMap = {1:3, 2:2, 3:1}. Bucket 3 has [1], bucket 2 has [2], bucket 1 has [3]. Scanning downward: bucket 3 yields 1, bucket 2 yields 2. Target k=2 is reached, returning [1, 2]. Correct in linear time.",
        'senior_followup': "Senior Scalability: In high-throughput streaming systems (e.g. trending hashtags on Twitter/X), exact counts across billions of events exceed memory limits. Systems employ the Count-Min Sketch or Heavy Hitters algorithms (Misra-Gries) to maintain approximate top-k items in bounded sub-megabyte memory."
    },
    'quiz': [
        {
            'question': "Why is Bucket Sort able to achieve O(N) time for Top K Frequent Elements, beating the standard O(N log N) sorting lower bound?",
            'options': [
                "Because the possible frequency of any element is strictly bounded between 1 and N (the length of the array), allowing non-comparison pigeonhole distribution.",
                "Because integers in the array are positive.",
                "Because it uses multi-threaded CPU instructions.",
                "Because the answer is guaranteed to be unique."
            ],
            'correct_index': 0,
            'explanation': "The $\\Omega(N \\log N)$ bound applies strictly to comparison-based sorting. Because the range of possible frequencies $[0..N]$ is known and bounded by the input size, Bucket Sort places elements into slots in $O(N)$ linear time."
        },
        {
            'question': "When would an engineer choose the Min-Heap approach over Bucket Sort in production?",
            'options': [
                "When data is continuously streamed and total N is unknown or unbounded, preventing allocation of an array of size N + 1.",
                "When all elements in the array are negative.",
                "When k equals the array size.",
                "When running on 32-bit JVMs."
            ],
            'correct_index': 0,
            'explanation': "If data arrives as an infinite stream, allocating an array of size `N + 1` is impossible. A Min-Heap of fixed size `k` maintains the top-k elements in memory at all times using strictly $O(k)$ space."
        }
    ]
}
