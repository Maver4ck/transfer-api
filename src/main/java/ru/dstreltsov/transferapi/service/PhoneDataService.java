package ru.dstreltsov.transferapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.dstreltsov.transferapi.entity.PhoneData;
import ru.dstreltsov.transferapi.entity.User;
import ru.dstreltsov.transferapi.repository.PhoneDataRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneDataService {

    private final PhoneDataRepository phoneDataRepository;

    @Transactional
    public void processPhonesUpdate(User user, Set<PhoneData> phonesFromRequest) {
        final Set<PhoneData> existingPhones = user.getPhoneData();
        final Set<PhoneData> phonesToAdd = new HashSet<>(phonesFromRequest);
        final Set<PhoneData> phonesToRemove = new HashSet<>(existingPhones);

        phonesToAdd.removeAll(existingPhones);
        phonesToRemove.removeAll(phonesFromRequest);

        log.info("Phones to add: {}, phones to remove: {}", phonesToAdd, phonesToRemove);

        final int phoneCount = existingPhones.size() - phonesToRemove.size() + phonesToAdd.size();
        if (phoneCount < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must have at least 1 phone.");
        }

        removePhones(phonesToRemove);
        addNewPhones(phonesToAdd);
    }

    public void savePhoneData(Set<PhoneData> phones) {
        if (phoneDataRepository.existsByPhoneIn(getPhonesSet(phones))) {
            log.error("One or more provided phones already exist in the system.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone data.");
        }
        phoneDataRepository.saveAll(phones);
    }

    private void addNewPhones(Set<PhoneData> phonesToAdd) {
        savePhoneData(phonesToAdd);
    }

    private void removePhones(Set<PhoneData> phonesToRemove) {
        phonesToRemove = phonesToRemove.stream()
                .map(this::getPhoneData)
                .collect(Collectors.toSet());
        phoneDataRepository.deleteAll(phonesToRemove);
    }

    private PhoneData getPhoneData(PhoneData phone) {
        return phoneDataRepository.findByPhone(phone.getPhone())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find phone."));
    }

    private Set<String> getPhonesSet(Set<PhoneData> phones) {
        return phones.stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toSet());
    }
}
