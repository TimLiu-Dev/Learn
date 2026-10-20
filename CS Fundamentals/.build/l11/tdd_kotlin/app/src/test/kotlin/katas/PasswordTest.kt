package katas

import katas.PasswordResult.Ok
import katas.PasswordResult.Rejected
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordTest {
    @Test
    fun `7 characters is too short`() {
        assertEquals(Rejected(listOf(Rule.TOO_SHORT)), checkPassword("Abcdef1"))
    }

    @Test
    fun `8 characters with a digit and an uppercase is ok`() {
        assertEquals(Ok, checkPassword("Abcdefg1"))
    }

    @Test
    fun `no digit is rejected`() {
        assertEquals(Rejected(listOf(Rule.NO_DIGIT)), checkPassword("Abcdefgh"))
    }

    @Test
    fun `no uppercase letter is rejected`() {
        assertEquals(Rejected(listOf(Rule.NO_UPPERCASE)), checkPassword("abcdefg1"))
    }

    @Test
    fun `a space is rejected`() {
        assertEquals(Rejected(listOf(Rule.HAS_SPACE)), checkPassword("Abc defg1"))
    }

    @Test
    fun `every failure is reported, in rule order`() {
        assertEquals(
            Rejected(listOf(Rule.TOO_SHORT, Rule.NO_DIGIT, Rule.NO_UPPERCASE)),
            checkPassword("abc"),
        )
    }

    @Test
    fun `the empty password fails three rules`() {
        assertEquals(
            Rejected(listOf(Rule.TOO_SHORT, Rule.NO_DIGIT, Rule.NO_UPPERCASE)),
            checkPassword(""),
        )
    }
}
