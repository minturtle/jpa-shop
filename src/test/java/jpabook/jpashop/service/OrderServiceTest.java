package jpabook.jpashop.service;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.dao.OrderRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private  OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    private OrderItemFactory orderItemFactory;

    private Member member;
    private List<OrderItem> orderItems = new ArrayList<>();
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        orderItemFactory = new OrderItemFactory(itemRepository);
        member = new Member("김민석", "경북 구미시", "대학로 61","금오공과 대학교");
        item1 = new Book("어린 왕자", 15000, 30, "김민석", "11234");
        item2 = new Album("김민석 정규 앨범 7집", 50000, 10, "김민석", "김민석 데뷔 20주년 기념");
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
        given(itemRepository.findById(item1.getId())).willReturn(Optional.ofNullable(item1));
        orderItems.add(orderItemFactory.CreateOrderItem(item1.getId(), 5));
        //when
        Order order = orderService.order(member, orderItems);
        //then
        assertThat(order.getMember()).isEqualTo(member);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(item1.getStockQuantity()).isEqualTo(25);
    }

    @Test
    @DisplayName("주문하기, 상품 재고보다 많은 주문")
    void t3() throws Exception {
        //given
        given(itemRepository.findById(item1.getId())).willReturn(Optional.ofNullable(item1));
        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->{
            orderItems.add(orderItemFactory.CreateOrderItem(item1.getId(), 50));
        };
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문한 수량이 남은 물건의 수량보다 많습니다.");
    }

    @Test
    @DisplayName("주문 취소하기")
    void t4() throws Exception {
        //given
        given(itemRepository.findById(item1.getId())).willReturn(Optional.ofNullable(item1));
        orderItems.add(orderItemFactory.CreateOrderItem(item1.getId(), 5));
        Order order = orderService.order(member, orderItems);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        //when
        orderService.cancel(order.getId());
        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }
}