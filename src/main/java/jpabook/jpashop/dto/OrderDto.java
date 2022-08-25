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
import java.util.Objects;


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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto orderDto = (OrderDto) o;
        return Objects.equals(id, orderDto.id) && Objects.equals(member, orderDto.member) && Objects.equals(orderedTime, orderDto.orderedTime) && status == orderDto.status && Objects.equals(orderItems, orderDto.orderItems) && Objects.equals(delivery, orderDto.delivery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member, orderedTime, status, orderItems, delivery);
    }
}
