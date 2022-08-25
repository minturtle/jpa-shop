package jpabook.jpashop.dao.em;

import jpabook.jpashop.dao.ItemRepository;

import jpabook.jpashop.domain.item.Item;


import javax.persistence.EntityManager;

public class EntityManagerItemRepository extends EntityManagerRepository<Item> implements ItemRepository {
    public EntityManagerItemRepository(EntityManager em) {
        super(em, Item.class);
    }
}


