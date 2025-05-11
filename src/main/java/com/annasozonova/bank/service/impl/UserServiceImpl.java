package com.annasozonova.bank.service.impl;

import com.annasozonova.bank.dto.CreateUserRequest;
import com.annasozonova.bank.dto.UserDto;
import com.annasozonova.bank.exception.ResourceNotFoundException;
import com.annasozonova.bank.model.Role;
import com.annasozonova.bank.repository.RoleRepository;
import com.annasozonova.bank.repository.UserRepository;
import com.annasozonova.bank.service.UserService;
import com.annasozonova.bank.util.UserMapper;
import com.annasozonova.bank.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * {@link UserService} implementation for managing application users.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Registers a new user.
     *
     * @param req user creation request
     * @return created user
     */
    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest req) {
        User user = UserMapper.toEntity(req);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        OffsetDateTime now = OffsetDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return UserMapper.toDto(userRepo.save(user));
    }

    /**
     * Returns a user by ID.
     *
     * @param userId user ID
     * @return found user
     * @throws ResourceNotFoundException if user does not exist
     */
    @Override
    public UserDto getUserById(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        return UserMapper.toDto(user);
    }

    /**
     * Returns all users with pagination.
     *
     * @param pageable pagination options
     * @return paginated list of users
     */
    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(UserMapper::toDto);
    }

    /**
     * Updates user information.
     *
     * @param userId user ID
     * @param req    updated data
     * @return updated user
     * @throws ResourceNotFoundException if user does not exist
     */
    @Override
    public UserDto updateUser(UUID userId, CreateUserRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setUpdatedAt(OffsetDateTime.now());
        return UserMapper.toDto(userRepo.save(user));
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId user ID
     * @throws ResourceNotFoundException if user does not exist
     */
    @Override
    public void deleteUser(UUID userId) {
        try {
            userRepo.deleteById(userId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }
    }

    /**
     * Returns a user by email.
     *
     * @param email user email
     * @return found user
     * @throws ResourceNotFoundException if user does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserMapper.toDto(user);
    }

    /**
     * Assigns roles to a user.
     *
     * @param userId    user ID
     * @param roleNames names of roles (e.g. "USER", "ADMIN")
     * @return updated user with assigned roles
     * @throws ResourceNotFoundException if user or any role does not exist
     */
    @Override
    @Transactional
    public UserDto assignRoles(UUID userId, Set<String> roleNames) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Set<Role> roles = roleNames.stream()
                .map(name -> roleRepo.findByName(name.toUpperCase())
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        user.setUpdatedAt(OffsetDateTime.now());
        return UserMapper.toDto(userRepo.save(user));
    }
}