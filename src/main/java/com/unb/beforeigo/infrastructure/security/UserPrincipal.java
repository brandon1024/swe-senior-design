package com.unb.beforeigo.infrastructure.security;

import com.unb.beforeigo.core.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal extends org.springframework.security.core.userdetails.User {

    private final Long id;

    private final String email;

    public UserPrincipal(final Long id, final String username, final String email, final String password,
                         final Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.email = email;
    }

    private UserPrincipal(final User user) {
        super(user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        this.id = user.getId();
        this.email = user.getEmail();
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public static UserPrincipal adapt(final User user) {
        return new UserPrincipal(user);
    }
}
