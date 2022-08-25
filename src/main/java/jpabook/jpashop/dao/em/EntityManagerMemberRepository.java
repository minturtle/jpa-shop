package jpabook.jpashop.dao.em;

import jpabook.jpashop.dao.em.EntityManagerRepository;
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;


public class EntityManagerMemberRepository extends EntityManagerRepository<Member> implements jpabook.jpashop.dao.MemberRepository {

    public EntityManagerMemberRepository(EntityManager em){
        super(em, Member.class);
    }

    @Override
    public Member findByUserId(String userId)throws EntityNotFoundException{
        try{
            Member entity = em.createQuery(getSelectQlStringWhere("userId"), Member.class)
                    .setParameter("userId", userId).getSingleResult();
            return entity;
        }catch (NoResultException e){
            throw new EntityNotFoundException();
        }
    }


}
