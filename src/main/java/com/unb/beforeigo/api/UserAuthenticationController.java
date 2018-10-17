package com.unb.beforeigo.api;

import com.unb.beforeigo.api.dto.request.AuthenticationRequest;
import com.unb.beforeigo.api.dto.response.AuthenticationResponse;
import com.unb.beforeigo.api.dto.response.IdentityAvailabilityResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.application.dao.UserDAO;
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

    @Autowired UserPrincipalService userPrincipalService;

    @Autowired AuthenticationManager authenticationManager;

    @Autowired UserDAO userDAO;

    /**
     * todo
     * */
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody final AuthenticationRequest request) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        final UserPrincipal userDetails = userPrincipalService.loadUserByUsername(request.getUsername());
        final String token = JSONWebTokenUtil.generateToken(userDetails);

        return new ResponseEntity<>(new AuthenticationResponse(token), HttpStatus.OK);
    }

    /**
     * todo
     * */
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> registerUser() {
        return null;
    }

    /**
     * todo
     * */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logoutUser() {
        return null;
    }

    /**
     * todo
     * */
    @RequestMapping(value = "/identity_available", method = RequestMethod.GET)
    public ResponseEntity<IdentityAvailabilityResponse> checkIdentityAvailability(@RequestParam(name = "username", required = false) final String username,
                                                                                  @RequestParam(name = "email", required = false) final String email) {
        if(username == null && email == null) {
            throw new BadRequestException("username or email request parameter must be specified");
        }

        Boolean usernameAvailable = null;
        Boolean emailAddressAvailable = null;

        if(username != null) {
            usernameAvailable = userDAO.existsByUsername(username);
        }

        if(email != null) {
            emailAddressAvailable = userDAO.existsByEmail(email);
        }

        return new ResponseEntity<>(new IdentityAvailabilityResponse(usernameAvailable, emailAddressAvailable), HttpStatus.OK);
    }
}
