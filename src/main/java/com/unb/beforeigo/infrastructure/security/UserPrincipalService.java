package com.unb.beforeigo.infrastructure.security;

import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPrincipalService implements UserDetailsService {

    @Autowired UserDAO userDAO;

    @Override
    @Transactional
    public UserPrincipal loadUserByUsername(final String username) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserPrincipal.adapt(user);
    }

    @Transactional
    public UserPrincipal loadUserById(final Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return UserPrincipal.adapt(user);
    }
}