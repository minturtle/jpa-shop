package jpabook.jpashop.controller;


import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.controller.response.ProductResponse;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.service.ProductService;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping("/list")
    public PaginationListDto<ProductResponse.Preview> search(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) SortOption sortType,
            @RequestParam(required = false) ProductType productType
            ){
        if (sortType == null) {
            sortType = SortOption.BY_DATE;
        }


        if(minPrice != null && maxPrice != null && minPrice > maxPrice){
            throw new IllegalArgumentException(ProductExceptionMessages.PRICE_RANGE_INVALID.getMessage());
        }

        ProductDto.PriceRange priceRange = null;
        if(minPrice != null && maxPrice != null){
            priceRange = new ProductDto.PriceRange(minPrice, maxPrice);
        }


        PaginationListDto<ProductDto.Preview> result = productService.search(
                new ProductDto.SearchCondition(
                        Optional.ofNullable(query),
                        Optional.ofNullable(priceRange),
                        Optional.ofNullable(category),
                        sortType,
                        productType
                ),
                PageRequest.of(page, size)
        );

        return null;
    }
//
//    @GetMapping("/{itemId}")
//    public ProductResponse.Detail findById(
//            @PathVariable(name = "itemId")String itemId
//    ){
//        return null;
//    }

}



