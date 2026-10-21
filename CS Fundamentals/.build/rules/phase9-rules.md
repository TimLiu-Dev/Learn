# Phase 9 shared rules (all four forks: L30–L33)

Workspace: `/Users/tl/Documents/Learns/CS Fundamentals/` (call it W). Scratchpad: `/private/tmp/claude-501/-Users-tl-Documents-Learns-CS-Fundamentals/81019dbd-af17-411d-8f17-c86ac7cdd7e3/scratchpad` (call it S).

## Files (NN = your lesson number, slug given in your prompt)
- Lesson: `W/lessons/00NN-<slug>.html` with `<body data-lesson="lNN">`.
- Template: `W/.build/tpl/00NN.html`. Verified sources: `W/.build/lNN/`. Memviz specs (if any): `W/.build/viz/lNN.py` (see `W/.build/viz/README.txt`; names `\w+`, prefix `lNN_`). Every path must match `[\w/.]+` (no hyphens anywhere, including Gradle subfolders, so use `gr_kotlin`, not `gr-kotlin`).
- Build: `cd "W/.build/bin" && python3 build.py ../tpl/00NN.html ../../lessons/00NN-<slug>.html`. `<!--CODE:lNN/path/File.kt-->` injects a verified file (language from extension); `<!--VIZ:name-->` injects a memviz spec.
- Gradle projects (only if your lesson needs one): put a copy under `W/.build/lNN/gr_kotlin/` and `W/.build/lNN/gr_java/`, generated with Gradle 9.7.1 (`gradle init --type kotlin-application` / `java-application`, Kotlin DSL), the same as L11's `W/.build/l11/tdd_kotlin` (look at its `app/build.gradle.kts`; `~/.gradle` caches the JDK 21 toolchain, Kotlin plugin and JUnit). Work in a scratch copy under S, then copy the sources into W. Before you finish, delete `build/`, `.gradle/` and `.kotlin/` from the copies in W.

## Don't edit shared files
`W/assets/*` (existing ones), `W/.build/bin/*`, `W/reference/*`, `NOTES.md`, `RESOURCES.md`, other lessons and templates. New styles go in a `<style>` block in your lesson's `<head>`; report them. You MAY create ONE new asset file, but only with the name assigned in your prompt (so siblings never collide). It must inject its own CSS using the theme tokens in `assets/course.css` (look at `assets/gitgraph.js` and `assets/pipeviz.js` as models), work in light and dark mode, and fit at phone width. I integrate links, notes, the roadmap and the reference sheet afterwards.

## Tools
- `export KOTLINC=S/kotlinc-dl/kotlinc/bin/kotlinc`, then `W/.build/bin/run.sh File.kt File.java …`.
- **Libraries** are checksum-verified jars in `S/libs/`. Never download other copies. `run.sh` takes no classpath, so compile with `-cp` yourself; in Gradle use the same coordinates.
  - **kotlinx.coroutines** 1.11.0: `kotlinx-coroutines-core-jvm-1.11.0.jar`.
  - **kotlinx.serialization** 1.12.0: `kotlinx-serialization-core-jvm-1.12.0.jar` + `kotlinx-serialization-json-jvm-1.12.0.jar`.
    - It needs the compiler plugin: `"$KOTLINC" -Xplugin=S/kotlinc-dl/kotlinc/lib/kotlinx-serialization-compiler-plugin.jar -cp "$SCP" File.kt -include-runtime -d out.jar`, then `java -cp "out.jar:$SCP" FileKt`.
    - In Gradle use the `plugin.serialization` plugin, `kotlin("plugin.serialization")`, plus `org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0`. Verify the plugin id against the Kotlin docs.
  - **Jackson 3** (`tools.jackson.*` packages, the current major; check its status and that Spring Boot 4 uses it before claiming so): `jackson-databind-3.2.4.jar` + `jackson-core-3.2.4.jar` + `jackson-annotations-2.22.jar`. Run with `java -cp "$JCP" File.java`. Gradle: `tools.jackson.core:jackson-databind:3.2.4`. Mention Jackson 2 (`com.fasterxml`) in one line, since older tutorials use it.
  - **H2** 2.5.252: `h2-2.5.252.jar`, an in-memory SQL database. JDBC is properly taught in L36, so this is only for the security lesson's injection demo.
- `.build/bin/run.sh` compiles into a private `mktemp -d` folder, so parallel runs are safe.
- JDK 26: `java File.java`, `javap`. Gradle 9.7.1 on PATH.

