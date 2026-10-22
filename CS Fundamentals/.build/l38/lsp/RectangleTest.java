package shapes;

class RectangleTest extends RectangleContract {
    @Override Shapes.Rectangle make(int width, int height) { return new Shapes.Rectangle(width, height); }
}
