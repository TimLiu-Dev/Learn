"""
Surgical batch repair & 2027 unification across existing lessons 0001 to 0116:
1. Re-renders 0067 as deterministic 4-stage CodeSignal ICA In-Memory KV Store (fixing wall-clock/concurrency bugs & missing 'scan' stepper preset).
2. Replaces expectedCount-only test cases in 0048, 0049, 0060, 0078, 0079, 0080 with exact expected outputs + unordered / unorderedInner flags, and sets unorderedInner on 0023 Group Anagrams.
3. Fixes all broken OOP & classless starterStubs + adds ListNode/TreeNode/Node header comments.
4. Repairs all 24 blank-on-load Visual Steppers (0037..0091) with explicit initial buildSteps(...) arguments.
5. Rebuilds Section 2.5 pseudocode with clean indentation & zero mangled tokens, fixes tautological variable descriptions, and removes leaked spoken quotes.
6. Normalizes all 464 quiz questions (strict equal word count across A-D + balanced answer letter rotation + explicit problemId).
7. Escapes all unescaped <Generic> / raw '<' in HTML & <pre>, converts leaked markdown `code` / **bold**, strips ""..."" doubled quotes, links Sister Problems to lesson HTML files, upgrades generic Primary Sources, updates 96/103/110/116 counts to 126, reclassifies 0068 to Multi-Tier OOP, and retitles Section 6 to Mid-Level (L4).
"""

import os
import sys
import glob
import re
import json
import html

sys.path.append("scripts")
from lesson_builder import (
    CATEGORY_PRIMARY_SOURCES,
    NODE_HEADERS,
    SAFE_INLINE_TAGS,
    sanitize_html_fragment,
    derive_clean_stubs_from_solutions,
    to_clean_pseudocode,
    describe_state_metric,
    normalize_quiz_questions,
)
from template_engine_v2 import LC_TO_LESSON_FILE, render_lesson_v2


