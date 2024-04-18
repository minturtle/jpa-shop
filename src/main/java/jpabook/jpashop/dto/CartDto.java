package jpabook.jpashop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public abstract class CartDto {

    @AllArgsConstructor
    @Getter
    public static class Add{
        private String productUid;
        private int quantity;
    }



    @AllArgsConstructor
    @Getter
    public static class Detail {

        private String productUid;
        private String productName;
        private String productImageUrl;
        private int price;
        private int quantity;
    }

    @AllArgsConstructor
    @Getter
    public static class Update {
        private String productUid;
        private int addCount;
    }
}
