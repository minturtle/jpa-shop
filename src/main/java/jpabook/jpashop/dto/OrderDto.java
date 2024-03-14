package jpabook.jpashop.dto;

import jpabook.jpashop.domain.user.User;

import jpabook.jpashop.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter @Setter
public class OrderDto {


    public OrderDto() {
    }

    public OrderDto(Long id, User user, LocalDateTime orderedTime, OrderStatus status, List<OrderItemListDto.OrderItemDto> orderItems, Delivery delivery) {
        this.id = id;
        this.user = user;
        this.orderedTime = orderedTime;
        this.status = status;
        this.orderItems = orderItems;
        this.delivery = delivery;
    }

    private Long id;

    private User user;

    private LocalDateTime orderedTime;

    private OrderStatus status;

    private List<OrderItemListDto.OrderItemDto> orderItems = new ArrayList<>();

    private Delivery delivery;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto orderDto = (OrderDto) o;
        return Objects.equals(id, orderDto.id) && Objects.equals(user, orderDto.user) && Objects.equals(orderedTime, orderDto.orderedTime) && status == orderDto.status && Objects.equals(orderItems, orderDto.orderItems) && Objects.equals(delivery, orderDto.delivery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, orderedTime, status, orderItems, delivery);
    }



    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderPreviewDto {

        private Long orderId;

        private String title;

        private int totalPrice;

    }
}
