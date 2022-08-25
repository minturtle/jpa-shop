package jpabook.jpashop.dao.jpa;


import jpabook.jpashop.dao.OrderRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {
    private final InnerJpaOrderRepository repository;

    @Override
    public List<Order> findByMember(Member member) {
        return repository.findByMember(member);
    }

    @Override
    public void save(Order order) {
        repository.save(order);
    }

    @Override
    public Order findById(Long id) throws EntityNotFoundException{
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Order> findAll() {
        return findAll();
    }
}


@Component
interface InnerJpaOrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByMember(Member member);
}