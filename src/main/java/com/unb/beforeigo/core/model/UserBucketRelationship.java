package com.unb.beforeigo.core.model;

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
 * Hibernate entity representing a bucket.
 *
 * @author Brandon Richardson
 * */
@Entity
@Table(name = "users_bucket_relationships",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"follower_id", "following_id"})})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserBucketRelationship extends PersistentObject {

    public UserBucketRelationship(final Long id) {
        this.setId(id);
    }

    @NotNull
    @ManyToOne
    private User follower;

    @NotNull
    @ManyToOne
    private Bucket following;

}