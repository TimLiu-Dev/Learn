import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoanLimitTest {
    @Test fun `a new member may borrow`() = assertTrue(mayBorrow(0))
    @Test fun `a member with five open loans may not`() = assertFalse(mayBorrow(5))
}
