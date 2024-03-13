package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

public interface OrderRepository {

    List<Order> findByMember(Member member);
    void save(Order order);

    Order findById(Long id) throws EntityNotFoundException;
    List<Order> findAll();

}
