package jpabook.jpashop.service;

import jpabook.jpashop.dao.MemberRepository;
import jpabook.jpashop.dao.em.EntityManagerMemberRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public void register(MemberDto registerDto) throws RegisterFailed{
        try {
            validRegisterForm(registerDto); //회원가입이 가능한 입력인지 검증

            //Member 객체가 만들어 질때 비밀번호는 암호화된다.
            Member member = Member.createMember(registerDto.getUsername(), registerDto.getUserId()
                    , registerDto.getPassword(), registerDto.getAddress().getCity()
                    ,registerDto.getAddress().getStreet(),registerDto.getAddress().getZipcode(), false);

            memberRepository.save(member);

        }catch (IllegalStateException e){
            throw new RegisterFailed(e.getMessage() , e);
        }
    }

    public MemberDto login(MemberDto loginDto) throws LoginFailed{
        try {

            Member findMember = memberRepository.findByUserId(loginDto.getUserId()); //throwable EntityNotFound
            checkIsPasswordCorrect(findMember, loginDto.getPassword()); //throwable LoginFailed

            return new MemberDto.MemberDtoBuilder()
                    .userIdAndPassword(findMember.getUserId(), findMember.getPassword())
                    .build();

        }catch (EntityNotFoundException e){
            throw new LoginFailed("유저 정보를 찾을 수 없습니다.", e);}
    }

    @Transactional(readOnly = true)
    public MemberDto getMemberDetail(Long id) throws EntityNotFoundException{
        Member findMember = memberRepository.findById(id);

        return new MemberDto.MemberDtoBuilder()
                .username(findMember.getName())
                .address(findMember.getAddress())
                .build();
    }

    public void updateAddress(MemberDto memberDto) throws LoginFailed{
         Member findMember = memberRepository.findByUserId(memberDto.getUserId());
        findMember.setAddress(memberDto.getAddress());
    }

    public void updatePassword(MemberDto memberDto, String modifiedPassword) throws IllegalStateException{
        checkIsPasswordUsable(modifiedPassword); //throwable IllegalStateException

        Member findMember = memberRepository.findByUserId(memberDto.getUserId());
        findMember.setPassword(modifiedPassword, false);
    }

    @Transactional(readOnly = true)
    protected void checkIsDuplicatedUserId(String id) throws IllegalStateException{
        try {
            Member findMember = memberRepository.findByUserId(id); //findMember 값이 있으면 안됨. 없으면 예외발생해서 정상 리턴
            checkIsFindMemberNull(findMember);
        }catch (EntityNotFoundException e){ return; }

        throw new IllegalStateException("이미 존재하는 ID입니다."); //findMember 값이 있는 경우
    }

    private void checkIsFindMemberNull(Member findMember) {
        if(findMember == null) throw new EntityNotFoundException();
    }

    private void checkIsPasswordCorrect(Member findMember, String finePassword) throws LoginFailed{
        if(!findMember.comparePassword(finePassword)) throw new LoginFailed("잘못된 비밀번호 입니다.");
    }

    private void validRegisterForm(MemberDto dto) throws IllegalStateException{

        checkIsEmptyFieldExist(dto); //throwable IllegalStateException
        checkIsDuplicatedUserId(dto.getUserId());  //throwable IllegalStateException
        checkIsPasswordUsable(dto.getPassword()); //throwable IllegalStateException

    }

    private void checkIsEmptyFieldExist(MemberDto dto) throws IllegalStateException{
        if (isEmpty(dto.getUserId()) || isEmpty(dto.getPassword()) || isEmpty(dto.getUsername()) || isEmpty(dto.getAddress())) {
            throw new IllegalStateException("필드를 모두 채워야 합니다.");
        }
    }

    private void checkIsPasswordUsable(String password)throws IllegalStateException{
        if(password.length() < 4) throw new IllegalStateException("비밀번호는 4글자 이상이여야 합니다.");
    }

    private boolean isEmpty(String st){ return (st == null || st.isEmpty());}
    private boolean isEmpty(Address address){
        return (address == null || isEmpty(address.getCity()) || isEmpty(address.getStreet()) || isEmpty(address.getZipcode()));
    }
}

class LoginFailed extends RuntimeException{
    public LoginFailed(String message) {
        super(message);
    }

    public LoginFailed(String message, Throwable cause) {
        super(message, cause);
    }
}

class RegisterFailed extends RuntimeException{
    public RegisterFailed() {
    }
    public RegisterFailed(String message, Throwable cause) {
        super(message, cause);
    }
}