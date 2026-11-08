import org.junit.Assert.assertEquals
import org.junit.Test

class ReadAgeTest {
    @Test fun `a good row gives the age`() {
        assertEquals(AgeResult.Ok(36), readAge("Ada,36"))
    }

    @Test fun `spaces around the fields are ignored`() {
        assertEquals(AgeResult.Ok(41), readAge(" Grace , 41 "))
    }

    @Test fun `a non-number age keeps the text`() {
        assertEquals(AgeResult.NotANumber("abc"), readAge("Ada,abc"))
    }

    @Test fun `a row with no age field is Missing, not NotANumber`() {
        assertEquals(AgeResult.Missing, readAge("Ada"))
    }

    @Test fun `a blank age is Missing`() {
        assertEquals(AgeResult.Missing, readAge("Ada, "))
    }
}
