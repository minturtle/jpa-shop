package jpabook.jpashop.domain.order;

import jpabook.jpashop.domain.Item;
import lombok.Getter;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Table(name = "order_items")
public class OrderItem {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;
    private int itemPrice;

    public int calculateTotalItemPrice(){
        return count * itemPrice;
    }


    public void setOrder(Order order) {
        this.order = order;
    }


    public void cancel(){
        this.item.addStock(count);
    }
}
