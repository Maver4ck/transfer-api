package ru.dstreltsov.transferapi.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class JwtRequestDto {

    @Email(message = "Wrong email format.")
    @NotNull(message = "Email is required.")
    private String email;
    @NotBlank(message = "Password is required and cannot be blank.")
    @Size(min = 8, max = 500, message = "Password must be between 8 and 500 characters long.")
    private String password;
}
