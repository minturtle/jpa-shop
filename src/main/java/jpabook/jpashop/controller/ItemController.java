package jpabook.jpashop.controller;


import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.controller.response.ItemResponse;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.service.ProductService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ProductService productService;

    @GetMapping("/")
    public PaginationListDto<ItemResponse.Preview> search(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "BY_DATE") SortOption sortType
    ){
        return null;
    }

    @GetMapping("/{itemId}")
    public ItemResponse.Detail findById(
            @PathVariable(name = "itemId")String itemId
    ){
        return null;
    }

}



