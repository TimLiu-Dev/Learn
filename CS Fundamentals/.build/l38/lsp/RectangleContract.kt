package shapes

import kotlin.test.Test
import kotlin.test.assertEquals

// Tests written once, against the base type. Every subtype must pass them (LSP).
abstract class RectangleContract {
    abstract fun make(width: Int, height: Int): Rectangle

    @Test
    fun `setting the width leaves the height alone`() {
        val r = make(3, 3)
        r.width = 5
        assertEquals(3, r.height)
        assertEquals(15, r.area)
    }
}

class RectangleTest : RectangleContract() {
    override fun make(width: Int, height: Int) = Rectangle(width, height)
}

class SquareTest : RectangleContract() {
    override fun make(width: Int, height: Int) = Square(width)
}
