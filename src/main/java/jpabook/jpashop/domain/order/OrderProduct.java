package jpabook.jpashop.domain.order;

import jpabook.jpashop.domain.product.Product;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_products")
@NoArgsConstructor
public class OrderProduct {

    public OrderProduct(Product product, int count, int itemPrice) {
        this.product = product;
        this.count = count;
        this.itemPrice = itemPrice;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Product product;

    private int count;
    private int itemPrice;

    public int calculateTotalItemPrice(){
        return count * itemPrice;
    }


    public void setOrder(Order order) {
        this.order = order;
    }


    public void cancel(){
        this.product.addStock(count);
    }
}
