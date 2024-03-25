package jpabook.jpashop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class CartDto {

    @AllArgsConstructor
    @Getter
    public static class Add{
        private String productUid;
        private int quantity;
    }


}
