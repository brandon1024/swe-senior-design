package com.unb.beforeigo.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthenticationResponse implements Serializable {

    private final String token;
}
