package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WordCountTest {
    @Test void matchesTheL3Report() {
        String text = "The cat sat on the mat. The mat was flat,\nand the cat was fat! A cat, a mat, a hat.";
        String expected = """
                total words: 21
                unique words: 11
                most frequent: the (4)
                repeated: a=3 cat=3 mat=3 the=4 was=2""";
        assertEquals(expected, WordCount.report(text));
    }
}
