package shelf.store

import java.time.LocalDate
import java.util.TreeMap
import shelf.domain.Book
import shelf.domain.Loan
import shelf.domain.Member

// In-memory stores: fast fakes for unit tests (L38). @Synchronized makes them safe for concurrent requests too.
class MemoryBookStore : BookStore {
    private val books = TreeMap<Long, Book>()          // sorted by id, like ORDER BY id
    private var nextId = 1L

    private fun matching(author: String?) = books.values.filter { author == null || it.author == author }

    @Synchronized override fun add(title: String, author: String, year: Int?) =
        Book(nextId++, title, author, year).also { books[it.id] = it }

    @Synchronized override fun find(id: Long) = books[id]
    @Synchronized override fun list(limit: Int, offset: Int, author: String?) = matching(author).drop(offset).take(limit)
    @Synchronized override fun count(author: String?) = matching(author).size

    @Synchronized override fun update(id: Long, title: String, author: String, year: Int?): Book? =
        if (books.containsKey(id)) Book(id, title, author, year).also { books[id] = it } else null

    @Synchronized override fun delete(id: Long) = books.remove(id) != null
}

class MemoryMemberStore : MemberStore {
    private val members = TreeMap<Long, Member>()
    private var nextId = 1L

    @Synchronized override fun add(name: String, email: String) =
        Member(nextId++, name, email).also { members[it.id] = it }

    @Synchronized override fun find(id: Long) = members[id]
    @Synchronized override fun findByEmail(email: String) = members.values.firstOrNull { it.email == email }
}

class MemoryLoanStore : LoanStore {
    private val loans = TreeMap<Long, Loan>()
    private var nextId = 1L

    @Synchronized override fun add(bookId: Long, memberId: Long, loaned: LocalDate) =
        Loan(nextId++, bookId, memberId, loaned, null).also { loans[it.id] = it }

    @Synchronized override fun find(id: Long) = loans[id]
    @Synchronized override fun openForBook(bookId: Long) = loans.values.firstOrNull { it.bookId == bookId && it.isOpen }
    @Synchronized override fun forMember(memberId: Long) = loans.values.filter { it.memberId == memberId }
    @Synchronized override fun anyForBook(bookId: Long) = loans.values.any { it.bookId == bookId }

    @Synchronized override fun close(id: Long, returned: LocalDate): Boolean {
        val loan = loans[id]?.takeIf { it.isOpen } ?: return false
        loans[id] = loan.copy(returned = returned)
        return true
    }
}
