package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.OrderItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    private OrderItemFactory orderItemFactory;


    private Member member;
    private Order order;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp(){
        orderItemFactory = new OrderItemFactory(itemRepository);

        member = new Member("김민석", "경북 구미시", "대학로 61","금오공과 대학교");
        item1 = new Book("어린 왕자", 15000, 30, "김민석", "11234");
        item2 = new Album("김민석 정규 앨범 7집", 50000, 10, "김민석", "김민석 데뷔 20주년 기념");
    }

    @Test
    @DisplayName("orderRepository 객체 생성")
    void t1() throws Exception {
        //given
        //when
        //then
        assertThat(orderRepository).isNotNull();
    }

    @Test
    @DisplayName("Order 객체 저장")
    void t2() throws Exception {
        //given

        given(itemRepository.findById(item1.getId())).willReturn(Optional.ofNullable(item1));

        order = new Order(member,List.of(orderItemFactory.CreateOrderItem(item1.getId(), 5)));

        //when
        orderRepository.save(order);
        Order findOrder = orderRepository.findById(this.order.getId()).orElseThrow(RuntimeException::new);
        //then
        assertThat(findOrder).isEqualTo(order);
        assertThat(findOrder.getMember()).isEqualTo(member);
        assertThat(findOrder.getDelivery().getAddress()).isEqualTo(member.getAddress());
    }

}