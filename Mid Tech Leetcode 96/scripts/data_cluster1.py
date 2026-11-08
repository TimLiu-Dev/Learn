import sys
sys.path.append('scripts')
from gen_cluster1 import cluster1_problems

# Add 24, 25, 26, 27 to cluster1_problems
cluster1_problems.extend([
    # ---------------- 0024: STRING COMPRESSION ----------------
    {
        'num': 24,
        'lc': 443,
        'title': "String Compression",
        'category': "Arrays, Strings & Hashing",
        'difficulty': "Medium",
        'lc_slug': "string-compression",
        'verbatim_statement': """<p>Given an array of characters <code>chars</code>, compress it using the following algorithm:</p>
<p class="mt-2">Begin with an empty string <code>s</code>. For each group of <strong>consecutive repeating characters</strong> in <code>chars</code>:</p>
<ul class="list-disc pl-5 mt-1 space-y-1 text-xs">
  <li>If the group's length is <code>1</code>, append the character to <code>s</code>.</li>
  <li>Otherwise, append the character followed by the group's length.</li>
</ul>
<p class="mt-2">The compressed string <code>s</code> <strong>should not be returned separately</strong>, but instead, be stored <strong>in the input character array <code>chars</code></strong>. Note that group lengths that are 10 or longer will be split into multiple characters in <code>chars</code>.</p>
<p class="mt-2">After you are done modifying the input array, return <em>the new length of the array</em>.</p>
<p class="mt-2">You must write an algorithm that uses only <strong>constant extra space</strong>.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'chars = ["a","a","b","b","c","c","c"]', 'output': '6, chars = ["a","2","b","2","c","3"]'},
            {'title': "Example 2", 'input': 'chars = ["a"]', 'output': '1, chars = ["a"]'},
            {'title': "Example 3", 'input': 'chars = ["a","b","b","b","b","b","b","b","b","b","b","b","b"]', 'output': '4, chars = ["a","b","1","2"]'}
        ],
        'constraints': [
            "1 <= chars.length <= 2000",
            "chars[i] is a lowercase English letter, uppercase English letter, digit, or symbol."
        ],
        'plain_english': "Perform in-place run-length encoding directly inside the given char array. Count consecutive repeating characters. Write the character, and if count > 1, write each digit of the count into the array. Return the new length, using strictly O(1) auxiliary space.",
        'boundary_checks': {
            'min_base': "<code>chars = [\"a\"]</code> &rarr; Single character, count 1 &rarr; return 1.",
            'best_exit': "All distinct chars <code>[\"a\",\"b\",\"c\"]</code> &rarr; No compression needed, return N.",
            'worst_scan': "12 consecutive 'b's &rarr; 'b','1','2' (length 3, multi-digit count split)."
        },
        'mental_model': {
            'analogy_title': "The Tape Recorder Overwriter",
            'analogy_text': "Imagine a cassette tape where a scanner head (read pointer) moves forward listening to repeated musical tones. A recording head (write pointer) follows behind on the same tape, overwriting old data with a compressed summary: the tone symbol followed by its repeat count. Because a compressed group (char + digits) is always less than or equal to the uncompressed group length, the write head never overtakes the read head.",
            'aha_moment': "Two pointers (read & write) operating on the same array. Since 1 char takes 1 cell, 2 chars take 2 cells ('a2'), and 10 chars take 3 cells ('a10'), the write pointer is strictly <= read pointer at all times.",
            'trigger': "Whenever asked for in-place array modification with run-length or grouping &rarr; Fast/Slow Read-Write Two Pointers.",
            'sisters': [
                {'name': "Move Zeroes", 'lc': "LC #283"},
                {'name': "Remove Duplicates from Sorted Array", 'lc': "LC #26"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N) Time, O(N) Auxiliary Space",
            'naive_text': "Build a StringBuilder or list containing compressed tokens, then copy characters back to chars. Fails the strict O(1) space interview constraint.",
            'wasted_work': "Allocating heap memory for a temporary buffer when the input array has sufficient capacity.",
            'optimal_complexity': "O(N) Time, O(1) Auxiliary Space",
            'optimal_text': "Linear scan with `read` finding group boundaries, and `write` updating characters and count digits in-place.",
            'core_invariant': "write <= read at all iterations; no unread character is ever prematurely overwritten.",
            'table_rows': [
                {'name': "1. Auxiliary Buffer", 'time': "O(N)", 'space': "O(N)", 'note': "Allocates StringBuilder; violates O(1) space constraint."},
                {'name': "★ 2. In-Place Read/Write", 'time': "O(N)", 'space': "O(1)", 'note': "Modifies chars array directly. Constant memory."}
            ]
        },
        'trace_table': {
            'input_desc': 'chars = ["a","a","b","b","c","c","c"]',
            'columns': ["Read Range", "Char", "Streak Count", "Write Action", "Array State", "Write Index"],
            'rows': [
                ["0..1", "'a'", "2", "write 'a', '2'", "['a','2','b','b','c','c','c']", "2"],
                ["2..3", "'b'", "2", "write 'b', '2'", "['a','2','b','2','c','c','c']", "4"],
                ["4..6", "'c'", "3", "write 'c', '3'", "['a','2','b','2','c','3','c']", "6"]
            ],
            'takeaway': "Returns write index 6. Unread cells beyond index 6 are ignored."
        },
        'stepper': {
            'title': "In-Place Read/Write Pointer Movement",
            'default_input': 'a, a, b, b, c, c, c',
            'presets': [
                {'label': "Standard Run", 'onclick': 'loadPreset(["a","a","b","b","c","c","c"])'},
                {'label': "Multi-Digit Count", 'onclick': 'loadPreset(["a","b","b","b","b","b","b","b","b","b","b","b","b"])'},
                {'label': "Singletons", 'onclick': 'loadPreset(["a","b","c","d"])'}
            ],
            'metrics': [
                {'id': "metric-write", 'label': "Write Pointer", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-read", 'label': "Read Pointer", 'color': "text-cyan-400", 'initial': "0"},
                {'id': "metric-streak", 'label': "Current Streak", 'color': "text-amber-400", 'initial': "0"},
                {'id': "metric-char", 'label': "Active Char", 'color': "text-purple-400", 'initial': "-"}
            ],
            'visual_html': '<div id="chars-array-container" class="w-full flex gap-2 justify-center items-center py-4 overflow-x-auto"></div>',
            'initial_narration': 'Click "Next Step" to trace read and write pointer steps.',
            'pseudocode_lines': [
                "var write = 0; var read = 0",
                "while (read < chars.size) {",
                "    val start = read; while (read < chars.size && chars[read] == chars[start]) read++",
                "    chars[write++] = chars[start]",
                "    val count = read - start",
                "    if (count > 1) for (d in count.toString()) chars[write++] = d",
                "}",
                "return write"
            ],
            'js_code': """
    let initialData = ["a","a","b","b","c","c","c"];

    function buildSteps(arr) {
      steps = [];
      const c = [...arr];
      let write = 0;
      let read = 0;

      steps.push({
        write: 0,
        read: 0,
        streak: 0,
        char: "-",
        arr: [...c],
        line: 1,
        narration: "Initialized write = 0 and read = 0."
      });

      while (read < c.length) {
        const start = read;
        const targetChar = c[start];
        while (read < c.length && c[read] === targetChar) {
          read++;
        }
        const count = read - start;

        c[write] = targetChar;
        const wAfterChar = write + 1;

        steps.push({
          write: wAfterChar,
          read: read,
          streak: count,
          char: targetChar,
          arr: [...c],
          line: 4,
          narration: `Read group of '${targetChar}' (streak length ${count}). Wrote '${targetChar}' at index ${write}.`
        });

        write = wAfterChar;

        if (count > 1) {
          const countStr = count.toString();
          for (let i = 0; i < countStr.length; i++) {
            c[write] = countStr[i];
            write++;
          }
          steps.push({
            write: write,
            read: read,
            streak: count,
            char: targetChar,
            arr: [...c],
            line: 6,
            narration: `Wrote count digits "${countStr}" into array. New write index = ${write}.`
          });
        }
      }

      steps.push({
        write: write,
        read: read,
        streak: 0,
        char: "Done",
        arr: [...c],
        line: 8,
        narration: `Compression complete! Returning new length = ${write}.`
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
      const arr = val.split(',').map(s => s.trim().replace(/['"]/g, '')).filter(s => s.length > 0);
      if (arr.length > 0) {
        initialData = arr;
        stepperReset();
        buildSteps(initialData);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-write').textContent = step.write;
      document.getElementById('metric-read').textContent = step.read;
      document.getElementById('metric-streak').textContent = step.streak;
      document.getElementById('metric-char').textContent = step.char;
""",
            'js_render_visual': """
      const container = document.getElementById('chars-array-container');
      container.innerHTML = '';
      step.arr.forEach((ch, idx) => {
        const isWrite = (idx === step.write);
        const isRead = (idx === step.read);
        const isPastWrite = (idx < step.write);

        const card = document.createElement('div');
        card.className = `flex flex-col items-center gap-1`;
        card.innerHTML = `
          <div class="w-10 h-10 rounded-lg flex items-center justify-center font-mono font-bold text-sm border ${
            isPastWrite ? 'border-emerald-500 bg-emerald-950/40 text-emerald-300' : 'border-slate-800 bg-slate-900 text-slate-400'
          }">
            ${ch}
          </div>
          <span class="text-[9px] font-mono text-slate-500">${idx}</span>
          <div class="flex gap-0.5 text-[8px] font-mono">
            ${isWrite ? '<span class="px-1 rounded bg-emerald-500 text-slate-950 font-bold">W</span>' : ''}
            ${isRead ? '<span class="px-1 rounded bg-cyan-400 text-slate-950 font-bold">R</span>' : ''}
          </div>
        `;
        container.appendChild(card);
      });
"""
        },
        'approaches': [
            {
                'name': "Naive: Auxiliary Buffer",
                'desc': "Construct compressed string in StringBuilder, then copy back to array.",
                'time': "O(N)",
                'space': "O(N)",
                'code_kotlin': """class Solution {
    fun compress(chars: CharArray): Int {
        val sb = StringBuilder()
        var i = 0
        while (i < chars.size) {
            val char = chars[i]
            var count = 0
            while (i < chars.size && chars[i] == char) {
                count++
                i++
            }
            sb.append(char)
            if (count > 1) sb.append(count)
        }
        for (j in 0 until sb.length) {
            chars[j] = sb[j]
        }
        return sb.length
    }
}""",
                'notes_kotlin': "Violates interview constraint: uses O(N) extra heap memory via StringBuilder.",
                'code_python': """class Solution:
    def compress(self, chars: list[str]) -> int:
        buffer = []
        i = 0
        while i < len(chars):
            ch = chars[i]
            count = 0
            while i < len(chars) and chars[i] == ch:
                count += 1
                i += 1
            buffer.append(ch)
            if count > 1:
                buffer.extend(list(str(count)))
        for j in range(len(buffer)):
            chars[j] = buffer[j]
        return len(buffer)""",
                'notes_python': "Creating an extra list buffer of characters consumes O(N) auxiliary space.",
                'code_java': """class Solution {
    public int compress(char[] chars) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < chars.length) {
            char c = chars[i];
            int count = 0;
            while (i < chars.length && chars[i] == c) {
                count++;
                i++;
            }
            sb.append(c);
            if (count > 1) sb.append(count);
        }
        for (int j = 0; j < sb.length(); j++) {
            chars[j] = sb.charAt(j);
        }
        return sb.length();
    }
}""",
                'notes_java': "Not admissible when interviewer explicitly forbids dynamic buffer allocations."
            },
            {
                'name': "Better: Two Pointers with String Split",
                'desc': "Two pointers using standard string formatting for digits.",
                'time': "O(N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun compress(chars: CharArray): Int {
        var write = 0
        var read = 0
        while (read < chars.size) {
            val start = read
            val ch = chars[start]
            while (read < chars.size && chars[read] == ch) {
                read++
            }
            chars[write++] = ch
            val count = read - start
            if (count > 1) {
                for (digit in count.toString()) {
                    chars[write++] = digit
                }
            }
        }
        return write
    }
}""",
                'notes_kotlin': "`count.toString()` allocates a tiny temporary string (at most 4 chars for N <= 2000), which is acceptable in practice.",
                'code_python': """class Solution:
    def compress(self, chars: list[str]) -> int:
        write = 0
        read = 0
        while read < len(chars):
            start = read
            ch = chars[start]
            while read < len(chars) and chars[read] == ch:
                read += 1
            chars[write] = ch
            write += 1
            count = read - start
            if count > 1:
                for digit in str(count):
                    chars[write] = digit
                    write += 1
        return write""",
                'notes_python': "Clean and readable Python. `str(count)` produces a 1-4 character string, keeping memory strictly bounded.",
                'code_java': """class Solution {
    public int compress(char[] chars) {
        int write = 0;
        int read = 0;
        while (read < chars.length) {
            int start = read;
            char ch = chars[start];
            while (read < chars.length && chars[read] == ch) {
                read++;
            }
            chars[write++] = ch;
            int count = read - start;
            if (count > 1) {
                for (char digit : Integer.toString(count).toCharArray()) {
                    chars[write++] = digit;
                }
            }
        }
        return write;
    }
}""",
                'notes_java': "Standard Java solution. `Integer.toString(count)` decomposes multi-digit numbers seamlessly."
            },
            {
                'name': "Optimal: Pure In-Place Mathematical Digit Extraction",
                'desc': "Pure O(1) auxiliary space without even temporary string allocation for count digits.",
                'time': "O(N)",
                'space': "O(1) Absolute Zero Heap Allocation",
                'code_kotlin': """class Solution {
    /**
     * Optimal: In-Place Two Pointers with Arithmetic Digit Extraction
     * Time: O(N) - Linear single pass over characters
     * Space: O(1) - Zero heap allocations; absolute constant memory
     */
    fun compress(chars: CharArray): Int {
        var write = 0
        var read = 0
        
        while (read < chars.size) {
            val start = read
            val ch = chars[start]
            while (read < chars.size && chars[read] == ch) {
                read++
            }
            
            chars[write++] = ch
            val count = read - start
            
            if (count > 1) {
                // Invert digits arithmetically or write via digit scan
                val digitStart = write
                var temp = count
                while (temp > 0) {
                    chars[write++] = '0' + (temp % 10)
                    temp /= 10
                }
                // Reverse written digits into correct significant order
                var l = digitStart
                var r = write - 1
                while (l < r) {
                    val swap = chars[l]
                    chars[l] = chars[r]
                    chars[r] = swap
                    l++; r--
                }
            }
        }
        return write
    }
}""",
                'notes_kotlin': "Demonstrates staff-level systems awareness: extracts digits via modulo/division and reverses in-place, eliminating temporary String objects.",
                'code_python': """class Solution:
    # Optimal: In-Place Two Pointers with Arithmetic Digit Extraction
    # Time: O(N) - Linear pass
    # Space: O(1) - Pure zero-allocation
    def compress(self, chars: list[str]) -> int:
        write = 0
        read = 0
        
        while read < len(chars):
            start = read
            ch = chars[start]
            while read < len(chars) and chars[read] == ch:
                read += 1
            
            chars[write] = ch
            write += 1
            count = read - start
            
            if count > 1:
                digit_start = write
                temp = count
                while temp > 0:
                    chars[write] = str(temp % 10)
                    write += 1
                    temp //= 10
                # Reverse digits in place to restore correct MSB order
                chars[digit_start:write] = reversed(chars[digit_start:write])
                
        return write""",
                'notes_python': "Reverses the slice `chars[digit_start:write]` in place. Demonstrates deep understanding of in-place memory mutations.",
                'code_java': """class Solution {
    /**
     * Optimal: In-Place Two Pointers with Arithmetic Digit Extraction
     * Time: O(N) - Linear scan
     * Space: O(1) - Pure zero-allocation
     */
    public int compress(char[] chars) {
        int write = 0;
        int read = 0;

        while (read < chars.length) {
            int start = read;
            char ch = chars[start];
            while (read < chars.length && chars[read] == ch) {
                read++;
            }

            chars[write++] = ch;
            int count = read - start;

            if (count > 1) {
                int digitStart = write;
                int temp = count;
                while (temp > 0) {
                    chars[write++] = (char) ('0' + (temp % 10));
                    temp /= 10;
                }
                // Reverse digits in-place
                int l = digitStart;
                int r = write - 1;
                while (l < r) {
                    char swap = chars[l];
                    chars[l] = chars[r];
                    chars[r] = swap;
                    l++; r--;
                }
            }
        }
        return write;
    }
}""",
                'notes_java': "Avoids String/Integer wrappers entirely. Critical for real-time low-latency systems (e.g. streaming telemetry)."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun compress(chars: CharArray): Int {
        var write = 0
        var read = 0
        while (read < chars.size) {
            val start = read
            val ch = chars[start]
            while (read < chars.size && chars[read] == ch) read++
            chars[write++] = ch
            val count = read - start
            if (count > 1) for (d in count.toString()) chars[write++] = d
        }
        return write
    }
}""",
            'python_boilerplate': """class Solution:
    def compress(self, chars: list[str]) -> int:
        write = 0
        read = 0
        while read < len(chars):
            start = read
            ch = chars[start]
            while read < len(chars) and chars[read] == ch:
                read += 1
            chars[write] = ch
            write += 1
            count = read - start
            if count > 1:
                for d in str(count):
                    chars[write] = d
                    write += 1
        return write""",
            'java_boilerplate': """class Solution {
    public int compress(char[] chars) {
        int write = 0, read = 0;
        while (read < chars.length) {
            int start = read;
            char ch = chars[start];
            while (read < chars.length && chars[read] == ch) read++;
            chars[write++] = ch;
            int count = read - start;
            if (count > 1) {
                for (char d : Integer.toString(count).toCharArray()) chars[write++] = d;
            }
        }
        return write;
    }
}""",
            'js_test_runner': """
      const testCases = [
        { input: ["a","a","b","b","c","c","c"], expectedLen: 6, expectedPrefix: ["a","2","b","2","c","3"] },
        { input: ["a"], expectedLen: 1, expectedPrefix: ["a"] },
        { input: ["a","b","b","b","b","b","b","b","b","b","b","b","b"], expectedLen: 4, expectedPrefix: ["a","b","1","2"] }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        const c = [...tc.input];
        let write = 0, read = 0;
        while (read < c.length) {
          let start = read;
          let ch = c[start];
          while (read < c.length && c[read] === ch) read++;
          c[write++] = ch;
          let count = read - start;
          if (count > 1) {
            for (let d of count.toString()) c[write++] = d;
          }
        }

        const matchLen = (write === tc.expectedLen);
        const matchContent = tc.expectedPrefix.every((ch, i) => c[i] === ch);
        const passed = matchLen && matchContent;
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: input=[${tc.input.map(s => `"${s}"`).join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (len=' + write + ', chars=' + JSON.stringify(c.slice(0, write)) + ')' : '&#10008; FAILED'}</span>
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
            'phase1': "I clarify: should count 1 be omitted? Yes, per problem statement singletons are just 'a', not 'a1'. Can group lengths exceed 9? Yes, if count is 12, it must be written as two separate characters '1' and '2'. Is modifying the original array required? Yes, we must modify chars in-place with O(1) auxiliary space.",
            'phase2': "For time and space trade-offs, allocating an auxiliary buffer is O(N) space. Because the compressed length of any run is strictly less than or equal to the uncompressed length, the write pointer will never surpass the read pointer. Therefore, I can use two pointers: a read pointer scanning group boundaries, and a write pointer placing characters and digits in-place, achieving O(N) time and O(1) space.",
            'phase3': "I track write = 0 and read = 0. In an outer while loop, I record start = read, advance read while characters match, compute count = read - start, write the character, and if count > 1, convert count to characters and write them sequentially. Finally, I return write.",
            'phase4': "Dry run edge cases: with chars = ['a'], count is 1, write writes 'a' and returns 1. With 12 'b's, it writes 'b', '1', '2' and returns 3. With all distinct chars, write matches read at every step. Invariants hold.",
            'senior_followup': "Senior Scalability: What if the character stream is infinite and arriving via network socket? We would emit chunks using a streaming state machine (maintaining current_char and running_count), flushing to the downstream buffer whenever the character changes."
        },
        'quiz': [
            {
                'question': "Why is it mathematically guaranteed that the write pointer will never overwrite an unread character?",
                'options': [
                    "Because arrays in Java and Kotlin prevent index collisions.",
                    "Because every run of length K >= 1 compresses into 1 character (for K=1) or 1 + num_digits characters (for K >= 2), which is always <= K.",
                    "Because the read pointer moves backward while write moves forward.",
                    "Because garbage collection freezes the write head."
                ],
                'correct_index': 1,
                'explanation': "A run of length 1 takes 1 cell (ratio 1:1). A run of length 2 takes 2 cells ('a2', ratio 2:2). For length >= 3, compressed size is strictly smaller than original (e.g. 10 chars take 3 cells 'a10'). Thus write <= read always."
            },
            {
                'question': "What is the compressed representation of a single character 'x'?",
                'options': [
                    "'x1'",
                    "'x0'",
                    "'x'",
                    "Empty string ''"
                ],
                'correct_index': 2,
                'explanation': "The problem specifically dictates: 'If the group length is 1, append the character to s'. Single characters are not followed by any digit."
            }
        ]
    },

    # ---------------- 0025: CONTAINER WITH MOST WATER ----------------
    {
        'num': 25,
        'lc': 11,
        'title': "Container With Most Water",
        'category': "Two Pointers",
        'difficulty': "Medium",
        'lc_slug': "container-with-most-water",
        'verbatim_statement': """<p>You are given an integer array <code>height</code> of length <code>n</code>. There are <code>n</code> vertical lines drawn such that the two endpoints of the <code>i<sup>th</sup></code> line are <code>(i, 0)</code> and <code>(i, height[i])</code>.</p>
<p class="mt-2">Find two lines that together with the x-axis form a container, such that the container contains the most water.</p>
<p class="mt-2">Return <em>the maximum amount of water a container can store</em>.</p>
<p class="mt-2"><strong>Notice</strong> that you may not slant the container.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'height = [1,8,6,2,5,4,8,3,7]', 'output': '49 (between index 1 and 8: min(8, 7) * (8 - 1) = 7 * 7 = 49)'},
            {'title': "Example 2", 'input': 'height = [1,1]', 'output': '1 (min(1, 1) * (1 - 0) = 1)'}
        ],
        'constraints': [
            "n == height.length",
            "2 <= n <= 10^5",
            "0 <= height[i] <= 10^4"
        ],
        'plain_english': "Pick two vertical walls such that the area of water trapped between them (width * minimum height of the two walls) is maximized. Start with the widest possible container at both ends, and greedily move the pointer pointing to the shorter wall inward.",
        'boundary_checks': {
            'min_base': "<code>height = [1, 1]</code> &rarr; Only two walls, area = min(1, 1) * 1 = 1.",
            'best_exit': "Two huge walls at the ends <code>[1000, 1, 1, 1000]</code> &rarr; Maximum width and height immediately evaluated.",
            'worst_scan': "100,000 ascending walls &rarr; Single linear converging scan in O(N) time."
        },
        'mental_model': {
            'analogy_title': "The Two Telescoping Dam Walls",
            'analogy_text': "Think of a valley between two movable dam gates. The water level is limited strictly by the shorter gate (water spills over anything higher). As the gates move closer together, width strictly decreases. The only possible way to compensate for lost width is to find a significantly taller wall. Moving the taller gate inward can NEVER increase water volume (height is still bottlenecked by the short gate, while width shrank). Therefore, you must discard the shorter gate and search inward.",
            'aha_moment': "Area = (right - left) * min(h[left], h[right]). If you keep the shorter wall and move the taller one inward, width decreases and height cannot increase. Thus, moving the taller wall is mathematically guaranteed to be inferior. You must advance the shorter wall.",
            'trigger': "Whenever maximizing an area bounded by two array indices &rarr; Inward-Converging Two Pointers.",
            'sisters': [
                {'name': "Trapping Rain Water", 'lc': "LC #42"},
                {'name': "Two Sum II - Input Array Is Sorted", 'lc': "LC #167"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N^2) Time, O(1) Space",
            'naive_text': "Compute area for every pair (i, j) with nested loops. For N = 100,000, this requires 5 billion iterations, triggering Time Limit Exceeded.",
            'wasted_work': "Testing pairs with smaller widths and identical or smaller heights that cannot possibly beat the current maximum.",
            'optimal_complexity': "O(N) Time, O(1) Space",
            'optimal_text': "Start left=0, right=n-1. Compute area. Advance the pointer with the smaller height. Converges in N steps.",
            'core_invariant': "At each step, the discarded wall can never form a larger container with any remaining unexamined line.",
            'table_rows': [
                {'name': "1. Brute Force Nested Loops", 'time': "O(N^2)", 'space': "O(1)", 'note': "Tests all N*(N-1)/2 pairs. TLE on N=10^5."},
                {'name': "★ 2. Converging Two Pointers", 'time': "O(N)", 'space': "O(1)", 'note': "Single pass. Optimal and production standard."}
            ]
        },
        'trace_table': {
            'input_desc': 'height = [1, 8, 6, 2, 5, 4, 8, 3, 7]',
            'columns': ["Left (h[L])", "Right (h[R])", "Width", "Min Height", "Area", "Max Area", "Action"],
            'rows': [
                ["0 (1)", "8 (7)", "8", "1", "8", "8", "h[L] < h[R] &rarr; L++"],
                ["1 (8)", "8 (7)", "7", "7", "49", "49", "h[R] < h[L] &rarr; R--"],
                ["1 (8)", "7 (3)", "6", "3", "18", "49", "h[R] < h[L] &rarr; R--"],
                ["1 (8)", "6 (8)", "5", "8", "40", "49", "h[L] == h[R] &rarr; R--"],
                ["1 (8)", "5 (4)", "4", "4", "16", "49", "h[R] < h[L] &rarr; R--"],
                ["1 (8)", "4 (5)", "3", "5", "15", "49", "h[R] < h[L] &rarr; R--"],
                ["1 (8)", "3 (2)", "2", "2", "4", "49", "h[R] < h[L] &rarr; R--"],
                ["1 (8)", "2 (6)", "1", "6", "6", "49", "Pointers meet"]
            ],
            'takeaway': "Max area 49 found at step 2. Guaranteed optimal."
        },
        'stepper': {
            'title': "Two-Pointer Inward Contraction & Water Level Simulation",
            'default_input': '1, 8, 6, 2, 5, 4, 8, 3, 7',
            'presets': [
                {'label': "LeetCode Example", 'onclick': 'loadPreset([1,8,6,2,5,4,8,3,7])'},
                {'label': "Symmetric Valley", 'onclick': 'loadPreset([4,3,2,1,4])'},
                {'label': "Descending Heights", 'onclick': 'loadPreset([9,8,7,6,5,4])'}
            ],
            'metrics': [
                {'id': "metric-left", 'label': "Left Pointer", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-right", 'label': "Right Pointer", 'color': "text-cyan-400", 'initial': "8"},
                {'id': "metric-curr-area", 'label': "Current Area", 'color': "text-amber-400", 'initial': "8"},
                {'id': "metric-max-area", 'label': "Max Area Found", 'color': "text-purple-400", 'initial': "8"}
            ],
            'visual_html': '<div id="container-visual-canvas" class="w-full flex items-end justify-center gap-2 h-44 border-b border-slate-800 pb-1"></div>',
            'initial_narration': 'Click "Next Step" to trace converging two pointers.',
            'pseudocode_lines': [
                "var l = 0; var r = height.size - 1; var maxArea = 0",
                "while (l < r) {",
                "    val currentArea = minOf(height[l], height[r]) * (r - l)",
                "    maxArea = maxOf(maxArea, currentArea)",
                "    if (height[l] < height[r]) l++ else r--",
                "}",
                "return maxArea"
            ],
            'js_code': """
    let initialData = [1, 8, 6, 2, 5, 4, 8, 3, 7];

    function buildSteps(arr) {
      steps = [];
      let l = 0;
      let r = arr.length - 1;
      let maxArea = 0;

      while (l < r) {
        const hL = arr[l];
        const hR = arr[r];
        const width = r - l;
        const minH = Math.min(hL, hR);
        const area = width * minH;
        maxArea = Math.max(maxArea, area);

        const moveLeft = (hL < hR);

        steps.push({
          l: l,
          r: r,
          hL: hL,
          hR: hR,
          area: area,
          maxArea: maxArea,
          move: moveLeft ? "L++" : "R--",
          line: 3,
          narration: `Left=${l} (h=${hL}), Right=${r} (h=${hR}). Width=${width}, MinHeight=${minH} &rarr; Area=${area}. Max=${maxArea}. Next: ${moveLeft ? 'Advance Left' : 'Advance Right'}.`
        });

        if (moveLeft) l++;
        else r--;
      }

      steps.push({
        l: l,
        r: r,
        hL: arr[l],
        hR: arr[r],
        area: 0,
        maxArea: maxArea,
        move: "Done",
        line: 7,
        narration: `Pointers met at index ${l}. Maximum water trapped = ${maxArea}.`
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
      if (arr.length >= 2) {
        initialData = arr;
        stepperReset();
        buildSteps(initialData);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-left').textContent = `${step.l} (h=${step.hL})`;
      document.getElementById('metric-right').textContent = `${step.r} (h=${step.hR})`;
      document.getElementById('metric-curr-area').textContent = step.area;
      document.getElementById('metric-max-area').textContent = step.maxArea;
""",
            'js_render_visual': """
      const container = document.getElementById('container-visual-canvas');
      container.innerHTML = '';
      const maxVal = Math.max(...initialData, 1);

      initialData.forEach((val, idx) => {
        const isLeft = (idx === step.l);
        const isRight = (idx === step.r);
        const isBetween = (idx > step.l && idx < step.r);
        const heightPct = Math.round((val / maxVal) * 100);

        const col = document.createElement('div');
        col.className = 'flex flex-col items-center justify-end h-full w-8';
        
        let barColor = 'bg-slate-800 text-slate-500';
        if (isLeft) barColor = 'bg-emerald-500 text-slate-950 font-bold';
        else if (isRight) barColor = 'bg-cyan-400 text-slate-950 font-bold';
        else if (isBetween) barColor = 'bg-slate-700/60 text-slate-400';

        col.innerHTML = `
          <span class="text-[9px] font-mono text-slate-400 mb-1">${val}</span>
          <div class="w-full ${barColor} rounded-t transition-all duration-300" style="height: ${heightPct}%;"></div>
          <span class="text-[9px] font-mono text-slate-500 mt-1">${idx}</span>
          <div class="text-[8px] font-mono font-bold mt-0.5">
            ${isLeft ? '<span class="text-emerald-400">L</span>' : ''}
            ${isRight ? '<span class="text-cyan-400">R</span>' : ''}
          </div>
        `;
        container.appendChild(col);
      });
"""
        },
        'approaches': [
            {
                'name': "Brute Force All Pairs",
                'desc': "Check area for every pair (i, j) with nested loops.",
                'time': "O(N^2)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun maxArea(height: IntArray): Int {
        var maxArea = 0
        for (i in 0 until height.size) {
            for (j in i + 1 until height.size) {
                val area = minOf(height[i], height[j]) * (j - i)
                maxArea = maxOf(maxArea, area)
            }
        }
        return maxArea
    }
}""",
                'notes_kotlin': "Quadratic O(N^2) time complexity. For 100,000 lines, 10^10 operations trigger Time Limit Exceeded.",
                'code_python': """class Solution:
    def maxArea(self, height: list[int]) -> int:
        max_area = 0
        n = len(height)
        for i in range(n):
            for j in range(i + 1, n):
                area = min(height[i], height[j]) * (j - i)
                if area > max_area:
                    max_area = area
        return max_area""",
                'notes_python': "Nested loop pairwise checks in Python are prohibitively slow. Will timeout on LeetCode test cases.",
                'code_java': """class Solution {
    public int maxArea(int[] height) {
        int maxArea = 0;
        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                int area = Math.min(height[i], height[j]) * (j - i);
                maxArea = Math.max(maxArea, area);
            }
        }
        return maxArea;
    }
}""",
                'notes_java': "Simple to write in 30 seconds as the naive baseline, but fails on large datasets."
            },
            {
                'name': "Better: Two Pointers with Fast Skipping",
                'desc': "Two pointers skipping inward while height is smaller than current bottleneck.",
                'time': "O(N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun maxArea(height: IntArray): Int {
        var l = 0
        var r = height.size - 1
        var maxArea = 0
        
        while (l < r) {
            val minH = minOf(height[l], height[r])
            maxArea = maxOf(maxArea, minH * (r - l))
            // Fast skip walls that are shorter than current bottleneck
            while (l < r && height[l] <= minH) l++
            while (l < r && height[r] <= minH) r--
        }
        return maxArea
    }
}""",
                'notes_kotlin': "Inner while-loops skip walls that cannot beat `minH`, saving loop iterations on plateau-heavy datasets.",
                'code_python': """class Solution:
    def maxArea(self, height: list[int]) -> int:
        l = 0
        r = len(height) - 1
        max_area = 0
        
        while l < r:
            min_h = min(height[l], height[r])
            max_area = max(max_area, min_h * (r - l))
            while l < r and height[l] <= min_h:
                l += 1
            while l < r and height[r] <= min_h:
                r -= 1
        return max_area""",
                'notes_python': "Skips flat/redundant walls quickly, yielding lower runtime percentiles in competitive scoring.",
                'code_java': """class Solution {
    public int maxArea(int[] height) {
        int l = 0;
        int r = height.length - 1;
        int maxArea = 0;

        while (l < r) {
            int minH = Math.min(height[l], height[r]);
            maxArea = Math.max(maxArea, minH * (r - l));
            while (l < r && height[l] <= minH) l++;
            while (l < r && height[r] <= minH) r--;
        }
        return maxArea;
    }
}""",
                'notes_java': "Optimized branch execution; avoids recalculating area when height has not improved."
            },
            {
                'name': "Optimal: Classic Converging Two Pointers",
                'desc': "Clean canonical interview solution with single branch pointer advancement.",
                'time': "O(N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: Inward-Converging Two Pointers
     * Time: O(N) - Single pass with pointers meeting in the middle
     * Space: O(1) - Constant auxiliary memory
     */
    fun maxArea(height: IntArray): Int {
        var left = 0
        var right = height.size - 1
        var maxWater = 0

        while (left < right) {
            val width = right - left
            val hL = height[left]
            val hR = height[right]

            if (hL < hR) {
                val area = hL * width
                if (area > maxWater) maxWater = area
                left++
            } else {
                val area = hR * width
                if (area > maxWater) maxWater = area
                right--
            }
        }

        return maxWater
    }
}""",
                'notes_kotlin': "Direct indexing on primitive `IntArray` avoids all boxing overhead. Extremely clean and fast.",
                'code_python': """class Solution:
    # Optimal: Inward-Converging Two Pointers
    # Time: O(N) - Linear pass
    # Space: O(1) - Constant memory
    def maxArea(self, height: list[int]) -> int:
        left = 0
        right = len(height) - 1
        max_water = 0
        
        while left < right:
            width = right - left
            h_left = height[left]
            h_right = height[right]
            
            if h_left < h_right:
                area = h_left * width
                left += 1
            else:
                area = h_right * width
                right -= 1
                
            if area > max_water:
                max_water = area
                
        return max_water""",
                'notes_python': "Idiomatic and readable. Capturing `h_left` and `h_right` in local variables minimizes dictionary/list lookups.",
                'code_java': """class Solution {
    /**
     * Optimal: Inward-Converging Two Pointers
     * Time: O(N) - Exactly N - 1 iterations
     * Space: O(1) - Primitive integers only
     */
    public int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int maxWater = 0;

        while (left < right) {
            int width = right - left;
            int hL = height[left];
            int hR = height[right];

            if (hL < hR) {
                int area = hL * width;
                if (area > maxWater) maxWater = area;
                left++;
            } else {
                int area = hR * width;
                if (area > maxWater) maxWater = area;
                right--;
            }
        }

        return maxWater;
    }
}""",
                'notes_java': "The production gold standard. No Math.min or Math.max overhead; uses direct branch comparison."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun maxArea(height: IntArray): Int {
        var l = 0; var r = height.size - 1; var maxWater = 0
        while (l < r) {
            val area = minOf(height[l], height[r]) * (r - l)
            if (area > maxWater) maxWater = area
            if (height[l] < height[r]) l++ else r--
        }
        return maxWater
    }
}""",
            'python_boilerplate': """class Solution:
    def maxArea(self, height: list[int]) -> int:
        l, r, max_w = 0, len(height) - 1, 0
        while l < r:
            max_w = max(max_w, min(height[l], height[r]) * (r - l))
            if height[l] < height[r]:
                l += 1
            else:
                r -= 1
        return max_w""",
            'java_boilerplate': """class Solution {
    public int maxArea(int[] height) {
        int l = 0, r = height.length - 1, maxWater = 0;
        while (l < r) {
            int area = Math.min(height[l], height[r]) * (r - l);
            if (area > maxWater) maxWater = area;
            if (height[l] < height[r]) l++; else r--;
        }
        return maxWater;
    }
}""",
            'js_test_runner': """
      const testCases = [
        { input: [1,8,6,2,5,4,8,3,7], expected: 49 },
        { input: [1,1], expected: 1 },
        { input: [4,3,2,1,4], expected: 16 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        let l = 0, r = tc.input.length - 1, res = 0;
        while (l < r) {
          const area = Math.min(tc.input[l], tc.input[r]) * (r - l);
          if (area > res) res = area;
          if (tc.input[l] < tc.input[r]) l++; else r--;
        }

        const passed = (res === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: height=[${tc.input.join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (area=' + res + ')' : '&#10008; FAILED (expected ' + tc.expected + ', got ' + res + ')'}</span>
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
            'phase1': "I clarify: can heights be zero? Yes. Is the array length guaranteed to be at least 2? Yes. Does slant angle matter? No, water level is purely horizontal, capped by min(h[left], h[right]).",
            'phase2': "For complexity, brute force tests all pairs in O(N^2). We can achieve O(N) time and O(1) space with two pointers at the boundaries. Because width strictly decreases as pointers move inward, keeping the shorter wall could never yield a larger area with any other line. Thus, moving the pointer at the shorter wall inward is optimal.",
            'phase3': "I initialize left = 0, right = height.length - 1, and maxWater = 0. In a loop while left < right, I compute the width and compare height[left] against height[right]. I update maxWater, then increment left if height[left] is smaller, or decrement right otherwise.",
            'phase4': "Dry running edge cases: with [1, 1], area is 1*1=1, pointers cross. With strictly ascending heights [1, 2, 3, 4], left moves every time, tracking max areas. Everything runs in O(N) time without any auxiliary allocation.",
            'senior_followup': "Senior Scalability: What if the array is stored across a distributed disk block storage? We can read chunks from both ends concurrently, maintaining global left/right maximums and computing area streams without loading the entire array into memory."
        },
        'quiz': [
            {
                'question': "Why is it mathematically safe to discard the shorter wall and never re-examine it?",
                'options': [
                    "Because moving the taller wall inward decreases width while height remains bounded by the shorter wall, guaranteeing a strictly smaller area.",
                    "Because taller walls have lower density.",
                    "Because sorting the array simplifies pointer movement.",
                    "Because two pointers always require discarding the minimum element."
                ],
                'correct_index': 0,
                'explanation': "Area is bottlenecked by the shorter line: area = width * min(h[L], h[R]). If we kept the shorter line and moved the taller one inward, width would shrink and the height could never exceed the shorter line, guaranteeing a strictly worse result."
            },
            {
                'question': "When height[left] == height[right], which pointer should be moved?",
                'options': [
                    "You must terminate the search immediately.",
                    "Either pointer can be moved (or both simultaneously) without missing the optimal solution.",
                    "Only the left pointer can be moved.",
                    "You must backtrack to index 0."
                ],
                'correct_index': 1,
                'explanation': "When both lines have equal height, neither can form a larger container with any intermediate line because width will be smaller and neither can provide a height greater than the current bottleneck. Thus moving either or both is completely safe."
            }
        ]
    },

    # ---------------- 0026: LONGEST SUBSTRING WITHOUT REPEATING CHARACTERS ----------------
    {
        'num': 26,
        'lc': 3,
        'title': "Longest Substring Without Repeating Characters",
        'category': "Sliding Window",
        'difficulty': "Medium",
        'lc_slug': "longest-substring-without-repeating-characters",
        'verbatim_statement': """<p>Given a string <code>s</code>, find the length of the <strong>longest substring</strong> without repeating characters.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 's = "abcabcbb"', 'output': '3 (The answer is "abc", with the length of 3.)'},
            {'title': "Example 2", 'input': 's = "bbbbb"', 'output': '1 (The answer is "b", with the length of 1.)'},
            {'title': "Example 3", 'input': 's = "pwwkew"', 'output': '3 (The answer is "wke", with the length of 3.)'}
        ],
        'constraints': [
            "0 <= s.length <= 5 * 10^4",
            "s consists of English letters, digits, symbols and spaces."
        ],
        'plain_english': "Find the length of the longest contiguous chunk of characters that contains zero duplicates. Maintain a dynamic sliding window [left..right]. Expand the right edge. If a duplicate character is seen, fast-forward the left edge past the previous occurrence of that character.",
        'boundary_checks': {
            'min_base': "<code>s = \"\"</code> &rarr; Empty string, return 0.",
            'best_exit': "All distinct characters <code>\"abcdef\"</code> &rarr; Window expands to end, returns N.",
            'worst_scan': "50,000 identical characters <code>\"aaaa...\"</code> &rarr; Left pointer jumps on every step, returns 1."
        },
        'mental_model': {
            'analogy_title': "The Caterpillar with a Memory Journal",
            'analogy_text': "Picture a caterpillar stretching across a conveyor belt of alphabet letters. Its head (right pointer) eats letters forward, jotting down the exact coordinate where each letter was seen in a journal. If the head eats a letter that is already inside its belly (between its tail and head), its tail (left pointer) snaps immediately forward to one position past the previous sighting of that letter.",
            'aha_moment': "Instead of shifting `left` by 1 iteratively with a HashSet (which takes up to 2N steps), store character indices in a HashMap or integer array. When a duplicate is detected, jump `left = max(left, lastSeen[char] + 1)` in O(1) time.",
            'trigger': "Whenever finding the longest/shortest contiguous substring satisfying a uniqueness condition &rarr; Dynamic Sliding Window.",
            'sisters': [
                {'name': "Longest Repeating Character Replacement", 'lc': "LC #424"},
                {'name': "Minimum Window Substring", 'lc': "LC #76"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N^3) or O(N^2) Time, O(Min(N, M)) Space",
            'naive_text': "Examine every possible substring (i, j) and check for uniqueness using a set. Total substrings is N*(N+1)/2, each taking O(N) to validate.",
            'wasted_work': "Re-scanning known unique character sequences from scratch after moving the start index by 1.",
            'optimal_complexity': "O(N) Time, O(Min(N, M)) Space (where M is alphabet size)",
            'optimal_text': "Single pass sliding window with direct index jumping. The right pointer advances exactly N times; the left pointer monotonically advances.",
            'core_invariant': "The window substring `s[left..right]` contains strictly unique characters at all times.",
            'table_rows': [
                {'name': "1. Brute Force Substrings", 'time': "O(N^3)", 'space': "O(N)", 'note': "Tests all substrings. TLE on N=50,000."},
                {'name': "2. Sliding Window + HashSet", 'time': "O(2N)", 'space': "O(Min(N, M))", 'note': "Shrinks left by 1 cell each iteration."},
                {'name': "★ 3. Sliding Window + Index Map", 'time': "O(N)", 'space': "O(Min(N, M))", 'note': "Direct left pointer jump. Optimal production standard."}
            ]
        },
        'trace_table': {
            'input_desc': 's = "abcabcbb"',
            'columns': ["Right", "Char", "Last Seen Index", "Left Before -> After", "Window Substring", "Window Len", "Max Len"],
            'rows': [
                ["0", "'a'", "None", "0 &rarr; 0", "\"a\"", "1", "1"],
                ["1", "'b'", "None", "0 &rarr; 0", "\"ab\"", "2", "2"],
                ["2", "'c'", "None", "0 &rarr; 0", "\"abc\"", "3", "3"],
                ["3", "'a'", "0", "0 &rarr; max(0, 0+1) = 1", "\"bca\"", "3", "3"],
                ["4", "'b'", "1", "1 &rarr; max(1, 1+1) = 2", "\"cab\"", "3", "3"],
                ["5", "'c'", "2", "2 &rarr; max(2, 2+1) = 3", "\"abc\"", "3", "3"],
                ["6", "'b'", "4", "3 &rarr; max(3, 4+1) = 5", "\"b\"", "1", "3"],
                ["7", "'b'", "6", "5 &rarr; max(5, 6+1) = 7", "\"b\"", "1", "3"]
            ],
            'takeaway': "Maximum length 3 ('abc') maintained throughout."
        },
        'stepper': {
            'title': "Dynamic Sliding Window with Index Jump",
            'default_input': 'abcabcbb',
            'presets': [
                {'label': "abcabcbb", 'onclick': 'loadPreset("abcabcbb")'},
                {'label': "pwwkew", 'onclick': 'loadPreset("pwwkew")'},
                {'label': "bbbbb", 'onclick': 'loadPreset("bbbbb")'}
            ],
            'metrics': [
                {'id': "metric-left", 'label': "Left Pointer", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-right", 'label': "Right Pointer", 'color': "text-cyan-400", 'initial': "0"},
                {'id': "metric-window-len", 'label': "Window Length", 'color': "text-amber-400", 'initial': "1"},
                {'id': "metric-max-len", 'label': "Max Length", 'color': "text-purple-400", 'initial': "1"}
            ],
            'visual_html': '<div id="sliding-window-visual" class="w-full flex items-center justify-center gap-2 py-4 overflow-x-auto"></div>',
            'initial_narration': 'Click "Next Step" to trace the sliding window expanding and jumping.',
            'pseudocode_lines': [
                "val lastSeen = IntArray(128) { -1 }; var left = 0; var maxLen = 0",
                "for (right in s.indices) {",
                "    val ch = s[right].code",
                "    if (lastSeen[ch] >= left) left = lastSeen[ch] + 1",
                "    lastSeen[ch] = right",
                "    maxLen = maxOf(maxLen, right - left + 1)",
                "}",
                "return maxLen"
            ],
            'js_code': """
    let initialData = "abcabcbb";

    function buildSteps(str) {
      steps = [];
      const lastSeen = {};
      let left = 0;
      let maxLen = 0;

      steps.push({
        left: 0,
        right: 0,
        winLen: 0,
        maxLen: 0,
        str: str,
        line: 1,
        narration: `Initialized window for string "${str}".`
      });

      for (let right = 0; right < str.length; right++) {
        const ch = str[right];
        let jumped = false;
        if (lastSeen[ch] !== undefined && lastSeen[ch] >= left) {
          left = lastSeen[ch] + 1;
          jumped = true;
        }
        lastSeen[ch] = right;
        const winLen = right - left + 1;
        maxLen = Math.max(maxLen, winLen);

        steps.push({
          left: left,
          right: right,
          winLen: winLen,
          maxLen: maxLen,
          str: str,
          char: ch,
          jumped: jumped,
          line: jumped ? 4 : 6,
          narration: jumped 
            ? `Duplicate '${ch}' detected inside window! Left jumped to ${left}. New window: "${str.substring(left, right + 1)}".`
            : `Expanded right to '${ch}' (idx ${right}). Valid window: "${str.substring(left, right + 1)}" (len ${winLen}). Max=${maxLen}.`
        });
      }

      steps.push({
        left: left,
        right: str.length - 1,
        winLen: (str.length > 0) ? (str.length - left) : 0,
        maxLen: maxLen,
        str: str,
        line: 8,
        narration: `Traversal complete! Longest substring without duplicates has length ${maxLen}.`
      });
    }

    function loadPreset(str) {
      document.getElementById('custom-input').value = str;
      initialData = str;
      stepperReset();
      buildSteps(initialData);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value.trim();
      if (val.length > 0) {
        initialData = val;
        stepperReset();
        buildSteps(initialData);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-left').textContent = step.left;
      document.getElementById('metric-right').textContent = step.right;
      document.getElementById('metric-window-len').textContent = step.winLen;
      document.getElementById('metric-max-len').textContent = step.maxLen;
""",
            'js_render_visual': """
      const container = document.getElementById('sliding-window-visual');
      container.innerHTML = '';

      for (let i = 0; i < step.str.length; i++) {
        const isInside = (i >= step.left && i <= step.right);
        const isLeft = (i === step.left);
        const isRight = (i === step.right);

        const card = document.createElement('div');
        card.className = 'flex flex-col items-center gap-1';
        card.innerHTML = `
          <div class="w-10 h-10 rounded-lg flex items-center justify-center font-mono font-bold text-sm border transition-all duration-200 ${
            isInside 
              ? 'border-emerald-500 bg-emerald-950/50 text-emerald-300 shadow-md shadow-emerald-500/20' 
              : 'border-slate-800 bg-slate-900 text-slate-500'
          }">
            ${step.str[i]}
          </div>
          <span class="text-[9px] font-mono text-slate-500">${i}</span>
          <div class="flex gap-0.5 text-[8px] font-mono font-bold">
            ${isLeft ? '<span class="text-emerald-400">L</span>' : ''}
            ${isRight ? '<span class="text-cyan-400">R</span>' : ''}
          </div>
        `;
        container.appendChild(card);
      }
"""
        },
        'approaches': [
            {
                'name': "Sliding Window + HashSet",
                'desc': "Expand right; while duplicate exists, incrementally remove from left.",
                'time': "O(2N)",
                'space': "O(Min(N, M))",
                'code_kotlin': """class Solution {
    fun lengthOfLongestSubstring(s: String): Int {
        val set = HashSet<Char>()
        var left = 0
        var maxLen = 0

        for (right in s.indices) {
            while (set.contains(s[right])) {
                set.remove(s[left])
                left++
            }
            set.add(s[right])
            maxLen = maxOf(maxLen, right - left + 1)
        }
        return maxLen
    }
}""",
                'notes_kotlin': "In the worst case (e.g. 'bbbb'), each character is added and removed once, performing 2N operations.",
                'code_python': """class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        char_set = set()
        left = 0
        max_len = 0
        
        for right in range(len(s)):
            while s[right] in char_set:
                char_set.remove(s[left])
                left += 1
            char_set.add(s[right])
            max_len = max(max_len, right - left + 1)
            
        return max_len""",
                'notes_python': "Clean and canonical sliding window with set. Safe, readable, and widely accepted in interviews.",
                'code_java': """import java.util.*;

class Solution {
    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            while (set.contains(s.charAt(right))) {
                set.remove(s.charAt(left));
                left++;
            }
            set.add(s.charAt(right));
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}""",
                'notes_java': "Works well, but suffers from boxing overhead converting primitive `char` to `Character` objects in HashSet."
            },
            {
                'name': "Better: Sliding Window + HashMap Index Jump",
                'desc': "Store last seen index in HashMap and fast-forward left pointer directly.",
                'time': "O(N)",
                'space': "O(Min(N, M))",
                'code_kotlin': """class Solution {
    fun lengthOfLongestSubstring(s: String): Int {
        val map = HashMap<Char, Int>()
        var left = 0
        var maxLen = 0

        for (right in s.indices) {
            val ch = s[right]
            if (map.containsKey(ch)) {
                left = maxOf(left, map[ch]!! + 1)
            }
            map[ch] = right
            maxLen = maxOf(maxLen, right - left + 1)
        }
        return maxLen
    }
}""",
                'notes_kotlin': "`left = maxOf(left, map[ch]!! + 1)` prevents `left` from ever moving backward when encountering an old duplicate outside the current window.",
                'code_python': """class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        last_seen = {}
        left = 0
        max_len = 0
        
        for right, ch in enumerate(s):
            if ch in last_seen and last_seen[ch] >= left:
                left = last_seen[ch] + 1
            last_seen[ch] = right
            max_len = max(max_len, right - left + 1)
            
        return max_len""",
                'notes_python': "Checks `last_seen[ch] >= left` to ensure the previous occurrence lies inside the current active window.",
                'code_java': """import java.util.*;

class Solution {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            if (map.containsKey(ch)) {
                left = Math.max(left, map.get(ch) + 1);
            }
            map.put(ch, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}""",
                'notes_java': "Replaces the inner while-loop with direct O(1) index jumping. Exactly N iterations total."
            },
            {
                'name': "Optimal: Direct-Mapped ASCII Array (Zero Heap Allocation)",
                'desc': "Use a fixed 128-element primitive integer array instead of HashMap.",
                'time': "O(N)",
                'space': "O(1) (Fixed 128 ints)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: Direct-Mapped ASCII Array Index Jumping
     * Time: O(N) - Strict single linear pass
     * Space: O(1) - Fixed 128-element IntArray (ASCII range)
     */
    fun lengthOfLongestSubstring(s: String): Int {
        val lastSeen = IntArray(128) { -1 }
        var left = 0
        var maxLen = 0

        for (right in s.indices) {
            val code = s[right].code
            if (lastSeen[code] >= left) {
                left = lastSeen[code] + 1
            }
            lastSeen[code] = right
            val currentLen = right - left + 1
            if (currentLen > maxLen) {
                maxLen = currentLen
            }
        }

        return maxLen
    }
}""",
                'notes_kotlin': "`IntArray(128)` directly accesses ASCII indices without hash function computation or object boxing. Maximum performance.",
                'code_python': """class Solution:
    # Optimal: Direct-Mapped Array / Hash Table
    # Time: O(N) - Strict single pass
    # Space: O(Min(N, M)) - Fixed alphabet size
    def lengthOfLongestSubstring(self, s: str) -> int:
        last_seen = [-1] * 128
        left = 0
        max_len = 0
        
        for right in range(len(s)):
            code = ord(s[right])
            if code < 128 and last_seen[code] >= left:
                left = last_seen[code] + 1
            if code < 128:
                last_seen[code] = right
            
            cur_len = right - left + 1
            if cur_len > max_len:
                max_len = cur_len
                
        return max_len""",
                'notes_python': "Using `ord(s[right])` with a 128-element array bypasses Python dictionary hashing overhead for ASCII characters.",
                'code_java': """import java.util.Arrays;

class Solution {
    /**
     * Optimal: Direct-Mapped 128 ASCII Primitive Array
     * Time: O(N) - Exactly N iterations
     * Space: O(1) - Constant 128 ints on thread stack
     */
    public int lengthOfLongestSubstring(String s) {
        int[] lastSeen = new int[128];
        Arrays.fill(lastSeen, -1);
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            if (lastSeen[ch] >= left) {
                left = lastSeen[ch] + 1;
            }
            lastSeen[ch] = right;
            int curLen = right - left + 1;
            if (curLen > maxLen) {
                maxLen = curLen;
            }
        }

        return maxLen;
    }
}""",
                'notes_java': "The production gold standard in Java. Zero garbage collection pressure, zero boxing, sub-millisecond execution."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun lengthOfLongestSubstring(s: String): Int {
        val map = HashMap<Char, Int>()
        var left = 0; var maxLen = 0
        for (right in s.indices) {
            val ch = s[right]
            if (map.containsKey(ch)) left = maxOf(left, map[ch]!! + 1)
            map[ch] = right
            maxLen = maxOf(maxLen, right - left + 1)
        }
        return maxLen
    }
}""",
            'python_boilerplate': """class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        last_seen = {}
        left = 0
        max_len = 0
        for right, ch in enumerate(s):
            if ch in last_seen and last_seen[ch] >= left:
                left = last_seen[ch] + 1
            last_seen[ch] = right
            max_len = max(max_len, right - left + 1)
        return max_len""",
            'java_boilerplate': """class Solution {
    public int lengthOfLongestSubstring(String s) {
        int[] last = new int[128];
        java.util.Arrays.fill(last, -1);
        int left = 0, maxLen = 0;
        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            if (last[ch] >= left) left = last[ch] + 1;
            last[ch] = right;
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}""",
            'js_test_runner': """
      const testCases = [
        { input: "abcabcbb", expected: 3 },
        { input: "bbbbb", expected: 1 },
        { input: "pwwkew", expected: 3 },
        { input: "", expected: 0 },
        { input: "au", expected: 2 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        const last = {};
        let l = 0, res = 0;
        for (let r = 0; r < tc.input.length; r++) {
          const ch = tc.input[r];
          if (last[ch] !== undefined && last[ch] >= l) l = last[ch] + 1;
          last[ch] = r;
          res = Math.max(res, r - l + 1);
        }

        const passed = (res === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: s="${tc.input}"</span>
          <span>${passed ? '&#10004; PASSED (len=' + res + ')' : '&#10008; FAILED (expected ' + tc.expected + ', got ' + res + ')'}</span>
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
            'phase1': "I clarify: can the string contain spaces, symbols, and digits? Yes. Can it be empty? Yes, for an empty string the answer is 0. Does substring mean contiguous characters? Yes, unlike subsequence, substring characters must be strictly adjacent.",
            'phase2': "For time-space trade-offs, checking all substrings is O(N^3). Using a dynamic sliding window with a set shrinks left by 1 in O(2N) time. By using an integer array of size 128 to record each character's last seen index, we can jump left directly to lastSeen[ch] + 1, achieving strict O(N) time and O(1) space (128 ASCII ints).",
            'phase3': "I allocate int[] lastSeen initialized to -1, left = 0, maxLen = 0. In a loop over right from 0 to s.length - 1, if lastSeen[char] >= left, I update left = lastSeen[char] + 1. Then I record lastSeen[char] = right, update maxLen = max(maxLen, right - left + 1), and return maxLen.",
            'phase4': "Dry run edge cases: with s = '', loop does not execute, returns 0. With 'abba': at index 0 ('a'), L=0. Index 1 ('b'), L=0. Index 2 ('b'), L jumps to 2. Index 3 ('a'), 'a' was seen at index 0, but since 0 < left (which is 2), left remains at 2. Window is 'ba', length 2. The condition lastSeen[ch] >= left prevents left from backtracking.",
            'senior_followup': "Senior Scalability: What if the characters are UTF-8 unicode code points (1.1 million possible glyphs)? A fixed 128-element array is no longer sufficient; we would switch to an LRU cache or sparse HashMap with a 64-bit integer hash to keep memory bounded."
        },
        'quiz': [
            {
                'question': "In the string 'abba', what would happen at the second 'a' (index 3) if you omitted the check `lastSeen[ch] >= left`?",
                'options': [
                    "A runtime IndexOutOfBoundsException.",
                    "Left would move backward from index 2 back to index 1, yielding an invalid window with duplicate 'b's.",
                    "The string would be cleared automatically.",
                    "The loop would terminate early."
                ],
                'correct_index': 1,
                'explanation': "The first 'a' is at index 0. If you blindly set left = lastSeen['a'] + 1 = 1, left moves backward from its current position 2, pulling the duplicate 'b' back into the window."
            },
            {
                'question': "What is the space complexity when using a 128-element integer array for standard ASCII strings?",
                'options': [
                    "O(N) where N is the length of the string.",
                    "O(1) constant space because the array size is fixed at 128 regardless of string length.",
                    "O(N^2) space.",
                    "O(log N) space."
                ],
                'correct_index': 1,
                'explanation': "A fixed-size array of 128 integers occupies exactly 512 bytes on memory, completely independent of whether the input string has 10 characters or 10 million characters."
            }
        ]
    },

    # ---------------- 0027: MAX CONSECUTIVE ONES III ----------------
    {
        'num': 27,
        'lc': 1004,
        'title': "Max Consecutive Ones III",
        'category': "Sliding Window",
        'difficulty': "Medium",
        'lc_slug': "max-consecutive-ones-iii",
        'verbatim_statement': """<p>Given a binary array <code>nums</code> and an integer <code>k</code>, return <em>the maximum number of consecutive <code>1</code>'s in the array if you can flip at most <code>k</code> <code>0</code>'s</em>.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2', 'output': '6 ([1,1,1,0,0,1,1,1,1,1,1] flipping index 5 and 10)'},
            {'title': "Example 2", 'input': 'nums = [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1], k = 3', 'output': '10'}
        ],
        'constraints': [
            "1 <= nums.length <= 10^5",
            "nums[i] is either 0 or 1.",
            "0 <= k <= nums.length"
        ],
        'plain_english': "Rephrase the problem: Find the longest contiguous subarray that contains at most k zeroes. Use a sliding window [left..right]. Expand right, counting zeroes. Whenever zeroes exceed k, shrink left until zeroes <= k. Maintain max window size.",
        'boundary_checks': {
            'min_base': "<code>k = 0</code> &rarr; Standard longest consecutive 1s problem (no flips allowed).",
            'best_exit': "<code>k >= nums.length</code> &rarr; Can flip entire array, returns N.",
            'worst_scan': "100,000 alternating [0,1,0,1...] &rarr; Window expands and contracts smoothly in O(N)."
        },
        'mental_model': {
            'analogy_title': "The Subway Turnstile Token Pass",
            'analogy_text': "Imagine walking down a long train corridor where 1s are open doors and 0s are locked doors. You hold exactly k 'pass tokens'. As your lead scout (right pointer) advances, every locked door costs 1 token. When you run out of tokens (zeroes > k), your caboose (left pointer) must move forward; whenever the caboose passes a locked door, you reclaim that token for the scout to use further down the train.",
            'aha_moment': "'Flip at most k zeroes' == 'Find longest subarray containing at most k zeroes'. A sliding window tracks `zeroCount <= k` effortlessly.",
            'trigger': "Whenever asked to find maximum length subarray with at most k invalid/modified elements &rarr; Variable Sliding Window.",
            'sisters': [
                {'name': "Max Consecutive Ones", 'lc': "LC #485"},
                {'name': "Fruit Into Baskets", 'lc': "LC #904"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N^2) Time, O(1) Space",
            'naive_text': "Check every subarray (i, j), count zeroes, update maximum if zeroes <= k. For N = 100,000, takes 5 billion operations.",
            'wasted_work': "Re-counting zeroes across overlapping subarrays instead of adjusting running count at window edges.",
            'optimal_complexity': "O(N) Time, O(1) Space",
            'optimal_text': "Both left and right pointers traverse the array at most once. Running zeroCount updated in O(1) time.",
            'core_invariant': "Window `[left..right]` contains at most `k` zeroes at the end of each right-expansion cycle.",
            'table_rows': [
                {'name': "1. Brute Force Subarrays", 'time': "O(N^2)", 'space': "O(1)", 'note': "Exhaustive pair checks; TLE on large arrays."},
                {'name': "2. Standard Shrinking Window", 'time': "O(2N)", 'space': "O(1)", 'note': "While zeroCount > k, increment left. Canonical."},
                {'name': "★ 3. Non-Shrinking Window", 'time': "O(N)", 'space': "O(1)", 'note': "Window never shrinks; slides forward at max width."}
            ]
        },
        'trace_table': {
            'input_desc': 'nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2',
            'columns': ["Right", "Val", "Zero Count", "Left Action", "Window [L..R]", "Window Len", "Max Len"],
            'rows': [
                ["0..2", "1,1,1", "0", "None", "[0..2]", "3", "3"],
                ["3", "0", "1", "None", "[0..3]", "4", "4"],
                ["4", "0", "2", "None", "[0..4]", "5", "5"],
                ["5", "0", "3", "Shrink left past first 0 &rarr; L=4", "[4..5]", "2", "5"],
                ["6..9", "1,1,1,1", "2", "None", "[4..9]", "6", "6"],
                ["10", "0", "3", "Shrink left past zero &rarr; L=5", "[5..10]", "6", "6"]
            ],
            'takeaway': "Maximum length of 6 achieved."
        },
        'stepper': {
            'title': "Sliding Window with Zero Token Budget Tracking",
            'default_input': '1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0',
            'presets': [
                {'label': "Example 1 (k=2)", 'onclick': 'loadPreset([1,1,1,0,0,0,1,1,1,1,0], 2)'},
                {'label': "k=0 (Pure 1s)", 'onclick': 'loadPreset([1,1,0,1,1,1,0,1], 0)'},
                {'label': "Sparse 1s (k=1)", 'onclick': 'loadPreset([0,0,1,1,0,0], 1)'}
            ],
            'metrics': [
                {'id': "metric-left", 'label': "Left Pointer", 'color': "text-emerald-400", 'initial': "0"},
                {'id': "metric-right", 'label': "Right Pointer", 'color': "text-cyan-400", 'initial': "0"},
                {'id': "metric-zeroes", 'label': "Zeroes in Window", 'color': "text-amber-400", 'initial': "0"},
                {'id': "metric-max-len", 'label': "Max Window Length", 'color': "text-purple-400", 'initial': "0"}
            ],
            'visual_html': '<div id="binary-window-visual" class="w-full flex items-center justify-center gap-1.5 py-4 overflow-x-auto"></div>',
            'initial_narration': 'Click "Next Step" to trace the sliding window token budget.',
            'pseudocode_lines': [
                "var left = 0; var zeroCount = 0; var maxLen = 0",
                "for (right in nums.indices) {",
                "    if (nums[right] == 0) zeroCount++",
                "    while (zeroCount > k) {",
                "        if (nums[left] == 0) zeroCount--",
                "        left++",
                "    }",
                "    maxLen = maxOf(maxLen, right - left + 1)",
                "}",
                "return maxLen"
            ],
            'js_code': """
    let initialData = [1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0];
    let kBudget = 2;

    function buildSteps(arr, kVal) {
      if (kVal !== undefined) kBudget = kVal;
      steps = [];
      let left = 0;
      let zeroes = 0;
      let maxLen = 0;

      steps.push({
        left: 0,
        right: 0,
        zeroes: 0,
        maxLen: 0,
        arr: arr,
        line: 1,
        narration: `Initialized sliding window with flip budget k = ${kBudget}.`
      });

      for (let right = 0; right < arr.length; right++) {
        if (arr[right] === 0) zeroes++;

        let shrank = false;
        while (zeroes > kBudget) {
          shrank = true;
          if (arr[left] === 0) zeroes--;
          left++;
        }

        const winLen = right - left + 1;
        maxLen = Math.max(maxLen, winLen);

        steps.push({
          left: left,
          right: right,
          zeroes: zeroes,
          maxLen: maxLen,
          arr: arr,
          shrank: shrank,
          line: shrank ? 6 : 8,
          narration: shrank
            ? `Zeroes exceeded budget (${kBudget})! Shrank left to ${left}. Window [${left}..${right}] has ${zeroes} zeroes.`
            : `Expanded right to index ${right} (val=${arr[right]}). Zeroes=${zeroes}/${kBudget}. Window length=${winLen}. Max=${maxLen}.`
        });
      }

      steps.push({
        left: left,
        right: arr.length - 1,
        zeroes: zeroes,
        maxLen: maxLen,
        arr: arr,
        line: 10,
        narration: `Finished scan! Maximum consecutive 1s achievable with at most ${kBudget} flips = ${maxLen}.`
      });
    }

    function loadPreset(arr, kVal) {
      document.getElementById('custom-input').value = arr.join(', ');
      initialData = arr;
      kBudget = kVal;
      stepperReset();
      buildSteps(initialData, kBudget);
      renderStepper();
    }

    function handleCustomInput() {
      const val = document.getElementById('custom-input').value;
      const arr = val.split(',').map(s => parseInt(s.trim())).filter(n => n === 0 || n === 1);
      if (arr.length > 0) {
        initialData = arr;
        stepperReset();
        buildSteps(initialData, kBudget);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-left').textContent = step.left;
      document.getElementById('metric-right').textContent = step.right;
      document.getElementById('metric-zeroes').textContent = `${step.zeroes} / ${kBudget}`;
      document.getElementById('metric-max-len').textContent = step.maxLen;
""",
            'js_render_visual': """
      const container = document.getElementById('binary-window-visual');
      container.innerHTML = '';

      step.arr.forEach((val, idx) => {
        const isInside = (idx >= step.left && idx <= step.right);
        const isLeft = (idx === step.left);
        const isRight = (idx === step.right);

        const card = document.createElement('div');
        card.className = 'flex flex-col items-center gap-1';
        
        let borderCls = 'border-slate-800 bg-slate-900 text-slate-500';
        if (isInside) {
          borderCls = (val === 1) 
            ? 'border-emerald-500 bg-emerald-950/40 text-emerald-300 font-bold'
            : 'border-amber-500 bg-amber-950/40 text-amber-300 font-bold';
        }

        card.innerHTML = `
          <div class="w-9 h-9 rounded-lg flex items-center justify-center font-mono text-sm border ${borderCls}">
            ${val}
          </div>
          <span class="text-[9px] font-mono text-slate-500">${idx}</span>
          <div class="flex gap-0.5 text-[8px] font-mono font-bold">
            ${isLeft ? '<span class="text-emerald-400">L</span>' : ''}
            ${isRight ? '<span class="text-cyan-400">R</span>' : ''}
          </div>
        `;
        container.appendChild(card);
      });
"""
        },
        'approaches': [
            {
                'name': "Brute Force All Subarrays",
                'desc': "Examine every pair (i, j), count zeroes, check if count <= k.",
                'time': "O(N^2)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun longestOnes(nums: IntArray, k: Int): Int {
        var maxLen = 0
        for (i in nums.indices) {
            var zeroes = 0
            for (j in i until nums.size) {
                if (nums[j] == 0) zeroes++
                if (zeroes <= k) {
                    maxLen = maxOf(maxLen, j - i + 1)
                } else {
                    break
                }
            }
        }
        return maxLen
    }
}""",
                'notes_kotlin': "Quadratic O(N^2). Times out on arrays of length 10^5.",
                'code_python': """class Solution:
    def longestOnes(self, nums: list[int], k: int) -> int:
        max_len = 0
        n = len(nums)
        for i in range(n):
            zeroes = 0
            for j in range(i, n):
                if nums[j] == 0:
                    zeroes += 1
                if zeroes <= k:
                    max_len = max(max_len, j - i + 1)
                else:
                    break
        return max_len""",
                'notes_python': "Nested iteration across large binary arrays is too slow for production constraints.",
                'code_java': """class Solution {
    public int longestOnes(int[] nums, int k) {
        int maxLen = 0;
        for (int i = 0; i < nums.length; i++) {
            int zeroes = 0;
            for (int j = i; j < nums.length; j++) {
                if (nums[j] == 0) zeroes++;
                if (zeroes <= k) {
                    maxLen = Math.max(maxLen, j - i + 1);
                } else {
                    break;
                }
            }
        }
        return maxLen;
    }
}""",
                'notes_java': "Exhaustive baseline that demonstrates understanding of the problem constraints."
            },
            {
                'name': "Better: Canonical Shrinking Sliding Window",
                'desc': "Expand right, increment zero count. While zeroes > k, increment left.",
                'time': "O(2N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    fun longestOnes(nums: IntArray, k: Int): Int {
        var left = 0
        var zeroCount = 0
        var maxLen = 0

        for (right in nums.indices) {
            if (nums[right] == 0) zeroCount++

            while (zeroCount > k) {
                if (nums[left] == 0) zeroCount--
                left++
            }

            maxLen = maxOf(maxLen, right - left + 1)
        }
        return maxLen
    }
}""",
                'notes_kotlin': "The canonical interview solution. Both left and right advance at most N times, running in O(N) time with O(1) space.",
                'code_python': """class Solution:
    def longestOnes(self, nums: list[int], k: int) -> int:
        left = 0
        zero_count = 0
        max_len = 0
        
        for right in range(len(nums)):
            if nums[right] == 0:
                zero_count += 1
                
            while zero_count > k:
                if nums[left] == 0:
                    zero_count -= 1
                left += 1
                
            max_len = max(max_len, right - left + 1)
            
        return max_len""",
                'notes_python': "Clear, standard Python sliding window. Easy to explain in under 2 minutes during an interview.",
                'code_java': """class Solution {
    public int longestOnes(int[] nums, int k) {
        int left = 0;
        int zeroCount = 0;
        int maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) zeroCount++;

            while (zeroCount > k) {
                if (nums[left] == 0) zeroCount--;
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}""",
                'notes_java': "Zero heap allocation. Fast primitive operations."
            },
            {
                'name': "Optimal: Non-Shrinking Max-Window",
                'desc': "Window never shrinks; it only expands or slides forward at maximum achieved width.",
                'time': "O(N)",
                'space': "O(1)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: Non-Shrinking Sliding Window
     * Time: O(N) - Single loop pass without inner while
     * Space: O(1) - Constant auxiliary storage
     */
    fun longestOnes(nums: IntArray, k: Int): Int {
        var left = 0
        var right = 0
        var kRemaining = k

        while (right < nums.size) {
            if (nums[right] == 0) {
                kRemaining--
            }
            if (kRemaining < 0) {
                if (nums[left] == 0) {
                    kRemaining++
                }
                left++
            }
            right++
        }

        return right - left
    }
}""",
                'notes_kotlin': "Staff-level optimization: when invalid, left shifts once alongside right, maintaining maximum window size without shrinking. Returns `right - left` directly.",
                'code_python': """class Solution:
    # Optimal: Non-Shrinking Sliding Window
    # Time: O(N) - Strictly N iterations
    # Space: O(1) - Pure primitive variables
    def longestOnes(self, nums: list[int], k: int) -> int:
        left = 0
        right = 0
        
        for right in range(len(nums)):
            if nums[right] == 0:
                k -= 1
            if k < 0:
                if nums[left] == 0:
                    k += 1
                left += 1
                
        return len(nums) - left""",
                'notes_python': "Eliminates the inner while loop. When k < 0, sliding the window forward preserves the maximum size found so far.",
                'code_java': """class Solution {
    /**
     * Optimal: Non-Shrinking Sliding Window
     * Time: O(N) - Exactly N iterations
     * Space: O(1)
     */
    public int longestOnes(int[] nums, int k) {
        int left = 0;
        int right = 0;

        for (right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                k--;
            }
            if (k < 0) {
                if (nums[left] == 0) {
                    k++;
                }
                left++;
            }
        }

        return right - left;
    }
}""",
                'notes_java': "The most compact O(N) solution possible. Highly impressive when presented as a senior follow-up."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun longestOnes(nums: IntArray, k: Int): Int {
        var left = 0; var zeroCount = 0; var maxLen = 0
        for (right in nums.indices) {
            if (nums[right] == 0) zeroCount++
            while (zeroCount > k) {
                if (nums[left] == 0) zeroCount--
                left++
            }
            maxLen = maxOf(maxLen, right - left + 1)
        }
        return maxLen
    }
}""",
            'python_boilerplate': """class Solution:
    def longestOnes(self, nums: list[int], k: int) -> int:
        left = 0; zeroes = 0; max_len = 0
        for right in range(len(nums)):
            if nums[right] == 0: zeroes += 1
            while zeroes > k:
                if nums[left] == 0: zeroes -= 1
                left += 1
            max_len = max(max_len, right - left + 1)
        return max_len""",
            'java_boilerplate': """class Solution {
    public int longestOnes(int[] nums, int k) {
        int left = 0, zeroes = 0, maxLen = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) zeroes++;
            while (zeroes > k) {
                if (nums[left] == 0) zeroes--;
                left++;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}""",
            'js_test_runner': """
      const testCases = [
        { nums: [1,1,1,0,0,0,1,1,1,1,0], k: 2, expected: 6 },
        { nums: [0,0,1,1,0,0,1,1,1,0,1,1,0,0,0,1,1,1,1], k: 3, expected: 10 },
        { nums: [1,1,1,1], k: 0, expected: 4 },
        { nums: [0,0,0], k: 1, expected: 1 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        let l = 0, z = 0, res = 0;
        for (let r = 0; r < tc.nums.length; r++) {
          if (tc.nums[r] === 0) z++;
          while (z > tc.k) {
            if (tc.nums[l] === 0) z--;
            l++;
          }
          res = Math.max(res, r - l + 1);
        }

        const passed = (res === tc.expected);
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: nums=[${tc.nums.join(',')}] (k=${tc.k})</span>
          <span>${passed ? '&#10004; PASSED (len=' + res + ')' : '&#10008; FAILED (expected ' + tc.expected + ', got ' + res + ')'}</span>
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
            'phase1': "I clarify: can k be 0? Yes, meaning we find the longest sequence of original 1s without any flips. Can k be greater than the array length? Yes, in which case we can flip all elements and return nums.length.",
            'phase2': "For space-time strategy, the naive approach tests every subarray in O(N^2). By reframing the problem as 'find the longest subarray with at most k zeroes', we can use a sliding window [left..right]. The right pointer expands greedily while tracking the zero count. If zeroes exceed k, we contract left until the invariant is restored. Both pointers move forward, giving O(N) time and O(1) space.",
            'phase3': "I initialize left = 0, zeroCount = 0, and maxLen = 0. In a loop over right from 0 to nums.length - 1: if nums[right] is 0, I increment zeroCount. While zeroCount > k, if nums[left] is 0, I decrement zeroCount, and increment left. Then I update maxLen = max(maxLen, right - left + 1). Finally, return maxLen.",
            'phase4': "Dry running edge cases: with [0,0,0] and k=1: right=0 zeroes=1, len=1. At right=1 zeroes=2 > 1, left advances to 1, zeroes=1, len=1. Returns 1. With [1,1,1,1] and k=0: zeroCount stays 0, window expands to 4, returns 4. Complexities hold.",
            'senior_followup': "Senior Follow-up: Can we solve it in a single pass without the inner while loop? Yes! If we allow the window to never shrink below its maximum achieved width, we can simply slide left forward by 1 whenever zeroCount > k, achieving strict N iterations and returning right - left directly."
        },
        'quiz': [
            {
                'question': "What is the key conceptual equivalence that unlocks this problem?",
                'options': [
                    "Flipping at most k zeroes is mathematically identical to finding the longest subarray containing at most k zeroes.",
                    "Binary numbers must always be processed with bitwise XOR.",
                    "Zeroes must be collected into a Min-Heap.",
                    "Dynamic programming with a 2D matrix is required."
                ],
                'correct_index': 0,
                'explanation': "Instead of actually mutating elements or simulating flips, any contiguous subarray containing at most k zeroes can be turned into an all-1s subarray by flipping those exact zeroes."
            },
            {
                'question': "Why is the time complexity of the canonical sliding window O(N) even though there is an inner while loop?",
                'options': [
                    "Because the inner loop runs only once.",
                    "Because the left pointer only moves forward and can move at most N times across the entire execution.",
                    "Because compiler loop unrolling optimizes it to O(1).",
                    "Because the array is binary."
                ],
                'correct_index': 1,
                'explanation': "Both `left` and `right` start at 0 and only move rightward. Since `left` never resets or moves backward, it traverses at most N steps total, making the amortized complexity strictly O(2N) = O(N)."
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

    print(f"Generating Cluster 1: {len(cluster1_problems)} problems...")
    for p in cluster1_problems:
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
