package jpabook.jpashop.controller.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public abstract class OrderRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CreateOrderRequest")
    public static class Create{
        private String accountUid;
        private List<ProductOrderInfo> products;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CreateOrderRequest")
    public static class ProductOrderInfo {
        private String productUid;
        private int quantity;
    }

}
