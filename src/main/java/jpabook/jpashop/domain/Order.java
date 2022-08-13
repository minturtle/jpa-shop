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

    public Order(Member member) {
        this.member = member;
        status = OrderStatus.ORDER;
        delivery = new Delivery(member.getAddress());
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
