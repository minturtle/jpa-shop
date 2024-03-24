package jpabook.jpashop.dto;


import jakarta.validation.constraints.NotNull;
import jpabook.jpashop.enums.product.ProductType;
import jpabook.jpashop.enums.product.SortOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
        private SortOption sortOption;

        @NotNull
        private ProductType productType;
    }


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static abstract class Detail{
        private String uid;
        private String name;
        private int price;
        private String thumbnailUrl;
        private String description;
        private int stockQuantity;
    }


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieDetail extends Detail{
        private String director;
        private String actor;

    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlbumDetail extends Detail{
        private String artist;
        private String etc;
    }



    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
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
