package jpabook.jpashop.dto;

import jpabook.jpashop.enums.product.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public abstract class CategoryDto {

    @AllArgsConstructor
    @Builder
    @Data
    public static class Info{
        private String uid;
        private String name;
        private ProductType productType;
    }

}
