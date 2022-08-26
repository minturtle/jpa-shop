package jpabook.jpashop.dao.em;

import jpabook.jpashop.dao.ItemRepository;

import jpabook.jpashop.domain.item.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import javax.persistence.EntityManager;
import java.util.List;

public class EntityManagerItemRepository extends EntityManagerRepository<Item> implements ItemRepository {
    public EntityManagerItemRepository(EntityManager em) {
        super(em, Item.class);
    }

    @Override
    public List<Item> findAll(Pageable pageable) {
        final List<Item> all = findAll();
        return all.subList(5 * (pageable.getPageNumber()-1), 5 *(pageable.getPageNumber()));
    }
}


