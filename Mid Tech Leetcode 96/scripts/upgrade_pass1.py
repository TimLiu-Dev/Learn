"""
Upgrades Lessons 0001 to 0023 to the 2027 Golden Master v2+ standard:
1. Restores missing <canvas id="whiteboard-canvas"> in 0002, 0003, 0004
2. Removes duplicate legacy scratchpads (sandbox-textarea, scratchpad-editor, code-editor) in 0002..0022
3. Injects Section 2.5 (Non-Code Solution Blueprint & Language-Agnostic Pseudocode) between Section 2 and Section 3 across 0001..0023
4. Updates/injects Quick-Jump Anchor Bar with Section 2.5 link in sticky header
5. Replaces all leetcode.com/problems/ links with neetcode.io/problems/
6. Restores full typed parameter signatures in starterStubs and generates problem-specific 3 hints + 4 length-balanced diagnostic MCQs for 0002..0022
"""

import glob
import re
import os
import json
import html
import sys

sys.path.append('scripts')
from lesson_builder import (
    build_stubs,
    build_hints,
    build_quiz,
    build_non_code_solution,
)

PASS1_RICH_META = {
    1: {
        "lc": 217, "title": "Contains Duplicate", "category": "Arrays, Strings & Hashing", "difficulty": "Easy",
        "lc_slug": "contains-duplicate", "method": "containsDuplicate",
        "min_base": "nums = [42] (single element -> false in O(1))",
        "best_exit": "nums = [7, 7, 1, 2] (duplicate at index 1 -> early exit true)",
        "worst_scan": "nums = [1..10^5] all distinct -> full O(N) scan and O(N) HashSet",
        "analogy_title": "The VIP Nightclub Bouncer & Stamp Checklist",
        "aha_moment": "Stop searching forward into the array; check backward in O(1) against a HashSet of seen elements.",
        "trigger": "Detecting duplicates or uniqueness in an unsorted sequence in O(N) time.",
        "naive_complexity": "O(N²) Time, O(1) Space",
        "wasted_work": "nested pairwise comparisons re-verifying elements already proven distinct",
        "optimal_complexity": "O(N) Time, O(N) Space",
        "optimal_text": "Maintain a HashSet of seen integers; for each num, if !seen.add(num) return true immediately.",
        "core_invariant": "seen contains every unique integer in nums[0..i-1]; any collision proves a duplicate exists.",
        "takeaway": "seen.add(num) checks membership and inserts in a single O(1) hash bucket probe.",
        "pseudocode_lines": [
            "val seen = hashSetOf<Int>()",
            "for (num in nums) {",
            "  if (!seen.add(num)) return true",
            "}",
            "return false"
        ]
    },
    2: {
        "lc": 1, "title": "Two Sum", "category": "Arrays, Strings & Hashing", "difficulty": "Easy",
        "lc_slug": "two-sum", "method": "twoSum",
        "min_base": "nums = [3, 3], target = 6 -> returns [0, 1] without overwriting needed index",
        "best_exit": "nums = [2, 7, 11, 15], target = 9 -> matches on index 1 in 2 steps",
        "worst_scan": "Matching pair sits at the last two indices n-2 and n-1 -> full O(N) pass",
        "analogy_title": "The Puzzle-Piece Claim Ticket Desk",
        "aha_moment": "For each number x, we already know the exact partner we need: complement = target - x. Look up complement in a HashMap<Value, Index>!",
        "trigger": "Finding a pair of elements summing or relating to a target in an unsorted array.",
        "naive_complexity": "O(N²) Time, O(1) Space",
        "wasted_work": "scanning the remaining array for target - nums[i] on every index i",
        "optimal_complexity": "O(N) Time, O(N) Space",
        "optimal_text": "Store seen values and their indices in a HashMap; before inserting nums[i], check if target - nums[i] is already in the map.",
        "core_invariant": "seen maps each value in nums[0..i-1] to its index; checking before inserting prevents using the same element twice.",
        "takeaway": "One-pass HashMap lookup reduces the inner O(N) partner search to an O(1) hash lookup.",
        "pseudocode_lines": [
            "val seen = HashMap<Int, Int>()",
            "for (i in nums.indices) {",
            "  val comp = target - nums[i]",
            "  if (comp in seen) return intArrayOf(seen[comp]!!, i)",
            "  seen[nums[i]] = i",
            "}",
            "return intArrayOf()"
        ],
        "tests": [
            {"input": {"nums": [2, 7, 11, 15], "target": 9}, "expected": [0, 1]},
            {"input": {"nums": [3, 2, 4], "target": 6}, "expected": [1, 2]},
            {"input": {"nums": [3, 3], "target": 6}, "expected": [0, 1]}
        ]
    },
    3: {
        "lc": 1768, "title": "Merge Strings Alternately", "category": "Arrays, Strings & Hashing", "difficulty": "Easy",
        "lc_slug": "merge-strings-alternately", "method": "mergeAlternately",
        "min_base": "word1 = \"a\", word2 = \"b\" -> single alternating step produces \"ab\"",
        "best_exit": "Equal length words -> both pointers exhaust simultaneously at n",
        "worst_scan": "Highly asymmetric lengths (1 vs 100) -> tail of longer word appended after alternation",
        "analogy_title": "The Highway Zipper Merge",
        "aha_moment": "Use a single index i from 0 up to max(len1, len2); append word1[i] if i < len1, then append word2[i] if i < len2.",
        "trigger": "Interleaving two or more sequences of potentially unequal lengths.",
        "naive_complexity": "O((M + N)²) Time with immutable String += concatenation",
        "wasted_work": "re-allocating and copying immutable String objects on every character append",
        "optimal_complexity": "O(M + N) Time, O(1) Aux Space",
        "optimal_text": "Pre-size a StringBuilder of capacity M + N and interleave characters using a single loop up to maxOf(M, N).",
        "core_invariant": "At step i, all available characters from word1[0..i] and word2[0..i] have been appended in alternating order.",
        "takeaway": "A mutable StringBuilder buffer avoids quadratic string copying overhead.",
        "pseudocode_lines": [
            "val sb = StringBuilder(word1.length + word2.length)",
            "val maxLen = maxOf(word1.length, word2.length)",
            "for (i in 0 until maxLen) {",
            "  if (i < word1.length) sb.append(word1[i])",
            "  if (i < word2.length) sb.append(word2[i])",
            "}",
            "return sb.toString()"
        ],
        "tests": [
            {"input": {"word1": "abc", "word2": "pqr"}, "expected": "apbqcr"},
            {"input": {"word1": "ab", "word2": "pqrs"}, "expected": "apbqrs"},
            {"input": {"word1": "abcd", "word2": "pq"}, "expected": "apbqcd"}
        ]
    },
    4: {
        "lc": 283, "title": "Move Zeroes", "category": "Two Pointers", "difficulty": "Easy",
        "lc_slug": "move-zeroes", "method": "moveZeroes",
        "min_base": "nums = [0] -> single element remains [0] with zero swaps",
        "best_exit": "nums = [1, 2, 3] (no zeroes) -> slow and fast stay locked together",
        "worst_scan": "nums = [0, 0, 0, 1] -> non-zero at tail swaps all the way to index 0",
        "analogy_title": "The Snowplow Compactor (Read/Write Pointers)",
        "aha_moment": "Maintain a 'write' (slow) pointer for the next non-zero slot; whenever 'read' (fast) sees a non-zero, swap nums[write] and nums[read] and advance write++.",
        "trigger": "In-place array partitioning or stable filtering with O(1) extra memory.",
        "naive_complexity": "O(N) Time, O(N) Auxiliary Space",
        "wasted_work": "allocating a separate result array to copy non-zeroes and trailing zeroes",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Use same-direction slow (insertPos) and fast (read) pointers to compact non-zero elements in place in a single pass.",
        "core_invariant": "All elements in nums[0..slow-1] are non-zero in their original relative order, and all elements in nums[slow..fast-1] are zero.",
        "takeaway": "Swapping nums[slow] with nums[fast] preserves relative order of non-zeroes while pushing zeroes to the tail in O(1) space.",
        "pseudocode_lines": [
            "var slow = 0",
            "for (fast in nums.indices) {",
            "  if (nums[fast] != 0) {",
            "    val temp = nums[slow]; nums[slow] = nums[fast]; nums[fast] = temp",
            "    slow++",
            "  }",
            "}"
        ],
        "tests": [
            {"input": {"nums": [0, 1, 0, 3, 12]}, "expected": [1, 3, 12, 0, 0]},
            {"input": {"nums": [0]}, "expected": [0]}
        ]
    },
    5: {
        "lc": 125, "title": "Valid Palindrome", "category": "Two Pointers", "difficulty": "Easy",
        "lc_slug": "valid-palindrome", "method": "isPalindrome",
        "min_base": "s = \" \" (only whitespace/punctuation) -> pointers cross without mismatch, returns true",
        "best_exit": "s = \"race a car\" -> exits early as soon as 'e' != 'a'",
        "worst_scan": "s = \"A man, a plan, a canal: Panama\" -> full inward convergence to center",
        "analogy_title": "Two Inspectors Pinching Inward",
        "aha_moment": "Pinch left and right pointers toward the center in place, skipping non-alphanumeric chars and comparing lowercase chars in O(1) memory.",
        "trigger": "Checking symmetry or pair constraints from both ends of a sequence.",
        "naive_complexity": "O(N) Time, O(N) Auxiliary Space",
        "wasted_work": "allocating regex-filtered and reversed string copies on the heap",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Advance left++ and right-- inward while skipping non-alphanumeric characters, returning false on the first case-insensitive mismatch.",
        "core_invariant": "All alphanumeric characters outside [left..right] have already been matched as symmetric mirrors.",
        "takeaway": "Inner skip loops must re-check left < right to prevent IndexOutOfBounds on strings of pure punctuation.",
        "pseudocode_lines": [
            "var left = 0; var right = s.length - 1",
            "while (left < right) {",
            "  while (left < right && !s[left].isLetterOrDigit()) left++",
            "  while (left < right && !s[right].isLetterOrDigit()) right--",
            "  if (s[left].lowercaseChar() != s[right].lowercaseChar()) return false",
            "  left++; right--",
            "}",
            "return true"
        ],
        "tests": [
            {"input": {"s": "A man, a plan, a canal: Panama"}, "expected": True},
            {"input": {"s": "race a car"}, "expected": False},
            {"input": {"s": " "}, "expected": True}
        ]
    },
    6: {
        "lc": 121, "title": "Best Time to Buy and Sell Stock", "category": "Sliding Window", "difficulty": "Easy",
        "lc_slug": "best-time-to-buy-and-sell-stock", "method": "maxProfit",
        "min_base": "prices = [5] (single day) -> no sell day after buy day, returns 0",
        "best_exit": "Monotonically decreasing [7, 6, 4, 3, 1] -> minPrice updates every day, maxProfit stays 0",
        "worst_scan": "prices = [7, 1, 5, 3, 6, 4] -> tracks valley 1 and finds peak spread 6 - 1 = 5",
        "analogy_title": "The Time-Traveler's Deepest Valley Tracker",
        "aha_moment": "To maximize profit selling on day i, you only care about the single lowest price seen on any day before i (minPrice).",
        "trigger": "Maximizing a directional difference A[j] - A[i] where j > i.",
        "naive_complexity": "O(N²) Time, O(1) Space",
        "wasted_work": "testing every (buyDay, sellDay) pair in nested loops",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Track running minPrice and maxProfit in one pass: update minPrice = min(minPrice, price) and maxProfit = max(maxProfit, price - minPrice).",
        "core_invariant": "At day i, minPrice holds the minimum value in prices[0..i] and maxProfit holds the best profit achievable in prices[0..i].",
        "takeaway": "A single scalar minPrice replaces the entire inner lookback loop.",
        "pseudocode_lines": [
            "var minPrice = Int.MAX_VALUE; var maxProfit = 0",
            "for (price in prices) {",
            "  if (price < minPrice) minPrice = price",
            "  else if (price - minPrice > maxProfit) maxProfit = price - minPrice",
            "}",
            "return maxProfit"
        ],
        "tests": [
            {"input": {"prices": [7, 1, 5, 3, 6, 4]}, "expected": 5},
            {"input": {"prices": [7, 6, 4, 3, 1]}, "expected": 0}
        ]
    },
    7: {
        "lc": 643, "title": "Maximum Average Subarray I", "category": "Sliding Window", "difficulty": "Easy",
        "lc_slug": "maximum-average-subarray-i", "method": "findMaxAverage",
        "min_base": "nums = [5], k = 1 -> initial window sum is 5, returns 5.0",
        "best_exit": "k == nums.size -> single window covers entire array, 0 slides needed",
        "worst_scan": "All negative values [-10, -5, -2, -8], k = 2 -> maxSum must initialize to first window sum, never 0!",
        "analogy_title": "The Boxcar Train Window (+Entering, -Leaving)",
        "aha_moment": "When a window of length k slides 1 step right, the k-1 middle elements don't change! Just add the entering right element and subtract the leaving left element: windowSum += nums[i] - nums[i - k].",
        "trigger": "Computing running sums, averages, or metrics over contiguous subarrays of fixed size k.",
        "naive_complexity": "O(N * k) Time, O(1) Space",
        "wasted_work": "re-summing all k elements from scratch on every window shift",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Sum the first k elements as windowSum and maxSum; slide i from k to n-1 updating windowSum += nums[i] - nums[i - k] and return maxSum.toDouble() / k.",
        "core_invariant": "At step i, windowSum equals the exact sum of the k-element window nums[i - k + 1 .. i].",
        "takeaway": "Deferring division (maxSum.toDouble() / k) until the return statement avoids N floating-point divisions inside the loop.",
        "pseudocode_lines": [
            "var windowSum = 0; for (i in 0 until k) windowSum += nums[i]",
            "var maxSum = windowSum",
            "for (i in k until nums.size) {",
            "  windowSum += nums[i] - nums[i - k]",
            "  if (windowSum > maxSum) maxSum = windowSum",
            "}",
            "return maxSum.toDouble() / k"
        ],
        "tests": [
            {"input": {"nums": [1, 12, -5, -6, 50, 3], "k": 4}, "expected": 12.75},
            {"input": {"nums": [5], "k": 1}, "expected": 5.0}
        ]
    },
    8: {
        "lc": 724, "title": "Find Pivot Index", "category": "Prefix Sum", "difficulty": "Easy",
        "lc_slug": "find-pivot-index", "method": "pivotIndex",
        "min_base": "nums = [2, 1, -1] -> pivot at index 0 where leftSum = 0 == rightSum (1 + -1)",
        "best_exit": "Balance achieved at index 0 -> returns 0 on the very first check",
        "worst_scan": "nums = [1, 2, 3] (no valid pivot) -> scans all N elements and returns -1",
        "analogy_title": "The Seesaw Balance Fulcrum",
        "aha_moment": "Once you know totalSum, you never need a suffix array! At any index i, rightSum is simply totalSum - leftSum - nums[i].",
        "trigger": "Finding an equilibrium index or range sum query on a static array.",
        "naive_complexity": "O(N²) Time, O(1) Space",
        "wasted_work": "re-summing elements to the left and right of i on every index",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Compute totalSum in one pass; then sweep left-to-right tracking leftSum and checking if leftSum == totalSum - leftSum - nums[i] before adding nums[i] to leftSum.",
        "core_invariant": "At the start of iteration i, leftSum equals sum(nums[0..i-1]) and totalSum - leftSum - nums[i] equals sum(nums[i+1..n-1]).",
        "takeaway": "Checking the balance condition BEFORE adding nums[i] to leftSum properly excludes the pivot element itself from both sides.",
        "pseudocode_lines": [
            "val totalSum = nums.sum(); var leftSum = 0",
            "for (i in nums.indices) {",
            "  val rightSum = totalSum - leftSum - nums[i]",
            "  if (leftSum == rightSum) return i",
            "  leftSum += nums[i]",
            "}",
            "return -1"
        ],
        "tests": [
            {"input": {"nums": [1, 7, 3, 6, 5, 6]}, "expected": 3},
            {"input": {"nums": [1, 2, 3]}, "expected": -1},
            {"input": {"nums": [2, 1, -1]}, "expected": 0}
        ]
    },
    9: {
        "lc": 20, "title": "Valid Parentheses", "category": "Stacks, Queues & Monotonic Stacks", "difficulty": "Easy",
        "lc_slug": "valid-parentheses", "method": "isValid",
        "min_base": "Odd length string (s.length % 2 != 0) or starts with closing ')' -> immediate false",
        "best_exit": "First char is closing bracket ']' on empty stack -> returns false on index 0",
        "worst_scan": "All opening brackets \"((((\" -> pushes all N chars; final stack.isEmpty() check returns false",
        "analogy_title": "The Nested Russian Doll LIFO Stack",
        "aha_moment": "The most recently opened bracket must be the very first one closed (LIFO). Push expected closing brackets onto a stack so matching is a simple stack.removeLast() == ch check!",
        "trigger": "Validating nested delimiters, AST tokens, or hierarchical open/close tags.",
        "naive_complexity": "O(N²) Time, O(N) Space",
        "wasted_work": "repeatedly calling String.replace(\"()\", \"\") in a loop until the string stops shrinking",
        "optimal_complexity": "O(N) Time, O(N) Space",
        "optimal_text": "Use an ArrayDeque stack: on opening brackets push the matching closer; on closing brackets return false if stack is empty or popped char != ch; return stack.isEmpty().",
        "core_invariant": "The stack holds the exact sequence of closing brackets required to close all currently open scopes in LIFO order.",
        "takeaway": "Always check stack.isEmpty() at the end—otherwise unclosed prefixes like \"(()\" would falsely pass!",
        "pseudocode_lines": [
            "val stack = ArrayDeque<Char>()",
            "for (ch in s) {",
            "  if (ch == '(') stack.addLast(')')",
            "  else if (ch == '[') stack.addLast(']')",
            "  else if (ch == '{') stack.addLast('}')",
            "  else if (stack.isEmpty() || stack.removeLast() != ch) return false",
            "}",
            "return stack.isEmpty()"
        ],
        "tests": [
            {"input": {"s": "()"}, "expected": True},
            {"input": {"s": "()[]{}"}, "expected": True},
            {"input": {"s": "(]"}, "expected": False}
        ]
    },
    10: {
        "lc": 933, "title": "Number of Recent Calls", "category": "Stacks, Queues & Monotonic Stacks", "difficulty": "Easy",
        "lc_slug": "number-of-recent-calls", "method": "ping",
        "min_base": "First ping(1) -> queue holds [1], returns size 1",
        "best_exit": "Large time jump ping(10000) -> evicts all expired timestamps < 7000 in O(1) amortized time",
        "worst_scan": "3000 consecutive pings within [1..3000] -> queue grows to max bounded size 3001",
        "analogy_title": "The 3000ms Sliding Turnstile Queue",
        "aha_moment": "Because ping timestamps arrive in strictly increasing chronological order, expired timestamps (< t - 3000) always sit at the front of a FIFO Queue!",
        "trigger": "Counting or rate-limiting monotonically ordered stream events within a trailing time window.",
        "naive_complexity": "O(N) per ping, O(N) Total Space",
        "wasted_work": "storing all historical pings forever and scanning the full list on every call",
        "optimal_complexity": "O(1) Amortized Time, O(W) Bounded Space (<= 3001)",
        "optimal_text": "Append t to an ArrayDeque queue, evict queue.removeFirst() while queue.first() < t - 3000, and return queue.size.",
        "core_invariant": "After eviction, the queue contains exclusively timestamps in the valid window [t - 3000, t].",
        "takeaway": "Each timestamp enters and leaves the queue at most once across all calls, yielding O(1) amortized cost per ping.",
        "pseudocode_lines": [
            "val q = ArrayDeque<Int>()",
            "fun ping(t: Int): Int {",
            "  q.addLast(t)",
            "  while (q.first() < t - 3000) q.removeFirst()",
            "  return q.size",
            "}"
        ],
        "tests": [
            {"input": {"t": 1}, "expected": 1},
            {"input": {"t": 100}, "expected": 2},
            {"input": {"t": 3001}, "expected": 3},
            {"input": {"t": 3002}, "expected": 3}
        ]
    },
    11: {
        "lc": 206, "title": "Reverse Linked List", "category": "Linked Lists & System Node Design", "difficulty": "Easy",
        "lc_slug": "reverse-linked-list", "method": "reverseList",
        "min_base": "head = null or single node [1] -> loop zero/one times, returns head intact",
        "best_exit": "Two-node list [1, 2] -> flips arrow in 2 steps to [2, 1]",
        "worst_scan": "5000-node list -> O(N) iterative in-place pointer flip with O(1) memory (no recursion stack overflow)",
        "analogy_title": "The Three-Pointer Train Car Uncoupler",
        "aha_moment": "Before flipping curr.next backward to prev, you MUST save val nextTemp = curr.next so you don't lose the rest of the train!",
        "trigger": "Reversing a singly linked list or sub-segment in place (such as Palindrome Linked List or Reverse Nodes in k-Group).",
        "naive_complexity": "O(N) Time, O(N) Auxiliary Space",
        "wasted_work": "pushing all nodes onto an explicit stack or O(N) recursion call stack",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Maintain prev = null and curr = head; while curr != null, cache nextTemp = curr.next, point curr.next = prev, advance prev = curr, and curr = nextTemp.",
        "core_invariant": "At the start of each step, prev points to the fully reversed prefix list and curr points to the head of the remaining unreversed suffix.",
        "takeaway": "When curr reaches null at the end of the list, prev sits directly on the new head.",
        "pseudocode_lines": [
            "var prev: ListNode? = null; var curr = head",
            "while (curr != null) {",
            "  val nextTemp = curr.next",
            "  curr.next = prev",
            "  prev = curr",
            "  curr = nextTemp",
            "}",
            "return prev"
        ],
        "tests": [
            {"input": {"head": [1, 2, 3, 4, 5]}, "expected": [5, 4, 3, 2, 1]},
            {"input": {"head": [1, 2]}, "expected": [2, 1]},
            {"input": {"head": []}, "expected": []}
        ]
    },
    12: {
        "lc": 21, "title": "Merge Two Sorted Lists", "category": "Linked Lists & System Node Design", "difficulty": "Easy",
        "lc_slug": "merge-two-sorted-lists", "method": "mergeTwoLists",
        "min_base": "list1 = null, list2 = null -> dummy.next is null, returns null cleanly",
        "best_exit": "One list is empty (list1 = null) -> immediately attaches non-empty list2 in O(1)",
        "worst_scan": "Interleaved lists [1, 3, 5] and [2, 4, 6] -> stitches every node one by one, then attaches remainder",
        "analogy_title": "The Dummy Sentinel Zipper Stitcher",
        "aha_moment": "Create a fixed dummy sentinel node so you never need special 'if head is null' branching, and attach the surviving non-null list in O(1) once the other list runs out!",
        "trigger": "Merging, filtering, or rewiring linked lists where the head node might change.",
        "naive_complexity": "O((M + N) log(M + N)) Time, O(M + N) Space",
        "wasted_work": "copying all node values into an array, sorting, and allocating brand-new ListNodes",
        "optimal_complexity": "O(M + N) Time, O(1) Space",
        "optimal_text": "Allocate a single dummy ListNode(-1) and a tail cursor; splice the smaller of l1 and l2 onto tail.next until one list is null, then set tail.next = l1 ?: l2.",
        "core_invariant": "dummy.next..tail contains all merged nodes in non-decreasing sorted order using the original list nodes in place.",
        "takeaway": "Attaching the leftover list tail.next = l1 ?: l2 takes O(1) pointer assignment without looping.",
        "pseudocode_lines": [
            "val dummy = ListNode(-1); var tail = dummy",
            "var p1 = list1; var p2 = list2",
            "while (p1 != null && p2 != null) {",
            "  if (p1.val <= p2.val) { tail.next = p1; p1 = p1.next }",
            "  else { tail.next = p2; p2 = p2.next }",
            "  tail = tail.next!!",
            "}",
            "tail.next = p1 ?: p2",
            "return dummy.next"
        ],
        "tests": [
            {"input": {"list1": [1, 2, 4], "list2": [1, 3, 4]}, "expected": [1, 1, 2, 3, 4, 4]},
            {"input": {"list1": [], "list2": []}, "expected": []},
            {"input": {"list1": [], "list2": [0]}, "expected": [0]}
        ]
    },
    13: {
        "lc": 141, "title": "Linked List Cycle", "category": "Linked Lists & System Node Design", "difficulty": "Easy",
        "lc_slug": "linked-list-cycle", "method": "hasCycle",
        "min_base": "head = null or single node with next = null -> fast?.next is null, returns false immediately",
        "best_exit": "Self-loop at head (pos = 0, length 1) -> fast and slow meet on first iteration",
        "worst_scan": "Long acyclic list of 10^4 nodes -> fast reaches end in N/2 steps with O(1) space",
        "analogy_title": "Floyd's Tortoise and Hare Track Lapping",
        "aha_moment": "On a circular track, a runner moving at 2x speed (Hare) closes the gap on a 1x runner (Tortoise) by exactly 1 node per step—guaranteeing they collide without skipping over each other!",
        "trigger": "Cycle detection in linked lists, state graphs, or functional sequences (e.g. Find the Duplicate Number).",
        "naive_complexity": "O(N) Time, O(N) Space",
        "wasted_work": "storing every visited ListNode reference inside an O(N) HashSet",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Initialize slow = head and fast = head; loop while fast != null && fast.next != null, advancing slow by 1 and fast by 2; return true if slow === fast.",
        "core_invariant": "Once both pointers enter a cycle of length C, the distance from fast to slow decreases by 1 modulo C on every step until distance == 0.",
        "takeaway": "Checking fast != null && fast.next != null guards fast.next.next against NullPointerException.",
        "pseudocode_lines": [
            "var slow = head; var fast = head",
            "while (fast != null && fast.next != null) {",
            "  slow = slow.next",
            "  fast = fast.next.next",
            "  if (slow === fast) return true",
            "}",
            "return false"
        ],
        "tests": [
            {"input": {"head": [3, 2, 0, -4], "pos": 1}, "expected": True},
            {"input": {"head": [1, 2], "pos": 0}, "expected": True},
            {"input": {"head": [1], "pos": -1}, "expected": False}
        ]
    },
    14: {
        "lc": 160, "title": "Intersection of Two Linked Lists", "category": "Linked Lists & System Node Design", "difficulty": "Easy",
        "lc_slug": "intersection-of-two-linked-lists", "method": "getIntersectionNode",
        "min_base": "Identical head node (headA === headB) -> loop doesn't even run, returns headA in O(1)",
        "best_exit": "Equal prefix lengths before merge -> pA and pB meet on the first pass without switching tracks",
        "worst_scan": "Disjoint lists (no intersection) -> both pointers walk lenA + lenB steps and meet at null simultaneously",
        "analogy_title": "The Two-Track Figure-Eight Equalizer",
        "aha_moment": "If runner A walks Track A then Track B (distance a + c + b), and runner B walks Track B then Track A (distance b + c + a), both walk the exact same total distance and arrive at the intersection node at the same instant!",
        "trigger": "Finding the merge point of two converging linked lists or parent-pointer tree nodes (LCA III).",
        "naive_complexity": "O(M + N) Time, O(M) Space",
        "wasted_work": "storing all nodes of list A in a HashSet to check membership from list B",
        "optimal_complexity": "O(M + N) Time, O(1) Space",
        "optimal_text": "Initialize pA = headA and pB = headB; while pA !== pB, advance pA = if (pA == null) headB else pA.next, and pB = if (pB == null) headA else pB.next; return pA.",
        "core_invariant": "After switching heads at null, pA and pB have identical remaining distance to the intersection node (or to the final null if disjoint).",
        "takeaway": "Allowing pA and pB to land on null before switching to the opposite head ensures disjoint lists terminate cleanly at pA === null && pB === null.",
        "pseudocode_lines": [
            "var pA = headA; var pB = headB",
            "while (pA !== pB) {",
            "  pA = if (pA == null) headB else pA.next",
            "  pB = if (pB == null) headA else pB.next",
            "}",
            "return pA"
        ],
        "tests": [
            {"input": {"intersectVal": 8, "listA": [4, 1, 8, 4, 5], "listB": [5, 6, 1, 8, 4, 5], "skipA": 2, "skipB": 3}, "expected": 8}
        ]
    },
    15: {
        "lc": 704, "title": "Binary Search", "category": "Binary Search", "difficulty": "Easy",
        "lc_slug": "binary-search", "method": "search",
        "min_base": "nums = [5], target = 5 -> left=0, right=0, mid=0 matches on step 1",
        "best_exit": "Target sits at exact middle index (n-1)/2 -> found on first probe in O(1)",
        "worst_scan": "Target missing from 10^4 elements -> takes at most log2(10^4) = 14 halving steps",
        "analogy_title": "The Dictionary Page Halver",
        "aha_moment": "In a sorted array, comparing target against nums[mid] eliminates half of the remaining search space in a single O(1) comparison!",
        "trigger": "Searching a sorted array or monotonic predicate domain in O(log N) time.",
        "naive_complexity": "O(N) Time, O(1) Space",
        "wasted_work": "linearly scanning every element from index 0 and ignoring sorted order",
        "optimal_complexity": "O(log N) Time, O(1) Space",
        "optimal_text": "Maintain closed interval [left..right]; compute overflow-safe mid = left + (right - left) / 2, return mid if equal, set left = mid + 1 if nums[mid] < target, else right = mid - 1.",
        "core_invariant": "If target exists in nums, its index is guaranteed to lie within the active closed interval [left..right].",
        "takeaway": "Computing mid = left + (right - left) / 2 prevents 32-bit signed integer overflow when left + right > Int.MAX_VALUE.",
        "pseudocode_lines": [
            "var left = 0; var right = nums.size - 1",
            "while (left <= right) {",
            "  val mid = left + (right - left) / 2",
            "  if (nums[mid] == target) return mid",
            "  else if (nums[mid] < target) left = mid + 1",
            "  else right = mid - 1",
            "}",
            "return -1"
        ],
        "tests": [
            {"input": {"nums": [-1, 0, 3, 5, 9, 12], "target": 9}, "expected": 4},
            {"input": {"nums": [-1, 0, 3, 5, 9, 12], "target": 2}, "expected": -1}
        ]
    },
    16: {
        "lc": 226, "title": "Invert Binary Tree", "category": "Binary Trees & BST", "difficulty": "Easy",
        "lc_slug": "invert-binary-tree", "method": "invertTree",
        "min_base": "root = null -> base case returns null immediately",
        "best_exit": "Single root node [1] -> swaps null children and returns root",
        "worst_scan": "Full binary tree of 100 nodes -> visits each node once in O(N) time and O(H) stack space",
        "analogy_title": "The Mirror-Image Coat Hanger Flip",
        "aha_moment": "To mirror an entire tree, recursively mirror the left and right subtrees and swap the left and right child pointers at every node!",
        "trigger": "Structural tree transformations where each node's children depend on transformed subtrees.",
        "naive_complexity": "O(N) Time, O(N) New Node Allocations",
        "wasted_work": "cloning an entirely new tree of TreeNodes instead of swapping child pointers in place",
        "optimal_complexity": "O(N) Time, O(H) Recursion Stack Space",
        "optimal_text": "If root == null return null; swap root.left and root.right pointers in place, recursively invert left and right subtrees, and return root.",
        "core_invariant": "Each recursive call invertTree(node) returns the root of a completely mirrored subtree.",
        "takeaway": "Caching val temp = root.left before assigning root.left = invertTree(root.right) avoids overwriting the original left child reference.",
        "pseudocode_lines": [
            "if (root == null) return null",
            "val temp = root.left",
            "root.left = invertTree(root.right)",
            "root.right = invertTree(temp)",
            "return root"
        ],
        "tests": [
            {"input": {"root": [4, 2, 7, 1, 3, 6, 9]}, "expected": [4, 7, 2, 9, 6, 3, 1]},
            {"input": {"root": [2, 1, 3]}, "expected": [2, 3, 1]}
        ]
    },
    17: {
        "lc": 104, "title": "Maximum Depth of Binary Tree", "category": "Binary Trees & BST", "difficulty": "Easy",
        "lc_slug": "maximum-depth-of-binary-tree", "method": "maxDepth",
        "min_base": "root = null -> depth of an empty tree is 0",
        "best_exit": "Single leaf node [1] -> 1 + max(0, 0) = 1",
        "worst_scan": "Degenerate skewed tree of N nodes -> recursion depth reaches O(N) height",
        "analogy_title": "Bottom-Up Subtree Height Relay",
        "aha_moment": "A node's height is simply 1 + the maximum height reported by its left and right children (with null reporting 0).",
        "trigger": "Computing bottom-up tree metrics (depth, balance, diameter, max path sum) via Post-Order DFS.",
        "naive_complexity": "O(N²) Time if height is recomputed repeatedly per node",
        "wasted_work": "re-traversing subtrees multiple times without bubbling heights bottom-up",
        "optimal_complexity": "O(N) Time, O(H) Space",
        "optimal_text": "Base case: if root == null return 0. Recursive step: return 1 + maxOf(maxDepth(root.left), maxDepth(root.right)).",
        "core_invariant": "Post-order DFS guarantees both child depths are fully resolved before computing the parent's depth.",
        "takeaway": "This 3-line post-order recurrence is the foundational building block for Balanced Binary Tree (#110), Diameter (#543), and Max Path Sum (#124).",
        "pseudocode_lines": [
            "if (root == null) return 0",
            "val leftDepth = maxDepth(root.left)",
            "val rightDepth = maxDepth(root.right)",
            "return 1 + maxOf(leftDepth, rightDepth)"
        ],
        "tests": [
            {"input": {"root": [3, 9, 20, None, None, 15, 7]}, "expected": 3},
            {"input": {"root": [1, None, 2]}, "expected": 2}
        ]
    },
    18: {
        "lc": 543, "title": "Diameter of Binary Tree", "category": "Binary Trees & BST", "difficulty": "Easy",
        "lc_slug": "diameter-of-binary-tree", "method": "diameterOfBinaryTree",
        "min_base": "Single node [1] -> 0 edges between nodes, returns diameter 0",
        "best_exit": "Two nodes [1, 2] -> leftDepth=1, rightDepth=0 -> maxDiameter = 1",
        "worst_scan": "Longest path bends through a deep subtree root without touching the main tree root!",
        "analogy_title": "The Arch-Bridge Span vs. Tower Height",
        "aha_moment": "At every node, the longest path bending THROUGH that node as the highest arch peak has length leftHeight + rightHeight—while the height it returns upward to its parent is 1 + max(leftHeight, rightHeight)!",
        "trigger": "Tree path problems where the optimal path can bend at any internal node (Diameter #543, Max Path Sum #124).",
        "naive_complexity": "O(N²) Time, O(H) Space",
        "wasted_work": "calling maxDepth(node.left) + maxDepth(node.right) separately from a top-down traversal on every node",
        "optimal_complexity": "O(N) Time, O(H) Space",
        "optimal_text": "Run a single post-order height(node) DFS that updates a shared maxDiameter = maxOf(maxDiameter, leftH + rightH) at each node while returning 1 + maxOf(leftH, rightH) to its parent.",
        "core_invariant": "When height(node) finishes, maxDiameter reflects the best arch span across all nodes inside node's subtree.",
        "takeaway": "Distinguish what a node UPDATES globally (leftH + rightH) from what it RETURNS upward to its parent (1 + max(leftH, rightH)).",
        "pseudocode_lines": [
            "var maxDiameter = 0",
            "fun height(node: TreeNode?): Int {",
            "  if (node == null) return 0",
            "  val leftH = height(node.left); val rightH = height(node.right)",
            "  maxDiameter = maxOf(maxDiameter, leftH + rightH)",
            "  return 1 + maxOf(leftH, rightH)",
            "}",
            "height(root); return maxDiameter"
        ],
        "tests": [
            {"input": {"root": [1, 2, 3, 4, 5]}, "expected": 3},
            {"input": {"root": [1, 2]}, "expected": 1}
        ]
    },
    19: {
        "lc": 70, "title": "Climbing Stairs", "category": "Dynamic Programming", "difficulty": "Easy",
        "lc_slug": "climbing-stairs", "method": "climbStairs",
        "min_base": "n = 1 -> 1 way; n = 2 -> 2 ways (base cases return immediately)",
        "best_exit": "Small n <= 2 -> O(1) direct return without entering loop",
        "worst_scan": "n = 45 -> 43 loop iterations in O(1) space (whereas unmemoized recursion takes 2^45 calls!)",
        "analogy_title": "The Two-Step Fibonacci Escalator",
        "aha_moment": "To land on step i, your final move could only come from step i-1 (a 1-step) or step i-2 (a 2-step). Thus ways(i) = ways(i-1) + ways(i-2) using just two rolling integers!",
        "trigger": "1D counting/optimization problems where state i depends exclusively on a fixed number of preceding states.",
        "naive_complexity": "O(2^N) Time, O(N) Call Stack",
        "wasted_work": "re-solving identical climbStairs(n - k) subtrees millions of times in an unmemoized recursion tree",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Initialize prev2 = 1 (step 1) and prev1 = 2 (step 2); loop i from 3..n computing curr = prev1 + prev2 and shifting prev2 = prev1, prev1 = curr.",
        "core_invariant": "After iteration i, prev1 holds the exact number of distinct ways to reach step i and prev2 holds the ways to reach step i-1.",
        "takeaway": "Compressing an O(N) DP array down to 2 registers (prev2, prev1) drops auxiliary memory to O(1).",
        "pseudocode_lines": [
            "if (n <= 2) return n",
            "var prev2 = 1; var prev1 = 2",
            "for (i in 3..n) {",
            "  val curr = prev1 + prev2",
            "  prev2 = prev1; prev1 = curr",
            "}",
            "return prev1"
        ],
        "tests": [
            {"input": {"n": 2}, "expected": 2},
            {"input": {"n": 3}, "expected": 3},
            {"input": {"n": 5}, "expected": 8}
        ]
    },
    20: {
        "lc": 136, "title": "Single Number", "category": "Bit Manipulation", "difficulty": "Easy",
        "lc_slug": "single-number", "method": "singleNumber",
        "min_base": "nums = [1] -> single element XORed with 0 returns 1",
        "best_exit": "nums = [2, 2, 1] -> 2 ^ 2 cancels to 0, leaving 0 ^ 1 = 1",
        "worst_scan": "30,000 elements with negative values -> bitwise XOR handles two's complement negative ints identically in O(1) space",
        "analogy_title": "The Matter-Antimatter XOR Annihilator",
        "aha_moment": "Bitwise XOR is both commutative and self-canceling (x ^ x = 0 and x ^ 0 = x). XORing all elements together annihilates every duplicate pair regardless of how far apart they sit!",
        "trigger": "Finding an odd-frequency element among even-frequency duplicates in O(1) auxiliary space.",
        "naive_complexity": "O(N) Time, O(N) Space",
        "wasted_work": "allocating a HashMap or HashSet to count frequencies when O(1) space is required",
        "optimal_complexity": "O(N) Time, O(1) Space",
        "optimal_text": "Initialize acc = 0; for each num in nums compute acc = acc xor num; return acc.",
        "core_invariant": "At step i, acc equals the bitwise XOR sum of nums[0..i], where every value seen an even number of times contributes 0.",
        "takeaway": "Because XOR is associative and commutative, sorting is never required—pairs cancel across any distance.",
        "pseudocode_lines": [
            "var acc = 0",
            "for (num in nums) {",
            "  acc = acc xor num",
            "}",
            "return acc"
        ],
        "tests": [
            {"input": {"nums": [2, 2, 1]}, "expected": 1},
            {"input": {"nums": [4, 1, 2, 1, 2]}, "expected": 4},
            {"input": {"nums": [1]}, "expected": 1}
        ]
    },
    21: {
        "lc": 191, "title": "Number of 1 Bits", "category": "Bit Manipulation", "difficulty": "Easy",
        "lc_slug": "number-of-1-bits", "method": "hammingWeight",
        "min_base": "n = 0 -> loop executes 0 times, returns 0",
        "best_exit": "Power of two (e.g. n = 128 = 0b10000000) -> 128 & 127 == 0 clears the single bit in 1 step!",
        "worst_scan": "All 32 bits set (-1 / 0xFFFFFFFF) -> executes at most 32 iterations",
        "analogy_title": "Brian Kernighan's Lowest-Bit Sniper",
        "aha_moment": "Instead of shifting through all 32 bit positions one by one, the operation n = n & (n - 1) directly erases the lowest set '1' bit in a single CPU instruction!",
        "trigger": "Counting set bits (Hamming weight) or checking if an integer is a power of two (n > 0 && (n & (n - 1)) == 0).",
        "naive_complexity": "Always 32 Loop Iterations (even when only 1 bit is set)",
        "wasted_work": "checking all 32 bit positions including zeros via right-shift polling",
        "optimal_complexity": "O(k) Time where k = number of set 1-bits (<= 32), O(1) Space",
        "optimal_text": "Loop while n != 0: clear the lowest set bit via n = n and (n - 1) and increment count++; return count.",
        "core_invariant": "Each execution of n = n & (n - 1) reduces the Hamming weight of n by exactly 1.",
        "takeaway": "Using while (n != 0) instead of while (n > 0) ensures signed 32-bit integers with the MSB sign bit set are handled accurately.",
        "pseudocode_lines": [
            "var count = 0; var curr = n",
            "while (curr != 0) {",
            "  curr = curr and (curr - 1)",
            "  count++",
            "}",
            "return count"
        ],
        "tests": [
            {"input": {"n": 11}, "expected": 3},
            {"input": {"n": 128}, "expected": 1},
            {"input": {"n": 2147483645}, "expected": 30}
        ]
    },
    22: {
        "lc": 338, "title": "Counting Bits", "category": "Bit Manipulation", "difficulty": "Easy",
        "lc_slug": "counting-bits", "method": "countBits",
        "min_base": "n = 0 -> returns [0] of length 1",
        "best_exit": "n = 2 -> dp[0]=0, dp[1]=dp[0]+1=1, dp[2]=dp[1]+0=1 -> [0, 1, 1]",
        "worst_scan": "n = 10^5 -> single O(N) pass with 1 bit-shift and 1 bit-mask per integer",
        "analogy_title": "Bit-Shift Prefix DP (i >> 1 plus Parity)",
        "aha_moment": "Shifting an integer right by 1 bit (i >> 1, or i / 2) drops its last bit—leaving a smaller number whose bit count you ALREADY computed in dp[i >> 1]! Just add the dropped last bit (i & 1)!",
        "trigger": "Computing bitwise properties across a contiguous range [0..n] using previously solved smaller integers.",
        "naive_complexity": "O(N log N) Time",
        "wasted_work": "running a separate hammingWeight loop from scratch for each i in 0..n",
        "optimal_complexity": "O(N) Time, O(1) Auxiliary Space (excluding output array)",
        "optimal_text": "Allocate ans = IntArray(n + 1); for i in 1..n set ans[i] = ans[i shr 1] + (i and 1); return ans.",
        "core_invariant": "Since i >> 1 < i for all i >= 1, ans[i >> 1] is always already computed before step i.",
        "takeaway": "Wrap (i and 1) in parentheses because '+' has higher operator precedence than bitwise '&' in Java/C++/Python!",
        "pseudocode_lines": [
            "val ans = IntArray(n + 1)",
            "for (i in 1..n) {",
            "  ans[i] = ans[i shr 1] + (i and 1)",
            "}",
            "return ans"
        ],
        "tests": [
            {"input": {"n": 2}, "expected": [0, 1, 1]},
            {"input": {"n": 5}, "expected": [0, 1, 1, 2, 1, 2]}
        ]
    },
    23: {
        "lc": 49, "title": "Group Anagrams", "category": "Arrays, Strings & Hashing", "difficulty": "Medium",
        "lc_slug": "group-anagrams", "method": "groupAnagrams",
        "min_base": "strs = [\"\"] -> single empty string maps to zero-count signature, returns [[\"\"]]",
        "best_exit": "All words distinct [\"rat\", \"car\", \"pig\"] -> N separate singleton buckets",
        "worst_scan": "10^4 strings of length 100 -> O(N * K) 26-char frequency signature avoids O(K log K) sorting",
        "analogy_title": "The 26-Slot Letter Tally Barcode",
        "aha_moment": "Two strings are anagrams if and only if they have the exact same 26-letter frequency count. Use their sorted string or 26-count barcode as the HashMap key!",
        "trigger": "Clustering items into equivalence classes by canonical signature.",
        "naive_complexity": "O(N² * K) Time, O(N * K) Space",
        "wasted_work": "comparing every pair of strings for anagram equivalence via nested loops",
        "optimal_complexity": "O(N * K) Time, O(N * K) Space",
        "optimal_text": "Map each string to its canonical 26-character frequency signature (or sorted key) in a HashMap<String, MutableList<String>> and return map.values.toList().",
        "core_invariant": "Every anagram family maps deterministically to the exact same canonical HashMap key.",
        "takeaway": "When building a string key from integer counts, delimit counts with '#' (or use a 26-char array/tuple) so [1, 10] and [11, 0] never collide.",
        "pseudocode_lines": [
            "val groups = HashMap<String, MutableList<String>>()",
            "for (word in strs) {",
            "  val counts = IntArray(26)",
            "  for (ch in word) counts[ch - 'a']++",
            "  val key = counts.joinToString(\"#\")",
            "  groups.getOrPut(key) { mutableListOf() }.add(word)",
            "}",
            "return groups.values.toList()"
        ]
    }
}

