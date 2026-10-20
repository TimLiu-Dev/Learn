    @Test
    void nineIsIX() {
        assertEquals("IX", Roman.toRoman(9));
    }

    @Test
    void year1994IsMCMXCIV() {
        assertEquals("MCMXCIV", Roman.toRoman(1994));
    }
