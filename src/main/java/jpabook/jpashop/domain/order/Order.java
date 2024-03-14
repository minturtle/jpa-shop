package jpabook.jpashop.domain.order;

import jdk.jfr.Timestamp;
import jpabook.jpashop.domain.BaseEntity;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import lombok.Getter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
public class Order extends BaseEntity {


    public void cancel(){
        this.status = OrderStatus.CANCEL;
        orderItems.forEach(OrderItem::cancel);
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

    @Enumerated(value = EnumType.STRING)
    @Column(name="order_status")
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();


    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

}
