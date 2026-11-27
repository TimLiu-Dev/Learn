"""Build .build/tpl/0047.html from tpl_src.html.

  python3 extract.py   # refresh the Shelf excerpts in shelf/
  python3 fill.py      # paste captures (<!--OUT:file-->) and the map spec (<!--MAPVIZ-->)
  cd ../bin && python3 build.py ../tpl/0047.html ../../lessons/0047-what-spring-boot-would-have-done.html

Every "You built" cell in the map is a verbatim line range from the tested L45/L41/L43 sources;
grab() asserts the range still contains the expected text, so drift fails loudly.
"""
import html
import json
import re
from pathlib import Path

HERE = Path(__file__).resolve().parent
B = HERE.parent
K = "l45/cap_kotlin/app/src/main/kotlin/shelf/"


def grab(src, first, last, needle):
    lines = (B / src).read_text().splitlines()[first - 1:last]
    text = "\n".join(lines)
    assert needle in text, (src, first, last, needle)
    pad = min(len(l) - len(l.lstrip()) for l in lines if l.strip())
    return "\n".join(l[pad:] for l in lines)


def row(piece, lesson, spring, shelf, spring_code, spring_note, android_note, hides, misses, android_none=False):
    return {
        "piece": piece, "lesson": lesson, "spring": spring,
        "cells": [
            {"code": shelf[0], "note": shelf[1]},
            {"code": spring_code, "note": spring_note},
            {"note": android_note, "none": android_none},
        ],
        "hides": hides, "misses": misses,
    }


