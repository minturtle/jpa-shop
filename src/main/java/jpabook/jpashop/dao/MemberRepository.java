package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;


@Repository
public class MemberRepository extends EntityManagerRepository<Member> {

    public MemberRepository(EntityManager em){
        super(em, Member.class);
    }

    public Member findByUserId(String name)throws EntityNotFoundException{
        try{
            Member entity = em.createQuery(getSelectQlStringWhere("userId"), Member.class)
                    .setParameter("userId", name).getSingleResult();
            return entity;
        }catch (NoResultException e){
            throw new EntityNotFoundException();
        }
    }


}
