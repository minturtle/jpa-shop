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
import jpabook.jpashop.dto.OrderPreviewDto;
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

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public OrderDto order(Long userId, OrderItemListDto orderItemDtos){
        Member member = memberRepository.findById(userId);

        List<OrderItem> orderItems = createOrderItemList(orderItemDtos);
        removeStockInList(orderItems);

        Order order = new Order(member, orderItems);

        orderRepository.save(order);

        OrderDto orderDto = createOrderDto(order);

        return orderDto;
    }


    /*
    * 주문을 취소함
    *
    * @param : Order Entity의 id값
    * */
    public void cancel(Long orderId) throws EntityNotFoundException{
        Order order = orderRepository.findById(orderId);
        order.cancel();
    }

    public OrderDto findById(Long orderId){
        Order order = orderRepository.findById(orderId);
        return createOrderDto(order);
    }


    /*
    * 유저의 주문 내역 리스트를 불러옴
    *
    * @param : Member Entity의 Id값
    * @return : 유저의 Order 리스트
    * */
    public List<OrderDto> findByUser(Long memberId) throws EntityNotFoundException{
        Member member = memberRepository.findById(memberId);

        List<Order> ordersByMember = orderRepository.findByMember(member);

        return ordersByMember.stream().map(this::createOrderDto).collect(Collectors.toList());
    }


    public OrderPreviewDto createOrderPreviewDto(OrderDto orderDto){
        StringBuilder sb = new StringBuilder(orderDto.getOrderItems().get(0).getItemName());
        if(orderDto.getOrderItems().size() != 1){
            sb.append(" 외 ")
                .append(orderDto.getOrderItems().size() - 1)
                .append("건");
        }

        return new OrderPreviewDto(orderDto.getId(),sb.toString(), getTotalPrice(orderDto));
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
                , createOrderItemDtoList(order.getOrderItems()), order.getDelivery());
        return orderDto;
    }

    private List<OrderItemListDto.OrderItemDto> createOrderItemDtoList(List<OrderItem> orderItems){
        return orderItems.stream()
                .map(oi->new OrderItemListDto.OrderItemDto(oi.getItem().getId(), oi.getItem().getName(),
                        oi.getItem().getPrice(), oi.getCount())).collect(Collectors.toList());
    }

    private int getTotalPrice(OrderDto orderDto) {
        return orderDto.getOrderItems().stream().mapToInt(oi->{return oi.getCount() * oi.getUnitPrice();}).sum();
    }

}
