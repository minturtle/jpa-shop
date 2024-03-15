package jpabook.jpashop.repository;

import jpabook.jpashop.domain.user.User;

import jakarta.persistence.EntityNotFoundException;
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


    @Query("select u from UsernamePasswordUser u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}
