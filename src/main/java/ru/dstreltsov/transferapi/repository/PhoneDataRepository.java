package ru.dstreltsov.transferapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dstreltsov.transferapi.entity.PhoneData;

import java.util.Optional;
import java.util.Set;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    Optional<PhoneData> findByPhone(String phone);

    boolean existsByPhoneIn(Set<String> phones);
}
