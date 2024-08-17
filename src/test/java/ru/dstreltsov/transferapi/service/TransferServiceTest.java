package ru.dstreltsov.transferapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.dstreltsov.transferapi.entity.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferService transferService;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    public void setUp() {
        fromAccount = new Account();
        toAccount = new Account();
    }

    @Test
    void testTransferMoney_Success() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(50);

        fromAccount.setBalance(BigDecimal.valueOf(100));
        toAccount.setBalance(BigDecimal.valueOf(20));

        when(accountService.getAccountByUserIdWithLock(fromUserId)).thenReturn(fromAccount);
        when(accountService.getAccountByUserIdWithLock(toUserId)).thenReturn(toAccount);

        transferService.transferMoney(fromUserId, toUserId, amount);

        assertEquals(BigDecimal.valueOf(50), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(70), toAccount.getBalance());
    }

    @Test
    void testTransferMoney_NegativeAmount() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(-50);

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount)
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        assertEquals("Transfer amount must be positive.", thrown.getReason());
    }

    @Test
    void testTransferMoney_SameUser() {
        Long fromUserId = 1L;
        Long toUserId = 1L;
        BigDecimal amount = BigDecimal.valueOf(50);

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount)
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        assertEquals("Transfer can be made only to other user.", thrown.getReason());
    }

    @Test
    void testTransferMoney_NotEnoughMoney() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(150);

        fromAccount.setBalance(BigDecimal.valueOf(100));
        toAccount.setBalance(BigDecimal.valueOf(20));

        when(accountService.getAccountByUserIdWithLock(fromUserId)).thenReturn(fromAccount);
        when(accountService.getAccountByUserIdWithLock(toUserId)).thenReturn(toAccount);

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount)
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        assertEquals("Not enough money.", thrown.getReason());
    }
}