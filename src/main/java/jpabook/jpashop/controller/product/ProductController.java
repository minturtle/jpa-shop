package jpabook.jpashop.controller.product;


import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.dto.PaginationListDto;
import jpabook.jpashop.controller.common.response.ProductResponse;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.common.InternalErrorException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.service.ProductService;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Loggable
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;
    @GetMapping("/list")
    public PaginationListDto<ProductResponse.Preview> search(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "BY_DATE") SortOption sortType,
            @RequestParam(required = false, defaultValue = "ALL") ProductType productType
            ){


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
                PageRequest.of(page - 1, size)
        );

        List<ProductResponse.Preview> responseList = result.getData()
                .stream().map(dto -> new ProductResponse.Preview(dto.getUid(), dto.getName(), dto.getPrice(), dto.getThumbnailUrl())).toList();

        return new PaginationListDto<>(
                result.getCount(),
                responseList
                );
    }

    @GetMapping("/{productUid}")
    public ProductResponse.Detail findById(
            @PathVariable(name = "productUid")String productUid
    ) throws CannotFindEntityException, InternalErrorException {
        ProductDto.Detail product = productService.findByUid(productUid);

        if(product instanceof  ProductDto.MovieDetail){
            return modelMapper.map(product, ProductResponse.MovieDetail.class);
        }
        if(product instanceof  ProductDto.AlbumDetail){
            return modelMapper.map(product, ProductResponse.AlbumDetail.class);
        }
        if(product instanceof  ProductDto.BookDetail){
            return modelMapper.map(product, ProductResponse.BookDetail.class);
        }

        throw new InternalErrorException(ProductExceptionMessages.ENTITY_PRODUCT_MAPPING_FAILED.getMessage());
    }

}



