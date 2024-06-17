package jpabook.jpashop.controller.product;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.controller.common.response.CategoryResponse;
import jpabook.jpashop.dto.CategoryDto;
import jpabook.jpashop.dto.CursorListDto;
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

import java.util.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Loggable
@Tag(name = "상품 정보를 조회하는 API입니다.")
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;



    @Operation(
            summary = "상품 리스트 조회 API V1",
            description = "상품의 리스트를 조회하는 API입니다. OFFSET 방식의 Pagination을 지원합니다.\n\n 상품은 이름으로 검색할 수 있으며, price range 범위 검색, 카테고리 필터링, 상품 타입 필터링이 가능하며, 정렬은 최신순, 이름순, 가격 낮은순으로 정렬할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리")
            }
    )
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


        validPriceRange(minPrice, maxPrice);

        ProductDto.PriceRange priceRange = createPriceRange(minPrice, maxPrice);


        ProductDto.SearchCondition searchCondition = createSearchCondition(query, category, sortType, productType, priceRange);



        PaginationListDto<ProductDto.Preview> result = productService.search(
                searchCondition,
                PageRequest.of(page - 1, size)
        );

        List<ProductResponse.Preview> responseList = result.getData()
                .stream().map(dto -> new ProductResponse.Preview(dto.getUid(), dto.getName(), dto.getPrice(), dto.getThumbnailUrl(), dto.getCreatedAt())).toList();

        return new PaginationListDto<>(
                result.getCount(),
                responseList
                );
    }


    @Operation(
            summary = "상품 리스트 조회 API V2",
            description = "상품의 리스트를 조회하는 API입니다. CURSOR 방식의 Pagination을 지원합니다.\n\n 상품은 이름으로 검색할 수 있으며, price range 범위 검색, 카테고리 필터링, 상품 타입 필터링이 가능하며, 정렬은 최신순, 이름순, 가격 낮은순으로 정렬할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리")
            }
    )
    @GetMapping("/v2/list")
    public CursorListDto<ProductResponse.Preview> searchCursor(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "BY_DATE") SortOption sortType,
            @RequestParam(required = false, defaultValue = "ALL") ProductType productType

    ){

        validPriceRange(minPrice, maxPrice);


        ProductDto.PriceRange priceRange = createPriceRange(minPrice, maxPrice);
        ProductDto.SearchCondition searchCondition = createSearchCondition(query, category, sortType, productType, priceRange);

        List<ProductDto.Preview> result = productService.search(searchCondition, Optional.ofNullable(cursor), size);



        return createProductListResponse(result);
    }

    @Operation(
            summary = "상품 상세 조회 API",
            description = "상품의 상세 정보를 조회하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리"),
                    @ApiResponse(responseCode = "404", description = "상품 정보 조회 실패 시"),
                    @ApiResponse(responseCode = "500", description = "상품 타입 변환 실패 시")
            }
    )
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

    @Operation(
            summary = "상품 카테고리 조회 API",
            description = "상품의 카테고리 정보를 조회하는 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상 처리")
            }
    )
    @GetMapping("/category")
    public CategoryResponse.ListResult getCategories(){
        List<CategoryDto.Info> categories = productService.getCategories();

        CategoryResponse.ListResult result = new CategoryResponse.ListResult();



        for(ProductType productType : ProductType.values()){
            result.add(productType);
        }


        for(CategoryDto.Info category : categories){
            result.get(category.getProductType()).addCategory(category.getUid(), category.getName());
        }

        return result;
    }


    /**
     * @author minseok kim
     * @description 상품 리스트 조회시 SearchCondition에 사용하기 위한 PriceRange를 설정하는 메서드
     * @param minPrice 조회할 상품의 최소 가격
     * @param maxPrice 조회할 상품의 최대 가격
     * @return PriceRange 객체
     * @exception
    */
    private static ProductDto.PriceRange createPriceRange(Integer minPrice, Integer maxPrice) {
        ProductDto.PriceRange priceRange = null;
        if(minPrice != null && maxPrice != null){
            priceRange = new ProductDto.PriceRange(minPrice, maxPrice);
        }
        return priceRange;
    }


    /**
     * @author minseok kim
     * @description 검색한 상품의 금액 범위가 올바른지 확인하는 메서드
     * @param minPrice 조회할 상품의 최소 가격
     * @param maxPrice 조회할 상품의 최대 가격
     * @exception IllegalArgumentException 상품의 범위가 올바르지 않을 시
    */
    private static void validPriceRange(Integer minPrice, Integer maxPrice) {
        if(minPrice != null && maxPrice != null && minPrice > maxPrice){
            throw new IllegalArgumentException(ProductExceptionMessages.PRICE_RANGE_INVALID.getMessage());
        }
    }

    /**
     * @author minseok kim
     * @description 상품 검색을 위한 SearchCondition 객체를 생성하는 메서드
     * @return SearchCondition 객체
    */
    private static ProductDto.SearchCondition createSearchCondition(String query, String category, SortOption sortType, ProductType productType, ProductDto.PriceRange priceRange) {
        return new ProductDto.SearchCondition(
                Optional.ofNullable(query),
                Optional.ofNullable(priceRange),
                Optional.ofNullable(category),
                sortType,
                productType
        );
    }


    /**
     * @author minseok kim
     * @description 상품 조회 결과를 기반으로 Response Body를 생성하는 API
     * @param result 상품 조회 결과
     * @return response body 객체
    */
    private static CursorListDto<ProductResponse.Preview> createProductListResponse(List<ProductDto.Preview> result) {
        List<ProductResponse.Preview> responseList = result
                .stream().map(dto -> new ProductResponse.Preview(dto.getUid(), dto.getName(), dto.getPrice(), dto.getThumbnailUrl(), dto.getCreatedAt())).toList();

        String cursor = null;

        if(!result.isEmpty()){
            ProductDto.Preview lastProduct = result.get(result.size() - 1);
            cursor = lastProduct.getUid();

        }


        return CursorListDto.<ProductResponse.Preview>builder()
                .cursor(cursor)
                .data(responseList)
                .build();
    }

}



