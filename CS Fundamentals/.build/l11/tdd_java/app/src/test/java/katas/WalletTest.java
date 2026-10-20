package katas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletTest {
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();          // a fresh wallet before EVERY test
    }

    @Test
    void depositIncreasesTheBalance() {
        wallet.deposit(500);
        assertEquals(500, wallet.getBalance());
    }

    @Test
    void aNewWalletIsEmpty() {
        assertEquals(0, wallet.getBalance());   // unaffected by the test above
    }

    @Test
    void withdrawingTooMuchIsRejected() {
        wallet.deposit(100);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> wallet.withdraw(500));
        assertEquals("insufficient funds", e.getMessage());
        assertEquals(100, wallet.getBalance()); // and nothing changed
    }

    @Test
    void zeroDepositIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(0));
    }
}
