package jpabook.jpashop.dto;

import jpabook.jpashop.enums.user.account.CashFlowStatus;
import jpabook.jpashop.enums.user.account.CashFlowType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public abstract class AccountDto {



    @AllArgsConstructor
    @Builder
    @Data
    public static class Create{
        private String userUid;
        private Long balance;
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class CashFlowRequest {
        private String accountUid;
        private long amount;
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class CashFlowResult {
        private String accountUid;
        private long amount;
        private LocalDateTime createdAt;
        private CashFlowType type;
        private CashFlowStatus status;
    }
}