## Everything verified
- Every code sample shown is a file you actually ran, injected via `<!--CODE:…-->`. Outputs, timings, test reports, stack traces and compiler errors are real captures.
- **Timings and measurements:** show the real numbers from this machine and say so ("on my machine; yours will differ"). Use warm-up runs; never claim precision you don't have. JMH was taught in L24; link it (and use it only if a comparison really needs it).
- Verify every factual claim against an official or canonical source with WebFetch/curl and cite inline:
  - JDK API docs (docs.oracle.com/en/java/javase/25/docs/api/…), Kotlin docs and Kotlin API reference, JLS/JEPs, dev.java (check redirects and `<title>` with curl).
  - RFCs, MDN HTTP docs, Beej's Guide, and OWASP: the phase's primary sources (see below).
  - OpenJDK source on GitHub (github.com/openjdk/jdk) for implementation details the API docs leave unspecified, labelled as "implementation detail, not a guarantee".
  - CLRS / *Effective Java* may be cited by chapter or item number.

## Match L13–L15 exactly in structure and style
Read `W/lessons/0013-generics.html` and `W/lessons/0015-lazy-pipelines-and-idiomatic-kotlin.html`, and their templates in `W/.build/tpl/`, first.
- Top: kicker; h1; subtitle; `.lesson-meta` with `#resume`; `.win`; `.toc` with `data-part`.
- Warm-up: recalls earlier lessons (`.warmup` plus quizzes).
- 3–4 parts, each with:
  - Kotlin/Java `.pair` code and real `.out`/`.err` blocks
  - a "What Kotlin does for you" `.auto` box
  - an `.exercise` with `details.solution`
  - a Check quiz and a `label.done` checkbox
