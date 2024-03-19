package jpabook.jpashop.exception.user.account;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountExceptionMessages {

    NEGATIVE_ACCOUNT_BALANCE("잔고가 0원보다 작을 수 없습니다"),
    CANNOT_FIND_ACCOUNT("게좌 정보를 조회할 수 없습니다.");
    private String message;
}
