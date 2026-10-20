package katas

import java.util.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class SortingTest {
    private val sorts = listOf<Pair<String, (MutableList<Int>) -> Unit>>(
        "insertion" to { insertionSort(it) },
        "merge" to { mergeSort(it) },
        "quick" to { quickSort(it) })

    // Run every sort on a copy of input and compare with the JDK's sorted().
    private fun checkAll(input: List<Int>) {
        for ((name, sort) in sorts) {
            val xs = input.toMutableList()
            sort(xs)
            assertEquals(input.sorted(), xs, "$name on $input")
        }
    }

    @Test fun `empty list`() = checkAll(emptyList())
    @Test fun `one element`() = checkAll(listOf(42))
    @Test fun `two elements out of order`() = checkAll(listOf(2, 1))
    @Test fun `duplicates`() = checkAll(listOf(3, 1, 3, 2, 1, 3, 3))
    @Test fun `already sorted`() = checkAll((1..100).toList())
    @Test fun `reversed`() = checkAll((100 downTo 1).toList())
    @Test fun `negative numbers`() = checkAll(listOf(0, -5, 7, -5, Int.MIN_VALUE, Int.MAX_VALUE))

    @Test
    fun `random lists with a fixed seed`() {
        val rnd = Random(42)
        repeat(100) { checkAll(List(rnd.nextInt(300)) { rnd.nextInt(50) - 25 }) }
    }

    @Test
    fun `strings sort too`() {
        val xs = mutableListOf("pear", "fig", "kiwi", "apple")
        mergeSort(xs)
        assertEquals(listOf("apple", "fig", "kiwi", "pear"), xs)
    }

    // Equal by compareTo, but distinguishable by suit.
    data class Card(val rank: Int, val suit: Char) : Comparable<Card> {
        override fun compareTo(other: Card) = rank.compareTo(other.rank)
    }

    @Test
    fun `insertion and merge are stable`() {
        val cards = listOf(Card(5, 'h'), Card(2, 's'), Card(5, 'c'), Card(2, 'd'), Card(5, 's'))
        val expected = listOf(Card(2, 's'), Card(2, 'd'), Card(5, 'h'), Card(5, 'c'), Card(5, 's'))
        assertEquals(expected, cards.toMutableList().also { insertionSort(it) })
        assertEquals(expected, cards.toMutableList().also { mergeSort(it) })
    }

    @Test
    fun `quick survives big sorted and all-equal input`() {
        val sorted = (1..100_000).toMutableList()
        quickSort(sorted)
        assertEquals((1..100_000).toList(), sorted)
        val same = MutableList(100_000) { 7 }
        quickSort(same)
        assertEquals(List(100_000) { 7 }, same)
    }
}
