// Interactive In-Browser Code Practice Runner using Skulpt Python
// Supports:
// - Live client-side Python execution against embedded test cases
// - Progressive hint ladder (Hint 1 -> Hint 2 -> Hint 3)
// - Final solution drawer with Kotlin, Python, and Java tabs
// - Tab key indentation (4 spaces) & code reset

(function () {
  "use strict";

  // Ensure Skulpt standard library reader is defined
  function builtinRead(x) {
    if (
      typeof Sk === "undefined" ||
      Sk.builtinFiles === undefined ||
      Sk.builtinFiles["files"][x] === undefined
    ) {
      throw "File not found: '" + x + "'";
    }
    return Sk.builtinFiles["files"][x];
  }

  // Handle Tab key in textareas
  function setupTabIndentation(textarea) {
    textarea.addEventListener("keydown", function (e) {
      if (e.key === "Tab") {
        e.preventDefault();
        const start = this.selectionStart;
        const end = this.selectionEnd;
        const value = this.value;

        if (e.shiftKey) {
          // Dedent: remove up to 4 leading spaces from current line
          const lineStart = value.lastIndexOf("\n", start - 1) + 1;
          const currentLine = value.substring(lineStart, start);
          const match = currentLine.match(/^ {1,4}/);
          if (match) {
            const removed = match[0].length;
            this.value = value.substring(0, lineStart) + currentLine.substring(removed) + value.substring(start);
            this.selectionStart = this.selectionEnd = Math.max(lineStart, start - removed);
          }
        } else {
          // Indent 4 spaces
          this.value = value.substring(0, start) + "    " + value.substring(end);
          this.selectionStart = this.selectionEnd = start + 4;
        }
      }
    });
  }

  // Initialize each practice widget
  document.querySelectorAll(".practice-widget").forEach(function (widget) {
    const editor = widget.querySelector(".practice-editor");
    const runBtn = widget.querySelector(".practice-run-btn");
    const hintBtn = widget.querySelector(".practice-hint-btn");
    const solutionBtn = widget.querySelector(".practice-solution-btn");
    const resetBtn = widget.querySelector(".practice-reset-btn");
    const consoleBox = widget.querySelector(".practice-console");
    const hintContainer = widget.querySelector(".practice-hints-container");
    const solutionDrawer = widget.querySelector(".practice-solution-drawer");

    if (!editor) return;

    setupTabIndentation(editor);

    // Save initial starter code for reset
    const starterCode = editor.value;

    // Parse test cases & hints
    let testCases = [];
    try {
      testCases = JSON.parse(widget.getAttribute("data-tests") || "[]");
    } catch (e) {
      console.error("Failed to parse test cases:", e);
    }

    let hints = [];
    try {
      hints = JSON.parse(widget.getAttribute("data-hints") || "[]");
    } catch (e) {
      console.error("Failed to parse hints:", e);
    }

    let currentHintIndex = 0;

    // Reset button
    if (resetBtn) {
      resetBtn.addEventListener("click", function () {
        if (confirm("Reset editor to starter boilerplate?")) {
          editor.value = starterCode;
          if (consoleBox) {
            consoleBox.innerHTML = '<p class="console-idle">Editor reset. Click <strong>▶ Run Tests</strong> to execute your code.</p>';
          }
        }
      });
    }

    // Hint button
    if (hintBtn && hints.length > 0) {
      hintBtn.textContent = `💡 Hint (0/${hints.length})`;
      hintBtn.addEventListener("click", function () {
        if (currentHintIndex < hints.length) {
          const hintCard = document.createElement("div");
          hintCard.className = "hint-card callout warn";
          hintCard.innerHTML = `<p><strong>Hint ${currentHintIndex + 1} of ${hints.length}:</strong> ${hints[currentHintIndex]}</p>`;
          hintContainer.appendChild(hintCard);
          hintContainer.style.display = "block";
          currentHintIndex++;

          if (currentHintIndex < hints.length) {
            hintBtn.textContent = `💡 Hint (${currentHintIndex}/${hints.length})`;
          } else {
            hintBtn.textContent = `💡 All ${hints.length} Hints Unlocked`;
            hintBtn.disabled = true;
          }
        }
      });
    } else if (hintBtn) {
      hintBtn.style.display = "none";
    }

    // Solution button
    if (solutionBtn && solutionDrawer) {
      solutionBtn.addEventListener("click", function () {
        const isHidden = solutionDrawer.style.display === "none" || !solutionDrawer.style.display;
        if (isHidden) {
          solutionDrawer.style.display = "block";
          solutionBtn.textContent = "🙈 Hide Solution";
          solutionBtn.classList.add("active");
        } else {
          solutionDrawer.style.display = "none";
          solutionBtn.textContent = "👁️ Reveal Final Solution";
          solutionBtn.classList.remove("active");
        }
      });
    }

    // Run tests button
    if (runBtn) {
      runBtn.addEventListener("click", function () {
        runBtn.disabled = true;
        runBtn.textContent = "⏳ Running...";

        if (consoleBox) {
          consoleBox.innerHTML = '<p class="console-running">Executing in browser environment...</p>';
        }

        // Check if Skulpt is loaded
        if (typeof Sk === "undefined") {
          if (consoleBox) {
            consoleBox.innerHTML = `
              <div class="callout warn">
                <p><strong>Python Engine Notice:</strong> In-browser runtime requires internet access to fetch Skulpt interpreter.</p>
                <p>You can still write your implementation above and verify it against the <strong>Hints</strong> and <strong>Trilingual Final Solutions</strong> below!</p>
              </div>`;
          }
          runBtn.disabled = false;
          runBtn.textContent = "▶ Run Tests";
          return;
        }

        const userCode = editor.value;
        let stdoutCaptured = "";

        // Build test runner harness
        // Tests format: [{"call": "twoSum([2,7,11,15], 9)", "expected": [0,1], "compare": "set_or_equal"}]
        const testsJson = JSON.stringify(testCases);
        const harness = `
import sys
import json
from collections import deque

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

def build_list(arr):
    dummy = ListNode(0)
    curr = dummy
    for x in arr:
        curr.next = ListNode(x)
        curr = curr.next
    return dummy.next

def list_to_array(node):
    res = []
    curr = node
    cnt = 0
    while curr and cnt < 1000:
        res.append(curr.val)
        curr = curr.next
        cnt += 1
    return res

class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def build_tree(arr):
    if not arr or arr[0] is None:
        return None
    root = TreeNode(arr[0])
    q = [root]
    qi = 0
    i = 1
    while qi < len(q) and i < len(arr):
        curr = q[qi]
        qi += 1
        if i < len(arr) and arr[i] is not None:
            curr.left = TreeNode(arr[i])
            q.append(curr.left)
        i += 1
        if i < len(arr) and arr[i] is not None:
            curr.right = TreeNode(arr[i])
            q.append(curr.right)
        i += 1
    return root

def tree_to_array(root):
    if not root: return []
    res = []
    q = [root]
    qi = 0
    while qi < len(q):
        curr = q[qi]
        qi += 1
        if curr:
            res.append(curr.val)
            q.append(curr.left)
            q.append(curr.right)
        else:
            res.append(None)
    while res and res[-1] is None:
        res.pop()
    return res

${userCode}

__tests = json.loads('''${testsJson.replace(/\\/g, "\\\\").replace(/'''/g, "\\'\\'\\'")}''')
__results = []
__all_passed = True

for __i, __t in enumerate(__tests):
    __call = __t.get("call", "")
    __expected = __t.get("expected")
    __comp = __t.get("compare", "equal")
    try:
        __actual = eval(__call)
        if isinstance(__actual, ListNode):
            __actual = list_to_array(__actual)
        elif isinstance(__actual, TreeNode):
            __actual = tree_to_array(__actual)

        __is_pass = False
        if __comp == "set_or_equal":
            if isinstance(__actual, list) and isinstance(__expected, list):
                __is_pass = (sorted(__actual) == sorted(__expected))
            else:
                __is_pass = (__actual == __expected)
        elif __comp == "set_of_lists":
            # Compare list of lists ignoring order of sublists
            try:
                __act_sorted = sorted([sorted(x) if isinstance(x, list) else x for x in __actual])
                __exp_sorted = sorted([sorted(x) if isinstance(x, list) else x for x in __expected])
                __is_pass = (__act_sorted == __exp_sorted)
            except:
                __is_pass = (__actual == __expected)
        else:
            __is_pass = (__actual == __expected)

        if not __is_pass:
            __all_passed = False
        __results.append({
            "idx": __i + 1,
            "call": __call,
            "expected": repr(__expected),
            "actual": repr(__actual),
            "passed": __is_pass
        })
    except Exception as __e:
        __all_passed = False
        __results.append({
            "idx": __i + 1,
            "call": __call,
            "expected": repr(__expected),
            "actual": type(__e).__name__ + ": " + str(__e),
            "passed": False,
            "error": True
        })

print("__PRACTICE_PAYLOAD__" + json.dumps({"passed": __all_passed, "results": __results}))
`;

        Sk.configure({
          output: function (text) {
            stdoutCaptured += text;
          },
          read: builtinRead,
          execLimit: 6000 // 6 seconds timeout limit
        });

        Sk.misceval
          .asyncToPromise(function () {
            return Sk.importMainWithBody("<stdin>", false, harness, true);
          })
          .then(function () {
            runBtn.disabled = false;
            runBtn.textContent = "▶ Run Tests";

            // Parse result payload from stdout
            const marker = "__PRACTICE_PAYLOAD__";
            const splitIdx = stdoutCaptured.lastIndexOf(marker);
            let userOutput = stdoutCaptured;
            let resultData = null;

            if (splitIdx !== -1) {
              userOutput = stdoutCaptured.substring(0, splitIdx);
              const jsonStr = stdoutCaptured.substring(splitIdx + marker.length).trim();
              try {
                resultData = JSON.parse(jsonStr);
              } catch (e) {
                console.error("JSON parse error on payload:", e);
              }
            }

            renderTestResults(consoleBox, resultData, userOutput, testCases.length);
          })
          .catch(function (err) {
            runBtn.disabled = false;
            runBtn.textContent = "▶ Run Tests";

            let errStr = err.toString();
            if (err.traceback && err.traceback.length) {
              // Extract readable error line
              errStr = err.toString();
            }

            let cleanUserOut = stdoutCaptured.replace(/__PRACTICE_PAYLOAD__[\s\S]*$/, "");
            renderError(consoleBox, errStr, cleanUserOut);
          });
      });
    }
  });

  function renderTestResults(container, data, userStdout, totalTests) {
    if (!container) return;
    if (!data || !data.results) {
      container.innerHTML = `
        <div class="callout ok">
          <p><strong>Code executed without syntax errors!</strong></p>
          ${userStdout ? `<pre class="stdout-pre"><code>${escapeHtml(userStdout)}</code></pre>` : ""}
        </div>`;
      return;
    }

    const passedCount = data.results.filter(function (r) {
      return r.passed;
    }).length;
    const isAllPass = passedCount === data.results.length;

    let html = "";
    if (isAllPass) {
      html += `<div class="test-header pass">
        <span class="test-badge pass">✓ ALL ${passedCount}/${data.results.length} TESTS PASSED</span>
        <span class="test-meta">Optimal correctness verified!</span>
      </div>`;
    } else {
      html += `<div class="test-header fail">
        <span class="test-badge fail">✗ ${passedCount}/${data.results.length} TESTS PASSED</span>
        <span class="test-meta">Review failed test case details below:</span>
      </div>`;
    }

    if (userStdout && userStdout.trim().length > 0) {
      html += `<div class="stdout-box">
        <div class="stdout-label">Program Standard Output (print):</div>
        <pre class="stdout-pre"><code>${escapeHtml(userStdout.trim())}</code></pre>
      </div>`;
    }

    html += '<div class="test-cases-list">';
    data.results.forEach(function (res) {
      const statusClass = res.passed ? "pass" : "fail";
      const icon = res.passed ? "✓" : "✗";
      html += `
        <div class="test-case-row ${statusClass}">
          <div class="test-case-top">
            <span class="test-case-badge ${statusClass}">${icon} Test ${res.idx}</span>
            <code class="test-call">${escapeHtml(res.call)}</code>
          </div>
          <div class="test-case-details">
            <div class="test-field"><span class="k">Expected:</span> <code class="v">${escapeHtml(res.expected)}</code></div>
            <div class="test-field"><span class="k">Got:</span> <code class="v ${statusClass}">${escapeHtml(res.actual)}</code></div>
          </div>
        </div>`;
    });
    html += "</div>";

    container.innerHTML = html;
  }

  function renderError(container, errString, userStdout) {
    if (!container) return;
    let html = `
      <div class="test-header fail">
        <span class="test-badge fail">⚠ RUNTIME OR SYNTAX ERROR</span>
      </div>
      <div class="callout warn" style="margin:.6rem 0;">
        <pre class="error-pre"><code>${escapeHtml(errString)}</code></pre>
      </div>`;

    if (userStdout && userStdout.trim().length > 0) {
      html += `<div class="stdout-box">
        <div class="stdout-label">Output before crash:</div>
        <pre class="stdout-pre"><code>${escapeHtml(userStdout.trim())}</code></pre>
      </div>`;
    }
    container.innerHTML = html;
  }

  function escapeHtml(str) {
    if (typeof str !== "string") str = String(str);
    return str
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
})();
