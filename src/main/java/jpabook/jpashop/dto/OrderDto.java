package jpabook.jpashop.dto;


import jpabook.jpashop.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class OrderDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static class Preview{
        private String orderUid;
        private String name;
        private int totalPrice;
        private LocalDateTime orderTime;
        private OrderStatus orderStatus;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Detail{
        private String orderUid;

        @Builder.Default
        private List<OrderedProductDetail> orderProducts = new ArrayList<>();

        private OrderPaymentDetail orderPaymentDetail;

        private LocalDateTime orderTime;
        private OrderStatus orderStatus;

        public void addOrderProduct(OrderedProductDetail orderedProductDetail){
            orderProducts.add(orderedProductDetail);
        }

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
    public static class OrderPaymentDetail {
        private String accountUid;
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
