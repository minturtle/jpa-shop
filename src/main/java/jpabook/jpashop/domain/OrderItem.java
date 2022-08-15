package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class OrderItem {


    protected OrderItem() {
    }

    public OrderItem(Item item, int count, int orderPrice) {
        this.item = item;
        this.count = count;
        this.orderPrice = orderPrice;
    }

    public void cancel(){
        this.item.addStock(count);
    }

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;
    private int orderPrice;


}
