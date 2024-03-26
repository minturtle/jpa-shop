package jpabook.jpashop.domain.order;

import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import lombok.Getter;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
public class Order extends BaseEntity {


    public void cancel(){
        this.status = OrderStatus.CANCELED;
        orderProducts.forEach(OrderProduct::cancel);
    }

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
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();


    public void addOrderItem(OrderProduct orderProduct){
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

}
