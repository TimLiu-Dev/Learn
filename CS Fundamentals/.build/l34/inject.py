"""Run every L34 SQL demo through /usr/bin/sqlite3 -echo and inject the verbatim
transcripts into ../tpl/0034.html between <!--TR:name--> and <!--/TR:name--> markers.
Also builds the sqlviz widget spec from real query results (<!--SV-->…<!--/SV-->).

Usage (from .build/l34):  python3 inject.py      then run ../bin/build.py as usual
Each run rebuilds library.db from library.sql, so results are deterministic.
"""
import html, json, re, shutil, subprocess, tempfile
from pathlib import Path

HERE = Path(__file__).resolve().parent
TPL = HERE.parent / "tpl" / "0034.html"
SQLITE = "/usr/bin/sqlite3"

# name -> (database, sql file). "lib" = library.db (read-only use),
# "scratch" = a fresh copy of library.db (for demos that change data), "mem" = :memory:
DEMOS = {
    "affinity": ("mem", "affinity.sql"),
    "schema": ("lib", "schema.sql"),
    "rowid": ("scratch", "rowid.sql"),
    "fk": ("scratch", "fk.sql"),
    "ondelete": ("scratch", "ondelete.sql"),
    "constraints": ("scratch", "constraints.sql"),
    "crud": ("scratch", "crud.sql"),
    "select": ("lib", "select.sql"),
    "precedence": ("lib", "precedence.sql"),
    "filters": ("lib", "filters.sql"),
    "nulls": ("lib", "nulls.sql"),
    "notin": ("scratch", "notin.sql"),
    "orderby": ("lib", "orderby.sql"),
    "paging": ("lib", "paging.sql"),
    "joins": ("lib", "joins.sql"),
    "puzzle": ("lib", "puzzle.sql"),
    "selfjoin": ("lib", "selfjoin.sql"),
    "threeway": ("lib", "threeway.sql"),
    "aggregates": ("lib", "aggregates.sql"),
    "groupby": ("lib", "groupby.sql"),
    "bare": ("lib", "bare.sql"),
    "subqueries": ("lib", "subqueries.sql"),
    "window": ("lib", "window.sql"),
    "alias": ("lib", "alias.sql"),
    "ex1": ("scratch", "ex1.sql"),
    "ex2": ("lib", "ex2.sql"),
    "ex3": ("lib", "ex3.sql"),
    "q1": ("lib", "final/q1.sql"),
    "q2": ("lib", "final/q2.sql"),
    "q3": ("lib", "final/q3.sql"),
    "q4": ("lib", "final/q4.sql"),
    "q5": ("lib", "final/q5.sql"),
    "q6": ("lib", "final/q6.sql"),
    "q7": ("lib", "final/q7.sql"),
    "q8": ("lib", "final/q8.sql"),
}

def capture(argv, stdin_file, cwd):
    """Run argv with stdout+stderr on one pseudo-terminal, so sqlite3 line-buffers
    and error messages appear exactly where they happened (a pipe would reorder them)."""
    import os, pty
    master, slave = pty.openpty()
    p = subprocess.Popen(argv, stdin=open(stdin_file), stdout=slave, stderr=slave, cwd=cwd)
    os.close(slave)
    chunks = []
    while True:
        try:
            data = os.read(master, 4096)
        except OSError:
            break
        if not data:
            break
        chunks.append(data)
    p.wait()
    os.close(master)
    return b"".join(chunks).decode().replace("\r\n", "\n").rstrip("\n")

def run(db, sql_file, work):
    lib = work / "library.db"
    if lib.exists():
        lib.unlink()
    subprocess.run([SQLITE, str(lib)], stdin=open(HERE / "library.sql"), check=True)
    if db == "scratch":
        shutil.copy(lib, work / "scratch.db")
        target, shown = str(work / "scratch.db"), "scratch.db"
    elif db == "lib":
        target, shown = str(lib), "library.db"
    else:
        target, shown = ":memory:", ":memory:"
    out = capture([SQLITE, "-echo", "-header", "-column", "-nullvalue", "NULL", target], HERE / sql_file, work)
    cmd = f"{SQLITE} -echo -header -column -nullvalue NULL {shown} &lt; {sql_file}"
    inputs = {l.rstrip() for l in (HERE / sql_file).read_text().splitlines() if l.strip()}
    lines = []
    for line in out.splitlines():
        e = html.escape(line.rstrip(), quote=False)
        lines.append(f"<b>{e}</b>" if line.rstrip() in inputs else e)
    pre = "\n".join(lines)
    setup = '<span class="p">$ </span>cp library.db scratch.db\n' if db == "scratch" else ""
    return f'<pre class="term">{setup}<span class="p">$ </span>{cmd}\n{pre}</pre>'

