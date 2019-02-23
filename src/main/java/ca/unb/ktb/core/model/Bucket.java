package ca.unb.ktb.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Hibernate entity representing a bucket.
 *
 * @author Brandon Richardson
 * */
@Entity
@Table(name = "buckets")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Bucket extends PersistentObject {

    public Bucket(final Long id) {
        this.setId(id);
    }

    @NotBlank
    @Size(max = 255)
    private String name;

    @ManyToOne
    @NotNull
    private User owner;

    @NotNull
    private Boolean isPublic;

    @Column(columnDefinition = "TEXT")
    private String description;
}