# =========================================================================
# 1. DETERMINISTIC 4-STAGE CODESIGNAL ICA DEFINITION FOR #0067
# =========================================================================
P67_ICA_V2 = {
    "num": 67,
    "lc": "ICA",
    "title": "In-Memory Key-Value Store with TTL (4-Stage ICA)",
    "category": "Multi-Tier OOP & Timed State Systems",
    "difficulty": "Medium",
    "pass_label": "Pass 2B",
    "lc_slug": "in-memory-key-value-store-ttl",
    "prev_file": "0066-lc981-time-based-key-value-store.html",
    "next_file": "0068-lc380-insert-delete-getrandom-o1.html",
    "method_name": "set",
    "verbatim_statement": (
        "Design a deterministic 4-Stage CodeSignal ICA In-Memory Key-Value Store supporting CRUD, lexicographical prefix scans, "
        "deterministic integer-timestamp TTL expiration, and nested transaction rollbacks.<br><br>"
        "Implement <code>InMemoryKVStore</code> across 4 progressive levels:<br>"
        "&bull; <strong>Level 1 (Core CRUD):</strong> <code>set(key, value)</code>, <code>get(key)</code> (returns value or <code>null</code>), <code>delete(key)</code> (returns <code>true</code> if live key existed).<br>"
        "&bull; <strong>Level 2 (Sorted Prefix Scan):</strong> <code>scan(prefix)</code> returns all live entries starting with <code>prefix</code> sorted lexicographically by key as <code>\"key(value)\"</code> strings.<br>"
        "&bull; <strong>Level 3 (Deterministic TTL):</strong> <code>setAtWithTtl(key, value, timestamp, ttl)</code> stores a value live on <code>[timestamp, timestamp + ttl)</code> (expired when <code>now &gt;= timestamp + ttl</code>); plus <code>getAt(key, timestamp)</code>, <code>deleteAt(key, timestamp)</code>, and <code>scanAt(prefix, timestamp)</code>.<br>"
        "&bull; <strong>Level 4 (Nested Transactions):</strong> <code>beginTransaction()</code>, <code>commit()</code>, and <code>rollback()</code> (restores exact pre-transaction values and TTLs in <code>O(T)</code> delta time; returns <code>false</code> if no transaction is active)."
    ),
    "examples": [
        {"title": "Stage 1–2 (CRUD & Scan)", "input": 'set("u:2","Bob"); set("u:1","Ada"); scan("u:")', "output": '["u:1(Ada)", "u:2(Bob)"]'},
        {"title": "Stage 3 (Deterministic TTL)", "input": 'setAtWithTtl("tok","xyz", 10, 5); getAt("tok", 14); getAt("tok", 15)', "output": '"xyz", then null (expired at t=15)'},
        {"title": "Stage 4 (Nested Rollback)", "input": 'set("bal","100"); beginTransaction(); set("bal","200"); rollback(); get("bal")', "output": '"100"'}
    ],
    "constraints": [
        "1 <= operations <= 10^4 across all 4 progressive ICA stages.",
        "Timestamps and TTLs are positive integers in [1, 10^9]; key is live strictly while timestamp < expiresAt.",
        "Transactions can nest arbitrarily deep; commit() merges undo deltas into parent transaction if nested."
    ],
    "plain_english": (
        "Model each stored record as an immutable Entry(value, expiresAt) so Level 1 timeless writes simply use expiresAt = INF. "
        "Instead of cloning the whole database on beginTransaction(), push an empty UndoMap onto a stack and record the prior Entry "
        "only on the FIRST mutation to each key inside that transaction."
    ),
    "boundary_checks": {
        "min_base": "commit() or rollback() with empty transaction stack -> cleanly returns false without mutating store.",
        "best_exit": "Exact boundary timestamp == start + ttl -> key is already expired (half-open interval [start, start + ttl)), returns null.",
        "worst_scan": "Nested transactions: begin -> write k=v2 -> begin -> write k=v3 -> commit -> rollback -> restores k back to original v1!"
    },
    "mental_model": {
        "analogy_title": "Tracing-Paper Undo Overlays & Stamped Freshness Tickets",
        "analogy_text": (
            "Picture a ledger book where every row holds a value plus an integer expiration tick (or Infinity). "
            "In CodeSignal ICA assessments, how you design Level 1 decides whether Level 3 and Level 4 break your code: "
            "wrapping values in Entry(value, expiresAt) from the start makes Level 3 TTL a 2-line helper! For Level 4 transactions, "
            "place a sheet of tracing paper (an empty UndoMap) on top of the stack. The first time you touch key K on that sheet, "
            "jot down K's previous Entry. Rollback tears off the top sheet and restores those jotted entries in O(T) time; "
            "nested commit merges the top sheet's earliest before-images down into the parent sheet via putIfAbsent."
        ),
        "aha_moment": "Store Entry(value, expiresAt) from Level 1 so timeless CRUD is just expiresAt = Long.MAX_VALUE, and use a stack of before-image UndoMaps (recording only the FIRST touch per key per transaction) for O(T) rollback.",
        "trigger": "CodeSignal 90-minute 4-Level Progressive ICA: CRUD -> Prefix Filter/Sort -> Timestamp TTL -> Rollback/Snapshots.",
        "sisters": [
            {"name": "Time Based Key-Value Store", "lc": "#981"},
            {"name": "Simple Bank System", "lc": "#2043"},
            {"name": "Cloud Storage System (4-Stage ICA)", "lc": "ICA", "file": "0126-custom-cloud-storage-ica.html"}
        ]
    },
    "naive_vs_optimal": {
        "naive_complexity": "O(N) Space & Time per beginTransaction()",
        "naive_text": "Store raw strings in Level 1 (forcing a total rewrite at Level 3) and deep-clone the entire HashMap on every beginTransaction().",
        "wasted_work": "Deep-cloning N keys across D nested transactions allocates O(D * N) memory and times out on Level 4 hidden suites.",
        "optimal_complexity": "O(1) CRUD/Begin, O(N log N) or O(log N + K) Scan, O(T) Rollback",
        "optimal_text": "Unified Entry(value, expiresAt) map + helper isLive(entry, ts) + Deque of UndoMaps capturing first-write before-images.",
        "core_invariant": "Each transaction frame maps every mutated key k to the exact Entry (or null) that existed immediately before that transaction began.",
        "table_rows": [
            {"name": "Naive: Full Map Clone per Txn", "time": "Begin: O(N), CRUD: O(1)", "space": "O(D · N)", "note": "TLE/MLE when 10^4 keys meet nested transactions."},
            {"name": "Better: Command Log Replay", "time": "Rollback: O(ops)", "space": "O(ops)", "note": "Harder to invert deletes and TTL overwrites cleanly."},
            {"name": "Optimal: Entry Wrapper + Before-Image Undo Stack", "time": "Begin/CRUD: O(1), Rollback: O(T)", "space": "O(N + T)", "note": "★ Canonical CodeSignal ICA Level 1–4 architecture."}
        ]
    },
    "trace_table": {
        "input_desc": 'setAtWithTtl("a","1", 10, 5) -> beginTransaction() -> setAtWithTtl("a","2", 12, 20) -> rollback() -> getAt("a", 14) -> getAt("a", 15)',
        "columns": ["Step", "Call", "Store State", "Top Undo Frame", "Output / Note"],
        "rows": [
            ["1", 'setAtWithTtl("a","1",10,5)', '{a: ("1", exp=15)}', "none", 'Stored "1" valid on [10, 15)'],
            ["2", "beginTransaction()", '{a: ("1", exp=15)}', "{}", "Pushed empty delta frame"],
            ["3", 'setAtWithTtl("a","2",12,20)', '{a: ("2", exp=32)}', '{a: ("1", exp=15)}', "Saved prior Entry before overwrite"],
            ["4", "rollback()", '{a: ("1", exp=15)}', "none", "true — restored original value AND exp=15!"],
            ["5", 'getAt("a", 14)', '{a: ("1", exp=15)}', "none", '"1" (14 < 15 -> still live)'],
            ["6", 'getAt("a", 15)', '{a: ("1", exp=15)}', "none", "null (15 >= 15 -> expired!)"]
        ],
        "takeaway": "Wrapping (value, expiresAt) in one Entry means rollback automatically restores both the prior value and its original TTL!"
    },
    "stepper": {
        "title": "4-Stage ICA Engine: CRUD, Prefix Scan, TTL & Undo Stack",
        "default_input": "txn",
        "presets": [
            {"label": "Stage 4: Nested Rollback", "onclick": 'loadPreset("txn")'},
            {"label": "Stage 3: Deterministic TTL", "onclick": 'loadPreset("ttl")'},
            {"label": "Stage 2: Sorted Prefix Scan", "onclick": 'loadPreset("scan")'}
        ],
        "metrics": [
            {"id": "metric-op", "label": "Operation", "color": "text-amber-400", "initial": "init"},
            {"id": "metric-tx-depth", "label": "Tx Stack Depth", "color": "text-cyan-400", "initial": "0"},
            {"id": "metric-active-keys", "label": "Live Keys", "color": "text-purple-400", "initial": "0"},
            {"id": "metric-status", "label": "Return / Status", "color": "text-emerald-400", "initial": "Ready"}
        ],
        "visual_html": '<div id="p67-stage" class="w-full flex flex-col items-center justify-center p-4 min-h-[160px] overflow-x-auto gap-4"></div>',
        "initial_narration": "Select any ICA stage preset or step forward to inspect store and undo-stack transitions.",
        "pseudocode_lines": [
            "data class Entry(val value: String, val expiresAt: Long)",
            "fun isLive(e: Entry?, ts: Long) = e != null && ts < e.expiresAt",
            "fun recordUndo(key: String) { if (tx.isNotEmpty() && key !in tx.last()) tx.last()[key] = store[key] }",
            "fun setAtWithTtl(k, v, ts, ttl) { recordUndo(k); store[k] = Entry(v, ts + ttl) }",
            "fun getAt(k, ts) = store[k]?.takeIf { ts < it.expiresAt }?.value",
            "fun scanAt(prefix, ts) = store.keys.sorted().filter { it.startsWith(prefix) && isLive(store[it], ts) }",
            "fun rollback(): pop top undo map and restore each (k -> oldEntry or remove)"
        ],
        "js_code": """
    let currentScenario = "txn";
    function buildSteps(scenario) {
      if (scenario) currentScenario = String(scenario).trim();
      steps = [];
      if (currentScenario === "ttl") {
        steps.push({ op: "init", depth: 0, status: "Ready", store: {}, undo: [], line: 1, narration: "Stage 3 TTL: Entries hold deterministic integer expiresAt = timestamp + ttl." });
        steps.push({ op: 'setAtWithTtl("tok","abc",10,5)', depth: 0, status: "void", store: { tok: '"abc" [exp=15]' }, undo: [], line: 4, narration: 'Stored "tok" at t=10 with ttl=5 -> live on [10, 15).' });
        steps.push({ op: 'getAt("tok", 14)', depth: 0, status: '"abc"', store: { tok: '"abc" [exp=15]' }, undo: [], line: 5, narration: 'At t=14: 14 < 15 is true -> returns "abc".' });
        steps.push({ op: 'getAt("tok", 15)', depth: 0, status: "null", store: {}, undo: [], line: 5, narration: 'At t=15: 15 < 15 is false -> expired! Returns null.' });
      } else if (currentScenario === "scan") {
        steps.push({ op: "init", depth: 0, status: "Ready", store: {}, undo: [], line: 1, narration: "Stage 2–3 Prefix Scan: filters live keys by prefix in lexicographical order." });
        steps.push({ op: 'setAtWithTtl("u:2","Bob",1,50)', depth: 0, status: "void", store: { "u:2": '"Bob" [exp=51]' }, undo: [], line: 4, narration: 'Inserted u:2 -> Bob.' });
        steps.push({ op: 'setAtWithTtl("u:1","Ada",2,10)', depth: 0, status: "void", store: { "u:1": '"Ada" [exp=12]', "u:2": '"Bob" [exp=51]' }, undo: [], line: 4, narration: 'Inserted u:1 -> Ada (expires at t=12).' });
        steps.push({ op: 'scanAt("u:", 5)', depth: 0, status: '["u:1(Ada)","u:2(Bob)"]', store: { "u:1": '"Ada" [exp=12]', "u:2": '"Bob" [exp=51]' }, undo: [], line: 6, narration: 'At t=5 both keys are live; sorted keys yield ["u:1(Ada)", "u:2(Bob)"].' });
        steps.push({ op: 'scanAt("u:", 15)', depth: 0, status: '["u:2(Bob)"]', store: { "u:2": '"Bob" [exp=51]' }, undo: [], line: 6, narration: 'At t=15 u:1 expired (15 >= 12), so scanAt returns ["u:2(Bob)"].' });
      } else {
        steps.push({ op: "init", depth: 0, status: "Ready", store: {}, undo: [], line: 1, narration: "Stage 4 Transactions: Undo stack records first-touch before-images." });
        steps.push({ op: 'set("bal","100")', depth: 0, status: "void", store: { bal: '"100" [INF]' }, undo: [], line: 4, narration: 'Root write: bal = "100".' });
        steps.push({ op: "beginTransaction()", depth: 1, status: "tx #1", store: { bal: '"100" [INF]' }, undo: [{ title: "Tx #1", deltas: {} }], line: 3, narration: "Pushed empty UndoMap for Tx #1." });
        steps.push({ op: 'set("bal","200")', depth: 1, status: "void", store: { bal: '"200" [INF]' }, undo: [{ title: "Tx #1", deltas: { bal: '"100" [INF]' } }], line: 3, narration: 'First write to bal in Tx #1 -> saved prior Entry ("100") into UndoMap.' });
        steps.push({ op: 'set("tmp","9")', depth: 1, status: "void", store: { bal: '"200" [INF]', tmp: '"9" [INF]' }, undo: [{ title: "Tx #1", deltas: { bal: '"100" [INF]', tmp: "null" } }], line: 4, narration: 'New key tmp recorded with prior state null.' });
        steps.push({ op: "rollback()", depth: 0, status: "true", store: { bal: '"100" [INF]' }, undo: [], line: 7, narration: 'Rollback popped Tx #1: restored bal="100" and deleted tmp in O(T) time!' });
      }
    }
    function renderStage(s) {
      const stage = document.getElementById('p67-stage');
      if (!stage) return;
      const keys = Object.keys(s.store);
      const storeHtml = keys.length === 0
        ? '<div class="text-xs font-mono text-slate-500 italic">Store is empty</div>'
        : keys.map(k => `<div class="p-1.5 rounded bg-slate-950 border border-slate-700 font-mono text-xs flex justify-between"><span class="text-amber-300 font-bold">${k}</span><span class="text-slate-200">${s.store[k]}</span></div>`).join('');
      const undoHtml = s.undo.length === 0
        ? '<div class="text-xs font-mono text-slate-500 italic">No active transactions</div>'
        : s.undo.map(u => `<div class="p-2 rounded bg-slate-950 border border-cyan-800/60 font-mono text-xs space-y-1"><span class="text-cyan-300 font-bold">${u.title}</span>` +
            Object.keys(u.deltas).map(dk => `<div class="text-[11px] text-slate-400 flex justify-between"><span>${dk} &larr;</span><span class="text-amber-400">${u.deltas[dk]}</span></div>`).join('') +
          `</div>`).join('');
      stage.innerHTML = `<div class="grid grid-cols-1 md:grid-cols-2 gap-4 w-full max-w-3xl">
        <div class="p-3 rounded-lg bg-slate-900 border border-slate-800 space-y-2"><span class="text-xs font-mono text-emerald-400 font-bold block">Live Store (Entry):</span>${storeHtml}</div>
        <div class="p-3 rounded-lg bg-slate-900 border border-slate-800 space-y-2"><span class="text-xs font-mono text-cyan-400 font-bold block">Undo Delta Stack:</span>${undoHtml}</div>
      </div>`;
    }
    function loadPreset(sc) { buildSteps(sc); stepperReset(); }
        """,
        "js_init_call": 'buildSteps("txn");',
        "js_update_metrics": """
        document.getElementById('metric-op').textContent = step.op;
        document.getElementById('metric-tx-depth').textContent = step.depth;
        document.getElementById('metric-active-keys').textContent = Object.keys(step.store).length;
        document.getElementById('metric-status').textContent = step.status;
        """
    },
    "approaches": [
        {
            "name": "Naive: Full HashMap Snapshot per Transaction",
            "badge": "Naive",
            "desc": "Clones the entire store HashMap on every beginTransaction(). Simple but O(N) time & space per transaction.",
            "time": "O(N) begin / O(1) rollback",
            "space": "O(D · N)",
            "code_kotlin": """import java.util.ArrayDeque

class InMemoryKVStore {
    private data class Entry(val value: String, val expiresAt: Long)
    private var store = HashMap<String, Entry>()
    private val snapshots = ArrayDeque<HashMap<String, Entry>>()
    private val INF = Long.MAX_VALUE

    fun set(key: String, value: String) = setAtWithTtl(key, value, 0L, INF)
    fun get(key: String): String? = getAt(key, 0L)
    fun delete(key: String): Boolean = deleteAt(key, 0L)
    fun scan(prefix: String): List<String> = scanAt(prefix, 0L)

    fun setAtWithTtl(key: String, value: String, timestamp: Long, ttl: Long) {
        val exp = if (ttl == INF) INF else timestamp + ttl
        store[key] = Entry(value, exp)
    }

    fun getAt(key: String, timestamp: Long): String? {
        val e = store[key] ?: return null
        return if (timestamp < e.expiresAt) e.value else null
    }

    fun deleteAt(key: String, timestamp: Long): Boolean {
        val e = store[key] ?: return false
        store.remove(key)
        return timestamp < e.expiresAt
    }

    fun scanAt(prefix: String, timestamp: Long): List<String> {
        val res = mutableListOf<String>()
        for (k in store.keys.toList().sorted()) {
            val e = store[k]
            if (k.startsWith(prefix) && e != null && timestamp < e.expiresAt) {
                res.add("$k(${e.value})")
            }
        }
        return res
    }

    fun beginTransaction() { snapshots.addLast(HashMap(store)) }
    fun commit(): Boolean {
        if (snapshots.isEmpty()) return false
        snapshots.removeLast()
        return true
    }
    fun rollback(): Boolean {
        if (snapshots.isEmpty()) return false
        store = snapshots.removeLast()
        return true
    }
}""",
            "code_python": """class InMemoryKVStore:
    def __init__(self):
        self.store = {}
        self.snapshots = []

    def set(self, key: str, value: str) -> None:
        self.setAtWithTtl(key, value, 0, float('inf'))

    def get(self, key: str):
        return self.getAt(key, 0)

    def delete(self, key: str) -> bool:
        return self.deleteAt(key, 0)

    def scan(self, prefix: str) -> list[str]:
        return self.scanAt(prefix, 0)

    def setAtWithTtl(self, key: str, value: str, timestamp: int, ttl: float) -> None:
        exp = float('inf') if ttl == float('inf') else timestamp + ttl
        self.store[key] = [value, exp]

    def getAt(self, key: str, timestamp: int):
        entry = self.store.get(key)
        if entry is None or timestamp >= entry[1]:
            return None
        return entry[0]

    def deleteAt(self, key: str, timestamp: int) -> bool:
        entry = self.store.pop(key, None)
        return entry is not None and timestamp < entry[1]

    def scanAt(self, prefix: str, timestamp: int) -> list[str]:
        out = []
        for k in sorted(self.store.keys()):
            entry = self.store[k]
            if k.startswith(prefix) and timestamp < entry[1]:
                out.append(f"{k}({entry[0]})")
        return out

    def beginTransaction(self) -> None:
        self.snapshots.append(dict(self.store))

    def commit(self) -> bool:
        if not self.snapshots:
            return False
        self.snapshots.pop()
        return True

    def rollback(self) -> bool:
        if not self.snapshots:
            return False
        self.store = self.snapshots.pop()
        return True""",
            "code_java": """import java.util.*;

class InMemoryKVStore {
    private static class Entry {
        final String value;
        final long expiresAt;
        Entry(String value, long expiresAt) { this.value = value; this.expiresAt = expiresAt; }
    }

    private Map<String, Entry> store = new HashMap<>();
    private final Deque<Map<String, Entry>> snapshots = new ArrayDeque<>();

    public void set(String key, String value) { setAtWithTtl(key, value, 0L, Long.MAX_VALUE); }
    public String get(String key) { return getAt(key, 0L); }
    public boolean delete(String key) { return deleteAt(key, 0L); }
    public List<String> scan(String prefix) { return scanAt(prefix, 0L); }

    public void setAtWithTtl(String key, String value, long timestamp, long ttl) {
        long exp = (ttl == Long.MAX_VALUE) ? Long.MAX_VALUE : timestamp + ttl;
        store.put(key, new Entry(value, exp));
    }

    public String getAt(String key, long timestamp) {
        Entry e = store.get(key);
        return (e != null && timestamp < e.expiresAt) ? e.value : null;
    }

    public boolean deleteAt(String key, long timestamp) {
        Entry e = store.remove(key);
        return e != null && timestamp < e.expiresAt;
    }

    public List<String> scanAt(String prefix, long timestamp) {
        List<String> keys = new ArrayList<>(store.keySet());
        Collections.sort(keys);
        List<String> res = new ArrayList<>();
        for (String k : keys) {
            Entry e = store.get(k);
            if (k.startsWith(prefix) && e != null && timestamp < e.expiresAt) {
                res.add(k + "(" + e.value + ")");
            }
        }
        return res;
    }

    public void beginTransaction() { snapshots.addLast(new HashMap<>(store)); }
    public boolean commit() {
        if (snapshots.isEmpty()) return false;
        snapshots.removeLast();
        return true;
    }
    public boolean rollback() {
        if (snapshots.isEmpty()) return false;
        store = snapshots.removeLast();
        return true;
    }
}"""
        },
        {
            "name": "Optimal: Unified Entry + Before-Image Delta Undo Stack",
            "badge": "Optimal",
            "desc": "Records only the first before-image per mutated key per transaction for O(1) begin and O(T) rollback.",
            "time": "O(1) CRUD / O(T) rollback",
            "space": "O(N + T)",
            "code_kotlin": """import java.util.ArrayDeque

class InMemoryKVStore {
    private data class Entry(val value: String, val expiresAt: Long)

    private val store = HashMap<String, Entry>()
    private val txStack = ArrayDeque<HashMap<String, Entry?>>()
    private val INF = Long.MAX_VALUE

    fun set(key: String, value: String) {
        setAtWithTtl(key, value, 0L, INF)
    }

    fun get(key: String): String? {
        return getAt(key, 0L)
    }

    fun delete(key: String): Boolean {
        return deleteAt(key, 0L)
    }

    fun scan(prefix: String): List<String> {
        return scanAt(prefix, 0L)
    }

    fun setAtWithTtl(key: String, value: String, timestamp: Long, ttl: Long) {
        recordUndo(key)
        val exp = if (ttl == INF) INF else timestamp + ttl
        store[key] = Entry(value, exp)
    }

    fun getAt(key: String, timestamp: Long): String? {
        val e = store[key] ?: return null
        if (timestamp >= e.expiresAt) return null
        return e.value
    }

    fun deleteAt(key: String, timestamp: Long): Boolean {
        val e = store[key] ?: return false
        val wasLive = timestamp < e.expiresAt
        recordUndo(key)
        store.remove(key)
        return wasLive
    }

    fun scanAt(prefix: String, timestamp: Long): List<String> {
        val keys = store.keys.toList().sorted()
        val out = mutableListOf<String>()
        for (k in keys) {
            val e = store[k]
            if (k.startsWith(prefix) && e != null && timestamp < e.expiresAt) {
                out.add("$k(${e.value})")
            }
        }
        return out
    }

    fun beginTransaction() {
        txStack.addLast(HashMap())
    }

    fun commit(): Boolean {
        if (txStack.isEmpty()) return false
        val top = txStack.removeLast()
        if (txStack.isNotEmpty()) {
            val parent = txStack.last()
            for ((k, oldEntry) in top) {
                if (!parent.containsKey(k)) {
                    parent[k] = oldEntry
                }
            }
        }
        return true
    }

    fun rollback(): Boolean {
        if (txStack.isEmpty()) return false
        val deltas = txStack.removeLast()
        for ((k, oldEntry) in deltas) {
            if (oldEntry == null) {
                store.remove(k)
            } else {
                store[k] = oldEntry
            }
        }
        return true
    }

    private fun recordUndo(key: String) {
        if (txStack.isNotEmpty()) {
            val top = txStack.last()
            if (!top.containsKey(key)) {
                top[key] = store[key]
            }
        }
    }
}""",
            "code_python": """from collections import deque

class InMemoryKVStore:
    def __init__(self):
        self.store = {}
        self.tx_stack = deque()

    def _record_undo(self, key: str) -> None:
        if self.tx_stack:
            top = self.tx_stack[-1]
            if key not in top:
                top[key] = self.store.get(key)

    def set(self, key: str, value: str) -> None:
        self.setAtWithTtl(key, value, 0, float('inf'))

    def get(self, key: str):
        return self.getAt(key, 0)

    def delete(self, key: str) -> bool:
        return self.deleteAt(key, 0)

    def scan(self, prefix: str) -> list[str]:
        return self.scanAt(prefix, 0)

    def setAtWithTtl(self, key: str, value: str, timestamp: int, ttl: float) -> None:
        self._record_undo(key)
        exp = float('inf') if ttl == float('inf') else timestamp + ttl
        self.store[key] = [value, exp]

    def getAt(self, key: str, timestamp: int):
        entry = self.store.get(key)
        if entry is None or timestamp >= entry[1]:
            return None
        return entry[0]

    def deleteAt(self, key: str, timestamp: int) -> bool:
        entry = self.store.get(key)
        if entry is None:
            return False
        was_live = timestamp < entry[1]
        self._record_undo(key)
        del self.store[key]
        return was_live

    def scanAt(self, prefix: str, timestamp: int) -> list[str]:
        res = []
        for k in sorted(self.store.keys()):
            entry = self.store[k]
            if k.startswith(prefix) and timestamp < entry[1]:
                res.append(f"{k}({entry[0]})")
        return res

    def beginTransaction(self) -> None:
        self.tx_stack.append({})

    def commit(self) -> bool:
        if not self.tx_stack:
            return False
        top = self.tx_stack.pop()
        if self.tx_stack:
            parent = self.tx_stack[-1]
            for k, old_entry in top.items():
                if k not in parent:
                    parent[k] = old_entry
        return True

    def rollback(self) -> bool:
        if not self.tx_stack:
            return False
        deltas = self.tx_stack.pop()
        for k, old_entry in deltas.items():
            if old_entry is None:
                self.store.pop(k, None)
            else:
                self.store[k] = old_entry
        return True""",
            "code_java": """import java.util.*;

class InMemoryKVStore {
    private static class Entry {
        final String value;
        final long expiresAt;
        Entry(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, Entry> store = new HashMap<>();
    private final Deque<Map<String, Entry>> txStack = new ArrayDeque<>();

    public void set(String key, String value) {
        setAtWithTtl(key, value, 0L, Long.MAX_VALUE);
    }

    public String get(String key) {
        return getAt(key, 0L);
    }

    public boolean delete(String key) {
        return deleteAt(key, 0L);
    }

    public List<String> scan(String prefix) {
        return scanAt(prefix, 0L);
    }

    public void setAtWithTtl(String key, String value, long timestamp, long ttl) {
        recordUndo(key);
        long exp = (ttl == Long.MAX_VALUE) ? Long.MAX_VALUE : timestamp + ttl;
        store.put(key, new Entry(value, exp));
    }

    public String getAt(String key, long timestamp) {
        Entry e = store.get(key);
        if (e == null || timestamp >= e.expiresAt) return null;
        return e.value;
    }

    public boolean deleteAt(String key, long timestamp) {
        Entry e = store.get(key);
        if (e == null) return false;
        boolean wasLive = timestamp < e.expiresAt;
        recordUndo(key);
        store.remove(key);
        return wasLive;
    }

    public List<String> scanAt(String prefix, long timestamp) {
        List<String> keys = new ArrayList<>(store.keySet());
        Collections.sort(keys);
        List<String> out = new ArrayList<>();
        for (String k : keys) {
            Entry e = store.get(k);
            if (k.startsWith(prefix) && e != null && timestamp < e.expiresAt) {
                out.add(k + "(" + e.value + ")");
            }
        }
        return out;
    }

    public void beginTransaction() {
        txStack.addLast(new HashMap<>());
    }

    public boolean commit() {
        if (txStack.isEmpty()) return false;
        Map<String, Entry> top = txStack.removeLast();
        if (!txStack.isEmpty()) {
            Map<String, Entry> parent = txStack.peekLast();
            for (Map.Entry<String, Entry> e : top.entrySet()) {
                parent.putIfAbsent(e.getKey(), e.getValue());
            }
        }
        return true;
    }

    public boolean rollback() {
        if (txStack.isEmpty()) return false;
        Map<String, Entry> deltas = txStack.removeLast();
        for (Map.Entry<String, Entry> e : deltas.entrySet()) {
            if (e.getValue() == null) {
                store.remove(e.getKey());
            } else {
                store.put(e.getKey(), e.getValue());
            }
        }
        return true;
    }

    private void recordUndo(String key) {
        if (!txStack.isEmpty()) {
            Map<String, Entry> top = txStack.peekLast();
            if (!top.containsKey(key)) {
                top.put(key, store.get(key));
            }
        }
    }
}"""
        }
    ],
    "test_cases": [
        {
            "mode": "oop",
            "ops": ["InMemoryKVStore", "set", "set", "get", "delete", "get", "delete"],
            "args": [[], ["a", "10"], ["b", "20"], ["a"], ["a"], ["a"], ["a"]],
            "expected": [None, None, None, "10", True, None, False]
        },
        {
            "mode": "oop",
            "ops": ["InMemoryKVStore", "set", "set", "set", "scan", "scan"],
            "args": [[], ["user:2", "Bob"], ["user:1", "Ada"], ["item:9", "Pen"], ["user:"], ["z:"]],
            "expected": [None, None, None, None, ["user:1(Ada)", "user:2(Bob)"], []]
        },
        {
            "mode": "oop",
            "ops": ["InMemoryKVStore", "setAtWithTtl", "setAtWithTtl", "getAt", "getAt", "scanAt", "deleteAt"],
            "args": [[], ["u:1", "Ada", 10, 5], ["u:2", "Bob", 10, 20], ["u:1", 14], ["u:1", 15], ["u:", 16], ["u:1", 16]],
            "expected": [None, None, None, "Ada", None, ["u:2(Bob)"], False]
        },
        {
            "mode": "oop",
            "ops": ["InMemoryKVStore", "set", "beginTransaction", "set", "beginTransaction", "set", "commit", "get", "rollback", "get", "rollback"],
            "args": [[], ["bal", "100"], [], ["bal", "200"], [], ["bal", "300"], [], ["bal"], [], ["bal"], []],
            "expected": [None, None, None, None, None, None, True, "300", True, "100", False]
        }
    ],
    "spoken_script": {
        "phase1": "In a 4-stage ICA, I first align on data invariants: TTL intervals are half-open [timestamp, timestamp + ttl), scan outputs lexicographically sorted 'key(value)' strings, and commit/rollback return false when no transaction is active.",
        "phase2": "Even in Stage 1, I wrap values in Entry(value, expiresAt = Long.MAX_VALUE) rather than raw strings so Stage 3 TTL requires zero refactoring. For Stage 4 transactions, full snapshots cost O(N) per begin; a stack of UndoMaps recording only the first mutation per key takes O(1) per write and O(T) on rollback.",
        "phase3": "Every mutating method calls recordUndo(key) before touching store: if txStack is non-empty and top doesn't contain key yet, store top[key] = store[key]. On rollback(), pop top and restore old Entry or remove if null. On nested commit(), merge top into parent via putIfAbsent so parent still remembers the pre-parent state!",
        "phase4": "Tracing nested commit + outer rollback: bal='100' -> begin(#1) -> set('200') records #1[bal='100'] -> begin(#2) -> set('300') records #2[bal='200'] -> commit(#2) uses putIfAbsent so #1 keeps bal='100' -> rollback(#1) restores bal='100'!",
        "mid_followup": "If Stage 2 scan(prefix) becomes read-heavy over 10^5 keys, replace the HashMap key sort with a TreeMap<String, Entry> and iterate store.tailMap(prefix) stopping on the first key that no longer startsWith(prefix), achieving O(log N + K) prefix scans."
    },
    "quiz_questions": [
        {
            "category": "ICA Level 1 -> Level 3 Extensibility",
            "prompt": "Why should Level 1 store Entry(value, expiresAt) immediately instead of raw String values?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Timeless writes use expiresAt = MAX_VALUE so Level 3 TTL adds zero schema rewrites.", "isCorrect": True, "whyRight": "Designing the record wrapper upfront lets Level 1 delegate cleanly to setAtWithTtl(k, v, 0, INF)."},
                {"id": "B", "text": "JVM HashMaps reject raw String values whenever key lengths exceed sixteen characters.", "isCorrect": False, "whyWrong": "HashMap supports arbitrary String keys and values."},
                {"id": "C", "text": "Wrapping strings automatically sorts HashMap keys in lexicographical prefix order.", "isCorrect": False, "whyWrong": "HashMap bucket order is hash-based regardless of value wrapper type."},
                {"id": "D", "text": "Entry wrappers reduce worst-case rollback complexity from O(T) down to O(1).", "isCorrect": False, "whyWrong": "Rollback still iterates over the T keys mutated in that transaction."}
            ]
        },
        {
            "category": "Code Snippet Bug Hunt (Nested Commit)",
            "prompt": "What bug breaks nested transactions in this `commit()` implementation?",
            "codeSnippet": "fun commit(): Boolean {\n    if (txStack.isEmpty()) return false\n    val top = txStack.removeLast()\n    if (txStack.isNotEmpty()) {\n        txStack.last().putAll(top) // <-- BUG HERE\n    }\n    return true\n}",
            "options": [
                {"id": "A", "text": "putAll overwrites parent's original before-image with the inner transaction's newer state.", "isCorrect": True, "whyRight": "If parent changed k from '100'->'200' and child changed '200'->'300', putAll overwrites parent's undo entry '100' with '200', breaking outer rollback!"},
                {"id": "B", "text": "removeLast pops from the bottom of ArrayDeque instead of the active transaction.", "isCorrect": False, "whyWrong": "addLast + removeLast operates strictly LIFO on the top frame."},
                {"id": "C", "text": "commit must clear the entire store whenever txStack still has parent frames.", "isCorrect": False, "whyWrong": "Committing an inner transaction keeps its mutations live in store."},
                {"id": "D", "text": "txStack.isEmpty throws ConcurrentModificationException inside single-threaded ICA runs.", "isCorrect": False, "whyWrong": "isEmpty() is a safe O(1) check."}
            ]
        },
        {
            "category": "TTL Boundary Semantics",
            "prompt": "If `setAtWithTtl('k', 'v', timestamp = 10, ttl = 5)` is called, what must `getAt('k', 15)` return?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "It returns null because half-open TTL [10, 15) expires at timestamp 15.", "isCorrect": True, "whyRight": "In CodeSignal ICA specs, ttl=5 starting at t=10 covers ticks 10, 11, 12, 13, 14 (5 ticks) and is expired at t=15 (timestamp >= expiresAt)."},
                {"id": "B", "text": "It returns 'v' because expiration only triggers strictly after timestamp + ttl.", "isCorrect": False, "whyWrong": "Using > instead of >= makes the key live for 6 ticks (10..15 inclusive) instead of 5."},
                {"id": "C", "text": "It throws IllegalStateException whenever an expired key is queried without deletion.", "isCorrect": False, "whyWrong": "Expired lookups simply return null."},
                {"id": "D", "text": "It resets the key's TTL window forward by another 5 ticks on read.", "isCorrect": False, "whyWrong": "Fixed TTL does not slide on read unless sliding-expiration is explicitly requested."}
            ]
        },
        {
            "category": "Prefix Scan Optimization",
            "prompt": "How do you optimize `scanAt(prefix, ts)` from O(N log N) key sorting to O(log N + K)?",
            "codeSnippet": None,
            "options": [
                {"id": "A", "text": "Maintain keys in a TreeMap and iterate tailMap(prefix) until startsWith fails.", "isCorrect": True, "whyRight": "TreeMap.tailMap(prefix) seeks to the first matching key in O(log N) and scans only the K matching keys in sorted order."},
                {"id": "B", "text": "Store keys inside an unsorted ArrayList and run binary search without sorting.", "isCorrect": False, "whyWrong": "Binary search fails on unsorted lists."},
                {"id": "C", "text": "Hash every prefix into a Bloom filter to enumerate keys in sorted order.", "isCorrect": False, "whyWrong": "Bloom filters cannot enumerate keys or preserve lexicographical order."},
                {"id": "D", "text": "Use a Min-Heap polling and discarding all keys on every scanAt call.", "isCorrect": False, "whyWrong": "Polling all keys takes O(N log N) per scan."}
            ]
        }
    ]
}


