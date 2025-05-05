package com.learnbridge.learn_bridge_back_end.security;

import com.learnbridge.learn_bridge_back_end.entity.AccountStatus;
import com.learnbridge.learn_bridge_back_end.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // we use email here for authentication instead of the actual username
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean isEnabled() {
        // Only ACTIVE users can authenticate
        return user.getAccountStatus() == AccountStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        // BLOCKED users are treated as “locked”
        return user.getAccountStatus() != AccountStatus.BLOCKED;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Optionally, treat DELETED or DISABLED as expired
        return user.getAccountStatus() == AccountStatus.ACTIVE;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getRole() {
        return user.getUserRole().name();
    }
}
