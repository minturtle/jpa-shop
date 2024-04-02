package jpabook.jpashop.exception.user.account;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountExceptionMessages {

    CANNOT_FIND_ACCOUNT("게좌 정보를 조회할 수 없습니다."),
    NEGATIVE_ACCOUNT_BALANCE("잔고가 0원보다 작을 수 없습니다"),
    BALANCE_OVERFLOW("계좌의 잔고가 최고 값을 넘었습니다."),
    ENTITY_ACCOUNT_MAPPING_FAILED("계좌의 정보가 주문과 올바르게 설정되어 있지 않습니다."),
    UNAUTHORIZED_ACCESS("사용자에게 접근 권한이 없는 계좌입니다.") ;
    private String message;
}
