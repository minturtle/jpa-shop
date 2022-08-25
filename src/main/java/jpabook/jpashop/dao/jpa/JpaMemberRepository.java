package jpabook.jpashop.dao.jpa;

import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaMemberRepository implements MemberRepository {

    private final InnerJpaRepository repository;

    @Override
    public void save(Member member) {
        repository.save(member);
    }

    @Override
    public Member findByName(String name) throws EntityNotFoundException {
        return repository.findByName(name).orElseThrow(EntityNotFoundException::new);

    }

    @Override
    public Member findByUserId(String userId) throws EntityNotFoundException {
        return repository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

    }

    @Override
    public Member findById(Long id) throws EntityNotFoundException {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Member> findAll() {
        return repository.findAll();
    }
}

@Component
interface InnerJpaRepository extends JpaRepository<Member , Long>{

    Optional<Member> findByUserId(String userId);
    Optional<Member> findByName(String name);
}