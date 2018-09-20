package com.unb.beforeigo.application.dao;

import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA Repository interface for defining specific DAO methods not already generated by JPA.
 *
 * @author Tyler Sargent
 * */
public interface ItemDAO extends JpaRepository<Item, Long> {

    /**
     * Find all {@link Item} objects with a given parent that are public.
     *
     * @param parent the creator and owner of the items to be found
     * @return a list of public items created by a given bucket
     * */
    List<Item> findAllByParent(final Bucket parent);
}