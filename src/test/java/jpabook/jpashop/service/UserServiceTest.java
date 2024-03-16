package jpabook.jpashop.service;

import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.domain.user.UsernamePasswordUser;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
        UsernamePasswordUser actual = (UsernamePasswordUser)userRepository.findByUid(savedUid)
                .orElseThrow(RuntimeException::new);

        
        assertThat(savedUid).isNotNull();
        assertThat(actual).extracting("name", "email", "addressInfo", "profileImageUrl", "username", "salt")
                .contains(givenName, givenEmail, new AddressInfo(address, detailedAddress), imageUrl, username, "salt");

        assertAll("verify password encode",
                ()->assertThat(actual.getPassword()).isNotNull(),
                ()->assertThat(actual.getPassword()).isNotEqualTo(password)
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
    @DisplayName("이미 가입되어 회원 DB에 저장된 이메일이라면 회원가입이 실패한다.")
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
    @DisplayName("KakaoUid, 기타 회원 정보를 입력해 회원가입을 수행해 회원 DB에 값을 저장할 수 있다.")
    public void testRegisterKakao() throws Exception{
        //given
        String givenName = "givenName";
        String givenEmail = "email@email.com";
        String address = "address";
        String detailedAddress = "detailedAddress";
        String imageUrl = "http://image.com/image.png";
        String kakaoUid = "2131241241";

        //when

        UserDto.KakaoUserRegisterInfo dto = UserDto.KakaoUserRegisterInfo.builder()
                .name(givenName)
                .email(givenEmail)
                .address(address)
                .detailedAddress(detailedAddress)
                .profileImageUrl(imageUrl)
                .kakaoUid(kakaoUid)
                .build();

        String savedUid = userService.register(dto);
        //then
        List<User> savedUsers = userRepository.findAll();


        assertThat(savedUsers).hasSize(1);
        assertThat(savedUsers).extracting("name", "uid", "email", "addressInfo", "profileImageUrl", "kakaoUid")
                .contains(tuple(givenName, savedUid, givenEmail, new AddressInfo(address, detailedAddress), imageUrl, kakaoUid));
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