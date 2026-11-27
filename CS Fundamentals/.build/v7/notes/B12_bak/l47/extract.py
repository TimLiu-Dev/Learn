"""Copy verbatim line ranges from the tested L45/L41/L43 sources into shelf/ excerpt files.
Run from .build/l47:  python3 extract.py   (it re-checks every range, so a drifted source fails loudly)."""
from pathlib import Path

B = Path(__file__).resolve().parent.parent           # .build
K = "l45/cap_kotlin/app/src/main/kotlin/shelf/"
J = "l45/cap_java/app/src/main/java/shelf/"

# name: (source, [(first, last)], must_contain)  line numbers are 1-based and inclusive
EXCERPTS = {
    "k_router.kt":  (K + "http/Router.kt", [(81, 102)], "fun handle(exchange: HttpExchange)"),
    "j_router.java": (J + "http/Router.java", [(70, 94)], "public void handle(HttpExchange exchange)"),
    "k_routes.kt":  (K + "http/Routes.kt", [(26, 28), (67, 77)], 'post("/loans")'),
    "j_routes.java": (J + "http/Routes.java", [(39, 40), (76, 86)], 'r.post("/loans"'),
    "k_root.kt":    (K + "App.kt", [(18, 34)], "class Shelf(dbFile: Path"),
    "j_root.java":  (J + "Shelf.java", [(20, 37)], "public Shelf(Path dbFile"),
    "k_problem.kt": (K + "http/Json.kt", [(22, 35)], "fun problem(e: ApiError"),
    "j_problem.java": (J + "http/Json.java", [(17, 18), (38, 41)], "static Response problem(ApiError e"),
    "k_tx.kt":      (K + "store/Db.kt", [(18, 28)], "inline fun <T> transaction"),
    "j_tx.java":    (J + "store/Db.java", [(55, 70)], "public <T> T transaction(Work<T> work)"),
    "k_cache.kt":   ("l41/gr_kotlin/app/src/main/kotlin/cache/Catalog.kt", [(53, 83)], "class CachedCatalog"),
    "j_cache.java": ("l41/gr_java/app/src/main/java/cache/Catalog.java", [(67, 100)], "class CachedCatalog"),
    "k_bucket.kt":  ("l43/gr_kotlin/app/src/main/kotlin/gateway/Resilience.kt", [(10, 30)], "class TokenBucket"),
    "j_bucket.java": ("l43/gr_java/app/src/main/java/gateway/TokenBucket.java", [(5, 32)], "class TokenBucket"),
    "k_test.kt":    ("l45/cap_kotlin/app/src/test/kotlin/shelf/ShelfApiTest.kt", [(16, 34)], "Shelf(dbFile, 0, clock)"),
    "j_test.java":  ("l45/cap_java/app/src/test/java/shelf/ShelfApiTest.java", [(19, 43)], "new Shelf(dbFile, 0, clock)"),
}

out = Path(__file__).resolve().parent / "shelf"
out.mkdir(exist_ok=True)
for name, (src, ranges, needle) in EXCERPTS.items():
    lines = (B / src).read_text().splitlines()
    parts = []
    for first, last in ranges:
        assert last <= len(lines), (name, src, last, len(lines))
        parts.append("\n".join(lines[first - 1:last]).rstrip())
    body = "\n\n    // ...\n\n".join(parts)
    assert needle in body, (name, needle)
    where = ", ".join(f"{a}–{b}" for a, b in ranges)
    header = f"// Excerpt of {src.split('/src/')[0].split('/')[0].upper()} {src.split('/')[-1]}, lines {where} (tested there; not a standalone file)"
    (out / name).write_text(header + "\n" + body + "\n")
    print(f"{name:16} <- {src} {where}")
