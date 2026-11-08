// Enhanced Transpiler and Runtime Sandbox for JVM & Python Algorithmic Subset

function createRuntimeEnv() {
  function __sub(a, b) {
    var na = typeof a === 'string' && a.length === 1 ? a.charCodeAt(0) : Number(a);
    var nb = typeof b === 'string' && b.length === 1 ? b.charCodeAt(0) : Number(b);
    return na - nb;
  }

  function __sort(arr) {
    if (!Array.isArray(arr)) return arr;
    if (arr.length > 0 && typeof arr[0] === 'number') {
      return arr.sort(function(a, b) { return a - b; });
    }
    return arr.sort();
  }

  function __HashSet() {
    this._set = new Set();
  }
  __HashSet.prototype.add = function(val) {
    if (this._set.has(val)) return false;
    this._set.add(val);
    return true;
  };
  __HashSet.prototype.contains = function(val) {
    return this._set.has(val);
  };
  __HashSet.prototype.has = function(val) {
    return this._set.has(val);
  };
  __HashSet.prototype.remove = function(val) {
    return this._set.delete(val);
  };
  Object.defineProperty(__HashSet.prototype, 'size', {
    get: function() { return this._set.size; }
  });

  function __ArrayList(init) {
    var arr = Array.isArray(init) ? init.slice() : [];
    arr.add = function(x) { this.push(x); return true; };
    return arr;
  }

  function __HashMap() {
    var map = new Map();
    var self = this;
    var proxy = new Proxy(this, {
      get: function(target, prop) {
        if (prop === '_map') return map;
        if (prop in target) return target[prop];
        if (map.has(prop)) return map.get(prop);
        return undefined;
      },
      set: function(target, prop, value) {
        if (prop in target) {
          target[prop] = value;
        } else {
          map.set(prop, value);
        }
        return true;
      }
    });
    this._map = map;
    return proxy;
  }
  __HashMap.prototype.put = function(k, v) { this._map.set(k, v); return v; };
  __HashMap.prototype.get = function(k) { return this._map.get(k); };
  __HashMap.prototype.getOrDefault = function(k, d) { return this._map.has(k) ? this._map.get(k) : d; };
  __HashMap.prototype.containsKey = function(k) { return this._map.has(k); };
  __HashMap.prototype.contains = function(k) { return this._map.has(k); };
  __HashMap.prototype.getOrPut = function(k, defaultValueFn) {
    if (!this._map.has(k)) {
      var val = typeof defaultValueFn === 'function' ? defaultValueFn() : defaultValueFn;
      this._map.set(k, val);
    }
    return this._map.get(k);
  };
  __HashMap.prototype.computeIfAbsent = function(k, mappingFn) {
    if (!this._map.has(k)) {
      var val = typeof mappingFn === 'function' ? mappingFn(k) : mappingFn;
      this._map.set(k, val);
    }
    return this._map.get(k);
  };
  __HashMap.prototype.values = function() {
    return Array.from(this._map.values());
  };

  function __StringBuilder() {
    this._parts = [];
  }
  __StringBuilder.prototype.append = function(val) {
    this._parts.push(val);
    return this;
  };
  __StringBuilder.prototype.toString = function() {
    return this._parts.join('');
  };

  function buildString(fn) {
    var sb = new __StringBuilder();
    fn.call(sb);
    return sb.toString();
  }

  function IntArray(size, initVal) {
    var arr = new Array(size);
    for (var i = 0; i < size; i++) arr[i] = (initVal || 0);
    arr.sort = function() { return __sort(this); };
    return arr;
  }

  function BooleanArray(size, initVal) {
    var arr = new Array(size);
    for (var i = 0; i < size; i++) arr[i] = (initVal || false);
    return arr;
  }

  return {
    __sub: __sub,
    __sort: __sort,
    __HashSet: __HashSet,
    __HashMap: __HashMap,
    __ArrayList: __ArrayList,
    __StringBuilder: __StringBuilder,
    buildString: buildString,
    IntArray: IntArray,
    BooleanArray: BooleanArray
  };
}

