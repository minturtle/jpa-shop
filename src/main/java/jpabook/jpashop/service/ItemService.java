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

    public <T> T findByName(String name, Class<T> c) throws IllegalArgumentException, EntityNotFoundException {
        Item findItem = itemRepository.findByName(name); //throwable EntityNotFoundException
        validateArguments(c, findItem); //찾은 상품과 argument로 입력받은 클래스가 같은 타입인지 확인
        return (T)findItem;
    }

    public Long updateItem(Long id, Item modifiedItem)throws IllegalArgumentException, EntityNotFoundException{
        Item findItem = itemRepository.findById(id); //throwable EntityNotFoundException
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
