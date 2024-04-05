package jpabook.jpashop.controller.common.response;


import io.swagger.v3.oas.annotations.media.Schema;
import jpabook.jpashop.domain.order.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public abstract class OrderResponse {


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Preview{
        private String orderId;
        private String name;

        private int totalPrice;
    }

    @Data
    @AllArgsConstructor
    @Schema(name = "OrderInfo")
    @NoArgsConstructor
    public static class Detail{
        private String orderUid;

        private List<OrderedProductDetail> orderProducts;

        private OrderPaymentDetail orderPaymentDetail;

        private LocalDateTime orderTime;
        private OrderStatus orderStatus;

    }

    @Data
    @NoArgsConstructor
    public static class OrderedProductDetail{
        private String productUid;
        private String productName;
        private String productImageUrl;
        private int unitPrice; //물건당 가격
        private int quantity;
        private int totalPrice;
    }

    @Data
    @NoArgsConstructor
    public static class OrderPaymentDetail {
        private String accountUid;
        private int totalPrice;
    }



    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderedItemInfo {
        private String itemId;
        private String itemName;
        private int itemPrice;
        private int orderQuantity;
        private int totalPrice;
    }

}
