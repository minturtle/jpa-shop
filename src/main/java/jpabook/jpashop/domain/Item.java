package jpabook.jpashop.domain;

import jpabook.jpashop.dto.ItemDto;
import lombok.Getter;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Table(name = "items")
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_uid", nullable = false, updatable = false, unique = true)
    private String uid;

    private String name;

    private int price;

    private int stockQuantity;

    private String thumbnailImageUrl;


    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity)throws IllegalArgumentException{
        checkIsOrderQuantityBiggerThanStock(quantity); //주문양이 재고보다 많으면 예외발생
        this.stockQuantity -= quantity;
    }

    private void checkIsOrderQuantityBiggerThanStock(int quantity) {
        if(stockQuantity < quantity) throw new IllegalArgumentException("주문한 수량이 남은 물건의 수량보다 많습니다.");
    }
}
