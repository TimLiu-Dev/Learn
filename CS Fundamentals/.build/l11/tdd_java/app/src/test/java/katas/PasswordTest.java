package katas;

import katas.PasswordResult.Ok;
import katas.PasswordResult.Rejected;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PasswordTest {
    @Test
    void sevenCharactersIsTooShort() {
        assertEquals(new Rejected(List.of(Rule.TOO_SHORT)), Password.check("Abcdef1"));
    }

    @Test
    void eightCharactersWithADigitAndAnUppercaseIsOk() {
        assertEquals(new Ok(), Password.check("Abcdefg1"));
    }

    @Test
    void noDigitIsRejected() {
        assertEquals(new Rejected(List.of(Rule.NO_DIGIT)), Password.check("Abcdefgh"));
    }

    @Test
    void noUppercaseLetterIsRejected() {
        assertEquals(new Rejected(List.of(Rule.NO_UPPERCASE)), Password.check("abcdefg1"));
    }

    @Test
    void aSpaceIsRejected() {
        assertEquals(new Rejected(List.of(Rule.HAS_SPACE)), Password.check("Abc defg1"));
    }

    @Test
    void everyFailureIsReportedInRuleOrder() {
        assertEquals(new Rejected(List.of(Rule.TOO_SHORT, Rule.NO_DIGIT, Rule.NO_UPPERCASE)),
                Password.check("abc"));
    }

    @Test
    void theEmptyPasswordFailsThreeRules() {
        assertEquals(new Rejected(List.of(Rule.TOO_SHORT, Rule.NO_DIGIT, Rule.NO_UPPERCASE)),
                Password.check(""));
    }
}