- Final exercise with `.rating` rows (KOTLIN, JAVA, plus a third row where your prompt says so).
- End matter:
  - an exit quiz interleaving earlier lessons
  - the "Build my results" summary and a midweek recall card
  - a `.primary` source box and a reference-sheet link to `../reference/networks-and-web.html` (I'll write that sheet)
  - `.ask`, `.lesson-nav` prev/next, and a reset button
- Scripts: prism 1.29.0 (core + java + kotlin, + bash if used), `../assets/lesson.js`, `../assets/memviz.js` if used, and your own asset if you made one.

## Networking lessons specifically
- **Servers bind to `127.0.0.1` only,** on a high port: pick a free one by binding to port 0, or use 18080+NN. Every server program must shut itself down, after N requests or a timeout. Run client and server in the same program, or start the server in the background, run the client, then kill the server. Never leave a process running, and never hang (macOS has no `timeout`; use `perl -e 'alarm N; exec @ARGV' cmd` or in-program timeouts).
- **Outbound network is available, but be a good citizen.**
  - Only light, read-only requests to `example.com`/`example.org` (IANA-reserved for documentation), `dig`/`nslookup` lookups, one `traceroute -m 8 example.com` at most, and `openssl s_client -connect example.com:443 -servername example.com </dev/null`.
  - No scanning, no load tests against anyone else's servers, no credentials.
  - Real captured output only; where output contains IPs or timing, say it will differ.
- **Tools that need sudo are labelled "not run here"** (`tcpdump`, Wireshark capture), cited, never faked. `nc`, `curl -v`, `dig`, `lsof -i`, `netstat -an`/`lsof` and `openssl` are available.
- **Sources:**
  - RFCs via rfc-editor.org, cited by number and section: RFC 9293 TCP, RFC 768 UDP, RFC 791 IPv4, RFC 8200 IPv6, RFC 1034/1035 DNS, RFC 9110 HTTP Semantics, RFC 9111 HTTP Caching, RFC 9112 HTTP/1.1, RFC 8446 TLS 1.3, RFC 8259 JSON.
  - MDN Web Docs (HTTP, Caching, CORS, cookies) and Beej's Guide to Network Programming.
  - The OWASP Top 10 2025 (top10.owasp.org/2025; verify the current list and numbering) and the OWASP Cheat Sheet Series (Password Storage, SQL Injection Prevention, XSS Prevention).
  - JDK API docs: `java.net` (`Socket`, `ServerSocket`, `InetAddress`, `DatagramSocket`), `java.net.http.HttpClient`, `com.sun.net.httpserver.HttpServer` (verify it's a supported, exported JDK API module `jdk.httpserver`), `java.security`/`javax.crypto`.
- **Kotlin vs Java:** keep the side-by-side discipline. Kotlin uses the same JDK networking APIs; show `use {}` for sockets, coroutines vs virtual threads for many connections (callbacks to L28/L29), kotlinx.serialization vs Jackson, and data classes vs records as DTOs. Say clearly when Kotlin has nothing extra.
- **Security lesson:** demos attack only your own localhost toy apps. Explain each vulnerability with a minimal vulnerable version and a fixed version. No real exploits against real systems; no malware-like code.
- **Bridge:**
  - Spring's `@RestController` and Jackson
  - Android's networking: OkHttp/Retrofit, the main-thread rule (`NetworkOnMainThreadException`, from docs), and cleartext HTTP blocked by default since Android 9 (Network Security Config, from docs)
  - Name them only and cite official docs; no framework code.

## Quiz rules
- Every option in a question has exactly the same number of words. Code options in `<code>` are exempt, but never mix code and text options in one question.
- For "what happens" questions use the 2-word patterns `Prints true` / `Prints false` / `Throws exception` / `Compile error` or `Gives 0` / `Gives null` …
- Big O questions can use code options like `<code>O(n)</code>`, as long as every option in that question is code.
- Vary the position of the correct answer. No formatting clues. Each `.why` explains, with a citation where relevant.

## Level and size
- A CS student with basic programming experience who mostly codes with AI and wants to code by hand.
- All code in Kotlin and Java; always point out what Kotlin does automatically that Java makes explicit.
- About 90–110 minutes, covering every listed topic with nothing skimmed. Exercises are typed from a blank file.
- Add an Android/Spring "bridge" aside where natural, without teaching the framework. Phase 9 is what Spring's web layer and Android's networking stand on.

## QA before you finish
- `python3 W/.build/bin/qa.py "W"`: your lesson must print OK. Its only allowed missing link is `../reference/networks-and-web.html` (and sibling lessons not yet built).
- `python3 W/.build/bin/chrome_qa.py "W"`: your lesson must show `sw` ≤ 500 and `errs=[]`. It runs on every page; ignore other pages.
- Screenshot every widget or memviz with headless Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --headless=new --screenshot=… --window-size=…`) and look at it.

## Final report (under 400 words)
1. What you built, and any deviations.
2. New styles/assets.
3. An HTML snippet of **2 `<section>` blocks** for the shared reference sheet, in the same format as `W/reference/generics-and-functions.html`. Keep each code cell short (under ~35 characters per `<code>`), because sheet columns are narrow.
4. Anything unverified.

## The Phase 9 outline (shared, so warm-ups and callbacks reference the right material)
- **L30 How Bytes Travel** (`0030-how-bytes-travel.html`): the layered model (link/IP/transport/application, with OSI mentioned), IP addresses/IPv4 vs IPv6, routing (`traceroute`), ports, TCP (handshake, reliable ordered byte stream, flow/congestion control in a paragraph) vs UDP (datagrams); DNS (resolution chain, record types, `dig`, `InetAddress`, TTL caching); sockets: a hand-written TCP echo server and client (`ServerSocket`/`Socket`, a thread or virtual thread per connection, callback L28), framing (TCP is a stream, so you need line/length framing), a UDP sender/receiver, `nc` as a test client.
- **L31 HTTP** (`0031-http.html`): HTTP/1.1 as text over TCP (send a raw request with a socket and with `nc`), methods, status codes, headers, `curl -v` as a debugging tool, `java.net.http.HttpClient` (sync/async), HTTP/2 and 3 briefly; HTTPS/TLS at a high level (certificates, chain of trust, `openssl s_client`, handshake idea, what TLS does and doesn't protect); HTTP caching (`Cache-Control` max-age/no-cache/no-store, `ETag`/`If-None-Match` → 304, `Last-Modified`, freshness, shown with a local server and curl).
- **L32 JSON & REST** (`0032-json-and-rest.html`): JSON syntax and types (RFC 8259), number pitfalls (L2 doubles, big ids), serialisation with kotlinx.serialization vs Jackson 3 (DTOs as data classes/records, unknown fields, nulls/defaults, dates as ISO-8601 strings); REST (resources and URIs, verbs, safe vs idempotent per RFC 9110, status codes for CRUD, statelessness); API design (naming, pagination, filtering, versioning, error bodies as RFC 9457 Problem Details, validation). Final: serve JSON from the JDK's built-in `HttpServer`, with no framework (a small CRUD API, tested with curl and `HttpClient`). Phase 9 milestone.
- **L33 Security Basics** (`0033-security-basics.html`): hashing vs encryption vs encoding (SHA-256 via `MessageDigest`, AES-GCM via `javax.crypto`, Base64 is not encryption), HMAC; storing passwords (salted slow hashes: PBKDF2 in the JDK with OWASP's iteration count, bcrypt/Argon2 named from the OWASP cheat sheet, constant-time compare); OWASP Top 10 2025 highlights: injection (SQL injection against an H2 in-memory DB with string concatenation vs `PreparedStatement`), XSS (an HTML page from the L32 server echoing input, then escaping), broken access control (IDOR on `/users/{id}`), plus secrets in code, dependency risks (callback L25 Gradle), HTTPS everywhere.
