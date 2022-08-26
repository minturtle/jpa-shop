package jpabook.jpashop.dao;

import jpabook.jpashop.domain.item.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ItemRepository {

    void save(Item item);
    Item findByName(String name);
    Item findById(Long id);
    List<Item> findAll(Pageable pageable);
    List<Item> findAll();
}
