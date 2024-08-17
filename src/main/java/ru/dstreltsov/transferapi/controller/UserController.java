package ru.dstreltsov.transferapi.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dstreltsov.transferapi.dto.request.CreateUserRequestDto;
import ru.dstreltsov.transferapi.dto.request.UpdateUserRequestDto;
import ru.dstreltsov.transferapi.dto.response.UserResponseDto;
import ru.dstreltsov.transferapi.entity.EmailData;
import ru.dstreltsov.transferapi.entity.PhoneData;
import ru.dstreltsov.transferapi.entity.User;
import ru.dstreltsov.transferapi.mapper.UserMapper;
import ru.dstreltsov.transferapi.security.MyUserDetails;
import ru.dstreltsov.transferapi.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping(path = "/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "User API")
public class UserController {

    private final UserMapper mapper;
    private final UserService userService;

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with associated account, email, and phone data.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User creation request with user data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateUserRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User created successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request due to invalid phone or email data",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequestDto dto) {
        final User user = mapper.toUserEntity(dto);
        user.getAccount().setUser(user);
        user.getEmailData().forEach(email -> email.setUser(user));
        user.getPhoneData().forEach(phone -> phone.setUser(user));

        userService.createUser(user);

        return ResponseEntity.ok().build();
    }

    @PatchMapping
    @Operation(
            summary = "Update user information",
            description = "Updates the user's email and phone data.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User update request containing new email and phone data",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request due to invalid data such as missing email or phone",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UpdateUserRequestDto dto,
                                           @AuthenticationPrincipal MyUserDetails userDetails) {
        final User user = mapper.toUserEntity(userDetails.getUserId());
        final Set<EmailData> updatedEmailData = mapper.toEmailDataEntitiesForUpdate(dto.getEmailData());
        final Set<PhoneData> updatedPhoneData = mapper.toPhoneDataEntitiesForUpdate(dto.getPhoneData());
        updatedEmailData.forEach(email -> email.setUser(user));
        updatedPhoneData.forEach(phone -> phone.setUser(user));

        userService.updateUser(user, updatedEmailData, updatedPhoneData);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(
            summary = "Get users by filters",
            description = "Retrieves a paginated list of users filtered by date of birth, phone, name, and email.",
            parameters = {
                    @Parameter(
                            name = "dateOfBirth",
                            description = "Filter users by date of birth. Format: dd.MM.yyyy",
                            example = "01.01.1990"
                    ),
                    @Parameter(
                            name = "phone",
                            description = "Filter users by phone number.",
                            example = "79991112233"
                    ),
                    @Parameter(
                            name = "name",
                            description = "Filter users by name (partial match).",
                            example = "John"
                    ),
                    @Parameter(
                            name = "email",
                            description = "Filter users by email.",
                            example = "john.doe@example.com"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved users",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Page.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    )
            }
    )
    public ResponseEntity<Page<UserResponseDto>> getUsers(@RequestParam(required = false) @JsonFormat(pattern = "dd.MM.yyyy") LocalDate dateOfBirth,
                                                          @RequestParam(required = false) String phone,
                                                          @RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String email,
                                                          Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").ascending());
        }

        final Page<User> page = userService.findUsersByFilters(dateOfBirth, phone, name, email, pageable);

        return ResponseEntity.ok(page.map(mapper::toUserDto));
    }
}
