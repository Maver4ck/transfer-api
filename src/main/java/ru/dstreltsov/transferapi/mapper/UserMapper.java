package ru.dstreltsov.transferapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.dstreltsov.transferapi.dto.request.CreateUserRequestDto;
import ru.dstreltsov.transferapi.dto.request.UpdateUserRequestDto;
import ru.dstreltsov.transferapi.dto.response.UserResponseDto;
import ru.dstreltsov.transferapi.entity.Account;
import ru.dstreltsov.transferapi.entity.EmailData;
import ru.dstreltsov.transferapi.entity.PhoneData;
import ru.dstreltsov.transferapi.entity.User;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUserEntity(CreateUserRequestDto dto);

    @Mapping(source = "userId", target = "id")
    User toUserEntity(Long userId);

    @Mapping(target = "user", ignore = true)
    @Mapping(source = "balance", target = "initialBalance")
    @Mapping(source = "balance", target = "balance")
    Account toAccountEntity(CreateUserRequestDto.Account dto);

    @Mapping(target = "user", ignore = true)
    Set<EmailData> toEmailDataEntities(Set<CreateUserRequestDto.EmailData> emailData);

    @Mapping(target = "user", ignore = true)
    Set<EmailData> toEmailDataEntitiesForUpdate(Set<UpdateUserRequestDto.EmailData> emailData);

    @Mapping(target = "user", ignore = true)
    Set<PhoneData> toPhoneDataEntities(Set<CreateUserRequestDto.PhoneData> phoneData);

    @Mapping(target = "user", ignore = true)
    Set<PhoneData> toPhoneDataEntitiesForUpdate(Set<UpdateUserRequestDto.PhoneData> phoneData);

    UserResponseDto toUserDto(User user);
}
