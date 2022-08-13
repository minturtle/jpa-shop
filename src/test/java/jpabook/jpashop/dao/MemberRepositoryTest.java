package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    private Member member;

    @BeforeEach
    void setUp(){
        member = new Member("김민석", "경북 구미시", "대학로 61","금오공과 대학교");
    }

    @Test
    @DisplayName("memberRepository 객체 생성")
    void t1() throws Exception {
        //given
        //when
        //then
        assertThat(memberRepository).isNotNull();
    }

    @Test
    @DisplayName("member 객체 저장 후 id로 조회")
    void t2() throws Exception {
        //given
        //when
        memberRepository.save(member);
        //then
        Member findMember = memberRepository.findById(this.member.getId()).orElseThrow(RuntimeException::new);
        assertThat(findMember).isEqualTo(member);
    }



}