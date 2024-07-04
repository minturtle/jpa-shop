package jpabook.jpashop.exception.product;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductExceptionMessages {

    CANNOT_FIND_PRODUCT("상품 정보를 조회할 수 없습니다."),
    ENTITY_PRODUCT_MAPPING_FAILED("연결된 상품 정보를 조회할 수 없습니다."),
    PRODUCT_TYPE_MAPPAING_FAILED("상품 정보 매핑에 실패했습니다."),
    NOT_ENOUGH_STOCK("주문한 수량이 남은 물건의 수량보다 많습니다."),
    PRICE_RANGE_INVALID("최소 가격이 최대 가격보다 높습니다."),
    SORT_TYPE_INVALID("정렬 타입이 올바르지 않습니다.");
    private String message;

}
