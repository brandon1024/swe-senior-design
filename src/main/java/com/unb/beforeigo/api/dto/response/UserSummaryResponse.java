package com.unb.beforeigo.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSummaryResponse implements Serializable {

    private final Long id;

    private final String username;

    private final String email;

    private final String firstName;

    private final String middleName;

    private final String lastName;
}
