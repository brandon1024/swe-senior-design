package com.unb.beforeigo.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserRegistrationRequest implements Serializable {

    private final String username;

    private final String email;

    private final String password;

    private final String passwordConfirm;

    private final String firstName;

    private final String middleName;

    private final String lastName;

    private String primaryStreetAddress;

    private String secondaryStreetAddress;

    private String city;

    private String province;

    private String country;

    private String postalCode;
}