WHITEBOARD_CANVAS_RESTORE_HTML = """        <div class="relative rounded-xl border border-slate-800 bg-slate-950 overflow-hidden shadow-inner cursor-crosshair">
          <canvas id="whiteboard-canvas" class="w-full h-72 sm:h-80 block"></canvas>
          <div id="wb-hint" class="absolute bottom-2.5 right-3 text-[10px] font-mono text-slate-600 pointer-events-none select-none">
            💡 Draw array boxes, indices, and pointers here
          </div>
        </div>
      </div>
    </section>"""

PRACTICE_ARENA_HTML = """
      <!-- GOLDEN MASTER RUNNABLE PRACTICE ARENA -->
      <div class="rounded-2xl border border-indigo-500/30 bg-slate-900/80 p-6 sm:p-8 space-y-5 shadow-2xl">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-4">
          <div>
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
              <h4 class="text-sm font-bold text-white font-mono uppercase tracking-wider">Interactive Runnable Practice Arena</h4>
            </div>
            <p class="text-xs text-slate-400 mt-1">Code solution cold in your target language. Real in-browser sandboxed execution against test cases.</p>
          </div>

          <div class="flex items-center gap-1.5 bg-slate-950 p-1 rounded-xl border border-slate-800 text-xs font-mono">
            <button onclick="LessonRuntime.PracticeArena.setLang('kotlin')" id="practice-lang-kotlin" class="px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold transition-all">Kotlin</button>
            <button onclick="LessonRuntime.PracticeArena.setLang('python')" id="practice-lang-python" class="px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all">Python</button>
            <button onclick="LessonRuntime.PracticeArena.setLang('java')" id="practice-lang-java" class="px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all">Java</button>
          </div>
        </div>

        <div class="relative">
          <div class="flex items-center justify-between px-4 py-2 bg-slate-950 border border-b-0 border-slate-800 rounded-t-xl text-xs font-mono text-slate-400">
            <span id="practice-filename" class="text-emerald-400 font-bold">Solution.kt</span>
            <span class="text-[11px] text-slate-500">Supports Tab key • 5,000,000 operations TLE limit</span>
          </div>
          <textarea id="practice-editor" rows="12" class="w-full bg-slate-950 border border-slate-800 rounded-b-xl p-4 text-xs font-mono text-emerald-300 focus:outline-none focus:border-emerald-500 leading-relaxed custom-scrollbar spellcheck-false" placeholder="Write your solution here..."></textarea>
        </div>

        <div class="p-3.5 rounded-xl bg-slate-950 border border-slate-800/80 space-y-2">
          <div class="flex items-center justify-between text-xs font-mono">
            <label for="practice-custom-input" class="text-slate-400 font-bold">Custom Test Input (JSON args array):</label>
            <span class="text-slate-500 text-[10px]">e.g. [[1, 2, 3, 1]]</span>
          </div>
          <div class="flex gap-2">
            <input id="practice-custom-input" type="text" class="flex-1 bg-slate-900 border border-slate-700 rounded-lg px-3 py-1.5 text-xs font-mono text-slate-200 focus:outline-none focus:border-indigo-500" placeholder="e.g. [[1, 2, 3, 1]]">
            <button onclick="LessonRuntime.PracticeArena.runCustomInput()" class="px-3.5 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs font-mono transition-colors">
              Run Custom Input
            </button>
          </div>
        </div>

        <div class="flex flex-wrap items-center justify-between gap-3 pt-1">
          <div class="flex items-center gap-2">
            <button onclick="LessonRuntime.PracticeArena.runTests()" class="px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs font-mono transition-all shadow-lg shadow-emerald-500/20 flex items-center gap-2">
              <span>▶</span> Run Code &amp; Test Cases
            </button>
            <button onclick="LessonRuntime.PracticeArena.resetStub()" class="px-3.5 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 font-mono text-xs transition-colors">
              Reset Stub
            </button>
          </div>

          <div class="flex items-center gap-2">
            <button onclick="LessonRuntime.PracticeArena.revealHint()" id="practice-hint-btn" class="px-3.5 py-2 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-300 hover:bg-amber-500/20 text-xs font-mono font-bold transition-colors">
              💡 Need a Hint? (Step 1/3)
            </button>
            <button onclick="LessonRuntime.PracticeArena.revealSolution()" class="px-3.5 py-2 rounded-xl bg-slate-800 border border-slate-700 text-slate-300 hover:text-white text-xs font-mono transition-colors">
              Gated Model Answer
            </button>
          </div>
        </div>

        <div id="practice-hints-container" class="space-y-2 hidden"></div>

        <div id="practice-model-box" class="hidden p-4 rounded-xl bg-slate-950 border border-emerald-500/30 space-y-3">
          <div class="flex items-center justify-between">
            <span class="text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider">Gated Model Reference Solution</span>
            <span class="text-[11px] font-mono text-slate-500">Study after writing your solution</span>
          </div>
          <pre id="practice-model-code" class="p-3.5 rounded-lg bg-slate-900 text-xs font-mono text-emerald-300 overflow-x-auto"></pre>
        </div>

        <div id="practice-console" class="hidden p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center justify-between border-b border-slate-800/80 pb-2">
            <div class="flex items-center gap-2">
              <span id="practice-status-dot" class="w-2 h-2 rounded-full bg-slate-500"></span>
              <span id="practice-status-text" class="text-xs font-mono font-bold text-slate-300">Test Execution Idle</span>
            </div>
          </div>
          <div id="practice-output" class="space-y-2 text-xs font-mono"></div>
        </div>
      </div>
"""


