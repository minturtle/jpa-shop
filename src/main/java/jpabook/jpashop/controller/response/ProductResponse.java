package jpabook.jpashop.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class ItemResponse {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Preview {
        private Long itemId;
        private String itemName;
        private int price;
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