# =========================================================================
# 2. EXACT TEST SUITES FOR 0023, 0048, 0049, 0060, 0078, 0079, 0080
# =========================================================================
EXACT_TEST_OVERRIDES = {
    23: [
        {"input": [["eat", "tea", "tan", "ate", "nat", "bat"]], "expected": [["bat"], ["nat", "tan"], ["ate", "eat", "tea"]], "unorderedInner": True},
        {"input": [[""]], "expected": [[""]], "unorderedInner": True},
        {"input": [["a"]], "expected": [["a"]], "unorderedInner": True},
    ],
    48: [
        {"input": [[1, 2, 3]], "expected": [[], [1], [2], [3], [1, 2], [1, 3], [2, 3], [1, 2, 3]], "unorderedInner": True},
        {"input": [[0]], "expected": [[], [0]], "unorderedInner": True},
        {"input": [[4, 5]], "expected": [[], [4], [5], [4, 5]], "unorderedInner": True},
    ],
    49: [
        {"input": [[1, 2, 3]], "expected": [[1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1]], "unordered": True},
        {"input": [[0, 1]], "expected": [[0, 1], [1, 0]], "unordered": True},
        {"input": [[1]], "expected": [[1]], "unordered": True},
    ],
    60: [
        {"input": [[-1, 0, 1, 2, -1, -4]], "expected": [[-1, -1, 2], [-1, 0, 1]], "unorderedInner": True},
        {"input": [[0, 1, 1]], "expected": [], "unorderedInner": True},
        {"input": [[0, 0, 0]], "expected": [[0, 0, 0]], "unorderedInner": True},
    ],
    78: [
        {"input": [[2, 3, 6, 7], 7], "expected": [[2, 2, 3], [7]], "unorderedInner": True},
        {"input": [[2, 3, 5], 8], "expected": [[2, 2, 2, 2], [2, 3, 3], [3, 5]], "unorderedInner": True},
        {"input": [[2], 1], "expected": [], "unorderedInner": True},
    ],
    79: [
        {"input": [3], "expected": ["((()))", "(()())", "(())()", "()(())", "()()()"], "unordered": True},
        {"input": [1], "expected": ["()"], "unordered": True},
        {"input": [2], "expected": ["(())", "()()"], "unordered": True},
    ],
    80: [
        {
            "input": [[[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]]],
            "expected": [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]],
            "unordered": True
        },
        {"input": [[[1]]], "expected": [[0, 0]], "unordered": True},
        {"input": [[[2, 1], [1, 2]]], "expected": [[0, 0], [0, 1], [1, 0], [1, 1]], "unordered": True},
    ],
}

