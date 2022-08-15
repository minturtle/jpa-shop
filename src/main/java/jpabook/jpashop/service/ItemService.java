package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public <T> T findByName(String name, Class<T> c) throws IllegalArgumentException{
        Item findItem = itemRepository.findByName(name).orElseThrow(()->new IllegalArgumentException("해당되는 상품을 찾을 수 없습니다."));
        validateArguments(c, findItem);
        return (T)findItem;
    }

    public Long updateItem(Long id, Item modifiedItem)throws IllegalArgumentException{
        Item findItem = itemRepository.findById(id).orElseThrow(()->new IllegalArgumentException("상품을 찾을 수 없습니다."));
        findItem.update(modifiedItem);
        return findItem.getId();
    }


    public List<Item> findAll(){
        return itemRepository.findAll();
    }

    private <T> void validateArguments(Class<T> c, Item findItem)throws IllegalArgumentException {
        if(!findItem.getClass().equals(c)) {
            throw new IllegalArgumentException("찾은 상품과 같은 타입이 아닙니다.");
        }
    }
}
