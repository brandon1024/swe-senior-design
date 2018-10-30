package com.unb.beforeigo.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAuthenticationResponse implements Serializable {

    private final String token;

    private final UserSummaryResponse user;
}
