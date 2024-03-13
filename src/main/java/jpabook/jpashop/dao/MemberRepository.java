package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import java.util.List;

public interface MemberRepository {

    void save(Member member);

    Member findByName(String name)throws EntityNotFoundException;

    Member findByUserId(String userId) throws EntityNotFoundException;

    Member findById(Long id) throws EntityNotFoundException;

    List<Member> findAll();

}
