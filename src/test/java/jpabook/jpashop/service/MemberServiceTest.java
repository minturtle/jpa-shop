package jpabook.jpashop.service;

import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.dto.MemberDto;

import jpabook.jpashop.util.Encryptor;
import org.assertj.core.api.ThrowableAssert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;

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
        registerDto1 = new MemberDto(1L,"root11", "1122", "김민석", "구미시", "대학로 61", "금오공과대학교");
        registerDto2 = new MemberDto(2L,"root12", "1122","김민석1", "경북 구미시", "대학로 61","금오공과 대학교");
        registerDto3 = new MemberDto(3L, "root13", "1122","김민석2" ,"대구광역시", "대학로 1","경북대학교");

        member1 = Member.createMember("김민석", "root11", Encryptor.encrypt("1122"),"경북 구미시", "대학로 61","금오공과 대학교");
        member2 = Member.createMember("김민석1","root12", Encryptor.encrypt("1122"), "대구광역시", "대학로 1","경북대학교");
        member3 = Member.createMember("김민석2","root13", Encryptor.encrypt("1122"), "서울특별시", "대학로 2", "서울대학교");
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
        memberService.register(registerDto1);
        //then

    }

    @Test
    @DisplayName("회원가입, 이미 존재하는 userId인 경우")
    void t3() throws Exception {
        //given
        given(memberRepository.findByUserId(registerDto1.getUserId())).willReturn(member1);

        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->memberService
                .register(registerDto1);
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(RegisterFailed.class)
                .hasMessage("이미 존재하는 ID입니다.");
    }

    @Test
    @DisplayName("회원가입, 비밀번호가 4글자 미만인 경우")
    void t6() throws Exception {
        //given
        registerDto1.setPassword("222");
        //when
        ThrowableAssert.ThrowingCallable throwableFunc = ()->memberService.register(registerDto1);
        //then
        assertThatThrownBy(throwableFunc).isInstanceOf(RegisterFailed.class)
                .hasMessage("비밀번호는 4글자 이상이여야 합니다.");
    }

    @Test
    @Disabled
    @DisplayName("회원가입, DB에 저장된 객체의 비밀번호가 암호화됐는지 확인")
    void t7() throws Exception {
        //given
        //when
        memberService.register(registerDto1);
        //then
        //assertThat(registeredMember.getPassword()).isEqualTo(Encryptor.encrypt("1122"));
    }

    @Test
    @DisplayName("로그인")
    void t5() throws Exception {
        //given
        given(memberRepository.findByUserId(registerDto1.getUserId())).willReturn(member1);
        //when
        final Long memberId = memberService.login(registerDto1);
        //then
        assertThat(memberId).isEqualTo(member1.getId());
    }

    @Test
    @DisplayName("로그인 실패")
    void t8() throws Exception {
        //given
        given(memberRepository.findByUserId(registerDto1.getUserId()))
                .willThrow(new EntityNotFoundException()); //아이디 찾기 실패

        given(memberRepository.findByUserId(registerDto2.getUserId()))
                .willReturn(member2);
        registerDto2.setPassword("asasfa2q3213sad");
        //when
        //then
        assertThatThrownBy(()->memberService.login(registerDto1)).isInstanceOf(LoginFailed.class)
                .hasMessage("유저 정보를 찾을 수 없습니다.");
        assertThatThrownBy(()->memberService.login(registerDto2)).isInstanceOf(LoginFailed.class)
                .hasMessage("잘못된 비밀번호 입니다.");
    }

    @Test
    @DisplayName("유저의 상세정보 조회")
    void t9() throws Exception {
        //given
        given(memberRepository.findById(member1.getId())).willReturn(member1);
        //when
        MemberDto memberDetail = memberService.getMemberDetail(member1.getId());
        //then
        assertThat(memberDetail.getUsername()).isEqualTo(member1.getName());
        assertThat(memberDetail.getAddress()).isEqualTo(member1.getAddress());
    }

    @Test
    @DisplayName("유저의 주소 변경")
    void t10() throws Exception {
        //given
        given(memberRepository.findById(member1.getId())).willReturn(member1);

        Address address = new Address("부산 광역시", "부산대학교", "기숙사 205호");
        //when
        memberService.updateAddress(member1.getId(), address);
        //then
        assertThat(member1.getAddress()).isEqualTo(address);
    }

    @Test
    @DisplayName("유저의 비밀번호 변경")
    void t11() throws Exception {
        //given
        given(memberRepository.findById(member1.getId())).willReturn(member1);
        //when
        memberService.updatePassword(member1.getId(), "12345");
        //then
        assertThat(member1.getPassword()).isEqualTo(Encryptor.encrypt("12345"));
    }

    @Test
    @DisplayName("유저의 비밀번호 변경, 조건에 맞지않음")
    void t12() throws Exception {
        //given
        //when
        //then

        assertThatThrownBy(()->memberService.updatePassword(member1.getId(), "12"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("비밀번호는 4글자 이상이여야 합니다.");
    }
}