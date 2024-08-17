package ru.dstreltsov.transferapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.dstreltsov.transferapi.entity.EmailData;
import ru.dstreltsov.transferapi.entity.User;
import ru.dstreltsov.transferapi.repository.EmailDataRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDataService {

    private final EmailDataRepository emailDataRepository;

    @Transactional
    public void processEmailsUpdate(User user, Set<EmailData> emailsFromRequest) {
        final Set<EmailData> existingEmails = user.getEmailData();
        final Set<EmailData> emailsToAdd = new HashSet<>(emailsFromRequest);
        final Set<EmailData> emailsToRemove = new HashSet<>(existingEmails);

        emailsToAdd.removeAll(existingEmails);
        emailsToRemove.removeAll(emailsFromRequest);

        log.info("Emails to add: {}, emails to remove: {}", emailsToAdd, emailsToRemove);

        final int emailCount = existingEmails.size() - emailsToRemove.size() + emailsToAdd.size();
        if (emailCount < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must have at least 1 email.");
        }

        removeEmails(emailsToRemove);
        addNewEmails(emailsToAdd);
    }

    public void saveEmailData(Set<EmailData> emails) {
        if (emailDataRepository.existsByEmailIn(getEmailsSet(emails))) {
            log.error("One or more provided email addresses already exist in the system.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email data.");
        }
        emailDataRepository.saveAll(emails);
    }

    public Optional<EmailData> findByEmailEagerly(String email) {
        return emailDataRepository.findByEmailEagerly(email);
    }

    private void addNewEmails(Set<EmailData> emailsToAdd) {
        saveEmailData(emailsToAdd);
    }

    private void removeEmails(Set<EmailData> emailsToRemove) {
        emailsToRemove = emailsToRemove.stream()
                .map(this::getEmailData)
                .collect(Collectors.toSet());
        emailDataRepository.deleteAll(emailsToRemove);
    }

    private EmailData getEmailData(EmailData email) {
        return emailDataRepository.findByEmail(email.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find email."));
    }

    private Set<String> getEmailsSet(Set<EmailData> emails) {
        return emails.stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toSet());
    }
}
