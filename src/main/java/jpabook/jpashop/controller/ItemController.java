package jpabook.jpashop.controller;


import jpabook.jpashop.dto.ItemDto;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items")
    public List<ItemDto.ItemPreviewDto> getItemList(@RequestParam int page, @RequestParam(defaultValue = "최신순")String sortTypeStr){
        ItemService.SortType sortType = ItemService.SortType.valueOf(sortTypeStr);
        final List<ItemDto> itemDtos = itemService.findAll(page, sortType);

        return itemDtos.stream()
                .map(itemDto->new ItemDto.ItemPreviewDto(itemDto.getId(), itemDto.getName(), itemDto.getPrice()))
                .collect(Collectors.toList());

    }
}
