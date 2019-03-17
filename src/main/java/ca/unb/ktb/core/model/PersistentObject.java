package ca.unb.ktb.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Top level class whose fields are applied to all entities that inherit from it.
 *
 * @author Brandon Richardson
 * */
@MappedSuperclass
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class PersistentObject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if(!(object instanceof PersistentObject)) {
            return false;
        }

        PersistentObject other = (PersistentObject)object;
        return (other.getId() == null ? this.getId() == null : other.getId().equals(this.getId()));
    }

    @Override
    public int hashCode() {
        return this.id == null ? super.hashCode() : this.id.intValue();
    }
}