ROWS = [
    row("Router", "L45", "@RestController + @GetMapping",
        (grab(K + "http/Routes.kt", 26, 28, 'get("/books/{id}")'), "A path template, a lambda, and req.id() parsing the id."),
        '@GetMapping("/books/{id}")\nfun book(@PathVariable id: Long): BookJson',
        "DispatcherServlet finds the method; the path variable is converted to Long for you (a failed conversion is a 400).",
        "No server-side router: an app is the client. Retrofit describes the same routes as an annotated interface.",
        "The dispatcher is your Router.match plus Request.id(), found by reflection over annotations (L25).",
        "Choosing URLs, status codes and what a resource is (Fielding ch. 5, L32)."),
    row("JSON", "L32, L45", "Jackson 3, auto-configured",
        (grab(K + "http/Json.kt", 26, 29, "bodyAs"), "kotlinx.serialization in Kotlin, Jackson 3 in Java, called by hand."),
        "fun add(@RequestBody book: NewBook): Book",
        "Spring Boot 4: \"Jackson 3 is the preferred and default library\"; a JsonMapper bean is auto-configured.",
        "kotlinx.serialization or Moshi converters plugged into Retrofit.",
        "@RequestBody and return values go through HttpMessageConverters: your bodyAs() and ok().",
        "Deciding unknown-key and null policies (L32 showed the two libraries disagree)."),
    row("Problem Details", "L45", "ProblemDetail + @ControllerAdvice",
        (grab(K + "http/Json.kt", 33, 35, "fun problem"), "Your own RFC 9457 body and media type."),
        "ProblemDetail.forStatusAndDetail(\n    HttpStatus.CONFLICT, e.message)",
        "spring.mvc.problemdetails.enabled=true renders Spring MVC's own errors this way too.",
        "Not applicable on the client: an app reads Problem bodies, it doesn't produce them.",
        "ResponseEntityExceptionHandler is a ready-made version of your catch blocks.",
        "Writing useful detail messages without leaking internals (L33).", android_none=True),
    row("500 and Errors", "L45", "DispatcherServlet catches Throwable",
        (grab(K + "http/Router.kt", 97, 100, "catch (e: Exception)"), "Catches Exception only, so an Error gave the client silence (L45)."),
        "catch (Throwable err) {\n  dispatchException = new ServletException(\n    \"Handler dispatch failed: \" + err, err);\n}",
        "From DispatcherServlet.doDispatch (Spring Framework 7.0.10 source): Errors are wrapped so @ExceptionHandler methods can match the cause.",
        "An uncaught exception on Android crashes the app process.",
        "The wrap-and-match-the-cause logic you rebuilt in MiniSpring (Part 1).",
        "Logging the real failure and deciding what the client may see.", android_none=True),
    row("Composition root", "L38, L45", "ApplicationContext + constructor injection",
        (grab(K + "App.kt", 24, 31, "Db(dbFile)"), "You named every concrete class in one place."),
        "@Service\nclass BookService(private val store: BookStore)",
        "Component scanning finds the classes; Spring calls the constructors. \"The Spring team generally advocates constructor injection.\"",
        "Hilt: \"a dependency injection library for Android that reduces the boilerplate of doing manual dependency injection\".",
        "The container is L25's reflective DI container, grown up: scanning, ordering, lifecycles.",
        "Designing the interfaces (L38). A container wires bad dependencies just as happily."),
    row("Stores (JDBC)", "L36, L45", "JdbcClient / Spring Data",
        (grab(K + "store/SqliteStores.kt", 27, 27, "override fun find"), "Your Connection.query helper and a hand-written row mapper."),
        "jdbc.sql(\"... WHERE b.id = ?\").param(id)\n    .query(Book::class.java).optional()",
        "JdbcClient (since 6.1) is auto-configured; it maps columns to a record or data class by name.",
        "Room: \"an abstraction layer over SQLite\" with compile-time checked queries.",
        "Connection handling, PreparedStatement binding and row mapping.",
        "The schema, the indexes and the SQL (L34–L35). N+1 is still possible (L36)."),
    row("Transactions", "L36, L45", "@Transactional (a proxy)",
        (grab(K + "store/Db.kt", 20, 28, "c.rollback()"), "autoCommit off, commit, roll back on any Throwable."),
        "@Transactional\nfun lend(bookId: Long, memberId: Long): Loan",
        "Default: roll back on RuntimeException and Error; checked exceptions commit. Self-invocation is not intercepted.",
        "Room's @Transaction on a DAO method runs it in a database transaction.",
        "A proxy wraps the call in your begin/commit/rollback (TxProxy, Part 2).",
        "Where the transaction boundary goes, and the race you fixed in L46."),
    row("Cache", "L41, L46", "@Cacheable(sync = true) / @CacheEvict",
        (grab("l41/gr_kotlin/app/src/main/kotlin/cache/Catalog.kt", 58, 65, "inFlight.putIfAbsent"), "Cache-aside plus request coalescing, by hand."),
        "@Cacheable(cacheNames = [\"books\"], sync = true)\nfun find(id: Long): Book?",
        "Needs @EnableCaching; providers include Caffeine and Redis. sync = true lets one thread load a missing key.",
        "Coil's memory and disk caches for images.",
        "Your L39 decorator plus L41's coalescing, applied by a proxy.",
        "Invalidation, TTLs, and whether to cache \"not found\" (an absent Optional caches null)."),
    row("Rate limiter", "L43, L46", "Nothing built in (filter or gateway)",
        (grab("l43/gr_kotlin/app/src/main/kotlin/gateway/Resilience.kt", 15, 20, "fun tryAcquire"), "A token bucket per client, 429 + Retry-After."),
        "filters:\n  - name: RequestRateLimiter",
        "Spring MVC has no rate limiter. Spring Cloud Gateway's RequestRateLimiter uses a Redis token bucket.",
        "Client side: back off and retry; WorkManager reschedules work that must run reliably.",
        "Only at the gateway, where the bucket lives in Redis.",
        "The policy: who is a client, what limits, what to send back (L43)."),
    row("Logs, metrics, traces", "L43, L46", "Actuator + Micrometer",
        (grab("l43/gr_kotlin/app/src/main/kotlin/gateway/Telemetry.kt", 10, 12, "fun json"), "JSON log lines, a hand-written histogram, traceparent."),
        "management.endpoints.web.exposure.include=\n    health,metrics,prometheus",
        "Only /actuator/health is exposed by default. Micrometer Tracing supports OpenTelemetry and Brave.",
        "Logcat for logs; production metrics come from separate libraries.",
        "Counters, histograms and trace propagation (L43), done consistently for every endpoint.",
        "Choosing what to measure and alert on: the golden signals (L43)."),
    row("Configuration", "L33, L46", "application.properties + env binding",
        (grab(K + "App.kt", 47, 49, "args.getOrNull"), "v1 read argv; L46 moved to environment variables."),
        "shelf.db=shelf.db        # SHELF_DB overrides it",
        "Relaxed binding: dots become underscores, dashes are removed, uppercase. spring.main.log-startup-info → SPRING_MAIN_LOGSTARTUPINFO.",
        "Build variants and resource files; secrets stay off the device.",
        "Parsing, type conversion and precedence rules for config sources.",
        "Keeping secrets out of the repository (L33)."),
    row("Integration tests", "L11, L45", "@SpringBootTest + RestTestClient",
        (grab("l45/cap_kotlin/app/src/test/kotlin/shelf/ShelfApiTest.kt", 18, 20, "Shelf(dbFile, 0, clock)"), "Port 0, a temp database, real HTTP."),
        "@SpringBootTest(webEnvironment = RANDOM_PORT)\n@AutoConfigureRestTestClient",
        "RANDOM_PORT starts a real server; @WebMvcTest slices load only the web layer; @MockitoBean replaces a bean.",
        "Local tests plus instrumented tests on a device; Hilt has its own test support.",
        "Starting the app and wiring a client: your ShelfApiTest set-up.",
        "Deciding what to test and keeping fakes honest (contract tests, L38)."),
    row("Graceful shutdown", "L46", "On by default",
        (grab(K + "App.kt", 40, 43, "server.stop(0)"), "v1 stopped at once; L46 drained requests first."),
        "spring.lifecycle.timeout-per-shutdown-phase=20s",
        "\"Graceful shutdown is enabled by default\"; server.shutdown=immediate turns it off.",
        "The Activity and process lifecycle decide when an app stops.",
        "Stop accepting, wait for in-flight requests, then close.",
        "Making requests short and idempotent so a restart is safe."),
    row("Runnable app", "L25, L46", "Executable jar (bootJar)",
        ("./gradlew installDist\nbuild/install/app/bin/app", "A start script and a lib/ folder (L25)."),
        "./gradlew bootJar\njava -jar build/libs/app.jar",
        "bootJar builds one jar with every dependency inside; bootRun runs without building one.",
        "An APK or app bundle, built by the Android Gradle plugin.",
        "Packaging the classpath (L25's fat-jar recipe, done properly).",
        "Choosing a JDK, memory limits and how to deploy (mission B)."),
    row("Threads", "L28, L45", "spring.threads.virtual.enabled=true",
        (grab(K + "App.kt", 20, 20, "newVirtualThreadPerTaskExecutor"), "One virtual thread per request."),
        "spring.threads.virtual.enabled=true",
        "Opt-in. The docs warn about pinned virtual threads and strongly recommend Java 24 or later.",
        "The main thread must never block: coroutines and lifecycle-aware Flow collection (L29).",
        "Which executor runs your handler.",
        "Shared mutable state is still a race (L26–L27, L46)."),
]

