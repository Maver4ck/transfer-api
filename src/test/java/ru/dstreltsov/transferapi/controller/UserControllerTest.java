package ru.dstreltsov.transferapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.dstreltsov.transferapi.dto.request.CreateUserRequestDto;
import ru.dstreltsov.transferapi.entity.User;
import ru.dstreltsov.transferapi.mapper.UserMapper;
import ru.dstreltsov.transferapi.mapper.UserMapperImpl;
import ru.dstreltsov.transferapi.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import({UserMapperImpl.class, UserController.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper mapper;

    @MockBean
    private UserService userService;

    @Test
    void testCreateUser() throws Exception {
        CreateUserRequestDto.Account account = new CreateUserRequestDto.Account();
        account.setBalance(BigDecimal.valueOf(1000.00));

        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setName("John Doe");
        requestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        requestDto.setPassword("password");
        requestDto.setAccount(account);
        requestDto.setEmailData(Set.of(emailData("john.doe@example.com"), emailData("unknown@example.com")));
        requestDto.setPhoneData(Set.of(phoneData("71234567890"), phoneData("79991231212")));

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(userService).createUser(any(User.class));
    }

    private CreateUserRequestDto.EmailData emailData(String email) {
        return new CreateUserRequestDto.EmailData(email);
    }

    private CreateUserRequestDto.PhoneData phoneData(String phone) {
        return new CreateUserRequestDto.PhoneData(phone);
    }

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().permitAll();

            return httpSecurity.build();
        }
    }
}
