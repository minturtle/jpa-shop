package jpabook.jpashop.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class CartResponse {



    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(name = "CartInfo", description = "장바구니 정보")
    public static class Info{
        private String productUid;
        private int quantity;

    }

}