SPEC = {"columns": ["You built (Shelf)", "Spring Boot 4.1 (docs, not run)", "Android"], "rows": ROWS}


PHASES = [  # (number, name, lessons in study order, reference sheets); v5 lessons L48–L57 slot in by study order
    (1, "Foundations Sprint", [1, 2, 3], ["cli-git-cheatsheet", "kotlin-java-basics"]),
    (2, "Memory, Part 1: Where Things Live", [4, 5], ["memory-and-equality"]),
    (3, "Objects & Types", [6, 7, 8, 9], ["classes-and-types"]),
    (4, "Errors, Testing & Debugging", [10, 11, 12], ["errors-testing-debugging"]),
    (5, "Generics & Functional Style", [13, 14, 15], ["generics-and-functions"]),
    (6, "Data Structures & Algorithms", [48, 16, 17, 18, 19, 20, 21, 49, 50, 51], ["data-structures-and-algorithms", "maths-and-theory"]),
    (7, "How the Machine Runs Your Code", [52, 22, 53, 23, 54, 24, 25], ["runtime-and-os", "shell"]),
    (8, "Concurrency", [26, 27, 28, 29], ["concurrency"]),
    (9, "Networks & the Web", [30, 31, 32, 33, 55], ["networks-and-web"]),
    (10, "Databases", [34, 35, 36, 56], ["databases"]),
    (11, "Design & Clean Code", [37, 38, 39, 40], ["design-and-clean-code"]),
    (12, "System Design & Caching", [41, 42, 43, 57, 44], ["system-design"]),
    (13, "Capstone: No-Framework REST API", [45, 46, 47], ["capstone"]),
]
EXTRA = {  # lessons that may not exist yet when this lesson is built
    46: ("0046-make-it-production-shaped.html", "L46 · Make It Production-Shaped"),
    47: ("0047-what-spring-boot-would-have-done.html", "L47 · What Spring Boot Would Have Done"),
}


