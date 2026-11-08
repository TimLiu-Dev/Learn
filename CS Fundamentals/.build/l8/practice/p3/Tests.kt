import org.junit.Assert.assertEquals
import org.junit.Test

class Duck : Animal() {
    override fun sound() = "Quack"
}

class DispatchTest {
    @Test fun `a dog knows its own sound`() {
        assertEquals("Woof", Dog().sound())
    }

    @Test fun `the cow is not forgotten`() {
        assertEquals("Moo", Cow().sound())
    }

    @Test fun `chorus uses each animal's sound`() {
        assertEquals("Woof Meow Moo", chorus(listOf(Dog(), Cat(), Cow())))
    }

    @Test fun `a new subclass works without editing chorus`() {
        assertEquals("Quack Woof", chorus(listOf(Duck(), Dog())))
    }
}
