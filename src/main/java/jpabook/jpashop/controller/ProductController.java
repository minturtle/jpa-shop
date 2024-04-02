package jpabook.jpashop.controller;


import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.controller.response.ItemResponse;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.service.ProductService;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping("/list")
    public PaginationListDto<ItemResponse.Preview> search(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "0") int minPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "214783647") int maxPrice,
            @RequestParam(required = false, defaultValue = "BY_DATE") SortOption sortType,
            @RequestParam(required = false) ProductType productType
            ){
        PaginationListDto<ProductDto.Preview> result = productService.search(
                new ProductDto.SearchCondition(
                        Optional.ofNullable(query),
                        Optional.of(new ProductDto.PriceRange(minPrice, maxPrice)),
                        Optional.ofNullable(category),
                        sortType,
                        productType
                ),
                PageRequest.of(page, size)
        );

        return null;
    }

    @GetMapping("/{itemId}")
    public ItemResponse.Detail findById(
            @PathVariable(name = "itemId")String itemId
    ){
        return null;
    }

}



