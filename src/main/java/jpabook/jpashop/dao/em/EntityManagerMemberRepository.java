package jpabook.jpashop.dao.em;

import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.domain.Member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;


public class EntityManagerMemberRepository extends EntityManagerRepository<Member> implements MemberRepository {

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
