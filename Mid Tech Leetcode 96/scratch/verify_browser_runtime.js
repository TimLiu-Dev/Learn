// Dynamic Test of Lesson 1 and Lesson 23 Code Sandbox in JSC
var runtimeScript = read('assets/lesson-runtime.js');
eval(runtimeScript);

print('=== STARTING DYNAMIC RUNTIME VERIFICATION ===\n');

// 1. LESSON 1: CONTAINS DUPLICATE
print('--- 1. Testing Lesson 0001 Contains Duplicate ---');
var lc217Tests = [
  { input: [[1, 2, 3, 1]], expected: true },
  { input: [[1, 2, 3, 4]], expected: false },
  { input: [[1, 1, 1, 3, 3, 4, 3, 2, 4, 2]], expected: true },
  { input: [[42]], expected: false },
  { input: [[7, 7, 1, 2, 3]], expected: true }
];

var lc217Solutions = {
  kotlin: `class Solution {
    fun containsDuplicate(nums: IntArray): Boolean {
        val seen = hashSetOf<Int>()
        for (num in nums) {
            if (!seen.add(num)) return true
        }
        return false
    }
}`,
  python: `class Solution:
    def containsDuplicate(self, nums: list[int]) -> bool:
        seen = set()
        for num in nums:
            if num in seen:
                return True
            seen.add(num)
        return False`,
  java: `import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (!seen.add(num)) return true;
        }
        return false;
    }
}`
};

var env = LessonRuntime._createRuntimeEnv();

['kotlin', 'python', 'java'].forEach(function(lang) {
  var code = lc217Solutions[lang];
  var transpiled = lang === 'python' ? LessonRuntime._transpilePython(code, 'containsDuplicate') : LessonRuntime._transpileJVM(code, 'containsDuplicate');
  var factory = new Function('__sub', '__sort', '__HashSet', '__HashMap', '__ArrayList', '__StringBuilder', 'buildString', 'IntArray', 'BooleanArray', 'ord', 'tuple', 'return ' + transpiled.code + ';');
  var fn = factory(env.__sub, env.__sort, env.__HashSet, env.__HashMap, env.__ArrayList, env.__StringBuilder, env.buildString, env.IntArray, env.BooleanArray, env.ord, env.tuple);
  var passCount = 0;
  for (var i = 0; i < lc217Tests.length; i++) {
    var tc = lc217Tests[i];
    var actual = fn.apply(null, JSON.parse(JSON.stringify(tc.input)));
    if (actual === tc.expected) passCount++;
  }
  print('  ' + lang.toUpperCase() + ': ' + passCount + '/' + lc217Tests.length + ' test cases passed');
  if (passCount !== lc217Tests.length) throw new Error('LC217 ' + lang + ' failed tests');
});

// 2. LESSON 23: GROUP ANAGRAMS
print('\n--- 2. Testing Lesson 0023 Group Anagrams ---');
var lc49Tests = [
  {
    input: [["eat", "tea", "tan", "ate", "nat", "bat"]],
    expected: [["bat"], ["nat", "tan"], ["ate", "eat", "tea"]]
  },
  {
    input: [[""]],
    expected: [[""]]
  },
  {
    input: [["a"]],
    expected: [["a"]]
  },
  {
    input: [["rat", "car", "pig"]],
    expected: [["rat"], ["car"], ["pig"]]
  }
];

function anagramComparator(actual, expected) {
  if (!Array.isArray(actual) || !Array.isArray(expected)) return false;
  if (actual.length !== expected.length) return false;
  function norm(groups) {
    return groups.map(function(g) {
      return g.slice().sort().join(',');
    }).sort().join('|');
  }
  return norm(actual) === norm(expected);
}

var lc49Solutions = {
  kotlin: `class Solution {
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val map = HashMap<String, MutableList<String>>()
        for (s in strs) {
            val count = IntArray(26)
            for (ch in s) {
                count[ch - 'a']++
            }
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
}`,
  python: `class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        from collections import defaultdict
        groups = defaultdict(list)
        for s in strs:
            count = [0] * 26
            for ch in s:
                count[ord(ch) - ord('a')] += 1
            groups[tuple(count)].append(s)
        return list(groups.values())`,
  java: `import java.util.*;

class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            int[] count = new int[26];
            for (int i = 0; i < s.length(); i++) {
                count[s.charAt(i) - 'a']++;
            }
            StringBuilder sb = new StringBuilder();
            for (int c : count) {
                sb.append('#').append(c);
            }
            String key = sb.toString();
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }
}`
};

['kotlin', 'python', 'java'].forEach(function(lang) {
  var code = lc49Solutions[lang];
  var transpiled = lang === 'python' ? LessonRuntime._transpilePython(code, 'groupAnagrams') : LessonRuntime._transpileJVM(code, 'groupAnagrams');
  var factory = new Function('__sub', '__sort', '__HashSet', '__HashMap', '__ArrayList', '__StringBuilder', 'buildString', 'IntArray', 'BooleanArray', 'ord', 'tuple', 'return ' + transpiled.code + ';');
  var fn = factory(env.__sub, env.__sort, env.__HashSet, env.__HashMap, env.__ArrayList, env.__StringBuilder, env.buildString, env.IntArray, env.BooleanArray, env.ord, env.tuple);
  var passCount = 0;
  for (var i = 0; i < lc49Tests.length; i++) {
    var tc = lc49Tests[i];
    var actual = fn.apply(null, JSON.parse(JSON.stringify(tc.input)));
    if (anagramComparator(actual, tc.expected)) passCount++;
  }
  print('  ' + lang.toUpperCase() + ': ' + passCount + '/' + lc49Tests.length + ' test cases passed');
  if (passCount !== lc49Tests.length) throw new Error('LC49 ' + lang + ' failed tests');
});

print('\n🎉 ALL RUNTIME VERIFICATION TESTS PASSED 100%!');
