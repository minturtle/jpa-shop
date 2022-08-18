package jpabook.jpashop.service;

import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public Member signIn(MemberDto registerDto) throws IllegalStateException{
        checkIsDuplicatedUserId(registerDto.getUserId()); //유저의 Id가 중복되는지 확인한다.
        Member member = registerDto.toMember();
        memberRepository.save(member);

        return member;
    }

    public Member login(MemberDto loginDto) throws LoginFailed{
        try {

            Member findMember = memberRepository.findByUserId(loginDto.getUserId());
            checkIsPasswordCorrect(findMember, loginDto.getPassword()); //throwable LoginFailed
            return findMember;

        }catch (EntityNotFoundException e){ throw new LoginFailed();}
    }

    private void checkIsPasswordCorrect(Member findMember, String finePassword) throws LoginFailed{
        if(!findMember.comparePassword(finePassword)) throw new LoginFailed();
    }

    @Transactional(readOnly = true)
    public List<Member> getMemberList(){
        return memberRepository.findAll();
    }

    public void checkIsDuplicatedUserId(String id) throws IllegalStateException{
        try {
            Member findMember = memberRepository.findByUserId(id); //findMember 값이 있으면 안됨. 없으면 예외발생해서 정상 리턴
            checkIsFindMemberNull(findMember);
        }catch (EntityNotFoundException e){ return; }

        throw new IllegalStateException("이미 존재하는 ID입니다."); //findMember 값이 있는 경우
    }

    private void checkIsFindMemberNull(Member findMember) {
        if(findMember == null) throw new EntityNotFoundException();
    }

}

class LoginFailed extends RuntimeException{}
