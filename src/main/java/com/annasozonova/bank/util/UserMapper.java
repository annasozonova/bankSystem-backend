package com.annasozonova.bank.util;

import com.annasozonova.bank.dto.CreateUserRequest;
import com.annasozonova.bank.dto.UserDto;
import com.annasozonova.bank.model.Role;
import com.annasozonova.bank.model.User;

import java.util.stream.Collectors;

/**
 * Mapper utility for converting between {@link User} entities and DTOs.
 */
public class UserMapper {

    /**
     * Converts a {@link CreateUserRequest} into a {@link User} entity.
     *
     * @param req user creation request
     * @return user entity
     */
    public static User toEntity(CreateUserRequest req) {
        var user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash(req.getPassword());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEnabled(true);
        return user;
    }

    /**
     * Converts a {@link User} entity into a {@link UserDto}.
     *
     * @param user user entity
     * @return user DTO
     */
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                user.getEnabled()
        );
    }
}
