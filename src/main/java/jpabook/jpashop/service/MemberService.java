package jpabook.jpashop.service;

import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

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

            User user = User.createMember(registerDto.getUsername(), registerDto.getUserId()
                    , registerDto.getPassword(), registerDto.getAddressInfo().getCity()
                    ,registerDto.getAddressInfo().getStreet(),registerDto.getAddressInfo().getZipcode());
            user.encryptPassword();

            memberRepository.save(user);
        }catch (IllegalStateException e){
            throw new RegisterFailed(e.getMessage() , e);
        }
    }


    public Long login(MemberDto loginDto) throws LoginFailed{
        try {

            User findUser = memberRepository.findByUserId(loginDto.getUserId()); //throwable EntityNotFound
            checkIsPasswordCorrect(findUser, loginDto.getPassword()); //throwable LoginFailed

            return findUser.getId();

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
            User findUser = memberRepository.findById(id);

            return new MemberDto.MemberDtoBuilder()
                    .username(findUser.getName())
                    .address(findUser.getAddressInfo())
                    .build();
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("유저 정보를 찾을 수 없습니다.");
        }catch(RuntimeException e){
            throw new EntityNotFoundException("입력된 아이디 값이 없습니다.");
        }
    }

    /*
    * 사용자 이름 변경
    *
    * @param id : MemberEntity의 ID값
    * @param String modifiedName : 바꿀 이름
    * */
    public void updateName(Long id, String modifiedName) throws EntityNotFoundException{
        User findUser = memberRepository.findById(id);
        findUser.setName(modifiedName);
    }

    /*
    * 사용자 주소변경
    *
    * @param id : User Entity의 Id값
    * @param address : 바뀐 주소 정보가 담겨있음.
    * */
    public void updateAddress(Long id, AddressInfo modifiedAddressInfo) throws EntityNotFoundException{
         User findUser = memberRepository.findById(id);
            findUser.setAddressInfo(modifiedAddressInfo);
    }


    /*
    * 사용자 비밀번호 변경, 비밀번호가 바꿀 수 있는지 검증 한 후 암호화 한 후 바뀜.
    *
    * @param id : User Entity의 id값
    * @param modifiedPassword : 바꾸려는 비밀번호 값
    * */
    public void updatePassword(Long id, String modifiedPassword) throws IllegalStateException, EntityNotFoundException{
        checkIsPasswordUsable(modifiedPassword); //throwable IllegalStateException

        User findUser = memberRepository.findById(id);
        findUser.setPassword(modifiedPassword);
        findUser.encryptPassword();
    }

    protected void checkIsDuplicatedUserId(String id) throws IllegalStateException{
        try {
            User findUser = memberRepository.findByUserId(id); //findMember 값이 있으면 안됨. 없으면 예외발생해서 정상 리턴
            checkIsFindMemberNull(findUser);
        }catch (EntityNotFoundException e){ return; }

        throw new IllegalStateException("이미 존재하는 ID입니다."); //findMember 값이 있는 경우
    }

    private void checkIsFindMemberNull(User findUser) {
        if(findUser == null) throw new EntityNotFoundException();
    }

    private void checkIsPasswordCorrect(User findUser, String finePassword) throws LoginFailed{
        if(!findUser.comparePassword(finePassword)) throw new LoginFailed("잘못된 비밀번호 입니다.");
    }

    private void validRegisterForm(MemberDto dto) throws IllegalStateException{

        checkIsEmptyFieldExist(dto); //throwable IllegalStateException
        checkIsDuplicatedUserId(dto.getUserId());  //throwable IllegalStateException
        checkIsPasswordUsable(dto.getPassword()); //throwable IllegalStateException

    }

    private void checkIsEmptyFieldExist(MemberDto dto) throws IllegalStateException{
        if (isEmpty(dto.getUserId()) || isEmpty(dto.getPassword()) || isEmpty(dto.getUsername()) || isEmpty(dto.getAddressInfo())) {
            throw new IllegalStateException("필드를 모두 채워야 합니다.");
        }
    }

    private void checkIsPasswordUsable(String password)throws IllegalStateException{
        if(password.length() < 4) throw new IllegalStateException("비밀번호는 4글자 이상이여야 합니다.");
    }

    private boolean isEmpty(String st){ return (st == null || st.isEmpty());}
    private boolean isEmpty(AddressInfo addressInfo){
        return (addressInfo == null || isEmpty(addressInfo.getCity()) || isEmpty(addressInfo.getStreet()) || isEmpty(addressInfo.getZipcode()));
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

