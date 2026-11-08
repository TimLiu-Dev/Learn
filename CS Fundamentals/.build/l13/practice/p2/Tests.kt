import org.junit.Assert.assertEquals
import org.junit.Test

class ShelterTest {
    @Test fun `a cat kennel can be used as a kennel of animals`() {
        assertEquals("Mia", kennelDemo())
    }

    @Test fun `a vet for any animal can treat a cat`() {
        assertEquals("treated Kit", vetDemo())
    }

    @Test fun `an array of cats can be read as animals`() {
        assertEquals("Mia Tom", rollCallDemo())
    }

    @Test fun `a kennel releases cats in arrival order`() {
        val k = CatKennel(Cat("Ada"), Cat("Bo"))
        assertEquals("Ada", k.release().name)
        assertEquals("Bo", k.release().name)
        assertEquals(0, k.size())
    }

    @Test fun `rollCall still takes an array of animals`() {
        assertEquals("Rex Mia", rollCall(arrayOf(Animal("Rex"), Cat("Mia"))))
    }
}
