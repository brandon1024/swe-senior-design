package ca.unb.ktb.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationRequest implements Serializable {

    private String username;

    private String emailAddress;

    private String password;
}
