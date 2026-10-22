"""Generate the final exercise's commit snapshots from final/start.

Each step directory holds full copies of only the files that commit changes.
Branch commits c1..c4 (+fixup) are authored on top of the ORIGINAL main;
main_update is the teammate's change that lands on main meanwhile.
"""
import os, shutil, sys

F = sys.argv[1]
START = os.path.join(F, "start")
KT = "books_kt/app/src/main/kotlin/books/"
KTT = "books_kt/app/src/test/kotlin/books/"
JV = "books_java/app/src/main/java/books/"
JVT = "books_java/app/src/test/java/books/"

tree = {}
for root, _, files in os.walk(START):
    for f in files:
        full = os.path.join(root, f)
        try:
            tree[os.path.relpath(full, START)] = open(full, encoding="utf-8").read()
        except UnicodeDecodeError:
            pass                                    # binary (gradle-wrapper.jar): never edited
base = dict(tree)


def edit(path, old, new, count=1):
    s = tree[path]
    assert s.count(old) == count, (path, old[:60], s.count(old))
    tree[path] = s.replace(old, new)


def snapshot(step, paths):
    for p in paths:
        dst = os.path.join(F, "steps", step, p)
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        open(dst, "w", encoding="utf-8").write(tree[p])


INLINE_KT = '''        val errors = mutableListOf<String>()
        if (input.title == null) errors += "title is required"
        else if (input.title.isBlank()) errors += "title must not be blank"
        if (input.year == null) errors += "year is required"
        else if (input.year !in 1450..2100) errors += "year must be between 1450 and 2100"
'''
INLINE_JV = '''        List<String> errors = new ArrayList<>();
        if (input.title() == null) errors.add("title is required");
        else if (input.title().isBlank()) errors.add("title must not be blank");
        if (input.year() == null) errors.add("year is required");
        else if (input.year() < 1450 || input.year() > 2100) errors.add("year must be between 1450 and 2100");
'''

# ---------- main_update: the teammate's change on main (from the original start) ----------
edit(KT + "BookApi.kt", INLINE_KT, INLINE_KT.replace("1450", "1440"), 2)
edit(JV + "BookApi.java", INLINE_JV, INLINE_JV.replace("1450", "1440"), 2)
edit(KTT + "BookApiTest.kt", "between 1450 and 2100", "between 1440 and 2100")
edit(JVT + "BookApiTest.java", "between 1450 and 2100", "between 1440 and 2100")
snapshot("main_update", [KT + "BookApi.kt", JV + "BookApi.java", KTT + "BookApiTest.kt", JVT + "BookApiTest.java"])
tree = dict(base)                                     # branch work starts from the ORIGINAL main

# ---------- c1: extract the duplicated validation ----------
edit(KT + "BookApi.kt", INLINE_KT, "        val errors = validate(input)\n", 2)
tree[KT + "Model.kt"] += '''
fun validate(b: NewBook): List<String> = buildList {
    when {
        b.title == null -> add("title is required")
        b.title.isBlank() -> add("title must not be blank")
    }
    when {
        b.year == null -> add("year is required")
        b.year !in 1450..2100 -> add("year must be between 1450 and 2100")
    }
}
'''
edit(JV + "BookApi.java", INLINE_JV, "        List<String> errors = Model.validate(input);\n", 2)
edit(JV + "Model.java", "import java.util.List;\n", "import java.util.ArrayList;\nimport java.util.List;\n")
edit(JV + "Model.java", '''    public record Problem(String type, String title, int status, String detail, List<String> errors) {}
}
''', '''    public record Problem(String type, String title, int status, String detail, List<String> errors) {}

    public static List<String> validate(NewBook b) {
        var errors = new ArrayList<String>();
        if (b.title() == null) errors.add("title is required");
        else if (b.title().isBlank()) errors.add("title must not be blank");
        if (b.year() == null) errors.add("year is required");
        else if (b.year() < 1450 || b.year() > 2100) errors.add("year must be between 1450 and 2100");
        return errors;
    }
}
''')
snapshot("c1", [KT + "BookApi.kt", KT + "Model.kt", JV + "BookApi.java", JV + "Model.java"])

