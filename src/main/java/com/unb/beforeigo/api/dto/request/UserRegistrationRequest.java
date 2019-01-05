package com.unb.beforeigo.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest implements Serializable {

    private String username;

    private String email;

    private String password;

    private String passwordConfirm;

    private String firstName;

    private String middleName;

    private String lastName;

    private String primaryStreetAddress;

    private String secondaryStreetAddress;

    private String city;

    private String province;

    private String country;

    private String postalCode;
}