# Explicit stepper init calls for the 24 broken steppers (excluding 67 which is full-rendered above)
STEPPER_INIT_OVERRIDES = {
    37: "buildSteps(initialNums);",
    38: "buildSteps(initialNums);",
    39: "buildSteps(rawTree);",
    40: "buildSteps(viewTree);",
    41: "buildSteps(goodTree);",
    47: "buildSteps(baseIntervals, insInterval);",
    49: "buildSteps(permNums);",
    55: "buildSteps(rawHouses);",
    57: "buildSteps(coinSet, targetAmt);",
    61: "buildSteps(rawStr, rawK);",
    63: "buildSteps(rawStr);",
    64: "buildSteps(rawExpr);",
    65: "buildSteps(rawTarget, rawPos, rawSpd);",
    68: "buildSteps(currentMode);",
    71: "buildSteps(rawPiles, rawH);",
    74: "buildSteps(rawQuery);",
    82: "buildSteps(rawAdj);",
    83: "buildSteps(rawN, rawPrereqs);",
    84: "buildSteps(rawN, rawPrereqs);",
    86: "buildSteps(rawTimes, rawN, rawK);",
    87: "buildSteps(rawS, rawDict);",
    89: "buildSteps(rawT1, rawT2);",
    91: "buildSteps(rawS, rawT);",
}

