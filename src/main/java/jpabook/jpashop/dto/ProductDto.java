package jpabook.jpashop.dto;


import jakarta.validation.constraints.NotNull;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

public abstract class ProductDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static class SearchCondition{
        @NotNull
        private Optional<String> name;

        @NotNull
        private Optional<PriceRange> priceRange;

        @NotNull
        private Optional<String> categoryUid;

        @NotNull
        private int page;

        @NotNull
        private int size;

        @NotNull
        private SortOption sortOption;

        @NotNull
        private ProductType productType;
    }


    @Data
    @AllArgsConstructor
    @Builder
    public static class Detail{

    }


    @Data
    @AllArgsConstructor
    @Builder
    public static class Preview{
        private String uid;
        private String name;
        private int price;
        private String thumbnailUrl;
    }


    @Data
    @AllArgsConstructor
    @Builder
    public static class PriceRange{
        private int minPrice;
        private int maxPrice;
    }
}
