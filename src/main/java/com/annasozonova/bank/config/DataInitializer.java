package com.annasozonova.bank.config;

import com.annasozonova.bank.dto.CreateUserRequest;
import com.annasozonova.bank.dto.UserDto;
import com.annasozonova.bank.model.Role;
import com.annasozonova.bank.repository.RoleRepository;
import com.annasozonova.bank.repository.UserRepository;
import com.annasozonova.bank.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Initializes default roles and admin user if they are missing.
 * <p>
 * This runs automatically at application startup.
 * </p>
 */
@Component
public class DataInitializer {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final UserService userService;

    public DataInitializer(RoleRepository roleRepo, UserService userService, UserRepository userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    /**
     * Ensures that required roles and the default admin user are present.
     * Runs once on application startup.
     */
    @PostConstruct
    public void init() {
        // Ensure required roles exist
        if (!roleRepo.existsByName("USER")) {
            roleRepo.save(Role.builder().name("USER").build());
        }
        if (!roleRepo.existsByName("ADMIN")) {
            roleRepo.save(Role.builder().name("ADMIN").build());
        }

        // Ensure default admin user exists
        String adminEmail = "admin@example.com";
        if (!userRepo.existsByEmail(adminEmail)) {
            CreateUserRequest req = new CreateUserRequest(
                    adminEmail,
                    "admin",
                    "Super",
                    "Admin"
            );
            UserDto adminDto = userService.createUser(req);
            userService.assignRoles(adminDto.getId(), Set.of("ADMIN"));
        }
    }
}
