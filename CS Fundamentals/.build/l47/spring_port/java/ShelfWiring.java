package dev.shelf;

import java.nio.file.Path;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shelf.service.BookService;
import shelf.service.LendingService;
import shelf.store.BookStore;
import shelf.store.Db;
import shelf.store.LoanStore;
import shelf.store.MemberStore;
import shelf.store.SqliteBookStore;
import shelf.store.SqliteLoanStore;
import shelf.store.SqliteMemberStore;

// NOT RUN HERE. Replaces Shelf's composition root: Spring calls these @Bean methods and
// passes each one the beans it asks for (constructor injection, resolved by type).
@Configuration(proxyBeanMethods = false)
public class ShelfWiring {
    @Bean Db db(@Value("${shelf.db}") String file) {
        Db db = new Db(Path.of(file));
        db.createSchema();
        return db;
    }
    @Bean BookStore bookStore(Db db) { return new SqliteBookStore(db); }
    @Bean MemberStore memberStore(Db db) { return new SqliteMemberStore(db); }
    @Bean LoanStore loanStore(Db db) { return new SqliteLoanStore(db); }
    @Bean Clock clock() { return Clock.systemUTC(); }
    @Bean BookService bookService(BookStore books, LoanStore loans) { return new BookService(books, loans); }
    @Bean LendingService lendingService(BookStore books, MemberStore members, LoanStore loans, Clock clock) {
        return new LendingService(books, members, loans, clock);
    }
}