def lessons():
    found = {}
    for f in sorted((B.parent / "lessons").glob("0*.html")):
        n = int(f.name[:4])
        title = re.search(r"<title>(.*?)</title>", f.read_text()).group(1)
        found[n] = (f.name, title)
    for n, v in EXTRA.items():
        found.setdefault(n, v)
    return found


def sheet_title(name):
    f = B.parent / "reference" / f"{name}.html"
    return re.search(r"<title>(.*?)</title>", f.read_text()).group(1) if f.exists() else "Capstone"


def phasemap():
    ls = lessons()
    rows = []
    for num, name, ns, sheets in PHASES:
        links = " · ".join(f'<a href="{ls[n][0]}">{ls[n][1].split(" · ", 1)[0]}</a>' for n in ns)
        refs = " · ".join(f'<a href="../reference/{s}.html">{sheet_title(s)}</a>' for s in sheets)
        rows.append(f'  <tr><td class="pm-n">{num}</td><td><a href="../reference/roadmap.html#p{num}">{html.escape(name)}</a></td>'
                    f'<td class="pm-l">{links}</td><td>{refs}</td></tr>')
    return '<div class="table-scroll"><table class="phasemap">\n  <tr><th>#</th><th>Phase</th><th>Lessons</th><th>Reference sheet</th></tr>\n' + "\n".join(rows) + "\n</table></div>"


def span(ns):
    """[48, 16, 17, 18] -> 'L48, L16–L18': consecutive runs collapse to ranges."""
    runs = [[ns[0]]]
    for n in ns[1:]:
        if n == runs[-1][-1] + 1:
            runs[-1].append(n)
        else:
            runs.append([n])
    return ", ".join(f"L{r[0]}" if len(r) == 1 else f"L{r[0]}–L{r[-1]}" for r in runs)


def ratings():
    out = []
    for num, name, ns, _ in PHASES:
        out.append(f'    <div class="rating" data-id="phase-{num}" data-label="Phase {num}: {html.escape(name)} ({span(ns)})">'
                   f'<span class="who">P{num}</span><button>Solid</button><button>Needed docs</button><button>Stuck</button><button>Skipped</button></div>')
    return '<div class="rating-group">\n' + "\n".join(out) + "\n  </div>"


def main():
    text = (HERE / "tpl_src.html").read_text()

    def out(m):
        return html.escape((HERE / "out" / m.group(1)).read_text().rstrip("\n"), quote=False)

    text = re.sub(r"<!--OUT:([\w.]+)-->", out, text)
    text = text.replace("<!--MAPVIZ-->", json.dumps(SPEC, ensure_ascii=False, indent=1).replace("</", "<\\/"))
    text = text.replace("<!--PHASEMAP-->", phasemap()).replace("<!--RATINGS-->", ratings())
    assert "<!--OUT:" not in text and "<!--MAPVIZ-->" not in text
    (B / "tpl" / "0047.html").write_text(text)
    print("wrote", B / "tpl" / "0047.html", f"({len(ROWS)} map rows)")


if __name__ == "__main__":
    main()
