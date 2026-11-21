/**
 * Mid-Tech LeetCode 110 Masterclass — Shared Runtime Engine
 *
 * Includes:
 * 1. High-DPI Vector Whiteboard & Sketchpad
 * 2. Multi-Approach & Tri-Language Code Viewer with Clipboard Copy
 * 3. Runnable Tri-Language Practice Arena (Kotlin, Python, Java)
 *    - Supports Standard functions, ListNode, TreeNode, Graph Node, and OOP Design Classes
 *    - 3 Progressive Hints + Gated Model Reveal + Infinite-Loop Guard
 * 4. 4-Question Diagnostic Code Quiz Engine with Per-Option Right/Wrong Explanations
 * 5. 48-Hour Spaced-Repetition Recall Tracker
 */

(function(global) {
  'use strict';

  var LessonRuntime = {};

  // =========================================================================
  // 1. HIGH-DPI VECTOR WHITEBOARD ENGINE
  // =========================================================================
  LessonRuntime.Whiteboard = {
    init: function(config) {
      var canvas = document.getElementById(config.canvasId || 'whiteboard-canvas');
      if (!canvas) return;

      var ctx = canvas.getContext('2d');
      var strokes = [];
      var currentStroke = null;
      var currentColor = '#10b981';
      var currentSize = 4;
      var currentMode = 'draw';

      function resize() {
        var rect = canvas.getBoundingClientRect();
        var dpr = window.devicePixelRatio || 1;
        canvas.width = rect.width * dpr;
        canvas.height = rect.height * dpr;
        ctx.scale(dpr, dpr);
        redraw();
      }

      function getCoords(e) {
        var rect = canvas.getBoundingClientRect();
        var clientX = e.touches ? e.touches[0].clientX : e.clientX;
        var clientY = e.touches ? e.touches[0].clientY : e.clientY;
        return { x: clientX - rect.left, y: clientY - rect.top };
      }

      function redraw() {
        var rect = canvas.getBoundingClientRect();
        ctx.clearRect(0, 0, rect.width, rect.height);
        for (var i = 0; i < strokes.length; i++) {
          var s = strokes[i];
          if (!s.points || s.points.length < 2) continue;
          ctx.strokeStyle = s.color;
          ctx.lineWidth = s.size;
          ctx.lineCap = 'round';
          ctx.lineJoin = 'round';
          ctx.beginPath();
          ctx.moveTo(s.points[0].x, s.points[0].y);
          for (var p = 1; p < s.points.length; p++) {
            ctx.lineTo(s.points[p].x, s.points[p].y);
          }
          ctx.stroke();
        }
      }

      function startDraw(e) {
        if (e.touches) e.preventDefault();
        var pos = getCoords(e);
        currentStroke = {
          color: currentMode === 'erase' ? '#020617' : currentColor,
          size: currentMode === 'erase' ? currentSize * 4 : currentSize,
          points: [pos]
        };
        strokes.push(currentStroke);
        var hint = document.getElementById(config.hintId || 'wb-hint');
        if (hint) hint.style.display = 'none';
      }

      function moveDraw(e) {
        if (!currentStroke) return;
        if (e.touches) e.preventDefault();
        var pos = getCoords(e);
        currentStroke.points.push(pos);
        var pts = currentStroke.points;
        if (pts.length >= 2) {
          ctx.strokeStyle = currentStroke.color;
          ctx.lineWidth = currentStroke.size;
          ctx.lineCap = 'round';
          ctx.lineJoin = 'round';
          ctx.beginPath();
          ctx.moveTo(pts[pts.length - 2].x, pts[pts.length - 2].y);
          ctx.lineTo(pts[pts.length - 1].x, pts[pts.length - 1].y);
          ctx.stroke();
        }
      }

      function endDraw() { currentStroke = null; }

      canvas.addEventListener('mousedown', startDraw);
      window.addEventListener('mousemove', moveDraw);
      window.addEventListener('mouseup', endDraw);
      canvas.addEventListener('touchstart', startDraw, { passive: false });
      window.addEventListener('touchmove', moveDraw, { passive: false });
      window.addEventListener('touchend', endDraw);
      window.addEventListener('resize', resize);

      window.wbSetColor = function(color) {
        currentColor = color;
        currentMode = 'draw';
        document.querySelectorAll('.wb-color-btn').forEach(function(btn) {
          btn.className = btn.getAttribute('data-color') === color
            ? "wb-color-btn w-6 h-6 rounded-full ring-2 ring-white ring-offset-2 ring-offset-slate-950 transition-all"
            : "wb-color-btn w-6 h-6 rounded-full hover:scale-110 transition-all";
        });
        var penBtn = document.getElementById('wb-tool-pen');
        var eraserBtn = document.getElementById('wb-tool-eraser');
        if (penBtn && eraserBtn) {
          penBtn.className = "px-2.5 py-1 rounded bg-teal-500/20 text-teal-300 font-bold transition-colors";
          eraserBtn.className = "px-2.5 py-1 rounded text-slate-400 hover:text-white transition-colors";
        }
      };

      window.wbSetMode = function(mode) {
        currentMode = mode;
        var penBtn = document.getElementById('wb-tool-pen');
        var eraserBtn = document.getElementById('wb-tool-eraser');
        if (mode === 'draw') {
          if (penBtn) penBtn.className = "px-2.5 py-1 rounded bg-teal-500/20 text-teal-300 font-bold transition-colors";
          if (eraserBtn) eraserBtn.className = "px-2.5 py-1 rounded text-slate-400 hover:text-white transition-colors";
        } else {
          if (eraserBtn) eraserBtn.className = "px-2.5 py-1 rounded bg-teal-500/20 text-teal-300 font-bold transition-colors";
          if (penBtn) penBtn.className = "px-2.5 py-1 rounded text-slate-400 hover:text-white transition-colors";
        }
      };

      window.wbSetSize = function(size) {
        currentSize = size;
        [2, 4, 8].forEach(function(s) {
          var btn = document.getElementById('wb-size-' + s);
          if (btn) {
            btn.className = s === size
              ? "wb-size-btn px-2 py-0.5 rounded bg-teal-500/20 border border-teal-500/40 text-[11px] text-teal-300 font-bold"
              : "wb-size-btn px-2 py-0.5 rounded bg-slate-900 border border-slate-800 text-[11px] text-slate-400";
          }
        });
      };

      window.wbUndo = function() { strokes.pop(); redraw(); };
      window.wbClear = function() {
        strokes = [];
        redraw();
        var hint = document.getElementById(config.hintId || 'wb-hint');
        if (hint) hint.style.display = 'block';
      };

      window.wbToggle = function() {
        var body = document.getElementById(config.bodyId || 'wb-body');
        var icon = document.getElementById(config.toggleIconId || 'wb-toggle-icon');
        var text = document.getElementById(config.toggleTextId || 'wb-toggle-text');
        if (!body) return;
        if (body.classList.contains('hidden')) {
          body.classList.remove('hidden');
          if (icon) icon.innerText = '▲';
          if (text) text.innerText = 'Minimize Board';
          setTimeout(resize, 50);
        } else {
          body.classList.add('hidden');
          if (icon) icon.innerText = '▼';
          if (text) text.innerText = 'Open Sketchpad';
        }
      };

      setTimeout(resize, 50);
    }
  };

  // =========================================================================
  // 2. MULTI-APPROACH & TRI-LANGUAGE CODE TABS
  // =========================================================================
  LessonRuntime.MultiApproach = {
    init: function(config) {
      var currentApproach = config.defaultApproach || 'optimal';
      var currentLang = config.defaultLang || 'kotlin';

      function updateUI() {
        ['brute', 'better', 'optimal'].forEach(function(a) {
          var btn = document.getElementById('tab-app-' + a);
          if (btn) {
            btn.className = a === currentApproach
              ? "approach-btn active px-3 py-1.5 rounded-lg text-xs font-mono font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 transition-all shadow-sm"
              : "approach-btn px-3 py-1.5 rounded-lg text-xs font-mono font-semibold text-slate-400 hover:text-white transition-all";
          }
        });

        var pill = document.getElementById('approach-complexity-pill');
        if (pill && config.complexity && config.complexity[currentApproach]) {
          pill.innerHTML = config.complexity[currentApproach].html;
          pill.className = "text-xs font-mono px-3 py-1.5 rounded-lg " + config.complexity[currentApproach].className;
        }

        ['kotlin', 'python', 'java'].forEach(function(l) {
          var btn = document.getElementById('lang-btn-' + l);
          if (btn) {
            btn.className = l === currentLang
              ? "lang-tab active px-4 py-2 rounded-t-lg text-xs font-bold font-mono border-b-2 border-emerald-400 text-emerald-400 bg-slate-900"
              : "lang-tab px-4 py-2 rounded-t-lg text-xs font-bold font-mono border-b-2 border-transparent text-slate-400 hover:text-slate-200";
          }
        });

        document.querySelectorAll('.code-solution-card').forEach(function(c) {
          c.classList.add('hidden');
        });
        var target = document.getElementById('code-' + currentApproach + '-' + currentLang);
        if (target) target.classList.remove('hidden');
      }

      window.switchApproach = function(app) { currentApproach = app; updateUI(); };
      window.switchLang = function(lang) { currentLang = lang; updateUI(); };
      window.copyCurrentCode = function() {
        var target = document.querySelector('#code-' + currentApproach + '-' + currentLang + ' pre');
        if (target && navigator.clipboard) {
          navigator.clipboard.writeText(target.innerText).then(function() {
            var icon = document.getElementById('copy-icon');
            var label = document.getElementById('copy-text');
            if (icon) icon.innerText = '✓';
            if (label) label.innerText = 'Copied!';
            setTimeout(function() {
              if (icon) icon.innerText = '📋';
              if (label) label.innerText = 'Copy Code';
            }, 2000);
          });
        }
      };

      updateUI();
    }
  };

  // =========================================================================
  // 3. RUNNABLE CODE PRACTICE ARENA (JVM-SUBSET & PYTHON RUNNER)
  // =========================================================================
  var __nodeOidCounter = 0;
  function __assignOid(obj) {
    Object.defineProperty(obj, '_oid', { value: '__OID_' + (++__nodeOidCounter), enumerable: false, configurable: true });
  }

  function ListNode(val, next) {
    __assignOid(this);
    this.val = val === undefined ? 0 : val;
    this.next = next === undefined ? null : next;
  }
  ListNode.prototype.toString = function() { return this._oid; };

  function TreeNode(val, left, right) {
    __assignOid(this);
    this.val = val === undefined ? 0 : val;
    this.left = left === undefined ? null : left;
    this.right = right === undefined ? null : right;
  }
  TreeNode.prototype.toString = function() { return this._oid; };

  function GraphNode(val, neighborsOrNext, random) {
    __assignOid(this);
    this.val = val === undefined ? 0 : val;
    if (Array.isArray(neighborsOrNext)) {
      this.neighbors = neighborsOrNext;
    } else {
      this.neighbors = [];
      this.next = neighborsOrNext === undefined ? null : neighborsOrNext;
      this.random = random === undefined ? null : random;
    }
  }
  GraphNode.prototype.toString = function() { return this._oid; };

  function toListNode(arr, pos) {
    if (!Array.isArray(arr) || arr.length === 0) return null;
    var nodes = arr.map(function(v) { return new ListNode(v); });
    for (var i = 0; i < nodes.length - 1; i++) nodes[i].next = nodes[i + 1];
    if (typeof pos === 'number' && pos >= 0 && pos < nodes.length) {
      nodes[nodes.length - 1].next = nodes[pos];
    }
    return nodes[0];
  }

  function fromListNode(head) {
    var out = [];
    var seen = new Set();
    var curr = head;
    while (curr && !seen.has(curr) && out.length < 10000) {
      seen.add(curr);
      out.push(curr.val);
      curr = curr.next;
    }
    return out;
  }

  function toTreeNode(arr) {
    if (!Array.isArray(arr) || arr.length === 0 || arr[0] === null) return null;
    var root = new TreeNode(arr[0]);
    var q = [root];
    var i = 1;
    while (q.length > 0 && i < arr.length) {
      var node = q.shift();
      if (i < arr.length && arr[i] !== null && arr[i] !== undefined) {
        node.left = new TreeNode(arr[i]);
        q.push(node.left);
      }
      i++;
      if (i < arr.length && arr[i] !== null && arr[i] !== undefined) {
        node.right = new TreeNode(arr[i]);
        q.push(node.right);
      }
      i++;
    }
    return root;
  }

  function fromTreeNode(root) {
    if (!root) return [];
    var out = [];
    var q = [root];
    while (q.length > 0 && out.length < 10000) {
      var n = q.shift();
      if (n === null) {
        out.push(null);
      } else {
        out.push(n.val);
        q.push(n.left || null);
        q.push(n.right || null);
      }
    }
    while (out.length > 0 && out[out.length - 1] === null) out.pop();
    return out;
  }

  function findTreeNode(root, targetVal) {
    if (!root) return null;
    if (root.val === targetVal) return root;
    return findTreeNode(root.left, targetVal) || findTreeNode(root.right, targetVal);
  }

  function toRandomList(spec) {
    if (!Array.isArray(spec) || spec.length === 0) return null;
    var nodes = spec.map(function(pair) { return new GraphNode(pair[0], null, null); });
    for (var i = 0; i < nodes.length; i++) {
      if (i + 1 < nodes.length) nodes[i].next = nodes[i + 1];
      var rIdx = spec[i][1];
      nodes[i].random = (rIdx !== null && rIdx !== undefined) ? nodes[rIdx] : null;
    }
    return nodes[0];
  }

  function fromRandomList(head) {
    if (!head) return [];
    var nodes = [];
    var idxMap = new Map();
    var curr = head;
    while (curr && !idxMap.has(curr) && nodes.length < 10000) {
      idxMap.set(curr, nodes.length);
      nodes.push(curr);
      curr = curr.next;
    }
    return nodes.map(function(n) {
      return [n.val, n.random !== null && n.random !== undefined ? idxMap.get(n.random) : null];
    });
  }

  function toGraph(adj) {
    if (!Array.isArray(adj) || adj.length === 0) return null;
    var nodes = adj.map(function(_, idx) { return new GraphNode(idx + 1, []); });
    for (var i = 0; i < adj.length; i++) {
      nodes[i].neighbors = adj[i].map(function(nb) { return nodes[nb - 1]; });
    }
    return nodes[0];
  }

  function fromGraph(node) {
    if (!node) return [];
    var map = new Map();
    var q = [node];
    map.set(node.val, node);
    while (q.length > 0 && map.size < 1000) {
      var curr = q.shift();
      var nbs = curr.neighbors || [];
      for (var i = 0; i < nbs.length; i++) {
        if (!map.has(nbs[i].val)) {
          map.set(nbs[i].val, nbs[i]);
          q.push(nbs[i]);
        }
      }
    }
    var maxV = Math.max.apply(null, Array.from(map.keys()));
    var out = [];
    for (var v = 1; v <= maxV; v++) {
      var n = map.get(v);
      out.push(n ? (n.neighbors || []).map(function(x) { return x.val; }) : []);
    }
    return out;
  }

  // =========================================================================
  // RUNTIME SHIMS & COLLECTION HELPERS
  // =========================================================================
  LessonRuntime._createRuntimeEnv = function() {
    function __sub(a, b) {
      var na = typeof a === 'string' && a.length === 1 ? a.charCodeAt(0) : Number(a);
      var nb = typeof b === 'string' && b.length === 1 ? b.charCodeAt(0) : Number(b);
      return na - nb;
    }

    function __ord(c) {
      return typeof c === 'string' && c.length > 0 ? c.charCodeAt(0) : Number(c);
    }

    function __chr(n) {
      return String.fromCharCode(Number(n));
    }

    function __Pair(a, b) {
      var arr = [a, b];
      Object.defineProperty(arr, 'first', { get: function() { return this[0]; } });
      Object.defineProperty(arr, 'second', { get: function() { return this[1]; } });
      return arr;
    }

    function __Triple(a, b, c) {
      var arr = [a, b, c];
      Object.defineProperty(arr, 'first', { get: function() { return this[0]; } });
      Object.defineProperty(arr, 'second', { get: function() { return this[1]; } });
      Object.defineProperty(arr, 'third', { get: function() { return this[2]; } });
      return arr;
    }

    function __sort(arr, cmp) {
      if (!Array.isArray(arr)) return arr;
      if (typeof cmp === 'function') return arr.sort(cmp);
      if (arr.length > 0 && typeof arr[0] === 'number') {
        return arr.sort(function(a, b) { return a - b; });
      }
      return arr.sort();
    }

    function __iter(col) {
      if (col == null) return [];
      if (Array.isArray(col)) return col;
      if (typeof col === 'string') return col.split('');
      if (col instanceof Set) return Array.from(col.values());
      if (col instanceof Map) return Array.from(col.keys());
      if (col._vals instanceof Map) return Array.from(col._vals.values());
      if (col._set instanceof Set) return Array.from(col._set.values());
      if (col._map instanceof Map) return Array.from(col._map.entries());
      if (Array.isArray(col._items)) return col._items;
      if (typeof col[Symbol.iterator] === 'function') return Array.from(col);
      if (typeof col === 'object') return Object.keys(col);
      return [];
    }

    function __HashSet(init) {
      this._set = new Set();
      this._vals = new Map();
      if (init) {
        var items = __iter(init);
        for (var i = 0; i < items.length; i++) this.add(items[i]);
      }
    }
    __HashSet.prototype.add = function(val) {
      var k = (Array.isArray(val) ? JSON.stringify(val) : val);
      if (this._set.has(k)) return false;
      this._set.add(k);
      this._vals.set(k, val);
      return true;
    };
    __HashSet.prototype.contains = function(val) {
      var k = (Array.isArray(val) ? JSON.stringify(val) : val);
      return this._set.has(k);
    };
    __HashSet.prototype.has = __HashSet.prototype.contains;
    __HashSet.prototype.remove = function(val) {
      var k = (Array.isArray(val) ? JSON.stringify(val) : val);
      this._vals.delete(k);
      return this._set.delete(k);
    };
    __HashSet.prototype.delete = __HashSet.prototype.remove;
    __HashSet.prototype.clear = function() { this._set.clear(); this._vals.clear(); };
    __HashSet.prototype.isEmpty = function() { return this._set.size === 0; };
    __HashSet.prototype.isNotEmpty = function() { return this._set.size > 0; };
    __HashSet.prototype.toList = function() { return Array.from(this._vals.values()); };
    __HashSet.prototype[Symbol.iterator] = function() { return this._vals.values(); };
    Object.defineProperty(__HashSet.prototype, 'size', {
      get: function() { return this._set.size; }
    });

    function __HashMap(init) {
      this._map = new Map();
      if (init && typeof init === 'object') {
        if (init._map instanceof Map) {
          var self = this;
          init._map.forEach(function(v, k) { self._map.set(k, v); });
        } else {
          var ks = Object.keys(init);
          for (var i = 0; i < ks.length; i++) this._map.set(ks[i], init[ks[i]]);
        }
      }
      var selfMap = this;
      return new Proxy(this, {
        get: function(target, prop) {
          if (prop === '_map') return target._map;
          if (prop === 'size') return target._map.size;
          if (prop in target) return target[prop];
          var key = target._proxyKey(prop);
          return target._map.get(target._resolveKey(key));
        },
        set: function(target, prop, value) {
          if (prop in target) {
            target[prop] = value;
          } else {
            var key = target._proxyKey(prop);
            target._map.set(key, value);
          }
          return true;
        },
        deleteProperty: function(target, prop) {
          var key = target._proxyKey(prop);
          return target._map.delete(target._resolveKey(key));
        }
      });
    }
    __HashMap.prototype._normKey = function(k) {
      if (Array.isArray(k)) return JSON.stringify(k);
      if (k && typeof k._oid === 'string') return k._oid;
      return k;
    };
    __HashMap.prototype._proxyKey = function(prop) {
      if (typeof prop === 'string' && /^-?\d+$/.test(prop) && String(Number(prop)) === prop) {
        return Number(prop);
      }
      return prop;
    };
    __HashMap.prototype._resolveKey = function(k) {
      var nk = this._normKey(k);
      if (this._map.has(nk)) return nk;
      if (typeof nk === 'string' && /^-?\d+$/.test(nk) && String(Number(nk)) === nk && this._map.has(Number(nk))) {
        return Number(nk);
      }
      if (typeof nk === 'number' && this._map.has(String(nk))) {
        return String(nk);
      }
      return nk;
    };
    __HashMap.prototype.put = function(k, v) { this._map.set(this._normKey(k), v); return v; };
    __HashMap.prototype.set = __HashMap.prototype.put;
    __HashMap.prototype.putIfAbsent = function(k, v) {
      var rk = this._resolveKey(k);
      if (!this._map.has(rk)) this._map.set(this._normKey(k), v);
      return this._map.get(rk);
    };
    __HashMap.prototype.get = function(k, d) {
      var rk = this._resolveKey(k);
      if (this._map.has(rk)) return this._map.get(rk);
      return arguments.length >= 2 ? d : undefined;
    };
    __HashMap.prototype.has = function(k) { return this._map.has(this._resolveKey(k)); };
    __HashMap.prototype.getOrDefault = function(k, d) {
      var rk = this._resolveKey(k);
      return this._map.has(rk) ? this._map.get(rk) : d;
    };
    __HashMap.prototype.containsKey = __HashMap.prototype.has;
    __HashMap.prototype.contains = __HashMap.prototype.has;
    __HashMap.prototype.remove = function(k, d) {
      var rk = this._resolveKey(k);
      if (!this._map.has(rk)) return arguments.length >= 2 ? d : undefined;
      var v = this._map.get(rk);
      this._map.delete(rk);
      return v;
    };
    __HashMap.prototype.pop = __HashMap.prototype.remove;
    __HashMap.prototype.delete = __HashMap.prototype.remove;
    __HashMap.prototype.clear = function() { this._map.clear(); };
    __HashMap.prototype.isEmpty = function() { return this._map.size === 0; };
    __HashMap.prototype.isNotEmpty = function() { return this._map.size > 0; };
    __HashMap.prototype.getOrPut = function(k, defaultValueFn) {
      var nk = this._normKey(k);
      if (!this._map.has(nk)) {
        var val = typeof defaultValueFn === 'function' ? defaultValueFn() : defaultValueFn;
        this._map.set(nk, val);
      }
      return this._map.get(nk);
    };
    __HashMap.prototype.computeIfAbsent = function(k, mappingFn) {
      var nk = this._normKey(k);
      if (!this._map.has(nk)) {
        var val = typeof mappingFn === 'function' ? mappingFn(k) : mappingFn;
        this._map.set(nk, val);
      }
      return this._map.get(nk);
    };
    __HashMap.prototype.keys = function() { return Array.from(this._map.keys()); };
    __HashMap.prototype.keySet = __HashMap.prototype.keys;
    __HashMap.prototype.values = function() { return Array.from(this._map.values()); };
    __HashMap.prototype.entries = function() { return Array.from(this._map.entries()); };
    __HashMap.prototype.entrySet = __HashMap.prototype.entries;
    __HashMap.prototype.tailMap = function(fromKey) {
      var ks = Array.from(this._map.keys()).sort();
      var res = new __HashMap();
      for (var i = 0; i < ks.length; i++) {
        if (ks[i] >= fromKey) res.put(ks[i], this._map.get(ks[i]));
      }
      return res;
    };
    __HashMap.prototype.forEach = function(cb) {
      this._map.forEach(function(v, k) { cb(k, v); });
    };
    __HashMap.prototype[Symbol.iterator] = function() { return this._map.entries(); };

    // TreeMap shim (sorted keys)
    function __TreeMap() { return new __HashMap(); }

    function __ArrayList(init) {
      var arr = [];
      if (init && typeof init !== 'number') {
        var items = Array.isArray(init) ? init : __iter(init);
        for (var i = 0; i < items.length; i++) arr.push(items[i]);
      }
      return arr;
    }

    function __ArrayDeque(init) {
      this._items = Array.isArray(init) ? init.slice() : (init ? __iter(init).slice() : []);
      return new Proxy(this, {
        get: function(target, prop) {
          if (typeof prop === 'string' && /^-?\d+$/.test(prop)) {
            var idx = Number(prop);
            if (idx < 0) idx = target._items.length + idx;
            return target._items[idx];
          }
          if (prop === 'length' || prop === 'size') return target._items.length;
          return target[prop];
        },
        set: function(target, prop, value) {
          if (typeof prop === 'string' && /^-?\d+$/.test(prop)) {
            var idx = Number(prop);
            if (idx < 0) idx = target._items.length + idx;
            target._items[idx] = value;
            return true;
          }
          target[prop] = value;
          return true;
        }
      });
    }
    __ArrayDeque.prototype.addLast = function(x) { this._items.push(x); return true; };
    __ArrayDeque.prototype.addFirst = function(x) { this._items.unshift(x); return true; };
    __ArrayDeque.prototype.add = function(x) { this._items.push(x); return true; };
    __ArrayDeque.prototype.offer = function(x) { this._items.push(x); return true; };
    __ArrayDeque.prototype.offerLast = __ArrayDeque.prototype.addLast;
    __ArrayDeque.prototype.offerFirst = __ArrayDeque.prototype.addFirst;
    __ArrayDeque.prototype.push = function(x) { this._items.push(x); return x; };
    __ArrayDeque.prototype.append = __ArrayDeque.prototype.addLast;
    __ArrayDeque.prototype.appendleft = __ArrayDeque.prototype.addFirst;
    __ArrayDeque.prototype.removeLast = function() { return this._items.pop(); };
    __ArrayDeque.prototype.pop = function() { return this._items.pop(); };
    __ArrayDeque.prototype.removeFirst = function() { return this._items.shift(); };
    __ArrayDeque.prototype.popleft = function() { return this._items.shift(); };
    __ArrayDeque.prototype.poll = function() { return this._items.shift(); };
    __ArrayDeque.prototype.pollFirst = function() { return this._items.shift(); };
    __ArrayDeque.prototype.pollLast = function() { return this._items.pop(); };
    __ArrayDeque.prototype.first = function() { return this._items[0]; };
    __ArrayDeque.prototype.firstOrNull = function() { return this._items.length ? this._items[0] : null; };
    __ArrayDeque.prototype.last = function() { return this._items[this._items.length - 1]; };
    __ArrayDeque.prototype.lastOrNull = function() { return this._items.length ? this._items[this._items.length - 1] : null; };
    __ArrayDeque.prototype.peek = function() { return this._items.length ? this._items[this._items.length - 1] : null; };
    __ArrayDeque.prototype.peekFirst = function() { return this._items.length ? this._items[0] : null; };
    __ArrayDeque.prototype.peekLast = function() { return this._items.length ? this._items[this._items.length - 1] : null; };
    __ArrayDeque.prototype.isEmpty = function() { return this._items.length === 0; };
    __ArrayDeque.prototype.isNotEmpty = function() { return this._items.length > 0; };
    __ArrayDeque.prototype.clear = function() { this._items = []; };
    __ArrayDeque.prototype.toArray = function() { return this._items.slice(); };
    __ArrayDeque.prototype.toIntArray = function() { return this._items.slice(); };
    Object.defineProperty(__ArrayDeque.prototype, 'size', { get: function() { return this._items.length; } });
    Object.defineProperty(__ArrayDeque.prototype, 'length', { get: function() { return this._items.length; } });
    __ArrayDeque.prototype[Symbol.iterator] = function() { return this._items[Symbol.iterator](); };

    // Min/Custom Binary Heap PriorityQueue
    function __PriorityQueue(cmp) {
      this._heap = [];
      if (cmp && cmp.__reverse) {
        this._cmp = function(a, b) { return b - a; };
      } else if (typeof cmp === 'function') {
        this._cmp = cmp;
      } else {
        this._cmp = function(a, b) {
          if (Array.isArray(a) && Array.isArray(b)) {
            for (var i = 0; i < Math.min(a.length, b.length); i++) {
              if (a[i] !== b[i]) return a[i] < b[i] ? -1 : 1;
            }
            return a.length - b.length;
          }
          return a < b ? -1 : (a > b ? 1 : 0);
        };
      }
    }
    __PriorityQueue.prototype._siftUp = function(i) {
      while (i > 0) {
        var p = (i - 1) >> 1;
        if (this._cmp(this._heap[i], this._heap[p]) < 0) {
          var tmp = this._heap[i]; this._heap[i] = this._heap[p]; this._heap[p] = tmp;
          i = p;
        } else break;
      }
    };
    __PriorityQueue.prototype._siftDown = function(i) {
      var n = this._heap.length;
      while (true) {
        var best = i, l = 2 * i + 1, r = 2 * i + 2;
        if (l < n && this._cmp(this._heap[l], this._heap[best]) < 0) best = l;
        if (r < n && this._cmp(this._heap[r], this._heap[best]) < 0) best = r;
        if (best === i) break;
        var tmp = this._heap[i]; this._heap[i] = this._heap[best]; this._heap[best] = tmp;
        i = best;
      }
    };
    __PriorityQueue.prototype.offer = function(x) { this._heap.push(x); this._siftUp(this._heap.length - 1); return true; };
    __PriorityQueue.prototype.add = __PriorityQueue.prototype.offer;
    __PriorityQueue.prototype.poll = function() {
      if (this._heap.length === 0) return null;
      var top = this._heap[0];
      var last = this._heap.pop();
      if (this._heap.length > 0) { this._heap[0] = last; this._siftDown(0); }
      return top;
    };
    __PriorityQueue.prototype.remove = __PriorityQueue.prototype.poll;
    __PriorityQueue.prototype.peek = function() { return this._heap.length ? this._heap[0] : null; };
    __PriorityQueue.prototype.isEmpty = function() { return this._heap.length === 0; };
    __PriorityQueue.prototype.isNotEmpty = function() { return this._heap.length > 0; };
    Object.defineProperty(__PriorityQueue.prototype, 'size', { get: function() { return this._heap.length; } });

    function __StringBuilder() { this._parts = []; }
    __StringBuilder.prototype.append = function(val) { this._parts.push(String(val)); return this; };
    __StringBuilder.prototype.toString = function() { return this._parts.join(''); };
    __StringBuilder.prototype.reverse = function() {
      var s = this._parts.join('').split('').reverse().join('');
      this._parts = [s];
      return this;
    };
    __StringBuilder.prototype.deleteCharAt = function(idx) {
      var s = this._parts.join('');
      this._parts = [s.slice(0, idx) + s.slice(idx + 1)];
      return this;
    };
    Object.defineProperty(__StringBuilder.prototype, 'length', {
      get: function() { return this._parts.join('').length; }
    });
    Object.defineProperty(__StringBuilder.prototype, 'lastIndex', {
      get: function() { return this._parts.join('').length - 1; }
    });

    function buildString(fn) {
      var sb = new __StringBuilder();
      fn.call(sb);
      return sb.toString();
    }

    function IntArray(size, initOrFn) {
      var arr = new Array(size);
      for (var i = 0; i < size; i++) {
        arr[i] = typeof initOrFn === 'function' ? initOrFn(i) : (initOrFn === undefined ? 0 : initOrFn);
      }
      if (size === 128 || size === 256) {
        return new Proxy(arr, {
          get: function(target, prop) {
            if (typeof prop === 'string' && prop.length === 1 && !/^\d$/.test(prop) && !(prop in target)) {
              return target[prop.charCodeAt(0)];
            }
            return target[prop];
          },
          set: function(target, prop, value) {
            if (typeof prop === 'string' && prop.length === 1 && !/^\d$/.test(prop)) {
              target[prop.charCodeAt(0)] = value;
            } else {
              target[prop] = value;
            }
            return true;
          }
        });
      }
      return arr;
    }

    function BooleanArray(size, initOrFn) {
      var arr = new Array(size);
      for (var i = 0; i < size; i++) {
        arr[i] = typeof initOrFn === 'function' ? initOrFn(i) : (initOrFn === undefined ? false : initOrFn);
      }
      return arr;
    }

    function CharArray(size, initOrFn) {
      var arr = new Array(size);
      for (var i = 0; i < size; i++) {
        arr[i] = typeof initOrFn === 'function' ? initOrFn(i) : (initOrFn === undefined ? '\0' : initOrFn);
      }
      return arr;
    }

    function DoubleArray(size, initOrFn) {
      return IntArray(size, initOrFn);
    }

    function __ArrayInit(size, initOrFn) {
      var arr = new Array(size);
      for (var i = 0; i < size; i++) {
        arr[i] = typeof initOrFn === 'function' ? initOrFn(i) : initOrFn;
      }
      return arr;
    }

    // Attach convenient Kotlin/Java Array.prototype helpers without breaking standard enumeration
    var __arrProto = Object.getPrototypeOf([]);
    var __strProto = Object.getPrototypeOf('');
    function __addProto(proto, name, fn) {
      if (!proto[name]) {
        Object.defineProperty(proto, name, { value: fn, enumerable: false, configurable: true, writable: true });
      }
    }
    __addProto(__arrProto, 'add', function(idxOrVal, val) {
      if (arguments.length === 2 && typeof idxOrVal === 'number') { this.splice(idxOrVal, 0, val); }
      else { this.push(idxOrVal); }
      return true;
    });
    __addProto(__arrProto, 'get', function(i) { return this[i]; });
    __addProto(__arrProto, 'set', function(i, v) { this[i] = v; return v; });
    __addProto(__arrProto, 'removeAt', function(i) { return this.splice(i, 1)[0]; });
    __addProto(__arrProto, 'remove', function(idxOrObj) {
      if (typeof idxOrObj === 'number') return this.splice(idxOrObj, 1)[0];
      var i = this.indexOf(idxOrObj);
      return i !== -1 ? (this.splice(i, 1), true) : false;
    });
    __addProto(__arrProto, 'removeLast', function() { return this.pop(); });
    __addProto(__arrProto, 'removeFirst', function() { return this.shift(); });
    __addProto(__arrProto, 'isEmpty', function() { return this.length === 0; });
    __addProto(__arrProto, 'isNotEmpty', function() { return this.length > 0; });
    __addProto(__arrProto, 'contains', function(x) { return this.indexOf(x) !== -1; });
    __addProto(__arrProto, 'first', function() { return this[0]; });
    __addProto(__arrProto, 'last', function() { return this[this.length - 1]; });
    __addProto(__arrProto, 'sum', function() { return this.reduce(function(a, b) { return a + b; }, 0); });
    __addProto(__arrProto, 'maxOrNull', function() { return this.length ? Math.max.apply(null, this) : null; });
    __addProto(__arrProto, 'minOrNull', function() { return this.length ? Math.min.apply(null, this) : null; });
    __addProto(__arrProto, 'toIntArray', function() { return this.slice(); });
    __addProto(__arrProto, 'toHashSet', function() { return new __HashSet(this); });
    __addProto(__arrProto, 'toSet', function() { return new __HashSet(this); });
    __addProto(__arrProto, 'toTypedArray', function() { return this.slice(); });
    __addProto(__arrProto, 'toArray', function() { return this.slice(); });
    __addProto(__arrProto, 'toList', function() { return this.slice(); });
    __addProto(__arrProto, 'getKey', function() { return this[0]; });
    __addProto(__arrProto, 'getValue', function() { return this[1]; });
    __addProto(__arrProto, 'count', function(val) {
      var c = 0;
      for (var i = 0; i < this.length; i++) if (this[i] === val) c++;
      return c;
    });
    __addProto(__arrProto, 'joinToString', function(sep) { return this.join(sep === undefined ? ', ' : sep); });
    __addProto(__arrProto, 'sortWith', function(cmp) { return this.sort(cmp); });
    __addProto(__arrProto, 'sorted', function() { return this.slice().sort(function(a, b) { return a - b; }); });
    __addProto(__arrProto, 'reversed', function() { return this.slice().reverse(); });
    __addProto(__arrProto, 'reversedArray', function() { return this.slice().reverse(); });
    __addProto(__arrProto, 'copyOf', function() { return this.slice(); });
    __addProto(__arrProto, 'clear', function() { this.length = 0; });
    if (!Object.getOwnPropertyDescriptor(__arrProto, 'size')) {
      Object.defineProperty(__arrProto, 'size', { get: function() { return this.length; }, enumerable: false, configurable: true });
    }
    if (!Object.getOwnPropertyDescriptor(__arrProto, 'lastIndex')) {
      Object.defineProperty(__arrProto, 'lastIndex', { get: function() { return this.length - 1; }, enumerable: false, configurable: true });
    }
    if (!Object.getOwnPropertyDescriptor(__arrProto, 'indices')) {
      Object.defineProperty(__arrProto, 'indices', {
        get: function() { var r = []; for (var i = 0; i < this.length; i++) r.push(i); return r; },
        enumerable: false, configurable: true
      });
    }
    __addProto(__strProto, 'isEmpty', function() { return this.length === 0; });
    __addProto(__strProto, 'isNotEmpty', function() { return this.length > 0; });
    __addProto(__strProto, 'reversed', function() { return this.split('').reverse().join(''); });
    __addProto(__strProto, 'find', function(sub, start) { return this.indexOf(sub, start || 0); });
    if (!Object.getOwnPropertyDescriptor(__strProto, 'lastIndex')) {
      Object.defineProperty(__strProto, 'lastIndex', { get: function() { return this.length - 1; }, enumerable: false, configurable: true });
    }
    if (!Object.getOwnPropertyDescriptor(__strProto, 'indices')) {
      Object.defineProperty(__strProto, 'indices', {
        get: function() { var r = []; for (var i = 0; i < this.length; i++) r.push(i); return r; },
        enumerable: false, configurable: true
      });
    }

    var __Character = {
      isLetterOrDigit: function(c) { return /[a-zA-Z0-9]/.test(String(c)); },
      isDigit: function(c) { return /[0-9]/.test(String(c)); },
      isLetter: function(c) { return /[a-zA-Z]/.test(String(c)); },
      toLowerCase: function(c) { return String(c).toLowerCase(); },
      toUpperCase: function(c) { return String(c).toUpperCase(); },
      getNumericValue: function(c) { return parseInt(c, 10); }
    };

    var __Arrays = {
      sort: function(a, cmp) { return __sort(a, cmp); },
      fill: function(a, v) { if (Array.isArray(a)) a.fill(v); },
      asList: function() { return Array.prototype.slice.call(arguments); },
      copyOf: function(a, len) { return a.slice(0, len); },
      toString: function(a) { return JSON.stringify(a); }
    };

    var __Collections = {
      sort: function(a, cmp) { return __sort(a, cmp); },
      reverse: function(a) { return a.reverse(); },
      reverseOrder: function() { return { __reverse: true }; },
      singletonList: function(x) { return [x]; },
      max: function(a) { return Math.max.apply(null, __iter(a)); },
      min: function(a) { return Math.min.apply(null, __iter(a)); }
    };

    // Python runtime helpers
    function __py_idx(obj, idx) {
      if (obj == null) return undefined;
      if ((Array.isArray(obj) || typeof obj === 'string') && typeof idx === 'number' && idx < 0) {
        return obj[obj.length + idx];
      }
      if (obj instanceof __HashMap || (obj && obj._map instanceof Map)) {
        return obj.get(idx);
      }
      return obj[idx];
    }

    function __py_setIdx(obj, idx, val) {
      if ((Array.isArray(obj)) && typeof idx === 'number' && idx < 0) {
        obj[obj.length + idx] = val;
      } else if (obj && obj._map instanceof Map) {
        obj.put(idx, val);
      } else {
        obj[idx] = val;
      }
      return val;
    }

    function __py_slice(obj, start, stop, step) {
      var len = obj.length;
      if (step === undefined || step === null) step = 1;
      if (step === -1 && start === null && stop === null) {
        return typeof obj === 'string' ? obj.split('').reverse().join('') : obj.slice().reverse();
      }
      var s = start === null || start === undefined ? (step > 0 ? 0 : len - 1) : (start < 0 ? Math.max(0, len + start) : Math.min(len, start));
      var e = stop === null || stop === undefined ? (step > 0 ? len : -1) : (stop < 0 ? Math.max(-1, len + stop) : Math.min(len, stop));
      if (step === 1) return obj.slice(s, e);
      var res = [];
      if (step > 0) { for (var i = s; i < e; i += step) res.push(obj[i]); }
      else { for (var j = s; j > e; j += step) res.push(obj[j]); }
      return typeof obj === 'string' ? res.join('') : res;
    }

    function __py_in(item, col) {
      if (col == null) return false;
      if (typeof col === 'string') return col.indexOf(item) !== -1;
      if (Array.isArray(col)) {
        if (Array.isArray(item)) {
          var s = JSON.stringify(item);
          return col.some(function(x) { return JSON.stringify(x) === s; });
        }
        return col.indexOf(item) !== -1;
      }
      if (col._set instanceof Set) return col.contains(item);
      if (col._map instanceof Map) return col.has(item);
      if (col instanceof Set) return col.has(item);
      if (col instanceof Map) return col.has(item);
      return item in col;
    }

    function __py_range(a, b, step) {
      var start = b === undefined ? 0 : a;
      var stop = b === undefined ? a : b;
      var st = step === undefined ? 1 : step;
      var res = [];
      if (st > 0) { for (var i = start; i < stop; i += st) res.push(i); }
      else if (st < 0) { for (var j = start; j > stop; j += st) res.push(j); }
      return res;
    }

    function __py_len(x) {
      if (x == null) return 0;
      if (typeof x.length === 'number') return x.length;
      if (typeof x.size === 'number') return x.size;
      if (x._map instanceof Map) return x._map.size;
      if (x._set instanceof Set) return x._set.size;
      if (typeof x === 'object') return Object.keys(x).length;
      return 0;
    }

    function __py_enumerate(col, start) {
      var arr = __iter(col);
      var s = start || 0;
      return arr.map(function(v, i) { return [i + s, v]; });
    }

    function __py_zip() {
      var arrays = Array.prototype.slice.call(arguments).map(__iter);
      if (arrays.length === 0) return [];
      var minLen = Math.min.apply(null, arrays.map(function(a) { return a.length; }));
      var res = [];
      for (var i = 0; i < minLen; i++) {
        res.push(arrays.map(function(a) { return a[i]; }));
      }
      return res;
    }

    function __py_sorted(col, keyFn, rev) {
      var arr = (typeof col === 'string' ? col.split('') : __iter(col).slice());
      arr.sort(function(a, b) {
        var ka = keyFn ? keyFn(a) : a;
        var kb = keyFn ? keyFn(b) : b;
        var c = 0;
        if (Array.isArray(ka) && Array.isArray(kb)) {
          for (var i = 0; i < Math.min(ka.length, kb.length); i++) {
            if (ka[i] !== kb[i]) { c = ka[i] < kb[i] ? -1 : 1; break; }
          }
          if (c === 0) c = ka.length - kb.length;
        } else {
          c = ka < kb ? -1 : (ka > kb ? 1 : 0);
        }
        return rev ? -c : c;
      });
      return arr;
    }

    function __py_Counter(col) {
      var m = new __HashMap();
      var arr = __iter(col);
      for (var i = 0; i < arr.length; i++) {
        m.put(arr[i], m.getOrDefault(arr[i], 0) + 1);
      }
      return m;
    }

    function __py_defaultdict(factory) {
      var m = new Map();
      return new Proxy(m, {
        get: function(target, prop) {
          if (prop === '_map') return target;
          if (prop === 'keys') return function() { return Array.from(target.keys()); };
          if (prop === 'values') return function() { return Array.from(target.values()); };
          if (prop === 'items') return function() { return Array.from(target.entries()); };
          if (prop === 'get') return function(k, d) { var nk = Array.isArray(k) ? JSON.stringify(k) : k; return target.has(nk) ? target.get(nk) : d; };
          if (prop === 'has') return function(k) { var nk = Array.isArray(k) ? JSON.stringify(k) : k; return target.has(nk); };
          if (prop === 'put' || prop === 'set') return function(k, v) { var nk = Array.isArray(k) ? JSON.stringify(k) : k; target.set(nk, v); return v; };
          if (prop === 'size') return target.size;
          var nk = Array.isArray(prop) ? JSON.stringify(prop) : prop;
          if (!target.has(nk)) {
            var def = factory === Array ? [] : (factory === Number ? 0 : (factory === Set ? new __HashSet() : (typeof factory === 'function' ? factory() : 0)));
            target.set(nk, def);
          }
          return target.get(nk);
        },
        set: function(target, prop, val) {
          var nk = Array.isArray(prop) ? JSON.stringify(prop) : prop;
          target.set(nk, val);
          return true;
        }
      });
    }

    var __heapq = {
      heappush: function(arr, item) {
        arr.push(item);
        var i = arr.length - 1;
        while (i > 0) {
          var p = (i - 1) >> 1;
          if (__heapq._lt(arr[i], arr[p])) {
            var t = arr[i]; arr[i] = arr[p]; arr[p] = t; i = p;
          } else break;
        }
      },
      heappop: function(arr) {
        var top = arr[0];
        var last = arr.pop();
        if (arr.length > 0) {
          arr[0] = last;
          __heapq._siftDown(arr, 0);
        }
        return top;
      },
      heapify: function(arr) {
        for (var i = (arr.length >> 1) - 1; i >= 0; i--) __heapq._siftDown(arr, i);
      },
      _lt: function(a, b) {
        if (Array.isArray(a) && Array.isArray(b)) {
          for (var i = 0; i < Math.min(a.length, b.length); i++) {
            if (a[i] !== b[i]) return a[i] < b[i];
          }
          return a.length < b.length;
        }
        return a < b;
      },
      _siftDown: function(arr, i) {
        var n = arr.length;
        while (true) {
          var best = i, l = 2 * i + 1, r = 2 * i + 2;
          if (l < n && __heapq._lt(arr[l], arr[best])) best = l;
          if (r < n && __heapq._lt(arr[r], arr[best])) best = r;
          if (best === i) break;
          var t = arr[i]; arr[i] = arr[best]; arr[best] = t; i = best;
        }
      }
    };

    var __bisect = {
      bisect_left: function(arr, x) {
        var l = 0, r = arr.length;
        while (l < r) {
          var m = (l + r) >> 1;
          var vm = Array.isArray(arr[m]) ? arr[m][0] : arr[m];
          var vx = Array.isArray(x) ? x[0] : x;
          if (vm < vx) l = m + 1; else r = m;
        }
        return l;
      },
      bisect_right: function(arr, x) {
        var l = 0, r = arr.length;
        while (l < r) {
          var m = (l + r) >> 1;
          var vm = Array.isArray(arr[m]) ? arr[m][0] : arr[m];
          var vx = Array.isArray(x) ? x[0] : x;
          if (vm <= vx) l = m + 1; else r = m;
        }
        return l;
      }
    };

    return {
      __sub: __sub, __sort: __sort, __iter: __iter,
      __HashSet: __HashSet, __HashMap: __HashMap, __TreeMap: __TreeMap,
      __ArrayList: __ArrayList, __ArrayDeque: __ArrayDeque, __PriorityQueue: __PriorityQueue,
      __listOf: function() { return Array.prototype.slice.call(arguments); },
      __setOf: function() { return new __HashSet(Array.prototype.slice.call(arguments)); },
      __mapOf: function() { return new __HashMap(); },
      arrayOfNulls: function(n) { return new Array(n).fill(null); },
      __StringBuilder: __StringBuilder, buildString: buildString,
      IntArray: IntArray, BooleanArray: BooleanArray, CharArray: CharArray, DoubleArray: DoubleArray,
      __ArrayInit: __ArrayInit, __Pair: __Pair, __Triple: __Triple,
      __Character: __Character, __Arrays: __Arrays, __Collections: __Collections,
      ListNode: ListNode, TreeNode: TreeNode, GraphNode: GraphNode,
      ord: __ord, chr: __chr,
      tuple: function(arr) { return Array.isArray(arr) ? JSON.stringify(arr) : String(arr); },
      float: function(x) { return (x === 'inf' || x === Infinity) ? 1e15 : ((x === '-inf' || x === -Infinity) ? -1e15 : Number(x)); },
      __py_set: function(x) { return new __HashSet(x); },
      __py_bitand: function(a, b) {
        if (a && a._set instanceof Set && b && b._set instanceof Set) {
          var res = new __HashSet();
          var items = __iter(a);
          for (var i = 0; i < items.length; i++) {
            if (b.contains(items[i])) res.add(items[i]);
          }
          return res;
        }
        return a & b;
      },
      __py_max: function() {
        if (arguments.length === 1) {
          var x = arguments[0];
          return Array.isArray(x) ? Math.max.apply(null, x) : Math.max.apply(null, __iter(x));
        }
        return Math.max.apply(null, arguments);
      },
      __py_min: function() {
        if (arguments.length === 1) {
          var x = arguments[0];
          return Array.isArray(x) ? Math.min.apply(null, x) : Math.min.apply(null, __iter(x));
        }
        return Math.min.apply(null, arguments);
      },
      __py_mul: function(a, b) {
        if (typeof a === 'string') return a.repeat(Math.max(0, b | 0));
        if (typeof b === 'string') return b.repeat(Math.max(0, a | 0));
        return a * b;
      },
      __py_sum: function(x) { var a = __iter(x), s = 0; for (var i = 0; i < a.length; i++) s += a[i]; return s; },
      __py_any: function(x) { return __iter(x).some(Boolean); },
      __py_all: function(x) { return __iter(x).every(Boolean); },
      __py_reversed: function(x) { var a = __iter(x); return (typeof a === 'string' ? a.split('') : a.slice()).reverse(); },
      __py_values: function(x) { if (!x) return []; return typeof x.values === 'function' ? x.values() : Object.values(x); },
      __py_keys: function(x) { if (!x) return []; return typeof x.keys === 'function' ? x.keys() : Object.keys(x); },
      __py_items: function(x) { if (!x) return []; return typeof x.entries === 'function' ? x.entries() : Object.entries(x); },
      __py_get: function(obj, k, d) {
        if (!obj) return d;
        if (typeof obj.get === 'function') return obj.get.length >= 2 ? obj.get(k, d) : (obj.has && obj.has(k) ? obj.get(k) : d);
        var nk = Array.isArray(k) ? JSON.stringify(k) : k;
        return Object.prototype.hasOwnProperty.call(obj, nk) ? obj[nk] : (d === undefined ? null : d);
      },
      __py_pop: function(obj, k, d) {
        if (!obj) return d;
        if (Array.isArray(obj)) return k === undefined ? obj.pop() : obj.splice(k, 1)[0];
        if (typeof obj.remove === 'function') return arguments.length >= 3 ? obj.remove(k, d) : obj.remove(k);
        var nk = Array.isArray(k) ? JSON.stringify(k) : k;
        if (!Object.prototype.hasOwnProperty.call(obj, nk)) return arguments.length >= 3 ? d : undefined;
        var v = obj[nk];
        delete obj[nk];
        return v;
      },
      Random: (function() {
        function R() { if (!(this instanceof R)) return new R(); }
        R.nextInt = function(n) { return Math.floor(Math.random() * n); };
        R.prototype.nextInt = R.nextInt;
        return R;
      })(),
      System: { currentTimeMillis: function() { return Date.now(); } },
      __random: {
        choice: function(a) { return a[Math.floor(Math.random() * a.length)]; },
        randint: function(a, b) { return a + Math.floor(Math.random() * (b - a + 1)); }
      },
      __py_idx: __py_idx, __py_setIdx: __py_setIdx, __py_slice: __py_slice,
      __py_in: __py_in, __py_range: __py_range, __py_len: __py_len,
      __py_enumerate: __py_enumerate, __py_zip: __py_zip, __py_sorted: __py_sorted,
      __py_Counter: __py_Counter, __py_defaultdict: __py_defaultdict,
      __heapq: __heapq, __bisect: __bisect,
      __py_bool: function(x) {
        if (x === null || x === undefined || x === false || x === 0 || x === '') return false;
        if (Array.isArray(x)) return x.length > 0;
        if (x._items) return x._items.length > 0;
        if (x._set) return x._set.size > 0;
        if (x._map) return x._map.size > 0;
        if (typeof x === 'object' && x.constructor === Object) return Object.keys(x).length > 0;
        return Boolean(x);
      },
      __charAdd: function(ch, offset) {
        if (typeof ch === 'string' && ch.length === 1 && typeof offset === 'number') {
          return String.fromCharCode(ch.charCodeAt(0) + offset);
        }
        return ch + offset;
      },
      IllegalArgumentException: function(m) { return new Error(m || 'IllegalArgumentException'); },
      RuntimeException: function(m) { return new Error(m || 'RuntimeException'); }
    };
  };

  // =========================================================================
  // LITERAL MASKING & BALANCED BRACE HELPERS
  // =========================================================================
  function maskStrings(code, isPython) {
    var literals = [];
    var out = '';
    var i = 0;
    while (i < code.length) {
      var ch = code[i];
      if (!isPython && ch === '/' && code[i + 1] === '/') {
        while (i < code.length && code[i] !== '\n') i++;
        continue;
      }
      if (!isPython && ch === '/' && code[i + 1] === '*') {
        i += 2;
        while (i < code.length && !(code[i] === '*' && code[i + 1] === '/')) i++;
        i += 2;
        continue;
      }
      if (isPython && ch === '#') {
        while (i < code.length && code[i] !== '\n') i++;
        continue;
      }
      if (isPython && (code.slice(i, i + 3) === '"""' || code.slice(i, i + 3) === "'''")) {
        var q3 = code.slice(i, i + 3);
        var j3 = i + 3;
        while (j3 < code.length && code.slice(j3, j3 + 3) !== q3) {
          if (code[j3] === '\\') j3 += 2; else j3++;
        }
        var raw3 = code.slice(i + 3, j3);
        var id3 = '__STR_' + literals.length + '__';
        literals.push(JSON.stringify(raw3));
        out += id3;
        i = j3 + 3;
        continue;
      }
      if (isPython && (ch === 'f' || ch === 'F') && (code[i + 1] === '"' || code[i + 1] === "'") && (i === 0 || !/\w/.test(code[i - 1]))) {
        var fq = code[i + 1];
        var fj = i + 2;
        while (fj < code.length && code[fj] !== fq) {
          if (code[fj] === '\\') fj += 2; else fj++;
        }
        var rawF = code.slice(i + 2, fj);
        var fParts = [], fi = 0, litBuf = '';
        while (fi < rawF.length) {
          if (rawF[fi] === '{') {
            if (rawF[fi + 1] === '{') { litBuf += '{'; fi += 2; continue; }
            if (litBuf) {
              literals.push(JSON.stringify(litBuf));
              fParts.push('__STR_' + (literals.length - 1) + '__');
              litBuf = '';
            }
            var fk = fi + 1, fDepth = 1;
            while (fk < rawF.length && fDepth > 0) {
              if (rawF[fk] === '{') fDepth++;
              else if (rawF[fk] === '}') fDepth--;
              fk++;
            }
            fParts.push('String(' + rawF.slice(fi + 1, fk - 1) + ')');
            fi = fk;
          } else {
            litBuf += rawF[fi++];
          }
        }
        if (litBuf || fParts.length === 0) {
          literals.push(JSON.stringify(litBuf));
          fParts.push('__STR_' + (literals.length - 1) + '__');
        }
        out += fParts.length === 1 ? fParts[0] : ('(' + fParts.join(' + ') + ')');
        i = fj + 1;
        continue;
      }
      if (ch === '"' || ch === "'") {
        var q = ch;
        var j = i + 1;
        while (j < code.length && code[j] !== q) {
          if (code[j] === '\\') j += 2; else j++;
        }
        var raw = code.slice(i + 1, j);
        var id = '__STR_' + literals.length + '__';
        // Convert Python single-quoted or normal string safely
        try {
          literals.push(JSON.stringify(eval('"' + raw.replace(/\\'/g, "'").replace(/"/g, '\\"').replace(/\n/g, '\\n') + '"')));
        } catch (e) {
          literals.push(JSON.stringify(raw));
        }
        out += id;
        i = j + 1;
        continue;
      }
      out += ch;
      i++;
    }
    return { code: out, literals: literals };
  }

  function unmaskStrings(code, literals) {
    return code.replace(/__STR_(\d+)__/g, function(_, idx) {
      return literals[Number(idx)];
    });
  }

  function findMatchingBrace(str, openIdx) {
    var depth = 1, i = openIdx + 1;
    while (depth > 0 && i < str.length) {
      if (str[i] === '{') depth++;
      else if (str[i] === '}') depth--;
      i++;
    }
    return i - 1;
  }

  // =========================================================================
  // JVM (KOTLIN + JAVA) TRANSPILER
  // =========================================================================
  function stripGenericTypes(s) {
    // Remove Type<...> or builderOf<...> argument lists (never comparison < ... > across operators/statements)
    var prev;
    do {
      prev = s;
      s = s.replace(/\b([A-Z]\w*|arrayOfNulls|hashSetOf|mutableSetOf|setOf|mutableMapOf|hashMapOf|mutableListOf|listOf|emptyList)<([A-Za-z0-9_.,\s\[\]?*]*)>/g, '$1');
    } while (s !== prev);
    return s;
  }

  function cleanJvmBackticks(s) {
    return s.replace(/\.`val`/g, '.val').replace(/`val`/g, 'val_').replace(/`in`/g, 'in_');
  }

  function parseParamNames(paramStr) {
    var params = [];
    if (!paramStr || !paramStr.trim()) return params;
    var parts = stripGenericTypes(paramStr).split(',');
    for (var i = 0; i < parts.length; i++) {
      var p = cleanJvmBackticks(parts[i].trim());
      if (!p) continue;
      p = p.split('=')[0].trim();
      p = p.replace(/^(?:(?:private|public|protected|override|final|vararg)\s+|(?:val|var|final)\s+)*/, '');
      if (p.indexOf(':') !== -1) {
        params.push(p.split(':')[0].trim());
      } else {
        var words = p.split(/\s+/);
        params.push(words[words.length - 1].trim());
      }
    }
    return params;
  }

  function findMatchingParen(str, openIdx) {
    var depth = 1, i = openIdx + 1;
    while (depth > 0 && i < str.length) {
      if (str[i] === '(') depth++;
      else if (str[i] === ')') depth--;
      i++;
    }
    return i - 1;
  }

  function transformJVMBody(js, isOOPClass, definedClasses) {
    definedClasses = definedClasses || {};
    // Kotlin/Java type casts & non-null assertions
    js = cleanJvmBackticks(js);
    js = js.replace(/!!/g, '');
    js = js.replace(/@Synchronized\b/g, '');
    js = js.replace(/@Suppress\([^)]*\)/g, '');
    js = js.replace(/\b(\d+)L\b/g, '$1');

    // Optional chaining assignment `a?.b = rhs` -> `if (a != null) { a.b = (rhs); }`
    js = js.replace(/^(\s*)([a-zA-Z_][\w.\[\]]*(?:\?\.[a-zA-Z_]\w*)*)\?\.([a-zA-Z_]\w*)\s*=(?!=)\s*(.+?);?\s*$/gm,
      '$1if ($2 != null) { $2.$3 = ($4); }');

    // Elvis with control flow (`?: return ...`, `?: continue`, `?: break`, `?: throw ...`)
    var elvisCounter = 0;
    js = js.replace(/^(\s*)((?:(?:private|public|protected|final)\s+)?(?:(?:val|var)\s+|[\w<>\[\]]+\s+)?\w+(?:\s*:\s*[^=]+)?\s*=|[\w.\[\]]+\s*=)\s*(.+?)\s*\?:\s*((?:return|continue|break|throw)\b[^;\n]*);?\s*$/gm,
      function(_, ind, lhs, rhs, action) {
        var tmp = '__elvis' + (elvisCounter++);
        return ind + 'var ' + tmp + ' = ' + rhs + '; if (' + tmp + ' == null) { ' + action + '; } ' + lhs + ' ' + tmp + ';';
      });
    js = js.replace(/\?:/g, '??');

    // Preserve explicit double division before stripping casts
    js = js.replace(/\.toDouble\(\)/g, ' * 1.0');
    js = js.replace(/\(\s*double\s*\)/g, '1.0 * ');
    js = js.replace(/\(\s*char\s*\)\s*\(/g, 'String.fromCharCode(');
    js = js.replace(/\(\s*(?:int|long)\s*\)\s*/g, '');

    // Java `new int[] { a, b }` or `new int[][] { ... }`
    js = js.replace(/\bnew\s+(?:int|long|double|boolean|char|String)\s*(?:\[\s*\])+\s*\{([\s\S]*?)\}/g, function(_, inner) {
      return '[' + inner.replace(/\{([^{}]*)\}/g, '[$1]') + ']';
    });

    // Kotlin `when (...) { ... }` or `when { ... }` using balanced braces
    var wMatch;
    while ((wMatch = js.match(/\bwhen\s*(?:\(([^)]+)\))?\s*\{/)) !== null) {
      var wStart = wMatch.index;
      var wOpen = wStart + wMatch[0].length - 1;
      var wClose = findMatchingBrace(js, wOpen);
      var expr = wMatch[1] ? wMatch[1].trim() : null;
      var wBody = js.slice(wOpen + 1, wClose);
      var out = [];
      var lines = wBody.split('\n');
      var branchIdx = 0;
      for (var li = 0; li < lines.length; li++) {
        var ln = lines[li].trim();
        if (!ln) continue;
        var arrowIdx = ln.indexOf('->');
        if (arrowIdx === -1) { out.push(ln); continue; }
        var cond = ln.slice(0, arrowIdx).trim();
        var act = ln.slice(arrowIdx + 2).trim();
        if (cond === 'else') {
          out.push(act.startsWith('{') ? 'else ' + act : 'else { ' + act + ' }');
        } else {
          var test = expr
            ? cond.split(',').map(function(c) { return '(' + expr + ') === ' + c.trim(); }).join(' || ')
            : cond;
          var prefix = (branchIdx === 0 ? 'if (' : 'else if (') + test + ') ';
          out.push(act.startsWith('{') ? prefix + act : prefix + '{ ' + act + ' }');
        }
        branchIdx++;
      }
      js = js.slice(0, wStart) + out.join('\n') + js.slice(wClose + 1);
    }

    // Kotlin `if (cond) a else b` single-line expression (after `=`, `return`, `,`, `(`)
    js = js.replace(/(\breturn[ \t]+|=[ \t]*)if\s*\(([^()]+)\)\s*([^\n{};]+?)\s+else\s+([^\n{};]+)/g, '$1($2 ? $3 : $4)');

    // Multi-line `return if (cond) { ... } else if ... { ... } else { ... }` (used in 0073, 0074, 0096 KT)
    function injectBranchReturn(branchBody) {
      var trimmed = branchBody.trim();
      if (!trimmed) return branchBody;
      var linesB = trimmed.split('\n');
      var last = linesB[linesB.length - 1].trim();
      if (last && !/^(?:return\b|throw\b|\})/.test(last) && !/=[^=]/.test(last)) {
        linesB[linesB.length - 1] = 'return ' + last + ';';
      }
      return linesB.join('\n');
    }
    var mIfRet;
    while ((mIfRet = js.match(/\breturn[ \t]+(?:if|else[ \t]+if)\s*\(/)) !== null) {
      var retPos = mIfRet.index;
      var cur = retPos + mIfRet[0].indexOf('if');
      var chainParts = [];
      var okChain = true;
      while (cur < js.length) {
        if (js.slice(cur).match(/^if\s*\(/)) {
          var opP = js.indexOf('(', cur);
          var clP = findMatchingParen(js, opP);
          var condTxt = js.slice(opP + 1, clP);
          var afterP = js.slice(clP + 1).match(/^\s*\{/);
          if (!afterP) { okChain = false; break; }
          var opB = clP + 1 + afterP[0].length - 1;
          var clB = findMatchingBrace(js, opB);
          var bodyTxt = injectBranchReturn(js.slice(opB + 1, clB));
          chainParts.push((chainParts.length ? 'else if (' : 'if (') + condTxt + ') {\n' + bodyTxt + '\n}');
          cur = clB + 1;
          var nextElse = js.slice(cur).match(/^\s*else\s+/);
          if (nextElse) { cur += nextElse[0].length; continue; }
          break;
        } else if (js.slice(cur).match(/^\{/)) {
          var clElse = findMatchingBrace(js, cur);
          var elseTxt = injectBranchReturn(js.slice(cur + 1, clElse));
          chainParts.push('else {\n' + elseTxt + '\n}');
          cur = clElse + 1;
          break;
        } else {
          okChain = false; break;
        }
      }
      if (!okChain || chainParts.length === 0) break;
      js = js.slice(0, retPos) + chainParts.join(' ') + js.slice(cur);
    }

    // Strip nested generic types EARLY so PriorityQueue<Pair<Int, Int>>, TrieNode, etc. match cleanly
    js = stripGenericTypes(js);

    // Java 2D array literal `= {{0, 1}, {1, 0}}`
    js = js.replace(/=\s*\{\s*(\{[^{}]*\}(?:\s*,\s*\{[^{}]*\})*)\s*\}/g, function(_, inner) {
      return '= [' + inner.replace(/\{([^{}]*)\}/g, '[$1]') + ']';
    });

    // Kotlin PriorityQueue { a, b -> ... } (must run BEFORE general arrow lambda scanner!)
    var pqM;
    while ((pqM = js.match(/(?:new\s+)?(?:java\.util\.)?\bPriorityQueue\s*(?:\(\s*\))?\s*\{/)) !== null) {
      var pqOpen = pqM.index + pqM[0].length - 1;
      var pqClose = findMatchingBrace(js, pqOpen);
      var pqInner = js.slice(pqOpen + 1, pqClose).trim();
      var pqArr = pqInner.match(/^\s*(\w+)\s*,\s*(\w+)\s*->\s*([\s\S]+)$/);
      var pqRep;
      if (pqArr) {
        var bLines = pqArr[3].trim().split('\n');
        var lastLine = bLines[bLines.length - 1].trim();
        if (lastLine && !/^return\b/.test(lastLine)) bLines[bLines.length - 1] = 'return ' + lastLine;
        pqRep = 'new __PriorityQueue(function(' + pqArr[1] + ', ' + pqArr[2] + ') {\n' + bLines.join('\n') + '\n})';
      } else {
        pqRep = 'new __PriorityQueue(function(a, b) { ' + pqInner + ' })';
      }
      js = js.slice(0, pqM.index) + pqRep + js.slice(pqClose + 1);
    }

    // Kotlin compareBy { it... } / compareByDescending { it... }
    var cbM;
    while ((cbM = js.match(/\b(compareBy|compareByDescending)\s*\{/)) !== null) {
      var cbOpen = cbM.index + cbM[0].length - 1;
      var cbClose = findMatchingBrace(js, cbOpen);
      var cbInner = js.slice(cbOpen + 1, cbClose).trim();
      var cbP = 'it', cbKey = cbInner;
      var cbArr = cbInner.match(/^(\w+)\s*->\s*([\s\S]+)$/);
      if (cbArr) { cbP = cbArr[1]; cbKey = cbArr[2].trim(); }
      var ka = cbKey.replace(new RegExp('\\b' + cbP + '\\b', 'g'), '__a');
      var kb = cbKey.replace(new RegExp('\\b' + cbP + '\\b', 'g'), '__b');
      var isDesc = cbM[1] === 'compareByDescending';
      var cmpJs = 'function(__a, __b) { var __ka = ' + ka + ', __kb = ' + kb + '; return ' +
        (isDesc ? '(__ka > __kb ? -1 : (__ka < __kb ? 1 : 0))' : '(__ka < __kb ? -1 : (__ka > __kb ? 1 : 0))') + '; }';
      js = js.slice(0, cbM.index) + cmpJs + js.slice(cbClose + 1);
    }

    // Kotlin `.sortWith { a, b -> ... }` and `.filter { ... }` and `.computeIfAbsent(k) { ... }` and `.getOrPut(k) { ... }`
    var swM;
    while ((swM = js.match(/\.sortWith\s*\{/)) !== null) {
      var swOpen = swM.index + swM[0].length - 1;
      var swClose = findMatchingBrace(js, swOpen);
      var swInner = js.slice(swOpen + 1, swClose).trim();
      var swArr = swInner.match(/^\s*(\w+)\s*,\s*(\w+)\s*->\s*([\s\S]+)$/);
      var swRep = swArr
        ? '.sort(function(' + swArr[1] + ', ' + swArr[2] + ') { return (' + swArr[3].trim() + '); })'
        : '.sort(function(a, b) { return (' + swInner + '); })';
      js = js.slice(0, swM.index) + swRep + js.slice(swClose + 1);
    }
    var fltM;
    while ((fltM = js.match(/\.filter\s*\{/)) !== null) {
      var flOpen = fltM.index + fltM[0].length - 1;
      var flClose = findMatchingBrace(js, flOpen);
      var flInner = js.slice(flOpen + 1, flClose).trim();
      var flP = 'it', flB = flInner;
      var flArr = flInner.match(/^(\w+)\s*->\s*([\s\S]+)$/);
      if (flArr) { flP = flArr[1]; flB = flArr[2].trim(); }
      js = js.slice(0, fltM.index) + '.filter(function(' + flP + ') { return (' + flB + '); })' + js.slice(flClose + 1);
    }
    js = js.replace(/(\w+)\.computeIfAbsent\(([^)]+)\)\s*\{\s*([^}]+)\s*\}/g, '$1.computeIfAbsent($2, function() { return $3; })');
    js = js.replace(/(\w+)\.getOrPut\(([^)]+)\)\s*\{\s*([^}]+)\s*\}/g, '$1.getOrPut($2, function() { return $3; })');

    // Kotlin `.forEach { ... }` and `.maxOfOrNull / .minOfOrNull { ... }` (balanced braces)
    var feM;
    while ((feM = js.match(/\.forEach\s*\{/)) !== null) {
      var feOpen = feM.index + feM[0].length - 1;
      var feClose = findMatchingBrace(js, feOpen);
      var feInner = js.slice(feOpen + 1, feClose).trim();
      var feP = 'it', feB = feInner;
      var feArr = feInner.match(/^(?:\(\s*([^)]+)\s*\)|(\w+))\s*->\s*([\s\S]+)$/);
      if (feArr) { feP = feArr[1] || feArr[2]; feB = feArr[3].trim(); }
      js = js.slice(0, feM.index) + '.forEach(function(' + feP + ') {\n' + feB + '\n})' + js.slice(feClose + 1);
    }
    var mxM;
    while ((mxM = js.match(/\.(maxOfOrNull|minOfOrNull)\s*\{/)) !== null) {
      var mxOpen = mxM.index + mxM[0].length - 1;
      var mxClose = findMatchingBrace(js, mxOpen);
      var mxInner = js.slice(mxOpen + 1, mxClose).trim();
      var mxP = 'it', mxB = mxInner;
      var mxArr = mxInner.match(/^(\w+)\s*->\s*([\s\S]+)$/);
      if (mxArr) { mxP = mxArr[1]; mxB = mxArr[2].trim(); }
      var isMax = mxM[1] === 'maxOfOrNull';
      var mxRep = '.reduce(function(__acc, ' + mxP + '){ var __v = (' + mxB + '); return (__acc === null || ' + (isMax ? '__v > __acc' : '__v < __acc') + ') ? __v : __acc; }, null)';
      js = js.slice(0, mxM.index) + mxRep + js.slice(mxClose + 1);
    }

    // Balanced lambda rewriter for Java `(a, b) -> ...` and `k -> ...` inside calls
    function scanLambdaExprEnd(str, startIdx) {
      var p = 0, b = 0, c = 0, k = startIdx;
      while (k < str.length) {
        var ch = str[k];
        if (ch === '(') p++;
        else if (ch === ')') { if (p === 0) break; p--; }
        else if (ch === '[') b++;
        else if (ch === ']') { if (b === 0) break; b--; }
        else if (ch === '{') c++;
        else if (ch === '}') { if (c === 0) break; c--; }
        else if (p === 0 && b === 0 && c === 0 && (ch === ',' || ch === ';' || ch === '\n')) break;
        k++;
      }
      return k;
    }
    js = js.replace(/\(\s*(\w+)\s*,\s*(\w+)\s*\)\s*->\s*\{/g, 'function($1, $2) {');
    var lamIdx = 0;
    while (true) {
      var restL = js.slice(lamIdx);
      var lM = restL.match(/(?:^|[,(]\s*)(?:\(\s*(\w+)\s*,\s*(\w+)\s*\)|(\w+))\s*->\s*/);
      if (!lM) break;
      var fullStart = lamIdx + lM.index;
      var arrowPrefixLen = lM[0].indexOf(lM[1] || lM[3]);
      if (lM[0].indexOf('(') !== -1 && (lM[0][0] === ',' || lM[0][0] === '(')) {
        var leadSep = (lM[0][0] === ',' || lM[0][0] === '(') ? lM[0][0] : '';
        arrowPrefixLen = leadSep ? 1 : 0;
        while (arrowPrefixLen < lM[0].length && /\s/.test(lM[0][arrowPrefixLen])) arrowPrefixLen++;
      }
      var lamStart = fullStart + arrowPrefixLen;
      var bodyStart = fullStart + lM[0].length;
      var paramsStr = lM[1] ? (lM[1] + ', ' + lM[2]) : lM[3];
      if (js[bodyStart] === '{') {
        var cbLam = findMatchingBrace(js, bodyStart);
        var repBlock = 'function(' + paramsStr + ') ' + js.slice(bodyStart, cbLam + 1);
        js = js.slice(0, lamStart) + repBlock + js.slice(cbLam + 1);
        lamIdx = lamStart + repBlock.length;
      } else {
        var exprEnd = scanLambdaExprEnd(js, bodyStart);
        var exprBody = js.slice(bodyStart, exprEnd).trim();
        var repExpr = 'function(' + paramsStr + ') { return (' + exprBody + '); }';
        js = js.slice(0, lamStart) + repExpr + js.slice(exprEnd);
        lamIdx = lamStart + repExpr.length;
      }
    }

    js = js.replace(/Collections\.reverseOrder\(\)/g, '__Collections.reverseOrder()');
    js = js.replace(/Collections\.singletonList\(/g, '__Collections.singletonList(');

    // Kotlin `?.let { ... }` and `.let { ... }` (ASI-safe `;` before IIFE)
    var letM;
    while ((letM = js.match(/(\??\.let)\s*\{/)) !== null) {
      var lDot = letM.index;
      var lOpen = lDot + letM[0].length - 1;
      var lClose = findMatchingBrace(js, lOpen);
      var lBody = js.slice(lOpen + 1, lClose).trim();
      var lParam = 'it', lStmt = lBody;
      var lArr = lBody.match(/^(\w+)\s*->\s*([\s\S]+)$/);
      if (lArr) { lParam = lArr[1]; lStmt = lArr[2].trim(); }
      var lrStart = lDot - 1, lpD = 0, lbD = 0;
      while (lrStart >= 0) {
        var chL = js[lrStart];
        if (chL === ')') lpD++;
        else if (chL === '(') { if (lpD === 0) break; lpD--; }
        else if (chL === ']') lbD++;
        else if (chL === '[') { if (lbD === 0) break; lbD--; }
        else if (lpD === 0 && lbD === 0 && !/[\w.$?]/.test(chL)) break;
        lrStart--;
      }
      if (lrStart < 0 || !/[\w.$?]/.test(js[lrStart])) lrStart++;
      var lRecv = js.slice(lrStart, lDot);
      var isSafe = letM[1][0] === '?';
      var beforeTrim = js.slice(0, lrStart).replace(/\s+$/, '');
      var needSemi = beforeTrim.length > 0 && !/[=(\[,:;?{}\n]$|\breturn$/.test(beforeTrim) && js.slice(beforeTrim.length, lrStart).indexOf('\n') !== -1;
      var letJs = (needSemi ? ';' : '') + '(function(' + lParam + '){ ' + (isSafe ? ('if (' + lParam + ' != null) { ' + lStmt + ' }') : lStmt) + ' })(' + lRecv + ')';
      js = js.slice(0, lrStart) + letJs + js.slice(lClose + 1);
    }

    // Kotlin `repeat(n) { ... }`
    var repM;
    while ((repM = js.match(/\brepeat\s*\(/)) !== null) {
      var rOpenP = repM.index + repM[0].length - 1;
      var rCloseP = findMatchingParen(js, rOpenP);
      var rCount = js.slice(rOpenP + 1, rCloseP).trim();
      var afterR = js.slice(rCloseP + 1);
      var brMatch = afterR.match(/^\s*\{/);
      if (!brMatch) break;
      var rOpenB = rCloseP + 1 + brMatch[0].length - 1;
      var rCloseB = findMatchingBrace(js, rOpenB);
      var rBody = js.slice(rOpenB + 1, rCloseB);
      var rLoop = 'for (var __ri = 0; __ri < (' + rCount + '); __ri++) {' + rBody + '}';
      js = js.slice(0, repM.index) + rLoop + js.slice(rCloseB + 1);
    }

    // Kotlin `.sortBy / .sortByDescending / .sortedBy / .sortedByDescending`
    var sbMatch;
    while ((sbMatch = js.match(/\.(sortBy|sortByDescending|sortedBy|sortedByDescending)\s*\{/)) !== null) {
      var sbStart = sbMatch.index;
      var kind = sbMatch[1];
      var sbOpen = sbStart + sbMatch[0].length - 1;
      var sbClose = findMatchingBrace(js, sbOpen);
      var sbInner = js.slice(sbOpen + 1, sbClose).trim();
      var pVar = 'it', keyExpr = sbInner;
      var arrowM = sbInner.match(/^(\w+)\s*->\s*([\s\S]+)$/);
      if (arrowM) { pVar = arrowM[1]; keyExpr = arrowM[2].trim(); }
      var keyA = keyExpr.replace(new RegExp('\\b' + pVar + '\\b', 'g'), '__a');
      var keyB = keyExpr.replace(new RegExp('\\b' + pVar + '\\b', 'g'), '__b');
      var desc = kind.indexOf('Descending') !== -1;
      var pre = kind.indexOf('sorted') === 0 ? '.slice()' : '';
      var cmpBody = desc
        ? 'return __ka > __kb ? -1 : (__ka < __kb ? 1 : 0);'
        : 'return __ka < __kb ? -1 : (__ka > __kb ? 1 : 0);';
      var rep = pre + '.sort(function(__a, __b) { var __ka = ' + keyA + ', __kb = ' + keyB + '; ' + cmpBody + ' })';
      js = js.slice(0, sbStart) + rep + js.slice(sbClose + 1);
    }

    // Remaining `PriorityQueue(...)` calls
    js = js.replace(/(?:new\s+)?(?:java\.util\.)?\bPriorityQueue\s*\(/g, 'new __PriorityQueue(');

    // Balanced `.toArray(...)` removal BEFORE `new int[...]` expansion
    var taIdx = js.indexOf('.toArray(');
    while (taIdx !== -1) {
      var taOpen = taIdx + '.toArray('.length - 1;
      var taClose = findMatchingParen(js, taOpen);
      js = js.slice(0, taOpen + 1) + ')' + js.slice(taClose + 1);
      taIdx = js.indexOf('.toArray(', taOpen + 2);
    }

    // Collection constructors (variadic helpers support arbitrary nesting)
    js = js.replace(/\b(?:hashSetOf|mutableSetOf|setOf)\s*\(/g, '__setOf(');
    js = js.replace(/(?:new\s+)?\b(?:HashSet|TreeSet|LinkedHashSet)\s*\(([^)]*)\)/g, 'new __HashSet($1)');

    js = js.replace(/\b(?:mutableMapOf|hashMapOf)\s*\(\)/g, 'new __HashMap()');
    js = js.replace(/(?:new\s+)?\b(?:HashMap|TreeMap|LinkedHashMap|ConcurrentHashMap|ConcurrentSkipListMap)\s*\(([^)]*)\)/g, 'new __HashMap($1)');

    js = js.replace(/(?:new\s+)?\b(?:java\.util\.)?(?:ArrayDeque|Stack|LinkedList)\s*\(([^)]*)\)/g, 'new __ArrayDeque($1)');

    js = js.replace(/(?:new\s+)?\bArrayList\s*\(([^)]*)\)/g, '__ArrayList($1)');
    js = js.replace(/\b(?:mutableListOf|listOf|intArrayOf|arrayOf|charArrayOf)\s*\(/g, '__listOf(');
    js = js.replace(/\bemptyList\s*\(\)/g, '[]');

    // Kotlin Array(n) { init }, arrayOfNulls(n), IntArray(n) { init }
    js = js.replace(/\barrayOfNulls\s*\(([^)]+)\)/g, 'new Array($1).fill(null)');
    js = js.replace(/\bArray\s*\(([^)]+)\)\s*\{\s*([^}]+)\s*\}/g, function(_, sz, body) {
      var fnBody = body.indexOf('->') !== -1 ? body.replace(/^\s*(\w+)\s*->/, 'function($1) { return ') + '; }' : 'function(it) { return ' + body + '; }';
      return '__ArrayInit(' + sz + ', ' + fnBody + ')';
    });
    js = js.replace(/\bIntArray\(([^)]+)\)\s*\{\s*([^}]+)\s*\}/g, 'IntArray($1, function(it) { return $2; })');
    js = js.replace(/\bBooleanArray\(([^)]+)\)\s*\{\s*([^}]+)\s*\}/g, 'BooleanArray($1, function(it) { return $2; })');
    js = js.replace(/\bIntArray\(([^)]+)\)/g, 'IntArray($1)');
    js = js.replace(/\bBooleanArray\(([^)]+)\)/g, 'BooleanArray($1)');
    js = js.replace(/\bCharArray\(([^)]+)\)/g, 'CharArray($1)');
    js = js.replace(/\bDoubleArray\(([^)]+)\)/g, 'DoubleArray($1)');
    js = js.replace(/new\s+int\[([^\]]+)\]\[\]/g, '__ArrayInit($1, function() { return []; })');
    js = js.replace(/new\s+int\[([^\]]+)\]\[([^\]]+)\]/g, '__ArrayInit($1, function() { return IntArray($2); })');
    js = js.replace(/new\s+boolean\[([^\]]+)\]\[([^\]]+)\]/g, '__ArrayInit($1, function() { return BooleanArray($2); })');
    js = js.replace(/new\s+char\[([^\]]+)\]\[([^\]]+)\]/g, '__ArrayInit($1, function() { return CharArray($2); })');
    js = js.replace(/new\s+int\[([^\]]+)\]/g, 'IntArray($1)');
    js = js.replace(/new\s+boolean\[([^\]]+)\]/g, 'BooleanArray($1)');
    js = js.replace(/new\s+char\[([^\]]+)\]/g, 'CharArray($1)');
    js = js.replace(/new\s+double\[([^\]]+)\]/g, 'DoubleArray($1)');
    js = js.replace(/new\s+String\[([^\]]+)\]/g, 'new Array($1)');
    js = js.replace(/\bnew\s+[A-Za-z_]\w*\s*\[([^\]]+)\](?:\[\])*/g, 'new Array($1).fill(null)');

    // StringBuilder & Pair & Triple & Nodes
    js = js.replace(/new\s+StringBuilder\([^)]*\)/g, 'new __StringBuilder()');
    js = js.replace(/\bStringBuilder\([^)]*\)/g, 'new __StringBuilder()');
    js = js.replace(/\bPair\(([^,]+),\s*([^)]+)\)/g, '__Pair($1, $2)');
    js = js.replace(/\bTriple\(([^,]+),\s*([^,]+),\s*([^)]+)\)/g, '__Triple($1, $2, $3)');
    js = js.replace(/\bnew\s+ListNode\b/g, 'new ListNode');
    js = js.replace(/\bListNode\(/g, 'new ListNode(');
    js = js.replace(/new\s+new\s+ListNode/g, 'new ListNode');
    js = js.replace(/\bnew\s+TreeNode\b/g, 'new TreeNode');
    js = js.replace(/\bTreeNode\(/g, 'new TreeNode(');
    js = js.replace(/new\s+new\s+TreeNode/g, 'new TreeNode');
    if (!definedClasses['Node']) {
      js = js.replace(/\bnew\s+Node\b/g, 'new GraphNode');
      js = js.replace(/(^|[^.])\bNode\(/g, '$1new GraphNode(');
      js = js.replace(/new\s+new\s+GraphNode/g, 'new GraphNode');
    }

    // Kotlin `.apply { ... }` (e.g. `new ListNode(0).apply { next = head }`)
    var appM;
    while ((appM = js.match(/\.apply\s*\{/)) !== null) {
      var aDot = appM.index;
      var aOpen = aDot + appM[0].length - 1;
      var aClose = findMatchingBrace(js, aOpen);
      var aBody = js.slice(aOpen + 1, aClose).trim();
      // Walk backward to grab receiver
      var rStart = aDot - 1, pDepth = 0, bDepth = 0;
      while (rStart >= 0) {
        var ch = js[rStart];
        if (ch === ')') pDepth++;
        else if (ch === '(') { if (pDepth === 0) break; pDepth--; }
        else if (ch === ']') bDepth++;
        else if (ch === '[') { if (bDepth === 0) break; bDepth--; }
        else if (pDepth === 0 && bDepth === 0 && !/[\w.$]/.test(ch)) {
          if (ch === ' ' && rStart >= 3 && js.slice(rStart - 3, rStart + 1) === 'new ') {
            rStart -= 3; break;
          }
          break;
        }
        rStart--;
      }
      if (rStart < 0 || !/[\w.$new]/.test(js[rStart])) rStart++;
      var recv = js.slice(rStart, aDot);
      var prefixBody = aBody.replace(/(^|[;\n]\s*)(\w+)\s*=(?!=)/g, '$1__it.$2 =');
      js = js.slice(0, rStart) + '(function(__it){ ' + prefixBody + '; return __it; })(' + recv + ')' + js.slice(aClose + 1);
    }

    // Numeric constants & casts
    js = js.replace(/\b(?:Int|Integer)\.MAX_VALUE\b/g, '2147483647');
    js = js.replace(/\b(?:Int|Integer)\.MIN_VALUE\b/g, '-2147483648');
    js = js.replace(/\b(?:Long|Double)\.MAX_VALUE\b/g, '1e15');
    js = js.replace(/\b(?:Long|Double)\.MIN_VALUE\b/g, '-1e15');
    js = js.replace(/\.toInt\(\)/g, '|0');
    js = js.replace(/\.toLong\(\)/g, '');
    js = js.replace(/\bCharacter\.isLetterOrDigit\(/g, '__Character.isLetterOrDigit(');
    js = js.replace(/\bCharacter\.isDigit\(/g, '__Character.isDigit(');
    js = js.replace(/\bCharacter\.toLowerCase\(/g, '__Character.toLowerCase(');
    js = js.replace(/([\w.\[\]]+)\.isLetterOrDigit\(\)/g, '__Character.isLetterOrDigit($1)');
    js = js.replace(/([\w.\[\]]+)\.isDigit\(\)/g, '__Character.isDigit($1)');
    js = js.replace(/([\w.\[\]]+)\.lowercaseChar\(\)/g, '__Character.toLowerCase($1)');
    js = js.replace(/([\w.\[\]]+)\.digitToInt\(\)/g, 'parseInt($1, 10)');
    js = js.replace(/([a-zA-Z_]\w*(?:\.[a-zA-Z_]\w*|\[[^\[\]]+\])*)\.code\b/g, 'ord($1)');
    js = js.replace(/\bInteger\.parseInt\(/g, 'parseInt(');
    js = js.replace(/\bInteger\.compare\(([^,]+),\s*([^)]+)\)/g, '(($1) < ($2) ? -1 : (($1) > ($2) ? 1 : 0))');
    js = js.replace(/([\w.\[\]]+)\.compareTo\(([^)]+)\)/g, '(($1) < ($2) ? -1 : (($1) > ($2) ? 1 : 0))');
    js = js.replace(/\bString\.valueOf\(/g, 'String(');

    // Integer halving & division
    js = js.replace(/(\(\s*(?:right|r|high|len)\s*-\s*(?:left|l|low|1)\s*\))\s*\/\s*2\b/g, 'Math.floor($1 / 2)');
    js = js.replace(/\(\s*((?:left|l|low)\s*\+\s*(?:right|r|high))\s*\)\s*\/\s*2\b/g, 'Math.floor(($1) / 2)');
    js = js.replace(/\(\s*(p\s*\+\s*k\s*-\s*1)\s*\)\s*\/\s*k\b/g, 'Math.floor(($1) / k)');
    js = js.replace(/\b(mid|len)\s*\/\s*(\w+)/g, 'Math.floor($1 / $2)');
    js = js.replace(/\b(sum|total|carry|rem)\s*\/\s*10\b/g, 'Math.floor($1 / 10)');
    js = js.replace(/(\w+)\s*\/=\s*10\b/g, '$1 = Math.floor($1 / 10)');
    js = js.replace(/(\w+)\s*\/=\s*(\w+)/g, '$1 = Math.trunc($1 / $2)');
    js = js.replace(/\b(curr|lastNum)\s*\/\s*(\bnum\b|\bcurr\b)/g, 'Math.trunc($1 / $2)');

    // Strings & Arrays
    js = js.replace(/String\(([^)]+)\.toCharArray\(\)\.apply\s*\{\s*sort\(\)\s*\}\)/g, '$1.split("").sort().join("")');
    js = js.replace(/(\w+)\.toCharArray\(\)\.sorted\(\)\.joinToString\([^)]*\)/g, '$1.split("").sort().join("")');
    js = js.replace(/new\s+String\(([^)]+)\)/g, '(Array.isArray($1) ? $1.join("") : String($1))');
    js = js.replace(/(\w+)\.toCharArray\(\)/g, '$1.split("")');
    js = js.replace(/Arrays\.sort\(/g, '__Arrays.sort(');
    js = js.replace(/Arrays\.fill\(/g, '__Arrays.fill(');
    js = js.replace(/Arrays\.asList\(/g, '__Arrays.asList(');
    js = js.replace(/Collections\.sort\(/g, '__Collections.sort(');
    js = js.replace(/(\w+)\.sort\(\)/g, '__sort($1)');
    js = js.replace(/(\w+(?:\.charAt\([^)]+\)|\[[^\[\]]+\])?)\s*-\s*(__STR_\d+__)/g, '__sub($1, $2)');
    js = js.replace(/(__STR_\d+__)\s*\+\s*\(([^)]+)\)/g, '__charAdd($1, ($2))');
    js = js.replace(/s\.indexOf\((__STR_\d+__),\s*startIndex\s*=\s*([^)]+)\)/g, 's.indexOf($1, $2)');

    // Kotlin destructuring `val (a, b) = ...`
    js = js.replace(/\b(?:val|var)\s*\(([^)]+)\)\s*=/g, 'var [$1] =');

    // Java/Kotlin variable declarations (safe against `.val` and `? val :`)
    js = js.replace(/\b(?:private|public|protected|final)\s+/g, '');
    var extraClasses = Object.keys(definedClasses).length ? ('|' + Object.keys(definedClasses).join('|')) : '';
    var typePattern = '(?:val|var|boolean|int|long|double|float|char|String|Integer|Boolean|Double|ListNode|TreeNode|Node|List|ArrayList|Map|HashMap|TreeMap|LinkedHashMap|NavigableMap|ConcurrentHashMap|ConcurrentSkipListMap|Set|HashSet|TreeSet|LinkedHashSet|Deque|ArrayDeque|Queue|PriorityQueue|Stack|StringBuilder|ValueEntry|Map\\.Entry|Random' + extraClasses + ')(?:\\[\\])*';
    js = js.replace(new RegExp('(^|[^\\w.])' + typePattern + '\\s+(\\w+)\\s*=', 'g'), '$1var $2 =');
    js = js.replace(new RegExp('(^|[^\\w.])' + typePattern + '\\s+(\\w+)\\s*;', 'g'), '$1var $2;');
    js = js.replace(/(^|[^\w.])(?:val|var)\s+(\w+)\s*:\s*[^=;\n]+(?=[=;\n])/g, '$1var $2 ');
    js = js.replace(/(^|[^\w.])(?:val|var)\s+(?=(?:[a-zA-Z_]\w*\s*[:=]|[(]))/g, '$1var ');

    // Balanced-paren `for (...)` rewriting
    var fIdx = 0;
    while (true) {
      var fMatch = js.slice(fIdx).match(/\bfor\s*\(/);
      if (!fMatch) break;
      var absStart = fIdx + fMatch.index;
      var openP = absStart + fMatch[0].length - 1;
      var closeP = findMatchingParen(js, openP);
      var header = js.slice(openP + 1, closeP).trim();
      var replHeader = null;

      var mWithIdx = header.match(/^\(([^)]+)\)\s+in\s+([\s\S]+?)\.withIndex\(\)$/);
      var mDestruct = !mWithIdx && header.match(/^\(([^)]+)\)\s+in\s+([\s\S]+)$/);
      var mIn = !mWithIdx && !mDestruct && header.match(/^(?:var\s+|val\s+)?(\w+)\s+in\s+([\s\S]+)$/);
      var mColon = !mWithIdx && !mDestruct && !mIn && header.match(/^(?:[\w.<>\[\]]+\s+)?(\w+)\s*:\s*([\s\S]+)$/);
      var mCStyle = !mWithIdx && !mDestruct && !mIn && !mColon && header.match(/^(?:int|long|var)\s+(\w+\s*=[\s\S]+)$/);

      if (mWithIdx) {
        var ps = mWithIdx[1].split(','), col = mWithIdx[2].trim();
        var idxV = ps[0].trim(), valV = ps[1].trim();
        replHeader = 'for (var [' + idxV + ', ' + valV + '] of __iter(' + col + '.map(function(__x, __i){ return [__i, __x]; })))';
      } else if (mDestruct) {
        replHeader = 'for (var [' + mDestruct[1].trim() + '] of __iter(' + mDestruct[2].trim() + '))';
      } else if (mIn) {
        var v = mIn[1], rhs = mIn[2].trim();
        var stepM = rhs.match(/^([\s\S]+?)\s+step\s+(\S+)$/);
        var stepVal = stepM ? stepM[2].trim() : null;
        var rangeExpr = stepM ? stepM[1].trim() : rhs;
        if (/\buntil\b/.test(rangeExpr)) {
          var u = rangeExpr.split(/\buntil\b/);
          replHeader = 'for (var ' + v + ' = ' + u[0].trim() + '; ' + v + ' < ' + u[1].trim() + '; ' + v + (stepVal ? ' += ' + stepVal : '++') + ')';
        } else if (/\bdownTo\b/.test(rangeExpr)) {
          var d = rangeExpr.split(/\bdownTo\b/);
          replHeader = 'for (var ' + v + ' = ' + d[0].trim() + '; ' + v + ' >= ' + d[1].trim() + '; ' + v + (stepVal ? ' -= ' + stepVal : '--') + ')';
        } else if (rangeExpr.indexOf('..') !== -1) {
          var dots = rangeExpr.split('..');
          replHeader = 'for (var ' + v + ' = ' + dots[0].trim() + '; ' + v + ' <= ' + dots[1].trim() + '; ' + v + (stepVal ? ' += ' + stepVal : '++') + ')';
        } else {
          replHeader = 'for (var ' + v + ' of __iter(' + rangeExpr + '))';
        }
      } else if (mColon) {
        replHeader = 'for (var ' + mColon[1] + ' of __iter(' + mColon[2].trim() + '))';
      } else if (mCStyle) {
        replHeader = 'for (var ' + mCStyle[1] + ')';
      }

      if (replHeader) {
        js = js.slice(0, absStart) + replHeader + js.slice(closeP + 1);
        fIdx = absStart + replHeader.length;
      } else {
        fIdx = closeP + 1;
      }
    }

    // Kotlin `buildString { ... }`
    var bsIdx = js.indexOf('buildString');
    while (bsIdx !== -1) {
      var openB = js.indexOf('{', bsIdx);
      if (openB === -1) break;
      var closeB = findMatchingBrace(js, openB);
      var inner = js.slice(openB + 1, closeB);
      var transformedInner = inner.replace(/(^|[^.\w])append\(/g, '$1sb.append(');
      js = js.slice(0, bsIdx) + '(function() { var sb = new __StringBuilder(); ' + transformedInner + '; return sb.toString(); })()' + js.slice(closeB + 1);
      bsIdx = js.indexOf('buildString');
    }

    // Nested Kotlin local functions
    js = js.replace(/\bfun\s+(\w+)\s*\(([^)]*)\)(?:\s*:\s*[^{]+)?\s*\{/g, function(_, fnName, rawParams) {
      return 'function ' + fnName + '(' + parseParamNames(rawParams).join(', ') + ') {';
    });

    // Kotlin range `in` / `!in` checks (`r !in 0 until m`, `x in 1..n`)
    js = js.replace(/(\b[\w.\[\]()]+)\s+!in\s+([\w.\[\]()\s+\-*]+?)\s+until\s+([\w.\[\]()+\-*]+?)(?=\s*(?:\|\||&&|\)|;|,|$))/g, '(($1) < ($2) || ($1) >= ($3))');
    js = js.replace(/(\b[\w.\[\]()]+)\s+in\s+([\w.\[\]()\s+\-*]+?)\s+until\s+([\w.\[\]()+\-*]+?)(?=\s*(?:\|\||&&|\)|;|,|$))/g, '(($1) >= ($2) && ($1) < ($3))');
    js = js.replace(/(\b[\w.\[\]()]+)\s+!in\s+([\w.\[\]()+\-*]+?)\s*\.\.\s*([\w.\[\]()+\-*]+?)(?=\s*(?:\|\||&&|\)|;|,|$))/g, '(($1) < ($2) || ($1) > ($3))');
    js = js.replace(/(\b[\w.\[\]()]+)\s+in\s+([\w.\[\]()+\-*]+?)\s*\.\.\s*([\w.\[\]()+\-*]+?)(?=\s*(?:\|\||&&|\)|;|,|$))/g, '(($1) >= ($2) && ($1) <= ($3))');

    // Kotlin `in` / `!in` checks outside `for (...)`
    js = js.replace(/(\b[\w.\[\]()]+)\s+!in\s+([\w.\[\]()]+)/g, '!__py_in($1, $2)');
    js = js.replace(/(\b[\w.\[\]()]+)\s+in\s+([\w.\[\]()]+)/g, '__py_in($1, $2)');

    // Math & property conversions
    js = js.replace(/\bmaxOf\(/g, 'Math.max(');
    js = js.replace(/\bminOf\(/g, 'Math.min(');
    js = js.replace(/\bMath\.abs\(/g, 'Math.abs(');
    js = js.replace(/\.values\b(?!\()/g, '.values()');
    js = js.replace(/\.keys\b(?!\()/g, '.keys()');
    js = js.replace(/\.size\(\)/g, '.size');
    js = js.replace(/\.length\(\)/g, '.length');
    js = js.replace(/([\w.\[\]]+)\.equals\(([^)]+)\)/g, '$1 === $2');
    js = js.replace(/\bsubstring\(/g, 'slice(');
    var jvmOp = '(?:\\([^()]+\\)|\\b[\\w.]+(?:\\[[^\\]]+\\])?)';
    js = js.replace(new RegExp('(' + jvmOp + ')\\s+xor\\s+(' + jvmOp + ')', 'g'), '(($1) ^ ($2))');
    js = js.replace(new RegExp('(' + jvmOp + ')\\s+and\\s+(' + jvmOp + ')', 'g'), '(($1) & ($2))');
    js = js.replace(new RegExp('(' + jvmOp + ')\\s+or\\s+(' + jvmOp + ')', 'g'), '(($1) | ($2))');
    js = js.replace(new RegExp('(' + jvmOp + ')\\s+shl\\s+(' + jvmOp + ')', 'g'), '(($1) << ($2))');
    js = js.replace(new RegExp('(' + jvmOp + ')\\s+shr\\s+(' + jvmOp + ')', 'g'), '(($1) >> ($2))');
    js = js.replace(new RegExp('(' + jvmOp + ')\\s+ushr\\s+(' + jvmOp + ')', 'g'), '(($1) >>> ($2))');

    // Infinite loop guard
    var guarded = 'var __ops = 0;\n';
    guarded += js.replace(/(for\s*\([^)]*\)\s*\{|while\s*\([^)]*\)\s*\{)/g,
      '$1 if (++__ops > 5000000) throw new Error("Time Limit Exceeded (>5M operations)"); ');
    return guarded;
  }

  LessonRuntime._extractMethod = function(code, methodName) {
    var masked = maskStrings(code, false);
    var clean = masked.code;
    var regex = new RegExp('(?:fun|def|public|private|static|[\\w<>\\[\\]]+)*\\s+' + methodName + '\\s*\\(([^)]*)\\)', 'm');
    var match = clean.match(regex);
    if (!match) {
      regex = new RegExp(methodName + '\\s*\\(([^)]*)\\)', 'm');
      match = clean.match(regex);
    }
    if (!match) return null;
    var paramStr = match[1];
    var afterHeaderIdx = match.index + match[0].length;
    var openBraceIdx = clean.indexOf('{', afterHeaderIdx);
    if (openBraceIdx === -1) return null;
    var endIdx = findMatchingBrace(clean, openBraceIdx);
    var body = clean.slice(openBraceIdx + 1, endIdx);
    return { params: parseParamNames(paramStr), body: body, literals: masked.literals };
  };

  function parseJvmClassMembers(clsName, clsBody, definedClasses) {
    var methods = [];
    var innerClassDefs = [];
    var fieldInitLines = [];
    var i = 0;

    function parseDefaults(paramStr) {
      var defs = [];
      if (!paramStr) return defs;
      var parts = paramStr.split(',');
      for (var k = 0; k < parts.length; k++) {
        var eqIdx = parts[k].indexOf('=');
        if (eqIdx !== -1) {
          var lhs = cleanJvmBackticks(parts[k].slice(0, eqIdx).trim())
            .replace(/^(?:(?:private|public|protected|override|final|vararg)\s+|(?:val|var|final)\s+)*/g, '')
            .split(':')[0].trim();
          var rhs = parts[k].slice(eqIdx + 1).trim();
          if (lhs) defs.push('if (' + lhs + ' === undefined) ' + lhs + ' = ' + rhs + ';');
        }
      }
      return defs;
    }

    while (i < clsBody.length) {
      while (i < clsBody.length && /\s/.test(clsBody[i])) i++;
      if (i >= clsBody.length) break;

      var rest = clsBody.slice(i);

      // Annotation line like @Suppress(...) or @Synchronized
      var mAnn = rest.match(/^@\w+(?:\([^)]*\))?/);
      if (mAnn) { i += mAnn[0].length; continue; }

      // Inner class `private data class Foo(...) { ... }` or `private static class Foo { ... }`
      var mInner = rest.match(/^(?:(?:private|public|protected|static|data|inner)\s+)*class\s+(\w+)(?:\s*\(([^)]*)\))?\s*(\{)?/);
      if (mInner) {
        var icName = mInner[1];
        definedClasses[icName] = true;
        var icParams = parseParamNames(mInner[2] || '');
        var icDefaults = parseDefaults(mInner[2] || '');
        var icBody = '';
        if (mInner[3] === '{') {
          var obI = i + mInner[0].length - 1;
          var cbI = findMatchingBrace(clsBody, obI);
          icBody = clsBody.slice(obI + 1, cbI);
          i = cbI + 1;
        } else {
          i += mInner[0].length;
        }
        var parsedInner = parseJvmClassMembers(icName, icBody, definedClasses);
        var icCtor = null;
        parsedInner.methods.forEach(function(m) {
          if (m.name === '__ctor' && (!icCtor || m.params.length >= icCtor.params.length)) icCtor = m;
        });
        if (icParams.length === 0 && icCtor) icParams = icCtor.params;
        var icOtherMeths = parsedInner.methods.filter(function(m) { return m.name !== '__ctor'; });
        var icInitJs = transformJVMBody(
          icDefaults.join('\n') + '\n' +
          (mInner[2] ? icParams.map(function(p) { return 'this.' + p + ' = ' + p + ';'; }).join('\n') + '\n' : '') +
          parsedInner.fieldInitLines.join('\n') + '\n' +
          (icCtor ? icCtor.body : ''),
          true, definedClasses
        );
        // Bind inner class fields onto `this.x` (skip shadowing constructor params!)
        var icFieldNames = [];
        parsedInner.fieldInitLines.forEach(function(fl) {
          var fm = stripGenericTypes(fl).replace(/^(?:(?:private|public|protected|final)\s+)+/, '').match(/^(?:val|var|[\w<>\[\]]+)\s+(\w+)/);
          if (fm) icFieldNames.push(fm[1]);
        });
        icInitJs = icInitJs.replace(/\bvar\s+(\w+)\s*(=|;)/g, function(full, vname, sep) {
          if (icFieldNames.indexOf(vname) !== -1) {
            if (icParams.indexOf(vname) !== -1 && sep === ';') return '';
            return sep === '=' ? ('this.' + vname + ' =') : ('this.' + vname + ' = undefined;');
          }
          return full;
        });
        var icFn = 'function ' + icName + '(' + icParams.join(', ') + ') {\n' +
          'if (!(this instanceof ' + icName + ')) return new ' + icName + '(' + icParams.join(', ') + ');\n' +
          'var self = this;\n' + icInitJs + '\n';
        icOtherMeths.forEach(function(m) {
          var mBody = transformJVMBody(m.defaults.join('\n') + '\n' + m.body, true, definedClasses);
          icFieldNames.concat(icParams).forEach(function(fn) {
            if (m.params.indexOf(fn) === -1) {
              mBody = mBody.replace(new RegExp('([^.\\w])\\b' + fn + '\\b', 'g'), '$1self.' + fn);
            }
          });
          icFn += 'this.' + m.name + ' = function(' + m.params.join(', ') + ') {\n' + mBody + '\n};\n';
        });
        icFn += '}';
        innerClassDefs.push(icFn);
        continue;
      }

      // Kotlin `fun` method (balanced-paren parameter extraction to support defaults like System.currentTimeMillis())
      var mKtHead = rest.match(/^(?:(?:private|public|protected|override|inline)\s+)*fun\s+(\w+)\s*\(/);
      if (mKtHead) {
        var mName = mKtHead[1];
        var pOpenIdx = i + mKtHead[0].length - 1;
        var pCloseIdx = findMatchingParen(clsBody, pOpenIdx);
        var rawParamStr = clsBody.slice(pOpenIdx + 1, pCloseIdx);
        var mParams = parseParamNames(rawParamStr), mDefaults = parseDefaults(rawParamStr);
        var afterParen = clsBody.slice(pCloseIdx + 1);
        var retSep = afterParen.match(/^(?:\s*:[^{\n=]+)?\s*(\{|=)/);
        if (retSep) {
          if (retSep[1] === '{') {
            var ob = pCloseIdx + 1 + retSep[0].length - 1;
            var cb = findMatchingBrace(clsBody, ob);
            methods.push({ name: mName, params: mParams, defaults: mDefaults, body: clsBody.slice(ob + 1, cb) });
            i = cb + 1; continue;
          } else {
            var eqPos = pCloseIdx + 1 + retSep[0].length;
            var eqEnd = clsBody.indexOf('\n', eqPos);
            if (eqEnd === -1) eqEnd = clsBody.length;
            var lineTail = clsBody.slice(eqPos, eqEnd).trim();
            if (lineTail.endsWith('{')) {
              var obEq = clsBody.indexOf('{', eqPos);
              var cbEq = findMatchingBrace(clsBody, obEq);
              methods.push({ name: mName, params: mParams, defaults: mDefaults, body: 'return ' + clsBody.slice(eqPos, cbEq + 1) + ';' });
              i = cbEq + 1; continue;
            } else {
              methods.push({ name: mName, params: mParams, defaults: mDefaults, body: 'return ' + lineTail + ';' });
              i = eqEnd + 1; continue;
            }
          }
        }
      }

      // Java constructor `ClassName(...) { ... }`
      var mCtor = rest.match(new RegExp('^(?:(?:private|public|protected)\\s+)?' + clsName + '\\s*\\(([^)]*)\\)\\s*\\{'));
      if (mCtor) {
        var obC = i + mCtor[0].length - 1;
        var cbC = findMatchingBrace(clsBody, obC);
        methods.push({ name: '__ctor', params: parseParamNames(mCtor[1]), defaults: [], body: clsBody.slice(obC + 1, cbC), rawParams: mCtor[1] });
        i = cbC + 1; continue;
      }

      // Java method `[visibility/static/synchronized] ReturnType methodName(params) { ... }`
      var mJa = rest.match(/^(?:(?:private|public|protected|static|final|synchronized)\s+)*([\w.\[\]]+(?:<(?:[^<>\n]|<[^<>\n]*>)*>)?(?:\[\])*)\s+(\w+)\s*\(([^)]*)\)\s*\{/);
      if (mJa && mJa[2] !== clsName && !/^(?:if|for|while|switch|catch)$/.test(mJa[1])) {
        var obJ = i + mJa[0].length - 1;
        var cbJ = findMatchingBrace(clsBody, obJ);
        methods.push({ name: mJa[2], params: parseParamNames(mJa[3]), defaults: [], body: clsBody.slice(obJ + 1, cbJ) });
        i = cbJ + 1; continue;
      }

      // Otherwise consume a class-level field statement/line (skipping any balanced braces cleanly)
      var lineEnd = i;
      var brDepth = 0;
      while (lineEnd < clsBody.length) {
        var ch = clsBody[lineEnd];
        if (ch === '{') brDepth++;
        else if (ch === '}') brDepth--;
        else if ((ch === '\n' || ch === ';') && brDepth <= 0) {
          if (ch === ';') lineEnd++;
          break;
        }
        lineEnd++;
      }
      var stmt = clsBody.slice(i, lineEnd).trim();
      if (stmt && stmt !== '{' && stmt !== '}' && !/^init\s*\{/.test(stmt)) {
        fieldInitLines.push(stmt);
      } else if (stmt && /^init\s*\{/.test(stmt)) {
        var obInit = stmt.indexOf('{'), cbInit = stmt.lastIndexOf('}');
        if (obInit !== -1 && cbInit > obInit) fieldInitLines.push(stmt.slice(obInit + 1, cbInit));
      }
      i = lineEnd + 1;
    }

    return { methods: methods, innerClassDefs: innerClassDefs, fieldInitLines: fieldInitLines };
  }

  LessonRuntime._transpileJVM = function(code, methodName) {
    var masked = maskStrings(code, false);
    var clean = cleanJvmBackticks(masked.code).replace(/^\s*(?:package|import)\s+.*$/gm, '');

    // Scan top-level classes; pick `Solution` if present else the LAST top-level class (helper classes appear first)
    var topClasses = [];
    var scanPos = 0;
    while (scanPos < clean.length) {
      var cm = clean.slice(scanPos).match(/\bclass\s+(\w+)(?:\s*\(([^)]*)\))?\s*\{/);
      if (!cm) break;
      var cOpen = scanPos + cm.index + cm[0].length - 1;
      var cClose = findMatchingBrace(clean, cOpen);
      topClasses.push({ name: cm[1], ctorParamsStr: cm[2] || '', body: clean.slice(cOpen + 1, cClose) });
      scanPos = cClose + 1;
    }

    var targetCls = topClasses.find(function(c) { return c.name === 'Solution'; }) || topClasses[topClasses.length - 1] || null;
    if (targetCls) {
      var clsName = targetCls.name;
      var ctorParamsStr = targetCls.ctorParamsStr;
      var clsBody = targetCls.body;

      var definedClasses = {};
      var parsedCls = parseJvmClassMembers(clsName, clsBody, definedClasses);
      var methods = parsedCls.methods;
      var innerClassDefs = parsedCls.innerClassDefs;
      var fieldInitLines = parsedCls.fieldInitLines;

      var hasCtor = Boolean(ctorParamsStr && ctorParamsStr.trim()) || methods.some(function(m) { return m.name === '__ctor'; });
      if ((clsName !== 'Solution' || hasCtor) && methods.length > 0) {
        var ctorMeth = methods.find(function(m) { return m.name === '__ctor'; });
        if (ctorMeth && ctorMeth.rawParams !== undefined) ctorParamsStr = ctorMeth.rawParams;
        var ctorParams = parseParamNames(ctorParamsStr);
        var fieldNames = [];
        fieldInitLines.forEach(function(fl) {
          var fm = stripGenericTypes(fl).replace(/^(?:(?:private|public|protected|final)\s+)+/, '').match(/^(?:val|var|[\w<>\[\]]+)\s+(\w+)/);
          if (fm) fieldNames.push(fm[1]);
        });
        var rawInit = fieldInitLines.join('\n') + '\n' + (ctorMeth ? ctorMeth.body : '');
        var initBody = innerClassDefs.join('\n') + '\n' + transformJVMBody(rawInit, true, definedClasses);
        fieldNames.forEach(function(fn) {
          if (ctorParams.indexOf(fn) === -1) {
            initBody = initBody.replace(new RegExp('\\bthis\\.' + fn + '\\b', 'g'), fn);
          } else {
            initBody = initBody.replace(new RegExp('\\bvar\\s+' + fn + '\\s*;', 'g'), '');
            initBody = initBody.replace(new RegExp('\\bthis\\.' + fn + '\\s*=\\s*' + fn + '\\b', 'g'), '');
          }
        });
        var jsReserved = { 'delete': '__m_delete', 'in': '__m_in' };
        var methodAliases = {};
        methods.filter(function(m) { return m.name !== '__ctor'; }).forEach(function(m) {
          if (jsReserved[m.name]) methodAliases[m.name] = jsReserved[m.name];
        });
        function fixReservedCalls(s) {
          Object.keys(methodAliases).forEach(function(origM) {
            s = s.replace(new RegExp('([^.\\w])\\b' + origM + '\\s*\\(', 'g'), '$1' + methodAliases[origM] + '(');
          });
          return s;
        }
        var jsClass = '(function() {\nfunction ' + clsName + '(' + ctorParams.join(', ') + ') {\n';
        jsClass += 'var self = this;\n' + fixReservedCalls(initBody) + '\n';
        methods.filter(function(m) { return m.name !== '__ctor'; }).forEach(function(m) {
          var mJs = transformJVMBody((m.defaults ? m.defaults.join('\n') + '\n' : '') + m.body, true, definedClasses);
          fieldNames.forEach(function(fn) {
            if (m.params.indexOf(fn) === -1) {
              mJs = mJs.replace(new RegExp('\\bthis\\.' + fn + '\\b', 'g'), fn);
            }
          });
          mJs = fixReservedCalls(mJs);
          var alias = methodAliases[m.name] || m.name;
          jsClass += 'this[' + JSON.stringify(m.name) + '] = function(' + m.params.join(', ') + ') {\n' + mJs + '\n};\n';
          jsClass += 'var ' + alias + ' = this[' + JSON.stringify(m.name) + '];\n';
        });
        jsClass += '}\nreturn ' + clsName + ';\n})()';
        return { isOOP: true, className: clsName, code: unmaskStrings(jsClass, masked.literals) };
      }

      if (methods.length > 0) {
        var mainMeth = methods.find(function(m) { return m.name === methodName; }) || methods[0];
        var fieldsJs = innerClassDefs.join('\n') + '\n' + transformJVMBody(fieldInitLines.join('\n'), false, definedClasses);
        var helpersJs = methods.filter(function(m) { return m !== mainMeth; }).map(function(m) {
          return 'function ' + m.name + '(' + m.params.join(', ') + ') {\n' + transformJVMBody(m.body, false, definedClasses) + '\n}';
        }).join('\n');
        var mainBodyJs = transformJVMBody(mainMeth.body, false, definedClasses);
        var fullFn = '(function __entry() {\n' + fieldsJs + '\n' + helpersJs + '\nfunction ' + mainMeth.name + '(' + mainMeth.params.join(', ') + ') {\n' + mainBodyJs + '\n}\nreturn ' + mainMeth.name + '.apply(this, arguments);\n})';
        return { isOOP: false, params: mainMeth.params, code: unmaskStrings(fullFn, masked.literals) };
      }
    }

    // Fallback: single-method extraction
    var parsed = LessonRuntime._extractMethod(code, methodName);
    var body = parsed ? parsed.body : clean;
    var params = parsed ? parsed.params : ['nums'];
    var fnCode = '(function ' + methodName + '(' + params.join(', ') + ') {\n' + transformJVMBody(body, false) + '\n})';
    return { isOOP: false, params: params, code: unmaskStrings(fnCode, parsed ? parsed.literals : masked.literals) };
  };

  // =========================================================================
  // PYTHON TRANSPILER (Indentation & Idiom Aware)
  // =========================================================================
  function convertPyTuples(expr) {
    var out = '';
    var i = 0;
    while (i < expr.length) {
      if (expr[i] === '(') {
        var prevCh = i > 0 ? expr[i - 1] : '';
        var isCall = /[\w.]/.test(prevCh);
        // Find matching ')'
        var depth = 1, j = i + 1, hasComma = false, hasTernary = false;
        while (j < expr.length && depth > 0) {
          if (expr[j] === '(') depth++;
          else if (expr[j] === ')') depth--;
          else if (depth === 1 && expr[j] === ',') hasComma = true;
          else if (depth === 1 && expr[j] === '?') hasTernary = true;
          j++;
        }
        if (depth === 0) {
          var inner = convertPyTuples(expr.slice(i + 1, j - 1));
          if (!isCall && hasComma && !hasTernary && !/^function\b/.test(inner.trim())) {
            out += '[' + inner + ']';
          } else {
            out += '(' + inner + ')';
          }
          i = j;
          continue;
        }
      }
      out += expr[i];
      i++;
    }
    return out;
  }

  function transformPyExpr(expr, helperClasses) {
    helperClasses = helperClasses || {};
    var e = expr;
    e = e.replace(/\bTrue\b/g, 'true').replace(/\bFalse\b/g, 'false').replace(/\bNone\b/g, 'null');
    e = e.replace(/\bis\s+not\b/g, '!==').replace(/\bis\b/g, '===');

    e = convertPyTuples(e);

    // Array multiplication `[x] * (n)` and `[x] * len(n)` / `[x] * n` BEFORE comprehension / operators
    e = e.replace(/\[([^\[\]]*)\]\s*\*\s*\(([^()]+)\)/g, 'new Array($2).fill($1)');
    e = e.replace(/\[([^\[\]]*)\]\s*\*\s*([\w.\[\]]+(?:\((?:[^()]|\([^()]*\))*\))?)/g, 'new Array($2).fill($1)');

    // Wrap generator expressions inside sum/max/min/any/all/sep.join(...) into [...] before comprehension expansion
    var genRe = /(?:(?:^|[^.\w])\b(?:sum|max|min|any|all)|\.join)\s*\(/g;
    var genMatch;
    while ((genMatch = genRe.exec(e)) !== null) {
      var openPos = genMatch.index + genMatch[0].length - 1;
      var closePos = findMatchingParen(e, openPos);
      var innerG = e.slice(openPos + 1, closePos);
      var gDepth = 0, hasTopFor = false, trimmedG = innerG.trim();
      if (!(trimmedG.startsWith('[') && trimmedG.endsWith(']'))) {
        for (var gi = 0; gi < innerG.length; gi++) {
          var gc = innerG[gi];
          if (gc === '(' || gc === '[' || gc === '{') gDepth++;
          else if (gc === ')' || gc === ']' || gc === '}') gDepth--;
          else if (gDepth === 0 && /^\s+for\s+/.test(innerG.slice(gi))) { hasTopFor = true; break; }
        }
      }
      if (hasTopFor) {
        var wrapped = trimmedG;
        e = e.slice(0, openPos + 1) + '[' + wrapped + ']' + e.slice(closePos);
        genRe.lastIndex = openPos + 2 + wrapped.length;
      }
    }

    // Dict comprehension `{k: v for ... in ...}` & List comprehension BEFORE `in` -> `__py_in` & ternary!
    e = e.replace(/\{\s*([^:{}\n]+?)\s*:\s*([^:{}\n]+?)\s+for\s+(.+?)\s+in\s+([^\{\}\n]+?)\s*\}/g, function(_, kE, vE, lhsV, iterE) {
      var lhsP = lhsV.trim().indexOf(',') !== -1 ? '[' + lhsV.trim() + ']' : lhsV.trim();
      return '(function(){ var __m = new __HashMap(); for (var ' + lhsP + ' of __iter(' + transformPyExpr(iterE, helperClasses) + ')) { __m.put(' + transformPyExpr(kE, helperClasses) + ', ' + transformPyExpr(vE, helperClasses) + '); } return __m; })()';
    });
    var lcBodyPat = '(?:\\[[^\\[\\]]*\\]|[^\\[\\]])+?';
    e = e.replace(new RegExp('\\[\\s*(' + lcBodyPat + ')\\s+for\\s+(.+?)\\s+in\\s+(' + lcBodyPat + ')\\s+if\\s+(' + lcBodyPat + ')\\s*\\]', 'g'),
      function(_, outE, lv, itE, condE) {
        var lvT = lv.trim();
        var itemArg = (lvT.indexOf(',') !== -1 || lvT[0] === '[') ? ('__lc_i') : lvT;
        var destruct = (lvT.indexOf(',') !== -1 || lvT[0] === '[') ? ('var ' + (lvT[0] === '[' ? lvT : '[' + lvT + ']') + ' = __lc_i; ') : '';
        return '__iter(' + transformPyExpr(itE, helperClasses) + ').filter(function(' + itemArg + '){ ' + destruct + 'return (' + transformPyExpr(condE, helperClasses) + '); }).map(function(' + itemArg + '){ ' + destruct + 'return (' + transformPyExpr(outE, helperClasses) + '); })';
      });
    e = e.replace(new RegExp('\\[\\s*(' + lcBodyPat + ')\\s+for\\s+(.+?)\\s+in\\s+(' + lcBodyPat + ')\\s*\\]', 'g'),
      function(_, outE, lv, itE) {
        var lvT = lv.trim();
        var itemArg = (lvT.indexOf(',') !== -1 || lvT[0] === '[') ? ('__lc_i') : lvT;
        var destruct = (lvT.indexOf(',') !== -1 || lvT[0] === '[') ? ('var ' + (lvT[0] === '[' ? lvT : '[' + lvT + ']') + ' = __lc_i; ') : '';
        return '__iter(' + transformPyExpr(itE, helperClasses) + ').map(function(' + itemArg + '){ ' + destruct + 'return (' + transformPyExpr(outE, helperClasses) + '); })';
      });

    // Python ternary `A if COND else B` (after comprehensions expanded `' for '`)
    if (e.indexOf(' for ') === -1) {
      e = e.replace(/^(.+?)\s+\bif\b\s+(.+?)\s+\belse\b\s+(.+)$/, function(_, a, cond, b) {
        var condTrim = cond.trim();
        var condWrapped = /^[a-zA-Z_]\w*(?:\.[a-zA-Z_]\w*|\[[^\[\]]+\])*$/.test(condTrim) ? ('__py_bool(' + condTrim + ')') : condTrim;
        var eqIdx = a.indexOf('=');
        if (eqIdx !== -1 && a[eqIdx - 1] !== '!' && a[eqIdx - 1] !== '<' && a[eqIdx - 1] !== '>' && a[eqIdx + 1] !== '=') {
          var lhs = a.slice(0, eqIdx + 1), valA = a.slice(eqIdx + 1).trim();
          return lhs + ' ((' + condWrapped + ') ? (' + valA + ') : (' + b + '))';
        }
        var retM = a.match(/^return\s+([\s\S]+)$/);
        if (retM) return 'return ((' + condWrapped + ') ? (' + retM[1] + ') : (' + b + '))';
        return '((' + condWrapped + ') ? (' + a + ') : (' + b + '))';
      });
    }

    e = e.replace(/(\b[\w.\[\]]+(?:\([^)]*\))?)\s*(<=?|>=?)\s*([\w.\[\]]+(?:\([^)]*\))?)\s*(<=?|>=?)\s*([\w.\[\]]+(?:\([^)]*\))?)/g,
      '($1 $2 $3 && $3 $4 $5)');
    // String methods MUST run BEFORE `not ...` so `not s[left].isalnum()` becomes `!__py_bool(__Character.isLetterOrDigit(...))`
    e = e.replace(/([\w.\[\]()]+)\.isalnum\(\)/g, '__Character.isLetterOrDigit($1)');
    e = e.replace(/([\w.\[\]()]+)\.isdigit\(\)/g, '__Character.isDigit($1)');
    e = e.replace(/([\w.\[\]()]+)\.lower\(\)/g, '$1.toLowerCase()');
    e = e.replace(/([\w.\[\]()]+)\.upper\(\)/g, '$1.toUpperCase()');
    e = e.replace(/([\w.\[\]()]+)\.strip\(\)/g, '$1.trim()');
    e = e.replace(/([\w.\[\]()]+)\.startswith\(/g, '$1.startsWith(');
    e = e.replace(/([\w.\[\]()]+)\.endswith\(/g, '$1.endsWith(');

    // Negative indexing & slicing BEFORE `in` -> `__py_in` so `s[j:i] in word_set` stays intact
    e = e.replace(/([a-zA-Z_]\w*(?:\.[a-zA-Z_]\w*(?:\([^()]*\))?|\[[^\[\]]+\])*)\[-(1|2|3|4|5)\]/g, '__py_idx($1, -$2)');
    e = e.replace(/([a-zA-Z_]\w*(?:\.[a-zA-Z_]\w*|\[[^\[\]]+\])*)\[((?:[^:\[\]]|\[[^\[\]]*\])*):((?:[^:\[\]]|\[[^\[\]]*\])*)\]/g, function(_, arr, a, b) {
      return '__py_slice(' + arr + ', ' + (a.trim() || 'null') + ', ' + (b.trim() || 'null') + ', 1)';
    });

    e = e.replace(/^del\s+/g, 'delete ');
    e = e.replace(/\band\b/g, '&&').replace(/\bor\b/g, '||');
    e = e.replace(/\bnot\s+in\b/g, '__NOTIN__');
    e = e.replace(/\bnot\s+([a-zA-Z_]\w*(?:\.[a-zA-Z_]\w*|\[[^\[\]]+\])*(?:\([^()]*\))?)(?!\w)/g, '!__py_bool($1)');
    e = e.replace(/\bnot\s*\(/g, '!__py_bool(');
    e = e.replace(/\bnot\s+/g, '!');
    var inOpPat = '(?:\\([^()]+\\)|\\[[^\\[\\]]+\\]|[a-zA-Z0-9_.]+(?:\\((?:[^()]|\\([^()]*\\))*\\)|\\[[^\\[\\]]+\\])*)';
    e = e.replace(new RegExp('(' + inOpPat + ')\\s+__NOTIN__\\s+(' + inOpPat + ')', 'g'), '!__py_in($1, $2)');
    e = e.replace(new RegExp('(' + inOpPat + ')\\s+in\\s+(' + inOpPat + ')', 'g'), '__py_in($1, $2)');
    e = e.replace(/(^|[^&])\b([a-zA-Z_]\w*(?:\.[a-zA-Z_]\w*|\[[^\[\]]+\])*)\s*&(?![&=])\s*([a-zA-Z_]\w*(?:\.[a-zA-Z_]\w*|\[[^\[\]]+\])*|\d+)/g, '$1__py_bitand($2, $3)');
    e = e.replace(/([\w.\[\]]+)\s*\/\/=\s*([^\n]+)/g, '$1 = Math.floor(($1) / ($2))');
    var divOpPat = '(?:\\([^()]+\\)|[a-zA-Z_]\\w*(?:\\.[a-zA-Z_]\\w*|\\[[^\\[\\]]+\\])*|\\d+)';
    e = e.replace(new RegExp('(' + divOpPat + ')\\s*\\/\\/\\s*(' + divOpPat + ')', 'g'), 'Math.floor(($1) / ($2))');
    e = e.replace(/(\b[\w.\[\]]*(?:str|char)\w*|__STR_\d+__)\s*\*\s*([\w.\[\]()]+)/gi, '__py_mul($1, $2)');

    // Python `lambda params: expr` (balanced over parens/brackets)
    var lamM;
    while ((lamM = e.match(/\blambda\s+([^:]+):\s*/)) !== null) {
      var lStart = lamM.index;
      var bStart = lStart + lamM[0].length;
      var ldP = 0, ldB = 0, ldC = 0, li = bStart;
      while (li < e.length) {
        var lch = e[li];
        if (lch === '(') ldP++;
        else if (lch === ')') { if (ldP === 0) break; ldP--; }
        else if (lch === '[') ldB++;
        else if (lch === ']') { if (ldB === 0) break; ldB--; }
        else if (lch === '{') ldC++;
        else if (lch === '}') { if (ldC === 0) break; ldC--; }
        else if (ldP === 0 && ldB === 0 && ldC === 0 && (lch === ',' || lch === '\n')) break;
        li++;
      }
      var lamBody = e.slice(bStart, li).trim();
      e = e.slice(0, lStart) + 'function(' + lamM[1].trim() + ') { return (' + lamBody + '); }' + e.slice(li);
    }

    e = e.replace(/\bcollections\.defaultdict\((?:list|int|set|dict)\)/g, function(m) {
      if (m.indexOf('list') !== -1) return '__py_defaultdict(Array)';
      if (m.indexOf('set') !== -1) return '__py_defaultdict(Set)';
      if (m.indexOf('(dict)') !== -1) return '__py_defaultdict(function(){ return new __HashMap(); })';
      return '__py_defaultdict(Number)';
    });
    e = e.replace(/\bdefaultdict\((?:list|int|set|dict)\)/g, function(m) {
      if (m.indexOf('list') !== -1) return '__py_defaultdict(Array)';
      if (m.indexOf('set') !== -1) return '__py_defaultdict(Set)';
      if (m.indexOf('(dict)') !== -1) return '__py_defaultdict(function(){ return new __HashMap(); })';
      return '__py_defaultdict(Number)';
    });
    e = e.replace(/\bcollections\.Counter\(/g, '__py_Counter(');
    e = e.replace(/\bcollections\.deque\(/g, 'new __ArrayDeque(');
    e = e.replace(/\bcollections\.defaultdict\(/g, '__py_defaultdict(');
    e = e.replace(/\bCounter\(/g, '__py_Counter(');
    e = e.replace(/\bdeque\(/g, 'new __ArrayDeque(');
    e = e.replace(/\bset\(/g, '__py_set(');
    e = e.replace(/\blist\(/g, '__iter(');
    e = e.replace(/\bheapq\./g, '__heapq.');
    e = e.replace(/\bbisect\.bisect_left\(/g, '__bisect.bisect_left(');
    e = e.replace(/\bbisect\.bisect_right\(/g, '__bisect.bisect_right(');
    e = e.replace(/(^|[^.])\bbisect_left\(/g, '$1__bisect.bisect_left(');
    e = e.replace(/(^|[^.])\bbisect_right\(/g, '$1__bisect.bisect_right(');
    e = e.replace(/(^|[^.])\brandom\./g, '$1__random.');
    e = e.replace(/\btime\.time\(\)/g, '(Date.now() / 1000)');
    e = e.replace(/\blen\(/g, '__py_len(');
    e = e.replace(/\brange\(/g, '__py_range(');
    e = e.replace(/\benumerate\(/g, '__py_enumerate(');
    e = e.replace(/\bzip\(/g, '__py_zip(');
    // Normalize `sorted(col, key=..., reverse=...)` and `arr.sort(key=..., reverse=...)`
    function rewritePySortCall(str, isMethod) {
      var tok = isMethod ? '.sort(' : 'sorted(';
      var idxS = str.indexOf(tok);
      while (idxS !== -1) {
        if (!isMethod && idxS > 0 && /[\w.]/.test(str[idxS - 1])) {
          idxS = str.indexOf(tok, idxS + 1);
          continue;
        }
        var opP = idxS + tok.length - 1;
        var clP = findMatchingParen(str, opP);
        var inside = str.slice(opP + 1, clP);
        // Split top-level args
        var args = [], d = 0, sStart = 0;
        for (var k = 0; k < inside.length; k++) {
          var ch = inside[k];
          if (ch === '(' || ch === '[' || ch === '{') d++;
          else if (ch === ')' || ch === ']' || ch === '}') d--;
          else if (ch === ',' && d === 0) { args.push(inside.slice(sStart, k).trim()); sStart = k + 1; }
        }
        args.push(inside.slice(sStart).trim());
        var colArg = isMethod ? null : args[0];
        var keyArg = 'null', revArg = 'false';
        for (var ai = isMethod ? 0 : 1; ai < args.length; ai++) {
          if (/^key\s*=/.test(args[ai])) keyArg = args[ai].replace(/^key\s*=\s*/, '');
          else if (/^reverse\s*=/.test(args[ai])) revArg = args[ai].replace(/^reverse\s*=\s*/, '');
          else if (ai === 1 && !isMethod) keyArg = args[ai];
        }
        var repS = isMethod
          ? ('.sort(function(__a, __b){ var __fn=' + keyArg + '; var __ka=__fn?__fn(__a):__a, __kb=__fn?__fn(__b):__b; var __c=__ka<__kb?-1:(__ka>__kb?1:0); return ' + revArg + '?-__c:__c; })')
          : ('__py_sorted(' + colArg + ', ' + keyArg + ', ' + revArg + ')');
        str = str.slice(0, idxS) + repS + str.slice(clP + 1);
        idxS = str.indexOf(tok, idxS + repS.length);
      }
      return str;
    }
    e = rewritePySortCall(e, false);
    e = rewritePySortCall(e, true);
    e = e.replace(/\breversed\(/g, '__py_reversed(');
    e = e.replace(/(^|[^.])\bmin\(/g, '$1__py_min(');
    e = e.replace(/(^|[^.])\bmax\(/g, '$1__py_max(');
    e = e.replace(/(^|[^.])\babs\(/g, '$1Math.abs(');
    e = e.replace(/(^|[^.])\bsum\(/g, '$1__py_sum(');
    e = e.replace(/(^|[^.])\bany\(/g, '$1__py_any(');
    e = e.replace(/(^|[^.])\ball\(/g, '$1__py_all(');
    e = e.replace(/(^|[^.])\bint\(/g, '$1parseInt(');
    e = e.replace(/(^|[^.])\bstr\(/g, '$1String(');
    e = e.replace(/\bListNode\(/g, 'new ListNode(');
    e = e.replace(/\bTreeNode\(/g, 'new TreeNode(');
    if (!helperClasses['Node']) {
      e = e.replace(/(^|[^.])\bNode\(/g, '$1new GraphNode(');
    }
    var joinM;
    while ((joinM = e.match(/(__STR_\d+__)\.join\(/)) !== null) {
      var jOpen = joinM.index + joinM[0].length - 1;
      var jClose = findMatchingParen(e, jOpen);
      var jInner = e.slice(jOpen + 1, jClose);
      e = e.slice(0, joinM.index) + '__iter(' + jInner + ').join(' + joinM[1] + ')' + e.slice(jClose + 1);
    }
    e = e.replace(/([\w.\[\]()]+)\.append\(/g, '$1.push(');
    e = e.replace(/([\w.\[\]()]+)\.pop\(([^)]+)\)/g, '__py_pop($1, $2)');
    e = e.replace(/([\w.\[\]]+)\.get\(/g, '__py_get($1, ');
    e = e.replace(/([\w.\[\]]+)\.values\(\)/g, '__py_values($1)');
    e = e.replace(/([\w.\[\]]+)\.keys\(\)/g, '__py_keys($1)');
    e = e.replace(/([\w.\[\]]+)\.items\(\)/g, '__py_items($1)');
    e = e.replace(/\bself\./g, 'this.');
    Object.keys(helperClasses).forEach(function(hc) {
      e = e.replace(new RegExp('\\bthis\\.' + hc + '\\(', 'g'), hc + '(');
    });
    return e;
  }

  function wrapPyCond(condJs) {
    // Convert bare `!ident` / `ident` truthiness checks cleanly
    var c = condJs.trim();
    c = c.replace(/(^|&&|\|\|)\s*!\s*([\w.\[\]]+)\s*(?=$|&&|\|\|)/g, '$1 !__py_bool($2)');
    c = c.replace(/(^|&&|\|\|)\s*([\w.\[\]]+)\s*(?=$|&&|\|\|)/g, '$1 __py_bool($2)');
    return c;
  }

  function compilePyLines(rawLines, helperClasses, extraPrefix) {
    var jsLines = ['var __ops = 0;'];
    if (extraPrefix) jsLines.push(extraPrefix);
    var indentStack = [{ ind: 0, kind: 'root', brk: null }];
    var brkCounter = 0;
    var declared = {};

    function hasSiblingElse(startIdx, loopInd) {
      for (var k = startIdx + 1; k < rawLines.length; k++) {
        if (rawLines[k].indent < loopInd) return false;
        if (rawLines[k].indent === loopInd) {
          return /^else\s*:/.test(rawLines[k].text);
        }
      }
      return false;
    }

    function currentLoopBrkFlag() {
      for (var s = indentStack.length - 1; s >= 0; s--) {
        if (indentStack[s].kind === 'loop') return indentStack[s].brk;
      }
      return null;
    }

    var pendingLoopElse = null;

    for (var j = 0; j < rawLines.length; j++) {
      var ind = rawLines[j].indent;
      var text = rawLines[j].text;

      while (indentStack.length > 1 && ind < indentStack[indentStack.length - 1].ind) {
        var popped = indentStack.pop();
        jsLines.push('}');
        if (popped.kind === 'loop' && popped.brk && ind === popped.loopInd) {
          pendingLoopElse = popped.brk;
        }
      }
      // Also clear pendingLoopElse if next statement at same indent is NOT `else:`
      if (pendingLoopElse && !(ind >= 0 && /^else\s*:/.test(text))) {
        pendingLoopElse = null;
      }
      // Check if an outer loop ended cleanly right AT `ind` (because `indentStack` pushes `ind + 4`, wait: popped when `ind <= top.loopInd`, so if `ind === top.loopInd` above `ind < top.ind` already popped it!).

      if (text === 'pass' || /^nonlocal\b/.test(text) || /^global\b/.test(text)) continue;

      if (text === 'break') {
        var bFlag = currentLoopBrkFlag();
        jsLines.push(bFlag ? (bFlag + ' = true; break;') : 'break;');
        continue;
      }

      var mDef = text.match(/^def\s+(\w+)\s*\(/);
      if (mDef) {
        var opD = mDef[0].length - 1;
        var clD = findMatchingParen(text, opD);
        var rawDParams = clD !== -1 ? text.slice(opD + 1, clD) : '';
        var dParams = parseParamNames(rawDParams).filter(function(p) { return p !== 'self'; });
        jsLines.push('function ' + mDef[1] + '(' + dParams.join(', ') + ') {');
        indentStack.push({ ind: ind + 4, kind: 'fn', brk: null });
        continue;
      }

      var singleIf = !/:\s*$/.test(text) && text.match(/^if\s+([^:]+):\s*(.+)$/);
      if (singleIf) {
        var sAct = singleIf[2].trim() === 'break' && currentLoopBrkFlag()
          ? (currentLoopBrkFlag() + ' = true; break')
          : transformPyExpr(singleIf[2], helperClasses);
        jsLines.push('if (' + wrapPyCond(transformPyExpr(singleIf[1], helperClasses)) + ') { ' + sAct + '; }');
        continue;
      }

      var mIf = text.match(/^if\s+(.+):$/);
      var mElif = text.match(/^elif\s+(.+):$/);
      var mElse = text.match(/^else\s*:$/);
      var mWhile = text.match(/^while\s+(.+):$/);
      var mFor = text.match(/^for\s+(.+?)\s+in\s+(.+):$/);

      if (mIf) {
        pendingLoopElse = null;
        jsLines.push('if (' + wrapPyCond(transformPyExpr(mIf[1], helperClasses)) + ') {');
        indentStack.push({ ind: ind + 4, kind: 'if', brk: null });
      } else if (mElif) {
        pendingLoopElse = null;
        jsLines.push('else if (' + wrapPyCond(transformPyExpr(mElif[1], helperClasses)) + ') {');
        indentStack.push({ ind: ind + 4, kind: 'if', brk: null });
      } else if (mElse) {
        if (pendingLoopElse) {
          jsLines.push('if (!' + pendingLoopElse + ') {');
          pendingLoopElse = null;
        } else {
          jsLines.push('else {');
        }
        indentStack.push({ ind: ind + 4, kind: 'else', brk: null });
      } else if (mWhile) {
        var wHasElse = hasSiblingElse(j, ind);
        var wFlag = wHasElse ? ('__brk' + (brkCounter++)) : null;
        if (wFlag) jsLines.push('var ' + wFlag + ' = false;');
        jsLines.push('while (' + wrapPyCond(transformPyExpr(mWhile[1], helperClasses)) + ') { if (++__ops > 5000000) throw new Error("TLE");');
        indentStack.push({ ind: ind + 4, kind: 'loop', loopInd: ind, brk: wFlag });
      } else if (mFor) {
        var fHasElse = hasSiblingElse(j, ind);
        var fFlag = fHasElse ? ('__brk' + (brkCounter++)) : null;
        if (fFlag) jsLines.push('var ' + fFlag + ' = false;');
        var target = convertPyTuples(mFor[1].trim());
        var iterExpr = transformPyExpr(mFor[2].trim(), helperClasses);
        var lhs = target.indexOf(',') !== -1 ? '[' + target + ']' : target;
        jsLines.push('for (var ' + lhs + ' of __iter(' + iterExpr + ')) { if (++__ops > 5000000) throw new Error("TLE");');
        indentStack.push({ ind: ind + 4, kind: 'loop', loopInd: ind, brk: fFlag });
      } else {
        var stmt = transformPyExpr(text, helperClasses);
        // Slice assignment `arr[a:b] = val`
        var sliceAssign = stmt.match(/^__py_slice\(([^,]+),\s*([^,]+),\s*([^,]+),\s*1\)\s*=\s*(.+)$/);
        if (sliceAssign) {
          var arrN = sliceAssign[1], aV = sliceAssign[2], bV = sliceAssign[3], valE = sliceAssign[4];
          stmt = '(function(){ var __s=' + (aV==='null'?'0':aV) + ', __e=' + (bV==='null'?arrN+'.length':bV) + ', __rep=__iter(' + valE + '); ' + arrN + '.splice.apply(' + arrN + ', [__s, __e - __s].concat(__rep)); })()';
        } else {
          var tupleAssign = stmt.match(/^([\w.\[\]]+(?:\s*,\s*[\w.\[\]]+)+)\s*=\s*(.+)$/);
          if (tupleAssign) {
            var vars = tupleAssign[1].split(',').map(function(v) { return v.trim(); });
            var rhs = tupleAssign[2].trim();
            var hasTopComma = false, td = 0;
            for (var ti = 0; ti < rhs.length; ti++) {
              var tc = rhs[ti];
              if (tc === '(' || tc === '[' || tc === '{') td++;
              else if (tc === ')' || tc === ']' || tc === '}') td--;
              else if (td === 0 && tc === ',') { hasTopComma = true; break; }
            }
            var rhsExpr = (rhs.startsWith('[') || !hasTopComma) ? rhs : '[' + rhs + ']';
            var decls = vars.filter(function(v) { return /^[a-zA-Z_]\w*$/.test(v) && !declared[v]; });
            decls.forEach(function(v) { declared[v] = true; });
            stmt = (decls.length ? 'var ' + decls.join(', ') + '; ' : '') + '[' + vars.join(', ') + '] = ' + rhsExpr;
          } else {
            var singleAssign = stmt.match(/^([a-zA-Z_]\w*)\s*=(?!=)/);
            if (singleAssign && !declared[singleAssign[1]]) {
              declared[singleAssign[1]] = true;
              stmt = 'var ' + stmt;
            }
          }
        }
        jsLines.push(stmt + ';');
      }
    }

    while (indentStack.length > 1) {
      indentStack.pop();
      jsLines.push('}');
    }
    return jsLines.join('\n');
  }

  LessonRuntime._transpilePython = function(code, methodName) {
    var masked = maskStrings(code, true);
    var clean = masked.code.replace(/^\s*(?:from|import)\s+.*$/gm, '');
    var rawFileLines = clean.split('\n');
    // Join continuation lines where (), [], {} depth > 0
    var lines = [];
    var parenBal = 0;
    for (var li = 0; li < rawFileLines.length; li++) {
      var rl = rawFileLines[li];
      if (parenBal > 0 && lines.length > 0) {
        lines[lines.length - 1] += ' ' + rl.trim();
      } else {
        lines.push(rl);
      }
      for (var ci = 0; ci < rl.length; ci++) {
        var cc = rl[ci];
        if (cc === '(' || cc === '[' || cc === '{') parenBal++;
        else if (cc === ')' || cc === ']' || cc === '}') parenBal = Math.max(0, parenBal - 1);
      }
    }

    function parsePyDefaults(paramStr) {
      var defs = [];
      if (!paramStr) return defs;
      // Split top-level commas only (avoid breaking `float('inf')` etc.)
      var parts = [], d = 0, pStart = 0;
      for (var k = 0; k < paramStr.length; k++) {
        var ch = paramStr[k];
        if (ch === '(' || ch === '[') d++;
        else if (ch === ')' || ch === ']') d--;
        else if (ch === ',' && d === 0) { parts.push(paramStr.slice(pStart, k)); pStart = k + 1; }
      }
      parts.push(paramStr.slice(pStart));
      for (var pi = 0; pi < parts.length; pi++) {
        var eqIdx = parts[pi].indexOf('=');
        if (eqIdx !== -1) {
          var pName = parts[pi].slice(0, eqIdx).split(':')[0].trim();
          var pVal = transformPyExpr(parts[pi].slice(eqIdx + 1).trim());
          if (pName && pName !== 'self') {
            defs.push('if (' + pName + ' === undefined) ' + pName + ' = ' + pVal + ';');
          }
        }
      }
      return defs;
    }

    // Parse all classes (top-level & nested inner classes) and module functions cleanly
    var classes = [];
    var moduleFuncs = [];
    var classStack = [];
    var currMeth = null;
    var baseIndent = -1;

    for (var i = 0; i < lines.length; i++) {
      var line = lines[i];
      if (!line.trim()) continue;
      var ind = line.search(/\S/);
      while (classStack.length > 0 && ind <= classStack[classStack.length - 1].indent) {
        classStack.pop();
      }
      if (currMeth && ind <= currMeth.defIndent) {
        currMeth = null;
      }

      var mCls = line.match(/^\s*class\s+(\w+)(?:\([^)]*\))?\s*:/);
      if (mCls) {
        var clsObj = { name: mCls[1], indent: ind, methods: [] };
        classes.push(clsObj);
        classStack.push(clsObj);
        currMeth = null;
        continue;
      }

      var mDef = line.match(/^(\s*)def\s+(\w+)\s*\(/);
      if (mDef) {
        var ownerCls = classStack.length > 0 ? classStack[classStack.length - 1] : null;
        if (!ownerCls || ind === ownerCls.indent + 4) {
          var defOpenP = mDef[0].length - 1;
          var defCloseP = findMatchingParen(line, defOpenP);
          var rawParams = defCloseP !== -1 ? line.slice(defOpenP + 1, defCloseP) : '';
          currMeth = {
            name: mDef[2],
            defIndent: ind,
            params: parseParamNames(rawParams).filter(function(p) { return p !== 'self'; }),
            defaults: parsePyDefaults(rawParams),
            body: []
          };
          if (ownerCls) ownerCls.methods.push(currMeth);
          else moduleFuncs.push(currMeth);
          baseIndent = -1;
          continue;
        }
      }

      if (currMeth) {
        if (baseIndent === -1) baseIndent = ind;
        currMeth.body.push({ indent: Math.max(0, ind - baseIndent), text: line.trim() });
      }
    }

    // Main class is Solution if present, else the LAST top-level class (so helper classes like TrieNode/ValueEntry come first)
    var topClasses = classes.filter(function(c) { return c.indent === 0; });
    var mainCls = topClasses.find(function(c) { return c.name === 'Solution'; }) || topClasses[topClasses.length - 1] || null;
    var helperClassesList = classes.filter(function(c) { return c !== mainCls; });
    var helperClasses = {};
    helperClassesList.forEach(function(hc) { helperClasses[hc.name] = true; });

    function emitHelperClass(hc) {
      var initM = hc.methods.find(function(m) { return m.name === '__init__'; });
      var out = 'function ' + hc.name + '(' + (initM ? initM.params.join(', ') : '') + ') {\n';
      out += 'if (!(this instanceof ' + hc.name + ')) return new ' + hc.name + '(' + (initM ? initM.params.join(', ') : '') + ');\n';
      if (initM) out += compilePyLines(initM.body, helperClasses, initM.defaults.join('\n')) + '\n';
      out += '}\n';
      hc.methods.filter(function(m) { return m.name !== '__init__'; }).forEach(function(m) {
        out += hc.name + '.prototype.' + m.name + ' = function(' + m.params.join(', ') + ') {\n' + compilePyLines(m.body, helperClasses, m.defaults.join('\n')) + '\n};\n';
      });
      return out;
    }

    var helperClsCode = helperClassesList.map(emitHelperClass).join('\n');

    function postUnmask(s) {
      var out = unmaskStrings(s, masked.literals);
      out = out.replace(/float\(["']inf["']\)/g, '1e15').replace(/float\(["']-inf["']\)/g, '-1e15');
      return out;
    }

    var clsName = mainCls ? mainCls.name : 'Solution';
    var methods = mainCls ? mainCls.methods : moduleFuncs;

    var hasInit = methods.some(function(m) { return m.name === '__init__'; });
    if ((clsName !== 'Solution' || hasInit) && methods.length > 0) {
      var initMeth = methods.find(function(m) { return m.name === '__init__'; });
      var jsClass = '(function() {\n' + helperClsCode + '\nfunction ' + clsName + '(' + (initMeth ? initMeth.params.join(', ') : '') + ') {\n';
      if (initMeth) jsClass += compilePyLines(initMeth.body, helperClasses, initMeth.defaults.join('\n')) + '\n';
      jsClass += '}\n';
      methods.filter(function(m) { return m.name !== '__init__'; }).forEach(function(m) {
        jsClass += clsName + '.prototype.' + m.name + ' = function(' + m.params.join(', ') + ') {\n' + compilePyLines(m.body, helperClasses, m.defaults.join('\n')) + '\n};\n';
      });
      jsClass += 'return ' + clsName + ';\n})()';
      return { isOOP: true, className: clsName, code: postUnmask(jsClass) };
    }

    var target = methods.find(function(m) { return m.name === methodName; }) || methods[0];
    if (!target) {
      return { isOOP: false, params: ['nums'], code: '(function(nums){ return null; })' };
    }
    var helperDefs = methods.filter(function(m) { return m !== target; }).map(function(m) {
      return 'function ' + m.name + '(' + m.params.join(', ') + ') {\n' + compilePyLines(m.body, helperClasses, m.defaults.join('\n')) + '\n}';
    }).join('\n');
    var mainBody = compilePyLines(target.body, helperClasses, target.defaults.join('\n'));
    var selfBind = 'var self = this || {}; self.' + target.name + ' = ' + target.name + ';\n';
    var fnStr = '(function ' + target.name + '(' + target.params.join(', ') + ') {\n' + selfBind + helperClsCode + '\n' + helperDefs + '\n' + mainBody.replace(/\bthis\./g, 'self.') + '\n})';
    return { isOOP: false, params: target.params, code: postUnmask(fnStr) };
  };

  // =========================================================================
  // COMPILE & RUN HELPER (Supports Standard, ListNode, TreeNode, Graph, OOP)
  // =========================================================================
  LessonRuntime._compileAndEvaluate = function(code, lang, config) {
    var runtime = LessonRuntime._createRuntimeEnv();
    var transpiled = lang === 'python'
      ? LessonRuntime._transpilePython(code, config.methodName)
      : LessonRuntime._transpileJVM(code, config.methodName);

    var envKeys = Object.keys(runtime);
    var envVals = envKeys.map(function(k) { return runtime[k]; });
    var factory = new Function(envKeys.join(', '), 'return ' + transpiled.code + ';');
    var compiled = factory.apply(null, envVals);

    return {
      isOOP: transpiled.isOOP,
      compiled: compiled
    };
  };

  LessonRuntime._runTestCase = function(compiledInfo, tc, config) {
    if (compiledInfo.isOOP || tc.mode === 'oop' || Array.isArray(tc.ops)) {
      var Cls = compiledInfo.compiled;
      var ops = tc.ops;
      var argsList = tc.args;
      var instance = null;
      var outputs = [];
      for (var i = 0; i < ops.length; i++) {
        var op = ops[i];
        var callArgs = JSON.parse(JSON.stringify(argsList[i] || []));
        if (i === 0) {
          instance = new (Function.prototype.bind.apply(Cls, [null].concat(callArgs)))();
          outputs.push(null);
        } else {
          var snakeOp = op.replace(/([a-z0-9])([A-Z]+)/g, '$1_$2').toLowerCase();
          var fnOp = typeof instance[op] === 'function' ? instance[op] : instance[snakeOp];
          var res = fnOp.apply(instance, callArgs);
          outputs.push(res === undefined ? null : res);
        }
      }
      return { actual: outputs, argsDisplay: [ops, argsList] };
    }

    function normalizeInputArgs(rawInput) {
      if (Array.isArray(rawInput)) {
        return JSON.parse(JSON.stringify(rawInput));
      }
      if (rawInput && typeof rawInput === 'object') {
        var vals = Object.keys(rawInput).map(function(k) { return rawInput[k]; });
        return JSON.parse(JSON.stringify(vals));
      }
      if (typeof rawInput === 'string') {
        var trimmed = rawInput.trim();
        if (trimmed === '') return [''];
        try {
          var parsed = JSON.parse('[' + trimmed + ']');
          return parsed;
        } catch (e) {
          var parts = [];
          var eqRe = /[a-zA-Z0-9_]+\s*=\s*(\[[\s\S]*?\](?=\s*,\s*[a-zA-Z0-9_]+\s*=|\s*$)|\{[^}]*\}|"[^"]*"|'[^']*'|[^\s,]+)/g;
          var m;
          while ((m = eqRe.exec(trimmed)) !== null) {
            var token = m[1].replace(/'/g, '"');
            try { parts.push(JSON.parse(token)); } catch (_) { parts.push(m[1]); }
          }
          if (parts.length > 0) return parts;
          return [trimmed];
        }
      }
      return [rawInput];
    }

    var fn = compiledInfo.compiled;
    var rawArgs = normalizeInputArgs(tc.input);
    var args = rawArgs.slice();

    // Auto-marshal ListNode / TreeNode / GraphNode based on config.ioType
    var io = config.ioType || '';
    if (io === 'list') {
      args = args.map(function(a) { return Array.isArray(a) ? toListNode(a) : a; });
    } else if (io === 'list_head_n') {
      args[0] = toListNode(args[0]);
    } else if (io === 'list_cycle') {
      args = [toListNode(args[0], args[1])];
    } else if (io === 'list_intersect') {
      // [intersectVal, listA, listB, skipA, skipB]
      var iv = args[0], lA = args[1], lB = args[2], sA = args[3], sB = args[4];
      var headA = toListNode(lA);
      var headB = toListNode(lB.slice(0, sB));
      if (iv > 0 && headA) {
        var curA = headA;
        for (var k = 0; k < sA && curA; k++) curA = curA.next;
        if (!headB) headB = curA;
        else {
          var tailB = headB;
          while (tailB.next) tailB = tailB.next;
          tailB.next = curA;
        }
      }
      args = [headA, headB];
    } else if (io === 'list_array') {
      args[0] = args[0].map(function(arr) { return toListNode(arr); });
    } else if (io === 'tree' && config.methodName !== 'buildTree') {
      args[0] = toTreeNode(args[0]);
    } else if (io === 'tree_lca') {
      var root = toTreeNode(args[0]);
      args = [root, findTreeNode(root, args[1]), findTreeNode(root, args[2])];
    } else if (io === 'random_list') {
      args[0] = toRandomList(args[0]);
    } else if (io === 'graph') {
      args[0] = toGraph(args[0]);
    }

    var actual = fn.apply(null, args);

    if (actual === undefined && args.length > 0 && tc.expected !== undefined && tc.expected !== null) {
      actual = args[0];
    }

    // Unmarshal output back to JSON array
    if (io === 'list' || io === 'list_head_n' || io === 'list_array') {
      if (actual === null || actual instanceof ListNode) actual = fromListNode(actual);
    } else if (io === 'list_intersect') {
      actual = actual ? actual.val : null;
    } else if (io === 'tree' && (actual === null || actual instanceof TreeNode)) {
      if (Array.isArray(tc.expected)) actual = fromTreeNode(actual);
    } else if (io === 'tree_lca') {
      actual = actual ? actual.val : null;
    } else if (io === 'random_list') {
      actual = fromRandomList(actual);
    } else if (io === 'graph') {
      actual = fromGraph(actual);
    }

    return { actual: actual, argsDisplay: rawArgs };
  };

  LessonRuntime._compareResult = function(actual, tc, config) {
    if (tc.isCustom) return true;
    if (typeof config.comparator === 'function') return config.comparator(actual, tc.expected, tc);
    if (tc.validCourseOrder && Array.isArray(actual)) {
      if (tc.expectCycle) return actual.length === 0;
      var numCourses = tc.input[0], prereqs = tc.input[1];
      if (actual.length !== numCourses) return false;
      var pos = {};
      for (var i = 0; i < actual.length; i++) pos[actual[i]] = i;
      for (var j = 0; j < prereqs.length; j++) {
        if (pos[prereqs[j][1]] >= pos[prereqs[j][0]]) return false;
      }
      return true;
    }
    if (Array.isArray(tc.expectedOneOf)) {
      return tc.expectedOneOf.some(function(opt) { return JSON.stringify(actual) === JSON.stringify(opt); });
    }
    if (typeof tc.expectedCount === 'number' && Array.isArray(actual)) {
      return actual.length === tc.expectedCount;
    }
    if (tc.weightedPick && Array.isArray(actual)) {
      var weights = tc.weightedPick.weights || [];
      var picks = actual.slice(1);
      if (picks.length === 0) return false;
      for (var wi = 0; wi < picks.length; wi++) {
        if (typeof picks[wi] !== 'number' || picks[wi] < 0 || picks[wi] >= weights.length) return false;
      }
      if (tc.weightedPick.dominantIdx !== undefined && picks.length >= 20) {
        var domCount = picks.filter(function(x) { return x === tc.weightedPick.dominantIdx; }).length;
        return domCount >= (tc.weightedPick.minDomCount || Math.floor(picks.length * 0.5));
      }
      return true;
    }
    if (tc.unordered && Array.isArray(actual) && Array.isArray(tc.expected)) {
      var norm = function(arr) {
        return arr.map(function(x) {
          if (Array.isArray(x)) return JSON.stringify(x.slice().sort());
          return JSON.stringify(x);
        }).sort();
      };
      return JSON.stringify(norm(actual)) === JSON.stringify(norm(tc.expected));
    }
    if (typeof actual === 'number' && typeof tc.expected === 'number') {
      return Math.abs(actual - tc.expected) < 1e-4;
    }
    return JSON.stringify(actual) === JSON.stringify(tc.expected);
  };

  LessonRuntime.PracticeArena = {
    init: function(config) {
      var currentLang = 'kotlin';
      var problemId = config.problemId || 'lc_practice';
      LessonRuntime._currentProblemId = problemId;
      var textarea = document.getElementById('practice-editor');
      if (!textarea) return;

      var storageKey = 'lc110_practice_code_' + problemId + '_';

      function loadCodeForLang(lang) {
        var saved = localStorage.getItem(storageKey + lang);
        if (saved) return saved;
        return config.starterStubs[lang] || '';
      }

      function setLang(lang) {
        currentLang = lang;
        ['kotlin', 'python', 'java'].forEach(function(l) {
          var btn = document.getElementById('practice-lang-' + l);
          if (btn) {
            btn.className = l === lang
              ? "px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 font-bold transition-all"
              : "px-3 py-1.5 rounded-lg text-slate-400 hover:text-white transition-all";
          }
        });
        var filenameMap = { kotlin: 'Solution.kt', python: 'solution.py', java: 'Solution.java' };
        var fnEl = document.getElementById('practice-filename');
        if (fnEl) fnEl.innerText = filenameMap[lang];
        textarea.value = loadCodeForLang(lang);
        var modelPre = document.getElementById('practice-model-code');
        if (modelPre && config.modelSolutions) {
          modelPre.innerText = config.modelSolutions[currentLang] || '';
        }
      }

      textarea.addEventListener('keydown', function(e) {
        if (e.key === 'Tab') {
          e.preventDefault();
          var start = this.selectionStart, end = this.selectionEnd;
          this.value = this.value.substring(0, start) + "    " + this.value.substring(end);
          this.selectionStart = this.selectionEnd = start + 4;
        } else if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
          e.preventDefault();
          window.runPracticeCode();
        }
      });

      textarea.addEventListener('input', function() {
        localStorage.setItem(storageKey + currentLang, textarea.value);
      });

      window.setPracticeLang = setLang;
      window.resetPracticeStub = function() {
        if (confirm("Reset current code back to the cold empty starter stub?")) {
          var stub = config.starterStubs[currentLang] || '';
          textarea.value = stub;
          localStorage.setItem(storageKey + currentLang, stub);
          var consoleBox = document.getElementById('practice-console');
          if (consoleBox) consoleBox.classList.add('hidden');
        }
      };

      var currentHintStep = 0;
      window.revealNextHint = function() {
        if (currentHintStep < config.hints.length) {
          currentHintStep++;
          var hintContainer = document.getElementById('practice-hints-container');
          if (hintContainer) {
            hintContainer.classList.remove('hidden');
            var card = document.createElement('div');
            card.className = "p-3.5 rounded-xl bg-slate-950 border border-indigo-900/50 text-xs font-mono space-y-1 animate-subtle";
            card.innerHTML = "<div class='text-indigo-400 font-bold uppercase text-[10px]'>💡 Progressive Hint " + currentHintStep + " of " + config.hints.length + ":</div><p class='text-slate-300 leading-relaxed'>" + config.hints[currentHintStep - 1] + "</p>";
            hintContainer.appendChild(card);
          }
          var hintBtn = document.getElementById('practice-hint-btn');
          if (hintBtn) {
            if (currentHintStep < config.hints.length) {
              hintBtn.innerText = "💡 Need another hint? (" + currentHintStep + "/" + config.hints.length + " used)";
            } else {
              hintBtn.innerText = "✓ All " + config.hints.length + " Hints Revealed";
              hintBtn.disabled = true;
              hintBtn.className = "text-xs font-mono text-slate-500 cursor-not-allowed";
            }
          }
        }
      };

      window.toggleModelSolution = function() {
        var box = document.getElementById('practice-model-box');
        if (!box) return;
        if (box.classList.contains('hidden')) {
          box.classList.remove('hidden');
          var pre = document.getElementById('practice-model-code');
          if (pre) pre.innerText = config.modelSolutions[currentLang] || '';
        } else {
          box.classList.add('hidden');
        }
      };

      window.loadModelIntoEditor = function() {
        if (confirm("Replace editor code with the official model solution?")) {
          var sol = config.modelSolutions[currentLang] || '';
          textarea.value = sol;
          localStorage.setItem(storageKey + currentLang, sol);
          window.runPracticeCode();
        }
      };

      window.runPracticeCode = function() {
        var code = textarea.value;
        var consoleBox = document.getElementById('practice-console');
        var dot = document.getElementById('practice-status-dot');
        var statusText = document.getElementById('practice-status-text');
        var output = document.getElementById('practice-output');
        if (!consoleBox) return;

        consoleBox.classList.remove('hidden');

        var compiledInfo;
        try {
          compiledInfo = LessonRuntime._compileAndEvaluate(code, currentLang, config);
        } catch (err) {
          if (dot) dot.className = "w-2.5 h-2.5 rounded-full bg-red-400";
          if (statusText) {
            statusText.className = "text-red-400 font-bold text-xs";
            statusText.innerText = "Compile / Syntax Error";
          }
          output.innerHTML = "<div class='p-3 rounded-lg bg-red-950/60 border border-red-800 text-red-200 text-xs font-mono'><strong>Error:</strong> " + err.message + "</div>";
          return;
        }

        var testCases = config.testCases.slice();
        var customInputVal = document.getElementById('practice-custom-input');
        if (customInputVal && customInputVal.value.trim()) {
          try {
            var parsedCustom = JSON.parse('[' + customInputVal.value.trim() + ']');
            testCases.push({ input: parsedCustom, isCustom: true });
          } catch (e) {}
        }

        var startTime = performance.now();
        var allPassed = true;
        var resultsHtml = '';

        for (var i = 0; i < testCases.length; i++) {
          var tc = testCases[i];
          var actual, argsDisplay = [], error = null;
          try {
            var res = LessonRuntime._runTestCase(compiledInfo, tc, config);
            actual = res.actual;
            argsDisplay = res.argsDisplay;
          } catch (err) {
            error = err.message;
            allPassed = false;
          }

          var passed = false;
          if (!error) {
            passed = LessonRuntime._compareResult(actual, tc, config);
            if (!passed) allPassed = false;
          }

          var statusBadge = error
            ? "<span class='px-2 py-0.5 rounded bg-red-950/80 border border-red-800 text-red-300 font-bold'>ERROR</span>"
            : (tc.isCustom
                ? "<span class='px-2 py-0.5 rounded bg-indigo-950/80 border border-indigo-800 text-indigo-300 font-bold'>CUSTOM</span>"
                : (passed
                    ? "<span class='px-2 py-0.5 rounded bg-emerald-950/80 border border-emerald-800 text-emerald-300 font-bold'>PASS ✓</span>"
                    : "<span class='px-2 py-0.5 rounded bg-red-950/80 border border-red-800 text-red-300 font-bold'>FAIL ✗</span>"));

          var inputStr = argsDisplay.map(function(a) { return JSON.stringify(a); }).join(', ');
          var expectedStr = tc.isCustom ? 'User Input'
            : (tc.expectedOneOf ? ('any of ' + JSON.stringify(tc.expectedOneOf))
            : (typeof tc.expectedCount === 'number' ? ('array of length ' + tc.expectedCount)
            : JSON.stringify(tc.expected)));
          var actualStr = error ? ('Exception: ' + error) : JSON.stringify(actual);

          resultsHtml += "<div class='p-3 rounded-xl bg-slate-900 border " + (passed ? 'border-slate-800' : 'border-red-900/60') + " space-y-1.5 text-xs font-mono'>" +
            "<div class='flex items-center justify-between'>" +
              "<span class='" + (passed ? 'text-emerald-400' : 'text-red-400') + " font-bold'>" + (tc.isCustom ? 'Custom Test Case:' : ('Test Case ' + (i + 1) + ':')) + "</span>" +
              statusBadge +
            "</div>" +
            "<div class='text-slate-400 truncate'><strong>Input:</strong> <span class='text-slate-200'>" + inputStr + "</span></div>" +
            (tc.isCustom ? "" : "<div class='text-slate-400'><strong>Expected:</strong> <span class='text-emerald-300'>" + expectedStr + "</span></div>") +
            "<div class='text-slate-400'><strong>Output:</strong> <span class='" + (passed ? 'text-cyan-300' : 'text-red-300') + "'>" + actualStr + "</span></div>" +
          "</div>";
        }

        var elapsedMs = Math.round((performance.now() - startTime) * 10) / 10;
        if (allPassed) {
          try {
            var solvedRaw = localStorage.getItem('lc110_solved_' + problemId);
            var solvedObj = solvedRaw ? JSON.parse(solvedRaw) : { langs: [] };
            if (!Array.isArray(solvedObj.langs)) solvedObj.langs = [];
            if (solvedObj.langs.indexOf(currentLang) === -1) solvedObj.langs.push(currentLang);
            solvedObj.at = Date.now();
            localStorage.setItem('lc110_solved_' + problemId, JSON.stringify(solvedObj));
            var recallKey = 'lc103_problem_' + problemId + '_recall';
            if (!localStorage.getItem(recallKey)) {
              localStorage.setItem(recallKey, Date.now().toString());
              if (typeof global.updateRecallBadge === 'function') global.updateRecallBadge();
            }
          } catch (e) {}
          if (dot) dot.className = "w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse";
          if (statusText) {
            statusText.className = "text-emerald-400 font-bold text-xs";
            statusText.innerText = "All " + testCases.length + " Test Cases Passed (" + elapsedMs + "ms)";
          }
        } else {
          if (dot) dot.className = "w-2.5 h-2.5 rounded-full bg-red-400";
          if (statusText) {
            statusText.className = "text-red-400 font-bold text-xs";
            statusText.innerText = "Test Suite Failed (" + elapsedMs + "ms)";
          }
        }
        output.innerHTML = "<div class='space-y-2'>" + resultsHtml + "</div>";
      };

      LessonRuntime.PracticeArena.setLang = setLang;
      LessonRuntime.PracticeArena.resetStub = window.resetPracticeStub;
      LessonRuntime.PracticeArena.revealHint = window.revealNextHint;
      LessonRuntime.PracticeArena.revealSolution = window.toggleModelSolution;
      LessonRuntime.PracticeArena.loadSolution = window.loadModelIntoEditor;
      LessonRuntime.PracticeArena.runTests = window.runPracticeCode;
      LessonRuntime.PracticeArena.runCustomInput = window.runPracticeCode;

      setLang('kotlin');
    }
  };

  // =========================================================================
  // 4. FOUR-QUESTION DIAGNOSTIC CODE QUIZ ENGINE
  // =========================================================================
  LessonRuntime.Quiz = {
    init: function(config) {
      var container = document.getElementById(config.containerId || 'quiz-container');
      if (!container) return;
      var questions = config.questions || [];
      var solvedSet = {};

      function render() {
        var html = '';
        for (var qIdx = 0; qIdx < questions.length; qIdx++) {
          var q = questions[qIdx];
          var qNum = qIdx + 1;
          var codeSnippetHtml = q.codeSnippet
            ? "<pre class='p-3.5 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-cyan-300 overflow-x-auto leading-relaxed'>" + q.codeSnippet + "</pre>"
            : "";

          var optionsHtml = '';
          for (var oIdx = 0; oIdx < q.options.length; oIdx++) {
            var opt = q.options[oIdx];
            optionsHtml += "<button onclick='window.checkQuizChoice(" + qNum + ", \"" + opt.id + "\")' id='quiz-opt-" + qNum + "-" + opt.id + "' class='w-full text-left p-3.5 rounded-xl bg-slate-950 border border-slate-800 hover:border-slate-700 text-slate-300 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed'>" +
              "<span class='px-2 py-0.5 rounded bg-slate-900 border border-slate-800 text-slate-400 font-bold uppercase text-[11px]'>" + opt.id + "</span>" +
              "<span class='flex-1'>" + opt.text + "</span>" +
            "</button>";
          }

          html += "<div class='p-6 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-4 shadow-xl' id='quiz-card-" + qNum + "'>" +
            "<div class='flex items-center justify-between text-xs font-mono'>" +
              "<span class='text-emerald-400 font-bold uppercase tracking-wider'>Diagnostic Check " + qNum + " of " + questions.length + "</span>" +
              "<span class='text-slate-500'>" + (q.category || 'Concept Check') + "</span>" +
            "</div>" +
            "<h3 class='text-sm sm:text-base font-bold text-white leading-relaxed'>" + q.prompt + "</h3>" +
            codeSnippetHtml +
            "<div class='space-y-2.5 pt-1'>" + optionsHtml + "</div>" +
            "<div id='quiz-feedback-" + qNum + "' class='hidden p-4 rounded-xl text-xs font-mono leading-relaxed space-y-2'></div>" +
          "</div>";
        }
        container.innerHTML = html;
      }

      window.checkQuizChoice = function(qNum, selectedId) {
        var q = questions[qNum - 1];
        if (!q) return;
        var selectedOpt = null;
        for (var i = 0; i < q.options.length; i++) {
          if (q.options[i].id === selectedId) selectedOpt = q.options[i];
        }
        var feedbackBox = document.getElementById('quiz-feedback-' + qNum);
        if (!feedbackBox) return;

        q.options.forEach(function(opt) {
          var btn = document.getElementById('quiz-opt-' + qNum + '-' + opt.id);
          if (btn) {
            btn.className = "w-full text-left p-3.5 rounded-xl bg-slate-950 border border-slate-800 text-slate-300 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed opacity-70";
          }
        });

        var chosenBtn = document.getElementById('quiz-opt-' + qNum + '-' + selectedId);
        if (selectedOpt && selectedOpt.isCorrect) {
          solvedSet[qNum] = true;
          try {
            var qPid = config.problemId || LessonRuntime._currentProblemId;
            if (!qPid && typeof window !== 'undefined' && window.location) {
              var mPid = window.location.pathname.match(/(\d{4})-/);
              if (mPid) qPid = mPid[1];
            }
            if (qPid && Object.keys(solvedSet).length >= questions.length && questions.length > 0) {
              localStorage.setItem('lc110_quiz_' + qPid, JSON.stringify({ passed: true, score: questions.length, total: questions.length, at: Date.now() }));
            }
          } catch (e) {}
          if (chosenBtn) {
            chosenBtn.className = "w-full text-left p-3.5 rounded-xl bg-emerald-950/60 border-2 border-emerald-500 text-emerald-200 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed shadow-lg shadow-emerald-500/20";
          }
          feedbackBox.className = "p-4 rounded-xl text-xs font-mono leading-relaxed bg-emerald-950/60 border border-emerald-800 text-emerald-200 space-y-2.5";
          feedbackBox.innerHTML = "<div class='flex items-center gap-2 font-bold text-emerald-300 uppercase text-[11px]'>" +
            "<span>✅</span> Correct! Choice " + selectedId + " is optimal</div>" +
            "<p class='text-slate-200'>" + selectedOpt.whyRight + "</p>" +
            (q.allExplanations ? "<div class='pt-2 border-t border-emerald-900/60 text-slate-300 space-y-1'>" +
              "<span class='text-[10px] uppercase font-bold text-emerald-400 block'>Why the other 3 choices fail:</span>" +
              q.allExplanations + "</div>" : "");
          feedbackBox.classList.remove('hidden');
        } else {
          if (chosenBtn) {
            chosenBtn.className = "w-full text-left p-3.5 rounded-xl bg-red-950/60 border-2 border-red-500 text-red-200 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed";
          }
          feedbackBox.className = "p-4 rounded-xl text-xs font-mono leading-relaxed bg-red-950/60 border border-red-800 text-red-200 space-y-2";
          feedbackBox.innerHTML = "<div class='flex items-center gap-2 font-bold text-red-300 uppercase text-[11px]'>" +
            "<span>❌</span> Choice " + selectedId + " is incorrect</div>" +
            "<p class='text-slate-200'>" + (selectedOpt ? selectedOpt.whyWrong : 'Incorrect selection.') + "</p>" +
            "<div class='pt-2 flex items-center justify-between text-[11px]'>" +
              "<span class='text-slate-400'>Click another option to try again, or:</span>" +
              "<button onclick='window.revealFullQuizAnswer(" + qNum + ")' class='text-indigo-400 hover:text-indigo-300 underline font-semibold'>Reveal Full Solution Breakdown ↗</button>" +
            "</div>";
          feedbackBox.classList.remove('hidden');
        }
      };

      window.revealFullQuizAnswer = function(qNum) {
        var q = questions[qNum - 1];
        if (!q) return;
        var feedbackBox = document.getElementById('quiz-feedback-' + qNum);
        if (!feedbackBox) return;
        var correctOpt = null;
        q.options.forEach(function(opt) {
          if (opt.isCorrect) correctOpt = opt;
          var btn = document.getElementById('quiz-opt-' + qNum + '-' + opt.id);
          if (btn && opt.isCorrect) {
            btn.className = "w-full text-left p-3.5 rounded-xl bg-emerald-950/60 border-2 border-emerald-500 text-emerald-200 text-xs font-mono transition-all flex items-start gap-3 leading-relaxed shadow-lg shadow-emerald-500/20";
          }
        });
        feedbackBox.className = "p-4 rounded-xl text-xs font-mono leading-relaxed bg-slate-950 border border-slate-800 text-slate-200 space-y-2.5";
        feedbackBox.innerHTML = "<div class='flex items-center gap-2 font-bold text-emerald-400 uppercase text-[11px]'>" +
          "<span>💡</span> Solution Breakdown: Choice " + (correctOpt ? correctOpt.id : '') + " is correct</div>" +
          "<p class='text-slate-300'>" + (correctOpt ? correctOpt.whyRight : '') + "</p>" +
          (q.allExplanations ? "<div class='pt-2 border-t border-slate-800 text-slate-300 space-y-1'>" +
            "<span class='text-[10px] uppercase font-bold text-slate-400 block'>Why the other 3 choices fail:</span>" +
            q.allExplanations + "</div>" : "");
      };

      render();
    }
  };

  // =========================================================================
  // 5. SPACED REPETITION 48-HOUR RECALL TIMER
  // =========================================================================
  LessonRuntime.SpacedRecall = {
    init: function(config) {
      var storageKey = 'lc103_problem_' + (config.problemId || '0001') + '_recall';
      var badge = document.getElementById(config.badgeId || 'recall-status-badge');
      var countdown = document.getElementById(config.countdownId || 'recall-countdown');

      function update() {
        var saved = localStorage.getItem(storageKey);
        if (!saved) {
          if (badge) badge.innerHTML = "Status: <span class='text-amber-400'>Not Practiced Yet</span>";
          if (countdown) countdown.innerText = "48h 00m 00s";
          return;
        }
        var elapsedMs = Date.now() - parseInt(saved, 10);
        var target48h = 48 * 60 * 60 * 1000;
        var diff = Math.max(0, target48h - elapsedMs);
        var hours = Math.floor(diff / (1000 * 60 * 60));
        var mins = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        var secs = Math.floor((diff % (1000 * 60)) / 1000);
        if (countdown) {
          countdown.innerText = (hours < 10 ? '0' : '') + hours + 'h ' + (mins < 10 ? '0' : '') + mins + 'm ' + (secs < 10 ? '0' : '') + secs + 's';
        }
        if (diff === 0) {
          if (badge) {
            badge.className = "px-3 py-1 rounded-full bg-emerald-950/80 border border-emerald-600 text-xs font-mono text-emerald-300 font-bold animate-pulse";
            badge.innerHTML = "Status: 🟢 Cold Recall Ready! (48h reached)";
          }
        } else {
          if (badge) {
            badge.className = "px-3 py-1 rounded-full bg-cyan-950/60 border border-cyan-800 text-xs font-mono text-cyan-300";
            badge.innerHTML = "Status: ⏳ Next Recall in ~" + Math.ceil(diff / (60 * 60 * 1000)) + " hrs";
          }
        }
      }

      window.markPracticedNow = function() {
        localStorage.setItem(storageKey, Date.now().toString());
        update();
      };
      window.resetRecallTimer = function() {
        localStorage.removeItem(storageKey);
        update();
      };

      global.updateRecallBadge = update;
      update();
      setInterval(update, 1000);
    }
  };

  if (typeof global.updateRecallBadge !== 'function') {
    global.updateRecallBadge = function() {};
  }

  global.LessonRuntime = LessonRuntime;

})(typeof window !== 'undefined' ? window : this);
