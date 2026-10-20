    @Test
    void zeroIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Roman.toRoman(0));
    }
