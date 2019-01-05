package com.unb.beforeigo.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserAuthenticationRequest implements Serializable {

    private final String username;

    private final String emailAddress;

    private final String password;
}
