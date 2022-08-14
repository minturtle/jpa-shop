package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class MemberRepository{

    private final EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    public Optional<Member> findByName(String name){
        final Member member = em.createQuery("select m from Member m where m.name =:name", Member.class)
                .setParameter("name", name).getSingleResult();
        return Optional.of(member);
    }

    public Optional<Member> findById(Long id){
        final Member member = em.find(Member.class, id);
        return Optional.of(member);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
