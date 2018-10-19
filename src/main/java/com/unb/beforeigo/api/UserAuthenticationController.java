package com.unb.beforeigo.api;

import com.unb.beforeigo.api.dto.request.AuthenticationRequest;
import com.unb.beforeigo.api.dto.request.UserRegistrationRequest;
import com.unb.beforeigo.api.dto.response.AuthenticationResponse;
import com.unb.beforeigo.api.dto.response.IdentityAvailabilityResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.application.dao.PhysicalAddressDAO;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.svc.UserService;
import com.unb.beforeigo.infrastructure.security.JSONWebTokenUtil;
import com.unb.beforeigo.infrastructure.security.UserPrincipal;
import com.unb.beforeigo.infrastructure.security.UserPrincipalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
public class UserAuthenticationController {

    @Autowired private UserPrincipalService userPrincipalService;

    @Autowired private AuthenticationManager authenticationManager;

    @Autowired private UserDAO userDAO;

    @Autowired private UserService userService;

    /**
     * Issue token to user.
     *
     * @param request a valid authentication request
     * @return 200 OK if the authentication succeeded, with the token in the response body.
     * @throws AuthenticationException if authentication fails
     * @throws com.unb.beforeigo.infrastructure.security.exception.UserNotFoundException if a user with the username
     * in the request body does not exist.
     * */
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public ResponseEntity<AuthenticationResponse> issueToken(@Valid @RequestBody final AuthenticationRequest request) throws AuthenticationException {
        final UserPrincipal userPrincipal;
        if(request.getUsername() != null) {
            userPrincipal = userPrincipalService.loadUserByUsername(request.getUsername());
        } else {
            userPrincipal = userPrincipalService.loadByEmailAddress(request.getEmailAddress());
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userPrincipal.getUsername(), request.getPassword()));
        final String token = JSONWebTokenUtil.generateToken(userPrincipal);

        return new ResponseEntity<>(new AuthenticationResponse(token, userPrincipal), HttpStatus.OK);
    }

    /**
     * Register a new user and issue a new token.
     *
     * @param registrationRequest a valid authentication request
     * @return 200 OK if the registration and authentication succeeded, with the token in the response body.
     * @throws AuthenticationException if authentication fails
     * @throws BadRequestException if the new user does not meet validation constraints
     * */
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<AuthenticationResponse> registerAndIssueToken(@Valid @RequestBody final UserRegistrationRequest registrationRequest) {
        if(!registrationRequest.getPassword().equals(registrationRequest.getPasswordConfirm())) {
            throw new BadRequestException("Password mismatch.");
        }

        User user = userService.buildUserFromRegistrationRequest(registrationRequest);
        userService.validateUser(user, () -> new BadRequestException("new user does not meet validation constraints"));
        userService.saveUser(user);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registrationRequest.getUsername(), registrationRequest.getPassword()));
        final UserPrincipal userPrincipal = userPrincipalService.loadUserByUsername(registrationRequest.getUsername());
        final String token = JSONWebTokenUtil.generateToken(userPrincipal);

        return new ResponseEntity<>(new AuthenticationResponse(token, userPrincipal), HttpStatus.OK);
    }

    /**
     * Refresh token.
     *
     * @return 200 OK if the authentication succeeded, with the token in the response body.
     * */
    @RequestMapping(value = "/token_refresh", method = RequestMethod.POST)
    public ResponseEntity<AuthenticationResponse> refreshToken(@AuthenticationPrincipal final UserPrincipal userPrincipal) {
        if(userPrincipal == null) {
            throw new UnauthorizedException("User is not authenticated, and therefore cannot be granted a new token.");
        }

        final String newToken = JSONWebTokenUtil.generateToken(userPrincipal);
        return new ResponseEntity<>(new AuthenticationResponse(newToken, userPrincipal), HttpStatus.OK);
    }

    /**
     * Check if a given identity is available.
     *
     * @param username optional request parameter for the email address
     * @param email optional request parameter for the username
     * @return IdentityAvailabilityResponse
     * */
    @RequestMapping(value = "/identity_available", method = RequestMethod.GET)
    public ResponseEntity<IdentityAvailabilityResponse> checkIdentityAvailability(@RequestParam(name = "username", required = false) final String username,
                                                                                  @RequestParam(name = "email", required = false) final String email) {
        if(username == null && email == null) {
            throw new BadRequestException("username or email request parameter must be specified");
        }

        Boolean usernameAvailable = username != null ? !userDAO.existsByUsername(username) : null;
        Boolean emailAddressAvailable = email != null ? !userDAO.existsByEmail(email) : null;

        return new ResponseEntity<>(new IdentityAvailabilityResponse(usernameAvailable, emailAddressAvailable), HttpStatus.OK);
    }
}
