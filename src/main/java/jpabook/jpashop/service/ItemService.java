package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Item save(Item item){
        itemRepository.save(item);
        return item;
    }

    public Item findByName(String name) throws IllegalArgumentException, EntityNotFoundException {
        Item findItem = itemRepository.findByName(name); //throwable EntityNotFoundException
        return findItem;
    }

    public Long updateItem(Long id, Item modifiedItem)throws IllegalArgumentException, EntityNotFoundException{
        Item findItem = itemRepository.findById(id); //throwable EntityNotFoundException
        findItem.update(modifiedItem);
        return findItem.getId();
    }

    public List<Item> findAll(){
        return itemRepository.findAll();
    }


}
