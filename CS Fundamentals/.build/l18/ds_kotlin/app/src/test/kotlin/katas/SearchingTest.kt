package katas

import java.util.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchingTest {
    @Test
    fun `empty list gives 0`() {
        assertEquals(0, lowerBound(emptyList(), 5))
    }

    @Test
    fun `finds the FIRST of several duplicates`() {
        assertEquals(1, lowerBound(listOf(1, 2, 2, 2, 3), 2))
    }

    @Test
    fun `missing value gives its insertion point`() {
        assertEquals(3, lowerBound(listOf(10, 20, 30, 40), 35))
    }

    @Test
    fun `smaller than everything gives 0, bigger gives size`() {
        val xs = listOf(10, 20, 30)
        assertEquals(0, lowerBound(xs, 1))
        assertEquals(3, lowerBound(xs, 99))
    }

    @Test
    fun `works for any comparable type`() {
        assertEquals(2, lowerBound(listOf("apple", "fig", "kiwi"), "grape"))
    }

    @Test
    fun `agrees with a linear scan on random data`() {
        val rnd = Random(7)
        repeat(200) {
            val xs = List(rnd.nextInt(50)) { rnd.nextInt(20) }.sorted()
            val target = rnd.nextInt(22) - 1
            val expected = xs.indexOfFirst { it >= target }.let { if (it == -1) xs.size else it }
            assertEquals(expected, lowerBound(xs, target), "xs=$xs target=$target")
        }
    }
}
