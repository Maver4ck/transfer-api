package ru.dstreltsov.transferapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.dstreltsov.transferapi.entity.Account;
import ru.dstreltsov.transferapi.repository.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private static final BigDecimal PERCENTAGE_INCREASE = new BigDecimal("0.10");
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");

    private final AccountRepository accountRepository;

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }


    @Transactional
    @Async
    public void updateBalance(Long accountId) {
        final Account account = accountRepository.getByIdWithLock(accountId);
        log.info("Pessimistic lock gained on account: {}", accountId);

        final BigDecimal maxBalance = account.getInitialBalance()
                .multiply(MAX_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("Max possible balance after increase: {}", maxBalance);

        if (account.getBalance().compareTo(maxBalance) == 0) {
            log.info("Account has max amount set, skip.");
            return;
        }

        BigDecimal newBalance = account.getBalance()
                .multiply(PERCENTAGE_INCREASE.add(BigDecimal.ONE))
                .setScale(2, RoundingMode.HALF_UP);

        if (newBalance.compareTo(maxBalance) > 0) {
            newBalance = maxBalance;
            log.info("Increased amount is greater than max allowed.");
        }
        log.info("New balance on account: {}", newBalance);
        account.setBalance(newBalance);
        log.info("Release lock on account: {}", accountId);
    }

    public List<Account> findAllBelowMaxBalance() {
        return accountRepository.findAllBelowMaxBalance();
    }

    public Account getAccountByUserIdWithLock(Long userId) {
        return accountRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));
    }
}
