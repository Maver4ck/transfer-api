package ru.dstreltsov.transferapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.dstreltsov.transferapi.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u.* FROM \"user\" u " +
            "WHERE (:dateOfBirth IS NULL OR u.date_of_birth > TO_DATE(:dateOfBirth, 'YYYY-MM-DD')) " +
            "AND (:phone IS NULL OR EXISTS (SELECT 1 FROM phone_data p WHERE p.phone = :phone AND p.user_id = u.id)) " +
            "AND (:name IS NULL OR u.name LIKE CONCAT(:name, '%')) " +
            "AND (:email IS NULL OR EXISTS (SELECT 1 FROM email_data e WHERE e.email = :email AND e.user_id = u.id))",
            countQuery = "SELECT COUNT(u.id) FROM \"user\" u " +
                    "WHERE (:dateOfBirth IS NULL OR u.date_of_birth > TO_DATE(:dateOfBirth, 'YYYY-MM-DD')) " +
                    "AND (:phone IS NULL OR EXISTS (SELECT 1 FROM phone_data p WHERE p.phone = :phone AND p.user_id = u.id)) " +
                    "AND (:name IS NULL OR u.name LIKE CONCAT(:name, '%')) " +
                    "AND (:email IS NULL OR EXISTS (SELECT 1 FROM email_data e WHERE e.email = :email AND e.user_id = u.id))",
            nativeQuery = true)
    Page<User> findUsersByFilters(String dateOfBirth,
                                  String phone,
                                  String name,
                                  String email,
                                  Pageable pageable);
}
