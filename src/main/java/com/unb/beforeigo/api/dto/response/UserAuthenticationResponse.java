package com.unb.beforeigo.api.dto.response;

import com.unb.beforeigo.infrastructure.security.UserPrincipal;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserAuthenticationResponse implements Serializable {

    private final String token;

    private final UserPrincipal user;
}
