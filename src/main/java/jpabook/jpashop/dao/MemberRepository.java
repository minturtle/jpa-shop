package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.List;

public interface MemberRepository {

    void save(Member member);

    Member findByName(String name)throws EntityNotFoundException;

    Member findByUserId(String userId) throws EntityNotFoundException;

    Member findById(Long id) throws EntityNotFoundException;

    List<Member> findAll();

}
