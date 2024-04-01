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
        private String name;
        private Long balance;
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class CashFlowRequest {
        private String accountUid;
        private int amount;
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class CashFlowResult {
        private String accountUid;
        private int amount;
        private LocalDateTime createdAt;
        private CashFlowType type;
        private CashFlowStatus status;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class Info {
        private String accountUid;
        private String accountName;
        private Long balance;

    }
}
