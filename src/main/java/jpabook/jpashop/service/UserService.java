package jpabook.jpashop.service;

import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.domain.user.UsernamePasswordAuthInfo;
import jpabook.jpashop.exception.user.*;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.util.NanoIdProvider;
import jpabook.jpashop.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @description 회원가입(Username, Password) 메서드
     * @author minseok kim
     * @param registerInfo 회원가입 정보
     * @return 저장된 사용자의 uid
     * @throws
    */
    @Transactional(rollbackFor = {PasswordValidationException.class, AlreadyExistsUserException.class, RuntimeException.class})
    public String register(UserDto.UsernamePasswordUserRegisterInfo registerInfo) throws PasswordValidationException, AlreadyExistsUserException {

        validDuplicationEmail(registerInfo.getEmail());
        validDuplicationUsername(registerInfo.getUsername());
        validPassword(registerInfo.getPassword());

        byte[] salt = passwordUtils.createSalt();
        String encodedPassword = passwordUtils
                .encodePassword(registerInfo.getPassword(), salt);

        String uid = nanoIdProvider.createNanoId();


        User newUser = new User(
                uid,
                registerInfo.getEmail(),
                registerInfo.getName(),
                registerInfo.getProfileImageUrl(),
                registerInfo.getAddress(),
                registerInfo.getDetailedAddress()
        );

        newUser.setUsernamePasswordAuthInfo(registerInfo.getUsername(), encodedPassword, salt);


        saveProcess(newUser);

        return uid;
    }

    /**
     * @description 회원가입(Kakao) 메서드
     * @author minseok kim
     * @param registerInfo 회원가입 정보
     * @return 저장된 사용자의 uid
     * @throws
     */
    @Transactional(rollbackFor = {PasswordValidationException.class, AlreadyExistsUserException.class, RuntimeException.class})
    public String register(UserDto.KakaoUserRegisterInfo registerInfo) throws AlreadyExistsUserException {
        validDuplicationEmail(registerInfo.getEmail());

        String uid = nanoIdProvider.createNanoId();

        User newUser = new User(
                uid,
                registerInfo.getEmail(),
                registerInfo.getName(),
                registerInfo.getProfileImageUrl(),
                registerInfo.getAddress(),
                registerInfo.getDetailedAddress()
        );

        newUser.setKakaoOAuth2AuthInfo(registerInfo.getKakaoUid());

        saveProcess(newUser);

        return uid;
    }

    /**
     * @description 회원가입(google) 메서드
     * @author minseok kim
     * @param registerInfo 회원가입 정보
     * @return 저장된 사용자의 uid
     * @throws
     */
    @Transactional(rollbackFor = {PasswordValidationException.class, AlreadyExistsUserException.class, RuntimeException.class})
    public String register(UserDto.GoogleUserRegisterInfo registerInfo) throws AlreadyExistsUserException {
        validDuplicationEmail(registerInfo.getEmail());

        String uid = nanoIdProvider.createNanoId();

        User newUser = new User(
                uid,
                registerInfo.getEmail(),
                registerInfo.getName(),
                registerInfo.getProfileImageUrl(),
                registerInfo.getAddress(),
                registerInfo.getDetailedAddress()
        );

        newUser.setGoogleOAuth2AuthInfo(registerInfo.getGoogleUid());

        saveProcess(newUser);

        return uid;

    }



    @Transactional(readOnly = true)
    public String login(String username, String password) throws LoginFailedException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new LoginFailedException(UserExceptonMessages.LOGIN_FAILED.getMessage()));

        UsernamePasswordAuthInfo usernamePasswordAuthInfo = user.getUsernamePasswordAuthInfo();

        if(!passwordUtils.matches(password, usernamePasswordAuthInfo.getSaltBytes(), usernamePasswordAuthInfo.getPassword())){
            throw new LoginFailedException(UserExceptonMessages.LOGIN_FAILED.getMessage());
        }

        return user.getUid();
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
    * 사용자 정보 변경
    *
    * @param userUid : MemberEntity의 uid값
    *
    * */
    public void update(String userUid, UserDto.UpdateDefaultUserInfo dto) throws CannotFindUserException {
        User findUser = userRepository.findByUid(userUid)
                .orElseThrow(() -> new CannotFindUserException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()
                ));


        findUser.setName(dto.getUpdatedName());
        findUser.setAddressInfo(dto.getUpdatedAddress(), dto.getUpdatedDetailAddress());
        findUser.setProfileImageUrl(dto.getUpdatedProfileImageUrl());
    }



    private void validDuplicationEmail(String email) throws AlreadyExistsUserException {
        if(userRepository.findByEmail(email).isPresent()){
            throw new AlreadyExistsUserException(UserExceptonMessages.ALREADY_EXISTS_EMAIL.getMessage());
        }
    }

    private void validDuplicationUsername(String username) throws AlreadyExistsUserException {
        if(userRepository.findByUsername(username).isPresent()){
            throw new AlreadyExistsUserException(UserExceptonMessages.ALREADY_EXISTS_USERNAME.getMessage());
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


    private void saveProcess(User newUser) throws AlreadyExistsUserException {
        try{
            userRepository.save(newUser);
        }catch (DataIntegrityViolationException e){
            throw new AlreadyExistsUserException();
        }
    }


}