# Stubs that need clean re-derivation from modelSolutions (broken OOP, classless 0082..0095, or node headers)
REBUILD_STUB_NUMS = {
    11, 12, 13, 14, 16, 17, 18,
    28, 31, 32, 33, 34, 35, 39, 40, 41, 42,
    66, 68, 69, 72, 73, 74,
    82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96,
    105, 106, 113, 114
}

ALL_KNOWN_HTML_TAGS = {
    "html","head","meta","title","script","link","style","body","header","nav","main","section",
    "div","span","p","a","h1","h2","h3","h4","h5","h6","ul","ol","li","strong","em","b","i","u",
    "code","pre","table","thead","tbody","tr","th","td","button","input","select","option",
    "textarea","canvas","svg","path","circle","rect","line","g","br","hr","footer","label","details","summary","sup","sub"
}


def extract_balanced_braces(s, open_idx):
    depth = 1
    i = open_idx + 1
    in_str = None
    esc = False
    while i < len(s) and depth > 0:
        ch = s[i]
        if esc:
            esc = False
        elif ch == '\\':
            esc = True
        elif in_str:
            if ch == in_str:
                in_str = None
        elif ch in ('"', "'", '`'):
            in_str = ch
        elif ch == '{':
            depth += 1
        elif ch == '}':
            depth -= 1
        i += 1
    return i - 1


