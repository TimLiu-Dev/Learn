package shelf.domain

import java.time.LocalDate

// The domain: plain data, no JSON or SQL annotations. The other layers translate to and from it.
data class Book(val id: Long, val title: String, val author: String, val year: Int?)

data class Member(val id: Long, val name: String, val email: String)

data class Loan(val id: Long, val bookId: Long, val memberId: Long, val loaned: LocalDate, val returned: LocalDate?) {
    val isOpen: Boolean get() = returned == null
}
