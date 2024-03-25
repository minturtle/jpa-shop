package jpabook.jpashop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public abstract class OrderDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static class Preview{

    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Detail{

    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class ProductInfo{
        private String productUid;
        private int quantity;
    }

}
