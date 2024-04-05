package jpabook.jpashop.domain.order;

import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@SuperBuilder
@NoArgsConstructor
public class Order extends BaseEntity {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;


    @Column(name = "order_uid", unique = true, nullable = false, updatable = false)
    private String uid;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private AddressInfo deliveryInfo;

    @Embedded
    private Payment payment;


    @Enumerated(value = EnumType.STRING)
    @Column(name="order_status")
    @Builder.Default
    private OrderStatus status = OrderStatus.ORDERED;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderProduct> orderProducts = new ArrayList<>();


    public void addOrderProduct(OrderProduct orderProduct){
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void cancel(){
        this.status = OrderStatus.CANCELED;
        orderProducts.forEach(OrderProduct::cancel);
    }

}
