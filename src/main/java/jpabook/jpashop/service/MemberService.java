package jpabook.jpashop.service;

import jpabook.jpashop.dao.MemberRepository;
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


    /*
    * 사용자의 회원가입 폼을 입력받아, 검증 후 회원가입을 진행
    *
    * @param registerDto : 회원가입 폼의 입력 값들(빈 값이 있어선 안됨.)
    * @return member entity의 ID값
    * */
    public void register(MemberDto registerDto) throws RegisterFailed{
        try {
            validRegisterForm(registerDto); //회원가입이 가능한 입력인지 검증

            Member member = Member.createMember(registerDto.getUsername(), registerDto.getUserId()
                    , registerDto.getPassword(), registerDto.getAddress().getCity()
                    ,registerDto.getAddress().getStreet(),registerDto.getAddress().getZipcode());
            member.encryptPassword();

            memberRepository.save(member);
        }catch (IllegalStateException e){
            throw new RegisterFailed(e.getMessage() , e);
        }
    }


    public Long login(MemberDto loginDto) throws LoginFailed{
        try {

            Member findMember = memberRepository.findByUserId(loginDto.getUserId()); //throwable EntityNotFound
            checkIsPasswordCorrect(findMember, loginDto.getPassword()); //throwable LoginFailed

            return findMember.getId();

        }catch (EntityNotFoundException e){
            throw new LoginFailed("유저 정보를 찾을 수 없습니다.", e);}
    }

    /*
    * 사용자 마이페이지에 필요한 정보들을 제공
    *
    * @param id : User Entity의 Id값
    * @return : 유저의 이름, 주소 정보가 담긴 Dto
    * */
    @Transactional(readOnly = true)
    public MemberDto getMemberDetail(Long id) throws EntityNotFoundException {
        try {
            Member findMember = memberRepository.findById(id);

            return new MemberDto.MemberDtoBuilder()
                    .username(findMember.getName())
                    .address(findMember.getAddress())
                    .build();
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("유저 정보를 찾을 수 없습니다.");
        }
    }

    /*
    * 사용자 이름 변경
    *
    * @param id : MemberEntity의 ID값
    * @param String modifiedName : 바꿀 이름
    * */
    public void updateName(Long id, String modifiedName) throws EntityNotFoundException{
        Member findMember = memberRepository.findById(id);
        findMember.setName(modifiedName);
    }

    /*
    * 사용자 주소변경
    *
    * @param id : User Entity의 Id값
    * @param address : 바뀐 주소 정보가 담겨있음.
    * */
    public void updateAddress(Long id, Address modifiedAddress) throws EntityNotFoundException{
         Member findMember = memberRepository.findById(id);
            findMember.setAddress(modifiedAddress);
    }


    /*
    * 사용자 비밀번호 변경, 비밀번호가 바꿀 수 있는지 검증 한 후 암호화 한 후 바뀜.
    *
    * @param id : User Entity의 id값
    * @param modifiedPassword : 바꾸려는 비밀번호 값
    * */
    public void updatePassword(Long id, String modifiedPassword) throws IllegalStateException, EntityNotFoundException{
        checkIsPasswordUsable(modifiedPassword); //throwable IllegalStateException

        Member findMember = memberRepository.findById(id);
        findMember.setPassword(modifiedPassword);
        findMember.encryptPassword();
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

    public static class LoginFailed extends RuntimeException{
        public LoginFailed(String message) {
            super(message);
        }

        public LoginFailed(String message, Throwable cause) {
            super(message, cause);
        }

        public LoginFailed() {
        }
    }

    public static class RegisterFailed extends RuntimeException{
        public RegisterFailed() {
        }
        public RegisterFailed(String message, Throwable cause) {
            super(message, cause);
        }

        public RegisterFailed(String message) {
            super(message);
        }
    }
}

