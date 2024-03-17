package jpabook.jpashop.service;

import jpabook.jpashop.domain.user.*;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.exception.user.AlreadyExistsUserException;
import jpabook.jpashop.exception.user.LoginFailedException;
import jpabook.jpashop.exception.user.PasswordValidationException;
import jpabook.jpashop.exception.user.UserExceptonMessages;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Slf4j
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @SpyBean
    private PasswordUtils passwordUtils;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName(
        "Username과 Password, 기타 회원 정보를 입력해 회원가입을 수행해 회원 DB에 비밀번호가 암호화된 채로 값을 저장할 수 있다."
    )
    void testUsernamePasswordRegister() throws Exception{
        // given
        String givenName = "givenName";
        String givenEmail = "email@email.com";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String username = "username";
        String password = "abc1234!";
        byte[] givenSalt = Base64.getDecoder().decode("salt");


        when(passwordUtils.createSalt()).thenReturn(givenSalt);


        UserDto.UsernamePasswordUserRegisterInfo dto = UserDto.UsernamePasswordUserRegisterInfo.builder()
                .name(givenName)
                .email(givenEmail)
                .address(address)
                .detailedAddress(detailedAddress)
                .profileImageUrl(imageUrl)
                .username(username)
                .password(password)
                .build();

        // when
        String savedUid = userService.register(dto);

        // then
        User actual = userRepository.findByUid(savedUid)
                .orElseThrow(RuntimeException::new);

        UsernamePasswordAuthInfo usernamePasswordAuthInfo = actual.getUsernamePasswordAuthInfo();

        assertThat(savedUid).isNotNull();
        assertThat(actual).extracting("name", "email", "addressInfo", "profileImageUrl")
                .contains(givenName, givenEmail, new AddressInfo(address, detailedAddress), imageUrl);

        assertThat(usernamePasswordAuthInfo.getUsername()).isEqualTo(username);
        assertThat(usernamePasswordAuthInfo.getSalt()).isEqualTo("salt");
        assertAll("verify password encode",
                ()->assertThat(usernamePasswordAuthInfo.getPassword()).isNotNull(),
                ()->assertThat(usernamePasswordAuthInfo.getPassword()).isNotEqualTo(password)
        );


    }


    @ParameterizedTest
    @CsvSource({"abcdefgh","12345678", "!@#$%^&*", "abcd1234", "abc123!"})
    @DisplayName("회원가입을 수행할 때 비밀번호는 영문, 숫자, 특수문자를 모두 포함하며, 8자 이상이 아니라면 회원가입에 실패한다.")
    public void testRegisterPasswordFailTest(String givenPassword) throws Exception{
        //given
        String givenName = "givenName";
        String givenEmail = "email@email.com";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String username = "username";

        UserDto.UsernamePasswordUserRegisterInfo dto = UserDto.UsernamePasswordUserRegisterInfo.builder()
                .name(givenName)
                .email(givenEmail)
                .address(address)
                .detailedAddress(detailedAddress)
                .profileImageUrl(imageUrl)
                .username(username)
                .password(givenPassword)
                .build();

        //when & then
        assertThatThrownBy(()->userService.register(dto))
                .isInstanceOf(PasswordValidationException.class)
                .hasMessage(UserExceptonMessages.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("이미 가입되어 회원 DB에 저장된 이메일의 유저가 Username으로 저장된 유저라면 회원가입이 실패한다.")
    public void testAlreadySavedEmail() throws Exception{
        //given
        String givenName = "givenName";
        String givenEmail = "email@email.com";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String password = "abc1234!";
        String username = "username";

        UserDto.UsernamePasswordUserRegisterInfo dto = UserDto.UsernamePasswordUserRegisterInfo.builder()
                .name(givenName)
                .email(givenEmail)
                .address(address)
                .detailedAddress(detailedAddress)
                .profileImageUrl(imageUrl)
                .username(username)
                .password(password)
                .build();

        saveUser("usernam1", "asdsad23123!@!@#", givenEmail);


        //when & then
        assertThatThrownBy(()->userService.register(dto))
                .isInstanceOf(AlreadyExistsUserException.class)
                .hasMessage(UserExceptonMessages.ALREADY_EXISTS_EMAIL.getMessage());

    }

    @Test
    @DisplayName("회원가입 시도시 이미 존재하는 username이라면 회원가입에 실패한다.")
    public void testAlreadyExistsUsernameRegister() throws Exception{
        //given
        String givenName = "givenName";
        String givenEmail = "email@email.com";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String password = "abc1234!";
        String username = "username";

        UserDto.UsernamePasswordUserRegisterInfo dto = UserDto.UsernamePasswordUserRegisterInfo.builder()
                .name(givenName)
                .email(givenEmail)
                .address(address)
                .detailedAddress(detailedAddress)
                .profileImageUrl(imageUrl)
                .username(username)
                .password(password)
                .build();


        saveUser(username, "asdsad23123!@!@#", "email2@email.com");

        //when & then
        assertThatThrownBy(()->userService.register(dto))
                .isInstanceOf(AlreadyExistsUserException.class)
                .hasMessage(UserExceptonMessages.ALREADY_EXISTS_USERNAME.getMessage());
    }


    @Test
    @DisplayName("동시에 같은 Id, 또는 이메일로 회원가입을 실패한다면, 맨 처음 회원가입을 제외하곤 실패한다.")
    public void registerConcurrencyTest() throws Exception{
        //given
        int threadSize = 10;
        CountDownLatch doneSignal = new CountDownLatch(threadSize);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        String givenName = "givenName";
        String givenEmail = "email3@email.com";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String password = "abc1234!";
        String username = "username";

        UserDto.UsernamePasswordUserRegisterInfo dto = UserDto.UsernamePasswordUserRegisterInfo.builder()
                .name(givenName)
                .email(givenEmail)
                .address(address)
                .detailedAddress(detailedAddress)
                .profileImageUrl(imageUrl)
                .username(username)
                .password(password)
                .build();

        //when
        for(int i = 0; i <threadSize; i++){
            executorService.execute(()->{
                try {
                    userService.register(dto);
                    successCount.getAndIncrement();
                }catch (AlreadyExistsUserException e){
                    failCount.getAndIncrement();
                    log.info(e.getMessage());
                }catch (Exception e){
                    fail();
                }finally {
                    doneSignal.countDown();
                }
            });
        }

        doneSignal.await();
        executorService.shutdown();
        //then
        List<User> savedUserList = userRepository.findAll();

        assertThat(savedUserList.size()).isEqualTo(1);

        assertAll("처음 회원가입을 제외하곤 실패",
                ()->assertThat(successCount.get()).isEqualTo(1),
                ()->assertThat(failCount.get()).isEqualTo(threadSize - 1));

    }

    @Test
    @DisplayName("username과 password를 입력해서 로그인할 수 있다.")
    public void testLogin() throws Exception{
        //given
        String password = "abc1234!";
        String username = "username";

        String savedUid = saveUser(username, password, "email@email.com");

        //when
        String actualUid = userService.login(username, password);

        //then
        assertThat(savedUid).isEqualTo(actualUid);

    }

    @Test
    @DisplayName("저장되지 않은 username으로 로그인에 시도할 시 로그인에 실패한다.")
    public void testIncorrectUsername() throws Exception{
        //given
        String password = "abc1234!";
        String username = "username";
        String incorrectUsername = "username11";


        saveUser(username, password, "email@email.com");
        //when & then
        assertThatThrownBy(()->userService.login(incorrectUsername, password))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage(UserExceptonMessages.LOGIN_FAILED.getMessage());

    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인에 시도할시 로그인에 실패한다.")
    public void testIncorrectPassword() throws Exception{
        //given
        String password = "abc1234!";
        String username = "username";
        String incorrectPassword = "abcd1234!";


        saveUser(username, password, "email@email.com");
        //when & then
        assertThatThrownBy(()->userService.login(username, incorrectPassword))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage(UserExceptonMessages.LOGIN_FAILED.getMessage());
    }

    @Test
    @DisplayName("카카오 인증 시도시, 이미 카카오 이메일과 kakaoUid가 회원 DB에 저장되어 있다면 해당 유저의 uid와 추가정보가 필요없다는 결과값을 리턴한다.")
    public void testUserAlreadyHasKakaoAuthInfo() throws Exception{
        //given
        String givenUid = "uid";
        String givenEmail = "email@kakao.com";
        String kakaoUid = "123141244124";

        saveKakaoUser(givenUid, givenEmail, kakaoUid);

        //when
        UserDto.OAuthLoginResult result = userService.loginKakao(kakaoUid, givenEmail);

        //then
        assertThat(result).extracting("uid", "isAdditionalInfoNeed")
                .contains(givenUid, false);
    }

    @Test
    @DisplayName("카카오 인증 시도시, 카카오 이메일을 사용하나 kakaoUid가 회원 DB에 저장되어 있지 않다면 해당 유저에 kakaoUid 정보를 저장하고 uid와 추가정보가 필요없다는 결과값을 리턴한다.")
    public void testUserAlreadyHasKakaoEmail() throws Exception{
        //given

        String username = "username";
        String password = "abc1234!";
        String email = "abcd@kakao.com";
        String kakaoUid = "21312412421";
        String savedUid = saveUser(username, password, email);

        //when
        UserDto.OAuthLoginResult result = userService.loginKakao(kakaoUid, email);

        //then
        User actual = userRepository.findByUid(savedUid).orElseThrow(RuntimeException::new);

        assertThat(result).extracting("uid", "isAdditionalInfoNeed")
                .contains(savedUid, false);
        assertThat(actual.getKakaoOAuth2AuthInfo().getKakaoUid()).isEqualTo(kakaoUid);

    }




    private void saveKakaoUser(String uid, String email, String kakaoUid){
        String givenName = "givenName";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";

        User newUser = new User(
                uid,
                email,
                givenName,
                imageUrl,
                address,
                detailedAddress
        );
        newUser.setKakaoOAuth2AuthInfo(kakaoUid);
        userRepository.save(newUser);
    }

    private String saveUser(String username, String password, String email) throws PasswordValidationException, AlreadyExistsUserException {
        String givenName = "givenName";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";

        UserDto.UsernamePasswordUserRegisterInfo dto = UserDto.UsernamePasswordUserRegisterInfo.builder()
                .name(givenName)
                .email(email)
                .address(address)
                .detailedAddress(detailedAddress)
                .profileImageUrl(imageUrl)
                .username(username)
                .password(password)
                .build();

        return userService.register(dto);
    }


}