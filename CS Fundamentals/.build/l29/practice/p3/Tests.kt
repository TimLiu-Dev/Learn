import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

// Three items per page, then empty pages. It records every page number it was asked for.
class FakeSearch(private val items: List<String>) : SearchApi {
    val fetched = mutableListOf<Int>()

    override suspend fun page(number: Int): List<String> {
        fetched += number
        delay(10)
        return items.drop((number - 1) * 3).take(3)
    }
}

val fruit = listOf("apple", "Grape", "kiwi", "pear", "grapefruit", "plum", "fig", "grape juice", "lime", "lemon")

class PagesTest {
    @Test fun `all items in order, then stop at the empty page`() = runBlocking {
        val api = FakeSearch(fruit)
        assertEquals(fruit, allItems(api).toList())
        assertEquals("pages fetched", listOf(1, 2, 3, 4, 5), api.fetched)
    }

    @Test fun `nothing is fetched until someone collects`() = runBlocking {
        val api = FakeSearch(fruit)
        val flow = allItems(api)
        assertEquals("pages fetched before collect", emptyList<Int>(), api.fetched)
        assertEquals("apple", flow.first())
        assertEquals("pages fetched for the first item", listOf(1), api.fetched)
    }

    @Test fun `first matches, uppercased`() = runBlocking {
        assertEquals(listOf("GRAPE", "GRAPEFRUIT"), firstMatches(FakeSearch(fruit), "grape", 2))
    }

    @Test fun `only the pages needed are fetched`() = runBlocking {
        val api = FakeSearch(fruit)
        firstMatches(api, "grape", 2)
        assertEquals("pages fetched", listOf(1, 2), api.fetched)
    }

    @Test fun `fewer matches than asked for reads to the end`() = runBlocking {
        val api = FakeSearch(fruit)
        assertEquals(listOf("LEMON"), firstMatches(api, "lem", 5))
        assertEquals("pages fetched", listOf(1, 2, 3, 4, 5), api.fetched)
    }

    @Test fun `no results at all`() = runBlocking {
        val api = FakeSearch(emptyList())
        assertEquals(emptyList<String>(), firstMatches(api, "x", 3))
        assertEquals("pages fetched", listOf(1), api.fetched)
    }
}
