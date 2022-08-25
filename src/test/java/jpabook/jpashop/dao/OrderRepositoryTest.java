package jpabook.jpashop.dao;

import jpabook.jpashop.dao.em.EntityManagerItemRepository;
import jpabook.jpashop.dao.em.EntityManagerOrderRepository;
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
import static org.mockito.Mockito.mock;


@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Mock
    private ItemRepository itemRepository;

    private Member member1;
    private Member member2;
    private Order order1;
    private Order order2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp(){
        member1 =  Member.createMember("김민석", "root11", "1111", "경북 구미시", "대학로 61","금오공과 대학교",false);
        member2 = Member.createMember("test", "aa", "bbcc", "a", "b", "c", false);
        item1 = new Book("어린 왕자", 15000, 30, "김민석", "11234");
        item2 = new Album("김민석 정규 앨범 7집", 50000, 10, "김민석", "김민석 데뷔 20주년 기념");


        order1 = new Order(member1,List.of(new OrderItem(item1, 5)));
        order2 = new Order(member1, List.of(new OrderItem(item1, 5), new OrderItem(item2, 3)));


        memberRepository.save(member1);
        memberRepository.save(member2);
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

        //when
        orderRepository.save(order1);
        Order findOrder = orderRepository.findById(this.order1.getId());
        //then
        assertThat(findOrder).isEqualTo(order1);
        assertThat(findOrder.getMember()).isEqualTo(member1);
        assertThat(findOrder.getDelivery().getAddress()).isEqualTo(member1.getAddress());
    }

    @Test
    @DisplayName("order 멤버로 조회")
    void t3() throws Exception {
        //given
        Order mockOrder = new Order(member2, List.of(new OrderItem(item1, 3)));

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(mockOrder);

        //when
        final List<Order> orders = orderRepository.findByMember(this.member1);
        //then
        assertThat(orders).contains(order1, order2).doesNotContain(mockOrder);
    }
}