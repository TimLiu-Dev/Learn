package org.example

import kotlin.test.Test
import kotlin.test.assertEquals

class WordCountTest {
    @Test fun `matches the L3 report`() {
        val text = "The cat sat on the mat. The mat was flat,\nand the cat was fat! A cat, a mat, a hat."
        val expected = """
            |total words: 21
            |unique words: 11
            |most frequent: the (4)
            |repeated: a=3 cat=3 mat=3 the=4 was=2
        """.trimMargin()
        assertEquals(expected, wordReport(text))
    }
}
