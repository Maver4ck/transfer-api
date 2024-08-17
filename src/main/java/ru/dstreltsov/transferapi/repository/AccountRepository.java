package ru.dstreltsov.transferapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.dstreltsov.transferapi.entity.Account;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    Optional<Account> findByUserIdWithLock(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Account getByIdWithLock(Long id);

    //оставим для простоты коэффициент константой
    @Query("SELECT a FROM Account a WHERE a.balance < a.initialBalance * 2.07")
    List<Account> findAllBelowMaxBalance();
}