def extract_balanced_brackets(s, open_idx):
    depth = 1
    i = open_idx + 1
    in_str = None
    esc = False
    while i < len(s) and depth > 0:
        ch = s[i]
        if esc:
            esc = False
        elif ch == '\\':
            esc = True
        elif in_str:
            if ch == in_str:
                in_str = None
        elif ch in ('"', "'", '`'):
            in_str = ch
        elif ch == '[':
            depth += 1
        elif ch == ']':
            depth -= 1
        i += 1
    return i - 1


def eval_js_obj_via_node(js_expr):
    import subprocess
    code = "const obj = (" + js_expr + "); process.stdout.write(JSON.stringify(obj));"
    r = subprocess.run(["node", "-e", code], capture_output=True, text=True)
    if r.returncode != 0:
        raise RuntimeError(f"Node parse failed: {r.stderr[:200]}")
    return json.loads(r.stdout)


def sanitize_html_outside_scripts(doc_html):
    r"""
    Safely cleans HTML outside <script> and <style>:
    1. Inside <pre>...</pre>: preserves legitimate <span ...> and <div ...> tags, escapes any raw '<' (e.g. `i < n`, `List<Int>`)!
    2. Outside <pre>: escapes unknown `<Generic>` tags (`<Pair<String, String>>`, `<Key, OldValue>`) and raw `<=` / `< ` comparisons,
       fixes raw `\mu\text{s}`, strips doubled `""...""` quotes, and converts markdown `code` / **bold** outside <code>/<pre>/<a>!
    """
    # Mask <script> and <style> blocks
    protected = []

    def mask_block(m):
        idx = len(protected)
        protected.append(m.group(0))
        return f"\x00BLK{idx}\x00"

    s = re.sub(r'<(script|style)\b[^>]*>[\s\S]*?</\1>', mask_block, doc_html, flags=re.I)
    s = re.sub(r'<!--[\s\S]*?-->', mask_block, s)

    # Fix raw LaTeX `\mu\text{s}`
    s = s.replace(r'\mu\text{s}', '&mu;s').replace(r'\mu', '&mu;')
    # Fix doubled quotes `""First...""` -> `"First..."`
    s = re.sub(r'>""([\s\S]*?)""<', r'>"\1"<', s)

    # Clean <pre> blocks first so `<` in code (like `i < n` or `HashSet<Integer>`) is properly escaped
    def clean_pre(m):
        open_tag, inner, close_tag = m.group(1), m.group(2), m.group(3)
        saved_pre_tags = []

        def save_legit(tm):
            tname = tm.group(1).lower()
            if tname in ("span", "div", "code", "strong", "em", "b", "i", "br"):
                k = len(saved_pre_tags)
                saved_pre_tags.append(tm.group(0))
                return f"\x00PTAG{k}\x00"
            return tm.group(0).replace("<", "&lt;").replace(">", "&gt;")

        inner2 = re.sub(r'</?\s*([a-zA-Z0-9]+)(?:\s+[^<>]*)?>', save_legit, inner)
        inner2 = inner2.replace("<", "&lt;")
        inner2 = re.sub(r'\x00PTAG(\d+)\x00', lambda x: saved_pre_tags[int(x.group(1))], inner2)
        idx = len(protected)
        protected.append(open_tag + inner2 + close_tag)
        return f"\x00BLK{idx}\x00"

    s = re.sub(r'(<pre\b[^>]*>)([\s\S]*?)(</pre>)', clean_pre, s, flags=re.I)

    # Mask legitimate HTML tags (with balanced attributes even if onclick contains `>` or `<`!)
    saved_tags = []
    out_chars = []
    i = 0
    n = len(s)
    while i < n:
        if s[i] == '<':
            # Try scanning a valid HTML tag `<tag ...>` or `</tag>` or `<!DOCTYPE ...>`
            if s[i:i+9].upper() == '<!DOCTYPE':
                end_dt = s.find('>', i)
                if end_dt != -1:
                    k = len(saved_tags)
                    saved_tags.append(s[i:end_dt+1])
                    out_chars.append(f"\x00HTAG{k}\x00")
                    i = end_dt + 1
                    continue
            m_t = re.match(r'^<\s*(/?)\s*([a-zA-Z][a-zA-Z0-9]*)\b', s[i:])
            if m_t and m_t.group(2).lower() in ALL_KNOWN_HTML_TAGS:
                # Scan until matching `>` respecting quoted attributes!
                j = i + m_t.end()
                in_q = None
                while j < n:
                    c = s[j]
                    if in_q:
                        if c == in_q:
                            in_q = None
                    elif c in ('"', "'"):
                        in_q = c
                    elif c == '>':
                        break
                    elif c == '<':
                        # Unclosed / not a real HTML tag if raw `<` appears outside quotes!
                        j = -1
                        break
                    j += 1
                if j != -1 and j < n and s[j] == '>':
                    k = len(saved_tags)
                    saved_tags.append(s[i:j+1])
                    out_chars.append(f"\x00HTAG{k}\x00")
                    i = j + 1
                    continue
            # Otherwise it's a raw `<` or `<Generic>`! Escape `<` to `&lt;`
            out_chars.append("&lt;")
            i += 1
        else:
            out_chars.append(s[i])
            i += 1

    s = "".join(out_chars)

    # Convert markdown `code` and **bold** in text (avoiding inside <code>...</code> by masking <code> spans first)
    def mask_code_el(cm):
        idx = len(protected)
        protected.append(cm.group(0))
        return f"\x00BLK{idx}\x00"

    # Temporarily restore <code> and </code> to mask code elements
    def restore_htags(txt):
        return re.sub(r'\x00HTAG(\d+)\x00', lambda x: saved_tags[int(x.group(1))], txt)

    s = restore_htags(s)
    s = re.sub(r'<code\b[^>]*>[\s\S]*?</code>', mask_code_el, s, flags=re.I)
    # Re-mask remaining HTML tags so we don't touch attributes
    saved_tags2 = []

    def mask_any_tag(tm):
        k = len(saved_tags2)
        saved_tags2.append(tm.group(0))
        return f"\x00T2_{k}\x00"

    # Match real HTML tags respecting quotes
    s2_out = []
    i = 0
    while i < len(s):
        if s[i] == '<':
            j = i + 1
            in_q = None
            while j < len(s):
                if in_q:
                    if s[j] == in_q:
                        in_q = None
                elif s[j] in ('"', "'"):
                    in_q = s[j]
                elif s[j] == '>':
                    break
                j += 1
            if j < len(s):
                k = len(saved_tags2)
                saved_tags2.append(s[i:j+1])
                s2_out.append(f"\x00T2_{k}\x00")
                i = j + 1
                continue
        s2_out.append(s[i])
        i += 1
    s = "".join(s2_out)

    s = re.sub(r'\*\*([^*<\x00\n]{2,80})\*\*', r'<strong>\1</strong>', s)
    s = re.sub(r'`([^`\x00\n]{1,90})`', r'<code>\1</code>', s)

    s = re.sub(r'\x00T2_(\d+)\x00', lambda x: saved_tags2[int(x.group(1))], s)
    # Unmask nested protected blocks (loop until all \x00BLK resolved)
    while '\x00BLK' in s:
        s = re.sub(r'\x00BLK(\d+)\x00', lambda x: protected[int(x.group(1))], s)
    return s


