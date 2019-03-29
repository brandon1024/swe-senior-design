package ca.unb.ktb.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest implements Serializable {

    private String username;

    private String email;

    @ToString.Exclude
    private String password;

    @ToString.Exclude
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
