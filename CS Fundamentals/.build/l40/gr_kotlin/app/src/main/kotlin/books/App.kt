package books

import java.sql.DriverManager

fun main() {
    DriverManager.getConnection("jdbc:sqlite::memory:").use { db ->
        seed(db)
        val catalog = BookCatalog(db)
        println("page 1: " + catalog.page(1, 2).items.map { it.title })
        println("page 2: " + catalog.page(2, 2).items.map { it.title })
    }
}

fun seed(db: java.sql.Connection) {
    db.createStatement().use { st ->
        st.execute("CREATE TABLE books(id INTEGER PRIMARY KEY, title TEXT NOT NULL, year INTEGER NOT NULL)")
        st.execute("""INSERT INTO books(title, year) VALUES ('Dune', 1965), ('Neuromancer', 1984),
                      ('Hyperion', 1989), ('Foundation', 1951), ('Kindred', 1979)""")
    }
}
