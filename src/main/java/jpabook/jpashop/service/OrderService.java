package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.dao.OrderRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.OrderItemListDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public OrderDto order(String userId, OrderItemListDto orderItemDtos){
        Member member = memberRepository.findByUserId(userId);

        List<OrderItem> orderItems = createOrderItemList(orderItemDtos);
        removeStockInList(orderItems);

        Order order = new Order(member, orderItems);

        orderRepository.save(order);

        OrderDto orderDto = createOrderDto(order);

        return orderDto;
    }

    @NotNull
    private OrderDto createOrderDto(Order order) {
        OrderDto orderDto = new OrderDto(order.getId(), order.getMember(), order.getOrderedTime(), order.getStatus()
                , order.getOrderItems(), order.getDelivery());
        return orderDto;
    }

    public void cancel(Long orderId) throws EntityNotFoundException{
        Order order = orderRepository.findById(orderId);
        order.cancel();
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


}
