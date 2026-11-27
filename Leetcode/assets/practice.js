// Interactive In-Browser Code Practice Runner powered by Pyodide (CPython 3.12 WebAssembly)
// Supports:
// - Lazy on-demand WebAssembly Python 3.12 runtime (zero page-load overhead; cached after first run)
// - Full CPython stdlib: heapq, Counter, deque, defaultdict, bisect, functools.cache, typing, eval, json
// - Isolated per-run namespace + optional data-setup driver code (keeps user editor clean)
// - Progressive hint ladder, trilingual solution drawer, Tab/Shift+Tab indentation, and boilerplate reset

(function () {
  "use strict";

  const PYODIDE_VERSION = "v0.27.4";
  const PYODIDE_INDEX_URL = `https://cdn.jsdelivr.net/pyodide/${PYODIDE_VERSION}/full/`;
  const PYODIDE_SCRIPT_URL = `${PYODIDE_INDEX_URL}pyodide.js`;

  let pyodidePromise = null;

  function loadPyodideRuntime() {
    if (pyodidePromise) return pyodidePromise;
    pyodidePromise = new Promise(function (resolve, reject) {
      function init() {
        if (typeof window.loadPyodide !== "function") {
          reject(new Error("Pyodide loader unavailable."));
          return;
        }
        window
          .loadPyodide({ indexURL: PYODIDE_INDEX_URL })
          .then(resolve)
          .catch(reject);
      }

      if (typeof window.loadPyodide === "function") {
        init();
        return;
      }

      const script = document.createElement("script");
      script.src = PYODIDE_SCRIPT_URL;
      script.async = true;
      script.onload = init;
      script.onerror = function () {
        pyodidePromise = null;
        reject(new Error("Failed to fetch Pyodide from CDN (check internet connection)."));
      };
      document.head.appendChild(script);
    });
    return pyodidePromise;
  }

  // Handle Tab / Shift+Tab key in textareas
  function setupTabIndentation(textarea) {
    textarea.addEventListener("keydown", function (e) {
      if (e.key === "Tab") {
        e.preventDefault();
        const start = this.selectionStart;
        const end = this.selectionEnd;
        const value = this.value;

        if (e.shiftKey) {
          const lineStart = value.lastIndexOf("\n", start - 1) + 1;
          const currentLine = value.substring(lineStart, start);
          const match = currentLine.match(/^ {1,4}/);
          if (match) {
            const removed = match[0].length;
            this.value =
              value.substring(0, lineStart) +
              currentLine.substring(removed) +
              value.substring(start);
            this.selectionStart = this.selectionEnd = Math.max(lineStart, start - removed);
          }
        } else {
          this.value = value.substring(0, start) + "    " + value.substring(end);
          this.selectionStart = this.selectionEnd = start + 4;
        }
      }
    });
  }

  const PYTHON_RUNNER_DRIVER = `
import sys, io, json, math, bisect, heapq, random, itertools, functools, collections, traceback
from collections import deque, defaultdict, Counter
from typing import List, Dict, Tuple, Set, Optional, Any

def __run_practice_suite(user_code_str, setup_code_str, tests_json_str):
    buf = io.StringIO()
    old_stdout = sys.stdout
    sys.stdout = buf

    ns = {
        "__builtins__": __builtins__,
        "sys": sys,
        "math": math,
        "bisect": bisect,
        "heapq": heapq,
        "random": random,
        "itertools": itertools,
        "functools": functools,
        "collections": collections,
        "deque": deque,
        "defaultdict": defaultdict,
        "Counter": Counter,
        "List": List,
        "Dict": Dict,
        "Tuple": Tuple,
        "Set": Set,
        "Optional": Optional,
        "Any": Any,
    }

    prelude = '''
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
    while curr and cnt < 2000:
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
    q = deque([root])
    i = 1
    while q and i < len(arr):
        curr = q.popleft()
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
    if not root:
        return []
    res = []
    q = deque([root])
    while q:
        curr = q.popleft()
        if curr:
            res.append(curr.val)
            q.append(curr.left)
            q.append(curr.right)
        else:
            res.append(None)
    while res and res[-1] is None:
        res.pop()
    return res
'''
    try:
        exec(prelude, ns, ns)
        exec(compile(user_code_str, "<editor>", "exec"), ns, ns)

        if "Solution" in ns and isinstance(ns["Solution"], type):
            sol = ns["Solution"]()
            for attr in dir(sol):
                if not attr.startswith("_") and attr not in ns:
                    ns[attr] = getattr(sol, attr)
                elif not attr.startswith("_") and callable(getattr(sol, attr)):
                    ns[attr] = getattr(sol, attr)

        if setup_code_str and setup_code_str.strip():
            exec(compile(setup_code_str, "<test-harness>", "exec"), ns, ns)

        tests = json.loads(tests_json_str)
        results = []
        all_passed = True
        ListNodeCls = ns["ListNode"]
        TreeNodeCls = ns["TreeNode"]

        for idx, t in enumerate(tests):
            call_expr = t.get("call", "")
            expected = t.get("expected")
            comp = t.get("compare", "equal")
            try:
                actual = eval(call_expr, ns, ns)
                if isinstance(actual, ListNodeCls):
                    actual = ns["list_to_array"](actual)
                elif isinstance(actual, TreeNodeCls):
                    actual = ns["tree_to_array"](actual)
                elif actual is None and expected == [] and ("build_list" in call_expr or "build_tree" in call_expr):
                    actual = []

                is_pass = False
                if comp == "set_or_equal":
                    if isinstance(actual, list) and isinstance(expected, list):
                        try:
                            is_pass = sorted(actual) == sorted(expected)
                        except Exception:
                            is_pass = actual == expected
                    else:
                        is_pass = actual == expected
                elif comp == "set_of_lists":
                    try:
                        act_s = sorted([sorted(x) if isinstance(x, list) else x for x in actual])
                        exp_s = sorted([sorted(x) if isinstance(x, list) else x for x in expected])
                        is_pass = act_s == exp_s
                    except Exception:
                        is_pass = actual == expected
                else:
                    is_pass = actual == expected

                if not is_pass:
                    all_passed = False
                results.append({
                    "idx": idx + 1,
                    "call": call_expr,
                    "expected": repr(expected),
                    "actual": repr(actual),
                    "passed": is_pass
                })
            except Exception as e:
                all_passed = False
                results.append({
                    "idx": idx + 1,
                    "call": call_expr,
                    "expected": repr(expected),
                    "actual": f"{type(e).__name__}: {e}",
                    "passed": False,
                    "error": True
                })

        return json.dumps({
            "ok": True,
            "passed": all_passed,
            "stdout": buf.getvalue(),
            "results": results
        })
    except Exception:
        tb_lines = traceback.format_exc().splitlines()
        filtered = [ln for ln in tb_lines if "__run_practice_suite" not in ln]
        return json.dumps({
            "ok": False,
            "error": "\\n".join(filtered[-8:]),
            "stdout": buf.getvalue()
        })
    finally:
        sys.stdout = old_stdout
`;

  // Extract clean LeetCode-style Kotlin starter stub from a full Kotlin solution without leaking implementation
  function extractKotlinStub(ktCode) {
    if (!ktCode) return "";
    const lines = ktCode.split("\n");
    const out = [];
    let depth = 0;
    let skipUntilDepth = -1;

    for (let i = 0; i < lines.length; i++) {
      const raw = lines[i];
      const trimmed = raw.trim();
      const noComment = raw.replace(/\/\/.*$/, "");

      let opens = 0;
      let closes = 0;
      for (let c = 0; c < noComment.length; c++) {
        if (noComment[c] === "{") opens++;
        else if (noComment[c] === "}") closes++;
      }

      if (skipUntilDepth >= 0) {
        depth += opens - closes;
        if (depth <= skipUntilDepth) {
          const indent = skipUntilDepth === 1 ? "    " : "";
          out.push(indent + "}");
          skipUntilDepth = -1;
        }
        continue;
      }

      if (trimmed.startsWith("import ")) {
        out.push(raw);
        continue;
      }

      if (depth === 0 && /^(class|object)\s+\w+/.test(trimmed)) {
        out.push(raw);
        depth += opens - closes;
        continue;
      }

      if (depth === 0 && /^fun\s+\w+/.test(trimmed)) {
        const sig = raw.replace(/\{[\s\S]*$/, "{");
        out.push(sig);
        out.push("    // TODO: implement in Kotlin");
        out.push("    TODO()");
        if (opens > closes) {
          skipUntilDepth = 0;
          depth += opens - closes;
        } else {
          out.push("}");
        }
        continue;
      }

      if (depth === 1) {
        if (/^private\s+/.test(trimmed)) {
          if (opens > closes) {
            skipUntilDepth = -2; // silent skip
            let inner = depth + opens - closes;
            while (i + 1 < lines.length && inner > 1) {
              i++;
              const nc = lines[i].replace(/\/\/.*$/, "");
              for (let c = 0; c < nc.length; c++) {
                if (nc[c] === "{") inner++;
                else if (nc[c] === "}") inner--;
              }
            }
            skipUntilDepth = -1;
          }
          continue;
        }
        if (/^fun\s+\w+/.test(trimmed)) {
          if (opens === 0) {
            out.push(raw);
            continue;
          }
          const sig = raw.replace(/\{[\s\S]*$/, "{");
          out.push(sig);
          out.push("        // TODO: implement in Kotlin");
          out.push("        TODO()");
          if (opens > closes) {
            skipUntilDepth = 1;
            depth += opens - closes;
          } else {
            out.push("    }");
          }
          continue;
        }
        if (trimmed === "}" && closes > opens) {
          out.push("}");
          depth += opens - closes;
          continue;
        }
      }

      depth += opens - closes;
    }

    return out.join("\n").trim();
  }

  let driverLoaded = false;

  // Initialize each practice widget
  document.querySelectorAll(".practice-widget").forEach(function (widget) {
    const editor = widget.querySelector(".practice-editor");
    const toolbar = widget.querySelector(".practice-toolbar");
    const runBtn = widget.querySelector(".practice-run-btn");
    const hintBtn = widget.querySelector(".practice-hint-btn");
    const solutionBtn = widget.querySelector(".practice-solution-btn");
    const resetBtn = widget.querySelector(".practice-reset-btn");
    const consoleBox = widget.querySelector(".practice-console");
    const hintContainer = widget.querySelector(".practice-hints-container");
    const solutionDrawer = widget.querySelector(".practice-solution-drawer");

    if (!editor) return;

    setupTabIndentation(editor);

    // Inject "Copy Kotlin Stub" button derived from the Kotlin solution signature (without revealing solution)
    const ktCodeEl = solutionDrawer
      ? solutionDrawer.querySelector('.tab-pane[data-lang="kotlin"] code')
      : null;
    const ktStub = ktCodeEl ? extractKotlinStub(ktCodeEl.textContent) : "";
    if (ktStub && toolbar) {
      const ktBtn = document.createElement("button");
      ktBtn.type = "button";
      ktBtn.className = "practice-btn practice-kt-btn";
      ktBtn.textContent = "📋 Kotlin Stub";
      ktBtn.title = "View and copy the clean Kotlin starter signature without revealing the solution";

      const ktDrawer = document.createElement("div");
      ktDrawer.className = "kt-stub-drawer callout";
      ktDrawer.innerHTML =
        '<p class="meta" style="margin:0 0 .25rem;"><strong>Kotlin Primary Target Starter</strong> (copied to clipboard — paste into LeetCode, IntelliJ, or chat):</p>' +
        "<pre><code></code></pre>";
      ktDrawer.querySelector("code").textContent = ktStub;
      toolbar.insertAdjacentElement("afterend", ktDrawer);

      ktBtn.addEventListener("click", function () {
        const open = ktDrawer.style.display === "block";
        ktDrawer.style.display = open ? "none" : "block";
        if (!open && navigator.clipboard && navigator.clipboard.writeText) {
          navigator.clipboard.writeText(ktStub).then(function () {
            ktBtn.textContent = "✓ Kotlin Stub Copied";
            setTimeout(function () {
              ktBtn.textContent = "📋 Hide Kotlin Stub";
            }, 1400);
          }).catch(function () {
            ktBtn.textContent = "📋 Hide Kotlin Stub";
          });
        } else {
          ktBtn.textContent = open ? "📋 Kotlin Stub" : "📋 Hide Kotlin Stub";
        }
      });

      if (resetBtn) {
        toolbar.insertBefore(ktBtn, resetBtn);
      } else {
        toolbar.appendChild(ktBtn);
      }
    }

    const starterCode = editor.value;
    const setupCode = widget.getAttribute("data-setup") || "";

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

    if (resetBtn) {
      resetBtn.addEventListener("click", function () {
        if (confirm("Reset editor to starter boilerplate?")) {
          editor.value = starterCode;
          if (consoleBox) {
            consoleBox.innerHTML =
              '<p class="console-idle">Editor reset. Click <strong>▶ Run Tests</strong> to execute your code.</p>';
          }
        }
      });
    }

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

    if (solutionBtn && solutionDrawer) {
      solutionBtn.addEventListener("click", function () {
        const isHidden =
          solutionDrawer.style.display === "none" || !solutionDrawer.style.display;
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

    if (runBtn) {
      runBtn.addEventListener("click", async function () {
        runBtn.disabled = true;
        const firstLoad = !pyodidePromise;
        runBtn.textContent = firstLoad ? "⏳ Loading Python 3.12..." : "⏳ Running...";

        if (consoleBox) {
          consoleBox.innerHTML = firstLoad
            ? '<p class="console-running">Initializing Python 3.12 WebAssembly runtime (first run ~2s, cached afterward)...</p>'
            : '<p class="console-running">Running test suite in Python 3.12...</p>';
        }

        try {
          const pyodide = await loadPyodideRuntime();
          if (!driverLoaded) {
            await pyodide.runPythonAsync(PYTHON_RUNNER_DRIVER);
            driverLoaded = true;
          }

          runBtn.textContent = "⏳ Running...";
          const runnerFn = pyodide.globals.get("__run_practice_suite");
          const rawJson = runnerFn(
            editor.value,
            setupCode,
            JSON.stringify(testCases)
          );
          runnerFn.destroy();

          const payload = JSON.parse(rawJson);
          if (!payload.ok) {
            renderError(consoleBox, payload.error, payload.stdout);
          } else {
            renderTestResults(consoleBox, payload, payload.stdout);
          }
        } catch (err) {
          if (consoleBox) {
            consoleBox.innerHTML = `
              <div class="callout warn">
                <p><strong>Offline / Runtime Notice:</strong> ${escapeHtml(err.message || String(err))}</p>
                <p>You can still write your Kotlin/Python/Java solution with your <code>/teach</code> coach in chat and verify against the <strong>Hints</strong> and <strong>Trilingual Solutions</strong> below.</p>
              </div>`;
          }
        } finally {
          runBtn.disabled = false;
          runBtn.textContent = "▶ Run Tests";
        }
      });
    }
  });

  function renderTestResults(container, data, userStdout) {
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
        <span class="test-meta">Python 3.12 verification passed — ready to translate/verify in Kotlin!</span>
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
