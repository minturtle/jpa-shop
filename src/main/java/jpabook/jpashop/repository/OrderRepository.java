package jpabook.jpashop.repository;

import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.domain.order.Order;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

public interface OrderRepository {

    List<Order> findByMember(User user);
    void save(Order order);

    Order findById(Long id) throws EntityNotFoundException;
    List<Order> findAll();

}
