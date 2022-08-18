package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    private Member member;
    private Order order;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp(){
        member =  Member.createMember("김민석", "root11", "1111", "경북 구미시", "대학로 61","금오공과 대학교");
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

        given(itemRepository.findById(item1.getId())).willReturn(item1);
        item1.removeStock(5);
        order = new Order(member,List.of(new OrderItem(item1, 5)));

        //when
        orderRepository.save(order);
        Order findOrder = orderRepository.findById(this.order.getId());
        //then
        assertThat(findOrder).isEqualTo(order);
        assertThat(findOrder.getMember()).isEqualTo(member);
        assertThat(findOrder.getDelivery().getAddress()).isEqualTo(member.getAddress());
    }

}