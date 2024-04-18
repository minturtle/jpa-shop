package jpabook.jpashop.exception.order;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderExceptionMessage {

    CANNOT_FIND_ORDER("주문 정보를 조회할 수 없습니다.");

    private String message;

}
