package jpabook.jpashop.controller;


import jpabook.jpashop.dto.ItemDto;
import jpabook.jpashop.service.ItemService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items")
    public ItemListResponse getItemList(@RequestParam int page, @RequestParam(defaultValue = "최신순")String sortTypeStr){
        ItemService.SortType sortType = ItemService.SortType.valueOf(sortTypeStr);
        final List<ItemDto> itemDtos = itemService.findAll(page, sortType);


        final ItemListResponse res = new ItemListResponse();
        res.setItems(itemDtos.stream()
                .map(itemDto->new ItemListResponse.ItemPreview(itemDto.getId(), itemDto.getName(), itemDto.getPrice()))
                .collect(Collectors.toList()));

        return res;
    }

    @GetMapping("/detail")
    public ItemDetailResponse getDetail(@RequestParam(name = "itemId")Long id){
        ItemDto findItem = itemService.findById(id);

        return new ItemDetailResponse(findItem.getId(), findItem.getName(), findItem.getDescription(), findItem.getPrice());
    }

    @GetMapping("/search")
    public ResponseEntity searchByName(@RequestParam String itemName){
        final ItemDto findItem = itemService.findByName(itemName);

        return new ResponseEntity(
                new ItemListResponse.ItemPreview(findItem.getId(), findItem.getName(), findItem.getPrice())
                ,HttpStatus.OK);
    }
}


// == response ==

@Getter @Setter
@NoArgsConstructor
class ItemListResponse{
    private List<ItemPreview> items = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemPreview{
        private Long itemId;
        private String itemName;
        private int price;
    }
}

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
class ItemDetailResponse {

    private Long itemId;
    private String itemName;
    private String description;
    private int price;

}
