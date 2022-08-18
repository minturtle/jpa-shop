package jpabook.jpashop.service;

import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.dto.MemberDto;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;

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
    private MemberDto registerDto1;
    private MemberDto registerDto2;
    private MemberDto registerDto3;

    @BeforeEach
    void setup(){
        registerDto1 = new MemberDto("root11", "1122", "김민석", "구미시", "대학로 61", "금오공과대학교");
        registerDto2 = new MemberDto( "root12", "1122","김민석1", "경북 구미시", "대학로 61","금오공과 대학교");
        registerDto3 = new MemberDto("root13", "1122","김민석2" ,"대구광역시", "대학로 1","경북대학교");

        member1 = Member.createMember("김민석", "root11", "1122","경북 구미시", "대학로 61","금오공과 대학교");
        member2 = Member.createMember("김민석1","root12", "1122", "대구광역시", "대학로 1","경북대학교");
        member3 = Member.createMember("김민석2","root13", "1122", "서울특별시", "대학로 2", "서울대학교");
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
        given(memberRepository.findByUserId(registerDto1.getUserId())).willThrow(new EntityNotFoundException());
        //when
        Member member = memberService.signIn(registerDto1);
        //then
        assertThat(member.getName()).isEqualTo("김민석");
        assertThat(member.getAddress()).isEqualTo(new Address("구미시", "대학로 61", "금오공과대학교"));
    }

    @Test
    @DisplayName("회원가입, 이미 존재하는 userId인 경우")
    void t3() throws Exception {
        //given
        given(memberRepository.findByUserId(registerDto1.getUserId())).willReturn(member1);
        //when

        ThrowableAssert.ThrowingCallable throwableFunc = ()->memberService
                .signIn(registerDto1);
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 ID입니다.");
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


    @Test
    @DisplayName("로그인")
    void t5() throws Exception {
        //given
        given(memberRepository.findByUserId(registerDto1.getUserId())).willReturn(member1);
        //when

        //then
        assertThat(memberService.login(registerDto1)).isEqualTo(member1);
    }

    @Test
    @DisplayName("로그인 실패")
    void t6() throws Exception {
        //given
        given(memberRepository.findByUserId(registerDto1.getUserId()))
                .willThrow(new EntityNotFoundException()); //아이디 찾기 실패

        given(memberRepository.findByUserId(registerDto2.getUserId()))
                .willReturn(member2);
        registerDto2.setPassword("asasfa2q3213sad");
        //when
        //then
        assertThatThrownBy(()->memberService.login(registerDto1)).isInstanceOf(LoginFailed.class);
        assertThatThrownBy(()->memberService.login(registerDto2)).isInstanceOf(LoginFailed.class);
    }
}