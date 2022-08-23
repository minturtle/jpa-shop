package jpabook.jpashop.dto;

import jdk.jfr.Timestamp;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter @Setter
public class OrderDto {


    public OrderDto() {
    }

    public OrderDto(Long id, Member member, LocalDateTime orderedTime, OrderStatus status, List<OrderItem> orderItems, Delivery delivery) {
        this.id = id;
        this.member = member;
        this.orderedTime = orderedTime;
        this.status = status;
        this.orderItems = orderItems;
        this.delivery = delivery;
    }

    private Long id;

    private Member member;

    private LocalDateTime orderedTime;

    private OrderStatus status;

    private List<OrderItem> orderItems = new ArrayList<>();

    private Delivery delivery;


}
