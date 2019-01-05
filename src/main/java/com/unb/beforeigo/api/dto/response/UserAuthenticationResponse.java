package com.unb.beforeigo.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserAuthenticationResponse implements Serializable {

    private final String token;

    private final UserSummaryResponse user;
}
