package com.unb.bucket.core.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * User model. An instance of this class represents a persistent user record in the database.
 *
 * @author Brandon Richardson
 * */
@Entity
@Table(name = "USERS")
@EntityListeners(AuditingEntityListener.class)
@Data
public class User extends PersistentObject{

    @Email(message = "Email must be valid")
    @Column(unique=true)
    private String email;

    @NotBlank
    @Column(unique=true)
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String middleName;
}
