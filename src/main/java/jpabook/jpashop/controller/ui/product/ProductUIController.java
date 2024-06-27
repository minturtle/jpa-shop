package jpabook.jpashop.controller.ui.product;


import jpabook.jpashop.controller.api.common.response.CategoryResponse;
import jpabook.jpashop.controller.api.common.response.ProductResponse;
import jpabook.jpashop.dto.CategoryDto;
import jpabook.jpashop.dto.CursorListDto;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductUIController {

    private final ProductService productService;
    @GetMapping("/list")
    public String getProductList(
            Model model
    ){
        Map<ProductType, CategoryResponse.ListInfo> categories = createCategory(productService.getCategories());

        model.addAttribute("categories", categories);
        return "/product/list";
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


    private static Map<ProductType, CategoryResponse.ListInfo> createCategory(List<CategoryDto.Info> categories){
        Map<ProductType, CategoryResponse.ListInfo> result = new HashMap<>();

        for(ProductType productType : ProductType.values()){
            if(productType.equals(ProductType.ALL)){
                continue;
            }
            result.put(productType, new CategoryResponse.ListInfo(productType));
        }

        for(CategoryDto.Info category : categories){
            result.get(category.getProductType()).addCategory(category.getUid(), category.getName());
        }


        return result;
    }

}
