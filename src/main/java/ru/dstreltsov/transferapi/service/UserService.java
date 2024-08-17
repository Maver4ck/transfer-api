package ru.dstreltsov.transferapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.dstreltsov.transferapi.entity.EmailData;
import ru.dstreltsov.transferapi.entity.PhoneData;
import ru.dstreltsov.transferapi.entity.User;
import ru.dstreltsov.transferapi.repository.UserRepository;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final EmailDataService emailDataService;
    private final PhoneDataService phoneDataService;

    @Transactional(rollbackFor = Exception.class)
    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        accountService.saveAccount(user.getAccount());
        emailDataService.saveEmailData(user.getEmailData());
        phoneDataService.savePhoneData(user.getPhoneData());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user, Set<EmailData> emailsFromRequest, Set<PhoneData> phonesFromRequest) {
        mergeUser(user);

        emailDataService.processEmailsUpdate(user, emailsFromRequest);
        phoneDataService.processPhonesUpdate(user, phonesFromRequest);
    }

    @Cacheable("users")
    public Page<User> findUsersByFilters(LocalDate dateOfBirth,
                                         String phone,
                                         String name,
                                         String email,
                                         Pageable pageable) {
        return userRepository.findUsersByFilters(
                dateOfBirth == null ? null : dateOfBirth.toString(),
                phone,
                name,
                email,
                pageable
        );
    }

    private void mergeUser(User detachedUser) {
        final User dbUser = userRepository.findById(detachedUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        detachedUser.copy(dbUser);
    }
}
