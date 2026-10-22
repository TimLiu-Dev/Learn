import java.lang.reflect.Modifier

// A SHALLOW module: everything is public, and callers must know the right order of calls.
class ShallowLoanDesk {
    val books = mutableMapOf<Long, String>()      // id -> title
    val members = mutableMapOf<Long, String>()    // id -> name
    val openLoans = mutableMapOf<Long, Long>()    // bookId -> memberId
    var nextId = 1L

    fun newId(): Long = nextId++
    fun isKnownBook(id: Long) = id in books
    fun isKnownMember(id: Long) = id in members
    fun isOnLoan(bookId: Long) = bookId in openLoans
    fun recordLoan(bookId: Long, memberId: Long) { openLoans[bookId] = memberId }
    fun clearLoan(bookId: Long) { openLoans.remove(bookId) ?: error("book $bookId is not on loan") }
}

// Caller 1 knows the ritual: check book, check member, check loan, then record.
fun lendCarefully(desk: ShallowLoanDesk, bookId: Long, memberId: Long): String = when {
    !desk.isKnownBook(bookId) -> "no such book"
    !desk.isKnownMember(memberId) -> "no such member"
    desk.isOnLoan(bookId) -> "already on loan"
    else -> { desk.recordLoan(bookId, memberId); "lent" }
}

// Caller 2, written months later by someone else, skips a step.
fun lendInAHurry(desk: ShallowLoanDesk, bookId: Long, memberId: Long): String {
    if (!desk.isKnownBook(bookId)) return "no such book"
    desk.recordLoan(bookId, memberId)
    return "lent"
}

fun main() {
    val desk = ShallowLoanDesk()
    val dune = desk.newId().also { desk.books[it] = "Dune" }
    val ada = desk.newId().also { desk.members[it] = "Ada" }
    val linus = desk.newId().also { desk.members[it] = "Linus" }

    println("Ada:   " + lendCarefully(desk, dune, ada))
    println("Linus: " + lendInAHurry(desk, dune, linus))
    println("Dune is now with: " + desk.members[desk.openLoans[dune]])
    println(runCatching { desk.clearLoan(99) }.exceptionOrNull())

    val surface = ShallowLoanDesk::class.java.declaredMethods.count { Modifier.isPublic(it.modifiers) }
    println("public methods callers can touch: $surface")
}
