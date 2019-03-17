package ca.unb.ktb.infrastructure.security;

import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.application.dao.UserDAO;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.infrastructure.security.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPrincipalService implements UserDetailsService {

    @Autowired private UserDAO userDAO;

    @Override
    @Transactional
    public UserPrincipal loadUserByUsername(final String username) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("user not found with username '%s'", username)));

        return UserPrincipal.adapt(user);
    }

    @Transactional
    public UserPrincipal loadByEmailAddress(final String emailAddress) {
        User user = userDAO.findByEmail(emailAddress)
                .orElseThrow(() -> new UserNotFoundException(String.format("user not found with email '%s'", emailAddress)));

        return UserPrincipal.adapt(user);
    }

    @Transactional
    public UserPrincipal loadUserById(final Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("user not found with id: '%s'", userId)));

        return UserPrincipal.adapt(user);
    }

    /**
     * Build a UserSummaryResponse DTO of a {@link UserPrincipal}.
     *
     * @param user The principal to be used to build a UserSummaryResponse.
     * @return A summary of the principal.
     * */
    public static UserSummaryResponse adaptPrincipalToSummary(final UserPrincipal user) {
        return new UserSummaryResponse(user.getId(), user.getUsername(), user.getEmail(), null, null, null, null, null);
    }
}