# ---------- c2: move storage behind BookRepository (with a leftover DEBUG line) ----------
tree[KT + "BookRepository.kt"] = '''package books

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/** Where books live. BookApi talks to this interface, never to a map (L38: depend on abstractions). */
interface BookRepository {
    fun add(title: String, year: Int): Book        // assigns the next id
    fun find(id: Long): Book?
    fun all(): List<Book>                          // sorted by id
    fun replace(book: Book): Boolean               // false if no book has that id
    fun delete(id: Long): Boolean                  // false if no book has that id
}

class InMemoryBookRepository : BookRepository {
    private val books = ConcurrentHashMap<Long, Book>()          // L27: safe for concurrent handlers
    private val nextId = AtomicLong(1)

    override fun add(title: String, year: Int): Book =
        Book(nextId.getAndIncrement(), title, year).also { books[it.id] = it }

    override fun find(id: Long): Book? = books[id]
    override fun all(): List<Book> = books.values.sortedBy { it.id }
    override fun replace(book: Book): Boolean = books.replace(book.id, book) != null
    override fun delete(id: Long): Boolean = books.remove(id) != null
}
'''
k = KT + "BookApi.kt"
edit(k, "import java.util.concurrent.ConcurrentHashMap\n", "")
edit(k, "import java.util.concurrent.atomic.AtomicLong\n", "")
edit(k, '''    private val books = ConcurrentHashMap<Long, Book>()          // L27: safe for concurrent handlers
    private val nextId = AtomicLong(1)
''', '''    private val repo: BookRepository = InMemoryBookRepository()
''')
edit(k, '"GET" -> books[id]?.let', '"GET" -> repo.find(id)?.let')
edit(k, '"DELETE" -> if (books.remove(id) != null) send', '"DELETE" -> if (repo.delete(id)) send')
edit(k, "val all = books.values.sortedBy { it.id }", "val all = repo.all()")
edit(k, '''        val book = Book(nextId.getAndIncrement(), input.title!!.trim(), input.year!!)
        books[book.id] = book
''', '''        val book = repo.add(input.title!!.trim(), input.year!!)
        println("DEBUG repo now has ${repo.all().size} books")
''')
edit(k, "        if (!books.containsKey(id)) return notFound(ex)   // not `id in books`: see L32\n",
     "        if (repo.find(id) == null) return notFound(ex)\n")
