package com.annasozonova.bank.security;

import com.annasozonova.bank.model.User;
import com.annasozonova.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Custom implementation of {@link UserDetailsService} that loads users
 * by email (for login) or by UUID (for JWT token validation).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Loads a user by email address.
     * Used by Spring Security during login authentication.
     *
     * @param email user email
     * @return user details
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new UserPrincipal(user);
    }

    /**
     * Loads a user by UUID.
     * Used by {@link com.annasozonova.bank.security.JwtAuthFilter} when validating JWT tokens.
     *
     * @param userId user UUID
     * @return user details
     * @throws UsernameNotFoundException if user is not found
     */
    public UserDetails loadUserById(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return new UserPrincipal(user);
    }
}
