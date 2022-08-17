package jpabook.jpashop.service;


import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
@RequiredArgsConstructor
public class OrderItemFactory {
    private final ItemRepository itemRepository;

    public OrderItem CreateOrderItem(Long itemId, int count) throws IllegalArgumentException, EntityNotFoundException {
        Item item = itemRepository.findById(itemId); //throwable EntityNotFoundException
        item.removeStock(count); // 이 때 주문하는 양(count)가 남은 재고 보다 많으면 예외 발생.
        OrderItem orderItem = new OrderItem(item, count, count * item.getPrice());
        return orderItem;

    }

}
