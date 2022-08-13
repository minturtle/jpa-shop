package jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Inheritance
public abstract class Item {
    protected Item(){}

    public Item(int stockQuantity, int price, String name) {
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.name = name;
    }

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private int stockQuantity;
    private int price;
    private String name;

}