edit(k, '''        val book = Book(id, input.title!!.trim(), input.year!!)
        books[id] = book
''', '''        val book = Book(id, input.title!!.trim(), input.year!!)
        repo.replace(book)
''')
tree[JV + "BookRepository.java"] = '''package books;

import books.Model.Book;
import java.util.List;
import java.util.Optional;

/** Where books live. BookApi talks to this interface, never to a map (L38: depend on abstractions). */
public interface BookRepository {
    Book add(String title, int year);      // assigns the next id
    Optional<Book> find(long id);
    List<Book> all();                      // sorted by id
    boolean replace(Book book);            // false if no book has that id
    boolean delete(long id);               // false if no book has that id
}
'''
tree[JV + "InMemoryBookRepository.java"] = '''package books;

import books.Model.Book;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryBookRepository implements BookRepository {
    private final Map<Long, Book> books = new ConcurrentHashMap<>();   // L27: safe for concurrent handlers
    private final AtomicLong nextId = new AtomicLong(1);

    @Override public Book add(String title, int year) {
        Book book = new Book(nextId.getAndIncrement(), title, year);
        books.put(book.id(), book);
        return book;
    }

    @Override public Optional<Book> find(long id) { return Optional.ofNullable(books.get(id)); }

    @Override public List<Book> all() {
        return books.values().stream().sorted(Comparator.comparingLong(Book::id)).toList();
    }

    @Override public boolean replace(Book book) { return books.replace(book.id(), book) != null; }

    @Override public boolean delete(long id) { return books.remove(id) != null; }
}
'''
j = JV + "BookApi.java"
edit(j, "import java.util.Comparator;\n", "")
edit(j, "import java.util.concurrent.ConcurrentHashMap;\n", "")
edit(j, "import java.util.concurrent.atomic.AtomicLong;\n", "")
edit(j, "import java.util.Map;\n", "import java.util.Map;\nimport java.util.Optional;\n")
edit(j, '''    private final Map<Long, Book> books = new ConcurrentHashMap<>();             // L27: safe for concurrent handlers
    private final AtomicLong nextId = new AtomicLong(1);
''', '''    private final BookRepository repo = new InMemoryBookRepository();
''')
edit(j, '''                    Book b = books.get(id);
                    if (b != null) send(ex, 200, json.writeValueAsString(b), "application/json"); else notFound(ex);
''', '''                    Optional<Book> b = repo.find(id);
                    if (b.isPresent()) send(ex, 200, json.writeValueAsString(b.get()), "application/json"); else notFound(ex);
''')
edit(j, "if (books.remove(id) != null) send(ex, 204, null, null);", "if (repo.delete(id)) send(ex, 204, null, null);")
edit(j, "List<Book> all = books.values().stream().sorted(Comparator.comparingLong(Book::id)).toList();",
     "List<Book> all = repo.all();")
edit(j, '''        Book book = new Book(nextId.getAndIncrement(), input.title().trim(), input.year());
        books.put(book.id(), book);
''', '''        Book book = repo.add(input.title().trim(), input.year());
        System.out.println("DEBUG repo now has " + repo.all().size() + " books");
''')
edit(j, "        if (!books.containsKey(id)) { notFound(ex); return; }\n",
     "        if (repo.find(id).isEmpty()) { notFound(ex); return; }\n")
edit(j, '''        Book book = new Book(id, input.title().trim(), input.year());
        books.put(id, book);
''', '''        Book book = new Book(id, input.title().trim(), input.year());
        repo.replace(book);
''')
snapshot("c2", [KT + "BookApi.kt", KT + "BookRepository.kt", JV + "BookApi.java",
                JV + "BookRepository.java", JV + "InMemoryBookRepository.java"])

# ---------- c3: inject the repository through the constructor ----------
edit(k, "class BookApi(port: Int = 0) {\n",
     "class BookApi(port: Int = 0, private val repo: BookRepository = InMemoryBookRepository()) {\n")
edit(k, "    private val repo: BookRepository = InMemoryBookRepository()\n", "")
edit(KTT + "BookApiTest.kt", '''        assertEquals("GET, POST", res.headers().firstValue("Allow").get())
    }
}
''', '''        assertEquals("GET, POST", res.headers().firstValue("Allow").get())
    }

    @Test fun `GET reads from an injected repository`() {
        val repo = InMemoryBookRepository().apply { add("Kindred", 1979) }
        val other = BookApi(port = 0, repo = repo).apply { start() }
        try {
            val res = client.send(HttpRequest.newBuilder(URI("http://127.0.0.1:${other.port}/books/1")).build(),
                                  BodyHandlers.ofString())
            assertEquals(Book(1, "Kindred", 1979), json.decodeFromString<Book>(res.body()))
        } finally {
            other.stop()
        }
    }
}
''')
edit(j, "    private final BookRepository repo = new InMemoryBookRepository();\n",
     "    private final BookRepository repo;\n")
edit(j, '''    public BookApi(int port) throws IOException {
        server = HttpServer.create''', '''    public BookApi(int port) throws IOException {
        this(port, new InMemoryBookRepository());
    }

    public BookApi(int port, BookRepository repo) throws IOException {
        this.repo = repo;
        server = HttpServer.create''')
