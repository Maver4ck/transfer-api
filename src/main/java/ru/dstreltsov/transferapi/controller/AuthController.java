package ru.dstreltsov.transferapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dstreltsov.transferapi.dto.request.JwtRequestDto;
import ru.dstreltsov.transferapi.dto.response.JwtResponseDto;
import ru.dstreltsov.transferapi.security.JwtTokenGenerator;
import ru.dstreltsov.transferapi.security.MyUserDetails;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Auth API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate a user and generate a JWT token",
            description = "This endpoint allows a user to authenticate by providing their email and password." +
                    " If the credentials are valid, a JWT token is generated and returned.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Authentication request containing email and password",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful authentication, returns a JWT token",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request if the input is invalid",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden if credentials are invalid",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            })
    public ResponseEntity<JwtResponseDto> createAuthenticationToken(@Valid @RequestBody JwtRequestDto dto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        final MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        final String token = jwtTokenGenerator.generateToken(userDetails.getUserId(), userDetails.getUsername());

        //возвращаем только токен, но обычно возвращаем еще
        //"type": "Bearer", "expiration": ...
        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}
