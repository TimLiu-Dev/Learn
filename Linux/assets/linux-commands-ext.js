/* ==========================================================================
   Linux Mastery 2027 — Extended Systems, Networking, Kernel & Ops Commands
   Plugs into LinuxLabExtDispatch in assets/linux-lab.js
   ========================================================================== */

(function (global) {
  'use strict';

  function initShellState(shell) {
    if (shell._extInit) return;
    shell._extInit = true;
    shell.umaskVal = '0022';
    shell.selinuxMode = 'Enforcing';
    shell.tmuxSessions = [
      { name: 'build-watch', windows: 2, attached: false }
    ];
    shell.users = {
      root: { uid: 0, gid: 0, groups: ['root'] },
      'android-dev': { uid: 1000, gid: 1000, groups: ['android-dev', 'sudo', 'docker'] },
      aegis: { uid: 990, gid: 990, groups: ['aegis'] }
    };
    shell.processes = [
      { pid: 1, user: 'root', cpu: '0.1', mem: '0.2', stat: 'Ss', cmd: '/sbin/init' },
      { pid: 512, user: 'root', cpu: '0.0', mem: '0.1', stat: 'Ss', cmd: '/lib/systemd/systemd-resolved' },
      { pid: 902, user: 'root', cpu: '0.0', mem: '0.1', stat: 'Ss', cmd: 'sshd: /usr/sbin/sshd -D' },
      { pid: 1120, user: 'aegis', cpu: '1.2', mem: '2.4', stat: 'Ssl', cmd: '/usr/local/bin/aegis-api --port 8443' },
      { pid: 1205, user: 'www-data', cpu: '0.3', mem: '0.8', stat: 'S', cmd: 'nginx: worker process' },
      { pid: 4821, user: 'android-dev', cpu: '88.4', mem: '54.2', stat: 'R', cmd: 'gradle-worker-daemon --max-workers=8' },
      { pid: shell.pid, user: shell.user, cpu: '0.0', mem: '0.1', stat: 'Ss', cmd: '-bash' }
    ];
    shell.services = {
      'ssh.service': { active: 'active', sub: 'running', enabled: 'enabled', desc: 'OpenBSD Secure Shell server', pid: 902 },
      'nginx.service': { active: 'active', sub: 'running', enabled: 'enabled', desc: 'A high performance web server and reverse proxy', pid: 1205 },
      'aegis-api.service': { active: 'active', sub: 'running', enabled: 'enabled', desc: 'AegisOps Telemetry Ingestion API', pid: 1120 },
      'chrony.service': { active: 'active', sub: 'running', enabled: 'enabled', desc: 'chrony, an NTP client/server', pid: 640 },
      'nftables.service': { active: 'active', sub: 'exited', enabled: 'enabled', desc: 'nftables firewall ruleset', pid: 0 },
      'aegis-backup.timer': { active: 'active', sub: 'waiting', enabled: 'enabled', desc: 'Daily LVM snapshot backup timer', pid: 0 }
    };
    shell.journal = [
      { unit: 'systemd', prio: 'info', msg: 'Started AegisOps Telemetry Ingestion API.' },
      { unit: 'sshd', prio: 'info', msg: 'Accepted publickey for android-dev from 10.20.0.14 port 54122 ssh2: ED25519' },
      { unit: 'nginx', prio: 'warn', msg: 'upstream timed out (110: Connection timed out) while reading response header from 127.0.0.1:8443' },
      { unit: 'aegis-api', prio: 'err', msg: 'TLS handshake error: certificate /etc/ssl/certs/aegis.crt expires in 3 days' },
      { unit: 'kernel', prio: 'err', msg: 'Out of memory: Killed process 4821 (gradle-worker-daemon) total-vm:5124000kB' }
    ];
    shell.nftRules = [
      'table inet filter {',
      '  chain input {',
      '    type filter hook input priority filter; policy drop;',
      '    iif "lo" accept',
      '    ct state established,related accept',
      '    tcp dport 22 accept',
      '    tcp dport 443 accept',
      '  }',
      '}'
    ];
    shell.ufwRules = [
      { port: '22/tcp', action: 'ALLOW IN', from: 'Anywhere' },
      { port: '443/tcp', action: 'ALLOW IN', from: 'Anywhere' }
    ];
    shell.ufwActive = true;
    shell.sysctl = {
      'vm.swappiness': '60',
      'net.ipv4.ip_forward': '0',
      'net.core.somaxconn': '4096',
      'fs.inotify.max_user_watches': '65536',
      'vm.overcommit_memory': '0'
    };
    shell.lvs = [
      { lv: 'root', vg: 'vg0', attr: '-wi-ao----', size: '30.00g' },
      { lv: 'aegis_data', vg: 'vg0', attr: 'twi-aotz--', size: '20.00g' }
    ];
    shell.openDeletedFiles = [
      { cmd: 'aegis-api', pid: 1120, user: 'aegis', fd: '7w', size: '18432000000', name: '/var/log/aegis/debug.log (deleted)' }
    ];
    shell.mounts = [
      { dev: '/dev/mapper/vg0-root', target: '/', fs: 'ext4', size: '30G', used: '14G', avail: '16G', pct: '47%', iused: '210400', itot: '1966080', ipct: '11%' },
      { dev: '/dev/mapper/vg0-aegis_data', target: '/var/lib/aegis', fs: 'xfs', size: '20G', used: '19G', avail: '1G', pct: '95%', iused: '48000', itot: '10485760', ipct: '1%' },
      { dev: 'tmpfs', target: '/run', fs: 'tmpfs', size: '800M', used: '1.4M', avail: '799M', pct: '1%', iused: '412', itot: '1024000', ipct: '1%' }
    ];
    shell.cronJobs = [
      '0 2 * * * /usr/local/bin/aegis-healthcheck.sh >> /var/log/aegis-cron.log 2>&1'
    ];
    shell.packages = {
      curl: '8.5.0-2ubuntu10',
      nginx: '1.24.0-2ubuntu7',
      tmux: '3.4-1build1',
      nftables: '1.0.9-1build1',
      chrony: '4.5-1ubuntu4',
      jq: '1.7.1-3build1'
    };
    shell.modules = ['overlay', 'br_netfilter', 'nf_tables', 'wireguard', 'nvme', 'ext4', 'xfs'];
  }

  function readInputLines(shell, files, stdin) {
    if (!files || files.length === 0) return { ok: true, text: stdin || '' };
    let combined = '';
    for (const f of files) {
      const res = shell.resolveNode(f, true);
      if (!res) return { ok: false, err: `No such file or directory: ${f}\n` };
      if (res.node.type === 'dir') return { ok: false, err: `Is a directory: ${f}\n` };
      combined += res.node.content;
    }
    return { ok: true, text: combined };
  }

  function splitLinesKeep(txt) {
    if (!txt) return [];
    const lines = txt.split('\n');
    if (lines[lines.length - 1] === '') lines.pop();
    return lines;
  }

  function octalToMode(oct, isDir) {
    const clean = oct.slice(-3).padStart(3, '0');
    const map = ['---', '--x', '-w-', '-wx', 'r--', 'r-x', 'rw-', 'rwx'];
    let out = isDir ? 'd' : '-';
    for (let i = 0; i < 3; i++) {
      const d = parseInt(clean[i], 10);
      out += map[isNaN(d) || d > 7 ? 0 : d];
    }
    if (oct.length === 4) {
      const s = parseInt(oct[0], 10);
      const arr = out.split('');
      if (s & 4) arr[3] = arr[3] === 'x' ? 's' : 'S';
      if (s & 2) arr[6] = arr[6] === 'x' ? 's' : 'S';
      if (s & 1) arr[9] = arr[9] === 'x' ? 't' : 'T';
      out = arr.join('');
    }
    return out;
  }

  function applySymbolicMode(curMode, expr) {
    const prefix = curMode[0];
    const perms = curMode.slice(1).split('');
    const parts = expr.split(',');
    for (const p of parts) {
      const m = p.match(/^([ugoa]*)([+\-=])([rwxst]+)$/);
      if (!m) continue;
      const who = m[1] || 'ugo';
      const op = m[2];
      const bits = m[3];
      const targets = [];
      if (who.includes('a') || who.includes('u')) targets.push(0);
      if (who.includes('a') || who.includes('g')) targets.push(3);
      if (who.includes('a') || who.includes('o')) targets.push(6);
      for (const base of targets) {
        if (op === '=') { perms[base] = '-'; perms[base + 1] = '-'; perms[base + 2] = '-'; }
        for (const b of bits) {
          const offset = b === 'r' ? 0 : (b === 'w' ? 1 : (b === 'x' ? 2 : -1));
          if (offset === -1) continue;
          if (op === '+' || op === '=') perms[base + offset] = b;
          else if (op === '-') perms[base + offset] = '-';
        }
      }
    }
    return prefix + perms.join('');
  }

  function parseSedExpr(expr) {
    const m = expr.match(/^s(.)(.*?)\1(.*?)\1([gi]*)$/);
    if (!m) return null;
    return { pat: m[2], rep: m[3], flags: m[4] };
  }

  function runAwk(program, text, sep) {
    const lines = splitLinesKeep(text);
    const reSep = sep ? new RegExp(sep) : /\s+/;
    let body = program.trim();
    let beginBlock = '';
    let endBlock = '';
    const bm = body.match(/BEGIN\s*\{([^}]*)\}/);
    if (bm) { beginBlock = bm[1].trim(); body = body.replace(bm[0], '').trim(); }
    const em = body.match(/END\s*\{([^}]*)\}/);
    if (em) { endBlock = em[1].trim(); body = body.replace(em[0], '').trim(); }

    let condExpr = null;
    let actionBlock = body;
    const pm = body.match(/^\/([^/]+)\/\s*\{([\s\S]*)\}$/);
    const nm = body.match(/^([^/{]+)\s*\{([\s\S]*)\}$/);
    if (pm) {
      condExpr = { type: 'regex', re: new RegExp(pm[1]) };
      actionBlock = pm[2].trim();
    } else if (nm) {
      condExpr = { type: 'expr', raw: nm[1].trim() };
      actionBlock = nm[2].trim();
    } else if (body.startsWith('{') && body.endsWith('}')) {
      actionBlock = body.slice(1, -1).trim();
    }

    const vars = {};
    const arrays = {};
    let out = '';

    const evalVal = (tok, fields, nr, fullLine) => {
      const t = tok.trim();
      if ((t.startsWith('"') && t.endsWith('"')) || (t.startsWith("'") && t.endsWith("'"))) return t.slice(1, -1);
      if (t === '$0') return fullLine;
      if (t === 'NR') return String(nr);
      if (t === 'NF') return String(fields.length);
      if (t === '$NF') return fields[fields.length - 1] || '';
      if (/^\$\d+$/.test(t)) return fields[parseInt(t.slice(1), 10) - 1] || '';
      if (vars[t] !== undefined) return String(vars[t]);
      return t;
    };

    const execStmt = (stmt, fields, nr, fullLine) => {
      const s = stmt.trim();
      if (!s) return;
      const incArr = s.match(/^([A-Za-z_]\w*)\[(.+?)\]\+\+$/);
      if (incArr) {
        const arrName = incArr[1];
        const key = evalVal(incArr[2], fields, nr, fullLine);
        arrays[arrName] = arrays[arrName] || {};
        arrays[arrName][key] = (arrays[arrName][key] || 0) + 1;
        return;
      }
      const addVar = s.match(/^([A-Za-z_]\w*)\s*\+=\s*(.+)$/);
      if (addVar) {
        const v = parseFloat(evalVal(addVar[2], fields, nr, fullLine)) || 0;
        vars[addVar[1]] = (parseFloat(vars[addVar[1]]) || 0) + v;
        return;
      }
      const incVar = s.match(/^([A-Za-z_]\w*)\+\+$/);
      if (incVar) { vars[incVar[1]] = (parseFloat(vars[incVar[1]]) || 0) + 1; return; }
      const forIn = s.match(/^for\s*\(\s*(\w+)\s+in\s+(\w+)\s*\)\s*print\s+(.+)$/);
      if (forIn) {
        const arr = arrays[forIn[2]] || {};
        for (const k of Object.keys(arr).sort()) {
          let expr = forIn[3];
          expr = expr.replace(new RegExp(`${forIn[2]}\\[${forIn[1]}\\]`, 'g'), `"${arr[k]}"`);
          expr = expr.replace(new RegExp(`\\b${forIn[1]}\\b`, 'g'), `"${k}"`);
          const parts = expr.split(',').map((x) => evalVal(x, [], 0, ''));
          out += parts.join(' ') + '\n';
        }
        return;
      }
      if (s.startsWith('print')) {
        const rest = s.slice(5).trim();
        if (!rest) { out += fullLine + '\n'; return; }
        const parts = rest.split(',').map((p) => evalVal(p, fields, nr, fullLine));
        out += parts.join(' ') + '\n';
      }
    };

    if (beginBlock) beginBlock.split(';').forEach((st) => execStmt(st, [], 0, ''));

    lines.forEach((line, i) => {
      const nr = i + 1;
      const fields = sep ? line.split(reSep) : line.trim().split(reSep).filter(Boolean);
      let ok = true;
      if (condExpr) {
        if (condExpr.type === 'regex') ok = condExpr.re.test(line);
        else if (condExpr.type === 'expr') {
          const cmp = condExpr.raw.match(/^(\$\d+|NR)\s*(==|!=|>=|<=|>|<|~)\s*(.+)$/);
          if (cmp) {
            const left = evalVal(cmp[1], fields, nr, line);
            const op = cmp[2];
            const right = cmp[3].trim().replace(/^["'/]|["'/]$/g, '');
            const ln = parseFloat(left);
            const rn = parseFloat(right);
            if (op === '==') ok = left === right;
            else if (op === '!=') ok = left !== right;
            else if (op === '~') ok = new RegExp(right).test(left);
            else if (op === '>') ok = ln > rn;
            else if (op === '>=') ok = ln >= rn;
            else if (op === '<') ok = ln < rn;
            else if (op === '<=') ok = ln <= rn;
          }
        }
      }
      if (ok && actionBlock) {
        actionBlock.split(';').forEach((st) => execStmt(st, fields, nr, line));
      }
    });

    if (endBlock) endBlock.split(';').forEach((st) => execStmt(st, [], lines.length, ''));
    return out;
  }

  function extDispatch(shell, cmd, args, stdin) {
    initShellState(shell);
    const { flags, pos } = shell.parseFlags(args);

    switch (cmd) {
      case 'alias': {
        if (args.length === 0) {
          return { stdout: Object.entries(shell.aliases).map(([k, v]) => `alias ${k}='${v}'`).join('\n') + '\n', stderr: '', exitCode: 0 };
        }
        for (const a of args) {
          const eq = a.indexOf('=');
          if (eq > 0) shell.aliases[a.slice(0, eq)] = a.slice(eq + 1).replace(/^['"]|['"]$/g, '');
        }
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'unalias': {
        for (const a of pos) delete shell.aliases[a];
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'source':
      case '.': {
        if (!pos[0]) return { stdout: '', stderr: `${cmd}: filename argument required\n`, exitCode: 2 };
        const r = shell.resolveNode(pos[0], true);
        if (!r || r.node.type !== 'file') return { stdout: '', stderr: `${cmd}: ${pos[0]}: No such file or directory\n`, exitCode: 1 };
        let out = '';
        let err = '';
        for (const line of r.node.content.split('\n')) {
          const t = line.trim();
          if (!t || t.startsWith('#')) continue;
          const res = shell.execute(t);
          out += res.stdout || '';
          err += res.stderr || '';
        }
        return { stdout: out, stderr: err, exitCode: 0 };
      }
      case 'tmux': {
        const sub = args[0] || 'ls';
        if (sub === 'ls' || sub === 'list-sessions') {
          return { stdout: shell.tmuxSessions.map((s) => `${s.name}: ${s.windows} windows (created Sun Nov 15 00:10:00 2026)${s.attached ? ' (attached)' : ''}`).join('\n') + '\n', stderr: '', exitCode: 0 };
        }
        if (sub === 'new' || sub === 'new-session') {
          const sIdx = args.indexOf('-s');
          const name = sIdx !== -1 && args[sIdx + 1] ? args[sIdx + 1] : String(shell.tmuxSessions.length);
          shell.tmuxSessions.push({ name, windows: 1, attached: true });
          return { stdout: `[detached (from session ${name})]\n`, stderr: '', exitCode: 0 };
        }
        if (sub === 'kill-session') {
          const tIdx = args.indexOf('-t');
          const target = tIdx !== -1 ? args[tIdx + 1] : '';
          shell.tmuxSessions = shell.tmuxSessions.filter((s) => s.name !== target);
          return { stdout: '', stderr: '', exitCode: 0 };
        }
        return { stdout: 'tmux 3.4: use tmux ls | tmux new -s <name> | tmux kill-session -t <name>\n', stderr: '', exitCode: 0 };
      }
      case 'vim':
      case 'vi':
      case 'nano': {
        return {
          stdout: `[${cmd}] Interactive full-screen editor mode is simulated in Lesson Labs.\nUse 'cat', 'echo >', or 'sed -i' in the shell, or use the Interactive Vim Drill above.\n`,
          stderr: '',
          exitCode: 0
        };
      }
      case 'sort': {
        const data = readInputLines(shell, pos, stdin);
        if (!data.ok) return { stdout: '', stderr: `sort: ${data.err}`, exitCode: 2 };
        let lines = splitLinesKeep(data.text);
        const numeric = flags.has('n');
        const rev = flags.has('r');
        const uniq = flags.has('u');
        let kCol = null;
        for (let i = 0; i < args.length; i++) {
          if (args[i] === '-k' && args[i + 1]) kCol = parseInt(args[++i], 10) - 1;
        }
        lines.sort((a, b) => {
          const av = kCol !== null ? (a.trim().split(/\s+/)[kCol] || '') : a;
          const bv = kCol !== null ? (b.trim().split(/\s+/)[kCol] || '') : b;
          if (numeric) return (parseFloat(av) || 0) - (parseFloat(bv) || 0);
          return av.localeCompare(bv);
        });
        if (rev) lines.reverse();
        if (uniq) lines = lines.filter((l, idx) => idx === 0 || l !== lines[idx - 1]);
        return { stdout: lines.length ? lines.join('\n') + '\n' : '', stderr: '', exitCode: 0 };
      }
      case 'uniq': {
        const data = readInputLines(shell, pos, stdin);
        if (!data.ok) return { stdout: '', stderr: `uniq: ${data.err}`, exitCode: 1 };
        const lines = splitLinesKeep(data.text);
        const count = flags.has('c');
        const dupOnly = flags.has('d');
        const out = [];
        for (let i = 0; i < lines.length; i++) {
          let c = 1;
          while (i + 1 < lines.length && lines[i + 1] === lines[i]) { c++; i++; }
          if (dupOnly && c < 2) continue;
          out.push(count ? `${String(c).padStart(7)} ${lines[i]}` : lines[i]);
        }
        return { stdout: out.length ? out.join('\n') + '\n' : '', stderr: '', exitCode: 0 };
      }
      case 'cut': {
        let delim = '\t';
        let fieldsSpec = '';
        const files = [];
        for (let i = 0; i < args.length; i++) {
          if (args[i] === '-d' && args[i + 1]) delim = args[++i];
          else if (args[i].startsWith('-d') && args[i].length > 2) delim = args[i].slice(2);
          else if (args[i] === '-f' && args[i + 1]) fieldsSpec = args[++i];
          else if (args[i].startsWith('-f') && args[i].length > 2) fieldsSpec = args[i].slice(2);
          else files.push(args[i]);
        }
        const data = readInputLines(shell, files, stdin);
        if (!data.ok) return { stdout: '', stderr: `cut: ${data.err}`, exitCode: 1 };
        const idxs = fieldsSpec.split(',').map((n) => parseInt(n, 10) - 1).filter((n) => !isNaN(n) && n >= 0);
        const out = splitLinesKeep(data.text).map((line) => {
          const cols = line.split(delim);
          return idxs.map((idx) => cols[idx] !== undefined ? cols[idx] : '').join(delim);
        });
        return { stdout: out.length ? out.join('\n') + '\n' : '', stderr: '', exitCode: 0 };
      }
      case 'tr': {
        const squeeze = flags.has('s');
        const del = flags.has('d');
        if (del && pos[0]) {
          const re = new RegExp(`[${pos[0]}]`, 'g');
          return { stdout: (stdin || '').replace(re, ''), stderr: '', exitCode: 0 };
        }
        if (squeeze && pos[0]) {
          const ch = pos[0].replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
          let out = (stdin || '').replace(new RegExp(`${ch}+`, 'g'), pos[0]);
          if (pos[1]) out = out.replaceAll(pos[0], pos[1]);
          return { stdout: out, stderr: '', exitCode: 0 };
        }
        if (pos.length >= 2) {
          let out = stdin || '';
          const s1 = pos[0] === 'a-z' ? 'abcdefghijklmnopqrstuvwxyz' : (pos[0] === '[:lower:]' ? 'abcdefghijklmnopqrstuvwxyz' : pos[0]);
          const s2 = pos[1] === 'A-Z' ? 'ABCDEFGHIJKLMNOPQRSTUVWXYZ' : (pos[1] === '[:upper:]' ? 'ABCDEFGHIJKLMNOPQRSTUVWXYZ' : pos[1]);
          for (let i = 0; i < s1.length; i++) {
            out = out.split(s1[i]).join(s2[Math.min(i, s2.length - 1)]);
          }
          return { stdout: out, stderr: '', exitCode: 0 };
        }
        return { stdout: stdin || '', stderr: '', exitCode: 0 };
      }
      case 'tee': {
        const append = flags.has('a');
        for (const f of pos) {
          shell.writeFile(f, stdin || '', append);
        }
        return { stdout: stdin || '', stderr: '', exitCode: 0 };
      }
      case 'sed': {
        const inPlace = flags.has('i');
        const quiet = flags.has('n');
        const expr = pos[0];
        const files = pos.slice(1);
        if (!expr) return { stdout: '', stderr: 'sed: missing expression\n', exitCode: 1 };
        const sub = parseSedExpr(expr);
        const printRange = expr.match(/^(\d+),(\d+)p$/);
        const printSingle = expr.match(/^(\d+)p$/);
        const delMatch = expr.match(/^\/(.+?)\/d$/);

        const transform = (txt) => {
          const lines = splitLinesKeep(txt);
          const out = [];
          lines.forEach((line, idx) => {
            const ln = idx + 1;
            if (delMatch) {
              if (!new RegExp(delMatch[1]).test(line)) out.push(line);
            } else if (printRange) {
              if (ln >= parseInt(printRange[1], 10) && ln <= parseInt(printRange[2], 10)) out.push(line);
            } else if (printSingle) {
              if (ln === parseInt(printSingle[1], 10)) out.push(line);
            } else if (sub) {
              const re = new RegExp(sub.pat, (sub.flags.includes('g') ? 'g' : '') + (sub.flags.includes('i') ? 'i' : ''));
              out.push(line.replace(re, sub.rep));
            } else if (!quiet) {
              out.push(line);
            }
          });
          return out.length ? out.join('\n') + '\n' : '';
        };

        if (files.length === 0) return { stdout: transform(stdin || ''), stderr: '', exitCode: 0 };
        let allOut = '';
        for (const fp of files) {
          const res = shell.resolveNode(fp, true);
          if (!res || res.node.type !== 'file') return { stdout: '', stderr: `sed: can't read ${fp}: No such file\n`, exitCode: 2 };
          const result = transform(res.node.content);
          if (inPlace) res.node.content = result;
          else allOut += result;
        }
        return { stdout: inPlace ? '' : allOut, stderr: '', exitCode: 0 };
      }
      case 'awk': {
        let sep = null;
        const cleanPos = [];
        for (let i = 0; i < args.length; i++) {
          if (args[i] === '-F' && args[i + 1]) sep = args[++i];
          else if (args[i].startsWith('-F') && args[i].length > 2) sep = args[i].slice(2);
          else cleanPos.push(args[i]);
        }
        const prog = cleanPos[0] || '{print}';
        const data = readInputLines(shell, cleanPos.slice(1), stdin);
        if (!data.ok) return { stdout: '', stderr: `awk: ${data.err}`, exitCode: 2 };
        return { stdout: runAwk(prog, data.text, sep), stderr: '', exitCode: 0 };
      }
      case 'xargs': {
        const useNull = flags.has('0');
        let replStr = null;
        const cmdParts = [];
        for (let i = 0; i < args.length; i++) {
          if (args[i] === '-I' && args[i + 1]) replStr = args[++i];
          else if (args[i].startsWith('-I') && args[i].length > 2) replStr = args[i].slice(2);
          else if (args[i] === '-0' || args[i] === '-r') continue;
          else if (args[i] === '-n' && args[i + 1]) { i++; }
          else cmdParts.push(args[i]);
        }
        const baseCmd = cmdParts.length ? cmdParts.join(' ') : 'echo';
        const items = useNull
          ? (stdin || '').split('\0').filter(Boolean)
          : (stdin || '').trim().split(/\s+/).filter(Boolean);
        let out = '';
        let err = '';
        if (replStr) {
          for (const it of items) {
            const r = shell.execute(baseCmd.split(replStr).join(`"${it}"`));
            out += r.stdout || '';
            err += r.stderr || '';
          }
        } else if (items.length > 0) {
          const r = shell.execute(baseCmd + ' ' + items.map((it) => `"${it}"`).join(' '));
          out = r.stdout || '';
          err = r.stderr || '';
        }
        return { stdout: out, stderr: err, exitCode: 0 };
      }
      case 'chmod': {
        if (pos.length < 2) return { stdout: '', stderr: 'chmod: missing operand\n', exitCode: 1 };
        const modeSpec = pos[0];
        const targets = pos.slice(1);
        for (const t of targets) {
          const res = shell.resolveNode(t, true);
          if (!res) return { stdout: '', stderr: `chmod: cannot access '${t}': No such file or directory\n`, exitCode: 1 };
          if (/^[0-7]{3,4}$/.test(modeSpec)) {
            res.node.mode = octalToMode(modeSpec, res.node.type === 'dir');
          } else {
            res.node.mode = applySymbolicMode(res.node.mode, modeSpec);
          }
        }
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'chown': {
        if (pos.length < 2) return { stdout: '', stderr: 'chown: missing operand\n', exitCode: 1 };
        const [owner, grp] = pos[0].split(':');
        for (const t of pos.slice(1)) {
          const res = shell.resolveNode(t, false);
          if (!res) return { stdout: '', stderr: `chown: cannot access '${t}': No such file or directory\n`, exitCode: 1 };
          if (owner) res.node.owner = owner;
          if (grp) res.node.group = grp;
        }
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'umask': {
        if (!pos[0]) return { stdout: shell.umaskVal + '\n', stderr: '', exitCode: 0 };
        shell.umaskVal = pos[0].padStart(4, '0');
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'ln': {
        const sym = flags.has('s');
        if (pos.length < 2) return { stdout: '', stderr: 'ln: missing file operand\n', exitCode: 1 };
        const target = pos[0];
        const linkName = pos[1];
        const p = shell.resolveParent(linkName);
        if (!p) return { stdout: '', stderr: `ln: failed to create link '${linkName}': No such file or directory\n`, exitCode: 1 };
        if (sym) {
          p.parent.children[p.name] = global.LinuxLab.vfsLink(target);
        } else {
          const src = shell.resolveNode(target, true);
          if (!src || src.node.type === 'dir') return { stdout: '', stderr: `ln: ${target}: hard link not allowed\n`, exitCode: 1 };
          p.parent.children[p.name] = src.node; // shared object reference simulates shared inode!
        }
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'getfacl': {
        if (!pos[0]) return { stdout: '', stderr: 'getfacl: missing file\n', exitCode: 1 };
        const r = shell.resolveNode(pos[0], true);
        if (!r) return { stdout: '', stderr: `getfacl: ${pos[0]}: No such file or directory\n`, exitCode: 1 };
        const extra = (r.node.acl || []).join('\n');
        return { stdout: `# file: ${pos[0]}\n# owner: ${r.node.owner}\n# group: ${r.node.group}\nuser::rw-\ngroup::r--\n${extra ? extra + '\n' : ''}other::r--\n`, stderr: '', exitCode: 0 };
      }
      case 'setfacl': {
        const mIdx = args.indexOf('-m');
        const spec = mIdx !== -1 ? args[mIdx + 1] : '';
        const target = pos[pos.length - 1];
        const r = shell.resolveNode(target, true);
        if (!r) return { stdout: '', stderr: `setfacl: ${target}: No such file or directory\n`, exitCode: 1 };
        r.node.acl = r.node.acl || [];
        if (spec) r.node.acl.push(spec);
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'groups': {
        const u = pos[0] || shell.user;
        const info = shell.users[u];
        if (!info) return { stdout: '', stderr: `groups: '${u}': no such user\n`, exitCode: 1 };
        return { stdout: `${u} : ${info.groups.join(' ')}\n`, stderr: '', exitCode: 0 };
      }
      case 'useradd': {
        const uname = pos[pos.length - 1];
        if (!uname) return { stdout: '', stderr: 'useradd: missing username\n', exitCode: 2 };
        const uid = 1001 + Object.keys(shell.users).length;
        shell.users[uname] = { uid, gid: uid, groups: [uname] };
        const pNode = shell.resolveNode('/etc/passwd', true);
        if (pNode) pNode.node.content += `${uname}:x:${uid}:${uid}::/home/${uname}:${args.includes('/usr/sbin/nologin') ? '/usr/sbin/nologin' : '/bin/bash'}\n`;
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'usermod': {
        const gIdx = args.indexOf('-aG');
        const uname = pos[pos.length - 1];
        if (gIdx !== -1 && args[gIdx + 1] && shell.users[uname]) {
          args[gIdx + 1].split(',').forEach((g) => {
            if (!shell.users[uname].groups.includes(g)) shell.users[uname].groups.push(g);
          });
        }
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'sudo': {
        if (args.length === 0) return { stdout: '', stderr: 'usage: sudo command...\n', exitCode: 1 };
        if (args[0] === '-l') {
          return { stdout: `User ${shell.user} may run the following commands on ${shell.host}:\n    (ALL : ALL) ALL\n`, stderr: '', exitCode: 0 };
        }
        const prevUser = shell.user;
        shell.user = 'root';
        const res = shell.executePipeline(args.join(' '));
        shell.user = prevUser;
        return res;
      }
      case 'kill': {
        const target = pos[pos.length - 1];
        if (flags.has('l')) return { stdout: ' 1) SIGHUP  2) SIGINT  9) SIGKILL  15) SIGTERM\n', stderr: '', exitCode: 0 };
        const pid = parseInt(target, 10);
        const idx = shell.processes.findIndex((p) => p.pid === pid);
        if (idx === -1) return { stdout: '', stderr: `bash: kill: (${target}) - No such process\n`, exitCode: 1 };
        shell.processes.splice(idx, 1);
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'pkill': {
        const pat = pos[pos.length - 1];
        if (!pat) return { stdout: '', stderr: 'pkill: missing pattern\n', exitCode: 1 };
        shell.processes = shell.processes.filter((p) => !p.cmd.includes(pat));
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'nice':
      case 'renice':
      case 'nohup': {
        if (cmd === 'nohup' && args.length > 0) return { stdout: `nohup: appending output to 'nohup.out'\n`, stderr: '', exitCode: 0 };
        return { stdout: `${cmd}: priority adjusted successfully\n`, stderr: '', exitCode: 0 };
      }
      case 'top': {
        const lines = [
          'top - 00:45:00 up 4:07,  1 user,  load average: 1.42, 0.98, 0.74',
          `Tasks: ${shell.processes.length} total,   1 running, ${shell.processes.length - 1} sleeping,   0 zombie`,
          'MiB Mem :   8000.0 total,   1387.0 free,   3345.0 used,   3268.0 buff/cache',
          '  PID USER      PR  NI    VIRT    RES  S  %CPU  %MEM COMMAND'
        ];
        shell.processes.forEach((p) => lines.push(`${String(p.pid).padStart(5)} ${p.user.padEnd(9)} 20   0  512000  42000  ${p.stat[0]} ${p.cpu.padStart(5)} ${p.mem.padStart(5)} ${p.cmd}`));
        return { stdout: lines.join('\n') + '\n', stderr: '', exitCode: 0 };
      }
      case 'systemctl': {
        const sub = pos[0] || 'list-units';
        const unit = pos[1];
        if (sub === 'daemon-reload') return { stdout: '', stderr: '', exitCode: 0 };
        if (sub === 'list-units' || sub === 'list-timers') {
          const rows = Object.entries(shell.services)
            .filter(([k]) => sub === 'list-timers' ? k.endsWith('.timer') : true)
            .map(([k, v]) => `  ${k.padEnd(22)} loaded ${v.active.padEnd(8)} ${v.sub.padEnd(10)} ${v.desc}`);
          return { stdout: rows.join('\n') + '\n', stderr: '', exitCode: 0 };
        }
        if (unit && !shell.services[unit] && !unit.includes('.')) {
          const withSvc = unit + '.service';
          if (shell.services[withSvc]) return extDispatch(shell, 'systemctl', [sub, withSvc], stdin);
        }
        if (!unit || !shell.services[unit]) {
          // Check if unit file exists in /etc/systemd/system/
          const uRes = unit ? shell.resolveNode(`/etc/systemd/system/${unit}`, true) : null;
          if (uRes && uRes.node.type === 'file') {
            shell.services[unit] = { active: 'inactive', sub: 'dead', enabled: 'disabled', desc: unit, pid: 0 };
          } else {
            return { stdout: '', stderr: `Unit ${unit || ''} could not be found.\n`, exitCode: 4 };
          }
        }
        const svc = shell.services[unit];
        if (sub === 'start' || sub === 'restart') { svc.active = 'active'; svc.sub = 'running'; return { stdout: '', stderr: '', exitCode: 0 }; }
        if (sub === 'stop') { svc.active = 'inactive'; svc.sub = 'dead'; return { stdout: '', stderr: '', exitCode: 0 }; }
        if (sub === 'enable') {
          svc.enabled = 'enabled';
          if (flags.has('now')) { svc.active = 'active'; svc.sub = 'running'; }
          return { stdout: `Created symlink /etc/systemd/system/multi-user.target.wants/${unit}.\n`, stderr: '', exitCode: 0 };
        }
        if (sub === 'cat') {
          const cf = shell.resolveNode(`/etc/systemd/system/${unit}`, true);
          return cf ? { stdout: `# /etc/systemd/system/${unit}\n${cf.node.content}\n`, stderr: '', exitCode: 0 }
                    : { stdout: '', stderr: `No files found for ${unit}.\n`, exitCode: 1 };
        }
        if (sub === 'disable') { svc.enabled = 'disabled'; return { stdout: '', stderr: '', exitCode: 0 }; }
        if (sub === 'status') {
          return {
            stdout: `● ${unit} - ${svc.desc}\n     Loaded: loaded (/etc/systemd/system/${unit}; ${svc.enabled})\n     Active: ${svc.active} (${svc.sub})\n`,
            stderr: '',
            exitCode: svc.active === 'active' ? 0 : 3
          };
        }
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'journalctl': {
        let uFilter = null;
        let pFilter = null;
        for (let i = 0; i < args.length; i++) {
          if (args[i] === '-u' && args[i + 1]) uFilter = args[++i].replace('.service', '');
          else if (args[i] === '-p' && args[i + 1]) pFilter = args[++i];
        }
        const rows = shell.journal.filter((j) => (!uFilter || j.unit === uFilter) && (!pFilter || j.prio === pFilter));
        return { stdout: rows.map((r) => `Nov 15 00:40:12 ${shell.host} ${r.unit}[${shell.pid}]: [${r.prio.toUpperCase()}] ${r.msg}`).join('\n') + '\n', stderr: '', exitCode: 0 };
      }
      case 'crontab': {
        if (flags.has('l')) return { stdout: shell.cronJobs.join('\n') + '\n', stderr: '', exitCode: 0 };
        return { stdout: 'crontab: use crontab -l to inspect scheduled jobs\n', stderr: '', exitCode: 0 };
      }
      case 'timedatectl': {
        return {
          stdout: '               Local time: Sun 2026-11-15 00:45:00 CST\n           Universal time: Sun 2026-11-15 06:45:00 UTC\n                 RTC time: Sun 2026-11-15 06:45:00\n                Time zone: America/Chicago (CST, -0600)\nSystem clock synchronized: yes\n              NTP service: active\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'chronyc': {
        return {
          stdout: 'Reference ID    : A9FEA97B (ntp.ubuntu.com)\nStratum         : 2\nSystem time     : 0.000142 seconds fast of NTP time\nLast offset     : +0.000081 seconds\nLeap status     : Normal\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'apt':
      case 'apt-cache':
      case 'dpkg': {
        const sub = pos[0];
        if (cmd === 'dpkg' && (flags.has('l') || sub === '-l')) {
          return { stdout: Object.entries(shell.packages).map(([k, v]) => `ii  ${k.padEnd(16)} ${v.padEnd(20)} amd64`).join('\n') + '\n', stderr: '', exitCode: 0 };
        }
        if (sub === 'update') return { stdout: 'Hit:1 http://archive.ubuntu.com/ubuntu noble InRelease\nReading package lists... Done\n', stderr: '', exitCode: 0 };
        if (sub === 'install' && pos[1]) {
          shell.packages[pos[1]] = '1.0.0-ubuntu1';
          return { stdout: `Setting up ${pos[1]} (1.0.0-ubuntu1) ...\n`, stderr: '', exitCode: 0 };
        }
        if (sub === 'policy' && pos[1]) {
          return { stdout: `${pos[1]}:\n  Installed: ${shell.packages[pos[1]] || '(none)'}\n  Candidate: ${shell.packages[pos[1]] || '1.0.0-ubuntu1'}\n`, stderr: '', exitCode: 0 };
        }
        return { stdout: `Installed packages: ${Object.keys(shell.packages).join(', ')}\n`, stderr: '', exitCode: 0 };
      }
      case 'tar': {
        const flagChars = args.filter((a) => a.startsWith('-')).join('');
        if (flagChars.includes('c')) {
          const outArc = pos[0];
          const src = pos[1];
          const srcRes = src ? shell.resolveNode(src, true) : null;
          if (!outArc || !srcRes) return { stdout: '', stderr: 'tar: invalid archive arguments\n', exitCode: 2 };
          const manifest = JSON.stringify({ root: srcRes.name, node: srcRes.node });
          shell.writeFile(outArc, `TARGZ::${manifest}`, false);
          return { stdout: '', stderr: '', exitCode: 0 };
        }
        if (flagChars.includes('t') && pos[0]) {
          const r = shell.resolveNode(pos[0], true);
          if (!r) return { stdout: '', stderr: `tar: ${pos[0]}: Cannot open\n`, exitCode: 2 };
          return { stdout: `${pos[0]} contents verified (valid archive)\n`, stderr: '', exitCode: 0 };
        }
        if (flagChars.includes('x') && pos[0]) {
          const r = shell.resolveNode(pos[0], true);
          if (!r) return { stdout: '', stderr: `tar: ${pos[0]}: Cannot open\n`, exitCode: 2 };
          if (r.node.content.startsWith('TARGZ::')) {
            const parsed = JSON.parse(r.node.content.slice(7));
            const cIdx = args.indexOf('-C');
            const destDir = cIdx !== -1 && args[cIdx + 1] ? args[cIdx + 1] : '.';
            const dRes = shell.resolveNode(destDir, true);
            if (dRes && dRes.node.type === 'dir') dRes.node.children[parsed.root] = parsed.node;
          }
          return { stdout: '', stderr: '', exitCode: 0 };
        }
        return { stdout: '', stderr: 'tar: specify -c, -t, or -x\n', exitCode: 2 };
      }
      case 'sha256sum': {
        if (!pos[0]) return { stdout: '', stderr: 'sha256sum: missing file\n', exitCode: 1 };
        const r = shell.resolveNode(pos[0], true);
        if (!r) return { stdout: '', stderr: `sha256sum: ${pos[0]}: No such file or directory\n`, exitCode: 1 };
        let hash = 0;
        for (let i = 0; i < r.node.content.length; i++) hash = ((hash << 5) - hash + r.node.content.charCodeAt(i)) | 0;
        const hex = Math.abs(hash).toString(16).padStart(64, 'a');
        return { stdout: `${hex}  ${pos[0]}\n`, stderr: '', exitCode: 0 };
      }
      case 'df': {
        const inodes = flags.has('i');
        if (inodes) {
          const hdr = 'Filesystem                 Inodes  IUsed    IFree IUse% Mounted on\n';
          return { stdout: hdr + shell.mounts.map((m) => `${m.dev.padEnd(25)} ${m.itot.padStart(7)} ${m.iused.padStart(6)} ${String(parseInt(m.itot, 10) - parseInt(m.iused, 10)).padStart(8)} ${m.ipct.padStart(4)} ${m.target}`).join('\n') + '\n', stderr: '', exitCode: 0 };
        }
        const hdr = 'Filesystem                Size  Used Avail Use% Mounted on\n';
        return { stdout: hdr + shell.mounts.map((m) => `${m.dev.padEnd(25)} ${m.size.padStart(4)} ${m.used.padStart(5)} ${m.avail.padStart(5)} ${m.pct.padStart(4)} ${m.target}`).join('\n') + '\n', stderr: '', exitCode: 0 };
      }
      case 'du': {
        const target = pos[0] || '.';
        const r = shell.resolveNode(target, true);
        if (!r) return { stdout: '', stderr: `du: cannot access '${target}': No such file or directory\n`, exitCode: 1 };
        return { stdout: `4.0K\t${target}\n`, stderr: '', exitCode: 0 };
      }
      case 'lsblk': {
        return {
          stdout: 'NAME               MAJ:MIN RM  SIZE RO TYPE MOUNTPOINTS\nvda                252:0    0   60G  0 disk \n├─vda1             252:1    0    1G  0 part /boot\n└─vda2             252:2    0   59G  0 part \n  ├─vg0-root       253:0    0   30G  0 lvm  /\n  └─vg0-aegis_data 253:1    0   20G  0 lvm  /var/lib/aegis\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'mount': {
        if (pos.length === 0) return { stdout: shell.mounts.map((m) => `${m.dev} on ${m.target} type ${m.fs} (rw,relatime)`).join('\n') + '\n', stderr: '', exitCode: 0 };
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'umount': {
        const target = pos[0];
        if (target === '/var/lib/aegis' && shell.openDeletedFiles.length > 0) {
          return { stdout: '', stderr: `umount: ${target}: target is busy.\n`, exitCode: 32 };
        }
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'free': {
        return {
          stdout: '               total        used        free      shared  buff/cache   available\nMem:           7.8Gi       3.3Gi       1.4Gi       120Mi       3.1Gi       4.6Gi\nSwap:          2.0Gi          0B       2.0Gi\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'lsof': {
        if (args.includes('+L1')) {
          return {
            stdout: 'COMMAND    PID  USER   FD   TYPE DEVICE    SIZE/OFF NLINK NODE NAME\n' +
              shell.openDeletedFiles.map((f) => `${f.cmd.padEnd(10)} ${String(f.pid).padEnd(4)} ${f.user.padEnd(6)} ${f.fd.padEnd(4)} REG  253,1 ${f.size.padStart(11)}     0  912 ${f.name}`).join('\n') + '\n',
            stderr: '',
            exitCode: 0
          };
        }
        return {
          stdout: 'COMMAND    PID  USER   FD   TYPE DEVICE SIZE/OFF NODE NAME\nsshd       902  root    3u  IPv4  18210      0t0  TCP *:22 (LISTEN)\nnginx     1205  www-data 6u IPv4  19402      0t0  TCP *:443 (LISTEN)\naegis-api 1120  aegis   8u  IPv4  20118      0t0  TCP 127.0.0.1:8443 (LISTEN)\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'fuser': {
        return { stdout: `${pos[0] || '/var/lib/aegis'}:  1120\n`, stderr: '', exitCode: 0 };
      }
      case 'pvs': return { stdout: '  PV         VG  Fmt  Attr PSize  PFree\n  /dev/vda2  vg0 lvm2 a--  59.00g 9.00g\n', stderr: '', exitCode: 0 };
      case 'vgs': return { stdout: '  VG  #PV #LV #SN Attr   VSize  VFree\n  vg0   1   2   0 wz--n- 59.00g 9.00g\n', stderr: '', exitCode: 0 };
      case 'lvs': return { stdout: '  LV         VG  Attr       LSize\n' + shell.lvs.map((l) => `  ${l.lv.padEnd(10)} ${l.vg.padEnd(3)} ${l.attr} ${l.size}`).join('\n') + '\n', stderr: '', exitCode: 0 };
      case 'lvextend': {
        const target = pos[pos.length - 1] || '';
        const lv = shell.lvs.find((l) => target.includes(l.lv));
        if (lv) lv.size = '25.00g';
        return { stdout: `  Size of logical volume vg0/${lv ? lv.lv : 'aegis_data'} changed from 20.00 GiB to 25.00 GiB.\n  Logical volume successfully resized.\n`, stderr: '', exitCode: 0 };
      }
      case 'lsmod': return { stdout: 'Module                  Size  Used by\n' + shell.modules.map((m) => `${m.padEnd(20)}  65536  1`).join('\n') + '\n', stderr: '', exitCode: 0 };
      case 'modprobe': {
        const mod = pos[pos.length - 1];
        if (flags.has('r')) shell.modules = shell.modules.filter((m) => m !== mod);
        else if (mod && !shell.modules.includes(mod)) shell.modules.push(mod);
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'dmesg': {
        return {
          stdout: '[Sun Nov 15 00:00:01 2026] Linux version 6.8.0-45-generic\n[Sun Nov 15 00:00:02 2026] nvme nvme0: pci function 0000:00:04.0\n[Sun Nov 15 00:07:44 2026] Out of memory: Killed process 4821 (gradle-worker-daemon)\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'lspci': return { stdout: '00:00.0 Host bridge: Intel Corporation 440FX\n00:03.0 Ethernet controller: Red Hat, Inc. Virtio network device\n00:04.0 Non-Volatile memory controller: Red Hat, Inc. Virtio 1.0 block/nvme\n', stderr: '', exitCode: 0 };
      case 'udevadm': return { stdout: 'DEVPATH=/devices/pci0000:00/0000:00:03.0/net/eth0\nSUBSYSTEM=net\nID_NET_DRIVER=virtio_net\n', stderr: '', exitCode: 0 };
      case 'ip': {
        const sub = pos[0] || 'addr';
        if (sub.startsWith('r')) {
          return { stdout: 'default via 10.20.0.1 dev eth0 proto dhcp src 10.20.0.45 metric 100\n10.20.0.0/24 dev eth0 proto kernel scope link src 10.20.0.45\n', stderr: '', exitCode: 0 };
        }
        return {
          stdout: '1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 state UNKNOWN\n    inet 127.0.0.1/8 scope host lo\n2: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 state UP\n    inet 10.20.0.45/24 brd 10.20.0.255 scope global eth0\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'ss': {
        return {
          stdout: 'Netid State  Recv-Q Send-Q Local Address:Port  Peer Address:Port Process\ntcp   LISTEN 0      128          0.0.0.0:22         0.0.0.0:*     users:(("sshd",pid=902,fd=3))\ntcp   LISTEN 0      511          0.0.0.0:443        0.0.0.0:*     users:(("nginx",pid=1205,fd=6))\ntcp   LISTEN 0      128        127.0.0.1:8443       0.0.0.0:*     users:(("aegis-api",pid=1120,fd=8))\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'ping': {
        const host = pos[pos.length - 1] || '127.0.0.1';
        return { stdout: `PING ${host} (${host}) 56(84) bytes of data.\n64 bytes from ${host}: icmp_seq=1 ttl=64 time=0.42 ms\n64 bytes from ${host}: icmp_seq=2 ttl=64 time=0.39 ms\n--- ${host} ping statistics ---\n2 packets transmitted, 2 received, 0% packet loss\n`, stderr: '', exitCode: 0 };
      }
      case 'dig': {
        const dom = pos[0] || 'aegis.internal';
        return { stdout: `; <<>> DiG 9.18 <<>> ${dom}\n;; ANSWER SECTION:\n${dom}.\t\t60\tIN\tA\t10.20.0.45\n;; SERVER: 127.0.0.53#53(systemd-resolved)\n`, stderr: '', exitCode: 0 };
      }
      case 'curl': {
        const url = pos.find((p) => p.startsWith('http')) || 'http://127.0.0.1:8443/healthz';
        if (flags.has('I')) return { stdout: 'HTTP/1.1 200 OK\nServer: nginx/1.24.0\nContent-Type: application/json\n', stderr: '', exitCode: 0 };
        return { stdout: '{"status":"healthy","service":"aegis-api","port":8443}\n', stderr: '', exitCode: 0 };
      }
      case 'traceroute': return { stdout: 'traceroute to 10.20.0.10, 30 hops max\n 1  10.20.0.1  0.412 ms\n 2  10.20.0.10  0.890 ms\n', stderr: '', exitCode: 0 };
      case 'ssh-keygen': {
        const fIdx = args.indexOf('-f');
        const outPath = fIdx !== -1 && args[fIdx + 1] ? args[fIdx + 1] : '~/.ssh/id_ed25519';
        shell.execute('mkdir -p ~/.ssh');
        shell.writeFile(outPath, '-----BEGIN OPENSSH PRIVATE KEY-----\n[simulated ed25519 key]\n-----END OPENSSH PRIVATE KEY-----\n', false);
        shell.writeFile(outPath + '.pub', 'ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAISimulatedKey android-dev@linux-lab-01\n', false);
        return { stdout: `Your identification has been saved in ${outPath}\nYour public key has been saved in ${outPath}.pub\n`, stderr: '', exitCode: 0 };
      }
      case 'ssh':
      case 'scp':
      case 'rsync': {
        return { stdout: `[${cmd}] Simulated encrypted transfer/session completed cleanly (0 errors).\n`, stderr: '', exitCode: 0 };
      }
      case 'nft': {
        if (args.join(' ').includes('list ruleset')) {
          return { stdout: shell.nftRules.join('\n') + '\n', stderr: '', exitCode: 0 };
        }
        shell.nftRules.push(`# added: ${args.join(' ')}`);
        return { stdout: '', stderr: '', exitCode: 0 };
      }
      case 'ufw': {
        if (pos[0] === 'status') {
          return { stdout: `Status: ${shell.ufwActive ? 'active' : 'inactive'}\nTo                         Action      From\n--                         ------      ----\n` + shell.ufwRules.map((r) => `${r.port.padEnd(26)} ${r.action.padEnd(11)} ${r.from}`).join('\n') + '\n', stderr: '', exitCode: 0 };
        }
        if (pos[0] === 'allow' && pos[1]) {
          shell.ufwRules.push({ port: pos[1], action: 'ALLOW IN', from: 'Anywhere' });
          return { stdout: 'Rule added\n', stderr: '', exitCode: 0 };
        }
        return { stdout: 'Firewall is active and enabled on system startup\n', stderr: '', exitCode: 0 };
      }
      case 'openssl': {
        return { stdout: 'subject=CN = aegis.internal\nissuer=CN = Aegis Internal CA\nnotBefore=Nov 15 00:00:00 2026 GMT\nnotAfter=Feb 13 00:00:00 2027 GMT\n', stderr: '', exitCode: 0 };
      }
      case 'wg': return { stdout: 'interface: wg0\n  public key: xTIBA5rboUvnH4htodjb6e697QjLERt1NAB4mZqp8Dg=\n  listening port: 51820\npeer: 9zKq8X2...\n  endpoint: 203.0.113.10:51820\n  allowed ips: 10.99.0.0/24\n', stderr: '', exitCode: 0 };
      case 'tcpdump': return { stdout: 'tcpdump: listening on eth0, link-type EN10MB\n00:45:01.102 IP 10.20.0.14.54122 > 10.20.0.45.443: Flags [S], seq 1001, win 64240\n00:45:01.102 IP 10.20.0.45.443 > 10.20.0.14.54122: Flags [S.], seq 8821, ack 1002\n2 packets captured\n', stderr: '', exitCode: 0 };
      case 'shellcheck': {
        if (!pos[0]) return { stdout: '', stderr: 'No files specified.\n', exitCode: 1 };
        const r = shell.resolveNode(pos[0], true);
        if (!r) return { stdout: '', stderr: `${pos[0]}: does not exist\n`, exitCode: 2 };
        const txt = r.node.content;
        const warns = [];
        if (!txt.includes('set -euo pipefail') && !txt.includes('set -e')) warns.push('SC2164/Info: Consider adding "set -euo pipefail" for fail-fast safety.');
        if (/rm\s+-rf\s+\$[A-Za-z_]+/.test(txt)) warns.push('SC2086/SC2115: Unquoted variable expansion in rm -rf! Quote and guard with "${VAR:?}".');
        return { stdout: warns.length ? warns.join('\n') + '\n' : '', stderr: '', exitCode: warns.length ? 1 : 0 };
      }
      case 'bash':
      case 'sh': {
        if (!pos[0]) return { stdout: '', stderr: '', exitCode: 0 };
        const r = shell.resolveNode(pos[0], true);
        if (!r || r.node.type !== 'file') return { stdout: '', stderr: `${cmd}: ${pos[0]}: No such file or directory\n`, exitCode: 127 };
        let out = '';
        let err = '';
        let strict = false;
        let code = 0;
        for (const rawLine of r.node.content.split('\n')) {
          const line = rawLine.trim();
          if (!line || line.startsWith('#')) continue;
          if (line.startsWith('set -e')) { strict = true; continue; }
          const res = shell.execute(line);
          out += res.stdout || '';
          err += res.stderr || '';
          code = res.exitCode;
          if (strict && code !== 0) break;
        }
        return { stdout: out, stderr: err, exitCode: code };
      }
      case 'python3': {
        if (args[0] === '-c' && args[1]) {
          return { stdout: `[python3] executed inline snippet -> OK\n`, stderr: '', exitCode: 0 };
        }
        return { stdout: 'Python 3.12.3 (Linux system automation runtime)\n', stderr: '', exitCode: 0 };
      }
      case 'sysctl': {
        if (flags.has('a')) {
          return { stdout: Object.entries(shell.sysctl).map(([k, v]) => `${k} = ${v}`).join('\n') + '\n', stderr: '', exitCode: 0 };
        }
        if (flags.has('w') || (pos[0] && pos[0].includes('='))) {
          const kv = pos[0] || '';
          const [k, v] = kv.split('=');
          if (k && v !== undefined) { shell.sysctl[k.trim()] = v.trim(); return { stdout: `${k.trim()} = ${v.trim()}\n`, stderr: '', exitCode: 0 }; }
        }
        if (pos[0] && shell.sysctl[pos[0]] !== undefined) {
          return { stdout: `${pos[0]} = ${shell.sysctl[pos[0]]}\n`, stderr: '', exitCode: 0 };
        }
        return { stdout: Object.entries(shell.sysctl).map(([k, v]) => `${k} = ${v}`).join('\n') + '\n', stderr: '', exitCode: 0 };
      }
      case 'vmstat': return { stdout: 'procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----\n r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st\n 2  0      0 1420312 312000 3117888   0    0    12    48  410  890 18  4 77  1  0\n', stderr: '', exitCode: 0 };
      case 'iostat': return { stdout: 'Device            r/s     w/s     rkB/s     wkB/s  await  %util\nvda              4.20   38.50    128.00   1840.00   1.85   8.40\n', stderr: '', exitCode: 0 };
      case 'sar': return { stdout: '00:35:01 CPU %user %system %iowait %idle\n00:45:01 all 18.20    4.10    1.05   76.65\n', stderr: '', exitCode: 0 };
      case 'getenforce': return { stdout: shell.selinuxMode + '\n', stderr: '', exitCode: 0 };
      case 'setenforce': { shell.selinuxMode = pos[0] === '0' || pos[0] === 'Permissive' ? 'Permissive' : 'Enforcing'; return { stdout: '', stderr: '', exitCode: 0 }; }
      case 'aa-status': return { stdout: 'apparmor module is loaded.\n18 profiles are loaded.\n16 profiles are in enforce mode.\n', stderr: '', exitCode: 0 };
      case 'ausearch': return { stdout: 'type=AVC msg=audit(1763189100.12:41): avc:  denied  { read } for  pid=1120 comm="aegis-api" name="secret.key" scontext=system_u:system_r:aegis_t:s0\n', stderr: '', exitCode: 0 };
      case 'capsh': return { stdout: 'Current: cap_net_bind_service=ep\nBounding set =cap_chown,cap_dac_override,cap_net_bind_service\n', stderr: '', exitCode: 0 };
      case 'strace': {
        if (flags.has('c')) {
          return {
            stdout: '% time     seconds  usecs/call     calls    errors syscall\n------ ----------- ----------- --------- --------- ----------------\n 61.20    0.041200          82       502        14 futex\n 24.50    0.016500          15      1100         0 epoll_wait\n 14.30    0.009620          12       802         3 openat\n',
            stderr: '',
            exitCode: 0
          };
        }
        return { stdout: 'openat(AT_FDCWD, "/etc/aegis/prod.conf", O_RDONLY) = -1 ENOENT (No such file or directory)\nwrite(2, "Fatal: missing /etc/aegis/prod.conf\\n", 36) = 36\nexit_group(1) = ?\n', stderr: '', exitCode: 0 };
      }
      case 'ltrace': return { stdout: 'getenv("AEGIS_ENV") = nil\nfopen("/etc/aegis/prod.conf", "r") = 0\n', stderr: '', exitCode: 0 };
      case 'resolvectl': {
        if (pos[0] === 'query' && pos[1]) {
          return { stdout: `${pos[1]}: 10.20.0.45\n-- Information acquired via protocol DNS in 1.2ms.\n`, stderr: '', exitCode: 0 };
        }
        return {
          stdout: 'Global\n         Protocols: +LLMNR +mDNS -DNSOverTLS DNSSEC=no/unsupported\nLink 2 (eth0)\n    Current Scopes: DNS\nCurrent DNS Server: 10.20.0.2\n       DNS Servers: 10.20.0.2 1.1.1.1\n        DNS Domain: aegis.internal\n',
          stderr: '',
          exitCode: 0
        };
      }
      case 'cryptsetup': {
        const sub = pos[0] || 'luksDump';
        if (sub === 'status') {
          return { stdout: '/dev/mapper/aegis_crypt is active and is in use.\n  type:    LUKS2\n  cipher:  aes-xts-plain64\n  keysize: 512 bits\n', stderr: '', exitCode: 0 };
        }
        return { stdout: 'LUKS header information\nVersion:       \t2\nCipher:        \taes-xts-plain64\nPBKDF:         \targon2id\nUUID:          \td4c8a1f2-7b30-4e92-a119-8840c0ffee01\n', stderr: '', exitCode: 0 };
      }
      case 'btrfs': {
        if (pos[0] === 'subvolume') {
          return { stdout: 'ID 256 gen 142 top level 5 path @\nID 257 gen 144 top level 5 path @var_log\nID 258 gen 145 top level 5 path @snapshots/daily-2026-11-15\n', stderr: '', exitCode: 0 };
        }
        return { stdout: 'Data, single: total=18.00GiB, used=7.42GiB\nMetadata, DUP: total=1.00GiB, used=312.00MiB\n', stderr: '', exitCode: 0 };
      }
      case 'logrotate': {
        const target = pos[0] || '/etc/logrotate.d/aegis';
        const lr = shell.resolveNode(target, true);
        if (lr && lr.node.type === 'file') {
          const txt = lr.node.content;
          if (!txt.includes('rotate') || (!txt.includes('postrotate') && !txt.includes('copytruncate'))) {
            return { stdout: `reading config file ${target}\n`, stderr: `error: ${target}: missing rotate count or postrotate/copytruncate handler\n`, exitCode: 1 };
          }
          return { stdout: `reading config file ${target}\nrotating pattern: /var/log/aegis/*.log -> OK (verified)\n`, stderr: '', exitCode: 0 };
        }
        return { stdout: 'logrotate 3.21.0: dry-run check complete (0 errors)\n', stderr: '', exitCode: 0 };
      }
      case 'perf': return { stdout: 'Samples: 4K of event \'cycles\', Event count: 2948102941\n 42.10%  aegis-api  [.] crypto_sha256_block\n 18.40%  [kernel]   [k] tcp_sendmsg\n', stderr: '', exitCode: 0 };
      case 'bpftrace': return { stdout: 'Attaching 1 probe...\n@syscalls[aegis-api]: 1842\n@syscalls[nginx]: 610\n', stderr: '', exitCode: 0 };
      case 'unshare': return { stdout: '[unshare] Isolated UTS/PID/Mount namespace spawned: hostname=container-01 pid=1\n', stderr: '', exitCode: 0 };
      case 'podman':
      case 'docker': return { stdout: 'CONTAINER ID  IMAGE                     STATUS        PORTS                   NAMES\na91f3b2c0d11  localhost/aegis-api:1.0   Up 4 hours    127.0.0.1:8443->8443    aegis-prod\n', stderr: '', exitCode: 0 };
      case 'virsh': return { stdout: ' Id   Name             State\n--------------------------------\n 1    aegis-bastion    running\n 2    aegis-app-01     running\n 3    aegis-obs-01     running\n', stderr: '', exitCode: 0 };
      case 'cloud-init': return { stdout: 'status: done\n', stderr: '', exitCode: 0 };
      case 'ansible':
      case 'ansible-playbook': return { stdout: 'PLAY RECAP *********************************************************************\naegis-bastion              : ok=12   changed=0    unreachable=0    failed=0\naegis-app-01               : ok=18   changed=0    unreachable=0    failed=0\n', stderr: '', exitCode: 0 };
      case 'nginx': {
        const cNode = shell.resolveNode('/etc/nginx/conf.d/aegis.conf', true);
        if (cNode && cNode.node.type === 'file') {
          const txt = cNode.node.content;
          if (txt.includes('BROKEN') || !txt.includes('proxy_pass') || !txt.includes(';')) {
            return { stdout: '', stderr: 'nginx: [emerg] invalid upstream directive in /etc/nginx/conf.d/aegis.conf\nnginx: configuration file /etc/nginx/nginx.conf test failed\n', exitCode: 1 };
          }
        }
        return { stdout: 'nginx: the configuration file /etc/nginx/nginx.conf syntax is ok\nnginx: configuration file /etc/nginx/nginx.conf test is successful\n', stderr: '', exitCode: 0 };
      }
      case 'update-grub':
      case 'update-initramfs':
      case 'chroot':
      case 'grubby':
        return { stdout: `[${cmd}] Completed successfully for kernel 6.8.0-45-generic.\n`, stderr: '', exitCode: 0 };
      default:
        return null;
    }
  }

  global.LinuxLabExtInit = initShellState;
  global.LinuxLabExtDispatch = extDispatch;
})(window);
