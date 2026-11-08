import sys
sys.path.append('scripts')

p52 = {
    'num': 52,
    'lc': 994,
    'title': "Rotting Oranges",
    'category': "2D Grid Traversals & Graphs",
    'difficulty': "Medium",
    'lc_slug': "rotting-oranges",
    'verbatim_statement': """<p>You are given an <code>m x n</code> <code>grid</code> where each cell can have one of three values:</p>
<ul>
  <li><code>0</code> representing an empty cell,</li>
  <li><code>1</code> representing a fresh orange, or</li>
  <li><code>2</code> representing a rotten orange.</li>
</ul>
<p>Every minute, any fresh orange that is <strong>4-directionally adjacent</strong> to a rotten orange becomes rotten.</p>
<p>Return <em>the minimum number of minutes that must elapse until no cell has a fresh orange</em>. If <em>this is impossible, return</em> <code>-1</code>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'grid = [[2,1,1],[1,1,0],[0,1,1]]', 'output': '4'},
        {'title': "Example 2", 'input': 'grid = [[2,1,1],[0,1,1],[1,0,1]]', 'output': '-1\nExplanation: The orange in the bottom left corner (row 2, column 0) is never rotten, because rotting only happens 4-directionally.'},
        {'title': "Example 3", 'input': 'grid = [[0,2]]', 'output': '0\nExplanation: Since there are already no fresh oranges at minute 0, the answer is just 0.'}
    ],
    'constraints': [
        "m == grid.length",
        "n == grid[i].length",
        "1 <= m, n <= 10",
        "grid[i][j] is 0, 1, or 2."
    ],
    'plain_english': "All rotten oranges spread rot simultaneously minute by minute. This is a multi-source Breadth-First Search (BFS). Initialize a queue with all starting rotten oranges and count all fresh oranges. Each minute, snapshot the queue size, pop all current rotten oranges, turn their 4-way adjacent fresh neighbors to rotten (2), push them to the queue, and decrement `freshCount`. If `freshCount == 0`, return minutes elapsed; otherwise return -1.",
    'boundary_checks': {
        'min_base': "No fresh oranges at all <code>freshCount == 0</code> &rarr; Immediately returns 0 minutes.",
        'best_exit': "All oranges already rotten &rarr; Returns 0.",
        'worst_scan': "Isolated fresh orange surrounded by 0s &rarr; `freshCount > 0` after queue drains &rarr; returns -1."
    },
    'mental_model': {
        'analogy_title': "Multi-Source Forest Fire Wavefront",
        'analogy_text': "Imagine a forest where lightning strikes multiple trees simultaneously at minute 0 (multi-source rotten oranges). Every minute, the fire spreads simultaneously from all active fire fronts to adjacent unburnt trees (`1 &rarr; 2`). The fire burns in concentric expanding rings. Once all fire fronts burn out, if any unburnt tree remains standing, the fire failed to reach it (-1). Otherwise, the time of the last wave is the total duration.",
        'aha_moment': "DFS CANNOT solve this because different rotten sources spread simultaneously! BFS naturally computes the shortest time for contamination because a queue processes time steps in strict chronological waves.",
        'trigger': "Simultaneous multi-source spreading, minimum time to infect/reach all nodes in grid &rarr; Multi-Source Queue BFS with `size` snapshot.",
        'sisters': [
            {'name': "01 Matrix", 'lc': "LC #542"},
            {'name': "Shortest Path in Binary Matrix", 'lc': "LC #1091"},
            {'name': "Number of Islands", 'lc': "LC #200"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O((M * N)^2) Time, O(M * N) Space",
        'naive_text': "Simulation: at each minute, scan the whole grid to find all 2s, create a new grid copying rots, and repeat until no changes occur.",
        'wasted_work': "Re-scanning every cell in the matrix on every single minute.",
        'optimal_complexity': "O(M * N) Time, O(M * N) Space",
        'optimal_text': "Multi-Source BFS. Every cell is enqueued at most once. Time is strictly linear with respect to grid cells.",
        'core_invariant': "At minute t, the queue contains exclusively the oranges that turned rotten exactly at minute t.",
        'table_rows': [
            {'name': "1. Full-Grid Minute Simulation", 'time': "O((M * N)^2)", 'space': "O(M * N)", 'note': "Scans entire matrix every minute; quadratic time."},
            {'name': "2. Multi-Source BFS with Time Matrix", 'time': "O(M * N)", 'space': "O(M * N)", 'note': "Allocates separate `time[M][N]` array."},
            {'name': "★ 3. Multi-Source BFS + Fresh Counter", 'time': "O(M * N)", 'space': "O(M * N)", 'note': "In-place grid rot mutation with instant `fresh == 0` check."}
        ]
    },
    'trace_table': {
        'input_desc': 'grid = [[2,1,1],[1,1,0],[0,1,1]] (Fresh = 6)',
        'columns': ["Minute", "Active Queue Snapshot", "Fresh Oranges Rotted", "Remaining Fresh Count", "Next Queue State"],
        'rows': [
            ["0", "[(0,0)] (size 1)", "None (Start)", "6", "[(0,1), (1,0)]"],
            ["1", "[(0,1), (1,0)] (size 2)", "(0,2), (1,1)", "4", "[(0,2), (1,1)]"],
            ["2", "[(0,2), (1,1)] (size 2)", "(2,1) (via (1,1))", "3", "[(2,1)]"],
            ["3", "[(2,1)] (size 1)", "(2,2)", "2", "[(2,2)]"],
            ["4", "[(2,2)] (size 1)", "None (all neighbors 0 or 2)", "0 (All fresh rotted!)", "[] (Queue empty)"]
        ],
        'takeaway': "Total minutes = 4. `freshCount == 0` confirms 100% rot completion."
    },
    'stepper': {
        'title': "Multi-Source BFS Rot Spreading Stepper",
        'default_input': '[[2,1,1],[1,1,0],[0,1,1]]',
        'presets': [
            {'label': "Example 1 (4 minutes)", 'onclick': 'loadPreset([[2,1,1],[1,1,0],[0,1,1]])'},
            {'label': "Example 2 (Unreachable -1)", 'onclick': 'loadPreset([[2,1,1],[0,1,1],[1,0,1]])'},
            {'label': "Already 0 Fresh", 'onclick': 'loadPreset([[0,2]])'}
        ],
        'metrics': [
            {'id': "metric-min", 'label': "Minutes Elapsed", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-fresh", 'label': "Fresh Oranges Remaining", 'color': "text-cyan-400", 'initial': "0"},
            {'id': "metric-qsize", 'label': "Queue Size (Rot Front)", 'color': "text-emerald-400", 'initial': "0"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="orange-stage" class="w-full flex items-center justify-center p-4 min-h-[160px]"></div>',
        'initial_narration': 'Click "Next Step" to trace multi-source BFS wave propagation.',
        'pseudocode_lines': [
            "val queue = ArrayDeque<IntArray>()",
            "var fresh = 0; var minutes = 0",
            "for (r in grid.indices) for (c in grid[0].indices) {",
            "    if (grid[r][c] == 2) queue.add(intArrayOf(r, c))",
            "    else if (grid[r][c] == 1) fresh++",
            "}",
            "while (queue.isNotEmpty() && fresh > 0) {",
            "    val size = queue.size; minutes++",
            "    repeat(size) {",
            "        val (r, c) = queue.removeFirst()",
            "        for ((dr, dc) in dirs) if (isFresh(r+dr, c+dc)) { grid[...]=2; fresh--; queue.add(...) }",
            "    }",
            "}",
            "return if (fresh == 0) minutes else -1"
        ],
        'js_code': """
    let rawOranges = [[2,1,1],[1,1,0],[0,1,1]];

    function buildSteps(matrix) {
      steps = [];
      let g = matrix.map(r => [...r]);
      let m = g.length, n = g[0].length;
      let q = [];
      let fresh = 0;

      for (let r = 0; r < m; r++) {
        for (let c = 0; c < n; c++) {
          if (g[r][c] === 2) q.push([r, c]);
          else if (g[r][c] === 1) fresh++;
        }
      }

      steps.push({
        min: 0, fresh: fresh, qsize: q.length,
        action: "Init Multi-Source Queue",
        grid: JSON.parse(JSON.stringify(g)),
        line: 4,
        narration: `Enqueued ${q.length} rotten oranges at minute 0. Fresh oranges count = ${fresh}.`
      });

      let minutes = 0;
      let dirs = [[0,1],[1,0],[0,-1],[-1,0]];

      while (q.length > 0 && fresh > 0) {
        let size = q.length;
        minutes++;

        for (let i = 0; i < size; i++) {
          let [r, c] = q.shift();
          for (let [dr, dc] of dirs) {
            let nr = r + dr, nc = c + dc;
            if (nr >= 0 && nr < m && nc >= 0 && nc < n && g[nr][nc] === 1) {
              g[nr][nc] = 2;
              fresh--;
              q.push([nr, nc]);
            }
          }
        }

        steps.push({
          min: minutes, fresh: fresh, qsize: q.length,
          action: `Minute ${minutes} Wave`,
          grid: JSON.parse(JSON.stringify(g)),
          line: 8,
          narration: `Minute ${minutes}: Rot spread to adjacent fresh oranges. Remaining fresh = ${fresh}.`
        });
      }

      let res = (fresh === 0) ? minutes : -1;
      steps.push({
        min: res, fresh: fresh, qsize: 0,
        action: (res !== -1) ? "All Rotted!" : "Impossible (-1)",
        grid: JSON.parse(JSON.stringify(g)),
        line: 14,
        narration: (res !== -1)
          ? `Complete! All oranges rotted in ${minutes} minutes.`
          : `Queue empty but ${fresh} fresh oranges remained isolated. Returned -1.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = JSON.stringify(arr);
      rawOranges = arr;
      stepperReset();
      buildSteps(rawOranges);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const parsed = JSON.parse(val);
        if (Array.isArray(parsed) && parsed.length > 0) {
          rawOranges = parsed;
          stepperReset();
          buildSteps(rawOranges);
          renderStepper();
        }
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-min').textContent = step.min;
      document.getElementById('metric-fresh').textContent = step.fresh;
      document.getElementById('metric-qsize').textContent = step.qsize;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('orange-stage');
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
          const val = step.grid[r][c];

          cell.className = `w-12 h-12 flex flex-col items-center justify-center rounded-lg font-mono text-xs font-bold border ${
            val === 2 ? 'bg-rose-500/20 text-rose-300 border-rose-500/60 ring-1 ring-rose-500/40' :
            val === 1 ? 'bg-amber-500/20 text-amber-300 border-amber-500/60' :
            'bg-slate-900 text-slate-600 border-slate-800'
          }`;
          cell.innerHTML = `
            <span>${val === 2 ? '☣ 2' : val === 1 ? '🍊 1' : '· 0'}</span>
            <span class="text-[8px] font-normal uppercase">${val === 2 ? 'ROTTEN' : val === 1 ? 'FRESH' : 'EMPTY'}</span>
          `;
          table.appendChild(cell);
        }
      }
      stage.appendChild(table);
"""
    },
    'approaches': [
        {
            'name': "Minute-by-Minute Grid Simulation (Naive)",
            'desc': "At each minute, iterate over the whole grid to find 2s, create a grid copy, and infect neighbors. Repeat until no new rots.",
            'time': "O((M * N)^2)",
            'space': "O(M * N)",
            'code_kotlin': """class Solution {
    fun orangesRotting(grid: Array<IntArray>): Int {
        val m = grid.size
        val n = grid[0].size
        var minutes = 0
        while (true) {
            val toRot = mutableListOf<Pair<Int, Int>>()
            for (r in 0 until m) {
                for (c in 0 until n) {
                    if (grid[r][c] == 2) {
                        for (d in arrayOf(intArrayOf(0,1), intArrayOf(1,0), intArrayOf(0,-1), intArrayOf(-1,0))) {
                            val nr = r + d[0]; val nc = c + d[1]
                            if (nr in 0 until m && nc in 0 until n && grid[nr][nc] == 1) {
                                toRot.add(Pair(nr, nc))
                            }
                        }
                    }
                }
            }
            if (toRot.isEmpty()) break
            for (p in toRot) grid[p.first][p.second] = 2
            minutes++
        }
        for (r in 0 until m) for (c in 0 until n) if (grid[r][c] == 1) return -1
        return minutes
    }
}""",
            'notes_kotlin': "Repeats full matrix scan every minute. Takes quadratic time.",
            'code_python': """class Solution:
    def orangesRotting(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        minutes = 0
        while True:
            to_rot = []
            for r in range(m):
                for c in range(n):
                    if grid[r][c] == 2:
                        for dr, dc in [(0,1),(1,0),(0,-1),(-1,0)]:
                            nr, nc = r + dr, c + dc
                            if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 1:
                                to_rot.append((nr, nc))
            if not to_rot:
                break
            for r, c in to_rot:
                grid[r][c] = 2
            minutes += 1
        return -1 if any(1 in row for row in grid) else minutes""",
            'notes_python': "Simulation approach. Inefficient on large grids.",
            'code_java': """class Solution {
    public int orangesRotting(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int minutes = 0;
        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};
        while (true) {
            List<int[]> toRot = new ArrayList<>();
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < n; c++) {
                    if (grid[r][c] == 2) {
                        for (int[] d : dirs) {
                            int nr = r + d[0], nc = c + d[1];
                            if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] == 1) {
                                toRot.add(new int[]{nr, nc});
                            }
                        }
                    }
                }
            }
            if (toRot.isEmpty()) break;
            for (int[] p : toRot) grid[p[0]][p[1]] = 2;
            minutes++;
        }
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) return -1;
            }
        }
        return minutes;
    }
}""",
            'notes_java': "Wasteful duplicate scans of non-changing cells."
        },
        {
            'name': "Multi-Source BFS with Separate Time Matrix",
            'desc': "Maintain a `dist[M][N]` array initialized to infinity. Multi-source BFS relaxes distance.",
            'time': "O(M * N)",
            'space': "O(M * N)",
            'code_kotlin': """class Solution {
    fun orangesRotting(grid: Array<IntArray>): Int {
        val m = grid.size
        val n = grid[0].size
        val dist = Array(m) { IntArray(n) { -1 } }
        val queue = ArrayDeque<IntArray>()
        for (r in 0 until m) for (c in 0 until n) {
            if (grid[r][c] == 2) {
                dist[r][c] = 0
                queue.add(intArrayOf(r, c))
            }
        }
        val dirs = arrayOf(intArrayOf(0,1), intArrayOf(1,0), intArrayOf(0,-1), intArrayOf(-1,0))
        var maxTime = 0
        while (queue.isNotEmpty()) {
            val (r, c) = queue.removeFirst()
            for (d in dirs) {
                val nr = r + d[0]; val nc = c + d[1]
                if (nr in 0 until m && nc in 0 until n && grid[nr][nc] == 1 && dist[nr][nc] == -1) {
                    dist[nr][nc] = dist[r][c] + 1
                    maxTime = maxOf(maxTime, dist[nr][nc])
                    queue.add(intArrayOf(nr, nc))
                }
            }
        }
        for (r in 0 until m) for (c in 0 until n) {
            if (grid[r][c] == 1 && dist[r][c] == -1) return -1
        }
        return maxTime
    }
}""",
            'notes_kotlin': "Uses distance matrix. Avoids mutating the original grid input.",
            'code_python': """class Solution:
    def orangesRotting(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        dist = [[-1] * n for _ in range(m)]
        queue = collections.deque()
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 2:
                    dist[r][c] = 0
                    queue.append((r, c))
        max_time = 0
        while queue:
            r, c = queue.popleft()
            for dr, dc in [(0,1),(1,0),(0,-1),(-1,0)]:
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 1 and dist[nr][nc] == -1:
                    dist[nr][nc] = dist[r][c] + 1
                    max_time = max(max_time, dist[nr][nc])
                    queue.append((nr, nc))
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 1 and dist[r][c] == -1:
                    return -1
        return max_time""",
            'notes_python': "Clean distance map approach. Non-destructive.",
            'code_java': """class Solution {
    public int orangesRotting(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dist = new int[m][n];
        for (int[] row : dist) Arrays.fill(row, -1);
        Queue<int[]> queue = new ArrayDeque<>();

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 2) {
                    dist[r][c] = 0;
                    queue.offer(new int[]{r, c});
                }
            }
        }

        int maxTime = 0;
        int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1];
            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] == 1 && dist[nr][nc] == -1) {
                    dist[nr][nc] = dist[r][c] + 1;
                    maxTime = Math.max(maxTime, dist[nr][nc]);
                    queue.offer(new int[]{nr, nc});
                }
            }
        }

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1 && dist[r][c] == -1) return -1;
            }
        }
        return maxTime;
    }
}""",
            'notes_java': "Separate distance matrix tracks minimum arrival minutes without mutating the input grid."
        },
        {
            'name': "Multi-Source BFS + Fresh Counter (Optimal Primary)",
            'desc': "Initialize queue with all 2s and tally fresh oranges count. Snapshot queue.size per minute. Mutate 1 -> 2.",
            'time': "O(M * N)",
            'space': "O(M * N)",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Multi-Source Queue BFS with Fresh Orange Counter
     * Time: O(M * N) - Every cell is processed at most once
     * Space: O(M * N) - Queue size bounded by grid dimensions
     */
    fun orangesRotting(grid: Array<IntArray>): Int {
        val m = grid.size
        val n = grid[0].size
        val queue = ArrayDeque<IntArray>()
        var freshCount = 0

        // 1. Collect all initial rotten oranges and count fresh oranges
        for (r in 0 until m) {
            for (c in 0 until n) {
                if (grid[r][c] == 2) {
                    queue.add(intArrayOf(r, c))
                } else if (grid[r][c] == 1) {
                    freshCount++
                }
            }
        }

        // If no fresh oranges exist initially, 0 minutes needed
        if (freshCount == 0) return 0

        var minutes = 0
        val dirs = arrayOf(intArrayOf(0, 1), intArrayOf(1, 0), intArrayOf(0, -1), intArrayOf(-1, 0))

        // 2. Multi-source BFS spreading in concentric waves
        while (queue.isNotEmpty() && freshCount > 0) {
            val levelSize = queue.size
            minutes++

            for (i in 0 until levelSize) {
                val (r, c) = queue.removeFirst()
                for (d in dirs) {
                    val nr = r + d[0]
                    val nc = c + d[1]

                    if (nr in 0 until m && nc in 0 until n && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2 // Mark rotten immediately
                        freshCount--
                        queue.add(intArrayOf(nr, nc))
                    }
                }
            }
        }

        return if (freshCount == 0) minutes else -1
    }
}""",
            'notes_kotlin': "`freshCount` avoids post-scan loops. `freshCount > 0` condition prevents phantom minute increments.",
            'code_python': """class Solution:
    # Optimal: Multi-Source Queue BFS
    # Time: O(M * N) - Single pass
    # Space: O(M * N)
    def orangesRotting(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        queue = collections.deque()
        fresh = 0

        # Step 1: Initialize multi-source queue and count fresh
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 2:
                    queue.append((r, c))
                elif grid[r][c] == 1:
                    fresh += 1

        if fresh == 0:
            return 0

        minutes = 0
        dirs = [(0, 1), (1, 0), (0, -1), (-1, 0)]

        # Step 2: BFS wave propagation
        while queue and fresh > 0:
            level_size = len(queue)
            minutes += 1
            for _ in range(level_size):
                r, c = queue.popleft()
                for dr, dc in dirs:
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 1:
                        grid[nr][nc] = 2
                        fresh -= 1
                        queue.append((nr, nc))

        return minutes if fresh == 0 else -1""",
            'notes_python': "Canonical Python implementation. Checking `fresh > 0` in while-loop avoids off-by-one errors.",
            'code_java': """class Solution {
    /**
     * Optimal: Multi-Source Queue BFS with Fresh Orange Counter
     * Time: O(M * N) - Linear graph traversal
     * Space: O(M * N) - Queue allocation
     */
    public int orangesRotting(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        Queue<int[]> queue = new ArrayDeque<>();
        int fresh = 0;

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }

        if (fresh == 0) return 0;

        int minutes = 0;
        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!queue.isEmpty() && fresh > 0) {
            int levelSize = queue.size();
            minutes++;

            for (int i = 0; i < levelSize; i++) {
                int[] curr = queue.poll();
                for (int[] d : dirs) {
                    int nr = curr[0] + d[0];
                    int nc = curr[1] + d[1];

                    if (nr >= 0 && nr < m && nc >= 0 && nc < n && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2; // Infect immediately
                        fresh--;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
        }

        return (fresh == 0) ? minutes : -1;
    }
}""",
            'notes_java': "Optimal production standard. Immediate `grid[nr][nc] = 2` prevents duplicate queue pollution."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun orangesRotting(grid: Array<IntArray>): Int {
        val q = ArrayDeque<IntArray>()
        var fresh = 0
        for (r in grid.indices) for (c in grid[0].indices) {
            if (grid[r][c] == 2) q.add(intArrayOf(r, c))
            else if (grid[r][c] == 1) fresh++
        }
        if (fresh == 0) return 0
        var mins = 0
        val dirs = arrayOf(intArrayOf(0,1), intArrayOf(1,0), intArrayOf(0,-1), intArrayOf(-1,0))
        while (q.isNotEmpty() && fresh > 0) {
            mins++
            repeat(q.size) {
                val (r, c) = q.removeFirst()
                for (d in dirs) {
                    val nr = r + d[0]; val nc = c + d[1]
                    if (nr in grid.indices && nc in grid[0].indices && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2; fresh--; q.add(intArrayOf(nr, nc))
                    }
                }
            }
        }
        return if (fresh == 0) mins else -1
    }
}""",
        'python_boilerplate': """import collections

class Solution:
    def orangesRotting(self, grid: List[List[int]]) -> int:
        q = collections.deque()
        fresh = 0
        for r in range(len(grid)):
            for c in range(len(grid[0])):
                if grid[r][c] == 2: q.append((r, c))
                elif grid[r][c] == 1: fresh += 1
        if fresh == 0: return 0
        mins = 0
        while q and fresh > 0:
            mins += 1
            for _ in range(len(q)):
                r, c = q.popleft()
                for dr, dc in [(0,1),(1,0),(0,-1),(-1,0)]:
                    nr, nc = r + dr, c + dc
                    if 0 <= nr < len(grid) and 0 <= nc < len(grid[0]) and grid[nr][nc] == 1:
                        grid[nr][nc] = 2
                        fresh -= 1
                        q.append((nr, nc))
        return mins if fresh == 0 else -1""",
        'java_boilerplate': """class Solution {
    public int orangesRotting(int[][] grid) {
        Queue<int[]> q = new ArrayDeque<>();
        int fresh = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 2) q.offer(new int[]{r, c});
                else if (grid[r][c] == 1) fresh++;
            }
        }
        if (fresh == 0) return 0;
        int mins = 0;
        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};
        while (!q.isEmpty() && fresh > 0) {
            mins++;
            int size = q.size();
            for (int i = 0; i < size; i++) {
                int[] cur = q.poll();
                for (int[] d : dirs) {
                    int nr = cur[0] + d[0], nc = cur[1] + d[1];
                    if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2; fresh--; q.offer(new int[]{nr, nc});
                    }
                }
            }
        }
        return fresh == 0 ? mins : -1;
    }
}""",
        'js_test_runner': """
      const testCases = [
        { grid: [[2,1,1],[1,1,0],[0,1,1]], expected: 4 },
        { grid: [[2,1,1],[0,1,1],[1,0,1]], expected: -1 },
        { grid: [[0,2]], expected: 0 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runRot(mat) {
          let g = mat.map(r => [...r]);
          let q = [], fresh = 0;
          for (let r = 0; r < g.length; r++) {
            for (let c = 0; c < g[0].length; c++) {
              if (g[r][c] === 2) q.push([r, c]);
              else if (g[r][c] === 1) fresh++;
            }
          }
          if (fresh === 0) return 0;
          let mins = 0;
          let dirs = [[0,1],[1,0],[0,-1],[-1,0]];
          while (q.length > 0 && fresh > 0) {
            mins++;
            let sz = q.length;
            for (let i = 0; i < sz; i++) {
              let [r, c] = q.shift();
              for (let [dr, dc] of dirs) {
                let nr = r + dr, nc = c + dc;
                if (nr >= 0 && nr < g.length && nc >= 0 && nc < g[0].length && g[nr][nc] === 1) {
                  g[nr][nc] = 2; fresh--; q.push([nr, nc]);
                }
              }
            }
          }
          return (fresh === 0) ? mins : -1;
        }

        const out = runRot(tc.grid);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: expected ${tc.expected}</span>
          <span>${passed ? '&#10004; PASSED (' + out + ' mins)' : '&#10008; FAILED (got ' + out + ')'}</span>
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
        'phase1': "I clarify: Can there be zero fresh oranges at the start? Yes, if fresh == 0, return 0 immediately. Can multiple rotten oranges exist at minute 0? Yes, and they must spread concurrently in parallel, which is the definition of multi-source BFS.",
        'phase2': "For strategy: Standard DFS cannot model simultaneous infection waves. We use multi-source BFS. In pass 1, we seed the queue with all initial rotten oranges (value 2) and count all fresh oranges (value 1). If `fresh == 0`, we return 0 immediately. Then, while `queue.isNotEmpty() && fresh > 0`, we increment minutes by 1, snapshot `queue.size`, and process all oranges for that minute wave. When a fresh orange is infected, we set `grid[nr][nc] = 2`, decrement `fresh`, and enqueue it. Finally, if `fresh == 0` return minutes, else return -1. Time is O(M * N) and space is O(M * N).",
        'phase3': "I implement `orangesRotting`: first populate `queue` with all (r, c) where `grid[r][c] == 2` and tally `fresh`. Loop while `queue.isNotEmpty() && fresh > 0`: `minutes++`, loop `size` times, poll cell, inspect 4 cardinal neighbors. If neighbor is 1, mutate to 2, decrement fresh, offer to queue. At the end, return `if (fresh == 0) minutes else -1`.",
        'phase4': "Dry run: isolated orange [[2,1,1],[0,1,1],[1,0,1]]. The orange at (2,0) has no adjacent fresh oranges because (1,0) and (2,1) are empty (0). When the queue empties after 4 minutes, fresh = 1 > 0. Returns -1. Correct.",
        'senior_followup': "Senior Scalability: In epidemiological simulations or viral broadcast propagation on social networks, contagion spreads concurrently across millions of user nodes. Systems run multi-source BFS via Pregel/GraphX vertex-centric programs where each infected node sends an infection message to its outgoing neighbors in supersteps."
    },
    'quiz': [
        {
            'question': "Why is Depth-First Search (DFS) unsuitable for finding the minimum minutes in Rotting Oranges?",
            'options': [
                "Because multiple rotten oranges spread infection simultaneously in concurrent concentric waves; DFS explores one long branch to maximum depth, failing to model concurrent multi-source arrivals.",
                "Because DFS cannot modify 2D arrays.",
                "Because DFS uses more heap memory than BFS.",
                "Because DFS cannot check cardinal directions."
            ],
            'correct_index': 0,
            'explanation': "BFS explores node frontiers in lockstep level by level (minute by minute), naturally resolving shortest arrival times across multiple concurrent starting points."
        },
        {
            'question': "Why is the condition `while (queue.isNotEmpty() && fresh > 0)` used rather than just `while (queue.isNotEmpty())`?",
            'options': [
                "To prevent an off-by-one error where minutes is incremented one extra time after the last fresh orange has already rotted.",
                "To prevent integer underflow.",
                "Because empty queues throw exceptions in Kotlin.",
                "It enables parallel GPU processing."
            ],
            'correct_index': 0,
            'explanation': "Once the last fresh orange is infected, the newly enqueued rotten oranges would otherwise trigger another while-loop iteration, incrementing `minutes` by 1 unnecessarily."
        }
    ]
}

p53 = {
    'num': 53,
    'lc': 841,
    'title': "Keys and Rooms",
    'category': "2D Grid Traversals & Graphs",
    'difficulty': "Medium",
    'lc_slug': "keys-and-rooms",
    'verbatim_statement': """<p>There are <code>n</code> rooms labeled from <code>0</code> to <code>n - 1</code> and all the rooms are locked, except for room <code>0</code>. Your goal is to visit all the rooms. However, you cannot enter a locked room without having its key.</p>
<p>When you visit a room, you may find a set of <strong>distinct keys</strong> in it. Each key has a number on it, denoting which room it unlocks, and you can take all of them with you to unlock other rooms.</p>
<p>Given an array <code>rooms</code> where <code>rooms[i]</code> is the set of keys that you can obtain if you visited room <code>i</code>, return <code>true</code> <em>if you can visit <strong>all</strong> the rooms, or</em> <code>false</code> <em>otherwise</em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'rooms = [[1],[2],[3],[]]', 'output': 'true\nExplanation: We visit room 0 and pick up key 1. We then visit room 1 and pick up key 2. We then visit room 2 and pick up key 3. We then visit room 3. Since we were able to visit every room, we return true.'},
        {'title': "Example 2", 'input': 'rooms = [[1,3],[3,0,1],[2],[0]]', 'output': 'false\nExplanation: We can not enter room number 2 since the only key that unlocks it is in that room.'}
    ],
    'constraints': [
        "n == rooms.length",
        "2 <= n <= 1000",
        "0 <= rooms[i].length <= 1000",
        "1 <= sum(rooms[i].length) <= 3000",
        "0 <= rooms[i][j] < n",
        "All the values of rooms[i] are unique."
    ],
    'plain_english': "Check if all nodes in a directed graph are reachable starting from node 0. Traverse the graph with DFS or BFS, keeping track of visited rooms in a boolean array or set. At the end, return `true` if `visitedCount == rooms.length`, else `false`.",
    'boundary_checks': {
        'min_base': "2 rooms <code>rooms = [[1], []]</code> &rarr; Returns true.",
        'best_exit': "Room 0 contains all keys <code>[[1, 2, 3], [], [], []]</code> &rarr; All rooms unlocked immediately.",
        'worst_scan': "Cycle with disconnected island <code>rooms = [[1], [0], [3], []]</code> &rarr; Rooms 2 and 3 unreachable, returns false."
    },
    'mental_model': {
        'analogy_title': "The Hotel Keycard Scavenger Hunt",
        'analogy_text': "You start in Hotel Room #0. On the nightstand, you find keycards to other rooms. You walk to those rooms, enter them, and collect any new keycards found inside. You continue opening newly discovered unlocked doors. When you have no more unvisited rooms to open, you check your checklist: did you open all N rooms in the building?",
        'aha_moment': "This is standard graph connectivity from a single source (node 0)! An adjacency list is already given as `rooms[i]`. We only need to check if the connected component containing node 0 has size N.",
        'trigger': "Directed graph reachability from source, unlock rooms, network packet broadcast &rarr; BFS or DFS reachability check.",
        'sisters': [
            {'name': "Number of Provinces", 'lc': "LC #547"},
            {'name': "Find if Path Exists in Graph", 'lc': "LC #1971"},
            {'name': "Clone Graph", 'lc': "LC #133"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(V + E) Time, O(V) Space",
        'naive_text': "Iterative Queue BFS collecting keys and tracking visited in a HashSet.",
        'wasted_work': "HashSet hashing overhead when room numbers are compact integers `0..N-1`.",
        'optimal_complexity': "O(V + E) Time, O(V) Space",
        'optimal_text': "DFS with primitive `boolean[n]` array and running count. Direct array indexing with zero hash overhead.",
        'core_invariant': "Every room entered is marked `visited[room] = true` exactly once, preventing infinite cycles.",
        'table_rows': [
            {'name': "1. Queue BFS with HashSet", 'time': "O(V + E)", 'space': "O(V)", 'note': "Uses HashSet for visited; object boxing overhead."},
            {'name': "2. BitSet Reachability", 'time': "O(V + E)", 'space': "O(V / 64)", 'note': "Uses bitwise flags; optimal memory footprint."},
            {'name': "★ 3. Recursive DFS with Boolean Array", 'time': "O(V + E)", 'space': "O(V) Call Stack", 'note': "Fastest execution speed; standard interview solution."}
        ]
    },
    'trace_table': {
        'input_desc': 'rooms = [[1, 3], [3, 0, 1], [2], [0]] (N = 4)',
        'columns': ["Step", "Room Entered", "Keys Found in Room", "Visited Array [0..3]", "Rooms Count Visited"],
        'rows': [
            ["Init", "Room 0", "[1, 3]", "[T, F, F, F]", "1"],
            ["1", "Room 1 (via key 1)", "[3, 0, 1]", "[T, T, F, F]", "2"],
            ["2", "Room 3 (via key 3)", "[0]", "[T, T, F, T]", "3"],
            ["3", "Keys [0, 1, 3] already visited", "-", "[T, T, F, T]", "3"],
            ["Finish", "Traversal halts (no new keys)", "-", "[T, T, F, T]", "3 < 4 &rarr; Return FALSE!"]
        ],
        'takeaway': "Room 2 is locked from the outside; only 3 of 4 rooms visited &rarr; Returns false."
    },
    'stepper': {
        'title': "Keys and Rooms DFS Reachability Stepper",
        'default_input': '[[1,3],[3,0,1],[2],[0]]',
        'presets': [
            {'label': "Example 1 (All Reachable)", 'onclick': 'loadPreset([[1],[2],[3],[]])'},
            {'label': "Example 2 (Room 2 Blocked)", 'onclick': 'loadPreset([[1,3],[3,0,1],[2],[0]])'}
        ],
        'metrics': [
            {'id': "metric-room", 'label': "Current Room", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-keys", 'label': "Keys Acquired", 'color': "text-cyan-400", 'initial': "[]"},
            {'id': "metric-visited", 'label': "Rooms Visited", 'color': "text-emerald-400", 'initial': "1 / N"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="rooms-stage" class="w-full flex items-center justify-center p-4 min-h-[140px] overflow-x-auto"></div>',
        'initial_narration': 'Click "Next Step" to trace room unlocking.',
        'pseudocode_lines': [
            "val visited = BooleanArray(rooms.size)",
            "var count = 0",
            "fun dfs(room: Int) {",
            "    visited[room] = true; count++",
            "    for (key in rooms[room]) {",
            "        if (!visited[key]) dfs(key)",
            "    }",
            "}",
            "dfs(0)",
            "return count == rooms.size"
        ],
        'js_code': """
    let rawRooms = [[1,3],[3,0,1],[2],[0]];

    function buildSteps(adj) {
      steps = [];
      let n = adj.length;
      let visited = new Array(n).fill(false);
      let count = 0;

      function dfs(u) {
        visited[u] = true;
        count++;
        steps.push({
          room: u, keys: `[${adj[u].join(', ')}]`,
          visitedStr: `${count} / ${n}`,
          action: `Entered Room ${u}`,
          visitedArr: [...visited],
          activeRoom: u, line: 4,
          narration: `Entered Room ${u}. Collected keys: [${adj[u].join(', ')}]. Total unlocked: ${count}/${n}.`
        });

        for (let key of adj[u]) {
          if (!visited[key]) {
            steps.push({
              room: u, keys: `[${adj[u].join(', ')}]`,
              visitedStr: `${count} / ${n}`,
              action: `Using Key ${key} &rarr; Room ${key}`,
              visitedArr: [...visited],
              activeRoom: u, line: 6,
              narration: `Found unvisited key ${key}. Unlocking Room ${key}.`
            });
            dfs(key);
          }
        }
      }

      dfs(0);

      let success = (count === n);
      steps.push({
        room: "Done", keys: "-",
        visitedStr: `${count} / ${n}`,
        action: success ? "ALL ROOMS UNLOCKED (TRUE)" : "SOME LOCKED (FALSE)",
        visitedArr: [...visited],
        activeRoom: -1, line: 10,
        narration: success
          ? `Success! All ${n} rooms were visited. Returned true.`
          : `Failed: Visited ${count} of ${n} rooms. Returned false.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = JSON.stringify(arr);
      rawRooms = arr;
      stepperReset();
      buildSteps(rawRooms);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const parsed = JSON.parse(val);
        if (Array.isArray(parsed) && parsed.length > 0) {
          rawRooms = parsed;
          stepperReset();
          buildSteps(rawRooms);
          renderStepper();
        }
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-room').textContent = step.room;
      document.getElementById('metric-keys').textContent = step.keys;
      document.getElementById('metric-visited').textContent = step.visitedStr;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('rooms-stage');
      stage.innerHTML = '';
      if (!step.visitedArr) return;

      const row = document.createElement('div');
      row.className = 'flex flex-wrap items-center justify-center gap-3';

      step.visitedArr.forEach((isVis, idx) => {
        const isActive = (idx === step.activeRoom);
        const card = document.createElement('div');
        card.className = `flex flex-col items-center justify-center p-3 rounded-xl border min-w-[70px] font-mono ${
          isActive ? 'bg-amber-500/30 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' :
          isVis ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' :
          'bg-slate-900 text-slate-500 border-slate-800'
        }`;
        card.innerHTML = `
          <span class="text-xs font-bold">Room ${idx}</span>
          <span class="text-[9px] uppercase">${isVis ? 'UNLOCKED' : 'LOCKED'}</span>
        `;
        row.appendChild(card);
      });

      stage.appendChild(row);
"""
    },
    'approaches': [
        {
            'name': "Iterative Queue BFS",
            'desc': "Use a Queue to hold available keys. Dequeue keys and visit rooms, enqueuing new keys.",
            'time': "O(V + E)",
            'space': "O(V)",
            'code_kotlin': """class Solution {
    fun canVisitAllRooms(rooms: List<List<Int>>): Boolean {
        val visited = BooleanArray(rooms.size)
        val queue = ArrayDeque<Int>()
        queue.add(0)
        visited[0] = true
        var count = 1

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            for (key in rooms[curr]) {
                if (!visited[key]) {
                    visited[key] = true
                    count++
                    queue.add(key)
                }
            }
        }

        return count == rooms.size
    }
}""",
            'notes_kotlin': "Standard iterative BFS using `ArrayDeque`. Immune to stack overflow.",
            'code_python': """class Solution:
    def canVisitAllRooms(self, rooms: List[List[int]]) -> bool:
        visited = [False] * len(rooms)
        visited[0] = True
        queue = collections.deque([0])
        count = 1

        while queue:
            curr = queue.popleft()
            for key in rooms[curr]:
                if not visited[key]:
                    visited[key] = True
                    count += 1
                    queue.append(key)

        return count == len(rooms)""",
            'notes_python': "Clean BFS using `collections.deque`.",
            'code_java': """class Solution {
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        int n = rooms.size();
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(0);
        visited[0] = true;
        int count = 1;

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            for (int key : rooms.get(curr)) {
                if (!visited[key]) {
                    visited[key] = true;
                    count++;
                    queue.offer(key);
                }
            }
        }

        return count == n;
    }
}""",
            'notes_java': "Iterative BFS. Safe against recursion stack limits."
        },
        {
            'name': "BitSet Reachability",
            'desc': "Use a Java BitSet or Python integer bitmask to track visited rooms.",
            'time': "O(V + E)",
            'space': "O(V / 64)",
            'code_kotlin': """class Solution {
    fun canVisitAllRooms(rooms: List<List<Int>>): Boolean {
        val n = rooms.size
        val visited = java.util.BitSet(n)
        val stack = ArrayDeque<Int>()
        stack.add(0)
        visited.set(0)

        while (stack.isNotEmpty()) {
            val u = stack.removeLast()
            for (v in rooms[u]) {
                if (!visited.get(v)) {
                    visited.set(v)
                    stack.add(v)
                }
            }
        }
        return visited.cardinality() == n
    }
}""",
            'notes_kotlin': "Compact BitSet representation. Excellent memory efficiency.",
            'code_python': """class Solution:
    def canVisitAllRooms(self, rooms: List[List[int]]) -> bool:
        visited = set([0])
        stack = [0]
        while stack:
            u = stack.pop()
            for key in rooms[u]:
                if key not in visited:
                    visited.add(key)
                    stack.append(key)
        return len(visited) == len(rooms)""",
            'notes_python': "Python set and stack DFS.",
            'code_java': """class Solution {
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        int n = rooms.size();
        BitSet visited = new BitSet(n);
        visited.set(0);
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            for (int key : rooms.get(u)) {
                if (!visited.get(key)) {
                    visited.set(key);
                    stack.push(key);
                }
            }
        }

        return visited.cardinality() == n;
    }
}""",
            'notes_java': "BitSet uses 64x less RAM than boolean arrays."
        },
        {
            'name': "Recursive DFS with Boolean Array (Optimal Primary)",
            'desc': "Explore reachable rooms recursively. Mark visited in boolean array and increment counter.",
            'time': "O(V + E)",
            'space': "O(V) Call Stack",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Recursive DFS Reachability
     * Time: O(V + E) - Visits each room and key edge exactly once
     * Space: O(V) - Recursion stack and boolean array of size V
     */
    fun canVisitAllRooms(rooms: List<List<Int>>): Boolean {
        val visited = BooleanArray(rooms.size)
        var count = 0

        fun dfs(room: Int) {
            visited[room] = true
            count++

            for (key in rooms[room]) {
                if (!visited[key]) {
                    dfs(key)
                }
            }
        }

        dfs(0)
        return count == rooms.size
    }
}""",
            'notes_kotlin': "Optimal and concise. Visiting each vertex and edge once in O(V + E) time.",
            'code_python': """class Solution:
    # Optimal: Recursive DFS Reachability
    # Time: O(V + E)
    # Space: O(V)
    def canVisitAllRooms(self, rooms: List[List[int]]) -> bool:
        visited = [False] * len(rooms)
        count = 0

        def dfs(u: int):
            nonlocal count
            visited[u] = True
            count += 1
            for key in rooms[u]:
                if not visited[key]:
                    dfs(key)

        dfs(0)
        return count == len(rooms)""",
            'notes_python': "Textbook optimal graph DFS.",
            'code_java': """class Solution {
    private int count = 0;

    /**
     * Optimal: Recursive DFS Reachability
     * Time: O(V + E)
     * Space: O(V)
     */
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        int n = rooms.size();
        boolean[] visited = new boolean[n];
        dfs(0, rooms, visited);
        return count == n;
    }

    private void dfs(int room, List<List<Integer>> rooms, boolean[] visited) {
        visited[room] = true;
        count++;

        for (int key : rooms.get(room)) {
            if (!visited[key]) {
                dfs(key, rooms, visited);
            }
        }
    }
}""",
            'notes_java': "Production standard. Fastest cache performance with direct boolean array lookups."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun canVisitAllRooms(rooms: List<List<Int>>): Boolean {
        val vis = BooleanArray(rooms.size)
        var c = 0
        fun dfs(u: Int) {
            vis[u] = true; c++
            for (k in rooms[u]) if (!vis[k]) dfs(k)
        }
        dfs(0)
        return c == rooms.size
    }
}""",
        'python_boilerplate': """class Solution:
    def canVisitAllRooms(self, rooms: List[List[int]]) -> bool:
        vis = [False] * len(rooms)
        c = 0
        def dfs(u):
            nonlocal c
            vis[u] = True; c += 1
            for k in rooms[u]:
                if not vis[k]: dfs(k)
        dfs(0)
        return c == len(rooms)""",
        'java_boilerplate': """class Solution {
    int c = 0;
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        boolean[] vis = new boolean[rooms.size()];
        dfs(0, rooms, vis);
        return c == rooms.size();
    }
    private void dfs(int u, List<List<Integer>> rooms, boolean[] vis) {
        vis[u] = true; c++;
        for (int k : rooms.get(u)) if (!vis[k]) dfs(k, rooms, vis);
    }
}""",
        'js_test_runner': """
      const testCases = [
        { rooms: [[1],[2],[3],[]], expected: true },
        { rooms: [[1,3],[3,0,1],[2],[0]], expected: false }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runRooms(adj) {
          let vis = new Array(adj.length).fill(false);
          let c = 0;
          function dfs(u) {
            vis[u] = true; c++;
            for (let k of adj[u]) if (!vis[k]) dfs(k);
          }
          dfs(0);
          return c === adj.length;
        }

        const out = runRooms(tc.rooms);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: expected ${tc.expected}</span>
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
        'phase1': "I clarify: Are we always starting at room 0? Yes, room 0 is initially unlocked. Are rooms and keys 0-indexed? Yes, rooms are 0 to n-1. Can a room contain keys to rooms we have already visited? Yes, which is why visited checking is essential to prevent infinite cycles.",
        'phase2': "For strategy: The input `rooms` is already an adjacency list representation of a directed graph where an edge exists from room `u` to room `v` if room `u` contains key `v`. We need to determine if all N nodes are reachable from source node 0. A standard DFS or BFS with a boolean `visited` array of size N solves this in O(V + E) time. We increment a counter whenever a new room is entered. If `count == n` at the end, return true; else false.",
        'phase3': "I implement `canVisitAllRooms`: allocate `visited = BooleanArray(rooms.size)` and counter `count = 0`. In `dfs(u)`: set `visited[u] = true`, increment `count++`. For each `key in rooms[u]`: if `!visited[key]`, recurse `dfs(key)`. Kick off with `dfs(0)`. Return `count == rooms.size`.",
        'phase4': "Dry run on [[1,3],[3,0,1],[2],[0]]: Start at 0, count=1. Visits 1 (count=2). Key 3 takes us to room 3 (count=3). Room 3 only has key 0 (already visited). All available keys are exhausted. Count = 3, but N = 4. Room 2 was never reached. Returns false. Correct.",
        'senior_followup': "Senior Scalability: In distributed cluster security policies (e.g. IAM permission chaining or OAuth scope reachability), this is identical to finding transitive closure from an initial trust credential. For large scale graphs with billions of nodes, transitive closure is computed using bitset-compressed matrix multiplication or parallel BFS wavefronts."
    },
    'quiz': [
        {
            'question': "What is the time complexity of the graph traversal in Keys and Rooms?",
            'options': [
                "O(V + E) where V is the number of rooms and E is the total number of keys across all rooms.",
                "O(V * E)",
                "O(V^2)",
                "O(log V)"
            ],
            'correct_index': 0,
            'explanation': "Each room (vertex) is visited at most once, and each key (directed edge) is inspected at most once, yielding linear graph traversal time $O(V + E)$."
        },
        {
            'question': "Why is a `visited` boolean array necessary when exploring the rooms?",
            'options': [
                "Because rooms can contain reciprocal keys (e.g. room 0 has key 1, and room 1 has key 0), which would cause infinite recursion without visited tracking.",
                "To sort the room numbers.",
                "Because Java lists cannot be modified during iteration.",
                "To free RAM memory."
            ],
            'correct_index': 0,
            'explanation': "Graphs often have cycles. If room 0 opens room 1, and room 1 contains key 0, without a visited check the algorithm would loop infinitely between rooms 0 and 1."
        }
    ]
}

