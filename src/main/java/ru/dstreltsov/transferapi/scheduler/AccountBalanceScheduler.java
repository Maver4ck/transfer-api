package ru.dstreltsov.transferapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.dstreltsov.transferapi.entity.Account;
import ru.dstreltsov.transferapi.service.AccountService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
//@ConditionalOnProperty опустим для простоты, но обычно делаем переключалку,
// чтобы иметь возможность переключать
public class AccountBalanceScheduler {

    private final AccountService accountService;

    @Scheduled(fixedRate = 30000)
    public void increaseBalance() {
        log.info("Start scheduled task: increase account balance.");
        final List<Account> accounts = accountService.findAllBelowMaxBalance();
        log.info("Accounts below max balance found: {}", accounts);
        accounts.forEach(acc -> accountService.updateBalance(acc.getId()));
    }
}
