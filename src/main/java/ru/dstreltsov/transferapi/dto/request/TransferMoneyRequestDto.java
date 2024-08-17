package ru.dstreltsov.transferapi.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class TransferMoneyRequestDto {

    @NotNull(message = "Recipient user ID must not be null.")
    private Long toUser;
    @NotNull(message = "Transfer amount must not be null.")
    @Min(value = 1, message = "Transfer amount must be greater than 0.")
    @Digits(integer = 18, fraction = 2, message = "Balance must have up to 18 digits with up to 2 decimal places.")
    private BigDecimal amount;
}
