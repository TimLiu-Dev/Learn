import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.xml.sax.InputSource

data class Book(val isbn: String, val title: String)

interface BookCatalog {                      // our interface (L38)
    fun find(isbn: String): Book?
    fun all(): List<Book>
}

class XmlBookFeed {                          // legacy, can't change
    fun fetchXml() =
        """<books><book isbn="9780441172719" title="Dune"/><book isbn="9780141439587" title="Emma"/></books>"""
}

class XmlFeedCatalog(private val feed: XmlBookFeed) : BookCatalog {
    override fun all(): List<Book> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(InputSource(StringReader(feed.fetchXml())))
        val nodes = doc.getElementsByTagName("book")
        return (0 until nodes.length).map { i ->
            val e = nodes.item(i) as Element
            Book(e.getAttribute("isbn"), e.getAttribute("title"))
        }
    }
    override fun find(isbn: String) = all().find { it.isbn == isbn }
}

// Kotlin extra: an extension function makes the adaptation read naturally
fun XmlBookFeed.asCatalog(): BookCatalog = XmlFeedCatalog(this)

fun main() {
    val catalog = XmlBookFeed().asCatalog()
    println(catalog.all())
    println(catalog.find("9780141439587")?.title ?: "not found")

    val shelf = arrayOf("Dune", "Emma")
    val view = shelf.asList()                // a read-only List view backed by the array
    shelf[0] = "Ulysses"
    println("view after shelf[0] = ...: $view")
}
