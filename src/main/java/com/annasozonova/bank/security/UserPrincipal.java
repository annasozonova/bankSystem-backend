package com.annasozonova.bank.security;

import com.annasozonova.bank.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Custom Spring Security {@link UserDetails} implementation that wraps a {@link User} entity.
 * Used internally by authentication logic and JWT validation.
 */
public class UserPrincipal implements UserDetails {

    /**
     * Unique identifier of the user.
     */
    @Getter
    private final UUID id;

    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructs a UserPrincipal from a User entity.
     * Roles are mapped to {@code ROLE_XXX} format.
     *
     * @param user application user entity
     */
    public UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.password = user.getPasswordHash();
        this.enabled = Boolean.TRUE.equals(user.getEnabled());
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Returns granted authorities (roles) of the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns hashed password of the user.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns login (email) of the user.
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user account is expired.
     * Always returns true (not expired).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user account is locked.
     * Always returns true (not locked).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user credentials are expired.
     * Always returns true (not expired).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