def linkify_sisters_in_section1(html_doc):
    m1 = re.search(r'(<section\s+id=["\']section-1["\'][\s\S]*?</section>)', html_doc)
    if not m1:
        return html_doc
    sec1 = m1.group(1)

    # Pattern A (Template v2): `<div class="flex items-center justify-between"><span>Name</span><span class="text-emerald-400">LC #123</span></div>`
    def repl_v2_sis(m):
        name_html, lc_txt = m.group(1), m.group(2)
        m_lc = re.search(r'(\d+)', lc_txt)
        if m_lc and int(m_lc.group(1)) in LC_TO_LESSON_FILE:
            target = LC_TO_LESSON_FILE[int(m_lc.group(1))]
            return f'<a href="{target}" class="flex items-center justify-between hover:text-emerald-400 underline decoration-slate-700 transition-colors"><span>{name_html}</span><span class="text-emerald-400">#{m_lc.group(1)} ↗</span></a>'
        return f'<a href="../reference/pattern-reference.html" class="flex items-center justify-between hover:text-emerald-400 underline decoration-slate-700 transition-colors"><span>{name_html}</span><span class="text-slate-400">{lc_txt}</span></a>'

    sec1_new = re.sub(
        r'<div class="flex items-center justify-between">\s*<span>([\s\S]*?)</span>\s*<span class="text-emerald-400">([\s\S]*?)</span>\s*</div>',
        repl_v2_sis,
        sec1
    )

    # Pattern B (Template v1 / Pass 1 text references like `LeetCode 198 (House Robber)` or `#128` or `master-plan.html`)
    sec1_new = sec1_new.replace(
        '<a href="../master-plan.html" class="text-slate-300 hover:text-emerald-400 underline">Problem #59: Longest Consecutive Sequence (#128)</a>',
        '<a href="0059-lc128-longest-consecutive-sequence.html" class="text-slate-300 hover:text-emerald-400 underline">Problem #59: Longest Consecutive Sequence (#128)</a>'
    )

    def repl_inline_lc(m):
        full = m.group(0)
        num = int(m.group(1))
        if num in LC_TO_LESSON_FILE:
            return f'<a href="{LC_TO_LESSON_FILE[num]}" class="text-emerald-400 hover:text-emerald-300 underline">{full}</a>'
        return full

    # If Sister section still has no `href=`, linkify `LeetCode NNN` or `#NNN` inside Sister block
    m_sis_pos = sec1_new.find("Sister")
    if m_sis_pos != -1 and 'href=' not in sec1_new[m_sis_pos:]:
        head_part = sec1_new[:m_sis_pos]
        tail_part = re.sub(r'(?:LeetCode\s+#?|LC\s*#?|#)(\d+)\b', repl_inline_lc, sec1_new[m_sis_pos:])
        if 'href=' not in tail_part:
            tail_part = tail_part.replace("Sister", '<a href="../reference/pattern-reference.html" class="underline hover:text-emerald-400">Sister</a>', 1)
        sec1_new = head_part + tail_part

    return html_doc[:m1.start()] + sec1_new + html_doc[m1.end():]


def rebuild_section25(html_doc, sols, method_name, category):
    m25 = re.search(r'(<section\s+id=["\']section-2-5["\'][\s\S]*?</section>)', html_doc)
    if not m25:
        return html_doc
    s25 = m25.group(1)

    # 1. Replace mangled <pre> pseudocode inside section-2-5 using clean Kotlin modelSolution lines
    kt_code = sols.get("kotlin", "")
    raw_lines = [l for l in kt_code.splitlines() if l.strip()]
    clean_lines = to_clean_pseudocode(raw_lines, method_name)
    pre_inner = "".join(
        f'<span class="text-slate-600 select-none mr-3">{idx:02d}</span>{html.escape(line)}\n'
        for idx, line in enumerate(clean_lines, 1)
    )
    s25 = re.sub(
        r'(<pre\b[^>]*>)[\s\S]*?(</pre>)',
        lambda m: m.group(1) + pre_inner + m.group(2),
        s25,
        count=1
    )

    # 2. Replace tautological `Tracks X across iterations (initial state: <code>Y</code>).`
    def repl_taut(m):
        lbl = m.group(1).strip()
        init_v = m.group(3).strip()
        role = describe_state_metric(lbl, init_v, category)
        return f'<strong class="text-emerald-400 font-mono block mb-0.5">{lbl}</strong>\n              <span class="text-slate-300 leading-relaxed">{role}</span>'

    s25 = re.sub(
        r'<strong class="text-emerald-400 font-mono block mb-0\.5">([^<]+)</strong>\s*<span class="text-slate-300 leading-relaxed">Tracks ([^<]+?) across iterations \(initial state: <code>([^<]*)</code>\)\.</span>',
        repl_taut,
        s25
    )

    # 3. Scrub first-person `"Let me write the code..."` quotes inside Step 3 of Non-Code Walkthrough
    s25 = re.sub(r'"?Let me write the code\.\s*', '', s25)
    s25 = re.sub(r'"?Let\'s write the code\.\s*', '', s25)
    s25 = s25.replace('" Throughout every transition,', ' Throughout every transition,')

    return html_doc[:m25.start()] + s25 + html_doc[m25.end():]


