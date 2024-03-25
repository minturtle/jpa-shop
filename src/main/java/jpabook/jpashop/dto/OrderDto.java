package jpabook.jpashop.dto;


import jpabook.jpashop.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
        private String orderUid;

        private List<OrderedProductDetail> orderProducts;

        private int totalPrice;
        private LocalDateTime orderTime;
        private OrderStatus orderStatus;

    }


    @Data
    @AllArgsConstructor
    @Builder
    public static class OrderedProductDetail{
        private String productUid;
        private String productName;
        private String productImageUrl;
        private int unitPrice; //물건당 가격
        private int quantity;
        private int totalPrice;
    }



    @Data
    @AllArgsConstructor
    @Builder
    public static class OrderProductRequestInfo {
        private String productUid;
        private int quantity;
    }



}
