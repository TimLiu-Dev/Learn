package shapes

open class Rectangle(width: Int, height: Int) {
    open var width = width
    open var height = height
    val area: Int get() = width * height
}

// "A square IS-A rectangle" (true in geometry) ...
class Square(side: Int) : Rectangle(side, side) {
    override var width: Int
        get() = super.width
        set(value) { super.width = value; super.height = value }   // ... so keep the sides equal
    override var height: Int
        get() = super.height
        set(value) { super.width = value; super.height = value }
}