def main():
    lessons = sorted(glob.glob("lessons/*.html"))
    assert len(lessons) >= 116, f"Expected >= 116 lessons, found {len(lessons)}"

    # Render 0067 fresh from P67_ICA_V2
    p67_html = render_lesson_v2(P67_ICA_V2)
    with open("lessons/0067-custom-in-memory-kv-store-ttl.html", "w", encoding="utf-8") as f:
        f.write(p67_html)
    print("✓ Rendered 0067-custom-in-memory-kv-store-ttl.html (Deterministic 4-Stage ICA)")

    # Map lesson num -> category from master-plan.html
    mp_text = open("master-plan.html", encoding="utf-8").read()
    num_to_cat = {}
    for m in re.finditer(r'\{\s*num:\s*(\d+).*?cat:\s*"([^"]+)"', mp_text):
        num_to_cat[int(m.group(1))] = m.group(2)
    num_to_cat[68] = "Multi-Tier OOP & Timed State Systems"

    updated = 0
    for num in range(1, 117):
        if num == 67:
            updated += 1
            continue
        path = f"lessons/{os.path.basename([p for p in lessons if os.path.basename(p).startswith(f'{num:04d}-')][0])}"
        doc = open(path, encoding="utf-8").read()
        cat = num_to_cat.get(num, "Algorithms")

        # Update 0068 category
        if num == 68:
            doc = doc.replace("CATEGORY: Linked Lists & System Node Design", "CATEGORY: Multi-Tier OOP & Timed State Systems")
            doc = doc.replace("CATEGORY: Linked Lists &amp; System Node Design", "CATEGORY: Multi-Tier OOP &amp; Timed State Systems")

        # Update 0116 sequential Next button to point to 0117
        if num == 116:
            doc = doc.replace(
                '<a href="../master-plan.html" class="px-3 py-1.5 rounded-lg bg-emerald-500 text-slate-950 font-bold text-xs font-mono hover:bg-emerald-400 transition-colors flex items-center gap-1.5" title="Next Problem">\n          <span class="hidden sm:inline">Curriculum Hub</span> <span>&rarr;</span>\n        </a>',
                '<a href="0117-lc242-valid-anagram.html" class="px-3 py-1.5 rounded-lg bg-emerald-500 text-slate-950 font-bold text-xs font-mono hover:bg-emerald-400 transition-colors flex items-center gap-1.5" title="Next Problem">\n          <span class="hidden sm:inline">Next #117</span> <span>&rarr;</span>\n        </a>'
            )

        # Cross-link Rate-Limiter Arc (#0098 -> #0010 -> #0032)
        if num == 98:
            doc = doc.replace("Design Hit Counter</span><span class=\"text-emerald-400\">#362</span>", "Number of Recent Calls</span><span class=\"text-emerald-400\">#933</span>")

        # Parse PracticeArena.init({...})
        m_pa = re.search(r'LessonRuntime\.PracticeArena\.init\(\s*\{', doc)
        assert m_pa, f"Missing PracticeArena.init in {path}"
        pa_open = m_pa.end() - 1
        pa_close = extract_balanced_braces(doc, pa_open)
        pa_obj_str = re.sub(r"comparator\s*:\s*anagramComparator\s*,?", "", doc[pa_open:pa_close + 1])
        pa_cfg = eval_js_obj_via_node(pa_obj_str)

        method_name = pa_cfg.get("methodName", "solve")
        io_type = pa_cfg.get("ioType")
        if num == 35 and not io_type:
            io_type = "random_list"
            pa_cfg["ioType"] = "random_list"
        if num == 82 and not io_type:
            io_type = "graph"
            pa_cfg["ioType"] = "graph"

        sols = pa_cfg["modelSolutions"]

        # Rebuild stubs where needed + ensure node header comments on all node problems
        if num in REBUILD_STUB_NUMS or io_type in ("list", "list_head_n", "list_cycle", "list_intersect", "list_array", "tree", "tree_lca", "random_list", "graph"):
            pa_cfg["starterStubs"] = derive_clean_stubs_from_solutions(sols, method_name, io_type)

        # Apply exact test overrides for 23, 48, 49, 60, 78, 79, 80
        if num in EXACT_TEST_OVERRIDES:
            pa_cfg["testCases"] = EXACT_TEST_OVERRIDES[num]

        # Re-serialize PracticeArena.init cleanly
        new_pa_str = json.dumps(pa_cfg, ensure_ascii=False)
        doc = doc[:pa_open] + new_pa_str + doc[pa_close + 1:]

        # Parse & normalize Quiz.init({...})
        m_qz = re.search(r'LessonRuntime\.Quiz\.init\(\s*\{', doc)
        assert m_qz, f"Missing Quiz.init in {path}"
        qz_open = m_qz.end() - 1
        qz_close = extract_balanced_braces(doc, qz_open)
        qz_cfg = eval_js_obj_via_node(doc[qz_open:qz_close + 1])
        qz_cfg["problemId"] = f"{num:04d}"
        qz_cfg["questions"] = normalize_quiz_questions(qz_cfg.get("questions", []), num)
        doc = doc[:qz_open] + json.dumps(qz_cfg, ensure_ascii=False) + doc[qz_close + 1:]

        # Fix broken steppers (24 files)
        if num in STEPPER_INIT_OVERRIDES:
            init_call = STEPPER_INIT_OVERRIDES[num]
            doc = re.sub(
                r'// Safe Stepper Execution[\s\S]*?catch\(err\)\s*\{\s*console\.warn\("Stepper buildSteps note:",\s*err\);\s*\}',
                f'try {{ {init_call} }} catch (err) {{ console.warn("Stepper buildSteps note:", err); }}',
                doc
            )

        # Rebuild Section 2.5 cleanly
        doc = rebuild_section25(doc, sols, method_name, cat)

        # Linkify Sister Problems in Section 1
        doc = linkify_sisters_in_section1(doc)

        # Upgrade generic kotlinlang.org/docs/home.html primary source
        if "https://kotlinlang.org/docs/home.html" in doc:
            src_url, src_title, src_book = CATEGORY_PRIMARY_SOURCES.get(
                cat,
                ("https://kotlinlang.org/docs/collections-overview.html", "Kotlin Collections Reference", "CLRS 4th Edition")
            )
            doc = re.sub(
                r'<a href="https://kotlinlang\.org/docs/home\.html"[^>]*>[\s\S]*?</p>',
                f'<a href="{src_url}" target="_blank" rel="noopener noreferrer" class="text-emerald-400 underline font-semibold">{html.escape(src_title)}</a> &amp; {html.escape(src_book)}. Quick-review in <a href="../reference/pattern-reference.html" class="text-cyan-400 underline">Pattern Cheat Sheet</a> and <a href="../reference/glossary.html" class="text-cyan-400 underline">Glossary</a>.</p>',
                doc
            )

        # Update stale branding (96 / 103 / 110 / 116 -> 126) & Senior -> Mid-Level (L4)
        doc = re.sub(r'(\bLESSON\s+\d+\s+OF\s+)(?:96|103|110|116)\b', r'\g<1>126', doc)
        doc = re.sub(r'(Mid-Tech LeetCode\s+)(?:96|103|110|116)\b', r'\g<1>126', doc)
        doc = re.sub(r'Curriculum Hub \((?:96|103|110|116) Problems\)', 'Curriculum Hub (126 Problems)', doc)
        doc = re.sub(r'(title="Back to Curriculum Hub"[^>]*>\s*)(?:96|103|110|116)(\s*</a>)', r'\g<1>126\2', doc)
        doc = doc.replace("Live Interview Spoken Script &amp; Senior Follow-Ups", "Live Interview Spoken Script &amp; Mid-Level (L4) Follow-Ups")
        doc = doc.replace("Senior Architectural &amp; Scalability Follow-Up:", "Mid-Level (L4) Production &amp; Requirement-Pivot Follow-Up:")

        # Sanitize all unescaped <Generic> / raw '<', raw LaTeX, doubled quotes, and leaked markdown outside scripts!
        doc = sanitize_html_outside_scripts(doc)

        with open(path, "w", encoding="utf-8") as f:
            f.write(doc)
        updated += 1

    print(f"🎉 Successfully repaired and unified all {updated} / 116 lessons!")


if __name__ == "__main__":
    main()
