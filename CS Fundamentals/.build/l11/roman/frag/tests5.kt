    @Test
    fun `0 is rejected`() {
        assertFailsWith<IllegalArgumentException> { toRoman(0) }
    }
