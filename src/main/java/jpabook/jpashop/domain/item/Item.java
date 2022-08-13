package jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Inheritance
public abstract class Item {


    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private int stockQuantity;
    private int price;
    private String name;

}