// Helper to extract method parameters & body with balanced curly braces
function extractMethod(code, methodName) {
  var clean = code.replace(/\/\*[\s\S]*?\*\//g, '').replace(/\/\/.*$/gm, '');
  
  var regex = new RegExp('(?:fun|def|public|private|static|\\w+(?:<[^>]*>)?|\\[\\])*\\s+' + methodName + '\\s*\\(([^)]*)\\)', 'm');
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

  var depth = 1;
  var endIdx = openBraceIdx + 1;
  while (depth > 0 && endIdx < clean.length) {
    if (clean[endIdx] === '{') depth++;
    else if (clean[endIdx] === '}') depth--;
    endIdx++;
  }

  var body = clean.slice(openBraceIdx + 1, endIdx - 1);
  
  var params = [];
  var parts = paramStr.split(',');
  for (var i = 0; i < parts.length; i++) {
    var p = parts[i].trim();
    if (!p) continue;
    if (p.indexOf(':') !== -1) {
      params.push(p.split(':')[0].trim());
    } else {
      var words = p.split(/\s+/);
      params.push(words[words.length - 1].trim());
    }
  }

  return {
    params: params,
    body: body
  };
}

// Transpile Kotlin / Java code to JS
function transpileJVM(code, methodName) {
  var parsed = extractMethod(code, methodName);
  var body = parsed ? parsed.body : code;
  var params = parsed ? parsed.params : ['nums'];

  var js = body;

  // 1. Remove package / import statements
  js = js.replace(/^\s*(?:package|import)\s+.*$/gm, '');

  // 2. computeIfAbsent & getOrPut BEFORE generic instantiation replacement
  js = js.replace(/(\w+)\.computeIfAbsent\(([^,]+),\s*(\w+)\s*->\s*new\s+ArrayList\s*(?:<[^>]*>)?\s*\(\)\)/g, '$1.computeIfAbsent($2, function($3) { return __ArrayList(); })');
  js = js.replace(/(\w+)\.computeIfAbsent\(([^,]+),\s*(\w+)\s*->\s*([^)]+)\)/g, '$1.computeIfAbsent($2, function($3) { return $4; })');
  js = js.replace(/(\w+)\.getOrPut\(([^)]+)\)\s*\{\s*([^}]+)\s*\}/g, '$1.getOrPut($2, function() { return $3; })');

  // 3. Generic instantiations and types
  js = js.replace(/hashSetOf\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashSet()');
  js = js.replace(/mutableSetOf\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashSet()');
  js = js.replace(/new\s+HashSet\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashSet()');
  js = js.replace(/\bHashSet\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashSet()');

  js = js.replace(/mutableMapOf\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashMap()');
  js = js.replace(/new\s+HashMap\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashMap()');
  js = js.replace(/\bHashMap\s*(?:<[\s\S]*?>)?\s*\(\)/g, 'new __HashMap()');

  // Strip type declarations:
  js = js.replace(/(?:Set|Map|List|ArrayList|HashMap|HashSet)\s*<[\s\S]*?>\s+(\w+)\s*=/g, 'var $1 =');
  js = js.replace(/(?:boolean|int|char|double|float|long|short|byte|String)\s*\[\s*\]\s+(\w+)\s*=/g, 'var $1 =');
  js = js.replace(/(?:boolean|int|char|double|float|long|short|byte|String)\s+(\w+)\s*=/g, 'var $1 =');

  // ArrayList / mutableListOf
  js = js.replace(/new\s+ArrayList\s*(?:<[\s\S]*?>)?\s*\(([^)]*)\)/g, '__ArrayList($1)');
  js = js.replace(/\bArrayList\s*(?:<[\s\S]*?>)?\s*\(([^)]*)\)/g, '__ArrayList($1)');
  js = js.replace(/\bmutableListOf\s*\(([^)]*)\)/g, '__ArrayList([$1])');
  js = js.replace(/\bmutableListOf\s*(?:<[\s\S]*?>)?\s*\(\)/g, '__ArrayList()');
  js = js.replace(/\bArrayList\s*(?:<[\s\S]*?>)?\s*\(\)/g, '__ArrayList()');

  // Arrays
  js = js.replace(/new\s+int\[([^\]]+)\]/g, 'IntArray($1)');
  js = js.replace(/\bIntArray\(([^)]+)\)/g, 'IntArray($1)');
  js = js.replace(/new\s+boolean\[([^\]]+)\]/g, 'BooleanArray($1)');
  js = js.replace(/\bBooleanArray\(([^)]+)\)/g, 'BooleanArray($1)');

  js = js.replace(/new\s+StringBuilder\(\)/g, 'new __StringBuilder()');

  // String / Char transformations
  js = js.replace(/String\(([^)]+)\.toCharArray\(\)\.apply\s*\{\s*sort\(\)\s*\}\)/g, '$1.split("").sort().join("")');
  js = js.replace(/(\w+)\.toCharArray\(\)\.sorted\(\)\.joinToString\([^)]*\)/g, '$1.split("").sort().join("")');
  js = js.replace(/new\s+String\(([^)]+)\)/g, '($1.join ? $1.join("") : String($1))');
  js = js.replace(/(\w+)\.toCharArray\(\)/g, '$1.split("")');
  js = js.replace(/Arrays\.sort\(([^)]+)\)/g, '__sort($1)');
  js = js.replace(/(\w+)\.sort\(\)/g, '__sort($1)');

  // char subtraction: ch - 'a' or s.charAt(i) - 'a'
  js = js.replace(/(\w+(?:\.charAt\([^)]+\))?)\s*-\s*['"]([a-zA-Z0-9])['"]/g, '__sub($1, "$2")');

  // Kotlin Loops
  js = js.replace(/for\s*\(\s*(?:val|var)?\s*(\w+)\s+in\s+([^)]+)\)/g, function(m, p1, p2) {
    if (p2.indexOf('until') !== -1) {
      var parts = p2.split('until');
      return 'for (var ' + p1 + ' = ' + parts[0].trim() + '; ' + p1 + ' < ' + parts[1].trim() + '; ' + p1 + '++)';
    }
    if (p2.indexOf('indices') !== -1) {
      var arrName = p2.replace('.indices', '').trim();
      return 'for (var ' + p1 + ' = 0; ' + p1 + ' < ' + arrName + '.length; ' + p1 + '++)';
    }
    return 'for (var ' + p1 + ' of ' + p2 + ')';
  });

  // Java Loops
  js = js.replace(/for\s*\(\s*(?:int|String|char|boolean|\w+)\s+(\w+)\s*:\s*([^)]+)\)/g, 'for (var $1 of $2)');
  js = js.replace(/for\s*\(\s*(?:int|var)\s+(\w+)\s*=/g, 'for (var $1 =');

  // Kotlin buildString { ... } using balanced brace parser
  var bsIdx = js.indexOf('buildString');
  while (bsIdx !== -1) {
    var openB = js.indexOf('{', bsIdx);
    if (openB === -1) break;
    var d = 1;
    var closeB = openB + 1;
    while (d > 0 && closeB < js.length) {
      if (js[closeB] === '{') d++;
      else if (js[closeB] === '}') d--;
      closeB++;
    }
    var inner = js.slice(openB + 1, closeB - 1);
    var transformedInner = inner.replace(/\bappend\(([^)]+)\)/g, 'sb.append($1)');
    var replacement = '(function() { var sb = new __StringBuilder(); ' + transformedInner + '; return sb.toString(); })()';
    js = js.slice(0, bsIdx) + replacement + js.slice(closeB);
    bsIdx = js.indexOf('buildString');
  }

  // Keywords & types cleanup
  js = js.replace(/\bval\s+/g, 'var ');
  js = js.replace(/\.values\b(?!\()/g, '.values()');
  js = js.replace(/\.size\b(?!\()/g, '.length');
  js = js.replace(/\.size\(\)/g, '.length');
  js = js.replace(/\.length\(\)/g, '.length');
  js = js.replace(/(\w+)\.equals\(([^)]+)\)/g, '$1 === $2');

  // Add loop guard
  var guardedJs = 'var __ops = 0;\n';
  guardedJs += js.replace(/(for\s*\([^)]*\)\s*\{|while\s*\([^)]*\)\s*\{)/g, '$1 if (++__ops > 5000000) throw new Error("Time Limit Exceeded (>5M operations / infinite loop)"); ');

  var paramList = params.length > 0 ? params.join(', ') : 'arg1';
  var fullFnCode = '(function(' + paramList + ') {\n' + guardedJs + '\n})';
  return {
    params: params,
    code: fullFnCode
  };
}