def get_solutions_from_html(content):
    pres = re.findall(r'<pre[^>]*>([\s\S]*?)</pre>', content)
    k_sols, p_sols, j_sols = [], [], []
    for p in pres:
        clean = html.unescape(re.sub(r'<[^>]+>', '', p)).strip()
        if any(cls in clean for cls in ('class Solution', 'class Logger', 'class RecentCounter')):
            if 'fun ' in clean or 'val ' in clean:
                k_sols.append(clean)
            elif 'def ' in clean or 'self' in clean:
                p_sols.append(clean)
            elif 'public ' in clean or 'private ' in clean:
                j_sols.append(clean)
    return {
        "kotlin": k_sols[-1] if k_sols else "// Solution\n",
        "python": p_sols[-1] if p_sols else "# Solution\n",
        "java": j_sols[-1] if j_sols else "// Solution\n"
    }


def build_prob_dict(num, sols):
    m = PASS1_RICH_META[num]
    return {
        "num": num,
        "lc": m["lc"],
        "title": m["title"],
        "category": m["category"],
        "difficulty": m["difficulty"],
        "lc_slug": m["lc_slug"],
        "boundary_checks": {
            "min_base": m["min_base"],
            "best_exit": m["best_exit"],
            "worst_scan": m["worst_scan"],
        },
        "mental_model": {
            "analogy_title": m["analogy_title"],
            "aha_moment": m["aha_moment"],
            "trigger": m["trigger"],
        },
        "naive_vs_optimal": {
            "naive_complexity": m["naive_complexity"],
            "wasted_work": m["wasted_work"],
            "optimal_complexity": m["optimal_complexity"],
            "optimal_text": m["optimal_text"],
            "core_invariant": m["core_invariant"],
        },
        "trace_table": {
            "columns": ["Step", "Active Pointer / Index", "Maintained State", "Decision / Action"],
            "takeaway": m["takeaway"],
        },
        "stepper": {
            "pseudocode_lines": m["pseudocode_lines"],
        },
        "spoken_script": {
            "phase2": m["optimal_text"],
            "phase3": f"Maintain the invariant: {m['core_invariant']} {m['takeaway']}",
            "phase4": m["min_base"],
        },
        "approaches": [
            {
                "name": "Optimal",
                "badge": "Optimal",
                "code_kotlin": sols["kotlin"],
                "code_python": sols["python"],
                "code_java": sols["java"],
            }
        ],
    }


