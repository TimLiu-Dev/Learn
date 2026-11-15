package shelf.service

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.fail
import shelf.store.MemoryBookStore
import shelf.store.MemoryLoanStore
import shelf.store.MemoryMemberStore

// A property-based test without a framework: random operations, and invariants checked after every one.
class LendingPropertyTest {
    private val clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC)

    @Test
    fun `no sequence of lends and returns breaks the lending rules`() {
        repeat(1_000) { seed -> run(seed.toLong()) }
    }

    private fun run(seed: Long) {
        val rnd = Random(seed)                              // same seed, same run: every failure can be replayed
        val books = MemoryBookStore()
        val members = MemoryMemberStore()
        val loans = MemoryLoanStore()
        val service = LendingService(books, members, loans, clock)
        repeat(6) { books.add("Book $it", "Author", null) }                // books 1..6
        repeat(2) { members.add("Member $it", "m$it@example.com") }         // members 1..2
        val steps = mutableListOf<String>()

        repeat(30) {
            steps += if (rnd.nextInt(3) < 2) {                              // lend twice as often as return
                val book = rnd.nextLong(1, 8)                               // 7 is an unknown book
                val member = rnd.nextLong(1, 4)                             // 3 is an unknown member
                "lend($book, $member) -> ${service.lend(book, member)::class.simpleName}"
            } else {
                val loan = rnd.nextLong(1, 12)
                "return($loan) -> ${service.returnLoan(loan)::class.simpleName}"
            }
            // The properties: whatever just happened, these must still be true.
            val all = (1L..2L).flatMap { loans.forMember(it) }
            val open = all.filter { it.isOpen }
            val problem = when {
                open.groupBy { it.memberId }.any { it.value.size > LendingService.MAX_OPEN_LOANS } ->
                    "a member holds more than ${LendingService.MAX_OPEN_LOANS} open loans"
                open.groupBy { it.bookId }.any { it.value.size > 1 } -> "a book is on two open loans"
                else -> null
            }
            if (problem != null) fail("seed $seed: $problem after ${steps.size} steps:\n  " + steps.joinToString("\n  "))
        }
    }
}
