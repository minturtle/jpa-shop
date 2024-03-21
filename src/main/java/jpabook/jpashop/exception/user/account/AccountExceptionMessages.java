package jpabook.jpashop.exception.user.account;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountExceptionMessages {

    CANNOT_FIND_ACCOUNT("게좌 정보를 조회할 수 없습니다."),
    NEGATIVE_ACCOUNT_BALANCE("잔고가 0원보다 작을 수 없습니다"),
    BALANCE_OVERFLOW("계좌의 잔고가 최고 값을 넘었습니다.");
    private String message;
}
