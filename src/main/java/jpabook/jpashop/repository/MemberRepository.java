package jpabook.jpashop.repository;

import jpabook.jpashop.domain.user.User;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface MemberRepository {

    void save(User user);

    User findByName(String name)throws EntityNotFoundException;

    User findByUserId(String userId) throws EntityNotFoundException;

    User findById(Long id) throws EntityNotFoundException;

    List<User> findAll();

}
