import org.junit.Assert.assertEquals
import org.junit.Test

class LwwTest {
    @Test fun `the newer timestamp wins in either order`() {
        val old = Versioned("[book]", 1, "A")
        val new = Versioned("[book,pen]", 2, "A")
        assertEquals(new, lww(old, new))
        assertEquals(new, lww(new, old))
    }

    @Test fun `a tie goes to the larger node name in either order`() {
        val a = Versioned("[book,lamp]", 1, "A")
        val b = Versioned("[book,pen]", 1, "B")
        assertEquals("lww(a, b)", b, lww(a, b))
        assertEquals("lww(b, a)", b, lww(b, a))
    }

    @Test fun `a first version is simply stored`() {
        val b = Replica("B")
        val v = Versioned("[book]", 1, "A")
        b.receive("cart", v)
        assertEquals(v, b.data["cart"])
    }

    @Test fun `two replicas agree after a partition heals`() {
        val a = Replica("A")
        val b = Replica("B")
        val fromA = a.write("cart", "[book,lamp]")   // both are the first write on their replica: ts = 1
        val fromB = b.write("cart", "[book,pen]")
        a.receive("cart", fromB)
        b.receive("cart", fromA)
        assertEquals("A and B must hold the same version", a.data["cart"], b.data["cart"])
    }

    @Test fun `a duplicate or a late old version changes nothing`() {
        val a = Replica("A")
        val b = Replica("B")
        val v1 = a.write("cart", "[book]")
        val v2 = a.write("cart", "[book,lamp]")
        b.receive("cart", v2)
        b.receive("cart", v2)
        b.receive("cart", v1)
        assertEquals(v2, b.data["cart"])
    }
}
