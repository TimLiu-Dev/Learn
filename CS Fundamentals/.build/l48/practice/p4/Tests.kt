import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TinyBloomTest {
    private val words = listOf("kotlin", "java", "gradle", "android", "spring", "junit", "heap", "stack",
        "lambda", "sequence", "generic", "record", "sealed", "enum", "thread", "socket")

    @Test fun `an empty filter contains nothing`() {
        val f = TinyBloom()
        assertTrue(words.none { f.mightContain(it) })
    }

    @Test fun `every added item is found`() {
        val f = TinyBloom()
        words.forEach { f.add(it) }
        assertTrue("a Bloom filter never says no to an item it holds", words.all { f.mightContain(it) })
    }

    @Test fun `add sets exactly the item's bits`() {
        for (w in listOf("kotlin", "android", "sealed", "sequence")) {
            val f = TinyBloom()
            f.add(w)
            val expected = f.positions(w).fold(0L) { acc, i -> acc or (1L shl i) }
            assertEquals("bits for \"$w\", positions ${f.positions(w)}",
                java.lang.Long.toBinaryString(expected), java.lang.Long.toBinaryString(f.bits))
        }
    }

    @Test fun `a fuller filter gives false positives`() {
        val f = TinyBloom()
        words.forEach { f.add(it) }
        val falseYes = (0 until 1000).map { "probe-$it" }.count { f.mightContain(it) }
        assertTrue("16 items set about half of the 64 bits, so some absent probes must say maybe", falseYes > 0)
    }
}
