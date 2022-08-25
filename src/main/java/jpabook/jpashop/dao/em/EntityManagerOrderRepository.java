package jpabook.jpashop.dao.em;


import jpabook.jpashop.dao.em.EntityManagerRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EntityManagerOrderRepository extends EntityManagerRepository<Order> {
    public EntityManagerOrderRepository(EntityManager em) {
        super(em, Order.class);
    }

    public List<Order> findByMember(Member member){
        return findAll().stream().filter(o->o.getMember().equals(member)).collect(Collectors.toList());
    }

    @Override
    protected String getSelectQlString() {
        return "select m from orders m";
    }
}
