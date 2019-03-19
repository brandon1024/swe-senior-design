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
 * DTO representing an item.
 *
 * @author Tyler Sargent
 * */
@Entity
@Table(name = "items")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Item extends PersistentObject {

    public Item(final Long id) {
        this.setId(id);
    }

    @NotBlank
    @Size(max = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Size(max = 255)
    private String link;

    @ManyToOne
    @NotNull
    private Bucket parent;

    @NotNull
    private Boolean isComplete;

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if(!(object instanceof Item)) {
            return false;
        }

        Item other = (Item)object;
        return (other.getId() == null ? this.getId() == null : other.getId().equals(this.getId()));
    }
}