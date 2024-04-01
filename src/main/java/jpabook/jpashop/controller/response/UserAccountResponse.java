package jpabook.jpashop.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public abstract class UserAccountResponse {


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Schema(name = "AccountCreateResponse")
    public static class Create{
        private String accountUid;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Schema(name = "AccountInfo")
    public static class Info{
        private String accountUid;
        private String accountName;
        private String balance;
    }
}
