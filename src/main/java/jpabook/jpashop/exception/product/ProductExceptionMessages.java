package jpabook.jpashop.exception.product;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductExceptionMessages {

    CANNOT_FIND_PRODUCT("상품 정보를 조회할 수 없습니다."),
    PRODUCT_TYPE_MAPPAING_FAILED("상품 정보 매핑에 실패했습니다."),
    NOT_ENOUGH_STOCK("주문한 수량이 남은 물건의 수량보다 많습니다.");
    private String message;

}
