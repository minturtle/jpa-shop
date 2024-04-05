package jpabook.jpashop.controller.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public abstract class CartRequest {

    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    @Schema(name = "AddCartRequest")
    public static class Add{
        private String productUid;
        private int quantity;
    }
}
