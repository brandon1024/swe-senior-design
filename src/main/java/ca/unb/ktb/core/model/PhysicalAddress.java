package ca.unb.ktb.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PhysicalAddress extends PersistentObject {

    public PhysicalAddress(final Long id) {
        this.setId(id);
    }

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

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if(!(object instanceof PhysicalAddress)) {
            return false;
        }

        PhysicalAddress other = (PhysicalAddress)object;
        return (other.getId() == null ? this.getId() == null : other.getId().equals(this.getId()));
    }
}