def q(db, sql):
    """Rows of a query as lists, straight from /usr/bin/sqlite3 -json."""
    p = subprocess.run([SQLITE, "-json", str(db), sql], capture_output=True, text=True, check=True)
    return [list(r.values()) for r in json.loads(p.stdout or "[]")]

def viz_spec(work):
    db = work / "library.db"
    if db.exists():
        db.unlink()
    subprocess.run([SQLITE, str(db)], stdin=open(HERE / "library.sql"), check=True)
    # --- Mode 1: the logical evaluation order of one query ---------------------------
    sql1 = ("SELECT author_id, count(*) AS books, max(price_cents) AS top\n"
            "FROM books\nWHERE year >= 1980\nGROUP BY author_id\nHAVING count(*) >= 3\n"
            "ORDER BY books DESC, author_id\nLIMIT 2;")
    clauses = ["FROM", "WHERE", "GROUP BY", "HAVING", "SELECT", "ORDER BY", "LIMIT"]
    cols = ["id", "title", "author_id", "year", "price_cents"]
    allrows = q(db, "SELECT id, title, author_id, year, price_cents FROM books ORDER BY id")
    keep = {r[0]: r[1] for r in q(db, "SELECT id, year >= 1980 FROM books")}
    kept = q(db, "SELECT id, title, author_id, year, price_cents FROM books WHERE year >= 1980 ORDER BY author_id, id")
    groups = sorted({r[2] for r in kept})
    gi = {a: f"g{k % 6}" for k, a in enumerate(groups)}
    grouped = q(db, "SELECT author_id, count(*), max(price_cents) FROM books WHERE year >= 1980 GROUP BY author_id ORDER BY author_id")
    having = q(db, "SELECT author_id, count(*), max(price_cents) FROM books WHERE year >= 1980 GROUP BY author_id HAVING count(*) >= 3 ORDER BY author_id")
    ordered = q(db, "SELECT author_id, count(*) AS books, max(price_cents) FROM books WHERE year >= 1980 GROUP BY author_id HAVING count(*) >= 3 ORDER BY books DESC, author_id")
    final = q(db, sql1.replace("\n", " "))
    hk = {r[0] for r in having}
    fk = {r[0] for r in final}
    gcols = ["author_id", "count(*)", "max(price_cents)"]
    ocols = ["author_id", "books", "top"]
    n_drop = sum(1 for v in keep.values() if v != 1)
    steps1 = [
        {"on": 0, "cols": cols, "rows": allrows, "cls": [""] * len(allrows),
         "note": f"<b>FROM books</b> comes first: every row of the table, {len(allrows)} of them. Nothing else exists yet, not even the column aliases."},
        {"on": 1, "cols": cols, "rows": allrows, "cls": ["" if keep[r[0]] == 1 else "drop" for r in allrows],
         "note": f"<b>WHERE year &gt;= 1980</b> tests each row on its own and drops {n_drop}. Beowulf goes too: <code>NULL &gt;= 1980</code> is unknown, and WHERE keeps only rows where the test is true."},
        {"on": 2, "cols": cols, "rows": kept, "cls": [gi[r[2]] for r in kept],
         "note": f"<b>GROUP BY author_id</b> sorts the {len(kept)} survivors into {len(groups)} groups (one colour each). From here on, the query works on groups, not rows."},
        {"on": 3, "cols": gcols, "rows": grouped, "cls": [gi[r[0]] if r[0] in hk else "drop" for r in grouped],
         "note": "<b>HAVING count(*) &gt;= 3</b> is a WHERE for groups. It can use aggregates, because each group now has a count. Groups with fewer than 3 books drop out."},
        {"on": 4, "cols": ocols, "rows": having, "cls": [gi[r[0]] for r in having],
         "note": "<b>SELECT</b> only now computes the output columns and gives them their names: <code>books</code> and <code>top</code>. That's why standard SQL can't use these aliases in WHERE."},
        {"on": 5, "cols": ocols, "rows": ordered, "cls": [gi[r[0]] for r in ordered],
         "note": "<b>ORDER BY books DESC, author_id</b> sorts the result. ORDER BY runs after SELECT, so it <em>can</em> use the alias <code>books</code>. The tie at 3 books is broken by author_id."},
        {"on": 6, "cols": ocols, "rows": ordered, "cls": [gi[r[0]] if r[0] in fk else "drop" for r in ordered],
         "note": f"<b>LIMIT 2</b> keeps the first two rows of the sorted result. Final answer: {len(final)} rows, exactly what the query returns when you run it."},
    ]
    # --- Mode 2: INNER vs LEFT JOIN, and the WHERE puzzle ----------------------------
    sql2 = ("SELECT a.name, b.title, b.year\nFROM authors a LEFT JOIN books b ON b.author_id = a.id\n"
            "WHERE a.id IN (3, 7, 8);   -- then: AND b.year > 2000")
    au = q(db, "SELECT id, name, born FROM authors WHERE id IN (3, 7, 8) ORDER BY id")
    bk = q(db, "SELECT id, title, author_id, year FROM books WHERE author_id IN (3, 7, 8) ORDER BY id")
    inner = q(db, "SELECT a.name, b.title, b.year FROM authors a JOIN books b ON b.author_id = a.id WHERE a.id IN (3, 7, 8) ORDER BY a.id, b.id")
    left = q(db, "SELECT a.name, b.title, b.year, b.id IS NULL FROM authors a LEFT JOIN books b ON b.author_id = a.id WHERE a.id IN (3, 7, 8) ORDER BY a.id, b.id")
    lpass = q(db, "SELECT coalesce(b.year > 2000, 0) FROM authors a LEFT JOIN books b ON b.author_id = a.id WHERE a.id IN (3, 7, 8) ORDER BY a.id, b.id")
    jcols = ["name", "title", "year"]
    lrows = [r[:3] for r in left]
    steps2 = [
        {"on": 0, "cols": ["id", "name", "born"], "rows": au, "cls": [""] * len(au),
         "note": "Three authors on the left: Ted Chiang, Anonymous, and Becky Chambers, who has no books in the library."},
        {"on": 1, "cols": ["id", "title", "author_id", "year"], "rows": bk, "cls": [""] * len(bk),
         "note": "Their books on the right. Every book points at its author with <code>author_id</code>. Nothing points at author 8."},
        {"on": 2, "cols": jcols, "rows": inner, "cls": [""] * len(inner),
         "note": f"<b>INNER JOIN … ON b.author_id = a.id</b> keeps only pairs that match: {len(inner)} rows. Becky Chambers has no match, so she vanishes."},
        {"on": 3, "cols": jcols, "rows": lrows, "cls": ["add" if r[3] else "" for r in left],
         "note": f"<b>LEFT JOIN</b> keeps every left row. Becky Chambers gets one row with NULLs in the book columns (green): {len(left)} rows."},
        {"on": 4, "cols": jcols, "rows": lrows, "cls": ["" if p[0] == 1 else "drop" for p in lpass],
         "note": "Now add <b>WHERE b.year &gt; 2000</b>. WHERE runs after the join and drops every row where the test isn't true, including Becky's NULL row. The LEFT JOIN quietly became an INNER JOIN. Put the condition in <code>ON</code> instead to keep her."},
    ]
    return {"modes": [
        {"name": "Logical order", "sql": sql1, "clauses": clauses, "steps": steps1},
        {"name": "INNER vs LEFT JOIN", "sql": sql2,
         "clauses": ["authors", "books", "INNER JOIN", "LEFT JOIN", "+ WHERE"], "steps": steps2},
    ]}

def main():
    text = TPL.read_text()
    with tempfile.TemporaryDirectory() as d:
        work = Path(d)
        for name, (db, f) in DEMOS.items():
            block = run(db, f, work)
            pat = re.compile(rf"<!--TR:{name}-->.*?<!--/TR:{name}-->", re.S)
            if not pat.search(text):
                print("no marker for", name); continue
            text = pat.sub(lambda m: f"<!--TR:{name}-->{block}<!--/TR:{name}-->", text)
            print("ok", name)
        spec = viz_spec(work)
        block = f'<script type="application/json" id="sv-l34">{json.dumps(spec)}</script>'
        text = re.sub(r"<!--SV-->.*?<!--/SV-->", lambda m: f"<!--SV-->{block}<!--/SV-->", text, flags=re.S)
        print("ok sqlviz")
    TPL.write_text(text)

if __name__ == "__main__":
    import sys
    if len(sys.argv) > 1:   # preview one demo:  python3 transcripts.py fk
        with tempfile.TemporaryDirectory() as d:
            for n in sys.argv[1:]:
                print(run(*DEMOS[n], Path(d)), "\n")
    else:
        main()
