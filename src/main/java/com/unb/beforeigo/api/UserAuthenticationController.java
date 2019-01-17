package com.unb.beforeigo.api;

import com.unb.beforeigo.api.dto.request.UserAuthenticationRequest;
import com.unb.beforeigo.api.dto.request.UserRegistrationRequest;
import com.unb.beforeigo.api.dto.response.UserAuthenticationResponse;
import com.unb.beforeigo.api.dto.response.UserIdentityAvailabilityResponse;
import com.unb.beforeigo.api.dto.response.UserSummaryResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.validation.EntityValidator;
import com.unb.beforeigo.core.svc.UserService;
import com.unb.beforeigo.infrastructure.security.JSONWebTokenUtil;
import com.unb.beforeigo.infrastructure.security.UserPrincipal;
import com.unb.beforeigo.infrastructure.security.UserPrincipalService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

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
     * @param request A valid authentication request.
     * @return The token. HTTP CREATED.
     * @throws AuthenticationException If authentication fails.
     * */
    @ApiOperation(value = "Issue a new token to a user.", response = UserAuthenticationResponse.class)
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public ResponseEntity<UserAuthenticationResponse> issueToken(@Valid @RequestBody final UserAuthenticationRequest request)
            throws AuthenticationException {
        final UserPrincipal userPrincipal;
        if(Objects.isNull(request.getEmailAddress())) {
            userPrincipal = userPrincipalService.loadUserByUsername(request.getUsername());
        } else {
            userPrincipal = userPrincipalService.loadByEmailAddress(request.getEmailAddress());
        }

        //Attempt to authenticate the user
        final Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal.getUsername(),
                request.getPassword());
        authenticationManager.authenticate(auth);

        //Generate JWT token
        final String token = JSONWebTokenUtil.generateToken(userPrincipal);
        userPrincipal.eraseCredentials();

        UserSummaryResponse response = UserPrincipalService.adaptPrincipalToSummary(userPrincipal);
        return new ResponseEntity<>(new UserAuthenticationResponse(token, response), HttpStatus.CREATED);
    }

    /**
     * Register a new user and issue a new token.
     *
     * @param registrationRequest A valid authentication request.
     * @return The token. HTTP OK.
     * @throws AuthenticationException If authentication fails.
     * @throws BadRequestException If the new user does not meet validation constraints.
     * */
    @ApiOperation(value = "Register a new user and issue a new token.", response = UserAuthenticationResponse.class)
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<UserAuthenticationResponse> registerAndIssueToken(
            @Valid @RequestBody final UserRegistrationRequest registrationRequest) {
        if(!Objects.equals(registrationRequest.getPassword(), registrationRequest.getPasswordConfirm())) {
            throw new BadRequestException("Password mismatch.");
        }

        final User user = UserService.buildUserFromRegistrationRequest(registrationRequest);
        EntityValidator.validateEntity(user, () -> new BadRequestException("new user does not meet validation constraints"));
        userService.createUser(user);

        //Attempt to authenticate the user
        final Authentication auth = new UsernamePasswordAuthenticationToken(registrationRequest.getUsername(),
                registrationRequest.getPassword());
        authenticationManager.authenticate(auth);

        //Load user and generate JWT token
        final UserPrincipal userPrincipal = userPrincipalService.loadUserByUsername(registrationRequest.getUsername());
        final String token = JSONWebTokenUtil.generateToken(userPrincipal);
        userPrincipal.eraseCredentials();

        UserSummaryResponse response = UserPrincipalService.adaptPrincipalToSummary(userPrincipal);
        return new ResponseEntity<>(new UserAuthenticationResponse(token, response), HttpStatus.OK);
    }

    /**
     * Refresh token.
     *
     * @param auth The authentication token.
     * @return The token. HTTP OK.
     * @throws UnauthorizedException If the user is not authenticated.
     * */
    @ApiOperation(value = "Refresh a token.", response = UserAuthenticationResponse.class)
    @RequestMapping(value = "/token_refresh", method = RequestMethod.POST)
    public ResponseEntity<UserAuthenticationResponse> refreshToken(@AuthenticationPrincipal final Authentication auth) {
        if(Objects.isNull(auth)) {
            throw new UnauthorizedException("User is not authenticated, and therefore cannot be granted a new token.");
        }

        //Generate new token
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        final String newToken = JSONWebTokenUtil.generateToken(userPrincipal);
        userPrincipal.eraseCredentials();

        UserSummaryResponse response = UserPrincipalService.adaptPrincipalToSummary(userPrincipal);
        return new ResponseEntity<>(new UserAuthenticationResponse(newToken, response), HttpStatus.OK);
    }

    /**
     * Check if a given identity is available.
     *
     * @param username Optional request parameter for the email address.
     * @param email Optional request parameter for the username.
     * @return UserIdentityAvailabilityResponse. HTTP OK.
     * */
    @ApiOperation(value = "Check if a given identity is available.", response = UserAuthenticationResponse.class)
    @RequestMapping(value = "/identity_available", method = RequestMethod.GET)
    public ResponseEntity<UserIdentityAvailabilityResponse> checkIdentityAvailability(
            @RequestParam(name = "username", required = false) final String username,
            @RequestParam(name = "email", required = false) final String email) {
        if(Objects.isNull(username) && Objects.isNull(email)) {
            throw new BadRequestException("username or email request parameter must be specified");
        }

        final Boolean usernameAvailable = username != null ? !userDAO.existsByUsername(username) : null;
        final Boolean emailAddressAvailable = email != null ? !userDAO.existsByEmail(email) : null;

        final UserIdentityAvailabilityResponse response =
                new UserIdentityAvailabilityResponse(usernameAvailable, emailAddressAvailable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
