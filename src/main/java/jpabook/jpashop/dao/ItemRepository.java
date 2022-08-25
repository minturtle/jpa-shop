package jpabook.jpashop.dao;

import jpabook.jpashop.domain.item.Item;

import java.util.List;

public interface ItemRepository {

    void save(Item item);
    Item findByName(String name);
    Item findById(Long id);
    List<Item> findAll();

}
