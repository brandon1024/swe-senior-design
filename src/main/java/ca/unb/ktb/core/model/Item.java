package ca.unb.ktb.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@Getter
@Setter
@ToString
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
}