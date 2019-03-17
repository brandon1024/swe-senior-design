package ca.unb.ktb.core.model;

import ca.unb.ktb.core.model.validation.Username;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * User Hibernate entity. An instance of this class represents a persistent user record in the database.
 *
 * @author Brandon Richardson
 * */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User extends PersistentObject {

    public enum Role {
        ADMIN,
        DEV,
        USER,
        VISITOR
    }

    public User(final Long id) {
        this.setId(id);
    }

    @Email(message = "Email must be valid")
    @Column(unique = true)
    @Size(max = 255)
    private String email;

    @Username
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 255)
    private String firstName;

    @NotBlank
    @Size(max = 255)
    private String lastName;

    @Size(max = 255)
    private String middleName;

    @OneToOne(orphanRemoval = true)
    private PhysicalAddress userAddress;

    @Size(max = 255)
    private String bio;

    @NotBlank
    @Size(max = 255)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;

    @JsonIgnore
    private String profilePictureObjectKey;

    /**
     * Build the user handle string for this user. A user handle is the '@' symbol, followed by the username.
     *
     * @return the user handle ("@username")
     * */
    public String getUserHandle() {
        return "@" + this.username;
    }
}
