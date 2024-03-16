package jpabook.jpashop.repository;

import jakarta.persistence.LockModeType;
import jpabook.jpashop.domain.user.User;

import jakarta.persistence.EntityNotFoundException;
import jpabook.jpashop.domain.user.UsernamePasswordUser;
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

    List<User> findAll();
    @Query("select u from UsernamePasswordUser u where u.username = :username")
    Optional<UsernamePasswordUser> findByUsername(@Param("username") String username);


    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select u from UsernamePasswordUser u where u.username = :username")
    Optional<UsernamePasswordUser> findByUsernameWithLock(@Param("username") String username);


}
