package katas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class BracketsTest {
    @Test void balancedStrings() {
        for (String s : List.of("", "()", "{[()]}", "f(a[0], {b})")) assertTrue(Brackets.isBalanced(s), s);
    }

    @Test void unbalancedStrings() {
        for (String s : List.of("(", ")", "([)]", "(()", "())", "}{")) assertFalse(Brackets.isBalanced(s), s);
    }
}
