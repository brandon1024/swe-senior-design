package com.unb.beforeigo.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserIdentityAvailabilityResponse implements Serializable {

    private final Boolean usernameAvailable;

    private final Boolean emailAddressAvailable;

}
