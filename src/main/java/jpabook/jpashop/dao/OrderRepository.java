package jpabook.jpashop.dao;


import jpabook.jpashop.domain.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class OrderRepository extends EntityManagerRepository<Order> {
    public OrderRepository(EntityManager em) {
        super(em, Order.class);
    }
}
