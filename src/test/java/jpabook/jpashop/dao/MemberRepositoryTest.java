package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    void setUp(){
        member1 = new Member("김민석", "경북 구미시", "대학로 61","금오공과 대학교");
        member2 = new Member("김민석1", "대구광역시", "대학로 1","경북대학교");
        member3 = new Member("김민석2", "서울특별시", "대학로 2", "서울대학교");
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
        memberRepository.save(member1);
        //then
        Member findMember = memberRepository.findById(this.member1.getId()).orElseThrow(RuntimeException::new);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    @DisplayName("member객체 저장 후 이름으로 조회")
    void t3() throws Exception {
        //given
        //when
        memberRepository.save(member1);
        //then
        Member findMember = memberRepository.findByName("김민석").orElseThrow(RuntimeException::new);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    @DisplayName("회원 조회, 3개의 회원 저장 후 3개의 값이 모두 있는지 확인")
    void t4() throws Exception {
        //given
        saveAllMembers();
        //when
        final List<Member> members = memberRepository.findAll();
        //then
        assertThat(members).contains(member1, Index.atIndex(0));
        assertThat(members).contains(member2, Index.atIndex(1));
        assertThat(members).contains(member3, Index.atIndex(2));

    }

    private void saveAllMembers() {
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
    }

}