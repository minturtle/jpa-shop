package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    private Member member;
    private Order order;


    @BeforeEach
    void setUp(){
        member = new Member("김민석", "경북 구미시", "대학로 61","금오공과 대학교");
        order = new Order(member);
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
        //when
        orderRepository.save(order);
        Order findOrder = orderRepository.findById(this.order.getId()).orElseThrow(RuntimeException::new);
        //then
        assertThat(findOrder).isEqualTo(order);
        assertThat(findOrder.getMember()).isEqualTo(member);
        assertThat(findOrder.getDelivery().getAddress()).isEqualTo(member.getAddress());
    }

}