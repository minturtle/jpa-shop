package jpabook.jpashop.controller.api.common.request;


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


    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    @Schema(name = "UpdateCartRequest")
    public static class Update{
        private String productUid;
        private int addCount;
    }

}
