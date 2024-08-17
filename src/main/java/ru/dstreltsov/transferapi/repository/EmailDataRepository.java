package ru.dstreltsov.transferapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.dstreltsov.transferapi.entity.EmailData;

import java.util.Optional;
import java.util.Set;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    Optional<EmailData> findByEmail(String email);

    @Query("SELECT e FROM EmailData e JOIN FETCH e.user WHERE e.email = :email")
    Optional<EmailData> findByEmailEagerly(String email);

    boolean existsByEmailIn(Set<String> emails);
}
