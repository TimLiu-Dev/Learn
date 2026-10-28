package shelf.http

import kotlinx.serialization.Serializable
import shelf.domain.Book
import shelf.domain.Loan
import shelf.domain.Member
import shelf.service.NewBook

// DTOs: the JSON shapes of the API. Input fields are nullable so that a missing field becomes
// a clear validation message instead of a parser error. Validation happens here, at the boundary.

@Serializable
data class BookIn(val title: String? = null, val author: String? = null, val year: Int? = null) {
    fun validate(): NewBook {
        val errors = buildList {
            if (title.isNullOrBlank()) add("title is required")
            else if (title.length > 200) add("title must be at most 200 characters")
            if (author.isNullOrBlank()) add("author is required")
            if (year != null && year !in 1400..2100) add("year must be between 1400 and 2100")
        }
        if (errors.isNotEmpty()) throw ApiError.invalid(errors)
        return NewBook(title!!.trim(), author!!.trim(), year)
    }
}

@Serializable
data class BookOut(val id: Long, val title: String, val author: String, val year: Int?) {
    companion object {
        fun from(b: Book) = BookOut(b.id, b.title, b.author, b.year)
    }
}

@Serializable
data class BookPage(val items: List<BookOut>, val limit: Int, val offset: Int, val total: Int, val next: String?)

@Serializable
data class MemberIn(val name: String? = null, val email: String? = null) {
    fun validate(): Pair<String, String> {
        val errors = buildList {
            if (name.isNullOrBlank()) add("name is required")
            if (email == null || !EMAIL.matches(email)) add("email must look like name@example.com")
        }
        if (errors.isNotEmpty()) throw ApiError.invalid(errors)
        return name!!.trim() to email!!.lowercase()
    }

    private companion object {
        val EMAIL = Regex("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")
    }
}

@Serializable
data class MemberOut(val id: Long, val name: String, val email: String) {
    companion object {
        fun from(m: Member) = MemberOut(m.id, m.name, m.email)
    }
}

@Serializable
data class LoanIn(val bookId: Long? = null, val memberId: Long? = null) {
    fun validate(): Pair<Long, Long> {
        val errors = buildList {
            if (bookId == null) add("bookId is required")
            if (memberId == null) add("memberId is required")
        }
        if (errors.isNotEmpty()) throw ApiError.invalid(errors)
        return bookId!! to memberId!!
    }
}

// Dates leave the API as ISO-8601 strings (L32).
@Serializable
data class LoanOut(val id: Long, val bookId: Long, val memberId: Long, val loaned: String, val returned: String?) {
    companion object {
        fun from(l: Loan) = LoanOut(l.id, l.bookId, l.memberId, l.loaned.toString(), l.returned?.toString())
    }
}

@Serializable
data class Health(val status: String)
