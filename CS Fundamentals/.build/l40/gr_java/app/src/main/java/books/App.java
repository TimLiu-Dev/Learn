package books;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) throws SQLException {
        try (Connection db = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            seed(db);
            var catalog = new BookCatalog(db);
            System.out.println("page 1: " + titles(catalog.page(1, 2)));
            System.out.println("page 2: " + titles(catalog.page(2, 2)));
        }
    }

    static Object titles(BookCatalog.BookPage page) {
        return page.items().stream().map(BookCatalog.Book::title).toList();
    }

    static void seed(Connection db) throws SQLException {
        try (Statement st = db.createStatement()) {
            st.execute("CREATE TABLE books(id INTEGER PRIMARY KEY, title TEXT NOT NULL, year INTEGER NOT NULL)");
            st.execute("""
                INSERT INTO books(title, year) VALUES ('Dune', 1965), ('Neuromancer', 1984),
                ('Hyperion', 1989), ('Foundation', 1951), ('Kindred', 1979)""");
        }
    }
}
