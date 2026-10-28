"""Turn tpl_src.html into ../tpl/0045.html by pasting real captures in.

Markers (all paths relative to this folder):
  <!--OUT:captures/x.txt-->              whole file, HTML-escaped
  <!--OUT:captures/x.txt@21-26,58,186-->  only those 1-based lines; "…" between gaps
  <!--OUT:captures/x.txt~regex-->        only lines matching the regex
  <!--VIZDATA-->                         routeviz spec built from captures/kt_viz.txt
Then run: cd ../bin && python3 build.py ../tpl/0045.html ../../lessons/0045-build-the-core.html
"""
import html
import json
import re
from pathlib import Path

HERE = Path(__file__).parent


def lines_of(path):
    return (HERE / path).read_text().replace("\r", "").rstrip("\n").split("\n")


def pick(spec):
    if "@" in spec:
        path, ranges = spec.split("@", 1)
        lines = lines_of(path)
        out, last = [], None
        for part in ranges.split(","):
            a, _, b = part.partition("-")
            a, b = int(a), int(b or a)
            if last is not None and a != last + 1:
                out.append("…")
            out.extend(lines[a - 1:b])
            last = b
        return "\n".join(out)
    if "~" in spec:
        path, rx = spec.split("~", 1)
        return "\n".join(l for l in lines_of(path) if re.search(rx, l))
    return "\n".join(lines_of(spec))


def viz():
    text = (HERE / "captures/kt_viz.txt").read_text().replace("\r", "")
    resp = {m.group(1): m.group(2).strip() for m in re.finditer(r"### (\w+)\n(.*?)(?=\n### |\Z)", text, re.S)}
    layers = ["HttpServer · context \"/\"", "Router.match", "Handler (Routes)", "Service", "Store · SQL", "Response"]
    s = lambda layer, note, code=None: {"layer": layer, "note": note, **({"code": code} if code else {})}
    scenarios = [
        {"name": "POST 201", "request": 'POST /books  {"title":"Dune","author":"Frank Herbert","year":1965}', "response": resp["post201"],
         "steps": [
             s(0, "The only context is <code>\"/\"</code>, the longest matching prefix of every path, so every request lands in <code>Router.handle</code> on a virtual thread."),
             s(1, "<code>match(\"POST\", \"/books\")</code> finds the template <code>/books</code> registered for POST: <b>Found</b>, no path parameters.", 'post("/books") { req -> ... }'),
             s(2, "The handler parses the body into a <code>BookIn</code> DTO and validates it at the boundary. Valid, so it becomes a <code>NewBook</code> command.", "req.bodyAs<BookIn>().validate()"),
             s(3, "<code>BookService.create</code> has no rules to check here; it hands the command to the store.", "books.add(title, author, year)"),
             s(4, "One transaction: find-or-create the author row, then insert the book with a <code>?</code> placeholder for every value.", "INSERT INTO books (title, author_id, year) VALUES (?, ?, ?)"),
             s(5, "201 with a <code>Location</code> header naming the new resource, and the book as JSON (RFC 9110 §9.3.3)."),
         ]},
        {"name": "GET 200", "request": "GET /books/1", "response": resp["get200"],
         "steps": [
             s(0, "Same context, same entry point: <code>Router.handle</code>."),
             s(1, "<code>/books/1</code> fits the template <code>/books/{id}</code>: <b>Found</b>, with params <code>{id=1}</code>.", 'get("/books/{id}") { req -> ... }'),
             s(2, "<code>req.id()</code> turns <code>\"1\"</code> into a <code>Long</code>; a non-number would be a 404.", "books.get(req.id())"),
             s(3, "<code>BookService.get</code> just asks the store.", "books.find(1)"),
             s(4, "A read on a fresh connection: a join to get the author's name.", "SELECT … FROM books b JOIN authors a … WHERE b.id = ?"),
             s(5, "200 and the book as JSON, mapped from the domain <code>Book</code> through <code>BookOut</code>."),
         ]},
        {"name": "Loan 409", "request": 'POST /loans  {"bookId":1,"memberId":1}   (book 1 is already lent)', "response": resp["loan409"],
         "steps": [
             s(0, "Into <code>Router.handle</code>."),
             s(1, "<code>/loans</code> + POST: <b>Found</b>.", 'post("/loans") { req -> ... }'),
             s(2, "The body becomes a <code>LoanIn</code>; both ids are present, so validation passes.", "lending.lend(bookId, memberId)"),
             s(3, "The service checks its rules in order: the book exists, the member exists, then <b>is the book already out?</b> Yes, so it returns <code>BookOnLoan</code> without writing anything.", "loans.openForBook(1) != null -> BookOnLoan"),
             s(4, "The check was a read that used the partial unique index (Part 3).", "SELECT … FROM loans WHERE book_id = ? AND returned IS NULL"),
             s(5, "The handler maps <code>BookOnLoan</code> to <code>409 Conflict</code> as Problem Details (RFC 9457)."),
         ]},
        {"name": "GET 404", "request": "GET /books/99", "response": resp["get404"],
         "steps": [
             s(0, "Into <code>Router.handle</code>."),
             s(1, "The template matches: <b>Found</b>, <code>{id=99}</code>. The router can't know whether book 99 exists.", 'get("/books/{id}") { req -> ... }'),
             s(2, "The handler asks for the book and gets <code>null</code>, so it throws <code>ApiError.notFound</code>.", "?: throw ApiError.notFound(req.path)"),
             s(3, "<code>BookService.get</code> returned null.", "books.find(99)"),
             s(4, "The query found no row.", "SELECT … WHERE b.id = ?"),
             s(5, "<code>Router.handle</code> catches the <code>ApiError</code> and renders it as a 404 problem."),
         ]},
        {"name": "PATCH 405", "request": "PATCH /books/1", "response": resp["patch405"],
         "steps": [
             s(0, "Into <code>Router.handle</code>."),
             s(1, "<code>/books/{id}</code> matches the path, but only for DELETE, GET and PUT: <b>WrongMethod</b>. No handler runs, and the database is never touched.", "Match.WrongMethod([DELETE, GET, PUT])"),
             s(5, "405 with an <code>Allow</code> header listing the methods that would work: RFC 9110 says a 405 MUST include it."),
         ]},
    ]
    return json.dumps({"layers": layers, "scenarios": scenarios}, ensure_ascii=False, indent=1)


src = (HERE / "tpl_src.html").read_text()
src = re.sub(r"<!--OUT:([^>]+?)-->", lambda m: html.escape(pick(m.group(1)), quote=False), src)
src = src.replace("<!--VIZDATA-->", viz())
assert "<!--OUT:" not in src
(HERE.parent / "tpl/0045.html").write_text(src)
print("wrote ../tpl/0045.html")