// Transpile Python code to JS
function transpilePython(code, methodName) {
  var clean = code.replace(/\/\*[\s\S]*?\*\//g, '').replace(/#.*$/gm, '');
  // Strip python imports (including indented imports)
  clean = clean.replace(/^\s*(?:from|import)\s+.*$/gm, '');

  var lines = clean.split('\n');
  var params = [];
  var rawBody = [];
  var insideMethod = false;
  var baseIndent = -1;

  for (var i = 0; i < lines.length; i++) {
    var line = lines[i];
    if (!line.trim()) continue;

    var defMatch = line.match(/^(\s*)def\s+(\w+)\s*\(([^)]*)\)/);
    if (defMatch) {
      if (defMatch[2] === methodName) {
        insideMethod = true;
        baseIndent = -1;
        var rawParams = defMatch[3].split(',');
        for (var p = 0; p < rawParams.length; p++) {
          var param = rawParams[p].trim().split(':')[0].trim();
          if (param && param !== 'self') {
            params.push(param);
          }
        }
        continue;
      } else if (insideMethod) {
        break;
      }
    }

    if (insideMethod) {
      var currentIndent = line.search(/\S/);
      if (baseIndent === -1) baseIndent = currentIndent;
      if (currentIndent < baseIndent) break;
      rawBody.push({
        indent: currentIndent - baseIndent,
        text: line.trim()
      });
    }
  }

  var jsLines = ['var __ops = 0;'];
  var indentStack = [0];

  for (var j = 0; j < rawBody.length; j++) {
    var item = rawBody[j];
    var ind = item.indent;
    var stmt = item.text;

    while (indentStack.length > 1 && ind < indentStack[indentStack.length - 1]) {
      indentStack.pop();
      jsLines.push('}');
    }

    // Python constructs translation
    stmt = stmt.replace(/\bTrue\b/g, 'true');
    stmt = stmt.replace(/\bFalse\b/g, 'false');
    stmt = stmt.replace(/\bNone\b/g, 'null');

    // Collections
    stmt = stmt.replace(/seen\s*=\s*set\(\)/g, 'var seen = new __HashSet()');
    stmt = stmt.replace(/groups\s*=\s*defaultdict\(list\)/g, 'var groups = new __HashMap()');

    // Conditional branches
    if (stmt.match(/^if\s+(\w+)\s+in\s+(\w+)\s*:/)) {
      stmt = stmt.replace(/^if\s+(\w+)\s+in\s+(\w+)\s*:/, 'if ($2.contains ? $2.contains($1) : ($1 in $2)) {');
    } else if (stmt.match(/^if\s+not\s+(\w+)\s*:/)) {
      stmt = stmt.replace(/^if\s+not\s+(\w+)\s*:/, 'if (!$1) {');
    } else if (stmt.match(/^if\s+([^:]+):/)) {
      stmt = stmt.replace(/^if\s+([^:]+):/, 'if ($1) {');
    } else if (stmt.match(/^elif\s+([^:]+):/)) {
      stmt = stmt.replace(/^elif\s+([^:]+):/, '} else if ($1) {');
    } else if (stmt.match(/^else\s*:/)) {
      stmt = stmt.replace(/^else\s*:/, '} else {');
    } else if (stmt.match(/^for\s+(\w+)\s+in\s+range\((\w+)\)\s*:/)) {
      stmt = stmt.replace(/^for\s+(\w+)\s+in\s+range\((\w+)\)\s*:/, 'for (var $1 = 0; $1 < $2; $1++) { if (++__ops > 5000000) throw new Error("TLE");');
    } else if (stmt.match(/^for\s+(\w+)\s+in\s+range\(([^,]+),\s*([^)]+)\)\s*:/)) {
      stmt = stmt.replace(/^for\s+(\w+)\s+in\s+range\(([^,]+),\s*([^)]+)\)\s*:/, 'for (var $1 = $2; $1 < $3; $1++) { if (++__ops > 5000000) throw new Error("TLE");');
    } else if (stmt.match(/^for\s+(\w+)\s+in\s+([^:]+):/)) {
      stmt = stmt.replace(/^for\s+(\w+)\s+in\s+([^:]+):/, 'for (var $1 of $2) { if (++__ops > 5000000) throw new Error("TLE");');
    } else if (stmt.match(/^while\s+([^:]+):/)) {
      stmt = stmt.replace(/^while\s+([^:]+):/, 'while ($1) { if (++__ops > 5000000) throw new Error("TLE");');
    }

    // Functions / Builtins
    stmt = stmt.replace(/len\((\w+)\)/g, '$1.length');
    stmt = stmt.replace(/["']["']\.join\(sorted\((\w+)\)\)/g, '$1.split("").sort().join("")');
    stmt = stmt.replace(/sorted\((\w+)\)/g, '$1.slice().sort()');

    // List append & map grouping
    stmt = stmt.replace(/groups\[([^\]]+)\]\.append\(([^)]+)\)/g, function(m, key, val) {
      return 'groups.getOrPut(' + key + ', function() { return []; }).push(' + val + ')';
    });
    stmt = stmt.replace(/(\w+)\.append\(([^)]+)\)/g, '$1.push($2)');

    // list(groups.values())
    stmt = stmt.replace(/list\((\w+)\.values\(\)\)/g, '$1.values()');

    // Variable declaration
    if (stmt.match(/^([a-zA-Z_]\w*)\s*=/)) {
      if (!stmt.startsWith('var ') && !stmt.startsWith('let ') && !stmt.startsWith('this.')) {
        stmt = 'var ' + stmt;
      }
    }

    var opensBlock = item.text.endsWith(':');
    if (opensBlock) {
      indentStack.push(ind + 4);
    } else {
      stmt += ';';
    }

    jsLines.push(stmt);
  }

  while (indentStack.length > 1) {
    indentStack.pop();
    jsLines.push('}');
  }

  var paramList = params.length > 0 ? params.join(', ') : 'arg1';
  var fullFnCode = '(function(' + paramList + ') {\n' + jsLines.join('\n') + '\n})';
  return {
    params: params,
    code: fullFnCode
  };
}