p54 = {
    'num': 54,
    'lc': 547,
    'title': "Number of Provinces",
    'category': "2D Grid Traversals & Graphs",
    'difficulty': "Medium",
    'lc_slug': "number-of-provinces",
    'verbatim_statement': """<p>There are <code>n</code> cities. Some of them are connected, while some are not. If city <code>a</code> is connected directly with city <code>b</code>, and city <code>b</code> is connected directly with city <code>c</code>, then city <code>a</code> is connected indirectly with city <code>c</code>.</p>
<p>A <strong>province</strong> is a group of directly or indirectly connected cities and no other cities outside of the group.</p>
<p>You are given an <code>n x n</code> matrix <code>isConnected</code> where <code>isConnected[i][j] = 1</code> if the <code>i<sup>th</sup></code> city and the <code>j<sup>th</sup></code> city are directly connected, and <code>isConnected[i][j] = 0</code> otherwise.</p>
<p>Return <em>the total number of <strong>provinces</strong></em>.</p>""",
    'examples': [
        {'title': "Example 1", 'input': 'isConnected = [[1,1,0],[1,1,0],[0,0,1]]', 'output': '2'},
        {'title': "Example 2", 'input': 'isConnected = [[1,0,0],[0,1,0],[0,0,1]]', 'output': '3'}
    ],
    'constraints': [
        "1 <= n <= 200",
        "n == isConnected.length",
        "n == isConnected[i].length",
        "isConnected[i][j] is 1 or 0.",
        "isConnected[i][i] == 1",
        "isConnected[i][j] == isConnected[j][i]"
    ],
    'plain_english': "Count the number of connected components in an undirected graph given as an adjacency matrix. Loop through all cities `0` to `n - 1`. If city `i` has not been visited, increment province count and launch a DFS/BFS to visit every city reachable from `i`.",
    'boundary_checks': {
        'min_base': "Single city <code>[[1]]</code> &rarr; Returns 1 province.",
        'best_exit': "All cities connected <code>isConnected[i][j] == 1</code> &rarr; Sunk in 1 DFS call, returns 1.",
        'worst_scan': "Identity matrix (all 0s off-diagonal) &rarr; Every city isolated, returns N provinces."
    },
    'mental_model': {
        'analogy_title': "Social Network Friendship Circles",
        'analogy_text': "Think of N students in a school. If Alice is friends with Bob, and Bob is friends with Charlie, they all belong to the same social circle (province). To count the distinct friendship circles, you pick an uncounted student, invite everyone in their entire friend network into the gymnasium, and count them as Circle #1. You then find the next student who hasn't been called yet and repeat. The number of groups called is the number of provinces.",
        'aha_moment': "This is identical to 'Number of Islands', but instead of a 4-directional grid, the graph is represented by an $N \\times N$ adjacency matrix! Each DFS visits city `u` by looping `for (v in 0 until n)` and recursing if `isConnected[u][v] == 1` and `!visited[v]`.",
        'trigger': "Connected components in undirected graph, adjacency matrix clusters &rarr; DFS with boolean visited array or Disjoint Set Union (Union-Find).",
        'sisters': [
            {'name': "Number of Islands", 'lc': "LC #200"},
            {'name': "Graph Valid Tree", 'lc': "LC #261"},
            {'name': "Redundant Connection", 'lc': "LC #684"}
        ]
    },
    'naive_vs_optimal': {
        'naive_complexity': "O(N^2 * alpha(N)) Time, O(N) Space",
        'naive_text': "Initialize Disjoint Set Union with N sets. Loop through all pairs `(i, j)` where `i < j`: if `isConnected[i][j] == 1`, union them and decrement count.",
        'wasted_work': "Union-Find structure overhead when a simple DFS visits each vertex and edge in O(N^2) time.",
        'optimal_complexity': "O(N^2) Time, O(N) Auxiliary Space",
        'optimal_text': "DFS with boolean `visited` array. Scans each row of the adjacency matrix once.",
        'core_invariant': "After completing DFS for city `i`, every city in `i`'s connected component has `visited[c] == true`.",
        'table_rows': [
            {'name': "1. Disjoint Set Union (Union-Find)", 'time': "O(N^2 * alpha(N))", 'space': "O(N)", 'note': "Excellent for dynamic graphs where edges arrive over time."},
            {'name': "2. Queue BFS with Visited Array", 'time': "O(N^2)", 'space': "O(N)", 'note': "Iterative FIFO exploration of components."},
            {'name': "★ 3. Recursive DFS with Boolean Array", 'time': "O(N^2)", 'space': "O(N) Call Stack", 'note': "Standard optimal algorithm. Cleanest in interviews."}
        ]
    },
    'trace_table': {
        'input_desc': 'isConnected = [[1,1,0],[1,1,0],[0,0,1]] (N = 3)',
        'columns': ["City i Loop", "Visited State [0, 1, 2]", "Action Taken", "Cities Traversed in DFS", "Total Provinces Count"],
        'rows': [
            ["i = 0", "[F, F, F]", "Unvisited! Province count +1", "DFS from 0 visits 0 and 1 &rarr; [T, T, F]", "1"],
            ["i = 1", "[T, T, F]", "Already visited in Province 1", "Skip", "1"],
            ["i = 2", "[T, T, F]", "Unvisited! Province count +1", "DFS from 2 visits 2 &rarr; [T, T, T]", "2"]
        ],
        'takeaway': "Total provinces = 2. City 0 and 1 share Province 1; City 2 is isolated in Province 2."
    },
    'stepper': {
        'title': "Adjacency Matrix Province Stepper",
        'default_input': '[[1,1,0],[1,1,0],[0,0,1]]',
        'presets': [
            {'label': "Example 1 (2 Provinces)", 'onclick': 'loadPreset([[1,1,0],[1,1,0],[0,0,1]])'},
            {'label': "Example 2 (3 Isolated)", 'onclick': 'loadPreset([[1,0,0],[0,1,0],[0,0,1]])'},
            {'label': "All Connected (1 Province)", 'onclick': 'loadPreset([[1,1,1],[1,1,1],[1,1,1]])'}
        ],
        'metrics': [
            {'id': "metric-city", 'label': "Inspecting City", 'color': "text-amber-400", 'initial': "0"},
            {'id': "metric-vis", 'label': "Visited Array", 'color': "text-cyan-400", 'initial': "[F, F, F]"},
            {'id': "metric-prov", 'label': "Provinces Found", 'color': "text-emerald-400", 'initial': "0"},
            {'id': "metric-action", 'label': "Action", 'color': "text-purple-400", 'initial': "Ready"}
        ],
        'visual_html': '<div id="prov-stage" class="w-full flex items-center justify-center p-4 min-h-[140px] overflow-x-auto"></div>',
        'initial_narration': 'Click "Next Step" to trace province component discovery.',
        'pseudocode_lines': [
            "var provinces = 0",
            "val visited = BooleanArray(n)",
            "for (i in 0 until n) {",
            "    if (!visited[i]) {",
            "        provinces++",
            "        dfs(i, isConnected, visited)",
            "    }",
            "}",
            "fun dfs(u, mat, visited) {",
            "    visited[u] = true",
            "    for (v in 0 until n) if (mat[u][v] == 1 && !visited[v]) dfs(v, mat, visited)",
            "}"
        ],
        'js_code': """
    let rawProv = [[1,1,0],[1,1,0],[0,0,1]];

    function buildSteps(mat) {
      steps = [];
      let n = mat.length;
      let visited = new Array(n).fill(false);
      let provCount = 0;

      for (let i = 0; i < n; i++) {
        if (!visited[i]) {
          provCount++;
          steps.push({
            city: i, vis: `[${visited.map(v => v ? 'T' : 'F').join(', ')}]`,
            prov: provCount, action: `New Province #${provCount} found at City ${i}`,
            visitedArr: [...visited], activeCity: i, line: 5,
            narration: `City ${i} is unvisited. Incremented provinces count to ${provCount}. Starting DFS component sweep.`
          });

          function dfs(u) {
            visited[u] = true;
            for (let v = 0; v < n; v++) {
              if (mat[u][v] === 1 && !visited[v]) {
                steps.push({
                  city: u, vis: `[${visited.map(x => x ? 'T' : 'F').join(', ')}]`,
                  prov: provCount, action: `Edge ${u} &rarr; ${v} (Connected)`,
                  visitedArr: [...visited], activeCity: v, line: 11,
                  narration: `City ${u} is connected to unvisited City ${v}. Exploring City ${v}.`
                });
                dfs(v);
              }
            }
          }

          dfs(i);
        } else {
          steps.push({
            city: i, vis: `[${visited.map(v => v ? 'T' : 'F').join(', ')}]`,
            prov: provCount, action: `City ${i} Already Visited`,
            visitedArr: [...visited], activeCity: i, line: 4,
            narration: `City ${i} already belongs to an existing province. Skipping.`
          });
        }
      }

      steps.push({
        city: "Done", vis: `[${visited.map(v => v ? 'T' : 'F').join(', ')}]`,
        prov: provCount, action: "Scan Complete",
        visitedArr: [...visited], activeCity: -1, line: 1,
        narration: `Scan complete! Total provinces = ${provCount}.`
      });
    }

    function loadPreset(arr) {
      document.getElementById('custom-input').value = JSON.stringify(arr);
      rawProv = arr;
      stepperReset();
      buildSteps(rawProv);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      try {
        const parsed = JSON.parse(val);
        if (Array.isArray(parsed) && parsed.length > 0) {
          rawProv = parsed;
          stepperReset();
          buildSteps(rawProv);
          renderStepper();
        }
      } catch(e) {}
    }
""",
        'js_update_metrics': """
      document.getElementById('metric-city').textContent = step.city;
      document.getElementById('metric-vis').textContent = step.vis;
      document.getElementById('metric-prov').textContent = step.prov;
      document.getElementById('metric-action').textContent = step.action;
""",
        'js_render_visual': """
      const stage = document.getElementById('prov-stage');
      stage.innerHTML = '';
      if (!step.visitedArr) return;

      const row = document.createElement('div');
      row.className = 'flex flex-wrap items-center justify-center gap-3';

      step.visitedArr.forEach((isVis, idx) => {
        const isActive = (idx === step.activeCity);
        const card = document.createElement('div');
        card.className = `flex flex-col items-center justify-center p-3 rounded-xl border min-w-[70px] font-mono ${
          isActive ? 'bg-amber-500/30 text-amber-300 border-amber-500 ring-2 ring-amber-500/40' :
          isVis ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40' :
          'bg-slate-900 text-slate-500 border-slate-800'
        }`;
        card.innerHTML = `
          <span class="text-xs font-bold">City ${idx}</span>
          <span class="text-[9px] uppercase">${isVis ? 'VISITED' : 'UNVISITED'}</span>
        `;
        row.appendChild(card);
      });

      stage.appendChild(row);
"""
    },
    'approaches': [
        {
            'name': "Disjoint Set Union (Union-Find)",
            'desc': "Initialize DSU with N components. Loop over all pairs (i, j): if isConnected[i][j] == 1, union them.",
            'time': "O(N^2 * alpha(N))",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun findCircleNum(isConnected: Array<IntArray>): Int {
        val n = isConnected.size
        val parent = IntArray(n) { it }
        var provinces = n

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
                provinces--
            }
        }

        for (i in 0 until n) {
            for (j in i + 1 until n) {
                if (isConnected[i][j] == 1) {
                    union(i, j)
                }
            }
        }

        return provinces
    }
}""",
            'notes_kotlin': "Union-Find with path compression. Decrements component counter on each successful union.",
            'code_python': """class Solution:
    def findCircleNum(self, isConnected: List[List[int]]) -> int:
        n = len(isConnected)
        parent = list(range(n))
        provinces = n

        def find(i):
            if parent[i] == i: return i
            parent[i] = find(parent[i])
            return parent[i]

        def union(i, j):
            nonlocal provinces
            ri, rj = find(i), find(j)
            if ri != rj:
                parent[ri] = rj
                provinces -= 1

        for i in range(n):
            for j in range(i + 1, n):
                if isConnected[i][j] == 1:
                    union(i, j)

        return provinces""",
            'notes_python': "Standard DSU approach. Ideal for dynamic incremental edges.",
            'code_java': """class Solution {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        int provinces = n;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    int r1 = find(parent, i);
                    int r2 = find(parent, j);
                    if (r1 != r2) {
                        parent[r1] = r2;
                        provinces--;
                    }
                }
            }
        }

        return provinces;
    }

    private int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return parent[i] = find(parent, parent[i]);
    }
}""",
            'notes_java': "Parent array with path compression."
        },
        {
            'name': "Breadth-First Search (Queue BFS)",
            'desc': "Iterate cities 0..N-1. For each unvisited city, increment count and run Queue BFS to mark all connected cities.",
            'time': "O(N^2)",
            'space': "O(N)",
            'code_kotlin': """class Solution {
    fun findCircleNum(isConnected: Array<IntArray>): Int {
        val n = isConnected.size
        val visited = BooleanArray(n)
        var provinces = 0
        val queue = ArrayDeque<Int>()

        for (i in 0 until n) {
            if (!visited[i]) {
                provinces++
                queue.add(i)
                visited[i] = true

                while (queue.isNotEmpty()) {
                    val u = queue.removeFirst()
                    for (v in 0 until n) {
                        if (isConnected[u][v] == 1 && !visited[v]) {
                            visited[v] = true
                            queue.add(v)
                        }
                    }
                }
            }
        }
        return provinces
    }
}""",
            'notes_kotlin': "Queue BFS avoids recursion call stack. Straightforward.",
            'code_python': """class Solution:
    def findCircleNum(self, isConnected: List[List[int]]) -> int:
        n = len(isConnected)
        visited = [False] * n
        provinces = 0
        queue = collections.deque()

        for i in range(n):
            if not visited[i]:
                provinces += 1
                queue.append(i)
                visited[i] = True
                while queue:
                    u = queue.popleft()
                    for v in range(n):
                        if isConnected[u][v] == 1 and not visited[v]:
                            visited[v] = True
                            queue.append(v)

        return provinces""",
            'notes_python': "Iterative BFS using `collections.deque`.",
            'code_java': """class Solution {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int provinces = 0;
        Queue<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                provinces++;
                queue.offer(i);
                visited[i] = true;
                while (!queue.isEmpty()) {
                    int u = queue.poll();
                    for (int v = 0; v < n; v++) {
                        if (isConnected[u][v] == 1 && !visited[v]) {
                            visited[v] = true;
                            queue.offer(v);
                        }
                    }
                }
            }
        }
        return provinces;
    }
}""",
            'notes_java': "Iterative BFS queue avoids recursion limits."
        },
        {
            'name': "Recursive Depth-First Search (Optimal Primary)",
            'desc': "Loop over cities 0..N-1. If unvisited, increment province count and launch recursive DFS to visit component.",
            'time': "O(N^2)",
            'space': "O(N) Call Stack",
            'code_kotlin': """class Solution {
    /**
     * Optimal: Recursive DFS Connected Components
     * Time: O(N^2) - Traverses N x N adjacency matrix
     * Space: O(N) - Visited boolean array and recursion stack
     */
    fun findCircleNum(isConnected: Array<IntArray>): Int {
        val n = isConnected.size
        val visited = BooleanArray(n)
        var provinces = 0

        fun dfs(u: Int) {
            visited[u] = true
            for (v in 0 until n) {
                if (isConnected[u][v] == 1 && !visited[v]) {
                    dfs(v)
                }
            }
        }

        for (i in 0 until n) {
            if (!visited[i]) {
                provinces++
                dfs(i)
            }
        }

        return provinces
    }
}""",
            'notes_kotlin': "Minimal code footprint. Direct primitive boolean array tracking.",
            'code_python': """class Solution:
    # Optimal: Recursive DFS
    # Time: O(N^2)
    # Space: O(N)
    def findCircleNum(self, isConnected: List[List[int]]) -> int:
        n = len(isConnected)
        visited = [False] * n
        provinces = 0

        def dfs(u: int):
            visited[u] = True
            for v in range(n):
                if isConnected[u][v] == 1 and not visited[v]:
                    dfs(v)

        for i in range(n):
            if not visited[i]:
                provinces += 1
                dfs(i)

        return provinces""",
            'notes_python': "Concise, elegant Python implementation.",
            'code_java': """class Solution {
    /**
     * Optimal: Recursive DFS Connected Components
     * Time: O(N^2) - Iterates over N x N matrix
     * Space: O(N) - Recursion stack bounded by N
     */
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int provinces = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                provinces++;
                dfs(i, isConnected, visited);
            }
        }

        return provinces;
    }

    private void dfs(int u, int[][] isConnected, boolean[] visited) {
        visited[u] = true;
        for (int v = 0; v < isConnected.length; v++) {
            if (isConnected[u][v] == 1 && !visited[v]) {
                dfs(v, isConnected, visited);
            }
        }
    }
}""",
            'notes_java': "Standard production template for adjacency matrix connected component analysis."
        }
    ],
    'scratchpad': {
        'kotlin_boilerplate': """class Solution {
    fun findCircleNum(isConnected: Array<IntArray>): Int {
        val n = isConnected.size
        val vis = BooleanArray(n)
        var p = 0
        fun dfs(u: Int) {
            vis[u] = true
            for (v in 0 until n) if (isConnected[u][v] == 1 && !vis[v]) dfs(v)
        }
        for (i in 0 until n) if (!vis[i]) { p++; dfs(i) }
        return p
    }
}""",
        'python_boilerplate': """class Solution:
    def findCircleNum(self, isConnected: List[List[int]]) -> int:
        n = len(isConnected)
        vis = [False] * n
        p = 0
        def dfs(u):
            vis[u] = True
            for v in range(n):
                if isConnected[u][v] == 1 and not vis[v]: dfs(v)
        for i in range(n):
            if not vis[i]:
                p += 1; dfs(i)
        return p""",
        'java_boilerplate': """class Solution {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length, p = 0;
        boolean[] vis = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!vis[i]) {
                p++;
                dfs(i, isConnected, vis);
            }
        }
        return p;
    }
    private void dfs(int u, int[][] mat, boolean[] vis) {
        vis[u] = true;
        for (int v = 0; v < mat.length; v++) {
            if (mat[u][v] == 1 && !vis[v]) dfs(v, mat, vis);
        }
    }
}""",
        'js_test_runner': """
      const testCases = [
        { mat: [[1,1,0],[1,1,0],[0,0,1]], expected: 2 },
        { mat: [[1,0,0],[0,1,0],[0,0,1]], expected: 3 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        function runCircle(mat) {
          let n = mat.length;
          let vis = new Array(n).fill(false);
          let p = 0;
          function dfs(u) {
            vis[u] = true;
            for (let v = 0; v < n; v++) {
              if (mat[u][v] === 1 && !vis[v]) dfs(v);
            }
          }
          for (let i = 0; i < n; i++) {
            if (!vis[i]) {
              p++;
              dfs(i);
            }
          }
          return p;
        }

        const out = runCircle(tc.mat);
        const passed = (out === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: expected ${tc.expected}</span>
          <span>${passed ? '&#10004; PASSED (' + out + ' provinces)' : '&#10008; FAILED (got ' + out + ')'}</span>
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
        'phase1': "I clarify: What is the format of the graph? It's an N x N adjacency matrix where `isConnected[i][j] == 1` indicates an undirected edge between city i and city j. What is the maximum value of N? Up to 200 cities.",
        'phase2': "For strategy: A province is an undirected connected component. We initialize a `visited` boolean array of size N and `provinces = 0`. We iterate `i` from `0` to `n - 1`. If city `i` is not visited, we increment `provinces++` and invoke `dfs(i)`. The DFS marks `visited[u] = true` and loops through all cities `v`: if `isConnected[u][v] == 1 && !visited[v]`, we recurse on `v`. Because we inspect an N x N matrix, time is O(N^2) and space is O(N).",
        'phase3': "I implement `findCircleNum`: initialize `visited = BooleanArray(n)` and `provinces = 0`. In `dfs(u)`: set `visited[u] = true`, loop `for (v in 0 until n)`: if `isConnected[u][v] == 1 && !visited[v]`, call `dfs(v)`. Loop `for (i in 0 until n)`: if `!visited[i]`, increment `provinces++` and call `dfs(i)`. Return `provinces`.",
        'phase4': "Dry run with [[1,1,0],[1,1,0],[0,0,1]]: i=0 unvisited &rarr; provinces=1. dfs(0) visits 0 and 1, setting visited=[T, T, F]. i=1 is visited, skip. i=2 unvisited &rarr; provinces=2. dfs(2) visits 2, setting visited=[T, T, T]. Returns 2 provinces. Correct.",
        'senior_followup': "Senior Scalability: In large-scale social networks (e.g. LinkedIn connection clusters with millions of users), the adjacency matrix is far too large (O(N^2) memory). We represent the graph with adjacency lists and compute connected components in parallel via MapReduce or Pregel Label Propagation."
    },
    'quiz': [
        {
            'question': "Why is the time complexity of Number of Provinces O(N^2) rather than O(V + E)?",
            'options': [
                "Because the graph is given as an N x N adjacency matrix; examining all possible edges for each vertex requires scanning all N entries in that row, regardless of how many 1s exist.",
                "Because cities are connected bidirectionally.",
                "Because provinces must be sorted.",
                "Because DFS is an exponential algorithm."
            ],
            'correct_index': 0,
            'explanation': "An adjacency matrix requires inspecting every entry $(u, v)$ to check if an edge exists, forcing $\\sum_{u=0}^{N-1} N = N^2$ operations regardless of graph density."
        },
        {
            'question': "When would an engineer prefer Disjoint Set Union (Union-Find) over DFS for this problem?",
            'options': [
                "When edge connections are dynamic and stream in continuously one by one over time, allowing incremental union operations without re-scanning the entire graph from scratch.",
                "When the graph has negative cycle weights.",
                "When the graph has more than 100 cities.",
                "To reduce the time complexity to O(1)."
            ],
            'correct_index': 0,
            'explanation': "DSU excels at online / dynamic connectivity queries where edges are added sequentially in real time, maintaining component counts without rerunning full traversals."
        }
    ]
}
