package ru.dstreltsov.transferapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.dstreltsov.transferapi.entity.EmailData;
import ru.dstreltsov.transferapi.service.EmailDataService;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final EmailDataService emailDataService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //отдаем более общую ошибку для защиты от перебора
        final EmailData emailData = emailDataService.findByEmailEagerly(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid data."));
        return new MyUserDetails(
                emailData.getUser().getId(),
                emailData.getEmail(),
                emailData.getUser().getPassword(),
                new ArrayList<>()
        );
    }
}
