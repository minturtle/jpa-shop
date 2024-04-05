package jpabook.jpashop.exception.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CartExceptionMessages {

    CART_NOT_FOUND("장바구니를 찾을 수 없습니다.");

    private final String message;
}
