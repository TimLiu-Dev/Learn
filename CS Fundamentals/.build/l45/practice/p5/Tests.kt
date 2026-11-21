import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PagingPropertyTest {
    private val skipsOne: Pager = { items, limit, offset -> items.drop(offset + 1).take(limit) }
    private val losesLastPage: Pager = { items, limit, offset ->
        if (offset > 0 && offset + limit > items.size) emptyList() else items.drop(offset).take(limit)
    }
    private val crashesPastTheEnd: Pager = { items, limit, offset -> items.subList(offset, minOf(offset + limit, items.size)) }
    private val oneTooMany: Pager = { items, limit, offset -> items.drop(offset).take(limit + 1) }
    private val wrongWhenExactlyFull: Pager = { items, limit, offset ->
        if (items.size == limit) items.take(limit - 1) else items.drop(offset).take(limit)
    }

    @Test fun `the correct pager passes`() {
        assertNull(checkPaging(::page, seed = 1))
        assertNull(checkPaging(::page, seed = 2))
    }

    @Test fun `a pager that skips an item is rejected`() {
        assertNotNull("skipsOne should be rejected", checkPaging(skipsOne, seed = 3))
    }

    @Test fun `a pager that loses the last, partial page is rejected`() {
        assertNotNull("losesLastPage should be rejected", checkPaging(losesLastPage, seed = 4))
    }

    @Test fun `a pager that throws is rejected, not crashed into`() {
        assertNotNull("crashesPastTheEnd should be rejected", checkPaging(crashesPastTheEnd, seed = 5))
    }

    @Test fun `pages longer than the limit are rejected, and so is a rare bug`() {
        assertNotNull("oneTooMany should be rejected", checkPaging(oneTooMany, seed = 6))
        assertNotNull("wrongWhenExactlyFull (only when the list has exactly limit items) should be rejected",
            checkPaging(wrongWhenExactlyFull, seed = 7))
    }

    @Test fun `the same seed gives the same verdict`() {
        assertEquals(checkPaging(losesLastPage, seed = 8), checkPaging(losesLastPage, seed = 8))
    }
}
