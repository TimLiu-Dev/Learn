package dev.shelf

import java.nio.file.Path
import java.time.Clock
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shelf.service.BookService
import shelf.service.LendingService
import shelf.store.BookStore
import shelf.store.Db
import shelf.store.LoanStore
import shelf.store.MemberStore
import shelf.store.SqliteBookStore
import shelf.store.SqliteLoanStore
import shelf.store.SqliteMemberStore

// NOT RUN HERE. Replaces Shelf's composition root: Spring calls these @Bean methods and
// passes each one the beans it asks for (constructor injection, resolved by type).
@Configuration(proxyBeanMethods = false)
class ShelfWiring {
    @Bean fun db(@Value("\${shelf.db}") file: String): Db = Db(Path.of(file)).also { it.createSchema() }
    @Bean fun bookStore(db: Db): BookStore = SqliteBookStore(db)
    @Bean fun memberStore(db: Db): MemberStore = SqliteMemberStore(db)
    @Bean fun loanStore(db: Db): LoanStore = SqliteLoanStore(db)
    @Bean fun clock(): Clock = Clock.systemUTC()
    @Bean fun bookService(books: BookStore, loans: LoanStore) = BookService(books, loans)
    @Bean fun lendingService(books: BookStore, members: MemberStore, loans: LoanStore, clock: Clock) =
        LendingService(books, members, loans, clock)
}