// TEST SUITE
var runtime = createRuntimeEnv();

function runTest(lang, name, code, methodName, testCases, comparator) {
  print('--- Testing ' + lang + ': ' + name + ' ---');
  var transpiled;
  if (lang === 'python') {
    transpiled = transpilePython(code, methodName);
  } else {
    transpiled = transpileJVM(code, methodName);
  }

  var fn;
  try {
    var factory = new Function(
      '__sub', '__sort', '__HashSet', '__HashMap', '__ArrayList', '__StringBuilder', 'buildString', 'IntArray', 'BooleanArray',
      'return ' + transpiled.code + ';'
    );
    fn = factory(
      runtime.__sub, runtime.__sort, runtime.__HashSet, runtime.__HashMap, runtime.__ArrayList, runtime.__StringBuilder, runtime.buildString, runtime.IntArray, runtime.BooleanArray
    );
  } catch (e) {
    print('COMPILE ERROR in ' + name + ': ' + e.message);
    print('Transpiled code:\n' + transpiled.code);
    return false;
  }

  var allPassed = true;
  for (var i = 0; i < testCases.length; i++) {
    var tc = testCases[i];
    var actual;
    try {
      actual = fn.apply(null, tc.input);
    } catch (err) {
      print('TEST ' + (i + 1) + ' EXCEPTION: ' + err.message);
      allPassed = false;
      continue;
    }

    var passed = comparator ? comparator(actual, tc.expected) : JSON.stringify(actual) === JSON.stringify(tc.expected);
    if (!passed) {
      print('TEST ' + (i + 1) + ' FAILED! Expected: ' + JSON.stringify(tc.expected) + ', Actual: ' + JSON.stringify(actual));
      allPassed = false;
    } else {
      print('TEST ' + (i + 1) + ' PASSED.');
    }
  }
  return allPassed;
}

