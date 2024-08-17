package ru.dstreltsov.transferapi.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequestDto {

    @NotBlank(message = "Name is required and cannot be blank.")
    @Size(min = 1, max = 500, message = "Name must be between 1 and 500 characters long.")
    private String name;
    @PastOrPresent(message = "Date of birth must be a past or present date.")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;
    @Size(min = 8, max = 500, message = "Password must be between 8 and 500 characters long.")
    @NotBlank(message = "Password is required and cannot be blank.")
    private String password;
    @Valid
    @NotNull(message = "Account information is required.")
    private Account account;
    @NotEmpty(message = "At least one email address is required.")
    @Valid
    private Set<EmailData> emailData;
    @NotEmpty(message = "At least one phone number is required.")
    @Valid
    private Set<PhoneData> phoneData;

    @JsonIgnore
    @AssertTrue(message = "Email data must contain unique elements.")
    public boolean isEmailDataUnique() {
        return emailData != null && emailData.size() == new HashSet<>(emailData).size();
    }

    @JsonIgnore
    @AssertTrue(message = "Phone data must contain unique elements.")
    public boolean isPhoneDataUnique() {
        return phoneData != null && phoneData.size() == new HashSet<>(phoneData).size();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Account {
        @NotNull(message = "Balance is required.")
        @PositiveOrZero(message = "Balance cannot be negative.")
        @Digits(integer = 18, fraction = 2, message = "Balance must have up to 18 digits with up to 2 decimal places.")
        private BigDecimal balance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailData {
        @Email(message = "Incorrect email format.")
        @NotBlank(message = "Email is required and cannot be blank.")
        @Size(min = 1, max = 200, message = "Email must be between 1 and 200 characters long.")
        private String email;

        @Override
        public int hashCode() {
            return Objects.hash(email);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmailData emailData = (EmailData) o;
            return Objects.equals(email, emailData.email);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneData {
        @NotBlank
        @Size(min = 11, max = 11, message = "Phone must be 11 characters long.")
        @Pattern(regexp = "7\\d{10}", message = "Phone number must start with '7' and be followed by 10 digits.")
        private String phone;

        @Override
        public int hashCode() {
            return Objects.hash(phone);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PhoneData phoneData = (PhoneData) o;
            return Objects.equals(phone, phoneData.phone);
        }
    }
}
