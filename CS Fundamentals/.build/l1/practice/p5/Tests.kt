import org.junit.Assert.assertEquals
import org.junit.Test

class GreetingTest {
    @Test fun `greets the katas folder`() {
        assertEquals("Hello, katas!", greeting("katas"))
    }

    @Test fun `uses the name it is given`() {
        assertEquals("Hello, Ada!", greeting("Ada"))
    }

    @Test fun `keeps a name with a space in one piece`() {
        assertEquals("Hello, Grace Hopper!", greeting("Grace Hopper"))
    }
}
