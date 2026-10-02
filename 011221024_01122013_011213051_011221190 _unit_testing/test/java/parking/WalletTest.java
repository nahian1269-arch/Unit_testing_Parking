package parking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {
    @Test
    void noArgumentConstructorStartsAtZero() {
        assertEquals(0.0, new Wallet().getBalance());
    }

    @Test
    void balanceConstructorPreservesStartingBalance() {
        assertEquals(100.0, new Wallet(100.0).getBalance());
    }

    @Test
    void addFundsIncreasesBalance() {
        Wallet wallet = new Wallet(20.0);
        wallet.addFunds(7.5);
        assertEquals(27.5, wallet.getBalance());
    }

    @Test
    void addFundsRejectsZeroAndNegativeAmounts() {
        Wallet wallet = new Wallet(20.0);
        assertThrows(InvalidAmountException.class, () -> wallet.addFunds(0.0));
        assertThrows(InvalidAmountException.class, () -> wallet.addFunds(-1.0));
        assertEquals(20.0, wallet.getBalance());
    }

    @Test
    void deductFundsAllowsExactBalance() {
        Wallet wallet = new Wallet(20.0);
        wallet.deductFunds(20.0);
        assertEquals(0.0, wallet.getBalance());
    }

    @Test
    void deductFundsRejectsInsufficientFundsWithoutChangingBalance() {
        Wallet wallet = new Wallet(20.0);
        assertThrows(InsufficientFundsException.class, () -> wallet.deductFunds(20.01));
        assertEquals(20.0, wallet.getBalance());
    }

    @Test
    void deductFundsRejectsNonPositiveAmounts() {
        Wallet wallet = new Wallet(20.0);
        assertThrows(InvalidAmountException.class, () -> wallet.deductFunds(0.0));
        assertThrows(InvalidAmountException.class, () -> wallet.deductFunds(-1.0));
        assertEquals(20.0, wallet.getBalance());
    }

    @Test
    void transferMovesFundsBetweenWallets() {
        Wallet from = new Wallet(30.0);
        Wallet to = new Wallet(5.0);
        from.transferFunds(to, 12.0);
        assertEquals(18.0, from.getBalance());
        assertEquals(17.0, to.getBalance());
    }

    @Test
    void transferRejectsInsufficientFundsAndLeavesBothWalletsUnchanged() {
        Wallet from = new Wallet(5.0);
        Wallet to = new Wallet(2.0);
        assertThrows(InsufficientFundsException.class, () -> from.transferFunds(to, 6.0));
        assertEquals(5.0, from.getBalance());
        assertEquals(2.0, to.getBalance());
    }

    @Test
    void transferRejectsNonPositiveAmounts() {
        Wallet from = new Wallet(5.0);
        Wallet to = new Wallet(2.0);
        assertThrows(InvalidAmountException.class, () -> from.transferFunds(to, 0.0));
        assertThrows(InvalidAmountException.class, () -> from.transferFunds(to, -1.0));
        assertEquals(5.0, from.getBalance());
        assertEquals(2.0, to.getBalance());
    }
}
