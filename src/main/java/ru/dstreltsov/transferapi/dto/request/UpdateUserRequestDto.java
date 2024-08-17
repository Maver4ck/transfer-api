package ru.dstreltsov.transferapi.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequestDto {

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
