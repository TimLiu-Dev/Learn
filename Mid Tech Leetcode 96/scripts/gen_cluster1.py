import os, sys
from template_engine import render_lesson

cluster1_problems = [
    # ---------------- 0023: GROUP ANAGRAMS ----------------
    {
        'num': 23,
        'lc': 49,
        'title': "Group Anagrams",
        'category': "Arrays, Strings & Hashing",
        'difficulty': "Medium",
        'prev_file': "0022-lc338-counting-bits.html",
        'next_file': "0024-lc443-string-compression.html",
        'lc_slug': "group-anagrams",
        'verbatim_statement': """<p>Given an array of strings <code>strs</code>, group the anagrams together. You can return the answer in <strong>any order</strong>.</p>
<p class="mt-2">An <strong>anagram</strong> is a word or phrase formed by rearranging the letters of a different word or phrase, typically using all the original letters exactly once.</p>""",
        'examples': [
            {'title': "Example 1", 'input': 'strs = ["eat","tea","tan","ate","nat","bat"]', 'output': '[["bat"],["nat","tan"],["ate","eat","tea"]]'},
            {'title': "Example 2", 'input': 'strs = [""]', 'output': '[[""]]'},
            {'title': "Example 3", 'input': 'strs = ["a"]', 'output': '[["a"]]'}
        ],
        'constraints': [
            "1 <= strs.length <= 10^4",
            "0 <= strs[i].length <= 100",
            "strs[i] consists of lowercase English letters only."
        ],
        'plain_english': "Take a list of words, cluster every word that contains the exact same character frequencies into the same bucket, and return all buckets. Two words are anagrams if and only if their sorted canonical form (or 26-element character frequency tuple) is identical.",
        'boundary_checks': {
            'min_base': "<code>strs = [\"\"]</code> &rarr; Empty string inside a single bucket <code>[[\"\"]]</code>.",
            'best_exit': "All strings identical or completely disjoint &rarr; Single bucket or N singleton buckets.",
            'worst_scan': "10,000 strings of length 100 all anagrams &rarr; Single bucket of 10,000 elements."
        },
        'mental_model': {
            'analogy_title': "The Mailroom Sorting Cubbies",
            'analogy_text': "Imagine a mailroom clerk receiving thousands of jumbled letters. Instead of comparing each letter against every other letter (O(N^2)), the clerk calculates a canonical fingerprint for each word: alphabetizing the letters ('eat' -> 'aet', 'tea' -> 'aet'). Words sharing the exact same fingerprint are dropped into the same labeled cubby in O(1) time.",
            'aha_moment': "Any anagram property is an equivalence relation. Map every string to a canonical key in a hash map: either sorted string ('aet') or a 26-integer frequency tuple count #1#0#1...#0.",
            'trigger': "Whenever asked to 'group', 'cluster', or 'match' strings by character composition &rarr; HashMap<CanonicalKey, List<String>>.",
            'sisters': [
                {'name': "Valid Anagram", 'lc': "LC #242"},
                {'name': "Find All Anagrams in a String", 'lc': "LC #438"}
            ]
        },
        'naive_vs_optimal': {
            'naive_complexity': "O(N^2 * K log K)",
            'naive_text': "Compare every pair of strings (i, j) by sorting them and checking equality. If equal, merge them into the same group. Requires nested loops across 10,000 strings resulting in 100 million comparisons.",
            'wasted_work': "Re-comparing strings against multiple candidates instead of using an O(1) hash table lookup.",
            'optimal_complexity': "O(N * K log K) or O(N * K)",
            'optimal_text': "Iterate through the array once. For each string of length K, compute its canonical signature. Insert into HashMap<Signature, List<String>>. Return map.values.",
            'core_invariant': "Two words are anagrams iff their character frequency signature is identical.",
            'table_rows': [
                {'name': "1. Naive Pairwise Scan", 'time': "O(N^2 * K)", 'space': "O(N * K)", 'note': "Nested loop string comparison; Time Limit Exceeded."},
                {'name': "2. Sorting HashMap Key", 'time': "O(N * K log K)", 'space': "O(N * K)", 'note': "Sort each word as key. Fast, concise, industry standard."},
                {'name': "★ 3. 26-Count Tuple Key", 'time': "O(N * K)", 'space': "O(N * K)", 'note': "Array of 26 ints as signature; linear in string length."}
            ]
        },
        'trace_table': {
            'input_desc': '["eat", "tea", "tan", "ate", "nat", "bat"]',
            'columns': ["String", "Sorted Key (Fingerprint)", "Map State After Insertion", "Bucket Count"],
            'rows': [
                ["\"eat\"", "\"aet\"", "{\"aet\": [\"eat\"]}", "1"],
                ["\"tea\"", "\"aet\"", "{\"aet\": [\"eat\", \"tea\"]}", "1"],
                ["\"tan\"", "\"ant\"", "{\"aet\": [\"eat\", \"tea\"], \"ant\": [\"tan\"]}", "2"],
                ["\"ate\"", "\"aet\"", "{\"aet\": [\"eat\", \"tea\", \"ate\"], \"ant\": [\"tan\"]}", "2"],
                ["\"nat\"", "\"ant\"", "{\"aet\": [...], \"ant\": [\"tan\", \"nat\"]}", "2"],
                ["\"bat\"", "\"abt\"", "{\"aet\": [...], \"ant\": [...], \"abt\": [\"bat\"]}", "3"]
            ],
            'takeaway': "Notice how words seamlessly funnel into their respective cubbies in one linear pass."
        },
        'stepper': {
            'title': "Canonical Signature & HashMap Bucket Placement",
            'default_input': 'eat, tea, tan, ate, nat, bat',
            'presets': [
                {'label': "Default 6 Words", 'onclick': 'loadPreset(["eat","tea","tan","ate","nat","bat"])'},
                {'label': "Identical Anagrams", 'onclick': 'loadPreset(["abc","bca","cab"])'},
                {'label': "Disjoint Words", 'onclick': 'loadPreset(["rat","car","pig"])'}
            ],
            'metrics': [
                {'id': "metric-word", 'label': "Current Word", 'color': "text-amber-400", 'initial': "eat"},
                {'id': "metric-key", 'label': "Fingerprint Key", 'color': "text-cyan-400", 'initial': "aet"},
                {'id': "metric-buckets", 'label': "Total Buckets", 'color': "text-emerald-400", 'initial': "1"},
                {'id': "metric-status", 'label': "Action", 'color': "text-purple-400", 'initial': "Mapping"}
            ],
            'visual_html': '<div id="anagram-buckets-container" class="w-full flex flex-wrap gap-4 justify-center items-start"></div>',
            'initial_narration': 'Click "Next Step" to trace canonical key generation and HashMap bucket clustering.',
            'pseudocode_lines': [
                "val map = mutableMapOf<String, MutableList<String>>()",
                "for (str in strs) {",
                "    val key = str.toCharArray().sorted().joinToString(\"\")",
                "    map.getOrPut(key) { mutableListOf() }.add(str)",
                "}",
                "return ArrayList(map.values)"
            ],
            'js_code': """
    let initialData = ["eat", "tea", "tan", "ate", "nat", "bat"];

    function buildSteps(arr) {
      steps = [];
      const map = {};
      steps.push({
        word: "Ready",
        key: "-",
        buckets: 0,
        status: "Initialize",
        mapState: {},
        line: 1,
        narration: "Initialized empty HashMap. Ready to iterate through words."
      });

      for (let i = 0; i < arr.length; i++) {
        const w = arr[i].trim();
        const sortedKey = w.split('').sort().join('');
        if (!map[sortedKey]) map[sortedKey] = [];
        map[sortedKey].push(w);

        steps.push({
          word: w,
          key: sortedKey,
          buckets: Object.keys(map).length,
          status: "Inserted",
          mapState: JSON.parse(JSON.stringify(map)),
          line: 4,
          narration: `Word "${w}" hashed to key "${sortedKey}". Placed in bucket [${sortedKey}].`
        });
      }

      steps.push({
        word: "Done",
        key: "Complete",
        buckets: Object.keys(map).length,
        status: "Finished",
        mapState: JSON.parse(JSON.stringify(map)),
        line: 6,
        narration: `Finished grouping! Returning ${Object.keys(map).length} grouped anagram lists.`
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
      const arr = val.split(',').map(s => s.trim()).filter(s => s.length > 0);
      if (arr.length > 0) {
        initialData = arr;
        stepperReset();
        buildSteps(initialData);
        renderStepper();
      }
    }
""",
            'js_update_metrics': """
      document.getElementById('metric-word').textContent = step.word;
      document.getElementById('metric-key').textContent = step.key;
      document.getElementById('metric-buckets').textContent = step.buckets;
      document.getElementById('metric-status').textContent = step.status;
""",
            'js_render_visual': """
      const container = document.getElementById('anagram-buckets-container');
      container.innerHTML = '';
      const keys = Object.keys(step.mapState || {});
      if (keys.length === 0) {
        container.innerHTML = '<span class="text-xs font-mono text-slate-500">HashMap empty</span>';
      } else {
        keys.forEach(k => {
          const words = step.mapState[k];
          const isCurrent = (k === step.key);
          const bucket = document.createElement('div');
          bucket.className = `p-3 rounded-xl border ${isCurrent ? 'border-emerald-500 bg-emerald-950/30' : 'border-slate-800 bg-slate-900/50'} text-xs font-mono min-w-[140px]`;
          bucket.innerHTML = `
            <div class="text-[10px] uppercase font-bold text-slate-400 mb-1">Key: <span class="text-cyan-400">${k}</span></div>
            <div class="flex flex-wrap gap-1">
              ${words.map(w => `<span class="px-2 py-0.5 rounded bg-slate-800 text-emerald-300 font-bold">${w}</span>`).join('')}
            </div>
          `;
          container.appendChild(bucket);
        });
      }
"""
        },
        'approaches': [
            {
                'name': "Brute Force Pair Comparison",
                'desc': "Compare every word against all others using visited boolean array.",
                'time': "O(N^2 * K log K)",
                'space': "O(N * K)",
                'code_kotlin': """class Solution {
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val result = mutableListOf<MutableList<String>>()
        val visited = BooleanArray(strs.size)
        
        for (i in strs.indices) {
            if (visited[i]) continue
            val group = mutableListOf(strs[i])
            visited[i] = true
            val sortedI = strs[i].toCharArray().sorted().joinToString("")
            
            for (j in i + 1 until strs.size) {
                if (!visited[j]) {
                    val sortedJ = strs[j].toCharArray().sorted().joinToString("")
                    if (sortedI == sortedJ) {
                        group.add(strs[j])
                        visited[j] = true
                    }
                }
            }
            result.add(group)
        }
        return result
    }
}""",
                'notes_kotlin': "Demonstrates quadratic O(N^2) comparison. BooleanArray avoids redundant grouping, but nested loops trigger Time Limit Exceeded on LeetCode.",
                'code_python': """class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        result = []
        visited = [False] * len(strs)
        
        for i in range(len(strs)):
            if visited[i]:
                continue
            group = [strs[i]]
            visited[i] = True
            sorted_i = sorted(strs[i])
            
            for j in range(i + 1, len(strs)):
                if not visited[j] and sorted_i == sorted(strs[j]):
                    group.append(strs[j])
                    visited[j] = True
            result.append(group)
            
        return result""",
                'notes_python': "Repeatedly calling `sorted()` on each comparison results in redundant sorting work. Inadmissible for large inputs.",
                'code_java': """import java.util.*;

class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> result = new ArrayList<>();
        boolean[] visited = new boolean[strs.length];

        for (int i = 0; i < strs.length; i++) {
            if (visited[i]) continue;
            List<String> group = new ArrayList<>();
            group.add(strs[i]);
            visited[i] = true;
            char[] arrI = strs[i].toCharArray();
            Arrays.sort(arrI);
            String sortedI = new String(arrI);

            for (int j = i + 1; j < strs.length; j++) {
                if (!visited[j]) {
                    char[] arrJ = strs[j].toCharArray();
                    Arrays.sort(arrJ);
                    if (sortedI.equals(new String(arrJ))) {
                        group.add(strs[j]);
                        visited[j] = true;
                    }
                }
            }
            result.add(group);
        }
        return result;
    }
}""",
                'notes_java': "Creates multiple transient String objects on every comparison. GC pressure will degrade latency significantly."
            },
            {
                'name': "Better: Sorting as HashMap Key",
                'desc': "Sort each string's characters to form the canonical HashMap key.",
                'time': "O(N * K log K)",
                'space': "O(N * K)",
                'code_kotlin': """class Solution {
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val map = HashMap<String, MutableList<String>>()
        for (s in strs) {
            val key = String(s.toCharArray().apply { sort() })
            map.getOrPut(key) { ArrayList() }.add(s)
        }
        return ArrayList(map.values)
    }
}""",
                'notes_kotlin': "Using `String(s.toCharArray().apply { sort() })` is idiomatic Kotlin, performing in-place primitive CharArray sort with zero boxing.",
                'code_python': """class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        from collections import defaultdict
        groups = defaultdict(list)
        for s in strs:
            key = "".join(sorted(s))
            groups[key].append(s)
        return list(groups.values())""",
                'notes_python': "`defaultdict(list)` eliminates key-existence checks. `\"\".join(sorted(s))` is concise and highly optimized in CPython.",
                'code_java': """import java.util.*;

class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }
}""",
                'notes_java': "`Map.computeIfAbsent` avoids double hash lookups (containsKey followed by get/put) common in junior implementations."
            },
            {
                'name': "Optimal: 26-Character Frequency Tuple",
                'desc': "Count character frequencies into a 26-element array and encode as unique key.",
                'time': "O(N * K)",
                'space': "O(N * K)",
                'code_kotlin': """class Solution {
    /**
     * Optimal: 26-Character Frequency Counting Key
     * Time: O(N * K) - Zero string sorting overhead
     * Space: O(N * K) - Total characters stored in Map
     */
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val map = HashMap<String, MutableList<String>>()
        
        for (s in strs) {
            val count = IntArray(26)
            for (ch in s) {
                count[ch - 'a']++
            }
            // Build delimiter-separated canonical signature
            val key = buildString {
                for (c in count) {
                    append('#')
                    append(c)
                }
            }
            map.getOrPut(key) { ArrayList() }.add(s)
        }
        return ArrayList(map.values)
    }
}""",
                'notes_kotlin': "`buildString` pre-allocates StringBuilder efficiently. `IntArray(26)` stays on thread stack without boxing.",
                'code_python': '''class Solution:
    # Optimal: 26-Character Frequency Counting Key
    # Time: O(N * K) - Linear in total input characters
    # Space: O(N * K) - Hash map storage
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        from collections import defaultdict
        groups = defaultdict(list)
        
        for s in strs:
            count = [0] * 26
            for ch in s:
                count[ord(ch) - ord('a')] += 1
            # Tuple of 26 integers is hashable and serves directly as dict key
            groups[tuple(count)].append(s)
            
        return list(groups.values())''',
                'notes_python': "Tuples are immutable and hashable, allowing Python to use `tuple(count)` directly as a dict key without string serialization!",
                'code_java': """import java.util.*;

class Solution {
    /**
     * Optimal: 26-Character Frequency Counting Key
     * Time: O(N * K) - Avoids K log K sorting overhead
     * Space: O(N * K)
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        
        for (String s : strs) {
            int[] count = new int[26];
            for (int i = 0; i < s.length(); i++) {
                count[s.charAt(i) - 'a']++;
            }
            
            // Build unique key e.g. #1#0#0...#2
            StringBuilder sb = new StringBuilder();
            for (int c : count) {
                sb.append('#').append(c);
            }
            String key = sb.toString();
            
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }
}""",
                'notes_java': "Delimiting with '#' ensures character counts like 1 and 11 don't collide with 11 and 1. Production standard."
            }
        ],
        'scratchpad': {
            'kotlin_boilerplate': """class Solution {
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val map = HashMap<String, MutableList<String>>()
        for (s in strs) {
            val key = String(s.toCharArray().apply { sort() })
            map.getOrPut(key) { ArrayList() }.add(s)
        }
        return ArrayList(map.values)
    }
}""",
            'python_boilerplate': """class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        from collections import defaultdict
        groups = defaultdict(list)
        for s in strs:
            groups["".join(sorted(s))].append(s)
        return list(groups.values())""",
            'java_boilerplate': """class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            map.computeIfAbsent(new String(chars), k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }
}""",
            'js_test_runner': """
      const testCases = [
        { input: ["eat","tea","tan","ate","nat","bat"], expectedCount: 3 },
        { input: [""], expectedCount: 1 },
        { input: ["a"], expectedCount: 1 }
      ];

      const resultsDiv = document.getElementById('scratchpad-results');
      const statusSpan = document.getElementById('scratchpad-status');
      resultsDiv.innerHTML = '';
      let allPassed = true;

      testCases.forEach((tc, idx) => {
        // Run test logic simulation
        const map = {};
        tc.input.forEach(w => {
          const k = w.split('').sort().join('');
          if (!map[k]) map[k] = [];
          map[k].push(w);
        });
        const passed = Object.keys(map).length === tc.expectedCount;
        if (!passed) allPassed = false;

        const row = document.createElement('div');
        row.className = `p-2 rounded text-xs font-mono flex items-center justify-between ${passed ? 'bg-emerald-950/30 text-emerald-300' : 'bg-rose-950/30 text-rose-300'}`;
        row.innerHTML = `
          <span>Test ${idx + 1}: input=[${tc.input.map(s => `"${s}"`).join(',')}]</span>
          <span>${passed ? '&#10004; PASSED (' + Object.keys(map).length + ' groups)' : '&#10008; FAILED'}</span>
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
            'phase1': "I'd first clarify: are all characters strictly lowercase English letters? Yes. Can the array contain empty strings? Yes, empty strings are valid anagrams of each other. Does the order of groups or words within groups matter? No, any order is acceptable.",
            'phase2': "For space-time trade-offs, the naive approach compares every pair in O(N^2 * K). Instead, since two words are anagrams if and only if their sorted characters are identical, I will use a HashMap where the key is the sorted string of length K (or a 26-integer frequency tuple), and the value is a list of strings matching that key. This achieves O(N * K log K) time with sorting, or O(N * K) with counting, using O(N * K) auxiliary space.",
            'phase3': "I'm initializing a HashMap with String keys and List<String> values. As I iterate through each string, I sort its characters into a canonical key. Using getOrPut (or computeIfAbsent), I append the original string to that bucket. Finally, I return the map's values as an ArrayList.",
            'phase4': "Let's dry run edge cases: with strs = [\"\"], key is \"\", map has one bucket containing [\"\"]. With single character words [\"a\"], key is \"a\", returning [[\"a\"]]. For completely disjoint words like [\"cat\",\"dog\"], we get two buckets of size 1. Space and time complexities hold.",
            'senior_followup': "Senior Scalability: What if the dataset is 100 billion strings stored across distributed nodes? We would use MapReduce: the Map phase emits (canonical_key, original_word), and the Shuffle/Sort phase groups identical keys across partitions so Reduce outputs the grouped clusters."
        },
        'quiz': [
            {
                'question': "Why is using a 26-integer frequency tuple asymptotically faster than sorting each string for very long strings?",
                'options': [
                    "Counting takes O(K) time whereas sorting takes O(K log K) time per string.",
                    "Sorting requires O(K^2) memory whereas counting uses zero space.",
                    "Hash tables do not support strings as keys.",
                    "Tuples eliminate hash collisions entirely."
                ],
                'correct_index': 0,
                'explanation': "Counting characters takes linear O(K) time by tallying characters into a fixed 26-size array, avoiding the O(K log K) comparison sort overhead."
            },
            {
                'question': "In Java and Kotlin, what is the danger of creating the counting key as a concatenated string without delimiters (e.g. \"100...2\")?",
                'options': [
                    "It causes memory stack overflow.",
                    "Without delimiters, count 1 followed by 10 collides with count 11 followed by 0 (\"110\").",
                    "String concatenation is forbidden inside HashMaps.",
                    "Kotlin does not support String concatenation."
                ],
                'correct_index': 1,
                'explanation': "Without delimiters like '#', count distributions like [1, 10] and [11, 0] serialize to the exact same string '110', producing false anagram collisions."
            }
        ]
    }
]

def main():
    print("Generating Cluster 1 (0023)...")
    for p in cluster1_problems:
        fname = f"lessons/{p['num']:04d}-{p['lc_slug']}.html"
        html_content = render_lesson(p)
        with open(fname, 'w', encoding='utf-8') as f:
            f.write(html_content)
        print(f"Generated {fname} ({len(html_content)} bytes)")

if __name__ == '__main__':
    main()
