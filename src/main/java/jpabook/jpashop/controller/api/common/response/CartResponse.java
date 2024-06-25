package jpabook.jpashop.controller.api.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public abstract class CartResponse {



    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    @Schema(name = "CartInfo", description = "장바구니 정보")
    public static class Info{
        private String productUid;
        private String productName;
        private String productImageUrl;
        private int price;
        private int quantity;
    }

}
