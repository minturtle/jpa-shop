package jpabook.jpashop.service;

import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    void setup(){
        member1 = new Member("김민석", "경북 구미시", "대학로 61","금오공과 대학교");
        member2 = new Member("김민석1", "대구광역시", "대학로 1","경북대학교");
        member3 = new Member("김민석2", "서울특별시", "대학로 2", "서울대학교");
    }

    @Test
    @DisplayName("memberService 동작 확인")
    void t1() throws Exception {
        //given
        //when
        //then
        assertThat(memberService).isNotNull();
    }

    @Test
    @DisplayName("회원 가입")
    void t2() throws Exception {
        //given
        //when
        Member member = memberService.signIn("김민석", "구미시", "대학로 61", "금오공과대학교");
        //then
        assertThat(member.getName()).isEqualTo("김민석");
        assertThat(member.getAddress()).isEqualTo(new Address("구미시", "대학로 61", "금오공과대학교"));
    }

    @Test
    @DisplayName("회원가입, 이미 존재하는 name인 경우")
    void t3() throws Exception {
        //given
        Optional<Member> member = Optional.of(member1);
        given(memberRepository.findByName("김민석")).willReturn(member);
        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->{memberService
                .signIn("김민석", "구미시", "대학로 61", "금오공과대학교");};
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 이름입니다.");
    }

    @Test
    @DisplayName("회원 조회,이미 저장된 3개의 멤버 조회 후 반환")
    void t4() throws Exception {
        //given
        List<Member> members = List.of(member1, member2, member3);
        given(memberRepository.findAll()).willReturn(members);
        //when
        final List<Member> findMemberList = memberService.getMemberList();
        //then
        assertThat(findMemberList).contains(member1, Index.atIndex(0));
        assertThat(findMemberList).contains(member2, Index.atIndex(1));
        assertThat(findMemberList).contains(member3, Index.atIndex(2));
    }
}