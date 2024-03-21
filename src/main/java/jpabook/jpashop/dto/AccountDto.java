package jpabook.jpashop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
    public static class WithdrawDeposit {
        private String accountUid;
        private long amount;
    }


    @AllArgsConstructor
    @Builder
    @Data
    public static class Transfer {
        private String fromAccountUid;
        private String toAccountUid;
        private long amount;

    }
}
