package jpabook.jpashop.controller.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jpabook.jpashop.enums.user.account.CashFlowStatus;
import jpabook.jpashop.enums.user.account.CashFlowType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
        private Long balance;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Schema(name = "CashflowResult")
    public static class CashflowResult{
        private String accountUid;
        private int amount;
        private LocalDateTime createdAt;
        private CashFlowType type;
        private CashFlowStatus status;
    }


}
