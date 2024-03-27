package jpabook.jpashop.repository;

import jpabook.jpashop.domain.order.Order;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findByUid(String uid);

    @Query("select o from Order o join fetch o.orderProducts op join fetch o.payment.account  join fetch op.product where o.uid = :uid")
    Optional<Order> findByUidWithJoinProductAccount(@Param("uid") String uid);
}
