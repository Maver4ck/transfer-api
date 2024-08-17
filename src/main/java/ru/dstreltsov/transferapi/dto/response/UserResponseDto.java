package ru.dstreltsov.transferapi.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private Account account;
    private Set<EmailData> emailData;
    private Set<PhoneData> phoneData;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Account {
        private BigDecimal balance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EmailData {
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PhoneData {
        private String phone;
    }
}
