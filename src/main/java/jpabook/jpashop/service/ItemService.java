package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private static final int PAGE_SIZE = 10;

    public void save(Item item){
        itemRepository.save(item);
    }


    public ItemDto findByName(String name) throws EntityNotFoundException {
        Item findItem = itemRepository.findByName(name); //throwable EntityNotFoundException

        ItemDto itemDto = createItemDto(findItem);


        return itemDto;
    }

    /*
    * 물건의 정보를 수정함.
    *
    * @param id : 수정할 Item Entity의 Id값
    * @param modifiedItemInfo : 수정된 물건의 정보가 담겨있는 객체
    *
    * @return : 수정한 Item Entity의 Id값
    * */
    public Long updateItem(Long id, ItemDto modifiedItemInfo)throws IllegalArgumentException, EntityNotFoundException{
        Item findItem = itemRepository.findById(id); //throwable EntityNotFoundException

        findItem.update(modifiedItemInfo);
        return findItem.getId();
    }


    public List<ItemDto> findAll(int page, SortType sortType){
        Sort sort = getSortByType(sortType);

        return itemRepository.findAll(PageRequest.of(page-1, PAGE_SIZE, sort))
                .stream().map(this::createItemDto).collect(Collectors.toList());
    }


    private ItemDto createItemDto(Item findItem) {
        return new ItemDto.ItemDtoBuilder()
                .putItemField(findItem.getName(), findItem.getPrice(), findItem.getStockQuantity())
                .setItemType(findItem.getClass())
                .putInheritedFields(findItem)
                .build();
    }

    private Sort getSortByType(SortType sortType) {
        Sort sort;

        if(sortType.equals(SortType.가격순)){
            sort = Sort.by("price").ascending();
        }
        else if(sortType.equals(SortType.이름순)){
            sort = Sort.by("name").ascending();
        }
        else{
            sort = Sort.by("id").descending();
        }
        return sort;
    }


    public static enum SortType{
        이름순, 가격순, 최신순
    }
}
