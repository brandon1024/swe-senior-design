package com.unb.beforeigo.core.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * Hibernate entity that is used to represent a physical/mailing address.
 *
 * @author Brandon Richardson
 * */
@Entity
@Table(name = "physical_addresses")
@EntityListeners(AuditingEntityListener.class)
@Data
public class PhysicalAddress extends PersistentObject {

    @Size(max = 255)
    private String primaryStreetAddress;

    @Size(max = 255)
    private String secondaryStreetAddress;

    @Size(max = 255)
    private String city;

    @Size(max = 255)
    private String province;

    @Size(max = 255)
    private String country;

    @Size(max = 255)
    private String postalCode;
}
