package jpabook.jpashop.service;


import jpabook.jpashop.dao.OrderRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order order(Member member, List<OrderItem> orderItems){
        Order order = new Order(member, orderItems);
        orderRepository.save(order);
        return order;
    }

    public Order cancel(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() ->new IllegalStateException("주문 정보를 찾을 수 없습니다."));
        order.cancel();

        return order;
    }

}
