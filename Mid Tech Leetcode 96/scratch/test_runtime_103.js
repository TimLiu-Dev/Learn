
load('assets/lesson-runtime.js');

print('=== TESTING DYNAMIC TRANSPILATION & EXECUTION IN JSC ===\n');

// 1. Two Sum (Kotlin, Python, Java)
print('--- 1. Testing Two Sum ---');
var env = LessonRuntime._createRuntimeEnv();
var twoSumPy = `class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        seen = {}
        for i, x in enumerate(nums):
            comp = target - x
            if comp in seen:
                return [seen[comp], i]
            seen[x] = i
        return []`;

var transpiledPy = LessonRuntime._transpilePython(twoSumPy, 'twoSum');
var factoryPy = new Function('__sub', '__sort', '__HashSet', '__HashMap', '__ArrayList', '__StringBuilder', 'buildString', 'IntArray', 'BooleanArray', 'ord', 'tuple', 'return ' + transpiledPy.code + ';');
var fnPy = factoryPy(env.__sub, env.__sort, env.__HashSet, env.__HashMap, env.__ArrayList, env.__StringBuilder, env.buildString, env.IntArray, env.BooleanArray, env.ord, env.tuple);

var res1 = fnPy([2, 7, 11, 15], 9);
print('  Python twoSum([2, 7, 11, 15], 9) -> ' + JSON.stringify(res1));
if (res1[0] !== 0 || res1[1] !== 1) throw new Error('twoSum failed');

// 2. Kadane's Maximum Subarray
print('\n--- 2. Testing Kadane Maximum Subarray ---');
var kadaneKt = `class Solution {
    fun maxSubArray(nums: IntArray): Int {
        var currSum = nums[0]
        var maxSum = nums[0]
        for (i in 1 until nums.size) {
            currSum = maxOf(nums[i], currSum + nums[i])
            maxSum = maxOf(maxSum, currSum)
        }
        return maxSum
    }
}`;

var transpiledKt = LessonRuntime._transpileJVM(kadaneKt, 'maxSubArray');
var factoryKt = new Function('__sub', '__sort', '__HashSet', '__HashMap', '__ArrayList', '__StringBuilder', 'buildString', 'IntArray', 'BooleanArray', 'ord', 'tuple', 'return ' + transpiledKt.code + ';');
var fnKt = factoryKt(env.__sub, env.__sort, env.__HashSet, env.__HashMap, env.__ArrayList, env.__StringBuilder, env.buildString, env.IntArray, env.BooleanArray, env.ord, env.tuple);

var res2 = fnKt([-2, 1, -3, 4, -1, 2, 1, -5, 4]);
print('  Kotlin maxSubArray([-2, 1, -3, 4, -1, 2, 1, -5, 4]) -> ' + res2);
if (res2 !== 6) throw new Error('maxSubArray failed: got ' + res2);

// 3. Longest Palindromic Substring
print('\n--- 3. Testing Longest Palindromic Substring ---');
var palKt = `class Solution {
    fun longestPalindrome(s: String): String {
        if (s.length <= 1) return s
        var start = 0
        var maxLen = 0
        fun expand(l: Int, r: Int) {
            var left = l
            var right = r
            while (left >= 0 && right < s.length && s[left] == s[right]) {
                left--
                right++
            }
            val len = right - left - 1
            if (len > maxLen) {
                maxLen = len
                start = left + 1
            }
        }
        for (i in s.indices) {
            expand(i, i)
            expand(i, i + 1)
        }
        return s.substring(start, start + maxLen)
    }
}`;

var transpiledPal = LessonRuntime._transpileJVM(palKt, 'longestPalindrome');
var factoryPal = new Function('__sub', '__sort', '__HashSet', '__HashMap', '__ArrayList', '__StringBuilder', 'buildString', 'IntArray', 'BooleanArray', 'ord', 'tuple', 'return ' + transpiledPal.code + ';');
var fnPal = factoryPal(env.__sub, env.__sort, env.__HashSet, env.__HashMap, env.__ArrayList, env.__StringBuilder, env.buildString, env.IntArray, env.BooleanArray, env.ord, env.tuple);

var res3 = fnPal('babad');
print('  Kotlin longestPalindrome("babad") -> ' + res3);
if (res3 !== 'bab' && res3 !== 'aba') throw new Error('longestPalindrome failed');

print('\n🎉 ALL DYNAMIC EXECUTION TESTS PASSED IN JAVASCRIPTCORE!');
