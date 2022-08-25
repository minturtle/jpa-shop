package jpabook.jpashop.service;

import jpabook.jpashop.dao.em.EntityManagerItemRepository;
import jpabook.jpashop.dao.em.EntityManagerMemberRepository;
import jpabook.jpashop.dao.em.EntityManagerOrderRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.OrderItemListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final EntityManagerOrderRepository orderRepository;
    private final EntityManagerItemRepository itemRepository;
    private final EntityManagerMemberRepository memberRepository;

    public OrderDto order(Long userId, OrderItemListDto orderItemDtos){
        Member member = memberRepository.findById(userId);

        List<OrderItem> orderItems = createOrderItemList(orderItemDtos);
        removeStockInList(orderItems);

        Order order = new Order(member, orderItems);

        orderRepository.save(order);

        OrderDto orderDto = createOrderDto(order);

        return orderDto;
    }


    public void cancel(Long orderId) throws EntityNotFoundException{
        Order order = orderRepository.findById(orderId);
        order.cancel();
    }

    public OrderDto findById(Long orderId){
        Order order = orderRepository.findById(orderId);
        return createOrderDto(order);
    }

    public List<OrderDto> findByUser(Long memberId){
        Member member = memberRepository.findById(memberId);

        List<Order> ordersByMember = orderRepository.findByMember(member);

        return ordersByMember.stream().map(this::createOrderDto).collect(Collectors.toList());
    }


    private List<OrderItem> createOrderItemList(OrderItemListDto orderItemDtos) {
        List<OrderItem> orderItems = new ArrayList<>(orderItemDtos.size());
        Iterator<OrderItemListDto.OrderItemDto> iterator = orderItemDtos.iterator();

        while(iterator.hasNext()){
            OrderItem orderItem = dtoToOrderItem(iterator.next());
            orderItems.add(orderItem);
        }
        return orderItems;
    }
    private OrderItem dtoToOrderItem(OrderItemListDto.OrderItemDto orderItemDto) throws EntityNotFoundException, IllegalArgumentException{
        Item findItem = itemRepository.findById(orderItemDto.getItemId());//throwable EntityNotFound;
        return new OrderItem(findItem, orderItemDto.getCount());
    }

    private void removeStockInList(List<OrderItem> orderItems){
        orderItems.forEach(oi->oi.removeStock(oi.getCount()));
    }

    private OrderDto createOrderDto(Order order) {
        OrderDto orderDto = new OrderDto(order.getId(), order.getMember(), order.getOrderedTime(), order.getStatus()
                , order.getOrderItems(), order.getDelivery());
        return orderDto;
    }
}
