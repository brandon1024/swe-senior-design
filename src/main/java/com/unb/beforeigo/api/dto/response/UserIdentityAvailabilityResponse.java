package com.unb.beforeigo.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserIdentityAvailabilityResponse implements Serializable {

    private final Boolean usernameAvailable;

    private final Boolean emailAddressAvailable;

}
