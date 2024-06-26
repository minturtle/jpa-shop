package jpabook.jpashop.repository;

import jpabook.jpashop.domain.order.Order;

import jpabook.jpashop.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findByUid(String uid);

    @Query("select o from Order o join fetch o.orderProducts op join fetch op.product join fetch o.payment.account where o.uid = :uid")
    Optional<Order> findByUidWithJoinProductAccount(@Param("uid") String uid);

    @Query("select o from Order o join o.user u where u.uid = :userUid")
    List<Order> findByUser(@Param("userUid") String userUid, Pageable pageable);

    @Query("select o from Order o join o.user u where u.uid = :userUid")
    List<Order> findByUser(@Param("userUid") String userUid);

    @Query("select count(o) from Order o join o.user u where u.uid = :userUid")
    Integer countByUser(@Param("userUid") String userUid);

}
