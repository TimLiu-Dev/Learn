package books;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookCatalogTest {
    private Connection db;
    private BookCatalog catalog;

    @BeforeEach void open() throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite::memory:");
        App.seed(db);
        catalog = new BookCatalog(db);
    }

    @AfterEach void close() throws SQLException { db.close(); }

    @Test void allReturnsEveryBook() throws SQLException {
        assertEquals(5, catalog.all().size());
    }
}
