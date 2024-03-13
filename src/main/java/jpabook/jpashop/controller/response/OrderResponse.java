package jpabook.jpashop.controller.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Detail{
        private String orderId;
        private String name;

        private List<OrderedItemInfo> items;
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
