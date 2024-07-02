package jpabook.jpashop.controller.ui.product;


import jpabook.jpashop.controller.api.common.response.CategoryResponse;
import jpabook.jpashop.controller.api.common.response.ProductResponse;
import jpabook.jpashop.dto.CategoryDto;
import jpabook.jpashop.dto.CursorListDto;
import jpabook.jpashop.dto.ProductDto;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.common.InternalErrorException;
import jpabook.jpashop.exception.product.ProductExceptionMessages;
import jpabook.jpashop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{productUid}")
    public String getProductDetail(
            @PathVariable String productUid,
            Model model
    ) throws CannotFindEntityException, InternalErrorException {

        ProductDto.Detail product = productService.findByUid(productUid);

        model.addAttribute("product", product);

        return "/product/detail";
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
