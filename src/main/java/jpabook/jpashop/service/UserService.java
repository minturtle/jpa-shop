package jpabook.jpashop.service;

import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.domain.user.UsernamePasswordUser;
import jpabook.jpashop.exception.user.AlreadyExistsUserException;
import jpabook.jpashop.exception.user.LoginFailedException;
import jpabook.jpashop.exception.user.PasswordValidationException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.util.NanoIdProvider;
import jpabook.jpashop.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;
    private final NanoIdProvider nanoIdProvider;

    /**
     * @description 회원가입 API
     * @author minseok kim
     * @param registerInfo 회원가입 정보
     * @return 저장된 사용자의 uid
     * @throws
    */
    @Transactional(rollbackFor = {PasswordValidationException.class, AlreadyExistsUserException.class, RuntimeException.class})
    public String register(UserDto.UsernamePasswordUserRegisterInfo registerInfo) throws PasswordValidationException, AlreadyExistsUserException {
        validRegisterForm(registerInfo);


        byte[] salt = passwordUtils.createSalt();
        String encodedPassword = passwordUtils
                .encodePassword(registerInfo.getPassword(), salt);

        String uid = nanoIdProvider.createNanoId();


        UsernamePasswordUser newUser = new UsernamePasswordUser(
                uid,
                registerInfo.getEmail(),
                registerInfo.getName(),
                registerInfo.getProfileImageUrl(),
                registerInfo.getAddress(),
                registerInfo.getDetailedAddress(),
                registerInfo.getUsername(),
                encodedPassword,
                new String(Base64.getEncoder().encode(salt))
        );


        userRepository.save(newUser);

        return newUser.getUid();
    }



    public String login(String username, String password) throws LoginFailedException {
        return null;
    }

    /*
    * 사용자 마이페이지에 필요한 정보들을 제공
    *
    * @param userUid : User Entity의 uid값
    * @return : 유저의 이름, 주소 정보가 담긴 Dto
    * */
    @Transactional(readOnly = true)
    public UserDto.Detail getUserInfo(String userUid){
        return null;
    }

    /*
    * 사용자 이름 변경
    *
    * @param userUid : MemberEntity의 ID값
    * @param String modifiedName : 바꿀 이름
    * */
    public void update(String userUid){

    }


    /**
     * @author minseok kim
     * @description 회원가입 정보가 유효한지 확인하는 메서드
     * @param registerInfo 검증하고자 하는 회원가입 정보
     * @exception PasswordValidationException 비밀번호 검증 실패시
    */
    private void validRegisterForm(
            UserDto.UsernamePasswordUserRegisterInfo registerInfo
    ) throws PasswordValidationException, AlreadyExistsUserException {
        validDuplicationUser(registerInfo.getUsername(), registerInfo.getEmail());
        validPassword(registerInfo.getPassword());
    }


    private void validDuplicationUser(String username, String email) throws AlreadyExistsUserException {
        if(userRepository.findByEmail(email).isPresent()){
            throw new AlreadyExistsUserException(UserExceptonMessages.ALREADY_EXISTS_EMAIL.getMessage());
        }
    }


    /**
     * @author minseok kim
     * @description 비밀번호가 유효한지 확인하는 메서드
     * @param password 검증하고자 하는 비밀번호
     * @exception PasswordValidationException 비밀번호 검증 실패시
    */
    private void validPassword(String password) throws PasswordValidationException {
        if(password.length() < 8){
            throw new PasswordValidationException(UserExceptonMessages.INVALID_PASSWORD.getMessage());
        }
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);

        if(!matcher.matches()){
            throw new PasswordValidationException(UserExceptonMessages.INVALID_PASSWORD.getMessage());
        }
    }


}

