package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {

    Long save(Item item);
    List<Item> findByNameContains(String name);
    List<Item> findAll(Pageable pageable);
    List<Item> findAll();
}