def render_section_2_5_html(p):
    non_code = build_non_code_solution(p)
    h = """    <!-- ========================================== -->
    <!-- SECTION 2.5: NON-CODE SOLUTION & PSEUDOCODE -->
    <!-- ========================================== -->
    <section id="section-2-5" class="rounded-2xl border border-amber-500/30 bg-slate-900/70 p-6 sm:p-8 space-y-6 shadow-xl">
      <div class="border-b border-slate-800 pb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <span class="text-xs font-mono text-amber-400 uppercase tracking-wider font-semibold">Section 2.5 &bull; Pre-Trace Algorithm Blueprint</span>
          <h3 class="text-xl sm:text-2xl font-bold text-white mt-1">Non-Code Solution Explanation &amp; Whiteboard Pseudocode</h3>
          <p class="text-xs text-slate-400 mt-1">Master the step-by-step logic and state variables in plain English before tracing or writing code.</p>
        </div>
        <span class="px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs font-mono font-bold">
          Language-Agnostic Blueprint
        </span>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center gap-2 text-xs font-mono font-bold text-cyan-400 uppercase tracking-wider">
            <span class="w-2 h-2 rounded-full bg-cyan-400"></span>
            1. State &amp; Data Structure Setup
          </div>
          <p class="text-xs text-slate-400 leading-relaxed">Variables and containers maintained during execution:</p>
          <ul class="space-y-2.5 text-xs text-slate-300">
"""
    for st_name, st_role in non_code.get('state_setup', []):
        h += f"""            <li class="p-2.5 rounded-lg bg-slate-900/80 border border-slate-800/80">
              <strong class="text-emerald-400 font-mono block mb-0.5">{st_name}</strong>
              <span class="text-slate-300 leading-relaxed">{st_role}</span>
            </li>\n"""
    h += """          </ul>
        </div>

        <div class="lg:col-span-2 p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
          <div class="flex items-center gap-2 text-xs font-mono font-bold text-emerald-400 uppercase tracking-wider">
            <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
            2. Step-by-Step Non-Code Algorithm Walkthrough
          </div>
          <div class="space-y-3">
"""
    for step_title, step_body in non_code.get('walkthrough_steps', []):
        h += f"""            <div class="p-3.5 rounded-xl bg-slate-900/70 border border-slate-800/80 space-y-1">
              <div class="text-xs font-mono font-bold text-amber-300">{step_title}</div>
              <p class="text-xs sm:text-sm text-slate-300 leading-relaxed">{step_body}</p>
            </div>\n"""
    h += """          </div>
        </div>
      </div>

      <div class="p-5 rounded-xl bg-slate-950 border border-amber-500/30 space-y-3">
        <div class="flex items-center justify-between">
          <span class="text-xs font-mono font-bold text-amber-400 uppercase tracking-wider">3. Clean Whiteboard Pseudocode (Syntax-Free)</span>
          <span class="text-[11px] font-mono text-slate-500">Direct blueprint for Trace Table &amp; Code</span>
        </div>
        <pre class="p-4 rounded-xl bg-slate-900/90 border border-slate-800 text-xs font-mono text-emerald-300 overflow-x-auto leading-relaxed">"""
    for idx_p, pline in enumerate(non_code.get('pseudocode_lines', []), 1):
        h += f"""<span class="text-slate-600 select-none mr-3">{idx_p:02d}</span>{html.escape(pline)}\n"""
    h += f"""</pre>
        <p class="text-xs text-slate-400 leading-relaxed">{non_code.get('bridge_note', '')}</p>
      </div>
    </section>
"""
    return h