// 1. CONTAINS DUPLICATE TESTS
var lc217Tests = [
  { input: [[1, 2, 3, 1]], expected: true },
  { input: [[1, 2, 3, 4]], expected: false },
  { input: [[1, 1, 1, 3, 3, 4, 3, 2, 4, 2]], expected: true }
];

var ktCode1 = [
  'class Solution {',
  '    fun containsDuplicate(nums: IntArray): Boolean {',
  '        val seen = hashSetOf<Int>()',
  '        for (num in nums) {',
  '            if (!seen.add(num)) return true',
  '        }',
  '        return false',
  '    }',
  '}'
].join('\n');

var pyCode1 = [
  'class Solution:',
  '    def containsDuplicate(self, nums: list[int]) -> bool:',
  '        seen = set()',
  '        for num in nums:',
  '            if num in seen:',
  '                return True',
  '            seen.add(num)',
  '        return False'
].join('\n');

var javaCode1 = [
  'import java.util.HashSet;',
  'import java.util.Set;',
  'class Solution {',
  '    public boolean containsDuplicate(int[] nums) {',
  '        Set<Integer> seen = new HashSet<>();',
  '        for (int num : nums) {',
  '            if (!seen.add(num)) return true;',
  '        }',
  '        return false;',
  '    }',
  '}'
].join('\n');

