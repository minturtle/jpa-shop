package jpabook.jpashop.service;

import jpabook.jpashop.dao.em.EntityManagerItemRepository;
import jpabook.jpashop.dao.em.EntityManagerMemberRepository;
import jpabook.jpashop.dao.em.OrderRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.OrderDto;
import jpabook.jpashop.dto.OrderItemListDto;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private  OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EntityManagerItemRepository itemRepository;

    @Mock
    private EntityManagerMemberRepository memberRepository;

    private Member member;
    private OrderItemListDto orderItemListDto;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        member = Member.createMember("김민석", "root11", "1111", "경북 구미시", "대학로 61","금오공과 대학교",false);
        item1 = new Book("어린 왕자", 15000, 30, "김민석", "11234");
        item2 = new Album("김민석 정규 앨범 7집", 50000, 10, "김민석", "김민석 데뷔 20주년 기념");
        orderItemListDto = new OrderItemListDto();
    }

    //아이템 정보 수량 => orderItem 객체 생성 => order에 집어넣음
    @Test
    @DisplayName("orderService 객체 생성")
    void t1() throws Exception {
        //given
        //when
        //then
        assertThat(orderService).isNotNull();
    }

    @Test
    @DisplayName("주문 하기")
    void t2() throws Exception {
        //given
        given(itemRepository.findById(item1.getId())).willReturn(item1);
        given(memberRepository.findById(member.getId())).willReturn(member);

        orderItemListDto.setItems(List.of(new OrderItemListDto.OrderItemDto(item1.getId(), 5))); //5개의 item1 주문
        //when
        final OrderDto orderDto = orderService.order(member.getId(), orderItemListDto);
        //then
        assertThat(orderDto.getMember()).isEqualTo(member);
        assertThat(orderDto.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(item1.getStockQuantity()).isEqualTo(25);
    }

    @Test
    @DisplayName("주문하기, 상품 재고보다 많은 주문")
    void t3() throws Exception {
        //given
        given(itemRepository.findById(item1.getId())).willReturn(item1);
        given(memberRepository.findById(member.getId())).willReturn(member);
        orderItemListDto.setItems(List.of(new OrderItemListDto.OrderItemDto(item1.getId(), 500)));
        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->{
            orderService.order(member.getId(), orderItemListDto);
        };
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문한 수량이 남은 물건의 수량보다 많습니다.");
    }

    @Test
    @DisplayName("주문 취소하기")
    void t4() throws Exception {
        //given

        given(itemRepository.findById(item1.getId())).willReturn(item1);
        given(memberRepository.findById(member.getId())).willReturn(member);

        Order order = new Order(member, getOrderItems(5, item1));
        orderItemListDto.setItems(List.of(new OrderItemListDto.OrderItemDto(item1.getId(), 5)));
        OrderDto orderDto = orderService.order(member.getId(), orderItemListDto);
        given(orderRepository.findById(orderDto.getId())).willReturn(order);

        //when
        orderService.cancel(orderDto.getId());

        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item1.getStockQuantity()).isEqualTo(30);
    }

    @Test
    @DisplayName("주문 id로 조회하기")
    void t5() throws Exception {
        //given
        Order order = new Order(member, getOrderItems(5, item1));
        given(orderRepository.findById(order.getId())).willReturn(order);
        //when
        OrderDto findOrderDto = orderService.findById(order.getId());
        //then
        assertThat(findOrderDto.getMember()).isEqualTo(member);
        assertThat(findOrderDto.getOrderItems()).contains(new OrderItem(item1, 5));
    }

    @Test
    @DisplayName("주문 유저로 조회하기")
    void t6() throws Exception {
        //given
        Order order1 = new Order(member, getOrderItems(5, item1));
        Order order2 = new Order(member, getOrderItems(4, item1, item2));

        given(memberRepository.findById(member.getId())).willReturn(member);
        given(orderRepository.findByMember(member)).willReturn(List.of(order1, order2));
        //when
        final List<OrderDto> ordersByUser = orderService.findByUser(member.getId());
        //then
        assertThat(ordersByUser.size()).isEqualTo(2);
    }


    private List<OrderItem> getOrderItems(int count, Item ... items) {
        return Arrays.stream(items).map(item -> new OrderItem(item, count)).collect(Collectors.toList());

    }



}