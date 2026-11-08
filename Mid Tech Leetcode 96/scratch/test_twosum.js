
const fs = require = (function() {
  return {
    readFileSync: function(p) { return readFile(p); }
  };
})();

// Load lesson-runtime.js
load('assets/lesson-runtime.js');

const starter = {
  kotlin: "class Solution {\n    fun twoSum(nums: IntArray, target: Int): IntArray {\n        val seen = HashMap<Int, Int>()\n        for (i in nums.indices) {\n            val comp = target - nums[i]\n            if (seen.containsKey(comp)) return intArrayOf(seen[comp]!!, i)\n            seen[nums[i]] = i\n        }\n        return intArrayOf()\n    }\n}",
  python: "class Solution:\n    def twoSum(self, nums: list[int], target: int) -> list[int]:\n        seen = {}\n        for i, num in enumerate(nums):\n            comp = target - num\n            if comp in seen:\n                return [seen[comp], i]\n            seen[num] = i\n        return []",
  java: "class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> seen = new HashMap<>();\n        for (int i = 0; i < nums.length; i++) {\n            int comp = target - nums[i];\n            if (seen.containsKey(comp)) return new int[] { seen.get(comp), i };\n            seen.put(nums[i], i);\n        }\n        return new int[] {};\n    }\n}"
};

const tc = [
  { input: { nums: [2, 7, 11, 15], target: 9 }, expected: [0, 1] },
  { input: { nums: [3, 2, 4], target: 6 }, expected: [1, 2] },
  { input: { nums: [3, 3], target: 6 }, expected: [0, 1] }
];

['kotlin', 'python', 'java'].forEach(lang => {
  const code = starter[lang];
  const transpiled = LessonRuntime.transpile(code, lang);
  const runner = LessonRuntime._createRuntimeEnv(transpiled);
  tc.forEach((t, i) => {
    const res = runner.twoSum(t.input.nums, t.input.target);
    const sortedRes = (res || []).slice().sort();
    const sortedExp = t.expected.slice().sort();
    const ok = sortedRes.length === 2 && sortedRes[0] === sortedExp[0] && sortedRes[1] === sortedExp[1];
    print(lang + ' TC' + (i+1) + ': ' + (ok ? 'PASS' : 'FAIL -> ' + JSON.stringify(res)));
  });
});
