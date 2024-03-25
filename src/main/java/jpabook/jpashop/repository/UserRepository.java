package jpabook.jpashop.repository;

import jakarta.persistence.LockModeType;
import jpabook.jpashop.domain.user.User;

import jpabook.jpashop.domain.user.UsernamePasswordAuthInfo;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUid(String uid);


    @Query("select u from User u join fetch u.accountList where u.uid = :uid")
    Optional<User> findByUidJoinAccount(@Param("uid") String uid);


    @Query("select u from User u join fetch u.cartList c join fetch c.product where u.uid = :uid")
    Optional<User> findByUidJoinCartProduct(@Param("uid") String uid);

    List<User> findAll();
    @Query("select u from User u where u.usernamePasswordAuthInfo.username = :username")
    Optional<User> findByUsername(@Param("username") String username);



    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select u from User u where u.usernamePasswordAuthInfo.username = :username")
    Optional<User> findByUsernameWithLock(@Param("username") String username);

}
