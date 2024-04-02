package jpabook.jpashop.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public abstract class UserAccountRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Schema(name = "CreateAccountForm")
    public static class Create {
        private String accountName;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Schema(name = "CashFlowForm")
    public static class CashFlowRequest {
        private String accountUid;
        private Integer amount;
    }
}
