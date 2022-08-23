package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public void save(Item item){
        itemRepository.save(item);
    }

    public ItemDto findByName(String name) throws EntityNotFoundException {
        Item findItem = itemRepository.findByName(name); //throwable EntityNotFoundException

        ItemDto itemDto = createItemDto(findItem);


        return itemDto;
    }


    public Long updateItem(Long id, ItemDto modifiedItemInfo)throws IllegalArgumentException, EntityNotFoundException{
        Item findItem = itemRepository.findById(id); //throwable EntityNotFoundException

        findItem.update(modifiedItemInfo);
        return findItem.getId();
    }

    public List<ItemDto> findAll(){
        return itemRepository.findAll().stream().map(this::createItemDto).collect(Collectors.toList());
    }



    private ItemDto createItemDto(Item findItem) {
        return new ItemDto.ItemDtoBuilder()
                .putItemField(findItem.getName(), findItem.getPrice(), findItem.getStockQuantity())
                .setItemType(findItem.getClass())
                .putInheritedFields(findItem)
                .build();
    }
}
