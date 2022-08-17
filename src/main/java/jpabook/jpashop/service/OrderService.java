package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.dao.OrderRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.OrderItemListDto;
import lombok.RequiredArgsConstructor;
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


    public Order order(Member member, OrderItemListDto orderItemDtos){

        List<OrderItem> orderItems = new ArrayList<>(orderItemDtos.size());
        Iterator<OrderItemListDto.OrderItemDto> iterator = orderItemDtos.iterator();

        while(iterator.hasNext()){
            orderItems.add(createOrderItem(getNextDto(iterator)));
        }

        Order order = new Order(member, orderItems);
        orderRepository.save(order);
        return order;
    }


    public Order cancel(Long orderId){
        Order order = orderRepository.findById(orderId);
        order.cancel();

        return order;
    }

    private OrderItem createOrderItem(OrderItemListDto.OrderItemDto orderItemDto) throws EntityNotFoundException, IllegalArgumentException{
        Item findItem = itemRepository.findById(orderItemDto.getItemId());//throwable EntityNotFound;
        findItem.removeStock(orderItemDto.getCount()); //throwable IllegalArgument;
        return new OrderItem(findItem, orderItemDto.getCount());
    }

    private OrderItemListDto.OrderItemDto getNextDto(Iterator<OrderItemListDto.OrderItemDto> iterator) {
        OrderItemListDto.OrderItemDto orderItemDto = iterator.next();
        return orderItemDto;
    }
}
