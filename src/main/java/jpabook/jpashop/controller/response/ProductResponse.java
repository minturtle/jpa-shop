package jpabook.jpashop.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class ProductResponse {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(name = "ProductResponsePreview")
    public static class Preview {
        private String productUid;
        private String productName;
        private int price;
        private String productImage;
    }



    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {

        private Long itemId;
        private String itemName;
        private String description;
        private int price;

    }
}
