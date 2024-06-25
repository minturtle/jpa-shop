package jpabook.jpashop.service;


import jpabook.jpashop.aop.annotations.Loggable;
import jpabook.jpashop.domain.user.*;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.user.*;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.util.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@Loggable
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NanoIdProvider nanoIdProvider;

    /**
     * Username, Password 기반의 회원가입 메서드
     * @author minseok kim
     * @param registerInfo 회원가입 정보
     * @return 저장된 사용자의 uid
     * @throws PasswordValidationException 비밀번호가 8자리 이상, 영문, 숫자, 특수문자 한글자 이상 조건을 만족하지 못한 경우
     * @throws AlreadyExistsUserException username 또는 email이 이미 존재하는 경우
    */
    @Transactional(rollbackFor = {PasswordValidationException.class, AlreadyExistsUserException.class, RuntimeException.class})
    public String register(UserDto.UsernamePasswordUserRegisterInfo registerInfo) throws PasswordValidationException, AlreadyExistsUserException {
        log.info("register user : email - {}", registerInfo.getEmail());


        validDuplicationEmail(registerInfo.getEmail());
        validDuplicationUsername(registerInfo.getUsername());
        validPasswordExpression(registerInfo.getPassword());

        String encodedPassword = passwordEncoder
                .encode(registerInfo.getPassword());

        String uid = nanoIdProvider.createNanoId();


        User newUser =  User.of(
                uid,
                registerInfo.getEmail(),
                registerInfo.getName(),
                registerInfo.getProfileImageUrl(),
                registerInfo.getAddress(),
                registerInfo.getDetailedAddress()
        );

        newUser.setUsernamePasswordAuthInfo(registerInfo.getUsername(), encodedPassword);


        saveProcess(newUser);


        log.info("register user success : userUid - {}, email - {}", uid, registerInfo.getEmail());
        return uid;
    }

    /**
     * username/password 로그인 메서드
     * @author minseok kim
     * @param username 사용자가 입력한 로그인 ID
     * @param password 사용자가 입력한 비밀번호
     * @return 인증된 사용자의 uid
     * @exception AuthenticateFailedException 로그인 실패시
     * @deprecated 이 메서드는 Spring Security의 도입으로 CustomUsernamePasswordAuthenticateFilter에 대체되었습니다.
    */
    @Deprecated
    public String login(String username, String password) throws AuthenticateFailedException {
        log.info("login user : username - {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticateFailedException(UserExceptonMessages.LOGIN_FAILED.getMessage()));

        UsernamePasswordAuthInfo usernamePasswordAuthInfo = user.getUsernamePasswordAuthInfo();

        if(!passwordEncoder.matches(password, usernamePasswordAuthInfo.getPassword())){
            throw new AuthenticateFailedException(UserExceptonMessages.LOGIN_FAILED.getMessage());
        }

        log.info("login user success : username - {}, userUid - {}", username, user.getUid());
        return user.getUid();
    }


    /**
     * 카카오 인증, 존재하지 않는 유저라면 DB에 사용자의 정보를 저장한 후 uid를 리턴한다.
     * @author minseok kim
     * @param kakaoUid 카카오에서 전달한 유저의 uid
     * @param email 사용자의 카카오 이메일
     * @return
     * @exception AlreadyExistsUserException 해당 메서드 실행 도중 해당 email로 회원가입이 완료된 경우
    */
    @Transactional(rollbackFor = {AlreadyExistsUserException.class, RuntimeException.class})
    public UserDto.OAuthLoginResult loginKakao(String kakaoUid, String email) throws AlreadyExistsUserException {
        log.info("login kakao user : email - {}", email);

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

        User newUser =  User.of(uid, email, "손님", null, null, null);

        newUser.setKakaoOAuth2AuthInfo(kakaoUid);


        saveProcess(newUser);

        log.info("login kakao user success : email - {}", email);
        return new UserDto.OAuthLoginResult(uid, true);
    }




    /**
     * 구글 인증, 존재하지 않는 유저라면 DB에 사용자의 정보를 저장한 후 uid를 리턴한다.
     * @author minseok kim
     * @param googleUid 카카오에서 전달한 유저의 uid
     * @param email 사용자의 카카오 이메일
     * @return
     * @exception AlreadyExistsUserException 해당 메서드 실행 도중 해당 email로 회원가입이 완료된 경우
     */
    @Transactional(rollbackFor = {AlreadyExistsUserException.class, RuntimeException.class})
    public UserDto.OAuthLoginResult loginGoogle(String googleUid,  String email) throws AlreadyExistsUserException {
        log.info("login google user : email - {}", email);
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

        User newUser = User.of(uid, email, "손님", null, null, null);

        newUser.setGoogleOAuth2AuthInfo(googleUid);

        saveProcess(newUser);

        log.info("login google user success : email - {}", email);
        return new UserDto.OAuthLoginResult(uid, true);
    }

    /**
     * 사용자 정보 조회 API
     * @author minseok kim
     * @param userUid 사용자의 고유 식별자
     * @throws CannotFindEntityException 고유 식별자로 유저 정보 조회에 실패했을 시
    */
    @Transactional(readOnly = true)
    public UserDto.Detail getUserInfo(String userUid) throws CannotFindEntityException {
        log.info("get user info : userUid - {}", userUid);
        User user = findUserByUidOrThrow(userUid);


        log.info("get user info success : userUid - {}", userUid);
        return UserDto.Detail.builder()
                .userUid(user.getUid())
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddressInfo().getAddress())
                .detailedAddress(user.getAddressInfo().getDetailedAddress())
                .profileImage(user.getProfileImageUrl())
                .build();
    }


    /**
     * 사용자의 이름, 주소, 프로필 이미지를 변경하는 메서드
     * @author minseok kim
     * @param userUid 사용자의 고유 식별자
     * @param dto : 업데이트된 유저 정보
     * @throws CannotFindEntityException 사용자의 고유식별자로 사용자를 조회할 수 없을 때
     * @throws OptimisticLockingFailureException 동시에 두 업데이트 요청이 들어와 업데이트에 실패한 경우
    */
    @Transactional(rollbackFor = {CannotFindEntityException.class, RuntimeException.class})
    public void updateUserInfo(String userUid, UserDto.UpdateDefaultUserInfo dto) throws CannotFindEntityException, OptimisticLockingFailureException {
        log.info("update user info : userUid - {}", userUid);
        User findUser = findUserByUidOrThrow(userUid);
        AddressInfo savedAddressInfo = findUser.getAddressInfo();


        findUser.setName(dto.getUpdatedName().orElse(findUser.getName()));

        findUser.setAddressInfo(
                dto.getUpdatedAddress().orElse(savedAddressInfo.getAddress()),
                dto.getUpdatedDetailAddress().orElse(savedAddressInfo.getDetailedAddress())
        );

        findUser.setProfileImageUrl(dto.getUpdatedProfileImageUrl().orElse(findUser.getProfileImageUrl()));
        log.info("update user info success : userUid - {}", userUid);
    }



    /**
     * 사용자의 비밀번호를 업데이트 하는 메서드
     * @author minseok kim
     * @param userUid 사용자의 고유 식별자
     * @param dto 비밀번호 업데이트에 필요한 정보
     * @throws CannotFindEntityException 사용자의 고유식별자로 사용자를 조회할 수 없는 경우
     * @throws OptimisticLockingFailureException 동시에 두 업데이트 요청이 들어와 업데이트에 실패한 경우
     * @throws UserAuthTypeException id/pw 정보가 존재하지 않는 유저인 경우
     * @throws AuthenticateFailedException 이전 비밀번호가 일치하지 않는 경우
     * @throws PasswordValidationException 새 비밀번호의 expression이 요구사항의 조건을 만족하지 못하는 경우
    */
    public void updatePassword(String userUid, UserDto.UpdatePassword dto)
            throws CannotFindEntityException, UserAuthTypeException, OptimisticLockingFailureException, AuthenticateFailedException, PasswordValidationException {
        log.info("update user password : userUid - {}", userUid);

        User findUser = findUserByUidOrThrow(userUid);
        UsernamePasswordAuthInfo authInfo = findUser.getUsernamePasswordAuthInfo();

        if(authInfo == null){
            throw new UserAuthTypeException(UserExceptonMessages.NO_USERNAME_PASSWORD_AUTH_INFO.getMessage());
        }

        if(!passwordEncoder.matches(dto.getBeforePassword(), authInfo.getPassword())){
            throw new AuthenticateFailedException(UserExceptonMessages.INVALID_PASSWORD.getMessage());
        }

        validPasswordExpression(dto.getAfterPassword());



        findUser.setUsernamePasswordAuthInfo(
                authInfo.getUsername(),
                passwordEncoder.encode(dto.getAfterPassword())
        );

        log.info("update user password success : userUid - {}", userUid);
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
    private void validPasswordExpression(String password) throws PasswordValidationException {
        if(password.length() < 8){
            throw new PasswordValidationException(UserExceptonMessages.INVALID_PASSWORD_EXPRESSION.getMessage());
        }
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);

        if(!matcher.matches()){
            throw new PasswordValidationException(UserExceptonMessages.INVALID_PASSWORD_EXPRESSION.getMessage());
        }
    }


    private void saveProcess(User newUser) throws AlreadyExistsUserException {
        try{
            userRepository.save(newUser);
        }catch (DataIntegrityViolationException e){
            throw new AlreadyExistsUserException();
        }
    }


    private boolean doKakaoAuthenticate(String kakaoUid, KakaoOAuth2AuthInfo kakaoAuthInfo) {
        return  kakaoAuthInfo != null && kakaoAuthInfo.getKakaoUid().equals(kakaoUid);
    }

    private boolean doGoogleAuthenticate(String googleUid, GoogleOAuth2AuthInfo googleAuthInfo) {
        return googleAuthInfo != null && googleAuthInfo.getGoogleUid().equals(googleUid);
    }

    private User findUserByUidOrThrow(String userUid) throws CannotFindEntityException {
        return userRepository.findByUid(userUid)
                .orElseThrow(() -> new CannotFindEntityException(UserExceptonMessages.CANNOT_FIND_USER.getMessage()
                ));
    }

}

