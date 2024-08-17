package ru.dstreltsov.transferapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dstreltsov.transferapi.dto.request.TransferMoneyRequestDto;
import ru.dstreltsov.transferapi.security.MyUserDetails;
import ru.dstreltsov.transferapi.service.TransferService;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping(path = "/v1/transfers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Transfer API")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @Operation(
            summary = "Transfer money between users",
            description = "Transfers a specified amount of money from one user to another.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Transfer money request containing recipient user ID and amount",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransferMoneyRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful transfer of money",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request, either due to invalid transfer amount or insufficient funds",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Account not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    public ResponseEntity<Void> transferMoney(@Valid @RequestBody TransferMoneyRequestDto dto,
                                              @AuthenticationPrincipal MyUserDetails userDetails) {
        final Long fromUserId = userDetails.getUserId();
        final Long toUserId = dto.getToUser();
        final BigDecimal amount = dto.getAmount();

        transferService.transferMoney(fromUserId, toUserId, amount);

        return ResponseEntity.ok().build();
    }
}
