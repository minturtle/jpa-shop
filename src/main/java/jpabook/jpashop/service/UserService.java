package jpabook.jpashop.service;


import jpabook.jpashop.domain.user.GoogleOAuth2AuthInfo;
import jpabook.jpashop.domain.user.KakaoOAuth2AuthInfo;
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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
     * @author minseok kim
     * @description username/password 인증 시도
     * @param
     * @return 인증된 사용자의 uid
     * @exception
    */
    public String login(String username, String password) throws LoginFailedException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new LoginFailedException(UserExceptonMessages.LOGIN_FAILED.getMessage()));

        UsernamePasswordAuthInfo usernamePasswordAuthInfo = user.getUsernamePasswordAuthInfo();

        if(!passwordUtils.matches(password, usernamePasswordAuthInfo.getSaltBytes(), usernamePasswordAuthInfo.getPassword())){
            throw new LoginFailedException(UserExceptonMessages.LOGIN_FAILED.getMessage());
        }

        return user.getUid();
    }


    /**
     * @author minseok kim
     * @description 카카오 인증, 존재하지 않는 유저라면 DB에 사용자의 정보를 저장한 후 uid를 리턴한다.
     * @param kakaoUid 카카오에서 전달한 유저의 uid
     * @param email 사용자의 카카오 이메일
     * @return
     * @exception AlreadyExistsUserException 해당 메서드 실행 도중 해당 email로 회원가입이 완료된 경우
    */
    @Transactional(rollbackFor = {AlreadyExistsUserException.class, RuntimeException.class})
    public UserDto.OAuthLoginResult loginKakao(String kakaoUid, String email) throws AlreadyExistsUserException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        boolean isUserPresent = userOptional.isPresent();

        // 사용자의 카카오인증 정보와 요청받은 인증 정보가 일치하는 경우
        if(isUserPresent && doKakaoAuthenticate(kakaoUid, userOptional.get().getKakaoOAuth2AuthInfo())){
            return new UserDto.OAuthLoginResult(userOptional.get().getUid(), false);
        }
        // 카카오 이메일을 사용하는 사용자는 존재하나, 카카오 인증정보가 존재하지 않는 경우
        if(isUserPresent && !(new KakaoOAuth2AuthInfo(kakaoUid).equals(userOptional.get().getKakaoOAuth2AuthInfo()))){
            userOptional.get().setKakaoOAuth2AuthInfo(kakaoUid);

            return new UserDto.OAuthLoginResult(userOptional.get().getUid(), false);
        }
        // 카카오 이메일을 사용하는 유저가 존재하지 않는 경우

        String uid = nanoIdProvider.createNanoId();

        User newUser = new User(uid, email, "손님", null, null, null);

        newUser.setKakaoOAuth2AuthInfo(kakaoUid);


        saveProcess(newUser);

        return new UserDto.OAuthLoginResult(uid, true);
    }




    /**
     * @author minseok kim
     * @description 구글 인증, 존재하지 않는 유저라면 DB에 사용자의 정보를 저장한 후 uid를 리턴한다.
     * @param googleUid 카카오에서 전달한 유저의 uid
     * @param email 사용자의 카카오 이메일
     * @return
     * @exception AlreadyExistsUserException 해당 메서드 실행 도중 해당 email로 회원가입이 완료된 경우
     */
    @Transactional(rollbackFor = {AlreadyExistsUserException.class, RuntimeException.class})
    public UserDto.OAuthLoginResult loginGoogle(String googleUid,  String email) throws AlreadyExistsUserException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        boolean isUserPresent = userOptional.isPresent();

        // 사용자의 카카오인증 정보와 요청받은 인증 정보가 일치하는 경우
        if(isUserPresent && doGoogleAuthenticate(googleUid, userOptional.get().getGoogleOAuth2AuthInfo())){
            return new UserDto.OAuthLoginResult(userOptional.get().getUid(), false);
        }
        // 카카오 이메일을 사용하는 사용자는 존재하나, 카카오 인증정보가 존재하지 않는 경우
        if(isUserPresent && !(new GoogleOAuth2AuthInfo(googleUid).equals(userOptional.get().getGoogleOAuth2AuthInfo()))){
            userOptional.get().setGoogleOAuth2AuthInfo(googleUid);

            return new UserDto.OAuthLoginResult(userOptional.get().getUid(), false);
        }
        String uid = nanoIdProvider.createNanoId();

        User newUser = new User(uid, email, "손님", null, null, null);

        newUser.setGoogleOAuth2AuthInfo(googleUid);

        saveProcess(newUser);
        return new UserDto.OAuthLoginResult(uid, true);
    }

    /**
     * @description 사용자 정보 조회 API
     * @author minseok kim
     * @param userUid 사용자의 고유 식별자
     * @throws
    */
    @Transactional(readOnly = true)
    public UserDto.Detail getUserInfo(String userUid){
        return null;
    }


    /**
     * @description 사용자의 이름, 주소, 프로필 이미지를 변경하는 메서드
     * @author minseok kim
     * @param userUid 사용자의 고유 식별자
     * @param dto : 업데이트된 유저 정보
     * @throws CannotFindUserException 사용자의 고유식별자로 사용자를 조회할 수 없을 때
     * @throws OptimisticLockingFailureException 동시에 두 업데이트 요청이 들어와 업데이트에 실패한 경우
    */
    public void updateUserInfo(String userUid, UserDto.UpdateDefaultUserInfo dto) throws CannotFindUserException, OptimisticLockingFailureException {
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


    private void updateProcess(User updatedUser) throws UserUpdateFailureException {
        try{
            userRepository.save(updatedUser);
        }catch (OptimisticLockingFailureException e){
            throw new UserUpdateFailureException(UserExceptonMessages.UPDATE_FAILED.getMessage());
        }
    }


    private boolean doKakaoAuthenticate(String kakaoUid, KakaoOAuth2AuthInfo kakaoAuthInfo) {
        return  kakaoAuthInfo != null && kakaoAuthInfo.getKakaoUid().equals(kakaoUid);
    }

    private boolean doGoogleAuthenticate(String googleUid, GoogleOAuth2AuthInfo googleAuthInfo) {
        return googleAuthInfo != null && googleAuthInfo.getGoogleUid().equals(googleUid);
    }

}

