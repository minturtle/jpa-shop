package jpabook.jpashop.domain;

import jdk.jfr.Timestamp;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@Getter
public class Order {

    protected Order(){}

    public Order(Member member, List<OrderItem> orderItems) {
        this.member = member;
        this.orderItems.addAll(orderItems);
        this.status = OrderStatus.ORDER;
        this.delivery = new Delivery(member.getAddress());
        this.orderedTime = LocalDateTime.now();
    }

    public void cancel(){
        this.status = OrderStatus.CANCEL;
        orderItems.forEach(OrderItem::cancel);
    }

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Timestamp
    private LocalDateTime orderedTime;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;
}
