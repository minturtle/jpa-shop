package jpabook.jpashop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public abstract class AccountDto {



    @AllArgsConstructor
    @Builder
    @Data
    public static class Create{
        private String uid;
    }

}