edit(JVT + "BookApiTest.java", '''        assertEquals("GET, POST", res.headers().firstValue("Allow").get());
    }
}
''', '''        assertEquals("GET, POST", res.headers().firstValue("Allow").get());
    }

    @Test @DisplayName("GET reads from an injected repository")
    void injected() throws Exception {
        var repo = new InMemoryBookRepository();
        repo.add("Kindred", 1979);
        var other = new BookApi(0, repo);
        other.start();
        try {
            var res = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + other.port() + "/books/1"))
                                      .build(), BodyHandlers.ofString());
            assertEquals(new Book(1, "Kindred", 1979), json.readValue(res.body(), Book.class));
        } finally {
            other.stop();
        }
    }
}
''')
snapshot("c3", [k, KTT + "BookApiTest.kt", j, JVT + "BookApiTest.java"])

# ---------- c4: unit-test validation without a server ----------
tree[KTT + "ValidateTest.kt"] = '''package books

import kotlin.test.Test
import kotlin.test.assertEquals

// Plain function, plain tests: no server, no HTTP, runs in milliseconds.
class ValidateTest {
    @Test fun `a valid book has no errors`() {
        assertEquals(emptyList(), validate(NewBook("Dune", 1965)))
    }

    @Test fun `missing fields are both reported`() {
        assertEquals(listOf("title is required", "year is required"), validate(NewBook()))
    }

    @Test fun `a blank title and a far-future year are both reported`() {
        assertEquals(2, validate(NewBook("  ", 3000)).size)
    }
}
'''
tree[JVT + "ValidateTest.java"] = '''package books;

import static org.junit.jupiter.api.Assertions.assertEquals;

import books.Model.NewBook;
import java.util.List;
import org.junit.jupiter.api.Test;

// Plain function, plain tests: no server, no HTTP, runs in milliseconds.
class ValidateTest {
    @Test void aValidBookHasNoErrors() {
        assertEquals(List.of(), Model.validate(new NewBook("Dune", 1965)));
    }

    @Test void missingFieldsAreBothReported() {
        assertEquals(List.of("title is required", "year is required"), Model.validate(new NewBook(null, null)));
    }

    @Test void aBlankTitleAndAFarFutureYearAreBothReported() {
        assertEquals(2, Model.validate(new NewBook("  ", 3000)).size());
    }
}
'''
snapshot("c4", [KTT + "ValidateTest.kt", JVT + "ValidateTest.java"])

# ---------- c5: CI for both projects ----------
tree[".github/workflows/ci.yml"] = '''name: CI

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        project: [books_kt, books_java]
    defaults:
      run:
        working-directory: ${{ matrix.project }}
    steps:
      - uses: actions/checkout@v7
      - uses: actions/setup-java@v6
        with:
          distribution: temurin
          java-version: '21'
      - uses: gradle/actions/setup-gradle@v6
      - run: ./gradlew build
'''
snapshot("c5", [".github/workflows/ci.yml"])

# ---------- fixup: remove the DEBUG lines that self-review found ----------
edit(k, '        println("DEBUG repo now has ${repo.all().size} books")\n', "")
edit(j, '        System.out.println("DEBUG repo now has " + repo.all().size() + " books");\n', "")
snapshot("fixup", [k, j])

# ---------- the conflict resolution's extra edit: carry 1440 into validate() ----------
edit(KT + "Model.kt", "b.year !in 1450..2100 -> add(\"year must be between 1450 and 2100\")",
     "b.year !in 1440..2100 -> add(\"year must be between 1440 and 2100\")")
edit(JV + "Model.java", "b.year() < 1450 || b.year() > 2100) errors.add(\"year must be between 1450 and 2100\")",
     "b.year() < 1440 || b.year() > 2100) errors.add(\"year must be between 1440 and 2100\")")
snapshot("resolve", [KT + "Model.kt", JV + "Model.java"])
print("steps written:", sorted(os.listdir(os.path.join(F, "steps"))))
