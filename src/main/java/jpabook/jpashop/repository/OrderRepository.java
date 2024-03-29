package jpabook.jpashop.repository;

import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.domain.order.Order;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
}
