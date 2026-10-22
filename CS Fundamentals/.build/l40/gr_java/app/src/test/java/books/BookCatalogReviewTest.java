package books;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Written by the reviewer: one test per suspected bug.
class BookCatalogReviewTest {
    private Connection db;
    private BookCatalog catalog;

    @BeforeEach void open() throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite::memory:");
        App.seed(db);
        catalog = new BookCatalog(db);
    }

    @AfterEach void close() throws SQLException { db.close(); }

    private void dropBooks() throws SQLException {
        try (Statement st = db.createStatement()) { st.execute("DROP TABLE books"); }
    }

    @Test void bug1_page1StartsAtTheFirstBook() {
        assertEquals(List.of("Dune", "Neuromancer"), App.titles(catalog.page(1, 2)));
    }

    @Test void bug2_page2IsNotPage1() {
        assertNotEquals(catalog.page(1, 2).items(), catalog.page(2, 2).items());
    }

    @Test void bug3_databaseErrorsAreNotHidden() throws SQLException {
        dropBooks();
        assertThrows(RuntimeException.class, () -> catalog.page(1, 2));
    }

    @Test void bug4_pageLeavesNoStatementOpen() throws SQLException {
        catalog.page(1, 2);
        dropBooks();                                   // SQLite refuses while a statement is open
    }
}
