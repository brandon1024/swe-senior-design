package ca.unb.ktb.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * User friendship Hibernate entity. An instance of this class represents a persistent friendship record in the
 * database.
 *
 * @author Brandon Richardson
 * */
@Entity
@Table(name = "users_relationships",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"follower_id", "following_id"})})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRelationship extends PersistentObject {

    public UserRelationship(final Long id) {
        this.setId(id);
    }

    @NotNull
    @ManyToOne
    private User follower;

    @NotNull
    @ManyToOne
    private User following;
}
