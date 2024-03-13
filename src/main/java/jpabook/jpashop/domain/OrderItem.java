package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Getter
public class OrderItem {


    protected OrderItem() {
    }

    public OrderItem(Item item, int count) {
        this.item = item;
        this.count = count;
        this.orderPrice = item.getPrice() * count;
    }

    public void cancel(){
        this.item.addStock(count);
    }
    public void removeStock(int count){this.item.removeStock(count);}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return count == orderItem.count && orderPrice == orderItem.orderPrice && Objects.equals(id, orderItem.id) && Objects.equals(order, orderItem.order) && Objects.equals(item, orderItem.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, item, count, orderPrice);
    }
}
