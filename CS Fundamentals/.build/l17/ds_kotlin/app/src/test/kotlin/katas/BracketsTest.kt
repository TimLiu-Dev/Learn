package katas

import kotlin.test.*

class BracketsTest {
    @Test fun `balanced strings`() {
        for (s in listOf("", "()", "{[()]}", "f(a[0], {b})")) assertTrue(isBalanced(s), s)
    }

    @Test fun `unbalanced strings`() {
        for (s in listOf("(", ")", "([)]", "(()", "())", "}{")) assertFalse(isBalanced(s), s)
    }
}
