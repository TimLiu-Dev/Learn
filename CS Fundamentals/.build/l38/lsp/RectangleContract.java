package shapes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import shapes.Shapes.Rectangle;

// Tests written once, against the base type. Every subtype must pass them (LSP).
abstract class RectangleContract {
    abstract Rectangle make(int width, int height);

    @Test
    void settingTheWidthLeavesTheHeightAlone() {
        Rectangle r = make(3, 3);
        r.setWidth(5);
        assertEquals(3, r.getHeight());
        assertEquals(15, r.area());
    }
}
