package jpabook.jpashop.service;

import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.domain.user.UsernamePasswordUser;
import jpabook.jpashop.dto.UserDto;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.PasswordUtils;
import jpabook.jpashop.util.NanoIdProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtils passwordUtils;

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
        User user = new UsernamePasswordUser(
                savedUid,
                givenEmail,
                givenName,
                imageUrl,
                address,
                detailedAddress,
                username,
                passwordUtils.encodePassword(password, givenSalt),
                new String(Base64.getEncoder().encode(givenSalt))
                );

        verify(userRepository, times(1))
                .save(user);
        assertThat(savedUid).isNotNull();
    }




    @TestConfiguration
    public static class TestConfig{

        @MockBean
        private UserRepository userRepository;

        @SpyBean
        private PasswordUtils passwordUtils;

        @Bean
        public UserService userService(NanoIdProvider nanoIdProvider){
            return new UserService(userRepository, passwordUtils, nanoIdProvider);
        }

        @Bean
        public NanoIdProvider nanoIdProvider(){
            return new NanoIdProvider();
        }

    }
}