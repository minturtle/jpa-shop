package jpabook.jpashop.service.user;

import jpabook.jpashop.domain.user.*;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.exception.common.CannotFindEntityException;
import jpabook.jpashop.exception.user.*;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.service.UserService;
import jpabook.jpashop.util.NanoIdProvider;
import jpabook.jpashop.util.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static jpabook.jpashop.testUtils.TestDataUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Slf4j
@Sql(value = "classpath:init-user-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:clean-up.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @SpyBean
    private PasswordUtils passwordUtils;


    @SpyBean
    private NanoIdProvider nanoIdProvider;


    @Test
    @DisplayName(
            "Username과 Password, 기타 회원 정보를 입력해 회원가입을 수행해 회원 DB에 비밀번호가 암호화된 채로 값을 저장할 수 있다."
    )
    void given_NewUserInfo_when_RegisterUsernamePasswordAuthType_then_saveUserWithEncryptedPassword() throws Exception {
        // given
        String givenName = "givenName";
        String givenEmail = "email@email.com";
        String givenAddress = "givenAddress";
        String givenDetailedAddress = "givenDetailedAddress";
        String givenImageUrl = "http://image.com/image.png";
        String givenUsername = "givenUsername";
        String givenPassword = "abc1234!";
        byte[] givenSalt = Base64.getDecoder().decode("salt");


        when(passwordUtils.createSalt()).thenReturn(givenSalt);


        UserDto.UsernamePasswordUserRegisterInfo dto = createRegisterInfo(givenName, givenEmail, givenAddress, givenDetailedAddress, givenImageUrl, givenUsername, givenPassword);

        // when
        String savedUid = userService.register(dto);

        // then
        User actual = userRepository.findByUid(savedUid)
                .orElseThrow(RuntimeException::new);

        UsernamePasswordAuthInfo usernamePasswordAuthInfo = actual.getUsernamePasswordAuthInfo();

        assertThat(savedUid).isNotNull();
        assertThat(actual).extracting("name", "email", "addressInfo", "profileImageUrl")
                .contains(givenName, givenEmail, new AddressInfo(givenAddress, givenDetailedAddress), givenImageUrl);

        assertThat(usernamePasswordAuthInfo.getUsername()).isEqualTo(givenUsername);
        assertThat(usernamePasswordAuthInfo.getSalt()).isEqualTo("salt");
        assertAll("verify givenPassword encode",
                () -> assertThat(usernamePasswordAuthInfo.getPassword()).isNotNull(),
                () -> assertThat(usernamePasswordAuthInfo.getPassword()).isNotEqualTo(givenPassword)
        );


    }


    @ParameterizedTest
    @CsvSource({"abcdefgh", "12345678", "!@#$%^&*", "abcd1234", "abc123!"})
    @DisplayName("회원가입을 수행할 때 비밀번호는 영문, 숫자, 특수문자를 모두 포함하며, 8자 이상이 아니라면 회원가입에 실패한다.")
    public void given_NewUserInfoWithInvalidPassword_when_RegisterUsernamePasswordAuthType_then_Failed(String givenPassword) throws Exception {
        //given
        String givenName = "givenName";
        String givenEmail = "email@email.com";
        String givenAddress = "givenAddress";
        String givenDetailedAddress = "givenDetailedAddress";
        String givenImageUrl = "http://image.com/image.png";
        String givenUsername = "givenUsername";

        UserDto.UsernamePasswordUserRegisterInfo dto = createRegisterInfo(givenName, givenEmail, givenAddress, givenDetailedAddress, givenImageUrl, givenUsername, givenPassword);

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userService.register(dto);

        //then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage(UserExceptonMessages.INVALID_PASSWORD_EXPRESSION.getMessage());
    }

    @Test
    @DisplayName("이미 가입되어 회원 DB에 저장된 이메일의 유저가 Username으로 저장된 유저라면 회원가입이 실패한다.")
    public void given_NewUserInfoWithAlreadyExistsEmail_when_RegisterUsernamePasswordAuthType_then_Fail() throws Exception {
        //given
        User alreadyExistUser = user1;

        String givenName = "givenName";
        String givenEmail = alreadyExistUser.getEmail();
        String givenAddress = "givenAddress";
        String givenDetailedAddress = "givenDetailedAddress";
        String givenImageUrl = "http://image.com/image.png";
        String givenPassword = "abc1234!";
        String givenUsername = "givenUsername";

        UserDto.UsernamePasswordUserRegisterInfo dto = createRegisterInfo(givenName, givenEmail, givenAddress, givenDetailedAddress, givenImageUrl, givenUsername, givenPassword);

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userService.register(dto);

        //then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(AlreadyExistsUserException.class)
                .hasMessage(UserExceptonMessages.ALREADY_EXISTS_EMAIL.getMessage());

    }

    @Test
    @DisplayName("회원가입 시도시 이미 존재하는 username이라면 회원가입에 실패한다.")
    public void given_NewUserInfoWithAlreadyExistsUsername_when_RegisterUsernamePasswordAuthType_then_Fail() throws Exception {
        //given
        User alreadyExistsUser = user1;


        String givenName = "givenName";
        String givenEmail = "email@email.com";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String password = "abc1234!";
        String username = alreadyExistsUser.getUsernamePasswordAuthInfo().getUsername();

        UserDto.UsernamePasswordUserRegisterInfo dto = createRegisterInfo(givenName, givenEmail, address, detailedAddress, imageUrl, username, password);


        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userService.register(dto);

        //then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(AlreadyExistsUserException.class)
                .hasMessage(UserExceptonMessages.ALREADY_EXISTS_USERNAME.getMessage());
    }


    @Test
    @DisplayName("동시에 같은 Id, 또는 이메일로 회원가입을 실패한다면, 맨 처음 회원가입을 제외하곤 실패한다.")
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    public void given_NewUserInfo_when_registerConcurrently_then_SuccessFirstOnly() throws Exception {
        //given
        int threadSize = 10;
        CountDownLatch doneSignal = new CountDownLatch(threadSize);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        String givenName = "givenName";
        String givenEmail = "email3@email.com";
        String givenAddress = "givenAddress";
        String givenDetailedAddress = "givenDetailedAddress";
        String givenImageUrl = "http://image.com/image.png";
        String givenPassword = "abc1234!";
        String givenUsername = "givenUsername";

        UserDto.UsernamePasswordUserRegisterInfo dto = createRegisterInfo(givenName, givenEmail, givenAddress, givenDetailedAddress, givenImageUrl, givenUsername, givenPassword);

        //when
        for (int i = 0; i < threadSize; i++) {
            executorService.execute(() -> {
                try {
                    userService.register(dto);
                    successCount.getAndIncrement();
                } catch (AlreadyExistsUserException e) {
                    failCount.getAndIncrement();
                    log.info(e.getMessage());
                } catch (Exception e) {
                    fail();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        doneSignal.await();
        executorService.shutdown();
        //then
        Optional<User> actualUser = userRepository.findByEmail(givenEmail);

        assertThat(actualUser).isPresent();

        assertAll("처음 회원가입을 제외하곤 실패",
                () -> assertThat(successCount.get()).isEqualTo(1),
                () -> assertThat(failCount.get()).isEqualTo(threadSize - 1));

    }

    @Test
    @DisplayName("username과 password를 입력해서 로그인할 수 있다.")
    public void given_UserAndUsernameAuthInfo_when_LoginUsernamePassword_then_Success() throws Exception {
        //given
        User alreadyExistsUser = user1;

        String username = alreadyExistsUser.getUsernamePasswordAuthInfo().getUsername();
        String password = "abc1234!";



        //when
        String actualUid = userService.login(username, password);

        //then
        assertThat(alreadyExistsUser.getUid()).isEqualTo(actualUid);

    }

    @Test
    @DisplayName("저장되지 않은 username으로 로그인에 시도할 시 로그인에 실패한다.")
    public void given_UnsavedUsername_when_LoginUsernamePassword_then_fail() throws Exception {
        //given
        String incorrectUsername = "username11";
        String password = "abc1234!";



        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userService.login(incorrectUsername, password);

        //then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(AuthenticateFailedException.class)
                .hasMessage(UserExceptonMessages.LOGIN_FAILED.getMessage());

    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인에 시도할시 로그인에 실패한다.")
    public void given_IncorrectPassword_when_LoginUsernamePassword_then_fail() throws Exception {
        //given
        String givenUsername = user1.getUsernamePasswordAuthInfo().getUsername();
        String givenIncorrectPassword = "12314a3214!";


        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userService.login(givenUsername, givenIncorrectPassword);
        //then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(AuthenticateFailedException.class)
                .hasMessage(UserExceptonMessages.LOGIN_FAILED.getMessage());
    }


    @Test
    @DisplayName("카카오 인증 시도시, 이미 카카오 이메일과 kakaoUid가 회원 DB에 저장되어 있다면 해당 유저의 uid와 추가정보가 필요없다는 결과값을 리턴한다.")
    public void given_kakaoUser_when_LoginKakao_then_ReturnUidAndNoNeedInfo() throws Exception {
        //given
        User alreadyExistsKakaoUser = user2;


        //when
        UserDto.OAuthLoginResult result = userService.loginKakao(alreadyExistsKakaoUser.getUid(), alreadyExistsKakaoUser.getEmail());

        //then
        assertThat(result).extracting("uid", "isAdditionalInfoNeed")
                .contains(alreadyExistsKakaoUser.getUid(), false);
    }

    @Test
    @DisplayName("카카오 인증 시도시, 카카오 이메일을 사용하나 kakaoUid가 회원 DB에 저장되어 있지 않다면 해당 유저에 kakaoUid 정보를 저장하고 uid와 추가정보가 필요없다는 결과값을 리턴한다.")
    public void given_kakaoEmailUserNotKakaoAuthType_when_LoginKakao_thenSaveKakaoAuthInfoAndReturnUidAndNoNeedInfo() throws Exception {
        //given
        User alreadyExistsUser = user1;
        String email = alreadyExistsUser.getEmail();
        String kakaoUid = "21312412421";

        //when
        UserDto.OAuthLoginResult result = userService.loginKakao(kakaoUid, email);

        //then
        User actual = userRepository.findByUid(alreadyExistsUser.getUid()).orElseThrow(RuntimeException::new);

        assertThat(result).extracting("uid", "isAdditionalInfoNeed")
                .contains(alreadyExistsUser.getUid(), false);
        assertThat(actual.getKakaoOAuth2AuthInfo().getKakaoUid()).isEqualTo(kakaoUid);

    }

    @Test
    @DisplayName("카카오 인증 시도시, 카카오 이메일에 해당하는 유저가 회원 DB에 저장되어 있지 않다면 해당 유저를 추가해 DB에 저장하고 uid와 추가정보가 필요하다는 결과값을 리턴한다.")
    public void given_NoKakaoUser_when_LoginKakao_thenSaveAndReturnUidAndNeedAdditionalInfo() throws Exception{
        //given
        String kakaoUid = "123124141";
        String email = "kakao@kakao.com";
        String givenUid = "uid";

        when(nanoIdProvider.createNanoId()).thenReturn(givenUid);


        //when
        UserDto.OAuthLoginResult result = userService.loginKakao(kakaoUid, email);

        //then
        User actual = userRepository.findByUid(givenUid).orElseThrow(RuntimeException::new);
        assertThat(result).extracting("uid", "isAdditionalInfoNeed")
                .contains(givenUid, true);
        assertThat(actual.getEmail()).isEqualTo(email);
        assertThat(actual.getKakaoOAuth2AuthInfo().getKakaoUid()).isEqualTo(kakaoUid);

    }

    @Test
    @DisplayName("동시에 같은 카카오 정보로 로그인 및 DB 저장을 시도한다면, 첫번쨰 요청을 제외하곤 카카오 로그인에 실패한다.")
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    public void given_KakaoLoginInfo_when_KakaoLoginConcurrently_then_SuccessFirstOnly() throws Exception{
        //given
        String kakaoUid = "123124141";
        String email = "kakao@kakao.com";
        String givenUid = "uid";

        int threadSize = 2;
        CountDownLatch doneSignal = new CountDownLatch(threadSize);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();


        when(nanoIdProvider.createNanoId()).thenReturn(givenUid);
        //when
        for(int i = 0; i < threadSize; i++){
            executorService.execute(()->{
                try{
                    userService.loginKakao(kakaoUid, email);
                    successCount.getAndIncrement();
                }catch (AlreadyExistsUserException e){
                    failCount.getAndIncrement();
                }catch (Exception e){
                    e.printStackTrace();
                    fail();
                }finally {
                    doneSignal.countDown();
                }
            });
        }

        doneSignal.await();
        executorService.shutdown();

        //then
        User actual = userRepository.findByUid(givenUid)
                .orElseThrow(RuntimeException::new);

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(actual.getEmail()).isEqualTo(email);
        assertThat(actual.getKakaoOAuth2AuthInfo().getKakaoUid()).isEqualTo(kakaoUid);
    }

    @Test
    @DisplayName("Google 인증 시도시, 이미 구글 이메일과 googleUid가 회원 DB에 저장되어 있다면 해당 유저의 uid와 추가정보가 필요없다는 결과값을 리턴한다.")
    public void given_GoogleUser_when_LoginGoogle_then_ReturnUidAndNoNeedInfo() throws Exception{
        //given
        User alreadyExistUser = user3;

        //when
        UserDto.OAuthLoginResult result = userService.loginGoogle(alreadyExistUser.getUid(), alreadyExistUser.getEmail());

        //then
        assertThat(result).extracting("uid", "isAdditionalInfoNeed")
                .contains(alreadyExistUser.getUid(), false);

    }

    @Test
    @DisplayName("구글 인증 시도시, 구글 이메일을 사용하나 googleUid가 회원 DB에 저장되어 있지 않다면 해당 유저에 googleUid 정보를 저장하고 uid와 추가정보가 필요없다는 결과값을 리턴한다.")
    public void given_googleEmailUserNotGoogleAuthType_when_LoginGoogle_thenSaveGoogleAuthInfoAndReturnUidAndNoNeedInfo() throws Exception{
        //given
        User alreadyExistsUser = user1;
        String googleUid = "2221324124";


        //when
        UserDto.OAuthLoginResult result = userService.loginGoogle(googleUid, alreadyExistsUser.getEmail());

        //then
        User actual = userRepository.findByUid(alreadyExistsUser.getUid()).orElseThrow(RuntimeException::new);

        assertThat(result).extracting("uid", "isAdditionalInfoNeed")
                .contains(alreadyExistsUser.getUid(), false);
        assertThat(actual.getGoogleOAuth2AuthInfo().getGoogleUid()).isEqualTo(googleUid);

    }


    @Test
    @DisplayName("구글 인증 시도시, 구글 이메일에 해당하는 유저가 회원 DB에 저장되어 있지 않다면 해당 유저를 추가해 DB에 저장하고 uid와 추가정보가 필요하다는 결과값을 리턴한다.")
    public void given_NoGoogleUser_when_LoginGoogle_thenSaveAndReturnUidAndNeedAdditionalInfo() throws Exception{
        //given
        String googleUid = "123124141";
        String email = "google@google.com";
        String givenUid = "uid";

        when(nanoIdProvider.createNanoId()).thenReturn(givenUid);


        //when
        UserDto.OAuthLoginResult result = userService.loginGoogle(googleUid, email);

        //then
        User actual = userRepository.findByUid(givenUid).orElseThrow(RuntimeException::new);

        assertThat(result).extracting("uid", "isAdditionalInfoNeed")
                .contains(givenUid, true);
        assertThat(actual.getEmail()).isEqualTo(email);
        assertThat(actual.getGoogleOAuth2AuthInfo().getGoogleUid()).isEqualTo(googleUid);

    }



    @Test
    @DisplayName("사용자의 변경하고자 하는 이름, 주소, 프로필 이미지 정보를 회원 DB에 업데이트 할 수있다.")
    void given_UserAndUpdateInfo_when_updateUserInfo_thenUpdate() throws Exception {
        // given
        User givenUser = user1;

        String givenUpdatedName = "givenUpdatedName";
        String givenUpdatedAddress = "givenUpdatedAddress";
        String givenUpdatedDetailAddress = "givenUpdatedDetailAddress";
        String givenUpdatedProfileImage = "http://image.com/update.png";


        // when
        UserDto.UpdateDefaultUserInfo updateDto = UserDto.UpdateDefaultUserInfo.builder()
                .updatedName(Optional.of(givenUpdatedName))
                .updatedAddress(Optional.of(givenUpdatedAddress))
                .updatedDetailAddress(Optional.of(givenUpdatedDetailAddress))
                .updatedProfileImageUrl(Optional.of(givenUpdatedProfileImage))
                .build();


        userService.updateUserInfo(givenUser.getUid(), updateDto);

        // then

        User actual = userRepository.findByUid(givenUser.getUid()).orElseThrow(RuntimeException::new);
        assertThat(actual).extracting("uid", "email", "name", "profileImageUrl", "addressInfo")
                .contains(givenUser.getUid(), givenUser.getEmail(), givenUpdatedName, givenUpdatedProfileImage, new AddressInfo(givenUpdatedAddress, givenUpdatedDetailAddress));

    }

    @Test
    @DisplayName("동시에 유저의 정보를 수정 요청하면 첫번째 요청의 정보만 DB에 반영되고, 두번째 요청은 실패한다.")
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    void given_UserAndAdditionalUserInfo_when_updateUserInfoConcurrently_then_UpdateFirstOnly() throws Exception{
        // given
        User givenUser = user1;

        String givenUpdatedName = "givenUpdatedName";
        String givenUpdatedAddress = "givenUpdatedAddress";
        String givenUpdatedDetailAddress = "givenUpdatedDetailAddress";
        String givenUpdatedProfileImage = "http://image.com/update.png";

        UserDto.UpdateDefaultUserInfo updateDto = UserDto.UpdateDefaultUserInfo.builder()
                .updatedName(Optional.of(givenUpdatedName))
                .updatedAddress(Optional.of(givenUpdatedAddress))
                .updatedDetailAddress(Optional.of(givenUpdatedDetailAddress))
                .updatedProfileImageUrl(Optional.of(givenUpdatedProfileImage))
                .build();

        int threadSize = 2;
        CountDownLatch doneSignal = new CountDownLatch(threadSize);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();




        for(int i = 0 ; i <threadSize; i++){
            executorService.execute(()-> {
                try {
                    // when
                    userService.updateUserInfo(
                            givenUser.getUid(),
                            updateDto
                    );
                    successCount.getAndIncrement();
                } catch (OptimisticLockingFailureException e) {
                    failCount.getAndIncrement();
                } catch (Exception e){
                    e.printStackTrace();
                    fail();
                }finally {
                    doneSignal.countDown();
                }
            });
        }
        doneSignal.await();
        executorService.shutdown();

        // then
        assertAll("success and fail count assertion",
                ()->assertThat(successCount.get()).isEqualTo(1),
                ()->assertThat(failCount.get()).isEqualTo(1));
    }


    @Test
    @DisplayName("사용자의 이전 비밀번호와 새 비밀번호를 입력받아 새 비밀번호로 업데이트 할 수 있다.")
    void given_UserAndBeforeAfterPassword_when_UpdatePassword_then_Success() throws Exception{
        // given
        User givenUser = user1;
        String previousPassword = "abc1234!";
        String updatedPassword = "update123!";


        // when
        userService.updatePassword(givenUser.getUid(), new UserDto.UpdatePassword(previousPassword, updatedPassword));
        // then
        User actual = userRepository.findByUid(givenUser.getUid()).orElseThrow(RuntimeException::new);

        byte[] actualSalt = actual.getUsernamePasswordAuthInfo().getSaltBytes();
        String actualPassword = actual.getUsernamePasswordAuthInfo().getPassword();

        assertThat(passwordUtils.matches(updatedPassword, actualSalt, actualPassword)).isTrue();
    }


    @Test
    @DisplayName("사용자의 비밀번호를 업데이트 할때, 사용자의 정보가 조회되지 않는다면 오류를 출력한다.")
    void given_NoUser_when_UpdatePassword_then_Fail() throws Exception{
        // given
        String givenUid = "uid";
        String password = "asdsadsad2132134!";
        String updatedPassword = "update123!";

        // when
        ThrowableAssert.ThrowingCallable throwingCallable =
                ()->userService.updatePassword(givenUid, new UserDto.UpdatePassword(password, updatedPassword));
        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(CannotFindEntityException.class)
                .hasMessage(UserExceptonMessages.CANNOT_FIND_USER.getMessage());
    }

    @Test
    @DisplayName("사용자의 비밀번호를 업데이트 할때, ID/PW 인증 정보가 존재하지 않는다면 오류를 출력한다.")
    void given_NoUsernamePasswordAuthInfo_when_UpdatePassword_then_Fail() throws Exception{
        // given
        User givenUser = user2;

        String password = "asdsadsad2132134!";
        String updatedPassword = "update123!";


        // when
        ThrowableAssert.ThrowingCallable throwingCallable =
                ()->userService.updatePassword(givenUser.getUid(), new UserDto.UpdatePassword(password, updatedPassword));
        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(UserAuthTypeException.class)
                .hasMessage(UserExceptonMessages.NO_USERNAME_PASSWORD_AUTH_INFO.getMessage());
    }


    @Test
    @DisplayName("사용자의 비밀번호를 업데이트 할때, 회원 DB의 비밀번호와 입력한 이전 비밀번호가 일치하지 않는 경우, 오류를 throw한다.")
    void given_IncorrectPassword_when_UpdatePassword_then_Fail() throws Exception{
        // given
        User user = user1;
        String incorrectPassword = "sadsaf21321@";
        String updatedPassword = "update123!";

        // when
        ThrowableAssert.ThrowingCallable throwingCallable =
                ()->userService.updatePassword(user.getUid(), new UserDto.UpdatePassword(incorrectPassword, updatedPassword));

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(AuthenticateFailedException.class)
                .hasMessage(UserExceptonMessages.INVALID_PASSWORD.getMessage());
    }


    @Test
    @DisplayName("사용자의 비밀번호를 업데이트 할때, 비밀번호의 조건(8자 이상, 영어,숫자,특수문자) 조건을 만족하지 못한다면 오류를 throw한다.")
    void given_InvalidUpdatedPassword_when_UpdatePassword_then_Fail() throws Exception{
        // given
        User user = user1;
        String password = "abc1234!";
        String updatedPassword = "aa!";

        // when
        ThrowableAssert.ThrowingCallable throwingCallable =
                ()->userService.updatePassword(user.getUid(), new UserDto.UpdatePassword(password, updatedPassword));
        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage(UserExceptonMessages.INVALID_PASSWORD_EXPRESSION.getMessage());


    }

    @Test
    @DisplayName("사용자의 uid로 이에 해당하는 이름, 이메일, 주소, 프로필 이미지 정보를 조회할 수 있다.")
    void given_User_when_GetUserInfo_then_Return() throws Exception{
        // given
        User givenUser = user1;


        // when
        UserDto.Detail result = userService.getUserInfo(givenUser.getUid());

        // then
        assertThat(result).extracting("userUid", "name", "email", "address", "detailedAddress", "profileImage")
                .contains(givenUser.getUid(), givenUser.getName(), givenUser.getEmail(), givenUser.getAddressInfo().getAddress(), givenUser.getAddressInfo().getDetailedAddress(), givenUser.getProfileImageUrl());
    }

    @Test
    @DisplayName("사용자의 uid로 유저 정보를 조회할때 해당하는 uid가 없다면 오류를 throw한다.")
    void given_InvalidUid_when_GetUserInfo_then_Fail() throws Exception{
        // given
        String invalidUid = "uid";

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = ()->userService.getUserInfo(invalidUid);

        // then
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(CannotFindEntityException.class)
                .hasMessage(UserExceptonMessages.CANNOT_FIND_USER.getMessage());
    }


    private static UserDto.UsernamePasswordUserRegisterInfo createRegisterInfo(String givenName, String givenEmail, String address, String detailedAddress, String imageUrl, String username, String password) {
        return UserDto.UsernamePasswordUserRegisterInfo.builder()
                .name(givenName)
                .email(givenEmail)
                .address(address)
                .detailedAddress(detailedAddress)
                .profileImageUrl(imageUrl)
                .username(username)
                .password(password)
                .build();
    }
}