package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>{
}
