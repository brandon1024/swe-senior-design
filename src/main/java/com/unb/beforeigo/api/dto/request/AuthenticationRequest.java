package com.unb.beforeigo.api.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthenticationRequest implements Serializable {

    private final String username;

    private final String emailAddress;

    private final String password;
}
