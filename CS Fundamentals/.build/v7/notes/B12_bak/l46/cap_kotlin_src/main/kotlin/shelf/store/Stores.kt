package shelf.store

import java.time.LocalDate
import shelf.domain.Book
import shelf.domain.Loan
import shelf.domain.Member

// What the services need from storage (L38: the policy owns the interfaces).
interface BookStore {
    fun add(title: String, author: String, year: Int?): Book
    fun find(id: Long): Book?
    fun list(limit: Int, offset: Int, author: String?): List<Book>   // ordered by id
    fun count(author: String?): Int
    fun update(id: Long, title: String, author: String, year: Int?): Book?   // null: no such book
    fun delete(id: Long): Boolean
}

interface MemberStore {
    fun add(name: String, email: String): Member
    fun find(id: Long): Member?
    fun findByEmail(email: String): Member?
    /** Insert unless the email is taken, as ONE atomic step; null if it was taken. */
    fun tryAdd(name: String, email: String): Member?
}

interface LoanStore {
    fun add(bookId: Long, memberId: Long, loaned: LocalDate): Loan
    fun find(id: Long): Loan?
    fun openForBook(bookId: Long): Loan?
    fun forMember(memberId: Long): List<Loan>                       // oldest first
    fun anyForBook(bookId: Long): Boolean
    fun close(id: Long, returned: LocalDate): Boolean               // false: missing or already closed
    /** Check both lending rules and insert the loan as ONE atomic step. */
    fun tryLend(bookId: Long, memberId: Long, loaned: LocalDate, maxOpen: Int): TryLend
}

sealed interface TryLend {
    data class Lent(val loan: Loan) : TryLend
    data object BookTaken : TryLend
    data object AtLimit : TryLend
}

