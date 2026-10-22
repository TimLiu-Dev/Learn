package shapes;

class SquareTest extends RectangleContract {
    @Override Shapes.Rectangle make(int width, int height) { return new Shapes.Square(width); }
}
