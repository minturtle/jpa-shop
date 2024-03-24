package jpabook.jpashop.exception.product;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductExceptionMessages {

    CANNOT_FIND_PRODUCT("상품 정보를 조회할 수 없습니다.");

    private String message;

}
