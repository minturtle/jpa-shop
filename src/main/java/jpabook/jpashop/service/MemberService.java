package jpabook.jpashop.service;

import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Member signIn(String name, String city, String street, String zipcode) throws IllegalStateException{
        checkIsDuplicatedUsername(name); //유저의 이름이 중복되는지 확인한다.
        Member member = new Member(name, city, street, zipcode);
        memberRepository.save(member);

        return member;
    }

    @Transactional(readOnly = true)
    public List<Member> getMemberList(){
        return memberRepository.findAll();
    }

    private void checkIsDuplicatedUsername(String name) throws IllegalStateException{
        try {
            memberRepository.findByName(name); //findMember 값이 있으면 안됨. 없으면 예외발생해서 정상 리턴
        }catch (EntityNotFoundException e){ return;}

        throw new IllegalArgumentException("이미 존재하는 이름입니다."); //findMember 값이 있는 경우

    }
}
