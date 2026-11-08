import sys
sys.path.append('scripts')

p50 = {
    'num': 50,
    'lc': 54,
    'title': "Spiral Matrix",
    'category': "2D Grid Traversals & Graphs",
    'difficulty': "Medium",
    'lc_slug': "spiral-matrix",
    'verbatim_statement': """<p>Given an <code>m x n</code> <code>matrix</code>, return <em>all elements of the</em> <code>matrix</code> <em>in spiral order</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'matrix = [[1,2,3],[4,5,6],[7,8,9]]', 'output': '[1,2,3,6,9,8,7,4,5]'},
        {'title': "Example 2", 'input': 'matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]', 'output': '[1,2,3,4,8,12,11,10,9,5,6,7]'}
    ],
    'constraints': [
        "m == matrix.length",
        "n == matrix[i].length",
        "1 <= m, n <= 10",
        "-100 <= matrix[i][j] <= 100"
    ],
    'plain_english': "Traverse a 2D matrix in clockwise spiral order: top edge (left to right), right edge (top to bottom), bottom edge (right to left), left edge (bottom to top). Maintain 4 boundary pointers: `top`, `bottom`, `left`, `right`. After traversing each edge, contract that boundary inward. Ensure `top <= bottom` and `left <= right` before the bottom and left sweeps.",
    'boundary_checks': {
        'min_base': "Single element <code>[[1]]</code> &rarr; Returns <code>[1]</code>.",
        'best_exit': "Single row <code>[[1, 2, 3, 4]]</code> &rarr; Boundary check prevents bottom-sweep duplicate traversal.",
        'worst_scan': "Single column <code>[[1],[2],[3],[4]]</code> &rarr; Boundary check prevents left-sweep duplicate traversal."
    },
    'mental_model': {
        'analogy_title': "The Shrinking Crop Harvester",
        'analogy_text': "Imagine a combine harvester cutting wheat around the perimeter of a rectangular farm. First it drives along the northern boundary fence from West to East, moving the northern fence inward by 1 row (`top++`). Next it cuts down the Eastern boundary fence from North to South, shifting the fence inward (`right--`). Next it cuts the Southern boundary Westward (`bottom--`), and finally up the Western boundary Northward (`left++`). It repeats until the fences meet in the center.",
        'aha_moment': "Always check `if (top <= bottom)` before sweeping the bottom edge, and `if (left <= right)` before sweeping the left edge! For non-square matrices (e.g. 1x4 or 3x1), the top/right boundary increments can cause boundaries to cross mid-cycle.",
        'trigger': "Spiral order, perimeter peeling, clockwise matrix traversal &rarr; 4 Boundary Pointers (top, bottom, left, right) with contraction.",
        'sisters': [
            {'name': "Spiral Matrix II", 'lc': "LC #59"},
            {'name': "Rotate Image", 'lc': "LC #48"},
            {'name': "Set Matrix Zeroes", 'lc': "LC #73"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(M * N) Time, O(M * N) Space",
        'naive_text': "Maintain a `visited[M][N]` boolean grid and direction vector array `[(0,1), (1,0), (0,-1), (-1,0)]`. Turn clockwise when hitting a boundary or visited cell.",
        'wasted_work': "Allocating an auxiliary boolean matrix for visited tracking.",
        'optimal_complexity': "O(M * N) Time, O(1) Auxiliary Space",
        'optimal_text': "4 Boundary pointers (`top`, `bottom`, `left`, `right`). Contracts boundaries inward in O(1) extra space.",
        'core_invariant': "At the start of each layer, all cells outside the bounding box `[top..bottom][left..right]` have already been visited in spiral order.",
        'table_rows': [
            {'name': "1. Direction Vectors + Visited Grid", 'time': "O(M * N)", 'space': "O(M * N)", 'note': "Allocates auxiliary visited matrix and checks bounds every cell."},
            {'name': "2. Recursive Layer Peeling", 'time': "O(M * N)", 'space': "O(min(M, N))", 'note': "Peels outer shell recursively; call stack overhead."},
            {'name': "★ 3. Four Boundary Pointers", 'time': "O(M * N)", 'space': "O(1) Auxiliary", 'note': "Direct pointer contraction; zero extra memory."}
        ]
    },
    'trace_table': {
        'input_desc': 'matrix = [[1,2,3],[4,5,6],[7,8,9]] (M=3, N=3)',
        'columns': ["Cycle", "Direction / Edge Swept", "Coordinates Visited", "Values Appended", "Boundary Contraction"],
        'rows': [
            ["Init", "-", "-", "-", "top=0, bottom=2, left=0, right=2"],
            ["Cycle 1", "Top (Left &rarr; Right)", "(0,0), (0,1), (0,2)", "[1, 2, 3]", "top becomes 1"],
            ["Cycle 1", "Right (Top &rarr; Bottom)", "(1,2), (2,2)", "[6, 9]", "right becomes 1"],
            ["Cycle 1", "Bottom (Right &rarr; Left)", "(2,1), (2,0)", "[8, 7]", "bottom becomes 1"],
            ["Cycle 1", "Left (Bottom &rarr; Top)", "(1,0)", "[4]", "left becomes 1"],
            ["Cycle 2", "Top (Left &rarr; Right)", "(1,1)", "[5]", "top becomes 2 > bottom=1 &rarr; Halt!"]
        ],
        'takeaway': "Total values collected = 9. Boundaries cross naturally (`top > bottom`), terminating the loop."
    },
    'stepper': {
        'title': "Spiral Matrix 4-Boundary Stepper",
        'default_input': '[[1,2,3],[4,5,6],[7,8,9]]',
        'presets': [
            {'label': "3x3 Matrix", 'onclick': 'loadPreset([[1,2,3],[4,5,6],[7,8,9]])'},
            {'label': "3x4 Matrix", 'onclick': 'loadPreset([[1,2,3,4],[5,6,7,8],[9,10,11,12]])'},
            {'label': "Single Row [[1,2,3,4]]", 'onclick': 'loadPreset([[1,2,3,4]])'}
        ],
        'metrics': [
            {'id': "metric-bounds", 'label': "Active Bounds (T,B,L,R)", 'color': "text-amber-400", 'initial': "0, 2, 0, 2"},
            {'id': "metric-curr", 'label': "Current Cell (r,c)", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-count", 'label': "Elements Visited", 'color': "text-emerald-400", 'initial': "0"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="spiral-stage" class="w-full flex items-center justify-center p-4 min-h-[160px]"></div>',
        'initial_narration': 'Click "Next Step" to trace spiral perimeter contraction.',
        'pseudocode_lines': [
            "var top = 0; var bottom = m - 1; var left = 0; var right = n - 1",
            "while (top <= bottom && left <= right) {",
            "    for (c in left..right) result.add(matrix[top][c]); top++",
            "    for (r in top..bottom) result.add(matrix[r][right]); right--",
            "    if (top <= bottom) { for (c in right downTo left) result.add(matrix[bottom][c]); bottom-- }",
            "    if (left <= right) { for (r in bottom downTo top) result.add(matrix[r][left]); left++ }",
            "}"
        ],
        'js_code': """
    let gridData = [[1,2,3],[4,5,6],[7,8,9]];

    function buildSteps(matrix) {
      steps = [];
      let m = matrix.length;
      let n = matrix[0].length;
      let top = 0, bottom = m - 1, left = 0, right = n - 1;
      let res = [];

      steps.push({
        bounds: `${top}, ${bottom}, ${left}, ${right}`, curr: "-",
        count: 0, action: "Init Boundaries",
        top: top, bottom: bottom, left: left, right: right,
        activeR: -1, activeC: -1, res: [], line: 1,
        narration: `Initialized boundaries: top=${top}, bottom=${bottom}, left=${left}, right=${right}.`
      });

      while (top <= bottom && left <= right) {
        // Top row
        for (let c = left; c <= right; c++) {
          res.push(matrix[top][c]);
          steps.push({
            bounds: `${top}, ${bottom}, ${left}, ${right}`, curr: `(${top},${c}) = ${matrix[top][c]}`,
            count: res.length, action: "Sweep Right",
            top: top, bottom: bottom, left: left, right: right,
            activeR: top, activeC: c, res: [...res], line: 3,
            narration: `Traversing top edge: visited [${top}][${c}] = ${matrix[top][c]}.`
          });
        }
        top++;

        // Right column
        for (let r = top; r <= bottom; r++) {
          res.push(matrix[r][right]);
          steps.push({
            bounds: `${top}, ${bottom}, ${left}, ${right}`, curr: `(${r},${right}) = ${matrix[r][right]}`,
            count: res.length, action: "Sweep Down",
            top: top, bottom: bottom, left: left, right: right,
            activeR: r, activeC: right, res: [...res], line: 4,
            narration: `Traversing right edge: visited [${r}][${right}] = ${matrix[r][right]}.`
          });
        }
        right--;

        // Bottom row
        if (top <= bottom) {
          for (let c = right; c >= left; c--) {
            res.push(matrix[bottom][c]);
            steps.push({
              bounds: `${top}, ${bottom}, ${left}, ${right}`, curr: `(${bottom},${c}) = ${matrix[bottom][c]}`,
              count: res.length, action: "Sweep Left",
              top: top, bottom: bottom, left: left, right: right,
              activeR: bottom, activeC: c, res: [...res], line: 5,
              narration: `Traversing bottom edge: visited [${bottom}][${c}] = ${matrix[bottom][c]}.`
            });
          }
          bottom--;
        }

        // Left column
        if (left <= right) {
          for (let r = bottom; r >= top; r--) {
            res.push(matrix[r][left]);
            steps.push({
              bounds: `${top}, ${bottom}, ${left}, ${right}`, curr: `(${r},${left}) = ${matrix[r][left]}`,
              count: res.length, action: "Sweep Up",
              top: top, bottom: bottom, left: left, right: right,
              activeR: r, activeC: left, res: [...res], line: 6,
              narration: `Traversing left edge: visited [${r}][${left}] = ${matrix[r][left]}.`
            });
          }
          left++;
        }
      }

      steps.push({
        bounds: `${top}, ${bottom}, ${left}, ${right}`, curr: "Done",
        count: res.length, action: "Spiral Complete",
        top: top, bottom: bottom, left: left, right: right,
        activeR: -1, activeC: -1, res: [...res], line: 2,
        narration: `Spiral complete! Result: [${res.join(', ')}].`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = JSON.stringify(arr);
      gridData = arr;
      stepperReset();
      buildSteps(gridData);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const parsed = JSON.parse(val);
        if (Array.isArray(parsed) && parsed.length > 0) {
          gridData = parsed;
          stepperReset();
          buildSteps(gridData);
          renderStepper();
        }
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-bounds').textContent = step.bounds;
      document.getElementById('metric-curr').textContent = step.curr;
      document.getElementById('metric-count').textContent = step.count;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('spiral-stage');
      stage.innerHTML = '';

      const table = document.createElement('div');
      table.className = 'grid gap-1.5 p-3 bg-slate-950 border border-slate-800 rounded-xl';
      const m = gridData.length;
      const n = gridData[0].length;
      table.style.gridTemplateColumns = `repeat(${n}, minmax(0, 1fr))`;

      for (let r = 0; r < m; r++) {
        for (let c = 0; c < n; c++) {
          const cell = document.createElement('div');
          const isCurr = (r === step.activeR && c === step.activeC);
          const isVisited = step.res && step.res.includes(gridData[r][c]);

          cell.className = `w-10 h-10 flex items-center justify-center rounded-lg font-mono text-xs font-bold border ${
            isCurr ? 'bg-amber-500/30 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' :
            isVisited ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' :
            'bg-slate-900 text-slate-400 border-slate-800'
          }`;
          cell.textContent = gridData[r][c];
          table.appendChild(cell);
        }
      }
      stage.appendChild(table);
"""
    },
    'approaches': [
        {
            'name': "Direction Vectors + Visited Grid (Naive)",
            'desc': "Use a boolean visited array and 4 direction vectors. Turn clockwise whenever the next step is blocked or out of bounds.",
            'time': "O(M * N)",
            'space': "O(M * N)",
            'code_kotlin': """class Solution {
    fun spiralOrder(matrix: Array<IntArray>): List<Int> {
        val m = matrix.size
        val n = matrix[0].size
        val visited = Array(m) { BooleanArray(n) }
        val result = mutableListOf<Int>()
        val dr = intArrayOf(0, 1, 0, -1)
        val dc = intArrayOf(1, 0, -1, 0)
        var r = 0; var c = 0; var d = 0

        for (i in 0 until m * n) {
            result.add(matrix[r][c])
            visited[r][c] = true
            val nr = r + dr[d]
            val nc = c + dc[d]
            if (nr in 0 until m && nc in 0 until n && !visited[nr][nc]) {
                r = nr; c = nc
            } else {
                d = (d + 1) % 4
                r += dr[d]; c += dc[d]
            }
        }
        return result
    }
}""",
            'notes_kotlin': "Direction vector rotation. Requires an auxiliary O(M * N) boolean grid.",
            'code_python': """class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        m, n = len(matrix), len(matrix[0])
        visited = [[False] * n for _ in range(m)]
        res = []
        dr = [0, 1, 0, -1]
        dc = [1, 0, -1, 0]
        r = c = d = 0
        for _ in range(m * n):
            res.append(matrix[r][c])
            visited[r][c] = True
            nr, nc = r + dr[d], c + dc[d]
            if 0 <= nr < m and 0 <= nc < n and not visited[nr][nc]:
                r, c = nr, nc
            else:
                d = (d + 1) % 4
                r, c = r + dr[d], c + dc[d]
        return res""",
            'notes_python': "Simulation approach. Correct, but consumes extra space for visited table.",
            'code_java': """class Solution {
    public List<Integer> spiralOrder(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean[][] visited = new boolean[m][n];
        List<Integer> result = new ArrayList<>();
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int r = 0, c = 0, d = 0;
        for (int i = 0; i < m * n; i++) {
            result.add(matrix[r][c]);
            visited[r][c] = true;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr >= 0 && nr < m && nc >= 0 && nc < n && !visited[nr][nc]) {
                r = nr; c = nc;
            } else {
                d = (d + 1) % 4;
                r += dr[d]; c += dc[d];
            }
        }
        return result;
    }
}""",
            'notes_java': "O(M * N) space for boolean flags."
        },
        {
            'name': "Recursive Shell Peeling",
            'desc': "Recursively peel the outermost perimeter shell, then recurse on the interior submatrix.",
            'time': "O(M * N)",
            'space': "O(min(M, N)) Call Stack",
            'code_kotlin': """class Solution {
    fun spiralOrder(matrix: Array<IntArray>): List<Int> {
        val result = mutableListOf<Int>()
        peel(matrix, 0, matrix.size - 1, 0, matrix[0].size - 1, result)
        return result
    }

    private fun peel(mat: Array<IntArray>, t: Int, b: Int, l: Int, r: Int, res: MutableList<Int>) {
        if (t > b || l > r) return
        for (c in l..r) res.add(mat[t][c])
        for (row in t + 1..b) res.add(mat[row][r])
        if (t < b && l < r) {
            for (c in r - 1 downTo l) res.add(mat[b][c])
            for (row in b - 1 downTo t + 1) res.add(mat[row][l])
        }
        peel(mat, t + 1, b - 1, l + 1, r - 1, res)
    }
}""",
            'notes_kotlin': "Recursive shell peeling. Consumes call stack memory.",
            'code_python': """class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        res = []
        def peel(t, b, l, r):
            if t > b or l > r: return
            for c in range(l, r + 1): res.append(matrix[t][c])
            for row in range(t + 1, b + 1): res.append(matrix[row][r])
            if t < b and l < r:
                for c in range(r - 1, l - 1, -1): res.append(matrix[b][c])
                for row in range(b - 1, t, -1): res.append(matrix[row][l])
            peel(t + 1, b - 1, l + 1, r - 1)
        peel(0, len(matrix) - 1, 0, len(matrix[0]) - 1)
        return res""",
            'notes_python': "Recursive division of matrix shells.",
            'code_java': """class Solution {
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        peel(matrix, 0, matrix.length - 1, 0, matrix[0].length - 1, result);
        return result;
    }

    private void peel(int[][] mat, int t, int b, int l, int r, List<Integer> res) {
        if (t > b || l > r) return;
        for (int c = l; c <= r; c++) res.add(mat[t][c]);
        for (int row = t + 1; row <= b; row++) res.add(mat[row][r]);
        if (t < b && l < r) {
            for (int c = r - 1; c >= l; c--) res.add(mat[b][c]);
            for (int row = b - 1; row > t; row--) res.add(mat[row][l]);
        }
        peel(mat, t + 1, b - 1, l + 1, r - 1, res);
    }
}""",
            'notes_java': "Clean recursive formulation with call stack overhead."
        },
        {
            'name': "Four Boundary Pointers (Optimal Primary)",
            'desc': "Maintain top, bottom, left, right bounds. Sweep 4 edges, contracting boundaries inward.",
            'time': "O(M * N)",
            'space': "O(1) Auxiliary Space",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Four Boundary Pointers Traversal
     * Time: O(M * N) - Visits every matrix cell exactly once
     * Space: O(1) - Only four boundary index variables (output list not counted)
     */
    fun spiralOrder(matrix: Array<IntArray>): List<Int> {
        val result = mutableListOf<Int>()
        var top = 0
        var bottom = matrix.size - 1
        var left = 0
        var right = matrix[0].size - 1

        while (top <= bottom && left <= right) {
            // 1. Traverse Right along Top row
            for (c in left..right) {
                result.add(matrix[top][c])
            }
            top++

            // 2. Traverse Down along Right column
            for (r in top..bottom) {
                result.add(matrix[r][right])
            }
            right--

            // 3. Traverse Left along Bottom row (guard against duplicate row)
            if (top <= bottom) {
                for (c in right downTo left) {
                    result.add(matrix[bottom][c])
                }
                bottom--
            }

            // 4. Traverse Up along Left column (guard against duplicate col)
            if (left <= right) {
                for (r in bottom downTo top) {
                    result.add(matrix[r][left])
                }
                left++
            }
        }

        return result
    }
}""",
            'notes_kotlin': "Strict O(1) auxiliary memory. Guard checks `top <= bottom` and `left <= right` prevent single-row/col duplicates.",
            'code_python': """class Solution:
    # Optimal: Four Boundary Pointers
    # Time: O(M * N) - Single pass
    # Space: O(1) Auxiliary
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        result = []
        top, bottom = 0, len(matrix) - 1
        left, right = 0, len(matrix[0]) - 1

        while top <= bottom and left <= right:
            # Traverse Right
            for c in range(left, right + 1):
                result.append(matrix[top][c])
            top += 1

            # Traverse Down
            for r in range(top, bottom + 1):
                result.append(matrix[r][right])
            right -= 1

            # Traverse Left
            if top <= bottom:
                for c in range(right, left - 1, -1):
                    result.append(matrix[bottom][c])
                bottom -= 1

            # Traverse Up
            if left <= right:
                for r in range(bottom, top - 1, -1):
                    result.append(matrix[r][left])
                left += 1

        return result""",
            'notes_python': "The canonical Python interview template. Clean and bug-free.",
            'code_java': """class Solution {
    /**
     * Optimal: Four Boundary Pointers
     * Time: O(M * N) - Every element visited once
     * Space: O(1) Auxiliary - Constant pointer memory
     */
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        int top = 0;
        int bottom = matrix.length - 1;
        int left = 0;
        int right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            // Traverse Right
            for (int c = left; c <= right; c++) {
                result.add(matrix[top][c]);
            }
            top++;

            // Traverse Down
            for (int r = top; r <= bottom; r++) {
                result.add(matrix[r][right]);
            }
            right--;

            // Traverse Left
            if (top <= bottom) {
                for (int c = right; c >= left; c--) {
                    result.add(matrix[bottom][c]);
                }
                bottom--;
            }

            // Traverse Up
            if (left <= right) {
                for (int r = bottom; r >= top; r--) {
                    result.add(matrix[r][left]);
                }
                left++;
            }
        }

        return result;
    }
}""",
            'notes_java': "Production standard. Fast pointer updates with zero heap allocation."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun spiralOrder(matrix: Array<IntArray>): List<Int> {
        val res = mutableListOf<Int>()
        var t = 0; var b = matrix.size - 1
        var l = 0; var r = matrix[0].size - 1
        while (t <= b && l <= r) {
            for (c in l..r) res.add(matrix[t][c]); t++
            for (row in t..b) res.add(matrix[row][r]); r--
            if (t <= b) { for (c in r downTo l) res.add(matrix[b][c]); b-- }
            if (l <= r) { for (row in b downTo t) res.add(matrix[row][l]); l++ }
        }
        return res
    }
}""",
        'python_boilerplate': """class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        res = []
        t, b, l, r = 0, len(matrix) - 1, 0, len(matrix[0]) - 1
        while t <= b and l <= r:
            for c in range(l, r + 1): res.append(matrix[t][c])
            t += 1
            for row in range(t, b + 1): res.append(matrix[row][r])
            r -= 1
            if t <= b:
                for c in range(r, l - 1, -1): res.append(matrix[b][c])
                b -= 1
            if l <= r:
                for row in range(b, t - 1, -1): res.append(matrix[row][l])
                l += 1
        return res""",
        'java_boilerplate': """class Solution {
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> res = new ArrayList<>();
        int t = 0, b = matrix.length - 1, l = 0, r = matrix[0].length - 1;
        while (t <= b && l <= r) {
            for (int c = l; c <= r; c++) res.add(matrix[t][c]);
            t++;
            for (int row = t; row <= b; row++) res.add(matrix[row][r]);
            r--;
            if (t <= b) {
                for (int c = r; c >= l; c--) res.add(matrix[b][c]);
                b--;
            }
            if (l <= r) {
                for (int row = b; row >= t; row--) res.add(matrix[row][l]);
                l++;
            }
        }
        return res;
    }
}""",
        'js_test_runner': """
      const testCases = [
        { mat: [[1,2,3],[4,5,6],[7,8,9]], expected: [1,2,3,6,9,8,7,4,5] },
        { mat: [[1,2,3,4],[5,6,7,8],[9,10,11,12]], expected: [1,2,3,4,8,12,11,10,9,5,6,7] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runSpiral(mat) {
          let res = [];
          let t = 0, b = mat.length - 1, l = 0, r = mat[0].length - 1;
          while (t <= b && l <= r) {
            for (let c = l; c <= r; c++) res.push(mat[t][c]);
            t++;
            for (let row = t; row <= b; row++) res.push(mat[row][r]);
            r--;
            if (t <= b) {
              for (let c = r; c >= l; c--) res.push(mat[b][c]);
              b--;
            }
            if (l <= r) {
              for (let row = b; row >= t; row--) res.push(mat[row][l]);
              l++;
            }
          }
          return res;
        }

        const out = runSpiral(tc.mat);
        const passed = JSON.stringify(out) === JSON.stringify(tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: matrix=${JSON.stringify(tc.mat)}</span>
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
        'phase1': "I clarify: Can the matrix be non-square (e.g. 1x4 or 3x1)? Yes, M and N can differ. Can the matrix have only 1 element? Yes, return that single element.",
        'phase2': "For strategy: Instead of maintaining a visited matrix and direction vectors, we can use 4 boundary pointers: `top = 0`, `bottom = m - 1`, `left = 0`, `right = n - 1`. In a while loop `while (top <= bottom && left <= right)`, we sweep the 4 edges: Right along top row (`top++`), Down along right column (`right--`), Left along bottom row (`bottom--`), and Up along left column (`left++`). Crucially, we must guard the bottom and left sweeps with `if (top <= bottom)` and `if (left <= right)` to prevent duplicate traversals on single row/column matrices. Time is O(M * N) and auxiliary space is O(1).",
        'phase3': "I implement `spiralOrder`: initialize boundaries. In the loop: loop `c` from `left` to `right`, append `matrix[top][c]`, increment `top`. Loop `r` from `top` to `bottom`, append `matrix[r][right]`, decrement `right`. If `top <= bottom`, loop `c` from `right` down to `left`, append, decrement `bottom`. If `left <= right`, loop `r` from `bottom` down to `top`, append, increment `left`. Return `result`.",
        'phase4': "Dry run edge case with single row [[1, 2, 3]]: top=0, bottom=0, left=0, right=2. Top sweep adds 1, 2, 3, top becomes 1. Right sweep: top(1) > bottom(0), loop skipped. Check `top <= bottom`: 1 <= 0 is False, bottom sweep skipped! Check `left <= right`: 0 <= 1, but left sweep skipped. Loop ends. Output is [1, 2, 3] without duplicate scans.",
        'senior_followup': "Senior Scalability: In computer graphics, geospatial tiles, and image rendering pipelines, spiral traversals are used to progressively stream resolution from screen center outward (foveated rendering), prioritizing bandwidth for central pixels."
    },
    'quiz': [
        {
            'question': "Why are the checks `if (top <= bottom)` and `if (left <= right)` required before sweeping the bottom and left edges?",
            'options': [
                "Because for non-square matrices (such as 1x4 or 3x1), the prior increments of `top++` or `right--` can invert the boundaries mid-cycle; without the check, elements from the already-traversed row or column would be duplicated.",
                "To prevent integer arithmetic overflow.",
                "Because matrices can contain null references.",
                "To sort the output in descending order."
            ],
            'correct_index': 0,
            'explanation': "In a 1x4 matrix, the top sweep traverses all elements and executes `top++` (`top` becomes 1, while `bottom` is 0). Without checking `top <= bottom`, the bottom sweep would re-traverse the same row in reverse."
        },
        {
            'question': "What is the auxiliary space complexity of the 4-boundary pointer algorithm (excluding the output array)?",
            'options': [
                "O(1) (strictly 4 scalar integer pointers)",
                "O(M * N)",
                "O(min(M, N))",
                "O(M + N)"
            ],
            'correct_index': 0,
            'explanation': "The algorithm only allocates four integer variables (`top`, `bottom`, `left`, `right`) to track boundaries, requiring $O(1)$ auxiliary memory."
        }
    ]
}

p51 = {
    'num': 51,
    'lc': 200,
    'title': "Number of Islands",
    'category': "2D Grid Traversals & Graphs",
    'difficulty': "Medium",
    'lc_slug': "number-of-islands",
    'verbatim_statement': """<p>Given an <code>m x n</code> 2D binary grid <code>grid</code> which represents a map of <code>'1'</code>s (land) and <code>'0'</code>s (water), return <em>the number of islands</em>.</p><p>An <strong>island</strong> is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'grid = [\n  ["1","1","1","1","0"],\n  ["1","1","0","1","0"],\n  ["1","1","0","0","0"],\n  ["0","0","0","0","0"]\n]', 'output': '1'},
        {'title': "Example 2", 'input': 'grid = [\n  ["1","1","0","0","0"],\n  ["1","1","0","0","0"],\n  ["0","0","1","0","0"],\n  ["0","0","0","1","1"]\n]', 'output': '3'}
    ],
    'constraints': [
        "m == grid.length",
        "n == grid[i].length",
        "1 <= m, n <= 300",
        "grid[i][j] is '0' or '1'."
    ],
    'plain_english': "Scan every cell `(r, c)` in the grid. When an unvisited land cell `'1'` is found, increment the island counter and launch a flood-fill (DFS or BFS) to 'sink' the entire connected component by flipping all adjacent land cells from `'1'` to `'0'`.",
    'boundary_checks': {
        'min_base': "All water grid <code>[['0']]</code> &rarr; Returns 0 islands.",
        'best_exit': "All land grid <code>[['1','1'],['1','1']]</code> &rarr; Sunk in single DFS call, returns 1.",
        'worst_scan': "Checkerboard pattern <code>[['1','0'],['0','1']]</code> &rarr; Diagonal cells do NOT connect; returns 2 islands."
    },
    'mental_model': {
        'analogy_title': "The Torpedo Sinking Drill",
        'analogy_text': "Think of looking down at an archipelago of land masses in the ocean. When your satellite spots a patch of land `'1'`, you launch a torpedo (island count + 1!). The torpedo detonates a flood-fill wave: every touching patch of connected land sinks into the ocean (`'0'`). You continue sweeping the ocean. If you spot another `'1'`, it must belong to a completely separate, disconnected island.",
        'aha_moment': "Mutating the grid in-place (`grid[r][c] = '0'`) serves as its own visited set! This eliminates the need for an auxiliary `visited[m][n]` boolean matrix and saves O(M*N) memory.",
        'trigger': "Connected components on 2D grid, cluster counting, flood fill &rarr; Grid DFS / BFS with in-place cell mutation.",
        'sisters': [
            {'name': "Max Area of Island", 'lc': "LC #695"},
            {'name': "Rotting Oranges", 'lc': "LC #994"},
            {'name': "Surrounded Regions", 'lc': "LC #130"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(M * N) Time, O(M * N) Space",
        'naive_text': "Build a disjoint-set union (Union-Find) structure for all M*N cells, connecting adjacent '1's. Count distinct root parents.",
        'wasted_work': "Allocating parent and rank arrays of size M*N when a direct flood-fill visits every cell in one pass.",
        'optimal_complexity': "O(M * N) Time, O(M * N) Worst-Case Stack Space",
        'optimal_text': "Recursive DFS sink flood-fill mutating '1' to '0'. Constant work per cell.",
        'core_invariant': "After completing the flood-fill for a cell `(r, c)`, all land cells in its connected component are turned into water `'0'`.",
        'table_rows': [
            {'name': "1. Disjoint Set Union (Union-Find)", 'time': "O(M * N * alpha(MN))", 'space': "O(M * N)", 'note': "Good for dynamic incremental edges, but high constant memory."},
            {'name': "2. Queue BFS with Visited Set", 'time': "O(M * N)", 'space': "O(min(M, N))", 'note': "Queue memory bounded by grid diagonal width."},
            {'name': "★ 3. In-Place DFS Flood Fill", 'time': "O(M * N)", 'space': "O(M * N) Call Stack", 'note': "Zero auxiliary data structures; sinks cells directly."}
        ]
    },
    'trace_table': {
        'input_desc': 'grid = [["1","1","0"],["1","0","0"],["0","0","1"]]',
        'columns': ["Grid Scan (r, c)", "Cell Value", "Action Taken", "Cells Sunk in Flood Fill", "Total Islands Count"],
        'rows': [
            ["(0, 0)", "'1'", "Island detected! Increment count", "Sinks (0,0), (0,1), (1,0) to '0'", "1"],
            ["(0, 1)", "'0' (sunk)", "Water &rarr; skip", "-", "1"],
            ["(0, 2)", "'0'", "Water &rarr; skip", "-", "1"],
            ["(1, 0)", "'0' (sunk)", "Water &rarr; skip", "-", "1"],
            ["(1, 1)", "'0'", "Water &rarr; skip", "-", "1"],
            ["(2, 2)", "'1'", "Island detected! Increment count", "Sinks (2,2) to '0'", "2"]
        ],
        'takeaway': "Total islands = 2. Sunk cells prevent redundant visits."
    },
    'stepper': {
        'title': "Island Sinking DFS Stepper",
        'default_input': '[["1","1","0"],["1","0","0"],["0","0","1"]]',
        'presets': [
            {'label': "Two Islands", 'onclick': 'loadPreset([["1","1","0"],["1","0","0"],["0","0","1"]])'},
            {'label': "Single Large Island", 'onclick': 'loadPreset([["1","1","1"],["1","1","0"],["0","0","0"]])'},
            {'label': "Checkerboard (Diagonal)", 'onclick': 'loadPreset([["1","0","1"],["0","1","0"],["1","0","1"]])'}
        ],
        'metrics': [
            {'id': "metric-cell", 'label': "Scanning Cell", 'color': "text-amber-400", 'initial': "(0,0)"},
            {'id': "metric-val", 'label': "Cell State", 'color': "text-cyan-400", 'initial': "-"},
            {'id': "metric-islands", 'label': "Islands Count", 'color': "text-emerald-400", 'initial': "0"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="island-stage" class="w-full flex items-center justify-center p-4 min-h-[160px]"></div>',
        'initial_narration': 'Click "Next Step" to trace island detection and sinking.',
        'pseudocode_lines': [
            "var count = 0",
            "for (r in grid.indices) for (c in grid[0].indices) {",
            "    if (grid[r][c] == '1') {",
            "        count++",
            "        dfsSink(grid, r, c)",
            "    }",
            "}",
            "fun dfsSink(grid, r, c) {",
            "    if (r !in grid.indices || c !in grid[0].indices || grid[r][c] != '1') return",
            "    grid[r][c] = '0'",
            "    dfsSink(grid, r+1, c); dfsSink(grid, r-1, c)",
            "    dfsSink(grid, r, c+1); dfsSink(grid, r, c-1)",
            "}"
        ],
        'js_code': """
    let rawGrid = [["1","1","0"],["1","0","0"],["0","0","1"]];

    function buildSteps(matrix) {
      steps = [];
      let g = matrix.map(r => [...r]);
      let m = g.length, n = g[0].length;
      let count = 0;

      for (let r = 0; r < m; r++) {
        for (let c = 0; c < n; c++) {
          if (g[r][c] === '1') {
            count++;
            steps.push({
              cell: `(${r}, ${c})`, val: "LAND ('1')",
              islands: count, action: `New Island Found! Count = ${count}`,
              grid: JSON.parse(JSON.stringify(g)),
              activeR: r, activeC: c, line: 4,
              narration: `Found unvisited land at (${r}, ${c}). Incremented island count to ${count}. Launching DFS sink.`
            });

            // Sink component
            function sink(cr, cc) {
              if (cr < 0 || cr >= m || cc < 0 || cc >= n || g[cr][cc] !== '1') return;
              g[cr][cc] = '0';
              steps.push({
                cell: `(${cr}, ${cc})`, val: "SINKING ('0')",
                islands: count, action: `Sunk cell (${cr}, ${cc})`,
                grid: JSON.parse(JSON.stringify(g)),
                activeR: cr, activeC: cc, line: 10,
                narration: `Sunk land cell (${cr}, ${cc}) to water '0'.`
              });
              sink(cr + 1, cc);
              sink(cr - 1, cc);
              sink(cr, cc + 1);
              sink(cr, cc - 1);
            }

            sink(r, c);
          } else {
            steps.push({
              cell: `(${r}, ${c})`, val: "WATER ('0')",
              islands: count, action: "Skip Water",
              grid: JSON.parse(JSON.stringify(g)),
              activeR: r, activeC: c, line: 3,
              narration: `Cell (${r}, ${c}) is water. Skipping.`
            });
          }
        }
      }

      steps.push({
        cell: "Done", val: "-", islands: count,
        action: "Grid Scan Complete",
        grid: JSON.parse(JSON.stringify(g)),
        activeR: -1, activeC: -1, line: 1,
        narration: `Scan complete! Total islands detected: ${count}.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = JSON.stringify(arr);
      rawGrid = arr;
      stepperReset();
      buildSteps(rawGrid);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const parsed = JSON.parse(val);
        if (Array.isArray(parsed) && parsed.length > 0) {
          rawGrid = parsed;
          stepperReset();
          buildSteps(rawGrid);
          renderStepper();
        }
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-cell').textContent = step.cell;
      document.getElementById('metric-val').textContent = step.val;
      document.getElementById('metric-islands').textContent = step.islands;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('island-stage');
      stage.innerHTML = '';
      if (!step.grid) return;

      const m = step.grid.length;
      const n = step.grid[0].length;
      const table = document.createElement('div');
      table.className = 'grid gap-1.5 p-3 bg-slate-950 border border-slate-800 rounded-xl';
      table.style.gridTemplateColumns = `repeat(${n}, minmax(0, 1fr))`;

      for (let r = 0; r < m; r++) {
        for (let c = 0; c < n; c++) {
          const cell = document.createElement('div');
          const isCurr = (r === step.activeR && c === step.activeC);
          const isLand = (step.grid[r][c] === '1');

          cell.className = `w-11 h-11 flex flex-col items-center justify-center rounded-lg font-mono text-xs font-bold border transition-colors ${
            isCurr ? 'bg-amber-500/30 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' :
            isLand ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/50' :
            'bg-slate-900 text-slate-500 border-slate-800'
          }`;
          cell.innerHTML = `
            <span>${step.grid[r][c]}</span>
            <span class="text-[8px] font-normal uppercase">${isLand ? 'LAND' : 'WATER'}</span>
          `;
          table.appendChild(cell);
        }
      }
      stage.appendChild(table);
"""
    },
    'approaches': [
        {
            'name': "Breadth-First Search (Queue BFS)",
            'desc': "Use a Queue to flood-fill each island iteratively. Sinks cells upon enqueuing to prevent duplicate queue entries.",
            'time': "O(M * N)",
            'space': "O(min(M, N))",
            'code_kotlin': """class Solution {
    fun numIslands(grid: Array<CharArray>): Int {
        var count = 0
        val m = grid.size
        val n = grid[0].size
        val queue = ArrayDeque<IntArray>()
        val dirs = arrayOf(intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(0, -1), intArrayOf(-1, 0))

        for (r in 0 until m) {
            for (c in 0 until n) {
                if (grid[r][c] == '1') {
                    count++
                    grid[r][c] = '0'
                    queue.add(intArrayOf(r, c))

                    while (queue.isNotEmpty()) {
                        val (currR, currC) = queue.removeFirst()
                        for (d in dirs) {
                            val nr = currR + d[0]
                            val nc = currC + d[1]
                            if (nr in 0 until m && nc in 0 until n && grid[nr][nc] == '1') {
                                grid[nr][nc] = '0'
                                queue.add(intArrayOf(nr, nc))
                            }
                        }
                    }
                }
            }
        }
        return count
    }
}""",
            'notes_kotlin': "Queue BFS. Marks `grid[nr][nc] = '0'` immediately upon enqueuing to prevent duplicate processing.",
            'code_python': """class Solution:
    def numIslands(self, grid: List[List[str]]) -> int:
        m, n = len(grid), len(grid[0])
        count = 0
        queue = collections.deque()
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]

        for r in range(m):
            for c in range(n):
                if grid[r][c] == '1':
                    count += 1
                    grid[r][c] = '0'
                    queue.append((r, c))
                    while queue:
                        cr, cc = queue.popleft()
                        for dr, dc in dirs:
                            nr, nc = cr + dr, cc + dc
                            if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == '1':
                                grid[nr][nc] = '0'
                                queue.append((nr, nc))
        return count""",
            'notes_python': "Iterative BFS avoids recursion limit errors on large 300x300 grids.",
            'code_java': """class Solution {
    public int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        int count = 0;
        Queue<int[]> queue = new ArrayDeque<>();
        int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}};

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    grid[r][c] = '0';
                    queue.offer(new int[]{r, c});
                    while (!queue.isEmpty()) {
                        int[] curr = queue.poll();
                        for (int[] d : dirs) {
                            int nr = curr[0] + d[0], nc = curr[1] + d[1];
                            if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] == '1') {
                                grid[nr][nc] = '0';
                                queue.offer(new int[]{nr, nc});
                            }
                        }
                    }
                }
            }
        }
        return count;
    }
}""",
            'notes_java': "Safe against stack overflow; queue holds at most min(M, N) elements along diagonal."
        },
        {
            'name': "Disjoint Set Union (Union-Find)",
            'desc': "Initialize DSU where each '1' is its own set. Union adjacent '1's. Return total number of disjoint sets.",
            'time': "O(M * N * alpha(MN))",
            'space': "O(M * N)",
            'code_kotlin': """class Solution {
    fun numIslands(grid: Array<CharArray>): Int {
        val m = grid.size
        val n = grid[0].size
        val parent = IntArray(m * n) { it }
        var count = 0

        for (r in 0 until m) {
            for (c in 0 until n) {
                if (grid[r][c] == '1') count++
            }
        }

        fun find(i: Int): Int {
            var root = i
            while (root != parent[root]) root = parent[root]
            var curr = i
            while (curr != root) {
                val nxt = parent[curr]
                parent[curr] = root
                curr = nxt
            }
            return root
        }

        fun union(i: Int, j: Int) {
            val r1 = find(i)
            val r2 = find(j)
            if (r1 != r2) {
                parent[r1] = r2
                count--
            }
        }

        for (r in 0 until m) {
            for (c in 0 until n) {
                if (grid[r][c] == '1') {
                    val id = r * n + c
                    if (r + 1 < m && grid[r + 1][c] == '1') union(id, (r + 1) * n + c)
                    if (c + 1 < n && grid[r][c + 1] == '1') union(id, r * n + c + 1)
                }
            }
        }
        return count
    }
}""",
            'notes_kotlin': "Union-Find with path compression. Excellent if islands are added dynamically over time.",
            'code_python': """class Solution:
    def numIslands(self, grid: List[List[str]]) -> int:
        m, n = len(grid), len(grid[0])
        parent = [i for i in range(m * n)]
        count = sum(1 for r in range(m) for c in range(n) if grid[r][c] == '1')

        def find(i):
            if parent[i] == i: return i
            parent[i] = find(parent[i])
            return parent[i]

        def union(i, j):
            nonlocal count
            ri, rj = find(i), find(j)
            if ri != rj:
                parent[ri] = rj
                count -= 1

        for r in range(m):
            for c in range(n):
                if grid[r][c] == '1':
                    idx = r * n + c
                    if r + 1 < m and grid[r + 1][c] == '1': union(idx, (r + 1) * n + c)
                    if c + 1 < n and grid[r][c + 1] == '1': union(idx, r * n + c + 1)
        return count""",
            'notes_python': "Union-Find solution. Computes connected components on undirected grid graph.",
            'code_java': """class Solution {
    public int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] parent = new int[m * n];
        for (int i = 0; i < parent.length; i++) parent[i] = i;
        int count = 0;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == '1') count++;
            }
        }
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == '1') {
                    int id = r * n + c;
                    if (r + 1 < m && grid[r + 1][c] == '1') count -= union(parent, id, (r + 1) * n + c);
                    if (c + 1 < n && grid[r][c + 1] == '1') count -= union(parent, id, r * n + c + 1);
                }
            }
        }
        return count;
    }

    private int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return parent[i] = find(parent, parent[i]);
    }

    private int union(int[] parent, int i, int j) {
        int rootI = find(parent, i), rootJ = find(parent, j);
        if (rootI != rootJ) {
            parent[rootI] = rootJ;
            return 1;
        }
        return 0;
    }
}""",
            'notes_java': "DSU takes O(M * N) space for parent array."
        },
        {
            'name': "In-Place DFS Flood Fill Sinking (Optimal Primary)",
            'desc': "Scan grid cells. When '1' is found, increment count and launch recursive DFS to sink all connected '1's to '0'.",
            'time': "O(M * N)",
            'space': "O(M * N) Worst-Case Stack Space",
            'code_kotlin': """class Solution {
    /**
     * Optimal: In-Place DFS Flood Fill Sinking
     * Time: O(M * N) - Every cell visited a constant number of times
     * Space: O(M * N) - Worst-case call stack on all-land grid
     */
    fun numIslands(grid: Array<CharArray>): Int {
        var count = 0
        for (r in grid.indices) {
            for (c in grid[0].indices) {
                if (grid[r][c] == '1') {
                    count++
                    dfsSink(grid, r, c)
                }
            }
        }
        return count
    }

    private fun dfsSink(grid: Array<CharArray>, r: Int, c: Int) {
        // Base case: out of bounds or water
        if (r !in grid.indices || c !in grid[0].indices || grid[r][c] != '1') {
            return
        }

        // Sink cell in-place (serves as visited marker)
        grid[r][c] = '0'

        // Explore 4 cardinal neighbors
        dfsSink(grid, r + 1, c)
        dfsSink(grid, r - 1, c)
        dfsSink(grid, r, c + 1)
        dfsSink(grid, r, c - 1)
    }
}""",
            'notes_kotlin': "In-place sinking eliminates auxiliary memory allocations. Cleanest pattern in interviews.",
            'code_python': """class Solution:
    # Optimal: In-Place DFS Flood Fill
    # Time: O(M * N)
    # Space: O(M * N) Worst-case recursion stack
    def numIslands(self, grid: List[List[str]]) -> int:
        if not grid:
            return 0

        m, n = len(grid), len(grid[0])
        count = 0

        def dfs(r: int, c: int):
            if r < 0 or r >= m or c < 0 or c >= n or grid[r][c] != '1':
                return
            grid[r][c] = '0'  # Sink land to water
            dfs(r + 1, c)
            dfs(r - 1, c)
            dfs(r, c + 1)
            dfs(r, c - 1)

        for r in range(m):
            for c in range(n):
                if grid[r][c] == '1':
                    count += 1
                    dfs(r, c)

        return count""",
            'notes_python': "Canonical Python solution. Modifying `grid[r][c] = '0'` replaces visited hash sets.",
            'code_java': """class Solution {
    /**
     * Optimal: In-Place DFS Flood Fill
     * Time: O(M * N) - Linear scan of matrix
     * Space: O(M * N) - Maximum recursion stack depth
     */
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int count = 0;
        int m = grid.length;
        int n = grid[0].length;

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfsSink(grid, r, c);
                }
            }
        }

        return count;
    }

    private void dfsSink(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') {
            return;
        }

        grid[r][c] = '0'; // Sink cell

        dfsSink(grid, r + 1, c);
        dfsSink(grid, r - 1, c);
        dfsSink(grid, r, c + 1);
        dfsSink(grid, r, c - 1);
    }
}""",
            'notes_java': "Zero extra objects. Fast cache locality with direct primitive character matrix updates."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun numIslands(grid: Array<CharArray>): Int {
        var count = 0
        fun sink(r: Int, c: Int) {
            if (r !in grid.indices || c !in grid[0].indices || grid[r][c] != '1') return
            grid[r][c] = '0'
            sink(r + 1, c); sink(r - 1, c); sink(r, c + 1); sink(r, c - 1)
        }
        for (r in grid.indices) {
            for (c in grid[0].indices) {
                if (grid[r][c] == '1') { count++; sink(r, c) }
            }
        }
        return count
    }
}""",
        'python_boilerplate': """class Solution:
    def numIslands(self, grid: List[List[str]]) -> int:
        count = 0
        m, n = len(grid), len(grid[0])
        def sink(r, c):
            if r < 0 or r >= m or c < 0 or c >= n or grid[r][c] != '1': return
            grid[r][c] = '0'
            sink(r + 1, c); sink(r - 1, c); sink(r, c + 1); sink(r, c - 1)
        for r in range(m):
            for c in range(n):
                if grid[r][c] == '1':
                    count += 1
                    sink(r, c)
        return count""",
        'java_boilerplate': """class Solution {
    public int numIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    sink(grid, r, c);
                }
            }
        }
        return count;
    }
    private void sink(char[][] g, int r, int c) {
        if (r < 0 || r >= g.length || c < 0 || c >= g[0].length || g[r][c] != '1') return;
        g[r][c] = '0';
        sink(g, r + 1, c); sink(g, r - 1, c); sink(g, r, c + 1); sink(g, r, c - 1);
    }
}""",
        'js_test_runner': """
      const testCases = [
        { grid: [["1","1","1","1","0"],["1","1","0","1","0"],["1","1","0","0","0"],["0","0","0","0","0"]], expected: 1 },
        { grid: [["1","1","0","0","0"],["1","1","0","0","0"],["0","0","1","0","0"],["0","0","0","1","1"]], expected: 3 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runNumIslands(mat) {
          let g = mat.map(r => [...r]);
          let count = 0, m = g.length, n = g[0].length;
          function sink(r, c) {
            if (r < 0 || r >= m || c < 0 || c >= n || g[r][c] !== '1') return;
            g[r][c] = '0';
            sink(r + 1, c); sink(r - 1, c); sink(r, c + 1); sink(r, c - 1);
          }
          for (let r = 0; r < m; r++) {
            for (let c = 0; c < n; c++) {
              if (g[r][c] === '1') {
                count++;
                sink(r, c);
              }
            }
          }
          return count;
        }

        const out = runNumIslands(tc.grid);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: expected ${tc.expected}</span>
          <span>${passed ? '&#10004; PASSED (' + out + ' islands)' : '&#10008; FAILED (got ' + out + ')'}</span>
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
        'phase1': "I clarify: Do diagonal connections count as part of the same island? No, the problem specifies adjacent lands connect horizontally or vertically only. Are we allowed to modify the input grid in place? Yes, mutating cells from '1' to '0' avoids allocating an extra M*N visited array.",
        'phase2': "For strategy: We scan the matrix cell by cell. When we encounter an unvisited land cell `'1'`, we increment our island count by 1 and trigger a recursive DFS flood fill. The DFS immediately mutates `grid[r][c] = '0'` (sinking the land) and recursively visits all 4 cardinal neighbors. By the time DFS returns, the entire connected island has sunk, ensuring no part of it is double-counted. Time is O(M * N) and space is O(M * N) worst-case call stack.",
        'phase3': "I implement `numIslands`: loop `r` over `0 until m`, `c` over `0 until n`. If `grid[r][c] == '1'`, increment `count++` and call `dfsSink(grid, r, c)`. In `dfsSink`: base check `if (r !in grid.indices || c !in grid[0].indices || grid[r][c] != '1') return`. Set `grid[r][c] = '0'`. Recurse on `(r+1,c)`, `(r-1,c)`, `(r,c+1)`, `(r,c-1)`.",
        'phase4': "Dry run: grid with two islands separated diagonally [[1, 0], [0, 1]]. (0,0) is '1' &rarr; count=1, sinks (0,0). Neighbors (1,0) and (0,1) are '0', stop. Loop advances to (1,1), which is '1' &rarr; count=2, sinks (1,1). Output is 2 islands. Correct.",
        'senior_followup': "Senior Scalability: For massive grids (e.g. 100,000 x 100,000 satellite images), a single machine cannot hold the grid or stack frames. We tile the image into subgrids, run local island counting per worker, and merge border edges across tile seams using distributed Disjoint Set Union (Union-Find)."
    },
    'quiz': [
        {
            'question': "Why is mutating `grid[r][c] = '0'` in place during DFS beneficial?",
            'options': [
                "It eliminates the need to allocate an auxiliary `visited[m][n]` matrix, reducing extra memory overhead.",
                "It changes the time complexity from O(M*N) to O(1).",
                "Because Java prohibits boolean arrays in graph traversals.",
                "It enables diagonal traversal."
            ],
            'correct_index': 0,
            'explanation': "Flipping land to water immediately marks the cell as visited, eliminating the memory and lookup overhead of an external visited set."
        },
        {
            'question': "In BFS on a grid, when MUST a neighbor cell `(nr, nc)` be marked visited / sunk?",
            'options': [
                "Immediately when enqueuing `(nr, nc)` into the FIFO queue.",
                "Only after popping `(nr, nc)` from the queue.",
                "When all 4 neighbors have been checked.",
                "At the end of the entire algorithm."
            ],
            'correct_index': 0,
            'explanation': "If you wait until popping to mark visited, multiple already-queued neighbors could enqueue the same cell repeatedly, causing an exponential explosion in queue size and TLE/MLE."
        }
    ]
}