def upgrade_file(fpath):
    with open(fpath, 'r', encoding='utf-8') as f:
        c = f.read()

    m_num = re.search(r'(\d{4})-', os.path.basename(fpath))
    num = int(m_num.group(1))
    num_str = f"{num:04d}"
    meta = PASS1_RICH_META[num]
    method = meta["method"]
    tests = meta.get("tests", [])

    # 1. Replace all leetcode.com/problems/ URLs with neetcode.io/problems/
    c = re.sub(
        r'https?://(?:www\.)?leetcode\.com/problems/([a-zA-Z0-9_-]+)/?',
        r'https://neetcode.io/problems/\1/',
        c
    )
    c = c.replace('View on LeetCode ↗', 'View on NeetCode ↗')
    c = c.replace('Official LC #', 'NeetCode #')
    c = c.replace('Open LeetCode cold', f'Open <a href="https://neetcode.io/problems/{meta["lc_slug"]}/" target="_blank" rel="noopener noreferrer" class="text-emerald-400 underline font-semibold">NeetCode #{meta["lc"]} ↗</a> cold')
    c = re.sub(
        r'Open LeetCode #(\d+) cold',
        f'Open <a href="https://neetcode.io/problems/{meta["lc_slug"]}/" target="_blank" rel="noopener noreferrer" class="text-emerald-400 underline font-semibold">NeetCode #\\1 ↗</a> cold',
        c
    )

    # 2. In 0002, 0003, 0004: restore whiteboard canvas in sketchpad-section and replace sandbox-textarea in section-5
    if num in (2, 3, 4):
        c = re.sub(
            r'<!-- Canvas Drawing Surface -->[\s\S]*?</section>',
            '<!-- Canvas Drawing Surface -->\n' + WHITEBOARD_CANVAS_RESTORE_HTML,
            c,
            count=1
        )
        c = re.sub(
            r'<!-- ======================================================== -->\s*<!-- INTERACTIVE CODE SANDBOX & SYNTAX TEST PLAYGROUND[\s\S]*?</section>',
            PRACTICE_ARENA_HTML.strip() + '\n    </section>',
            c,
            count=1
        )
        # Guard legacy sandbox-textarea JS listener in bottom script
        c = c.replace(
            "const textarea = document.getElementById('sandbox-textarea');\n      textarea.value = BOILERPLATES[currentPlaygroundLang];",
            "const textarea = document.getElementById('sandbox-textarea');\n      if (textarea) textarea.value = BOILERPLATES[currentPlaygroundLang];"
        )
        c = c.replace(
            "const textarea = document.getElementById('sandbox-textarea');\n    textarea.addEventListener(",
            "const textarea = document.getElementById('sandbox-textarea');\n    if (textarea) textarea.addEventListener("
        )

    # 3. In 0005..0010: remove duplicate legacy scratchpad-editor block before GOLDEN MASTER RUNNABLE PRACTICE ARENA
    if 5 <= num <= 10:
        c = re.sub(
            r'<!-- INTERACTIVE CODE PLAYGROUND / SCRATCHPAD -->[\s\S]*?(?=\s*</div>\s*<!-- GOLDEN MASTER RUNNABLE PRACTICE ARENA -->)',
            '',
            c,
            count=1
        )
        # Guard legacy scratchpad-editor JS references
        c = c.replace(
            "document.getElementById('scratchpad-editor').value =",
            "if (document.getElementById('scratchpad-editor')) document.getElementById('scratchpad-editor').value ="
        )
        c = c.replace(
            "const editor = document.getElementById('scratchpad-editor');\n      editor.value =",
            "const editor = document.getElementById('scratchpad-editor');\n      if (editor) editor.value ="
        )
        c = c.replace(
            "document.getElementById('scratchpad-editor').addEventListener(",
            "if (document.getElementById('scratchpad-editor')) document.getElementById('scratchpad-editor').addEventListener("
        )

    # 4. In 0011..0022: remove duplicate legacy code-editor block before GOLDEN MASTER RUNNABLE PRACTICE ARENA
    if 11 <= num <= 22:
        c = re.sub(
            r'<!-- (?:Live|Interactive) Code Scratchpad -->[\s\S]*?(?=<!-- GOLDEN MASTER RUNNABLE PRACTICE ARENA -->)',
            '',
            c,
            count=1
        )
        c = c.replace(
            "document.getElementById('code-editor').value =",
            "if (document.getElementById('code-editor')) document.getElementById('code-editor').value ="
        )

    # 5. Extract solutions & build rich prob_dict
    sols = get_solutions_from_html(c)
    prob_dict = build_prob_dict(num, sols)

    # 6. Inject or update Section 2.5 (Non-Code Solution Blueprint & Pseudocode) before Section 3
    sec25_html = render_section_2_5_html(prob_dict)
    if 'id="section-2-5"' in c:
        c = re.sub(r'<section id="section-2-5"[\s\S]*?</section>', lambda _: sec25_html.strip(), c, count=1)
    else:
        # Insert right before Section 3 comment or tag
        sec3_patterns = [
            r'(<!-- ========================================== -->\s*<!-- SECTION 3:)',
            r'(<!-- Section 3:)',
            r'(<section id="section-3")'
        ]
        inserted = False
        for pat in sec3_patterns:
            if re.search(pat, c):
                c = re.sub(pat, lambda m: sec25_html + "\n    " + m.group(1), c, count=1)
                inserted = True
                break
        if not inserted:
            print(f"⚠️ Could not find section-3 anchor in {fpath}")

    # 7. Ensure Quick-Jump bar in sticky header exists and includes 2.5 Solution & Pseudocode
    if 'Jump to:' in c:
        if 'href="#section-2-5"' not in c:
            c = c.replace(
                '<a href="#section-2" class="hover:text-emerald-400 transition-colors">2. Naive vs Optimal</a>',
                '<a href="#section-2" class="hover:text-emerald-400 transition-colors">2. Naive vs Optimal</a>\n        <a href="#section-2-5" class="hover:text-amber-300 transition-colors font-bold text-amber-400">2.5 Solution &amp; Pseudocode</a>'
            )
    else:
        quick_jump_bar = """    <!-- Quick-Jump Anchor Bar -->
    <div class="border-t border-slate-800/80 bg-slate-950/95 overflow-x-auto custom-scrollbar">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-2 flex items-center gap-4 text-xs font-mono text-slate-400 whitespace-nowrap">
        <span class="text-slate-500 font-bold uppercase tracking-wider text-[10px]">Jump to:</span>
        <a href="#section-0" class="hover:text-emerald-400 transition-colors font-bold text-slate-300">0. Problem</a>
        <a href="#sketchpad-section" class="hover:text-teal-400 transition-colors font-bold text-teal-300">✏️ Whiteboard</a>
        <a href="#section-1" class="hover:text-emerald-400 transition-colors">1. Mental Model</a>
        <a href="#section-2" class="hover:text-emerald-400 transition-colors">2. Naive vs Optimal</a>
        <a href="#section-2-5" class="hover:text-amber-300 transition-colors font-bold text-amber-400">2.5 Solution &amp; Pseudocode</a>
        <a href="#section-3" class="hover:text-emerald-400 transition-colors">3. Trace Table</a>
        <a href="#section-4" class="hover:text-emerald-400 transition-colors">4. Pocock Stepper</a>
        <a href="#section-5" class="hover:text-emerald-400 transition-colors font-bold text-indigo-400">5. Code &amp; Practice</a>
        <a href="#section-6" class="hover:text-emerald-400 transition-colors">6. Interview Script</a>
        <a href="#section-7" class="hover:text-emerald-400 transition-colors">7. Quiz &amp; Recall</a>
      </div>
    </div>
  </header>"""
        c = c.replace('</header>', quick_jump_bar, 1)

    # 8. For 0002..0022: replace the runtime_init_code block with full typed starterStubs, problem-specific hints, and problem-specific 4 MCQs
    if 2 <= num <= 22:
        stubs = build_stubs(prob_dict, method)
        hints = build_hints(prob_dict)
        quiz_questions = build_quiz(prob_dict)

        new_init_block = f"""// ==========================================
    // INITIALIZATION VIA SHARED RUNTIME ENGINE
    // ==========================================
    window.addEventListener('DOMContentLoaded', () => {{
      // Safe Whiteboard Init
      try {{ if (typeof initWhiteboard === 'function') initWhiteboard(); }} catch(e) {{}}
      try {{ if (typeof initCanvas === 'function') initCanvas(); }} catch(e) {{}}
      try {{
        if (window.LessonRuntime && LessonRuntime.Whiteboard && document.getElementById('whiteboard-canvas')) {{
          LessonRuntime.Whiteboard.init({{ canvasId: 'whiteboard-canvas' }});
        }}
      }} catch(e) {{}}

      // Safe Stepper Execution
      try {{
        if (typeof buildSteps === 'function') {{
          const cand = (typeof stepperData !== 'undefined') ? stepperData :
                       (typeof initialData !== 'undefined') ? initialData :
                       (typeof arrData !== 'undefined') ? arrData :
                       (typeof list1Data !== 'undefined') ? list1Data :
                       (typeof nodesData !== 'undefined') ? nodesData :
                       undefined;
          if (cand !== undefined) {{
            buildSteps(cand);
          }} else {{
            buildSteps();
          }}
        }}
      }} catch(err) {{
        console.warn("Stepper buildSteps note:", err);
      }}

      try {{
        if (typeof renderStepper === 'function') renderStepper();
        if (typeof renderStep === 'function') renderStep();
      }} catch(err) {{
        console.warn("Stepper render note:", err);
      }}

      // Initialize Practice Arena
      LessonRuntime.PracticeArena.init({{
        problemId: '{num_str}',
        methodName: '{method}',
        starterStubs: {json.dumps(stubs)},
        modelSolutions: {json.dumps(sols)},
        hints: {json.dumps(hints)},
        testCases: {json.dumps(tests)}
      }});

      // Initialize 4-Question Balanced Diagnostic Quizzes
      LessonRuntime.Quiz.init({{
        containerId: 'quiz-container',
        questions: {json.dumps(quiz_questions)}
      }});

      // Initialize 48-Hour Cold Recall Protocol
      LessonRuntime.SpacedRecall.init({{
        problemId: '{num_str}',
        badgeId: 'recall-status-badge',
        countdownId: 'recall-countdown'
      }});
    }});"""

        c = re.sub(
            r'// ==========================================\s*// INITIALIZATION VIA SHARED RUNTIME ENGINE[\s\S]*?(?=</script>)',
            lambda _: new_init_block + "\n  ",
            c
        )

    with open(fpath, 'w', encoding='utf-8') as f:
        f.write(c)
    print(f"✅ Upgraded {fpath} ({len(c)} bytes)")


def main():
    files = sorted(glob.glob('lessons/00[0-2][0-9]*.html'))
    for f in files:
        m = re.search(r'(\d{4})-', os.path.basename(f))
        if m:
            num = int(m.group(1))
            if 1 <= num <= 23:
                upgrade_file(f)


if __name__ == '__main__':
    main()
