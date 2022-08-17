package jpabook.jpashop.dao;

import jpabook.jpashop.domain.item.Item;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ItemRepository extends EntityManagerRepository<Item> {

    public ItemRepository(EntityManager em) {
        super(em, Item.class);
    }
}


