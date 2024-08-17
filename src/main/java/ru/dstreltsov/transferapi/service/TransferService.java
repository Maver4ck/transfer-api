package ru.dstreltsov.transferapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.dstreltsov.transferapi.entity.Account;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountService accountService;

    @CacheEvict(value = "users", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer amount must be positive.");
        }

        if (Objects.equals(fromUserId, toUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer can be made only to other user.");
        }

        final Account fromAccount = accountService.getAccountByUserIdWithLock(fromUserId);
        final Account toAccount = accountService.getAccountByUserIdWithLock(toUserId);

        log.info("Locks gained on accounts: {}, {}", fromAccount.getId(), toAccount.getId());

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough money.");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        log.info("Release locks");
    }
}
