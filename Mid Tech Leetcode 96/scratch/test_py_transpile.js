
load('assets/lesson-runtime.js');

var py = `class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        for i, x in enumerate(nums):
            pass`;

var transpiled = LessonRuntime._transpilePython(py, 'twoSum');
print('Transpiled code:');
print(transpiled.code);
