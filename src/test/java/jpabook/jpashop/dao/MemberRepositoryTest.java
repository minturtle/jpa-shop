package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    void setUp(){
        member1 = Member.createMember("김민석", "root11", "1122", "경북 구미시", "대학로 61","금오공과 대학교");
        member2 = Member.createMember("김민석1", "root12", "1122","대구광역시", "대학로 1","경북대학교");
        member3 = Member.createMember("김민석2","root13", "1122" ,"서울특별시", "대학로 2", "서울대학교");
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
        Member findMember = memberRepository.findById(member1.getId());
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    @DisplayName("member id로 조회 찾을 수 없는 경우")
    void t5() throws Exception {
        //given
        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->memberRepository.findById(523L);
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("member객체 저장 후 이름으로 조회")
    void t3() throws Exception {
        //given
        //when
        memberRepository.save(member1);
        //then
        Member findMember = memberRepository.findByName("김민석");

        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    @DisplayName("member 이름으로 조회 찾을수 없는 경우")
    void t4() throws Exception {
        //given

        ThrowableAssert.ThrowingCallable throwableFunc = ()->memberRepository.findByName("아무개");
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(EntityNotFoundException.class);
    }


    @Test
    @DisplayName("회원 조회, 3개의 회원 저장 후 3개의 값이 모두 있는지 확인")
    void t6() throws Exception {
        //given
        saveAllMembers();
        //when
        final List<Member> members = memberRepository.findAll();
        //then
        assertThat(members).contains(member1, Index.atIndex(0));
        assertThat(members).contains(member2, Index.atIndex(1));
        assertThat(members).contains(member3, Index.atIndex(2));

    }

    @Test
    @DisplayName("userId로 회원 조회하기")
    void t7() throws Exception {
        //given
        saveAllMembers();
        //when
        Member findMember = memberRepository.findByUserId(member1.getUserId());
        //then
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    @DisplayName("없는 userId 조회하기")
    void t8() throws Exception {
        //given
        //when
        ThrowableAssert.ThrowingCallable func = ()->memberRepository.findByUserId("aaafsaf223234");
        //then
        assertThatThrownBy(func).isInstanceOf(EntityNotFoundException.class);
    }
    private void saveAllMembers() {
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
    }

}