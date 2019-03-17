package ca.unb.ktb.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserSummaryResponse implements Serializable {

    private final Long id;

    private final String username;

    private final String email;

    private final String bio;

    private final String firstName;

    private final String middleName;

    private final String lastName;

    private final String profileImageResourceUrl;
}
