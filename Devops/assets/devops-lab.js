(function () {
  "use strict";

  const STORAGE_KEY = "devops_mastery_2027_v1";
  const THEME_KEY = "devops_theme_2027";

  /* ------------------------------------------------------------------
   * Persistence & Theme
   * ------------------------------------------------------------------ */
  function loadState() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) return JSON.parse(raw);
    } catch (_) {}
    return { lessons: {} };
  }

  function saveState(state) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (_) {}
  }

  function getLessonState(lessonId) {
    const state = loadState();
    if (!state.lessons[lessonId]) {
      state.lessons[lessonId] = { labs: {}, quizzes: {}, completed: false };
    }
    return state.lessons[lessonId];
  }

  function updateLessonState(lessonId, updater) {
    const state = loadState();
    if (!state.lessons[lessonId]) {
      state.lessons[lessonId] = { labs: {}, quizzes: {}, completed: false };
    }
    updater(state.lessons[lessonId]);
    saveState(state);
    return state.lessons[lessonId];
  }

  function initTheme() {
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.documentElement.setAttribute("data-theme", saved);
    const btn = document.getElementById("theme-toggle");
    if (btn) {
      btn.textContent = saved === "dark" ? "Light Mode" : "Dark Mode";
      btn.addEventListener("click", () => {
        const current = document.documentElement.getAttribute("data-theme") || "dark";
        const next = current === "dark" ? "light" : "dark";
        document.documentElement.setAttribute("data-theme", next);
        localStorage.setItem(THEME_KEY, next);
        btn.textContent = next === "dark" ? "Light Mode" : "Dark Mode";
      });
    }
  }

  /* ------------------------------------------------------------------
   * HTML escaping helper
   * ------------------------------------------------------------------ */
  function esc(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  /* ------------------------------------------------------------------
   * Simulated Linux / Bash Sandbox Engine
   * ------------------------------------------------------------------ */
  function clone(obj) {
    return JSON.parse(JSON.stringify(obj));
  }

  function modeToRwx(mode) {
    const m = String(mode).slice(-3).padStart(3, "0");
    const map = ["---", "--x", "-w-", "-wx", "r--", "r-x", "rw-", "rwx"];
    return m
      .split("")
      .map((d) => map[parseInt(d, 10) || 0])
      .join("");
  }

  function resolvePath(cwd, target) {
    if (!target || target === "~") return "/home/devops";
    let p = target.startsWith("/") ? target : (cwd === "/" ? "" : cwd) + "/" + target;
    const parts = [];
    for (const seg of p.split("/")) {
      if (!seg || seg === ".") continue;
      if (seg === "..") parts.pop();
      else parts.push(seg);
    }
    return "/" + parts.join("/");
  }

  function tokenizeShell(line, env) {
    // Splits a single command invocation respecting single quotes, double quotes, and escapes.
    // Tracks whether an argument was quoted so unquoted spaces in $VAR reproduce real Bash word-splitting!
    const tokens = [];
    let current = "";
    let inSingle = false;
    let inDouble = false;
    let hadQuote = false;

    function expandVars(text) {
      return text.replace(/\$\{([A-Za-z_][A-Za-z0-9_]*)(?::-([^}]*))?\}|\$([A-Za-z_][A-Za-z0-9_]*|\?)/g, (_, bName, defVal, sName) => {
        const key = bName || sName;
        if (key === "?") return String(env["?"] ?? "0");
        if (Object.prototype.hasOwnProperty.call(env, key) && env[key] !== "") {
          return String(env[key]);
        }
        return defVal !== undefined ? defVal : "";
      });
    }

    for (let i = 0; i < line.length; i++) {
      const ch = line[i];
      if (ch === "\\" && !inSingle && i + 1 < line.length) {
        current += line[++i];
        continue;
      }
      if (ch === "'" && !inDouble) {
        inSingle = !inSingle;
        hadQuote = true;
        continue;
      }
      if (ch === '"' && !inSingle) {
        inDouble = !inDouble;
        hadQuote = true;
        continue;
      }
      if (!inSingle && !inDouble && /\s/.test(ch)) {
        if (current.length > 0 || hadQuote) {
          tokens.push({ val: current, quoted: hadQuote });
          current = "";
          hadQuote = false;
        }
        continue;
      }
      if (!inSingle && ch === "$") {
        // Check ${...} or $VAR
        const rest = line.slice(i);
        const match = rest.match(/^\$\{([A-Za-z_][A-Za-z0-9_]*)(?::-([^}]*))?\}|^\$([A-Za-z_][A-Za-z0-9_]*|\?)/);
        if (match) {
          const expanded = expandVars(match[0]);
          if (!inDouble && /\s/.test(expanded)) {
            // Unquoted word splitting!
            const parts = expanded.trim().split(/\s+/);
            if (current.length > 0) {
              tokens.push({ val: current + parts.shift(), quoted: false });
              current = "";
            }
            for (let p = 0; p < parts.length - 1; p++) {
              tokens.push({ val: parts[p], quoted: false });
            }
            current = parts[parts.length - 1] || "";
          } else {
            current += expanded;
          }
          i += match[0].length - 1;
          continue;
        }
      }
      current += ch;
    }

    if (inSingle || inDouble) {
      throw new Error("bash: syntax error: unterminated quote");
    }
    if (current.length > 0 || hadQuote) {
      tokens.push({ val: current, quoted: hadQuote });
    }
    return tokens;
  }

  function createSandbox(initialState) {
    const state = clone(
      initialState || {
        cwd: "/home/devops",
        user: "devops",
        env: { HOME: "/home/devops", USER: "devops", PATH: "/usr/local/bin:/usr/bin:/bin" },
        files: {},
        procs: [],
        ports: [],
      }
    );
    state.env = state.env || {};
    state.env["?"] = "0";
    state.opts = { e: false, u: false, pipefail: false };
    state.traps = {};

    function listDir(dirPath) {
      const prefix = dirPath === "/" ? "/" : dirPath + "/";
      const entries = new Map();
      for (const [p, meta] of Object.entries(state.files)) {
        if (!p.startsWith(prefix)) continue;
        const rest = p.slice(prefix.length);
        if (!rest) continue;
        const slashIdx = rest.indexOf("/");
        if (slashIdx === -1) {
          entries.set(rest, { name: rest, ...meta });
        } else {
          const dname = rest.slice(0, slashIdx);
          if (!entries.has(dname)) {
            entries.set(dname, { name: dname, type: "dir", mode: "0755", owner: "devops", group: "devops", content: "" });
          }
        }
      }
      return Array.from(entries.values()).sort((a, b) => a.name.localeCompare(b.name));
    }

    function runSingleCmd(rawCmd, stdinText) {
      let stdout = "";
      let stderr = "";
      let exitCode = 0;

      // Strip redirections from rawCmd carefully (>, >>, 2>&1, 2>/dev/null)
      let redirectOut = null;
      let redirectAppend = false;
      let mergeErr = false;
      let dropErr = false;

      let cleaned = rawCmd;
      if (/\s2>&1/.test(cleaned)) {
        mergeErr = true;
        cleaned = cleaned.replace(/\s2>&1/g, "");
      }
      if (/\s2>\s*\/dev\/null/.test(cleaned)) {
        dropErr = true;
        cleaned = cleaned.replace(/\s2>\s*\/dev\/null/g, "");
      }
      const redirMatch = cleaned.match(/\s(>>|>)\s*([^\s]+)\s*$/);
      if (redirMatch) {
        redirectAppend = redirMatch[1] === ">>";
        redirectOut = redirMatch[2];
        cleaned = cleaned.slice(0, redirMatch.index);
      }

      // Check strict set -u for unset $VAR references before tokenizing
      if (state.opts.u) {
        const varRefs = cleaned.match(/\$(?:([A-Za-z_][A-Za-z0-9_]*)|\{([A-Za-z_][A-Za-z0-9_]*)(?::-([^}]*))?\})/g) || [];
        for (const ref of varRefs) {
          if (ref.includes(":-")) continue;
          const name = ref.replace(/[\$\{\}]/g, "");
          if (!Object.prototype.hasOwnProperty.call(state.env, name)) {
            return { stdout: "", stderr: `bash: ${name}: unbound variable`, exitCode: 1 };
          }
        }
      }

      // Handle inline assignment: VAR=value or export VAR=value
      const assignMatch = cleaned.trim().match(/^(?:export\s+)?([A-Za-z_][A-Za-z0-9_]*)=(.*)$/);
      if (assignMatch && !cleaned.trim().startsWith("export ") || (assignMatch && cleaned.trim().startsWith("export ") && !/\s/.test(assignMatch[2].replace(/(["']).*?\1/g, "")))) {
        const varName = assignMatch[1];
        let val = assignMatch[2].trim();
        if ((val.startsWith('"') && val.endsWith('"')) || (val.startsWith("'") && val.endsWith("'"))) {
          val = val.slice(1, -1);
        }
        // Basic $(...) command substitution
        const cmdSub = val.match(/^\$\((.+)\)$/);
        if (cmdSub) {
          const subRes = executePipeline(cmdSub[1]);
          val = subRes.stdout.replace(/\n+$/, "");
        }
        state.env[varName] = val;
        return { stdout: "", stderr: "", exitCode: 0 };
      }

      let tokens;
      try {
        tokens = tokenizeShell(cleaned.trim(), state.env);
      } catch (err) {
        return { stdout: "", stderr: err.message, exitCode: 2 };
      }
      if (tokens.length === 0) return { stdout: "", stderr: "", exitCode: 0 };

      const cmd = tokens[0].val;
      const args = tokens.slice(1).map((t) => t.val);

      switch (cmd) {
        case "echo": {
          const noNewline = args[0] === "-n";
          const body = (noNewline ? args.slice(1) : args).join(" ");
          stdout = body + (noNewline ? "" : "\n");
          break;
        }
        case "printf": {
          const fmt = args[0] || "";
          let idx = 1;
          stdout = fmt
            .replace(/\\n/g, "\n")
            .replace(/%s/g, () => (idx < args.length ? args[idx++] : ""));
          break;
        }
        case "pwd": {
          stdout = state.cwd + "\n";
          break;
        }
        case "whoami": {
          stdout = state.user + "\n";
          break;
        }
        case "id": {
          stdout = `uid=1000(${state.user}) gid=1000(${state.user}) groups=1000(${state.user})\n`;
          break;
        }
        case "uname": {
          stdout = args.includes("-a")
            ? "Linux prod-app-01 6.8.0-45-generic #45-Ubuntu SMP x86_64 GNU/Linux\n"
            : "Linux\n";
          break;
        }
        case "cd": {
          const target = resolvePath(state.cwd, args[0] || "~");
          state.cwd = target;
          break;
        }
        case "env": {
          stdout = Object.entries(state.env)
            .filter(([k]) => k !== "?")
            .map(([k, v]) => `${k}=${v}`)
            .join("\n") + "\n";
          break;
        }
        case "export": {
          for (const a of args) {
            const eq = a.indexOf("=");
            if (eq !== -1) state.env[a.slice(0, eq)] = a.slice(eq + 1);
          }
          break;
        }
        case "set": {
          for (const a of args) {
            if (a.startsWith("-") && !a.startsWith("-o")) {
              if (a.includes("e")) state.opts.e = true;
              if (a.includes("u")) state.opts.u = true;
              if (a.includes("x")) state.opts.x = true;
            }
            if (a === "-o" && args.includes("pipefail")) state.opts.pipefail = true;
          }
          break;
        }
        case "true": {
          exitCode = 0;
          break;
        }
        case "false": {
          exitCode = 1;
          break;
        }
        case "exit": {
          exitCode = parseInt(args[0] || "0", 10) || 0;
          break;
        }
        case "trap": {
          if (args.length >= 2) {
            state.traps[args[1]] = args[0];
          }
          break;
        }
        case "mkdir": {
          const targets = args.filter((a) => !a.startsWith("-"));
          for (const t of targets) {
            const p = resolvePath(state.cwd, t);
            state.files[p] = state.files[p] || { type: "dir", mode: "0755", owner: state.user, group: state.user, content: "" };
          }
          break;
        }
        case "touch": {
          const targets = args.filter((a) => !a.startsWith("-"));
          for (const t of targets) {
            const p = resolvePath(state.cwd, t);
            if (!state.files[p]) {
              state.files[p] = { type: "file", mode: "0644", owner: state.user, group: state.user, content: "" };
            }
          }
          break;
        }
        case "rm": {
          const targets = args.filter((a) => !a.startsWith("-"));
          const force = args.some((a) => a.includes("f"));
          for (const t of targets) {
            const p = resolvePath(state.cwd, t);
            if (state.files[p]) {
              delete state.files[p];
            } else if (!force) {
              stderr += `rm: cannot remove '${t}': No such file or directory\n`;
              exitCode = 1;
            }
          }
          break;
        }
        case "ls": {
          const flags = args.filter((a) => a.startsWith("-")).join("");
          const longFmt = flags.includes("l");
          const showAll = flags.includes("a");
          const targets = args.filter((a) => !a.startsWith("-"));
          const targetPath = resolvePath(state.cwd, targets[0] || state.cwd);

          if (state.files[targetPath] && state.files[targetPath].type !== "dir") {
            const f = state.files[targetPath];
            const name = targetPath.split("/").pop();
            stdout = longFmt
              ? `-${modeToRwx(f.mode)} 1 ${f.owner || "devops"} ${f.group || "devops"} ${String((f.content || "").length).padStart(5)} Nov 21 03:58 ${targetPath}\n`
              : `${name}\n`;
          } else {
            const items = listDir(targetPath).filter((item) => showAll || !item.name.startsWith("."));
            if (items.length === 0 && !state.files[targetPath]) {
              stderr = `ls: cannot access '${targets[0] || targetPath}': No such file or directory\n`;
              exitCode = 2;
            } else if (longFmt) {
              stdout = items
                .map((item) => {
                  const prefix = item.type === "dir" ? "d" : "-";
                  const size = String((item.content || "").length).padStart(5);
                  return `${prefix}${modeToRwx(item.mode)} 1 ${item.owner || "devops"} ${item.group || "devops"} ${size} Nov 21 03:58 ${item.name}`;
                })
                .join("\n") + (items.length ? "\n" : "");
            } else {
              stdout = items.map((i) => i.name).join("  ") + (items.length ? "\n" : "");
            }
          }
          break;
        }
        case "cat": {
          const targets = args.filter((a) => !a.startsWith("-"));
          if (targets.length === 0) {
            stdout = stdinText;
          } else {
            for (const t of targets) {
              const p = resolvePath(state.cwd, t);
              if (!state.files[p] || state.files[p].type === "dir") {
                stderr += `cat: ${t}: No such file or directory\n`;
                exitCode = 1;
              } else {
                stdout += state.files[p].content;
                if (stdout && !stdout.endsWith("\n")) stdout += "\n";
              }
            }
          }
          break;
        }
        case "head":
        case "tail": {
          let n = 10;
          const files = [];
          for (let i = 0; i < args.length; i++) {
            if (args[i] === "-n" && args[i + 1]) {
              n = parseInt(args[++i], 10) || 10;
            } else if (/^-\d+$/.test(args[i])) {
              n = parseInt(args[i].slice(1), 10) || 10;
            } else {
              files.push(args[i]);
            }
          }
          let text = stdinText;
          if (files.length > 0) {
            const p = resolvePath(state.cwd, files[0]);
            if (!state.files[p]) {
              stderr = `${cmd}: cannot open '${files[0]}': No such file or directory\n`;
              exitCode = 1;
              break;
            }
            text = state.files[p].content;
          }
          const lines = text.replace(/\n$/, "").split("\n");
          const sliced = cmd === "head" ? lines.slice(0, n) : lines.slice(-n);
          stdout = sliced.join("\n") + "\n";
          break;
        }
        case "grep": {
          let ignoreCase = false;
          let invert = false;
          let countOnly = false;
          const positional = [];
          for (const a of args) {
            if (a.startsWith("-") && a.length > 1) {
              if (a.includes("i")) ignoreCase = true;
              if (a.includes("v")) invert = true;
              if (a.includes("c")) countOnly = true;
            } else {
              positional.push(a);
            }
          }
          const pattern = positional[0];
          if (!pattern) {
            stderr = "grep: missing pattern\n";
            exitCode = 2;
            break;
          }
          let text = stdinText;
          if (positional[1]) {
            const p = resolvePath(state.cwd, positional[1]);
            if (!state.files[p]) {
              stderr = `grep: ${positional[1]}: No such file or directory\n`;
              exitCode = 2;
              break;
            }
            text = state.files[p].content;
          }
          const regex = new RegExp(pattern, ignoreCase ? "i" : "");
          const lines = text ? text.replace(/\n$/, "").split("\n") : [];
          const matched = lines.filter((ln) => {
            const hit = regex.test(ln);
            return invert ? !hit : hit;
          });
          if (countOnly) {
            stdout = `${matched.length}\n`;
          } else {
            stdout = matched.length ? matched.join("\n") + "\n" : "";
          }
          if (matched.length === 0) exitCode = 1;
          break;
        }
        case "awk": {
          let sep = /\s+/;
          const positional = [];
          for (let i = 0; i < args.length; i++) {
            if (args[i] === "-F" && args[i + 1]) {
              sep = args[++i];
            } else if (args[i].startsWith("-F") && args[i].length > 2) {
              sep = args[i].slice(2);
            } else {
              positional.push(args[i]);
            }
          }
          const prog = positional[0] || "";
          let text = stdinText;
          if (positional[1]) {
            const p = resolvePath(state.cwd, positional[1]);
            if (!state.files[p]) {
              stderr = `awk: fatal: cannot open file '${positional[1]}'\n`;
              exitCode = 2;
              break;
            }
            text = state.files[p].content;
          }
          const lines = text ? text.replace(/\n$/, "").split("\n") : [];
          // Support optional /pattern/ filter + {print $1, $2...}
          const patMatch = prog.match(/^\s*\/([^/]+)\/\s*\{(.+)\}\s*$/);
          const blockMatch = prog.match(/\{(.+)\}/);
          const filterRe = patMatch ? new RegExp(patMatch[1]) : null;
          const body = patMatch ? patMatch[2] : blockMatch ? blockMatch[1] : "";
          const printMatch = body.match(/print\s+(.+)/);

          const outLines = [];
          for (const ln of lines) {
            if (filterRe && !filterRe.test(ln)) continue;
            const fields = typeof sep === "string" ? ln.split(sep) : ln.trim().split(sep);
            if (printMatch) {
              const cols = printMatch[1].split(",").map((c) => c.trim());
              const rendered = cols.map((c) => {
                if (c === "$0") return ln;
                const m = c.match(/^\$(\d+)$/);
                if (m) return fields[parseInt(m[1], 10) - 1] ?? "";
                return c.replace(/^["']|["']$/g, "");
              });
              outLines.push(rendered.join(" "));
            }
          }
          stdout = outLines.length ? outLines.join("\n") + "\n" : "";
          break;
        }
        case "cut": {
          let delim = "\t";
          let fieldsSpec = "1";
          const positional = [];
          for (let i = 0; i < args.length; i++) {
            if (args[i] === "-d" && args[i + 1]) delim = args[++i];
            else if (args[i].startsWith("-d")) delim = args[i].slice(2);
            else if (args[i] === "-f" && args[i + 1]) fieldsSpec = args[++i];
            else if (args[i].startsWith("-f")) fieldsSpec = args[i].slice(2);
            else positional.push(args[i]);
          }
          const idxList = fieldsSpec.split(",").map((n) => parseInt(n, 10) - 1);
          const text = positional[0] && state.files[resolvePath(state.cwd, positional[0])]
            ? state.files[resolvePath(state.cwd, positional[0])].content
            : stdinText;
          const lines = text ? text.replace(/\n$/, "").split("\n") : [];
          stdout = lines
            .map((ln) => {
              const parts = ln.split(delim);
              return idxList.map((idx) => parts[idx] ?? "").join(delim);
            })
            .join("\n") + (lines.length ? "\n" : "");
          break;
        }
        case "sort": {
          const flags = args.filter((a) => a.startsWith("-")).join("");
          const rev = flags.includes("r");
          const num = flags.includes("n");
          const files = args.filter((a) => !a.startsWith("-"));
          let text = stdinText;
          if (files[0] && state.files[resolvePath(state.cwd, files[0])]) {
            text = state.files[resolvePath(state.cwd, files[0])].content;
          }
          const lines = text ? text.replace(/\n$/, "").split("\n") : [];
          lines.sort((a, b) => {
            if (num) {
              const na = parseFloat(a.trim()) || 0;
              const nb = parseFloat(b.trim()) || 0;
              return na - nb;
            }
            return a.localeCompare(b);
          });
          if (rev) lines.reverse();
          stdout = lines.length ? lines.join("\n") + "\n" : "";
          break;
        }
        case "uniq": {
          const count = args.includes("-c");
          const lines = stdinText ? stdinText.replace(/\n$/, "").split("\n") : [];
          const out = [];
          for (let i = 0; i < lines.length; i++) {
            let c = 1;
            while (i + 1 < lines.length && lines[i + 1] === lines[i]) {
              c++;
              i++;
            }
            out.push(count ? `${String(c).padStart(7)} ${lines[i]}` : lines[i]);
          }
          stdout = out.length ? out.join("\n") + "\n" : "";
          break;
        }
        case "wc": {
          const lines = stdinText ? stdinText.replace(/\n$/, "").split("\n") : [];
          if (args.includes("-l")) {
            stdout = `${stdinText ? lines.length : 0}\n`;
          } else {
            const words = stdinText.trim() ? stdinText.trim().split(/\s+/).length : 0;
            stdout = `${lines.length} ${words} ${stdinText.length}\n`;
          }
          break;
        }
        case "jq": {
          const rawOut = args.includes("-r");
          const expr = args.find((a) => !a.startsWith("-")) || ".";
          const fileArg = args.filter((a) => !a.startsWith("-"))[1];
          let text = stdinText;
          if (fileArg) {
            const p = resolvePath(state.cwd, fileArg);
            if (!state.files[p]) {
              stderr = `jq: error: Could not open file ${fileArg}: No such file or directory\n`;
              exitCode = 2;
              break;
            }
            text = state.files[p].content;
          }
          const lines = text.trim() ? text.trim().split("\n") : [];
          const results = [];
          for (const ln of lines) {
            let obj;
            try {
              obj = JSON.parse(ln);
            } catch (_) {
              continue;
            }
            // Handle select(.field == "val") | .other or simple .field
            const pipeParts = expr.split("|").map((s) => s.trim());
            let include = true;
            let val = obj;
            for (const part of pipeParts) {
              const selMatch = part.match(/^select\(\s*\.([A-Za-z0-9_]+)\s*==\s*["']([^"']+)["']\s*\)$/);
              if (selMatch) {
                if (String(val?.[selMatch[1]]) !== selMatch[2]) {
                  include = false;
                  break;
                }
              } else if (part.startsWith(".")) {
                const keys = part.slice(1).split(".").filter(Boolean);
                for (const k of keys) val = val?.[k];
              }
            }
            if (include) {
              results.push(rawOut && typeof val === "string" ? val : JSON.stringify(val));
            }
          }
          stdout = results.length ? results.join("\n") + "\n" : "";
          break;
        }
        case "chmod": {
          const modeArg = args[0];
          const targets = args.slice(1);
          if (!modeArg || targets.length === 0) {
            stderr = "chmod: missing operand\n";
            exitCode = 1;
            break;
          }
          for (const t of targets) {
            const p = resolvePath(state.cwd, t);
            if (!state.files[p]) {
              stderr += `chmod: cannot access '${t}': No such file or directory\n`;
              exitCode = 1;
              continue;
            }
            if (/^[0-7]{3,4}$/.test(modeArg)) {
              state.files[p].mode = modeArg.slice(-3).padStart(4, "0");
            } else if (modeArg === "+x" || modeArg === "u+x") {
              const cur = parseInt(state.files[p].mode.slice(-3), 8);
              const next = (cur | (modeArg === "+x" ? 0o111 : 0o100)).toString(8).padStart(4, "0");
              state.files[p].mode = next;
            } else if (modeArg === "go-rwx" || modeArg === "og-rwx") {
              const cur = parseInt(state.files[p].mode.slice(-3), 8);
              const next = (cur & 0o700).toString(8).padStart(4, "0");
              state.files[p].mode = next;
            }
          }
          break;
        }
        case "chown": {
          const ownerSpec = args[0] || "";
          const targets = args.slice(1);
          const [newOwner, newGroup] = ownerSpec.split(":");
          for (const t of targets) {
            const p = resolvePath(state.cwd, t);
            if (!state.files[p]) {
              stderr += `chown: cannot access '${t}': No such file or directory\n`;
              exitCode = 1;
            } else {
              if (newOwner) state.files[p].owner = newOwner;
              if (newGroup) state.files[p].group = newGroup;
            }
          }
          break;
        }
        case "ps": {
          const header = "USER       PID %CPU %MEM STAT COMMAND";
          const rows = state.procs.map(
            (p) =>
              `${String(p.user).padEnd(10)} ${String(p.pid).padStart(3)}  ${String(p.cpu).padStart(4)}  ${String(p.mem).padStart(4)} ${String(p.stat || "Ssl").padEnd(4)} ${p.cmd}`
          );
          stdout = [header, ...rows].join("\n") + "\n";
          break;
        }
        case "kill": {
          let sig = "TERM";
          const pids = [];
          for (const a of args) {
            if (a.startsWith("-")) {
              sig = a.replace(/^-/, "").toUpperCase();
              if (sig === "15" || sig === "SIGTERM") sig = "TERM";
              if (sig === "9" || sig === "SIGKILL") sig = "KILL";
            } else {
              pids.push(parseInt(a, 10));
            }
          }
          for (const pid of pids) {
            const idx = state.procs.findIndex((p) => p.pid === pid);
            if (idx === -1) {
              stderr += `bash: kill: (${pid}) - No such process\n`;
              exitCode = 1;
            } else {
              const proc = state.procs[idx];
              proc.killedBy = sig;
              state.procs.splice(idx, 1);
              state.ports = state.ports.filter((pt) => pt.pid !== pid);
              stdout += sig === "TERM"
                ? `[signal SIGTERM -> PID ${pid} (${proc.cmd.split(" ")[0]}) graceful shutdown]\n`
                : `[signal SIGKILL -> PID ${pid} terminated immediately without cleanup]\n`;
            }
          }
          break;
        }
        case "ss": {
          const header = "Netid State   Recv-Q Send-Q Local Address:Port  Process";
          const rows = state.ports.map(
            (pt) => `${pt.proto.padEnd(5)} LISTEN  0      128    0.0.0.0:${String(pt.port).padEnd(10)} users:(("${pt.proc}",pid=${pt.pid}))`
          );
          stdout = [header, ...rows].join("\n") + "\n";
          break;
        }
        case "df": {
          stdout = [
            "Filesystem      Size  Used Avail Use% Mounted on",
            "/dev/nvme0n1p1   50G   41G  9.0G  82% /",
            "/dev/nvme1n1    100G   34G   66G  34% /var/lib/docker",
          ].join("\n") + "\n";
          break;
        }
        case "free": {
          stdout = [
            "               total        used        free      shared  buff/cache   available",
            "Mem:           7.8Gi       5.1Gi       420Mi        48Mi       2.3Gi       2.4Gi",
            "Swap:             0B          0B          0B",
          ].join("\n") + "\n";
          break;
        }
        case "[":
        case "test": {
          let expr = cmd === "[" ? args.slice(0, -1) : args;
          let negate = false;
          if (expr[0] === "!") {
            negate = true;
            expr = expr.slice(1);
          }
          if (expr[0] === "-f") {
            const p = resolvePath(state.cwd, expr[1] || "");
            exitCode = state.files[p] && state.files[p].type === "file" ? 0 : 1;
          } else if (expr[0] === "-d") {
            const p = resolvePath(state.cwd, expr[1] || "");
            exitCode = state.files[p] && state.files[p].type === "dir" ? 0 : 1;
          } else if (expr[0] === "-n") {
            exitCode = expr[1] ? 0 : 1;
          } else if (expr[0] === "-z") {
            exitCode = !expr[1] ? 0 : 1;
          } else if (expr[1] === "==" || expr[1] === "=") {
            exitCode = expr[0] === expr[2] ? 0 : 1;
          } else if (expr[1] === "!=") {
            exitCode = expr[0] !== expr[2] ? 0 : 1;
          } else {
            exitCode = expr[0] ? 0 : 1;
          }
          if (negate) exitCode = exitCode === 0 ? 1 : 0;
          break;
        }
        case "git": {
          const g = state.git || (state.git = { branch: "main", commits: ["init: initial repo"], tags: [], staged: [], hooks: {} });
          const sub = args[0] || "status";
          if (sub === "status") {
            stdout = `On branch ${g.branch}\n${g.staged.length ? "Changes to be committed:\n  " + g.staged.join("\n  ") : "nothing to commit, working tree clean"}\n`;
          } else if (sub === "branch") {
            if (args[1]) {
              g.branch = args[1];
              stdout = `Created and noted branch '${args[1]}'\n`;
            } else {
              stdout = `* ${g.branch}\n`;
            }
          } else if (sub === "checkout" || sub === "switch") {
            const create = args.includes("-b") || args.includes("-c");
            const target = args.slice(1).filter((a) => !a.startsWith("-"))[0] || "main";
            g.branch = target;
            stdout = `${create ? "Switched to a new branch" : "Switched to branch"} '${target}'\n`;
          } else if (sub === "add") {
            g.staged.push(...args.slice(1));
            stdout = "";
          } else if (sub === "commit") {
            const mIdx = args.indexOf("-m");
            const msg = mIdx !== -1 ? args[mIdx + 1] : "";
            if (!msg) {
              stderr = "error: switch `m` requires a value\n";
              exitCode = 1;
            } else if (g.hooks && g.hooks["commit-msg"] === "conventional" && !/^(feat|fix|chore|docs|ci|refactor|perf|test)(\(.+\))?!?: .+/.test(msg)) {
              stderr = `commit-msg hook rejected: '${msg}' does not match Conventional Commits (type(scope): summary)\n`;
              exitCode = 1;
            } else {
              g.commits.unshift(msg);
              g.staged = [];
              stdout = `[${g.branch} ${Math.random().toString(16).slice(2, 9)}] ${msg}\n`;
            }
          } else if (sub === "tag") {
            const tagName = args.slice(1).filter((a) => !a.startsWith("-"))[0];
            if (tagName) {
              if (!/^v?\d+\.\d+\.\d+(-[A-Za-z0-9.]+)?$/.test(tagName)) {
                stderr = `tag warning: '${tagName}' is not strict SemVer\n`;
              }
              g.tags.push(tagName);
              stdout = `Created tag ${tagName}\n`;
            } else {
              stdout = g.tags.join("\n") + (g.tags.length ? "\n" : "");
            }
          } else if (sub === "log") {
            stdout = g.commits.map((c, idx) => `${(0xa1b2c3d + idx).toString(16)} ${c}`).join("\n") + "\n";
          } else if (sub === "rebase") {
            const onto = args[1] || "main";
            stdout = `Successfully rebased and updated refs/heads/${g.branch} onto ${onto}.\n`;
          } else {
            stdout = `git ${args.join(" ")} executed on ${g.branch}\n`;
          }
          break;
        }
        case "dig": {
          const dns = state.dns || {};
          const host = args.find((a) => !a.startsWith("-") && !a.startsWith("+") && !/^(A|AAAA|CNAME|TXT|MX)$/i.test(a)) || "api.example.com";
          const rtype = (args.find((a) => /^(A|AAAA|CNAME|TXT|MX)$/i.test(a)) || "A").toUpperCase();
          const rec = dns[host] || dns[`${host}:${rtype}`];
          if (!rec) {
            stdout = `; <<>> DiG 9.18 <<>> ${host} ${rtype}\n;; status: NXDOMAIN\n`;
            exitCode = 1;
          } else if (args.includes("+short")) {
            stdout = `${rec}\n`;
          } else {
            stdout = `; <<>> DiG 9.18 <<>> ${host} ${rtype}\n;; status: NOERROR\n;; ANSWER SECTION:\n${host}.\t300\tIN\t${rtype}\t${rec}\n`;
          }
          break;
        }
        case "curl": {
          const http = state.http || {};
          const url = args.find((a) => /^https?:\/\//.test(a)) || "";
          const cflags = args.filter((a) => a.startsWith("-") && !a.startsWith("--")).join("");
          const headOnly = cflags.includes("I");
          const showStatus = cflags.includes("s") || cflags.includes("i") || cflags.includes("v") || headOnly;
          const failOnErr = cflags.includes("f");
          const entry = http[url];
          if (!entry) {
            stderr = `curl: (6) Could not resolve or connect to '${url || "(empty)"}'\n`;
            exitCode = 6;
          } else {
            const status = entry.status || 200;
            if (failOnErr && status >= 400) {
              stderr = `curl: (22) The requested URL returned error: ${status}\n`;
              exitCode = 22;
            } else {
              if (showStatus) {
                stdout += `HTTP/2 ${status}\n`;
                if (entry.headers) {
                  for (const [hk, hv] of Object.entries(entry.headers)) stdout += `${hk}: ${hv}\n`;
                }
                stdout += "\n";
              }
              if (!headOnly && entry.body !== undefined) {
                stdout += typeof entry.body === "string" ? entry.body + "\n" : JSON.stringify(entry.body) + "\n";
              }
            }
          }
          break;
        }
        case "openssl": {
          const certs = state.certs || {};
          const target = args.find((a) => certs[a]) || Object.keys(certs)[0];
          const cert = certs[target];
          if (!cert) {
            stderr = "openssl: no certificate matched in sandbox\n";
            exitCode = 1;
          } else {
            stdout = `subject=CN = ${cert.cn}\nissuer=CN = ${cert.issuer || "Let's Encrypt R10"}\nnotAfter=${cert.notAfter}\nDNS:${cert.san || cert.cn}\n`;
          }
          break;
        }
        case "systemctl": {
          const svcs = state.services || {};
          const action = args[0] || "status";
          const unit = args[1] || "";
          if (!svcs[unit]) {
            stderr = `Unit ${unit} could not be found.\n`;
            exitCode = 4;
          } else if (action === "restart" || action === "start") {
            svcs[unit].active = "active";
            stdout = "";
          } else if (action === "stop") {
            svcs[unit].active = "inactive";
            stdout = "";
          } else if (action === "is-active") {
            stdout = `${svcs[unit].active}\n`;
            exitCode = svcs[unit].active === "active" ? 0 : 3;
          } else {
            stdout = `● ${unit} - ${svcs[unit].desc || "Managed Service"}\n   Active: ${svcs[unit].active} (${svcs[unit].sub || "running"})\n   Memory: ${svcs[unit].mem || "256.0M"} (max: ${svcs[unit].limit || "512.0M"})\n`;
          }
          break;
        }
        case "journalctl": {
          const jlogs = state.journal || [];
          const uIdx = args.indexOf("-u");
          const unit = uIdx !== -1 ? args[uIdx + 1] : null;
          const filtered = unit ? jlogs.filter((l) => l.includes(unit)) : jlogs;
          stdout = filtered.join("\n") + (filtered.length ? "\n" : "");
          break;
        }
        case "kubectl": {
          const k = state.k8s || { ns: "default", pods: [], deployments: [], services: [] };
          const sub = args[0] || "get";
          const resType = args[1] || "pods";
          if (sub === "get") {
            if (resType.startsWith("po")) {
              stdout = "NAME                          READY   STATUS             RESTARTS   AGE\n" +
                (k.pods || []).map((p) => `${p.name.padEnd(29)} ${p.ready.padEnd(7)} ${p.status.padEnd(18)} ${String(p.restarts).padEnd(10)} ${p.age}`).join("\n") + "\n";
            } else if (resType.startsWith("deploy")) {
              stdout = "NAME              READY   UP-TO-DATE   AVAILABLE   AGE\n" +
                (k.deployments || []).map((d) => `${d.name.padEnd(17)} ${d.ready.padEnd(7)} ${String(d.upToDate).padEnd(12)} ${String(d.available).padEnd(11)} ${d.age}`).join("\n") + "\n";
            } else if (resType.startsWith("svc")) {
              stdout = "NAME          TYPE        CLUSTER-IP     PORT(S)    AGE\n" +
                (k.services || []).map((s) => `${s.name.padEnd(13)} ${s.type.padEnd(11)} ${s.ip.padEnd(14)} ${s.ports.padEnd(10)} ${s.age}`).join("\n") + "\n";
            }
          } else if (sub === "logs") {
            const podName = args.find((a) => !a.startsWith("-") && a !== "logs");
            const pod = (k.pods || []).find((p) => p.name === podName);
            stdout = pod ? (pod.logs || "(empty log)\n") : "";
            if (!pod) { stderr = `Error from server (NotFound): pods "${podName}" not found\n`; exitCode = 1; }
          } else if (sub === "describe") {
            const podName = args[2];
            const pod = (k.pods || []).find((p) => p.name === podName);
            stdout = pod ? (pod.describe || `Name: ${pod.name}\nStatus: ${pod.status}\n`) : "";
          } else if (sub === "rollout") {
            stdout = args[1] === "undo"
              ? `deployment.apps/${args[2] || "api"} rolled back\n`
              : `deployment "${args[2] || "api"}" successfully rolled out\n`;
          } else {
            stdout = `kubectl ${args.join(" ")} completed.\n`;
          }
          break;
        }
        default: {
          stderr = `bash: ${cmd}: command not found in sandbox (available: ls, cat, head, tail, grep, awk, cut, sort, uniq, wc, jq, chmod, chown, ps, kill, ss, df, free, git, curl, dig, openssl, systemctl, journalctl, kubectl, echo, export, set, mkdir, touch, rm, cd, pwd, test)\n`;
          exitCode = 127;
        }
      }

      if (dropErr) stderr = "";
      if (mergeErr && stderr) {
        stdout += stderr;
        stderr = "";
      }
      if (redirectOut) {
        const target = resolvePath(state.cwd, redirectOut);
        if (target !== "/dev/null") {
          const prev = redirectAppend && state.files[target] ? state.files[target].content : "";
          state.files[target] = {
            type: "file",
            mode: state.files[target]?.mode || "0644",
            owner: state.user,
            group: state.user,
            content: prev + stdout,
          };
        }
        stdout = "";
      }

      state.env["?"] = String(exitCode);
      return { stdout, stderr, exitCode };
    }

    function executePipeline(pipelineStr) {
      // Split by | outside quotes
      const stages = [];
      let cur = "";
      let q = null;
      for (let i = 0; i < pipelineStr.length; i++) {
        const ch = pipelineStr[i];
        if ((ch === '"' || ch === "'") && pipelineStr[i - 1] !== "\\") {
          q = q === ch ? null : q ? q : ch;
        }
        if (!q && ch === "|" && pipelineStr[i + 1] !== "|" && pipelineStr[i - 1] !== "|") {
          stages.push(cur.trim());
          cur = "";
        } else {
          cur += ch;
        }
      }
      if (cur.trim()) stages.push(cur.trim());

      let stdin = "";
      let allErr = "";
      let lastCode = 0;
      let pipefailCode = 0;

      for (const stage of stages) {
        const res = runSingleCmd(stage, stdin);
        stdin = res.stdout;
        allErr += res.stderr;
        lastCode = res.exitCode;
        if (res.exitCode !== 0) pipefailCode = res.exitCode;
      }

      const finalCode = state.opts.pipefail && pipefailCode !== 0 ? pipefailCode : lastCode;
      state.env["?"] = String(finalCode);
      return { stdout: stdin, stderr: allErr, exitCode: finalCode };
    }

    function runScript(scriptText) {
      const lines = scriptText.split(/\r?\n/);
      let out = "";
      let err = "";
      let lastCode = 0;
      let aborted = false;

      // Support simple single-level if ... then ... [else ...] fi blocks
      let i = 0;
      while (i < lines.length && !aborted) {
        let raw = lines[i].trim();
        i++;
        if (!raw || raw.startsWith("#")) continue;

        if (raw.startsWith("if ")) {
          const condCmd = raw.replace(/^if\s+/, "").replace(/;\s*then\s*$/, "").trim();
          const thenLines = [];
          const elseLines = [];
          let inElse = false;
          while (i < lines.length) {
            const inner = lines[i].trim();
            i++;
            if (inner === "then") continue;
            if (inner === "else") {
              inElse = true;
              continue;
            }
            if (inner === "fi") break;
            (inElse ? elseLines : thenLines).push(inner);
          }
          const condRes = executePipeline(condCmd);
          out += condRes.stdout;
          err += condRes.stderr;
          const branch = condRes.exitCode === 0 ? thenLines : elseLines;
          for (const bLine of branch) {
            if (!bLine || bLine.startsWith("#")) continue;
            const r = runLogicalLine(bLine);
            out += r.stdout;
            err += r.stderr;
            lastCode = r.exitCode;
            if (state.opts.e && lastCode !== 0) {
              aborted = true;
              break;
            }
          }
          continue;
        }

        const r = runLogicalLine(raw);
        out += r.stdout;
        err += r.stderr;
        lastCode = r.exitCode;
        if (state.opts.e && lastCode !== 0) {
          err += `[set -e] Script exited early on line with exit code ${lastCode}\n`;
          aborted = true;
        }
      }

      if (state.traps.EXIT) {
        const trapRes = executePipeline(state.traps.EXIT);
        out += trapRes.stdout;
        err += trapRes.stderr;
      }

      return { stdout: out, stderr: err, exitCode: lastCode, state };
    }

    function runLogicalLine(line) {
      // Split by && or || outside quotes
      const parts = [];
      let cur = "";
      let q = null;
      for (let i = 0; i < line.length; i++) {
        const ch = line[i];
        if ((ch === '"' || ch === "'") && line[i - 1] !== "\\") {
          q = q === ch ? null : q ? q : ch;
        }
        if (!q && ((ch === "&" && line[i + 1] === "&") || (ch === "|" && line[i + 1] === "|"))) {
          parts.push({ cmd: cur.trim(), op: line.slice(i, i + 2) });
          cur = "";
          i++;
        } else {
          cur += ch;
        }
      }
      if (cur.trim()) parts.push({ cmd: cur.trim(), op: null });

      let out = "";
      let err = "";
      let code = 0;

      for (let idx = 0; idx < parts.length; idx++) {
        const p = parts[idx];
        const res = executePipeline(p.cmd);
        out += res.stdout;
        err += res.stderr;
        code = res.exitCode;
        if (p.op === "&&" && code !== 0) break;
        if (p.op === "||" && code === 0) break;
      }
      return { stdout: out, stderr: err, exitCode: code };
    }

    return { runScript, state };
  }

  /* ------------------------------------------------------------------
   * Config / Manifest / Pipeline Simulator (Docker, YAML, HCL, Proxy)
   * ------------------------------------------------------------------ */
  function simulateConfig(kind, code) {
    const lines = code.split(/\r?\n/);
    const nonEmpty = lines.filter((l) => l.trim() && !l.trim().startsWith("#"));
    let stdout = "";
    let stderr = "";
    let exitCode = 0;

    if (nonEmpty.length === 0) {
      return { stdout: "", stderr: `[${kind}] Empty configuration file.`, exitCode: 1, parsed: {} };
    }

    if (kind === "yaml") {
      // Check tabs & indentation consistency
      for (let idx = 0; idx < lines.length; idx++) {
        if (/^\t+/.test(lines[idx])) {
          stderr += `YAML syntax error on line ${idx + 1}: tabs are forbidden for indentation in YAML\n`;
          exitCode = 1;
        }
      }
      if (!exitCode) {
        const keys = nonEmpty.map((l) => l.trim().split(":")[0]).filter(Boolean);
        const kindMatch = code.match(/^\s*kind:\s*([A-Za-z0-9_]+)/m);
        const nameMatch = code.match(/^\s*name:\s*([^\s#]+)/m);
        const svcMatches = [...code.matchAll(/^\s{2}([a-zA-Z0-9_-]+):\s*$/gm)].map((m) => m[1]);
        if (kindMatch) {
          stdout = `[dry-run] Validated Kubernetes/Argo/Policy manifest: kind=${kindMatch[1]} name=${nameMatch ? nameMatch[1] : "(unnamed)"}\n`;
        } else if (code.includes("services:")) {
          stdout = `[compose config] Validated Compose YAML. Services detected: ${svcMatches.join(", ") || "(parsed)"}\n`;
        } else if (code.includes("jobs:") && code.includes("steps:")) {
          stdout = `[actionlint] Validated GitHub Actions workflow (${keys.length} statements).\n`;
        } else {
          stdout = `[yaml-check] Valid YAML structure (${nonEmpty.length} active lines).\n`;
        }
      }
      return { stdout, stderr, exitCode, parsed: { code } };
    }

    if (kind === "dockerfile") {
      const fromLines = nonEmpty.filter((l) => /^FROM\s+/i.test(l.trim()));
      if (fromLines.length === 0) {
        return { stdout: "", stderr: "Dockerfile error: missing FROM instruction\n", exitCode: 1, parsed: {} };
      }
      let out = [];
      let step = 1;
      for (const ln of nonEmpty) {
        const t = ln.trim();
        out.push(`Step ${step++}/${nonEmpty.length} : ${t}`);
        if (/^FROM\s+.*:latest\b/i.test(t)) {
          stderr += `WARN: Unpinned ':latest' tag in '${t}' — pin an explicit version/digest.\n`;
        }
      }
      const hasUser = nonEmpty.some((l) => /^USER\s+(?!root\b|0\b)/i.test(l.trim()));
      if (!hasUser) {
        stderr += "WARN: Container runs as root; add a non-root USER instruction.\n";
      }
      out.push(`Successfully built image (${fromLines.length} stage(s), non-root=${hasUser})`);
      return { stdout: out.join("\n") + "\n", stderr, exitCode: 0, parsed: { stages: fromLines.length, hasUser } };
    }

    if (kind === "hcl") {
      let depth = 0;
      for (let i = 0; i < lines.length; i++) {
        for (const ch of lines[i]) {
          if (ch === "{") depth++;
          if (ch === "}") depth--;
        }
      }
      if (depth !== 0) {
        return { stdout: "", stderr: `Error: Unbalanced braces in HCL configuration (depth=${depth})\n`, exitCode: 1, parsed: {} };
      }
      const resources = [...code.matchAll(/resource\s+"([^"]+)"\s+"([^"]+)"/g)].map((m) => `+ ${m[1]}.${m[2]}`);
      const modules = [...code.matchAll(/module\s+"([^"]+)"/g)].map((m) => `+ module.${m[1]}`);
      const items = [...resources, ...modules];
      stdout = `Success! The configuration is valid.\n\nTerraform Plan Summary:\n${items.length ? items.join("\n") : "(0 resources declared)"}\n\nPlan: ${items.length} to add, 0 to change, 0 to destroy.\n`;
      return { stdout, stderr, exitCode: 0, parsed: { resources, modules } };
    }

    if (kind === "python") {
      const hasDefOrImport = nonEmpty.some((l) => /^(import|from|def|class|with|for|if|async|@)\b/.test(l.trim()));
      const parenBalance = (code.match(/\(/g) || []).length - (code.match(/\)/g) || []).length;
      if (parenBalance !== 0) {
        return { stdout: "", stderr: `SyntaxError: unbalanced parentheses (diff=${parenBalance})\n`, exitCode: 1, parsed: {} };
      }
      if (!hasDefOrImport) {
        stderr += "WARN: Consider structuring DevOps scripts with imports and reusable functions.\n";
      }
      stdout = `[python3 -m py_compile] Bytecode syntax valid (${nonEmpty.length} statements).\n`;
      return { stdout, stderr, exitCode: 0, parsed: { code } };
    }

    if (kind === "k6") {
      const braceBalance = (code.match(/\{/g) || []).length - (code.match(/\}/g) || []).length;
      if (braceBalance !== 0) {
        return { stdout: "", stderr: `SyntaxError: unbalanced '{' / '}' in k6 script (diff=${braceBalance})\n`, exitCode: 1, parsed: {} };
      }
      stdout = `[k6 inspect] Script valid (${nonEmpty.length} lines). Thresholds & default() checked.\n`;
      return { stdout, stderr, exitCode: 0, parsed: { code } };
    }

    if (kind === "caddy" || kind === "nginx" || kind === "systemd" || kind === "properties" || kind === "promql" || kind === "ini") {
      let depth = 0;
      for (const ln of lines) {
        for (const ch of ln) {
          if (ch === "{") depth++;
          if (ch === "}") depth--;
        }
      }
      if ((kind === "caddy" || kind === "nginx") && depth !== 0) {
        return { stdout: "", stderr: `${kind}: syntax error — unbalanced '{' / '}' blocks\n`, exitCode: 1, parsed: {} };
      }
      stdout = `[${kind}] Syntax valid (${nonEmpty.length} directives evaluated).\n`;
      return { stdout, stderr, exitCode: 0, parsed: { code } };
    }

    return { stdout: `[ok] Evaluated ${nonEmpty.length} lines.\n`, stderr: "", exitCode: 0, parsed: { code } };
  }

  function runWithMode(cfg, code) {
    if (typeof cfg.runner === "function") {
      return cfg.runner(code, createSandbox(cfg.env));
    }
    if (cfg.mode && cfg.mode !== "bash") {
      return simulateConfig(cfg.mode, code);
    }
    return createSandbox(cfg.env).runScript(code);
  }

  /* ------------------------------------------------------------------
   * Mount Annotated Runnable Code Example
   * ------------------------------------------------------------------ */
  function mountExample(cfg) {
    const el = document.getElementById(cfg.id);
    if (!el) return;

    el.innerHTML = `
      <div class="box-header">
        <div class="box-title">
          <span>${esc(cfg.title)}</span>
          ${cfg.badge ? `<span class="badge badge-bridge">${esc(cfg.badge)}</span>` : ""}
        </div>
        <button class="btn btn-primary btn-sm run-example-btn">Run Example</button>
      </div>
      <div class="box-body">
        ${cfg.note ? `<p style="margin-top:0;color:var(--text-muted);font-size:0.92rem;">${esc(cfg.note)}</p>` : ""}
        <div class="code-block"><pre><code>${esc(cfg.code)}</code></pre></div>
        <div class="terminal-out hidden" aria-live="polite"></div>
      </div>
    `;

    const btn = el.querySelector(".run-example-btn");
    const term = el.querySelector(".terminal-out");
    btn.addEventListener("click", () => {
      const res = runWithMode(cfg, cfg.code);
      term.classList.remove("hidden");
      const prompt = cfg.cmdPrompt || (cfg.mode && cfg.mode !== "bash" ? `$ validate --type ${cfg.mode}` : "$ bash example.sh");
      const outText = (res.stdout + (res.stderr ? `\n[stderr / warnings]\n${res.stderr}` : "")).trim() || "(no output)";
      term.textContent = `${prompt}\n${outText}\n\n[exit code: ${res.exitCode}]`;
    });
  }

  /* ------------------------------------------------------------------
   * Mount Interactive Code Practice Lab
   * ------------------------------------------------------------------ */
  function mountLab(cfg) {
    const el = document.getElementById(cfg.id);
    if (!el) return;

    const lessonState = getLessonState(cfg.lessonId);
    const saved = lessonState.labs[cfg.id] || {};
    let hintIndex = 0;

    el.innerHTML = `
      <div class="box-header">
        <div class="box-title">
          <span>Lab: ${esc(cfg.title)}</span>
          <span class="badge ${saved.passed ? "badge-available" : "badge-planned"} lab-status-badge">
            ${saved.passed ? "Passed" : "Not Checked"}
          </span>
        </div>
        <span style="font-size:0.82rem;color:var(--text-dim);font-family:var(--font-mono);">${esc(cfg.sandboxLabel || "Browser DevOps Sandbox")}</span>
      </div>
      <div class="box-body">
        <p style="margin-top:0;">${cfg.promptHtml}</p>
        <textarea class="editor-textarea" spellcheck="false" aria-label="Code editor for ${esc(cfg.title)}">${esc(saved.code ?? cfg.starterCode)}</textarea>
        <div class="lab-actions">
          <button class="btn btn-primary btn-sm act-run">Run</button>
          <button class="btn btn-success btn-sm act-check">Check Solution</button>
          <button class="btn btn-sm act-hint">Hint (<span class="hint-count">0</span>/${cfg.hints.length})</button>
          <button class="btn btn-sm act-answer">Show Final Answer</button>
          <button class="btn btn-ghost btn-sm act-reset">Reset</button>
        </div>
        <div class="terminal-out hidden"></div>
        <div class="check-results hidden"></div>
        <div class="hint-panel"></div>
        <div class="answer-panel">
          <div style="font-weight:700;margin-bottom:0.45rem;">Reference Solution & Explanation</div>
          <div class="code-block"><pre><code>${esc(cfg.solutionCode)}</code></pre></div>
          <p style="margin:0.65rem 0 0;font-size:0.9rem;">${cfg.solutionExplanationHtml}</p>
        </div>
      </div>
    `;

    const textarea = el.querySelector(".editor-textarea");
    const term = el.querySelector(".terminal-out");
    const checksDiv = el.querySelector(".check-results");
    const hintPanel = el.querySelector(".hint-panel");
    const answerPanel = el.querySelector(".answer-panel");
    const hintCountEl = el.querySelector(".hint-count");
    const statusBadge = el.querySelector(".lab-status-badge");

    function executeUserCode() {
      const res = runWithMode(cfg, textarea.value);
      term.classList.remove("hidden");
      const prompt = cfg.cmdPrompt || (cfg.mode && cfg.mode !== "bash" ? `$ check --type ${cfg.mode}` : "$ bash lab_solution.sh");
      const combined = (res.stdout + (res.stderr ? `\n[stderr / warnings]\n${res.stderr}` : "")).trim() || "(no output)";
      term.textContent = `${prompt}\n${combined}\n\n[exit code: ${res.exitCode}]`;
      return res;
    }

    el.querySelector(".act-run").addEventListener("click", () => {
      executeUserCode();
      updateLessonState(cfg.lessonId, (ls) => {
        ls.labs[cfg.id] = { ...(ls.labs[cfg.id] || {}), code: textarea.value };
      });
    });

    el.querySelector(".act-check").addEventListener("click", () => {
      const res = executeUserCode();
      const results = cfg.checks.map((chk) => {
        let ok = false;
        try {
          ok = Boolean(chk.test(textarea.value, res));
        } catch (_) {
          ok = false;
        }
        return { label: chk.label, ok };
      });

      const allPassed = results.every((r) => r.ok);
      checksDiv.classList.remove("hidden");
      checksDiv.innerHTML = results
        .map(
          (r) =>
            `<div class="check-item ${r.ok ? "pass" : "fail"}">
              <span>${r.ok ? "PASS" : "FAIL"}</span>
              <span>${esc(r.label)}</span>
            </div>`
        )
        .join("");

      statusBadge.textContent = allPassed ? "Passed" : "Needs Work";
      statusBadge.className = `badge ${allPassed ? "badge-available" : "badge-security"} lab-status-badge`;

      updateLessonState(cfg.lessonId, (ls) => {
        ls.labs[cfg.id] = { code: textarea.value, passed: allPassed };
      });
      if (typeof cfg.onProgressChange === "function") cfg.onProgressChange();
    });

    el.querySelector(".act-hint").addEventListener("click", () => {
      if (hintIndex < cfg.hints.length) hintIndex++;
      hintCountEl.textContent = String(hintIndex);
      hintPanel.classList.add("is-open");
      hintPanel.innerHTML = cfg.hints
        .slice(0, hintIndex)
        .map((h, idx) => `<div style="margin-bottom:0.35rem;"><strong>Hint ${idx + 1}:</strong> ${h}</div>`)
        .join("");
    });

    el.querySelector(".act-answer").addEventListener("click", (e) => {
      const open = answerPanel.classList.toggle("is-open");
      e.target.textContent = open ? "Hide Final Answer" : "Show Final Answer";
    });

    el.querySelector(".act-reset").addEventListener("click", () => {
      textarea.value = cfg.starterCode;
      term.classList.add("hidden");
      checksDiv.classList.add("hidden");
      hintPanel.classList.remove("is-open");
      answerPanel.classList.remove("is-open");
      el.querySelector(".act-answer").textContent = "Show Final Answer";
      hintIndex = 0;
      hintCountEl.textContent = "0";
      statusBadge.textContent = "Not Checked";
      statusBadge.className = "badge badge-planned lab-status-badge";
      updateLessonState(cfg.lessonId, (ls) => {
        delete ls.labs[cfg.id];
      });
      if (typeof cfg.onProgressChange === "function") cfg.onProgressChange();
    });
  }

  /* ------------------------------------------------------------------
   * Mount Multiple-Choice Quiz with Per-Option Explanations
   * ------------------------------------------------------------------ */
  function mountQuiz(cfg) {
    const el = document.getElementById(cfg.id);
    if (!el) return;

    const lessonState = getLessonState(cfg.lessonId);
    const saved = lessonState.quizzes[cfg.id];

    el.innerHTML = `
      <div class="box-header">
        <div class="box-title">
          <span>Quiz ${cfg.number}: ${esc(cfg.title)}</span>
          <span class="badge badge-planned quiz-status">Unanswered</span>
        </div>
      </div>
      <div class="box-body">
        <div class="quiz-question">${cfg.questionHtml}</div>
        ${cfg.code ? `<div class="code-block" style="margin-bottom:0.85rem;"><pre><code>${esc(cfg.code)}</code></pre></div>` : ""}
        <div class="quiz-options">
          ${cfg.options
            .map(
              (opt, idx) => `
              <button class="quiz-opt" data-idx="${idx}">
                <span class="quiz-opt-key">${String.fromCharCode(65 + idx)}.</span>
                <span>${opt.labelHtml}</span>
              </button>`
            )
            .join("")}
        </div>
        <div class="quiz-feedback"></div>
      </div>
    `;

    const optBtns = Array.from(el.querySelectorAll(".quiz-opt"));
    const feedbackEl = el.querySelector(".quiz-feedback");
    const statusEl = el.querySelector(".quiz-status");

    function renderSelection(selectedIdx) {
      const isRight = selectedIdx === cfg.correctIndex;
      optBtns.forEach((btn, idx) => {
        btn.classList.remove("is-selected", "is-correct", "is-wrong");
        if (idx === cfg.correctIndex) btn.classList.add("is-correct");
        else if (idx === selectedIdx) btn.classList.add("is-wrong");
      });

      statusEl.textContent = isRight ? "Correct" : "Incorrect";
      statusEl.className = `badge ${isRight ? "badge-available" : "badge-project"} quiz-status`;

      feedbackEl.classList.add("is-open");
      feedbackEl.innerHTML = `
        <div style="font-weight:700;color:${isRight ? "var(--green)" : "var(--red)"};">
          ${isRight ? "Correct!" : `Not quite — Option ${String.fromCharCode(65 + cfg.correctIndex)} is correct.`}
        </div>
        <ul class="why-list">
          ${cfg.options
            .map((opt, idx) => {
              const correct = idx === cfg.correctIndex;
              const letter = String.fromCharCode(65 + idx);
              return `<li class="why-item ${correct ? "correct" : "wrong"}">
                <strong>${letter} (${correct ? "Why this is correct" : "Why this is wrong"}):</strong> ${opt.whyHtml}
              </li>`;
            })
            .join("")}
        </ul>
      `;
    }

    optBtns.forEach((btn) => {
      btn.addEventListener("click", () => {
        const idx = parseInt(btn.getAttribute("data-idx"), 10);
        renderSelection(idx);
        updateLessonState(cfg.lessonId, (ls) => {
          ls.quizzes[cfg.id] = { selected: idx, correct: idx === cfg.correctIndex };
        });
        if (typeof cfg.onProgressChange === "function") cfg.onProgressChange();
      });
    });

    if (saved && typeof saved.selected === "number") {
      renderSelection(saved.selected);
    }
  }

  /* ------------------------------------------------------------------
   * Export Public API
   * ------------------------------------------------------------------ */
  window.DevOpsLab = {
    initTheme,
    loadState,
    getLessonState,
    updateLessonState,
    mountExample,
    mountLab,
    mountQuiz,
    createSandbox,
    simulateConfig,
  };

  document.addEventListener("DOMContentLoaded", initTheme);
})();
