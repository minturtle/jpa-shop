package jpabook.jpashop.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public abstract class UserAccountResponse {


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Create{
        private String accountUid;
    }

}
