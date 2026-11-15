/* ==========================================================================
   Linux Mastery 2027 — Interactive Linux Shell, Lab Checker & Quiz Engine
   Pure vanilla JS (no modules/fetch) so everything runs seamlessly via file://
   ========================================================================== */

(function (global) {
  'use strict';

  const STORAGE_KEY = 'linux_mastery_2027_v1';

  /* ------------------------------------------------------------------------
     1. Progress Persistence (localStorage)
     ------------------------------------------------------------------------ */
  const ProgressStore = {
    load() {
      try {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (!raw) return { lessons: {} };
        const parsed = JSON.parse(raw);
        return parsed && typeof parsed === 'object' ? parsed : { lessons: {} };
      } catch (_) {
        return { lessons: {} };
      }
    },
    save(data) {
      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
      } catch (_) {
        /* ignore storage quota or privacy restrictions */
      }
    },
    getLesson(lessonId) {
      const data = this.load();
      if (!data.lessons[lessonId]) {
        data.lessons[lessonId] = { labsPassed: [], quizAnswers: {}, completed: false };
      }
      return data.lessons[lessonId];
    },
    markLabPassed(lessonId, labId) {
      const data = this.load();
      const lesson = data.lessons[lessonId] || { labsPassed: [], quizAnswers: {}, completed: false };
      if (!lesson.labsPassed.includes(labId)) {
        lesson.labsPassed.push(labId);
      }
      data.lessons[lessonId] = lesson;
      this.save(data);
      return lesson;
    },
    recordQuizAnswer(lessonId, qId, optionIndex) {
      const data = this.load();
      const lesson = data.lessons[lessonId] || { labsPassed: [], quizAnswers: {}, completed: false };
      lesson.quizAnswers[qId] = optionIndex;
      data.lessons[lessonId] = lesson;
      this.save(data);
      return lesson;
    },
    setLessonCompleted(lessonId, completed) {
      const data = this.load();
      const lesson = data.lessons[lessonId] || { labsPassed: [], quizAnswers: {}, completed: false };
      lesson.completed = Boolean(completed);
      data.lessons[lessonId] = lesson;
      this.save(data);
      return lesson;
    },
    resetAll() {
      this.save({ lessons: {} });
    }
  };

  /* ------------------------------------------------------------------------
     2. Default Linux Server Virtual Filesystem (Ubuntu 24.04 LTS style)
     ------------------------------------------------------------------------ */
  function d(children, owner, group, mode) {
    return {
      type: 'dir',
      mode: mode || 'drwxr-xr-x',
      owner: owner || 'root',
      group: group || 'root',
      mtime: 'Nov 15 00:10',
      children: children || {}
    };
  }

  function f(content, owner, group, mode) {
    return {
      type: 'file',
      mode: mode || '-rw-r--r--',
      owner: owner || 'root',
      group: group || 'root',
      mtime: 'Nov 15 00:10',
      content: content || ''
    };
  }

  function l(target) {
    return {
      type: 'link',
      mode: 'lrwxrwxrwx',
      owner: 'root',
      group: 'root',
      mtime: 'Nov 15 00:10',
      target: target
    };
  }

  function buildDefaultFS() {
    return d({
      bin: l('/usr/bin'),
      sbin: l('/usr/sbin'),
      lib: l('/usr/lib'),
      boot: d({
        'vmlinuz-6.8.0-45-generic': f('[binary kernel image]\n'),
        'initrd.img-6.8.0-45-generic': f('[compressed initramfs image]\n'),
        grub: d({
          'grub.cfg': f('# GRUB2 bootloader configuration\nset default="0"\nset timeout=5\n')
        })
      }),
      dev: d({
        null: f('', 'root', 'root', 'crw-rw-rw-'),
        zero: f('', 'root', 'root', 'crw-rw-rw-'),
        pts: d({
          '0': f('', 'android-dev', 'tty', 'crw--w----')
        })
      }),
      etc: d({
        'os-release': f(
          'PRETTY_NAME="Ubuntu 24.04.1 LTS"\nNAME="Ubuntu"\nVERSION_ID="24.04"\nVERSION="24.04.1 LTS (Noble Numbat)"\nID=ubuntu\nID_LIKE=debian\nHOME_URL="https://www.ubuntu.com/"\n'
        ),
        hostname: f('linux-lab-01\n'),
        hosts: f('127.0.0.1 localhost\n127.0.1.1 linux-lab-01\n'),
        passwd: f(
          'root:x:0:0:root:/root:/bin/bash\nsystemd-network:x:100:102:systemd Network Management:/run/systemd:/usr/sbin/nologin\nandroid-dev:x:1000:1000:Junior Android Dev:/home/android-dev:/bin/bash\n'
        ),
        shells: f('/bin/sh\n/bin/bash\n/usr/bin/bash\n/bin/rbash\n'),
        fstab: f('# /etc/fstab: static file system information.\nUUID=a1b2c3d4-01 / ext4 defaults,errors=remount-ro 0 1\n'),
        'app-server': d({
          'app.conf': f(
            '# Production API Gateway Config\nservice_name=build-telemetry-api\nbind_address=127.0.0.1\nlisten_port=8443\nworkers=4\nlog_level=info\n'
          ),
          'broken.conf': f(
            '# Legacy staging config\nservice_name=artifact-cache\nbind_address=0.0.0.0\nlisten_port=9099\n'
          )
        })
      }),
      home: d({
        'android-dev': d({
          '.bashrc': f(
            '# ~/.bashrc: executed by bash(1) for non-login shells.\nexport PATH="$HOME/.local/bin:$PATH"\nalias ll="ls -alF"\nalias la="ls -A"\n',
            'android-dev', 'android-dev'
          ),
          '.profile': f(
            '# ~/.profile: executed by the command interpreter for login shells.\nPATH="$HOME/bin:$HOME/.local/bin:$PATH"\n',
            'android-dev', 'android-dev'
          ),
          '.bash_history': f('uname -a\npwd\nls -la\n', 'android-dev', 'android-dev', '-rw-------'),
          'README.txt': f(
            'Welcome to Linux Mastery 2027!\nYour goal: transition from Junior Android Developer to Senior Linux Engineer.\nTry exploring /etc/os-release, /proc/version, and /var/log/syslog.\n',
            'android-dev', 'android-dev'
          ),
          projects: d({
            'android-ci-notes.md': f(
              '# Android CI Build Runner Notes\n- Gradle daemon requires at least 4GB RAM\n- Watch /var/log/syslog for OOM or runner failures\n',
              'android-dev', 'android-dev'
            )
          }, 'android-dev', 'android-dev'),
          staging: d({
            'api.conf': f('port=8080\nenv=staging\n', 'android-dev', 'android-dev'),
            'db.conf': f('host=127.0.0.1\npool=16\n', 'android-dev', 'android-dev'),
            'access.log': f('2026-11-15T00:01:00Z GET /health 200\n', 'android-dev', 'android-dev'),
            'build-101.tmp': f('temp artifact 101\n', 'android-dev', 'android-dev'),
            'build-102.tmp': f('temp artifact 102\n', 'android-dev', 'android-dev'),
            'cache.tmp': f('temp cache blob\n', 'android-dev', 'android-dev'),
            '.env.bak': f('API_SECRET=rotate_me_immediately\n', 'android-dev', 'android-dev'),
            '.keep': f('do not remove\n', 'android-dev', 'android-dev')
          }, 'android-dev', 'android-dev')
        }, 'android-dev', 'android-dev')
      }),
      opt: d({
        'android-sdk': d({
          'README.txt': f('Optional third-party / vendor software lives in /opt.\n')
        })
      }),
      proc: d({
        version: f('Linux version 6.8.0-45-generic (buildd@lcy02-amd64-044) (x86_64-linux-gnu-gcc-13 (Ubuntu 13.2.0-23ubuntu4) 13.2.0)\n'),
        cpuinfo: f('processor\t: 0\nvendor_id\t: GenuineIntel\nmodel name\t: Intel(R) Xeon(R) Platinum 8375C CPU @ 2.90GHz\ncpu cores\t: 4\n'),
        meminfo: f('MemTotal:        8192000 kB\nMemFree:         1420312 kB\nMemAvailable:    4850200 kB\nBuffers:          312000 kB\nCached:          3117888 kB\nSwapTotal:       2097152 kB\nSwapFree:        2097152 kB\n'),
        uptime: f('14820.42 58102.11\n')
      }),
      root: d({}, 'root', 'root', 'drwx------'),
      run: d({
        'systemd-resolved.pid': f('512\n')
      }),
      srv: d({
        www: d({
          'index.html': f('<h1>Service Data Directory (/srv)</h1>\n')
        })
      }),
      sys: d({
        kernel: d({
          hostname: f('linux-lab-01\n')
        })
      }),
      tmp: d({
        'sess_991a.sock': f('', 'android-dev', 'android-dev', 'srwxrwxrwx')
      }, 'root', 'root', 'drwxrwxrwt'),
      usr: d({
        bin: d({
          bash: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          ls: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          cat: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          grep: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          find: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          mkdir: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          cp: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          mv: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          rm: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          ps: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          uname: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          man: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          head: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          tail: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x'),
          wc: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x')
        }),
        sbin: d({
          sshd: f('[ELF 64-bit LSB pie executable]\n', 'root', 'root', '-rwxr-xr-x')
        }),
        lib: d({}),
        local: d({
          bin: d({})
        })
      }),
      var: d({
        log: d({
          syslog: f(
            'Nov 15 00:05:11 linux-lab-01 systemd[1]: Started Build Telemetry API.\n' +
            'Nov 15 00:07:44 linux-lab-01 kernel: [14210.11] Out of memory: Killed process 4821 (gradle-worker-daemon) total-vm:5124000kB, anon-rss:4612000kB\n' +
            'Nov 15 00:08:02 linux-lab-01 systemd[1]: gradle-runner.service: Main process exited, code=killed, status=9/KILL\n' +
            'Nov 15 00:09:15 linux-lab-01 sshd[902]: Accepted publickey for android-dev from 10.20.0.14 port 54122 ssh2\n'
          ),
          'app-server': d({
            'error.log': f(
              '[2026-11-15 00:04:10] INFO  Starting build-telemetry-api...\n' +
              '[2026-11-15 00:04:11] WARN  TLS cert expires in 14 days\n' +
              '[2026-11-15 00:04:12] ERROR Failed to bind socket on port 8443: Address already in use\n' +
              '[2026-11-15 00:04:12] FATAL Server startup aborted (exit code 98)\n'
            ),
            'access.log': f(
              '10.20.0.14 - - [15/Nov/2026:00:01:02 +0000] "GET /healthz HTTP/1.1" 200 15\n'
            )
          })
        })
      })
    });
  }

  function cloneNode(node) {
    return JSON.parse(JSON.stringify(node));
  }

  /* ------------------------------------------------------------------------
     3. Built-in Manual Pages (man / whatis / apropos)
     ------------------------------------------------------------------------ */
  const MAN_PAGES = {
    ls: {
      section: 1,
      summary: 'list directory contents',
      body: 'NAME\n       ls - list directory contents\n\nSYNOPSIS\n       ls [OPTION]... [FILE]...\n\nDESCRIPTION\n       List information about FILEs (the current directory by default).\n       -a, --all       do not ignore entries starting with .\n       -l              use a long listing format\n       -h, --human     with -l, print human readable sizes (e.g., 1K 234M)\n       -S              sort by file size, largest first\n       -r, --reverse   reverse order while sorting\n       -R, --recursive list subdirectories recursively'
    },
    cd: {
      section: 1,
      summary: 'change the shell working directory (shell builtin)',
      body: 'NAME\n       cd - change the shell working directory\n\nSYNOPSIS\n       cd [dir]\n\nDESCRIPTION\n       Change the current directory to dir. Defaults to $HOME.\n       Use "cd -" to toggle back to the previous working directory ($OLDPWD).\n       NOTE: cd MUST be a shell builtin because a child process cannot mutate its parent shell\'s cwd.'
    },
    pwd: {
      section: 1,
      summary: 'print name of current/working directory',
      body: 'NAME\n       pwd - print name of current/working directory\n\nSYNOPSIS\n       pwd\n\nDESCRIPTION\n       Print the full filename of the current working directory.'
    },
    mkdir: {
      section: 1,
      summary: 'make directories',
      body: 'NAME\n       mkdir - make directories\n\nSYNOPSIS\n       mkdir [OPTION]... DIRECTORY...\n\nDESCRIPTION\n       Create DIRECTORY(ies), if they do not already exist.\n       -p, --parents   no error if existing, make parent directories as needed'
    },
    cp: {
      section: 1,
      summary: 'copy files and directories',
      body: 'NAME\n       cp - copy files and directories\n\nSYNOPSIS\n       cp [OPTION]... SOURCE DEST\n\nDESCRIPTION\n       Copy SOURCE to DEST, or multiple SOURCE(s) to DIRECTORY.\n       -r, -R          copy directories recursively'
    },
    mv: {
      section: 1,
      summary: 'move (rename) files',
      body: 'NAME\n       mv - move (rename) files\n\nSYNOPSIS\n       mv SOURCE DEST\n\nDESCRIPTION\n       Rename SOURCE to DEST, or move SOURCE(s) to DIRECTORY.'
    },
    rm: {
      section: 1,
      summary: 'remove files or directories',
      body: 'NAME\n       rm - remove files or directories\n\nSYNOPSIS\n       rm [OPTION]... [FILE]...\n\nDESCRIPTION\n       Remove (unlink) the FILE(s).\n       -r, -R          remove directories and their contents recursively\n       -f, --force     ignore nonexistent files and arguments, never prompt'
    },
    cat: {
      section: 1,
      summary: 'concatenate files and print on the standard output',
      body: 'NAME\n       cat - concatenate files and print on the standard output\n\nSYNOPSIS\n       cat [OPTION]... [FILE]...\n\nDESCRIPTION\n       Concatenate FILE(s) to standard output.\n       -n              number all output lines'
    },
    grep: {
      section: 1,
      summary: 'print lines that match patterns',
      body: 'NAME\n       grep - print lines that match patterns\n\nSYNOPSIS\n       grep [OPTION]... PATTERN [FILE]...\n\nDESCRIPTION\n       Search for PATTERN in each FILE or standard input.\n       -i              ignore case distinctions\n       -n              prefix each line with line number\n       -v              invert match (select non-matching lines)\n       -c              print only a count of matching lines\n       -r              search directories recursively'
    },
    find: {
      section: 1,
      summary: 'search for files in a directory hierarchy',
      body: 'NAME\n       find - search for files in a directory hierarchy\n\nSYNOPSIS\n       find [path...] [expression]\n\nDESCRIPTION\n       Recursively search directories.\n       -name PATTERN   base of file name matches shell pattern\n       -type [f|d]     file is of type regular file (f) or directory (d)'
    },
    uname: {
      section: 1,
      summary: 'print system information',
      body: 'NAME\n       uname - print system information\n\nSYNOPSIS\n       uname [OPTION]...\n\nDESCRIPTION\n       -a, --all       print all information\n       -s              print the kernel name\n       -n              print the network node hostname\n       -r              print the kernel release\n       -m              print the machine hardware name'
    },
    ps: {
      section: 1,
      summary: 'report a snapshot of the current processes',
      body: 'NAME\n       ps - report a snapshot of the current processes\n\nSYNOPSIS\n       ps [options]\n\nDESCRIPTION\n       Displays information about active processes.\n       ps -p PID       select by process ID (use $$ for current shell)\n       ps aux          BSD syntax showing all users\' processes'
    },
    type: {
      section: 1,
      summary: 'display information about command type (shell builtin)',
      body: 'NAME\n       type - indicate how a command name would be interpreted\n\nSYNOPSIS\n       type NAME...\n\nDESCRIPTION\n       Shows whether NAME is an alias, shell builtin, or external executable in $PATH.'
    },
    man: {
      section: 1,
      summary: 'an interface to the system reference manuals',
      body: 'NAME\n       man - an interface to the system reference manuals\n\nMANUAL SECTIONS\n       1   Executable programs or shell commands\n       2   System calls (functions provided by the kernel)\n       3   Library calls (functions within program libraries)\n       5   File formats and conventions (e.g. /etc/passwd)\n       8   System administration commands (usually only for root)'
    }
  };

  /* ------------------------------------------------------------------------
     4. Virtual Linux Shell Interpreter
     ------------------------------------------------------------------------ */
  class LinuxShell {
    constructor(fsRoot, opts) {
      this.root = fsRoot || buildDefaultFS();
      opts = opts || {};
      this.user = opts.user || 'android-dev';
      this.host = opts.host || 'linux-lab-01';
      this.home = opts.home || '/home/android-dev';
      this.cwd = opts.cwd || this.home;
      this.oldPwd = this.cwd;
      this.pid = opts.pid || 1337;
      this.lastExit = 0;
      this.history = [];
      this.env = Object.assign({
        USER: this.user,
        HOME: this.home,
        SHELL: '/bin/bash',
        PATH: '/home/android-dev/.local/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin',
        TERM: 'xterm-256color',
        LANG: 'en_US.UTF-8'
      }, opts.env || {});
      this.aliases = {
        ll: 'ls -alF',
        la: 'ls -A'
      };
      if (global.LinuxLabExtInit) {
        global.LinuxLabExtInit(this);
      }
    }

    normalizePath(rawPath, baseDir) {
      if (!rawPath) return baseDir || this.cwd;
      let p = rawPath;
      if (p === '~') p = this.home;
      else if (p.startsWith('~/')) p = this.home + p.slice(1);
      if (!p.startsWith('/')) {
        const base = baseDir || this.cwd;
        p = (base === '/' ? '' : base) + '/' + p;
      }
      const parts = p.split('/').filter(Boolean);
      const stack = [];
      for (const seg of parts) {
        if (seg === '.') continue;
        if (seg === '..') {
          if (stack.length > 0) stack.pop();
        } else {
          stack.push(seg);
        }
      }
      return '/' + stack.join('/');
    }

    resolveNode(rawPath, followLastSymlink) {
      if (followLastSymlink === undefined) followLastSymlink = true;
      const abs = this.normalizePath(rawPath);
      if (abs === '/') return { node: this.root, path: '/', parent: null, name: '/' };
      const parts = abs.split('/').filter(Boolean);
      let curr = this.root;
      let currPath = '';
      for (let i = 0; i < parts.length; i++) {
        const part = parts[i];
        const isLast = i === parts.length - 1;
        if (!curr || curr.type !== 'dir' || !curr.children[part]) {
          return null;
        }
        const parent = curr;
        curr = curr.children[part];
        currPath += '/' + part;
        if (curr.type === 'link' && (!isLast || followLastSymlink)) {
          const target = curr.target.startsWith('/')
            ? curr.target
            : this.normalizePath(curr.target, currPath.slice(0, currPath.lastIndexOf('/')) || '/');
          const resolved = this.resolveNode(target, true);
          if (!resolved) return null;
          if (isLast) {
            return { node: resolved.node, path: resolved.path, parent: parent, name: part };
          }
          curr = resolved.node;
          currPath = resolved.path;
        } else if (isLast) {
          return { node: curr, path: currPath, parent: parent, name: part };
        }
      }
      return null;
    }

    resolveParent(rawPath) {
      const abs = this.normalizePath(rawPath);
      if (abs === '/') return null;
      const idx = abs.lastIndexOf('/');
      const parentPath = idx === 0 ? '/' : abs.slice(0, idx);
      const name = abs.slice(idx + 1);
      const pRes = this.resolveNode(parentPath, true);
      if (!pRes || pRes.node.type !== 'dir') return null;
      return { parent: pRes.node, parentPath: pRes.path, name, fullPath: abs };
    }

    writeFile(rawPath, content, append) {
      const existing = this.resolveNode(rawPath, true);
      if (existing) {
        if (existing.node.type === 'dir') return { error: `Is a directory: ${rawPath}` };
        existing.node.content = append ? existing.node.content + content : content;
        return { ok: true };
      }
      const p = this.resolveParent(rawPath);
      if (!p) return { error: `No such file or directory: ${rawPath}` };
      p.parent.children[p.name] = f(content, this.user, this.user);
      return { ok: true };
    }

    expandBraces(token) {
      const m = token.match(/^([^{}]*)\{([^{}]+)\}(.*)$/);
      if (!m) return [token];
      const prefix = m[1];
      const body = m[2];
      const suffix = m[3];
      const alts = body.split(',');
      const results = [];
      for (const alt of alts) {
        for (const sub of this.expandBraces(prefix + alt + suffix)) {
          results.push(sub);
        }
      }
      return results;
    }

    globToRegex(pattern) {
      let re = '^';
      for (let i = 0; i < pattern.length; i++) {
        const c = pattern[i];
        if (c === '*') re += '.*';
        else if (c === '?') re += '.';
        else if (c === '[') {
          const end = pattern.indexOf(']', i);
          if (end !== -1) {
            re += pattern.slice(i, end + 1);
            i = end;
          } else {
            re += '\\[';
          }
        } else if ('\\^$.|+(){}'.includes(c)) {
          re += '\\' + c;
        } else {
          re += c;
        }
      }
      re += '$';
      return new RegExp(re);
    }

    expandGlobToken(token) {
      if (!/[*?[]/.test(token)) return [token];
      const expandedPath = token.startsWith('~/') ? this.home + token.slice(1) : token;
      const lastSlash = expandedPath.lastIndexOf('/');
      const dirPart = lastSlash === -1 ? '.' : (lastSlash === 0 ? '/' : expandedPath.slice(0, lastSlash));
      const filePattern = lastSlash === -1 ? expandedPath : expandedPath.slice(lastSlash + 1);
      const dirRes = this.resolveNode(dirPart, true);
      if (!dirRes || dirRes.node.type !== 'dir') return [token];
      const regex = this.globToRegex(filePattern);
      const showHidden = filePattern.startsWith('.');
      const names = Object.keys(dirRes.node.children).sort();
      const matches = [];
      for (const name of names) {
        if (name.startsWith('.') && !showHidden) continue;
        if (regex.test(name)) {
          if (lastSlash === -1) matches.push(name);
          else if (dirPart === '/') matches.push('/' + name);
          else matches.push(dirPart + '/' + name);
        }
      }
      return matches.length > 0 ? matches : [token];
    }

    expandVars(str) {
      return str
        .replace(/\$\$/g, String(this.pid))
        .replace(/\$\?/g, String(this.lastExit))
        .replace(/\$\{([A-Za-z_][A-Za-z0-9_]*)\}|\$([A-Za-z_][A-Za-z0-9_]*)/g, (_, b, s) => {
          const name = b || s;
          if (name === 'PWD') return this.cwd;
          if (name === 'OLDPWD') return this.oldPwd;
          return this.env[name] !== undefined ? this.env[name] : '';
        });
    }

    tokenizeSegment(seg) {
      const tokens = [];
      let cur = '';
      let quote = null;
      let hadQuotes = false;

      for (let i = 0; i < seg.length; i++) {
        const ch = seg[i];
        if (quote) {
          if (ch === quote) {
            quote = null;
          } else if (quote === '"' && ch === '\\' && i + 1 < seg.length) {
            cur += seg[++i];
          } else {
            cur += ch;
          }
        } else {
          if (ch === "'" || ch === '"') {
            quote = ch;
            hadQuotes = true;
          } else if (ch === '\\' && i + 1 < seg.length) {
            cur += seg[++i];
          } else if (/\s/.test(ch)) {
            if (cur.length > 0 || hadQuotes) {
              tokens.push({ value: cur, quoted: hadQuotes });
              cur = '';
              hadQuotes = false;
            }
          } else if (ch === '>' && cur === '2' && !hadQuotes) {
            if (seg.startsWith('>&1', i)) {
              tokens.push({ value: '2>&1', quoted: false });
              cur = '';
              i += 2;
            } else if (seg[i + 1] === '>') {
              tokens.push({ value: '2>>', op: true });
              cur = '';
              i++;
            } else {
              tokens.push({ value: '2>', op: true });
              cur = '';
            }
          } else if (ch === '>' && seg[i + 1] === '>') {
            if (cur) { tokens.push({ value: cur, quoted: hadQuotes }); cur = ''; hadQuotes = false; }
            tokens.push({ value: '>>', op: true });
            i++;
          } else if (ch === '>') {
            if (cur) { tokens.push({ value: cur, quoted: hadQuotes }); cur = ''; hadQuotes = false; }
            tokens.push({ value: '>', op: true });
          } else {
            cur += ch;
          }
        }
      }
      if (cur.length > 0 || hadQuotes) {
        tokens.push({ value: cur, quoted: hadQuotes });
      }
      return tokens;
    }

    splitTopLevel(line, delimiters) {
      const parts = [];
      let cur = '';
      let quote = null;
      for (let i = 0; i < line.length; i++) {
        const ch = line[i];
        if (quote) {
          if (ch === quote) quote = null;
          cur += ch;
          continue;
        }
        if (ch === "'" || ch === '"') {
          quote = ch;
          cur += ch;
          continue;
        }
        let matched = null;
        for (const d of delimiters) {
          if (line.startsWith(d, i)) {
            matched = d;
            break;
          }
        }
        if (matched) {
          parts.push({ text: cur.trim(), sep: matched });
          cur = '';
          i += matched.length - 1;
        } else {
          cur += ch;
        }
      }
      if (cur.trim() || parts.length === 0) {
        parts.push({ text: cur.trim(), sep: null });
      }
      return parts;
    }

    execute(rawInput) {
      const line = (rawInput || '').trim();
      if (!line) return { stdout: '', stderr: '', exitCode: 0 };
      this.history.push(line);

      const chains = this.splitTopLevel(line, ['&&', ';']);
      let allOut = '';
      let allErr = '';
      let lastCode = 0;
      let cleared = false;

      for (let i = 0; i < chains.length; i++) {
        const segment = chains[i].text;
        if (!segment) continue;
        const res = this.executePipeline(segment);
        if (res.clear) cleared = true;
        if (res.stdout) allOut += res.stdout;
        if (res.stderr) allErr += res.stderr;
        lastCode = res.exitCode;
        this.lastExit = lastCode;

        if (chains[i].sep === '&&' && lastCode !== 0) {
          break;
        }
      }
      return { stdout: allOut, stderr: allErr, exitCode: lastCode, clear: cleared };
    }

    executePipeline(pipelineStr) {
      const stages = this.splitTopLevel(pipelineStr, ['|']).map((s) => s.text).filter(Boolean);
      let stdin = '';
      let totalErr = '';
      let exitCode = 0;
      let cleared = false;

      for (let i = 0; i < stages.length; i++) {
        const res = this.executeSingle(stages[i], stdin);
        if (res.clear) cleared = true;
        if (res.stderr) totalErr += res.stderr;
        stdin = res.stdout || '';
        exitCode = res.exitCode;
      }
      return { stdout: stdin, stderr: totalErr, exitCode, clear: cleared };
    }

    executeSingle(cmdStr, stdin) {
      const rawTokens = this.tokenizeSegment(cmdStr);
      if (rawTokens.length === 0) return { stdout: '', stderr: '', exitCode: 0 };

      // Extract redirections
      let stdoutFile = null;
      let stdoutAppend = false;
      let stderrFile = null;
      let stderrToStdout = false;
      const args = [];

      for (let i = 0; i < rawTokens.length; i++) {
        const tok = rawTokens[i];
        if (!tok.quoted && tok.value === '2>&1') {
          stderrToStdout = true;
        } else if (tok.op && (tok.value === '>' || tok.value === '>>')) {
          const target = rawTokens[++i];
          if (!target) return { stdout: '', stderr: 'bash: syntax error near unexpected token `newline\'\n', exitCode: 2 };
          stdoutFile = this.expandVars(target.value);
          stdoutAppend = tok.value === '>>';
        } else if (tok.op && tok.value === '2>') {
          const target = rawTokens[++i];
          if (!target) return { stdout: '', stderr: 'bash: syntax error near unexpected token `newline\'\n', exitCode: 2 };
          stderrFile = this.expandVars(target.value);
        } else {
          const v = tok.quoted ? tok.value : this.expandVars(tok.value);
          if (tok.quoted) {
            args.push(v);
          } else {
            for (const b of this.expandBraces(v)) {
              for (const g of this.expandGlobToken(b)) {
                args.push(g);
              }
            }
          }
        }
      }

      if (args.length === 0) return { stdout: '', stderr: '', exitCode: 0 };
      let cmd = args[0];
      let rest = args.slice(1);
      let res;
      if (this.aliases && this.aliases[cmd] && !this._inAlias) {
        this._inAlias = true;
        res = this.executePipeline(this.aliases[cmd] + (rest.length ? ' ' + rest.map((x) => `"${x}"`).join(' ') : ''));
        this._inAlias = false;
      } else {
        res = this.dispatch(cmd, rest, stdin || '');
      }

      let out = res.stdout || '';
      let err = res.stderr || '';

      if (stderrToStdout && err) {
        out += err;
        err = '';
      }

      if (stdoutFile) {
        if (stdoutFile === '/dev/null') {
          out = '';
        } else {
          const w = this.writeFile(stdoutFile, out, stdoutAppend);
          if (w.error) {
            err += `bash: ${w.error}\n`;
            return { stdout: '', stderr: err, exitCode: 1, clear: res.clear };
          }
          out = '';
        }
      }

      if (stderrFile) {
        if (stderrFile === '/dev/null') {
          err = '';
        } else {
          const w2 = this.writeFile(stderrFile, err, false);
          if (w2.error) err += `bash: ${w2.error}\n`;
          else err = '';
        }
      }

      return { stdout: out, stderr: err, exitCode: res.exitCode || 0, clear: res.clear };
    }

    dispatch(cmd, args, stdin) {
      switch (cmd) {
        case 'clear': return { stdout: '', stderr: '', exitCode: 0, clear: true };
        case 'true': return { stdout: '', stderr: '', exitCode: 0 };
        case 'false': return { stdout: '', stderr: '', exitCode: 1 };
        case 'pwd': return { stdout: this.cwd + '\n', stderr: '', exitCode: 0 };
        case 'whoami': return { stdout: this.user + '\n', stderr: '', exitCode: 0 };
        case 'hostname': return { stdout: this.host + '\n', stderr: '', exitCode: 0 };
        case 'date': return { stdout: 'Sun Nov 15 00:45:00 CST 2026\n', stderr: '', exitCode: 0 };
        case 'id': return { stdout: `uid=1000(${this.user}) gid=1000(${this.user}) groups=1000(${this.user}),27(sudo),999(docker)\n`, stderr: '', exitCode: 0 };
        case 'history': return { stdout: this.history.map((h, i) => `  ${String(i + 1).padStart(3)}  ${h}`).join('\n') + '\n', stderr: '', exitCode: 0 };
        case 'env': return { stdout: Object.entries(this.env).map(([k, v]) => `${k}=${v}`).join('\n') + '\n', stderr: '', exitCode: 0 };
        case 'export': return this.cmdExport(args);
        case 'echo': return this.cmdEcho(args);
        case 'cd': return this.cmdCd(args);
        case 'ls': return this.cmdLs(args);
        case 'cat': return this.cmdCat(args, stdin);
        case 'head': return this.cmdHeadTail(args, stdin, true);
        case 'tail': return this.cmdHeadTail(args, stdin, false);
        case 'wc': return this.cmdWc(args, stdin);
        case 'grep': return this.cmdGrep(args, stdin);
        case 'mkdir': return this.cmdMkdir(args);
        case 'touch': return this.cmdTouch(args);
        case 'cp': return this.cmdCp(args);
        case 'mv': return this.cmdMv(args);
        case 'rm': return this.cmdRm(args);
        case 'rmdir': return this.cmdRmdir(args);
        case 'find': return this.cmdFind(args);
        case 'tree': return this.cmdTree(args);
        case 'stat': return this.cmdStat(args);
        case 'file': return this.cmdFile(args);
        case 'which': return this.cmdWhich(args);
        case 'type': return this.cmdType(args);
        case 'man': return this.cmdMan(args);
        case 'whatis': return this.cmdWhatis(args);
        case 'apropos': return this.cmdApropos(args);
        case 'uname': return this.cmdUname(args);
        case 'ps': return this.cmdPs(args);
        case 'help': return this.cmdHelp();
        default:
          if (typeof global.LinuxLabExtDispatch === 'function') {
            const extRes = global.LinuxLabExtDispatch(this, cmd, args, stdin);
            if (extRes) return extRes;
          }
          return { stdout: '', stderr: `${cmd}: command not found (try 'help' for supported sandbox commands)\n`, exitCode: 127 };
      }
    }

    cmdHelp() {
      const list = [
        'Supported Sandbox Commands:',
        '  Navigation & Info : pwd, cd, ls (-la, -lh, -S), tree, find, stat, file',
        '  Files & Dirs      : cat (-n), head, tail, mkdir (-p), touch, cp (-r), mv, rm (-rf), rmdir',
        '  Text & Pipes      : echo, grep (-i, -n, -v, -c, -r), wc (-l, -w, -c), |, >, >>, 2>, &&',
        '  System & Shell    : uname (-a, -r), ps (-p, aux), whoami, id, env, export, type, which',
        '  Docs & Session    : man <cmd>, whatis <cmd>, apropos <kw>, <cmd> --help, history, clear'
      ];
      return { stdout: list.join('\n') + '\n', stderr: '', exitCode: 0 };
    }

    cmdExport(args) {
      if (args.length === 0) return this.dispatch('env', [], '');
      for (const a of args) {
        const eq = a.indexOf('=');
        if (eq > 0) {
          this.env[a.slice(0, eq)] = a.slice(eq + 1);
        }
      }
      return { stdout: '', stderr: '', exitCode: 0 };
    }

    cmdEcho(args) {
      let newline = true;
      let start = 0;
      if (args[0] === '-n') { newline = false; start = 1; }
      const text = args.slice(start).join(' ');
      return { stdout: text + (newline ? '\n' : ''), stderr: '', exitCode: 0 };
    }

    cmdCd(args) {
      const target = args[0];
      let dest;
      if (!target || target === '~') dest = this.home;
      else if (target === '-') dest = this.oldPwd;
      else dest = target;

      const res = this.resolveNode(dest, true);
      if (!res) return { stdout: '', stderr: `bash: cd: ${dest}: No such file or directory\n`, exitCode: 1 };
      if (res.node.type !== 'dir') return { stdout: '', stderr: `bash: cd: ${dest}: Not a directory\n`, exitCode: 1 };
      this.oldPwd = this.cwd;
      this.cwd = res.path;
      return { stdout: target === '-' ? this.cwd + '\n' : '', stderr: '', exitCode: 0 };
    }

    parseFlags(args) {
      const flags = new Set();
      const pos = [];
      for (const a of args) {
        if (a === '--help') { flags.add('help'); continue; }
        if (a.startsWith('--')) { flags.add(a.slice(2)); continue; }
        if (a.startsWith('-') && a.length > 1) {
          for (const ch of a.slice(1)) flags.add(ch);
        } else {
          pos.push(a);
        }
      }
      return { flags, pos };
    }

    formatSize(bytes, human) {
      if (!human) return String(bytes).padStart(5);
      if (bytes < 1024) return (bytes + 'B').padStart(5);
      if (bytes < 1024 * 1024) return ((bytes / 1024).toFixed(1) + 'K').padStart(5);
      return ((bytes / (1024 * 1024)).toFixed(1) + 'M').padStart(5);
    }

    nodeSize(node) {
      if (node.type === 'dir') return 4096;
      if (node.type === 'link') return node.target.length;
      return (node.content || '').length;
    }

    cmdLs(args) {
      const { flags, pos } = this.parseFlags(args);
      if (flags.has('help')) return { stdout: MAN_PAGES.ls.body + '\n', stderr: '', exitCode: 0 };
      const showAll = flags.has('a') || flags.has('all');
      const longFmt = flags.has('l');
      const human = flags.has('h') || flags.has('human-readable');
      const sortSize = flags.has('S');
      const reverse = flags.has('r');
      const targets = pos.length > 0 ? pos : ['.'];

      let out = '';
      let err = '';
      let code = 0;

      for (let tIdx = 0; tIdx < targets.length; tIdx++) {
        const target = targets[tIdx];
        const res = this.resolveNode(target, !longFmt);
        if (!res) {
          err += `ls: cannot access '${target}': No such file or directory\n`;
          code = 2;
          continue;
        }
        if (res.node.type !== 'dir') {
          if (longFmt) {
            const sz = this.formatSize(this.nodeSize(res.node), human);
            const suffix = res.node.type === 'link' ? ` -> ${res.node.target}` : '';
            out += `${res.node.mode} 1 ${res.node.owner} ${res.node.group} ${sz} ${res.node.mtime} ${target}${suffix}\n`;
          } else {
            out += `${target}\n`;
          }
          continue;
        }
        if (targets.length > 1) out += `${target}:\n`;
        let entries = Object.keys(res.node.children).filter((n) => showAll || !n.startsWith('.'));
        if (showAll) entries = ['.', '..', ...entries];

        if (sortSize) {
          entries.sort((a, b) => {
            const na = a === '.' || a === '..' ? res.node : res.node.children[a];
            const nb = b === '.' || b === '..' ? res.node : res.node.children[b];
            return this.nodeSize(nb) - this.nodeSize(na);
          });
        } else {
          entries.sort((a, b) => a.localeCompare(b));
        }
        if (reverse) entries.reverse();

        if (longFmt) {
          out += `total ${entries.length * 4}\n`;
          for (const name of entries) {
            const node = name === '.' || name === '..' ? res.node : res.node.children[name];
            const sz = this.formatSize(this.nodeSize(node), human);
            const display = node.type === 'link' ? `${name} -> ${node.target}` : (node.type === 'dir' ? `${name}/` : name);
            out += `${node.mode} 1 ${node.owner.padEnd(11)} ${node.group.padEnd(11)} ${sz} ${node.mtime} ${display}\n`;
          }
        } else {
          const formatted = entries.map((name) => {
            if (name === '.' || name === '..') return name + '/';
            const node = res.node.children[name];
            if (node.type === 'dir') return name + '/';
            if (node.type === 'link') return name + '@';
            return name;
          });
          out += (formatted.length ? formatted.join('  ') + '\n' : '');
        }
        if (targets.length > 1 && tIdx < targets.length - 1) out += '\n';
      }
      return { stdout: out, stderr: err, exitCode: code };
    }

    cmdCat(args, stdin) {
      const { flags, pos } = this.parseFlags(args);
      if (flags.has('help')) return { stdout: MAN_PAGES.cat.body + '\n', stderr: '', exitCode: 0 };
      const numbered = flags.has('n');
      let out = '';
      let err = '';
      let code = 0;

      const formatLines = (txt) => {
        if (!numbered) return txt;
        const lines = txt.split('\n');
        if (lines[lines.length - 1] === '') lines.pop();
        return lines.map((l, idx) => `${String(idx + 1).padStart(6)}  ${l}`).join('\n') + (lines.length ? '\n' : '');
      };

      if (pos.length === 0) {
        return { stdout: formatLines(stdin), stderr: '', exitCode: 0 };
      }

      for (const p of pos) {
        const res = this.resolveNode(p, true);
        if (!res) {
          err += `cat: ${p}: No such file or directory\n`;
          code = 1;
        } else if (res.node.type === 'dir') {
          err += `cat: ${p}: Is a directory\n`;
          code = 1;
        } else {
          out += formatLines(res.node.content);
        }
      }
      return { stdout: out, stderr: err, exitCode: code };
    }

    cmdHeadTail(args, stdin, isHead) {
      let count = 10;
      const files = [];
      for (let i = 0; i < args.length; i++) {
        if (args[i] === '-n' && args[i + 1]) {
          count = parseInt(args[++i], 10) || 10;
        } else if (/^-\d+$/.test(args[i])) {
          count = parseInt(args[i].slice(1), 10) || 10;
        } else {
          files.push(args[i]);
        }
      }
      let content = stdin;
      if (files.length > 0) {
        const res = this.resolveNode(files[0], true);
        if (!res) return { stdout: '', stderr: `${isHead ? 'head' : 'tail'}: cannot open '${files[0]}': No such file or directory\n`, exitCode: 1 };
        if (res.node.type === 'dir') return { stdout: '', stderr: `${isHead ? 'head' : 'tail'}: error reading '${files[0]}': Is a directory\n`, exitCode: 1 };
        content = res.node.content;
      }
      const lines = content.split('\n');
      if (lines[lines.length - 1] === '') lines.pop();
      const sliced = isHead ? lines.slice(0, count) : lines.slice(-count);
      return { stdout: sliced.length ? sliced.join('\n') + '\n' : '', stderr: '', exitCode: 0 };
    }

    cmdWc(args, stdin) {
      const { flags, pos } = this.parseFlags(args);
      const countStats = (txt) => {
        const lines = (txt.match(/\n/g) || []).length;
        const trimmed = txt.trim();
        const words = trimmed ? trimmed.split(/\s+/).length : 0;
        const bytes = txt.length;
        return { lines, words, bytes };
      };
      const format = (st, label) => {
        const parts = [];
        if (!flags.has('l') && !flags.has('w') && !flags.has('c')) {
          parts.push(String(st.lines).padStart(4), String(st.words).padStart(5), String(st.bytes).padStart(5));
        } else {
          if (flags.has('l')) parts.push(String(st.lines).padStart(4));
          if (flags.has('w')) parts.push(String(st.words).padStart(5));
          if (flags.has('c')) parts.push(String(st.bytes).padStart(5));
        }
        return parts.join(' ') + (label ? ' ' + label : '') + '\n';
      };

      if (pos.length === 0) return { stdout: format(countStats(stdin), ''), stderr: '', exitCode: 0 };
      let out = '';
      let err = '';
      let code = 0;
      for (const p of pos) {
        const res = this.resolveNode(p, true);
        if (!res) { err += `wc: ${p}: No such file or directory\n`; code = 1; }
        else if (res.node.type === 'dir') { err += `wc: ${p}: Is a directory\n`; code = 1; }
        else out += format(countStats(res.node.content), p);
      }
      return { stdout: out, stderr: err, exitCode: code };
    }

    cmdGrep(args, stdin) {
      const { flags, pos } = this.parseFlags(args);
      if (flags.has('help')) return { stdout: MAN_PAGES.grep.body + '\n', stderr: '', exitCode: 0 };
      if (pos.length === 0) return { stdout: '', stderr: 'Usage: grep [OPTION]... PATTERN [FILE]...\n', exitCode: 2 };
      const pattern = pos[0];
      const files = pos.slice(1);
      const regexSrc = flags.has('w') ? `\\b(${pattern})\\b` : pattern;
      const regex = new RegExp(regexSrc, flags.has('i') ? 'i' : '');
      const onlyMatch = flags.has('o');
      const invert = flags.has('v');
      const showNum = flags.has('n');
      const countOnly = flags.has('c');

      const searchText = (text, prefix) => {
        const lines = text.split('\n');
        if (lines[lines.length - 1] === '') lines.pop();
        let matched = 0;
        let out = '';
        lines.forEach((line, idx) => {
          const m = line.match(regex);
          const ok = Boolean(m);
          if ((ok && !invert) || (!ok && invert)) {
            matched++;
            if (!countOnly) {
              const p = prefix ? prefix + ':' : '';
              const n = showNum ? (idx + 1) + ':' : '';
              out += `${p}${n}${onlyMatch && m ? m[0] : line}\n`;
            }
          }
        });
        if (countOnly) out = (prefix ? prefix + ':' : '') + matched + '\n';
        return { out, matched };
      };

      if (files.length === 0) {
        const r = searchText(stdin, '');
        return { stdout: r.out, stderr: '', exitCode: r.matched > 0 ? 0 : 1 };
      }

      let out = '';
      let err = '';
      let totalMatched = 0;
      for (const fp of files) {
        const res = this.resolveNode(fp, true);
        if (!res) { err += `grep: ${fp}: No such file or directory\n`; continue; }
        if (res.node.type === 'dir') {
          if (flags.has('r') || flags.has('R')) {
            const walk = (dirNode, dirPath) => {
              for (const name of Object.keys(dirNode.children).sort()) {
                const child = dirNode.children[name];
                const cp = dirPath + '/' + name;
                if (child.type === 'dir') walk(child, cp);
                else if (child.type === 'file') {
                  const r = searchText(child.content, cp);
                  out += r.out;
                  totalMatched += r.matched;
                }
              }
            };
            walk(res.node, fp.replace(/\/$/, ''));
          } else {
            err += `grep: ${fp}: Is a directory\n`;
          }
        } else {
          const r = searchText(res.node.content, files.length > 1 ? fp : '');
          out += r.out;
          totalMatched += r.matched;
        }
      }
      return { stdout: out, stderr: err, exitCode: totalMatched > 0 ? 0 : 1 };
    }

    cmdMkdir(args) {
      const { flags, pos } = this.parseFlags(args);
      if (flags.has('help')) return { stdout: MAN_PAGES.mkdir.body + '\n', stderr: '', exitCode: 0 };
      if (pos.length === 0) return { stdout: '', stderr: 'mkdir: missing operand\n', exitCode: 1 };
      const parents = flags.has('p') || flags.has('parents');
      let err = '';
      let code = 0;

      for (const raw of pos) {
        const abs = this.normalizePath(raw);
        if (parents) {
          const parts = abs.split('/').filter(Boolean);
          let curr = this.root;
          for (const part of parts) {
            if (!curr.children[part]) {
              curr.children[part] = d({}, this.user, this.user);
            } else if (curr.children[part].type !== 'dir') {
              err += `mkdir: cannot create directory '${raw}': File exists\n`;
              code = 1;
              break;
            }
            curr = curr.children[part];
          }
        } else {
          if (this.resolveNode(abs, true)) {
            err += `mkdir: cannot create directory '${raw}': File exists\n`;
            code = 1;
            continue;
          }
          const p = this.resolveParent(abs);
          if (!p) {
            err += `mkdir: cannot create directory '${raw}': No such file or directory\n`;
            code = 1;
            continue;
          }
          p.parent.children[p.name] = d({}, this.user, this.user);
        }
      }
      return { stdout: '', stderr: err, exitCode: code };
    }

    cmdTouch(args) {
      const { pos } = this.parseFlags(args);
      if (pos.length === 0) return { stdout: '', stderr: 'touch: missing file operand\n', exitCode: 1 };
      let err = '';
      let code = 0;
      for (const raw of pos) {
        const res = this.resolveNode(raw, true);
        if (res) {
          res.node.mtime = 'Nov 15 00:45';
        } else {
          const w = this.writeFile(raw, '', false);
          if (w.error) { err += `touch: cannot touch '${raw}': ${w.error}\n`; code = 1; }
        }
      }
      return { stdout: '', stderr: err, exitCode: code };
    }

    cmdCp(args) {
      const { flags, pos } = this.parseFlags(args);
      if (flags.has('help')) return { stdout: MAN_PAGES.cp.body + '\n', stderr: '', exitCode: 0 };
      if (pos.length < 2) return { stdout: '', stderr: 'cp: missing destination file operand\n', exitCode: 1 };
      const recursive = flags.has('r') || flags.has('R') || flags.has('a');
      const destRaw = pos[pos.length - 1];
      const sources = pos.slice(0, -1);
      const destRes = this.resolveNode(destRaw, true);

      if (sources.length > 1 && (!destRes || destRes.node.type !== 'dir')) {
        return { stdout: '', stderr: `cp: target '${destRaw}' is not a directory\n`, exitCode: 1 };
      }

      let err = '';
      let code = 0;
      for (const srcRaw of sources) {
        const srcRes = this.resolveNode(srcRaw, true);
        if (!srcRes) { err += `cp: cannot stat '${srcRaw}': No such file or directory\n`; code = 1; continue; }
        if (srcRes.node.type === 'dir' && !recursive) {
          err += `cp: -r not specified; omitting directory '${srcRaw}'\n`;
          code = 1;
          continue;
        }
        const cloned = cloneNode(srcRes.node);
        if (destRes && destRes.node.type === 'dir') {
          destRes.node.children[srcRes.name] = cloned;
        } else {
          const p = this.resolveParent(destRaw);
          if (!p) { err += `cp: cannot create regular file '${destRaw}': No such file or directory\n`; code = 1; continue; }
          p.parent.children[p.name] = cloned;
        }
      }
      return { stdout: '', stderr: err, exitCode: code };
    }

    cmdMv(args) {
      const { flags, pos } = this.parseFlags(args);
      if (flags.has('help')) return { stdout: MAN_PAGES.mv.body + '\n', stderr: '', exitCode: 0 };
      if (pos.length < 2) return { stdout: '', stderr: 'mv: missing destination file operand\n', exitCode: 1 };
      const destRaw = pos[pos.length - 1];
      const sources = pos.slice(0, -1);
      const destRes = this.resolveNode(destRaw, true);

      if (sources.length > 1 && (!destRes || destRes.node.type !== 'dir')) {
        return { stdout: '', stderr: `mv: target '${destRaw}' is not a directory\n`, exitCode: 1 };
      }

      let err = '';
      let code = 0;
      for (const srcRaw of sources) {
        const srcRes = this.resolveNode(srcRaw, false);
        if (!srcRes || !srcRes.parent) { err += `mv: cannot stat '${srcRaw}': No such file or directory\n`; code = 1; continue; }
        const node = srcRes.node;
        if (destRes && destRes.node.type === 'dir') {
          delete srcRes.parent.children[srcRes.name];
          destRes.node.children[srcRes.name] = node;
        } else {
          const p = this.resolveParent(destRaw);
          if (!p) { err += `mv: cannot move '${srcRaw}' to '${destRaw}': No such file or directory\n`; code = 1; continue; }
          delete srcRes.parent.children[srcRes.name];
          p.parent.children[p.name] = node;
        }
      }
      return { stdout: '', stderr: err, exitCode: code };
    }

    cmdRm(args) {
      const { flags, pos } = this.parseFlags(args);
      if (flags.has('help')) return { stdout: MAN_PAGES.rm.body + '\n', stderr: '', exitCode: 0 };
      if (pos.length === 0) return { stdout: '', stderr: 'rm: missing operand\n', exitCode: 1 };
      const recursive = flags.has('r') || flags.has('R');
      const force = flags.has('f');
      let err = '';
      let code = 0;

      for (const raw of pos) {
        const abs = this.normalizePath(raw);
        if (abs === '/') {
          err += "rm: it is dangerous to operate recursively on '/'\n";
          code = 1;
          continue;
        }
        const res = this.resolveNode(raw, false);
        if (!res) {
          if (!force) { err += `rm: cannot remove '${raw}': No such file or directory\n`; code = 1; }
          continue;
        }
        if (res.node.type === 'dir' && !recursive) {
          err += `rm: cannot remove '${raw}': Is a directory\n`;
          code = 1;
          continue;
        }
        if (res.parent) {
          delete res.parent.children[res.name];
        }
      }
      return { stdout: '', stderr: err, exitCode: code };
    }

    cmdRmdir(args) {
      const { pos } = this.parseFlags(args);
      if (pos.length === 0) return { stdout: '', stderr: 'rmdir: missing operand\n', exitCode: 1 };
      let err = '';
      let code = 0;
      for (const raw of pos) {
        const res = this.resolveNode(raw, false);
        if (!res) { err += `rmdir: failed to remove '${raw}': No such file or directory\n`; code = 1; continue; }
        if (res.node.type !== 'dir') { err += `rmdir: failed to remove '${raw}': Not a directory\n`; code = 1; continue; }
        if (Object.keys(res.node.children).length > 0) { err += `rmdir: failed to remove '${raw}': Directory not empty\n`; code = 1; continue; }
        if (res.parent) delete res.parent.children[res.name];
      }
      return { stdout: '', stderr: err, exitCode: code };
    }

    cmdFind(args) {
      let start = '.';
      let idx = 0;
      if (args[0] && !args[0].startsWith('-')) {
        start = args[0];
        idx = 1;
      }
      let namePattern = null;
      let nameInsensitive = false;
      let typeFilter = null;
      let maxDepth = Infinity;
      let print0 = false;
      for (let i = idx; i < args.length; i++) {
        if (args[i] === '-name' && args[i + 1]) namePattern = this.globToRegex(args[++i]);
        else if (args[i] === '-iname' && args[i + 1]) { namePattern = this.globToRegex(args[++i].toLowerCase()); nameInsensitive = true; }
        else if (args[i] === '-type' && args[i + 1]) typeFilter = args[++i];
        else if (args[i] === '-maxdepth' && args[i + 1]) maxDepth = parseInt(args[++i], 10);
        else if (args[i] === '-print0') print0 = true;
      }
      const res = this.resolveNode(start, true);
      if (!res) return { stdout: '', stderr: `find: '${start}': No such file or directory\n`, exitCode: 1 };
      const results = [];
      const walk = (node, displayPath, baseName, depth) => {
        const typeOk = !typeFilter || (typeFilter === 'd' && node.type === 'dir') || (typeFilter === 'f' && node.type === 'file') || (typeFilter === 'l' && node.type === 'link');
        const testName = nameInsensitive ? baseName.toLowerCase() : baseName;
        const nameOk = !namePattern || namePattern.test(testName);
        if (typeOk && nameOk) results.push(displayPath);
        if (node.type === 'dir' && depth < maxDepth) {
          for (const k of Object.keys(node.children).sort()) {
            const nextPath = displayPath === '/' ? '/' + k : displayPath + '/' + k;
            walk(node.children[k], nextPath, k, depth + 1);
          }
        }
      };
      walk(res.node, start.replace(/\/$/, '') || '/', res.name, 0);
      const sep = print0 ? '\0' : '\n';
      return { stdout: results.length ? results.join(sep) + (print0 ? '\0' : '\n') : '', stderr: '', exitCode: 0 };
    }

    cmdTree(args) {
      const target = args[0] || '.';
      const res = this.resolveNode(target, true);
      if (!res) return { stdout: `${target} [error opening dir]\n`, stderr: '', exitCode: 2 };
      const lines = [target];
      let dirs = 0;
      let files = 0;
      const walk = (node, prefix) => {
        if (node.type !== 'dir') return;
        const keys = Object.keys(node.children).filter((k) => !k.startsWith('.')).sort();
        keys.forEach((k, i) => {
          const isLast = i === keys.length - 1;
          const child = node.children[k];
          const connector = isLast ? '└── ' : '├── ';
          lines.push(prefix + connector + k + (child.type === 'dir' ? '/' : (child.type === 'link' ? ` -> ${child.target}` : '')));
          if (child.type === 'dir') {
            dirs++;
            walk(child, prefix + (isLast ? '    ' : '│   '));
          } else {
            files++;
          }
        });
      };
      walk(res.node, '');
      lines.push(`\n${dirs} directories, ${files} files`);
      return { stdout: lines.join('\n') + '\n', stderr: '', exitCode: 0 };
    }

    cmdStat(args) {
      if (!args[0]) return { stdout: '', stderr: 'stat: missing operand\n', exitCode: 1 };
      const res = this.resolveNode(args[0], false);
      if (!res) return { stdout: '', stderr: `stat: cannot stat '${args[0]}': No such file or directory\n`, exitCode: 1 };
      const n = res.node;
      const kind = n.type === 'dir' ? 'directory' : (n.type === 'link' ? 'symbolic link' : 'regular file');
      const out = [
        `  File: ${args[0]}${n.type === 'link' ? ' -> ' + n.target : ''}`,
        `  Size: ${this.nodeSize(n)}\t\tBlocks: 8\t\tIO Block: 4096\t${kind}`,
        `Access: (${n.mode})  Uid: ( 1000/ ${n.owner})   Gid: ( 1000/ ${n.group})`,
        `Modify: 2026-11-15 00:10:00.000000000 -0600`
      ].join('\n') + '\n';
      return { stdout: out, stderr: '', exitCode: 0 };
    }

    cmdFile(args) {
      if (!args[0]) return { stdout: '', stderr: 'Usage: file FILE...\n', exitCode: 1 };
      let out = '';
      for (const a of args) {
        const res = this.resolveNode(a, false);
        if (!res) out += `${a}: cannot open (No such file or directory)\n`;
        else if (res.node.type === 'dir') out += `${a}: directory\n`;
        else if (res.node.type === 'link') out += `${a}: symbolic link to ${res.node.target}\n`;
        else if (res.node.content.startsWith('[ELF')) out += `${a}: ELF 64-bit LSB pie executable, x86-64\n`;
        else out += `${a}: ASCII text\n`;
      }
      return { stdout: out, stderr: '', exitCode: 0 };
    }

    cmdWhich(args) {
      if (!args[0]) return { stdout: '', stderr: '', exitCode: 1 };
      const builtins = new Set(['cd', 'echo', 'export', 'pwd', 'type', 'history', 'true', 'false']);
      if (builtins.has(args[0])) return { stdout: '', stderr: '', exitCode: 1 };
      const binRes = this.resolveNode('/usr/bin/' + args[0], true);
      if (binRes) return { stdout: `/usr/bin/${args[0]}\n`, stderr: '', exitCode: 0 };
      const sbinRes = this.resolveNode('/usr/sbin/' + args[0], true);
      if (sbinRes) return { stdout: `/usr/sbin/${args[0]}\n`, stderr: '', exitCode: 0 };
      return { stdout: '', stderr: '', exitCode: 1 };
    }

    cmdType(args) {
      if (!args[0]) return { stdout: '', stderr: '', exitCode: 1 };
      let out = '';
      let code = 0;
      const builtins = new Set(['cd', 'echo', 'export', 'pwd', 'type', 'history', 'true', 'false']);
      for (const name of args) {
        if (this.aliases[name]) out += `${name} is aliased to \`${this.aliases[name]}\'\n`;
        else if (builtins.has(name)) out += `${name} is a shell builtin\n`;
        else if (this.resolveNode('/usr/bin/' + name, true)) out += `${name} is /usr/bin/${name}\n`;
        else if (this.resolveNode('/usr/sbin/' + name, true)) out += `${name} is /usr/sbin/${name}\n`;
        else { out += `bash: type: ${name}: not found\n`; code = 1; }
      }
      return { stdout: out, stderr: '', exitCode: code };
    }

    cmdMan(args) {
      if (!args[0]) return { stdout: '', stderr: 'What manual page do you want?\nFor example, try \'man ls\'.\n', exitCode: 1 };
      const page = MAN_PAGES[args[0]];
      if (!page) return { stdout: '', stderr: `No manual entry for ${args[0]}\n`, exitCode: 16 };
      return { stdout: `${args[0].toUpperCase()}(${page.section})\n\n${page.body}\n`, stderr: '', exitCode: 0 };
    }

    cmdWhatis(args) {
      if (!args[0]) return { stdout: '', stderr: 'whatis what?\n', exitCode: 1 };
      const page = MAN_PAGES[args[0]];
      if (!page) return { stdout: '', stderr: `${args[0]}: nothing appropriate.\n`, exitCode: 16 };
      return { stdout: `${args[0]} (${page.section}) - ${page.summary}\n`, stderr: '', exitCode: 0 };
    }

    cmdApropos(args) {
      if (!args[0]) return { stdout: '', stderr: 'apropos what?\n', exitCode: 1 };
      const q = args[0].toLowerCase();
      const hits = Object.entries(MAN_PAGES)
        .filter(([k, v]) => k.includes(q) || v.summary.toLowerCase().includes(q) || v.body.toLowerCase().includes(q))
        .map(([k, v]) => `${k} (${v.section}) - ${v.summary}`);
      if (!hits.length) return { stdout: '', stderr: `${args[0]}: nothing appropriate.\n`, exitCode: 16 };
      return { stdout: hits.join('\n') + '\n', stderr: '', exitCode: 0 };
    }

    cmdUname(args) {
      const { flags } = this.parseFlags(args);
      if (flags.size === 0 || flags.has('s')) {
        if (flags.has('a') || flags.has('all')) {
          return { stdout: `Linux ${this.host} 6.8.0-45-generic #45-Ubuntu SMP PREEMPT_DYNAMIC x86_64 GNU/Linux\n`, stderr: '', exitCode: 0 };
        }
        return { stdout: 'Linux\n', stderr: '', exitCode: 0 };
      }
      if (flags.has('a') || flags.has('all')) {
        return { stdout: `Linux ${this.host} 6.8.0-45-generic #45-Ubuntu SMP PREEMPT_DYNAMIC x86_64 GNU/Linux\n`, stderr: '', exitCode: 0 };
      }
      const parts = [];
      if (flags.has('n')) parts.push(this.host);
      if (flags.has('r')) parts.push('6.8.0-45-generic');
      if (flags.has('m')) parts.push('x86_64');
      return { stdout: parts.join(' ') + '\n', stderr: '', exitCode: 0 };
    }

    cmdPs(args) {
      const joined = args.join(' ');
      if (joined.includes('-p')) {
        return {
          stdout: `    PID TTY          TIME CMD\n   ${this.pid} pts/0    00:00:00 bash\n`,
          stderr: '',
          exitCode: 0
        };
      }
      return {
        stdout: [
          'USER         PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND',
          'root           1  0.0  0.1 168940 12912 ?        Ss   00:00   0:02 /sbin/init',
          'root         512  0.0  0.1  21420  9440 ?        Ss   00:00   0:00 /lib/systemd/systemd-resolved',
          'root         902  0.0  0.0  15420  6200 ?        Ss   00:09   0:00 sshd: android-dev [priv]',
          `android+    ${this.pid}  0.0  0.0  10240  5120 pts/0    Ss   00:10   0:00 -bash`,
          'android+    1402  0.0  0.0  11300  3800 pts/0    R+   00:45   0:00 ps ' + joined
        ].join('\n') + '\n',
        stderr: '',
        exitCode: 0
      };
    }

    displayPath() {
      if (this.cwd === this.home) return '~';
      if (this.cwd.startsWith(this.home + '/')) return '~' + this.cwd.slice(this.home.length);
      return this.cwd;
    }
  }

  /* ------------------------------------------------------------------------
     5. Interactive Terminal Widget Mounting
     ------------------------------------------------------------------------ */
  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');
  }

  function mountTerminal(container, options) {
    options = options || {};
    let shell = new LinuxShell(options.fsFactory ? options.fsFactory() : buildDefaultFS());
    let histIdx = -1;

    container.innerHTML = `
      <div class="terminal-box">
        <div class="terminal-chrome">
          <div class="terminal-dots">
            <span class="dot dot-red"></span>
            <span class="dot dot-amber"></span>
            <span class="dot dot-green"></span>
            <span style="margin-left:0.45rem;">${escapeHtml(options.title || 'android-dev@linux-lab-01: bash')}</span>
          </div>
          <div style="display:flex; gap:0.4rem;">
            <button type="button" class="btn btn-sm term-reset-btn">Reset FS</button>
          </div>
        </div>
        <div class="terminal-screen" tabindex="0"></div>
        <div class="terminal-input-row">
          <span class="term-prompt">${shell.user}@${shell.host}:<span class="term-path">${escapeHtml(shell.displayPath())}</span>$</span>
          <input type="text" class="terminal-input" placeholder="Type a Linux command (e.g., uname -a, ls -la, help)..." autocomplete="off" spellcheck="false" />
        </div>
      </div>
    `;

    const screen = container.querySelector('.terminal-screen');
    const input = container.querySelector('.terminal-input');
    const promptEl = container.querySelector('.term-prompt');
    const resetBtn = container.querySelector('.term-reset-btn');

    function updatePrompt() {
      promptEl.innerHTML = `${escapeHtml(shell.user)}@${escapeHtml(shell.host)}:<span class="term-path">${escapeHtml(shell.displayPath())}</span>$`;
    }

    function appendBanner() {
      if (options.banner) {
        const div = document.createElement('div');
        div.className = 'term-line term-info';
        div.textContent = options.banner;
        screen.appendChild(div);
      }
    }

    function runCommand(cmdText) {
      const trimmed = (cmdText || '').trim();
      const promptHtml = `<span class="term-prompt">${escapeHtml(shell.user)}@${escapeHtml(shell.host)}:<span class="term-path">${escapeHtml(shell.displayPath())}</span>$</span> `;
      const cmdLine = document.createElement('div');
      cmdLine.className = 'term-line';
      cmdLine.innerHTML = promptHtml + `<span class="term-cmd">${escapeHtml(cmdText)}</span>`;
      screen.appendChild(cmdLine);

      if (trimmed) {
        const res = shell.execute(trimmed);
        if (res.clear) {
          screen.innerHTML = '';
        } else {
          if (res.stdout) {
            const outEl = document.createElement('div');
            outEl.className = 'term-line';
            outEl.textContent = res.stdout.replace(/\n$/, '');
            screen.appendChild(outEl);
          }
          if (res.stderr) {
            const errEl = document.createElement('div');
            errEl.className = 'term-line term-err';
            errEl.textContent = res.stderr.replace(/\n$/, '');
            screen.appendChild(errEl);
          }
        }
        histIdx = shell.history.length;
      }

      updatePrompt();
      screen.scrollTop = screen.scrollHeight;
      if (options.onCommand) options.onCommand(shell);
    }

    input.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        const val = input.value;
        input.value = '';
        runCommand(val);
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (shell.history.length > 0 && histIdx > 0) {
          histIdx--;
          input.value = shell.history[histIdx];
        } else if (histIdx === shell.history.length && shell.history.length > 0) {
          histIdx = shell.history.length - 1;
          input.value = shell.history[histIdx];
        }
      } else if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (histIdx < shell.history.length - 1) {
          histIdx++;
          input.value = shell.history[histIdx];
        } else {
          histIdx = shell.history.length;
          input.value = '';
        }
      } else if (e.key === 'l' && e.ctrlKey) {
        e.preventDefault();
        screen.innerHTML = '';
      } else if (e.key === 'Tab') {
        e.preventDefault();
        const val = input.value;
        const lastSpace = val.lastIndexOf(' ');
        const prefix = lastSpace === -1 ? val : val.slice(lastSpace + 1);
        const before = lastSpace === -1 ? '' : val.slice(0, lastSpace + 1);
        const lastSlash = prefix.lastIndexOf('/');
        const dirPart = lastSlash === -1 ? '.' : (lastSlash === 0 ? '/' : prefix.slice(0, lastSlash));
        const namePart = lastSlash === -1 ? prefix : prefix.slice(lastSlash + 1);
        const dirRes = shell.resolveNode(dirPart, true);
        if (dirRes && dirRes.node.type === 'dir') {
          const matches = Object.keys(dirRes.node.children).filter((k) => k.startsWith(namePart));
          if (matches.length === 1) {
            const match = matches[0];
            const isDir = dirRes.node.children[match].type === 'dir';
            const completed = (lastSlash === -1 ? '' : prefix.slice(0, lastSlash + 1)) + match + (isDir ? '/' : '');
            input.value = before + completed;
          }
        }
      }
    });

    screen.addEventListener('click', () => input.focus());

    resetBtn.addEventListener('click', () => {
      shell = new LinuxShell(options.fsFactory ? options.fsFactory() : buildDefaultFS());
      histIdx = -1;
      screen.innerHTML = '';
      appendBanner();
      updatePrompt();
      if (options.onReset) options.onReset(shell);
    });

    appendBanner();

    return {
      getShell: () => shell,
      run: (cmd) => {
        runCommand(cmd);
        input.focus();
      },
      reset: () => resetBtn.click(),
      focus: () => input.focus()
    };
  }

  /* ------------------------------------------------------------------------
     6. Runnable Code Examples
     ------------------------------------------------------------------------ */
  function initCodeExamples(sandboxTerminal) {
    const blocks = document.querySelectorAll('.code-example');
    blocks.forEach((block) => {
      const pre = block.querySelector('pre');
      const runBtn = block.querySelector('[data-action="run-example"]');
      const sendBtn = block.querySelector('[data-action="send-sandbox"]');
      const copyBtn = block.querySelector('[data-action="copy-example"]');
      const outBox = block.querySelector('.code-example-output');
      const rawCmds = (block.getAttribute('data-cmd') || (pre ? pre.textContent : '')).trim();

      if (copyBtn) {
        copyBtn.addEventListener('click', () => {
          if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(rawCmds);
            const orig = copyBtn.textContent;
            copyBtn.textContent = 'Copied!';
            setTimeout(() => { copyBtn.textContent = orig; }, 1200);
          }
        });
      }

      if (runBtn && outBox) {
        runBtn.addEventListener('click', () => {
          const factory = global.LESSON_FS_FACTORY || buildDefaultFS;
          const tempShell = new LinuxShell(factory());
          const lines = rawCmds.split('\n').map((l) => l.trim()).filter((l) => l && !l.startsWith('#'));
          let rendered = '';
          for (const line of lines) {
            rendered += `$ ${line}\n`;
            const res = tempShell.execute(line);
            if (res.stdout) rendered += res.stdout;
            if (res.stderr) rendered += res.stderr;
          }
          outBox.textContent = rendered.trimEnd() || '(command exited 0 with no output)';
          outBox.classList.add('visible');
        });
      }

      if (sendBtn && sandboxTerminal) {
        sendBtn.addEventListener('click', () => {
          const lines = rawCmds.split('\n').map((l) => l.trim()).filter((l) => l && !l.startsWith('#'));
          lines.forEach((l) => sandboxTerminal.run(l));
          const sandboxEl = document.getElementById('sandbox-terminal');
          if (sandboxEl) sandboxEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
        });
      }
    });
  }

  /* ------------------------------------------------------------------------
     7. Practice Labs Controller (Run + Check + Hints + Final Answer)
     ------------------------------------------------------------------------ */
  function initLabs(lessonId, labSpecs, fsFactory, onProgressChange) {
    const progress = ProgressStore.getLesson(lessonId);

    labSpecs.forEach((spec, idx) => {
      const mount = document.getElementById(spec.mountId);
      if (!mount) return;

      const isInitiallyPassed = progress.labsPassed.includes(spec.id);
      let hintLevel = 0;

      mount.innerHTML = `
        <div class="lab-card ${isInitiallyPassed ? 'passed' : ''}" id="card-${spec.id}">
          <div class="lab-header">
            <div>
              <span class="badge badge-cyan">Practice Lab ${idx + 1}</span>
              <span class="badge ${spec.tierClass || 'badge-muted'}" style="margin-left:0.35rem;">${escapeHtml(spec.tier || 'Hands-On')}</span>
              <h3 class="lab-title" style="margin-top:0.45rem;">${escapeHtml(spec.title)}</h3>
            </div>
            <span class="lab-status ${isInitiallyPassed ? 'passed' : ''}" id="status-${spec.id}">
              ${isInitiallyPassed ? 'PASSED' : 'NOT VERIFIED'}
            </span>
          </div>
          <p style="color:var(--text-secondary); margin-top:0;">${spec.prompt}</p>
          <div class="lab-term-mount"></div>
          <div class="lab-controls">
            <button type="button" class="btn btn-success" data-role="check">Check Solution</button>
            <button type="button" class="btn btn-amber" data-role="hint">Show Hint (0/${spec.hints.length})</button>
            <button type="button" class="btn btn-primary" data-role="answer">Show Final Answer</button>
            <button type="button" class="btn" data-role="reset-lab">Reset Lab</button>
          </div>
          <div class="lab-feedback" id="fb-${spec.id}"></div>
          <div class="hint-drawer" id="hint-${spec.id}"></div>
          <div class="answer-drawer" id="ans-${spec.id}">
            <strong>Final Answer &amp; Explanation:</strong>
            <pre style="margin:0.55rem 0; padding:0.7rem; background:var(--bg-terminal); border-radius:6px; overflow-x:auto; color:#a7f3d0;">${escapeHtml(spec.answerCommands.join('\n'))}</pre>
            <p style="margin:0.4rem 0 0.65rem; color:var(--text-secondary);">${spec.answerExplanation}</p>
            <button type="button" class="btn btn-sm btn-primary" data-role="run-answer">Run Answer in Lab Terminal</button>
          </div>
        </div>
      `;

      const termMount = mount.querySelector('.lab-term-mount');
      const termCtrl = mountTerminal(termMount, {
        title: `lab-${idx + 1}: ${spec.id}`,
        fsFactory: spec.fsFactory || fsFactory,
        banner: spec.banner || 'Lab environment ready. Type commands below, then click "Check Solution".'
      });

      const card = mount.querySelector('.lab-card');
      const statusEl = mount.querySelector(`#status-${spec.id}`);
      const fbEl = mount.querySelector(`#fb-${spec.id}`);
      const hintBtn = mount.querySelector('[data-role="hint"]');
      const hintDrawer = mount.querySelector(`#hint-${spec.id}`);
      const ansBtn = mount.querySelector('[data-role="answer"]');
      const ansDrawer = mount.querySelector(`#ans-${spec.id}`);
      const checkBtn = mount.querySelector('[data-role="check"]');
      const resetLabBtn = mount.querySelector('[data-role="reset-lab"]');
      const runAnsBtn = mount.querySelector('[data-role="run-answer"]');

      checkBtn.addEventListener('click', () => {
        const checks = spec.verify(termCtrl.getShell());
        const allOk = checks.every((c) => c.ok);
        fbEl.className = `lab-feedback visible ${allOk ? 'pass' : 'fail'}`;
        const listHtml = checks
          .map((c) => `<li class="${c.ok ? 'check-item-ok' : 'check-item-bad'}">${c.ok ? '[PASS]' : '[FAIL]'} ${escapeHtml(c.label)}</li>`)
          .join('');
        fbEl.innerHTML = `<strong>${allOk ? 'All checks passed!' : 'Some checks did not pass yet:'}</strong><ul class="check-list">${listHtml}</ul>`;

        if (allOk) {
          card.classList.add('passed');
          statusEl.className = 'lab-status passed';
          statusEl.textContent = 'PASSED';
          ProgressStore.markLabPassed(lessonId, spec.id);
          if (onProgressChange) onProgressChange();
        }
      });

      hintBtn.addEventListener('click', () => {
        if (hintLevel < spec.hints.length) hintLevel++;
        hintBtn.textContent = `Show Hint (${hintLevel}/${spec.hints.length})`;
        hintDrawer.classList.add('visible');
        hintDrawer.innerHTML = spec.hints
          .slice(0, hintLevel)
          .map((h, i) => `<div style="margin-bottom:0.4rem;"><strong>Hint ${i + 1}:</strong> ${h}</div>`)
          .join('');
      });

      ansBtn.addEventListener('click', () => {
        ansDrawer.classList.toggle('visible');
        ansBtn.textContent = ansDrawer.classList.contains('visible') ? 'Hide Final Answer' : 'Show Final Answer';
      });

      runAnsBtn.addEventListener('click', () => {
        spec.answerCommands.forEach((cmd) => termCtrl.run(cmd));
      });

      resetLabBtn.addEventListener('click', () => {
        termCtrl.reset();
        fbEl.className = 'lab-feedback';
        fbEl.innerHTML = '';
      });
    });
  }

  /* ------------------------------------------------------------------------
     8. Diagnostic Multiple-Choice Quiz Controller
     ------------------------------------------------------------------------ */
  function initQuizzes(lessonId, quizSpecs, onProgressChange) {
    const progress = ProgressStore.getLesson(lessonId);

    quizSpecs.forEach((q, idx) => {
      const mount = document.getElementById(q.mountId);
      if (!mount) return;
      const letters = ['A', 'B', 'C', 'D'];
      const savedChoice = progress.quizAnswers[q.id];

      const codeHtml = q.code ? `<pre class="quiz-code">${escapeHtml(q.code)}</pre>` : '';
      const optionsHtml = q.options.map((opt, optIdx) => `
        <button type="button" class="quiz-option" data-opt="${optIdx}">
          <div class="quiz-option-top">
            <span class="quiz-letter">${letters[optIdx]}.</span>
            <span>${escapeHtml(opt.text)}</span>
          </div>
          <div class="quiz-option-why">
            <strong>${optIdx === q.correctIndex ? 'Why this is correct:' : 'Why this is wrong:'}</strong>
            ${opt.why}
          </div>
        </button>
      `).join('');

      mount.innerHTML = `
        <div class="quiz-card" id="quiz-${q.id}">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:0.5rem; gap:0.5rem;">
            <span class="badge badge-violet">Question ${idx + 1} of ${quizSpecs.length}</span>
            <span class="badge badge-muted" id="qstatus-${q.id}">Unanswered</span>
          </div>
          <p class="quiz-question">${q.question}</p>
          ${codeHtml}
          <div class="quiz-options">${optionsHtml}</div>
          <div class="quiz-summary" id="qsum-${q.id}"></div>
        </div>
      `;

      const optionBtns = mount.querySelectorAll('.quiz-option');
      const statusBadge = mount.querySelector(`#qstatus-${q.id}`);
      const summaryEl = mount.querySelector(`#qsum-${q.id}`);

      function revealAnswer(chosenIdx) {
        const isCorrect = chosenIdx === q.correctIndex;
        optionBtns.forEach((btn, i) => {
          btn.classList.remove('selected', 'is-correct', 'is-wrong');
          if (i === q.correctIndex) btn.classList.add('is-correct');
          else if (i === chosenIdx) btn.classList.add('is-wrong');
          if (i === chosenIdx) btn.classList.add('selected');
          btn.querySelector('.quiz-option-why').classList.add('visible');
        });
        statusBadge.className = `badge ${isCorrect ? 'badge-green' : 'badge-rose'}`;
        statusBadge.textContent = isCorrect ? 'Correct' : `Incorrect (Answer: ${letters[q.correctIndex]})`;
        summaryEl.innerHTML = `<strong>Takeaway:</strong> ${q.takeaway}`;
      }

      optionBtns.forEach((btn) => {
        btn.addEventListener('click', () => {
          const chosen = parseInt(btn.getAttribute('data-opt'), 10);
          ProgressStore.recordQuizAnswer(lessonId, q.id, chosen);
          revealAnswer(chosen);
          if (onProgressChange) onProgressChange();
        });
      });

      if (savedChoice !== undefined) {
        revealAnswer(savedChoice);
      }
    });
  }

  /* ------------------------------------------------------------------------
     9. Export Public API
     ------------------------------------------------------------------------ */
  global.LinuxLab = {
    ProgressStore,
    buildDefaultFS,
    vfsDir: d,
    vfsFile: f,
    vfsLink: l,
    LinuxShell,
    mountTerminal,
    initCodeExamples,
    initLabs,
    initQuizzes
  };
})(window);