runTest('kotlin', 'LC217 Kotlin', ktCode1, 'containsDuplicate', lc217Tests);
runTest('python', 'LC217 Python', pyCode1, 'containsDuplicate', lc217Tests);
runTest('java', 'LC217 Java', javaCode1, 'containsDuplicate', lc217Tests);

// 2. GROUP ANAGRAMS TESTS
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
  }
];

var ktCode49 = [
  'class Solution {',
  '    fun groupAnagrams(strs: Array<String>): List<List<String>> {',
  '        val map = HashMap<String, MutableList<String>>()',
  '        for (s in strs) {',
  '            val key = String(s.toCharArray().apply { sort() })',
  '            map.getOrPut(key) { ArrayList() }.add(s)',
  '        }',
  '        return ArrayList(map.values)',
  '    }',
  '}'
].join('\n');

var pyCode49 = [
  'class Solution:',
  '    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:',
  '        from collections import defaultdict',
  '        groups = defaultdict(list)',
  '        for s in strs:',
  '            groups["".join(sorted(s))].append(s)',
  '        return list(groups.values())'
].join('\n');

var javaCode49 = [
  'import java.util.*;',
  'class Solution {',
  '    public List<List<String>> groupAnagrams(String[] strs) {',
  '        Map<String, List<String>> map = new HashMap<>();',
  '        for (String s : strs) {',
  '            char[] chars = s.toCharArray();',
  '            Arrays.sort(chars);',
  '            String key = new String(chars);',
  '            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);',
  '        }',
  '        return new ArrayList<>(map.values());',
  '    }',
  '}'
].join('\n');

runTest('kotlin', 'LC49 Kotlin', ktCode49, 'groupAnagrams', lc49Tests, anagramComparator);
runTest('python', 'LC49 Python', pyCode49, 'groupAnagrams', lc49Tests, anagramComparator);
runTest('java', 'LC49 Java', javaCode49, 'groupAnagrams', lc49Tests, anagramComparator);

// 3. OPTIMAL KOTLIN 26-COUNT FOR GROUP ANAGRAMS
var ktOptimal49 = [
  'class Solution {',
  '    fun groupAnagrams(strs: Array<String>): List<List<String>> {',
  '        val map = HashMap<String, MutableList<String>>()',
  '        for (s in strs) {',
  '            val count = IntArray(26)',
  '            for (ch in s) {',
  '                count[ch - "a"]++',
  '            }',
  '            val key = buildString {',
  '                for (c in count) {',
  '                    append("#")',
  '                    append(c)',
  '                }',
  '            }',
  '            map.getOrPut(key) { ArrayList() }.add(s)',
  '        }',
  '        return ArrayList(map.values)',
  '    }',
  '}'
].join('\n');

runTest('kotlin', 'LC49 Optimal Kotlin (26-count)', ktOptimal49, 'groupAnagrams', lc49Tests, anagramComparator);

// 4. EMPTY STUB TEST (MUST FAIL TEST CASES GRACEFULLY)
var emptyStub = [
  'class Solution {',
  '    fun containsDuplicate(nums: IntArray): Boolean {',
  '        return false',
  '    }',
  '}'
].join('\n');
print('--- Testing Empty Stub (Should fail test 1 and 3) ---');
var stubPassed = runTest('kotlin', 'LC217 Empty Stub', emptyStub, 'containsDuplicate', lc217Tests);
print('Empty stub overall passed: ' + stubPassed + ' (Expected: false)');

// 5. INFINITE LOOP GUARD TEST
var infiniteLoop = [
  'class Solution {',
  '    fun containsDuplicate(nums: IntArray): Boolean {',
  '        while (true) {}',
  '        return false',
  '    }',
  '}'
].join('\n');
print('--- Testing Infinite Loop Guard ---');
runTest('kotlin', 'LC217 Infinite Loop', infiniteLoop, 'containsDuplicate', lc217Tests);
